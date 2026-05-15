package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
import com.xdel.luke.DOCOD.CODupdate2;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.ShpmtVerification.ShpmtV2Activity;
import com.xdel.luke.objClasses.Spinner_PostDataOKP;
import com.xdel.luke.objClasses.Spinner_PostItemAdapterOKP;
import com.xdel.luke.objClasses.UpdateDOCOD;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCallCheck;
import com.xdel.luke.objClasses.objCheckedItems;
import com.xdel.luke.objClasses.objConvertedBitmapToBase64;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objUpdateCOD_PostData;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class setDeliveredMassStatusActivity extends AppCompatActivity {

    Context context;
    static boolean errored = false;
    //private static String regid;
    private ProgressDialog pd, pd2, pdreview;
    ConnectionDetector cd;
    private String[] Recipients = new String[]{"Recipient", "Family Member", "Domestic Helper", "Neighbour", "Slot Under Door",
            "Slot In Mailbox", "Colleague", "Reception", "Concierge", "Front Desk", "Mailroom", "Warehouse/Store", "Guardhouse", "Riser", "Others"};
    private Spinner ddlsetDeliveredMassStatusReceiver, ddlSpecific;
    private EditText txtReason;
    private TextView lblRequirePhotoNote;
    private Button btnsetDeliveredMassStatusViewImg, btnSign, btnsetDeliveredMassStatusCamera, btnsetDeliveredMassStatusCancel, btnsetDeliveredMassStatusClear,
            btnsetDeliveredMassStatusNext, btnsetDeliveredMassStatusSubmit, btnGetReview;
    CheckBox cbReview;
    LinearLayout mContent, LLhidden, LLspecific01, LLspecific02;
    public static Bitmap mBitmap;
    View mView;
    File mypath;
    signature mSignature;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    public static int ddlSelectedIndex = 0;
    public static String ishuman = "0";
    public static String selectedRecipient;
    public static String selectedSpecific;
    public static boolean isFromMassSetOKP = false;
    private String RecipientTypesJsonStr;
    private boolean dropdownloadedSuccessful = true;
    public static Boolean hasPartOne;
    public static Boolean JobhasCOD = false;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    String convertedBitmapToBase64 = "";
    Bitmap bitmap = null;
    public static ArrayList<String> imagePaths;
    public static ArrayList<String> SimagePaths;
    public static ArrayList<CustomGallery> dataT;
    String langType = "en";
    public static String mCurrentPhotoPath;
    public static String GIF_FILE_PREFIX = "IMG_";
    public static String GIF_FILE_SUFFIX = ".gif";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    public static String signatureString;
    public static Integer selectedSpecificIDX;
    private static Boolean hasSignature = false;
    public static String remarks = "";
    private ProgressDialog progressDoalog;
    private int CannotTakePhoto;
    private Long CanGetReviewLinkJobsIDX = null;
    private TextChangeListener tc;
    TextView lblIsDefaultVRPpage;
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

        setContentView(R.layout.activity_set_delivered_mass_status);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));

        pd2 = new ProgressDialog(this);
        pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd2.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));

        pdreview = new ProgressDialog(this);
        pdreview.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdreview.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

        ddlsetDeliveredMassStatusReceiver = (Spinner) findViewById(R.id.ddlsetDeliveredMassStatusReceiver);
        ddlSpecific = (Spinner) findViewById(R.id.ddlSpecific);
        btnsetDeliveredMassStatusViewImg = (Button) findViewById(R.id.btnsetDeliveredMassStatusViewImg);
        btnsetDeliveredMassStatusCamera = (Button) findViewById(R.id.btnsetDeliveredMassStatusCamera);
        btnsetDeliveredMassStatusCancel = (Button) findViewById(R.id.btnsetDeliveredMassStatusCancel);
        /*btnsetDeliveredMassStatusClear = (Button) findViewById(R.id.btnsetDeliveredMassStatusClear);*/
        btnsetDeliveredMassStatusNext = (Button) findViewById(R.id.btnsetDeliveredMassStatusNext);
        btnsetDeliveredMassStatusSubmit = (Button) findViewById(R.id.btnsetDeliveredMassStatusSubmit);
        btnSign = (Button) findViewById(R.id.btnSign);
        btnGetReview = (Button) findViewById(R.id.btnGetReview);
        cbReview = findViewById(R.id.cbReview);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
       /* mContent = (LinearLayout) findViewById(R.id.setDeliveredMassStatuslinearLayout);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        mView = mContent;*/
        txtReason = (EditText) findViewById(R.id.txtReason);
        lblRequirePhotoNote = (TextView) findViewById(R.id.lblRequirePhotoNote);
        LLhidden = (LinearLayout) findViewById(R.id.LLhidden);
        LLspecific01 = (LinearLayout) findViewById(R.id.LLspecific01);
        LLspecific02 = (LinearLayout) findViewById(R.id.LLspecific02);

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
            setTitle(myApp.getAppContext().getString(R.string.title_activity_set_delivered_mass_status));
            myApp.currentActivity = this;
            context = myApp.getAppContext();

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
                objGlobalMethods.writeToFile("setDeliveredMassStatusActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

            //objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "mChildCheckStates2PrefStr", "");
            //JobsHandler.resetrePositionedGroupItems(GcmPreferences);

            objGlobalMethods.setGCMPrefsMass(GcmPreferences, new String[]{"hfJobInfoJsonStr", "mChildCheckStates2PrefStr"}, new String[]{"", ""});
            JobsHandler.checkForceLogOut();

            lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

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

            String RegID = objGlobalMethods.getRegistrationIdOnly();
            CannotTakePhoto = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto"));

            RecipientTypesJsonStr = objGlobalMethods.getStoredRecipientTypes(GcmPreferences);
            //getCanGetReviewLinkJobsIDX();
            if (!RecipientTypesJsonStr.equals("")) {
                loadddlReceiver3(RecipientTypesJsonStr);
                if (dropdownloadedSuccessful) {
                    ddlsetDeliveredMassStatusReceiver.setSelection(ddlSelectedIndex);
                    if (CannotTakePhoto > 0) {
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnsetDeliveredMassStatusViewImg);
                    } else if (CannotTakePhoto == 0) {
                        if (Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0)) {
                            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnsetDeliveredMassStatusViewImg);
                        } else if (Camera5Activity.mCurrentPhotoPath == null && (imagePaths != null && imagePaths.size() > 0)) {
                            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnsetDeliveredMassStatusViewImg);
                        } else {
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnsetDeliveredMassStatusViewImg);
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

            try {
                JobhasCOD = Boolean.parseBoolean(objGlobalMethods.getGCMPrefs(GcmPreferences, "JobhasCOD"));
            } catch (Exception e) {
                JobhasCOD = false;
            }

            if (hasPartOne == null) {
                hasPartOne = false;
            }

            if (JobhasCOD && hasPartOne) {
                btnsetDeliveredMassStatusNext.setVisibility(View.VISIBLE);
                btnsetDeliveredMassStatusSubmit.setVisibility(View.GONE);
            } else if (JobhasCOD && !hasPartOne) {
                btnsetDeliveredMassStatusNext.setVisibility(View.VISIBLE);
                btnsetDeliveredMassStatusSubmit.setVisibility(View.GONE);
            } else if (!JobhasCOD && hasPartOne) {
                btnsetDeliveredMassStatusNext.setVisibility(View.VISIBLE);
                btnsetDeliveredMassStatusSubmit.setVisibility(View.GONE);
            } else {
                btnsetDeliveredMassStatusNext.setVisibility(View.GONE);
                btnsetDeliveredMassStatusSubmit.setVisibility(View.VISIBLE);
            }
            hasSignature = false;
            txtReason.setText(remarks);
            cbReview.setOnCheckedChangeListener(null);
            cbReview.setChecked(objGlobalMethods.cbReviewSelected());
            cbReview.setOnCheckedChangeListener(cbReviewOnCheckedChangeListener());
        }
    }

    private void loadddlReceiver3(String aRecipientTypesJsonStr) {
        ArrayList<Spinner_PostDataOKP> SpinnerPostDataArr = new ArrayList<Spinner_PostDataOKP>();
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            SpinnerPostDataArr = JobsHandler.rtnRecipientTypeslatest3(GcmPreferences, aRecipientTypesJsonStr, langType);
            Spinner_PostItemAdapterOKP adapter = new Spinner_PostItemAdapterOKP(this, SpinnerPostDataArr);
            ddlsetDeliveredMassStatusReceiver.setAdapter(adapter);
            ddlsetDeliveredMassStatusReceiver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    try {
                        ddlSelectedIndex = position;
                        TextView lblStatusCodesDesc = (TextView) view.findViewById(R.id.lblStatusCodesDesc);
                        TextView lblDescEN = (TextView) view.findViewById(R.id.lblStatusCodesDescEN);
                        TextView lblInstructions = (TextView) view.findViewById(R.id.lblInstructions);
                        TextView lblFlag = ((TextView) (view.findViewById(R.id.lblFlag)));
                        boolean RecipientTypeRequireRemarks = false;
                        boolean remarksIsEmpty = objGlobalMethods.IsEmptyString(txtReason.getText().toString());
                        boolean reqRemarks = false;
                        LLspecific01.setVisibility(View.GONE);
                        LLspecific02.setVisibility(View.GONE);
                        LLhidden.setVisibility(View.VISIBLE);
                        txtReason.removeTextChangedListener(tc);

                        if (position == 0) {
                            ishuman = "0";
                            lblStatusCodesDesc.setBackgroundColor(Color.RED);
                            lblStatusCodesDesc.setTextColor(Color.WHITE);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnsetDeliveredMassStatusNext);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnsetDeliveredMassStatusSubmit);
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnSign);
                            SignatureNameActivity.ReceiverName = "";
                            //btnGetReview.setVisibility(View.GONE); // no longer in use since 05/11/2020
                            cbReview.setVisibility(View.GONE);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "cbReviewSelected", "");
                            if (CannotTakePhoto > 0) {
                                //hideshownote(true, "CannotTakePhoto");
                                hideshownote2(true, "CannotTakePhoto", "");
                                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnsetDeliveredMassStatusViewImg);
                            } else {
                                //hideshownote(false, "");
                                hideshownote2(false, "", "");
                            }

                            LLspecific01.setVisibility(View.GONE);
                            LLspecific02.setVisibility(View.GONE);
                            LLhidden.setVisibility(View.VISIBLE);
                            selectedSpecificIDX = null;
                        } else if (position > 0) {
                            lblStatusCodesDesc.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                            lblStatusCodesDesc.setTextColor(Color.WHITE);
                            String requirePhoto = ((TextView) view.findViewById(R.id.lblStatusCodesIDX)).getText().toString();
                            String requiredName = ((TextView) view.findViewById(R.id.lblName_Required)).getText().toString();
                            String instructions = lblInstructions.getText().toString();
                            TextView lblsubcatEN = ((TextView)(view.findViewById(R.id.lblsubcatEN)));
                            TextView lblsubcatZH = ((TextView)(view.findViewById(R.id.lblsubcatZH)));
                            String subcatEN = lblsubcatEN.getText().toString();
                            String subcatZH = lblsubcatZH.getText().toString();
                            String flagstr = lblFlag.getText().toString();

                            ishuman = ((TextView) view.findViewById(R.id.lblIsHuman)).getText().toString();
                            selectedRecipient = ((TextView) view.findViewById(R.id.lblStatusCodesDescEN)).getText().toString();
                            ctrlSettings.enableDisableCommonButtonWithStyles(ishuman.equals("1") ? true : false, btnSign);
                            if (selectedRecipient.toLowerCase().equals("recipient")) {
                                //SignatureNameActivity.ReceiverName = getCommonRecipientName();
                                String jfs = "";
                                long jobflag = 0;

                                try {
                                    jfs = objGlobalMethods.getGCMPrefs(GcmPreferences, "jobflag");
                                    jfs = !jfs.equals("") ? jfs : "0";
                                    jobflag = Integer.parseInt(jfs);
                                } catch (Exception e) {

                                }

                                //Boolean canGetReviewLink = (CanGetReviewLinkJobsIDX != null && CanGetReviewLinkJobsIDX > 0) && (jobflag & 16777216) == 16777216;
                                //btnGetReview.setVisibility(canGetReviewLink ? View.VISIBLE : View.GONE); // no longer in use since 05/11/2020
                                Boolean canGetReviewLink = (jobflag & 16777216) == 16777216;
                                cbReview.setVisibility(canGetReviewLink ? View.VISIBLE : View.GONE);
                            } else {
                                if (ishuman.equals("1") && !objGlobalMethods.IsEmptyString(SignatureNameActivity.ReceiverName) && SignatureNameActivity.ReceiverName.equals(getCommonRecipientName())) {
                                    SignatureNameActivity.ReceiverName = "";
                                } else if (!ishuman.equals("1")) {
                                    SignatureNameActivity.ReceiverName = "";
                                    signatureString = "";
                                    selectedSpecificIDX = null;
                                }

                                int flag = 0;
                                if (!objGlobalMethods.IsEmptyString(flagstr) ){
                                    try {
                                        flag = Integer.parseInt(flagstr);
                                    } catch (Exception e) {
                                        objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity ddlReceiver.setOnItemSelectedListener convert flag. Exception: " + e.getMessage(),"","","","");
                                    }
                                }
                                RecipientTypeRequireRemarks = JobsHandler2.RecipientTypeRequireRemarks(flag);

                                //btnGetReview.setVisibility(View.GONE); // no longer in use since 05/11/2020
                                cbReview.setVisibility(View.GONE);
                            }

                            reqRemarks = RecipientTypeRequireRemarks && remarksIsEmpty;
                            enabledisableSubmitNextBtns2(requirePhoto, lblDescEN.getText().toString(), instructions, reqRemarks);

                            if (RecipientTypeRequireRemarks) {
                                try {
                                    tc = new TextChangeListener(requirePhoto, lblDescEN.getText().toString(), instructions, reqRemarks);
                                    txtReason.addTextChangedListener(tc);
                                } catch (Exception e) {
                                    String s = JobsHandler.getStackTraceString(e);
                                    objGlobalMethods.writeToFile("Exception at setDeliveredStatusActivity ddlReceiver.setOnItemSelectedListener onTextChangeListener. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
                                }
                            } else {
                                txtReason.removeTextChangedListener(tc);
                            }

                            boolean loadgetsubCat = !objGlobalMethods.IsEmptyString(subcatEN) && !objGlobalMethods.IsEmptyString(subcatZH);
                            if (loadgetsubCat) {
                                loadSpecificDDL(subcatEN, subcatZH, requirePhoto, lblDescEN.getText().toString(), instructions);
                            }
                        }
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity ddlsetDeliveredMassStatusReceiver.setOnItemSelectedListener. btnsetDeliveredMassStatusNext is enabled. Exception: " + e.getMessage().toString(), "", "", "", "");
                        ishuman = "0";
                        ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnsetDeliveredMassStatusNext);
                        ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnsetDeliveredMassStatusSubmit);
                        //btnGetReview.setVisibility(View.GONE); // no longer in use since 05/11/2020
                        cbReview.setVisibility(View.GONE);
                        SignatureNameActivity.ReceiverName = "";
                        //hideshownote(false, "");
                        hideshownote2(false, "", "");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            dropdownloadedSuccessful = false;
        }
    }

    private void loadSpecificDDL(String subCatENJsonStr, String subCatZHJsonStr, final String aRequiredNumOfPhotostr, final String adesc, final String ainstructions) {
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

                Spinner_PostItemAdapterOKP adapter = new Spinner_PostItemAdapterOKP(this, SpinnerPostDataArr);
                ddlSpecific.setAdapter(adapter);

                ddlSpecific.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        processSelectedSpecific(position, true, aRequiredNumOfPhotostr, adesc, ainstructions);
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
                objGlobalMethods.writeToFile("setDeliveredMassStatusActivity SpecificDDL not loaded. EN n ZH length not matched.", "", "", "", "");
                LLspecific01.setVisibility(View.GONE);
                LLspecific02.setVisibility(View.GONE);
                LLhidden.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity loadSpecificDDL. Exception: " + e.getMessage(), "", "", "", "");
            LLspecific01.setVisibility(View.GONE);
            LLspecific02.setVisibility(View.GONE);
            LLhidden.setVisibility(View.VISIBLE);
        }
    }

    private void processSelectedSpecific(int pos, boolean auto, final String aRequiredNumOfPhotostr, final String adesc, final String instructions) {
        String selectedsubCat = "";
        try {
            if (ddlSpecific != null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                TextView lblStatusCodesDesc = ((TextView)(ddlSpecific.getSelectedView().findViewById(R.id.lblStatusCodesDesc)));

                if (pos == 0) {
                    lblStatusCodesDesc.setBackgroundColor(Color.RED);
                    lblStatusCodesDesc.setTextColor(Color.WHITE);
                    selectedSpecificIDX = 0;
                    selectedSpecific = "";
                } else {
                    lblStatusCodesDesc.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                    lblStatusCodesDesc.setTextColor(Color.WHITE);
                    TextView lblDescEN = ((TextView)(ddlSpecific.getSelectedView().findViewById(R.id.lblStatusCodesDescEN)));
                    selectedsubCat = lblDescEN.getText().toString();
                    lblStatusCodesDesc.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                    lblStatusCodesDesc.setTextColor(Color.WHITE);
                    selectedSpecificIDX = pos;
                    selectedSpecific = " (" + selectedsubCat + ")";

                    boolean remarksIsEmpty = objGlobalMethods.IsEmptyString(txtReason.getText().toString());
                    boolean reqRemarks = (objGlobalMethods.IsEqualString(selectedsubCat, "OTHERS") ? true : false) && remarksIsEmpty;
                    enabledisableSubmitNextBtns2(aRequiredNumOfPhotostr, lblDescEN.getText().toString(), instructions, reqRemarks);
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity processSelected. There was an error when selecting specific. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        remarks = txtReason.getText().toString();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onDestroy() {
        if (pd != null) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
        pd = null;

        if (pd2 != null) {
            if (pd2.isShowing()) {
                pd2.dismiss();
            }
        }
        pd2 = null;

        if (pdreview != null) {
            if (pdreview.isShowing()) {
                pdreview.dismiss();
            }
        }
        pdreview = null;

        if (progressDoalog != null) {
            if (progressDoalog.isShowing()) {
                progressDoalog.dismiss();
            }
        }
        progressDoalog = null;

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_delivered_mass_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        isFromMassSetOKP = false;
        Camera5Activity.mCurrentPhotoPath = null;
        dataT = null;
        imagePaths = null;
        SimagePaths = null;
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mBitmap = null;
        mCurrentPhotoPath = null;
        ddlSelectedIndex = 0;
        ishuman = "0";
        remarks = "";
        SignatureNameActivity.ReceiverName = "";
        signatureString = "";
        selectedSpecificIDX = null;


        MassUpdateOKPwP2.massupdateokpwp2PostDataArr = null;
        /*String objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
        String checklistvalue = objGlobalMethods.getChecklistRelatedSP_KeyValue("checklistvalue");
        String tempDOCODListJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "tempDOCODListJsonStr");*/

        String hasStandardRunJob = objGlobalMethods.getGCMPrefs(GcmPreferences, "isStandardRunJob");


        Intent parentIntent;
        if (!objGlobalMethods.IsEmptyString(hasStandardRunJob)) {
            String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "cbReviewSelected"};
            String[] prefvalue = new String[]{"", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
            String[] prefNameU = new String[]{"objPendingJobStatusUpdatesobjALPrefStr", "objPendingJobPhotoUpdatesobjALPrefStr", "mutlipleCODjobs", "objJobInfoBringtoUpdateCODListJsonStr", "ojisPage"};
            String[] prefvalueU = new String[]{"", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(UpdateJobInfoPreferences, prefNameU, prefvalueU);
            JobInfoActivity.accessFromDirection = 0;

            parentIntent = new Intent(this, JobsListGroupActivity.class);
        } else {
            parentIntent = new Intent(this, ShpmtV2Activity.class);
        }

        /*if (!objGlobalMethods.IsEmptyString(tempDOCODListJsonStr)) {
            parentIntent = new Intent(this, CODupdate2.class);
            JobInfoActivity.accessFromDirection = 0;
        } else if (!objGlobalMethods.IsEmptyString(checklistvalue)) {
            parentIntent = new Intent(this, CheckListActivity.class);
            JobInfoActivity.accessFromDirection = 0;
        } else if (!objCheckedItemsJsonStr.equals("")) {
            SignatureNameActivity.requireSignature = false;
            parentIntent = new Intent(this, JobsListGroupActivity.class);
            JobInfoActivity.accessFromDirection = 0;
            String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "cbReviewSelected"};
            String[] prefvalue = new String[]{"", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
            String[] prefNameU = new String[]{"objPendingJobStatusUpdatesobjALPrefStr", "objPendingJobPhotoUpdatesobjALPrefStr", "mutlipleCODjobs", "objJobInfoBringtoUpdateCODListJsonStr", "ojisPage"};
            String[] prefvalueU = new String[]{"", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(UpdateJobInfoPreferences, prefNameU, prefvalueU);
        } else {
            SignatureNameActivity.requireSignature = false;
            parentIntent = new Intent(this, JobInfoActivity.class);
            JobInfoActivity.accessFromDirection = 0;
            String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "cbReviewSelected"};
            String[] prefvalue = new String[]{"", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
            String[] prefNameU = new String[]{"objPendingJobStatusUpdatesobjALPrefStr", "objPendingJobPhotoUpdatesobjALPrefStr", "mutlipleCODjobs", "objJobInfoBringtoUpdateCODListJsonStr", "ojisPage"};
            String[] prefvalueU = new String[]{"", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(UpdateJobInfoPreferences, prefNameU, prefvalueU);
        }*/

        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    public void btnViewImgClick(View view) {
        isFromMassSetOKP = true;
        Camera5Activity.mCurrentPhotoPath = null;
        bitmap = null;
        remarks = txtReason.getText().toString();
        Intent i = new Intent(setDeliveredMassStatusActivity.this, CustomGalleryActivity.class);
        startActivity(i);
    }

    public void btnCameraClick(View view) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        CannotTakePhoto = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto"));

        if (CannotTakePhoto > 0) {
            showDialog(CannotTakePhoto == 3 ? myApp.getAppContext().getString(R.string.alert_allcannottakephoto).toUpperCase()
                    : CannotTakePhoto == 2 ? myApp.getAppContext().getString(R.string.alert_somecannottakephoto).toUpperCase()
                    : myApp.getAppContext().getString(R.string.alert_cannottakephoto).toUpperCase());
        } else {
            isFromMassSetOKP = true;
            Camera5Activity.mCurrentPhotoPath = null;
            bitmap = null;
            remarks = txtReason.getText().toString();
            Intent i = new Intent(setDeliveredMassStatusActivity.this, Camera5Activity.class);
            CheckListActivity.openCamera = null;
            CheckListActivity.itemIndex = null;
            try {
                if (imagePaths != null && imagePaths.size() > 0) {
                    if (imagePaths.size() >= 6) {
                        objGlobalMethods.writeToFile("setDeliveredMassStatusActivity btnCameraClick blocked. 6 photos already. ", "", "", "", "");
                        showDialog(myApp.getAppContext().getString(R.string.cannotlaunchcam));
                    } else {
                        objGlobalMethods.writeToFile("setDeliveredMassStatusActivity btnCameraClick. ", "", "", "", "");
                        startActivity(i);
                    }
                } else if (imagePaths != null && imagePaths.size() == 0) {
                    objGlobalMethods.writeToFile("setDeliveredMassStatusActivity btnCameraClick. ", "", "", "", "");
                    startActivity(i);
                } else if (imagePaths == null) {
                    objGlobalMethods.writeToFile("setDeliveredMassStatusActivity btnCameraClick. ", "", "", "", "");
                    startActivity(i);
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at setDeliveredMassStatusActivity btnCameraClick. " + e.getMessage().toString(), "", "", "", "");
            }
        }
    }

    public void CancelsetDeliveredStatus(View view) {
        onBackPressed();
    }

    public void clearSignature(View view) {
        mSignature.clear();
    }

    public String getCommonRecipientName() {
        String initn = "";
        String n = "";
        String finalresult = "";
        objJobInfo ji = null;
        try {
            Gson gson = new Gson();
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            String objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
            objCheckedItems[] objCheckedItemsAL = null;
            if (!objCheckedItemsJsonStr.equals("")) {
                objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
            } else {
                objCheckedItemsAL = null;
            }

            if (objCheckedItemsAL != null && objCheckedItemsAL.length > 0) {
                for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                    ji = objCheckedItemsAL[i].jobinfo;
                    n = JobsHandler.getRecipientNameFromJob(ji);
                    if (!objGlobalMethods.IsEmptyString(initn) && !objGlobalMethods.IsEmptyString(n) && !initn.equals(n)) {
                        initn = "";
                        break;
                    } else if (!objGlobalMethods.IsEmptyString(n)) {
                        initn = n;
                    } else if (objGlobalMethods.IsEmptyString(n)) {
                        continue;
                    }
                }
            }
            finalresult = initn;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusACtivity getCommonRecipientName. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return finalresult;
        }
    }

    public void btnSign_Click(View view) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        remarks = txtReason.getText().toString();
        SignatureNameActivity.rtnPageName = "setDeliveredMassStatusActivity";

        boolean showSign = SignatureNameActivity.requireSignature && objGlobalMethods.IsEmptyString(signatureString);
        Boolean hasStandardRunJob = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isStandardRunJob")) ? true : false;
        boolean showName = !hasStandardRunJob && ishuman.equals("1") && !selectedRecipient.toLowerCase().equals("recipient") && objGlobalMethods.IsEmptyString(SignatureNameActivity.ReceiverName);
        String[] prefName = new String[]{"reqSign", "reqName"};
        String[] prefvalue = new String[]{showSign ? "y" : "", showName ? "y" : ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        Intent intent = new Intent(this, SignatureNameActivity.class);
        startActivity(intent);
    }

    public void btnNextClick(View view) {
        String DOINV_Collection_Prompt_Msg = "";
        String DO_pcs = "";
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        try {
            Gson gson = new Gson();
            String objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
            String[] aCheckedItemsIDX = null;
            if (!objCheckedItemsIDXJsonStr.isEmpty()) {
                aCheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
            } else {
                aCheckedItemsIDX = null;
            }
            String str1 = aCheckedItemsIDX == null ? "" : Arrays.toString(aCheckedItemsIDX);
            if (!str1.isEmpty() && !str1.equals("") && str1 != "" && !str1.equals("[]") && str1 != "[]") {
                str1 = str1.substring(1, str1.length());
                str1 = str1.substring(0, str1.length() - 1);
            }

            //DOINV_Collection_Prompt_Msg = JobsHandler.returnListOfDOINV(str1);
            ArrayList<String> DOINV_chk = JobsHandler.returnListOfDOINVNew2(GcmPreferences);
            DOINV_Collection_Prompt_Msg = DOINV_chk != null && DOINV_chk.size() == 2 && !objGlobalMethods.IsEmptyString(DOINV_chk.get(0)) ? DOINV_chk.get(0) : "";
            DO_pcs = DOINV_chk != null && DOINV_chk.size() == 2 && !objGlobalMethods.IsEmptyString(DOINV_chk.get(1)) ? DOINV_chk.get(1) : "";
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity btnNextClick DOINV_Collection_Prompt_Msg. Exception: " + s, "", "", "", "");
        }

        try {
            Boolean hasStandardRunJob = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isStandardRunJob")) ? true : false;
            Boolean isResidential = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isResidential")) ? true : false;
            boolean showSign = (SignatureNameActivity.requireSignature && objGlobalMethods.IsEmptyString(signatureString) && ishuman.equals("1")) || (isResidential && objGlobalMethods.IsEmptyString(signatureString) && ishuman.equals("1"));
            //boolean showName = !hasStandardRunJob && ishuman.equals("1") && !selectedRecipient.toLowerCase().equals("recipient") && objGlobalMethods.IsEmptyString(SignatureNameActivity.ReceiverName);
            Boolean reqSelectSubCat = LLspecific01.getVisibility() == View.VISIBLE && LLspecific02.getVisibility() == View.VISIBLE;
            Boolean SubCatSelected = reqSelectSubCat && selectedSpecificIDX != null && selectedSpecificIDX > 0 && !objGlobalMethods.IsEmptyString(selectedSpecific);

            //if (showSign || showName) {
            if (showSign) {
                //showSignatureAlertNew(showSign, showName);
                showSignatureAlertNew(showSign, true);
            } else if (reqSelectSubCat && !SubCatSelected) {
                showDialog(myApp.getAppContext().getString(R.string.promptreclation));
            }  else {
                if (!DOINV_Collection_Prompt_Msg.isEmpty() && !DOINV_Collection_Prompt_Msg.equals("") && DOINV_Collection_Prompt_Msg != "") {
                    //promptForDOINVmsg(DOINV_Collection_Prompt_Msg);
                    promptDO_Collection(DOINV_Collection_Prompt_Msg, DO_pcs, 1);
                } else {
                    nextprompt();
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity btnNextClick promptForDOINVmsg or nextprompt. Exception: " + s, "", "", "", "");
        }
    }

    private void promptForDOINVmsg(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private AlertDialog promptDO_CollectionDialog = null;

    private void promptDO_Collection(final String doinv, final String DO_pcs, int mode) {
        //mode 0 = button submit click, 1 == button next click for cod collection
        try {
            if (objGlobalMethods.IsEmptyString(doinv) && objGlobalMethods.IsEmptyString(DO_pcs)) {
                if (mode == 0)
                    submitprompt("");
                else
                    nextprompt();
            } else {
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
                                    submitprompt("");
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
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity VerifyShipmentPromptNew. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void nextprompt() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        Gson gson = new Gson();
        isFromMassSetOKP = false;
        Camera5Activity.mCurrentPhotoPath = null;
        bitmap = null;
        String objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
        String objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
        objGlobalMethods.writeToFile("Click Next to set part 2 Job(s) after setting mass OKP for part 1 job(s).",
                "", "", "", "");

        objCheckedItems[] objCheckedItemsAL = null;
        String[] CheckedItemsIDX = null;

        if (!objCheckedItemsJsonStr.equals("")) {
            objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
        } else {
            objCheckedItemsAL = null;
        }

        if (!objCheckedItemsIDXJsonStr.equals("")) {
            CheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
        } else {
            CheckedItemsIDX = null;
        }

        if (JobhasCOD && hasPartOne) {
            //go to MassUpdateOKPwP2 then UpdateCOD_List
            prepareJobsToUpdateCOD_ListSQLiteNew(objCheckedItemsAL, CheckedItemsIDX, GcmPreferences, false);
        } else if (JobhasCOD && !hasPartOne) {
            //go to UpdateCODList
            prepareJobsToUpdateCOD_ListSQLiteNew(objCheckedItemsAL, CheckedItemsIDX, GcmPreferences, true);
        } else if (!JobhasCOD && hasPartOne) {
            //go to MassUpdateOKPwP2
            prepareJobsToUpdateCOD_ListSQLiteNew(objCheckedItemsAL, CheckedItemsIDX, GcmPreferences, false);
        } else {
            showErrorAlertNreturnToDashboard2("");
        }
    }

    private void prepareJobsToUpdateCOD_ListSQLiteNew(objCheckedItems[] aobjCheckedItemsAL, String[] aCheckedItemsIDX,
                                                   SharedPreferences aGcmPreferences, Boolean ProceedToUpdateCOD_List) {
        String ss = "Checklist taken from job(s) that required ID verification.\n";
        Gson gson = new Gson();
        ArrayList<Long> JobIDXsForContactlessChecklist = null;
        ArrayList<Long> JobIDXsForVerificationChecklist = null;
        ArrayList<String> JobNosForContactlessChecklist = null;
        ArrayList<String> JobNosForVerificationChecklist = null;
        String JobIDXsForContactlessChecklistJsonStr = objGlobalMethods.getGCMPrefs(aGcmPreferences, "JobIDXsForContactlessChecklistJsonStr");
        String JobIDXsForVerificationChecklistJsonStr = objGlobalMethods.getGCMPrefs(aGcmPreferences, "JobIDXsForVerificationChecklistJsonStr");
        String JobNosForContactlessChecklistJsonStr = objGlobalMethods.getGCMPrefs(aGcmPreferences, "JobNosForContactlessChecklistJsonStr");
        String JobNosForVerificationChecklistJsonStr = objGlobalMethods.getGCMPrefs(aGcmPreferences, "JobNosForVerificationChecklistJsonStr");
        if (!objGlobalMethods.IsEmptyString(JobIDXsForContactlessChecklistJsonStr)) {
            JobIDXsForContactlessChecklist = gson.fromJson(JobIDXsForContactlessChecklistJsonStr, new TypeToken<List<Long>>() {}.getType());
        }
        if (!objGlobalMethods.IsEmptyString(JobIDXsForVerificationChecklistJsonStr)) {
            JobIDXsForVerificationChecklist = gson.fromJson(JobIDXsForVerificationChecklistJsonStr, new TypeToken<List<Long>>() {}.getType());
        }

        String objUpdateCODPostDataListJsonStr = "";
        String objJobInfoBringtoUpdateCODListJsonStr = "";
        String mutlipleCODjobs = "";
        List<objUpdateCOD_PostData> objUpdateCODPostDataList = null;
        List<objJobInfo> objJobInfoBringtoUpdateCODList = null;
        objJobInfo ji;
        String part1cnno = "";
        Boolean hasCOD = false;
        /*String Receiver = selectedRecipient;
        String reason = txtReason.getText().toString();*/
        String pendingReceiver = selectedRecipient;
        if (!SignatureNameActivity.ReceiverName.isEmpty()) {
            pendingReceiver = pendingReceiver + ": " + SignatureNameActivity.ReceiverName;
        }
        pendingReceiver += !objGlobalMethods.IsEmptyString(selectedSpecific) ? selectedSpecific : "";
        String pendingreason = txtReason.getText().toString();

        String AdditionalCallLogsInfo = "";
        String objCallCheckJsonStr = objGlobalMethods.getGCMPrefs(aGcmPreferences, "objCallCheckJsonStr");
        objCallCheck occ = !objGlobalMethods.IsEmptyString(objCallCheckJsonStr) ? gson.fromJson(objCallCheckJsonStr, objCallCheck.class) : null;
        if (occ != null) {
            //reason += occ.getAdditionalCallLogsInfo();
            AdditionalCallLogsInfo += occ.getAdditionalCallLogsInfo();
        }

        //String checklistvalue = objGlobalMethods.getGCMPrefs(aGcmPreferences, "checklistvalue");
        String checklistvalue = objGlobalMethods.getChecklistRelatedSP_KeyValue("checklistvalue");

        if (!objGlobalMethods.IsEmptyString(checklistvalue)) {
            if (!objGlobalMethods.IsEmptyString(JobNosForContactlessChecklistJsonStr) && !objGlobalMethods.IsEmptyString(JobNosForVerificationChecklistJsonStr)) {
                JobNosForVerificationChecklist = gson.fromJson(JobNosForVerificationChecklistJsonStr, new TypeToken<List<String>>() {}.getType());
                String s = objGlobalMethods.rtnStringFromStringArrayList(JobNosForVerificationChecklist);
                ss += s + "\n";
            }
            //pendingreason += "\n\n" + checklistvalue;
        }

        CannotTakePhoto = objGlobalMethods.getGCMPrefs(aGcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(aGcmPreferences, "CannotTakePhoto"));
        String cannottakephoto = "";
        if (CannotTakePhoto > 0 && pendingreason.length() > 0) {
            //pendingreason += "\n\nRestricted area. Photo taking not allowed.";
            cannottakephoto += "\n\nRestricted area. Photo taking not allowed.";
        } else if (CannotTakePhoto > 0 && pendingreason.length() == 0) {
            //pendingreason += " Restricted area. Photo taking not allowed.";
            cannottakephoto += " Restricted area. Photo taking not allowed.";
        }

        final String reason = pendingreason;
        final String Receiver = pendingReceiver;
        final String aAdditionalCallLogsInfo = AdditionalCallLogsInfo;
        final String achecklistvalue = checklistvalue;
        final String acannottakephoto = cannottakephoto;

        final String sss = ss;
        final ArrayList<Long> JobIDXsForContactlessChecklistFinal = JobIDXsForContactlessChecklist;
        final ArrayList<Long> JobIDXsForVerificationChecklistFinal = JobIDXsForVerificationChecklist;

        ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = null;
        ArrayList<String> UploadJobSignatureIABitmapToBase64List = null;
        try {
            String RegID = objGlobalMethods.getRegistrationIdOnly();
            if (aobjCheckedItemsAL != null && aobjCheckedItemsAL.length > 0) {
                //convertedBitmapToBase64List = JobsHandler.RTNconvertedBitmapToBase64List();
                convertedBitmapToBase64List = RTNobjConvertedBitmapToBase64List();
                UploadJobSignatureIABitmapToBase64List = JobsHandler.RTNconvertedUploadJobSignatureIABitmapToBase64List();
                if (SimagePaths != null && SimagePaths.size() > 0) {
                    for (int i = 0; i <= SimagePaths.size() - 1; i++) {
                        objGlobalMethods.deleteFile(SimagePaths.get(i));
                    }
                }

                objUpdateCODPostDataListJsonStr = objGlobalMethods.getGCMPrefs(aGcmPreferences, "objUpdateCODPostDataListJsonStr");
                if (objUpdateCODPostDataListJsonStr.equals("") || objUpdateCODPostDataListJsonStr.equals("[]")) {
                    objUpdateCODPostDataList = null;
                } else {
                    objUpdateCODPostDataList = gson.fromJson(objUpdateCODPostDataListJsonStr, new TypeToken<List<objUpdateCOD_PostData>>() {
                    }.getType());
                }

                objJobInfoBringtoUpdateCODListJsonStr = objGlobalMethods.getGCMPrefs(aGcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr");
                if (objJobInfoBringtoUpdateCODListJsonStr.equals("") || objJobInfoBringtoUpdateCODListJsonStr.equals("[]")) {
                    objJobInfoBringtoUpdateCODList = null;
                } else {
                    objJobInfoBringtoUpdateCODList = gson.fromJson(objJobInfoBringtoUpdateCODListJsonStr, new TypeToken<List<objJobInfo>>() {
                    }.getType());
                }

                int pci = 0;
                String pcs = "0";
                objJobInfo currJobInfo = null;
                String finalReason = "";
                String finalReceiver = Receiver;

                String JobNos = "";
                for (int i = 0; i <= aobjCheckedItemsAL.length - 1; i++) {
                    currJobInfo = aobjCheckedItemsAL[i].jobinfo;
                    JobNos += !objGlobalMethods.IsEmptyString(JobNos) ? "," + String.valueOf(currJobInfo.JobNo) : String.valueOf(currJobInfo.JobNo);
                }

                for (int i = 0; i <= aobjCheckedItemsAL.length - 1; i++) {
                    hasCOD = false;
                    part1cnno = "";
                    part1cnno = aobjCheckedItemsAL[i].CNNO;

                    ji = aobjCheckedItemsAL[i].jobinfo;

                    pci = aobjCheckedItemsAL[i].pcs;
                    pcs = String.valueOf(pci);

                    finalReason = "Mass OKP." + reason;
                    finalReason += !objGlobalMethods.IsEmptyString(pcs) ? "\n\nPcs handed over: " + pcs : "";
                    finalReason +=
                            currJobInfo.getTypeOfPinVerification().equals(1) ||
                                    currJobInfo.getTypeOfPinVerification().equals(2) ||
                                    currJobInfo.getTypeOfPinVerification().equals(3) ? "\n\n" + currJobInfo.getManualPinVerificaionText() : "";

                    if (currJobInfo.getTypeOfPinVerification().equals(1)) {
                        finalReceiver = "PIN Verification (" + finalReceiver + ")";
                    }

                    if (!objGlobalMethods.IsEmptyString(acannottakephoto)) {
                        finalReason += "\n\n" + acannottakephoto;
                    }

                    if (!objGlobalMethods.IsEmptyString(aAdditionalCallLogsInfo)) {
                        finalReason += "\n\n" + aAdditionalCallLogsInfo;
                    }

                    if (!objGlobalMethods.IsEmptyString(achecklistvalue)) {
                        finalReason += "\n\n" + achecklistvalue;
                    }

                    if (!objGlobalMethods.IsEmptyArray(JobIDXsForContactlessChecklistFinal) && !objGlobalMethods.IsEmptyArray(JobIDXsForVerificationChecklistFinal)) {
                        if (JobIDXsForContactlessChecklistFinal.contains(Long.parseLong(aobjCheckedItemsAL[i].JobsIDX))) {
                            finalReason += "\n" + sss;
                        }
                    }

                    if (!objGlobalMethods.IsEmptyString(JobNos))
                        finalReason += "\n\nSelected Jobs:\n" + JobNos;


                    hasCOD = JobsHandler.checkForCODwithCatType(ji, 2);

                    if (hasCOD) {
                        objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, ji, 2, true,
                                RegID, false, part1cnno, finalReceiver);
                    }

                    ji.setpendingStatusUpdate(135);
                    ji.setReceiver(finalReceiver);
                    ji.setFailureReason("");
                    ji.setReason(finalReason);
                    //ji.setconvertedBitmapToBase64(convertedBitmapToBase64List);
                    ji.setObjConvertedBitmapToBase64(convertedBitmapToBase64List);
                    ji.setUploadJobSignatureIABitmapToBase64List(UploadJobSignatureIABitmapToBase64List);
                    ji.setSignatureStr(signatureString);
                    objJobInfoBringtoUpdateCODList = JobsHandler.RTNobjJobInfoBringtoUpdateCODList(objJobInfoBringtoUpdateCODList, ji);
                }

                if (objJobInfoBringtoUpdateCODList != null && objJobInfoBringtoUpdateCODList.size() > 0) {
                    objJobInfo[] objJobInfoBringtoUpdateCODAL = objJobInfoBringtoUpdateCODList.toArray(new objJobInfo[objJobInfoBringtoUpdateCODList.size()]);
                    objJobInfoBringtoUpdateCODListJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL);
                    if (objJobInfoBringtoUpdateCODListJsonStr.equals("[]")) {
                        objJobInfoBringtoUpdateCODListJsonStr = "";
                    }
                } else {
                    objJobInfoBringtoUpdateCODListJsonStr = "";
                }
                objGlobalMethods.setGCMPrefs(aGcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", objJobInfoBringtoUpdateCODListJsonStr);

                if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                    objUpdateCOD_PostData[] objUpdateCODPostDataAL = objUpdateCODPostDataList.toArray(new objUpdateCOD_PostData[objUpdateCODPostDataList.size()]);
                    mutlipleCODjobs = gson.toJson(objUpdateCODPostDataAL);
                    if (mutlipleCODjobs.equals("[]")) {
                        mutlipleCODjobs = "";
                    }
                } else {
                    mutlipleCODjobs = "";
                }
                objGlobalMethods.setGCMPrefs(aGcmPreferences, "mutlipleCODjobs", mutlipleCODjobs);

                if (ProceedToUpdateCOD_List) {
                    //ojisPage == 0 == JobInfoActivity | ojisPage == 1 == JobsListGroupActivity | ojisPage == 2 == setDeliveredStatusActivity |
                    //ojisPage == 3 == setDeliveredMassStatusActivity | ojisPage == 4 == MassUpdateOKPwP2 |
                    if (!objGlobalMethods.cbReviewSelected()) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "CanGetReviewLinkJobsIDXsJsonStr", "");
                    }
                    objGlobalMethods.setGCMPrefs(aGcmPreferences, "ojisPage", "3");
                    Intent intent = new Intent(this, UpdateCOD_List.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (!objGlobalMethods.cbReviewSelected()) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "CanGetReviewLinkJobsIDXsJsonStr", "");
                    }
                    Intent intent = new Intent(this, MassUpdateOKPwP2.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                showErrorAlertNreturnToDashboard2("");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setDeliveredMassStatusActivity prepareJobsToUpdateCOD_ListSQLiteNew. Exception: " + e.toString(), "", "", "", "");
            showErrorAlertNreturnToDashboard2("");
        }
    }

    private void prepareJobsToUpdateCOD_ListSQLite(objCheckedItems[] aobjCheckedItemsAL, String[] aCheckedItemsIDX,
                                                   SharedPreferences aGcmPreferences, Boolean ProceedToUpdateCOD_List) {
        Gson gson = new Gson();
        String objUpdateCODPostDataListJsonStr = "";
        String objJobInfoBringtoUpdateCODListJsonStr = "";
        String mutlipleCODjobs = "";
        List<objUpdateCOD_PostData> objUpdateCODPostDataList = null;
        List<objJobInfo> objJobInfoBringtoUpdateCODList = null;
        objJobInfo ji;
        String part1cnno = "";
        Boolean hasCOD = false;
        String Receiver = selectedRecipient;
        String reason = txtReason.getText().toString();

        String objCallCheckJsonStr = objGlobalMethods.getGCMPrefs(aGcmPreferences, "objCallCheckJsonStr");
        objCallCheck occ = !objGlobalMethods.IsEmptyString(objCallCheckJsonStr) ? gson.fromJson(objCallCheckJsonStr, objCallCheck.class) : null;
        if (occ != null) {
            reason += occ.getAdditionalCallLogsInfo();
        }

        //String checklistvalue = objGlobalMethods.getGCMPrefs(aGcmPreferences, "checklistvalue");
        String checklistvalue = objGlobalMethods.getChecklistRelatedSP_KeyValue("checklistvalue");

        if (checklistvalue.length() > 0) {
            reason += "\n\n" + checklistvalue;
        }

        CannotTakePhoto = objGlobalMethods.getGCMPrefs(aGcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(aGcmPreferences, "CannotTakePhoto"));
        if (CannotTakePhoto > 0 && reason.length() > 0) {
            reason += "\n\nRestricted area. Photo taking not allowed.";
        } else if (CannotTakePhoto > 0 && reason.length() == 0) {
            reason += " Restricted area. Photo taking not allowed.";
        }


        if (!SignatureNameActivity.ReceiverName.isEmpty()) {
            Receiver = Receiver + ": " + SignatureNameActivity.ReceiverName;
        }
        Receiver += !objGlobalMethods.IsEmptyString(selectedSpecific) ? selectedSpecific : "";

        ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = null;
        ArrayList<String> UploadJobSignatureIABitmapToBase64List = null;
        try {
            String RegID = objGlobalMethods.getRegistrationIdOnly();
            if (aobjCheckedItemsAL != null && aobjCheckedItemsAL.length > 0) {
                //convertedBitmapToBase64List = JobsHandler.RTNconvertedBitmapToBase64List();
                convertedBitmapToBase64List = RTNobjConvertedBitmapToBase64List();
                UploadJobSignatureIABitmapToBase64List = JobsHandler.RTNconvertedUploadJobSignatureIABitmapToBase64List();
                if (SimagePaths != null && SimagePaths.size() > 0) {
                    for (int i = 0; i <= SimagePaths.size() - 1; i++) {
                        objGlobalMethods.deleteFile(SimagePaths.get(i));
                    }
                }

                objUpdateCODPostDataListJsonStr = objGlobalMethods.getGCMPrefs(aGcmPreferences, "objUpdateCODPostDataListJsonStr");
                if (objUpdateCODPostDataListJsonStr.equals("") || objUpdateCODPostDataListJsonStr.equals("[]")) {
                    objUpdateCODPostDataList = null;
                } else {
                    objUpdateCODPostDataList = gson.fromJson(objUpdateCODPostDataListJsonStr, new TypeToken<List<objUpdateCOD_PostData>>() {
                    }.getType());
                }

                objJobInfoBringtoUpdateCODListJsonStr = objGlobalMethods.getGCMPrefs(aGcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr");
                if (objJobInfoBringtoUpdateCODListJsonStr.equals("") || objJobInfoBringtoUpdateCODListJsonStr.equals("[]")) {
                    objJobInfoBringtoUpdateCODList = null;
                } else {
                    objJobInfoBringtoUpdateCODList = gson.fromJson(objJobInfoBringtoUpdateCODListJsonStr, new TypeToken<List<objJobInfo>>() {
                    }.getType());
                }

                for (int i = 0; i <= aobjCheckedItemsAL.length - 1; i++) {
                    hasCOD = false;
                    part1cnno = "";
                    part1cnno = aobjCheckedItemsAL[i].CNNO;

                    ji = aobjCheckedItemsAL[i].jobinfo;

                    hasCOD = JobsHandler.checkForCODwithCatType(ji, 2);

                    if (hasCOD) {
                        objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, ji, 2, true,
                                RegID, false, part1cnno, Receiver);
                    }

                    ji.setpendingStatusUpdate(135);
                    ji.setReceiver(Receiver);
                    ji.setFailureReason("");
                    ji.setReason(reason);
                    //ji.setconvertedBitmapToBase64(convertedBitmapToBase64List);
                    ji.setObjConvertedBitmapToBase64(convertedBitmapToBase64List);
                    ji.setUploadJobSignatureIABitmapToBase64List(UploadJobSignatureIABitmapToBase64List);
                    ji.setSignatureStr(signatureString);
                    objJobInfoBringtoUpdateCODList = JobsHandler.RTNobjJobInfoBringtoUpdateCODList(objJobInfoBringtoUpdateCODList, ji);
                }

                if (objJobInfoBringtoUpdateCODList != null && objJobInfoBringtoUpdateCODList.size() > 0) {
                    objJobInfo[] objJobInfoBringtoUpdateCODAL = objJobInfoBringtoUpdateCODList.toArray(new objJobInfo[objJobInfoBringtoUpdateCODList.size()]);
                    objJobInfoBringtoUpdateCODListJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL);
                    if (objJobInfoBringtoUpdateCODListJsonStr.equals("[]")) {
                        objJobInfoBringtoUpdateCODListJsonStr = "";
                    }
                } else {
                    objJobInfoBringtoUpdateCODListJsonStr = "";
                }
                objGlobalMethods.setGCMPrefs(aGcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", objJobInfoBringtoUpdateCODListJsonStr);

                if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                    objUpdateCOD_PostData[] objUpdateCODPostDataAL = objUpdateCODPostDataList.toArray(new objUpdateCOD_PostData[objUpdateCODPostDataList.size()]);
                    mutlipleCODjobs = gson.toJson(objUpdateCODPostDataAL);
                    if (mutlipleCODjobs.equals("[]")) {
                        mutlipleCODjobs = "";
                    }
                } else {
                    mutlipleCODjobs = "";
                }
                objGlobalMethods.setGCMPrefs(aGcmPreferences, "mutlipleCODjobs", mutlipleCODjobs);

                if (ProceedToUpdateCOD_List) {
                    //ojisPage == 0 == JobInfoActivity | ojisPage == 1 == JobsListGroupActivity | ojisPage == 2 == setDeliveredStatusActivity |
                    //ojisPage == 3 == setDeliveredMassStatusActivity | ojisPage == 4 == MassUpdateOKPwP2 |
                    if (!objGlobalMethods.cbReviewSelected()) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "CanGetReviewLinkJobsIDXsJsonStr", "");
                    }
                    objGlobalMethods.setGCMPrefs(aGcmPreferences, "ojisPage", "3");
                    Intent intent = new Intent(this, UpdateCOD_List.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (!objGlobalMethods.cbReviewSelected()) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "CanGetReviewLinkJobsIDXsJsonStr", "");
                    }
                    Intent intent = new Intent(this, MassUpdateOKPwP2.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                showErrorAlertNreturnToDashboard2("");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setDeliveredMassStatusActivity prepareJobsToUpdateCOD_List. Exception: " + e.toString(), "", "", "", "");
            showErrorAlertNreturnToDashboard2("");
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
            /*if (imagePaths != null && imagePaths.size() > 0 && dataT != null && dataT.size() > 0) {
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
                }
            }*/
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setDeliveredMassStatusActivity RTNconvertedBitmapToBase64List. Exception: " + e.toString(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity RtnConvertedBitmapToBase64str. Exception: " + e.toString(), "", "", "", "");
            ConvertedBitmapToBase64str = "";
        } finally {
            return ConvertedBitmapToBase64str;
        }
    }

    public void btnSubmitClick(View view) {
        String DOINV_Collection_Prompt_Msg = "";
        String DO_pcs = "";
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        try {
            Gson gson = new Gson();
            String objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
            String[] aCheckedItemsIDX = null;
            if (!objCheckedItemsIDXJsonStr.isEmpty()) {
                aCheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
            } else {
                aCheckedItemsIDX = null;
            }
            String str1 = aCheckedItemsIDX == null ? "" : Arrays.toString(aCheckedItemsIDX);
            if (!str1.isEmpty() && !str1.equals("") && str1 != "" && !str1.equals("[]") && str1 != "[]") {
                str1 = str1.substring(1, str1.length());
                str1 = str1.substring(0, str1.length() - 1);
            }

            //DOINV_Collection_Prompt_Msg = JobsHandler.returnListOfDOINV(str1);
            ArrayList<String> DOINV_chk = JobsHandler.returnListOfDOINVNew2(GcmPreferences);
            DOINV_Collection_Prompt_Msg = DOINV_chk != null && DOINV_chk.size() == 2 && !objGlobalMethods.IsEmptyString(DOINV_chk.get(0)) ? DOINV_chk.get(0) : "";
            DO_pcs = DOINV_chk != null && DOINV_chk.size() == 2 && !objGlobalMethods.IsEmptyString(DOINV_chk.get(1)) ? DOINV_chk.get(1) : "";
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity btnSubmitClick DOINV_Collection_Prompt_Msg. Exception: " + s, "", "", "", "");
        }

        Boolean hasStandardRunJob = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isStandardRunJob")) ? true : false;
        /*boolean showSign = (SignatureNameActivity.requireSignature && (objGlobalMethods.IsEmptyString(SignatureNameActivity.ReceiverName) || objGlobalMethods.IsEmptyString(signatureString))) ||
                !hasStandardRunJob && ishuman.equals("1") && (objGlobalMethods.IsEmptyString(SignatureNameActivity.ReceiverName) || objGlobalMethods.IsEmptyString(signatureString)) ? true : false;*/
        Boolean isResidential = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isResidential")) ? true : false;
        Boolean requiredSignaturePrompt = JobsHandler.showSignatureRequiredPromptMass(GcmPreferences);
        boolean showSign = (requiredSignaturePrompt && SignatureNameActivity.requireSignature && objGlobalMethods.IsEmptyString(signatureString) && ishuman.equals("1")) ||
                (isResidential && objGlobalMethods.IsEmptyString(signatureString) && ishuman.equals("1"));
        //boolean showName = !hasStandardRunJob && ishuman.equals("1") && !selectedRecipient.toLowerCase().equals("recipient") && objGlobalMethods.IsEmptyString(SignatureNameActivity.ReceiverName);
        Boolean reqSelectSubCat = LLspecific01.getVisibility() == View.VISIBLE && LLspecific02.getVisibility() == View.VISIBLE;
        Boolean SubCatSelected = reqSelectSubCat && selectedSpecificIDX != null && selectedSpecificIDX > 0 && !objGlobalMethods.IsEmptyString(selectedSpecific);

        //if (showSign || showName) {
        if (showSign) {
            //showSignatureAlertNew(showSign, showName);
            showSignatureAlertNew(showSign, true);
        } else if (reqSelectSubCat && !SubCatSelected) {
            showDialog(myApp.getAppContext().getString(R.string.promptreclation));
        }  else {
            //submitprompt(DOINV_Collection_Prompt_Msg);
            promptDO_Collection(DOINV_Collection_Prompt_Msg, DO_pcs, 0);
        }
    }

    private void submitprompt(String DOINV_Collection_Prompt_Msg) {
        isFromMassSetOKP = false;
        Camera5Activity.mCurrentPhotoPath = null;
        bitmap = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        String msg = myApp.getAppContext().getString(R.string.alert_okp);
        msg += !DOINV_Collection_Prompt_Msg.isEmpty() && !DOINV_Collection_Prompt_Msg.equals("") && DOINV_Collection_Prompt_Msg != "" ? "\n\n" + DOINV_Collection_Prompt_Msg : "";
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", update job status
                Gson gson = new Gson();
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();

                String objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
                String objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
                objCheckedItems[] aobjCheckedItemsAL = null;
                String[] aCheckedItemsIDX = null;
                if (!objCheckedItemsJsonStr.isEmpty()) {
                    aobjCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                } else {
                    aobjCheckedItemsAL = null;
                }

                if (!objCheckedItemsIDXJsonStr.isEmpty()) {
                    aCheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
                } else {
                    aCheckedItemsIDX = null;
                }

                objGlobalMethods.writeToFile("Click to Mass OKP Job(s).",
                        "", "", "", "");

                //massUpdateJobStatusSubmitSQLite(aobjCheckedItemsAL, aCheckedItemsIDX, GcmPreferences, UpdateJobInfoPreferences, signatureString);
                massUpdateJobStatusSubmitSQLiteNew(aobjCheckedItemsAL, aCheckedItemsIDX, GcmPreferences, UpdateJobInfoPreferences, signatureString);

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

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(1);
        }
    };

    private void massUpdateJobStatusSubmitSQLiteNew(final objCheckedItems[] aobjCheckedItemsAL, String[] aCheckedItemsIDX, final SharedPreferences gcmPrefs,
                                                 final SharedPreferences updateJobInfoPrefs, final String signStr) {
        Date dt = new Date();
        String ss = "Checklist taken from job(s) that required ID verification.\n";
        Gson gson = new Gson();
        ArrayList<Long> JobIDXsForContactlessChecklist = null;
        ArrayList<Long> JobIDXsForVerificationChecklist = null;
        ArrayList<String> JobNosForContactlessChecklist = null;
        ArrayList<String> JobNosForVerificationChecklist = null;
        String JobIDXsForContactlessChecklistJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "JobIDXsForContactlessChecklistJsonStr");
        String JobIDXsForVerificationChecklistJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "JobIDXsForVerificationChecklistJsonStr");
        String JobNosForContactlessChecklistJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "JobNosForContactlessChecklistJsonStr");
        String JobNosForVerificationChecklistJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "JobNosForVerificationChecklistJsonStr");
        if (!objGlobalMethods.IsEmptyString(JobIDXsForContactlessChecklistJsonStr)) {
            JobIDXsForContactlessChecklist = gson.fromJson(JobIDXsForContactlessChecklistJsonStr, new TypeToken<List<Long>>() {}.getType());
        }
        if (!objGlobalMethods.IsEmptyString(JobIDXsForVerificationChecklistJsonStr)) {
            JobIDXsForVerificationChecklist = gson.fromJson(JobIDXsForVerificationChecklistJsonStr, new TypeToken<List<Long>>() {}.getType());
        }

        Long l = dt.getTime();
        final String timestampstr = "/Date(" + l + ")/";
        String pendingReceiver = selectedRecipient;
        if (!SignatureNameActivity.ReceiverName.isEmpty()) {
            pendingReceiver = pendingReceiver + ": " + SignatureNameActivity.ReceiverName;
        }
        pendingReceiver += !objGlobalMethods.IsEmptyString(selectedSpecific) ? selectedSpecific : "";

        String pendingreason = txtReason.getText().toString();
        String AdditionalCallLogsInfo = "";

        String objCallCheckJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "objCallCheckJsonStr");
        objCallCheck occ = !objGlobalMethods.IsEmptyString(objCallCheckJsonStr) ? gson.fromJson(objCallCheckJsonStr, objCallCheck.class) : null;
        if (occ != null) {
            //pendingreason += occ.getAdditionalCallLogsInfo();
            AdditionalCallLogsInfo += occ.getAdditionalCallLogsInfo();
        }

        //String checklistvalue = objGlobalMethods.getGCMPrefs(gcmPrefs, "checklistvalue");
        String checklistvalue = objGlobalMethods.getChecklistRelatedSP_KeyValue("checklistvalue");

        if (!objGlobalMethods.IsEmptyString(checklistvalue)) {
            if (!objGlobalMethods.IsEmptyString(JobNosForContactlessChecklistJsonStr) && !objGlobalMethods.IsEmptyString(JobNosForVerificationChecklistJsonStr)) {
                JobNosForVerificationChecklist = gson.fromJson(JobNosForVerificationChecklistJsonStr, new TypeToken<List<String>>() {}.getType());
                String s = objGlobalMethods.rtnStringFromStringArrayList(JobNosForVerificationChecklist);
                ss += s + "\n";
            }
            //pendingreason += "\n\n" + checklistvalue;
        }

        CannotTakePhoto = objGlobalMethods.getGCMPrefs(gcmPrefs, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(gcmPrefs, "CannotTakePhoto"));
        String cannottakephoto = "";
        if (CannotTakePhoto > 0 && pendingreason.length() > 0) {
            //pendingreason += "\n\nRestricted area. Photo taking not allowed.";
            cannottakephoto += "\n\nRestricted area. Photo taking not allowed.";
        } else if (CannotTakePhoto > 0 && pendingreason.length() == 0) {
            //pendingreason += " Restricted area. Photo taking not allowed.";
            cannottakephoto += " Restricted area. Photo taking not allowed.";
        }

        final String reason = pendingreason;
        final String Receiver = pendingReceiver;
        final String aAdditionalCallLogsInfo = AdditionalCallLogsInfo;
        final String achecklistvalue = checklistvalue;
        final String acannottakephoto = cannottakephoto;

        final String sss = ss;
        final ArrayList<Long> JobIDXsForContactlessChecklistFinal = JobIDXsForContactlessChecklist;
        final ArrayList<Long> JobIDXsForVerificationChecklistFinal = JobIDXsForVerificationChecklist;
        try {
            if (aobjCheckedItemsAL != null && aobjCheckedItemsAL.length > 0) {
                if (aCheckedItemsIDX != null && aCheckedItemsIDX.length > 0) {
                    String str1 = Arrays.toString(aCheckedItemsIDX);
                    objGlobalMethods.writeToFile("Mass OKP job(s).", "JobsIDXs: " + str1, "Receiver: " + Receiver, "", "");
                }
                progressDoalog = new ProgressDialog(this);
                progressDoalog.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));
                progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDoalog.setMax(aobjCheckedItemsAL.length);
                progressDoalog.setCanceledOnTouchOutside(false);
                progressDoalog.show();

                final Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                final int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                ////final ArrayList<String> convertedBitmapToBase64List = RTNconvertedBitmapToBase64List();
                //final ArrayList<String> convertedBitmapToBase64List = JobsHandler.RTNconvertedBitmapToBase64List();
                final ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = RTNobjConvertedBitmapToBase64List();
                final ArrayList<objConvertedBitmapToBase64> CheckListconvertedBitmapToBase64List = RTNCheckListConvertedBitmapToBase64List();
                final ArrayList<String> UploadJobSignatureIABitmapToBase64List = JobsHandler.RTNconvertedUploadJobSignatureIABitmapToBase64List();
                if (SimagePaths != null && SimagePaths.size() > 0) {
                    for (int i = 0; i <= SimagePaths.size() - 1; i++) {
                        objGlobalMethods.deleteFile(SimagePaths.get(i));
                    }
                }

                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();

                        try {
                            String RegID = objGlobalMethods.getRegistrationIdOnly();
                            String statuscode = "";
                            String finalReason = "";
                            String finalReceiver = Receiver;
                            int pci = 0;
                            String pcs = "0";
                            objJobInfo currJobInfo = null;

                            String JobNos = "";
                            for (int i = 0; i <= aobjCheckedItemsAL.length - 1; i++) {
                                currJobInfo = aobjCheckedItemsAL[i].jobinfo;
                                JobNos += !objGlobalMethods.IsEmptyString(JobNos) ? "," + String.valueOf(currJobInfo.JobNo) : String.valueOf(currJobInfo.JobNo);
                            }

                            for (int i = 0; i <= aobjCheckedItemsAL.length - 1; i++) {
                                Thread.sleep(200);
                                handle.sendMessage(handle.obtainMessage());

                                currJobInfo = aobjCheckedItemsAL[i].jobinfo;
                                pci = aobjCheckedItemsAL[i].pcs;
                                pcs = pci > 0 ? String.valueOf(pci) : "";

                                finalReason = "Mass OKP." + reason;
                                finalReason += !objGlobalMethods.IsEmptyString(pcs) ? "\n\nPcs handed over: " + pcs : "";
                                finalReason +=
                                        currJobInfo.getTypeOfPinVerification().equals(1) ||
                                                currJobInfo.getTypeOfPinVerification().equals(2) ||
                                                currJobInfo.getTypeOfPinVerification().equals(3) ? "\n\n" + currJobInfo.getManualPinVerificaionText() : "";
                                finalReceiver = Receiver;
                                if (currJobInfo.getTypeOfPinVerification().equals(1)) {
                                    finalReceiver = "PIN Verification (" + finalReceiver + ")";
                                }

                                if (!objGlobalMethods.IsEmptyString(acannottakephoto)) {
                                    finalReason += "\n\n" + acannottakephoto;
                                }

                                if (!objGlobalMethods.IsEmptyString(aAdditionalCallLogsInfo)) {
                                    finalReason += "\n\n" + aAdditionalCallLogsInfo;
                                }

                                if (!objGlobalMethods.IsEmptyString(achecklistvalue)) {
                                    finalReason += "\n\n" + achecklistvalue;
                                }

                                if (!objGlobalMethods.IsEmptyArray(JobIDXsForContactlessChecklistFinal) && !objGlobalMethods.IsEmptyArray(JobIDXsForVerificationChecklistFinal)) {
                                    if (JobIDXsForContactlessChecklistFinal.contains(Long.parseLong(aobjCheckedItemsAL[i].JobsIDX))) {
                                        finalReason += "\n" + sss;
                                    }
                                }

                                if (!objGlobalMethods.IsEmptyString(JobNos))
                                    finalReason += "\nSelected Jobs:\n" + JobNos;

                                Boolean DOfullySubmitted = JobsHandler.ProcessDOincForSubmission(gcmPrefs, updateJobInfoPrefs, RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX));
                                statuscode = "";

                                if (DOfullySubmitted) {
                                    JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(updateJobInfoPrefs, RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX),
                                            135, timestampstr, finalReason, "",
                                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, finalReceiver);
                                    statuscode = "[135]";
                                    //set from dip to OKP
                                    DAO_Jobs.updateJobsCatTypeID(3, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX));
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 2);
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 3);
                                } else {
                                    JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(updateJobInfoPrefs, RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX),
                                            47, timestampstr, finalReason, "",
                                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, finalReceiver);
                                    statuscode = "[47]";
                                    //set from dip to Partial Delivery
                                    DAO_Jobs.updateJobsCatTypeID(5, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX));
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 2);
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 5);
                                }

                                if (convertedBitmapToBase64List != null && convertedBitmapToBase64List.size() > 0) {
                                    for (int x = 0; x <= convertedBitmapToBase64List.size() - 1; x++) {
                                        JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(updateJobInfoPrefs, RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX),
                                                statuscode +  finalReceiver,
                                                convertedBitmapToBase64List.get(x).convertedBitmapToBase64, timestampstr, lastknownlocation.getLatitude(),
                                                lastknownlocation.getLongitude(), Accuracy,
                                                convertedBitmapToBase64List.get(x).FilePath);
                                    }
                                }

                                //newly added 07/05/2021
                                if (CheckListconvertedBitmapToBase64List != null && CheckListconvertedBitmapToBase64List.size() > 0) {
                                    for (int x = 0; x <= CheckListconvertedBitmapToBase64List.size() - 1; x++) {
                                        JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(updateJobInfoPrefs, RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX),
                                                !objGlobalMethods.IsEmptyString(CheckListconvertedBitmapToBase64List.get(x).photodesc) ?
                                                        statuscode + CheckListconvertedBitmapToBase64List.get(x).photodesc : statuscode + finalReceiver,
                                                CheckListconvertedBitmapToBase64List.get(x).convertedBitmapToBase64, timestampstr,
                                                lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                                CheckListconvertedBitmapToBase64List.get(x).FilePath);
                                    }
                                }
                                //newly added 07/05/2021

                                if (UploadJobSignatureIABitmapToBase64List != null && UploadJobSignatureIABitmapToBase64List.size() > 0) {
                                    for (int x = 0; x <= UploadJobSignatureIABitmapToBase64List.size() - 1; x++) {
                                        JobsHandler.addUpdateJobSignatureIAQueALPrefs(updateJobInfoPrefs,
                                                RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX), UploadJobSignatureIABitmapToBase64List.get(x),
                                                timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                                    }
                                }

                                //if (SignatureStr != null && !SignatureStr.equals("")) {
                                if (signStr != null && !signStr.equals("")) {
                                    JobsHandler.addUpdateJobSignatureQueALPrefs(updateJobInfoPrefs,
                                            RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX), signStr, timestampstr,
                                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                                }
                            }

                            String tempDOCODJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "tempDOCODJsonStr");
                            if (tempDOCODJsonStr != "" && tempDOCODJsonStr != "[]") {
                                UpdateDOCOD updatedocod = gson.fromJson(tempDOCODJsonStr, UpdateDOCOD.class);
                                JobsHandler.addUpdateDOCODQueALPrefs(UpdateJobInfoPreferences, updatedocod);
                            }

                            String tempDOCODListJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "tempDOCODListJsonStr");
                            if (tempDOCODListJsonStr != "" && tempDOCODListJsonStr != "[]") {
                                List<UpdateDOCOD> updatedocodList = gson.fromJson(tempDOCODListJsonStr, new TypeToken<List<UpdateDOCOD>>() {
                                }.getType());
                                for (int t = 0; t <= updatedocodList.size()-1; t++) {
                                    JobsHandler.addUpdateDOCODQueALPrefs(UpdateJobInfoPreferences, updatedocodList.get(t));
                                }
                            }

                            JobsHandler.processMassGetReviewLink(GcmPreferences, UpdateJobInfoPreferences, gson);

                            objGlobalMethods.setGCMPrefs(gcmPrefs, objGlobalVariables.checkedItemsName, "");
                            objGlobalMethods.setGCMPrefs(gcmPrefs, objGlobalVariables.checkedItemsIDX, "");

                            progressDoalog.dismiss();
                            JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "","", true);
                            Context ctx = myApp.getAppContext();
                            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                            objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, true);

                            resetAddressListActivityhfJobsInfoArrJsonStr2SQLite(gcmPrefs);
                        } catch (Exception e) {
                            progressDoalog.dismiss();
                            String stack = JobsHandler.getStackTraceString(e);
                            objGlobalMethods.writeToFile("Exception caught at setDeliveredMassStatusActivity massUpdateJobStatusSubmitSQLiteNew progress dialog thread. Exception: "
                                    + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                            JobsHandler.sendErrorEmail("Exception caught at setDeliveredMassStatusActivity massUpdateJobStatusSubmitSQLiteNew progress dialog thread. Exception: "
                                    + e.getMessage().toString() + "\n\nStack: " + stack);
                            showErrorAlertNreturnToDashboard2("");
                        }
                    }
                };
                t.start();
            } else {
                showErrorAlertNreturnToDashboard2("");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setDeliveredMassStatusActivity massUpdateJobStatusSubmitSQLiteNew. Exception: " + e.toString(), "", "", "", "");
            showErrorAlertNreturnToDashboard2("");
        }

    }

    //checked
    private void massUpdateJobStatusSubmitSQLite(final objCheckedItems[] aobjCheckedItemsAL, String[] aCheckedItemsIDX, final SharedPreferences gcmPrefs,
                                                 final SharedPreferences updateJobInfoPrefs, final String signStr) {
        Date dt = new Date();
        String ss = "Checklist taken from job(s) that required ID verification.\n";
        Gson gson = new Gson();
        ArrayList<Long> JobIDXsForContactlessChecklist = null;
        ArrayList<Long> JobIDXsForVerificationChecklist = null;
        ArrayList<String> JobNosForContactlessChecklist = null;
        ArrayList<String> JobNosForVerificationChecklist = null;
        String JobIDXsForContactlessChecklistJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "JobIDXsForContactlessChecklistJsonStr");
        String JobIDXsForVerificationChecklistJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "JobIDXsForVerificationChecklistJsonStr");
        String JobNosForContactlessChecklistJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "JobNosForContactlessChecklistJsonStr");
        String JobNosForVerificationChecklistJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "JobNosForVerificationChecklistJsonStr");
        if (!objGlobalMethods.IsEmptyString(JobIDXsForContactlessChecklistJsonStr)) {
            JobIDXsForContactlessChecklist = gson.fromJson(JobIDXsForContactlessChecklistJsonStr, new TypeToken<List<Long>>() {}.getType());
        }
        if (!objGlobalMethods.IsEmptyString(JobIDXsForVerificationChecklistJsonStr)) {
            JobIDXsForVerificationChecklist = gson.fromJson(JobIDXsForVerificationChecklistJsonStr, new TypeToken<List<Long>>() {}.getType());
        }

        Long l = dt.getTime();
        final String timestampstr = "/Date(" + l + ")/";
        String pendingReceiver = selectedRecipient;
        if (!SignatureNameActivity.ReceiverName.isEmpty()) {
            pendingReceiver = pendingReceiver + ": " + SignatureNameActivity.ReceiverName;
        }
        pendingReceiver += !objGlobalMethods.IsEmptyString(selectedSpecific) ? selectedSpecific : "";

        String pendingreason = txtReason.getText().toString();

        String objCallCheckJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "objCallCheckJsonStr");
        objCallCheck occ = !objGlobalMethods.IsEmptyString(objCallCheckJsonStr) ? gson.fromJson(objCallCheckJsonStr, objCallCheck.class) : null;
        if (occ != null) {
            pendingreason += occ.getAdditionalCallLogsInfo();
        }

        //String checklistvalue = objGlobalMethods.getGCMPrefs(gcmPrefs, "checklistvalue");
        String checklistvalue = objGlobalMethods.getChecklistRelatedSP_KeyValue("checklistvalue");

        if (!objGlobalMethods.IsEmptyString(checklistvalue)) {
            if (!objGlobalMethods.IsEmptyString(JobNosForContactlessChecklistJsonStr) && !objGlobalMethods.IsEmptyString(JobNosForVerificationChecklistJsonStr)) {
                JobNosForVerificationChecklist = gson.fromJson(JobNosForVerificationChecklistJsonStr, new TypeToken<List<String>>() {}.getType());
                String s = objGlobalMethods.rtnStringFromStringArrayList(JobNosForVerificationChecklist);
                ss += s + "\n";
            }
            pendingreason += "\n\n" + checklistvalue;
        }

        CannotTakePhoto = objGlobalMethods.getGCMPrefs(gcmPrefs, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(gcmPrefs, "CannotTakePhoto"));
        if (CannotTakePhoto > 0 && pendingreason.length() > 0) {
            pendingreason += "\n\nRestricted area. Photo taking not allowed.";
        } else if (CannotTakePhoto > 0 && pendingreason.length() == 0) {
            pendingreason += " Restricted area. Photo taking not allowed.";
        }

        final String Receiver = pendingReceiver;
        final String reason = pendingreason;
        final String sss = ss;
        final ArrayList<Long> JobIDXsForContactlessChecklistFinal = JobIDXsForContactlessChecklist;
        final ArrayList<Long> JobIDXsForVerificationChecklistFinal = JobIDXsForVerificationChecklist;
        try {
            if (aobjCheckedItemsAL != null && aobjCheckedItemsAL.length > 0) {
                if (aCheckedItemsIDX != null && aCheckedItemsIDX.length > 0) {
                    String str1 = Arrays.toString(aCheckedItemsIDX);
                    objGlobalMethods.writeToFile("Mass OKP job(s).", "JobsIDXs: " + str1, "Receiver: " + Receiver, "", "");
                }
                progressDoalog = new ProgressDialog(this);
                progressDoalog.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));
                progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDoalog.setMax(aobjCheckedItemsAL.length);
                progressDoalog.setCanceledOnTouchOutside(false);
                progressDoalog.show();

                final Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                final int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                ////final ArrayList<String> convertedBitmapToBase64List = RTNconvertedBitmapToBase64List();
                //final ArrayList<String> convertedBitmapToBase64List = JobsHandler.RTNconvertedBitmapToBase64List();
                final ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = RTNobjConvertedBitmapToBase64List();
                final ArrayList<objConvertedBitmapToBase64> CheckListconvertedBitmapToBase64List = RTNCheckListConvertedBitmapToBase64List();
                final ArrayList<String> UploadJobSignatureIABitmapToBase64List = JobsHandler.RTNconvertedUploadJobSignatureIABitmapToBase64List();
                if (SimagePaths != null && SimagePaths.size() > 0) {
                    for (int i = 0; i <= SimagePaths.size() - 1; i++) {
                        objGlobalMethods.deleteFile(SimagePaths.get(i));
                    }
                }

                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();

                        try {
                            String RegID = objGlobalMethods.getRegistrationIdOnly();
                            String statuscode = "";
                            String finalReason = "";
                            int pci = 0;
                            String pcs = "0";
                            for (int i = 0; i <= aobjCheckedItemsAL.length - 1; i++) {
                                Thread.sleep(200);
                                handle.sendMessage(handle.obtainMessage());

                                pci = aobjCheckedItemsAL[i].pcs;
                                pcs = String.valueOf(pci);

                                finalReason = "Pcs handed over: " + pcs + "\n\n" + reason;
                                if (!objGlobalMethods.IsEmptyArray(JobIDXsForContactlessChecklistFinal) && !objGlobalMethods.IsEmptyArray(JobIDXsForVerificationChecklistFinal)) {
                                    if (JobIDXsForContactlessChecklistFinal.contains(Long.parseLong(aobjCheckedItemsAL[i].JobsIDX))) {
                                        finalReason += "\n" + sss;
                                    }
                                }
                                Boolean DOfullySubmitted = JobsHandler.ProcessDOincForSubmission(gcmPrefs, updateJobInfoPrefs, RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX));
                                statuscode = "";

                                if (DOfullySubmitted) {
                                    /*JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(updateJobInfoPrefs, RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX), 135, timestampstr, reason, "",
                                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, Receiver);*/
                                    JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(updateJobInfoPrefs, RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX), 135, timestampstr, finalReason, "",
                                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, Receiver);
                                    statuscode = "[135]";
                                    //set from dip to OKP
                                    DAO_Jobs.updateJobsCatTypeID(3, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX));
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 2);
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 3);
                                } else {
                                    /*JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(updateJobInfoPrefs, RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX), 47, timestampstr, reason, "",
                                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, Receiver);*/
                                    JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(updateJobInfoPrefs, RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX), 47, timestampstr, finalReason, "",
                                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, Receiver);
                                    statuscode = "[47]";
                                    //set from dip to Partial Delivery
                                    DAO_Jobs.updateJobsCatTypeID(5, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX));
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 2);
                                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 5);
                                }

                                if (convertedBitmapToBase64List != null && convertedBitmapToBase64List.size() > 0) {
                                    for (int x = 0; x <= convertedBitmapToBase64List.size() - 1; x++) {
                                        JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(updateJobInfoPrefs, RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX),
                                                statuscode +  Receiver,
                                                convertedBitmapToBase64List.get(x).convertedBitmapToBase64, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                                convertedBitmapToBase64List.get(x).FilePath);
                                    }
                                }

                                //newly added 07/05/2021
                                if (CheckListconvertedBitmapToBase64List != null && CheckListconvertedBitmapToBase64List.size() > 0) {
                                    for (int x = 0; x <= CheckListconvertedBitmapToBase64List.size() - 1; x++) {
                                        JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(updateJobInfoPrefs, RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX),
                                                !objGlobalMethods.IsEmptyString(CheckListconvertedBitmapToBase64List.get(x).photodesc) ? statuscode + CheckListconvertedBitmapToBase64List.get(x).photodesc : statuscode + Receiver,
                                                CheckListconvertedBitmapToBase64List.get(x).convertedBitmapToBase64, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                                CheckListconvertedBitmapToBase64List.get(x).FilePath);
                                    }
                                }
                                //newly added 07/05/2021

                                if (UploadJobSignatureIABitmapToBase64List != null && UploadJobSignatureIABitmapToBase64List.size() > 0) {
                                    for (int x = 0; x <= UploadJobSignatureIABitmapToBase64List.size() - 1; x++) {
                                        JobsHandler.addUpdateJobSignatureIAQueALPrefs(updateJobInfoPrefs,
                                                RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX), UploadJobSignatureIABitmapToBase64List.get(x), timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                                    }
                                }

                                //if (SignatureStr != null && !SignatureStr.equals("")) {
                                if (signStr != null && !signStr.equals("")) {
                                    JobsHandler.addUpdateJobSignatureQueALPrefs(updateJobInfoPrefs,
                                            RegID, Long.parseLong(aobjCheckedItemsAL[i].JobsIDX), signStr, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                                }
                            }

                            String tempDOCODJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "tempDOCODJsonStr");
                            if (tempDOCODJsonStr != "" && tempDOCODJsonStr != "[]") {
                                UpdateDOCOD updatedocod = gson.fromJson(tempDOCODJsonStr, UpdateDOCOD.class);
                                JobsHandler.addUpdateDOCODQueALPrefs(UpdateJobInfoPreferences, updatedocod);
                            }

                            String tempDOCODListJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "tempDOCODListJsonStr");
                            if (tempDOCODListJsonStr != "" && tempDOCODListJsonStr != "[]") {
                                List<UpdateDOCOD> updatedocodList = gson.fromJson(tempDOCODListJsonStr, new TypeToken<List<UpdateDOCOD>>() {
                                }.getType());
                                for (int t = 0; t <= updatedocodList.size()-1; t++) {
                                    JobsHandler.addUpdateDOCODQueALPrefs(UpdateJobInfoPreferences, updatedocodList.get(t));
                                }
                            }

                            JobsHandler.processMassGetReviewLink(GcmPreferences, UpdateJobInfoPreferences, gson);

                            objGlobalMethods.setGCMPrefs(gcmPrefs, objGlobalVariables.checkedItemsName, "");
                            objGlobalMethods.setGCMPrefs(gcmPrefs, objGlobalVariables.checkedItemsIDX, "");

                            progressDoalog.dismiss();
                            JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "","", true);
                            Context ctx = myApp.getAppContext();
                            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                            objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, true);

                            resetAddressListActivityhfJobsInfoArrJsonStr2SQLite(gcmPrefs);
                        } catch (Exception e) {
                            progressDoalog.dismiss();
                            String stack = JobsHandler.getStackTraceString(e);
                            objGlobalMethods.writeToFile("Exception caught at setDeliveredMassStatusActivity massUpdateJobStatusSubmitSQLite progress dialog thread. Exception: "
                                    + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                            JobsHandler.sendErrorEmail("Exception caught at setDeliveredMassStatusActivity massUpdateJobStatusSubmitSQLite progress dialog thread. Exception: "
                                    + e.getMessage().toString() + "\n\nStack: " + stack);
                            showErrorAlertNreturnToDashboard2("");
                        }
                    }
                };
                t.start();
            } else {
                showErrorAlertNreturnToDashboard2("");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setDeliveredMassStatusActivity massUpdateJobStatusSubmitSQLite. Exception: " + e.toString(), "", "", "", "");
            showErrorAlertNreturnToDashboard2("");
        }

    }

    public void BackToLoginPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        Camera5Activity.mCurrentPhotoPath = null;
        dataT = null;
        imagePaths = null;
        SimagePaths = null;
        bitmap = null;
        ddlSelectedIndex = 0;
        ishuman = "0";
        remarks = "";
        SignatureNameActivity.ReceiverName = "";
        signatureString = "";
        selectedSpecificIDX = null;
        SignatureNameActivity.requireSignature = false;

        String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX,
                "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "isFromDashboardSub", "selectedRouteAddressJobsIDXs", "wifistate", "leftofc"};
        String[] prefvalue = new String[]{"-1", "", "", "", "", "", "", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        String[] uprefName = new String[]{"objPendingJobStatusUpdatesobjALPrefStr", "objPendingJobPhotoUpdatesobjALPrefStr"};
        String[] uprefvalue = new String[]{"", ""};
        objGlobalMethods.setUpdateJobInfoPrefsMass(UpdateJobInfoPreferences, uprefName, uprefvalue);

        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        Intent parentIntent;
        parentIntent = new Intent(setDeliveredMassStatusActivity.this, MainActivity.class);
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at steDeliveredMassStatusActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at steDeliveredMassStatusActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at steDeliveredMassStatusActivity showChangePermissionDialog. ActivityNotFoundException: "
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
                //if user pressed "ok", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                Camera5Activity.mCurrentPhotoPath = null;
                dataT = null;
                imagePaths = null;
                SimagePaths = null;
                bitmap = null;
                ddlSelectedIndex = 0;
                ishuman = "0";
                remarks = "";
                SignatureNameActivity.ReceiverName = "";
                signatureString = "";
                selectedSpecificIDX = null;
                SignatureNameActivity.requireSignature = false;

                Intent i;
                String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX,
                        "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "isFromDashboardSub", "selectedRouteAddressJobsIDXs"};
                String[] prefvalue = new String[]{"-1", "", "", "", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
                //i = new Intent(setDeliveredMassStatusActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(setDeliveredMassStatusActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSignatureAlertNew(final boolean reqSign, final boolean reqName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(objGlobalMethods.getOKPrequirementMsgNew(reqSign, reqName, false, false, ""));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "OK", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                remarks = txtReason.getText().toString();
                SignatureNameActivity.rtnPageName = "setDeliveredMassStatusActivity";

                String[] prefName = new String[]{"reqSign", "reqName"};
                String[] prefvalue = new String[]{reqSign ? "y" : "", reqName ? "y" : ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                Intent intent = new Intent(setDeliveredMassStatusActivity.this, SignatureNameActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSignatureAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.errrequiresignature));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "OK", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                remarks = txtReason.getText().toString();
                SignatureNameActivity.rtnPageName = "setDeliveredMassStatusActivity";
                Intent intent = new Intent(setDeliveredMassStatusActivity.this, SignatureNameActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showErrorAlertNreturnToDashboard2(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_unableupdatep1p21by1));
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                Camera5Activity.mCurrentPhotoPath = null;
                dataT = null;
                imagePaths = null;
                SimagePaths = null;
                bitmap = null;

                mBitmap = null;
                mCurrentPhotoPath = null;
                ddlSelectedIndex = 0;
                ishuman = "0";
                remarks = "";
                SignatureNameActivity.ReceiverName = "";
                signatureString = "";
                selectedSpecificIDX = null;
                SignatureNameActivity.requireSignature = false;

                String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX,
                        "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr"};
                String[] prefvalue = new String[]{"-1", "", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                String[] prefNameU = new String[]{"objPendingJobStatusUpdatesobjALPrefStr", "objPendingJobPhotoUpdatesobjALPrefStr", "objUpdateCODPostDataListJsonStr", "objJobInfoBringtoUpdateCODListJsonStr",
                        "mutlipleCODjobs", "ojisPage"};
                String[] prefvalueU = new String[]{"", "", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(UpdateJobInfoPreferences, prefNameU, prefvalueU);

                Intent i;
                com.xdel.luke.MainActivity.uponLogin = 0;
                //i = new Intent(setDeliveredMassStatusActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(setDeliveredMassStatusActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void enabledisableSubmitNextBtns2(String RequiredNumOfPhotostr, String desc, String instructions, boolean RecipientTypeRequireRemarks) {
        Context ctx = myApp.getAppContext();
        if (CannotTakePhoto > 0 && RequiredNumOfPhotostr.equals("0")) {
            hideshownote2(true, "CannotTakePhoto", "");
            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnsetDeliveredMassStatusSubmit);
            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnsetDeliveredMassStatusNext);
        } else if (CannotTakePhoto == 3 && !RequiredNumOfPhotostr.equals("0")) {
            hideshownote2(true, "CannotTakePhoto", "");
            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnsetDeliveredMassStatusSubmit);
            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnsetDeliveredMassStatusNext);
        } else if (CannotTakePhoto == 2 && !RequiredNumOfPhotostr.equals("0")) {
            hideshownote2(true, "CannotTakePhoto", "");
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnsetDeliveredMassStatusSubmit);
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnsetDeliveredMassStatusNext);
        } else if (CannotTakePhoto == 0) {
            if (RequiredNumOfPhotostr.equals("0") &&
                    ((Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0)) ||
                            (Camera5Activity.mCurrentPhotoPath == null || (imagePaths == null || imagePaths.size() == 0)))) {
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnsetDeliveredMassStatusSubmit);
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnsetDeliveredMassStatusNext);
            } else if (!RequiredNumOfPhotostr.equals("0")) {
                if (imagePaths == null || (imagePaths.size() < Integer.parseInt(RequiredNumOfPhotostr))) {
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnsetDeliveredMassStatusSubmit);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnsetDeliveredMassStatusNext);
                } else if (imagePaths != null && (imagePaths.size() >= Integer.parseInt(RequiredNumOfPhotostr))) {
                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnsetDeliveredMassStatusSubmit);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnsetDeliveredMassStatusNext);
                }
            }

            if (RecipientTypeRequireRemarks) {
                desc = "remarks";
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnsetDeliveredMassStatusSubmit);
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnsetDeliveredMassStatusNext);
            }

            if (desc.equals("remarks")) {
                hideshownote2(true, desc, objGlobalMethods.IsEmptyString(instructions) ? ctx.getString(R.string.lbl_enterremarkswhowhere) : instructions);
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
        private final String instructions;
        private final boolean RecipientTypeRequireRemarks;


        public TextChangeListener(String aRequiredNumOfPhotostr, String adesc,
                                  String ainstructions, boolean aRecipientTypeRequireRemarks) {
            this.RequiredNumOfPhotostr = aRequiredNumOfPhotostr;
            this.desc = adesc;
            this.instructions = ainstructions;
            this.RecipientTypeRequireRemarks = aRecipientTypeRequireRemarks;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                enabledisableSubmitNextBtns2(RequiredNumOfPhotostr, desc,
                        instructions, RecipientTypeRequireRemarks && objGlobalMethods.IsEmptyString(txtReason.getText().toString()) ? true : false);
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity TextChangeListener. Exception: " + e.getMessage() + "\nStack: " + ss,"","","","");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
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
            lblRequirePhotoNote.setText("");
        }
    }

    private void hideshownote(Boolean show, String type) {
        try {
            if (show && type.equals("ud")) {
                lblRequirePhotoNote.setText(myApp.getAppContext().getString(R.string.lbl_reqphoto_underdoor));
            } else if (show && type.equals("lb")) {
                lblRequirePhotoNote.setText(myApp.getAppContext().getString(R.string.lbl_reqphoto_lb));
            } else if (show && type.equals("riser")) {
                lblRequirePhotoNote.setText(myApp.getAppContext().getString(R.string.lbl_reqphoto_riser));
            } else if (show && type.equals("shoes")) {
                lblRequirePhotoNote.setText(myApp.getAppContext().getString(R.string.lbl_reqphoto_shoe));
            } else if (show && type.equals("cheque")) {
                lblRequirePhotoNote.setText(myApp.getAppContext().getString(R.string.lbl_reqphoto_cheque));
            } else if (show && type.toLowerCase().equals("cannottakephoto")) {
                lblRequirePhotoNote.setText(CannotTakePhoto == 3 ? myApp.getAppContext().getString(R.string.alert_allcannottakephoto).toUpperCase()
                        : CannotTakePhoto == 2 ? myApp.getAppContext().getString(R.string.alert_somecannottakephoto).toUpperCase()
                        : myApp.getAppContext().getString(R.string.alert_cannottakephoto).toUpperCase());
            } else if (show && type.equals("others")) {
                lblRequirePhotoNote.setText(myApp.getAppContext().getString(R.string.lbl_reqphoto));
            } else if ((show && type.isEmpty()) || !show) {
                lblRequirePhotoNote.setText("");
            }
        } catch (Exception e) {
            lblRequirePhotoNote.setText("");
        }
    }

    private void resetAddressListActivityhfJobsInfoArrJsonStr2SQLite(SharedPreferences gcmp) {
        //Intent parentIntent = new Intent(setDeliveredMassStatusActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        Intent parentIntent = new Intent(setDeliveredMassStatusActivity.this, !isVrpView() ? DrawerMainActivity.class : DrawerMain2Activity.class);

        mBitmap = null;
        mCurrentPhotoPath = null;
        ddlSelectedIndex = 0;
        ishuman = "0";
        remarks = "";
        SignatureNameActivity.ReceiverName = "";
        signatureString = "";
        selectedSpecificIDX = null;
        SignatureNameActivity.requireSignature = false;
        Integer CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(gcmp);
        JobsHandler.sethfJobsInfoArrJsonStrPref(gcmp, "");
        objGlobalMethods.setJobInfoJsonStrPref(gcmp, "");
        objGlobalMethods.resetJobsListGroupScrollIndexNpostion(gcmp);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();

        try {
            int TotalJobsCountPerCatTypePerPostal = 0;
            String Postal = objGlobalMethods.getGCMPrefs(gcmp, "desPostal");
            if (Postal != "") {
                String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(gcmp, "SelectedVRPJobsIDXs");
                /*TotalJobsCountPerCatTypePerPostal = !objGlobalMethods.IsDefaultVRPpage() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                        !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                                : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);*/
                TotalJobsCountPerCatTypePerPostal = !isVrpView() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                        !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                                : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);

                if (TotalJobsCountPerCatTypePerPostal > 0) {
                    //back to JobsListGroupActivity
                    parentIntent = new Intent(setDeliveredMassStatusActivity.this, JobsListGroupActivity.class);
                    com.xdel.luke.MainActivity.uponLogin = 0;
                    startActivity(parentIntent);
                    setDeliveredMassStatusActivity.this.finish();
                } else {
                    //back to DashboardSubActivity or AddressListRoutingActivity or AddressListNewActivity or DrawerMainActivity
                    if (!objGlobalMethods.getGCMPrefs(gcmp, "isFromDashboardSub").isEmpty()) {
                        //back to DashboardSubActivity or AddressListRoutingActivity
                        objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTypePrefName, "-1");
                        objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTitlePrefName, "");

                        Integer TotalPIPDIPJobsCountPerPostal = DAO_Jobs.getTotalPIPDIPJobsCountPerPostal(Postal);
                        if (TotalPIPDIPJobsCountPerPostal > 0) {
                            parentIntent = new Intent(this, DashboardSubActivity.class);
                            MainActivity.uponLogin = 0;
                            startActivity(parentIntent);
                            setDeliveredMassStatusActivity.this.finish();
                        } else {
                            objGlobalMethods.setGCMPrefs(gcmp, "isFromDashboardSub", "");
                            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(gcmp);
                            objGlobalMethods.resetAddressListNewScrollIndexNpostion(gcmp);

                            if (DashboardSubActivity.frompage != null) {
                                parentIntent = new Intent(this, MapsAllActivity.class);
                            } else {
                                //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                                parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                            }

                            MainActivity.uponLogin = 0;
                            startActivity(parentIntent);
                            setDeliveredMassStatusActivity.this.finish();
                        }
                    } else {
                        //back to AddressListNewActivity or DrawerMainActivity
                        objGlobalMethods.setGCMPrefs(gcmp, "isFromDashboardSub", "");
                        MainActivity.uponLogin = 0;
                        int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
                        if (TotalJobsCountPerCatType == 0) {
                            objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTypePrefName, "-1");
                            objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTitlePrefName, "");
                            //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                            parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                            startActivity(parentIntent);
                            setDeliveredMassStatusActivity.this.finish();
                        } else {
                            objGlobalMethods.resetAddressListNewScrollIndexNpostion(gcmp);
                            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(gcmp);
                            parentIntent = new Intent(this, AddressListNewActivity.class);
                            startActivity(parentIntent);
                            setDeliveredMassStatusActivity.this.finish();
                        }
                    }
                }
            } else {
                //back to DashboardSubActivity or AddressListRoutingActivity or AddressListNewActivity or DrawerMainActivity
                objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTitlePrefName, "");
                if (!objGlobalMethods.getGCMPrefs(gcmp, "isFromDashboardSub").isEmpty()) {
                    objGlobalMethods.setGCMPrefs(gcmp, "isFromDashboardSub", "");
                    objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(gcmp);
                    objGlobalMethods.resetAddressListNewScrollIndexNpostion(gcmp);

                    if (DashboardSubActivity.frompage != null) {
                        parentIntent = new Intent(this, MapsAllActivity.class);
                    } else {
                        //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                        parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                    }

                    MainActivity.uponLogin = 0;
                    startActivity(parentIntent);
                    setDeliveredMassStatusActivity.this.finish();
                } else {
                    objGlobalMethods.setGCMPrefs(gcmp, "isFromDashboardSub", "");
                    MainActivity.uponLogin = 0;
                    int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
                    if (TotalJobsCountPerCatType == 0) {
                        objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTypePrefName, "-1");
                        objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTitlePrefName, "");
                        //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                        parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                        startActivity(parentIntent);
                        setDeliveredMassStatusActivity.this.finish();
                    } else {
                        objGlobalMethods.resetAddressListNewScrollIndexNpostion(gcmp);
                        objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(gcmp);
                        parentIntent = new Intent(this, AddressListNewActivity.class);
                        startActivity(parentIntent);
                        setDeliveredMassStatusActivity.this.finish();
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setDeliveredMassStatusActivity resetAddressListActivityhfJobsInfoArrJsonStr2SQLite. Exception: " + e.toString(), "", "", "", "");
            objGlobalMethods.setGCMPrefs(gcmp, "isFromDashboardSub", "");
            objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTypePrefName, "-1");
            objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTitlePrefName, "");
            objGlobalMethods.setGCMPrefs(gcmp, "selectedRouteAddressJobsIDXs", "");
            //parentIntent = new Intent(setDeliveredMassStatusActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
            parentIntent = new Intent(setDeliveredMassStatusActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            setDeliveredMassStatusActivity.this.finish();
        }
    }

    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public String saveNreturnSignatureImgBitmapStr(View v) {
            Date dt = new Date();
            /*Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());*/
            String str = "";

            mCurrentPhotoPath = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
            } else {
                mAlbumStorageDirFactory = new BaseAlbumDirFactory();
            }

            File f = null;

            try {
                f = setUpPhotoFile();
                mCurrentPhotoPath = f.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                f = null;
                mCurrentPhotoPath = null;
            }


            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(mBitmap);
            try {
                FileOutputStream mFileOutStream = new FileOutputStream(mCurrentPhotoPath);

                v.draw(canvas);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();
                String url = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);

                //In case you want to delete the file
                //boolean deleted = mypath.delete();
                //Log.v("log_tag","deleted: " + mypath.toString() + deleted);
                //If you want to convert the image to string use base64 converter

                str = convertBitmapToBase64(mBitmap);
                mBitmap.recycle();
                mBitmap = null;

            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity signature class save function. Exception: " + e.toString(), "", "", "", "");
                str = "";
            } finally {
                return str;
            }
        }

        public void save(View v) {
            /*Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());*/
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
                ;
            }
            Canvas canvas = new Canvas(mBitmap);
            try {
                FileOutputStream mFileOutStream = new FileOutputStream(mypath);

                v.draw(canvas);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();
                String url = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);
                Log.v("log_tag", "url: " + url);
                //In case you want to delete the file
                //boolean deleted = mypath.delete();
                //Log.v("log_tag","deleted: " + mypath.toString() + deleted);
                //If you want to convert the image to string use base64 converter

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear() {
            path.reset();
            hasSignature = false;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            //mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    hasSignature = true;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    hasSignature = true;
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
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
                        //Log.d("CameraSample", "failed to create directory");
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

    //****************Saving Signature************************************************************************************************************************

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
                }
            }
        } catch (Exception e) {
            convertedBitmapToBase64List = null;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity RTNCheckListConvertedBitmapToBase64List. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return convertedBitmapToBase64List;
        }
    }

    private void getCanGetReviewLinkJobsIDX() {
        String JobsIDX = "";
        String jfs = "";
        long jobflag = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        try {
            jfs = objGlobalMethods.getGCMPrefs(GcmPreferences, "jobflag");
            jfs = !jfs.equals("") ? jfs : "0";
            jobflag = Integer.parseInt(jfs);

            if ((jobflag & 16777216) == 16777216) {
                Gson gson = new Gson();
                String objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
                String[] aCheckedItemsIDX = null;
                if (!objCheckedItemsIDXJsonStr.isEmpty()) {
                    aCheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
                } else {
                    aCheckedItemsIDX = null;
                }
                if (aCheckedItemsIDX != null && aCheckedItemsIDX.length > 0) {
                    for (int i = 0; i <= aCheckedItemsIDX.length - 1; i++) {
                        JobsIDX = aCheckedItemsIDX[0];
                        if (!JobsIDX.isEmpty() && !JobsIDX.equals("")) {
                            CanGetReviewLinkJobsIDX = !JobsIDX.isEmpty() && !JobsIDX.equals("") ? Long.parseLong(JobsIDX) : Long.parseLong("0");
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity getCanGetReviewLinkJobsIDX. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void btnbtnGetReviewClick(View view) {
        try {
            if (CanGetReviewLinkJobsIDX != null && CanGetReviewLinkJobsIDX > 0) {
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
                            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity btnbtnGetReviewClick setPositiveButton onClick. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity btnbtnGetReviewClick. Exception: " + e.getMessage(), "", "", "", "");

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
                objGlobalMethods.writeToFile("Review Link requested for JobsIDX: " + String.valueOf(CanGetReviewLinkJobsIDX), "", "", "", "");
                JobsHandler.addSendReviewLinkQueALPrefs(UpdateJobInfoPreferences, RegID, CanGetReviewLinkJobsIDX);
                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at setDeliveredMassStatusActivity sendReviewLink doInBackground. Excpetion: " + e.getMessage().toString(), "", "", "", "");
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
                objGlobalMethods.writeToFile("Exception caught at setDeliveredMassStatusActivity sendReviewLink onPostExecute. Excpetion: " + e.toString(), "", "", "", "");
                showDialog(myApp.getAppContext().getString(R.string.errgetreviewlink));
            }
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
                    objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity cbReviewOnCheckedChangeListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at setDeliveredMassStatusActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDeliveredMassStatusActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
