package com.xdel.luke.Rooting;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.scottyab.rootbeer.RootBeer;
import com.xdel.luke.BuildConfig;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

/**
 * Root detection and enforcement decision for LUKE.
 *
 * The decision is calculated once per process, normally from myApp.onCreate(), and is never
 * re-evaluated afterwards. A fixed decision guarantees that an operation such as a job sync cannot
 * switch from allowed to blocked midway, which could otherwise send JobsHandler down a delete path.
 *
 * Release builds: blocked = rooted AND BuildConfig.ROOT_ENFORCE (false for the logging-only pilot).
 * Debug builds: root is only warned about, unless the debug-only RootSimulation enforce switch is on.
 * The release source set version of RootSimulation always returns false.
 *
 * isRooted() and isBlocked() are only meaningful once the decision exists. Callers must call
 * init(Context) first or check isDecided(). blockOperation() handles the undetermined state itself.
 *
 * Local root detection can be bypassed, for example by Magisk hiding or runtime hooking.
 * It is not a substitute for server-side attestation.
 */
public final class RootGuard {

    private static final String TAG = "RootGuard";
    private static final Object LOCK = new Object();

    /** Immutable decision for this process. Assigned once, never replaced. Null until a context is available. */
    private static volatile Decision decision;

    private static volatile boolean blockedOperationLogged = false;
    private static volatile boolean undeterminedLogged = false;

    private RootGuard() {
    }

    private static final class Decision {
        final boolean rooted;
        final boolean blocked;

        Decision(boolean rooted, boolean blocked) {
            this.rooted = rooted;
            this.blocked = blocked;
        }
    }

    /** Calculates and caches the decision for this process. Safe to call more than once. */
    public static void init(Context ctx) {
        getDecision(ctx);
    }

    /** True once the final decision for this process has been calculated and cached. */
    public static boolean isDecided() {
        return decision != null;
    }

    /**
     * True if root was detected, or simulated in a debug build. Does not mean the app is blocked.
     * Returns false while undetermined, so call init(Context) first or check isDecided().
     */
    public static boolean isRooted() {
        Decision d = getDecision(null);
        return d != null && d.rooted;
    }

    /**
     * True if root was detected and enforcement is active for this build.
     * Returns false while undetermined, so call init(Context) first or check isDecided().
     */
    public static boolean isBlocked() {
        Decision d = getDecision(null);
        return d != null && d.blocked;
    }

    /**
     * Guard for network and email operations. Returns true if the operation must not run.
     * The blocking result never depends on whether logging succeeds.
     * While the decision is undetermined, only this call is blocked and nothing is cached.
     */
    public static boolean blockOperation(String operation, String detail) {
        Decision d = getDecision(null);
        if (d == null) {
            if (!undeterminedLogged) {
                undeterminedLogged = true;
                safeLog("RootGuard: decision not available yet, " + operation + " deferred for this call only.");
            }
            return true;
        }
        if (!d.blocked) {
            return false;
        }
        if (!blockedOperationLogged) {
            blockedOperationLogged = true;
            String what = detail != null ? operation + " (" + detail + ")" : operation;
            safeLog("RootGuard blocked " + what + ". Further blocked operations in this process are not logged.");
        }
        return true;
    }

    /** Returns the cached decision, calculating it if possible. Returns null while no context exists. */
    private static Decision getDecision(Context ctx) {
        Decision d = decision;
        if (d != null) {
            return d;
        }
        synchronized (LOCK) {
            if (decision != null) {
                return decision;
            }
            Context appCtx = null;
            try {
                if (ctx != null) {
                    appCtx = ctx.getApplicationContext() != null ? ctx.getApplicationContext() : ctx;
                } else {
                    appCtx = myApp.getApplicationCtx();
                }
            } catch (Throwable t) {
                appCtx = null;
            }
            if (appCtx == null) {
                //no context yet: nothing is cached, so the real decision is still made later
                return null;
            }
            Decision result;
            try {
                result = evaluate(appCtx);
            } catch (Throwable t) {
                //fail open on any unexpected error, so myApp.onCreate can never crash here
                result = new Decision(false, false);
                safeLog("Exception at RootGuard getDecision. Root detection skipped for this process. Exception: " + t);
            }
            decision = result;
            return decision;
        }
    }

    private static Decision evaluate(Context ctx) {
        long start = SystemClock.elapsedRealtime();
        boolean detected = false;
        String indicators = "";
        try {
            RootBeer rootBeer = new RootBeer(ctx);
            rootBeer.setLogging(BuildConfig.DEBUG);
            //isRooted() excludes the busybox check, which causes false positives on some OEM builds
            detected = rootBeer.isRooted();
            if (detected) {
                indicators = describeIndicators(rootBeer);
            }
        } catch (Throwable t) {
            //fail open: an unexpected detector error must not lock out legitimate drivers
            detected = false;
            safeLog("Exception at RootGuard evaluate. Root detection skipped for this process. Exception: " + t);
        }

        boolean simulated = false;
        boolean enforce = false;
        try {
            if (BuildConfig.DEBUG) {
                simulated = RootSimulation.isRootSimulated(ctx);
                enforce = RootSimulation.isEnforceSimulated(ctx);
            } else {
                enforce = BuildConfig.ROOT_ENFORCE;
            }
        } catch (Throwable t) {
            simulated = false;
            enforce = false;
            safeLog("Exception at RootGuard evaluate reading enforcement settings. Enforcement off for this process. Exception: " + t);
        }

        boolean rooted = detected || simulated;
        boolean blocked = rooted && enforce;
        long elapsed = SystemClock.elapsedRealtime() - start;

        if (rooted) {
            StringBuilder msg = new StringBuilder("RootGuard: root indicators detected");
            if (detected) {
                msg.append(" [").append(indicators).append("]");
            }
            if (simulated) {
                msg.append(" [SIMULATED, debug build]");
            }
            if (blocked) {
                msg.append(". Enforcement ON, app blocked");
            } else if (BuildConfig.DEBUG) {
                msg.append(". Enforcement OFF, debug build warns only");
            } else {
                msg.append(". Enforcement OFF, logging-only pilot");
            }
            msg.append(". Check took ").append(elapsed).append(" ms.");
            safeLog(msg.toString());
        } else {
            try {
                Log.i(TAG, "RootGuard: no root indicators detected. Check took " + elapsed + " ms.");
            } catch (Throwable ignored) {
            }
        }
        return new Decision(rooted, blocked);
    }

    /** Diagnostic only. Each check is isolated so a failing check cannot affect the decision or crash the app. */
    private static String describeIndicators(RootBeer rb) {
        StringBuilder sb = new StringBuilder();
        try {
            if (rb.detectRootManagementApps()) sb.append("rootManagementApps,");
        } catch (Throwable ignored) {
        }
        try {
            if (rb.detectPotentiallyDangerousApps()) sb.append("dangerousApps,");
        } catch (Throwable ignored) {
        }
        try {
            if (rb.checkForSuBinary()) sb.append("suBinary,");
        } catch (Throwable ignored) {
        }
        try {
            if (rb.checkForDangerousProps()) sb.append("dangerousProps,");
        } catch (Throwable ignored) {
        }
        try {
            if (rb.checkForRWPaths()) sb.append("rwSystemPaths,");
        } catch (Throwable ignored) {
        }
        try {
            if (rb.detectTestKeys()) sb.append("testKeys,");
        } catch (Throwable ignored) {
        }
        try {
            if (rb.checkSuExists()) sb.append("whichSu,");
        } catch (Throwable ignored) {
        }
        try {
            if (rb.checkForRootNative()) sb.append("native,");
        } catch (Throwable ignored) {
        }
        try {
            if (rb.checkForMagiskBinary()) sb.append("magiskBinary,");
        } catch (Throwable ignored) {
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Local, non-network logging. objGlobalMethods.writeToFile only appends to the daily log file on
     * external storage, but it can throw (for example from its own catch block), so every failure is
     * contained here and never reaches the caller.
     */
    private static void safeLog(String message) {
        try {
            Log.w(TAG, message);
        } catch (Throwable ignored) {
        }
        try {
            objGlobalMethods.writeToFile(message, "", "", "", "");
        } catch (Throwable ignored) {
        }
    }
}
