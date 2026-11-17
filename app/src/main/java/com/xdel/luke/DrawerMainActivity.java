package com.xdel.luke;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.data.BleDevice;
import com.tzone.bluetooth.data.BleScanState;
import com.tzone.devices.BaseDevice;
import com.xdel.luke.BCnSVC.Download_Data_BroadcastReceiver;
import com.xdel.luke.BCnSVC.ScreenOnOffReceiver;
import com.xdel.luke.BCnSVC.SendQueuedEmail_Receiver;
import com.xdel.luke.Caging.CageScanActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.HRM.ProfileActivity;
import com.xdel.luke.HandTakeOver.HandoverActivity;
import com.xdel.luke.HandTakeOver.TakeoverActivity;
import com.xdel.luke.JobInventoryVerify.JobInventoryVAdapter;
import com.xdel.luke.JobInventoryVerify.JobInventoryVerifyActivity;
import com.xdel.luke.Locker.L2_LA_GetUserResult;
import com.xdel.luke.PieceByPiecePU.PipPcs;
import com.xdel.luke.PieceByPiecePU.StartQueryActivity;
import com.xdel.luke.Receivers.RecevierMgr;
import com.xdel.luke.Rooting.RootGuard;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.Voip.VoipApp;
import com.xdel.luke.ble.BleClass;
import com.xdel.luke.inventory.InventoryMainActivity;
import com.xdel.luke.messagingClasses.ChatMsgSenders;
import com.xdel.luke.messagingClasses.GetAllMsg_Receiver;
import com.xdel.luke.mpd.MPDMainActivity;
import com.xdel.luke.mpd.mpdclass;
import com.xdel.luke.objClasses.LocaleHelper;
import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.NFCTag;
import com.xdel.luke.objClasses.RouteLocation;
import com.xdel.luke.objClasses.WifiBroadcastReceiver;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objAuthDriverResult;
import com.xdel.luke.objClasses.objCabinet;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objMPD_Info;
import com.xdel.luke.objClasses.objRecipientType;
import com.xdel.luke.pdfviewer.pdfclass;
import com.xdel.luke.ppReader.ui.ppMainActivity;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjCategoryCount;
import com.xdel.luke.sqlite.dbObjJobs;
import com.xdel.luke.sqlite.dbObjTradeInList;
import com.xdel.luke.sqlite.objInsertJobInfo;
import com.xdel.luke.sqlite.objJobsInsertionInfo;
import com.xdel.luke.templogger.MacDevicePair;
import com.xdel.luke.templogger.TempLoggerRegActivity;
import com.xdel.luke.templogger.btReportGeneratorIntentService;
import com.xdel.luke.templogger.temploggerclass;
import com.xdel.luke.test.bluetoothActivity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

public class DrawerMainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    //=========================================dashboard code from dashboard2=================================================

    private final Semaphore mutex = new Semaphore(1);
    private Context context;
    ConnectionDetector cd;
    private MenuItem menuItem;
    private String LoginName = "";
    private String LoginPassword = "";
    private String regid;
    private String cHashstr;
    private int catType;
    private int categoryType = -1;
    private String categoryTitle = "";
    private ProgressDialog pd, pdCheck, pdCOD, pdRouting;
    static boolean errored = false;
    private String[] val;
    private ArrayList DriverJobsArrayList;
    private AlertDialog ad;
    public static Handler handler = new Handler();
    private ListView listView;
    public static boolean DashboardOpenFromPush = false;
    private static Boolean isRefresh = false;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs, MedsColSP;
    private int queCount = 0;
    private boolean DeAuthDriverResult;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    public static boolean updateJobInfoStatusTrueFalse = true;
    public static boolean JobsCategoryHasherrored = false;
    public static boolean GetDriverJobsALerrored = false;
    public static boolean alarmStarted = false;
    public static boolean alarmStarted2 = false;
    static PendingIntent pendingIntent, pendingIntent2;
    static AlarmManager manager, manager2;
    private File attachment;
    private EditText txtDashboard2Input;
    private MediaPlayer player;
    static String cameraScannedCode;
    private Boolean refreshQueBooleanString = false;
    private ProgressDialog pdSearch;
    private ProgressDialog pdRecalRoute;
    private LinearLayout Dashboard2UnreadMsgLinearLayout;
    private LinearLayout Dashboard2ListviewLinearLayout;
    private LinearLayout LLsettings;
    String langType = "en";
    private static String showRouteList = "";
    private static Double totalCOD;
    private static Boolean isDunning = false;
    String SENDER_ID = "17277152085";
    public NfcAdapter mNfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    Boolean isFromLocationNFCscan = false;
    private SwipeRefreshLayout swiperefresh;
    ScreenShotContentObserver screenShotContentObserver;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private Dashboard_PostItemAdapter adapter;
    private NavigationView navigationView;
    private ImageView imgProfilePix;
    FloatingActionButton fabprobation;
    private boolean lite;

    private static String getAppVersionName() {
        try {
            Context context = myApp.getAppContext();
            String versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);

        }
    }

    public void initMenu() {
        try {
            navigationView = (NavigationView) findViewById(R.id.navigation_view);
            View header = navigationView.getHeaderView(0);
            LinearLayout LLStatus01 = (LinearLayout) header.findViewById(R.id.LLStatus01);
            LinearLayout LLStatus02 = (LinearLayout) header.findViewById(R.id.LLStatus02);
            LinearLayout LLStatus03 = (LinearLayout) header.findViewById(R.id.LLStatus03);
            LinearLayout LLStatus04 = (LinearLayout) header.findViewById(R.id.LLStatus04);
            LinearLayout LLStatus05 = (LinearLayout) header.findViewById(R.id.LLStatus05);
            LinearLayout LLStatus06 = (LinearLayout) header.findViewById(R.id.LLStatus06);
            LinearLayout LLStatus07 = (LinearLayout) header.findViewById(R.id.LLStatus07);

            LinearLayout LLCOD, LLCC, LLMsg;

            if (Boolean.parseBoolean(ishomesuser) || lite) {
                LLStatus01.setVisibility(View.GONE);
                LLStatus02.setVisibility(View.GONE);
                LLStatus03.setVisibility(View.GONE);
                LLStatus04.setVisibility(View.GONE);
                LLStatus05.setVisibility(Boolean.parseBoolean(ishomesuser) ? View.GONE : View.VISIBLE);
                LLStatus06.setVisibility(View.GONE);
                LLStatus07.setVisibility(View.GONE);

                if (LLStatus05 != null && LLStatus05.getVisibility() == View.VISIBLE) {
                    LLCOD = LLStatus05.findViewById(R.id.LLCOD);
                    LLCC = LLStatus05.findViewById(R.id.LLCC);
                    LLMsg = LLStatus05.findViewById(R.id.LLMsg);
                    LLCOD.setVisibility(View.GONE);
                    LLCC.setVisibility(View.GONE);
                    LLMsg.setVisibility(View.VISIBLE);
                }
            } else {
                LLStatus01.setVisibility(View.GONE);
                LLStatus02.setVisibility(View.GONE);
                LLStatus03.setVisibility(View.VISIBLE);
                LLStatus04.setVisibility(View.VISIBLE);
                LLStatus05.setVisibility(View.VISIBLE);
                LLStatus06.setVisibility(View.VISIBLE);
                LLStatus07.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity initMenu. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void menu_btn_click(View v) {
        try {
            if (v != null) {
                Intent i;

                if (v.getId() == R.id.LLbtnbulkpu) {
                    drawerLayout.closeDrawers();
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    BulkPickUpActivity.scannedItems = null;
                    useridx = "D" + useridx;
                    String SCANNEDCURRENTID = useridx.toUpperCase() + "|" + username.toUpperCase();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "BulkPickUpScannedIDnUsername", SCANNEDCURRENTID);
                    i = new Intent(myApp.currentActivity, BulkPickUpActivity.class);
                    handler.removeCallbacks(runnable);
                    startActivity(i);
                    finish();
                } else if (v.getId() == R.id.LLbtnjobsheet) {
                    drawerLayout.closeDrawers();
                    if (Boolean.parseBoolean(ishomesuser)) {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String[] prefName = new String[]{"jobsheetSubmitscannedItemsPrefStr", "jsmode"};
                        String[] prefvalue = new String[]{"", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                        i = new Intent(myApp.currentActivity, JobsheetActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        JobsheetSubmitActivity.scannedItems = null;
                        useridx = "D" + useridx;
                        String SCANNEDCURRENTID2 = useridx.toUpperCase() + "|" + username.toUpperCase();

                        String[] prefName = new String[]{"JobsheetScannedIDnUsername", "JobsheetScannedUsername", "jobsheetSubmitscannedItemsPrefStr", "jsmode"};
                        String[] prefvalue = new String[]{SCANNEDCURRENTID2, username.toUpperCase(), "", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                        i = new Intent(myApp.currentActivity, JobsheetSubmitActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else if (v.getId() == R.id.LLbtncageout) {
                    drawerLayout.closeDrawers();
                    i = new Intent(myApp.currentActivity, CageScanActivity.class);
                    startActivity(i);
                    finish();
                } else if (v.getId() == R.id.LLbtnshuttle) {
                    drawerLayout.closeDrawers();
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    JobsheetSubmitActivity.scannedItems = null;
                    useridx = "D" + useridx;
                    String SCANNEDCURRENTID2 = useridx.toUpperCase() + "|" + username.toUpperCase();

                    String[] prefNameShuttle = new String[]{"JobsheetScannedIDnUsername", "JobsheetScannedUsername", "jobsheetSubmitscannedItemsPrefStr", "jsmode"};
                    String[] prefvalueShuttle = new String[]{SCANNEDCURRENTID2, username.toUpperCase(), "", "shuttle"};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefNameShuttle, prefvalueShuttle);

                    i = new Intent(myApp.currentActivity, JobsheetSubmitActivity.class);
                    startActivity(i);
                    finish();
                } else if (v.getId() == R.id.LLbtnhandover) {
                    drawerLayout.closeDrawers();
                    HandoverActivity.scannedItems = null;
                    String HOScannedIDnUsername = useridx;
                    HandoverActivity.hotoqrlist = null;
                    String[] prefName3 = new String[]{"HOScannedIDnUsername", "verifyTOQR"};
                    String[] prefvalue3 = new String[]{HOScannedIDnUsername, ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName3, prefvalue3);

                    i = new Intent(myApp.currentActivity, HandoverActivity.class);
                    startActivity(i);
                    finish();
                } else if (v.getId() == R.id.LLbtntakeover) {
                    drawerLayout.closeDrawers();
                    TakeoverActivity.scannedItems = null;
                    String TOScannedIDnUsername = useridx.toUpperCase();
                    String[] prefName4 = new String[]{"TOScannedIDnUsername", "scanTOqr", "TOQRimgB64Str", "to_json"};
                    String[] prefvalue4 = new String[]{TOScannedIDnUsername, "", "", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName4, prefvalue4);

                    i = new Intent(myApp.currentActivity, TakeoverActivity.class);
                    startActivity(i);
                    finish();
                } else if (v.getId() == R.id.LLbtncod) {
                    drawerLayout.closeDrawers();
                    getCODList getCODListtask = new getCODList();
                    getCODListtask.execute();
                } else if (v.getId() == R.id.LLbtnmsg) {
                    drawerLayout.closeDrawers();
                    i = new Intent(myApp.currentActivity, Messaginglist.class);
                    handler.removeCallbacks(runnable);
                    List<ChatMsgSenders> chatmsgsendersAL = null;
                    chatmsgsendersAL = DAO_Jobs.getChatMsgSendersList();
                    if (chatmsgsendersAL != null && chatmsgsendersAL.size() > 0) {
                        startActivity(i);
                        finish();
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "unreadMsgExist", "false");
                        showDialog(myApp.getAppContext().getString(R.string.alert_nomsg));
                    }
                } else if (v.getId() == R.id.LLbtncreditcardimport) {
                    String citidbshsbc = "0";
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    drawerLayout.closeDrawers();
                    CreditCardActivity.scannedItems = null;
                    i = new Intent(myApp.currentActivity, CreditCardActivity.class);
                    handler.removeCallbacks(runnable);
                    citidbshsbc = "0";
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "citidbshsbc", citidbshsbc);
                    startActivity(i);
                    finish();
                } else if (v.getId() == R.id.LLbtnmpd) {
                    drawerLayout.closeDrawers();
                    i = new Intent(myApp.currentActivity, MPD_CheckOutActivity.class);
                    startActivity(i);
                    finish();
                } else if (v.getId() == R.id.LLbtnKoolar) {
                    drawerLayout.closeDrawers();
                    i = new Intent(myApp.currentActivity, MPDMainActivity.class);
                    startActivity(i);
                    finish();
                } else if (v.getId() == R.id.LLbtnInvTally) {
                    drawerLayout.closeDrawers();
                    i = new Intent(myApp.currentActivity, InventoryMainActivity.class);
                    startActivity(i);
                    finish();
                } else if (v.getId() == R.id.LLbtnBTL) {
                    drawerLayout.closeDrawers();
                    i = new Intent(myApp.currentActivity, TempLoggerRegActivity.class);
                    startActivity(i);
                    finish();
                } else if (v.getId() == R.id.LLbtnBPU2) {
                    drawerLayout.closeDrawers();
                    i = new Intent(myApp.currentActivity, StartQueryActivity.class);
                    startActivity(i);
                    finish();
                } else if (v.getId() == R.id.txtLogout) {
                    LogOutNew();
                }

                /*switch (v.getId()) {
                    case R.id.LLbtnbulkpu:
                        drawerLayout.closeDrawers();
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        BulkPickUpActivity.scannedItems = null;
                        useridx = "D" + useridx;
                        String SCANNEDCURRENTID = useridx.toUpperCase() + "|" + username.toUpperCase();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "BulkPickUpScannedIDnUsername", SCANNEDCURRENTID);
                        i = new Intent(myApp.currentActivity, BulkPickUpActivity.class);
                        handler.removeCallbacks(runnable);
                        startActivity(i);
                        finish();
                        break;
                    case R.id.LLbtnjobsheet:
                        drawerLayout.closeDrawers();
                        if (Boolean.parseBoolean(ishomesuser)) {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            String[] prefName = new String[]{"jobsheetSubmitscannedItemsPrefStr", "jsmode"};
                            String[] prefvalue = new String[]{"", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                            i = new Intent(myApp.currentActivity, JobsheetActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            JobsheetSubmitActivity.scannedItems = null;
                            useridx = "D" + useridx;
                            String SCANNEDCURRENTID2 = useridx.toUpperCase() + "|" + username.toUpperCase();

                            String[] prefName = new String[]{"JobsheetScannedIDnUsername", "JobsheetScannedUsername", "jobsheetSubmitscannedItemsPrefStr", "jsmode"};
                            String[] prefvalue = new String[]{SCANNEDCURRENTID2, username.toUpperCase(), "", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                            i = new Intent(myApp.currentActivity, JobsheetSubmitActivity.class);
                            startActivity(i);
                            finish();
                        }
                        break;
                    case R.id.LLbtncageout:
                        drawerLayout.closeDrawers();
                        i = new Intent(myApp.currentActivity, CageScanActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    case R.id.LLbtnshuttle:
                        drawerLayout.closeDrawers();
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        JobsheetSubmitActivity.scannedItems = null;
                        useridx = "D" + useridx;
                        String SCANNEDCURRENTID2 = useridx.toUpperCase() + "|" + username.toUpperCase();

                        String[] prefNameShuttle = new String[]{"JobsheetScannedIDnUsername", "JobsheetScannedUsername", "jobsheetSubmitscannedItemsPrefStr", "jsmode"};
                        String[] prefvalueShuttle = new String[]{SCANNEDCURRENTID2, username.toUpperCase(), "", "shuttle"};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefNameShuttle, prefvalueShuttle);

                        i = new Intent(myApp.currentActivity, JobsheetSubmitActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    case R.id.LLbtnhandover:
                        drawerLayout.closeDrawers();
                        HandoverActivity.scannedItems = null;
                        String HOScannedIDnUsername = useridx;
                        HandoverActivity.hotoqrlist = null;
                        String[] prefName3 = new String[]{"HOScannedIDnUsername", "verifyTOQR"};
                        String[] prefvalue3 = new String[]{HOScannedIDnUsername, ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName3, prefvalue3);

                        i = new Intent(myApp.currentActivity, HandoverActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    case R.id.LLbtntakeover:
                        drawerLayout.closeDrawers();
                        TakeoverActivity.scannedItems = null;
                        String TOScannedIDnUsername = useridx.toUpperCase();
                        String[] prefName4 = new String[]{"TOScannedIDnUsername", "scanTOqr", "TOQRimgB64Str", "to_json"};
                        String[] prefvalue4 = new String[]{TOScannedIDnUsername, "", "", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName4, prefvalue4);

                        i = new Intent(myApp.currentActivity, TakeoverActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    case R.id.LLbtncod:
                        drawerLayout.closeDrawers();
                        getCODList getCODListtask = new getCODList();
                        getCODListtask.execute();
                        break;
                    case R.id.LLbtnmsg:
                        drawerLayout.closeDrawers();
                        i = new Intent(myApp.currentActivity, Messaginglist.class);
                        handler.removeCallbacks(runnable);
                        List<ChatMsgSenders> chatmsgsendersAL = null;
                        chatmsgsendersAL = DAO_Jobs.getChatMsgSendersList();
                        if (chatmsgsendersAL != null && chatmsgsendersAL.size() > 0) {
                            startActivity(i);
                            finish();
                        } else {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "unreadMsgExist", "false");
                            showDialog(myApp.getAppContext().getString(R.string.alert_nomsg));
                        }
                        break;
                    case R.id.LLbtncreditcardimport:
                        String citidbshsbc = "0";
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        drawerLayout.closeDrawers();
                        CreditCardActivity.scannedItems = null;
                        i = new Intent(myApp.currentActivity, CreditCardActivity.class);
                        handler.removeCallbacks(runnable);
                        citidbshsbc = "0";
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "citidbshsbc", citidbshsbc);
                        startActivity(i);
                        finish();
                        break;
                    case R.id.LLbtnmpd:
                        drawerLayout.closeDrawers();
                        i = new Intent(myApp.currentActivity, MPD_CheckOutActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    case R.id.LLbtnKoolar:
                        drawerLayout.closeDrawers();
                        i = new Intent(myApp.currentActivity, MPDMainActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    case R.id.LLbtnInvTally:
                        drawerLayout.closeDrawers();
                        i = new Intent(myApp.currentActivity, InventoryMainActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    case R.id.LLbtnBTL:
                        drawerLayout.closeDrawers();
                        i = new Intent(myApp.currentActivity, TempLoggerRegActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    case R.id.LLbtnBPU2:
                        drawerLayout.closeDrawers();
                        i = new Intent(myApp.currentActivity, StartQueryActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    case R.id.txtLogout:
                        LogOutNew();
                        break;
                }*/
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity menu_btn_click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void initNavigationDrawerNew() {
        try {
            navigationView = (NavigationView) findViewById(R.id.navigation_view);
            GcmPreferences = objGlobalMethods.getGcmPreferences();

            String GcName = "";
            byte[] GcImg = null;
            String GcID = "";
            if (Boolean.parseBoolean(ishomesuser)) {
                GcName = username;
            } else {
                objAuthDriverResult res = objGlobalMethods.getGCdetails(GcmPreferences);
                GcName = res.getFullName();
                GcImg = res.getImage();
                GcID = res.getID();
            }

            View header = navigationView.getHeaderView(0);
            TextView couriername = (TextView) header.findViewById(R.id.couriername);
            couriername.setText(GcName);

            TextView lukeversion = (TextView) header.findViewById(R.id.lukeversion);
            lukeversion.setText("Version " + getAppVersionName());

            imgProfilePix = (ImageView) header.findViewById(R.id.imgProfilePix);
            if (imgProfilePix != null) {
                if (GcImg != null) {
                    imgProfilePix.setImageBitmap(BitmapFactory.decodeByteArray(GcImg, 0, GcImg.length));
                }
                imgProfilePix.setOnClickListener(imgProfilePixOnClick());
            }

            if (lite)
                imgProfilePix.setOnClickListener(null);

            drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
            actionBarDrawerToggle = new ActionBarDrawerToggle(myApp.currentActivity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

                @Override
                public void onDrawerClosed(View v) {
                    super.onDrawerClosed(v);
                }

                @Override
                public void onDrawerOpened(View v) {
                    super.onDrawerOpened(v);
                }
            };
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity initNavigationDrawerNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void setMenuText() {
        try {
            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
            updateDrawerJSCount(navigationView);
            updateDrawerCODNew(navigationView);
            setMsgCountNew(navigationView);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity setMenuText. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void updateDrawerCODNew(NavigationView navigationView) {
        View header = null;
        try {
            if (navigationView == null) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
            }
            header = navigationView.getHeaderView(0);
            TextView txtCODbadge = (TextView) header.findViewById(R.id.txtCODbadge);

            Double codval = 0.0;
            String codvalPrefStr = "";
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            codvalPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalCOD");
            try {
                if (codvalPrefStr != null && !codvalPrefStr.equals("") && codvalPrefStr != "") {
                    codval = Double.parseDouble(codvalPrefStr);
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity updateDrawerCODNew codval conversion. Exception: " + e.getMessage().toString(), "", "", "", "");
                codval = 0.0;
            }
            DecimalFormat dec = new DecimalFormat("#0.00");

            txtCODbadge.setText("$" + dec.format(codval));
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity updateDrawerCODNew. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    public void setMsgCountNew(NavigationView navigationView) {
        View header = null;
        try {
            if (navigationView == null) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
            }
            header = navigationView.getHeaderView(0);
            TextView txtMSGbadge = (TextView) header.findViewById(R.id.txtMSGbadge);
            int unreadMsgCount = DAO_Jobs.getUnreadMessageCount();
            txtMSGbadge.setText(String.valueOf(unreadMsgCount));
            txtMSGbadge.setVisibility(unreadMsgCount > 0 ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity setMsgCountNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void updateDrawerJSCount(NavigationView navigationView) {
        View header = null;
        try {
            if (navigationView == null) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
            }
            header = navigationView.getHeaderView(0);
            TextView txtJSbadge = (TextView) header.findViewById(R.id.txtJSbadge);
            String count = "0";
            count = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.JobsheetCountName);
            txtJSbadge.setText(count);
            txtJSbadge.setVisibility(!objGlobalMethods.IsEmptyString(count) && !count.equals("0") ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity updateDrawerJSCount. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void initDashboardMenu(SharedPreferences gcmp) {
        Context ctx = myApp.getAppContext();
        if (!Boolean.parseBoolean(ishomesuser)) {
            showRouteList = objGlobalMethods.getGCMPrefs(gcmp, "showRouteList");
            showRouteList = "true";
            objGlobalMethods.setGCMPrefs(gcmp, "showRouteList", "true");
            if (lite) {
                /*val = new String[]{
                        ctx.getResources().getString(R.string.title_activity_allMap),
                        ctx.getResources().getString(R.string.item_dashboard2DIP),
                        ctx.getResources().getString(R.string.item_dashboard2PD)};*/
                val = new String[]{
                        ctx.getString(R.string.title_activity_allMap),
                        ctx.getString(R.string.item_dashboard2DIP),
                        ctx.getString(R.string.item_dashboard2PD)};
            } else if (showRouteList != null && !showRouteList.equals("")) {
                /*val = new String[]{
                        ctx.getResources().getString(R.string.title_activity_allMap),
                        ctx.getResources().getString(R.string.item_routing),
                        ctx.getResources().getString(R.string.item_dashboard2PIP),
                        ctx.getResources().getString(R.string.item_dashboard2PU),
                        ctx.getResources().getString(R.string.item_dashboard2DIP),
                        ctx.getResources().getString(R.string.item_dashboard2OKP),
                        ctx.getResources().getString(R.string.item_dashboard2FP),
                        ctx.getResources().getString(R.string.item_dashboard2FD),
                        ctx.getResources().getString(R.string.item_dashboard2PD),
                        ctx.getResources().getString(R.string.item_dashboard2TT)};*/
                val = new String[]{
                        ctx.getString(R.string.title_activity_allMap),
                        ctx.getString(R.string.item_routing),
                        ctx.getString(R.string.item_dashboard2PIP),
                        ctx.getString(R.string.item_dashboard2PU),
                        ctx.getString(R.string.item_dashboard2DIP),
                        ctx.getString(R.string.item_dashboard2OKP),
                        ctx.getString(R.string.item_dashboard2FP),
                        ctx.getString(R.string.item_dashboard2FD),
                        ctx.getString(R.string.item_dashboard2PD),
                        ctx.getString(R.string.item_dashboard2TT)};

            } else {
                /*val = new String[]{
                        ctx.getResources().getString(R.string.item_dashboard2PIP),
                        ctx.getResources().getString(R.string.item_dashboard2PU),
                        ctx.getResources().getString(R.string.item_dashboard2DIP),
                        ctx.getResources().getString(R.string.item_dashboard2OKP),
                        ctx.getResources().getString(R.string.item_dashboard2FP),
                        ctx.getResources().getString(R.string.item_dashboard2FD),
                        ctx.getResources().getString(R.string.item_dashboard2PD),
                        ctx.getResources().getString(R.string.item_dashboard2TT)};*/
                val = new String[]{
                        ctx.getString(R.string.item_dashboard2PIP),
                        ctx.getString(R.string.item_dashboard2PU),
                        ctx.getString(R.string.item_dashboard2DIP),
                        ctx.getString(R.string.item_dashboard2OKP),
                        ctx.getString(R.string.item_dashboard2FP),
                        ctx.getString(R.string.item_dashboard2FD),
                        ctx.getString(R.string.item_dashboard2PD),
                        ctx.getString(R.string.item_dashboard2TT)};
            }
        } else {
            if (
                    objGlobalMethods.IsEqualString(useridx, "1") || objGlobalMethods.IsEqualString(useridx, "425") ||
                    objGlobalMethods.IsEqualString(useridx, "396") || objGlobalMethods.IsEqualString(useridx, "315") ||
                    objGlobalMethods.IsEqualString(useridx, "446") || objGlobalMethods.IsEqualString(useridx, "304") ||
                    objGlobalMethods.IsEqualString(useridx, "489")
            ) {
            /*if (objGlobalMethods.IsEqualString(useridx, "1") || objGlobalMethods.IsEqualString(useridx, "425") ||
                    objGlobalMethods.IsEqualString(useridx, "315") || objGlobalMethods.IsEqualString(useridx, "446")) {*/
                //1 andrew 425 xiaver 315 cedric 446 constance 304 kean

                /*val = new String[]{
                        ctx.getResources().getString(R.string.item_dashboard2JOBSHEEThomes),
                        ctx.getResources().getString(R.string.item_dashboard2SHUTTLE),
                        ctx.getResources().getString(R.string.item_dashboard2STATUS),
                        ctx.getResources().getString(R.string.item_dashboard2CageInhomes),
                        ctx.getResources().getString(R.string.item_dashboard2Koolar),
                        ctx.getResources().getString(R.string.tally_shipment),
                        ctx.getResources().getString(R.string.item_jobinventoryverification),
                        ctx.getResources().getString(R.string.item_dashboard2_safekeep)
                };*/
                val = new String[]{
                        ctx.getString(R.string.item_dashboard2JOBSHEEThomes),
                        ctx.getString(R.string.item_dashboard2SHUTTLE),
                        ctx.getString(R.string.item_dashboard2STATUS),
                        ctx.getString(R.string.item_dashboard2CageInhomes),
                        ctx.getString(R.string.item_dashboard2Koolar),
                        ctx.getString(R.string.tally_shipment),
                        ctx.getString(R.string.item_jobinventoryverification),
                        ctx.getString(R.string.item_dashboard2_safekeep)
                };
            } else {
                /*val = new String[]{
                        ctx.getResources().getString(R.string.item_dashboard2JOBSHEEThomes),
                        ctx.getResources().getString(R.string.item_dashboard2SHUTTLE),
                        ctx.getResources().getString(R.string.item_dashboard2STATUS),
                        ctx.getResources().getString(R.string.item_dashboard2CageInhomes),
                        ctx.getResources().getString(R.string.item_dashboard2Koolar),
                        ctx.getResources().getString(R.string.tally_shipment),
                        ctx.getResources().getString(R.string.item_jobinventoryverification)
                };*/

                val = new String[]{
                        ctx.getString(R.string.item_dashboard2JOBSHEEThomes),
                        ctx.getString(R.string.item_dashboard2SHUTTLE),
                        ctx.getString(R.string.item_dashboard2STATUS),
                        ctx.getString(R.string.item_dashboard2CageInhomes),
                        ctx.getString(R.string.item_dashboard2Koolar),
                        ctx.getString(R.string.tally_shipment),
                        ctx.getString(R.string.item_jobinventoryverification)
                };
            }
        }

    }

    private void BleDisconnectAnyConnectedDevices() {
        try {
            /*SharedPreferences g = objGlobalMethods.getGcmPreferences();
            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            String mdpsJsonStr =  objGlobalMethods.getGCMPrefs(g, "mdpsJsonStr");
            if (!objGlobalMethods.IsEmptyString(mdpsJsonStr) && !mdpsJsonStr.equals("[]")) {
                List<MacDevicePair> mdps = new Gson().fromJson(mdpsJsonStr, new TypeToken<List<MacDevicePair>>() {
                }.getType());

                List<BleDevice> bds = BleManager.getInstance().getAllConnectedDevice();
                BleDevice bd = null;
                if (bds != null) {
                    for (int i = 0; i <= bds.size() -1; i++) {
                        bd = bds.get(i);

                    }
                }

            }*/
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
                BleManager.getInstance().cancelScan();
                objGlobalMethods.writeToFile("DrawerMainActivity BleDisconnectAnyConnectedDevices BleManager.getInstance().cancelScan()", "", "", "", "");
            }
            BleManager.getInstance().disconnectAllDevice();
            BleManager.getInstance().destroy();
            objGlobalMethods.writeToFile("DrawerMainActivity BleManager.getInstance().destroy()", "", "", "", "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity BleDisconnectAnyConnectedDevices. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void initBleManager() {
        try {
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
                BleManager.getInstance().cancelScan();
                objGlobalMethods.writeToFile("DrawerMainActivity initBleManager BleManager.getInstance().cancelScan()", "", "", "", "");
            }
            BleManager.getInstance().disconnectAllDevice();
            BleManager.getInstance().destroy();
            //objGlobalMethods.writeToFile("DrawerMainActivity initBleManager BleManager.getInstance().destroy()","","","","");

            BleManager.getInstance().init(getApplication());
            BleManager.getInstance()
                    .enableLog(true)
                    .setReConnectCount(3, 5000)
                    .setConnectOverTime(20000)
                    .setOperateTimeout(5000);
            objGlobalMethods.writeToFile("BleManager initiated at DrawerMainActivity", "", "", "", "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity initBleManager. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");

        /*String[] s = new String[]{};
        String x = s[1];*/
        try {
            /*String languageToLoad = langType; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());*/
            myApp.getInstance().initAppLanguage(getBaseContext());
            myApp.getInstance().initAppLanguage(myApp.getAppContext());

            //disable taking screen shot
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            //disable taking screen shot

            //setContentView(R.layout.activity_drawer_main);
            setContentView(R.layout.activity_drawer_main_new);

            toolbar = (Toolbar) findViewById(R.id.toolbar);

            //progress dialog for upon login to load items and
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));

            pdSearch = new ProgressDialog(this);
            pdSearch.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdSearch.setCanceledOnTouchOutside(false);
            pdSearch.setMessage(myApp.getAppContext().getString(R.string.pd_waitSearch));

            listView = (ListView) findViewById(R.id.listView);
            pdCheck = new ProgressDialog(this);
            pdCheck.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdCheck.setCanceledOnTouchOutside(false);
            pdCheck.setMessage(myApp.getAppContext().getString(R.string.pd_waitCheck));

            pdCOD = new ProgressDialog(this);
            pdCOD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdCOD.setCanceledOnTouchOutside(false);
            pdCOD.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfoCOD));

            pdRouting = new ProgressDialog(this);
            pdRouting.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdRouting.setCanceledOnTouchOutside(false);
            pdRouting.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveRouting));

            pdRecalRoute = new ProgressDialog(this);
            pdRecalRoute.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdRecalRoute.setCanceledOnTouchOutside(false);
            pdRecalRoute.setMessage(myApp.getAppContext().getString(R.string.pdrecalroute));

            txtDashboard2Input = (EditText) findViewById(R.id.txtDashboard2Input);
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

            fabprobation = (FloatingActionButton) findViewById(R.id.fabprobation);

            //RecevierMgr.unregScreenOnOffReceiverNew();
            RecevierMgr.regScreenOnOffReceiverNew();
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DrawerMainAcitivty onCreate. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private String getDeviceInfo() {
        String s = "";
        try {
            s += "Manufacturer: " + Build.MANUFACTURER.toUpperCase() + "\n";
            s += "Brand: " + Build.BRAND.toUpperCase() + "\n";
            s += "Model: " + Build.MODEL.toUpperCase() + "\n";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ContextCompat.checkSelfPermission(myApp.getAppContext(), android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                s += "Serial: " + Build.getSerial();
            } else {
                s += "Serial: " + Build.SERIAL;
            }

            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                s += "\nIMEI: " + tm.getDeviceId();
            }
        } catch (Exception e) {
            String x = e.getMessage();
        } finally {
            return s;
        }
    }

    private AlertDialog EntePasswordalertDialog = null;

    private void ViewSettingsPrompt() {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setTitle(myApp.getAppContext().getString(R.string.enterpassword));
            final View settingsView = getLayoutInflater().inflate(R.layout.custom_settings_pwd, null);
            builder.setView(settingsView);
            Button btnCancel = (Button) settingsView.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                if (getCurrentFocus() != null) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }
                            }
                        }, 100);

                        EntePasswordalertDialog.dismiss();
                    } catch (Exception e) {
                        String s = e.getMessage().toString();
                    }
                }
            });

            Button btnVerify = (Button) settingsView.findViewById(R.id.btnVerify);
            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EditText txtSettingsPwd = (EditText) settingsView.findViewById(R.id.txtSettingsPwd);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                if (getCurrentFocus() != null) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }
                            }
                        }, 100);
                        if (!txtSettingsPwd.getText().toString().isEmpty() && !txtSettingsPwd.getText().toString().equals("") && txtSettingsPwd.getText().toString() != "") {
                            if (objGlobalMethods.verifySettingsPwd(txtSettingsPwd.getText().toString())) {
                                EntePasswordalertDialog.dismiss();
                                Intent i = new Intent(myApp.currentActivity, SettingsActivity.class);
                                startActivity(i);
                                drawerLayout.closeDrawers();
                            } else {
                                Context ctx = myApp.getAppContext();
                                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.btn_ok), ctx.getString(R.string.alert_incorrectpwd)).show();
                            }
                        } else {
                            Context ctx = myApp.getAppContext();
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.btn_ok), ctx.getString(R.string.enterpassword)).show();
                        }
                    } catch (Exception e) {
                        String s = e.getMessage().toString();
                    }
                }
            });

            builder.setCancelable(true);
            EntePasswordalertDialog = builder.create();
            EntePasswordalertDialog.show();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity ViewSettingsPrompt. Exception: " + s, "", "", "", "");
        }
    }

    private View.OnClickListener imgProfilePixOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout != null) {
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(myApp.currentActivity, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        //R6: R5 may have redirected this screen inside super.onResume(); nothing below may run on a finishing activity
        if (isFinishing()) {
            return;
        }
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                MedsColSP = objGlobalMethods.getMedsColSP();
                if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }

                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;
                isDunning = false;
                CODupdate.al = null;
                CODupdate.total = 0.0;
                HandoverActivity.hotoqrlist = null;
                langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");

                context = myApp.getAppContext();

                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];
                lite = JobsHandler2.isLuke_Reduced(GcmPreferences);
                Common_BroadcastReceiver.count = 0;
                setTitle(myApp.getAppContext().getString(R.string.title_activity_dashboard2));
                cd = new ConnectionDetector(null);

                if (VoipApp.observer != null) {
                    try {
                        if (JobInfoActivity.app != null) {
                            JobInfoActivity.app.deinit();
                        } else {
                            VoipApp.observer = null;
                        }
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at DrawerMainActivity onResume JobInfoActivity.app.deinit(). Exception: " + e.getMessage(), "", "", "", "");
                    }
                }

                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                boolean proceedRefresh = false;
                if (IsMaintenanceHours)
                    proceedRefresh = objGlobalMethods.currTimeAllowJobsRefreshDuringMaintenanceDay();
                boolean isWalker = JobsHandler2.isWalker(GcmPreferences);
                boolean isStdRunner = JobsHandler2.isStdRunner(GcmPreferences);
                boolean isSatSun = false;
                boolean isAOHtime = false;

                if (!isWalker && isStdRunner) {
                    isSatSun = JobsHandler2.isSatSun();
                    isAOHtime = JobsHandler2.isAOHtime();
                }
                if (!IsMaintenanceHours &&
                        (JobsHandler.hasOpsQ() || objGlobalMethods.IsDefaultVRPpage() ||
                                objGlobalMethods.isVRPbySettings() || (!isWalker && isStdRunner && isSatSun) ||
                                (!isWalker && isStdRunner && !isSatSun && isAOHtime))) {
                    if (isStdRunner && isSatSun || (isStdRunner && !isSatSun && isAOHtime)) {
                        JobsHandler.redirectToVrpPage2();
                    } else {
                        JobsHandler.redirectToVrpPage();
                    }
                } else {
                    setSupportActionBar(toolbar);
                    //hideNavigationItems();
                    //initNavigationDrawer();
                    initMenu();
                    initNavigationDrawerNew();
                    initDashboardMenu(GcmPreferences);
                    changeActiveNotiLang(username);

                    regid = objGlobalMethods.getRegistrationId(context, GcmPreferences);

                    categoryType = -1;
                    String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, "fromSubMenu", "MapViewedPostal",
                            "DigiAgreementcreateViewTime", "ListPriorityOnly", "prevfirstQlocation",
                            "InvMainMode", "InvSubMode", "InvTallyMode", "InventoryBatchIDStr", "InvTallyQtyCanUndo", "Batch_ID", "StockCodeMode",
                            "showMPD_Dialog_IsShowing", "showRegBTTL_Dialog_IsShowing", "FailAsND", "FailAsND_Reason"};
                    String[] prefvalue = new String[]{"-1", "", "",
                            "", "t", "",
                            "", "", "", "", "", "", "",
                            "", "", "", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    /*String[] mpn = new String[]{"MPDGroupIDX", "MPD_GroupStr", "MPD_GetGroup_ObjStr", "MPDScanMode", "MPDMainMode"};
                    String[] mpv = new String[]{"", "", "", "", ""};
                    objGlobalMethods.setMpdSPMass(mpn, mpv);

                    String[] ppn = new String[]{"PPMainMode"};
                    String[] ppv = new String[]{""};
                    objGlobalMethods.setPassportSPMass(null, ppn, ppv);

                    String[] medpn = new String[]{"mChildCheckStates2PrefStr", "SQGroupScrollSavedIndex", "SQGroupScrollSavedPosition", "scanned"};
                    String[] medpv = new String[]{"", "", "", ""};
                    objGlobalMethods.setMedsColPrefsMass(MedsColSP, medpn, medpv);*/

                    JobsHandler3.clearVrpSP();
                    JobsHandler3.clearShpmtVSP(null);
                    JobsHandler3.clearMedsColSP(MedsColSP);
                    JobsHandler3.clearPassportSP(null);
                    JobsHandler3.clearMpdSP();

                    isRefresh = false;

                    objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                    objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                    objGlobalMethods.resetJobsListGroupScrollIndexNpostion(GcmPreferences);
                    objGlobalMethods.offSpeaker();

                    SharedPreferences vrpsp = objGlobalMethods.getVrpRelatedSharedPreferences();
                    objGlobalMethods.setcredEditedDateLong(vrpsp);

                    if (username.equals("") && password.equals("")) {
                        BackToLoginPage2();
                    }

                    int appFlag = myApp.getDeviceSettingsFlag();
                    if (myApp.isAirplaneModeOnFlag(appFlag)) {
                        showChangeAirplaneModeSettingsDialog();
                        return;
                    }
                    if (myApp.isGpsOnFlag(appFlag)) {
                        showChangeGPSModeSettingsDialog();
                        return;
                    }

                    if (myApp.isAllPermissionGrantedFlag(appFlag)) {
                        showChangePermissionDialog();
                        return;
                    }

                    if (myApp.GPSisHighAccFlag(appFlag)) {
                        showChangeGPStoHighAccModeSettingsDialog();
                        return;
                    }

                    if (!temploggerclass.isBTEnabled()) {
                        objGlobalMethods.writeToFile("DrawerMainActivity !BTEnabled", "", "", "", "");
                        temploggerclass.promptToOnBT();
                        return;
                    }

                    try {
                        if (mNfcAdapter != null && myApp.isNfcEnabled(mNfcAdapter, context)) {
                            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                            IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
                            IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
                            IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};

                            PendingIntent nfcPendingIntent = PendingIntent.getActivity(myApp.getAppContext(), 0, new Intent(myApp.currentActivity, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                            mNfcAdapter.enableForegroundDispatch(myApp.currentActivity, nfcPendingIntent, nfcIntentFilter, null);
                        }
                    } catch (Exception e) {
                        //String stack = JobsHandler.getStackTraceString(e);
                        //objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity onResume mNfcAdapter portion. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
                    }

                    objGlobalMethods.resetAllImageRelatedStaticVariables(false);
                    JobsHandler.clearAllStaticFromPageAndNextActivityFields();
                    objGlobalMethods.clearOnResumeDrawerMainActivityGcmPrefs(GcmPreferences);
                    objGlobalMethods.clearCageScanActivityGcmPrefs(GcmPreferences);
                    JobsHandler.clearCheckListActivityStatic(GcmPreferences, true);
                    JobsHandler.clearDunningActivityPrefs(GcmPreferences);
                    JobsHandler.clearCallActivityPrefs(GcmPreferences);
                    JobsHandler.clearJobsRetrievalActivityPrefs(GcmPreferences);
                    JobsHandler.clearJobsListGroupPrefs(GcmPreferences);
                    JobsHandler.clearLeaveApplicationActivityPrefs(GcmPreferences);
                    JobsHandler.clearChangePwdActivityPrefs(GcmPreferences);
                    JobsHandler.clearlAllDetailPagesStatic();
                    JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "", "", true);
                    fabprobation.setOnClickListener(null);
                    if (JobsHandler2.isprobationGC(GcmPreferences)) {
                        fabprobation.show();
                        fabprobation.setOnClickListener(fabprobation_OnClickListener());
                    } else {
                        fabprobation.hide();
                    }

                    if (!isFromLocationNFCscan) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "NFCLocationtagPrefVal", "");
                        Jobs_Retrieval_Activity.fromPage = null;
                    }
                    DashboardSubActivity.frompage = null;
                    JobsHandler.checkForceLogOut();

                    boolean idledMoreThan3Hrs = JobsHandler.idledMoreThan3Hrs_new();
                    //idledMoreThan3Hrs = true;
                    if (idledMoreThan3Hrs) {
                        objGlobalMethods.writeToFile("Idled more than 3 hours. Manual force logout.", "", "", "", "");
                        JobsHandler.ManualForceLogout();
                    }

                    if (cd.isConnectingToInternet()) {
                        objGlobalMethods.requireGcReLogin();
                    }

                    objGlobalMethods.checkGPSnPerMinRunAlarmRunning(ishomesuser);
                    DAO_Jobs.initdbHelper();

                    if (Boolean.parseBoolean(ishomesuser)) {
                        if (!cd.isConnectingToInternet()) {
                            //Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, context.getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                            if (DashboardOpenFromPush) {
                                if (FCMservice.mNotificationManager != null) {
                                    FCMservice.mNotificationManager.cancel(2);
                                }
                                FCMservice.notiNotRead = false;
                                DashboardOpenFromPush = false;
                            }
                        }

                        SharedPreferences jiv = objGlobalMethods.getJobInventoryVSP();
                        objGlobalMethods.setJobInventoryVSP(jiv, "JobInventoryDetailJson", "");
                        JobInventoryVerifyActivity.ScannedAItems = null;

                        /*if (getStoredStatusCodes(context, "en").equals("") || getStoredStatusCodes(context, "zh").equals("")) {
                            downloadStatusCodes();
                        }*/

                        objGlobalMethods.setGCMPrefs(GcmPreferences, "pagemode", "dashboard");
                        loadHomesUserList();
                        sendBroadcast(new Intent(context, BackendRun_BroadcastReceiver.class));
                    } else {
                        if (objGlobalMethods.IsDefaultVRPpage() && !IsMaintenanceHours) {
                            Intent i = new Intent(context, DrawerMain2Activity.class);
                            myApp.currentActivity.startActivity(i);
                            finish();
                            return;
                        }

                        if (!cd.isConnectingToInternet()) {
                            Toast.makeText(context, context.getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                            if (DashboardOpenFromPush) {
                                if (FCMservice.mNotificationManager != null) {
                                    FCMservice.mNotificationManager.cancel(2);
                                }
                                FCMservice.notiNotRead = false;
                                DashboardOpenFromPush = false;
                            }

                            if (showRouteList != null && !showRouteList.equals("")) {
                                loadListFirst2SQLite();
                            } else {
                                loadListFirst();
                            }

                            //JobsHandler.doubleCheckUpdatePIPSQLite(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx, false);
                            JobsHandler.doubleCheckUpdatePIPSQLiteNew(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                        } else {
                            if (getStoredStatusCodes(context, "en").equals("") || getStoredStatusCodes(context, "zh").equals("")) {
                                downloadStatusCodes();
                            }

                            if (objGlobalMethods.getStoredRecipientTypes(GcmPreferences).equals("")) {
                                JobsHandler.downloadRecipientTypesWS task = new JobsHandler.downloadRecipientTypesWS(regid, GcmPreferences);
                                task.execute();
                            }

                            try {
                                if (objGlobalMethods.getGCMPrefs(GcmPreferences, "refreshQueBooleanString").toString().equals("")) {
                                    refreshQueBooleanString = false;
                                } else {
                                    refreshQueBooleanString = Boolean.parseBoolean(objGlobalMethods.getGCMPrefs(GcmPreferences, "refreshQueBooleanString"));
                                }

                            } catch (Exception e) {
                                refreshQueBooleanString = false;
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "refreshQueBooleanString", "false");
                            }

                            if (DashboardOpenFromPush) {
                                if (FCMservice.mNotificationManager != null) {
                                    FCMservice.mNotificationManager.cancel(2);
                                }
                                FCMservice.notiNotRead = false;
                                if (refreshQueBooleanString) {
                                    refresh();
                                } else if (showRouteList != null && !showRouteList.equals("")) {
                                    loadListFirst2SQLite();
                                } else {
                                    loadListFirst();
                                }

                                //check for any new job ye to be updated to PIP
                                //JobsHandler.doubleCheckUpdatePIPSQLite(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx, false);
                                JobsHandler.doubleCheckUpdatePIPSQLiteNew(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                                DashboardOpenFromPush = false;
                            } else if (MainActivity.uponLogin == 1) {
                                if (!Boolean.parseBoolean(ishomesuser)) {
                                    if ((IsMaintenanceHours && proceedRefresh) || !IsMaintenanceHours) {
                                        sendBroadcast(new Intent(context, WifiBroadcastReceiver.class));
                                        objGlobalMethods.writeToFile("DrawerMainActivity MainActivity.uponLogin == 1. calling GetDriverJobsCountSQLite.", "", "", "", "");
                                        if (regid.equals("")) {
                                            regid = objGlobalMethods.getRegistrationId(null, GcmPreferences);
                                        }
                                        GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                                        task.execute();
                                        temploggerclass.decideToStartTempTimer(null, "DrawerMainActivity");
                                        //double check for any new job yet to be updated to PIP is done at the end of GetDriverJobsCount2
                                    } else {
                                        loadListFirst2SQLite();
                                    }
                                } else {
                                    if (showRouteList != null && !showRouteList.equals("")) {
                                        //loadListFirst2();
                                        loadListFirst2SQLite();
                                    } else {
                                        loadListFirst();
                                    }
                                }
                                //double check for any new job yet to be updated to PIP is done at the end of GetDriverJobsCount2*/
                            } else if (!updateJobInfoStatusTrueFalse) {
                                if (!Boolean.parseBoolean(ishomesuser)) {
                                    if ((IsMaintenanceHours && proceedRefresh) || !IsMaintenanceHours) {
                                        objGlobalMethods.writeToFile("DrawerMainActivity updateJobInfoStatusTrueFalse is false. calling GetDriverJobsCountSQLite.", "", "", "", "");
                                        updateJobInfoStatusTrueFalse = true;
                                        if (regid.equals("")) {
                                            regid = objGlobalMethods.getRegistrationId(null, GcmPreferences);
                                        }
                                        GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                                        task.execute();
                                        //double check for any new job yet to be updated to PIP is done at the end of GetDriverJobsCount2
                                    } else {
                                        loadListFirst2SQLite();
                                    }
                                }
                            } else {
                                if (refreshQueBooleanString) {
                                    refresh();
                                } else {
                                    if (showRouteList != null && !showRouteList.equals("")) {
                                        loadListFirst2SQLite();
                                    } else {
                                        loadListFirst();
                                    }
                                }
                                //check for any new job yet to be updated to PIP
                                //JobsHandler.doubleCheckUpdatePIPSQLite(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx, false);
                                JobsHandler.doubleCheckUpdatePIPSQLiteNew(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                            }
                            queCount = JobsHandler.returnNumberOfQueToSend(UpdateJobInfoPreferences);
                            if (queCount > 0) {
                                sendBroadcast(new Intent(context, BackendRun_BroadcastReceiver.class));
                            }
                            MainActivity.uponLogin = 0;
                        }
                    }

                    Dashboard2UnreadMsgLinearLayout = (LinearLayout) findViewById(R.id.Dashboard2UnreadMsgLinearLayout);
                    Dashboard2ListviewLinearLayout = (LinearLayout) findViewById(R.id.Dashboard2ListviewLinearLayout);
                    objGlobalMethods.getMsgReminder(GcmPreferences, "DrawerMainActivity", Dashboard2UnreadMsgLinearLayout, Dashboard2ListviewLinearLayout);

                    //objGlobalMethods.showDialog(myApp.currentActivity, "ok", JobsHandler2.isSatSun() ? "true" : "false").show();

                    txtDashboard2Input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                            cameraScannedCode = null;
                            if (!txtDashboard2Input.getText().toString().equals("")) {
                                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                                    return false;
                                if (actionId == 0) {
                                    search(txtDashboard2Input.getText().toString());

                                    if (getCurrentFocus() != null) {
                                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                    }

                                } else if (actionId == EditorInfo.IME_ACTION_GO) {
                                    search(txtDashboard2Input.getText().toString());

                                    if (getCurrentFocus() != null) {
                                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                    }
                                }

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        listView.clearFocus();
                                        txtDashboard2Input.clearFocus();
                                        txtDashboard2Input.setText("");
                                        txtDashboard2Input.requestFocus();
                                    }
                                }, 100);
                            }
                            return true;
                        }
                    });

                    String requestIntentName = objGlobalMethods.getGCMPrefs(GcmPreferences, "QRScanRequestIntent");
                    if (!requestIntentName.equals("") && !requestIntentName.equals("dashboard")) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                    }

                    cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
                    if (!cameraScannedCode.equals("")) {
                        String searchedval = "";
                        searchedval = cameraScannedCode;
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        search(searchedval);
                    }

                    listView.clearFocus();
                    txtDashboard2Input.clearFocus();
                    txtDashboard2Input.setText("");
                    txtDashboard2Input.requestFocus();
                    objGlobalMethods.checkToPromptNotiForBattCharging();
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !objGlobalMethods.isActiveServiceRunning()) {
                        objGlobalMethods.startActiveForegroundSvc("");
                    }

                    int qe = JobsHandler.numOfQueuedEmails();
                    if (qe > 0) {
                        objGlobalMethods.writeToFile("SendQueuedEmailReceiver called at DrawerMainActivity.", "", "", "", "");
                        Intent SendQueuedEmailReceiver = new Intent(context, SendQueuedEmail_Receiver.class);
                        sendBroadcast(SendQueuedEmailReceiver);
                    }
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawer_main, menu);

        if (Boolean.parseBoolean(ishomesuser)) {
            menu.findItem(R.id.refresh).setVisible(false);
            menu.findItem(R.id.vrp).setVisible(false);
        }
        /*else if (lite) {
            menu.findItem(R.id.vrp).setVisible(false);
        }*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        cd = new ConnectionDetector(null);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        boolean currTimeAllowJobsRefreshDuringMaintenanceDay = false;
        if (IsMaintenanceHours)
            currTimeAllowJobsRefreshDuringMaintenanceDay = objGlobalMethods.currTimeAllowJobsRefreshDuringMaintenanceDay();

        if (item.getItemId() == R.id.vrp) {
            if (!IsMaintenanceHours) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                //objGlobalMethods.setGCMPrefs(GcmPreferences, "pagemode", "vrp");
                objGlobalMethods.writeToFile("Switched to VRP Mode.", "", "", "", "");

                String[] prefName = new String[]{"pagemode", "refreshQueBooleanString"};
                String[] prefvalue = new String[]{"vrp", "true"};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                Intent i = new Intent(myApp.currentActivity, DrawerMain2Activity.class);

                startActivity(i);
                finish();
            } else {
                Context ctx = myApp.getAppContext();
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.unable_change_mode_maintenance)).show();
            }

                /*Intent i = new Intent(this, bluetoothActivity.class);
                startActivity(i);*/
        } else if (item.getItemId() == R.id.refresh) {
            if (cd.isConnectingToInternet()) {
                boolean proceed = false;
                proceed = ((IsMaintenanceHours && currTimeAllowJobsRefreshDuringMaintenanceDay) || !IsMaintenanceHours);

                if (proceed) {
                    isRefresh = true;
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                    if (regid.equals("")) {
                        regid = objGlobalMethods.getRegistrationIdOnly();
                    }
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                    username = UsernamePwd[0];
                    password = UsernamePwd[1];
                    useridx = UsernamePwd[2];
                    ishomesuser = UsernamePwd[3];
                    GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                    task.execute();
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.unable_refresh_maintenance));
                }

                //JobsHandler2.UpdateUnreadMsgAftJobUpdate();
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_cannotRefreshNoInternet));
            }
        } else if (item.getItemId() == R.id.qrscan) {
            launchQRScan();
        } else if (item.getItemId() == R.id.dashboard2Language) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
            if (langType.equals("en") || langType.equals("")) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "zh");
                LocaleHelper.setSelectedLanguageId("zh");
            } else if (langType.equals("zh")) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "en");
                LocaleHelper.setSelectedLanguageId("en");
            }

            Boolean langChange = true;
            String langChangeStr = String.valueOf(langChange);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "langChangeStr", langChangeStr);
            this.recreate();
        }

        /*switch (item.getItemId()) {
            case R.id.vrp:
                if (!IsMaintenanceHours) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, "pagemode", "vrp");
                    objGlobalMethods.writeToFile("Switched to VRP Mode.", "", "", "", "");

                    String[] prefName = new String[]{"pagemode", "refreshQueBooleanString"};
                    String[] prefvalue = new String[]{"vrp", "true"};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    Intent i = new Intent(myApp.currentActivity, DrawerMain2Activity.class);

                    startActivity(i);
                    finish();
                } else {
                    Context ctx = myApp.getAppContext();
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.unable_change_mode_maintenance)).show();
                }

                *//*Intent i = new Intent(this, bluetoothActivity.class);
                startActivity(i);*//*
                break;
            case R.id.refresh:
                if (cd.isConnectingToInternet()) {
                    boolean proceed = false;
                    proceed = ((IsMaintenanceHours && currTimeAllowJobsRefreshDuringMaintenanceDay) || !IsMaintenanceHours);

                    if (proceed) {
                        isRefresh = true;
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                        if (regid.equals("")) {
                            regid = objGlobalMethods.getRegistrationIdOnly();
                        }
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                        username = UsernamePwd[0];
                        password = UsernamePwd[1];
                        useridx = UsernamePwd[2];
                        ishomesuser = UsernamePwd[3];
                        GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                        task.execute();
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.unable_refresh_maintenance));
                    }

                    //JobsHandler2.UpdateUnreadMsgAftJobUpdate();
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_cannotRefreshNoInternet));
                }
                break;
            case R.id.qrscan:
                launchQRScan();
                break;
            case R.id.dashboard2Language:
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
                if (langType.equals("en") || langType.equals("")) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "zh");
                    LocaleHelper.setSelectedLanguageId("zh");
                } else if (langType.equals("zh")) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "en");
                    LocaleHelper.setSelectedLanguageId("en");
                }

                Boolean langChange = true;
                String langChangeStr = String.valueOf(langChange);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "langChangeStr", langChangeStr);
                this.recreate();
        }*/

        return super.onOptionsItemSelected(item);
    }

    //=========================================dashboard code from dashboard2=================================================

    private void changeActiveNotiLang(String name) {
        String langChangeStr = "false";
        Boolean langChange = false;
        try {
            langChangeStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "langChangeStr");
            try {
                langChange = Boolean.parseBoolean(langChangeStr);
            } catch (Exception e) {
                langChange = false;
            }

            if (langChange) {
                langChange = false;
                langChangeStr = String.valueOf(langChange);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "langChangeStr", langChangeStr);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    objGlobalMethods.startActiveForegroundSvc("");
                } else {
                    if (FCMservice.mNotificationManager != null) {
                        FCMservice.mNotificationManager.cancel(-1);
                        MainActivity.showActiveNotification(name);
                    }
                }
            } else {
                //do nothing
            }
        } catch (Exception e) {
            langChange = false;
            langChangeStr = String.valueOf(langChange);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "langChangeStr", langChangeStr);
        }
    }

    public void Dashboard2UnreadMsgtextClick(View view) {
        Intent parentIntent = new Intent(myApp.currentActivity, Messaginglist.class);
        startActivity(parentIntent);
        this.finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean hasAutoFocus = false;
        PackageManager pm = getPackageManager();

        hasAutoFocus = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);

        if (hasAutoFocus) {
            int action = event.getAction();
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (action == KeyEvent.ACTION_DOWN) {
                        //TODO
                        launchQRScan();
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (action == KeyEvent.ACTION_DOWN) {
                        //TODO
                        launchQRScan();
                    }
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void launchQRScan() {
        Intent parentIntent;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "dashboard");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");

        parentIntent = new Intent(myApp.currentActivity, QRBarcodeReader.class);
        handler.removeCallbacks(runnable);
        startActivity(parentIntent);
    }

    @Override
    protected void onPause() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            FCMservice.currentActivity = null;
            myApp.currentActivity = null;

            try {
                if (mNfcAdapter != null)
                    mNfcAdapter.disableForegroundDispatch(this);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at DrawerMainActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }

            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at DrawerMainActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
            }

        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pd.dismiss();
        pdCheck.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

        try {
            if (pd != null) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
            pd = null;

            if (pdSearch != null) {
                if (pdSearch.isShowing()) {
                    pdSearch.dismiss();
                }
            }
            pdSearch = null;

            if (pdCheck != null) {
                if (pdCheck.isShowing()) {
                    pdCheck.dismiss();
                }
            }
            pdCheck = null;

            if (pdCOD != null) {
                if (pdCOD.isShowing()) {
                    pdCOD.dismiss();
                }
            }
            pdCOD = null;

            if (pdRouting != null) {
                if (pdRouting.isShowing()) {
                    pdRouting.dismiss();
                }
            }
            pdRouting = null;

            if (pdRecalRoute != null) {
                if (pdRecalRoute.isShowing()) {
                    pdRecalRoute.dismiss();
                }
            }
            pdRecalRoute = null;

            DAO_Jobs.nullifystaticitems();

            isRefresh = null;
            if (Dashboard2UnreadMsgLinearLayout != null) {
                Dashboard2UnreadMsgLinearLayout.setOnClickListener(null);
                Dashboard2UnreadMsgLinearLayout = null;
            }

            Dashboard2ListviewLinearLayout = null;
            GcmPreferences = null;
            UpdateJobInfoPreferences = null;
            context = null;
            if (drawerLayout != null) {
                if (actionBarDrawerToggle != null) {
                    drawerLayout.removeDrawerListener(actionBarDrawerToggle);
                }
                drawerLayout = null;
            }
            toolbar = null;
            cHashstr = null;
            listView = null;
            UsernamePwd = null;
            categoryTitle = null;
            cd = null;
            mNfcAdapter = null;
            regid = null;
            if (txtDashboard2Input != null) {
                txtDashboard2Input.setOnEditorActionListener(null);
                txtDashboard2Input = null;
            }
            actionBarDrawerToggle = null;

            ////this portion will crash if change langauge and attempt to open up the drawer
            /*if (imgProfilePix != null) {
                imgProfilePix.setOnClickListener(null);
                Drawable img = imgProfilePix.getDrawable();
                imgProfilePix.setImageDrawable(null);
                if (img != null && img instanceof BitmapDrawable) {
                    BitmapDrawable bd = ((BitmapDrawable) img);
                    if (!bd.getBitmap().isRecycled()) {
                        bd.getBitmap().recycle();
                        bd = null;
                    }
                }
                imgProfilePix = null;
            }*/
            ////this portion will crash if change langauge and attempt to open up the drawer

            if (navigationView != null) {
                navigationView.setNavigationItemSelectedListener(null);
                navigationView = null;
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity onDestroy. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        //LogOutNew();
    }

    //Start showDialog
    public Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    public Dialog showChargeBattDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                Boolean asd = false;
                SharedPreferences gcmp = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                objGlobalMethods.setGCMPrefs(gcmp, "showBattChargeNotiPrefStr", asd.toString());
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    private void showChangeAirplaneModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_airplanemode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (android.os.Build.VERSION.SDK_INT < 17) {
                    try {
                        Intent intentAirplaneMode = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                        intentAirplaneMode.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentAirplaneMode);
                    } catch (ActivityNotFoundException e) {
                        String stack = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: "
                                + e.toString() + "\nStack: " + stack, "", "", "", "");
                    }
                } else {
                    Intent intent1 = new Intent("android.settings.WIRELESS_SETTINGS");
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPSModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawerMainActivity.this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPStoHighAccModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawerMainActivity.this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmodehighacc));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DrawerMainActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangePermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpspermission));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DrawerMainActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void refresh() {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (cd.isConnectingToInternet()) {
                isRefresh = true;
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                if (regid.equals("")) {
                    regid = objGlobalMethods.getRegistrationIdOnly();
                }
                GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                task.execute();
            } else {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "refreshQueBooleanString", "true");
            }


        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Dashboard2 refresh. " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void LogOutNew() {
        Context ctx = myApp.getAppContext();
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            showDialog(myApp.getAppContext().getString(R.string.alert_cannotLogoutNoInternet));
            // stop executing code by return
            return;
        }

        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
        ishomesuser = UsernamePwd[3];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        String logoutmsg = !Boolean.parseBoolean(ishomesuser) ? ctx.getString(R.string.logoutrestartphone) + "\n\n" + ctx.getString(R.string.alert_cfmlogout) : ctx.getString(R.string.alert_cfmlogout);
        builder.setMessage(logoutmsg);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if user pressed "yes",
                // 1. check that there is no more ques in the backend, if not, have to wait for all the ques to be cleared b4,
                // 2. then send back all the plotted locations,
                // 3. then he is allowed to exit from application
                pdCheck.show();
                dialog.dismiss();
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];

                stopRunnable();
                Context ctx = myApp.getAppContext();

                /*RecevierMgr rm = new RecevierMgr();
                rm.unregScreenOnOffReceiver();*/
                RecevierMgr.unregScreenOnOffReceiverNew();

                JobsHandler2.checkForBatchIDsDataToSendBack();

                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                queCount = JobsHandler.returnNumberOfQueToSend(UpdateJobInfoPreferences);

                if (queCount > 0) {
                    //unable to logout due there are still ques
                    pdCheck.dismiss();
                    objGlobalMethods.writeToFile("Unable to log out. There are still data waiting to be sent out. Please try again later.", "", "", "", "");
                    showDialog(myApp.getAppContext().getString(R.string.alert_cannotlogoutsendingdata));
                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                    return;
                } else if (queCount == 0) {
                    //no que, send back all plotted locations
                    //then logout
                    if (Boolean.parseBoolean(ishomesuser)) {
                        objGlobalMethods.writeToFile("Logging out HOMES USER.", "", "", "", "");
                        checkForAttachmentToEmailOut task = new checkForAttachmentToEmailOut();
                        task.execute();
                    } else {
                        objGlobalMethods.writeToFile("Logging out.", "", "", "", "");
                        checkForAttachmentToEmailOut task = new checkForAttachmentToEmailOut();
                        task.execute();
                    }
                }
            }
        });

        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private class checkForAttachmentToEmailOut extends AsyncTask<Void, Void, File> {
        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {

        }

        @Override
        protected File doInBackground(Void... params) {
            try {
                Date dt = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
                String Filenamedtstr = "";
                Filenamedtstr = formatter.format(dt);
                File externalStorageDir = Environment.getExternalStorageDirectory();
                attachment = new File(externalStorageDir, Filenamedtstr + "_log.txt");
            } catch (Exception e) {
                attachment = null;
            }
            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final File success) {
            String attachmentfilename = "";
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            ishomesuser = UsernamePwd[3];
            try {
                if (attachment.exists()) {
                    attachmentfilename = attachment.getAbsolutePath().toString();
                    if (!attachmentfilename.equals("")) {
                        Mail m = new Mail("luke@xdel.com", "XDel2015");
                        String versionname = objGlobalMethods.getAppVersionName(myApp.getAppContext());
                        objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, attachmentfilename, "", "Luke report from " + username
                                + ", Version: " + versionname, attachment);
                        sendmail.execute();
                    }
                }
            } catch (Exception e) {

            }

            if (Boolean.parseBoolean(ishomesuser)) {
                LogOutHomesWSCall();
            } else {
                LogOutWSCall task = new LogOutWSCall(username, password);
                task.execute();
            }
        }
    }

    private class LogOutWSCall extends AsyncTask<Void, Void, Boolean> {
        private final String LoginName;
        private final String Password;

        LogOutWSCall(String aLoginName, String aPassword) {
            LoginName = aLoginName;
            Password = aPassword;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            regid = objGlobalMethods.getRegistrationId(myApp.getAppContext(), GcmPreferences);
            DeAuthDriverResult = WebService.UnAuthDriver(regid, timestampstr, 0, 0);
            return DeAuthDriverResult;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean success) {
            try {
                pdCheck.dismiss();
                if (!success) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_cannotlogouttryagain)).show();
                } else {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                    username = UsernamePwd[0];
                    useridx = UsernamePwd[2];
                    /*objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.PROPERTY_REG_ID, "");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "prevLoginUserIDX", useridx);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "prevLoginUserName", username);*/

                    String[] pName = new String[]{objGlobalVariables.PROPERTY_REG_ID, "prevLoginUserIDX", "prevLoginUserName", "InventoryBatchIDListJsonStr"};
                    String[] pvalue = new String[]{
                            "", useridx, username, ""
                    };
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);

                    JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
                    Intent parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
                    startActivity(parentIntent);
                    JobsHandler.actFinish();
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity LogOutWSCall onPostExecute. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
            }
            errored = false;
        }
    }

    private void LogOutHomesWSCall() {
        Intent parentIntent;
        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        /*objGlobalMethods.setGCMPrefs(GcmPreferences, "totalJobsForTheDaytxt", "0");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "performancetxt", "0.0%");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "GetAllMsgCompleted", "");*/

        String[] prefName = new String[]{"totalJobsForTheDaytxt", "performancetxt", "wifistate", "leftofc", "GetAllMsgCompleted", "FullName", "Image", "ID", "MaskedID",
                "prevLoginUserName", "prevLoginUserIDX", "probationGC", "probationPeriod", "DriverFlag", "InventoryBatchIDListJsonStr",
                "pointMthToDate", "ptpercentage", "dist_travelled"};
        String[] prefvalue = new String[]{"0", "0.0%", "", "", "", "", "", "", "",
                "", "", "", "", "", "",
                "0.0", "0.0", "0.00"};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        JobsHandler.clearGPSQue(LocationPrefs);
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(2);
        }
        FCMservice.notiNotRead = false;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            objGlobalMethods.stopActiveForegroundSvc();
        } else {
            if (FCMservice.mNotificationManager != null) {
                FCMservice.mNotificationManager.cancel(-1);
            }
        }
        DAO_Jobs.dropLukeDB();
        startActivity(parentIntent);
        JobsHandler.actFinish();
    }

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    public void BackToLoginPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "dailypoints", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        JobsHandler.clearGPSQue(LocationPrefs);
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
        FCMservice.notiNotRead = false;

        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(2);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            objGlobalMethods.stopActiveForegroundSvc();
        } else {
            if (FCMservice.mNotificationManager != null) {
                FCMservice.mNotificationManager.cancel(-1);
            }
        }
        Intent parentIntent;
        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
        DAO_Jobs.dropLukeDB();
        startActivity(parentIntent);
        finish();
    }

    public void setCategoryHashes(Context context, String valName, String val, String RetrieveTypeNameStr, String JobIDXsJsonStr,
                                  String JobInfoJsonPrefName, String JobInfoJsonPrefJsonStr, String JobsCountName, String JobCount) {

        GcmPreferences = objGlobalMethods.getGcmPreferences();
        JobsHandler.setCategoryHashesForDashboard(GcmPreferences, valName, val, RetrieveTypeNameStr, JobIDXsJsonStr, JobInfoJsonPrefName, JobInfoJsonPrefJsonStr, JobsCountName, String.valueOf(JobCount));
    }

    private ArrayList getSystemJobsCountAL() {
        final SharedPreferences prefs = objGlobalMethods.getGcmPreferences();
        ArrayList al = new ArrayList();
        al.add(prefs.getString(objGlobalVariables.PIPjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.PUjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.DIPjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.DELjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.FPjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.FDjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.PDjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.JobsheetCountName, ""));
        return al;
    }

    private void loadListFirst() {
        Context ctx = myApp.getAppContext();
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            ArrayList systemJobsCountAL = getSystemJobsCountAL();
            ArrayList<Dashboard_PostData> dashboardPostDataArr = new ArrayList<Dashboard_PostData>();
            Dashboard_PostData postdata;
            DecimalFormat dec = new DecimalFormat("#.00");
            setMsgCountNew(null);

            for (int i = 0; i <= val.length - 1; i++) {
                if (i <= 5) {
                    if (i == 0) {
                        if (!Boolean.parseBoolean(ishomesuser)) {
                            if (DashboardOpenFromPush) {
                                postdata = new Dashboard_PostData(val[i], (String) systemJobsCountAL.get(i), true, true);
                                dashboardPostDataArr.add(postdata);
                            } else {
                                postdata = new Dashboard_PostData(val[i], (String) systemJobsCountAL.get(i), false, true);
                                dashboardPostDataArr.add(postdata);
                            }
                        } else {
                            postdata = new Dashboard_PostData(val[i], "0", false, true);
                            dashboardPostDataArr.add(postdata);
                        }
                    } else {
                        if (!Boolean.parseBoolean(ishomesuser)) {
                            postdata = new Dashboard_PostData(val[i], (String) systemJobsCountAL.get(i), false, true);
                            dashboardPostDataArr.add(postdata);
                        } else {
                            postdata = new Dashboard_PostData(val[i], "0", false, true);
                            dashboardPostDataArr.add(postdata);
                        }
                    }
                }
            }

            if (!Boolean.parseBoolean(ishomesuser)) {
                String totalJobsForTheDaytxt = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalJobsForTheDaytxt");
                String performancetxt = objGlobalMethods.getGCMPrefs(GcmPreferences, "performancetxt");
                String pt = ctx.getString(R.string.performancetxt01) + " " + totalJobsForTheDaytxt + "\n" + ctx.getString(R.string.performancetxt02) + " " + performancetxt;
                if (!objGlobalMethods.IsEmptyString(pt)) {
                    postdata = new Dashboard_PostData(pt, String.valueOf(""), false, false);
                    dashboardPostDataArr.add(0, postdata);
                }
            }

            adapter = new Dashboard_PostItemAdapter(myApp.getAppContext(), dashboardPostDataArr);

            listView.setAdapter(adapter);

            //updateDrawerCOD(); //shifted to setMsgCountNew

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i;
                    view.clearFocus();
                    View v;
                    TextView lblCount;
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                    username = UsernamePwd[0];
                    password = UsernamePwd[1];
                    useridx = UsernamePwd[2];
                    ishomesuser = UsernamePwd[3];

                    switch (position) {
                        case 0:
                            //do nothing
                            break;
                        case 1:
                            categoryType = 0;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2PIP);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2PIP));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoPIPALjsonPrefName);
                            }

                            break;
                        case 2:
                            categoryType = 1;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2PU);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2PU));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "1");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoPUALjsonPrefName);
                            }

                            break;
                        case 3:
                            categoryType = 2;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2DIP);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2DIP));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "2");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoDIPALjsonPrefName);
                            }

                            break;
                        case 4:
                            categoryType = 3;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2OKP);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2OKP));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "3");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoDELALjsonPrefName);
                            }

                            break;
                        case 5:
                            categoryType = 4;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2FP);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2FP));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "4");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoFPALjsonPrefName);
                            }

                            break;
                        case 6:
                            categoryType = 5;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2FD);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2FD));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "5");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoFDALjsonPrefName);
                            }

                            break;
                        case 7:
                            categoryType = 6;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2PD);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2PD));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "6");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoPDALjsonPrefName);
                            }

                            break;
                    }
                }
            });
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity loadListFirst. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
        } finally {

        }
    }

    private void loadHomesUserList() {
        Integer JobsheetQueCount = 0;
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        try {
            ArrayList<Dashboard_PostData> dashboardPostDataArr = new ArrayList<Dashboard_PostData>();
            Dashboard_PostData postdata;

            for (int i = 0; i <= val.length - 1; i++) {
                if (i == 0) {
                    //show JobSheet
                    JobsheetQueCount = JobsHandler.getJobsheetQueCount(UpdateJobInfoPreferences);
                    postdata = new Dashboard_PostData(val[i], String.valueOf(JobsheetQueCount), false, true);
                    postdata.setIsHomesUser(true);
                    dashboardPostDataArr.add(postdata);
                } else if (i == 1) {
                    //show Shuttle
                    postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                    postdata.setIsHomesUser(true);
                    dashboardPostDataArr.add(postdata);
                } else if (i == 2) {
                    //show check status
                    postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                    postdata.setIsHomesUser(true);
                    dashboardPostDataArr.add(postdata);
                } else if (i == 3) {
                    postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                    postdata.setIsHomesUser(true);
                    dashboardPostDataArr.add(postdata);
                } else {
                    postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                    postdata.setIsHomesUser(true);
                    dashboardPostDataArr.add(postdata);
                }
            }

            adapter = new Dashboard_PostItemAdapter(myApp.getAppContext(), dashboardPostDataArr);

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i;
                    view.clearFocus();
                    View v;
                    TextView lblCount;
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    switch (position) {
                        case 0:
                            //Jobsheet
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "jsmode", "");
                            i = new Intent(myApp.currentActivity, JobsheetActivity.class);
                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 1:
                            //Shuttle

                            String[] prefNameShuttle = new String[]{"JobsheetScannedIDnUsername", "JobsheetScannedUsername", "jobsheetSubmitscannedItemsPrefStr", "jsmode"};
                            String[] prefvalueShuttle = new String[]{"", "", "", "shuttle"};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefNameShuttle, prefvalueShuttle);

                            i = new Intent(myApp.currentActivity, JobsheetActivity.class);
                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 2:
                            //Check Status
                            i = new Intent(myApp.currentActivity, CheckStatusActivity.class);
                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 3:
                            i = new Intent(myApp.currentActivity, CageScanActivity.class);
                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            //finish();
                            break;
                        case 4:
                            i = new Intent(myApp.currentActivity, MPDMainActivity.class);
                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 5:
                            i = new Intent(myApp.currentActivity, InventoryMainActivity.class);
                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 6:
                            //Job Inventory Verification
                            i = new Intent(myApp.currentActivity, JobInventoryVerifyActivity.class);
                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 7:
                            //Passport Safekeep
                            i = new Intent(myApp.currentActivity, ppMainActivity.class);
                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                    }
                }
            });

        } catch (Exception e) {

        }
    }

    private void initRedirectionToJobInfo(String JobInfoALjsonPrefName) {
        Gson gson = new Gson();
        String JsonStr, JobInfoJsonStr;
        objJobInfo[] JobInfoAL;
        objJobInfo JobInfo;
        String postal = "";
        String address = "";
        String addressWithUnit = "";
        double LAT = 0;
        double LNG = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        Boolean hasCOD = false;

        try {
            JsonStr = GcmPreferences.getString(JobInfoALjsonPrefName, "");
            JobInfoAL = gson.fromJson(JsonStr, objJobInfo[].class);
            JobInfo = JobInfoAL[0];
            JobInfoJsonStr = gson.toJson(JobInfo);
            objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, JobInfoJsonStr);
            JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");

            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            if (catType == 0 || catType == 1 || catType == 4) {
                //pip/pu/fp
                if (JobInfo.PURedirectedLocation != null) {
                    postal = JobInfo.PURedirectedLocation.getPOSTALCODE();
                    LAT = JobInfo.PURedirectedLocation.LAT;
                    LNG = JobInfo.PURedirectedLocation.LNG;
                    address = objGlobalMethods.returnAddressWithPostalNoUnit(JobInfo.PURedirectedLocation);
                } else if (JobInfo.PULocation != null) {
                    postal = JobInfo.PULocation.getPOSTALCODE();
                    LAT = JobInfo.PULocation.LAT;
                    LNG = JobInfo.PULocation.LNG;
                    address = objGlobalMethods.returnAddressWithPostalNoUnit(JobInfo.PULocation);
                }

                objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", String.valueOf(LAT));
                objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", String.valueOf(LNG));
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", postal);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", address);
            } else if (catType == 2 || catType == 3 || catType == 5 || catType == 6) {
                //dip/okp/fd/PD
                if (JobInfo.DLRedirectedLocation != null) {
                    postal = JobInfo.DLRedirectedLocation.getPOSTALCODE();
                    LAT = JobInfo.DLRedirectedLocation.LAT;
                    LNG = JobInfo.DLRedirectedLocation.LNG;
                    address = objGlobalMethods.returnAddressWithPostalNoUnit(JobInfo.DLRedirectedLocation);
                    //only for dunning
                    addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(JobInfo.DLRedirectedLocation);

                } else if (JobInfo.DLLocation != null) {
                    postal = JobInfo.DLLocation.getPOSTALCODE();
                    LAT = JobInfo.DLLocation.LAT;
                    LNG = JobInfo.DLLocation.LNG;
                    address = objGlobalMethods.returnAddressWithPostalNoUnit(JobInfo.DLLocation);
                    //only for dunning
                    addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(JobInfo.DLLocation);
                }

                if (catType == 2 || catType == 6) {
                    hasCOD = JobsHandler.checkForCODwithCatType(JobInfo, catType);
                    if (hasCOD) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", "true");
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", "false");
                    }

                    isDunning = false;
                    if ((JobInfo.ClientIDX == 14753 || JobInfo.ClientIDX == 4868) && JobInfo.DeliveryContents.toUpperCase().equals("DUNNING DOCUMENT")) {
                        isDunning = true;
                        long JobsIDX = JobInfo.JobsIDX;
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX, String.valueOf(JobsIDX));
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "addressWithUnit", addressWithUnit);
                        String cn = JobInfo.JobNo;
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "currJobInfoCN", String.valueOf(cn));
                    }
                }

                objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", String.valueOf(LAT));
                objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", String.valueOf(LNG));
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", postal);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", address);

            }

        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity, initRedirectionToJobInfo. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");

            if (cd.isConnectingToInternet()) {
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                JobsHandler.sendErrorEmail("Exception caught at DrawerMainActivity for " + username + ", initRedirectionToJobInfo." + "\n" + e.getMessage().toString() + ". Refreshing data.");
                isRefresh = true;
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                if (regid.equals("")) {
                    regid = objGlobalMethods.getRegistrationId(null, GcmPreferences);
                }
                GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                task.execute();
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_cannotRefreshNoInternet));
            }
        }
    }

    private void startRedirection(TextView lblCount, String JobInfoALjsonPrefName) {
        Intent i = new Intent(myApp.currentActivity, JobInfoActivity.class);
        int count;
        try {
            if (!lblCount.getText().toString().isEmpty() && !lblCount.getText().toString().toLowerCase().equals("error")) {
                count = Integer.parseInt(lblCount.getText().toString());
                if (count > 0 && count > 1) {
                    //more than 1 jobs, go to addresslist
                    if (categoryType == 0) {
                        if (FCMservice.mNotificationManager != null) {
                            FCMservice.mNotificationManager.cancel(2);
                        }
                        FCMservice.notiNotRead = false;
                    }
                    i = new Intent(myApp.currentActivity, AddressListNewActivity.class);
                    JobInfoActivity.accessFromDirection = 0;
                    handler.removeCallbacks(runnable);
                    startActivity(i);
                } else if (count > 0 && count == 1) {
                    //only 1 job, go str8 to jobInfo
                    initRedirectionToJobInfo(JobInfoALjsonPrefName);
                    if (categoryType == 0) {
                        if (FCMservice.mNotificationManager != null) {
                            FCMservice.mNotificationManager.cancel(2);
                        }
                        FCMservice.notiNotRead = false;
                    } else if (categoryType == 3 || categoryType == 4 || categoryType == 5) {
                        i = new Intent(myApp.currentActivity, JobInfoActivity.class);
                        JobInfoActivity.accessFromDirection = 1;
                    } else {
                        if (isDunning) {
                            i = new Intent(myApp.currentActivity, setDunningDeliveredStatusActivity.class);
                            JobInfoActivity.accessFromDirection = 0;
                        } else {
                            i = new Intent(myApp.currentActivity, JobInfoActivity.class);
                            JobInfoActivity.accessFromDirection = 1;
                        }
                    }
                    handler.removeCallbacks(runnable);
                    startActivity(i);
                } else if (count == 0) {
                    //0 jobs, do nothing
                }
            } else {
                //assume to be 0 jobs, do nothing
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity, startRedirection. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            JobsHandler.sendErrorEmail("Exception caught at DrawerMainActivity for " + username + ", startRedirection." + "\n" + e.getMessage().toString());
        }
    }

    public class getCODList extends AsyncTask<Void, Void, Boolean> {
        getCODList() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdCOD.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean asd = true;
            try {
                cd = new ConnectionDetector(null);
                if (cd.isConnectingToInternet()) {
                    JobsHandler.processCODJobs();
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity getCODList doInBackground. Exception: " + e.toString(), "", "", "", "");
                asd = false;
            } finally {
                return asd;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean success) {
            try {
                pdCOD.dismiss();
                Double codval = 0.0;
                String codvalPrefStr = "";
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                codvalPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalCOD");
                try {
                    codval = Double.parseDouble(codvalPrefStr);
                } catch (Exception e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity getCODList onPostExecute codval conversion. Exception: "
                            + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    codval = 0.0;
                }

                if (codval == 0.0) {

                } else if (codval > 0.0) {
                    Intent i = new Intent(myApp.currentActivity, MainCODActivity.class);
                    handler.removeCallbacks(runnable);
                    startActivity(i);
                    finish();
                }
            } catch (Exception e) {
                pdCOD.dismiss();
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity getCODList onPostExecute. Exception: " + e.toString() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    private String getRouteListJsonStr(SharedPreferences gcmp) {
        String JsonStr = "";
        try {
            JsonStr = objGlobalMethods.getGCMPrefs(gcmp, "RouteList");
        } catch (Exception e) {
            JsonStr = "";
            objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity getRouteListJsonStr. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return JsonStr;
        }
    }

    public class getRoutingList extends AsyncTask<Void, Void, Boolean> {
        private final Boolean isForMap;

        getRoutingList(Boolean aisForMap) {
            this.isForMap = aisForMap;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdRouting.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean asd = true;
            try {
                JobsHandler.HandleRouteList(regid, GcmPreferences);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity getRoutingList doInBackground. Excpetion: " + e.toString(), "", "", "", "");
                asd = false;
            } finally {
                return asd;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean success) {
            String RouteListJsonStr = "";
            try {
                RouteListJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "RouteList");
                //RouteListJsonStr = "";
                if (!RouteListJsonStr.equals("") && !RouteListJsonStr.equals("[]")) {
                    Intent i = new Intent(myApp.currentActivity, isForMap ? MapsAllActivity.class : AddressListRoutingActivity.class);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "nonSysCalRoute", "");
                    pdRouting.dismiss();
                    handler.removeCallbacks(runnable);
                    startActivity(i);
                    finish();
                } else if (RouteListJsonStr.equals("")) {
                    getOwnPlots t = new getOwnPlots(isForMap);
                    t.execute();
                }
            } catch (Exception e) {
                pdRouting.dismiss();
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity getRoutingList onPostExecute. Excpetion: " + e.toString() + "\nStack: " + stack, "", "", "", "");
            }
        }
    }

    public class getOwnPlots extends AsyncTask<Void, Void, RouteLocation[]> {
        private final Boolean isForMap;

        getOwnPlots(Boolean aisForMap) {
            this.isForMap = aisForMap;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
        }

        @Override
        protected RouteLocation[] doInBackground(Void... params) {
            RouteLocation[] AL = null;
            try {
                AL = DAO_Jobs.getRouteLocationAL();
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity getOwnPlots doInBackground. Excpetion: " + e.toString(), "", "", "", "");
                AL = null;
            } finally {
                return AL;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final RouteLocation[] AL) {
            String RouteListJsonStr = "";
            try {
                if (AL != null && AL.length > 0) {
                    Gson gson = new Gson();
                    RouteListJsonStr = gson.toJson(AL);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "RouteList", RouteListJsonStr);
                    Intent i = null;
                    if (!RouteListJsonStr.equals("") && !RouteListJsonStr.equals("[]")) {
                        i = new Intent(myApp.currentActivity, isForMap ? MapsAllActivity.class : AddressListRoutingActivity.class);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "nonSysCalRoute", "true");
                        pdRouting.dismiss();
                        handler.removeCallbacks(runnable);
                        startActivity(i);
                        finish();
                    } else {
                        pdRouting.dismiss();
                        showDialog(myApp.getAppContext().getString(R.string.alertNoRouteToShow));
                    }
                } else {
                    pdRouting.dismiss();
                    showDialog(myApp.getAppContext().getString(R.string.alertNoRouteToShow));
                }
            } catch (Exception e) {
                pdRouting.dismiss();
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity getOwnPlots onPostExecute. Exception: " + e.toString() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    public void updateListRowPerCategory(int index, Boolean changeCountColor) {
        Dashboard_PostData postdata = null;
        boolean isStdRunner = false;
        boolean isSatSun = false;
        boolean promptReqMpdBool = false;
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        try {
            if (GcmPreferences == null)
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            boolean llite = JobsHandler2.isLuke_Reduced(GcmPreferences);
            //objGlobalMethods.writeToFile("DrawerMainActivity updateListRowPerCategory called.", "", "", "", "");
            List<dbObjCategoryCount> dbObjCategoryCountAL = DAO_Jobs.getPerCategoryCount(index);
            adapter = (Dashboard_PostItemAdapter) listView.getAdapter();

            if (adapter != null) {
                if (dbObjCategoryCountAL != null && dbObjCategoryCountAL.size() > 0) {
                    dbObjCategoryCount obj = dbObjCategoryCountAL.get(0);
                    if (llite && (index == 2 || index == 6)) {
                        postdata = showRouteList != null && showRouteList != "" && index == 2 ? adapter.getItem(1) : adapter.getItem(2);
                        postdata.postCount = String.valueOf(obj.getCount());
                        postdata.changeCountColorBool = changeCountColor;
                        postdata.setDIP(obj != null && obj.CatTypeID == 2);
                        postdata.setOKP(obj != null && obj.CatTypeID == 3);
                        postdata.setReqNetsCount(obj.ReqNetsCount);
                        postdata.setReqVisaCount(obj.ReqVisaCount);
                        postdata.setReqTiCount(obj.ReqTiCount);
                        postdata.setColdCount(obj.ColdCount);
                        promptReqMpdBool = (obj.CatTypeID == 2);
                    /*if (obj.CatTypeID == 2) {
                        promptReqMPD();
                    }*/
                    } else if (!llite) {
                        postdata = showRouteList != null && showRouteList != "" ? adapter.getItem(index + 3) : adapter.getItem(index + 1);
                        postdata.postCount = String.valueOf(obj.getCount());
                        postdata.changeCountColorBool = changeCountColor;
                        postdata.setDIP(obj != null && obj.CatTypeID == 2);
                        postdata.setOKP(obj != null && obj.CatTypeID == 3);
                        postdata.setReqNetsCount(obj.ReqNetsCount);
                        postdata.setReqVisaCount(obj.ReqVisaCount);
                        postdata.setReqTiCount(obj.ReqTiCount);
                        postdata.setColdCount(obj.ColdCount);
                        promptReqMpdBool = (obj.CatTypeID == 2);
                    /*if (obj.CatTypeID == 2) {
                        promptReqMPD();
                    }*/
                    }
                } else {
                    if (llite && (index == 2 || index == 6)) {
                        postdata = showRouteList != null && showRouteList != "" && index == 2 ? adapter.getItem(1) : adapter.getItem(2);
                        postdata.postCount = "0";
                        postdata.changeCountColorBool = changeCountColor;
                        postdata.setReqNetsCount(0);
                        postdata.setReqVisaCount(0);
                        postdata.setReqTiCount(0);
                        postdata.setColdCount(0);
                    } else if (!llite) {
                        postdata = showRouteList != null && showRouteList != "" ? adapter.getItem(index + 3) : adapter.getItem(index + 1);
                        postdata.postCount = "0";
                        postdata.changeCountColorBool = changeCountColor;
                        postdata.setReqNetsCount(0);
                        postdata.setReqVisaCount(0);
                        postdata.setReqTiCount(0);
                        postdata.setColdCount(0);
                    }
                }
                adapter.notifyDataSetChanged();
                isStdRunner = JobsHandler2.isStdRunner(GcmPreferences);
                if (isStdRunner) {
                    isSatSun = JobsHandler2.isSatSun();
                }
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity updateListRowPerCategory. Call loadListFirst(). Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
            if (showRouteList != null && !showRouteList.equals("")) {
                loadListFirst2SQLite();
            } else {
                loadListFirst();
            }
        } finally {
            if (!IsMaintenanceHours &&
                    (JobsHandler.hasOpsQ() || objGlobalMethods.IsDefaultVRPpage() || objGlobalMethods.isVRPbySettings() || (isStdRunner && isSatSun))) {
                if (isStdRunner && isSatSun) {
                    JobsHandler.redirectToVrpPage2();
                } else {
                    JobsHandler.redirectToVrpPage();
                }
            }
        }
    }

    public void updatePerformanceItem() {
        Dashboard_PostData postdata = null;
        Context ctx = myApp.getAppContext();
        try {
            adapter = (Dashboard_PostItemAdapter) listView.getAdapter();
            if (adapter != null) {
                postdata = adapter.getItem(0);
                String totalJobsForTheDaytxt = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalJobsForTheDaytxt");
                String performancetxt = objGlobalMethods.getGCMPrefs(GcmPreferences, "performancetxt");
                //String dist_travelledtxt = objGlobalMethods.getGCMPrefs(GcmPreferences, "dist_travelled");

                ////RMB TO CHANGE AT loadListFirst2SQLite()

                ////this is with percentage only
                String pt = ctx.getString(R.string.performancetxt01) + " " + totalJobsForTheDaytxt
                        + "\n" + ctx.getString(R.string.performancetxt02) + " " + performancetxt;


                ////this is with distance travelled
                /*String pt = ctx.getString(R.string.performancetxt01) + " " + totalJobsForTheDaytxt
                        + "\n" + ctx.getString(R.string.performancetxt02) + " " + performancetxt
                        + "\n" + ctx.getString(R.string.performancetxt06) + ": " + (objGlobalMethods.IsEmptyString(dist_travelledtxt) ? "0.00 " + ctx.getString(R.string.performancetxt04) : dist_travelledtxt) + " " + ctx.getString(R.string.performancetxt04);*/

                ////this is with distance travelled and avg speed
                /*String pt = ctx.getString(R.string.performancetxt01) + " " + totalJobsForTheDaytxt
                        + "\n" + ctx.getString(R.string.performancetxt02) + " " + performancetxt
                        + "\n" + ctx.getString(R.string.performancetxt03) + " " + (objGlobalMethods.IsEmptyString(dist_travelledtxt) ? "0.0%" : dist_travelledtxt) + ctx.getString(R.string.performancetxt04)
                        + "\n" + "AVG SPEED: 0KM/H";*/
                if (!objGlobalMethods.IsEmptyString(pt)) {
                    postdata.setPostTitle(pt);
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity updatePerformanceItem. Exception: " + e.getMessage(), "", "", "", "");
            if (showRouteList != null && !showRouteList.equals("")) {
                loadListFirst2SQLite();
            } else {
                loadListFirst();
            }
        }
    }

    //shifted to setMenuText
    public void updateDrawerCOD() {
        try {
            Double codval = 0.0;
            String codvalPrefStr = "";
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            codvalPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalCOD");
            try {
                if (codvalPrefStr != null && !codvalPrefStr.equals("") && codvalPrefStr != "") {
                    codval = Double.parseDouble(codvalPrefStr);
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity updateDrawerCOD codval conversion. Exception: " + e.getMessage().toString(), "", "", "", "");
                codval = 0.0;
            }
            DecimalFormat dec = new DecimalFormat("#0.00");
            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
            Menu nav_Menu = navigationView.getMenu();
            String title = myApp.getAppContext().getString(R.string.item_dashboard2COD) + " $" + String.valueOf(dec.format(codval));
            nav_Menu.findItem(R.id.cod).setTitle(title);
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity updateDrawerCOD. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    /*public void updateListviewRowJustForCOD() {
        Dashboard_PostData postdata11 = null;
        try {
            adapter = (Dashboard_PostItemAdapter) listView.getAdapter();
            if (adapter != null) {
                Double codval = 0.0;
                String codvalPrefStr = "";
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                codvalPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalCOD");
                try {
                    codval = Double.parseDouble(codvalPrefStr);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity updateListviewRowJustForCOD codval conversion. Exception: " + e.getMessage().toString(), "", "", "", "");
                    codval = 0.0;
                }

                DecimalFormat dec = new DecimalFormat("#.00");
                if (showRouteList != null && !showRouteList.equals("")) {
                    postdata11 = adapter.getItem(13);
                    postdata11.changeCountColorBool = true;
                    if (codval == 0.0) {
                        postdata11.postTitle = val[13] + "  " + "$0.00";
                        postdata11.postCount = "0";
                    } else if (codval > 0.0) {
                        postdata11.postTitle = val[13] + "  " + "$" + String.valueOf(dec.format(codval));
                        postdata11.postCount = "1";
                    }
                } else {
                    postdata11 = adapter.getItem(12);
                    postdata11.changeCountColorBool = true;
                    if (codval == 0.0) {
                        postdata11.postTitle = val[12] + "  " + "$0.00";
                        postdata11.postCount = "0";
                    } else if (codval > 0.0) {
                        postdata11.postTitle = val[12] + "  " + "$" + String.valueOf(dec.format(codval));
                        postdata11.postCount = "1";
                    }
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Dashboard2Acitivty updateListviewRowJustForCOD. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
        }
    }*/

    public void updateDrawerJobsheet() {
        String count = "0";
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            count = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.JobsheetCountName);

            if (Boolean.parseBoolean(ishomesuser)) {
                Dashboard_PostData postdata = null;
                adapter = (Dashboard_PostItemAdapter) listView.getAdapter();
                if (adapter != null) {
                    postdata = adapter.getItem(0);
                    postdata.changeCountColorBool = true;
                    postdata.postCount = count;
                    adapter.notifyDataSetChanged();
                }
            } else {
                /*NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
                Menu nav_Menu = navigationView.getMenu();
                String title = myApp.getAppContext().getString(R.string.item_dashboard2JOBSHEET) + " " + count;
                nav_Menu.findItem(R.id.cod).setTitle(title);*/

                updateDrawerJSCount(null);
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity updateDrawerJobsheet. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    /*public void updateListviewRowJustForJobsheet() {
        Dashboard_PostData postdata7 = null;
        String count = "0";
        try {
            adapter = (Dashboard_PostItemAdapter) listView.getAdapter();
            if (adapter != null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                count = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.JobsheetCountName);
                if (showRouteList != null && !showRouteList.equals("")) {
                    postdata7 = adapter.getItem(9);
                    postdata7.changeCountColorBool = true;
                    postdata7.postCount = count;
                } else {
                    postdata7 = adapter.getItem(8);
                    postdata7.changeCountColorBool = true;
                    postdata7.postCount = count;
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Dashboard2Acitivty updateListviewRowJustForJobsheet. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
        }
    }*/

    //start timer section
    public static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                /* do what you need to do */
                if (!JobsHandler.backEndIsRunning) {
                    //runner Is Not Running
                    Context context = myApp.getAppContext();
                    context.sendBroadcast(new Intent(context, BackendRun_BroadcastReceiver.class));
                } else {

                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught @ DrawerMainActivity Runnable(). " + e.getMessage().toString() + "\nStack: " + stack,
                        "", "", "", "");
            }
        }
    };

    public static void stopRunnable() {
        JobsHandler.backEndIsRunning = false;
        handler.removeCallbacks(runnable);
    }

    public static void postphoneRunnable() {
        JobsHandler.backEndIsRunning = false;
        handler.postDelayed(runnable, 15000);
    }

    //end timer section

    //start Hash SHA1
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

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public static String getSaltedSHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String h = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            String ss = "jnksntr*(YjhUB7uygH%Fg8*j(jOIPJuHG^GFBuijniojKJHBYG6FGh^&%^$6598)(";
            String sss = text + ss;
            md.update(sss.getBytes("iso-8859-1"), 0, sss.length());
            byte[] sha1hashsalted = md.digest();
            h = convertToHex(sha1hashsalted);
        } catch (Exception e) {
            String s = e.getMessage();
        } finally {
            return h;
        }
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        //Always use a SecureRandom generator
        //SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        SecureRandom sr = new SecureRandom();
        //Create array for salt
        String s = "jnksntr*(YjhUB7uygH%Fg8*j(jOIPJuHG^GFBuijniojKJHBYG6FGh^&%^$6598)(";
        byte[] salt = s.getBytes();
        //Get a random salt
        //return salt
        return salt;
    }

    //end HASH SHA1

    private String getStoredStatusCodes(Context context, String enzh) {
        final SharedPreferences prefs = objGlobalMethods.getGcmPreferences();
        String storedStatusCodes = "";
        if (enzh.equals("en")) {
            storedStatusCodes = prefs.getString(objGlobalVariables.storedStatusCodesPrefNameEN, "");
        } else {
            storedStatusCodes = prefs.getString(objGlobalVariables.storedStatusCodesPrefNameZH, "");
        }
        return storedStatusCodes;
    }

    private void downloadStatusCodes() {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            regid = objGlobalMethods.getRegistrationIdOnly();
            JobsHandler.downloadStatusCodesLibWS task = new JobsHandler.downloadStatusCodesLibWS(regid, GcmPreferences);
            task.execute();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity downloadStatusCodes. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void stopPlayingTone() {
        try {
            if (player != null) {
                player.reset();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playTone() {
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(myApp.getAppContext(), Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error));
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    //=========================================dashboard code from dashboard2=================================================

    //=========================================sqlite=================================================

    //=========================================sqlite=================================================
    private class GetDriverJobsCountSQLite extends AsyncTask<Void, Void, Boolean> {
        final SharedPreferences gcmPref;
        final SharedPreferences aUpdateJobInfoPreferences;
        final String aregid;
        final String ausername;
        final String auseridx;
        Date starttime;
        Date endtime;

        GetDriverJobsCountSQLite(SharedPreferences agcmPref, SharedPreferences aUpdateJobInfoPreferences,
                                 String aaregid, String aausername, String aauseridx) {
            this.gcmPref = agcmPref;
            this.aUpdateJobInfoPreferences = aUpdateJobInfoPreferences;
            this.aregid = aaregid;
            this.ausername = aausername;
            this.auseridx = aauseridx;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            starttime = new Date();
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //R6: skip job loading while RootGuard blocks this process; cancelling makes AsyncTask call onCancelled instead of onPostExecute
            if (RootGuard.blockOperation("DrawerMainActivity.GetDriverJobsCountSQLite", null)) {
                cancel(false);
                return false;
            }
            Boolean asd = true;
            Date dt = new Date();
            totalCOD = 0.0;
            int TradeInModelCount = 0;
            dbObjTradeInList[] TradeInList = null;
            objRecipientType[] RecipientTypesArray;
            NFCTag[] NFCTagList = null;
            ArrayList<objJobsInsertionInfo> objJobsInsertionInfoList = new ArrayList<>();
            objJobsInsertionInfo JobsInsertionInfo = null;
            objCabinet Cabinet = null;
            Long[] JobsIDXarr = null;
            objJobInfo[] JobInfoAL = null;
            objJobInfo[] PIPJobInfoAL = null;
            ArrayList<PipPcs> pippcslist = null;
            String s = "";
            int JobsIDXarrsize = 0;
            int JobInfoALsize = 0;
            String logmsg = "DrawerMainActivity GetDriverJobsCountSQLite";
            try {
                lite = JobsHandler2.isLuke_Reduced(gcmPref);
                objGlobalMethods.setGCMPrefs(gcmPref, "isrefreshing", "t");
                if (!Boolean.parseBoolean(ishomesuser)) {
                    DriverJobsArrayList = JobsHandler.GetDriverJobsArrayList(aregid);
                } else {
                    DriverJobsArrayList = null;
                    objGlobalMethods.writeToFile("DrawerMainActivity GetDriverJobsCountSQLite doInBackground DriverJobsArrayList is null.", "", "", "", "");
                }

                if (DriverJobsArrayList != null && DriverJobsArrayList.size() > 0) {
                    //DAO_Jobs.deleteAllJobs();
                    for (int i = 0; i <= DriverJobsArrayList.size() - 1; i++) {
                        ArrayList arr = new ArrayList();
                        JobsIDXarr = null;
                        JobInfoAL = null;
                        s = "";
                        JobsIDXarrsize = 0;
                        JobInfoALsize = 0;

                        arr = (ArrayList) DriverJobsArrayList.get(i);
                        JobsIDXarr = arr != null && arr.size() > 0 && arr.get(0) != null ? (Long[]) arr.get(0) : null;
                        JobInfoAL = arr != null && arr.size() == 3 && arr.get(2) != null ? (objJobInfo[]) arr.get(2) : null;
                        if (JobsIDXarr != null && JobInfoAL != null) {
                            Arrays.sort(JobsIDXarr);
                            for (int v = 0; v <= JobsIDXarr.length - 1; v++) {
                                s += "[" + String.valueOf(JobsIDXarr[v]) + "]";
                            }

                            if (!s.trim().equals("")) {
                                try {
                                    cHashstr = SHA1(s);
                                } catch (Exception e) {
                                    objGlobalMethods.writeToFile("Exception at DrawerMainActivity GetDriverJobsCountSQLite doInBackground cHashstr conversion. Exception: " + e.getMessage(), "", "", "", "");
                                    s = "";
                                    cHashstr = "";
                                }
                            } else {
                                cHashstr = "";
                            }

                            JobsIDXarrsize = JobsIDXarr == null ? 0 : JobsIDXarr.length;
                            JobInfoALsize = JobInfoAL == null ? 0 : JobInfoAL.length;
                            logmsg += "\n" +
                                    "JobsIDXarr size = " + String.valueOf(JobsIDXarrsize) + " JobInfoAL size = " + String.valueOf(JobInfoALsize) + " when i = " + String.valueOf(i);
                            //objGlobalMethods.writeToFile("DrawerMainActivity GetDriverJobsCountSQLite JobsIDXarr size = " + String.valueOf(JobsIDXarrsize) + " JobInfoAL size = " + String.valueOf(JobInfoALsize) + " when i = " + String.valueOf(i), "", "", "", "");

                            JobsHandler.setCategoryHashes(gcmPref,
                                    i == 0 ? objGlobalVariables.PIPhashName :
                                            i == 1 ? objGlobalVariables.PUhashName :
                                                    i == 2 ? objGlobalVariables.DIPhashName :
                                                            i == 3 ? objGlobalVariables.DELhashName :
                                                                    i == 4 ? objGlobalVariables.FPhashName :
                                                                            i == 5 ? objGlobalVariables.FDhashName :
                                                                                    i == 6 ? objGlobalVariables.PDhashName :
                                                                                            objGlobalVariables.TThashName, cHashstr);

                            if (i == 0 && JobInfoAL != null)
                                PIPJobInfoAL = JobInfoAL;

                            JobsInsertionInfo = new objJobsInsertionInfo(JobInfoAL, i);
                            objJobsInsertionInfoList.add(JobsInsertionInfo);
                        }

                        if (i == 7 && objJobsInsertionInfoList.size() > 0) {
                            for (int k = 0; k <= objJobsInsertionInfoList.size() - 1; k++) {
                                DAO_Jobs.deleteJobsByCatTypeID(objJobsInsertionInfoList.get(k).CatTypeID);
                            }
                            ArrayList<objInsertJobInfo> ijilist = DAO_Jobs.rtnInsertJobInfoAL(objJobsInsertionInfoList);
                            if (ijilist != null && ijilist.size() > 0) {
                                DAO_Jobs.insertJobsBatch3(ijilist);
                            }

                        }
                    }

                    if (PIPJobInfoAL != null && PIPJobInfoAL.length > 0) {
                        pippcslist = JobsHandler2.rtnPipPcs(PIPJobInfoAL, null);
                        if (pippcslist != null && pippcslist.size() > 0) {
                            objGlobalMethods.writeToFile("DrawerMainActivity bulkInsertPIPPCS pippcslist size " + String.valueOf(pippcslist.size()), "", "", "", "");
                            DAO_Jobs.bulkInsertPIPPCS(pippcslist);
                            /*DAO_Jobs.cleanPIPPCS();
                            DAO_Jobs.updatePIPPCS_ZeroScanned();*/
                        }
                    }

                    if (!objGlobalMethods.IsEmptyString(logmsg)) {
                        objGlobalMethods.writeToFile(logmsg, "", "", "", "");
                    }

                    ////JobsHandler.processCODJobs();
                    Cabinet = WebService.GetCabinet(aregid, "2");
                    JobsHandler.setSaveAmended4QuesAndInitialSetOfJobsIDXs(Cabinet);
                    JobsHandler2.updateCabinetForPrevDayDataToNew(Cabinet, aregid, useridx);
                    //JobsHandler2.enableVRP_EditRouteButton_NearHendersonOfcNew(GcmPreferences);
                    JobsHandler2.enableVRP_EditRouteButton_NearHendersonOfcNew(GcmPreferences, isRefresh);
                    //JobsHandler.getJobQue2(aregid, false, false, -1);
                    ArrayList<Integer> ListItemIndex = new ArrayList<>();
                    ListItemIndex.add(-1);
                    String JobsPostalsToBeUNPARKEDJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "JobsPostalsToBeUNPARKEDJsonStr");
                    ArrayList<String> JobsPostalsToBeUNPARKED = null;
                    if (!objGlobalMethods.IsEmptyString(JobsPostalsToBeUNPARKEDJsonStr)) {
                        JobsPostalsToBeUNPARKED = new Gson().fromJson(JobsPostalsToBeUNPARKEDJsonStr, new TypeToken<ArrayList<String>>() {
                        }.getType());
                    }
                    JobsHandler.getJobQue2(aregid, false, false, ListItemIndex, JobsPostalsToBeUNPARKED);

                    /*////unpark jobs that has same postals as original pip dip jobs, must be after getJobQue2 due to VRP portion
                    JobsHandler2.regroupParkedJobs();
                    ////unpark jobs that has same postals as original pip dip jobs, must be after getJobQue2 due to VRP portion*/

                    /*TradeInList = WebService.GetTradeInList(aregid);
                    if (TradeInList != null && TradeInList.length > 0) {
                        DAO_Jobs.insertTradeInList(TradeInList);
                    }

                    NFCTagList = WebService.GetNFCTagsList(aregid);
                    if (NFCTagList != null && NFCTagList.length > 0) {
                        DAO_Jobs.insertNFCTAGS(NFCTagList);
                    }*/

                    //mpds is shifted to upon successful login n mpdcheckout page
                    ArrayList<objMPD_Info> mpds = objGlobalMethods.grabMPDFromServer();
                    /*if (mpds != null && mpds.size() > 0) {
                        String t = "";
                        objMPD_Info mi = null;
                        for (int mpd = 0; mpd <= mpds.size() -1; mpd++) {
                            mi = mpds.get(mpd);
                            t += "\n" + "[Device_Type: " + mi.Device_Type + ", Serial_Number: " + mi.Serial_Number + "]";
                        }
                        if (!objGlobalMethods.IsEmptyString(t))
                            objGlobalMethods.writeToFile(t,"","","","");
                    }*/
                    //objGlobalMethods.setMPDsPrefStr(mpds, gcmPref);
                    objGlobalMethods.setMPDsPrefStrNew(mpds);

                    objGlobalMethods.grabSettingsFromServer();

                    /*if (!pdfclass.pdfExist()) {
                        //download pdf
                        pdfclass.downloadpdf();
                    } else {
                        pdfclass.pdfVersionCheck();
                    }*/

                    /*String edgar = "fg66Lf-89T0:APA91bHwzbg_CD31jba25QJ_4a_vd4FUEPqLQLWO0vPj56EwCfnWF8BTYYF7lhEFu3vWTEza-bwdomP8hfR5nW4D7GHzL0Sp3FTNkF1MxRDnbsUZJeoKGeSPECuk25_KvAm1LCD4BrgR";
                    L2_LA_GetUserResult GetUserResult = WebService.L2_LA_GetUser(edgar);*/
                    L2_LA_GetUserResult GetUserResult = WebService.L2_LA_GetUser(aregid);
                    //GetUserResult = null;
                    //JobsHandler2.addCallRequeue(GcmPreferences, 1);
                    if (GetUserResult != null) {
                        Gson gson = new Gson();
                        String GetUserResultJsonStr = gson.toJson(GetUserResult);
                        //objGlobalMethods.setGCMPrefs(gcmPref, "GetUserResultJsonStr", GetUserResultJsonStr);
                        String[] prefName = new String[]{"GetUserResultJsonStr", "LockerUserKeyBarcode64str"};
                        String[] prefvalue = new String[]{
                                GetUserResultJsonStr,
                                ""
                        };
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                        JobsHandler2.removeCallRequeue(GcmPreferences, 1);
                    } else {
                        //objGlobalMethods.setGCMPrefs(gcmPref, "GetUserResultJsonStr", "");
                        String[] prefName = new String[]{"GetUserResultJsonStr", "LockerUserKeyBarcode64str"};
                        String[] prefvalue = new String[]{
                                "",
                                ""
                        };
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                        JobsHandler2.addCallRequeue(GcmPreferences, 1);
                    }

                    //temploggerclass.chkgrabtemps(GcmPreferences, aregid, true);

                    /*Cabinet = WebService.GetCabinet(aregid, "2");
                    JobsHandler.setSaveAmended4QuesAndInitialSetOfJobsIDXs(Cabinet);*/

                    //JobsHandler.HandleRouteList(regid, gcmPref);

                    ////do not uncomment til further notice
                    /*RecipientTypesArray = WebService.GetRecipientTypes(aregid, "L2_GetRecipientTypes");
                    if (RecipientTypesArray.length > 0) {
                        Gson gson = new Gson();
                        String RecipientTypesArrayJsonStr;
                        RecipientTypesArrayJsonStr = gson.toJson(RecipientTypesArray);
                        JobsHandler.storeRecipientTypes(gcmPref, RecipientTypesArrayJsonStr);
                    }*/
                    ////do not uncomment til further notice

                    if (!lite)
                        JobsHandler.getGCPerformancePercentage(aUpdateJobInfoPreferences, gcmPref, false);
                    //JobsHandler.getGCDistTravelled(gcmPref, aregid);

                    asd = true;
                } else {
                    asd = false;
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity GetDriverJobsCountSQLite doInBackground. Exception: " + e.toString(), "", "", "", "");
                asd = false;
                totalCOD = 0.0;
                objGlobalMethods.setGCMPrefs(gcmPref, "isrefreshing", "f");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            try {
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                objGlobalMethods.setGCMPrefs(gcmPref, "isrefreshing", "f");
                DriverJobsArrayList = null;
                if (context == null) {
                    context = myApp.getAppContext();
                }
                if (success) {
                    if (isRefresh == null) {
                        isRefresh = false;
                    }
                    if (isRefresh) {
                        //updateList();
                        if (refreshQueBooleanString) {
                            objGlobalMethods.setGCMPrefs(gcmPref, "refreshQueBooleanString", "false");
                        }

                        isRefresh = false;
                        if (showRouteList != null && !showRouteList.equals("")) {
                            loadListFirst2SQLite();
                        } else {
                            loadListFirst();
                        }
                    } else {
                        if (!objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours) {
                            if (showRouteList != null && !showRouteList.equals("")) {
                                loadListFirst2SQLite();
                            } else {
                                loadListFirst();
                            }
                        }
                    }

                    Intent broadcastedIntent = new Intent(context, GetAllMsg_Receiver.class);
                    broadcastedIntent.putExtra("uponLogin", 1);
                    sendBroadcast(broadcastedIntent);

                    Intent DownloadDataReceiver = new Intent(context, Download_Data_BroadcastReceiver.class);
                    sendBroadcast(DownloadDataReceiver);
                } else {
                    if (!objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours) {
                        if (showRouteList != null && !showRouteList.equals("")) {
                            loadListFirst2SQLite();
                        } else {
                            loadListFirst();
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        // Notifiy activity
                        //hideNavigationItems();
                        initMenu();
                    }
                });

            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity GetDriverJobsCountSQLite onPostExecute. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                if (pd != null) {
                    pd.dismiss();
                }
            } finally {
                endtime = new Date();
                if (pd != null) {
                    pd.dismiss();
                }
                long diffTime = endtime.getTime() - starttime.getTime();
                Toast.makeText(myApp.getAppContext(), String.valueOf(diffTime / 1000) + " seconds.", Toast.LENGTH_SHORT).show();
                //check for any new job yet to be updated to PIP
                //JobsHandler.doubleCheckUpdatePIPSQLite(gcmPref, aUpdateJobInfoPreferences, aregid, ausername, auseridx, false);
                JobsHandler.doubleCheckUpdatePIPSQLiteNew(gcmPref, aUpdateJobInfoPreferences, aregid, ausername, auseridx);
                if (!success) {
                    String errmsg = "";
                    if (cd.isConnectingToInternet()) {
                        errmsg = myApp.getAppContext().getString(R.string.alert_noRetrieveDataFail);
                    } else {
                        errmsg = myApp.getAppContext().getString(R.string.alert_noRetrieveDataFailDueNoInternet);
                    }
                    try {
                        showDialog(errmsg);
                    } catch (Exception e) {
                        String stack = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity GetDriverJobsCountSQLite onPostExecute showDialog alert_noRetrieveDataFail. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                        Toast.makeText(myApp.getAppContext(), errmsg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void loadListFirst2SQLite() {
        Context ctx = myApp.getAppContext();
        boolean isWalker = false;
        boolean isStdRunner = false;
        boolean isSatSun = false;
        boolean isAOHtime = false;
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        int medc = 0;
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            List<dbObjCategoryCount> dbObjCategoryCountAL = DAO_Jobs.getCategoryCount();
            ArrayList<Dashboard_PostData> dashboardPostDataArr = new ArrayList<Dashboard_PostData>();
            medc = DAO_Jobs.getMedJobsCount();
            if (medc > 0) {
                DAO_Jobs.cleanPIPPCS();
                objGlobalMethods.writeToFile("DrawerMainActivity updatePIPPCS_ZeroScanned", "", "", "", "");
                DAO_Jobs.updatePIPPCS_ZeroScanned();
            }

            Dashboard_PostData postdata;
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            lite = JobsHandler2.isLuke_Reduced(GcmPreferences);
            DecimalFormat dec = new DecimalFormat("#.00");
            setMenuText();

            for (int i = 0; i <= val.length - 1; i++) {
                if (lite) {
                    if (i >= 1 && i <= 2) {
                        dbObjCategoryCount obj = null;
                        if (dbObjCategoryCountAL != null && dbObjCategoryCountAL.size() > 0) {
                            for (int x = 0; x <= dbObjCategoryCountAL.size() - 1; x++) {
                                if (i == 1) {
                                    if (dbObjCategoryCountAL.get(x).getCatTypeID() == 2) {
                                        obj = dbObjCategoryCountAL.get(x);
                                        break;
                                    }
                                } else if (i == 2) {
                                    if (dbObjCategoryCountAL.get(x).getCatTypeID() == 6) {
                                        obj = dbObjCategoryCountAL.get(x);
                                        break;
                                    }
                                }
                            }
                        }
                        if (DashboardOpenFromPush) {
                            if (obj != null) {
                                postdata = new Dashboard_PostData(val[i], obj.getCount().toString(), true, true);
                                postdata.setColdCount(obj.ColdCount);
                            } else {
                                postdata = new Dashboard_PostData(val[i], "0", true, true);
                                postdata.setColdCount(0);
                            }
                        } else {
                            if (obj != null) {
                                postdata = new Dashboard_PostData(val[i], obj.getCount().toString(), false, true);
                                postdata.setColdCount(obj.ColdCount);
                            } else {
                                postdata = new Dashboard_PostData(val[i], "0", false, true);
                                postdata.setColdCount(0);
                            }
                        }
                        dashboardPostDataArr.add(postdata);
                    } else if (i == 0) {
                        //all maps
                        postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                        postdata.setLite(true);
                        dashboardPostDataArr.add(postdata);
                    }
                } else {
                    //if (i >= 2 && i <= 7) {
                    //if (i >= 2 && i <= 8) {
                    if (i >= 2 && i <= 9) {
                        dbObjCategoryCount obj = null;

                        if (i == 2) {
                            if (!Boolean.parseBoolean(ishomesuser)) {
                                if (dbObjCategoryCountAL != null && dbObjCategoryCountAL.size() > 0) {
                                    for (int x = 0; x <= dbObjCategoryCountAL.size() - 1; x++) {
                                        if (dbObjCategoryCountAL.get(x).getCatTypeID() == 0) {
                                            obj = dbObjCategoryCountAL.get(x);
                                            break;
                                        }
                                    }
                                }
                                if (DashboardOpenFromPush) {
                                    if (obj != null) {
                                        postdata = new Dashboard_PostData(val[i], obj.getCount().toString(), true, true);
                                        postdata.setColdCount(obj.ColdCount);
                                    } else {
                                        postdata = new Dashboard_PostData(val[i], "0", true, true);
                                        postdata.setColdCount(0);
                                    }
                                } else {
                                    if (obj != null) {
                                        postdata = new Dashboard_PostData(val[i], obj.getCount().toString(), false, true);
                                        postdata.setColdCount(obj.ColdCount);
                                    } else {
                                        postdata = new Dashboard_PostData(val[i], "0", false, true);
                                        postdata.setColdCount(0);
                                    }
                                }
                                dashboardPostDataArr.add(postdata);
                            } else {
                                postdata = new Dashboard_PostData(val[i], "0", false, true);
                                postdata.setColdCount(0);
                                dashboardPostDataArr.add(postdata);
                            }
                        } else {
                            if (!Boolean.parseBoolean(ishomesuser)) {
                                if (dbObjCategoryCountAL != null && dbObjCategoryCountAL.size() > 0) {
                                    for (int x = 0; x <= dbObjCategoryCountAL.size() - 1; x++) {
                                        if (dbObjCategoryCountAL.get(x).getCatTypeID() == i - 2) {
                                            obj = dbObjCategoryCountAL.get(x);
                                            break;
                                        }
                                    }
                                }
                                if (obj != null) {
                                    postdata = new Dashboard_PostData(val[i], obj.getCount().toString(), false, true);
                                    postdata.setReqNetsCount(obj.ReqNetsCount);
                                    postdata.setReqVisaCount(obj.ReqVisaCount);
                                    postdata.setReqTiCount(obj.ReqTiCount);
                                    postdata.setColdCount(obj.ColdCount);
                                } else {
                                    postdata = new Dashboard_PostData(val[i], "0", false, true);
                                    postdata.setColdCount(0);
                                }
                                postdata.setDIP(obj != null && obj.CatTypeID == 2);
                                postdata.setOKP(obj != null && obj.CatTypeID == 3);
                                dashboardPostDataArr.add(postdata);
                            } else {
                                postdata = new Dashboard_PostData(val[i], "0", false, true);
                                postdata.setColdCount(0);
                                postdata.setDIP(obj != null && obj.CatTypeID == 2);
                                postdata.setOKP(obj != null && obj.CatTypeID == 3);
                                dashboardPostDataArr.add(postdata);
                            }
                        }
                    } else if (i == 0) {
                        //all maps
                        postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                        dashboardPostDataArr.add(postdata);
                    } else if (i == 1) {
                        //routing
                        postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                        dashboardPostDataArr.add(postdata);
                    }
                }
            }

            if (!Boolean.parseBoolean(ishomesuser) && !lite) {
                String totalJobsForTheDaytxt = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalJobsForTheDaytxt");
                String performancetxt = objGlobalMethods.getGCMPrefs(GcmPreferences, "performancetxt");
                String dist_travelledtxt = objGlobalMethods.getGCMPrefs(GcmPreferences, "dist_travelled");

                ////RMB TO CHANGE AT updatePerformanceItem()
                String pt = ctx.getString(R.string.performancetxt01) + " " + totalJobsForTheDaytxt
                        + "\n" + ctx.getString(R.string.performancetxt02) + " " + performancetxt;

                ////this is with distance travelled
                /*String pt = ctx.getString(R.string.performancetxt01) + " " + totalJobsForTheDaytxt
                        + "\n" + ctx.getString(R.string.performancetxt02) + " " + performancetxt
                        + "\n" + ctx.getString(R.string.performancetxt06) + ": " + (objGlobalMethods.IsEmptyString(dist_travelledtxt) ? "0.00 " + ctx.getString(R.string.performancetxt04) : dist_travelledtxt) + " " + ctx.getString(R.string.performancetxt04);*/

                ////this is with distance travelled and avg speed
                /*String pt = ctx.getString(R.string.performancetxt01) + " " + totalJobsForTheDaytxt
                        + "\n" + ctx.getString(R.string.performancetxt02) + " " + performancetxt
                        + "\n" + ctx.getString(R.string.performancetxt03) + " " + (objGlobalMethods.IsEmptyString(dist_travelledtxt) ? "0.0%" : dist_travelledtxt) + ctx.getString(R.string.performancetxt04)
                        + "\n" + "AVG SPEED: 0KM/H";*/

                if (!objGlobalMethods.IsEmptyString(pt)) {
                    postdata = new Dashboard_PostData(pt, String.valueOf(""), false, false);
                    dashboardPostDataArr.add(0, postdata);
                }
            }

            adapter = new Dashboard_PostItemAdapter(myApp.getAppContext(), dashboardPostDataArr);

            listView = null;
            if (listView == null) {
                listView = (ListView) findViewById(R.id.listView);
            }
            listView.setAdapter(adapter);
            //updateDrawerCOD(); //shifted to setMsgCountNew
            //promptReqMPD();
            objGlobalMethods.showNewPIPdialog(GcmPreferences);
            objGlobalMethods.writeToFile("loadListFirst2SQLite complete", "", "", "", "");
            isStdRunner = JobsHandler2.isStdRunner(GcmPreferences);
            isWalker = JobsHandler2.isWalker(GcmPreferences);
            if (!isWalker && isStdRunner) {
                isSatSun = JobsHandler2.isSatSun();
                isAOHtime = JobsHandler2.isAOHtime();
            }

            //initBleManager();
            listView.setOnItemClickListener(null);
            if (lite) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i;
                        view.clearFocus();
                        View v;
                        TextView lblCount;
                        String[] prefName;
                        String[] prefvalue;
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                        boolean currTimeAllowJobsRefreshDuringMaintenanceDay = false;
                        if (IsMaintenanceHours)
                            currTimeAllowJobsRefreshDuringMaintenanceDay = objGlobalMethods.currTimeAllowJobsRefreshDuringMaintenanceDay();

                        boolean proceed = false;
                        proceed = ((IsMaintenanceHours && currTimeAllowJobsRefreshDuringMaintenanceDay) || !IsMaintenanceHours);

                        switch (position) {
                            case 0:
                                //do nothing
                                if (!Boolean.parseBoolean(ishomesuser)) {
                                    if (!proceed) {
                                        getOwnPlots t = new getOwnPlots(true);
                                        t.execute();
                                    } else {
                                        objGlobalMethods.setGCMPrefs(GcmPreferences, "RouteList", "");
                                        getRoutingList task = new getRoutingList(true);
                                        task.execute();
                                    }
                                }
                                break;
                            case 1:
                                categoryType = 2;
                                categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2DIP);
                                prefName = new String[]{objGlobalVariables.categoryTitlePrefName, objGlobalVariables.categoryTypePrefName};
                                prefvalue = new String[]{
                                        myApp.getAppContext().getString(R.string.item_dashboard2DIP),
                                        "2"
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                if (v == null) {
                                    return;
                                } else {
                                    lblCount = (TextView) v.findViewById(R.id.lblCount);
                                    startRedirectionSQLite(lblCount, 2);
                                }
                                break;
                            case 2:
                                categoryType = 6;
                                categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2PD);
                                prefName = new String[]{objGlobalVariables.categoryTitlePrefName, objGlobalVariables.categoryTypePrefName};
                                prefvalue = new String[]{
                                        myApp.getAppContext().getString(R.string.item_dashboard2PD),
                                        "6"
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                if (v == null) {
                                    return;
                                } else {
                                    lblCount = (TextView) v.findViewById(R.id.lblCount);
                                    startRedirectionSQLite(lblCount, 6);
                                }
                                break;
                        }
                    }
                });
            } else {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i;
                        view.clearFocus();
                        View v;
                        TextView lblCount;
                        String[] prefName;
                        String[] prefvalue;
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                        boolean currTimeAllowJobsRefreshDuringMaintenanceDay = false;
                        if (IsMaintenanceHours)
                            currTimeAllowJobsRefreshDuringMaintenanceDay = objGlobalMethods.currTimeAllowJobsRefreshDuringMaintenanceDay();

                        boolean proceed = false;
                        proceed = ((IsMaintenanceHours && currTimeAllowJobsRefreshDuringMaintenanceDay) || !IsMaintenanceHours);

                        switch (position) {
                            case 0:
                                //do nothing
                                //recal route
                                if (!proceed) {
                                    getOwnPlots t = new getOwnPlots(true);
                                    t.execute();
                                } else {
                                    recalRoute r = new recalRoute();
                                    r.execute();
                                }

                            /*if (pdfclass.hasUnbrickPDFfile() ) {
                                pdfclass.savefile();
                            } else {
                                objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.btn_ok), "No PDF viewer app.").show();
                            }*/
                                break;
                        /*case 1:
                            //recal route
                            recalRoute r = new recalRoute();
                            r.execute();*/
                            case 1:
                                if (!Boolean.parseBoolean(ishomesuser)) {
                                    if (!proceed) {
                                        getOwnPlots t = new getOwnPlots(true);
                                        t.execute();
                                    } else {
                                        objGlobalMethods.setGCMPrefs(GcmPreferences, "RouteList", "");
                                        getRoutingList task = new getRoutingList(true);
                                        task.execute();
                                    }
                                }
                                //openpdf();
                                break;
                            case 2:
                                if (Boolean.parseBoolean(ishomesuser)) {

                                } else {
                                    if (!proceed) {
                                        getOwnPlots t = new getOwnPlots(false);
                                        t.execute();
                                    } else {
                                        objGlobalMethods.setGCMPrefs(GcmPreferences, "RouteList", "");
                                        getRoutingList task = new getRoutingList(false);
                                        task.execute();
                                    }
                                }
                                break;
                            case 3:
                                categoryType = 0;
                                categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2PIP);
                                prefName = new String[]{objGlobalVariables.categoryTitlePrefName, objGlobalVariables.categoryTypePrefName};
                                prefvalue = new String[]{
                                        myApp.getAppContext().getString(R.string.item_dashboard2PIP),
                                        "0"
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                if (v == null) {
                                    return;
                                } else {
                                    lblCount = (TextView) v.findViewById(R.id.lblCount);
                                    startRedirectionSQLite(lblCount, 0);
                                }
                                break;
                            case 4:
                                categoryType = 1;
                                categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2PU);
                                prefName = new String[]{objGlobalVariables.categoryTitlePrefName, objGlobalVariables.categoryTypePrefName};
                                prefvalue = new String[]{
                                        myApp.getAppContext().getString(R.string.item_dashboard2PU),
                                        "1"
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                if (v == null) {
                                    return;
                                } else {
                                    lblCount = (TextView) v.findViewById(R.id.lblCount);
                                    startRedirectionSQLite(lblCount, 1);
                                }
                                break;
                            case 5:
                                categoryType = 2;
                                categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2DIP);
                                prefName = new String[]{objGlobalVariables.categoryTitlePrefName, objGlobalVariables.categoryTypePrefName};
                                prefvalue = new String[]{
                                        myApp.getAppContext().getString(R.string.item_dashboard2DIP),
                                        "2"
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                if (v == null) {
                                    return;
                                } else {
                                    lblCount = (TextView) v.findViewById(R.id.lblCount);
                                    startRedirectionSQLite(lblCount, 2);
                                }
                                break;
                            case 6:
                                categoryType = 3;
                                categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2OKP);
                                prefName = new String[]{objGlobalVariables.categoryTitlePrefName, objGlobalVariables.categoryTypePrefName};
                                prefvalue = new String[]{
                                        myApp.getAppContext().getString(R.string.item_dashboard2OKP),
                                        "3"
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                if (v == null) {
                                    return;
                                } else {
                                    lblCount = (TextView) v.findViewById(R.id.lblCount);
                                    startRedirectionSQLite(lblCount, 3);
                                }
                                break;
                            case 7:
                                categoryType = 4;
                                categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2FP);
                                prefName = new String[]{objGlobalVariables.categoryTitlePrefName, objGlobalVariables.categoryTypePrefName};
                                prefvalue = new String[]{
                                        myApp.getAppContext().getString(R.string.item_dashboard2FP),
                                        "4"
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                if (v == null) {
                                    return;
                                } else {
                                    lblCount = (TextView) v.findViewById(R.id.lblCount);
                                    startRedirectionSQLite(lblCount, 4);
                                }
                                break;
                            case 8:
                                categoryType = 5;
                                categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2FD);
                                prefName = new String[]{objGlobalVariables.categoryTitlePrefName, objGlobalVariables.categoryTypePrefName};
                                prefvalue = new String[]{
                                        myApp.getAppContext().getString(R.string.item_dashboard2FD),
                                        "5"
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                if (v == null) {
                                    return;
                                } else {
                                    lblCount = (TextView) v.findViewById(R.id.lblCount);
                                    startRedirectionSQLite(lblCount, 5);
                                }
                                break;
                            case 9:
                                categoryType = 6;
                                categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2PD);
                                prefName = new String[]{objGlobalVariables.categoryTitlePrefName, objGlobalVariables.categoryTypePrefName};
                                prefvalue = new String[]{
                                        myApp.getAppContext().getString(R.string.item_dashboard2PD),
                                        "6"
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                if (v == null) {
                                    return;
                                } else {
                                    lblCount = (TextView) v.findViewById(R.id.lblCount);
                                    startRedirectionSQLite(lblCount, 6);
                                }
                                break;
                            case 10:
                                categoryType = 7;
                                categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2TT);
                                prefName = new String[]{objGlobalVariables.categoryTitlePrefName, objGlobalVariables.categoryTypePrefName};
                                prefvalue = new String[]{
                                        myApp.getAppContext().getString(R.string.item_dashboard2TT),
                                        "7"
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                if (v == null) {
                                    return;
                                } else {
                                    lblCount = (TextView) v.findViewById(R.id.lblCount);
                                    startRedirectionSQLite(lblCount, 7);
                                }
                                break;
                        }
                    }
                });
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity loadListFirst2SQLite. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            if (!IsMaintenanceHours &&
                    (JobsHandler.hasOpsQ() || objGlobalMethods.IsDefaultVRPpage() ||
                            objGlobalMethods.isVRPbySettings() || (!isWalker && isStdRunner && isSatSun) ||
                            (!isWalker && isStdRunner && !isSatSun && isAOHtime))) {
                if (isStdRunner && isSatSun || (isStdRunner && !isSatSun && isAOHtime)) {
                    JobsHandler.redirectToVrpPage2();
                } else {
                    JobsHandler.redirectToVrpPage();
                }
            } else {
                promptReqMPD();
            }
        }
    }

    private void startRedirectionSQLite(TextView lblCount, Integer CatTypeID) {
        Intent i = new Intent(myApp.currentActivity, JobInfoActivity.class);
        int count;
        try {

            if (!lblCount.getText().toString().isEmpty() && !lblCount.getText().toString().toLowerCase().equals("error")) {
                count = Integer.parseInt(lblCount.getText().toString());
                if (categoryType == 0) {
                    if (FCMservice.mNotificationManager != null) {
                        FCMservice.mNotificationManager.cancel(2);
                    }
                    FCMservice.notiNotRead = false;
                }
                if (count > 0 && count > 1) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "pagemode", "dashboard");
                    //more than 1 jobs, go to addresslist
                    i = new Intent(myApp.currentActivity, AddressListNewActivity.class);

                    JobInfoActivity.accessFromDirection = 0;
                    handler.removeCallbacks(runnable);
                    startActivity(i);
                    finish();
                } else if (count > 0 && count == 1) {
                    //only 1 job, go str8 to jobInfo
                    List<dbObjJobs> dbObjJobsListAL = DAO_Jobs.getAlldbObjJobsPerCategeory(categoryType);
                    if (dbObjJobsListAL != null && dbObjJobsListAL.size() > 1) {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "pagemode", "dashboard");
                        i = new Intent(myApp.currentActivity, AddressListNewActivity.class);
                        JobInfoActivity.accessFromDirection = 0;
                        handler.removeCallbacks(runnable);
                        startActivity(i);
                        finish();
                    } else if (dbObjJobsListAL != null && dbObjJobsListAL.size() == 1) {
                        dbObjJobs obj = dbObjJobsListAL.get(0);
                        initRedirectionToJobInfoSQLite2(CatTypeID, obj.getJobInfoJsonStr(), obj.getLAT(), obj.getLNG(), obj.getPostal(),
                                obj.AddressWithPostalNoUnit, obj.AddressWithoutPostal);
                    } else if (dbObjJobsListAL == null || dbObjJobsListAL.size() == 0) {
                        //0 jobs, do nothing
                    }
                } else if (count == 0) {
                    //0 jobs, do nothing
                }
            } else {
                //assume to be 0 jobs, do nothing
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity, startRedirectionSQLite. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            JobsHandler.sendErrorEmail("Exception caught at DrawerMainActivity for " + username + ", startRedirectionSQLite." + "\n" + e.getMessage().toString());
        }
    }

    private void initRedirectionToJobInfoSQLite2(Integer CatTypeID, String hfJobInfoJsonStr, Double LAT, Double LNG, String postal, String address, String addressWithUnit) {
        Gson gson = new Gson();
        objJobInfo JobInfo;
        Boolean aisDunning = false;
        Intent i = new Intent(myApp.currentActivity, JobInfoActivity.class);
        try {
            JobInfo = gson.fromJson(hfJobInfoJsonStr, objJobInfo.class);
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, hfJobInfoJsonStr);

            if (CatTypeID == 0 || CatTypeID == 1 || CatTypeID == 4) {
                String[] prefName = new String[]{"LAT", "LNG", "desPostal", "desAddress"};
                String[] prefvalue = new String[]{String.valueOf(LAT), String.valueOf(LNG), postal, address};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            } else if (CatTypeID == 2 || CatTypeID == 3 || CatTypeID == 5 || CatTypeID == 6 || CatTypeID == 7) {
                aisDunning = JobsHandler.isDunning(JobInfo);
                if (CatTypeID == 2 && aisDunning) {
                    objGlobalMethods.writeToFile(JobInfo.JobNo + " is Dunning.", "", "", "", "");
                    if (JobInfo != null) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", JobsHandler.CannotTakePhoto(JobInfo, 2) ? "1" : "0");
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
                    }

                    String[] prefName = new String[]{objGlobalVariables.currJobInfoIDX, "currJobInfoCN"};
                    String[] prefvalue = new String[]{String.valueOf(JobInfo.JobsIDX), JobInfo.JobNo};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                }

                String[] prefName = new String[]{"LAT", "LNG", "desPostal", "desAddress", "addressWithUnit"};
                String[] prefvalue = new String[]{String.valueOf(LAT), String.valueOf(LNG), postal, address, addressWithUnit};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
            }

            if (categoryType == 3 || categoryType == 4 || categoryType == 5 || categoryType == 6 || categoryType == 7) {
                i = new Intent(myApp.currentActivity, JobInfoActivity.class);
                JobInfoActivity.accessFromDirection = 1;
            } else {
                if (aisDunning) {
                    i = new Intent(myApp.currentActivity, setDunningDeliveredStatusActivity.class);
                    JobInfoActivity.accessFromDirection = 0;
                } else {
                    i = new Intent(myApp.currentActivity, JobInfoActivity.class);
                    JobInfoActivity.accessFromDirection = 1;
                }
            }
            objGlobalMethods.setGCMPrefs(GcmPreferences, "pagemode", "dashboard");
            handler.removeCallbacks(runnable);
            startActivity(i);
            finish();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity initRedirectionToJobInfoSQLite2. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void search(String searchValue) {
        ArrayList al = null;
        List<dbObjJobs> rs = null;
        dbObjJobs obj = null;
        Integer totalCount = 0;
        Integer CatTypeID = -1;
        String Postal = "";
        Intent intent = null;
        String JobInfoJsonStr = "";
        Double LAT = 0.0;
        Double LNG = 0.0;
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            rs = DAO_Jobs.searchJobs(-1, "", searchValue);
            if (rs != null && rs.size() > 0) {
                obj = rs.get(0);
                CatTypeID = obj.getCatTypeID();
                Postal = obj.getPostal();
                totalCount = DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);
                al = new ArrayList();
                al.add(obj);
                al.add(totalCount);
            }

            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            if (al != null && al.size() > 0) {
                if (al.get(0) != null && (Integer) al.get(1) > 0) {
                    obj = (dbObjJobs) al.get(0);
                    totalCount = (Integer) al.get(1);
                    CatTypeID = obj.getCatTypeID();
                    Postal = obj.getPostal();
                    LAT = obj.getLAT();
                    LNG = obj.getLNG();

                    if (CatTypeID == 0) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "NEW PICK UP");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");
                    } else if (CatTypeID == 1) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "PICKED UP");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "1");
                    } else if (CatTypeID == 2) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "NEW DELIVERY");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "2");
                    } else if (CatTypeID == 3) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "DELIVERED");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "3");
                    } else if (CatTypeID == 4) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "FAILED PICKUP");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "4");
                    } else if (CatTypeID == 5) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "FAILED DELIVERY");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "5");
                    } else if (CatTypeID == 6) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "SCHEDULED DELIVERY");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "6");
                    }

                    objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", Postal);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", String.valueOf(LAT));
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", String.valueOf(LNG));

                    if (totalCount == 1) {
                        if (obj.getIsDunning()) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX, String.valueOf(obj.getJobsIDX()));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "currJobInfoCN", obj.getJobNo());

                            Gson gson = new Gson();
                            objJobInfo ji = obj.JobInfoJsonStr == "" ? null : gson.fromJson(obj.JobInfoJsonStr, objJobInfo.class);
                            if (ji != null) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", JobsHandler.CannotTakePhoto(ji, 2) ? "1" : "0");
                            } else {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
                            }

                            intent = new Intent(myApp.currentActivity, setDunningDeliveredStatusActivity.class);
                            JobInfoActivity.accessFromDirection = 0;
                        } else {
                            intent = new Intent(myApp.currentActivity, JobInfoActivity.class);
                            JobInfoActivity.accessFromDirection = 1;
                        }
                        JobInfoJsonStr = obj.getJobInfoJsonStr();
                        objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, JobInfoJsonStr);
                    } else if (totalCount > 1) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "searchedval", searchValue);
                        intent = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
                        JobInfoActivity.accessFromDirection = 0;
                    }

                    if (intent != null) {
                        startActivity(intent);
                    } else {
                        playTone();
                        showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
                    }
                } else {
                    playTone();
                    showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
                }
            } else {
                playTone();
                showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity searchValue. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
        }
    }

    //=========================================sqlite=================================================

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(myApp.getAppContext(),
                    "Permission accepted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(myApp.getAppContext(),
                    "Permission denied", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // TODO: handle Intent
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            String action = intent.getAction();
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                /*byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                String tagID = objGlobalMethods.ByteArrayToHexString(id);
                //tagID = tagID != null && tagID.length() > 0 ? tagID.substring(0,tagID.length()-1) : "ID Not Found!";
                tagID = tagID != null && tagID.length() > 0 ? tagID : "";
                objGlobalMethods.setGCMPrefs(GcmPreferences, "NFCtagScannedVal", tagID != null && !tagID.isEmpty() ? tagID.toUpperCase() : "");

                if (!tagID.isEmpty()) {
                    isFromLocationNFCscan = true;
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "jrpudip", "0");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "loadlistCount", "0");
                    Jobs_Retrieval_Activity.fromPage = "DrawerMainActivity";
                    Intent jrintent = new Intent(this, Jobs_Retrieval_Activity.class);
                    startActivity(jrintent);
                    this.finish();
                }*/
            }

        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity handleIntent. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    public void promptReqMPD() {
        /*objGlobalMethods.promptReqMPD();
        mpdclass.promptRegBTTL();*/
        objGlobalMethods.promptReqMPD_New();
    }

    private class recalRoute extends AsyncTask<Void, Void, Boolean> {
        public recalRoute() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdRecalRoute.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean L2_DeleteProblem = false;
            boolean L2_CreateProblem = false;
            long driveridx = 0;
            try {
                regid = objGlobalMethods.getRegistrationIdOnly();
                if (!objGlobalMethods.IsEmptyString(regid)) {
                    L2_DeleteProblem = WebService.L2_DeleteProblem(regid);
                }
                //L2_DeleteProblem = true;
                if (L2_DeleteProblem) {
                    WebService.L2_DeleteJobQueue(regid);
                    L2_CreateProblem = WebService.L2_CreateProblem(regid);
                }
                if (L2_CreateProblem) {
                    JobsHandler.HandleRouteList(regid, objGlobalMethods.getGcmPreferences());
                    //JobsHandler.getJobQue2(regid, true, true, -1);
                    ArrayList<Integer> ListItemIndex = new ArrayList<>();
                    ListItemIndex.add(-1);

                    /////this part here works differently from vrp page, thus does not empty wat are supposed to be emptied in vrp page portion
                    String JobsPostalsToBeUNPARKEDJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "JobsPostalsToBeUNPARKEDJsonStr");
                    ArrayList<String> JobsPostalsToBeUNPARKED = null;
                    if (!objGlobalMethods.IsEmptyString(JobsPostalsToBeUNPARKEDJsonStr)) {
                        JobsPostalsToBeUNPARKED = new Gson().fromJson(JobsPostalsToBeUNPARKEDJsonStr, new TypeToken<ArrayList<String>>() {
                        }.getType());
                    }
                    /////this part here works differently from vrp page, thus does not empty wat are supposed to be emptied in vrp page portion
                    JobsHandler.getJobQue2(regid, true, true, ListItemIndex, JobsPostalsToBeUNPARKED);
                }
            } catch (Exception e) {
                L2_CreateProblem = false;
                objGlobalMethods.writeToFile("Exception at DrawerMainActivity recalRoute doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return L2_CreateProblem;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            try {
                if (pdRecalRoute != null) {
                    pdRecalRoute.dismiss();
                }
                if (success) {
                    objGlobalMethods.writeToFile("ST_CreateProblem successful.", "", "", "", "");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "RouteList", "");
                    getRoutingList task = new getRoutingList(true);
                    task.execute();
                } else {
                    objGlobalMethods.writeToFile("ST_CreateProblem unsuccessful.", "", "", "", "");
                    //showDialog(myApp.getAppContext().getString(R.string.recalroute_notok)).show();
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at DrawerMainActivity recalRoute onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                if (pdRecalRoute != null) {
                    pdRecalRoute.dismiss();
                }
            }
        }
    }

    private View.OnClickListener fabprobation_OnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Context c = myApp.getAppContext();
                    String probationPeriod = objGlobalMethods.getGCMPrefs(objGlobalMethods.getGcmPreferences(), "probationPeriod");
                    if (!objGlobalMethods.IsEmptyString(probationPeriod)) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        Date pp = objGlobalMethods.JsonDateToDate(probationPeriod);
                        objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.probationperiod) + " " + formatter.format(pp)).show();
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at DrawerMain2Activity fabprobation_OnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at DrawerMainActivity", "", "", "", "");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMainActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

}
