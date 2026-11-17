package com.xdel.luke.Rooting;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;

/**
 * Keeps a blocked process on RootBlockedActivity.
 *
 * Registered only when RootGuard has blocked this process, so it does nothing while ROOT_ENFORCE=false.
 * Redirects from onActivityResumed only: every screen passes through onResume before the user can use it,
 * and by then its onCreate has completed, so finishing it is a normal lifecycle step.
 * Fails closed: when this callback returns, the resumed activity is finishing or the process is ending.
 * Covers activities only. Background network and email are guarded separately (R6/R7).
 */
public final class RootActivityGuard implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "RootGuard";

    private RootActivityGuard() {
    }

    /**
     * Registers the guard when RootGuard has blocked this process. Call after RootGuard.init().
     * Returns false only if the process is blocked and registration failed; the caller must then fail closed.
     */
    public static boolean register(Application app) {
        if (!RootGuard.isBlocked()) {
            return true;
        }
        try {
            app.registerActivityLifecycleCallbacks(new RootActivityGuard());
            log("RootActivityGuard registered. Activities will be redirected to the blocked screen.");
            return true;
        } catch (Throwable t) {
            log("RootActivityGuard registration failed: " + t);
            return false;
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity == null || activity instanceof RootBlockedActivity || isFinishing(activity)) {
            return;
        }
        boolean opened = false;
        try {
            Intent i = new Intent(activity, RootBlockedActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(i);
            opened = true;
        } catch (Throwable t) {
            log("RootActivityGuard could not open the blocked screen, closing the app's activities instead: " + t);
            showNotice(activity);
        }
        if (!opened) {
            try {
                activity.finishAffinity();
            } catch (Throwable t) {
                log("RootActivityGuard finishAffinity failed: " + t);
            }
        }
        try {
            activity.finish();
        } catch (Throwable t) {
            log("RootActivityGuard finish failed: " + t);
        }
        if (!isFinishing(activity)) {
            //last resort: a screen that cannot be closed must not stay usable
            log("RootActivityGuard could not close " + activity.getClass().getName() + ", ending the process.");
            try {
                android.os.Process.killProcess(android.os.Process.myPid());
            } catch (Throwable ignored) {
            }
        }
    }

    private static boolean isFinishing(Activity activity) {
        try {
            return activity.isFinishing();
        } catch (Throwable t) {
            return false;
        }
    }

    private static void showNotice(Activity activity) {
        try {
            Toast.makeText(activity.getApplicationContext(), R.string.root_blocked_message, Toast.LENGTH_LONG).show();
        } catch (Throwable ignored) {
        }
    }

    /** Local logging only, same approach as RootGuard; never throws. */
    private static void log(String message) {
        try {
            Log.w(TAG, message);
        } catch (Throwable ignored) {
        }
        try {
            objGlobalMethods.writeToFile(message, "", "", "", "");
        } catch (Throwable ignored) {
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
