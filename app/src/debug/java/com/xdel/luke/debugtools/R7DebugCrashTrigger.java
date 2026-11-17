package com.xdel.luke.debugtools;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.xdel.luke.ExceptionHandler.DefaultExceptionHandler;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DEBUG BUILD ONLY. One-shot R7 crash trigger for A7 testing. Inactive unless the marker
 * files/r7_debug/crash_trigger exists (created via "adb shell run-as"). Its text selects the mode:
 *   MAIN (default, also for an empty file)  uncaught exception on the main thread
 *   BACKGROUND                              uncaught exception on a new background thread
 *   MUTEX_HELD                              a background thread takes JobsHandler.mutex and keeps it,
 *                                           then the main thread throws (lock-held crash scenario).
 *                                           With the current crash handler this is expected to hang
 *                                           (known A7 issue); recovery needs a force-stop, only with approval.
 * LUKE's DefaultExceptionHandler is installed in MainActivity.onCreate. The trigger checks, at the moment
 * each crash would be thrown, that this handler is the process default handler and that the crashing thread
 * passes uncaught exceptions to it; otherwise it does nothing. The marker is deleted before any crash, so the
 * restart scheduled by the crash handler cannot crash again. Unknown marker text: marker removed, nothing happens.
 */
final class R7DebugCrashTrigger {

    static final String DEBUG_DIR = "r7_debug";
    static final String MARKER = "crash_trigger";
    static final String MODE_MAIN = "MAIN";
    static final String MODE_BACKGROUND = "BACKGROUND";
    static final String MODE_MUTEX_HELD = "MUTEX_HELD";

    private static final String TAG = "LUKE_R7_CRASH_TRIGGER";
    private static final long CHECK_DELAY_MS = 1500;
    private static final AtomicBoolean SCHEDULED = new AtomicBoolean(false);
    private static final AtomicBoolean FIRED = new AtomicBoolean(false);
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private R7DebugCrashTrigger() {
    }

    /** Called on the main thread after an activity resumes; schedules one delayed check, nothing else. */
    static void onActivityResumed(Activity activity) {
        if (activity == null || FIRED.get() || !SCHEDULED.compareAndSet(false, true)) {
            return;
        }
        final Context appContext = activity.getApplicationContext();
        MAIN_HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                SCHEDULED.set(false);
                checkAndFire(appContext);
            }
        }, CHECK_DELAY_MS);
    }

    /** True when an uncaught exception on thread t would reach LUKE's DefaultExceptionHandler. */
    static boolean lukeHandlerActiveFor(Thread t) {
        Thread.UncaughtExceptionHandler own = t.getUncaughtExceptionHandler();
        //a thread without its own handler reports its ThreadGroup, which passes the exception to the default handler
        boolean usesDefaultHandler = own == null || own == t.getThreadGroup();
        return usesDefaultHandler && Thread.getDefaultUncaughtExceptionHandler() instanceof DefaultExceptionHandler;
    }

    private static void checkAndFire(Context ctx) {
        String mode;
        try {
            File marker = new File(new File(ctx.getFilesDir(), DEBUG_DIR), MARKER);
            if (!marker.exists()) {
                return;
            }
            if (!lukeHandlerActiveFor(Thread.currentThread())) {
                log("R7 DEBUG crash trigger present but DefaultExceptionHandler is not active; waiting, marker kept.");
                return;
            }
            String requested = readMode(marker);
            //consume the marker first, so the restart scheduled by the crash handler cannot crash again
            if (!marker.delete()) {
                log("R7 DEBUG crash trigger marker could not be deleted; not crashing.");
                return;
            }
            if (requested == null) {
                log("R7 DEBUG crash trigger marker had unknown text; marker removed, no crash.");
                return;
            }
            if (!FIRED.compareAndSet(false, true)) {
                return;
            }
            mode = requested;
            log("R7 DEBUG crash trigger fired. Mode: " + mode);
        } catch (Throwable t) {
            Log.w(TAG, "R7 DEBUG crash trigger check failed: " + t);
            return;
        }
        fire(mode);
    }

    private static void fire(String mode) {
        if (MODE_BACKGROUND.equals(mode)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!lukeHandlerActiveFor(Thread.currentThread())) {
                        log("R7 DEBUG crash trigger BACKGROUND aborted: DefaultExceptionHandler is not active.");
                        return;
                    }
                    throw new R7DebugCrashException(MODE_BACKGROUND);
                }
            }, "R7DebugCrash-BACKGROUND").start();
        } else if (MODE_MUTEX_HELD.equals(mode)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!lukeHandlerActiveFor(Looper.getMainLooper().getThread())) {
                        log("R7 DEBUG crash trigger MUTEX_HELD aborted before taking the mutex: DefaultExceptionHandler is not active.");
                        return;
                    }
                    //only a mutex taken here may ever be released by this trigger
                    if (!JobsHandler.mutex.tryAcquire()) {
                        log("R7 DEBUG crash trigger MUTEX_HELD aborted: JobsHandler.mutex is in use, not acquired by the trigger; no crash.");
                        return;
                    }
                    log("R7 DEBUG crash trigger MUTEX_HELD: JobsHandler.mutex acquired by trigger thread (kept, not released).");
                    boolean posted = MAIN_HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!lukeHandlerActiveFor(Thread.currentThread())) {
                                //this trigger acquired the mutex above, so releasing it here is correct
                                JobsHandler.mutex.release();
                                log("R7 DEBUG crash trigger MUTEX_HELD aborted: DefaultExceptionHandler is not active; trigger's mutex released.");
                                return;
                            }
                            throw new R7DebugCrashException(MODE_MUTEX_HELD);
                        }
                    });
                    if (!posted) {
                        //the main-thread crash will never run; release the mutex this trigger acquired
                        JobsHandler.mutex.release();
                        log("R7 DEBUG crash trigger MUTEX_HELD aborted: main-thread crash could not be posted; trigger's mutex released, no crash.");
                    }
                }
            }, "R7DebugCrash-MUTEX").start();
        } else {
            //MAIN: checkAndFire runs on the main thread and verified the handler immediately before this point
            if (!lukeHandlerActiveFor(Thread.currentThread())) {
                log("R7 DEBUG crash trigger MAIN aborted: DefaultExceptionHandler is not active.");
                return;
            }
            throw new R7DebugCrashException(MODE_MAIN);
        }
    }

    /** MAIN for an empty file, the mode for known text, null for unknown text. */
    private static String readMode(File marker) throws IOException {
        InputStream in = new FileInputStream(marker);
        String text;
        try {
            byte[] buf = new byte[64];
            int n = in.read(buf);
            text = n <= 0 ? "" : new String(buf, 0, n, StandardCharsets.UTF_8);
        } finally {
            in.close();
        }
        text = text.trim().toUpperCase(Locale.US);
        if (text.isEmpty() || MODE_MAIN.equals(text)) {
            return MODE_MAIN;
        }
        if (MODE_BACKGROUND.equals(text) || MODE_MUTEX_HELD.equals(text)) {
            return text;
        }
        return null;
    }

    private static void log(String message) {
        Log.w(TAG, message);
        try {
            objGlobalMethods.writeToFile(message, "", "", "", "");
        } catch (Throwable ignored) {
        }
    }

    /** Deliberate test exception; the name makes test crashes unmistakable in logs and crash emails. */
    static final class R7DebugCrashException extends RuntimeException {
        R7DebugCrashException(String mode) {
            super("R7 DEBUG crash trigger (deliberate test crash), mode=" + mode);
        }
    }
}
