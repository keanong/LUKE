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
import android.graphics.Color;
import android.location.Location;
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

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.data.BleScanState;
import com.xdel.luke.BCnSVC.Download_Data_BroadcastReceiver;
import com.xdel.luke.BCnSVC.SendQueuedEmail_Receiver;
import com.xdel.luke.BottomSheet.vrpUnlockDialog;
import com.xdel.luke.BottomSheet.vrpUnlockPostData;
import com.xdel.luke.Caging.CageScanActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.HRM.ProfileActivity;
import com.xdel.luke.HandTakeOver.HandoverActivity;
import com.xdel.luke.HandTakeOver.TakeoverActivity;
import com.xdel.luke.Locker.L2_LA_GetUserResult;
import com.xdel.luke.PieceByPiecePU.PipPcs;
import com.xdel.luke.PieceByPiecePU.StartQueryActivity;
import com.xdel.luke.Receivers.RecevierMgr;
import com.xdel.luke.Rooting.RootGuard;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.Voip.VoipApp;
import com.xdel.luke.Vrp.JobQueue;
import com.xdel.luke.Vrp.VRPSwipeAndDragHelper2;
import com.xdel.luke.Vrp.VrpList_RecycleViewAdapter;
import com.xdel.luke.Vrp.VrpList_RecycleViewAdapter2;
import com.xdel.luke.credits.credit;
import com.xdel.luke.inventory.InventoryMainActivity;
import com.xdel.luke.messagingClasses.ChatMsgSenders;
import com.xdel.luke.messagingClasses.GetAllMsg_Receiver;
import com.xdel.luke.mpd.MPDMainActivity;
import com.xdel.luke.objClasses.LocaleHelper;
import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.NFCTag;
import com.xdel.luke.objClasses.WifiBroadcastReceiver;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objAuthDriverResult;
import com.xdel.luke.objClasses.objCabinet;
import com.xdel.luke.objClasses.objETACabinet;
import com.xdel.luke.objClasses.objGC_CustomQueue;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objMPD_Info;
import com.xdel.luke.objClasses.objRecipientType;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjCategoryCount;
import com.xdel.luke.sqlite.dbObjJobs;
import com.xdel.luke.sqlite.dbObjTradeInList;
import com.xdel.luke.sqlite.objInsertJobInfo;
import com.xdel.luke.sqlite.objJobsInsertionInfo;
import com.xdel.luke.templogger.TempLoggerRegActivity;
import com.xdel.luke.templogger.temploggerclass;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class DrawerMain2Activity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    //=========================================dashboard code from dashboard2=================================================

    private final Semaphore mutex = new Semaphore(1);
    private Context context;
    ConnectionDetector cd;
    private MenuItem menuItem;
    private String LoginName = "";
    private String LoginPassword = "";
    //private String regid;
    private String cHashstr;
    private int catType;
    private int categoryType = -1;
    private String categoryTitle = "";
    public ProgressDialog pdl;
    private ProgressDialog pd, pdCheck, pdCOD, pdRouting;
    private ProgressDialog pdRecalRoute;
    private String[] val;
    private ArrayList DriverJobsArrayList;
    private AlertDialog ad;
    private RecyclerView rv;
    private static boolean isRefresh;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs, MedsColSP;
    private int queCount = 0;
    private boolean DeAuthDriverResult;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    static AlarmManager manager, manager2;
    private File attachment;
    private EditText txtDashboard2Input;
    private MediaPlayer player;
    static String cameraScannedCode;
    private Boolean refreshQueBooleanString = false;
    private ProgressDialog pdSearch;
    private LinearLayout Dashboard2UnreadMsgLinearLayout, Dashboard2ListviewLinearLayout;
    private LinearLayout LLbtnPercentage, LLbtnOpen, LLpercentage;
    private LinearLayout LLsettings, llptpie, llhidden1, llhidden2;
    String langType = "en";
    private static String showRouteList = "";
    private static Boolean isDunning = false;
    String SENDER_ID = "17277152085";
    public NfcAdapter mNfcAdapter;
    //public static final String MIME_TEXT_PLAIN = "text/plain";
    Boolean isFromLocationNFCscan = false;
    private SwipeRefreshLayout swiperefresh;
    private VrpList_RecycleViewAdapter adapter;
    private VrpList_RecycleViewAdapter2 adapter2;
    public Button btnEditRoute, btnUpdateRoute, btnCancelUpdateRoute, btnResetRoute;
    private TextView lblpercentage, lblpoint;


    private TextView lblp, lblunlock;
    private RelativeLayout rlpercentage, rlunlock;

    ScreenShotContentObserver screenShotContentObserver;
    FloatingActionButton fabprobation;
    private boolean lite;

    //=========================================dashboard code from dashboard2=================================================

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
            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
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
                LLStatus01.setVisibility(View.VISIBLE);
                LLStatus02.setVisibility(View.VISIBLE);
                LLStatus03.setVisibility(View.VISIBLE);
                LLStatus04.setVisibility(View.VISIBLE);
                LLStatus05.setVisibility(View.VISIBLE);
                LLStatus06.setVisibility(View.VISIBLE);
                LLStatus07.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity initMenu. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void menu_btn_click(View v) {
        try {
            if (v != null) {
                Intent i;
                boolean isEditingRoute = false;
                isEditingRoute = objGlobalMethods.isEditingRoute();
                Context ctx = myApp.getAppContext();

                if (v.getId() == R.id.LLbtnPU) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        directToCategory(ctx, isEditingRoute, 1);
                    }
                } else if (v.getId() == R.id.LLbtnOKP) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        directToCategory(ctx, isEditingRoute, 3);
                    }
                } else if (v.getId() == R.id.LLbtnFP) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        directToCategory(ctx, isEditingRoute, 4);
                    }
                } else if (v.getId() == R.id.LLbtnFD) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        directToCategory(ctx, isEditingRoute, 5);
                    }
                } else if (v.getId() == R.id.LLbtnPD) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        directToCategory(ctx, isEditingRoute, 6);
                    }
                } else if (v.getId() == R.id.LLbtnTT) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        directToCategory(ctx, isEditingRoute, 7);
                    }
                } else if (v.getId() == R.id.LLbtnbulkpu) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        BulkPickUpActivity.scannedItems = null;
                        useridx = "D" + useridx;
                        String SCANNEDCURRENTID = useridx.toUpperCase() + "|" + username.toUpperCase();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "BulkPickUpScannedIDnUsername", SCANNEDCURRENTID);
                        i = new Intent(DrawerMain2Activity.this, BulkPickUpActivity.class);
                        DrawerMainActivity.handler.removeCallbacks(runnable);
                        startActivity(i);
                        finish();
                    }
                } else if (v.getId() == R.id.LLbtnjobsheet) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        if (Boolean.parseBoolean(ishomesuser)) {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "jobsheetSubmitscannedItemsPrefStr", "");
                            i = new Intent(DrawerMain2Activity.this, JobsheetActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            JobsheetSubmitActivity.scannedItems = null;
                            useridx = "D" + useridx;
                            String SCANNEDCURRENTID2 = useridx.toUpperCase() + "|" + username.toUpperCase();

                            String[] prefName = new String[]{"JobsheetScannedIDnUsername", "JobsheetScannedUsername", "jobsheetSubmitscannedItemsPrefStr"};
                            String[] prefvalue = new String[]{SCANNEDCURRENTID2, username.toUpperCase(), ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                            i = new Intent(DrawerMain2Activity.this, JobsheetSubmitActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                } else if (v.getId() == R.id.LLbtncageout) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        i = new Intent(DrawerMain2Activity.this, CageScanActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else if (v.getId() == R.id.LLbtnshuttle) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
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
                    }
                } else if (v.getId() == R.id.LLbtnhandover) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        HandoverActivity.scannedItems = null;
                        String HOScannedIDnUsername = useridx;
                        HandoverActivity.hotoqrlist = null;
                        String[] prefName3 = new String[]{"HOScannedIDnUsername", "verifyTOQR"};
                        String[] prefvalue3 = new String[]{HOScannedIDnUsername, ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName3, prefvalue3);

                        i = new Intent(myApp.currentActivity, HandoverActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else if (v.getId() == R.id.LLbtntakeover) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        TakeoverActivity.scannedItems = null;
                        String TOScannedIDnUsername = useridx.toUpperCase();
                        String[] prefName4 = new String[]{"TOScannedIDnUsername", "scanTOqr", "TOQRimgB64Str", "to_json"};
                        String[] prefvalue4 = new String[]{TOScannedIDnUsername, "", "", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName4, prefvalue4);

                        i = new Intent(myApp.currentActivity, TakeoverActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else if (v.getId() == R.id.LLbtncod) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        getCODList getCODListtask = new getCODList();
                        getCODListtask.execute();
                    }
                } else if (v.getId() == R.id.LLbtnmsg) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        i = new Intent(DrawerMain2Activity.this, Messaginglist.class);
                        DrawerMainActivity.handler.removeCallbacks(runnable);
                        List<ChatMsgSenders> chatmsgsendersAL = null;
                        chatmsgsendersAL = DAO_Jobs.getChatMsgSendersList();
                        if (chatmsgsendersAL != null && chatmsgsendersAL.size() > 0) {
                            startActivity(i);
                            finish();
                        } else {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "unreadMsgExist", "false");
                            showDialog(ctx.getString(R.string.alert_nomsg));
                        }
                    }
                } else if (v.getId() == R.id.LLbtncreditcardimport) {
                    String citidbshsbc = "0";
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        CreditCardActivity.scannedItems = null;
                        i = new Intent(DrawerMain2Activity.this, CreditCardActivity.class);
                        DrawerMainActivity.handler.removeCallbacks(runnable);
                        citidbshsbc = "0";
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "citidbshsbc", citidbshsbc);
                        startActivity(i);
                        finish();
                    }
                } else if (v.getId() == R.id.LLbtnmpd) {
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        i = new Intent(DrawerMain2Activity.this, MPD_CheckOutActivity.class);
                        startActivity(i);
                        finish();
                    }
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
                    drawerLayout.closeDrawers();
                    if (!isEditingRoute) {
                        LogOutNew();
                    }
                }

                /*switch (v.getId()) {
                    case R.id.LLbtnPU:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            directToCategory(ctx, isEditingRoute, 1);
                        }
                        break;
                    case R.id.LLbtnOKP:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            directToCategory(ctx, isEditingRoute, 3);
                        }
                        break;
                    case R.id.LLbtnFP:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            directToCategory(ctx, isEditingRoute, 4);
                        }
                        break;
                    case R.id.LLbtnFD:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            directToCategory(ctx, isEditingRoute, 5);
                        }
                        break;
                    case R.id.LLbtnPD:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            directToCategory(ctx, isEditingRoute, 6);
                        }
                        break;
                    case R.id.LLbtnTT:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            directToCategory(ctx, isEditingRoute, 7);
                        }
                        break;
                    case R.id.LLbtnbulkpu:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            BulkPickUpActivity.scannedItems = null;
                            useridx = "D" + useridx;
                            String SCANNEDCURRENTID = useridx.toUpperCase() + "|" + username.toUpperCase();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "BulkPickUpScannedIDnUsername", SCANNEDCURRENTID);
                            i = new Intent(DrawerMain2Activity.this, BulkPickUpActivity.class);
                            DrawerMainActivity.handler.removeCallbacks(runnable);
                            startActivity(i);
                            finish();
                        }
                        break;
                    case R.id.LLbtnjobsheet:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            if (Boolean.parseBoolean(ishomesuser)) {
                                GcmPreferences = objGlobalMethods.getGcmPreferences();
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "jobsheetSubmitscannedItemsPrefStr", "");
                                i = new Intent(DrawerMain2Activity.this, JobsheetActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                GcmPreferences = objGlobalMethods.getGcmPreferences();
                                JobsheetSubmitActivity.scannedItems = null;
                                useridx = "D" + useridx;
                                String SCANNEDCURRENTID2 = useridx.toUpperCase() + "|" + username.toUpperCase();

                                String[] prefName = new String[]{"JobsheetScannedIDnUsername", "JobsheetScannedUsername", "jobsheetSubmitscannedItemsPrefStr"};
                                String[] prefvalue = new String[]{SCANNEDCURRENTID2, username.toUpperCase(), ""};
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                i = new Intent(DrawerMain2Activity.this, JobsheetSubmitActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                        break;
                    case R.id.LLbtncageout:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            i = new Intent(DrawerMain2Activity.this, CageScanActivity.class);
                            startActivity(i);
                            finish();
                        }
                        break;
                    case R.id.LLbtnshuttle:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
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
                        }
                        break;
                    case R.id.LLbtnhandover:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            HandoverActivity.scannedItems = null;
                            String HOScannedIDnUsername = useridx;
                            HandoverActivity.hotoqrlist = null;
                            String[] prefName3 = new String[]{"HOScannedIDnUsername", "verifyTOQR"};
                            String[] prefvalue3 = new String[]{HOScannedIDnUsername, ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName3, prefvalue3);

                            i = new Intent(myApp.currentActivity, HandoverActivity.class);
                            startActivity(i);
                            finish();
                        }
                        break;
                    case R.id.LLbtntakeover:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            TakeoverActivity.scannedItems = null;
                            String TOScannedIDnUsername = useridx.toUpperCase();
                            String[] prefName4 = new String[]{"TOScannedIDnUsername", "scanTOqr", "TOQRimgB64Str", "to_json"};
                            String[] prefvalue4 = new String[]{TOScannedIDnUsername, "", "", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName4, prefvalue4);

                            i = new Intent(myApp.currentActivity, TakeoverActivity.class);
                            startActivity(i);
                            finish();
                        }
                        break;
                    case R.id.LLbtncod:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            getCODList getCODListtask = new getCODList();
                            getCODListtask.execute();
                        }
                        break;
                    case R.id.LLbtnmsg:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            i = new Intent(DrawerMain2Activity.this, Messaginglist.class);
                            DrawerMainActivity.handler.removeCallbacks(runnable);
                            List<ChatMsgSenders> chatmsgsendersAL = null;
                            chatmsgsendersAL = DAO_Jobs.getChatMsgSendersList();
                            if (chatmsgsendersAL != null && chatmsgsendersAL.size() > 0) {
                                startActivity(i);
                                finish();
                            } else {
                                GcmPreferences = objGlobalMethods.getGcmPreferences();
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "unreadMsgExist", "false");
                                showDialog(ctx.getString(R.string.alert_nomsg));
                            }
                        }
                        break;
                    case R.id.LLbtncreditcardimport:
                        String citidbshsbc = "0";
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            CreditCardActivity.scannedItems = null;
                            i = new Intent(DrawerMain2Activity.this, CreditCardActivity.class);
                            DrawerMainActivity.handler.removeCallbacks(runnable);
                            citidbshsbc = "0";
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "citidbshsbc", citidbshsbc);
                            startActivity(i);
                            finish();
                        }
                        break;
                    case R.id.LLbtnmpd:
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            i = new Intent(DrawerMain2Activity.this, MPD_CheckOutActivity.class);
                            startActivity(i);
                            finish();
                        }
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
                        drawerLayout.closeDrawers();
                        if (!isEditingRoute) {
                            LogOutNew();
                        }
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
            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
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

            ImageView imgProfilePix = (ImageView) header.findViewById(R.id.imgProfilePix);
            if (imgProfilePix != null) {
                if (GcImg != null) {
                    imgProfilePix.setImageBitmap(BitmapFactory.decodeByteArray(GcImg, 0, GcImg.length));
                }
                imgProfilePix.setOnClickListener(imgProfilePixOnClick());
            }

            if (lite)
                imgProfilePix.setOnClickListener(null);

            drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(myApp.currentActivity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

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

    private void setMenuText() {
        try {
            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
            updateMainBadges(navigationView);
            updateDrawerJSCount(navigationView);
            updateDrawerCODNew(navigationView);
            setMsgCountNew(navigationView);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity setMenuText. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void updateMainBadges(NavigationView navigationView) {
        View header = null;
        try {
            if (navigationView == null) {
                navigationView = (NavigationView) findViewById(R.id.navigation_view);
            }
            header = navigationView.getHeaderView(0);
            TextView txtPUbadge = (TextView) header.findViewById(R.id.txtPUbadge);
            TextView txtOKPbadge = (TextView) header.findViewById(R.id.txtOKPbadge);
            TextView txtFPbadge = (TextView) header.findViewById(R.id.txtFPbadge);
            TextView txtFDbadge = (TextView) header.findViewById(R.id.txtFDbadge);
            TextView txtPDbadge = (TextView) header.findViewById(R.id.txtPDbadge);
            TextView txtTTbadge = (TextView) header.findViewById(R.id.txtTTbadge);

            List<dbObjCategoryCount> dbObjCategoryCountAL = DAO_Jobs.getCategoryCount();
            dbObjCategoryCount obj = null;
            int puc = 0;
            int okpc = 0;
            int fpc = 0;
            int fdc = 0;
            int pdc = 0;
            int ttc = 0;
            if (dbObjCategoryCountAL != null && dbObjCategoryCountAL.size() > 0) {
                for (int i = 0; i <= dbObjCategoryCountAL.size() - 1; i++) {
                    obj = dbObjCategoryCountAL.get(i);
                    if (obj != null) {
                        if (obj.CatTypeID == 1) {
                            puc = obj.getCount();
                        } else if (obj.CatTypeID == 3) {
                            okpc = obj.getCount();
                        } else if (obj.CatTypeID == 4) {
                            fpc = obj.getCount();
                        } else if (obj.CatTypeID == 5) {
                            fdc = obj.getCount();
                        } else if (obj.CatTypeID == 6) {
                            pdc = obj.getCount();
                        } else if (obj.CatTypeID == 7) {
                            ttc = obj.getCount();
                        }
                    }
                }
            }

            txtPUbadge.setText(String.valueOf(puc));
            txtOKPbadge.setText(String.valueOf(okpc));
            txtFPbadge.setText(String.valueOf(fpc));
            txtFDbadge.setText(String.valueOf(fdc));
            txtPDbadge.setText(String.valueOf(pdc));
            txtTTbadge.setText(String.valueOf(ttc));

            txtPUbadge.setVisibility(puc > 0 ? View.VISIBLE : View.GONE);
            txtOKPbadge.setVisibility(okpc > 0 ? View.VISIBLE : View.GONE);
            txtFPbadge.setVisibility(fpc > 0 ? View.VISIBLE : View.GONE);
            txtFDbadge.setVisibility(fdc > 0 ? View.VISIBLE : View.GONE);
            txtPDbadge.setVisibility(pdc > 0 ? View.VISIBLE : View.GONE);
            txtTTbadge.setVisibility(ttc > 0 ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity updateMainBadges. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity updateDrawerJSCount. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
                objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity updateDrawerCODNew codval conversion. Exception: " + e.getMessage().toString(), "", "", "", "");
                codval = 0.0;
            }
            DecimalFormat dec = new DecimalFormat("#0.00");

            txtCODbadge.setText("$" + dec.format(codval));
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity updateDrawerCODNew. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity setMsgCountNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void directToCategory(final Context ctx, boolean isEditingRoute, int typeid) {
        try {
            if (!isEditingRoute) {
                List<dbObjJobs> dbObjJobsListAL = DAO_Jobs.getAlldbObjJobsPerCategeory(typeid);
                if (dbObjJobsListAL != null && dbObjJobsListAL.size() > 0) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName,
                            typeid == 1 ? ctx.getString(R.string.item_dashboard2PU) :
                                    typeid == 3 ? ctx.getString(R.string.item_dashboard2OKP) :
                                            typeid == 4 ? ctx.getString(R.string.item_dashboard2FP) :
                                                    typeid == 5 ? ctx.getString(R.string.item_dashboard2FD) :
                                                            typeid == 6 ? ctx.getString(R.string.item_dashboard2PD) :
                                                                    ctx.getString(R.string.item_dashboard2TT)
                    );
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName,
                            typeid == 1 ? "1" :
                                    typeid == 3 ? "3" :
                                            typeid == 4 ? "4" :
                                                    typeid == 5 ? "5" :
                                                            typeid == 6 ? "6" :
                                                                    "7"
                    );
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "fromSubMenu", "true");
                    if (dbObjJobsListAL != null && dbObjJobsListAL.size() > 1) {
                        Intent i = new Intent(DrawerMain2Activity.this, AddressListNewActivity.class);
                        JobInfoActivity.accessFromDirection = 0;
                        startActivity(i);
                        finish();
                    } else if (dbObjJobsListAL != null && dbObjJobsListAL.size() == 1) {
                        dbObjJobs obj = dbObjJobsListAL.get(0);
                        initRedirectionToJobInfoSQLite2(typeid, obj.getJobInfoJsonStr(), obj.getLAT(), obj.getLNG(), obj.getPostal(),
                                obj.AddressWithPostalNoUnit, obj.AddressWithoutPostal);
                    }
                } else {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_nojobfound)).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity directToCategory when typeid = " + String.valueOf(typeid) + ". Exception: " + s, "", "", "", "");
        }
    }

    private void initRedirectionToJobInfoSQLite2(Integer CatTypeID, String hfJobInfoJsonStr, Double LAT, Double LNG, String postal, String address, String addressWithUnit) {
        Gson gson = new Gson();
        objJobInfo JobInfo;
        Boolean aisDunning = false;
        Intent i = new Intent(DrawerMain2Activity.this, JobInfoActivity.class);
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
                i = new Intent(DrawerMain2Activity.this, JobInfoActivity.class);
                JobInfoActivity.accessFromDirection = 1;
            } else {
                if (aisDunning) {
                    i = new Intent(DrawerMain2Activity.this, setDunningDeliveredStatusActivity.class);
                    JobInfoActivity.accessFromDirection = 0;
                } else {
                    i = new Intent(DrawerMain2Activity.this, JobInfoActivity.class);
                    JobInfoActivity.accessFromDirection = 1;
                }
            }
            startActivity(i);
            finish();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity initRedirectionToJobInfoSQLite2. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void initDashboardMenu(SharedPreferences gcmp) {
        Context ctx = myApp.getAppContext();
        try {
            if (!Boolean.parseBoolean(ishomesuser)) {
                showRouteList = objGlobalMethods.getGCMPrefs(gcmp, "showRouteList");
                showRouteList = "true";
                objGlobalMethods.setGCMPrefs(gcmp, "showRouteList", "true");
                if (showRouteList != null && !showRouteList.equals("")) {
                    val = new String[]{
                            ctx.getString(R.string.title_activity_allMap),
                            ctx.getString(R.string.item_routing),
                            ctx.getString(R.string.item_dashboard2PIP),
                            ctx.getString(R.string.item_dashboard2PU),
                            ctx.getString(R.string.item_dashboard2DIP),
                            ctx.getString(R.string.item_dashboard2OKP),
                            ctx.getString(R.string.item_dashboard2FP),
                            ctx.getString(R.string.item_dashboard2FD),
                            ctx.getString(R.string.item_dashboard2PD)};
                } else {
                    val = new String[]{
                            ctx.getString(R.string.item_dashboard2PIP),
                            ctx.getString(R.string.item_dashboard2PU),
                            ctx.getString(R.string.item_dashboard2DIP),
                            ctx.getString(R.string.item_dashboard2OKP),
                            ctx.getString(R.string.item_dashboard2FP),
                            ctx.getString(R.string.item_dashboard2FD),
                            ctx.getString(R.string.item_dashboard2PD)};
                }
            } else {
                val = new String[]{
                        ctx.getString(R.string.item_dashboard2JOBSHEEThomes),
                        ctx.getString(R.string.item_dashboard2STATUS)
                };
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity initDashboardMenu. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");

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

            setContentView(R.layout.activity_drawer_main2_new);

            toolbar = (Toolbar) findViewById(R.id.toolbar);

            //progress dialog for upon login to load items and
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));

            pdl = new ProgressDialog(this);
            pdl.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdl.setCanceledOnTouchOutside(false);
            pdl.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));

            pdSearch = new ProgressDialog(this);
            pdSearch.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdSearch.setCanceledOnTouchOutside(false);
            pdSearch.setMessage(myApp.getAppContext().getString(R.string.pd_waitSearch));

            rv = (RecyclerView) findViewById(R.id.rv);
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(this));
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

            btnEditRoute = (Button) findViewById(R.id.btnEditRoute);
            btnUpdateRoute = (Button) findViewById(R.id.btnUpdateRoute);
            btnCancelUpdateRoute = (Button) findViewById(R.id.btnCancelUpdateRoute);
            btnResetRoute = (Button) findViewById(R.id.btnResetRoute);
            lblpercentage = findViewById(R.id.lblpercentage);
            lblpoint = findViewById(R.id.lblpoint);
            llptpie = findViewById(R.id.llptpie);
            llhidden1 = findViewById(R.id.llhidden1);
            llhidden2 = findViewById(R.id.llhidden2);

            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

            LLpercentage = findViewById(R.id.LLpercentage);
            LLbtnPercentage = findViewById(R.id.LLbtnPercentage);
            lblp = findViewById(R.id.lblp);
            LLbtnOpen = findViewById(R.id.LLbtnOpen);
            lblunlock = findViewById(R.id.lblunlock);
            rlpercentage = findViewById(R.id.rlpercentage);
            rlunlock = findViewById(R.id.rlunlock);

            fabprobation = (FloatingActionButton) findViewById(R.id.fabprobation);

            RecevierMgr.regScreenOnOffReceiverNew();
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DrawerMainAcitivty onCreate. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
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
                //setTitle(myApp.getAppContext().getString(R.string.title_activity_dashboard3));
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

                setSupportActionBar(toolbar);
                /*hideNavigationItems();
                initNavigationDrawer();*/
                initMenu();
                initNavigationDrawerNew();
                initDashboardMenu(GcmPreferences);
                changeActiveNotiLang(username);

                String RegID = objGlobalMethods.getRegistrationIdOnly();
                cd = new ConnectionDetector(null);
                categoryType = -1;
                String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, "fromSubMenu", "MapViewedPostal",
                        "DigiAgreementcreateViewTime", "pagemode",
                        "InvMainMode", "InvSubMode", "InvTallyMode", "InventoryBatchIDStr", "InvTallyQtyCanUndo", "Batch_ID", "StockCodeMode",
                        "showMPD_Dialog_IsShowing", "showRegBTTL_Dialog_IsShowing", "FailAsND", "FailAsND_Reason"};
                String[] prefvalue = new String[]{"-1", "", "",
                        "", "vrp",
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

                SharedPreferences vrpsp = objGlobalMethods.getVrpRelatedSharedPreferences();
                objGlobalMethods.setcredEditedDateLong(vrpsp);

                JobsHandler3.clearShpmtVSP(null);
                JobsHandler3.clearMedsColSP(MedsColSP);
                JobsHandler3.clearPassportSP(null);
                JobsHandler3.clearMpdSP();

                isRefresh = false;

                objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                objGlobalMethods.resetJobsListGroupScrollIndexNpostion(GcmPreferences);
                objGlobalMethods.offSpeaker();

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
                    objGlobalMethods.writeToFile("DrawerMain2Activity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                try {
                    if (mNfcAdapter != null && myApp.isNfcEnabled(mNfcAdapter, context)) {
                        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
                        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
                        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};
                        PendingIntent nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                        mNfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilter, null);
                    }
                } catch (Exception e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity onResume mNfcAdapter portion. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
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
                //boolean idledMoreThan3Hrs = false;
                if (idledMoreThan3Hrs) {
                    JobsHandler.ManualForceLogout();
                }

                if (cd.isConnectingToInternet()) {
                    objGlobalMethods.requireGcReLogin();
                }

                objGlobalMethods.checkGPSnPerMinRunAlarmRunning(ishomesuser);
                DAO_Jobs.initdbHelper(this);

                boolean isNearHendersonOfcNew = objGlobalMethods.isNearHendersonOfcNew(GcmPreferences);
                isNearHendersonOfcNew = false;
                if (Boolean.parseBoolean(ishomesuser)) {
                    if (!cd.isConnectingToInternet()) {
                        Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                        if (DrawerMainActivity.DashboardOpenFromPush) {
                            if (FCMservice.mNotificationManager != null) {
                                FCMservice.mNotificationManager.cancel(2);
                            }
                            FCMservice.notiNotRead = false;
                            DrawerMainActivity.DashboardOpenFromPush = false;
                        }
                    }
                } else {
                    boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                    if (!objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours) {
                        Context ctx = myApp.getAppContext();
                        Intent i = new Intent(ctx, DrawerMainActivity.class);
                        myApp.currentActivity.startActivity(i);
                        finish();
                        return;
                    }

                    if (!cd.isConnectingToInternet()) {
                        Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                        if (DrawerMainActivity.DashboardOpenFromPush) {
                            if (FCMservice.mNotificationManager != null) {
                                FCMservice.mNotificationManager.cancel(2);
                            }
                            FCMservice.notiNotRead = false;
                            DrawerMainActivity.DashboardOpenFromPush = false;
                        }

                        loadVrpList2(isNearHendersonOfcNew, false);

                        //JobsHandler.doubleCheckUpdatePIPSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx, false);
                        JobsHandler.doubleCheckUpdatePIPSQLiteNew(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);
                    } else {
                        if (getStoredStatusCodes(context, "en").equals("") || getStoredStatusCodes(context, "zh").equals("")) {
                            downloadStatusCodes();
                        }

                        if (objGlobalMethods.getStoredRecipientTypes(GcmPreferences).equals("")) {
                            JobsHandler.downloadRecipientTypesWS task = new JobsHandler.downloadRecipientTypesWS(RegID, GcmPreferences);
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

                        if (DrawerMainActivity.DashboardOpenFromPush) {
                            if (FCMservice.mNotificationManager != null) {
                                FCMservice.mNotificationManager.cancel(2);
                            }
                            FCMservice.notiNotRead = false;
                            if (refreshQueBooleanString) {
                                refresh();
                            } else {
                                loadVrpList2(isNearHendersonOfcNew, false);
                            }

                            //check for any new job ye to be updated to PIP
                            //JobsHandler.doubleCheckUpdatePIPSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx, false);
                            JobsHandler.doubleCheckUpdatePIPSQLiteNew(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);
                            DrawerMainActivity.DashboardOpenFromPush = false;
                        } else if (MainActivity.uponLogin == 1) {
                            if (!Boolean.parseBoolean(ishomesuser)) {
                                sendBroadcast(new Intent(context, WifiBroadcastReceiver.class));
                                objGlobalMethods.writeToFile("DrawerMain2Activity MainActivity.uponLogin == 1. calling GetDriverJobsCountSQLite.", "", "", "", "");
                                if (RegID.equals("")) {
                                    RegID = objGlobalMethods.getRegistrationIdOnly();
                                }
                                isRefresh = true;
                                GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);
                                task.execute();
                                temploggerclass.decideToStartTempTimer(null, "DrawerMain2Activity");
                                //double check for any new job yet to be updated to PIP is done at the end of GetDriverJobsCount2
                            } else {
                                loadVrpList2(isNearHendersonOfcNew, false);
                            }
                            //double check for any new job yet to be updated to PIP is done at the end of GetDriverJobsCount2*/
                        } else if (!DrawerMainActivity.updateJobInfoStatusTrueFalse) {
                            if (!Boolean.parseBoolean(ishomesuser)) {
                                objGlobalMethods.writeToFile("DrawerMain2Activity updateJobInfoStatusTrueFalse is false. calling GetDriverJobsCountSQLite.", "", "", "", "");
                                DrawerMainActivity.updateJobInfoStatusTrueFalse = true;
                                if (RegID.equals("")) {
                                    RegID = objGlobalMethods.getRegistrationIdOnly();
                                }
                                GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);
                                task.execute();
                                //double check for any new job yet to be updated to PIP is done at the end of GetDriverJobsCount2
                            }
                        } else {
                            if (refreshQueBooleanString) {
                                refresh();
                            } else {
                                loadVrpList2(isNearHendersonOfcNew, false);
                            }
                            //check for any new job yet to be updated to PIP
                            if (RegID.equals("")) {
                                RegID = objGlobalMethods.getRegistrationIdOnly();
                            }
                            //JobsHandler.doubleCheckUpdatePIPSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx, false);
                            JobsHandler.doubleCheckUpdatePIPSQLiteNew(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);
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
                objGlobalMethods.getMsgReminder(GcmPreferences, "DrawerMain2Activity", Dashboard2UnreadMsgLinearLayout, Dashboard2ListviewLinearLayout);

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
                                    rv.clearFocus();
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

                rv.clearFocus();
                txtDashboard2Input.clearFocus();
                txtDashboard2Input.setText("");
                txtDashboard2Input.requestFocus();
                objGlobalMethods.checkToPromptNotiForBattCharging();
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !objGlobalMethods.isActiveServiceRunning()) {
                    objGlobalMethods.startActiveForegroundSvc("");
                }

                int qe = JobsHandler.numOfQueuedEmails();

                lblpercentage.setOnClickListener(null);

                if (qe > 0) {
                    objGlobalMethods.writeToFile("SendQueuedEmailReceiver called at DrawerMain2Activity.", "", "", "", "");
                    Intent SendQueuedEmailReceiver = new Intent(context, SendQueuedEmail_Receiver.class);
                    sendBroadcast(SendQueuedEmailReceiver);
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawer_main2, menu);

        if (Boolean.parseBoolean(ishomesuser)) {
            menu.findItem(R.id.refresh).setVisible(false);
            menu.findItem(R.id.dash).setVisible(false);
            menu.findItem(R.id.map).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent parentIntent;
        cd = new ConnectionDetector(null);
        boolean isEditingRoute = false;
        isEditingRoute = objGlobalMethods.isEditingRoute();

        if (item.getItemId() == R.id.map) {
            if (!isEditingRoute) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, "nonSysCalRoute", "");
                Intent v = new Intent(this, MapsAllActivity.class);
                startActivity(v);
                finish();
            }

            //objGlobalMethods.clearCredSP(null);

            //objGlobalMethods.setVrpRelatedSP_KeyValue2(objGlobalMethods.getVrpRelatedSharedPreferences(), "credEditedDateLong", "1754462264929");
        } else if (item.getItemId() == R.id.dash) {
            if (!isEditingRoute) {
                if (JobsHandler.hasOpsQ()) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.opsqueset)).show();
                } else if (objGlobalMethods.isVRPbySettings()) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.isvrpbysetting)).show();
                } else {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "pagemode", "dashboard");
                    objGlobalMethods.writeToFile("Switched to Summary Mode.", "", "", "", "");
                    Intent i = new Intent(this, DrawerMainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        } else if (item.getItemId() == R.id.refresh) {
            if (!isEditingRoute) {
                if (cd.isConnectingToInternet()) {
                    isRefresh = true;
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                    String RegID = objGlobalMethods.getRegistrationIdOnly();
                    GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);
                    task.execute();
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_cannotRefreshNoInternet));
                }
            }
        } else if (item.getItemId() == R.id.qrscan) {
            if (!isEditingRoute) {
                launchQRScan();
            }
        } else if (item.getItemId() == R.id.dashboard2Language) {
            if (!isEditingRoute) {
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

                    /*if (cd.isConnectingToInternet()) {
                        isRefresh = true;
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                        String RegID = objGlobalMethods.getRegistrationIdOnly();
                        GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);
                        task.setfinalresetQ(true);
                        task.execute();
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_cannotRefreshNoInternet));
                    }*/
            }
        }

        /*switch (item.getItemId()) {
            case R.id.map:
                if (!isEditingRoute) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "nonSysCalRoute", "");
                    Intent v = new Intent(this, MapsAllActivity.class);
                    startActivity(v);
                    finish();
                }

                //objGlobalMethods.clearCredSP(null);

                //objGlobalMethods.setVrpRelatedSP_KeyValue2(objGlobalMethods.getVrpRelatedSharedPreferences(), "credEditedDateLong", "1754462264929");
                break;
            case R.id.dash:
                if (!isEditingRoute) {
                    if (JobsHandler.hasOpsQ()) {
                        objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.opsqueset)).show();
                    } else if (objGlobalMethods.isVRPbySettings()) {
                        objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.isvrpbysetting)).show();
                    } else {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "pagemode", "dashboard");
                        objGlobalMethods.writeToFile("Switched to Summary Mode.", "", "", "", "");
                        Intent i = new Intent(this, DrawerMainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
                break;
            case R.id.refresh:
                if (!isEditingRoute) {
                    if (cd.isConnectingToInternet()) {
                        isRefresh = true;
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                        String RegID = objGlobalMethods.getRegistrationIdOnly();
                        GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);
                        task.execute();
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_cannotRefreshNoInternet));
                    }
                }
                break;
            case R.id.qrscan:
                if (!isEditingRoute) {
                    launchQRScan();
                }
                break;
            case R.id.dashboard2Language:
                if (!isEditingRoute) {
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

                    *//*if (cd.isConnectingToInternet()) {
                        isRefresh = true;
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                        String RegID = objGlobalMethods.getRegistrationIdOnly();
                        GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);
                        task.setfinalresetQ(true);
                        task.execute();
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_cannotRefreshNoInternet));
                    }*//*
                }
        }*/
        return super.onOptionsItemSelected(item);
    }

    //=========================================dashboard code from dashboard2=================================================

    private void changeActiveNotiLang(String name) {
        String langChangeStr = "false";
        Boolean langChange = false;
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
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
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "langChangeStr", langChangeStr);
        }
    }

    public void Dashboard2UnreadMsgtextClick(View view) {
        Intent parentIntent = new Intent(this, Messaginglist.class);
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
        String[] prefName = new String[]{"QRScanRequestIntent", "cameraScannedCode"};
        String[] prefvalue = new String[]{"dashboard", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        parentIntent = new Intent(DrawerMain2Activity.this, QRBarcodeReader.class);
        DrawerMainActivity.handler.removeCallbacks(runnable);
        startActivity(parentIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
        pdCheck.dismiss();
        if (pdl != null && pdl.isShowing()) {
            pdl.dismiss();
            pdl = null;
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
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
                objGlobalMethods.writeToFile("Exception at DrawerMain2Activity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }

            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at DrawerMain2Activity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
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
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        /*boolean isEditingRoute = false;
        isEditingRoute = objGlobalMethods.isEditingRoute();
        if (!isEditingRoute) {
            LogOutNew();
        }*/
    }

    public Dialog showDialogWithTitle(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
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
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawerMain2Activity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawerMain2Activity.this);
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DrawerMain2Activity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DrawerMain2Activity showChangePermissionDialog. ActivityNotFoundException: "
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
                String RegID = objGlobalMethods.getRegistrationIdOnly();
                GetDriverJobsCountSQLite task = new GetDriverJobsCountSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);
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
                    pdCheck.hide();
                    objGlobalMethods.writeToFile("Unable to log out. There are still data waiting to be sent out. Please try again later.", "", "", "", "");
                    showDialog(myApp.getAppContext().getString(R.string.alert_cannotlogoutsendingdata));
                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                    return;
                } else if (queCount == 0) {
                    //no que, send back all plotted locations
                    //then logout
                    if (Boolean.parseBoolean(ishomesuser)) {
                        objGlobalMethods.writeToFile("Logging out HOMES USER.", "", "", "", "");
                        LogOutHomesWSCall();
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

            LogOutWSCall task = new LogOutWSCall(username, password);
            task.execute();
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
            String RegID = objGlobalMethods.getRegistrationIdOnly();
            DeAuthDriverResult = WebService.UnAuthDriver(RegID, timestampstr, 0, 0);
            return DeAuthDriverResult;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean success) {
            try {
                pdCheck.hide();
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

                    String[] pName = new String[]{objGlobalVariables.PROPERTY_REG_ID, "prevLoginUserIDX", "prevLoginUserName"};
                    String[] pvalue = new String[]{
                            "", useridx, username
                    };
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);

                    JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
                    Intent parentIntent = new Intent(DrawerMain2Activity.this, MainActivity.class);
                    startActivity(parentIntent);
                    JobsHandler.actFinish();
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity LogOutWSCall onPostExecute. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
            }
            DrawerMainActivity.errored = false;
        }
    }

    private void LogOutHomesWSCall() {
        Intent parentIntent;
        parentIntent = new Intent(DrawerMain2Activity.this, MainActivity.class);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        String[] prefName = new String[]{"wifistate", "leftofc", "GetAllMsgCompleted",
                "totalJobsForTheDaytxt", "performancetxt", "pointMthToDate", "ptpercentage", "dist_travelled"};
        String[] prefvalue = new String[]{"", "", "",
                "0", "0.0%", "0.0", "0.0", "0.00"};
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
        finish();
    }

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(DrawerMain2Activity.this, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    public void BackToLoginPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, "dailypoints", "wifistate", "leftofc"};
        String[] prefvalue = new String[]{"-1", "", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

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
        parentIntent = new Intent(DrawerMain2Activity.this, MainActivity.class);
        DAO_Jobs.dropLukeDB();
        startActivity(parentIntent);
        finish();
    }

    public void setCategoryHashes(Context context, String valName, String val, String RetrieveTypeNameStr, String JobIDXsJsonStr,
                                  String JobInfoJsonPrefName, String JobInfoJsonPrefJsonStr, String JobsCountName, String JobCount) {

        GcmPreferences = objGlobalMethods.getGcmPreferences();
        JobsHandler.setCategoryHashesForDashboard(GcmPreferences, valName, val, RetrieveTypeNameStr, JobIDXsJsonStr, JobInfoJsonPrefName, JobInfoJsonPrefJsonStr, JobsCountName, String.valueOf(JobCount));
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
                //cd = new ConnectionDetector(context);
                cd = new ConnectionDetector(null);
                if (cd.isConnectingToInternet()) {
                    JobsHandler.processCODJobs();
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity getCODList doInBackground. Exception: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity getCODList onPostExecute codval conversion. Exception: "
                            + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    codval = 0.0;
                }

                if (codval == 0.0) {

                } else if (codval > 0.0) {
                    Intent i = new Intent(DrawerMain2Activity.this, MainCODActivity.class);
                    DrawerMainActivity.handler.removeCallbacks(runnable);
                    startActivity(i);
                }
            } catch (Exception e) {
                pdCOD.dismiss();
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity getCODList onPostExecute. Excpetion: " + e.toString() + "\nStack: " + stack, "", "", "", "");
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
                objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity updateDrawerCOD codval conversion. Exception: " + e.getMessage().toString(), "", "", "", "");
                codval = 0.0;
            }
            DecimalFormat dec = new DecimalFormat("#0.00");
            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
            Menu nav_Menu = navigationView.getMenu();
            String title = myApp.getAppContext().getString(R.string.item_dashboard2COD) + " $" + String.valueOf(dec.format(codval));
            nav_Menu.findItem(R.id.cod).setTitle(title);
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity updateDrawerCOD. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

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

                /* and here comes the "trick" */

                //start backendRun again after 15s
                //handler.postDelayed(this, 15000);
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught @ DrawerMain2Activity Runnable(). " + e.getMessage().toString() + "\nStack: " + stack,
                        "", "", "", "");
            }
        }
    };

    public static void stopRunnable() {
        JobsHandler.backEndIsRunning = false;
        DrawerMainActivity.handler.removeCallbacks(runnable);
    }

    public static void postphoneRunnable() {
        JobsHandler.backEndIsRunning = false;
        DrawerMainActivity.handler.postDelayed(runnable, 15000);
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
            String RegID = objGlobalMethods.getRegistrationIdOnly();
            JobsHandler.downloadStatusCodesLibWS task = new JobsHandler.downloadStatusCodesLibWS(RegID, GcmPreferences);
            task.execute();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity downloadStatusCodes. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    //=========================================sqlite=================================================
    private class GetDriverJobsCountSQLite extends AsyncTask<Void, Void, Boolean> {
        final SharedPreferences gcmPref;
        final SharedPreferences aUpdateJobInfoPreferences;
        final String aregid;
        final String ausername;
        final String auseridx;
        Date starttime;
        Date endtime;
        boolean resetQ = false;
        boolean finalresetQ = false;

        public void setfinalresetQ(boolean rq) {
            this.finalresetQ = rq;
        }

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
            if (RootGuard.blockOperation("DrawerMain2Activity.GetDriverJobsCountSQLite", null)) {
                cancel(false);
                return false;
            }
            Boolean asd = true;
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
            boolean hasOpsQueBool = false;
            List<JobQueue> existingOpsQ = new ArrayList<>();
            ArrayList<objGC_CustomQueue> OPS_CustomQueueList = new ArrayList<>();
            objGC_CustomQueue OPS_CustomQueue = null;
            JobQueue ojq = null;
            Long ojqJobsIDX = Long.parseLong("0");

            boolean lastUpdatedIsNearHendersonOfc = false;
            //boolean isNearHendersonOfc = false;
            String logmsg = "DrawerMain2Activity2 GetDriverJobsCountSQLite";
            List<Long> pipIDXs = null;
            List<Long> dipIDXs = null;
            String saveAmended4QuesJsonStr = "";

            try {
                lite = JobsHandler2.isLuke_Reduced(gcmPref);
                objGlobalMethods.setGCMPrefs(gcmPref, "isrefreshing", "t");
                if (!Boolean.parseBoolean(ishomesuser)) {
                    DriverJobsArrayList = JobsHandler.GetDriverJobsArrayList(aregid);
                } else {
                    DriverJobsArrayList = null;
                    objGlobalMethods.writeToFile("DrawerMainActivity2 GetDriverJobsCountSQLite doInBackground DriverJobsArrayList is null.", "", "", "", "");
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
                        if (i == 0)
                            pipIDXs = Arrays.asList(JobsIDXarr);
                        if (i == 2)
                            dipIDXs = Arrays.asList(JobsIDXarr);

                        if (JobsIDXarr != null && JobInfoAL != null) {
                            Arrays.sort(JobsIDXarr);
                            for (int v = 0; v <= JobsIDXarr.length - 1; v++) {
                                s += "[" + String.valueOf(JobsIDXarr[v]) + "]";
                            }

                            if (!s.trim().equals("")) {
                                try {
                                    cHashstr = SHA1(s);
                                } catch (Exception e) {
                                    objGlobalMethods.writeToFile("Exception at DrawerMain2Activity2 GetDriverJobsCountSQLite doInBackground cHashstr conversion. Exception: " + e.getMessage(), "", "", "", "");
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
                            //objGlobalMethods.writeToFile("DrawerMain2Activity2 GetDriverJobsCountSQLite JobsIDXarr size = " + String.valueOf(JobsIDXarrsize) + " JobInfoAL size = " + String.valueOf(JobInfoALsize) + " when i = " + String.valueOf(i), "", "", "", "");

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

                        if (i == 6 && objJobsInsertionInfoList.size() > 0) {
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
                    //resetQ = JobsHandler2.enableVRP_EditRouteButton_NearHendersonOfcNew(GcmPreferences) && isRefresh;
                    resetQ = JobsHandler2.enableVRP_EditRouteButton_NearHendersonOfcNew(GcmPreferences, isRefresh);
                    if (finalresetQ)
                        resetQ = finalresetQ;
                    //resetQ = false;
                    if (resetQ) {
                        SharedPreferences sp = objGlobalMethods.getVrpRelatedSharedPreferences();
                        JobsHandler2.removePostalFrom_credOpenedPostalsNew(sp, resetQ);
                        objGlobalMethods.setVrpRelatedSP_KeyValue2(sp, "ManualQueuedPostalJson", "");
                        /*List<String> savedpostalsList = null;
                        SharedPreferences sp = objGlobalMethods.getVrpRelatedSharedPreferences();

                        objGlobalMethods.setVrpRelatedSP_KeyValue2(sp, "ManualQueuedPostalJson", "");

                        String credOpenedPostalsJson = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "credOpenedPostalsJson");
                        if (!objGlobalMethods.IsEmptyString(credOpenedPostalsJson))
                            savedpostalsList = new Gson().fromJson(credOpenedPostalsJson, new TypeToken<ArrayList<String>>() {
                            }.getType());
                        if (savedpostalsList != null && savedpostalsList.size() > 0) {
                            JobsHandler2.removePostalFrom_credOpenedPostals(sp, savedpostalsList);

                            String cred_postalcountused = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "cred_postalcountused");
                            int usedC = objGlobalMethods.IsEmptyString(cred_postalcountused) ? 0 : Integer.parseInt(cred_postalcountused);
                            int prevusedC = savedpostalsList != null && savedpostalsList.size() > 0 ? savedpostalsList.size() : 0;
                            if (prevusedC > 0)
                                usedC = usedC - prevusedC;

                            if (usedC < 0)
                                usedC = 0;

                            credOpenedPostalsJson = "";

                            Date dt = new Date();
                            long dtlong = dt.getTime();
                            String credEditedDateLong = String.valueOf(dtlong);
                            String[] vrpspprefName = new String[]{"credOpenedPostalsJson", "cred_postalcountused", "credEditedDateLong"};
                            String[] vrpspprefvalue = new String[]{credOpenedPostalsJson, String.valueOf(usedC), credEditedDateLong};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        }*/


                        List<JobQueue> NewJQlist = JobsHandler.GetProcessJobQue6();
                        hasOpsQueBool = hasOpsQue(NewJQlist);
                        if (hasOpsQueBool)
                            existingOpsQ = getAllOpsQueue(NewJQlist);

                        boolean L2_SaveCabinet = false;
                        boolean L2_DeleteProblem = false;
                        boolean L2_CreateProblem = false;
                        String[] UsernamePwd = new String[4];
                        String useridx = "";
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(objGlobalMethods.getGcmPreferences());
                        useridx = UsernamePwd[2];

                        Date dt = new Date();
                        Long l = dt.getTime();
                        String timestampstr = "/Date(" + l + ")/";

                        String[] qal = new String[]{
                                "false", "", ""
                        };
                        String alJsonStr = qal != null ? new Gson().toJson(qal) : "";

                        objCabinet cabinet = new objCabinet(0, Long.parseLong(!useridx.isEmpty() && !useridx.equals("") && useridx != "" ? useridx : "0"), "2", alJsonStr, timestampstr);

                        L2_SaveCabinet = WebService.SaveCabinet(aregid, cabinet);

                        if (L2_SaveCabinet) {
                            L2_DeleteProblem = WebService.L2_DeleteProblem(aregid);
                        }

                        //L2_DeleteProblem = true;
                        if (L2_DeleteProblem) {
                            WebService.L2_DeleteJobQueue(aregid);
                            L2_CreateProblem = WebService.L2_CreateProblem(aregid);
                        }
                        if (L2_CreateProblem) {
                            JobsHandler.HandleRouteList(aregid, objGlobalMethods.getGcmPreferences());
                            String[] prefName = new String[]{"lastpostal", "lastJobQueueDataJsonStr", "JobsPostalsToBeUNPARKEDJsonStr", "prevfirstQlocation"};
                            String[] prefvalue = new String[]{"", "", "", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                            if (existingOpsQ != null && existingOpsQ.size() > 0) {
                                int que = 1;

                                for (int o = 0; o <= existingOpsQ.size() - 1; o++) {
                                    ojq = existingOpsQ.get(o);
                                    if (ojq.JobsIDXsList != null && ojq.JobsIDXsList.size() > 1) {
                                        for (int ojqi = 0; ojqi <= ojq.JobsIDXsList.size() - 1; ojqi++) {
                                            ojqJobsIDX = ojq.JobsIDXsList.get(ojqi);
                                            if (pipIDXs.contains(ojqJobsIDX) || dipIDXs.contains(ojqJobsIDX)) {
                                                OPS_CustomQueue = new objGC_CustomQueue(ojqJobsIDX, que);
                                                OPS_CustomQueueList.add(OPS_CustomQueue);
                                                que++;
                                            }
                                        }
                                    } else {
                                        ojqJobsIDX = ojq.JobsIDXsList.get(0);
                                        if (pipIDXs.contains(ojqJobsIDX) || dipIDXs.contains(ojqJobsIDX)) {
                                            OPS_CustomQueue = new objGC_CustomQueue(ojqJobsIDX, que);
                                            OPS_CustomQueueList.add(OPS_CustomQueue);
                                            que++;
                                        }
                                    }
                                }

                                if (OPS_CustomQueueList != null && OPS_CustomQueueList.size() > 0) {
                                    boolean L2_SetOpsETAResult = WebService.L2_SetOpsETA(aregid, OPS_CustomQueueList);
                                    if (!L2_SetOpsETAResult)
                                        objGlobalMethods.writeToFile("L2_SetOpsETAResult ruturn false.", "", "", "", "");
                                }
                            }
                        } else {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "prevfirstQlocation", "");
                        }
                    }

                    ArrayList<Integer> ListItemIndex = new ArrayList<>();
                    ListItemIndex.add(-1);
                    String JobsPostalsToBeUNPARKEDJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "JobsPostalsToBeUNPARKEDJsonStr");
                    ArrayList<String> JobsPostalsToBeUNPARKED = null;
                    if (!objGlobalMethods.IsEmptyString(JobsPostalsToBeUNPARKEDJsonStr)) {
                        JobsPostalsToBeUNPARKED = new Gson().fromJson(JobsPostalsToBeUNPARKEDJsonStr, new TypeToken<ArrayList<String>>() {
                        }.getType());
                    }
                    JobsHandler.getJobQue2(aregid, isRefresh, false, ListItemIndex, JobsPostalsToBeUNPARKED);
                    //JobsHandler.getJobQue2(aregid, false, false, ListItemIndex, JobsPostalsToBeUNPARKED);

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

                    L2_LA_GetUserResult GetUserResult = WebService.L2_LA_GetUser(aregid);
                    /*String rid = "fxWZUGeHbFU:APA91bFxv20gYb8EiYy6J3x5gkz55D1-LX1JcIWepKQywVQ06_O1qkEZl5VTYrzWdjc601oJkVLtrj-TxtlTItIpQ1NnLZNK6MjWr5GQm53JkAiFVm3428iZzo1AF9HIP5yCDURYr2ye";
                    L2_LA_GetUserResult GetUserResult = WebService.L2_LA_GetUser(rid);*/
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
                    /*RecipientTypesArray = WebService.GetRecipientTypes(regid, "L2_GetRecipientTypes");
                    if (RecipientTypesArray.length > 0) {
                        Gson gson = new Gson();
                        String RecipientTypesArrayJsonStr;
                        RecipientTypesArrayJsonStr = gson.toJson(RecipientTypesArray);
                        JobsHandler.storeRecipientTypes(GcmPreferences, RecipientTypesArrayJsonStr);
                    }*/
                    ////do not uncomment til further notice

                    if (!lite)
                        JobsHandler.getGCPerformancePercentage(aUpdateJobInfoPreferences, gcmPref, false);
                    //JobsHandler.getGCDistTravelled(gcmPref, aregid);

                    //calculateAvailCreditsNew(false);

                    asd = true;
                } else {
                    asd = false;
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity GetDriverJobsCountSQLite doInBackground. Exception: " + e.toString(), "", "", "", "");
                asd = false;
                objGlobalMethods.setGCMPrefs(gcmPref, "isrefreshing", "f");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            try {
                objGlobalMethods.setGCMPrefs(gcmPref, "route_edited_count", "0");
                objGlobalMethods.setGCMPrefs(gcmPref, "isrefreshing", "f");
                if (success) {
                    if (isRefresh) {
                        //updateList();
                        if (refreshQueBooleanString) {
                            objGlobalMethods.setGCMPrefs(gcmPref, "refreshQueBooleanString", "false");
                        } else {
                            /*menuItem.collapseActionView();
                            menuItem.setActionView(null);*/
                        }
                        /*isRefresh = false;
                        if (resetQ) {
                            loadVrpList2(resetQ);
                        }*/
                        loadVrpList2(resetQ, true);
                    } else {
                        loadVrpList2(resetQ, true);
                    }

                    Intent broadcastedIntent = new Intent(context, GetAllMsg_Receiver.class);
                    broadcastedIntent.putExtra("uponLogin", 1);
                    sendBroadcast(broadcastedIntent);

                    Intent DownloadDataReceiver = new Intent(context, Download_Data_BroadcastReceiver.class);
                    sendBroadcast(DownloadDataReceiver);
                } else {
                    loadVrpList2(false, false);
                }
                isRefresh = false;

                runOnUiThread(new Runnable() {
                    public void run() {
                        // Notifiy activity
                        //hideNavigationItems();
                        initMenu();
                    }
                });
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity GetDriverJobsCountSQLite onPostExecute. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                if (pd != null) {
                    pd.hide();
                }
            } finally {
                endtime = new Date();
                if (pd != null) {
                    pd.hide();
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
                        objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity GetDriverJobsCountSQLite onPostExecute showDialog alert_noRetrieveDataFail. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                        Toast.makeText(myApp.getAppContext(), errmsg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private boolean hasOpsQue(List<JobQueue> NewJQlist) {
        boolean asd = false;
        JobQueue jq = null;
        try {
            if (NewJQlist != null && NewJQlist.size() > 0) {
                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    jq = NewJQlist.get(i);
                    if (jq.OPS_Queue != null && jq.OPS_Queue > 0) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity hasOpsQue. Exception: " + e.getMessage() + "\n" + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private List<JobQueue> getAllOpsQueue(List<JobQueue> NewJQlist) {
        List<JobQueue> r = new ArrayList<>();
        boolean asd = false;
        JobQueue jq = null;
        try {
            if (NewJQlist != null && NewJQlist.size() > 0) {
                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    jq = NewJQlist.get(i);
                    if (jq.OPS_Queue != null && jq.OPS_Queue > 0) {
                        r.add(jq);
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity getAllOpsQueue. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return r;
        }
    }

    private boolean hasWeighted(List<JobQueue> njq) {
        boolean asd = false;
        boolean istestuserID = objGlobalMethods.isTestUserID();
        JobQueue j = null;
        try {
            for (int i = 1; i <= njq.size() - 1; i++) {
                j = njq.get(i);
                asd = ((j.Flag & 4) == 4) && !istestuserID;
                if (asd)
                    break;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity hasWeighted. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private void loadVrpList2(boolean resetQ, boolean recalCred) {
        boolean hasWeighted = false;
        boolean hasOpsQueBool = false;
        boolean QueisUpdated = false;
        boolean VRP_StartEarly = false;
        boolean isBefore935am = false;
        Context ctx = myApp.getAppContext();
        boolean isWalker = false;
        boolean isStdRunner = false;
        boolean isSatSun = false;
        boolean isAOHtime = false;
        int medc = 0;
        try {
            SharedPreferences vsp = objGlobalMethods.getVrpRelatedSharedPreferences();
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            lite = JobsHandler2.isLuke_Reduced(GcmPreferences);
            //setMenuCatCount();
            setMenuText();
            String lastpostal = objGlobalMethods.getGCMPrefs(GcmPreferences, "lastpostal");
            JobsHandler2.regroupParkedJobs(lastpostal);
            ArrayList<String> listcurr = DAO_Jobs.getAllPostalsInDip();
            //JobsHandler2.removePostalFrom_credOpenedPostals(null, null); ////commented here since inside following function already calling
            //calculateAvailCreditsNew(false, listcurr);
            calCredits(false, listcurr, resetQ);
            /*if (recalCred)
                calculateAvailCreditsNew(false, listcurr);
            else
                JobsHandler2.removePostalFrom_credOpenedPostals(null, null);*/
            List<JobQueue> NewJQlist = JobsHandler.GetProcessJobQue6();
            List<JobQueue> First_3_JobQueue = null;
            hasOpsQueBool = (objGlobalMethods.getGCMPrefs(GcmPreferences, "hasOpsQue")).equals("t");
            medc = DAO_Jobs.getMedJobsCount();
            if (medc > 0) {
                DAO_Jobs.cleanPIPPCS();
                objGlobalMethods.writeToFile("DrawerMain2Activity updatePIPPCS_ZeroScanned", "", "", "", "");
                DAO_Jobs.updatePIPPCS_ZeroScanned();
            }
            if (NewJQlist != null && NewJQlist.size() > 0) {
                QueisUpdated = JobsHandler.QueisUpdated();
                //hasOpsQueBool = hasOpsQue(NewJQlist); //replace by the code above
                hasWeighted = hasWeighted(NewJQlist) && !resetQ;
                VRP_StartEarly = JobsHandler2.VRP_StartEarly(GcmPreferences);
                isBefore935am = JobsHandler2.isBefore1005am();

                if (VRP_StartEarly && isBefore935am && !hasOpsQueBool) {
                    JobsHandler.releaseAllQue(NewJQlist);
                    hasOpsQueBool = true;
                }
                adapter2 = new VrpList_RecycleViewAdapter2();
                ////this is for touch hold drag
                /*VRPSwipeAndDragHelper2 swipeAndDragHelper2 = new VRPSwipeAndDragHelper2(adapter2);
                ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper2);
                adapter2.setTouchHelper(touchHelper);*/
                ////this is for touch hold drag
                rv.setAdapter(adapter2);
                ////this is for touch hold drag
                /*touchHelper.attachToRecyclerView(rv);*/
                ////this is for touch hold drag

                //First_3_JobQueue = JobsHandler.getFirst_3_JobQueue(NewJQlist);
                First_3_JobQueue = NewJQlist;

                VrpList_RecycleViewAdapter2.itemEnableMoveArray = new SparseBooleanArray();
                for (int i = 0; i <= First_3_JobQueue.size() - 1; i++) {
                    VrpList_RecycleViewAdapter2.itemEnableMoveArray.put(i, false);
                    /*if (hasOpsQueBool && First_3_JobQueue.get(i).type.equals("vrp")) {
                        First_3_JobQueue.get(i).setIsEnabled(false);
                        First_3_JobQueue.get(i).setisUpdated(false);
                    }*/
                }

                adapter2.setNewJQlist(First_3_JobQueue);
                //adapter2.setsaveAmended4QuesJsonStr(objGlobalMethods.getGCMPrefs(GcmPreferences, "saveAmended4QuesJsonStr"));
                adapter2.setsaveAmended4QuesJsonStr(objGlobalMethods.getVrpRelatedSP_KeyValue("saveAmended4QuesJsonStr"));
                // restore index and position
                int pos = 0;

                try {
                    pos = Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "DrawerMain2ScrollSavedPosition"));
                } catch (Exception e) {
                    pos = 0;
                }

                if (pos > 0) {
                    try {
                        rv.scrollToPosition(pos);
                    } catch (Exception e) {

                    }
                    objGlobalMethods.resetDrawerMain2ActivityScrollIndexNposition(GcmPreferences);
                }
                //promptReqMPD();
                objGlobalMethods.showNewPIPdialog(GcmPreferences);

                boolean isAllWhite = adapter2.getisAllWhite();
                if (hasOpsQueBool) {
                    objGlobalMethods.writeToFile("1", "", "", "", "");
                    ctrlSettings.enableDisableEditButtonWithStyles(false, btnEditRoute);
                } else if (hasWeighted) {
                    objGlobalMethods.writeToFile("2", "", "", "", "");
                    ctrlSettings.enableDisableEditButtonWithStyles(false, btnEditRoute);
                } else if (NewJQlist.size() == 1) {
                    objGlobalMethods.writeToFile("3", "", "", "", "");
                    ctrlSettings.enableDisableEditButtonWithStyles(false, btnEditRoute);
                } else if (NewJQlist.size() > 1 && QueisUpdated && isAllWhite) {
                    objGlobalMethods.writeToFile("4", "", "", "", "");
                    ctrlSettings.enableDisableEditButtonWithStyles(true, btnEditRoute);
                } else if (NewJQlist.size() > 1 && !QueisUpdated && isAllWhite) {
                    objGlobalMethods.writeToFile("5", "", "", "", "");
                    ctrlSettings.enableDisableEditButtonWithStyles(true, btnEditRoute);
                } else if (NewJQlist.size() > 1 && QueisUpdated) {
                    objGlobalMethods.writeToFile("6", "", "", "", "");
                    ctrlSettings.enableDisableEditButtonWithStyles(resetQ, btnEditRoute);
                } else if (NewJQlist.size() > 1 && !QueisUpdated) {
                    objGlobalMethods.writeToFile("7", "", "", "", "");
                    ctrlSettings.enableDisableEditButtonWithStyles(resetQ, btnEditRoute);
                }
            } else {
                int TotalOutstandingNumOfPipDip = DAO_Jobs.getTotalOutstandingNumOfPipDip();
                if (TotalOutstandingNumOfPipDip == 0) {
                    objGlobalMethods.setVrpRelatedSP_KeyValue("saveAmended4QuesJsonStr", "");
                }
                objGlobalMethods.writeToFile("8", "", "", "", "");
                ctrlSettings.enableDisableEditButtonWithStyles(false, btnEditRoute);
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.btn_ok), ctx.getString(R.string.alert_nodataR));
            }

            //ctrlSettings.enableDisableEditButtonWithStyles(true, btnEditRoute);
            Integer[] pipdipc = DAO_Jobs.getTotalOutstandingNumOfPipDipInidividually();
            if (toolbar != null) {
                if (pipdipc != null && pipdipc.length > 0) {
                    toolbar.setTitle(ctx.getString(R.string.pip) + pipdipc[0] + " " + ctx.getString(R.string.dip) + pipdipc[1]);
                } else {
                    toolbar.setTitle(ctx.getString(R.string.pip) + "0 " + ctx.getString(R.string.dip) + "0");
                }
            }

            lblpercentage.setOnClickListener(null);
            //llhidden1.setOnClickListener(null);
            //llhidden2.setOnClickListener(null);
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            updatePerformanceItem();
            updateUnlock(hasOpsQueBool, listcurr.size());
            if ((First_3_JobQueue.size() <= 1 && pipdipc != null && (pipdipc[0] > 0 || pipdipc[1] > 0)) || ((username.equals("xd")) || (username.equals("hill")))) {
                lblpercentage.setOnClickListener(LLpercentage_OnClickListener());
                //llhidden1.setOnClickListener(LLpercentage_OnClickListener());
                //llhidden2.setOnClickListener(LLpercentage_OnClickListener());
            } else if ((username.equals("xd")) || (username.equals("hill"))) {
                lblpercentage.setOnClickListener(LLpercentage_OnClickListener());
                //llhidden1.setOnClickListener(LLpercentage_OnClickListener());
                //llhidden2.setOnClickListener(LLpercentage_OnClickListener());
            }
            if (lite)
                lblpercentage.setText(ctx.getText(R.string.info_na));

            isStdRunner = JobsHandler2.isStdRunner(GcmPreferences);
            isWalker = JobsHandler2.isWalker(GcmPreferences);
            if (!isWalker && isStdRunner) {
                isSatSun = JobsHandler2.isSatSun();
                isAOHtime = JobsHandler2.isAOHtime();
            }

            //initBleManager();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity loadVrpList2. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            if ((isWalker && !hasOpsQueBool) || (isStdRunner && !isSatSun && !isAOHtime && !hasOpsQueBool)) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, "pagemode", "dashboard");
                if (isWalker) {
                    objGlobalMethods.writeToFile("Switched to Summary Mode due isWalker.", "", "", "", "");
                } else {
                    objGlobalMethods.writeToFile("Switched to Summary Mode due isStdRunner && !isSatSun.", "", "", "", "");
                }
                Intent i = new Intent(myApp.currentActivity, DrawerMainActivity.class);
                startActivity(i);
                finish();
            }
            promptReqMPD();
        }
    }

    public void calCredits(boolean runOnUIthread, ArrayList<String> listcurr, boolean resetQ) {
        SharedPreferences sp = objGlobalMethods.getVrpRelatedSharedPreferences();
        String msgHeader = "";
        Gson gson = new Gson();
        credit cd = null;
        String creditAM = "";
        String creditPM = "";
        String creditAOH = "";

        ArrayList<String> listA = null;
        //List<String> listPOF = null;
        ArrayList<String> TotalCredPostals = new ArrayList<>();
        boolean hasOpsQueBool = false;
        String ManualQueuedPostalUsedJson = "";
        ArrayList<String> ManualQueuedPostalUsedList = new ArrayList<>();
        List<String> missing = null;
        List<String> extra = null;
        boolean insideProcessed = false;

        int cred_postalcount = 0;
        int cred_postalcountNew = 0;
        double postalcountd = 0.0;
        double limitpostalcountd = 0.0;
        ArrayList<String> listPipDip = null;

        try {
            if (!resetQ)
                JobsHandler2.removePostalFrom_credOpenedPostalsNew(sp, false);

            /*boolean isAM = JobsHandler3.isAM();
            boolean isPM = JobsHandler3.isPM();
            boolean isAOH = JobsHandler3.isAOH();*/
            int AMONLYc = 0;
            int AMc = 0;
            int PMc = 0;
            int AOHc = 0;
            credit cdAM = null;
            credit cdPM = null;
            credit cdAOH = null;
            //msgHeader = (isAM ? "creditAM" : isPM ? "creditPM" : "creditAOH") + " ";
            creditAM = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "creditAM");
            creditPM = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "creditPM");
            creditAOH = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "creditAOH");

            AMONLYc = DAO_Jobs.getAllPostalsInPipDipByAMOnlySlot().size();
            listPipDip = DAO_Jobs.getAllPostalsInPipDipByAMSlot(); //this incl AM PM
            AMc = AMONLYc > 0 ? listPipDip.size() : 0;
            if (AMc == 0) {
                listPipDip = DAO_Jobs.getAllPostalsInPipDipByPMSlot();
                PMc = listPipDip.size();
                if (PMc == 0) {
                    listPipDip = DAO_Jobs.getAllPostalsInPipDipByAOHSlot();
                    AOHc = listPipDip.size();
                }
            }

            cdAM = !objGlobalMethods.IsEmptyString(creditAM) ? gson.fromJson(creditAM, credit.class) : null;
            cdPM = !objGlobalMethods.IsEmptyString(creditPM) ? gson.fromJson(creditPM, credit.class) : null;
            cdAOH = !objGlobalMethods.IsEmptyString(creditAOH) ? gson.fromJson(creditAOH, credit.class) : null;
            cd = (AMc > 0) && cdAM != null ? cdAM : (PMc > 0) && cdPM != null ? cdPM : (AOHc > 0) && cdAOH != null ? cdAOH : null;

            if (cd == null) {
                if (AOHc > 0) {
                    cd = new credit();
                } else if (PMc > 0) {
                    cd = new credit();
                } else if (AMc > 0) {
                    cd = new credit();
                }
            }
            if (cd == null) {
                if (cdAOH != null) {
                    AOHc++;
                    cd = cdAOH;
                } else if (cdPM != null) {
                    PMc++;
                    cd = cdPM;
                } else if (cdAM != null) {
                    AMc++;
                    cd = cdAM;
                }
            }


            /*cdAM = (AMc > 0) && !objGlobalMethods.IsEmptyString(creditAM) ? gson.fromJson(creditAM, credit.class) : null;
            cdPM = (AMc == 0 && PMc > 0) && !objGlobalMethods.IsEmptyString(creditPM) ? gson.fromJson(creditPM, credit.class) : null;
            cdAOH = (PMc == 0 && AOHc > 0) && !objGlobalMethods.IsEmptyString(creditAOH) ? gson.fromJson(creditAOH, credit.class) : null;
            cd = cdAM != null ? cdAM : cdPM != null ? cdPM : cdAOH != null ? cdAOH : null;*/

            if ((PMc > 0) && cd != null && cdAM != null && cdAM.credOpenedPostals != null && cdAM.credOpenedPostals.size() > 0) {
                for (int i = cdAM.credOpenedPostals.size() - 1; i >= 0; i--) {
                    cd.addCredOpenedPostals(cdAM.credOpenedPostals.get(i));
                    cdAM.credOpenedPostals.remove(i);
                }
            }
            if ((AOHc > 0) && cd != null && cdPM != null && cdPM.credOpenedPostals != null && cdPM.credOpenedPostals.size() > 0) {
                for (int i = cdPM.credOpenedPostals.size() - 1; i >= 0; i--) {
                    cd.addCredOpenedPostals(cdPM.credOpenedPostals.get(i));
                    cdPM.credOpenedPostals.remove(i);
                }
            }

            /*if (isAM && !objGlobalMethods.IsEmptyString(creditAM))
                cd = gson.fromJson(creditAM, credit.class);
            if (isPM && !objGlobalMethods.IsEmptyString(creditPM))
                cd = gson.fromJson(creditPM, credit.class);
            if (isAOH && !objGlobalMethods.IsEmptyString(creditAOH))
                cd = gson.fromJson(creditAOH, credit.class);*/

            //hasOpsQueBool = (objGlobalMethods.getGCMPrefs(GcmPreferences, "hasOpsQue")).equals("t");
            /*if (listcurr == null)
                listcurr = DAO_Jobs.getAllPostalsInPipDipBySlot(isAM ? 1 : isPM ? 2 : 4);*/
            if (listcurr == null)
                listcurr = DAO_Jobs.getAllPostalsInDip();

            if (cd != null) {
                listA = cd.ListA;
                if (listA == null || (listA != null && listA.size() == 0)) {
                    //cd.setTotalCredPostals(listcurr);
                    cd.setTotalCredPostals(listPipDip);
                    ManualQueuedPostalUsedList = cd.ManualQueuedPostalUsedList;

                    cred_postalcount = cd.TotalCredPostals.size() +
                            (cd.TotalNCUsedPostals != null ? cd.TotalNCUsedPostals.size() : 0) +
                            (cd.credUsedPostals != null ? cd.credUsedPostals.size() : 0) +
                            (ManualQueuedPostalUsedList != null ? ManualQueuedPostalUsedList.size() : 0);
                    postalcountd = cred_postalcount;
                    limitpostalcountd = (25 * postalcountd) / 100.0f;
                    int limitpostalcount = (int) Math.round(limitpostalcountd);

                    cd.setCred_postalcount(cred_postalcount);
                    cd.setCred_postalcountlimit(limitpostalcount);
                    cd.setListA(listPipDip);

                    String creditJson = gson.toJson(cd);

                    if (AMc > 0) {
                        String[] vrpspprefName = new String[]{"creditAM"};
                        String[] vrpspprefvalue = new String[]{creditJson};
                        objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                    }
                    if (PMc > 0) {
                        creditAM = cdAM != null ? gson.toJson(cdAM) : "";
                        if (!objGlobalMethods.IsEmptyString(creditAM)) {
                            String[] vrpspprefName = new String[]{"creditAM", "creditPM"};
                            String[] vrpspprefvalue = new String[]{creditAM, creditJson};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        } else {
                            String[] vrpspprefName = new String[]{"creditPM"};
                            String[] vrpspprefvalue = new String[]{creditJson};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        }
                    }
                    if (AOHc > 0) {
                        creditPM = cdPM != null ? gson.toJson(cdPM) : "";
                        if (!objGlobalMethods.IsEmptyString(creditPM)) {
                            String[] vrpspprefName = new String[]{"creditPM", "creditAOH"};
                            String[] vrpspprefvalue = new String[]{creditPM, creditJson};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        } else {
                            String[] vrpspprefName = new String[]{"creditAOH"};
                            String[] vrpspprefvalue = new String[]{creditJson};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        }
                    }

                    String res = msgHeader + "cred_postalcountlimit: 0, cred_postalcount: 0";
                    objGlobalMethods.writeToFile("DrawerMain2Activity calCredits completed #1. " + res, "", "", "", "");
                } else if (listA != null && listA.size() > 0 && listPipDip != null && listPipDip.size() > 0) {
                    TotalCredPostals = cd.TotalCredPostals;
                    if (TotalCredPostals == null)
                        TotalCredPostals = new ArrayList<String>();
                    ManualQueuedPostalUsedList = cd.ManualQueuedPostalUsedList;

                    final List<String> listB = listPipDip;
                    final List<String> listAa = listA;
                    extra = listB.stream()
                            .filter(item -> !listAa.contains(item))
                            .collect(Collectors.toList());

                    missing = listA.stream()
                            .filter(item -> !listB.contains(item))
                            .collect(Collectors.toList());

                    if (missing != null && missing.size() > 0) {
                        int lastIndex = -1;
                        for (int i = 0; i <= missing.size() - 1; i++) {
                            lastIndex = -1;
                            lastIndex = TotalCredPostals.lastIndexOf(missing.get(i));
                            TotalCredPostals.remove(lastIndex);
                        }
                    }

                    if (extra != null && extra.size() > 0) {
                        for (int i = 0; i <= extra.size() - 1; i++) {
                            TotalCredPostals.add(extra.get(i));
                        }
                    }

                    cred_postalcountNew = TotalCredPostals.size() +
                            (cd.TotalNCUsedPostals != null ? cd.TotalNCUsedPostals.size() : 0) +
                            (cd.credUsedPostals != null ? cd.credUsedPostals.size() : 0) +
                            (ManualQueuedPostalUsedList != null ? ManualQueuedPostalUsedList.size() : 0);
                    postalcountd = cred_postalcountNew;
                    limitpostalcountd = (25 * postalcountd) / 100.0f;
                    int limitpostalcount = (int) Math.round(limitpostalcountd);
                    listA = listPipDip;

                    cd.setCred_postalcountlimit(limitpostalcount);
                    cd.setCred_postalcount(cred_postalcountNew);
                    cd.setListA(listA);
                    cd.setTotalCredPostals(TotalCredPostals);

                    String cred_postalcountused = "0";
                    cred_postalcountused = objGlobalMethods.getGCMPrefs(sp, "cred_postalcountused");
                    cred_postalcountused = objGlobalMethods.IsEmptyString(cred_postalcountused) ? "0" : cred_postalcountused;
                    int cred_postalcountusedd = 0;
                    if (!objGlobalMethods.IsEmptyString(cred_postalcountused))
                        cred_postalcountusedd = Integer.parseInt(cred_postalcountused);

                    boolean clear_credOpenedPostalsJson = (limitpostalcount <= cred_postalcountusedd);
                    if (clear_credOpenedPostalsJson)
                        cd.setCredOpenedPostals(null);

                    String creditJson = cd != null ? gson.toJson(cd) : "";

                    if (AMc > 0) {
                        String[] vrpspprefName = new String[]{"creditAM"};
                        String[] vrpspprefvalue = new String[]{creditJson};
                        objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                    }
                    if (PMc > 0) {
                        creditAM = cdAM != null ? gson.toJson(cdAM) : "";
                        if (!objGlobalMethods.IsEmptyString(creditAM)) {
                            String[] vrpspprefName = new String[]{"creditAM", "creditPM"};
                            String[] vrpspprefvalue = new String[]{creditAM, creditJson};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        } else {
                            String[] vrpspprefName = new String[]{"creditPM"};
                            String[] vrpspprefvalue = new String[]{creditJson};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        }
                    }
                    if (AOHc > 0) {
                        creditPM = cdPM != null ? gson.toJson(cdPM) : "";
                        if (!objGlobalMethods.IsEmptyString(creditPM)) {
                            String[] vrpspprefName = new String[]{"creditPM", "creditAOH"};
                            String[] vrpspprefvalue = new String[]{creditPM, creditJson};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        } else {
                            String[] vrpspprefName = new String[]{"creditAOH"};
                            String[] vrpspprefvalue = new String[]{creditJson};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        }
                    }

                    String res = "cred_postalcountlimit: " + String.valueOf(limitpostalcount) + ", cred_postalcount: " + String.valueOf(cred_postalcountNew);
                    objGlobalMethods.writeToFile("DrawerMain2Activity calCredits completed #2. " + res, "", "", "", "");
                } else if (listPipDip == null || (listPipDip != null && listPipDip.size() == 0)) {
                    cd.setTotalCredPostals(listPipDip);
                    ManualQueuedPostalUsedList = cd.ManualQueuedPostalUsedList;

                    cred_postalcount = cd.TotalCredPostals.size() +
                            (cd.TotalNCUsedPostals != null ? cd.TotalNCUsedPostals.size() : 0) +
                            (cd.credUsedPostals != null ? cd.credUsedPostals.size() : 0) +
                            (ManualQueuedPostalUsedList != null ? ManualQueuedPostalUsedList.size() : 0);
                    postalcountd = cred_postalcount;
                    limitpostalcountd = (25 * postalcountd) / 100.0f;
                    int limitpostalcount = (int) Math.round(limitpostalcountd);

                    cd.setCred_postalcountlimit(limitpostalcount);
                    cd.setCred_postalcount(cred_postalcount);
                    cd.setListA(listPipDip);

                    String creditJson = gson.toJson(cd);

                    if (AMc > 0) {
                        String[] vrpspprefName = new String[]{"creditAM"};
                        String[] vrpspprefvalue = new String[]{creditJson};
                        objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                    }
                    if (PMc > 0) {
                        creditAM = cdAM != null ? gson.toJson(cdAM) : "";
                        if (!objGlobalMethods.IsEmptyString(creditAM)) {
                            String[] vrpspprefName = new String[]{"creditAM", "creditPM"};
                            String[] vrpspprefvalue = new String[]{creditAM, creditJson};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        } else {
                            String[] vrpspprefName = new String[]{"creditPM"};
                            String[] vrpspprefvalue = new String[]{creditJson};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        }
                    }
                    if (AOHc > 0) {
                        creditPM = cdPM != null ? gson.toJson(cdPM) : "";
                        if (!objGlobalMethods.IsEmptyString(creditPM)) {
                            String[] vrpspprefName = new String[]{"creditPM", "creditAOH"};
                            String[] vrpspprefvalue = new String[]{creditPM, creditJson};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        } else {
                            String[] vrpspprefName = new String[]{"creditAOH"};
                            String[] vrpspprefvalue = new String[]{creditJson};
                            objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                        }
                    }

                    String res = msgHeader + "cred_postalcountlimit: 0, cred_postalcount: 0";
                    objGlobalMethods.writeToFile("DrawerMain2Activity calCredits completed #3. " + res, "", "", "", "");
                }
            } else {
                cd = new credit();
                //cred_postalcount = listcurr.size();
                cred_postalcount = listPipDip.size();
                postalcountd = cred_postalcount;
                limitpostalcountd = (25 * postalcountd) / 100.0f;
                int limitpostalcount = (int) Math.round(limitpostalcountd);

                cd.setCred_postalcount(cred_postalcount);
                cd.setCred_postalcountlimit(limitpostalcount);
                cd.setListA(listPipDip);
                //cd.setTotalCredPostals(listcurr);
                cd.setTotalCredPostals(listPipDip);

                String creditJson = gson.toJson(cd);

                if (AMc > 0) {
                    String[] vrpspprefName = new String[]{"creditAM"};
                    String[] vrpspprefvalue = new String[]{creditJson};
                    objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                }
                if (PMc > 0) {
                    creditAM = cdAM != null ? gson.toJson(cdAM) : "";
                    if (!objGlobalMethods.IsEmptyString(creditAM)) {
                        String[] vrpspprefName = new String[]{"creditAM", "creditPM"};
                        String[] vrpspprefvalue = new String[]{creditAM, creditJson};
                        objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                    } else {
                        String[] vrpspprefName = new String[]{"creditPM"};
                        String[] vrpspprefvalue = new String[]{creditJson};
                        objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                    }
                }
                if (AOHc > 0) {
                    creditPM = cdPM != null ? gson.toJson(cdPM) : "";
                    if (!objGlobalMethods.IsEmptyString(creditPM)) {
                        String[] vrpspprefName = new String[]{"creditPM", "creditAOH"};
                        String[] vrpspprefvalue = new String[]{creditPM, creditJson};
                        objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                    } else {
                        String[] vrpspprefName = new String[]{"creditAOH"};
                        String[] vrpspprefvalue = new String[]{creditJson};
                        objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
                    }
                }

                String res = msgHeader + "cred_postalcountlimit: " + String.valueOf(limitpostalcount) + ", cred_postalcount: " + String.valueOf(cred_postalcount);
                objGlobalMethods.writeToFile("DrawerMain2Activity calCredits completed #4. " + res, "", "", "", "");
            }

            if (runOnUIthread) {
                hasOpsQueBool = (objGlobalMethods.getGCMPrefs(GcmPreferences, "hasOpsQue")).equals("t");
                updateUnlock(hasOpsQueBool, listcurr.size());
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity calCredits. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void updatePerformanceItem() {
        Context ctx = myApp.getAppContext();
        try {
            rlpercentage.setOnClickListener(null);
            String totalJobsForTheDaytxt = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalJobsForTheDaytxt");
            String performancetxt = objGlobalMethods.getGCMPrefs(GcmPreferences, "performancetxt");
            String dist_travelledtxt = objGlobalMethods.getGCMPrefs(GcmPreferences, "dist_travelled");
            String ptpercentageS = objGlobalMethods.getGCMPrefs(GcmPreferences, "ptpercentage");
            String pointMthToDateS = objGlobalMethods.getGCMPrefs(GcmPreferences, "pointMthToDate");
            lite = JobsHandler2.isLuke_Reduced(GcmPreferences);

            ////this is with percentage only
            final String pt = ctx.getString(R.string.performancetxt01) + " " + (objGlobalMethods.IsEmptyString(totalJobsForTheDaytxt) ? "0" : totalJobsForTheDaytxt)
                    + "\n\n" + ctx.getString(R.string.performancetxt02) + " " + (objGlobalMethods.IsEmptyString(performancetxt) ? "0.0%" : performancetxt);

            ////this is with distance travelled
            /*final String pt = ctx.getString(R.string.performancetxt01) + " " + (objGlobalMethods.IsEmptyString(totalJobsForTheDaytxt) ? "0" : totalJobsForTheDaytxt)
                    + "\n" + ctx.getString(R.string.performancetxt02) + " " + (objGlobalMethods.IsEmptyString(performancetxt) ? "0.0%" : performancetxt)
                    + "\n" + ctx.getString(R.string.performancetxt06) + ": " + (objGlobalMethods.IsEmptyString(dist_travelledtxt) ? "0.00 " + ctx.getString(R.string.performancetxt04) : dist_travelledtxt) + " " + ctx.getString(R.string.performancetxt04);*/

            ////this is with distance travelled and avg speed
                /*final String pt = ctx.getString(R.string.performancetxt01) + " " + totalJobsForTheDaytxt
                        + "\n" + ctx.getString(R.string.performancetxt02) + " " + performancetxt
                        + "\n" + ctx.getString(R.string.performancetxt03) + " " + (objGlobalMethods.IsEmptyString(dist_travelledtxt) ? "0.0%" : dist_travelledtxt) + ctx.getString(R.string.performancetxt04)
                        + "\n" + "AVG SPEED: 0KM/H";*/

            String lblptxt = (objGlobalMethods.IsEmptyString(totalJobsForTheDaytxt) ? "0" : totalJobsForTheDaytxt) + "\n" + (objGlobalMethods.IsEmptyString(performancetxt) ? "0.0%" : performancetxt);
            lblp.setText(lblptxt);

            rlpercentage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), pt).show();
                }
            });

            rlpercentage.setOnLongClickListener(null);
            rlpercentage.setOnLongClickListener(LLpercentage_OnLongClickListener());

            lblpercentage.setText(!lite ? pt : "");

            updatePointPercentage(pointMthToDateS, ptpercentageS);

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity updatePerformanceItem. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void updatePointPercentage(String pointMthToDateS, String ptpercentageS) {
        Context ctx = myApp.getAppContext();
        double ptpercentage = 0.0;
        try {
            llptpie.setOnClickListener(null);
            lblpoint.setText(pointMthToDateS);
            ptpercentage = !objGlobalMethods.IsEmptyString(ptpercentageS) ? Double.parseDouble(ptpercentageS) : 0.0;

            llptpie.setBackgroundResource(
                    ptpercentage >= 0.0 && ptpercentage <= 10.0 ? R.mipmap.pie00 :
                            ptpercentage > 10.0 && ptpercentage <= 20.0 ? R.mipmap.pie10 :
                                    ptpercentage > 20.0 && ptpercentage <= 30.0 ? R.mipmap.pie20 :
                                            ptpercentage > 30.0 && ptpercentage <= 40.0 ? R.mipmap.pie30 :
                                                    ptpercentage > 40.0 && ptpercentage <= 50.0 ? R.mipmap.pie40 :
                                                            ptpercentage > 50.0 && ptpercentage <= 60.0 ? R.mipmap.pie50 :
                                                                    ptpercentage > 60.0 && ptpercentage <= 70.0 ? R.mipmap.pie60 :
                                                                            ptpercentage > 70.0 && ptpercentage <= 80.0 ? R.mipmap.pie70 :
                                                                                    ptpercentage > 80.0 && ptpercentage <= 90.0 ? R.mipmap.pie80 :
                                                                                            ptpercentage > 90.0 && ptpercentage <= 100.0 ? R.mipmap.pie90 :
                                                                                                    R.mipmap.pie100);

            llptpie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    String[] prefName = new String[]{"dailypoints", "summarypoints", "summaryfromdate", "summarytodate",
                            "summaryfromdate2", "summarytodate2", "currentpointdatelong", "visiblelv", "gcsch"};
                    String[] prefvalue = new String[]{"", "", "", "", "", "", "", "", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    Intent intent = new Intent(DrawerMain2Activity.this, DriverPointSummaryActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity updatePointPercentage. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private String getjobsidx(int type) {
        String j = "";
        ArrayList<String> list = null;
        try {
            if (type == 1) {
                list = DAO_Jobs.getAllJobsIDXsInPipDipByAMOnlySlot();
            } else if (type == 2) {
                list = DAO_Jobs.getAllJobsIDXsInPipDipByPMSlot();
            } else if (type == 3) {
                list = DAO_Jobs.getAllJobsIDXsInPipDipByAOHSlot();
            }
            if (list != null && list.size() > 0)
                j = String.join(",", list);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity getjobsidx. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return j;
        }
    }

    public void updateUnlock(boolean hasOpsQueBool, int listcurrsize) {
        SharedPreferences vrpsp = objGlobalMethods.getVrpRelatedSharedPreferences();
        String ManualQueuedPostalJson = "";
        boolean hasManualQueuedPostal = false;
        List<String> listPipDip = null;
        Gson gson = new Gson();
        try {
            /*boolean isAM = JobsHandler3.isAM();
            boolean isPM = JobsHandler3.isPM();
            boolean isAOH = JobsHandler3.isAOH();*/
            int AMONLYc = 0;
            int PMONLYc = 0;
            int AMc = 0;
            int PMc = 0;
            int AOHc = 0;
            String creditAM = "";
            String creditPM = "";
            String creditAOH = "";
            creditAM = objGlobalMethods.getVrpRelatedSP_KeyValue2(vrpsp, "creditAM");
            creditPM = objGlobalMethods.getVrpRelatedSP_KeyValue2(vrpsp, "creditPM");
            creditAOH = objGlobalMethods.getVrpRelatedSP_KeyValue2(vrpsp, "creditAOH");
            credit prevcd = null;
            credit cdAM = null;
            credit cdPM = null;
            credit cdAOH = null;
            credit cdc = null;
            credit cd = null;

            /*AMONLYc = DAO_Jobs.getAllPostalsInPipDipByAMOnlySlot().size();
            listPipDip = DAO_Jobs.getAllPostalsInPipDipByAMSlot();
            AMc = AMONLYc > 0 ? listPipDip.size() : 0;
            if (AMc == 0) {
                listPipDip = DAO_Jobs.getAllPostalsInPipDipByPMSlot();
                PMc = listPipDip.size();
                if (PMc == 0) {
                    listPipDip = DAO_Jobs.getAllPostalsInPipDipByAOHSlot();
                    AOHc = listPipDip.size();
                }
            }
            cdAM = (AMc > 0) && !objGlobalMethods.IsEmptyString(creditAM) ? gson.fromJson(creditAM, credit.class) : null;
            cdPM = (AMc == 0 && PMc > 0) && !objGlobalMethods.IsEmptyString(creditPM) ? gson.fromJson(creditPM, credit.class) : null;
            cdAOH = (PMc == 0 && AOHc > 0) && !objGlobalMethods.IsEmptyString(creditAOH) ? gson.fromJson(creditAOH, credit.class) : null;
            cd = cdAM != null ? cdAM : cdPM != null ? cdPM : cdAOH != null ? cdAOH : null;*/
            AMONLYc = DAO_Jobs.getAllPostalsInPipDipByAMOnlySlot().size();
            PMONLYc = DAO_Jobs.getAllPostalsInPipDipByPMSlot().size();
            //AOHc = DAO_Jobs.getAllPostalsInPipDipByAOHSlot().size();
            cdAM = !objGlobalMethods.IsEmptyString(creditAM) ? gson.fromJson(creditAM, credit.class) : null;
            cdPM = !objGlobalMethods.IsEmptyString(creditPM) ? gson.fromJson(creditPM, credit.class) : null;
            cdAOH = !objGlobalMethods.IsEmptyString(creditAOH) ? gson.fromJson(creditAOH, credit.class) : null;

            cd = cdAOH != null && PMONLYc == 0 ? cdAOH : cdPM != null && AMONLYc == 0 ? cdPM : cdAM != null ? cdAM : null;
            String whichcd = cdAOH != null && PMONLYc == 0 ? "cdAOH" : cdPM != null && AMONLYc == 0 ? "cdPM" : cdAM != null ? "cdAM" : "null";
            String sss = "AMONLYc " + String.valueOf(AMONLYc) + " PMONLYc " + String.valueOf(PMONLYc) + " ";
            sss += cdAM == null ? "cdAM n " : "cdAM ok ";
            sss += cdPM == null ? "cdPM n " : "cdPM ok ";
            sss += cdAOH == null ? "cdAOH n " : "cdAOH ok ";
            sss += " whichcd " + whichcd + " ";

            /*if (AMONLYc > 0)
                sss += "\nAM JobsIDX: " + getjobsidx(1);
            if (PMONLYc > 0)
                sss += "\nPM JobsIDX: " + getjobsidx(2);
            if (AOHc > 0)
                sss += "\nAOH JobsIDX: " + getjobsidx(3);*/

            if (rlunlock == null)
                rlunlock = findViewById(R.id.rlunlock);
            if (lblunlock == null)
                lblunlock = findViewById(R.id.lblunlock);

            rlunlock.setOnClickListener(null);
            int cred_postalcount = 0;
            int usedpostalcount = 0;
            int limitpostalcount = 0;
            int bal = 0;
            int prevcredOpenedPostalsCount = prevcd != null && prevcd.credOpenedPostals != null ? prevcd.credOpenedPostals.size() : 0;

            ManualQueuedPostalJson = objGlobalMethods.getGCMPrefs(vrpsp, "ManualQueuedPostalJson");
            hasManualQueuedPostal = !objGlobalMethods.IsEmptyString(ManualQueuedPostalJson);

            usedpostalcount = cd != null ? cd.cred_postalcountused : 0;
            limitpostalcount = cd != null ? cd.cred_postalcountlimit : 0;
            cred_postalcount = cd != null ? cd.cred_postalcount : 0;

            bal = (limitpostalcount - usedpostalcount) < 0 ? 0 : (limitpostalcount - usedpostalcount);

            String msg = String.valueOf(bal);
            sss += "\n";
            sss += "cred_postalcount " + String.valueOf(cred_postalcount) + " limitpostalcount " + String.valueOf(limitpostalcount) + " usedpostalcount " + String.valueOf(usedpostalcount);

            lblunlock.setText(msg);

            boolean btnEditRouteEnabled = btnEditRoute.isEnabled();
            boolean limitReached = usedpostalcount > 0 && usedpostalcount >= limitpostalcount;
            boolean enable_rlunlock = ((!hasOpsQueBool && !btnEditRouteEnabled && !hasManualQueuedPostal) && bal > 0 && listcurrsize > 0 && prevcredOpenedPostalsCount == 0);

            sss += "\n" + (hasOpsQueBool ? " hasOpsQueBool t" : " hasOpsQueBool f") + " " + (btnEditRouteEnabled ? " btnEditRouteEnabled t" : " btnEditRouteEnabled f") +
                    " " + (hasManualQueuedPostal ? " hasManualQueuedPostal t" : " hasManualQueuedPostal f") + " "
                    + " bal " + String.valueOf(bal) + " " + " listcurrsize " + String.valueOf(listcurrsize) + " "
                    + " prevcredOpenedPostalsCount " + String.valueOf(prevcredOpenedPostalsCount);
            sss += "\nenable_rlunlock " + (enable_rlunlock ? "t" : "f");

            objGlobalMethods.writeToFile(sss, "", "", "", "");

            rlunlock.setBackgroundResource(
                    (!hasOpsQueBool && !btnEditRouteEnabled && !hasManualQueuedPostal) && bal > 0 && listcurrsize > 0 && prevcredOpenedPostalsCount == 0 ? R.drawable.rect_border_body_green :
                            R.drawable.rect_border_body_grey
            );
            lblunlock.setTextColor((!hasOpsQueBool && !btnEditRouteEnabled && !hasManualQueuedPostal) ? Color.BLACK :
                    Color.WHITE);

            if (!hasOpsQueBool && !btnEditRouteEnabled && limitReached) {
                rlunlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context ctx = myApp.getAppContext();
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.max_selection3)).show();
                    }
                });
            } else if (!hasOpsQueBool && !btnEditRouteEnabled && !hasManualQueuedPostal && prevcredOpenedPostalsCount > 0) {
                rlunlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context ctx = myApp.getAppContext();
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.earlier_selection_not_done)).show();
                    }
                });
            } else if (!hasOpsQueBool && !btnEditRouteEnabled && !hasManualQueuedPostal && prevcredOpenedPostalsCount == 0) {
                rlunlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //calculateAvailCreditsNew(true, null);
                        calCredits(true, null, true);
                        vrpUnlockDialog vd = new vrpUnlockDialog();
                        vd.show(getSupportFragmentManager(), "vrpUnlockDialog");
                    }
                });
            } else if (hasOpsQueBool) {
                rlunlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context ctx = myApp.getAppContext();
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.opsqueset)).show();
                    }
                });
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity updateUnlock. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void refreshrv(boolean recalCreds) {
        //loadVrpList2(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadVrpList2(false, recalCreds);
            }
        });
    }

    private void search(String searchValue) {
        ArrayList al = null;
        List<dbObjJobs> rs = null;
        dbObjJobs obj = null;
        Integer totalCount = 0;
        Integer CatTypeID = -1;
        String desAddress = "";
        String Postal = "";
        Intent intent = null;
        String JobInfoJsonStr = "";
        Double LAT = 0.0;
        Double LNG = 0.0;
        Boolean found = null;
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            rs = DAO_Jobs.searchJobs(-1, "", searchValue);
            //new
            if (rs != null && rs.size() > 0) {
                found = true;
                obj = rs.get(0);
                List<JobQueue> First_3_JobQueue = null;
                VrpList_RecycleViewAdapter adapter = (VrpList_RecycleViewAdapter) rv.getAdapter();
                if (adapter != null) {
                    First_3_JobQueue = adapter.getJQlist();
                    if (First_3_JobQueue != null && First_3_JobQueue.size() > 0) {
                        for (int x = 0; x <= First_3_JobQueue.size() - 1; x++) {
                            if (First_3_JobQueue.get(x).getPostalCode() != null && First_3_JobQueue.get(x).getPostalCode().equals(obj.getPostal()) && First_3_JobQueue.get(x).getIsEnabled()) {
                                CatTypeID = obj.getCatTypeID();
                                Postal = obj.getPostal();
                                desAddress = obj.getAddressWithPostalNoUnit();
                                totalCount = DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);
                                al = new ArrayList();
                                al.add(obj);
                                al.add(totalCount);
                                break;
                            }
                        }
                    }
                }
            } else {
                found = false;
            }
            //new

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
                    }

                    objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", desAddress);
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

                            intent = new Intent(DrawerMain2Activity.this, setDunningDeliveredStatusActivity.class);
                            JobInfoActivity.accessFromDirection = 0;
                        } else {
                            intent = new Intent(DrawerMain2Activity.this, JobInfoActivity.class);
                            JobInfoActivity.accessFromDirection = 1;
                        }
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "true");
                        JobInfoJsonStr = obj.getJobInfoJsonStr();
                        objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, JobInfoJsonStr);
                    } else if (totalCount > 1) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "searchedval", searchValue);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "true");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobsInfoArrJsonStr", "");
                        intent = new Intent(DrawerMain2Activity.this, JobsListGroupActivity.class);
                        JobInfoActivity.accessFromDirection = 0;
                    }

                    if (intent != null) {
                        startActivity(intent);
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
                    }
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
                }
            } else {
                showDialog(found == null ? myApp.getAppContext().getString(R.string.alert_errorlocatingjob) : found ? myApp.getAppContext().getString(R.string.alert_errorjobnotwithinlist) : myApp.getAppContext().getString(R.string.alert_jobnotfound));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity searchValue. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
        }
    }

    //=========================================sqlite=================================================

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this,
                    "Permission accepted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,
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
                    Jobs_Retrieval_Activity.fromPage = "DrawerMain2Activity";
                    Intent jrintent = new Intent(this, Jobs_Retrieval_Activity.class);
                    startActivity(jrintent);
                    this.finish();
                }*/
            }

        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity handleIntent. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    public void promptReqMPD() {
        /*objGlobalMethods.promptReqMPD();
        mpdclass.promptRegBTTL();*/
        objGlobalMethods.promptReqMPD_New();
    }

    public void btnUpdateRoute_Click(View v) {
        List<JobQueue> NewJQlist = null;
        List<JobQueue> tempJQlist = null;
        final Context ctx = myApp.getAppContext();
        ConnectionDetector cd;
        boolean hasMovedRoute = false;
        JobQueue jq = null;
        int idx = -1;
        try {
            cd = new ConnectionDetector(null);
            if (adapter2 == null) {
                adapter2 = initAdapter();
            }
            if (adapter2 != null) {
                NewJQlist = adapter2.getJQlist();
                tempJQlist = NewJQlist;

                if (tempJQlist != null && tempJQlist.size() > 0) {
                    for (int i = 0; i <= tempJQlist.size() - 1; i++) {
                        jq = tempJQlist.get(i);
                        if (!jq.IsHeader && jq.isMoved && !jq.isUpdated) {
                            hasMovedRoute = true;
                            idx = i;
                            break;
                        }
                    }

                    if (hasMovedRoute) {
                        if (cd.isConnectingToInternet()) {
                            final int idxx = idx;
                            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                            builder.setCancelable(false);
                            builder.setTitle(ctx.getString(R.string.alert_cfmupdateroute));
                            builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if user pressed "yes", update job status
                                    if (adapter2 == null) {
                                        adapter2 = initAdapter();
                                    }
                                    if (adapter2 != null) {
                                        List<JobQueue> NewJQlist = adapter2.getJQlist();
                                        //List<JobQueue> tempJQlist = JobsHandler.rtnEnableDisableJobQueForUpdates(NewJQlist, null);
                                        List<JobQueue> tempJQlist = JobsHandler.rtnEnableDisableJobQueForUpdatesNew(NewJQlist, null);
                                        SetGCETA t = new SetGCETA(tempJQlist, adapter2, idxx);
                                        t.execute();
                                    } else {
                                        showDialog(ctx.getString(R.string.alert_errorupdateroute));
                                    }
                                }
                            });
                            builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if user select "No", just cancel this dialog and continue with app
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            objGlobalMethods.showDialog(myApp.currentActivity, "OK", ctx.getString(R.string.toast_workingconnection)).show();
                        }
                    } else {
                        showDialogWithTitle(ctx.getString(R.string.alert_errorupdateroute), ctx.getString(R.string.alert_routeplsselect));
                    }
                } else {
                    showDialog(ctx.getString(R.string.alert_errorupdateroute));
                }
            } else {
                showDialog(ctx.getString(R.string.alert_errorupdateroute));
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity btnUpdateRoute_Click. Exception: " + s, "", "", "", "");
        }
    }

    public void btnEditRoute_Click(View v) {
        try {
            final Context ctx = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setCancelable(false);
            builder.setTitle(ctx.getString(R.string.alert_cfmeditupdateroute));
            builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    try {
                        if (adapter2 == null) {
                            adapter2 = initAdapter();
                        }
                        if (adapter2 != null) {
                            List<JobQueue> NewJQlist = adapter2.getJQlist();
                            adapter2.setOriginalJQlist(NewJQlist);
                            int itempos = 0;
                            JobQueue jq = null;
                            /*for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                                jq = NewJQlist.get(i);
                                if (!jq.getIsHeader() && jq.getType().equals("vrp")) {
                                    itempos = i;
                                    break;
                                }
                            }*/
                            for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                                jq = NewJQlist.get(i);
                                if (itempos == 0 && !jq.getIsHeader() && jq.getType().equals("vrp")) {
                                    itempos = i;
                                }
                                if (!jq.getIsHeader() && jq.getType().equals("vrp")) {
                                    NewJQlist.get(i).setisUpdated(false);
                                    NewJQlist.get(i).setisMoved(false);
                                }
                            }
                            adapter2.setNewJQlist(NewJQlist);

                            objGlobalMethods.writeToFile("Attempt to edit route.", "", "", "", "");
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "NewJQlistitempos", String.valueOf(itempos));
                            adapter2.setitemEnableMoveArray(true, itempos);
                            hideShowUpdateCtrls(true, false);
                            adapter2.notifyDataSetChanged();
                            adapter2.finaloldPosition = null;
                            adapter2.finalnewPosition = null;
                        } else {
                            showDialog(ctx.getString(R.string.alert_errorupdateroute));
                        }
                    } catch (Exception e) {
                        String s = e.getMessage();
                        objGlobalMethods.writeToFile("Exception at DrawerMain2Activity btnEditRoute_Click setPositiveButton onClick. Exception: " + s, "", "", "", "");
                        showDialog(ctx.getString(R.string.alert_errorupdateroute));
                    }
                }
            });
            builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity btnEditRoute_Click. Exception: " + s, "", "", "", "");
        }
    }

    public void btnCancelUpdateRoute_Click(View v) {
        try {
            final Context ctx = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setCancelable(false);
            builder.setTitle(ctx.getString(R.string.alert_cfmcancelupdateroute));
            builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    try {
                        if (adapter2 == null) {
                            adapter2 = initAdapter();
                        }
                        if (adapter2 != null) {
                            List<JobQueue> al = adapter2.getOriginalJQlist();
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            String NewJQlistitempos = objGlobalMethods.getGCMPrefs(GcmPreferences, "NewJQlistitempos");
                            int itempos = !NewJQlistitempos.isEmpty() && !NewJQlistitempos.equals("") ? Integer.parseInt(NewJQlistitempos) : 0;
                            adapter2.setNewJQlist(al);
                            adapter2.setitemEnableMoveArray(false, itempos);
                            hideShowUpdateCtrls(false, false);
                            adapter2.nullOriginalJQlist();
                            adapter2.finaloldPosition = null;
                            adapter2.finalnewPosition = null;
                            adapter2.notifyDataSetChanged();
                        } else {
                            showDialog(ctx.getString(R.string.alert_errorupdateroute));
                        }
                    } catch (Exception e) {
                        String s = e.getMessage();
                        objGlobalMethods.writeToFile("Exception at DrawerMain2Activity btnCancelUpdateRoute_Click setPositiveButton onClick. Exception: " + s, "", "", "", "");
                        showDialog(ctx.getString(R.string.alert_errorupdateroute));
                    }
                }
            });
            builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity btnCancelUpdateRoute_Click. Exception: " + s, "", "", "", "");
        }
    }

    public void btnResetRoute_Click(View v) {
        try {
            final Context ctx = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setCancelable(false);
            builder.setTitle(ctx.getString(R.string.alert_cfmresetupdateroute));
            builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    try {
                        if (adapter2 == null) {
                            adapter2 = initAdapter();
                        }
                        if (adapter2 != null) {
                            List<JobQueue> al = adapter2.getOriginalJQlist();
                            List<JobQueue> NewJQlist = al;
                            adapter2.setOriginalJQlist(al);

                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            String NewJQlistitempos = objGlobalMethods.getGCMPrefs(GcmPreferences, "NewJQlistitempos");
                            int itempos = !NewJQlistitempos.isEmpty() && !NewJQlistitempos.equals("") ? Integer.parseInt(NewJQlistitempos) : 0;
                            adapter2.resetNewJQlist(NewJQlist);
                            adapter2.setitemEnableMoveArray(true, itempos);
                            hideShowUpdateCtrls(true, false);
                            adapter2.finaloldPosition = null;
                            adapter2.finalnewPosition = null;
                            adapter2.notifyDataSetChanged();
                        } else {
                            showDialog(ctx.getString(R.string.alert_errorupdateroute));
                        }
                    } catch (Exception e) {
                        String s = e.getMessage();
                        objGlobalMethods.writeToFile("Exception at DrawerMain2Activity btnResetRoute_Click setPositiveButton onClick. Exception: " + s, "", "", "", "");
                        showDialog(ctx.getString(R.string.alert_errorupdateroute));
                    }
                }
            });
            builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity btnResetRoute_Click. Exception: " + s, "", "", "", "");
        }
    }

    private VrpList_RecycleViewAdapter2 initAdapter() {
        try {
            adapter2 = new VrpList_RecycleViewAdapter2();
            VRPSwipeAndDragHelper2 swipeAndDragHelper2 = new VRPSwipeAndDragHelper2(adapter2);
            ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper2);
            adapter2.setTouchHelper(touchHelper);
            rv.setAdapter(adapter2);
            touchHelper.attachToRecyclerView(rv);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity initAdapter. Exception: " + s, "", "", "", "");
        } finally {
            return adapter2;
        }
    }

    public void hideShowUpdateCtrls(boolean IsEditing, boolean hideall) {
        try {
            if (hideall) {
                btnCancelUpdateRoute.setVisibility(View.GONE);
                btnEditRoute.setVisibility(View.GONE);
                btnUpdateRoute.setVisibility(View.GONE);
                btnResetRoute.setVisibility(View.GONE);
            } else if (IsEditing) {
                btnCancelUpdateRoute.setVisibility(View.VISIBLE);
                btnEditRoute.setVisibility(View.GONE);
                btnUpdateRoute.setVisibility(View.VISIBLE);
                btnResetRoute.setVisibility(View.VISIBLE);
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(gcmp, "editingvrp", "true");
            } else {
                btnCancelUpdateRoute.setVisibility(View.GONE);
                btnEditRoute.setVisibility(View.VISIBLE);
                btnUpdateRoute.setVisibility(View.GONE);
                btnResetRoute.setVisibility(View.GONE);
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(gcmp, "editingvrp", "");
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception caught at DrawerMain2Activity hideShowUpdateCtrls. Exception: " + s, "", "", "", "");
        }
    }

    public class SetGCETA extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pd;
        private final List<JobQueue> tempJQlist;
        private final VrpList_RecycleViewAdapter2 adapter;
        private final int idxx;

        public SetGCETA(List<JobQueue> atempJQlist, VrpList_RecycleViewAdapter2 aadapter, int aidxx) {
            this.tempJQlist = atempJQlist;
            this.adapter = aadapter;
            this.idxx = aidxx;
            this.pd = new ProgressDialog(myApp.currentActivity);
            this.pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.pd.setCanceledOnTouchOutside(false);
            this.pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean asd = false;
            objGC_CustomQueue GC_CustomQueue = null;
            ArrayList<objGC_CustomQueue> AQueue = null;
            JobQueue jq = null;
            JobQueue removalq = null;
            JobQueue tempjq = null;
            int queue = 0;
            int numOfNonHeaderJQsProcessed = 0;
            String jqJobsIDXs = "";
            List<String> jqJobsIDXsArr = null;
            List<Long> jqJobsIDXsList = null;
            String ManualQueuedPostalJson = "";
            List<String> ManualQueuedPostalList = new ArrayList<>();
            try {
                if (tempJQlist != null && tempJQlist.size() > 0) {
                    for (int i = 0; i <= tempJQlist.size() - 1; i++) {
                        jq = tempJQlist.get(i);
                        if (!jq.getIsHeader() && !jq.getType().equals("ops") && jq.getisMoved()) {
                            jqJobsIDXsList = jq.getJobsIDXsList();
                            if (jqJobsIDXsList != null && jqJobsIDXsList.size() > 0) {
                                jqJobsIDXs = jq.getJobsIDXs();
                                queue++;
                                for (int x = 0; x <= jqJobsIDXsList.size() - 1; x++) {
                                    GC_CustomQueue = new objGC_CustomQueue(jqJobsIDXsList.get(x), queue);
                                    if (AQueue == null) {
                                        AQueue = new ArrayList<objGC_CustomQueue>();
                                    }
                                    AQueue.add(GC_CustomQueue);
                                    if (!ManualQueuedPostalList.contains(jq.PostalCode))
                                        ManualQueuedPostalList.add(jq.PostalCode);
                                }
                            }
                            break;
                        }
                    }

                    if (AQueue != null && AQueue.size() > 0) {
                        objGlobalMethods.writeToFile("JobsIDXs submitted for GC Queue #1: " + jqJobsIDXs, "", "", "", "");
                        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                        SharedPreferences updateJobInfoPref = objGlobalMethods.getUpdateJobInfoPreferences();
                        String regid = objGlobalMethods.getRegistrationId(myApp.getAppContext(), gcmp);

                        Gson gson = new Gson();
                        String saveAmended4QuesJsonStr = "";
                        if (tempJQlist != null && tempJQlist.size() > 0) {
                            List<JobQueue> al = new ArrayList<JobQueue>();
                            for (int i = 0; i <= tempJQlist.size() - 1; i++) {
                                tempjq = tempJQlist.get(i);
                                tempjq.setStartID(i);
                                if (!tempjq.IsHeader && tempjq.getType().equals("vrp") && tempjq.isMoved && al.size() == 0) {
                                    tempjq.setisUpdated(tempjq.getisMoved());
                                    al.add(tempjq);
                                }
                            }
                            saveAmended4QuesJsonStr = al != null && al.size() > 0 ? gson.toJson(al) : "";
                        } else {
                            saveAmended4QuesJsonStr = "";
                        }


                        String[] UsernamePwd = new String[4];
                        String useridx = "";
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
                        useridx = UsernamePwd[2];
                        Date dt = new Date();
                        Long l = dt.getTime();
                        String timestampstr = "/Date(" + l + ")/";
                        String finalInitialSetOfJobsIDXsJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "InitialSetOfJobsIDXsJsonStr");

                        ManualQueuedPostalJson = ManualQueuedPostalList.size() > 0 ? gson.toJson(ManualQueuedPostalList) : "";
                        String credEditedDateLong = String.valueOf(l);
                        String[] vrpPrefName = new String[]{"saveAmended4QuesJsonStr", "ManualQueuedPostalJson", "credEditedDateLong"};
                        String[] vrpPrefValue = new String[]{saveAmended4QuesJsonStr, ManualQueuedPostalJson, credEditedDateLong};
                        objGlobalMethods.setVrpSPMass2(vrpPrefName, vrpPrefValue);

                        String[] qal = new String[]{
                                "true", saveAmended4QuesJsonStr, finalInitialSetOfJobsIDXsJsonStr
                        };
                        String alJsonStr = qal != null ? gson.toJson(qal) : "";
                        if (!alJsonStr.isEmpty() && !alJsonStr.equals("[]") && !alJsonStr.equals("")) {
                            objGlobalMethods.setGCMPrefs(gcmp, "QCabinetDataJsonStr", alJsonStr);
                            //objGlobalMethods.writeToFile("QCabinetDataJsonStr updated at DrawerMain2Activity SetGCETA with " + qal != null ? "json value." : "empty string.", "", "", "", "");
                        }
                        objCabinet cabinet = new objCabinet(0, Long.parseLong(!useridx.isEmpty() && !useridx.equals("") && useridx != "" ? useridx : "0"), "2", alJsonStr, timestampstr);
                        objETACabinet ETACabinet = new objETACabinet(regid, cabinet, AQueue);
                        objGlobalMethods.writeToFile("GC set New GC Queue. Updating new GC Queue.", "", "", "", "");
                        JobsHandler.addUpdateGCETA_n_Cabinet_List(updateJobInfoPref, ETACabinet);
                        myApp.getAppContext().sendBroadcast(new Intent(myApp.getAppContext(), BackendRun_BroadcastReceiver.class));
                        asd = true;
                    }
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at DrawerMain2Activity SetGCETA doInBackground. Exception: " + s, "", "", "", "");
                asd = false;
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Context ctx = myApp.getAppContext();
            try {
                if (pd != null) {
                    pd.dismiss();
                }
                if (success) {
                    List<JobQueue> NewJQlist = tempJQlist;
                    adapter2.setitemEnableMoveArray(false, 0);
                    //NewJQlist = JobsHandler.rtnEnableDisableJobQueForUpdatesNew(NewJQlist, null);

                    NewJQlist = adapter2.getOriginalJQlist();
                    if (NewJQlist != null && NewJQlist.size() > 0 && idxx > -1) {
                        NewJQlist.get(idxx).setisUpdated(true);
                        NewJQlist.get(idxx).setisMoved(true);
                        NewJQlist.get(idxx).setIsEnabled(true);
                    }

                    adapter2.setNewJQlist(NewJQlist);

                    hideShowUpdateCtrls(false, false);
                    adapter2.notifyDataSetChanged();
                    adapter2.setOriginalJQlist(null);
                    adapter2.finaloldPosition = null;
                    adapter2.finalnewPosition = null;
                    objGlobalMethods.writeToFile("9", "", "", "", "");
                    ctrlSettings.enableDisableEditButtonWithStyles(false, btnEditRoute);
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    //adapter2.setsaveAmended4QuesJsonStr(objGlobalMethods.getGCMPrefs(GcmPreferences, "saveAmended4QuesJsonStr"));
                    adapter2.setsaveAmended4QuesJsonStr(objGlobalMethods.getVrpRelatedSP_KeyValue("saveAmended4QuesJsonStr"));
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "lastpostal", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_routeupdated)).show();
                } else {
                    /*adapter2.setitemEnableMoveArray(false, 0);
                    hideShowUpdateCtrls(false, false);
                    adapter2.notifyDataSetChanged();
                    adapter2.nullOriginalJQlist();
                    adapter2.finaloldPosition = null;
                    adapter2.finalnewPosition = null;
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorupdateroute)).show();*/

                    if (adapter2 == null) {
                        adapter2 = initAdapter();
                    }
                    if (adapter2 != null) {
                        List<JobQueue> al = adapter2.getOriginalJQlist();
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String NewJQlistitempos = objGlobalMethods.getGCMPrefs(GcmPreferences, "NewJQlistitempos");
                        int itempos = !NewJQlistitempos.isEmpty() && !NewJQlistitempos.equals("") ? Integer.parseInt(NewJQlistitempos) : 0;
                        adapter2.setNewJQlist(al);
                        adapter2.setitemEnableMoveArray(false, itempos);
                        hideShowUpdateCtrls(false, false);
                        adapter2.nullOriginalJQlist();
                        adapter2.finaloldPosition = null;
                        adapter2.finalnewPosition = null;
                        adapter2.notifyDataSetChanged();
                    } else {
                        showDialog(ctx.getString(R.string.alert_errorupdateroute));
                    }
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at DrawerMain2Activity SetGCETA doInBackground. Exception: " + s, "", "", "", "");
                List<JobQueue> al = adapter2.getOriginalJQlist();
                adapter2.setNewJQlist(al);
                adapter2.setitemEnableMoveArray(false, 0);
                hideShowUpdateCtrls(false, false);
                adapter2.notifyDataSetChanged();
                adapter2.nullOriginalJQlist();
                adapter2.finaloldPosition = null;
                adapter2.finalnewPosition = null;
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorupdateroute)).show();
            }
        }
    }

    public void processSetLimitedGCETA(ArrayList<vrpUnlockPostData> selectedList) {
        if (adapter2 == null)
            adapter2 = initAdapter();
        if (adapter2 != null) {
            List<JobQueue> NewJQlist = adapter2.getJQlist();
            adapter2.setOriginalJQlist(NewJQlist);
        }

        SetLimitedGCETANew g = new SetLimitedGCETANew(selectedList);
        g.execute();
    }

    public class SetLimitedGCETANew extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pd;
        private final ArrayList<vrpUnlockPostData> selectedList;
        //private List<Integer> QueIdxList = null;
        private ArrayList<String> postalsList = null;
        private List<JobQueue> DiffSaveAmended4Ques = null;
        /*private boolean isAM = false;
        private boolean isPM = false;
        private boolean isAOH = false;*/
        private credit cd = null;

        public SetLimitedGCETANew(ArrayList<vrpUnlockPostData> aselectedList) {
            this.selectedList = aselectedList;
            this.pd = new ProgressDialog(myApp.currentActivity);
            this.pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.pd.setCanceledOnTouchOutside(false);
            this.pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean asd = false;
            List<JobQueue> NewJQlist = null;
            List<JobQueue> al = new ArrayList<JobQueue>();
            List<String> listPipDip = null;
            objGC_CustomQueue GC_CustomQueue = null;
            ArrayList<objGC_CustomQueue> AQueue = null;
            JobQueue jq = null;
            JobQueue njq = null;
            vrpUnlockPostData vp = null;
            int queue = 0;
            List<Long> jqJobsIDXsList = null;
            String log = "";
            ArrayList<String> credOpenedPostals = null;
            String credOpenedPostalsJson = "";
            SharedPreferences vsp = objGlobalMethods.getVrpRelatedSharedPreferences();
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            String saveAmended4QuesJsonStr = "";
            Gson gson = new Gson();

            try {
                /*isAM = JobsHandler3.isAM();
                isPM = JobsHandler3.isPM();
                isAOH = JobsHandler3.isAOH();*/
                int AMONLYc = 0;
                int AMc = 0;
                int PMc = 0;
                int AOHc = 0;
                String creditJson = "";
                String creditAM = "";
                String creditPM = "";
                String creditAOH = "";
                creditAM = objGlobalMethods.getVrpRelatedSP_KeyValue2(vsp, "creditAM");
                creditPM = objGlobalMethods.getVrpRelatedSP_KeyValue2(vsp, "creditPM");
                creditAOH = objGlobalMethods.getVrpRelatedSP_KeyValue2(vsp, "creditAOH");
                credit cdAM = null;
                credit cdPM = null;
                credit cdAOH = null;

                AMONLYc = DAO_Jobs.getAllPostalsInPipDipByAMOnlySlot().size();
                listPipDip = DAO_Jobs.getAllPostalsInPipDipByAMSlot();
                AMc = AMONLYc > 0 ? listPipDip.size() : 0;
                if (AMc == 0) {
                    listPipDip = DAO_Jobs.getAllPostalsInPipDipByPMSlot();
                    PMc = listPipDip.size();
                    if (PMc == 0) {
                        listPipDip = DAO_Jobs.getAllPostalsInPipDipByAOHSlot();
                        AOHc = listPipDip.size();
                    }
                }

                cdAM = (AMc > 0) && !objGlobalMethods.IsEmptyString(creditAM) ? gson.fromJson(creditAM, credit.class) : null;
                cdPM = (AMc == 0 && PMc > 0) && !objGlobalMethods.IsEmptyString(creditPM) ? gson.fromJson(creditPM, credit.class) : null;
                cdAOH = (PMc == 0 && AOHc > 0) && !objGlobalMethods.IsEmptyString(creditAOH) ? gson.fromJson(creditAOH, credit.class) : null;
                cd = cdAM != null ? cdAM : cdPM != null ? cdPM : cdAOH != null ? cdAOH : null;

                /*if (isAM)
                    cd = !objGlobalMethods.IsEmptyString(creditAM) ? gson.fromJson(creditAM, credit.class) : null;
                if (isPM)
                    cd = !objGlobalMethods.IsEmptyString(creditPM) ? gson.fromJson(creditPM, credit.class) : null;
                if (isAOH)
                    cd = !objGlobalMethods.IsEmptyString(creditAOH) ? gson.fromJson(creditAOH, credit.class) : null;*/

                if (selectedList != null && selectedList.size() > 0) {
                    NewJQlist = adapter2.getJQlist();

                    for (int i = 0; i <= selectedList.size() - 1; i++) {
                        vp = selectedList.get(i);

                        if (postalsList == null)
                            postalsList = new ArrayList<>();
                        if (!objGlobalMethods.IsEmptyString(vp.POSTAL) && !postalsList.contains(vp.POSTAL))
                            postalsList.add(vp.POSTAL);
                    }

                    for (int i = 0; i <= selectedList.size() - 1; i++) {
                        vp = selectedList.get(i);

                        if (NewJQlist != null && NewJQlist.size() > 0) {
                            for (int n = 0; n <= NewJQlist.size() - 1; n++) {
                                njq = NewJQlist.get(n);
                                if (njq.type.equals("vrp") && njq.PostalCode != null && njq.PostalCode.equals(vp.POSTAL)) {

                                    jqJobsIDXsList = njq.getJobsIDXsList();
                                    if (jqJobsIDXsList != null && jqJobsIDXsList.size() > 0) {
                                        if (al == null)
                                            al = new ArrayList<>();

                                        if (al.contains(njq))
                                            continue;

                                        al.add(njq);

                                        queue++;
                                        for (int x = 0; x <= jqJobsIDXsList.size() - 1; x++) {
                                            //queue++;
                                            GC_CustomQueue = new objGC_CustomQueue(jqJobsIDXsList.get(x), queue);
                                            if (AQueue == null) {
                                                AQueue = new ArrayList<objGC_CustomQueue>();
                                            }
                                            AQueue.add(GC_CustomQueue);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    if (postalsList != null && postalsList.size() > 0) {
                        log = String.join(",", postalsList);
                        objGlobalMethods.writeToFile("Opening jobs for postal(s) " + log, "", "", "", "");
                    }

                    if (AQueue != null && AQueue.size() > 0) {
                        SharedPreferences updateJobInfoPref = objGlobalMethods.getUpdateJobInfoPreferences();
                        String regid = objGlobalMethods.getRegistrationId(myApp.getAppContext(), gcmp);

                        saveAmended4QuesJsonStr = al != null && al.size() > 0 ? gson.toJson(al) : "";

                        objGlobalMethods.setVrpRelatedSP_KeyValue2(vsp, "saveAmended4QuesJsonStr", saveAmended4QuesJsonStr);
                        String[] UsernamePwd = new String[4];
                        String useridx = "";
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
                        useridx = UsernamePwd[2];
                        Date dt = new Date();
                        Long l = dt.getTime();
                        String timestampstr = "/Date(" + l + ")/";
                        String finalInitialSetOfJobsIDXsJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "InitialSetOfJobsIDXsJsonStr");
                        String[] qal = new String[]{
                                "true", saveAmended4QuesJsonStr, finalInitialSetOfJobsIDXsJsonStr
                        };
                        String alJsonStr = qal != null ? gson.toJson(qal) : "";
                        if (!alJsonStr.isEmpty() && !alJsonStr.equals("[]") && !alJsonStr.equals("")) {
                            objGlobalMethods.setGCMPrefs(gcmp, "QCabinetDataJsonStr", alJsonStr);
                        }
                        objCabinet cabinet = new objCabinet(0, Long.parseLong(!useridx.isEmpty() && !useridx.equals("") && useridx != "" ? useridx : "0"),
                                "2", alJsonStr, timestampstr);
                        objETACabinet ETACabinet = new objETACabinet(regid, cabinet, AQueue);
                        objGlobalMethods.writeToFile("GC opens New GC Queue. Updating new GC Queue.", "", "", "", "");
                        JobsHandler.addUpdateGCETA_n_Cabinet_List(updateJobInfoPref, ETACabinet);
                        myApp.getAppContext().sendBroadcast(new Intent(myApp.getAppContext(), BackendRun_BroadcastReceiver.class));
                    }
                }

                if (cd == null)
                    cd = new credit();

                credOpenedPostals = postalsList;
                cd.setCredOpenedPostals(credOpenedPostals);
                creditJson = cd != null ? gson.toJson(cd) : "";

                Date dt = new Date();
                long dtlong = dt.getTime();
                String credEditedDateLong = String.valueOf(dtlong);
                //credEditedDateLong = "1749967573000";

                if (AMc > 0) {
                    String[] vrpspprefName = new String[]{"creditAM", "credEditedDateLong"};
                    String[] vrpspprefvalue = new String[]{creditJson, credEditedDateLong};
                    objGlobalMethods.setVrpSPMass(vsp, vrpspprefName, vrpspprefvalue);
                }
                if (PMc > 0) {
                    String[] vrpspprefName = new String[]{"creditPM", "credEditedDateLong"};
                    String[] vrpspprefvalue = new String[]{creditJson, credEditedDateLong};
                    objGlobalMethods.setVrpSPMass(vsp, vrpspprefName, vrpspprefvalue);
                }
                if (AOHc > 0) {
                    String[] vrpspprefName = new String[]{"creditAOH", "credEditedDateLong"};
                    String[] vrpspprefvalue = new String[]{creditJson, credEditedDateLong};
                    objGlobalMethods.setVrpSPMass(vsp, vrpspprefName, vrpspprefvalue);
                }

                /*if (isAM) {
                    String[] vrpspprefName = new String[]{"creditAM", "credEditedDateLong"};
                    String[] vrpspprefvalue = new String[]{creditJson, credEditedDateLong};
                    objGlobalMethods.setVrpSPMass(vsp, vrpspprefName, vrpspprefvalue);
                }
                if (isPM) {
                    String[] vrpspprefName = new String[]{"creditPM", "credEditedDateLong"};
                    String[] vrpspprefvalue = new String[]{creditJson, credEditedDateLong};
                    objGlobalMethods.setVrpSPMass(vsp, vrpspprefName, vrpspprefvalue);
                }
                if (isAOH) {
                    String[] vrpspprefName = new String[]{"creditAOH", "credEditedDateLong"};
                    String[] vrpspprefvalue = new String[]{creditJson, credEditedDateLong};
                    objGlobalMethods.setVrpSPMass(vsp, vrpspprefName, vrpspprefvalue);
                }*/

                asd = true;

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at DrawerMain2Activity SetLimitedGCETANew doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Context ctx = myApp.getAppContext();
            SharedPreferences sp = objGlobalMethods.getVrpRelatedSharedPreferences();
            try {
                if (pd != null) {
                    pd.dismiss();
                }

                try {
                    int cred_postalcountused = cd != null ? cd.cred_postalcountused : 0;
                    int cred_postalcountlimit = cd != null ? cd.cred_postalcountlimit : 0;
                    int bal = 0;
                    bal = (cred_postalcountlimit - cred_postalcountused) < 0 ? 0 : (cred_postalcountlimit - cred_postalcountused);
                    String msg = String.valueOf(bal);
                    lblunlock.setText(msg);
                } catch (Exception ex) {
                    objGlobalMethods.writeToFile("Exception at DrawerMain2Activity SetLimitedGCETANew onPostExecute lblunlock.setText(msg). Exception: "
                            + ex.getMessage() + "\nStack: " + JobsHandler.getStackTraceString(ex), "", "", "", "");
                }

                if (success) {
                    List<JobQueue> NewJQlist = null;
                    adapter2.setitemEnableMoveArray(false, 0);

                    NewJQlist = adapter2.getJQlist();
                    JobQueue njq = null;
                    boolean exists = false;
                    if (NewJQlist != null && NewJQlist.size() > 0 && postalsList != null && postalsList.size() > 0) {
                        for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                            njq = NewJQlist.get(i);
                            if (!njq.IsHeader) {
                                if (postalsList.contains(njq.PostalCode)) {
                                    NewJQlist.get(i).setisUpdated(true);
                                    NewJQlist.get(i).setisMoved(true);
                                    NewJQlist.get(i).setIsEnabled(true);
                                    for (int p = 0; p <= postalsList.size() - 1; p++) {
                                        if (postalsList.get(p).equals(njq.PostalCode)) {
                                            NewJQlist.get(i).setGC_Queue(p + 1);
                                            break;
                                        }
                                    }
                                } else if (!((NewJQlist.get(i).Flag & 4) == 4)) {
                                    NewJQlist.get(i).setisUpdated(false);
                                    NewJQlist.get(i).setisMoved(false);
                                    NewJQlist.get(i).setIsEnabled(false);
                                    NewJQlist.get(i).setValid(false);
                                }
                            }
                        }
                    }
                    NewJQlist = JobsHandler.RearrangeValidJobQueTop(NewJQlist, false);
                    adapter2.setNewJQlist(NewJQlist);

                    hideShowUpdateCtrls(false, false);
                    adapter2.notifyDataSetChanged();
                    adapter2.setOriginalJQlist(null);
                    adapter2.finaloldPosition = null;
                    adapter2.finalnewPosition = null;
                    objGlobalMethods.writeToFile("10", "", "", "", "");
                    ctrlSettings.enableDisableEditButtonWithStyles(false, btnEditRoute);
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    adapter2.setsaveAmended4QuesJsonStr(objGlobalMethods.getVrpRelatedSP_KeyValue("saveAmended4QuesJsonStr"));
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "lastpostal", "");
                    String ProcessJobQueJsonStr = new Gson().toJson(NewJQlist);
                    objGlobalMethods.setVrpRelatedSP_KeyValue2(sp, "ProcessJobQueJsonStr", ProcessJobQueJsonStr);
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_routeupdated)).show();
                } else {
                    if (adapter2 == null) {
                        adapter2 = initAdapter();
                    }
                    if (adapter2 != null) {
                        List<JobQueue> al = adapter2.getJQlist();
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String NewJQlistitempos = objGlobalMethods.getGCMPrefs(GcmPreferences, "NewJQlistitempos");
                        int itempos = !NewJQlistitempos.isEmpty() && !NewJQlistitempos.equals("") ? Integer.parseInt(NewJQlistitempos) : 0;
                        adapter2.setNewJQlist(al);
                        adapter2.setitemEnableMoveArray(false, itempos);
                        hideShowUpdateCtrls(false, false);
                        adapter2.nullOriginalJQlist();
                        adapter2.finaloldPosition = null;
                        adapter2.finalnewPosition = null;
                        adapter2.notifyDataSetChanged();
                    } else {
                        showDialog(ctx.getString(R.string.alert_errorupdateroute));
                    }
                }

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at DrawerMain2Activity SetLimitedGCETANew onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");

                List<JobQueue> al = adapter2.getOriginalJQlist();
                adapter2.setNewJQlist(al);
                adapter2.setitemEnableMoveArray(false, 0);
                hideShowUpdateCtrls(false, false);
                adapter2.notifyDataSetChanged();
                adapter2.nullOriginalJQlist();
                adapter2.finaloldPosition = null;
                adapter2.finalnewPosition = null;
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorupdateroute)).show();
            }
        }
    }

    public class SetLimitedGCETA2 extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pd;
        private final ArrayList<vrpUnlockPostData> selectedList;
        //private List<Integer> QueIdxList = null;
        private List<String> postalsList = null;
        private List<JobQueue> DiffSaveAmended4Ques = null;

        public SetLimitedGCETA2(ArrayList<vrpUnlockPostData> aselectedList) {
            this.selectedList = aselectedList;
            this.pd = new ProgressDialog(myApp.currentActivity);
            this.pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.pd.setCanceledOnTouchOutside(false);
            this.pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean asd = false;
            List<JobQueue> NewJQlist = null;
            List<JobQueue> al = new ArrayList<JobQueue>();
            objGC_CustomQueue GC_CustomQueue = null;
            ArrayList<objGC_CustomQueue> AQueue = null;
            JobQueue jq = null;
            JobQueue njq = null;
            vrpUnlockPostData vp = null;
            int queue = 0;
            List<Long> jqJobsIDXsList = null;
            String log = "";
            List<String> credOpenedPostals = null;
            String credOpenedPostalsJson = "";
            SharedPreferences sp = objGlobalMethods.getVrpRelatedSharedPreferences();
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            String saveAmended4QuesJsonStr = "";

            try {
                /*credOpenedPostalsJson = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "credOpenedPostalsJson");
                if (!objGlobalMethods.IsEmptyString(credOpenedPostalsJson))
                    credOpenedPostals = new Gson().fromJson(credOpenedPostalsJson, new TypeToken<ArrayList<String>>() {
                    }.getType());*/

                if (selectedList != null && selectedList.size() > 0) {
                    NewJQlist = adapter2.getJQlist();

                    for (int i = 0; i <= selectedList.size() - 1; i++) {
                        vp = selectedList.get(i);

                        if (postalsList == null)
                            postalsList = new ArrayList<>();
                        if (!objGlobalMethods.IsEmptyString(vp.POSTAL) && !postalsList.contains(vp.POSTAL))
                            postalsList.add(vp.POSTAL);
                    }

                    //selectedList.sort(Comparator.comparingInt(o -> o.position)); ////already done in vrpUnlockListViewAdapter.getSelectedList()

                    for (int i = 0; i <= selectedList.size() - 1; i++) {
                        vp = selectedList.get(i);

                        if (NewJQlist != null && NewJQlist.size() > 0) {
                            for (int n = 0; n <= NewJQlist.size() - 1; n++) {
                                njq = NewJQlist.get(n);
                                if (njq.type.equals("vrp") && njq.PostalCode != null && njq.PostalCode.equals(vp.POSTAL)) {

                                    jqJobsIDXsList = njq.getJobsIDXsList();
                                    if (jqJobsIDXsList != null && jqJobsIDXsList.size() > 0) {
                                        if (al == null)
                                            al = new ArrayList<>();

                                        if (al.contains(njq))
                                            continue;

                                        al.add(njq);

                                        //if (QueIdxList == null)
                                        //QueIdxList = new ArrayList<>();
                                        //QueIdxList.add(n);
                                        queue++;
                                        for (int x = 0; x <= jqJobsIDXsList.size() - 1; x++) {
                                            //queue++;
                                            GC_CustomQueue = new objGC_CustomQueue(jqJobsIDXsList.get(x), queue);
                                            if (AQueue == null) {
                                                AQueue = new ArrayList<objGC_CustomQueue>();
                                            }
                                            AQueue.add(GC_CustomQueue);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }


                    if (postalsList != null && postalsList.size() > 0) {
                        log = String.join(",", postalsList);
                        objGlobalMethods.writeToFile("Opening jobs for postal(s) " + log, "", "", "", "");
                    }

                    if (AQueue != null && AQueue.size() > 0) {
                        SharedPreferences updateJobInfoPref = objGlobalMethods.getUpdateJobInfoPreferences();
                        String regid = objGlobalMethods.getRegistrationId(myApp.getAppContext(), gcmp);

                        Gson gson = new Gson();
                        saveAmended4QuesJsonStr = al != null && al.size() > 0 ? gson.toJson(al) : "";

                        objGlobalMethods.setVrpRelatedSP_KeyValue2(sp, "saveAmended4QuesJsonStr", saveAmended4QuesJsonStr);
                        String[] UsernamePwd = new String[4];
                        String useridx = "";
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
                        useridx = UsernamePwd[2];
                        Date dt = new Date();
                        Long l = dt.getTime();
                        String timestampstr = "/Date(" + l + ")/";
                        String finalInitialSetOfJobsIDXsJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "InitialSetOfJobsIDXsJsonStr");
                        String[] qal = new String[]{
                                "true", saveAmended4QuesJsonStr, finalInitialSetOfJobsIDXsJsonStr
                        };
                        String alJsonStr = qal != null ? gson.toJson(qal) : "";
                        if (!alJsonStr.isEmpty() && !alJsonStr.equals("[]") && !alJsonStr.equals("")) {
                            objGlobalMethods.setGCMPrefs(gcmp, "QCabinetDataJsonStr", alJsonStr);
                        }
                        objCabinet cabinet = new objCabinet(0, Long.parseLong(!useridx.isEmpty() && !useridx.equals("") && useridx != "" ? useridx : "0"),
                                "2", alJsonStr, timestampstr);
                        objETACabinet ETACabinet = new objETACabinet(regid, cabinet, AQueue);
                        objGlobalMethods.writeToFile("GC opens New GC Queue. Updating new GC Queue.", "", "", "", "");
                        JobsHandler.addUpdateGCETA_n_Cabinet_List(updateJobInfoPref, ETACabinet);
                        myApp.getAppContext().sendBroadcast(new Intent(myApp.getAppContext(), BackendRun_BroadcastReceiver.class));
                    }
                }

                credOpenedPostals = postalsList;
                credOpenedPostalsJson = credOpenedPostals != null && credOpenedPostals.size() > 0 ? new Gson().toJson(credOpenedPostals) : "";

                Date dt = new Date();
                long dtlong = dt.getTime();
                String credEditedDateLong = String.valueOf(dtlong);
                //credEditedDateLong = "1749967573000";

                String[] vrpspprefName = new String[]{"credOpenedPostalsJson", "credEditedDateLong"};
                String[] vrpspprefvalue = new String[]{credOpenedPostalsJson, credEditedDateLong};
                objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);

                asd = true;

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at DrawerMain2Activity SetLimitedGCETA doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Context ctx = myApp.getAppContext();
            SharedPreferences sp = objGlobalMethods.getVrpRelatedSharedPreferences();
            try {
                if (pd != null) {
                    pd.dismiss();
                }
                //calculateAvailCredits(true);

                try {
                    String cred_postalcountused = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "cred_postalcountused");
                    cred_postalcountused = objGlobalMethods.IsEmptyString(cred_postalcountused) ? "0" : cred_postalcountused;
                    String cred_postalcountlimit = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "cred_postalcountlimit");
                    cred_postalcountlimit = objGlobalMethods.IsEmptyString(cred_postalcountlimit) ? "0" : cred_postalcountlimit;
                    String cred_postalcount = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "cred_postalcount");
                    cred_postalcount = objGlobalMethods.IsEmptyString(cred_postalcount) ? "0" : cred_postalcount;
                    //String msg = cred_postalcountused + "\n" + cred_postalcountlimit + "\n" + cred_postalcount;

                    int bal = 0;
                    //cred_postalcountused
                    int usedpostalcount = Integer.parseInt(cred_postalcountused);
                    int limitpostalcount = Integer.parseInt(cred_postalcountlimit);
                    bal = (limitpostalcount - usedpostalcount) < 0 ? 0 : (limitpostalcount - usedpostalcount);
                    String msg = String.valueOf(bal);
                    lblunlock.setText(msg);
                } catch (Exception ex) {
                    objGlobalMethods.writeToFile("Exception at DrawerMain2Activity SetLimitedGCETA onPostExecute lblunlock.setText(msg). Exception: "
                            + ex.getMessage() + "\nStack: " + JobsHandler.getStackTraceString(ex), "", "", "", "");
                }

                if (success) {
                    List<JobQueue> NewJQlist = null;
                    adapter2.setitemEnableMoveArray(false, 0);

                    NewJQlist = adapter2.getJQlist();
                    JobQueue njq = null;
                    boolean exists = false;
                    if (NewJQlist != null && NewJQlist.size() > 0 && postalsList != null && postalsList.size() > 0) {
                        for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                            njq = NewJQlist.get(i);
                            if (!njq.IsHeader) {
                                if (postalsList.contains(njq.PostalCode)) {
                                    NewJQlist.get(i).setisUpdated(true);
                                    NewJQlist.get(i).setisMoved(true);
                                    NewJQlist.get(i).setIsEnabled(true);
                                    for (int p = 0; p <= postalsList.size() - 1; p++) {
                                        if (postalsList.get(p).equals(njq.PostalCode)) {
                                            NewJQlist.get(i).setGC_Queue(p + 1);
                                            break;
                                        }
                                    }
                                } else if (!((NewJQlist.get(i).Flag & 4) == 4)) {
                                    NewJQlist.get(i).setisUpdated(false);
                                    NewJQlist.get(i).setisMoved(false);
                                    NewJQlist.get(i).setIsEnabled(false);
                                    NewJQlist.get(i).setValid(false);
                                }

                                /*if (DiffSaveAmended4Ques != null && DiffSaveAmended4Ques.size() > 0) {
                                    exists = false;
                                    for (int d = 0; d <= DiffSaveAmended4Ques.size() - 1; d++) {
                                        if (DiffSaveAmended4Ques.get(d).PostalCode.equals(njq.PostalCode)) {
                                            exists = true;
                                            break;
                                        }
                                    }
                                    if (exists) {
                                        NewJQlist.get(i).setisUpdated(false);
                                        NewJQlist.get(i).setisMoved(false);
                                        NewJQlist.get(i).setIsEnabled(false);
                                    }
                                }*/
                            }

                        }
                    }
                    NewJQlist = JobsHandler.RearrangeValidJobQueTop(NewJQlist, false);
                    adapter2.setNewJQlist(NewJQlist);

                    hideShowUpdateCtrls(false, false);
                    adapter2.notifyDataSetChanged();
                    adapter2.setOriginalJQlist(null);
                    adapter2.finaloldPosition = null;
                    adapter2.finalnewPosition = null;
                    objGlobalMethods.writeToFile("10", "", "", "", "");
                    ctrlSettings.enableDisableEditButtonWithStyles(false, btnEditRoute);
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    adapter2.setsaveAmended4QuesJsonStr(objGlobalMethods.getVrpRelatedSP_KeyValue("saveAmended4QuesJsonStr"));
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "lastpostal", "");
                    String ProcessJobQueJsonStr = new Gson().toJson(NewJQlist);
                    objGlobalMethods.setVrpRelatedSP_KeyValue2(sp, "ProcessJobQueJsonStr", ProcessJobQueJsonStr);
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_routeupdated)).show();
                } else {
                    if (adapter2 == null) {
                        adapter2 = initAdapter();
                    }
                    if (adapter2 != null) {
                        List<JobQueue> al = adapter2.getJQlist();
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String NewJQlistitempos = objGlobalMethods.getGCMPrefs(GcmPreferences, "NewJQlistitempos");
                        int itempos = !NewJQlistitempos.isEmpty() && !NewJQlistitempos.equals("") ? Integer.parseInt(NewJQlistitempos) : 0;
                        adapter2.setNewJQlist(al);
                        adapter2.setitemEnableMoveArray(false, itempos);
                        hideShowUpdateCtrls(false, false);
                        adapter2.nullOriginalJQlist();
                        adapter2.finaloldPosition = null;
                        adapter2.finalnewPosition = null;
                        adapter2.notifyDataSetChanged();
                    } else {
                        showDialog(ctx.getString(R.string.alert_errorupdateroute));
                    }
                }

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at DrawerMain2Activity SetLimitedGCETA onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");

                List<JobQueue> al = adapter2.getOriginalJQlist();
                adapter2.setNewJQlist(al);
                adapter2.setitemEnableMoveArray(false, 0);
                hideShowUpdateCtrls(false, false);
                adapter2.notifyDataSetChanged();
                adapter2.nullOriginalJQlist();
                adapter2.finaloldPosition = null;
                adapter2.finalnewPosition = null;
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorupdateroute)).show();
            }
        }
    }

    public void enableDisableEditRouteButtons(boolean enabled) {
        try {
            btnResetRoute.setEnabled(enabled);
            btnCancelUpdateRoute.setEnabled(enabled);
            btnUpdateRoute.setEnabled(enabled);
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity enableDisableEditRouteButtons. Exception: " + s, "", "", "", "");
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

    private View.OnClickListener LLpercentage_OnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    recalRoute rr = new recalRoute();
                    rr.execute();
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at DrawerMain2Activity LLpercentage_OnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private View.OnLongClickListener LLpercentage_OnLongClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    recalRoute rr = new recalRoute();
                    rr.execute();
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at DrawerMain2Activity LLpercentage_OnLongClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_errorperformoperation)).show();
                } finally {
                    return false;
                }
            }
        };
    }

    public void extCallForRecalRoute() {
        try {
            recalRoute rr = new recalRoute();
            rr.execute();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity extCallForRecalRoute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public class recalRoute extends AsyncTask<Void, Void, Boolean> {
        public recalRoute() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdRecalRoute.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean L2_SaveCabinet = false;
            boolean L2_DeleteProblem = false;
            boolean L2_CreateProblem = false;
            long driveridx = 0;
            try {
                String regid = objGlobalMethods.getRegistrationIdOnly();
                if (!objGlobalMethods.IsEmptyString(regid)) {
                    String[] UsernamePwd = new String[4];
                    String useridx = "";
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(objGlobalMethods.getGcmPreferences());
                    useridx = UsernamePwd[2];

                    Date dt = new Date();
                    Long l = dt.getTime();
                    String timestampstr = "/Date(" + l + ")/";

                    String[] qal = new String[]{
                            "false", "", ""
                    };
                    String alJsonStr = qal != null ? new Gson().toJson(qal) : "";

                    objCabinet cabinet = new objCabinet(0, Long.parseLong(!useridx.isEmpty() && !useridx.equals("") && useridx != "" ? useridx : "0"), "2", alJsonStr, timestampstr);

                    L2_SaveCabinet = WebService.SaveCabinet(regid, cabinet);
                }

                if (L2_SaveCabinet) {
                    L2_DeleteProblem = WebService.L2_DeleteProblem(regid);
                }

                /*if (!objGlobalMethods.IsEmptyString(regid)) {
                    L2_DeleteProblem = WebService.L2_DeleteProblem(regid);
                }*/
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

                    String[] prefName = new String[]{"lastpostal", "lastJobQueueDataJsonStr", "JobsPostalsToBeUNPARKEDJsonStr"};
                    String[] prefvalue = new String[]{"", "", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    /*String JobsPostalsToBeUNPARKEDJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "JobsPostalsToBeUNPARKEDJsonStr");
                    ArrayList<String> JobsPostalsToBeUNPARKED = null;
                    if (!objGlobalMethods.IsEmptyString(JobsPostalsToBeUNPARKEDJsonStr)) {
                        JobsPostalsToBeUNPARKED = new Gson().fromJson(JobsPostalsToBeUNPARKEDJsonStr, new TypeToken<ArrayList<String>>() {
                        }.getType());
                    }*/
                    JobsHandler.getJobQue2(regid, true, true, ListItemIndex, null);
                }
                objGlobalMethods.setGCMPrefs(GcmPreferences, "prevfirstQlocation", "");
            } catch (Exception e) {
                L2_CreateProblem = false;
                objGlobalMethods.writeToFile("Exception at DrawerMain2Activity recalRoute doInBackground. Exception: " + e.getMessage(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ST_CreateProblem successful at DrawerMain2Activity.", "", "", "", "");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "RouteList", "");
                    loadVrpList2(false, false);
                } else {
                    objGlobalMethods.writeToFile("ST_CreateProblem unsuccessful at DrawerMain2Activity.", "", "", "", "");
                    //showDialog(myApp.getAppContext().getString(R.string.recalroute_notok)).show();
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at DrawerMain2Activity recalRoute onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                if (pdRecalRoute != null) {
                    pdRecalRoute.dismiss();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at DrawerMain2Activity", "", "", "", "");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DrawerMain2Activity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }


}
