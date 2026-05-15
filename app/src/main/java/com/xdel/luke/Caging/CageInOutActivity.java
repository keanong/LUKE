package com.xdel.luke.Caging;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
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
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.FCM.FCMservice;
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

import java.util.ArrayList;
import java.util.List;

public class CageInOutActivity extends AppCompatActivity {

    ConnectionDetector cd;
    private ProgressDialog pd, pdCheck;
    private String langType = "en";
    private static ListView listView;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    TextView lblUserIDX, lblCageInOutSubmitIsHomesUser, lblCageBarcodeVal, lblItemsScheduledDate, lblCageItemTotalSubmitCOUNTtext, lblCageItemTotalAllocatedCOUNT, lblCageItemTotalAllocatedScannedCOUNT, lblCageItemTotalSubmitCOUNT;
    private static EditText txtCageBarcodeMANUALENTRY;
    private Button btnCageBarcodeSubmitENTER, btnCageAccept, btnCageReject, btnCageSubmitCANCEL, btnCageSubmitCLEARALL, btnCageSubmitSUBMIT;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    static String cameraScannedCode;
    private MediaPlayer player;
    public static ArrayList<objCageInOutItem> fullGClistItems = null;
    public ArrayList<objCageInOutItem> AllocatedItems = null;
    public static ArrayList<objCageInOutItem> ScannedAItems = null;
    public static ArrayList<objCageInOutItem> Excluded = null;
    CageInOut_PostItemAdapter adapter = null;
    ScreenShotContentObserver screenShotContentObserver;
    private Uri errToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error);
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);

    private void stopPlayingTone() {
        try {
            if (player != null) {
                player.reset();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playTone() {
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, errToneURI);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at CageInOutActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at CageInOutActivity playSuccessVerifiedTone. Exception: " + e.getMessage(), "", "", "", "");
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

        setContentView(R.layout.activity_cage_in_out);

        lblUserIDX = findViewById(R.id.lblUserIDX);
        lblCageInOutSubmitIsHomesUser = (TextView) findViewById(R.id.lblCageInOutSubmitIsHomesUser);
        txtCageBarcodeMANUALENTRY = (EditText) findViewById(R.id.txtCageBarcodeMANUALENTRY);
        lblCageBarcodeVal = (TextView) findViewById(R.id.lblCageBarcodeVal);
        lblCageItemTotalSubmitCOUNTtext = (TextView) findViewById(R.id.lblCageItemTotalSubmitCOUNTtext);
        lblCageItemTotalAllocatedCOUNT = (TextView) findViewById(R.id.lblCageItemTotalAllocatedCOUNT);
        lblCageItemTotalAllocatedScannedCOUNT = (TextView) findViewById(R.id.lblCageItemTotalAllocatedScannedCOUNT);
        lblCageItemTotalSubmitCOUNT = (TextView) findViewById(R.id.lblCageItemTotalSubmitCOUNT);
        lblItemsScheduledDate = (TextView) findViewById(R.id.lblItemsScheduledDate);
        listView = (ListView) findViewById(R.id.listView);
        btnCageBarcodeSubmitENTER = (Button) findViewById(R.id.btnCageBarcodeSubmitENTER);

        btnCageAccept = (Button) findViewById(R.id.btnCageAccept);
        btnCageReject = (Button) findViewById(R.id.btnCageReject);
        btnCageSubmitCANCEL = (Button) findViewById(R.id.btnCageSubmitCANCEL);
        btnCageSubmitCLEARALL = (Button) findViewById(R.id.btnCageSubmitCLEARALL);
        btnCageSubmitSUBMIT = (Button) findViewById(R.id.btnCageSubmitSUBMIT);

        pdCheck = new ProgressDialog(this);
        pdCheck.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdCheck.setCanceledOnTouchOutside(false);
        pdCheck.setMessage(myApp.getAppContext().getString(R.string.verifyitem));

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            Context ctx = myApp.getAppContext();

            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
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
                lblUserIDX.setText(useridx);
                Boolean IsHOMESuser = objGlobalMethods.IsHomesUserString(ishomesuser, GcmPreferences);
                setTitle(IsHOMESuser ? ctx.getString(R.string.cagein) : ctx.getString(R.string.cageout));

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
                    objGlobalMethods.writeToFile("CageInOutActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                DashboardSubActivity.frompage = null;

                //cd = new ConnectionDetector(ctx);
                cd = new ConnectionDetector(null);

                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    Toast.makeText(ctx, ctx.getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }

                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");

                initPage(IsHOMESuser, GcmPreferences);

            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageInOutActivity onResume. Exception: " + e.getMessage(), "", "", "", "");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GcmPreferences = null;
        UpdateJobInfoPreferences = null;
        cameraScannedCode = null;
        ScannedAItems = null;
        fullGClistItems = null;
        Excluded = null;
        listView = null;
        txtCageBarcodeMANUALENTRY = null;

        try {
            stopPlayingTone();
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

        DAO_Jobs.nullifystaticitems();
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at CageInOutActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at CageInOutActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at CageInOutActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at CageInOutActivity showChangePermissionDialog. ActivityNotFoundException: "
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
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "cageinout");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        parentIntent = new Intent(CageInOutActivity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cage_scan, menu);

        if (Boolean.parseBoolean(ishomesuser)) {
            menu.findItem(R.id.refresh).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.qrscan) {
            launchQRScan();
        } else if (item.getItemId() == R.id.refresh) {
            reSync t = new reSync();
            t.execute();
        }

        /*Intent parentIntent;
        switch (item.getItemId()) {
            case R.id.qrscan:
                launchQRScan();
                break;
            case R.id.refresh:
                reSync t = new reSync();
                t.execute();
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        try {
            stopPlayingTone();
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        initBack();
    }

    private void initBack() {
        if (ScannedAItems != null && ScannedAItems.size() > 0 && adapter != null) {
            playTone();
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
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.0f);
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.0f);
        } else {
            back();
        }
    }

    private void back() {
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        Context ctx = myApp.getAppContext();
        fullGClistItems = null;
        ScannedAItems = null;
        Excluded = null;
        listView = null;
        txtCageBarcodeMANUALENTRY = null;
        String[] prefName = new String[]{"gc_cage_accept_reject", "CageInOutScannedAItemsPrefStr", "CageInOutScannedExcludedPrefStr"};
        String[] prefvalue = new String[]{"0", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
        Intent parentIntent = new Intent(this, CageScanActivity.class);
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    private void initPage(boolean IsHOMESuser, SharedPreferences gcmp) {
        String gc_cage_accept_reject = "0";
        String CageLocation = "";
        String ScanCage_ddMMyyyy, ScanCage_AMPM, ScanCage_AMPMVal, ddMMyyyyAMPM;
        Context ctx = myApp.getAppContext();
        ArrayList<String> items = null;

        try {
            cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
            CageLocation = objGlobalMethods.getGCMPrefs(gcmp, "CageLocation");
            lblCageBarcodeVal.setText(!CageLocation.isEmpty() && !CageLocation.equals("") && CageLocation != "" ? CageLocation.toUpperCase() : ctx.getString(R.string.pls_scan_cage));
            lblCageInOutSubmitIsHomesUser.setText(IsHOMESuser ? "true" : "false");
            gc_cage_accept_reject = objGlobalMethods.getGCMPrefs(gcmp, "gc_cage_accept_reject");
            int acceptreject = (gc_cage_accept_reject.equals("0") || gc_cage_accept_reject == null || gc_cage_accept_reject.equals("")) ? 0 : 1;
            ScannedAItems = getScannedAItems(gcmp);
            Excluded = getScannedExcluded(gcmp);

            if (IsHOMESuser) {
                lblCageItemTotalSubmitCOUNTtext.setVisibility(View.VISIBLE);
                lblItemsScheduledDate.setVisibility(View.VISIBLE);
                lblCageItemTotalAllocatedCOUNT.setVisibility(View.GONE);
                lblCageItemTotalAllocatedScannedCOUNT.setVisibility(View.GONE);
                ScanCage_ddMMyyyy = objGlobalMethods.getGCMPrefs(GcmPreferences, "ScanCage_ddMMyyyy");
                ScanCage_AMPM = objGlobalMethods.getGCMPrefs(GcmPreferences, "ScanCage_AMPM");
                ScanCage_AMPMVal = !ScanCage_AMPM.isEmpty() && !ScanCage_AMPM.equals("") && !ScanCage_AMPM.equals("0") && ScanCage_AMPM != "" && ScanCage_AMPM != "0" ?
                        ctx.getString(R.string.pm) : ctx.getString(R.string.am);
                ddMMyyyyAMPM = ScanCage_ddMMyyyy + " " + ScanCage_AMPMVal;
                lblItemsScheduledDate.setText(ddMMyyyyAMPM);
                btnCageAccept.setVisibility(View.GONE);
                btnCageReject.setVisibility(View.GONE);
                btnCageSubmitSUBMIT.setText(ctx.getString(R.string.cagein));
                lblCageItemTotalSubmitCOUNT.setText(ScannedAItems != null ? String.valueOf(ScannedAItems.size()) : "0");
                ctrlSettings.enableDisableCommonButtonWithStyles(ScannedAItems != null && ScannedAItems.size() > 0 ? true : false, btnCageSubmitCLEARALL);
                ctrlSettings.enableDisableSubmitButtonWithStyles(ScannedAItems != null && ScannedAItems.size() > 0 ? true : false, btnCageSubmitSUBMIT);

                loadlistHQ();
                listItemHQOnClick();
            } else {
                lblCageItemTotalSubmitCOUNTtext.setVisibility(View.GONE);
                lblItemsScheduledDate.setVisibility(View.GONE);
                lblCageItemTotalAllocatedCOUNT.setVisibility(View.VISIBLE);
                lblCageItemTotalAllocatedScannedCOUNT.setVisibility(View.VISIBLE);
                lblCageItemTotalSubmitCOUNT.setVisibility(View.GONE);
                btnCageAccept.setVisibility(View.VISIBLE);
                btnCageReject.setVisibility(View.VISIBLE);
                btnCageSubmitSUBMIT.setText(ctx.getString(R.string.cageout));
                togglePUDIP(gc_cage_accept_reject, false);
                String aitemscount = ScannedAItems != null ? String.valueOf(ScannedAItems.size()) : "0";
                String excludedcount = Excluded != null ? String.valueOf(Excluded.size()) : "0";

                int[] TotalAllocationItemCountAndScanned = getTotalAllocationItemCountAndScanned(gcmp);
                int totalAllocated = TotalAllocationItemCountAndScanned[0];
                int totalAllocatedScanned = TotalAllocationItemCountAndScanned[1];
                int totalScanned = getTotalScannedAItemsNExcludedScannedCount();
                lblCageItemTotalAllocatedCOUNT.setText(ctx.getString(R.string.totalallocated) + " " + String.valueOf(totalAllocated));
                lblCageItemTotalAllocatedScannedCOUNT.setText(ctx.getString(R.string.totalallocatedscanned) + " " + String.valueOf(totalAllocatedScanned));
                lblCageItemTotalSubmitCOUNT.setText(ctx.getString(R.string.buttontext_accept) + ": " + aitemscount + " / " + ctx.getString(R.string.buttontext_reject) + ": " + excludedcount + " / " + ctx.getString(R.string.totalapplying) + " " + String.valueOf(totalScanned));
                ctrlSettings.enableDisableCommonButtonWithStyles(totalScanned > 0 ? true : false, btnCageSubmitCLEARALL);
                ctrlSettings.enableDisableSubmitButtonWithStyles(totalAllocatedScanned < totalAllocated ? false : true, btnCageSubmitSUBMIT);

                loadlistGC(0);
                //listItemGCOnClick();
            }

            txtCageBarcodeMANUALENTRY.setHint(
                    (!CageLocation.isEmpty() && !CageLocation.equals("") && CageLocation != "")
                            ? ctx.getString(R.string.lblccCN)
                            : ctx.getString(R.string.cagebarcode));

            if (!cameraScannedCode.isEmpty() && !cameraScannedCode.equals("") && cameraScannedCode != "") {
                objGlobalMethods.setGCMPrefs(gcmp, "cameraScannedCode", "");
                inputManualEntry2(cameraScannedCode.toUpperCase());
            }

            txtCageBarcodeMANUALENTRY.requestFocus();

            txtCageBarcodeMANUALENTRY.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                        return false;
                    if (actionId == 0) {
                        //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                        inputManualEntry2(txtCageBarcodeMANUALENTRY.getText().toString().toUpperCase());
                        if (getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                    } else if (actionId == EditorInfo.IME_ACTION_GO) {
                        inputManualEntry2(txtCageBarcodeMANUALENTRY.getText().toString().toUpperCase());
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
            objGlobalMethods.writeToFile("Exception at CageInOutActivity initPage. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void btnCageBarcodeSubmitENTERClick(View view) {
        String v = txtCageBarcodeMANUALENTRY.getText().toString().toUpperCase();
        Context ctx = myApp.getAppContext();
        try {
            if (!v.isEmpty() && !v.equals("") && v != "") {
                inputManualEntry2(v);
            } else {
                playTone();
                showAlert2(ctx.getString(R.string.scan_enter_item_ref));
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity btnCageBarcodeSubmitENTERClick. Exception: " + e.getMessage(), "", "", "", "");
            showAlert2(ctx.getString(R.string.errorverifying));
        } finally {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public void btnCageSubmitCANCELClick(View view) {
        initBack();
    }

    public void btnCageSubmitCLEARALLClick(View view) {
        final SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        String gc_cage_accept_reject = "0";
        final Context ctx = myApp.getAppContext();

        try {
            ScannedAItems = getScannedAItems(gcmp);
            Excluded = getScannedExcluded(gcmp);
            String IsHOMESuserStr = lblCageInOutSubmitIsHomesUser.getText().toString();
            final boolean IsHOMESuser = !IsHOMESuserStr.isEmpty() && !IsHOMESuserStr.equals("") && !IsHOMESuserStr.equals("false") && IsHOMESuserStr != "" && IsHOMESuserStr != "false" ? true : false;
            gc_cage_accept_reject = objGlobalMethods.getGCMPrefs(gcmp, "gc_cage_accept_reject");
            final int acceptreject = (gc_cage_accept_reject.equals("0") || gc_cage_accept_reject == null || gc_cage_accept_reject.equals("")) ? 0 : 1;

            if ((ScannedAItems != null && ScannedAItems.size() > 0) || (Excluded != null && Excluded.size() > 0)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(ctx.getString(R.string.alert_cleardata));
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status
                        try {
                            clear(gcmp, IsHOMESuser, acceptreject);
                        } catch (Exception e) {

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
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.0f);
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.0f);

            }
        } catch (Exception e) {

        }
    }

    private void clear(SharedPreferences gcmp, boolean IsHOMESuser, int acceptreject) {
        Context ctx = myApp.getAppContext();
        try {
            if (ScannedAItems != null) {
                objGlobalMethods.setGCMPrefs(gcmp, "CageInOutScannedAItemsPrefStr", "");
                ScannedAItems.clear();
            }
            if (Excluded != null) {
                objGlobalMethods.setGCMPrefs(gcmp, "CageInOutScannedExcludedPrefStr", "");
                Excluded.clear();
            }

            if (IsHOMESuser) {
                loadlistHQ();
            } else {
                loadlistGC(0);
            }

            enabledisablectrlsAccordingToUserType();
            if (acceptreject == 0) {
                ctrlSettings.enableDisableCommonButtonWithStyles(ScannedAItems != null && ScannedAItems.size() > 0 ? true : false, btnCageSubmitCLEARALL);
            } else {
                ctrlSettings.enableDisableCommonButtonWithStyles(Excluded != null && Excluded.size() > 0 ? true : false, btnCageSubmitCLEARALL);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity clear. Exception: " + e.getMessage(), "", "", "", "");
            showAlert2(ctx.getString(R.string.alert_errorperformoperation));
        }
    }

    private void showCfmSubmissionPOP(final boolean IsHOMESuser, final long CageIDX) {
        final Context ctx = myApp.getAppContext();
        int c = 0;
        String extratext = "";
        try {
            if (ScannedAItems != null && ScannedAItems.size() > 0) {
                for (int i = 0; i <= ScannedAItems.size() - 1; i++) {
                    objCageInOutItem item = ScannedAItems.get(i);
                    if (item.color == 0 || item.color == 1) {
                        c++;
                    }
                }
            }
            extratext = c > 0 ? "\n" + c + " " + ctx.getString(R.string.itemsnotscanned) + "\n" : "";
            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setCancelable(false);
            builder.setTitle(myApp.getAppContext().getString(R.string.cfmsubmit));
            builder.setMessage(!objGlobalMethods.IsEmptyString(extratext) ? extratext : "");
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
                    SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                    try {
                        if (IsHOMESuser) {
                            if (ScannedAItems != null && ScannedAItems.size() > 0) {
                                CageIn t = new CageIn(CageIDX);
                                t.execute();
                            } else if ((ScannedAItems != null && ScannedAItems.size() == 0) || ScannedAItems == null) {
                                playTone();
                                showAlert2(ctx.getString(R.string.unabletocageinnodata));
                            }
                        } else {
                            if ((ScannedAItems != null && ScannedAItems.size() > 0) || (Excluded != null && Excluded.size() > 0)) {
                                int[] TotalAllocationItemCountAndScanned = getTotalAllocationItemCountAndScanned(gcmp);
                                int totalAllocated = TotalAllocationItemCountAndScanned[0];
                                int totalAllocatedScanned = TotalAllocationItemCountAndScanned[1];
                                int totalNotScanned = 0;

                                if (totalAllocatedScanned != totalAllocated) {
                                    totalNotScanned = totalAllocated - totalAllocatedScanned;
                                    showAlert2(ctx.getString(R.string.scanallitems) + " " + String.valueOf(totalNotScanned) + " " + ctx.getString(R.string.itemsnotscanned));
                                } else {
                                    CageOut t = new CageOut(CageIDX);
                                    t.execute();
                                }
                            } else if (ScannedAItems != null && ScannedAItems.size() == 0 && Excluded != null && Excluded.size() == 0) {
                                //prompt
                                playTone();
                                showAlert2(ctx.getString(R.string.unabletocageinnodata));
                            } else if (ScannedAItems == null && Excluded == null) {
                                //prompt
                                playTone();
                                showAlert2(ctx.getString(R.string.unabletocageinnodata));
                            }
                        }
                    } catch (Exception e) {
                        playTone();
                        showAlert2(ctx.getString(R.string.alert_errorperformoperation));
                    }
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.0f);
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.0f);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at CageInOutActivity showCfmSubmissionPOP. Exception: " + e.toString(), "", "", "", "");
        }
    }

    public void btnCageSubmitSUBMITClick(View view) {
        Context ctx = myApp.getAppContext();
        String CageIDXstr = "0";
        long CageIDX = 0;
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        SharedPreferences up = objGlobalMethods.getUpdateJobInfoPreferences();
        try {
            String IsHOMESuserStr = lblCageInOutSubmitIsHomesUser.getText().toString();
            boolean IsHOMESuser = !IsHOMESuserStr.isEmpty() && !IsHOMESuserStr.equals("") && !IsHOMESuserStr.equals("false") && IsHOMESuserStr != "" && IsHOMESuserStr != "false" ? true : false;
            CageIDXstr = objGlobalMethods.getGCMPrefs(gcmp, "CageIDX");
            if (!CageIDXstr.isEmpty() && !CageIDXstr.equals("") && CageIDXstr != "" && !CageIDXstr.equals("0") && CageIDXstr != "0") {
                try {
                    CageIDX = Long.parseLong(CageIDXstr);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at CageInOutActivity btnCageBarcodeSubmitENTERClick converting CageIDX. Exception: " + e.getMessage(), "", "", "", "");
                }
            }

            if (CageIDX > 0) {
                showCfmSubmissionPOP(IsHOMESuser, CageIDX);
            } else {
                showAlert2(ctx.getString(R.string.alert_error));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity btnCageSubmitSUBMITClick. Exception: " + e.getMessage(), "", "", "", "");
            showAlert2(ctx.getString(R.string.alert_error));
        }
    }

    private void togglePUDIP(String mode, Boolean isToggled) {
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        try {
            if (mode.equals("0") || mode == null || mode.equals("")) {
                ctrlSettings.toggleButtonsWithStyles(true, btnCageAccept);
                ctrlSettings.toggleButtonsWithStyles(false, btnCageReject);
                objGlobalMethods.setGCMPrefs(gcmp, "gc_cage_accept_reject", "0");
            } else if (mode.equals("1")) {
                ctrlSettings.toggleButtonsWithStyles(true, btnCageReject);
                ctrlSettings.toggleButtonsWithStyles(false, btnCageAccept);
                objGlobalMethods.setGCMPrefs(gcmp, "gc_cage_accept_reject", "1");
            }

            enabledisablectrlsAccordingToUserType();
            if (mode.equals("0") || mode == null || mode.equals("")) {
                ctrlSettings.enableDisableCommonButtonWithStyles(ScannedAItems != null && ScannedAItems.size() > 0 ? true : false, btnCageSubmitCLEARALL);
            } else if (mode.equals("1")) {
                ctrlSettings.enableDisableCommonButtonWithStyles(Excluded != null && Excluded.size() > 0 ? true : false, btnCageSubmitCLEARALL);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity togglePUDIP. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void loadlistHQ() {
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        Context ctx = myApp.getAppContext();
        ArrayList<objCageInOutItem> items = null;
        try {
            if (ScannedAItems != null && ScannedAItems.size() > 0) {
                items = ScannedAItems;
            } else if (ScannedAItems == null || ScannedAItems.size() == 0) {
                items = getScannedAItems(gcmp);
                if (items == null) {
                    items = new ArrayList<objCageInOutItem>();
                }
            }
            adapter = new CageInOut_PostItemAdapter(this, items);
            listView.setAdapter(adapter);

            //runs after listview is done updating itself
            listView.post(new Runnable() {
                public void run() {
                    //scroll the listview to the last item
                    listView.setSelection(listView.getCount() - 1);
                    //focus certain view or any other calls after listview completed updating

                }
            });
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity loadlistHQ. Exception: " + e.getMessage(), "", "", "", "");
            playTone();
            showAlert2(ctx.getString(R.string.alert_errorperformoperation));
        }
    }

    private ArrayList<objCageInOutItem> getAllItems() {
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        objCageInOutItem item = null;
        String L2_Get_CageResultJsonStr;
        objL2_Get_CageResult CageResult = null;
        objCageItem[] Items_In_Cage = null;
        objCageItem SingleItems_In_Cage = null;
        Gson gson = new Gson();
        objCageInOutItem sai = null;
        objCageInOutItem exi = null;

        try {
            fullGClistItems = null;
            L2_Get_CageResultJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "L2_Get_CageResult");
            if (!L2_Get_CageResultJsonStr.isEmpty() && !L2_Get_CageResultJsonStr.equals("") && L2_Get_CageResultJsonStr != "") {
                CageResult = gson.fromJson(L2_Get_CageResultJsonStr, objL2_Get_CageResult.class);
                if (CageResult != null && CageResult.Items_In_Cage != null && CageResult.Items_In_Cage.length > 0) {
                    Items_In_Cage = CageResult.Items_In_Cage;
                    if (Items_In_Cage != null) {
                        AllocatedItems = new ArrayList<objCageInOutItem>();
                        for (int i = 0; i <= Items_In_Cage.length - 1; i++) {
                            SingleItems_In_Cage = Items_In_Cage[i];
                            item = new objCageInOutItem(SingleItems_In_Cage.Reference, false, SingleItems_In_Cage.Job_Ref, SingleItems_In_Cage.JobsIDX);
                            AllocatedItems.add(item);
                        }
                    }
                }
            }

            if (AllocatedItems != null && AllocatedItems.size() > 0) {
                final ArrayList<objCageInOutItem> templist = AllocatedItems;
                fullGClistItems = templist;
                if (ScannedAItems != null && ScannedAItems.size() > 0) {
                    for (int i = 0; i <= ScannedAItems.size() - 1; i++) {
                        sai = ScannedAItems.get(i);
                        for (int x = 0; x <= fullGClistItems.size() - 1; x++) {
                            if (fullGClistItems.get(x).ref.equals(sai.ref)) {
                                fullGClistItems.get(x).setColor(1);
                                break;
                            }
                            if (fullGClistItems.get(x).JobsIDX == sai.JobsIDX) {
                                fullGClistItems.get(x).setColor(1);
                                break;
                            }
                        }
                    }
                }

                if (Excluded != null && Excluded.size() > 0) {
                    boolean exists = false;
                    int breakidx = -1;
                    for (int i = 0; i <= Excluded.size() - 1; i++) {
                        exists = false;
                        exi = Excluded.get(i);
                        for (int x = 0; x <= fullGClistItems.size() - 1; x++) {
                            if (fullGClistItems.get(x).ref.equals(exi.ref)) {
                                breakidx = x;
                                exists = true;
                                break;
                            }
                        }
                        if (exists) {
                            fullGClistItems.get(breakidx).setColor(2);
                        } else {
                            fullGClistItems.add(exi);
                        }
                    }
                }
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity getAllItems. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return fullGClistItems;
        }
    }

    private void loadlistGC(final int scrolltopos) {
        Context ctx = myApp.getAppContext();
        try {
            fullGClistItems = getAllItems();
            if (fullGClistItems != null && fullGClistItems.size() > 0) {
                if (adapter == null) {
                    adapter = new CageInOut_PostItemAdapter(this, fullGClistItems);
                    listView.setAdapter(adapter);
                } else {
                    adapter.clear();
                    adapter.addAll(fullGClistItems);
                }
                //runs after listview is done updating itself
                listView.post(new Runnable() {
                    public void run() {
                        //scroll the listview to the last item
                        listView.setSelection(scrolltopos < 0 ? listView.getCount() - 1 : scrolltopos);
                        //focus certain view or any other calls after listview completed updating

                    }
                });
            } else {
                showAlert2(ctx.getString(R.string.noitemincage));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOut loadlistGC. Exception: " + e.getMessage(), "", "", "", "");
            playTone();
            showAlert2(ctx.getString(R.string.alert_errorperformoperation));
        }
    }

    private boolean isCageBarcode(String input) {
        boolean asd = false;
        try {
            asd = input.substring(0, 3).equals("WL0");
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity isCageBarcode. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    private ArrayList<String> IsDupScanAL(String input, int mode) {
        ArrayList<String> al = new ArrayList<>();
        al.add(input);
        al.add("false");
        al.add("0");
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        boolean itemFound = false;
        try {
            if (mode == 0) {
                //AItems
                ScannedAItems = getScannedAItems(gcmp);
                if (ScannedAItems != null && ScannedAItems.size() > 0) {
                    for (int i = 0; i <= ScannedAItems.size() - 1; i++) {
                        if (ScannedAItems.get(i).ref.toUpperCase().equals(input)) {
                            al.set(0, ScannedAItems.get(i).job_ref.toUpperCase());
                            al.set(1, "true");
                            //al.set(2, "0");
                            al.set(2, String.valueOf(i));
                            itemFound = true;
                            break;
                        }
                    }
                }
                if (!itemFound) {
                    Excluded = getScannedExcluded(gcmp);
                    if (Excluded != null && Excluded.size() > 0) {
                        for (int i = 0; i <= Excluded.size() - 1; i++) {
                            if (Excluded.get(i).ref.toUpperCase().equals(input)) {
                                al.set(0, Excluded.get(i).job_ref.toUpperCase());
                                al.set(1, "true");
                                al.set(2, "1");
                                itemFound = true;
                                break;
                            }
                        }
                    }
                }
            } else {
                //Excluded
                Excluded = getScannedExcluded(gcmp);
                if (Excluded != null && Excluded.size() > 0) {
                    for (int i = 0; i <= Excluded.size() - 1; i++) {
                        if (Excluded.get(i).ref.toUpperCase().equals(input)) {
                            al.set(0, Excluded.get(i).job_ref.toUpperCase());
                            al.set(1, "true");
                            al.set(2, "1");
                            itemFound = true;
                            break;
                        }
                    }
                }
                if (!itemFound) {
                    ScannedAItems = getScannedAItems(gcmp);
                    if (ScannedAItems != null && ScannedAItems.size() > 0) {
                        if (ScannedAItems != null && ScannedAItems.size() > 0) {
                            for (int i = 0; i <= ScannedAItems.size() - 1; i++) {
                                if (ScannedAItems.get(i).ref.toUpperCase().equals(input)) {
                                    al.set(0, ScannedAItems.get(i).job_ref.toUpperCase());
                                    al.set(1, "true");
                                    al.set(2, "0");
                                    itemFound = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity IsDupScanAL. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return al;
        }
    }

    private void inputManualEntry2(String input) {
        Context ctx = myApp.getAppContext();
        String gc_cage_accept_reject = "0";
        int acceptreject = 0;
        String CageIDXstr = "0";
        String UserIDXstr = lblUserIDX.getText().toString();
        long CageIDX = 0;
        long UserIDX = 0;
        ArrayList<String> IsDupScanALval = null;
        try {
            String IsHOMESuserStr = lblCageInOutSubmitIsHomesUser.getText().toString();
            boolean IsHOMESuser = !IsHOMESuserStr.isEmpty() && !IsHOMESuserStr.equals("") && !IsHOMESuserStr.equals("false") && IsHOMESuserStr != "" && IsHOMESuserStr != "false" ? true : false;
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            gc_cage_accept_reject = objGlobalMethods.getGCMPrefs(gcmp, "gc_cage_accept_reject");
            acceptreject = (gc_cage_accept_reject.equals("0") || gc_cage_accept_reject == null || gc_cage_accept_reject.equals("")) ? 0 : 1;
            CageIDXstr = objGlobalMethods.getGCMPrefs(gcmp, "CageIDX");
            if (!CageIDXstr.isEmpty() && !CageIDXstr.equals("") && CageIDXstr != "" && !CageIDXstr.equals("0") && CageIDXstr != "0") {
                try {
                    CageIDX = Long.parseLong(CageIDXstr);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at CageInOutActivity inputManualEntry2 converting CageIDX. Exception: " + e.getMessage(), "", "", "", "");
                }
            }
            if (!objGlobalMethods.IsEmptyString(UserIDXstr)) {
                try {
                    UserIDX = Long.parseLong(UserIDXstr);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at CageInOutActivity inputManualEntry2 converting UserIDX. Exception: " + e.getMessage(), "", "", "", "");
                }
            }

            if (CageIDX > 0 && UserIDX > 0) {
                objGlobalMethods.writeToFile(input + " captured for verification.", "", "", "", "");
                IsDupScanALval = IsDupScanAL(input, acceptreject);
                if (isCageBarcode(input)) {
                    focusTextbox();
                    playTone();
                    showAlert2(ctx.getString(R.string.cagebarcodescanned));
                } else if (Boolean.parseBoolean(IsDupScanALval.get(1))) {
                    focusTextbox();
                    if (!IsHOMESuser) {
                        playTone();
                        showAlert2(ctx.getString(R.string.alert_duplicatescan) + "\n" + IsDupScanALval.get(0));
                    } else {
                        if (ScannedAItems != null && ScannedAItems.size() > 0) {
                            final int foundPos = IsDupScanALval.get(2).equals("") ? 0 : Integer.parseInt(IsDupScanALval.get(2));
                            ScannedAItems.get(foundPos).setBlink(true);
                            adapter = (CageInOut_PostItemAdapter) listView.getAdapter();
                            if (adapter != null) {
                                objCageInOutItem item = adapter.getItem(foundPos);
                                if (item != null) {
                                    listView.post(new Runnable() {
                                        public void run() {
                                            try {
                                                //scroll the listview to the last item
                                                listView.setSelection(foundPos);
                                                //focus certain view or any other calls after listview completed updating
                                            } catch (Exception e) {
                                                objGlobalMethods.writeToFile("Exception at CageInOutActivity listView.setSelection(foundPos);. Exception: " + e.getMessage(), "", "", "", "");
                                            }
                                        }
                                    });
                                    item.setBlink(true);
                                    adapter.notifyDataSetChanged();
                                    playTone();
                                }
                            }
                        }
                    }
                } else if (IsHOMESuser) {
                    Can_Cage_In t = new Can_Cage_In(UserIDX, CageIDX, input);
                    t.execute();
                } else {
                    Verify_Via_L2_Get_CageResult_First2 t = new Verify_Via_L2_Get_CageResult_First2(input, CageIDX, (gc_cage_accept_reject.equals("0") || gc_cage_accept_reject == null || gc_cage_accept_reject.equals("")) ? 0 : 1);
                    t.execute();
                }
            } else {
                txtCageBarcodeMANUALENTRY.setText("");
                txtCageBarcodeMANUALENTRY.requestFocus();
                playTone();
                showAlert2(ctx.getString(R.string.errorverifying));
            }
        } catch (Exception e) {
            txtCageBarcodeMANUALENTRY.setText("");
            txtCageBarcodeMANUALENTRY.requestFocus();
            objGlobalMethods.writeToFile("Exception at CageInOutActivity inputManualEntry2. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private ArrayList<objCageInOutItem> getScannedAItems(SharedPreferences gcmp) {
        String CageInOutScannedAItemsPrefStr = "";
        ArrayList<objCageInOutItem> jpd = null;
        try {
            CageInOutScannedAItemsPrefStr = objGlobalMethods.getGCMPrefs(gcmp, "CageInOutScannedAItemsPrefStr");
            if (!CageInOutScannedAItemsPrefStr.isEmpty() && !CageInOutScannedAItemsPrefStr.equals("") && !CageInOutScannedAItemsPrefStr.equals("[]") && CageInOutScannedAItemsPrefStr != "" && CageInOutScannedAItemsPrefStr != "[]") {
                Gson gson = new Gson();
                jpd = gson.fromJson(CageInOutScannedAItemsPrefStr, new TypeToken<List<objCageInOutItem>>() {
                }.getType());
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity getScannedAItems. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return jpd;
        }
    }

    private ArrayList<objCageInOutItem> getScannedExcluded(SharedPreferences gcmp) {
        String CageInOutScannedExcludedPrefStr = "";
        ArrayList<objCageInOutItem> jpd = null;
        try {
            CageInOutScannedExcludedPrefStr = objGlobalMethods.getGCMPrefs(gcmp, "CageInOutScannedExcludedPrefStr");
            if (!CageInOutScannedExcludedPrefStr.isEmpty() && !CageInOutScannedExcludedPrefStr.equals("") && !CageInOutScannedExcludedPrefStr.equals("[]") && CageInOutScannedExcludedPrefStr != "" && CageInOutScannedExcludedPrefStr != "[]") {
                Gson gson = new Gson();
                jpd = gson.fromJson(CageInOutScannedExcludedPrefStr, new TypeToken<List<objCageInOutItem>>() {
                }.getType());
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity getScannedExcluded. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return jpd;
        }
    }

    private int getTotalScannedAItemsNExcludedScannedCount() {
        int t = 0;
        try {
            if (ScannedAItems != null) {
                t += ScannedAItems.size();
            }
            if (Excluded != null) {
                t += Excluded.size();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity getTotalScannedAItemsNExcludedScannedCount. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return t;
        }
    }

    private int[] getTotalAllocationItemCountAndScanned(SharedPreferences gcmp) {
        int[] TotalAllocationItemCountAndScanned = new int[]{0, 0};
        int totalAllocated = 0;
        int totalAllocatedScanned = 0;
        String L2_Get_CageResultJsonStr = "";
        objL2_Get_CageResult res = null;
        Gson gson = new Gson();
        try {
            L2_Get_CageResultJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "L2_Get_CageResult");
            if (!L2_Get_CageResultJsonStr.isEmpty() && !L2_Get_CageResultJsonStr.equals("") && !L2_Get_CageResultJsonStr.equals("[]") && L2_Get_CageResultJsonStr != "" && L2_Get_CageResultJsonStr != "[]") {
                res = gson.fromJson(L2_Get_CageResultJsonStr, objL2_Get_CageResult.class);
                if (res != null && res.Items_In_Cage != null && res.Items_In_Cage.length > 0) {
                    totalAllocated = res.Items_In_Cage.length;
                    TotalAllocationItemCountAndScanned[0] = totalAllocated;
                    if (ScannedAItems != null) {
                        for (int i = 0; i <= ScannedAItems.size() - 1; i++) {
                            for (int x = 0; x <= res.Items_In_Cage.length - 1; x++) {
                                if (ScannedAItems.get(i).ref.equals(res.Items_In_Cage[x].Reference.toUpperCase())) {
                                    totalAllocatedScanned++;
                                    break;
                                }
                            }
                        }
                    }
                    if (Excluded != null) {
                        for (int i = 0; i <= Excluded.size() - 1; i++) {
                            for (int x = 0; x <= res.Items_In_Cage.length - 1; x++) {
                                if (Excluded.get(i).ref.equals(res.Items_In_Cage[x].Reference.toUpperCase())) {
                                    totalAllocatedScanned++;
                                    break;
                                }
                            }
                        }
                    }
                    TotalAllocationItemCountAndScanned[1] = totalAllocatedScanned;
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity getTotalAllocationItemCountAndScanned. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return TotalAllocationItemCountAndScanned;
        }
    }

    public void enabledisablectrlsAccordingToUserType() {
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        Context ctx = myApp.getAppContext();
        try {
            String IsHOMESuserStr = lblCageInOutSubmitIsHomesUser.getText().toString();
            boolean IsHOMESuser = !IsHOMESuserStr.isEmpty() && !IsHOMESuserStr.equals("") && !IsHOMESuserStr.equals("false") && IsHOMESuserStr != "" && IsHOMESuserStr != "false" ? true : false;

            if (IsHOMESuser) {
                lblCageItemTotalSubmitCOUNT.setText(ScannedAItems != null ? String.valueOf(ScannedAItems.size()) : "0");
                ctrlSettings.enableDisableCommonButtonWithStyles(ScannedAItems != null && ScannedAItems.size() > 0 ? true : false, btnCageSubmitCLEARALL);
                ctrlSettings.enableDisableSubmitButtonWithStyles(ScannedAItems != null && ScannedAItems.size() > 0 ? true : false, btnCageSubmitSUBMIT);
            } else {
                String aitemscount = ScannedAItems != null ? String.valueOf(ScannedAItems.size()) : "0";
                String excludedcount = Excluded != null ? String.valueOf(Excluded.size()) : "0";
                int[] TotalAllocationItemCountAndScanned = getTotalAllocationItemCountAndScanned(gcmp);
                int totalAllocated = TotalAllocationItemCountAndScanned[0];
                int totalAllocatedScanned = TotalAllocationItemCountAndScanned[1];
                int totalScanned = getTotalScannedAItemsNExcludedScannedCount();
                lblCageItemTotalAllocatedCOUNT.setText(ctx.getString(R.string.totalallocated) + " " + String.valueOf(totalAllocated));
                lblCageItemTotalAllocatedScannedCOUNT.setText(ctx.getString(R.string.totalallocatedscanned) + " " + String.valueOf(totalAllocatedScanned));
                lblCageItemTotalSubmitCOUNT.setText(ctx.getString(R.string.buttontext_accept) + ": " + aitemscount + " / " + ctx.getString(R.string.buttontext_reject) + ": " + excludedcount + " / " + ctx.getString(R.string.totalapplying) + " " + String.valueOf(totalScanned));
                ctrlSettings.enableDisableCommonButtonWithStyles(totalScanned > 0 ? true : false, btnCageSubmitCLEARALL);
                ctrlSettings.enableDisableSubmitButtonWithStyles(totalAllocatedScanned != totalAllocated ? false : true, btnCageSubmitSUBMIT);
            }
            txtCageBarcodeMANUALENTRY.setText("");
            focusTextbox();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity enabledisablectrlsAccordingToUserType. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private class Can_Cage_In extends AsyncTask<Void, Void, objCan_Cage_InResult> {
        final String ASearchString;
        final long ACageIDX;
        final long AUserIDX;

        Can_Cage_In(long aAUserIDX, long aACageIDX, String aASearchString) {
            this.ACageIDX = aACageIDX;
            this.ASearchString = aASearchString;
            this.AUserIDX = aAUserIDX;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            try {
                pdCheck.show();
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageInOutAcitivity Can_Cage_In onPreExecute. Exception: " + e.getMessage(), "", "", "", "");
            }
        }

        @Override
        protected objCan_Cage_InResult doInBackground(Void... params) {
            objCan_Cage_InResult res = null;
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            try {
                objGlobalMethods.writeToFile("Processing at CageInOutActivity Can_Cage_In doInBackground. ACageIDX: " + String.valueOf(ACageIDX) + ", ASearchString: " + ASearchString, "", "", "", "");
                //res = WebService.Can_Cage_In(ACageIDX, ASearchString);
                res = WebService.Can_CageIn(AUserIDX, ACageIDX, ASearchString);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageInOutActivity Can_Cage_In doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final objCan_Cage_InResult res) {
            Context ctx = myApp.getAppContext();
            try {
                if (pdCheck != null) {
                    pdCheck.dismiss();
                }
                JobsHandler.setCurrDTasPrevDTforIdlingCheck();
                if (res == null) {
                    objGlobalMethods.writeToFile("ACageIDX: " + String.valueOf(ACageIDX) + ", ASearchString: " + ASearchString + " not verified at CageInOutActivity Can_Cage_In. res is null.", "", "", "", "");
                    playTone();
                    showAlert2(ctx.getString(R.string.errorverifying));
                } else if (res != null && res.Status != 0 && res.Status == 1) {
                    objGlobalMethods.writeToFile("ACageIDX: " + String.valueOf(ACageIDX) + ", ASearchString: " + ASearchString + " not found in system. Req verification at CageInOutActivity.", "", "", "", "");
                    playTone();
                    addToScannedAItemsList2(ASearchString, ASearchString, "0", 3);
                } else if (res != null && res.Status != 0 && res.Status != 1) {
                    objGlobalMethods.writeToFile("ACageIDX: " + String.valueOf(ACageIDX) + ", ASearchString: " + ASearchString + " not verified at CageInOutActivity Can_Cage_In. res.Message: " + res.Message, "", "", "", "");
                    playTone();
                    showAlert2(res.Message);
                } else if (res != null && res.Status == 0) {
                    objGlobalMethods.writeToFile("ACageIDX: " + String.valueOf(ACageIDX) + ", ASearchString: " + ASearchString + " verified at CageInOutActivity Can_Cage_In.", "", "", "", "");
                    playSuccessVerifiedTone();
                    addToScannedAItemsList2(ASearchString, ASearchString, "0", 0);
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageInOutActivity Can_Cage_In onPostExecute for ACageIDX: " + String.valueOf(ACageIDX) + ", ASearchString: " + ASearchString + ". Exception: " + e.getMessage(), "", "", "", "");
                playTone();
                showAlert2(ctx.getString(R.string.errorverifying));
            } finally {
                enabledisablectrlsAccordingToUserType();
            }
        }
    }

    private class CageIn extends AsyncTask<Void, Void, objCage_InResult> {
        final long CageIDX;

        public CageIn(long aCageIDX) {
            this.CageIDX = aCageIDX;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected objCage_InResult doInBackground(Void... params) {
            objCage_InResult res = null;
            String scannedItemsStr = "";
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            SharedPreferences up = objGlobalMethods.getUpdateJobInfoPreferences();
            ArrayList<String> refAL = new ArrayList<>();
            String ref = "";
            try {
                objGlobalMethods.writeToFile("CageInOutActivity CageIn started.", "", "", "", "");
                String HQ_CageIn_ScheduledDateTimeStr = objGlobalMethods.getGCMPrefs(gcmp, "HQ_CageIn_ScheduledDateTimeStr");
                String useridxStr = objGlobalMethods.getGCMPrefs(gcmp, "IDX");
                for (int i = 0; i <= ScannedAItems.size() - 1; i++) {
                    ref = ScannedAItems.get(i).ref;
                    refAL.add(ref);
                    if (i == ScannedAItems.size() - 1) {
                        scannedItemsStr += ref;
                    } else {
                        scannedItemsStr += ref + ",";
                    }
                }

                objGlobalMethods.writeToFile("CageInOutActivity CageIn processing: " + scannedItemsStr, "", "", "", "");
                res = WebService.Cage_In(Long.parseLong(useridxStr), CageIDX, HQ_CageIn_ScheduledDateTimeStr, refAL);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageInOutActivity CageIn doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final objCage_InResult res) {
            Context ctx = myApp.getAppContext();
            try {
                if (pd != null) {
                    pd.dismiss();
                }
                JobsHandler.setCurrDTasPrevDTforIdlingCheck();

                if (res == null || (res != null && res.Status != 0)) {
                    if (res == null) {
                        objGlobalMethods.writeToFile("CageInOutActivity CageIn fails due res == null. ", "", "", "", "");
                    } else {
                        objGlobalMethods.writeToFile("CageInOutActivity CageIn fails due res Status: " + String.valueOf(res.Status) + ", Message: " + res.Message, "", "", "", "");
                    }
                    playTone();
                    showAlert2(ctx.getString(R.string.alert_errorperformoperation));
                    focusTextbox();
                } else if (res != null && res.Status == 0) {
                    objGlobalMethods.writeToFile("CageInOutActivity CageIn success.", "", "", "", "");
                    //showSubmissionOKAlert("");
                    playSuccessVerifiedTone();

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            objGlobalMethods.clearCageScanActivityGcmPrefs(GcmPreferences);
                            fullGClistItems = null;
                            ScannedAItems = null;
                            Excluded = null;
                            Intent parentIntent;
                            parentIntent = new Intent(CageInOutActivity.this, CageScanActivity.class);
                            MainActivity.uponLogin = 0;
                            startActivity(parentIntent);
                            myApp.currentActivity.finish();
                        }
                    }, 600);

                    /*GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.clearCageScanActivityGcmPrefs(GcmPreferences);
                    fullGClistItems = null;
                    ScannedAItems = null;
                    Excluded = null;
                    Intent parentIntent;
                    parentIntent = new Intent(CageInOutActivity.this, CageScanActivity.class);
                    MainActivity.uponLogin = 0;
                    startActivity(parentIntent);
                    myApp.currentActivity.finish();*/
                }
            } catch (Exception e) {
                playTone();
                objGlobalMethods.writeToFile("Exception at CageInOutActivity CageIn onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
                showAlert2(ctx.getString(R.string.alert_errorperformoperation));
                focusTextbox();
            }
        }
    }

    private class Verify_Via_L2_Get_CageResult_First2 extends AsyncTask<Void, Void, obj_Verify_Via_L2_Get_CageResult_First> {
        final String ASearchString;
        final long ACageIDX;
        final int gc_cage_accept_reject;

        Verify_Via_L2_Get_CageResult_First2(String aASearchString, long aACageIDX, int agc_cage_accept_reject) {
            this.ASearchString = aASearchString.toUpperCase();
            this.ACageIDX = aACageIDX;
            this.gc_cage_accept_reject = agc_cage_accept_reject;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdCheck.show();
        }

        @Override
        protected obj_Verify_Via_L2_Get_CageResult_First doInBackground(Void... params) {
            obj_Verify_Via_L2_Get_CageResult_First al = new obj_Verify_Via_L2_Get_CageResult_First(false, ASearchString);
            Gson gson = new Gson();
            String L2_Get_CageResultJsonStr;
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            objL2_Get_CageResult CageResult = null;
            objCageItem[] Items_In_Cage = null;
            try {
                L2_Get_CageResultJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "L2_Get_CageResult");
                if (!L2_Get_CageResultJsonStr.isEmpty() && !L2_Get_CageResultJsonStr.equals("") && L2_Get_CageResultJsonStr != "") {
                    CageResult = gson.fromJson(L2_Get_CageResultJsonStr, objL2_Get_CageResult.class);
                    if (CageResult != null && CageResult.Items_In_Cage != null && CageResult.Items_In_Cage.length > 0) {
                        Items_In_Cage = CageResult.Items_In_Cage;
                        for (int i = 0; i <= Items_In_Cage.length - 1; i++) {
                            if (Items_In_Cage[i].Reference.toUpperCase().equals(ASearchString)) {
                                al.setExists(true);
                                al.setJob_ref(Items_In_Cage[i].Job_Ref.toUpperCase());
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageInOutActivity Verify_Via_L2_Get_CageResult_First2 doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return al;
            }
        }

        @Override
        protected void onPostExecute(final obj_Verify_Via_L2_Get_CageResult_First res) {
            Context ctx = myApp.getAppContext();
            try {
                if (pdCheck != null) {
                    pdCheck.dismiss();
                }
                JobsHandler.setCurrDTasPrevDTforIdlingCheck();
                if (res.exists) {
                    L2_Verify_Cage_Item t = new L2_Verify_Cage_Item(ASearchString, res.job_ref, ACageIDX, gc_cage_accept_reject);
                    t.execute();
                } else {
                    L2_Verify_Cage_Item t = new L2_Verify_Cage_Item(ASearchString, ASearchString, ACageIDX, gc_cage_accept_reject);
                    t.execute();
                    /*playTone();
                    objGlobalMethods.writeToFile(ASearchString + " is not part of CageIDX " + String.valueOf(ACageIDX) + ". Add into EXCLUDED LIST.", "", "", "", "");
                    showAlert(ASearchString + " " + ctx.getString(R.string.items_dun_belong_to_this_cage)).show();*/
                }
            } catch (Exception e) {
                playTone();
                objGlobalMethods.writeToFile("Exception at CageInOutActivity Verify_Via_L2_Get_CageResult_First2 onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
                showAlert2(ctx.getString(R.string.errorverifying));
            }
        }

    }

    private class L2_Verify_Cage_Item extends AsyncTask<Void, Void, objL2_Verify_Cage_ItemResult> {
        final String ASearchString;
        final String job_ref;
        final long ACageIDX;
        final int gc_cage_accept_reject;

        L2_Verify_Cage_Item(String aASearchString, String ajob_ref, long aACageIDX, int agc_cage_accept_reject) {
            this.ASearchString = aASearchString;
            this.job_ref = ajob_ref;
            this.ACageIDX = aACageIDX;
            this.gc_cage_accept_reject = agc_cage_accept_reject;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdCheck.show();
        }

        @Override
        protected objL2_Verify_Cage_ItemResult doInBackground(Void... params) {
            objL2_Verify_Cage_ItemResult res = null;
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            try {
                objGlobalMethods.writeToFile("Processing at CageInOutActivity L2_Verify_Cage_Item doInBackground. ACageIDX: " + String.valueOf(ACageIDX) + ", ASearchString: " + ASearchString, "", "", "", "");
                String regID = objGlobalMethods.getRegistrationIdOnly();
                res = WebService.L2_Verify_Cage_Item(regID, ACageIDX, ASearchString);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageInOutActivity L2_Verify_Cage_Item doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final objL2_Verify_Cage_ItemResult res) {
            Context ctx = myApp.getAppContext();
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            try {
                if (pdCheck != null) {
                    pdCheck.dismiss();
                }
                JobsHandler.setCurrDTasPrevDTforIdlingCheck();
                if (res == null) {
                    objGlobalMethods.writeToFile("ACageIDX: " + String.valueOf(ACageIDX) + ", ASearchString: " + ASearchString + " not verified at CageInOutActivity L2_Verify_Cage_Item. res is null.", "", "", "", "");
                    playTone();
                    showAlert2(ctx.getString(R.string.errorverifying));
                } else if (res != null && res.Status != 0) {
                    objGlobalMethods.writeToFile("ACageIDX: " + String.valueOf(ACageIDX) + ", ASearchString: " + ASearchString + " not verified at CageInOutActivity L2_Verify_Cage_Item. res.Message: " + res.Message, "", "", "", "");
                    playTone();
                    showAlert2(res.Message);
                } else if (res != null && res.Status == 0) {
                    playSuccessVerifiedTone();
                    addToScannedAItemsList2(ASearchString, job_ref, res.Message, 1);
                }
            } catch (Exception e) {
                playTone();
                objGlobalMethods.writeToFile("Exception at CageInOutActivity L2_Verify_Cage_Item onPostExecute for ACageIDX: " + String.valueOf(ACageIDX) + ", ASearchString: " + ASearchString + ". Exception: " + e.getMessage(), "", "", "", "");
                showAlert2(ctx.getString(R.string.errorverifying));
            } finally {
                enabledisablectrlsAccordingToUserType();
                if (gc_cage_accept_reject == 0) {
                    ctrlSettings.enableDisableCommonButtonWithStyles(ScannedAItems != null && ScannedAItems.size() > 0 ? true : false, btnCageSubmitCLEARALL);
                } else {
                    ctrlSettings.enableDisableCommonButtonWithStyles(Excluded != null && Excluded.size() > 0 ? true : false, btnCageSubmitCLEARALL);
                }
            }
        }
    }

    private void addToScannedAItemsList2(String cn, String job_ref, String JobsIDX, int colorCode) {
        Context ctx = myApp.getAppContext();
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        objCageInOutItem item;
        try {
            ScannedAItems = getScannedAItems(gcmp);
            if (ScannedAItems == null) {
                ScannedAItems = new ArrayList<objCageInOutItem>();
            }
            item = new objCageInOutItem(cn, false, job_ref, Long.parseLong(JobsIDX));
            item.setColor(colorCode);
            ScannedAItems.add(item);
            Gson gson = new Gson();
            String CageInOutScannedAItemsPrefStr = gson.toJson(ScannedAItems);
            objGlobalMethods.setGCMPrefs(gcmp, "CageInOutScannedAItemsPrefStr", CageInOutScannedAItemsPrefStr);
            objGlobalMethods.writeToFile(cn + " added to ScannedAItems.", "", "", "", "");
            ScannedAItems.get(ScannedAItems.size() - 1).setBlink(true);

            if (colorCode == 0 || colorCode == 3) {
                if (adapter == null) {
                    adapter = new CageInOut_PostItemAdapter(this, ScannedAItems);
                    listView.setAdapter(adapter);
                } else {
                    adapter.clear();
                    adapter.addAll(ScannedAItems);
                }

                //runs after listview is done updating itself
                listView.post(new Runnable() {
                    public void run() {
                        //scroll the listview to the last item
                        listView.setSelection(listView.getCount() - 1);
                        //focus certain view or any other calls after listview completed updating

                    }
                });
            } else {
                int scrolltopos = -1;
                objCageInOutItem ioi = null;
                if (fullGClistItems != null && fullGClistItems.size() > 0) {
                    for (int i = 0; i <= fullGClistItems.size() - 1; i++) {
                        ioi = fullGClistItems.get(i);
                        if (ioi.ref.toUpperCase().equals(cn) || ioi.JobsIDX == Long.parseLong(JobsIDX)) {
                            scrolltopos = i;
                            break;
                        }
                    }
                }
                loadlistGC(scrolltopos);
            }

            txtCageBarcodeMANUALENTRY.setText("");
            focusTextbox();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity addToScannedAItemsList2. Exception: " + e.getMessage(), "", "", "", "");
            playTone();
            showAlert2(ctx.getString(R.string.alert_errorperformoperation));
        }
    }

    private class CageOut extends AsyncTask<Void, Void, objL2_Cage_OutResult> {
        final long CageIDX;

        public CageOut(long aCageIDX) {
            this.CageIDX = aCageIDX;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected objL2_Cage_OutResult doInBackground(Void... params) {
            objL2_Cage_OutResult res = null;
            String scannedItemsStr = "";
            String scannedRejectedItemsStr = "";
            String log = "CageInOutActivity CageOut processing:";
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            SharedPreferences up = objGlobalMethods.getUpdateJobInfoPreferences();
            ArrayList<String> ScannedAItemsAL = new ArrayList<>();
            ArrayList<String> ExcludedAL = new ArrayList<>();
            String ScannedStr = "";
            String ExcludedStr = "";
            try {
                objGlobalMethods.writeToFile("CageInOutActivity CageOut started.", "", "", "", "");
                if (ScannedAItems != null) {
                    for (int i = 0; i <= ScannedAItems.size() - 1; i++) {
                        ScannedStr = ScannedAItems.get(i).ref;
                        ScannedAItemsAL.add(ScannedStr);
                        if (i == ScannedAItems.size() - 1) {
                            scannedItemsStr += ScannedStr;
                        } else {
                            scannedItemsStr += ScannedStr + ",";
                        }
                    }
                }
                if (Excluded != null) {
                    for (int i = 0; i <= Excluded.size() - 1; i++) {
                        ExcludedStr = Excluded.get(i).ref;
                        ExcludedAL.add(ExcludedStr);
                        if (i == Excluded.size() - 1) {
                            scannedRejectedItemsStr += ExcludedStr;
                        } else {
                            scannedRejectedItemsStr += ExcludedStr + ",";
                        }
                    }
                }

                if (!scannedItemsStr.isEmpty() && !scannedItemsStr.equals("") && scannedItemsStr != "") {
                    log += " Accepted: " + scannedItemsStr;
                }
                if (!scannedRejectedItemsStr.isEmpty() && !scannedRejectedItemsStr.equals("") && scannedRejectedItemsStr != "") {
                    log += " Rejected: " + scannedRejectedItemsStr;
                }

                objGlobalMethods.writeToFile(log, "", "", "", "");
                String ARegID = objGlobalMethods.getRegistrationIdOnly();
                res = WebService.L2_Cage_Out(ARegID, CageIDX, ScannedAItemsAL, ExcludedAL);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CageInOutActivity CageOut doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final objL2_Cage_OutResult res) {
            Context ctx = myApp.getAppContext();
            try {
                if (pd != null) {
                    pd.dismiss();
                }
                JobsHandler.setCurrDTasPrevDTforIdlingCheck();
                if (res == null || (res != null && res.Status != 0)) {
                    if (res == null) {
                        objGlobalMethods.writeToFile("CageInOutActivity CageOut fails due res == null. ", "", "", "", "");
                    } else {
                        objGlobalMethods.writeToFile("CageInOutActivity CageOut fails due res Status: " + String.valueOf(res.Status) + ", Message: " + res.Message, "", "", "", "");
                    }
                    playTone();
                    showAlert2(ctx.getString(R.string.alert_errorperformoperation));
                    focusTextbox();
                } else if (res != null && res.Status == 0) {
                    objGlobalMethods.writeToFile("CageInOutActivity CageOut success.", "", "", "", "");
                    showSubmissionOKAlert(res.Message);
                }
            } catch (Exception e) {
                playTone();
                objGlobalMethods.writeToFile("Exception at CageInOutActivity CageOut onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
                showAlert2(ctx.getString(R.string.alert_errorperformoperation));
                focusTextbox();
            }
        }
    }

    private class reSync extends AsyncTask<Void, Void, Boolean[]> {
        final String WDBarcode;
        final SharedPreferences gcmp;

        reSync() {
            this.gcmp = objGlobalMethods.getGcmPreferences();
            this.WDBarcode = objGlobalMethods.getGCMPrefs(gcmp, "cagebarcode");
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Boolean[] doInBackground(Void... params) {
            Boolean asd[] = new Boolean[]{false, false};
            ArrayList<objCageInOutItem> list = null;
            objL2_Get_CageResult res = null;
            String regID = objGlobalMethods.getRegistrationIdOnly();
            try {
                objGlobalMethods.writeToFile("Processing at CageInOutActivity reSync doInBackground. WDBarcode: " + WDBarcode, "", "", "", "");
                res = WebService.L2_Get_Cage(regID, WDBarcode);
                if (res == null) {
                    objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " not processed at CageInOutActivity reSync. res is null.", "", "", "", "");
                } else if (res != null && res.IDX == 0) {
                    objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " not processed at CageInOutActivity reSync. res.IDX == 0.", "", "", "", "");
                } else {
                    asd[0] = true;
                    Gson gson = new Gson();
                    String L2_Get_CageResultJsonStr = gson.toJson(res);
                    if (res.Items_In_Cage != null && res.Items_In_Cage.length > 0) {
                        objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " processed at CageInOutActivity reSync.", "", "", "", "");
                        objGlobalMethods.setGCMPrefs(gcmp, "L2_Get_CageResult", L2_Get_CageResultJsonStr);
                        asd[1] = true;
                    } else {
                        objGlobalMethods.writeToFile("WDBarcode: " + WDBarcode + " processed at CageInOutActivity reSync. No Items in cage.", "", "", "", "");
                        objGlobalMethods.setGCMPrefs(gcmp, "L2_Get_CageResult", "");
                        asd[1] = false;
                    }
                }
            } catch (Exception e) {
                asd[0] = false;
                asd[1] = false;
                objGlobalMethods.writeToFile("Exception at CageInOutActivity reSync doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean[] res) {
            Context ctx = myApp.getAppContext();
            try {
                pd.dismiss();
                JobsHandler.setCurrDTasPrevDTforIdlingCheck();
                if (res[0] && res[1]) {
                    loadlistGC(0);
                } else {
                    playTone();
                    showAlert2((res[0] && !res[1]) ? ctx.getString(R.string.alert_nojobfound) : ctx.getString(R.string.alert_retreiveErr));
                }
            } catch (Exception e) {
                if (pd != null) {
                    pd.dismiss();
                }
                objGlobalMethods.writeToFile("Exception at CageInOutActivity reSync onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                enabledisablectrlsAccordingToUserType();
                ctrlSettings.enableDisableCommonButtonWithStyles(ScannedAItems != null && ScannedAItems.size() > 0 ? true : false, btnCageSubmitCLEARALL);
            }
        }

    }

    private void showSubmissionOKAlert(String msg) {
        Context ctx = myApp.getAppContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);

        builder.setMessage(!msg.isEmpty() && !msg.equals("") && msg != "" ? msg : ctx.getString(R.string.alert_data_submittedsuccess));
        builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.clearCageScanActivityGcmPrefs(GcmPreferences);
                fullGClistItems = null;
                ScannedAItems = null;
                Excluded = null;
                Intent parentIntent;
                parentIntent = new Intent(CageInOutActivity.this, CageScanActivity.class);
                MainActivity.uponLogin = 0;
                startActivity(parentIntent);
                myApp.currentActivity.finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.0f);
    }

    private void showAlert2(String msg) {
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
        alertdialog.show();
        alertdialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.0f);
    }

    private Dialog showAlert(String msg) {
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
                try {
                    listView.clearFocus();
                    txtCageBarcodeMANUALENTRY.setText("");
                    txtCageBarcodeMANUALENTRY.requestFocus();
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at CageInOutActivity focusTextbox. Exception: " + e.getMessage(), "", "", "", "");
                }
            }
        }, 100);
    }

    public void btnbtnCageAcceptClick(View view) {
        togglePUDIP("0", true);
    }

    public void btnCageRejectClick(View view) {
        togglePUDIP("1", true);
    }

    private void listItemHQOnClick() {
        final Context ctx = myApp.getAppContext();
        try {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    View v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                    final int deleteRow = position;
                    if (v == null)
                        return;

                    TextView lblcn = (TextView) v.findViewById(R.id.lblcn);
                    AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                    builder.setCancelable(false);

                    builder.setMessage(myApp.getAppContext().getString(R.string.alert_submitjobsheetcfmDelete) + " " + lblcn.getText().toString() + " ?");

                    builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user pressed "yes", update job status
                            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                            String gc_cage_accept_reject = "0";
                            gc_cage_accept_reject = objGlobalMethods.getGCMPrefs(gcmp, "gc_cage_accept_reject");
                            int acceptreject = (gc_cage_accept_reject.equals("0") || gc_cage_accept_reject == null || gc_cage_accept_reject.equals("")) ? 0 : 1;
                            String removingRef = "";

                            removingRef = ScannedAItems.get(deleteRow).ref;
                            objGlobalMethods.writeToFile("Removing " + removingRef + " from Accept list.", "", "", "", "");
                            ScannedAItems.remove(deleteRow);
                            if (ScannedAItems.size() > 0) {
                                Gson gson = new Gson();
                                String scannedItemsPrefStr = "";
                                scannedItemsPrefStr = gson.toJson(ScannedAItems);
                                objGlobalMethods.setGCMPrefs(gcmp, "CageInOutScannedAItemsPrefStr", scannedItemsPrefStr);
                            } else if (ScannedAItems.size() == 0) {
                                objGlobalMethods.setGCMPrefs(gcmp, "CageInOutScannedAItemsPrefStr", "");
                            }
                            loadlistHQ();

                            enabledisablectrlsAccordingToUserType();
                            if (acceptreject == 0) {
                                ctrlSettings.enableDisableCommonButtonWithStyles(ScannedAItems != null && ScannedAItems.size() > 0 ? true : false, btnCageSubmitCLEARALL);
                            } else {
                                ctrlSettings.enableDisableCommonButtonWithStyles(Excluded != null && Excluded.size() > 0 ? true : false, btnCageSubmitCLEARALL);
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
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.0f);
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.0f);
                }
            });
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity listItemHQOnClick. Exception: " + e.getMessage(), "", "", "", "");
            playTone();
            showAlert2(ctx.getString(R.string.alert_error));
        }
    }

    private void listItemGCOnClick() {
        final Context ctx = myApp.getAppContext();
        try {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    View v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                    objCageInOutItem selecteditem = null;
                    final int deleteRow = position;
                    if (v == null)
                        return;

                    TextView lblcn = (TextView) v.findViewById(R.id.lblcn);
                    AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                    builder.setCancelable(false);

                    if (fullGClistItems != null && fullGClistItems.size() > 0 && fullGClistItems.size() >= position + 1) {
                        selecteditem = fullGClistItems.get(deleteRow);
                        final objCageInOutItem itemInQns = selecteditem;

                        if (itemInQns.color > 0 && itemInQns.color < 3) {
                            if (itemInQns.color == 1) {
                                builder.setMessage(myApp.getAppContext().getString(R.string.cfmreject) + " " + lblcn.getText().toString() + " ?");
                            } else {
                                builder.setMessage(myApp.getAppContext().getString(R.string.cfmaccept) + " " + lblcn.getText().toString() + " ?");
                            }

                            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if user pressed "yes", update job status
                                    String removingRef = "";
                                    removingRef = itemInQns.ref;
                                    objGlobalMethods.writeToFile("Removing " + removingRef + (itemInQns.color == 1 ? " from Accept list." : " from Excluded list."), "", "", "", "");

                                    if (itemInQns.color == 1) {
                                        if (Excluded == null) {
                                            Excluded = new ArrayList<objCageInOutItem>();
                                        }
                                        objCageInOutItem nobj = itemInQns;
                                        nobj.setColor(2);
                                        Excluded.add(nobj);
                                        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                                        Gson gson = new Gson();
                                        String CageInOutScannedExcludedPrefStr = gson.toJson(Excluded);
                                        objGlobalMethods.setGCMPrefs(gcmp, "CageInOutScannedExcludedPrefStr", CageInOutScannedExcludedPrefStr);
                                        objGlobalMethods.writeToFile(nobj.ref + " added to Excluded.", "", "", "", "");

                                        for (int i = 0; i <= ScannedAItems.size() - 1; i++) {
                                            if (ScannedAItems.get(i).ref.equals(itemInQns.ref)) {
                                                ScannedAItems.remove(i);
                                                String CageInOutScannedAItemsPrefStr = gson.toJson(ScannedAItems);
                                                objGlobalMethods.setGCMPrefs(gcmp, "CageInOutScannedAItemsPrefStr", CageInOutScannedAItemsPrefStr);
                                                objGlobalMethods.writeToFile(nobj.ref + " removed from ScannedAItems.", "", "", "", "");
                                                break;
                                            }
                                        }
                                    } else {
                                        if (ScannedAItems == null) {
                                            ScannedAItems = new ArrayList<objCageInOutItem>();
                                        }
                                        objCageInOutItem nobj = itemInQns;
                                        nobj.setColor(1);
                                        ScannedAItems.add(nobj);
                                        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                                        Gson gson = new Gson();
                                        String CageInOutScannedAItemsPrefStr = gson.toJson(ScannedAItems);
                                        objGlobalMethods.setGCMPrefs(gcmp, "CageInOutScannedAItemsPrefStr", CageInOutScannedAItemsPrefStr);
                                        objGlobalMethods.writeToFile(nobj.ref + " added to ScannedAItems.", "", "", "", "");

                                        for (int i = 0; i <= Excluded.size() - 1; i++) {
                                            if (Excluded.get(i).ref.equals(itemInQns.ref)) {
                                                Excluded.remove(i);
                                                String CageInOutScannedExcludedPrefStr = gson.toJson(Excluded);
                                                objGlobalMethods.setGCMPrefs(gcmp, "CageInOutScannedExcludedPrefStr", CageInOutScannedExcludedPrefStr);
                                                objGlobalMethods.writeToFile(nobj.ref + " removed from Excluded.", "", "", "", "");
                                                break;
                                            }
                                        }
                                    }
                                    loadlistGC(deleteRow);
                                    enabledisablectrlsAccordingToUserType();
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
                }
            });
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageInOutActivity listItemGCOnClick. Exception: " + e.getMessage(), "", "", "", "");
            playTone();
            showAlert2(ctx.getString(R.string.alert_error));
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
                    objGlobalMethods.writeToFile("Bluetooth is on at CageInOutActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CageInOutActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }



}
