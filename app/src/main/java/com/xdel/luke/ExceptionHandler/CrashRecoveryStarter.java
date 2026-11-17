package com.xdel.luke.ExceptionHandler;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * R7 A7 Stage 2: Android wiring for crash-report recovery. start() runs once per process and
 * only starts a short-lived bootstrap thread; that thread builds the store in
 * getNoBackupFilesDir()/r7_crash, the Env and the Sender, and starts one
 * CrashRecoveryCoordinator. No disk or network work runs on the main thread, and start()
 * never throws, so a failure here cannot stop app startup.
 */
public final class CrashRecoveryStarter {

    static final String TAG = "LUKE_R7_RECOVERY";
    static final String BOOT_THREAD = "R7CrashRecoveryStart";
    private static final AtomicBoolean STARTED = new AtomicBoolean(false);

    /**
     * The existing transport, unchanged: objGlobalMethods.sendEmails with myFile null, so the
     * attached log is never deleted. No credentials or SMTP settings are handled here.
     */
    static final CrashReportRecovery.Sender SENDER = (to, subject, body, attachmentPath) ->
            objGlobalMethods.sendEmails(to, subject, body, attachmentPath, null);

    private CrashRecoveryStarter() {
    }

    /** Starts recovery once per process; later calls do nothing. Never throws. */
    public static void start(Application app) {
        if (app == null || !STARTED.compareAndSet(false, true)) {
            return;
        }
        try {
            Context ctx = app.getApplicationContext();
            Thread boot = new Thread(() -> bootstrap(ctx), BOOT_THREAD);
            boot.setDaemon(true);
            boot.start();
        } catch (Throwable t) {
            Log.w(TAG, "R7 crash recovery not started: " + t);
        }
    }

    /** Runs on the bootstrap thread: the only place the store directory is looked up. */
    private static void bootstrap(Context ctx) {
        try {
            CrashReportStore store = new CrashReportStore(
                    CrashReportStore.storeDir(ctx.getNoBackupFilesDir()));
            CrashReportRecovery.Env env = new AndroidEnv(ctx);
            CrashReportRecovery recovery = new CrashReportRecovery(store, SENDER, env);
            new CrashRecoveryCoordinator(recovery, env).start();
        } catch (Throwable t) {
            Log.w(TAG, "R7 crash recovery not started: " + t);
        }
    }

    /** Clock, network, log, name and version as the existing crash email uses them. */
    static final class AndroidEnv implements CrashReportRecovery.Env {

        private final Context ctx;

        AndroidEnv(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public long now() {
            return System.currentTimeMillis();
        }

        /** The existing ConnectionDetector check (it reports true if the check itself fails). */
        @Override
        public boolean networkUp() {
            return new ConnectionDetector(ctx).isConnectingToInternet();
        }

        /** The existing daily log (<ddMMyyyy>_log.txt) and logcat. */
        @Override
        public void log(String line) {
            Log.i(TAG, line);
            objGlobalMethods.writeToFile(line, "", "", "", "");
        }

        /** FullName, else the username, as getLogFiles() uses them; read just before a send. */
        @Override
        public String fullName() {
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            String name = objGlobalMethods.getGCMPrefs(g, "FullName");
            if (name == null || name.isEmpty()) {
                name = objGlobalMethods.getGCMPrefs(g, "username");
            }
            return name == null ? "" : name;
        }

        @Override
        public String currentVersion() {
            try {
                String v = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0)
                        .versionName;
                return v == null ? "" : v;
            } catch (Exception e) {
                return "";
            }
        }

        /** Debug kill points S4a, S5a and S6a; R7CrashTestPoints does nothing in Release. */
        @Override
        public void testPoint(String name) {
            R7CrashTestPoints.at(name);
        }
    }
}
