package com.xdel.luke.mpd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.BottomSheet.ProgressBarDialog;
import com.xdel.luke.Caging.CageScanActivity;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsheetActivity;
import com.xdel.luke.JobsheetSubmitActivity;
import com.xdel.luke.MainActivity;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objMPD_Info;
import com.xdel.luke.objClasses.xwsBase;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.MacDevicePair;
import com.xdel.luke.templogger.TemperaturePayload;
import com.xdel.luke.templogger.bttemploggerStandaloneclass;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MPDMainActivity extends AppCompatActivity {
    Context context;
    ConnectionDetector cd;
    private MediaPlayer player;
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);

    private String username;
    private String password;
    private String useridx;
    private String driveridx;
    private String ishomesuser;
    private String[] UsernamePwd = new String[4];
    static String cameraScannedCode;

    private LinearLayout LLMPDMainBlank, LLMPDMainShown, LLMPDMainEquipments, LLMPDMainJobs, LLmodeop;
    private ListView lvMPDMainEquipments;
    private ListView lvMPDMainJobs;
    private EditText txtMPDMainBarcodeMANUALENTRY;
    private Button btnMPDMainBarcodeSubmitENTER, btnMPDMainScanMPD, btnMPDMainEq, btnMPDMainJob, btnMPDMainDel, btnMPDMainAdd, btnMPDMainCANCEL;
    private ProgressBarDialog pd, pdCheck;

    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage, lblMPDMainMPDGroupIDX, lblMPDMainSubmitIsHomesUser, lblMPDMainUserIDX, lblMPDMainDriverIDX, lbljoblog, lbleqlog;

    private MPDMainMPD_ItemListAdapter MPD_ItemListAdapter;
    private MPDMainJob_ItemListAdapter Job_ItemListAdapter;
    private ArrayList<MPD_Item> mpditemlist;
    private ArrayList<String> refattlist;

    String MPDGroupIDX = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
        setContentView(R.layout.activity_m_p_d_main);

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);
        lblMPDMainMPDGroupIDX = findViewById(R.id.lblMPDMainMPDGroupIDX);
        lblMPDMainSubmitIsHomesUser = findViewById(R.id.lblMPDMainSubmitIsHomesUser);
        lblMPDMainUserIDX = findViewById(R.id.lblMPDMainUserIDX);
        lblMPDMainDriverIDX = findViewById(R.id.lblMPDMainDriverIDX);
        lbljoblog = findViewById(R.id.lbljoblog);
        lbleqlog = findViewById(R.id.lbleqlog);
        txtMPDMainBarcodeMANUALENTRY = findViewById(R.id.txtMPDMainBarcodeMANUALENTRY);
        btnMPDMainBarcodeSubmitENTER = findViewById(R.id.btnMPDMainBarcodeSubmitENTER);
        btnMPDMainScanMPD = findViewById(R.id.btnMPDMainScanMPD);

        LLMPDMainBlank = findViewById(R.id.LLMPDMainBlank);
        LLMPDMainShown = findViewById(R.id.LLMPDMainShown);
        LLmodeop = findViewById(R.id.LLmodeop);

        btnMPDMainEq = findViewById(R.id.btnMPDMainEq);
        btnMPDMainJob = findViewById(R.id.btnMPDMainJob);

        LLMPDMainEquipments = findViewById(R.id.LLMPDMainEquipments);
        LLMPDMainJobs = findViewById(R.id.LLMPDMainJobs);

        lvMPDMainEquipments = (ListView) findViewById(R.id.lvMPDMainEquipments);
        lvMPDMainJobs = (ListView) findViewById(R.id.lvMPDMainJobs);

        btnMPDMainDel = findViewById(R.id.btnMPDMainDel);
        btnMPDMainAdd = findViewById(R.id.btnMPDMainAdd);
        btnMPDMainCANCEL = findViewById(R.id.btnMPDMainCANCEL);

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
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            context = myApp.getAppContext();

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            Boolean IsHOMESuser = objGlobalMethods.IsHomesUserString(ishomesuser, GcmPreferences);
            lblMPDMainUserIDX.setText(IsHOMESuser ? useridx : "");
            lblMPDMainDriverIDX.setText(!IsHOMESuser ? useridx : "");

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
                objGlobalMethods.writeToFile("MPDMainActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

            JobsHandler.checkForceLogOut();
            DashboardSubActivity.frompage = null;
            lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

            boolean idledMoreThan3Hrs = JobsHandler.idledMoreThan3Hrs_new();
            if (idledMoreThan3Hrs) {
                objGlobalMethods.writeToFile("Idled more than 3 hours. Manual force logout.", "", "", "", "");
                JobsHandler.ManualForceLogout();
            }

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
                initPage(IsHOMESuser, GcmPreferences);
            }

            //initPage(IsHOMESuser, GcmPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GcmPreferences = null;
        UpdateJobInfoPreferences = null;
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at MPDMainActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at MPDMainActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
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
        String[] prefName = new String[]{"cameraScannedCode", "QRScanRequestIntent"};
        String[] prefvalue = new String[]{"", "MPDMainActivity"};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        parentIntent = new Intent(MPDMainActivity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void btnMPDMainBarcodeSubmitENTER_Click(View v) {
        //MPDMainMode 0 blank, scan mpd to get group
        //MPDMainMode 1, equipment
        //MPDMainMode 2, jobs
        //MPDScanMode 0, scan mpd to get group
        //MPDScanMode 1, attach
        //MPDScanMode 2, detach
        String input = "";
        String MPDMainMode = "";
        String MPDScanMode = "";
        int MPDMainModeInt = 0;
        int MPDScanModeInt = 0;
        //SharedPreferences g = objGlobalMethods.getGcmPreferences();
        try {
            MPDMainMode = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDMainMode");
            MPDScanMode = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDScanMode");
            MPDMainModeInt = !objGlobalMethods.IsEmptyString(MPDMainMode) ? Integer.parseInt(MPDMainMode) : 0;
            MPDScanModeInt = !objGlobalMethods.IsEmptyString(MPDScanMode) ? Integer.parseInt(MPDScanMode) : 0;

            input = txtMPDMainBarcodeMANUALENTRY.getText().toString();

            if (!objGlobalMethods.IsEmptyString(input)) {
                inputManualEntry(input, MPDMainModeInt, MPDScanModeInt);
            } else {
                playTone();
                Context c = myApp.getAppContext();
                if (MPDMainModeInt == 0 || MPDMainModeInt == 1) {
                    objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.pls_enter_item_ref)).show();
                } else {
                    objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.pls_enter_job_ref)).show();
                }
            }

            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MPDMainActivity btnMPDMainBarcodeSubmitENTER_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnMPDMainEq_Click(View v) {

        String[] prefName = new String[]{"MPDMainMode", "MPDScanMode"};
        String[] prefvalue = new String[]{"1", "1"};
        objGlobalMethods.setMpdSPMass(prefName, prefvalue);
        txtMPDMainBarcodeMANUALENTRY.setHint(myApp.getAppContext().getString(R.string.lblequipmentref));
        toggle(1, 1);
    }

    public void btnMPDMainJob_Click(View v) {
        String[] prefName = new String[]{"MPDMainMode", "MPDScanMode"};
        String[] prefvalue = new String[]{"2", "1"};
        objGlobalMethods.setMpdSPMass(prefName, prefvalue);
        txtMPDMainBarcodeMANUALENTRY.setHint(myApp.getAppContext().getString(R.string.lbljobref));
        toggle(2, 1);
    }

    public void btnMPDMainCANCEL_Click(View v) {
        back();
    }

    public void btnMPDMainAdd_Click(View v) {
        /*String[] prefName = new String[]{"MPDScanMode", "cameraScannedCode", "QRScanRequestIntent"};
        String[] prefvalue = new String[]{"1", "", "MPDMainActivity"};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);*/
        String[] prefName = new String[]{"cameraScannedCode", "QRScanRequestIntent"};
        String[] prefvalue = new String[]{"", "MPDMainActivity"};
        objGlobalMethods.setGCMPrefsMass(null, prefName, prefvalue);
        objGlobalMethods.setMPDRelatedSP_KeyValue("MPDScanMode", "1");

        ctrlSettings.tottleButtonsWithStyles(true, btnMPDMainAdd);
        ctrlSettings.tottleButtonsWithStyles(false, btnMPDMainDel);
        //launchQRScan();
    }

    public void btnMPDMainDel_Click(View v) {
        /*String[] prefName = new String[]{"MPDScanMode", "cameraScannedCode", "QRScanRequestIntent"};
        String[] prefvalue = new String[]{"2", "", "MPDMainActivity"};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);*/
        String[] prefName = new String[]{"cameraScannedCode", "QRScanRequestIntent"};
        String[] prefvalue = new String[]{"", "MPDMainActivity"};
        objGlobalMethods.setGCMPrefsMass(null, prefName, prefvalue);
        objGlobalMethods.setMPDRelatedSP_KeyValue("MPDScanMode", "2");

        ctrlSettings.tottleButtonsWithStyles(false, btnMPDMainAdd);
        ctrlSettings.tottleButtonsWithStyles(true, btnMPDMainDel);
        //launchQRScan();
    }

    public void btnMPDMainScanMPD_Click(View v) {
        //MPDScanMode 0, scan mpd to get group
        /*String[] prefName = new String[]{"MPDScanMode", "cameraScannedCode", "QRScanRequestIntent"};
        String[] prefvalue = new String[]{"", "", "MPDMainActivity"};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);*/
        String[] prefName = new String[]{"cameraScannedCode", "QRScanRequestIntent"};
        String[] prefvalue = new String[]{"", "MPDMainActivity"};
        objGlobalMethods.setGCMPrefsMass(null, prefName, prefvalue);
        objGlobalMethods.setMPDRelatedSP_KeyValue("MPDScanMode", "");

        launchQRScan();
    }

    private void back() {
        Intent parentIntent = new Intent(this, DrawerMainActivity.class);
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(g);
        ishomesuser = UsernamePwd[3];

        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        parentIntent = new Intent(this, (Boolean.parseBoolean(ishomesuser)) ? DrawerMainActivity.class : !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);

        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    private void initPage(boolean IsHOMESuser, SharedPreferences g) {
        String MPDMainMode = "";
        String MPDScanMode = "";

        try {
            lblMPDMainSubmitIsHomesUser.setText(IsHOMESuser ? "true" : "false");
            MPDGroupIDX = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDGroupIDX");
            lblMPDMainMPDGroupIDX.setText(MPDGroupIDX);

            cameraScannedCode = objGlobalMethods.getGCMPrefs(g, "cameraScannedCode");
            if (!cameraScannedCode.isEmpty() && !cameraScannedCode.equals("") && cameraScannedCode != "") {
                MPDMainMode = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDMainMode");
                MPDScanMode = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDScanMode");
                inputManualEntry(cameraScannedCode,
                        !objGlobalMethods.IsEmptyString(MPDMainMode) ? Integer.parseInt(MPDMainMode) : 0,
                        !objGlobalMethods.IsEmptyString(MPDScanMode) ? Integer.parseInt(MPDScanMode) : 0);
            } else {
                loaddata(null, g);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MPDMainActivity initPage. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void resetPage() {
        try {
            lblMPDMainMPDGroupIDX.setText("");
            setTitle("MPD Group ?");
            MPD_ItemListAdapter = null;
            lvMPDMainEquipments.setAdapter(null);
            Job_ItemListAdapter = null;
            lvMPDMainJobs.setAdapter(null);
            txtMPDMainBarcodeMANUALENTRY.setHint(myApp.getAppContext().getString(R.string.lblequipmentref));

            /*SharedPreferences g = objGlobalMethods.getGcmPreferences();
            String[] prefName = new String[]{"MPDGroupIDX", "MPD_GetGroup_ObjStr", "MPDMainMode", "MPDScanMode"};
            String[] prefvalue = new String[]{"", "", "0", "1"};
            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);*/

            String[] prefName = new String[]{"MPDGroupIDX", "MPD_GetGroup_ObjStr", "MPDMainMode", "MPDScanMode"};
            String[] prefvalue = new String[]{"", "", "0", "1"};
            objGlobalMethods.setMpdSPMass(prefName, prefvalue);
            toggle(0,0);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MPDMainActivity resetPage. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void loaddata(MPD_GetGroup_Obj mgo, SharedPreferences g) {
        //MPDMainMode 0 blank, scan mpd to get group
        //MPDMainMode 1, equipment
        //MPDMainMode 2, jobs
        //MPDScanMode 0, scan mpd to get group
        //MPDScanMode 1, attach
        //MPDScanMode 2, detach
        Context ctx = myApp.getAppContext();
        MPD_Group mg = null;
        MPD_Group_Item mgi = null;
        String MPD_GetGroup_ObjStr = "";
        String MPDScanMode = "";
        int MPDScanModeInt = 0;
        String MPDMainMode = "";
        int MPDMainModeInt = 0;

        try {
            /*if (g == null)
                g = objGlobalMethods.getGcmPreferences();*/

            MPDGroupIDX = lblMPDMainMPDGroupIDX.getText().toString();
            setTitle("MPD Group " + (objGlobalMethods.IsEmptyString(MPDGroupIDX) ? "?" : MPDGroupIDX));

            MPDMainMode = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDMainMode");
            MPDScanMode = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDScanMode");
            MPDMainModeInt = objGlobalMethods.IsEmptyString(MPDMainMode) ? 0 : Integer.parseInt(MPDMainMode);
            MPDScanModeInt = objGlobalMethods.IsEmptyString(MPDScanMode) ? 0 : Integer.parseInt(MPDScanMode);

            if (mgo == null) {
                MPD_GetGroup_ObjStr = objGlobalMethods.getMPDRelatedSP_KeyValue("MPD_GetGroup_ObjStr");
                if (!objGlobalMethods.IsEmptyString(MPD_GetGroup_ObjStr))
                    mgo = new Gson().fromJson(MPD_GetGroup_ObjStr, MPD_GetGroup_Obj.class);
            }

            if (mgo != null) {
                MPD_ItemListAdapter = new MPDMainMPD_ItemListAdapter(this, (mgo.mpditemlist != null) ? mgo.mpditemlist : new ArrayList<MPD_Item>());
                lvMPDMainEquipments.setAdapter(MPD_ItemListAdapter);
                Job_ItemListAdapter = new MPDMainJob_ItemListAdapter(this, (mgo.refattlist != null) ? mgo.refattlist : new ArrayList<String>());
                lvMPDMainJobs.setAdapter(Job_ItemListAdapter);

                //txtMPDMainBarcodeMANUALENTRY.setHint(MPDMainModeInt <= 1 ? ctx.getString(R.string.lblequipmentid) : ctx.getString(R.string.lbljobref));
            } else {
                MPDMainModeInt = 0;
                MPD_ItemListAdapter = new MPDMainMPD_ItemListAdapter(this, new ArrayList<MPD_Item>());
                lvMPDMainEquipments.setAdapter(MPD_ItemListAdapter);
                Job_ItemListAdapter = new MPDMainJob_ItemListAdapter(this, new ArrayList<String>());
                lvMPDMainJobs.setAdapter(Job_ItemListAdapter);
            }

            txtMPDMainBarcodeMANUALENTRY.setHint(MPDMainModeInt <= 1 ? ctx.getString(R.string.lblequipmentref) : ctx.getString(R.string.lbljobref));
            toggle(MPDMainModeInt, MPDScanModeInt);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MPDMainActivity loaddata. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void toggle(int MPDMainMode, int MPDScanMode) {
        //MPDMainMode 0 blank, scan mpd to get group
        //MPDMainMode 1, equipment
        //MPDMainMode 2, jobs
        //MPDScanMode 0, scan mpd to get group
        //MPDScanMode 1, attach
        //MPDScanMode 2, detach
        Context c = myApp.getAppContext();
        try {
            String IsHOMESuserString = lblMPDMainSubmitIsHomesUser.getText().toString();
            boolean IsHOMESuser = IsHOMESuserString.equals("true") || IsHOMESuserString.equals("false") ? Boolean.parseBoolean(IsHOMESuserString) : false;

            LLMPDMainBlank.setVisibility(MPDMainMode == 0 ? View.VISIBLE : View.GONE);
            LLMPDMainShown.setVisibility(MPDMainMode > 0 ? View.VISIBLE : View.GONE);
            LLMPDMainEquipments.setVisibility(MPDMainMode == 1 && IsHOMESuser ? View.VISIBLE : View.GONE);
            LLMPDMainJobs.setVisibility(MPDMainMode == 2 ? View.VISIBLE : View.GONE);

            btnMPDMainScanMPD.setVisibility(MPDMainMode == 0 ? View.VISIBLE : View.GONE);
            LLmodeop.setVisibility(MPDMainMode == 0 ? View.GONE : View.VISIBLE);
            /*btnMPDMainAdd.setVisibility(MPDMainMode == 0 ? View.GONE : View.VISIBLE);
            btnMPDMainDel.setVisibility(MPDMainMode == 0 ? View.GONE : View.VISIBLE);*/

            if (!IsHOMESuser) {
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnMPDMainEq);
                btnMPDMainEq.setBackgroundResource(R.drawable.button_tabstyle_unselected);
                btnMPDMainEq.setTextColor(ContextCompat.getColor(c, R.color.White));
                btnMPDMainJob.setBackgroundResource(R.drawable.button_tabstyle_selected);
                btnMPDMainJob.setTextColor(ContextCompat.getColor(c, R.color.brightergreen));
            } else {
                /*ctrlSettings.tottleButtonsWithStyles(MPDMainMode == 1, btnMPDMainEq);
                ctrlSettings.tottleButtonsWithStyles(MPDMainMode == 2, btnMPDMainJob);*/
                btnMPDMainEq.setBackgroundResource((MPDMainMode == 1) ? R.drawable.button_tabstyle_selected : R.drawable.button_tabstyle_unselected);
                btnMPDMainEq.setTextColor((MPDMainMode == 1) ? ContextCompat.getColor(c, R.color.brightergreen) : ContextCompat.getColor(c, R.color.White));
                btnMPDMainJob.setBackgroundResource((MPDMainMode == 2) ? R.drawable.button_tabstyle_selected : R.drawable.button_tabstyle_unselected);
                btnMPDMainJob.setTextColor((MPDMainMode == 2) ? ContextCompat.getColor(c, R.color.brightergreen) : ContextCompat.getColor(c, R.color.White));
            }

            ctrlSettings.tottleButtonsWithStyles(MPDScanMode == 1, btnMPDMainAdd);
            ctrlSettings.tottleButtonsWithStyles(MPDScanMode == 2, btnMPDMainDel);


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MPDMainActivity toggle. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void inputManualEntry(String input, int aMPDMainMode, int aMPDScanMode) {
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        String IsHOMESuserString = "";
        boolean IsHOMESuser;
        try {
            objGlobalMethods.setGCMPrefs(g, "cameraScannedCode", "");
            IsHOMESuserString = lblMPDMainSubmitIsHomesUser.getText().toString();
            IsHOMESuser = IsHOMESuserString.equals("true") || IsHOMESuserString.equals("false") ? Boolean.parseBoolean(IsHOMESuserString) : false;

            if (aMPDMainMode == 0 && IsHOMESuser) {
                _MPD_GetGroup_ByBarcode mgb = new _MPD_GetGroup_ByBarcode(input, IsHOMESuser);
                mgb.execute();
            } else if (aMPDMainMode == 0 && !IsHOMESuser) {
                Context c = myApp.getAppContext();
                /*String mpdsPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "mpdsPrefStr");
                ArrayList<objMPD_Info> mpds = null;
                objMPD_Info mpd = null;
                boolean exist = false;
                if (!mpdsPrefStr.isEmpty() && !mpdsPrefStr.equals("") && mpdsPrefStr != "" && !mpdsPrefStr.equals("[]") && mpdsPrefStr != "[]") {
                    Gson gson = new Gson();
                    mpds = gson.fromJson(mpdsPrefStr, new TypeToken<List<objMPD_Info>>() {
                    }.getType());
                }*/

                ArrayList<objMPD_Info> mpds = null;
                objMPD_Info mpd = null;
                boolean exist = false;

                mpds = objGlobalMethods.getMPDsALfromPrefStr();

                if (mpds != null && mpds.size() > 0) {
                    for (int i = 0; i <= mpds.size() -1; i++) {
                        mpd = mpds.get(i);
                        if (mpd.Serial_Number.toUpperCase().equals(input)) {
                            exist =  true;
                            break;
                        }
                    }

                    if (exist) {
                        _MPD_GetGroup_ByBarcode mgb = new _MPD_GetGroup_ByBarcode(input, IsHOMESuser);
                        mgb.execute();
                    } else {
                        playTone();
                        focusTextbox();
                        objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), input + " " + c.getString(R.string.item_not_assigned_to_u_yet)).show();
                    }
                } else {
                    playTone();
                    focusTextbox();
                    objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), input + " " + c.getString(R.string.item_not_assigned_to_u_yet)).show();
                }
            } else if (aMPDMainMode == 1 && aMPDScanMode == 1) {
                //attach equip
                MPDGroupIDX = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDGroupIDX");
                String MPDMainUserIDXstr = lblMPDMainUserIDX.getText().toString();
                long MPDMainUserIDX = !objGlobalMethods.IsEmptyString(MPDMainUserIDXstr) ? Long.parseLong(MPDMainUserIDXstr) : 0;

                if (!objGlobalMethods.IsEmptyString(MPDGroupIDX)) {
                    AttachEq ae = new AttachEq(input, Long.parseLong(MPDGroupIDX), MPDMainUserIDX);
                    ae.execute();
                }
            } else if (aMPDMainMode == 1 && aMPDScanMode == 2) {
                //detach equip
                MPDGroupIDX = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDGroupIDX");
                String MPDMainUserIDXstr = lblMPDMainUserIDX.getText().toString();
                long MPDMainUserIDX = !objGlobalMethods.IsEmptyString(MPDMainUserIDXstr) ? Long.parseLong(MPDMainUserIDXstr) : 0;

                if (!objGlobalMethods.IsEmptyString(MPDGroupIDX)) {
                    DetachEq de = new DetachEq(input, Long.parseLong(MPDGroupIDX), MPDMainUserIDX);
                    de.execute();
                }
            } else if (aMPDMainMode == 2 && aMPDScanMode == 1) {
                //attach job
                MPDGroupIDX = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDGroupIDX");
                String MPDMainUserIDXstr = lblMPDMainUserIDX.getText().toString();
                String MPDMainDriverIDXstr = lblMPDMainDriverIDX.getText().toString();
                long MPDMainUserIDX = !objGlobalMethods.IsEmptyString(MPDMainUserIDXstr) ? Long.parseLong(MPDMainUserIDXstr) : 0;
                long MPDMainDriverIDX = !objGlobalMethods.IsEmptyString(MPDMainDriverIDXstr) ? Long.parseLong(MPDMainDriverIDXstr) : 0;

                if (!objGlobalMethods.IsEmptyString(MPDGroupIDX)) {
                    AttachJobs aj = new AttachJobs(input, Long.parseLong(MPDGroupIDX), MPDMainUserIDX, MPDMainDriverIDX);
                    aj.execute();
                }

            } else if (aMPDMainMode == 2 && aMPDScanMode == 2) {
                //detach job
                MPDGroupIDX = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDGroupIDX");
                String MPDMainUserIDXstr = lblMPDMainUserIDX.getText().toString();
                String MPDMainDriverIDXstr = lblMPDMainDriverIDX.getText().toString();
                long MPDMainUserIDX = !objGlobalMethods.IsEmptyString(MPDMainUserIDXstr) ? Long.parseLong(MPDMainUserIDXstr) : 0;
                long MPDMainDriverIDX = !objGlobalMethods.IsEmptyString(MPDMainDriverIDXstr) ? Long.parseLong(MPDMainDriverIDXstr) : 0;

                if (!objGlobalMethods.IsEmptyString(MPDGroupIDX)) {
                    DetachJobs dj = new DetachJobs(input, Long.parseLong(MPDGroupIDX), MPDMainUserIDX, MPDMainDriverIDX);
                    dj.execute();
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CageScanActivity inputManualEntry. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private class MPD_GetGroup_Obj {
        public MPD_Group mg;
        public ArrayList<MPD_Item> mpditemlist;
        public ArrayList<String> refattlist;
        public boolean MPD_Attach_Jobs_To_MPDGroupIDXResult;
        public boolean MPD_Detach_Jobs_From_MPDGroupIDXResult;
        public String MPD_AddGroupItemBySerialNumberResult;
        public String MPD_RemoveGroupItemBySerialNumberResult;
        public String errMsg;
        public boolean hasBTTL;
        public String BTTLMac;
        public long BTTLMPDIDX;

        public void setBTTLMPDIDX(long BTTLMPDIDX) {
            this.BTTLMPDIDX = BTTLMPDIDX;
        }

        public void setBTTLMac(String BTTLMac) {
            this.BTTLMac = BTTLMac;
        }

        public void setHasBTTL(boolean hasBTTL) {
            this.hasBTTL = hasBTTL;
        }

        public MPD_GetGroup_Obj() {
        }

        public MPD_GetGroup_Obj(MPD_Group amg, ArrayList<MPD_Item> ampditemlist, ArrayList<String> arefattlist) {
            this.mg = amg;
            this.mpditemlist = ampditemlist;
            this.refattlist = arefattlist;
        }

        public void setMPD_Attach_Jobs_To_MPDGroupIDXResult(boolean MPD_Attach_Jobs_To_MPDGroupIDXResult) {
            this.MPD_Attach_Jobs_To_MPDGroupIDXResult = MPD_Attach_Jobs_To_MPDGroupIDXResult;
        }

        public void setMPD_Detach_Jobs_From_MPDGroupIDXResult(boolean MPD_Detach_Jobs_From_MPDGroupIDXResult) {
            this.MPD_Detach_Jobs_From_MPDGroupIDXResult = MPD_Detach_Jobs_From_MPDGroupIDXResult;
        }

        public void setMPD_AddGroupItemBySerialNumberResult(String MPD_AddGroupItemBySerialNumberResult) {
            this.MPD_AddGroupItemBySerialNumberResult = MPD_AddGroupItemBySerialNumberResult;
        }

        public void setMPD_RemoveGroupItemBySerialNumberResult(String MPD_RemoveGroupItemBySerialNumberResult) {
            this.MPD_RemoveGroupItemBySerialNumberResult = MPD_RemoveGroupItemBySerialNumberResult;
        }

        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
        }
    }

    public class _MPD_GetGroup_ByBarcode extends AsyncTask<Void, Void, MPD_GetGroup_Obj> {
        private final String AMPDItem_Barcode;
        private final boolean isHOMESuser;

        public _MPD_GetGroup_ByBarcode(String aAMPDItem_Barcode, boolean aisHOMESuser) {
            this.AMPDItem_Barcode = aAMPDItem_Barcode;
            this.isHOMESuser = aisHOMESuser;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            showPD();
        }

        @Override
        protected MPD_GetGroup_Obj doInBackground(Void... params) {
            MPD_GetGroup_Obj mgo = null;
            MPD_Group mg = null;
            MPD_Group_Item mgi = null;
            Context ctx = myApp.getAppContext();
            xwsBase xb = null;
            boolean hasBTTL = false;
            String BTTLMac = "";
            long BTTLMPDIDX = 0;
            try {
                String HOMESAPI = ctx.getString(R.string.action_HOMESAPI);
                mg = WebService.MPD_GetGroup_ByBarcode(HOMESAPI, AMPDItem_Barcode);
                if (mg != null) {
                    mpditemlist = null;
                    if (mg.Items != null && mg.Items.length > 0) {
                        for (int i = 0; i <= mg.Items.length - 1; i++) {
                            mgi = mg.Items[i];
                            if (mgi != null && mgi.MPD_Item != null) {
                                if (mpditemlist == null)
                                    mpditemlist = new ArrayList<>();
                                mpditemlist.add(mgi.MPD_Item);
                                if (!hasBTTL && mgi.MPD_Item.Device_Type == 22)
                                    hasBTTL = true;

                                if (hasBTTL)
                                    BTTLMac = mgi.MPD_Item.Device_Identifier;

                                if (hasBTTL)
                                    BTTLMPDIDX = mgi.MPD_Item.IDX;
                            }
                        }
                    }

                    refattlist = null;
                    if (mg.References_Attached != null && mg.References_Attached.length > 0) {
                        for (int i = 0; i <= mg.References_Attached.length - 1; i++) {
                            if (refattlist == null)
                                refattlist = new ArrayList<>();
                            refattlist.add(mg.References_Attached[i]);
                        }
                    }

                    mgo = new MPD_GetGroup_Obj(mg, mpditemlist, refattlist);
                    mgo.setHasBTTL(hasBTTL);
                    mgo.setBTTLMac(BTTLMac);
                    mgo.setBTTLMPDIDX(BTTLMPDIDX);
                    Long MPDGroupIDX = mg.IDX;
                    String MPD_GetGroup_ObjStr = new Gson().toJson(mgo);

                    //SharedPreferences g = objGlobalMethods.getGcmPreferences();
                    String[] prefName = new String[]{"MPDGroupIDX", "MPD_GetGroup_ObjStr", "MPDMainMode", "MPDScanMode"};
                    String[] prefvalue = new String[]{String.valueOf(MPDGroupIDX), MPD_GetGroup_ObjStr, isHOMESuser ? "1" : "2", "1"};
                    ////String[] prefvalue = new String[]{String.valueOf(MPDGroupIDX), MPD_GetGroup_ObjStr, "2", "1"}; //purposely make MPDMainMode 2 as now only allow attach detach job
                    //objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                    objGlobalMethods.setMpdSPMass(prefName, prefvalue);
                } else {
                    String[] prefName = new String[]{"MPDMainMode"};
                    String[] prefvalue = new String[]{""};
                    //objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);
                    objGlobalMethods.setMpdSPMass(prefName, prefvalue);
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at _MPD_GetGroup_ByBarcode doInBackground. Exception: " + s, "", "", "", "");
            } finally {
                return mgo;
            }
        }

        @Override
        protected void onPostExecute(final MPD_GetGroup_Obj mgo) {
            Context ctx = myApp.getAppContext();
            try {
                dismissPD();
                if (mgo == null) {
                    playTone();
                    focusTextbox();
                    if (isHOMESuser) {
                        showCreateGroup(AMPDItem_Barcode).show();
                    } else {
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.no_grp_record)).show();
                    }
                } else {
                    playSuccessVerifiedTone();
                    focusTextbox();
                    lblMPDMainMPDGroupIDX.setText(String.valueOf(mgo.mg.IDX));
                    loaddata(mgo, null);

                    if (mgo.hasBTTL && !objGlobalMethods.IsEmptyString(mgo.BTTLMac)) {
                        String ARegID = null;
                        bttemploggerStandaloneclass btc = new bttemploggerStandaloneclass();
                        SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                        String bttemploggerStandaloneclassStarted = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "bttemploggerStandaloneclassStarted");

                        if (!objGlobalMethods.IsEmptyString(bttemploggerStandaloneclassStarted)) {
                            objGlobalMethods.showDialog(myApp.currentActivity, "OK", "Please wait for 1 minute.").show();
                        } else {

                            btc.setTempcheckforattachment(true);
                            btc.setRequestedBTTLMac(mgo.BTTLMac);
                            btc.setARegID(ARegID);
                            btc.scan();
                        }
                    }
                }
            } catch (Exception e) {
                String s = e.getMessage();
                objGlobalMethods.writeToFile("Exception at MPDMainActivity _MPD_GetGroup_ByBarcode onPostExecute. Exception: " + s, "", "", "", "");
                playTone();
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
            }
        }
    }

    public void showReconditionMsg(double LiveTemp) {
        try {
            if (LiveTemp < 2 || LiveTemp > 8) {
                playTone();
                resetPage();
                Context c = myApp.getAppContext();
                objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.bttl_livetemp_non_compliant)).show();
            } else {
                //acceptable range, do nothing
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at MPDMainActivity showReconditionMsg onPostExecute. Exception: " + s, "", "", "", "");
        }
    }

    public Dialog showCreateGroup(final String input) {
        Context ctx = myApp.getAppContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setTitle("");
        builder.setCancelable(false);
        builder.setMessage(ctx.getString(R.string.item_no_grp));

        builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
                String MPDMainUserIDXstr = lblMPDMainUserIDX.getText().toString();
                String MPDMainDriverIDXstr = lblMPDMainDriverIDX.getText().toString();
                long MPDMainUserIDX = !objGlobalMethods.IsEmptyString(MPDMainUserIDXstr) ? Long.parseLong(MPDMainUserIDXstr) : 0;
                AttachEq ae = new AttachEq(input, 0, MPDMainUserIDX);
                ae.execute();
            }
        });

        builder.setNeutralButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        AlertDialog alertdialog = builder.create();
        return alertdialog;
    }

    public class AttachJobs extends AsyncTask<Void, Void, MPD_GetGroup_Obj> {
        private final String AJobReferences;
        private final long AGroupIDX;
        private final long AUserIDX;
        private final long ADriverIDX;

        public AttachJobs(String aAJobReferences, long aAGroupIDX, long aAUserIDX, long aADriverIDX) {
            this.AJobReferences = aAJobReferences;
            this.AGroupIDX = aAGroupIDX;
            this.AUserIDX = aAUserIDX;
            this.ADriverIDX = aADriverIDX;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            showPD();
        }

        @Override
        protected MPD_GetGroup_Obj doInBackground(Void... params) {
            MPD_Attach_Jobs_To_MPDGroupIDXResult res = null;
            Context ctx = myApp.getAppContext();
            MPD_GetGroup_Obj mgo = null;
            MPD_Group mg = null;
            MPD_Group_Item mgi = null;
            String HOMESAPI = ctx.getString(R.string.action_HOMESAPI);
            try {
                res = WebService.MPD_Attach_Jobs_To_MPDGroupIDX(HOMESAPI, AJobReferences, AGroupIDX, AUserIDX, ADriverIDX);

                mg = WebService.MPD_GetGroup(HOMESAPI, AGroupIDX);
                if (mg != null) {
                    mpditemlist = null;
                    if (mg.Items != null && mg.Items.length > 0) {
                        for (int i = 0; i <= mg.Items.length - 1; i++) {
                            mgi = mg.Items[i];
                            if (mgi != null && mgi.MPD_Item != null) {
                                if (mpditemlist == null)
                                    mpditemlist = new ArrayList<>();
                                mpditemlist.add(mgi.MPD_Item);
                            }
                        }
                    }

                    refattlist = null;
                    if (mg.References_Attached != null && mg.References_Attached.length > 0) {
                        for (int i = 0; i <= mg.References_Attached.length - 1; i++) {
                            if (refattlist == null)
                                refattlist = new ArrayList<>();
                            refattlist.add(mg.References_Attached[i]);
                        }
                    }

                    mgo = new MPD_GetGroup_Obj(mg, mpditemlist, refattlist);
                    Long MPDGroupIDX = mg.IDX;
                    String MPD_GetGroup_ObjStr = new Gson().toJson(mgo);

                    //SharedPreferences g = objGlobalMethods.getGcmPreferences();
                    String[] prefName = new String[]{"MPDGroupIDX", "MPD_GetGroup_ObjStr"};
                    String[] prefvalue = new String[]{String.valueOf(MPDGroupIDX), MPD_GetGroup_ObjStr};
                    //objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                    objGlobalMethods.setMpdSPMass(prefName, prefvalue);
                }
                if (mgo != null) {
                    mgo.setMPD_Attach_Jobs_To_MPDGroupIDXResult(res != null && res.Status == 0);
                    mgo.setErrMsg((res != null && !objGlobalMethods.IsEmptyString(res.Message)) ? res.Message : "");
                } else {
                    mgo = new MPD_GetGroup_Obj();
                    mgo.setMPD_Attach_Jobs_To_MPDGroupIDXResult(false);
                    mgo.setErrMsg((res != null && !objGlobalMethods.IsEmptyString(res.Message)) ? res.Message : "");
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MPDMainActivity AttachJobs doInBackground. Exception: " + s, "", "", "", "");
            } finally {
                return mgo;
            }
        }

        @Override
        protected void onPostExecute(final MPD_GetGroup_Obj mgo) {
            Context ctx = myApp.getAppContext();
            try {
                dismissPD();
                if (mgo == null) {
                    playTone();
                    focusTextbox();
                    lbljoblog.setText(ctx.getString(R.string.alert_noRetrieveDataFail));
                } else if (mgo.MPD_Attach_Jobs_To_MPDGroupIDXResult) {
                    objGlobalMethods.writeToFile(AJobReferences + " added to GroupIDX: " + AGroupIDX,"","","","");
                    playSuccessVerifiedTone();
                    focusTextbox();
                    lbljoblog.setText(mgo.errMsg);
                    loaddata(mgo, null);
                } else {
                    objGlobalMethods.writeToFile("Failure to add " + AJobReferences + " to GroupIDX: " + AGroupIDX,"","","","");
                    playTone();
                    focusTextbox();
                    lbljoblog.setText(mgo.errMsg);
                    loaddata(mgo, null);
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MPDMainActivity AttachJobs onPostExecute. Exception: " + s, "", "", "", "");
                playTone();
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
            }
        }
    }

    public class DetachJobs extends AsyncTask<Void, Void, MPD_GetGroup_Obj> {
        private final String AJobReferences;
        private final long AGroupIDX;
        private final long AUserIDX;
        private final long ADriverIDX;

        public DetachJobs(String aAJobReferences, long aAGroupIDX, long aAUserIDX, long aADriverIDX) {
            this.AJobReferences = aAJobReferences;
            this.AGroupIDX = aAGroupIDX;
            this.AUserIDX = aAUserIDX;
            this.ADriverIDX = aADriverIDX;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            showPD();
        }

        @Override
        protected MPD_GetGroup_Obj doInBackground(Void... params) {
            MPD_Detach_Jobs_From_MPDGroupIDXResult res = null;
            Context ctx = myApp.getAppContext();
            MPD_GetGroup_Obj mgo = null;
            MPD_Group mg = null;
            MPD_Group_Item mgi = null;
            String HOMESAPI = ctx.getString(R.string.action_HOMESAPI);
            try {
                res = WebService.MPD_Detach_Jobs_From_MPDGroupIDX(HOMESAPI, AJobReferences, AGroupIDX, AUserIDX, ADriverIDX);

                mg = WebService.MPD_GetGroup(HOMESAPI, AGroupIDX);
                if (mg != null) {
                    mpditemlist = null;
                    if (mg.Items != null && mg.Items.length > 0) {
                        for (int i = 0; i <= mg.Items.length - 1; i++) {
                            mgi = mg.Items[i];
                            if (mgi != null && mgi.MPD_Item != null) {
                                if (mpditemlist == null)
                                    mpditemlist = new ArrayList<>();
                                mpditemlist.add(mgi.MPD_Item);
                            }
                        }
                    }

                    refattlist = null;
                    if (mg.References_Attached != null && mg.References_Attached.length > 0) {
                        for (int i = 0; i <= mg.References_Attached.length - 1; i++) {
                            if (refattlist == null)
                                refattlist = new ArrayList<>();
                            refattlist.add(mg.References_Attached[i]);
                        }
                    }

                    mgo = new MPD_GetGroup_Obj(mg, mpditemlist, refattlist);
                    Long MPDGroupIDX = mg.IDX;
                    String MPD_GetGroup_ObjStr = new Gson().toJson(mgo);

                    //SharedPreferences g = objGlobalMethods.getGcmPreferences();
                    String[] prefName = new String[]{"MPDGroupIDX", "MPD_GetGroup_ObjStr"};
                    String[] prefvalue = new String[]{String.valueOf(MPDGroupIDX), MPD_GetGroup_ObjStr};
                    //objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                    objGlobalMethods.setMpdSPMass(prefName, prefvalue);
                }
                if (mgo != null) {
                    mgo.setMPD_Detach_Jobs_From_MPDGroupIDXResult(res != null && res.Status == 0);
                    mgo.setErrMsg((res != null && !objGlobalMethods.IsEmptyString(res.Message)) ? res.Message : "");
                } else {
                    mgo = new MPD_GetGroup_Obj();
                    mgo.setMPD_Detach_Jobs_From_MPDGroupIDXResult(false);
                    mgo.setErrMsg((res != null && !objGlobalMethods.IsEmptyString(res.Message)) ? res.Message : "");
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MPDMainActivity DetachJobs doInBackground. Exception: " + s, "", "", "", "");
            } finally {
                return mgo;
            }
        }

        @Override
        protected void onPostExecute(final MPD_GetGroup_Obj mgo) {
            Context ctx = myApp.getAppContext();
            try {
                dismissPD();
                if (mgo == null) {
                    playTone();
                    focusTextbox();
                    lbljoblog.setText(ctx.getString(R.string.alert_noRetrieveDataFail));
                } else if (mgo.MPD_Detach_Jobs_From_MPDGroupIDXResult) {
                    playSuccessVerifiedTone();
                    focusTextbox();
                    lbljoblog.setText(mgo.errMsg);
                    loaddata(mgo, null);
                } else {
                    playTone();
                    focusTextbox();
                    lbljoblog.setText(mgo.errMsg);
                    loaddata(mgo, null);
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MPDMainActivity DetachJobs onPostExecute. Exception: " + s, "", "", "", "");
                playTone();
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
            }
        }

    }

    public class AttachEq extends AsyncTask<Void, Void, MPD_GetGroup_Obj> {
        private final String SerialNumbers;
        private final long AGroupIDX;
        private final long AUserIDX;

        public AttachEq(String aSerialNumbers, long aAGroupIDX, long aAUserIDX) {
            this.SerialNumbers = aSerialNumbers;
            this.AGroupIDX = aAGroupIDX;
            this.AUserIDX = aAUserIDX;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            showPD();
        }

        @Override
        protected MPD_GetGroup_Obj doInBackground(Void... params) {
            String res = null;
            Context ctx = myApp.getAppContext();
            MPD_GetGroup_Obj mgo = null;
            MPD_Group mg = null;
            MPD_Group_Item mgi = null;
            String HOMESAPI = ctx.getString(R.string.action_HOMESAPI);
            try {
                res = WebService.MPD_AddGroupItemBySerialNumber(HOMESAPI, AGroupIDX, AUserIDX, SerialNumbers);

                if (AGroupIDX == 0 && res != null && res.toUpperCase().equals("OK")) {
                    mg = WebService.MPD_GetGroup_ByBarcode(HOMESAPI, SerialNumbers);
                } else if (AGroupIDX > 0) {
                    mg = WebService.MPD_GetGroup(HOMESAPI, AGroupIDX);
                }

                if (mg != null) {
                    mpditemlist = null;
                    if (mg.Items != null && mg.Items.length > 0) {
                        for (int i = 0; i <= mg.Items.length - 1; i++) {
                            mgi = mg.Items[i];
                            if (mgi != null && mgi.MPD_Item != null) {
                                if (mpditemlist == null)
                                    mpditemlist = new ArrayList<>();
                                mpditemlist.add(mgi.MPD_Item);
                            }
                        }
                    }

                    refattlist = null;
                    if (mg.References_Attached != null && mg.References_Attached.length > 0) {
                        for (int i = 0; i <= mg.References_Attached.length - 1; i++) {
                            if (refattlist == null)
                                refattlist = new ArrayList<>();
                            refattlist.add(mg.References_Attached[i]);
                        }
                    }

                    mgo = new MPD_GetGroup_Obj(mg, mpditemlist, refattlist);
                    Long MPDGroupIDX = mg.IDX;
                    String MPD_GetGroup_ObjStr = new Gson().toJson(mgo);

                    //SharedPreferences g = objGlobalMethods.getGcmPreferences();
                    if (AGroupIDX == 0) {
                        String[] prefName = new String[]{"MPDGroupIDX", "MPD_GetGroup_ObjStr", "MPDMainMode", "MPDScanMode"};
                        String[] prefvalue = new String[]{String.valueOf(MPDGroupIDX), MPD_GetGroup_ObjStr, "1", "1"};
                        //objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                        objGlobalMethods.setMpdSPMass(prefName, prefvalue);
                    } else {
                        String[] prefName = new String[]{"MPDGroupIDX", "MPD_GetGroup_ObjStr"};
                        String[] prefvalue = new String[]{String.valueOf(MPDGroupIDX), MPD_GetGroup_ObjStr};
                        //objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                        objGlobalMethods.setMpdSPMass(prefName, prefvalue);
                    }
                }
                if (mgo != null) {
                    mgo.setMPD_AddGroupItemBySerialNumberResult(!objGlobalMethods.IsEmptyString(res) ? res : "");
                    mgo.setErrMsg(!objGlobalMethods.IsEmptyString(res) ? res : ctx.getString(R.string.unableprocess));
                } else {
                    mgo = new MPD_GetGroup_Obj();
                    mgo.setMPD_AddGroupItemBySerialNumberResult("FALSE");
                    mgo.setErrMsg(!objGlobalMethods.IsEmptyString(res) ? res : ctx.getString(R.string.unableprocess));
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MPDMainActivity AttachEq doInBackground. Exception: " + s, "", "", "", "");
            } finally {
                return mgo;
            }
        }

        @Override
        protected void onPostExecute(final MPD_GetGroup_Obj mgo) {
            Context ctx = myApp.getAppContext();
            try {
                dismissPD();
                if (mgo == null) {
                    playTone();
                    focusTextbox();
                    lbleqlog.setText(ctx.getString(R.string.alert_errorperformoperation));
                } else if (!objGlobalMethods.IsEmptyString(mgo.MPD_AddGroupItemBySerialNumberResult) && mgo.MPD_AddGroupItemBySerialNumberResult.toUpperCase().equals("OK")) {
                    playSuccessVerifiedTone();
                    focusTextbox();
                    lbleqlog.setText("");
                    loaddata(mgo, null);
                } else {
                    playTone();
                    focusTextbox();
                    lbleqlog.setText(mgo.errMsg);
                    loaddata(mgo, null);
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MPDMainActivity AttachEq onPostExecute. Exception: " + s, "", "", "", "");
                playTone();
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
            }
        }
    }

    public class DetachEq extends AsyncTask<Void, Void, MPD_GetGroup_Obj> {
        private final String SerialNumbers;
        private final long AGroupIDX;
        private final long AUserIDX;

        public DetachEq(String aSerialNumbers, long aAGroupIDX, long aAUserIDX) {
            this.SerialNumbers = aSerialNumbers;
            this.AGroupIDX = aAGroupIDX;
            this.AUserIDX = aAUserIDX;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            showPD();
        }

        @Override
        protected MPD_GetGroup_Obj doInBackground(Void... params) {
            String res = null;
            Context ctx = myApp.getAppContext();
            MPD_GetGroup_Obj mgo = null;
            MPD_Group mg = null;
            MPD_Group_Item mgi = null;
            String HOMESAPI = ctx.getString(R.string.action_HOMESAPI);
            try {
                res = WebService.MPD_RemoveGroupItemBySerialNumber(HOMESAPI, AGroupIDX, AUserIDX, SerialNumbers);

                mg = WebService.MPD_GetGroup(HOMESAPI, AGroupIDX);
                if (mg != null) {
                    mpditemlist = null;
                    if (mg.Items != null && mg.Items.length > 0) {
                        for (int i = 0; i <= mg.Items.length - 1; i++) {
                            mgi = mg.Items[i];
                            if (mgi != null && mgi.MPD_Item != null) {
                                if (mpditemlist == null)
                                    mpditemlist = new ArrayList<>();
                                mpditemlist.add(mgi.MPD_Item);
                            }
                        }
                    }

                    refattlist = null;
                    if (mg.References_Attached != null && mg.References_Attached.length > 0) {
                        for (int i = 0; i <= mg.References_Attached.length - 1; i++) {
                            if (refattlist == null)
                                refattlist = new ArrayList<>();
                            refattlist.add(mg.References_Attached[i]);
                        }
                    }

                    mgo = new MPD_GetGroup_Obj(mg, mpditemlist, refattlist);
                    Long MPDGroupIDX = mg.IDX;
                    String MPD_GetGroup_ObjStr = new Gson().toJson(mgo);

                    //SharedPreferences g = objGlobalMethods.getGcmPreferences();
                    String[] prefName = new String[]{"MPDGroupIDX", "MPD_GetGroup_ObjStr"};
                    String[] prefvalue = new String[]{String.valueOf(MPDGroupIDX), MPD_GetGroup_ObjStr};
                    //objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                    objGlobalMethods.setMpdSPMass(prefName, prefvalue);
                }
                if (mgo != null) {
                    mgo.setMPD_RemoveGroupItemBySerialNumberResult(!objGlobalMethods.IsEmptyString(res) ? res : "");
                    mgo.setErrMsg(!objGlobalMethods.IsEmptyString(res) ? res : ctx.getString(R.string.unableprocess));
                } else {
                    mgo = new MPD_GetGroup_Obj();
                    mgo.setMPD_RemoveGroupItemBySerialNumberResult("FALSE");
                    mgo.setErrMsg(!objGlobalMethods.IsEmptyString(res) ? res : ctx.getString(R.string.unableprocess));
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MPDMainActivity DetachEq doInBackground. Exception: " + s, "", "", "", "");
            } finally {
                return mgo;
            }
        }

        @Override
        protected void onPostExecute(final MPD_GetGroup_Obj mgo) {
            Context ctx = myApp.getAppContext();
            try {
                dismissPD();
                if (mgo == null) {
                    playTone();
                    focusTextbox();
                    lbleqlog.setText(ctx.getString(R.string.alert_errorperformoperation));
                } else if (!objGlobalMethods.IsEmptyString(mgo.MPD_RemoveGroupItemBySerialNumberResult) && mgo.MPD_RemoveGroupItemBySerialNumberResult.toUpperCase().substring(0,2).equals("OK")) {
                    playSuccessVerifiedTone();
                    focusTextbox();
                    lbleqlog.setText(mgo.MPD_RemoveGroupItemBySerialNumberResult.substring(5));
                    loaddata(mgo, null);
                } else {
                    playTone();
                    focusTextbox();
                    lbleqlog.setText(mgo.errMsg);
                    loaddata(mgo, null);
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MPDMainActivity DetachEq onPostExecute. Exception: " + s, "", "", "", "");
                playTone();
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
            }
        }

    }

    public void focusTextbox() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                txtMPDMainBarcodeMANUALENTRY.setText("");
                txtMPDMainBarcodeMANUALENTRY.requestFocus();
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

    private void showChangeAirplaneModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetSubmitActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPStoHighAccModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetSubmitActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangePermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpspermission));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
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

    public void showPD() {
        try {
            pd = new ProgressBarDialog(myApp.currentActivity);
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
            pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));
            pd.show();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MPDMainActivity showPD. Exception: " + s, "", "", "", "");
        }
    }

    public void dismissPD() {
        try {
            if (pd != null)
                pd.dismiss();
            pd = null;
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MPDMainActivity dismissPD. Exception: " + s, "", "", "", "");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at MPDMainActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MPDMainActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }



}