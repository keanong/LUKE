package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.Spinner_PostData;
import com.xdel.luke.objClasses.Spinner_PostItemAdapter;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objADunningStatus;
import com.xdel.luke.objClasses.objConvertedBitmapToBase64;
import com.xdel.luke.objClasses.objDunningSubmit;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class setDunningDeliveredStatusActivity extends AppCompatActivity {
    public static String remarks = "";
    public static Bitmap mBitmap;
    public static String mCurrentPhotoPath;
    public static String GIF_FILE_PREFIX = "IMG_";
    public static String GIF_FILE_SUFFIX = ".gif";
    public static String signatureString;
    private static Boolean hasSignature = false;
    public static boolean isFromSetDunningOKP = false;
    public static ArrayList<String> imagePaths;
    public static ArrayList<CustomGallery> dataT;
    public static Integer TotalJobsCountPerCatTypePerPostalB4Updates = 0;

    //private String regid;
    Context context;
    private ProgressDialog pd;
    ConnectionDetector cd;
    private String[] valRxBy;
    private String[] valSlotted;
    private String[] valnotDelivered;
    private String[] valBuilding;
    private String[] valRxByValue = new String[]{"dsConsignee", "dsConsigneeSpouse", "dsFamilyMember", "dsFamilyHelper", "dsFriend", "dsRoomMate", "dsTenant", "dsColleague", "dsReception"};
    private String[] valSlottedValue = new String[]{"dsSlotLB", "dsSlotDoor"};
    private String[] valnotDeliveredValue = new String[]{"dsNDUnableToSlot", "dsConsigneeAbroad", "dsConsigneeNoLongerAtAddress", "dsOthers"};
    private String[] valBuildingValue = new String[]{"dwHDB", "dwAptCondo", "dwLanded", "dwOfficeFactory", "dwShopRestaurant"};
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    private RadioGroup rg1;
    private Spinner ddlReceiver, ddlBuilding;
    private TextView tv2;
    private TextView lblJobNo;
    private EditText txtRemarks;
    private Button btnViewImg, btnCancel, btnClear, btnSubmit, btnSign, btnCamera;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    LinearLayout mContent;
    signature mSignature;
    View mView;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    String SelectedDunningMainOptionIdxStr = "";
    String SelectedDunningspinnerIdxStr = "";
    String SelectedDunningBuildingIdxStr = "";
    Integer SelectedDunningMainOptionIdx;
    Integer SelectedDunningspinnerIdx;
    Integer SelectedDunningBuildingIdx;
    String DunningDelivery_Status;
    String DunningType_of_Dwelling;
    String CNNO;
    String addressWithUnit;
    private Boolean DunningspinnerLoadedSuccessful = true;
    private Boolean DunningBuildingLoadedSuccessful = true;
    String convertedBitmapToBase64 = "";
    Bitmap bitmap = null;
    String langType = "en";
    private boolean CannotTakePhoto = false;
    TextView lblIsDefaultVRPpage;

    ScreenShotContentObserver screenShotContentObserver;

    private void initdropdownMenu() {

        valRxBy = new String[]{
                myApp.getAppContext().getString(R.string.dunning_RxByConsignee),
                myApp.getAppContext().getString(R.string.dunning_RxByConsigneeSpouse),
                myApp.getAppContext().getString(R.string.dunning_RxByFamilyMember),
                myApp.getAppContext().getString(R.string.dunning_RxByFamilyHelper),
                myApp.getAppContext().getString(R.string.dunning_RxByFriend),
                myApp.getAppContext().getString(R.string.dunning_RxByRoomMate),
                myApp.getAppContext().getString(R.string.dunning_RxByTenant),
                myApp.getAppContext().getString(R.string.dunning_RxByColleague),
                myApp.getAppContext().getString(R.string.dunning_RxByReception)};

        valSlotted = new String[]{
                myApp.getAppContext().getString(R.string.dunning_SlottedLetterbox),
                myApp.getAppContext().getString(R.string.dunning_SlottedUnderDoor)};

        valnotDelivered = new String[]{
                myApp.getAppContext().getString(R.string.dunning_NonDeliveryUnableSlot),
                myApp.getAppContext().getString(R.string.dunning_NonDeliveryAbroad),
                myApp.getAppContext().getString(R.string.dunning_NonDeliveryNoLonger),
                myApp.getAppContext().getString(R.string.dunning_NonDeliveryOthers)};

        valBuilding = new String[]{
                myApp.getAppContext().getString(R.string.dunning_BuildingHDB),
                myApp.getAppContext().getString(R.string.dunning_BuildingCondo),
                myApp.getAppContext().getString(R.string.dunning_BuildingLanded),
                myApp.getAppContext().getString(R.string.dunning_BuildingOffice),
                myApp.getAppContext().getString(R.string.dunning_BuildingShop)};


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

        setContentView(R.layout.activity_set_dunning_delivered_status);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));
        rg1 = (RadioGroup) findViewById(R.id.rg1);
        tv2 = (TextView) findViewById(R.id.textView2);
        lblJobNo = (TextView) findViewById(R.id.lblJobNo);
        ddlReceiver = (Spinner) findViewById(R.id.ddlReceiver);
        ddlBuilding = (Spinner) findViewById(R.id.ddlBuilding);
        btnSign = (Button) findViewById(R.id.btnSign);
        btnViewImg = (Button) findViewById(R.id.btnViewImg);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
       /* mContent = (LinearLayout) findViewById(R.id.linearLayout);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        mView = mContent;*/
        txtRemarks = (EditText) findViewById(R.id.txtRemarks);

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            myApp.currentActivity = this;
            context = myApp.getAppContext();

            Gson gson = new Gson();

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

            if (!temploggerclass.isBTEnabled()) {
                objGlobalMethods.writeToFile("setDunningDeliveredStatusActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

            JobsHandler.checkForceLogOut();

            lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

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
            String enableJobUpdatesPref = objGlobalMethods.getGCMPrefs(GcmPreferences, "enableJobUpdates");
            boolean enableJobUpdates = enableJobUpdatesPref.equals("") || enableJobUpdatesPref.equals("true") ? true : false;

            if (username.equals("") && password.equals("")) {
                BackToLoginPage();
            }

            String RegID = objGlobalMethods.getRegistrationIdOnly();
            CannotTakePhoto = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto") == "true" ? true : false;

            initdropdownMenu();
            retrieveStoredValues(GcmPreferences);

            if (SelectedDunningMainOptionIdx >= 0) {
                ((RadioButton) rg1.getChildAt(SelectedDunningMainOptionIdx)).setChecked(true);
                rg1.setBackgroundColor(Color.parseColor("#ffff9e1b"));
            } else {
                rg1.setBackgroundColor(Color.RED);
                SelectedDunningMainOptionIdx = -1;
                SelectedDunningspinnerIdx = -1;
            }

            loadDeliverySpinner(SelectedDunningMainOptionIdx);
            loadBuildingSpinner();

            if (DunningspinnerLoadedSuccessful) {
                if (SelectedDunningspinnerIdx >= 0) {
                    ddlReceiver.setSelection(SelectedDunningspinnerIdx);
                }
            }

            if (DunningBuildingLoadedSuccessful) {
                if (SelectedDunningBuildingIdx >= 0) {
                    ddlBuilding.setSelection(SelectedDunningBuildingIdx);
                }
            }

            if (!enableJobUpdates) {
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnSign);
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnViewImg);
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCamera);
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
            } else
            if (SelectedDunningMainOptionIdx >= 0 && SelectedDunningspinnerIdx >= 0 && SelectedDunningBuildingIdx >= 0) {
                if (SelectedDunningMainOptionIdx == 1 || SelectedDunningMainOptionIdx == 2) {
                    if (Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0)) {
                        if (rg1.getCheckedRadioButtonId() == R.id.rb2) {
                            if (SelectedDunningspinnerIdx == 1) { //letterbox, 2 photos
                                ctrlSettings.enableDisableCommonButtonWithStyles(true, btnViewImg);
                                ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningType_of_Dwelling.equals("") && imagePaths.size() >= 2 ? true : false, btnSubmit);
                            } else if (SelectedDunningspinnerIdx == 2) {//letterbox, 1 photo
                                ctrlSettings.enableDisableCommonButtonWithStyles(true, btnViewImg);
                                ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningType_of_Dwelling.equals("") ? true : false, btnSubmit);
                            }
                        } else {
                            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnViewImg);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningType_of_Dwelling.equals("") ? true : false, btnSubmit);
                        }
                    } else {
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnViewImg);
                    }

                    /*if (Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0)) {
                        ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnSubmit);
                    } else {
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                    }*/
                }
            } else {
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
            }

            txtRemarks.setText(remarks);

            hasSignature = false;

            Integer catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
            TotalJobsCountPerCatTypePerPostalB4Updates = DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal);
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDunningDeliveredStatusActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
    }

    @Override
    protected void onPause() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at setDunningDeliveredStatusActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    private void retrieveStoredValues(SharedPreferences gcmp) {
        SelectedDunningMainOptionIdxStr = objGlobalMethods.getGCMPrefs(gcmp, "SelectedDunningMainOptionIdxStr");
        SelectedDunningspinnerIdxStr = objGlobalMethods.getGCMPrefs(gcmp, "SelectedDunningspinnerIdxStr");
        SelectedDunningBuildingIdxStr = objGlobalMethods.getGCMPrefs(gcmp, "SelectedDunningBuildingIdxStr");
        DunningDelivery_Status = objGlobalMethods.getGCMPrefs(gcmp, "DunningDelivery_Status");
        DunningType_of_Dwelling = objGlobalMethods.getGCMPrefs(gcmp, "DunningType_of_Dwelling");
        CNNO = objGlobalMethods.getGCMPrefs(gcmp, "currJobInfoCN");
        addressWithUnit = objGlobalMethods.getGCMPrefs(gcmp, "addressWithUnit");

        if (SelectedDunningMainOptionIdxStr.equals("") || SelectedDunningMainOptionIdxStr.equals("-1")) {
            SelectedDunningMainOptionIdx = -1;
        } else {
            SelectedDunningMainOptionIdx = Integer.parseInt(SelectedDunningMainOptionIdxStr);
        }

        if (SelectedDunningspinnerIdxStr.equals("") || SelectedDunningspinnerIdxStr.equals("-1")) {
            SelectedDunningspinnerIdx = -1;
        } else {
            SelectedDunningspinnerIdx = Integer.parseInt(SelectedDunningspinnerIdxStr);
        }

        if (SelectedDunningBuildingIdxStr.equals("") || SelectedDunningBuildingIdxStr.equals("-1")) {
            SelectedDunningBuildingIdx = -1;
        } else {
            SelectedDunningBuildingIdx = Integer.parseInt(SelectedDunningBuildingIdxStr);
        }

        if (DunningDelivery_Status == null) {
            DunningDelivery_Status = "";
        }

        if (DunningType_of_Dwelling == null) {
            DunningType_of_Dwelling = "";
        }

        //lblJobNo.setText(CNNO);
        lblJobNo.setText(addressWithUnit);
        setTitle(CNNO);
    }

    public void BackToLoginPage() {
        isFromSetDunningOKP = false;
        Camera5Activity.mCurrentPhotoPath = null;
        CustomGalleryActivity.dataT = null;
        CustomGalleryActivity.imagePaths = null;
        dataT = null;
        imagePaths = null;
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        mBitmap = null;
        remarks = "";
        signatureString = "";
        SignatureNameActivity.requireSignature = false;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();

        String[] prefName = new String[]{"SelectedDunningMainOptionIdxStr", "SelectedDunningspinnerIdxStr", "SelectedDunningBuildingIdxStr", "DunningDelivery_Status", "DunningType_of_Dwelling",
                "currJobInfoCN", "addressWithUnit", objGlobalVariables.currJobInfoIDX, objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, objGlobalVariables.checkedItemsName,
                objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "isFromDashboardSub", "wifistate", "leftofc"};
        String[] prefvalue = new String[]{"", "", "", "", "", "", "", "", "-1", "", "", "", "", "", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");

        Intent parentIntent;
        parentIntent = new Intent(setDunningDeliveredStatusActivity.this, MainActivity.class);
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at setDunningDeliveredStatusActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at setDunningDeliveredStatusActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at setDunningDeliveredStatusActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Dialog showDialog(String msg) {
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
                mBitmap = null;
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
                //i = new Intent(setDunningDeliveredStatusActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(setDunningDeliveredStatusActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                MainActivity.uponLogin = 0;

                DrawerMainActivity.DashboardOpenFromPush = false;
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
        getMenuInflater().inflate(R.menu.menu_set_dunning_delivered_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent parentIntent;

        if (id == R.id.map) {
            //open map
            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()) {
                parentIntent = new Intent(setDunningDeliveredStatusActivity.this, MapsActivity.class);
                MainActivity.uponLogin = 0;
                startActivity(parentIntent);
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_unableopenmapnointernet));
            }
        }



        /*switch (item.getItemId()) {
            case R.id.map:
                //open map
                //cd = new ConnectionDetector(context);
                cd = new ConnectionDetector(null);
                if (cd.isConnectingToInternet()) {
                    parentIntent = new Intent(setDunningDeliveredStatusActivity.this, MapsActivity.class);
                    MainActivity.uponLogin = 0;
                    startActivity(parentIntent);
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_unableopenmapnointernet));
                }
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        Gson gson = new Gson();
        // Check which radio button was clicked

        if (view.getId() == R.id.rb1) {
            if (checked)
                SelectedDunningMainOptionIdx = 0;
            objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningMainOptionIdxStr", String.valueOf(SelectedDunningMainOptionIdx));
            tv2.setText(myApp.getAppContext().getString(R.string.dunning_docuRxBylbl));
            rg1.setBackgroundColor(Color.parseColor("#ffff9e1b"));
            loadDeliverySpinner(0);
            SignatureNameActivity.requireSignature = true;
            showDialog(myApp.getAppContext().getString(R.string.additionalInfoSign));
        } else if (view.getId() == R.id.rb2) {
            if (checked)
                SelectedDunningMainOptionIdx = 1;
            objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningMainOptionIdxStr", String.valueOf(SelectedDunningMainOptionIdx));
            tv2.setText(myApp.getAppContext().getString(R.string.dunning_Slottedlbl));
            rg1.setBackgroundColor(Color.parseColor("#ffff9e1b"));
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
            loadDeliverySpinner(1);
            SignatureNameActivity.requireSignature = false;
        } else if (view.getId() == R.id.rb3) {
            if (checked)
                SelectedDunningMainOptionIdx = 2;
            objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningMainOptionIdxStr", String.valueOf(SelectedDunningMainOptionIdx));
            tv2.setText(myApp.getAppContext().getString(R.string.dunning_notdeliveredlbl));
            rg1.setBackgroundColor(Color.parseColor("#ffff9e1b"));
            loadDeliverySpinner(2);
            SignatureNameActivity.requireSignature = false;
        }


        /*switch (view.getId()) {
            case R.id.rb1:
                if (checked)
                    SelectedDunningMainOptionIdx = 0;
                objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningMainOptionIdxStr", String.valueOf(SelectedDunningMainOptionIdx));
                tv2.setText(myApp.getAppContext().getString(R.string.dunning_docuRxBylbl));
                rg1.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                loadDeliverySpinner(0);
                SignatureNameActivity.requireSignature = true;
                showDialog(myApp.getAppContext().getString(R.string.additionalInfoSign));
                break;
            case R.id.rb2:
                if (checked)
                    SelectedDunningMainOptionIdx = 1;
                objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningMainOptionIdxStr", String.valueOf(SelectedDunningMainOptionIdx));
                tv2.setText(myApp.getAppContext().getString(R.string.dunning_Slottedlbl));
                rg1.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                loadDeliverySpinner(1);
                SignatureNameActivity.requireSignature = false;
                break;
            case R.id.rb3:
                if (checked)
                    SelectedDunningMainOptionIdx = 2;
                objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningMainOptionIdxStr", String.valueOf(SelectedDunningMainOptionIdx));
                tv2.setText(myApp.getAppContext().getString(R.string.dunning_notdeliveredlbl));
                rg1.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                loadDeliverySpinner(2);
                SignatureNameActivity.requireSignature = false;
                break;
        }*/
    }

    private void loadDeliverySpinner(Integer SelectedMainOptionIdx) {
        ArrayList<Spinner_PostData> SpinnerPostDataArr = new ArrayList<Spinner_PostData>();
        Spinner_PostData postdata;
        postdata = new Spinner_PostData(myApp.getAppContext().getString(R.string.spinnerSelect), "", "", "");
        SpinnerPostDataArr.add(postdata);
        String[] val = null;
        String[] valValue = null;
        try {
            if (SelectedMainOptionIdx == 0) {
                val = valRxBy;
                valValue = valRxByValue;
            } else if (SelectedMainOptionIdx == 1) {
                val = valSlotted;
                valValue = valSlottedValue;
            } else if (SelectedMainOptionIdx == 2) {
                val = valnotDelivered;
                valValue = valnotDeliveredValue;
            }

            if (SelectedMainOptionIdx >= 0) {
                for (int i = 0; i <= val.length - 1; i++) {
                    postdata = new Spinner_PostData(val[i], valValue[i], String.valueOf(i), "");
                    SpinnerPostDataArr.add(postdata);
                }
            } else {
                //do nothing
            }

            if (SpinnerPostDataArr.size() > 1) {
                ddlReceiver.setEnabled(true);
            } else {
                ddlReceiver.setEnabled(false);
            }

            Spinner_PostItemAdapter adapter = new Spinner_PostItemAdapter(this, SpinnerPostDataArr);

            ddlReceiver.setAdapter(adapter);

            ddlReceiver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        DunningType_of_Dwelling = objGlobalMethods.getGCMPrefs(GcmPreferences, "DunningType_of_Dwelling");
                        SelectedDunningspinnerIdx = position;
                        TextView lblStatusCodesDesc = (TextView) view.findViewById(R.id.lblStatusCodesDesc);
                        if (position == 0) {
                            lblStatusCodesDesc.setBackgroundColor(Color.RED);
                            lblStatusCodesDesc.setTextColor(Color.WHITE);
                            DunningDelivery_Status = "";
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "DunningDelivery_Status", DunningDelivery_Status);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningspinnerIdxStr", String.valueOf(SelectedDunningspinnerIdx));
                            ctrlSettings.enableDisableCommonButtonWithStyles(Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0) ? true : false, btnViewImg);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                        } else {
                            lblStatusCodesDesc.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                            lblStatusCodesDesc.setTextColor(Color.WHITE);
                            DunningDelivery_Status = ((TextView) view.findViewById(R.id.lblStatusCodesDescEN)).getText().toString();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "DunningDelivery_Status", DunningDelivery_Status);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningspinnerIdxStr", String.valueOf(SelectedDunningspinnerIdx));

                            if (rg1.getCheckedRadioButtonId() == R.id.rb2 || rg1.getCheckedRadioButtonId() == R.id.rb3) {
                                //slotted
                                if (Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0)) {
                                    if (rg1.getCheckedRadioButtonId() == R.id.rb2) {
                                        if (SelectedDunningspinnerIdx == 1) { //letterbox, 2 photos
                                            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnViewImg);
                                            ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningType_of_Dwelling.equals("") && imagePaths.size() >= 2 ? true : false, btnSubmit);
                                        } else if (SelectedDunningspinnerIdx == 2) {//letterbox, 1 photo
                                            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnViewImg);
                                            ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningType_of_Dwelling.equals("") ? true : false, btnSubmit);
                                        }
                                    } else {
                                        ctrlSettings.enableDisableCommonButtonWithStyles(true, btnViewImg);
                                        ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningType_of_Dwelling.equals("") ? true : false, btnSubmit);
                                    }
                                } else {
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnViewImg);
                                    if (rg1.getCheckedRadioButtonId() == R.id.rb2) {
                                        if (SelectedDunningspinnerIdx == 1) {
                                            //showDialog(myApp.getAppContext().getString(R.string.lbl_reqphoto_lb));
                                            String prompt_reqphoto_lb = objGlobalMethods.getGCMPrefs(GcmPreferences, "prompt_reqphoto_lb");
                                            if (prompt_reqphoto_lb.isEmpty() || prompt_reqphoto_lb.equals("") || prompt_reqphoto_lb == "") {
                                                objGlobalMethods.setGCMPrefs(GcmPreferences, "prompt_reqphoto_lb", "1");
                                                showDialog(myApp.getAppContext().getString(R.string.lbl_reqphoto_lb));
                                            }
                                        } else if (SelectedDunningspinnerIdx == 2) {
                                            //showDialog(myApp.getAppContext().getString(R.string.lbl_reqphoto_underdoor));
                                            String prompt_reqphoto_underdoor = objGlobalMethods.getGCMPrefs(GcmPreferences, "prompt_reqphoto_underdoor");
                                            if (prompt_reqphoto_underdoor.isEmpty() || prompt_reqphoto_underdoor.equals("") || prompt_reqphoto_underdoor == "") {
                                                objGlobalMethods.setGCMPrefs(GcmPreferences, "prompt_reqphoto_underdoor", "1");
                                                showDialog(myApp.getAppContext().getString(R.string.lbl_reqphoto_underdoor));
                                            }
                                        }
                                    }
                                }

                                /*if (Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0)) {
                                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnViewImg);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningType_of_Dwelling.equals("") ? true : false, btnSubmit);
                                } else {
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnViewImg);
                                }*/
                            } else {
                                ctrlSettings.enableDisableCommonButtonWithStyles(Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0) ? true : false, btnViewImg);
                                ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningType_of_Dwelling.equals("") && !signatureString.isEmpty() ? true : false, btnSubmit);
                            }
                        }
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at setDunningDeliveredStatusActivity ddlReceiver.setOnItemSelectedListener. btnSubmit is enabled. Exception: " + e.getMessage().toString(), "", "", "", "");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (Exception e) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            DunningspinnerLoadedSuccessful = false;
            objGlobalMethods.setGCMPrefs(GcmPreferences, "DunningDelivery_Status", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningspinnerIdxStr", "0");
            rg1.clearCheck();
            rg1.setBackgroundColor(Color.RED);
            tv2.setText(myApp.getAppContext().getString(R.string.dunning_docuRxBylbl));
            ddlReceiver.setEnabled(false);
            showDialog(myApp.getAppContext().getString(R.string.dunning_spinnerLoadingErrorAlert));
        }
    }

    private void loadBuildingSpinner() {
        ArrayList<Spinner_PostData> SpinnerPostDataArr = new ArrayList<Spinner_PostData>();
        Spinner_PostData postdata;
        postdata = new Spinner_PostData(myApp.getAppContext().getString(R.string.spinnerSelect), "", "", "");
        SpinnerPostDataArr.add(postdata);
        String[] val = null;
        String[] valValue = null;
        try {
            val = valBuilding;
            valValue = valBuildingValue;

            for (int i = 0; i <= val.length - 1; i++) {
                postdata = new Spinner_PostData(val[i], valValue[i], String.valueOf(i), "");
                SpinnerPostDataArr.add(postdata);
            }

            if (SpinnerPostDataArr.size() > 1) {
                ddlBuilding.setEnabled(true);
            } else {
                ddlBuilding.setEnabled(false);
            }

            Spinner_PostItemAdapter adapter = new Spinner_PostItemAdapter(this, SpinnerPostDataArr);

            ddlBuilding.setAdapter(adapter);

            ddlBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        DunningDelivery_Status = objGlobalMethods.getGCMPrefs(GcmPreferences, "DunningDelivery_Status");
                        SelectedDunningBuildingIdx = position;
                        TextView lblStatusCodesDesc = (TextView) view.findViewById(R.id.lblStatusCodesDesc);
                        if (position == 0) {
                            lblStatusCodesDesc.setBackgroundColor(Color.RED);
                            lblStatusCodesDesc.setTextColor(Color.WHITE);
                            DunningType_of_Dwelling = "";
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "DunningType_of_Dwelling", DunningType_of_Dwelling);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningBuildingIdxStr", String.valueOf(SelectedDunningBuildingIdx));
                            ctrlSettings.enableDisableCommonButtonWithStyles(Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0) ? true : false, btnViewImg);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                        } else {
                            lblStatusCodesDesc.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                            lblStatusCodesDesc.setTextColor(Color.WHITE);
                            DunningType_of_Dwelling = ((TextView) view.findViewById(R.id.lblStatusCodesDescEN)).getText().toString();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "DunningType_of_Dwelling", DunningType_of_Dwelling);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningBuildingIdxStr", String.valueOf(SelectedDunningBuildingIdx));

                            if (rg1.getCheckedRadioButtonId() == R.id.rb2 || rg1.getCheckedRadioButtonId() == R.id.rb3) {
                                if (Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0)) {
                                    if (rg1.getCheckedRadioButtonId() == R.id.rb2) {
                                        if (SelectedDunningspinnerIdx == 1) { //letterbox, 2 photos
                                            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnViewImg);
                                            ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningType_of_Dwelling.equals("") && imagePaths.size() >= 2 ? true : false, btnSubmit);
                                        } else if (SelectedDunningspinnerIdx == 2) {//letterbox, 1 photo
                                            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnViewImg);
                                            ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningType_of_Dwelling.equals("") ? true : false, btnSubmit);
                                        }
                                    } else {
                                        ctrlSettings.enableDisableCommonButtonWithStyles(true, btnViewImg);
                                        ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningType_of_Dwelling.equals("") ? true : false, btnSubmit);
                                    }
                                } else {
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnViewImg);
                                }

                                /*if (Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0)) {
                                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnViewImg);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningDelivery_Status.equals("") ? true : false, btnSubmit);
                                } else {
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnSubmit);
                                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnViewImg);
                                }*/
                            } else {
                                ctrlSettings.enableDisableCommonButtonWithStyles(Camera5Activity.mCurrentPhotoPath != null || (imagePaths != null && imagePaths.size() > 0) ? true : false, btnViewImg);
                                ctrlSettings.enableDisableSubmitButtonWithStyles(!DunningDelivery_Status.equals("") && !signatureString.isEmpty() ? true : false, btnSubmit);
                            }
                        }
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at setDunningDeliveredStatusActivity ddlReceiver.setOnItemSelectedListener. btnSubmit is enabled. Exception: " + e.getMessage().toString(), "", "", "", "");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (Exception e) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            DunningBuildingLoadedSuccessful = false;
            objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningBuildingIdxStr", "0");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backstepSQLite();
    }

    private void backstepSQLite() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        isFromSetDunningOKP = false;
        Camera5Activity.mCurrentPhotoPath = null;
        CustomGalleryActivity.dataT = null;
        CustomGalleryActivity.imagePaths = null;
        dataT = null;
        imagePaths = null;
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        if (mBitmap != null) {

            mBitmap.recycle();
            mBitmap = null;
        }
        mCurrentPhotoPath = null;

        remarks = "";
        signatureString = "";
        SignatureNameActivity.requireSignature = false;

        String[] prefName = new String[]{"SelectedDunningMainOptionIdxStr", "SelectedDunningspinnerIdxStr", "SelectedDunningBuildingIdxStr", "DunningDelivery_Status", "DunningType_of_Dwelling",
                objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "part2puHasCOD", "currJobInfoCN", "addressWithUnit",
                objGlobalVariables.currJobInfoIDX};
        String[] prefvalue = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");

        int CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
        String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
        String fromSubMenu = objGlobalMethods.getGCMPrefs(GcmPreferences, "fromSubMenu");
        /*int TotalJobsCountPerCatTypePerPostal = !objGlobalMethods.IsDefaultVRPpage() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                        : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);*/
        int TotalJobsCountPerCatTypePerPostal = !isVrpView() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                        : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);
        Intent parentIntent = null;
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();

        if (!GcmPreferences.getString("isFromMainCOD", "").isEmpty()) {
            String[] prefName0 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD", "isFromMessaging", "isFromDashboardSub"};
            String[] prefvalue0 = new String[]{"", "", "", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName0, prefvalue0);

            //objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMessaging", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
            parentIntent = new Intent(this, MainCODActivity.class);
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        } else if (!GcmPreferences.getString("isFromMessaging", "").isEmpty()) {
            String[] prefName1 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD", "isFromMessaging", "isFromDashboardSub"};
            String[] prefvalue1 = new String[]{"", "", "", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName1, prefvalue1);

            //objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMessaging", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
            parentIntent = new Intent(this, Messaging.class);
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        } else if (!GcmPreferences.getString("isFromDashboardSub", "").isEmpty()) {
            if (TotalJobsCountPerCatTypePerPostal > 1) {
                parentIntent = new Intent(this, JobsListGroupActivity.class);
            } else if (TotalJobsCountPerCatTypePerPostal == 0 || TotalJobsCountPerCatTypePerPostal == 1) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                MainActivity.uponLogin = 0;
            }
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        } else if (TotalJobsCountPerCatTypePerPostal == 0) {
            String[] prefName2 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD"};
            String[] prefvalue2 = new String[]{"", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName2, prefvalue2);

            int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
            if (TotalJobsCountPerCatType > 1) {
                parentIntent = new Intent(this, AddressListNewActivity.class);
            } else if (TotalJobsCountPerCatType <= 1) {
                //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            }
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        } else if (TotalJobsCountPerCatTypePerPostal == 1 && TotalJobsCountPerCatTypePerPostalB4Updates > 1) {
            String[] prefName3 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD"};
            String[] prefvalue3 = new String[]{"", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName3, prefvalue3);

            //objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", "");
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
            parentIntent = new Intent(this, JobsListGroupActivity.class);
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        } else if (TotalJobsCountPerCatTypePerPostal == 1 && TotalJobsCountPerCatTypePerPostalB4Updates <= 1) {
            String[] prefName4 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD"};
            String[] prefvalue4 = new String[]{"", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName4, prefvalue4);

            int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
            if (TotalJobsCountPerCatType > 1) {
                parentIntent = new Intent(this, AddressListNewActivity.class);
            } else if (TotalJobsCountPerCatType <= 1) {
                //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            }
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        } else if (TotalJobsCountPerCatTypePerPostal > 1) {
            parentIntent = new Intent(this, JobsListGroupActivity.class);
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        }

    }

    public void btnViewImgClick(View view) {
        isFromSetDunningOKP = true;
        Camera5Activity.mCurrentPhotoPath = null;
        bitmap = null;
        remarks = txtRemarks.getText().toString();
        Intent i = new Intent(setDunningDeliveredStatusActivity.this, CustomGalleryActivity.class);
        startActivity(i);
    }

    public void btnCameraClick(View view) {
        isFromSetDunningOKP = true;
        Camera5Activity.mCurrentPhotoPath = null;
        bitmap = null;
        remarks = txtRemarks.getText().toString();
        Intent i = new Intent(setDunningDeliveredStatusActivity.this, Camera5Activity.class);
        CheckListActivity.openCamera = null;
        CheckListActivity.itemIndex = null;
        try {

            if (imagePaths != null && imagePaths.size() > 0) {
                if (imagePaths.size() >= 6) {
                    objGlobalMethods.writeToFile("setDunningDeliveredStatusActivity btnCameraClick blocked. 6 photo already. ", "", "", "", "");
                    showDialog(myApp.getAppContext().getString(R.string.cannotlaunchcam));
                } else {
                    objGlobalMethods.writeToFile("setDunningDeliveredStatusActivity btnCameraClick. ", "", "", "", "");
                    startActivity(i);
                }
            } else if (imagePaths != null && imagePaths.size() == 0) {
                objGlobalMethods.writeToFile("setDunningDeliveredStatusActivity btnCameraClick. ", "", "", "", "");
                startActivity(i);
            } else if (imagePaths == null) {
                objGlobalMethods.writeToFile("setDunningDeliveredStatusActivity btnCameraClick. ", "", "", "", "");
                startActivity(i);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at setDunningDeliveredStatusActivity btnCameraClick. " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnCancelClicked(View view) {
        onBackPressed();
    }

    public void btnSubmitClicked(View view) {
        //submit();
        submitSQLite();
    }

    private void submitSQLite() {
        final SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        retrieveStoredValues(gcmp);
        final Integer aSelectedDunningMainOptionIdx = SelectedDunningMainOptionIdx;
        final Integer aSelectedDunningspinnerIdx = SelectedDunningspinnerIdx;
        final Integer aSelectedDunningBuildingIdx = SelectedDunningBuildingIdx;
        final String aDunningDelivery_Status = DunningDelivery_Status;
        final String aDunningType_of_Dwelling = DunningType_of_Dwelling;

        if (!aDunningDelivery_Status.equals("") && !aDunningType_of_Dwelling.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_dunningstatus));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                    try {
                        String RegID = objGlobalMethods.getRegistrationIdOnly();
                        //ArrayList<String> convertedBitmapToBase64List = null;
                        String DunningOKPNH_Status = objGlobalVariables.returnDunningOKPNH_Status(aDunningDelivery_Status);
                        objDunningSubmit dunningSubmit;
                        objADunningStatus dunningStatus;

                        String JobsIDXstr = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoIDX);
                        long JobsIDX = Long.parseLong(JobsIDXstr);
                        Date dt = new Date();
                        Long l = dt.getTime();
                        String timestampstr = "/Date(" + l + ")/";
                        String remarks = txtRemarks.getText().toString();
                        //String checklistvalue = objGlobalMethods.getGCMPrefs(gcmp, "checklistvalue");
                        String checklistvalue = objGlobalMethods.getChecklistRelatedSP_KeyValue("checklistvalue");

                        if (checklistvalue.length() > 0) {
                            remarks += "\n\n" + checklistvalue;
                        }
                        String pr_latemsg0 = objGlobalMethods.getGCMPrefs(GcmPreferences, "pr_latemsg0");
                        if (!objGlobalMethods.IsEmptyString(pr_latemsg0)) {
                            remarks += "\n" + pr_latemsg0;
                        }

                        dunningStatus = new objADunningStatus(JobsIDX, timestampstr, objGlobalVariables.returnDunningDelivery_Status(aDunningDelivery_Status),
                                objGlobalVariables.returnDunningDwelling(aDunningType_of_Dwelling), remarks);

                        Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                        int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                        objGlobalMethods.writeToFile("Click to set Dunning job status.",
                                "JobsIDX: " + String.valueOf(JobsIDX), "aDunningDelivery_Status: " + aDunningDelivery_Status + ", aDunningType_of_Dwelling: " + aDunningType_of_Dwelling, "", "");

                        JobsHandler.addUpdateDunningStatus(UpdateJobInfoPreferences, RegID, dunningStatus, aDunningDelivery_Status,
                                aDunningType_of_Dwelling, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);

                        Gson gson = new Gson();
                        objJobInfo currJobInfo = gson.fromJson(gcmp.getString("hfJobInfoJsonStr", ""), objJobInfo.class);

                        if (rg1.getCheckedRadioButtonId() == R.id.rb1 || rg1.getCheckedRadioButtonId() == R.id.rb2) {
                            ////set from dip to okp
                            //add jobinfo to okp pref
                            DAO_Jobs.updateJobsCatTypeID(3, JobsIDX);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmp, 2);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmp, 3);
                        } else {
                            //add jobinfo to FD pref
                            DAO_Jobs.updateJobsCatTypeID(5, JobsIDX);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmp, 2);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmp, 5);
                        }

                        //convertedBitmapToBase64List = RTNconvertedBitmapToBase64List();
                        final ArrayList<objConvertedBitmapToBase64> newConvertedBitmapToBase64List = RTNobjConvertedBitmapToBase64List();
                        Camera5Activity.mCurrentPhotoPath = null;
                        imagePaths = null;
                        dataT = null;

                        if (newConvertedBitmapToBase64List != null && newConvertedBitmapToBase64List.size() > 0) {
                            for (int x = 0; x <= newConvertedBitmapToBase64List.size() - 1; x++) {
                                JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, JobsIDX,
                                        DunningOKPNH_Status + objGlobalVariables.returnDunningDelivery_StatusString(aDunningDelivery_Status),
                                        newConvertedBitmapToBase64List.get(x).convertedBitmapToBase64, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                        newConvertedBitmapToBase64List.get(x).FilePath);
                            }
                        }

                        mCurrentPhotoPath = "";
                        mBitmap = null;

                        if (signatureString != null && !signatureString.isEmpty()) {
                            JobsHandler.addUpdateJobSignatureQueALPrefs(UpdateJobInfoPreferences,
                                    RegID, JobsIDX, signatureString, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                        }

                        Context ctx = myApp.getAppContext();
                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                        objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, true);

                        resetAddressListActivityhfJobsInfoArrJsonStrSQLite();
                    } catch (Exception e) {
                        String s = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception caught at setDunningDeliveredStatusActivity btnSubmitClick. " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
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
        } else {
            showDialog(myApp.getAppContext().getString(R.string.dunning_submitAlert));
        }
    }

    private void resetAddressListActivityhfJobsInfoArrJsonStrSQLite() {
        Camera5Activity.mCurrentPhotoPath = null;
        CustomGalleryActivity.dataT = null;
        CustomGalleryActivity.imagePaths = null;
        bitmap = null;
        imagePaths = null;
        dataT = null;
        mBitmap = null;
        mCurrentPhotoPath = null;
        remarks = "";
        signatureString = "";
        SignatureNameActivity.requireSignature = false;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();

        String[] prefName = new String[]{"SelectedDunningMainOptionIdxStr", "SelectedDunningspinnerIdxStr", "SelectedDunningBuildingIdxStr", "DunningDelivery_Status", "DunningType_of_Dwelling", "currJobInfoCN",
                "addressWithUnit", objGlobalVariables.currJobInfoIDX};
        String[] prefvalue = new String[]{"", "", "", "", "", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        //objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningMainOptionIdxStr", "");
        //objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningspinnerIdxStr", "");
        //objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedDunningBuildingIdxStr", "");
        //objGlobalMethods.setGCMPrefs(GcmPreferences, "DunningDelivery_Status", "");
        //objGlobalMethods.setGCMPrefs(GcmPreferences, "DunningType_of_Dwelling", "");
        //objGlobalMethods.setGCMPrefs(GcmPreferences, "currJobInfoCN", "");
        //objGlobalMethods.setGCMPrefs(GcmPreferences, "addressWithUnit", "");
        //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX, "");
        objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, "");
        objGlobalMethods.resetJobsListGroupScrollIndexNpostion(GcmPreferences);
        Integer CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        Intent parentIntent = null;
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        try {
            int TotalJobsCountPerCatTypePerPostal = 0;
            String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
            if (Postal != "") {
                String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
                /*TotalJobsCountPerCatTypePerPostal = !objGlobalMethods.IsDefaultVRPpage() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                        !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                                : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);*/
                TotalJobsCountPerCatTypePerPostal = !isVrpView() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                        !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                                : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);

                if (TotalJobsCountPerCatTypePerPostal > 0) {
                    //back to JobsListGroupActivity
                    parentIntent = new Intent(setDunningDeliveredStatusActivity.this, JobsListGroupActivity.class);
                    com.xdel.luke.MainActivity.uponLogin = 0;
                    startActivity(parentIntent);
                    setDunningDeliveredStatusActivity.this.finish();
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
                            setDunningDeliveredStatusActivity.this.finish();
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
                            setDunningDeliveredStatusActivity.this.finish();
                        }
                    } else {
                        //back to AddressListNewActivity or DrawerMainActivity
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                        MainActivity.uponLogin = 0;
                        int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
                        if (TotalJobsCountPerCatType == 0) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                            //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                            parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                            startActivity(parentIntent);
                            setDunningDeliveredStatusActivity.this.finish();
                        } else {
                            objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                            parentIntent = new Intent(this, AddressListNewActivity.class);
                            startActivity(parentIntent);
                            setDunningDeliveredStatusActivity.this.finish();
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
                    setDunningDeliveredStatusActivity.this.finish();
                } else {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                    MainActivity.uponLogin = 0;
                    int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
                    if (TotalJobsCountPerCatType == 0) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                        //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                        parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                        startActivity(parentIntent);
                        setDunningDeliveredStatusActivity.this.finish();
                    } else {
                        objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                        objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                        parentIntent = new Intent(this, AddressListNewActivity.class);
                        startActivity(parentIntent);
                        setDunningDeliveredStatusActivity.this.finish();
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setDunningDeliveredStatusActivity resetAddressListActivityhfJobsInfoArrJsonStrSQLite. Exception: " + e.getMessage().toString(), "", "", "", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
            com.xdel.luke.MainActivity.uponLogin = 0;
            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
            //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
            parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            startActivity(parentIntent);
            setDunningDeliveredStatusActivity.this.finish();
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
            objGlobalMethods.writeToFile("Exception caught at setDunningDeliveredStatusActivity RTNobjConvertedBitmapToBase64List. Exception: " + e.toString(), "", "", "", "");
            convertedBitmapToBase64List = null;
        } finally {
            return convertedBitmapToBase64List;
        }
    }

    private ArrayList<String> RTNconvertedBitmapToBase64List() {
        ArrayList<String> convertedBitmapToBase64List = null;
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
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setDunningDeliveredStatusActivity RTNconvertedBitmapToBase64List. Exception: " + e.toString(), "", "", "", "");
            convertedBitmapToBase64List = null;
        } finally {
            return convertedBitmapToBase64List;
        }
    }

    public void clearSignature(View view) {
        mSignature.clear();
    }

    public void btnSign_Click(View view) {
        try {
            remarks = txtRemarks.getText().toString();
            SignatureNameActivity.rtnPageName = "setDunningDeliveredStatusActivity";
            SignatureNameActivity.ReceiverName = null;
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "reqName", "");
            Intent intent = new Intent(this, SignatureNameActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            showDialog(myApp.getAppContext().getString(R.string.unabletoopensignature));
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
                /*mFileOutStream.flush();
                mFileOutStream.close();
                String url = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);*/

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
            Date dt = new Date();
            /*Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());*/

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
                ;
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

                //galleryAddPic();


            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity signature class save function. Exception: " + e.toString(), "", "", "", "");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at setDunningDeliveredStatusActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at setDunningDeliveredStatusActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
