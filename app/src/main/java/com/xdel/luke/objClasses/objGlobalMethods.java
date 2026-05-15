package com.xdel.luke.objClasses;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.location.Location;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.AboutActivity;
import com.xdel.luke.AddressListNewActivity;
import com.xdel.luke.AddressListRoutingActivity;
import com.xdel.luke.AlbumStorageDirFactory;
import com.xdel.luke.BCnSVC.ActiveService;
import com.xdel.luke.BCnSVC.SendQueuedEmail_Receiver;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.BaseAlbumDirFactory;
import com.xdel.luke.BulkPickUpActivity;
import com.xdel.luke.CODupdate;
import com.xdel.luke.Caging.CageInOutActivity;
import com.xdel.luke.Caging.CageScanActivity;
import com.xdel.luke.Camera5Activity;
import com.xdel.luke.CheckListActivity;
import com.xdel.luke.CheckStatusActivity;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.CreditCardActivity;
import com.xdel.luke.CustomGallery2Activity;
import com.xdel.luke.CustomGalleryActivity;
import com.xdel.luke.DOinvCheckActivity;
import com.xdel.luke.Dashboard2Activity;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.DriverPointMain_Activity;
import com.xdel.luke.DriverPointSummaryActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.FroyoAlbumDirFactory;
import com.xdel.luke.GcmIntentService;
import com.xdel.luke.HRM.ChangePwdActivity;
import com.xdel.luke.HRM.ProfileActivity;
import com.xdel.luke.HRM.StaffPassActivity;
import com.xdel.luke.HRM_Leave.LeaveActivity;
import com.xdel.luke.HRM_Leave.LeaveApplicationActivity;
import com.xdel.luke.ItemGroupCheck;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobScheduler.NewPerMinBCR;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.JobsListGroupActivity;
import com.xdel.luke.Jobs_Retrieval_Activity;
import com.xdel.luke.JobsheetActivity;
import com.xdel.luke.JobsheetSubmitActivity;
import com.xdel.luke.Label_Tagging_Activity;
import com.xdel.luke.MPD_CheckOutActivity;
import com.xdel.luke.MainActivity;
import com.xdel.luke.MainCODActivity;
import com.xdel.luke.MapsActivity;
import com.xdel.luke.MapsAllActivity;
import com.xdel.luke.MassUpdateOKPwP2;
import com.xdel.luke.Messaging;
import com.xdel.luke.Messaginglist;
import com.xdel.luke.OtpActivity;
import com.xdel.luke.PerMinuteRun_BroadcastReceiver;
import com.xdel.luke.QRReaderActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.SecuritySealActivity;
import com.xdel.luke.SettingsActivity;
import com.xdel.luke.SignatureNameActivity;
import com.xdel.luke.UpdateCOD_List;
import com.xdel.luke.ViewImageActivity;
import com.xdel.luke.ViewNotificationActivity;
import com.xdel.luke.Voip.CallActivity;
import com.xdel.luke.WebService;
import com.xdel.luke.messagingClasses.ChatMessage;
import com.xdel.luke.messagingClasses.Status;
import com.xdel.luke.messagingClasses.UserType;
import com.xdel.luke.mpd.MPDMainActivity;
import com.xdel.luke.mpd.MPD_Group;
import com.xdel.luke.mpd.mpdclass;
import com.xdel.luke.setDeliveredMassStatusActivity;
import com.xdel.luke.setDeliveredStatusActivity;
import com.xdel.luke.setDunningDeliveredStatusActivity;
import com.xdel.luke.setFailStatusActivity;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.TempLoggerRegActivity;
import com.xdel.luke.zip.ZipManager;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by kean.ong on 18/06/2015.
 */
public class objGlobalMethods {

    static final String TAG = "Luke";
    static boolean mailSent = false;
    public static boolean GPSalarmStarted = false;
    static PendingIntent GPSpendingIntent;
    public static AlarmManager GPSmanager;
    public static Handler GPSHandler;
    public static boolean PerMinRunalarmStarted = false;
    static PendingIntent PerMinRunPendingIntent;
    static AlarmManager PerMinRunManager;
    public static boolean NewPerMinRunalarmStarted = false;
    static PendingIntent NewPerMinRunPendingIntent;
    static AlarmManager NewPerMinRunManager;

    private String regid;
    private ProgressDialog pd, pdCheck;
    private boolean errored = false;

    public static SharedPreferences getMedsColSP() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return myApp.getAppContext().getSharedPreferences("MedsColSP",
                Context.MODE_PRIVATE);
    }
    public static SharedPreferences getSystemRelatedSharedPreferences() {
        return myApp.getAppContext().getSharedPreferences("SystemRelatedSharedPreferences",
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getGcmPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getUpdateJobInfoPreferences() {
        return myApp.getAppContext().getSharedPreferences("UpdateJobInfoPrefs",
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getBTLoggerRelatedSharedPreferences() {
        return myApp.getAppContext().getSharedPreferences("BTLoggerRelatedSharedPreferences",
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getVrpRelatedSharedPreferences() {
        return myApp.getAppContext().getSharedPreferences("vrpsp",
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getChecklistSP() {
        return myApp.getAppContext().getSharedPreferences("ChecklistSP",
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getMPDRelatedSP() {
        return myApp.getAppContext().getSharedPreferences("MPDSP",
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getLocationSP() {
        return myApp.getAppContext().getSharedPreferences("LocationSP",
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getJobInventoryVSP() {
        return myApp.getAppContext().getSharedPreferences("JobInventoryVSP",
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getDistanceSP() {
        return myApp.getAppContext().getSharedPreferences("DistanceSP",
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getPassportSP() {
        return myApp.getAppContext().getSharedPreferences("PassportSP",
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getJobInfoSP() {
        return myApp.getAppContext().getSharedPreferences("JobInfoSP",
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getGrouplistSP() {
        return myApp.getAppContext().getSharedPreferences("GrouplistSP",
                Context.MODE_PRIVATE);
    }

    public static SharedPreferences getShpmtVSP() {
        return myApp.getAppContext().getSharedPreferences("ShpmtVSP",
                Context.MODE_PRIVATE);
    }


    public static String getMedsColPrefs(SharedPreferences prefs, String prefName) {
        if (prefs == null) {
            prefs = getMedsColSP();
        }
        return prefs.getString(prefName, "");
    }

    public static String getGCMPrefs(SharedPreferences prefs, String prefName) {
        if (prefs == null) {
            prefs = getGcmPreferences();
        }
        return prefs.getString(prefName, "");
    }

    public static String getUpdateJobInfoPrefs(SharedPreferences prefs, String prefName) {
        if (prefs == null) {
            prefs = getUpdateJobInfoPreferences();
        }
        return prefs.getString(prefName, "");
    }

    public static String getBTLoggerRelatedPrefs(SharedPreferences prefs, String prefName) {
        if (prefs == null) {
            prefs = getBTLoggerRelatedSharedPreferences();
        }
        return prefs.getString(prefName, "");
    }

    public static String getVrpRelatedSP_KeyValue(String key) {
        String v = "";
        SharedPreferences sp = getVrpRelatedSharedPreferences();
        try {
            v = sp.getString(key, "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods getVrpRelatedSP_KeyValue. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return v;
        }
    }

    public static String getVrpRelatedSP_KeyValue2(SharedPreferences sp, String key) {
        String v = "";
        if (sp == null)
            sp = getVrpRelatedSharedPreferences();
        try {
            v = sp.getString(key, "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods getVrpRelatedSP_KeyValue2. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return v;
        }
    }

    public static String getChecklistRelatedSP_KeyValue(String key) {
        String v = "";
        SharedPreferences sp = getChecklistSP();
        try {
            v = sp.getString(key, "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods getChecklistRelatedSP_KeyValue. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return v;
        }
    }

    public static String getChecklistFromChecklistSP() {
        String cl = "";
        SharedPreferences sp = getChecklistSP();
        try {
            cl = sp.getString("DIPChecklist", "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods getChecklistFromChecklistSP. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return cl;
        }
    }

    public static String getMPDRelatedSP_KeyValue0(SharedPreferences sp, String key) {
        String v = "";
        if (sp == null)
            sp = getMPDRelatedSP();

        try {
            v = sp.getString(key, "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods getMPDRelatedSP_KeyValue0. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return v;
        }
    }

    public static String getMPDRelatedSP_KeyValue(String key) {
        String v = "";
        SharedPreferences sp = getMPDRelatedSP();
        try {
            v = sp.getString(key, "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods getMPDRelatedSP_KeyValue. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return v;
        }
    }

    public static String getLocationRelatedSP_KeyValue(String key) {
        String v = "";
        SharedPreferences sp = getLocationSP();
        try {
            v = sp.getString(key, "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods getLocationRelatedSP_KeyValue. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return v;
        }
    }

    public static String getJobInventoryVSP_KeyValue(SharedPreferences prefs, String prefName) {
        if (prefs == null) {
            prefs = getJobInventoryVSP();
        }
        return prefs.getString(prefName, "");
    }

    public static String getDistanceSP_KeyValue(SharedPreferences prefs, String prefName) {
        if (prefs == null) {
            prefs = getDistanceSP();
        }
        return prefs.getString(prefName, "");
    }

    public static String getPassportSP_KeyValue(SharedPreferences prefs, String prefName) {
        if (prefs == null) {
            prefs = getPassportSP();
        }
        return prefs.getString(prefName, "");
    }

    public static String getJobInfoSP_KeyValue(SharedPreferences prefs, String prefName) {
        if (prefs == null) {
            prefs = getJobInfoSP();
        }
        return prefs.getString(prefName, "");
    }

    public static String getGrouplistSP_KeyValue(SharedPreferences prefs, String prefName) {
        if (prefs == null) {
            prefs = getGrouplistSP();
        }
        return prefs.getString(prefName, "");
    }

    public static String getShpmtVSP_KeyValue(SharedPreferences prefs, String prefName) {
        if (prefs == null) {
            prefs = getShpmtVSP();
        }
        return prefs.getString(prefName, "");
    }


    public static void setMedsColPrefs(SharedPreferences prefs, String prefName, String value) {
        if (prefs == null) {
            prefs = getMedsColSP();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prefName, value);
        editor.apply();
    }

    public static void setMedsColPrefsMass(SharedPreferences gcmp, String[] prefName, String[] value) {
        if (gcmp == null) {
            gcmp = getMedsColSP();
        }
        SharedPreferences.Editor editor = gcmp.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setMedsColPrefsMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setGCMPrefs(SharedPreferences prefs, String prefName, String value) {
        if (prefs == null) {
            prefs = getGcmPreferences();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prefName, value);
        editor.apply();
    }

    public static void setGCMPrefsMass(SharedPreferences gcmp, String[] prefName, String[] value) {
        if (gcmp == null) {
            gcmp = getGcmPreferences();
        }
        SharedPreferences.Editor editor = gcmp.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setGCMPrefsMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setUpdateJobInfoPrefs(SharedPreferences prefs, String prefName, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prefName, value);
        editor.apply();
    }

    public static void setUpdateJobInfoPrefsMass(SharedPreferences prefs, String[] prefName, String[] value) {
        if (prefs == null) {
            prefs = getUpdateJobInfoPreferences();
        }
        SharedPreferences.Editor editor = prefs.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setUpdateJobInfoPrefsMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setBTLoggerRelatedPrefs(SharedPreferences prefs, String prefName, String value) {
        if (prefs == null)
            prefs = getBTLoggerRelatedSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prefName, value);
        editor.apply();
    }

    public static void setBTLoggerRelatedMass(SharedPreferences prefs, String[] prefName, String[] value) {
        if (prefs == null) {
            prefs = getBTLoggerRelatedSharedPreferences();
        }
        SharedPreferences.Editor editor = prefs.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setBTLoggerRelatedMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setVrpRelatedSP_KeyValue(String key, String value) {
        SharedPreferences sp = getVrpRelatedSharedPreferences();
        try {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods setVrpRelatedSP_KeyValue. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void setVrpRelatedSP_KeyValue2(SharedPreferences sp, String key, String value) {
        if (sp == null)
            sp = getVrpRelatedSharedPreferences();
        try {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods setVrpRelatedSP_KeyValue2. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void setVrpSPMass(SharedPreferences prefs, String[] prefName, String[] value) {
        if (prefs == null) {
            prefs = getVrpRelatedSharedPreferences();
        }
        SharedPreferences.Editor editor = prefs.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setVrpSPMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setVrpSPMass2(String[] prefName, String[] value) {
        SharedPreferences prefs = getVrpRelatedSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setVrpSPMass2. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setChecklistRelatedSP_KeyValue(String key, String value) {
        SharedPreferences sp = getChecklistSP();
        try {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods setChecklistRelatedSP_KeyValue. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void saveChecklistSP(String v) {
        SharedPreferences sp = getChecklistSP();
        try {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("DIPChecklist", v);
            editor.apply();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods saveChecklistSP. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void clearGrouplistSP() {
        SharedPreferences sp = getGrouplistSP();
        try {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("jobSelectionInfoJsonStr", "");
            editor.putString("mChildCheckStatesPrefStr", "");
            editor.apply();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods clearGrouplistSP. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void setMPDRelatedSP_KeyValue(String key, String value) {
        SharedPreferences sp = getMPDRelatedSP();
        try {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods setMPDRelatedSP_KeyValue. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void setMpdSPMass(String[] prefName, String[] value) {
        SharedPreferences prefs = getMPDRelatedSP();
        SharedPreferences.Editor editor = prefs.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setMpdSPMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setLocationRelatedSP_KeyValue(String key, String value) {
        SharedPreferences sp = getLocationSP();
        try {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods setLocationRelatedSP_KeyValue. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void setJobInventoryVSP(SharedPreferences prefs, String prefName, String value) {
        if (prefs == null) {
            prefs = getJobInventoryVSP();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prefName, value);
        editor.apply();
    }

    public static void setJobInventoryVSPMass(SharedPreferences gcmp, String[] prefName, String[] value) {
        if (gcmp == null) {
            gcmp = getJobInventoryVSP();
        }
        SharedPreferences.Editor editor = gcmp.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setJobInventoryVSPMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setDistanceSP_KeyValue(String key, String value) {
        SharedPreferences sp = getDistanceSP();
        try {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods setDistanceSP_KeyValue. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void setDistanceSPMass(String[] prefName, String[] value) {
        SharedPreferences prefs = getDistanceSP();
        SharedPreferences.Editor editor = prefs.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setDistanceSPMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setPassportSP_KeyValue(SharedPreferences prefs, String prefName, String value) {
        if (prefs == null) {
            prefs = getPassportSP();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prefName, value);
        editor.apply();
    }

    public static void setPassportSPMass(SharedPreferences prefs, String[] prefName, String[] value) {
        if (prefs == null) {
            prefs = getPassportSP();
        }
        SharedPreferences.Editor editor = prefs.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setPassportSPMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setJobInfoSP_KeyValue(SharedPreferences prefs, String prefName, String value) {
        if (prefs == null) {
            prefs = getJobInfoSP();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prefName, value);
        editor.apply();
    }

    public static void setJobInfoSPMass(SharedPreferences prefs, String[] prefName, String[] value) {
        if (prefs == null) {
            prefs = getJobInfoSP();
        }
        SharedPreferences.Editor editor = prefs.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setJobInfoSPMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setGrouplistSP_KeyValue(SharedPreferences prefs, String prefName, String value) {
        if (prefs == null) {
            prefs = getGrouplistSP();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prefName, value);
        editor.apply();
    }

    public static void setGrouplistSPMass(SharedPreferences prefs, String[] prefName, String[] value) {
        if (prefs == null) {
            prefs = getGrouplistSP();
        }
        SharedPreferences.Editor editor = prefs.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setGrouplistSPMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setShpmtVSP_KeyValue(SharedPreferences prefs, String prefName, String value) {
        if (prefs == null) {
            prefs = getShpmtVSP();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(prefName, value);
        editor.apply();
    }

    public static void setShpmtVSPMass(SharedPreferences prefs, String[] prefName, String[] value) {
        if (prefs == null) {
            prefs = getShpmtVSP();
        }
        SharedPreferences.Editor editor = prefs.edit();
        try {
            for (int i = 0; i <= prefName.length - 1; i++) {
                editor.putString(prefName[i], value[i]);
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setShpmtVSPMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }




    public static void setLastUpdatedJobLatLng(SharedPreferences g, boolean chkUnPark) {
        if (g == null) {
            g = getGcmPreferences();
        }
        try {
            String fLAT = getGCMPrefs(g, "LAT");
            String fLNG = getGCMPrefs(g, "LNG");
            String lastJobQueueDataJsonStr = getGCMPrefs(g, "JobQueueDataJsonStr");
            String desPostal = getGCMPrefs(g, "desPostal");
            String lastpostal = getGCMPrefs(g, "lastpostal");
            if (chkUnPark && !IsEmptyString(lastpostal) && !IsEqualString(desPostal, lastpostal)) {
                int TotalOriDIPJobsCountByPostal = DAO_Jobs.getTotalOriPIPDIPJobsCountByPostalSQLstr(lastpostal);
                if (TotalOriDIPJobsCountByPostal == 0) {
                    DAO_Jobs.updateParkedDIPJobsToParkedCatByPostal(lastpostal);
                }
            }
            lastpostal = desPostal;
            SharedPreferences.Editor editor = g.edit();
            editor.putString("fLAT", fLAT);
            editor.putString("fLNG", fLNG);
            if (chkUnPark) {
                editor.putString("lastpostal", lastpostal);
                editor.putString("lastJobQueueDataJsonStr", lastJobQueueDataJsonStr);
                editor.putString("JobsPostalsToBeUNPARKEDJsonStr", "");
            }
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setLastUpdatedJobLatLng. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void setSettingsPrefs(objSettings settings) {
        try {
            SharedPreferences gcmp = getGcmPreferences();
            SharedPreferences.Editor editor = gcmp.edit();
            editor.putString("SettingsDistVioEnabled", settings != null && settings.DistVioEnabled ? "true" : "false");
            editor.apply();
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods setSettingsPrefs. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    public static String getSettingsDistVioEnabled() {
        String DistVioEnabled = "";
        try {
            SharedPreferences gcmp = getGcmPreferences();
            DistVioEnabled = gcmp.getString("SettingsDistVioEnabled", "");
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            DistVioEnabled = "";
        } finally {
            return DistVioEnabled;
        }
    }

    public static void setJobInfoJsonStrPref(SharedPreferences prefs, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("hfJobInfoJsonStr", value);
        editor.apply();
    }

    /*public static void sethfJobsInfoArrJsonStrPref(SharedPreferences prefs,String value){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("hfJobsInfoArrJsonStr", value);
        editor.commit();
    }*/



    public static Spanned getSpannedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }

    public static void showNewPIPdialogUponReceived(final SharedPreferences gcmp) {
        try {
            Class<?> cc = null;
            boolean asd = false;
            if (myApp.currentActivity != null) {
                cc = myApp.currentActivity.getClass();
                asd = (cc == MapsAllActivity.class || cc == AddressListRoutingActivity.class || cc == DrawerMainActivity.class ||
                        cc == DrawerMain2Activity.class || cc == AddressListNewActivity.class || cc == JobsListGroupActivity.class ||
                        cc == DashboardSubActivity.class);
                if (asd) {
                    if (cc == MapsAllActivity.class) {
                        final MapsAllActivity act = (MapsAllActivity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                showNewPIPdialog(gcmp);
                                //act.setUpMap2SQLiteCustomIcons(null);
                                act.setUpMap2SQLiteCustomIconsAddNewPipLocations();

                            }
                        });
                    } else if (cc == AddressListRoutingActivity.class) {
                        final AddressListRoutingActivity act = (AddressListRoutingActivity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                showNewPIPdialog(gcmp);
                            }
                        });
                    } else if (cc == DrawerMainActivity.class) {
                        final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                showNewPIPdialog(gcmp);
                            }
                        });
                    } else if (cc == DrawerMain2Activity.class) {
                        final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                showNewPIPdialog(gcmp);
                            }
                        });
                    } else if (cc == AddressListNewActivity.class) {
                        final AddressListNewActivity act = (AddressListNewActivity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                showNewPIPdialog(gcmp);
                            }
                        });
                    } else if (cc == JobsListGroupActivity.class) {
                        final JobsListGroupActivity act = (JobsListGroupActivity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                showNewPIPdialog(gcmp);
                            }
                        });
                    } else if (cc == DashboardSubActivity.class) {
                        final DashboardSubActivity act = (DashboardSubActivity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                showNewPIPdialog(gcmp);
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods showNewPIPdialogUponReceived. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void showNewPIPdialog(SharedPreferences gcmp) {
        String showpippop = getGCMPrefs(gcmp, "showpippop");
        try {
            if (!IsEmptyString(showpippop)) {
                objGlobalMethods.setGCMPrefs(gcmp, "showpippop", "");
                final Context ctx = myApp.getAppContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                builder.setCancelable(false);
                String msg = myApp.getAppContext().getString(R.string.newpip);
                writeToFile("showNewPIPdialog called.", "", "", "", "");

                builder.setMessage(msg);
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.roger), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (FCMservice.mNotificationManager != null) {
                                FCMservice.mNotificationManager.cancel(2);
                            }
                            FCMservice.notiNotRead = false;
                            dialog.dismiss();
                        } catch (ActivityNotFoundException e) {
                            String stack = JobsHandler.getStackTraceString(e);
                            writeToFile("ActivityNotFoundException at objGlobalMethods showNewPIPdialog setPositiveButton onClick. ActivityNotFoundException: "
                                    + e.toString() + "\nStack: " + stack, "", "", "", "");
                        } catch (Exception e) {
                            writeToFile("Exception at objGlobalMethods showNewPIPdialog setPositiveButton onClick. Exception: "
                                    + e.toString(), "", "", "", "");
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods showNewPIPdialog. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static String getDuration(long DigiAgreementcreateViewTime) {
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        String v = "";
        long curr = new Date().getTime();
        long diff = curr - DigiAgreementcreateViewTime;
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        int f = 0;
        try {
            long elapsedDays = diff / daysInMilli;
            diff = diff % daysInMilli;

            long elapsedHours = diff / hoursInMilli;
            diff = diff % hoursInMilli;

            long elapsedMinutes = diff / minutesInMilli;
            diff = diff % minutesInMilli;

            long elapsedSeconds = diff / secondsInMilli;

            if (elapsedDays > 0) {
                v += elapsedDays + " Day(s)";
                f = 1;
            }
            if (elapsedHours > 0) {
                v += ((f & 1) == 1) ? " " + elapsedHours + " Hour(s)" : elapsedHours + " Hour(s)";
                f += 2;
            }
            if (elapsedMinutes > 0) {
                v += (((f & 1) == 1) || ((f & 2) == 2)) ? " " + elapsedMinutes + " Minute(s)" : elapsedMinutes + " Minute(s)";
                f += 3;
            }
            if (elapsedSeconds > 0) {
                v += (((f & 1) == 1) || ((f & 2) == 2) || ((f & 3) == 3)) ? " " + elapsedSeconds + " Second(s)" : elapsedSeconds + " Second(s)";
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods getDuration. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return v;
        }
    }

    public static boolean showDigiAgreement() {
        SharedPreferences gcmp = getGcmPreferences();
        boolean asd = false;
        String lastDigiAgreementTSJsonStr = "";
        long lastDigiAgreementTS = 0;
        try {
            lastDigiAgreementTSJsonStr = getGCMPrefs(gcmp, "lastDigiAgreementTSJsonStr");
            //lastDigiAgreementTSJsonStr = "1604219229000"; // 01/11/082709am
            //lastDigiAgreementTSJsonStr = "1604564829000"; // 05/11/082709am
            if (IsEmptyString(lastDigiAgreementTSJsonStr) || IsEqualString(lastDigiAgreementTSJsonStr, "0")) {
                asd = true;
            } else {
                lastDigiAgreementTS = Long.parseLong(lastDigiAgreementTSJsonStr);
                asd = JobsHandler.moreThanXdaysZerofied(lastDigiAgreementTS, 14, true, false);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods cbReviewSelected. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean cbReviewSelected() {
        boolean asd = false;
        try {
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            String cbReviewSelectedstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "cbReviewSelected");
            asd = !objGlobalMethods.IsEmptyString(cbReviewSelectedstr);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods cbReviewSelected. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    //not completed
    public static Date getCurrentGMTDate() {
        Date cGMTd = new Date();
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                    Locale.getDefault());
            cGMTd = calendar.getTime();
            DateFormat date = new SimpleDateFormat("Z");
        } catch (Exception e) {

        } finally {
            return cGMTd;
        }
    }
    //not completed

    public static boolean isFutureDate(Date input) {
        boolean asd = false;
        boolean asd2 = false;
        boolean istoday = false;
        int comparedval = 0;
        Date zerofiedInputDate;
        Calendar c = Calendar.getInstance();
        Date today530 = objGlobalMethods.getTodayDateAtCertainTime(5, 30);
        Timestamp ts;
        Timestamp ts530 = new Timestamp(today530.getTime());
        //Date zerofiednow = getZeroSecondsTimeDate(new Date());
        Date zerofiednow = getZeroTimeDate(new Date());

        //0 comes when two date are same,
        //1 comes when date1 is higher then date2
        //-1 comes when date1 is lower then date2
        try {
            zerofiedInputDate = objGlobalMethods.getZeroTimeDate(input);
            //comparedval = zerofiednow.compareTo(zerofiedInputDate);
            comparedval = zerofiedInputDate.compareTo(zerofiednow);
            asd = comparedval == 1;
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods isFutureDate. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean isFutureDateOr530(Date input) {
        boolean asd = false;
        boolean asd2 = false;
        boolean istoday = false;
        int comparedval = 0;
        Date zerofiedInputDate;
        Calendar c = Calendar.getInstance();
        Date today530 = objGlobalMethods.getTodayDateAtCertainTime(5, 30);
        Timestamp ts;
        Timestamp ts530 = new Timestamp(today530.getTime());
        Date zerofiednow = getZeroSecondsTimeDate(new Date());

        //0 comes when two date are same,
        //1 comes when date1 is higher then date2
        //-1 comes when date1 is lower then date2
        try {
            zerofiedInputDate = objGlobalMethods.getZeroSecondsTimeDate(input);
            //comparedval = zerofiednow.compareTo(zerofiedInputDate);
            comparedval = zerofiedInputDate.compareTo(zerofiednow);
            asd2 = comparedval == 1 || comparedval == 0;
            if (!asd2) {
                return asd;
            } else if (asd2 && comparedval == 0) {
                asd = zerofiedInputDate.after(today530) || zerofiedInputDate.equals(today530);
            } else {
                asd = true;
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods isFutureDateOr530. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static String maskID(String v) {
        String s = v;
        String firstC = "";
        String last4C = "";
        int fullLength = v.length();
        int maskLength = fullLength <= 5 ? 0 : fullLength - 5;
        String maskStr = "";
        try {
            if (maskLength <= 0) {
                s = v;
            } else {
                firstC = v.substring(0, 1);
                last4C = v.substring(fullLength - 4, fullLength);
                for (int i = 0; i <= maskLength - 1; i++) {
                    maskStr += "\u2022";
                }
                s = firstC + maskStr + last4C;
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods maskID. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return s;
        }
    }

    public static String getToggledGCID(SharedPreferences gcmp, boolean show, String currViewedTxtValue) {
        String id = "";
        try {
            id = show ? gcmp.getString("ID", "") : gcmp.getString("MaskedID", "");
            if (IsEmptyString(id) && !IsEmptyString(currViewedTxtValue)) {
                id = currViewedTxtValue;
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods getToggledGCID. Exception: " + e.getMessage(), "", "", "", "");
            id = currViewedTxtValue;
        } finally {
            return id;
        }
    }

    public static String rtnStringFromStringArrayList(ArrayList al) {
        String s = "";
        try {
            if (!IsEmptyArray(al)) {
                for (int i = 0; i <= al.size() - 1; i++) {
                    s += (i == 0) ? al.get(i) : "," + al.get(i);
                }
            }
        } catch (Exception e) {
            s = "";
            writeToFile("Exception at objGlobalMethods rtnStringFromStringArrayList. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return s;
        }
    }

    public static boolean IsEqualString(String val1, String val2) {
        boolean asd = false;
        try {
            if (!IsEmptyString(val1) && !IsEmptyString(val2)) {
                asd = val1.toUpperCase().equals(val2.toUpperCase());
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods IsEqualString. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean IsEmptyArray(ArrayList al) {
        boolean asd = false;
        try {
            asd = (al == null) || (al != null && al.size() == 0);
        } catch (Exception e) {
            asd = true;
            writeToFile("Exception at objGlobalMethods IsEmptyArray. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean UserCanScreenShot(SharedPreferences gcmp) {
        boolean asd = false;
        String useridx = "";
        try {
            useridx = objGlobalMethods.getGCMPrefs(gcmp, "IDX");
            asd = (!objGlobalMethods.IsEmptyString(useridx) &&
                    (
                            /*objGlobalMethods.IsEqualString(useridx, "1294") ||
                                    objGlobalMethods.IsEqualString(useridx, "1767") ||
                                    objGlobalMethods.IsEqualString(useridx, "2084") ||
                                    objGlobalMethods.IsEqualString(useridx, "304")*/
                            objGlobalMethods.IsEqualString(useridx, "1294") ||
                                    objGlobalMethods.IsEqualString(useridx, "2084") ||
                                    objGlobalMethods.IsEqualString(useridx, "304")
                    )
            );
            //asd =  (!objGlobalMethods.IsEmptyString(useridx) && (objGlobalMethods.IsEqualString(useridx, "1294") || objGlobalMethods.IsEqualString(useridx, "2084")));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods UserCanScreenShot. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean isKeanHill(SharedPreferences gcmp) {
        boolean asd = false;
        String useridx = "";
        try {
            useridx = objGlobalMethods.getGCMPrefs(gcmp, "IDX");
            /*//asd = (!objGlobalMethods.IsEmptyString(useridx) && (objGlobalMethods.IsEqualString(useridx, "1294") || objGlobalMethods.IsEqualString(useridx, "1767")));
            asd = (!objGlobalMethods.IsEmptyString(useridx) && (objGlobalMethods.IsEqualString(useridx, "1294")));
            //asd =  (!objGlobalMethods.IsEmptyString(useridx) && (objGlobalMethods.IsEqualString(useridx, "1767")));*/
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods isKeanHill. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean IsEmptyString(String val) {
        boolean asd = false;
        try {
            if (val == null) {
                val = "";
            }
            asd = val.isEmpty() || val.equals("") || val == "";
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods IsEmptyString. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static ArrayList sentenceHasValidContactNo(String v) {
        ArrayList MainAL = new ArrayList();
        ArrayList al = new ArrayList();
        String suspectedWord = "";
        int nxtIdx = -1;
        int startIDX = -1;
        int endIDX = -1;
        char c;
        char nextc;
        boolean isDigit = false;
        boolean isDigit2 = false;
        try {

            for (int i = 0; i <= v.length() - 1; i++) {
                isDigit = false;
                c = v.charAt(i);
                if (Character.isDigit(c)) {
                    suspectedWord += c;
                    isDigit = true;
                }
                if (isDigit) {
                    nxtIdx = i + 1;
                    if (nxtIdx > v.length() - 1) {
                        //end of sentence alr
                        //chk if suspectedWord length == 8 and is validNum\
                        if (suspectedWord.length() == 8 && validSGTelNum(suspectedWord, false)) {
                            al = new ArrayList();
                            startIDX = i - 8;
                            endIDX = i;
                            al.add(suspectedWord);
                            al.add(startIDX);
                            al.add(endIDX);
                            MainAL.add(al);
                            suspectedWord = "";
                        }
                    } else if (nxtIdx <= v.length() - 1) {
                        nextc = v.charAt(nxtIdx);
                        isDigit2 = Character.isDigit(nextc);
                        if (isDigit2 && suspectedWord.length() == 8) {
                            suspectedWord = "";
                        } else if (!isDigit2 && suspectedWord.length() == 8) {
                            al = new ArrayList();
                            startIDX = i - 8;
                            endIDX = i;
                            al.add(suspectedWord);
                            al.add(startIDX);
                            al.add(endIDX);
                            MainAL.add(al);
                            suspectedWord = "";
                        } else if (isDigit2 && suspectedWord.length() < 8) {
                            //continue
                            continue;
                        } else if (!isDigit2 && suspectedWord.length() < 8) {
                            suspectedWord = "";
                            continue;
                        }
                    }
                }
            }
        } catch (Exception e) {
            MainAL = new ArrayList();
            writeToFile("Exception at objGlobalMethods sentenceHasValidContactNo. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return MainAL;
        }
    }

    public static String[] rtnSplitStringArr(String s, String delimeter) {
        String[] arr = null;
        try {
            arr = s.split(delimeter);
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods rtnSplitStringArr. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return arr;
        }
    }

    public static objAuthDriverResult getGCdetails(SharedPreferences prefs) {
        objAuthDriverResult res = new objAuthDriverResult();
        Gson gson = new Gson();
        byte[] Image = null;
        String ImageJsonStr = "";
        try {
            if (prefs == null) {
                prefs = getGcmPreferences();
            }
            res.setFullName(prefs.getString("FullName", ""));
            try {
                ImageJsonStr = prefs.getString("Image", "");
                if (!ImageJsonStr.isEmpty() && !ImageJsonStr.equals("") && !ImageJsonStr.equals("[]")) {
                    Image = gson.fromJson(ImageJsonStr, byte[].class);
                }
                res.setImage(Image);
            } catch (Exception e) {
                writeToFile("Exception at objGlobalMethods getGCdetails converting GcImage. Exception: " + e.getMessage(), "", "", "", "");
            }

            res.setID(prefs.getString("ID", ""));
            res.setMaskedID(prefs.getString("MaskedID", ""));
            res.setProbationGC(prefs.getString("probationGC", ""));
            res.setDateConfirmed(prefs.getString("probationPeriod", ""));
            String DriverFlag = prefs.getString("DriverFlag", "");
            res.setFlag(!objGlobalMethods.IsEmptyString(DriverFlag) ? Integer.parseInt(DriverFlag) : 0);
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods getGCdetails. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return res;
        }
    }

    public static boolean intentServierIsRunning(String serviceName) {
        boolean asd = false;
        Context ctx = myApp.getAppContext();
        try {
            ActivityManager manager = (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                //if ("com.xdel.luke.BCnSVC.SendQueuedEmail_IntentService".equals(service.service.getClassName())) {
                if (serviceName.equals(service.service.getClassName())) {
                    asd = true;
                    break;
                }
            }
        } catch (Exception e) {
            asd = false;
            writeToFile("Exception at objGlobalMethods intentServierIsRunning. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static void Blink(View v, boolean blink) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        try {
            if (blink) {
                v.startAnimation(anim);
            } else {
                v.clearAnimation(); // cancel blink animation
                v.setAlpha(1.0f); // restore original alpha
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods Blink. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static boolean isEven(int pos) {
        return pos % 2 == 0;
    }

    public static boolean isToday(Date d) {
        boolean asd = false;
        Calendar today = Calendar.getInstance();
        try {

        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods isToday. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static Integer convertStringToInteger(String val) {
        int intv = 0;
        try {
            intv = Integer.parseInt(val);
        } catch (Exception e) {
            intv = 0;
        } finally {
            return intv;
        }
    }

    public static String getDeviceResolution() {
        Context ctx = myApp.getAppContext();
        int density = ctx.getResources().getDisplayMetrics().densityDpi;
        switch (density) {
            case DisplayMetrics.DENSITY_MEDIUM:
                return "MDPI";
            case DisplayMetrics.DENSITY_HIGH:
                return "HDPI";
            case DisplayMetrics.DENSITY_LOW:
                return "LDPI";
            case DisplayMetrics.DENSITY_XHIGH:
                return "XHDPI";
            case DisplayMetrics.DENSITY_TV:
                return "TV";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "XXHDPI";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "XXXHDPI";
            case DisplayMetrics.DENSITY_560:
                return "XXXHDPI";
            default:
                return "Unknown";
        }
    }

    public static String getSizeName(Context context) {
        /*int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;*/

        if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            return "large";
        } else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            return "normal";
        } else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            return "small";
        } else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            return "xlarge";
        } else {
            return "undefined";
        }

        /*switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return "small";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return "normal";
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return "large";
            case 4: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
                return "xlarge";
            default:
                return "undefined";
        }*/
    }

    public static boolean isNearHendersonOfcNew(SharedPreferences gcmp) {
        boolean asd = false;
        Gson gson = new Gson();
        String HendersonLocationPrefStr = "";
        HendersonLocationCustom hlc = null;
        Location gpsLocationFD = null;
        try {
            gpsLocationFD = objGlobalMethods.getLastKnownLocationForJobUpdate();
            if (gcmp == null)
                gcmp = getGcmPreferences();
            float lat = gpsLocationFD != null ? (float) gpsLocationFD.getLatitude() : 0.00f;
            float lng = gpsLocationFD != null ? (float) gpsLocationFD.getLongitude() : 0.00f;
            HendersonLocationPrefStr = getGCMPrefs(gcmp, "HendersonLocationPrefStr");
            if (!HendersonLocationPrefStr.isEmpty()) {
                hlc = gson.fromJson(HendersonLocationPrefStr, HendersonLocationCustom.class);
                double calcdist = LocationService.calcDistance(lat, lng, hlc.lat, hlc.lng);
                if (calcdist <= 0.2) { //less than equal to 200m from office
                    asd = true;
                }
            }
         } catch (Exception e) {
             String s = e.getMessage().toString();
             writeToFile("Exception at objGlobalMethods isNearHendersonOfcNew. Exception: " + s, "", "", "", "");
         } finally {
             return asd;
         }
    }

    public static Boolean isNearHendersonOfc(float lat, float lng) {
        Boolean asd = false;
        Location HendersonOfcLocation = null;
        Gson gson = new Gson();
        String HendersonLocationPrefStr = "";
        HendersonLocationCustom hlc = null;

        try {
            SharedPreferences GcmPreferences = getGcmPreferences();
            HendersonLocationPrefStr = getGCMPrefs(GcmPreferences, "HendersonLocationPrefStr");
            if (!HendersonLocationPrefStr.isEmpty()) {
                /*HendersonOfcLocation = gson.fromJson(HendersonLocationPrefStr, Location.class);
                Location lastKnownLocation = getLastKnownLocationForJobUpdate();
                double calcdist = LocationService.calcDistance(lat, lng, HendersonOfcLocation.getLatitude(), HendersonOfcLocation.getLongitude());
                if (calcdist <= 0.2) { //less than equal to 200m from office
                    asd = true;
                }*/

                hlc = gson.fromJson(HendersonLocationPrefStr, HendersonLocationCustom.class);
                double calcdist = LocationService.calcDistance(lat, lng, hlc.lat, hlc.lng);
                if (calcdist <= 0.2) { //less than equal to 200m from office
                    asd = true;
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods isNearHendersonOfc. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static void clearAllUnwantedActiveActivities(Context ctx) {
        try {

        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods clearAllUnwantedActiveActivities. Exception: " + s, "", "", "", "");
        }
    }

    public static boolean isEditingRoute() {
        boolean asd = false;
        try {
            SharedPreferences gcmp = getGcmPreferences();
            String editingvrp = getGCMPrefs(gcmp, "editingvrp");
            asd = !editingvrp.isEmpty() && !editingvrp.equals("") && editingvrp != "";
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods isEditingRoute. Exception: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean ifDeviceTypeCheckedOut(ArrayList<objMPD_Info> mpds, int DeviceTypeToCheck) {
        boolean asd = false;
        try {
            for (int i = 0; i <= mpds.size() - 1; i++) {
                if (mpds.get(i).Device_Type == DeviceTypeToCheck) {
                    asd = true;
                    break;
                }
            }
        } catch (Exception e) {
            String s = e.getMessage();
            writeToFile("Exception at objGlobalMethods ifDeviceTypeCheckedOut. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static void promptReqMPD_New() {
        try {
            Context ctx = myApp.getAppContext();
            ArrayList<String> mpdal = rtnReqMPDnames();
            boolean hasNFCjobsBool = hasNFCjobs();
            String RegBTTLmsg = mpdclass.promptRegBTTL_New();
            if (mpdal != null && mpdal.size() > 0) {
                showMpd_DialogNew(mpdal);
            } else if (!objGlobalMethods.IsEmptyString(RegBTTLmsg)) {
                objGlobalMethods.showRegBTTL_DialogNew(RegBTTLmsg, true);
            } else if (hasNFCjobsBool) {
             int pageid = 0; //not link to anything in the entire solution
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class) {
                    pageid = 1;
                }
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                    pageid = 2;
                }
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListNewActivity.class) {
                    pageid = 3;
                }
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListRoutingActivity.class) {
                    pageid = 4;
                }
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DashboardSubActivity.class) {
                    pageid = 5;
                }
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobsListGroupActivity.class) {
                    pageid = 6;
                }
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MapsAllActivity.class) {
                    pageid = 7;
                }

                NfcAdapter mNfcAdapter =
                        pageid == 1 ? ((DrawerMainActivity) myApp.currentActivity).mNfcAdapter :
                                pageid == 2 ? ((DrawerMain2Activity) myApp.currentActivity).mNfcAdapter :
                                        pageid == 3 ? ((AddressListNewActivity) myApp.currentActivity).mNfcAdapter :
                                                pageid == 4 ? ((AddressListRoutingActivity) myApp.currentActivity).mNfcAdapter :
                                                        pageid == 5 ? ((DashboardSubActivity) myApp.currentActivity).mNfcAdapter :
                                                                pageid == 6 ? ((JobsListGroupActivity) myApp.currentActivity).mNfcAdapter : null;
                SharedPreferences gcmp = getGcmPreferences();
                //nfc capabilities: 0 == no nfc hardware, 1 == enabled, 2 == not enabled
                if (mNfcAdapter == null && pageid != 7) {
                    //no nfc hardware
                    //showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.nonfchardware)).show();
                    objGlobalMethods.setGCMPrefs(gcmp, "nfc_cap", "0");
                } else if (mNfcAdapter != null && myApp.isNfcEnabled(mNfcAdapter, ctx)) {
                    //nfc enabled
                    //do nothing
                    objGlobalMethods.setGCMPrefs(gcmp, "nfc_cap", "1");
                } else if (mNfcAdapter != null && !myApp.isNfcEnabled(mNfcAdapter, ctx)) {
                    //nfc not enabled
                    objGlobalMethods.setGCMPrefs(gcmp, "nfc_cap", "2");
                    showEnableNFC_Dialog().show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods promptReqMPD_New. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void promptReqMPD() {
        try {
            Context ctx = myApp.getAppContext();
            ArrayList<String> mpdal = rtnReqMPDnames();
            boolean hasNFCjobsBool = hasNFCjobs();
            if (mpdal != null && mpdal.size() > 0) {
                //showMPD_Dialog(mpdal).show();
                showMpd_DialogNew(mpdal);
            } else if (hasNFCjobsBool) {
                int pageid = 0; //not link to anything in the entire solution
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class) {
                    pageid = 1;
                }
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                    pageid = 2;
                }
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListNewActivity.class) {
                    pageid = 3;
                }
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListRoutingActivity.class) {
                    pageid = 4;
                }
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DashboardSubActivity.class) {
                    pageid = 5;
                }
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobsListGroupActivity.class) {
                    pageid = 6;
                }
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MapsAllActivity.class) {
                    pageid = 7;
                }

                NfcAdapter mNfcAdapter =
                        pageid == 1 ? ((DrawerMainActivity) myApp.currentActivity).mNfcAdapter :
                                pageid == 2 ? ((DrawerMain2Activity) myApp.currentActivity).mNfcAdapter :
                                        pageid == 3 ? ((AddressListNewActivity) myApp.currentActivity).mNfcAdapter :
                                                pageid == 4 ? ((AddressListRoutingActivity) myApp.currentActivity).mNfcAdapter :
                                                        pageid == 5 ? ((DashboardSubActivity) myApp.currentActivity).mNfcAdapter :
                                                                pageid == 6 ? ((JobsListGroupActivity) myApp.currentActivity).mNfcAdapter : null;
                SharedPreferences gcmp = getGcmPreferences();
                //nfc capabilities: 0 == no nfc hardware, 1 == enabled, 2 == not enabled
                if (mNfcAdapter == null && pageid != 7) {
                    //no nfc hardware
                    //showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.nonfchardware)).show();
                    objGlobalMethods.setGCMPrefs(gcmp, "nfc_cap", "0");
                } else if (mNfcAdapter != null && myApp.isNfcEnabled(mNfcAdapter, ctx)) {
                    //nfc enabled
                    //do nothing
                    objGlobalMethods.setGCMPrefs(gcmp, "nfc_cap", "1");
                } else if (mNfcAdapter != null && !myApp.isNfcEnabled(mNfcAdapter, ctx)) {
                    //nfc not enabled
                    objGlobalMethods.setGCMPrefs(gcmp, "nfc_cap", "2");
                    showEnableNFC_Dialog().show();
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods promptReqMPD. Exception: " + s, "", "", "", "");
        }
    }

    public static boolean hasNFCjobs() {
        boolean asd = false;
        try {
            asd = DAO_Jobs.getPIPDIP_NFC_JobsCount() > 0;
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static ArrayList<String> rtnReqMPDnames() {
        SharedPreferences gcmp = getGcmPreferences();
        String savedFlagSumStr = "";
        String savedFlag2SumStr = "";
        int savedFlagSum = 0;
        int savedFlag2Sum = 0;
        int jobFlagSum = 0;
        int job2FlagSum = 0;
        int FlagSum = 0;
        int Flag2Sum = 0;
        ArrayList<Integer> FlagSumArr = null;
        ArrayList<Integer> Flag2SumArr = null;
        ArrayList<String> mpdal = new ArrayList();
        Context ctx = myApp.getAppContext();
        String mpdsPrefStr = "";
        ArrayList<objMPD_Info> mpds = null;
        try {
            savedFlagSumStr = getGCMPrefs(gcmp, "savedFlagSumStr");
            savedFlag2SumStr = getGCMPrefs(gcmp, "savedFlag2SumStr");
            /*mpdsPrefStr = getGCMPrefs(gcmp, "mpdsPrefStr");
            if (!mpdsPrefStr.isEmpty() && !mpdsPrefStr.equals("") && mpdsPrefStr != "") {
                Gson gson = new Gson();
                mpds = gson.fromJson(mpdsPrefStr, new TypeToken<List<objMPD_Info>>() {
                }.getType());
            }*/

            mpds = getMPDsALfromPrefStr();

            try {
                savedFlagSum = !IsEmptyString(savedFlagSumStr) ? Integer.parseInt(savedFlagSumStr) : 0;
                savedFlag2Sum = !IsEmptyString(savedFlag2SumStr) ? Integer.parseInt(savedFlag2SumStr) : 0;
            } catch (Exception e) {
                String s = e.getMessage().toString();
                writeToFile("Exception at objGlobalMethods promptReqMPD converting savedFlagSumStr, savedFlag2SumStr to int. Exception: " + s, "", "", "", "");
                savedFlagSum = 0;
            }
            FlagSumArr = DAO_Jobs.getFlagSumArr();
            if (FlagSumArr != null && FlagSumArr.size() > 0) {
                for (int i = 0; i <= FlagSumArr.size() - 1; i++) {
                    jobFlagSum = FlagSumArr.get(i);
                    if ((jobFlagSum & 4) == 4 && ((FlagSum & 4) != 4)) {
                        FlagSum += 4;
                    }
                    if ((jobFlagSum & 8) == 8 && ((FlagSum & 8) != 8)) {
                        FlagSum += 8;
                    }
                    if ((jobFlagSum & 16) == 16 && ((FlagSum & 16) != 16)) {
                        FlagSum += 16;
                    }
                }
            }

            if (FlagSum > 0 && FlagSum != savedFlagSum) {
                setGCMPrefs(gcmp, "savedFlagSumStr", String.valueOf(FlagSum));
                if ((FlagSum & 4) == 4) {
                    //nets
                    if (mpds != null && mpds.size() > 0) {
                        if (!ifDeviceTypeCheckedOut(mpds, 2)) {
                            mpdal.add(ctx.getString(R.string.mpdnets));
                        }
                    } else {
                        mpdal.add(ctx.getString(R.string.mpdnets));
                    }
                }
                if ((FlagSum & 8) == 8) {
                    //visa
                    if (mpds != null && mpds.size() > 0) {
                        if (!ifDeviceTypeCheckedOut(mpds, 1)) {
                            mpdal.add(ctx.getString(R.string.mpdcc));
                        }
                    } else {
                        mpdal.add(ctx.getString(R.string.mpdcc));
                    }
                }
                if ((FlagSum & 16) == 16) {
                    //trade in
                    /*if (mpds != null && mpds.size() > 0){

                    } else {
                        mpdal.add(ctx.getString(R.string.alert_ensurempddrawn));
                    }*/
                }
            } else if (FlagSum == 0) {
                mpdal = null;
                setGCMPrefs(gcmp, "savedFlagSumStr", "");
            }

            Flag2SumArr = DAO_Jobs.getFlag2SumArr();
            if (Flag2SumArr != null && Flag2SumArr.size() > 0) {
                for (int i = 0; i <= Flag2SumArr.size() - 1; i++) {
                    job2FlagSum = Flag2SumArr.get(i);
                    if ((job2FlagSum & 64) == 64 && ((Flag2Sum & 64) != 64)) {
                        Flag2Sum += 64;
                    }
                }
            }

            if (Flag2Sum > 0 && ((Flag2Sum != savedFlag2Sum) || (Flag2Sum == savedFlag2Sum))) {
                if (Flag2Sum != savedFlag2Sum)
                    setGCMPrefs(gcmp, "savedFlag2SumStr", String.valueOf(Flag2Sum));

                if ((Flag2Sum & 64) == 64) {
                    //TL
                    if (mpds != null && mpds.size() > 0) {
                        if (!ifDeviceTypeCheckedOut(mpds, 21) && !ifDeviceTypeCheckedOut(mpds, 22)) {
                            writeToFile("ifDeviceTypeCheckedOut != 21 or 22", "", "", "", "");
                            if (mpdal == null)
                                mpdal = new ArrayList<>();
                            mpdal.add(ctx.getString(R.string.mpdtl));
                        }
                    } else {
                        writeToFile("mpdal == null", "", "", "", "");
                        if (mpdal == null)
                            mpdal = new ArrayList<>();
                        mpdal.add(ctx.getString(R.string.mpdtl));
                    }
                }
            } else if (Flag2Sum == 0) {
                mpdal = null;
                setGCMPrefs(gcmp, "savedFlag2SumStr", "");
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods promptReqMPD. Exception: " + s, "", "", "", "");
            mpdal = null;
        } finally {
            return mpdal;
        }
    }

    public static ArrayList<objMPD_Info> processMPD() {
        return null;
    }

    public static ArrayList<objMPD_Info> getMPDsALfromPrefStr() {
        ArrayList<objMPD_Info> mpds = null;
        String mpdsPrefStr;
        Gson gson = new Gson();
        try {
            //SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            //mpdsPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "mpdsPrefStr");
            mpdsPrefStr = objGlobalMethods.getMPDRelatedSP_KeyValue("mpdsPrefStr");
            if (!mpdsPrefStr.isEmpty() && !mpdsPrefStr.equals("") && mpdsPrefStr != "" && !mpdsPrefStr.equals("[]") && mpdsPrefStr != "[]") {
                mpds = gson.fromJson(mpdsPrefStr, new TypeToken<List<objMPD_Info>>() {
                }.getType());
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods getMPDsALfromPrefStr. Exception: " + s, "", "", "", "");
            mpds = null;
        } finally {
            return mpds;
        }
    }

    public static ArrayList<objMPD_Info> grabMPDFromServer() {
        SharedPreferences gcmp = getGcmPreferences();
        String regid = getRegistrationId(null, gcmp);
        String[] MPD_Check_HoldingResult;
        ArrayList<objMPD_Info> mpds = null;
        objMPD_Info mpd = null;
        String mpdstr;
        String[] mpdstrsplit;
        try {
            MPD_Check_HoldingResult = WebService.MPD_Check_Holding(regid);
            if (MPD_Check_HoldingResult != null && MPD_Check_HoldingResult.length > 0) {
                for (int i = 0; i <= MPD_Check_HoldingResult.length - 1; i++) {
                    mpd = null;
                    mpdstr = MPD_Check_HoldingResult[i];
                    try {
                        mpdstrsplit = mpdstr.split("\\|");
                        if (mpdstrsplit != null && mpdstrsplit.length == 2) {
                            mpd = new objMPD_Info(Integer.parseInt(mpdstrsplit[0]), mpdstrsplit[1]);
                        }
                    } catch (Exception e) {
                        String s = e.getMessage().toString();
                        writeToFile("Exception at objGlobalMethods ReprocessMPDs spliting data. Exception: " + s, "", "", "", "");
                    }
                    if (mpd != null) {
                        if (mpds == null) {
                            mpds = new ArrayList<objMPD_Info>();
                        }
                        mpds.add(mpd);
                    }
                }
            } else if (MPD_Check_HoldingResult != null && MPD_Check_HoldingResult.length == 0) {
                mpds = new ArrayList<objMPD_Info>();
            }

            MPD_Group[] mgs = WebService.L2_Get_MPD_Groups(regid);
            if (mgs != null && mgs.length > 0) {
                Gson gson = new Gson();
                String MPDGroupsJsonStr = gson.toJson(mgs);
                //setGCMPrefs(gcmp, "MPDGroupsJsonStr", MPDGroupsJsonStr);
                setMPDRelatedSP_KeyValue("MPDGroupsJsonStr", MPDGroupsJsonStr);

                mpdclass mc = new mpdclass();
                mc.setMPDGroups(mgs);
                mc.setARegID(regid);
                mc.processMPDGroupNew();
            } else {
                String[] prefName = new String[]{"MPDGroupsJsonStr", "mdpsJsonStr"};
                String[] prefvalue = new String[]{"", ""};
                //objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);
                setMpdSPMass(prefName, prefvalue);

                String[] btprefName = new String[]{"lukeTLregistryListJsonStr"};
                String[] btprefvalue = new String[]{""};
                objGlobalMethods.setBTLoggerRelatedMass(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), btprefName, btprefvalue);
            }

            boolean hasBTTL = false;
            if (mpds != null && mpds.size() > 0) {
                for (int i = 0; i <= mpds.size() -1; i++) {
                    if (mpds.get(i).Device_Type == 22) {
                        hasBTTL = true;
                        break;
                    }
                }
            }

            objGlobalMethods.setBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "hasBTTL", hasBTTL ? "t" : "");

        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods grabSettingsFromServer. Exception: " + s, "", "", "", "");
        } finally {
            return mpds;
        }
    }

    public static void setMPDsPrefStr(ArrayList<objMPD_Info> mpds, SharedPreferences gcmp) {
        String mpdsPrefStr = "";
        try {
            if (mpds != null && mpds.size() > 0) {
                Gson gson = new Gson();
                mpdsPrefStr = gson.toJson(mpds);
            }
            setGCMPrefs(gcmp, "mpdsPrefStr", mpdsPrefStr);
            //writeToFile("setMPDsPrefStr completed successfully.", "", "", "", "");
        } catch (Exception e) {
            String s = e.getMessage();
            writeToFile("Exception at objGlobalMethods setMPDsPrefStr. Exception: " + s, "", "", "", "");
        }
    }

    public static void setMPDsPrefStrNew(ArrayList<objMPD_Info> mpds) {
        String mpdsPrefStr = "";
        try {
            if (mpds != null && mpds.size() > 0) {
                Gson gson = new Gson();
                mpdsPrefStr = gson.toJson(mpds);
            }
            setMPDRelatedSP_KeyValue("mpdsPrefStr", mpdsPrefStr);
        } catch (Exception e) {
            String s = e.getMessage();
            writeToFile("Exception at objGlobalMethods setMPDsPrefStrNew. Exception: " + s, "", "", "", "");
        }
    }

    public static void grabSettingsFromServerUponLogin() {
        SharedPreferences gcmp = getGcmPreferences();
        String regid = getRegistrationId(null, gcmp);
        try {
            objCabinet Cabinet = null;
            Cabinet = WebService.GetCabinet(regid, "1");
            objGlobalMethods.setSettings2(Cabinet == null ? "" : Cabinet.Data.isEmpty() || Cabinet.Data.equals("") || Cabinet.Data == "" ? "" : Cabinet.Data);
            writeToFile("grabSettingsFromServerUponLogin completed successfully.", "", "", "", "");
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods grabSettingsFromServerUponLogin. Exception: " + s, "", "", "", "");
        }
    }

    public static void grabSettingsFromServer() {
        SharedPreferences gcmp = getGcmPreferences();
        String regid = getRegistrationId(null, gcmp);
        try {
            objCabinet Cabinet = null;
            Cabinet = WebService.GetCabinet(regid, "1");
            objGlobalMethods.setSettings(Cabinet == null ? "" : Cabinet.Data.isEmpty() || Cabinet.Data.equals("") || Cabinet.Data == "" ? "" : Cabinet.Data);
            //writeToFile("grabSettingsFromServer completed successfully.", "", "", "", "");
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods grabSettingsFromServer. Exception: " + s, "", "", "", "");
        }
    }

    public static String getDefSetsJsonStr() {
        String asd = "";
        settings defSets = new settings();
        Gson gson = new Gson();
        try {
            asd = gson.toJson(defSets);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods getDefSetsJsonStr. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static String getFinalSetsJsonStr() {
        String FinalSetsJsonStr = "";
        SharedPreferences gcmp = getGcmPreferences();
        try {
            FinalSetsJsonStr = getGCMPrefs(gcmp, "finalSettingsStr");
            if (FinalSetsJsonStr.isEmpty() || FinalSetsJsonStr.equals("") || FinalSetsJsonStr == "") {
                FinalSetsJsonStr = getDefSetsJsonStr();
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods getFinalSetsJsonStr. Exception: " + s, "", "", "", "");
        } finally {
            return FinalSetsJsonStr;
        }
    }

    public static void setSettings2(String settingsStr) {
        String DefSetsJsonStr = getDefSetsJsonStr();
        settings serverSets = null;
        settings finalSets = new settings();
        String finalSettingsStr = "";
        Gson gson = new Gson();
        SharedPreferences gcmp = getGcmPreferences();
        try {
            SharedPreferences updateJobInfoPref = getUpdateJobInfoPreferences();
            String regid = getRegistrationId(myApp.getAppContext(), gcmp);
            String[] UsernamePwd = new String[4];
            String useridx;
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
            useridx = UsernamePwd[2];
            Long l = new Date().getTime();
            String timestampstr = "/Date(" + l + ")/";

            if (!settingsStr.isEmpty() && !settingsStr.equals("") && settingsStr != "") {
                try {
                    serverSets = gson.fromJson(settingsStr, settings.class);
                    finalSets = serverSets;
                    finalSettingsStr = gson.toJson(serverSets);
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                    writeToFile("Exception at objGlobalMethods setSettings2 converting serverSets to settings obj. Exception: " + s, "", "", "", "");
                }
            } else {
                finalSettingsStr = DefSetsJsonStr;
                /*objCabinet cabinet = new objCabinet(0, Long.parseLong(!useridx.isEmpty() && !useridx.equals("") && useridx != "" ? useridx : "0"), "1", DefSetsJsonStr, timestampstr);
                JobsHandler.addUpdateCabinetQueALPrefs(updateJobInfoPref, regid, cabinet);*/
            }

            finalSettingsStr = finalSettingsStr.isEmpty() || finalSettingsStr.equals("") || finalSettingsStr == "" ? DefSetsJsonStr : finalSettingsStr;

            objCabinet cabinet = new objCabinet(0, Long.parseLong(!useridx.isEmpty() && !useridx.equals("") && useridx != "" ? useridx : "0"), "1", finalSettingsStr, timestampstr);
            JobsHandler.addUpdateCabinetQueALPrefs(updateJobInfoPref, regid, cabinet);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods setSettings2. Exception: " + s, "", "", "", "");
            finalSettingsStr = DefSetsJsonStr;
            finalSets = new settings();
        } finally {
            setGCMPrefs(gcmp, "finalSettingsStr", finalSettingsStr);
            saveIndividualSettingsPref(finalSets);
        }
    }

    public static void setSettings(String settingsStr) {
        String DefSetsJsonStr = getDefSetsJsonStr();
        settings serverSets = null;
        settings finalSets = new settings();
        String finalSettingsStr = "";
        Gson gson = new Gson();
        SharedPreferences gcmp = getGcmPreferences();
        try {
            if (!settingsStr.isEmpty() && !settingsStr.equals("") && settingsStr != "") {
                try {
                    serverSets = gson.fromJson(settingsStr, settings.class);
                    finalSettingsStr = gson.toJson(serverSets);
                    finalSets = serverSets;
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                    writeToFile("Exception at objGlobalMethods setSettings converting serverSets to settings obj. Exception: " + s, "", "", "", "");
                }
            }
            finalSettingsStr = finalSettingsStr.isEmpty() || finalSettingsStr.equals("") || finalSettingsStr == "" ? DefSetsJsonStr : finalSettingsStr;
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods setSettings. Exception: " + s, "", "", "", "");
            finalSettingsStr = DefSetsJsonStr;
            finalSets = new settings();
        } finally {
            setGCMPrefs(gcmp, "finalSettingsStr", finalSettingsStr);
            saveIndividualSettingsPref(finalSets);
        }
    }

    public static settings getSettings() {
        settings sets = null;
        settings defSets = new settings();
        Gson gson = new Gson();
        String finalSettingsStr = "";
        SharedPreferences gcmp = getGcmPreferences();
        try {
            finalSettingsStr = getGCMPrefs(gcmp, "finalSettingsStr");
            sets = finalSettingsStr.isEmpty() || finalSettingsStr.equals("") || finalSettingsStr == "" ? defSets : gson.fromJson(finalSettingsStr, settings.class);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods getSettings. Exception: " + s, "", "", "", "");
            sets = defSets;
        } finally {
            return sets;
        }
    }

    public static void saveIndividualSettingsPref(settings sets) {
        String GPSalarmInterval = "";
        String GPSsendingIntervalCount = "";
        String ActDeactSpic = "";
        String IsExpressGC = "";
        String pagemode = "";
        String isVRPbySettings = "";
        SharedPreferences gcmp = getGcmPreferences();
        try {
            GPSalarmInterval = sets.GpsSendingInterval == 0 || sets.GpsSendingIntervalCount == 0 ? "" : String.valueOf(sets.GpsSendingInterval);
            GPSsendingIntervalCount = sets.GpsSendingInterval == 0 || sets.GpsSendingIntervalCount == 0 ? "" : String.valueOf(sets.GpsSendingIntervalCount);
            ActDeactSpic = String.valueOf(sets.SignatureIA ? 1 : 0);
            IsExpressGC = String.valueOf(sets.IsExpressGC);
            pagemode = sets.FollowVRP || JobsHandler.hasOpsQ() || objGlobalMethods.IsDefaultVRPpage() ? "vrp" : "dashboard";
            isVRPbySettings = sets.FollowVRP ? "y" : "";
            SharedPreferences.Editor editor = gcmp.edit();
            editor.putString("GPSalarmInterval", GPSalarmInterval);
            editor.putString("GPSsendingIntervalCount", GPSsendingIntervalCount);
            editor.putString("ActDeactSpic", ActDeactSpic);
            editor.putString("IsExpressGC", IsExpressGC);
            editor.putString("pagemode", pagemode);
            editor.putString("isVRPbySettings", isVRPbySettings);
            editor.apply();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods saveIndividualSettingsPref. Exception: " + s, "", "", "", "");
        }
    }

    public static String getMobileOrWifiSignalStrength() {
        String asd = "Signal Strength: ";
        ConnectivityManager cm = null;
        NetworkInfo activeNetwork = null;
        Context ctx = null;
        try {
            ctx = myApp.getAppContext();
            cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnected()) {
                WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifi = wifiManager.getConnectionInfo();
                if (wifi != null) {
                    int rssi = wifi.getRssi();
                    asd += rssi <= 0 && rssi >= -50 ? "Excellent" : rssi < -50 && rssi >= -70 ? "Good" : rssi < -70 && rssi >= -80 ? "Fair" : rssi < -80 && rssi >= -100 ? "Weak" : "No Signal";
                } else {
                    asd += "Err";
                }
            } else if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE && activeNetwork.isConnected()) {
                TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
                    if (cellInfos != null) {
                        for (int i = 0; i < cellInfos.size(); i++) {
                            if (cellInfos.get(i).isRegistered()) {
                                if (cellInfos.get(i) instanceof CellInfoWcdma) {
                                    CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) telephonyManager.getAllCellInfo().get(0);
                                    CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                                    int dbm = cellSignalStrengthWcdma.getDbm();
                                    asd += dbm <= 0 && dbm >= -95 ? "Excellent" : dbm < -95 && dbm >= -103 ? "Good" : dbm < -103 && dbm >= -108 ? "Fair"
                                            : dbm < -108 && dbm >= -116 ? "Weak" : "No Signal";
                                    //asd += "Mobile data: " + String.valueOf(cellSignalStrengthWcdma.getDbm());
                                } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                                    CellInfoGsm cellInfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
                                    CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                                    int dbm = cellSignalStrengthGsm.getDbm();
                                    asd += dbm <= 0 && dbm >= -95 ? "Excellent" : dbm < -95 && dbm >= -103 ? "Good" : dbm < -103 && dbm >= -108 ? "Fair"
                                            : dbm < -108 && dbm >= -116 ? "Weak" : "No Signal";
                                    //asd += "Mobile data: " + String.valueOf(cellSignalStrengthGsm.getDbm());
                                } else if (cellInfos.get(i) instanceof CellInfoLte) {
                                    CellInfoLte cellInfoLte = (CellInfoLte) telephonyManager.getAllCellInfo().get(0);
                                    CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                                    int dbm = cellSignalStrengthLte.getDbm();
                                    asd += dbm <= 0 && dbm >= -95 ? "Excellent" : dbm < -95 && dbm >= -105 ? "Good" : dbm < -105 && dbm >= -115 ? "Fair"
                                            : dbm < -115 && dbm >= -120 ? "Weak" : "No Signal";
                                    //asd += "Mobile data: " + String.valueOf(cellSignalStrengthLte.getDbm());
                                } else if (cellInfos.get(i) instanceof CellInfoCdma) {
                                    CellInfoCdma cellInfoCdma = (CellInfoCdma) telephonyManager.getAllCellInfo().get(0);
                                    CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                                    int dbm = cellSignalStrengthCdma.getDbm();
                                    asd += dbm <= 0 && dbm >= -95 ? "Excellent" : dbm < -95 && dbm >= -103 ? "Good" : dbm < -103 && dbm >= -108 ? "Fair"
                                            : dbm < -108 && dbm >= -116 ? "Weak" : "No Signal";
                                    //asd += "Mobile data: " + String.valueOf(cellSignalStrengthCdma.getDbm());
                                }
                            }
                        }
                    }

                } catch (SecurityException se) {

                }
            } else {
                asd += "Err";
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods getMobileOrWifiSignalStrength. Exception: " + s, "", "", "", "");
            asd += "Err";
        } finally {
            return asd;
        }
    }

    public static String getSettingsJsonStr(SharedPreferences gcmp) {
        String savedstr = "";
        String asd = "";
        settings sets;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            savedstr = getGCMPrefs(gcmp, "settingsjson");
            if (savedstr.isEmpty() || savedstr.equals("") || savedstr == "") {
                sets = new settings();
            } else {
                try {
                    Gson gs = new Gson();
                    sets = gs.fromJson(savedstr, settings.class);
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                    writeToFile("Exception at objGlobalMethods getSettingsJsonStr converting settingsjson. Exception: " + s, "", "", "", "");
                    sets = new settings();
                }
            }
            sets.setSettingsPWD("********");
            asd = gson.toJson(sets);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods getSettingsJsonStr. Exception: " + s, "", "", "", "");
            sets = new settings();
            asd = gson.toJson(sets);
        } finally {
            return asd;
        }
    }

    public static boolean verifySettingsPwd(String pwd) {
        boolean asd = false;
        try {
            settings sets = new settings();
            asd = sets.getSettingsPWD().equals(pwd);
        } catch (Exception e) {
            asd = false;
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods verifySettingsPwd. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean verifyCreateCommunalFilePwd(String pwd) {
        boolean asd = false;
        try {
            settings sets = new settings();
            asd = objGlobalMethods.IsEqualString(pwd, "XDel9911");
        } catch (Exception e) {
            asd = false;
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods verifyCreateCommunalFilePwd. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static void offSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) myApp.getAppContext().getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.isSpeakerphoneOn()) {
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(false);
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods offSpeaker. Exception: " + s, "", "", "", "");
        }
    }

    public static String getDeviceInfo() {
        String sss = "";
        try {
            String manu = Build.MANUFACTURER;
            String brand = Build.BRAND;
            String device = Build.DEVICE;
            String product = Build.PRODUCT;
            //String serial = "NA";
            String imei1 = "NA";
            String imei2 = "NA";
            final TelephonyManager telephonyManager = (TelephonyManager) myApp.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Todo Don't forget to ask the permission
                if (ContextCompat.checkSelfPermission(myApp.getAppContext(), android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    /*try {
                        serial = Build.getSerial();
                    } catch (Exception e) {
                        String s = e.getMessage().toString();
                    }*/

                    try {
                        imei1 = telephonyManager.getImei(0);
                    } catch (Exception e) {
                        String s = e.getMessage().toString();
                    }
                    try {
                        imei2 = telephonyManager.getImei(1);
                    } catch (Exception e) {
                        String s = e.getMessage().toString();
                    }
                }
            } else {
                /*try {
                    serial = Build.SERIAL;
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                }*/

                try {
                    imei1 = telephonyManager.getDeviceId(0);
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                }
                try {
                    imei2 = telephonyManager.getDeviceId(1);
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                }
            }

            if (manu != null && !manu.isEmpty() && !manu.equals("") && manu != "") {
                sss += "Manufacturer: " + manu + "\n";
            }
            if (brand != null && !brand.isEmpty() && !brand.equals("") && brand != "") {
                sss += "Brand: " + brand + "\n";
            }
            if (device != null && !device.isEmpty() && !device.equals("") && device != "") {
                sss += "Device: " + device + "\n";
            }
            if (product != null && !product.isEmpty() && !product.equals("") && product != "") {
                sss += "Product: " + product + "\n";
            }
            /*if (serial != null && !serial.isEmpty() && !serial.equals("") && serial != "") {
                sss += "Serial: " + serial + "\n";
            }*/
            if (imei1 != null && !imei1.isEmpty() && !imei1.equals("") && imei1 != "") {
                sss += "Imei 1: " + imei1 + "\n";
            }
            if (imei2 != null && !imei2.isEmpty() && !imei2.equals("") && imei2 != "") {
                sss += "Imei 2: " + imei2 + "\n";
            }

            /*sss = "Manufacturer: " + manu + "\nBrand: " + brand + "\nDevice: " + device + "\nProduct: " + product
                    + "\nSerial: " + serial + "\nImei 1: " + imei1  + "\nImei 2: " + imei2;*/
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods getDeviceInfo. Exception: " + s, "", "", "", "");
            sss = "NA";
        } finally {
            return sss;
        }
    }

    public static boolean CallLogContainsConnectingText(String InitialMsg) {
        boolean asd = false;
        try {
            asd = InitialMsg.contains("Connecting/Ringing");
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods CallLogContainsConnectingText. Exception: " + e.getMessage(), "", "", "", "");
            asd = true;
        } finally {
            return asd;
        }
    }

    public static String composeCallLog(String InitialMsg, String NewMsg) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM HH:mm:ss");
        Date dt = new Date();
        String currDateTime = formatter.format(dt);
        String fullmsg = "";
        String FinalMsg = InitialMsg == null || InitialMsg.equals("null") ? "" : InitialMsg;
        try {
            fullmsg = currDateTime + " " + NewMsg;
            if (!FinalMsg.contains(fullmsg)) {
                FinalMsg += FinalMsg != null && !FinalMsg.isEmpty() && !FinalMsg.equals("") && FinalMsg != "" ? "\n" + fullmsg : fullmsg;
                SharedPreferences gcmp = getGcmPreferences();
                setGCMPrefs(gcmp, "CallLogFullMsg", FinalMsg);
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods composeCallLog. Exception: " + s, "", "", "", "");
        } finally {
            return FinalMsg;
        }
    }

    public static void UpdateCallLogCallEnded() {
        SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        Long JobsIDX = null;
        /*String JobsIDXstr = "";
        String tel = "";*/
        try {
            String CallJobsIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, "CallJobsIDX");
            String Calltel = objGlobalMethods.getGCMPrefs(GcmPreferences, "Calltel");
            String Callext = objGlobalMethods.getGCMPrefs(GcmPreferences, "Callext");
            String callOfc = getGCMPrefs(GcmPreferences, "callOfc");

            try {
                JobsIDX = Long.parseLong(CallJobsIDX);
            } catch (Exception e) {
                JobsIDX = Long.parseLong("0");
            }

            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            CallActivity.log = objGlobalMethods.getGCMPrefs(gcmp, "CallLogFullMsg");
            if (JobsIDX > 0 && CallActivity.log != null && !CallActivity.log.isEmpty() && !CallActivity.log.equals("") && CallActivity.log != "") {
                String timestampstr = objGlobalMethods.rtntimestampstr();
                Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                String regid = objGlobalMethods.getRegistrationId(myApp.getAppContext(), GcmPreferences);
                JobsHandler.addUpdateJobInfoCallLogsQueALPrefs(UpdateJobInfoPreferences, regid, JobsIDX, 388, timestampstr,
                        CallActivity.log, "",
                        lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

                writeToFile("Call ended with " + Calltel + ".", "JobsIDX: " + CallJobsIDX, "Submitted StatusCodeIDX: 388", "", "");
                Context ctx = myApp.getAppContext();
                ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            }

        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods UpdateCallLogCallEnded. Exception: " + s, "", "", "", "");
        }
    }

    public static void insertCallLog(SharedPreferences GcmPreferences, boolean connected) {
        try {
            String Calltel = objGlobalMethods.getGCMPrefs(GcmPreferences, "Calltel");
            int catType = objGlobalMethods.getDashboard2ActivityCategoryType(objGlobalMethods.getGcmPreferences());
            //CallActivity.callloginserted = true;
            //setGCMPrefs(GcmPreferences, "callloginserted", "t");
            DAO_Jobs.insertCallLog(Calltel, new Date().getTime(), catType, false, connected);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods insertCallLog. Exception: " + s, "", "", "", "");
        }
    }

    public static int hasMultiValidSGTelNum(String tel) {
        int NumOfValid = 0;
        boolean asd = false;
        boolean asd2 = false;
        String newtel = "";
        int c = 0;
        try {
            tel = tel.replaceAll("\\s+", ""); // replace all spaces to empty string
            String[] array = tel.split("/");
            if (array != null && array.length > 0) {
                for (int i = 0; i <= array.length - 1; i++) {
                    asd2 = validSGTelNum(array[i], true);
                    NumOfValid += asd2 ? 1 : 0;
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods hasMultiValidSGTelNum. Exception: " + s, "", "", "", "");
            NumOfValid = 0;
        } finally {
            return NumOfValid;
        }
    }

    public static String rtnMultiValidSGTelNum(String tel) {
        String v = "";
        String t = "";
        boolean asd = false;
        String newtelwExt = "";
        String firsttel = "";
        int reservedTelCharCount = 0;
        try {
            tel = tel.replaceAll("\\s+", ""); // replace all spaces to empty string
            String[] array = tel.split("/");
            if (array != null && array.length > 0) {
                for (int i = 0; i <= array.length - 1; i++) {
                    asd = false;
                    t = "";
                    if (!array[i].isEmpty() && !array[i].equals("") && array[i] != "") {
                        /*t = array[i].replaceAll("\\s+", ""); // replace all spaces to empty string
                        t = (t.substring(0, 3).equals("+65")) ? t.substring(3, t.length()) : (t.substring(0, 2).equals("65")) ? t.substring(2, t.length()) : t;
                        asd = t.length() == 8 && t.matches("\\d+(?:\\.\\d+)?") &&
                                (t.substring(0, 1).equals("3") || t.substring(0, 1).equals("6") ||
                                        t.substring(0, 1).equals("8") || t.substring(0, 1).equals("9"));
                        if (asd) {
                            v += t + ",";
                        }*/

                        t = array[i];
                        asd = validSGTelNum(t, true);
                        if (asd) {
                            t = GetPhone(t);
                            if (i > 0 && asd && t.length() == 8) {
                                v += t + ",";
                            } else if (i > 0 && asd && t.length() > 0 && t.length() < 8 && !firsttel.isEmpty() && !firsttel.equals("")) {
                                reservedTelCharCount = firsttel.length() - t.length();
                                newtelwExt = firsttel.substring(0, reservedTelCharCount) + t;
                                v += newtelwExt + ",";
                            } else if (i == 0 && asd) {
                                firsttel = t;
                                v += t + ",";
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods rtnMultiValidSGTelNum. Exception: " + e.getMessage(), "", "", "", "");
            v = "";
        } finally {
            return v;
        }
    }

    public static String[] returnALlValidSGMobileNum(String tel) {
        String[] v = null;
        List<String> vl = new ArrayList<>();
        String rtnTel = "";
        boolean asd = false;
        String newtel = "";
        String newtelwExt = "";
        String firsttel = "";
        int reservedTelCharCount = 0;
        try {
            tel = tel.replaceAll("\\s+", ""); // replace all spaces to empty string
            String[] array = tel.split("/");
            if (array != null && array.length > 0) {
                for (int i = 0; i <= array.length - 1; i++) {
                    newtelwExt = "";
                    newtel = array[i];
                    asd = validSGMobileNum(newtel);

                    if (asd) {
                        newtel = GetPhone(newtel);
                        if (i > 0 && asd && newtel.length() == 8 && !vl.contains(newtel)) {
                            vl.add(newtel);
                        } else if (i > 0 && asd && newtel.length() > 0 && newtel.length() < 8 && !firsttel.isEmpty() && !firsttel.equals("")) {
                            reservedTelCharCount = firsttel.length() - newtel.length();
                            newtelwExt = firsttel.substring(0, reservedTelCharCount) + newtel;
                            if (!vl.contains(newtelwExt)) {
                                vl.add(newtelwExt);
                            }
                            //vl.add(newtelwExt);
                        } else if (i == 0 && asd) {
                            firsttel = newtel;
                            vl.add(newtel);
                        }
                    }
                }
            }

            if (vl != null && vl.size() > 0) {
                v = vl.toArray(new String[vl.size()]);
            }

        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods returnALlValidSGMobileNum. Exception: " + s, "", "", "", "");
            v = new String[]{""};
        } finally {
            return v;
        }
    }

    public static String[] returnAllValidSGTelNum(String tel, boolean callOfcExtension) {
        String[] v = null;
        List<String> vl = new ArrayList<>();
        String rtnTel = "";
        boolean asd = false;
        String newtel = "";
        String newtelwExt = "";
        String firsttel = "";
        int reservedTelCharCount = 0;
        try {
            tel = tel.replaceAll("\\s+", ""); // replace all spaces to empty string
            String[] array = tel.split("/");
            if (array != null && array.length > 0) {
                for (int i = 0; i <= array.length - 1; i++) {
                    newtelwExt = "";
                    newtel = array[i];
                    asd = validSGTelNum(newtel, callOfcExtension);

                    if (asd) {
                        //newtel = newtel.replaceAll("\\s+", ""); // replace all spaces to empty string
                        //newtel = rtnTelNum(newtel);
                        newtel = GetPhone(newtel);
                        //newtel = (newtel.length() > 4 && newtel.substring(0, 3).equals("+65")) ? newtel.substring(3, newtel.length()) : (newtel.length() > 4 && newtel.substring(0, 2).equals("65")) ? newtel.substring(2, newtel.length()) : newtel;
                        if (i > 0 && asd && newtel.length() == 8 && !vl.contains(newtel)) {
                            vl.add(newtel);
                        } else if (i > 0 && asd && newtel.length() > 0 && newtel.length() < 8 && !firsttel.isEmpty() && !firsttel.equals("")) {
                            reservedTelCharCount = firsttel.length() - newtel.length();
                            newtelwExt = firsttel.substring(0, reservedTelCharCount) + newtel;
                            if (!vl.contains(newtelwExt)) {
                                vl.add(newtelwExt);
                            }
                            //vl.add(newtelwExt);
                        } else if (i == 0 && asd) {
                            firsttel = newtel;
                            vl.add(newtel);
                        }
                    }

                    /*if (asd) {
                        vl.add(array[i]);
                    }*/
                }
            }

            if (vl != null && vl.size() > 0) {
                v = vl.toArray(new String[vl.size()]);
            }

        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods returnOneOfMultipleValidSGTelNum. Exception: " + s, "", "", "", "");
            v = new String[]{""};
        } finally {
            return v;
        }
    }

    public static String rtnAllValidTelNumbers(int catTypeID, objCheckedItems[] objCheckedItemsAL) {
        String ValidTelNumbers = "";
        objJobInfo ji = null;
        objContactStructure cs = null;
        boolean isZoneTelAndHasAuthRecBool = false;
        String tel = "";
        String others = "";
        String mobile = "";
        String tels = "";
        String otherss = "";
        String mobiles = "";
        String validtels = "";
        String validothers = "";
        String validmobiles = "";

        List<String> telList = new ArrayList();
        List<String> othersList = new ArrayList<>();
        List<String> mobileList = new ArrayList<>();

        try {
            if (objCheckedItemsAL != null && objCheckedItemsAL.length > 0) {
                for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                    ji = objCheckedItemsAL[i].jobinfo;
                    isZoneTelAndHasAuthRecBool = JobsHandler.isZoneTelAndHasAuthRec(ji);
                    cs = catTypeID == 0 && ji.PULocation.Contacts != null && ji.PULocation.Contacts.length > 0 ? ji.PULocation.Contacts[0] :
                            catTypeID == 1 && ji.DLRedirectedLocation != null && (isZoneTelAndHasAuthRecBool && ji.DLRedirectedLocation.Contacts != null && ji.DLRedirectedLocation.Contacts.length > 0) ?
                                    ji.DLRedirectedLocation.Contacts[0] : ji.DLLocation != null && ji.DLLocation.Contacts != null && ji.DLLocation.Contacts.length > 0 ? ji.DLLocation.Contacts[0] : null;
                    if (cs != null) {
                        tel = (cs.TEL != null && !cs.TEL.equals("")) ? objGlobalMethods.getNonSpacesTelNum(cs.TEL) : "";
                        others = (cs.OTHER != null && !cs.OTHER.equals("")) ? objGlobalMethods.getNonSpacesTelNum(cs.OTHER) : "";
                        mobile = (cs.MOBILE != null && !cs.MOBILE.equals("")) ? objGlobalMethods.getNonSpacesTelNum(cs.MOBILE) : "";
                        if (!tel.isEmpty() && !tel.equals("") && !telList.contains(tel)) {
                            telList.add(tel);
                        }
                        if (!others.isEmpty() && !others.equals("") && !othersList.contains(others)) {
                            othersList.add(others);
                        }
                        if (!mobile.isEmpty() && !mobile.equals("") && !mobileList.contains(mobile)) {
                            mobileList.add(mobile);
                        }
                    }
                }

                if (telList.size() > 0) {
                    for (int i = 0; i <= telList.size() - 1; i++) {
                        tels = telList.get(i);
                        validtels += rtnMultiValidSGTelNum(tels);
                    }
                    if (!validtels.isEmpty() && !validtels.equals("") && !validtels.equals(",") && validtels.length() > 2) {
                        validtels = validtels.substring(0, validtels.length() - 1);
                    }
                }

                if (othersList.size() > 0) {
                    for (int i = 0; i <= othersList.size() - 1; i++) {
                        otherss = othersList.get(i);
                        validothers += rtnMultiValidSGTelNum(otherss);
                    }
                    if (!validothers.isEmpty() && !validothers.equals("") && !validothers.equals(",") && validothers.length() > 2) {
                        validothers = validothers.substring(0, validothers.length() - 1);
                    }
                }

                if (mobileList.size() > 0) {
                    for (int i = 0; i <= mobileList.size() - 1; i++) {
                        mobiles = mobileList.get(i);
                        validmobiles += rtnMultiValidSGTelNum(mobiles);
                    }
                    if (!validmobiles.isEmpty() && !validmobiles.equals("") && !validmobiles.equals(",") && validmobiles.length() > 2) {
                        validmobiles = validmobiles.substring(0, validmobiles.length() - 1);
                    }
                }

                if (!validtels.isEmpty() && !validtels.equals("") && !validtels.equals(",")) {
                    ValidTelNumbers += validtels;
                }
                if (!validothers.isEmpty() && !validothers.equals("") && !validothers.equals(",")) {
                    if (!ValidTelNumbers.isEmpty()) {
                        ValidTelNumbers += "," + validothers;
                    } else {
                        ValidTelNumbers += validothers;
                    }
                }
                if (!validmobiles.isEmpty() && !validmobiles.equals("") && !validmobiles.equals(",")) {
                    if (!ValidTelNumbers.isEmpty()) {
                        ValidTelNumbers += "," + validmobiles;
                    } else {
                        ValidTelNumbers += validmobiles;
                    }
                }
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods rtnAllValidTelNumbers. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return ValidTelNumbers;
        }
    }

    public static String rtnTelNum(String v) {
        String t = "";
        String finaltel = "";
        try {
            for (char c : v.toCharArray()) {
                if (Character.isDigit(c)) {
                    finaltel += c;
                }
            }
        } catch (Exception e) {
            finaltel = "";
            writeToFile("Exception at objGlobalMethods rtnTelNum. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return finaltel;
        }
    }

    public static boolean validSGMobileNum(String tel) {
        boolean asd = false;
        try {
            if (!tel.isEmpty() && !tel.equals("") && tel != "") {
                tel = tel.replaceAll("\\s+", ""); // replace all spaces to empty string
                boolean isContactNumber = IsContactNumber(tel);
                boolean isMobile = IsMobile(tel);
                asd = isMobile;
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods validSGMobileNum. Exception: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean validSGTelNum(String tel, boolean acceptExtension) {
        boolean asd = false;
        String newtel = "";

        try {
            /*if (!tel.isEmpty() && !tel.equals("") && tel != "") {
                tel = tel.replaceAll("\\s+", ""); // replace all spaces to empty string
                tel = rtnTelNum(tel);

                newtel = (tel.length() > 4 && tel.substring(0, 3).equals("+65")) ? tel.substring(3, tel.length()) : (tel.length() > 4 && tel.substring(0, 2).equals("65")) ? tel.substring(2, tel.length()) : tel;

                asd = (
                        acceptExtension && newtel.length() > 0 && newtel.length() <= 4 && newtel.matches("\\d+(?:\\.\\d+)?")
                ) ||
                        (
                                newtel.length() == 8 && newtel.matches("\\d+(?:\\.\\d+)?") &&
                                        (newtel.substring(0, 1).equals("3") || newtel.substring(0, 1).equals("6") ||
                                                newtel.substring(0, 1).equals("8") || newtel.substring(0, 1).equals("9"))
                        );
            }*/
            if (!tel.isEmpty() && !tel.equals("") && tel != "") {
                tel = tel.replaceAll("\\s+", ""); // replace all spaces to empty string
                //tel = rtnTelNum(tel);
                boolean isContactNumber = IsContactNumber(tel);
                boolean isPhone = IsPhone(tel);
                boolean isMobile = IsMobile(tel);
                newtel = isContactNumber || isPhone || isMobile ? GetPhone(tel) : tel;

                //newtel = (tel.length() > 4 && tel.substring(0, 3).equals("+65")) ? tel.substring(3, tel.length()) : (tel.length() > 4 && tel.substring(0, 2).equals("65")) ? tel.substring(2, tel.length()) : tel;

                asd = (
                        acceptExtension && newtel.length() > 0 && newtel.length() <= 4 && newtel.matches("\\d+(?:\\.\\d+)?")
                ) ||
                        (
                                isContactNumber || isPhone || isMobile
                        );
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods validSGTelNum. Exception: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static String getNonSpacesTelNum(String tel) {
        String s = tel;
        try {
            s = tel.replaceAll("\\s+", "");
        } catch (Exception e) {
            s = tel;
        } finally {
            return s;
        }
    }

    public static String[] convertStringToArray(String str, String delimiter) {
        String[] list = null;
        try {
            if (!IsEmptyString(str) && !IsEmptyString(delimiter)) {
                list = str.split(delimiter);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods convertStringToArray. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return list;
        }
    }

    public static String GetPhone(String CT) {
        if (IsEmptyString(CT))
            return "";
        if (CT.length() == 8)
            return CT;

        String res = CT;
        if (res.startsWith("+65") || res.startsWith("65") && res.length() > 8) {
            if (res.startsWith("+65") && res.length() == 11)
                //res = res.Remove(0, 3);
                res = res.substring(3, res.length());
            else if (res.startsWith("65") && res.length() == 10)
                //res = res.Remove(0, 2);
                res = res.substring(2, res.length());
        }
        return res;
    }

    public static Boolean IsContactNumber(String AContactNumber) {
        String rct = GetPhone(AContactNumber);
        boolean isInt = false;
        Integer isIntValue = null;
        try {
            isIntValue = Integer.parseInt(rct);
        } catch (Exception e) {
            isIntValue = null;
        }
        isInt = isIntValue != null;
        return (rct.length() == 8) && isInt &&
                (rct.startsWith("6") || rct.startsWith("3") || rct.startsWith("8") ||
                        rct.startsWith("9"));
    }

    public static Boolean IsPhone(String AContactNumber) {
        String rct = GetPhone(AContactNumber);
        return (IsContactNumber(rct) && (rct.startsWith("6") || rct.startsWith("3")));
    }

    public static Boolean IsMobile(String AContactNumber) {
        String rct = GetPhone(AContactNumber);
        return (IsContactNumber(rct) && (rct.startsWith("8") || rct.startsWith("9")) && !rct.equals("88888888") && !rct.equals("99999999") && !rct.equals("98765432"));
    }

    public static void clearOnResumeDrawerMainActivityGcmPrefs(SharedPreferences gcmp) {
        try {
            SharedPreferences.Editor editor = gcmp.edit();
            editor.putString("jrbcscanning", "");
            editor.putString("dailypoints", "");
            editor.putString("LAT", "");
            editor.putString("LNG", "");
            editor.putString("desPostal", "");
            editor.putString("desAddress", "");
            editor.putString(objGlobalVariables.checkedItemsName, "");
            editor.putString(objGlobalVariables.checkedItemsIDX, "");
            editor.putString("ScannedCodeStrsPrefStr", "");
            editor.putString("massupdateokpwp2PostDataArr", "");
            editor.putString("DOListArrJsonStr", "");
            editor.putString("tempDOCODJsonStr", "");
            editor.putString("tempDOCODListJsonStr", "");
            editor.putString("scannedtype", "");
            editor.putString("scannedval", "");
            editor.putString("CannotTakePhoto", "");
            editor.putString("SecuritySealNumber", "");
            editor.putString("IDVerification", "");
            editor.putString("IDVerificationName", "");
            editor.putString("IMEIVerification", "");
            editor.putString("IMEIv1", "");
            editor.putString("IMEIv2", "");
            editor.putString("hasIMEI_Verification", "");
            editor.putString("hasSerialNum_Verification", "");
            editor.putString("hasVoucher_Verification", "");
            editor.putString("VOUCHERv0", "");
            editor.putString("VOUCHERv1", "");
            editor.putString("VOUCHERv2", "");
            editor.putString("CNIDX", "");
            editor.putString("reqLesserImgCompression", "");
            editor.putString("SelectedVRPJobsIDXs", "");
            editor.putString("pinglog", "");
            editor.putString("DestinationLocationJsonStr", "");
            editor.putString("isEarlierThanDeliveryStartTime", "");
            editor.putString("TypeOfPinVerificationStr", "");
            editor.putString("hideshow10to19NewForced", "");
            editor.putString("hasOtpStr", "");
            editor.putString("hasVerifiedOTPStr", "");
            editor.putString("CustIDVerificationNotRequiredStr", "");
            editor.putString("isLockerJobStr", "");
            editor.putString("CurrJobInfoStatusCodeIDX", "");
            editor.putString("currJobInfoFlag", "");
            editor.putString("IsCLCN_For_DoorOpenPenalty", "");
            editor.putString("isStandardRunJob", "");
            editor.putString("DatesALJson", "");
            editor.apply();
            saveChecklistSP("");
            clearGrouplistSP();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods clearOnResumeDrawerMainActivityGcmPrefs. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void clearOnResumeAddressListNewActivityGcmPrefs(SharedPreferences gcmp) {
        try {
            SharedPreferences.Editor editor = gcmp.edit();
            editor.putString("dailypoints", "");
            editor.putString("NFCLocationtagPrefVal", "");
            editor.putString(objGlobalVariables.checkedItemsName, "");
            editor.putString(objGlobalVariables.checkedItemsIDX, "");
            editor.putString("ScannedCodeStrsPrefStr", "");
            editor.putString("massupdateokpwp2PostDataArr", "");
            editor.putString("DOListArrJsonStr", "");
            editor.putString("tempDOCODJsonStr", "");
            editor.putString("tempDOCODListJsonStr", "");
            editor.putString("scannedtype", "");
            editor.putString("scannedval", "");
            editor.putString("CannotTakePhoto", "");
            editor.putString("SecuritySealNumber", "");
            editor.putString("IDVerification", "");
            editor.putString("IDVerificationName", "");
            editor.putString("IMEIVerification", "");
            editor.putString("IMEIv1", "");
            editor.putString("IMEIv2", "");
            editor.putString("hasIMEI_Verification", "");
            editor.putString("hasSerialNum_Verification", "");
            editor.putString("hasVoucher_Verification", "");
            editor.putString("VOUCHERv0", "");
            editor.putString("VOUCHERv1", "");
            editor.putString("VOUCHERv2", "");
            editor.putString("CNIDX", "");
            editor.putString("reqLesserImgCompression", "");
            editor.putString("pinglog", "");
            editor.putString("DestinationLocationJsonStr", "");
            editor.putString("isEarlierThanDeliveryStartTime", "");
            editor.putString("TypeOfPinVerificationStr", "");
            editor.putString("hideshow10to19NewForced", "");
            editor.putString("hasOtpStr", "");
            editor.putString("hasVerifiedOTPStr", "");
            editor.putString("CustIDVerificationNotRequiredStr", "");
            editor.putString("isLockerJobStr", "");
            editor.putString("CurrJobInfoStatusCodeIDX", "");
            editor.putString("currJobInfoFlag", "");
            editor.putString("IsCLCN_For_DoorOpenPenalty", "");
            editor.putString("isStandardRunJob", "");
            editor.putString("DatesALJson", "");
            editor.apply();
            saveChecklistSP("");
            clearGrouplistSP();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods clearOnResumeAddressListNewActivityGcmPrefs. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void clearOnResumeCreditCardActivityGcmPrefs(SharedPreferences gcmp) {
        try {
            SharedPreferences.Editor editor = gcmp.edit();
            editor.putString("dailypoints", "");
            editor.putString("NFCLocationtagPrefVal", "");
            editor.putString("CannotTakePhoto", "");
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods clearOnResumeCreditCardActivityGcmPrefs. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void clearOnResumeDashboardSubActivityGcmPrefs(SharedPreferences gcmp) {
        try {
            SharedPreferences.Editor editor = gcmp.edit();
            editor.putString("dailypoints", "");
            editor.putString("NFCLocationtagPrefVal", "");
            editor.putString(objGlobalVariables.checkedItemsName, "");
            editor.putString(objGlobalVariables.checkedItemsIDX, "");
            editor.putString("ScannedCodeStrsPrefStr", "");
            editor.putString("massupdateokpwp2PostDataArr", "");
            editor.putString("DOListArrJsonStr", "");
            editor.putString("tempDOCODJsonStr", "");
            editor.putString("tempDOCODListJsonStr", "");
            editor.putString("scannedtype", "");
            editor.putString("scannedval", "");
            editor.putString("CannotTakePhoto", "");
            editor.putString("SecuritySealNumber", "");
            editor.putString("IDVerification", "");
            editor.putString("IDVerificationName", "");
            editor.putString("IMEIVerification", "");
            editor.putString("IMEIv1", "");
            editor.putString("IMEIv2", "");
            editor.putString("hasIMEI_Verification", "");
            editor.putString("hasSerialNum_Verification", "");
            editor.putString("hasVoucher_Verification", "");
            editor.putString("VOUCHERv0", "");
            editor.putString("VOUCHERv1", "");
            editor.putString("VOUCHERv2", "");
            editor.putString("CNIDX", "");
            editor.putString("reqLesserImgCompression", "");
            editor.putString("pinglog", "");
            editor.putString("DestinationLocationJsonStr", "");
            editor.putString("isEarlierThanDeliveryStartTime", "");
            editor.putString("TypeOfPinVerificationStr", "");
            editor.putString("hideshow10to19NewForced", "");
            editor.putString("hasOtpStr", "");
            editor.putString("hasVerifiedOTPStr", "");
            editor.putString("CustIDVerificationNotRequiredStr", "");
            editor.putString("isLockerJobStr", "");
            editor.putString("CurrJobInfoStatusCodeIDX", "");
            editor.putString("currJobInfoFlag", "");
            editor.putString("IsCLCN_For_DoorOpenPenalty", "");
            editor.putString("isStandardRunJob", "");
            editor.putString("DatesALJson", "");
            editor.apply();
            saveChecklistSP("");
            clearGrouplistSP();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods clearOnResumeDashboardSubActivityGcmPrefs. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void clearOnResumeDriverPointMain_Activity_n_DriverPointSummaryActivityGcmPrefs(SharedPreferences gcmp) {
        try {
            SharedPreferences.Editor editor = gcmp.edit();
            editor.putString("CannotTakePhoto", "");
            editor.putString("SecuritySealNumber", "");
            editor.putString("IDVerification", "");
            editor.putString("IDVerificationName", "");
            editor.putString("IMEIVerification", "");
            editor.putString("IMEIv1", "");
            editor.putString("IMEIv2", "");
            editor.putString("hasIMEI_Verification", "");
            editor.putString("hasSerialNum_Verification", "");
            editor.putString("hasVoucher_Verification", "");
            editor.putString("VOUCHERv0", "");
            editor.putString("VOUCHERv1", "");
            editor.putString("VOUCHERv2", "");
            editor.putString("CNIDX", "");
            editor.putString("jobflag", "");
            editor.putString("jobflag2", "");
            editor.putString("reqLesserImgCompression", "");
            editor.putString("pinglog", "");
            editor.putString("DestinationLocationJsonStr", "");
            editor.putString("isEarlierThanDeliveryStartTime", "");
            editor.putString("TypeOfPinVerificationStr", "");
            editor.putString("hideshow10to19NewForced", "");
            editor.putString("hasOtpStr", "");
            editor.putString("hasVerifiedOTPStr", "");
            editor.putString("CustIDVerificationNotRequiredStr", "");
            editor.putString("isLockerJobStr", "");
            editor.putString("CurrJobInfoStatusCodeIDX", "");
            editor.putString("currJobInfoFlag", "");
            editor.putString("IsCLCN_For_DoorOpenPenalty", "");
            editor.putString("isStandardRunJob", "");
            editor.putString("DatesALJson", "");
            editor.apply();
            objGlobalMethods.saveChecklistSP("");
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods clearOnResumeDriverPointMain_Activity_n_DriverPointSummaryActivityGcmPrefs. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void clearOnResumeJobInfoActivityGcmPrefs(SharedPreferences gcmp) {
        try {
            SharedPreferences.Editor editor = gcmp.edit();
            editor.putString("searchedval", "");
            editor.putString(objGlobalVariables.currJobInfoIDX, "");
            editor.putString(objGlobalVariables.currJobInfoLinkedJobInfo, "");
            editor.putString(objGlobalVariables.currJobInfoLinkedJobInfoIDX, "");
            editor.putString(objGlobalVariables.checkedItemsName, "");
            editor.putString(objGlobalVariables.checkedItemsIDX, "");
            editor.putString("ScannedCodeStrsPrefStr", "");
            editor.putString("massupdateokpwp2PostDataArr", "");
            editor.putString("OTPtriesJsonStr", "");
            editor.putString("part2puHasCOD", "");
            editor.putString("DOListArrJsonStr", "");
            editor.putString("tempDOCODJsonStr", "");
            editor.putString("tempDOCODListJsonStr", "");
            //editor.putString("AuthActivity", "");
            editor.apply();
            clearGrouplistSP();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods clearOnResumeJobInfoActivityGcmPrefs. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void clearOnResumeJobsListGroupActivityGcmPrefs(SharedPreferences gcmp) {
        try {
            SharedPreferences.Editor editor = gcmp.edit();
            editor.putString("massupdateokpwp2PostDataArr", "");
            editor.putString("DOListArrJsonStr", "");
            editor.putString("tempDOCODJsonStr", "");
            editor.putString("tempDOCODListJsonStr", "");
            editor.putString("dailypoints", "");
            editor.putString("NFCLocationtagPrefVal", "");
            editor.putString("scannedtype", "");
            editor.putString("scannedval", "");
            editor.putString("CannotTakePhoto", "");
            editor.putString("SecuritySealNumber", "");
            editor.putString("IDVerification", "");
            editor.putString("IDVerificationName", "");
            editor.putString("IMEIVerification", "");
            editor.putString("IMEIv1", "");
            editor.putString("IMEIv2", "");
            editor.putString("hasIMEI_Verification", "");
            editor.putString("hasSerialNum_Verification", "");
            editor.putString("hasVoucher_Verification", "");
            editor.putString("VOUCHERv0", "");
            editor.putString("VOUCHERv1", "");
            editor.putString("VOUCHERv2", "");
            editor.putString("CNIDX", "");
            editor.putString("reqLesserImgCompression", "");
            editor.putString("pinglog", "");
            editor.putString("DestinationLocationJsonStr", "");
            editor.putString("isEarlierThanDeliveryStartTime", "");
            editor.putString("TypeOfPinVerificationStr", "");
            editor.putString("hideshow10to19NewForced", "");
            editor.putString("hasOtpStr", "");
            editor.putString("hasVerifiedOTPStr", "");
            editor.putString("CustIDVerificationNotRequiredStr", "");
            editor.putString("isLockerJobStr", "");
            editor.putString("CurrJobInfoStatusCodeIDX", "");
            editor.putString("currJobInfoFlag", "");
            editor.putString("IsCLCN_For_DoorOpenPenalty", "");
            editor.putString("isStandardRunJob", "");
            editor.putString("DatesALJson", "");
            editor.apply();
            objGlobalMethods.saveChecklistSP("");
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods clearOnResumeJobsListGroupActivityGcmPrefs. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void clearCageScanActivityGcmPrefs(SharedPreferences gcmp) {
        try {
            SharedPreferences.Editor editor = gcmp.edit();
            editor.putString("ScanCage_ddMMyyyy", "");
            editor.putString("ScanCage_AMPM", "");
            editor.putString("CageIDX", "");
            editor.putString("cagebarcode", "");
            editor.putString("CageLocation", "");
            editor.putString("lblCageScanSubmitIsHomesUser", "");
            editor.putString("HQ_CageIn_ScheduledDateTimeStr", "");
            editor.putString("L2_Get_CageResult", "");
            editor.putString("gc_cage_accept_reject", "");
            editor.putString("CageInOutScannedAItemsPrefStr", "");
            editor.putString("CageInOutScannedExcludedPrefStr", "");
            editor.putString("HQ_total_CageIn", "0");
            editor.apply();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods clearCageScanActivityGcmPrefs. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static boolean isNotHomesUser(String username, String password, String ishomesuser) {
        boolean asd = false;
        boolean homesuser;
        try {
            try {
                homesuser = ishomesuser == null || ishomesuser.equals("") ? false : Boolean.parseBoolean(ishomesuser);
            } catch (Exception e) {
                homesuser = false;
            }

            asd = !username.equals("") && !password.equals("") && !homesuser;
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods isNotHomesUser. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = true;
        } finally {
            return asd;
        }
    }

    public static boolean isActiveServiceRunning() {
        boolean serviceRunning = false;
        try {
            Context ctx = myApp.getAppContext();
            ActivityManager am = (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
            Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
            while (i.hasNext()) {
                ActivityManager.RunningServiceInfo runningServiceInfo = i
                        .next();

                if (runningServiceInfo.service.getClassName().equals("com.xdel.luke.BCnSVC.ActiveService")) {
                    serviceRunning = true;

                    if (runningServiceInfo.foreground) {
                        //service run in foreground
                    }
                }
            }
        } catch (Exception e) {
            serviceRunning = false;
        } finally {
            return serviceRunning;
        }
    }

    public static void startActiveForegroundSvc(String bootup) {
        try {
            /*if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Context ctx = myApp.getAppContext();
                Intent i = new Intent(ctx, ActiveService.class);
                if (bootup != null && !bootup.equals("")) {
                    i.putExtra("bootup", "true");
                }
                i.setAction(ActiveService.ACTION_START_FOREGROUND_SERVICE);
                ctx.startService(i);
            }*/
            Context ctx = myApp.getAppContext();
            Intent i = new Intent(ctx, ActiveService.class);
            if (bootup != null && !bootup.equals("")) {
                i.putExtra("bootup", "true");
            }
            i.setAction(ActiveService.ACTION_START_FOREGROUND_SERVICE);
            ctx.startService(i);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods startActiveForegroundSvc. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void stopActiveForegroundSvc() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Context ctx = myApp.getAppContext();
                Intent i = new Intent(ctx, ActiveService.class);
                i.setAction(ActiveService.ACTION_STOP_FOREGROUND_SERVICE);
                ctx.startService(i);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods stopActiveForegroundSvc. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static Date[] getDateRange() {
        Date[] dateArr = new Date[2];
        Date begining, end;

        {
            Calendar calendar = getCalendarForNow();
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            begining = calendar.getTime();
            dateArr[0] = begining;
        }

        {
            Calendar calendar = getCalendarForNow();
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndofDay(calendar);
            end = calendar.getTime();
            dateArr[1] = begining;
        }

        return dateArr;
    }

    public static String convertLongToReadableDateTime(long l) {
        String rdt = "";
        try {

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception caught at objGlobalMethods convertLongToReadableDateTime. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return rdt;
        }
    }

    public static String convertSelectedDateToLocaleDate(String selecteddatestr) {
        String datelongval = "";
        Date dt;
        try {
            String[] array = selecteddatestr.split("-");
            int day = Integer.parseInt(array[0]);
            int mth = Integer.parseInt(array[1]);
            int yr = Integer.parseInt(array[2]);

            Calendar calendar = getCalendarForNow();
            calendar.set(yr, mth - 1, day);
            Long l = calendar.getTimeInMillis();
            datelongval = "/Date(" + l + ")/";
        } catch (Exception e) {
            Date d = new Date();
            writeToFile("Exception caught at objGlobalMethods convertSelectedDateToLocaleDate. Exception: " + e.getMessage().toString(), "", "", "", "");
            datelongval = "";
        } finally {
            return datelongval;
        }
    }

    public static String convertCalendarToLocaleDate(Calendar c) {
        String v = "";
        try {
            Long l = c.getTimeInMillis();
            v = "/Date(" + l + ")/";
        } catch (Exception e) {
            writeToFile("Exception caught at objGlobalMethods convertCalendarToLocaleDate. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return v;
        }
    }

    public static String[] getCurrMonthToDateRangeLongValueString() {
        String[] dateLongValueStringArr = new String[2];
        Date begining, end;

        {
            Calendar calendar = getCalendarForNow();
            ////newly added to get 1 mth earlier to yest date
            ////newly added to get 1 mth earlier to yest date
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            begining = calendar.getTime();
            Long l = begining.getTime();
            dateLongValueStringArr[0] = "/Date(" + l + ")/";
        }

        {
            Calendar calendar = getCalendarForNow();
            //calendar.add(Calendar.DATE, -1); //this is until yesterday date, commenting this line will be up to today's date
            setTimeToEndofDay(calendar);
            end = calendar.getTime();
            Long l = end.getTime();
            String timestampstr = "/Date(" + l + ")/";
            dateLongValueStringArr[1] = "/Date(" + l + ")/";
        }

        return dateLongValueStringArr;
    }

    public static String[] getCurrMonthFullDateRangeLongValueString() {
        String[] dateLongValueStringArr = new String[2];
        Date begining, end;

        {
            Calendar calendar = getCalendarForNow();
            ////newly added to get 1 mth earlier to yest date
            ////newly added to get 1 mth earlier to yest date
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            begining = calendar.getTime();
            Long l = begining.getTime();
            dateLongValueStringArr[0] = "/Date(" + l + ")/";
        }

        {
            Calendar calendar = getCalendarForNow();
            ////calendar.add(Calendar.DATE, -1); //this is until yesterday date, commenting this line will be up to today's date
            //setTimeToEndofDay(calendar);
            setTimeToBeginningOfDay(calendar);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            end = calendar.getTime();
            Long l = end.getTime();
            String timestampstr = "/Date(" + l + ")/";
            dateLongValueStringArr[1] = "/Date(" + l + ")/";
        }

        return dateLongValueStringArr;
    }

    public static String[] getCurrMonthFullDateRangePlus2WksLongValueString() {
        String[] dateLongValueStringArr = new String[2];
        Date begining, end;

        {
            Calendar calendar = getCalendarForNow();
            ////newly added to get 1 mth earlier to yest date
            ////newly added to get 1 mth earlier to yest date
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            begining = calendar.getTime();
            Long l = begining.getTime();
            dateLongValueStringArr[0] = "/Date(" + l + ")/";
        }

        {
            Calendar calendar = getCalendarForNow();
            ////calendar.add(Calendar.DATE, -1); //this is until yesterday date, commenting this line will be up to today's date
            //setTimeToEndofDay(calendar);
            setTimeToBeginningOfDay(calendar);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            end = calendar.getTime();
            Long l = end.getTime();
            String timestampstr = "/Date(" + l + ")/";
            dateLongValueStringArr[1] = "/Date(" + l + ")/";
        }

        return dateLongValueStringArr;
    }

    public static String[] getPrevMonthDateRangeLongValueString() {
        String[] dateLongValueStringArr = new String[2];
        Date begining, end;

        {
            Calendar calendar = getCalendarForNow();
            //newly added to get 1 mth earlier to yest date
            calendar.add(Calendar.MONTH, -1);
            //newly added to get 1 mth earlier to yest date
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            begining = calendar.getTime();
            Long l = begining.getTime();
            dateLongValueStringArr[0] = "/Date(" + l + ")/";

            int lastday = calendar.getActualMaximum(Calendar.DATE);
            calendar.set(Calendar.DATE, lastday);
            setTimeToEndofDay(calendar);
            end = calendar.getTime();
            Long l2 = end.getTime();
            dateLongValueStringArr[1] = "/Date(" + l2 + ")/";
        }

        return dateLongValueStringArr;
    }

    public static int[] getCurrMonthDateRangeRangeDMYLongValue() {
        int[] v = new int[3];
        int d, m, y;
        try {
            Calendar calendar = getCalendarForNow();
            //newly added to get 1 mth earlier to yest date
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            d = calendar.get(Calendar.DAY_OF_MONTH);
            m = calendar.get(Calendar.MONTH);
            y = calendar.get(Calendar.YEAR);
            v[0] = d;
            v[1] = m;
            v[2] = y;
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods getPrevMonthDateRangeRangeDMYLongValue. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return v;
        }
    }

    public static int[] getCurrMonthToDateRangeDMYLongValue() {
        int[] v = new int[3];
        int d, m, y;
        try {
            Calendar calendar = getCalendarForNow();
            //calendar.add(Calendar.DATE, -1); //this is until yesterday date, commenting this line will be up to today's date
            setTimeToEndofDay(calendar);
            d = calendar.get(Calendar.DAY_OF_MONTH);
            m = calendar.get(Calendar.MONTH);
            y = calendar.get(Calendar.YEAR);
            v[0] = d;
            v[1] = m;
            v[2] = y;
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods getCurrMonthToDateRangeDMYLongValue. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return v;
        }
    }

    public static int[] getCurrMonthFullDateRangeDMYLongValue() {
        int[] v = new int[3];
        int d, m, y;
        try {
            Calendar calendar = getCalendarForNow();
            //calendar.add(Calendar.DATE, -1); //this is until yesterday date, commenting this line will be up to today's date
            setTimeToEndofDay(calendar);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            d = calendar.get(Calendar.DAY_OF_MONTH);
            m = calendar.get(Calendar.MONTH);
            y = calendar.get(Calendar.YEAR);
            v[0] = d;
            v[1] = m;
            v[2] = y;
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods getCurrMonthToDateRangeDMYLongValue. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return v;
        }
    }

    public static int[] getCurrMonthFullDateRangePlus1MthDMYLongValue() {
        int[] v = new int[3];
        int d, m, y;
        try {
            Calendar calendar = getCalendarForNow();
            //calendar.add(Calendar.DATE, -1); //this is until yesterday date, commenting this line will be up to today's date
            setTimeToEndofDay(calendar);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.add(Calendar.MONTH, 1);
            d = calendar.get(Calendar.DAY_OF_MONTH);
            m = calendar.get(Calendar.MONTH);
            y = calendar.get(Calendar.YEAR);
            v[0] = d;
            v[1] = m;
            v[2] = y;
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods getCurrMonthFullDateRangePlus1MthDMYLongValue. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return v;
        }
    }

    public static int[] getPrevMonthDateRangeRangeDMYLongValue() {
        int[] v = new int[3];
        int d, m, y;
        try {
            Calendar calendar = getCalendarForNow();
            //newly added to get 1 mth earlier to yest date
            calendar.add(Calendar.MONTH, -1);
            //newly added to get 1 mth earlier to yest date
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            d = calendar.get(Calendar.DAY_OF_MONTH);
            m = calendar.get(Calendar.MONTH);
            y = calendar.get(Calendar.YEAR);
            v[0] = d;
            v[1] = m;
            v[2] = y;
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods getPrevMonthDateRangeRangeDMYLongValue. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return v;
        }
    }

    public static long[] getPrevMonthStartEndTime() {
        long[] PrevMonthStartEndTime = new long[2];
        Date begining, end;
        try {
            {
                Calendar calendar = getCalendarForNow();
                //newly added to get 1 mth earlier to yest date
                calendar.add(Calendar.MONTH, -1);
                //newly added to get 1 mth earlier to yest date
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                setTimeToBeginningOfDay(calendar);
                begining = calendar.getTime();
                Long l = begining.getTime();
                PrevMonthStartEndTime[0] = l;
            }

            {
                Calendar calendar = getCalendarForNow();
                calendar.add(Calendar.MONTH, -1);
                int lastday = calendar.getActualMaximum(Calendar.DATE);
                calendar.set(Calendar.DATE, lastday);
                setTimeToEndofDay(calendar);
                end = calendar.getTime();
                Long l = end.getTime();

            }
        } catch (Exception e) {

        } finally {
            return PrevMonthStartEndTime;
        }
    }

    public static String[] getDateRangeLongValueString() {
        String[] dateLongValueStringArr = new String[2];
        Date begining, end;

        {
            Calendar calendar = getCalendarForNow();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            begining = calendar.getTime();
            Long l = begining.getTime();
            String timestampstr = "/Date(" + l + ")/";
            dateLongValueStringArr[0] = "/Date(" + l + ")/";
        }

        {
            Calendar calendar = getCalendarForNow();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndofDay(calendar);
            end = calendar.getTime();
            Long l = end.getTime();
            String timestampstr = "/Date(" + l + ")/";
            dateLongValueStringArr[1] = "/Date(" + l + ")/";
        }

        return dateLongValueStringArr;
    }

    public static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    public static boolean checkPlayServices(Context ctx) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ctx);
        if (resultCode != ConnectionResult.SUCCESS) {
            Activity act = (Activity) ctx;
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                /*GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        objGlobalVariables.PLAY_SERVICES_RESOLUTION_REQUEST).show();*/
                GooglePlayServicesUtil.getErrorDialog(resultCode, act,
                        objGlobalVariables.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                act.finish();
            }
            return false;
        }
        return true;
    }

    public static boolean isStill_relogging_in() {
        boolean asd = false;
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        String relogging_in = objGlobalMethods.getGCMPrefs(gcmp, "relogging_in");
        try {
            asd = !relogging_in.isEmpty() && !relogging_in.equals("") && relogging_in != "" ? true : false;
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods isStill_relogging_in. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static void requireGcReLogin() {
        SharedPreferences gcmp = getGcmPreferences();
        //String relogging_in = getGCMPrefs(gcmp, "relogging_in");
        String regId = getRegistrationIdOnly();
        try {
            boolean IsMaintenanceHours = currTimeIsBetweenFromDateToDateToIgnore();
            String[] UsernamePwd = new String[4];
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
            String username = UsernamePwd[0];
            String password = UsernamePwd[1];
            String ishomesuser = UsernamePwd[3];

            if (!IsMaintenanceHours && isStill_relogging_in()
                    && (username.length() != 0 && username != "") && (password.length() != 0 && password != "") && !Boolean.parseBoolean(ishomesuser)) {
                //attempt relogin
                boolean hasXDelCommunalDeviceFile_ = objGlobalMethods.hasXDelCommunalDeviceFile();
                Location aalocation = getLastKnownLocationForJobUpdate();
                GcReLogin task = new GcReLogin(username, password, regId, "", aalocation, hasXDelCommunalDeviceFile_);
                task.execute();
            }
        } catch (Exception e) {
            writeToFile("Exception caught at objGlobalMethods requireGcReLogin. " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void storeRegistrationId(Context context, String regId, SharedPreferences gcmp) {
        try {
            writeToFile("RegID stored.", "", "", "", "");
            int appVersion = getAppVersion(context);
            SharedPreferences.Editor editor = gcmp.edit();
            editor.putString(objGlobalVariables.PROPERTY_REG_ID, regId);
            editor.putInt(objGlobalVariables.PROPERTY_APP_VERSION, appVersion);
            editor.apply();
            String[] UsernamePwd = new String[4];
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
            String username = UsernamePwd[0];
            String password = UsernamePwd[1];
            String ishomesuser = UsernamePwd[3];
            //ConnectionDetector cd = new ConnectionDetector(context);
            ConnectionDetector cd = new ConnectionDetector(null);
            boolean IsMaintenanceHours = currTimeIsBetweenFromDateToDateToIgnore();
            if (!IsMaintenanceHours && (username.length() != 0 && username != "") && (password.length() != 0 && password != "") && !Boolean.parseBoolean(ishomesuser)) {
                setGCMPrefs(gcmp, "relogging_in", "1");
                if (cd.isConnectingToInternet()) {
                    setGCMPrefs(gcmp, "relogging_in", "1");
                    boolean hasXDelCommunalDeviceFile_ = objGlobalMethods.hasXDelCommunalDeviceFile();
                    Location aalocation = getLastKnownLocationForJobUpdate();
                    GcReLogin task = new GcReLogin(username, password, regId, "", aalocation, hasXDelCommunalDeviceFile_);
                    task.execute();
                } else {
                    //on hold here, attempt relogin at every page
                }
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods storeRegistrationId. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static void sendGcmPrefEmail() {
        try {
            SharedPreferences gcmp = getGcmPreferences();
            SharedPreferences UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
            Gson gson = new Gson();
            String gcmpJson = gson.toJson(gcmp);
            String UpdateJobInfoPreferencesJson = gson.toJson(UpdateJobInfoPreferences);

            objAuthDriverResult dr = objGlobalMethods.getGCdetails(gcmp);
            String GcName = dr.getFullName();
            //JobsHandler.sendNormalEmail("Luke Preferences Json from " + GcName, "GCMP\n\n" + gcmpJson + "\n\nUpdateJobInfoPreferences\n\n" + UpdateJobInfoPreferencesJson);

            prepareSendEmail(new String[]{"it@xdel.com"}, "Luke Preferences Json from " + GcName, "GCMP\n\n" + gcmpJson + "\n\nUpdateJobInfoPreferences\n\n" + UpdateJobInfoPreferencesJson,
                    "", false, "");
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods sendGcmPrefZippedEmail. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private static class GcReLogin extends AsyncTask<Void, Void, objAuthDriverResult> {
        private final String aLoginName;
        private final String aPassword;
        private final String aRegID;
        private final String aQRCode;
        private final Location aalocation;
        private final boolean hasXDelCommunalDeviceFile_;

        GcReLogin(String aaLoginName, String aaPassword, String aaRegID, String aaQRCode, Location aaalocation, boolean ahasXDelCommunalDeviceFile_) {
            this.aLoginName = aaLoginName;
            this.aPassword = aaPassword;
            this.aRegID = aaRegID;
            this.aQRCode = aaQRCode;
            this.aalocation = aaalocation;
            this.hasXDelCommunalDeviceFile_ = ahasXDelCommunalDeviceFile_;
        }

        @Override
        protected objAuthDriverResult doInBackground(Void... params) {
            objAuthDriverResult res = null;
            try {
                Date dt = new Date();
                Long l = dt.getTime();
                String timestampstr = "/Date(" + l + ")/";

                Location lastknownlocation = aalocation;
                int Accuracy = lastknownlocation == null ? 0 : Math.round(lastknownlocation.getAccuracy() * 100);
                objAuthRequest req = new objAuthRequest(aLoginName, aPassword, aRegID, timestampstr,
                        lastknownlocation == null ? 0 : lastknownlocation.getLatitude(), lastknownlocation == null ? 0 : lastknownlocation.getLongitude(), Accuracy, aQRCode);

                req.setCommunalDevice(hasXDelCommunalDeviceFile_);
                res = WebService.getGCLoginStatus(req);

                if (res != null && res.Status < 0) {
                    JobsHandler.ForceEmailImagesOut();
                    sendGcmPrefEmail();
                    Context context = myApp.getAppContext();
                    Intent SendQueuedEmailReceiver = new Intent(context, SendQueuedEmail_Receiver.class);
                    context.sendBroadcast(SendQueuedEmailReceiver);
                }
            } catch (Exception e) {
                writeToFile("Exception caught at objGlobalMethods GcReLogin doInBackground. " + e.getMessage().toString(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final objAuthDriverResult res) {
            try {
                SharedPreferences gcmp = getGcmPreferences();
                if (res != null && res.Status >= 0) {
                    //do nothing, login succ
                    writeToFile("ReLogin after RegID changed successful.", "", "", "", "");
                    setGCMPrefs(gcmp, "relogging_in", "");
                    Context ctx = myApp.getAppContext();
                    ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                    Intent SendQueuedEmailReceiver = new Intent(ctx, SendQueuedEmail_Receiver.class);
                    ctx.sendBroadcast(SendQueuedEmailReceiver);
                } else if (res != null && res.Status < 0) {
                    writeToFile("ReLogin after RegID changed failed due " + res.Message + ". Force logout.", "", "", "", "");
                    setGCMPrefs(gcmp, "relogging_in", "");
                    JobsHandler.ManualForceLogout();
                } else {
                    /*setGCMPrefs(gcmp, "relogging_in", "1");
                    writeToFile("ReLogin after RegID changed unsuccessful. Try again later when navigate to other page.", "", "", "", "");*/
                }
            } catch (Exception e) {
                writeToFile("Exception caught at objGlobalMethods GcReLogin onPostExecute. " + e.getMessage().toString(), "", "", "", "");
            }
        }
    }

    public static String getRegistrationId(Context context, SharedPreferences prefs) {
        //final SharedPreferences prefs = getGcmPreferences(context);
        if (prefs == null) {
            prefs = getGcmPreferences();
        }
        String registrationId = prefs.getString(objGlobalVariables.PROPERTY_REG_ID, "");
        //String registrationId = "";
        if (registrationId.isEmpty()) {

            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(objGlobalVariables.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        /*int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }*/
        return registrationId;
    }

    public static String getRegistrationIdOnly() {
        SharedPreferences GcmPreferences = getGcmPreferences();
        String registrationId = GcmPreferences.getString(objGlobalVariables.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {

            return "";
        }
        return registrationId;
    }

    public static String[] getLoginUsernamePwd(SharedPreferences GcmPreferences) {
        if (GcmPreferences == null) {
            GcmPreferences = getGcmPreferences();
        }
        final SharedPreferences prefs = GcmPreferences;
        String username = "";
        String pwd = "";
        String useridx = "0";
        String ishomesuser = "false";

        try {
            username = prefs.getString("username", "");
            pwd = prefs.getString("password", "");
            useridx = prefs.getString("IDX", "");
            ishomesuser = prefs.getString("ishomesuser", "");
        } catch (Exception e) {
            username = "";
            pwd = "";
            useridx = "0";
            ishomesuser = "false";
        }

        return new String[]{username, pwd, useridx, ishomesuser};
    }

    public static boolean IsHomesUser(SharedPreferences gcmp) {
        boolean asd = false;
        String ishomesuser;
        try {
            if (gcmp == null) {
                gcmp = getGcmPreferences();
            }
            ishomesuser = gcmp.getString("ishomesuser", "");
            asd = !ishomesuser.isEmpty() && !ishomesuser.equals("") && ishomesuser != "" ? Boolean.parseBoolean(ishomesuser) : false;
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean IsHomesUserString(String val, SharedPreferences gcmp) {
        boolean asd = false;
        try {
            if (gcmp == null) {
                gcmp = getGcmPreferences();
            }
            asd = !val.isEmpty() && !val.equals("") && val != "" ? Boolean.parseBoolean(val) : false;
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean isHomesUserMode(String val) {
        boolean asd = false;
        try {
            asd = !val.isEmpty() && !val.equals("") && val != "" ? Boolean.parseBoolean(val) : false;
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            String versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static Date rtnddMMyyyyToDateObj(String d) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        try {
            date = formatter.parse(d);
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods rtnddMMyyyyToDateObj. Exception: " + e.getMessage(), "", "", "", "");
            date = new Date();
        } finally {
            return date;
        }
    }

    public static Calendar toCalendar(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c;
    }

    public static Calendar toCalendar(int yr, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, yr);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    public static Calendar toCalendarDateHrMinAMPM(Date d, int Hr, int Min, int AMPM) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR, Hr);
        c.set(Calendar.MINUTE, Min);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.AM_PM, AMPM);
        return c;
    }

    public static Calendar toCalendarYrMthDayHrMinAMPM(int yr, int monthOfYear, int dayOfMonth, int Hr, int Min, int AMPM) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, yr);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.HOUR, Hr);
        c.set(Calendar.MINUTE, Min);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.AM_PM, AMPM);
        return c;
    }

    public static Date getGMTDateTime(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        res = calendar.getTime();

        return res;
    }

    public static Date getZeroTimeDate(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    public static Date getZeroSecondsTimeDate(Date d) {
        Date res = d;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(d);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    public static String rtntimestampstr() {
        Date dt = new Date();
        Long l = dt.getTime();
        String timestampstr = "/Date(" + l + ")/";
        return timestampstr;
    }

    public static Date JsonDateToDate(String jsonDate) {
        //  "/Date(1321867151710+0100)/"
        int idx1 = jsonDate.indexOf("(");
        int idx2 = jsonDate.contains("+") ? jsonDate.indexOf(")") - 5 : jsonDate.indexOf(")");
        String s = jsonDate.substring(idx1 + 1, idx2);
        long l = Long.valueOf(s);
        return new Date(l);
    }

    public static String rtnJsonDateToJsonDateAfterAddMinus(String jsonDate, int day) {
        String v = jsonDate;
        Date initalDate;
        Date newDate;
        long newDateLong;
        try {
            initalDate = JsonDateToDate(v);
            Calendar c = Calendar.getInstance();
            c.setTime(initalDate);

            c.add(Calendar.DATE, 1);
            newDate = c.getTime();
            newDateLong = newDate.getTime();
            v = "/Date(" + newDateLong + ")/";
        } catch (Exception e) {

        } finally {
            return v;
        }
    }

    public static Date getTodayDateAtCertainTime(int hr, int min) {
        Date d = new Date();
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(d);
            c.add(Calendar.HOUR, hr);
            c.add(Calendar.MINUTE, min);
            d = c.getTime();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods getTodayDateAtCertainTime. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return d;
        }
    }

    public static Date MillisecToDate(Long milli) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli * 1000);
        return calendar.getTime();
    }

    public static String MillisecToDateStr2(Long milli, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli);
        return formatter.format(calendar.getTime());
    }

    public static String MillisecToDateStr(Long milli, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli * 1000);
        return formatter.format(calendar.getTime());
    }

    public static Date LongToDate(Long longDate) {
        return longDate == null ? new Date() : new Date(longDate);
    }

    public static String DateTimeToJsonDateTime(Date dt) {
        String jd = "";
        SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyMMddHHmmssZ");
        try {
            jd = SIMPLE_DATE_FORMAT.format(dt);
        } catch (Exception e) {
            String s = e.getMessage().toString();
        } finally {
            return jd;
        }
    }

    public static Long DateToLong(String jsondate) {
        //  "/Date(1321867151710+0100)/"
        long l = 0;
        try {
            int idx1 = jsondate.indexOf("(");
            int idx2 = jsondate.indexOf(")") - 5;
            String s = jsondate.substring(idx1 + 1, idx2);
            l = Long.valueOf(s); //1321867151710
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at ojGlobalMethods DateToSecondsLong. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            l = 0;
        } finally {
            return l;
        }
    }

    public static Long DateToSecondsLong(String jsonDate) {
        //  "/Date(1321867151710+0100)/"
        long l = 0;
        try {
            int idx1 = jsonDate.indexOf("(");
            int idx2 = jsonDate.indexOf(")") - 5;
            String s = jsonDate.substring(idx1 + 1, idx2);
            l = Long.valueOf(s) / 1000; //1321867151710/1000
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at ojGlobalMethods DateToSecondsLong. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            l = 0;
        } finally {
            return l;
        }
    }

    public static Long[] returnAllDatesSQLiteLong(objJobInfo ji) {
        Long[] al = new Long[6];
        Long FromDateTime, PickByDateTime, ReadyDateTime, ToDateTime, ExtDateTime, ExtFromDateTime;
        try {
            if (ji.FromDateTime != null && !ji.FromDateTime.equals("")) {
                FromDateTime = DateToSecondsLong(ji.FromDateTime);
            } else {
                FromDateTime = Long.parseLong("0");
            }

            if (ji.PickByDateTime != null && !ji.PickByDateTime.equals("")) {
                PickByDateTime = DateToSecondsLong(ji.PickByDateTime);
            } else {
                PickByDateTime = Long.parseLong("0");
            }

            if (ji.ReadyDateTime != null && !ji.ReadyDateTime.equals("")) {
                ReadyDateTime = DateToSecondsLong(ji.ReadyDateTime);
            } else {
                ReadyDateTime = Long.parseLong("0");
            }

            if (ji.ToDateTime != null && !ji.ToDateTime.equals("")) {
                ToDateTime = DateToSecondsLong(ji.ToDateTime);
            } else {
                ToDateTime = Long.parseLong("0");
            }

            if (ji.ExtDateTime != null && !ji.ExtDateTime.equals("")) {
                ExtDateTime = DateToSecondsLong(ji.ExtDateTime);
            } else {
                ExtDateTime = Long.parseLong("0");
            }

            if (ji.ExtFromDateTime != null && !ji.ExtFromDateTime.equals("")) {
                ExtFromDateTime = DateToSecondsLong(ji.ExtFromDateTime);
            } else {
                ExtFromDateTime = Long.parseLong("0");
            }

            al[0] = FromDateTime;
            al[1] = PickByDateTime;
            al[2] = ReadyDateTime;
            al[3] = ToDateTime;
            al[4] = ExtDateTime;
            al[5] = ExtFromDateTime;
        } catch (Exception e) {
            al[0] = Long.parseLong("0");
            al[1] = Long.parseLong("0");
            al[2] = Long.parseLong("0");
            al[3] = Long.parseLong("0");
            al[4] = Long.parseLong("0");
            al[5] = Long.parseLong("0");
        } finally {
            return al;
        }
    }

    public static String[] returnAllDatesSQLiteString(objJobInfo ji) {
        String[] al = new String[5];
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date FromDateTime, PickByDateTime, ReadyDateTime, ToDateTime, ExtDateTime;
        String FromDateTimeStr, PickByDateTimeStr, ReadyDateTimeStr, ToDateTimeStr, ExtDateTimeStr;
        try {
            if (ji.FromDateTime != null && !ji.FromDateTime.equals("")) {
                FromDateTime = JsonDateToDate(ji.FromDateTime);
                FromDateTimeStr = formatter.format(FromDateTime);
            } else {
                FromDateTimeStr = null;
            }

            if (ji.PickByDateTime != null && !ji.PickByDateTime.equals("")) {
                PickByDateTime = JsonDateToDate(ji.PickByDateTime);
                PickByDateTimeStr = formatter.format(PickByDateTime);
            } else {
                PickByDateTimeStr = null;
            }

            if (ji.ReadyDateTime != null && !ji.ReadyDateTime.equals("")) {
                ReadyDateTime = JsonDateToDate(ji.ReadyDateTime);
                ReadyDateTimeStr = formatter.format(ReadyDateTime);
            } else {
                ReadyDateTimeStr = null;
            }

            if (ji.ToDateTime != null && !ji.ToDateTime.equals("")) {
                ToDateTime = JsonDateToDate(ji.ToDateTime);
                ToDateTimeStr = formatter.format(ToDateTime);
            } else {
                ToDateTimeStr = null;
            }

            if (ji.ExtDateTime != null && !ji.ExtDateTime.equals("")) {
                ExtDateTime = JsonDateToDate(ji.ExtDateTime);
                ExtDateTimeStr = formatter.format(ExtDateTime);
            } else {
                ExtDateTimeStr = null;
            }

            al[0] = FromDateTimeStr;
            al[1] = PickByDateTimeStr;
            al[2] = ReadyDateTimeStr;
            al[3] = ToDateTimeStr;
            al[4] = ExtDateTimeStr;
        } catch (Exception e) {
            al[0] = null;
            al[1] = null;
            al[2] = null;
            al[3] = null;
            al[4] = null;
        }
        return al;
    }

    public static Date[] returnAllDates(objJobInfo ji) {
        //ArrayList<Date> al = new ArrayList();
        Date[] al = new Date[6];
        Date FromDateTime, PickByDateTime, ReadyDateTime, ToDateTime, ExtDateTime, ExtFromDateTime;
        try {
            if (ji.FromDateTime != null && !ji.FromDateTime.equals("")) {
                FromDateTime = JsonDateToDate(ji.FromDateTime);
            } else {
                FromDateTime = null;
            }

            if (ji.PickByDateTime != null && !ji.PickByDateTime.equals("")) {
                PickByDateTime = JsonDateToDate(ji.PickByDateTime);
            } else {
                PickByDateTime = null;
            }

            if (ji.ReadyDateTime != null && !ji.ReadyDateTime.equals("")) {
                ReadyDateTime = JsonDateToDate(ji.ReadyDateTime);
            } else {
                ReadyDateTime = null;
            }

            if (ji.ToDateTime != null && !ji.ToDateTime.equals("")) {
                ToDateTime = JsonDateToDate(ji.ToDateTime);
            } else {
                ToDateTime = null;
            }

            if (ji.ExtDateTime != null && !ji.ExtDateTime.equals("")) {
                ExtDateTime = JsonDateToDate(ji.ExtDateTime);
            } else {
                ExtDateTime = null;
            }

            if (ji.ExtFromDateTime != null && !ji.ExtFromDateTime.equals("")) {
                ExtFromDateTime = JsonDateToDate(ji.ExtFromDateTime);
                if (ToDateTime != null) {
                    int comparedval = 0;
                    //0 comes when two date are same,
                    //1 comes when date1 is higher then date2
                    //-1 comes when date1 is lower then date2
                    comparedval = ExtFromDateTime.compareTo(ToDateTime);
                    if (comparedval == 0 || comparedval == -1) {
                        ExtFromDateTime = null;
                    }
                }
            } else {
                ExtFromDateTime = null;
            }

            /*al.add(FromDateTime);
            al.add(PickByDateTime);
            al.add(ReadyDateTime);
            al.add(ToDateTime);
            al.add(ExtDateTime);*/
            al[0] = FromDateTime;
            al[1] = PickByDateTime;
            al[2] = ReadyDateTime;
            al[3] = ToDateTime;
            al[4] = ExtDateTime;
            al[5] = ExtFromDateTime;
        } catch (Exception e) {
            FromDateTime = null;
            PickByDateTime = null;
            ReadyDateTime = null;
            ToDateTime = null;
            ExtDateTime = null;
            ExtFromDateTime = null;
            al[0] = FromDateTime;
            al[1] = PickByDateTime;
            al[2] = ReadyDateTime;
            al[3] = ToDateTime;
            al[4] = ExtDateTime;
            al[5] = ExtFromDateTime;
        }
        return al;
    }

    public static String returnFullAddress(objAddressStructure AddressStructure) {
        String address = "";
        try {
            if (AddressStructure.getBLOCK() != null) {
                if (!AddressStructure.getBLOCK().equals("")) {
                    address += ", BLK " + AddressStructure.getBLOCK();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getSTREET() != null) {
                if (!AddressStructure.getSTREET().equals("")) {
                    address += ", " + AddressStructure.getSTREET();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getUNIT() != null) {
                if (!AddressStructure.getUNIT().equals("")) {
                    address += ", " + AddressStructure.getUNIT();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getBUILDING() != null) {
                if (!AddressStructure.getBUILDING().equals("")) {
                    address += ", " + AddressStructure.getBUILDING();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getPOSTALCODE() != null) {
                if (!AddressStructure.getPOSTALCODE().equals("")) {
                    address += ", " + AddressStructure.getPOSTALCODE();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            address = address.substring(2, address.length());

        } catch (Exception e) {
            e.printStackTrace();
            address = "Error";
        }
        return address;
    }

    public static String returnAddressWithPostalNoUnit(objAddressStructure AddressStructure) {
        String address = "";
        try {
            if (AddressStructure.getBLOCK() != null) {
                if (!AddressStructure.getBLOCK().equals("")) {
                    address += ", BLK " + AddressStructure.getBLOCK();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getSTREET() != null) {
                if (!AddressStructure.getSTREET().equals("")) {
                    address += ", " + AddressStructure.getSTREET();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getBUILDING() != null) {
                if (!AddressStructure.getBUILDING().equals("")) {
                    address += ", " + AddressStructure.getBUILDING();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getPOSTALCODE() != null) {
                if (!AddressStructure.getPOSTALCODE().equals("")) {
                    address += ", " + AddressStructure.getPOSTALCODE();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            address = address.substring(2, address.length());

        } catch (Exception e) {
            e.printStackTrace();
            address = "Error";
        }
        return address;
    }

    public static String returnAddressWithStreetOnly(objAddressStructure AddressStructure) {
        String address = "";
        try {
            if (AddressStructure.getSTREET() != null) {
                if (!AddressStructure.getSTREET().equals("")) {
                    address += ", " + AddressStructure.getSTREET();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            address = address.substring(2, address.length());

        } catch (Exception e) {
            e.printStackTrace();
            address = "Error";
        }
        return address;
    }

    public static String returnAddressWithoutPostalNUnitNBuilding(objAddressStructure AddressStructure) {
        String address = "";
        try {
            if (AddressStructure.getBLOCK() != null) {
                if (!AddressStructure.getBLOCK().equals("")) {
                    address += ", BLK " + AddressStructure.getBLOCK();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getSTREET() != null) {
                if (!AddressStructure.getSTREET().equals("")) {
                    address += ", " + AddressStructure.getSTREET();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            address = address.substring(2, address.length());

        } catch (Exception e) {
            e.printStackTrace();
            address = "Error";
        }
        return address;
    }

    public static String returnAddressWithoutPostalNUnit(objAddressStructure AddressStructure) {
        String address = "";
        try {
            if (AddressStructure.getBLOCK() != null) {
                if (!AddressStructure.getBLOCK().equals("")) {
                    //address += ", BLOCK " + AddressStructure.getBLOCK() ;
                    address += ", BLK " + AddressStructure.getBLOCK();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getSTREET() != null) {
                if (!AddressStructure.getSTREET().equals("")) {
                    address += ", " + AddressStructure.getSTREET();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            /*if (AddressStructure.getUNIT() != null){
                if (!AddressStructure.getUNIT().equals("")){
                    address += AddressStructure.getUNIT() + ", ";
                }
                else {
                    address = "" ;
                }
            }
            else {
                address = "";
            }

            if (AddressStructure.getBUILDING() != null){
                if (!AddressStructure.getBUILDING().equals("")){
                    address += ", " + AddressStructure.getBUILDING();
                }
                else {
                    address += "";
                }
            }
            else {
                address += "";
            }*/

            address = address.substring(2, address.length());

        } catch (Exception e) {
            e.printStackTrace();
            address = "Error";
        }
        return address;
    }

    public static String returnFullAddressNoSpaces(objAddressStructure AddressStructure) {
        String address = "";
        try {
            if (AddressStructure.getBLOCK() != null) {
                if (!AddressStructure.getBLOCK().equals("")) {
                    address += AddressStructure.getBLOCK().trim().toLowerCase().toString();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getSTREET() != null) {
                if (!AddressStructure.getSTREET().equals("")) {
                    address += AddressStructure.getSTREET().trim().toLowerCase().toString();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getUNIT() != null) {
                if (!AddressStructure.getUNIT().equals("")) {
                    address += AddressStructure.getUNIT().trim().toLowerCase().toString();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getBUILDING() != null) {
                if (!AddressStructure.getBUILDING().equals("")) {
                    address += AddressStructure.getBUILDING().trim().toLowerCase().toString();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getPOSTALCODE() != null) {
                if (!AddressStructure.getPOSTALCODE().equals("")) {
                    address += AddressStructure.getPOSTALCODE().trim().toLowerCase().toString();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }
        } catch (Exception e) {
            address = "";
        } finally {
            return address;
        }
    }

    public static String returnUnitNoOnly(objAddressStructure AddressStructure) {
        String unitno = "";
        try {
            if (AddressStructure.getUNIT() != null) {
                if (!AddressStructure.getUNIT().equals("")) {
                    unitno = AddressStructure.getUNIT() + ", ";
                } else {
                    unitno = "";
                }
            } else {
                unitno = "";
            }
        } catch (Exception e) {
            unitno = "";
        } finally {
            return unitno;
        }
    }

    public static Boolean checkAddressHasBlock(objAddressStructure AddressStructure) {
        Boolean asd = false;
        try {
            if (AddressStructure != null) {
                if (AddressStructure.getBLOCK() != null) {
                    if (!AddressStructure.getBLOCK().equals("")) {
                        asd = true;
                    }
                }
            }
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static String returnAddressWithoutPostal(objAddressStructure AddressStructure) {
        String address = "";
        try {
            if (AddressStructure.getBLOCK() != null) {
                if (!AddressStructure.getBLOCK().equals("")) {
                    //address += ", BLOCK " + AddressStructure.getBLOCK() ;
                    address += ", BLK " + AddressStructure.getBLOCK();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getSTREET() != null) {
                if (!AddressStructure.getSTREET().equals("")) {
                    address += ", " + AddressStructure.getSTREET();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getUNIT() != null) {
                if (!AddressStructure.getUNIT().equals("")) {
                    address += ", " + AddressStructure.getUNIT();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            if (AddressStructure.getBUILDING() != null) {
                if (!AddressStructure.getBUILDING().equals("")) {
                    address += ", " + AddressStructure.getBUILDING();
                } else {
                    address += "";
                }
            } else {
                address += "";
            }

            address = address.substring(2, address.length());

        } catch (Exception e) {
            address = "Error";
        }
        return address;
    }

    public static String returnAddressWithPostalOnly(objAddressStructure AddressStructure) {
        String address = "";
        try {
            if (AddressStructure.getPOSTALCODE() != null) {
                if (!AddressStructure.getPOSTALCODE().equals("")) {
                    address = AddressStructure.getPOSTALCODE();
                } else {
                    address = "";
                }
            } else {
                address = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            address = "Error";
        }
        return address;
    }

    public static String returnBuilding(objAddressStructure AddressStructure) {
        String building = "";
        try {
            if (AddressStructure.getBUILDING() != null && !AddressStructure.getBUILDING().equals("")) {
                building = AddressStructure.getBUILDING();
            } else {
                building = "";
            }
        } catch (Exception e) {
            building = "";
        } finally {
            return building;
        }
    }

    public static String getStoredRecipientTypes(SharedPreferences GcmPreferences) {
        String RecipientTypesJsonStr = GcmPreferences.getString(objGlobalVariables.storedRecipientTypesPrefName, "");
        return RecipientTypesJsonStr;
    }

    public static int getDashboard2ActivityCategoryType(SharedPreferences prefs) {
        int catType = -1;
        String catTypeStr = prefs.getString(objGlobalVariables.categoryTypePrefName, "");
        if (!catTypeStr.isEmpty()) {
            try {
                catType = Integer.parseInt(catTypeStr);
            } catch (Exception e) {
                writeToFile("Exception at objGlobalMethods getDashboard2ActivityCategoryType. Exception: " + e.getMessage(), "", "", "", "");
            }
        }

        return catType;
    }

    public static void setDashboard2ActivityCategoryType(SharedPreferences prefs, String value) {
        try {
            setGCMPrefs(prefs, objGlobalVariables.categoryTypePrefName, value);
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods setDashboard2ActivityCategoryType. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static String getStoredStatusCodes() {
        final SharedPreferences prefs = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String storedStatusCodes = prefs.getString(objGlobalVariables.storedStatusCodesPrefName, "");
        return storedStatusCodes;
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString().toUpperCase();
    }

    public static String getZippedLogFilesAbsolutePath() {
        String path = "";
        File externalStorageDir = null;
        String storagePathStr = "";
        ArrayList<String> requiredFilesPaths = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        String zippedFilename = formatter.format(new Date()) + "_last7logs.zip";
        String fullzippedFilename = "";
        File zippedFile = null;

        try {
            externalStorageDir = Environment.getExternalStorageDirectory();
            storagePathStr = externalStorageDir.getPath();
            requiredFilesPaths = get7DaysLogFilesPaths2();
            if (requiredFilesPaths != null && requiredFilesPaths.size() > 0) {
                fullzippedFilename = storagePathStr + "/" + zippedFilename;
                ZipManager.zip(requiredFilesPaths, fullzippedFilename);
                zippedFile = new File(externalStorageDir, zippedFilename);
                path = zippedFile.exists() ? zippedFile.getAbsolutePath().toString() : "";
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods getZippedLogFilesAbsolutePath. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            path = "";
        } finally {
            return path;
        }
    }

    public static ArrayList<String> get7DaysLogFilesPaths2() {
        ArrayList<String> af = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        String Filenamedtstr = "";
        Date currDate = new Date();
        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currDate);
        File f = null;
        try {
            File externalStorageDir = Environment.getExternalStorageDirectory();
            for (int i = 0; i <= 7; i++) {
                if (i > 0) {
                    c.add(Calendar.DATE, -1);
                }
                d = c.getTime();
                Filenamedtstr = formatter.format(d);
                f = new File(externalStorageDir, Filenamedtstr + "_log.txt");
                if (f != null && f.exists()) {
                    af.add(f.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            String s = e.getMessage();
            writeToFile("Exception at objGlobalMethods get7DaysLogFilesPaths2. Exception: " + s, "", "", "", "");
            af = null;
        } finally {
            return af;
        }
    }

    public static ArrayList<String> get7DaysLogFilesPaths() {
        ArrayList<String> af = new ArrayList<>();
        Calendar SevenDaysAgoCalendar = Calendar.getInstance();
        Calendar TwentySecondMarchTwentyEighteenCalendar = Calendar.getInstance();
        String Filenamedtstr = "";
        File f = null;
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        try {
            File externalStorageDir = Environment.getExternalStorageDirectory();
            for (int i = 0; i <= 7; i++) {
                SevenDaysAgoCalendar = Calendar.getInstance();
                SevenDaysAgoCalendar.add(Calendar.DATE, -i);
                Filenamedtstr = formatter.format(SevenDaysAgoCalendar.getTime());
                f = new File(externalStorageDir, Filenamedtstr + "_log.txt");
                if (f != null && f.exists()) {
                    af.add(f.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            String s = e.getMessage();
            writeToFile("Exception at objGlobalMethods get7DaysLogFilesPaths. Exception: " + s, "", "", "", "");
            af = null;
        } finally {
            return af;
        }
    }

    public static boolean createCommunalFile() {
        boolean asd = false;
        try {
            /*String sdCard = Environment.getExternalStorageDirectory().toString();
            File myFile = new File(sdCard + "/XDelQueue/XDelCommunalDeviceFile.txt");
            if (!myFile.exists()) {
                myFile.createNewFile();
                asd = true;
            }*/
            asd = createXDelQueueDir("XDelQueue", "XDelCommunalDeviceFile.txt");
        } catch (Exception e) {
            String s = e.getMessage();
            writeToFile("Exception at objGlobalMethods createCommunalFile. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean createXDelQueueDir(String dirName, String fileName) {
        boolean asd = false;
        try {
            //// your sd card
            String sdCard = Environment.getExternalStorageDirectory().toString();
            //// make sure your target location folder exists!
            File targetLocation = new File(sdCard + "/" + dirName + "/" + fileName);
            boolean targetLocation_mkdirBoolsucc = targetLocation.exists();
            boolean targetLocationFileCreatedsucc = false;
            boolean dirExists = false;
            if (!targetLocation.exists()) {
                File dir = new File(sdCard + "/" + dirName + "/");
                dirExists = dir.isDirectory();
                targetLocation_mkdirBoolsucc = !dirExists ? targetLocation.getParentFile().mkdirs() : true;
            }
            if (targetLocation_mkdirBoolsucc) {
                targetLocation.createNewFile();
                asd = targetLocation.exists();
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods createXDelQueueDir. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public static boolean writeToCommunalFile(String msg) {
        boolean asd = false;
        try {
            String sdCard = Environment.getExternalStorageDirectory().toString();
            File myFile = new File(sdCard + "/XDelQueue/XDelCommunalDeviceFile.txt");
            if (myFile.exists()) {
                PrintWriter out = null;
                BufferedWriter bw = null;
                FileWriter fw = null;
                try {
                    //write to existing file on a new line
                    fw = new FileWriter(myFile, true);
                    bw = new BufferedWriter(fw);
                    out = new PrintWriter(bw);
                    out.println(msg);
                    asd = true;
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                    String s = JobsHandler.getStackTraceString(e);
                    String ss = e.getMessage() + s;
                } finally {
                    try {
                        if (out != null) {
                            out.close(); // Will close bw and fw too
                        } else if (bw != null) {
                            bw.close(); // Will close fw too
                        } else if (fw != null) {
                            fw.close();
                        } else {
                            // Oh boy did it fail hard! :3
                        }
                    } catch (IOException e) {
                        // Closing the file writers failed for some obscure reason
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            String s = e.getMessage();
            writeToFile("Exception at objGlobalMethods writeToCommunalFile. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean hasXDelCommunalDeviceFile() {
        boolean asd = false;
        try {
            String sdCard = Environment.getExternalStorageDirectory().toString();
            File targetLocation = new File(sdCard + "/XDelQueue/XDelCommunalDeviceFile.txt");
            asd = targetLocation.exists();
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods hasXDelCommunalDeviceFile. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static void writeToFile(String action, String JobsIDX, String StatusCodesIDX, String username, String DriverIDX) {
        String data = "";
        Date datetime = new Date();
        PrintWriter out = null;
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
            SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");

            String Filenamedtstr = "";
            Filenamedtstr = formatter.format(datetime);
            String dtstr = formatter2.format(datetime);

            File externalStorageDir = Environment.getExternalStorageDirectory();
            File myFile = new File(externalStorageDir, Filenamedtstr + "_log.txt");

            if (myFile.exists()) {
                try {
                    //write to existing file on a new line
                    data = dtstr + ", " + action + ", " + JobsIDX + ", " + StatusCodesIDX;
                    fw = new FileWriter(myFile, true);
                    bw = new BufferedWriter(fw);
                    out = new PrintWriter(bw);
                    out.println(data);
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                    String s = JobsHandler.getStackTraceString(e);
                    String ss = e.getMessage() + s;
                } finally {
                    try {
                        if (out != null) {
                            out.close(); // Will close bw and fw too
                        } else if (bw != null) {
                            bw.close(); // Will close fw too
                        } else if (fw != null) {
                            fw.close();
                        } else {
                            // Oh boy did it fail hard! :3
                        }
                    } catch (IOException e) {
                        // Closing the file writers failed for some obscure reason
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    data = username + ", " + DriverIDX + "\n" + dtstr + ", " + action + ", " + JobsIDX + ", " + StatusCodesIDX;
                    myFile.createNewFile();
                    fw = new FileWriter(myFile, true);
                    bw = new BufferedWriter(fw);
                    out = new PrintWriter(bw);
                    out.print(data);
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close(); // Will close bw and fw too
                        } else if (bw != null) {
                            bw.close(); // Will close fw too
                        } else if (fw != null) {
                            fw.close();
                        } else {
                            // Oh boy did it fail hard! :3
                        }
                    } catch (IOException e) {
                        // Closing the file writers failed for some obscure reason
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            //prompt
            String s = e.getMessage().toString();
        }
    }

    public static void writeToSepcificFile(String action) {
        String data = "";
        Date datetime = new Date();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
            SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");

            String Filenamedtstr = "";
            Filenamedtstr = formatter.format(datetime);
            String dtstr = formatter2.format(datetime);

            File externalStorageDir = Environment.getExternalStorageDirectory();
            File myFile = new File(externalStorageDir, "LocationNfcBarcodePairing.txt");
            PrintWriter out = null;
            BufferedWriter bw = null;
            FileWriter fw = null;

            if (myFile.exists()) {
                try {
                    //write to existing file on a new line
                    data = action + "\n";
                    fw = new FileWriter(myFile, true);
                    bw = new BufferedWriter(fw);
                    out = new PrintWriter(bw);
                    out.println(data);
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                } finally {
                    try {
                        if (out != null) {
                            out.close(); // Will close bw and fw too
                        } else if (bw != null) {
                            bw.close(); // Will close fw too
                        } else if (fw != null) {
                            fw.close();
                        } else {
                            // Oh boy did it fail hard! :3
                        }
                    } catch (IOException e) {
                        // Closing the file writers failed for some obscure reason
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    data = action + "\n";
                    myFile.createNewFile();
                    fw = new FileWriter(myFile, true);
                    bw = new BufferedWriter(fw);
                    out = new PrintWriter(bw);
                    out.print(data);
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close(); // Will close bw and fw too
                        } else if (bw != null) {
                            bw.close(); // Will close fw too
                        } else if (fw != null) {
                            fw.close();
                        } else {
                            // Oh boy did it fail hard! :3
                        }
                    } catch (IOException e) {
                        // Closing the file writers failed for some obscure reason
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            //prompt
            String stack = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods writeToSepcificFile. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    public static void stopAlarmForCommon() {
        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(1);
        }
        FCMservice.notiNotRead = false;
        FCMservice.stopAlarmForCommon();
        writeToFile("AlarmForCommon stopped.", "", "", "", "");
    }

    /*public static void sendNormalEmail(String subj, String mailbody){
        Mail m = new Mail("luke@xdel.com", "XDel2015");

        Date dt = new Date();
        SimpleDateFormat formatter2 = new SimpleDateFormat("ddMMyyyy h:mm:ss a");
        String dtstr = formatter2.format(dt);
        objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", dtstr + " " + mailbody, subj, null);
        sendmail.execute();
    }*/

    public static String getSavedBitmapFileToQueueFolder(String base64, String JobsIDX) {
        String filepath = "";
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        String filename = formatter.format(new Date()) + "_" + JobsIDX + ".jpg";
        try {
            String sdCard = Environment.getExternalStorageDirectory().toString();
            Bitmap bm = null;
            byte[] imageAsBytes = Base64.decode(base64.getBytes(), Base64.DEFAULT);
            bm = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(sdCard + "/XDelQueue/" + filename);
            boolean targetLocation_mkdirBoolsucc = f.exists();
            boolean targetLocationFileCreatedsucc = false;
            boolean dirExists = false;
            if (!f.exists()) {
                File dir = new File(sdCard + "/XDelQueue/");
                dirExists = dir.isDirectory();
                targetLocation_mkdirBoolsucc = !dirExists ? f.getParentFile().mkdirs() : true;
            }
            if (targetLocation_mkdirBoolsucc) {
                f.createNewFile();
                //write the bytes in file
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                // remember close de FileOutput
                fo.close();

                if (f != null && f.exists()) {
                    filepath = f.getAbsolutePath().toString();
                }
            }
        } catch (Exception e) {
            filepath = "";
            String s = e.getMessage();
            writeToFile("Exception at objGlobalMethods getSavedBitmapFileToQueueFolder. Exception: " + s, "", "", "", "");
        } finally {
            return filepath;
        }
    }

    private static File getAlbumDir(AlbumStorageDirFactory mAlbumStorageDirFactory) {
        File storageDir = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                storageDir = mAlbumStorageDirFactory.getAlbumStorageDir("Images");
                ////below 2 lines of codes for app specific folder, but have issues on some devices suspected unable to read/write from app specific folder
                //String dirPath = getFilesDir().getAbsolutePath() + File.separator + getAlbumName(); //app specific - same location per preferenes & sqlite, cannot find via normal file system
                //storageDir = new File(dirPath);

                if (storageDir != null) {
                    if (!storageDir.mkdirs()) {
                        if (!storageDir.exists()) {
                            //Log.d("CameraSample", "failed to create directory");
                            objGlobalMethods.writeToFile("objGlobalMethods getAlbumDir failed to create directory.", "", "", "", "");
                            return null;
                        }
                    }
                }

            } else {
                //Toast.makeText(myApp.getAppContext(), "External storage is not mounted READ/WRITE.", Toast.LENGTH_SHORT);
                //do nothing here
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at objGlobalMethods getAlbumDir. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return storageDir;
        }
    }

    public static String getcopyFileToImagesFolder(File file) {
        String copiedFileFilePath = "";
        boolean succ = false;
        AlbumStorageDirFactory mAlbumStorageDirFactory = null;
        try {
            // your sd card
            String sdCard = Environment.getExternalStorageDirectory().toString();
            // the file to be moved or copied
            final File sourceLocation = file;
            // make sure your target location folder exists!
            String filename = sourceLocation.getName();

            //File targetLocation = new File(sdCard + "/XDelQueue/" + filename);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
            } else {
                mAlbumStorageDirFactory = new BaseAlbumDirFactory();
            }
            File targetLocation = new File(getAlbumDir(mAlbumStorageDirFactory) + filename);
            boolean targetLocation_mkdirBoolsucc = targetLocation.exists();
            boolean targetLocationFileCreatedsucc = false;
            boolean dirExists = false;
            if (!targetLocation.exists()) {
                //File dir = new File(sdCard + "/XDelQueue/");
                File dir = new File(getAlbumDir(mAlbumStorageDirFactory).getAbsolutePath());
                dirExists = dir.isDirectory();
                targetLocation_mkdirBoolsucc = !dirExists ? targetLocation.getParentFile().mkdirs() : true;
            }
            if (targetLocation_mkdirBoolsucc) {
                targetLocation.createNewFile();
                targetLocationFileCreatedsucc = targetLocation.exists();
            }

            copiedFileFilePath = targetLocation.getAbsolutePath().toString();
            // 1 = move the file, 2 = copy the file
            int actionChoice = 2;
            // moving the file to another directory
            if (actionChoice == 1 && targetLocationFileCreatedsucc) {

                if (sourceLocation.renameTo(targetLocation)) {
                    writeToFile("Move file successfully.", "", "", "", "");
                    succ = true;
                } else {
                    succ = false;
                    writeToFile("Move file failed..", "", "", "", "");
                }
            }
            // we will copy the file
            else {
                // make sure the target file exists
                if (sourceLocation.exists() && targetLocationFileCreatedsucc) {
                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);
                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    succ = true;
                    writeToFile("Copy file successfully.", "", "", "", "");
                } else {
                    succ = false;
                    writeToFile("Move file failed. Source file missing.", "", "", "", "");
                }
            }
        } catch (Exception e) {
            succ = false;
            copiedFileFilePath = "";
            writeToFile("Exception at objGlobalMethods copyFileToQueueFolder. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return succ ? copiedFileFilePath : "";
        }
    }

    public static String GetcopyFileToQueueFolder(File file, String filename) {
        String copiedFileFilePath = "";
        boolean succ = false;
        try {
            //// your sd card
            String sdCard = Environment.getExternalStorageDirectory().toString();
            //// the file to be moved or copied
            final File sourceLocation = file;
            //// make sure your target location folder exists!
            //String filename = sourceLocation.getName();
            File targetLocation = new File(sdCard + "/XDelQUeue/" + filename);
            boolean targetLocation_mkdirBoolsucc = targetLocation.exists();
            boolean targetLocationFileCreatedsucc = false;
            boolean dirExists = false;
            if (!targetLocation.exists()) {
                File dir = new File(sdCard + "/XDelQueue/");
                dirExists = dir.isDirectory();
                targetLocation_mkdirBoolsucc = !dirExists ? targetLocation.getParentFile().mkdirs() : true;
            }
            if (targetLocation_mkdirBoolsucc) {
                targetLocation.createNewFile();
                targetLocationFileCreatedsucc = targetLocation.exists();
            }

            copiedFileFilePath = targetLocation.getAbsolutePath().toString();
            // 1 = move the file, 2 = copy the file
            int actionChoice = 2;
            // moving the file to another directory
            if (actionChoice == 1 && targetLocationFileCreatedsucc) {

                if (sourceLocation.renameTo(targetLocation)) {
                    writeToFile("Move file successfully.", "", "", "", "");
                    succ = true;
                } else {
                    succ = false;
                    writeToFile("Move file failed..", "", "", "", "");
                }
            }
            // we will copy the file
            else {
                // make sure the target file exists
                if (sourceLocation.exists() && targetLocationFileCreatedsucc) {
                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);
                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    succ = true;
                    writeToFile("Copy file successfully.", "", "", "", "");
                } else {
                    succ = false;
                    writeToFile("Move file failed. Source file missing.", "", "", "", "");
                }
            }

        } catch (Exception e) {
            succ = false;
            copiedFileFilePath = "";
            writeToFile("Exception at objGlobalMethods copyFileToQueueFolder. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return succ ? copiedFileFilePath : "";
        }
    }

    public static boolean isLukeScreen() {
        boolean asd = false;

        try {
            asd = ((myApp.currentActivity != null) &&
                    (
                            myApp.currentActivity.getClass() == AboutActivity.class ||
                                    myApp.currentActivity.getClass() == AddressListNewActivity.class ||
                                    myApp.currentActivity.getClass() == AddressListRoutingActivity.class ||
                                    myApp.currentActivity.getClass() == BulkPickUpActivity.class ||
                                    myApp.currentActivity.getClass() == Camera5Activity.class ||
                                    myApp.currentActivity.getClass() == CheckListActivity.class ||
                                    myApp.currentActivity.getClass() == CheckStatusActivity.class ||
                                    myApp.currentActivity.getClass() == CODupdate.class ||
                                    myApp.currentActivity.getClass() == CreditCardActivity.class ||
                                    myApp.currentActivity.getClass() == CustomGallery2Activity.class ||
                                    myApp.currentActivity.getClass() == CustomGalleryActivity.class ||
                                    myApp.currentActivity.getClass() == Dashboard2Activity.class ||
                                    myApp.currentActivity.getClass() == DashboardSubActivity.class ||
                                    myApp.currentActivity.getClass() == DOinvCheckActivity.class ||
                                    myApp.currentActivity.getClass() == DrawerMain2Activity.class ||
                                    myApp.currentActivity.getClass() == DrawerMainActivity.class ||
                                    myApp.currentActivity.getClass() == DriverPointMain_Activity.class ||
                                    myApp.currentActivity.getClass() == DriverPointSummaryActivity.class ||
                                    myApp.currentActivity.getClass() == ItemGroupCheck.class ||
                                    myApp.currentActivity.getClass() == JobInfoActivity.class ||
                                    myApp.currentActivity.getClass() == Jobs_Retrieval_Activity.class ||
                                    myApp.currentActivity.getClass() == JobsheetActivity.class ||
                                    myApp.currentActivity.getClass() == JobsheetSubmitActivity.class ||
                                    myApp.currentActivity.getClass() == JobsListGroupActivity.class ||
                                    myApp.currentActivity.getClass() == Label_Tagging_Activity.class ||
                                    myApp.currentActivity.getClass() == MainActivity.class ||
                                    myApp.currentActivity.getClass() == MainCODActivity.class ||
                                    myApp.currentActivity.getClass() == MapsActivity.class ||
                                    myApp.currentActivity.getClass() == MapsAllActivity.class ||
                                    myApp.currentActivity.getClass() == MassUpdateOKPwP2.class ||
                                    myApp.currentActivity.getClass() == Messaging.class ||
                                    myApp.currentActivity.getClass() == Messaginglist.class ||
                                    myApp.currentActivity.getClass() == MPD_CheckOutActivity.class ||
                                    myApp.currentActivity.getClass() == OtpActivity.class ||
                                    myApp.currentActivity.getClass() == QRReaderActivity.class ||
                                    myApp.currentActivity.getClass() == SecuritySealActivity.class ||
                                    myApp.currentActivity.getClass() == setDeliveredMassStatusActivity.class ||
                                    myApp.currentActivity.getClass() == setDeliveredStatusActivity.class ||
                                    myApp.currentActivity.getClass() == setDunningDeliveredStatusActivity.class ||
                                    myApp.currentActivity.getClass() == setFailStatusActivity.class ||
                                    myApp.currentActivity.getClass() == SettingsActivity.class ||
                                    myApp.currentActivity.getClass() == SignatureNameActivity.class ||
                                    myApp.currentActivity.getClass() == UpdateCOD_List.class ||
                                    myApp.currentActivity.getClass() == ViewImageActivity.class ||
                                    myApp.currentActivity.getClass() == ViewNotificationActivity.class ||
                                    myApp.currentActivity.getClass() == CallActivity.class ||
                                    myApp.currentActivity.getClass() == CageInOutActivity.class ||
                                    myApp.currentActivity.getClass() == CageScanActivity.class ||
                                    myApp.currentActivity.getClass() == ChangePwdActivity.class ||
                                    myApp.currentActivity.getClass() == ProfileActivity.class ||
                                    myApp.currentActivity.getClass() == StaffPassActivity.class ||
                                    myApp.currentActivity.getClass() == LeaveActivity.class ||
                                    myApp.currentActivity.getClass() == LeaveApplicationActivity.class
                    )
            );
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods isLukeScreen. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private static boolean appInForeground(@NonNull Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            if (runningAppProcesses == null) {
                return false;
            }

            for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
                if (runningAppProcess.processName.equals(context.getPackageName()) &&
                        runningAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods isLukeScreen. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            return false;
        }
    }

    public static boolean isApplicationBroughtToBackground() {
        Context ctx = myApp.getAppContext();
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(ctx.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static ScreenShotContentObserver getScreenshotObserver() {
        ScreenShotContentObserver screenShotContentObserver = null;
        try {
            HandlerThread handlerThread = new HandlerThread("content_observer");
            handlerThread.start();
            final Handler handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                }
            };

            screenShotContentObserver = new ScreenShotContentObserver(handler, myApp.getAppContext()) {
                @Override
                protected void onScreenShot(String path, String fileName) {
                    //final boolean isInBackground = isApplicationBroughtToBackground();
                    final boolean appInForeground = appInForeground(myApp.getAppContext());
                    //final boolean isLukeActivity = isLukeScreen();
                    SharedPreferences gcmp = getGcmPreferences();
                    //if (!isInBackground) {
                    if (appInForeground) {
                        File file = new File(path); //this is the file of screenshot image
                        String spath = file.getParent();
                        final File file2 = new File((spath + "/" + fileName));
                        String copiedFileFilePath = "";
                        String copiedFilefileName = getGCMPrefs(getGcmPreferences(), "copiedFilefileName");

                        if (file2 != null && file2.exists()) {
                            if (!copiedFilefileName.equals(fileName)) {
                                copiedFileFilePath = GetcopyFileToQueueFolder(file2, fileName);
                                setGCMPrefs(getGcmPreferences(), "copiedFilefileName", fileName);
                            } else {
                                copiedFileFilePath = "";
                            }
                            //copiedFileFilePath = GetcopyFileToQueueFolder(file2, fileName);
                            ////copiedFileFilePath = getcopyFileToImagesFolder(file);

                            final Handler handler = new Handler();
                            //final File ff = file;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    //ff.delete();
                                    file2.delete();
                                }
                            }, 10000);

                            Context ctx = myApp.getAppContext();
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            ctx.sendBroadcast(mediaScanIntent);
                        }
                        if (!objGlobalMethods.IsEmptyString(copiedFileFilePath)) {
                            prepareSendSSEmail(copiedFileFilePath);
                        }
                    }
                }
            };
        } catch (Exception e) {
            screenShotContentObserver = null;
            writeToFile("Exception at objGlobalMethods ScreenshotObserver. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return screenShotContentObserver;
        }
    }

    public static void prepareSendPR_LateMsg0Email(String situation) {
        String[] UsernamePwd = new String[4];
        String prevLoginUserName;
        SharedPreferences gcmp = getGcmPreferences();
        String FullName = objGlobalMethods.getGCMPrefs(gcmp, "FullName");
        if (FullName.isEmpty() || FullName.equals("") || FullName == "") {
            FullName = objGlobalMethods.getGCMPrefs(gcmp, "username");
        }
        String JobNo = objGlobalMethods.getGCMPrefs(gcmp, "currJobInfoJobNo");
        String JobsIDX = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoIDX);
        try {
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
            prevLoginUserName = objGlobalMethods.getGCMPrefs(gcmp, "prevLoginUserName");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date dt = new Date();
            String timestamp = formatter.format(dt);
            Location lastknownlocation = getLastKnownLocationForJobUpdate();
            String locationStr = "";

            if (lastknownlocation.getLatitude() > 0 && lastknownlocation.getLongitude() > 0) {
                locationStr = "Location: " + String.valueOf(lastknownlocation.getLatitude()) + "," + String.valueOf(lastknownlocation.getLongitude()) + " https://www.google.com/maps?t=m&q="
                        + String.valueOf(lastknownlocation.getLatitude()) + "," + String.valueOf(lastknownlocation.getLongitude());
            }

            String msg = "GC: " + (FullName != null && !FullName.isEmpty() && !FullName.equals("") && FullName != "" ? FullName
                    : prevLoginUserName != null && !prevLoginUserName.isEmpty() && !prevLoginUserName.equals("") && prevLoginUserName != "" ? prevLoginUserName : "");
            msg += "\nDateTime: " + timestamp;
            if (!locationStr.isEmpty() && !locationStr.equals("") && locationStr != "") {
                msg += "\n" + locationStr;
            }
            msg += "\nJob No.: " + JobNo;
            msg += "\nJob IDX.: " + JobsIDX;

            msg += !IsEmptyString(situation) ? "\n\nSituation: " + situation : "";

            String[] toArr = {"it@xdel.com", "aci@xdel.com"};
            //String[] toArr = {"kean.ong@xdel.com"};
            objEmailItem ei = new objEmailItem(toArr, "Job not updated after given duration.", msg, "", "Job not updated after given duration alert email at " + timestamp + " prepared.");
            ei.setDeleteAttachementFile(true);
            writeToFile("Job not updated after given duration alert email at " + timestamp + " prepared.", "", "", "", "");
            JobsHandler.addEmailToQue(ei);
            Context ctx = myApp.getAppContext();
            ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods prepareSendPR_LateMsg0Email. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static void prepareSendSSEmail(String copiedFileFilePath) {
        String[] UsernamePwd = new String[4];
        String username;
        String prevLoginUserName;
        SharedPreferences gcmp = getGcmPreferences();
        String FullName = objGlobalMethods.getGCMPrefs(gcmp, "FullName");
        if (FullName.isEmpty() || FullName.equals("") || FullName == "") {
            FullName = objGlobalMethods.getGCMPrefs(gcmp, "username");
        }
        try {
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
            username = UsernamePwd[0];
            prevLoginUserName = objGlobalMethods.getGCMPrefs(gcmp, "prevLoginUserName");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date dt = new Date();
            String timestamp = formatter.format(dt);
            String deviceInfo = getDeviceInfo();
            Location lastknownlocation = getLastKnownLocationForJobUpdate();
            String locationStr = "";

            if (lastknownlocation.getLatitude() > 0 && lastknownlocation.getLongitude() > 0) {
                locationStr = "Location: " + String.valueOf(lastknownlocation.getLatitude()) + "," + String.valueOf(lastknownlocation.getLongitude()) + " https://www.google.com/maps?t=m&q="
                        + String.valueOf(lastknownlocation.getLatitude()) + "," + String.valueOf(lastknownlocation.getLongitude());
            }

            /*String msg = "GC: " + (username != null && !username.isEmpty() && !username.equals("") && username != "" ? username
                    : prevLoginUserName != null && !prevLoginUserName.isEmpty() && !prevLoginUserName.equals("") && prevLoginUserName != "" ? prevLoginUserName : "");*/
            String msg = "GC: " + (FullName != null && !FullName.isEmpty() && !FullName.equals("") && FullName != "" ? FullName
                    : prevLoginUserName != null && !prevLoginUserName.isEmpty() && !prevLoginUserName.equals("") && prevLoginUserName != "" ? prevLoginUserName : "");
            msg += "\nDateTime: " + timestamp;
            msg += "\nScreen: " + myApp.currentActivity.getClass().getSimpleName();
            if (!locationStr.isEmpty() && !locationStr.equals("") && locationStr != "") {
                msg += "\n" + locationStr;
            }

            msg += "\n\nDevice Info\n" + deviceInfo;

            //String[] toArr = {"it@xdel.com", "harold.lee@xdel.com", "aci@xdel.com"};
            String[] toArr = {"kean.ong@xdel.com"};
            objEmailItem ei = new objEmailItem(toArr, "LUKE Screenshot Alert!", msg, copiedFileFilePath, "Screenshot alert email at " + timestamp + " prepared.");
            //objEmailItem ei = new objEmailItem(toArr, "LUKE Screenshot Alert!", msg, "");
            ei.setDeleteAttachementFile(true);
            writeToFile("Screenshot alert email at " + timestamp + " prepared.", "", "", "", "");
            JobsHandler.addEmailToQue(ei);
            Context ctx = myApp.getAppContext();
            ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods prepareSendSSEmail. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static void prepareSendEmail(String[] toArr, String subj, String bodytxt, String AttachmentFilePath, boolean DeleteAttachmentFile, String EmailObjectiveTitle) {
        String[] UsernamePwd = new String[4];
        String username;
        try {
            //String[] toArr = {"kean.ong@xdel.com"};
            writeToFile("Email added to queue. Subject: " + subj, "", "", "", "");
            objEmailItem ei = new objEmailItem(toArr, subj, bodytxt, AttachmentFilePath, EmailObjectiveTitle);
            ei.setDeleteAttachementFile(DeleteAttachmentFile);
            JobsHandler.addEmailToQue(ei);
            /*Context ctx = myApp.getAppContext();
            ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));*/
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods prepareSendEmail. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static class sendSSEmail extends AsyncTask<Void, Void, Boolean> {
        private final Mail m;
        private final String attachmentfilename;
        private final String subjectstr;
        private final String bodystr;
        private final File myFile;

        public sendSSEmail(Mail amail, String aattachmentfilename, String abodystr, String asubjectstr, File amyFile) {
            this.m = amail;
            this.attachmentfilename = aattachmentfilename;
            this.subjectstr = asubjectstr;
            this.bodystr = abodystr;
            this.myFile = amyFile;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            mailSent = false;
            //String[] toArr = {"it@xdel.com", "harold.lee@xdel.com"};
            String[] toArr = {"kean.ong@xdel.com", "it@xdel.com"};
            m.setTo(toArr);
            m.setFrom("luke@xdel.com"); // pwd XDel2015
            if (subjectstr.equals("")) {
                m.setSubject("Luke report.");
            } else {
                m.setSubject(subjectstr);
            }

            if (bodystr.equals("")) {
                m.setBody("Luke report from handheld.");
            } else {
                m.setBody(bodystr);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //android.os.Debug.waitForDebugger();
            //Call Web Method
            try {
                if (!attachmentfilename.equals("")) {
                    m.addAttachment(attachmentfilename);
                }
                return m.send();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                writeToFile("There was a problem sending the email at sendSSEmail doInBackground. " + e.getMessage() + "\nStack:\n" + s, "", "", "", "");
                return null;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean success) {
            if (success != null) {
                if (success == true) {
                    if (!attachmentfilename.equals("") && myFile != null) {
                        myFile.delete();
                    }
                }
            }
        }
    }

    public static class sendEmail extends AsyncTask<Void, Void, Boolean> {

        private final Mail m;
        private final String attachmentfilename;
        private final String subjectstr;
        private final String bodystr;
        private final File myFile;

        public sendEmail(Mail amail, String aattachmentfilename, String abodystr, String asubjectstr, File amyFile) {
            this.m = amail;
            this.attachmentfilename = aattachmentfilename;
            this.subjectstr = asubjectstr;
            this.bodystr = abodystr;
            this.myFile = amyFile;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            mailSent = false;
            String[] toArr = {"it@xdel.com"};
            m.setTo(toArr);
            m.setFrom("luke@xdel.com"); // pwd XDel2015
            if (subjectstr.equals("")) {
                m.setSubject("Luke report.");
            } else {
                m.setSubject(subjectstr);
            }

            if (bodystr.equals("")) {
                m.setBody("Luke report from handheld.");
            } else {
                m.setBody(bodystr);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //android.os.Debug.waitForDebugger();
            //Call Web Method
            try {
                if (!attachmentfilename.equals("")) {
                    m.addAttachment(attachmentfilename);
                }
                return m.send();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                writeToFile("There was a problem sending the email at sendEmail doInBackground. Exception" + e.getMessage() + "\nStack:\n" + s, "", "", "", "");
                return null;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean success) {
            if (success != null) {
                if (success == true) {
                    /*if (!attachmentfilename.equals("") && myFile != null) {
                        myFile.delete();
                    }*/
                }
            }
        }
    }

    public static void sendEmail3(Mail m, String attachmentfilename, String subjectstr, String bodystr, File myFile) {
        Date dt = new Date();
        Boolean success = false;
        try {
            mailSent = false;
            String[] toArr = {"kean.ong@xdel.com"};
            m.setTo(toArr);
            m.setFrom("luke@xdel.com"); // pwd XDel2015
            if (subjectstr.equals("")) {
                m.setSubject("Luke report.");
            } else {
                m.setSubject(subjectstr);
            }

            if (bodystr.equals("")) {
                m.setBody("Luke report from handheld.");
            } else {
                m.setBody(bodystr);
            }

            success = sendEmailSuccessful(m, attachmentfilename);
            if (success == true) {
                if (!attachmentfilename.equals("") && myFile != null) {
                    myFile.delete();
                }
            }
        } catch (Exception e) {
            writeToFile("Exception caught at objGlobalMethods.sendEmail3. Exception: " + e.toString(), "", "", "", "");
        }
    }

    public static boolean sendEmails(String[] toArr, String subjectstr, String bodystr, String attachmentfilename, File myFile) {
        boolean success = false;
        try {
            Mail m = new Mail("luke@xdel.com", "XDel2015");
            m.setFrom("luke@xdel.com"); // pwd XDel2015
            m.setTo(toArr);
            if (subjectstr.equals("")) {
                m.setSubject("Luke report.");
            } else {
                m.setSubject(subjectstr);
            }
            if (bodystr.equals("")) {
                m.setBody("Luke report from handheld.");
            } else {
                m.setBody(bodystr);
            }
            success = sendEmailSuccessful(m, attachmentfilename);
            /*if (success == true) {
                if (!attachmentfilename.equals("") && myFile != null) {
                    myFile.delete();
                }
            }*/
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods sendEmails. Exception: " + e.getMessage(), "", "", "", "");
            success = false;
        } finally {
            return success;
        }
    }

    private static Boolean sendEmailSuccessful(Mail m, String attachmentfilename) {
        Boolean asd = false;
        try {
            if (!IsEmptyString(attachmentfilename)) {
                m.addAttachment(attachmentfilename);
            }
            //return m.send();
            asd = m.send();
        } catch (Exception e) {
            //String s = JobsHandler.getStackTraceString(e);
            //writeToFile("There was a problem sending the email at sendEmailSuccessful. Exception: " + e.getMessage() + "\nStack:\n" + s, "", "", "", "");
            writeToFile("There was a problem sending the email at sendEmailSuccessful. Exception: " + e.getMessage(), "", "", "", "");
            //return false;
            asd = false;
        } finally {
            return asd;
        }
    }

    public static void checkGPSnPerMinRunAlarmRunning(String ishomesuser) {
        try {
            if (!Boolean.parseBoolean(ishomesuser)) {
                if (!GPSalarmStarted) {
                    //start grapping GPS location and send locations coordinates
                    //objGlobalMethods.writeToFile("objGlobalMethods checkGPSnPerMinRunAlarmRunning calling startGpsAlarm()", "", "", "", "");
                    startGpsAlarm();
                }
                //startGpsAlarm();

                /*if (!PerMinRunalarmStarted) {
                    writeToFile("PerMinRunalarmStarted not started. Starting it now.", "", "", "", "");
                    startPerMinuteAlarm();
                }*/
                /*if (!NewPerMinRunalarmStarted) {
                    writeToFile("NewPerMinRunalarmStarted not started. Starting it now.", "", "", "", "");
                    startNewPerMinAlarm();
                }*/
            }
        } catch (Exception e) {
            writeToFile("Exception caught at objGlobalMethods checkGPSnPerMinRunAlarmRunning. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void startNewPerMinAlarm() {
        try {
            stopNewPerMinAlarm();
            Context ctx = myApp.getApplicationCtx();
            int interval = 60000; //10s
            SharedPreferences GcmPreferences = ctx.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);

            NewPerMinRunManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(ctx, NewPerMinBCR.class);
            NewPerMinRunPendingIntent = PendingIntent.getBroadcast(ctx, 0, alarmIntent, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            //NewPerMinRunManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, NewPerMinRunPendingIntent);
            NewPerMinRunManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    interval, // 60000 = 1 minute
                    NewPerMinRunPendingIntent);
            writeToFile("startNewPerMinAlarm started.", "", "", "", "");
            NewPerMinRunalarmStarted = true;
        } catch (Exception e) {
            writeToFile("Exception caught at objGlobalMethods startNewPerMinAlarm. Exception: , " + e.getMessage().toString(),
                    "", "", "", "");
        }
    }

    public static void stopNewPerMinAlarm() {
        try {
            if (NewPerMinRunManager != null) {
                NewPerMinRunManager.cancel(NewPerMinRunPendingIntent);
            }
            NewPerMinRunalarmStarted = false;
            writeToFile("stopNewPerMinAlarm stopped.", "", "", "", "");
        } catch (Exception e) {
            writeToFile("Exception caught at objGlobalMethods stopNewPerMinAlarm. Exception: , " + e.getMessage().toString(),
                    "", "", "", "");
        }
    }

    public static void startGpsAlarm() {
        try {
            stopGpsAlarm();
            Context ctx = myApp.getAppContext();
            int interval = 20000; //20s
            SharedPreferences GcmPreferences = ctx.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);

            GPSmanager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(ctx, LocationService_Receiver.class);
            GPSpendingIntent = PendingIntent.getBroadcast(ctx, 0, alarmIntent, 0);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "stopLocationService", "");
            String intervalStr = GcmPreferences.getString("GPSalarmInterval", "");
            if (!intervalStr.equals("")) {
                interval = Integer.parseInt(intervalStr);
            } else {
                interval = 10000; //10s
                SharedPreferences.Editor editor = GcmPreferences.edit();
                editor.putString("GPSalarmInterval", String.valueOf(interval));
                editor.apply();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            GPSmanager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    interval, // 60000 = 1 minute
                    GPSpendingIntent);
            //writeToFile("startGpsAlarm()", "", "", "", "");
        } catch (Exception e) {
            writeToFile("Exception caught at objGlobalMethods startGpsAlarm. Exception: , " + e.getMessage().toString(),
                    "", "", "", "");
        }
    }

    public static void stopGpsAlarm() {
        try {
            if (GPSmanager != null) {
                GPSmanager.cancel(GPSpendingIntent);
            }
            GPSalarmStarted = false;
            LocationService.currentlyProcessingLocation = false;
            LocationService.stopLocationUpdates();
            //writeToFile("GPSalarm stopped.", "", "", "", "");
            Context ctx = myApp.getAppContext();
            SharedPreferences gcmp = ctx.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            objGlobalMethods.setGCMPrefs(gcmp, "stopLocationService", "true");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception caught at objGlobalMethods stopGpsAlarm. Exception: , " + e.getMessage().toString() + "\nStack: " + s,
                    "", "", "", "");
        }
    }

    public static void startGPSAlarmHandler() {
        try {
            stopGPSAlarmHandler();
            Context ctx = myApp.getAppContext();
            int interval = 20000; //20s
            SharedPreferences GcmPreferences = ctx.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            Intent alarmIntent = new Intent(ctx, LocationService_Receiver.class);
            GPSpendingIntent = PendingIntent.getBroadcast(ctx, 0, alarmIntent, 0);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "stopLocationService", "");
            String intervalStr = GcmPreferences.getString("GPSalarmInterval", "");
            if (!intervalStr.equals("")) {
                interval = Integer.parseInt(intervalStr);
            } else {
                interval = 10000; //10s
                SharedPreferences.Editor editor = GcmPreferences.edit();
                editor.putString("GPSalarmInterval", String.valueOf(interval));
                editor.apply();
            }

            GPSHandler = new Handler();
            GPSHandler.postDelayed(getLastLocation, 10000);
            /*startGPSAlarmHandlerAsync t = new startGPSAlarmHandlerAsync(10000);
            t.execute();*/
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods startGPSAlarmHandler. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    private static Runnable getLastLocation = new Runnable() {
        @Override
        public void run() {
            try {
                //writeToFile("Starting LocationService_Receiver", "", "", "", "");
                Context context = myApp.getAppContext();
                context.sendBroadcast(new Intent(context, LocationService_Receiver.class));
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                writeToFile("Exception at objGlobalMethods getLastLocation. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            }
        }
    };

    public static void stopGPSAlarmHandler() {
        try {
            if (GPSHandler != null) {
                GPSHandler.removeCallbacks(getLastLocation);
            }
            GPSalarmStarted = false;
            LocationService.currentlyProcessingLocation = false;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods startGPSAlarmHandler. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void startPerMinuteAlarm() {
        try {
            Context ctx = myApp.getAppContext();
            if (!PerMinRunalarmStarted) {
                Intent alarmIntent = new Intent(ctx, PerMinuteRun_BroadcastReceiver.class);
                PerMinRunPendingIntent = PendingIntent.getBroadcast(ctx, 0, alarmIntent, 0);
                PerMinRunManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                //int interval = 30000;
                int interval = 60000;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                PerMinRunManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, PerMinRunPendingIntent);
                PerMinRunalarmStarted = true;
            }
        } catch (Exception e) {
            Date dt = new Date();
            writeToFile("Exception caught at objGlobalMethods startPerMinuteAlarm. Exception: , " + e.getMessage().toString(),
                    "", "", "", "");
        }
    }

    public static void stopPerMinRunAlarm() {
        try {
            if (PerMinRunManager != null) {
                PerMinRunManager.cancel(PerMinRunPendingIntent);
            }
            PerMinRunalarmStarted = false;
            //writeToFile("PerMinRunAlarm stopped.", "", "", "", "");
        } catch (Exception e) {
            Date dt = new Date();
            writeToFile("Exception caught at objGlobalMethods stopPerMinRunAlarm. Exception: , " + e.getMessage().toString(),
                    "", "", "", "");
        }
    }

    public static Boolean getMsgReminderBool(SharedPreferences GcmPreferences) {
        Date dt = new Date();
        String unreadMsgExist = "";
        Boolean unreadMsgExistBool = false;
        try {
            unreadMsgExist = getGCMPrefs(GcmPreferences, "unreadMsgExist");
            if (unreadMsgExist == null || unreadMsgExist.equals("") || unreadMsgExist.equals("false")) {
                unreadMsgExistBool = false;
            } else if (unreadMsgExist.equals("true")) {
                unreadMsgExistBool = true;
            }

        } catch (Exception e) {
            writeToFile("Exception caught at objGlobalMethods msgReminder. Exception: " + e.toString(), "", "", "", "");
            unreadMsgExistBool = false;
        } finally {
            return unreadMsgExistBool;
        }
    }

    public static void showIarrived(LinearLayout LLone, LinearLayout LLtwo, Boolean show) {
        try {
            if (show) {
                LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) LLone.getLayoutParams();
                lParams.weight = 1f;
                LLone.setLayoutParams(lParams);
                LinearLayout.LayoutParams lParams2 = (LinearLayout.LayoutParams) LLtwo.getLayoutParams();
                lParams2.weight = 8.3f;
                LLone.setLayoutParams(lParams);
            } else {
                LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) LLone.getLayoutParams();
                lParams.weight = 0f;
                LLone.setLayoutParams(lParams);
                LinearLayout.LayoutParams lParams2 = (LinearLayout.LayoutParams) LLtwo.getLayoutParams();
                lParams2.weight = 9.3f;
                LLone.setLayoutParams(lParams);
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            writeToFile("Exception caught at objGlobalMethods showIarrived. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    public static void getMsgReminder(SharedPreferences GcmPreferences, String ActivityClassName, LinearLayout LLone, LinearLayout LLtwo) {
        String unreadMsgExist = "";
        Boolean unreadMsgExistBool = false;
        int unreadMsgCount = 0;

        try {
            unreadMsgCount = DAO_Jobs.getUnreadMessageCount();
            String DeviceResolution = getDeviceResolution();
            int catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            if (!ActivityClassName.equals("") && LLone != null && LLtwo != null && unreadMsgCount == 0) {
                LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) LLone.getLayoutParams();
                lParams.weight = 0f;
                LLone.setLayoutParams(lParams);
                LinearLayout.LayoutParams lParams2 = (LinearLayout.LayoutParams) LLtwo.getLayoutParams();

                lParams2.weight = ActivityClassName.equals("AddressListRoutingActivity") || (ActivityClassName.equals("DrawerMain2Activity") && DeviceResolution.equals("XXHDPI")) ? 10f :
                        ActivityClassName.equals("DrawerMain2Activity") ? 8.2f :
                                (ActivityClassName.equals("DrawerMainActivity") && !DeviceResolution.equals("XXHDPI")) ? 8.2f :
                                        ActivityClassName.equals("AddressListNewActivity") && catType == 2 ? 8f : 9f;

                ////with Distance travelled
                /*lParams2.weight = ActivityClassName.equals("AddressListRoutingActivity") || (ActivityClassName.equals("DrawerMain2Activity") && DeviceResolution.equals("XXHDPI")) ? 10f :
                        ActivityClassName.equals("DrawerMain2Activity") ? 8.2f :
                                (ActivityClassName.equals("DrawerMainActivity") && !DeviceResolution.equals("XXHDPI")) ? 8.2f :
                                        ActivityClassName.equals("AddressListNewActivity") && catType == 2 ? 7f : 8f;*/

                LLtwo.setLayoutParams(lParams2);
            } else if (!ActivityClassName.equals("") && LLone != null && LLtwo != null && unreadMsgCount > 0) {
                LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) LLone.getLayoutParams();
                lParams.weight = 1f;
                LLone.setLayoutParams(lParams);
                LinearLayout.LayoutParams lParams2 = (LinearLayout.LayoutParams) LLtwo.getLayoutParams();

                lParams2.weight = ActivityClassName.equals("AddressListRoutingActivity") || (ActivityClassName.equals("DrawerMain2Activity") && DeviceResolution.equals("XXHDPI")) ? 8f :
                        ActivityClassName.equals("DrawerMain2Activity") ? 7.2f :
                                (ActivityClassName.equals("DrawerMainActivity") && !DeviceResolution.equals("XXHDPI")) ? 7.1f :
                                        ActivityClassName.equals("AddressListNewActivity") && catType == 2 ? 7f : 8f;

                ////with Distance travelled
                /*lParams2.weight = ActivityClassName.equals("AddressListRoutingActivity") || (ActivityClassName.equals("DrawerMain2Activity") && DeviceResolution.equals("XXHDPI")) ? 8f :
                        ActivityClassName.equals("DrawerMain2Activity") ? 7.2f :
                                (ActivityClassName.equals("DrawerMainActivity") && !DeviceResolution.equals("XXHDPI")) ? 7.1f :
                                        ActivityClassName.equals("AddressListNewActivity") && catType == 2 ? 7f : 8f;*/

                LLtwo.setLayoutParams(lParams2);
            }
        } catch (Exception e) {
            writeToFile("Exception caught at objGlobalMethods msgReminder. Exception: " + e.toString(), "", "", "", "");

        }
    }

    public static ChatMessage createNewDateCM(Long d) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String dateText = "";
        String todayDateText = dateFormat.format(new Date());
        ChatMessage newDatecm = new ChatMessage();
        try {
            newDatecm.setmessageDate(d);
            newDatecm.setMessageTime(d);
            Date dd = new Date(d);
            dd = objGlobalMethods.getZeroTimeDate(dd);
            dateText = dateFormat.format(dd);
            newDatecm.setMessageText(dateText.equals(todayDateText) ? "Today" : dateText);
            newDatecm.setMessageStatus(Status.SENT);
            newDatecm.setUserType(UserType.DATE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods createNewDateCM. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        } finally {
            return newDatecm;
        }
    }

    public static Double[] getJobLATLNG(objJobInfo ji, int catType) {
        Date dt = new Date();
        Double[] LATLNG = null;
        Double LAT = 0.0;
        Double LNG = 0.0;
        try {
            if (catType == 0 || catType == 1 || catType == 4) {
                if (ji.PURedirectedLocation != null) {
                    LAT = ji.PURedirectedLocation.LAT;
                    LNG = ji.PURedirectedLocation.LNG;
                } else if (ji.PULocation != null) {
                    LAT = ji.PULocation.LAT;
                    LNG = ji.PULocation.LNG;
                }
                LATLNG = new Double[]{LAT, LNG};
            } else if (catType == 2 || catType == 3 || catType == 5) {
                if (ji.DLRedirectedLocation != null) {
                    LAT = ji.DLRedirectedLocation.LAT;
                    LNG = ji.DLRedirectedLocation.LNG;
                } else if (ji.DLLocation != null) {
                    LAT = ji.DLLocation.LAT;
                    LNG = ji.DLLocation.LNG;
                }
                LATLNG = new Double[]{LAT, LNG};
            }
        } catch (Exception e) {
            writeToFile("Exception caught at objGlobalMethods getJobLATLNG. Exception: " + e.toString(), "", "", "", "");
            LATLNG = null;
        } finally {
            return LATLNG;
        }
    }

    public static void resetAllImageRelatedStaticVariables(boolean ExcludeFailStatusActivity) {
        Camera5Activity.mCurrentPhotoPath = null;

        SignatureNameActivity.rtnPageName = "";
        setDeliveredStatusActivity.signatureString = "";
        setDeliveredStatusActivity.selectedSpecificIDX = null;
        setDeliveredMassStatusActivity.signatureString = "";
        setDeliveredMassStatusActivity.selectedSpecificIDX = null;
        setDunningDeliveredStatusActivity.signatureString = "";

        if (!ExcludeFailStatusActivity) {
            setFailStatusActivity.imagePaths = null;
            setFailStatusActivity.dataT = null;
            setFailStatusActivity.isFromSetFailSatus = false;
        }

        String skiplocker = getGCMPrefs(getGcmPreferences(), "skiplocker");
        if (!IsEmptyString(skiplocker)) {
            //do nothing
        } else {
            setDeliveredStatusActivity.imagePaths = null;
            setDeliveredStatusActivity.SimagePaths = null;
            setDeliveredStatusActivity.dataT = null;
            setDeliveredStatusActivity.failPart2FromPart1Bool = false;
        }

        setDeliveredStatusActivity.ddlSelectedIndex = 0;
        setDeliveredStatusActivity.ishuman = "0";
        setDeliveredStatusActivity.selectedRecipient = "";
        setDeliveredStatusActivity.selectedSpecific = null;
        setDeliveredStatusActivity.isFromSetOKP = false;
        setDeliveredStatusActivity.remarks = "";

        setDeliveredMassStatusActivity.imagePaths = null;
        setDeliveredMassStatusActivity.SimagePaths = null;
        setDeliveredMassStatusActivity.dataT = null;
        setDeliveredMassStatusActivity.ddlSelectedIndex = 0;
        setDeliveredMassStatusActivity.ishuman = "0";
        setDeliveredMassStatusActivity.selectedRecipient = "";
        setDeliveredMassStatusActivity.selectedSpecific = null;
        setDeliveredMassStatusActivity.isFromMassSetOKP = false;
        setDeliveredMassStatusActivity.hasPartOne = false;
        setDeliveredMassStatusActivity.remarks = "";

        MassUpdateOKPwP2.massupdateokpwp2PostDataArr = null;
        MassUpdateOKPwP2.isFailFromMassUpdateOKPwP2 = false;

        LeaveApplicationActivity.dataT = null;
        LeaveApplicationActivity.imagePaths = null;
        LeaveApplicationActivity.isFromLeaveApplication = false;

        CustomGalleryActivity.dataT = null;
        CustomGalleryActivity.imagePaths = null;

        /*CheckListActivity.PhotoImagePathList = null;
        CheckListActivity.dataTlist = null;*/
    }

    public static Boolean verifyBarcodeForSearchFunction(String barcode) {
        Date dt = new Date();
        Boolean asd = true;
        try {
            if (barcode.length() != 10) {
                asd = false;
            } else {
                Boolean extraValidation = false;
                try {
                    Long extraVal = Long.parseLong(barcode.substring(2, 8));
                    extraValidation = true;
                } catch (Exception e) {
                    extraValidation = false;
                }

                if (extraValidation) {
                    String Val = barcode.substring(0, 8);
                    String CheckSum = barcode.substring(8, 10).toUpperCase();
                    byte[] asc = Val.getBytes("US-ASCII");

                    int agg = (asc[0] * 41) +
                            (asc[1] * 59) +
                            (asc[2] * 37) +
                            (asc[3] * 73) +
                            (asc[4] * 31) +
                            (asc[5] * 27) +
                            (asc[6] * 23) +
                            (asc[7] * 89);

                    String Verify = Integer.toHexString(agg % 256).toUpperCase();
                    if (Verify.length() == 1) {
                        Verify = "0" + Verify;
                    }

                    asd = CheckSum.equals(Verify);
                } else {
                    asd = false;
                }
            }
        } catch (Exception e) {
            writeToFile("Exception caught at objGlobalMethods verifyBarcodeForSearchFunction. Returning FALSE. Exception: " + e.toString(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean verifyBarcode(String barcode) {
        Date dt = new Date();
        Boolean asd = true;
        try {
            String xbarcode = barcode.toUpperCase();
            if (xbarcode.length() != 10) {
                //writeToFile("objGlobalMethods verifyBarcode returns FALSE due length less than 10.", "", "", "", "");
                asd = false;
            } else {
                Boolean extraValidation = false;
                try {
                    Long extraVal = Long.parseLong(xbarcode.substring(2, 8));
                    extraValidation = true;
                } catch (Exception e) {
                    extraValidation = false;
                }

                if (extraValidation) {
                    String Val = xbarcode.substring(0, 8);
                    String CheckSum = xbarcode.substring(8, 10).toUpperCase();
                    byte[] asc = Val.getBytes("US-ASCII");

                    int agg = (asc[0] * 41) +
                            (asc[1] * 59) +
                            (asc[2] * 37) +
                            (asc[3] * 73) +
                            (asc[4] * 31) +
                            (asc[5] * 27) +
                            (asc[6] * 23) +
                            (asc[7] * 89);

                    String Verify = Integer.toHexString(agg % 256).toUpperCase();
                    if (Verify.length() == 1) {
                        Verify = "0" + Verify;
                    }
                    //Integer VerifyInt = Integer.parseInt(Verify);
                    asd = CheckSum.equals(Verify);
                    /*if (asd) {
                        writeToFile("objGlobalMethods verifyBarcode returns TRUE.", "", "", "", "");
                    } else {
                        writeToFile("objGlobalMethods verifyBarcode returns FALSE.", "", "", "", "");
                    }*/
                } else {
                    //writeToFile("objGlobalMethods verifyBarcode extraValidation returns false. Middle 6 characters unable to convert to LONG variable.", "", "", "", "");
                    asd = false;
                }
            }
        } catch (Exception e) {
            writeToFile("Exception caught at objGlobalMethods verifyBarcode. Returning FALSE. Exception: " + e.toString(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean getBattChargingState(Context ctx) {
        Boolean isCharging = false;
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = ctx.registerReceiver(null, ifilter);
        try {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
        } catch (Exception e) {
            isCharging = false;
        } finally {
            return isCharging;
        }
    }

    public static Integer getBattLvl(Context ctx) {
        Integer battlvlInt = 0;
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = ctx.registerReceiver(null, ifilter);
        try {
            //get batt lvl
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = ((float) level / (float) scale) * 100.0f;
            battlvlInt = Math.round(batteryPct);
        } catch (Exception e) {
            battlvlInt = 0;
        } finally {
            return battlvlInt;
        }
    }

    public static boolean isScreenDisplayOn(Context ctx) {
        boolean asd = false;
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                DisplayManager dm = (DisplayManager) ctx.getSystemService(Context.DISPLAY_SERVICE);
                for (Display display : dm.getDisplays()) {
                    if (display.getState() == Display.STATE_ON) {
                        asd = true;
                        break;
                    }
                }
            } else {
                PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
                //noinspection deprecation
                asd = pm.isScreenOn();
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods isScreenDisplayOn. Exception: " + e.getMessage(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }

    }

    public static Boolean isScreenOn(Context mContext) {
        Boolean asd = false;
        PowerManager pm = (PowerManager) mContext.getSystemService(
                Context.POWER_SERVICE);

        try {
            if (android.os.Build.VERSION.SDK_INT >= 20) {
                if (pm.isInteractive()) {
                    //Do stuff
                    asd = true;
                }
            } else {
                if (pm.isScreenOn()) {
                    //Do stuff
                    asd = true;
                }
            }
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean isReqStaffID(Integer jobflag) {
        Boolean asd = false;

        try {
            asd = (jobflag & 4096) == 4096 ? true : false;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods isReqStaffID. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean isTotallyCannotSlot(Integer jobflag) {
        Boolean asd = false;
        Boolean intendedRecOnly = false;
        Boolean canSlotLB = false;
        Boolean canSlotUnderDoor = false;
        Boolean canGuard = false;
        try {
            intendedRecOnly = (jobflag & 16) == 16 ? true : false;
            canSlotLB = (jobflag & 1) == 1 ? true : false;
            canSlotUnderDoor = (jobflag & 2) == 2 ? true : false;
            canGuard = (jobflag & 4) == 4 ? true : false;

            if (intendedRecOnly || (!intendedRecOnly && !canSlotLB && !canSlotUnderDoor && !canGuard)) {
                asd = true;
            } else if (!intendedRecOnly && ((canSlotLB || canSlotUnderDoor || canGuard))) {
                asd = false;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods isTotallyCannotSlot. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean isIntendedRecOnly(Integer jobflag) {
        Boolean asd = false;
        try {
            asd = ((jobflag & 16) == 16) ? true : false;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods isIntendedRecOnly. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean canSLOTLBorDOORorDOORSTEP(int f, int f2) {
        boolean asd = false;
        try {
            asd = isCanSlotLB(f) || isCanSlotUnderDoor(f) || CanLeaveDoorStep(f2);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler canSLOTLBorDOORorDOORSTEP. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static Boolean isCanSlotLB(Integer jobflag) {
        Boolean asd = false;
        try {
            asd = (jobflag & 1) == 1 ? true : false;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods isCanSlotLB. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean isCanSlotUnderDoor(Integer jobflag) {
        Boolean asd = false;
        try {
            asd = (jobflag & 2) == 2 ? true : false;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods isCanSlotUnderDoor. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean isCanGuard(Integer jobflag) {
        Boolean asd = false;
        try {
            asd = (jobflag & 4) == 4 ? true : false;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods isCanGuard. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean CanLeaveDoorStep(Integer jobflag2) {
        Boolean asd = false;
        try {
            asd = (jobflag2 & 2048) == 2048;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods CanLeaveDoorStep. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean isPickAndSendJob(Integer jobflag) {
        Boolean asd = false;
        try {
            asd = (jobflag & 8388608) == 8388608 ? true : false;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods isPickAndSendJob. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static View.OnClickListener rtnImgV_OnClickListener(final Integer type) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Context ct = myApp.getAppContext();
                    String msg =
                            type == 1 ? ct.getString(R.string.jobtype_otp)
                                    : type == 2 ? ct.getString(R.string.jobtype_dnf)
                                    : type == 3 ? ct.getString(R.string.jobtype_reqSignature)
                                    : type == 4 ? ct.getString(R.string.jobtype_tradein)
                                    : type == 5 ? ct.getString(R.string.jobtype_2way)
                                    : type == 6 ? ct.getString(R.string.jobtype_nophoto)
                                    : type == 7 ? ct.getString(R.string.jobtype_canSlotLB)
                                    : type == 8 ? ct.getString(R.string.jobtype_canSlotDoor)
                                    : type == 9 ? ct.getString(R.string.jobtype_canGuard)
                                    : type == 10 ? ct.getString(R.string.jobtype_byhand)
                                    : type == 11 ? ct.getString(R.string.jobtype_explicit)
                                    : type == 12 ? ct.getString(R.string.jobtype_nets)
                                    : type == 13 ? ct.getString(R.string.jobtype_visamaster)
                                    : type == 14 ? ct.getString(R.string.jobtype_picknsend_more)
                                    : type == 15 ? ct.getString(R.string.jobtype_picknsend)
                                    : type == 16 ? ct.getString(R.string.jobtype_jobchanges_more)
                                    : type == 17 ? ct.getString(R.string.jobtype_jobchanges)
                                    : type == 18 ? ct.getString(R.string.jobtype_cold)
                                    : "";

                    objGlobalMethods.showDialog(myApp.currentActivity, ct.getString(R.string.buttontext_ok), msg).show();
                } catch (Exception e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    writeToFile("Exception at objGlobalMethods rtnImgV_OnClickListener. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    public static void showToNoteMsgPrompt(String title, ArrayList<String> msgs) {
        Context c = myApp.getAppContext();
        int added = 0;
        try {
            if (msgs != null && msgs.size() > 0) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                final AlertDialog.Builder ToNoteBuilder = new AlertDialog.Builder(myApp.currentActivity);
                final View ToNotedv = myApp.currentActivity.getLayoutInflater().inflate(R.layout.custom_tonote_dialog, null);
                ToNoteBuilder.setView(ToNotedv);
                ToNoteBuilder.setCancelable(false);
                TextView txtToNoteTitle = ToNotedv.findViewById(R.id.txtToNoteTitle);
                txtToNoteTitle.setText(!IsEmptyString(title) ? title : c.getString(R.string.tonote));
                TextView btnCustomToNoteOK = (TextView) ToNotedv.findViewById(R.id.btnCustomToNoteOK);
                LinearLayout LLcustomToNote_msg = (LinearLayout) ToNotedv.findViewById(R.id.LLcustomToNote_msg);

                for (int i = 0; i <= msgs.size() - 1; i++) {
                    TextView tv = new TextView(c);
                    tv.setText(msgs.get(i));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    //tv.setTextColor(ContextCompat.getColor(c, R.color.tickgreen));
                    tv.setAllCaps(true);
                    //tv.setBackgroundColor((ts % 2) == 0? Color.WHITE : Color.BLACK);
                    tv.setTextColor(ContextCompat.getColor(c, R.color.black));
                    //tv.setTypeface(null, Typeface.BOLD);
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    //tv.setPadding(50,40,75,40);
                    tv.setPadding(75, 40, 75, 40);
                    tv.setLayoutParams(params);
                    //tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_bullet, 0, 0, 0);
                    tv.setBackground(c.getDrawable(R.drawable.bottom_border));
                    LLcustomToNote_msg.addView(tv);
                    added++;
                }

                final AlertDialog ToNotedvd = ToNoteBuilder.create();

                btnCustomToNoteOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ToNotedvd.cancel();
                        } catch (Exception e) {

                        }
                    }
                });

                if (added > 0) {
                    ToNotedvd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    ToNotedvd.show();
                }
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods showToNoteMsgPrompt. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static void showToNoteMsgPromptScrollable(String title, ArrayList<String> msgs) {
        Context c = myApp.getAppContext();
        int added = 0;
        try {
            if (msgs != null && msgs.size() > 0) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                final AlertDialog.Builder ToNoteBuilder = new AlertDialog.Builder(myApp.currentActivity);
                final View ToNotedv = myApp.currentActivity.getLayoutInflater().inflate(R.layout.custom_scannedlist_scrollable, null);
                ToNoteBuilder.setView(ToNotedv);
                ToNoteBuilder.setCancelable(false);
                TextView txtToNoteTitle = ToNotedv.findViewById(R.id.txtToNoteTitle);
                txtToNoteTitle.setText(!IsEmptyString(title) ? title : c.getString(R.string.tonote));
                TextView btnCustomToNoteOK = (TextView) ToNotedv.findViewById(R.id.btnCustomToNoteOK);
                LinearLayout LLcustomToNote_msg = (LinearLayout) ToNotedv.findViewById(R.id.LLcustomToNote_msg);

                for (int i = 0; i <= msgs.size() - 1; i++) {
                    TextView tv = new TextView(c);
                    tv.setText(msgs.get(i));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    //tv.setTextColor(ContextCompat.getColor(c, R.color.tickgreen));
                    tv.setAllCaps(true);
                    //tv.setBackgroundColor((ts % 2) == 0? Color.WHITE : Color.BLACK);
                    tv.setTextColor(ContextCompat.getColor(c, R.color.black));
                    //tv.setTypeface(null, Typeface.BOLD);
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    //tv.setPadding(50,40,75,40);
                    tv.setPadding(75, 40, 75, 40);
                    tv.setLayoutParams(params);
                    //tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_bullet, 0, 0, 0);
                    tv.setBackground(c.getDrawable(R.drawable.bottom_border));
                    LLcustomToNote_msg.addView(tv);
                    added++;
                }

                final AlertDialog ToNotedvd = ToNoteBuilder.create();

                btnCustomToNoteOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ToNotedvd.cancel();
                        } catch (Exception e) {

                        }
                    }
                });

                if (added > 0) {
                    ToNotedvd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    ToNotedvd.show();
                }
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods showToNoteMsgPrompt. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static void showCustomCanCannotMsgPrompt(String title, String directMsg, ArrayList<String> tickSpecInst, ArrayList<String> crossSpecInst) {
        String msg = "";
        Context c = myApp.getAppContext();
        int added = 0;
        boolean isDirectMsg = false;
        try {
            if ((!IsEmptyString(directMsg)) || ((tickSpecInst != null && tickSpecInst.size() > 0) || (crossSpecInst != null && crossSpecInst.size() > 0))) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);

                final AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                final View dv = myApp.currentActivity.getLayoutInflater().inflate(R.layout.custom_can_cannot_dialog, null);
                builder.setView(dv);
                builder.setCancelable(false);
                TextView txtTitle = dv.findViewById(R.id.txtTitle);
                txtTitle.setText(!IsEmptyString(title) ? title : c.getString(R.string.tonote));
                TextView btnCustomCanCannotOK = (TextView) dv.findViewById(R.id.btnCustomCanCannotOK);
                LinearLayout LLcustomCanCannot_msg = (LinearLayout) dv.findViewById(R.id.LLcustomCanCannot_msg);
                LinearLayout LLcustomCanCannotMain = (LinearLayout) dv.findViewById(R.id.LLcustomCanCannotMain);

                if (!IsEmptyString(directMsg)) {
                    TextView tv = new TextView(c);
                    tv.setText(directMsg);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    tv.setTextColor(ContextCompat.getColor(c, R.color.black));
                    tv.setAllCaps(true);
                    //tv.setBackgroundColor((ts % 2) == 0? Color.WHITE : Color.BLACK);
                    tv.setTypeface(null, Typeface.NORMAL);
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setLayoutParams(params);
                    //tv.setPadding(20,15,20,0);
                    tv.setPadding(75, 10, 75, 0);
                    LLcustomCanCannot_msg.addView(tv);
                    added++;
                    /*LLcustomCanCannotMain.setBackgroundColor(ContextCompat.getColor(c, R.color.White));
                    btnCustomCanCannotOK.setTextColor(ContextCompat.getColor(c, R.color.colorPrimary));*/
                } else if (((tickSpecInst != null && tickSpecInst.size() > 0) || (crossSpecInst != null && crossSpecInst.size() > 0))) {
                    if (tickSpecInst != null && tickSpecInst.size() > 0) {
                        for (int ts = 0; ts <= tickSpecInst.size() - 1; ts++) {
                            TextView tv = new TextView(c);
                            tv.setText(tickSpecInst.get(ts));
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            //tv.setTextColor(ContextCompat.getColor(c, R.color.tickgreen));
                            tv.setAllCaps(true);
                            //tv.setBackgroundColor((ts % 2) == 0? Color.WHITE : Color.BLACK);
                            tv.setTextColor(ContextCompat.getColor(c, R.color.black));
                            tv.setTypeface(null, Typeface.BOLD);
                            tv.setGravity(Gravity.CENTER_VERTICAL);
                            tv.setPadding(50, 0, 75, 0);
                            tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_ok_black, 0, 0, 0);
                            tv.setLayoutParams(params);
                            tv.setBackgroundColor(ContextCompat.getColor(c, R.color.brightergreen));
                            LLcustomCanCannot_msg.addView(tv);
                            added++;
                        }
                    }
                    if (crossSpecInst != null && crossSpecInst.size() > 0) {
                        for (int cs = 0; cs <= crossSpecInst.size() - 1; cs++) {
                            TextView tv = new TextView(c);
                            tv.setText(crossSpecInst.get(cs));
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            //tv.setTextColor(Color.RED);
                            tv.setTextColor(ContextCompat.getColor(c, R.color.White));
                            tv.setAllCaps(true);
                            tv.setTypeface(null, Typeface.BOLD);
                            tv.setGravity(Gravity.CENTER_VERTICAL);
                            tv.setPadding(50, 0, 75, 0);
                            tv.setLayoutParams(params);
                            tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_failpu_white, 0, 0, 0);
                            tv.setBackgroundColor(ContextCompat.getColor(c, R.color.menu_red));
                            LLcustomCanCannot_msg.addView(tv);
                            added++;
                        }
                    }
                }

                final AlertDialog dvd = builder.create();

                btnCustomCanCannotOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            dvd.cancel();
                        } catch (Exception e) {

                        }
                    }
                });

                if (added > 0) {
                    dvd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dvd.show();
                }
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods showCustomCanCannotMsgPrompt. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static String getCurrInfoFlagMsg2(Integer currJobInfoFLAG, Integer showRestrictionPromptInt, Boolean isForFailDelivery, String intendedName) {
        String msg = "";
        Boolean intendedRecOnly = false;
        Boolean canSlotLB = false;
        Boolean canSlotUnderDoor = false;
        Boolean canGuard = false;
        Context c = myApp.getAppContext();
        try {
            intendedRecOnly = isIntendedRecOnly(currJobInfoFLAG);
            canSlotLB = isCanSlotLB(currJobInfoFLAG);
            canSlotUnderDoor = isCanSlotUnderDoor(currJobInfoFLAG);
            canGuard = isCanGuard(currJobInfoFLAG);

            if (!isForFailDelivery) {
                if (intendedRecOnly) {
                    msg += !IsEmptyString(intendedName) ? c.getString(R.string.canonlypassto) + "\n\n" + intendedName.toUpperCase() + "\n" : c.getString(R.string.lblSI_1_4) + "\n";
                    msg += "\n" + c.getString(R.string.lblSI_1_1_1).toUpperCase();
                    msg += "\n" + c.getString(R.string.lblSI_1_2_2).toUpperCase();
                    msg += "\n" + c.getString(R.string.lblSI_1_3_3).toUpperCase();
                    msg += "\n" + c.getString(R.string.lblSI_1_4_4).toUpperCase();
                    msg += "\n" + c.getString(R.string.lblSI_1_5_5).toUpperCase();
                } else if (!canSlotLB && !canSlotUnderDoor && !canGuard) {
                    msg += c.getString(R.string.lblSI_1_1_1).toUpperCase();
                    msg += "\n" + c.getString(R.string.lblSI_1_2_2).toUpperCase();
                    msg += "\n" + c.getString(R.string.lblSI_1_3_3).toUpperCase();
                    msg += "\n" + c.getString(R.string.lblSI_1_4_4).toUpperCase();
                    msg += "\n" + c.getString(R.string.lblSI_1_5_5).toUpperCase();
                } else if ((canSlotLB || canSlotUnderDoor || canGuard)) {
                    if (canSlotLB) {
                        if (msg.equals("")) {
                            msg += c.getString(R.string.alertIfNotIn).toUpperCase() + "\n";
                        }
                        msg += "\n" + c.getString(R.string.lblSI_1_1).toUpperCase();
                    }
                    if (canSlotUnderDoor) { //door
                        if (msg.equals("")) {
                            msg += c.getString(R.string.alertIfNotIn).toUpperCase() + "\n";
                        }
                        msg += "\n" + c.getString(R.string.lblSI_1_2).toUpperCase();
                        msg += "\n" + c.getString(R.string.lblSI_1_4_4).toUpperCase();
                        msg += "\n" + c.getString(R.string.lblSI_1_5_5).toUpperCase();
                    }
                    if (canGuard) { //guard
                        if (msg.equals("")) {
                            msg += c.getString(R.string.alertIfNotIn).toUpperCase() + "\n";
                        }
                        msg += "\n" + c.getString(R.string.lblSI_1_3).toUpperCase();
                    }
                }
            } else {
                //when FD
                if (canSlotLB) { //lb
                    if (msg.equals("")) {
                        msg += c.getString(R.string.alertALternatives).toUpperCase() + "\n";
                    }
                    msg += "\n" + c.getString(R.string.lblSI_1_1_1_1).toUpperCase();
                }
                if (canSlotUnderDoor) { //door
                    if (msg.equals("")) {
                        msg += c.getString(R.string.alertALternatives).toUpperCase() + "\n";
                    }
                    msg += "\n" + c.getString(R.string.lblSI_1_2_2_2).toUpperCase();
                    /*msg += "\n" + myApp.getAppContext().getString(R.string.lblSI_1_4_4_4).toUpperCase();
                    msg += "\n" + myApp.getAppContext().getString(R.string.lblSI_1_5_5_5).toUpperCase();*/
                }
                if (canGuard) { //guard
                    if (msg.equals("")) {
                        msg += c.getString(R.string.alertALternatives).toUpperCase() + "\n";
                    }
                    msg += "\n" + c.getString(R.string.lblSI_1_3_3_3).toUpperCase();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods getCurrInfoFlagMsg2. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            msg = "";
        } finally {
            return msg;
        }
    }

    public static String getOKPrequirementMsgNew(boolean reqSign, boolean reqName, boolean reqSeal, boolean reqStaffID, String aPreDeterminedStatusCodeIDX) {
        String s = "";
        try {
            if (reqSign) {
                s += objGlobalMethods.IsEmptyString(aPreDeterminedStatusCodeIDX) ?
                        myApp.getAppContext().getString(R.string.errrequiresignature_only) :
                        myApp.getAppContext().getString(R.string.errrequire_reject_signature_only);
            }

            if (reqName && !IsEmptyString(s)) {
                s += "\n\n" +
                        (objGlobalMethods.IsEmptyString(aPreDeterminedStatusCodeIDX) ?
                                myApp.getAppContext().getString(R.string.errrequirename_only) :
                                myApp.getAppContext().getString(R.string.errrequire_reject_name_only));
            } else if (reqName && IsEmptyString(s)) {
                s += objGlobalMethods.IsEmptyString(aPreDeterminedStatusCodeIDX) ?
                        myApp.getAppContext().getString(R.string.errrequirename_only) :
                        myApp.getAppContext().getString(R.string.errrequire_reject_name_only);
            }

            if (reqSeal && !s.equals("")) {
                s += "\n\n" + myApp.getAppContext().getString(R.string.errrequireseal);
            } else if (reqSeal && s.equals("")) {
                s += myApp.getAppContext().getString(R.string.errrequireseal);
            }

            if (reqStaffID && !s.equals("")) {
                s += "\n\n" + myApp.getAppContext().getString(R.string.errrequirestaffid);
            } else if (reqStaffID && s.equals("")) {
                s += myApp.getAppContext().getString(R.string.errrequirestaffid);
            }
        } catch (Exception e) {
            s = "";
            writeToFile("Exception at objGlobalMethods getOKPrequirementMsgNew. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return s;
        }
    }

    public static String getOKPrequirementMsg(boolean reqSign, boolean reqSeal, boolean reqStaffID) {
        String s = "";
        try {
            if (reqSign) {
                s += myApp.getAppContext().getString(R.string.errrequiresignature);
            }

            if (reqSeal && !s.equals("")) {
                s += "\n\n" + myApp.getAppContext().getString(R.string.errrequireseal);
            } else if (reqSeal && s.equals("")) {
                s += myApp.getAppContext().getString(R.string.errrequireseal);
            }

            if (reqStaffID && !s.equals("")) {
                s += "\n\n" + myApp.getAppContext().getString(R.string.errrequirestaffid);
            } else if (reqStaffID && s.equals("")) {
                s += myApp.getAppContext().getString(R.string.errrequirestaffid);
            }
        } catch (Exception e) {
            s = "";
            writeToFile("Exception at objGlobalMethods getOKPrequirementMsg. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return s;
        }
    }

    public static Boolean getRestriction(Integer flag) {
        Boolean restrictionAvail = false;
        try {
            if ((flag & 1) == 1) {
                restrictionAvail = true;
            }
            if ((flag & 2) == 2) {
                restrictionAvail = true;
            }
            if ((flag & 3) == 3) {
                restrictionAvail = true;
            }
            if ((flag & 16) == 16) {
                restrictionAvail = true;
            }
        } catch (Exception e) {
            restrictionAvail = false;
        } finally {
            return restrictionAvail;
        }
    }

    public static Boolean IsOutOfLocationRange(double[] DestinationLatLng, Location gpsLocation) {
        Boolean asd = false;
        float distMeter = 0;

        try {
            if (gpsLocation.getLatitude() > 0 && gpsLocation.getLatitude() > 0) {
                if (DestinationLatLng != null && DestinationLatLng[0] > 0 && DestinationLatLng[1] > 0) {
                    Location DestinationLocation = new Location("Destination");
                    DestinationLocation.setLatitude(DestinationLatLng[0]);
                    DestinationLocation.setLongitude(DestinationLatLng[1]);
                    distMeter = gpsLocation.distanceTo(DestinationLocation);
                    if (distMeter > 300) {
                        asd = true;
                    }
                }
            } else {
                asd = true;
            }
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean IsOutOfDestinationLocationCustomRange(DestinationLocationCustom DestinationLocation, Location gpsLocation) {
        Boolean asd = false;
        float distMeter = 0;
        float tolerancefloat = 0;
        try {
            if (DestinationLocation.DestinationTolerance > 0) {
                tolerancefloat = DestinationLocation.DestinationTolerance / 100;
            }
            if (gpsLocation.getLatitude() == 0 && gpsLocation.getLongitude() == 0) {
                asd = false;
            } else if (gpsLocation.getLatitude() > 0 && gpsLocation.getLongitude() > 0 &&
                    DestinationLocation != null && DestinationLocation.lat > 0 && DestinationLocation.lng > 0) {
                Location dest = new Location("dest");
                dest.setLatitude(DestinationLocation.lat);
                dest.setLongitude(DestinationLocation.lng);
                distMeter = gpsLocation.distanceTo(dest);
                //asd = distMeter > 300 ? true : false;
                //asd = distMeter > 500 ? true : false;
                asd = distMeter > (tolerancefloat * 2) ? true : false;
            }
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean IsOutOfDestinationLocationRange(Location DestinationLocation, Location gpsLocation, int tolerance) {
        Boolean asd = false;
        float distMeter = 0;
        float tolerancefloat = 0;
        try {
            /*if (gpsLocation.getLatitude() > 0 && gpsLocation.getLongitude() > 0 && DestinationLocation != null && DestinationLocation.getLatitude() > 0 && DestinationLocation.getLongitude() > 0) {
                distMeter = gpsLocation.distanceTo(DestinationLocation);
                asd = distMeter > 1000 ? true : false;
            }*/
            if (tolerance > 0) {
                tolerancefloat = tolerance / 100;
            }
            if (gpsLocation.getLatitude() == 0 && gpsLocation.getLongitude() == 0) {
                asd = false;
            } else if (gpsLocation.getLatitude() > 0 && gpsLocation.getLongitude() > 0 &&
                    DestinationLocation != null && DestinationLocation.getLatitude() > 0 && DestinationLocation.getLongitude() > 0) {
                distMeter = gpsLocation.distanceTo(DestinationLocation);
                //asd = distMeter > 300 ? true : false;
                //asd = distMeter > 500 ? true : false;
                asd = distMeter > (tolerancefloat * 2) ? true : false;
            }
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static DestinationLocationCustom getDestinationLocationCustom(objJobInfo ji, int PIPorDIP) {
        DestinationLocationCustom lc = new DestinationLocationCustom();
        try {
            if (ji != null) {
                if (PIPorDIP == 0) {
                    //for PU
                    lc.setLat(ji.PURedirectedLocation != null ? ji.PURedirectedLocation.LAT : ji.PULocation != null ? ji.PULocation.LAT : 0);
                    lc.setLng(ji.PURedirectedLocation != null ? ji.PURedirectedLocation.LNG : ji.PULocation != null ? ji.PULocation.LNG : 0);
                } else if (PIPorDIP == 2) {
                    lc.setLat(ji.DLRedirectedLocation != null ? ji.DLRedirectedLocation.LAT : ji.DLLocation != null ? ji.DLLocation.LAT : 0);
                    lc.setLng(ji.DLRedirectedLocation != null ? ji.DLRedirectedLocation.LNG : ji.DLLocation != null ? ji.DLLocation.LNG : 0);
                }
            }
        } catch (Exception e) {
            lc.setLat(0);
            lc.setLng(0);
            writeToFile("Exception at objGlobalMethods getDestinationLocationCustom. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return lc;
        }
    }

    public static Location getDestinationLocation(objJobInfo ji, int PIPorDIP) {
        Location lc = new Location("Destination");
        try {
            if (ji != null) {
                if (PIPorDIP == 0) {
                    //for PU
                    lc.setLatitude(ji.PURedirectedLocation != null ? ji.PURedirectedLocation.LAT : ji.PULocation != null ? ji.PULocation.LAT : 0);
                    lc.setLongitude(ji.PURedirectedLocation != null ? ji.PURedirectedLocation.LNG : ji.PULocation != null ? ji.PULocation.LNG : 0);
                } else if (PIPorDIP == 2) {
                    lc.setLatitude(ji.DLRedirectedLocation != null ? ji.DLRedirectedLocation.LAT : ji.DLLocation != null ? ji.DLLocation.LAT : 0);
                    lc.setLongitude(ji.DLRedirectedLocation != null ? ji.DLRedirectedLocation.LNG : ji.DLLocation != null ? ji.DLLocation.LNG : 0);
                }
            }
        } catch (Exception e) {
            lc.setLatitude(0);
            lc.setLongitude(0);
            writeToFile("Exception at objGlobalMethods getDestinationLocation. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return lc;
        }
    }

    public static int getDestinationTolerance(objJobInfo ji, int catType) {
        int tolerance = 0;
        try {
            if (ji != null) {
                if (catType == 0) {
                    //for PU
                    tolerance = ji.PURedirectedLocation != null ? ji.PURedirectedLocation.DISTANCE_TOLERANCE : ji.PULocation != null ? ji.PULocation.DISTANCE_TOLERANCE : 0;
                } else if (catType == 2) {
                    tolerance = ji.DLRedirectedLocation != null ? ji.DLRedirectedLocation.DISTANCE_TOLERANCE : ji.DLLocation != null ? ji.DLLocation.DISTANCE_TOLERANCE : 0;
                }
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods getDestinationTolerance. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return tolerance;
        }
    }

    public static Location getLastKnownLocationForJobUpdate() {
        Location returnLocation = null;
        try {
            if (LocationService.mLastLocationReceived != null) {
                Date dt = new Date();
                long diffInMillisec = dt.getTime() - LocationService.mLastLocationReceived.getTime();
                if (diffInMillisec <= 300000) {
                    returnLocation = LocationService.mLastLocationReceived;
                } else {
                    //return 0,0
                    returnLocation = new Location("zerozero");
                    returnLocation.setLatitude(0.0);
                    returnLocation.setLongitude(0.0);
                }
            } else {
                returnLocation = new Location("zerozero");
                returnLocation.setLatitude(0.0);
                returnLocation.setLongitude(0.0);
            }
        } catch (Exception e) {
            returnLocation = new Location("zerozero");
            returnLocation.setLatitude(0.0);
            returnLocation.setLongitude(0.0);
        } finally {
            return returnLocation;
        }
    }

    public static void sendLukeDVmsgEmail(objJobInfo ji, int cattypeid) {
        String ss = "";
        String[] UsernamePwd = new String[4];
        SimpleDateFormat formatter2 = new SimpleDateFormat("ddMMyyyy HH:mm:ss");
        String dtstr = formatter2.format(new Date());
        String jobNo = "";
        try {
            if (ji != null) {
                jobNo = ji.JobNo;
            }
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(getGcmPreferences());
            String username = UsernamePwd[0];
            String useridx = UsernamePwd[2];
            Location DestinationLocationFD = objGlobalMethods.getDestinationLocation(ji, cattypeid);
            Location gpsLocationFD = objGlobalMethods.getLastKnownLocationForJobUpdate();
            int tolerance = objGlobalMethods.getDestinationTolerance(ji, cattypeid);
            float distMeter = gpsLocationFD.distanceTo(DestinationLocationFD);
            String gcLocation = "\n\nGC location: " + String.valueOf(gpsLocationFD.getLatitude()) + "," + String.valueOf(gpsLocationFD.getLongitude());
            String destLocation = "\nDestination: " + String.valueOf(DestinationLocationFD.getLatitude()) + "," + String.valueOf(DestinationLocationFD.getLongitude());
            String GcName = username;
            objAuthDriverResult res = objGlobalMethods.getGCdetails(getGcmPreferences());
            GcName = res.getFullName();
            ss = GcName + "\n" + dtstr + "\nJob No: " + jobNo + gcLocation + destLocation + "\n\nDestination Tolerance: " + tolerance / 100 + "m x 1.5 \nActual distance apart: " + distMeter + "m";
            String subj = "I Have Arrive DV from " + GcName;
            prepareSendEmail(new String[]{"it@xdel.com"}, subj,
                    ss, "", false, subj);
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods sendLukeDVmsgEmail. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static double[] getDestinationLatLng(objJobInfo ji, int PIPorDIP) {
        double[] latlng = null;
        try {
            if (ji != null) {
                if (PIPorDIP == 0) {
                    //for PU
                    if (ji.PURedirectedLocation != null) {
                        latlng = new double[]{ji.PURedirectedLocation.LAT, ji.PURedirectedLocation.LNG};
                    } else if (ji.PULocation != null) {
                        latlng = new double[]{ji.PULocation.LAT, ji.PULocation.LNG};
                    } else {
                        latlng = null;
                    }
                } else if (PIPorDIP == 2) {
                    if (ji.DLRedirectedLocation != null) {
                        latlng = new double[]{ji.DLRedirectedLocation.LAT, ji.DLRedirectedLocation.LNG};
                    } else if (ji.DLLocation != null) {
                        latlng = new double[]{ji.DLLocation.LAT, ji.DLLocation.LNG};
                    } else {
                        latlng = null;
                    }
                }
            }
        } catch (Exception ex) {
            latlng = null;
        } finally {
            return latlng;
        }
    }

    public static void promptNotiForBattCharging(SharedPreferences gcmp) {
        try {
            Boolean asd = true;
            objGlobalMethods.setGCMPrefs(gcmp, "showBattChargeNotiPrefStr", asd.toString());
            if (myApp.currentActivity.getClass() == DrawerMainActivity.class) {
                final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.showChargeBattDialog(myApp.getAppContext().getString(R.string.alert_battcharge));
                    }
                });
            } else if (myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.showChargeBattDialog(myApp.getAppContext().getString(R.string.alert_battcharge));
                    }
                });
            } else if (myApp.currentActivity.getClass() == DashboardSubActivity.class) {
                final DashboardSubActivity act = (DashboardSubActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.showChargeBattDialog(myApp.getAppContext().getString(R.string.alert_battcharge));
                    }
                });
            } else if (myApp.currentActivity.getClass() == AddressListNewActivity.class) {
                final AddressListNewActivity act = (AddressListNewActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.showChargeBattDialog(myApp.getAppContext().getString(R.string.alert_battcharge));
                    }
                });
            } else if (myApp.currentActivity.getClass() == AddressListRoutingActivity.class) {
                final AddressListRoutingActivity act = (AddressListRoutingActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.showChargeBattDialog(myApp.getAppContext().getString(R.string.alert_battcharge));
                    }
                });
            } else if (myApp.currentActivity.getClass() == MainCODActivity.class) {
                final MainCODActivity act = (MainCODActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.showChargeBattDialog(myApp.getAppContext().getString(R.string.alert_battcharge));
                    }
                });
            } else if (myApp.currentActivity.getClass() == MapsActivity.class) {
                final MapsActivity act = (MapsActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.showChargeBattDialog(myApp.getAppContext().getString(R.string.alert_battcharge));
                    }
                });
            } else if (myApp.currentActivity.getClass() == MapsAllActivity.class) {
                final MapsAllActivity act = (MapsAllActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.showChargeBattDialog(myApp.getAppContext().getString(R.string.alert_battcharge));
                    }
                });
            } else if (myApp.currentActivity.getClass() == Messaging.class) {
                final Messaging act = (Messaging) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.showChargeBattDialog(myApp.getAppContext().getString(R.string.alert_battcharge));
                    }
                });
            } else if (myApp.currentActivity.getClass() == Messaginglist.class) {
                final Messaginglist act = (Messaginglist) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.showChargeBattDialog(myApp.getAppContext().getString(R.string.alert_battcharge));
                    }
                });
            }

        } catch (Exception e) {
            //do nothing
        }
    }

    public static Boolean checkshowBattChargeNoti() {
        Boolean showBattChargeNoti = false;
        try {
            SharedPreferences gcmPrefs = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            String showBattChargeNotiPrefStr = "";
            showBattChargeNotiPrefStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "showBattChargeNotiPrefStr");
            if (!showBattChargeNotiPrefStr.isEmpty()) {
                try {
                    showBattChargeNoti = Boolean.parseBoolean(showBattChargeNotiPrefStr);
                } catch (Exception e) {
                    showBattChargeNoti = false;
                }
            }
        } catch (Exception e) {

        } finally {
            return showBattChargeNoti;
        }
    }

    public static void checkToPromptNotiForBattCharging() {
        try {
            SharedPreferences gcmPrefs = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            Boolean showBattChargeNoti = checkshowBattChargeNoti();

            if (showBattChargeNoti) {
                promptNotiForBattCharging(gcmPrefs);
            } else {
                setGCMPrefs(gcmPrefs, "showBattChargeNotiPrefStr", showBattChargeNoti.toString());
            }
        } catch (Exception e) {

        }
    }

    public static void sendBattLvl(Context acontext, SharedPreferences GCMprefs) {
        try {
            Integer battlvlInt = objGlobalMethods.getBattLvl(acontext);
            String RegID = GCMprefs.getString(objGlobalVariables.PROPERTY_REG_ID, "");
            Boolean updateBattSuccess = WebService.UpdateBattLvl(RegID, battlvlInt);
            if (updateBattSuccess != null) {
                if (!updateBattSuccess) {
                    //if result return false, stop current activity and postpone, do not add back to que
                    Date dt = new Date();
                    writeToFile("Update battlvl returns false. Batt lvl: " + String.valueOf(battlvlInt), "", "", "", "");
                } else {
                    ////if result return true, do nothing
                }
            } else {
                //if error, stop current activity and postpone
            }
        } catch (Exception e) {
            Date dt = new Date();
            writeToFile("Exception caught at LocationService sendBattLvl. Exception: , " + e.getMessage().toString(),
                    "", "", "", "");
        }
    }

    public static class objSignatureJobInfo implements Comparable<objSignatureJobInfo> {
        public Long JobsIDX;
        public Long ContactIDX;
        public objJobInfo ji;

        public objSignatureJobInfo(Long aJobsIDX, Long aContactIDX, objJobInfo aji) {
            this.JobsIDX = aJobsIDX;
            this.ContactIDX = aContactIDX;
            this.ji = aji;
        }

        public int compareTo(objSignatureJobInfo other) {
            return ContactIDX.compareTo(other.ContactIDX);
        }
    }

    public static ArrayList massOKPchecks(objCheckedItems[] objCheckedItemsAL) {
        ArrayList al = new ArrayList();
        Boolean[] OtpSignature = new Boolean[3];
        objJobInfo ji = null;
        Boolean hasOTP = false;
        Boolean cannotMassOKPforReqSignature = false;
        Boolean masterHasReqSignature = false;
        Boolean hasReqSignature = false;
        ArrayList<Long> ReqSignatureJobsIDXs = new ArrayList<Long>();
        long selectedContactIDXforSignature = 0;
        long FirstContactIDXforSignature = 0;
        long nextContactIDXforSignature = 0;
        Boolean alljobsToSameContactIDX = true;
        ArrayList<Long> ReqSignatureContactIDX = new ArrayList<>();
        long prevJobsIDX = 0;
        long prevContactIDX = 0;
        objJobInfo prevJi = null;
        List<objSignatureJobInfo> objSignatureJobInfoList = new ArrayList<objSignatureJobInfo>();
        objSignatureJobInfo SignatureJobInfo = null;
        List<ArrayList> PrevJobsIDXnJI = new ArrayList<>();
        ArrayList objPrevJobsIDXnJI;
        try {
            for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                ji = objCheckedItemsAL[i].jobinfo;

                if (((ji.FLAG & 512) == 512) && ji.PIN != null && !ji.PIN.isEmpty()) {
                    hasOTP = true;
                    break;
                }

                if (ji.DLRedirectedLocation != null && ji.DLRedirectedLocation.Contacts != null && ji.DLRedirectedLocation.Contacts.length > 0) {
                    selectedContactIDXforSignature = ji.DLRedirectedLocation.Contacts[0].IDX;
                } else if (ji.DLLocation != null && ji.DLLocation.Contacts != null && ji.DLLocation.Contacts.length > 0) {
                    selectedContactIDXforSignature = ji.DLLocation.Contacts[0].IDX;
                }

                SignatureJobInfo = new objSignatureJobInfo(ji.JobsIDX, selectedContactIDXforSignature, ji);
                objSignatureJobInfoList.add(SignatureJobInfo);

                /*if(((ji.FLAG & 1024) == 1024)) {
                    masterHasReqSignature = true;
                    if (ReqSignatureContactIDX.size() == 0) {

                        ReqSignatureJobsIDXs.add(ji.JobsIDX);
                        ReqSignatureContactIDX.add(selectedContactIDXforSignature);
                    } else if (ReqSignatureContactIDX.contains(selectedContactIDXforSignature)){
                        ReqSignatureJobsIDXs.add(ji.JobsIDX);
                    }  else if (!ReqSignatureContactIDX.contains(selectedContactIDXforSignature)) {
                        alljobsToSameContactIDX = false;
                    }
                } else {
                    if (ReqSignatureContactIDX.size() == 0) {
                        prevJi = ji;
                        prevJobsIDX = ji.JobsIDX;
                    } else if (ReqSignatureContactIDX.size() > 0) {
                        if (ReqSignatureContactIDX.contains(selectedContactIDXforSignature)) {
                            ReqSignatureJobsIDXs.add(ji.JobsIDX);
                        }
                    }
                }*/

            }

            if (hasOTP) {
                cannotMassOKPforReqSignature = false;
                masterHasReqSignature = false;
                OtpSignature[0] = hasOTP;
                OtpSignature[1] = cannotMassOKPforReqSignature;
                OtpSignature[2] = masterHasReqSignature;
                al.add(OtpSignature);
                al.add(null);
            } else {
                Collections.sort(objSignatureJobInfoList);

                for (int i = 0; i <= objSignatureJobInfoList.size() - 1; i++) {
                    SignatureJobInfo = objSignatureJobInfoList.get(i);
                    if ((SignatureJobInfo.ji.FLAG & 1024) == 1024) {
                        //signature required
                        masterHasReqSignature = true;
                        if (ReqSignatureContactIDX.size() == 0) {
                            ReqSignatureContactIDX.add(SignatureJobInfo.ContactIDX);
                            ReqSignatureJobsIDXs.add(SignatureJobInfo.JobsIDX);
                        } else if (ReqSignatureContactIDX.size() > 0) {
                            if (ReqSignatureContactIDX.contains(SignatureJobInfo.ContactIDX)) {
                                ReqSignatureJobsIDXs.add(SignatureJobInfo.JobsIDX);
                            } else {

                            }
                        }

                    } else {
                        //signature not required
                        if (ReqSignatureContactIDX.size() == 0) {
                            objPrevJobsIDXnJI = new ArrayList();
                            objPrevJobsIDXnJI.add(SignatureJobInfo.JobsIDX);
                            objPrevJobsIDXnJI.add(SignatureJobInfo.ContactIDX);
                            objPrevJobsIDXnJI.add(SignatureJobInfo.ji);
                            PrevJobsIDXnJI.add(objPrevJobsIDXnJI);
                        } else if (ReqSignatureContactIDX.size() > 0) {
                            if (ReqSignatureContactIDX.contains(SignatureJobInfo.ContactIDX)) {

                            }
                        }
                    }
                }


                if (ReqSignatureJobsIDXs.size() == 0) {
                    cannotMassOKPforReqSignature = false;
                    masterHasReqSignature = false;
                    OtpSignature[0] = hasOTP; //false
                    OtpSignature[1] = cannotMassOKPforReqSignature;
                    OtpSignature[2] = masterHasReqSignature;
                    al.add(OtpSignature);
                    al.add(null);
                } else if (ReqSignatureJobsIDXs.size() > 0) {

                }
            }

            if (alljobsToSameContactIDX) {
                cannotMassOKPforReqSignature = false;
            }


            OtpSignature[0] = hasOTP;
            OtpSignature[1] = cannotMassOKPforReqSignature;
            OtpSignature[2] = masterHasReqSignature;
            al.add(OtpSignature);
            al.add(ReqSignatureJobsIDXs);
        } catch (Exception e) {
            OtpSignature = new Boolean[3];
            OtpSignature[0] = false;
            OtpSignature[1] = false;
            OtpSignature[2] = false;
            al.add(OtpSignature);
            al.add(null);
            Date dt = new Date();
            writeToFile("Exception caught at objGlobalMethods hasOTPorReqSignatureJobs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return al;
        }
    }

    public static Boolean hasSecurityBag(objCheckedItems[] objCheckedItemsAL) {
        Boolean asd = false;
        objJobInfo ji = null;
        try {
            if (objCheckedItemsAL != null && objCheckedItemsAL.length > 0) {
                for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                    ji = objCheckedItemsAL[i].jobinfo;
                    if (JobsHandler.reqSecurityTag(ji)) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods hasSecurityBag. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean hasIDVerification(objCheckedItems[] objCheckedItemsAL) {
        Boolean asd = false;
        objJobInfo ji = null;
        try {
            if (objCheckedItemsAL != null && objCheckedItemsAL.length > 0) {
                for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                    ji = objCheckedItemsAL[i].jobinfo;
                    if (JobsHandler.hasIDVerification(ji)) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods hasIDVerification. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean[] hasOTPorReqSignatureJobs(objCheckedItems[] objCheckedItemsAL) {
        Boolean[] OtpSignature = new Boolean[4];
        objJobInfo ji = null;
        Boolean hasOTP = false;
        Boolean cannotMassOKPforReqSignature = false;
        Boolean hasReqSignature = false;
        Boolean CanGetReviewLink = false;
        long selectedContactIDXforSignature = 0;
        long FirstContactIDXforSignature = 0;
        long nextContactIDXforSignature = 0;
        List<Long> ContactIDXAL = new ArrayList<>();
        List<Long> PrevContactIDXAL = new ArrayList<>();

        try {
            for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                ji = objCheckedItemsAL[i].jobinfo;

                if (((ji.FLAG & 512) == 512) && ji.PIN != null && !ji.PIN.isEmpty()) {
                    hasOTP = true;
                    cannotMassOKPforReqSignature = false;
                    hasReqSignature = false;
                    OtpSignature[0] = hasOTP;
                    OtpSignature[1] = cannotMassOKPforReqSignature;
                    OtpSignature[2] = hasReqSignature;
                    OtpSignature[3] = (ji.FLAG & 16777216) == 16777216 ? true : false;
                    return OtpSignature;
                }

                if (ji.DLRedirectedLocation != null && ji.DLRedirectedLocation.Contacts != null && ji.DLRedirectedLocation.Contacts.length > 0) {
                    selectedContactIDXforSignature = ji.DLRedirectedLocation.Contacts[0].IDX;
                } else if (ji.DLLocation != null && ji.DLLocation.Contacts != null && ji.DLLocation.Contacts.length > 0) {
                    selectedContactIDXforSignature = ji.DLLocation.Contacts[0].IDX;
                }

                if ((ji.FLAG & 1024) == 1024) {
                    if (ContactIDXAL.size() == 0 && PrevContactIDXAL.size() == 0) {
                        ContactIDXAL.add(selectedContactIDXforSignature);
                    } else if (ContactIDXAL.size() == 0 && PrevContactIDXAL.size() > 0) {
                        for (int x = PrevContactIDXAL.size() - 1; x >= 0; x--) {
                            if (!PrevContactIDXAL.get(x).equals(selectedContactIDXforSignature)) {
                                hasOTP = false;
                                cannotMassOKPforReqSignature = true;
                                hasReqSignature = true;
                                OtpSignature[0] = hasOTP;
                                OtpSignature[1] = cannotMassOKPforReqSignature;
                                OtpSignature[2] = hasReqSignature;
                                OtpSignature[3] = (ji.FLAG & 16777216) == 16777216 ? true : false;
                                return OtpSignature;
                            } else {
                                if (ContactIDXAL.size() == 0) {
                                    ContactIDXAL.add(PrevContactIDXAL.get(x));
                                    PrevContactIDXAL.remove(x);
                                }
                            }
                        }
                    } else if (ContactIDXAL.size() > 0 && PrevContactIDXAL.size() == 0) {
                        if (!ContactIDXAL.contains(selectedContactIDXforSignature)) {
                            hasOTP = false;
                            cannotMassOKPforReqSignature = true;
                            hasReqSignature = true;
                            OtpSignature[0] = hasOTP;
                            OtpSignature[1] = cannotMassOKPforReqSignature;
                            OtpSignature[2] = hasReqSignature;
                            OtpSignature[3] = (ji.FLAG & 16777216) == 16777216 ? true : false;
                            return OtpSignature;
                        }
                    } else if (ContactIDXAL.size() > 0 && PrevContactIDXAL.size() > 0) {
                        for (int x = PrevContactIDXAL.size() - 1; x >= 0; x--) {
                            if (!PrevContactIDXAL.get(x).equals(selectedContactIDXforSignature)) {
                                hasOTP = false;
                                cannotMassOKPforReqSignature = true;
                                hasReqSignature = true;
                                OtpSignature[0] = hasOTP;
                                OtpSignature[1] = cannotMassOKPforReqSignature;
                                OtpSignature[2] = hasReqSignature;
                                OtpSignature[3] = (ji.FLAG & 16777216) == 16777216 ? true : false;
                                return OtpSignature;
                            } else {
                                PrevContactIDXAL.remove(x);
                            }
                        }

                        if (!ContactIDXAL.contains(selectedContactIDXforSignature)) {
                            hasOTP = false;
                            cannotMassOKPforReqSignature = true;
                            hasReqSignature = true;
                            OtpSignature[0] = hasOTP;
                            OtpSignature[1] = cannotMassOKPforReqSignature;
                            OtpSignature[2] = hasReqSignature;
                            OtpSignature[3] = (ji.FLAG & 16777216) == 16777216 ? true : false;
                            return OtpSignature;
                        }
                    }

                } else {
                    //add to prevContactIDXAL
                    PrevContactIDXAL.add(selectedContactIDXforSignature);
                }
            }

            if (ContactIDXAL.size() > 0 && PrevContactIDXAL.size() > 0) {
                for (int x = PrevContactIDXAL.size() - 1; x >= 0; x--) {
                    if (ContactIDXAL.contains(PrevContactIDXAL.get(x))) {
                        PrevContactIDXAL.remove(x);
                    }
                }
            }

            if (ContactIDXAL.size() > 0 && PrevContactIDXAL.size() > 0) {
                hasOTP = false;
                cannotMassOKPforReqSignature = true;
                hasReqSignature = true;
            } else if (ContactIDXAL.size() > 0 && PrevContactIDXAL.size() == 0) {
                hasOTP = false;
                cannotMassOKPforReqSignature = false;
                hasReqSignature = true;
            } else if (ContactIDXAL.size() == 0 && PrevContactIDXAL.size() > 0) {
                hasOTP = false;
                cannotMassOKPforReqSignature = false;
                hasReqSignature = false;
            } else if (ContactIDXAL.size() == 0 && PrevContactIDXAL.size() == 0) {
                hasOTP = false;
                cannotMassOKPforReqSignature = false;
                hasReqSignature = false;
            }

            OtpSignature[0] = hasOTP;
            OtpSignature[1] = cannotMassOKPforReqSignature;
            OtpSignature[2] = hasReqSignature;
            OtpSignature[3] = (ji.FLAG & 16777216) == 16777216 ? true : false;

        } catch (Exception e) {
            hasOTP = false;
            cannotMassOKPforReqSignature = false;
            hasReqSignature = false;
            OtpSignature = new Boolean[3];
            OtpSignature[0] = hasOTP;
            OtpSignature[1] = cannotMassOKPforReqSignature;
            OtpSignature[2] = hasReqSignature;
            OtpSignature[3] = (ji.FLAG & 16777216) == 16777216 ? true : false;
            Date dt = new Date();
            writeToFile("Exception caught at objGlobalMethods hasOTPorReqSignatureJobs. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return OtpSignature;
        }
    }

    public static Boolean[] hasOTPorReqSignatureJobs2(objCheckedItems[] objCheckedItemsAL) {
        Boolean[] OtpSignature = new Boolean[3];
        objJobInfo ji = null;
        Boolean hasOTP = false;
        Boolean cannotMassOKPforReqSignature = false;
        Boolean hasReqSignature = false;
        long selectedContactIDXforSignature = 0;
        long FirstContactIDXforSignature = 0;
        long nextContactIDXforSignature = 0;
        try {
            for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                ji = objCheckedItemsAL[i].jobinfo;

                if (((ji.FLAG & 512) == 512) && ji.PIN != null && !ji.PIN.isEmpty()) {
                    hasOTP = true;
                    cannotMassOKPforReqSignature = true;
                    break;
                }

                if (ji.DLRedirectedLocation != null && ji.DLRedirectedLocation.Contacts != null && ji.DLRedirectedLocation.Contacts.length > 0) {
                    selectedContactIDXforSignature = ji.DLRedirectedLocation.Contacts[0].IDX;
                } else if (ji.DLLocation != null && ji.DLLocation.Contacts != null && ji.DLLocation.Contacts.length > 0) {
                    selectedContactIDXforSignature = ji.DLLocation.Contacts[0].IDX;
                }

                if (FirstContactIDXforSignature == 0) {
                    FirstContactIDXforSignature = selectedContactIDXforSignature;
                    nextContactIDXforSignature = selectedContactIDXforSignature;
                } else if (FirstContactIDXforSignature > 0) {
                    nextContactIDXforSignature = selectedContactIDXforSignature;
                }

                if ((ji.FLAG & 1024) == 1024) {
                    hasReqSignature = true;
                }

                if (hasReqSignature && FirstContactIDXforSignature != nextContactIDXforSignature) {
                    cannotMassOKPforReqSignature = true;
                    break;
                }
            }

            OtpSignature[0] = hasOTP;
            OtpSignature[1] = cannotMassOKPforReqSignature;
            OtpSignature[2] = hasReqSignature;
        } catch (Exception e) {
            OtpSignature = new Boolean[3];
            OtpSignature[0] = false;
            OtpSignature[1] = false;
            OtpSignature[2] = false;
            Date dt = new Date();
            writeToFile("Exception caught at objGlobalMethods hasOTPorReqSignatureJobs2. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return OtpSignature;
        }
    }

    public static Boolean hasOTPjobs(objCheckedItems[] objCheckedItemsAL) {
        Boolean asd = false;
        objJobInfo ji = null;
        try {
            for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                ji = objCheckedItemsAL[i].jobinfo;
                if (((ji.FLAG & 512) == 512) && ji.PIN != null && !ji.PIN.isEmpty()) {
                    asd = true;
                    break;
                }
            }
        } catch (Exception e) {
            Date dt = new Date();
            String stacktrace = JobsHandler.getStackTraceString(e);
            writeToFile("Exception caught at objGLobalMethods hasOTPjobs. Exception: " + e.getMessage().toString() + ", Stacktrace: " + stacktrace, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean hasRequireSignatureJobs(objCheckedItems[] objCheckedItemsAL) {
        Boolean asd = false;
        objJobInfo ji = null;
        try {
            for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                ji = objCheckedItemsAL[i].jobinfo;
                if (((ji.FLAG & 1024) == 1024)) {
                    asd = true;
                    break;
                }
            }
        } catch (Exception e) {
            Date dt = new Date();
            String stacktrace = JobsHandler.getStackTraceString(e);
            writeToFile("Exception caught at objGLobalMethods requireSignatureJobs. Exception: " + e.getMessage().toString() + ", Stacktrace: " + stacktrace, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean isExpType2HigherExpType(int ExpType1, int ExpType2) {
        Boolean asd = false;

        try {
            if ((ExpType1 == 1 || ExpType1 == 2 || ExpType1 == 3 || ExpType1 == 4 || ExpType1 == 5 || ExpType1 == 6) &&
                    (ExpType2 == 1 || ExpType2 == 2 || ExpType2 == 3 || ExpType2 == 4 || ExpType2 == 5 || ExpType2 == 6)) {
                if (ExpType2 <= ExpType1) {
                    asd = true;
                } else if (ExpType2 > ExpType1) {
                    asd = false;
                }

            } else if ((ExpType1 == 1 || ExpType1 == 2 || ExpType1 == 3 || ExpType1 == 4 || ExpType1 == 5 || ExpType1 == 6) &&
                    (ExpType2 == 0 || ExpType2 == 7 || ExpType2 == 8 || ExpType2 == 9 || ExpType2 == 10)) {
                if (ExpType2 == 0) {
                    //530+
                    asd = false;
                } else if (ExpType2 == 7) {
                    //AOH
                    asd = false;
                } else if (ExpType2 == 8) {
                    //wkend
                    asd = false;
                } else if (ExpType2 == 9) {
                    //2days
                    asd = false;
                } else if (ExpType2 == 10) {
                    //3days
                    asd = false;
                }
            } else if ((ExpType1 == 0 || ExpType1 == 7 || ExpType1 == 8 || ExpType1 == 9) &&
                    (ExpType2 == 1 || ExpType2 == 2 || ExpType2 == 3 || ExpType2 == 4 || ExpType2 == 5 || ExpType2 == 6)) {
                asd = true;
            }
        } catch (Exception e) {
            Date dt = new Date();
            writeToFile("Exception caught at objGlobalMethods isHigherExpType. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static objAddressListSortingCheck rtn_objAddressListSortingCheck(Date currDate, int catType, objJobInfo ji, String AddressWithPostalNoUnit, String postal,
                                                                            double lat, double lng, objAddressStructure addressStructure) {
        Boolean asd = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
        Date delfromdate, pubydate, rdydate, delbydate, extdate;
        long TimeDiffMillSec = 0;
        Boolean skip = false;
        Boolean DNF = false;
        Boolean OTP = false;
        Boolean setLateColor = false;
        Boolean setBoutLateColor = false;
        //ArrayList<Date> DatesAL;
        Date[] DatesAL;
        objAddressListSortingCheck oalsc = null;
        String timingStr = "";
        int ExpType = -1;
        int rdydateistoday = 0;
        int pubydateistoday = 0;
        int delbydateistoday = 0;
        Boolean hasExtDate = false;
        Boolean isAOHsvc = false;
        Boolean isWKENDsvc = false;
        Boolean isTwoDaysvc = false;
        Boolean isThreeDaysvc = false;
        Boolean addressHasBlock = checkAddressHasBlock(addressStructure);
        //String fulladdressnospaces = returnFullAddressNoSpaces(addressStructure);
        String fulladdressnopostal = returnAddressWithoutPostal(addressStructure);

        try {
            DatesAL = objGlobalMethods.returnAllDates(ji);
            delfromdate = (Date) DatesAL[0];
            pubydate = (Date) DatesAL[1];
            rdydate = (Date) DatesAL[2];
            delbydate = (Date) DatesAL[3];
            extdate = (Date) DatesAL[4];
            ExpType = ji.ExpressType;
            //date1.compareTo(date2) > 0 : date1 after date2, == 0: date1 equal to date2, <0 date1 before date2
            if (catType == 0 || catType == 1 || catType == 4) {
                //===========================================pip list===========================================

                //timing is not included
                rdydateistoday = getZeroTimeDate(rdydate).compareTo(getZeroTimeDate(currDate));
                pubydateistoday = getZeroTimeDate(pubydate).compareTo(getZeroTimeDate(currDate));

                if (extdate != null && extdate.compareTo(delbydate) <= 0) {
                    //extdate datetime is earlier than delbydate datetime or equal to delbydate datetime, considered no time extension
                    //use delbydate
                    delbydateistoday = getZeroTimeDate(delbydate).compareTo(getZeroTimeDate(currDate));
                } else if (extdate != null && extdate.compareTo(delbydate) > 0) {
                    //extdate is same time or later than delbydate
                    //use extdate
                    hasExtDate = true;
                    delbydateistoday = getZeroTimeDate(extdate).compareTo(getZeroTimeDate(currDate));
                } else if (extdate == null) {
                    //no extdate
                    //use delbydate
                    delbydateistoday = getZeroTimeDate(delbydate).compareTo(getZeroTimeDate(currDate));
                }

                //delbydateistoday = getZeroTimeDate(delbydate).compareTo(getZeroTimeDate(currDate));

                if (ji.ExpressType == 7) {
                    //aoh svc
                    isAOHsvc = true;
                } else if (ji.ExpressType == 8) {
                    //wkend svc
                    isWKENDsvc = true;
                } else if (ji.ExpressType == 9) {
                    //2days svc
                    isTwoDaysvc = true;
                } else if (ji.ExpressType == 10) {
                    //3days svc
                    isThreeDaysvc = true;
                }

                if ((ji.FLAG & 8) == 8) {
                    DNF = true;
                }

                if ((ji.FLAG & 512) == 512) {
                    OTP = true;
                }

                if (rdydateistoday < 0 && pubydateistoday < 0 && delbydateistoday < 0) {
                    //rdydate n pubydate n delbydate r earlier than today (timing no included)
                    //already late collection & delivery
                    //use delbydate to get time difference will do since already late delivery

                    //TimeDiffMillSec = delbydate.getTime() - currDate.getTime();

                    setLateColor = true;
                    setBoutLateColor = false;
                    skip = false;
                    //timingStr = "PU FM " + sdf.format(rdydate) + ", DEL BY " + sdf.format(delbydate);
                    if (hasExtDate) {
                        TimeDiffMillSec = extdate.getTime() - currDate.getTime();
                        timingStr = myApp.getAppContext().getString(R.string.alnpufm) + " " + sdf.format(rdydate)
                                + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate) + "\n"
                                + myApp.getAppContext().getString(R.string.alnext) + " " + sdf.format(extdate);
                    } else {
                        TimeDiffMillSec = delbydate.getTime() - currDate.getTime();
                        timingStr = myApp.getAppContext().getString(R.string.alnpufm) + " " + sdf.format(rdydate)
                                + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate);
                    }

                } else if (rdydateistoday < 0 && pubydateistoday < 0 && delbydateistoday >= 0) {
                    //rdydate n pubydate r earlier than today, but delbydate is equal or later than today (timing no included)
                    //already late collection only
                    //use rdydate to get time difference will do since already late collection, rdydate will tell whichever job is later, if 2 jobs with the same calculation,
                    //there will be a sort by express type, more urgent will be on top

                    TimeDiffMillSec = rdydate.getTime() - currDate.getTime();

                    setLateColor = true;
                    setBoutLateColor = false;
                    skip = false;

                    if (pubydate.compareTo(rdydate) > 0) {
                        ////pubydate is after rdydate
                        //timingStr = "PU BY " + sdf.format(pubydate) + ", DEL BY " + sdf.format(delbydate);
                        if (hasExtDate) {
                            timingStr = myApp.getAppContext().getString(R.string.alnpuby1) + " " + sdf.format(pubydate)
                                    + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate) + "\n"
                                    + myApp.getAppContext().getString(R.string.alnext) + " " + sdf.format(extdate);
                        } else {
                            timingStr = myApp.getAppContext().getString(R.string.alnpuby1) + " " + sdf.format(pubydate)
                                    + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate);
                        }

                    } else if (pubydate.compareTo(rdydate) == 0) {
                        ////pubydate is same as rdydate
                        //timingStr = "PU FM " + sdf.format(rdydate) + ", DEL BY " + sdf.format(delbydate);
                        if (hasExtDate) {
                            timingStr = myApp.getAppContext().getString(R.string.alnpufm) + sdf.format(rdydate) + " "
                                    + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate) + "\n"
                                    + myApp.getAppContext().getString(R.string.alnext) + " " + sdf.format(extdate);
                        } else {
                            timingStr = myApp.getAppContext().getString(R.string.alnpufm) + sdf.format(rdydate) + " "
                                    + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate);
                        }

                    }
                } else if (rdydateistoday < 0 && pubydateistoday >= 0) {
                    //rdydate is earlier than today but pubydate is equal or later than today (timing not included)
                    //delbydate is cfm after pubydate
                    //may not be late yet for collection
                    //not late for delivery
                    //use pubydate to get time difference

                    TimeDiffMillSec = pubydate.getTime() - currDate.getTime();
                    if (pubydate.compareTo(currDate) <= 0) {
                        //late for collection
                        //timingStr = "PU BY " + sdf.format(pubydate) + ", DEL BY " + sdf.format(delbydate);
                        if (hasExtDate) {
                            timingStr = myApp.getAppContext().getString(R.string.alnpuby1) + " " + sdf.format(pubydate)
                                    + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate) + "\n"
                                    + myApp.getAppContext().getString(R.string.alnext) + " " + sdf.format(extdate);
                        } else {
                            timingStr = myApp.getAppContext().getString(R.string.alnpuby1) + " " + sdf.format(pubydate)
                                    + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate);
                        }

                    } else if (pubydate.compareTo(currDate) > 0) {
                        //timingStr = "PU FM " + sdf.format(rdydate) + ", PU BY " + sdf.format(pubydate);
                        timingStr = myApp.getAppContext().getString(R.string.alnpufm) + " " + sdf.format(rdydate) + myApp.getAppContext().getString(R.string.alnpuby2) + " " + sdf.format(pubydate);
                    }

                    setLateColor = true;
                    setBoutLateColor = true;
                    skip = false;

                } else if (rdydateistoday >= 0) {
                    //rdy date is today or later (timing is not included), do normal check

                    if (rdydate.compareTo(currDate) > 0) {
                        //rdydate is after currdatetime
                        //cannot collect yet
                        //use rdydate to get time difference

                        TimeDiffMillSec = rdydate.getTime() - currDate.getTime();
                        if (pubydate.compareTo(rdydate) > 0) {
                            //puby is set, after rdydate
                            //timingStr = "PU FM " + sdf.format(rdydate) + ", PU BY " + sdf.format(pubydate);
                            timingStr = myApp.getAppContext().getString(R.string.alnpufm) + " " + sdf.format(rdydate) + myApp.getAppContext().getString(R.string.alnpuby2) + " " + sdf.format(pubydate);
                        } else if (pubydate.compareTo(rdydate) <= 0) {
                            //puby is same as rdydate
                            //timingStr = "PU FM " + sdf.format(rdydate) + ", DEL BY " + sdf.format(delbydate);
                            if (hasExtDate) {
                                timingStr = myApp.getAppContext().getString(R.string.alnpufm) + " " + sdf.format(rdydate)
                                        + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate) + "\n"
                                        + myApp.getAppContext().getString(R.string.alnext) + " " + sdf.format(extdate);
                            } else {
                                timingStr = myApp.getAppContext().getString(R.string.alnpufm) + " " + sdf.format(rdydate)
                                        + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate);
                            }

                        }

                        setLateColor = false;
                        setBoutLateColor = false;
                        skip = true;
                    } else if ((rdydate.compareTo(currDate) < 0) && (pubydate.compareTo(rdydate) > 0) && (pubydate.compareTo(currDate) > 0)) {
                        //rdydate is earlier than currdatetime
                        //pubydate is set, after rdydate
                        //pubydate is later than currdatetime
                        //not late yet for collection
                        //use pubydate to get time difference

                        TimeDiffMillSec = pubydate.getTime() - currDate.getTime();
                        ////timingStr = "PU BY " + sdf.format(pubydate) + ", DEL BY " + sdf.format(delbydate);
                        //timingStr = "PU FM " + sdf.format(rdydate) + ", PU BY " + sdf.format(pubydate);
                        timingStr = myApp.getAppContext().getString(R.string.alnpufm) + " " + sdf.format(rdydate) + myApp.getAppContext().getString(R.string.alnpuby2) + " " + sdf.format(pubydate);

                        setLateColor = false;
                        long diffSeconds2 = TimeDiffMillSec / 1000;
                        if (diffSeconds2 >= 2700) {
                            //less than or equal 45min
                            setBoutLateColor = false;
                        } else {
                            setBoutLateColor = true;
                        }
                        skip = false;
                    } else if ((rdydate.compareTo(currDate) <= 0) && (pubydate.compareTo(rdydate) > 0) && (pubydate.compareTo(currDate) <= 0)) {
                        //rdydate is earlier than or equal to currdatetime
                        //puby is set, after rdydate
                        //puby is earlier or equal to currdatetime
                        //already late for collection
                        //use pubydate to get time difference

                        TimeDiffMillSec = pubydate.getTime() - currDate.getTime();
                        //timingStr = "PU FM " + sdf.format(rdydate) + ", PU BY " + sdf.format(pubydate);
                        timingStr = myApp.getAppContext().getString(R.string.alnpufm) + " " + sdf.format(rdydate) + myApp.getAppContext().getString(R.string.alnpuby2) + " " + sdf.format(pubydate);

                        setLateColor = true;
                        setBoutLateColor = false;
                        skip = false;

                    } else if ((rdydate.compareTo(currDate) <= 0) && (pubydate.compareTo(rdydate) == 0) && (delbydate.compareTo(currDate) > 0) && hasExtDate) {
                        //rdydate is earlier than or equal to currdatetime
                        //puby not set
                        //delbydate is after currdatetime
                        //has time extension, definitely is after delbydate
                        //might be late for delivery
                        //use extdate to get time difference

                        TimeDiffMillSec = extdate.getTime() - currDate.getTime();
                        //timingStr = "PU FM " + sdf.format(rdydate) + ", DEL BY " + sdf.format(delbydate);
                        timingStr = myApp.getAppContext().getString(R.string.alnpufm) + " " + sdf.format(rdydate)
                                + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate) + "\n"
                                + myApp.getAppContext().getString(R.string.alnext) + " " + sdf.format(extdate);


                        setLateColor = false;
                        long diffSeconds2 = TimeDiffMillSec / 1000;
                        if (diffSeconds2 >= 2700) {
                            //less than or equal 45min
                            setBoutLateColor = false;
                        } else {
                            setBoutLateColor = true;
                        }
                        skip = false;
                    } else if ((rdydate.compareTo(currDate) <= 0) && (pubydate.compareTo(rdydate) == 0) && (delbydate.compareTo(currDate) > 0) && !hasExtDate) {
                        //rdydate is earlier than or equal to currdatetime
                        //puby not set
                        //delbydate is after currdatetime
                        //dun have time extension
                        //might be late for delivery
                        //use delbydate to get time difference

                        TimeDiffMillSec = delbydate.getTime() - currDate.getTime();
                        //timingStr = "PU FM " + sdf.format(rdydate) + ", DEL BY " + sdf.format(delbydate);
                        timingStr = myApp.getAppContext().getString(R.string.alnpufm) + " " + sdf.format(rdydate)
                                + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate);


                        setLateColor = false;
                        long diffSeconds2 = TimeDiffMillSec / 1000;
                        if (diffSeconds2 >= 2700) {
                            //less than or equal 45min
                            setBoutLateColor = false;
                        } else {
                            setBoutLateColor = true;
                        }
                        skip = false;
                    } else if ((rdydate.compareTo(currDate) <= 0) && (pubydate.compareTo(rdydate) == 0) && (delbydate.compareTo(currDate) <= 0) && hasExtDate) {
                        //rdydate is earlier than or equal to currdatetime
                        //puby not set
                        //delbydate is b4 or equal to currdatetime
                        //has time extension, definitely later than delbydate
                        //already late for delivery
                        //use extdate to get time difference

                        TimeDiffMillSec = extdate.getTime() - currDate.getTime();
                        //timingStr = "PU FM " + sdf.format(rdydate) + ", DEL BY " + sdf.format(delbydate);
                        timingStr = myApp.getAppContext().getString(R.string.alnpufm) + " " + sdf.format(rdydate)
                                + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate) + "\n"
                                + myApp.getAppContext().getString(R.string.alnext) + " " + sdf.format(extdate);

                        setLateColor = true;
                        setBoutLateColor = false;
                        skip = false;

                    } else if ((rdydate.compareTo(currDate) <= 0) && (pubydate.compareTo(rdydate) == 0) && (delbydate.compareTo(currDate) <= 0) && !hasExtDate) {
                        //rdydate is earlier than or equal to currdatetime
                        //puby not set
                        //delbydate is b4 or equal to currdatetime
                        //already late for delivery
                        //use delbydate to get time difference

                        TimeDiffMillSec = delbydate.getTime() - currDate.getTime();
                        //timingStr = "PU FM " + sdf.format(rdydate) + ", DEL BY " + sdf.format(delbydate);
                        timingStr = myApp.getAppContext().getString(R.string.alnpufm) + " " + sdf.format(rdydate)
                                + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate);

                        setLateColor = true;
                        setBoutLateColor = false;
                        skip = false;
                    }
                }

            } else if (catType == 2 || catType == 3 || catType == 5) {
                if (extdate != null && extdate.compareTo(delbydate) > 0) {
                    //extdate later than delbydate
                    //use extdate
                    hasExtDate = true;
                }

                //===========================================dip list===========================================
                if (delfromdate.compareTo(currDate) > 0) {
                    //del from time is later
                    //skip, cannot del now (or so to speak should not del now

                    if (hasExtDate) {
                        TimeDiffMillSec = extdate.getTime() - currDate.getTime();
                    } else {
                        TimeDiffMillSec = delbydate.getTime() - currDate.getTime();
                    }

                    if ((ji.FLAG & 8) == 8) {
                        DNF = true;
                    }

                    if ((ji.FLAG & 512) == 512) {
                        OTP = true;
                    }
                    setLateColor = false;
                    setBoutLateColor = false;
                    skip = true;
                    ////timingStr = "DEL FM " + sdf.format(delfromdate) + ", DEL BY " + sdf.format(delbydate);
                    //timingStr = myApp.getAppContext().getString(R.string.alndelfm) + " " + sdf.format(delfromdate) + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate);
                    if (hasExtDate) {
                        timingStr = myApp.getAppContext().getString(R.string.alndelfm) + " "
                                + sdf.format(delfromdate) + " - " + sdf.format(delbydate) + "\n"
                                + myApp.getAppContext().getString(R.string.alnext) + " " + sdf.format(extdate);
                    } else {
                        timingStr = myApp.getAppContext().getString(R.string.alndelfm) + " "
                                + sdf.format(delfromdate) + " - " + sdf.format(delbydate);
                    }

                } else if (delfromdate.compareTo(currDate) <= 0) {
                    //del from time is now or already passed curr time
                    //get the time diff between now n del by time


                    if ((ji.FLAG & 8) == 8) {
                        DNF = true;
                    }

                    if ((ji.FLAG & 512) == 512) {
                        OTP = true;
                    }

                    skip = false;
                    ////timingStr = "DEL FM " + sdf.format(delfromdate) + ", DEL BY " + sdf.format(delbydate);
                    //timingStr = myApp.getAppContext().getString(R.string.alndelfm) + " " + sdf.format(delfromdate) + myApp.getAppContext().getString(R.string.alndelby2) + " " + sdf.format(delbydate);
                    if (hasExtDate) {
                        TimeDiffMillSec = extdate.getTime() - currDate.getTime();

                        if (extdate.compareTo(currDate) > 0) {
                            //extdate is after currdatetime
                            setLateColor = false;
                            long diffSeconds2 = TimeDiffMillSec / 1000;
                            if (diffSeconds2 >= 2700) {
                                //less than or equal 45min
                                setBoutLateColor = false;
                            } else {
                                setBoutLateColor = true;
                            }
                        } else if (extdate.compareTo(currDate) <= 0) {
                            //delbydate is now or already passed
                            //late delivery already
                            setLateColor = true;
                            setBoutLateColor = false;
                        }

                        timingStr = myApp.getAppContext().getString(R.string.alndelfm) + " "
                                + sdf.format(delfromdate) + " - " + sdf.format(delbydate) + "\n"
                                + myApp.getAppContext().getString(R.string.alnext) + " " + sdf.format(extdate);
                    } else {
                        TimeDiffMillSec = delbydate.getTime() - currDate.getTime();

                        if (delbydate.compareTo(currDate) > 0) {
                            //delbydate is after currdatetime
                            setLateColor = false;
                            long diffSeconds2 = TimeDiffMillSec / 1000;
                            if (diffSeconds2 >= 2700) {
                                //less than or equal 45min
                                setBoutLateColor = false;
                            } else {
                                setBoutLateColor = true;
                            }
                        } else if (delbydate.compareTo(currDate) <= 0) {
                            //delbydate is now or already passed
                            //late delivery already
                            setLateColor = true;
                            setBoutLateColor = false;
                        }

                        timingStr = myApp.getAppContext().getString(R.string.alndelfm) + " "
                                + sdf.format(delfromdate) + " - " + sdf.format(delbydate);
                    }

                }
            }

            oalsc = new objAddressListSortingCheck(TimeDiffMillSec, postal, AddressWithPostalNoUnit, fulladdressnopostal, lat, lng, skip, DNF, OTP,
                    timingStr, ExpType, setLateColor, setBoutLateColor, addressHasBlock, rdydate, pubydate, delfromdate, delbydate, extdate);
        } catch (Exception ex) {
            Date dt = new Date();
            writeToFile("Exception caught at objGlobalMethods isYestJob. Exception: " + ex.getMessage().toString(), "", "", "", "");
            oalsc = null;
        } finally {
            return oalsc;
        }
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static boolean isDuringWeekend(Date d) {

        Calendar c = Calendar.getInstance();
        c.setTime(d);

        return c.get(c.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(c.DAY_OF_WEEK) == Calendar.SUNDAY;

    }

    public static void resetAddressListRoutingScrollIndexNpostion(SharedPreferences gcmp) {
        /*objGlobalMethods.setGCMPrefs(gcmp, "AddressListRoutingScrollSavedIndex", String.valueOf(0));
        objGlobalMethods.setGCMPrefs(gcmp, "AddressListRoutingScrollSavedPosition", String.valueOf(0));*/
        SharedPreferences.Editor editor = gcmp.edit();
        editor.putString("AddressListRoutingScrollSavedIndex", "0");
        editor.putString("AddressListRoutingScrollSavedPosition", "0");
        editor.apply();
    }

    public static void resetAddressListNewScrollIndexNpostion(SharedPreferences gcmp) {
        /*objGlobalMethods.setGCMPrefs(gcmp, "AddressListNewScrollSavedIndex", String.valueOf(0));
        objGlobalMethods.setGCMPrefs(gcmp, "AddressListNewScrollSavedPosition", String.valueOf(0));*/
        SharedPreferences.Editor editor = gcmp.edit();
        editor.putString("AddressListNewScrollSavedIndex", "0");
        editor.putString("AddressListNewScrollSavedPosition", "0");
        editor.apply();
    }

    public static void resetJobsListGroupScrollIndexNpostion(SharedPreferences gcmp) {
        SharedPreferences.Editor editor = gcmp.edit();
        editor.putString("JobsListGroupScrollSavedIndex", "0");
        editor.putString("JobsListGroupScrollSavedPosition", "0");
        editor.putString("objMultiSelectInitialCriteriaJsonStr", "");
        editor.apply();
    }

    public static void resetDrawerMain2ActivityScrollIndexNposition(SharedPreferences gcmp) {
        objGlobalMethods.setGCMPrefs(gcmp, "DrawerMain2ScrollSavedIndex", String.valueOf(0));
        objGlobalMethods.setGCMPrefs(gcmp, "DrawerMain2ScrollSavedPosition", String.valueOf(0));
    }

    //NFC ACTION_TAG_DISCOVERED
    public static String ByteArrayToHexString(byte[] inarray) { // converts byte arrays to string
        int i, j, in;
        String[] hex = {
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
        };
        String out = "";

        /*for (j = 0; j < inarray.length; ++j) {
            in = inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }*/

        for (j = 0; j < inarray.length; ++j) {
            in = inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
    //NFC ACTION_TAG_DISCOVERED

    public static String ReplaceCheckListDisplayTextLang(String txt) {

        try {
            txt = txt.replace("LOA Collected", myApp.getAppContext().getString(R.string.cl_loa));
            txt = txt.replace("Trade-in model expected", myApp.getAppContext().getString(R.string.cl_tradeinexpected));
            txt = txt.replace("No trade-in device expected. Do not accept any device from receiver", myApp.getAppContext().getString(R.string.cl_donotaccept));
        } catch (Exception e) {
            txt = txt;
        } finally {
            return txt;
        }
    }

    public static String returnShortMonthString(Integer mth) {
        String asd = "";
        try {
            if (mth == 1) {
                asd = myApp.getAppContext().getString(R.string.jan);
            } else if (mth == 2) {
                asd = myApp.getAppContext().getString(R.string.feb);
            } else if (mth == 3) {
                asd = myApp.getAppContext().getString(R.string.mar);
            } else if (mth == 4) {
                asd = myApp.getAppContext().getString(R.string.apr);
            } else if (mth == 5) {
                asd = myApp.getAppContext().getString(R.string.may);
            } else if (mth == 6) {
                asd = myApp.getAppContext().getString(R.string.jun);
            } else if (mth == 7) {
                asd = myApp.getAppContext().getString(R.string.jul);
            } else if (mth == 8) {
                asd = myApp.getAppContext().getString(R.string.aug);
            } else if (mth == 9) {
                asd = myApp.getAppContext().getString(R.string.sep);
            } else if (mth == 10) {
                asd = myApp.getAppContext().getString(R.string.oct);
            } else if (mth == 11) {
                asd = myApp.getAppContext().getString(R.string.nov);
            } else if (mth == 12) {
                asd = myApp.getAppContext().getString(R.string.dec);
            }
        } catch (Exception e) {

        } finally {
            return asd;
        }
    }

    public static Dialog showInstallWaze() {
        Context c = myApp.getAppContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setTitle("");
        builder.setCancelable(false);
        builder.setMessage(c.getString(R.string.installwaze));

        builder.setPositiveButton(c.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                myApp.currentActivity.startActivity(intent);
            }
        });

        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.dismiss();
            }
        });

        AlertDialog alertdialog = builder.create();
        return alertdialog;
    }

    public static Dialog showDialog(Context ctx, String btnText, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("");
        builder.setCancelable(false);
        builder.setMessage(msg);

        builder.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        AlertDialog alertdialog = builder.create();
        return alertdialog;
    }

    public static Dialog showCancellableDialog(Context ctx, String title, String btnText, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setMessage(msg);

        builder.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        AlertDialog alertdialog = builder.create();
        return alertdialog;
    }

    public static Dialog showCustomVerificationDialog(Activity ctx, String btnText, String boldtxtstr, String msg, boolean verified) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("");
        builder.setCancelable(false);

        final View cv = ctx.getLayoutInflater().inflate(R.layout.custom_verified_dialog, null);
        builder.setView(cv);

        TextView boldtxt = cv.findViewById(R.id.boldtxt);
        boldtxt.setText(boldtxtstr);
        TextView txt = cv.findViewById(R.id.txt);
        txt.setText(msg);
        LinearLayout LL = cv.findViewById(R.id.LL);
        if (verified)
            LL.setBackgroundResource(R.color.brightergreen);

        boldtxt.setTextColor(!verified ? Color.WHITE : Color.BLACK);
        txt.setTextColor(!verified ? Color.WHITE : Color.BLACK);
        boldtxt.setVisibility(!objGlobalMethods.IsEmptyString(boldtxtstr) ? View.VISIBLE : View.GONE);
        txt.setVisibility(!objGlobalMethods.IsEmptyString(msg) ? View.VISIBLE : View.GONE);

        builder.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        AlertDialog alertdialog = builder.create();
        return alertdialog;
    }

    public Dialog ProgressBarDialog(String msg) {
        AlertDialog alertdialog = null;
        try {
            final Context ctx = myApp.getAppContext();
            final Activity act = myApp.currentActivity;
            final AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setCancelable(false);
            final View v = act.getLayoutInflater().inflate(R.layout.progressbar_layout, null);
            builder.setView(v);
            TextView lbl = (TextView) v.findViewById(R.id.lblText);
            lbl.setText(msg);

            alertdialog = builder.create();

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods ProgressBarDialog. Exception: " + e.getMessage() + ". Stack:\n" + s, "", "", "", "");
        } finally {
            return alertdialog;
        }
    }

    public static void showPR_lateprompt() {
        AlertDialog pr_LatepromptAlertDialog = null;
        SharedPreferences gcmp = getGcmPreferences();
        try {
            final Context ctx = myApp.getAppContext();
            final Activity act = myApp.currentActivity;
            boolean isScreenDisplayOn = isScreenDisplayOn(ctx);
            boolean isInBackground = isApplicationBroughtToBackground();
            if (isScreenDisplayOn && !isInBackground) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setCancelable(true);
                final View callView = act.getLayoutInflater().inflate(R.layout.custom_prlate01_dialog, null);
                String langType = objGlobalMethods.getGCMPrefs(gcmp, "language");
                builder.setView(callView);
                builder.setCancelable(false);
                Button btnPRLate01Submit = (Button) callView.findViewById(R.id.btnPRLate01Submit);
                final Spinner ddlPRLate01Reason = (Spinner) callView.findViewById(R.id.ddlPRLate01Reason);

                ArrayList<Spinner_PostDataOKP> SpinnerPostDataArr = new ArrayList<Spinner_PostDataOKP>();
                Spinner_PostDataOKP postdata = null;
                Spinner_PostDataOKP postdata2 = null;
                Spinner_PostDataOKP postdata3 = null;
                if (langType.equals("en") || langType.equals("")) {
                    postdata = new Spinner_PostDataOKP(ctx.getString(R.string.recevierchking), ctx.getString(R.string.recevierchking), "", "", "", "", "", "", 0);
                    postdata2 = new Spinner_PostDataOKP(ctx.getString(R.string.recevierchkingjustfinished), ctx.getString(R.string.recevierchkingjustfinished), "", "", "", "", "", "", 0);
                    postdata3 = new Spinner_PostDataOKP(ctx.getString(R.string.presswrongbtn), ctx.getString(R.string.presswrongbtn), "", "", "", "", "", "", 0);
                } else if (langType.equals("zh")) {
                    postdata = new Spinner_PostDataOKP(ctx.getString(R.string.recevierchking_C), ctx.getString(R.string.recevierchking), "", "", "", "", "", "", 0);
                    postdata2 = new Spinner_PostDataOKP(ctx.getString(R.string.recevierchkingjustfinished_C), ctx.getString(R.string.recevierchkingjustfinished), "", "", "", "", "", "", 0);
                    postdata3 = new Spinner_PostDataOKP(ctx.getString(R.string.presswrongbtn_C), ctx.getString(R.string.presswrongbtn), "", "", "", "", "", "", 0);
                }
                SpinnerPostDataArr.add(postdata);
                SpinnerPostDataArr.add(postdata2);
                SpinnerPostDataArr.add(postdata3);
                Spinner_PostItemAdapterOKP adapter = new Spinner_PostItemAdapterOKP(ctx, SpinnerPostDataArr);
                ddlPRLate01Reason.setAdapter(adapter);

                ddlPRLate01Reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        TextView lblStatusCodesDesc = ((TextView) (ddlPRLate01Reason.getSelectedView().findViewById(R.id.lblStatusCodesDesc)));
                        lblStatusCodesDesc.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                        lblStatusCodesDesc.setTextColor(Color.WHITE);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                final AlertDialog alertdialog = builder.create();
                alertdialog.show();
                JobsHandler.disposeRulingTimer();
                prepareSendPR_LateMsg0Email("Prompted. Awaiting GC input.");

                btnPRLate01Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            TextView lblDescEN = ((TextView) (ddlPRLate01Reason.getSelectedView().findViewById(R.id.lblStatusCodesDescEN)));
                            String val = lblDescEN.getText().toString();
                            logPR_latemsg0(val, false);
                            alertdialog.cancel();
                            JobsHandler.disposeRulingTimer();
                            JobsHandler.startRulingTimer(new Date(), Long.parseLong("180000"));
                            //JobsHandler.startRulingTimer(new Date(), Long.parseLong("60000"));
                        } catch (Exception e) {
                            writeToFile("Exception at JobInfoActivity btnVerifyShipmentSubmitValue.setOnClickListener. Exception: " + e.getMessage(), "", "", "", "");
                        }
                    }
                });
            } else {
                /*if (isScreenDisplayOn && isInBackground) {
                    logPR_latemsg0("LUKE is not focused.", true);
                } else if (!isScreenDisplayOn) {
                    logPR_latemsg0("Screen is off currently.", true);
                } else {
                    logPR_latemsg0("Other unknown behavior.", false);
                }*/
                String msg = "";
                msg = (isScreenDisplayOn && isInBackground) ? "LUKE is not focused." : (!isScreenDisplayOn) ? "Screen is off currently." : "Other unknown behavior.";
                logPR_latemsg0(msg, ((isScreenDisplayOn && isInBackground) || (!isScreenDisplayOn)) ? true : false);
                JobsHandler.disposeRulingTimer();
                JobsHandler.startRulingTimer(new Date(), Long.parseLong("180000"));
                //JobsHandler.startRulingTimer(new Date(), Long.parseLong("60000"));
                prepareSendPR_LateMsg0Email(msg);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods showPR_lateprompt. Exception: " + e.getMessage() + ". Stack:\n" + s, "", "", "", "");
        }
    }

    public static void logPR_latemsg0(String val, boolean reqFuturePrompt) {
        try {
            SharedPreferences gcmp = getGcmPreferences();
            String pr_latemsg0 = getGCMPrefs(gcmp, "pr_latemsg0");
            SimpleDateFormat formatter2 = new SimpleDateFormat("ddMMyyyy HH:mm:ss");
            Date dt = new Date();
            String dtstr = formatter2.format(dt);
            if (IsEmptyString(pr_latemsg0)) {
                pr_latemsg0 += !IsEmptyString(val) ? dtstr + ": " + val : "";
            } else {
                pr_latemsg0 += !IsEmptyString(val) ? "\n" + dtstr + ": " + val : "";
            }
            objGlobalMethods.setGCMPrefs(gcmp, "pr_latemsg0", pr_latemsg0);
            objGlobalMethods.setGCMPrefs(gcmp, "pr_latemsg0_reqFuturePrompt", reqFuturePrompt ? "t" : "");
        } catch (Exception e) {
            writeToFile("Exeption at objGlobalMethods logPR_latemsg0. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static Dialog showChangeAirplaneModeSettingsDialog(final Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_airplanemode));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (android.os.Build.VERSION.SDK_INT < 17) {
                    try {
                        Intent intentAirplaneMode = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                        intentAirplaneMode.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //ctx.startActivity(intentAirplaneMode);
                        myApp.currentActivity.startActivity(intentAirplaneMode);
                    } catch (ActivityNotFoundException e) {
                        writeToFile("ActivityNotFoundException at objGlobalMethods showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                    }
                } else {
                    Intent intent1 = new Intent("android.settings.WIRELESS_SETTINGS");
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //ctx.startActivity(intent1);
                    myApp.currentActivity.startActivity(intent1);
                }

            }
        });
        AlertDialog alert = builder.create();
        return alert;
    }

    public static Dialog showChangeGPSModeSettingsDialog(final Context ctx) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //ctx.startActivity(myIntent);
                    myApp.currentActivity.startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    writeToFile("ActivityNotFoundException at objGlobalMethods showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        return alert;
    }

    public static Dialog showChangePermissionDialog(final Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpspermission));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", ctx.getPackageName(), null);
                    intent.setData(uri);
                    //ctx.startActivity(intent);
                    myApp.currentActivity.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    writeToFile("ActivityNotFoundException at objGlobalMethods showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        return alert;
    }

    public static Dialog showChangeGPStoHighAccModeSettingsDialog(final Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmodehighacc));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //ctx.startActivity(myIntent);
                    myApp.currentActivity.startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    writeToFile("ActivityNotFoundException at objGlobalMethods showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        return alert;
    }

    public static Dialog showEnableNFC_Dialog() {
        final Context ctx = myApp.getAppContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        String msg = myApp.getAppContext().getString(R.string.alert_enablednfc);
        builder.setMessage(msg);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    //ctx.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    myApp.currentActivity.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    writeToFile("ActivityNotFoundException at objGlobalMethods showEnableNFC_Dialog setPositiveButton onClick. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                } catch (Exception e) {
                    writeToFile("Exception at objGlobalMethods showEnableNFC_Dialog setPositiveButton onClick. Exception: "
                            + e.toString(), "", "", "", "");
                }

            }
        });

        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.cancel();
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    writeToFile("ActivityNotFoundException at objGlobalMethods showEnableNFC_Dialog setNegativeButton onClick. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                } catch (Exception e) {
                    writeToFile("Exception at objGlobalMethods showEnableNFC_Dialog setPositiveButton onClick. Exception: "
                            + e.toString(), "", "", "", "");
                }

            }
        });

        AlertDialog alert = builder.create();
        return alert;
    }

    public static void showRegBTTL_DialogNew(String msg, final boolean Cancelable) {
        try {
            SharedPreferences g = getGcmPreferences();
            String showRegBTTL_Dialog_IsShowing = getGCMPrefs(g, "showRegBTTL_Dialog_IsShowing");
            if (IsEmptyString(showRegBTTL_Dialog_IsShowing)) {
                final Context ctx = myApp.getAppContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                builder.setCancelable(false);

                writeToFile("showRegBTTL_DialogNew called.", "", "", "", "");

                builder.setMessage(msg);
                builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            SharedPreferences g = getGcmPreferences();
                            setGCMPrefs(g, "showRegBTTL_Dialog_IsShowing", "");
                            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class) {
                                dialog.dismiss();
                                final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                                dialog.dismiss();
                                final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListRoutingActivity.class) {
                                dialog.dismiss();
                                final AddressListRoutingActivity act = (AddressListRoutingActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListNewActivity.class) {
                                dialog.dismiss();
                                final AddressListNewActivity act = (AddressListNewActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobsListGroupActivity.class) {
                                dialog.dismiss();
                                final JobsListGroupActivity act = (JobsListGroupActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DashboardSubActivity.class) {
                                dialog.dismiss();
                                final DashboardSubActivity act = (DashboardSubActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MapsAllActivity.class) {
                                dialog.dismiss();
                                final MapsAllActivity act = (MapsAllActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MPDMainActivity.class) {
                                dialog.dismiss();
                                final MPDMainActivity act = (MPDMainActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            }

                        } catch (ActivityNotFoundException e) {
                            String stack = JobsHandler.getStackTraceString(e);
                            writeToFile("ActivityNotFoundException at objGlobalMethods showRegBTTL_DialogNew setPositiveButton onClick. ActivityNotFoundException: "
                                    + e.toString() + "\nStack: " + stack, "", "", "", "");
                        } catch (Exception e) {
                            writeToFile("Exception at objGlobalMethods showRegBTTL_DialogNew setPositiveButton onClick. Exception: "
                                    + e.toString(), "", "", "", "");
                        }

                    }
                });

                if (Cancelable) {
                    builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                SharedPreferences g = getGcmPreferences();
                                setGCMPrefs(g, "showRegBTTL_Dialog_IsShowing", "");
                                dialog.cancel();
                            } catch (ActivityNotFoundException e) {
                                String stack = JobsHandler.getStackTraceString(e);
                                writeToFile("ActivityNotFoundException at objGlobalMethods showRegBTTL_DialogNew setNegativeButton onClick. ActivityNotFoundException: "
                                        + e.toString() + "\nStack: " + stack, "", "", "", "");
                            } catch (Exception e) {
                                writeToFile("Exception at objGlobalMethods showRegBTTL_DialogNew setNegativeButton onClick. Exception: "
                                        + e.toString(), "", "", "", "");
                            }

                        }
                    });
                }

                setGCMPrefs(g, "showRegBTTL_Dialog_IsShowing", "t");
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods showRegBTTL_DialogNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static Dialog showRegBTTL_Dialog(String msg) {
        final Context ctx = myApp.getAppContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);

        writeToFile("showRegBTTL_Dialog called.", "", "", "", "");

        builder.setMessage(msg);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class) {
                        dialog.dismiss();
                        final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                        dialog.dismiss();
                        final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListRoutingActivity.class) {
                        dialog.dismiss();
                        final AddressListRoutingActivity act = (AddressListRoutingActivity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListNewActivity.class) {
                        dialog.dismiss();
                        final AddressListNewActivity act = (AddressListNewActivity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobsListGroupActivity.class) {
                        dialog.dismiss();
                        final JobsListGroupActivity act = (JobsListGroupActivity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DashboardSubActivity.class) {
                        dialog.dismiss();
                        final DashboardSubActivity act = (DashboardSubActivity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MapsAllActivity.class) {
                        dialog.dismiss();
                        final MapsAllActivity act = (MapsAllActivity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, TempLoggerRegActivity.class);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    }

                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    writeToFile("ActivityNotFoundException at objGlobalMethods showRegBTTL_Dialog setPositiveButton onClick. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                } catch (Exception e) {
                    writeToFile("Exception at objGlobalMethods showRegBTTL_Dialog setPositiveButton onClick. Exception: "
                            + e.toString(), "", "", "", "");
                }

            }
        });

        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.cancel();
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    writeToFile("ActivityNotFoundException at objGlobalMethods showRegBTTL_Dialog setNegativeButton onClick. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                } catch (Exception e) {
                    writeToFile("Exception at objGlobalMethods showRegBTTL_Dialog setPositiveButton onClick. Exception: "
                            + e.toString(), "", "", "", "");
                }

            }
        });

        AlertDialog alert = builder.create();
        return alert;
    }

    public static void showMpd_DialogNew(ArrayList<String> mpd) {
        try {
            SharedPreferences g = getGcmPreferences();
            String showMPD_Dialog_IsShowing = getGCMPrefs(g, "showMPD_Dialog_IsShowing");
            if (IsEmptyString(showMPD_Dialog_IsShowing)) {
                final Context ctx = myApp.getAppContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                builder.setCancelable(false);
                String msg = ctx.getString(R.string.alert_ensurempddrawn);
                writeToFile("showMPD_Dialog called.", "", "", "", "");
                for (int i = 0; i <= mpd.size() - 1; i++) {
                    msg += "\n" + mpd.get(i);
                }
                builder.setMessage(msg);
                builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            SharedPreferences g = getGcmPreferences();
                            setGCMPrefs(g, "showMPD_Dialog_IsShowing", "");
                            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class) {
                                dialog.dismiss();
                                final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                                dialog.dismiss();
                                final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListRoutingActivity.class) {
                                dialog.dismiss();
                                final AddressListRoutingActivity act = (AddressListRoutingActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListNewActivity.class) {
                                dialog.dismiss();
                                final AddressListNewActivity act = (AddressListNewActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobsListGroupActivity.class) {
                                dialog.dismiss();
                                final JobsListGroupActivity act = (JobsListGroupActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DashboardSubActivity.class) {
                                dialog.dismiss();
                                final DashboardSubActivity act = (DashboardSubActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MapsAllActivity.class) {
                                dialog.dismiss();
                                final MapsAllActivity act = (MapsAllActivity) myApp.currentActivity;
                                Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                                myApp.currentActivity.startActivity(myIntent);
                                act.finish();
                            }

                        } catch (ActivityNotFoundException e) {
                            SharedPreferences g = getGcmPreferences();
                            setGCMPrefs(g, "showMPD_Dialog_IsShowing", "");
                            String stack = JobsHandler.getStackTraceString(e);
                            writeToFile("ActivityNotFoundException at objGlobalMethods showMpd_DialogNew setPositiveButton onClick. ActivityNotFoundException: "
                                    + e.toString() + "\nStack: " + stack, "", "", "", "");
                        } catch (Exception e) {
                            SharedPreferences g = getGcmPreferences();
                            setGCMPrefs(g, "showMPD_Dialog_IsShowing", "");
                            writeToFile("Exception at objGlobalMethods showMpd_DialogNew setPositiveButton onClick. Exception: "
                                    + e.toString(), "", "", "", "");
                        }

                    }
                });

                /*builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            SharedPreferences g = getGcmPreferences();
                            setGCMPrefs(g, "showMPD_Dialog_IsShowing", "");
                            dialog.cancel();
                        } catch (ActivityNotFoundException e) {
                            SharedPreferences g = getGcmPreferences();
                            setGCMPrefs(g, "showMPD_Dialog_IsShowing", "");
                            String stack = JobsHandler.getStackTraceString(e);
                            writeToFile("ActivityNotFoundException at objGlobalMethods showMpd_DialogNew setNegativeButton onClick. ActivityNotFoundException: "
                                    + e.toString() + "\nStack: " + stack, "", "", "", "");
                        } catch (Exception e) {
                            SharedPreferences g = getGcmPreferences();
                            setGCMPrefs(g, "showMPD_Dialog_IsShowing", "");
                            writeToFile("Exception at objGlobalMethods showMpd_DialogNew setNegativeButton onClick. Exception: "
                                    + e.toString(), "", "", "", "");
                        }

                    }
                });*/

                setGCMPrefs(g, "showMPD_Dialog_IsShowing", "t");
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods showMpd_DialogNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static Dialog showMPD_Dialog(ArrayList<String> mpd) {
        final Context ctx = myApp.getAppContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        String msg = ctx.getString(R.string.alert_ensurempddrawn);
        writeToFile("showMPD_Dialog called.", "", "", "", "");
        for (int i = 0; i <= mpd.size() - 1; i++) {
            msg += "\n" + mpd.get(i);
        }
        builder.setMessage(msg);
        builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class) {
                        dialog.dismiss();
                        final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                        //ctx.startActivity(myIntent);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                        dialog.dismiss();
                        final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                        //ctx.startActivity(myIntent);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListRoutingActivity.class) {
                        dialog.dismiss();
                        final AddressListRoutingActivity act = (AddressListRoutingActivity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                        //ctx.startActivity(myIntent);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListNewActivity.class) {
                        dialog.dismiss();
                        final AddressListNewActivity act = (AddressListNewActivity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                        //ctx.startActivity(myIntent);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobsListGroupActivity.class) {
                        dialog.dismiss();
                        final JobsListGroupActivity act = (JobsListGroupActivity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                        //ctx.startActivity(myIntent);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DashboardSubActivity.class) {
                        dialog.dismiss();
                        final DashboardSubActivity act = (DashboardSubActivity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                        //ctx.startActivity(myIntent);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MapsAllActivity.class) {
                        dialog.dismiss();
                        final MapsAllActivity act = (MapsAllActivity) myApp.currentActivity;
                        Intent myIntent = new Intent(act, MPD_CheckOutActivity.class);
                        //ctx.startActivity(myIntent);
                        myApp.currentActivity.startActivity(myIntent);
                        act.finish();
                    }

                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    writeToFile("ActivityNotFoundException at objGlobalMethods showMPD_Dialog setPositiveButton onClick. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                } catch (Exception e) {
                    writeToFile("Exception at objGlobalMethods showMPD_Dialog setPositiveButton onClick. Exception: "
                            + e.toString(), "", "", "", "");
                }

            }
        });

        builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.cancel();
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    writeToFile("ActivityNotFoundException at objGlobalMethods showMPD_Dialog setNegativeButton onClick. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                } catch (Exception e) {
                    writeToFile("Exception at objGlobalMethods showMPD_Dialog setPositiveButton onClick. Exception: "
                            + e.toString(), "", "", "", "");
                }

            }
        });

        AlertDialog alert = builder.create();
        return alert;
    }

    public static boolean IsMaintenanceHours(objSettings sets) {
        int fromYr = 2019;
        int fromMth = 3;
        int fromDay = 29;
        int fromHr = 19;
        int fromMin = 0;

        int toYr = 2019;
        int toMth = 3;
        int toDay = 29;
        int toHr = 23;
        int toMin = 59;

        Boolean asd = false;
        Date tweakedDateFrom = new Date();
        Date tweakedDateTo = new Date();
        Date tweakedDateCurr = new Date();
        try {
            Calendar calendartweakedDateFrom = new GregorianCalendar(fromYr, fromMth - 1, fromDay, fromHr, fromMin, 0);
            tweakedDateFrom = calendartweakedDateFrom.getTime();

            Calendar calendartweakedDateTo = new GregorianCalendar(toYr, toMth - 1, toDay, toHr, toMin, 0);
            tweakedDateTo = calendartweakedDateTo.getTime();

            //curr time is == or later than dateFrom, curr time is <= dateTo
            asd = tweakedDateCurr.compareTo(tweakedDateFrom) >= 0 && tweakedDateCurr.compareTo(tweakedDateTo) <= 0 ? true : false;
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods currTimeIsBetweenFromDateToDateToIgnore. Exception: " + e.getMessage(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean currTimeIsBeforeGivenDate(int gyr, int gmth, int gday) {
        Boolean asd = false;
        Date tweakedGivenDate = new Date();
        Date tweakedDateCurr = new Date();
        try {
            Calendar c = new GregorianCalendar(gyr, gmth - 1, gday, 0, 0, 0);
            tweakedGivenDate = c.getTime();

            //0 comes when two date are same,
            //1 comes when date1 is higher then date2
            //-1 comes when date1 is lower then date2
            asd = tweakedDateCurr.compareTo(tweakedGivenDate) < 0;
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods currTimeIsBeforeGivenDate. Exception: " + e.getMessage(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean currTimeAllowJobsRefreshDuringMaintenanceDay() {
        boolean asd = true;
        int fromYr = 2023;
        int fromMth = 6;
        int fromDay = 16;
        int fromHr = 10; //purposely set at 1030 so gc can still refresh jobs between 0900-1030
        int fromMin = 30;//purposely set at 1030 so gc can still refresh jobs between 0900-1030

        Date tweakedDateFrom = new Date();
        Date tweakedDateCurr = new Date();

        try {
            Calendar calendartweakedDateFrom = new GregorianCalendar(fromYr, fromMth - 1, fromDay, fromHr, fromMin, 0);
            tweakedDateFrom = calendartweakedDateFrom.getTime();

            //0 comes when two date are same,
            //1 comes when date1 is higher then date2
            //-1 comes when date1 is lower then date2
            //curr time is earlier than maintenance start datetime
            asd = tweakedDateCurr.compareTo(tweakedDateFrom) < 0;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods currTimeAllowJobsRefreshDuringMaintenanceDay. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public static Boolean currTimeIsBetweenFromDateToDateToIgnore() {
        /*
        changes to the dates here, rmb to change at currTimeAllowJobsRefreshDuringMaintenanceDay
        function is just above this function
        * */
        int fromYr = 2023;
        int fromMth = 6;
        int fromDay = 16;
        int fromHr = 9;
        int fromMin = 0;

        int toYr = 2023;
        int toMth = 6;
        int toDay = 16;
        int toHr = 23;
        int toMin = 59;

        Boolean asd = false;
        Date tweakedDateFrom = new Date();
        Date tweakedDateTo = new Date();
        Date tweakedDateCurr = new Date();
        try {
            Calendar calendartweakedDateFrom = new GregorianCalendar(fromYr, fromMth - 1, fromDay, fromHr, fromMin, 0);
            tweakedDateFrom = calendartweakedDateFrom.getTime();

            Calendar calendartweakedDateTo = new GregorianCalendar(toYr, toMth - 1, toDay, toHr, toMin, 0);
            tweakedDateTo = calendartweakedDateTo.getTime();

            //curr time is == or later than dateFrom, curr time is <= dateTo
            asd = tweakedDateCurr.compareTo(tweakedDateFrom) >= 0 && tweakedDateCurr.compareTo(tweakedDateTo) <= 0 ? true : false;
            /*SharedPreferences sp = objGlobalMethods.getSystemRelatedSharedPreferences();
            objGlobalMethods.setGCMPrefs(sp, "IsMaintenanceHours" , asd ? "t" : "");*/
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods currTimeIsBetweenFromDateToDateToIgnore. Exception: " + e.getMessage(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean currTimeIsBetweenFromDateToDate(Date dateFrom, Date dateTo) {
        Boolean asd = false;
        Date currTime = new Date();
        Date tweakedDateFrom = dateFrom;
        Date tweakedDateTo = dateTo;
        Date tweakedDateCurr = new Date();
        try {
            Calendar calendartweakedDateFrom = Calendar.getInstance();
            calendartweakedDateFrom.setTime(tweakedDateFrom);
            calendartweakedDateFrom.set(Calendar.DAY_OF_YEAR, 0);
            calendartweakedDateFrom.set(Calendar.MONTH, 0);
            calendartweakedDateFrom.set(Calendar.YEAR, 0);
            calendartweakedDateFrom.add(Calendar.MINUTE, -30);
            tweakedDateFrom = calendartweakedDateFrom.getTime();

            Calendar calendartweakedDateTo = Calendar.getInstance();
            calendartweakedDateTo.setTime(tweakedDateTo);
            calendartweakedDateTo.set(Calendar.DAY_OF_YEAR, 0);
            calendartweakedDateTo.set(Calendar.MONTH, 0);
            calendartweakedDateTo.set(Calendar.YEAR, 0);
            tweakedDateTo = calendartweakedDateTo.getTime();

            Calendar calendartweakedDateCurr = Calendar.getInstance();
            calendartweakedDateCurr.setTime(tweakedDateCurr);
            calendartweakedDateCurr.set(Calendar.DAY_OF_YEAR, 0);
            calendartweakedDateCurr.set(Calendar.MONTH, 0);
            calendartweakedDateCurr.set(Calendar.YEAR, 0);
            tweakedDateCurr = calendartweakedDateCurr.getTime();

            //curr time is == or later than dateFrom, curr time is <= dateTo
            asd = tweakedDateCurr.compareTo(tweakedDateFrom) >= 0 && tweakedDateCurr.compareTo(tweakedDateTo) <= 0 ? true : false;
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static void clearStaticActivities() {
        GcmIntentService.currentActivity = null;
        FCMservice.currentActivity = null;
        //JobsHandler.currentActivity = null;
        //myApp.currentActivity = null;
    }

    public static Boolean StringAequalsStringB(String stringA, String stringB) {
        return stringA.toUpperCase().equals(stringB.toUpperCase());
    }

    public static boolean IsDefaultVRPpage() {
        boolean asd = false;
        try {
            SharedPreferences gcmp = getGcmPreferences();
            asd = getGCMPrefs(gcmp, "pagemode").equals("vrp");
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods IsDefaultVRPpage. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean isVRPbySettings() {
        boolean asd = false;
        try {
            SharedPreferences gcmp = getGcmPreferences();
            asd = getGCMPrefs(gcmp, "isVRPbySettings").equals("y");
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods isVRPbySettings. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean isAppSpecifcFolder(String filepath, String foldername) {
        boolean asd = false;
        try {
            String dirPath = myApp.getAppContext().getFilesDir().getAbsolutePath() + File.separator + foldername; //app specific - same location per preferenes & sqlite, cannot find via normal file system
            File f = new File(filepath);
            if (f != null && f.exists()) {
                String s = f.getParent();
                asd = IsEqualString(dirPath, s);
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods isAppSpecifcFolder. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static void deleteAllFilesFromAppSpecificFolderMoreThanXdaysZerofied(String folderName, int Xdays, boolean inclTdy) {
        try {
            boolean asd;
            File f;
            int c = 0;
            if (IsEmptyString(folderName)) {
                folderName = myApp.getAppContext().getString(R.string.appspecificfilefolder_image);
            }
            String dirPath = myApp.getAppContext().getFilesDir().getAbsolutePath() + File.separator + folderName; //app specific - same location per preferenes & sqlite, cannot find via normal file system
            File folder = !IsEmptyString(dirPath) ? new File(dirPath) : null;
            if (folder != null && folder.isDirectory()) {
                String[] children = folder.list();
                if (children != null && children.length > 0) {
                    for (int i = 0; i < children.length; i++) {
                        f = new File(folder, children[i]);
                        asd = JobsHandler.moreThanXdaysZerofied(f.lastModified(), 0, false, true); //Xdays == 0 in order not to amend lastModified.
                        if (asd) {
                            f.delete();
                            c++;
                        }
                    }
                    objGlobalMethods.writeToFile(String.valueOf(c) + " file(s) deleted from app specific folder " + folderName.toUpperCase(), "", "", "", "");
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            prepareSendEmail(new String[]{"it@xdel.com"}, "Unable to delete files in Images folder.",
                    "deleteAllFilesFromAppSpecificFolderMoreThanXdaysZerofied Unable to delete files in Images folder.\nStack:\n" + s,
                    "", false, "Unable to delete files in Images folder.");
            writeToFile("Exception at objGlobalMethods deleteAllFilesFromAppSpecificFolderMoreThanXdaysZerofied. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static void deleteAllUnusedFiles(String folderName) {
        try {
            AlbumStorageDirFactory mAlbumStorageDirFactory = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
            } else {
                mAlbumStorageDirFactory = new BaseAlbumDirFactory();
            }
            boolean asd;
            File f;
            int c = 0;
            if (IsEmptyString(folderName)) {
                folderName = myApp.getAppContext().getString(R.string.appspecificfilefolder_image);
            }
            //String dirPath = myApp.getAppContext().getFilesDir().getAbsolutePath() + File.separator + folderName; //app specific - same location per preferenes & sqlite, cannot find via normal file system
            File folder = mAlbumStorageDirFactory.getAlbumStorageDir(folderName);
            if (folder != null && folder.isDirectory()) {
                String[] children = folder.list();
                if (children != null && children.length > 0) {
                    for (int i = 0; i < children.length; i++) {
                        f = new File(folder, children[i]);
                        asd = JobsHandler.moreThanXdaysZerofied(f.lastModified(), 0, false, true); //Xdays == 0 in order not to amend lastModified.
                        if (asd) {
                            f.delete();
                            c++;
                        }
                    }
                    objGlobalMethods.writeToFile(String.valueOf(c) + " file(s) deleted from app specific folder " + folderName.toUpperCase(), "", "", "", "");
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            prepareSendEmail(new String[]{"it@xdel.com"}, "Unable to delete files in Images folder.",
                    "deleteAllUnusedFiles Unable to delete files in Images folder.\nStack:\n" + s,
                    "", false, "Unable to delete files in Images folder.");
            writeToFile("Exception at objGlobalMethods deleteAllUnusedFiles. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static void deleteFile(String path) {
        try {
            File att = new File(path);
            if (att != null)
                att.delete();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            writeToFile("Exception at objGlobalMethods deleteFile. Exception: " + s, "", "", "", "");
        }
    }

    public static objCallStatusHashmapList getCallStatusHashmapList(SharedPreferences gp) {
        objCallStatusHashmapList obj = null;
        String CallStatusHashmapListPrefStr = "";
        Gson gson = new Gson();
        try {
            CallStatusHashmapListPrefStr = objGlobalMethods.getGCMPrefs(gp, "CallStatusHashmapListPrefStr");
            if (!CallStatusHashmapListPrefStr.isEmpty() && !CallStatusHashmapListPrefStr.equals("") && CallStatusHashmapListPrefStr != "") {
                obj = gson.fromJson(CallStatusHashmapListPrefStr, new TypeToken<objCallStatusHashmapList>() {
                }.getType());
            } else {
                obj = new objCallStatusHashmapList();
            }
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods getCallStatusHashmapList. Exception: " + e.getMessage(), "", "", "", "");
            obj = null;
        } finally {
            return obj;
        }
    }

    public static void addDialedNumberToHashmap(String dialedNum, String status) {
        SharedPreferences gp = objGlobalMethods.getGcmPreferences();
        String CallStatusHashmapListPrefStr = objGlobalMethods.getGCMPrefs(gp, "CallStatusHashmapListPrefStr");
        objCallStatusHashmapList CallStatusHashmapList;
        HashMap<String, objCallStatus> calllist;
        objCallStatus cs = null;
        boolean containkey = false;
        Gson gson = new Gson();
        try {
            if (!CallStatusHashmapListPrefStr.isEmpty() && !CallStatusHashmapListPrefStr.equals("") && CallStatusHashmapListPrefStr != "") {
                CallStatusHashmapList = gson.fromJson(CallStatusHashmapListPrefStr, new TypeToken<objCallStatusHashmapList>() {
                }.getType());
            } else {
                CallStatusHashmapList = new objCallStatusHashmapList();
            }
            calllist = CallStatusHashmapList.getCalllist();
            containkey = calllist.containsKey(dialedNum);
            cs = !containkey ? new objCallStatus() : calllist.get(dialedNum);
            cs.addStatus(status);
            if (containkey) {
                cs.setTimeStart();
            }
            CallStatusHashmapList.addDialedNumber(dialedNum, cs);
            CallStatusHashmapListPrefStr = gson.toJson(CallStatusHashmapList);
            objGlobalMethods.setGCMPrefs(gp, "CallStatusHashmapListPrefStr", CallStatusHashmapListPrefStr);
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods addDialedNumberToHashmap. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static void removedDialedNumberToHashmap(String dialedNumber) {
        try {
            SharedPreferences gp = objGlobalMethods.getGcmPreferences();
            String CallStatusHashmapListPrefStr = objGlobalMethods.getGCMPrefs(gp, "CallStatusHashmapListPrefStr");
            objCallStatusHashmapList CallStatusHashmapList;
            Gson gson = new Gson();
            if (!CallStatusHashmapListPrefStr.isEmpty() && !CallStatusHashmapListPrefStr.equals("") && CallStatusHashmapListPrefStr != "") {
                CallStatusHashmapList = gson.fromJson(CallStatusHashmapListPrefStr, new TypeToken<objCallStatusHashmapList>() {
                }.getType());
            } else {
                CallStatusHashmapList = new objCallStatusHashmapList();
            }
            CallStatusHashmapList.removeDialedNumber(dialedNumber);
            CallStatusHashmapListPrefStr = gson.toJson(CallStatusHashmapList);
            objGlobalMethods.setGCMPrefs(gp, "CallStatusHashmapListPrefStr", CallStatusHashmapListPrefStr);
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods clearDialedNumberToHashmap. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static void clearDialedNumberToHashmap() {
        try {
            SharedPreferences gp = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(gp, "CallStatusHashmapListPrefStr", "");
        } catch (Exception e) {
            writeToFile("Exception at objGlobalMethods clearDialedNumberToHashmap. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static boolean isTestUserID() {
        boolean asd = false;
        String useridx = "";
        try {

            useridx = objGlobalMethods.getGCMPrefs(getGcmPreferences(), "IDX");
            asd = (!objGlobalMethods.IsEmptyString(useridx) &&
                    (
                            /*objGlobalMethods.IsEqualString(useridx, "1294") ||
                                    objGlobalMethods.IsEqualString(useridx, "1767") ||
                                    objGlobalMethods.IsEqualString(useridx, "2084") ||
                                    objGlobalMethods.IsEqualString(useridx, "304")*/
                            /*objGlobalMethods.IsEqualString(useridx, "1294") ||
                                    objGlobalMethods.IsEqualString(useridx, "2084") ||
                                    objGlobalMethods.IsEqualString(useridx, "304")*/
                            objGlobalMethods.IsEqualString(useridx, "2084") ||
                                    objGlobalMethods.IsEqualString(useridx, "304")
                    )
            );
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods isTestUserID. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public static String b64ToString(String b64) {
        String v = "";
        try {
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(b64);
            if (decodedBytes != null)
                v = new String(decodedBytes);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods b64ToString. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return v;
        }
    }

    public static class deleteLogFilesAsync extends AsyncTask<Void, Void, Integer> {
        public deleteLogFilesAsync() {

        }

        @Override
        protected Integer doInBackground(Void... params) {
            Integer c = 0;
            Calendar SevenDaysAgoCalendar = Calendar.getInstance();
            Calendar OneDayAgoCalendar;
            Date tdyZerofied = objGlobalMethods.getZeroTimeDate(new Date());

            Calendar TwentySecondMarchTwentyEighteenCalendar = Calendar.getInstance();
            String Filenamedtstr = "";
            File myFile = null;
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
                File externalStorageDir = Environment.getExternalStorageDirectory();

                /*//code to delete log for 22032018 due over-logging, lagging the app, log file size over 20mb
                TwentySecondMarchTwentyEighteenCalendar.set(2018, 2, 22);
                Filenamedtstr = formatter.format(TwentySecondMarchTwentyEighteenCalendar.getTime());
                myFile = new File(externalStorageDir, Filenamedtstr + "_log.txt");
                if (myFile != null && myFile.exists()) {
                    myFile.delete();
                }
                //code to delete log for 22032018 due over-logging, lagging the app, log file size over 20mb*/

                for (int i = 10; i <= 1000; i++) { //changed from 7days to 10days
                    SevenDaysAgoCalendar = Calendar.getInstance();
                    SevenDaysAgoCalendar.add(Calendar.DAY_OF_YEAR, -i);
                    Filenamedtstr = formatter.format(SevenDaysAgoCalendar.getTime());
                    myFile = new File(externalStorageDir, Filenamedtstr + "_log.txt");
                    if (myFile != null && myFile.exists()) {
                        myFile.delete();
                        c++;
                    }
                }

                ////delete images from app specific Images folder
                //deleteAllFilesFromAppSpecificFolderMoreThanXdaysZerofied(myApp.getAppContext().getString(R.string.appspecificfilefolder_image), 1, false);
                deleteAllUnusedFiles(myApp.getAppContext().getString(R.string.appspecificfilefolder_image));


            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                writeToFile("Exception at objGlobalMethods deleteLogFilesAsync doInBackground. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            } finally {
                return c;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Integer c) {
            try {
                if (c > 0) {
                    writeToFile(String.valueOf(c) + " log files deleted.", "", "", "", "");
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                writeToFile("Exception at objGlobalMethods deleteLogFilesAsync onPostExecute. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    public static void resetCred() {
        try {
            SharedPreferences vrpsp = getVrpRelatedSharedPreferences();
            //objGlobalMethods.setVrpRelatedSP_KeyValue2(vrpsp, "credEditedDateLong", "1754462264929");
            String credEditedDateLong = getVrpRelatedSP_KeyValue2(vrpsp, "credEditedDateLong");
            long credEditedDateLongValue = !IsEmptyString(credEditedDateLong) ? Long.parseLong(credEditedDateLong) : 0;
            if (credEditedDateLongValue == 0)
                credEditedDateLongValue = new Date().getTime();
            objGlobalMethods.writeToFile("credEditedDateLong = " + credEditedDateLong,"","","","");
            if (credEditedDateLongValue == 0 || (credEditedDateLongValue > 0 && JobsHandler.moreThanXdaysZerofied(credEditedDateLongValue, 0, false, true))) {
                String[] vrpspprefName = new String[]{"credEditedDateLong", "creditAM", "creditPM", "creditAOH"};
                String[] vrpspprefvalue = new String[]{"", "", "", ""};
                objGlobalMethods.setVrpSPMass(vrpsp, vrpspprefName, vrpspprefvalue);

                objGlobalMethods.writeToFile("Credits info reset","","","","");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods resetCred. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void setcredEditedDateLong(SharedPreferences vrpsp) {
        String v = "";
        Date now = new Date();
        try {
            if (vrpsp == null)
                vrpsp = getVrpRelatedSharedPreferences();
            String credEditedDateLong = getVrpRelatedSP_KeyValue2(vrpsp, "credEditedDateLong");
            if (!IsEmptyString(credEditedDateLong)) {
                Date ceDate = new Date();
                ceDate.setTime(Long.parseLong(credEditedDateLong));
                if (!JobsHandler2.isToday(ceDate))
                    v = String.valueOf(ceDate.getTime());
            } else
                v = String.valueOf(now.getTime());

            if (!IsEmptyString(v))
                objGlobalMethods.setVrpRelatedSP_KeyValue2(vrpsp, "credEditedDateLong", v);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            writeToFile("Exception at objGlobalMethods setcredEditedDateLong. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

}
