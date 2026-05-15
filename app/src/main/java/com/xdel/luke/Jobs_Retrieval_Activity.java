package com.xdel.luke;

import android.app.Activity;
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
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.Jobs_Retrieval_PostItemAdapter;
import com.xdel.luke.objClasses.Jobs_Retrieval_PostItemAdapter_p1;
import com.xdel.luke.objClasses.Jobs_Retrieval_Tags_PostData_p1;
import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.NFCTag;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjJobs;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Jobs_Retrieval_Activity extends AppCompatActivity {
    Context context;
    ConnectionDetector cd;
    private MediaPlayer player;
    private static SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    Jobs_Retrieval_PostItemAdapter adapter = null;
    public static Activity activity;
    public static ArrayList<Jobs_Retrieval_Tags_PostData_p1> p1AL = new ArrayList<>();
    public static String cameraScannedCode;
    public static String fromPage = "";
    public static ListView listView;
    public static ProgressDialog progressDialog;
    private Button btnJRPUtoggle;
    private Button btnJRDIPtoggle;
    private Button btnJRfail;
    private Button btnJRpu;
    private Button btnJRokp;
    private TextView lbljrAddress;
    private TextView lbljrTotalbagsentered;
    private TextView lbljrTotalbagsdivider;
    private TextView lbljrTotalbags;
    public static TextView lbljrTotaljobs;
    private TextView lblpufmdelto;
    private EditText txtjrBagInput;
    private ProgressDialog pd;
    private ProgressDialog pd2;
    public final String MIME_TEXT_PLAIN = "text/plain";

    NfcAdapter mNfcAdapter;
    PendingIntent nfcPendingIntent;
    Boolean isFromLocationNFCscan = false;
    Boolean isFromLocationBCscan = false;
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot
        setContentView(R.layout.activity_jobs__retrieval_);

        listView = (ListView) findViewById(R.id.listView);
        //lbljrTotalVal = (TextView) findViewById(R.id.lbljrTotalVal);
        //lbljrtotal = (TextView) findViewById(R.id.lbljrtotal);
        lbljrAddress = (TextView) findViewById(R.id.lbljrAddress);
        lbljrTotalbagsentered = (TextView) findViewById(R.id.lbljrTotalbagsentered);
        lbljrTotalbagsdivider = (TextView) findViewById(R.id.lbljrTotalbagsdivider);
        lbljrTotalbags = (TextView) findViewById(R.id.lbljrTotalbags);
        lbljrTotaljobs = (TextView) findViewById(R.id.lbljrTotaljobs);
        //txtjrBagInput = (EditText) findViewById(R.id.txtjrBagInput);
        lblpufmdelto = (TextView) findViewById(R.id.lblpufmdelto);
        //txtjrInput = (EditText) findViewById(R.id.txtjrInput);

        btnJRPUtoggle = (Button) findViewById(R.id.btnJRPUtoggle);
        btnJRDIPtoggle = (Button) findViewById(R.id.btnJRDIPtoggle);
        btnJRfail = (Button) findViewById(R.id.btnJRfail);
        btnJRpu = (Button) findViewById(R.id.btnJRpu);
        btnJRokp = (Button) findViewById(R.id.btnJRokp);
        //btnjrENTER = (Button) findViewById(R.id.btnjrENTER);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);

        activity = this;

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
            //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            try {
                context = getApplicationContext();

                objGlobalMethods.setGCMPrefs(GcmPreferences, "PreDeterminedStatusCodeIDX", "");

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
                        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                        mNfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilter, null);
                    }

                    if (mNfcAdapter != null && !myApp.isNfcEnabled(mNfcAdapter, context)) {
                        String jrbcscanning = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrbcscanning");
                        if (jrbcscanning.equals("")) {
                            showChangeNfcSettingDialog();
                            return;
                        }
                    }
                } catch (Exception e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception caught at Jobs_Retrieval_Activity onResume mNfcAdapter portion. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
                }

                DashboardSubActivity.frompage = null;
                JobsHandler.checkForceLogOut();

                //cd = new ConnectionDetector(context);
                cd = new ConnectionDetector(null);

                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    //Toast.makeText(context, "Please connect to working Internet connection.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }

                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");

                initPage();
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at Jobs_Retrieval_Activity onResume. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listView = null;
        lbljrAddress = null;
        btnJRfail = null;
        btnJRpu = null;
        btnJRokp = null;
        progressDialog = null;
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity onDestroy unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
        DAO_Jobs.nullifystaticitems();
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        try {
            if (mNfcAdapter != null)
                mNfcAdapter.disableForegroundDispatch(this);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity onPause unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }

        super.onPause();
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at Jobs_Retrieval_Activity showMobileNetworkSettingDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at Jobs_Retrieval_Activity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at Jobs_Retrieval_Activity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at Jobs_Retrieval_Activity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at Jobs_Retrieval_Activity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeNfcSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_nfc));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent myIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at Jobs_Retrieval_Activity showChangeNfcSettingDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });

        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, "jrbcscanning", "xxx");
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
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
            default:
                return super.dispatchKeyEvent(event);
        }

    }

    private void launchQRScan() {
        Intent parentIntent;
        GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "jobsretrieval");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        parentIntent = new Intent(Jobs_Retrieval_Activity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jobs__retrieval_, menu);
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
                launchQRScan();
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            back();
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Jobs_Retrieval_Activity onBackPressed. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void back() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        JobsHandler.clearJobsRetrievalActivityPrefs(GcmPreferences);
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        //Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
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

    private void initPage() {
        JobInfoActivity.frompage = null;

        /*txtjrInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (actionId == 0) {
                    //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }

                } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (txtjrInput.getText().toString().isEmpty()) {
                        playTone();
                        //showDialog("Consignment Note is empty.");
                        showDialog(myApp.getAppContext().getString(R.string.alert_plsenterCN));
                    } else {
                        //do smth

                    }
                } else if (actionId == 6) {
                    return false;
                }
                return true;
            }
        });*/

        objGlobalMethods.setGCMPrefs(GcmPreferences, "loadlistViaNFC", "0");
        cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
        if (!cameraScannedCode.equals("")) {
            isFromLocationNFCscan = false;
            isFromLocationBCscan = true;
        }

        String jrpudip = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrpudip");
        togglePUDIP(jrpudip, false);
    }

    public static void focusTextbox() {
        listView.clearFocus();
        listView.setFocusable(false);
        //lbljrTotalVal.clearFocus();
        /*btnjrENTER.clearFocus();
        txtjrInput.setText("");
        txtjrInput.clearFocus();
        txtjrInput.setFocusable(true);
        txtjrInput.setFocusableInTouchMode(true);*/
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
            objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity stopPlayingTone. Exception: " + e.getMessage(),"","","","");
        }
    }

    private void playTone() {
        /*player = MediaPlayer.create(Jobs_Retrieval_Activity.this, R.raw.error);
        player.start();*/
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(Jobs_Retrieval_Activity.this,  Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error));
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity playTone. Exception: " + e.getMessage(),"","","","");
        }
    }

    private void togglePUDIP(String jrpudip, Boolean isToggled) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (jrpudip.equals("0") || jrpudip == null || jrpudip.equals("")) {
                //PU
                btnJRpu.setVisibility(View.VISIBLE);
                btnJRokp.setVisibility(View.GONE);
                ctrlSettings.toggleButtonsWithStyles(true, btnJRPUtoggle);
                ctrlSettings.toggleButtonsWithStyles(false, btnJRDIPtoggle);
                ctrlSettings.enableDisableButtonWithStyles(false, btnJRpu);
                if (isToggled) {
                    p1AL = new ArrayList<>();
                    Jobs_Retrieval_PostItemAdapter_p1 adapter = new Jobs_Retrieval_PostItemAdapter_p1(myApp.getAppContext(), p1AL);
                    listView.setAdapter(adapter);
                    lbljrAddress.setText("-");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "jrpudip", "0");
                }
            } else if (jrpudip.equals("1")) {
                //dbs
                btnJRpu.setVisibility(View.GONE);
                btnJRokp.setVisibility(View.VISIBLE);
                ctrlSettings.toggleButtonsWithStyles(false, btnJRPUtoggle);
                ctrlSettings.toggleButtonsWithStyles(true, btnJRDIPtoggle);
                ctrlSettings.enableDisableButtonWithStyles(false, btnJRokp);
                if (isToggled) {
                    p1AL = new ArrayList<>();
                    Jobs_Retrieval_PostItemAdapter_p1 adapter = new Jobs_Retrieval_PostItemAdapter_p1(myApp.getAppContext(), p1AL);
                    listView.setAdapter(adapter);
                    lbljrAddress.setText("-");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "2");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "jrpudip", "1");
                }
            }

            String NFCtagScannedVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "NFCtagScannedVal");
            String nfcLocationTagPrefVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "NFCLocationtagPrefVal");
            String jraddressIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, "jraddressIDX");

            if (fromPage != null && !fromPage.isEmpty() && fromPage.equals("DrawerMainActivity")) {
                isFromLocationNFCscan = true;
                fromPage = null;
            }

            if (isFromLocationNFCscan) {
                isFromLocationNFCscan = false;
                //NFCTag nfcLocationTag = !NFCtagScannedVal.isEmpty() ? DAO_Jobs.getSearchForLocationNFCTagsRecord(NFCtagScannedVal) : null;
                NFCTag nfcLocationTag = null;
                Boolean isLocationTag = false;
                Boolean isBagTag = false;

                if (nfcLocationTagPrefVal.isEmpty() && !NFCtagScannedVal.isEmpty()) {
                    //check if NFCtagScannedVal is location tag
                    p1AL = null;
                    nfcLocationTag = DAO_Jobs.getSearchForLocationNFCTagsRecordViaNFC(NFCtagScannedVal);
                    isLocationTag = nfcLocationTag == null ? false : true;
                } else if (!nfcLocationTagPrefVal.isEmpty() && !NFCtagScannedVal.isEmpty() && nfcLocationTagPrefVal.equals(NFCtagScannedVal)) {
                    isLocationTag = true;
                } else if (!nfcLocationTagPrefVal.isEmpty() && !NFCtagScannedVal.isEmpty() && !nfcLocationTagPrefVal.equals(NFCtagScannedVal)) {
                    nfcLocationTag = DAO_Jobs.getSearchForLocationNFCTagsRecordViaNFC(NFCtagScannedVal);
                    isLocationTag = nfcLocationTag == null ? false : true;
                    isBagTag = !isLocationTag ? true : false;
                    if (isLocationTag) {
                        p1AL = null;
                    }
                }

                if (isLocationTag) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "NFCLocationtagPrefVal", NFCtagScannedVal);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "NFCtagScannedVal", "");
                    loadlistViaNFC3 t = new loadlistViaNFC3(NFCtagScannedVal);
                    t.execute();
                }/* else if (isBagTag) {
                    //process scannedtag with NFCtagScannedVal with current list
                    addremoveBags(NFCtagScannedVal);
                }*/
            } else if (isFromLocationBCscan) {
                isFromLocationBCscan = false;
                NFCTag nfcLocationTag = null;
                Boolean isLocationTag = false;
                Boolean isBagTag = false;

                if (jraddressIDX.isEmpty() && !cameraScannedCode.isEmpty()) {
                    //check if NFCtagScannedVal is location tag
                    p1AL = null;
                    nfcLocationTag = DAO_Jobs.getSearchForLocationNFCTagsRecordViaAddressIDX(cameraScannedCode);
                    isLocationTag = nfcLocationTag == null ? false : true;
                    jraddressIDX = isLocationTag ? cameraScannedCode : "";
                } else if (!jraddressIDX.isEmpty() && !cameraScannedCode.isEmpty() && jraddressIDX.equals(cameraScannedCode)) {
                    isLocationTag = true;
                } else if (!jraddressIDX.isEmpty() && !cameraScannedCode.isEmpty() && !jraddressIDX.equals(cameraScannedCode)) {
                    nfcLocationTag = DAO_Jobs.getSearchForLocationNFCTagsRecordViaAddressIDX(cameraScannedCode);
                    isLocationTag = nfcLocationTag == null ? false : true;
                    isBagTag = !isLocationTag ? true : false;
                    jraddressIDX = isLocationTag ? cameraScannedCode : "";
                    if (isLocationTag) {
                        p1AL = null;
                    }
                }

                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");

                if (isLocationTag) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "jraddressIDX", jraddressIDX);
                    LocationBarcodeSearch(jraddressIDX);
                }/* else if (isBagTag) {
                    //process scannedtag with NFCtagScannedVal with current list
                    //shd not have anything to do here since bag tagging is via NFC or manual No. of Bags entry, will not touch here
                }*/
            } else if (isToggled && !nfcLocationTagPrefVal.isEmpty()) {
                loadlistViaNFC3 t = new loadlistViaNFC3(nfcLocationTagPrefVal);
                t.execute();
            } else if (isToggled && !jraddressIDX.isEmpty()) {
                LocationBarcodeSearch(jraddressIDX);
            } else if (p1AL!= null && p1AL.size() > 0) {
                loadlistcommon3();
            }
        } catch (Exception e) {
            isFromLocationNFCscan = false;
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity togglePUDIP. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    public void btnJRPUClick(View view) {
        togglePUDIP("0", true);
    }

    public void btnJRDIPClick(View view) {
        togglePUDIP("1", true);
    }

    public void btnjrFAILclick(View v) {

    }

    public void btnjrPUclick(View v) {

    }

    public void btnjrOKPclick(View v) {

    }

    public void btnjrENTERClick(View v) {

    }

    private void loadlistcommon3() {
        try {
            /*String jrpudip = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrpudip");
            String jrTotalBags = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrTotalBags");*/
            String jrAddress = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrAddress");
            String jrTotalJobs = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrTotalJobs");

            if (p1AL != null && p1AL.size() > 0) {
                Jobs_Retrieval_PostItemAdapter_p1 adapter = new Jobs_Retrieval_PostItemAdapter_p1(myApp.getAppContext(), p1AL);
                listView.setAdapter(adapter);
            }
            lbljrAddress.setText(jrAddress);
            lbljrTotaljobs.setText(jrTotalJobs.isEmpty() ? "0" : jrTotalJobs);
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity loadlistcommon3. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private class loadlistViaNFC3 extends AsyncTask<Void, Void, List<dbObjJobs>> {
        final String tagID;
        final String jrpudip;

        loadlistViaNFC3(String atagID) {
            this.tagID = atagID;
            this.jrpudip = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrpudip");
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected List<dbObjJobs> doInBackground(Void... params) {
            List<dbObjJobs> dbObjJobsAL = null;
            try {
                NFCTag nfcTag = DAO_Jobs.getSearchForLocationNFCTagsRecordViaNFC(tagID);
                if (nfcTag != null && !jrpudip.isEmpty()) {
                    //dbObjJobsAL = DAO_Jobs.getAllJobsPerLocationNFCTagPerCategory(nfcTag.CAIDX1, nfcTag.CAIDX2, jrpudip.equals("0") ? 0 : 2);
                    dbObjJobsAL = DAO_Jobs.getAllJobsPerLocationNFCTagPerCategoryWithOutBagTags(nfcTag.CAIDX1, nfcTag.CAIDX2, jrpudip.equals("0") ? 0 : 2);
                }
            } catch (Exception e) {
                dbObjJobsAL = null;
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity loadlistViaNFC2 doInBackground. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            } finally {
                return dbObjJobsAL;
            }
        }

        @Override
        protected void onPostExecute(final List<dbObjJobs> dbObjJobsAL) {
            objJobInfo ji;
            String JobInfoJsonStr = "";
            Gson gson = new Gson();
            Jobs_Retrieval_Tags_PostData_p1 postdata;
            String AddressWithoutPostal = "";
            String itemAddress = "";
            long currDatelong = new Date().getTime();
            String currtimestampstr = "/Date(" + currDatelong + "+0800)/";
            long newcurrtimestamp = objGlobalMethods.DateToSecondsLong(currtimestampstr);
            Date itemDate = null;
            Date itemDate2 = null;
            Integer noOfBags = 0;
            Integer noOfJobs = 0;
            long firstTimestamp = 0;
            String ContentTypeDesc;
            String SecuritySealNumber;
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM h:mma");
            try {
                p1AL = new ArrayList<>();
                progressDialog.dismiss();
                if (dbObjJobsAL != null && dbObjJobsAL.size() > 0) {
                    for (int i = 0; i <= dbObjJobsAL.size() - 1; i++) {
                        dbObjJobs obj = dbObjJobsAL.get(i);
                        ji = null;
                        JobInfoJsonStr = "";
                        JobInfoJsonStr = obj.JobInfoJsonStr;
                        if (JobInfoJsonStr != "") {
                            itemAddress = "NA";
                            ji = gson.fromJson(JobInfoJsonStr, objJobInfo.class);
                            itemDate = objGlobalMethods.JsonDateToDate(ji.ReadyDateTime);
                            itemDate2 = objGlobalMethods.JsonDateToDate(ji.ToDateTime);

                            //show only the earliest job
                            if (!jrpudip.isEmpty() && jrpudip.equals("0")) {
                                if (i == 0) {
                                    firstTimestamp = Long.parseLong(obj.ReadyDateTime);
                                } else if (i > 0 && firstTimestamp > 0 && Long.parseLong(obj.ReadyDateTime) > firstTimestamp) {
                                    break;
                                }
                            }

                            noOfBags += ji.BarcodeTags == null || ji.BarcodeTags.length == 0 ? 0 : ji.BarcodeTags.length;
                            noOfJobs += 1;

                            if (!jrpudip.isEmpty() && jrpudip.equals("1")) {
                                //okp
                                itemAddress = ji.PURedirectedLocation != null ? objGlobalMethods.returnFullAddress(ji.PURedirectedLocation) : objGlobalMethods.returnFullAddress(ji.PULocation);
                            } else {
                                //pu
                                itemAddress = ji.DLRedirectedLocation != null ? objGlobalMethods.returnFullAddress(ji.DLRedirectedLocation) : objGlobalMethods.returnFullAddress(ji.DLLocation);
                            }

                            ContentTypeDesc = ji.DeliveryContents == null ? "Document" : ji.DeliveryContents;
                            SecuritySealNumber = JobsHandler.getSingleSecuritySealNumber(ji);

                            postdata = new Jobs_Retrieval_Tags_PostData_p1(ji.JobsIDX, ji.ConsignmentNote != null && !ji.ConsignmentNote.isEmpty() ? ji.ConsignmentNote : ji.JobNo,
                                    !jrpudip.isEmpty() && jrpudip.equals("0") ? myApp.getAppContext().getString(R.string.alnpufm) + ": " + formatter.format(itemDate) :
                                            myApp.getAppContext().getString(R.string.alndelby1) + ": " + formatter.format(itemDate2)
                                    , "", itemAddress, ContentTypeDesc, SecuritySealNumber);
                            p1AL.add(postdata);

                            AddressWithoutPostal = !jrpudip.isEmpty() && jrpudip.equals("1") ? myApp.getAppContext().getString(R.string.lblDELTO2) + "\n" :
                                    myApp.getAppContext().getString(R.string.lblPUFM2) + "\n";
                            AddressWithoutPostal += obj.AddressWithoutPostal;
                            lblpufmdelto.setText(!jrpudip.isEmpty() && jrpudip.equals("1") ? "" : myApp.getAppContext().getString(R.string.lblDELTO2));
                        }
                    }

                    if (p1AL == null || p1AL.size() == 0) {
                        //objGlobalMethods.setGCMPrefs(GcmPreferences, "jrpudip", "0");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "loadlistCount", "0");
                        resetList(true);
                        showDialog(myApp.getAppContext().getString(R.string.alert_nojobfound));
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "jrAddress", AddressWithoutPostal != null && !AddressWithoutPostal.isEmpty() ? AddressWithoutPostal : "-");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "jrTotalJobs", String.valueOf(noOfJobs));
                        loadlistcommon3();
                    }
                } else {
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, "jrpudip", "0");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "loadlistCount", "0");
                    resetList(true);
                    showDialog(myApp.getAppContext().getString(R.string.alert_nojobfound));
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity loadlistViaNFC3 onPostExecute. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                resetList(true);
                showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
            }
        }
    }

    private class loadlistViaAddressIDX3 extends AsyncTask<Void, Void, List<dbObjJobs>> {
        final Long addressIDX;
        final String jrpudip;

        loadlistViaAddressIDX3(Long aaddressIDX) {
            this.addressIDX = aaddressIDX;
            this.jrpudip = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrpudip");
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected List<dbObjJobs> doInBackground(Void... params) {
            List<dbObjJobs> dbObjJobsAL = null;
            try {
                if (!jrpudip.isEmpty()) {
                    //dbObjJobsAL = DAO_Jobs.getAllJobsPerLocationIDXTagPerCategory(addressIDX, jrpudip.equals("0") ? 0 : 2);
                    dbObjJobsAL = DAO_Jobs.getAllJobsPerLocationIDXTagPerCategoryWithOutBagTags(addressIDX, jrpudip.equals("0") ? 0 : 2);
                }
            } catch (Exception e) {
                dbObjJobsAL = null;
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity loadlistViaAddressIDX3 doInBackground. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            } finally {
                return dbObjJobsAL;
            }
        }

        @Override
        protected void onPostExecute(final List<dbObjJobs> dbObjJobsAL) {
            objJobInfo ji;
            String JobInfoJsonStr = "";
            Gson gson = new Gson();
            Jobs_Retrieval_Tags_PostData_p1 postdata;
            String AddressWithoutPostal = "";
            String itemAddress = "";
            long currDatelong = new Date().getTime();
            String currtimestampstr = "/Date(" + currDatelong + "+0800)/";
            long newcurrtimestamp = objGlobalMethods.DateToSecondsLong(currtimestampstr);
            Date itemDate = null;
            Date itemDate2 = null;
            Integer noOfBags = 0;
            Integer noOfJobs = 0;
            long firstTimestamp = 0;
            String ContentTypeDesc = "";
            String SecuritySealNumber = "";
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM h:mma");
            try {
                p1AL = new ArrayList<>();
                progressDialog.dismiss();
                if (dbObjJobsAL != null && dbObjJobsAL.size() > 0) {
                    for (int i = 0; i <= dbObjJobsAL.size() - 1; i++) {
                        dbObjJobs obj = dbObjJobsAL.get(i);
                        ji = null;
                        JobInfoJsonStr = "";
                        JobInfoJsonStr = obj.JobInfoJsonStr;
                        if (JobInfoJsonStr != "") {
                            itemAddress = "NA";
                            ji = gson.fromJson(JobInfoJsonStr, objJobInfo.class);
                            itemDate = objGlobalMethods.JsonDateToDate(ji.ReadyDateTime);
                            itemDate2 = objGlobalMethods.JsonDateToDate(ji.ToDateTime);

                            /*long expectedtime = Long.parseLong(!jrpudip.isEmpty() && jrpudip.equals("1") ? obj.ToDateTime : obj.ReadyDateTime);
                            if (expectedtime > newcurrtimestamp) {
                                break;
                            }*/

                            //show only the earliest job
                            if (!jrpudip.isEmpty() && jrpudip.equals("0")) {
                                if (i == 0) {
                                    firstTimestamp = Long.parseLong(obj.ReadyDateTime);
                                } else if (i > 0 && firstTimestamp > 0 && Long.parseLong(obj.ReadyDateTime) > firstTimestamp) {
                                    break;
                                }
                            }

                            noOfBags += ji.BarcodeTags == null || ji.BarcodeTags.length == 0 ? 0 : ji.BarcodeTags.length;
                            noOfJobs += 1;

                            if (!jrpudip.isEmpty() && jrpudip.equals("1")) {
                                //okp
                                itemAddress = ji.PURedirectedLocation != null ? objGlobalMethods.returnFullAddress(ji.PURedirectedLocation) : objGlobalMethods.returnFullAddress(ji.PULocation);
                            } else {
                                //pu
                                itemAddress = ji.DLRedirectedLocation != null ? objGlobalMethods.returnFullAddress(ji.DLRedirectedLocation) : objGlobalMethods.returnFullAddress(ji.DLLocation);
                            }

                            ContentTypeDesc = ji.DeliveryContents == null ? "Document" : ji.DeliveryContents;
                            SecuritySealNumber = JobsHandler.getSingleSecuritySealNumber(ji);

                            postdata = new Jobs_Retrieval_Tags_PostData_p1(ji.JobsIDX, ji.ConsignmentNote != null && !ji.ConsignmentNote.isEmpty() ? ji.ConsignmentNote : ji.JobNo,
                                    !jrpudip.isEmpty() && jrpudip.equals("0") ? myApp.getAppContext().getString(R.string.alnpufm) + ": " + formatter.format(itemDate) :
                                            myApp.getAppContext().getString(R.string.alndelby1) + ": " + formatter.format(itemDate2)
                                    , "", itemAddress, ContentTypeDesc, SecuritySealNumber);
                            p1AL.add(postdata);

                            AddressWithoutPostal = !jrpudip.isEmpty() && jrpudip.equals("1") ? myApp.getAppContext().getString(R.string.lblDELTO2) + "\n" :
                                    myApp.getAppContext().getString(R.string.lblPUFM2) + "\n";
                            AddressWithoutPostal += obj.AddressWithoutPostal;
                            lblpufmdelto.setText(!jrpudip.isEmpty() && jrpudip.equals("1") ? "" : myApp.getAppContext().getString(R.string.lblDELTO2));
                        }
                    }

                    if (p1AL == null || p1AL.size() == 0) {
                        //objGlobalMethods.setGCMPrefs(GcmPreferences, "jrpudip", "0");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "loadlistCount", "0");
                        resetList(true);
                        showDialog(myApp.getAppContext().getString(R.string.alert_nojobfound));
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "jrAddress", AddressWithoutPostal != null && !AddressWithoutPostal.isEmpty() ? AddressWithoutPostal : "-");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "jrTotalJobs", String.valueOf(noOfJobs));
                        loadlistcommon3();
                    }
                } else {
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, "jrpudip", "0");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "loadlistCount", "0");
                    resetList(true);
                    showDialog(myApp.getAppContext().getString(R.string.alert_nojobfound));
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity loadlistViaNFC3 onPostExecute. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                resetList(true);
                showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
            }
        }
    }

    private void resetList(Boolean clearall) {
        try {
            p1AL = new ArrayList<>();
            Jobs_Retrieval_PostItemAdapter_p1 adapter = new Jobs_Retrieval_PostItemAdapter_p1(myApp.getAppContext(), p1AL);
            listView.setAdapter(adapter);
            /*if (clearall) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "jrAddress", "-");
            }*/

            objGlobalMethods.setGCMPrefs(GcmPreferences, "jrTotalBagsEntered", "0");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "jrTotalBags", "0");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "jrTotalJobs", "0");

            lbljrAddress.setText("-");
            lbljrTotaljobs.setText("0");
            lbljrTotalbagsentered.setText("0");
            lbljrTotalbags.setText("0");
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity resetList. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
        }
    }

    private void LocationBarcodeSearch(String addressIDXstr) {
        try {
            Long addressIDX = null;
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "NFCLocationtagPrefVal", "");
            if (!addressIDXstr.isEmpty()) {
                try {
                    addressIDX = Long.parseLong(addressIDXstr);
                } catch (Exception e) {
                    addressIDX = null;
                }

                if (addressIDX != null && addressIDX > 0) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "jraddressIDX", String.valueOf(addressIDX));
                    loadlistViaAddressIDX3 t = new loadlistViaAddressIDX3(addressIDX);
                    t.execute();
                } else {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "jraddressIDX", "");
                    resetList(true);
                    showDialog(myApp.getAppContext().getString(R.string.alert_locationcodenotfound));
                }
            } else {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "jraddressIDX", "");
                resetList(true);
                showDialog(myApp.getAppContext().getString(R.string.alert_scanaddressidx));
            }

            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity LocationBarcodeSearch. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

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

                String type = intent.getType();
                if (MIME_TEXT_PLAIN.equals(type)) {

                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    new NdefReaderTask().execute(tag);

                } else {
                    //Log.d(TAG, "Wrong mime type: " + type);
                    objGlobalMethods.writeToFile("Wrong mime type", "", "", "", "");
                }
            } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

                // In case we would still use the Tech Discovered Intent
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String[] techList = tag.getTechList();
                String searchedTech = Ndef.class.getName();

                for (String tech : techList) {
                    if (searchedTech.equals(tech)) {
                        new NdefReaderTask().execute(tag);
                        break;
                    }
                }
            } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                String tagID = objGlobalMethods.ByteArrayToHexString(id);
                //tagID = tagID != null && tagID.length() > 0 ? tagID.substring(0,tagID.length()-1) : "ID Not Found!";
                tagID = tagID != null && tagID.length() > 0 ? tagID : "";
                objGlobalMethods.setGCMPrefs(GcmPreferences, "NFCtagScannedVal", tagID != null && !tagID.isEmpty() ? tagID.toUpperCase() : "");
                isFromLocationNFCscan = true;
                isFromLocationBCscan = false;
            }

        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity handleIntent. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    //ACTION_TAG_DISCOVERED
    private String ByteArrayToHexString(byte[] inarray) { // converts byte arrays to string
        int i, j, in;
        String[] hex = {
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
        };
        String out = "";

        /*for (j = 0; j < inarray.length; ++j) {
            in = inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }*/

        for (j = 0; j < inarray.length; ++j) {
            in = inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
    //ACTION_TAG_DISCOVERED

    //ACTION_NDEF_DISCOVERED
    // Decoding a payload containing text
    private String getTextData(byte[] payload) {
        if (payload == null)
            return null;
        try {
            int langageCodeLength = payload[0] & 0077;
            return new String(payload, langageCodeLength + 1, payload.length - langageCodeLength - 1, ((payload[0] & 0200) == 0) ? Charset.forName("UTF-8") : Charset.forName("UTF-16"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        String s = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity NdefReaderTask doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, ((payload[0] & 128) == 0) ? Charset.forName("UTF-8") : Charset.forName("UTF-16"));
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                showDialog(result);
                //mTextView.setText("Read content: " + result);
            }
        }
    }
    //ACTION_NDEF_DISCOVERED

    //temporary paring code
    private void addtofile(String barcode, String tagid) {
        String data = barcode + ", " + tagid;
        objGlobalMethods.writeToSepcificFile(data);
        focusTextbox();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "NFCLocationtagPrefVal", "");
    }

    private File attachment;

    private class checkForAttachmentToEmailOut extends AsyncTask<Void, Void, File> {
        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
        }

        @Override
        protected File doInBackground(Void... params) {

            try {
                File externalStorageDir = Environment.getExternalStorageDirectory();
                attachment = new File(externalStorageDir, "LocationNfcBarcodePairing.txt");
            } catch (Exception e) {
                attachment = null;
            }

            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final File success) {
            String attachmentfilename = "";
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            try {
                if (attachment.exists()) {
                    attachmentfilename = attachment.getAbsolutePath().toString();
                    if (!attachmentfilename.equals("")) {
                        Mail m = new Mail("luke@xdel.com", "XDel2015");
                        objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, attachmentfilename, "", "Barcode NFC pairing file", attachment);
                        sendmail.execute();
                    }
                }
            } catch (Exception e) {

            }
        }
    }
    //temporary paring code

    /*private void addremoveBags(String bagTag) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String jrpudip = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrpudip");
        Boolean exist = false;
        objCabinetItems items;

        try {
            if (ItemsScanned == null) {
                ItemsScanned = new ArrayList<>();
            }

            //check if bag is already scanned
            for (int x = 0; x <= ItemsScanned.size() - 1; x++) {
                if (ItemsScanned.get(x).BagID.equals(bagTag)) {
                    exist = true;
                    break;
                }
            }

            if (exist) {
                loadlistcommon();
                showDialog(myApp.getAppContext().getString(R.string.alert_duplicatescan));
            } else {
                //check if bag belong to any of the jobs
                items = null;
                for (int x = 0; x <= JRPostDataAL.size() - 1; x++) {
                    if (JRPostDataAL.get(x).tagID.equals(bagTag)) {
                        exist = true;
                        items = new objCabinetItems(0, JRPostDataAL.get(x).JobsIDX, JRPostDataAL.get(x).PULID, JRPostDataAL.get(x).OKPLID, bagTag);
                        break;
                    }
                }

                if (ItemsScanned == null) {
                    ItemsScanned = new ArrayList<>();
                }

                if (!exist) {
                    //given that every location tag will always has at least 1 job at any point of the time, we use any pulid here
                    items = new objCabinetItems(0, 0, JRPostDataAL.get(0).PULID, 0, bagTag);
                }

                ItemsScanned.add(items);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "jrTotalBagsEntered", ItemsScanned == null || ItemsScanned.size() == 0 ? "" : String.valueOf(ItemsScanned.size()));
                loadlistcommon();
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
        }
    }*/

    /*private void loadlistcommon() {
        try {
            String jrpudip = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrpudip");
            String jrAddress = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrAddress");
            String jrTotalBags = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrTotalBags");
            String jrTotalJobs = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrTotalJobs");

            if (ItemsScanned != null && ItemsScanned.size() > 0) {
                Jobs_Retrieval_PostItemAdapter adapter = new Jobs_Retrieval_PostItemAdapter(myApp.getAppContext(), ItemsScanned);
                listView.setAdapter(adapter);
            }
            lbljrAddress.setText(jrAddress);
            lbljrTotaljobs.setText(jrTotalJobs.isEmpty() ? "0" : jrTotalJobs);

            if (jrpudip.equals("0") || jrpudip == null || jrpudip.equals("")) {
                lbljrTotalbags.setVisibility(View.VISIBLE);
                lbljrTotalbagsdivider.setVisibility(View.VISIBLE);
                lbljrTotalbagsentered.setText(String.valueOf(ItemsScanned == null || ItemsScanned.size() == 0 ? 0 : ItemsScanned.size()));
                lbljrTotalbags.setText(jrTotalBags.isEmpty() ? "0" : jrTotalBags);
                ctrlSettings.enableDisableButtonWithStyles(
                        ItemsScanned == null || ItemsScanned.size() == 0 ? false : true, btnJRpu);
            } else if (jrpudip.equals("1")) {
                lbljrTotalbags.setVisibility(View.GONE);
                lbljrTotalbagsdivider.setVisibility(View.GONE);
                ctrlSettings.enableDisableButtonWithStyles(
                        ItemsScanned == null || ItemsScanned.size() != Integer.parseInt(jrTotalBags.isEmpty() ? "0" : jrTotalBags) ? false : true, btnJRokp);
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity loadlistcommon. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }*/

    /*private class loadlistViaAddressIDX extends AsyncTask<Void, Void, List<dbObjJobs>> {
        final Long addressIDX;
        final String jrpudip;

        loadlistViaAddressIDX(Long aaddressIDX) {
            this.addressIDX = aaddressIDX;
            this.jrpudip = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrpudip");
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected List<dbObjJobs> doInBackground(Void... params) {
            List<dbObjJobs> dbObjJobsAL = null;
            try {
                //String jrpudip = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrpudip");
                if (!jrpudip.isEmpty()) {
                    dbObjJobsAL = DAO_Jobs.getAllJobsPerLocationIDXTagPerCategory(addressIDX, jrpudip.equals("0") ? 0 : 2);
                }
            } catch (Exception e) {
                dbObjJobsAL = null;
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity loadlistViaAddressIDX doInBackground. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            } finally {
                return dbObjJobsAL;
            }
        }

        @Override
        protected void onPostExecute(final List<dbObjJobs> dbObjJobsAL) {
            objJobInfo ji;
            String JobInfoJsonStr = "";
            Gson gson = new Gson();
            Jobs_Retrieval_Tags_PostData postdata;
            String AddressWithoutPostal = "";
            Integer noOfBags = 0;
            Integer noOfJobs = 0;
            long firstTimestamp = 0;
            try {
                if (JRPostDataAL == null) {
                    JRPostDataAL = new ArrayList<>();
                }
                progressDialog.dismiss();
                if (dbObjJobsAL != null && dbObjJobsAL.size() > 0) {
                    for (int i = 0; i <= dbObjJobsAL.size() - 1; i++) {
                        dbObjJobs obj = dbObjJobsAL.get(i);
                        ji = null;
                        JobInfoJsonStr = "";
                        JobInfoJsonStr = obj.JobInfoJsonStr;
                        if (JobInfoJsonStr != "") {
                            ji = gson.fromJson(JobInfoJsonStr, objJobInfo.class);
                            noOfBags += ji.BarcodeTags == null || ji.BarcodeTags.length == 0 ? 0 : ji.BarcodeTags.length;
                            noOfJobs += 1;
                            if (!jrpudip.isEmpty() && jrpudip.equals("2")) {
                                for (int d = 0; d <= ji.BarcodeTags.length - 1; d++) {
                                    //postdata = new Jobs_Retrieval_Tags_PostData(ji.BarcodeTags[d].toUpperCase(), ji.JobsIDX);
                                    //JRPostDataAL.add(postdata);
                                }
                            }

                            AddressWithoutPostal = obj.AddressWithoutPostal;
                            //show only the earliest job
                            if (i == 0) {
                                firstTimestamp = Long.parseLong(obj.ReadyDateTime);
                            } else if (i > 0 && firstTimestamp > 0 && Long.parseLong(obj.ReadyDateTime) > firstTimestamp) {
                                break;
                            }
                        }
                    }

                    if (JRPostDataAL != null && JRPostDataAL.size() > 0) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "jrAddress", AddressWithoutPostal != null && !AddressWithoutPostal.isEmpty() ? AddressWithoutPostal : "-");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "jrTotalBags", String.valueOf(noOfBags));
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "jrTotalJobs", String.valueOf(noOfJobs));
                        loadlistcommon3();
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "jrAddress", AddressWithoutPostal != null && !AddressWithoutPostal.isEmpty() ? AddressWithoutPostal : "-");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "jrTotalBags", String.valueOf(noOfBags));
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "jrTotalJobs", String.valueOf(noOfJobs));
                        loadlistcommon3();
                    }
                } else {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "jrpudip", "0");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "loadlistCount", "0");

                    resetList(true);
                    showDialog(myApp.getAppContext().getString(R.string.alert_nojobfound));
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_Activity loadlistViaAddressIDX onPostExecute. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                resetList(true);
                showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
            }
        }
    }*/

}

