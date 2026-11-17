package com.xdel.luke.ExceptionHandler;

import com.xdel.luke.ExceptionHandler.CrashReportStore.Report;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * R7 A7 Stage 2: recovery of saved crash reports (approved design sections 3, 4 and 6).
 * Plain Java: the send function, clock, network check, logger, name, version and debug test
 * points come from Sender and Env, so every rule runs in JVM unit tests. Threads, the
 * 180-second timeout and the 6-hour re-check are in CrashRecoveryCoordinator, and the email
 * text is built by CrashReportEmails. Nothing here takes JobsHandler.mutex, uses the email
 * queue, or deletes log or ZIP files.
 */
public final class CrashReportRecovery {

    public static final String[] RECIPIENTS = {"it@xdel.com"};
    public static final int MAX_ATTEMPTS = 7;
    public static final long RETRY_SPACING_MS = TimeUnit.HOURS.toMillis(24);
    public static final long DIGEST_INTERVAL_MS = TimeUnit.DAYS.toMillis(7);
    public static final long RECEIPT_MAX_AGE_MS = TimeUnit.DAYS.toMillis(30);
    public static final int MAX_PENDING = 3;
    public static final int MAX_HELD = 3;
    public static final String DIGEST_KEY = "digest";
    static final long LOCK_TIMEOUT_MS = 5000;
    static final String HELD_ATTEMPTS = "attempts exhausted";
    static final String HELD_EXPIRED = "7-day window expired";
    static final String HELD_CLOCK = "clock anomaly";
    static final String HELD_OVERFLOW = "retention overflow";

    /** Sends one email; true only if the SMTP server accepted it. In the app: sendEmails. */
    public interface Sender {
        boolean send(String[] to, String subject, String body, String attachmentPath);
    }

    /** Everything outside the store that recovery needs. */
    public interface Env {
        long now();

        boolean networkUp();

        void log(String line);

        /** FullName for subjects, read on a background thread just before a send (D4). */
        String fullName();

        String currentVersion();

        /** Debug kill points S4a, S5a and S6a; does nothing in Release. */
        void testPoint(String name);
    }

    public enum Outcome { SENT, FAILED, NOT_ELIGIBLE, BUSY, ERROR }

    /** What the email text needs to know about the store at one moment. */
    static final class Snapshot {
        long lastSent;
        final List<Report> pending = new ArrayList<>();
        final List<Report> held = new ArrayList<>();
        CrashReportStore.Summary summary;
        String summaryIssue = "";
    }

    private interface Step {
        void run() throws Exception;
    }

    private final CrashReportStore store;
    private final Sender sender;
    private final Env env;

    public CrashReportRecovery(CrashReportStore store, Sender sender, Env env) {
        if (store == null || sender == null || env == null) {
            throw new IllegalArgumentException("store, sender and env are required");
        }
        this.store = store;
        this.sender = sender;
        this.env = env;
    }

    CrashReportStore store() {
        return store;
    }

    // ---------------- retry policy (design section 3) ----------------

    /** Why a pending report must be held now, or null; checked in this order. */
    static String holdReason(Report r, long now) {
        if (now < r.cycleStart || now < r.lastAttemptAt) {
            return HELD_CLOCK;
        }
        if (r.attempts >= MAX_ATTEMPTS) {
            return HELD_ATTEMPTS;
        }
        if (now >= r.expiresAt) {
            return HELD_EXPIRED;
        }
        return null;
    }

    /** May start an attempt now: not to be held, and first attempt or 24 h since the last. */
    static boolean eligible(Report r, long now) {
        return holdReason(r, now) == null
                && (r.attempts == 0 || now - r.lastAttemptAt >= RETRY_SPACING_MS);
    }

    // ---------------- recovery run (design section 6) ----------------

    /** One complete synchronous run: housekeeping, eligible reports oldest first, the digest. */
    public void runOnce() {
        housekeeping(Collections.<String>emptySet());
        if (!env.networkUp()) {
            log("no network; nothing sent");
            return;
        }
        Set<String> tried = new HashSet<>();
        String id;
        while ((id = nextEligible(env.now(), tried)) != null) {
            tried.add(id);
            sendReport(id);
        }
        if (digestReady(env.now())) {
            sendDigest();
        }
    }

    /**
     * Steps 1-3 and 7, never sending: old temporary files, retry_ files, damaged and delivered
     * reports, holds, retention, the counter and old receipts. Reports in busy are being sent
     * and are left alone. Each step is isolated, so one failure does not skip the others.
     */
    public void housekeeping(Set<String> busy) {
        long now = env.now();
        step("temporary files", () -> logCount(store.cleanupTemps(now),
                "old temporary files deleted"));
        step("retry files", () -> restoreRetries(now, busy));
        step("pending reports", () -> checkReports(CrashReportStore.PENDING, busy));
        step("held reports", () -> checkReports(CrashReportStore.HELD, busy));
        step("holds", () -> applyHolds(now, busy));
        step("retention", () -> applyRetention(now, busy));
        step("sequence counter", this::raiseCounter);
        step("receipts", () -> logCount(store.pruneReceipts(now, RECEIPT_MAX_AGE_MS),
                "receipts older than 30 days deleted"));
    }

    /** The oldest pending report that may start an attempt now and is not in skip, or null. */
    public String nextEligible(long now, Set<String> skip) {
        for (Report r : readValid(CrashReportStore.PENDING)) {
            if (!skip.contains(r.id) && !store.hasReceipt(r.id) && eligible(r, now)) {
                return r.id;
            }
        }
        return null;
    }

    // ---------------- sending one report (design sections 3 and 5) ----------------

    /**
     * One attempt for one report, run by a worker thread. Takes the report's send lock,
     * re-reads and re-checks the report, and saves attempts and lastAttemptAt before sending.
     * After an accepted send: receipt, last_sent, recovery notes, then the report is deleted.
     */
    public Outcome sendReport(String id) {
        SendLock lock = SendLock.tryAcquire(store.dir(), id);
        if (lock == null) {
            log("report " + id + " skipped: its send lock is held");
            return Outcome.BUSY;
        }
        try {
            File f = store.file(CrashReportStore.PENDING, id);
            if (!f.isFile()) {
                return Outcome.NOT_ELIGIBLE;
            }
            Report r = readOrMarkCorrupt(f);
            if (r == null) {
                return Outcome.ERROR;
            }
            long now = env.now();
            if (store.hasReceipt(id) || !eligible(r, now)) {
                return Outcome.NOT_ELIGIBLE;
            }
            r.attempts++;
            r.lastAttemptAt = now;
            store.rewrite(CrashReportStore.PENDING, r); //counted before the send
            env.testPoint("S4a");
            long errors = readErrors();
            String attachment = CrashReportEmails.attachmentPath(r);
            String subject = CrashReportEmails.reportSubject(env.fullName(), r,
                    env.currentVersion());
            String body = CrashReportEmails.reportBody(r, snapshot(id), errors,
                    attachment != null);
            log("sending report " + id + " seq " + CrashReportStore.seqText(r.seq)
                    + ", attempt " + r.attempts + " of " + MAX_ATTEMPTS);
            String path = attachment == null ? "" : attachment;
            if (!sender.send(RECIPIENTS.clone(), subject, body, path)) {
                log("report " + id + " not sent; attempt " + r.attempts + " counted");
                return Outcome.FAILED;
            }
            env.testPoint("S5a");
            afterDelivery(r, errors);
            return Outcome.SENT;
        } catch (Throwable t) {
            error("send of report " + id, t);
            return Outcome.ERROR;
        } finally {
            lock.release();
        }
    }

    /**
     * Bookkeeping after the SMTP server accepted a report. A failure here is logged and
     * counted; the report then stays pending and may be sent again (accepted duplicate risk).
     */
    private void afterDelivery(Report r, long errors) {
        try {
            store.writeReceipt(r.id, r.seq, env.now());
            env.testPoint("S6a");
            if (r.seq != null && !store.updateLastSent(r.seq, LOCK_TIMEOUT_MS)) {
                log("last_sent not raised to " + r.seq);
            }
            if (!store.clearRecoveryNotes(r.seqNote, LOCK_TIMEOUT_MS)) {
                log("recovery notes of report " + r.id + " not cleared");
            }
            consumeErrors(errors);
            Files.deleteIfExists(store.file(CrashReportStore.PENDING, r.id).toPath());
            log("report " + r.id + " delivered; receipt written and report deleted");
        } catch (Throwable t) {
            error("bookkeeping after delivering report " + r.id, t);
        }
    }

    // ---------------- weekly digest (design section 5) ----------------

    /**
     * True if a digest is due (held or summary entries exist and 7 days since the last success)
     * and may start now (24 hours since the last attempt). A clock earlier than a saved time
     * resets that time to now and allows nothing.
     */
    public boolean digestReady(long now) {
        if (!hasUndelivered(snapshot(null))) {
            return false;
        }
        if (!store.lock(LOCK_TIMEOUT_MS)) {
            log("digest check skipped: store lock busy");
            return false;
        }
        try {
            DigestState d = readDigestState();
            if (now < d.lastAttemptAt || now < d.lastSentAt) {
                d.lastAttemptAt = Math.min(d.lastAttemptAt, now);
                d.lastSentAt = Math.min(d.lastSentAt, now);
                d.write(store);
                log("digest: clock earlier than its saved times; times reset, nothing sent");
                return false;
            }
            boolean due = d.lastSentAt == 0 || now - d.lastSentAt >= DIGEST_INTERVAL_MS;
            boolean spaced = d.lastAttemptAt == 0 || now - d.lastAttemptAt >= RETRY_SPACING_MS;
            return due && spaced;
        } catch (IOException e) {
            log("digest check failed: " + e);
            return false;
        } finally {
            store.unlock();
        }
    }

    /**
     * One digest attempt, run by a worker thread under the digest send lock. The attempt time
     * and Digest ID are saved before the send; if that save fails nothing is sent. It never
     * reads or writes report attempts.
     */
    public Outcome sendDigest() {
        SendLock lock = SendLock.tryAcquire(store.dir(), DIGEST_KEY);
        if (lock == null) {
            return Outcome.BUSY;
        }
        try {
            long now = env.now();
            if (!digestReady(now)) {
                return Outcome.NOT_ELIGIBLE;
            }
            String digestId = UUID.randomUUID().toString();
            long errors = saveDigestAttempt(now, digestId);
            if (errors < 0) {
                return Outcome.ERROR;
            }
            String subject = CrashReportEmails.digestSubject(env.fullName(),
                    env.currentVersion());
            String body = CrashReportEmails.digestBody(digestId, snapshot(null), errors);
            log("sending digest " + digestId);
            if (!sender.send(RECIPIENTS.clone(), subject, body, "")) {
                log("digest " + digestId + " not sent");
                return Outcome.FAILED;
            }
            markDigestSent(env.now(), errors);
            return Outcome.SENT;
        } catch (Throwable t) {
            error("digest", t);
            return Outcome.ERROR;
        } finally {
            lock.release();
        }
    }

    /** Saves lastAttemptAt and lastId; returns the unreported error count, or -1 on failure. */
    private long saveDigestAttempt(long now, String digestId) {
        if (!store.lock(LOCK_TIMEOUT_MS)) {
            log("digest not sent: store lock busy");
            return -1;
        }
        try {
            DigestState d = readDigestState();
            d.lastAttemptAt = now;
            d.lastId = digestId;
            d.write(store);
            return d.recoveryErrors;
        } catch (IOException e) {
            log("digest not sent: attempt not saved: " + e);
            return -1;
        } finally {
            store.unlock();
        }
    }

    private void markDigestSent(long at, long errors) {
        if (!store.lock(LOCK_TIMEOUT_MS)) {
            log("digest sent, but lastSentAt not saved: store lock busy");
            return;
        }
        try {
            DigestState d = readDigestState();
            d.lastSentAt = at;
            d.consumeErrors(errors);
            d.write(store);
            log("digest sent");
        } catch (IOException e) {
            log("digest sent, but lastSentAt not saved: " + e);
        } finally {
            store.unlock();
        }
    }

    private static boolean hasUndelivered(Snapshot s) {
        return !s.held.isEmpty() || (s.summary != null
                && (!s.summary.entries.isEmpty() || s.summary.omitted > 0));
    }

    // ---------------- recovery errors (digest.state) ----------------

    /** Logs an error and counts it for the next email; never throws. */
    public void error(String where, Throwable t) {
        log("error in " + where + ": " + t);
        if (!store.lock(LOCK_TIMEOUT_MS)) {
            return;
        }
        try {
            DigestState d = readDigestState();
            d.addError();
            d.write(store);
        } catch (Throwable e) {
            log("error count not saved: " + e);
        } finally {
            store.unlock();
        }
    }

    private long readErrors() {
        if (!store.lock(LOCK_TIMEOUT_MS)) {
            return 0;
        }
        try {
            return readDigestState().recoveryErrors;
        } catch (IOException e) {
            return 0;
        } finally {
            store.unlock();
        }
    }

    private void consumeErrors(long reported) throws IOException {
        if (reported <= 0 || !store.lock(LOCK_TIMEOUT_MS)) {
            return;
        }
        try {
            DigestState d = readDigestState();
            d.consumeErrors(reported);
            d.write(store);
        } finally {
            store.unlock();
        }
    }

    /** Reads digest.state and logs a damaged one; the store lock must be held. */
    private DigestState readDigestState() throws IOException {
        StringBuilder issue = new StringBuilder();
        DigestState d = DigestState.read(store, issue);
        if (issue.length() > 0) {
            log(issue.toString());
        }
        return d;
    }

    // ---------------- housekeeping steps (design sections 3 and 4) ----------------

    private void restoreRetries(long now, Set<String> busy) throws IOException {
        for (File f : store.list(CrashReportStore.RETRY)) {
            String id = CrashReportStore.idOf(f.getName());
            if (id != null && busy.contains(id)) {
                continue;
            }
            Report r = readOrMarkCorrupt(f);
            if (r == null) {
                continue;
            }
            if (store.hasReceipt(r.id)) {
                Files.deleteIfExists(f.toPath());
                log("retry of already delivered report " + r.id + " deleted");
                continue;
            }
            try {
                //reset only once: an already reset file is just moved again
                if (r.attempts != 0 || !r.heldReason.isEmpty()) {
                    r.attempts = 0;
                    r.cycle = r.cycle < Integer.MAX_VALUE ? r.cycle + 1 : r.cycle;
                    r.cycleStart = now;
                    r.expiresAt = now + CrashReportStore.WINDOW_MS;
                    r.lastAttemptAt = 0;
                    r.heldAt = 0;
                    r.heldReason = "";
                    store.rewrite(CrashReportStore.RETRY, r);
                }
                store.move(f, CrashReportStore.PENDING);
                log("manual retry of report " + r.id + ": cycle " + r.cycle
                        + ", new 7-day window");
            } catch (IOException e) {
                log("retry of report " + r.id + " not restored: " + e);
            }
        }
    }

    /** Marks damaged reports corrupt and deletes reports whose receipt proves delivery. */
    private void checkReports(String prefix, Set<String> busy) throws IOException {
        for (File f : store.list(prefix)) {
            String id = CrashReportStore.idOf(f.getName());
            if (id != null && busy.contains(id)) {
                continue;
            }
            Report r = readOrMarkCorrupt(f);
            if (r == null) {
                continue;
            }
            if (store.hasReceipt(r.id)) {
                Files.deleteIfExists(f.toPath());
                log("report " + r.id + " already delivered (receipt found); report deleted");
            } else if (!store.clearRecoveryNotes(r.seqNote, LOCK_TIMEOUT_MS)) {
                log("recovery notes of report " + r.id + " not cleared");
            }
        }
    }

    private void applyHolds(long now, Set<String> busy) throws IOException {
        for (Report r : readValid(CrashReportStore.PENDING)) {
            String reason = holdReason(r, now);
            if (reason != null && !busy.contains(r.id)) {
                hold(r, reason, now);
            }
        }
    }

    /** Pending above MAX_PENDING: oldest held. Held above MAX_HELD: oldest to the summary. */
    private void applyRetention(long now, Set<String> busy) throws IOException {
        List<Report> pending = readValid(CrashReportStore.PENDING);
        int excess = pending.size() - MAX_PENDING;
        for (int i = 0; i < pending.size() && excess > 0; i++) {
            if (!busy.contains(pending.get(i).id)) {
                hold(pending.get(i), HELD_OVERFLOW, now);
                excess--;
            }
        }
        List<Report> held = readValid(CrashReportStore.HELD);
        Collections.sort(held, BY_HELD_AT);
        excess = held.size() - MAX_HELD;
        for (int i = 0; i < held.size() && excess > 0; i++) {
            Report r = held.get(i);
            if (busy.contains(r.id)) {
                continue;
            }
            store.appendSummary(r, CrashReportEmails.heldStatus(r), now, LOCK_TIMEOUT_MS);
            Files.deleteIfExists(store.file(CrashReportStore.HELD, r.id).toPath());
            log("held report " + r.id + " condensed to the undelivered summary");
            excess--;
        }
    }

    private void hold(Report r, String reason, long now) throws IOException {
        r.heldAt = now;
        r.heldReason = reason;
        store.rewrite(CrashReportStore.PENDING, r);
        store.move(store.file(CrashReportStore.PENDING, r.id), CrashReportStore.HELD);
        log("report " + r.id + " seq " + CrashReportStore.seqText(r.seq) + " held: " + reason);
    }

    /** Raises the counter to the highest trusted sequence in the store; never lowers it. */
    private void raiseCounter() {
        CrashReportStore.SeqRecovery rec = store.recoverMaxSeq();
        if (!rec.issues.isEmpty()) {
            log("sequence evidence issues: " + rec.issues);
        }
        if (rec.max > 0 && !store.raiseCounterTo(rec.max, LOCK_TIMEOUT_MS)) {
            log("counter not raised to " + rec.max);
        }
    }

    private void logCount(int n, String what) {
        if (n > 0) {
            log(n + " " + what);
        }
    }

    // ---------------- reading reports ----------------

    /** Valid reports of one state, oldest crash first; unreadable ones are skipped. */
    private List<Report> readValid(String prefix) {
        List<Report> out = new ArrayList<>();
        for (File f : store.list(prefix)) {
            try {
                out.add(store.read(f));
            } catch (IOException e) {
                //reported and marked by checkReports
            }
        }
        Collections.sort(out, BY_TIME);
        return out;
    }

    /** The report, or null; a damaged file is renamed corrupt_<id>.rec and logged. */
    private Report readOrMarkCorrupt(File f) {
        try {
            return store.read(f);
        } catch (CrashReportStore.CorruptFileException e) {
            try {
                File to = store.markCorrupt(f);
                log(f.getName() + " damaged (" + e.getMessage() + "); kept as " + to.getName());
            } catch (IOException m) {
                log(f.getName() + " damaged and not renamed: " + m);
            }
            return null;
        } catch (IOException e) {
            log(f.getName() + " skipped: " + e);
            return null;
        }
    }

    /** The store as the email text needs it; excludeId is left out of the pending list. */
    Snapshot snapshot(String excludeId) {
        Snapshot s = new Snapshot();
        s.lastSent = store.readLastSentOrZero();
        for (Report r : readValid(CrashReportStore.PENDING)) {
            if (!r.id.equals(excludeId)) {
                s.pending.add(r);
            }
        }
        s.held.addAll(readValid(CrashReportStore.HELD));
        if (!store.lock(LOCK_TIMEOUT_MS)) {
            s.summaryIssue = "summary not read: store lock busy";
            return s;
        }
        try {
            s.summary = store.readSummary();
        } catch (NoSuchFileException e) {
            s.summary = null;
        } catch (IOException e) {
            s.summaryIssue = "summary unreadable: " + e.getClass().getSimpleName();
        } finally {
            store.unlock();
        }
        return s;
    }

    private static final Comparator<Report> BY_TIME = (a, b) -> {
        int c = Long.compare(a.time, b.time);
        return c != 0 ? c : a.id.compareTo(b.id);
    };

    private static final Comparator<Report> BY_HELD_AT = (a, b) -> {
        int c = Long.compare(a.heldAt, b.heldAt);
        return c != 0 ? c : BY_TIME.compare(a, b);
    };

    private void step(String name, Step s) {
        try {
            s.run();
        } catch (Throwable t) {
            error(name, t);
        }
    }

    private void log(String line) {
        try {
            env.log("R7 recovery: " + line);
        } catch (Throwable ignored) {
            //logging must never break recovery
        }
    }
}
