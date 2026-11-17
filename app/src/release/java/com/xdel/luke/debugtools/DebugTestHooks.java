package com.xdel.luke.debugtools;

import android.app.Application;

/**
 * Release build: the R7 debug test tools do not exist. install() does nothing; there is no fake transport
 * and no crash trigger, and this class references no Debug-only class.
 */
public final class DebugTestHooks {

    private DebugTestHooks() {
    }

    public static void install(Application app) {
        //intentionally empty
    }
}
