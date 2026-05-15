package com.xdel.luke.templogger;

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
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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

import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsheetActivity;
import com.xdel.luke.MainActivity;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.mpd.mpdclass;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AttDttJobActivity extends AppCompatActivity {
    Context context;
    ConnectionDetector cd;
    private MediaPlayer player;

    private static ListView listView;
    private static TextView lblAttDttJobLoggerID, lblAttDttJobDriverIDXval, lblAttDttJobSubmitCOUNT;
    private static EditText txtAttDttJobSubmitMANUALENTRY;
    private static Button btnAttDttJobSubmitENTER;
    private static Button btnAttDttJobSCAN;
    private static Button btnAttDttJobSubmitCANCEL;
    private static Button btnAttDttJobSubmitSUBMIT;
    private ProgressDialog pd, pdCheck;

    static boolean errored = false;
    private static SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    private String attdttDriverIDXval = "";
    private String LukeAPI;
    public static ArrayList<templogger_postdata> scannedItems = null;
    templogger_Adapter adapter = null;
    static String cameraScannedCode;
    private String langType = "en";
    NfcAdapter mNfcAdapter;
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");

        setContentView(R.layout.activity_att_dtt_job);

        listView = (ListView) findViewById(R.id.listView);
        lblAttDttJobLoggerID = (TextView) findViewById(R.id.lblAttDttJobLoggerID);
        lblAttDttJobDriverIDXval = (TextView) findViewById(R.id.lblAttDttJobDriverIDXval);
        lblAttDttJobSubmitCOUNT = (TextView) findViewById(R.id.lblAttDttJobSubmitCOUNT);
        txtAttDttJobSubmitMANUALENTRY = (EditText) findViewById(R.id.txtAttDttJobSubmitMANUALENTRY);
        btnAttDttJobSubmitENTER = (Button) findViewById(R.id.btnAttDttJobSubmitENTER);
        btnAttDttJobSCAN = (Button) findViewById(R.id.btnAttDttJobSCAN);
        btnAttDttJobSubmitCANCEL = (Button) findViewById(R.id.btnAttDttJobSubmitCANCEL);
        btnAttDttJobSubmitSUBMIT = (Button) findViewById(R.id.btnAttDttJobSubmitSUBMIT);
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

            setTitle(myApp.getAppContext().getString(R.string.title_activity_attjob));

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

            ArrayList<String> btNames = mpdclass.BTTL_Not_Reg_Yet();

            if (btNames != null && btNames.size() > 0) {
                String n = "";
                for (int i = 0; i<=btNames.size() -1; i++) {
                    n += btNames.get(i) + "\n";
                }
                String msg = n + "\n" + myApp.getAppContext().getString(R.string.temploggerregprompt);
                objGlobalMethods.showRegBTTL_DialogNew(msg, false);
            } else {
                initPage();
            }
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
        lblAttDttJobLoggerID = null;
        txtAttDttJobSubmitMANUALENTRY = null;
        btnAttDttJobSubmitENTER = null;
        btnAttDttJobSCAN = null;
        btnAttDttJobSubmitCANCEL = null;
        btnAttDttJobSubmitSUBMIT = null;
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at AttDttJobActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at AttDttJobActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at AttDttJobActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
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
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "attdttjobsubmit");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        parentIntent = new Intent(AttDttJobActivity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attdttjob, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.clear)
            btnAttDttJobCLEARALLClick(null);

        /*switch (item.getItemId()) {
            *//*case R.id.qrscan:
                launchQRScan();
                break;*//*
            case R.id.clear:
                btnAttDttJobCLEARALLClick(null);
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
        objGlobalMethods.setGCMPrefs(GcmPreferences, "AttDttJobSubmitscannedItemsPrefStr", "");
        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
        username = UsernamePwd[0];
        useridx = UsernamePwd[2];
        ishomesuser = UsernamePwd[3];
        scannedItems = null;
        Intent parentIntent;
        if (Boolean.parseBoolean(ishomesuser)) {
            parentIntent = new Intent(this, JobsheetActivity.class);
        } else {
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        }
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
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
                objGlobalMethods.setGCMPrefs(GcmPreferences, "BulkPickUpScannedIDnUsername", "");
                scannedItems = null;

                //Intent i = new Intent(BulkPickUpActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                Intent i = new Intent(AttDttJobActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                MainActivity.uponLogin = 0;
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initPage() {
        attdttDriverIDXval = objGlobalMethods.getGCMPrefs(GcmPreferences, "attdttDriverIDXval");
        if (attdttDriverIDXval.isEmpty()) {
            //show alert and return to previous page
            showErrorAlert("");
        } else {
            lblAttDttJobDriverIDXval.setText(attdttDriverIDXval);
            if (scannedItems != null) {
                lblAttDttJobSubmitCOUNT.setText(String.valueOf(scannedItems.size()));
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnAttDttJobSubmitSUBMIT);
            } else {
                lblAttDttJobSubmitCOUNT.setText("0");
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnAttDttJobSubmitSUBMIT);
            }
        }

        txtAttDttJobSubmitMANUALENTRY.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
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
            txtAttDttJobSubmitMANUALENTRY.setText(cameraScannedCode);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            submitManualEntry();
        }

        if (scannedItems != null && scannedItems.size() > 0 && adapter == null) {
            adapter = new templogger_Adapter(this, scannedItems);
            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                final int deleteRow = position;
                if (v == null)
                    return;

                TextView lblBulkPickUpItemTitle = (TextView) v.findViewById(R.id.lblBulkPickUpItemTitle);

                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                builder.setCancelable(false);
                builder.setMessage(myApp.getAppContext().getString(R.string.alert_submitjobsheetcfmDelete) + " " + lblBulkPickUpItemTitle.getText().toString() + " ?");
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status
                        scannedItems.remove(deleteRow);
                        if (scannedItems.size() > 0) {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnAttDttJobSubmitSUBMIT);
                        } else if (scannedItems.size() == 0) {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnAttDttJobSubmitSUBMIT);
                        }
                        lblAttDttJobSubmitCOUNT.setText(String.valueOf(scannedItems.size()));
                        adapter.notifyDataSetChanged();
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

    private void submitManualEntry() {
        if (!txtAttDttJobSubmitMANUALENTRY.getText().toString().equals("")) {
            int foundidx = -1;
            int bulkycount = 0;
            boolean jobFound = false;
            View v;
            TextView lblCnno;
            String currentDateTime = "";
            Date dt = new Date();
            //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            currentDateTime = formatter.format(dt);
            String currentScannedItem = "";
            templogger_postdata data = null;
            String Cnno = "";
            String scanned = txtAttDttJobSubmitMANUALENTRY.getText().toString().toUpperCase();
            objGlobalMethods.writeToFile("Spooled: " + scanned, "", "", "", "");

            if (listView.getCount() > 0) {
                for (int i = 0; i <= listView.getCount() - 1; i++) {
                    v = listView.getAdapter().getView(i, null, null);
                    lblCnno = (TextView) v.findViewById(R.id.lblBulkPickUpItemTitle);
                    Cnno = lblCnno.getText().toString();

                    if (scanned.equals(Cnno.toUpperCase())) {
                        jobFound = true;
                        foundidx = i;
                        break;
                    }
                }
            }

            if (!jobFound) {
                data = new templogger_postdata(scanned.toUpperCase());
                if (scannedItems != null) {
                    scannedItems.add(data);
                    if (adapter == null) {
                        adapter = new templogger_Adapter(this, scannedItems);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    scannedItems = new ArrayList<templogger_postdata>();
                    scannedItems.add(data);
                    adapter = new templogger_Adapter(this, scannedItems);
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

                playSuccessVerifiedTone();
                lblAttDttJobSubmitCOUNT.setText(String.valueOf(scannedItems.size()));
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnAttDttJobSubmitSUBMIT);
                focusTextbox();
            } else {
                final int foundPos = foundidx;
                listView.post(new Runnable() {
                    public void run() {
                        try {
                            //scroll the listview to the last item
                            listView.setSelection(foundPos);
                            //focus certain view or any other calls after listview completed updating
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception at BulkPickUpActivity listView.setSelection(foundPos);. Exception: " + e.getMessage(), "", "", "", "");
                        }
                    }
                });

                playSuccessVerifiedTone();
                scannedItems.get(foundPos).setBlink(true);
                adapter.notifyDataSetChanged();
            }
        } else {
            playTone();
            //showDialog("Please enter something.");
            Context c = myApp.getAppContext();
            objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok),c.getString(R.string.alert_enteratleastonejob)).show();
        }
    }

    public void btnBulkPickUpENTERClick(View view) {
        submitManualEntry();
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void btnAttDttJobSubmitCANCELClick(View view) {
        initBack();
    }

    public void btnAttDttJobCLEARALLClick(View view) {
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
                        lblAttDttJobSubmitCOUNT.setText("0");
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnAttDttJobSubmitSUBMIT);
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

    public void btnAttDttJobSCANClick(View v) {
        launchQRScan();
    }

    private void clear() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        scannedItems.clear();
        adapter.clear();
        adapter.notifyDataSetChanged();
        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnAttDttJobSubmitSUBMIT);
        lblAttDttJobSubmitCOUNT.setText(String.valueOf(scannedItems.size()));
        focusTextbox();
    }

    public static void focusTextbox() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                listView.clearFocus();
                btnAttDttJobSubmitSUBMIT.clearFocus();
                btnAttDttJobSubmitCANCEL.clearFocus();
                btnAttDttJobSCAN.clearFocus();
                btnAttDttJobSubmitENTER.clearFocus();
                lblAttDttJobSubmitCOUNT.clearFocus();
                txtAttDttJobSubmitMANUALENTRY.setText("");
                txtAttDttJobSubmitMANUALENTRY.requestFocus();
            }
        }, 100);
    }

    private void stopPlayingTone(){
        try {
            if (player != null) {
                player.reset();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at BulkPickUpActivity stopPlayingTone. Exception: " + e.getMessage(),"","","","");
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
            objGlobalMethods.writeToFile("Exception at BulkPickUpActivity playSuccessVerifiedTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playTone() {
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity,  Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error));
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at BulkPickUpActivity playTone. Exception: " + e.getMessage(),"","","","");
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

}