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
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.Jobsheet_PostData;
import com.xdel.luke.objClasses.Jobsheet_PostItemAdapter;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobsheetSubmitActivity extends AppCompatActivity {
    Context context;
    ConnectionDetector cd;
    private MediaPlayer player;

    private static ListView listView;
    private static TextView lblJobsheetSubmitIDval;
    private static TextView lblJobsheetSubmitCOUNT;
    private static TextView lbldesc;
    private static EditText txtJobsheetSubmitMANUALENTRY;
    private static Button btnJobsheetSubmitENTER;
    private static Button btnJobsheetSCAN;
    private static Button btnJobsheetSubmitCANCEL;
    private static Button btnJobsheetSubmitSUBMIT;
    private ProgressDialog pd, pdCheck;

    static boolean errored = false;
    private static SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    private String JobsheetScannedIDnUsername = "";
    private String LukeAPI;
    public static ArrayList<Jobsheet_PostData> scannedItems = null;
    Jobsheet_PostItemAdapter adapter = null;
    static String cameraScannedCode;
    TextView lbljobsheetSubmitIsHomesUser, lbljsmode;
    private String langType = "en";
    NfcAdapter mNfcAdapter;
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;

    private ArrayList<Jobsheet_PostData> getScannedItems(SharedPreferences gcmp) {
        String jobsheetSubmitscannedItemsPrefStr = "";
        ArrayList<Jobsheet_PostData> jpd = null;
        try {
            jobsheetSubmitscannedItemsPrefStr = objGlobalMethods.getGCMPrefs(gcmp, "jobsheetSubmitscannedItemsPrefStr");

            if (jobsheetSubmitscannedItemsPrefStr.equals("") || jobsheetSubmitscannedItemsPrefStr.equals("[]")) {

            } else {
                Gson gson = new Gson();
                jpd = gson.fromJson(jobsheetSubmitscannedItemsPrefStr, new TypeToken<List<Jobsheet_PostData>>() {
                }.getType());
            }

            /*if (jobsheetSubmitscannedItemsPrefStr != "" && jobsheetSubmitscannedItemsPrefStr != "[]") {
                Gson gson = new Gson();
                jpd = gson.fromJson(jobsheetSubmitscannedItemsPrefStr, new TypeToken<List<Jobsheet_PostData>>(){}.getType());
            }*/
        } catch (Exception e) {
            //as per instructed from http://stackoverflow.com/questions/15022727/arrayadapter-notifydatasetchanged-is-not-working
        } finally {
            return jpd;
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

        setContentView(R.layout.activity_jobsheet_submit);

        listView = (ListView) findViewById(R.id.listView);
        lblJobsheetSubmitIDval = (TextView) findViewById(R.id.lblJobsheetSubmitIDval);
        lblJobsheetSubmitCOUNT = (TextView) findViewById(R.id.lblJobsheetSubmitCOUNT);
        lbljobsheetSubmitIsHomesUser = (TextView) findViewById(R.id.lbljobsheetSubmitIsHomesUser);
        lbljsmode = (TextView) findViewById(R.id.lbljsmode);
        lbldesc = (TextView) findViewById(R.id.lbldesc);
        txtJobsheetSubmitMANUALENTRY = (EditText) findViewById(R.id.txtJobsheetSubmitMANUALENTRY);
        btnJobsheetSubmitENTER = (Button) findViewById(R.id.btnJobsheetSubmitENTER);
        btnJobsheetSCAN = (Button) findViewById(R.id.btnJobsheetSCAN);
        btnJobsheetSubmitCANCEL = (Button) findViewById(R.id.btnJobsheetSubmitCANCEL);
        btnJobsheetSubmitSUBMIT = (Button) findViewById(R.id.btnJobsheetSubmitSUBMIT);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));

        pdCheck = new ProgressDialog(this);
        pdCheck.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdCheck.setCanceledOnTouchOutside(false);
        pdCheck.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

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
            objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity isShuttleModeOnResume. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
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
            objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity isShuttleModeOnAction. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
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
            context = myApp.getAppContext();

            boolean shuttle = isShuttleModeOnResume();

            setTitle(shuttle ?
                    myApp.getAppContext().getString(R.string.title_activity_shuttle) :
                    myApp.getAppContext().getString(R.string.title_activity_jobsheet));

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
                objGlobalMethods.writeToFile("JobsheetSubmitActivity !BTEnabled", "", "", "", "");
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
                    PendingIntent nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                    mNfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilter, null);
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                //objGlobalMethods.writeToFile("Exception caught at JobsheetSubmitActivity onResume mNfcAdapter portion. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
            }

            JobsHandler.checkForceLogOut();
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GcmPreferences = null;
        UpdateJobInfoPreferences = null;
        scannedItems = null;
        cameraScannedCode = null;
        listView = null;
        lblJobsheetSubmitIDval = null;
        lblJobsheetSubmitCOUNT = null;
        lbldesc = null;
        txtJobsheetSubmitMANUALENTRY = null;
        btnJobsheetSubmitENTER = null;
        btnJobsheetSCAN = null;
        btnJobsheetSubmitCANCEL = null;
        btnJobsheetSubmitSUBMIT = null;
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
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
            objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
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
                        Date dt = new Date();
                        objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetSubmitActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetSubmitActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetSubmitActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetSubmitActivity showChangePermissionDialog. ActivityNotFoundException: "
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
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "jobsheetsubmit");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        parentIntent = new Intent(JobsheetSubmitActivity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jobsheet_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.clear) {
            btnJobsheetSubmitCLEARALLClick(null);
        }

        /*switch (item.getItemId()) {
            *//*case R.id.qrscan:
                launchQRScan();
                break;*//*
            case R.id.clear:
                btnJobsheetSubmitCLEARALLClick(null);
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        initBack();
    }

    private void initBack() {
        if (scannedItems != null && scannedItems.size() > 0 && adapter != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_moveawayclearall));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    back();
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
            back();
        }
    }

    private void back() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        /*objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedIDnUsername", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedUsername", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "jobsheetSubmitscannedItemsPrefStr", "");*/

        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
        username = UsernamePwd[0];
        useridx = UsernamePwd[2];
        ishomesuser = UsernamePwd[3];
        scannedItems = null;
        Intent parentIntent;
        if (Boolean.parseBoolean(ishomesuser)) {

            String[] prefName = new String[]{"JobsheetScannedIDnUsername", "JobsheetScannedUsername", "jobsheetSubmitscannedItemsPrefStr"};
            String[] prefvalue = new String[]{"","", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            parentIntent = new Intent(this, JobsheetActivity.class);
        } else {

            String[] prefName = new String[]{"JobsheetScannedIDnUsername", "JobsheetScannedUsername", "jobsheetSubmitscannedItemsPrefStr", "jsmode"};
            String[] prefvalue = new String[]{"","", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        }
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

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

    private void showErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if (msg.isEmpty()) {

            builder.setMessage(myApp.getAppContext().getString(R.string.alert_submitjobsheetnoid));
        } else {
            builder.setMessage(msg);
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedIDnUsername", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "jobsheetSubmitscannedItemsPrefStr", "");
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];
                scannedItems = null;

                Intent i;
                if (Boolean.parseBoolean(ishomesuser)) {
                    i = new Intent(JobsheetSubmitActivity.this, JobsheetActivity.class);
                } else {
                    //i = new Intent(JobsheetSubmitActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                    boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                    i = new Intent(JobsheetSubmitActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                }
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSubmissionOKAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setMessage(myApp.getAppContext().getString(R.string.alert_data_submittedsuccess));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedIDnUsername", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "jobsheetSubmitscannedItemsPrefStr", "");
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];
                scannedItems = null;
                Intent parentIntent;
                if (Boolean.parseBoolean(ishomesuser)) {
                    parentIntent = new Intent(JobsheetSubmitActivity.this, JobsheetActivity.class);
                } else {
                    //parentIntent = new Intent(JobsheetSubmitActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                    boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                    parentIntent = new Intent(JobsheetSubmitActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                }
                MainActivity.uponLogin = 0;
                startActivity(parentIntent);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initPage() {
        scannedItems = getScannedItems(objGlobalMethods.getGcmPreferences());
        String JobsheetScannedUsername = "";
        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
        ishomesuser = UsernamePwd[3];
        lbljobsheetSubmitIsHomesUser.setText(ishomesuser);
        JobsheetScannedIDnUsername = objGlobalMethods.getGCMPrefs(GcmPreferences, "JobsheetScannedIDnUsername");
        JobsheetScannedUsername = objGlobalMethods.getGCMPrefs(GcmPreferences, "JobsheetScannedUsername");

        if (JobsheetScannedIDnUsername.isEmpty() || JobsheetScannedUsername.isEmpty()) {
            //show alert and return to previous page
            showErrorAlert("");
        } else {
            lblJobsheetSubmitIDval.setText(JobsheetScannedIDnUsername);
            lbldesc.setText(getDesc(JobsheetScannedUsername));

            if (scannedItems != null && scannedItems.size() > 0) {
                lblJobsheetSubmitCOUNT.setText(String.valueOf(scannedItems.size()));
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnJobsheetSubmitSUBMIT);
            } else {
                lblJobsheetSubmitCOUNT.setText("0");
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnJobsheetSubmitSUBMIT);
            }
        }

        txtJobsheetSubmitMANUALENTRY.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (actionId == 0) {
                    //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                    submitManualEntry();
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } else if (actionId == EditorInfo.IME_ACTION_GO) {
                    submitManualEntry();
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

        cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
        if (!cameraScannedCode.equals("")) {
            txtJobsheetSubmitMANUALENTRY.setText(cameraScannedCode);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            submitManualEntry();
        }

        if (scannedItems != null && scannedItems.size() > 0 && adapter == null)  {
            adapter = new Jobsheet_PostItemAdapter(this, scannedItems);
            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                final int deleteRow = position;
                if (v == null)
                    return;

                TextView lblJobsheetItemTitle = (TextView) v.findViewById(R.id.lblJobsheetItemTitle);

                AlertDialog.Builder builder = new AlertDialog.Builder(JobsheetSubmitActivity.this);
                builder.setCancelable(false);
                builder.setMessage(myApp.getAppContext().getString(R.string.alert_submitjobsheetcfmDelete) + " " + lblJobsheetItemTitle.getText().toString() + " ?");
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status
                        scannedItems.remove(deleteRow);
                        if (scannedItems.size() > 0) {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnJobsheetSubmitSUBMIT);
                            Gson gson = new Gson();
                            String jobsheetSubmitscannedItemsPrefStr = "";
                            jobsheetSubmitscannedItemsPrefStr = gson.toJson(scannedItems);
                            objGlobalMethods.setGCMPrefs(objGlobalMethods.getGcmPreferences(), "jobsheetSubmitscannedItemsPrefStr", jobsheetSubmitscannedItemsPrefStr);
                            adapter.clear();
                            adapter.addAll(scannedItems);
                        } else if (scannedItems.size() == 0) {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnJobsheetSubmitSUBMIT);
                            objGlobalMethods.setGCMPrefs(objGlobalMethods.getGcmPreferences(), "jobsheetSubmitscannedItemsPrefStr", "");
                            adapter.clear();
                        }
                        lblJobsheetSubmitCOUNT.setText(String.valueOf(scannedItems.size() - getBulkyCount2(scannedItems)));
                        focusTextbox();
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
        });
        focusTextbox();
    }

    private String getDesc(String JobsheetScannedUsername) {
        String v = JobsheetScannedUsername;
        String c = "";
        try {
            ArrayList<String> standards = new ArrayList<String>();
            standards.add("S8888888B"); //
            standards.add("S8888888A");
            standards.add("S8888888C");
            standards.add("S8888888S");
            standards.add("S8888888K");
            standards.add("S8888888X");

            for (int i  = 0; i <= standards.size() -1; i++) {
                c = standards.get(i);
                if(objGlobalMethods.IsEqualString(c, v) && objGlobalMethods.IsEqualString(v, "S8888888B")) {
                    v = "WHSE-C";
                    break;
                } else if(objGlobalMethods.IsEqualString(c, v) && objGlobalMethods.IsEqualString(v, "S8888888A")) {
                    v = "WHSE-A";
                    break;
                } else if(objGlobalMethods.IsEqualString(c, v) && objGlobalMethods.IsEqualString(v, "S8888888C")) {
                    v = "WHSE-PSCH";
                    break;
                } else if(objGlobalMethods.IsEqualString(c, v) && objGlobalMethods.IsEqualString(v, "S8888888S")) {
                    v = "WHSE-SCL";
                    break;
                } else if(objGlobalMethods.IsEqualString(c, v) && objGlobalMethods.IsEqualString(v, "S8888888K")) {
                    v = "WHSE-OK";
                    break;
                } else if(objGlobalMethods.IsEqualString(c, v) && objGlobalMethods.IsEqualString(v, "S8888888X")) {
                    v = "CAGE-OUT";
                    break;
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity getDesc. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return v;
        }
    }

    private class submitManualEntryAsync extends AsyncTask<Void, Void, Boolean> {

        private final String scanned;
        private ArrayList<Jobsheet_PostData> SCANNEDItems;
        private Context ctx;
        private boolean isHomesUser;

        submitManualEntryAsync(String ascanned, ArrayList<Jobsheet_PostData> aSCANNEDItems) {
            this.scanned = ascanned;
            this.SCANNEDItems = aSCANNEDItems;
            this.ctx = myApp.getAppContext();
            this.isHomesUser = false;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pdCheck.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean asd = true;

            try {
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                ishomesuser = UsernamePwd[3];
                isHomesUser = !ishomesuser.isEmpty() && !ishomesuser.equals("") ? Boolean.parseBoolean(ishomesuser) : false;
                useridx = UsernamePwd[2];
                long DriverIDX = 0;
                String NRIC = "";

                if (!isHomesUser) {
                    try {
                        DriverIDX = !Boolean.parseBoolean(ishomesuser) ? Long.parseLong(useridx) : 0;
                    } catch (Exception e) {
                        String s = e.getMessage().toString();
                        objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity submitManualEntryAsync doInBackground DriverIDX conversion. Exception: " + s, "", "", "", "");
                    }
                } else {
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    NRIC = objGlobalMethods.getGCMPrefs(GcmPreferences, "JobsheetScannedUsername");
                }
                asd = WebService.CanDeliver(ctx.getString(R.string.action_HOMESAPI), DriverIDX, NRIC, scanned);
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity doInBackground. Exception: " + s, "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean suc) {
            try {
                pdCheck.dismiss();
                if (suc == null) {
                    playTone();
                    focusTextbox();
                    showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
                } else if (suc) {
                    includeInJS(SCANNEDItems, scanned, ctx);
                } else {
                    if (isHomesUser) {
                        playTone();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setCancelable(false);
                        builder.setMessage(myApp.getAppContext().getString(R.string.alert_showopsgccannotdeliver));
                        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", update job status
                                includeInJS(SCANNEDItems, scanned, ctx);
                            }
                        });

                        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user select "No", just cancel this dialog and continue with app
                                dialog.cancel();
                                focusTextbox();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        playTone();
                        focusTextbox();
                        showDialog(myApp.getAppContext().getString(R.string.alert_showgccannotdeliver));
                    }
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity onPostExecute. Exception: " + s, "", "", "", "");
                playTone();
                showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
            }
        }
    }

    private void includeInJS(ArrayList<Jobsheet_PostData> SCANNEDItems, String scanned, Context ctx) {
        Jobsheet_PostData data = null;
        String currentDateTime = "";
        Date dt = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        currentDateTime = formatter.format(dt);
        String currentScannedItem = "";
        try {
            currentScannedItem = "\"" + lblJobsheetSubmitIDval.getText().toString().toUpperCase() + "\"" + "," + "\"" + scanned.toUpperCase() + "\"" + "," + "\"" + currentDateTime.toUpperCase() + "\"";
            data = new Jobsheet_PostData(lblJobsheetSubmitIDval.getText().toString().toUpperCase(), scanned.toUpperCase(), currentDateTime.toUpperCase(), currentScannedItem);

            if (SCANNEDItems != null) {
                SCANNEDItems.add(data);
                Gson gson = new Gson();
                String jobsheetSubmitscannedItemsPrefStr = "";
                jobsheetSubmitscannedItemsPrefStr = gson.toJson(SCANNEDItems);
                objGlobalMethods.setGCMPrefs(objGlobalMethods.getGcmPreferences(), "jobsheetSubmitscannedItemsPrefStr", jobsheetSubmitscannedItemsPrefStr);
                if (adapter == null) {
                    adapter = new Jobsheet_PostItemAdapter(ctx, SCANNEDItems);
                    listView.setAdapter(adapter);
                } else {
                    adapter.clear();
                    adapter.addAll(SCANNEDItems);
                }
            } else {
                SCANNEDItems = new ArrayList<Jobsheet_PostData>();
                SCANNEDItems.add(data);
                Gson gson = new Gson();
                String jobsheetSubmitscannedItemsPrefStr = "";
                jobsheetSubmitscannedItemsPrefStr = gson.toJson(SCANNEDItems);
                objGlobalMethods.setGCMPrefs(objGlobalMethods.getGcmPreferences(), "jobsheetSubmitscannedItemsPrefStr", jobsheetSubmitscannedItemsPrefStr);
                adapter = new Jobsheet_PostItemAdapter(ctx, SCANNEDItems);
                listView.setAdapter(adapter);
            }
            //runs after listview is done updating itself
            listView.post(new Runnable() {
                public void run() {
                    //scroll the listview to the last item
                    listView.setSelection(listView.getCount() - 1);
                    //focus certain view or any other calls after listview completed updating
                }
            });

            lblJobsheetSubmitCOUNT.setText(String.valueOf(SCANNEDItems.size() - getBulkyCount2(SCANNEDItems)));
            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnJobsheetSubmitSUBMIT);
            focusTextbox();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity includeInJS. Exception: " + s, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    private void submitManualEntry2() {
        try {
            if (!txtJobsheetSubmitMANUALENTRY.getText().toString().equals("")) {
                boolean jobFound = false;
                View v;
                TextView lblCnno;
                String Cnno = "";
                String scanned = txtJobsheetSubmitMANUALENTRY.getText().toString().toLowerCase();
                scannedItems = getScannedItems(objGlobalMethods.getGcmPreferences());
                if (!scanned.equals("bulky") && !scanned.equals("prouter") && !scanned.equals("nestle") && !scanned.equals("groupon") && !scanned.equals("wmg") && !scanned.equals("edenred")) {
                    for (int i = 0; i <= listView.getCount() - 1; i++) {
                        v = listView.getAdapter().getView(i, null, null);
                        lblCnno = (TextView) v.findViewById(R.id.lblJobsheetItemTitle);
                        Cnno = lblCnno.getText().toString();

                        if (scanned.equals(Cnno.toLowerCase())) {
                            jobFound = true;
                            break;
                        }
                    }
                } else if (scanned.equals("bulky")) {
                    if (listView.getCount() > 0) {
                        v = listView.getAdapter().getView(listView.getCount() - 1, null, null);
                        lblCnno = (TextView) v.findViewById(R.id.lblJobsheetItemTitle);
                        Cnno = lblCnno.getText().toString();
                        if (Cnno.toLowerCase().equals("bulky")) {
                            playTone();
                            showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_plsenterCN));
                            return;
                        }
                    }
                } else {
                    if (listView.getCount() > 0) {
                        v = listView.getAdapter().getView(listView.getCount() - 1, null, null);
                        lblCnno = (TextView) v.findViewById(R.id.lblJobsheetItemTitle);
                        Cnno = lblCnno.getText().toString();
                        if (Cnno.toLowerCase().equals("bulky") || Cnno.toLowerCase().equals("prouter") || Cnno.toLowerCase().equals("nestle") || Cnno.toLowerCase().equals("groupon") || Cnno.toLowerCase().equals("wmg") || Cnno.toLowerCase().equals("edenred")) {
                            playTone();
                            showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_plsenterCN));
                            return;
                        }
                    }
                }

                if (!jobFound) {
                    submitManualEntryAsync t = new submitManualEntryAsync(scanned, scannedItems);
                    t.execute();
                } else {
                    playTone();
                    showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_duplicatescan) + "\n" + txtJobsheetSubmitMANUALENTRY.getText().toString());
                }
            } else {
                playTone();
                showDialog(myApp.getAppContext().getString(R.string.alert_plsenterCN));
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();

        }
    }

    private void submitManualEntry() {
        if (!txtJobsheetSubmitMANUALENTRY.getText().toString().equals("")) {
            int foundidx = -1;
            int bulkycount = 0;
            boolean jobFound = false;
            View v;
            TextView lblCnno;
            String currentDateTime = "";
            Date dt = new Date();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            currentDateTime = formatter.format(dt);
            String currentScannedItem = "";
            Jobsheet_PostData data = null;
            String Cnno = "";
            String scanned = txtJobsheetSubmitMANUALENTRY.getText().toString().toLowerCase();
            scannedItems = getScannedItems(objGlobalMethods.getGcmPreferences());

            if (!scanned.equals("bulky") && !scanned.equals("prouter") && !scanned.equals("nestle") && !scanned.equals("groupon") && !scanned.equals("wmg") && !scanned.equals("edenred")) {
                for (int i = 0; i <= listView.getCount() - 1; i++) {
                    v = listView.getAdapter().getView(i, null, null);
                    lblCnno = (TextView) v.findViewById(R.id.lblJobsheetItemTitle);
                    Cnno = lblCnno.getText().toString();

                    if (scanned.equals(Cnno.toLowerCase())) {
                        jobFound = true;
                        foundidx = i;
                        break;
                    }
                }
            } else if (scanned.equals("bulky")) {
                if (listView.getCount() > 0) {
                    v = listView.getAdapter().getView(listView.getCount() - 1, null, null);
                    lblCnno = (TextView) v.findViewById(R.id.lblJobsheetItemTitle);
                    Cnno = lblCnno.getText().toString();
                    if (Cnno.toLowerCase().equals("bulky")) {
                        playTone();
                        showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_plsenterCN));
                        return;
                    }
                }
            } else {
                if (listView.getCount() > 0) {
                    v = listView.getAdapter().getView(listView.getCount() - 1, null, null);
                    lblCnno = (TextView) v.findViewById(R.id.lblJobsheetItemTitle);
                    Cnno = lblCnno.getText().toString();
                    if (Cnno.toLowerCase().equals("bulky") || Cnno.toLowerCase().equals("prouter") || Cnno.toLowerCase().equals("nestle") || Cnno.toLowerCase().equals("groupon") || Cnno.toLowerCase().equals("wmg") || Cnno.toLowerCase().equals("edenred")) {
                        playTone();
                        showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_plsenterCN));
                        return;
                    }
                }
            }

            if (!jobFound) {
                currentScannedItem = "\"" + lblJobsheetSubmitIDval.getText().toString().toUpperCase() + "\"" + "," + "\"" + scanned.toUpperCase() + "\"" + "," + "\"" + currentDateTime.toUpperCase() + "\"";
                data = new Jobsheet_PostData(lblJobsheetSubmitIDval.getText().toString().toUpperCase(), scanned.toUpperCase(), currentDateTime.toUpperCase(), currentScannedItem);
                if (scannedItems != null) {
                    scannedItems.add(data);
                    if (adapter == null) {
                        adapter = new Jobsheet_PostItemAdapter(this, scannedItems);
                        listView.setAdapter(adapter);
                    } else {
                        //dunno why yet must do the following lines this way compared to BulkPickUpActivity
                        adapter.clear();
                        adapter.addAll(scannedItems);
                        adapter.notifyDataSetChanged();
                    }
                    objGlobalMethods.writeToFile("JS " + scanned.toUpperCase(), "", "", "", "");
                    Gson gson = new Gson();
                    String jobsheetSubmitscannedItemsPrefStr = "";
                    jobsheetSubmitscannedItemsPrefStr = gson.toJson(scannedItems);
                    objGlobalMethods.setGCMPrefs(objGlobalMethods.getGcmPreferences(), "jobsheetSubmitscannedItemsPrefStr", jobsheetSubmitscannedItemsPrefStr);
                } else {
                    scannedItems = new ArrayList<Jobsheet_PostData>();
                    scannedItems.add(data);
                    adapter = new Jobsheet_PostItemAdapter(this, scannedItems);
                    listView.setAdapter(adapter);
                    objGlobalMethods.writeToFile("JS " + scanned.toUpperCase(), "", "", "", "");
                    Gson gson = new Gson();
                    String jobsheetSubmitscannedItemsPrefStr = "";
                    jobsheetSubmitscannedItemsPrefStr = gson.toJson(scannedItems);
                    objGlobalMethods.setGCMPrefs(objGlobalMethods.getGcmPreferences(), "jobsheetSubmitscannedItemsPrefStr", jobsheetSubmitscannedItemsPrefStr);
                }

                //runs after listview is done updating itself
                listView.post(new Runnable() {
                    public void run() {
                        //scroll the listview to the last item
                        listView.setSelection(listView.getCount() - 1);
                        //focus certain view or any other calls after listview completed updating

                    }
                });

                playSuccessVerifiedTone();
                lblJobsheetSubmitCOUNT.setText(String.valueOf(scannedItems.size() - getBulkyCount2(scannedItems)));
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnJobsheetSubmitSUBMIT);
                focusTextbox();
            } else {
                playTone();
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_duplicatescan) + "\n" + txtJobsheetSubmitMANUALENTRY.getText().toString());

                ////the following code wun prompt but highlight duplicate, gc claimed not good, must rescan, did not highlight e dup, but i tested got highlight (blink)
                /*final int foundPos = foundidx;
                playSuccessVerifiedTone();
                //dunno why yet must do the following lines this way compared to BulkPickUpActivity
                adapter.clear();
                scannedItems.get(foundPos).setBlink(true);
                adapter.addAll(scannedItems);
                adapter.notifyDataSetChanged();

                listView.post(new Runnable() {
                    public void run() {
                        try {
                            //scroll the listview to the last item
                            listView.setSelection(foundPos);
                            //focus certain view or any other calls after listview completed updating
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception at JobhsheetSubmitActivity listView.setSelection(foundPos);. Exception: " + e.getMessage(), "", "", "", "");
                        }
                    }
                });*/
            }
        } else {
            playTone();
            showDialog(myApp.getAppContext().getString(R.string.alert_plsenterCN));
        }
    }

    private int getBulkyCount2(ArrayList<Jobsheet_PostData> osi) {
        Jobsheet_PostData data = null;
        int bcount = 0;
        try {
            for (int i = 0; i <= osi.size() - 1; i++) {
                data = osi.get(i);
                if (data.postTitle.toLowerCase().equals("bulky") || data.postTitle.toLowerCase().equals("prouter") || data.postTitle.toLowerCase().equals("nestle") ||
                        data.postTitle.toLowerCase().equals("groupon") || data.postTitle.toLowerCase().equals("wmg") || data.postTitle.toLowerCase().equals("edenred")) {
                    bcount += 1;
                }
            }
        } catch (Exception e) {

        } finally {
            return bcount;
        }
    }

    private int getBulkyCount() {
        Jobsheet_PostData data = null;
        int bcount = 0;
        try {
            for (int i = 0; i <= scannedItems.size() - 1; i++) {
                data = scannedItems.get(i);
                if (data.postTitle.toLowerCase().equals("bulky") || data.postTitle.toLowerCase().equals("prouter") || data.postTitle.toLowerCase().equals("nestle") ||
                        data.postTitle.toLowerCase().equals("groupon") || data.postTitle.toLowerCase().equals("wmg") || data.postTitle.toLowerCase().equals("edenred")) {
                    bcount += 1;
                }
            }
        } catch (Exception e) {

        } finally {
            return bcount;
        }

    }

    public void btnJobsheetSubmitENTERClick(View view) {
        submitManualEntry();
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void btnJobsheetSubmitCANCELClick(View view) {
        initBack();
    }

    public void btnJobsheetSubmitCLEARALLClick(View view) {
        scannedItems = getScannedItems(objGlobalMethods.getGcmPreferences());

        if (scannedItems != null && scannedItems.size() > 0 && adapter != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);

            builder.setMessage(myApp.getAppContext().getString(R.string.alert_cleardata));

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    try {
                        clear();
                    } catch (Exception e) {
                        lblJobsheetSubmitCOUNT.setText("0");
                        //ctrlSettings.enableDisableSubmitButton(false, btnJobsheetSubmitSUBMIT);
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnJobsheetSubmitSUBMIT);
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
    }

    public void btnJobsheetSCANClick(View v) {
        launchQRScan();
    }

    private void clear() {
        objGlobalMethods.setGCMPrefs(GcmPreferences, "jobsheetSubmitscannedItemsPrefStr", "");
        scannedItems.clear();
        adapter.clear();
        adapter.notifyDataSetChanged();
        //ctrlSettings.enableDisableSubmitButton(false, btnJobsheetSubmitSUBMIT);
        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnJobsheetSubmitSUBMIT);
        objGlobalMethods.writeToFile("GC chose to clear data at JobsheetSubmitActivity.", "", "", "", "");
        lblJobsheetSubmitCOUNT.setText(String.valueOf(scannedItems.size()));
        focusTextbox();
    }

    public void btnJobsheetSubmitSUBMITClick(View view) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String JobsheetScannedUsername = objGlobalMethods.getGCMPrefs(GcmPreferences, "JobsheetScannedUsername");
        scannedItems = getScannedItems(GcmPreferences);

        try {
            if (scannedItems != null && scannedItems.size() > 0) {
                boolean shuttle = isShuttleModeOnAction();
                String msg = shuttle ?
                        myApp.getAppContext().getString(R.string.alert_submitshuttle) :
                        myApp.getAppContext().getString(R.string.alert_submitjobsheet);

                ishomesuser = lbljobsheetSubmitIsHomesUser.getText().toString();
                if (!Boolean.parseBoolean(ishomesuser)) {
                    showDriverSubmissionPOP(msg, shuttle);
                } else {
                    if (JobsheetScannedUsername.equals("S8888888B")
                            || JobsheetScannedUsername.equals("S8888888A")
                            || JobsheetScannedUsername.equals("S8888888C")
                            || JobsheetScannedUsername.equals("S8888888S")
                            || JobsheetScannedUsername.equals("S8888888X")
                            || JobsheetScannedUsername.equals("S8888887B")
                            || JobsheetScannedUsername.equals("GROUPON")
                            || JobsheetScannedUsername.equals("NESTLE")
                            || JobsheetScannedUsername.equals("PROUTER")
                            || JobsheetScannedUsername.substring(0, 3).equals("WL00")
                            || shuttle) {

                        showHomesUserSubmissionPOP(msg, shuttle);
                    } else {

                        showDriverSubmissionPOP(msg, shuttle);
                    }
                }
            } else {
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_enteratleastonejob));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at JobsheetSubmitActivity btnJobsheetSubmitSUBMITClick. Exception: " + e.toString(), "", "", "", "");
            focusTextbox();
            showDialog("Error submitting Jobsheet due following error." + "\n" + e.getMessage());
        }
    }

    private void showDriverSubmissionPOP(String msg, final boolean shuttle) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(msg);

            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_jobsheetsubmitcancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_jobsheetsubmityes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status , no printing
                    try {
                        scannedItems = getScannedItems(objGlobalMethods.getGcmPreferences());
                        submitJS(false, scannedItems, lblJobsheetSubmitIDval.getText().toString(), shuttle);

                        scannedItems = null;
                    } catch (Exception e) {
                        showDialog("Error submitting Jobsheet due following error." + "\n" + e.getMessage());
                    }
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at JobsheetSubmitActivity showDriverSubmissionPOP. Exception: " + e.toString(), "", "", "", "");
        }
    }

    private void showHomesUserSubmissionPOP(String msg, final boolean shuttle) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("");
            builder.setMessage(msg);

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_jobsheetsubmityes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    try {
                        scannedItems = getScannedItems(objGlobalMethods.getGcmPreferences());
                        submitJS(false, scannedItems, lblJobsheetSubmitIDval.getText().toString(), shuttle);
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception caught at JobsheetSubmitActivity showHomesUserSubmissionPOP setPositiveButton. Exception: " + e.toString(), "", "", "", "");
                        showDialog(myApp.getAppContext().getString(R.string.alert_errSubmitJobsheet));
                    }
                }
            });

            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_jobsheetsubmitcancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at JobsheetSubmitActivity showHomesUserSubmissionPOP. Exception: " + e.toString(), "", "", "", "");
        }
    }

    private void submitJS(Boolean PrintJobSheet, ArrayList<Jobsheet_PostData> scannedItemsAL, String lblJobsheetSubmitIDval, boolean shuttle) {
        Date dt = new Date();
        Boolean asd = true;
        String[] JSDarr;
        String currentScannedItem = "";
        Jobsheet_PostData data = null;
        String currentDateTime = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        currentDateTime = formatter.format(dt);
        String scannedItemsStr = "none";

        try {
            objGlobalMethods.writeToFile("JobsheetSubmitActivity submitJS started.", "", "", "", "");

            try {
                if (PrintJobSheet) {
                    currentScannedItem = "\"" + lblJobsheetSubmitIDval.toUpperCase() + "\"" + "," + "\"" + "PRINT" + "\"" + "," + "\"" + currentDateTime.toUpperCase() + "\"";
                    data = new Jobsheet_PostData(lblJobsheetSubmitIDval.toUpperCase(), "PRINT", currentDateTime.toUpperCase(), currentScannedItem);
                    scannedItemsAL.add(data);
                }

                JSDarr = new String[scannedItemsAL.size()];
                for (int i = 0; i <= scannedItemsAL.size() - 1; i++) {
                    JSDarr[i] = scannedItemsAL.get(i).combinedStr;
                    if (i == 0) {
                        scannedItemsStr = "";
                        scannedItemsStr += scannedItemsAL.get(i).postTitle + ", ";
                    } else if (i > 0) {
                        scannedItemsStr += scannedItemsAL.get(i).postTitle + ", ";
                    } else if (i == scannedItemsAL.size() - 1) {
                        scannedItemsStr += scannedItemsAL.get(i).postTitle;
                    }
                }

                scannedItems = null;

                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                if (shuttle) {
                    JobsHandler.addUploadShuttleQueALPrefs(UpdateJobInfoPreferences, JSDarr);
                    objGlobalMethods.writeToFile("JobsHandler.addUploadShuttleQueALPrefs: " + scannedItemsStr, "", "", "", "");
                } else {
                    JobsHandler.addUploadJobsheetQueALPrefs(UpdateJobInfoPreferences, JSDarr);
                    objGlobalMethods.writeToFile("JobsHandler.addUploadJobsheetQueALPrefs: " + scannedItemsStr, "", "", "", "");
                }

                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                asd = true;
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at JobsheetSubmitActivity submitJS inner TryCatch. Exception: " + e.toString(), "", "", "", "");
                asd = false;
            }

            objGlobalMethods.writeToFile("JobsheetSubmitActivity submitJS ended.", "", "", "", "");

            if (asd) {
                pd.dismiss();
                scannedItems = null;
                playSuccessVerifiedTone();
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                String[] prefName = new String[]{"JobsheetScannedIDnUsername", "jobsheetSubmitscannedItemsPrefStr"};
                String[] prefvalue = new String[]{"", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];
                scannedItems = null;
                Intent parentIntent;
                if (Boolean.parseBoolean(ishomesuser)) {
                    parentIntent = new Intent(JobsheetSubmitActivity.this, JobsheetActivity.class);
                } else {
                    boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                    parentIntent = new Intent(JobsheetSubmitActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                }
                MainActivity.uponLogin = 0;
                startActivity(parentIntent);
                finish();
            } else {
                pd.dismiss();
                showDialog(myApp.getAppContext().getString(R.string.alert_errSubmitJobsheet));
                focusTextbox();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity submitJS. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errSubmitJobsheet));
            focusTextbox();
        }
    }

    public static void focusTextbox() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                listView.clearFocus();
                btnJobsheetSubmitSUBMIT.clearFocus();
                btnJobsheetSubmitCANCEL.clearFocus();
                btnJobsheetSCAN.clearFocus();
                btnJobsheetSubmitENTER.clearFocus();
                lblJobsheetSubmitCOUNT.clearFocus();
                lblJobsheetSubmitIDval.clearFocus();
                txtJobsheetSubmitMANUALENTRY.setText("");
                txtJobsheetSubmitMANUALENTRY.requestFocus();
            }
        }, 100);
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
            objGlobalMethods.writeToFile("Exception at JobhsheetSubmitActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at JobhsheetSubmitActivity playSuccessVerifiedTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playTone() {
        try {
            stopPlayingTone();
            Uri uri = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error);
            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, uri);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at JobhsheetSubmitActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private Dialog showDialogNfocusTxtbox(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                focusTextbox();
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // TODO: handle Intent
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
            objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity handleIntent. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at JobsheetSubmitActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsheetSubmitActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
