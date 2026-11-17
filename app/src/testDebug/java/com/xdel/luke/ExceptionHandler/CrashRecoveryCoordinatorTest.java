package com.xdel.luke.ExceptionHandler;

import com.xdel.luke.ExceptionHandler.CrashReportStore.Report;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * R7 A7 Stage 2: JVM tests for CrashRecoveryCoordinator with short timeouts. Every wait is a
 * bounded latch or monitor wait, every blocked fake send is released in finally, and every
 * coordinator is stopped in finally. Report IDs are unique per test because SendLock keys are
 * shared by the whole JVM.
 */
public class CrashRecoveryCoordinatorTest {

    private static final long T0 = 1700000000000L;
    private static final long WAIT_MS = 5000;

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private CrashReportStore store;
    private SharedEnv env;

    @Before
    public void setUp() throws IOException {
        store = new CrashReportStore(new File(tmp.newFolder(), CrashReportStore.DIR_NAME));
        env = new SharedEnv();
    }

    // B1
    @Test
    public void eligibleReportIsSentOnWorkerThread() throws Exception {
        pending("b1r1", 1L, T0);
        GateSender sender = new GateSender(0);
        CrashRecoveryCoordinator c = coordinator(sender, 5000, 60000);
        try {
            c.start();
            assertTrue("send started", sender.awaitCalls(1, WAIT_MS));
            assertEquals(CrashRecoveryCoordinator.WORKER_NAME, sender.thread(0));
            assertTrue("delivered", env.awaitLog("report b1r1 delivered", WAIT_MS));
            assertTrue(store.hasReceipt("b1r1"));
            assertFalse(store.file(CrashReportStore.PENDING, "b1r1").exists());
        } finally {
            sender.release.countDown();
            c.stop();
        }
    }

    // B2
    @Test
    public void timedOutWorkerIsAbandonedAndItsReportNeverStartedTwice() throws Exception {
        pending("b2r1", 1L, T0 - 2); //crash times before the fixed clock
        pending("b2r2", 2L, T0 - 1);
        GateSender sender = new GateSender(1); //only the first send blocks
        CrashRecoveryCoordinator c = coordinator(sender, 300, 60000);
        try {
            c.start();
            assertTrue(sender.awaitCalls(1, WAIT_MS));
            assertEquals("b2r1", sender.id(0));
            assertTrue("abandoned", env.awaitLog("send b2r1 exceeded", WAIT_MS));
            assertTrue("next report sent meanwhile", sender.awaitCalls(2, WAIT_MS));
            assertEquals("b2r2", sender.id(1));
            assertTrue(env.awaitLog("report b2r2 delivered", WAIT_MS));
            Thread.sleep(500); //later runs happen; the blocked report must not be started again
            assertEquals(1, sender.count("b2r1"));

            sender.release.countDown(); //the stuck send now succeeds late
            assertTrue("late success", env.awaitLog("report b2r1 delivered", WAIT_MS));
            assertTrue(store.hasReceipt("b2r1"));
            assertEquals(1, sender.count("b2r1"));
        } finally {
            sender.release.countDown();
            c.stop();
        }
    }

    // B3
    @Test
    public void twoAbandonedWorkersStopFurtherSends() throws Exception {
        pending("b3r1", 1L, T0 - 3); //crash times before the fixed clock
        pending("b3r2", 2L, T0 - 2);
        pending("b3r3", 3L, T0 - 1);
        GateSender sender = new GateSender(Integer.MAX_VALUE); //every send blocks
        CrashRecoveryCoordinator c = coordinator(sender, 200, 60000);
        try {
            c.start();
            assertTrue(env.awaitLog("no new send: " + CrashRecoveryCoordinator.MAX_ABANDONED
                    + " abandoned workers", WAIT_MS));
            Thread.sleep(400);
            assertEquals(2, sender.calls());
            assertEquals(0, sender.count("b3r3"));
            assertTrue(store.file(CrashReportStore.PENDING, "b3r3").exists());
        } finally {
            sender.release.countDown();
            c.stop();
        }
    }

    // B4
    @Test
    public void stopPreventsFurtherCoordinatorRuns() throws Exception {
        GateSender sender = new GateSender(0);
        CrashRecoveryCoordinator c = coordinator(sender, 5000, 150);
        try {
            c.start();
            assertTrue("periodic runs happen", env.awaitNetworkChecks(2, WAIT_MS));
        } finally {
            c.stop();
        }
        Thread.sleep(300); //let a run already in progress finish
        int checks = env.networkChecks();
        pending("b4r1", 1L, T0);
        c.requestRun();
        Thread.sleep(600); //several recheck periods
        assertEquals("no run after stop()", checks, env.networkChecks());
        assertEquals(0, sender.calls());
        assertTrue(store.file(CrashReportStore.PENDING, "b4r1").exists());
    }

    // ---------------- helpers ----------------

    private CrashRecoveryCoordinator coordinator(GateSender sender, long sendTimeoutMs,
            long recheckMs) {
        CrashReportRecovery recovery = new CrashReportRecovery(store, sender, env);
        return new CrashRecoveryCoordinator(recovery, env, sendTimeoutMs, recheckMs);
    }

    private void pending(String id, long seq, long time) throws IOException {
        store.savePending(Report.newReport(id, seq, "", 0, time, "5.0.7", "",
                "java.lang.IllegalStateException: boom"));
    }

    /** The Report ID line of a crash-report body. */
    private static String reportId(String body) {
        for (String line : body.split("\n")) {
            if (line.startsWith("Report ID: ")) {
                return line.substring("Report ID: ".length());
            }
        }
        return "";
    }

    /** Records sends; the first blockCalls of them wait (at most 10 s) for release. */
    static final class GateSender implements CrashReportRecovery.Sender {
        final CountDownLatch release = new CountDownLatch(1);
        private final int blockCalls;
        private final List<String> ids = new ArrayList<>();
        private final List<String> threads = new ArrayList<>();

        GateSender(int blockCalls) {
            this.blockCalls = blockCalls;
        }

        @Override
        public boolean send(String[] to, String subject, String body, String attachmentPath) {
            int n;
            synchronized (this) {
                ids.add(reportId(body));
                threads.add(Thread.currentThread().getName());
                n = ids.size();
                notifyAll();
            }
            if (n <= blockCalls) {
                try {
                    release.await(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return true;
        }

        synchronized boolean awaitCalls(int n, long ms) throws InterruptedException {
            long deadline = System.currentTimeMillis() + ms;
            while (ids.size() < n) {
                long left = deadline - System.currentTimeMillis();
                if (left <= 0) {
                    return false;
                }
                wait(left);
            }
            return true;
        }

        synchronized int calls() {
            return ids.size();
        }

        synchronized String id(int i) {
            return ids.get(i);
        }

        synchronized String thread(int i) {
            return threads.get(i);
        }

        synchronized int count(String id) {
            int c = 0;
            for (String s : ids) {
                if (s.equals(id)) {
                    c++;
                }
            }
            return c;
        }
    }

    /** Thread-safe Env: fixed clock, network up, logs and network checks can be awaited. */
    static final class SharedEnv implements CrashReportRecovery.Env {
        private final List<String> logs = new ArrayList<>();
        private int networkChecks;

        @Override
        public long now() {
            return T0;
        }

        @Override
        public synchronized boolean networkUp() {
            networkChecks++;
            notifyAll();
            return true;
        }

        @Override
        public synchronized void log(String line) {
            logs.add(line);
            notifyAll();
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
            //no kill points in JVM tests
        }

        synchronized int networkChecks() {
            return networkChecks;
        }

        synchronized boolean awaitNetworkChecks(int n, long ms) throws InterruptedException {
            long deadline = System.currentTimeMillis() + ms;
            while (networkChecks < n) {
                long left = deadline - System.currentTimeMillis();
                if (left <= 0) {
                    return false;
                }
                wait(left);
            }
            return true;
        }

        synchronized boolean awaitLog(String part, long ms) throws InterruptedException {
            long deadline = System.currentTimeMillis() + ms;
            while (true) {
                for (String line : logs) {
                    if (line.contains(part)) {
                        return true;
                    }
                }
                long left = deadline - System.currentTimeMillis();
                if (left <= 0) {
                    return false;
                }
                wait(left);
            }
        }
    }
}
