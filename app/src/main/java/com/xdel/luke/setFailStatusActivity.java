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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Location;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.core.app.JobIntentService;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.MassUpdateOKPwP2_PostData;
import com.xdel.luke.objClasses.Spinner_PostData;
import com.xdel.luke.objClasses.Spinner_PostDataFail;
import com.xdel.luke.objClasses.Spinner_PostItemAdapter;
import com.xdel.luke.objClasses.Spinner_PostItemAdapterFail;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCallCheck;
import com.xdel.luke.objClasses.objCheckedItems;
import com.xdel.luke.objClasses.objConvertedBitmapToBase64;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objNumOfCallOutPast15min;
import com.xdel.luke.objClasses.objStatusCodes;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//import android.support.v7.app.ActionBarActivity;

//public class setFailStatusActivity extends ActionBarActivity {
public class setFailStatusActivity extends AppCompatActivity {
    static boolean errored = false;
    public static int ddlSelectedIndex = 0;
    public static int ddlSelectedFailureReasonIndex = 0;
    public static String selectedStatusCodesIDX = "";
    public static String selectedFailureReasonStr = "";
    public static String reasons = "";
    public static ArrayList<String> imagePaths;
    public static ArrayList<CustomGallery> dataT;
    public static boolean isFromSetFailSatus = false;
    public static MassUpdateOKPwP2_PostData MassUpdateOKPwP2PostData;
    public static String fromPage = "";
    public static int accessFromDirection = 0;
    private static String updatemodeForFailure = "-1";

    Context context;
    private long JobsIDX;
    ConnectionDetector cd;
    //private String regid;
    private List<objStatusCodes> listobjStatusCodesG;
    TextView lblfailuretype, txtFailurereason;
    private Spinner ddlFailureType;
    private Spinner ddlFailureReason;
    private Button btnViewImg, btnCamera, btnCancel, btnSubmit, btnAtt, btnNext, btnCallLogImg;
    private TextView lblSelectedStatusCodesIDX;
    private TextView lblSelectedFailureReasonIDX;
    private TextView lblRequirePhotoNote;
    private TextView lblcallAttemptsCount;
    private TextView lblFlag;
    private TextView lblAllValidTelNums;
    private EditText txtReason;
    private String StatusCodesFullJsonStr;
    objStatusCodes[] fullStatusCodesAL;
    objStatusCodes PerobjStatusCode;
    String[] requiredPhotoStatusCodesIDXal = new String[]{"67", "68", "70", "75", "78", "80", "108", "109", "113", "116", "117", "118", "119", "120", "121",
            "122", "123", "126", "127", "128", "159", "162", "163", "164", "167", "168", "180", "181", "183", "184", "186", "212", "221", "222", "238", "255", "257", "372"};
    private boolean dropdownloadedSuccessful = true;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    private int catType;
    private int acatType = -1;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    private ProgressDialog pd;
    private ProgressDialog pdFT;
    Bitmap bitmap = null;
    Bitmap bitmap2 = null;
    private MediaPlayer player;
    String langType = "en";
    private ProgressDialog progressDoalog;
    NfcAdapter mNfcAdapter;
    private int CannotTakePhoto;
    //AllCannotTakePhoto: 0 == 0 job cannot take photo, 1 == some cannot take photo, 2 == all cannot take photo
    private TextChangeListener tc;
    private ddlFailureReasonOnSelectedListener frsl;
    TextView lblIsDefaultVRPpage;
    private boolean CallLogImgTakenBool = false;
    private boolean normalImgTakenBool = false;
    private String failPart2FromPart1Bool = "";


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

        setContentView(R.layout.activity_set_fail_status);

        lblAllValidTelNums = (TextView) findViewById(R.id.lblAllValidTelNums);
        /*lblFlag = (TextView) findViewById(R.id.lblFlag);
        lblcallAttemptsCount = (TextView) findViewById(R.id.lblcallAttemptsCount);*/
        lblfailuretype = (TextView) findViewById(R.id.lblfailuretype);
        txtFailurereason = (TextView) findViewById(R.id.txtFailurereason);
        txtFailurereason.setText(myApp.getAppContext().getString(R.string.lblFailureReason));
        lblRequirePhotoNote = (TextView) findViewById(R.id.lblRequirePhotoNote);
        ddlFailureType = (Spinner) findViewById(R.id.ddlFailureType);
        ddlFailureReason = (Spinner) findViewById(R.id.ddlFailureReason);
        btnAtt = (Button) findViewById(R.id.btnAtt);
        btnAtt.setText(myApp.getAppContext().getString(R.string.buttontext_browse));
        btnCallLogImg = (Button) findViewById(R.id.btnCallLogImg);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnCamera.setText(myApp.getAppContext().getString(R.string.buttontext_cam));
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setText(myApp.getAppContext().getString(R.string.buttontext_cancel));
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setText(myApp.getAppContext().getString(R.string.buttontext_submit));
        btnNext = (Button) findViewById(R.id.btnNext);
        txtReason = (EditText) findViewById(R.id.txtReason);
        lblSelectedStatusCodesIDX = (TextView) findViewById(R.id.lblSelectedStatusCodesIDX);
        lblSelectedFailureReasonIDX = (TextView) findViewById(R.id.lblSelectedFailureReasonIDX);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));
        pdFT = new ProgressDialog(this);
        pdFT.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdFT.setCanceledOnTouchOutside(false);
        pdFT.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingFailureTypeCodes));
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        try {
            String IDVerificationVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "IDVerification");
            if (IDVerificationVal != null && !IDVerificationVal.isEmpty() && !IDVerificationVal.equals("") && IDVerificationVal != "" && accessFromDirection == 1) {
                accessFromDirection = 0;
                //objGlobalMethods.showDialog(this, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.no_cust_id_photo)).show();
                myApp.currentActivity = this;
                objGlobalMethods.showCustomCanCannotMsgPrompt("", myApp.getAppContext().getString(R.string.no_cust_id_photo), null, null);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity onCreate. Exception: " + e.getMessage(), "", "", "", "");
        }

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //int acatType = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
            getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        }
        //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        myApp.currentActivity = this;
        context = myApp.getAppContext();


        acatType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        if (acatType == 0) {
            setTitle(myApp.getAppContext().getString(R.string.title_activity_FP));
        } else if (acatType == 2) {
            setTitle(myApp.getAppContext().getString(R.string.title_activity_FD));
        }

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
            objGlobalMethods.writeToFile("setFailStatusActivity !BTEnabled", "", "", "", "");
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
            //objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity onResume mNfcAdapter portion. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
        }

        objGlobalMethods.setGCMPrefs(GcmPreferences, "mChildCheckStates2PrefStr", "");
        JobsHandler.resetrePositionedGroupItems(GcmPreferences);
        JobsHandler.checkForceLogOut();

        lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

        cd = new ConnectionDetector(null);
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
            // stop executing code by return
            //return;
        }

        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
        username = UsernamePwd[0];
        password = UsernamePwd[1];
        useridx = UsernamePwd[2];
        ishomesuser = UsernamePwd[3];

        if (username.equals("") && password.equals("")) {
            BackToLoginPage();
        }

        //String RegID = objGlobalMethods.getRegistrationIdOnly();
        String CannotTakePhotoStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto");
        CannotTakePhoto = CannotTakePhotoStr.equals("") ? 0 : Integer.parseInt(CannotTakePhotoStr);
        lblRequirePhotoNote.setText(CannotTakePhoto == 3 ? myApp.getAppContext().getString(R.string.alert_allcannottakephoto).toUpperCase()
                : CannotTakePhoto == 2 ? myApp.getAppContext().getString(R.string.alert_somecannottakephoto).toUpperCase()
                : CannotTakePhoto == 1 ? myApp.getAppContext().getString(R.string.alert_cannottakephoto).toUpperCase() : "");

        if (MassUpdateOKPwP2PostData != null) {
            btnSubmit.setText(myApp.getAppContext().getString(R.string.buttontext_ok));
            btnNext.setVisibility(View.GONE);
        } else {
            btnSubmit.setText(myApp.getAppContext().getString(R.string.buttontext_submit));
            updatemodeForFailure = objGlobalMethods.getGCMPrefs(GcmPreferences, "updatemodeForFailure");
            if (updatemodeForFailure.equals("") || updatemodeForFailure.equals("-1") || updatemodeForFailure.equals("1")) {
                btnSubmit.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.GONE);
            } else if (!updatemodeForFailure.equals("") && !updatemodeForFailure.equals("-1") && updatemodeForFailure.equals("0")) {
                btnNext.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.GONE);
            } else if (!updatemodeForFailure.equals("") && !updatemodeForFailure.equals("-1") && updatemodeForFailure.equals("2")) {
                btnNext.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.GONE);
            }
        }

        if (langType.equals("en") || langType.equals("")) {
            StatusCodesFullJsonStr = getStoredStatusCodesEnZH("en");
        } else if (langType.equals("zh")) {
            StatusCodesFullJsonStr = getStoredStatusCodesEnZH("zh");
        }

        String[] prefName = new String[]{"isCallLog", "failactivityisresume"};
        String[] prefvalue = new String[]{"", "t"};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        /*objGlobalMethods.setGCMPrefs(GcmPreferences, "isCallLog", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "failactivityisresume", "t");*/

        String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
        lblAllValidTelNums.setText(AllValidTelNums);
        CheckReqCallLogImg2(AllValidTelNums);
        GcmPreferences = objGlobalMethods.getGcmPreferences();

        failPart2FromPart1Bool = "f";
        failPart2FromPart1Bool = objGlobalMethods.getGCMPrefs(GcmPreferences, "failPart2FromPart1Bool");
        CallLogImgTakenBool = CallLogImgTaken();
        normalImgTakenBool = CannotTakePhoto > 0 ? true : normalImgTaken();

        if (!StatusCodesFullJsonStr.equals("")) {
            loadddlFailureType(acatType);
            if (dropdownloadedSuccessful) {
                if (MassUpdateOKPwP2.isFailFromMassUpdateOKPwP2) {
                    if (CannotTakePhoto > 0) {
                        ctrlSettings.enableDisableCommonButtonWithStyles(true, btnAtt);
                        if (selectedStatusCodesIDX == null || selectedStatusCodesIDX.equals("")) {
                            ddlSelectedIndex = 0;
                            ddlFailureType.setSelection(0);
                        } else {
                            ddlFailureType.setSelection(ddlSelectedIndex);
                        }
                    } else {
                        if (Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0)) {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnSubmit);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnNext);
                            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnAtt);
                            if (selectedStatusCodesIDX == null || selectedStatusCodesIDX.equals("")) {
                                ddlSelectedIndex = 0;
                                ddlFailureType.setSelection(0);
                            } else {
                                ddlFailureType.setSelection(ddlSelectedIndex);
                            }
                        } else {
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnAtt);
                            if (selectedStatusCodesIDX == null || selectedStatusCodesIDX.equals("")) {
                                ddlSelectedIndex = 0;
                                ddlFailureType.setSelection(0);
                            } else {
                                ddlFailureType.setSelection(ddlSelectedIndex);
                            }
                        }
                    }
                } else {
                    if (CannotTakePhoto > 0) {
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnAtt);
                        if (selectedStatusCodesIDX == null || selectedStatusCodesIDX.equals("")) {
                            ddlSelectedIndex = 0;
                            ddlFailureType.setSelection(0);
                        } else {
                            ddlFailureType.setSelection(ddlSelectedIndex);
                        }
                    } else {
                        if (Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0)) {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnSubmit);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnNext);
                            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnAtt);
                            if (selectedStatusCodesIDX == null || selectedStatusCodesIDX.equals("")) {
                                ddlSelectedIndex = 0;
                                ddlFailureType.setSelection(0);
                            } else {
                                ddlFailureType.setSelection(ddlSelectedIndex);
                            }
                        } else {
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnSubmit);
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnNext);
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnAtt);
                            if (selectedStatusCodesIDX == null || selectedStatusCodesIDX.equals("")) {
                                ddlFailureType.setSelection(0);
                            } else {
                                ddlFailureType.setSelection(ddlSelectedIndex);
                            }
                        }
                    }
                }
            } else {
                objGlobalMethods.writeToFile(myApp.getAppContext().getString(R.string.alert_nodataRtnJobInfo), "", "", "", "");
                showErrorAlert(myApp.getAppContext().getString(R.string.alert_nodataRtnJobInfo));
            }

            txtReason.setText(reasons);

        } else {
            objGlobalMethods.writeToFile(myApp.getAppContext().getString(R.string.alert_nodataRtnJobInfo), "", "", "", "");
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_nodataRtnJobInfo));
        }
    }

    private int getPreDeterminedStatusCodeIDX() {
        if (GcmPreferences == null) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
        }
        int PreDeterminedStatusCodeIDX = 0;
        String PreDeterminedStatusCodeIDXstr = "";
        try {
            //commented cuz not failing directly from checklist

            /*PreDeterminedStatusCodeIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "PreDeterminedStatusCodeIDX");
            PreDeterminedStatusCodeIDX = !objGlobalMethods.IsEmptyString(PreDeterminedStatusCodeIDXstr) ? Integer.parseInt(PreDeterminedStatusCodeIDXstr) : 0;*/
        } catch (Exception e) {
            PreDeterminedStatusCodeIDX = 0;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity getPreDeterminedStatusCodeIDX. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return PreDeterminedStatusCodeIDX;
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDoalog != null) {
            if (progressDoalog.isShowing()) {
                progressDoalog.dismiss();
            }
        }
        progressDoalog = null;

        if (pdFT != null) {
            if (pdFT.isShowing()) {
                pdFT.dismiss();
            }
        }
        pdFT = null;

        if (pd != null) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
        pd = null;

        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();

        lblfailuretype = null;
        txtFailurereason = null;
        ddlFailureType = null;
        ddlFailureReason = null;
        btnCamera = null;
        btnCancel = null;
        btnSubmit = null;
        btnAtt = null;
        btnNext = null;
        btnCallLogImg = null;
        lblSelectedStatusCodesIDX = null;
        lblRequirePhotoNote = null;
        lblAllValidTelNums = null;
        txtReason = null;
        GcmPreferences = null;
        UpdateJobInfoPreferences = null;
        mNfcAdapter = null;
        player = null;

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
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);

            txtReason.removeTextChangedListener(tc);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void BackToLoginPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        MassUpdateOKPwP2PostData = null;
        MassUpdateOKPwP2.massupdateokpwp2PostDataArr = null;
        Camera5Activity.mCurrentPhotoPath = null;
        bitmap = null;
        String[] prefName = new String[]{"isFromDashboardSub", objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, "selectedRouteAddressJobsIDXs", "updatemodeForFailure",
                "wifistate", "leftofc"};
        String[] prefvalue = new String[]{"", "-1", "", "", "-1", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        Intent parentIntent;
        parentIntent = new Intent(setFailStatusActivity.this, MainActivity.class);
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        ddlSelectedFailureReasonIndex = 0;
        selectedFailureReasonStr = "";
        ddlSelectedIndex = 0;
        selectedStatusCodesIDX = "";
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(2);
        }
        FCMservice.notiNotRead = false;
        JobsHandler.clearGPSQue(LocationPrefs);
        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(1);
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

    private Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);
        builder.setCancelable(false);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        return builder.show();
    }

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
                    Date dt = new Date();
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showMobileNetworkSettingDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
                        Date dt = new Date();
                        objGlobalMethods.writeToFile("ActivityNotFoundException at setFailStatusActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    Date dt = new Date();
                    objGlobalMethods.writeToFile("ActivityNotFoundException at setFailStatusActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at setFailStatusActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at setFailStatusActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showErrorAlert(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodataRtnJobInfo));
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                Camera5Activity.mCurrentPhotoPath = null;
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                MassUpdateOKPwP2PostData = null;
                MassUpdateOKPwP2.massupdateokpwp2PostDataArr = null;
                ddlSelectedFailureReasonIndex = 0;
                selectedFailureReasonStr = "";
                ddlSelectedIndex = 0;
                selectedStatusCodesIDX = "";
                String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr"};
                String[] prefvalue = new String[]{"", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
                Intent i;
                i = new Intent(setFailStatusActivity.this, JobInfoActivity.class);
                JobInfoActivity.accessFromDirection = 0;
                com.xdel.luke.MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showErrorAlertNreturnToDashboard(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                MassUpdateOKPwP2PostData = null;
                MassUpdateOKPwP2.massupdateokpwp2PostDataArr = null;
                Camera5Activity.mCurrentPhotoPath = null;
                CustomGalleryActivity.imagePaths = null;
                CustomGalleryActivity.dataT = null;
                ddlSelectedFailureReasonIndex = 0;
                selectedFailureReasonStr = "";
                ddlSelectedIndex = 0;
                selectedStatusCodesIDX = "";
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }

                String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "isFromDashboardSub",
                        objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, "selectedRouteAddressJobsIDXs"};
                String[] prefvalue = new String[]{"", "", "", "", "", "-1", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");

                Intent i;
                com.xdel.luke.MainActivity.uponLogin = 0;
                //i = new Intent(setFailStatusActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(setFailStatusActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(i);
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showErrorAlertNreturnToMassUpdateOKPwP2(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                MassUpdateOKPwP2PostData = null;
                Camera5Activity.mCurrentPhotoPath = null;
                CustomGalleryActivity.imagePaths = null;
                CustomGalleryActivity.dataT = null;
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
                ddlSelectedFailureReasonIndex = 0;
                selectedFailureReasonStr = "";
                ddlSelectedIndex = 0;
                selectedStatusCodesIDX = "";
                objGlobalMethods.setGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr", "");

                Intent i;
                com.xdel.luke.MainActivity.uponLogin = 0;
                i = new Intent(setFailStatusActivity.this, MassUpdateOKPwP2.class);
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
        getMenuInflater().inflate(R.menu.menu_set_fail_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent parentIntent;

        switch (item.getItemId()) {
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String failPart2FromPart1Bool = objGlobalMethods.getGCMPrefs(GcmPreferences, "failPart2FromPart1Bool");
        if (failPart2FromPart1Bool.equals("t")) {
            objGlobalMethods.writeToFile("Accidental/Attempt to cancel Fail Job process. Block.", "", "", "", "");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            TextView at = new TextView(myApp.getAppContext());
            at.setText(myApp.getAppContext().getString(R.string.attention));
            at.setTextSize(16);
            at.setTypeface(null, Typeface.BOLD);
            at.setTextColor(ContextCompat.getColor(myApp.getAppContext(), R.color.colorPrimary));
            at.setPadding(75, 20, 0, 20);
            at.setBackgroundColor(Color.WHITE);
            builder.setCustomTitle(at);
            builder.setMessage(myApp.getAppContext().getString(R.string.atttemptcancelfailp2prompt));

            builder.setNeutralButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    AlertDialog.Builder b = new AlertDialog.Builder(myApp.currentActivity);
                    b.setCancelable(false);
                    TextView tv = new TextView(myApp.getAppContext());
                    tv.setText(myApp.getAppContext().getString(R.string.attention));
                    tv.setTextSize(16);
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setTextColor(ContextCompat.getColor(myApp.getAppContext(), R.color.colorPrimary));
                    tv.setPadding(75, 20, 0, 20);
                    tv.setBackgroundColor(Color.WHITE);
                    b.setCustomTitle(tv);
                    b.setMessage(myApp.getAppContext().getString(R.string.cancelp2failreminderalert));
                    //back();
                    b.setNeutralButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            objGlobalMethods.writeToFile("GC chose to continue FAIL PART 2.", "", "", "", "");
                            dialog.cancel();
                        }
                    });

                    b.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            back();
                        }
                    });

                    AlertDialog al2 = b.create();
                    al2.show();
                }
            });

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_continue), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    objGlobalMethods.writeToFile("GC chose to continue FAIL PART 2.", "", "", "", "");
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } else {
            back();
        }
    }

    private void back() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        String failPart2FromPart1Bool = objGlobalMethods.getGCMPrefs(GcmPreferences, "failPart2FromPart1Bool");
        try {
            objGlobalMethods.writeToFile("Fail job process cancelled by GC.", "", "", "", "");
            isFromSetFailSatus = false;
            accessFromDirection = 0;
            setDeliveredMassStatusActivity.isFromMassSetOKP = false;
            setDeliveredStatusActivity.isFromSetOKP = false;
            dataT = null;
            imagePaths = null;

            Intent parentIntent = null;
            Camera5Activity.mCurrentPhotoPath = null;

            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }

            reasons = "";
            ddlSelectedFailureReasonIndex = 0;
            selectedFailureReasonStr = "";
            ddlSelectedIndex = 0;
            selectedStatusCodesIDX = "";

            /*String[] prefNameX = new String[]{"searchedval", "ScannedCodeStrsPrefStr", "PreDeterminedStatusCodeIDX", "failreasonchanged", "failactivityisresume"};
            String[] prefvalueX = new String[]{"", "", "", "", ""};*/
            String[] prefNameX = new String[]{"searchedval", "ScannedCodeStrsPrefStr", "failreasonchanged", "failactivityisresume"};
            String[] prefvalueX = new String[]{"", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefNameX, prefvalueX);

            if (fromPage != null && fromPage.equals("checklist")) {
                fromPage = null;
                parentIntent = new Intent(this, CheckListActivity.class);
            } else if (MassUpdateOKPwP2PostData != null) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "2");
                MassUpdateOKPwP2PostData = null;
                parentIntent = new Intent(this, MassUpdateOKPwP2.class);
            } else {
                MassUpdateOKPwP2PostData = null;
                MassUpdateOKPwP2.massupdateokpwp2PostDataArr = null;
                String objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);

                String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX,
                        "massupdateokpwp2PostDataArr", "objPendingJobStatusUpdatesobjALPrefStr", "objPendingJobPhotoUpdatesobjALPrefStr"};
                String[] prefvalue = new String[]{"", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                String[] prefName0 = new String[]{"objJobInfoBringtoUpdateCODListJsonStr", "mutlipleCODjobs", "ojisPage"};
                String[] prefvalue0 = new String[]{"", "", ""};
                objGlobalMethods.setGCMPrefsMass(UpdateJobInfoPreferences, prefName0, prefvalue0);

                //String failPart2FromPart1Bool = objGlobalMethods.getGCMPrefs(GcmPreferences, "failPart2FromPart1Bool");
                if (failPart2FromPart1Bool.equals("t")) {
                    String[] prefName1 = new String[]{"failPart2FromPart1Bool", objGlobalVariables.categoryTypePrefName, "hfJobsInfoArrJsonStr", "desPostal", "LAT", "LNG", "desAddress"};
                    String[] prefvalue1 = new String[]{"f", "2", "", "", "", "", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName1, prefvalue1);
                    if (!objGlobalMethods.getGCMPrefs(GcmPreferences, "isFromDashboardSub").isEmpty()) {
                        //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                        parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                        JobInfoActivity.accessFromDirection = 0;
                    } else {
                        parentIntent = new Intent(this, AddressListNewActivity.class);
                        JobInfoActivity.accessFromDirection = 0;
                    }
                } else if (!objCheckedItemsJsonStr.equals("")) {
                    parentIntent = new Intent(this, JobsListGroupActivity.class);
                    JobInfoActivity.accessFromDirection = 0;
                } else {
                    parentIntent = new Intent(this, JobInfoActivity.class);
                    JobInfoActivity.accessFromDirection = 0;
                }
            }
            com.xdel.luke.MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            this.finish();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity back. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private String getStoredStatusCodesEnZH(String type) {
        /*final SharedPreferences prefs = objGlobalMethods.getGcmPreferences();
        String fullStatusCodesJsonStr = "";
        if (type.equals("en") || type.equals("")) {
            fullStatusCodesJsonStr = prefs.getString(objGlobalVariables.storedStatusCodesPrefNameEN, "");
        } else if (type.equals("zh")) {
            fullStatusCodesJsonStr = prefs.getString(objGlobalVariables.storedStatusCodesPrefNameZH, "");
        }*/

        if (GcmPreferences == null)
            GcmPreferences = objGlobalMethods.getGcmPreferences();
        String fullStatusCodesJsonStr = "";
        if (type.equals("en") || type.equals("")) {
            fullStatusCodesJsonStr = GcmPreferences.getString(objGlobalVariables.storedStatusCodesPrefNameEN, "");
        } else if (type.equals("zh")) {
            fullStatusCodesJsonStr = GcmPreferences.getString(objGlobalVariables.storedStatusCodesPrefNameZH, "");
        }

        return fullStatusCodesJsonStr;
    }

    private void loadddlFailureType(int acatType) {
        try {
            Gson gson = new Gson();
            fullStatusCodesAL = gson.fromJson(StatusCodesFullJsonStr, objStatusCodes[].class);
            ArrayList<Spinner_PostDataFail> SpinnerPostDataArr = new ArrayList<Spinner_PostDataFail>();
            Spinner_PostDataFail postdata;
            objStatusCodes obj;
            listobjStatusCodesG = getSpecificFailureTypeStatusCodes(fullStatusCodesAL, acatType);
            if (listobjStatusCodesG != null && listobjStatusCodesG.size() > 0) {
                postdata = new Spinner_PostDataFail(myApp.getAppContext().getString(R.string.spinnerSelect), "", "", "", 0);
                SpinnerPostDataArr.add(postdata);

                for (int i = 0; i <= listobjStatusCodesG.size() - 1; i++) {
                    obj = listobjStatusCodesG.get(i);
                    postdata = new Spinner_PostDataFail(obj.Description, "", String.valueOf(obj.IDX), obj.Remarks, obj.Flag);
                    SpinnerPostDataArr.add(postdata);
                }

                Spinner_PostItemAdapterFail adapter = new Spinner_PostItemAdapterFail(this, SpinnerPostDataArr);
                ddlFailureType.setAdapter(adapter);
                ddlFailureType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            processFailureTypeSelection(view, position, null);
                        } catch (Exception e) {
                            String s = JobsHandler.getStackTraceString(e);
                            objGlobalMethods.writeToFile("Exception at setFailStatusActivity ddlFailureType.setOnItemSelectedListener. btnSubmit is enabled. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnSubmit);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnNext);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            } else if (listobjStatusCodesG == null || listobjStatusCodesG.size() == 0) {
                dropdownloadedSuccessful = false;
            }

        } catch (Exception e) {
            dropdownloadedSuccessful = false;
        }
    }

    private void processFailureTypeSelection(View view, int position, Boolean setfailactivityisresume) {
        try {
            if (view == null) {
                view = ddlFailureType.getSelectedView();
            }
            if (view != null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                selectedStatusCodesIDX = ((TextView) view.findViewById(R.id.lblStatusCodesIDX)).getText().toString();
                TextView lblStatusCodesDesc = (TextView) view.findViewById(R.id.lblStatusCodesDesc);
                TextView lblFlag = (TextView) view.findViewById(R.id.lblFlag);
                int Flag = lblFlag.getText().toString().isEmpty() ? 0 : objGlobalMethods.convertStringToInteger(lblFlag.getText().toString());
                String failreasonchanged = objGlobalMethods.getGCMPrefs(GcmPreferences, "failreasonchanged");
                String failactivityisresume = objGlobalMethods.getGCMPrefs(GcmPreferences, "failactivityisresume");
                if (setfailactivityisresume != null && setfailactivityisresume)
                    failactivityisresume = "t";

                //int acatType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                acatType = acatType == -1 ? objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences) : acatType;
                Context ctx = myApp.getAppContext();
                if (objGlobalMethods.IsEmptyString(failPart2FromPart1Bool))
                    failPart2FromPart1Bool = objGlobalMethods.getGCMPrefs(GcmPreferences, "failPart2FromPart1Bool");
                /*String failPart2FromPart1Bool = "f";
                failPart2FromPart1Bool = objGlobalMethods.getGCMPrefs(GcmPreferences, "failPart2FromPart1Bool");*/
                txtReason.removeTextChangedListener(tc);
                //boolean normalImgTakenBool = CannotTakePhoto > 0 ? true : normalImgTaken();
                boolean normalImgTakenBoolSecondCheck = normalImgTakenBool;
                boolean canSubmit = true;
                boolean needtocall = false;

                if (CannotTakePhoto > 0) {
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnAtt);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnCamera);
                    lblRequirePhotoNote.setText(CannotTakePhoto == 3 ? ctx.getString(R.string.alert_allcannottakephoto).toUpperCase()
                            : CannotTakePhoto == 2 ? ctx.getString(R.string.alert_somecannottakephoto).toUpperCase()
                            : CannotTakePhoto == 1 ? ctx.getString(R.string.alert_cannottakephoto).toUpperCase() : "");
                } else {
                    if (!objGlobalMethods.IsEmptyString(selectedStatusCodesIDX) && selectedStatusCodesIDX.equals("75")) {
                        normalImgTakenBoolSecondCheck = true;
                        lblRequirePhotoNote.setText("");
                        ctrlSettings.enableDisableCommonButtonWithStyles(true, btnAtt);
                        ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCamera);
                        canSubmit = true;
                    } else {
                        lblRequirePhotoNote.setText(!normalImgTakenBoolSecondCheck ? ctx.getString(R.string.proofatlocation) : "");
                        ctrlSettings.enableDisableCommonButtonWithStyles(normalImgTakenBoolSecondCheck ? true : false, btnAtt);
                        ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCamera);
                        canSubmit = normalImgTakenBoolSecondCheck ? true : false;
                    }
                    /*lblRequirePhotoNote.setText(!normalImgTakenBool ? ctx.getString(R.string.proofatlocation) : "");
                    ctrlSettings.enableDisableCommonButtonWithStyles(normalImgTakenBool ? true : false, btnAtt);
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCamera);
                    canSubmit = normalImgTakenBool ? true : false;*/
                }

                if (selectedStatusCodesIDX.equals("")) {
                    lblStatusCodesDesc.setBackgroundColor(Color.RED);
                    lblStatusCodesDesc.setTextColor(Color.WHITE);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnNext);
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCamera);
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCallLogImg);
                    ddlFailureReason.setOnItemSelectedListener(null);
                    loadddlFailureReason(0);
                    selectedFailureReasonStr = "";
                    ddlSelectedFailureReasonIndex = 0;
                    ddlFailureReason.setEnabled(false);
                    lblRequirePhotoNote.setText(ctx.getString(R.string.selectfailuretype));

                    if (!objGlobalMethods.IsEmptyString(failactivityisresume)) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "failactivityisresume", "");
                    }
                } else {
                    lblStatusCodesDesc.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                    lblStatusCodesDesc.setTextColor(Color.WHITE);
                    lblSelectedStatusCodesIDX.setText(selectedStatusCodesIDX);
                    final int prevSelectedIndex = ddlSelectedIndex;
                    ddlSelectedIndex = position;

                    if (selectedStatusCodesIDX.equals("77")) {
                        //temploggertakephoto

                    } else if (failPart2FromPart1Bool.equals("t")) {
                        ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCamera);
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCallLogImg);
                        if (CannotTakePhoto > 0) {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnSubmit);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnNext);
                        } else {
                            List<String> scal = new ArrayList<String>(Arrays.asList(requiredPhotoStatusCodesIDXal));
                            if (scal.contains(selectedStatusCodesIDX)) {
                                if (imagePaths != null && imagePaths.size() > 0 && dataT != null && dataT.size() > 0) {
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnSubmit);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnNext);

                                } else if (imagePaths != null && imagePaths.size() == 0 && dataT != null && dataT.size() == 0) {
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnNext);
                                } else if (imagePaths == null) {
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnNext);
                                }
                            } else {
                                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnSubmit);
                                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnNext);
                            }
                        }
                    } else {
                        boolean reqCallFlag = (Flag & 1) == 1;
                        String noCallMade = objGlobalMethods.getGCMPrefs(GcmPreferences, "noCallMade");
                        String totalnumofcallstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalnumofcall");
                        String reqCallLogImg = objGlobalMethods.getGCMPrefs(GcmPreferences, "reqCallLogImg");
                        int totalnumofcall = totalnumofcallstr.equals("") ? 0 : Integer.parseInt(totalnumofcallstr);
                        //boolean CallLogImgTaken = CallLogImgTaken();

                        if (reqCallFlag && totalnumofcall == 0 && reqCallLogImg.equals("t") && CannotTakePhoto == 0) {
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCallLogImg);
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCamera);
                            lblRequirePhotoNote.setText(acatType == 0 ? ctx.getString(R.string.nocallviasystempip) : ctx.getString(R.string.nocallviasystemdip));
                            canSubmit = false;
                            needtocall = true;
                        } else if (CannotTakePhoto > 0 && reqCallFlag && totalnumofcall >= 0 && noCallMade.equals("t") && !CallLogImgTakenBool && reqCallLogImg.equals("t")) {
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCamera);
                            if (totalnumofcall < 2) {
                                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCallLogImg);
                                lblRequirePhotoNote.setText(acatType == 0 ? ctx.getString(R.string.numcallsmade) + " " + String.valueOf(totalnumofcall) + "\n" + ctx.getString(R.string.takecalllogphotopip2call)
                                        : ctx.getString(R.string.numcallsmade) + " " + String.valueOf(totalnumofcall) + "\n" + ctx.getString(R.string.takecalllogphotodip2call));
                                canSubmit = false;
                                needtocall = true;
                            } else {
                                ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCallLogImg);
                                lblRequirePhotoNote.setText(acatType == 0 ? ctx.getString(R.string.takecalllogphotopip0) : ctx.getString(R.string.takecalllogphotodip0));
                                canSubmit = false;
                                needtocall = true;
                            }
                        } else if (CannotTakePhoto > 0 && reqCallFlag && totalnumofcall > 0 && noCallMade.equals("t") && CallLogImgTakenBool) {
                            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCallLogImg);
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnAtt);
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCamera);
                        } else {
                            ctrlSettings.enableDisableCommonButtonWithStyles(CannotTakePhoto > 0 ? false : true, btnCamera);
                            if (reqCallFlag && totalnumofcall > 0 && noCallMade.equals("t") && reqCallLogImg.equals("t") && !CallLogImgTakenBool) {
                                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCamera);
                                if (totalnumofcall < 2) {
                                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCallLogImg);
                                    lblRequirePhotoNote.setText(acatType == 0 ? ctx.getString(R.string.numcallsmade) + " " + String.valueOf(totalnumofcall) + "\n" + ctx.getString(R.string.takecalllogphotopip2call)
                                            : ctx.getString(R.string.numcallsmade) + " " + String.valueOf(totalnumofcall) + "\n" + ctx.getString(R.string.takecalllogphotodip2call));
                                    canSubmit = false;
                                    needtocall = true;
                                } else {
                                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCallLogImg);
                                    lblRequirePhotoNote.setText(acatType == 0 ? ctx.getString(R.string.takecalllogphotopip0) : ctx.getString(R.string.takecalllogphotodip0));
                                    canSubmit = false;
                                    needtocall = true;
                                }
                            } else if (!reqCallFlag
                                    || (reqCallFlag && totalnumofcall > 0 && noCallMade.equals("t") && CallLogImgTakenBool)
                                    || (reqCallFlag && totalnumofcall > 0 && noCallMade.equals("f"))
                                    || (reqCallFlag && totalnumofcall == 0 && noCallMade.equals("f") && reqCallLogImg.equals("f") && CannotTakePhoto > 0)
                            ) {
                                String AllValidTelNums = lblAllValidTelNums != null ? lblAllValidTelNums.getText().toString() : "";
                                ctrlSettings.enableDisableCommonButtonWithStyles(!objGlobalMethods.IsEmptyString(AllValidTelNums), btnCallLogImg);
                                List<String> scal = new ArrayList<String>(Arrays.asList(requiredPhotoStatusCodesIDXal));
                                ctrlSettings.enableDisableCommonButtonWithStyles(CallLogImgTakenBool || (normalImgTakenBoolSecondCheck && CannotTakePhoto == 0) ? true : false, btnAtt);

                                if (scal.contains(selectedStatusCodesIDX)) {
                                    canSubmit = normalImgTakenBoolSecondCheck ? true : false;
                                } else {
                                    //ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnSubmit);
                                    //ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnNext);
                                }
                            } else {
                                String AllValidTelNums = lblAllValidTelNums != null ? lblAllValidTelNums.getText().toString() : "";
                                ctrlSettings.enableDisableCommonButtonWithStyles(!objGlobalMethods.IsEmptyString(AllValidTelNums), btnCallLogImg);

                            }
                        }
                    }

                    if (canSubmit) {
                        lblRequirePhotoNote.setText("");
                        if (CannotTakePhoto > 0) {
                            lblRequirePhotoNote.setText(CannotTakePhoto == 3 ? ctx.getString(R.string.alert_allcannottakephoto).toUpperCase()
                                    : CannotTakePhoto == 2 ? ctx.getString(R.string.alert_somecannottakephoto).toUpperCase()
                                    : CannotTakePhoto == 1 ? ctx.getString(R.string.alert_cannottakephoto).toUpperCase() : "");
                        }
                    }

                    if (!objGlobalMethods.IsEmptyString(failactivityisresume) && objGlobalMethods.IsEmptyString(failreasonchanged)) { //is resume
                        ddlFailureReason.setOnItemSelectedListener(null);
                        loadddlFailureReason(position);
                        if (ddlFailureReason.isEnabled()) {
                            ddlFailureReason.setSelection(ddlSelectedFailureReasonIndex);
                        }
                        frsl = new ddlFailureReasonOnSelectedListener();
                        ddlFailureReason.setOnItemSelectedListener(frsl);
                        canSubmit = (ddlSelectedFailureReasonIndex > 0 || txtReason.getText().toString().length() >= 6);
                    } else if (objGlobalMethods.IsEmptyString(failactivityisresume)) { //not is resume, can b type change or reason change
                        if (objGlobalMethods.IsEmptyString(failreasonchanged)) {
                            ddlFailureReason.setOnItemSelectedListener(null);
                            loadddlFailureReason(position);
                            if (ddlFailureReason.isEnabled()) {
                                ddlFailureReason.setSelection(ddlSelectedFailureReasonIndex);
                            }
                            frsl = new ddlFailureReasonOnSelectedListener();
                            ddlFailureReason.setOnItemSelectedListener(frsl);
                            canSubmit = (ddlSelectedFailureReasonIndex > 0 || txtReason.getText().toString().length() >= 6);

                            if (selectedStatusCodesIDX.equals("75")) {
                                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.nhna_promptmsg)).show();
                            }
                        } else {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "failreasonchanged", "");
                            if (ddlFailureReason.isEnabled()) {
                                if ((ddlSelectedFailureReasonIndex == 0 && (txtReason.getText().toString().length() < 6) && objGlobalMethods.IsEmptyString(lblRequirePhotoNote.getText().toString()))) {
                                    lblRequirePhotoNote.setText(ctx.getString(R.string.failure_reasons_mandate));
                                    canSubmit = false;
                                } else if (CannotTakePhoto > 0 && ddlSelectedFailureReasonIndex == 0 && (txtReason.getText().toString().length() < 6) && !objGlobalMethods.IsEmptyString(lblRequirePhotoNote.getText().toString()) && !needtocall) {
                                    lblRequirePhotoNote.setText(ctx.getString(R.string.failure_reasons_mandate));
                                    canSubmit = false;
                                } else if (CannotTakePhoto > 0 && ddlSelectedFailureReasonIndex == 0 && (txtReason.getText().toString().length() < 6) && !objGlobalMethods.IsEmptyString(lblRequirePhotoNote.getText().toString()) && needtocall) {
                                    canSubmit = false;
                                }
                            }
                        }
                    }

                    txtReason.removeTextChangedListener(tc);
                    if (ddlFailureReason.isEnabled()) {
                        tc = new TextChangeListener();
                        txtReason.addTextChangedListener(tc);
                    }

                    ctrlSettings.enableDisableSubmitButtonWithStyles(canSubmit, btnSubmit);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(canSubmit, btnNext);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity processFailureTypeSelection. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void CheckReqCallLogImg2(String AllValidTelNums) {
        ArrayList<objNumOfCallOutPast15min> al = null;
        objNumOfCallOutPast15min obj = null;
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        boolean reqCallLog = true;
        int totalnumofcall = 0;
        int numofcall = 0;
        int CallConnected = 0;
        int reqCallLogImg = 0;
        boolean noCallMade = true;
        boolean CallConnectedBoo = false;
        try {
            //al = AllValidTelNums.isEmpty() || AllValidTelNums.equals("") ? null : DAO_Jobs.getNumOfCallOutPast30minArr(AllValidTelNums);
            al = AllValidTelNums.isEmpty() || AllValidTelNums.equals("") ? null : DAO_Jobs.getNumOfCallOutPast15minArr(AllValidTelNums);
            if (al != null && al.size() > 0) {
                //system have valid numbers, gc make at least 1 call via system
                if (al.size() == 1) {
                    obj = al.get(0);
                    numofcall = obj.NumOfCallOutPast15min;
                    reqCallLogImg = obj.reqCallLogImg;
                    CallConnected = obj.CallConnected;
                    totalnumofcall += numofcall;

                    CallConnectedBoo = (CallConnectedBoo || (!CallConnectedBoo && CallConnected > 0)) ? true : false;

                    if (CallConnectedBoo) {
                        noCallMade = false;
                        reqCallLog = false;
                    } else if (reqCallLogImg == 1 && numofcall > 0) {
                        noCallMade = true;
                        reqCallLog = true;
                    } else if (reqCallLogImg == 0 && numofcall > 0) {
                        noCallMade = numofcall < 2 ? true : false;
                        reqCallLog = numofcall <= 2 ? true : false;
                    }
                } else if (al.size() > 1) {
                    for (int i = 0; i <= al.size() - 1; i++) {
                        obj = al.get(i);
                        numofcall = obj.NumOfCallOutPast15min;
                        reqCallLogImg = obj.reqCallLogImg;
                        CallConnected = obj.CallConnected;
                        totalnumofcall += numofcall;

                        CallConnectedBoo = (CallConnectedBoo || (!CallConnectedBoo && CallConnected > 0)) ? true : false;

                        if (CallConnectedBoo) {
                            noCallMade = false;
                            reqCallLog = false;
                            break;
                        } else if (reqCallLogImg == 1) {
                            noCallMade = !noCallMade ? false : true;
                            reqCallLog = !reqCallLog ? false : true;
                        } else if (reqCallLogImg == 0) {
                            noCallMade = false;
                            reqCallLog = false;
                        }
                    }
                }

                String[] prefName = new String[]{
                        "totalnumofcall",
                        "noCallMade",
                        "reqCallLogImg"};
                String[] prefvalue = new String[]{
                        String.valueOf(totalnumofcall),
                        !noCallMade ? "f" : "t",
                        reqCallLog ? "t" : "f"};
                objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);

            } else if (al != null && al.size() == 0) {
                //system have valid numbers, but gc no make any call via system

                String[] prefName = new String[]{
                        "totalnumofcall",
                        "noCallMade",
                        "reqCallLogImg"};
                String[] prefvalue = new String[]{
                        "0",
                        "t",
                        "t"};
                objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
            } else {
                //system does not have any valid numbers

                String[] prefName = new String[]{
                        "totalnumofcall",
                        "noCallMade",
                        "reqCallLogImg"};
                String[] prefvalue = new String[]{
                        "0",
                        "f",
                        "f"};
                objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
            }
        } catch (Exception e) {
            String[] prefName = new String[]{
                    "totalnumofcall",
                    "noCallMade",
                    "reqCallLogImg"};
            String[] prefvalue = new String[]{
                    "2",
                    "f",
                    "f"};
            objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);

            objGlobalMethods.writeToFile("Exception at setFailStatusActivity CheckReqCallLogImg. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private boolean normalImgTaken() {
        boolean asd = false;
        try {
            if (imagePaths != null && imagePaths.size() > 0 && dataT != null && dataT.size() > 0) {
                for (int i = 0; i <= dataT.size() - 1; i++) {
                    if (!dataT.get(i).isCallLog) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity normalImgTaken. Exception: " + e.getMessage(), "", "", "", "");
            asd = true;
        } finally {
            return asd;
        }
    }

    private void loadddlFailureReason(int FailtureTypeSelectedIndex) {
        ArrayList<Spinner_PostData> SpinnerPostDataArr = new ArrayList<Spinner_PostData>();
        Spinner_PostData postdata;
        final objStatusCodes obj;
        String fullRemarks = "";
        String fullRemarksEN = "";

        try {
            if (FailtureTypeSelectedIndex > 0) {
                if (listobjStatusCodesG != null && listobjStatusCodesG.size() > 0) {
                    obj = listobjStatusCodesG.get(FailtureTypeSelectedIndex - 1);
                    fullRemarks = obj.Remarks;
                    fullRemarksEN = obj.RemarksEN;
                    if (!fullRemarks.equals("")) {

                        String[] value_split = fullRemarks.split("\\|");
                        String[] value_splitEN = fullRemarksEN.split("\\|");

                        postdata = new Spinner_PostData(myApp.getAppContext().getString(R.string.failure_reasons_prompt), "", "", "");
                        SpinnerPostDataArr.add(postdata);

                        for (int i = 0; i <= value_split.length - 1; i++) {
                            postdata = new Spinner_PostData(value_split[i], value_splitEN[i], String.valueOf(i), "");
                            SpinnerPostDataArr.add(postdata);
                        }

                        if (SpinnerPostDataArr.size() > 1) {
                            ddlFailureReason.setEnabled(true);
                        } else {
                            ddlFailureReason.setEnabled(false);
                        }
                    } else {
                        postdata = new Spinner_PostData(myApp.getAppContext().getString(R.string.failure_no_reasons_prompt), "", "", "");
                        SpinnerPostDataArr.add(postdata);
                        ddlFailureReason.setEnabled(false);
                    }
                } else {
                    postdata = new Spinner_PostData(myApp.getAppContext().getString(R.string.failure_no_reasons_prompt), "", "", "");
                    SpinnerPostDataArr.add(postdata);
                    ddlFailureReason.setEnabled(false);
                }
            } else {
                postdata = new Spinner_PostData(myApp.getAppContext().getString(R.string.failure_no_reasons_prompt), "", "", "");
                SpinnerPostDataArr.add(postdata);
                ddlFailureReason.setEnabled(false);
            }

            Spinner_PostItemAdapter adapter = new Spinner_PostItemAdapter(this, SpinnerPostDataArr);

            ddlFailureReason.setAdapter(adapter);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity loadddlFailureReason. Exception: " + e.toString(), "", "", "", "");
            postdata = new Spinner_PostData(myApp.getAppContext().getString(R.string.failure_reasons_prompt), "", "", "");
            SpinnerPostDataArr.add(postdata);
            Spinner_PostItemAdapter adapter = new Spinner_PostItemAdapter(this, SpinnerPostDataArr);
            ddlFailureReason.setAdapter(adapter);
            ddlFailureReason.setEnabled(false);
        }
    }

    private class ddlFailureReasonOnSelectedListener implements AdapterView.OnItemSelectedListener {
        public ddlFailureReasonOnSelectedListener() {

        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Boolean setfailactivityisresume = null;
            try {
                ddlSelectedFailureReasonIndex = position;
                //ddlSelectedFailureReasonIndex = position >= ddlSelectedFailureReasonIndex ? position : ddlSelectedFailureReasonIndex;
                TextView lblStatusCodesDesc = (TextView) view.findViewById(R.id.lblStatusCodesDesc);
                if (position == 0) {
                    selectedFailureReasonStr = "";
                } else {
                    lblStatusCodesDesc.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                    lblStatusCodesDesc.setTextColor(Color.WHITE);
                    selectedFailureReasonStr = ((TextView) view.findViewById(R.id.lblStatusCodesDescEN)).getText().toString();
                }

                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                String failactivityisresume = objGlobalMethods.getGCMPrefs(gcmp, "failactivityisresume");
                if (!objGlobalMethods.IsEmptyString(failactivityisresume)) { //is resume

                    String[] prefName = new String[]{"failreasonchanged", "failactivityisresume"};
                    String[] prefvalue = new String[]{"", ""};
                    objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
                    setfailactivityisresume = true;
                    /*objGlobalMethods.setGCMPrefs(gcmp, "failreasonchanged", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "failactivityisresume", "");*/
                } else {

                    String[] prefName = new String[]{"failreasonchanged", "failactivityisresume"};
                    String[] prefvalue = new String[]{"t", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    /*objGlobalMethods.setGCMPrefs(gcmp, "failreasonchanged", "t");
                    objGlobalMethods.setGCMPrefs(gcmp, "failactivityisresume", "");*/
                }
                processFailureTypeSelection(null, ddlSelectedIndex, setfailactivityisresume);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at setFailStatusActivity ddlFailureReasonOnSelectedListener onItemSelected. btnSubmit is enabled. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnSubmit);
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnNext);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class TextChangeListener implements TextWatcher {

        public TextChangeListener() {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                /*boolean enabled = !requireSubFailSelection();
                ctrlSettings.enableDisableSubmitButtonWithStyles(enabled, btnSubmit);
                ctrlSettings.enableDisableSubmitButtonWithStyles(enabled, btnNext);
                if (enabled) {
                    lblRequirePhotoNote.setText("");
                }*/
                processFailureTypeSelection(null, ddlSelectedIndex, true);
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at setFailStatusActivity TextChangeListener. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private List<objStatusCodes> getSpecificFailureTypeStatusCodes(objStatusCodes[] afullStatusCodesAL, int acatType) {
        if (GcmPreferences == null)
            GcmPreferences = objGlobalMethods.getGcmPreferences();
        List<objStatusCodes> listobjStatusCodes = new ArrayList<objStatusCodes>();
        int PreDeterminedStatusCodeIDX = getPreDeterminedStatusCodeIDX();
        String isLockerJob = "";
        boolean isLockerJobB = false;
        boolean FailAsND = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "FailAsND"));
        String CNIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "CNIDX");
        long CNIDX = !objGlobalMethods.IsEmptyString(CNIDXstr) ? Long.parseLong(CNIDXstr) : 0;
        boolean isPCDream = JobsHandler2.isPCDreamFulfilmentCNDIX(CNIDX);
        boolean isEarlierThanDeliveryStartTime = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isEarlierThanDeliveryStartTime"));
        boolean isStandardRunJob = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isStandardRunJob"));

        /*String DatesALJson = objGlobalMethods.getGCMPrefs(GcmPreferences, "DatesALJson");
        Date[] DatesAL = null;
        try {
            DatesAL = objGlobalMethods.IsEmptyString(DatesALJson) ? null : new Gson().fromJson(DatesALJson, Date[].class);
            if (DatesAL != null && DatesAL.length >= 5) {
                boolean isEarlierThanDeliveryStartTime2 = isEarlierThanDeliveryStartTime;
                if (acatType == 2 && !isStandardRunJob)
                    isEarlierThanDeliveryStartTime2 = JobsHandler2.attemptTimeIsEarlierThanWindow(acatType, DatesAL[0], DatesAL[3], DatesAL[5], DatesAL[4]);
                isEarlierThanDeliveryStartTime = isEarlierThanDeliveryStartTime2;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity getSpecificFailureTypeStatusCodes converting DatesAL. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }*/

        if (FailAsND) {
           /* String remarks = (langType.equals("zh")) ? "不能执行这份工作" : "CANNOT DELIVER";
            objStatusCodes sc = new objStatusCodes();
            sc.setIDX(77);
            sc.setStatusCode("ND");
            sc.setDescription("NOT DELIVERED");
            sc.setRemarks(remarks);
            sc.setRemarksEN("NOT DELIVERED");*/

            String remarks = (langType.equals("zh")) ? "温度不合规" : "TEMPERATURE NOT COMPLIANT";
            objStatusCodes sc = new objStatusCodes();
            sc.setIDX(77);
            sc.setStatusCode("ND");
            sc.setDescription((langType.equals("zh")) ? "温度不合规" : "TEMPERATURE NOT COMPLIANT"); //TEXT FOR SELECTED FAIL STATUS FOR ND
            sc.setRemarks(remarks);
            sc.setRemarksEN("TEMPERATURE NOT COMPLIANT");


            listobjStatusCodes.add(sc);
        } else if (afullStatusCodesAL != null && afullStatusCodesAL.length > 0) {
            try {

                //catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                isLockerJob = objGlobalMethods.getGCMPrefs(GcmPreferences, "isLockerJob");
                isLockerJobB = !objGlobalMethods.IsEmptyString(isLockerJob);
                if (acatType == 0) {
                    //status codes for FP showing in PIP
                    for (int i = 0; i <= afullStatusCodesAL.length - 1; i++) {
                        PerobjStatusCode = afullStatusCodesAL[i];

                        if (isLockerJobB) {
                            if (PerobjStatusCode.DriverAffected.equals("1") && PerobjStatusCode.CodeType.equals("2") && PerobjStatusCode.IDX == 221) {
                                listobjStatusCodes.add(PerobjStatusCode);
                            }
                        } else {
                            if (PerobjStatusCode.DriverAffected.equals("1") && PerobjStatusCode.CodeType.equals("2") && PerobjStatusCode.IDX != 221) {
                                listobjStatusCodes.add(PerobjStatusCode);
                            }
                        }

                        /*if (PerobjStatusCode.DriverAffected.equals("1") && PerobjStatusCode.CodeType.equals("2")) {
                            listobjStatusCodes.add(PerobjStatusCode);
                        }*/
                    }
                    lblfailuretype.setText(myApp.getAppContext().getString(R.string.lblFPfailuretype));
                } else if (acatType == 2) {
                    //status codes for FD showing in DIP
                    String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                    boolean AllValidTelNumsIsEmpty = objGlobalMethods.IsEmptyString(AllValidTelNums);
                    boolean isDVatHigherToleranceBool = JobsHandler.isDVatHigherTolerance();
                    boolean hasStatusCodeIDX75 = false;
                    for (int i = 0; i <= afullStatusCodesAL.length - 1; i++) {
                        PerobjStatusCode = afullStatusCodesAL[i];
                        hasStatusCodeIDX75 = hasStatusCodeIDX(listobjStatusCodes, 75);
                        if (!isStandardRunJob && isEarlierThanDeliveryStartTime && (PerobjStatusCode.IDX == 75 || PerobjStatusCode.IDX == 78 || PerobjStatusCode.IDX == 68)) {
                            //do not include NH and NH-NA and Closed Upon Arrival at Delivery Location
                        } else if (!isStandardRunJob && isDVatHigherToleranceBool && AllValidTelNumsIsEmpty && (PerobjStatusCode.IDX == 75 || PerobjStatusCode.IDX == 78 || PerobjStatusCode.IDX == 68)) {
                            //do not include NH and NH-NA and Closed Upon Arrival at Delivery Location
                            //should include isDVatHigherToleranceBool in this if statement, to test on 59 telok blanga job w/o contact number
                        } else if (!isStandardRunJob && isDVatHigherToleranceBool && PerobjStatusCode.IDX == 68) {
                            //do not include Closed Upon Arrival at Delivery location
                        }
                        else if (isLockerJobB) {
                            if (PerobjStatusCode.DriverAffected.equals("2") && PerobjStatusCode.CodeType.equals("2") && PerobjStatusCode.IDX == 222) {
                                listobjStatusCodes.add(PerobjStatusCode);
                            }
                        }
                        else if (isPCDream) {
                            if (PerobjStatusCode.DriverAffected.equals("2") && PerobjStatusCode.CodeType.equals("2") && PerobjStatusCode.IDX == 67) {
                                listobjStatusCodes.add(PerobjStatusCode);
                            } else if (PerobjStatusCode.DriverAffected.equals("2") && PerobjStatusCode.CodeType.equals("2") && PerobjStatusCode.IDX == 75 && isDVatHigherToleranceBool && !AllValidTelNumsIsEmpty) {
                                listobjStatusCodes.add(PerobjStatusCode);
                            } else if (PerobjStatusCode.DriverAffected.equals("2") && PerobjStatusCode.CodeType.equals("2") && PerobjStatusCode.IDX == 78 && isDVatHigherToleranceBool && !hasStatusCodeIDX75) {
                                listobjStatusCodes.add(PerobjStatusCode);
                            } else if (PerobjStatusCode.DriverAffected.equals("2") && PerobjStatusCode.CodeType.equals("2") && PerobjStatusCode.IDX == 78 && !isDVatHigherToleranceBool && !hasStatusCodeIDX75) {
                                listobjStatusCodes.add(PerobjStatusCode);
                            }    else if (PerobjStatusCode.DriverAffected.equals("2") && PerobjStatusCode.CodeType.equals("2") && PerobjStatusCode.IDX == 80) {
                                listobjStatusCodes.add(PerobjStatusCode);
                            }
                        }
                        else {
                            if (PreDeterminedStatusCodeIDX != 0) {
                                if (PerobjStatusCode.DriverAffected.equals("2") && PerobjStatusCode.CodeType.equals("2") && PerobjStatusCode.IDX == PreDeterminedStatusCodeIDX) {
                                    listobjStatusCodes.add(PerobjStatusCode);
                                }
                            } else {
                                if (PerobjStatusCode.DriverAffected.equals("2") && PerobjStatusCode.CodeType.equals("2") && PerobjStatusCode.IDX != 222) {
                                    //if (PerobjStatusCode.IDX == 75 && !isStandardRunJob && isDVatHigherToleranceBool && !AllValidTelNumsIsEmpty) {
                                    if (PerobjStatusCode.IDX == 75 && !isStandardRunJob && isDVatHigherToleranceBool) {
                                        listobjStatusCodes.add(PerobjStatusCode);
                                    } else if (PerobjStatusCode.IDX == 78 && !isStandardRunJob && isDVatHigherToleranceBool && !hasStatusCodeIDX75) {
                                        listobjStatusCodes.add(PerobjStatusCode);
                                    } else if (PerobjStatusCode.IDX == 78 && !isStandardRunJob && !isDVatHigherToleranceBool && !hasStatusCodeIDX75) {
                                        listobjStatusCodes.add(PerobjStatusCode);
                                    } else if (PerobjStatusCode.IDX != 75 && PerobjStatusCode.IDX != 78){
                                        listobjStatusCodes.add(PerobjStatusCode);
                                    }
                                    //listobjStatusCodes.add(PerobjStatusCode);
                                }
                            }
                        }

                        /*if (PreDeterminedStatusCodeIDX != 0) {
                            if (PerobjStatusCode.DriverAffected.equals("2") && PerobjStatusCode.CodeType.equals("2") && PerobjStatusCode.IDX == PreDeterminedStatusCodeIDX) {
                                listobjStatusCodes.add(PerobjStatusCode);
                            }
                        } else {
                            if (PerobjStatusCode.DriverAffected.equals("2") && PerobjStatusCode.CodeType.equals("2")) {
                                listobjStatusCodes.add(PerobjStatusCode);
                            }
                        }*/
                        /*if (PerobjStatusCode.DriverAffected.equals("2") && PerobjStatusCode.CodeType.equals("2")) {
                            listobjStatusCodes.add(PerobjStatusCode);
                        }*/
                    }
                    lblfailuretype.setText(myApp.getAppContext().getString(R.string.lblFDfailuretype));
                }
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
        return listobjStatusCodes;
    }

    public boolean hasStatusCodeIDX(List<objStatusCodes> listobjStatusCodes, long idx) {
        boolean asd = false;
        try {
            if (listobjStatusCodes != null && listobjStatusCodes.size() > 0) {
                for (int i = 0; i <= listobjStatusCodes.size() -1; i++) {
                    if (listobjStatusCodes.get(i).IDX == idx) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity hasStatusCodeIDX. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public void btnViewImgClick(View view) {
        reasons = txtReason.getText().toString();
        Intent i = new Intent(setFailStatusActivity.this, ViewImageActivity.class);
        startActivity(i);
    }

    public void btnAttImgClick(View view) {
        isFromSetFailSatus = true;
        reasons = txtReason.getText().toString();
        Intent i = new Intent(setFailStatusActivity.this, CustomGalleryActivity.class);
        startActivity(i);
    }

    public void btnCameraClick(View view) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String CannotTakePhotoStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto");
        CannotTakePhoto = CannotTakePhotoStr.equals("") ? 0 : Integer.parseInt(CannotTakePhotoStr);

        if (CannotTakePhoto > 0) {
            showDialog(CannotTakePhoto == 3 ? myApp.getAppContext().getString(R.string.alert_allcannottakephoto).toUpperCase()
                    : CannotTakePhoto == 2 ? myApp.getAppContext().getString(R.string.alert_somecannottakephoto).toUpperCase()
                    : myApp.getAppContext().getString(R.string.alert_cannottakephoto).toUpperCase());
        } else {
            isFromSetFailSatus = true;
            setDeliveredMassStatusActivity.isFromMassSetOKP = false;
            setDeliveredStatusActivity.isFromSetOKP = false;
            reasons = txtReason.getText().toString();
            Intent i = new Intent(myApp.currentActivity, Camera5Activity.class);
            //Intent i = new Intent(myApp.currentActivity, CameraViewActivity.class);
            CheckListActivity.openCamera = null;
            CheckListActivity.itemIndex = null;
            try {
                if (imagePaths != null && imagePaths.size() > 0) {
                    int callLogImageCount = 0;
                    if (dataT != null && dataT.size() > 0) {
                        for (int x = 0; x <= dataT.size() - 1; x++) {
                            callLogImageCount += dataT.get(x).isCallLog ? 1 : 0;
                        }
                    }
                    if (imagePaths.size() - callLogImageCount >= 6) {
                        objGlobalMethods.writeToFile("setFailStatusActivity btnCameraClick blocked. 6 photos already. ", "", "", "", "");
                        showDialog(myApp.getAppContext().getString(R.string.cannotlaunchcam));
                    } else {
                        objGlobalMethods.writeToFile("setFailStatusActivity btnCameraClick. ", "", "", "", "");
                        startActivity(i);
                    }
                    /*if (imagePaths.size() >= 6) {
                        showDialog(myApp.getAppContext().getString(R.string.cannotlaunchcam));
                    } else {
                        startActivity(i);
                    }*/
                } else if (imagePaths != null && imagePaths.size() == 0) {
                    objGlobalMethods.writeToFile("setFailStatusActivity btnCameraClick. ", "", "", "", "");
                    startActivity(i);
                } else if (imagePaths == null) {
                    objGlobalMethods.writeToFile("setFailStatusActivity btnCameraClick. ", "", "", "", "");
                    startActivity(i);
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity btnCameraClick. " + e.getMessage().toString(), "", "", "", "");
            }
        }
    }

    public void btnCallLogImgClick(View view) {
        try {
            Context ctx = myApp.getAppContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(ctx.getString(R.string.att_call_log_ss_or_takephoto));

            builder.setPositiveButton(ctx.getString(R.string.takecalllogphoto), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    isFromSetFailSatus = true;
                    setDeliveredMassStatusActivity.isFromMassSetOKP = false;
                    setDeliveredStatusActivity.isFromSetOKP = false;
                    reasons = txtReason.getText().toString();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isCallLog", "t");
                    Intent i = new Intent(setFailStatusActivity.this, Camera5Activity.class);
                    startActivity(i);
                }
            });

            builder.setNegativeButton(ctx.getString(R.string.attachcalllog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    isFromSetFailSatus = true;
                    reasons = txtReason.getText().toString();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isCallLog", "t");
                    Intent i = new Intent(setFailStatusActivity.this, CustomGalleryActivity.class);
                    startActivity(i);

                }
            });

            builder.setNeutralButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });


            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity btnCallLogImgClick. " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public void btnCancelClick(View view) {
        onBackPressed();
    }

    private ArrayList<objConvertedBitmapToBase64> RTNobjConvertedBitmapToBase64List() {
        ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = null;
        String convertedBitmapToBase64 = "";
        objConvertedBitmapToBase64 obj = null;
        boolean isCallLogImg = false;
        try {
            if (imagePaths != null && imagePaths.size() > 0 && dataT != null && dataT.size() > 0) {
                for (int i = 0; i <= imagePaths.size() - 1; i++) {
                    isCallLogImg = dataT.get(i).isCallLog;
                    String imgmCurrentPhotoPath = imagePaths.get(i);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(imgmCurrentPhotoPath, bmOptions);

                    if (bitmap != null) {
                        convertedBitmapToBase64 = convertBitmapToBase64(bitmap);
                        bitmap.recycle();
                        bitmap = null;
                    } else {
                        convertedBitmapToBase64 = "";
                    }

                    if (!convertedBitmapToBase64.equals("")) {
                        obj = new objConvertedBitmapToBase64(isCallLogImg, convertedBitmapToBase64);
                        obj.setFilePath(imgmCurrentPhotoPath);
                        if (convertedBitmapToBase64List == null) {
                            convertedBitmapToBase64List = new ArrayList<>();
                            convertedBitmapToBase64List.add(obj);
                        } else {
                            convertedBitmapToBase64List.add(obj);
                        }
                    }
                    //objGlobalMethods.deleteFile(imgmCurrentPhotoPath);
                }
            }
        } catch (Exception e) {
            convertedBitmapToBase64List = null;
        } finally {
            return convertedBitmapToBase64List;
        }
    }

    private ArrayList<objConvertedBitmapToBase64> RTNCheckListConvertedBitmapToBase64List() {
        ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = null;
        String convertedBitmapToBase64 = "";
        objConvertedBitmapToBase64 obj = null;
        boolean isCallLogImg = false;
        String photodesc = "";
        try {
            /*if (CheckListActivity.PhotoImagePathList != null && CheckListActivity.PhotoImagePathList.size() > 0) {
                for (int i = 0; i <= CheckListActivity.PhotoImagePathList.size() - 1; i++) {
                    String imgmCurrentPhotoPath = CheckListActivity.PhotoImagePathList.get(i);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(imgmCurrentPhotoPath, bmOptions);

                    if (bitmap != null) {
                        convertedBitmapToBase64 = convertBitmapToBase64(bitmap);
                        bitmap.recycle();
                        bitmap = null;
                    } else {
                        convertedBitmapToBase64 = "";
                    }

                    if (!convertedBitmapToBase64.equals("")) {
                        obj = new objConvertedBitmapToBase64(isCallLogImg, convertedBitmapToBase64);
                        obj.setFilePath(imgmCurrentPhotoPath);
                        if (convertedBitmapToBase64List == null) {
                            convertedBitmapToBase64List = new ArrayList<>();
                            convertedBitmapToBase64List.add(obj);
                        } else {
                            convertedBitmapToBase64List.add(obj);
                        }
                    }
                }
            }*/
            if (CheckListActivity.dataTlist != null && CheckListActivity.dataTlist.size() > 0) {
                for (int i = 0; i <= CheckListActivity.dataTlist.size() - 1; i++) {
                    String imgmCurrentPhotoPath = CheckListActivity.dataTlist.get(i).sdcardPath;
                    photodesc = objGlobalMethods.IsEmptyString(CheckListActivity.dataTlist.get(i).photodesc) ? "" : CheckListActivity.dataTlist.get(i).photodesc;
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(imgmCurrentPhotoPath, bmOptions);

                    if (bitmap != null) {
                        convertedBitmapToBase64 = convertBitmapToBase64(bitmap);
                        bitmap.recycle();
                        bitmap = null;
                    } else {
                        convertedBitmapToBase64 = "";
                    }

                    if (!convertedBitmapToBase64.equals("")) {
                        obj = new objConvertedBitmapToBase64(isCallLogImg, convertedBitmapToBase64);
                        obj.setFilePath(imgmCurrentPhotoPath);
                        obj.setPhotodesc(photodesc);
                        if (convertedBitmapToBase64List == null) {
                            convertedBitmapToBase64List = new ArrayList<>();
                            convertedBitmapToBase64List.add(obj);
                        } else {
                            convertedBitmapToBase64List.add(obj);
                        }
                    }
                    //objGlobalMethods.deleteFile(imgmCurrentPhotoPath);
                }
            }
        } catch (Exception e) {
            convertedBitmapToBase64List = null;
        } finally {
            return convertedBitmapToBase64List;
        }
    }

    private ArrayList<String> RTNconvertedBitmapToBase64List() {
        ArrayList<String> convertedBitmapToBase64List = null;
        String convertedBitmapToBase64 = "";
        try {
            if (imagePaths != null && imagePaths.size() > 0 && dataT != null && dataT.size() > 0) {
                for (int i = 0; i <= imagePaths.size() - 1; i++) {
                    String imgmCurrentPhotoPath = imagePaths.get(i);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(imgmCurrentPhotoPath, bmOptions);

                    try {
                        if (bitmap != null) {
                            ExifInterface exif = new ExifInterface(imgmCurrentPhotoPath);
                            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
                            int rotationAngle = 0;
                            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                                rotationAngle = 90;
                            if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                                rotationAngle = 180;
                            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                                rotationAngle = 270;

                            Matrix matrix = new Matrix();
                            matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bmOptions.outWidth, bmOptions.outHeight, matrix, true);
                        }
                    } catch (Exception e) {
                        bitmap = BitmapFactory.decodeFile(imgmCurrentPhotoPath, bmOptions);
                    }

                    if (bitmap != null) {
                        convertedBitmapToBase64 = convertBitmapToBase64(bitmap);
                        bitmap.recycle();
                        bitmap = null;
                    } else {
                        convertedBitmapToBase64 = "";
                    }

                    if (!convertedBitmapToBase64.equals("")) {
                        if (convertedBitmapToBase64List == null) {
                            convertedBitmapToBase64List = new ArrayList<>();
                            convertedBitmapToBase64List.add(convertedBitmapToBase64);
                        } else {
                            convertedBitmapToBase64List.add(convertedBitmapToBase64);
                        }
                    }
                    //objGlobalMethods.deleteFile(imgmCurrentPhotoPath);
                }
            }
        } catch (Exception e) {
            convertedBitmapToBase64List = null;
        } finally {
            return convertedBitmapToBase64List;
        }
    }

    public void btnNextClick(View view) {
        String linkedJobStr = "";
        Gson gson = new Gson();
        List<objJobInfo> objJobInfoBringtoUpdateCODList = null;
        String objJobInfoBringtoUpdateCODListJsonStr = "";
        String ReasonStr = "";
        ArrayList<String> convertedBitmapToBase64List;
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (!selectedStatusCodesIDX.equals("")) {
                linkedJobStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoLinkedJobInfo);
                objJobInfo linkedJob = null;
                if (!linkedJobStr.equals("")) {
                    linkedJob = gson.fromJson(linkedJobStr, objJobInfo.class);
                }

                linkedJob.setpendingStatusUpdate(Long.parseLong(selectedStatusCodesIDX));
                linkedJob.setReceiver("");
                if (!selectedFailureReasonStr.equals("")) {
                    ReasonStr = selectedFailureReasonStr;
                }
                if (!txtReason.getText().toString().equals("")) {
                    ReasonStr += "\n\n" + txtReason.getText().toString();
                }
                //String checklistvalue = objGlobalMethods.getGCMPrefs(GcmPreferences, "checklistvalue");
                String checklistvalue = objGlobalMethods.getChecklistRelatedSP_KeyValue("checklistvalue");

                if (checklistvalue.length() > 0) {
                    ReasonStr += "\n\n" + checklistvalue;
                }
                String pr_latemsg0 = objGlobalMethods.getGCMPrefs(GcmPreferences, "pr_latemsg0");
                if (!objGlobalMethods.IsEmptyString(pr_latemsg0)) {
                    ReasonStr += "\n" + pr_latemsg0;
                }
                CannotTakePhoto = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto"));
                if (CannotTakePhoto > 0 && ReasonStr.length() > 0) {
                    ReasonStr += "\n\nRestricted area. Photo taking not allowed.";
                } else if (CannotTakePhoto > 0 && ReasonStr.length() == 0) {
                    ReasonStr += " Restricted area. Photo taking not allowed.";
                }

                linkedJob.setFailureReason(ReasonStr);
                convertedBitmapToBase64List = RTNconvertedBitmapToBase64List();
                linkedJob.setconvertedBitmapToBase64(convertedBitmapToBase64List);
                Camera5Activity.mCurrentPhotoPath = null;
                imagePaths = null;
                dataT = null;
                objJobInfoBringtoUpdateCODListJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr");
                objJobInfoBringtoUpdateCODList = gson.fromJson(objJobInfoBringtoUpdateCODListJsonStr, new TypeToken<List<objJobInfo>>() {
                }.getType());


                objJobInfoBringtoUpdateCODList = JobsHandler.RTNobjJobInfoBringtoUpdateCODList(objJobInfoBringtoUpdateCODList, linkedJob);

                objJobInfoBringtoUpdateCODListJsonStr = gson.toJson(objJobInfoBringtoUpdateCODList);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", objJobInfoBringtoUpdateCODListJsonStr);

                Intent intent = null;
                if (updatemodeForFailure.equals("0")) {
                    //proceed to updateCOD
                    //0 == part1 has cod, fail p2 pu, will proceed
                    intent = new Intent(this, UpdateCOD_List.class);
                } else if (updatemodeForFailure.equals("2")) {
                    //proceed back to massUpdateOKPwP2
                    intent = new Intent(this, MassUpdateOKPwP2.class);
                }

                startActivity(intent);
                this.finish();
            } else {
                playTone();
                showDialog(myApp.getAppContext().getString(R.string.selectfailuretype));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity btnNextClick. Exception: " + e.toString(), "", "", "", "");

        }
    }

    private boolean requireSubFailSelection() {
        boolean asd = true;
        try {
            asd = (ddlFailureReason.isEnabled() && objGlobalMethods.IsEmptyString(selectedFailureReasonStr)) && txtReason.getText().toString().length() < 6;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity requireSubFailSelection. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public void btnSubmitClick2(View view) {
        try {
            if (!selectedStatusCodesIDX.equals("")) {
                if (MassUpdateOKPwP2PostData != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setMessage(myApp.getAppContext().getString(R.string.failremaining));

                    builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO2), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String ReasonStr = "";
                            if (!selectedFailureReasonStr.equals("")) {
                                ReasonStr = selectedFailureReasonStr;
                            }
                            if (!txtReason.getText().toString().equals("")) {
                                ReasonStr += "\n\n" + txtReason.getText().toString();
                            }
                            GcmPreferences = objGlobalMethods.getGcmPreferences();

                            String objCallCheckJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objCallCheckJsonStr");
                            Gson gson = new Gson();
                            objCallCheck occ = !objGlobalMethods.IsEmptyString(objCallCheckJsonStr) ? gson.fromJson(objCallCheckJsonStr, objCallCheck.class) : null;
                            if (occ != null) {
                                ReasonStr += occ.getAdditionalCallLogsInfo();
                            }

                            /*String checklistvalue = objGlobalMethods.getGCMPrefs(GcmPreferences, "checklistvalue");
                            if (checklistvalue.length() > 0) {
                                ReasonStr += "\n\n" + checklistvalue;
                            }*/
                            String pr_latemsg0 = objGlobalMethods.getGCMPrefs(GcmPreferences, "pr_latemsg0");
                            if (!objGlobalMethods.IsEmptyString(pr_latemsg0)) {
                                ReasonStr += "\n" + pr_latemsg0;
                            }
                            CannotTakePhoto = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto"));
                            if (CannotTakePhoto > 0 && ReasonStr.length() > 0) {
                                ReasonStr += "\n\nRestricted area. Photo taking not allowed.";
                            } else if (CannotTakePhoto > 0 && ReasonStr.length() == 0) {
                                ReasonStr += " Restricted area. Photo taking not allowed.";
                            }

                            boolean FailAsND = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "FailAsND"));
                            if (FailAsND) {
                                String FailAsND_Reason = objGlobalMethods.getGCMPrefs(GcmPreferences, "FailAsND_Reason");
                                if (!objGlobalMethods.IsEmptyString(FailAsND_Reason)) {
                                    ReasonStr += "\n\n" + FailAsND_Reason;
                                }
                            }

                            FailMassUpdateOKPwP2submitSQLite(GcmPreferences, false, ReasonStr);

                        }
                    });

                    builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String ReasonStr = "";
                            if (!selectedFailureReasonStr.equals("")) {
                                ReasonStr = selectedFailureReasonStr;
                            }
                            if (!txtReason.getText().toString().equals("")) {
                                ReasonStr += "\n\n" + txtReason.getText().toString();
                            }
                            GcmPreferences = objGlobalMethods.getGcmPreferences();

                            String objCallCheckJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objCallCheckJsonStr");
                            Gson gson = new Gson();
                            objCallCheck occ = !objGlobalMethods.IsEmptyString(objCallCheckJsonStr) ? gson.fromJson(objCallCheckJsonStr, objCallCheck.class) : null;
                            if (occ != null) {
                                ReasonStr += occ.getAdditionalCallLogsInfo();
                            }

                            /*String checklistvalue = objGlobalMethods.getGCMPrefs(GcmPreferences, "checklistvalue");
                            if (checklistvalue.length() > 0) {
                                ReasonStr += "\n\n" + checklistvalue;
                            }*/
                            String pr_latemsg0 = objGlobalMethods.getGCMPrefs(GcmPreferences, "pr_latemsg0");
                            if (!objGlobalMethods.IsEmptyString(pr_latemsg0)) {
                                ReasonStr += "\n" + pr_latemsg0;
                            }
                            CannotTakePhoto = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto"));
                            if (CannotTakePhoto > 0 && ReasonStr.length() > 0) {
                                ReasonStr += "\nRestricted area. Photo taking not allowed.";
                            } else if (CannotTakePhoto > 0 && ReasonStr.length() == 0) {
                                ReasonStr += " Restricted area. Photo taking not allowed.";
                            }

                            boolean FailAsND = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "FailAsND"));
                            if (FailAsND) {
                                String FailAsND_Reason = objGlobalMethods.getGCMPrefs(GcmPreferences, "FailAsND_Reason");
                                if (!objGlobalMethods.IsEmptyString(FailAsND_Reason)) {
                                    ReasonStr += "\n\n" + FailAsND_Reason;
                                }
                            }

                            FailMassUpdateOKPwP2submitSQLite(GcmPreferences, true, ReasonStr);

                        }
                    });

                    builder.setNeutralButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user pressed "No", just update this job only
                            dialog.cancel();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);

                    if (catType == 0) {
                        builder.setMessage(myApp.getAppContext().getString(R.string.alert_fp));
                    } else if (catType == 2) {
                        builder.setMessage(myApp.getAppContext().getString(R.string.alert_fd));
                    }

                    builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Gson gson = new Gson();
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                            objJobInfo currJobInfo = null;
                            String hfJobInfoJsonStr = GcmPreferences.getString("hfJobInfoJsonStr", "");
                            String ManualPinVerificaionText = "";
                            if (!hfJobInfoJsonStr.isEmpty()) {
                                currJobInfo = gson.fromJson(hfJobInfoJsonStr, objJobInfo.class);
                            }

                            String objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
                            String objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);

                            objGlobalMethods.writeToFile("Click to Fail Job(s).", "", "", "", "");

                            String ReasonStr = "";
                            if (!selectedFailureReasonStr.equals("")) {
                                ReasonStr = selectedFailureReasonStr;
                            }

                            if (!txtReason.getText().toString().equals("")) {
                                ReasonStr += "\n\n" + txtReason.getText().toString();
                            }

                            String objCallCheckJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objCallCheckJsonStr");
                            objCallCheck occ = !objGlobalMethods.IsEmptyString(objCallCheckJsonStr) ? gson.fromJson(objCallCheckJsonStr, objCallCheck.class) : null;
                            if (occ != null) {
                                ReasonStr += occ.getAdditionalCallLogsInfo();
                            }


                            if (currJobInfo != null) {
                                ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText();
                                if (ManualPinVerificaionText.length() > 0) {
                                    ReasonStr += "\n\n" + ManualPinVerificaionText;
                                }
                            }

                            String failPart2FromPart1Bool = objGlobalMethods.getGCMPrefs(GcmPreferences, "failPart2FromPart1Bool");
                            //String checklistvalue = !failPart2FromPart1Bool.equals("t") ? objGlobalMethods.getGCMPrefs(GcmPreferences, "checklistvalue") : "";
                            String checklistvalue = !failPart2FromPart1Bool.equals("t") ? objGlobalMethods.getChecklistRelatedSP_KeyValue("checklistvalue") : "";

                            if (checklistvalue.length() > 0) {
                                ReasonStr += "\n\n" + checklistvalue;
                            }
                            String pr_latemsg0 = objGlobalMethods.getGCMPrefs(GcmPreferences, "pr_latemsg0");
                            if (!objGlobalMethods.IsEmptyString(pr_latemsg0)) {
                                ReasonStr += "\n" + pr_latemsg0;
                            }

                            CannotTakePhoto = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto"));
                            if (CannotTakePhoto > 0 && ReasonStr.length() > 0) {
                                ReasonStr += "\n Restricted area. Photo taking not allowed.";
                            } else if (CannotTakePhoto > 0 && ReasonStr.length() == 0) {
                                ReasonStr += " Restricted area. Photo taking not allowed.";
                            }

                            boolean FailAsND = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "FailAsND"));
                            if (FailAsND) {
                                String FailAsND_Reason = objGlobalMethods.getGCMPrefs(GcmPreferences, "FailAsND_Reason");
                                if (!objGlobalMethods.IsEmptyString(FailAsND_Reason)) {
                                    ReasonStr += "\n\n" + FailAsND_Reason;
                                }
                            }

                            massUpdateJobsStatusSubmitSQLite(ReasonStr, objCheckedItemsJsonStr, objCheckedItemsIDXJsonStr, GcmPreferences, UpdateJobInfoPreferences);
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
            } else {
                playTone();
                showDialog(myApp.getAppContext().getString(R.string.selectfailuretype));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity btnSubmitClick. " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private boolean CallLogImgTaken() {
        boolean asd = false;
        try {
            if (imagePaths != null && imagePaths.size() > 0 && dataT != null && dataT.size() > 0) {
                for (int i = 0; i <= dataT.size() - 1; i++) {
                    if (dataT.get(i).isCallLog) {
                        asd = true;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            asd = false;
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity CallLogImgTaken. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    //checked
    public void FailMassUpdateOKPwP2submitSQLite(final SharedPreferences GcmPref, Boolean updateAll, final String Reason) {
        objJobInfo ji = null;

        try {
            final ArrayList<String> convertedBitmapToBase64List = RTNconvertedBitmapToBase64List();
            final ArrayList<objConvertedBitmapToBase64> newConvertedBitmapToBase64List = RTNobjConvertedBitmapToBase64List();
            if (updateAll) {
                String[] finalCheckedItemsIDX = new String[MassUpdateOKPwP2.massupdateokpwp2PostDataArr.size()];
                for (int i = 0; i <= MassUpdateOKPwP2.massupdateokpwp2PostDataArr.size() - 1; i++) {
                    finalCheckedItemsIDX[i] = MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).Part2JobsIDX;
                }
                String str1 = Arrays.toString(finalCheckedItemsIDX);
                objGlobalMethods.writeToFile("Fail All Part 2 job at setFailStatusActivity FailMassUpdateOKPwP2submitSQLite when MassUpdateOKPwP2PostData is not null.",
                        "JobsIDX: " + str1, "Entered StatusCodeIDX: " + selectedStatusCodesIDX, "", "");

                progressDoalog = new ProgressDialog(this);
                progressDoalog.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));
                progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                int count = 0;
                for (int i = 0; i <= MassUpdateOKPwP2.massupdateokpwp2PostDataArr.size() - 1; i++) {
                    count = MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).StatusCodesIDX == null ||
                            MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).StatusCodesIDX.equals("") ||
                            MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).StatusCodesIDX.equals("0") ? i + 1 : i + 0;
                }
                progressDoalog.setMax(count);
                progressDoalog.setCanceledOnTouchOutside(false);
                progressDoalog.show();

                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i <= MassUpdateOKPwP2.massupdateokpwp2PostDataArr.size() - 1; i++) {
                                if (MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).StatusCodesIDX == null ||
                                        MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).StatusCodesIDX.equals("") ||
                                        MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).StatusCodesIDX.equals("0")) {
                                    Thread.sleep(200);
                                    handle.sendMessage(handle.obtainMessage());

                                    MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).currJobInfo.setFailureReason(Reason);
                                    MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).currJobInfo.setpendingStatusUpdate(Long.parseLong(selectedStatusCodesIDX));
                                    MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).currJobInfo.setReceiver("");
                                    MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).currJobInfo.setconvertedBitmapToBase64(convertedBitmapToBase64List);
                                    MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).currJobInfo.setObjConvertedBitmapToBase64(newConvertedBitmapToBase64List);

                                    MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).StatusCodesIDX = selectedStatusCodesIDX;
                                    MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).FailureReason = Reason;
                                    MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).Part2convertedBitmapToBase64 = convertedBitmapToBase64List;

                                    if (i == MassUpdateOKPwP2.massupdateokpwp2PostDataArr.size() - 1) {
                                        progressDoalog.dismiss();
                                        MassUpdateOKPwP2PostData = null;
                                        Camera5Activity.mCurrentPhotoPath = null;
                                        dataT = null;
                                        imagePaths = null;
                                        isFromSetFailSatus = false;
                                        setDeliveredMassStatusActivity.isFromMassSetOKP = false;
                                        setDeliveredStatusActivity.isFromSetOKP = false;
                                        if (bitmap != null) {
                                            bitmap.recycle();
                                            bitmap = null;
                                        }
                                        reasons = "";
                                        ddlSelectedFailureReasonIndex = 0;
                                        selectedFailureReasonStr = "";
                                        ddlSelectedIndex = 0;
                                        selectedStatusCodesIDX = "";

                                        objGlobalMethods.setGCMPrefs(GcmPref, objGlobalVariables.categoryTypePrefName, "2");
                                        Intent parentIntent = new Intent(setFailStatusActivity.this, MassUpdateOKPwP2.class);
                                        startActivity(parentIntent);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            progressDoalog.dismiss();
                            objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity FailMassUpdateOKPwP2submitSQLite progress dialog thread. Exception: " + e.getMessage().toString(), "", "", "", "");
                            JobsHandler.sendErrorEmail("Exception caught at setFailStatusActivity FailMassUpdateOKPwP2submitSQLite progress dialog thread. Exception: " + e.getMessage().toString());
                        }
                    }
                };
                t.start();
            } else {
                ji = null;
                objGlobalMethods.writeToFile("Fail Part 2 job at setFailStatusActivity FailMassUpdateOKPwP2submitSQLite when MassUpdateOKPwP2PostData is not null.",
                        "JobsIDX: " + MassUpdateOKPwP2PostData.Part2JobsIDX, "Entered StatusCodeIDX: " + selectedStatusCodesIDX, "", "");

                ji = MassUpdateOKPwP2PostData.currJobInfo;
                ji.setFailureReason(Reason);
                ji.setpendingStatusUpdate(Long.parseLong(selectedStatusCodesIDX));
                ji.setReceiver("");
                ji.setconvertedBitmapToBase64(convertedBitmapToBase64List);
                ji.setObjConvertedBitmapToBase64(newConvertedBitmapToBase64List);
                MassUpdateOKPwP2PostData.currJobInfo = ji;

                for (int i = 0; i <= MassUpdateOKPwP2.massupdateokpwp2PostDataArr.size() - 1; i++) {
                    if (MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).Part2JobsIDX.equals(MassUpdateOKPwP2PostData.Part2JobsIDX)) {
                        MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).currJobInfo = ji;

                        MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).StatusCodesIDX = selectedStatusCodesIDX;
                        MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).FailureReason = Reason;
                        MassUpdateOKPwP2.massupdateokpwp2PostDataArr.get(i).Part2convertedBitmapToBase64 = convertedBitmapToBase64List;
                        break;
                    }
                }

                MassUpdateOKPwP2PostData = null;
                Camera5Activity.mCurrentPhotoPath = null;
                dataT = null;
                imagePaths = null;
                isFromSetFailSatus = false;
                setDeliveredMassStatusActivity.isFromMassSetOKP = false;
                setDeliveredStatusActivity.isFromSetOKP = false;
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
                reasons = "";
                ddlSelectedFailureReasonIndex = 0;
                selectedFailureReasonStr = "";
                ddlSelectedIndex = 0;
                selectedStatusCodesIDX = "";

                objGlobalMethods.setGCMPrefs(GcmPref, objGlobalVariables.categoryTypePrefName, "2");
                Intent parentIntent = new Intent(setFailStatusActivity.this, MassUpdateOKPwP2.class);
                startActivity(parentIntent);
            }
        } catch (Exception e) {
            MassUpdateOKPwP2PostData = null;
            Camera5Activity.mCurrentPhotoPath = null;
            dataT = null;
            imagePaths = null;

            isFromSetFailSatus = false;
            setDeliveredMassStatusActivity.isFromMassSetOKP = false;
            setDeliveredStatusActivity.isFromSetOKP = false;

            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            reasons = "";
            ddlSelectedFailureReasonIndex = 0;
            selectedFailureReasonStr = "";
            ddlSelectedIndex = 0;
            selectedStatusCodesIDX = "";

            objGlobalMethods.setGCMPrefs(GcmPref, objGlobalVariables.categoryTypePrefName, "2");

            objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity btnSubmitClick when FailMassUpdateOKPwP2submitSQLite not null. Exception: " + e.toString(), "", "", "", "");
            showErrorAlertNreturnToMassUpdateOKPwP2("Unable to fail. Please try again.");
        }
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(1);
        }
    };

    //checked
    public void massUpdateJobsStatusSubmitSQLite(final String reason, String objCheckedItemsJsonStr, String objCheckedItemsIDXJsonStr, final SharedPreferences gcmPrefs,
                                                 final SharedPreferences updateJobInfoPrefs) {
        Date dt = new Date();
        Gson gson = new Gson();
        Long l = dt.getTime();
        final String timestampstr = "/Date(" + l + ")/";
        String linkedJobStr = "";
        String convertedBitmapToBase64 = "";

        try {
            String RegID = objGlobalMethods.getRegistrationIdOnly();
            objGlobalMethods.writeToFile("setFailStatusActivity massUpdateJobsStatusSubmitSQLite started.", "", "", "", "");
            catType = objGlobalMethods.getDashboard2ActivityCategoryType(gcmPrefs);
            JobsIDX = 0;

            final ArrayList<objConvertedBitmapToBase64> newConvertedBitmapToBase64List = RTNobjConvertedBitmapToBase64List();
            final ArrayList<objConvertedBitmapToBase64> CheckListconvertedBitmapToBase64List = RTNCheckListConvertedBitmapToBase64List();

            String failPart2FromPart1Bool = objGlobalMethods.getGCMPrefs(GcmPreferences, "failPart2FromPart1Bool");

            if (failPart2FromPart1Bool.equals("t")) {
               /* objGlobalMethods.setGCMPrefs(gcmPrefs, "failPart2FromPart1Bool", "f");
                objGlobalMethods.setGCMPrefs(gcmPrefs, objGlobalVariables.categoryTypePrefName, "2");*/

                String[] prefName = new String[]{"failPart2FromPart1Bool", objGlobalVariables.categoryTypePrefName};
                String[] prefvalue = new String[]{"f", "2"};
                objGlobalMethods.setGCMPrefsMass(gcmPrefs, prefName, prefvalue);
                linkedJobStr = objGlobalMethods.getGCMPrefs(gcmPrefs, objGlobalVariables.currJobInfoLinkedJobInfo);
                objJobInfo linkedJob = null;
                if (!linkedJobStr.equals("")) {
                    linkedJob = gson.fromJson(linkedJobStr, objJobInfo.class);
                }
                //fail single part2
                objGlobalMethods.writeToFile("Fail Part 2 job.",
                        "JobsIDX: " + String.valueOf(linkedJob.JobsIDX), "Submitted StatusCodeIDX: " + String.valueOf(selectedStatusCodesIDX), "", "");

                Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(updateJobInfoPrefs, RegID, linkedJob.JobsIDX, Long.parseLong(selectedStatusCodesIDX), timestampstr, reason, "",
                        lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

                if (newConvertedBitmapToBase64List != null && newConvertedBitmapToBase64List.size() > 0) {
                    for (int x = 0; x <= newConvertedBitmapToBase64List.size() - 1; x++) {
                        JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(updateJobInfoPrefs, RegID, linkedJob.JobsIDX,
                                newConvertedBitmapToBase64List.get(x).isCallLogImg ? "[" + selectedStatusCodesIDX + "]" + "CALL_LOG" : "[" + selectedStatusCodesIDX + "]" + reason,
                                newConvertedBitmapToBase64List.get(x).convertedBitmapToBase64,
                                timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                newConvertedBitmapToBase64List.get(x).FilePath);
                    }
                }


                Camera5Activity.mCurrentPhotoPath = null;
                imagePaths = null;
                dataT = null;

                //add Part 2 jobinfo to fp pref
                DAO_Jobs.insertJobs(linkedJob, 4, linkedJobStr);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 4);
                objGlobalMethods.setGCMPrefs(gcmPrefs, objGlobalVariables.currJobInfoLinkedJobInfo, "");

                JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "", "", true);
                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, true);

                Camera5Activity.mCurrentPhotoPath = null;
                bitmap = null;
                dataT = null;
                imagePaths = null;
                MassUpdateOKPwP2PostData = null;
                objGlobalMethods.writeToFile("setFailStatusActivity massUpdateJobsStatusSubmitSQLite ended.", "", "", "", "");
                resetAddressListActivityhfJobsInfoArrJsonStrSQLite();
            } else {
                if (!objCheckedItemsJsonStr.equals("")) {
                    //mass fail
                    String[] CheckedItemsIDX = null;
                    CheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
                    if (CheckedItemsIDX != null && CheckedItemsIDX.length > 0) {
                        String str1 = Arrays.toString(CheckedItemsIDX);
                        objGlobalMethods.writeToFile("Mass fail job(s).", "JobsIDXs: " + str1, "Submitted StatusCodeIDX: " + String.valueOf(selectedStatusCodesIDX), "", "");
                    }

                    final objCheckedItems[] objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                    final List<objCheckedItems> objCheckedItemsList = objCheckedItemsAL != null && objCheckedItemsAL.length > 0 ? new ArrayList<objCheckedItems>(Arrays.asList(objCheckedItemsAL)) : null;

                    progressDoalog = new ProgressDialog(this);
                    progressDoalog.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));
                    progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDoalog.setMax(objCheckedItemsAL.length);
                    progressDoalog.setCanceledOnTouchOutside(false);
                    progressDoalog.show();

                    final Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                    final int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                    //final ArrayList<String> convertedBitmapToBase64List = RTNconvertedBitmapToBase64List();

                    if (objCheckedItemsList != null && objCheckedItemsList.size() > 0) {
                        final Thread t = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    String RegID = objGlobalMethods.getRegistrationIdOnly();
                                    Gson gson = new Gson();

                                    for (int i = objCheckedItemsList.size() - 1; i >= 0; i--) {
                                        Thread.sleep(200);
                                        handle.sendMessage(handle.obtainMessage());

                                        objJobInfo currJobInfo = gson.fromJson(objCheckedItemsAL[i].JobInfoJsonStr, objJobInfo.class);
                                        JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(updateJobInfoPrefs, RegID, currJobInfo.JobsIDX, Long.parseLong(selectedStatusCodesIDX), timestampstr, reason, "",
                                                lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

                                        /*if (convertedBitmapToBase64List != null && convertedBitmapToBase64List.size() > 0) {
                                            for (int x = 0; x <= convertedBitmapToBase64List.size() - 1; x++) {
                                                JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(updateJobInfoPrefs, RegID, currJobInfo.JobsIDX, reason, convertedBitmapToBase64List.get(x),
                                                        timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                                            }
                                        }*/
                                        if (newConvertedBitmapToBase64List != null && newConvertedBitmapToBase64List.size() > 0) {
                                            for (int x = 0; x <= newConvertedBitmapToBase64List.size() - 1; x++) {
                                                JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(updateJobInfoPrefs, RegID, currJobInfo.JobsIDX,
                                                        newConvertedBitmapToBase64List.get(x).isCallLogImg ? "[" + selectedStatusCodesIDX + "]" + "CALL_LOG" : "[" + selectedStatusCodesIDX + "]" + reason,
                                                        newConvertedBitmapToBase64List.get(x).convertedBitmapToBase64, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                                        newConvertedBitmapToBase64List.get(x).FilePath);
                                            }
                                        }

                                        if (catType == 0) {
                                            ////set from pip to fp
                                            DAO_Jobs.updateJobsCatTypeID(4, currJobInfo.JobsIDX);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 0);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 4);
                                        } else if (catType == 2) {
                                            ////set from dip to fd
                                            DAO_Jobs.updateJobsCatTypeID(5, currJobInfo.JobsIDX);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 2);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 5);
                                        }

                                        objCheckedItemsList.remove(i);

                                        if (i == 0) {
                                            Camera5Activity.mCurrentPhotoPath = null;
                                            bitmap = null;
                                            imagePaths = null;
                                            dataT = null;
                                            MassUpdateOKPwP2PostData = null;
                                            objCheckedItems[] newobjCheckedItemsAL;
                                            newobjCheckedItemsAL = objCheckedItemsList.toArray(new objCheckedItems[objCheckedItemsList.size()]);
                                            String objCheckedItemsJsonStr2 = gson.toJson(newobjCheckedItemsAL);
                                            if (objCheckedItemsJsonStr2.equals("[]")) {
                                                objCheckedItemsJsonStr2 = "";
                                            }
                                            objGlobalMethods.setGCMPrefs(gcmPrefs, objGlobalVariables.checkedItemsName, objCheckedItemsJsonStr2);

                                            progressDoalog.dismiss();
                                            JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "", "", true);
                                            Context ctx = myApp.getAppContext();
                                            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                                            objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, true);

                                            objGlobalMethods.writeToFile("setFailStatusActivity massUpdateJobsStatusSubmitSQLite ended.", "", "", "", "");
                                            resetAddressListActivityhfJobsInfoArrJsonStrSQLite();
                                        }

                                    }
                                } catch (Exception e) {
                                    progressDoalog.dismiss();
                                    objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity massUpdateJobsStatusSubmitSQLite mass fail jobs progress dialog thread. Exception: " + e.getMessage().toString(), "", "", "", "");
                                    JobsHandler.sendErrorEmail("Exception caught at setFailStatusActivity massUpdateJobsStatusSubmitSQLite mass fail jobs progress dialog thread. Exception: " + e.getMessage().toString());
                                }
                            }
                        };
                        t.start();
                    } else {
                        //return back to dash board,

                        objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, false);

                        showErrorAlertNreturnToDashboard(myApp.getAppContext().getString(R.string.unableretrievejob));
                    }
                } else {
                    //fail single job
                    pd.show();

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String RegID = objGlobalMethods.getRegistrationIdOnly();
                                String JobsIDXstr = "";
                                JobsIDXstr = objGlobalMethods.getGCMPrefs(gcmPrefs, objGlobalVariables.currJobInfoIDX);
                                JobsIDX = Long.parseLong(JobsIDXstr);
                                objGlobalMethods.writeToFile("Fail job.",
                                        "JobsIDX: " + String.valueOf(JobsIDX), "Submitted StatusCodeIDX: " + selectedStatusCodesIDX + "\nReason: " + reason, "", "");
                                Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                                int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                                ArrayList<String> convertedBitmapToBase64List = RTNconvertedBitmapToBase64List();
                                JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(updateJobInfoPrefs, RegID, JobsIDX, Long.parseLong(selectedStatusCodesIDX), timestampstr, reason, "",
                                        lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

                                if (CheckListconvertedBitmapToBase64List != null && CheckListconvertedBitmapToBase64List.size() > 0) {
                                    for (int x = 0; x <= CheckListconvertedBitmapToBase64List.size() - 1; x++) {
                                        JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(updateJobInfoPrefs, RegID, JobsIDX,
                                                !objGlobalMethods.IsEmptyString(CheckListconvertedBitmapToBase64List.get(x).photodesc) ? "[" + selectedStatusCodesIDX + "]" + CheckListconvertedBitmapToBase64List.get(x).photodesc : "[" + selectedStatusCodesIDX + "]" + reason,
                                                CheckListconvertedBitmapToBase64List.get(x).convertedBitmapToBase64, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                                CheckListconvertedBitmapToBase64List.get(x).FilePath);
                                    }
                                }
                                if (newConvertedBitmapToBase64List != null && newConvertedBitmapToBase64List.size() > 0) {
                                    for (int x = 0; x <= newConvertedBitmapToBase64List.size() - 1; x++) {
                                        JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(updateJobInfoPrefs, RegID, JobsIDX,
                                                newConvertedBitmapToBase64List.get(x).isCallLogImg ? "[" + selectedStatusCodesIDX + "]" + "CALL_LOG" : "[" + selectedStatusCodesIDX + "]" + reason,
                                                newConvertedBitmapToBase64List.get(x).convertedBitmapToBase64, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                                newConvertedBitmapToBase64List.get(x).FilePath);
                                    }
                                }

                                Camera5Activity.mCurrentPhotoPath = null;
                                dataT = null;
                                imagePaths = null;

                                if (catType == 0) {
                                    ////set from pip to fp
                                    DAO_Jobs.updateJobsCatTypeID(4, JobsIDX);
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 0);
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 4);
                                } else if (catType == 2) {
                                    ////set from dip to fd
                                    DAO_Jobs.updateJobsCatTypeID(5, JobsIDX);
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 2);
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 5);
                                }

                                JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "", "", true);
                                Context ctx = myApp.getAppContext();
                                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                                objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, true);

                                Camera5Activity.mCurrentPhotoPath = null;
                                bitmap = null;
                                dataT = null;
                                imagePaths = null;
                                MassUpdateOKPwP2PostData = null;
                                objGlobalMethods.writeToFile("setFailStatusActivity massUpdateJobsStatusSubmitSQLite ended.", "", "", "", "");
                                resetAddressListActivityhfJobsInfoArrJsonStrSQLite();
                                if (pd != null) {
                                    pd.dismiss();
                                }
                            } catch (Exception e) {
                                if (pd != null) {
                                    pd.dismiss();
                                }
                                objGlobalMethods.writeToFile(
                                        "Exception caught at setFailStatusActivity massUpdateJobsStatusSubmitSQLite fail single job progress dialog thread. JobsIDX: " + JobsIDX + ". Exception: " + e.getMessage().toString(), "", "", "", "");
                                JobsHandler.sendErrorEmail(
                                        "Exception caught at setFailStatusActivity massUpdateJobsStatusSubmitSQLite fail single job progress dialog thread. JobsIDX: " + JobsIDX + ". Exception: " + e.getMessage().toString());
                            }
                        }
                    });
                    t.start();
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity massUpdateJobsStatusSubmitSQLite. Exception: " + e.toString(), "", "", "", "");
            Camera5Activity.mCurrentPhotoPath = null;
            bitmap = null;
            dataT = null;
            imagePaths = null;
            MassUpdateOKPwP2PostData = null;
            ddlSelectedFailureReasonIndex = 0;
            selectedFailureReasonStr = "";
            ddlSelectedIndex = 0;
            selectedStatusCodesIDX = "";

            String[] prefName = new String[]{"isFromDashboardSub", objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, "selectedRouteAddressJobsIDXs",
                    objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "isFromMainCOD"};
            String[] prefvalue = new String[]{"", "-1", "", "", "", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(gcmPrefs, prefName, prefvalue);

            objGlobalMethods.setGCMPrefs(updateJobInfoPrefs, "objPendingJobStatusUpdatesobjALPrefStr", "");
            objGlobalMethods.setGCMPrefs(updateJobInfoPrefs, "objPendingJobPhotoUpdatesobjALPrefStr", "");
            objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity massUpdateJobsStatusSubmitSQLite onPostExecute" + e.getMessage().toString(), "", "", "", "");
            //Intent in = new Intent(setFailStatusActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            Intent in = new Intent(setFailStatusActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            com.xdel.luke.MainActivity.uponLogin = 0;

            if (pd != null) {
                pd.dismiss();
            }
            startActivity(in);
            setFailStatusActivity.this.finish();

        }
    }

    private String convertBitmapToBase64(Bitmap bm) {
        String encoded;
        byte[] array;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

            array = byteArrayOutputStream.toByteArray();
            encoded = Base64.encodeToString(array, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return encoded;
    }

    private void resetAddressListActivityhfJobsInfoArrJsonStrSQLite() {
        Camera5Activity.mCurrentPhotoPath = null;
        dataT = null;
        imagePaths = null;
        bitmap = null;
        reasons = "";
        ddlSelectedFailureReasonIndex = 0;
        selectedFailureReasonStr = "";
        ddlSelectedIndex = 0;
        selectedStatusCodesIDX = "";
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        MassUpdateOKPwP2PostData = null;
        MassUpdateOKPwP2.massupdateokpwp2PostDataArr = null;
        String[] prefName = new String[]{"searchedval", objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr",
                "massupdateokpwp2PostDataArr", "hfJobInfoJsonStr", "JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
        String[] prefvalue = new String[]{"", "", "", "", "", "", "0", "0"};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
        catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        Intent parentIntent = null;
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        try {
            if (pd != null) {
                pd.dismiss();
            }
            if (JobInfoActivity.frompage != null && JobInfoActivity.frompage.equals("Jobs_Retrieval_Activity")) {
                JobInfoActivity.frompage = null;
                String[] prefName0 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD", "isFromDashboardSub"};
                String[] prefvalue0 = new String[]{"", "", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName0, prefvalue0);

                //objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", "");
                //objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", "");
                //objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", "");
                //objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", "");
                //objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
                //objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                parentIntent = new Intent(this, Jobs_Retrieval_Activity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (!GcmPreferences.getString("isFromMainCOD", "").isEmpty()) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
                parentIntent = new Intent(this, MainCODActivity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                setFailStatusActivity.this.finish();
            } else {
                int TotalJobsCountPerCatTypePerPostal = 0;
                String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
                if (Postal != "") {
                    String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
                    /*TotalJobsCountPerCatTypePerPostal = !objGlobalMethods.IsDefaultVRPpage() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal) :
                            !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(catType, Postal, SelectedVRPJobsIDXs)
                                    : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal);*/
                    TotalJobsCountPerCatTypePerPostal = !isVrpView() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal) :
                            !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(catType, Postal, SelectedVRPJobsIDXs)
                                    : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal);

                    if (TotalJobsCountPerCatTypePerPostal > 0) {
                        //back to JobsListGroupActivity
                        parentIntent = new Intent(setFailStatusActivity.this, JobsListGroupActivity.class);
                        com.xdel.luke.MainActivity.uponLogin = 0;
                        startActivity(parentIntent);
                        setFailStatusActivity.this.finish();
                    } else {
                        //back to DashboardSubActivity or AddressListRoutingActivity or AddressListNewActivity or DrawerMainActivity
                        if (!objGlobalMethods.getGCMPrefs(GcmPreferences, "isFromDashboardSub").isEmpty()) {
                            //back to DashboardSubActivity or AddressListRoutingActivity
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");

                            Integer TotalPIPDIPJobsCountPerPostal = DAO_Jobs.getTotalPIPDIPJobsCountPerPostal(Postal);
                            if (TotalPIPDIPJobsCountPerPostal > 0) {
                                parentIntent = new Intent(this, DashboardSubActivity.class);
                                MainActivity.uponLogin = 0;
                                startActivity(parentIntent);
                                setFailStatusActivity.this.finish();
                            } else {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                                objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                                objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);

                                if (DashboardSubActivity.frompage != null) {
                                    parentIntent = new Intent(this, MapsAllActivity.class);
                                } else {
                                    //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                                    parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                                }

                                MainActivity.uponLogin = 0;
                                startActivity(parentIntent);
                                setFailStatusActivity.this.finish();
                            }
                        } else {
                            //back to AddressListNewActivity or DrawerMainActivity
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                            MainActivity.uponLogin = 0;
                            int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(catType);
                            if (TotalJobsCountPerCatType == 0) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                                //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                                parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                                startActivity(parentIntent);
                                setFailStatusActivity.this.finish();
                            } else {
                                objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                                objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                                parentIntent = new Intent(this, AddressListNewActivity.class);
                                startActivity(parentIntent);
                                setFailStatusActivity.this.finish();
                            }
                        }
                    }
                } else {
                    //back to DashboardSubActivity or AddressListRoutingActivity or AddressListNewActivity or DrawerMainActivity
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                    if (!objGlobalMethods.getGCMPrefs(GcmPreferences, "isFromDashboardSub").isEmpty()) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                        objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                        objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);

                        if (DashboardSubActivity.frompage != null) {
                            parentIntent = new Intent(this, MapsAllActivity.class);
                        } else {
                            //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                            parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                        }

                        MainActivity.uponLogin = 0;
                        startActivity(parentIntent);
                        setFailStatusActivity.this.finish();
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                        MainActivity.uponLogin = 0;
                        int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(catType);
                        if (TotalJobsCountPerCatType == 0) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                            //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                            parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                            startActivity(parentIntent);
                            setFailStatusActivity.this.finish();
                        } else {
                            objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                            parentIntent = new Intent(this, AddressListNewActivity.class);
                            startActivity(parentIntent);
                            setFailStatusActivity.this.finish();
                        }
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity resetAddressListActivityhfJobsInfoArrJsonStrSQLite. Return to Main Dashboard. Exception: " + e.getMessage().toString(), "", "", "", "");
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "searchedval", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName, "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX, "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "massupdateokpwp2PostDataArr", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
            objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
            objGlobalMethods.resetJobsListGroupScrollIndexNpostion(GcmPreferences);
            com.xdel.luke.MainActivity.uponLogin = 0;
            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
            if (pd != null) {
                pd.dismiss();
            }
            //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
            parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            startActivity(parentIntent);
            setFailStatusActivity.this.finish();
        }
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
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playTone() {
        /*player = MediaPlayer.create(setFailStatusActivity.this, R.raw.error);
        player.start();*/
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error));
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity handleIntent. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
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
                    objGlobalMethods.writeToFile("Bluetooth is on at setFailStatusActivity", "", "", "", "");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setFailStatusActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

}
