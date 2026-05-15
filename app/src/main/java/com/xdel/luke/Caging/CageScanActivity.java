package com.xdel.luke.Caging;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.HRM_Leave.AMPM_PostItem;
import com.xdel.luke.HRM_Leave.AMPM_PostItemAdapter;
import com.xdel.luke.HRM_Leave.objAMPM;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CageScanActivity extends AppCompatActivity {
    static String cameraScannedCode;
    private ConnectionDetector cd;
    private ProgressDialog pd, pdCheck;
    private String langType = "en";
    private String username;
    private String password;
    private String useridx;
    private String ishomesuser;
    private String[] UsernamePwd = new String[4];
    private MediaPlayer player;
    private TextView lblCageIDX, lblCageLocation, lblItemsAlreadyCagedInCount, lblCageScanSubmitIsHomesUser, lblSelectedHr, lblSelectedMin, lblSelectedAMPM, lblScanCageBarcodeVal, lblitemreadyaftertext, lbldateslot, lbltimecolon0, lblScanCageDate, lbltimeslot, lbltimecolon, lblinstructions;
    private static SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    private static EditText txtCageBarcodeMANUALENTRY;
    private Button btnScanCageBarcodeSubmitENTER, btnScanCageCANCEL, btnScanCageNext;
    private LinearLayout LLScanCageHomes1, LLScanCageHomes2, LLScanCageHomes3, LLScanCageHomes4, LLScanCageHomes5, LLScanCageHomes6;
    private Spinner ddlHr, ddlMin, ddlAMPM;
    SimpleDateFormat aformat = new SimpleDateFormat("dd/MM/yyyy");
    DatePickerDialog picker;
    ScreenShotContentObserver screenShotContentObserver;
    private Uri errToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error);
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);
    TextView lblIsDefaultVRPpage;

    private void stopPlayingTone(){
        try {
            if (player != null) {
                player.reset();
                //player.prepare();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity stopPlayingTone. Exception: " + e.getMessage(),"","","","");
        }
    }

    private void playTone() {
        /*player = MediaPlayer.create(CageScanActivity.this, R.raw.error);
        player.start();*/
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, errToneURI);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at CageScanActivity playTone. Exception: " + e.getMessage(),"","","","");
        }
    }

    private void playSuccessVerifiedTone() {
        try {
            stopPlayingTone();
            /*player = MediaPlayer.create(myApp.currentActivity, R.raw.doorbell);
            player.prepare();
            player.start();*/

            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, verifiedToneURI);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at CageInOutActivity playSuccessVerifiedTone. Exception: " + e.getMessage(),"","","","");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
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

        setContentView(R.layout.activity_cage_scan);

        lblCageIDX = (TextView) findViewById(R.id.lblCageIDX);
        lblCageLocation = (TextView) findViewById(R.id.lblCageLocation);
        lblItemsAlreadyCagedInCount = (TextView) findViewById(R.id.lblItemsAlreadyCagedInCount);
        lblCageScanSubmitIsHomesUser = (TextView) findViewById(R.id.lblCageScanSubmitIsHomesUser);
        lblSelectedHr = (TextView) findViewById(R.id.lblSelectedHr);
        lblSelectedMin = (TextView) findViewById(R.id.lblSelectedMin);
        lblSelectedAMPM = (TextView) findViewById(R.id.lblSelectedAMPM);
        txtCageBarcodeMANUALENTRY = (EditText) findViewById(R.id.txtCageBarcodeMANUALENTRY);
        lblScanCageBarcodeVal = (TextView) findViewById(R.id.lblScanCageBarcodeVal);
        lblitemreadyaftertext = (TextView) findViewById(R.id.lblitemreadyaftertext);
        lbldateslot = (TextView) findViewById(R.id.lbldateslot);
        lbltimecolon0 = (TextView) findViewById(R.id.lbltimecolon0);
        lblScanCageDate = (TextView) findViewById(R.id.lblScanCageDate);
        lbltimeslot = (TextView) findViewById(R.id.lbltimeslot);
        lbltimecolon = (TextView) findViewById(R.id.lbltimecolon);
        lblinstructions = (TextView) findViewById(R.id.lblinstructions);

        btnScanCageBarcodeSubmitENTER = (Button) findViewById(R.id.btnScanCageBarcodeSubmitENTER);
        btnScanCageCANCEL = (Button) findViewById(R.id.btnScanCageCANCEL);
        btnScanCageNext = (Button) findViewById(R.id.btnScanCageNext);

        LLScanCageHomes1 = (LinearLayout) findViewById(R.id.LLScanCageHomes1);
        LLScanCageHomes2 = (LinearLayout) findViewById(R.id.LLScanCageHomes2);
        LLScanCageHomes3 = (LinearLayout) findViewById(R.id.LLScanCageHomes3);
        LLScanCageHomes4 = (LinearLayout) findViewById(R.id.LLScanCageHomes4);
        LLScanCageHomes5 = (LinearLayout) findViewById(R.id.LLScanCageHomes5);
        LLScanCageHomes6 = (LinearLayout) findViewById(R.id.LLScanCageHomes6);

        ddlHr = (Spinner) findViewById(R.id.ddlHr);
        ddlMin = (Spinner) findViewById(R.id.ddlMin);
        ddlAMPM = (Spinner) findViewById(R.id.ddlAMPM);

        pdCheck = new ProgressDialog(this);
        pdCheck.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdCheck.setCanceledOnTouchOutside(false);
        pdCheck.setMessage(myApp.getAppContext().getString(R.string.verifycage));

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_wait_loading));

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
            Context ctx = myApp.getAppContext();
            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;

                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];
                Boolean IsHOMESuser = objGlobalMethods.IsHomesUserString(ishomesuser, GcmPreferences);
                setTitle(ctx.getString(R.string.scancage));

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
                    objGlobalMethods.writeToFile("CageScanActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

                DashboardSubActivity.frompage = null;

                boolean idledMoreThan3Hrs = JobsHandler.idledMoreThan3Hrs_new();
                if (idledMoreThan3Hrs) {
                    objGlobalMethods.writeToFile("Idled more than 3 hours. Manual force logout.","","","","");
                    JobsHandler.ManualForceLogout();
                }

                //cd = new ConnectionDetector(ctx);
                cd = new ConnectionDetector(null);

                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    //Toast.makeText(context, "Please connect to working Internet connection.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(ctx, ctx.getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }

                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
                initPage(IsHOMESuser, GcmPreferences);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageScanActivity onResume. Exception: " + e.getMessage(), "", "", "", "");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            lblCageIDX = null;
            lblCageLocation = null;
            lblItemsAlreadyCagedInCount = null;
            lblCageScanSubmitIsHomesUser = null;
            lblSelectedHr = null;
            lblSelectedMin = null;
            lblSelectedAMPM = null;
            lblScanCageBarcodeVal = null;
            lblitemreadyaftertext = null;
            lbldateslot = null;
            lbltimecolon0 = null;
            lblScanCageDate = null;
            lbltimeslot = null;
            lbltimecolon = null;
            lblinstructions = null;
            txtCageBarcodeMANUALENTRY = null;
            btnScanCageBarcodeSubmitENTER = null;
            btnScanCageCANCEL = null;
            btnScanCageNext = null;
            LLScanCageHomes1 = null;
            LLScanCageHomes2 = null;
            LLScanCageHomes3 = null;
            LLScanCageHomes4 = null;
            LLScanCageHomes5 = null;
            LLScanCageHomes6 = null;
            ddlHr = null;
            ddlMin = null;
            ddlAMPM = null;

            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
        DAO_Jobs.nullifystaticitems();
    }

    private void showChangeAirplaneModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_airplanemode));

        //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at CageScanActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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

        //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Date dt = new Date();
                    objGlobalMethods.writeToFile("ActivityNotFoundException at CageScanActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at CageScanActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at CageScanActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
        GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "cagescan");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        parentIntent = new Intent(CageScanActivity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cage_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        if (item.getItemId() == R.id.qrscan) {
            launchQRScan();
        }

        /*Intent parentIntent;
        switch (item.getItemId()) {
            case R.id.qrscan:
                launchQRScan();
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageScanActivity onPause unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
            }
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        //Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    private void initPage(boolean IsHOMESuser, SharedPreferences gcmp) {
        Context ctx = myApp.getAppContext();
        String cagebarcode = "";
        String CageIDX = "";
        String CageLocation = "";
        String HQ_total_CageIn = "0";
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            CageIDX = objGlobalMethods.getGCMPrefs(gcmp, "CageIDX");
            cagebarcode = objGlobalMethods.getGCMPrefs(gcmp, "cagebarcode");
            CageLocation = objGlobalMethods.getGCMPrefs(gcmp, "CageLocation");
            HQ_total_CageIn = objGlobalMethods.getGCMPrefs(gcmp, "HQ_total_CageIn");
            lblCageLocation.setText(CageLocation);
            boolean validcagebarcodescanned = !cagebarcode.isEmpty() && !cagebarcode.equals("") && cagebarcode != "";
            lblCageIDX.setText(CageIDX);
            lblCageScanSubmitIsHomesUser.setText(IsHOMESuser ? "true" : "false");
            lblItemsAlreadyCagedInCount.setVisibility(IsHOMESuser && validcagebarcodescanned ? View.VISIBLE : View.GONE);
            lblitemreadyaftertext.setVisibility(IsHOMESuser && validcagebarcodescanned ? View.VISIBLE : View.GONE);
            lbldateslot.setVisibility(IsHOMESuser && validcagebarcodescanned ? View.VISIBLE : View.GONE);
            lbltimecolon0.setVisibility(IsHOMESuser && validcagebarcodescanned ? View.VISIBLE : View.GONE);
            lblScanCageDate.setVisibility(IsHOMESuser && validcagebarcodescanned ? View.VISIBLE : View.GONE);
            lbltimeslot.setVisibility(IsHOMESuser && validcagebarcodescanned ? View.VISIBLE : View.GONE);
            lbltimecolon.setVisibility(IsHOMESuser && validcagebarcodescanned ? View.VISIBLE : View.GONE);
            ddlAMPM.setVisibility(IsHOMESuser && validcagebarcodescanned ? View.VISIBLE : View.GONE);
            lblinstructions.setVisibility(validcagebarcodescanned ? View.VISIBLE : View.GONE);
            lblScanCageBarcodeVal.setText(validcagebarcodescanned ? CageLocation.toUpperCase() : ctx.getString(R.string.pls_scan_cage));
            lblItemsAlreadyCagedInCount.setText(ctx.getString(R.string.totalallocated) + " " + HQ_total_CageIn);
            ctrlSettings.enableDisableSubmitButtonWithStyles(validcagebarcodescanned ? true : false, btnScanCageNext);

            if (IsHOMESuser) {
                loadAMPM();
                ddlAMPM.setOnItemSelectedListener(null);
                setPresetSelectedDate();
                ddlAMPM.setOnItemSelectedListener(rtnddlFromAMPMOnItemSelectedListener());
            }

            cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
            if (!cameraScannedCode.isEmpty() && !cameraScannedCode.equals("") && cameraScannedCode != "") {
                inputManualEntry(cameraScannedCode);
            }

            txtCageBarcodeMANUALENTRY.requestFocus();
            txtCageBarcodeMANUALENTRY.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                        return false;
                    if (actionId == 0) {
                        //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                        if (!txtCageBarcodeMANUALENTRY.getText().toString().isEmpty() && !txtCageBarcodeMANUALENTRY.getText().toString().equals("") && txtCageBarcodeMANUALENTRY.getText().toString() != "") {
                            inputManualEntry(txtCageBarcodeMANUALENTRY.getText().toString().toUpperCase());
                        } else {
                            Context ctx = myApp.getAppContext();
                            //objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.scan_enter_cage_barcode)).show();
                            showAlert(ctx.getString(R.string.scan_enter_cage_barcode)).show();
                        }

                        if (getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                    } else if (actionId == EditorInfo.IME_ACTION_GO) {
                        if (!txtCageBarcodeMANUALENTRY.getText().toString().isEmpty() && !txtCageBarcodeMANUALENTRY.getText().toString().equals("") && txtCageBarcodeMANUALENTRY.getText().toString() != "") {
                            inputManualEntry(txtCageBarcodeMANUALENTRY.getText().toString().toUpperCase());
                        } else {
                            Context ctx = myApp.getAppContext();
                            //objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.scan_enter_cage_barcode)).show();
                            showAlert(ctx.getString(R.string.scan_enter_cage_barcode)).show();
                        }
                        if (getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                    } else if (actionId == 6) {
                        return false;
                    }
                    return true;
                }
            });

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity initPage. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void loadHr() {
        Calendar now = Calendar.getInstance();
        int currHr = now.get(Calendar.HOUR);

        ArrayList<Hour_PostItem> list = new ArrayList<>();
        try {
            for (int i = 1; i <= 12; i++) {
                list.add(new Hour_PostItem(String.valueOf(i)));
            }
            Hour_PostItemAdapter ad = new Hour_PostItemAdapter(this, list);
            ddlHr.setAdapter(ad);

            //ddlHr.setOnItemSelectedListener(null);
            //ddlHr.setOnItemSelectedListener(rtnddlHrOnItemSelectedListener());
            ddlHr.setSelection(currHr - 1);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity loadHr. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private AdapterView.OnItemSelectedListener rtnddlHrOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    /*TextView lblStatusCodesIDX = (TextView) findViewById(R.id.lblStatusCodesIDX);
                    lblSelectedHr.setText(String.valueOf(position));*/

                    String ScanCage_Hr = objGlobalMethods.getGCMPrefs(GcmPreferences, "ScanCage_Hr");
                    int prevSelectionPosition = -1;
                    if (!ScanCage_Hr.isEmpty() && !ScanCage_Hr.equals("") && ScanCage_Hr != "") {
                        try {
                            prevSelectionPosition = Integer.parseInt(ScanCage_Hr);
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception at CageScanActivity rtnddlHrOnItemSelectedListener prevSelectionPosition = Integer.parseInt(ScanCage_Hr). Exception: " + e.getMessage(),"","","","");
                        }
                    }

                    int selectedMin = ddlMin.getSelectedItemPosition();
                    int selectedAMPM = ddlAMPM.getSelectedItemPosition();
                    Date selectedDate = objGlobalMethods.rtnddMMyyyyToDateObj(lblScanCageDate.getText().toString());
                    Date selectedDateTime = objGlobalMethods.toCalendarDateHrMinAMPM(selectedDate, position + 1, selectedMin, selectedAMPM).getTime();
                    Date now = objGlobalMethods.getZeroSecondsTimeDate(Calendar.getInstance().getTime());
                    int datesComparedRes = selectedDateTime.compareTo(now);
                    //0 comes when two date are same,
                    //1 comes when date1 is higher then date2
                    //-1 comes when date1 is lower then date2
                    if (datesComparedRes < 0) {
                        Calendar Cdrnow = objGlobalMethods.toCalendar(now);
                        int currHr = Cdrnow.get(Calendar.HOUR);
                        int currMin = Cdrnow.get(Calendar.MINUTE);
                        int currAMPM = Cdrnow.get(Calendar.AM_PM);

                        ddlHr.setOnItemSelectedListener(null);
                        ddlMin.setOnItemSelectedListener(null);
                        ddlAMPM.setOnItemSelectedListener(null);
                        ddlHr.setSelection(currHr - 1);
                        ddlMin.setSelection(currMin);
                        ddlAMPM.setSelection(currAMPM);
                        ddlHr.setOnItemSelectedListener(rtnddlHrOnItemSelectedListener());
                        ddlMin.setOnItemSelectedListener(rtnddlMinOnItemSelectedListener());
                        ddlAMPM.setOnItemSelectedListener(rtnddlFromAMPMOnItemSelectedListener());
                        lblSelectedHr.setText(String.valueOf(currHr - 1));
                        lblSelectedMin.setText(String.valueOf(currMin));
                        lblSelectedAMPM.setText(String.valueOf(currAMPM));
                    } else {
                        lblSelectedHr.setText(String.valueOf(position));
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at CageScanActivity rtnddlHrOnItemSelectedListener. Exception: " + e.getMessage(), "", "", "", "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    private void loadMin() {
        Calendar now = Calendar.getInstance();
        int currMin = now.get(Calendar.MINUTE);
        ArrayList<Min_PostItem> list = new ArrayList<>();
        try {
            for (int i = 0; i <= 59; i++) {
                list.add(new Min_PostItem(String.valueOf(i)));
            }
            Min_PostItemAdapter ad = new Min_PostItemAdapter(this, list);
            ddlMin.setAdapter(ad);

            //ddlMin.setOnItemSelectedListener(null);
            //ddlMin.setOnItemSelectedListener(rtnddlMinOnItemSelectedListener());
            ddlMin.setSelection(currMin);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity loadMin. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private AdapterView.OnItemSelectedListener rtnddlMinOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    /*TextView lblStatusCodesIDX = (TextView) findViewById(R.id.lblStatusCodesIDX);
                    lblSelectedMin.setText(String.valueOf(position));*/

                    String ScanCage_Min = objGlobalMethods.getGCMPrefs(GcmPreferences, "ScanCage_Min");
                    int prevSelectionPosition = -1;
                    if (!ScanCage_Min.isEmpty() && !ScanCage_Min.equals("") && ScanCage_Min != "") {
                        try {
                            prevSelectionPosition = Integer.parseInt(ScanCage_Min);
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception at CageScanActivity rtnddlMinOnItemSelectedListener prevSelectionPosition = Integer.parseInt(ScanCage_Min). Exception: " + e.getMessage(),"","","","");
                        }
                    }

                    int selectedHr = ddlHr.getSelectedItemPosition() + 1;
                    int selectedAMPM = ddlAMPM.getSelectedItemPosition();
                    Date selectedDate = objGlobalMethods.rtnddMMyyyyToDateObj(lblScanCageDate.getText().toString());
                    Date selectedDateTime = objGlobalMethods.toCalendarDateHrMinAMPM(selectedDate, selectedHr, position, selectedAMPM).getTime();
                    Date now = objGlobalMethods.getZeroSecondsTimeDate(Calendar.getInstance().getTime());
                    int datesComparedRes = selectedDateTime.compareTo(now);
                    //0 comes when two date are same,
                    //1 comes when date1 is higher then date2
                    //-1 comes when date1 is lower then date2
                    if (datesComparedRes < 0) {
                        Calendar Cdrnow = objGlobalMethods.toCalendar(now);
                        int currHr = Cdrnow.get(Calendar.HOUR);
                        int currMin = Cdrnow.get(Calendar.MINUTE);
                        int currAMPM = Cdrnow.get(Calendar.AM_PM);

                        ddlHr.setOnItemSelectedListener(null);
                        ddlMin.setOnItemSelectedListener(null);
                        ddlAMPM.setOnItemSelectedListener(null);
                        ddlHr.setSelection(currHr - 1);
                        ddlMin.setSelection(currMin);
                        ddlAMPM.setSelection(currAMPM);
                        ddlHr.setOnItemSelectedListener(rtnddlHrOnItemSelectedListener());
                        ddlMin.setOnItemSelectedListener(rtnddlMinOnItemSelectedListener());
                        ddlAMPM.setOnItemSelectedListener(rtnddlFromAMPMOnItemSelectedListener());
                        lblSelectedHr.setText(String.valueOf(currHr - 1));
                        lblSelectedMin.setText(String.valueOf(currMin));
                        lblSelectedAMPM.setText(String.valueOf(currAMPM));
                    } else {
                        lblSelectedMin.setText(String.valueOf(position));
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at CageScanActivity rtnddlMinOnItemSelectedListener. Exception: " + e.getMessage(), "", "", "", "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    private void loadAMPM() {
        Calendar now = Calendar.getInstance();
        int currAMPM = now.get(Calendar.AM_PM);
        AMPM_PostItem am, pm;
        ArrayList<AMPM_PostItem> list = new ArrayList<>();
        try {
            am = new AMPM_PostItem("AM", String.valueOf(objAMPM.AMPM.apAM.ordinal()));
            pm = new AMPM_PostItem("PM", String.valueOf(objAMPM.AMPM.apPM.ordinal()));
            list.add(am);
            list.add(pm);
            AMPM_PostItemAdapter adapterFrom = new AMPM_PostItemAdapter(this, list);
            AMPM_PostItemAdapter adapterTo = new AMPM_PostItemAdapter(this, list);
            ddlAMPM.setAdapter(adapterFrom);

            //ddlAMPM.setOnItemSelectedListener(null);
            //ddlAMPM.setOnItemSelectedListener(rtnddlFromAMPMOnItemSelectedListener());
            ddlAMPM.setSelection(0);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity loadAMPM. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private AdapterView.OnItemSelectedListener rtnddlFromAMPMOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    TextView lblStatusCodesIDX = (TextView) findViewById(R.id.lblStatusCodesIDX);
                    lblSelectedAMPM.setText(String.valueOf(position));
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "ScanCage_AMPM", lblSelectedAMPM.getText().toString());
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at CageScanActivity rtnddlFromAMPMOnItemSelectedListener. Exception: " + e.getMessage(), "", "", "", "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    private void setPresetSelectedDate(){
        try {
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            String ScanCage_ddMMyyyy = objGlobalMethods.getGCMPrefs(GcmPreferences, "ScanCage_ddMMyyyy");
            lblScanCageDate.setText(!ScanCage_ddMMyyyy.isEmpty() && !ScanCage_ddMMyyyy.equals("") && ScanCage_ddMMyyyy != "" ? ScanCage_ddMMyyyy : aformat.format(Calendar.getInstance().getTime()));

            String ScanCage_AMPM = objGlobalMethods.getGCMPrefs(GcmPreferences, "ScanCage_AMPM");
            if (!ScanCage_AMPM.isEmpty() && !ScanCage_AMPM.equals("") && ScanCage_AMPM != "") {
                try {
                    ddlAMPM.setSelection(Integer.parseInt(ScanCage_AMPM));
                } catch (Exception e) {

                }
            }

            String[] prefName = new String[]{"ScanCage_ddMMyyyy", "ScanCage_AMPM"};
            String[] prefvalue = new String[]{lblScanCageDate.getText().toString(), lblSelectedAMPM.getText().toString()};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity setPresetSelectedDate. Exception: " + e.getMessage(),"","","","");
        }
    }

    public void lblScanCageDateClick(View view) {
        try {
            Calendar cldr = objGlobalMethods.toCalendar(objGlobalMethods.rtnddMMyyyyToDateObj(lblScanCageDate.getText().toString()));
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            // date picker dialog
            picker = new DatePickerDialog(CageScanActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Date selectedDateTime = objGlobalMethods.toCalendar(year, monthOfYear, dayOfMonth).getTime();
                            lblScanCageDate.setText(aformat.format(selectedDateTime));
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "ScanCage_ddMMyyyy", lblScanCageDate.getText().toString());
                        }
                    }, year, month, day);

            picker.getDatePicker().setMinDate(new Date().getTime());
            picker.show();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity lblScanCageDateClick. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void btnScanCageCANCELClick(View view) {
        back();
    }

    public void btnScanCageNextClick(View view) {
        Context ctx = myApp.getAppContext();
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        try {
            String HQ_CageIn_ScheduledDateTimeStr = getScheduledDateTimeStr();
            String[] prefName = new String[]{"CageIDX", "CageLocation",
                    "ScanCage_ddMMyyyy", "ScanCage_AMPM", "HQ_CageIn_ScheduledDateTimeStr", "CageInOutScannedExcludedPrefStr", "CageInOutScannedExcludedPrefStr"};
            String[] prefvalue = new String[]{lblCageIDX.getText().toString(), lblScanCageBarcodeVal.getText().toString(),
                    lblScanCageDate.getText().toString(), lblSelectedAMPM.getText().toString(), HQ_CageIn_ScheduledDateTimeStr, "", ""};
            objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
            Intent nxt = new Intent(CageScanActivity.this, CageInOutActivity.class);
            startActivity(nxt);
            finish();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity btnScanCageNextClick. Exception: " + e.getMessage(),"","","","");
            showAlert(ctx.getString(R.string.alert_error)).show();
        }
    }

    private String getScheduledDateTimeStr(){
        String str = "";
        try {
            Date selectedDate = objGlobalMethods.rtnddMMyyyyToDateObj(lblScanCageDate.getText().toString());
            Calendar cal = objGlobalMethods.toCalendar(selectedDate);
            //cal.set(Calendar.AM_PM, ddlAMPM.getSelectedItemPosition());
            cal.set(Calendar.HOUR_OF_DAY, ddlAMPM.getSelectedItemPosition() == 0 ? 7 : 13);
            selectedDate = cal.getTime();
            Long l = selectedDate.getTime();
            str = "/Date(" + l + ")/";
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity getScheduledDateTimeStr. Exception: " + e.getMessage(),"","","","");
        } finally {
            return str;
        }
    }

    public void buttontext_changecageClick(View view) {
        final Context ctx = myApp.getAppContext();
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);

            builder.setMessage(ctx.getString(R.string.buttontext_cfm_changecage));

            builder.setPositiveButton(ctx.getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    Context ctx = myApp.getAppContext();
                    try {
                        //launchQRScan();
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnScanCageNext);
                        txtCageBarcodeMANUALENTRY.setText("");
                        lblScanCageBarcodeVal.setText(ctx.getString(R.string.pls_scan_cage));
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String[] prefName = new String[]{"CageIDX", "cagebarcode"};
                        String[] prefvalue = new String[]{"", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at CageScanActivity buttontext_changecageClick setPositiveButton. Exception: " + e.getMessage(),"","","","");
                        //objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_error)).show();
                        showAlert(ctx.getString(R.string.alert_error)).show();
                    }
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
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity buttontext_changecageClick. Exception: " + e.getMessage(), "", "", "", "");
            txtCageBarcodeMANUALENTRY.setText("");
            txtCageBarcodeMANUALENTRY.requestFocus();
        }
    }

    public void btnCageScanBarcodeSubmitENTERClick(View view){
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        if (!txtCageBarcodeMANUALENTRY.getText().toString().isEmpty() && !txtCageBarcodeMANUALENTRY.getText().toString().equals("") && txtCageBarcodeMANUALENTRY.getText().toString() != "") {
            inputManualEntry(txtCageBarcodeMANUALENTRY.getText().toString().toUpperCase());
        } else {
            Context ctx = myApp.getAppContext();
            playTone();
            showAlert(ctx.getString(R.string.scan_enter_cage_barcode)).show();
        }
    }

    private void inputManualEntry(String input) {
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        String IsHOMESuserString = "";
        boolean IsHOMESuser;
        try {
            /*boolean idledMoreThan3Hrs = JobsHandler.idledMoreThan3Hrs_new();
            idledMoreThan3Hrs = true;
            if (idledMoreThan3Hrs) {
                playTone();
                Context ctx = myApp.getAppContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                builder.setTitle("");
                builder.setCancelable(false);
                builder.setMessage("Idled more than 3 hours.\nForce logout.\n\nPlease login again.");
                builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application
                        objGlobalMethods.writeToFile("Idled more than 3 hours. Manual force logout.","","","","");
                        dialog.dismiss();
                        JobsHandler.ManualForceLogout();
                    }
                });

                AlertDialog alertdialog = builder.create();
                alertdialog.show();
            } else {
                objGlobalMethods.setGCMPrefs(gcmp, "cameraScannedCode", "");
                IsHOMESuserString = lblCageScanSubmitIsHomesUser.getText().toString();
                IsHOMESuser = IsHOMESuserString.equals("true") || IsHOMESuserString.equals("false") ? Boolean.parseBoolean(IsHOMESuserString) : false;
                if (IsHOMESuser) {
                    HQ_Get_Cage t = new HQ_Get_Cage(input);
                    t.execute();
                } else {
                    L2_Verify_Cage t = new L2_Verify_Cage(input);
                    t.execute();
                }
            }*/

            objGlobalMethods.setGCMPrefs(gcmp, "cameraScannedCode", "");
            IsHOMESuserString = lblCageScanSubmitIsHomesUser.getText().toString();
            IsHOMESuser = IsHOMESuserString.equals("true") || IsHOMESuserString.equals("false") ? Boolean.parseBoolean(IsHOMESuserString) : false;
            if (IsHOMESuser) {
                HQ_Get_Cage t = new HQ_Get_Cage(input);
                t.execute();
            } else {
                L2_Verify_Cage t = new L2_Verify_Cage(input);
                t.execute();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity inputManualEntry. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void hideshowHQctrls(boolean show) {
        try {
            lblitemreadyaftertext.setVisibility(show ? View.VISIBLE : View.GONE);
            lbldateslot.setVisibility(show ? View.VISIBLE : View.GONE);
            lbltimecolon0.setVisibility(show ? View.VISIBLE : View.GONE);
            lblScanCageDate.setVisibility(show ? View.VISIBLE : View.GONE);
            lbltimeslot.setVisibility(show ? View.VISIBLE : View.GONE);
            lbltimecolon.setVisibility(show ? View.VISIBLE : View.GONE);
            ddlAMPM.setVisibility(show ? View.VISIBLE : View.GONE);
            lblinstructions.setVisibility(show ? View.VISIBLE : View.GONE);
            lblItemsAlreadyCagedInCount.setVisibility(show ? View.VISIBLE : View.GONE);
            ctrlSettings.enableDisableSubmitButtonWithStyles(show, btnScanCageNext);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity hideshowHQctrls. Exception: " + e.getMessage(),"","","","");
        }
    }

    private class HQ_Get_Cage extends AsyncTask<Void, Void, objGet_CageResult> {
        final String WDBarcode;

        HQ_Get_Cage(String aWDBarcode) {
            this.WDBarcode = aWDBarcode;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdCheck.show();
        }

        @Override
        protected objGet_CageResult doInBackground(Void... params) {
            objGet_CageResult res = null;
            try {
                objGlobalMethods.writeToFile("Processing at CageScanActivity HQ_Get_Cage doInBackground. WDBarcode: " + WDBarcode,"","","","");
                res = WebService.Get_Cage(WDBarcode);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageScanActivity HQ_Get_Cage doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final objGet_CageResult res) {
            Context ctx = myApp.getAppContext();
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            int HQ_total_CageIn = 0;
            try {
                if (pdCheck != null) {
                    pdCheck.dismiss();
                }
                JobsHandler.setCurrDTasPrevDTforIdlingCheck();
                txtCageBarcodeMANUALENTRY.setText("");
                txtCageBarcodeMANUALENTRY.requestFocus();
                if (res != null && res.IDX > 0) {
                    playSuccessVerifiedTone();
                    objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " verified at CageScanActivity HQ_Get_Cage.","","","","");
                    lblCageIDX.setText(String.valueOf(res.IDX));
                    lblScanCageBarcodeVal.setText(res.Location);
                    if (res.Items_In_Cage != null && res.Items_In_Cage.length > 0) {
                        HQ_total_CageIn = res.Items_In_Cage.length;
                    }
                    lblItemsAlreadyCagedInCount.setText(ctx.getString(R.string.totalallocated) + " " + String.valueOf(HQ_total_CageIn));
                    objGlobalMethods.setGCMPrefs(gcmp, "cagebarcode", WDBarcode);
                    objGlobalMethods.setGCMPrefs(gcmp, "CageIDX", String.valueOf(res.IDX));
                    objGlobalMethods.setGCMPrefs(gcmp, "CageLocation", res.Location);
                    objGlobalMethods.setGCMPrefs(gcmp, "HQ_total_CageIn", String.valueOf(HQ_total_CageIn));
                    //hideshowHQctrls(true);
                    btnScanCageNextClick(null);
                } else {
                    objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " not verified at CageScanActivity HQ_Get_Cage. res.IDX < 0.","","","","");
                    lblCageIDX.setText("");
                    lblScanCageBarcodeVal.setText(ctx.getString(R.string.pls_scan_cage));
                    lblItemsAlreadyCagedInCount.setText(ctx.getString(R.string.totalallocated) + " 0");
                    objGlobalMethods.setGCMPrefs(gcmp, "cagebarcode", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "CageIDX", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "CageLocation", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "HQ_total_CageIn", "0");
                    hideshowHQctrls(false);
                    playTone();
                    //objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), WDBarcode + "\n\n" + ctx.getString(R.string.verifycageerro)).show();
                    showAlert(WDBarcode + "\n\n" + ctx.getString(R.string.verifycageerro)).show();
                }
            } catch (Exception e) {
                txtCageBarcodeMANUALENTRY.setText("");
                txtCageBarcodeMANUALENTRY.requestFocus();
                lblCageIDX.setText("");
                lblScanCageBarcodeVal.setText(ctx.getString(R.string.pls_scan_cage));
                lblItemsAlreadyCagedInCount.setText(ctx.getString(R.string.totalallocated) + " " + String.valueOf(HQ_total_CageIn));
                objGlobalMethods.setGCMPrefs(gcmp, "cagebarcode", "");
                objGlobalMethods.setGCMPrefs(gcmp, "CageIDX", "");
                objGlobalMethods.setGCMPrefs(gcmp, "CageLocation", "");
                objGlobalMethods.setGCMPrefs(gcmp, "HQ_total_CageIn", "0");
                hideshowHQctrls(false);
                playTone();
                objGlobalMethods.writeToFile("Exception at CageScanActivity HQ_Get_Cage onPostExecute for WDBarcode: " + WDBarcode + ". Exception: " + e.getMessage(), "", "", "", "");
                //objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.errorverifying)).show();
                showAlert(ctx.getString(R.string.errorverifying)).show();
            }
        }
    }

    private class L2_Verify_Cage extends AsyncTask<Void, Void, objL2_Verify_CageResult> {
        final String WDBarcode;

        L2_Verify_Cage(String aWDBarcode) {
            this.WDBarcode = aWDBarcode;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdCheck.show();
        }

        @Override
        protected objL2_Verify_CageResult doInBackground(Void... params) {
            objL2_Verify_CageResult res = null;
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            String regID = objGlobalMethods.getRegistrationIdOnly();
            try {
                objGlobalMethods.writeToFile("Processing at CageScanActivity L2_Verify_Cage doInBackground. WDBarcode: " + WDBarcode,"","","","");
                res = WebService.L2_Verify_Cage(regID, WDBarcode);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageScanActivity L2_Verify_Cage doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final objL2_Verify_CageResult res) {
            Context ctx = myApp.getAppContext();
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            try {
                if (pdCheck != null) {
                    pdCheck.dismiss();
                }
                JobsHandler.setCurrDTasPrevDTforIdlingCheck();
                txtCageBarcodeMANUALENTRY.setText("");
                txtCageBarcodeMANUALENTRY.requestFocus();
                if (res == null) {
                    objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " not verified at CageScanActivity L2_Verify_Cage. res is null. ","","","","");
                    lblCageIDX.setText("");
                    lblScanCageBarcodeVal.setText(ctx.getString(R.string.pls_scan_cage));
                    objGlobalMethods.setGCMPrefs(gcmp, "cagebarcode", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "CageIDX", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "CageLocation", "");
                    hideshowHQctrls(false);
                    playTone();
                    //objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.errorverifying)).show();
                    showAlert(ctx.getString(R.string.errorverifying)).show();
                } else if (res != null && res.Status != 0) {
                    objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " not verified at CageScanActivity L2_Verify_Cage. res.Message: " + res.Message,"","","","");
                    lblCageIDX.setText("");
                    lblScanCageBarcodeVal.setText(ctx.getString(R.string.pls_scan_cage));
                    objGlobalMethods.setGCMPrefs(gcmp, "cagebarcode", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "CageIDX", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "CageLocation", "");
                    hideshowHQctrls(false);
                    playTone();
                    //objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), res.Message).show();
                    showAlert(res.Message).show();
                } else if (res != null && res.Status == 0) {
                    objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " verified at CageScanActivity L2_Verify_Cage.","","","","");
                    L2_Get_Cage t = new L2_Get_Cage(WDBarcode);
                    t.execute();
                }
            } catch (Exception e) {
                txtCageBarcodeMANUALENTRY.setText("");
                txtCageBarcodeMANUALENTRY.requestFocus();
                lblCageIDX.setText("");
                lblScanCageBarcodeVal.setText(ctx.getString(R.string.pls_scan_cage));
                objGlobalMethods.setGCMPrefs(gcmp, "cagebarcode", "");
                objGlobalMethods.setGCMPrefs(gcmp, "CageIDX", "");
                objGlobalMethods.setGCMPrefs(gcmp, "CageLocation", "");
                hideshowHQctrls(false);
                playTone();
                objGlobalMethods.writeToFile("Exception at CageScanActivity L2_Verify_Cage onPostExecute for WDBarcode: " + WDBarcode + ". Exception: " + e.getMessage(), "", "", "", "");
                //objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.errorverifying)).show();
                showAlert(ctx.getString(R.string.errorverifying)).show();
            }
        }
    }

    private class L2_Get_Cage extends AsyncTask<Void, Void, objL2_Get_CageResult> {
        final String WDBarcode;

        L2_Get_Cage(String aWDBarcode) {
            this.WDBarcode = aWDBarcode;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected objL2_Get_CageResult doInBackground(Void... params) {
            objL2_Get_CageResult res = null;
            String regID = objGlobalMethods.getRegistrationIdOnly();
            try {
                objGlobalMethods.writeToFile("Processing at CageScanActivity L2_Get_Cage doInBackground. WDBarcode: " + WDBarcode,"","","","");
                res = WebService.L2_Get_Cage(regID, WDBarcode);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageScanActivity L2_Get_Cage doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final objL2_Get_CageResult res) {
            Context ctx = myApp.getAppContext();
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            try {
                if (pd != null) {
                    pd.dismiss();
                }
                JobsHandler.setCurrDTasPrevDTforIdlingCheck();
                txtCageBarcodeMANUALENTRY.setText("");
                txtCageBarcodeMANUALENTRY.requestFocus();
                if (res == null) {
                    objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " not processed at CageScanActivity L2_Get_Cage. res is null.","","","","");
                    lblCageIDX.setText("");
                    lblScanCageBarcodeVal.setText(ctx.getString(R.string.pls_scan_cage));
                    objGlobalMethods.setGCMPrefs(gcmp, "cagebarcode", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "CageIDX", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "L2_Get_CageResult", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "CageLocation", "");
                    hideshowHQctrls(false);
                    playTone();
                    //objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.errorloadingdata)).show();
                    showAlert(ctx.getString(R.string.errorloadingdata)).show();
                } else if (res != null && res.IDX == 0) {
                    objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " not processed at CageScanActivity L2_Get_Cage. res.IDX == 0.","","","","");
                    lblCageIDX.setText("");
                    lblScanCageBarcodeVal.setText(ctx.getString(R.string.pls_scan_cage));
                    objGlobalMethods.setGCMPrefs(gcmp, "cagebarcode", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "CageIDX", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "L2_Get_CageResult", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "CageLocation", "");
                    hideshowHQctrls(false);
                    playTone();
                    //objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.errorloadingdata)).show();
                    showAlert(ctx.getString(R.string.errorloadingdata)).show();
                } else {
                    //objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " processed at CageScanActivity L2_Get_Cage.","","","","");

                    Gson gson = new Gson();
                    String L2_Get_CageResultJsonStr = gson.toJson(res);

                    if (res.Items_In_Cage != null && res.Items_In_Cage.length > 0) {
                        lblCageIDX.setText(String.valueOf(res.IDX));
                        lblScanCageBarcodeVal.setText(res.Location);
                        objGlobalMethods.setGCMPrefs(gcmp, "cagebarcode", WDBarcode);
                        objGlobalMethods.setGCMPrefs(gcmp, "CageIDX", String.valueOf(res.IDX));
                        objGlobalMethods.setGCMPrefs(gcmp, "L2_Get_CageResult", L2_Get_CageResultJsonStr);
                        objGlobalMethods.setGCMPrefs(gcmp, "CageLocation", res.Location);

                        objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " processed at CageScanActivity L2_Get_Cage.","","","","");
                        lblinstructions.setVisibility(View.VISIBLE);
                        ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnScanCageNext);
                    } else {
                        lblCageIDX.setText("");
                        lblScanCageBarcodeVal.setText(ctx.getString(R.string.pls_scan_cage));
                        objGlobalMethods.setGCMPrefs(gcmp, "cagebarcode", "");
                        objGlobalMethods.setGCMPrefs(gcmp, "CageIDX", "");
                        objGlobalMethods.setGCMPrefs(gcmp, "L2_Get_CageResult", "");
                        objGlobalMethods.setGCMPrefs(gcmp, "CageLocation", "");

                        objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " processed at CageScanActivity L2_Get_Cage. No Items in cage.","","","","");
                        lblinstructions.setVisibility(View.GONE);
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnScanCageNext);
                        playTone();
                        //objGlobalMethods.showDialog(myApp.currentActivity,ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.noitemincage) + " " + res.Location).show();
                        showAlert(ctx.getString(R.string.noitemincage) + " " + res.Location).show();
                    }
                }
            } catch (Exception e) {
                txtCageBarcodeMANUALENTRY.setText("");
                txtCageBarcodeMANUALENTRY.requestFocus();
                lblCageIDX.setText("");
                lblScanCageBarcodeVal.setText(ctx.getString(R.string.pls_scan_cage));
                objGlobalMethods.setGCMPrefs(gcmp, "cagebarcode", "");
                objGlobalMethods.setGCMPrefs(gcmp, "CageIDX", "");
                objGlobalMethods.setGCMPrefs(gcmp, "L2_Get_CageResult", "");
                objGlobalMethods.setGCMPrefs(gcmp, "CageLocation", "");
                hideshowHQctrls(false);
                objGlobalMethods.writeToFile("Exception at CageScanActivity L2_Get_Cage onPostExecute for WDBarcode: " + WDBarcode + ". Exception: " + e.getMessage(), "", "", "", "");
                playTone();
                //objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.errorverifying)).show();
                showAlert(ctx.getString(R.string.errorverifying)).show();
            }
        }
    }

    private Dialog showAlert(String msg){
        Context ctx = myApp.getAppContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setTitle("");
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                focusTextbox();
                dialog.dismiss();
            }
        });

        AlertDialog alertdialog = builder.create();
        return alertdialog;
    }

    public static void focusTextbox() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                txtCageBarcodeMANUALENTRY.setText("");
                txtCageBarcodeMANUALENTRY.requestFocus();
            }
        }, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at CageScanActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CageScanActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
