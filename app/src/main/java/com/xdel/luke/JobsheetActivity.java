package com.xdel.luke;

import android.app.AlertDialog;
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
import android.provider.MediaStore;
import android.provider.Settings;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;


//public class JobsheetActivity extends ActionBarActivity {
public class JobsheetActivity extends AppCompatActivity {
    Context context;
    ConnectionDetector cd;

    EditText txtJobsheetScanID;
    TextView lblJobsheetSCANNEDCURRENTID;
    TextView lblJobsheetSelectedButton, lbljsmode;
    Button btnJobsheetEnter;
    Button btnJobsheetWHSEC;
    Button btnJobsheetWHSEA;
    Button btnJobsheetWHSEPSCH;
    Button btnJobsheetWHSESCL;
    Button btnJobsheetWHSEOK;
    Button btnJobsheetCageOut;
    Button btnJobsheet207C;
    Button btnItemGroupCheck;
    Button btnJobsheetGROUPON;
    Button btnJobsheetNESTLE;
    Button btnJobsheetPROUTER;
    Button btnJobsheetNEXT;
    private ProgressDialog pd, pdCheck;

    static boolean errored = false;
    private static SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    public static final String LoginUsername = "username";
    public static final String LoginPwd = "password";
    public static final String UserIDX = "IDX";
    public static final String isHomesUser = "ishomesuser";
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    private static String ScannedID = "";
    private static String ScannedUserName = "";
    private String LukeAPI;
    static String cameraScannedCode;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;
    private Uri errToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error);
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);
    private MediaPlayer player;

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

        setContentView(R.layout.activity_jobsheet);

        txtJobsheetScanID = (EditText) findViewById(R.id.txtJobsheetScanID);
        lblJobsheetSCANNEDCURRENTID = (TextView) findViewById(R.id.lblJobsheetSCANNEDCURRENTID);
        lblJobsheetSelectedButton = (TextView) findViewById(R.id.lblJobsheetSelectedButton);
        lbljsmode =  (TextView) findViewById(R.id.lbljsmode);
        btnJobsheetEnter = (Button) findViewById(R.id.btnJobsheetEnter);
        btnJobsheetWHSEC = (Button) findViewById(R.id.btnJobsheetWHSEC);
        btnJobsheetWHSEA = (Button) findViewById(R.id.btnJobsheetWHSEA);
        btnJobsheetWHSEPSCH = (Button) findViewById(R.id.btnJobsheetWHSEPSCH);
        btnJobsheetWHSESCL = (Button) findViewById(R.id.btnJobsheetWHSESCL);
        btnJobsheetWHSEOK = (Button) findViewById(R.id.btnJobsheetWHSEOK);
        btnJobsheetCageOut = (Button) findViewById(R.id.btnJobsheetCageOut);
        btnJobsheet207C = (Button) findViewById(R.id.btnJobsheet207C);
        btnJobsheetGROUPON = (Button) findViewById(R.id.btnJobsheetGROUPON);
        btnJobsheetNESTLE = (Button) findViewById(R.id.btnJobsheetNESTLE);
        btnJobsheetPROUTER = (Button) findViewById(R.id.btnJobsheetPROUTER);
        btnJobsheetNEXT = (Button) findViewById(R.id.btnJobsheetNEXT);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        //pd.setMessage("Please wait. Retrieving Info...");
        pd.setMessage("Please wait. Retrieving Info...");

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    private boolean isShuttleModeOnResume() {
        boolean asd = false;
        try {
            String jsmode = objGlobalMethods.getGCMPrefs(GcmPreferences, "jsmode");
            lbljsmode.setText(jsmode);
            asd = objGlobalMethods.IsEqualString(jsmode, "shuttle");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsheetActivity isShuttleMode. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    private boolean isShuttleModeOnAction() {
        boolean asd = false;
        try {
            String jsmode = lbljsmode.getText().toString();
            if (objGlobalMethods.IsEmptyString(jsmode)) {
                jsmode = objGlobalMethods.getGCMPrefs(GcmPreferences, "jsmode");
            }
            asd = objGlobalMethods.IsEqualString(jsmode, "shuttle");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsheetActivity isShuttleModeOnAction. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    @Override
    protected void onResume() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = getUpdateJobInfoPreferences(context);
        if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
            getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        }
        //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        myApp.currentActivity = this;
        context = myApp.getAppContext();

        boolean shuttle = isShuttleModeOnResume();

        setTitle(shuttle ?
                myApp.getAppContext().getString(R.string.title_activity_shuttle) :
                myApp.getAppContext().getString(R.string.item_dashboard2JOBSHEEThomes));

        hideButtons(shuttle);

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
            objGlobalMethods.writeToFile("JobsheetActivity !BTEnabled", "", "", "", "");
            temploggerclass.promptToOnBT();
            return;
        }

        lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

        DashboardSubActivity.frompage = null;

        //cd = new ConnectionDetector(context);
        cd = new ConnectionDetector(null);

        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            //Toast.makeText(context, "Please connect to working Internet connection.", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
        }

        objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");

        initPage();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsheetActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity = null;

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsheetActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetActivity showChangePermissionDialog. ActivityNotFoundException: "
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
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "jobsheet");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        //parentIntent = new Intent(Dashboard2Activity.this, QRReaderActivity.class);
        parentIntent = new Intent(JobsheetActivity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    //Start showDialog
    private Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jobsheet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.qrscan) {
            launchQRScan();
        }

        /*Intent parentIntent;
        switch (item.getItemId()) {
            case R.id.qrscan:
                *//*GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                objGlobalMethods.setGCMPrefs(GcmPreferences,"QRScanRequestIntent", "jobsheet");
                //parentIntent = new Intent(JobsheetActivity.this, QRReaderActivity.class);
                parentIntent = new Intent(JobsheetActivity.this, QRBarcodeReader.class);
                startActivity(parentIntent);*//*
                launchQRScan();
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        /*objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedIDnUsername", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedUsername", "");*/

        String[] prefName = new String[]{"JobsheetScannedIDnUsername", "JobsheetScannedUsername", "jsmode"};
        String[] prefvalue = new String[]{"","", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    private SharedPreferences getUpdateJobInfoPreferences(Context context) {
        return getSharedPreferences("UpdateJobInfoPrefs",
                Context.MODE_PRIVATE);
    }

    private void initPage() {
        setCurrentID("", "No ID selected.");

        txtJobsheetScanID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (actionId == 0) {
                    //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                    //setlblJobsheetSCANNEDCURRENTID();
                    if (!txtJobsheetScanID.getText().toString().isEmpty()) {
                        setCurrentID(txtJobsheetScanID.getText().toString().toUpperCase(), "You selected ID: " + txtJobsheetScanID.getText().toString().toUpperCase());
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_submitjobsheetnoid2));
                        //ctrlSettings.enableDisableSubmitButton(false, btnJobsheetNEXT);
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnJobsheetNEXT);
                    }
                } else if (actionId == EditorInfo.IME_ACTION_GO) {
                    //setlblJobsheetSCANNEDCURRENTID();
                    if (!txtJobsheetScanID.getText().toString().isEmpty()) {
                        setCurrentID(txtJobsheetScanID.getText().toString().toUpperCase(), "You selected ID: " + txtJobsheetScanID.getText().toString().toUpperCase());
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_submitjobsheetnoid2));
                        //ctrlSettings.enableDisableSubmitButton(false, btnJobsheetNEXT);
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnJobsheetNEXT);
                    }
                } else if (actionId == 6) {
                    return false;
                }
                return true;
            }
        });

        cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
        if (!cameraScannedCode.equals("")) {
            txtJobsheetScanID.setText(cameraScannedCode);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            setCurrentID(txtJobsheetScanID.getText().toString().toUpperCase(), "You selected ID: " + txtJobsheetScanID.getText().toString().toUpperCase());
        }

        txtJobsheetScanID.clearFocus();
    }

    private void hideButtons(boolean shuttle) {
        try {
            btnJobsheetWHSEC.setVisibility(shuttle ? View.GONE : View.VISIBLE);
            btnJobsheetWHSEA.setVisibility(shuttle ? View.GONE : View.VISIBLE);
            btnJobsheetWHSEPSCH.setVisibility(shuttle ? View.GONE : View.VISIBLE);
            btnJobsheetWHSESCL.setVisibility(shuttle ? View.GONE : View.VISIBLE);
            btnJobsheetWHSEOK.setVisibility(shuttle ? View.GONE : View.VISIBLE);
            btnJobsheetCageOut.setVisibility(shuttle ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsheetActivity hideButtons. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private void enableDisableButtons(boolean isEnabled) {
        txtJobsheetScanID.setEnabled(isEnabled);
        ctrlSettings.enableDisableCommonButtonWithStyles(isEnabled, btnJobsheetEnter);
        ctrlSettings.enableDisableCommonButtonWithStyles(isEnabled, btnJobsheetWHSEC);
        ctrlSettings.enableDisableCommonButtonWithStyles(isEnabled, btnJobsheetWHSEA);
        ctrlSettings.enableDisableCommonButtonWithStyles(isEnabled, btnJobsheetWHSEPSCH);
        ctrlSettings.enableDisableCommonButtonWithStyles(isEnabled, btnJobsheetWHSESCL);
        ctrlSettings.enableDisableCommonButtonWithStyles(isEnabled, btnJobsheet207C);
        ctrlSettings.enableDisableCommonButtonWithStyles(isEnabled, btnJobsheetGROUPON);
        ctrlSettings.enableDisableCommonButtonWithStyles(isEnabled, btnJobsheetNESTLE);
        ctrlSettings.enableDisableCommonButtonWithStyles(isEnabled, btnJobsheetPROUTER);
        /*ctrlSettings.enableDisableCommonButton(isEnabled, btnJobsheetEnter);
        ctrlSettings.enableDisableCommonButton(isEnabled, btnJobsheetWHSEC);
        ctrlSettings.enableDisableCommonButton(isEnabled, btnJobsheetWHSEA);
        ctrlSettings.enableDisableCommonButton(isEnabled, btnJobsheetWHSEPSCH);
        ctrlSettings.enableDisableCommonButton(isEnabled, btnJobsheetWHSESCL);
        ctrlSettings.enableDisableCommonButton(isEnabled, btnJobsheet207C);
        ctrlSettings.enableDisableCommonButton(isEnabled, btnJobsheetGROUPON);
        ctrlSettings.enableDisableCommonButton(isEnabled, btnJobsheetNESTLE);
        ctrlSettings.enableDisableCommonButton(isEnabled, btnJobsheetPROUTER);
        btnJobsheetEnter.setEnabled(isEnabled);
        btnJobsheetWHSEC.setEnabled(isEnabled);
        btnJobsheetWHSEA.setEnabled(isEnabled);
        btnJobsheetWHSEPSCH.setEnabled(isEnabled);
        btnJobsheetWHSESCL.setEnabled(isEnabled);
        btnJobsheet207C.setEnabled(isEnabled);
        btnJobsheetGROUPON.setEnabled(isEnabled);
        btnJobsheetNESTLE.setEnabled(isEnabled);
        btnJobsheetPROUTER.setEnabled(isEnabled);*/
    }

    private void setCurrentID(String ausername, String buttonName) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
        username = UsernamePwd[0];
        useridx = UsernamePwd[2];
        ishomesuser = UsernamePwd[3];

        if (!Boolean.parseBoolean(ishomesuser)) {
            useridx = "D" + useridx;
        }
        ScannedID = useridx;

        if (!ausername.equals("")) {
            ScannedUserName = ausername;
            lblJobsheetSCANNEDCURRENTID.setText(ScannedID.toUpperCase() + "|" + ScannedUserName.toUpperCase());
            lblJobsheetSelectedButton.setText(buttonName.toUpperCase());
            //ctrlSettings.enableDisableSubmitButton(true, btnJobsheetNEXT);
            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnJobsheetNEXT);
        } else {
            ScannedUserName = username;
            if (!Boolean.parseBoolean(ishomesuser)) {
                lblJobsheetSCANNEDCURRENTID.setText(ScannedID.toUpperCase() + "|" + ScannedUserName.toUpperCase());
                //ctrlSettings.enableDisableSubmitButton(true, btnJobsheetNEXT);
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnJobsheetNEXT);

                enableDisableButtons(false);
            } else {
                lblJobsheetSCANNEDCURRENTID.setText(ScannedID.toUpperCase() + "|");
                lblJobsheetSelectedButton.setText("");
                //ctrlSettings.enableDisableSubmitButton(false, btnJobsheetNEXT);
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnJobsheetNEXT);
                enableDisableButtons(true);
            }
        }

        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedUsername", ScannedUserName.toUpperCase());

        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        focusTextbox();

        if (!objGlobalMethods.IsEmptyString(ausername)) {
            btnJobsheetNEXTClick(null);
        }
    }

    public void btnJobsheetCANCELClick(View view) {
        back();
    }

    public void btnJobsheetNEXTClick(View view) {
        if (IsNotGCID()) {
            playSuccessVerifiedTone();
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedIDnUsername", lblJobsheetSCANNEDCURRENTID.getText().toString().toUpperCase());

            boolean shuttle = isShuttleModeOnResume();

            /*String[] prefNameShuttle = new String[]{"JobsheetScannedIDnUsername", "JobsheetScannedUsername", "jobsheetSubmitscannedItemsPrefStr", "jsmode"};
            String[] prefvalueShuttle = new String[]{lblJobsheetSCANNEDCURRENTID.getText().toString().toUpperCase(), "", "", shuttle ? "shuttle" : ""};*/
            String[] prefNameShuttle = new String[]{"JobsheetScannedIDnUsername", "jobsheetSubmitscannedItemsPrefStr", "jsmode"};
            String[] prefvalueShuttle = new String[]{lblJobsheetSCANNEDCURRENTID.getText().toString().toUpperCase(), "", shuttle ? "shuttle" : ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefNameShuttle, prefvalueShuttle);

            Intent i = new Intent(this, JobsheetSubmitActivity.class);
            startActivity(i);
            this.finish();
        } else {
            VerifyGCID x = new VerifyGCID();
            x.execute();
        }

    }

    private boolean IsNotGCID() {
        boolean asd = false;
        try {
            //"S8888888B", "S8888888A", "S8888888C", "S8888888S", "S8888888K", "S8888888X")
            ArrayList<String> standards = new ArrayList<String>();
            standards.add("S8888888B");
            standards.add("S8888888A");
            standards.add("S8888888C");
            standards.add("S8888888S");
            standards.add("S8888888K");
            standards.add("S8888888X");

            String JobsheetScannedIDnUsername = lblJobsheetSCANNEDCURRENTID.getText().toString().toUpperCase();
            String[] JobsheetScannedIDnUsernameArr = JobsheetScannedIDnUsername.split("\\|");
            if (JobsheetScannedIDnUsernameArr != null && JobsheetScannedIDnUsernameArr.length > 0 && JobsheetScannedIDnUsernameArr.length == 2) {
                String av = JobsheetScannedIDnUsernameArr[1];
                asd = !objGlobalMethods.IsEmptyString(av) && standards.contains(av);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsheetActivity IsNotGCID. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private class VerifyGCID extends AsyncTask<Void, Void, Long> {

        public VerifyGCID() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Long doInBackground(Void... params) {
            long VerifyGCIDResult = 0;
            try {
                String JobsheetScannedIDnUsername = lblJobsheetSCANNEDCURRENTID.getText().toString().toUpperCase();
                String[] JobsheetScannedIDnUsernameArr = JobsheetScannedIDnUsername.split("\\|");
                if (JobsheetScannedIDnUsernameArr != null && JobsheetScannedIDnUsernameArr.length > 0 && JobsheetScannedIDnUsernameArr.length == 2) {
                    VerifyGCIDResult = WebService.VerifyGCID(Long.parseLong(JobsheetScannedIDnUsernameArr[0]), JobsheetScannedIDnUsernameArr[1]);
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobsheetActivity VerifyGCID doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return VerifyGCIDResult;
            }
        }

        @Override
        protected void onPostExecute(final Long res) {
            boolean err = false;
            Context ctx = myApp.getAppContext();
            try {
                if (pd != null) {
                    pd.dismiss();
                }
                if (res > 0) {
                    playSuccessVerifiedTone();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedIDnUsername", lblJobsheetSCANNEDCURRENTID.getText().toString().toUpperCase());
                    Intent i = new Intent(myApp.currentActivity, JobsheetSubmitActivity.class);
                    startActivity(i);
                    myApp.currentActivity.finish();
                } else {
                    playTone();
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.gcid_not_verified)).show();
                }
            } catch (Exception e) {
                playTone();
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobsheetActivity VerifyGCID onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        }
    }

    public void setbtnJobsheetEnter(View view) {
        if (!txtJobsheetScanID.getText().toString().isEmpty()) {
            //setCurrentID(txtJobsheetScanID.getText().toString().toUpperCase(), "You selected ID: " + txtJobsheetScanID.getText().toString().toUpperCase());
            setCurrentID(txtJobsheetScanID.getText().toString().toUpperCase(), myApp.getAppContext().getString(R.string.alert_submitjobsheetselectedID) + txtJobsheetScanID.getText().toString().toUpperCase());
        } else {
            //showDialog("ID is empty.");
            showDialog(myApp.getAppContext().getString(R.string.alert_submitjobsheetnoid2));
            //ctrlSettings.enableDisableSubmitButton(false, btnJobsheetNEXT);
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnJobsheetNEXT);
            focusTextbox();
        }
    }

    public void setBtnJobsheetWHSEC(View view) {
        //setCurrentID("S8888888B", "You selected ID: WHSE-C");
        setCurrentID("S8888888B", myApp.getAppContext().getString(R.string.alert_submitjobsheetselectedID) + "WHSE-C");
    }

    public void setbtnJobsheetWHSEA(View view) {
        //setCurrentID("S8888888A", "You selected ID: WHSE-A");
        setCurrentID("S8888888A", myApp.getAppContext().getString(R.string.alert_submitjobsheetselectedID) + "WHSE-A");
    }

    public void setbtnJobsheetWHSEPSCH(View view) {
        //setCurrentID("S8888888C", "You selected ID: WHSE-PSCH");
        setCurrentID("S8888888C", myApp.getAppContext().getString(R.string.alert_submitjobsheetselectedID) + "WHSE-PSCH");
    }

    public void setbtnJobsheetWHSESCL(View view) {
        //setCurrentID("S8888888S", "You selected ID: WHSE-SCL");
        setCurrentID("S8888888S", myApp.getAppContext().getString(R.string.alert_submitjobsheetselectedID) + "WHSE-SCL");
    }

    public void setbtnJobsheetWHSEOK(View view) {
        setCurrentID("S8888888K", myApp.getAppContext().getString(R.string.alert_submitjobsheetselectedID) + "WHSE-OK");
    }

    public void setbtnJobsheet207C(View view) {
        //setCurrentID("S8888887B", "You selected ID: 207-C");
        setCurrentID("S8888887B", myApp.getAppContext().getString(R.string.alert_submitjobsheetselectedID) + "207-C");
    }

    public void btnJobsheetCageOut(View view) {
        setCurrentID("S8888888X", myApp.getAppContext().getString(R.string.alert_submitjobsheetselectedID) + "CAGE-OUT");
    }

    public void setbtnJobsheetGROUPON(View view) {
        //setCurrentID("GROUPON", "You selected ID: GROUPON");
        setCurrentID("GROUPON", myApp.getAppContext().getString(R.string.alert_submitjobsheetselectedID) + "GROUPON");
    }

    public void setbtnJobsheetNESTLE(View view) {
        //setCurrentID("NESTLE", "You selected ID: NESTLE");
        setCurrentID("NESTLE", myApp.getAppContext().getString(R.string.alert_submitjobsheetselectedID) + "NESTLE");
    }

    public void setbtnJobsheetPROUTER(View view) {
        //setCurrentID("PROUTER", "You selected ID: PROUTER");
        setCurrentID("PROUTER", myApp.getAppContext().getString(R.string.alert_submitjobsheetselectedID) + "PROUTER");
    }

    public void setbtnItemC(View view) {

        Intent intent = new Intent(this, ItemGroupCheck.class);
        startActivity(intent);
    }

    private void focusTextbox() {
        txtJobsheetScanID.setText("");
        txtJobsheetScanID.requestFocus();
    }

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
            objGlobalMethods.writeToFile("Exception at JobsheetActivity stopPlayingTone. Exception: " + e.getMessage(),"","","","");
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
            objGlobalMethods.writeToFile("Exception at JobsheetActivity playTone. Exception: " + e.getMessage(),"","","","");
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
            objGlobalMethods.writeToFile("Exception at JobsheetActivity playSuccessVerifiedTone. Exception: " + e.getMessage(),"","","","");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at JobsheetActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsheetActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }



}

