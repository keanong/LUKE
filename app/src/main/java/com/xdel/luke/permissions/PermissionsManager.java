package com.xdel.luke.permissions;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.List;

public class PermissionsManager {

    private Activity activity;
    private static final int PERMISSIONS_REQUEST_CODE = 1001;
    private static final int INSTALL_APPS_REQUEST_CODE = 1002;
    private static final int BATTERY_OPTIMIZATION_REQUEST_CODE = 1003;
    private static final int LOCATION_SETTINGS_REQUEST_CODE = 1004;
    private static final int BLUETOOTH_ENABLE_REQUEST_CODE = 1005;
    private static final int DATA_SAVER_REQUEST_CODE = 1006;

    // Define all required permissions
    private static final String[] REQUIRED_PERMISSIONS = {
            /*Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG*/
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE
    };

    // Background location permission (Android 10+)
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static final String BACKGROUND_LOCATION_PERMISSION = Manifest.permission.ACCESS_BACKGROUND_LOCATION;

    // Android 13+ permissions
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private static final String[] ANDROID_13_PERMISSIONS = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.NEARBY_WIFI_DEVICES
    };

    public PermissionsManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * Check if all required permissions and system settings are configured
     */
    public boolean areAllPermissionsAndSettingsConfigured() {
        List<String> missingPermissions = getMissingPermissions();
        /*return missingPermissions.isEmpty() &&
                canInstallUnknownApps() &&
                isAirplaneModeOff() &&
                isBatteryOptimizationDisabled() &&
                isLocationModeHighAccuracy() &&
                isBluetoothEnabled() &&
                isUnrestrictedDataUsageEnabled();*/
        return missingPermissions.isEmpty();
    }

    /**
     * Check if all required permissions are granted (original method)
     */
    public boolean areAllPermissionsGranted() {
        List<String> missingPermissions = getMissingPermissions();
        return missingPermissions.isEmpty() && canInstallUnknownApps();
    }

    /**
     * Get list of missing permissions
     */
    public List<String> getMissingPermissions() {
        List<String> missingPermissions = new ArrayList<>();

        // Check standard permissions
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        // Check background location permission separately (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!isBackgroundLocationGranted()) {
                missingPermissions.add(BACKGROUND_LOCATION_PERMISSION);
            }
        }

        // Check Android 13+ permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            for (String permission : ANDROID_13_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(activity, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.add(permission);
                }
            }
        }

        return missingPermissions;
    }

    /**
     * Check if airplane mode is off
     */
    public boolean isAirplaneModeOff() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return Settings.Global.getInt(activity.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, 0) == 0;
            } else {
                return Settings.System.getInt(activity.getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON, 0) == 0;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            return true; // Assume it's off if we can't check
        }
    }

    /**
     * Check if battery optimization is disabled for this app
     */
    public boolean isBatteryOptimizationDisabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
                return powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                return false;
            }
        }
        return true; // Not applicable for older versions
    }

    /**
     * Check if unrestricted data usage is enabled for this app
     * This checks if the app is exempt from data saver restrictions
     */
    public boolean isUnrestrictedDataUsageEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    // Check if data saver is enabled
                    int restrictBackgroundStatus = connectivityManager.getRestrictBackgroundStatus();

                    // If data saver is not enabled, return true
                    if (restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED) {
                        return true;
                    }

                    // If data saver is enabled, check if this app is whitelisted
                    return restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED;
                }
                return true;
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                return true; // Assume it's enabled if we can't check
            }
        }
        return true; // Not applicable for older versions
    }

    /**
     * Check if Bluetooth is enabled
     */
    public boolean isBluetoothEnabled() {
        try {
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager != null) {
                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                if (bluetoothAdapter != null) {
                    return bluetoothAdapter.isEnabled();
                }
            }
            return false;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            return false;
        }
    }
    /**
     * Check if location mode is set to high accuracy
     */
    public boolean isLocationModeHighAccuracy() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int locationMode = Settings.Secure.getInt(activity.getContentResolver(),
                        Settings.Secure.LOCATION_MODE);
                return locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
            } else {
                // For older versions, check if GPS and network providers are enabled
                LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            return false;
        }
    }

    /**
     * Request all missing permissions and configure system settings
     */
    public void requestMissingPermissionsAndSettings() {
        // First request permissions
        requestMissingPermissions();

        // Then handle system settings
        //requestSystemSettingsConfiguration();
    }

    /**
     * Request system settings configuration
     */
    public void requestSystemSettingsConfiguration() {
        // Check and request airplane mode to be turned off
        if (!isAirplaneModeOff()) {
            requestAirplaneModeOff();
        }

        // Check and request battery optimization to be disabled
        if (!isBatteryOptimizationDisabled()) {
            requestDisableBatteryOptimization();
        }

        // Check and request location mode to be high accuracy
        if (!isLocationModeHighAccuracy()) {
            requestLocationHighAccuracy();
        }

        // Check and request Bluetooth to be enabled
        if (!isBluetoothEnabled()) {
            requestEnableBluetooth();
        }

        // Check and request unrestricted data usage
        if (!isUnrestrictedDataUsageEnabled()) {
            requestUnrestrictedDataUsage();
        }
    }

    /**
     * Request user to turn off airplane mode
     */
    public void requestAirplaneModeOff() {
        try {
            Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
            activity.startActivity(intent);
            onAirplaneModeSettingRequested();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            // Fallback to general settings
            try {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                activity.startActivity(intent);
            } catch (Exception ex) {
                String s2 = JobsHandler.getStackTraceString(ex);
            }
        }
    }

    /**
     * Request to disable battery optimization
     */
    public void requestDisableBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, BATTERY_OPTIMIZATION_REQUEST_CODE);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                // Fallback to battery optimization settings
                try {
                    Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    activity.startActivity(intent);
                } catch (Exception ex) {
                    String s2 = JobsHandler.getStackTraceString(ex);
                }
            }
        }
    }

    /**
     * Request unrestricted data usage (exempt from data saver)
     */
    public void requestUnrestrictedDataUsage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                // Try to open the specific data usage settings for this app
                Intent intent = new Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, DATA_SAVER_REQUEST_CODE);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                // Fallback to general data usage settings
                try {
                    Intent intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                    activity.startActivity(intent);
                } catch (Exception ex) {
                    String s2 = JobsHandler.getStackTraceString(ex);
                    // Final fallback to wireless settings
                    try {
                        Intent intent2 = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        activity.startActivity(intent2);
                    } catch (Exception ex2) {
                        String s3 = JobsHandler.getStackTraceString(ex2);
                    }
                }
            }
        }
    }

    /**
     * Request Bluetooth to be enabled
     */
    public void requestEnableBluetooth() {
        try {
            BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager != null) {
                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                    // For Android 12+ (API 31+), we need to check BLUETOOTH_CONNECT permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
                                == PackageManager.PERMISSION_GRANTED) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            activity.startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_REQUEST_CODE);
                        } else {
                            // Fallback to Bluetooth settings
                            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                            activity.startActivity(intent);
                        }
                    } else {
                        // For older versions, directly request enable
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        activity.startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_REQUEST_CODE);
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            // Fallback to Bluetooth settings
            try {
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                activity.startActivity(intent);
            } catch (Exception ex) {
                String s2 = JobsHandler.getStackTraceString(ex);
            }
        }
    }
    /**
     * Request location mode to be set to high accuracy
     */
    public void requestLocationHighAccuracy() {
        try {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivityForResult(intent, LOCATION_SETTINGS_REQUEST_CODE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }
    }

    /**
     * Request all missing permissions (original method)
     */
    public void requestMissingPermissions() {
        List<String> missingPermissions = getMissingPermissions();
        List<String> regularPermissions = new ArrayList<>();
        boolean needsBackgroundLocation = false;

        // Separate background location from regular permissions
        for (String permission : missingPermissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    permission.equals(BACKGROUND_LOCATION_PERMISSION)) {
                needsBackgroundLocation = true;
            } else {
                regularPermissions.add(permission);
            }
        }

        // Request regular permissions first
        if (!regularPermissions.isEmpty()) {
            String[] permissionsArray = regularPermissions.toArray(new String[0]);
            ActivityCompat.requestPermissions(activity, permissionsArray, PERMISSIONS_REQUEST_CODE);
        }

        // Request background location separately if needed
        // Note: Background location should only be requested after foreground location is granted
        if (needsBackgroundLocation && areBasicLocationPermissionsGranted()) {
            requestBackgroundLocationPermission();
        }

        // Handle install unknown apps permission separately
        if (!canInstallUnknownApps()) {
            requestInstallUnknownAppsPermission();
        }
    }

    /**
     * Request background location permission specifically
     */
    private void requestBackgroundLocationPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (areBasicLocationPermissionsGranted()) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{BACKGROUND_LOCATION_PERMISSION},
                            PERMISSIONS_REQUEST_CODE);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }
    }

    /**
     * Check if basic location permissions (foreground) are granted
     */
    private boolean areBasicLocationPermissionsGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if background location permission is granted (Android 10+)
     */
    private boolean isBackgroundLocationGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(activity, BACKGROUND_LOCATION_PERMISSION)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Not applicable for older versions
    }

    /**
     * Check specific permission categories
     */
    public boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean areContactsPermissionsGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED;
    }

    public boolean areFilePermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses scoped storage and media permissions
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO)
                            == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO)
                            == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED;
        }
    }

    public boolean areLocationPermissionsGranted() {
        boolean basicLocationGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        // For Android 10+ (API 29+), also check background location for "Allow all the time"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean backgroundLocationGranted = ContextCompat.checkSelfPermission(activity, BACKGROUND_LOCATION_PERMISSION)
                    == PackageManager.PERMISSION_GRANTED;
            return basicLocationGranted && backgroundLocationGranted;
        }

        return basicLocationGranted;
    }

    public boolean isMicrophonePermissionGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean areMusicAudioPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    public boolean areNearbyDevicesPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.NEARBY_WIFI_DEVICES)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Not applicable for older versions
    }

    public boolean arePhonePermissionsGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    public boolean arePhotosVideosPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO)
                            == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    public boolean canInstallUnknownApps() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return activity.getPackageManager().canRequestPackageInstalls();
        }
        return true; // Not applicable for older versions
    }

    /**
     * Request install unknown apps permission
     */
    public void requestInstallUnknownAppsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, INSTALL_APPS_REQUEST_CODE);
        }
    }

    /**
     * Get human-readable permission status report
     */
    public String getPermissionStatusReport() {
        StringBuilder report = new StringBuilder();
        report.append("Permission & System Settings Status Report:\n\n");

        // Permissions
        report.append("=== PERMISSIONS ===\n");
        report.append("Camera: ").append(isCameraPermissionGranted() ? "✓ Granted" : "✗ Denied").append("\n");
        report.append("Contacts: ").append(areContactsPermissionsGranted() ? "✓ Granted" : "✗ Denied").append("\n");
        report.append("Files: ").append(areFilePermissionsGranted() ? "✓ Granted" : "✗ Denied").append("\n");

        // Enhanced location status reporting
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean basicLocation = areBasicLocationPermissionsGranted();
            boolean backgroundLocation = isBackgroundLocationGranted();

            if (basicLocation && backgroundLocation) {
                report.append("Location: ✓ Granted (All the time)").append("\n");
            } else if (basicLocation) {
                report.append("Location: ⚠ Partial (Only while using app)").append("\n");
            } else {
                report.append("Location: ✗ Denied").append("\n");
            }
        } else {
            report.append("Location: ").append(areLocationPermissionsGranted() ? "✓ Granted" : "✗ Denied").append("\n");
        }

        report.append("Microphone: ").append(isMicrophonePermissionGranted() ? "✓ Granted" : "✗ Denied").append("\n");
        report.append("Music & Audio: ").append(areMusicAudioPermissionsGranted() ? "✓ Granted" : "✗ Denied").append("\n");
        report.append("Nearby Devices: ").append(areNearbyDevicesPermissionsGranted() ? "✓ Granted" : "✗ Denied").append("\n");
        report.append("Phone: ").append(arePhonePermissionsGranted() ? "✓ Granted" : "✗ Denied").append("\n");
        report.append("Photos & Videos: ").append(arePhotosVideosPermissionsGranted() ? "✓ Granted" : "✗ Denied").append("\n");
        report.append("Install Unknown Apps: ").append(canInstallUnknownApps() ? "✓ Granted" : "✗ Denied").append("\n");

        // System Settings
        report.append("\n=== SYSTEM SETTINGS ===\n");
        report.append("Airplane Mode: ").append(isAirplaneModeOff() ? "✓ Off" : "✗ On").append("\n");
        report.append("Battery Optimization: ").append(isBatteryOptimizationDisabled() ? "✓ Disabled" : "✗ Optimizing").append("\n");
        report.append("Location Mode: ").append(isLocationModeHighAccuracy() ? "✓ High Accuracy" : "✗ Not High Accuracy").append("\n");
        report.append("Bluetooth: ").append(isBluetoothEnabled() ? "✓ Enabled" : "✗ Disabled").append("\n");
        report.append("Unrestricted Data Usage: ").append(isUnrestrictedDataUsageEnabled() ? "✓ Enabled" : "✗ Restricted").append("\n");

        return report.toString();
    }

    /**
     * Handle permission request results
     * Call this from your Activity's onRequestPermissionsResult method
     */
    public void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allBasicLocationGranted = true;
            boolean backgroundLocationJustGranted = false;

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    onPermissionGranted(permissions[i]);

                    // Check if background location was just granted
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                            permissions[i].equals(BACKGROUND_LOCATION_PERMISSION)) {
                        backgroundLocationJustGranted = true;
                    }
                } else {
                    // Permission denied
                    onPermissionDenied(permissions[i]);

                    // If basic location permissions are denied, don't request background location
                    if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) ||
                            permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        allBasicLocationGranted = false;
                    }
                }
            }

            // Handle background location permission granted
            if (backgroundLocationJustGranted) {
                // Use Handler to avoid ANR - don't start location services immediately
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    onLocationAllTheTimeGranted();
                });
            }

            // If basic location permissions were just granted and we need background location
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    allBasicLocationGranted &&
                    areBasicLocationPermissionsGranted() &&
                    !isBackgroundLocationGranted()) {
                // Delay background location request to avoid ANR
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    requestBackgroundLocationPermission();
                }, 500); // 500ms delay
            }
        }
    }

    /**
     * Handle activity result for install unknown apps
     * Call this from your Activity's onActivityResult method
     */
    public void handleActivityResult(int requestCode, int resultCode) {
        switch (requestCode) {
            case INSTALL_APPS_REQUEST_CODE:
                if (canInstallUnknownApps()) {
                    onInstallUnknownAppsGranted();
                } else {
                    onInstallUnknownAppsDenied();
                }
                break;

            case BATTERY_OPTIMIZATION_REQUEST_CODE:
                if (isBatteryOptimizationDisabled()) {
                    onBatteryOptimizationDisabled();
                } else {
                    onBatteryOptimizationNotDisabled();
                }
                break;

            case LOCATION_SETTINGS_REQUEST_CODE:
                if (isLocationModeHighAccuracy()) {
                    onLocationModeSetToHighAccuracy();
                } else {
                    onLocationModeNotHighAccuracy();
                }
                break;

            case BLUETOOTH_ENABLE_REQUEST_CODE:
                if (isBluetoothEnabled()) {
                    onBluetoothEnabled();
                } else {
                    onBluetoothNotEnabled();
                }
                break;

            case DATA_SAVER_REQUEST_CODE:
                if (isUnrestrictedDataUsageEnabled()) {
                    onUnrestrictedDataUsageEnabled();
                } else {
                    onUnrestrictedDataUsageNotEnabled();
                }
                break;
        }
    }

    // Override these methods to handle permission results
    protected void onPermissionGranted(String permission) {
        // Handle individual permission granted
        // IMPORTANT: Don't start heavy operations here - use background thread
    }

    protected void onPermissionDenied(String permission) {
        // Handle individual permission denied
    }

    protected void onInstallUnknownAppsGranted() {
        // Handle install unknown apps permission granted
    }

    protected void onInstallUnknownAppsDenied() {
        // Handle install unknown apps permission denied
    }

    protected void onLocationAllTheTimeGranted() {
        // Called when background location permission is granted
        // IMPORTANT: Don't start location services on main thread
    }

    protected void onAirplaneModeSettingRequested() {
        // Called when airplane mode settings screen is opened
    }

    protected void onBatteryOptimizationDisabled() {
        // Called when battery optimization is disabled
    }

    protected void onBatteryOptimizationNotDisabled() {
        // Called when battery optimization is still enabled
    }

    protected void onLocationModeSetToHighAccuracy() {
        // Called when location mode is set to high accuracy
    }

    protected void onLocationModeNotHighAccuracy() {
        // Called when location mode is not high accuracy
    }

    protected void onBluetoothEnabled() {
        // Called when Bluetooth is enabled
    }

    protected void onBluetoothNotEnabled() {
        // Called when Bluetooth is not enabled
    }

    protected void onUnrestrictedDataUsageEnabled() {
        // Called when unrestricted data usage is enabled
    }

    protected void onUnrestrictedDataUsageNotEnabled() {
        // Called when unrestricted data usage is not enabled
    }

    public static boolean isHighGPSAccuracy() {
        boolean asd = false;
        int bluetooth_result = 0;
        try {
                /*0 = LOCATION_MODE_OFF
                1 = LOCATION_MODE_SENSORS_ONLY
                2 = LOCATION_MODE_BATTERY_SAVING
                3 = LOCATION_MODE_HIGH_ACCURACY*/
            int mode = 0;
            mode = Settings.Secure.getInt(myApp.getAppContext().getContentResolver(), Settings.Secure.LOCATION_MODE);
            asd = mode == 3;

            WifiManager wm = (WifiManager) myApp.getAppContext().getSystemService(Context.WIFI_SERVICE);
            boolean wsa = wm.isScanAlwaysAvailable();
            ContentResolver resolver = myApp.getAppContext().getContentResolver();
            try {
                bluetooth_result = Settings.Global.getInt(resolver,"ble_scan_always_enabled");
                if(bluetooth_result == 1) {
                    // Bluetooth scanning is on
                } else {
                    // Bluetooth scanning is off
                }
            } catch (Settings.SettingNotFoundException ex) {
                bluetooth_result = 1;
            }
            asd = mode == 3 && wsa && bluetooth_result == 1;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at PermissionsManager isHighGPSAccuracy. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");

        } finally {
            return asd;
        }
    }
}