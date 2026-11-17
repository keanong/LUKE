package com.xdel.luke.debugtools;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.xdel.luke.BuildConfig;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DEBUG BUILD ONLY. Entry point for the R7 debug test tools, called once from myApp.onCreate.
 * install() only registers an activity lifecycle callback: no threads, no network, no crash, and no change to
 * JavaMail. The Debug APK is distributed to users, so SMTP sending stays real; the fake SMTP transport is
 * registered only on the unit-test class path (src/testDebug/resources).
 *   R7DebugCrashTrigger  scheduled only from onActivityResumed, and only while LUKE's DefaultExceptionHandler
 *                        is active for the main thread; the trigger re-checks the handler before any crash.
 *   FakeEmailTransport   available to Debug-only code through fakeEmailTransport(); no production code path
 *                        uses it, and it never sends anything. The Release DebugTestHooks has no such method,
 *                        so Release code cannot reference it.
 */
public final class DebugTestHooks {

    private static final String TAG = "LUKE_R7_DEBUG_HOOKS";
    private static final AtomicBoolean INSTALLED = new AtomicBoolean(false);
    private static volatile Application application;
    private static volatile FakeEmailTransport fakeTransport;

    private DebugTestHooks() {
    }

    public static void install(Application app) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        if (app == null || !INSTALLED.compareAndSet(false, true)) {
            return;
        }
        try {
            app.registerActivityLifecycleCallbacks(new Callbacks());
            // saved only after registration succeeded
            application = app;
        } catch (Throwable t) {
            // allow a later explicit install() to retry
            INSTALLED.set(false);
            Log.w(TAG, "R7 debug test hooks not installed: " + t);
        }
    }

    /**
     * DEBUG ONLY. The fake transport for Debug-only callers, or null before install().
     * Creating it touches no file and sends nothing; it records a message only when send() is called.
     */
    public static FakeEmailTransport fakeEmailTransport() {
        Application app = application;
        if (app == null) {
            return null;
        }
        FakeEmailTransport transport = fakeTransport;
        if (transport == null) {
            synchronized (DebugTestHooks.class) {
                transport = fakeTransport;
                if (transport == null) {
                    transport = FakeEmailTransport.forContext(app);
                    fakeTransport = transport;
                }
            }
        }
        return transport;
    }

    private static final class Callbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityResumed(Activity activity) {
            try {
                //lifecycle callbacks run on the main thread; schedule only while LUKE's DefaultExceptionHandler is active
                if (R7DebugCrashTrigger.lukeHandlerActiveFor(Thread.currentThread())) {
                    R7DebugCrashTrigger.onActivityResumed(activity);
                }
            } catch (Throwable t) {
                Log.w(TAG, "R7 debug crash trigger scheduling failed: " + t);
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }
}
