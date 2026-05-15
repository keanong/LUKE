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
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
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

import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.BulkPickUp_PostData;
import com.xdel.luke.objClasses.BulkPickUp_PostItemAdapter;
import com.xdel.luke.objClasses.LocationProvider;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objBulkPickUp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.temploggerclass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BulkPickUpActivity extends AppCompatActivity {

    Context context;
    ConnectionDetector cd;
    private MediaPlayer player;
    //private static String regid;
    private static ListView listView;
    private static TextView lblBulkPickUpIDval;
    private static TextView lblBulkPickUpCOUNT;
    private static EditText txtBulkPickUpMANUALENTRY;
    private static Button btnBulkPickUpENTER;
    private static Button btnBulkPickUpSCAN;
    private static Button btnBulkPickUpCANCEL;
    private static Button btnBulkPickUpSUBMIT;
    private ProgressDialog pd, pdCheck;
    private LocationProvider mLocationProvider;

    static boolean errored = false;
    private static SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    private static String BulkPickUpScannedIDnUsername = "";
    private String LukeAPI;
    public static ArrayList<BulkPickUp_PostData> scannedItems = null;
    BulkPickUp_PostItemAdapter adapter = null;
    String cameraScannedCode;
    private String langType = "en";
    NfcAdapter mNfcAdapter;
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;

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

        setContentView(R.layout.activity_bulk_pu);

        listView = (ListView) findViewById(R.id.listView);
        lblBulkPickUpIDval = (TextView) findViewById(R.id.lblBulkPickUpIDval);
        lblBulkPickUpCOUNT = (TextView) findViewById(R.id.lblBulkPickUpCOUNT);
        txtBulkPickUpMANUALENTRY = (EditText) findViewById(R.id.txtBulkPickUpMANUALENTRY);
        btnBulkPickUpENTER = (Button) findViewById(R.id.btnBulkPickUpENTER);
        btnBulkPickUpSCAN = (Button) findViewById(R.id.btnBulkPickUpSCAN);
        btnBulkPickUpCANCEL = (Button) findViewById(R.id.btnBulkPickUpCANCEL);
        btnBulkPickUpSUBMIT = (Button) findViewById(R.id.btnBulkPickUpSUBMIT);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        //pd.setMessage("Please wait. Submitting Info...");
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));
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
            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                context = myApp.getAppContext();

                myApp.currentActivity = this;
                setTitle(myApp.getAppContext().getString(R.string.title_activity_bulk_pick_up));

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
                    objGlobalMethods.writeToFile("BulkPickUpActivity !BTEnabled", "", "", "", "");
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
                    //objGlobalMethods.writeToFile("Exception caught at BulkPickUpActivity onResume mNfcAdapter portion. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
                }


                JobsHandler.checkForceLogOut();
                objGlobalMethods.setGCMPrefs(GcmPreferences, "dailypoints", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "NFCLocationtagPrefVal", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
                DashboardSubActivity.frompage = null;

                //cd = new ConnectionDetector(context);
                cd = new ConnectionDetector(null);
                //regid = getRegistrationId(context);
                String RegID = objGlobalMethods.getRegistrationIdOnly();
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }
                initPage();
            } catch (Exception e) {
                //String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at BulkPickUpActivity onResume. Exception: " + e.getMessage().toString(), "", "", "", "");
            }
        }
    }

    @Override
    protected void onDestroy() {
        listView = null;
        lblBulkPickUpIDval = null;
        lblBulkPickUpCOUNT = null;
        txtBulkPickUpMANUALENTRY = null;
        btnBulkPickUpENTER = null;
        btnBulkPickUpSCAN = null;
        btnBulkPickUpCANCEL = null;
        btnBulkPickUpSUBMIT = null;
        //regid = null;
        GcmPreferences = null;
        UpdateJobInfoPreferences = null;
        BulkPickUpScannedIDnUsername = null;
        scannedItems = null;
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at BulkPickUpActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
        super.onDestroy();
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at BulkPickUpActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at BulkPickUpActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at BulkPickUpActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at BulkPickUpActivity showChangePermissionDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String[] prefName = new String[]{"QRScanRequestIntent", "cameraScannedCode"};
        String[] prefvalue = new String[]{"bulkpickup", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        parentIntent = new Intent(BulkPickUpActivity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bulk_pick_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.qrscan) {
            launchQRScan();
        } else if (item.getItemId() == R.id.clear) {
            btnBulkPickUpCLEARALLClick(null);
        }

        /*switch (item.getItemId()) {
            case R.id.qrscan:
                launchQRScan();
                break;
            case R.id.clear:
                btnBulkPickUpCLEARALLClick(null);
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
            //builder.setMessage("Moving away from thus screen will CLEAR all scanned information. Confirm to clear ALL pending data?");
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_moveawayclearall));
            //builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    back();
                }
            });
            //builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
        String[] prefName = new String[]{"BulkPickUpScannedIDnUsername", "cameraScannedCode"};
        String[] prefvalue = new String[]{"", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        scannedItems = null;
        Intent parentIntent;
        //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
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
            objGlobalMethods.writeToFile("Exception at BulkPickUpActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at BulkPickUpActivity onPause unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
    }

    private Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

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
                objGlobalMethods.setGCMPrefs(GcmPreferences, "BulkPickUpScannedIDnUsername", "");
                scannedItems = null;

                //Intent i = new Intent(BulkPickUpActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                Intent i = new Intent(BulkPickUpActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                MainActivity.uponLogin = 0;
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSubmissionOKAlert(int JSPcount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(JSPcount + " " + myApp.getAppContext().getString(R.string.alert_BPUdatasubmitted));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                String[] prefName = new String[]{"BulkPickUpScannedIDnUsername", "cameraScannedCode"};
                String[] prefvalue = new String[]{"", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                scannedItems = null;

                //Intent parentIntent = new Intent(BulkPickUpActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                Intent parentIntent = new Intent(BulkPickUpActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                MainActivity.uponLogin = 0;
                startActivity(parentIntent);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initPage() {
        BulkPickUpScannedIDnUsername = objGlobalMethods.getGCMPrefs(GcmPreferences, "BulkPickUpScannedIDnUsername");
        if (BulkPickUpScannedIDnUsername.isEmpty()) {
            //show alert and return to previous page
            showErrorAlert("");
        } else {
            lblBulkPickUpIDval.setText(BulkPickUpScannedIDnUsername);
            if (scannedItems != null) {
                lblBulkPickUpCOUNT.setText(String.valueOf(scannedItems.size()));
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnBulkPickUpSUBMIT);
            } else {
                lblBulkPickUpCOUNT.setText("0");
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnBulkPickUpSUBMIT);
            }
        }

        txtBulkPickUpMANUALENTRY.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
            txtBulkPickUpMANUALENTRY.setText(cameraScannedCode);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            submitManualEntry();
        }

        if (scannedItems != null && scannedItems.size() > 0 && adapter == null) {
            adapter = new BulkPickUp_PostItemAdapter(this, scannedItems);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(BulkPickUpActivity.this);
                builder.setCancelable(false);
                builder.setMessage(myApp.getAppContext().getString(R.string.alert_submitjobsheetcfmDelete) + " " + lblBulkPickUpItemTitle.getText().toString() + " ?");
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status
                        scannedItems.remove(deleteRow);
                        if (scannedItems.size() > 0) {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnBulkPickUpSUBMIT);
                        } else if (scannedItems.size() == 0) {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnBulkPickUpSUBMIT);
                        }
                        lblBulkPickUpCOUNT.setText(String.valueOf(scannedItems.size()));
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
        if (!txtBulkPickUpMANUALENTRY.getText().toString().equals("")) {
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
            BulkPickUp_PostData data = null;
            String Cnno = "";
            String scanned = txtBulkPickUpMANUALENTRY.getText().toString().toUpperCase();
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
                data = new BulkPickUp_PostData(scanned.toUpperCase());
                if (scannedItems != null) {
                    scannedItems.add(data);
                    if (adapter == null) {
                        adapter = new BulkPickUp_PostItemAdapter(this, scannedItems);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    scannedItems = new ArrayList<BulkPickUp_PostData>();
                    scannedItems.add(data);
                    adapter = new BulkPickUp_PostItemAdapter(this, scannedItems);
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
                lblBulkPickUpCOUNT.setText(String.valueOf(scannedItems.size()));
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnBulkPickUpSUBMIT);
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
            showDialog(myApp.getAppContext().getString(R.string.alert_enteratleastonejob));
        }
    }

    public void btnBulkPickUpENTERClick(View view) {
        submitManualEntry();
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void btnBulkPickUpCANCELClick(View view) {
        initBack();
    }

    public void btnBulkPickUpCLEARALLClick(View view) {
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
                        lblBulkPickUpCOUNT.setText("0");
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnBulkPickUpSUBMIT);
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

    public void btnBulkPickUpSCANClick(View v) {
        launchQRScan();
    }

    private void clear() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        scannedItems.clear();
        adapter.clear();
        adapter.notifyDataSetChanged();
        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnBulkPickUpSUBMIT);
        lblBulkPickUpCOUNT.setText(String.valueOf(scannedItems.size()));
        focusTextbox();
    }

    public void btnBulkPickUpSUBMITClick(View view) {
        final Date dt = new Date();
        try {
            if (scannedItems != null && scannedItems.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(myApp.getAppContext().getString(R.string.alert_submitBPU));
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status
                        try {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                            String RegID = objGlobalMethods.getRegistrationIdOnly();
                            submit(scannedItems, RegID, GcmPreferences, UpdateJobInfoPreferences);

                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception caught at BulkPickUpActivity btnBulkPickUpSUBMITClick. Exception: " + e.toString(), "", "", "", "");
                            showDialog(myApp.getAppContext().getString(R.string.alert_errSubmitJobsheet));
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
                //showDialog("Error submitting Bulk Pick Up data." + "\n" + "0 Scanned data saved.");
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_enteratleastonejob));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at BulkPickUpActivity btnBulkPickUpSUBMITClick. Exception: " + e.toString(), "", "", "", "");
        }
    }

    private void submit(ArrayList<BulkPickUp_PostData> scannedItemsAL, String aregid, SharedPreferences gcmp, SharedPreferences updatep) {
        Date dt = new Date();
        Boolean asd = true;
        Long l = dt.getTime();
        String timestampstr = "/Date(" + l + ")/";
        objBulkPickUp objBulkPickUpobj = null;
        String[] JSParr;
        int JSPcount = 0;

        try {
            objGlobalMethods.writeToFile("BulkPickUpActivity submit started.", "", "", "", "");
            JSParr = new String[scannedItemsAL.size()];
            try {
                for (int i = 0; i <= scannedItemsAL.size() - 1; i++) {
                    JSParr[i] = scannedItemsAL.get(i).Cnno;
                }
                Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                if (aregid.isEmpty() || aregid.equals("")) {
                    aregid = objGlobalMethods.getRegistrationIdOnly();
                }
                objBulkPickUpobj = new objBulkPickUp(aregid, JSParr, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude());
                JobsHandler.addUploadBulkPickUpQueALPrefs(updatep, objBulkPickUpobj);

                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                JSPcount = JSParr.length;
                scannedItems = null;
                asd = true;
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at BulkPickUpActivity submit inner TryCatch. Exception: " + e.toString(), "", "", "", "");
                asd = false;
            }

            if (asd) {
                objGlobalMethods.writeToFile("BulkPickUpActivity submit successful. submit ended.", "", "", "", "");
                JSPcount = JSParr.length;
                scannedItems = null;
                showSubmissionOKAlert(JSPcount);
            } else {
                objGlobalMethods.writeToFile("BulkPickUpActivity submit fails. submit ended.", "", "", "", "");
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_submittedfail));
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at BulkPickUpActivity submit. Exception: " + e.toString(), "", "", "", "");
            showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_submittedfail));
        }
    }

    public static void focusTextbox() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                listView.clearFocus();
                btnBulkPickUpSUBMIT.clearFocus();
                btnBulkPickUpCANCEL.clearFocus();
                btnBulkPickUpSCAN.clearFocus();
                btnBulkPickUpENTER.clearFocus();
                lblBulkPickUpCOUNT.clearFocus();
                lblBulkPickUpIDval.clearFocus();
                txtBulkPickUpMANUALENTRY.setText("");
                txtBulkPickUpMANUALENTRY.requestFocus();
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // TODO: handle Intent
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
            objGlobalMethods.writeToFile("Exception at BulkPickUpActivity handleIntent. Exception: " + e.getMessage().toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at BulkPickUpActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at BulkPickUpActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
