package com.xdel.luke.ExceptionHandler;

import android.app.Application;
import android.util.Log;

import com.xdel.luke.BuildConfig;
import com.xdel.luke.objClasses.myApp;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * DEBUG BUILD ONLY. R7 A7 crash test points: W0, W1 and W3 in the crash handler (decision D3,
 * option b) and S4a, S5a and S6a in recovery. at(point) does nothing unless the marker
 * files/r7_debug/crash_kill_at names exactly this point. The marker is deleted first, then the
 * process is killed, so one marker fires at most once. The Release version is empty.
 */
public final class R7CrashTestPoints {

    static final String DEBUG_DIR = "r7_debug";
    static final String MARKER = "crash_kill_at";
    private static final String TAG = "LUKE_R7_TEST_POINT";
    private static final int MAX_MARKER_BYTES = 64;

    private R7CrashTestPoints() {
    }

    public static void at(String point) {
        if (!BuildConfig.DEBUG || point == null) {
            return;
        }
        try {
            Application app = myApp.getInstance();
            if (app == null) {
                return;
            }
            File marker = new File(new File(app.getFilesDir(), DEBUG_DIR), MARKER);
            if (!marker.isFile() || marker.length() > MAX_MARKER_BYTES) {
                return;
            }
            String wanted = new String(Files.readAllBytes(marker.toPath()),
                    StandardCharsets.US_ASCII).trim();
            if (!point.equals(wanted) || !marker.delete()) {
                return;
            }
            Log.w(TAG, "R7 test point " + point + " reached: killing the process");
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Throwable t) {
            Log.w(TAG, "R7 test point " + point + " ignored: " + t);
        }
    }
}
