package com.xdel.luke.HRM_Leave;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xdel.luke.Camera5Activity;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.CustomGallery;
import com.xdel.luke.CustomGalleryActivity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.HRM.Spinner_PostItemAdapterLeaveType;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.setDeliveredMassStatusActivity;
import com.xdel.luke.setDeliveredStatusActivity;
import com.xdel.luke.setFailStatusActivity;
import com.xdel.luke.templogger.temploggerclass;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LeaveApplicationActivity extends AppCompatActivity {

    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String langType = "en";
    String[] UsernamePwd = new String[4];
    String username;
    String password;
    String useridx;
    String ishomesuser;
    ConnectionDetector cd;
    Bitmap bitmap = null;
    private objLeaveType[] objLeaveTypelist = null;
    Spinner ddlLeaveType, ddlDateFromAMPM, ddlDateToAMPM;
    TextView lblEntitedVal, lblBalanceToYrVal, lblBalanceVal, lblTotalVal, lblBalanceBroughtOverVal;
    TextView lblSelectedLeaveTypeIDX, lblSelectedLeaveTypeIDXval, lblSelectedFromAMPM, lblSelectedToAMPM, lblUserType, lblUserDriverIDX, txtDateFrom, txtDateTo, lblSelectedLeaveFlag;
    EditText txtRemarks;
    Button btnLeaveBrowse, btnLeaveCamera, btnLeaveApplyCancel, btnLeaveApply;
    SimpleDateFormat aformat = new SimpleDateFormat("dd/MM/yyyy");
    DatePickerDialog picker;
    private ProgressDialog pd;
    public static ArrayList<String> imagePaths;
    public static ArrayList<CustomGallery> dataT;
    public static boolean isFromLeaveApplication = false;
    ScreenShotContentObserver screenShotContentObserver;

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(LeaveApplicationActivity.this, MainActivity.class);
        startActivity(parentIntent);
        finish();
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

        setContentView(R.layout.activity_leave_application);

        ddlLeaveType = (Spinner) findViewById(R.id.ddlLeaveType);
        ddlDateFromAMPM = (Spinner) findViewById(R.id.ddlDateFromAMPM);
        ddlDateToAMPM = (Spinner) findViewById(R.id.ddlDateToAMPM);
        lblSelectedLeaveFlag = findViewById(R.id.lblSelectedLeaveFlag);
        lblSelectedLeaveTypeIDX = (TextView) findViewById(R.id.lblSelectedLeaveTypeIDX);
        lblSelectedLeaveTypeIDXval = (TextView) findViewById(R.id.lblSelectedLeaveTypeIDXval);
        lblSelectedFromAMPM = (TextView) findViewById(R.id.lblSelectedFromAMPM);
        lblSelectedToAMPM = (TextView) findViewById(R.id.lblSelectedToAMPM);
        lblUserType = (TextView) findViewById(R.id.lblUserType);
        lblUserDriverIDX = (TextView) findViewById(R.id.lblUserDriverIDX);
        txtDateFrom = (TextView) findViewById(R.id.txtDateFrom);
        txtDateTo = (TextView) findViewById(R.id.txtDateTo);
        txtRemarks = (EditText) findViewById(R.id.txtRemarks);

        lblEntitedVal = (TextView) findViewById(R.id.lblEntitedVal);
        lblBalanceToYrVal = findViewById(R.id.lblBalanceToYrVal);
        lblBalanceVal = (TextView) findViewById(R.id.lblBalanceVal);
        lblTotalVal = (TextView) findViewById(R.id.lblTotalVal);
        lblBalanceBroughtOverVal = (TextView) findViewById(R.id.lblBalanceBroughtOverVal);

        btnLeaveBrowse = (Button) findViewById(R.id.btnLeaveBrowse);
        btnLeaveCamera = (Button) findViewById(R.id.btnLeaveCamera);
        btnLeaveApplyCancel = (Button) findViewById(R.id.btnLeaveApplyCancel);
        btnLeaveApply = (Button) findViewById(R.id.btnLeaveApply);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

    }

    @Override
    protected void onResume() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;
                Context ctx = myApp.getAppContext();
                setTitle(ctx.getString(R.string.title_activity_apply_leave));

                JobsHandler.checkForceLogOut();

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("LeaveApplicationActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];

                if (username.equals("") && password.equals("")) {
                    BackToLoginPage2();
                }

                lblUserType.setText(!Boolean.parseBoolean(ishomesuser) ? String.valueOf(objRecordType.RecordType.etGC.ordinal()) : String.valueOf(objRecordType.RecordType.etStaff.ordinal()));
                lblUserDriverIDX.setText(useridx);

                //cd = new ConnectionDetector(ctx);
                cd = new ConnectionDetector(null);
                // Check if Internet present
                if (!cd.isConnectingToInternet()) {
                    //// Internet Connection is not present
                    Toast.makeText(ctx, ctx.getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                } else {
                    initPage();
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LeaveActivity onResume. Exception: " + e.getMessage(), "", "", "", "");
            }
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
    }

    @Override
    protected void onPause() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                String[] prefName = new String[]{"Leave_RecordType", "Leave_GC_UserIDX",
                        "Leave_LeaveFrom", "Leave_FromAMPM",
                        "Leave_LeaveTo", "Leave_ToAMPM",
                        "Leave_Units", "Leave_UOM",
                        "Leave_LeaveTypeIDX", "Leave_SelectedLeaveTypeIDXval", "Leave_Remarks"};
                String[] prefvalue = new String[]{lblUserType.getText().toString(), lblUserDriverIDX.getText().toString(),
                        txtDateFrom.getText().toString(), lblSelectedFromAMPM.getText().toString(),
                        txtDateTo.getText().toString(), lblSelectedToAMPM.getText().toString(),
                        "0", "0",
                        lblSelectedLeaveTypeIDX.getText().toString(), lblSelectedLeaveTypeIDXval.getText().toString(), txtRemarks.getText().toString()};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                try {
                    if (screenShotContentObserver != null)
                        getContentResolver().unregisterContentObserver(screenShotContentObserver);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity onPause unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity onPause. Exception: " + e.getMessage(), "", "", "", "");
            }
        }
        super.onPause();
    }

    private long rtnDriverUserIDX_LongVal(String idx) {
        long val = 0;
        try {
            val = idx.equals("") ? 0 : Long.parseLong(idx);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity rtnDriverUserIDX_LongVal. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return val;
        }
    }

    private void showErrorAlert(String errMsg, final boolean leavetypeErr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_noLeaveTypedata));
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
                if (leavetypeErr) {
                    onBackPressed();
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        Intent parentIntent = new Intent(this, LeaveActivity.class);
        MainActivity.uponLogin = 0;
        isFromLeaveApplication = false;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    private void initPage() {
        //boolean LeaveTypeDDLloaded = false;
        boolean LeaveTypeDDLloaded = false;
        try {
            LeaveTypeDDLloaded = loadLeaveTypeDDL();
            if (!LeaveTypeDDLloaded) {
                showErrorAlert("", true);
            } else {
                showTodayDate();
                loadAMPMDDL(ddlDateFromAMPM, ddlDateToAMPM);
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                String RecordType = objGlobalMethods.getGCMPrefs(GcmPreferences, "Leave_RecordType");
                String GC_UserIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, "Leave_GC_UserIDX");
                String LeaveFrom = objGlobalMethods.getGCMPrefs(GcmPreferences, "Leave_LeaveFrom");
                String FromAMPM = objGlobalMethods.getGCMPrefs(GcmPreferences, "Leave_FromAMPM");
                String LeaveTo = objGlobalMethods.getGCMPrefs(GcmPreferences, "Leave_LeaveTo");
                String ToAMPM = objGlobalMethods.getGCMPrefs(GcmPreferences, "Leave_ToAMPM");
                String Units = objGlobalMethods.getGCMPrefs(GcmPreferences, "Leave_Units");
                String UOM = objGlobalMethods.getGCMPrefs(GcmPreferences, "Leave_UOM");
                String LeaveTypeIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, "Leave_LeaveTypeIDX");
                String SelectedLeaveTypeIDXval = objGlobalMethods.getGCMPrefs(GcmPreferences, "Leave_SelectedLeaveTypeIDXval");
                String Remarks = objGlobalMethods.getGCMPrefs(GcmPreferences, "Leave_Remarks");

                lblUserType.setText(RecordType);
                lblUserDriverIDX.setText(GC_UserIDX);
                if (!LeaveFrom.equals("")) {
                    txtDateFrom.setText(LeaveFrom);
                }
                ddlDateFromAMPM.setSelection((FromAMPM.equals("") ? 0 : Integer.parseInt(FromAMPM)));
                if (!LeaveTo.equals("")) {
                    txtDateTo.setText(LeaveTo);
                }
                ddlDateToAMPM.setSelection(ToAMPM.equals("") ? 0 : Integer.parseInt(ToAMPM));
                lblSelectedLeaveTypeIDX.setText(LeaveTypeIDX.equals("") ? "0" : LeaveTypeIDX);
                lblSelectedLeaveTypeIDXval.setText(SelectedLeaveTypeIDXval.equals("") ? "0" : SelectedLeaveTypeIDXval);
                ddlLeaveType.setSelection(LeaveTypeIDX.equals("") ? 0 : Integer.parseInt(LeaveTypeIDX));
                txtRemarks.setText(Remarks);

                txtDateFrom.setOnClickListener(null);
                txtDateTo.setOnClickListener(null);
                txtDateFrom.setOnClickListener(txtDateOnClick());
                txtDateTo.setOnClickListener(txtDateOnClick());
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LeaveActivity initPage. Exception: " + e.getMessage(), "", "", "", "");
            onBackPressed();
        }
    }

    private boolean loadLeaveTypeDDL() {
        boolean asd = false;
        objLeaveType type = null;
        ArrayList<objLeaveType> SpinnerPostDataArr = new ArrayList<>();
        Gson gson = new Gson();
        Context ctx;
        try {
            ctx = myApp.getAppContext();
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            String LeaveTypesJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LeaveTypesJsonStr");
            if (!LeaveTypesJsonStr.equals("") && !LeaveTypesJsonStr.equals("[]")) {
                objLeaveTypelist = gson.fromJson(LeaveTypesJsonStr, objLeaveType[].class);
                if (objLeaveTypelist != null && objLeaveTypelist.length > 0) {
                    //load leavetype ddl
                    type = new objLeaveType(0, ctx.getString(R.string.spinnerSelect), ctx.getString(R.string.spinnerSelect), false, false, false, 0, 0, 0, 0, 0, 0);
                    SpinnerPostDataArr.add(type);
                    for (int i = 0; i <= objLeaveTypelist.length - 1; i++) {
                        type = objLeaveTypelist[i];
                        if (langType.equals("zh")) {
                            type.setDescription(objLeaveTypelist[i].Description_C == null || objLeaveTypelist[i].Description_C.equals("") ? objLeaveTypelist[i].Description : objLeaveTypelist[i].Description_C);
                        }
                        if (((objLeaveTypelist[i].Flag & 1) != 1) && ((objLeaveTypelist[i].Flag & 4) != 4)) {
                            SpinnerPostDataArr.add(type);
                        }
                    }

                    if (SpinnerPostDataArr != null && SpinnerPostDataArr.size() > 0) {
                        Spinner_PostItemAdapterLeaveType adapter = new Spinner_PostItemAdapterLeaveType(this, SpinnerPostDataArr);
                        ddlLeaveType.setAdapter(adapter);
                        asd = true;
                        ddlLeaveType.setOnItemSelectedListener(null);
                        ddlLeaveType.setOnItemSelectedListener(rtnddlLeaveTypeOnItemSelectedListener());
                    }
                }
            }
        } catch (Exception e) {
            asd = false;
            objGlobalMethods.writeToFile("Exception at LeaveActivity loadLeaveTypeDDL. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    private AdapterView.OnItemSelectedListener rtnddlLeaveTypeOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    TextView lblDescription = (TextView) view.findViewById(R.id.lblDescription);
                    TextView lblIDX = (TextView) view.findViewById(R.id.lblIDX);
                    TextView lblBalanceToDate = view.findViewById(R.id.lblBalanceToDate);
                    TextView lblBalanceBroughtOver = view.findViewById(R.id.lblBalanceBroughtOver);
                    TextView lblBalance = (TextView) view.findViewById(R.id.lblBalance);
                    TextView lblEntitled = (TextView) view.findViewById(R.id.lblEntitled);
                    TextView lblFlag = (TextView) view.findViewById(R.id.lblFlag);
                    TextView lblCanExceed = (TextView) view.findViewById(R.id.lblCanExceed);

                    String balanceStr = lblBalanceToDate.getText().toString();
                    String FlagStr = lblFlag.getText().toString();
                    double balance;
                    String canExceedStr = lblCanExceed.getText().toString();
                    boolean canExceed = canExceedStr.equals("") || canExceedStr.equals("f") ? false : true;
                    int FLAG;

                    try {
                        balance = balanceStr.equals("") ? 0 : Double.parseDouble(balanceStr);
                    } catch (Exception e) {
                        balance = 0;
                        objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity rtnddlLeaveTypeOnItemSelectedListener converting balanceStr to DOUBLE. Exception: " + e.getMessage(), "", "", "", "");
                    }

                    try {
                        FLAG = FlagStr.equals("") ? 0 : Integer.parseInt(FlagStr);
                    } catch (Exception e) {
                        FLAG = 0;
                        objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity rtnddlLeaveTypeOnItemSelectedListener FlagStr balanceStr to INTEGER. Exception: " + e.getMessage(), "", "", "", "");
                    }
                    ////flag 1 == cannot apply themselves, 2 require image, 4 hidden, 8 sg only

                    GcmPreferences = objGlobalMethods.getGcmPreferences();

                    if (position == 0) {
                        lblDescription.setBackgroundColor(Color.RED);
                        lblDescription.setTextColor(Color.WHITE);
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnLeaveBrowse);
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnLeaveCamera);
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnLeaveApply);
                        lblSelectedLeaveFlag.setText("0");
                        lblSelectedLeaveTypeIDX.setText("0");
                        lblSelectedLeaveTypeIDXval.setText("0");
                        lblEntitedVal.setText("0");
                        lblBalanceToYrVal.setText("0");
                        lblBalanceVal.setText("0");
                        lblTotalVal.setText("0");
                        lblBalanceBroughtOverVal.setText("0");
                    } else {
                        lblDescription.setBackgroundColor(Color.parseColor("#ffff9e1b"));
                        lblDescription.setTextColor(Color.WHITE);
                        lblSelectedLeaveFlag.setText(!objGlobalMethods.IsEmptyString(FlagStr) ? FlagStr : "0");
                        lblSelectedLeaveTypeIDX.setText(String.valueOf(position));
                        lblSelectedLeaveTypeIDXval.setText(String.valueOf(lblIDX.getText().toString()));
                        lblEntitedVal.setText(lblEntitled.getText().toString());
                        lblBalanceToYrVal.setText(lblBalance.getText().toString());
                        lblBalanceVal.setText(lblBalanceToDate.getText().toString());
                        lblBalanceBroughtOverVal.setText(lblBalanceBroughtOver.getText().toString());

                        btnLeaveBrowse.setVisibility(((FLAG & 2) == 2) ? View.VISIBLE : View.GONE);
                        btnLeaveCamera.setVisibility(((FLAG & 2) == 2) ? View.VISIBLE : View.GONE);

                        if (balance <= 0 && !canExceed) {
                            lblBalanceVal.setTextColor(Color.RED);
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnLeaveBrowse);
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnLeaveCamera);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnLeaveApply);
                        } else if ((balance <= 0 && canExceed) || (balance > 0)) {
                            lblBalanceVal.setTextColor(Color.BLACK);
                            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnLeaveBrowse);
                            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnLeaveCamera);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnLeaveApply);
                        }
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity rtnddlLeaveTypeOnItemSelectedListener. Exception: " + e.getMessage(), "", "", "", "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    private void loadAMPMDDL(Spinner sp1, Spinner sp2) {
        AMPM_PostItem am, pm;
        ArrayList<AMPM_PostItem> list = new ArrayList<>();
        try {
            am = new AMPM_PostItem("AM", String.valueOf(objAMPM.AMPM.apAM.ordinal()));
            pm = new AMPM_PostItem("PM", String.valueOf(objAMPM.AMPM.apPM.ordinal()));
            list.add(am);
            list.add(pm);
            AMPM_PostItemAdapter adapterFrom = new AMPM_PostItemAdapter(this, list);
            AMPM_PostItemAdapter adapterTo = new AMPM_PostItemAdapter(this, list);
            ddlDateFromAMPM.setAdapter(adapterFrom);
            ddlDateToAMPM.setAdapter(adapterTo);

            ddlDateFromAMPM.setOnItemSelectedListener(null);
            ddlDateToAMPM.setOnItemSelectedListener(null);
            ddlDateFromAMPM.setOnItemSelectedListener(rtnddlFromAMPMOnItemSelectedListener());
            ddlDateToAMPM.setOnItemSelectedListener(rtnddlToAMPMOnItemSelectedListener());

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LeaveActivity loadAMPMDDL. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private AdapterView.OnItemSelectedListener rtnddlFromAMPMOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    TextView lblStatusCodesIDX = (TextView) findViewById(R.id.lblStatusCodesIDX);
                    lblSelectedFromAMPM.setText(String.valueOf(position));
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity rtnddlFromAMPMOnItemSelectedListener. Exception: " + e.getMessage(), "", "", "", "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    private AdapterView.OnItemSelectedListener rtnddlToAMPMOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    TextView lblStatusCodesIDX = (TextView) findViewById(R.id.lblStatusCodesIDX);
                    lblSelectedToAMPM.setText(String.valueOf(position));
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity rtnddlToAMPMOnItemSelectedListener. Exception: " + e.getMessage(), "", "", "", "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    public void btnAttImgClick(View view) {
        isFromLeaveApplication = true;
        Camera5Activity.mCurrentPhotoPath = null;
        Intent i = new Intent(LeaveApplicationActivity.this, CustomGalleryActivity.class);
        startActivity(i);
    }

    public void btnCameraClick(View view) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();

        isFromLeaveApplication = true;
        Camera5Activity.mCurrentPhotoPath = null;
        setFailStatusActivity.isFromSetFailSatus = false;
        setDeliveredMassStatusActivity.isFromMassSetOKP = false;
        setDeliveredStatusActivity.isFromSetOKP = false;
        Intent i = new Intent(LeaveApplicationActivity.this, Camera5Activity.class);
        Context ctx = LeaveApplicationActivity.this;
        try {
            if (imagePaths != null && imagePaths.size() > 0) {
                int callLogImageCount = 0;
                if (dataT != null && dataT.size() > 0) {
                    for (int x = 0; x <= dataT.size() - 1; x++) {
                        callLogImageCount += dataT.get(x).isCallLog ? 1 : 0;
                    }
                }
                if (imagePaths.size() - callLogImageCount >= 6) {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.cannotlaunchcam));
                } else {
                    startActivity(i);
                }
            } else if (imagePaths != null && imagePaths.size() == 0) {
                startActivity(i);
            } else if (imagePaths == null) {
                startActivity(i);
            }
            objGlobalMethods.setDashboard2ActivityCategoryType(GcmPreferences, "-1");
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at LeaveApplicationActivity btnCameraClick. " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public void btnLeaveApplyCancelClicked(View v) {
        try {
            onBackPressed();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity btnLeaveApplyCancelClicked. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void submit(View v) {
        Context ctx = myApp.getAppContext();
        String remarks = txtRemarks.getText().toString();
        String FlagStr = lblSelectedLeaveFlag.getText().toString();
        int FLAG;
        try {
            FLAG = FlagStr.equals("") ? 0 : Integer.parseInt(FlagStr);
        } catch (Exception e) {
            FLAG = 0;
            objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity rtnddlLeaveTypeOnItemSelectedListener FlagStr balanceStr to INTEGER. Exception: " + e.getMessage(), "", "", "", "");
        }

        if (objGlobalMethods.IsEmptyString(remarks)) {
            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.enterleaveremarks)).show();
        } else if (((FLAG & 2) == 2) && ((imagePaths == null) || (imagePaths != null && imagePaths.size() == 0 && dataT != null && dataT.size() == 0))) {
            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_takemcphoto)).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(ctx.getString(R.string.leave_cfm_apply));

            builder.setPositiveButton(ctx.getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    submitLeave();
                }
            });

            builder.setNegativeButton(ctx.getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void submitLeave(){
        objLeaveRecordSubmit lr;
        String[] Images = null;
        Date FromDate, ToDate;
        long FromDateLong, ToDateLong;
        String FromTimestampstr = "";
        String ToTimestampstr = "";
        int fromAMPM = -1;
        int toAMPM = -1;
        long LeaveTypeIDX;
        String remarks;
        Context ctx = myApp.getAppContext();
        try {
            ArrayList<String> convertedBitmapToBase64List = RTNconvertedBitmapToBase64List();
            Images = convertedBitmapToBase64List == null ? null : convertedBitmapToBase64List.toArray(new String[convertedBitmapToBase64List.size()]);
            FromDate = objGlobalMethods.rtnddMMyyyyToDateObj(txtDateFrom.getText().toString());
            ToDate = objGlobalMethods.rtnddMMyyyyToDateObj(txtDateTo.getText().toString());
            FromDateLong = FromDate.getTime();
            ToDateLong = ToDate.getTime();
            FromTimestampstr = "/Date(" + FromDateLong + ")/";
            ToTimestampstr = "/Date(" + ToDateLong + ")/";
            try {
                fromAMPM = Integer.parseInt(lblSelectedFromAMPM.getText().toString());
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity submit converting fromAMPM. Exception: " + e.getMessage(),"","","","");
                fromAMPM = 0;
            }
            try {
                toAMPM = Integer.parseInt(lblSelectedToAMPM.getText().toString());
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity submit converting toAMPM. Exception: " + e.getMessage(),"","","","");
                toAMPM = 0;
            }
            try {
                LeaveTypeIDX = Long.parseLong(lblSelectedLeaveTypeIDXval.getText().toString());
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity submit converting LeaveTypeIDX. Exception: " + e.getMessage(),"","","","");
                LeaveTypeIDX = 0;
            }
            remarks = txtRemarks.getText().toString();
            lr = new objLeaveRecordSubmit(0,
                    lblUserType.getText().toString().equals("") || lblUserType.getText().toString().equals("gc") ? objRecordType.RecordType.etGC.ordinal() : objRecordType.RecordType.etStaff.ordinal(),
                    0,
                    FromTimestampstr,
                    fromAMPM,
                    ToTimestampstr,
                    toAMPM,
                    0,
                    objUOM.UOM.umDays.ordinal(),
                    LeaveTypeIDX,
                    0,
                    objStatus.Status.stSubmitted.ordinal(),
                    remarks,
                    Images != null && Images.length > 0 ? Images : null
            );

            UpdateLeaveApplication ula = new UpdateLeaveApplication(lr);
            ula.execute();

            /*SharedPreferences UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            JobsHandler.addUpdateLeaveApplicationQueALPrefs(UpdateJobInfoPreferences, lr);
            ctx = myApp.getAppContext();
            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            showErrorAlert(ctx.getString(R.string.alert_submittedsuccess), true);*/

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity submitLeave. Exception: " + e.getMessage(), "", "", "", "");
            showErrorAlert(ctx.getString(R.string.leave_submit_error), false);
        }
    }

    private class UpdateLeaveApplication extends AsyncTask<Void, Void, objLeaveRecord> {
        private final objLeaveRecordSubmit LeaveRecordSubmit;

        UpdateLeaveApplication(objLeaveRecordSubmit aLeaveRecordSubmit){
            this.LeaveRecordSubmit = aLeaveRecordSubmit;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected objLeaveRecord doInBackground(Void... params) {
            objLeaveRecord res = null;
            try {
                String ARegID;
                ARegID = objGlobalMethods.getRegistrationIdOnly();
                res = WebService.UpdateLeaveApplication(ARegID, LeaveRecordSubmit);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity UpdateLeaveApplication doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final objLeaveRecord res) {
            try {
                pd.dismiss();
                Context ctx = myApp.getAppContext();
                if (res == null || (res != null && res.IDX == 0)) {
                    showErrorAlert(ctx.getString(R.string.alert_submitLeaveError), false);
                } else if (res != null && res.IDX > 0) {
                    showErrorAlert(ctx.getString(R.string.alert_submittedsuccess), true);
                } else if (res != null && res.IDX < 0) {
                    showErrorAlert(res.Remarks != null && !res.Remarks.isEmpty() ? res.Remarks : ctx.getString(R.string.alert_submitLeaveRejected), false);
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity UpdateLeaveApplication onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            }
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
                }
            }
        } catch (Exception e) {
            convertedBitmapToBase64List = null;
        } finally {
            return convertedBitmapToBase64List;
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

    private View.OnClickListener txtDateOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final int id = v.getId();
                    Calendar todayCldr = Calendar.getInstance();
                    Calendar cldr = Calendar.getInstance();
                    cldr = id == R.id.txtDateFrom
                            ? objGlobalMethods.toCalendar(objGlobalMethods.rtnddMMyyyyToDateObj(txtDateFrom.getText().toString()))
                            : objGlobalMethods.toCalendar(objGlobalMethods.rtnddMMyyyyToDateObj(txtDateTo.getText().toString()));
                    final int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);

                    // date picker dialog
                    picker = new DatePickerDialog(LeaveApplicationActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    if (id == R.id.txtDateFrom) {
                                        Date datefrom = objGlobalMethods.toCalendar(year,monthOfYear,dayOfMonth).getTime();
                                        txtDateFrom.setText(aformat.format(datefrom));
                                        //txtDateFrom.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                                        Date dateto = objGlobalMethods.rtnddMMyyyyToDateObj(txtDateTo.getText().toString());
                                        //0 comes when two date are same,
                                        //1 comes when date1 is higher then date2
                                        //-1 comes when date1 is lower then date2
                                        if (dateto.compareTo(datefrom) <=0) {
                                            txtDateTo.setText(aformat.format(datefrom));
                                        }
                                    } else {
                                        Date dateto = objGlobalMethods.toCalendar(year,monthOfYear,dayOfMonth).getTime();
                                        txtDateTo.setText(aformat.format(dateto));
                                        //txtDateTo.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    }
                                }
                            }, year, month, day);

                    picker.getDatePicker().setMinDate(
                            id == R.id.txtDateFrom ? todayCldr.getTimeInMillis() :
                                    objGlobalMethods.toCalendar(objGlobalMethods.rtnddMMyyyyToDateObj(txtDateFrom.getText().toString())).getTimeInMillis()
                    );

                    picker.show();

                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity txtDateOnClick. Exception: " + e.getMessage(), "", "", "", "");
                }
            }
        };
    }

    private void showTodayDate() {
        try {
            Calendar calendar = Calendar.getInstance();
            txtDateFrom.setText(aformat.format(calendar.getTime()));
            txtDateTo.setText(aformat.format(calendar.getTime()));
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity showTodayDate. Exception: " + e.getMessage(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at LeaveApplicationActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
