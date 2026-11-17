package com.xdel.luke.debugtools;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * DEBUG BUILD ONLY. R7 fake email transport for tests: records each message instead of sending it.
 * It never opens a network connection and never uses Mail or objGlobalMethods.
 * Same parameters as objGlobalMethods.sendEmails. Not used by any production code path in Stage 1.
 * Mode marker (optional, created via "adb shell run-as"): files/r7_debug/fake_transport_mode
 *   ACCEPT (default)   record the message and report it accepted
 *   FAIL               record the attempt and report failure
 *   ACCEPT_THEN_KILL   record the message, then end the process before returning
 *                      (simulates SMTP acceptance followed by process death)
 * Unknown mode text is treated as FAIL.
 * ACCEPT_THEN_KILL is one-shot: before the process killer runs, the mode file is atomically renamed to a unique
 * fake_transport_mode.used.<uuid> claim file and its text re-checked; the killer runs only if this call made the
 * claim and the claimed text is ACCEPT_THEN_KILL, so a restart or a concurrent caller cannot kill again.
 * Records: files/r7_debug/fake_outbox/<yyyyMMdd_HHmmss_SSS>_<seq>_<uuid>.txt, ending with END_MARKER.
 * A record is written to a unique temporary file, flushed to disk, then published with a hard link,
 * which fails instead of replacing an existing file; without hard-link support it is published with an
 * exclusive create. A record therefore never overwrites another, across processes and restarts.
 * R7 Stage 1.5: deliver(...) is the entry point used by FakeSmtpTransport for every Debug JavaMail send. It records
 * any number of attachments and reports an explicit Outcome; send(...) keeps its Stage 1 behaviour and format.
 */
public final class FakeEmailTransport {

    public static final String DEBUG_DIR = "r7_debug";
    public static final String MODE_FILE = "fake_transport_mode";
    public static final String OUTBOX_DIR = "fake_outbox";
    public static final String MODE_ACCEPT = "ACCEPT";
    public static final String MODE_FAIL = "FAIL";
    public static final String MODE_ACCEPT_THEN_KILL = "ACCEPT_THEN_KILL";
    /** Last line of every complete record; a record without it was cut short. */
    public static final String END_MARKER = "--- END OF R7 FAKE OUTBOX RECORD ---";
    /** Prefix of the unique claim file the mode file is renamed to when ACCEPT_THEN_KILL is used: .used.<uuid>. */
    public static final String USED_MODE_FILE = MODE_FILE + ".used";

    /** Result of deliver(...): only ACCEPTED means the message was recorded and accepted. */
    public enum Outcome {
        ACCEPTED,
        FAILED_MODE,
        ATTACHMENT_MISSING,
        RECORD_FAILED
    }

    private static final AtomicLong SEQ = new AtomicLong();

    private final File baseDir;
    private final Runnable processKiller;

    /** Device use: base directory files/r7_debug; ACCEPT_THEN_KILL ends this process. */
    public static FakeEmailTransport forContext(Context ctx) {
        return new FakeEmailTransport(new File(ctx.getApplicationContext().getFilesDir(), DEBUG_DIR), new Runnable() {
            @Override
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    /** Test use: explicit base directory and process killer. */
    public FakeEmailTransport(File baseDir, Runnable processKiller) {
        if (baseDir == null || processKiller == null) {
            throw new IllegalArgumentException("baseDir and processKiller are required");
        }
        this.baseDir = baseDir;
        this.processKiller = processKiller;
    }

    /**
     * @return true when the message was recorded and the mode reports it accepted; false otherwise.
     * A message that could not be recorded is reported as failed, so nothing counts as delivered without evidence.
     */
    public synchronized boolean send(String[] toArr, String subject, String body, String attachmentPath, File attachmentFile) {
        String mode = readMode();
        boolean recorded = writeRecord(mode, toArr, subject, body, attachmentPath, attachmentFile);
        if (MODE_FAIL.equals(mode)) {
            return false;
        }
        if (MODE_ACCEPT_THEN_KILL.equals(mode) && recorded && claimKillMode()) {
            processKiller.run();
        }
        return recorded;
    }

    /**
     * R7 Stage 1.5 entry point for FakeSmtpTransport: records the message with every attachment, in order.
     * fileNames.get(i) is the name given to attachments.get(i), or null when absent.
     * With failOnMissingAttachment, a missing attachment file is recorded as FAILED_ATTACHMENT_MISSING and reported
     * as ATTACHMENT_MISSING without running the process killer; without it, the record shows the attachment as absent
     * and delivery continues, as send(...) does.
     * @return ACCEPTED only when the record was published and the mode accepts it.
     */
    public synchronized Outcome deliver(String[] toArr, String subject, String body, List<File> attachments,
                                        List<String> fileNames, boolean failOnMissingAttachment) {
        String mode = readMode();
        List<File> files = attachments == null ? new ArrayList<File>() : attachments;
        boolean missing = false;
        for (File f : files) {
            if (f == null || !f.isFile()) {
                missing = true;
            }
        }
        if (missing && failOnMissingAttachment) {
            publish(buildDeliveryRecord(mode, "FAILED_ATTACHMENT_MISSING", toArr, subject, body, files, fileNames));
            return Outcome.ATTACHMENT_MISSING;
        }
        String result = MODE_FAIL.equals(mode) ? "FAILED" : (MODE_ACCEPT_THEN_KILL.equals(mode) ? "ACCEPTED_THEN_KILL" : "ACCEPTED");
        boolean recorded = publish(buildDeliveryRecord(mode, result, toArr, subject, body, files, fileNames));
        if (!recorded) {
            return Outcome.RECORD_FAILED;
        }
        if (MODE_FAIL.equals(mode)) {
            return Outcome.FAILED_MODE;
        }
        if (MODE_ACCEPT_THEN_KILL.equals(mode) && claimKillMode()) {
            processKiller.run();
        }
        return Outcome.ACCEPTED;
    }

    /** Current mode: ACCEPT when no mode file exists, FAIL when its text is unknown or unreadable. */
    public String readMode() {
        File modeFile = new File(baseDir, MODE_FILE);
        if (!modeFile.exists()) {
            return MODE_ACCEPT;
        }
        try {
            String text = readSmallText(modeFile).trim().toUpperCase(Locale.US);
            if (text.isEmpty() || MODE_ACCEPT.equals(text)) {
                return MODE_ACCEPT;
            }
            if (MODE_FAIL.equals(text) || MODE_ACCEPT_THEN_KILL.equals(text)) {
                return text;
            }
            return MODE_FAIL;
        } catch (IOException e) {
            return MODE_FAIL;
        }
    }

    /** Published records, oldest first by file name; temporary files are not included. */
    public List<File> listRecords() {
        File[] files = new File(baseDir, OUTBOX_DIR).listFiles();
        List<File> records = new ArrayList<>();
        if (files != null) {
            Arrays.sort(files);
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(".txt")) {
                    records.add(f);
                }
            }
        }
        return records;
    }

    /**
     * Claims and disarms ACCEPT_THEN_KILL for this invocation only: atomically renames the mode file to a unique
     * claim name, then checks the claimed text. True only if this call moved the file and it said ACCEPT_THEN_KILL;
     * the claimed file stays as the used marker. Other text is put back; a missing or unmovable file means no kill.
     */
    boolean claimKillMode() {
        Path modePath = new File(baseDir, MODE_FILE).toPath();
        Path claimed = new File(baseDir, USED_MODE_FILE + "." + UUID.randomUUID()).toPath();
        try {
            Files.move(modePath, claimed, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException | RuntimeException e) {
            return false;
        }
        String text;
        try {
            text = readSmallText(claimed.toFile()).trim().toUpperCase(Locale.US);
        } catch (IOException e) {
            text = "";
        }
        if (MODE_ACCEPT_THEN_KILL.equals(text)) {
            return true;
        }
        try {
            Files.move(claimed, modePath);
        } catch (IOException | RuntimeException ignored) {
            //a new mode file appeared meanwhile; leave the claimed file in place and do not kill
        }
        return false;
    }

    private boolean writeRecord(String mode, String[] toArr, String subject, String body, String attachmentPath, File attachmentFile) {
        return publish(buildRecord(mode, toArr, subject, body, attachmentPath, attachmentFile));
    }

    /** Publishes one complete record; false when it could not be written. Never overwrites an existing record. */
    private boolean publish(String recordText) {
        File outbox = new File(baseDir, OUTBOX_DIR);
        if (!outbox.isDirectory() && !outbox.mkdirs() && !outbox.isDirectory()) {
            return false;
        }
        byte[] content = recordText.getBytes(StandardCharsets.UTF_8);
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(new Date())
                + "_" + String.format(Locale.US, "%06d", SEQ.incrementAndGet() % 1000000L)
                + "_" + UUID.randomUUID();
        Path tmp = new File(outbox, name + ".tmp").toPath();
        Path record = new File(outbox, name + ".txt").toPath();

        try {
            writeNew(tmp, content);
        } catch (IOException e) {
            //writeNew removes only a file it created itself; an existing file is never touched
            return false;
        }
        try {
            Files.createLink(record, tmp);
            deleteOwnTemp(tmp);
            return true;
        } catch (FileAlreadyExistsException e) {
            deleteOwnTemp(tmp);
            return false;
        } catch (IOException | UnsupportedOperationException | SecurityException e) {
            //file system without hard links: publish with an exclusive create instead
            try {
                writeNew(record, content);
                return true;
            } catch (IOException e2) {
                return false;
            } finally {
                deleteOwnTemp(tmp);
            }
        }
    }

    private static String buildRecord(String mode, String[] toArr, String subject, String body, String attachmentPath, File attachmentFile) {
        File attachment = attachmentFile;
        if (attachment == null && attachmentPath != null && !attachmentPath.isEmpty()) {
            attachment = new File(attachmentPath);
        }
        boolean attachmentExists = attachment != null && attachment.isFile();
        String result = MODE_FAIL.equals(mode) ? "FAILED" : (MODE_ACCEPT_THEN_KILL.equals(mode) ? "ACCEPTED_THEN_KILL" : "ACCEPTED");

        StringBuilder sb = new StringBuilder();
        sb.append("time: ").append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).format(new Date())).append('\n');
        sb.append("mode: ").append(mode).append('\n');
        sb.append("result: ").append(result).append('\n');
        sb.append("to: ").append(Arrays.toString(toArr)).append('\n');
        sb.append("subject: ").append(subject).append('\n');
        sb.append("attachmentPath: ").append(attachmentPath).append('\n');
        sb.append("attachmentExists: ").append(attachmentExists).append('\n');
        sb.append("attachmentBytes: ").append(attachmentExists ? attachment.length() : -1).append('\n');
        sb.append("attachmentSha256: ").append(attachmentExists ? sha256(attachment) : "").append('\n');
        sb.append("bodyChars: ").append(body == null ? -1 : body.length()).append('\n');
        sb.append("body:\n").append(body).append('\n');
        sb.append(END_MARKER).append('\n');
        return sb.toString();
    }

    /**
     * Record for deliver(...): the Stage 1 header fields describe the first attachment (or none), followed by
     * attachmentCount and one indexed block per attachment, then the body and END_MARKER as in Stage 1.
     */
    private static String buildDeliveryRecord(String mode, String result, String[] toArr, String subject, String body,
                                              List<File> files, List<String> fileNames) {
        File first = files.isEmpty() ? null : files.get(0);
        boolean firstExists = first != null && first.isFile();

        StringBuilder sb = new StringBuilder();
        sb.append("time: ").append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).format(new Date())).append('\n');
        sb.append("mode: ").append(mode).append('\n');
        sb.append("result: ").append(result).append('\n');
        sb.append("to: ").append(Arrays.toString(toArr)).append('\n');
        sb.append("subject: ").append(subject).append('\n');
        sb.append("attachmentPath: ").append(first == null ? "" : first.getPath()).append('\n');
        sb.append("attachmentExists: ").append(firstExists).append('\n');
        sb.append("attachmentBytes: ").append(firstExists ? first.length() : -1).append('\n');
        sb.append("attachmentSha256: ").append(firstExists ? sha256(first) : "").append('\n');
        sb.append("attachmentCount: ").append(files.size()).append('\n');
        for (int i = 0; i < files.size(); i++) {
            File f = files.get(i);
            boolean exists = f != null && f.isFile();
            String name = fileNames != null && i < fileNames.size() ? fileNames.get(i) : null;
            sb.append("attachment.").append(i).append(".name: ").append(name).append('\n');
            sb.append("attachment.").append(i).append(".path: ").append(f == null ? null : f.getPath()).append('\n');
            sb.append("attachment.").append(i).append(".exists: ").append(exists).append('\n');
            sb.append("attachment.").append(i).append(".bytes: ").append(exists ? f.length() : -1).append('\n');
            sb.append("attachment.").append(i).append(".sha256: ").append(exists ? sha256(f) : "").append('\n');
        }
        sb.append("bodyChars: ").append(body == null ? -1 : body.length()).append('\n');
        sb.append("body:\n").append(body).append('\n');
        sb.append(END_MARKER).append('\n');
        return sb.toString();
    }

    /**
     * Creates the file exclusively (fails if it already exists), writes the content, flushes it to disk and closes it.
     * Success means all of these completed. If writing, flushing or closing fails, the file created by this call is
     * removed. If the exclusive create itself fails, nothing was created and nothing is deleted or modified.
     */
    private static void writeNew(Path path, byte[] content) throws IOException {
        //CREATE_NEW fails with FileAlreadyExistsException if the path exists; that existing file is never touched
        FileChannel ch = FileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        //from here on the file exists and was created exclusively by this call
        boolean ok = false;
        try {
            try (FileChannel channel = ch) {
                ByteBuffer buf = ByteBuffer.wrap(content);
                while (buf.hasRemaining()) {
                    channel.write(buf);
                }
                channel.force(true);
            }
            //reached only after write, force and close have all succeeded
            ok = true;
        } finally {
            if (!ok) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    //keep the original failure; an undeletable leftover is an unpublished .tmp or a record without END_MARKER
                }
            }
        }
    }

    /** Removes a temporary file that this transport created in the same call. */
    private static void deleteOwnTemp(Path tmp) {
        try {
            Files.deleteIfExists(tmp);
        } catch (IOException ignored) {
        }
    }

    private static String readSmallText(File f) throws IOException {
        InputStream in = new FileInputStream(f);
        try {
            byte[] buf = new byte[64];
            int n = in.read(buf);
            return n <= 0 ? "" : new String(buf, 0, n, StandardCharsets.UTF_8);
        } finally {
            in.close();
        }
    }

    private static String sha256(File f) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            InputStream in = new FileInputStream(f);
            try {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) {
                    md.update(buf, 0, n);
                }
            } finally {
                in.close();
            }
            StringBuilder hex = new StringBuilder();
            for (byte b : md.digest()) {
                hex.append(String.format(Locale.US, "%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return "unavailable";
        }
    }
}
