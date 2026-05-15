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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xdel.luke.BCnSVC.SendQueuedEmail_Receiver;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.Voip.VoipApp;
import com.xdel.luke.Vrp.JobQueue;
import com.xdel.luke.mpd.mpdclass;
import com.xdel.luke.objClasses.AddressListNewSQLite_PostData;
import com.xdel.luke.objClasses.AddressListNewSQLite_PostItemAdapter;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objAddressListJobInfo;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjAddressList;
import com.xdel.luke.sqlite.dbObjJobs;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;
import java.util.List;

public class AddressListNewActivity extends AppCompatActivity {
    private MediaPlayer player;
    String cameraScannedCode;
    ConnectionDetector cd;
    Context context;
    private ListView listView;
    private TextView list_empty;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    public EditText AddresslistInput;
    private Button btnlistall, btnlistpriority;
    private static ProgressDialog pdSearch;
    String langType = "en";
    String username, password, useridx, ishomesuser;
    String[] UsernamePwd = new String[4];
    private static Boolean isDunning = false;
    private LinearLayout AddressListUnreadMsgLinearLayout;
    private LinearLayout AddressListListviewLinearLayout;
    private LinearLayout LLDeliveryTabs;
    private int catType;
    public NfcAdapter mNfcAdapter;
    //public static final String MIME_TEXT_PLAIN = "text/plain";
    Boolean isFromLocationNFCscan = false;
    ScreenShotContentObserver screenShotContentObserver;
    private AddressListNewSQLite_PostItemAdapter adapter;
    TextView lblIsDefaultVRPpage;
    public static boolean tesboo = false;

    private void showChangeAirplaneModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddressListNewActivity.this);
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at AddressListNewActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
            AlertDialog.Builder builder = new AlertDialog.Builder(AddressListNewActivity.this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmode));

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        objGlobalMethods.writeToFile("ActivityNotFoundException at AddressListNewActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                    }

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity showChangeGPSModeSettingsDialog. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

    }

    private void showChangeGPStoHighAccModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddressListNewActivity.this);
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at AddressListNewActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at AddressListNewActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void rtnToDashBoard() {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName};
            String[] prefvalue = new String[]{"-1", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
            JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
            Intent i;
            //i = new Intent(AddressListNewActivity.this, DrawerMainActivity.class);
            i = new Intent(myApp.currentActivity != null ? myApp.currentActivity : this, DrawerMainActivity.class);
            startActivity(i);
            finish();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity rtnToDashBoard. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private void showErrorAlert() {
        MainActivity.uponLogin = 0;
        DrawerMainActivity.DashboardOpenFromPush = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity != null ? myApp.currentActivity : this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName};
                String[] prefvalue = new String[]{"-1", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
                Intent i;
                //i = new Intent(AddressListNewActivity.this, DrawerMainActivity.class);
                i = new Intent(myApp.currentActivity, DrawerMainActivity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
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

    public void clearAddressListStaticValues() {
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        String[] prefName = new String[]{"PIPCompanyNameAL", "PUCompanyNameAL", "DIPCompanyNameAL", "DELCompanyNameAL", "FPCompanyNameAL", "FDCompanyNameAL",
                "PIPPostalJobsAL", "PUPostalJobsAL", "DIPPostalJobsAL", "DELPostalJobsAL", "FPPostalJobsAL", "FDPostalJobsAL",
                "PIPPostalJobsCountAL", "PUPostalJobsCountAL", "DIPPostalJobsCountAL", "DELPostalJobsCountAL", "FPPostalJobsCountAL", "FDPostalJobsCountAL"};
        String[] prefvalue = new String[]{"", "", "", "", "", "",
                "", "", "", "", "", "",
                "", "", "", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);


        //objGlobalMethods.setGCMPrefs(gcmp, "PIPCompanyNameAL", "");
        //objGlobalMethods.setGCMPrefs(gcmp,"PUCompanyNameAL","");
        //objGlobalMethods.setGCMPrefs(gcmp,"DIPCompanyNameAL","");
        //objGlobalMethods.setGCMPrefs(gcmp,"DELCompanyNameAL","");
        //objGlobalMethods.setGCMPrefs(gcmp, "FPCompanyNameAL", "");
        //objGlobalMethods.setGCMPrefs(gcmp,"FDCompanyNameAL","");

        //objGlobalMethods.setGCMPrefs(gcmp,"PIPPostalJobsAL","");
        //objGlobalMethods.setGCMPrefs(gcmp,"PUPostalJobsAL","");
        //objGlobalMethods.setGCMPrefs(gcmp, "DIPPostalJobsAL", "");
        //objGlobalMethods.setGCMPrefs(gcmp,"DELPostalJobsAL","");
        //objGlobalMethods.setGCMPrefs(gcmp, "FPPostalJobsAL", "");
        //objGlobalMethods.setGCMPrefs(gcmp, "FDPostalJobsAL", "");

        //objGlobalMethods.setGCMPrefs(gcmp,"PIPPostalJobsCountAL","");
        //objGlobalMethods.setGCMPrefs(gcmp,"PUPostalJobsCountAL","");
        //objGlobalMethods.setGCMPrefs(gcmp, "DIPPostalJobsCountAL", "");
        //objGlobalMethods.setGCMPrefs(gcmp, "DELPostalJobsCountAL", "");
        //objGlobalMethods.setGCMPrefs(gcmp, "FPPostalJobsCountAL", "");
        //objGlobalMethods.setGCMPrefs(gcmp, "FDPostalJobsCountAL", "");
    }

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

        setContentView(R.layout.activity_address_list);

        listView = (ListView) findViewById(R.id.listView);
        list_empty = findViewById(R.id.list_empty);
        AddresslistInput = (EditText) findViewById(R.id.AddresslistInput);
        btnlistall = findViewById(R.id.btnlistall);
        btnlistpriority = findViewById(R.id.btnlistpriority);

        pdSearch = new ProgressDialog(this);
        pdSearch.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdSearch.setCanceledOnTouchOutside(false);
        pdSearch.setMessage(myApp.getAppContext().getString(R.string.pd_waitSearch));
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            isDunning = false;
            CODupdate.al = null;
            CODupdate.total = 0.0;

            Gson gson = new Gson();
            context = myApp.getAppContext();

            setTitle(GcmPreferences.getString(objGlobalVariables.categoryTitlePrefName, "") + " " + myApp.getAppContext().getString(R.string.addresslist));
            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);

            /*objCallStatusHashmapList CallStatusHashmapList = objGlobalMethods.getCallStatusHashmapList(GcmPreferences);
            HashMap<String, objCallStatus> calllist = CallStatusHashmapList.getCalllist();*/
            /*String regid_ = objGlobalMethods.getRegistrationIdOnly();
            objGlobalMethods.storeRegistrationId(context,regid_,GcmPreferences);*/

            if (VoipApp.observer != null) {
                try {
                    if (JobInfoActivity.app != null) {
                        JobInfoActivity.app.deinit();
                    } else {
                        VoipApp.observer = null;
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at AddressListNewActivity onResume JobInfoActivity.app.deinit(). Exception: " + e.getMessage(), "", "", "", "");
                }
            }

            if (JobsHandler.hasOpsQ()) {
                JobsHandler.redirectToVrpPage();
            } else {
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
                    objGlobalMethods.writeToFile("AddressListNewActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

                try {
                    if (mNfcAdapter != null && myApp.isNfcEnabled(mNfcAdapter, context)) {
                        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
                        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
                        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};
                        /*PendingIntent nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                        mNfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilter, null);*/

                        PendingIntent nfcPendingIntent = PendingIntent.getActivity(myApp.getAppContext(), 0, new Intent(myApp.currentActivity, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                        mNfcAdapter.enableForegroundDispatch(myApp.currentActivity, nfcPendingIntent, nfcIntentFilter, null);
                    }
                } catch (Exception e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception caught at AddressListNewActivity onResume mNfcAdapter portion. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
                }

                String[] prefName = new String[]{"showMPD_Dialog_IsShowing", "showRegBTTL_Dialog_IsShowing", "FailAsND", "FailAsND_Reason"};
                String[] prefvalue = new String[]{"", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                objGlobalMethods.resetAllImageRelatedStaticVariables(false);
                objGlobalMethods.resetJobsListGroupScrollIndexNpostion(GcmPreferences);
                objGlobalMethods.offSpeaker();
                JobsHandler.clearAllStaticFromPageAndNextActivityFields();
                objGlobalMethods.clearOnResumeAddressListNewActivityGcmPrefs(GcmPreferences);
                JobsHandler.clearCheckListActivityStatic(GcmPreferences, true);
                JobsHandler.clearDunningActivityPrefs(GcmPreferences);
                JobsHandler.clearCallActivityPrefs(GcmPreferences);
                JobsHandler.clearlAllDetailPagesStatic();
                JobsHandler.clearJobsRetrievalActivityPrefs(GcmPreferences);
                JobsHandler.clearJobsListGroupPrefs(GcmPreferences);
                JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "", "", true);
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

                objGlobalMethods.checkGPSnPerMinRunAlarmRunning(ishomesuser);

                String RegID = objGlobalMethods.getRegistrationIdOnly();

                // Check if Internet present
                if (!cd.isConnectingToInternet()) {
                    //// Internet Connection is not present
                    Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }

                catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                List<dbObjAddressList> dbObjAddressListAL = DAO_Jobs.getAlldbObjJobsPerCategoryForAddressList(catType);
                if (dbObjAddressListAL != null && dbObjAddressListAL.size() > 0) {
                    loadListSQLite(dbObjAddressListAL, catType);
                } else {
                    //showErrorAlert();
                    rtnToDashBoard();
                }

                /*if (tesboo) {
                    tesboo = false;
                    rtnToDashBoard();
                } else if (dbObjAddressListAL != null && dbObjAddressListAL.size() > 0) {
                    loadListSQLite(dbObjAddressListAL, catType);
                } else {
                    //showErrorAlert();
                    tesboo = false;
                    rtnToDashBoard();
                }*/

                //JobsHandler.doubleCheckUpdatePIPSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx, false);
                JobsHandler.doubleCheckUpdatePIPSQLiteNew(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);

                AddressListUnreadMsgLinearLayout = (LinearLayout) findViewById(R.id.AddressListUnreadMsgLinearLayout);
                AddressListListviewLinearLayout = (LinearLayout) findViewById(R.id.AddressListListviewLinearLayout);
                LLDeliveryTabs = (LinearLayout) findViewById(R.id.LLDeliveryTabs);
                LLDeliveryTabs.setVisibility(catType == 2 ? View.VISIBLE : View.GONE);
                objGlobalMethods.getMsgReminder(GcmPreferences, "AddressListNewActivity", AddressListUnreadMsgLinearLayout, AddressListListviewLinearLayout);

                AddresslistInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        int SearchedcatType = -1;
                        SearchedcatType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                        cameraScannedCode = null;
                        if (!AddresslistInput.getText().toString().equals("")) {
                            if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                                return false;
                            if (actionId == 0) {
                                // input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                                if (SearchedcatType >= 0) {
                                    search(AddresslistInput.getText().toString());

                                    if (getCurrentFocus() != null) {
                                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                    }
                                } else if (SearchedcatType == -1) {
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                                    playTone();
                                    //showDialog("Error locating job.");
                                    showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
                                }
                            } else if (actionId == EditorInfo.IME_ACTION_GO) {
                                if (SearchedcatType >= 0) {
                                    search(AddresslistInput.getText().toString());

                                    if (getCurrentFocus() != null) {
                                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                    }
                                } else if (SearchedcatType == -1) {
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                                    playTone();
                                    //showDialog("Error locating job.");
                                    showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
                                }
                            }

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    listView.clearFocus();
                                    AddresslistInput.clearFocus();
                                    AddresslistInput.setText("");
                                    AddresslistInput.requestFocus();
                                }
                            }, 100);
                        }
                        return true;
                    }
                });

                cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
                if (!cameraScannedCode.equals("")) {
                    String searchedval = "";
                    int SearchedcatType = -1;
                    SearchedcatType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                    if (SearchedcatType >= 0) {
                        searchedval = cameraScannedCode;
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        search(searchedval);
                    } else if (SearchedcatType == -1) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        playTone();
                        //showDialog("Error locating job.");
                        showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
                    }
                }

                listView.clearFocus();
                AddresslistInput.clearFocus();
                AddresslistInput.setText("");
                AddresslistInput.requestFocus();
                objGlobalMethods.checkToPromptNotiForBattCharging();
                int qe = JobsHandler.numOfQueuedEmails();
                if (qe > 0) {
                    objGlobalMethods.writeToFile("SendQueuedEmailReceiver called at AddressListNewActivity.", "", "", "", "");
                    Intent SendQueuedEmailReceiver = new Intent(context, SendQueuedEmail_Receiver.class);
                    sendBroadcast(SendQueuedEmailReceiver);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, "cameraScannedCode"};
            String[] prefvalue = new String[]{"-1", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
            //Intent parentIntent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            Intent parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity onBackPressed. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            //Intent parentIntent = new Intent(this, DrawerMainActivity.class);
            Intent parentIntent = new Intent(myApp.currentActivity, DrawerMainActivity.class);
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        }
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
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        /*try {
            boolean isInBackground = objGlobalMethods.isApplicationBroughtToBackground();
            if (isInBackground) {
                JobsHandler.sendErrorEmail("MainActivity is in BACKGROUND");
            } else {
                JobsHandler.sendErrorEmail("MainActivity is in FOREGROUND");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*try {
            boolean isInBackground = objGlobalMethods.isApplicationBroughtToBackground();
            if (isInBackground) {
                JobsHandler.sendErrorEmail("MainActivity is in BACKGROUND");
            } else {
                JobsHandler.sendErrorEmail("MainActivity is in FOREGROUND");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }*/
    }

    @Override
    public void onDestroy() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        try {
            if (pdSearch != null && pdSearch.isShowing()) {
                pdSearch.dismiss();
            }
            pdSearch = null;
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, "cameraScannedCode"};
            String[] prefvalue = new String[]{"-1", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
            GcmPreferences = null;
            UpdateJobInfoPreferences = null;
            isDunning = null;
            mNfcAdapter = null;
            adapter = null;
            //objGlobalMethods.clearStaticActivities();
            DAO_Jobs.nullifystaticitems();

            if (AddresslistInput != null) {
                AddresslistInput.setOnEditorActionListener(null);
                AddresslistInput = null;
            }
            if (AddressListUnreadMsgLinearLayout != null) {
                AddressListUnreadMsgLinearLayout.setOnClickListener(null);
                AddressListUnreadMsgLinearLayout = null;
            }

            AddressListListviewLinearLayout = null;

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity onDestroy. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_address_list_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent parentIntent;

        if (item.getItemId() == R.id.qrscan)
            launchQRScan();

        /*switch (item.getItemId()) {
            case R.id.qrscan:
                launchQRScan();
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void AddressListUnreadMsgtextClick(View view) {
        //Intent parentIntent = new Intent(this, Messaginglist.class);
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
        String[] prefName = new String[]{"QRScanRequestIntent", "cameraScannedCode"};
        String[] prefvalue = new String[]{"addresslist", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        //objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "addresslist");
        //objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        //parentIntent = new Intent(AddressListNewActivity.this, QRBarcodeReader.class);
        parentIntent = new Intent(myApp.currentActivity, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        //parentIntent = new Intent(AddressListNewActivity.this, MainActivity.class);
        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    public void BackToLoginPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, "wifistate", "leftofc"};
        String[] prefvalue = new String[]{"-1", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        //objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        //objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        clearAddressListStaticValues();
        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        Intent parentIntent;
        //parentIntent = new Intent(AddressListNewActivity.this, MainActivity.class);
        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
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

    private void stopPlayingTone() {
        try {
            if (player != null) {
                player.reset();
                //player.prepare();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void btnlistall_click(View v) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "ListPriorityOnly", "");
            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            List<dbObjAddressList> dbObjAddressListAL = DAO_Jobs.getAlldbObjJobsPerCategoryForAddressList(catType);
            if (dbObjAddressListAL != null && dbObjAddressListAL.size() > 0) {
                loadListSQLite(dbObjAddressListAL, catType);
            } else {
                showErrorAlert();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity btnlistall_click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnlistpriority_click(View v) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "ListPriorityOnly", "t");
            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            List<dbObjAddressList> dbObjAddressListAL = DAO_Jobs.getAlldbObjJobsPerCategoryForAddressList(catType);
            if (dbObjAddressListAL != null && dbObjAddressListAL.size() > 0) {
                loadListSQLite(dbObjAddressListAL, catType);
            } else {
                showErrorAlert();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity btnlistpriority_click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void togglebutton(int selectedBtnIDX) {
        try {
            if (selectedBtnIDX == 0) {
                ctrlSettings.tottleButtonsWithStyles(true, btnlistall);
                ctrlSettings.tottleButtonsWithStyles(false, btnlistpriority);
            } else {
                ctrlSettings.tottleButtonsWithStyles(true, btnlistpriority);
                ctrlSettings.tottleButtonsWithStyles(false, btnlistall);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity togglebutton. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    //=========================================sqlite=================================================


    private void loadListSQLite(List<dbObjAddressList> dbObjAddressListAL, int CatTypeID) {
        //new codes for sorting, delete if not working
        List<objAddressListJobInfo> AddressListJobInfoAL = new ArrayList<objAddressListJobInfo>();
        objAddressListJobInfo AddressListJobInfo;
        //new codes for sorting, delete if not working

        GcmPreferences = objGlobalMethods.getGcmPreferences();
        ArrayList<AddressListNewSQLite_PostData> postdataAL = new ArrayList<>();
        AddressListNewSQLite_PostData postdata;
        Boolean isDelivery = CatTypeID == 2 ? true : false;
        int prioritycount = 0;
        String ListPriorityOnly = objGlobalMethods.getGCMPrefs(GcmPreferences, "ListPriorityOnly");
        boolean showPriority = !objGlobalMethods.IsEmptyString(ListPriorityOnly) && isDelivery ? true : false;
        boolean add = false;

        try {
            dbObjAddressList obj = null;
            /*if (showPriority){
                for (int i = 0; i <= dbObjAddressListAL.size() - 1; i++) {
                    obj = dbObjAddressListAL.get(i);
                    if (obj.PriorityCount > 0) {
                        prioritycount++;
                    }
                }
            }*/

            for (int i = 0; i <= dbObjAddressListAL.size() - 1; i++) {
                obj = dbObjAddressListAL.get(i);
                //add = (showPriority && ((obj.PriorityCount > 0) || (obj.IsLate))) || (!showPriority);
                add = (showPriority && ((obj.PriorityCount > 0) || (obj.IsLate))) || (!showPriority);
                if (add) {
                    postdata = new AddressListNewSQLite_PostData(obj.Address, obj.Postal, String.valueOf(obj.JOBCOUNT), obj.LAT, obj.LNG, obj.SignaureFlag,
                            obj.DNFFlag, obj.OTPFlag, obj.TimingText, obj.IsLate, obj.HasNewlyInserted, obj.HasDOCOD, obj.HasTradeIn, isDelivery, obj.HasP2,
                            obj.isIntendedRecOnlyCount, obj.isTotallyExplicitCount, obj.canSlotLBCount, obj.canSlotDoorCount, obj.canGuardCount,
                            obj.isPickAndSendCount, obj.JobChangesCount, obj.canDoorstepCount, obj.contactlessCount);
                    postdata.setReqNetsCount(obj.ReqNetsCount);
                    postdata.setReqVisaCount(obj.ReqVisaCount);
                    postdata.setReqTiCount(obj.ReqTiCount);
                    postdata.setHASLUNCHTIME(obj.HASLUNCHTIME);
                    postdata.setColdCount(obj.ColdCount);
                    postdataAL.add(postdata);
                }
            }

            if (showPriority && postdataAL.size() == 0) {
                for (int i = 0; i <= dbObjAddressListAL.size() - 1; i++) {
                    obj = dbObjAddressListAL.get(i);
                    postdata = new AddressListNewSQLite_PostData(obj.Address, obj.Postal, String.valueOf(obj.JOBCOUNT), obj.LAT, obj.LNG, obj.SignaureFlag,
                            obj.DNFFlag, obj.OTPFlag, obj.TimingText, obj.IsLate, obj.HasNewlyInserted, obj.HasDOCOD, obj.HasTradeIn, isDelivery, obj.HasP2,
                            obj.isIntendedRecOnlyCount, obj.isTotallyExplicitCount, obj.canSlotLBCount, obj.canSlotDoorCount, obj.canGuardCount,
                            obj.isPickAndSendCount, obj.JobChangesCount, obj.canDoorstepCount, obj.contactlessCount);
                    postdata.setReqNetsCount(obj.ReqNetsCount);
                    postdata.setReqVisaCount(obj.ReqVisaCount);
                    postdata.setReqTiCount(obj.ReqTiCount);
                    postdata.setHASLUNCHTIME(obj.HASLUNCHTIME);
                    postdata.setColdCount(obj.ColdCount);
                    postdataAL.add(postdata);
                }
                showPriority = false;
            }

            adapter = new AddressListNewSQLite_PostItemAdapter(myApp.getAppContext(), postdataAL);

            listView.setAdapter(adapter);
            listView.setEmptyView(list_empty);
            togglebutton(showPriority ? 1 : 0);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    View v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                    if (v == null) {
                        return;
                    } else {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        // save index and top position
                        int index = listView.getFirstVisiblePosition();
                        View vTop = listView.getChildAt(0);
                        int top = (vTop == null) ? 0 : (vTop.getTop() - listView.getPaddingTop());
                        // save index and top position

                        TextView lblCount = (TextView) v.findViewById(R.id.lblCount);
                        TextView hfPostal = (TextView) v.findViewById(R.id.hfPostal);
                        TextView lblAddressListRowNewAddress = (TextView) v.findViewById(R.id.lblAddressListRowNewAddress);
                        TextView lblLAT = (TextView) v.findViewById(R.id.lblLAT);
                        TextView lblLNG = (TextView) v.findViewById(R.id.lblLNG);
                        String currPostal = hfPostal.getText().toString();
                        String currAddress = lblAddressListRowNewAddress.getText().toString();
                        String[] prefName = new String[]{"desPostal", "LAT", "LNG", "desAddress", "AddressListNewScrollSavedIndex", "AddressListNewScrollSavedPosition"};
                        String[] prefvalue = new String[]{currPostal, lblLAT.getText().toString(), lblLNG.getText().toString(), currAddress, String.valueOf(index), String.valueOf(top)};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                        startRedirectionSQLite(lblCount, currPostal);
                    }
                }
            });

            if (catType == 0) {
                if (FCMservice.mNotificationManager != null) {
                    FCMservice.mNotificationManager.cancel(2);
                }
                FCMservice.notiNotRead = false;
            }

            // restore index and position
            int index = 0;
            int pos = 0;

            try {
                index = Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "AddressListNewScrollSavedIndex"));
            } catch (Exception e) {
                index = 0;
            }

            try {
                pos = Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "AddressListNewScrollSavedPosition"));
            } catch (Exception e) {
                pos = 0;
            }

            listView.setSelectionFromTop(index, pos);
            // restore index and position
            promptReqMPD();
            objGlobalMethods.showNewPIPdialog(GcmPreferences);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at AddressListNewActivity loadListSQLite. Exception: " + e.toString(), "", "", "", "");
            showErrorAlert();
        }
    }

    private void startRedirectionSQLite(TextView lblCount, String Postal) {
        Intent i = new Intent(myApp.currentActivity, JobInfoActivity.class);
        int count;

        if (!lblCount.getText().toString().isEmpty() && !lblCount.getText().toString().toLowerCase().equals("error")) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            count = Integer.parseInt(lblCount.getText().toString());

            if (count > 0 && count > 1) {
                //more than 1 jobs
                i = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
                startActivity(i);
            } else if (count > 0 && count == 1) {
                //only 1 job, go str8 to jobInfo
                initRedirectionToJobInfoSQLite(Postal);
            } else if (count == 0) {
                //assume to be 0 jobs, do nothing
            }
        } else {
            //assume to be 0 jobs, do nothing
            //or
            //show error instead of count, do nothing
        }
    }

    private void initRedirectionToJobInfoSQLite(String Postal) {
        Gson gson = new Gson();
        objJobInfo JobInfo;
        String postal = "";
        String address = "";
        String addressWithUnit = "";
        double LAT = 0;
        double LNG = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();

        try {
            int catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);

            String hfJobInfoJsonStr = DAO_Jobs.gethfJobInfoJsonStr(Postal, catType);
            if (hfJobInfoJsonStr != "" && hfJobInfoJsonStr != "[]") {
                JobInfo = gson.fromJson(hfJobInfoJsonStr, objJobInfo.class);
                //objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, hfJobInfoJsonStr);

                if (catType == 0 || catType == 1 || catType == 4) {
                    if (JobInfo.PURedirectedLocation != null) {
                        postal = JobInfo.PURedirectedLocation.getPOSTALCODE();
                        LAT = JobInfo.PURedirectedLocation.LAT;
                        LNG = JobInfo.PURedirectedLocation.LNG;
                        //address = objGlobalMethods.returnAddressWithoutPostalNUnit(JobInfo.PURedirectedLocation);
                        address = objGlobalMethods.returnAddressWithPostalNoUnit(JobInfo.PURedirectedLocation);
                    } else if (JobInfo.PULocation != null) {
                        postal = JobInfo.PULocation.getPOSTALCODE();
                        LAT = JobInfo.PULocation.LAT;
                        LNG = JobInfo.PULocation.LNG;
                        //address = objGlobalMethods.returnAddressWithoutPostalNUnit(JobInfo.PULocation);
                        address = objGlobalMethods.returnAddressWithPostalNoUnit(JobInfo.PULocation);
                    }

                    String[] prefName = new String[]{"LAT", "LNG", "desPostal", "desAddress", "addressWithUnit", "hfJobInfoJsonStr"};
                    String[] prefvalue = new String[]{String.valueOf(LAT), String.valueOf(LNG), postal, address, addressWithUnit, hfJobInfoJsonStr};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                } else if (catType == 2 || catType == 3 || catType == 5 || catType == 6 || catType == 7) {
                    if (JobInfo.DLRedirectedLocation != null) {
                        postal = JobInfo.DLRedirectedLocation.getPOSTALCODE();
                        LAT = JobInfo.DLRedirectedLocation.LAT;
                        LNG = JobInfo.DLRedirectedLocation.LNG;
                        //address = objGlobalMethods.returnAddressWithoutPostalNUnit(JobInfo.DLRedirectedLocation);
                        address = objGlobalMethods.returnAddressWithPostalNoUnit(JobInfo.DLRedirectedLocation);
                        //only for dunning
                        addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(JobInfo.DLRedirectedLocation);
                    } else if (JobInfo.DLLocation != null) {
                        postal = JobInfo.DLLocation.getPOSTALCODE();
                        LAT = JobInfo.DLLocation.LAT;
                        LNG = JobInfo.DLLocation.LNG;
                        //address = objGlobalMethods.returnAddressWithoutPostalNUnit(JobInfo.DLLocation);
                        address = objGlobalMethods.returnAddressWithPostalNoUnit(JobInfo.DLLocation);
                        //only for dunning
                        addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(JobInfo.DLLocation);
                    }

                    if (catType == 2) {
                        isDunning = JobsHandler.isDunning(JobInfo);
                        if (isDunning) {
                            objGlobalMethods.writeToFile(JobInfo.JobNo + " is Dunning.", "", "", "", "");
                            /*if (JobInfo != null) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", JobsHandler.CannotTakePhoto(JobInfo, 2) ? "1" : "0");
                            } else {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
                            }

                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX, String.valueOf(JobInfo.JobsIDX));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "currJobInfoCN", JobInfo.JobNo);*/

                            String[] prefNameD = new String[]{objGlobalVariables.currJobInfoIDX, "currJobInfoCN", "CannotTakePhoto"};
                            String[] prefvalueD = new String[]{String.valueOf(JobInfo.JobsIDX), JobInfo.JobNo, ((JobInfo != null && JobsHandler.CannotTakePhoto(JobInfo, 2)) ? "1" : "")};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefNameD, prefvalueD);
                        }
                    }

                    String[] prefName = new String[]{"LAT", "LNG", "desPostal", "desAddress", "addressWithUnit", "hfJobInfoJsonStr"};
                    String[] prefvalue = new String[]{String.valueOf(LAT), String.valueOf(LNG), postal, address, addressWithUnit, hfJobInfoJsonStr};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                }

                Intent intent;
                if (catType == 3 || catType == 4 || catType == 5 || catType == 6 || catType == 7) {
                    intent = new Intent(myApp.currentActivity, JobInfoActivity.class);
                    JobInfoActivity.accessFromDirection = 1;
                } else {
                    if (isDunning) {
                        intent = new Intent(myApp.currentActivity, setDunningDeliveredStatusActivity.class);
                        JobInfoActivity.accessFromDirection = 0;
                    } else {
                        intent = new Intent(myApp.currentActivity, JobInfoActivity.class);
                        JobInfoActivity.accessFromDirection = 1;
                    }
                }
                startActivity(intent);
            } else {
                showErrorAlert();
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at AddressListNewActivity initRedirectionToJobInfoSQLite. Exception: " + e.getMessage().toString(), "", "", "", "");
            JobsHandler.sendExceptionEmail("Exception caught at AddressListNewActivity initRedirectionToJobInfoSQLite. Exception: " + e.getMessage().toString() + "\n\nStack: " + stack);
            showErrorAlert();
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
            CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            rs = DAO_Jobs.searchJobs(CatTypeID, "", searchValue);
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
                        //pdSearch.hide();
                        startActivity(intent);
                    } else {
                        //pdSearch.hide();
                        playTone();
                        showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
                    }
                } else {
                    //pdSearch.hide();
                    playTone();
                    showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
                }
            } else {
                //pdSearch.hide();
                playTone();
                showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity search. Exception: " + e.getMessage().toString(), "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
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
                    objGlobalMethods.writeToFile("Bluetooth is on at AddressListNewActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListNewActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
