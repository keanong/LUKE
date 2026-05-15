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
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.ShpmtVerification.ShpmtVActivity;
import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.Spinner_PostDataOKP;
import com.xdel.luke.objClasses.Spinner_PostItemAdapterOKP;
import com.xdel.luke.objClasses.UpdateDOCOD;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objConvertedBitmapToBase64;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objTagging;
import com.xdel.luke.objClasses.objUpdateCOD_PostData;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import android.support.v7.app.ActionBarActivity;

//public class setDeliveredStatusActivity extends ActionBarActivity {
public class setDeliveredStatusActivity extends AppCompatActivity {

    /*
    private static Bitmap mBitmap;
    private static String mCurrentPhotoPath;
    private static final String GIF_FILE_PREFIX = "IMG_";
    private static final String GIF_FILE_SUFFIX = ".gif";
    private static Boolean JobhasCOD = false;
    private static Boolean P2JobhasCOD = false;
    */

    public static Bitmap mBitmap;
    public static boolean failPart2FromPart1Bool = false;
    public static int ddlSelectedIndex = 0;
    public static String ishuman = "0";
    public static String selectedStatusCodesIDX = "";
    public static String selectedRecipient;
    public static String selectedSpecific;
    public static boolean isFromSetOKP = false;
    public static ArrayList<String> imagePaths;
    public static ArrayList<String> SimagePaths;
    public static ArrayList<CustomGallery> dataT;
    public static String mCurrentPhotoPath;
    public static String GIF_FILE_PREFIX = "IMG_";
    public static String GIF_FILE_SUFFIX = ".gif";
    public static String signatureString;
    public static Integer selectedSpecificIDX;
    public static Boolean JobhasCOD = false;
    public static Boolean P2JobhasCOD = false;
    public static String remarks = "";
    public static String fromPage = "";
    public static int accessFromDirection = 0;
    private static int TotalJobsCountPerCatTypePerPostalB4Updates = 0;

    static boolean errored = false;
    //private String regid;
    Context context;
    private ProgressDialog pd, pdreview;
    ConnectionDetector cd;
    private String[] Recipients = new String[]{"Recipient", "Family Member", "Domestic Helper", "Neighbour", "Slot Under Door",
            "Slot In Mailbox", "Colleague", "Reception", "Concierge", "Front Desk", "Mailroom", "Warehouse/Store", "Guardhouse", "Riser", "Others"};
    private Spinner ddlReceiver, ddlSpecific;
    private EditText txtReason;
    private Button btnViewImg, btnCamera, btnCancel, btnClear, btnSubmit, btnNext, btnSign, btnGetReview, btnCallLogImg;
    private CheckBox cbReview;
    File mypath;
    private TextView lblRequirePhotoNote, lblSecurityBagTrueFalse, lblhasOtp, lblhasVerifiedOTP, lblshowSignBasedOnOTP,
            lblReceiverName, lblisContactlessDelivery, lblIsCirclesContactlessCanSlotLBDoorJob, lblgetRecipientNameFromJob;
    private objJobInfo ji;
    private objJobInfo[] subJobInfoAL;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    private int catType;
    private String RecipientTypesJsonStr;
    private boolean dropdownloadedSuccessful = true;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    String convertedBitmapToBase64 = "";
    Bitmap bitmap = null;
    String langType = "en";
    LinearLayout mContent, LLhidden, LLspecific01, LLspecific02, linearLayout1;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    NfcAdapter mNfcAdapter;
    private int CannotTakePhoto;
    private TextChangeListener tc;
    TextView lblIsDefaultVRPpage;
    String PreDeterminedStatusCodeIDX = "";
    //AllCannotTakePhoto: 0 == 0 job cannot take photo, 1 == some cannot take photo, 2 == all cannot take photo

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

        setContentView(R.layout.activity_setdeliveredstatus);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));

        pdreview = new ProgressDialog(this);
        pdreview.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdreview.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

        ddlReceiver = (Spinner) findViewById(R.id.ddlReceiver);
        ddlSpecific = (Spinner) findViewById(R.id.ddlSpecific);
        btnViewImg = (Button) findViewById(R.id.btnViewImg);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        //btnClear = (Button) findViewById(R.id.btnClear);
        btnSign = (Button) findViewById(R.id.btnSign);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnGetReview = (Button) findViewById(R.id.btnGetReview);
        cbReview = findViewById(R.id.cbReview);
        btnCallLogImg = (Button) findViewById(R.id.btnCallLogImg);
        txtReason = (EditText) findViewById(R.id.txtReason);
        lblRequirePhotoNote = (TextView) findViewById(R.id.lblRequirePhotoNote);
        lblSecurityBagTrueFalse = (TextView) findViewById(R.id.lblSecurityBagTrueFalse);

        lblhasOtp = (TextView) findViewById(R.id.lblhasOtp);
        lblhasVerifiedOTP = (TextView) findViewById(R.id.lblhasVerifiedOTP);
        lblshowSignBasedOnOTP = (TextView) findViewById(R.id.lblshowSignBasedOnOTP);
        lblReceiverName = (TextView) findViewById(R.id.lblReceiverName);
        lblisContactlessDelivery = (TextView) findViewById(R.id.lblisContactlessDelivery);
        lblIsCirclesContactlessCanSlotLBDoorJob = (TextView) findViewById(R.id.lblIsCirclesContactlessCanSlotLBDoorJob);
        lblgetRecipientNameFromJob = (TextView) findViewById(R.id.lblgetRecipientNameFromJob);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        LLhidden = (LinearLayout) findViewById(R.id.LLhidden);
        LLspecific01 = (LinearLayout) findViewById(R.id.LLspecific01);
        LLspecific02 = (LinearLayout) findViewById(R.id.LLspecific02);
        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        linearLayout1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });

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

        }
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
            getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        }
        //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        myApp.currentActivity = this;
        context = myApp.getAppContext();

        Gson gson = new Gson();
        PreDeterminedStatusCodeIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, "PreDeterminedStatusCodeIDX");
        boolean PreDeterminedStatusCodeIDXisNonOKP = !objGlobalMethods.IsEmptyString(PreDeterminedStatusCodeIDX) && PreDeterminedStatusCodeIDX.equals("129") ? true : false;
        setTitle(myApp.getAppContext().getString(PreDeterminedStatusCodeIDXisNonOKP ? R.string.title_activity_Rejected_Delivery : R.string.title_activity_set_delivered_status));

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
            objGlobalMethods.writeToFile("setDeliveredStatusActivity !BTEnabled", "", "", "", "");
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
                PendingIntent nfcPendingIntent = PendingIntent.getActivity(myApp.currentActivity, 0, new Intent(myApp.currentActivity, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                mNfcAdapter.enableForegroundDispatch(myApp.currentActivity, nfcPendingIntent, nfcIntentFilter, null);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }

        String[] prefName = new String[]{"isCallLog", "isItemProof", "mChildCheckStates2PrefStr"};
        String[] prefvalue = new String[]{"", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
        JobsHandler.resetrePositionedGroupItems(GcmPreferences);

        JobsHandler.checkForceLogOut();

        //cd = new ConnectionDetector(context);
        cd = new ConnectionDetector(null);

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

        lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

        String RegID = objGlobalMethods.getRegistrationIdOnly();
        CannotTakePhoto = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto"));

        catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        RecipientTypesJsonStr = objGlobalMethods.getStoredRecipientTypes(GcmPreferences);

        //objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
        //Boolean hasOtp = ((currJobInfo.FLAG & 512) == 512) && currJobInfo.PIN != null && !currJobInfo.PIN.isEmpty();
        //Boolean hasVerifiedOTP = false;
        //hasVerifiedOTP = hasOtp && JobsHandler.hasVerifiedOTP(currJobInfo);
        //boolean showSignBasedOnOTP = hasOtp && !hasVerifiedOTP;
        //String ReceiverName = JobsHandler.getRecipientNameFromJob(currJobInfo);
        //boolean isContactlessDelivery = JobsHandler.isContactlessDelivery(currJobInfo);
        ////boolean IsCirclesContactlessCanSlotLBDoorJob = JobsHandler.isCirclesContactlessCanSlotLBDoorJob(currJobInfo); //detect if is CirclesContactless & if got CanLeaveAtDoorstep flag
        //boolean IsCirclesContactlessCanSlotLBDoorJob = false;

        String hasOtpStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "hasOtpStr");
        Boolean hasOtp = !objGlobalMethods.IsEmptyString(hasOtpStr);
        String hasVerifiedOTPStr  = objGlobalMethods.getGCMPrefs(GcmPreferences, "hasVerifiedOTPStr");
        Boolean hasVerifiedOTP = !objGlobalMethods.IsEmptyString(hasVerifiedOTPStr);
        boolean showSignBasedOnOTP = hasOtp && !hasVerifiedOTP;
        String ReceiverName = objGlobalMethods.getGCMPrefs(GcmPreferences, "ReceiverName");
        String isContactlessDeliveryStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "isContactlessDeliveryStr");
        boolean isContactlessDelivery = !objGlobalMethods.IsEmptyString(isContactlessDeliveryStr) ? true : false;
        boolean IsCirclesContactlessCanSlotLBDoorJob = false;


        lblhasOtp.setText(hasOtp ? "1" : "");
        lblhasVerifiedOTP.setText(hasVerifiedOTP ? "1" : "");
        lblshowSignBasedOnOTP.setText(showSignBasedOnOTP ? "1" : "");
        lblReceiverName.setText(ReceiverName);
        lblisContactlessDelivery.setText(isContactlessDelivery ? "1" : "");
        lblIsCirclesContactlessCanSlotLBDoorJob.setText(IsCirclesContactlessCanSlotLBDoorJob ? "1" : "");
        //lblgetRecipientNameFromJob.setText(JobsHandler.getRecipientNameFromJob(currJobInfo));
        lblgetRecipientNameFromJob.setText(ReceiverName);

        if (SignatureNameActivity.requireSignature && hasOtp && hasVerifiedOTP) {
            SignatureNameActivity.requireSignature = false;
        }
        Boolean isResidential = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isResidential")) ? true : false;
        if (isResidential && hasOtp && hasVerifiedOTP) {
            objGlobalMethods.setGCMPrefs(GcmPreferences, "isResidential", "");
        }

        String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
        setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
        if (!RecipientTypesJsonStr.equals("")) {
            loadddlReceiver3(RecipientTypesJsonStr);
            if (dropdownloadedSuccessful) {
                ddlReceiver.setSelection(ddlSelectedIndex);
                if (CannotTakePhoto > 0) {
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnViewImg);
                } else if (CannotTakePhoto == 0) {
                    if (Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0)) {
                        ctrlSettings.enableDisableCommonButtonWithStyles(true, btnViewImg);
                    } else {
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnViewImg);
                    }
                }
            } else {
                JobsHandler.downloadRecipientTypesWS task = new JobsHandler.downloadRecipientTypesWS(RegID, GcmPreferences);
                task.execute();
                showErrorAlert(myApp.getAppContext().getString(R.string.alert_noRecipienttype));
            }
        } else {
            JobsHandler.downloadRecipientTypesWS task = new JobsHandler.downloadRecipientTypesWS(RegID, GcmPreferences);
            task.execute();
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_noRecipienttype));
        }

        objGlobalMethods.setGCMPrefs(GcmPreferences, "failPart2FromPart1Bool", "f");

        //JobhasCOD = JobsHandler.checkForCODwithCatType(currJobInfo, 2);
        //P2JobhasCOD = JobsHandler.checkPart2ForCOD(currJobInfo, GcmPreferences);
        //Boolean isSecurityBag = JobsHandler.reqSecurityTag(currJobInfo);
        //lblSecurityBagTrueFalse.setText(isSecurityBag ? "true" : "");

        String JobhasCODStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "JobhasCODStr");
        String P2JobhasCODStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "P2JobhasCODStr");
        String isSecurityBagStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "isSecurityBagStr");
        JobhasCOD = !objGlobalMethods.IsEmptyString(JobhasCODStr) ? true : false;
        P2JobhasCOD = !objGlobalMethods.IsEmptyString(P2JobhasCODStr) ? true : false;
        Boolean isSecurityBag = !objGlobalMethods.IsEmptyString(isSecurityBagStr) ? true : false;
        lblSecurityBagTrueFalse.setText(isSecurityBag ? "true" : "");


        if ((JobhasCOD || P2JobhasCOD) && objGlobalMethods.IsEmptyString(PreDeterminedStatusCodeIDX)) {
            btnNext.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        } else {
            btnNext.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        }

        txtReason.setText(remarks == null || remarks.equals("") ? "" : remarks);

        String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
        TotalJobsCountPerCatTypePerPostalB4Updates = DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal);

        //btnSign.setVisibility(isContactlessDelivery ? View.GONE : View.VISIBLE);
        //btnGetReview.setVisibility(isContactlessDelivery ? View.GONE : View.VISIBLE); // no longer in use since 05/11/2020
        cbReview.setVisibility(isContactlessDelivery ? View.GONE : View.VISIBLE);
        cbReview.setOnCheckedChangeListener(null);
        cbReview.setChecked(objGlobalMethods.cbReviewSelected());
        cbReview.setOnCheckedChangeListener(cbReviewOnCheckedChangeListener());

        /*try {
            String reqItemProofImg = objGlobalMethods.getGCMPrefs(GcmPreferences, "reqItemProofImg");
            if (!objGlobalMethods.IsEmptyString(reqItemProofImg) && accessFromDirection == 1) {

                if (JobsHandler.hasImgFileMoreThan7MinOld(imagePaths, dataT, null)) {
                    accessFromDirection = 0;
                    objGlobalMethods.showDialog(this, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.reqitemproofimg0)).show();
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity onResume. Exception: " + e.getMessage(), "", "", "", "");
        }*/
    }

    private void loadddlReceiver3(String aRecipientTypesJsonStr) {
        ArrayList<Spinner_PostDataOKP> SpinnerPostDataArr = new ArrayList<Spinner_PostDataOKP>();
        try {
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            SpinnerPostDataArr = JobsHandler.rtnRecipientTypeslatest3(GcmPreferences, aRecipientTypesJsonStr, langType);
            //Spinner_PostItemAdapterOKP adapter = new Spinner_PostItemAdapterOKP(this, SpinnerPostDataArr);
            Spinner_PostItemAdapterOKP adapter = new Spinner_PostItemAdapterOKP(myApp.getAppContext(), SpinnerPostDataArr);
            ddlReceiver.setAdapter(adapter);
            //ddlReceiver.setSelection(0, false);
            ddlReceiver.setOnItemSelectedListener(ddlReceiverOnItemSelectedListener());
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity loadddlReceiver3. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            dropdownloadedSuccessful = false;
        }
    }

    private AdapterView.OnItemSelectedListener ddlReceiverOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                try {
                    //processSelected(position, true);
                    ddlSelectedIndex = position;
                    TextView lblStatusCodesDesc = ((TextView) (view.findViewById(R.id.lblStatusCodesDesc)));
                    TextView lblDescEN = ((TextView) (view.findViewById(R.id.lblStatusCodesDescEN)));
                    TextView lblInstructions = ((TextView) (view.findViewById(R.id.lblInstructions)));
                    TextView lblFlag = ((TextView) (view.findViewById(R.id.lblFlag)));

                    Boolean isSecurityBag = !lblSecurityBagTrueFalse.getText().toString().equals("") ? true : false;
                    boolean RecipientTypeRequireRemarks = false;
                    boolean remarksIsEmpty = objGlobalMethods.IsEmptyString(txtReason.getText().toString());
                    boolean reqRemarks = false;
                    LLspecific01.setVisibility(View.GONE);
                    LLspecific02.setVisibility(View.GONE);
                    LLhidden.setVisibility(View.VISIBLE);
                    txtReason.removeTextChangedListener(tc);

                    /*TextView vlblhasOtp = ((TextView) (view.findViewById(R.id.lblhasOtp)));
                    TextView vlblhasVerifiedOTP = ((TextView) (view.findViewById(R.id.lblhasVerifiedOTP)));
                    TextView vlblshowSignBasedOnOTP = ((TextView) (view.findViewById(R.id.lblshowSignBasedOnOTP)));
                    TextView vlblReceiverName = ((TextView) (view.findViewById(R.id.lblReceiverName)));
                    TextView vlblisContactlessDelivery = ((TextView) (view.findViewById(R.id.lblisContactlessDelivery)));
                    TextView vlblIsCirclesContactlessCanSlotLBDoorJob = ((TextView) (view.findViewById(R.id.lblIsCirclesContactlessCanSlotLBDoorJob)));
                    TextView vlblgetRecipientNameFromJob = ((TextView) (view.findViewById(R.id.lblgetRecipientNameFromJob)));*/

                    Boolean hasOtp = !objGlobalMethods.IsEmptyString(lblhasOtp.getText().toString());
                    /*Boolean hasVerifiedOTP = false;
                    hasVerifiedOTP = !objGlobalMethods.IsEmptyString(lblhasVerifiedOTP.getText().toString());*/
                    boolean showSignBasedOnOTP = !objGlobalMethods.IsEmptyString(lblshowSignBasedOnOTP.getText().toString());
                    String RecipientNameFromJob = lblgetRecipientNameFromJob.getText().toString();

                    if (position == 0) {
                        ishuman = "0";
                        //if (auto) {
                        lblStatusCodesDesc.setBackgroundColor(Color.RED);
                        lblStatusCodesDesc.setTextColor(Color.WHITE);
                        //}

                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnNext);
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnSign);
                        SignatureNameActivity.ReceiverName = "";
                        //btnGetReview.setVisibility(View.GONE); // no longer in use since 05/11/2020
                        cbReview.setVisibility(View.GONE);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cbReviewSelected", "");
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCallLogImg);
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnViewImg);
                        if (CannotTakePhoto > 0) {
                            hideshownote2(true, "CannotTakePhoto", "");
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnViewImg);
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCamera);
                        } else {
                            hideshownote2(false, "", "");
                        }
                        boolean CallLogImgTaken = CallLogImgTaken();
                        String okpShowReqCallLogImgButton = objGlobalMethods.getGCMPrefs(GcmPreferences, "okpShowReqCallLogImgButton");
                        ctrlSettings.enableDisableCommonButtonWithStyles(okpShowReqCallLogImgButton.equals("t") ? true : false, btnCallLogImg);
                        ctrlSettings.enableDisableCommonButtonWithStyles(
                                ((okpShowReqCallLogImgButton.equals("t") && CallLogImgTaken) && CannotTakePhoto <= 0 || CannotTakePhoto <= 0) ? true : false, btnCamera);
                        ctrlSettings.enableDisableCommonButtonWithStyles(
                                (okpShowReqCallLogImgButton.equals("t") && CallLogImgTaken) ? true : false, btnViewImg);

                        if (okpShowReqCallLogImgButton.equals("t") && !CallLogImgTaken) {
                            //hideshownote(true, "calllog");
                            hideshownote2(true, "calllog", "");
                        }/* else if (hasItemProofImgFileMoreThan7MinOld) {
                        hideshownote(true, "hasItemProofImgFileMoreThan7MinOld");
                    }*/
                        LLspecific01.setVisibility(View.GONE);
                        LLspecific02.setVisibility(View.GONE);
                        LLhidden.setVisibility(View.VISIBLE);
                        selectedSpecificIDX = null;
                    } else {
                        //if (auto) {
                        lblStatusCodesDesc.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                        lblStatusCodesDesc.setTextColor(Color.WHITE);
                        //}
                        String requirePhoto = ((TextView) (view.findViewById(R.id.lblStatusCodesIDX))).getText().toString();
                        String requiredName = ((TextView) (view.findViewById(R.id.lblName_Required))).getText().toString();
                        String instructions = lblInstructions.getText().toString();
                        TextView lblsubcatEN = ((TextView) (view.findViewById(R.id.lblsubcatEN)));
                        TextView lblsubcatZH = ((TextView) (view.findViewById(R.id.lblsubcatZH)));
                        String subcatEN = lblsubcatEN.getText().toString();
                        String subcatZH = lblsubcatZH.getText().toString();
                        String flagstr = lblFlag.getText().toString();

                        ishuman = ((TextView) (view.findViewById(R.id.lblIsHuman))).getText().toString();
                        selectedRecipient = ((TextView) (view.findViewById(R.id.lblStatusCodesDescEN))).getText().toString();
                        /*Gson gson = new Gson();
                        objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                        Boolean hasOtp = ((currJobInfo.FLAG & 512) == 512) && currJobInfo.PIN != null && !currJobInfo.PIN.isEmpty();
                        Boolean hasVerifiedOTP = false;
                        hasVerifiedOTP = hasOtp && JobsHandler.hasVerifiedOTP(currJobInfo);
                        boolean showSignBasedOnOTP = hasOtp && !hasVerifiedOTP;*/


                        boolean CallLogImgTaken = CallLogImgTaken();
                        String okpShowReqCallLogImgButton = objGlobalMethods.getGCMPrefs(GcmPreferences, "okpShowReqCallLogImgButton");

                        ctrlSettings.enableDisableCommonButtonWithStyles(okpShowReqCallLogImgButton.equals("t") ? true : false, btnCallLogImg);
                        int CirclesLogicType = -1; // -1 = proceed, 0 = no call, 1 = 1 call, 2 = 2 or more & nid photo, 3 = calllogimg taken
                        boolean isContactlessDelivery = !objGlobalMethods.IsEmptyString(lblisContactlessDelivery.getText().toString());

                        if (selectedRecipient.toLowerCase().equals("recipient")) {
                            if (hasOtp) {
                                ctrlSettings.enableDisableCommonButtonWithStyles(showSignBasedOnOTP ? true : false, btnSign);
                            } else {
                                ctrlSettings.enableDisableCommonButtonWithStyles(true, btnSign);
                            }

                            /*SignatureNameActivity.ReceiverName = JobsHandler.getRecipientNameFromJob(currJobInfo);
                            boolean isContactlessDelivery = JobsHandler.isContactlessDelivery(currJobInfo);*/
                            SignatureNameActivity.ReceiverName = lblReceiverName.getText().toString();

                            String jfs = objGlobalMethods.getGCMPrefs(GcmPreferences, "jobflag");
                            jfs = !jfs.equals("") ? jfs : "0";
                            Integer jobflag = 0;
                            try {
                                jobflag = Integer.parseInt(jfs);
                            } catch (Exception e) {
                                objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity ddlReceiver.setOnItemSelectedListener convert jobflag. Exception: " + e.getMessage().toString(), "", "", "", "");
                                jobflag = 0;
                            }
                            Boolean canGetReviewLink = (JobInfoActivity.CanGetReviewLinkJobsIDX != null && JobInfoActivity.CanGetReviewLinkJobsIDX > 0) && (jobflag & 16777216) == 16777216;
                            //btnGetReview.setVisibility(!isContactlessDelivery && canGetReviewLink ? View.VISIBLE : View.GONE); // no longer in use since 05/11/2020
                            cbReview.setVisibility(!isContactlessDelivery && canGetReviewLink ? View.VISIBLE : View.GONE);
                        } else {
                            //ctrlSettings.enableDisableCommonButtonWithStyles(true, btnSign);
                            if (ishuman.equals("1")) {
                                if (hasOtp) {
                                    ctrlSettings.enableDisableCommonButtonWithStyles(showSignBasedOnOTP ? true : false, btnSign);
                                } else {
                                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnSign);
                                }
                            } else {
                                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnSign);
                            }
                            String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                            boolean HasAllValidTelNums = !objGlobalMethods.IsEmptyString(AllValidTelNums);
                            //boolean IsCirclesContactlessCanSlotLBDoorJob = JobsHandler.isCirclesContactlessCanSlotLBDoorJob(currJobInfo); //detect if is CirclesContactless & if got CanLeaveAtDoorstep flag
                            boolean IsCirclesContactlessCanSlotLBDoorJob = !objGlobalMethods.IsEmptyString(lblIsCirclesContactlessCanSlotLBDoorJob.getText().toString()); //detect if is CirclesContactless & if got CanLeaveAtDoorstep flag
                            boolean IsSlotLbDoor = (selectedRecipient.toLowerCase().equals("slot in mailbox") || selectedRecipient.toLowerCase().equals("slot under door"));
                            boolean IsDoorstep = selectedRecipient.toLowerCase().equals("leave at doorstep");
                            boolean isSlotLbDoorGuard = (selectedRecipient.toLowerCase().equals("slot in mailbox") || selectedRecipient.toLowerCase().equals("slot under door") || selectedRecipient.toLowerCase().equals("guardhouse"));
                            boolean isGuard = selectedRecipient.toLowerCase().equals("guardhouse");
                            String exemptFromCallingForLBDoorGuard = objGlobalMethods.getGCMPrefs(GcmPreferences, "exemptFromCallingForLBDoorGuard");
                            String noCallMade = objGlobalMethods.getGCMPrefs(GcmPreferences, "noCallMade");
                            String totalnumofcallstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalnumofcall");
                            String reqCallLogImg = objGlobalMethods.getGCMPrefs(GcmPreferences, "reqCallLogImg");
                            int totalnumofcall = totalnumofcallstr.equals("") ? 0 : Integer.parseInt(totalnumofcallstr);
                            int flag = 0;
                            if (!objGlobalMethods.IsEmptyString(flagstr)) {
                                try {
                                    flag = Integer.parseInt(flagstr);
                                } catch (Exception e) {
                                    objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity ddlReceiver.setOnItemSelectedListener convert flag. Exception: " + e.getMessage(), "", "", "", "");
                                }
                            }

                            RecipientTypeRequireRemarks = JobsHandler2.RecipientTypeRequireRemarks(flag);

                            //if (IsCirclesContactlessCanSlotLBDoorJob && IsSlotLbDoor && HasAllValidTelNums) {
                            //if ((IsCirclesContactlessCanSlotLBDoorJob && IsSlotLbDoor && HasAllValidTelNums) || (isSlotLbDoorGuard && HasAllValidTelNums && objGlobalMethods.IsEmptyString(exemptFromCallingForLBDoorGuard))) {

                            /*if ((HasAllValidTelNums && objGlobalMethods.IsEmptyString(exemptFromCallingForLBDoorGuard)) &&
                                    ((IsCirclesContactlessCanSlotLBDoorJob && IsSlotLbDoor) || isSlotLbDoorGuard || IsDoorstep)) {*/
                            if ((HasAllValidTelNums && objGlobalMethods.IsEmptyString(exemptFromCallingForLBDoorGuard)) &&
                                    ((isContactlessDelivery && IsSlotLbDoor) || isSlotLbDoorGuard || IsDoorstep)) {
                                CirclesLogicType = totalnumofcall == 0 && reqCallLogImg.equals("t") ? 0 :
                                        totalnumofcall >= 0 && noCallMade.equals("t") && !CallLogImgTaken && reqCallLogImg.equals("t") && totalnumofcall < 2 ? 1 :
                                                totalnumofcall >= 0 && noCallMade.equals("t") && !CallLogImgTaken && reqCallLogImg.equals("t") && totalnumofcall >= 2 ? 2 :
                                                        totalnumofcall > 0 && noCallMade.equals("t") && CallLogImgTaken ? 3 : -1;
                            }


                            //if (ishuman.equals("1") && !objGlobalMethods.IsEmptyString(SignatureNameActivity.ReceiverName) && SignatureNameActivity.ReceiverName.equals(JobsHandler.getRecipientNameFromJob(currJobInfo))) {
                            if (ishuman.equals("1") && !objGlobalMethods.IsEmptyString(SignatureNameActivity.ReceiverName) && SignatureNameActivity.ReceiverName.equals(RecipientNameFromJob)) {
                                SignatureNameActivity.ReceiverName = "";
                            } else if ((!ishuman.equals("1") || (isGuard && objGlobalMethods.IsEmptyString(exemptFromCallingForLBDoorGuard))) && CirclesLogicType > -1) {
                                Context ctx = myApp.getAppContext();
                                SignatureNameActivity.ReceiverName = "";
                                signatureString = "";
                                selectedSpecificIDX = null;
                                instructions = CirclesLogicType == 0 ? ctx.getString(R.string.nocallviasystemdiptoinfd) :
                                        CirclesLogicType == 1 ? ctx.getString(R.string.numcallsmade) + " " + String.valueOf(totalnumofcall) + "\n" + ctx.getString(R.string.nocallviasystemdiptoinfd2x) :
                                                CirclesLogicType == 2 ? ctx.getString(R.string.takecalllogphotodip0) :
                                                        instructions;
                                okpShowReqCallLogImgButton = "t";
                            } else if (!ishuman.equals("1")) {
                                SignatureNameActivity.ReceiverName = "";
                                signatureString = "";
                                selectedSpecificIDX = null;
                            }
                            //btnGetReview.setVisibility(View.GONE); // no longer in use since 05/11/2020
                            cbReview.setVisibility(View.GONE);
                        }

                        reqRemarks = RecipientTypeRequireRemarks && remarksIsEmpty;
                        enabledisableSubmitNextBtns2(requirePhoto, lblDescEN.getText().toString(), okpShowReqCallLogImgButton, CallLogImgTaken, instructions, CirclesLogicType, reqRemarks);

                        if (RecipientTypeRequireRemarks) {
                            try {
                                tc = new TextChangeListener(requirePhoto, lblDescEN.getText().toString(), okpShowReqCallLogImgButton, CallLogImgTaken, instructions, CirclesLogicType, reqRemarks);
                                txtReason.addTextChangedListener(tc);
                            } catch (Exception e) {
                                String s = JobsHandler.getStackTraceString(e);
                                objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity ddlReceiver.setOnItemSelectedListener onTextChangeListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                            }
                        } else {
                            txtReason.removeTextChangedListener(tc);
                        }

                        boolean loadgetsubCat = !objGlobalMethods.IsEmptyString(subcatEN) && !objGlobalMethods.IsEmptyString(subcatZH);
                        if (loadgetsubCat) {
                            //loadSpecificDDL(subcatEN, subcatZH);
                            loadSpecificDDL(subcatEN, subcatZH, requirePhoto, lblDescEN.getText().toString(), okpShowReqCallLogImgButton, CallLogImgTaken, instructions, CirclesLogicType, RecipientTypeRequireRemarks);
                        } else {
                            selectedSpecific = "";
                        }
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity ddlReceiver.setOnItemSelectedListener. Selected position: "
                            + position + ". btnSubmit is enabled. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");

                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private void enabledisableSubmitNextBtns2(String RequiredNumOfPhotostr, String desc, String okpShowReqCallLogImgButton, boolean CallLogImgTaken,
                                              String instructions, int CirclesLogicType, boolean RecipientTypeRequireRemarks) {
        if (okpShowReqCallLogImgButton.equals("t") && !CallLogImgTaken) {
            desc = "calllog";
        }

        Context ctx = myApp.getAppContext();
        int normPixCount = 0;
        int allPixCount = 0;
        try {
            if (imagePaths != null && imagePaths.size() > 0 && dataT != null && dataT.size() > 0) {
                for (int i = 0; i <= dataT.size() - 1; i++) {
                    allPixCount++;
                    if (!dataT.get(i).isCallLog && !dataT.get(i).isItemProof) {
                        normPixCount++;
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity enabledisableSubmitNextBtns get normPixCount. Exception: " + e.getMessage(), "", "", "", "");
        }

        //purposely put this here in order to skip CirclesLogicType checks
        if (CirclesLogicType == 3) {
            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCallLogImg);
            CirclesLogicType = -1;
        }

        if (CirclesLogicType > -1) {
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnNext);
            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCamera);
            ctrlSettings.enableDisableCommonButtonWithStyles(CirclesLogicType == 2 ? true : false, btnCallLogImg);
            hideshownote2(true, "", instructions);
        } else if (CannotTakePhoto > 0) {
            hideshownote2(true, "CannotTakePhoto", "");
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnNext);
            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCamera);

            if (okpShowReqCallLogImgButton.equals("t")) {
                ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCallLogImg);
                ctrlSettings.enableDisableCommonButtonWithStyles(CallLogImgTaken ? true : false, btnViewImg);
                ctrlSettings.enableDisableSubmitButtonWithStyles(CallLogImgTaken ? true : false, btnSubmit);
                ctrlSettings.enableDisableSubmitButtonWithStyles(CallLogImgTaken ? true : false, btnNext);
            } else {
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCallLogImg);
                ctrlSettings.enableDisableCommonButtonWithStyles(allPixCount > 0 ? true : false, btnViewImg);
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnSubmit);
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnNext);
            }
        } else {
            if (RequiredNumOfPhotostr.equals("0") &&
                    ((Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0)) ||
                            (Camera5Activity.mCurrentPhotoPath == null || (imagePaths == null || imagePaths.size() == 0)))) {
                if (okpShowReqCallLogImgButton.equals("t")) {
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCallLogImg);
                    ctrlSettings.enableDisableCommonButtonWithStyles(CallLogImgTaken ? true : false, btnViewImg);
                    ctrlSettings.enableDisableCommonButtonWithStyles(CallLogImgTaken ? true : false, btnCamera);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(CallLogImgTaken ? true : false, btnSubmit);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(CallLogImgTaken ? true : false, btnNext);
                } else {
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCallLogImg);
                    ctrlSettings.enableDisableCommonButtonWithStyles(allPixCount > 0 ? true : false, btnViewImg);
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCamera);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnSubmit);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnNext);
                }
            } else if (!RequiredNumOfPhotostr.equals("0")) {
                if (okpShowReqCallLogImgButton.equals("t")) {
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCallLogImg);
                    ctrlSettings.enableDisableCommonButtonWithStyles(CallLogImgTaken ? true : false, btnViewImg);
                    ctrlSettings.enableDisableCommonButtonWithStyles(CallLogImgTaken ? true : false, btnCamera);
                } else {
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCallLogImg);
                    ctrlSettings.enableDisableCommonButtonWithStyles(allPixCount > 0 ? true : false, btnViewImg);
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnCamera);
                }

                if (normPixCount < Integer.parseInt(RequiredNumOfPhotostr)) {
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnNext);
                } else if (normPixCount >= Integer.parseInt(RequiredNumOfPhotostr)) {
                    if (okpShowReqCallLogImgButton.equals("t")) {
                        ctrlSettings.enableDisableSubmitButtonWithStyles(CallLogImgTaken && normPixCount > 0 && normPixCount >= Integer.parseInt(RequiredNumOfPhotostr) ? true : false, btnSubmit);
                        ctrlSettings.enableDisableSubmitButtonWithStyles(CallLogImgTaken && normPixCount > 0 && normPixCount >= Integer.parseInt(RequiredNumOfPhotostr) ? true : false, btnNext);
                    } else {
                        ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnSubmit);
                        ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnNext);
                    }
                }
            }

            if (RecipientTypeRequireRemarks) {
                desc = "remarks";
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnNext);
            }

            if (desc.equals("remarks")) {
                hideshownote2(true, desc, objGlobalMethods.IsEmptyString(instructions) ? ctx.getString(R.string.lbl_enterremarkswhowhere) : instructions);
            } else if (desc.equals("calllog")) {
                hideshownote2(true, desc, instructions);
            } else if (desc.equals("hasItemProofImgFileMoreThan7MinOld")) {
                hideshownote2(true, desc, instructions);
            } else if (!objGlobalMethods.IsEmptyString(desc)) {
                hideshownote2(true, "", instructions);
            } else if (!RequiredNumOfPhotostr.equals("0")) {
                hideshownote2(true, "others", instructions);
            } else {
                hideshownote2(false, "", "");
            }
        }
    }

    private class TextChangeListener implements TextWatcher {
        private final String RequiredNumOfPhotostr;
        private final String desc;
        private final String okpShowReqCallLogImgButton;
        private final boolean CallLogImgTaken;
        private final String instructions;
        private final int CirclesLogicType;
        private final boolean RecipientTypeRequireRemarks;


        public TextChangeListener(String aRequiredNumOfPhotostr, String adesc, String aokpShowReqCallLogImgButton, boolean aCallLogImgTaken,
                                  String ainstructions, int aCirclesLogicType, boolean aRecipientTypeRequireRemarks) {
            this.RequiredNumOfPhotostr = aRequiredNumOfPhotostr;
            this.desc = adesc;
            this.okpShowReqCallLogImgButton = aokpShowReqCallLogImgButton;
            this.CallLogImgTaken = aCallLogImgTaken;
            this.instructions = ainstructions;
            this.CirclesLogicType = aCirclesLogicType;
            this.RecipientTypeRequireRemarks = aRecipientTypeRequireRemarks;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                enabledisableSubmitNextBtns2(RequiredNumOfPhotostr, desc, okpShowReqCallLogImgButton, CallLogImgTaken,
                        instructions, CirclesLogicType, RecipientTypeRequireRemarks && objGlobalMethods.IsEmptyString(txtReason.getText().toString()) ? true : false);
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity TextChangeListener. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void loadSpecificDDL(String subCatENJsonStr, String subCatZHJsonStr, final String aRequiredNumOfPhotostr, final String adesc, final String aokpShowReqCallLogImgButton,
                                 final boolean aCallLogImgTaken, final String ainstructions, final int aCirclesLogicType, final boolean RecipientTypeRequireRemarks) {
        String[] value_splitEN = null;
        String[] value_splitZH = null;
        Spinner_PostDataOKP d = null;
        String langType = "en";
        String selectedsubCatIdxStr = "";
        int selectedsubCatIdx = 0;
        Context ctx = myApp.getAppContext();

        try {
            value_splitEN = subCatENJsonStr.split("\\|");
            value_splitZH = subCatZHJsonStr.split("\\|");
            if (value_splitEN.length > 1 && value_splitZH.length > 1 && value_splitEN.length == value_splitZH.length) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
                ArrayList<Spinner_PostDataOKP> SpinnerPostDataArr = new ArrayList<Spinner_PostDataOKP>();
                d = new Spinner_PostDataOKP(ctx.getString(R.string.spinnerSelect), "[PLEASE SELECT]", "1", "0", "0", "", "", "", 0);
                SpinnerPostDataArr.add(d);
                for (int i = 0; i <= value_splitEN.length - 1; i++) {
                    d = new Spinner_PostDataOKP((langType.equals("en") || langType.equals("")) ? value_splitEN[i] : value_splitZH[i],
                            value_splitEN[i], "0", "0", "0", "", "", "", 0);
                    SpinnerPostDataArr.add(d);
                }

                //Spinner_PostItemAdapterOKP adapter = new Spinner_PostItemAdapterOKP(this, SpinnerPostDataArr);
                Spinner_PostItemAdapterOKP adapter = new Spinner_PostItemAdapterOKP(myApp.getAppContext(), SpinnerPostDataArr);
                ddlSpecific.setAdapter(adapter);

                ddlSpecific.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        processSelectedSpecific(position, true, aRequiredNumOfPhotostr, adesc, aokpShowReqCallLogImgButton, aCallLogImgTaken, ainstructions, aCirclesLogicType, RecipientTypeRequireRemarks);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if (selectedSpecificIDX != null) {
                    ddlSpecific.setSelection(selectedSpecificIDX);
                }
                if (SpinnerPostDataArr.size() > 1) {
                    LLspecific01.setVisibility(View.VISIBLE);
                    LLspecific02.setVisibility(View.VISIBLE);
                    LLhidden.setVisibility(View.GONE);
                }
            } else {
                objGlobalMethods.writeToFile("SpecificDDL not loaded. EN n ZH length not matched.", "", "", "", "");
                LLspecific01.setVisibility(View.GONE);
                LLspecific02.setVisibility(View.GONE);
                LLhidden.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity loadSpecificDDL. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            LLspecific01.setVisibility(View.GONE);
            LLspecific02.setVisibility(View.GONE);
            LLhidden.setVisibility(View.VISIBLE);
        }
    }

    private void processSelectedSpecific(int pos, boolean auto, final String aRequiredNumOfPhotostr, final String adesc, final String aokpShowReqCallLogImgButton,
                                         final boolean aCallLogImgTaken, final String ainstructions, final int aCirclesLogicType, boolean RecipientTypeRequireRemarks) {
        String selectedsubCat = "";
        try {
            if (ddlSpecific != null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                TextView lblStatusCodesDesc = ((TextView) (ddlSpecific.getSelectedView().findViewById(R.id.lblStatusCodesDesc)));

                if (pos == 0) {
                    lblStatusCodesDesc.setBackgroundColor(Color.RED);
                    lblStatusCodesDesc.setTextColor(Color.WHITE);
                    selectedSpecificIDX = 0;
                    selectedSpecific = "";
                } else {
                    lblStatusCodesDesc.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                    lblStatusCodesDesc.setTextColor(Color.WHITE);
                    TextView lblDescEN = ((TextView) (ddlSpecific.getSelectedView().findViewById(R.id.lblStatusCodesDescEN)));
                    selectedsubCat = lblDescEN.getText().toString();
                    lblStatusCodesDesc.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                    lblStatusCodesDesc.setTextColor(Color.WHITE);
                    selectedSpecificIDX = pos;
                    selectedSpecific = " (" + selectedsubCat + ")";

                    boolean remarksIsEmpty = objGlobalMethods.IsEmptyString(txtReason.getText().toString());
                    boolean reqRemarks = (objGlobalMethods.IsEqualString(selectedsubCat, "OTHERS") ? true : false) && remarksIsEmpty;
                    enabledisableSubmitNextBtns2(aRequiredNumOfPhotostr, adesc, aokpShowReqCallLogImgButton, aCallLogImgTaken, ainstructions, aCirclesLogicType, reqRemarks);

                    if (objGlobalMethods.IsEqualString(selectedsubCat, "OTHERS")) {
                        tc = new TextChangeListener(aRequiredNumOfPhotostr, adesc, aokpShowReqCallLogImgButton, aCallLogImgTaken, ainstructions, aCirclesLogicType, reqRemarks);
                        txtReason.addTextChangedListener(tc);
                    } else {
                        txtReason.removeTextChangedListener(tc);
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity processSelected. There was an error when selecting relation. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remarks = null;
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();

        GcmPreferences = null;
        UpdateJobInfoPreferences = null;
        mContent = null;
        LLhidden = null;
        LLspecific01 = null;
        LLspecific02 = null;
        linearLayout1 = null;

        if (pd != null) {
            pd.dismiss();
            pd = null;
        }

        if (pdreview != null) {
            pdreview.dismiss();
            pdreview = null;
        }

        ddlReceiver = null;
        ddlSpecific = null;
        btnViewImg = null;
        btnCamera = null;
        btnCancel = null;
        btnSign = null;
        btnSubmit = null;
        btnNext = null;
        btnGetReview = null;
        cbReview = null;
        btnCallLogImg = null;
        txtReason = null;
        lblRequirePhotoNote = null;
        lblSecurityBagTrueFalse = null;
        mNfcAdapter = null;
        LLhidden = null;
        LLspecific01 = null;
        LLspecific02 = null;
        linearLayout1 = null;
        cd = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        remarks = txtReason.getText().toString();
        try {
            if (mNfcAdapter != null)
                mNfcAdapter.disableForegroundDispatch(this);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void BackToLoginPage() {
        Camera5Activity.mCurrentPhotoPath = null;
        dataT = null;
        imagePaths = null;
        SimagePaths = null;
        bitmap = null;
        remarks = "";
        SignatureNameActivity.ReceiverName = "";
        signatureString = "";
        selectedSpecificIDX = null;
        ddlSelectedIndex = 0;
        ishuman = "0";
        SignatureNameActivity.requireSignature = false;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "massupdateokpwp2PostDataArr", "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");

        Intent parentIntent;
        //parentIntent = new Intent(setDeliveredStatusActivity.this, MainActivity.class);
        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
        //objGlobalMethods.stopAlarmForCommon();
        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(2);
        }
        FCMservice.notiNotRead = false;
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
        JobsHandler.clearGPSQue(LocationPrefs);
        DAO_Jobs.dropLukeDB();
        startActivity(parentIntent);
        finish();
    }

    private Dialog showDialog(String msg) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
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

                if (Build.VERSION.SDK_INT < 17) {
                    try {
                        Intent intentAirplaneMode = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                        intentAirplaneMode.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentAirplaneMode);
                    } catch (ActivityNotFoundException e) {
                        Date dt = new Date();
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at setDeliveredStatusActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at setDeliveredStatusActivity showChangePermissionDialog. ActivityNotFoundException: "
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
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "OK", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                Camera5Activity.mCurrentPhotoPath = null;
                dataT = null;
                imagePaths = null;
                SimagePaths = null;
                bitmap = null;
                remarks = "";
                SignatureNameActivity.ReceiverName = "";
                signatureString = "";
                selectedSpecificIDX = null;
                ddlSelectedIndex = 0;
                ishuman = "0";
                SignatureNameActivity.requireSignature = false;
                bitmap = null;
                MassUpdateOKPwP2.massupdateokpwp2PostDataArr = null;
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName, "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX, "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "massupdateokpwp2PostDataArr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "selectedRouteAddressJobsIDXs", "");
                Intent i;

                //i = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                MainActivity.uponLogin = 0;

                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSignatureAlert3(final boolean reqSign, final boolean reqName, final boolean reqSeal, final boolean reqStaffID, final String aPreDeterminedStatusCodeIDX) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setCancelable(false);
            String s = objGlobalMethods.getOKPrequirementMsgNew(reqSign, reqName, reqSeal, reqStaffID, aPreDeterminedStatusCodeIDX);
            builder.setMessage(s);
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "OK", then he is allowed to exit from application
                    try {
                        remarks = txtReason.getText().toString();
                        Intent intent = null;
                        if (reqSeal) {
                            SecuritySealActivity.nextActivtyIntent = new Intent(myApp.currentActivity, SignatureNameActivity.class);
                            SignatureNameActivity.rtnPageName = "setDeliveredStatusActivity";
                            intent = new Intent(myApp.currentActivity, SecuritySealActivity.class);
                        } else {
                            SignatureNameActivity.rtnPageName = "setDeliveredStatusActivity";
                            intent = new Intent(myApp.currentActivity, SignatureNameActivity.class);
                        }
                        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                        String[] prefName = new String[]{"reqSign", "reqName"};
                        String[] prefvalue = new String[]{reqSign ? "y" : "", reqName ? "y" : ""};
                        objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
                        startActivity(intent);
                    } catch (Exception e) {
                        String ss = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity showSignatureAlert3 setPositiveButton onClick. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
                    }
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity showSignatureAlert3. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    private void showSignatureAlert2(final boolean reqSign, final boolean reqSeal, final boolean reqStaffID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        String s = objGlobalMethods.getOKPrequirementMsg(reqSign, reqSeal, reqStaffID);
        builder.setMessage(s);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "OK", then he is allowed to exit from application
                remarks = txtReason.getText().toString();
                Intent intent = null;
                if (reqSeal) {
                    /*SecuritySealActivity.nextActivtyIntent = new Intent(setDeliveredStatusActivity.this, SignatureNameActivity.class);
                    SignatureNameActivity.rtnPageName = "setDeliveredStatusActivity";
                    intent = new Intent(setDeliveredStatusActivity.this, SecuritySealActivity.class);*/
                    SecuritySealActivity.nextActivtyIntent = new Intent(myApp.currentActivity, SignatureNameActivity.class);
                    SignatureNameActivity.rtnPageName = "setDeliveredStatusActivity";
                    intent = new Intent(myApp.currentActivity, SecuritySealActivity.class);
                } else {
                    SignatureNameActivity.rtnPageName = "setDeliveredStatusActivity";
                    //intent = new Intent(setDeliveredStatusActivity.this, SignatureNameActivity.class);
                    intent = new Intent(myApp.currentActivity, SignatureNameActivity.class);
                }
                startActivity(intent);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setdeliveredstatus, menu);
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

    private void hideshownote2(Boolean show, String type, String txt) {
        Context ctx = myApp.getAppContext();
        try {
            if (show && type.toLowerCase().equals("cannottakephoto")) {
                lblRequirePhotoNote.setText(CannotTakePhoto == 3 ? ctx.getString(R.string.alert_allcannottakephoto).toUpperCase()
                        : CannotTakePhoto == 2 ? ctx.getString(R.string.alert_somecannottakephoto).toUpperCase()
                        : ctx.getString(R.string.alert_cannottakephoto).toUpperCase());
            } else if (show && type.equals("calllog")) {
                lblRequirePhotoNote.setText(ctx.getString(R.string.att_call_log_ss_or_takephoto0));
            } else if (show && type.equals("hasItemProofImgFileMoreThan7MinOld")) {
                lblRequirePhotoNote.setText(ctx.getString(R.string.reqitemproofimg0));
            } else if (show && !objGlobalMethods.IsEmptyString(txt)) {
                lblRequirePhotoNote.setText(txt);
            } else if ((show && type.isEmpty()) || !show) {
                lblRequirePhotoNote.setText("");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity hideshownote2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            lblRequirePhotoNote.setText("");
        }
    }

    private void hideshownote(Boolean show, String type) {
        Context ctx = myApp.getAppContext();
        try {
            if (show && type.equals("ud")) {
                lblRequirePhotoNote.setText(ctx.getString(R.string.lbl_reqphoto_underdoor));
            } else if (show && type.equals("lb")) {
                lblRequirePhotoNote.setText(ctx.getString(R.string.lbl_reqphoto_lb));
            } else if (show && type.equals("riser")) {
                lblRequirePhotoNote.setText(ctx.getString(R.string.lbl_reqphoto_riser));
            } else if (show && type.equals("shoes")) {
                lblRequirePhotoNote.setText(ctx.getString(R.string.lbl_reqphoto_shoe));
            } else if (show && type.equals("cheque")) {
                lblRequirePhotoNote.setText(ctx.getString(R.string.lbl_reqphoto_cheque));
            } else if (show && type.toLowerCase().equals("cannottakephoto")) {
                lblRequirePhotoNote.setText(CannotTakePhoto == 3 ? ctx.getString(R.string.alert_allcannottakephoto).toUpperCase()
                        : CannotTakePhoto == 2 ? ctx.getString(R.string.alert_somecannottakephoto).toUpperCase()
                        : ctx.getString(R.string.alert_cannottakephoto).toUpperCase());
            } else if (show && type.equals("others")) {
                lblRequirePhotoNote.setText(ctx.getString(R.string.lbl_reqphoto));
            } else if (show && type.equals("calllog")) {
                lblRequirePhotoNote.setText(ctx.getString(R.string.att_call_log_ss_or_takephoto0));
            } else if (show && type.equals("hasItemProofImgFileMoreThan7MinOld")) {
                lblRequirePhotoNote.setText(ctx.getString(R.string.reqitemproofimg0));
            } else if ((show && type.isEmpty()) || !show) {
                lblRequirePhotoNote.setText("");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity hideshownote. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            lblRequirePhotoNote.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        isFromSetOKP = false;
        accessFromDirection = 0;
        Camera5Activity.mCurrentPhotoPath = null;
        CustomGalleryActivity.dataT = null;
        CustomGalleryActivity.imagePaths = null;
        remarks = null;
        SignatureNameActivity.ReceiverName = "";
        SignatureNameActivity.StaffID = "";
        signatureString = "";
        selectedSpecificIDX = null;

        //SignatureNameActivity.requireSignature = false;
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mCurrentPhotoPath = null;
        ddlSelectedIndex = 0;
        ishuman = "0";
        String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "part2puHasCOD", "cbReviewSelected"};
        String[] prefvalue = new String[]{"", "", "", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");

        /*Intent parentIntent = new Intent(this, JobInfoActivity.class);
        if (!fromPage.toUpperCase().equals("") && (fromPage.equals("JobInfoActivity") || fromPage.equals("OtpActivity"))) {
            parentIntent = new Intent(this, JobInfoActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("CODupdate")) {
            parentIntent = new Intent(this, CODupdate.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("CheckListActivity")) {
            parentIntent = new Intent(this, CheckListActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("DOinvCheckActivity")) {
            parentIntent = new Intent(this, DOinvCheckActivity.class);
        }*/

        Intent parentIntent = new Intent(myApp.currentActivity, JobInfoActivity.class);
        /*if (!fromPage.toUpperCase().equals("") && fromPage.equals("AuthActivity")) {
            parentIntent = new Intent(myApp.currentActivity, AuthActivity.class);
        } else*/
        if (!fromPage.toUpperCase().equals("") && (fromPage.equals("JobInfoActivity") || fromPage.equals("OtpActivity"))) {
            parentIntent = new Intent(myApp.currentActivity, JobInfoActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("CODupdate")) {
            parentIntent = new Intent(myApp.currentActivity, CODupdate.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("CheckListActivity")) {
            parentIntent = new Intent(myApp.currentActivity, CheckListActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("DOinvCheckActivity")) {
            parentIntent = new Intent(myApp.currentActivity, DOinvCheckActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("ShpmtVActivity")) {
            PreDeterminedStatusCodeIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, "PreDeterminedStatusCodeIDX");
            boolean PreDeterminedStatusCodeIDXisNonOKP = !objGlobalMethods.IsEmptyString(PreDeterminedStatusCodeIDX) && PreDeterminedStatusCodeIDX.equals("129") ? true : false;
            if (PreDeterminedStatusCodeIDXisNonOKP) {
                if (ShpmtVActivity.fromPage != null && ShpmtVActivity.fromPage.equalsIgnoreCase("CheckListActivity")) {
                    parentIntent = new Intent(myApp.currentActivity, CheckListActivity.class);
                } else {
                    parentIntent = new Intent(myApp.currentActivity, ShpmtVActivity.class);
                }
            } else {
                parentIntent = new Intent(myApp.currentActivity, ShpmtVActivity.class);
            }
            //parentIntent = new Intent(myApp.currentActivity, ShpmtVActivity.class);
        }
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    public void btnViewImgClick(View view) {
        isFromSetOKP = true;
        Camera5Activity.mCurrentPhotoPath = null;
        bitmap = null;
        remarks = txtReason.getText().toString();
        //Intent i = new Intent(setDeliveredStatusActivity.this, CustomGalleryActivity.class);
        Intent i = new Intent(myApp.currentActivity, CustomGalleryActivity.class);
        startActivity(i);
    }

    public void btnCameraClick(View view) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        CannotTakePhoto = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto"));
        CheckListActivity.openCamera = null;
        CheckListActivity.itemIndex = null;
        if (CannotTakePhoto > 0) {
            showDialog(CannotTakePhoto == 3 ? myApp.getAppContext().getString(R.string.alert_allcannottakephoto).toUpperCase()
                    : CannotTakePhoto == 2 ? myApp.getAppContext().getString(R.string.alert_somecannottakephoto).toUpperCase()
                    : myApp.getAppContext().getString(R.string.alert_cannottakephoto).toUpperCase());
        } else {
            isFromSetOKP = true;
            Camera5Activity.mCurrentPhotoPath = null;
            bitmap = null;
            remarks = txtReason.getText().toString();
            //Intent i = new Intent(setDeliveredStatusActivity.this, Camera5Activity.class);
            Intent i = new Intent(myApp.currentActivity, Camera5Activity.class);
            CheckListActivity.openCamera = null;
            CheckListActivity.itemIndex = null;
            try {

                if (imagePaths != null && imagePaths.size() > 0) {
                    if (imagePaths.size() >= 6) {
                        objGlobalMethods.writeToFile("setDeliveredStatusActivity btnCameraClick blocked. 6 photos taken already. ", "", "", "", "");
                        showDialog(myApp.getAppContext().getString(R.string.cannotlaunchcam));
                    } else {
                        objGlobalMethods.writeToFile("setDeliveredStatusActivity btnCameraClick. ", "", "", "", "");
                        startActivity(i);
                    }
                } else if (imagePaths != null && imagePaths.size() == 0) {
                    objGlobalMethods.writeToFile("setDeliveredStatusActivity btnCameraClick. ", "", "", "", "");
                    startActivity(i);
                } else if (imagePaths == null) {
                    objGlobalMethods.writeToFile("setDeliveredStatusActivity btnCameraClick. ", "", "", "", "");
                    startActivity(i);
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity btnCameraClick. " + e.getMessage().toString(), "", "", "", "");
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
                    setDeliveredMassStatusActivity.isFromMassSetOKP = false;
                    setDeliveredStatusActivity.isFromSetOKP = true;
                    remarks = txtReason.getText().toString();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isCallLog", "t");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isItemProof", "");
                    //Intent i = new Intent(setDeliveredStatusActivity.this, Camera5Activity.class);
                    Intent i = new Intent(myApp.currentActivity, Camera5Activity.class);
                    startActivity(i);
                }
            });

            builder.setNegativeButton(ctx.getString(R.string.attachcalllog), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    remarks = txtReason.getText().toString();
                    isFromSetOKP = true;
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isCallLog", "t");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isItemProof", "");
                    //Intent i = new Intent(setDeliveredStatusActivity.this, CustomGalleryActivity.class);
                    Intent i = new Intent(myApp.currentActivity, CustomGalleryActivity.class);
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
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity btnCallLogImgClick. " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnShipmentphotoClick(View view) {
        try {
            Context ctx = myApp.getAppContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(ctx.getString(R.string.takeitemproofimg));

            builder.setPositiveButton(ctx.getString(R.string.takephoto), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    setDeliveredMassStatusActivity.isFromMassSetOKP = false;
                    setDeliveredStatusActivity.isFromSetOKP = true;
                    remarks = txtReason.getText().toString();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isItemProof", "t");
                    //Intent i = new Intent(setDeliveredStatusActivity.this, Camera5Activity.class);
                    Intent i = new Intent(myApp.currentActivity, Camera5Activity.class);
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
            objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity btnShipmentphotoClick. " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public void CancelsetDeliveredStatus(View view) {
        onBackPressed();
    }

    public void btnSign_Click(View view) {
        try {
            remarks = txtReason.getText().toString();
            Intent intent = null;
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            Boolean requiredSignaturePrompt = JobsHandler.showSignatureRequiredPrompt(GcmPreferences);
            Boolean showSign = false;
            Boolean showName = false;
            Boolean showSeal = false;
            Boolean reqSeal = JobsHandler.hasSecuritySealListSaved(GcmPreferences);
            Boolean SealAllVerified = false;
            Boolean isStandardRunJob = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isStandardRunJob")) ? true : false;
            if (reqSeal) {
                SealAllVerified = JobsHandler.SecuritySealAllVerified(GcmPreferences);
            }
            showSeal = reqSeal && !SealAllVerified;

            if (showSeal) {
                SecuritySealActivity.nextActivtyIntent = new Intent(myApp.currentActivity, SignatureNameActivity.class);
                SignatureNameActivity.rtnPageName = "setDeliveredStatusActivity";
                intent = new Intent(myApp.currentActivity, SecuritySealActivity.class);
            } else {
                showSign = requiredSignaturePrompt && (SignatureNameActivity.requireSignature && signatureString.equals("")) && (ishuman.equals("1"));
                showName = !isStandardRunJob && ishuman.equals("1") && !selectedRecipient.toLowerCase().equals("recipient") && objGlobalMethods.IsEmptyString(SignatureNameActivity.ReceiverName);
                String[] prefName = new String[]{"reqSign", "reqName"};
                String[] prefvalue = new String[]{showSign ? "y" : "", showName ? "y" : ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                SignatureNameActivity.rtnPageName = "setDeliveredStatusActivity";
                //intent = new Intent(setDeliveredStatusActivity.this, SignatureNameActivity.class);
                intent = new Intent(myApp.currentActivity, SignatureNameActivity.class);
            }
            startActivity(intent);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity btnSign_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private ArrayList<String> RTNconvertedBitmapToBase64List() {
        ArrayList<String> convertedBitmapToBase64List = null;
        try {
            if (imagePaths != null && imagePaths.size() > 0 && dataT != null && dataT.size() > 0) {
                for (int i = 0; i <= imagePaths.size() - 1; i++) {
                    String imgmCurrentPhotoPath = imagePaths.get(i);
                    convertedBitmapToBase64 = RtnConvertedBitmapToBase64str(imgmCurrentPhotoPath);
                    if (!convertedBitmapToBase64.equals("")) {
                        if (convertedBitmapToBase64List == null) {
                            convertedBitmapToBase64List = new ArrayList<>();
                            convertedBitmapToBase64List.add(convertedBitmapToBase64);
                        } else {
                            convertedBitmapToBase64List.add(convertedBitmapToBase64);
                        }
                    }
                }
            }
            if (SimagePaths != null && SimagePaths.size() > 0) {
                for (int i = 0; i <= SimagePaths.size() - 1; i++) {
                    String imgmCurrentPhotoPath = SimagePaths.get(i);
                    convertedBitmapToBase64 = RtnConvertedBitmapToBase64str(imgmCurrentPhotoPath);
                    if (!convertedBitmapToBase64.equals("")) {
                        if (convertedBitmapToBase64List == null) {
                            convertedBitmapToBase64List = new ArrayList<>();
                            convertedBitmapToBase64List.add(convertedBitmapToBase64);
                        } else {
                            convertedBitmapToBase64List.add(convertedBitmapToBase64);
                        }
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity RTNconvertedBitmapToBase64List. Exception: " + e.toString(), "", "", "", "");
            convertedBitmapToBase64List = null;
        } finally {
            return convertedBitmapToBase64List;
        }
    }

    private String RtnConvertedBitmapToBase64str(String imgmCurrentPhotoPath) {
        String ConvertedBitmapToBase64str = null;
        try {
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
                ConvertedBitmapToBase64str = convertBitmapToBase64(bitmap);
                bitmap.recycle();
                bitmap = null;
            } else {
                ConvertedBitmapToBase64str = "";
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity RtnConvertedBitmapToBase64str. Exception: " + e.toString() + "\nStack: " + s, "", "", "", "");
            ConvertedBitmapToBase64str = "";
        } finally {
            return ConvertedBitmapToBase64str;
        }
    }

    //when have COD collection
    public void btnNextClick(View view) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        Boolean showSign = false;
        Boolean showName = false;
        Boolean showSeal = false;
        Boolean showStaff = false;
        Boolean reqSelectSubCat = LLspecific01.getVisibility() == View.VISIBLE && LLspecific02.getVisibility() == View.VISIBLE;
        Boolean SubCatSelected = reqSelectSubCat && selectedSpecificIDX != null && selectedSpecificIDX > 0 && !objGlobalMethods.IsEmptyString(selectedSpecific);
        Boolean requiredSignaturePrompt = JobsHandler.showSignatureRequiredPrompt(GcmPreferences);
        Boolean reqSeal = JobsHandler.hasSecuritySealListSaved(GcmPreferences);
        Boolean SealAllVerified = false;
        Boolean isStandardRunJob = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isStandardRunJob")) ? true : false;
        if (reqSeal) {
            SealAllVerified = JobsHandler.SecuritySealAllVerified(GcmPreferences);
        }
        Boolean reqStaffID = JobsHandler.reqStaffID(GcmPreferences);
        Boolean validStaffID = reqStaffID && (SignatureNameActivity.StaffID != null && !SignatureNameActivity.StaffID.equals(""));

        showSign = requiredSignaturePrompt && (SignatureNameActivity.requireSignature && signatureString.equals("")) && (ishuman.equals("1"));
        showName = !isStandardRunJob && ishuman.equals("1") && !selectedRecipient.toLowerCase().equals("recipient") && objGlobalMethods.IsEmptyString(SignatureNameActivity.ReceiverName);
        showSeal = reqSeal && !SealAllVerified;
        showStaff = reqStaffID && !validStaffID;
        //code when doing multiple seal updates

        String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
        //String DOINV_Collection_Prompt_Msg = JobsHandler.returnListOfDOINV(JobsIDXstr);

        ArrayList<String> DOINV_chk = JobsHandler.returnListOfDOINVNew(GcmPreferences);
        String DOINV_Collection_Prompt_Msg = DOINV_chk != null && DOINV_chk.size() == 2 && !objGlobalMethods.IsEmptyString(DOINV_chk.get(0)) ? DOINV_chk.get(0) : "";
        String DO_pcs = DOINV_chk != null && DOINV_chk.size() == 2 && !objGlobalMethods.IsEmptyString(DOINV_chk.get(1)) ? DOINV_chk.get(1) : "";

        /*String reqItemProofImg = objGlobalMethods.getGCMPrefs(GcmPreferences, "reqItemProofImg");
        boolean hasItemProofImgFileMoreThan7MinOld = false;
        if (!objGlobalMethods.IsEmptyString(reqItemProofImg)) {
            hasItemProofImgFileMoreThan7MinOld = JobsHandler.hasImgFileMoreThan7MinOld(imagePaths, dataT, null);
            if (hasItemProofImgFileMoreThan7MinOld) {
                clearAllAttachedItemProofImg();
            }
        }*/

        PreDeterminedStatusCodeIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, "PreDeterminedStatusCodeIDX");

        if (showSign || showName || showSeal || showStaff) {
            showSignatureAlert3(showSign, showName, showSeal, showStaff, PreDeterminedStatusCodeIDX);
        } else if (reqSelectSubCat && !SubCatSelected) {
            showDialog(myApp.getAppContext().getString(R.string.promptreclation));
        } else {
            if (!DOINV_Collection_Prompt_Msg.isEmpty() && !DOINV_Collection_Prompt_Msg.equals("") && DOINV_Collection_Prompt_Msg != "") {
                //promptForDOINVmsg(DOINV_Collection_Prompt_Msg);
                promptDO_Collection(DOINV_Collection_Prompt_Msg, DO_pcs, 1);
            } else {
                nextprompt();
            }
        }
        //code when doing multiple seal updates
    }

    private void ignoreOldselected() {
        try {
            if (ddlReceiver != null) {
                Class<?> c = ddlReceiver.getClass().getSuperclass().getSuperclass().getSuperclass();
                Field rf = c.getDeclaredField("mOldSelectedPosition");
                rf.setAccessible(true);
                rf.setInt(ddlReceiver, -1);
            }
        } catch (Exception e) {
            String s = e.getMessage();
        }
    }

    private void promptForDOINVmsg(String msg) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "OK", then he is allowed to exit from application
                nextprompt();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void nextprompt() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        Date dt = new Date();
        Long l = dt.getTime();
        String Receiver = selectedRecipient;

        if (!SignatureNameActivity.ReceiverName.isEmpty()) {
            Receiver = Receiver + ": " + SignatureNameActivity.ReceiverName;
        }
        Receiver += !objGlobalMethods.IsEmptyString(selectedSpecific) ? selectedSpecific : "";
        Gson gson = new Gson();
        String objUpdateCODPostDataListJsonStr = "";
        List<objUpdateCOD_PostData> objUpdateCODPostDataList = null;
        objJobInfo linkedjob = null;
        String part1cnno = "";
        List<objJobInfo> objJobInfoBringtoUpdateCODList = null;
        String objJobInfoBringtoUpdateCODListJsonStr = "";
        String mutlipleCODjobs = "";
        //ArrayList<String> convertedBitmapToBase64List = null;
        ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = null;
        ArrayList<objConvertedBitmapToBase64> CheckListconvertedBitmapToBase64List = null;
        ArrayList<String> UploadJobSignatureIABitmapToBase64List = null;
        String SignatureStr = "";
        String reason = "";
        String pcs = "";
        String RegID = objGlobalMethods.getRegistrationIdOnly();
        try {
            objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);

            if (currJobInfo.ConsignmentNote != null && !currJobInfo.ConsignmentNote.equals("")) {
                part1cnno = currJobInfo.ConsignmentNote;
            } else {
                part1cnno = currJobInfo.JobNo;
            }

            reason = txtReason.getText().toString();
            /*if (currJobInfo.getTypeOfPinVerification().equals(1) || currJobInfo.getTypeOfPinVerification().equals(2) || currJobInfo.getTypeOfPinVerification().equals(3)) {
                reason += "\n\n" + currJobInfo.getManualPinVerificaionText();
                Receiver = "PIN Verification (" + Receiver + ")";
            }*/

            pcs = objGlobalMethods.getGCMPrefs(GcmPreferences, "pcs");
            reason += !objGlobalMethods.IsEmptyString(pcs) ? "\n\nPcs handed over: " + pcs : "";

            reason += currJobInfo.getTypeOfPinVerification().equals(1) || currJobInfo.getTypeOfPinVerification().equals(2) || currJobInfo.getTypeOfPinVerification().equals(3) ? "\n\n" + currJobInfo.getManualPinVerificaionText() : "";
            if (currJobInfo.getTypeOfPinVerification().equals(1)) {
                Receiver = "PIN Verification (" + Receiver + ")";
            }

            if (SignatureNameActivity.StaffID != null && !SignatureNameActivity.StaffID.equals("")) {
                Receiver += "\nStaff ID: " + SignatureNameActivity.StaffID;
            }

            //String checklistvalue = objGlobalMethods.getGCMPrefs(GcmPreferences, "checklistvalue");
            String checklistvalue = objGlobalMethods.getChecklistRelatedSP_KeyValue("checklistvalue");

            if (checklistvalue.length() > 0) {
                reason += "\n\n" + checklistvalue;
            }
            String pr_latemsg0 = objGlobalMethods.getGCMPrefs(GcmPreferences, "pr_latemsg0");
            if (!objGlobalMethods.IsEmptyString(pr_latemsg0)) {
                reason += "\n" + pr_latemsg0;
            }

            String securityseallistvalue = "";
            securityseallistvalue = objGlobalMethods.getGCMPrefs(GcmPreferences, "securityseallistvalue");
            if (securityseallistvalue.length() > 0) {
                reason += "\n\n" + securityseallistvalue;
            }

            CannotTakePhoto = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto"));
            if (CannotTakePhoto > 0 && reason.length() > 0) {
                reason += "\n\nRestricted area. Photo taking not allowed.";
            } else if (CannotTakePhoto > 0 && reason.length() == 0) {
                reason += " Restricted area. Photo taking not allowed.";
            }

            currJobInfo.setReason(reason);

            objUpdateCODPostDataListJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objUpdateCODPostDataListJsonStr");
            if (objUpdateCODPostDataListJsonStr.equals("") || objUpdateCODPostDataListJsonStr.equals("[]")) {
                objUpdateCODPostDataList = null;
            } else {
                objUpdateCODPostDataList = gson.fromJson(objUpdateCODPostDataListJsonStr, new TypeToken<List<objUpdateCOD_PostData>>() {
                }.getType());
            }

            objJobInfoBringtoUpdateCODListJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr");
            if (objJobInfoBringtoUpdateCODListJsonStr.equals("") || objJobInfoBringtoUpdateCODListJsonStr.equals("[]")) {
                objJobInfoBringtoUpdateCODList = null;
            } else {
                objJobInfoBringtoUpdateCODList = gson.fromJson(objJobInfoBringtoUpdateCODListJsonStr, new TypeToken<List<objJobInfo>>() {
                }.getType());
            }

            if (JobhasCOD && P2JobhasCOD) {
                //p1 & 2 have COD
                //for xdel cash n cust cash
                objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, currJobInfo, 2, true,
                        RegID, false, part1cnno, Receiver);
            } else if (!JobhasCOD && P2JobhasCOD) {
                //p1 no COD, p2 has
            } else if (JobhasCOD && !P2JobhasCOD) {
                //p1 has COD, p2 no COD
                //for xdel cash n cust cash
                objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, currJobInfo, 2, true,
                        RegID, false, part1cnno, Receiver);
            }

            currJobInfo.setpendingStatusUpdate(135);
            currJobInfo.setReceiver(Receiver);
            currJobInfo.setFailureReason("");
            //convertedBitmapToBase64List = JobsHandler.RTNconvertedBitmapToBase64List();
            convertedBitmapToBase64List = RTNobjConvertedBitmapToBase64List();
            CheckListconvertedBitmapToBase64List = RTNCheckListConvertedBitmapToBase64List();
            UploadJobSignatureIABitmapToBase64List = JobsHandler.RTNconvertedUploadJobSignatureIABitmapToBase64List();
            if (SimagePaths != null && SimagePaths.size() > 0) {
                for (int i = 0; i <= SimagePaths.size() - 1; i++) {
                    objGlobalMethods.deleteFile(SimagePaths.get(i));
                }
            }
            //currJobInfo.setconvertedBitmapToBase64(convertedBitmapToBase64List);
            currJobInfo.setObjConvertedBitmapToBase64(convertedBitmapToBase64List);
            currJobInfo.setObjCheckListConvertedBitmapToBase64List(CheckListconvertedBitmapToBase64List);
            currJobInfo.setUploadJobSignatureIABitmapToBase64List(UploadJobSignatureIABitmapToBase64List);
            Camera5Activity.mCurrentPhotoPath = null;
            imagePaths = null;
            SimagePaths = null;
            dataT = null;
            currJobInfo.setSignatureStr(signatureString);
            mCurrentPhotoPath = "";
            mBitmap = null;

            objJobInfoBringtoUpdateCODList = JobsHandler.RTNobjJobInfoBringtoUpdateCODList(objJobInfoBringtoUpdateCODList, currJobInfo);

            if (currJobInfo.LinkedJobs != null && currJobInfo.LinkedJobs.length > 0 && currJobInfo.LinkedJobs[0] != null) {
                linkedjob = currJobInfo.LinkedJobs[0];
            }

            if (linkedjob != null) {
                // has p2, prompt update p2
                promptToUpdateP2(currJobInfo, linkedjob, objJobInfoBringtoUpdateCODList, objUpdateCODPostDataList, JobhasCOD, P2JobhasCOD, part1cnno);
            } else {
                //no p2, no prompt, str8 to UpdateCOD_List
                if (objJobInfoBringtoUpdateCODList != null && objJobInfoBringtoUpdateCODList.size() > 0) {
                    objJobInfo[] objJobInfoBringtoUpdateCODAL = objJobInfoBringtoUpdateCODList.toArray(new objJobInfo[objJobInfoBringtoUpdateCODList.size()]);
                    objJobInfoBringtoUpdateCODListJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL);
                } else {
                    objJobInfoBringtoUpdateCODListJsonStr = "";
                }

                //this is used to update job status when at updateCODlist or CODupdate page after setting all COD collection updates
                objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", objJobInfoBringtoUpdateCODListJsonStr);

                if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                    objUpdateCOD_PostData[] objUpdateCODPostDataAL = objUpdateCODPostDataList.toArray(new objUpdateCOD_PostData[objUpdateCODPostDataList.size()]);
                    mutlipleCODjobs = gson.toJson(objUpdateCODPostDataAL);
                } else {
                    mutlipleCODjobs = "";
                }

                //this is used to display @ updateCODlist for XDel cash or cm cash
                objGlobalMethods.setGCMPrefs(GcmPreferences, "mutlipleCODjobs", mutlipleCODjobs);

                //ojisPage == 0 == JobInfoActivity | ojisPage == 1 == JobsListGroupActivity | ojisPage == 2 == setDeliveredStatusActivity |
                // ojisPage == 3 == setDeliveredMassStatusActivity | ojisPage == 4 == MassUpdateOKPwP2
                objGlobalMethods.setGCMPrefs(GcmPreferences, "ojisPage", "2");

                remarks = "";

                Intent intent = null;
                //intent = new Intent(this, UpdateCOD_List.class);
                intent = new Intent(myApp.currentActivity, UpdateCOD_List.class);

                startActivity(intent);
                this.finish();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity nextprompt. " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void promptToUpdateP2(final objJobInfo currJobInfo, final objJobInfo alinkedJobInfo, final List<objJobInfo> aobjJobInfoBringtoUpdateCODList,
                                  final List<objUpdateCOD_PostData> aobjUpdateCODPostDataList, final Boolean p1HasCOD, final Boolean p2hasCOD, final String apart1cnno) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_pup2));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_PU), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //if user pressed "yes", update job status
                    Gson gson = new Gson();
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    String RegID = objGlobalMethods.getRegistrationIdOnly();
                    String part1cnno = apart1cnno;
                    objJobInfo linkedJobInfo = alinkedJobInfo;
                    List<objJobInfo> objJobInfoBringtoUpdateCODList = aobjJobInfoBringtoUpdateCODList;
                    List<objUpdateCOD_PostData> objUpdateCODPostDataList = aobjUpdateCODPostDataList;
                    String objJobInfoBringtoUpdateCODListJsonStr = "";
                    String mutlipleCODjobs = "";

                    objGlobalMethods.writeToFile("Click to update Part 2 job status.",
                            "JobsIDX: " + String.valueOf(linkedJobInfo.JobsIDX), "Submitted StatusCodeIDX: 49", "", "");

                    if (p2hasCOD) {
                        objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, linkedJobInfo, 0, true,
                                RegID, true, part1cnno, "");
                    }

                    linkedJobInfo.setpendingStatusUpdate(49);
                    linkedJobInfo.setReceiver("");
                    linkedJobInfo.setFailureReason("");
                    objJobInfoBringtoUpdateCODList = JobsHandler.RTNobjJobInfoBringtoUpdateCODList(objJobInfoBringtoUpdateCODList, linkedJobInfo);

                    if (objJobInfoBringtoUpdateCODList != null && objJobInfoBringtoUpdateCODList.size() > 0) {
                        objJobInfo[] objJobInfoBringtoUpdateCODAL = objJobInfoBringtoUpdateCODList.toArray(new objJobInfo[objJobInfoBringtoUpdateCODList.size()]);
                        objJobInfoBringtoUpdateCODListJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL);
                    } else {
                        objJobInfoBringtoUpdateCODListJsonStr = "";
                    }
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", objJobInfoBringtoUpdateCODListJsonStr);

                    if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                        objUpdateCOD_PostData[] objUpdateCODPostDataAL = objUpdateCODPostDataList.toArray(new objUpdateCOD_PostData[objUpdateCODPostDataList.size()]);
                        mutlipleCODjobs = gson.toJson(objUpdateCODPostDataAL);
                    } else {
                        mutlipleCODjobs = "";
                    }
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "mutlipleCODjobs", mutlipleCODjobs);

                    //ojisPage == 0 == JobInfoActivity | ojisPage == 1 == JobsListGroupActivity | ojisPage == 2 == setDeliveredStatusActivity | ojisPage == 3 == setDeliveredMassStatusActivity |
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "ojisPage", "2");

                    remarks = "";

                    Intent intent = null;
                    //intent = new Intent(setDeliveredStatusActivity.this, UpdateCOD_List.class);
                    intent = new Intent(myApp.currentActivity, UpdateCOD_List.class);

                    startActivity(intent);
                    finish();

                }
            });

            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_FP), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objJobInfo linkedJobInfo = alinkedJobInfo;
                    Gson gson = new Gson();
                    String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, "failPart2FromPart1Bool"};
                    String[] prefvalue = new String[]{"0", "t"};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");
                    //failPart2FromPart1Bool = true;
                    objGlobalMethods.writeToFile("Click to Fail Part 2 job status.",
                            "JobsIDX: " + String.valueOf(linkedJobInfo.JobsIDX), "", "", "");
                    //updatemodeForFailure: -1 == normal, will not proceed | 0 == part1 has cod, fail p2 pu, will proceed
                    //updatemodeForFailure: 1 == part1 no cod, fail p2 pu, will not proceed | 2 == from masUpdateOKPwP2 fail p2, must return to massUpdateOKPwP2
                    String updatemodeForFailure = "-1";
                    if (p1HasCOD) {
                        //after failure, will proceed to UpdateCOD_list
                        updatemodeForFailure = "0";

                        String mutlipleCODjobs = "";
                        String objJobInfoBringtoUpdateCODListJsonStr = "";
                        List<objUpdateCOD_PostData> objUpdateCODPostDataList = aobjUpdateCODPostDataList;
                        objUpdateCOD_PostData[] objUpdateCODPostDataAL = null;
                        if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                            objUpdateCODPostDataAL = objUpdateCODPostDataList.toArray(new objUpdateCOD_PostData[objUpdateCODPostDataList.size()]);
                        } else {
                            objUpdateCODPostDataAL = null;
                        }

                        objJobInfo[] objJobInfoBringtoUpdateCODAL = null;
                        List<objJobInfo> objJobInfoBringtoUpdateCODList = aobjJobInfoBringtoUpdateCODList;
                        if (objJobInfoBringtoUpdateCODList != null && objJobInfoBringtoUpdateCODList.size() > 0) {
                            objJobInfoBringtoUpdateCODAL = objJobInfoBringtoUpdateCODList.toArray(new objJobInfo[objJobInfoBringtoUpdateCODList.size()]);
                        } else {
                            objJobInfoBringtoUpdateCODAL = null;
                        }


                        mutlipleCODjobs = gson.toJson(objUpdateCODPostDataAL);
                        if (mutlipleCODjobs.equals("") || mutlipleCODjobs.equals("[]")) {
                            mutlipleCODjobs = "";
                        }
                        objJobInfoBringtoUpdateCODListJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL);
                        if (objJobInfoBringtoUpdateCODListJsonStr.equals("") || objJobInfoBringtoUpdateCODListJsonStr.equals("[]")) {
                            objJobInfoBringtoUpdateCODListJsonStr = "";
                        }

                        objGlobalMethods.setGCMPrefs(GcmPreferences, "mutlipleCODjobs", mutlipleCODjobs);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", objJobInfoBringtoUpdateCODListJsonStr);


                    } else if (!p1HasCOD) {
                        //after failure, will not proceed to UpdateCOD_list
                        updatemodeForFailure = "1";

                        objGlobalMethods.setGCMPrefs(GcmPreferences, "mutlipleCODjobs", "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", "");
                    }

                    objGlobalMethods.setGCMPrefs(GcmPreferences, "updatemodeForFailure", updatemodeForFailure);
                    remarks = "";
                    //Intent parentIntent = new Intent(setDeliveredStatusActivity.this, setFailStatusActivity.class);
                    Intent parentIntent = new Intent(myApp.currentActivity, setFailStatusActivity.class);
                    MainActivity.uponLogin = 0;
                    startActivity(parentIntent);

                }
            });

            final AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity promptToUpdateP2. Exception: " + e.toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnSubmitClick(View view) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            Boolean isResidential = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isResidential")) ? true : false;
            Boolean showSign = false;
            Boolean showName = false;
            Boolean showSeal = false;
            Boolean showStaff = false;
            Boolean reqSelectSubCat = LLspecific01.getVisibility() == View.VISIBLE && LLspecific02.getVisibility() == View.VISIBLE;
            Boolean SubCatSelected = reqSelectSubCat && selectedSpecificIDX != null && selectedSpecificIDX > 0 && !objGlobalMethods.IsEmptyString(selectedSpecific);
            Boolean requiredSignaturePrompt = JobsHandler.showSignatureRequiredPrompt(GcmPreferences);
            Boolean reqSeal = JobsHandler.hasSecuritySealListSaved(GcmPreferences);
            Boolean SealAllVerified = false;
            Boolean isStandardRunJob = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isStandardRunJob")) ? true : false;
            if (reqSeal) {
                SealAllVerified = JobsHandler.SecuritySealAllVerified(GcmPreferences);
            }
            Boolean reqStaffID = JobsHandler.reqStaffID(GcmPreferences);
            Boolean validStaffID = reqStaffID && (SignatureNameActivity.StaffID != null && !SignatureNameActivity.StaffID.equals(""));

            showSign = (requiredSignaturePrompt && (SignatureNameActivity.requireSignature && signatureString.equals("")) && (ishuman.equals("1"))) ||
                    (isResidential && signatureString.equals("") && ishuman.equals("1"));

            boolean btnSignEnabled = btnSign.isEnabled();
            if (!btnSignEnabled) {
                showSign = false;
            }

            //showName = !isStandardRunJob && ishuman.equals("1") && !selectedRecipient.toLowerCase().equals("recipient") && objGlobalMethods.IsEmptyString(SignatureNameActivity.ReceiverName);
            ////mandating all jobs including standard run jobs to require name entry due to 1x citibank standard run from adeline email dated 25april2024, per x instruction to apply to all
            showName = ishuman.equals("1") && !selectedRecipient.toLowerCase().equals("recipient") && objGlobalMethods.IsEmptyString(SignatureNameActivity.ReceiverName);
            showSeal = reqSeal && !SealAllVerified;
            showStaff = reqStaffID && !validStaffID;
            //code when doing multiple seal updates
            String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
            //String DOINV_Collection_Prompt_Msg = JobsHandler.returnListOfDOINV(JobsIDXstr);

            ArrayList<String> DOINV_chk = JobsHandler.returnListOfDOINVNew(GcmPreferences);
            String DOINV_Collection_Prompt_Msg = DOINV_chk != null && DOINV_chk.size() == 2 && !objGlobalMethods.IsEmptyString(DOINV_chk.get(0)) ? DOINV_chk.get(0) : "";
            String DO_pcs = DOINV_chk != null && DOINV_chk.size() == 2 && !objGlobalMethods.IsEmptyString(DOINV_chk.get(1)) ? DOINV_chk.get(1) : "";

        /*String reqItemProofImg = objGlobalMethods.getGCMPrefs(GcmPreferences, "reqItemProofImg");
        boolean hasItemProofImgFileMoreThan7MinOld = false;
        if (!objGlobalMethods.IsEmptyString(reqItemProofImg)) {
            hasItemProofImgFileMoreThan7MinOld = JobsHandler.hasImgFileMoreThan7MinOld(imagePaths, dataT, null);
            if (hasItemProofImgFileMoreThan7MinOld) {
                clearAllAttachedItemProofImg();
            }
        }*/

            PreDeterminedStatusCodeIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, "PreDeterminedStatusCodeIDX");

            if (showSign || showName || showSeal || showStaff) {
                //objGlobalMethods.writeToFile("Require signature for jobsidx: " + objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX), "", "", "", "");
                boolean rempty = false;
                String r = showSign ? "Signature" : "";
                rempty = objGlobalMethods.IsEmptyString(r);
                r += rempty && showName ? "Name" : !rempty && showName ? ", Name" : "";
                rempty = objGlobalMethods.IsEmptyString(r);
                r += rempty && showSeal ? "Seal" : !rempty && showSeal ? ", Seal" : "";
                rempty = objGlobalMethods.IsEmptyString(r);
                r += rempty && showStaff ? "StaffName" : !rempty && showStaff ? ", StaffName" : "";


                objGlobalMethods.writeToFile("Require " + r + " for jobsidx: " + objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX), "", "", "", "");
                showSignatureAlert3(showSign, showName, showSeal, showStaff, PreDeterminedStatusCodeIDX);
            } else if (reqSelectSubCat && !SubCatSelected) {
                showDialog(myApp.getAppContext().getString(R.string.promptreclation));
            } else {
                submitOKPpromptNew(DOINV_Collection_Prompt_Msg, DO_pcs, PreDeterminedStatusCodeIDX);
            }
            //code when doing multiple seal updates

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity btnSubmitClick. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private Boolean showSignatureRequiredPrompt() {
        Boolean asd = false;
        objJobInfo ji = null;
        Gson gson = new Gson();
        try {
            ji = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            if (ji.FLAG != null && ji.FLAG > 0) {
                if (((ji.FLAG & 512) == 512) && ((ji.FLAG & 1024) == 1024)) {
                    //flag is set as required otp && signature
                    asd = true;
                } else if (((ji.FLAG & 512) == 512) && ((ji.FLAG & 1024) != 1024)) {
                    //flag is set as required otp only
                    asd = !ji.getTypeOfPinVerification().equals(1) ? true : false;
                } else if ((ji.FLAG & 1024) == 1024) {
                    //flag is set as required singature
                    asd = true;
                }
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity showSignatureRequiredPrompt. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            asd = true;
        } finally {
            SignatureNameActivity.requireSignature = asd;
            return asd;
        }
    }

    private void submitOKPprompt(String DOINV_Collection_Prompt_Msg, String PreDeterminedStatusCodeIDX) {
        try {
            isFromSetOKP = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            String msg = objGlobalMethods.IsEmptyString(PreDeterminedStatusCodeIDX) ?
                    myApp.getAppContext().getString(R.string.alert_okp) :
                    myApp.getAppContext().getString(R.string.alert_reject_delivery);
            if (objGlobalMethods.IsEmptyString(PreDeterminedStatusCodeIDX)) {
                msg += !DOINV_Collection_Prompt_Msg.isEmpty() && !DOINV_Collection_Prompt_Msg.equals("") && DOINV_Collection_Prompt_Msg != "" ? "\n\n" + DOINV_Collection_Prompt_Msg : "";
            }
            builder.setMessage(msg);
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                    String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                    long JobsIDX = Long.parseLong(JobsIDXstr);
                    Date dt = new Date();
                    Long l = dt.getTime();
                    String timestampstr = "/Date(" + l + ")/";
                    String Receiver = selectedRecipient;

                    if (!SignatureNameActivity.ReceiverName.isEmpty()) {
                        Receiver = Receiver + ": " + SignatureNameActivity.ReceiverName;
                    }

                    Receiver += !objGlobalMethods.IsEmptyString(selectedSpecific) ? selectedSpecific : "";

                    if (Receiver != null) {
                        try {
                            objGlobalMethods.writeToFile("Click to OKP job status.",
                                    "JobsIDX: " + String.valueOf(JobsIDX), "Submitted Recipient: " + Receiver, "", "");

                            String RegID = objGlobalMethods.getRegistrationIdOnly();
                            submitOKPSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, JobsIDX, timestampstr, Receiver);

                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity submitOKPprompt. " + e.getMessage().toString(), "", "", "", "");
                        }
                    } else {
                        showErrorAlert(myApp.getAppContext().getString(R.string.alert_noRecipienttype));
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
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity submitOKPprompt. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private void submitOKPpromptNew(String DOINV_Collection_Prompt_Msg, String DO_pcs, String PreDeterminedStatusCodeIDX) {
        try {
            isFromSetOKP = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            String msg = objGlobalMethods.IsEmptyString(PreDeterminedStatusCodeIDX) ?
                    myApp.getAppContext().getString(R.string.alert_okp) :
                    myApp.getAppContext().getString(R.string.alert_reject_delivery);
            /*if (objGlobalMethods.IsEmptyString(PreDeterminedStatusCodeIDX)) {
                msg += !DOINV_Collection_Prompt_Msg.isEmpty() && !DOINV_Collection_Prompt_Msg.equals("") && DOINV_Collection_Prompt_Msg != "" ? "\n\n" + DOINV_Collection_Prompt_Msg : "";
            }*/
            builder.setMessage(msg);
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    dialog.dismiss();
                    if (!objGlobalMethods.IsEmptyString(DOINV_Collection_Prompt_Msg)) {
                        promptDO_Collection(DOINV_Collection_Prompt_Msg, DO_pcs, 0);
                    } else {
                        pre_submitOKPSQLite();
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
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity submitOKPpromptNew. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private AlertDialog promptDO_CollectionDialog = null;

    private void promptDO_Collection(final String doinv, final String DO_pcs, int mode) {
        //mode 0 = button submit click, 1 == button next click for cod collection
        try {
            final Context ctx = myApp.currentActivity;
            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            final View callView = getLayoutInflater().inflate(R.layout.custom_do_collect_dialog, null);
            builder.setView(callView);
            Button btn_cx = (Button) callView.findViewById(R.id.btn_cx);
            Button btn_ok = (Button) callView.findViewById(R.id.btn_ok);
            btn_ok.setVisibility(View.GONE);
            CheckBox cbAck = (CheckBox) callView.findViewById(R.id.cbAck);
            cbAck.setText(
                    ctx.getString(R.string.cfm_do_collection_01) + " " + DO_pcs + " " + ctx.getString(R.string.cfm_do_collection_02)
            );
            final TextView txtInfo = (TextView) callView.findViewById(R.id.txtInfo);
            txtInfo.setText(doinv);

            cbAck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    btn_ok.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                }
            });

            btn_cx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        promptDO_CollectionDialog.dismiss();
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity btnVerifyShipmentCancel.setOnClickListener. Exception: " + e.getMessage(), "", "", "", "");
                    }
                }
            });

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (cbAck.isChecked()) {
                            promptDO_CollectionDialog.dismiss();
                            if (mode == 0)
                                pre_submitOKPSQLite();
                            else
                                nextprompt();
                        }
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at promptDO_Collection btn_ok.setOnClickListener. Exception: " + e.getMessage(), "", "", "", "");
                    }
                }
            });

            builder.setCancelable(false);
            promptDO_CollectionDialog = builder.create();
            promptDO_CollectionDialog.show();

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity VerifyShipmentPromptNew. Exception: " + e.getMessage(), "", "", "", "");
        }
    }


    public void pre_submitOKPSQLite() {
        try {
            //if user pressed "yes", update job status
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
            long JobsIDX = Long.parseLong(JobsIDXstr);
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            String Receiver = selectedRecipient;

            if (!SignatureNameActivity.ReceiverName.isEmpty()) {
                Receiver = Receiver + ": " + SignatureNameActivity.ReceiverName;
            }

            Receiver += !objGlobalMethods.IsEmptyString(selectedSpecific) ? selectedSpecific : "";

            if (Receiver != null) {
                try {
                    objGlobalMethods.writeToFile("Click to OKP job status.",
                            "JobsIDX: " + String.valueOf(JobsIDX), "Submitted Recipient: " + Receiver, "", "");

                    String RegID = objGlobalMethods.getRegistrationIdOnly();
                    submitOKPSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, JobsIDX, timestampstr, Receiver);

                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity submitOKPprompt. " + e.getMessage().toString(), "", "", "", "");
                }
            } else {
                showErrorAlert(myApp.getAppContext().getString(R.string.alert_noRecipienttype));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("","","","","");
        }
    }

    public void submitOKPSQLite(SharedPreferences gcmp, SharedPreferences updatep, final String aregid, long idx, String atimestampstr, String Receiver) {
        ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = null;
        ArrayList<objConvertedBitmapToBase64> CheckListconvertedBitmapToBase64List = null;
        ArrayList<String> UploadJobSignatureIABitmapToBase64List = null;
        String tempDOCODJsonStr = "";
        String SignatureStr = "";
        String reason = txtReason.getText().toString();
        String statuscode = "";
        String pcs = "";
        try {
            Gson gson = new Gson();
            objJobInfo currJobInfo = gson.fromJson(gcmp.getString("hfJobInfoJsonStr", ""), objJobInfo.class);

            pcs = objGlobalMethods.getGCMPrefs(GcmPreferences, "pcs");
            reason += !objGlobalMethods.IsEmptyString(pcs) ? "\n\nPcs handed over: " + pcs : "";

            reason += currJobInfo.getTypeOfPinVerification().equals(1) || currJobInfo.getTypeOfPinVerification().equals(2) || currJobInfo.getTypeOfPinVerification().equals(3) ? "\n\n" + currJobInfo.getManualPinVerificaionText() : "";
            if (currJobInfo.getTypeOfPinVerification().equals(1)) {
                Receiver = "PIN Verification (" + Receiver + ")";
            }

            if (!objGlobalMethods.IsEmptyString(SignatureNameActivity.StaffID)) {
                Receiver += "\nStaff ID: " + SignatureNameActivity.StaffID;
            }

            //String checklistvalue = objGlobalMethods.getGCMPrefs(gcmp, "checklistvalue");
            String checklistvalue = objGlobalMethods.getChecklistRelatedSP_KeyValue("checklistvalue");

            if (!objGlobalMethods.IsEmptyString(checklistvalue)) {
                reason += "\n\n" + checklistvalue;
            }
            String pr_latemsg0 = objGlobalMethods.getGCMPrefs(gcmp, "pr_latemsg0");
            if (!objGlobalMethods.IsEmptyString(pr_latemsg0)) {
                reason += "\n" + pr_latemsg0;
            }

            String securityseallistvalue = "";
            securityseallistvalue = objGlobalMethods.getGCMPrefs(gcmp, "securityseallistvalue");
            if (!objGlobalMethods.IsEmptyString(securityseallistvalue)) {
                reason += "\n\n" + securityseallistvalue.toUpperCase();
            }

            CannotTakePhoto = objGlobalMethods.getGCMPrefs(gcmp, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto"));
            if (CannotTakePhoto > 0 && reason.length() > 0) {
                reason += "\n\nRestricted area. Photo taking not allowed.";
            } else if (CannotTakePhoto > 0 && reason.length() == 0) {
                reason += " Restricted area. Photo taking not allowed.";
            }

            Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
            int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);

            Boolean DOfullySubmitted = JobsHandler.ProcessDOincForSubmission(gcmp, updatep, aregid, idx);
            PreDeterminedStatusCodeIDX = objGlobalMethods.getGCMPrefs(gcmp, "PreDeterminedStatusCodeIDX");
            boolean nonOKPcode = !objGlobalMethods.IsEmptyString(PreDeterminedStatusCodeIDX);

            if (nonOKPcode) {
                reason += "\n\nAttended by: " + Receiver;
            }

            if (DOfullySubmitted) {
                JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(updatep, aregid, idx, nonOKPcode ? Long.parseLong(PreDeterminedStatusCodeIDX) :135,
                        atimestampstr, reason, "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, Receiver);
                statuscode = nonOKPcode ? "[80]" : "[135]";
                ////set from dip to okp
                DAO_Jobs.updateJobsCatTypeID(nonOKPcode ? 5 : 3, idx);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmp, 2);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmp, nonOKPcode ? 5 : 3);
            } else {
                JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(updatep, aregid, idx, nonOKPcode ? Long.parseLong(PreDeterminedStatusCodeIDX) : 47,
                        atimestampstr, reason, "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, Receiver);
                statuscode = nonOKPcode ? "[80]" : "[47]";
                ////set from dip to FD - partial delivery
                DAO_Jobs.updateJobsCatTypeID(5, idx);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmp, 2);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmp, 5);
            }

            convertedBitmapToBase64List = RTNobjConvertedBitmapToBase64List();
            CheckListconvertedBitmapToBase64List = RTNCheckListConvertedBitmapToBase64List();
            UploadJobSignatureIABitmapToBase64List = JobsHandler.RTNconvertedUploadJobSignatureIABitmapToBase64List();
            if (SimagePaths != null && SimagePaths.size() > 0) {
                for (int i = 0; i <= SimagePaths.size() - 1; i++) {
                    objGlobalMethods.deleteFile(SimagePaths.get(i));
                }
            }

            Camera5Activity.mCurrentPhotoPath = null;
            imagePaths = null;
            SimagePaths = null;
            dataT = null;
            mCurrentPhotoPath = "";
            mBitmap = null;

            if (CheckListconvertedBitmapToBase64List != null && CheckListconvertedBitmapToBase64List.size() > 0) {
                for (int x = 0; x <= CheckListconvertedBitmapToBase64List.size() - 1; x++) {
                    JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(updatep, aregid, idx,
                            !objGlobalMethods.IsEmptyString(CheckListconvertedBitmapToBase64List.get(x).photodesc) ? statuscode + CheckListconvertedBitmapToBase64List.get(x).photodesc : statuscode + Receiver,
                            CheckListconvertedBitmapToBase64List.get(x).convertedBitmapToBase64,
                            atimestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                            CheckListconvertedBitmapToBase64List.get(x).FilePath);
                }
            }

            if (convertedBitmapToBase64List != null && convertedBitmapToBase64List.size() > 0) {
                for (int x = 0; x <= convertedBitmapToBase64List.size() - 1; x++) {
                    JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(updatep, aregid, idx,
                            convertedBitmapToBase64List.get(x).isCallLogImg ? statuscode + "CALL_LOG" : statuscode + Receiver,
                            convertedBitmapToBase64List.get(x).convertedBitmapToBase64,
                            atimestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                            convertedBitmapToBase64List.get(x).FilePath);
                }
            }

            if (UploadJobSignatureIABitmapToBase64List != null && UploadJobSignatureIABitmapToBase64List.size() > 0) {
                for (int x = 0; x <= UploadJobSignatureIABitmapToBase64List.size() - 1; x++) {
                    JobsHandler.addUpdateJobSignatureIAQueALPrefs(updatep, aregid, idx, UploadJobSignatureIABitmapToBase64List.get(x),
                            atimestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                }
            }

            if (!objGlobalMethods.IsEmptyString(signatureString)) {
                JobsHandler.addUpdateJobSignatureQueALPrefs(updatep,
                        aregid, idx, signatureString, atimestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
            }

            tempDOCODJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "tempDOCODJsonStr");
            if (!objGlobalMethods.IsEmptyString(tempDOCODJsonStr)) {
                if (tempDOCODJsonStr != "[]") {
                    UpdateDOCOD updatedocod = gson.fromJson(tempDOCODJsonStr, UpdateDOCOD.class);
                    JobsHandler.addUpdateDOCODQueALPrefs(updatep, updatedocod);
                }
            }

            if (objGlobalMethods.cbReviewSelected()) {
                //objGlobalMethods.writeToFile("Review Link requested upon OKP for JobsIDX: " + JobInfoActivity.CanGetReviewLinkJobsIDX, "", "", "", "");
                JobsHandler.addSendReviewLinkQueALPrefs(updatep, aregid, JobInfoActivity.CanGetReviewLinkJobsIDX);
            }

            objJobInfo linkedJobsInfo = null;
            String linkedJobsInfoStr = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoLinkedJobInfo);
            try {
                if (!objGlobalMethods.IsEmptyString(linkedJobsInfoStr)) {
                    linkedJobsInfo = gson.fromJson(linkedJobsInfoStr, objJobInfo.class);
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Fail to convert Part 2 Json String to objJobInfo. " + e.getMessage().toString(),
                        "Part 1 JobsIDX: " + String.valueOf(idx), "Submitted Recipient: " + Receiver, "", "");

                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
                username = UsernamePwd[0];
                password = UsernamePwd[1];

                Mail m = new Mail("luke@xdel.com", "XDel2015");
                objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", "Fail to convert Part 2 Json String to objJobInfo, Part 1 JobsIDX: " + String.valueOf(idx) + ". " + e.getMessage().toString(),
                        "Luke update error " + username + ".", null);
                sendmail.execute();
            }

            JobsHandler.AddRemoveClearObjCallback(gcmp, "", "", true);
            Context ctx = myApp.getAppContext();
            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

            objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, true);

            if (linkedJobsInfo != null) {
                //prompt to update part 2 status
                objGlobalMethods.writeToFile("Prompt to PU or Fail Part 2 JobsIDX: " + String.valueOf(linkedJobsInfo.JobsIDX), "", "", "", "");
                resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob2SQLite(linkedJobsInfo);
            } else {
                resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob3SQLite();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity submitOKP. Exception: " + e.toString() + "\nStack: " + s, "", "", "", "");
            Camera5Activity.mCurrentPhotoPath = null;
            imagePaths = null;
            SimagePaths = null;
            dataT = null;
        }
    }

    private String[] getRequiredNumOfPhotosNdesc(int pos) {
        String[] al = new String[]{"", ""};
        String numstr = "0";
        String desc = "";
        Spinner_PostDataOKP postdata = null;
        try {
            Spinner_PostItemAdapterOKP adapter = (Spinner_PostItemAdapterOKP) ddlReceiver.getAdapter();
            if (adapter != null) {
                postdata = adapter.getItem(pos);
                numstr = postdata.IDX;
                desc = postdata.DescEN;
                al[0] = numstr;
                al[1] = desc;
            }
        } catch (Exception e) {
            al[0] = "0";
            al[1] = "";
        } finally {
            return al;
        }
    }

    private void resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob2SQLite(objJobInfo alinkedJobInfo) {
        final objJobInfo linkedJobInfo = alinkedJobInfo;
        final String deliveryContent = linkedJobInfo.DeliveryContents;
        Camera5Activity.mCurrentPhotoPath = null;
        bitmap = null;
        imagePaths = null;
        SimagePaths = null;
        dataT = null;
        ddlSelectedIndex = 0;
        ishuman = "0";
        mBitmap = null;
        mCurrentPhotoPath = null;
        remarks = "";
        SignatureNameActivity.ReceiverName = "";
        signatureString = "";
        selectedSpecificIDX = null;
        SignatureNameActivity.requireSignature = false;
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            //builder.setMessage(myApp.getAppContext().getString(R.string.alert_pup2) + "\n\n" + myApp.getAppContext().getString(R.string.lblCONTENT) + deliveryContent);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_pup2));

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_PU), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //if user pressed "yes", update job status
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();

                    Date dt = new Date();
                    Long l = dt.getTime();
                    String timestampstr = "/Date(" + l + ")/";

                    objGlobalMethods.writeToFile("Click to update Part 2 job status.",
                            "JobsIDX: " + String.valueOf(linkedJobInfo.JobsIDX), "Submitted StatusCodeIDX: 49", "", "");

                    Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                    int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                    String RegID = objGlobalMethods.getRegistrationIdOnly();
                    JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, linkedJobInfo.JobsIDX, 49, timestampstr, "", "",
                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

                    String tempUpdateaddUploadTaggingQueALPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "tempUpdateaddUploadTaggingQueALPrefStr");
                    if (!objGlobalMethods.IsEmptyString(tempUpdateaddUploadTaggingQueALPrefStr) && !tempUpdateaddUploadTaggingQueALPrefStr.equals("[]")) {
                        objTagging[] queAL;
                        Gson gson = new Gson();
                        queAL = gson.fromJson(tempUpdateaddUploadTaggingQueALPrefStr, objTagging[].class);
                        for (int qa = 0; qa <= queAL.length - 1; qa++) {
                            JobsHandler.addUploadTaggingQueALPrefs(UpdateJobInfoPreferences, queAL[qa]);
                        }
                    }

                    Context ctx = myApp.getAppContext();
                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                    ////add jobinfo to pu pref
                    Gson gson = new Gson();
                    String linkedJobStr = gson.toJson(linkedJobInfo);
                    DAO_Jobs.insertJobs(linkedJobInfo, 1, linkedJobStr);
                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 1);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoLinkedJobInfo, "");
                    resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob3SQLite();
                }
            });
            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_FP), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();

                    String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, "failPart2FromPart1Bool"};
                    String[] prefvalue = new String[]{"0", "t"};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");
                    //failPart2FromPart1Bool = true;
                    objGlobalMethods.writeToFile("Click to Fail Part 2 job status.",
                            "JobsIDX: " + String.valueOf(linkedJobInfo.JobsIDX), "", "", "");

                    //Intent parentIntent = new Intent(setDeliveredStatusActivity.this, setFailStatusActivity.class);
                    Intent parentIntent = new Intent(myApp.currentActivity, setFailStatusActivity.class);
                    MainActivity.uponLogin = 0;
                    startActivity(parentIntent);
                }
            });

            final AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob2SQLite. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob3SQLite() {
        mBitmap = null;
        mCurrentPhotoPath = null;
        ishuman = "0";
        remarks = "";
        SignatureNameActivity.ReceiverName = "";
        signatureString = "";
        selectedSpecificIDX = null;
        SignatureNameActivity.requireSignature = false;
        Camera5Activity.mCurrentPhotoPath = null;
        dataT = null;
        imagePaths = null;
        SimagePaths = null;
        bitmap = null;
        ddlSelectedIndex = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
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
            if (JobInfoActivity.frompage != null && JobInfoActivity.frompage.equals("Jobs_Retrieval_Activity")) {
                JobInfoActivity.frompage = null;
                String[] prefName0 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD", "isFromDashboardSub"};
                String[] prefvalue0 = new String[]{"", "", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName0, prefvalue0);

                parentIntent = new Intent(myApp.currentActivity, Jobs_Retrieval_Activity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (!GcmPreferences.getString("isFromMainCOD", "").isEmpty()) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
                parentIntent = new Intent(myApp.currentActivity, MainCODActivity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                setDeliveredStatusActivity.this.finish();
            } else {
                int TotalJobsCountPerCatTypePerPostal = 0;
                String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
                if (Postal != "") {
                    String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
                    TotalJobsCountPerCatTypePerPostal = !isVrpView() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal) :
                            !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(catType, Postal, SelectedVRPJobsIDXs)
                                    : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal);

                    if (TotalJobsCountPerCatTypePerPostal > 0) {
                        //back to JobsListGroupActivity
                        parentIntent = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
                        com.xdel.luke.MainActivity.uponLogin = 0;
                        startActivity(parentIntent);
                        setDeliveredStatusActivity.this.finish();
                    } else {
                        //back to DashboardSubActivity or AddressListRoutingActivity or AddressListNewActivity or DrawerMainActivity
                        if (!objGlobalMethods.getGCMPrefs(GcmPreferences, "isFromDashboardSub").isEmpty()) {
                            //back to DashboardSubActivity or AddressListRoutingActivity
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");

                            Integer TotalPIPDIPJobsCountPerPostal = DAO_Jobs.getTotalPIPDIPJobsCountPerPostal(Postal);
                            if (TotalPIPDIPJobsCountPerPostal > 0) {
                                parentIntent = new Intent(myApp.currentActivity, DashboardSubActivity.class);
                                MainActivity.uponLogin = 0;
                                startActivity(parentIntent);
                                setDeliveredStatusActivity.this.finish();
                            } else {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                                objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                                objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);

                                if (DashboardSubActivity.frompage != null) {
                                    parentIntent = new Intent(myApp.currentActivity, MapsAllActivity.class);
                                } else {
                                    parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                                }

                                MainActivity.uponLogin = 0;
                                startActivity(parentIntent);
                                setDeliveredStatusActivity.this.finish();
                            }
                        } else {
                            //back to AddressListNewActivity or DrawerMainActivity
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                            MainActivity.uponLogin = 0;
                            int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(catType);
                            if (TotalJobsCountPerCatType == 0) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                                parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                                startActivity(parentIntent);
                                setDeliveredStatusActivity.this.finish();
                            } else {
                                objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                                objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                                parentIntent = new Intent(myApp.currentActivity, AddressListNewActivity.class);
                                startActivity(parentIntent);
                                setDeliveredStatusActivity.this.finish();
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
                            parentIntent = new Intent(myApp.currentActivity, MapsAllActivity.class);
                        } else {
                            parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                        }

                        MainActivity.uponLogin = 0;
                        startActivity(parentIntent);
                        setDeliveredStatusActivity.this.finish();
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                        MainActivity.uponLogin = 0;
                        int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(catType);
                        if (TotalJobsCountPerCatType == 0) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                            parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                            startActivity(parentIntent);
                            setDeliveredStatusActivity.this.finish();
                        } else {
                            objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                            parentIntent = new Intent(myApp.currentActivity, AddressListNewActivity.class);
                            startActivity(parentIntent);
                            setDeliveredStatusActivity.this.finish();
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at setFailStatusActivity resetAddressListActivityhfJobsInfoArrJsonStrSQLite. Return to Main Dashboard. Exception: "
                    + e.getMessage() + "\nStack: " + s, "", "", "", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
            objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
            objGlobalMethods.resetJobsListGroupScrollIndexNpostion(GcmPreferences);
            com.xdel.luke.MainActivity.uponLogin = 0;
            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
            parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            startActivity(parentIntent);
            setDeliveredStatusActivity.this.finish();
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
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity convertBitmapToBase64. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            return "";
        }
        return encoded;
    }

    //****************Saving Signature************************************************************************************************************************

    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Date dt = new Date();
                        objGlobalMethods.writeToFile("Camera5Activity getAlbumDir failed to create directory.", "", "", "", "");
                        return null;
                    }
                }
            }

        } else {
            Toast.makeText(myApp.getAppContext(), "External storage is not mounted READ/WRITE.", Toast.LENGTH_SHORT);
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = GIF_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, GIF_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void galleryAddPic() {
        Date dt = new Date();
        try {
            Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at Camera5Activity galleryAddPic. " + e.getMessage().toString(), "", "", "", "");
            mCurrentPhotoPath = null;
        }
    }

    //****************Saving Signature************************************************************************************************************************

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
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity handleIntent. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    public void btnbtnGetReviewClick(View view) {
        try {
            if (JobInfoActivity.CanGetReviewLinkJobsIDX != null && JobInfoActivity.CanGetReviewLinkJobsIDX > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(myApp.getAppContext().getString(R.string.lblgetreviewlink2));

                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sendReviewLink srl = new sendReviewLink();
                            srl.execute();
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity btnbtnGetReviewClick setPositiveButton onClick. Exception: " + e.getMessage(), "", "", "", "");
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
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity btnbtnGetReviewClick. Exception: " + e.getMessage(), "", "", "", "");

        }
    }

    private class sendReviewLink extends AsyncTask<Void, Void, Boolean> {
        public sendReviewLink() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pdreview.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean asd = true;

            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                String RegID = objGlobalMethods.getRegistrationIdOnly();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                objGlobalMethods.writeToFile("Review Link requested for JobsIDX: " + String.valueOf(JobInfoActivity.CanGetReviewLinkJobsIDX), "", "", "", "");
                JobsHandler.addSendReviewLinkQueALPrefs(UpdateJobInfoPreferences, RegID, JobInfoActivity.CanGetReviewLinkJobsIDX);
                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity sendReviewLink doInBackground. Excpetion: " + e.getMessage().toString(), "", "", "", "");
                asd = false;
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (pdreview != null) {
                pdreview.dismiss();
            }
            try {
                if (success == null || !success) {
                    showDialog(myApp.getAppContext().getString(R.string.errgetreviewlink));
                } else if (success) {
                    showDialog(myApp.getAppContext().getString(R.string.lblgetreviewlink3));
                }

            } catch (Exception e) {
                if (pdreview != null) {
                    pdreview.dismiss();
                }
                objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity sendReviewLink onPostExecute. Excpetion: " + e.toString(), "", "", "", "");
                showDialog(myApp.getAppContext().getString(R.string.errgetreviewlink));
            }
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
                }
            }
        } catch (Exception e) {
            convertedBitmapToBase64List = null;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity RTNCheckListConvertedBitmapToBase64List. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return convertedBitmapToBase64List;
        }
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
                }
            }
        } catch (Exception e) {
            convertedBitmapToBase64List = null;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity RTNobjConvertedBitmapToBase64List. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return convertedBitmapToBase64List;
        }
    }

    private boolean hasImgTaken() {
        boolean asd = false;
        try {
            asd = (imagePaths != null && imagePaths.size() > 0 && dataT != null && dataT.size() > 0);
        } catch (Exception e) {
            asd = false;
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity hasImgTaken. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
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
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity CallLogImgTaken. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private void clearAllAttachedItemProofImg() {
        try {
            if (imagePaths != null && imagePaths.size() > 0 && dataT != null && dataT.size() > 0) {
                for (int i = dataT.size() - 1; i >= 0; i--) {
                    if (dataT.get(i).isItemProof) {
                        imagePaths.remove(i);
                        dataT.remove(i);
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity clearAllAttachedItemProofImg. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private CompoundButton.OnCheckedChangeListener cbReviewOnCheckedChangeListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (GcmPreferences == null) {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                    }
                    if (isChecked) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cbReviewSelected", "t");
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cbReviewSelected", "");
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity cbReviewOnCheckedChangeListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at setDeliveredStatusActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}

