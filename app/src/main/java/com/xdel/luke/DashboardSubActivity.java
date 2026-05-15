package com.xdel.luke;

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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xdel.luke.BCnSVC.SendQueuedEmail_Receiver;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.mpd.mpdclass;
import com.xdel.luke.objClasses.DashboardSub_PostData;
import com.xdel.luke.objClasses.DashboardSub_PostItemAdapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjCategoryCount;
import com.xdel.luke.sqlite.dbObjJobs;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class DashboardSubActivity extends AppCompatActivity {

    private final Semaphore mutex = new Semaphore(1);
    static Context context;
    ConnectionDetector cd;
    private ProgressDialog pdRouting;
    static boolean errored = false;
    private String[] val;
    private ListView listView;
    private TextView DashboardSubtext;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    private int queCount = 0;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    public static EditText txtDashboard2Input;
    private MediaPlayer player;
    static String cameraScannedCode;
    private LinearLayout DashboardSubUnreadMsgLinearLayout;
    private LinearLayout DashboardSubListviewLinearLayout;

    String langType = "en";
    private static Boolean isDunning = false;
    public NfcAdapter mNfcAdapter;
    public static String frompage;
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        GcmPreferences = getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");

        /*String languageToLoad = langType; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/

        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_dashboard_sub);

        val = new String[]{
                myApp.getAppContext().getString(R.string.item_dashboard2PIP),
                myApp.getAppContext().getString(R.string.item_dashboard2DIP)};

        listView = (ListView) findViewById(R.id.listView);
        DashboardSubtext = (TextView) findViewById(R.id.DashboardSubtext);
        txtDashboard2Input = (EditText) findViewById(R.id.txtDashboard2Input);

        pdRouting = new ProgressDialog(this);
        pdRouting.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdRouting.setCanceledOnTouchOutside(false);
        pdRouting.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveRouting));
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            GcmPreferences = getGcmPreferences();
            UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            setTitle("");
            isDunning = false;
            CODupdate.al = null;
            CODupdate.total = 0.0;

            context = myApp.getAppContext();

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];

            String address = objGlobalMethods.getGCMPrefs(GcmPreferences, "desAddress");
            String postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");

            lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

            //DashboardSubtext.setText(address + (!objGlobalMethods.IsDefaultVRPpage() ? ", " + postal : ""));
            DashboardSubtext.setText(address + (!isVrpView() ? ", " + postal : ""));

            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");

            /*if (!myApp.isMobileDateOn()){
                showMobileNetworkSettingDialog();
                return;
            }*/

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
                objGlobalMethods.writeToFile("DashboardSubActivity !BTEnabled", "", "", "", "");
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
                //objGlobalMethods.writeToFile("Exception caught at DashboardSubActivity onResume mNfcAdapter portion. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
            }

            String[] prefName = new String[]{"showMPD_Dialog_IsShowing", "showRegBTTL_Dialog_IsShowing", "FailAsND", "FailAsND_Reason"};
            String[] prefvalue = new String[]{"", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            objGlobalMethods.resetAllImageRelatedStaticVariables(false);
            objGlobalMethods.resetJobsListGroupScrollIndexNpostion(GcmPreferences);
            objGlobalMethods.offSpeaker();
            JobsHandler.clearAllStaticFromPageAndNextActivityFields();
            objGlobalMethods.clearOnResumeDashboardSubActivityGcmPrefs(GcmPreferences);
            JobsHandler.clearCheckListActivityStatic(GcmPreferences, true);
            JobsHandler.clearDunningActivityPrefs(GcmPreferences);
            JobsHandler.clearCallActivityPrefs(GcmPreferences);
            JobsHandler.clearlAllDetailPagesStatic();
            JobsHandler.clearJobsRetrievalActivityPrefs(GcmPreferences);
            JobsHandler.clearJobsListGroupPrefs(GcmPreferences);
            JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "","", true);
            JobsHandler.checkForceLogOut();

            if (username.equals("") && password.equals("")) {
                BackToLoginPage();
            }

            if (cd.isConnectingToInternet()) {
                objGlobalMethods.requireGcReLogin();
            }

            if (JobsHandler.hasOpsQ() && !JobsHandler.isReadingFirstQue_Location_Or_Job(myApp.currentActivity.getClass())) {
                JobsHandler.redirectToVrpPage();
            } else {
                loadListSQLite2();

                queCount = JobsHandler.returnNumberOfQueToSend(UpdateJobInfoPreferences);
                if (queCount > 0) {
                    sendBroadcast(new Intent(context, BackendRun_BroadcastReceiver.class));
                }

                DashboardSubUnreadMsgLinearLayout = (LinearLayout) findViewById(R.id.DashboardSubUnreadMsgLinearLayout);
                DashboardSubListviewLinearLayout = (LinearLayout) findViewById(R.id.DashboardSubListviewLinearLayout);
                objGlobalMethods.getMsgReminder(GcmPreferences, "DashboardSubActivity", DashboardSubUnreadMsgLinearLayout, DashboardSubListviewLinearLayout);
                objGlobalMethods.checkToPromptNotiForBattCharging();

                int qe = JobsHandler.numOfQueuedEmails();
                if (qe > 0) {
                    objGlobalMethods.writeToFile("SendQueuedEmailReceiver called at DashboardSubActivity.","","","","");
                    Intent SendQueuedEmailReceiver = new Intent(context, SendQueuedEmail_Receiver.class);
                    sendBroadcast(SendQueuedEmailReceiver);
                }
            }
        }
    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DashboardSubActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
        DAO_Jobs.nullifystaticitems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity = null;
        try {
            if(mNfcAdapter!= null)
                mNfcAdapter.disableForegroundDispatch(this);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DashboardSubActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DashboardSubActivity onPause unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void DashboardSubUnreadMsgtextClick(View view) {
        Intent parentIntent = new Intent(this, Messaginglist.class);
        startActivity(parentIntent);
        this.finish();
    }

    //Start showDialog
    public Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        //String stack = JobsHandler.getStackTraceString(e);
                        //objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                        objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardSubActivity.this);
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
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("ActivityNotFoundException at DashboardSubActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DashboardSubActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPStoHighAccModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardSubActivity.this);
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
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("ActivityNotFoundException at DashboardSubActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DashboardSubActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("ActivityNotFoundException at DashboardSubActivity showChangePermissionDialog. ActivityNotFoundException: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DashboardSubActivity showChangePermissionDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showMobileNetworkSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardSubActivity.this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_mobiledatamode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    /*Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                    startActivity(intent);*/
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                } catch (ActivityNotFoundException e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("ActivityNotFoundException at DashboardSubActivity showMobileNetworkSettingDialog. ActivityNotFoundException: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DashboardSubActivity showMobileNetworkSettingDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Dialog showDialogNfocusTxtbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_errorretrivedata2));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "selectedRouteAddressJobsIDXs", "");
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                Intent i;
                objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                //i = new Intent(DashboardSubActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(DashboardSubActivity.this, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                startActivity(i);
                finish();
                dialog.dismiss();

            }
        });

        return builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //internet connection check is inside LogOut function
        GcmPreferences = getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "selectedRouteAddressJobsIDXs", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", "");
        Intent i = null;
        if (frompage == "MapsAllActivity") {
            i = new Intent(DashboardSubActivity.this, MapsAllActivity.class);
        } else {
            String RouteList = objGlobalMethods.getGCMPrefs(GcmPreferences, "RouteList");
            /*i = new Intent(DashboardSubActivity.this,
                    !objGlobalMethods.IsDefaultVRPpage() && (!RouteList.isEmpty() || !RouteList.equals("") || RouteList != "") ? AddressListRoutingActivity.class :
                            !objGlobalMethods.IsDefaultVRPpage() && (RouteList.isEmpty() || RouteList.equals("") || RouteList == "") ? DrawerMainActivity.class
                                    : DrawerMain2Activity.class);*/
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            i = new Intent(DashboardSubActivity.this,
                    (!isVrpView() || IsMaintenanceHours) && (!RouteList.isEmpty() || !RouteList.equals("") || RouteList != "") ? AddressListRoutingActivity.class :
                            (!isVrpView() || IsMaintenanceHours) && (RouteList.isEmpty() || RouteList.equals("") || RouteList == "") ? DrawerMainActivity.class
                                    : DrawerMain2Activity.class);
        }
        startActivity(i);
    }

    public void BackToLoginPage() {
        GcmPreferences = getGcmPreferences();
        UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "selectedRouteAddressJobsIDXs", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", "");
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        JobsHandler.clearGPSQue(LocationPrefs);
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
        //objGlobalMethods.stopAlarmForCommon();
        FCMservice.notiNotRead = false;

        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(2);
        }
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)  {
            objGlobalMethods.stopActiveForegroundSvc();
        } else {
            if (FCMservice.mNotificationManager != null) {
                FCMservice.mNotificationManager.cancel(-1);
            }
        }
        Intent parentIntent;
        parentIntent = new Intent(DashboardSubActivity.this, MainActivity.class);
        DAO_Jobs.dropLukeDB();
        startActivity(parentIntent);
        finish();
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGcmPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private SharedPreferences getUpdateJobInfoPreferences() {
        return getSharedPreferences("UpdateJobInfoPrefs",
                Context.MODE_PRIVATE);
    }

    private String getRegistrationId() {
        final SharedPreferences prefs = getGcmPreferences();
        String registrationId = prefs.getString(objGlobalVariables.PROPERTY_REG_ID, "");
        //String registrationId = "";
        if (registrationId.isEmpty()) {

            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(objGlobalVariables.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(myApp.getAppContext());
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    private void loadListSQLite2(){
        ArrayList<DashboardSub_PostData> dashboardSubPostDataArr = new ArrayList<DashboardSub_PostData>();
        DashboardSub_PostData postdata;
        int count = 0;
        List<dbObjCategoryCount> dbObjCategoryCountAL = null;
        try {
            String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
            if (Postal != "") {
                for (int i =0; i<=2; i++) {
                    count = 0;

                    if (i == 0) {
                        //if (!objGlobalMethods.IsDefaultVRPpage()) {
                        if (!isVrpView()) {
                            dbObjCategoryCountAL = DAO_Jobs.getTotalJobsCountPerCatTypePerPostalList(0, Postal);
                        } else {
                            dbObjCategoryCountAL = DAO_Jobs.getTotalJobsCountPerCatTypePerPostalList(0, Postal);
                        }
                    } else if (i == 2) {
                        //if (!objGlobalMethods.IsDefaultVRPpage()) {
                        if (!isVrpView()) {
                            dbObjCategoryCountAL = DAO_Jobs.getTotalJobsCountPerCatTypePerPostalList(2, Postal);
                        } else {
                            dbObjCategoryCountAL = DAO_Jobs.getTotalJobsCountPerCatTypePerPostalList(2, Postal);

                        }
                    }

                    if (i == 0) {
                        if (dbObjCategoryCountAL != null && dbObjCategoryCountAL.size() > 0) {
                            postdata = new DashboardSub_PostData(val[i], String.valueOf(dbObjCategoryCountAL.get(0).Count), Postal, false);
                            postdata.setColdCount(dbObjCategoryCountAL.get(0).ColdCount);
                        } else {
                            postdata = new DashboardSub_PostData(val[i], "0", Postal, false);
                            postdata.setColdCount(0);
                        }
                        dashboardSubPostDataArr.add(postdata);
                    } else if (i == 2) {
                        if (dbObjCategoryCountAL != null && dbObjCategoryCountAL.size() > 0) {
                            postdata = new DashboardSub_PostData(val[i-1], String.valueOf(dbObjCategoryCountAL.get(0).Count), Postal, false);
                            postdata.setReqNetsCount(dbObjCategoryCountAL.get(0).ReqNetsCount);
                            postdata.setReqVisaCount(dbObjCategoryCountAL.get(0).ReqVisaCount);
                            postdata.setReqTiCount(dbObjCategoryCountAL.get(0).ReqTiCount);
                            postdata.setColdCount(dbObjCategoryCountAL.get(0).ColdCount);
                        } else {
                            postdata = new DashboardSub_PostData(val[i-1], "0", Postal, false);
                            postdata.setReqNetsCount(0);
                            postdata.setReqVisaCount(0);
                            postdata.setReqTiCount(0);
                            postdata.setColdCount(0);
                        }
                        postdata.setDIP(true);
                        dashboardSubPostDataArr.add(postdata);
                    }
                }

                if (dashboardSubPostDataArr.size() > 0) {
                    DashboardSub_PostItemAdapter adapter = new DashboardSub_PostItemAdapter(this, dashboardSubPostDataArr);

                    listView.setAdapter(adapter);
                    promptReqMPD();
                    objGlobalMethods.showNewPIPdialog(objGlobalMethods.getGcmPreferences());
                    //pdRouting.dismiss();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent i;
                            view.clearFocus();
                            View v;
                            TextView lblCount;
                            TextView lblhfJobsInfoArrJsonStr;
                            GcmPreferences = getGcmPreferences();
                            switch (position) {
                                case 0:
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2PIP));
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");

                                    v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                    if (v == null) {
                                        return;
                                    } else {
                                        lblCount = (TextView) v.findViewById(R.id.lblCount);
                                        lblhfJobsInfoArrJsonStr = (TextView) v.findViewById(R.id.lblhfJobsInfoArrJsonStr);
                                        startRedirectionSQLite(lblCount, 0, lblhfJobsInfoArrJsonStr.getText().toString());
                                    }

                                    break;
                                case 1:
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2DIP));
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "2");

                                    v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                    if (v == null) {
                                        return;
                                    } else {
                                        lblCount = (TextView) v.findViewById(R.id.lblCount);
                                        lblhfJobsInfoArrJsonStr = (TextView) v.findViewById(R.id.lblhfJobsInfoArrJsonStr);
                                        startRedirectionSQLite(lblCount, 2, lblhfJobsInfoArrJsonStr.getText().toString());
                                    }

                                    break;
                            }
                        }
                    });
                } else {
                    pdRouting.dismiss();
                    objGlobalMethods.writeToFile("No Jobs found at DashboardSubActivity loadListSQLite. Returning to AddressListRoutingActivity.", "", "", "", "");
                    showDialogNfocusTxtbox();
                }
            } else {
                pdRouting.dismiss();
                objGlobalMethods.writeToFile("Postal is empty at DashboardSubActivity loadListSQLite. Returning to AddressListRoutingActivity.", "", "", "", "");
                showDialogNfocusTxtbox();
            }
        } catch (Exception e) {
            pdRouting.dismiss();
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at DashboardSubActivity loadListSQLite. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at DashboardSubActivity loadListSQLite. Exception: " + e.toString(), "", "", "", "");
            showDialogNfocusTxtbox();
        }
    }

    private void loadListSQLite(){
        ArrayList<DashboardSub_PostData> dashboardSubPostDataArr = new ArrayList<DashboardSub_PostData>();
        DashboardSub_PostData postdata;
        int count = 0;
        try {
            String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
            if (Postal != "") {
                for (int i =0; i<=2; i++) {
                    count = 0;

                    if (i == 0) {
                        //if (!objGlobalMethods.IsDefaultVRPpage()) {
                        if (!isVrpView()) {
                            count = DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(0, Postal);
                        } else {
                            String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
                            count = !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(0, Postal, SelectedVRPJobsIDXs) :
                                    DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(0, Postal);
                        }
                    } else if (i == 2) {
                        //if (!objGlobalMethods.IsDefaultVRPpage()) {
                        if (!isVrpView()) {
                            count = DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(2, Postal);
                        } else {
                            String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
                            count = !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(2, Postal, SelectedVRPJobsIDXs) :
                                    DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(2, Postal);
                        }
                    }

                    if (i == 0) {
                        postdata = new DashboardSub_PostData(val[i], String.valueOf(count), Postal, false);
                        dashboardSubPostDataArr.add(postdata);
                    } else if (i == 2) {
                        postdata = new DashboardSub_PostData(val[i-1], String.valueOf(count), Postal, false);
                        dashboardSubPostDataArr.add(postdata);
                    }
                }

                if (dashboardSubPostDataArr.size() > 0) {
                    DashboardSub_PostItemAdapter adapter = new DashboardSub_PostItemAdapter(this, dashboardSubPostDataArr);

                    listView.setAdapter(adapter);
                    promptReqMPD();
                    //pdRouting.dismiss();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent i;
                            view.clearFocus();
                            View v;
                            TextView lblCount;
                            TextView lblhfJobsInfoArrJsonStr;
                            GcmPreferences = getGcmPreferences();
                            switch (position) {
                                case 0:
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2PIP));
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");

                                    v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                    if (v == null) {
                                        return;
                                    } else {
                                        lblCount = (TextView) v.findViewById(R.id.lblCount);
                                        lblhfJobsInfoArrJsonStr = (TextView) v.findViewById(R.id.lblhfJobsInfoArrJsonStr);
                                        startRedirectionSQLite(lblCount, 0, lblhfJobsInfoArrJsonStr.getText().toString());
                                    }

                                    break;
                                case 1:
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2DIP));
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "2");

                                    v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                                    if (v == null) {
                                        return;
                                    } else {
                                        lblCount = (TextView) v.findViewById(R.id.lblCount);
                                        lblhfJobsInfoArrJsonStr = (TextView) v.findViewById(R.id.lblhfJobsInfoArrJsonStr);
                                        startRedirectionSQLite(lblCount, 2, lblhfJobsInfoArrJsonStr.getText().toString());
                                    }

                                    break;
                            }
                        }
                    });
                } else {
                    pdRouting.dismiss();
                    objGlobalMethods.writeToFile("No Jobs found at DashboardSubActivity loadListSQLite. Returning to AddressListRoutingActivity.", "", "", "", "");
                    showDialogNfocusTxtbox();
                }
            } else {
                pdRouting.dismiss();
                objGlobalMethods.writeToFile("Postal is empty at DashboardSubActivity loadListSQLite. Returning to AddressListRoutingActivity.", "", "", "", "");
                showDialogNfocusTxtbox();
            }
        } catch (Exception e) {
            pdRouting.dismiss();
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at DashboardSubActivity loadListSQLite. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at DashboardSubActivity loadListSQLite. Exception: " + e.toString(), "", "", "", "");
            showDialogNfocusTxtbox();
        }
    }

    private void startRedirectionSQLite(TextView lblCount, int categoryType, String Postal) {
        Intent i;
        int count;
        GcmPreferences = getGcmPreferences();
        try {
            if (!lblCount.getText().toString().isEmpty()) {
                if (!lblCount.getText().toString().toLowerCase().equals("error")) {
                    count = Integer.parseInt(lblCount.getText().toString());
                    if (count > 0) {
                        if (count > 1) {
                            //more than 1 jobs, go to addresslist
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "true");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobsInfoArrJsonStr", "");
                            i = new Intent(DashboardSubActivity.this, JobsListGroupActivity.class);
                            JobInfoActivity.accessFromDirection = 0;
                            startActivity(i);
                        } else if (count == 1) {
                            //only 1 job, go str8 to jobInfo
                            int proceed = 0;
                            proceed = initRedirectionToJobInfoSQLite(categoryType, Postal);
                            /*if (proceed == 1) {
                                if (categoryType == 0) {
                                    //objGlobalMethods.writeToFile("stopGpsAlarm at DashboardSubActivity startRedirectionSQLite", "", "", "", "");
                                    //objGlobalMethods.stopAlarmForCommon();
                                    if (FCMservice.mNotificationManager != null) {
                                        FCMservice.mNotificationManager.cancel(2);
                                    }
                                    FCMservice.notiNotRead = false;
                                }
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "true");
                                if (isDunning) {
                                    i = new Intent(DashboardSubActivity.this, setDunningDeliveredStatusActivity.class);
                                    JobInfoActivity.accessFromDirection = 0;
                                } else {
                                    i = new Intent(DashboardSubActivity.this, JobInfoActivity.class);
                                    JobInfoActivity.accessFromDirection = 1;
                                }
                                startActivity(i);
                            } else if (proceed > 1) {
                                //more than 1 jobs, go to addresslist
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "true");
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobsInfoArrJsonStr", "");
                                i = new Intent(DashboardSubActivity.this, JobsListGroupActivity.class);
                                JobInfoActivity.accessFromDirection = 0;
                                startActivity(i);
                            } else if (proceed == 0) {
                                loadListSQLite();
                            }*/

                            if (proceed > 0) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "true");
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobsInfoArrJsonStr", "");
                                i = new Intent(DashboardSubActivity.this, JobsListGroupActivity.class);
                                JobInfoActivity.accessFromDirection = 0;
                                startActivity(i);
                            } else {
                                loadListSQLite();
                            }

                        }
                    } else if (count == 0) {
                        //0 jobs, do nothing
                    }
                } else {
                    //show error instead of count, do nothing
                }
            } else {
                //assume to be 0 jobs, do nothing
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at DashboardSubActivity, startRedirection." + "\n" + e.getMessage().toString() + "\nStack: " + stack, "", "", username, useridx);
            //JobsHandler.sendErrorEmail("Exception caught at DashboardSubActivity for " + username + ", startRedirection." + "\n" + e.getMessage().toString());
            objGlobalMethods.writeToFile("Exception caught at DashboardSubActivity, startRedirection." + "\n" + e.getMessage().toString(), "", "", username, useridx);
        }

    }

    private int initRedirectionToJobInfoSQLite(int CatTypeID, String Postal) {
        int asd = 0;
        Gson gson = new Gson();
        String JsonStr;
        objJobInfo JobInfo;
        String postal = "";
        String address = "";
        String addressWithUnit = "";
        double LAT = 0;
        double LNG = 0;
        GcmPreferences = getGcmPreferences();
        Boolean hasCOD = false;

        try {
            String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
            /*List<dbObjJobs> objAL = !objGlobalMethods.IsDefaultVRPpage() ? DAO_Jobs.getAlldbObjJobsPerCategoryForJobsList(CatTypeID, Postal) :
                    !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getAlldbObjJobsPerCategoryForJobsListByJobsIDXs(CatTypeID, Postal, SelectedVRPJobsIDXs) : DAO_Jobs.getAlldbObjJobsPerCategoryForJobsList(CatTypeID, Postal);*/
            List<dbObjJobs> objAL = !isVrpView() ? DAO_Jobs.getAlldbObjJobsPerCategoryForJobsList(CatTypeID, Postal) :
                    !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getAlldbObjJobsPerCategoryForJobsListByJobsIDXs(CatTypeID, Postal, SelectedVRPJobsIDXs) : DAO_Jobs.getAlldbObjJobsPerCategoryForJobsList(CatTypeID, Postal);
            if (objAL != null && objAL.size() == 0) {
                asd = 0;
            } else if (objAL != null && objAL.size() > 1) {
                asd = 2;
            } else if (objAL != null && objAL.size() == 1) {
                JsonStr = objAL.get(0).getJobInfoJsonStr();
                objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, JsonStr);
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                if (JsonStr != "") {
                    JobInfo = gson.fromJson(JsonStr, objJobInfo.class);
                    if (JobInfo != null) {
                        hasCOD = JobsHandler.checkForCODwithCatType(JobInfo, CatTypeID);
                        if (hasCOD) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", "true");
                        } else {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", "false");
                        }

                        isDunning = objAL.get(0).getIsDunning();
                        if (isDunning) {

                            if (JobInfo != null) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", JobsHandler.CannotTakePhoto(JobInfo, 2) ? "1" : "0");
                            } else {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
                            }

                            long JobsIDX = JobInfo.JobsIDX;
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX, String.valueOf(JobsIDX));
                            addressWithUnit = objAL.get(0).getAddressWithoutPostal();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "addressWithUnit", addressWithUnit);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "currJobInfoCN", JobInfo.JobNo);
                        }
                        asd = 1;
                    } else {
                        asd = 0;
                    }
                } else {
                    asd = 0;
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at DashboardSubActivity, initRedirectionToJobInfoSQLite." + "\n" + e.getMessage().toString(),
                    "", "", username, useridx);
        } finally {
            return asd;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // TODO: handle Intent
        /*Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if(tag != null) {
            new NdefReaderTask().execute(tag);
        }*/

        try {
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            String action = intent.getAction();
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {


            } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {


            } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                return;
            }

        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at DashboardSubActivity handleIntent. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception at DashboardSubActivity handleIntent. Exception: " + e.getMessage().toString(), "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    public void promptReqMPD(){
        /*objGlobalMethods.promptReqMPD();
        mpdclass.promptRegBTTL();*/
        objGlobalMethods.promptReqMPD_New();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at DashboardSubActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DashboardSubActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
