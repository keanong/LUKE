package com.xdel.luke;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.BottomSheet.skipOTPdialog;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.SlideDateTimePicker.SlideDateListener;
import com.xdel.luke.SlideDateTimePicker.SlideDatePicker;
import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.jobSelectionInfo;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCheckedItems;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objUpdateCOD_PostData;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OtpActivity extends FragmentActivity {


    public static boolean failPart2FromPart1Bool = false;
    public static FragmentManager manager;
    public static String fromPage = "";
    public static Intent nextActivtyIntent;

    //private String regid;
    private ProgressDialog pd;
    private int year, month, day;
    private Calendar calendar;
    private AlertDialog alertD = null;
    private FragmentActivity activity;
    CustomKeyboard mCustomKeyboard;
    EditText edittext0;
    EditText txt01, txt02, txt03, txt04, txt05, txt06;
    TextView lblJobsIDX;
    TextView lblPin, lblPins, lblMassOKPbool, lblJobsIDXs, lblj1, lblJobsNos;
    Button btnMVerify, btnVerify, btnBack;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    TextView lbldate;
    ImageView imgbtnDate;
    SimpleDateFormat Dateformatter = new SimpleDateFormat("dd/MM/yyyy");
    SlideDateListener slideDateListener;
    View.OnClickListener DateOnClickListener;
    ScreenShotContentObserver screenShotContentObserver;
    TextChangeListener01 tc01;
    TextChangeListener02 tc02;
    TextChangeListener03 tc03;
    TextChangeListener04 tc04;
    TextChangeListener05 tc05;
    TextChangeListener06 tc06;

    TextChangeListener01_ tc01_;
    TextChangeListener02_ tc02_;
    TextChangeListener03_ tc03_;
    TextChangeListener04_ tc04_;
    TextChangeListener05_ tc05_;
    TextChangeListener06_ tc06_;

    /*BackKeyOnKeyListener02 bkl02;
    BackKeyOnKeyListener03 bkl03;
    BackKeyOnKeyListener04 bkl04;
    BackKeyOnKeyListener05 bkl05;
    BackKeyOnKeyListener06 bkl06;*/
    private String objCheckedItemsJsonStr;
    private objCheckedItems[] aobjCheckedItemsAL = null;
    private String objCheckedItemsIDXJsonStr;
    private String[] aCheckedItemsIDX;
    private String aCheckedItemsJOBSNOS;
    private String aCheckedItemsJOBSIDXS;
    private ArrayList<String> OTPS = null;
    private String OTPList;
    private String jobflagval;
    private int FLAG;
    private String jobSelectionInfoJsonStr;
    private jobSelectionInfo j1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_otp);

        mCustomKeyboard = new CustomKeyboard(this, R.id.keyboardview, R.xml.hexkbd);
        edittext0 = (EditText) findViewById(R.id.edittext0);
        txt01 = (EditText) findViewById(R.id.txt01);
        txt02 = (EditText) findViewById(R.id.txt02);
        txt03 = (EditText) findViewById(R.id.txt03);
        txt04 = (EditText) findViewById(R.id.txt04);
        txt05 = (EditText) findViewById(R.id.txt05);
        txt06 = (EditText) findViewById(R.id.txt06);
        lblJobsIDX = (TextView) findViewById(R.id.lblJobsIDX);
        lblPin = (TextView) findViewById(R.id.lblPin);
        lblPins = (TextView) findViewById(R.id.lblPins);
        lblMassOKPbool = findViewById(R.id.lblMassOKPbool);
        lblJobsIDXs = findViewById(R.id.lblJobsIDXs);
        lblJobsNos = findViewById(R.id.lblJobsNos);
        lblj1 = findViewById(R.id.lblj1);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_resendOTP));
        btnMVerify = (Button) findViewById(R.id.btnMVerify);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnBack = (Button) findViewById(R.id.btnBack);
        activity = (FragmentActivity) this;
        manager = activity.getSupportFragmentManager();

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

        txt01.requestFocus();
    }

    private boolean isMassOKP(SharedPreferences gcmp, SharedPreferences glsp) {
        Gson gson = new Gson();
        try {
            jobSelectionInfoJsonStr = objGlobalMethods.getGrouplistSP_KeyValue(glsp, "jobSelectionInfoJsonStr");
            j1 = !objGlobalMethods.IsEmptyString(jobSelectionInfoJsonStr) ? gson.fromJson(jobSelectionInfoJsonStr, jobSelectionInfo.class) : null;

            objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.checkedItemsIDX);
            if (!objGlobalMethods.IsEmptyString(objCheckedItemsIDXJsonStr))
                aCheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);
            else return false;

            if (aCheckedItemsIDX != null && aCheckedItemsIDX.length > 0)
                aCheckedItemsJOBSIDXS = "jx" + String.join(",jx", aCheckedItemsIDX);
            else return false;

            if (j1 == null)
                return false;

            OTPS = j1.OTPList;

            if (!objGlobalMethods.IsEmptyString(aCheckedItemsJOBSIDXS) && OTPS != null && OTPS.size() > 0) {
                lblMassOKPbool.setText("t");
                lblJobsIDXs.setText(aCheckedItemsJOBSIDXS);
                lblj1.setText(j1 != null ? gson.toJson(j1) : "");
                lblPins.setText(gson.toJson(OTPS));
                return true;
            }


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at OtpActivity isMassOKP. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            myApp.currentActivity = this;
        }
        try {
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            SharedPreferences glsp = objGlobalMethods.getGrouplistSP();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }

            if (!temploggerclass.isBTEnabled()) {
                objGlobalMethods.writeToFile("OtpActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

            mCustomKeyboard.registerEditText(R.id.txt01, 0);
            Gson gson = new Gson();

            objGlobalMethods.setGCMPrefs(GcmPreferences, "mChildCheckStates2PrefStr", "");

            if (isMassOKP(GcmPreferences, glsp)) {
                btnMVerify.setVisibility(
                        (j1.CHECKLIST == null || j1.CHECKLIST.length == 0)
                                || (j1.CHECKLIST != null && j1.CHECKLIST.length == 1)
                                || ((j1.FLAG & 8192) == 8192)
                                ? View.GONE : View.VISIBLE);
                hideshowtxt2();
                removeTextChangedListener2();
                preloadIndividualPinV();
                addTextChangedListener2();
                objGlobalMethods.writeToFile("massOKP OtpActivity viewed for JobsIDX: " + aCheckedItemsJOBSIDXS, "", "", "", "");
            } else {
                lblMassOKPbool.setText("");
                lblJobsIDXs.setText("");
                lblj1.setText("");
                //JobsHandler.resetrePositionedGroupItems(GcmPreferences);
                objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                if (currJobInfo != null && currJobInfo.PIN != null && !currJobInfo.PIN.isEmpty() && currJobInfo.JobsIDX > 0) {
                    objGlobalMethods.writeToFile("OtpActivity viewed for JobsIDX: " + String.valueOf(currJobInfo.JobsIDX), "", "", "", "");
                    lblJobsIDX.setText(String.valueOf(currJobInfo.JobsIDX));
                    lblPin.setText(currJobInfo.PIN);

                    /*boolean isResidentialType = (JobsHandler2.getLocationType(currJobInfo) == 1);
                btnMVerify.setVisibility(
                        (currJobInfo.CheckList == null || currJobInfo.CheckList.length == 0)
                                || (currJobInfo.CheckList != null && currJobInfo.CheckList.length == 1 && isResidentialType)
                                ? View.GONE : View.VISIBLE);*/
                    btnMVerify.setVisibility(
                            (currJobInfo.CheckList == null || currJobInfo.CheckList.length == 0)
                                    || (currJobInfo.CheckList != null && currJobInfo.CheckList.length == 1)
                                    || ((currJobInfo.FLAG & 8192) == 8192)
                                    ? View.GONE : View.VISIBLE);

                    //btnMVerify.setVisibility(currJobInfo.CheckList == null || currJobInfo.CheckList.length == 0 ? View.GONE : View.VISIBLE);

                    hideshowtxt(currJobInfo.PIN);
                    removeTextChangedListener();
                    preloadIndividualPinV();
                    addTextChangedListener(currJobInfo.PIN);
                    //enabledisablebtnVerify();

                } else {
                    //unable to verify here, proceed to manual verification
                    objGlobalMethods.writeToFile("Unable to verify PIN due currJobInfo == null || currJobInfo.PIN == null || currJobInfo.PIN.isEmpty() || currJobInfo.JobsIDX == 0", "", "", "", "");
                    showErrorAlert(myApp.getAppContext().getString(R.string.otperrverify0));
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at OtpActivity. Exception: " + e.getMessage().toString(), "", "", "", "");
            //unable to verify here, proceed to manual verification
            showErrorAlert(myApp.getAppContext().getString(R.string.otperrverify0));
        }
    }

    private void addBackKeyOnKeyListener() {
        try {

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at addBackKeyOnKeyListener addTextChangedListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void removeBackKeyOnKeyListener() {
        try {

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at addBackKeyOnKeyListener addTextChangedListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void addTextChangedListener2() {
        try {
            tc01_ = new TextChangeListener01_(OTPS, 1);
            tc02_ = new TextChangeListener02_(OTPS, 2);
            tc03_ = new TextChangeListener03_(OTPS, 3);
            tc04_ = new TextChangeListener04_(OTPS, 4);
            tc05_ = new TextChangeListener05_(OTPS, 5);
            tc06_ = new TextChangeListener06_(OTPS);
            txt01.addTextChangedListener(tc01_);
            txt02.addTextChangedListener(tc02_);
            txt03.addTextChangedListener(tc03_);
            txt04.addTextChangedListener(tc04_);
            txt05.addTextChangedListener(tc05_);
            txt06.addTextChangedListener(tc06_);
            addOnFocusChangeListener(txt01);
            addOnFocusChangeListener(txt02);
            addOnFocusChangeListener(txt03);
            addOnFocusChangeListener(txt04);
            addOnFocusChangeListener(txt05);
            addOnFocusChangeListener(txt06);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at OtpActivity addTextChangedListener2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void addTextChangedListener(String fullpin) {
        try {
            tc01 = new TextChangeListener01(fullpin, 1);
            tc02 = new TextChangeListener02(fullpin, 2);
            tc03 = new TextChangeListener03(fullpin, 3);
            tc04 = new TextChangeListener04(fullpin, 4);
            tc05 = new TextChangeListener05(fullpin, 5);
            tc06 = new TextChangeListener06(fullpin);
            txt01.addTextChangedListener(tc01);
            txt02.addTextChangedListener(tc02);
            txt03.addTextChangedListener(tc03);
            txt04.addTextChangedListener(tc04);
            txt05.addTextChangedListener(tc05);
            txt06.addTextChangedListener(tc06);
            addOnFocusChangeListener(txt01);
            addOnFocusChangeListener(txt02);
            addOnFocusChangeListener(txt03);
            addOnFocusChangeListener(txt04);
            addOnFocusChangeListener(txt05);
            addOnFocusChangeListener(txt06);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at OtpActivity addTextChangedListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void removeTextChangedListener2() {
        try {
            txt01.removeTextChangedListener(tc01_);
            txt02.removeTextChangedListener(tc02_);
            txt03.removeTextChangedListener(tc03_);
            txt04.removeTextChangedListener(tc04_);
            txt05.removeTextChangedListener(tc05_);
            txt06.removeTextChangedListener(tc06_);
            txt01.setOnFocusChangeListener(null);
            txt02.setOnFocusChangeListener(null);
            txt03.setOnFocusChangeListener(null);
            txt04.setOnFocusChangeListener(null);
            txt05.setOnFocusChangeListener(null);
            txt06.setOnFocusChangeListener(null);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at OtpActivity removeTextChangedListener2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void removeTextChangedListener() {
        try {
            txt01.removeTextChangedListener(tc01);
            txt02.removeTextChangedListener(tc02);
            txt03.removeTextChangedListener(tc03);
            txt04.removeTextChangedListener(tc04);
            txt05.removeTextChangedListener(tc05);
            txt06.removeTextChangedListener(tc06);
            txt01.setOnFocusChangeListener(null);
            txt02.setOnFocusChangeListener(null);
            txt03.setOnFocusChangeListener(null);
            txt04.setOnFocusChangeListener(null);
            txt05.setOnFocusChangeListener(null);
            txt06.setOnFocusChangeListener(null);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at OtpActivity removeTextChangedListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void tempSaveIndivialPinV() {
        try {
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            String[] p = new String[]{
                    txt01 != null ? txt01.getText().toString().trim() : "",
                    txt02 != null ? txt02.getText().toString().trim() : "",
                    txt03 != null ? txt03.getText().toString().trim() : "",
                    txt04 != null ? txt04.getText().toString().trim() : "",
                    txt05 != null ? txt05.getText().toString().trim() : "",
                    txt06 != null ? txt06.getText().toString().trim() : ""
            };
            Gson gson = new Gson();
            String pJstr = gson.toJson(p);
            if (!objGlobalMethods.IsEmptyString(pJstr) && !objGlobalMethods.IsEqualString(pJstr, "[]")) {
                objGlobalMethods.setGCMPrefs(gcmp, "pJstr", pJstr);
            } else {
                objGlobalMethods.setGCMPrefs(gcmp, "pJstr", "");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at OtpActivity tempSaveIndivialPinV. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void hideshowtxt2() {
        String longestPin = "";
        try {
            if (OTPS != null && OTPS.size() > 0) {
                for (String s : OTPS) {
                    if (s.length() > longestPin.length()) {
                        longestPin = s;
                    }
                }
            }

            int l = longestPin.length();
            txt01.setVisibility(l > 0 ? View.VISIBLE : View.GONE);
            txt02.setVisibility(l > 1 ? View.VISIBLE : View.GONE);
            txt03.setVisibility(l > 2 ? View.VISIBLE : View.GONE);
            txt04.setVisibility(l > 3 ? View.VISIBLE : View.GONE);
            txt05.setVisibility(l > 4 ? View.VISIBLE : View.GONE);
            txt06.setVisibility(l > 5 ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at OtpActivity hideshowtxt2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void hideshowtxt(String Pin) {
        try {
            int l = Pin.length();
            txt01.setVisibility(l > 0 ? View.VISIBLE : View.GONE);
            txt02.setVisibility(l > 1 ? View.VISIBLE : View.GONE);
            txt03.setVisibility(l > 2 ? View.VISIBLE : View.GONE);
            txt04.setVisibility(l > 3 ? View.VISIBLE : View.GONE);
            txt05.setVisibility(l > 4 ? View.VISIBLE : View.GONE);
            txt06.setVisibility(l > 5 ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at OtpActivity hideshowtxt. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void preloadIndividualPinV() {
        try {
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            Gson gson = new Gson();
            String pJstr = objGlobalMethods.getGCMPrefs(gcmp, "pJstr");
            String[] p = !objGlobalMethods.IsEmptyString(pJstr) ? gson.fromJson(pJstr, String[].class) : null;
            String p1 = "";
            String p2 = "";
            String p3 = "";
            String p4 = "";
            String p5 = "";
            String p6 = "";
            if (p != null && p.length > 0) {
                p1 = p[0];
                p2 = p[1];
                p3 = p[2];
                p4 = p[3];
                p5 = p[4];
                p6 = p[5];
            }

            txt01.setText(p1);
            txt02.setText(p2);
            txt03.setText(p3);
            txt04.setText(p4);
            txt05.setText(p5);
            txt06.setText(p6);

            if (p != null && p.length > 0) {
                for (int i = p.length; i >= 1; i--) {
                    if (!objGlobalMethods.IsEmptyString(p[i - 1])) {
                        if (i == 6) {
                            txt06.requestFocus();
                            txt06.setSelection(txt06.getText().length());
                        } else if (i == 5) {
                            txt06.requestFocus();
                        } else if (i == 4) {
                            txt05.requestFocus();
                        } else if (i == 3) {
                            txt04.requestFocus();
                        } else if (i == 2) {
                            txt03.requestFocus();
                        } else if (i == 1) {
                            txt02.requestFocus();
                            txt02.setSelection(txt02.getText().length());
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at OtpActivity preloadIndividualPinV. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void enabledisablebtnVerify() {
        try {
            int pinlength = !objGlobalMethods.IsEmptyString(lblPin.getText().toString()) ? lblPin.getText().toString().length() : 0;
            String pinv =
                    (!objGlobalMethods.IsEmptyString(txt01.getText().toString()) ? txt01.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt02.getText().toString()) ? txt02.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt03.getText().toString()) ? txt03.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt04.getText().toString()) ? txt04.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt05.getText().toString()) ? txt05.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt06.getText().toString()) ? txt06.getText().toString() : "");
            if (!objGlobalMethods.IsEmptyString(pinv) && pinv.length() == pinlength) {
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnVerify);
            } else {
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnVerify);
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at OtpActivity enabledisablebtnVerify. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at OtpActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "pJstr", "");
        DAO_Jobs.nullifystaticitems();
    }

    @Override
    protected void onPause() {
        try {
            tempSaveIndivialPinV();
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at OtpActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // NOTE Trap the back key: when the CustomKeyboard is still visible hide it, only when it is invisible, finish activity
        /*if( mCustomKeyboard.isCustomKeyboardVisible() ) {
            mCustomKeyboard.hideCustomKeyboard();
        }
        else {
            Intent intent = new Intent(OtpActivity.this, JobInfoActivity.class);
            startActivity(intent);
            this.finish();
        }*/

        ////old code
        /*SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        Gson gson = new Gson();
        objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
        //currJobInfo.setTypeOfPinVerification(0);
        if (!objGlobalMethods.IsEmptyString(fromPage) && !objGlobalMethods.IsEqualString(fromPage, "CheckListActivity")) {
            currJobInfo.setTypeOfPinVerification(0);
        }
        currJobInfo.setManualPinVerificaionText("");
        String currJobInfoPrefStr = gson.toJson(currJobInfo);
        objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", currJobInfoPrefStr);
        objGlobalMethods.setGCMPrefs(GcmPreferences, "pJstr", "");
        //Intent intent = new Intent(OtpActivity.this, JobInfoActivity.class);
        Intent intent = new Intent(OtpActivity.this, !objGlobalMethods.IsEmptyString(fromPage) && objGlobalMethods.IsEqualString(fromPage, "CheckListActivity") ? CheckListActivity.class : JobInfoActivity.class);
        startActivity(intent);
        this.finish();*/
        ////old code
        back();
    }

    private void back() {
        boolean massOKP = !objGlobalMethods.IsEmptyString(lblMassOKPbool.getText().toString());
        objJobInfo currJobInfo = null;
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        Gson gson = new Gson();
        String[] prefName = null;
        String[] prefvalue = null;

        if (massOKP) {
            prefName = new String[]{"TypeOfPinVerificationStr"};
            prefvalue = new String[]{"0"};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
        } else {
            currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            if (!objGlobalMethods.IsEmptyString(fromPage) && !objGlobalMethods.IsEqualString(fromPage, "CheckListActivity"))
                currJobInfo.setTypeOfPinVerification(0);
            String currJobInfoPrefStr = gson.toJson(currJobInfo);
            prefName = new String[]{"hfJobInfoJsonStr", "TypeOfPinVerificationStr"};
            prefvalue = new String[]{currJobInfoPrefStr, String.valueOf(currJobInfo.getTypeOfPinVerification())};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
        }

        //Intent intent = new Intent(OtpActivity.this, JobInfoActivity.class);
        Intent intent = new Intent(OtpActivity.this, !objGlobalMethods.IsEmptyString(fromPage) && objGlobalMethods.IsEqualString(fromPage, "CheckListActivity") ? CheckListActivity.class : JobInfoActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_otp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Dialog showDialog(String msg) {
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
                //if user pressed "yes", then he is allowed to exit from application
                SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                Gson gson = new Gson();
                objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                currJobInfo.setTypeOfPinVerification(0);
                currJobInfo.setManualPinVerificaionText("");
                String currJobInfoPrefStr = gson.toJson(currJobInfo);
                //objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", currJobInfoPrefStr);
                String[] prefName = null;
                String[] prefvalue = null;
                prefName = new String[]{"hfJobInfoJsonStr", "TypeOfPinVerificationStr"};
                prefvalue = new String[]{currJobInfoPrefStr, String.valueOf(currJobInfo.getTypeOfPinVerification())};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                //Intent intent = new Intent(OtpActivity.this, JobInfoActivity.class);
                Intent intent = new Intent(OtpActivity.this, !objGlobalMethods.IsEmptyString(fromPage) && objGlobalMethods.IsEqualString(fromPage, "CheckListActivity") ? CheckListActivity.class : JobInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void promptToUpdateP2(final objJobInfo alinkedJobInfo, final List<objJobInfo> aobjJobInfoBringtoUpdateCODList,
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
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    String part1cnno = apart1cnno;
                    objJobInfo linkedJobInfo = alinkedJobInfo;
                    List<objJobInfo> objJobInfoBringtoUpdateCODList = aobjJobInfoBringtoUpdateCODList;
                    List<objUpdateCOD_PostData> objUpdateCODPostDataList = aobjUpdateCODPostDataList;
                    String objJobInfoBringtoUpdateCODListJsonStr = "";
                    String mutlipleCODjobs = "";


                    objGlobalMethods.writeToFile("Click to update Part 2 job status.",
                            "JobsIDX: " + String.valueOf(linkedJobInfo.JobsIDX), "Submitted StatusCodeIDX: 49", "", "");

                    if (p2hasCOD) {
                        String RegID = objGlobalMethods.getRegistrationIdOnly();
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

                    //ojisPage == 0 == JobInfoActivity | ojisPage == 1 == JobsListGroupActivity | ojisPage == 2 == setDeliveredStatusActivity |
                    // ojisPage == 3 == setDeliveredMassStatusActivity | ojisPage == 4 == MassUpdateOKPwP2 | ojisPage == 5 == OtpActivity
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "ojisPage", "5");

                    Intent intent = new Intent(OtpActivity.this, UpdateCOD_List.class);
                    startActivity(intent);
                    finish();

                }
            });

            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_FP), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objJobInfo linkedJobInfo = alinkedJobInfo;
                    Gson gson = new Gson();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");
                    objGlobalMethods.writeToFile("Click to Fail Part 2 job status.",
                            "JobsIDX: " + String.valueOf(linkedJobInfo.JobsIDX), "", "", "");
                    failPart2FromPart1Bool = true;
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

                    Intent parentIntent = new Intent(OtpActivity.this, setFailStatusActivity.class);
                    MainActivity.uponLogin = 0;
                    startActivity(parentIntent);

                }
            });

            final AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity promptToUpdateP2. Exception: " + e.toString(), "", "", "", "");
        }
    }

    //currently not in used in activity_otp
    public void btnVerifyClick(View v) {
        String part1cnno = "";
        String objUpdateCODPostDataListJsonStr = "";
        List<objUpdateCOD_PostData> objUpdateCODPostDataList = null;
        List<objJobInfo> objJobInfoBringtoUpdateCODList = null;
        objJobInfo linkedjob = null;
        String objJobInfoBringtoUpdateCODListJsonStr = "";
        String mutlipleCODjobs = "";
        Date dt = new Date();
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        SharedPreferences UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        String reason = "";
        String Receiver = "PIN Verification";

        try {
            Boolean verified = false;
            String val = edittext0.getText().toString();
            val =
                    (!objGlobalMethods.IsEmptyString(txt01.getText().toString()) ? txt01.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt02.getText().toString()) ? txt02.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt03.getText().toString()) ? txt03.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt04.getText().toString()) ? txt04.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt05.getText().toString()) ? txt05.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt06.getText().toString()) ? txt06.getText().toString() : "");
            if (!val.equals("")) {
                if (val.equals(lblPin.getText().toString())) {
                    verified = true;
                }

                if (verified) {
                    Gson gson = new Gson();
                    objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                    //objGlobalMethods.writeToFile("PIN verification successful for JobsIDX: " + String.valueOf(currJobInfo.JobsIDX) + ", PIN: " + edittext0.getText().toString().toUpperCase(), "", "", "", "");
                    objGlobalMethods.writeToFile("PIN verification successful for JobsIDX: " + String.valueOf(currJobInfo.JobsIDX) + ", PIN: " + val.toUpperCase(), "", "", "", "");
                    currJobInfo.setTypeOfPinVerification(1);
                    //currJobInfo.setManualPinVerificaionText("PIN verification code entered: " + edittext0.getText().toString().toUpperCase());
                    currJobInfo.setManualPinVerificaionText("PIN verification code entered: " + val.toUpperCase());
                    String currJobInfoPrefStr = gson.toJson(currJobInfo);
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", currJobInfoPrefStr);
                    String[] prefName = null;
                    String[] prefvalue = null;
                    prefName = new String[]{"hfJobInfoJsonStr", "TypeOfPinVerificationStr"};
                    prefvalue = new String[]{currJobInfoPrefStr, String.valueOf(currJobInfo.getTypeOfPinVerification())};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    Boolean JobhasCOD = false;
                    Boolean P2JobhasCOD = false;

                    JobhasCOD = JobsHandler.checkForCODwithCatType(currJobInfo, 2);
                    P2JobhasCOD = JobsHandler.checkPart2ForCOD(currJobInfo, GcmPreferences);

                    if (currJobInfo.ConsignmentNote != null && !currJobInfo.ConsignmentNote.equals("")) {
                        part1cnno = currJobInfo.ConsignmentNote;
                    } else {
                        part1cnno = currJobInfo.JobNo;
                    }

                    reason = currJobInfo.getManualPinVerificaionText();

                    if (JobhasCOD || P2JobhasCOD) {
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

                        String RegID = objGlobalMethods.getRegistrationIdOnly();
                        if (JobhasCOD && P2JobhasCOD) {
                            //p1 & 2 have COD
                            objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, currJobInfo, 2, true,
                                    RegID, false, part1cnno, Receiver);
                        } else if (!JobhasCOD && P2JobhasCOD) {
                            //p1 no COD, p2 has
                        } else if (JobhasCOD && !P2JobhasCOD) {
                            //p1 has COD, p2 no COD
                            objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, currJobInfo, 2, true,
                                    RegID, false, part1cnno, Receiver);
                        }

                        currJobInfo.setpendingStatusUpdate(135);
                        currJobInfo.setReceiver(Receiver);
                        currJobInfo.setFailureReason("");
                        currJobInfo.setconvertedBitmapToBase64(null);
                        currJobInfo.setSignatureStr("");

                        objJobInfoBringtoUpdateCODList = JobsHandler.RTNobjJobInfoBringtoUpdateCODList(objJobInfoBringtoUpdateCODList, currJobInfo);

                        if (currJobInfo.LinkedJobs != null && currJobInfo.LinkedJobs.length > 0 && currJobInfo.LinkedJobs[0] != null) {
                            linkedjob = currJobInfo.LinkedJobs[0];
                        }

                        if (linkedjob != null) {
                            // has p2, prompt update p2
                            promptToUpdateP2(linkedjob, objJobInfoBringtoUpdateCODList, objUpdateCODPostDataList, JobhasCOD, P2JobhasCOD, part1cnno);
                        } else {
                            //no p2, no prompt, str8 to UpdateCOD_List
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

                            //ojisPage == 0 == JobInfoActivity | ojisPage == 1 == JobsListGroupActivity | ojisPage == 2 == setDeliveredStatusActivity |
                            // ojisPage == 3 == setDeliveredMassStatusActivity | ojisPage == 4 == MassUpdateOKPwP2 | ojisPage == 5 == OtpActivity
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "ojisPage", "5");

                            Intent intent = new Intent(this, UpdateCOD_List.class);
                            startActivity(intent);
                            this.finish();
                        }


                    } else if (!JobhasCOD && !P2JobhasCOD) {
                        Long l = dt.getTime();
                        String timestampstr = "/Date(" + l + ")/";
                        String RegID = objGlobalMethods.getRegistrationIdOnly();
                        submitOKP(GcmPreferences, UpdateJobInfoPreferences, RegID, currJobInfo.JobsIDX, timestampstr, "PIN Verification", currJobInfo.getManualPinVerificaionText(), currJobInfo);
                    }
                    ////update COD portion

                } else {
                    //get numbers of tries, max 3 tries, else, show skip
                    Gson gson = new Gson();
                    objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                    String ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText();
                    if (ManualPinVerificaionText != null && !ManualPinVerificaionText.equals("")) {
                        currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText + "\nWrong PIN entered: " + val);
                    } else {
                        currJobInfo.setManualPinVerificaionText("Wrong PIN entered: " + val);
                    }

                    String currJobInfoPrefStr = gson.toJson(currJobInfo);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", currJobInfoPrefStr);

                    Integer OTPtries;
                    String OTPtriesJsonStr = "";
                    OTPtriesJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "OTPtriesJsonStr");
                    if (!OTPtriesJsonStr.equals("") || !OTPtriesJsonStr.equals("[]")) {
                        OTPtries = gson.fromJson(OTPtriesJsonStr, Integer.class);
                    } else {
                        OTPtries = 0;
                    }

                    if (OTPtries == null) {
                        OTPtries = 0;
                    }

                    OTPtries += 1;
                    OTPtriesJsonStr = gson.toJson(OTPtries);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "OTPtriesJsonStr", OTPtriesJsonStr);
                    edittext0.setText("");

                    //OTPtries = 3;

                    if ((currJobInfo.FLAG & 512) == 512) {
                        if (OTPtries > 2) {
                            btnMVerify.setVisibility(View.VISIBLE);
                            showDialog(myApp.getAppContext().getString(R.string.otp_Mverify_3tries));
                        } else if (OTPtries <= 2) {
                            btnMVerify.setVisibility(View.GONE);
                            showDialog(myApp.getAppContext().getString(R.string.otperrverify));
                        }
                    } else if ((currJobInfo.FLAG & 1024) == 1024) {
                        if (OTPtries > 2) {
                            btnMVerify.setVisibility(View.GONE);
                            showDialog(myApp.getAppContext().getString(R.string.otperrverify));
                        } else if (OTPtries <= 2) {
                            btnMVerify.setVisibility(View.GONE);
                        }
                    }
                }
            } else {
                showDialog(myApp.getAppContext().getString(R.string.otp_enterval));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at OtpActivity btnVerifyClick. Exception: " + e.getMessage().toString(), "", "", "", "");
            //unable to verify here
            showErrorAlert(myApp.getAppContext().getString(R.string.otperrverify0));
        }
    }

    public void btnVerifyClick2(View v) {
        next();
    }

    public void submitOKP(SharedPreferences gcmp, SharedPreferences updatep, String aregid, long idx, String atimestampstr, String Receiver, String reason, objJobInfo currJobInfo) {
        String SignatureStr = "";

        try {
            objGlobalMethods.writeToFile("OtpActivity submitOKP started.", "", "", "", "");
            Gson gson = new Gson();

            //String checklistvalue = objGlobalMethods.getGCMPrefs(gcmp, "checklistvalue");
            String checklistvalue = objGlobalMethods.getChecklistRelatedSP_KeyValue("checklistvalue");
            if (checklistvalue.length() > 0) {
                reason += "\n\n" + checklistvalue;
            }
            String pr_latemsg0 = objGlobalMethods.getGCMPrefs(gcmp, "pr_latemsg0");
            if (!objGlobalMethods.IsEmptyString(pr_latemsg0)) {
                reason += "\n" + pr_latemsg0;
            }

            Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
            int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);

            JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(updatep, aregid, idx, 135, atimestampstr, reason, "",
                    lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, Receiver);

            Camera5Activity.mCurrentPhotoPath = null;

            ////set from dip to okp
            DAO_Jobs.updateJobsCatTypeID(3, idx);
            JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmp, 2);
            JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmp, 3);

            Context ctx = myApp.getAppContext();
            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

            objJobInfo linkedJobsInfo = null;
            try {
                String linkedJobsInfoStr = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoLinkedJobInfo);
                if (!linkedJobsInfoStr.equals("")) {
                    linkedJobsInfo = gson.fromJson(linkedJobsInfoStr, objJobInfo.class);
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Fail to convert Part 2 Json String to objJobInfo. " + e.getMessage().toString(),
                        "Part 1 JobsIDX: " + String.valueOf(idx), "Submitted Recipient: " + Receiver, "", "");
                //Mail m = new Mail("xdelsingapore66@gmail.com", "12321qwE");
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
                username = UsernamePwd[0];
                password = UsernamePwd[1];

                Mail m = new Mail("luke@xdel.com", "XDel2015");
                objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", "Fail to convert Part 2 Json String to objJobInfo, Part 1 JobsIDX: " + String.valueOf(idx) + ". " + e.getMessage().toString(),
                        "Luke update error " + username + ".", null);
                sendmail.execute();
            }

            objGlobalMethods.writeToFile("OtpActivity submitOKP ended.", "", "", "", "");

            if (linkedJobsInfo != null) {
                //prompt to update part 2 status
                resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob2(gcmp.getString("hfJobsInfoArrJsonStr", ""), idx, linkedJobsInfo);
            } else {
                //resetAddressListActivityhfJobsInfoArrJsonStr(gcmp.getString("hfJobsInfoArrJsonStr", ""), idx);
                resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob3();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at OtpActivity submitOKP. Exception: " + e.toString(), "", "", "", "");
        }
    }

    public void next2() {
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        boolean massOKP = !objGlobalMethods.IsEmptyString(lblMassOKPbool.getText().toString());
        Gson gson = new Gson();
        objCheckedItems ci = null;
        objJobInfo currJobInfo = null;
        String ManualPinVerificaionText = "";
        try {
            Boolean verified = false;
            String val = edittext0.getText().toString();
            val =
                    (!objGlobalMethods.IsEmptyString(txt01.getText().toString()) ? txt01.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt02.getText().toString()) ? txt02.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt03.getText().toString()) ? txt03.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt04.getText().toString()) ? txt04.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt05.getText().toString()) ? txt05.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt06.getText().toString()) ? txt06.getText().toString() : "");

            if (!objGlobalMethods.IsEmptyString(val)) {
                //verified = val.equals(lblPin.getText().toString()) ? true : false;
                if (OTPS == null && !objGlobalMethods.IsEmptyString(lblPins.getText().toString()))
                    OTPS = gson.fromJson(lblPins.getText().toString(), new TypeToken<ArrayList<String>>() {
                    }.getType());
                if (OTPS != null && OTPS.size() > 0 && OTPS.contains(val))
                    verified = true;

                //j1 = !objGlobalMethods.IsEmptyString(lblj1.getText().toString()) ? gson.fromJson(lblj1.getText().toString(), jobSelectionInfo.class) : null;

                objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
                if (!objGlobalMethods.IsEmptyString(objCheckedItemsJsonStr))
                    aobjCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);

                //String allJobsIDXs = " for " + lblJobsIDXs.getText().toString();
                String OTP_FromWhichJob = "";
                String startmsg = "Courier attempt to mass PIN verification.";

                if (verified) {
                    if (aobjCheckedItemsAL != null && aobjCheckedItemsAL.length > 0) {
                        for (int i = 0; i <= aobjCheckedItemsAL.length - 1; i++) {
                            currJobInfo = aobjCheckedItemsAL[i].jobinfo;
                            if (objGlobalMethods.IsEqualString(val, currJobInfo.PIN)) {
                                OTP_FromWhichJob = "from " + currJobInfo.JobNo;
                                break;
                            }
                        }

                        for (int i = 0; i <= aobjCheckedItemsAL.length - 1; i++) {
                            ci = aobjCheckedItemsAL[i];
                            currJobInfo = ci.jobinfo;
                            ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText();
                            /*currJobInfo.setManualPinVerificaionText(
                                    (
                                    (!objGlobalMethods.IsEmptyString(ManualPinVerificaionText)) ?
                                            ManualPinVerificaionText + "\nVerified PIN entered: " + val.toUpperCase() :
                                            ("Verified PIN entered: " + val.toUpperCase())
                                            ) + "\n" + allJobsIDXs
                            );*/
                            currJobInfo.setManualPinVerificaionText(
                                    (
                                            (!objGlobalMethods.IsEmptyString(ManualPinVerificaionText)) ?
                                                    ManualPinVerificaionText + startmsg + "\nVerified PIN entered: " + val.toUpperCase() :
                                                    (startmsg + "\nVerified PIN entered: " + val.toUpperCase())
                                    ) + "\n" + OTP_FromWhichJob
                            );
                            currJobInfo.setTypeOfPinVerification(1);
                            currJobInfo.setOTPtries(3);
                            aobjCheckedItemsAL[i].setJobinfo(currJobInfo);
                        }
                        objGlobalMethods.writeToFile("PIN verification successful for JobsIDXs: " + lblJobsIDXs.getText().toString() + ", PIN: " + val.toUpperCase(),
                                "", "", "", "");
                        objCheckedItemsJsonStr = gson.toJson(aobjCheckedItemsAL);

                        String[] prefName = null;
                        String[] prefvalue = null;
                        prefName = new String[]{objGlobalVariables.checkedItemsName, "TypeOfPinVerificationStr", "hasVerifiedOTPStr"};
                        prefvalue = new String[]{objCheckedItemsJsonStr, "1", "1"};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                        startActivity(nextActivtyIntent);
                        finish();
                    } else {
                        showErrorAlert(myApp.getAppContext().getString(R.string.otperrverify0));
                    }
                } else {
                    if (aobjCheckedItemsAL != null && aobjCheckedItemsAL.length > 0) {
                        for (int i = 0; i <= aobjCheckedItemsAL.length - 1; i++) {
                            ci = aobjCheckedItemsAL[i];
                            currJobInfo = ci.jobinfo;
                            ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText();
                            /*currJobInfo.setManualPinVerificaionText(
                                    (
                                    (!objGlobalMethods.IsEmptyString(ManualPinVerificaionText)) ?
                                            ManualPinVerificaionText + "\nWrong PIN entered: " + val.toUpperCase() :
                                            ("Wrong PIN entered: " + val.toUpperCase())
                                ) + "\n" + allJobsIDXs
                            );*/
                            currJobInfo.setManualPinVerificaionText(
                                    (
                                            (!objGlobalMethods.IsEmptyString(ManualPinVerificaionText)) ?
                                                    ManualPinVerificaionText + startmsg + "\nWrong PIN entered: " + val.toUpperCase() :
                                                    (startmsg + "\nWrong PIN entered: " + val.toUpperCase())
                                    ) + "\n" + OTP_FromWhichJob
                            );

                            aobjCheckedItemsAL[i].setJobinfo(currJobInfo);
                        }
                        objCheckedItemsJsonStr = gson.toJson(aobjCheckedItemsAL);
                        String[] prefName = null;
                        String[] prefvalue = null;
                        prefName = new String[]{objGlobalVariables.checkedItemsName};
                        prefvalue = new String[]{objCheckedItemsJsonStr};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                        setTxtEmpty();
                        showDialog(myApp.getAppContext().getString(R.string.otperrverify));
                    } else {
                        showErrorAlert(myApp.getAppContext().getString(R.string.otperrverify0));
                    }
                }
            } else {
                showDialog(myApp.getAppContext().getString(R.string.otp_enterval));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at OtpActivity next2. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            //unable to verify here
            edittext0.setText("");
            setTxtEmpty();
            showErrorAlert(myApp.getAppContext().getString(R.string.otperrverify0));
        }
    }

    public void next() {
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        try {
            Boolean verified = false;
            String val = edittext0.getText().toString();
            val =
                    (!objGlobalMethods.IsEmptyString(txt01.getText().toString()) ? txt01.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt02.getText().toString()) ? txt02.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt03.getText().toString()) ? txt03.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt04.getText().toString()) ? txt04.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt05.getText().toString()) ? txt05.getText().toString() : "") +
                            (!objGlobalMethods.IsEmptyString(txt06.getText().toString()) ? txt06.getText().toString() : "");
            if (!val.equals("")) {
                verified = val.equals(lblPin.getText().toString()) ? true : false;
                if (verified) {
                    Gson gson = new Gson();
                    objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                    objGlobalMethods.writeToFile("PIN verification successful for JobsIDX: " + String.valueOf(currJobInfo.JobsIDX) + ", PIN: " + val.toUpperCase(), "", "", "", "");
                    String ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText();
                    if (ManualPinVerificaionText != null && !ManualPinVerificaionText.equals("")) {
                        currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText + "\nVerified PIN entered: " + val.toUpperCase());
                    } else {
                        currJobInfo.setManualPinVerificaionText("Verified PIN entered: " + val.toUpperCase());
                    }
                    currJobInfo.setTypeOfPinVerification(1);
                    currJobInfo.setOTPtries(3);
                    String currJobInfoPrefStr = gson.toJson(currJobInfo);
                    DAO_Jobs.updateJobInfoPrefStr(currJobInfo.JobsIDX, currJobInfoPrefStr);
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", currJobInfoPrefStr);
                    String[] prefName = null;
                    String[] prefvalue = null;
                    prefName = new String[]{"hfJobInfoJsonStr", "TypeOfPinVerificationStr", "hasVerifiedOTPStr"};
                    prefvalue = new String[]{currJobInfoPrefStr, String.valueOf(currJobInfo.getTypeOfPinVerification()), "1"};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    startActivity(nextActivtyIntent);
                    finish();
                } else {
                    Gson gson = new Gson();
                    objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                    objGlobalMethods.writeToFile("PIN verification failed for JobsIDX: " + String.valueOf(currJobInfo.JobsIDX) + ", PIN: " + val.toUpperCase(), "", "", "", "");
                    String ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText();
                    if (!objGlobalMethods.IsEmptyString(ManualPinVerificaionText)) {
                        currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText + "\nWrong PIN entered: " + val);
                    } else {
                        currJobInfo.setManualPinVerificaionText("Wrong PIN entered: " + val);
                    }

                    Integer OTPtries = currJobInfo.OTPtries == null ? 0 : currJobInfo.OTPtries;

                    if (OTPtries == null) {
                        OTPtries = 0;
                    }

                    OTPtries += 1;
                    currJobInfo.setOTPtries(OTPtries);

                    if (OTPtries >= 3 && (currJobInfo.CheckList != null && currJobInfo.CheckList.length > 0)) {
                        if (ManualPinVerificaionText != null && !ManualPinVerificaionText.equals("")) {
                            //currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText + "\nWrong PIN entered: " + val + "\n3 unsuccessful attempts made. Proceed to alternate verification process.");
                            currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText + "\nWrong PIN entered: " + val);
                        } else {
                            //currJobInfo.setManualPinVerificaionText("Wrong PIN entered: " + val + "\n3 unsuccessful attempts made. Proceed to alternate verification process.");
                            currJobInfo.setManualPinVerificaionText("Wrong PIN entered: " + val);
                        }
                        currJobInfo.setTypeOfPinVerification(2);
                    }

                    String currJobInfoPrefStr = gson.toJson(currJobInfo);
                    DAO_Jobs.updateJobInfoPrefStr(currJobInfo.JobsIDX, currJobInfoPrefStr);

                    //objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", currJobInfoPrefStr);
                    String[] prefName = null;
                    String[] prefvalue = null;
                    prefName = new String[]{"hfJobInfoJsonStr", "TypeOfPinVerificationStr", "hasVerifiedOTPStr"};
                    prefvalue = new String[]{currJobInfoPrefStr, String.valueOf(currJobInfo.getTypeOfPinVerification()), ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    edittext0.setText("");
                    setTxtEmpty();
                    if (OTPtries >= 3) {
                        boolean isResidentialType = (JobsHandler2.getLocationType(currJobInfo) == 1);
                        if (
                                (currJobInfo.CheckList == null || currJobInfo.CheckList.length == 0)
                                        || (currJobInfo.CheckList != null && currJobInfo.CheckList.length == 1 && isResidentialType) || ((currJobInfo.FLAG & 8192) == 8192)
                        ) {
                            showDialog(myApp.getAppContext().getString(R.string.otperrverify));
                        } else {
                            autoSkip();
                        }
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.otperrverify));
                    }
                }
            } else {
                showDialog(myApp.getAppContext().getString(R.string.otp_enterval));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caughtat OtpActivity next. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            //unable to verify here
            edittext0.setText("");
            setTxtEmpty();
            showErrorAlert(myApp.getAppContext().getString(R.string.otperrverify0));
        }
    }

    private void setTxtEmpty() {
        try {
            edittext0.setText("");
            txt01.setText("");
            txt02.setText("");
            txt03.setText("");
            txt04.setText("");
            txt05.setText("");
            txt06.setText("");
            txt01.requestFocus();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caughtat OtpActivity setTxtEmpty. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob2(String AddressListActivityhfJobsInfoArrJsonStr, long JobsIDX, objJobInfo alinkedJobInfo) {
        objJobInfo[] subJobInfoAL;
        objJobInfo ji;

        final objJobInfo linkedJobInfo = alinkedJobInfo;
        final long AJobsIDX = JobsIDX;
        final String AAddressListActivityhfJobsInfoArrJsonStr = AddressListActivityhfJobsInfoArrJsonStr;

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_pup2));

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_PU), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //if user pressed "yes", update job status
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    SharedPreferences UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();

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

                    Context ctx = myApp.getAppContext();
                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                    ////add jobinfo to pu pref
                    Gson gson = new Gson();
                    String linkedJobStr = gson.toJson(linkedJobInfo);
                    DAO_Jobs.insertJobs(linkedJobInfo, 1, linkedJobStr);

                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoLinkedJobInfo, "");

                    resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob3();
                }
            });
            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_FP), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    SharedPreferences UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();

                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");
                    objGlobalMethods.writeToFile("Click to Fail Part 2 job status.",
                            "JobsIDX: " + String.valueOf(linkedJobInfo.JobsIDX), "", "", "");
                    failPart2FromPart1Bool = true;
                    Intent parentIntent = new Intent(OtpActivity.this, setFailStatusActivity.class);
                    MainActivity.uponLogin = 0;
                    startActivity(parentIntent);

                }
            });

            final AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at OtpActivity resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob2. Exception: " + e.getMessage().toString(),
                    "", "", "", "");
        }
    }

    private void resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob3() {
        SignatureNameActivity.requireSignature = false;
        Camera5Activity.mCurrentPhotoPath = null;
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        SharedPreferences UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        MassUpdateOKPwP2.massupdateokpwp2PostDataArr = null;
        String[] prefName = new String[]{"searchedval", objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr"};
        String[] prefvalue = new String[]{"", "", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
        objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, "");
        objGlobalMethods.resetJobsListGroupScrollIndexNpostion(GcmPreferences);
        Integer catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();

        try {
            if (!GcmPreferences.getString("isFromMainCOD", "").isEmpty()) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
                Intent parentIntent = new Intent(this, MainCODActivity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else {
                int TotalJobsCountPerCatTypePerPostal = 0;
                String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
                if (Postal != "") {
                    String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
                    TotalJobsCountPerCatTypePerPostal = !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal) :
                            !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(catType, Postal, SelectedVRPJobsIDXs)
                                    : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal);

                    if (TotalJobsCountPerCatTypePerPostal > 0) {
                        //back to JobsListGroupActivity
                        Intent parentIntent = new Intent(this, JobsListGroupActivity.class);
                        com.xdel.luke.MainActivity.uponLogin = 0;
                        startActivity(parentIntent);
                        this.finish();
                    } else {
                        //back to DashboardSubActivity or AddressListRoutingActivity or AddressListNewActivity or DrawerMainActivity
                        if (!objGlobalMethods.getGCMPrefs(GcmPreferences, "isFromDashboardSub").isEmpty()) {
                            //back to DashboardSubActivity or AddressListRoutingActivity
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");

                            Integer TotalPIPDIPJobsCountPerPostal = DAO_Jobs.getTotalPIPDIPJobsCountPerPostal(Postal);
                            if (TotalPIPDIPJobsCountPerPostal > 0) {
                                Intent parentIntent = new Intent(this, DashboardSubActivity.class);
                                MainActivity.uponLogin = 0;
                                startActivity(parentIntent);
                                this.finish();
                            } else {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                                objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                                objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                                Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                                MainActivity.uponLogin = 0;
                                startActivity(parentIntent);
                                this.finish();
                            }
                        } else {
                            //back to AddressListNewActivity or DrawerMainActivity
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                            MainActivity.uponLogin = 0;
                            int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(catType);
                            if (TotalJobsCountPerCatType == 0) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                                Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                                startActivity(parentIntent);
                                this.finish();
                            } else {
                                objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                                objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                                Intent parentIntent = new Intent(this, AddressListNewActivity.class);
                                startActivity(parentIntent);
                                this.finish();
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
                        Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                        MainActivity.uponLogin = 0;
                        startActivity(parentIntent);
                        this.finish();
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                        MainActivity.uponLogin = 0;
                        int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(catType);
                        if (TotalJobsCountPerCatType == 0) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                            Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                            startActivity(parentIntent);
                            this.finish();
                        } else {
                            objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                            Intent parentIntent = new Intent(this, AddressListNewActivity.class);
                            startActivity(parentIntent);
                            this.finish();
                        }
                    }
                }
            }


        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at OtpActivity resetAddressListActivityhfJobsInfoArrJsonStrOpenLinkJob3. Return to Main Dashboard. Exception: " + e.getMessage().toString(), "", "", "", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
            objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
            objGlobalMethods.resetJobsListGroupScrollIndexNpostion(GcmPreferences);
            com.xdel.luke.MainActivity.uponLogin = 0;
            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
            Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            startActivity(parentIntent);
            this.finish();
        }
    }

    public void autoSkip() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.otperrverifylocked));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String[] prefName = null;
                    String[] prefvalue = null;
                    prefName = new String[]{"hideshow10to19NewForced"};
                    prefvalue = new String[]{"s"};
                    objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);

                    objGlobalMethods.writeToFile("OtpActivity autoSkip after 3 unsuccessful attempts made.", "", "", "", "");
                    startActivity(nextActivtyIntent);
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();


        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at OtpActivity autoSkip. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    public void btnSkip(View v) {
        try {
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            Gson gson = new Gson();
            boolean massOKP = !objGlobalMethods.IsEmptyString(lblMassOKPbool.getText().toString());

            if (massOKP) {
                String msg = "";
                String JobsIDXs = "";
                //String JobsNOs = "";
                objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
                if (!objGlobalMethods.IsEmptyString(objCheckedItemsJsonStr))
                    aobjCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);

                objCheckedItems ci = null;
                objJobInfo currJobInfo = null;
                String ManualPinVerificaionText = "";
                if (aobjCheckedItemsAL != null && aobjCheckedItemsAL.length > 0) {
                    for (int i = 0; i <= aobjCheckedItemsAL.length - 1; i++) {
                        ci = aobjCheckedItemsAL[i];
                        currJobInfo = ci.jobinfo;
                        JobsIDXs += !objGlobalMethods.IsEmptyString(JobsIDXs) ? "," + String.valueOf(currJobInfo.JobsIDX) : String.valueOf(currJobInfo.JobsIDX);
                        //JobsNOs += !objGlobalMethods.IsEmptyString(JobsNOs) ? "," + String.valueOf(currJobInfo.JobNo) : String.valueOf(currJobInfo.JobNo);
                    }
                    for (int i = 0; i <= aobjCheckedItemsAL.length - 1; i++) {
                        ci = aobjCheckedItemsAL[i];
                        currJobInfo = ci.jobinfo;
                        ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText();
                        if (ManualPinVerificaionText != null && !ManualPinVerificaionText.equals("")) {
                            //currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText + "\nCourier attempt to mass skip PIN verification.\n" + JobsNOs);
                            currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText + "\nCourier attempt to mass skip PIN verification.");
                        } else {
                            //currJobInfo.setManualPinVerificaionText("Courier attempt to mass skip PIN verification.\n" + JobsNOs);
                            currJobInfo.setManualPinVerificaionText("Courier attempt to mass skip PIN verification.");
                        }
                        aobjCheckedItemsAL[i].setJobinfo(currJobInfo);
                    }

                    objGlobalMethods.writeToFile("GC attempt to skip PIN verification for JobsIDXs: " + String.valueOf(JobsIDXs),
                            "", "", "", "");
                    objCheckedItemsJsonStr = gson.toJson(aobjCheckedItemsAL);
                    String[] prefName = null;
                    String[] prefvalue = null;
                    /*prefName = new String[]{objGlobalVariables.checkedItemsName, "hideshow10to19NewForced"};
                    prefvalue = new String[]{objCheckedItemsJsonStr, "s"};*/
                    prefName = new String[]{objGlobalVariables.checkedItemsName};
                    prefvalue = new String[]{objCheckedItemsJsonStr};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    skipOTPdialog sotp = new skipOTPdialog();
                    sotp.setMassOKP(true);
                    sotp.setAobjCheckedItemsAL(aobjCheckedItemsAL);
                    sotp.show(getSupportFragmentManager(), "sotp");
                }
            } else {
                objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                String ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText();
                objGlobalMethods.writeToFile("GC attempt to skip PIN verification for JobsIDX: " + String.valueOf(currJobInfo.JobsIDX), "", "", "", "");
                if (ManualPinVerificaionText != null && !ManualPinVerificaionText.equals("")) {
                    currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText + "\nCourier attempt to skip PIN verification.");
                } else {
                    currJobInfo.setManualPinVerificaionText("Courier attempt to skip PIN verification.");
                }
                String currJobInfoPrefStr = gson.toJson(currJobInfo);
                DAO_Jobs.updateJobInfoPrefStr(currJobInfo.JobsIDX, currJobInfoPrefStr);

                String[] prefName = null;
                String[] prefvalue = null;
                /*prefName = new String[]{"hfJobInfoJsonStr", "hideshow10to19NewForced"};
                prefvalue = new String[]{currJobInfoPrefStr, "s"};*/
                prefName = new String[]{"hfJobInfoJsonStr"};
                prefvalue = new String[]{currJobInfoPrefStr};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                skipOTPdialog sotp = new skipOTPdialog();
                sotp.show(getSupportFragmentManager(), "sotp");
            }

            /*SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            Gson gson = new Gson();
            objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            String ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText();
            objGlobalMethods.writeToFile("GC attempt to skip PIN verification for JobsIDX: " + String.valueOf(currJobInfo.JobsIDX), "", "", "", "");
            if (ManualPinVerificaionText != null && !ManualPinVerificaionText.equals("")) {
                currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText + "\nCourier attempt to skip PIN verification.");
            } else {
                currJobInfo.setManualPinVerificaionText("Courier attempt to skip PIN verification.");
            }
            String currJobInfoPrefStr = gson.toJson(currJobInfo);
            DAO_Jobs.updateJobInfoPrefStr(currJobInfo.JobsIDX, currJobInfoPrefStr);

            String[] prefName = null;
            String[] prefvalue = null;
            prefName = new String[]{"hfJobInfoJsonStr", "hideshow10to19NewForced"};
            prefvalue = new String[]{currJobInfoPrefStr, "s"};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
            skipOTPdialog sotp = new skipOTPdialog();
            sotp.show(getSupportFragmentManager(), "sotp");*/
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at OtpActivity btnSkip. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    public void btnBack_Click(View v) {
        back();
    }

    private void showDate(int day, int month, int year) {
        lbldate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        /*Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();*/
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg3, arg2 + 1, arg1);
                }
            };

    public void btnResendClick(View v) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.otpbuttontext_resendcfm));

            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //do nothing
                }
            });

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    objGlobalMethods.writeToFile("Courier attempted to resend OTP for JobsIDX: " + lblJobsIDX.getText().toString(), "", "", "", "");
                    resendOTP t = new resendOTP(Long.parseLong(lblJobsIDX.getText().toString()));
                    t.execute();
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            //1st alert dialog
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at OtpActivity btnResendClick for JobsIDX: " + lblJobsIDX.getText().toString() + ". Exception: " + e.getMessage().toString(), "", "", "", "");
            showErrorAlert(myApp.getAppContext().getString(R.string.pd_resendOTPerr));
        }
    }

    private class resendOTP extends AsyncTask<Void, Void, Boolean> {
        private final long JobsIDX;

        public resendOTP(long aJobsIDX) {
            this.JobsIDX = aJobsIDX;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean asd = true;
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";

            try {
                String RegID = objGlobalMethods.getRegistrationIdOnly();
                Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);

                asd = WebService.getSendPinResult(RegID, JobsIDX, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);

            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at OtpActivity resendOTP doInBackground. Excpetion: " + e.getMessage().toString(), "", "", "", "");
                asd = false;
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            String RouteListJsonStr = "";
            pd.dismiss();
            try {
                Context ctx = myApp.getAppContext();
                if (success == null || !success) {
                    objGlobalMethods.writeToFile(myApp.getAppContext().getString(R.string.pd_resendOTPerr) + " for JobsIDX: " + String.valueOf(JobsIDX), "", "", "", "");
                    //showDialog(myApp.getAppContext().getString(R.string.pd_resendOTPerr));
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.pd_resendOTPerr)).show();
                } else if (success) {
                    objGlobalMethods.writeToFile(myApp.getAppContext().getString(R.string.pd_resendOTPsuc) + " for JobsIDX: " + String.valueOf(JobsIDX), "", "", "", "");
                    //showDialog(myApp.getAppContext().getString(R.string.pd_resendOTPsuc));
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.pd_resendOTPsuc)).show();
                }
            } catch (Exception e) {
                pd.dismiss();
                objGlobalMethods.writeToFile("Exception caught at OtpActivity resendOTP onPostExecute. Excpetion: " + e.toString(), "", "", "", "");
                showDialog(myApp.getAppContext().getString(R.string.pd_resendOTPerr));
            }
        }
    }

    private SlideDateListener rtnSlideDateListener(final TextView lblDate) {
        return new SlideDateListener() {

            @Override
            public void onDateSet(Date date) {
                // Do something with the date. This Date object contains
                // the date and time that the user has selected.
                lblDate.setText(Dateformatter.format(date));
            }

            @Override
            public void onDateCancel() {
                // Overriding onDateTimeCancel() is optional.
            }
        };
    }

    public static View.OnClickListener rtnlblDateOnClickListener(final SlideDateListener listener, final Date ddate) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new SlideDatePicker.Builder(manager)
                        .setListener(listener)
                        .setInitialDate(ddate != null ? ddate : new Date())
                        //.setMinDate(minDate)
                        //.setMaxDate(maxDate)
                        .setTheme(SlideDatePicker.HOLO_DARK)
                        //.setIndicatorColor(Color.parseColor("#990000"))
                        .build()
                        .show();
            }
        };
    }

    //for custom dialog box with custom interface n custom btns
    public void btnMVerifyClick(View v) {
        try {
            LayoutInflater layoutInflater = LayoutInflater.from(OtpActivity.this);
            View promptView = layoutInflater.inflate(R.layout.dialog_datepicker, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OtpActivity.this);

            // set prompts.xml to be the layout file of the alertdialog builder
            alertDialogBuilder.setView(promptView);

            final ImageView imgbtnDate = (ImageView) promptView.findViewById(R.id.imgbtnDate);
            final TextView lbldate = (TextView) promptView.findViewById(R.id.lbldate);
            final Button btnCancel = (Button) promptView.findViewById(R.id.btnCancel);
            final Button btnVerify = (Button) promptView.findViewById(R.id.btnVerify);

            // Show a datepicker when the dateButton is clicked
            imgbtnDate.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  //Calendar now = Calendar.getInstance();
                                                  final Calendar c = Calendar.getInstance();

                                                  DatePickerDialog dpd = new DatePickerDialog(OtpActivity.this,
                                                          new DatePickerDialog.OnDateSetListener() {

                                                              @Override
                                                              public void onDateSet(DatePicker view, int year,
                                                                                    int monthOfYear, int dayOfMonth) {
                                                                  lbldate.setText(dayOfMonth + "-"
                                                                          + (monthOfYear + 1) + "-" + year);

                                                              }
                                                          }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                                                  dpd.show();
                                              }
                                          }
            );

            lbldate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Calendar now = Calendar.getInstance();
                    final Calendar c = Calendar.getInstance();

                    DatePickerDialog dpd = new DatePickerDialog(OtpActivity.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    lbldate.setText(dayOfMonth + "-"
                                            + (monthOfYear + 1) + "-" + year);

                                }
                            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                    dpd.show();
                }
            });

            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String part1cnno = "";
                    String reason = "";
                    String Receiver = "PIN Verification";
                    String objUpdateCODPostDataListJsonStr = "";
                    List<objUpdateCOD_PostData> objUpdateCODPostDataList = null;
                    List<objJobInfo> objJobInfoBringtoUpdateCODList = null;
                    objJobInfo linkedjob = null;
                    String objJobInfoBringtoUpdateCODListJsonStr = "";
                    String mutlipleCODjobs = "";
                    Date dt = new Date();
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    SharedPreferences UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();

                    try {

                        Gson gson = new Gson();
                        objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                        String ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText();
                        if (ManualPinVerificaionText != null && !ManualPinVerificaionText.equals("")) {
                            currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText + "\n\nCourier chose manual verification. NRIC date of issue: " + lbldate.getText().toString());
                        } else {
                            currJobInfo.setManualPinVerificaionText("Courier chose manual verification. NRIC date of issue: " + lbldate.getText().toString());
                        }

                        String currJobInfoPrefStr = gson.toJson(currJobInfo);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", currJobInfoPrefStr);

                        Boolean JobhasCOD = false;
                        Boolean P2JobhasCOD = false;

                        JobhasCOD = JobsHandler.checkForCODwithCatType(currJobInfo, 2);
                        P2JobhasCOD = JobsHandler.checkPart2ForCOD(currJobInfo, GcmPreferences);

                        if (currJobInfo.ConsignmentNote != null && !currJobInfo.ConsignmentNote.equals("")) {
                            part1cnno = currJobInfo.ConsignmentNote;
                        } else {
                            part1cnno = currJobInfo.JobNo;
                        }

                        reason = currJobInfo.getManualPinVerificaionText();

                        if (JobhasCOD || P2JobhasCOD) {
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

                            String RegID = objGlobalMethods.getRegistrationIdOnly();
                            if (JobhasCOD && P2JobhasCOD) {
                                //p1 & 2 have COD
                                objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, currJobInfo, 2, true,
                                        RegID, false, part1cnno, Receiver);
                            } else if (!JobhasCOD && P2JobhasCOD) {
                                //p1 no COD, p2 has
                            } else if (JobhasCOD && !P2JobhasCOD) {
                                //p1 has COD, p2 no COD
                                objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, currJobInfo, 2, true,
                                        RegID, false, part1cnno, Receiver);
                            }

                            currJobInfo.setpendingStatusUpdate(135);
                            currJobInfo.setReceiver(Receiver);
                            currJobInfo.setFailureReason("");
                            currJobInfo.setconvertedBitmapToBase64(null);
                            currJobInfo.setSignatureStr("");

                            objJobInfoBringtoUpdateCODList = JobsHandler.RTNobjJobInfoBringtoUpdateCODList(objJobInfoBringtoUpdateCODList, currJobInfo);

                            if (currJobInfo.LinkedJobs != null && currJobInfo.LinkedJobs.length > 0 && currJobInfo.LinkedJobs[0] != null) {
                                linkedjob = currJobInfo.LinkedJobs[0];
                            }

                            if (linkedjob != null) {
                                // has p2, prompt update p2
                                promptToUpdateP2(linkedjob, objJobInfoBringtoUpdateCODList, objUpdateCODPostDataList, JobhasCOD, P2JobhasCOD, part1cnno);
                            } else {
                                //no p2, no prompt, str8 to UpdateCOD_List
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

                                //ojisPage == 0 == JobInfoActivity | ojisPage == 1 == JobsListGroupActivity | ojisPage == 2 == setDeliveredStatusActivity |
                                // ojisPage == 3 == setDeliveredMassStatusActivity | ojisPage == 4 == MassUpdateOKPwP2 | ojisPage == 5 == OtpActivity
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "ojisPage", "5");

                                Intent intent = new Intent(OtpActivity.this, UpdateCOD_List.class);
                                startActivity(intent);
                                OtpActivity.this.finish();
                            }
                        } else if (!JobhasCOD && !P2JobhasCOD) {
                            Long l = dt.getTime();
                            String timestampstr = "/Date(" + l + ")/";
                            String RegID = objGlobalMethods.getRegistrationIdOnly();
                            submitOKP(GcmPreferences, UpdateJobInfoPreferences, RegID, currJobInfo.JobsIDX, timestampstr, "PIN Verification", currJobInfo.getManualPinVerificaionText(), currJobInfo);
                        }
                    } catch (Exception e) {

                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alertD != null) {
                        alertD.dismiss();
                    }
                }
            });


            alertDialogBuilder
                    .setCancelable(false);

            // create an alert dialog
            alertD = alertDialogBuilder.create();

            alertD.show();


        } catch (Exception e) {
            Date dt = new Date();

        }
    }

    public String getEnteredPin() {
        String v = "";
        try {
            v = (!objGlobalMethods.IsEmptyString(txt01.getText().toString()) ? txt01.getText().toString() : "") +
                    (!objGlobalMethods.IsEmptyString(txt02.getText().toString()) ? txt02.getText().toString() : "") +
                    (!objGlobalMethods.IsEmptyString(txt03.getText().toString()) ? txt03.getText().toString() : "") +
                    (!objGlobalMethods.IsEmptyString(txt04.getText().toString()) ? txt04.getText().toString() : "") +
                    (!objGlobalMethods.IsEmptyString(txt05.getText().toString()) ? txt05.getText().toString() : "") +
                    (!objGlobalMethods.IsEmptyString(txt06.getText().toString()) ? txt06.getText().toString() : "");
        } catch (Exception e) {
            String ss = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at OtpActivity getEnteredPin. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
        } finally {
            return v;
        }
    }

    private void addOnFocusChangeListener(EditText txt) {
        txt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) v).setText("");
                    int cid = v.getId();
                    if (cid == R.id.txt01) {
                        mCustomKeyboard.registerEditText(R.id.txt01, 0);
                    } else if (cid == R.id.txt02) {
                        mCustomKeyboard.registerEditText(R.id.txt02, R.id.txt01);
                    } else if (cid == R.id.txt03) {
                        mCustomKeyboard.registerEditText(R.id.txt03, R.id.txt02);
                    } else if (cid == R.id.txt04) {
                        mCustomKeyboard.registerEditText(R.id.txt04, R.id.txt03);
                    } else if (cid == R.id.txt05) {
                        mCustomKeyboard.registerEditText(R.id.txt05, R.id.txt04);
                    } else if (cid == R.id.txt06) {
                        mCustomKeyboard.registerEditText(R.id.txt06, R.id.txt05);
                    }
                }
            }
        });
    }

    private class TextChangeListener01 implements TextWatcher {
        private final String Pin;
        private final int pos;

        public TextChangeListener01(String apin, int apos) {
            this.Pin = apin;
            this.pos = apos;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                int pinlength = !objGlobalMethods.IsEmptyString(Pin) ? Pin.length() : 0;
                if (pinlength > 0) {
                    if (pos < pinlength) {
                        if (!objGlobalMethods.IsEmptyString(txt01.getText().toString())) {
                            if (txt02 != null) {
                                txt02.requestFocus();
                                txt02.setSelection(txt02.getText().length());
                                if (objGlobalMethods.IsEmptyString(txt02.getText().toString())) {
                                    //txt02.setOnKeyListener(new BackKeyOnKeyListener02(1));
                                } else {
                                    txt02.setText("");
                                }
                            }
                        }
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnVerify);
                    } else if (pos == pinlength) {
                        String v = getEnteredPin();
                        ctrlSettings.enableDisableSubmitButtonWithStyles(v.length() == pinlength ? true : false, btnVerify);
                        if (v.length() == pinlength) {
                            next();
                        }
                    }
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener01 onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                //txt01.setOnKeyListener(null);
            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener01 afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }

    private class TextChangeListener02 implements TextWatcher {
        private final String Pin;
        private final int pos;

        public TextChangeListener02(String apin, int apos) {
            this.Pin = apin;
            this.pos = apos;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                int pinlength = !objGlobalMethods.IsEmptyString(Pin) ? Pin.length() : 0;
                if (pinlength > 0) {
                    if (pos < pinlength) {
                        if (!objGlobalMethods.IsEmptyString(txt02.getText().toString())) {
                            if (txt03 != null) {
                                txt03.requestFocus();
                                txt03.setSelection(txt03.getText().length());
                                if (objGlobalMethods.IsEmptyString(txt03.getText().toString())) {
                                    //txt03.setOnKeyListener(new BackKeyOnKeyListener03(1));
                                } else {
                                    txt03.setText("");
                                }
                            }
                        }
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnVerify);
                    } else if (pos == pinlength) {
                        String v = getEnteredPin();
                        ctrlSettings.enableDisableSubmitButtonWithStyles(v.length() == pinlength ? true : false, btnVerify);
                        if (v.length() == pinlength) {
                            next();
                        }
                    }
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener02 onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                /*txt02.setOnKeyListener(null);
                if (objGlobalMethods.IsEmptyString(s.toString())) {
                    txt02.setOnKeyListener(new BackKeyOnKeyListener02(1));
                } else {
                    txt02.setOnKeyListener(new BackKeyOnKeyListener02(1));
                }*/
            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener02 afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }

    private class BackKeyOnKeyListener02 implements View.OnKeyListener {
        private final int lastlength;
        private int flength;

        public BackKeyOnKeyListener02(int alastlength) {
            this.lastlength = alastlength;
            this.flength = lastlength;
        }

        @Override
        public boolean onKey(View v, int keyCode,
                             KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (flength > 0) {
                    flength--;
                    return false;
                } else {
                    txt01.setText("");
                    txt01.requestFocus();
                    txt01.setSelection(txt01.getText().toString().trim().length());
                    /*if (!objGlobalMethods.IsEmptyString(txt01.getText().toString())) {
                        txt01.setOnKeyListener(null);
                    }
                    txt02.setOnKeyListener(null);*/
                }
                return true;
            }
            return false;//this line is important

        }
    }

    private class TextChangeListener03 implements TextWatcher {
        private final String Pin;
        private final int pos;

        public TextChangeListener03(String apin, int apos) {
            this.Pin = apin;
            this.pos = apos;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                int pinlength = !objGlobalMethods.IsEmptyString(Pin) ? Pin.length() : 0;
                if (pinlength > 0) {
                    if (pos < pinlength) {
                        if (!objGlobalMethods.IsEmptyString(txt03.getText().toString())) {
                            if (txt04 != null) {
                                txt04.requestFocus();
                                txt04.setSelection(txt04.getText().length());
                                if (objGlobalMethods.IsEmptyString(txt04.getText().toString())) {
                                    //txt04.setOnKeyListener(new BackKeyOnKeyListener04(1));
                                } else {
                                    txt04.setText("");
                                }
                            }
                        }
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnVerify);
                    } else if (pos == pinlength) {
                        String v = getEnteredPin();
                        ctrlSettings.enableDisableSubmitButtonWithStyles(v.length() == pinlength ? true : false, btnVerify);
                        if (v.length() == pinlength) {
                            next();
                        }
                    }
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener03 onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                /*txt03.setOnKeyListener(null);
                if (objGlobalMethods.IsEmptyString(s.toString())) {
                    txt03.setOnKeyListener(new BackKeyOnKeyListener03(1));
                } else {
                    txt03.setOnKeyListener(new BackKeyOnKeyListener03(1));
                }*/
            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener03 afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }

    private class BackKeyOnKeyListener03 implements View.OnKeyListener {
        private final int lastlength;
        private int flength;

        public BackKeyOnKeyListener03(int alastlength) {
            this.lastlength = alastlength;
            this.flength = lastlength;
        }

        @Override
        public boolean onKey(View v, int keyCode,
                             KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (flength > 0) {
                    flength--;
                    return false;
                } else {
                    txt02.setText("");
                    txt02.requestFocus();
                    txt02.setSelection(txt02.getText().toString().trim().length());
                    /*if (!objGlobalMethods.IsEmptyString(txt02.getText().toString())) {
                        txt02.setOnKeyListener(null);
                    } else {
                        txt02.setOnKeyListener(new BackKeyOnKeyListener02(1));
                    }
                    txt03.setOnKeyListener(null);*/
                }
                return true;
            }
            return false;//this line is important

        }
    }

    private class TextChangeListener04 implements TextWatcher {
        private final String Pin;
        private final int pos;

        public TextChangeListener04(String apin, int apos) {
            this.Pin = apin;
            this.pos = apos;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                int pinlength = !objGlobalMethods.IsEmptyString(Pin) ? Pin.length() : 0;
                if (pinlength > 0) {
                    if (pos < pinlength) {
                        if (!objGlobalMethods.IsEmptyString(txt04.getText().toString())) {
                            if (txt05 != null) {
                                txt05.requestFocus();
                                txt05.setSelection(txt05.getText().length());
                                if (objGlobalMethods.IsEmptyString(txt05.getText().toString())) {
                                    //txt05.setOnKeyListener(new BackKeyOnKeyListener05(1));
                                } else {
                                    txt05.setText("");
                                }
                            }
                        }
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnVerify);
                    } else if (pos == pinlength) {
                        String v = getEnteredPin();
                        ctrlSettings.enableDisableSubmitButtonWithStyles(v.length() == pinlength ? true : false, btnVerify);
                        if (v.length() == pinlength) {
                            next();
                        }
                    }
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener04 onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                /*txt04.setOnKeyListener(null);
                if (objGlobalMethods.IsEmptyString(s.toString())) {
                    txt04.setOnKeyListener(new BackKeyOnKeyListener04(1));
                } else {
                    txt04.setOnKeyListener(new BackKeyOnKeyListener04(1));
                }*/
            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener04 afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }

    private class BackKeyOnKeyListener04 implements View.OnKeyListener {
        private final int lastlength;
        private int flength;

        public BackKeyOnKeyListener04(int alastlength) {
            this.lastlength = alastlength;
            this.flength = lastlength;
        }

        @Override
        public boolean onKey(View v, int keyCode,
                             KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (flength > 0) {
                    flength--;
                    return false;
                } else {
                    txt03.setText("");
                    txt03.requestFocus();
                    txt03.setSelection(txt03.getText().toString().trim().length());
                    /*if (!objGlobalMethods.IsEmptyString(txt03.getText().toString())) {
                        txt03.setOnKeyListener(null);
                    } else {
                        txt03.setOnKeyListener(new BackKeyOnKeyListener03(1));
                    }
                    txt04.setOnKeyListener(null);*/
                }
                return true;
            }
            return false;//this line is important

        }
    }

    private class TextChangeListener05 implements TextWatcher {
        private final String Pin;
        private final int pos;

        public TextChangeListener05(String apin, int apos) {
            this.Pin = apin;
            this.pos = apos;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                int pinlength = !objGlobalMethods.IsEmptyString(Pin) ? Pin.length() : 0;
                if (pinlength > 0) {
                    if (pos < pinlength) {
                        if (!objGlobalMethods.IsEmptyString(txt05.getText().toString())) {
                            if (txt06 != null) {
                                txt06.requestFocus();
                                txt06.setSelection(txt06.getText().length());
                                if (objGlobalMethods.IsEmptyString(txt06.getText().toString())) {
                                    //txt06.setOnKeyListener(new BackKeyOnKeyListener06(1));
                                } else {
                                    txt06.setText("");
                                }
                            }
                        }
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnVerify);
                    } else if (pos == pinlength) {
                        String v = getEnteredPin();
                        ctrlSettings.enableDisableSubmitButtonWithStyles(v.length() == pinlength ? true : false, btnVerify);
                        if (v.length() == pinlength) {
                            next();
                        }
                    }
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener05 onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                /*txt05.setOnKeyListener(null);
                if (objGlobalMethods.IsEmptyString(s.toString())) {
                    txt05.setOnKeyListener(new BackKeyOnKeyListener05(1));
                } else {
                    txt05.setOnKeyListener(new BackKeyOnKeyListener05(1));
                }*/
            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener05 afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }

    private class BackKeyOnKeyListener05 implements View.OnKeyListener {
        private final int lastlength;
        private int flength;

        public BackKeyOnKeyListener05(int alastlength) {
            this.lastlength = alastlength;
            this.flength = lastlength;
        }

        @Override
        public boolean onKey(View v, int keyCode,
                             KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (flength > 0) {
                    flength--;
                    return false;
                } else {
                    txt04.setText("");
                    txt04.requestFocus();
                    txt04.setSelection(txt04.getText().toString().trim().length());
                    /*if (!objGlobalMethods.IsEmptyString(txt04.getText().toString())) {
                        txt04.setOnKeyListener(null);
                    } else {
                        txt04.setOnKeyListener(new BackKeyOnKeyListener04(1));
                    }
                    txt05.setOnKeyListener(null);*/
                }
                return true;
            }
            return false;//this line is important

        }
    }

    private class TextChangeListener06 implements TextWatcher {
        private final String Pin;

        public TextChangeListener06(String apin) {
            this.Pin = apin;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                int pinlength = !objGlobalMethods.IsEmptyString(Pin) ? Pin.length() : 0;
                String v = getEnteredPin();
                ctrlSettings.enableDisableSubmitButtonWithStyles(v.length() == pinlength ? true : false, btnVerify);
                if (v.length() == pinlength) {
                    next();
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener06 onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                /*txt06.setOnKeyListener(null);
                if (objGlobalMethods.IsEmptyString(s.toString())) {
                    txt06.setOnKeyListener(new BackKeyOnKeyListener06(1));
                } else {
                    txt06.setOnKeyListener(new BackKeyOnKeyListener06(1));
                }*/
            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener06 afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }

    private class BackKeyOnKeyListener06 implements View.OnKeyListener {
        private final int lastlength;
        private int flength;

        public BackKeyOnKeyListener06(int alastlength) {
            this.lastlength = alastlength;
            this.flength = lastlength;
        }

        @Override
        public boolean onKey(View v, int keyCode,
                             KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (flength > 0) {
                    flength--;
                    return false;
                } else {
                    txt05.setText("");
                    txt05.requestFocus();
                    txt05.setSelection(txt05.getText().toString().trim().length());
                    /*if (!objGlobalMethods.IsEmptyString(txt05.getText().toString())) {
                        txt05.setOnKeyListener(null);
                    } else {
                        txt05.setOnKeyListener(new BackKeyOnKeyListener05(1));
                    }
                    txt06.setOnKeyListener(null);*/
                }
                return true;
            }
            return false;//this line is important

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
                    objGlobalMethods.writeToFile("Bluetooth is on at OTPActivity", "", "", "", "");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at OTPActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }


    private class TextChangeListener01_ implements TextWatcher {
        private final ArrayList<String> Pins;
        private final int pos;

        public TextChangeListener01_(ArrayList<String> apin, int apos) {
            this.Pins = apin;
            this.pos = apos;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                int pinlength = Pins != null && Pins.size() > 0 ? Pins.get(0).length() : 0;
                if (pinlength > 0) {
                    if (pos < pinlength) {
                        if (!objGlobalMethods.IsEmptyString(txt01.getText().toString())) {
                            if (txt02 != null) {
                                txt02.requestFocus();
                                txt02.setSelection(txt02.getText().length());
                                if (objGlobalMethods.IsEmptyString(txt02.getText().toString())) {
                                    //txt02.setOnKeyListener(new BackKeyOnKeyListener02(1));
                                } else {
                                    txt02.setText("");
                                }
                            }
                        }
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnVerify);
                    } else if (pos == pinlength) {
                        String v = getEnteredPin();
                        ctrlSettings.enableDisableSubmitButtonWithStyles(v.length() == pinlength ? true : false, btnVerify);
                        if (v.length() == pinlength) {
                            next2();
                        }
                    }
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener01_ onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                //txt01.setOnKeyListener(null);
            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener01_ afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }

    private class TextChangeListener02_ implements TextWatcher {
        private final ArrayList<String> Pins;
        private final int pos;

        public TextChangeListener02_(ArrayList<String> apin, int apos) {
            this.Pins = apin;
            this.pos = apos;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                int pinlength = Pins != null && Pins.size() > 0 ? Pins.get(0).length() : 0;
                if (pinlength > 0) {
                    if (pos < pinlength) {
                        if (!objGlobalMethods.IsEmptyString(txt02.getText().toString())) {
                            if (txt03 != null) {
                                txt03.requestFocus();
                                txt03.setSelection(txt03.getText().length());
                                if (objGlobalMethods.IsEmptyString(txt03.getText().toString())) {
                                    //txt03.setOnKeyListener(new BackKeyOnKeyListener03(1));
                                } else {
                                    txt03.setText("");
                                }
                            }
                        }
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnVerify);
                    } else if (pos == pinlength) {
                        String v = getEnteredPin();
                        ctrlSettings.enableDisableSubmitButtonWithStyles(v.length() == pinlength ? true : false, btnVerify);
                        if (v.length() == pinlength) {
                            next2();
                        }
                    }
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener02_ onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {

            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener02_ afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }

    private class TextChangeListener03_ implements TextWatcher {
        private final ArrayList<String> Pins;
        private final int pos;

        public TextChangeListener03_(ArrayList<String> apin, int apos) {
            this.Pins = apin;
            this.pos = apos;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                int pinlength = Pins != null && Pins.size() > 0 ? Pins.get(0).length() : 0;
                if (pinlength > 0) {
                    if (pos < pinlength) {
                        if (!objGlobalMethods.IsEmptyString(txt03.getText().toString())) {
                            if (txt04 != null) {
                                txt04.requestFocus();
                                txt04.setSelection(txt04.getText().length());
                                if (objGlobalMethods.IsEmptyString(txt04.getText().toString())) {
                                    //txt04.setOnKeyListener(new BackKeyOnKeyListener04(1));
                                } else {
                                    txt04.setText("");
                                }
                            }
                        }
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnVerify);
                    } else if (pos == pinlength) {
                        String v = getEnteredPin();
                        ctrlSettings.enableDisableSubmitButtonWithStyles(v.length() == pinlength ? true : false, btnVerify);
                        if (v.length() == pinlength) {
                            next2();
                        }
                    }
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener03_ onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                /*txt03.setOnKeyListener(null);
                if (objGlobalMethods.IsEmptyString(s.toString())) {
                    txt03.setOnKeyListener(new BackKeyOnKeyListener03(1));
                } else {
                    txt03.setOnKeyListener(new BackKeyOnKeyListener03(1));
                }*/
            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener03_ afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }

    private class TextChangeListener04_ implements TextWatcher {
        private final ArrayList<String> Pins;
        private final int pos;

        public TextChangeListener04_(ArrayList<String> apin, int apos) {
            this.Pins = apin;
            this.pos = apos;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                int pinlength = Pins != null && Pins.size() > 0 ? Pins.get(0).length() : 0;
                if (pinlength > 0) {
                    if (pos < pinlength) {
                        if (!objGlobalMethods.IsEmptyString(txt04.getText().toString())) {
                            if (txt05 != null) {
                                txt05.requestFocus();
                                txt05.setSelection(txt05.getText().length());
                                if (objGlobalMethods.IsEmptyString(txt05.getText().toString())) {
                                    //txt05.setOnKeyListener(new BackKeyOnKeyListener05(1));
                                } else {
                                    txt05.setText("");
                                }
                            }
                        }
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnVerify);
                    } else if (pos == pinlength) {
                        String v = getEnteredPin();
                        ctrlSettings.enableDisableSubmitButtonWithStyles(v.length() == pinlength ? true : false, btnVerify);
                        if (v.length() == pinlength) {
                            next2();
                        }
                    }
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener04_ onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                /*txt04.setOnKeyListener(null);
                if (objGlobalMethods.IsEmptyString(s.toString())) {
                    txt04.setOnKeyListener(new BackKeyOnKeyListener04(1));
                } else {
                    txt04.setOnKeyListener(new BackKeyOnKeyListener04(1));
                }*/
            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener04_ afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }

    private class TextChangeListener05_ implements TextWatcher {
        private final ArrayList<String> Pins;
        private final int pos;

        public TextChangeListener05_(ArrayList<String> apin, int apos) {
            this.Pins = apin;
            this.pos = apos;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                int pinlength = Pins != null && Pins.size() > 0 ? Pins.get(0).length() : 0;
                if (pinlength > 0) {
                    if (pos < pinlength) {
                        if (!objGlobalMethods.IsEmptyString(txt05.getText().toString())) {
                            if (txt06 != null) {
                                txt06.requestFocus();
                                txt06.setSelection(txt06.getText().length());
                                if (objGlobalMethods.IsEmptyString(txt06.getText().toString())) {
                                    //txt06.setOnKeyListener(new BackKeyOnKeyListener06(1));
                                } else {
                                    txt06.setText("");
                                }
                            }
                        }
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnVerify);
                    } else if (pos == pinlength) {
                        String v = getEnteredPin();
                        ctrlSettings.enableDisableSubmitButtonWithStyles(v.length() == pinlength ? true : false, btnVerify);
                        if (v.length() == pinlength) {
                            next2();
                        }
                    }
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener05_ onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                /*txt05.setOnKeyListener(null);
                if (objGlobalMethods.IsEmptyString(s.toString())) {
                    txt05.setOnKeyListener(new BackKeyOnKeyListener05(1));
                } else {
                    txt05.setOnKeyListener(new BackKeyOnKeyListener05(1));
                }*/
            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener05_ afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }

    private class TextChangeListener06_ implements TextWatcher {
        private final ArrayList<String> Pins;

        public TextChangeListener06_(ArrayList<String> apin) {
            this.Pins = apin;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                int pinlength = Pins != null && Pins.size() > 0 ? Pins.get(0).length() : 0;
                String v = getEnteredPin();
                ctrlSettings.enableDisableSubmitButtonWithStyles(v.length() == pinlength ? true : false, btnVerify);
                if (v.length() == pinlength) {
                    next2();
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener06_ onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                /*txt06.setOnKeyListener(null);
                if (objGlobalMethods.IsEmptyString(s.toString())) {
                    txt06.setOnKeyListener(new BackKeyOnKeyListener06(1));
                } else {
                    txt06.setOnKeyListener(new BackKeyOnKeyListener06(1));
                }*/
            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at OtpActivity TextChangeListener06_ afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }


}
