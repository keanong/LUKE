package com.xdel.luke.objClasses;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.facebook.stetho.Stetho;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.xdel.luke.AddressListNewActivity;
import com.xdel.luke.AddressListRoutingActivity;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.JobsHandler;
//import com.xdel.luke.Manifest;
import com.xdel.luke.MapsActivity;
import com.xdel.luke.MapsAllActivity;
import com.xdel.luke.Messaging;
import com.xdel.luke.Messaginglist;
import com.xdel.luke.R;
import com.xdel.luke.Rooting.RootActivityGuard;
import com.xdel.luke.Rooting.RootGuard;
import com.xdel.luke.messagingClasses.NativeLoader;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.net.ssl.SSLContext;

/**
 * Created by kean.ong on 07/10/2015.
 */
public class myApp extends Application {

    public static Context context;
    public static Context appContext;
    private static myApp Instance;
    public static volatile Handler applicationHandler = null;
    public static Activity currentActivity;
    public static final String CHANNEL_ID = "LUKE_CHANNEL";
    public static final String CHANNEL_ID_PIP = "New_Pick_Up";
    public static final String CHANNEL_ID_MSG = "New_Messages";


    public void onCreate() {
        super.onCreate();
        /*if(BuildConfig.DEBUG) //for identifying indept violations etc
            StrictMode.enableDefaults();*/

        myApp.context = getApplicationContext(); // this is activity context, tied to lifecycle of the activity , replace in all activities' onCreate/onResume using setAppContext
        myApp.appContext = getApplicationContext(); // this is tied to lifecycle of the application
        Instance = this;
        RootGuard.init(this);
        if (!RootActivityGuard.register(this)) {
            //fail closed: a blocked process without the activity guard must not start normally
            throw new IllegalStateException("RootGuard: process is blocked but the activity guard could not be registered.");
        }
        applicationHandler = new Handler(getInstance().getMainLooper());
        createNotificationChannel();
        createNotificationChannelPIP();
        createNotificationChannelMSG();
        ////check for memory leaks
        //LeakCanary.install(this);
        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/
        ////check for memory leaks

        ////check sqlite data n sharedpreferences related in google chrome
        //Stetho.initializeWithDefaults(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build()
        );
        ////check sqlite data n sharedpreferences related in google chrome

        ////messaging related
        NativeLoader.initNativeLibs(myApp.getInstance());
        ////messaging related

        try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                 | NoSuchAlgorithmException | KeyManagementException e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at myApp onCreate OpenSSL. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
        com.xdel.luke.debugtools.DebugTestHooks.install(this);
        //R7 A7: crash-report recovery; isolated so a failure cannot stop app startup
        try {
            com.xdel.luke.ExceptionHandler.CrashRecoveryNotifier.createChannel(this);
        } catch (Throwable t) {
            //createChannel() never throws; this only guards against class-loading errors
        }
        try {
            com.xdel.luke.ExceptionHandler.CrashRecoveryStarter.start(this);
        } catch (Throwable t) {
            //start() never throws; this only guards against class-loading errors
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel sc = new NotificationChannel(
                    CHANNEL_ID,
                    "Luke",
                    NotificationManager.IMPORTANCE_LOW
            );
            sc.setSound(null, null);
            sc.setVibrationPattern(null);
            sc.setBypassDnd(false);
            sc.enableLights(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(sc);
        }
    }

    private void createNotificationChannelPIP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel sc = new NotificationChannel(
                    CHANNEL_ID_PIP,
                    "New Pick Up",
                    NotificationManager.IMPORTANCE_HIGH
            );
            sc.setVibrationPattern(null);
            sc.setBypassDnd(false);
            sc.enableLights(true);
            sc.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pip3),
                    new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build());


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(sc);
        }
    }

    private void createNotificationChannelMSG() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel sc = new NotificationChannel(
                    CHANNEL_ID_MSG,
                    "Messaging",
                    NotificationManager.IMPORTANCE_HIGH
            );
            sc.setVibrationPattern(null);
            sc.setBypassDnd(false);
            sc.enableLights(true);
            sc.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.text_message),
                    new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build());

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(sc);
        }
    }

    public static myApp getInstance() {
        return Instance;
    }

    public static Context getAppContext() {
        return myApp.context;
    }

    public static Context getApplicationCtx() {
        return myApp.appContext;
    }

    public static void setAppContext(Context ctx) {
        myApp.context = ctx;
    }

    public static boolean appAllowBackgroundLocation() {
        boolean asd = false;
        try {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(getInstance(), android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        asd = true;
                    }
                } else {
                    asd = true;
                }
            } catch (Exception e) {
                asd = true;
                objGlobalMethods.writeToFile("Exception at myApp appAllowBackgroundLocation Version checking. Exception: " + e.getMessage(), "", "", "", "");
            }
        } catch (Exception e) {
            asd = true;
            objGlobalMethods.writeToFile("Exception at myApp appAllowBackgroundLocation. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean BTConnectGranted() {
        boolean asd = false;
        String[] PERMISSIONS_LOCATION = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_PRIVILEGED
        };
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                int permission2 = ContextCompat.checkSelfPermission(getInstance(), Manifest.permission.BLUETOOTH_CONNECT);
                if (permission2 != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            myApp.currentActivity,
                            PERMISSIONS_LOCATION,
                            1
                    );
                }
            } else {
                asd = true;
            }
        } catch (Exception e) {
            asd = true;
            objGlobalMethods.writeToFile("Exception at myApp BTScanGranted. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean bt_enabled() {
        boolean asd = false;
        try {

        } catch (Exception e) {
            asd = true;
            objGlobalMethods.writeToFile("Exception at myApp bt_enabled. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean appIsOptimized(String packageName) {
        boolean asd = false;
        try {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                        asd = true;
                    }
                }
                //PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                //asd = (pm.isIgnoringBatteryOptimizations(packageName));
            } catch (Exception e) {
                asd = true;
                objGlobalMethods.writeToFile("Exception at myApp appIsOptimized Version checking. Exception: " + e.getMessage(), "", "", "", "");
            }
        } catch (Exception e) {
            asd = true;
            objGlobalMethods.writeToFile("Exception at myApp appWhitelistFromBattOptimization. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static int getDeviceSettingsFlag() {
        int f = 0;
        boolean AirplaneModeOn0 = false;
        boolean AirplaneModeOn = false;
        boolean GpsOn0 = false;
        boolean GpsOn = false;
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        boolean AllPermissionGranted0 = true;
        boolean AllPermissionGranted = true;
        boolean GPSisHighAcc0 = false;
        boolean GPSisHighAcc = false;
        Context ctx = getAppContext();

        try {
            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {

                    AirplaneModeOn0 = Settings.System.getInt(ctx.getContentResolver(),
                            Settings.System.AIRPLANE_MODE_ON, 0) != 0;
                } else {
                    AirplaneModeOn0 = Settings.Global.getInt(ctx.getContentResolver(),
                            Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
                }
            } catch (Exception e) {
                AirplaneModeOn0 = false;
                objGlobalMethods.writeToFile("Exception at myApp getDeviceSettingsFlag AirplaneModeOn. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                AirplaneModeOn = AirplaneModeOn0;
            }

            try {
                if (locationManager == null)
                    locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
                try {
                    gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch (Exception ex) {
                    //do nothing...
                }

                try {
                    network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception ex) {
                    //do nothing...
                }
            } catch (Exception e) {
                gps_enabled = false;
                network_enabled = false;
                objGlobalMethods.writeToFile("Exception at myApp getDeviceSettingsFlag GpsOn. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                GpsOn = gps_enabled || network_enabled;
            }

            try {
                if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (AllPermissionGranted0) {
                        AllPermissionGranted0 = false;
                    }
                }
                /*if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    if (AllPermissionGranted0) {
                        AllPermissionGranted0 = false;
                    }
                }*/
                if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (AllPermissionGranted0) {
                        AllPermissionGranted0 = false;
                    }
                }
                if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    if (AllPermissionGranted0) {
                        AllPermissionGranted0 = false;
                    }
                }
                if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (AllPermissionGranted0) {
                        AllPermissionGranted0 = false;
                    }
                }
                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    if (AllPermissionGranted0) {
                        AllPermissionGranted0 = false;
                    }
                }
            } catch (Exception e) {
                AllPermissionGranted0 = false;
                objGlobalMethods.writeToFile("Exception at myApp getDeviceSettingsFlag AllPermissionGranted. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                AllPermissionGranted = AllPermissionGranted0;
            }

            try {
                /*0 = LOCATION_MODE_OFF
                1 = LOCATION_MODE_SENSORS_ONLY
                2 = LOCATION_MODE_BATTERY_SAVING
                3 = LOCATION_MODE_HIGH_ACCURACY*/
                int mode = 0;
                mode = Settings.Secure.getInt(ctx.getContentResolver(), Settings.Secure.LOCATION_MODE);
                GPSisHighAcc0 = mode == 3;
            } catch (Exception e) {
                GPSisHighAcc0 = true;
                objGlobalMethods.writeToFile("Exception at myApp getDeviceSettingsFlag GPSisHighAcc. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                GPSisHighAcc = GPSisHighAcc0;
            }

            if (AirplaneModeOn0 && !((f & 1) == 1)) {
                f += 1;
            }
            if (!GpsOn && !((f & 2) == 2)) {
                f += 2;
            }
            if (!AllPermissionGranted && !((f & 4) == 4)) {
                f += 4;
            }
            if (!GPSisHighAcc && !((f & 8) == 8)) {
                f += 8;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at myApp getDeviceSettingsFlag. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return f;
        }
    }

    public static boolean isAirplaneModeOnFlag(int f) {
        return ((f & 1) == 1);
    }

    public static boolean isGpsOnFlag(int f) {
        return ((f & 2) == 2);
    }

    public static boolean isAllPermissionGrantedFlag(int f) {
        return ((f & 4) == 4);
    }

    public static boolean GPSisHighAccFlag(int f) {
        return ((f & 8) == 8);
    }

    public static Boolean isNfcEnabled(NfcAdapter mNfcAdapter, Context ctx) {
        Boolean asd = false;
        try {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(ctx);
            asd = mNfcAdapter.isEnabled();
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at myApp isNfcEnabled. Exception: " + e.getMessage(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean isOnNonWorkingScreen() {
        Boolean asd = false;
        try {
            if (currentActivity != null && (
                    currentActivity.getClass() == DrawerMainActivity.class ||
                            currentActivity.getClass() == DrawerMain2Activity.class ||
                            currentActivity.getClass() == DashboardSubActivity.class ||
                            currentActivity.getClass() == AddressListNewActivity.class ||
                            currentActivity.getClass() == AddressListRoutingActivity.class ||
                            currentActivity.getClass() == MapsAllActivity.class ||
                            currentActivity.getClass() == MapsActivity.class ||
                            currentActivity.getClass() == Messaginglist.class ||
                            currentActivity.getClass() == Messaging.class)) {
                asd = true;
            } else {
                asd = false;
            }
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static void setLocale(final Context ctx) {
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        String langType = "en";
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            } else {
                Locale locale = new Locale(langType);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                ctx.getResources().updateConfiguration(config, ctx.getResources().getDisplayMetrics());
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at myApp setLocale. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    ////language change
    public void initAppLanguage(Context context) {
        LocaleHelper.initialize(context, LocaleHelper.getSelectedLanguageId());
    }
    ////language change


}


