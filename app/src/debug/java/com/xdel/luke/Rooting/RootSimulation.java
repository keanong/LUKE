package com.xdel.luke.Rooting;

import android.content.Context;

import java.io.File;

/**
 * DEBUG BUILD ONLY. Simulated root detection for testing without a rooted device.
 * Disabled by default. Activated only by marker files in the app's private files directory,
 * which can be created only through "adb shell run-as", available for debuggable builds only.
 *   luke_root_sim          simulate a positive root detection result
 *   luke_root_sim_enforce  simulate enforcement, so a rooted or simulated result blocks the app
 */
final class RootSimulation {

    static final String ROOT_FILE = "luke_root_sim";
    static final String ENFORCE_FILE = "luke_root_sim_enforce";

    private RootSimulation() {
    }

    static boolean isRootSimulated(Context ctx) {
        return markerExists(ctx, ROOT_FILE);
    }

    static boolean isEnforceSimulated(Context ctx) {
        return markerExists(ctx, ENFORCE_FILE);
    }

    private static boolean markerExists(Context ctx, String name) {
        try {
            return new File(ctx.getFilesDir(), name).exists();
        } catch (Throwable t) {
            return false;
        }
    }
}
