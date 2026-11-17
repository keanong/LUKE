package com.xdel.luke.Rooting;

import android.content.Context;

/** Release build: root simulation does not exist and can never be activated. */
final class RootSimulation {

    private RootSimulation() {
    }

    static boolean isRootSimulated(Context ctx) {
        return false;
    }

    static boolean isEnforceSimulated(Context ctx) {
        return false;
    }
}
