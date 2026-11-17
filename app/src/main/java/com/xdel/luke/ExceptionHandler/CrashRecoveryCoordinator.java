package com.xdel.luke.ExceptionHandler;

import com.xdel.luke.ExceptionHandler.CrashReportRecovery.Outcome;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * R7 A7 Stage 2: runs CrashReportRecovery in the background (approved design sections 3 and 6
 * and the revised timeout, six-hour and robustness sections). One coordinator thread runs
 * housekeeping, eligibility checks and scheduling in order and never waits for SMTP; each send
 * runs on its own daemon worker. Housekeeping makes no report changes (holds, retention,
 * corrupt marking, delivered-report deletion, retry restores) for reports being sent; its
 * other steps only read them or remove old temporary files and old receipts. Shared files
 * change only under the store lock. At most MAX_ABANDONED abandoned workers per process.
 * Fields marked "coordinator thread" are used only on that thread; the rest are guarded by this.
 */
public final class CrashRecoveryCoordinator {

    public static final long SEND_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(180);
    public static final long RECHECK_MS = TimeUnit.HOURS.toMillis(6);
    public static final int MAX_ABANDONED = 2;
    public static final int MAX_REBUILDS = 3;
    static final String THREAD_NAME = "R7CrashRecovery";
    static final String WORKER_NAME = "R7CrashSend";

    private final CrashReportRecovery recovery;
    private final CrashReportRecovery.Env env;
    private final long sendTimeoutMs;
    private final long recheckMs;

    //IDs being sent; only the worker removes its own ID
    private final Set<String> inFlight = Collections.synchronizedSet(new HashSet<String>());

    //guarded by this
    private ScheduledThreadPoolExecutor executor;
    private int rebuilds;
    private boolean disabled;
    private boolean runQueued;

    //coordinator thread
    private String active;
    private final Set<String> abandoned = new HashSet<>();
    private final Set<String> deferred = new HashSet<>();
    private ScheduledFuture<?> timeout;
    private ScheduledFuture<?> recheck;

    public CrashRecoveryCoordinator(CrashReportRecovery recovery, CrashReportRecovery.Env env) {
        this(recovery, env, SEND_TIMEOUT_MS, RECHECK_MS);
    }

    /** Shorter timeouts are for JVM tests only. */
    CrashRecoveryCoordinator(CrashReportRecovery recovery, CrashReportRecovery.Env env,
            long sendTimeoutMs, long recheckMs) {
        if (recovery == null || env == null) {
            throw new IllegalArgumentException("recovery and env are required");
        }
        this.recovery = recovery;
        this.env = env;
        this.sendTimeoutMs = sendTimeoutMs;
        this.recheckMs = recheckMs;
    }

    /** Starts the first run; the caller makes sure this happens once per process. */
    public void start() {
        requestRun();
    }

    /**
     * Asks for one run as soon as possible. Requests made before that run starts merge into it,
     * so at most one run is waiting at any time. Safe from any thread.
     */
    public void requestRun() {
        synchronized (this) {
            if (runQueued) {
                return;
            }
            runQueued = true;
        }
        if (post(this::run, 0) == null) {
            synchronized (this) {
                runQueued = false;
            }
        }
    }

    /** Stops this coordinator for good; for JVM tests (the app relies on process death). */
    synchronized void stop() {
        disabled = true;
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    // ---------------- coordinator thread ----------------

    /** The 6-hour run: deferred reports and the digest get another chance, then a normal run. */
    private void periodicRun() {
        deferred.clear();
        run();
    }

    /**
     * One run: housekeeping, then at most one new send. Errors are logged and counted, and the
     * next 6-hour run is always scheduled, replacing any earlier one.
     */
    private void run() {
        synchronized (this) {
            runQueued = false;
        }
        try {
            Set<String> busy = busyIds();
            recovery.housekeeping(busy);
            if (active != null) {
                return; //one send at a time; its result starts the next run
            }
            if (abandoned.size() >= MAX_ABANDONED) {
                log("no new send: " + MAX_ABANDONED + " abandoned workers in this process");
                return;
            }
            if (!env.networkUp()) {
                log("no network; nothing sent");
                return;
            }
            Set<String> skip = new HashSet<>(busy);
            skip.addAll(deferred);
            long now = env.now();
            String id = recovery.nextEligible(now, skip);
            if (id != null) {
                startWorker(id);
            } else if (!skip.contains(CrashReportRecovery.DIGEST_KEY)
                    && recovery.digestReady(now)) {
                startWorker(CrashReportRecovery.DIGEST_KEY);
            }
        } catch (Throwable t) {
            recovery.error("coordinator run", t);
        } finally {
            scheduleRecheck();
        }
    }

    private void startWorker(String key) {
        inFlight.add(key);
        active = key;
        ScheduledFuture<?> limit = post(() -> timedOut(key), sendTimeoutMs);
        if (limit == null) {
            inFlight.remove(key);
            active = null;
            log("send " + key + " not started: its timeout could not be scheduled");
            return;
        }
        Thread w = new Thread(() -> work(key), WORKER_NAME);
        w.setDaemon(true);
        w.setUncaughtExceptionHandler((t, e) -> log("worker " + key + " died: " + e));
        try {
            w.start();
        } catch (Throwable t) {
            limit.cancel(false);
            inFlight.remove(key);
            active = null;
            recovery.error("starting worker " + key, t);
            return;
        }
        timeout = limit;
    }

    /** A worker's result: the next run starts, and an abandoned worker frees its place. */
    private void workerDone(String key, Outcome outcome) {
        if (outcome != Outcome.SENT && outcome != Outcome.FAILED) {
            deferred.add(key); //busy, not eligible or error: wait for the 6-hour run
        }
        if (abandoned.remove(key)) {
            log("abandoned worker for " + key + " returned: " + outcome);
        }
        if (key.equals(active)) {
            active = null;
            if (timeout != null) {
                timeout.cancel(false);
                timeout = null;
            }
        }
        requestRun();
    }

    private void timedOut(String key) {
        if (key.equals(active) && inFlight.contains(key)) {
            active = null;
            timeout = null;
            abandoned.add(key);
            log("send " + key + " exceeded " + sendTimeoutMs / 1000
                    + " s; worker abandoned (still blocked)");
            requestRun();
        }
    }

    private void scheduleRecheck() {
        if (recheck != null) {
            recheck.cancel(false);
        }
        recheck = post(this::periodicRun, recheckMs);
    }

    private Set<String> busyIds() {
        synchronized (inFlight) {
            return new HashSet<>(inFlight);
        }
    }

    // ---------------- worker thread ----------------

    /** Runs one send; whatever happens, frees its ID and reports back to the coordinator. */
    private void work(String key) {
        Outcome outcome = Outcome.ERROR;
        try {
            outcome = CrashReportRecovery.DIGEST_KEY.equals(key)
                    ? recovery.sendDigest() : recovery.sendReport(key);
        } catch (Throwable t) {
            recovery.error("worker " + key, t);
        } finally {
            inFlight.remove(key);
            Outcome result = outcome;
            if (post(() -> workerDone(key, result), 0) == null) {
                log("result of " + key + " not delivered: coordinator stopped");
            }
        }
    }

    // ---------------- coordinator thread management ----------------

    /**
     * Schedules a task on the coordinator thread, creating the thread if needed. A coordinator
     * that was shut down or rejects work is rebuilt, at most MAX_REBUILDS times per process;
     * after that recovery is disabled for this process. Null if nothing was scheduled.
     */
    private synchronized ScheduledFuture<?> post(Runnable task, long delayMs) {
        for (int i = 0; i < 2; i++) {
            if (!ensureExecutor()) {
                return null;
            }
            try {
                return executor.schedule(guarded(task), delayMs, TimeUnit.MILLISECONDS);
            } catch (RejectedExecutionException e) {
                executor.shutdownNow();
            }
        }
        return null;
    }

    private boolean ensureExecutor() {
        if (disabled) {
            return false;
        }
        if (executor != null && !executor.isShutdown()) {
            return true;
        }
        if (executor != null) {
            if (rebuilds >= MAX_REBUILDS) {
                disabled = true;
                log("R7 crash recovery disabled for this process");
                return false;
            }
            rebuilds++;
            log("coordinator rebuilt (" + rebuilds + " of " + MAX_REBUILDS + ")");
        }
        executor = new ScheduledThreadPoolExecutor(1, r -> {
            Thread t = new Thread(r, THREAD_NAME);
            t.setDaemon(true);
            t.setUncaughtExceptionHandler((th, e) -> coordinatorDied(e));
            return t;
        });
        executor.setRemoveOnCancelPolicy(true);
        return true;
    }

    /** Last resort: a coordinator thread that dies anyway is replaced through a rebuild. */
    private void coordinatorDied(Throwable e) {
        recovery.error("coordinator thread", e);
        synchronized (this) {
            if (executor != null) {
                executor.shutdownNow();
            }
            runQueued = false;
        }
        requestRun();
    }

    private Runnable guarded(Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Throwable t) {
                recovery.error("coordinator task", t);
            }
        };
    }

    private void log(String line) {
        try {
            env.log("R7 coordinator: " + line);
        } catch (Throwable ignored) {
            //logging must never break recovery
        }
    }
}
