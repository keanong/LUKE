package com.xdel.luke.ExceptionHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

/**
 * R7 A7: durable store for crash reports, delivery receipts, the crash sequence counter
 * and the undelivered summary.
 * Plain Java with no Android types, so it runs in JVM unit tests. The caller supplies the
 * directory; in the app it is storeDir(context.getNoBackupFilesDir()): private, excluded
 * from Auto Backup and outside every FileProvider path.
 * Every file is written atomically: a sibling .tmp file is written and synced, moved over
 * the target with ATOMIC_MOVE, then the directory is synced where the platform allows it.
 * Every file carries a format version and a CRC32; a file that fails validation is reported
 * as corrupt and never trusted.
 * Shared files (counter, last_sent, summary) change only under the two-level store lock:
 * a static ReentrantLock for the threads of this process, then an exclusive FileChannel
 * lock on .store.lock for any other process.
 * Nothing here takes JobsHandler.mutex, uses SharedPreferences or touches the network.
 */
public final class CrashReportStore {

    public static final String DIR_NAME = "r7_crash";
    public static final String PENDING = "pending_";
    public static final String HELD = "held_";
    public static final String RETRY = "retry_";
    public static final String CORRUPT = "corrupt_";
    public static final String SENT = "sent_";
    public static final String REC_EXT = ".rec";
    public static final String SENT_EXT = ".ok";
    public static final String TMP_EXT = ".tmp";
    public static final String COUNTER_FILE = "seq";
    public static final String COUNTER_CORRUPT_FILE = "seq.corrupt";
    public static final String LAST_SENT_FILE = "last_sent";
    public static final String SUMMARY_FILE = "undelivered_summary.txt";
    public static final String SUMMARY_CORRUPT_FILE = "undelivered_summary.corrupt";
    public static final String STORE_LOCK_FILE = ".store.lock";
    public static final String UNKNOWN = "unknown";
    /** Seven days from cycleStart; no attempt may start at or after expiresAt. */
    public static final long WINDOW_MS = TimeUnit.DAYS.toMillis(7);
    public static final int EXC_MAX_CHARS = 1024;
    public static final int SUMMARY_EXC_MAX_CHARS = 200;
    public static final int SUMMARY_MAX_ENTRIES = 200;
    public static final long HINT_MAX_ABOVE_TRUSTED = 1000000L;
    static final int FORMAT_VERSION = 1;
    static final long MAX_FILE_BYTES = 256 * 1024;
    static final long TMP_MIN_AGE_MS = TimeUnit.MINUTES.toMillis(10);
    private static final long LOCK_POLL_MS = 50;
    private static final Pattern ID = Pattern.compile("[0-9A-Za-z-]{1,64}");
    private static final Pattern KEY = Pattern.compile("[A-Za-z][A-Za-z0-9_]{0,31}");
    private static final Pattern SEQ_HINT =
            Pattern.compile("(?m)^seq=(\\d{1,18})\\r?$");
    private static final Pattern SUMMARY_HINT =
            Pattern.compile("(?m)^e\\d{1,4}=(\\d{1,18})\\|");
    private static final Pattern OMITTED_HINT =
            Pattern.compile("(?m)^omitted=\\d{1,18}\\|\\d{1,18}\\|(\\d{1,18})\\|");

    private static final ReentrantLock STORE_LOCK = new ReentrantLock();
    //both are set only while the current thread holds STORE_LOCK
    private static RandomAccessFile lockFile;
    private static FileLock fileLock;

    private final File dir;

    public CrashReportStore(File dir) {
        if (dir == null) {
            throw new IllegalArgumentException("dir is required");
        }
        this.dir = dir;
    }

    /** The store directory inside the given base directory, normally getNoBackupFilesDir(). */
    public static File storeDir(File noBackupFilesDir) {
        return new File(noBackupFilesDir, DIR_NAME);
    }

    public File dir() {
        return dir;
    }

    void ensureDir() throws IOException {
        if (!dir.isDirectory() && !dir.mkdirs() && !dir.isDirectory()) {
            throw new IOException("cannot create " + dir);
        }
    }

    // ---------------- two-level store lock ----------------

    /**
     * Acquires the store lock, waiting at most timeoutMs in total for both levels;
     * reentrant for the holding thread. Returns false on timeout, interruption or any
     * I/O problem, and then holds nothing.
     */
    public boolean lock(long timeoutMs) {
        long deadline = System.nanoTime()
                + TimeUnit.MILLISECONDS.toNanos(Math.max(0, timeoutMs));
        try {
            long waitNs = Math.max(0, deadline - System.nanoTime());
            if (!STORE_LOCK.tryLock(waitNs, TimeUnit.NANOSECONDS)) {
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        if (STORE_LOCK.getHoldCount() > 1) {
            return true;
        }
        boolean ok = false;
        try {
            ok = acquireFileLock(deadline);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Throwable t) {
            ok = false;
        } finally {
            if (!ok) {
                releaseFileLock();
                STORE_LOCK.unlock();
            }
        }
        return ok;
    }

    /** Releases one hold of the store lock; the file lock is released with the last hold. */
    public void unlock() {
        if (!STORE_LOCK.isHeldByCurrentThread()) {
            return;
        }
        if (STORE_LOCK.getHoldCount() == 1) {
            try {
                releaseFileLock();
            } finally {
                STORE_LOCK.unlock();
            }
        } else {
            STORE_LOCK.unlock();
        }
    }

    /**
     * Second level of the store lock: polls a non-blocking FileChannel lock on .store.lock
     * until the deadline set by lock(long), so both levels share one time limit. A holder in
     * this JVM (OverlappingFileLockException) counts as busy. On success lockFile and
     * fileLock are set; on any failure everything opened here is closed before returning
     * or throwing. The caller must hold STORE_LOCK.
     */
    private boolean acquireFileLock(long deadline) throws IOException, InterruptedException {
        releaseFileLock();
        ensureDir();
        RandomAccessFile raf = new RandomAccessFile(new File(dir, STORE_LOCK_FILE), "rw");
        boolean acquired = false;
        try {
            while (true) {
                FileLock l;
                try {
                    l = raf.getChannel().tryLock();
                } catch (OverlappingFileLockException e) {
                    l = null;
                }
                if (l != null) {
                    lockFile = raf;
                    fileLock = l;
                    acquired = true;
                    return true;
                }
                long leftNs = deadline - System.nanoTime();
                if (leftNs <= 0) {
                    return false;
                }
                long sleepNs = Math.min(
                        TimeUnit.MILLISECONDS.toNanos(LOCK_POLL_MS), leftNs);
                TimeUnit.NANOSECONDS.sleep(sleepNs);
            }
        } finally {
            if (!acquired) {
                closeQuietly(raf);
            }
        }
    }

    /**
     * Releases and closes whatever the file level holds. Safe when nothing or only part was
     * acquired and when called twice; the fields are cleared first. Never throws, so the
     * caller's STORE_LOCK.unlock() always runs.
     */
    private static void releaseFileLock() {
        FileLock l = fileLock;
        RandomAccessFile raf = lockFile;
        fileLock = null;
        lockFile = null;
        if (l != null) {
            try {
                l.release();
            } catch (Throwable ignored) {
                //closing the file below also releases the lock
            }
        }
        closeQuietly(raf);
    }

    private static void closeQuietly(RandomAccessFile raf) {
        if (raf != null) {
            try {
                raf.close();
            } catch (Throwable ignored) {
                //nothing else to release
            }
        }
    }

    // ---------------- atomic file I/O ----------------

    /**
     * Writes content to target atomically, replacing an existing file. A failed write leaves
     * the previous target untouched and removes its temporary file.
     */
    static void writeAtomic(File target, byte[] content) throws IOException {
        File parent = target.getParentFile();
        if (parent == null
                || (!parent.isDirectory() && !parent.mkdirs() && !parent.isDirectory())) {
            throw new IOException("no directory for " + target);
        }
        File tmp = new File(parent, target.getName() + TMP_EXT);
        boolean moved = false;
        try {
            FileOutputStream out = new FileOutputStream(tmp);
            try {
                out.write(content);
                out.flush();
                out.getFD().sync();
            } finally {
                out.close();
            }
            Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE);
            moved = true;
        } finally {
            if (!moved) {
                tmp.delete();
            }
        }
        syncDirectory(parent);
    }

    /** Like writeAtomic, but refuses to replace an existing target. */
    static void writeAtomicNew(File target, byte[] content) throws IOException {
        if (target.exists()) {
            throw new FileAlreadyExistsException(target.getPath());
        }
        writeAtomic(target, content);
    }

    /** Renames atomically within the store directory; refuses to replace an existing target. */
    static void moveAtomic(File from, File to) throws IOException {
        if (to.exists()) {
            throw new FileAlreadyExistsException(to.getPath());
        }
        Files.move(from.toPath(), to.toPath(), StandardCopyOption.ATOMIC_MOVE);
        syncDirectory(to.getParentFile());
    }

    /** Best effort: forces the directory entry to storage where the platform supports it. */
    static void syncDirectory(File d) {
        if (d == null) {
            return;
        }
        FileChannel ch = null;
        try {
            ch = FileChannel.open(d.toPath(), StandardOpenOption.READ);
            ch.force(true);
        } catch (Throwable ignored) {
            //not supported everywhere (for example Windows); the file data was already synced
        } finally {
            if (ch != null) {
                try {
                    ch.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /** Reads a whole file, refusing anything larger than MAX_FILE_BYTES. */
    static byte[] readBounded(File f) throws IOException {
        if (f.length() > MAX_FILE_BYTES) {
            throw new CorruptFileException("file too large: " + f.getName());
        }
        return Files.readAllBytes(f.toPath());
    }

    /** Reads and validates a key=value file; a damaged file is a CorruptFileException. */
    Map<String, String> readKv(File f) throws IOException {
        return decode(readBounded(f));
    }

    // ---------------- versioned key=value format with CRC32 ----------------

    /**
     * v=1, then the entries in order, then crc32 over every preceding byte. Values are
     * escaped onto one line; keys must match KEY and must not be v or crc32.
     */
    static byte[] encode(Map<String, String> entries) {
        StringBuilder sb = new StringBuilder();
        sb.append("v=").append(FORMAT_VERSION).append('\n');
        for (Map.Entry<String, String> e : entries.entrySet()) {
            String k = e.getKey();
            if (k == null || !KEY.matcher(k).matches() || k.equals("v") || k.equals("crc32")) {
                throw new IllegalArgumentException("invalid key " + k);
            }
            String v = e.getValue() == null ? "" : e.getValue();
            sb.append(k).append('=').append(escape(v)).append('\n');
        }
        String body = sb.toString();
        return (body + "crc32=" + crcHex(body) + "\n").getBytes(StandardCharsets.UTF_8);
    }

    /** Inverse of encode; any deviation (version, CRC, key, escape, duplicate) is corrupt. */
    static Map<String, String> decode(byte[] data) throws CorruptFileException {
        if (data == null || data.length == 0) {
            throw new CorruptFileException("empty file");
        }
        String text = new String(data, StandardCharsets.UTF_8);
        int at = text.lastIndexOf("\ncrc32=");
        if (at < 0) {
            throw new CorruptFileException("missing crc32");
        }
        String body = text.substring(0, at + 1);
        String tail = text.substring(at + 7);
        if (tail.isEmpty() || tail.indexOf('\n') != tail.length() - 1
                || !crcHex(body).equals(tail.substring(0, tail.length() - 1))) {
            throw new CorruptFileException("crc32 mismatch");
        }
        String[] lines = body.split("\n", -1);
        //body ends with '\n', so the last element is empty
        if (lines.length < 2 || !lines[0].equals("v=" + FORMAT_VERSION)) {
            throw new CorruptFileException("unsupported format version");
        }
        Map<String, String> out = new LinkedHashMap<>();
        for (int i = 1; i < lines.length - 1; i++) {
            int eq = lines[i].indexOf('=');
            String k = eq > 0 ? lines[i].substring(0, eq) : "";
            boolean badKey = !KEY.matcher(k).matches() || k.equals("v") || k.equals("crc32");
            if (badKey || out.containsKey(k)) {
                throw new CorruptFileException("invalid line " + i);
            }
            out.put(k, unescape(lines[i].substring(eq + 1)));
        }
        return out;
    }

    /** Escapes backslash, LF and CR so every value stays on one line. */
    static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '\n') {
                sb.append("\\n");
            } else if (c == '\r') {
                sb.append("\\r");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** CRC32 of the UTF-8 bytes of body, as lowercase hex without leading zeros. */
    static String crcHex(String body) {
        CRC32 crc = new CRC32();
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        crc.update(b, 0, b.length);
        return Long.toHexString(crc.getValue());
    }

    /** Inverse of escape; a dangling or unknown escape is a CorruptFileException. */
    static String unescape(String s) throws CorruptFileException {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\\') {
                sb.append(c);
                continue;
            }
            if (i + 1 >= s.length()) {
                throw new CorruptFileException("dangling escape");
            }
            char n = s.charAt(++i);
            if (n == '\\') {
                sb.append('\\');
            } else if (n == 'n') {
                sb.append('\n');
            } else if (n == 'r') {
                sb.append('\r');
            } else {
                throw new CorruptFileException("invalid escape");
            }
        }
        return sb.toString();
    }

    static long parseLong(String s, String key) throws CorruptFileException {
        if (s == null) {
            throw new CorruptFileException("missing " + key);
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new CorruptFileException("invalid " + key);
        }
    }

    static int nonNegativeInt(String s, String key) throws CorruptFileException {
        long v = parseLong(s, key);
        if (v < 0 || v > Integer.MAX_VALUE) {
            throw new CorruptFileException("invalid " + key);
        }
        return (int) v;
    }

    /** null for "unknown"; otherwise a positive number. */
    static Long parseSeq(String s) throws CorruptFileException {
        if (UNKNOWN.equals(s)) {
            return null;
        }
        long v = parseLong(s, "seq");
        if (v <= 0) {
            throw new CorruptFileException("invalid seq");
        }
        return v;
    }

    static String seqText(Long seq) {
        return seq == null ? UNKNOWN : String.valueOf(seq);
    }

    static String nz(String s) {
        return s == null ? "" : s;
    }

    static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }

    /** The id in pending_/held_/retry_/corrupt_<id>.rec or sent_<id>.ok, or null otherwise. */
    static String idOf(String name) {
        String rest = null;
        for (String p : new String[] {PENDING, HELD, RETRY, CORRUPT}) {
            if (name.startsWith(p) && name.endsWith(REC_EXT)
                    && name.length() >= p.length() + REC_EXT.length()) {
                rest = name.substring(p.length(), name.length() - REC_EXT.length());
            }
        }
        if (name.startsWith(SENT) && name.endsWith(SENT_EXT)
                && name.length() >= SENT.length() + SENT_EXT.length()) {
            rest = name.substring(SENT.length(), name.length() - SENT_EXT.length());
        }
        return rest != null && ID.matcher(rest).matches() ? rest : null;
    }

    /** A file that exists but fails validation; it is never trusted. */
    public static final class CorruptFileException extends IOException {
        public CorruptFileException(String message) {
            super(message);
        }
    }

    // ---------------- crash report ----------------

    /** One crash report; the field names are the keys in its file. seq == null means unknown. */
    public static final class Report {
        public String id;
        public Long seq;
        public String seqNote = "";
        public int suppressedBefore;
        public long time;
        public long cycleStart;
        public long expiresAt;
        public int cycle = 1;
        public int attempts;
        public long lastAttemptAt;
        public String version = "";
        public String logPath = "";
        public String exc = "";
        public long heldAt;
        public String heldReason = "";

        /** A new report: cycle 1 starting at the crash time and expiring WINDOW_MS later. */
        public static Report newReport(String id, Long seq, String seqNote, int suppressedBefore,
                long crashTime, String version, String logPath, String exc) {
            if (id == null || !ID.matcher(id).matches()) {
                throw new IllegalArgumentException("invalid id");
            }
            Report r = new Report();
            r.id = id;
            r.seq = seq;
            r.seqNote = nz(seqNote);
            r.suppressedBefore = Math.max(0, suppressedBefore);
            r.time = crashTime;
            r.cycleStart = crashTime;
            r.expiresAt = crashTime + WINDOW_MS;
            r.version = nz(version);
            r.logPath = nz(logPath);
            r.exc = truncate(nz(exc), EXC_MAX_CHARS);
            return r;
        }

        Map<String, String> toMap() {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("id", id);
            m.put("seq", seqText(seq));
            m.put("seqNote", nz(seqNote));
            m.put("suppressedBefore", String.valueOf(suppressedBefore));
            m.put("time", String.valueOf(time));
            m.put("cycleStart", String.valueOf(cycleStart));
            m.put("expiresAt", String.valueOf(expiresAt));
            m.put("cycle", String.valueOf(cycle));
            m.put("attempts", String.valueOf(attempts));
            m.put("lastAttemptAt", String.valueOf(lastAttemptAt));
            m.put("version", nz(version));
            m.put("logPath", nz(logPath));
            m.put("exc", nz(exc));
            m.put("heldAt", String.valueOf(heldAt));
            m.put("heldReason", nz(heldReason));
            return m;
        }

        static Report fromMap(Map<String, String> m) throws CorruptFileException {
            Report r = new Report();
            r.id = m.get("id");
            if (r.id == null || !ID.matcher(r.id).matches()) {
                throw new CorruptFileException("invalid id");
            }
            r.seq = parseSeq(m.get("seq"));
            r.seqNote = nz(m.get("seqNote"));
            r.suppressedBefore = nonNegativeInt(m.get("suppressedBefore"), "suppressedBefore");
            r.time = parseLong(m.get("time"), "time");
            r.cycleStart = parseLong(m.get("cycleStart"), "cycleStart");
            r.expiresAt = parseLong(m.get("expiresAt"), "expiresAt");
            r.cycle = nonNegativeInt(m.get("cycle"), "cycle");
            r.attempts = nonNegativeInt(m.get("attempts"), "attempts");
            r.lastAttemptAt = parseLong(m.get("lastAttemptAt"), "lastAttemptAt");
            r.version = nz(m.get("version"));
            r.logPath = nz(m.get("logPath"));
            r.exc = nz(m.get("exc"));
            r.heldAt = parseLong(m.get("heldAt"), "heldAt");
            r.heldReason = nz(m.get("heldReason"));
            if (r.cycle < 1) {
                throw new CorruptFileException("invalid cycle");
            }
            return r;
        }
    }

    // ---------------- report files ----------------

    public File file(String prefix, String id) {
        return new File(dir, prefix + id + REC_EXT);
    }

    /**
     * Saves a new pending report. No lock is needed because the name is unique; an existing
     * file is never replaced.
     */
    public File savePending(Report r) throws IOException {
        ensureDir();
        File f = file(PENDING, r.id);
        writeAtomicNew(f, encode(r.toMap()));
        return f;
    }

    /**
     * Atomically replaces an existing report file of the given state with the report's
     * current fields.
     */
    public void rewrite(String prefix, Report r) throws IOException {
        File f = file(prefix, r.id);
        if (!f.isFile()) {
            throw new NoSuchFileException(f.getPath());
        }
        writeAtomic(f, encode(r.toMap()));
    }

    /** Reads and validates a report; the id inside must match the id in the file name. */
    public Report read(File f) throws IOException {
        Report r = Report.fromMap(readKv(f));
        if (!r.id.equals(idOf(f.getName()))) {
            throw new CorruptFileException("id does not match file name");
        }
        return r;
    }

    /**
     * Report files of one state (PENDING, HELD, RETRY or CORRUPT), sorted by name; .tmp files
     * are never listed.
     */
    public List<File> list(String prefix) {
        return listMatching(prefix, REC_EXT);
    }

    private List<File> listMatching(String prefix, String ext) {
        List<File> out = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            Arrays.sort(files);
            for (File f : files) {
                String n = f.getName();
                if (f.isFile() && n.startsWith(prefix) && n.endsWith(ext)) {
                    out.add(f);
                }
            }
        }
        return out;
    }

    /** Moves a report to another state keeping its id; refuses to replace an existing file. */
    public File move(File from, String toPrefix) throws IOException {
        String id = idOf(from.getName());
        if (id == null) {
            throw new IOException("not a report file: " + from.getName());
        }
        File to = file(toPrefix, id);
        moveAtomic(from, to);
        return to;
    }

    /** Renames an unreadable report to corrupt_<id>.rec (numbered if taken); never deletes it. */
    public File markCorrupt(File f) throws IOException {
        String id = idOf(f.getName());
        String safe = id != null ? id : f.getName().replaceAll("[^0-9A-Za-z-]", "-");
        String base = CORRUPT + safe;
        File to = new File(dir, base + REC_EXT);
        for (int i = 1; to.exists(); i++) {
            to = new File(dir, base + "-" + i + REC_EXT);
        }
        moveAtomic(f, to);
        return to;
    }

    /**
     * Deletes .tmp files older than TMP_MIN_AGE_MS; younger ones may belong to a write in
     * progress.
     */
    public int cleanupTemps(long now) {
        int n = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                boolean oldTmp = f.isFile() && f.getName().endsWith(TMP_EXT)
                        && now - f.lastModified() >= TMP_MIN_AGE_MS;
                if (oldTmp && f.delete()) {
                    n++;
                }
            }
        }
        return n;
    }

    // ---------------- delivery receipts ----------------

    public File receiptFile(String id) {
        return new File(dir, SENT + id + SENT_EXT);
    }

    /** Written only after sendEmails returned true, i.e. after the SMTP server accepted it. */
    public void writeReceipt(String id, Long seq, long sentAt) throws IOException {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("seq", seqText(seq));
        m.put("sentAt", String.valueOf(sentAt));
        ensureDir();
        writeAtomic(receiptFile(id), encode(m));
    }

    /**
     * A receipt file is evidence of delivery even if later damaged, because it is created
     * only after the SMTP server accepted the message.
     */
    public boolean hasReceipt(String id) {
        return receiptFile(id).isFile();
    }

    public List<File> listReceipts() {
        return listMatching(SENT, SENT_EXT);
    }

    /**
     * Deletes receipts older than maxAgeMs (30 days in the approved design) by sentAt, or by
     * file time if a receipt is unreadable. Returns the number deleted.
     */
    public int pruneReceipts(long now, long maxAgeMs) {
        int n = 0;
        for (File f : listReceipts()) {
            long sentAt;
            try {
                sentAt = parseLong(readKv(f).get("sentAt"), "sentAt");
            } catch (IOException e) {
                sentAt = f.lastModified();
            }
            if (now - sentAt > maxAgeMs && f.delete()) {
                n++;
            }
        }
        return n;
    }

    // ---------------- crash sequence counter ----------------

    /** Result of allocateSeq(): seq == null means unknown, and note says why. */
    public static final class SeqAllocation {
        public final Long seq;
        public final String note;
        public final int suppressedBefore;

        SeqAllocation(Long seq, String note, int suppressedBefore) {
            this.seq = seq;
            this.note = nz(note);
            this.suppressedBefore = suppressedBefore;
        }
    }

    /** Counter contents: seq is the last allocated number (0 for none). */
    static final class Counter {
        long seq;
        int suppressed;
    }

    /**
     * Allocates the next sequence number and persists the counter before returning, under the
     * store lock (at most lockTimeoutMs). Never throws: on failure seq is null and note says
     * why. Suppressed crashes since the last report are returned and reset in the same write.
     */
    public SeqAllocation allocateSeq(long lockTimeoutMs) {
        boolean locked = false;
        try {
            locked = lock(lockTimeoutMs);
            if (!locked) {
                String why = "counter lock not acquired within " + lockTimeoutMs + " ms";
                return new SeqAllocation(null, why, 0);
            }
            StringBuilder note = new StringBuilder();
            Counter c = loadCounterOrRecover(note);
            long base = Math.max(c.seq, readLastSentOrZero());
            if (base == Long.MAX_VALUE) {
                return new SeqAllocation(null, "counter exhausted at Long.MAX_VALUE", 0);
            }
            Counter next = new Counter();
            next.seq = base + 1;
            try {
                writeCounter(next);
            } catch (Throwable t) {
                return new SeqAllocation(null, "counter write failed; last known " + base, 0);
            }
            return new SeqAllocation(next.seq, note.toString(), c.suppressed);
        } catch (Throwable t) {
            return new SeqAllocation(null, "counter error: " + t.getClass().getSimpleName(), 0);
        } finally {
            if (locked) {
                unlock();
            }
        }
    }

    /** Counts one suppressed crash (60-second rule) without allocating a number. Never throws. */
    public boolean incrementSuppressed(long lockTimeoutMs) {
        boolean locked = false;
        try {
            locked = lock(lockTimeoutMs);
            if (!locked) {
                return false;
            }
            Counter c = loadCounterOrRecover(new StringBuilder());
            if (c.suppressed < Integer.MAX_VALUE) {
                c.suppressed++;
            }
            writeCounter(c);
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            if (locked) {
                unlock();
            }
        }
    }

    /**
     * Raises the stored counter to at least minSeq, never lowering it and keeping the
     * suppressed count. False for minSeq <= 0, a lock timeout or any failure. Never throws.
     */
    public boolean raiseCounterTo(long minSeq, long lockTimeoutMs) {
        if (minSeq <= 0) {
            return false;
        }
        boolean locked = false;
        try {
            locked = lock(lockTimeoutMs);
            if (!locked) {
                return false;
            }
            Counter c = loadCounterOrRecover(new StringBuilder());
            if (c.seq < minSeq) {
                c.seq = minSeq;
                writeCounter(c);
            }
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            if (locked) {
                unlock();
            }
        }
    }

    /**
     * The counter as stored, or null if no counter file exists. A path that exists but is not
     * a regular file, or a damaged or invalid file (bad CRC, negative seq, invalid suppressed
     * count), is a CorruptFileException. The caller must hold the store lock.
     */
    Counter readCounter() throws IOException {
        File f = new File(dir, COUNTER_FILE);
        if (!f.exists()) {
            return null;
        }
        if (!f.isFile()) {
            throw new CorruptFileException("counter path is not a regular file");
        }
        Map<String, String> m = readKv(f);
        Counter c = new Counter();
        c.seq = parseLong(m.get("seq"), "seq");
        c.suppressed = nonNegativeInt(m.get("suppressed"), "suppressed");
        if (c.seq < 0) {
            throw new CorruptFileException("invalid seq");
        }
        return c;
    }

    /** Writes the counter atomically (versioned, CRC). The caller must hold the store lock. */
    private void writeCounter(Counter c) throws IOException {
        if (c.seq < 0 || c.suppressed < 0) {
            throw new IllegalArgumentException("invalid counter");
        }
        Map<String, String> m = new LinkedHashMap<>();
        m.put("seq", String.valueOf(c.seq));
        m.put("suppressed", String.valueOf(c.suppressed));
        ensureDir();
        writeAtomic(new File(dir, COUNTER_FILE), encode(m));
    }

    // ---------------- counter recovery ----------------

    private static final String NOTE_FILE = COUNTER_FILE + ".note";
    private static final String NOTE_CORRUPT_FILE = NOTE_FILE + ".corrupt";
    private static final String LAST_SENT_CORRUPT_FILE = LAST_SENT_FILE + ".corrupt";

    /**
     * Reads the counter. A missing or corrupt counter is rebuilt from recoverMaxSeq(); an
     * unreadable one is an IOException and nothing changes. A corrupt counter is preserved as
     * seq.corrupt, seq.corrupt-1, ... and its suppressed count is lost (the note says so).
     * Every pending recovery note is appended to note. The caller must hold the store lock.
     */
    private Counter loadCounterOrRecover(StringBuilder note) throws IOException {
        Counter c = null;
        boolean corrupt = false;
        try {
            c = readCounter();
        } catch (CorruptFileException e) {
            corrupt = true;
        }
        if (c == null) {
            SeqRecovery r = recoverMaxSeq();
            //note first: if it cannot be stored nothing is moved or rewritten, and the next
            //attempt repeats the whole recovery, so the note is never lost
            addRecoveryNote(recoveryText(corrupt, r));
            if (corrupt) {
                preserveCorrupt(new File(dir, COUNTER_FILE), COUNTER_CORRUPT_FILE);
            }
            c = new Counter();
            c.seq = r.max;
            writeCounter(c);
        }
        appendPendingNotes(note);
        return c;
    }

    /** Recovery note for a missing or corrupt counter; r is the recoverMaxSeq() result. */
    private static String recoveryText(boolean corrupt, SeqRecovery r) {
        String lost = corrupt ? "; suppressed count lost because the counter was corrupt"
                + " (damaged counter kept as " + COUNTER_CORRUPT_FILE + "*)" : "";
        String found = r.issues.isEmpty() ? "" : "; recovery found: " + r.issues;
        if (r.max <= 0) {
            return (corrupt ? "counter reset; no prior values found" + lost
                    : "new counter (first report on this install or app data cleared)") + found;
        }
        String from = r.source + (r.usedHint ? ", includes hint from corrupt record" : "");
        return (corrupt ? "counter recovered" : "counter missing") + " (max " + r.max + " from "
                + from + "); possible gap" + lost + found;
    }

    /**
     * Moves damaged evidence to baseName, or baseName-1, baseName-2, ... if that name is
     * taken; existing evidence is never overwritten. The caller must hold the store lock.
     */
    File preserveCorrupt(File f, String baseName) throws IOException {
        File to = new File(dir, baseName);
        for (int i = 1; to.exists(); i++) {
            to = new File(dir, baseName + "-" + i);
        }
        moveAtomic(f, to);
        return to;
    }

    // ---------------- recovery notes (seq.note) ----------------

    /**
     * Pending recovery notes as {id, text}, oldest first; empty if seq.note does not exist.
     * A damaged note file is preserved as seq.note.corrupt* and replaced by one note saying
     * so, so the loss is reported instead of hidden. The caller must hold the store lock.
     */
    private List<String[]> readNotesOrRepair() throws IOException {
        File f = new File(dir, NOTE_FILE);
        if (!f.exists()) {
            return new ArrayList<>();
        }
        try {
            if (!f.isFile()) {
                throw new CorruptFileException("note path is not a regular file");
            }
            List<String[]> out = new ArrayList<>();
            for (String v : readKv(f).values()) {
                int bar = v.indexOf('|');
                if (bar < 1 || !ID.matcher(v.substring(0, bar)).matches()) {
                    throw new CorruptFileException("invalid recovery note");
                }
                out.add(new String[] {v.substring(0, bar), v.substring(bar + 1)});
            }
            return out;
        } catch (CorruptFileException e) {
            File kept = preserveCorrupt(f, NOTE_CORRUPT_FILE);
            List<String[]> out = new ArrayList<>();
            out.add(newNote("earlier recovery notes unreadable; kept as " + kept.getName()));
            writeNotes(out);
            return out;
        }
    }

    private static String[] newNote(String text) {
        return new String[] {java.util.UUID.randomUUID().toString(), text};
    }

    /** Writes the notes atomically, or deletes seq.note when none remain. Lock required. */
    private void writeNotes(List<String[]> notes) throws IOException {
        File f = new File(dir, NOTE_FILE);
        if (notes.isEmpty()) {
            Files.deleteIfExists(f.toPath());
            return;
        }
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < notes.size(); i++) {
            m.put("n" + (i + 1), notes.get(i)[0] + "|" + notes.get(i)[1]);
        }
        ensureDir();
        writeAtomic(f, encode(m));
    }

    /** Stores one more recovery note, keeping every existing one. Lock required. */
    private void addRecoveryNote(String text) throws IOException {
        List<String[]> notes = readNotesOrRepair();
        notes.add(newNote(text));
        writeNotes(notes);
    }

    /** Appends every pending note as "[R7N:id] text"; an unreadable note store is reported. */
    private void appendPendingNotes(StringBuilder note) {
        List<String[]> notes;
        try {
            notes = readNotesOrRepair();
        } catch (IOException e) {
            separate(note).append("recovery notes unreadable: ")
                    .append(e.getClass().getSimpleName());
            return;
        }
        for (String[] n : notes) {
            separate(note).append(tag(n[0])).append(n[1]);
        }
    }

    private static StringBuilder separate(StringBuilder sb) {
        return sb.length() == 0 ? sb : sb.append("; ");
    }

    private static String tag(String id) {
        return "[R7N:" + id + "] ";
    }

    /**
     * Call only after savePending() has saved the report carrying savedNote (its seqNote).
     * Removes exactly the notes that report carried: a note is removed only if
     * "[R7N:id] text" appears verbatim in savedNote. Newer notes are kept. False, keeping
     * every note, on a lock timeout or any failure. Never throws.
     */
    public boolean clearRecoveryNotes(String savedNote, long lockTimeoutMs) {
        if (savedNote == null || savedNote.isEmpty()) {
            return true;
        }
        boolean locked = false;
        try {
            locked = lock(lockTimeoutMs);
            if (!locked) {
                return false;
            }
            List<String[]> notes = readNotesOrRepair();
            List<String[]> keep = new ArrayList<>();
            for (String[] n : notes) {
                if (!savedNote.contains(tag(n[0]) + n[1])) {
                    keep.add(n);
                }
            }
            if (keep.size() != notes.size()) {
                writeNotes(keep);
            }
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            if (locked) {
                unlock();
            }
        }
    }

    // ---------------- last delivered sequence (last_sent) ----------------

    /** Highest delivered sequence; 0 when last_sent is missing, corrupt or unreadable. */
    public long readLastSentOrZero() {
        try {
            Long v = readLastSentStrict();
            return v == null ? 0 : v;
        } catch (IOException e) {
            return 0;
        }
    }

    /** null if last_sent does not exist; CorruptFileException if damaged or not a file. */
    private Long readLastSentStrict() throws IOException {
        File f = new File(dir, LAST_SENT_FILE);
        if (!f.exists()) {
            return null;
        }
        if (!f.isFile()) {
            throw new CorruptFileException("last_sent is not a regular file");
        }
        long v = parseLong(readKv(f).get("seq"), "seq");
        if (v <= 0) {
            throw new CorruptFileException("invalid last_sent seq");
        }
        return v;
    }

    /**
     * Raises last_sent to at least seq and never lowers it. A missing or corrupt last_sent is
     * rebuilt as the larger of seq and the highest valid receipt; a corrupt one is recorded
     * in a recovery note and preserved as last_sent.corrupt* first. An unreadable one is left
     * unchanged. False for seq <= 0, a lock timeout or any failure. Never throws.
     */
    public boolean updateLastSent(long seq, long lockTimeoutMs) {
        if (seq <= 0) {
            return false;
        }
        boolean locked = false;
        try {
            locked = lock(lockTimeoutMs);
            if (!locked) {
                return false;
            }
            Long current;
            try {
                current = readLastSentStrict();
            } catch (CorruptFileException e) {
                addRecoveryNote("last_sent corrupt (kept as " + LAST_SENT_CORRUPT_FILE
                        + "*); rebuilt from receipts");
                preserveCorrupt(new File(dir, LAST_SENT_FILE), LAST_SENT_CORRUPT_FILE);
                current = null;
            }
            long target = current == null ? Math.max(seq, maxReceiptSeq()) : seq;
            if (current == null || current < target) {
                writeLastSent(target);
            }
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            if (locked) {
                unlock();
            }
        }
    }

    private void writeLastSent(long v) throws IOException {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("seq", String.valueOf(v));
        ensureDir();
        writeAtomic(new File(dir, LAST_SENT_FILE), encode(m));
    }

    // ---------------- delivery receipt evidence ----------------

    /**
     * Validated sequence of a receipt: version and CRC (readKv), an id equal to the id in the
     * file name, a positive sentAt, and a seq that parseSeq accepts; null for an unknown seq.
     * Anything else is a CorruptFileException, so an incomplete receipt is never trusted.
     */
    private Long readReceiptSeq(File f) throws IOException {
        Map<String, String> m = readKv(f);
        String id = m.get("id");
        if (id == null || !id.equals(idOf(f.getName()))) {
            throw new CorruptFileException("receipt id does not match file name");
        }
        if (parseLong(m.get("sentAt"), "sentAt") <= 0) {
            throw new CorruptFileException("invalid sentAt");
        }
        return parseSeq(m.get("seq"));
    }

    /** Highest seq in valid sent_*.ok receipts: delivered reports only, never pending or held. */
    private long maxReceiptSeq() {
        long max = 0;
        for (File f : listReceipts()) {
            try {
                Long s = readReceiptSeq(f);
                if (s != null && s > max) {
                    max = s;
                }
            } catch (IOException | RuntimeException e) {
                //an invalid or unreadable receipt proves nothing
            }
        }
        return max;
    }

    // ---------------- sequence recovery ----------------

    /** Result of recoverMaxSeq(): highest sequence, its source, and problems found. */
    public static final class SeqRecovery {
        public final long max;
        public final String source;
        public final boolean usedHint;
        /** Corrupt or unreadable evidence seen, e.g. "last_sent corrupt"; "" if none. */
        public final String issues;

        SeqRecovery(long max, String source, boolean usedHint, String issues) {
            this.max = max;
            this.source = source;
            this.usedHint = usedHint;
            this.issues = nz(issues);
        }
    }

    /** Running state of one recoverMaxSeq() scan. */
    private static final class Scan {
        long trusted;
        String trustedFrom = "none";
        long hint;
        String hintFrom = "none";
        final StringBuilder issues = new StringBuilder();

        void trust(long v, String from) {
            if (v > trusted) {
                trusted = v;
                trustedFrom = from;
            }
        }

        void hint(long v, String from) {
            if (v > hint) {
                hint = v;
                hintFrom = from;
            }
        }

        void issue(String text) {
            separate(issues).append(text);
        }
    }

    /**
     * Highest crash sequence number in the store. Trusted: a valid counter, valid pending,
     * held and retry reports, valid receipts, a valid last_sent and a valid summary. Hints:
     * the seq= line of corrupt files (counter, seq.corrupt*, last_sent, last_sent.corrupt*,
     * corrupt_*.rec, reports, receipts) and readable summary lines; a hint counts only if at
     * most HINT_MAX_ABOVE_TRUSTED above the trusted maximum. Each category is scanned on its
     * own, so a failure is listed in issues and the rest are still scanned. Read-only; JVM
     * errors such as OutOfMemoryError are not caught.
     */
    public SeqRecovery recoverMaxSeq() {
        Scan s = new Scan();
        try {
            scanCounter(s);
        } catch (RuntimeException e) {
            s.issue("counter scan failed: " + e.getClass().getSimpleName());
        }
        try {
            scanReports(s);
        } catch (RuntimeException e) {
            s.issue("reports scan failed: " + e.getClass().getSimpleName());
        }
        try {
            scanLastSent(s);
        } catch (RuntimeException e) {
            s.issue("last_sent scan failed: " + e.getClass().getSimpleName());
        }
        try {
            scanSummary(s);
        } catch (RuntimeException e) {
            s.issue("summary scan failed: " + e.getClass().getSimpleName());
        }
        try {
            scanEvidence(s);
        } catch (RuntimeException e) {
            s.issue("evidence scan failed: " + e.getClass().getSimpleName());
        }
        if (s.hint > s.trusted && s.hint - s.trusted <= HINT_MAX_ABOVE_TRUSTED) {
            return new SeqRecovery(s.hint, s.hintFrom, true, s.issues.toString());
        }
        return new SeqRecovery(s.trusted, s.trustedFrom, false, s.issues.toString());
    }

    /** A valid counter is trusted; a corrupt one gives only a hint. Read-only. */
    private void scanCounter(Scan s) {
        try {
            Counter c = readCounter();
            if (c != null) {
                s.trust(c.seq, "counter");
            }
        } catch (CorruptFileException e) {
            s.issue("counter corrupt");
            s.hint(scanHint(new File(dir, COUNTER_FILE), SEQ_HINT), "corrupt counter");
        } catch (IOException | RuntimeException e) {
            s.issue("counter unreadable");
        }
    }

    /** Valid pending, held and retry reports and valid receipts are trusted; others hint. */
    private void scanReports(Scan s) {
        List<File> files = new ArrayList<>();
        for (String prefix : new String[] {PENDING, HELD, RETRY}) {
            files.addAll(list(prefix));
        }
        files.addAll(listReceipts());
        for (File f : files) {
            boolean receipt = f.getName().startsWith(SENT);
            try {
                Long v = receipt ? readReceiptSeq(f) : read(f).seq;
                if (v != null) {
                    s.trust(v, receipt ? "sent receipt" : "report");
                }
            } catch (CorruptFileException e) {
                s.issue("corrupt " + f.getName());
                s.hint(scanHint(f, SEQ_HINT), "corrupt " + f.getName());
            } catch (IOException | RuntimeException e) {
                s.issue("unreadable " + f.getName());
            }
        }
    }

    /** A valid last_sent is trusted; missing adds nothing; corrupt or unreadable is an issue. */
    private void scanLastSent(Scan s) {
        try {
            Long v = readLastSentStrict();
            if (v != null) {
                s.trust(v, "last_sent");
            }
        } catch (CorruptFileException e) {
            s.issue("last_sent corrupt");
            s.hint(scanHint(new File(dir, LAST_SENT_FILE), SEQ_HINT), "corrupt last_sent");
        } catch (IOException | RuntimeException e) {
            s.issue("last_sent unreadable");
        }
    }

    /** A valid summary is trusted; a damaged one gives hints from its readable lines. */
    private void scanSummary(Scan s) {
        File f = new File(dir, SUMMARY_FILE);
        if (!f.exists()) {
            return;
        }
        try {
            s.trust(readSummary().maxSeq(), "summary");
        } catch (CorruptFileException e) {
            s.issue("summary corrupt");
            s.hint(scanSummaryHint(f), "corrupt summary");
        } catch (IOException | RuntimeException e) {
            s.issue("summary unreadable");
        }
    }

    /**
     * Hints from preserved evidence: seq.corrupt*, last_sent.corrupt*, corrupt_*.rec and
     * undelivered_summary.corrupt*. Recovery notes (seq.note*) are never scanned. scanHint
     * returns 0 instead of throwing, so one bad file cannot stop the others.
     */
    private void scanEvidence(Scan s) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            String n = f.getName();
            if (n.startsWith(COUNTER_CORRUPT_FILE) || n.startsWith(LAST_SENT_CORRUPT_FILE)
                    || (n.startsWith(CORRUPT) && n.endsWith(REC_EXT))) {
                s.hint(scanHint(f, SEQ_HINT), n);
            } else if (n.startsWith(SUMMARY_CORRUPT_FILE)) {
                s.hint(scanSummaryHint(f), n);
            }
        }
    }

    /**
     * Largest number captured by p in a possibly damaged file; 0 if none, too large or
     * unreadable. Catches IOException and RuntimeException only; JVM errors propagate.
     */
    static long scanHint(File f, Pattern p) {
        long max = 0;
        try {
            if (f == null || !f.isFile() || f.length() > MAX_FILE_BYTES) {
                return 0;
            }
            Matcher m = p.matcher(new String(readBounded(f), StandardCharsets.ISO_8859_1));
            while (m.find()) {
                try {
                    max = Math.max(max, Long.parseLong(m.group(1)));
                } catch (NumberFormatException ignored) {
                    //patterns allow at most 18 digits, so this cannot overflow; kept defensive
                }
            }
        } catch (IOException | RuntimeException ignored) {
            //an unreadable file gives no hint
        }
        return max;
    }

    static long scanSummaryHint(File f) {
        return Math.max(scanHint(f, SUMMARY_HINT), scanHint(f, OMITTED_HINT));
    }

    /** One summary line: seq|id|time|attempts|condensedAt|status|exc (exc may contain '|'). */
    static final class SummaryEntry {
        Long seq;
        String id;
        long time;
        int attempts;
        long condensedAt;
        String status;
        String exc;

        static SummaryEntry of(Report r, String status, long condensedAt) {
            SummaryEntry e = new SummaryEntry();
            e.seq = r.seq;
            e.id = r.id;
            e.time = r.time;
            e.attempts = r.attempts;
            e.condensedAt = condensedAt;
            e.status = nz(status).replace('|', '/');
            e.exc = truncate(nz(r.exc), SUMMARY_EXC_MAX_CHARS);
            return e;
        }

        String encodeValue() {
            return seqText(seq) + "|" + id + "|" + time + "|" + attempts + "|" + condensedAt
                    + "|" + status + "|" + exc;
        }

        static SummaryEntry decodeValue(String v) throws CorruptFileException {
            String[] p = v == null ? new String[0] : v.split("\\|", 7);
            if (p.length != 7 || !ID.matcher(p[1]).matches()) {
                throw new CorruptFileException("invalid summary entry");
            }
            SummaryEntry e = new SummaryEntry();
            e.seq = parseSeq(p[0]);
            e.id = p[1];
            e.time = parseLong(p[2], "time");
            e.attempts = nonNegativeInt(p[3], "attempts");
            e.condensedAt = parseLong(p[4], "condensedAt");
            e.status = p[5];
            e.exc = p[6];
            return e;
        }
    }

    /** Summary contents: entries oldest first; older ones are folded into the omitted range. */
    static final class Summary {
        final List<SummaryEntry> entries = new ArrayList<>();
        long omitted;
        long omittedMinSeq;
        long omittedMaxSeq;
        long omittedFirstTime;
        long omittedLastTime;

        /** Highest sequence in the entries and the omitted range; 0 if none is known. */
        long maxSeq() {
            long max = omittedMaxSeq;
            for (SummaryEntry e : entries) {
                if (e.seq != null && e.seq > max) {
                    max = e.seq;
                }
            }
            return max;
        }

        /** Folds the oldest entries into the omitted range until SUMMARY_MAX_ENTRIES remain. */
        void fold() {
            while (entries.size() > SUMMARY_MAX_ENTRIES) {
                SummaryEntry e = entries.remove(0);
                long s = e.seq == null ? 0 : e.seq;
                if (s > 0) {
                    omittedMinSeq = omittedMinSeq == 0 ? s : Math.min(omittedMinSeq, s);
                }
                omittedMaxSeq = Math.max(omittedMaxSeq, s);
                omittedFirstTime = omitted == 0 ? e.time : Math.min(omittedFirstTime, e.time);
                omittedLastTime = Math.max(omittedLastTime, e.time);
                omitted++;
            }
        }
    }

    // ---------------- undelivered summary file ----------------

    /**
     * Reads undelivered_summary.txt: an optional omitted=<n>|<minSeq>|<maxSeq>|<first>|<last>
     * record and e1..eN in order. Missing: NoSuchFileException. Not a regular file, damaged
     * (version, CRC, format) or any invalid key or value: CorruptFileException. Other I/O
     * failures stay IOExceptions. The caller must hold the store lock.
     */
    Summary readSummary() throws IOException {
        File f = new File(dir, SUMMARY_FILE);
        if (!f.exists()) {
            throw new NoSuchFileException(f.getPath());
        }
        if (!f.isFile()) {
            throw new CorruptFileException("summary path is not a regular file");
        }
        Map<String, String> m = readKv(f);
        Summary s = new Summary();
        String om = m.remove("omitted");
        if (om != null) {
            String[] p = om.split("\\|", -1);
            if (p.length != 5) {
                throw new CorruptFileException("invalid omitted record");
            }
            s.omitted = parseLong(p[0], "omitted");
            s.omittedMinSeq = parseLong(p[1], "omittedMinSeq");
            s.omittedMaxSeq = parseLong(p[2], "omittedMaxSeq");
            s.omittedFirstTime = parseLong(p[3], "omittedFirstTime");
            s.omittedLastTime = parseLong(p[4], "omittedLastTime");
            if (s.omitted <= 0 || s.omittedMinSeq < 0 || s.omittedMaxSeq < s.omittedMinSeq) {
                throw new CorruptFileException("invalid omitted record");
            }
        }
        int n = 0;
        for (Map.Entry<String, String> e : m.entrySet()) {
            if (!e.getKey().equals("e" + (++n))) {
                throw new CorruptFileException("unexpected summary key " + e.getKey());
            }
            s.entries.add(SummaryEntry.decodeValue(e.getValue()));
        }

        return s;
    }

    /** Writes the summary atomically: omitted (only if entries were folded), then e1..eN. */
    private void writeSummary(Summary s) throws IOException {
        Map<String, String> m = new LinkedHashMap<>();
        if (s.omitted > 0) {
            m.put("omitted", s.omitted + "|" + s.omittedMinSeq + "|" + s.omittedMaxSeq + "|"
                    + s.omittedFirstTime + "|" + s.omittedLastTime);
        }
        for (int i = 0; i < s.entries.size(); i++) {
            m.put("e" + (i + 1), s.entries.get(i).encodeValue());
        }
        ensureDir();
        writeAtomic(new File(dir, SUMMARY_FILE), encode(m));
    }

    // ---------------- condensing reports ----------------

    /**
     * Adds one condensed report to undelivered_summary.txt, folding the oldest entries past
     * SUMMARY_MAX_ENTRIES, and writes it atomically under the store lock (at most
     * lockTimeoutMs). A damaged summary is preserved as undelivered_summary.corrupt* and a new
     * one is started. A lock timeout or I/O failure is propagated. If preservation succeeds but
     * writing the replacement fails, the original damaged file remains available as a
     * .corrupt file.
     */
    public void appendSummary(Report r, String status, long condensedAt, long lockTimeoutMs)
            throws IOException {
        if (r == null || r.id == null || !ID.matcher(r.id).matches()) {
            throw new IllegalArgumentException("invalid report");
        }
        if (!lock(lockTimeoutMs)) {
            throw new IOException("store lock not acquired within " + lockTimeoutMs + " ms");
        }
        try {
            File f = new File(dir, SUMMARY_FILE);
            Summary s;
            if (!f.exists()) {
                s = new Summary();
            } else {
                try {
                    s = readSummary();
                } catch (CorruptFileException e) {
                    preserveCorrupt(f, SUMMARY_CORRUPT_FILE);
                    s = new Summary();
                }
            }
            s.entries.add(SummaryEntry.of(r, status, condensedAt));
            s.fold();
            writeSummary(s);
        } finally {
            unlock();
        }
    }
}
