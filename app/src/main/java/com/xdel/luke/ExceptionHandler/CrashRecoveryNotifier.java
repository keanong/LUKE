package com.xdel.luke.ExceptionHandler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.xdel.luke.MainActivity;
import com.xdel.luke.R;

/**
 * R7 A7 Stage 2: the reopen prompt after a crash (approved design C3). From API 29 a background
 * app cannot start its activity, so the crash handler posts one notification on the
 * LUKE_CRASH_RECOVERY channel; tapping it opens MainActivity. Nothing here asks for a
 * permission: if notifications are off or the channel is blocked, the prompt is skipped and
 * the reason is returned for the caller to log. Neither method throws.
 */
public final class CrashRecoveryNotifier {

    public static final String CHANNEL_ID = "LUKE_CRASH_RECOVERY";
    static final String CHANNEL_NAME = "Crash recovery";
    static final String CHANNEL_DESCRIPTION = "Shown after LUKE stops unexpectedly, to reopen it.";
    /** A tag of its own, so the ID cannot clash with LUKE's other notifications. */
    static final String NOTIFICATION_TAG = "R7_CRASH_RECOVERY";
    static final int NOTIFICATION_ID = 1;
    static final String TITLE = "LUKE stopped unexpectedly";
    static final String TEXT = "Tap to reopen LUKE.";

    private CrashRecoveryNotifier() {
    }

    /**
     * Creates the channel (API 26+). Android keeps the user's settings for an existing channel,
     * so later calls change nothing. Never throws.
     */
    public static void createChannel(Context ctx) {
        if (ctx == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        try {
            NotificationManager nm = ctx.getSystemService(NotificationManager.class);
            if (nm == null) {
                return;
            }
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            ch.setDescription(CHANNEL_DESCRIPTION);
            nm.createNotificationChannel(ch);
        } catch (Throwable ignored) {
            //the prompt is optional; the crash report is sent either way
        }
    }

    /**
     * API 29+: posts the reopen prompt and returns null, or returns why it was skipped. Below
     * API 29 it does nothing; the crash handler keeps the existing AlarmManager restart there.
     */
    public static String showReopenPrompt(Context ctx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return "below API 29";
        }
        if (ctx == null) {
            return "no context";
        }
        try {
            NotificationManager nm = ctx.getSystemService(NotificationManager.class);
            if (nm == null) {
                return "no notification service";
            }
            if (!nm.areNotificationsEnabled()) {
                return "notifications disabled for LUKE";
            }
            NotificationChannel ch = nm.getNotificationChannel(CHANNEL_ID);
            if (ch == null) {
                return "channel " + CHANNEL_ID + " missing";
            }
            if (ch.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                return "channel " + CHANNEL_ID + " blocked";
            }
            Intent open = new Intent(ctx, MainActivity.class);
            open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent tap = PendingIntent.getActivity(ctx, NOTIFICATION_ID, open,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            nm.notify(NOTIFICATION_TAG, NOTIFICATION_ID,
                    new NotificationCompat.Builder(ctx, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_xdel_noti2)
                            .setContentTitle(TITLE)
                            .setContentText(TEXT)
                            .setContentIntent(tap)
                            .setAutoCancel(true)
                            .build());
            return null;
        } catch (Throwable t) {
            return "prompt failed: " + t;
        }
    }
}
