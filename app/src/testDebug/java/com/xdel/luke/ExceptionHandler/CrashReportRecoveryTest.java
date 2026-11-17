package com.xdel.luke.ExceptionHandler;

import com.xdel.luke.ExceptionHandler.CrashReportRecovery.Outcome;
import com.xdel.luke.ExceptionHandler.CrashReportStore.Report;
import com.xdel.luke.ExceptionHandler.CrashReportStore.Summary;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * R7 A7 Stage 2: JVM tests for CrashReportRecovery (approved design sections 3 to 6), run
 * synchronously through runOnce() and housekeeping(). Every test has its own store folder; the
 * Sender and Env are fakes, so nothing touches Android, the network or SMTP.
 */
public class CrashReportRecoveryTest {

    private static final long HOUR_MS = TimeUnit.HOURS.toMillis(1);
    private static final long DAY_MS = TimeUnit.DAYS.toMillis(1);
    private static final long T0 = 1700000000000L;
    private static final String PENDING = CrashReportStore.PENDING;
    private static final String HELD = CrashReportStore.HELD;
    private static final String RETRY = CrashReportStore.RETRY;

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private CrashReportStore store;
    private FakeEnv env;
    private FakeSender sender;
    private CrashReportRecovery recovery;

    @Before
    public void setUp() throws IOException {
        store = new CrashReportStore(new File(tmp.newFolder(), CrashReportStore.DIR_NAME));
        env = new FakeEnv();
        env.now = T0;
        sender = new FakeSender();
        recovery = new CrashReportRecovery(store, sender, env);
    }

    // A1
    @Test
    public void successfulDeliveryWritesReceiptAndRemovesPendingReport() throws IOException {
        pending("r1", 1L, T0, "");
        recovery.runOnce();

        assertEquals(1, sender.sent.size());
        Sent s = sender.sent.get(0);
        assertArrayEquals(new String[] {"it@xdel.com"}, s.to);
        assertEquals("Luke UNCAUGHT ERROR report from Tester. Version 5.0.7", s.subject);
        assertTrue(s.body, s.body.startsWith("Luke UNCAUGHT ERROR report\nReport ID: r1\n"
                + "Crash sequence: 1\nLast delivered sequence: none\n"));
        assertTrue(store.hasReceipt("r1"));
        assertFalse(store.file(PENDING, "r1").exists());
        assertEquals(1L, store.readLastSentOrZero());
    }

    // A2
    @Test
    public void failedDeliveryCountsAttemptAndWaitsTwentyFourHours() throws IOException {
        pending("r1", 1L, T0, "");
        sender.results.add(false);
        recovery.runOnce();
        assertEquals(1, sender.sent.size());
        Report r = read(PENDING, "r1");
        assertEquals(1, r.attempts);
        assertEquals(T0, r.lastAttemptAt);

        env.now = T0 + DAY_MS - 60000;
        recovery.runOnce();
        assertEquals("no retry before 24 hours", 1, sender.sent.size());

        //a successful send of another report grants r1 no extra attempt and no early retry
        pending("r2", 2L, env.now, "");
        recovery.runOnce();
        assertEquals(2, sender.sent.size());
        assertTrue(sender.sent.get(1).body.contains("Report ID: r2"));
        assertEquals(1, read(PENDING, "r1").attempts);

        env.now = T0 + DAY_MS;
        recovery.runOnce();
        assertEquals(3, sender.sent.size());
        assertTrue(sender.sent.get(2).body.contains("Report ID: r1"));
        assertTrue(store.hasReceipt("r1"));
    }

    // A3
    @Test
    public void noNetworkSendsNothingAndCountsNoAttempt() throws IOException {
        pending("r1", 1L, T0, "");
        env.network = false;
        recovery.runOnce();

        assertEquals(0, sender.sent.size());
        Report r = read(PENDING, "r1");
        assertEquals(0, r.attempts);
        assertEquals(0L, r.lastAttemptAt);
        assertTrue(env.logs.contains("R7 recovery: no network; nothing sent"));
    }

    // A4
    @Test
    public void maximumAttemptsMoveReportToHeld() throws IOException {
        Report r = pending("r1", 1L, T0, "");
        r.attempts = CrashReportRecovery.MAX_ATTEMPTS;
        r.lastAttemptAt = T0;
        store.rewrite(PENDING, r);
        env.now = T0 + DAY_MS;

        recovery.housekeeping(Collections.<String>emptySet());

        assertFalse(store.file(PENDING, "r1").exists());
        Report h = read(HELD, "r1");
        assertEquals(CrashReportRecovery.HELD_ATTEMPTS, h.heldReason);
        assertEquals(env.now, h.heldAt);
        assertNull(recovery.nextEligible(env.now, Collections.<String>emptySet()));
    }

    // A5
    @Test
    public void expiredWindowAndClockAnomalyMoveReportsToHeld() throws IOException {
        pending("r1", 1L, T0, "");
        Report r2 = pending("r2", 2L, T0 + HOUR_MS, "");
        r2.attempts = 1;
        r2.lastAttemptAt = T0 + 8 * DAY_MS; //later than the device clock below
        store.rewrite(PENDING, r2);
        env.now = T0 + 7 * DAY_MS; //r1's window ends exactly now; r2's has not ended

        recovery.housekeeping(Collections.<String>emptySet());

        Report h1 = read(HELD, "r1");
        assertEquals(CrashReportRecovery.HELD_EXPIRED, h1.heldReason);
        String status = CrashReportEmails.heldStatus(h1);
        assertTrue(status, status.startsWith("HELD (7-day window expired ")
                && status.contains(", detected "));
        assertEquals(CrashReportRecovery.HELD_CLOCK, read(HELD, "r2").heldReason);
        assertEquals(0, sender.sent.size());
    }

    // A6
    @Test
    public void corruptReportIsQuarantinedAndRetentionLimitsApply() throws IOException {
        for (int i = 1; i <= 4; i++) {
            pending("p" + i, i, T0 - 10000L + i * 1000L, ""); //crash times before the clock
        }
        Files.write(store.file(PENDING, "bad").toPath(),
                "not a report".getBytes(StandardCharsets.US_ASCII));
        for (int i = 1; i <= 4; i++) {
            held("h" + i, 10L + i, T0 - DAY_MS, T0 - DAY_MS + i * 1000L);
        }

        recovery.housekeeping(Collections.<String>emptySet());

        assertFalse(store.file(PENDING, "bad").exists());
        File corrupt = new File(store.dir(), CrashReportStore.CORRUPT + "bad" + CrashReportStore.REC_EXT);
        assertTrue(corrupt.isFile());
        //four pending: the oldest is held
        assertEquals(CrashReportRecovery.HELD_OVERFLOW, read(HELD, "p1").heldReason);
        assertEquals(3, store.list(PENDING).size());
        //now five held: the two oldest are condensed to the summary, then deleted
        assertFalse(store.file(HELD, "h1").exists());
        assertFalse(store.file(HELD, "h2").exists());
        assertTrue(store.file(HELD, "h3").exists());
        assertTrue(store.file(HELD, "h4").exists());
        assertEquals(3, store.list(HELD).size());
        Summary s = readSummary();
        assertEquals(2, s.entries.size());
        assertEquals("h1", s.entries.get(0).id);
        assertEquals("h2", s.entries.get(1).id);
        assertTrue(s.entries.get(0).status.startsWith("HELD (attempts exhausted, since "));

        recovery.runOnce();
        assertTrue(sender.sent.size() > 0);
        for (Sent sent : sender.sent) {
            assertFalse("the corrupt report must never be sent", sent.body.contains("Report ID: bad"));
        }
    }

    // A7
    @Test
    public void existingReceiptPreventsDuplicateDelivery() throws IOException {
        pending("r1", 1L, T0, "");
        store.writeReceipt("r1", 1L, T0);
        recovery.runOnce();
        assertEquals(0, sender.sent.size());
        assertFalse(store.file(PENDING, "r1").exists());

        //a worker that still finds the report sends nothing either
        pending("r2", 2L, T0, "");
        store.writeReceipt("r2", 2L, T0);
        assertEquals(Outcome.NOT_ELIGIBLE, recovery.sendReport("r2"));
        assertEquals(0, sender.sent.size());
    }

    // A8
    @Test
    public void retryStartsNewCycleAndIsResetOnlyOnce() throws IOException {
        retryFromHeld("r1");
        pending("r1", 9L, T0, ""); //an existing pending_r1 makes the move fail
        env.now = T0 + 2 * DAY_MS;

        recovery.housekeeping(Collections.<String>emptySet());
        Report reset = read(RETRY, "r1");
        assertEquals(0, reset.attempts);
        assertEquals(2, reset.cycle);
        assertEquals(T0 + 2 * DAY_MS, reset.cycleStart);
        assertEquals(T0 + 9 * DAY_MS, reset.expiresAt);
        assertEquals(0L, reset.lastAttemptAt);
        assertEquals("", reset.heldReason);

        env.now = T0 + 3 * DAY_MS;
        recovery.housekeeping(Collections.<String>emptySet());
        Report again = read(RETRY, "r1");
        assertEquals("reset only once", 2, again.cycle);
        assertEquals(T0 + 2 * DAY_MS, again.cycleStart);

        Files.delete(store.file(PENDING, "r1").toPath());
        recovery.housekeeping(Collections.<String>emptySet());
        assertFalse(store.file(RETRY, "r1").exists());
        Report restored = read(PENDING, "r1");
        assertEquals(2, restored.cycle);
        assertEquals(T0 + 2 * DAY_MS, restored.cycleStart);

        recovery.runOnce();
        assertEquals(1, sender.sent.size());
        assertTrue(store.hasReceipt("r1"));
    }

    // A9
    @Test
    public void retryOfBusyReportIsLeftUntouched() throws IOException {
        retryFromHeld("r1");
        File retry = store.file(RETRY, "r1");
        byte[] before = Files.readAllBytes(retry.toPath());
        env.now = T0 + 2 * DAY_MS;

        recovery.housekeeping(Collections.singleton("r1"));
        assertArrayEquals(before, Files.readAllBytes(retry.toPath()));
        assertFalse(store.file(PENDING, "r1").exists());

        recovery.housekeeping(Collections.<String>emptySet());
        assertFalse(retry.exists());
        assertEquals(2, read(PENDING, "r1").cycle);
    }

    // A10
    @Test
    public void attachmentIsKeptAndDigestAndRecoveryErrorsFollowTheRules() throws IOException {
        File log = tmp.newFile("crash_day_log.txt");
        Files.write(log.toPath(), "log line".getBytes(StandardCharsets.US_ASCII));
        pending("r1", 1L, T0 - 2, log.getAbsolutePath());
        pending("r2", 2L, T0 - 1, new File(tmp.getRoot(), "missing_log.txt").getAbsolutePath());
        Report h1 = held("h1", 5L, T0 - DAY_MS, T0 - DAY_MS);
        recovery.error("test", new RuntimeException("x"));
        sender.results.addAll(Arrays.asList(true, true, false)); //r1, r2, then the digest fails

        recovery.runOnce();
        assertEquals(3, sender.sent.size());
        Sent s1 = sender.sent.get(0);
        assertEquals(log.getAbsolutePath(), s1.attachment);
        assertTrue(log.isFile());
        assertEquals("log line", new String(Files.readAllBytes(log.toPath()), StandardCharsets.US_ASCII));
        assertTrue(s1.body.contains("Recovery errors since last report: 1\n"));
        Sent s2 = sender.sent.get(1);
        assertEquals("", s2.attachment);
        assertTrue(s2.body.contains("Log file not available\n"));
        assertFalse("errors are reported once", s2.body.contains("Recovery errors"));
        Sent d1 = sender.sent.get(2);
        assertEquals("Luke UNDELIVERED crash reports from Tester. Version 5.0.8", d1.subject);
        assertTrue(d1.body.contains("Digest ID: ") && d1.body.contains(" | h1 | "));
        assertEquals("", d1.attachment);

        env.now = T0 + 23 * HOUR_MS;
        recovery.runOnce();
        assertEquals("no digest attempt within 24 hours of a failed one", 3, sender.sent.size());

        env.now = T0 + DAY_MS;
        recovery.runOnce();
        assertEquals(4, sender.sent.size());
        assertTrue(sender.sent.get(3).subject.startsWith("Luke UNDELIVERED crash reports"));

        env.now = T0 + 7 * DAY_MS - 1;
        recovery.runOnce();
        assertEquals("no second digest within 7 days of a successful one", 4, sender.sent.size());

        env.now = T0 + 8 * DAY_MS;
        recovery.runOnce();
        assertEquals(5, sender.sent.size());
        assertEquals("the digest never changes report attempts", h1.attempts, read(HELD, "h1").attempts);
    }

    // ---------------- helpers ----------------

    private Report pending(String id, long seq, long time, String logPath) throws IOException {
        Report r = Report.newReport(id, seq, "", 0, time, "5.0.7", logPath,
                "java.lang.IllegalStateException: boom");
        store.savePending(r);
        return r;
    }

    private Report held(String id, long seq, long time, long heldAt) throws IOException {
        Report r = Report.newReport(id, seq, "", 0, time, "5.0.7", "",
                "java.lang.RuntimeException: held");
        r.attempts = CrashReportRecovery.MAX_ATTEMPTS;
        r.lastAttemptAt = time;
        r.heldAt = heldAt;
        r.heldReason = CrashReportRecovery.HELD_ATTEMPTS;
        store.savePending(r);
        store.move(store.file(PENDING, id), HELD);
        return r;
    }

    /** A held report renamed retry_<id>.rec, as a technician does with run-as. */
    private void retryFromHeld(String id) throws IOException {
        held(id, 1L, T0, T0 + DAY_MS);
        store.move(store.file(HELD, id), RETRY);
    }

    private Report read(String prefix, String id) throws IOException {
        return store.read(store.file(prefix, id));
    }

    private Summary readSummary() throws IOException {
        assertTrue(store.lock(1000));
        try {
            return store.readSummary();
        } finally {
            store.unlock();
        }
    }

    static final class Sent {
        final String[] to;
        final String subject;
        final String body;
        final String attachment;

        Sent(String[] to, String subject, String body, String attachment) {
            this.to = to;
            this.subject = subject;
            this.body = body;
            this.attachment = attachment;
        }
    }

    /** Records every send; returns the scripted results in order, then true. */
    static final class FakeSender implements CrashReportRecovery.Sender {
        final List<Boolean> results = new ArrayList<>();
        final List<Sent> sent = new ArrayList<>();

        @Override
        public boolean send(String[] to, String subject, String body, String attachmentPath) {
            sent.add(new Sent(to, subject, body, attachmentPath));
            return results.isEmpty() || results.remove(0);
        }
    }

    static final class FakeEnv implements CrashReportRecovery.Env {
        long now;
        boolean network = true;
        final List<String> logs = new ArrayList<>();
        final List<String> points = new ArrayList<>();

        @Override
        public long now() {
            return now;
        }

        @Override
        public boolean networkUp() {
            return network;
        }

        @Override
        public void log(String line) {
            logs.add(line);
        }

        @Override
        public String fullName() {
            return "Tester";
        }

        @Override
        public String currentVersion() {
            return "5.0.8";
        }

        @Override
        public void testPoint(String name) {
            points.add(name);
        }
    }
}
