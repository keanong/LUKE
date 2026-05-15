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
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.Voip.VoipApp;
import com.xdel.luke.mpd.mpdclass;
import com.xdel.luke.objClasses.AddressListRouting_PostData;
import com.xdel.luke.objClasses.AddressListRouting_PostItemAdapter;
import com.xdel.luke.objClasses.RouteLocation;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjJobs;
import com.xdel.luke.sqlite.objRouteJobEarliestTiming;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;
import java.util.List;

public class AddressListRoutingActivity extends AppCompatActivity {

    static boolean errored = false;
    Context context;
    ConnectionDetector cd;
    private String regid;
    private RouteLocation[] RouteLocationAL;
    public static String RouteListJsonStr;
    public static String count;
    private ListView listView;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    private LinearLayout AddressListUnreadMsgLinearLayout;
    private LinearLayout AddressListListviewLinearLayout;
    private MediaPlayer player;
    private ProgressDialog pdSearch;
    String langType = "en";
    private static Boolean isDunning = false;
    private static Integer categoryType = -1;
    public NfcAdapter mNfcAdapter;
    ScreenShotContentObserver screenShotContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
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

        setContentView(R.layout.activity_address_list_routing);

        listView = (ListView) findViewById(R.id.listView);

        pdSearch = new ProgressDialog(this);
        pdSearch.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdSearch.setCanceledOnTouchOutside(false);
        pdSearch.setMessage(myApp.getAppContext().getString(R.string.pd_waitSearch));
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            CODupdate.al = null;
            CODupdate.total = 0.0;

            Gson gson = new Gson();
            context = myApp.getAppContext();

            setTitle(myApp.getAppContext().getString(R.string.title_activity_AddressListRoutingActivity));

            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);

            if (VoipApp.observer != null) {
                try {
                    if (JobInfoActivity.app != null) {
                        JobInfoActivity.app.deinit();
                    } else {
                        VoipApp.observer = null;
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at AddressListRoutingActivity onResume JobInfoActivity.app.deinit(). Exception: " + e.getMessage(), "", "", "", "");
                }
            }

            if (JobsHandler.hasOpsQ()) {
                JobsHandler.redirectToVrpPage();
            } else {
                isDunning = false;
                categoryType = -1;
                objGlobalMethods.setGCMPrefs(GcmPreferences, "selectedRouteAddressID", "");

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
                    objGlobalMethods.writeToFile("AddressListRoutingActivity !BTEnabled", "", "", "", "");
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
                    //objGlobalMethods.writeToFile("Exception caught at AddressListRoutingActivity onResume mNfcAdapter portion. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
                }

                objGlobalMethods.resetAllImageRelatedStaticVariables(false);
                objGlobalMethods.resetJobsListGroupScrollIndexNpostion(GcmPreferences);
                objGlobalMethods.offSpeaker();
                JobsHandler.clearAllStaticFromPageAndNextActivityFields();

                String[] prefName = new String[]{"DOListArrJsonStr", "tempDOCODJsonStr", "dailypoints", "NFCLocationtagPrefVal", "scannedtype", "scannedval",
                        "CannotTakePhoto", "SecuritySealNumber", "IDVerification", "IDVerificationName", "massupdateokpwp2PostDataArr", "tempDOCODListJsonStr",
                        "FailAsND", "FailAsND_Reason", "IMEIVerification", "IMEIv1", "IMEIv2", "hasIMEI_Verification",
                        "hasSerialNum_Verification", "hasVoucher_Verification", "VOUCHERv0", "VOUCHERv1", "VOUCHERv2", "reqLesserImgCompression",
                        "CNIDX", "DestinationLocationJsonStr", "isEarlierThanDeliveryStartTime", "TypeOfPinVerificationStr", "DIPChecklist", "hasOtpStr",
                        "hasVerifiedOTPStr", "CustIDVerificationNotRequiredStr", "isLockerJobStr", "CurrJobInfoStatusCodeIDX", "currJobInfoFlag", "IsCLCN_For_DoorOpenPenalty",
                        "isStandardRunJob", "DatesALJson", "hideshow10to19NewForced"};
                String[] prefvalue = new String[]{"", "", "", "", "", "",
                        "", "", "", "", "", "",
                        "", "", "", "", "", "",
                        "", "", "", "", "", "",
                        "", "", "", "", "", "",
                        "", "", "", "", "", "",
                        "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                objGlobalMethods.saveChecklistSP("");

                JobsHandler.clearCheckListActivityStatic(GcmPreferences, true);
                JobsHandler.clearDunningActivityPrefs(GcmPreferences);
                JobsHandler.clearCallActivityPrefs(GcmPreferences);
                JobsHandler.clearlAllDetailPagesStatic();
                objGlobalMethods.checkToPromptNotiForBattCharging();
                JobsHandler.clearJobsRetrievalActivityPrefs(GcmPreferences);
                JobsHandler.clearJobsListGroupPrefs(GcmPreferences);
                JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "","", true);
                DashboardSubActivity.frompage = null;
                JobsHandler.checkForceLogOut();

                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];

                if (username.equals("") && password.equals("")) {
                    BackToLoginPage2();
                }

                if (cd.isConnectingToInternet()) {
                    objGlobalMethods.requireGcReLogin();
                }

                //regid = getRegistrationId(context);
                regid = objGlobalMethods.getRegistrationId(null, GcmPreferences);

                // Check if Internet present
                if (!cd.isConnectingToInternet()) {
                    //// Internet Connection is not present
                    Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }

                RouteLocationAL = null;
                RouteListJsonStr = getRouteListJsonStr();
                if (!RouteListJsonStr.equals("") && !RouteListJsonStr.equals("[]")) {
                    RouteLocationAL = gson.fromJson(RouteListJsonStr, RouteLocation[].class);
                    //loadList();
                    loadListSQLite();
                } else {
                    showErrorAlert();
                }

                //JobsHandler.doubleCheckUpdatePIPSQLite(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx, false);
                JobsHandler.doubleCheckUpdatePIPSQLiteNew(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);

                AddressListUnreadMsgLinearLayout = (LinearLayout) findViewById(R.id.AddressListUnreadMsgLinearLayout);
                AddressListListviewLinearLayout = (LinearLayout) findViewById(R.id.AddressListListviewLinearLayout);
                objGlobalMethods.getMsgReminder(GcmPreferences, "AddressListRoutingActivity", AddressListUnreadMsgLinearLayout, AddressListListviewLinearLayout);
                listView.clearFocus();
            }
        }
    }

    public void AddressListRoutingUnreadMsgtextClick(View view) {
        Intent parentIntent = new Intent(this, Messaginglist.class);
        startActivity(parentIntent);
        this.finish();
    }

    //Start showDialog

    private void showMobileNetworkSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at AddressListRoutingActivity showMobileNetworkSettingDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeAirplaneModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddressListRoutingActivity.this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_airplanemode));

        //builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (android.os.Build.VERSION.SDK_INT < 17) {
                    try {
                        Intent intentAirplaneMode = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                        intentAirplaneMode.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentAirplaneMode);
                    } catch (ActivityNotFoundException e) {
                        objGlobalMethods.writeToFile("ActivityNotFoundException at AddressListRoutingActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddressListRoutingActivity.this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmode));


            //builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        objGlobalMethods.writeToFile("ActivityNotFoundException at AddressListRoutingActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                    }

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListRoutingActivity showChangeGPSModeSettingsDialog. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

    }

    private void showChangeGPStoHighAccModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddressListRoutingActivity.this);
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
                    /*String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at AddressListRoutingActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");*/
                    objGlobalMethods.writeToFile("ActivityNotFoundException at AddressListRoutingActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
                            + e.toString(), "", "", "", "");
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
                    /*String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at AddressListRoutingActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");*/
                    objGlobalMethods.writeToFile("ActivityNotFoundException at AddressListRoutingActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showErrorAlert() {
        MainActivity.uponLogin = 0;
        //Dashboard2Activity.DashboardOpenFromPush = false;
        DrawerMainActivity.DashboardOpenFromPush = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                Intent i;
                //i = new Intent(AddressListRoutingActivity.this, Dashboard2Activity.class);
                i = new Intent(AddressListRoutingActivity.this, DrawerMainActivity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_address_list_routing, menu);
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
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", "");
        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        //Intent parentIntent = new Intent(this, Dashboard2Activity.class);
        Intent parentIntent = new Intent(this, DrawerMainActivity.class);
        MainActivity.uponLogin = 0;
        //Dashboard2Activity.DashboardOpenFromPush = false;
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();

    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity = null;
        try {
            if (mNfcAdapter != null)
                mNfcAdapter.disableForegroundDispatch(this);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListRoutingActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at AddressListRoutingActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GcmPreferences = null;
        UpdateJobInfoPreferences = null;
        count = null;
        categoryType = null;
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at AddressListRoutingActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
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

    private String getRouteListJsonStr() {
        String JsonStr = "";
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            JsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "RouteList");
        } catch (Exception e) {
            JsonStr = "";
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListRoutingActivity getRouteListJsonStr. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return JsonStr;
        }
    }

    private void loadListSQLite() {
        RouteLocation rl = null;
        ArrayList<Long> newJobsIDXList = null;
        Long[] newJobsIDXListarr = null;
        ArrayList<AddressListRouting_PostData> addresslistPostDataArr = new ArrayList<AddressListRouting_PostData>();
        AddressListRouting_PostData postdata;
        Boolean changeCountColorBool = false;
        Boolean JobsIDXisStillPIPorDIP = false;
        objRouteJobEarliestTiming rjet = null;
        String JobsIDXs = "";

        try {
            if (RouteLocationAL != null && RouteLocationAL.length > 0) {
                for (int i = 0; i <= RouteLocationAL.length - 1; i++) {
                    changeCountColorBool = false;
                    rl = RouteLocationAL[i];

                    for (int x = 0; x <= rl.JobsIDXList.length - 1; x++) {
                        JobsIDXisStillPIPorDIP = DAO_Jobs.JobsIDXisStillPIPorDIP(rl.JobsIDXList[x]);
                        if (JobsIDXisStillPIPorDIP && newJobsIDXList == null) {
                            newJobsIDXList = new ArrayList<>();
                            newJobsIDXList.add(rl.JobsIDXList[x]);
                        } else if (JobsIDXisStillPIPorDIP && newJobsIDXList != null) {
                            newJobsIDXList.add(rl.JobsIDXList[x]);
                        }
                    }

                    if (newJobsIDXList != null && newJobsIDXList.size() > 0) {
                        newJobsIDXListarr = newJobsIDXList.toArray(new Long[newJobsIDXList.size()]);
                        rl.setJobsIDXList(newJobsIDXListarr);

                        rjet = null;
                        JobsIDXs = "";
                        for (int a = 0; a <= rl.JobsIDXList.length - 1; a++) {
                            JobsIDXs += String.valueOf(rl.JobsIDXList[a]) + ",";
                        }
                        if (!JobsIDXs.isEmpty() && !JobsIDXs.equals("")) {
                            JobsIDXs = JobsIDXs.substring(0, JobsIDXs.length() - 1);
                        }
                        if (!JobsIDXs.isEmpty() && !JobsIDXs.equals("")) {
                            rjet = DAO_Jobs.getobjRouteJobEarliestTiming(JobsIDXs);
                        }

                        postdata = new AddressListRouting_PostData(rl.Location.full_address, String.valueOf(rl.JobsIDXList.length),
                                "", rl.Location.postal, Double.parseDouble(rl.Location.lat), Double.parseDouble(rl.Location.lon), changeCountColorBool, rl.Location.id);
                        postdata.setRjet(rjet);

                        addresslistPostDataArr.add(postdata);
                        newJobsIDXList = null;
                    }
                }

                if (addresslistPostDataArr.size() == 0) {
                    //Intent parent = new Intent(this, Dashboard2Activity.class);
                    Intent parent = new Intent(this, DrawerMainActivity.class);
                    startActivity(parent);
                    this.finish();
                } else {
                    AddressListRouting_PostItemAdapter adapter = new AddressListRouting_PostItemAdapter(this, addresslistPostDataArr);
                    listView.setAdapter(adapter);
                    promptReqMPD();
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(AddressListRoutingActivity.this, DashboardSubActivity.class);
                        int count = 0;
                        View v = listView.getChildAt(position - listView.getFirstVisiblePosition());

                        if (v == null) {
                            return;
                        } else {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();

                            // save index and top position
                            int index = listView.getFirstVisiblePosition();
                            View vTop = listView.getChildAt(0);
                            int top = (vTop == null) ? 0 : (vTop.getTop() - listView.getPaddingTop());
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "AddressListRoutingScrollSavedIndex", String.valueOf(index));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "AddressListRoutingScrollSavedPosition", String.valueOf(top));
                            // save index and top position

                            TextView lblAddressListRoutingAddress = (TextView) v.findViewById(R.id.lblAddressListRoutingAddress);
                            TextView lblAddressListRoutingPostal = (TextView) v.findViewById(R.id.lblAddressListRoutingPostal);
                            TextView lblCount = (TextView) v.findViewById(R.id.lblCount);
                            TextView hfJobsIDX = (TextView) v.findViewById(R.id.hfJobsIDX);
                            TextView lblLAT = (TextView) v.findViewById(R.id.lblLAT);
                            TextView lblLNG = (TextView) v.findViewById(R.id.lblLNG);
                            TextView lblid = (TextView) v.findViewById(R.id.lblid);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", lblLAT.getText().toString());
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", lblLNG.getText().toString());
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", lblAddressListRoutingPostal.getText().toString());
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", lblAddressListRoutingAddress.getText().toString());

                            count = Integer.parseInt(lblCount.getText().toString());
                            if (count > 1) {
                                startActivity(i);
                            } else if (count == 1) {
                                startRedirectionSQLite(lblCount, lblAddressListRoutingPostal.getText().toString());
                            } else if (count == 0) {
                                //do nothing
                            }
                        }
                    }
                });

                // restore index and position
                int index = 0;
                int pos = 0;

                try {
                    index = Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "AddressListRoutingScrollSavedIndex"));
                } catch (Exception e) {
                    index = 0;
                }

                try {
                    pos = Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "AddressListRoutingScrollSavedPosition"));
                } catch (Exception e) {
                    pos = 0;
                }

                listView.setSelectionFromTop(index, pos);
                // restore index and position

                String nonSysCalRoute = objGlobalMethods.getGCMPrefs(GcmPreferences, "nonSysCalRoute");
                if (!nonSysCalRoute.equals("")) {
                    Toast.makeText(context, myApp.getAppContext().getString(R.string.toastnoncalculatedroute), Toast.LENGTH_LONG).show();
                }
            } else {
                showErrorAlert();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at AddressListRoutingActivity loadList. Exception: " + e.toString(), "", "", "", "");
            showErrorAlert();
        }
    }

    private void startRedirectionSQLite(TextView lblCount, String Postal) {
        Intent i;
        int count;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        try {
            if (!lblCount.getText().toString().isEmpty()) {
                if (!lblCount.getText().toString().toLowerCase().equals("error")) {
                    count = Integer.parseInt(lblCount.getText().toString());
                    if (count > 0) {
                        if (count > 1) {
                            //more than 1 jobs, go to addresslist
                            i = new Intent(this, DashboardSubActivity.class);
                            JobInfoActivity.accessFromDirection = 0;
                            startActivity(i);
                        } else if (count == 1) {
                            //only 1 job, go str8 to jobInfo
                            int proceed = 0;
                            proceed = initRedirectionToJobInfoSQLite(Postal);
                            if (proceed == 1) {
                                if (categoryType == 0) {
                                    //objGlobalMethods.stopAlarmForCommon();
                                    if (FCMservice.mNotificationManager != null) {
                                        FCMservice.mNotificationManager.cancel(2);
                                    }
                                    FCMservice.notiNotRead = false;
                                }
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "true");
                                if (isDunning) {
                                    i = new Intent(this, setDunningDeliveredStatusActivity.class);
                                    JobInfoActivity.accessFromDirection = 0;
                                } else {
                                    i = new Intent(this, JobInfoActivity.class);
                                    JobInfoActivity.accessFromDirection = 1;
                                }
                                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, String.valueOf(categoryType));
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            } else if (proceed > 1) {
                                //more than 1 jobs, go to addresslist
                                i = new Intent(this, DashboardSubActivity.class);
                                JobInfoActivity.accessFromDirection = 0;
                                startActivity(i);
                            } else if (proceed == 0) {
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
            objGlobalMethods.writeToFile("Exception caught at DashboardSubActivity, startRedirection." + "\n" + e.getMessage().toString(),
                    "", "", username, useridx);
            JobsHandler.sendErrorEmail("Exception caught at DashboardSubActivity for " + username + ", startRedirection." + "\n" + e.getMessage().toString());
        }

    }

    private int initRedirectionToJobInfoSQLite(String Postal) {
        int asd = 0;
        Gson gson = new Gson();
        String JsonStr;
        objJobInfo JobInfo;
        String addressWithUnit = "";
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        Boolean hasCOD = false;
        try {
            List<dbObjJobs> objAL = DAO_Jobs.getAlldbObjJobsForDashboardSub(Postal);
            if (objAL != null && objAL.size() == 0) {
                asd = 0;
            } else if (objAL != null && objAL.size() > 1) {
                asd = 2;
            } else if (objAL != null && objAL.size() == 1) {
                JsonStr = objAL.get(0).getJobInfoJsonStr();
                categoryType = objAL.get(0).getCatTypeID();
                objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, JsonStr);
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                if (JsonStr != "") {
                    JobInfo = gson.fromJson(JsonStr, objJobInfo.class);
                    if (JobInfo != null) {
                        hasCOD = JobsHandler.checkForCODwithCatType(JobInfo, categoryType);
                        if (hasCOD) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", "true");
                        } else {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", "false");
                        }

                        isDunning = JobsHandler.isDunning(JobInfo);
                        if (isDunning) {
                            objGlobalMethods.writeToFile(objAL.get(0).getJobNo() + " is Dunning.", "", "", "", "");
                            if (JobInfo != null) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", JobsHandler.CannotTakePhoto(JobInfo, 2) ? "1" : "0");
                            } else {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
                            }

                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX, String.valueOf(JobInfo.JobsIDX));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "currJobInfoCN", JobInfo.JobNo);
                            addressWithUnit = objAL.get(0).getAddressWithoutPostal();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "addressWithUnit", addressWithUnit);
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

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(AddressListRoutingActivity.this, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    public void BackToLoginPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", "");
        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        Intent parentIntent;
        parentIntent = new Intent(AddressListRoutingActivity.this, MainActivity.class);
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
        //objGlobalMethods.stopAlarmForCommon();
        FCMservice.notiNotRead = false;
        JobsHandler.clearGPSQue(LocationPrefs);
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
        DAO_Jobs.dropLukeDB();
        startActivity(parentIntent);
        finish();
    }

    private void playTone() {
        player = MediaPlayer.create(AddressListRoutingActivity.this, R.raw.error);
        player.start();
    }

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
                SharedPreferences gcmp = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                objGlobalMethods.setGCMPrefs(gcmp, "showBattChargeNotiPrefStr", asd.toString());
                dialog.dismiss();
            }
        });

        return builder.show();
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
            //objGlobalMethods.writeToFile("Exception at AddressListNew_Activity handleIntent. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception at AddressListNew_Activity handleIntent. Exception: " + e.getMessage().toString(), "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    public void promptReqMPD() {
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
                    objGlobalMethods.writeToFile("Bluetooth is on at AddressListRoutingActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListRoutingActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
