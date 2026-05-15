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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.BCnSVC.SendQueuedEmail_Receiver;
import com.xdel.luke.DOCOD.CODupdate2;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.ShpmtVerification.ShpmtV2Activity;
import com.xdel.luke.Voip.VoipApp;
import com.xdel.luke.mpd.mpdclass;
import com.xdel.luke.objClasses.JobListGroupSortedSQLiteNew_PostItemAdapter;
import com.xdel.luke.objClasses.JobListGroupSortedSQLite_PostData;
import com.xdel.luke.objClasses.JobListGroupSortedSQLite_PostItemAdapter;
import com.xdel.luke.objClasses.checklistObjForMassOKP;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.jobSelectionInfo;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCheckedItems;
import com.xdel.luke.objClasses.objDOObject;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objListItemPosChecked;
import com.xdel.luke.objClasses.objMultiSelectInitialCriteria;
import com.xdel.luke.objClasses.objUpdateCOD_PostData;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjJobs;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JobsListGroupActivity extends AppCompatActivity {

    private String perJobInfoJsonStr;
    ConnectionDetector cd;
    Context context;
    //private String regid;
    private ExpandableListView expListView;
    private EditText txtInput;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    //private JobListGroupSortedSQLite_PostItemAdapter expandableSortedSQLiteListAdapter;
    private JobListGroupSortedSQLiteNew_PostItemAdapter expandableSortedSQLiteListAdapter;
    private int catType;
    private MediaPlayer player;
    private ProgressDialog pd;
    private List<String> listDataHeader;
    private HashMap<String, List<JobListGroupSortedSQLite_PostData>> listDataChildSortedSQLite2;
    private ArrayList<String> headerAl;
    private ArrayList itemAL;
    private List<JobListGroupSortedSQLite_PostData> subGroupItemSqliteAL;
    private Button btnFail, btnPU, btnOKP, btnDIP, btnOKPdummy;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    static String cameraScannedCode;
    private ProgressDialog pdProcessing;

    private LinearLayout JobsListGroupUnreadMsgLinearLayout;
    private LinearLayout JobsListGroupListviewLinearLayout;
    private List<objJobInfo> jiList;

    String langType = "en";
    private static ProgressDialog progressDoalog;
    TextView lblIsDefaultVRPpage;

    public NfcAdapter mNfcAdapter;
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);
    private Uri errToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error);
    private Uri errTone2URI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.dingdong);
    public static final String MIME_TEXT_PLAIN = "text/plain";

    ScreenShotContentObserver screenShotContentObserver;

    private static Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(1);
        }
    };

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

        setContentView(R.layout.activity_jobs_list_group);

        expListView = (ExpandableListView) findViewById(R.id.expListView);
        txtInput = (EditText) findViewById(R.id.txtInput);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));

        pdProcessing = new ProgressDialog(this);
        pdProcessing.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdProcessing.setCanceledOnTouchOutside(false);
        pdProcessing.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

        btnFail = (Button) findViewById(R.id.btnFail);
        btnPU = (Button) findViewById(R.id.btnPU);
        btnOKP = (Button) findViewById(R.id.btnOKP);
        btnDIP = (Button) findViewById(R.id.btnDIP);
        btnOKPdummy = (Button) findViewById(R.id.btnOKPdummy);
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
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            CODupdate.al = null;
            CODupdate.total = 0.0;

            context = myApp.getAppContext();

            setTitle(GcmPreferences.getString(objGlobalVariables.categoryTitlePrefName, "") + " " + myApp.getAppContext().getString(R.string.JOBlist));
            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);
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
                objGlobalMethods.writeToFile("JobsListGroupActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

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
                //objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity onResume mNfcAdapter portion. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
            }

            String[] prefName = new String[]{"showMPD_Dialog_IsShowing", "showRegBTTL_Dialog_IsShowing", "FailAsND", "FailAsND_Reason"};
            String[] prefvalue = new String[]{"", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            objGlobalMethods.offSpeaker();
            JobsHandler.clearAllStaticFromPageAndNextActivityFields();
            objGlobalMethods.resetAllImageRelatedStaticVariables(false);
            JobsHandler.resetDOinvCheckList(GcmPreferences);
            objGlobalMethods.clearOnResumeJobsListGroupActivityGcmPrefs(GcmPreferences);
            objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
            objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
            JobsHandler.clearCheckListActivityStatic(GcmPreferences, true);
            JobsHandler.clearDunningActivityPrefs(GcmPreferences);
            JobsHandler.clearCallActivityPrefs(GcmPreferences);
            JobsHandler.clearlAllDetailPagesStatic();
            JobsHandler.clearJobsRetrievalActivityPrefs(GcmPreferences);
            JobsHandler.checkForceLogOut();

            lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

            if (VoipApp.observer != null) {
                try {
                    if (JobInfoActivity.app != null) {
                        JobInfoActivity.app.deinit();
                    } else {
                        VoipApp.observer = null;
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at JobsListGroupActivity onResume JobInfoActivity.app.deinit(). Exception: " + e.getMessage(), "", "", "", "");
                }
            }

            if (JobsHandler.hasOpsQ() && !JobsHandler.isReadingFirstQue_Location_Or_Job(myApp.currentActivity.getClass())) {
                JobsHandler.redirectToVrpPage();
            } else {
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
                    // Internet Connection is not present
                    Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                    // stop executing code by return
                }

                //new code with sqlite
                String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
                if (Postal != "") {
                    catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                    hideshowButtons(catType);
                    String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
                    /*List<dbObjJobs> dbObjJobsAL = !objGlobalMethods.IsDefaultVRPpage() ? DAO_Jobs.getAlldbObjJobsPerCategoryForJobsList(catType, Postal) :
                            !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getAlldbObjJobsPerCategoryForJobsListByJobsIDXs(catType, Postal, SelectedVRPJobsIDXs) :
                                    DAO_Jobs.getAlldbObjJobsPerCategoryForJobsList(catType, Postal);*/
                    List<dbObjJobs> dbObjJobsAL = !isVrpView() ? DAO_Jobs.getAlldbObjJobsPerCategoryForJobsList(catType, Postal) :
                            !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getAlldbObjJobsPerCategoryForJobsListByJobsIDXs(catType, Postal, SelectedVRPJobsIDXs) :
                                    DAO_Jobs.getAlldbObjJobsPerCategoryForJobsList(catType, Postal);

                    if (dbObjJobsAL != null && dbObjJobsAL.size() > 0) {
                        //loadListSQLite2(dbObjJobsAL);
                        loadListSQLiteNew(dbObjJobsAL);
                    } else {
                        showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata) + "\n" + "dbObjJobsAL is Null or empty.");
                    }
                } else {
                    showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata) + "\n" + "GcmPreferences.getString(desPostal) is empty.");
                }
                //new code with sqlite

                //JobsHandler.doubleCheckUpdatePIPSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx, false);
                JobsHandler.doubleCheckUpdatePIPSQLiteNew(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);

                JobsListGroupUnreadMsgLinearLayout = (LinearLayout) findViewById(R.id.JobsListGroupUnreadMsgLinearLayout);
                JobsListGroupListviewLinearLayout = (LinearLayout) findViewById(R.id.JobsListGroupListviewLinearLayout);
                objGlobalMethods.getMsgReminder(GcmPreferences, "JobsListGroupActivity", JobsListGroupUnreadMsgLinearLayout, JobsListGroupListviewLinearLayout);

                String searchedval = "";
                searchedval = objGlobalMethods.getGCMPrefs(GcmPreferences, "searchedval");
                catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);

                cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
                if (!cameraScannedCode.equals("")) {
                    catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                    if (catType >= 0) {
                        //selectJobSQLite2();
                        selectJobSQLiteNew();
                    } else {
                        invalidAction();
                    }
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                } else if (!objGlobalMethods.IsEmptyString(searchedval)) {
                    txtInput.setText(searchedval);
                    //selectJobSQLite2();
                    selectJobSQLiteNew();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "searchedval", "");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            expListView.clearFocus();
                            txtInput.clearFocus();
                            txtInput.setText("");
                            txtInput.requestFocus();
                        }
                    }, 100);
                } else {
                    // restore index and position
                    int index = 0;
                    int pos = 0;
                    try {
                        index = Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "JobsListGroupScrollSavedIndex"));
                    } catch (Exception e) {
                        index = 0;
                    }

                    try {
                        pos = Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "JobsListGroupScrollSavedPosition"));
                    } catch (Exception e) {
                        pos = 0;
                    }

                    if (index == 0 && pos == 0) {

                    } else {
                        final Handler handler = new Handler();
                        final int finalpos = pos;
                        final int finalindex = index;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                    /*final int skippedCount = getSkippedCount(expandableSortedSQLiteListAdapter._listDataChild.entrySet().iterator(), finalindex, expandableSortedSQLiteListAdapter._listDataHeader);
                                    expListView.setSelection(skippedCount + finalpos);*/
                                expListView.setSelection(finalpos);
                                // restore index and position
                            }
                        }, 100);
                    }
                }

                int qe = JobsHandler.numOfQueuedEmails();
                if (qe > 0) {
                    objGlobalMethods.writeToFile("SendQueuedEmailReceiver called at JobsListGroupActivity.", "", "", "", "");
                    Intent SendQueuedEmailReceiver = new Intent(context, SendQueuedEmail_Receiver.class);
                    sendBroadcast(SendQueuedEmailReceiver);
                }

                txtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                        cameraScannedCode = null;
                        //if (catType == 0 || catType == 1 || catType == 2) {
                        if (!txtInput.getText().toString().equals("")) {
                            if (catType >= 0) {
                                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                                    return false;
                                if (actionId == 0) {
                                    // input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                                    //selectJobSQLite2();
                                    selectJobSQLiteNew();
                                    if (getCurrentFocus() != null) {
                                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                    }

                                } else if (actionId == EditorInfo.IME_ACTION_GO) {
                                    //selectJobSQLite2();
                                    selectJobSQLiteNew();
                                    if (getCurrentFocus() != null) {
                                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                    }
                                }
                            } else {
                                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                                    return false;
                                invalidAction();
                            }


                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    expListView.clearFocus();
                                    txtInput.clearFocus();
                                    txtInput.setText("");
                                    txtInput.requestFocus();
                                }
                            }, 100);
                        }

                        return true;
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

        try {
            if (pd != null) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
            pd = null;

            if (pdProcessing != null) {
                if (pdProcessing.isShowing()) {
                    pdProcessing.dismiss();
                }
            }
            pdProcessing = null;

            if (progressDoalog != null) {
                if (progressDoalog.isShowing()) {
                    progressDoalog.dismiss();
                }
            }
            progressDoalog = null;

            JobListGroupSortedSQLite_PostItemAdapter.progressDoalog = null;
            expandableSortedSQLiteListAdapter = null;
            mNfcAdapter = null;
            //objGlobalMethods.clearStaticActivities();

            DAO_Jobs.nullifystaticitems();

            if (JobsListGroupUnreadMsgLinearLayout != null) {
                JobsListGroupUnreadMsgLinearLayout.setOnClickListener(null);
                JobsListGroupUnreadMsgLinearLayout = null;
            }

            JobsListGroupListviewLinearLayout = null;

            if (txtInput != null) {
                txtInput.setOnEditorActionListener(null);
                txtInput = null;
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity onDestroy. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        super.onDestroy();
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
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void JobsListGroupUnreadMsgtextClick(View view) {
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
        String[] prefvalue = new String[]{"jobslist", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        //objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "jobslist");
        //objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");

        //parentIntent = new Intent(JobsListGroupActivity.this, QRBarcodeReader.class);
        parentIntent = new Intent(myApp.currentActivity, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    //Start showDialog
    private Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);
        builder.setCancelable(false);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                dialog.dismiss();

            }
        });

        return builder.show();
    }

    private void showReqCallLogImgAlert(final int cattype, final int count, final objCheckedItems[] objCheckedItemsAL, final int type) {
        //type 1: FAIL 2: OKP
        Context ctx = myApp.getAppContext();
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("");
            builder.setCancelable(false);
            builder.setMessage(cattype == 0 ? ctx.getString(R.string.takecalllogphotopip) : ctx.getString(R.string.takecalllogphotodip));

            builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    dialog.dismiss();
                    SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(gcmp, "okpShowReqCallLogImgButton", "t");
                    if (type == 1) {
                        verifyB4massFailStatus("fail", count, objCheckedItemsAL, cattype == 2 ? "fd" : "fp");
                    } else {

                    }
                }
            });

            AlertDialog alertdialog = builder.create();
            alertdialog.show();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity showReqCallLogImgAlert. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private Dialog showIhaveArriveDialog() {
        Context ctx = myApp.getAppContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(ctx.getString(R.string.buttontext_arr));
        builder.setMessage(ctx.getString(R.string.alert_ihavearriveprompt));
        builder.setIcon(R.mipmap.ic_action_i_arr);
        builder.setCancelable(false);

        builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                dialog.dismiss();

            }
        });

        return builder.show();
    }

    public static void showPromptScanBarcodeUponPU(Boolean isSecurityBag) {
        try {
            Context ctx = myApp.currentActivity;
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setCancelable(false);
            builder.setMessage(isSecurityBag ? myApp.getAppContext().getString(R.string.alert_PUpromptSecurity) : myApp.getAppContext().getString(R.string.alert_PUprompt0));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    dialog.dismiss();
                }
            });
            final AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity showPromptScanBarcodeUponPU. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at JobsListGroupActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsListGroupActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPStoHighAccModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsListGroupActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsListGroupActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Dialog showDialogNfocusTxtbox(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                txtInput.setText("");
                txtInput.requestFocus();
                dialog.dismiss();

            }
        });

        return builder.show();
    }

    private void showErrorAlert(String errMsg) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (myApp.currentActivity == null)
            myApp.currentActivity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName};
                String[] prefvalue = new String[]{"-1", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                Intent i;
                //i = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jobs_list_group, menu);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        int menucatType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        if (menucatType == 0) {
            //pip
            menu.findItem(R.id.massPU).setTitle(myApp.getAppContext().getString(R.string.action_massPU));
            menu.findItem(R.id.massFail).setTitle(myApp.getAppContext().getString(R.string.action_massFail));
            /*menu.findItem(R.id.massPU).setVisible(true);
            menu.findItem(R.id.massDIP).setVisible(false);
            menu.findItem(R.id.massOKP).setVisible(false);
            menu.findItem(R.id.massFail).setVisible(true);*/
            menu.findItem(R.id.massPU).setVisible(false);
            menu.findItem(R.id.massDIP).setVisible(false);
            menu.findItem(R.id.massOKP).setVisible(false);
            menu.findItem(R.id.massFail).setVisible(false);
        } else if (menucatType == 1) {
            //pu
            menu.findItem(R.id.massDIP).setTitle(myApp.getAppContext().getString(R.string.action_massDIP));
            /*menu.findItem(R.id.massPU).setVisible(false);
            menu.findItem(R.id.massDIP).setVisible(true);
            menu.findItem(R.id.massOKP).setVisible(false);
            menu.findItem(R.id.massFail).setVisible(false);*/
            menu.findItem(R.id.massPU).setVisible(false);
            menu.findItem(R.id.massDIP).setVisible(false);
            menu.findItem(R.id.massOKP).setVisible(false);
            menu.findItem(R.id.massFail).setVisible(false);
        } else if (menucatType == 2) {
            //dip
            menu.findItem(R.id.massOKP).setTitle(myApp.getAppContext().getString(R.string.action_massOKP));
            menu.findItem(R.id.massFail).setTitle(myApp.getAppContext().getString(R.string.action_massFailDIP));
            /*menu.findItem(R.id.massPU).setVisible(false);
            menu.findItem(R.id.massDIP).setVisible(false);
            menu.findItem(R.id.massOKP).setVisible(true);
            menu.findItem(R.id.massFail).setVisible(true);*/
            menu.findItem(R.id.massPU).setVisible(false);
            menu.findItem(R.id.massDIP).setVisible(false);
            menu.findItem(R.id.massOKP).setVisible(false);
            menu.findItem(R.id.massFail).setVisible(false);
        } else {
            //OKP
            menu.findItem(R.id.massPU).setVisible(false);
            menu.findItem(R.id.massDIP).setVisible(false);
            menu.findItem(R.id.massOKP).setVisible(false);
            menu.findItem(R.id.massFail).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent parentIntent;
        int i = 0;
        Gson gson = new Gson();
        objCheckedItems[] objCheckedItemsAL = null;
        String[] CheckedItemsIDX = null;
        String objCheckedItemsJsonStr = "";
        String objCheckedItemsIDXJsonStr = "";
        Boolean[] hasOTPorReqSignBool;
        Boolean hasSpecialCOD = false;
        Boolean hasMulitpleTradeInJobsSelected = false;
        ArrayList massOKPchecks = null;

        if (item.getItemId() == R.id.waze) {
            cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()) {
                ////following attempts to open waze map, if installed, else, open playstore
                ////z == zoom level, max value 8192
                try {
                    // Launch Waze to look for Hawaii:
                    //if waze not installed, will open browser
                    //better to check if waze is stalled first
                    String LATstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LAT");
                    String LNGstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LNG");
                    String ll = LATstr + "," + LNGstr;
                    String postalstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
                    if (!objGlobalMethods.IsEmptyString(postalstr)) {
                        if (JobsHandler2.wazeInstalled()) {
                            String url = "https://waze.com/ul?ll=" + ll + "&navigate=yes";
                            //String url = "https://waze.com/ul?q=Singapore " + postalstr + "&navigate=yes";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        } else {
                            objGlobalMethods.showInstallWaze().show();
                        }
                    }

                } catch (ActivityNotFoundException ex) {
                    // If Waze is not installed, open it in Google Play:
                    objGlobalMethods.showInstallWaze().show();
                }
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_unableopenmapnointernet));
            }
        } else if (item.getItemId() == R.id.map) {
            //open map
            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()) {
                //parentIntent = new Intent(JobsListGroupActivity.this, MapsActivity.class);
                parentIntent = new Intent(myApp.currentActivity, MapsActivity.class);
                MainActivity.uponLogin = 0;
                startActivity(parentIntent);
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_unableopenmapnointernet));
            }
        } else if (item.getItemId() == R.id.qrscan) {
            launchQRScan();
        } else if (item.getItemId() == R.id.massPU) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
            objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
            if (!objCheckedItemsJsonStr.equals("")) {
                objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                i = objCheckedItemsAL.length;
            } else {
                i = 0;
            }

            if (!objCheckedItemsIDXJsonStr.equals("")) {
                CheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
            }

            verifyB4massUpdateJobStatus(49, objCheckedItemsAL, i, CheckedItemsIDX);
        } else if (item.getItemId() == R.id.massDIP) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
            objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
            if (!objCheckedItemsJsonStr.equals("")) {
                objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                i = objCheckedItemsAL.length;
            } else {
                i = 0;
            }

            if (!objCheckedItemsIDXJsonStr.equals("")) {
                CheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
            }

            verifyB4massUpdateJobStatus(104, objCheckedItemsAL, i, CheckedItemsIDX);
        } else if (item.getItemId() == R.id.massOKP) {

        } else if (item.getItemId() == R.id.massFail) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
            objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
            if (!objCheckedItemsJsonStr.equals("")) {
                objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                i = objCheckedItemsAL.length;
            } else {
                i = 0;
            }

            int CurrCatType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            if (CurrCatType == 0) {
                verifyB4massFailStatus("fail", i, objCheckedItemsAL, "fp");
            } else if (CurrCatType == 2) {
                verifyB4massFailStatus("fail", i, objCheckedItemsAL, "fd");
            }
        }

        /*switch (item.getItemId()) {
            case R.id.waze:
                cd = new ConnectionDetector(null);
                if (cd.isConnectingToInternet()) {
                    ////following attempts to open waze map, if installed, else, open playstore
                    ////z == zoom level, max value 8192
                    try {
                        // Launch Waze to look for Hawaii:
                        //if waze not installed, will open browser
                        //better to check if waze is stalled first
                        String LATstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LAT");
                        String LNGstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LNG");
                        String ll = LATstr + "," + LNGstr;
                        String postalstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
                        if (!objGlobalMethods.IsEmptyString(postalstr)) {
                            if (JobsHandler2.wazeInstalled()) {
                                String url = "https://waze.com/ul?ll=" + ll + "&navigate=yes";
                                //String url = "https://waze.com/ul?q=Singapore " + postalstr + "&navigate=yes";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                            } else {
                                objGlobalMethods.showInstallWaze().show();
                            }
                        }

                    } catch (ActivityNotFoundException ex) {
                        // If Waze is not installed, open it in Google Play:
                        objGlobalMethods.showInstallWaze().show();
                    }
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_unableopenmapnointernet));
                }
                break;
            case R.id.map:
                //open map
                //cd = new ConnectionDetector(context);
                cd = new ConnectionDetector(null);
                if (cd.isConnectingToInternet()) {
                    //parentIntent = new Intent(JobsListGroupActivity.this, MapsActivity.class);
                    parentIntent = new Intent(myApp.currentActivity, MapsActivity.class);
                    MainActivity.uponLogin = 0;
                    startActivity(parentIntent);
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_unableopenmapnointernet));
                }
                break;
            case R.id.qrscan:
                launchQRScan();
                break;
            case R.id.massPU:
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
                objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
                if (!objCheckedItemsJsonStr.equals("")) {
                    objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                    i = objCheckedItemsAL.length;
                } else {
                    i = 0;
                }

                if (!objCheckedItemsIDXJsonStr.equals("")) {
                    CheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
                }

                verifyB4massUpdateJobStatus(49, objCheckedItemsAL, i, CheckedItemsIDX);

                break;
            case R.id.massDIP:
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
                objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
                if (!objCheckedItemsJsonStr.equals("")) {
                    objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                    i = objCheckedItemsAL.length;
                } else {
                    i = 0;
                }

                if (!objCheckedItemsIDXJsonStr.equals("")) {
                    CheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
                }

                verifyB4massUpdateJobStatus(104, objCheckedItemsAL, i, CheckedItemsIDX);
                break;
            case R.id.massOKP:

                break;
            case R.id.massFail:
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
                objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
                if (!objCheckedItemsJsonStr.equals("")) {
                    objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                    i = objCheckedItemsAL.length;
                } else {
                    i = 0;
                }

                int CurrCatType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                if (CurrCatType == 0) {
                    verifyB4massFailStatus("fail", i, objCheckedItemsAL, "fp");
                } else if (CurrCatType == 2) {
                    verifyB4massFailStatus("fail", i, objCheckedItemsAL, "fd");
                }
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GcmPreferences = objGlobalMethods.getGcmPreferences();

        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");

        String[] prefName = new String[]{"searchedval", "ScannedCodeStrsPrefStr", objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX};
        String[] prefvalue = new String[]{"", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        if (!GcmPreferences.getString("isFromDashboardSub", "").isEmpty()) {
            String[] prefName1 = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName};
            String[] prefvalue1 = new String[]{"-1", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName1, prefvalue1);

            //Intent parentIntent = new Intent(this, DashboardSubActivity.class);
            Intent parentIntent = new Intent(myApp.currentActivity, DashboardSubActivity.class);
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            this.finish();
        } else {
            String[] prefName2 = new String[]{"desPostal", "LAT", "LNG", "desAddress"};
            String[] prefvalue2 = new String[]{"", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName2, prefvalue2);

            //Intent parentIntent = new Intent(this, AddressListNewActivity.class);
            Intent parentIntent = new Intent(myApp.currentActivity, AddressListNewActivity.class);
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            this.finish();
        }
    }

    private void hideshowButtons(int CatTypeID) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            String enableJobUpdatesPref = objGlobalMethods.getGCMPrefs(GcmPreferences, "enableJobUpdates");
            boolean enableJobUpdates = enableJobUpdatesPref.equals("") || enableJobUpdatesPref.equals("true") ? true : false;

            btnPU.setVisibility(CatTypeID == 0 ? View.VISIBLE : View.GONE);
            btnDIP.setVisibility(CatTypeID == 1 ? View.VISIBLE : View.GONE);
            btnOKP.setVisibility(CatTypeID == 2 ? View.VISIBLE : View.GONE);
            btnFail.setVisibility(CatTypeID == 0 || CatTypeID == 2 ? View.VISIBLE : View.GONE);

            btnPU.setText(myApp.getAppContext().getString(R.string.action_massPU));
            btnDIP.setText(myApp.getAppContext().getString(R.string.action_massDIP));
            btnOKP.setText(myApp.getAppContext().getString(R.string.action_massOKP));
            btnFail.setText(CatTypeID == 0 ? myApp.getAppContext().getString(R.string.action_massFail) : myApp.getAppContext().getString(R.string.action_massFailDIP));

            ctrlSettings.enableDisableSubmitButtonWithStyles(enableJobUpdates, btnPU);
            ctrlSettings.enableDisableSubmitButtonWithStyles(enableJobUpdates, btnDIP);
            ctrlSettings.enableDisableSubmitButtonWithStyles(enableJobUpdates, btnOKP);
            ctrlSettings.enableDisableFailButtonWithStyles(enableJobUpdates, btnFail);

        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity hideshowButtons. Exception: " + s, "", "", "", "");
        }
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    //private void expandSortedSQLiteList(JobListGroupSortedSQLite_PostItemAdapter adapter) {
    private void expandSortedSQLiteList(JobListGroupSortedSQLiteNew_PostItemAdapter adapter) {
        for (int i = 0; i <= adapter.getGroupCount() - 1; i++) {
            expListView.expandGroup(i);
        }
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        //parentIntent = new Intent(JobsListGroupActivity.this, MainActivity.class);
        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    private void invalidAction() {
        //playTone();
        showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_operationnotsupported));
    }

    //check
    public void submitMassPU(View v) {
        int i = 0;
        Gson gson = new Gson();
        objCheckedItems[] objCheckedItemsAL = null;
        String[] CheckedItemsIDX = null;
        String objCheckedItemsJsonStr = "";
        String objCheckedItemsIDXJsonStr = "";
        boolean not_ihavearrive = false;
        objJobInfo ji = null;
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
            objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
            if (!objCheckedItemsJsonStr.equals("")) {
                objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                i = objCheckedItemsAL.length;
            } else {
                i = 0;
            }

            if (!objCheckedItemsIDXJsonStr.equals("")) {
                CheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
            }

            if (i > 0) {
                Boolean PromptScanBarcode = false;
                Boolean isSecurityBag = false;
                for (int q = 0; q <= objCheckedItemsAL.length - 1; q++) {
                    ji = objCheckedItemsAL[q].jobinfo;
                    if (JobsHandler.PromptScanBarcodeUponPU(ji)) {
                        PromptScanBarcode = true;
                        isSecurityBag = JobsHandler.reqSecurityTag(ji);
                        break;
                    }
                    if (ji.i_have_arrived_timestamp == 0) {
                        not_ihavearrive = true;
                        break;
                    }
                }

                if (PromptScanBarcode) {
                    showDialog(isSecurityBag ? myApp.getAppContext().getString(R.string.alert_reqSecuritytagging) : myApp.getAppContext().getString(R.string.alert_reqtagging));
                } else if (not_ihavearrive) {
                    showIhaveArriveDialog();
                } else {
                    verifyB4massUpdateJobStatus(49, objCheckedItemsAL, i, CheckedItemsIDX);
                }
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_nojobselect));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity submitPU. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    //check
    public void submitMassDIP(View v) {
        int i = 0;
        Gson gson = new Gson();
        objCheckedItems[] objCheckedItemsAL = null;
        String[] CheckedItemsIDX = null;
        String objCheckedItemsJsonStr = "";
        String objCheckedItemsIDXJsonStr = "";

        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
            objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
            if (!objCheckedItemsJsonStr.equals("")) {
                objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                i = objCheckedItemsAL.length;
            } else {
                i = 0;
            }
            if (i > 0) {
                if (!objCheckedItemsIDXJsonStr.equals("")) {
                    CheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
                }

                verifyB4massUpdateJobStatus(104, objCheckedItemsAL, i, CheckedItemsIDX);
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_nojobselect));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity submitMassDIP. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    public void submitMassOKPNew(View v) {
        Intent asd = null;
        SharedPreferences glsp = objGlobalMethods.getGrouplistSP();
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        String jobSelectionInfoJsonStr = "";
        jobSelectionInfo j1 = JobListGroupSortedSQLiteNew_PostItemAdapter.j1;
        Gson gson = new Gson();
        objCheckedItems[] objCheckedItemsAL = null;
        String objCheckedItemsJsonStr = "";
        Boolean hasPartOne = false;
        boolean hasIHaveArrived_NotUpdatedYet = false;
        boolean attemptTimeOneOrMoreDaysEarlierThanWindowForAllSelected = false;
        boolean hasStandardRunJob = false;
        boolean hasXDelJob = false;
        boolean isB2B = false;
        boolean hasResidential = false;
        ArrayList<Long> CanGetReviewLinkJobsIDXs = new ArrayList<>();
        int i = 0;

        try {


            objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
            if (!objCheckedItemsJsonStr.equals("")) {
                objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                i = objCheckedItemsAL.length;
                if (i > 1) {
                    for (int x = 0; x <= objCheckedItemsAL.length - 1; x++) {
                        if (objCheckedItemsAL[x].isPartOne != null && objCheckedItemsAL[x].isPartOne) {
                            hasPartOne = true;
                            break;
                        }
                    }
                }
            } else {
                i = 0;
            }

            if (i > 0 && i == 1) {
                asd = new Intent(this, JobInfoActivity.class);
                JobInfoActivity.accessFromDirection = 1;
                String JobInfoJsonStr = objCheckedItemsAL[0].JobInfoJsonStr;
                objJobInfo JobInfo = gson.fromJson(JobInfoJsonStr, objJobInfo.class);
                Boolean isDunning = JobsHandler.isDunning(JobInfo);

                String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX,
                        "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "hfJobInfoJsonStr"};
                String[] prefvalue = new String[]{"", "",
                        "", "", JobInfoJsonStr};
                objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);

                String[] prefUName = new String[]{"objPendingJobStatusUpdatesobjALPrefStr", "objPendingJobPhotoUpdatesobjALPrefStr", "objUpdateJobInfoPhotoObjListJsonStr", "objUpdateJobInfoSignatureObjListJsonStr"};
                String[] prefUvalue = new String[]{"", "", "", ""};
                objGlobalMethods.setUpdateJobInfoPrefsMass(UpdateJobInfoPreferences, prefUName, prefUvalue);

                if (isDunning) {
                    String CannotTakePhoto = "";
                    if (JobInfo != null)
                        CannotTakePhoto = JobsHandler.CannotTakePhoto(JobInfo, 2) ? "1" : "0";

                    String addressWithUnit = "";
                    if (JobInfo.DLRedirectedLocation != null) {
                        //only for dunning
                        addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(JobInfo.DLRedirectedLocation);
                    } else if (JobInfo.DLLocation != null) {
                        //only for dunning
                        addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(JobInfo.DLLocation);
                    }

                    String[] prefName2 = new String[]{"CannotTakePhoto", objGlobalVariables.currJobInfoIDX, "currJobInfoCN", "addressWithUnit"};
                    String[] prefvalue2 = new String[]{CannotTakePhoto, String.valueOf(JobInfo.JobsIDX), JobInfo.JobNo, addressWithUnit};
                    objGlobalMethods.setGCMPrefsMass(gcmp, prefName2, prefvalue2);

                    asd = new Intent(myApp.currentActivity, setDunningDeliveredStatusActivity.class);
                    JobInfoActivity.accessFromDirection = 0;
                }
                startActivity(asd);
            } else if (i > 0 && i > 1) {
                Boolean SettingsDistVioEnabled = !objGlobalMethods.getSettingsDistVioEnabled().equals("");
                SettingsDistVioEnabled = false;
                Boolean PromptDistanceViolationUponUpdate = JobsHandler.PromptDistanceViolationUponUpdate(objCheckedItemsAL[0].jobinfo, catType, 4);
                PromptDistanceViolationUponUpdate = false;

                if (SettingsDistVioEnabled && PromptDistanceViolationUponUpdate) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_outofrangecfm)).show();
                } else {
                    hasIHaveArrived_NotUpdatedYet = JobsHandler.hasIHaveArrived_NotUpdatedYet(objCheckedItemsAL);
                    ////commented away, dunno wan to implement or not
                    /*if (!hasIHaveArrived_NotUpdatedYet) {
                        attemptTimeOneOrMoreDaysEarlierThanWindowForAllSelected = JobsHandler.attemptTimeOneOrMoreDaysEarlierThanWindowForAllSelected(objCheckedItemsAL);
                    }*/
                    ////commented away, dunno wan to implement or not

                    if (hasIHaveArrived_NotUpdatedYet) {
                        showIhaveArriveDialog();
                    } else {
                        if (j1 == null)
                            jobSelectionInfoJsonStr = objGlobalMethods.getGrouplistSP_KeyValue(glsp, "jobSelectionInfoJsonStr");
                        if (j1 == null && !objGlobalMethods.IsEmptyString(jobSelectionInfoJsonStr))
                            j1 = gson.fromJson(jobSelectionInfoJsonStr, jobSelectionInfo.class);


                        if (JobListGroupSortedSQLiteNew_PostItemAdapter.j1 != null) {
                            //at this stage shd not need to check if multiselect contains trade-in/specialCOD/seal/staff ID/shpmt verification/item image proof etc
                            //as they shd alr been filtered out
                            objMultiSelectInitialCriteria cri = JobListGroupSortedSQLite_PostItemAdapter.criteria;
                            //isB2B = JobsHandler.isB2B(objCheckedItemsAL); the flag checker here is now used for cannot skip otp as of 11/07/2025
                            hasResidential = JobsHandler2.hasResidentialLocationType(objCheckedItemsAL);

                            Integer jobflagval = j1.FLAG;
                            Integer jobflag2val = j1.FLAG2;

                            SignatureNameActivity.requireSignature = j1.ReqSign;
                            hasStandardRunJob = JobsHandler.hasStandardRunJob(objCheckedItemsAL);
                            hasXDelJob = JobsHandler.hasXDelJob(objCheckedItemsAL);
                            CanGetReviewLinkJobsIDXs = JobsHandler.rtnReviewLinkJobsIDXs(objCheckedItemsAL);

                            String[] finalChecklist = j1.CHECKLIST;
                            String IDVerificationName = !objGlobalMethods.IsEmptyString(j1.RECEIVER_NAME) ? j1.RECEIVER_NAME : "";
                            String IDVerification = !objGlobalMethods.IsEmptyString(j1.CUSTID) ? j1.CUSTID : "";
                            String JobIDXsForContactlessChecklistJsonStr = !objGlobalMethods.IsEmptyArray(j1.JobIDXsForContactlessChecklist) ? gson.toJson(j1.JobIDXsForContactlessChecklist) : "";
                            String JobIDXsForVerificationChecklistJsonStr = !objGlobalMethods.IsEmptyArray(j1.JobIDXsForVerificationChecklist) ? gson.toJson(j1.JobIDXsForVerificationChecklist) : "";
                            String JobNosForContactlessChecklistJsonStr = !objGlobalMethods.IsEmptyArray(j1.JobNosForContactlessChecklist) ? gson.toJson(j1.JobNosForContactlessChecklist) : "";
                            String JobNosForVerificationChecklistJsonStr = !objGlobalMethods.IsEmptyArray(j1.JobNosForVerificationChecklist) ? gson.toJson(j1.JobNosForVerificationChecklist) : "";
                            String CanGetReviewLinkJobsIDXsJsonStr = (CanGetReviewLinkJobsIDXs != null && CanGetReviewLinkJobsIDXs.size() > 0) ? gson.toJson(CanGetReviewLinkJobsIDXs) : "";

                            String[] prefName = new String[]{"jobflag", "jobflag2", "IDVerificationName", "IDVerification", "CanGetReviewLinkJobsIDXsJsonStr",
                                    "JobIDXsForContactlessChecklistJsonStr", "JobIDXsForVerificationChecklistJsonStr", "JobNosForContactlessChecklistJsonStr",
                                    "JobNosForVerificationChecklistJsonStr", "isResidential"};
                            String[] prefvalue = new String[]{String.valueOf(jobflagval), String.valueOf(jobflag2val), IDVerificationName, IDVerification, CanGetReviewLinkJobsIDXsJsonStr,
                                    JobIDXsForContactlessChecklistJsonStr, JobIDXsForVerificationChecklistJsonStr, JobNosForContactlessChecklistJsonStr,
                                    JobNosForVerificationChecklistJsonStr, hasResidential ? "y" : ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                            if (finalChecklist == null && hasResidential) {
                                finalChecklist = new String[]{myApp.getAppContext().getString(R.string.residential_photo_checklist_item)};
                            }

                            verifyB4redirection2("okp", hasPartOne, i, objCheckedItemsAL, hasStandardRunJob, hasXDelJob, finalChecklist, jobflagval, jobflag2val);
                        } else {
                            objGlobalMethods.writeToFile("Error at JobsListGroupActivity submitMassOKPNew. JobListGroupSortedSQLiteNew_PostItemAdapter.j1 IS NULL.", "", "", "", "");
                            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
                        }
                    }
                }
            } else if (i == 0) {
                showDialog(myApp.getAppContext().getString(R.string.alert_nojobselect));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity submitMassOKPNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void submitMassOKP2(View v) {
        int i = 0;
        Gson gson = new Gson();
        objCheckedItems[] objCheckedItemsAL = null;
        String objCheckedItemsJsonStr = "";
        Boolean hasPartOne = false;
        boolean hasIHaveArrived_NotUpdatedYet = false;
        boolean attemptTimeOneOrMoreDaysEarlierThanWindowForAllSelected = false;
        boolean hasStandardRunJob = false;
        boolean hasXDelJob = false;
        boolean isB2B = false;
        boolean hasResidential = false;
        ArrayList<Long> CanGetReviewLinkJobsIDXs = new ArrayList<>();

        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        try {
            objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
            if (!objCheckedItemsJsonStr.equals("")) {
                objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                i = objCheckedItemsAL.length;
                if (i > 1) {
                    for (int x = 0; x <= objCheckedItemsAL.length - 1; x++) {
                        if (objCheckedItemsAL[x].isPartOne != null && objCheckedItemsAL[x].isPartOne) {
                            hasPartOne = true;
                            break;
                        }
                    }
                }
            } else {
                i = 0;
            }

            if (i > 0 && i == 1) {
                Intent asd;
                //asd = new Intent(this, JobInfoActivity.class);
                asd = new Intent(myApp.currentActivity, JobInfoActivity.class);
                JobInfoActivity.accessFromDirection = 1;
                String JobInfoJsonStr = objCheckedItemsAL[0].JobInfoJsonStr;
                objJobInfo JobInfo = gson.fromJson(JobInfoJsonStr, objJobInfo.class);
                Boolean isDunning = JobsHandler.isDunning(JobInfo);

                String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr"};
                String[] prefvalue = new String[]{"", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objUpdateJobInfoPhotoObjListJsonStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objUpdateJobInfoSignatureObjListJsonStr", "");

                objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, JobInfoJsonStr);
                if (isDunning) {
                    if (JobInfo != null) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", JobsHandler.CannotTakePhoto(JobInfo, 2) ? "1" : "0");
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
                    }

                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX, String.valueOf(JobInfo.JobsIDX));
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "currJobInfoCN", JobInfo.JobNo);
                    String addressWithUnit = "";
                    if (JobInfo.DLRedirectedLocation != null) {
                        //only for dunning
                        addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(JobInfo.DLRedirectedLocation);
                    } else if (JobInfo.DLLocation != null) {
                        //only for dunning
                        addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(JobInfo.DLLocation);
                    }
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "addressWithUnit", addressWithUnit);
                    //asd = new Intent(this, setDunningDeliveredStatusActivity.class);
                    asd = new Intent(myApp.currentActivity, setDunningDeliveredStatusActivity.class);
                    JobInfoActivity.accessFromDirection = 0;
                }
                startActivity(asd);
            } else if (i > 0 && i > 1) {
                Boolean SettingsDistVioEnabled = !objGlobalMethods.getSettingsDistVioEnabled().equals("") ? true : false;
                SettingsDistVioEnabled = false;
                Boolean PromptDistanceViolationUponUpdate = JobsHandler.PromptDistanceViolationUponUpdate(objCheckedItemsAL[0].jobinfo, catType, 4);
                PromptDistanceViolationUponUpdate = false;

                if (SettingsDistVioEnabled && PromptDistanceViolationUponUpdate) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_outofrangecfm)).show();
                } else {
                    hasIHaveArrived_NotUpdatedYet = JobsHandler.hasIHaveArrived_NotUpdatedYet(objCheckedItemsAL);
                    ////commented away, dunno wan to implement or not
                    /*if (!hasIHaveArrived_NotUpdatedYet) {
                        attemptTimeOneOrMoreDaysEarlierThanWindowForAllSelected = JobsHandler.attemptTimeOneOrMoreDaysEarlierThanWindowForAllSelected(objCheckedItemsAL);
                    }*/
                    ////commented away, dunno wan to implement or not

                    if (hasIHaveArrived_NotUpdatedYet) {
                        showIhaveArriveDialog();
                    } else {
                        if (JobListGroupSortedSQLite_PostItemAdapter.criteria == null) {
                            if (GcmPreferences == null) {
                                GcmPreferences = objGlobalMethods.getGcmPreferences();
                            }
                            String objMultiSelectInitialCriteriaJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objMultiSelectInitialCriteriaJsonStr");
                            JobListGroupSortedSQLite_PostItemAdapter.criteria = objGlobalMethods.IsEmptyString(objMultiSelectInitialCriteriaJsonStr) ? null :
                                    gson.fromJson(objMultiSelectInitialCriteriaJsonStr, objMultiSelectInitialCriteria.class);
                        }

                        if (JobListGroupSortedSQLite_PostItemAdapter.criteria != null) {
                            //at this stage shd not need to check if multiselect contains trade-in/specialCOD/seal/staff ID/shpmt verification/item image proof etc
                            //as they shd alr been filtered out
                            objMultiSelectInitialCriteria cri = JobListGroupSortedSQLite_PostItemAdapter.criteria;
                            //isB2B = JobsHandler.isB2B(objCheckedItemsAL); the flag checker here is now used for cannot skip otp as of 11/07/2025
                            hasResidential = JobsHandler2.hasResidentialLocationType(objCheckedItemsAL);
                            Integer jobflagval = 0;
                            jobflagval += cri.hasIsReqStaffID ? 4096 : 0;
                            jobflagval += cri.getHasContactless() && !cri.getIDVerificationRequired() ? 67108864 : 0;
                            jobflagval += cri.getCanSlotLB() ? 1 : 0;
                            jobflagval += cri.getCanSlotUnderDoor() ? 2 : 0;
                            jobflagval += cri.getCanGuard() ? 4 : 0;
                            jobflagval += cri.getReceiverOnly() ? 16 : 0;
                            jobflagval += cri.getReviewLinkSMS() ? 16777216 : 0;
                            jobflagval += cri.getReqSign() ? 1024 : 0;
                            jobflagval += isB2B ? 8192 : 0;

                            Integer jobflag2val = 0;
                            jobflag2val += cri.getCanLeaveDoorStep() ? 2048 : 0;
                            jobflag2val += cri.IDVerificationReqFlag;
                            jobflag2val += cri.IDVerificationNotReqFlag;

                            SignatureNameActivity.requireSignature = cri.getReqSign();
                            hasStandardRunJob = JobsHandler.hasStandardRunJob(objCheckedItemsAL);
                            hasXDelJob = JobsHandler.hasXDelJob(objCheckedItemsAL);
                            CanGetReviewLinkJobsIDXs = JobsHandler.rtnReviewLinkJobsIDXs(objCheckedItemsAL);

                            //objGlobalMethods.setGCMPrefs(GcmPreferences, "jobflag", String.valueOf(jobflagval));
                            String[] finalChecklist = cri.getFinalChecklist();
                            String IDVerificationName = !objGlobalMethods.IsEmptyString(cri.ContactName) ? cri.ContactName : "";
                            String IDVerification = !objGlobalMethods.IsEmptyString(cri.CustID) ? cri.CustID : "";
                            String JobIDXsForContactlessChecklistJsonStr = !objGlobalMethods.IsEmptyArray(cri.JobIDXsForContactlessChecklist) ? gson.toJson(cri.JobIDXsForContactlessChecklist) : "";
                            String JobIDXsForVerificationChecklistJsonStr = !objGlobalMethods.IsEmptyArray(cri.JobIDXsForVerificationChecklist) ? gson.toJson(cri.JobIDXsForVerificationChecklist) : "";
                            String JobNosForContactlessChecklistJsonStr = !objGlobalMethods.IsEmptyArray(cri.JobNosForContactlessChecklist) ? gson.toJson(cri.JobNosForContactlessChecklist) : "";
                            String JobNosForVerificationChecklistJsonStr = !objGlobalMethods.IsEmptyArray(cri.JobNosForVerificationChecklist) ? gson.toJson(cri.JobNosForVerificationChecklist) : "";
                            String CanGetReviewLinkJobsIDXsJsonStr = (CanGetReviewLinkJobsIDXs != null && CanGetReviewLinkJobsIDXs.size() > 0) ? gson.toJson(CanGetReviewLinkJobsIDXs) : "";

                            String[] prefName = new String[]{"jobflag", "jobflag2", "IDVerificationName", "IDVerification", "CanGetReviewLinkJobsIDXsJsonStr",
                                    "JobIDXsForContactlessChecklistJsonStr", "JobIDXsForVerificationChecklistJsonStr", "JobNosForContactlessChecklistJsonStr",
                                    "JobNosForVerificationChecklistJsonStr", "isResidential"};
                            String[] prefvalue = new String[]{String.valueOf(jobflagval), String.valueOf(jobflag2val), IDVerificationName, IDVerification, CanGetReviewLinkJobsIDXsJsonStr,
                                    JobIDXsForContactlessChecklistJsonStr, JobIDXsForVerificationChecklistJsonStr, JobNosForContactlessChecklistJsonStr,
                                    JobNosForVerificationChecklistJsonStr, hasResidential ? "y" : ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                            if (finalChecklist == null && hasResidential) {
                                finalChecklist = new String[]{myApp.getAppContext().getString(R.string.residential_photo_checklist_item)};
                            }

                            verifyB4redirection2("okp", hasPartOne, i, objCheckedItemsAL, hasStandardRunJob, hasXDelJob, finalChecklist, jobflagval, jobflag2val);
                        } else {
                            objGlobalMethods.writeToFile("Error at JobsListGroupActivity submitMassOKP2. obListGroupSortedSQLite_PostItemAdapter.criteria IS NULL.", "", "", "", "");
                            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
                        }
                    }
                }
            } else if (i == 0) {
                showDialog(myApp.getAppContext().getString(R.string.alert_nojobselect));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity submitMassOKP2. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    public void OKPdummyClick(View v) {
        Context ctx = myApp.getAppContext();
        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.okpdummyalert)).show();
    }

    public void submitMassFPFD(View v) {
        int i = 0;
        Gson gson = new Gson();
        objCheckedItems[] objCheckedItemsAL = null;
        String objCheckedItemsJsonStr = "";
        objJobInfo ji = null;
        boolean hasIHaveArrived_NotUpdatedYet = false;
        boolean allSelectedReqCallOutFirst = false;
        boolean allSelectedAttemptedEarlier = false;
        ArrayList<String> JobNoCN = new ArrayList<>();
        boolean CanSlot = false;
        boolean CanUnderDoor = false;
        boolean aCanSlot = false;
        boolean aCanUnderDoor = false;
        String jobnocnval = "";
        Context c = myApp.getAppContext();

        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
            if (!objCheckedItemsJsonStr.equals("")) {
                objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                i = objCheckedItemsAL.length;
            } else {
                i = 0;
            }

            if (i > 0) {
                hasIHaveArrived_NotUpdatedYet = JobsHandler.hasIHaveArrived_NotUpdatedYet(objCheckedItemsAL);
                if (hasIHaveArrived_NotUpdatedYet) {
                    showIhaveArriveDialog();
                } else {
                    int CurrCatType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                    Boolean cannotphoto = false;
                    int NoOfCannotTakePhotoJobs = 0;
                    for (int q = 0; q <= objCheckedItemsAL.length - 1; q++) {
                        jobnocnval = "";
                        ji = objCheckedItemsAL[q].jobinfo;

                        if (JobsHandler.CannotTakePhoto(ji, CurrCatType)) {
                            NoOfCannotTakePhotoJobs += 1;
                        }
                        if (CurrCatType == 2) {
                            jobnocnval = !objGlobalMethods.IsEmptyString(ji.ConsignmentNote) ? ji.ConsignmentNote : ji.JobNo;
                            CanSlot = objGlobalMethods.isCanSlotLB(ji.FLAG);
                            CanUnderDoor = objGlobalMethods.isCanSlotUnderDoor(ji.FLAG);
                            if ((CanSlot || CanUnderDoor) && !JobNoCN.contains(jobnocnval)) {
                                JobNoCN.add(jobnocnval);
                            }
                            if (!aCanSlot && CanSlot) {
                                aCanSlot = true;
                            }
                            if (!aCanUnderDoor && CanUnderDoor) {
                                aCanUnderDoor = true;
                            }
                        }
                    }

                    //AllCannotTakePhoto: 0 == 0 job cannot take photo, 1 == some cannot take photo, 2 == all cannot take photo
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", NoOfCannotTakePhotoJobs == 0 ? "0"
                            : (NoOfCannotTakePhotoJobs > 0 && NoOfCannotTakePhotoJobs < objCheckedItemsAL.length) ? "2"
                            : NoOfCannotTakePhotoJobs == objCheckedItemsAL.length && objCheckedItemsAL.length > 1 ? "3"
                            : NoOfCannotTakePhotoJobs == objCheckedItemsAL.length && objCheckedItemsAL.length == 1 ? "1" : "0");

                    cannotphoto = ((NoOfCannotTakePhotoJobs > 0) && (NoOfCannotTakePhotoJobs < objCheckedItemsAL.length)) ? true : false;

                    if (cannotphoto) {
                        showDialog(c.getString(R.string.alert_cannottakephoto0));
                    } else {
                        if (CurrCatType == 0) {
                            verifyB4massFailStatus("fail", i, objCheckedItemsAL, "fp");
                        } else if (CurrCatType == 2) {
                            //new code for call mandate logic - start
                            /*boolean hasStandardRunJob = JobsHandler.hasStandardRunJob(objCheckedItemsAL);
                            boolean hasXDelJob = JobsHandler.hasXDelJob(objCheckedItemsAL);
                            allSelectedReqCallOutFirst = JobsHandler.allSelectedReqCallOutFirst(objCheckedItemsAL);
                            allSelectedAttemptedEarlier = JobsHandler.allSelectedAttemptedEarlier(2, objCheckedItemsAL);
                            String AllValidTelNums = JobsHandler.getAllValidTelNums(objCheckedItemsAL, null, 2);
                            String[] ContactlessCallPromptCheck = null;
                            String ContactlessCallPromptMsg = "";
                            String ContactlessCanProceed = "";
                            if (!hasStandardRunJob && !hasXDelJob && (allSelectedReqCallOutFirst || allSelectedAttemptedEarlier) && !objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                                setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
                                ContactlessCallPromptCheck = JobsHandler.getContactlessCallPromptMsg(2);
                                ContactlessCanProceed = ContactlessCallPromptCheck[0];
                                ContactlessCallPromptMsg = ContactlessCallPromptCheck[1];
                            }
                            String okpShowReqCallLogImgButton = objGlobalMethods.getGCMPrefs(GcmPreferences, "okpShowReqCallLogImgButton");
                            if (!hasStandardRunJob && !hasXDelJob && (allSelectedReqCallOutFirst || allSelectedAttemptedEarlier) && !objGlobalMethods.IsEmptyString(AllValidTelNums)
                                    && !objGlobalMethods.IsEmptyString(ContactlessCallPromptMsg) && ContactlessCanProceed.equals("0")) {

                                *//*final objCheckedItems[] FinalobjCheckedItemsAL = objCheckedItemsAL;
                                final Handler handler = new Handler();
                                final boolean dueReqCallOutFirst = allSelectedReqCallOutFirst;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        refreshListToChangeColor(FinalobjCheckedItemsAL, dueReqCallOutFirst ? 1 : 2);
                                    }
                                }, 100);*//*

                                showDialog(allSelectedReqCallOutFirst ? myApp.getAppContext().getString(R.string.plscallrecipientfirstgrp)
                                        : myApp.getAppContext().getString(R.string.attemptearlycalldipgrp));
                            } else if (!hasStandardRunJob && !hasXDelJob && (allSelectedReqCallOutFirst || allSelectedAttemptedEarlier) && !objGlobalMethods.IsEmptyString(AllValidTelNums)
                                    && !objGlobalMethods.IsEmptyString(ContactlessCallPromptMsg) && ContactlessCanProceed.equals("1") && objGlobalMethods.IsEmptyString(okpShowReqCallLogImgButton)) {

                                final objCheckedItems[] FinalobjCheckedItemsAL = objCheckedItemsAL;
                                *//*final Handler handler = new Handler();
                                final boolean dueReqCallOutFirst = allSelectedReqCallOutFirst;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        refreshListToChangeColor(FinalobjCheckedItemsAL, dueReqCallOutFirst ? 1 : 2);
                                    }
                                }, 100);*//*

                                showReqCallLogImgAlert(CurrCatType, i, FinalobjCheckedItemsAL, 1);
                            } else {
                                verifyB4massFailStatus("fail", i, objCheckedItemsAL, "fd");
                            }*/

                            //new code for call mandate logic - end

                            if ((aCanSlot || aCanUnderDoor) && JobNoCN.size() > 0) {
                                String CNtxt = "";
                                String txtstarter = c.getString(R.string.followingjobs);
                                String fulltxt = "";
                                for (int x = 0; x <= JobNoCN.size() - 1; x++) {
                                    CNtxt += JobNoCN.get(x) + "\n";
                                }
                                if (!objGlobalMethods.IsEmptyString(CNtxt)) {
                                    fulltxt = txtstarter + "\n\n" + CNtxt;
                                }
                                if (!objGlobalMethods.IsEmptyString(fulltxt)) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                    builder.setCancelable(false);
                                    TextView at = new TextView(myApp.getAppContext());
                                    at.setText(myApp.getAppContext().getString(R.string.reminder));
                                    at.setTextSize(14);
                                    at.setTypeface(null, Typeface.BOLD);
                                    at.setTextColor(ContextCompat.getColor(myApp.getAppContext(), R.color.colorPrimary));
                                    at.setPadding(75, 20, 0, 20);
                                    at.setBackgroundColor(Color.WHITE);
                                    builder.setCustomTitle(at);
                                    builder.setMessage(fulltxt);

                                    builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO3), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //do nothing
                                        }
                                    });

                                    final int count = i;
                                    final objCheckedItems[] aobjCheckedItemsAL = objCheckedItemsAL;
                                    builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes3), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            verifyB4massFailStatus("fail", count, aobjCheckedItemsAL, "fd");
                                        }
                                    });

                                    AlertDialog alert = builder.create();
                                    alert.show();

                                } else {
                                    verifyB4massFailStatus("fail", i, objCheckedItemsAL, "fd");
                                }
                            } else {
                                verifyB4massFailStatus("fail", i, objCheckedItemsAL, "fd");
                            }
                        }
                    }
                }
            } else {
                showDialog(c.getString(R.string.alert_nojobselect));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity submitMassFPFD. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    private void verifyB4redirection2(String type, final Boolean ahasPartOne, int count, final objCheckedItems[] aobjCheckedItemsAL,
                                      final boolean hasStandardRunJob, final boolean hasXDelJob, final String[] Checklist, final Integer jobflagval, final Integer jobflag2val) {
        final Boolean hasPartOne = ahasPartOne;
        final Context ctx = myApp.getAppContext();

        if (count > 0) {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                /*final Location DestinationLocationOKP = objGlobalMethods.getDestinationLocation(aobjCheckedItemsAL[0].jobinfo, 2);
                final Location gpsLocationOKP = objGlobalMethods.getLastKnownLocationForJobUpdate();

                if (objGlobalMethods.IsOutOfDestinationLocationRange(DestinationLocationOKP, gpsLocationOKP)) {
                    float distMeter = gpsLocationOKP.distanceTo(DestinationLocationOKP);
                    objGlobalMethods.writeToFile("Out of range for MASS OKP SUCH AS JobsIDX: " + aobjCheckedItemsAL[0].jobinfo.JobsIDX + ", Destination LatLng: "
                            + DestinationLocationOKP.getLatitude() + ", " + DestinationLocationOKP.getLongitude() + ", gpsLocation LatLng: "
                            + gpsLocationOKP.getLatitude() + ", " + gpsLocationOKP.getLongitude() + ", Distane: " + distMeter + "meters.","","","","");

                    builder.setMessage(myApp.getAppContext().getString(R.string.alert_outofrangecfm) + " " +
                            myApp.getAppContext().getString(R.string.alert_cfmupdate) + " " + count + " " + myApp.getAppContext().getString(R.string.alert_cfmupdateOKP));
                } else {
                    builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdate) + " " + count + " " + myApp.getAppContext().getString(R.string.alert_cfmupdateOKP));
                }*/

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                final View dv = myApp.currentActivity.getLayoutInflater().inflate(R.layout.custom_can_cannot_dialog, null);
                builder.setView(dv);
                TextView txtTitle = dv.findViewById(R.id.txtTitle);
                TextView btnCustomCanCannotOK = dv.findViewById(R.id.btnCustomCanCannotOK);
                TextView btnCustomCanCannotNo = dv.findViewById(R.id.btnCustomCanCannotNo);
                txtTitle.setText(ctx.getString(R.string.reminder));
                btnCustomCanCannotNo.setVisibility(View.VISIBLE);
                LinearLayout LLcustomCanCannot_msg = dv.findViewById(R.id.LLcustomCanCannot_msg);
                LinearLayout LLcustomCanCannotMain = dv.findViewById(R.id.LLcustomCanCannotMain);

                ArrayList<String> NormalInst = new ArrayList<>();
                ArrayList<String> tickSpecInst = new ArrayList<>();
                ArrayList<String> crossSpecInst = new ArrayList<>();
                String actionMsg = "";
                String directMsg = "";
                if (jobflagval > 0 && ((jobflagval & 67108864) == 67108864)) {
                    NormalInst.add(ctx.getString(R.string.somecontactless));
                } else {
                    if (jobflagval > 0 && (jobflagval & 16) == 16) {
                        NormalInst.add(ctx.getString(R.string.alert_therearejobs));
                        tickSpecInst.add(ctx.getString(R.string.lblSI_1_4));
                        crossSpecInst.add(ctx.getString(R.string.lblSI_1_1_1));
                        crossSpecInst.add(ctx.getString(R.string.lblSI_1_2_2));
                        crossSpecInst.add(ctx.getString(R.string.lblSI_1_3_3));
                        crossSpecInst.add(ctx.getString(R.string.lblSI_1_6_6));
                        crossSpecInst.add(ctx.getString(R.string.lblSI_1_4_4));
                        crossSpecInst.add(ctx.getString(R.string.lblSI_1_5_5));
                    } else if ((jobflagval > 0 && ((jobflagval & 1) != 1) && ((jobflagval & 2) != 2) && ((jobflagval & 4) != 4)) || (jobflagval == 0)) {
                        NormalInst.add(ctx.getString(R.string.alert_therearejobs));
                        crossSpecInst.add(ctx.getString(R.string.lblSI_1_1_1));
                        crossSpecInst.add(ctx.getString(R.string.lblSI_1_2_2));
                        crossSpecInst.add(ctx.getString(R.string.lblSI_1_3_3));
                        crossSpecInst.add(ctx.getString(R.string.lblSI_1_6_6));
                        crossSpecInst.add(ctx.getString(R.string.lblSI_1_4_4));
                        crossSpecInst.add(ctx.getString(R.string.lblSI_1_5_5));
                    }
                }

                if (NormalInst != null && NormalInst.size() > 0) {
                    TextView tv = new TextView(ctx);
                    tv.setText(NormalInst.get(0));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    tv.setTextColor(ContextCompat.getColor(ctx, R.color.black));
                    tv.setAllCaps(true);
                    //tv.setBackgroundColor((ts % 2) == 0? Color.WHITE : Color.BLACK);
                    tv.setTypeface(null, Typeface.NORMAL);
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setLayoutParams(params);
                    //tv.setPadding(20,15,20,0);
                    tv.setPadding(75, 0, 75, 0);
                    LLcustomCanCannot_msg.addView(tv);
                }
                if (tickSpecInst != null && tickSpecInst.size() > 0) {
                    for (int ts = 0; ts <= tickSpecInst.size() - 1; ts++) {
                        TextView tv = new TextView(ctx);
                        tv.setText(tickSpecInst.get(ts));
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                        //tv.setTextColor(ContextCompat.getColor(c, R.color.tickgreen));
                        tv.setAllCaps(true);
                        //tv.setBackgroundColor((ts % 2) == 0? Color.WHITE : Color.BLACK);
                        tv.setTextColor(ContextCompat.getColor(ctx, R.color.black));
                        tv.setTypeface(null, Typeface.BOLD);
                        tv.setGravity(Gravity.CENTER_VERTICAL);
                        tv.setPadding(50, 0, 75, 0);
                        tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_ok_black, 0, 0, 0);
                        tv.setLayoutParams(params);
                        tv.setBackgroundColor(ContextCompat.getColor(ctx, R.color.brightergreen));
                        LLcustomCanCannot_msg.addView(tv);
                    }
                }
                if (crossSpecInst != null && crossSpecInst.size() > 0) {
                    for (int cs = 0; cs <= crossSpecInst.size() - 1; cs++) {
                        TextView tv = new TextView(ctx);
                        tv.setText(crossSpecInst.get(cs));
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                        //tv.setTextColor(Color.RED);
                        tv.setTextColor(ContextCompat.getColor(ctx, R.color.White));
                        tv.setAllCaps(true);
                        tv.setTypeface(null, Typeface.BOLD);
                        tv.setGravity(Gravity.CENTER_VERTICAL);
                        tv.setPadding(50, 0, 75, 0);
                        tv.setLayoutParams(params);
                        tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_failpu_white, 0, 0, 0);
                        tv.setBackgroundColor(ContextCompat.getColor(ctx, R.color.menu_red));
                        LLcustomCanCannot_msg.addView(tv);
                    }
                }
                actionMsg = ctx.getString(R.string.alert_cfmupdate) + "\n" + count + " " + ctx.getString(R.string.alert_cfmupdateOKP);
                TextView tv = new TextView(ctx);
                tv.setText(actionMsg);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                tv.setTextColor(ContextCompat.getColor(ctx, R.color.black));
                tv.setAllCaps(true);
                //tv.setBackgroundColor((ts % 2) == 0? Color.WHITE : Color.BLACK);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setLayoutParams(params);
                //tv.setPadding(20,15,20,0);
                tv.setPadding(75, 10, 75, 0);
                LLcustomCanCannot_msg.addView(tv);

                final AlertDialog dvd = builder.create();
                dvd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dvd.show();

                btnCustomCanCannotOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            dvd.dismiss();
                            massOKPchecksNew(ahasPartOne, aobjCheckedItemsAL, hasStandardRunJob, hasXDelJob, Checklist, jobflagval);
                        } catch (Exception e) {

                        }
                    }
                });

                btnCustomCanCannotNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            dvd.cancel();
                        } catch (Exception e) {

                        }
                    }
                });
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity verifyB4redirection2. Exception: " + e.getMessage().toString(), "", "", "", "");
            }
        } else if (count == 0) {
            //playTone();
            showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_nojobselect));
        }
    }

    private void massOKPchecksNew(boolean ahasPartOne, objCheckedItems[] aobjCheckedItemsAL,
                                  boolean hasStandardRunJob, boolean hasXDelJob, String[] Checklist, final Integer jobflagval) {

        final objCheckedItems[] objCheckedItemsAL = aobjCheckedItemsAL;
        final Boolean hasPartOne = ahasPartOne;
        Boolean listHasCOD = false;
        boolean listHasSpecialCOD = false;
        Intent parentIntent = null;
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        try {
            objGlobalMethods.writeToFile("JobsListGroupActivity massOKPchecks started.", "", "", "", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr", "");

            listHasCOD = JobsHandler.checkMassUpdateHasCODtoCollectOrNot(objCheckedItemsAL, 2, GcmPreferences);
            listHasSpecialCOD = JobsHandler2.checkMassUpdateHasDOCODtoCollectOrNot(objCheckedItemsAL);

            if (!listHasCOD) {
                String[] prefName = new String[]{"JobhasCOD", "mutlipleCODjobs", "objJobInfoBringtoUpdateCODListJsonStr"};
                String[] prefvalue = new String[]{"false", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
            } else {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", "true");
            }

            if (listHasSpecialCOD) {
                String DOListArrJsonStr = "";
                String jobsidxs = "";
                ArrayList<objDOObject[]> DOListArrayList = new ArrayList<>();

                objJobInfo ji = null;
                for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                    ji = objCheckedItemsAL[i].jobinfo;
                    if (JobsHandler.checkforDOCOD(ji)) {
                        jobsidxs += ji.JobsIDX;
                        if (ji.DOList != null && ji.DOList.length > 0) {
                            ji.DOList[0].setJobsIDX(String.valueOf(ji.JobsIDX));
                            ji.DOList[0].setJobNo(ji.JobNo);
                            ji.DOList[0].setCN(ji.ConsignmentNote);
                        }
                        DOListArrayList.add(ji.DOList);
                    }
                }

                if (DOListArrayList.size() > 0) {
                    Gson gson = new Gson();
                    DOListArrJsonStr = gson.toJson(DOListArrayList);
                }
                objGlobalMethods.setGCMPrefs(GcmPreferences, "DOListArrJsonStr", DOListArrJsonStr == "[]" ? "" : DOListArrJsonStr == "" ? "" : DOListArrJsonStr);
            }

            Boolean hasDOinv = JobsHandler.hasDOinv2(objCheckedItemsAL, GcmPreferences);
            boolean hasChecklist = Checklist != null && Checklist.length > 0;
            if (hasChecklist) {
                String checklistObjForMassOKPJsonStr = "";
                checklistObjForMassOKP cl = new checklistObjForMassOKP();
                cl.setChecklist(Checklist);
                cl.setFlag(jobflagval);
                for (int x = 0; x <= objCheckedItemsAL.length - 1; x++) {
                    cl.addJobsIDX(objCheckedItemsAL[x].JobsIDX);
                }
                Gson gson = new Gson();
                checklistObjForMassOKPJsonStr = gson.toJson(cl);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "checklistObjForMassOKP", checklistObjForMassOKPJsonStr);
            } else {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "checklistObjForMassOKP", "");
            }

            int NoOfCannotTakePhotoJobs = 0;
            for (int q = 0; q <= objCheckedItemsAL.length - 1; q++) {
                if (JobsHandler.CannotTakePhoto(objCheckedItemsAL[q].jobinfo, 2)) {
                    NoOfCannotTakePhotoJobs += 1;
                }
                JobsHandler.setJobClientContactIDXforReceiverType(GcmPreferences, objCheckedItemsAL[q].jobinfo, false);
            }

            //AllCannotTakePhoto: 0 == 0 job cannot take photo, 1 == some cannot take photo, 2 == all cannot take photo
            objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", NoOfCannotTakePhotoJobs == 0 ? "0"
                    : (NoOfCannotTakePhotoJobs > 0 && NoOfCannotTakePhotoJobs < objCheckedItemsAL.length) ? "2" : NoOfCannotTakePhotoJobs == objCheckedItemsAL.length ? "3" : "0");

            objGlobalMethods.setGCMPrefs(GcmPreferences, "isStandardRunJob", hasStandardRunJob || hasXDelJob ? "y" : "");


            setDeliveredMassStatusActivity.hasPartOne = hasPartOne;
            if (hasDOinv && !hasChecklist) {
                DOinvCheckActivity.fromPage = "JobsListGroupActivity";
                //DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.currentActivity, ShpmtV2Activity.class);
                ShpmtV2Activity.fromPage = "DOinvCheckActivity";
                parentIntent = new Intent(myApp.currentActivity, DOinvCheckActivity.class);
                if (listHasSpecialCOD) {
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.currentActivity, CODupdate2.class);
                    //CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, ShpmtV2Activity.class);
                    ShpmtV2Activity.fromPage = "CODupdate2";
                }
            } else if (hasDOinv && hasChecklist) {
                //doinv > checklist > okp
                //check done
                DOinvCheckActivity.fromPage = "JobsListGroupActivity";
                DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.currentActivity, CheckListActivity.class);
                CheckListActivity.ActionCode = "DS";
                //CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, ShpmtV2Activity.class);
                CheckListActivity.fromPage = "DOinvCheckActivity";
                ShpmtV2Activity.fromPage = "CheckListActivity";
                parentIntent = new Intent(this, DOinvCheckActivity.class);
                if (listHasSpecialCOD) {
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.currentActivity, CheckListActivity.class);
                    CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, CODupdate2.class);
                    CheckListActivity.ActionCode = "DS";
                    CODupdate2.fromPage = "CheckListActivity";
                    //CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, ShpmtV2Activity.class);
                    ShpmtV2Activity.fromPage = "CODupdate2";
                }
            } else if (!hasDOinv && hasChecklist) {
                CheckListActivity.fromPage = "JobsListGroupActivity";
                //CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, ShpmtV2Activity.class);
                CheckListActivity.ActionCode = "DS";
                ShpmtV2Activity.fromPage = "CheckListActivity";
                parentIntent = new Intent(myApp.currentActivity, CheckListActivity.class);
                if (listHasSpecialCOD) {
                    CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, CODupdate2.class);
                    CODupdate2.fromPage = "CheckListActivity";
                    //CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, ShpmtV2Activity.class);
                    ShpmtV2Activity.fromPage = "CODupdate2";
                }
            } else {
                //parentIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                parentIntent = new Intent(myApp.currentActivity, ShpmtV2Activity.class);
                ShpmtV2Activity.fromPage = "JobsListGroupActivity";
                if (listHasSpecialCOD) {
                    CODupdate2.fromPage = "JobsListGroupActivity";
                    //CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, ShpmtV2Activity.class);
                    ShpmtV2Activity.fromPage = "CODupdate2";
                }

                if (hasStandardRunJob) {
                    parentIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);

                    if (listHasSpecialCOD) {
                        CODupdate2.fromPage = "JobsListGroupActivity";
                        CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    }
                } else {
                    parentIntent = new Intent(myApp.currentActivity, ShpmtV2Activity.class);
                    ShpmtV2Activity.fromPage = "JobsListGroupActivity";
                    if (listHasSpecialCOD) {
                        CODupdate2.fromPage = "JobsListGroupActivity";
                        //CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                        CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, ShpmtV2Activity.class);
                        ShpmtV2Activity.fromPage = "CODupdate2";
                    }
                }
            }

            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity massOKPchecksNew. Exception: " + e.getMessage().toString(), "", "", "", "");
        }

    }

    private void massOKPchecks2(boolean ahasPartOne, objCheckedItems[] aobjCheckedItemsAL,
                                boolean hasStandardRunJob, boolean hasXDelJob, String[] Checklist, final Integer jobflagval) {
        try {
            final objCheckedItems[] objCheckedItemsAL = aobjCheckedItemsAL;
            final Boolean hasPartOne = ahasPartOne;
            Boolean listHasCOD = false;
            boolean listHasSpecialCOD = false;
            Intent parentIntent = null;
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            try {
                objGlobalMethods.writeToFile("JobsListGroupActivity massOKPchecks started.", "", "", "", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr", "");

                listHasCOD = JobsHandler.checkMassUpdateHasCODtoCollectOrNot(objCheckedItemsAL, 2, GcmPreferences);
                listHasSpecialCOD = JobsHandler2.checkMassUpdateHasDOCODtoCollectOrNot(objCheckedItemsAL);

                if (!listHasCOD) {
                    String[] prefName = new String[]{"JobhasCOD", "mutlipleCODjobs", "objJobInfoBringtoUpdateCODListJsonStr"};
                    String[] prefvalue = new String[]{"false", "", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                } else {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", "true");
                }

                if (listHasSpecialCOD) {
                    String DOListArrJsonStr = "";
                    String jobsidxs = "";
                    ArrayList<objDOObject[]> DOListArrayList = new ArrayList<>();

                    objJobInfo ji = null;
                    for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                        ji = objCheckedItemsAL[i].jobinfo;
                        if (JobsHandler.checkforDOCOD(ji)) {
                            jobsidxs += ji.JobsIDX;
                            if (ji.DOList != null && ji.DOList.length > 0) {
                                ji.DOList[0].setJobsIDX(String.valueOf(ji.JobsIDX));
                                ji.DOList[0].setJobNo(ji.JobNo);
                                ji.DOList[0].setCN(ji.ConsignmentNote);
                            }
                            DOListArrayList.add(ji.DOList);
                        }
                    }

                    if (DOListArrayList.size() > 0) {
                        Gson gson = new Gson();
                        DOListArrJsonStr = gson.toJson(DOListArrayList);
                    }
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "DOListArrJsonStr", DOListArrJsonStr == "[]" ? "" : DOListArrJsonStr == "" ? "" : DOListArrJsonStr);
                }

                Boolean hasDOinv = JobsHandler.hasDOinv2(objCheckedItemsAL, GcmPreferences);
                boolean hasChecklist = Checklist != null && Checklist.length > 0;
                if (hasChecklist) {
                    String checklistObjForMassOKPJsonStr = "";
                    checklistObjForMassOKP cl = new checklistObjForMassOKP();
                    cl.setChecklist(Checklist);
                    cl.setFlag(jobflagval);
                    for (int x = 0; x <= objCheckedItemsAL.length - 1; x++) {
                        cl.addJobsIDX(objCheckedItemsAL[x].JobsIDX);
                    }
                    Gson gson = new Gson();
                    checklistObjForMassOKPJsonStr = gson.toJson(cl);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "checklistObjForMassOKP", checklistObjForMassOKPJsonStr);
                } else {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "checklistObjForMassOKP", "");
                }

                int NoOfCannotTakePhotoJobs = 0;
                for (int q = 0; q <= objCheckedItemsAL.length - 1; q++) {
                    if (JobsHandler.CannotTakePhoto(objCheckedItemsAL[q].jobinfo, 2)) {
                        NoOfCannotTakePhotoJobs += 1;
                    }
                    JobsHandler.setJobClientContactIDXforReceiverType(GcmPreferences, objCheckedItemsAL[q].jobinfo, false);
                }

                //AllCannotTakePhoto: 0 == 0 job cannot take photo, 1 == some cannot take photo, 2 == all cannot take photo
                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", NoOfCannotTakePhotoJobs == 0 ? "0"
                        : (NoOfCannotTakePhotoJobs > 0 && NoOfCannotTakePhotoJobs < objCheckedItemsAL.length) ? "2" : NoOfCannotTakePhotoJobs == objCheckedItemsAL.length ? "3" : "0");

                objGlobalMethods.setGCMPrefs(GcmPreferences, "isStandardRunJob", hasStandardRunJob || hasXDelJob ? "y" : "");


                setDeliveredMassStatusActivity.hasPartOne = hasPartOne;
                if (hasDOinv && !hasChecklist) {
                    DOinvCheckActivity.fromPage = "JobsListGroupActivity";
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    parentIntent = new Intent(myApp.currentActivity, DOinvCheckActivity.class);
                    if (listHasSpecialCOD) {
                        DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.currentActivity, CODupdate2.class);
                        CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    }
                } else if (hasDOinv && hasChecklist) {
                    //doinv > checklist > okp
                    //check done
                    DOinvCheckActivity.fromPage = "JobsListGroupActivity";
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.currentActivity, CheckListActivity.class);
                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    CheckListActivity.fromPage = "DOinvCheckActivity";
                    parentIntent = new Intent(this, DOinvCheckActivity.class);
                    if (listHasSpecialCOD) {
                        DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.currentActivity, CheckListActivity.class);
                        CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, CODupdate2.class);
                        CheckListActivity.ActionCode = "DS";
                        CODupdate2.fromPage = "CheckListActivity";
                        CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    }
                } else if (!hasDOinv && hasChecklist) {
                    CheckListActivity.fromPage = "JobsListGroupActivity";
                    CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    CheckListActivity.ActionCode = "DS";
                    parentIntent = new Intent(myApp.currentActivity, CheckListActivity.class);
                    if (listHasSpecialCOD) {
                        CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, CODupdate2.class);
                        CODupdate2.fromPage = "CheckListActivity";
                        CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    }
                } else {
                    parentIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    if (listHasSpecialCOD) {
                        CODupdate2.fromPage = "JobsListGroupActivity";
                        CODupdate2.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    }
                }

                MainActivity.uponLogin = 0;
                startActivity(parentIntent);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity massOKPchecks. Exception: " + e.getMessage().toString(), "", "", "", "");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity massOKPchecks2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void verifyB4massUpdateJobStatus(final long aAStatusCodeIDX, final objCheckedItems[] objCheckedItemsAL, int count, final String[] CheckedItemsIDX) {
        final String[] finalCheckedItemsIDX = CheckedItemsIDX;

        if (count > 0) {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);

                if (aAStatusCodeIDX == 49) {
                    builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdate) + " " + count + " " + myApp.getAppContext().getString(R.string.alert_cfmupdatePU));
                } else if (aAStatusCodeIDX == 104) {
                    builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdate) + " " + count + " " + myApp.getAppContext().getString(R.string.alert_cfmupdateDIP));
                }

                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status.
                        String str1 = Arrays.toString(finalCheckedItemsIDX);
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();

                        objGlobalMethods.writeToFile("Mass Update Job(s) to " + aAStatusCodeIDX + ".",
                                "JobsIDXs: " + str1, "", "", "");

                        //massUpdateJobStatusSubmitWithCODchecks0SQLite(aAStatusCodeIDX, objCheckedItemsAL, finalCheckedItemsIDX, GcmPreferences, UpdateJobInfoPreferences);
                        massUpdateJobStatusWithChecks(aAStatusCodeIDX, objCheckedItemsAL, finalCheckedItemsIDX, GcmPreferences, UpdateJobInfoPreferences);

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

            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity verifyB4massUpdateJobStatus. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            }
        } else if (count == 0) {
            //playTone();
            showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_nojobselect));
        }
    }

    private void verifyB4massFailStatus(final String type, int count, final objCheckedItems[] objCheckedItemsAL, final String failtype) {
        if (count > 0) {
            try {
                final int finalCatTypeID = failtype.equals("fp") ? 0 : 2;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);

                if (failtype.equals("fp")) {
                    /*final Location DestinationLocationFP = objGlobalMethods.getDestinationLocation(objCheckedItemsAL[0].jobinfo, 0);
                    final Location gpsLocationFP = objGlobalMethods.getLastKnownLocationForJobUpdate();
                    if (objGlobalMethods.IsOutOfDestinationLocationRange(DestinationLocationFP, gpsLocationFP)) {
                        float distMeter = gpsLocationFP.distanceTo(DestinationLocationFP);
                        objGlobalMethods.writeToFile("Out of range for MASS FP SUCH AS JobsIDX: " + objCheckedItemsAL[0].jobinfo.JobsIDX + ", Destination LatLng: "
                                + DestinationLocationFP.getLatitude() + ", " + DestinationLocationFP.getLongitude() + ", gpsLocation LatLng: "
                                + gpsLocationFP.getLatitude() + ", " + gpsLocationFP.getLongitude() + ", Distane: " + distMeter + "meters.","","","","");

                        builder.setMessage(myApp.getAppContext().getString(R.string.alert_outofrangecfm) + " " +
                                myApp.getAppContext().getString(R.string.alert_cfmupdate) + " " + count + " " + myApp.getAppContext().getString(R.string.alert_cfmupdateFP));
                    } else {
                        builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdate) + " " + count + " " + myApp.getAppContext().getString(R.string.alert_cfmupdateFP));
                    }*/

                    builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdate) + " " + count + " " + myApp.getAppContext().getString(R.string.alert_cfmupdateFP));
                } else if (failtype.equals("fd")) {
                    /*final Location DestinationLocationFD = objGlobalMethods.getDestinationLocation(objCheckedItemsAL[0].jobinfo, 2);
                    final Location gpsLocationFD = objGlobalMethods.getLastKnownLocationForJobUpdate();
                    if (objGlobalMethods.IsOutOfDestinationLocationRange(DestinationLocationFD, gpsLocationFD)) {
                        float distMeter = gpsLocationFD.distanceTo(DestinationLocationFD);
                        objGlobalMethods.writeToFile("Out of range for MASS FD SUCH AS JobsIDX: " + objCheckedItemsAL[0].jobinfo.JobsIDX + ", Destination LatLng: "
                                + DestinationLocationFD.getLatitude() + ", " + DestinationLocationFD.getLongitude() + ", gpsLocation LatLng: "
                                + gpsLocationFD.getLatitude() + ", " + gpsLocationFD.getLongitude() + ", Distane: " + distMeter + "meters.","","","","");
                        builder.setMessage(myApp.getAppContext().getString(R.string.alert_outofrangecfm) + " " +
                                myApp.getAppContext().getString(R.string.alert_cfmupdate) + " " + count + " " + myApp.getAppContext().getString(R.string.alert_cfmupdateFD));
                    } else {
                        builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdate) + " " + count + " " + myApp.getAppContext().getString(R.string.alert_cfmupdateFD));
                    }*/

                    builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdate) + " " + count + " " + myApp.getAppContext().getString(R.string.alert_cfmupdateFD));
                }

                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status.
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        Gson gson = new Gson();
                        String[] CheckedItemsIDX = null;
                        CheckedItemsIDX = new String[objCheckedItemsAL.length];
                        String objCheckedItemsIDXJsonStr = "";

                        for (int k = 0; k <= objCheckedItemsAL.length - 1; k++) {
                            CheckedItemsIDX[k] = objCheckedItemsAL[k].JobsIDX;
                        }
                        objCheckedItemsIDXJsonStr = gson.toJson(CheckedItemsIDX);
                        if (objCheckedItemsIDXJsonStr.equals("[]")) {
                            objCheckedItemsIDXJsonStr = "";
                        }

                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX, objCheckedItemsIDXJsonStr);
                        Intent parentIntent;
                        if (type.equalsIgnoreCase("fail")) {
                            String AllValidTelNums = objGlobalMethods.rtnAllValidTelNumbers(finalCatTypeID, objCheckedItemsAL);

                            if (!AllValidTelNums.isEmpty() && !AllValidTelNums.equals("") && !AllValidTelNums.substring(AllValidTelNums.length() - 1).equals(",")) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "AllValidTelNums", AllValidTelNums);
                            } else {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "AllValidTelNums", "");
                            }

                            objGlobalMethods.setGCMPrefs(GcmPreferences, "ojisPage", "-1");
                            //parentIntent = new Intent(JobsListGroupActivity.this, setFailStatusActivity.class);
                            parentIntent = new Intent(myApp.currentActivity, setFailStatusActivity.class);
                            MainActivity.uponLogin = 0;
                            startActivity(parentIntent);
                        }
                    }
                });

                builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity verifyB4massFailStatus. Exception: " + e.getMessage().toString(), "", "", "", "");
            }
        } else if (count == 0) {
            //playTone();
            showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_nojobselect));
        }
    }

    public void massUpdateJobStatusWithChecks(final long AStatusCodeIDX, final objCheckedItems[] objCheckedItemsAL, String[] CheckedItemsIDX,
                                              final SharedPreferences gcmPrefs, final SharedPreferences UpdateJobInfoPrefs) {
        Date dt = new Date();
        Gson gson = new Gson();
        Long l = dt.getTime();
        final String timestampstr = "/Date(" + l + ")/";
        List<objUpdateCOD_PostData> objUpdateCODPostDataList = null;
        Boolean hasCOD = false;
        Boolean listHasCOD = false;
        String mutlipleCODjobs = "";
        List<objJobInfo> objJobInfoBringtoUpdateCODList = null;
        String objJobInfoBringtoUpdateCODListJsonStr = "";
        Intent parentIntent = null;
        objJobInfo currJobInfo;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        try {
            objGlobalMethods.writeToFile("JobsListGroupActivity massUpdateJobStatusWithChecks started.", "", "", "", "");
            objGlobalMethods.setGCMPrefs(gcmPrefs, "ScannedCodeStrsPrefStr", "");

            final List<objCheckedItems> objCheckedItemsList = objCheckedItemsAL != null && objCheckedItemsAL.length > 0 ? new ArrayList<objCheckedItems>(Arrays.asList(objCheckedItemsAL)) : null;

            if (AStatusCodeIDX == 49) {
                listHasCOD = JobsHandler.checkMassUpdateHasCODtoCollectOrNot(objCheckedItemsAL, 0, gcmPrefs);
            } else {
                listHasCOD = false;
            }

            if (!listHasCOD && (objCheckedItemsList != null && objCheckedItemsList.size() > 0)) {
                progressDoalog = new ProgressDialog(this);
                progressDoalog.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));
                progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDoalog.setMax(objCheckedItemsList.size());
                progressDoalog.setCanceledOnTouchOutside(false);
                progressDoalog.show();

                final Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                final int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);

                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        objCheckedItems objCheckedItemsobj = null;
                        Gson gson0 = new Gson();
                        String RegID = objGlobalMethods.getRegistrationIdOnly();
                        String JobsIDXstr;
                        long JobsIDX;
                        List<Long> JobsIDXList = new ArrayList<>();
                        Long[] JobsIDXsArr = null;
                        String jobsidxs = "";
                        try {
                            for (int i = objCheckedItemsList.size() - 1; i >= 0; i--) {
                                Thread.sleep(200);
                                handle.sendMessage(handle.obtainMessage());

                                objCheckedItemsobj = objCheckedItemsAL[i];
                                /*JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPrefs, RegID, Long.parseLong(objCheckedItemsobj.JobsIDX), AStatusCodeIDX, timestampstr, "", "",
                                        lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");*/
                                JobsIDX = Long.parseLong(objCheckedItemsobj.JobsIDX);
                                if (!JobsIDXList.contains(JobsIDX))
                                    JobsIDXList.add(JobsIDX);

                                //jobsidxs += (objGlobalMethods.IsEmptyString(jobsidxs)) ? String.valueOf(JobsIDX) : "," + String.valueOf(JobsIDX);

                                if (AStatusCodeIDX == 49) {
                                    ////set from pip to pu
                                    ////remove from pip pref
                                    DAO_Jobs.updateJobsCatTypeID(1, Long.parseLong(objCheckedItemsobj.JobsIDX));
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 0);
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 1);
                                } else if (AStatusCodeIDX == 104) {
                                    ////set from pu to dip
                                    ////remove from pu pref
                                    DAO_Jobs.updateJobAddressFromPUtoDIP(2, Long.parseLong(objCheckedItemsobj.JobsIDX), objCheckedItemsobj.JobInfoJsonStr);
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 1);
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 2);
                                    objCheckedItemsobj.jobinfo.setI_have_arrived_timestamp(0);
                                    String hfJobInfoJsonStr = gson0.toJson(objCheckedItemsobj.jobinfo);
                                    hfJobInfoJsonStr = hfJobInfoJsonStr == "[]" || hfJobInfoJsonStr == "" ? "" : hfJobInfoJsonStr;
                                    DAO_Jobs.updateJobInfoPrefStr(Long.parseLong(objCheckedItemsobj.JobsIDX), hfJobInfoJsonStr);
                                }
                                objCheckedItemsList.remove(i);

                                if (i == 0) {
                                    /*if (!objGlobalMethods.IsEmptyString(jobsidxs)) {
                                        objGlobalMethods.writeToFile("Mass update job status.", "JobsIDX: " +
                                                jobsidxs, "Submitted StatusCodeIDX: " + String.valueOf(AStatusCodeIDX), "", "");
                                    }*/
                                    jobsidxs = Arrays.toString(JobsIDXList.toArray());
                                    if (JobsIDXList != null && JobsIDXList.size() > 0) {
                                        objGlobalMethods.writeToFile("Mass update job status.", "JobsIDX: " +
                                                jobsidxs, "Submitted StatusCodeIDX: " + String.valueOf(AStatusCodeIDX), "", "");
                                        JobsIDXsArr = JobsIDXList.toArray(new Long[JobsIDXList.size()]);
                                        JobsHandler.addUpdateJobInfoStatusWithGPSMassQueALPrefs(UpdateJobInfoPreferences, RegID, JobsIDXsArr, AStatusCodeIDX,
                                                timestampstr, "", "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(),
                                                Accuracy, "");
                                    }

                                    String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "mutlipleCODjobs", "objJobInfoBringtoUpdateCODListJsonStr",
                                            "ScannedCodeStrsPrefStr"};
                                    String[] prefvalue = new String[]{"", "", "", "", ""};
                                    objGlobalMethods.setGCMPrefsMass(gcmPrefs, prefName, prefvalue);

                                    Context ctx = myApp.getAppContext();
                                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                                    objGlobalMethods.writeToFile("JobsListGroupActivity massUpdateJobStatusWithChecks mass set " + String.valueOf(AStatusCodeIDX) + " ended without error.", "", "", "", "");

                                    if (AStatusCodeIDX == 49) {
                                        objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, true);
                                    }

                                    progressDoalog.dismiss();
                                    resetBack();
                                }
                            }
                        } catch (Exception e) {
                            progressDoalog.dismiss();
                            objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity massUpdateJobStatusWithChecks progress dialog thread. Exception: " + e.getMessage().toString(), "", "", "", "");
                            JobsHandler.sendErrorEmail("Exception caught at JobsListGroupActivity massUpdateJobStatusWithChecks progress dialog thread. Exception: " + e.getMessage().toString());
                            resetBack();
                        }
                    }
                };
                t.start();
            } else if (objCheckedItemsList != null && objCheckedItemsList.size() > 0) {
                String RegID = objGlobalMethods.getRegistrationIdOnly();
                for (int i = objCheckedItemsList.size() - 1; i >= 0; i--) {
                    currJobInfo = null;
                    objCheckedItems objCheckedItemsobj = objCheckedItemsAL[i];
                    currJobInfo = objCheckedItemsobj.jobinfo;
                    hasCOD = JobsHandler.checkForCODwithCatType(currJobInfo, 0);
                    currJobInfo.setpendingStatusUpdate(49);
                    currJobInfo.setReceiver("");
                    currJobInfo.setFailureReason("");
                    objJobInfoBringtoUpdateCODList = JobsHandler.RTNobjJobInfoBringtoUpdateCODList(objJobInfoBringtoUpdateCODList, currJobInfo);
                    if (hasCOD) {
                        objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, currJobInfo, 0, true, RegID, false, "", "");
                    }
                }

                if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                    objJobInfo[] objJobInfoBringtoUpdateCODAL = objJobInfoBringtoUpdateCODList.toArray(new objJobInfo[objJobInfoBringtoUpdateCODList.size()]);
                    objJobInfoBringtoUpdateCODListJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL);
                    objGlobalMethods.setGCMPrefs(gcmPrefs, "objJobInfoBringtoUpdateCODListJsonStr", objJobInfoBringtoUpdateCODListJsonStr);

                    objUpdateCOD_PostData[] objUpdateCODPostDataAL = objUpdateCODPostDataList.toArray(new objUpdateCOD_PostData[objUpdateCODPostDataList.size()]);
                    mutlipleCODjobs = gson.toJson(objUpdateCODPostDataAL);
                    objGlobalMethods.setGCMPrefs(gcmPrefs, "mutlipleCODjobs", mutlipleCODjobs);

                    //ojisPage == 0 == JobInfoActivity | ojisPage == 1 == JobsListGroupActivity | ojisPage == 2 == setDeliveredStatusActivity | ojisPage == 3 == setDeliveredMassStatusActivity |
                    objGlobalMethods.setGCMPrefs(gcmPrefs, "ojisPage", "1");

                    //Intent intent = new Intent(this, UpdateCOD_List.class);
                    Intent intent = new Intent(myApp.currentActivity, UpdateCOD_List.class);
                    startActivity(intent);
                    finish();
                }
            }

        } catch (Exception e) {
            objGlobalMethods.setGCMPrefs(gcmPrefs, "ScannedCodeStrsPrefStr", "");
            pd.dismiss();
            objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity massUpdateJobStatusWithChecks. Exception: " + e.toString(), "", "", "", "");

            if (!objGlobalMethods.getGCMPrefs(gcmPrefs, "isFromDashboardSub").isEmpty()) {
                objGlobalMethods.setGCMPrefs(gcmPrefs, "isFromDashboardSub", "");
                objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                //parentIntent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
            } else {
                objGlobalMethods.setGCMPrefs(gcmPrefs, "isFromDashboardSub", "");
                objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                //parentIntent = new Intent(JobsListGroupActivity.this, AddressListNewActivity.class);
                parentIntent = new Intent(myApp.currentActivity, AddressListNewActivity.class);
            }

            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            JobsListGroupActivity.this.finish();
        }

    }

    //=========================================sqlite=================================================
    private void loadListSQLiteNew(List<dbObjJobs> dbObjJobsAL) {
        SharedPreferences glsp = objGlobalMethods.getGrouplistSP();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        try {
            Boolean isPartOne = false;
            objJobInfo ji;
            String JobInfoJsonStr = "";
            headerAl = new ArrayList<String>();
            itemAL = new ArrayList();
            listDataHeader = new ArrayList<String>();
            listDataChildSortedSQLite2 = new HashMap<String, List<JobListGroupSortedSQLite_PostData>>();
            Gson gson = new Gson();
            boolean isStandardRunJob = false;
            boolean isXDelJob = false;
            String[] ContactlessCallPromptCheck = null;
            String ContactlessCallPromptMsg = "";
            String ContactlessCanProceed = "";
            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            Boolean isDelivery = catType == 2 || catType == 3 || catType == 5 || catType == 6 ? true : false;
            JobListGroupSortedSQLite_PostData postdata;
            int headerALselectedIDX = 0;
            Boolean CbIsEnabled = false;
            if (catType == 0 || catType == 1 || catType == 2) {
                //CbIsEnabled = true;
                if ((objGlobalMethods.IsDefaultVRPpage()) && (catType == 0 || catType == 2)) {
                    String enableJobUpdatesPref = objGlobalMethods.getGCMPrefs(GcmPreferences, "enableJobUpdates");
                    CbIsEnabled = enableJobUpdatesPref.equals("") || enableJobUpdatesPref.equals("true") ? true : false;
                } else {
                    CbIsEnabled = true;
                }
            }
            /*if (catType == 1 || catType == 2) {
                CbIsEnabled = true;
            }*/
            //new code with sqlite
            if (dbObjJobsAL != null) {
                for (int i = 0; i <= dbObjJobsAL.size() - 1; i++) {
                    dbObjJobs obj = dbObjJobsAL.get(i);
                    ji = null;
                    JobInfoJsonStr = "";
                    JobInfoJsonStr = obj.JobInfoJsonStr;
                    if (JobInfoJsonStr != "") {
                        ji = gson.fromJson(JobInfoJsonStr, objJobInfo.class);
                    }

                    /*//to test for different CLientIDX, comment off after use
                    if (ji.JobsIDX == 5828415 || ji.JobsIDX == 5884684 || ji.JobsIDX == 3765749 || ji.JobsIDX == 3660219 || ji.JobsIDX == 5879125 || ji.JobsIDX == 3365101) {
                        ji.ClientIDX = 1; // as circles
                    } else if (ji.JobsIDX == 3326670 || ji.JobsIDX == 3510361 || ji.JobsIDX == 3715656 || ji.JobsIDX == 5547552 || ji.JobsIDX == 5290804 || ji.JobsIDX == 5430628
                            || ji.JobsIDX == 5749920 || ji.JobsIDX == 5536330 || ji.JobsIDX == 4777075 || ji.JobsIDX == 3805620 || ji.JobsIDX == 4777135 || ji.JobsIDX == 4777137) {
                        ji.ClientIDX = 2; // as FS
                    } else if (ji.JobsIDX == 3637395 || ji.JobsIDX == 3714537 || ji.JobsIDX == 5331417) {
                        ji.ClientIDX = 3; // as M1
                    } else if (ji.JobsIDX == 5547553) {
                        ji.ClientIDX = 4; // as grid
                    } else if (ji.JobsIDX == 5547959) {
                        ji.ClientIDX = 5; // as zero1
                    } else if (ji.JobsIDX == 3329621) {
                        ji.ClientIDX = 6; // as alps
                    } else if (ji.JobsIDX == 5767341) {
                        ji.ClientIDX = 7; //as doc from A
                    } else if (ji.JobsIDX == 5767741) {
                        ji.ClientIDX = 8; // as doc from B
                    } else if (ji.JobsIDX == 5879085 || ji.JobsIDX == 5879070) {
                        ji.ClientIDX = 9; // as doc from C
                    }
                    //to test for different CLientIDX, comment off after use*/

                    if (!headerAl.contains(obj.AddressWithoutPostal)) {
                        headerAl.add(obj.AddressWithoutPostal);
                        subGroupItemSqliteAL = new ArrayList<JobListGroupSortedSQLite_PostData>();
                        postdata = new JobListGroupSortedSQLite_PostData(obj.ExpTypeDesc, obj.CompanyName, obj.ContactName, obj.AddressWithoutPostal, obj.JobInfoJsonStr, (obj.CNNo == null || obj.CNNo.isEmpty()) ? obj.JobNo : obj.CNNo, obj.PartDesc,
                                String.valueOf(obj.JobsIDX), false, CbIsEnabled, (obj.FLAG & 8) == 8 ? true : false, ((obj.FLAG & 512) == 512) && !objGlobalMethods.IsEmptyString(ji.PIN) ? true : false, (obj.FLAG & 1024) == 1024 ? true : false,
                                obj.FLAG, obj.IsPartOne, obj.IsDunning, obj.TimingTxt, obj.PCS, ji, obj.HasDOCOD, obj.HasTradeIn,
                                isDelivery, obj.NetsVisaTi, obj.isPickAndSendCount, obj.JobChangesCount, subGroupItemSqliteAL.size(), ji.FLAG2);
                        postdata.setCatTypeID(catType);
                        postdata.setClientIDX(ji.ClientIDX);
                        postdata.setPosition(subGroupItemSqliteAL.size());
                        postdata.setHASLUNCHTIME(obj.HASLUNCHTIME);
                        subGroupItemSqliteAL.add(postdata);
                        itemAL.add(subGroupItemSqliteAL);
                    } else {
                        headerALselectedIDX = headerAl.indexOf(obj.AddressWithoutPostal);
                        subGroupItemSqliteAL = (List<JobListGroupSortedSQLite_PostData>) itemAL.get(headerALselectedIDX);
                        postdata = new JobListGroupSortedSQLite_PostData(obj.ExpTypeDesc, obj.CompanyName, obj.ContactName, obj.AddressWithoutPostal, obj.JobInfoJsonStr, (obj.CNNo == null || obj.CNNo.isEmpty()) ? obj.JobNo : obj.CNNo, obj.PartDesc,
                                String.valueOf(obj.JobsIDX), false, CbIsEnabled, (obj.FLAG & 8) == 8 ? true : false, ((obj.FLAG & 512) == 512) && !objGlobalMethods.IsEmptyString(ji.PIN) ? true : false, (obj.FLAG & 1024) == 1024 ? true : false,
                                obj.FLAG, obj.IsPartOne, obj.IsDunning, obj.TimingTxt, obj.PCS, ji, obj.HasDOCOD, obj.HasTradeIn,
                                isDelivery, obj.NetsVisaTi, obj.isPickAndSendCount, obj.JobChangesCount, subGroupItemSqliteAL.size(), ji.FLAG2);
                        postdata.setCatTypeID(catType);
                        postdata.setClientIDX(ji.ClientIDX);
                        postdata.setPosition(subGroupItemSqliteAL.size());
                        postdata.setHASLUNCHTIME(obj.HASLUNCHTIME);
                        subGroupItemSqliteAL.add(postdata);
                        itemAL.set(headerALselectedIDX, subGroupItemSqliteAL);
                    }
                }

                String objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
                String objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);

                String mChildCheckStatesPrefStr = objGlobalMethods.getGrouplistSP_KeyValue(glsp, "mChildCheckStatesPrefStr");
                HashMap<Integer, boolean[]> mChildCheckStates = new HashMap<Integer, boolean[]>();
                if (!objGlobalMethods.IsEmptyString(mChildCheckStatesPrefStr) && !mChildCheckStatesPrefStr.equalsIgnoreCase("[]")) {
                    mChildCheckStates = gson.fromJson(mChildCheckStatesPrefStr, new TypeToken<HashMap<Integer, boolean[]>>() {
                    }.getType());
                }

                if (!objGlobalMethods.IsEmptyString(objCheckedItemsIDXJsonStr) && !objGlobalMethods.IsEmptyString(objCheckedItemsJsonStr)) {
                    HashMap<String, List<JobListGroupSortedSQLite_PostData>> rePositionedGroupItems = new HashMap<String, List<JobListGroupSortedSQLite_PostData>>();
                    for (int i = 0; i <= headerAl.size() - 1; i++) {
                        rePositionedGroupItems.put(headerAl.get(i), (List<JobListGroupSortedSQLite_PostData>) itemAL.get(i));
                    }

                    String jobSelectionInfoJsonStr = objGlobalMethods.getGrouplistSP_KeyValue(glsp, "jobSelectionInfoJsonStr");
                    JobListGroupSortedSQLiteNew_PostItemAdapter.j1 = objGlobalMethods.IsEmptyString(jobSelectionInfoJsonStr) ? null :
                            gson.fromJson(jobSelectionInfoJsonStr, jobSelectionInfo.class);

                    if (JobListGroupSortedSQLiteNew_PostItemAdapter.j1 != null) {
                        ArrayList<Long> InvalidJobIDXList = JobListGroupSortedSQLiteNew_PostItemAdapter.j1.InvalidJobIDXList;
                        boolean allowedSelection = false;
                        String headervalue = "";
                        String key = "";
                        List<JobListGroupSortedSQLite_PostData> rePosItems = new ArrayList<JobListGroupSortedSQLite_PostData>();
                        List<JobListGroupSortedSQLite_PostData> oriItems = new ArrayList<JobListGroupSortedSQLite_PostData>();
                        JobListGroupSortedSQLite_PostData rePosItem = null;
                        String oriJobsIDX = "";
                        boolean exist = false;
                        Iterator<HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>>> itr = null;
                        ji = null;

                        if (InvalidJobIDXList != null && InvalidJobIDXList.size() > 0) {
                            for (int i = 0; i <= headerAl.size() - 1; i++) {
                                headervalue = headerAl.get(i);
                                itr = rePositionedGroupItems.entrySet().iterator();

                                outer:
                                while (itr.hasNext()) {
                                    HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>> entry = itr.next();
                                    key = entry.getKey();
                                    if (key.equalsIgnoreCase(headervalue)) {
                                        boolean[] getCheck = mChildCheckStates.get(i);
                                        oriItems = (List<JobListGroupSortedSQLite_PostData>) itemAL.get(i);
                                        for (int x = 0; x <= oriItems.size() - 1; x++) {
                                            ji = oriItems.get(x).ji;
                                            oriItems.get(x).setSavedValuedNew(!InvalidJobIDXList.contains(ji.JobsIDX), getCheck[x]);
                                        }
                                    }
                                }
                            }
                        } else {
                            for (int i = 0; i <= headerAl.size() - 1; i++) {
                                headervalue = headerAl.get(i);
                                itr = rePositionedGroupItems.entrySet().iterator();
                                outer:
                                while (itr.hasNext()) {
                                    HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>> entry = itr.next();
                                    key = entry.getKey();
                                    if (key.equalsIgnoreCase(headervalue)) {
                                        boolean[] getCheck = mChildCheckStates.get(i);
                                        oriItems = (List<JobListGroupSortedSQLite_PostData>) itemAL.get(i);
                                        for (int x = 0; x <= oriItems.size() - 1; x++) {
                                            oriItems.get(x).setSavedValuedNew(false, getCheck[x]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    JobListGroupSortedSQLiteNew_PostItemAdapter.j1 = null;
                    if (mChildCheckStates != null) {
                        for (int i = 0; i <= mChildCheckStates.size() -1; i++) {
                            for (int m = 0; m <= mChildCheckStates.get(i).length -1; m++) {
                                mChildCheckStates.get(i)[m] = false;
                            }
                        }
                    }
                }

                for (int i = 0; i <= headerAl.size() - 1; i++) {
                    listDataHeader.add(headerAl.get(i));
                    listDataChildSortedSQLite2.put(headerAl.get(i), (List<JobListGroupSortedSQLite_PostData>) itemAL.get(i));
                }

                ////expandableSortedSQLiteListAdapter = new JobListGroupSortedSQLite_PostItemAdapter(this, listDataHeader, listDataChildSortedSQLite2);
                //expandableSortedSQLiteListAdapter = new JobListGroupSortedSQLite_PostItemAdapter(myApp.getAppContext(), listDataHeader, listDataChildSortedSQLite2);
                expandableSortedSQLiteListAdapter = new JobListGroupSortedSQLiteNew_PostItemAdapter(myApp.getAppContext(), listDataHeader, listDataChildSortedSQLite2);
                expandableSortedSQLiteListAdapter.setmChildCheckStates(mChildCheckStates);
                expandableSortedSQLiteListAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

                // setting list adapter
                expListView.setAdapter(expandableSortedSQLiteListAdapter);
                setGroupIndicatorToRight();
                expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
            } else {
                showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata) + "\n" + "JobInfoAL is Null.");
            }
            //new code with sqlite

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity loadListSQLiteNew. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata) + "\n" + e.getMessage().toString());
        } finally {
            EnableDisableFailOKPbuttonsNew(JobListGroupSortedSQLiteNew_PostItemAdapter.j1);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    expListView.clearFocus();
                    txtInput.clearFocus();
                    txtInput.setText("");
                    txtInput.requestFocus();
                    promptReqMPD();
                    objGlobalMethods.showNewPIPdialog(GcmPreferences);
                }
            }, 100);
        }
    }

    private void loadListSQLite2(List<dbObjJobs> dbObjJobsAL) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        try {
            Boolean isPartOne = false;
            objJobInfo ji;
            String JobInfoJsonStr = "";
            headerAl = new ArrayList<String>();
            itemAL = new ArrayList();
            listDataHeader = new ArrayList<String>();
            listDataChildSortedSQLite2 = new HashMap<String, List<JobListGroupSortedSQLite_PostData>>();
            Gson gson = new Gson();
            boolean isStandardRunJob = false;
            boolean isXDelJob = false;
            String[] ContactlessCallPromptCheck = null;
            String ContactlessCallPromptMsg = "";
            String ContactlessCanProceed = "";
            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            Boolean isDelivery = catType == 2 || catType == 3 || catType == 5 || catType == 6 ? true : false;
            JobListGroupSortedSQLite_PostData postdata;
            int headerALselectedIDX = 0;
            Boolean CbIsEnabled = false;
            //JobListGroupSortedSQLite_PostItemAdapter dummyAdapter = new JobListGroupSortedSQLite_PostItemAdapter(this, null, null);
            JobListGroupSortedSQLite_PostItemAdapter dummyAdapter = new JobListGroupSortedSQLite_PostItemAdapter(myApp.getAppContext(), null, null);
            if (catType == 0 || catType == 1 || catType == 2) {
                //CbIsEnabled = true;
                if ((objGlobalMethods.IsDefaultVRPpage()) && (catType == 0 || catType == 2)) {
                    String enableJobUpdatesPref = objGlobalMethods.getGCMPrefs(GcmPreferences, "enableJobUpdates");
                    CbIsEnabled = enableJobUpdatesPref.equals("") || enableJobUpdatesPref.equals("true") ? true : false;
                } else {
                    CbIsEnabled = true;
                }
            }
            /*if (catType == 1 || catType == 2) {
                CbIsEnabled = true;
            }*/
            //new code with sqlite
            if (dbObjJobsAL != null) {
                for (int i = 0; i <= dbObjJobsAL.size() - 1; i++) {
                    dbObjJobs obj = dbObjJobsAL.get(i);
                    ji = null;
                    JobInfoJsonStr = "";
                    JobInfoJsonStr = obj.JobInfoJsonStr;
                    if (JobInfoJsonStr != "") {
                        ji = gson.fromJson(JobInfoJsonStr, objJobInfo.class);
                    }

                    /*//to test for different CLientIDX, comment off after use
                    if (ji.JobsIDX == 5828415 || ji.JobsIDX == 5884684 || ji.JobsIDX == 3765749 || ji.JobsIDX == 3660219 || ji.JobsIDX == 5879125 || ji.JobsIDX == 3365101) {
                        ji.ClientIDX = 1; // as circles
                    } else if (ji.JobsIDX == 3326670 || ji.JobsIDX == 3510361 || ji.JobsIDX == 3715656 || ji.JobsIDX == 5547552 || ji.JobsIDX == 5290804 || ji.JobsIDX == 5430628
                            || ji.JobsIDX == 5749920 || ji.JobsIDX == 5536330 || ji.JobsIDX == 4777075 || ji.JobsIDX == 3805620 || ji.JobsIDX == 4777135 || ji.JobsIDX == 4777137) {
                        ji.ClientIDX = 2; // as FS
                    } else if (ji.JobsIDX == 3637395 || ji.JobsIDX == 3714537 || ji.JobsIDX == 5331417) {
                        ji.ClientIDX = 3; // as M1
                    } else if (ji.JobsIDX == 5547553) {
                        ji.ClientIDX = 4; // as grid
                    } else if (ji.JobsIDX == 5547959) {
                        ji.ClientIDX = 5; // as zero1
                    } else if (ji.JobsIDX == 3329621) {
                        ji.ClientIDX = 6; // as alps
                    } else if (ji.JobsIDX == 5767341) {
                        ji.ClientIDX = 7; //as doc from A
                    } else if (ji.JobsIDX == 5767741) {
                        ji.ClientIDX = 8; // as doc from B
                    } else if (ji.JobsIDX == 5879085 || ji.JobsIDX == 5879070) {
                        ji.ClientIDX = 9; // as doc from C
                    }
                    //to test for different CLientIDX, comment off after use*/

                    if (!headerAl.contains(obj.AddressWithoutPostal)) {
                        headerAl.add(obj.AddressWithoutPostal);
                        subGroupItemSqliteAL = new ArrayList<JobListGroupSortedSQLite_PostData>();
                        postdata = new JobListGroupSortedSQLite_PostData(obj.ExpTypeDesc, obj.CompanyName, obj.ContactName, obj.AddressWithoutPostal, obj.JobInfoJsonStr, (obj.CNNo == null || obj.CNNo.isEmpty()) ? obj.JobNo : obj.CNNo, obj.PartDesc,
                                String.valueOf(obj.JobsIDX), false, CbIsEnabled, (obj.FLAG & 8) == 8 ? true : false, (obj.FLAG & 512) == 512 ? true : false, (obj.FLAG & 1024) == 1024 ? true : false,
                                obj.FLAG, obj.IsPartOne, obj.IsDunning, obj.TimingTxt, obj.PCS, ji, obj.HasDOCOD, obj.HasTradeIn,
                                isDelivery, obj.NetsVisaTi, obj.isPickAndSendCount, obj.JobChangesCount, subGroupItemSqliteAL.size(), ji.FLAG2);
                        postdata.setCatTypeID(catType);
                        postdata.setClientIDX(ji.ClientIDX);
                        postdata.setPosition(subGroupItemSqliteAL.size());
                        postdata.setHASLUNCHTIME(obj.HASLUNCHTIME);
                        subGroupItemSqliteAL.add(postdata);
                        itemAL.add(subGroupItemSqliteAL);
                    } else {
                        headerALselectedIDX = headerAl.indexOf(obj.AddressWithoutPostal);
                        subGroupItemSqliteAL = (List<JobListGroupSortedSQLite_PostData>) itemAL.get(headerALselectedIDX);
                        postdata = new JobListGroupSortedSQLite_PostData(obj.ExpTypeDesc, obj.CompanyName, obj.ContactName, obj.AddressWithoutPostal, obj.JobInfoJsonStr, (obj.CNNo == null || obj.CNNo.isEmpty()) ? obj.JobNo : obj.CNNo, obj.PartDesc,
                                String.valueOf(obj.JobsIDX), false, CbIsEnabled, (obj.FLAG & 8) == 8 ? true : false, (obj.FLAG & 512) == 512 ? true : false, (obj.FLAG & 1024) == 1024 ? true : false,
                                obj.FLAG, obj.IsPartOne, obj.IsDunning, obj.TimingTxt, obj.PCS, ji, obj.HasDOCOD, obj.HasTradeIn,
                                isDelivery, obj.NetsVisaTi, obj.isPickAndSendCount, obj.JobChangesCount, subGroupItemSqliteAL.size(), ji.FLAG2);
                        postdata.setCatTypeID(catType);
                        postdata.setClientIDX(ji.ClientIDX);
                        postdata.setPosition(subGroupItemSqliteAL.size());
                        postdata.setHASLUNCHTIME(obj.HASLUNCHTIME);
                        subGroupItemSqliteAL.add(postdata);
                        itemAL.set(headerALselectedIDX, subGroupItemSqliteAL);
                    }
                }

                String objMultiSelectInitialCriteriaJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objMultiSelectInitialCriteriaJsonStr");
                JobListGroupSortedSQLite_PostItemAdapter.criteria = objGlobalMethods.IsEmptyString(objMultiSelectInitialCriteriaJsonStr) ? null :
                        gson.fromJson(objMultiSelectInitialCriteriaJsonStr, objMultiSelectInitialCriteria.class);

                HashMap<String, List<JobListGroupSortedSQLite_PostData>> rePositionedGroupItems = JobsHandler.getRePositionedGroupItems(GcmPreferences);

                String headervalue = "";
                String key = "";
                List<JobListGroupSortedSQLite_PostData> rePosItems = new ArrayList<JobListGroupSortedSQLite_PostData>();
                List<JobListGroupSortedSQLite_PostData> oriItems = new ArrayList<JobListGroupSortedSQLite_PostData>();
                JobListGroupSortedSQLite_PostData rePosItem = null;
                String oriJobsIDX = "";
                boolean exist = false;
                int countUp = 0;
                int countdown = 0;
                if (rePositionedGroupItems != null && rePositionedGroupItems.size() > 0) {
                    Iterator<HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>>> itr = null;
                    for (int i = 0; i <= headerAl.size() - 1; i++) {
                        headervalue = headerAl.get(i);
                        itr = rePositionedGroupItems.entrySet().iterator();
                        outer:
                        while (itr.hasNext()) {
                            HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>> entry = itr.next();
                            key = entry.getKey();
                            if (key.equalsIgnoreCase(headervalue)) {
                                rePosItems = entry.getValue();
                                oriItems = (List<JobListGroupSortedSQLite_PostData>) itemAL.get(i);
                                for (int x = 0; x <= oriItems.size() - 1; x++) {
                                    oriJobsIDX = oriItems.get(x).JobsIDX;
                                    for (int v = 0; v <= rePosItems.size() - 1; v++) {
                                        rePosItem = rePosItems.get(v);
                                        if (objGlobalMethods.IsEqualString(rePosItem.JobsIDX, oriJobsIDX)) {
                                            exist = true;
                                            break;
                                        }
                                    }

                                    if (exist) {
                                        oriItems.get(x).setSavedValued(rePosItem.position, rePosItem.oriPos, rePosItem.allowedSelection, rePosItem.IsSelected, rePosItem.CbIsEnabled);
                                    } else {
                                        if (dummyAdapter.hasRejectedCriteria2(oriItems.get(x).ji, 0)) {
                                            oriItems.get(x).setSavedValued(rePosItems.size() - countdown, rePosItems.size() - countdown, false, false, true);
                                            countdown++;
                                        } else {
                                            countUp++;
                                            oriItems.get(x).setSavedValued(rePosItems.size() - 1 + countUp, rePosItems.size() - 1 + countUp, true, false, true);
                                        }
                                    }
                                }
                                //rearrange items
                                Collections.sort((List<JobListGroupSortedSQLite_PostData>) itemAL.get(i));
                            }
                        }
                    }
                }

                for (int i = 0; i <= headerAl.size() - 1; i++) {
                    listDataHeader.add(headerAl.get(i));
                    listDataChildSortedSQLite2.put(headerAl.get(i), (List<JobListGroupSortedSQLite_PostData>) itemAL.get(i));
                }

                ////expandableSortedSQLiteListAdapter = new JobListGroupSortedSQLite_PostItemAdapter(this, listDataHeader, listDataChildSortedSQLite2);
                //expandableSortedSQLiteListAdapter = new JobListGroupSortedSQLite_PostItemAdapter(myApp.getAppContext(), listDataHeader, listDataChildSortedSQLite2);
                expandableSortedSQLiteListAdapter = new JobListGroupSortedSQLiteNew_PostItemAdapter(myApp.getAppContext(), listDataHeader, listDataChildSortedSQLite2);
                expandableSortedSQLiteListAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

                String mChildCheckStates2PrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "mChildCheckStates2PrefStr");
                if (!objGlobalMethods.IsEmptyString(mChildCheckStates2PrefStr) && !mChildCheckStates2PrefStr.equalsIgnoreCase("[]")) {
                    expandableSortedSQLiteListAdapter.mChildCheckStates2 = gson.fromJson(mChildCheckStates2PrefStr, new TypeToken<HashMap<Integer, objListItemPosChecked[]>>() {
                    }.getType());
                }

                // setting list adapter
                expListView.setAdapter(expandableSortedSQLiteListAdapter);
                setGroupIndicatorToRight();
                expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                dummyAdapter = null;
            } else {
                showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata) + "\n" + "JobInfoAL is Null.");
            }
            //new code with sqlite

        } catch (Exception e) {
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata) + "\n" + e.getMessage().toString());
        } finally {
            EnableDisableFailOKPbuttons();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    expListView.clearFocus();
                    txtInput.clearFocus();
                    txtInput.setText("");
                    txtInput.requestFocus();
                    promptReqMPD();
                    objGlobalMethods.showNewPIPdialog(GcmPreferences);
                }
            }, 100);
        }
    }

    public void scrollToTopOfGroup(int groupIDX, List<String> DataHeader, HashMap<String, List<JobListGroupSortedSQLite_PostData>> DataChild2) {
        try {
            int skippedCount = (groupIDX == 0) ? 1 : getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
            final int askippedCount = skippedCount > 0 ? skippedCount - 1 : skippedCount;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    expListView.setSelection(askippedCount);
                }
            }, 100);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity scrollToTop. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void EnableDisableFailOKPbuttonsNew(jobSelectionInfo j1) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            SharedPreferences jobsp = objGlobalMethods.getJobInfoSP();
            Integer CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);

            if (CatTypeID == 2) {
                if (j1 == null) {
                    String jobSelectionInfoJsonStr = objGlobalMethods.getJobInfoSP_KeyValue(jobsp, "jobSelectionInfoJsonStr");
                    Gson gson = new Gson();
                    j1 = objGlobalMethods.IsEmptyString(jobSelectionInfoJsonStr) ? null :
                            gson.fromJson(jobSelectionInfoJsonStr, jobSelectionInfo.class);
                }

                boolean hasValidSelection = j1 != null && j1.SelectedValidJobIDXList != null && j1.SelectedValidJobIDXList.size() > 0;
                boolean hasInvalidSelection = j1 != null && j1.SelectedInvalidJobIDXList != null && j1.SelectedInvalidJobIDXList.size() > 0;
                boolean hasSelection = hasValidSelection && !hasInvalidSelection;

                ctrlSettings.enableDisableFailButtonWithStyles(hasValidSelection || hasInvalidSelection, btnFail);
                ctrlSettings.enableDisableSubmitButtonWithStyles(hasSelection, btnOKP);
                btnOKP.setVisibility(hasInvalidSelection ? View.GONE : View.VISIBLE);
                btnOKPdummy.setVisibility(hasInvalidSelection ? View.VISIBLE : View.GONE);
                Context ctx = myApp.getAppContext();

                btnOKP.setText(j1 != null && j1.HAS_CHECKLIST && hasSelection ?
                        ctx.getString(R.string.buttontext_checklist) : ctx.getString(R.string.action_massOKP));
                btnOKPdummy.setText(j1 != null && j1.HAS_CHECKLIST && hasSelection ?
                        ctx.getString(R.string.buttontext_checklist) : ctx.getString(R.string.action_massOKP));
            } else {
                btnOKPdummy.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity EnableDisableFailOKPbuttonsNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void EnableDisableFailOKPbuttons() {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            Integer CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);

            if (CatTypeID == 2) {
                if (JobListGroupSortedSQLite_PostItemAdapter.criteria == null) {
                    if (GcmPreferences == null) {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                    }
                    String objMultiSelectInitialCriteriaJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objMultiSelectInitialCriteriaJsonStr");
                    Gson gson = new Gson();
                    JobListGroupSortedSQLite_PostItemAdapter.criteria = objGlobalMethods.IsEmptyString(objMultiSelectInitialCriteriaJsonStr) ? null :
                            gson.fromJson(objMultiSelectInitialCriteriaJsonStr, objMultiSelectInitialCriteria.class);
                }
                boolean ahasNotAllowedSelected = false;
                if (JobListGroupSortedSQLite_PostItemAdapter.criteria != null) {
                    ahasNotAllowedSelected = JobListGroupSortedSQLite_PostItemAdapter.criteria.NotAllowedSelectionCount > 0 ? true : false;
                }
                String objCheckedItemsJsonStr = "";
                objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
                boolean hasSelection = !objGlobalMethods.IsEmptyString(objCheckedItemsJsonStr);


                ctrlSettings.enableDisableFailButtonWithStyles(hasSelection ? true : false, btnFail);
                ctrlSettings.enableDisableSubmitButtonWithStyles(hasSelection ? true : false, btnOKP);
                btnOKP.setVisibility(ahasNotAllowedSelected ? View.GONE : View.VISIBLE);
                btnOKPdummy.setVisibility(ahasNotAllowedSelected ? View.VISIBLE : View.GONE);
                Context ctx = myApp.getAppContext();

                btnOKP.setText(JobListGroupSortedSQLite_PostItemAdapter.criteria != null && JobListGroupSortedSQLite_PostItemAdapter.criteria.HasCheckList() && hasSelection ?
                        ctx.getString(R.string.buttontext_checklist) : ctx.getString(R.string.action_massOKP));
                btnOKPdummy.setText(JobListGroupSortedSQLite_PostItemAdapter.criteria != null && JobListGroupSortedSQLite_PostItemAdapter.criteria.HasCheckList() && hasSelection ?
                        ctx.getString(R.string.buttontext_checklist) : ctx.getString(R.string.action_massOKP));
            } else {
                btnOKPdummy.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity EnableDisableFailOKPbuttons. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void selectJobSQLite2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String objMultiSelectInitialCriteriaJsonStr = "";
        String c = "";
        String objCheckedItemsJsonStr = "";
        boolean firstSelected = false;
        boolean jobFound = false;
        boolean canSelect = true;
        String cr = "";
        Integer CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
        Boolean DenyJobFoundSelection = false;
        Boolean isSecurityBag = false;
        List<JobListGroupSortedSQLite_PostData> clist = null;
        JobListGroupSortedSQLite_PostData cjpd = null;
        objJobInfo cji = null;
        List<dbObjJobs> dbObjJobsAL = null;
        dbObjJobs obj = null;
        objJobInfo ji = null;
        boolean reqPromptScanBarcodeUponPU = false;

        int counter = 0;
        int counterUp = 0;
        int newpos;
        int itemoriPos;
        boolean hasSpecialCOD = false;
        boolean hasTradeIn = false;
        boolean hasCheckList = false;
        String[] Checklist = null;
        boolean hasSecuritySeal = false;
        boolean hasIsReqStaffID = false;
        boolean hasReqShipmentVerification = false;
        boolean hasContactless = false;
        String JobContactNumbers = "";
        String ContactName = "";
        boolean hasReqItemProof = false;
        boolean IDVerificationRequired = false;
        boolean hasOTP = false;
        boolean ReceiverOnly = false;
        boolean ReqSign = false;
        boolean hasCustID = false;
        boolean hasReviewLinkSMS = false;
        boolean hasReqNFCscan = false;
        boolean isDunnnig = false;
        String CustID = "";
        long JobsIDX = 0;
        boolean aBaseCannotMassOKPwOthers;
        boolean aJobCannotMassOKPwOthers;
        String JobNoCN;

        try {
            if (cameraScannedCode != null) {
                if (!cameraScannedCode.equals("")) {
                    c = cameraScannedCode.toUpperCase();
                } else {
                    c = txtInput.getText().toString().toUpperCase();
                }
            } else {
                c = txtInput.getText().toString().toUpperCase();
            }

            if (c.indexOf(objGlobalVariables.EIDTPOD) != 0) {
                dbObjJobsAL = DAO_Jobs.searchJobs(CatTypeID, Postal, c);
                if (dbObjJobsAL != null && dbObjJobsAL.size() > 0) {
                    obj = dbObjJobsAL.get(0);
                    //expandableSortedSQLiteListAdapter = (JobListGroupSortedSQLite_PostItemAdapter) expListView.getExpandableListAdapter();
                    expandableSortedSQLiteListAdapter = (JobListGroupSortedSQLiteNew_PostItemAdapter) expListView.getExpandableListAdapter();
                    List<String> DataHeader;
                    HashMap<String, List<JobListGroupSortedSQLite_PostData>> DataChild2;
                    DataHeader = expandableSortedSQLiteListAdapter._listDataHeader;
                    DataChild2 = expandableSortedSQLiteListAdapter._listDataChild;
                    List<JobListGroupSortedSQLite_PostData> jpdlist = null;
                    JobListGroupSortedSQLite_PostData jpd = null;
                    Iterator<HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>>> itr = DataChild2.entrySet().iterator();
                    String groupName = "";
                    int groupIDX = 0;
                    int index = -1;
                    String key = "";

                    try {
                        outer:
                        while (itr.hasNext()) {
                            jpd = null;
                            HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>> entry = itr.next();
                            key = entry.getKey();
                            jpdlist = entry.getValue();
                            for (int i = 0; i <= jpdlist.size() - 1; i++) {
                                jpd = jpdlist.get(i);
                                if (jpd.JobsIDX.equals(String.valueOf(obj.JobsIDX))) {
                                    for (int j = 0; j <= DataHeader.size() - 1; j++) {
                                        groupIDX = j;
                                        groupName = DataHeader.get(j);
                                        if (key.equalsIgnoreCase(groupName)) {
                                            reqPromptScanBarcodeUponPU = JobsHandler.PromptScanBarcodeUponPU(jpd.ji);
                                            if (CatTypeID == 0 && reqPromptScanBarcodeUponPU) {
                                                //PIP
                                                //do nothing but return jobfound = true
                                                DenyJobFoundSelection = true;
                                                isSecurityBag = JobsHandler.reqSecurityTag(jpd.ji);
                                            }
                                            if (CatTypeID == 0 || CatTypeID == 1) {
                                                //PU
                                                //free select to set PU to DIP
                                                expandableSortedSQLiteListAdapter.mChildCheckStates2 = expandableSortedSQLiteListAdapter.getmChildCheckStates2(groupIDX);
                                                objListItemPosChecked getChecked2[] = expandableSortedSQLiteListAdapter.mChildCheckStates2.get(groupIDX);
                                                getChecked2[i].setChecked(true);
                                                expandableSortedSQLiteListAdapter.mChildCheckStates2.put(groupIDX, getChecked2);

                                                //jpd.setAllowedSelection(true);
                                                /*if (expandableSortedSQLiteListAdapter.mChildCheckStates.containsKey(groupIDX)) {
                                                    boolean getChecked[] = expandableSortedSQLiteListAdapter.mChildCheckStates.get(groupIDX);
                                                    getChecked[i] = true;
                                                    expandableSortedSQLiteListAdapter.mChildCheckStates.put(groupIDX, getChecked);
                                                    JobsHandler.processCheckedItemsBeforeSubmitSQLite(jpd, true, GcmPreferences, myApp.getAppContext());
                                                } else {
                                                    boolean getChecked[] = new boolean[jpdlist.size()];
                                                    getChecked[i] = true;
                                                    expandableSortedSQLiteListAdapter.mChildCheckStates.put(groupIDX, getChecked);
                                                    JobsHandler.processCheckedItemsBeforeSubmitSQLite(jpd, true, GcmPreferences, myApp.getAppContext());
                                                }*/
                                            } else if (CatTypeID == 6) {
                                                //pd
                                                //do nothing
                                            } else {
                                                //DIP
                                                ji = jpd.ji;
                                                objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
                                                firstSelected = objGlobalMethods.IsEmptyString(objCheckedItemsJsonStr);
                                                if (firstSelected) {
                                                    counter = 0;
                                                    counterUp = 0;
                                                    newpos = 0;
                                                    itemoriPos = 0;
                                                    hasSpecialCOD = JobsHandler.checkforDOCOD(ji);
                                                    hasTradeIn = JobsHandler.hasTradeIn(ji);
                                                    hasCheckList = JobsHandler.HasCheckList(ji.CheckList, "DS");
                                                    Checklist = hasCheckList ? ji.CheckList : null;
                                                    hasSecuritySeal = JobsHandler.JobHasSecuritySeal(ji, false);
                                                    hasIsReqStaffID = objGlobalMethods.isReqStaffID(ji.FLAG);
                                                    hasReqShipmentVerification = JobsHandler.isReqShipmentVerification(ji);
                                                    hasContactless = JobsHandler.isContactlessDelivery(ji);
                                                    JobContactNumbers = JobsHandler.getValidTelNumsFromJobNew(ji, 2);
                                                    if (objGlobalMethods.IsEmptyString(JobContactNumbers)) {
                                                        JobContactNumbers = JobsHandler.getTelNumsFromJobNoValidation(ji, 2);
                                                    }
                                                    String[] tellist = objGlobalMethods.convertStringToArray(JobContactNumbers, ",");
                                                    ContactName = JobsHandler.getContactName(ji, 2);
                                                    hasReqItemProof = JobsHandler.hasReqItemProofImg(ji, null, 2);
                                                    IDVerificationRequired = JobsHandler.CustIDVerificationRequired(ji) && !JobsHandler.CustIDVerificationNotRequired(ji) && !hasContactless;
                                                    hasOTP = JobsHandler.hasOTPwPin(ji);
                                                    ReceiverOnly = JobsHandler.ReceiverOnly(ji);
                                                    ReqSign = (ji.FLAG & 1024) == 1024;
                                                    hasCustID = JobsHandler.hasCustID(ji);
                                                    hasReviewLinkSMS = JobsHandler.hasReviewLinkSMS(ji);
                                                    hasReqNFCscan = JobsHandler.reqNFCscan(ji);
                                                    isDunnnig = JobsHandler.isDunning(ji);
                                                    CustID = ji.IDVerification;
                                                    JobsIDX = ji.JobsIDX;
                                                    JobNoCN = !objGlobalMethods.IsEmptyString(ji.ConsignmentNote) ? ji.ConsignmentNote : ji.JobNo;
                                                    JobListGroupSortedSQLite_PostItemAdapter.criteria = new objMultiSelectInitialCriteria(hasSpecialCOD, hasTradeIn, hasSecuritySeal,
                                                            hasIsReqStaffID, hasReqShipmentVerification, hasContactless, hasOTP, hasReqItemProof, hasCustID, IDVerificationRequired,
                                                            CustID, JobsIDX, jpd.ClientIDX, Checklist, tellist, ContactName, ReceiverOnly, isDunnnig, JobNoCN, ReqSign, hasReviewLinkSMS);
                                                    JobListGroupSortedSQLite_PostItemAdapter.criteria.setCanSlotLBUnderDoorGuard(ji.FLAG, ji.FLAG2);
                                                    JobListGroupSortedSQLite_PostItemAdapter.criteria.setHasReqNFCscan(hasReqNFCscan);
                                                    if (IDVerificationRequired && !hasContactless && hasCheckList) {
                                                        JobListGroupSortedSQLite_PostItemAdapter.criteria.addJobIDXsForVerificationChecklist(ji.JobsIDX);
                                                        JobListGroupSortedSQLite_PostItemAdapter.criteria.addJobNosForVerificationChecklist(ji.JobNo);
                                                    }
                                                    if (!IDVerificationRequired && hasContactless && hasCheckList) {
                                                        JobListGroupSortedSQLite_PostItemAdapter.criteria.addJobIDXsForContactlessChecklist(ji.JobsIDX);
                                                        JobListGroupSortedSQLite_PostItemAdapter.criteria.addJobNosForContactlessChecklist(ji.JobNo);
                                                    }
                                                    aBaseCannotMassOKPwOthers = JobListGroupSortedSQLite_PostItemAdapter.BaseCannotMassOKPwOthers();

                                                    if (expandableSortedSQLiteListAdapter._listDataChild != null && expandableSortedSQLiteListAdapter._listDataChild.size() > 0
                                                            && expandableSortedSQLiteListAdapter._listDataHeader != null && expandableSortedSQLiteListAdapter._listDataHeader.size() > 0) {
                                                        for (int x = 0; x <= expandableSortedSQLiteListAdapter._listDataHeader.size() - 1; x++) {
                                                            counter = 0;
                                                            counterUp = 0;
                                                            clist = expandableSortedSQLiteListAdapter._listDataChild.get(expandableSortedSQLiteListAdapter._listDataHeader.get(x));
                                                            expandableSortedSQLiteListAdapter.mChildCheckStates2 = expandableSortedSQLiteListAdapter.getmChildCheckStates2(x);
                                                            objListItemPosChecked getChecked2[] = expandableSortedSQLiteListAdapter.mChildCheckStates2.get(x);

                                                            if (x != groupIDX) {
                                                                for (int y = 0; y <= clist.size() - 1; y++) {
                                                                    itemoriPos = clist.get(y).oriPos;
                                                                    clist.get(y).setCbEnabledAllowedSelection(true, false, itemoriPos);
                                                                    getChecked2[y].setIsCheck_N_NewPos(false, itemoriPos);
                                                                }
                                                            } else {
                                                                for (int z = 0; z <= clist.size() - 1; z++) {
                                                                    cjpd = clist.get(z);
                                                                    cji = cjpd.ji;
                                                                    aJobCannotMassOKPwOthers = JobListGroupSortedSQLite_PostItemAdapter.JobCannotMassOKPwOthers(cji);
                                                                    if (cji.JobsIDX != ji.JobsIDX) {
                                                                        counter++;
                                                                        if (aBaseCannotMassOKPwOthers || aJobCannotMassOKPwOthers || expandableSortedSQLiteListAdapter.hasRejectedCriteria2(cji, 0)) {
                                                                            newpos = clist.size() - counter;
                                                                            clist.get(z).setCbEnabledAllowedSelection(true, false, newpos);
                                                                            getChecked2[z].setIsCheck_N_NewPos(false, newpos);
                                                                        } else {
                                                                            counterUp++;
                                                                            counter--;
                                                                            newpos = counterUp;
                                                                            clist.get(z).setCbEnabledAllowedSelection(true, true, newpos);
                                                                            getChecked2[z].setIsCheck_N_NewPos(false, newpos);
                                                                        }
                                                                    } else {
                                                                        if (z > 0 && counter > 0) {
                                                                            counter--;
                                                                        }
                                                                        clist.get(z).setCbEnabledAllowedSelection(true, true, 0);
                                                                        getChecked2[z].setIsCheck_N_NewPos(true, 0);
                                                                    }
                                                                }
                                                                Collections.sort(expandableSortedSQLiteListAdapter._listDataChild.get(expandableSortedSQLiteListAdapter._listDataHeader.get(groupIDX)));
                                                                Collections.sort(Arrays.asList(expandableSortedSQLiteListAdapter.mChildCheckStates2.get(groupIDX)));
                                                                HashMap<String, List<JobListGroupSortedSQLite_PostData>> rePositionedGroupItems = new HashMap<String, List<JobListGroupSortedSQLite_PostData>>();
                                                                rePositionedGroupItems.put(expandableSortedSQLiteListAdapter._listDataHeader.get(groupIDX),
                                                                        expandableSortedSQLiteListAdapter._listDataChild.get(expandableSortedSQLiteListAdapter._listDataHeader.get(groupIDX)));
                                                                Gson gson = new Gson();
                                                                String rePositionedGroupItemsPrefStr = gson.toJson(rePositionedGroupItems);
                                                                objGlobalMethods.setGCMPrefs(GcmPreferences, "rePositionedGroupItemsPrefStr", rePositionedGroupItemsPrefStr);
                                                            }
                                                        }
                                                    }
                                                    jpd.setAllowedSelection(true);
                                                    Gson gson = new Gson();
                                                    objMultiSelectInitialCriteriaJsonStr = gson.toJson(JobListGroupSortedSQLite_PostItemAdapter.criteria);
                                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "objMultiSelectInitialCriteriaJsonStr", objMultiSelectInitialCriteriaJsonStr);
                                                    //JobListGroupSortedSQLite_PostItemAdapter.criteria = null;

                                                } else {
                                                    if (JobListGroupSortedSQLite_PostItemAdapter.criteria == null) {
                                                        objMultiSelectInitialCriteriaJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objMultiSelectInitialCriteriaJsonStr");
                                                        if (!objGlobalMethods.IsEmptyString(objMultiSelectInitialCriteriaJsonStr)) {
                                                            Gson gson = new Gson();
                                                            JobListGroupSortedSQLite_PostItemAdapter.criteria = gson.fromJson(objMultiSelectInitialCriteriaJsonStr, objMultiSelectInitialCriteria.class);
                                                        }
                                                    }

                                                    objListItemPosChecked getChecked2[] = expandableSortedSQLiteListAdapter.mChildCheckStates2.get(groupIDX);
                                                    if (!jpd.allowedSelection && !getChecked2[i].isChecked()) {
                                                        JobListGroupSortedSQLite_PostItemAdapter.criteria.setNotAllowedSelectionCount(1);
                                                    }
                                                    getChecked2[i].setChecked(true);
                                                    expandableSortedSQLiteListAdapter.mChildCheckStates2.put(groupIDX, getChecked2);
                                                }
                                            }
                                            index = i;
                                            jobFound = true;
                                            break outer;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity SelectJob().", "", "", "", "");
                        jpd = null;
                        jobFound = false;
                    }

                    if (jobFound) {
                        playSuccessVerifiedTone();
                        JobsHandler.processCheckedItemsBeforeSubmitSQLite(jpd, true, GcmPreferences, myApp.getAppContext());
                        if (expandableSortedSQLiteListAdapter.mChildCheckStates2 != null && expandableSortedSQLiteListAdapter.mChildCheckStates2.size() > 0) {
                            Gson gson = new Gson();
                            String mChildCheckStates2PrefStr = gson.toJson(expandableSortedSQLiteListAdapter.mChildCheckStates2);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "mChildCheckStates2PrefStr", mChildCheckStates2PrefStr);
                        }
                        expandableSortedSQLiteListAdapter.notifyDataSetChanged();
                        final Boolean aDenyJobFoundSelection = DenyJobFoundSelection;
                        final Boolean aisSecurityBag = isSecurityBag;
                        final int finalindex = index;
                        if (index > 0) {
                            if (CatTypeID != 2) {
                                int finalIDX = 0;
                                if (groupIDX == 0) {
                                    finalIDX = 1 + finalindex;
                                    expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                                    //expListView.smoothScrollToPositionFromTop(groupIDX + index, 0);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(1 + finalindex);
                                            if (aDenyJobFoundSelection) {
                                                showPromptScanBarcodeUponPU(aisSecurityBag);
                                            }
                                        }
                                    }, 100);
                                } else if (groupIDX > 0) {
                                    final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                    finalIDX = skippedCount + finalindex;
                                    expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                                    //expListView.smoothScrollToPositionFromTop(skippedCount + index, 0);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(skippedCount + finalindex);
                                            if (aDenyJobFoundSelection) {
                                                showPromptScanBarcodeUponPU(aisSecurityBag);
                                            }
                                        }
                                    }, 100);
                                }

                                String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                                String[] prefvalue = new String[]{String.valueOf(groupIDX), String.valueOf(finalIDX)};
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                            } else {
                                if (firstSelected) {
                                    final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                    if (groupIDX == 0) {
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(0);
                                            }
                                        }, 100);
                                    } else {
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(skippedCount - 1);
                                            }
                                        }, 100);
                                    }

                                    String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                                    String[] prefvalue = new String[]{String.valueOf(groupIDX), groupIDX == 0 ? "0" : String.valueOf(skippedCount - 1)};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                } else {
                                    int finalIDX = 0;
                                    if (groupIDX == 0) {
                                        finalIDX = finalindex;
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(1 + finalindex);
                                            }
                                        }, 100);
                                    } else if (groupIDX > 0) {
                                        final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                        finalIDX = skippedCount + finalindex;
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(skippedCount + finalindex);
                                            }
                                        }, 100);
                                    }

                                    String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                                    String[] prefvalue = new String[]{String.valueOf(groupIDX), String.valueOf(finalIDX)};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                }
                            }
                        } else {
                            if (CatTypeID != 2) {
                                int finalIDX = 0;
                                if (groupIDX == 0) {
                                    finalIDX = 1 + finalindex;
                                    expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                                    //expListView.smoothScrollToPositionFromTop(groupIDX + index, 0);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(1 + finalindex);
                                            if (aDenyJobFoundSelection) {
                                                showPromptScanBarcodeUponPU(aisSecurityBag);
                                            }
                                        }
                                    }, 100);
                                } else if (groupIDX > 0) {
                                    final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                    finalIDX = skippedCount + finalindex;
                                    expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                                    //expListView.smoothScrollToPositionFromTop(skippedCount + index, 0);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(skippedCount + finalindex);
                                            if (aDenyJobFoundSelection) {
                                                showPromptScanBarcodeUponPU(aisSecurityBag);
                                            }
                                        }
                                    }, 100);
                                }

                                String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                                String[] prefvalue = new String[]{String.valueOf(groupIDX), String.valueOf(finalIDX)};
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                            } else {
                                if (firstSelected) {
                                    final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                    if (groupIDX == 0) {
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(0);
                                            }
                                        }, 100);
                                    } else {
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(skippedCount - 1);
                                            }
                                        }, 100);
                                    }

                                    String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                                    String[] prefvalue = new String[]{String.valueOf(groupIDX), groupIDX == 0 ? "0" : String.valueOf(skippedCount - 1)};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                } else {
                                    int finalIDX = 0;
                                    if (groupIDX == 0) {
                                        finalIDX = finalindex;
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(1 + finalindex);
                                            }
                                        }, 100);
                                    } else if (groupIDX > 0) {
                                        final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                        finalIDX = skippedCount + finalindex;
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(skippedCount + finalindex);
                                            }
                                        }, 100);
                                    }

                                    String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                                    String[] prefvalue = new String[]{String.valueOf(groupIDX), String.valueOf(finalIDX)};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                }
                            }
                        }
                    } else {
                        playTone();
                        showDialogNfocusTxtbox(c + "\n\n" + myApp.getAppContext().getString(R.string.alert_jobnotfound));
                    }
                } else {
                    if (CatTypeID == 0) {
                        dbObjJobsAL = DAO_Jobs.searchJobs(1, Postal, c);
                        if (dbObjJobsAL != null && dbObjJobsAL.size() > 0) {
                            playTone2();
                            showDialogNfocusTxtbox(c + "\n\n" + myApp.getAppContext().getString(R.string.itempu));
                        } else {
                            playTone();
                            showDialogNfocusTxtbox(c + "\n\n" + myApp.getAppContext().getString(R.string.alert_jobnotfound));
                        }
                    } else {
                        playTone();
                        showDialogNfocusTxtbox(c + "\n\n" + myApp.getAppContext().getString(R.string.alert_jobnotfound));
                    }
                    /*playTone();
                    showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_jobnotfound));*/
                }
            } else {
                playTone();
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_jobnotfound));
            }
            EnableDisableFailOKPbuttons();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity selectJobSQLite2. Exception: " + e.getMessage().toString(), "", "", "", "");
            playTone();
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata));
        }
    }

    public boolean hasChecked(HashMap<Integer, boolean[]> mChildCheckStates) {
        boolean asd = false;
        try {
            if (mChildCheckStates == null) {
                Gson gson = new Gson();
                SharedPreferences glsp = objGlobalMethods.getGrouplistSP();
                String mChildCheckStatesPrefStr = objGlobalMethods.getGrouplistSP_KeyValue(glsp, "mChildCheckStatesPrefStr");
                mChildCheckStates = objGlobalMethods.IsEmptyString(mChildCheckStatesPrefStr) ? null :
                        gson.fromJson(mChildCheckStatesPrefStr, new TypeToken<HashMap<Integer, boolean[]>>() {
                        }.getType());
            }

            if (mChildCheckStates != null && mChildCheckStates.size() > 0) {
                outer:
                for (boolean[] arr : mChildCheckStates.values()) {
                    for (boolean b : arr) {
                        if (b) {
                            asd = true;
                            break outer;
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobListGroupActivity hasChecked. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private void selectJobSQLiteNew() {
        SharedPreferences glsp = objGlobalMethods.getGrouplistSP();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String jobSelectionInfoJsonStr = "";
        String jobSelectionInfoJsonStr_ = "";
        Gson gson = new Gson();
        String c = "";
        boolean firstSelected = false;
        boolean jobFound = false;
        Integer CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
        Boolean DenyJobFoundSelection = false;
        Boolean isSecurityBag = false;
        List<JobListGroupSortedSQLite_PostData> clist = null;
        JobListGroupSortedSQLite_PostData cjpd = null;
        objJobInfo cji = null;
        List<dbObjJobs> dbObjJobsAL = null;
        dbObjJobs obj = null;
        objJobInfo ji = null;
        boolean reqPromptScanBarcodeUponPU = false;
        //jobSelectionInfo j1 = null;
        jobSelectionInfo j2 = null;
        boolean j2canMass = false;

        try {
            if (cameraScannedCode != null) {
                if (!cameraScannedCode.equals("")) {
                    c = cameraScannedCode.toUpperCase();
                } else {
                    c = txtInput.getText().toString().toUpperCase();
                }
            } else {
                c = txtInput.getText().toString().toUpperCase();
            }

            if (c.indexOf(objGlobalVariables.EIDTPOD) != 0) {
                dbObjJobsAL = DAO_Jobs.searchJobs(CatTypeID, Postal, c);
                if (dbObjJobsAL != null && dbObjJobsAL.size() > 0) {
                    obj = dbObjJobsAL.get(0);
                    expandableSortedSQLiteListAdapter = (JobListGroupSortedSQLiteNew_PostItemAdapter) expListView.getExpandableListAdapter();
                    List<String> DataHeader;
                    HashMap<String, List<JobListGroupSortedSQLite_PostData>> DataChild2;
                    DataHeader = expandableSortedSQLiteListAdapter._listDataHeader;
                    DataChild2 = expandableSortedSQLiteListAdapter._listDataChild;
                    List<JobListGroupSortedSQLite_PostData> jpdlist = null;
                    JobListGroupSortedSQLite_PostData jpd = null;
                    Iterator<HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>>> itr = DataChild2.entrySet().iterator();
                    String groupName = "";
                    int groupIDX = 0;
                    int index = -1;
                    String key = "";

                    try {
                        outer:
                        while (itr.hasNext()) {
                            jpd = null;
                            HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>> entry = itr.next();
                            key = entry.getKey();
                            jpdlist = entry.getValue();
                            for (int i = 0; i <= jpdlist.size() - 1; i++) {
                                jpd = jpdlist.get(i);
                                if (jpd.JobsIDX.equals(String.valueOf(obj.JobsIDX))) {
                                    for (int j = 0; j <= DataHeader.size() - 1; j++) {
                                        groupIDX = j;
                                        groupName = DataHeader.get(j);
                                        if (key.equalsIgnoreCase(groupName)) {
                                            reqPromptScanBarcodeUponPU = JobsHandler.PromptScanBarcodeUponPU(jpd.ji);
                                            if (CatTypeID == 0 && reqPromptScanBarcodeUponPU) {
                                                //PIP
                                                //do nothing but return jobfound = true
                                                DenyJobFoundSelection = true;
                                                isSecurityBag = JobsHandler.reqSecurityTag(jpd.ji);
                                            }
                                            if (CatTypeID == 0 || CatTypeID == 1) {
                                                //PU
                                                //free select to set PU to DIP
                                                //expandableSortedSQLiteListAdapter.mChildCheckStates = expandableSortedSQLiteListAdapter.getmChildCheckStates(groupIDX);
                                                boolean getChecked[] = expandableSortedSQLiteListAdapter.getmChildCheckStates(groupIDX).get(groupIDX);
                                                getChecked[i] = true;
                                                expandableSortedSQLiteListAdapter.mChildCheckStates.put(groupIDX, getChecked);
                                            } else if (CatTypeID == 6) {
                                                //pd
                                                //do nothing
                                            } else {
                                                //DIP
                                                ji = jpd.ji;
                                                //objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
                                                firstSelected = !hasChecked(expandableSortedSQLiteListAdapter.mChildCheckStates);
                                                if (firstSelected) {
                                                    int itemoriPos;
                                                    boolean j1canMass = false;

                                                    expandableSortedSQLiteListAdapter.j1 = jobSelectionInfo.getjobSelectionInfo(ji, true);
                                                    j1canMass = expandableSortedSQLiteListAdapter.j1.canMassWithOthers();

                                                    if (expandableSortedSQLiteListAdapter._listDataChild != null && expandableSortedSQLiteListAdapter._listDataChild.size() > 0 && expandableSortedSQLiteListAdapter._listDataHeader != null && expandableSortedSQLiteListAdapter._listDataHeader.size() > 0) {
                                                        for (int z = 0; z <= expandableSortedSQLiteListAdapter._listDataHeader.size() - 1; z++) {
                                                            clist = expandableSortedSQLiteListAdapter._listDataChild.get(expandableSortedSQLiteListAdapter._listDataHeader.get(z));

                                                            boolean getChecked[] = expandableSortedSQLiteListAdapter.getmChildCheckStates(z).get(z);
                                                            if (z != groupIDX) {
                                                                for (int x = 0; x <= clist.size() - 1; x++) {
                                                                    itemoriPos = clist.get(x).oriPos;
                                                                    if (!j1canMass) {
                                                                        clist.get(x).setCbEnabledAllowedSelection(true, false, itemoriPos);
                                                                        expandableSortedSQLiteListAdapter.j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                                        //getChecked[x] = false;
                                                                    } else {
                                                                        cjpd = clist.get(x);
                                                                        cji = cjpd.ji;
                                                                        j2 = jobSelectionInfo.getjobSelectionInfo(cji, false);
                                                                        j2canMass = expandableSortedSQLiteListAdapter.j1.canMass(j2);

                                                                        clist.get(x).setCbEnabledAllowedSelection(true, j2canMass, itemoriPos);
                                                                        if (!j2canMass)
                                                                            expandableSortedSQLiteListAdapter.j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                                        //getChecked[x] = false;

                                                                    }
                                                                    getChecked[x] = false;
                                                                }
                                                                expandableSortedSQLiteListAdapter.mChildCheckStates.put(z, getChecked);

                                                            } else {
                                                                for (int x = 0; x <= clist.size() - 1; x++) {
                                                                    cjpd = clist.get(x);
                                                                    cji = cjpd.ji;
                                                                    if (cji.JobsIDX != ji.JobsIDX && !j1canMass) {
                                                                        clist.get(x).setCbEnabledAllowedSelection(true, false, 0);
                                                                        expandableSortedSQLiteListAdapter.j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                                    } else if (cji.JobsIDX != ji.JobsIDX) {
                                                                        j2 = jobSelectionInfo.getjobSelectionInfo(cji, false);
                                                                        j2canMass = expandableSortedSQLiteListAdapter.j1.canMass(j2);

                                                                        clist.get(x).setCbEnabledAllowedSelection(true, !(!j1canMass || !j2canMass), 0);
                                                                        if (!j2canMass)
                                                                            expandableSortedSQLiteListAdapter.j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                                        getChecked[x] = false;
                                                                    } else {
                                                                        clist.get(x).setCbEnabledAllowedSelection(true, true, 0);
                                                                        expandableSortedSQLiteListAdapter.j1.addSelectedValidJobIDXList(cji.JobsIDX);
                                                                        getChecked[x] = true;
                                                                    }

                                                                }
                                                                expandableSortedSQLiteListAdapter.mChildCheckStates.put(groupIDX, getChecked);
                                                            }
                                                        }
                                                    }
                                                    jpd.setAllowedSelection(true);
                                                    jobSelectionInfoJsonStr = gson.toJson(expandableSortedSQLiteListAdapter.j1);

                                                } else {
                                                    boolean getChecked[] = expandableSortedSQLiteListAdapter.getmChildCheckStates(groupIDX).get(groupIDX);
                                                    getChecked[i] = true;
                                                    expandableSortedSQLiteListAdapter.mChildCheckStates.put(groupIDX, getChecked);
                                                    if (expandableSortedSQLiteListAdapter.j1 == null) {
                                                        jobSelectionInfoJsonStr_ = objGlobalMethods.getGrouplistSP_KeyValue(glsp, "jobSelectionInfoJsonStr");
                                                        if (!objGlobalMethods.IsEmptyString(jobSelectionInfoJsonStr_)) {
                                                            expandableSortedSQLiteListAdapter.j1 = gson.fromJson(jobSelectionInfoJsonStr_, jobSelectionInfo.class);
                                                        }
                                                    }
                                                    j2 = jobSelectionInfo.getjobSelectionInfo(ji, false);
                                                    if (!expandableSortedSQLiteListAdapter.j1.canMass(j2)) {
                                                        expandableSortedSQLiteListAdapter.j1.setNotAllowedSelectionCount(1);
                                                        expandableSortedSQLiteListAdapter.j1.addSelectedInvalidJobIDXList(ji.JobsIDX);
                                                    } else {
                                                        expandableSortedSQLiteListAdapter.j1.addSelectedValidJobIDXList(ji.JobsIDX);
                                                        expandableSortedSQLiteListAdapter.j1.setFieldsFromj2(j2);

                                                        ////
                                                        ////
                                                        boolean j1canMass = expandableSortedSQLiteListAdapter.j1.canMassWithOthers();
                                                        if (expandableSortedSQLiteListAdapter._listDataChild != null && expandableSortedSQLiteListAdapter._listDataChild.size() > 0 && expandableSortedSQLiteListAdapter._listDataHeader != null && expandableSortedSQLiteListAdapter._listDataHeader.size() > 0) {
                                                            for (int z = 0; z <= expandableSortedSQLiteListAdapter._listDataHeader.size() - 1; z++) {
                                                                clist = expandableSortedSQLiteListAdapter._listDataChild.get(expandableSortedSQLiteListAdapter._listDataHeader.get(z));
                                                                if (z != groupIDX) {
                                                                    boolean getChecked2[] = expandableSortedSQLiteListAdapter.getmChildCheckStates(z).get(z);
                                                                    for (int x = 0; x <= clist.size() - 1; x++) {
                                                                        if (clist.get(x).ji.JobsIDX != expandableSortedSQLiteListAdapter.j1.JobsIDX) {
                                                                            if (!j1canMass) {
                                                                                clist.get(x).setCbEnabledAllowedSelection(true, false, 0);
                                                                                expandableSortedSQLiteListAdapter.j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                                            } else {
                                                                                cjpd = clist.get(x);
                                                                                cji = cjpd.ji;
                                                                                j2 = jobSelectionInfo.getjobSelectionInfo(cji, false);
                                                                                j2canMass = expandableSortedSQLiteListAdapter.j1.canMass(j2);

                                                                                clist.get(x).setCbEnabledAllowedSelection(true, j2canMass, 0);
                                                                                if (!j2canMass)
                                                                                    expandableSortedSQLiteListAdapter.j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);

                                                                            }
                                                                        }
                                                                    }
                                                                    expandableSortedSQLiteListAdapter.mChildCheckStates.put(z, getChecked2);

                                                                } else {
                                                                    for (int x = 0; x <= clist.size() - 1; x++) {
                                                                        if (clist.get(x).ji.JobsIDX != expandableSortedSQLiteListAdapter.j1.JobsIDX) {
                                                                            cjpd = clist.get(x);
                                                                            cji = cjpd.ji;
                                                                            if (cji.JobsIDX != ji.JobsIDX && !j1canMass) {
                                                                                clist.get(x).setCbEnabledAllowedSelection(true, false, 0);
                                                                                expandableSortedSQLiteListAdapter.j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                                            } else if (cji.JobsIDX != ji.JobsIDX) {
                                                                                j2 = jobSelectionInfo.getjobSelectionInfo(cji, false);
                                                                                j2canMass = expandableSortedSQLiteListAdapter.j1.canMass(j2);

                                                                                clist.get(x).setCbEnabledAllowedSelection(true, !(!j1canMass || !j2canMass), 0);
                                                                                if (!j2canMass)
                                                                                    expandableSortedSQLiteListAdapter.j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                                            } else {
                                                                                clist.get(x).setCbEnabledAllowedSelection(true, true, 0);
                                                                                expandableSortedSQLiteListAdapter.j1.addSelectedValidJobIDXList(cji.JobsIDX);
                                                                            }
                                                                        }

                                                                    }
                                                                    expandableSortedSQLiteListAdapter.mChildCheckStates.put(groupIDX, getChecked);
                                                                }
                                                            }
                                                        }
                                                        ////
                                                        ////
                                                    }

                                                    jobSelectionInfoJsonStr = gson.toJson(expandableSortedSQLiteListAdapter.j1);
                                                }
                                            }
                                            index = i;
                                            jobFound = true;

                                            jpdlist.get(i).setIsSelected(true);
                                            JobsHandler3.processCheckedItemsBeforeSubmitSQLiteNew(jpd, true, GcmPreferences);
                                            break outer;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity selectJobSQLiteNew part 1.", "", "", "", "");
                        jpd = null;
                        jobFound = false;
                    }

                    if (jobFound) {
                        playSuccessVerifiedTone();
                        //JobsHandler.processCheckedItemsBeforeSubmitSQLite(jpd, true, GcmPreferences, myApp.getAppContext());
                        /*if (expandableSortedSQLiteListAdapter.mChildCheckStates != null && expandableSortedSQLiteListAdapter.mChildCheckStates.size() > 0) {
                            String mChildCheckStates2PrefStr = gson.toJson(expandableSortedSQLiteListAdapter.mChildCheckStates2);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "mChildCheckStates2PrefStr", mChildCheckStates2PrefStr);
                        }*/
                        String mChildCheckStatesPrefStr = (expandableSortedSQLiteListAdapter.mChildCheckStates != null && expandableSortedSQLiteListAdapter.mChildCheckStates.size() > 0)
                                ? gson.toJson(expandableSortedSQLiteListAdapter.mChildCheckStates) : "";
                        String[] glspPName = new String[]{"mChildCheckStatesPrefStr", "jobSelectionInfoJsonStr"};
                        String[] glspPalue = new String[]{mChildCheckStatesPrefStr, jobSelectionInfoJsonStr};
                        objGlobalMethods.setGrouplistSPMass(glsp, glspPName, glspPalue);

                        expandableSortedSQLiteListAdapter.notifyDataSetChanged();
                        final Boolean aDenyJobFoundSelection = DenyJobFoundSelection;
                        final Boolean aisSecurityBag = isSecurityBag;
                        final int finalindex = index;
                        if (index > 0) {
                            if (CatTypeID != 2) {
                                int finalIDX = 0;
                                if (groupIDX == 0) {
                                    finalIDX = 1 + finalindex;
                                    expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                                    //expListView.smoothScrollToPositionFromTop(groupIDX + index, 0);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(1 + finalindex);
                                            if (aDenyJobFoundSelection) {
                                                showPromptScanBarcodeUponPU(aisSecurityBag);
                                            }
                                        }
                                    }, 100);
                                } else if (groupIDX > 0) {
                                    final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                    finalIDX = skippedCount + finalindex;
                                    expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(skippedCount + finalindex);
                                            if (aDenyJobFoundSelection) {
                                                showPromptScanBarcodeUponPU(aisSecurityBag);
                                            }
                                        }
                                    }, 100);
                                }

                                /*String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                                String[] prefvalue = new String[]{String.valueOf(groupIDX), String.valueOf(finalIDX)};
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);*/
                            } else {
                                if (firstSelected) {
                                    final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(skippedCount - 1);
                                        }
                                    }, 100);

                                    /*String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                                    String[] prefvalue = new String[]{String.valueOf(groupIDX), groupIDX == 0 ? "0" : String.valueOf(skippedCount - 1)};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);*/
                                } else {
                                    int finalIDX = 0;
                                    if (groupIDX == 0) {
                                        finalIDX = finalindex;
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(1 + finalindex);
                                            }
                                        }, 100);
                                    } else if (groupIDX > 0) {
                                        final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                        finalIDX = skippedCount + finalindex;
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(skippedCount + finalindex);
                                            }
                                        }, 100);
                                    }

                                    /*String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                                    String[] prefvalue = new String[]{String.valueOf(groupIDX), String.valueOf(finalIDX)};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);*/
                                }
                            }
                        } else {
                            if (CatTypeID != 2) {
                                int finalIDX = 0;
                                if (groupIDX == 0) {
                                    finalIDX = 1 + finalindex;
                                    expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(1 + finalindex);
                                            if (aDenyJobFoundSelection) {
                                                showPromptScanBarcodeUponPU(aisSecurityBag);
                                            }
                                        }
                                    }, 100);
                                } else if (groupIDX > 0) {
                                    final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                    finalIDX = skippedCount + finalindex;
                                    expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(skippedCount + finalindex);
                                            if (aDenyJobFoundSelection) {
                                                showPromptScanBarcodeUponPU(aisSecurityBag);
                                            }
                                        }
                                    }, 100);
                                }

                                /*String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                                String[] prefvalue = new String[]{String.valueOf(groupIDX), String.valueOf(finalIDX)};
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);*/
                            } else {
                                if (firstSelected) {
                                    final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                    if (groupIDX == 0) {
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(0);
                                            }
                                        }, 100);
                                    } else {
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(skippedCount - 1);
                                            }
                                        }, 100);
                                    }

                                    /*String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                                    String[] prefvalue = new String[]{String.valueOf(groupIDX), groupIDX == 0 ? "0" : String.valueOf(skippedCount - 1)};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);*/
                                } else {
                                    int finalIDX = 0;
                                    if (groupIDX == 0) {
                                        finalIDX = finalindex;
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(1 + finalindex);
                                            }
                                        }, 100);
                                    } else if (groupIDX > 0) {
                                        final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                        finalIDX = skippedCount + finalindex;
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                expListView.setSelection(skippedCount + finalindex);
                                            }
                                        }, 100);
                                    }

                                    /*String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                                    String[] prefvalue = new String[]{String.valueOf(groupIDX), String.valueOf(finalIDX)};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);*/
                                }
                            }
                        }
                    } else {
                        playTone();
                        showDialogNfocusTxtbox(c + "\n\n" + myApp.getAppContext().getString(R.string.alert_jobnotfound));
                    }
                } else {
                    if (CatTypeID == 0) {
                        dbObjJobsAL = DAO_Jobs.searchJobs(1, Postal, c);
                        if (dbObjJobsAL != null && dbObjJobsAL.size() > 0) {
                            playTone2();
                            showDialogNfocusTxtbox(c + "\n\n" + myApp.getAppContext().getString(R.string.itempu));
                        } else {
                            playTone();
                            showDialogNfocusTxtbox(c + "\n\n" + myApp.getAppContext().getString(R.string.alert_jobnotfound));
                        }
                    } else {
                        playTone();
                        showDialogNfocusTxtbox(c + "\n\n" + myApp.getAppContext().getString(R.string.alert_jobnotfound));
                    }
                }
            } else {
                playTone();
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_jobnotfound));
            }
            EnableDisableFailOKPbuttonsNew(expandableSortedSQLiteListAdapter.j1);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity selectJobSQLiteNew. Exception: " + e.getMessage().toString(), "", "", "", "");
            playTone();
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata));
        }
    }

    public static int getSkippedCount(Iterator<HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>>> itr, int selectGroupIDX, List<String> DataHeader) {
        int count = 0;
        String key = "";
        List<JobListGroupSortedSQLite_PostData> jpdlist = null;
        String groupName = "";
        List<String> keyList = new ArrayList<>();
        List<List<JobListGroupSortedSQLite_PostData>> jpdlistList = new ArrayList<>();
        try {
            while (itr.hasNext()) {
                HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>> entry = itr.next();
                key = entry.getKey();
                jpdlist = entry.getValue();
                keyList.add(key);
                jpdlistList.add(jpdlist);
            }

            asd:
            for (int j = 0; j <= DataHeader.size() - 1; j++) {
                if (j < selectGroupIDX) {
                    groupName = DataHeader.get(j);
                    outer:
                    for (int i = 0; i <= keyList.size() - 1; i++) {
                        if (keyList.get(i).equals(groupName) && jpdlistList.get(i) != null) {
                            count += jpdlistList.get(i).size() + 1;
                            break outer;
                        }
                    }
                } else if (j == selectGroupIDX) {
                    count += 1;
                    break asd;
                }
            }

        } catch (Exception e) {
            count = 0;
        } finally {
            return count;
        }
    }

    private void resetBack() {

        try {
            JobsHandler.clearJobsListGroupPrefs(GcmPreferences);
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "mChildCheckStates2PrefStr", "");
            objGlobalMethods.setGrouplistSP_KeyValue(objGlobalMethods.getGrouplistSP(), "mChildCheckStatesPrefStr", "");
            int TotalJobsCountPerCatTypePerPostal = 0;
            String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            if (Postal != "") {
                catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
                /*TotalJobsCountPerCatTypePerPostal = !objGlobalMethods.IsDefaultVRPpage() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal) :
                        !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(catType, Postal, SelectedVRPJobsIDXs)
                                : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal);*/
                TotalJobsCountPerCatTypePerPostal = !isVrpView() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal) :
                        !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(catType, Postal, SelectedVRPJobsIDXs)
                                : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal);

                if (TotalJobsCountPerCatTypePerPostal > 0) {
                    final JobsListGroupActivity act = (JobsListGroupActivity) myApp.currentActivity;
                    /*final List<dbObjJobs> dbObjJobsAL = !objGlobalMethods.IsDefaultVRPpage() ? DAO_Jobs.getAlldbObjJobsPerCategoryForJobsList(catType, Postal) :
                            !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getAlldbObjJobsPerCategoryForJobsListByJobsIDXs(catType, Postal, SelectedVRPJobsIDXs) :
                                    DAO_Jobs.getAlldbObjJobsPerCategoryForJobsList(catType, Postal);*/
                    final List<dbObjJobs> dbObjJobsAL = !isVrpView() ? DAO_Jobs.getAlldbObjJobsPerCategoryForJobsList(catType, Postal) :
                            !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getAlldbObjJobsPerCategoryForJobsListByJobsIDXs(catType, Postal, SelectedVRPJobsIDXs) :
                                    DAO_Jobs.getAlldbObjJobsPerCategoryForJobsList(catType, Postal);

                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            // Notifiy activity
                            //act.loadListSQLite2(dbObjJobsAL);
                            act.loadListSQLiteNew(dbObjJobsAL);
                        }
                    });

                } else {
                    if (!objGlobalMethods.getGCMPrefs(GcmPreferences, "isFromDashboardSub").isEmpty()) {
                        //back to DashboardSubActivity or AddressListRoutingActivity

                        String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName};
                        String[] prefvalue = new String[]{"-1", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                        //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                        //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");

                        Integer TotalPIPDIPJobsCountPerPostal = DAO_Jobs.getTotalPIPDIPJobsCountPerPostal(Postal);
                        if (TotalPIPDIPJobsCountPerPostal > 0) {
                            //Intent parentIntent = new Intent(JobsListGroupActivity.this, DashboardSubActivity.class);
                            Intent parentIntent = new Intent(myApp.currentActivity, DashboardSubActivity.class);
                            MainActivity.uponLogin = 0;
                            startActivity(parentIntent);
                            JobsListGroupActivity.this.finish();
                        } else {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                            objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                            //Intent parentIntent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                            Intent parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                            MainActivity.uponLogin = 0;
                            startActivity(parentIntent);
                            JobsListGroupActivity.this.finish();
                        }
                    } else {
                        //back to AddressListNewActivity or DrawerMainActivity
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                        MainActivity.uponLogin = 0;
                        int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(catType);
                        if (TotalJobsCountPerCatType == 0) {
                            String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName};
                            String[] prefvalue = new String[]{"-1", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                            //Intent parentIntent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                            Intent parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                            startActivity(parentIntent);
                            JobsListGroupActivity.this.finish();
                        } else {
                            objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                            //Intent parentIntent = new Intent(JobsListGroupActivity.this, AddressListNewActivity.class);
                            Intent parentIntent = new Intent(myApp.currentActivity, AddressListNewActivity.class);
                            startActivity(parentIntent);
                            JobsListGroupActivity.this.finish();
                        }
                    }
                }

            } else {
                //back to DashboardSubActivity or AddressListRoutingActivity or AddressListNewActivity or DrawerMainActivity

                String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName};
                String[] prefvalue = new String[]{"-1", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                if (!objGlobalMethods.getGCMPrefs(GcmPreferences, "isFromDashboardSub").isEmpty()) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                    objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                    objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                    //Intent parentIntent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                    Intent parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                    MainActivity.uponLogin = 0;
                    startActivity(parentIntent);
                    JobsListGroupActivity.this.finish();
                } else {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                    MainActivity.uponLogin = 0;
                    int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(catType);
                    if (TotalJobsCountPerCatType == 0) {
                        String[] prefName1 = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName};
                        String[] prefvalue1 = new String[]{"-1", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName1, prefvalue1);

                        //Intent parentIntent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                        Intent parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                        startActivity(parentIntent);
                        JobsListGroupActivity.this.finish();
                    } else {
                        objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                        objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                        //Intent parentIntent = new Intent(JobsListGroupActivity.this, AddressListNewActivity.class);
                        Intent parentIntent = new Intent(myApp.currentActivity, AddressListNewActivity.class);
                        startActivity(parentIntent);
                        JobsListGroupActivity.this.finish();
                    }
                }
            }


        } catch (Exception e) {
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName, "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX, "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "mutlipleCODjobs", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", "");
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at JobsListGroupActivity reset back. Exception: " + e.getMessage().toString(), "", "", "", "");

            //Intent parentIntent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            Intent parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            startActivity(parentIntent);
            JobsListGroupActivity.this.finish();
        }
    }

    //=========================================sqlite=================================================

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
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            String action = intent.getAction();
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                return;
            }

        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListNew_Activity handleIntent. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    public void promptReqMPD() {
        /*objGlobalMethods.promptReqMPD();
        mpdclass.promptRegBTTL();*/
        objGlobalMethods.promptReqMPD_New();
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
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playTone() {
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, errToneURI);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playTone2() {
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, errTone2URI);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity playTone2. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playSuccessVerifiedTone() {
        try {
            stopPlayingTone();

            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, verifiedToneURI);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity playSuccessVerifiedTone. Exception: " + e.getMessage(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at JobsListGroupActivity", "", "", "", "");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }


}

