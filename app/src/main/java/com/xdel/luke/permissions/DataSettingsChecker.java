package com.xdel.luke.permissions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class DataSettingsChecker {

    private static final String TAG = "DataSettingsChecker";

    public interface DataSettingsListener {
        void onBackgroundDataEnabled(boolean enabled);
        void onUnrestrictedAccessEnabled(boolean enabled);
        void onAllSettingsValid(boolean allEnabled);
    }

    /**
     * Check both mobile data background usage and unrestricted data access
     */
    public static DataSettingsStatus checkDataSettings(Context context) {
        DataSettingsStatus status = new DataSettingsStatus();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            // Check Data Saver status (unrestricted access)
            int restrictStatus = cm.getRestrictBackgroundStatus();

            switch (restrictStatus) {
                case ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED:
                    // Data Saver is OFF - both settings are effectively enabled
                    status.isBackgroundDataEnabled = true;
                    status.isUnrestrictedAccessEnabled = true;
                    status.dataSaverStatus = "Data Saver: OFF";
                    break;

                case ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED:
                    // Data Saver is ON, but app is whitelisted
                    status.isUnrestrictedAccessEnabled = true;
                    status.dataSaverStatus = "Unrestricted Access: ON";

                    // Still need to check background data separately
                    status.isBackgroundDataEnabled = checkBackgroundDataEnabled(context);
                    break;

                case ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED:
                    // Data Saver is ON and app is restricted
                    status.isUnrestrictedAccessEnabled = false;
                    status.isBackgroundDataEnabled = false; // Restricted means no background data
                    status.dataSaverStatus = "Data Access: RESTRICTED";
                    break;
            }
        } else {
            // Pre-Android N - only check background data
            status.isBackgroundDataEnabled = checkBackgroundDataEnabled(context);
            status.isUnrestrictedAccessEnabled = true; // Not applicable
            status.dataSaverStatus = "Data Saver not supported";
        }

        status.allSettingsEnabled = status.isBackgroundDataEnabled && status.isUnrestrictedAccessEnabled;

        Log.d(TAG, "Background Data: " + status.isBackgroundDataEnabled);
        Log.d(TAG, "Unrestricted Access: " + status.isUnrestrictedAccessEnabled);
        Log.d(TAG, "All Settings Valid: " + status.allSettingsEnabled);

        return status;
    }

    /**
     * Check if background data is enabled for the app
     * Note: This is a best-effort check as there's no direct API
     */
    private static boolean checkBackgroundDataEnabled(Context context) {
        // For Android 7+, background data restriction is handled by Data Saver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            int status = cm.getRestrictBackgroundStatus();
            // If not explicitly restricted, assume background data is enabled
            return status != ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED;
        }

        // For older versions, we assume it's enabled unless we can detect otherwise
        // There's no reliable way to check this on older Android versions
        return true;
    }

    /**
     * Prompt user to enable required data settings
     */
    public static void promptForDataSettings(Activity activity, DataSettingsStatus status) {
        if (status.allSettingsEnabled) {
            return; // All settings are already enabled
        }

        StringBuilder message = new StringBuilder();
        message.append("To ensure the app works properly, please enable the following settings:\n\n");

        if (!status.isBackgroundDataEnabled) {
            message.append("✗ Enable mobile data usage in background\n");
        } else {
            message.append("✓ Mobile data background usage: Enabled\n");
        }

        if (!status.isUnrestrictedAccessEnabled) {
            message.append("✗ Allow unrestricted data access when Data Saver is on\n");
        } else {
            message.append("✓ Unrestricted data access: Enabled\n");
        }

        message.append("\nYou'll be taken to the app settings where you can enable these options.");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Data Settings Required")
                .setMessage(message.toString())
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    openDataSettings(activity, status);
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(false)
                .show();
    }

    /**
     * Open appropriate settings based on what needs to be enabled
     */
    private static void openDataSettings(Activity activity, DataSettingsStatus status) {
        boolean opened = false;

        // Try app-specific data settings first
        if (!status.isUnrestrictedAccessEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            opened = openUnrestrictedDataSettings(activity);
        }

        // Fallback to app details settings
        if (!opened) {
            opened = openAppDataSettings(activity);
        }

        // Final fallback to main settings
        if (!opened) {
            openMainSettings(activity);
        }
    }

    /**
     * Open unrestricted data settings (Android 7+)
     */
    private static boolean openUnrestrictedDataSettings(Activity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "Unrestricted data settings not available");
            return false;
        }
    }

    /**
     * Open app-specific data usage settings
     */
    private static boolean openAppDataSettings(Activity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);

            Toast.makeText(activity,
                    "Look for 'Mobile data & Wi-Fi' or 'Data usage' settings",
                    Toast.LENGTH_LONG).show();
            return true;
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "App details settings not available");
            return false;
        }
    }

    /**
     * Fallback to main settings
     */
    private static void openMainSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        activity.startActivity(intent);

        Toast.makeText(activity,
                "Navigate to: Apps → " + activity.getString(activity.getApplicationInfo().labelRes) +
                        " → Mobile data & Wi-Fi → Enable all data options",
                Toast.LENGTH_LONG).show();
    }

    /**
     * Data class to hold settings status
     */
    public static class DataSettingsStatus {
        public boolean isBackgroundDataEnabled;
        public boolean isUnrestrictedAccessEnabled;
        public boolean allSettingsEnabled;
        public String dataSaverStatus;

        @Override
        public String toString() {
            return "DataSettingsStatus{" +
                    "backgroundData=" + isBackgroundDataEnabled +
                    ", unrestrictedAccess=" + isUnrestrictedAccessEnabled +
                    ", allEnabled=" + allSettingsEnabled +
                    ", dataSaverStatus='" + dataSaverStatus + '\'' +
                    '}';
        }
    }

    /**
     * BroadcastReceiver to monitor data settings changes
     */
    public static class DataSettingsReceiver extends BroadcastReceiver {
        private DataSettingsListener listener;

        public DataSettingsReceiver(DataSettingsListener listener) {
            this.listener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.ACTION_RESTRICT_BACKGROUND_CHANGED.equals(intent.getAction())) {
                DataSettingsStatus status = checkDataSettings(context);

                if (listener != null) {
                    listener.onBackgroundDataEnabled(status.isBackgroundDataEnabled);
                    listener.onUnrestrictedAccessEnabled(status.isUnrestrictedAccessEnabled);
                    listener.onAllSettingsValid(status.allSettingsEnabled);
                }
            }
        }

        public static IntentFilter getIntentFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.ACTION_RESTRICT_BACKGROUND_CHANGED);
            return filter;
        }
    }
}
