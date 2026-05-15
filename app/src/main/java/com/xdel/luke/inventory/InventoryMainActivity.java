package com.xdel.luke.inventory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;

import android.app.AlertDialog;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Batch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.BottomSheet.InventoryLogginDialog;
import com.xdel.luke.BottomSheet.extDialog;
import com.xdel.luke.BottomSheet.promiseOptDialog;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.Label_Tagging_Activity;
import com.xdel.luke.MainActivity;
import com.xdel.luke.MapsActivity;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.mpd.MPDMainActivity;
import com.xdel.luke.mpd.MPDMainMPD_ItemListAdapter;
import com.xdel.luke.mpd.MPD_Item;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objDOObject;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objMPD_Info;
import com.xdel.luke.objClasses.objUpdateCOD_PostData;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class InventoryMainActivity extends AppCompatActivity {
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

    private TextView lblIsDefaultVRPpage, lblBatch_ID, lblInvMainSubmitIsHomesUser, lblInvMainUserIDX,
            lblInvMainDriverIDX, lblInvMainTallylog, lblInvMainBallog, lblInvMainTallyStockCode, lblInvMainTallyQty, lblInvMainTallyQtyVBal, lblInvMainTallyQtyBal, lblMainBlankLog;
    private EditText txtInvMainBarcodeMANUALENTRY, txtInvMainTallyQty;
    private Button btnInvMainBarcodeSubmitENTER, btnInvVerify, btnInvBal, btnInvMainCANCEL, btnInvMainTallyQtyUndo, btnInvMainTallyQtyAdd, btnInvMainTallyQtyReset, btnInvMainSummary;

    private LinearLayout LLInvMainBlank, LLInvMainShown, LLInvMainTally, LLInvMainBal, LLInvMainTallyQty, LLInvMainTallySR;

    ListView lvInvMainBal, lvInvMainTallySR, lvInvMainBatchID;

    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    private ProgressDialog pd, pdCheck;

    private Inventory_Adapter adapter;
    private StockRef_Adapter adapter_sr;
    private Inventory_BatchID_Adapter adapter_bi;
    private Menu menu;

    String Batch_ID = "";
    private AlertDialog promptQtyOrRefTallyAlert = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
        setContentView(R.layout.activity_inventory_main);

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);
        lblBatch_ID = findViewById(R.id.lblBatch_ID);
        lblInvMainSubmitIsHomesUser = findViewById(R.id.lblInvMainSubmitIsHomesUser);
        lblInvMainUserIDX = findViewById(R.id.lblInvMainUserIDX);
        lblInvMainDriverIDX = findViewById(R.id.lblInvMainDriverIDX);
        lblInvMainTallyStockCode = findViewById(R.id.lblInvMainTallyStockCode);
        lblInvMainTallyQty = findViewById(R.id.lblInvMainTallyQty);
        lblInvMainTallylog = findViewById(R.id.lblInvMainTallylog);
        lblInvMainTallyQtyVBal = findViewById(R.id.lblInvMainTallyQtyVBal);
        lblInvMainTallyQtyBal = findViewById(R.id.lblInvMainTallyQtyBal);
        lblMainBlankLog = findViewById(R.id.lblMainBlankLog);
        //lblInvMainBallog = findViewById(R.id.lblInvMainBallog);

        lvInvMainBal = findViewById(R.id.lvInvMainBal);
        lvInvMainTallySR = findViewById(R.id.lvInvMainTallySR);
        lvInvMainBatchID = findViewById(R.id.lvInvMainBatchID);

        txtInvMainBarcodeMANUALENTRY = findViewById(R.id.txtInvMainBarcodeMANUALENTRY);
        txtInvMainTallyQty = findViewById(R.id.txtInvMainTallyQty);

        btnInvMainBarcodeSubmitENTER = findViewById(R.id.btnInvMainBarcodeSubmitENTER);
        btnInvVerify = findViewById(R.id.btnInvVerify);
        btnInvBal = findViewById(R.id.btnInvBal);
        btnInvMainCANCEL = findViewById(R.id.btnInvMainCANCEL);
        btnInvMainSummary = findViewById(R.id.btnInvMainSummary);

        btnInvMainTallyQtyUndo = findViewById(R.id.btnInvMainTallyQtyUndo);
        btnInvMainTallyQtyAdd = findViewById(R.id.btnInvMainTallyQtyAdd);
        btnInvMainTallyQtyReset = findViewById(R.id.btnInvMainTallyQtyReset);

        LLInvMainBlank = findViewById(R.id.LLInvMainBlank);
        LLInvMainShown = findViewById(R.id.LLInvMainShown);
        LLInvMainTally = findViewById(R.id.LLInvMainTally);
        LLInvMainBal = findViewById(R.id.LLInvMainBal);
        LLInvMainTallyQty = findViewById(R.id.LLInvMainTallyQty);
        LLInvMainTallySR = findViewById(R.id.LLInvMainTallySR);


        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));

        pdCheck = new ProgressDialog(this);
        pdCheck.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdCheck.setCanceledOnTouchOutside(false);
        pdCheck.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

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
            setTitle(context.getString(R.string.tally_shipment));

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            Boolean IsHOMESuser = objGlobalMethods.IsHomesUserString(ishomesuser, GcmPreferences);
            lblInvMainUserIDX.setText(IsHOMESuser ? useridx : "");
            lblInvMainDriverIDX.setText(!IsHOMESuser ? useridx : "");

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
                objGlobalMethods.writeToFile("InventoryMainActivity !BTEnabled", "", "", "", "");
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

            cd = new ConnectionDetector(null);

            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                //Toast.makeText(context, "Please connect to working Internet connection.", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
            }

            objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
            initPage(IsHOMESuser, GcmPreferences);
        }
    }

    private void showmenu(boolean show) {
        try {
            if (this.menu != null) {
                this.menu.findItem(R.id.switchmode).setVisible(false);
                this.menu.findItem(R.id.logs).setVisible(show);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity showmenu. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_inventory_main, menu);

        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        String InvMainMode = objGlobalMethods.getGCMPrefs(g, "InvMainMode");
        int InvMainModeInt = objGlobalMethods.IsEmptyString(InvMainMode) ? 0 : Integer.parseInt(InvMainMode);
        showmenu(InvMainModeInt > 0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.switchmode) {
            switchToBatchIDScanMode();
        } else if (id == R.id.logs) {
            InventoryLogginDialog ild = new InventoryLogginDialog();
            ild.show(getSupportFragmentManager(), "inventorylog");
        }

        /*Intent parentIntent;
        switch (item.getItemId()) {
            case R.id.switchmode:
                switchToBatchIDScanMode();
                break;
            case R.id.logs:
                InventoryLogginDialog ild = new InventoryLogginDialog();
                ild.show(getSupportFragmentManager(), "inventorylog");
                break;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GcmPreferences = null;
        UpdateJobInfoPreferences = null;
        try {
            if (promptQtyOrRefTallyAlert != null && promptQtyOrRefTallyAlert.isShowing()) {
                promptQtyOrRefTallyAlert.dismiss();
                promptQtyOrRefTallyAlert = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity onDestroy set promptQtyOrRefTallyAlert null. Exception: " + e.getMessage(), "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity = null;

        try {
            if (promptQtyOrRefTallyAlert != null && promptQtyOrRefTallyAlert.isShowing()) {
                promptQtyOrRefTallyAlert.dismiss();
                promptQtyOrRefTallyAlert = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity onPause set promptQtyOrRefTallyAlert null. Exception: " + e.getMessage(), "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
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
        String[] prefvalue = new String[]{"", "InventoryMainActivity"};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        parentIntent = new Intent(InventoryMainActivity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        String InvMainMode = objGlobalMethods.getGCMPrefs(g, "InvMainMode");
        int InvMainModeInt = objGlobalMethods.IsEmptyString(InvMainMode) ? 0 : Integer.parseInt(InvMainMode);
        if (InvMainModeInt > 0) {
            switchToBatchIDScanMode();
        } else {
            back();
        }
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
        String InvMainMode = "";

        try {
            lblInvMainSubmitIsHomesUser.setText(IsHOMESuser ? "true" : "false");
            Batch_ID = objGlobalMethods.getGCMPrefs(g, "Batch_ID");
            lblBatch_ID.setText(!objGlobalMethods.IsEmptyString(Batch_ID) ? Batch_ID : "");

            ctrlSettings.enableDisableSubmitButtonWithStyles(!objGlobalMethods.IsEmptyString(Batch_ID), btnInvMainSummary);

            cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
            if (!cameraScannedCode.isEmpty() && !cameraScannedCode.equals("") && cameraScannedCode != "") {
                InvMainMode = objGlobalMethods.getGCMPrefs(GcmPreferences, "InvMainMode");
                inputManualEntry(cameraScannedCode,
                        !objGlobalMethods.IsEmptyString(InvMainMode) ? Integer.parseInt(InvMainMode) : 0);
            } else {
                loaddata(null, g);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity initPage. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void switchToBatchIDScanMode() {
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        try {
            showmenu(false);

            String[] prefName = new String[]{"Batch_ID", "InventoryBatchIDStr", "InvMainMode", "InvSubMode", "StockCodeMode"};
            String[] prefvalue = new String[]{"", "", "0", "0", ""};
            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
            adapter = new Inventory_Adapter(this, new ArrayList<InventoryLite>());
            lvInvMainBal.setAdapter(adapter);
            lblInvMainTallylog.setText("");
            adapter_sr = new StockRef_Adapter(myApp.getAppContext(), new ArrayList<StockRef>());
            lvInvMainTallySR.setAdapter(adapter_sr);
            loadBatchIDs();
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnInvMainSummary);
            //toggle(0, 0, 0, false, "", -1, null, "", "");
            toggle(0, 0, 0, false, "", null, "", "");

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity switchToBatchIDScanMode. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void loaddata(ArrayList<InventoryLite> invls, SharedPreferences g) {
        Context ctx = myApp.getAppContext();
        String InventoryBatchIDStr = "";
        InventoryBatchID ib = null;

        String InvMainMode = "";
        String InvSubMode = "";
        String InvTallyMode = "";
        String InvTallyQtyCanUndo = "";
        boolean can_undo = false;
        int InvMainModeInt = 0;
        int InvSubModeInt = 0;
        int InvTallyModeInt = 0;

        try {
            if (g == null)
                g = objGlobalMethods.getGcmPreferences();

            Batch_ID = objGlobalMethods.getGCMPrefs(g, "Batch_ID");
            Batch_ID = lblBatch_ID.getText().toString();

            InvMainMode = objGlobalMethods.getGCMPrefs(g, "InvMainMode");
            InvSubMode = objGlobalMethods.getGCMPrefs(g, "InvSubMode");
            InvTallyMode = objGlobalMethods.getGCMPrefs(g, "InvTallyMode");
            InvTallyQtyCanUndo = objGlobalMethods.getGCMPrefs(g, "InvTallyQtyCanUndo");
            can_undo = objGlobalMethods.IsEmptyString(InvTallyQtyCanUndo) ? false : Boolean.parseBoolean(InvTallyQtyCanUndo);
            InvMainModeInt = objGlobalMethods.IsEmptyString(InvMainMode) ? 0 : Integer.parseInt(InvMainMode);
            InvSubModeInt = objGlobalMethods.IsEmptyString(InvSubMode) ? 0 : Integer.parseInt(InvSubMode);
            InvTallyModeInt = objGlobalMethods.IsEmptyString(InvTallyMode) ? 0 : Integer.parseInt(InvTallyMode);

            if (invls == null) {
                InventoryBatchIDStr = objGlobalMethods.getGCMPrefs(g, "InventoryBatchIDStr");
                if (!objGlobalMethods.IsEmptyString(InventoryBatchIDStr))
                    ib = new Gson().fromJson(InventoryBatchIDStr, InventoryBatchID.class);

                if (ib != null && ib.invls != null)
                    invls = ib.invls;
            }

            if (invls != null) {
                //Collections.sort(invl, new InventoryLite.SortByStockReferences());
                /*Collections.sort(invl, Comparator.comparing(InventoryLite::getDescription)
                        .thenComparing(InventoryLite::getvBalance)
                        .thenComparing(InventoryLite::getBalance)
                );*/

                Collections.sort(invls,
                        Comparator.comparing(InventoryLite::getHiddenStockCode)
                );

                adapter = new Inventory_Adapter(this, invls);
                lvInvMainBal.setAdapter(adapter);
            } else {
                InvMainModeInt = 0;
                adapter = new Inventory_Adapter(this, new ArrayList<InventoryLite>());
                lvInvMainBal.setAdapter(adapter);
                loadBatchIDs();
            }
            //toggle(InvMainModeInt, InvSubModeInt, InvTallyModeInt, can_undo, "", -1, null, "", "");
            toggle(InvMainModeInt, InvSubModeInt, InvTallyModeInt, can_undo, "", null, "", "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity loaddata. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void loadBatchIDs() {
        ArrayList<InventoryBatchID> InventoryBatchIDList = null;
        InventoryBatchID ib = null;
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        ArrayList<String> list = null;

        try {
            InventoryBatchIDList = getSavedInventoryBatchIDList(g);
            if (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) {
                for (int i = 0; i <= InventoryBatchIDList.size() - 1; i++) {
                    ib = InventoryBatchIDList.get(i);
                    if (list == null)
                        list = new ArrayList<>();
                    list.add(ib.Batch_ID);
                }
            }

            adapter_bi = new Inventory_BatchID_Adapter(this, (list != null && list.size() > 0) ? list : new ArrayList<String>());
            lvInvMainBatchID.setAdapter(adapter_bi);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity loadBatchIDs. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void inputManualEntry(String input, int aInvMainMode) {
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        int aInvMainModeInt = 0;
        try {
            objGlobalMethods.setGCMPrefs(g, "cameraScannedCode", "");

            if (aInvMainMode == 0) {
                GetInventoryBatch gib = new GetInventoryBatch(input);
                gib.execute();
            } else {
                //getStockCodeDetails(input);
                chkStockCodeDetailsNew cscd = new chkStockCodeDetailsNew(input);
                cscd.execute();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity inputManualEntry. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    //public void toggle(int aInvMainMode, int aInvSubMode, int aInvTallyMode, boolean can_undo, final String BatchID, final int ibidx, final InventoryLite invl, final String sc, final String input) {
    public void toggle(int aInvMainMode, int aInvSubMode, int aInvTallyMode, boolean can_undo, final String BatchID, final InventoryLite invl, final String sc, final String input) {
        Context c = myApp.getAppContext();
        try {
            showmenu(aInvMainMode > 0);
            LLInvMainBlank.setVisibility((aInvMainMode == 0) ? View.VISIBLE : View.GONE);
            LLInvMainShown.setVisibility((aInvMainMode > 0) ? View.VISIBLE : View.GONE);
            LLInvMainTally.setVisibility((aInvMainMode > 0) && (aInvSubMode == 0) ? View.VISIBLE : View.GONE);
            LLInvMainBal.setVisibility((aInvMainMode > 0) && (aInvSubMode == 1) ? View.VISIBLE : View.GONE);

            btnInvVerify.setBackgroundResource((aInvMainMode > 0) && (aInvSubMode == 0) ? R.drawable.button_tabstyle_selected : R.drawable.button_tabstyle_unselected);
            btnInvBal.setBackgroundResource((aInvMainMode > 0) && (aInvSubMode == 1) ? R.drawable.button_tabstyle_selected : R.drawable.button_tabstyle_unselected);

            btnInvVerify.setTextColor((aInvMainMode > 0) && (aInvSubMode == 0) ? ContextCompat.getColor(c, R.color.brightergreen) : ContextCompat.getColor(c, R.color.White));
            btnInvBal.setTextColor((aInvMainMode > 0) && (aInvSubMode == 1) ? ContextCompat.getColor(c, R.color.brightergreen) : ContextCompat.getColor(c, R.color.White));

            txtInvMainBarcodeMANUALENTRY.setHint(c.getString((aInvMainMode > 0) ? R.string.stockcode : R.string.batch_id));

            LLInvMainTallyQty.setVisibility(aInvTallyMode == 1 ? View.VISIBLE : View.GONE);
            LLInvMainTallySR.setVisibility(aInvTallyMode == 2 ? View.VISIBLE : View.GONE);
            ctrlSettings.enableDisableCommonButtonWithStyles(can_undo, btnInvMainTallyQtyUndo);
            String vbalstr = lblInvMainTallyQtyVBal.getText().toString();
            ctrlSettings.enableDisableCommonButtonWithStyles((!objGlobalMethods.IsEmptyString(vbalstr) && !vbalstr.equals("0")), btnInvMainTallyQtyReset);

            if (aInvTallyMode == 0)
                lblInvMainTallyStockCode.setText(myApp.getAppContext().getString(R.string.stockcodestart));

            /*if (aInvTallyMode == 3 && aInvMainMode > 0)
                promptQtyOrRefTally(BatchID, ibidx, invl, sc, input);*/
            if (aInvTallyMode == 3 && aInvMainMode > 0)
                promptQtyOrRefTally(BatchID, invl, sc, input);


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity toggle. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    //public void toggleToTallyNew(final InventoryLite invl, final int invlidx) {
    public void toggleToTallyNew(final InventoryLite invl) {
        int status = 0;
        String sc = "";
        Context c = myApp.getAppContext();
        int InvTallyMode = 1;
        try {
            if (invl != null) {
                if (!((invl.Flag & 2) == 2) && !((invl.Flag & 4) == 4) && !((invl.Flag & 8) == 8) && !((invl.Flag & 16) == 16) && !((invl.Flag & 32) == 32) && !((invl.Flag & 64) == 64)) {
                    InvTallyMode = invl.InvTallyMode;
                    String[] prefName = new String[]{"InvTallyMode", "InvTallyQtyCanUndo", "InvSubMode"};
                    String[] prefvalue = new String[]{String.valueOf(InvTallyMode), "", "0"};
                    objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);
                    //status = invl.VFlag > 0 ? 2 : 1;
                    status = invl.vBalance >= invl.Balance ? 2 : 1;
                    sc = !objGlobalMethods.IsEmptyString(invl.StockCode) ? invl.StockCode : "";
                } else {
                    if (invl.Stockrefs != null && invl.Stockrefs.size() > 0 && (invl.vBalance != invl.Balance)) {
                        InvTallyMode = invl.InvTallyMode;
                        status = 1;
                    } else if (invl.Stockrefs != null && invl.Stockrefs.size() > 0 && (invl.vBalance == invl.Balance)) {
                        InvTallyMode = invl.InvTallyMode;
                        status = 2;
                    } else {
                        InvTallyMode = invl.InvTallyMode;
                        status = invl.vBalance >= invl.Balance ? 2 : 1;
                    }

                    if (status > 0) {
                        sc = !objGlobalMethods.IsEmptyString(invl.StockCode) ? invl.StockCode : "";
                        String[] prefName = new String[]{"InvTallyMode", "InvTallyQtyCanUndo", "InvSubMode"};
                        String[] prefvalue = new String[]{String.valueOf(InvTallyMode), "", "0"};
                        objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);
                    }
                }

                if (status == 1 || status == 2) {
                    playSuccessVerifiedTone();
                    lblInvMainTallyStockCode.setText(sc);

                    lblInvMainTallylog.setText(status == 2 ? (objGlobalMethods.IsEmptyString(sc) ? "" : sc + " ") + c.getString(R.string.items_tallied) : "");

                    if (InvTallyMode == 2) {
                        setTxtBoxHint(c.getString(R.string.stockref));
                        adapter_sr = new StockRef_Adapter(myApp.getAppContext(), invl.Stockrefs);
                        lvInvMainTallySR.setAdapter(adapter_sr);
                        //toggle(1, 0, InvTallyMode, false, "", -1, null, "", "");
                        toggle(1, 0, InvTallyMode, false, "", null, "", "");
                    } else if (InvTallyMode == 1) {
                        setTxtBoxHint(c.getString(R.string.stockcode));
                        lblInvMainTallyQtyVBal.setText(String.valueOf(invl.vBalance));
                        lblInvMainTallyQtyBal.setText(String.valueOf(invl.Balance));
                        //toggle(1, 0, InvTallyMode, false, "", -1, null, "", "");
                        toggle(1, 0, InvTallyMode, false, "", null, "", "");
                    } else {
                        setTxtBoxHint(c.getString(R.string.stockcode));
                        //toggle(1, 0, InvTallyMode, false, invl.Batch_ID, invlidx, invl, invl.StockCode, "");
                        toggle(1, 0, InvTallyMode, false, invl.Batch_ID, invl, invl.StockCode, "");
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity toggleToTallyNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnInvMainBarcodeSubmitENTER_Click(View v) {
        String input = "";
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        String InvMainMode = objGlobalMethods.getGCMPrefs(g, "InvMainMode");
        int InvMainModeInt = objGlobalMethods.IsEmptyString(InvMainMode) ? 0 : Integer.parseInt(InvMainMode);
        boolean ok = false;
        try {
            input = txtInvMainBarcodeMANUALENTRY.getText().toString();
            focusTextbox();
            ok = !objGlobalMethods.IsEmptyString(input);
            //ok = !objGlobalMethods.IsEmptyString(input) || (objGlobalMethods.IsEmptyString(input) && InvMainModeInt == 0);

            if (ok) {
                inputManualEntry(InvMainModeInt == 0 ? "" : input, InvMainModeInt);
                //inputManualEntry(input, InvMainModeInt);
            } else {
                playTone();
                Context c = myApp.getAppContext();
                if (InvMainModeInt == 0) {
                    objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.pls_enter_batch_id)).show();
                } else {
                    objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.pls_enter_stockcode)).show();
                }
            }
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity btnInvMainBarcodeSubmitENTER_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

    }

    public void btnInvVerify_Click(View v) {
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        String[] prefName = new String[]{"InvSubMode"};
        String[] prefvalue = new String[]{"0"};
        objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
        String InvTallyMode = objGlobalMethods.getGCMPrefs(g, "InvTallyMode");
        String InvTallyQtyCanUndo = objGlobalMethods.getGCMPrefs(g, "InvTallyQtyCanUndo");
        boolean can_undo = objGlobalMethods.IsEmptyString(InvTallyQtyCanUndo) ? false : Boolean.parseBoolean(InvTallyQtyCanUndo);
        int InvTallyModeInt = objGlobalMethods.IsEmptyString(InvTallyMode) ? 0 : Integer.parseInt(InvTallyMode);

        //toggle(1, 0, InvTallyModeInt, can_undo, "", -1, null, "", "");
        toggle(1, 0, InvTallyModeInt, can_undo, "", null, "", "");
    }

    public void btnInvBal_Click(View v) {
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        String[] prefName = new String[]{"InvSubMode"};
        String[] prefvalue = new String[]{"1"};
        objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
        String InvTallyMode = objGlobalMethods.getGCMPrefs(g, "InvTallyMode");
        String InvTallyQtyCanUndo = objGlobalMethods.getGCMPrefs(g, "InvTallyQtyCanUndo");
        boolean can_undo = objGlobalMethods.IsEmptyString(InvTallyQtyCanUndo) ? false : Boolean.parseBoolean(InvTallyQtyCanUndo);
        int InvTallyModeInt = objGlobalMethods.IsEmptyString(InvTallyMode) ? 0 : Integer.parseInt(InvTallyMode);

        //toggle(1, 1, InvTallyModeInt, can_undo, "", -1, null, "", "");
        toggle(1, 1, InvTallyModeInt, can_undo, "", null, "", "");
    }

    public void btnInvMainTallyQtyUndo_Click(View v) {
        try {
            Context ctx = myApp.getAppContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setTitle("");
            builder.setCancelable(false);
            builder.setMessage(ctx.getString(R.string.undo_last_add));

            builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    dialog.dismiss();
                    TallyQty atq = new TallyQty(0, 1, lblInvMainTallyStockCode.getText().toString());
                    atq.execute();
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
            alertdialog.show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity btnInvMainTallyQtyUndo_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnInvMainTallyQtyAdd_Click(View v) {
        String input = "";
        int va = 0;
        Context c = myApp.getAppContext();
        try {
            input = txtInvMainTallyQty.getText().toString();
            va = !objGlobalMethods.IsEmptyString(input) ? Integer.parseInt(input) : 0;
            if (va > 0) {
                TallyQty atq = new TallyQty(va, 0, lblInvMainTallyStockCode.getText().toString());
                atq.execute();
            } else {
                objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.pls_enter_quantity)).show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity btnInvMainTallyQtyAdd_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public void btnInvMainTallyQtyReset_Click(View v) {
        try {
            Context ctx = myApp.getAppContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setTitle("");
            builder.setCancelable(false);
            builder.setMessage(ctx.getString(R.string.undo_last_add));

            builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    dialog.dismiss();
                    TallyQty atq = new TallyQty(0, 2, lblInvMainTallyStockCode.getText().toString());
                    atq.execute();
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
            alertdialog.show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity btnInvMainTallyQtyReset_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnInvMainCANCEL_Click(View v) {
        try {
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            String InvMainMode = objGlobalMethods.getGCMPrefs(g, "InvMainMode");
            int InvMainModeInt = objGlobalMethods.IsEmptyString(InvMainMode) ? 0 : Integer.parseInt(InvMainMode);
            if (InvMainModeInt > 0) {
                switchToBatchIDScanMode();
            } else {
                back();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity btnInvMainCANCEL_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

    }

    public void btnInvMainSummary_Click(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.r_u_done_tally));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
                SharedPreferences g = objGlobalMethods.getGcmPreferences();
                String InventoryBatchIDStr = objGlobalMethods.getGCMPrefs(g, "InventoryBatchIDStr");
                try {
                    proceedToSummary ps = new proceedToSummary();
                    ps.execute();
                    /*if (!objGlobalMethods.IsEmptyString(InventoryBatchIDStr)) {
                        String[] prefName = new String[]{"InvMainMode", "InvSubMode"};
                        String[] prefvalue = new String[]{"1", "1"};
                        objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);

                        Intent intent = new Intent(myApp.currentActivity, InventorySummaryActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Context c = myApp.getAppContext();
                        objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.no_data_loaded_yet)).show();
                    }*/
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at InventoryMainActivity btnInvMainSummary_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        });

        builder.setNeutralButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public class proceedToSummary extends AsyncTask<Void, Void, Boolean> {

        public proceedToSummary(){}

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdCheck.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean res = false;

            try {
                SharedPreferences g = objGlobalMethods.getGcmPreferences();
                String InventoryBatchIDStr = objGlobalMethods.getGCMPrefs(g, "InventoryBatchIDStr");
                if (!objGlobalMethods.IsEmptyString(InventoryBatchIDStr)) {
                    String[] prefName = new String[]{"InvMainMode", "InvSubMode"};
                    String[] prefvalue = new String[]{"1", "1"};
                    objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                    res = true;
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity proceedToSummary doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final Boolean res) {
            Context c = myApp.getAppContext();
            try {
                pdCheck.dismiss();
                if (res) {
                    Intent intent = new Intent(myApp.currentActivity, InventorySummaryActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    promptErrProcessing();
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity proceedToSummary onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }


    }

    public class addTallyQtyResult {
        public int status;
        public ArrayList<InventoryLite> invls;
        public int bal;
        public int vbalance;

        public addTallyQtyResult(int astatus, int abal, ArrayList<InventoryLite> ainvls) {
            this.status = astatus;
            this.bal = abal;
            this.invls = ainvls;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setinvls(ArrayList<InventoryLite> ainvls) {
            this.invls = ainvls;
        }

        public void setBal(int bal) {
            this.bal = bal;
        }

        public void setVbalance(int avbalance) {
            this.vbalance = avbalance;
        }

    }

    public class TallyQty extends AsyncTask<Void, Void, addTallyQtyResult> {
        private final int v;
        private int mode;
        private final String sc;

        public TallyQty(int av, int amode, String asc) {
            this.v = av;
            this.mode = amode;
            this.sc = asc;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdCheck.show();
        }

        @Override
        protected addTallyQtyResult doInBackground(Void... params) {
            addTallyQtyResult res = new addTallyQtyResult(0, 0, null);
            String InventoryBatchIDStr = "";
            //String chkStockCodeDetailsResultJsonStr = "";
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            //chkStockCodeDetailsResult r = null;
            InventoryBatchID ib = null;
            ArrayList<InventoryLite> invls = null;
            InventoryLite invl = null;
            int vbal = 0;
            int bal = 0;
            int fbal = 0;
            int pbal = 0;
            Gson gson = new Gson();
            boolean invlfound = false;
            boolean vbalEQUALbal = false;
            boolean can_undo = false;
            String InventoryBatchIDListJsonStr = "";
            ArrayList<InventoryBatchID> InventoryBatchIDList = null;
            try {
                /*chkStockCodeDetailsResultJsonStr = objGlobalMethods.getGCMPrefs(g, "chkStockCodeDetailsResult");
                if (!objGlobalMethods.IsEmptyString(chkStockCodeDetailsResultJsonStr))
                    r = gson.fromJson(chkStockCodeDetailsResultJsonStr, chkStockCodeDetailsResult.class);*/

                //if (r != null)
                ib = getInventoryBatchID();

                //if (ib != null && r != null) {
                if (ib != null) {
                    invls = ib.invls;
                    if (invls != null && invls.size() > 0) {
                        for (int i = 0; i <= invls.size() - 1; i++) {
                            invl = invls.get(i);
                            //if (invl.StockCode.toUpperCase().equals(r.StockCode.toUpperCase())) {
                            if (invl.StockCode.toUpperCase().equals(this.sc)) {
                                if (this.mode == 0) {
                                    //add
                                    /*if (invl.vBalance >= invl.Balance) {
                                        res.setBal(invl.Balance);
                                        res.setStatus(4);
                                        res.setinvls(invls);

                                        invlfound = true;
                                        ib.addLogs("StockCode " + this.sc + " detected.\nInventory tallied.");
                                        InventoryBatchIDStr = gson.toJson(ib);

                                    } else {*/
                                    bal = invl.Balance;
                                    vbal = invl.vBalance;
                                    pbal = invl.vBalance;
                                    fbal = vbal + v;


                                    invls.get(i).setvBalance(fbal);
                                    invls.get(i).setPrevBalance(pbal);
                                    vbalEQUALbal = fbal >= bal;
                                    invls.get(i).setVFlag(vbalEQUALbal ? 1 : 0);
                                    if (vbalEQUALbal)
                                        invls.get(i).setHiddenStockCode("1_" + invl.StockCode);
                                    can_undo = true;
                                    ib.setInventory(invls);
                                    ib.addLogs("StockCode " + this.sc + " detected.\nQty added " + String.valueOf(v) + ".\nNew Balance: " + String.valueOf(fbal) + "/" + String.valueOf(bal));

                                    objGlobalMethods.writeToFile("StockCode " + this.sc + " detected. Qty added " + String.valueOf(v) + ". New Balance: " + String.valueOf(fbal) + "/" + String.valueOf(bal), "", "", "", "");
                                    InventoryBatchIDStr = gson.toJson(ib);

                                    invlfound = true;
                                    res.setBal(invl.Balance);
                                    res.setVbalance(fbal);
                                    res.setStatus(1);
                                    res.setinvls(invls);
                                    //}
                                } else if (this.mode == 1) {
                                    //undo
                                    bal = invl.Balance;
                                    pbal = invl.prevBalance;
                                    vbal = invl.vBalance;

                                    invls.get(i).setvBalance(pbal);
                                    vbalEQUALbal = false;
                                    invls.get(i).setVFlag(vbalEQUALbal ? 1 : 0);
                                    invls.get(i).setHiddenStockCode("0_" + invl.StockCode);
                                    ib.setInventory(invls);
                                    ib.addLogs("StockCode " + this.sc + " detected.\nUNDO once.\nPrevious Verified Qty: " + String.valueOf(vbal)
                                            + "\nNew Balance: " + String.valueOf(pbal) + "/" + String.valueOf(bal));

                                    objGlobalMethods.writeToFile("StockCode " + this.sc + " detected. UNDO once. Previous Verified Qty: " + String.valueOf(vbal)
                                            + " New Balance: " + String.valueOf(pbal) + "/" + String.valueOf(bal), "", "", "", "");
                                    InventoryBatchIDStr = gson.toJson(ib);

                                    invlfound = true;
                                    res.setBal(invl.Balance);
                                    res.setVbalance(pbal);
                                    res.setStatus(1);
                                    res.setinvls(invls);
                                } else {
                                    //reset
                                    bal = invl.Balance;
                                    pbal = invl.vBalance;

                                    invls.get(i).setvBalance(0);
                                    invls.get(i).setPrevBalance(pbal);
                                    vbalEQUALbal = false;
                                    invls.get(i).setVFlag(vbalEQUALbal ? 1 : 0);
                                    invls.get(i).setHiddenStockCode("0_" + invl.StockCode);
                                    can_undo = true;
                                    ib.setInventory(invls);
                                    ib.addLogs("StockCode " + this.sc + " detected.\nRESET.\nPrevious Verified Qty: " + String.valueOf(pbal) + ".\nNew Balance: 0/" + String.valueOf(bal));
                                    objGlobalMethods.writeToFile("StockCode " + this.sc + " detected. RESET. Previous Verified Qty: " + String.valueOf(pbal) + ". New Balance: 0/" + String.valueOf(bal), "", "", "", "");
                                    InventoryBatchIDStr = gson.toJson(ib);

                                    invlfound = true;
                                    res.setBal(invl.Balance);
                                    res.setVbalance(0);
                                    res.setStatus(1);
                                    res.setinvls(invls);
                                }
                                break;
                            }
                        }

                        /*if (invlfound) {
                            String[] prefName = new String[]{"InventoryBatchIDStr", "chkStockCodeDetailsResult", "InvTallyQtyCanUndo"};
                            String[] prefvalue = new String[]{InventoryBatchIDStr, chkStockCodeDetailsResultJsonStr, can_undo ? "true" : ""};
                            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                        }*/
                        if (invlfound) {
                            InventoryBatchIDList = getSavedInventoryBatchIDList(g);
                            int currInventoryBatchID_ListIndex = getSavedInventoryBatchID_ListIndex(ib.Batch_ID, InventoryBatchIDList);
                            InventoryBatchIDList.set(currInventoryBatchID_ListIndex, ib);
                            InventoryBatchIDListJsonStr = gson.toJson(InventoryBatchIDList);

                            String[] prefName = new String[]{"InvTallyQtyCanUndo", "InventoryBatchIDStr", "InventoryBatchIDListJsonStr"};
                            String[] prefvalue = new String[]{can_undo ? "true" : "", InventoryBatchIDStr, InventoryBatchIDListJsonStr};
                            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                        }

                    } else {
                        ib.addLogs("Unable to process TallyQty");
                        objGlobalMethods.writeToFile("Unable to process TallyQty. (invls == null || invls.size() == 0).", "", "", "", "");
                    }
                } else {
                    objGlobalMethods.writeToFile("Unable to process TallyQty. ib is null.", "", "", "", "");
                }

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity TallyQty doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final addTallyQtyResult res) {
            Context c = myApp.getAppContext();
            try {
                pdCheck.dismiss();
                if (res.status == 0) {
                    dispTallyProcessErr();
                } else if (res.status == 1) {
                    playSuccessVerifiedTone();
                    lblInvMainTallyQtyVBal.setText(String.valueOf(res.vbalance));
                    lblInvMainTallyQtyBal.setText(String.valueOf(res.bal));
                    focustxtInvMainTallyQty();
                    loaddata(res.invls, null);
                } else if (res.status == 2) {
                    playSuccessVerifiedTone();
                    focustxtInvMainTallyQty();
                    loaddata(res.invls, null);
                } else if (res.status == 4) {
                    dispTallied("");
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity TallyQty onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }

    }

    private class TallyStockRef extends AsyncTask<Void, Void, addTallyQtyResult> {
        private final String input;
        private String sc;

        public TallyStockRef(String ainput) {
            this.input = ainput;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdCheck.show();
        }

        @Override
        protected addTallyQtyResult doInBackground(Void... params) {
            addTallyQtyResult res = new addTallyQtyResult(0, 0, null);
            String InventoryBatchIDStr = "";
            String chkStockCodeDetailsResultJsonStr = "";
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            chkStockCodeDetailsResult r = null;
            InventoryBatchID ib = null;
            ArrayList<InventoryLite> invls_saved = null;
            ArrayList<InventoryLite> invls = null;
            InventoryLite invl = null;
            int vbal = 0;
            int bal = 0;
            int fbal = 0;
            int pbal = 0;
            Gson gson = new Gson();
            boolean invlfound = false;
            boolean vbalEQUALbal = false;
            boolean can_undo = false;
            ArrayList<StockRef> stockref = null;
            ArrayList<StockRef> stockref_saved = null;
            StockRef sr = null;
            int invlBal = 0;
            int invlvBal = 0;
            int invlvBalnew = 0;
            String InvTallyMode = "";

            try {
                ib = getInventoryBatchID();

                if (ib != null) {
                    invls = ib.invls;
                    outer:
                    if (invls != null && invls.size() > 0) {
                        for (int i = 0; i <= invls.size() - 1; i++) {
                            invl = invls.get(i);
                            if (!objGlobalMethods.IsEmptyString(invl.StockCode) && objGlobalMethods.IsEqualString(invl.StockCode, input)) {
                                //scanned stockcode, retrieve n show via stockcode
                                res.setBal(invl.Balance);
                                res.setVbalance(invl.vBalance);
                                res.setinvls(invls);
                                res.setStatus(1);

                                String[] prefName = new String[]{"InventoryBatchIDStr", "InvTallyMode", "InvTallyQtyCanUndo"};
                                String[] prefvalue = new String[]{InventoryBatchIDStr, "1", ""};
                                objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                            } else if (invl.Stockrefs != null && invl.Stockrefs.size() > 0) {
                                for (int v = 0; v <= invl.Stockrefs.size() - 1; v++) {
                                    sr = invl.Stockrefs.get(v);
                                    if (sr != null && !objGlobalMethods.IsEmptyString(sr.StockReference) && objGlobalMethods.IsEqualString(sr.StockReference, input)) {
                                        if (!sr.verified) {
                                            sr.setVerified(true);
                                            invlBal = invl.Balance;
                                            invlvBal = invl.vBalance;
                                            invlvBalnew = invlvBal + 1;
                                            invls.get(i).setvBalance(invlvBalnew);
                                            invls.get(i).setVFlag(invlBal == invlvBalnew ? 1 : 0);

                                            invls.get(i).Stockrefs.set(v, sr);
                                            ib.setInventory(invls);
                                            InventoryBatchIDStr = gson.toJson(ib);
                                            invlfound = true;
                                            res.setStatus((invl.vBalance >= invl.Balance) ? 4 : 2);

                                            String[] prefName = new String[]{"InventoryBatchIDStr"};
                                            String[] prefvalue = new String[]{InventoryBatchIDStr};
                                            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                                        } else {
                                            res.setStatus(3);
                                        }
                                        this.sc = invl.StockCode;
                                        break outer;
                                    }
                                }
                            }
                        }
                    }

                }

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity TallyStockRef doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final addTallyQtyResult res) {
            Context c = myApp.getAppContext();
            try {
                pdCheck.dismiss();
                if (res.status == 0) {
                    dispTallyProcessErr();
                } else if (res.status == 1) {
                    playSuccessVerifiedTone();
                    lblInvMainTallyQtyVBal.setText(String.valueOf(res.vbalance));
                    lblInvMainTallyQtyBal.setText(String.valueOf(res.bal));
                    focustxtInvMainTallyQty();
                    loaddata(res.invls, null);
                } else if (res.status == 2) {
                    dispTallyStockRefVerified(input);
                    playSuccessVerifiedTone();
                    focustxtInvMainTallyQty();
                    loaddata(res.invls, null);
                } else if (res.status == 3) {
                    dispTallyStockRefVerified(input);
                    playSuccessVerifiedTone();
                } else if (res.status == 4) {
                    dispTallied("");
                    playSuccessVerifiedTone();
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity TallyStockRef onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    public class chkStockCodeDetailsResult {
        public int status;
        public ArrayList<StockRef> Stockrefs;
        public int balance;
        public int pbalance;
        public int vbalance;
        public String StockCode;
        public ArrayList<InventoryLite> invls;
        public int InvTallyMode = 1;
        public int invlidx = -1;
        public String BatchID = "";
        public InventoryLite invl;

        public chkStockCodeDetailsResult(int astatus, ArrayList<StockRef> aStockRef, int abalance, int apbalance, int avbalance, String aStockCode) {
            this.status = astatus;
            this.Stockrefs = aStockRef;
            this.balance = abalance;
            this.pbalance = apbalance;
            this.vbalance = avbalance;
            this.StockCode = aStockCode;
        }

        public void setInvls(ArrayList<InventoryLite> invls) {
            this.invls = invls;
        }

        public void setPbalance(int pbalance) {
            this.pbalance = pbalance;
        }

        public void setVbalance(int vbalance) {
            this.vbalance = vbalance;
        }

        public void setInvTallyMode(int invTallyMode) {
            InvTallyMode = invTallyMode;
        }

        public void setinvlidx(int ib_idx) {
            this.invlidx = ib_idx;
        }

        public void setBatchID(String batchID) {
            BatchID = batchID;
        }

        public void setInvl(InventoryLite invl) {
            this.invl = invl;
        }
    }

    private InventoryBatchID getInventoryBatchID() {
        InventoryBatchID ib = null;
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        String InventoryBatchIDStr = "";

        try {
            InventoryBatchIDStr = objGlobalMethods.getGCMPrefs(g, "InventoryBatchIDStr");
            if (!objGlobalMethods.IsEmptyString(InventoryBatchIDStr)) {
                ib = new Gson().fromJson(InventoryBatchIDStr, InventoryBatchID.class);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exeption at InventoryMainActivity getInventoryBatchID. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return ib;
        }
    }

    private class chkStockCodeDetailsNew extends AsyncTask<Void, Void, chkStockCodeDetailsResult> {

        private final String input;
        private String sc;
        private int detectedStockRefsIDX = -1;

        public chkStockCodeDetailsNew(String ainput) {
            this.input = ainput;
        }

        @Override
        protected chkStockCodeDetailsResult doInBackground(Void... params) {
            chkStockCodeDetailsResult res = null;
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            String InventoryBatchIDStr = "";
            InventoryBatchID ib = null;
            ArrayList<InventoryLite> invls = null;
            InventoryLite invl = null;
            Gson gson = new Gson();
            String InvTallyMode = "";
            StockRef sr = null;
            int invlBal = 0;
            int invlvBal = 0;
            int invlvBalnew = 0;
            boolean updateInventoryBatchIDStr = false;
            String InventoryBatchIDListJsonStr = "";
            ArrayList<InventoryBatchID> InventoryBatchIDList = null;
            int foundidx = -1;
            boolean savedStockCodeMode = false;
            boolean StockCodeMode = false;
            String StockCodeModeStr = "";
            try {
                InventoryBatchIDStr = objGlobalMethods.getGCMPrefs(g, "InventoryBatchIDStr");

                if (!objGlobalMethods.IsEmptyString(InventoryBatchIDStr)) {
                    ib = new Gson().fromJson(InventoryBatchIDStr, InventoryBatchID.class);
                    if (ib != null && ib.invls != null && ib.invls.size() > 0) {
                        invls = ib.invls;
                        outer:
                        for (int i = 0; i <= invls.size() - 1; i++) {
                            invl = invls.get(i);
                            if (!objGlobalMethods.IsEmptyString(invl.StockCode) && objGlobalMethods.IsEqualString(invl.StockCode, input)) {
                                if (!((invl.Flag & 2) == 2) && !((invl.Flag & 4) == 4) && !((invl.Flag & 8) == 8) && !((invl.Flag & 16) == 16) && !((invl.Flag & 32) == 32) && !((invl.Flag & 64) == 64)) {
                                    //show enter quantity
                                    foundidx = i;
                                    this.sc = this.input;
                                    InvTallyMode = "1";
                                    res = new chkStockCodeDetailsResult(invl.vBalance >= invl.Balance ? 2 : 1, null, invl.Balance, invl.prevBalance, invl.vBalance, invl.StockCode);
                                    res.setInvTallyMode(Integer.parseInt(InvTallyMode));
                                    res.setinvlidx(foundidx);
                                    res.setBatchID(ib.Batch_ID);

                                    ib.addLogs("StockCode " + input + " detected for Qty entry.");
                                    objGlobalMethods.writeToFile("StockCode " + input + " detected for Qty entry.", "", "", "", "");
                                    InventoryBatchIDStr = gson.toJson(ib);
                                    updateInventoryBatchIDStr = true;
                                    break outer;
                                } else {
                                    //show scan/enter stock ref
                                    if (invl.Stockrefs != null && invl.Stockrefs.size() > 0 && (invl.vBalance != invl.Balance)) {
                                        foundidx = i;
                                        StockCodeMode = true;
                                        this.sc = this.input;
                                        InvTallyMode = (invl.vBalance == 0) ? "3" : String.valueOf(invl.InvTallyMode);
                                        res = new chkStockCodeDetailsResult(1, invl.Stockrefs, invl.Balance, invl.prevBalance, invl.vBalance, invl.StockCode);
                                        res.setInvTallyMode(Integer.parseInt(InvTallyMode));
                                        res.setinvlidx(foundidx);
                                        res.setBatchID(ib.Batch_ID);
                                        if (InvTallyMode.equals("3"))
                                            res.setInvl(invl);

                                        ib.addLogs((invl.vBalance == 0) ? "StockCode " + input + " detected. Requesting for Qty entry or StockReference tally."
                                                : "StockCode " + input + " detected for StockReference tally.");

                                        objGlobalMethods.writeToFile((invl.vBalance == 0) ?
                                                "StockCode " + input + " detected. Requesting for Qty entry or StockReference tally."
                                                : "StockCode " + input + " detected for StockReference tally.", "", "", "", "");

                                        InventoryBatchIDStr = gson.toJson(ib);
                                        updateInventoryBatchIDStr = true;
                                        break outer;
                                    } else if (invl.Stockrefs != null && invl.Stockrefs.size() > 0 && (invl.vBalance == invl.Balance)) {
                                        foundidx = i;
                                        StockCodeMode = true;
                                        this.sc = this.input;
                                        InvTallyMode = String.valueOf(invl.InvTallyMode);
                                        res = new chkStockCodeDetailsResult(2, invl.Stockrefs, invl.Balance, invl.prevBalance, invl.vBalance, invl.StockCode);
                                        res.setInvTallyMode(Integer.parseInt(InvTallyMode));
                                        res.setinvlidx(foundidx);
                                        res.setBatchID(ib.Batch_ID);

                                        ib.addLogs("StockCode " + input + " detected. Inventory tallied.");
                                        objGlobalMethods.writeToFile("StockCode " + input + " detected. Inventory tallied.", "", "", "", "");
                                        InventoryBatchIDStr = gson.toJson(ib);
                                        updateInventoryBatchIDStr = true;
                                        break outer;
                                    } else {
                                        foundidx = i;
                                        StockCodeMode = true;
                                        this.sc = this.input;
                                        InvTallyMode = "1";
                                        res = new chkStockCodeDetailsResult(invl.vBalance >= invl.Balance ? 2 : 1, null, invl.Balance, invl.prevBalance, invl.vBalance, invl.StockCode);
                                        res.setInvTallyMode(Integer.parseInt(InvTallyMode));
                                        res.setinvlidx(foundidx);
                                        res.setBatchID(ib.Batch_ID);

                                        ib.addLogs("StockCode " + input + " detected for StockReference tally. No Stock Ref found. Switch to Qty entry.");
                                        objGlobalMethods.writeToFile("StockCode " + input + " detected for StockReference tally. No Stock Ref found. Switch to Qty entry.", "", "", "", "");
                                        InventoryBatchIDStr = gson.toJson(ib);
                                        updateInventoryBatchIDStr = true;
                                        break outer;
                                    }
                                }
                            } else if (invl.Stockrefs != null && invl.Stockrefs.size() > 0) {
                                StockCodeModeStr = objGlobalMethods.getGCMPrefs(g, "StockCodeMode");
                                savedStockCodeMode = !objGlobalMethods.IsEmptyString(StockCodeModeStr) ? true : false;
                                //check thru stockref
                                for (int v = 0; v <= invl.Stockrefs.size() - 1; v++) {
                                    sr = invl.Stockrefs.get(v);
                                    if (sr != null && !objGlobalMethods.IsEmptyString(sr.StockReference) && objGlobalMethods.IsEqualString(sr.StockReference, input)) {
                                        foundidx = i;
                                        this.sc = invl.StockCode;
                                        /*InvTallyMode = (invl.InvTallyMode != 3 && invl.vBalance == 0 && savedStockCodeMode) ? String.valueOf(invl.InvTallyMode) :
                                                (invl.InvTallyMode != 3 && invl.vBalance > 0) ? String.valueOf(invl.InvTallyMode) :
                                                        "3";*/
                                        InvTallyMode = invl.InvTallyMode == 3 ? "2" : String.valueOf(invl.InvTallyMode);

                                        if (InvTallyMode.equals("2")) {
                                            if (!sr.verified) {
                                                sr.setVerified(true);
                                                invlBal = invl.Balance;
                                                invlvBal = invl.vBalance;
                                                invlvBalnew = invlvBal + 1;
                                                invls.get(i).setvBalance(invlvBalnew);
                                                invls.get(i).setVFlag(invlBal == invlvBalnew ? 1 : 0);
                                                invls.get(i).setInvTallyMode(2);
                                                if (invlBal == invlvBalnew)
                                                    invls.get(i).setHiddenStockCode("1_" + invl.StockCode);

                                                invls.get(i).Stockrefs.set(v, sr);
                                                ib.setInventory(invls);
                                            }

                                            detectedStockRefsIDX = v;

                                            ib.addLogs((invl.Balance == invl.vBalance)
                                                    ? "StockReference " + input + " verified." + "\n" + sc + " tally completed."
                                                    : "StockReference " + input + " verified.");

                                            objGlobalMethods.writeToFile(
                                                    (invl.Balance == invl.vBalance)
                                                            ? "StockReference " + input + " verified." + " " + sc + " tally completed."
                                                            : "StockReference " + input + " verified."
                                                    , "", "", "", "");
                                            InventoryBatchIDStr = gson.toJson(ib);
                                            updateInventoryBatchIDStr = true;

                                            invl.Stockrefs.set(v, sr);
                                            res = new chkStockCodeDetailsResult(4, invl.Stockrefs, invl.Balance, invl.prevBalance, invl.vBalance, invl.StockCode);
                                            res.setInvTallyMode(Integer.parseInt(InvTallyMode));
                                            res.setinvlidx(foundidx);
                                            res.setInvls(ib.invls);
                                            res.setBatchID(ib.Batch_ID);
                                        } else {
                                            res = new chkStockCodeDetailsResult(4, invl.Stockrefs, invl.Balance, invl.prevBalance, invl.vBalance, invl.StockCode);
                                            res.setInvTallyMode(Integer.parseInt(InvTallyMode));
                                            res.setinvlidx(foundidx);
                                            res.setBatchID(ib.Batch_ID);

                                            if (InvTallyMode.equals("3"))
                                                res.setInvl(invl);
                                        }

                                        break outer;
                                    }
                                }
                            }
                        }

                        if (res == null) {
                            ib.addLogs(input + " detected.\nNot Found.");
                            InventoryBatchIDStr = gson.toJson(ib);
                            updateInventoryBatchIDStr = true;
                        }
                    } else {
                        if (ib != null) {
                            ib.addLogs("Unable to process " + input);
                            updateInventoryBatchIDStr = true;
                        }
                        objGlobalMethods.writeToFile("Unable to process InventoryMainActivity chkStocKCodeDetails due (ib == null || ib.invls == null || ib.invls.size() == 0).", "", "", "", "");
                    }
                } else {
                    objGlobalMethods.writeToFile("Unable to process InventoryMainActivity chkStocKCodeDetails due InventoryBatchIDStr is empty.", "", "", "", "");
                }

                if (updateInventoryBatchIDStr) {
                    InventoryBatchIDList = getSavedInventoryBatchIDList(g);
                    if (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) {
                        int currInventoryBatchID_ListIndex = getSavedInventoryBatchID_ListIndex(ib.Batch_ID, InventoryBatchIDList);
                        if (currInventoryBatchID_ListIndex > -1) {
                            InventoryBatchIDList.set(currInventoryBatchID_ListIndex, ib);
                            InventoryBatchIDListJsonStr = gson.toJson(InventoryBatchIDList);
                            String[] prefName = new String[]{"InvTallyMode", "InvTallyQtyCanUndo", "InvSubMode", "InventoryBatchIDStr", "InventoryBatchIDListJsonStr", "StockCodeMode"};
                            String[] prefvalue = new String[]{InvTallyMode, "", "0", InventoryBatchIDStr, InventoryBatchIDListJsonStr, StockCodeMode ? "t" : ""};
                            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                        } else {
                            String[] prefName = new String[]{"InvTallyMode", "InvTallyQtyCanUndo", "InvSubMode", "InventoryBatchIDStr", "StockCodeMode"};
                            String[] prefvalue = new String[]{InvTallyMode, "", "0", InventoryBatchIDStr, StockCodeMode ? "t" : ""};
                            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                        }
                    } else {
                        String[] prefName = new String[]{"InvTallyMode", "InvTallyQtyCanUndo", "InvSubMode", "InventoryBatchIDStr", "StockCodeMode"};
                        String[] prefvalue = new String[]{InvTallyMode, "", "0", InventoryBatchIDStr, StockCodeMode ? "t" : ""};
                        objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                    }
                } else {
                    String[] prefName = new String[]{"InvTallyMode", "InvTallyQtyCanUndo", "InvSubMode", "StockCodeMode"};
                    String[] prefvalue = new String[]{InvTallyMode, "", "0", StockCodeMode ? "t" : ""};
                    objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity chkStockCodeDetailsNew doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final chkStockCodeDetailsResult res) {
            Context c = myApp.getAppContext();
            try {
                pdCheck.dismiss();
                if (res != null && res.status == 3) {
                    dispTallyStockRefNotFound();
                    setTxtBoxHint(c.getString(R.string.stockcode));
                } else if (res != null && (res.status == 1 || res.status == 2)) {
                    playSuccessVerifiedTone();
                    lblInvMainTallyStockCode.setText(sc);

                    lblInvMainTallylog.setText(res.status == 2 ? (objGlobalMethods.IsEmptyString(sc) ? "" : sc + " ") + c.getString(R.string.items_tallied) : "");

                    if (res.InvTallyMode == 1) {
                        setTxtBoxHint(c.getString(R.string.stockcode));
                        lblInvMainTallyQtyVBal.setText(String.valueOf(res.vbalance));
                        lblInvMainTallyQtyBal.setText(String.valueOf(res.balance));
                        //toggle(1, 0, res.InvTallyMode, false, "", -1, null, "", "");
                        toggle(1, 0, res.InvTallyMode, false, "", null, "", "");
                    } else if (res.InvTallyMode == 2) {
                        if (res.Stockrefs != null) {
                            setTxtBoxHint(c.getString(R.string.stockref));
                            adapter_sr = new StockRef_Adapter(myApp.getAppContext(), res.Stockrefs);
                            lvInvMainTallySR.setAdapter(adapter_sr);
                            //toggle(1, 0, res.InvTallyMode, false, "", -1, null, "", "");
                            toggle(1, 0, res.InvTallyMode, false, "", null, "", "");
                        } else {
                            setTxtBoxHint(c.getString(R.string.stockcode));
                            lblInvMainTallyQtyVBal.setText(String.valueOf(res.vbalance));
                            lblInvMainTallyQtyBal.setText(String.valueOf(res.balance));
                            //toggle(1, 0, res.InvTallyMode, false, "", -1, null, "", "");
                            toggle(1, 0, res.InvTallyMode, false, "", null, "", "");
                        }
                    } else {
                        //promptQtyOrRefTally(res.BatchID, res.invlidx, res.invl, sc, input);
                        promptQtyOrRefTally(res.BatchID, res.invl, sc, input);
                    }
                } else if (res != null && res.status == 4) {
                    setTxtBoxHint(c.getString(R.string.stockcode));
                    playSuccessVerifiedTone();
                    lblInvMainTallyStockCode.setText(sc);
                    if (res.vbalance >= res.balance) {
                        dispTallied(sc);
                    }
                    if (res.InvTallyMode == 1) {
                        setTxtBoxHint(c.getString(R.string.stockcode));
                        lblInvMainTallyQtyVBal.setText(String.valueOf(res.vbalance));
                        lblInvMainTallyQtyBal.setText(String.valueOf(res.balance));
                        //toggle(1, 0, res.InvTallyMode, false, "", -1, null, "", "");
                        toggle(1, 0, res.InvTallyMode, false, "", null, "", "");
                    } else if (res.InvTallyMode == 2) {
                        if (res.Stockrefs != null) {
                            setTxtBoxHint(c.getString(R.string.stockref));
                            adapter_sr = new StockRef_Adapter(myApp.getAppContext(), res.Stockrefs);
                            lvInvMainTallySR.setAdapter(adapter_sr);
                            if (detectedStockRefsIDX >= 0) {
                                lvInvMainTallySR.post(new Runnable() {
                                    public void run() {
                                        try {
                                            //scroll the listview to the last item
                                            lvInvMainTallySR.setSelection(detectedStockRefsIDX);
                                            //focus certain view or any other calls after listview completed updating
                                        } catch (Exception e) {
                                            objGlobalMethods.writeToFile("Exception at InventoryMainActivity lvInvMainTallySR.setSelection(detectedStockRefsIDX);. Exception: " + e.getMessage(), "", "", "", "");
                                        }
                                    }
                                });
                            }
                            loaddata(res.invls, null);
                            //toggle(1, 0, res.InvTallyMode, false, "", -1, null, "", "");
                            toggle(1, 0, res.InvTallyMode, false, "", null, "", "");
                        } else {
                            setTxtBoxHint(c.getString(R.string.stockcode));
                            lblInvMainTallyQtyVBal.setText(String.valueOf(res.vbalance));
                            lblInvMainTallyQtyBal.setText(String.valueOf(res.balance));
                            //toggle(1, 0, res.InvTallyMode, false, "", -1, null, "", "");
                            toggle(1, 0, res.InvTallyMode, false, "", null, "", "");
                        }
                    } else {
                        //prompt qty or ref tally
                        //promptQtyOrRefTally(res.BatchID, res.invlidx, res.invl, sc, input);
                        promptQtyOrRefTally(res.BatchID, res.invl, sc, input);
                    }
                } else {
                    setTxtBoxHint(c.getString(R.string.stockcode));
                    dispTallyInvNotFound(input);
                    //toggle(1, 0, 0, false, "", -1, null, "", "");
                    toggle(1, 0, 0, false, "", null, "", "");
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity chkStockCodeDetailsNew onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    //private void promptQtyOrRefTally(final String BatchID, final int invlidx, final InventoryLite invl, final String sc, final String input) {
    private void promptQtyOrRefTally(final String BatchID, final InventoryLite invl, final String sc, final String input) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.qty_or_ref_tally));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.stock_ref_mode), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
                try {
                    //setInvlTallyMode sitm = new setInvlTallyMode(BatchID, invlidx, invl, 2, sc + "\n" + input, sc, input);
                    setInvlTallyModeNew sitm = new setInvlTallyModeNew(BatchID, invl, 2, sc + "\n" + input, sc, input);
                    sitm.execute();
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at InventoryMainActivity promptQtyOrRefTally. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        });


        builder.setNegativeButton(myApp.getAppContext().getString(R.string.qty_entry_mode), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
                //setInvlTallyMode sitm = new setInvlTallyMode(BatchID, invlidx, invl, 1, sc + "\n" + input, sc, input);
                setInvlTallyModeNew sitm = new setInvlTallyModeNew(BatchID, invl, 1, sc + "\n" + input, sc, input);
                sitm.execute();
            }
        });


        builder.setNeutralButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                try {
                    dialog.dismiss();
                    SharedPreferences g = objGlobalMethods.getGcmPreferences();
                    String[] prefName = new String[]{"InvMainMode", "InvSubMode", "InvTallyMode", "InvTallyQtyCanUndo"};
                    String[] prefvalue = new String[]{"1", "1", "0", ""};
                    objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                    lblInvMainTallylog.setText("");
                    adapter_sr = new StockRef_Adapter(myApp.getAppContext(), new ArrayList<StockRef>());
                    lvInvMainTallySR.setAdapter(adapter_sr);
                    //toggle(1, 1, 0, false, "", -1, null, "", "");
                    toggle(1, 1, 0, false, "", null, "", "");
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at InventoryMainActivity promptQtyOrRefTally NeutralButton Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        });

        promptQtyOrRefTallyAlert = builder.create();
        promptQtyOrRefTallyAlert.show();
    }

    public class setInvlTallyModeNew extends AsyncTask<Void, Void, InventoryLite> {
        private final String BatchID;
        private int invlselectedidx = -1;
        private InventoryLite invl;
        private final int InvTallyMode;
        private final String ref;
        private final String sc;
        private final String input;
        private ArrayList<InventoryLite> invls;

        public setInvlTallyModeNew(String aBatchID, InventoryLite ainvl, int aInvTallyMode, String aref, String asc, String ainput) {
            this.BatchID = aBatchID;
            this.invl = ainvl;
            this.InvTallyMode = aInvTallyMode;
            this.ref = aref;
            this.sc = asc;
            this.input = ainput;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            lblInvMainTallyStockCode.setText(myApp.getAppContext().getString(R.string.stockcodestart));
            LLInvMainTallyQty.setVisibility(View.GONE);
            LLInvMainTallySR.setVisibility(View.GONE);
            pdCheck.show();
        }

        @Override
        protected InventoryLite doInBackground(Void... params) {
            boolean asd = false;
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            String InventoryBatchIDStr = "";
            String InventoryBatchIDListJsonStr = "";
            ArrayList<InventoryBatchID> InventoryBatchIDList = null;
            InventoryBatchID ib = null;
            InventoryBatchID saved_ib = null;
            InventoryLite finvl = null;
            ArrayList<InventoryLite> invlss = null;
            InventoryLite invlloop = null;
            Gson gson = new Gson();
            int foundindx = -1;
            StockRef sr = null;
            int invlBal = 0;
            int invlvBal = 0;
            int invlvBalnew = 0;
            boolean reloadbal = false;

            try {
                InventoryBatchIDList = getSavedInventoryBatchIDList(g);
                if (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) {
                    for (int i = 0; i <= InventoryBatchIDList.size() - 1; i++) {
                        saved_ib = InventoryBatchIDList.get(i);
                        if (!objGlobalMethods.IsEmptyString(saved_ib.Batch_ID) && objGlobalMethods.IsEqualString(saved_ib.Batch_ID, BatchID)) {
                            foundindx = i;
                            ib = saved_ib;
                            invl.setInvTallyMode(InvTallyMode);
                            if (ib != null && ib.invls != null && ib.invls.size() > 0) {
                                for (int v = 0; v <= ib.invls.size() -1; v++) {
                                    invlloop = ib.invls.get(v);
                                    if (invlloop.IDX == invl.IDX) {
                                        invlselectedidx = v;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

                if (ib != null) {
                    if (InvTallyMode == 2 && invl.Stockrefs != null && invl.Stockrefs.size() > 0) {
                        for (int v = 0; v <= invl.Stockrefs.size() - 1; v++) {
                            sr = invl.Stockrefs.get(v);
                            if (sr != null && !objGlobalMethods.IsEmptyString(sr.StockReference) && objGlobalMethods.IsEqualString(sr.StockReference, input)) {
                                if (!sr.verified) {
                                    sr.setVerified(true);
                                    invlBal = invl.Balance;
                                    invlvBal = invl.vBalance;
                                    invlvBalnew = invlvBal + 1;
                                    invl.setvBalance(invlvBalnew);
                                    invl.setVFlag(invlBal == invlvBalnew ? 1 : 0);


                                    if (invlBal == invlvBalnew)
                                        invl.setHiddenStockCode("1_" + invl.StockCode);

                                    invl.Stockrefs.set(v, sr);
                                    reloadbal = true;
                                }


                                ib.addLogs((invl.Balance == invl.vBalance)
                                        ? "StockReference " + input + " verified." + "\n" + sc + " tally completed."
                                        : "StockReference " + input + " verified.");

                                objGlobalMethods.writeToFile(
                                        (invl.Balance == invl.vBalance)
                                                ? "StockReference " + input + " verified." + " " + sc + " tally completed."
                                                : "StockReference " + input + " verified."
                                        , "", "", "", "");

                                invl.Stockrefs.set(v, sr);
                                break;
                            }
                        }
                    }

                    ib.invls.set(invlselectedidx, invl);
                    if (reloadbal)
                        invls = ib.invls;
                    ib.addLogs((InvTallyMode == 1 ? "Qty Entry selected for " : "Reference Tally selected for ") + ref);
                    InventoryBatchIDStr = gson.toJson(ib);
                    InventoryBatchIDList.set(foundindx, ib);
                    InventoryBatchIDListJsonStr = gson.toJson(InventoryBatchIDList);

                    String[] prefName = new String[]{"Batch_ID", "InventoryBatchIDStr", "InvMainMode", "InvSubMode", "InventoryBatchIDListJsonStr", "InvTallyMode"};
                    String[] prefvalue = new String[]{Batch_ID, InventoryBatchIDStr, "1", "1", InventoryBatchIDListJsonStr, String.valueOf(InvTallyMode)};
                    objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                }

                ib = getInventoryBatchID_from_SavedInventoryBatchIDList_by_BatchID(g, BatchID);
                invl.setInvTallyMode(InvTallyMode);
                if (ib.invls != null && ib.invls.size() > 0 && invlselectedidx >= 0 && invlselectedidx <= ib.invls.size() - 1) {
                    ib.invls.set(invlselectedidx, invl);
                    finvl = invl;
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity setInvlTallyModeNew doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return finvl;
            }
        }

        @Override
        protected void onPostExecute(final InventoryLite res) {
            Context c = myApp.getAppContext();
            try {
                pdCheck.dismiss();
                if (res != null) {
                    playSuccessVerifiedTone();
                    //toggleToTallyNew(res, invlidx);
                    toggleToTallyNew(res);
                    if (invls != null)
                        loaddata(invls, null);
                } else {
                    dispTallyProcessErr();
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity setInvlTallyModeNew onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }

    }

    public class setInvlTallyMode extends AsyncTask<Void, Void, InventoryLite> {
        private final String BatchID;
        //private final int invlidx;
        private final int invlidx;
        private InventoryLite invl;
        private final int InvTallyMode;
        private final String ref;
        private final String sc;
        private final String input;
        private ArrayList<InventoryLite> invls;

        public setInvlTallyMode(String aBatchID, int ainvlidx, InventoryLite ainvl, int aInvTallyMode, String aref, String asc, String ainput) {
        //public setInvlTallyMode(String aBatchID, InventoryLite ainvl, int aInvTallyMode, String aref, String asc, String ainput) {
            this.BatchID = aBatchID;
            this.invlidx = ainvlidx;
            this.invl = ainvl;
            this.InvTallyMode = aInvTallyMode;
            this.ref = aref;
            this.sc = asc;
            this.input = ainput;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            lblInvMainTallyStockCode.setText(myApp.getAppContext().getString(R.string.stockcodestart));
            LLInvMainTallyQty.setVisibility(View.GONE);
            LLInvMainTallySR.setVisibility(View.GONE);
            pdCheck.show();
        }

        @Override
        protected InventoryLite doInBackground(Void... params) {
            boolean asd = false;
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            String InventoryBatchIDStr = "";
            String InventoryBatchIDListJsonStr = "";
            ArrayList<InventoryBatchID> InventoryBatchIDList = null;
            InventoryBatchID ib = null;
            InventoryBatchID saved_ib = null;
            InventoryLite finvl = null;
            Gson gson = new Gson();
            int foundindx = -1;
            StockRef sr = null;
            int invlBal = 0;
            int invlvBal = 0;
            int invlvBalnew = 0;
            boolean reloadbal = false;

            try {
                InventoryBatchIDList = getSavedInventoryBatchIDList(g);
                if (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) {
                    for (int i = 0; i <= InventoryBatchIDList.size() - 1; i++) {
                        saved_ib = InventoryBatchIDList.get(i);
                        if (!objGlobalMethods.IsEmptyString(saved_ib.Batch_ID) && objGlobalMethods.IsEqualString(saved_ib.Batch_ID, BatchID)) {
                            foundindx = i;
                            ib = saved_ib;
                            invl.setInvTallyMode(InvTallyMode);

                            if (InvTallyMode == 2 && invl.Stockrefs != null && invl.Stockrefs.size() > 0) {
                                for (int v = 0; v <= invl.Stockrefs.size() - 1; v++) {
                                    sr = invl.Stockrefs.get(v);
                                    if (sr != null && !objGlobalMethods.IsEmptyString(sr.StockReference) && objGlobalMethods.IsEqualString(sr.StockReference, input)) {
                                        if (!sr.verified) {
                                            sr.setVerified(true);
                                            invlBal = invl.Balance;
                                            invlvBal = invl.vBalance;
                                            invlvBalnew = invlvBal + 1;
                                            invl.setvBalance(invlvBalnew);
                                            invl.setVFlag(invlBal == invlvBalnew ? 1 : 0);


                                            if (invlBal == invlvBalnew)
                                                invl.setHiddenStockCode("1_" + invl.StockCode);

                                            invl.Stockrefs.set(v, sr);
                                            reloadbal = true;
                                        }


                                        ib.addLogs((invl.Balance == invl.vBalance)
                                                ? "StockReference " + input + " verified." + "\n" + sc + " tally completed."
                                                : "StockReference " + input + " verified.");

                                        objGlobalMethods.writeToFile(
                                                (invl.Balance == invl.vBalance)
                                                        ? "StockReference " + input + " verified." + " " + sc + " tally completed."
                                                        : "StockReference " + input + " verified."
                                                , "", "", "", "");

                                        invl.Stockrefs.set(v, sr);
                                        break;
                                    }
                                }
                            }

                            ib.invls.set(invlidx, invl);
                            if (reloadbal)
                                invls = ib.invls;
                            ib.addLogs((InvTallyMode == 1 ? "Qty Entry selected for " : "Reference Tally selected for ") + ref);
                            InventoryBatchIDStr = gson.toJson(ib);
                            InventoryBatchIDList.set(foundindx, ib);
                            InventoryBatchIDListJsonStr = gson.toJson(InventoryBatchIDList);

                            String[] prefName = new String[]{"Batch_ID", "InventoryBatchIDStr", "InvMainMode", "InvSubMode", "InventoryBatchIDListJsonStr", "InvTallyMode"};
                            String[] prefvalue = new String[]{Batch_ID, InventoryBatchIDStr, "1", "1", InventoryBatchIDListJsonStr, String.valueOf(InvTallyMode)};
                            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);

                            break;
                        }
                    }
                }

                ib = getInventoryBatchID_from_SavedInventoryBatchIDList_by_BatchID(g, BatchID);
                invl.setInvTallyMode(InvTallyMode);
                if (ib.invls != null && ib.invls.size() > 0 && invlidx >= 0 && invlidx <= ib.invls.size() - 1) {
                    ib.invls.set(invlidx, invl);
                    finvl = invl;
                    asd = true;
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity setInvlTallyMode doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return finvl;
            }
        }

        @Override
        protected void onPostExecute(final InventoryLite res) {
            Context c = myApp.getAppContext();
            try {
                pdCheck.dismiss();
                if (res != null) {
                    playSuccessVerifiedTone();
                    //toggleToTallyNew(res, invlidx);
                    toggleToTallyNew(res);
                    if (invls != null)
                        loaddata(invls, null);
                } else {
                    dispTallyProcessErr();
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity setInvlTallyMode onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }

    }

    private class chkStockCodeDetails extends AsyncTask<Void, Void, chkStockCodeDetailsResult> {
        private final String input;
        private String sc;

        public chkStockCodeDetails(String ainput) {
            this.input = ainput;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            lblInvMainTallyStockCode.setText(myApp.getAppContext().getString(R.string.stockcodestart));
            LLInvMainTallyQty.setVisibility(View.GONE);
            LLInvMainTallySR.setVisibility(View.GONE);
            pdCheck.show();
        }

        @Override
        protected chkStockCodeDetailsResult doInBackground(Void... params) {
            chkStockCodeDetailsResult res = null;
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            String InventoryBatchIDStr = "";
            InventoryBatchID ib = null;
            ArrayList<InventoryLite> invls = null;
            InventoryLite invl = null;
            Gson gson = new Gson();
            //String chkStockCodeDetailsResultJsonStr = "";
            String InvTallyMode = "";
            StockRef sr = null;
            int invlBal = 0;
            int invlvBal = 0;
            int invlvBalnew = 0;
            boolean updateInventoryBatchIDStr = false;
            String InventoryBatchIDListJsonStr = "";
            ArrayList<InventoryBatchID> InventoryBatchIDList = null;
            try {
                InventoryBatchIDStr = objGlobalMethods.getGCMPrefs(g, "InventoryBatchIDStr");

                if (!objGlobalMethods.IsEmptyString(InventoryBatchIDStr)) {
                    ib = new Gson().fromJson(InventoryBatchIDStr, InventoryBatchID.class);
                    if (ib != null && ib.invls != null && ib.invls.size() > 0) {
                        invls = ib.invls;
                        outer:
                        for (int i = 0; i <= invls.size() - 1; i++) {
                            invl = invls.get(i);
                            if (!objGlobalMethods.IsEmptyString(invl.StockCode) && objGlobalMethods.IsEqualString(invl.StockCode, input)) {
                                //if ((invl.Flag == 0) || ((invl.Flag & 1) == 1)) {
                                if (!((invl.Flag & 2) == 2) && !((invl.Flag & 4) == 4) && !((invl.Flag & 8) == 8) && !((invl.Flag & 16) == 16) && !((invl.Flag & 32) == 32) && !((invl.Flag & 64) == 64)) {
                                    //show enter quantity
                                    InvTallyMode = "1";
                                    //res = new chkStockCodeDetailsResult(invl.VFlag > 0 ? 2 : 1, null, invl.Balance, invl.prevBalance, invl.vBalance, invl.StockCode);
                                    res = new chkStockCodeDetailsResult(invl.vBalance >= invl.Balance ? 2 : 1, null, invl.Balance, invl.prevBalance, invl.vBalance, invl.StockCode);

                                    this.sc = this.input;
                                    ib.addLogs("StockCode " + input + " detected for Qty entry.");
                                    objGlobalMethods.writeToFile("StockCode " + input + " detected for Qty entry.", "", "", "", "");
                                    InventoryBatchIDStr = gson.toJson(ib);
                                    updateInventoryBatchIDStr = true;
                                    break outer;
                                } else {
                                    //show scan/enter stock ref
                                    if (invl.Stockrefs != null && invl.Stockrefs.size() > 0 && (invl.vBalance != invl.Balance)) {
                                        InvTallyMode = "2";
                                        res = new chkStockCodeDetailsResult(1, invl.Stockrefs, 0, 0, invl.vBalance, invl.StockCode);
                                        //chkStockCodeDetailsResultJsonStr = gson.toJson(res);
                                        this.sc = this.input;
                                        ib.addLogs("StockCode " + input + " detected for StockReference tally.");
                                        objGlobalMethods.writeToFile("StockCode " + input + " detected for StockReference tally.", "", "", "", "");
                                        InventoryBatchIDStr = gson.toJson(ib);
                                        updateInventoryBatchIDStr = true;
                                        break outer;
                                    } else if (invl.Stockrefs != null && invl.Stockrefs.size() > 0 && (invl.vBalance == invl.Balance)) {
                                        res = new chkStockCodeDetailsResult(2, null, 0, 0, invl.vBalance, invl.StockCode);
                                        InvTallyMode = "2";
                                        this.sc = this.input;
                                        ib.addLogs("StockCode " + input + " detected. Inventory tallied.");
                                        objGlobalMethods.writeToFile("StockCode " + input + " detected. Inventory tallied.", "", "", "", "");
                                        InventoryBatchIDStr = gson.toJson(ib);
                                        updateInventoryBatchIDStr = true;
                                        break outer;
                                        //promptItemsTallied(sc);
                                    } else {
                                        InvTallyMode = "1";
                                        res = new chkStockCodeDetailsResult(invl.vBalance >= invl.Balance ? 2 : 1, null, invl.Balance, invl.prevBalance, invl.vBalance, invl.StockCode);
                                        this.sc = this.input;
                                        ib.addLogs("StockCode " + input + " detected for StockReference tally. No Stock Ref found. Switch to Qty entry.");
                                        objGlobalMethods.writeToFile("StockCode " + input + " detected for StockReference tally. No Stock Ref found. Switch to Qty entry.", "", "", "", "");
                                        InventoryBatchIDStr = gson.toJson(ib);
                                        updateInventoryBatchIDStr = true;
                                        break outer;
                                        /*res = new chkStockCodeDetailsResult(3, null, 0, 0, invl.vBalance, invl.StockCode);
                                        InvTallyMode = "2";
                                        this.sc = this.input;
                                        ib.addLogs("StockCode " + input + " detected. StockReference not found.");
                                        objGlobalMethods.writeToFile("StockCode " + input + " detected. StockReference not found.","","","","");
                                        InventoryBatchIDStr = gson.toJson(ib);
                                        updateInventoryBatchIDStr = true;
                                        break outer;*/

                                    }
                                }
                            } else if (invl.Stockrefs != null && invl.Stockrefs.size() > 0) {
                                //check thru stockref
                                for (int v = 0; v <= invl.Stockrefs.size() - 1; v++) {
                                    sr = invl.Stockrefs.get(v);
                                    if (sr != null && !objGlobalMethods.IsEmptyString(sr.StockReference) && objGlobalMethods.IsEqualString(sr.StockReference, input)) {
                                        if (!sr.verified) {
                                            sr.setVerified(true);
                                            invlBal = invl.Balance;
                                            invlvBal = invl.vBalance;
                                            invlvBalnew = invlvBal + 1;
                                            invls.get(i).setvBalance(invlvBalnew);
                                            invls.get(i).setVFlag(invlBal == invlvBalnew ? 1 : 0);
                                            if (invlBal == invlvBalnew)
                                                invls.get(i).setHiddenStockCode("1_" + invl.StockCode);

                                            invls.get(i).Stockrefs.set(v, sr);
                                            ib.setInventory(invls);
                                            /*ib.addLogs("StockReference " + input + " verified.");
                                            InventoryBatchIDStr = gson.toJson(ib);
                                            updateInventoryBatchIDStr = true;*/
                                        }

                                        this.sc = invl.StockCode;

                                        ib.addLogs((invl.Balance == invl.vBalance)
                                                ? "StockReference " + input + " verified." + "\n" + sc + " tally completed."
                                                : "StockReference " + input + " verified.");
                                        objGlobalMethods.writeToFile(
                                                (invl.Balance == invl.vBalance)
                                                        ? "StockReference " + input + " verified." + " " + sc + " tally completed."
                                                        : "StockReference " + input + " verified."
                                                , "", "", "", "");
                                        InventoryBatchIDStr = gson.toJson(ib);
                                        updateInventoryBatchIDStr = true;

                                        invl.Stockrefs.set(v, sr);
                                        InvTallyMode = "2";
                                        res = new chkStockCodeDetailsResult(4, invl.Stockrefs, invl.Balance, 0, invl.vBalance, invl.StockCode);
                                        res.setInvls(ib.invls);
                                        //chkStockCodeDetailsResultJsonStr = gson.toJson(res);
                                        break outer;
                                    }
                                }
                            }
                        }

                        if (res == null) {
                            ib.addLogs(input + " detected.\nNot Found.");
                            InventoryBatchIDStr = gson.toJson(ib);
                            updateInventoryBatchIDStr = true;
                        }
                    } else {
                        if (ib != null) {
                            ib.addLogs("Unable to process " + input);
                            updateInventoryBatchIDStr = true;
                        }
                        objGlobalMethods.writeToFile("Unable to process InventoryMainActivity chkStocKCodeDetails due (ib == null || ib.invls == null || ib.invls.size() == 0).", "", "", "", "");
                    }
                } else {
                    objGlobalMethods.writeToFile("Unable to process InventoryMainActivity chkStocKCodeDetails due InventoryBatchIDStr is empty.", "", "", "", "");
                }

                if (updateInventoryBatchIDStr) {
                    InventoryBatchIDList = getSavedInventoryBatchIDList(g);
                    if (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) {
                        int currInventoryBatchID_ListIndex = getSavedInventoryBatchID_ListIndex(ib.Batch_ID, InventoryBatchIDList);
                        if (currInventoryBatchID_ListIndex > -1) {
                            InventoryBatchIDList.set(currInventoryBatchID_ListIndex, ib);
                            InventoryBatchIDListJsonStr = gson.toJson(InventoryBatchIDList);
                            String[] prefName = new String[]{"InvTallyMode", "InvTallyQtyCanUndo", "InvSubMode", "InventoryBatchIDStr", "InventoryBatchIDListJsonStr"};
                            String[] prefvalue = new String[]{InvTallyMode, "", "0", InventoryBatchIDStr, InventoryBatchIDListJsonStr};
                            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                        } else {
                            String[] prefName = new String[]{"InvTallyMode", "InvTallyQtyCanUndo", "InvSubMode", "InventoryBatchIDStr"};
                            String[] prefvalue = new String[]{InvTallyMode, "", "0", InventoryBatchIDStr};
                            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                        }
                    } else {
                        String[] prefName = new String[]{"InvTallyMode", "InvTallyQtyCanUndo", "InvSubMode", "InventoryBatchIDStr"};
                        String[] prefvalue = new String[]{InvTallyMode, "", "0", InventoryBatchIDStr};
                        objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                    }
                } else {
                    String[] prefName = new String[]{"InvTallyMode", "InvTallyQtyCanUndo", "InvSubMode"};
                    String[] prefvalue = new String[]{InvTallyMode, "", "0"};
                    objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                }

                /*if (updateInventoryBatchIDStr) {
                    String[] prefName = new String[]{"chkStockCodeDetailsResult", "InvTallyMode", "InvTallyQtyCanUndo", "InvSubMode", "InventoryBatchIDStr"};
                    String[] prefvalue = new String[]{chkStockCodeDetailsResultJsonStr, InvTallyMode, "", "0", InventoryBatchIDStr};
                    objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                } else {
                    String[] prefName = new String[]{"chkStockCodeDetailsResult", "InvTallyMode", "InvTallyQtyCanUndo", "InvSubMode"};
                    String[] prefvalue = new String[]{chkStockCodeDetailsResultJsonStr, InvTallyMode, "", "0"};
                    objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                }*/

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity chkStockCodeDetails doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final chkStockCodeDetailsResult res) {
            Context c = myApp.getAppContext();
            try {
                pdCheck.dismiss();
                if (res != null && res.status == 3) {
                    dispTallyStockRefNotFound();
                    setTxtBoxHint(c.getString(R.string.stockcode));
                } else if (res != null && (res.status == 1 || res.status == 2)) {
                    playSuccessVerifiedTone();
                    lblInvMainTallyStockCode.setText(sc);

                    lblInvMainTallylog.setText(res.status == 2 ? (objGlobalMethods.IsEmptyString(sc) ? "" : sc + " ") + c.getString(R.string.items_tallied) : "");

                    if (res.Stockrefs != null) {
                        setTxtBoxHint(c.getString(R.string.stockref));
                        adapter_sr = new StockRef_Adapter(myApp.getAppContext(), res.Stockrefs);
                        lvInvMainTallySR.setAdapter(adapter_sr);
                        //toggle(1, 0, 2, false, "", -1, null, "", "");
                        toggle(1, 0, 2, false, "", null, "", "");
                    } else {
                        setTxtBoxHint(c.getString(R.string.stockcode));
                        lblInvMainTallyQtyVBal.setText(String.valueOf(res.vbalance));
                        lblInvMainTallyQtyBal.setText(String.valueOf(res.balance));
                        //toggle(1, 0, 1, false, "", -1, null, "", "");
                        toggle(1, 0, 1, false, "", null, "", "");
                    }
                } else if (res != null && res.status == 4) {
                    setTxtBoxHint(c.getString(R.string.stockcode));
                    lblInvMainTallyStockCode.setText(sc);
                    if (res.balance == res.vbalance) {
                        dispTallied(sc);
                    } else {
                        dispTallyStockRefVerified(input);
                    }

                    if (res.Stockrefs != null) {
                        adapter_sr = new StockRef_Adapter(myApp.getAppContext(), res.Stockrefs);
                        lvInvMainTallySR.setAdapter(adapter_sr);
                        loaddata(res.invls, null);
                        //toggle(1, 0, 2, false, "", -1, null, "", "");
                        toggle(1, 0, 2, false, "", null, "", "");
                    }

                } else {
                    setTxtBoxHint(c.getString(R.string.stockcode));
                    dispTallyInvNotFound(input);
                    //toggle(1, 0, 0, false, "", -1, null, "", "");
                    toggle(1, 0, 0, false, "", null, "", "");
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity chkStockCodeDetails onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    public void GetSavedInventoryBatchByBatchID(String ABatchID) {
        loadSavedInventoryBatchByBatchID t = new loadSavedInventoryBatchByBatchID(ABatchID);
        t.execute();
    }

    private class loadSavedInventoryBatchByBatchID extends AsyncTask<Void, Void, InventoryBatchID> {
        private final String ABatchID;
        private int foundindx;
        private ArrayList<InventoryBatchID> InventoryBatchIDList = null;

        public loadSavedInventoryBatchByBatchID(String abid) {
            this.ABatchID = abid;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdCheck.show();
        }

        @Override
        protected InventoryBatchID doInBackground(Void... params) {
            ArrayList<InventoryBatchID> AInventoryBatchIDList = null;
            InventoryBatchID saved_ib = null;
            InventoryBatchID ib = null;
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            try {
                AInventoryBatchIDList = getSavedInventoryBatchIDList(g);

                if (AInventoryBatchIDList != null && AInventoryBatchIDList.size() > 0) {
                    for (int i = 0; i <= AInventoryBatchIDList.size() - 1; i++) {
                        saved_ib = AInventoryBatchIDList.get(i);
                        if (!objGlobalMethods.IsEmptyString(saved_ib.Batch_ID) && objGlobalMethods.IsEqualString(saved_ib.Batch_ID, ABatchID)) {
                            this.foundindx = i;
                            ib = saved_ib;
                            this.InventoryBatchIDList = AInventoryBatchIDList;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity loadSavedInventoryBatchByBatchID doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return ib;
            }
        }

        @Override
        protected void onPostExecute(final InventoryBatchID ib) {
            Context ctx = myApp.getAppContext();
            String InventoryBatchIDStr = "";
            String InventoryBatchIDListJsonStr = "";
            ArrayList<InventoryBatchID> InventoryBatchIDList = null;
            Gson gson = new Gson();
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            try {
                pdCheck.dismiss();
                if (ib != null) {
                    ib.addLogs(ABatchID + " BatchID selected.");
                    InventoryBatchIDStr = gson.toJson(ib);
                    this.InventoryBatchIDList.set(this.foundindx, ib);
                    InventoryBatchIDListJsonStr = gson.toJson(this.InventoryBatchIDList);

                    String[] prefName = new String[]{"Batch_ID", "InventoryBatchIDStr", "InvMainMode", "InvSubMode", "InventoryBatchIDListJsonStr"};
                    String[] prefvalue = new String[]{ib != null ? ABatchID : "", InventoryBatchIDStr, ib != null ? "1" : "0", ib != null ? "1" : "0", InventoryBatchIDListJsonStr};
                    objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);

                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnInvMainSummary);
                    objGlobalMethods.writeToFile("Download data for Batch ID: " + ABatchID + " successful.", "", "", "", "");
                    lblMainBlankLog.setText("");
                    playSuccessVerifiedTone();
                    focusTextbox();
                    setTxtBoxHint(ctx.getString(R.string.stockcode));
                    lblBatch_ID.setText(ib.Batch_ID);
                    loaddata(ib.invls, null);
                } else {
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnInvMainSummary);
                    playTone();
                    focusTextbox();
                    setTxtBoxHint(ctx.getString(R.string.batch_id));
                    lblBatch_ID.setText("");
                    //toggle(0, 0, 0, false, "", -1, null, "", "");
                    toggle(0, 0, 0, false, "", null, "", "");
                    lblMainBlankLog.setText(ctx.getString(R.string.err_retrieve_inventory_batch) + " " + ABatchID);
                    objGlobalMethods.writeToFile("Error downloading data for Batch ID: " + ABatchID, "", "", "", "");
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity loadSavedInventoryBatchByBatchID onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    public class GetInventoryBatch extends AsyncTask<Void, Void, InventoryBatchID> {
        private final String ABatchID;

        public GetInventoryBatch(String aABatchID) {
            this.ABatchID = aABatchID;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdCheck.show();
        }

        @Override
        protected InventoryBatchID doInBackground(Void... params) {
            ArrayList<InventoryLite> invls = null;
            InventoryLite invl = null;
            Inventory[] invs = null;
            Inventory inv = null;
            Context ctx = myApp.getAppContext();
            String InventoryBatchIDListJsonStr = "";
            ArrayList<InventoryBatchID> InventoryBatchIDList = null;
            InventoryBatchID saved_ib = null;
            InventoryBatchID ib = null;
            InventoryDetails[] ids = null;
            InventoryDetails id = null;
            String InventoryBatchIDStr = "";
            String[] StockReferences = null;
            String ref = "";
            ArrayList<StockRef> srs = null;
            StockRef sr = null;
            Gson gson = new Gson();
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            int foundindx = -1;
            try {
                InventoryBatchIDList = getSavedInventoryBatchIDList(g);

                if (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) {
                    for (int i = 0; i <= InventoryBatchIDList.size() - 1; i++) {
                        saved_ib = InventoryBatchIDList.get(i);
                        if (!objGlobalMethods.IsEmptyString(saved_ib.Batch_ID) && objGlobalMethods.IsEqualString(saved_ib.Batch_ID, ABatchID)) {
                            foundindx = i;
                            ib = saved_ib;
                            break;
                        }
                    }
                }

                if (ib == null) {

                    String HOMESAPI = ctx.getString(R.string.action_HOMESAPI);
                    invs = WebService.GetInventoryBatch(HOMESAPI, ABatchID);

                    if (invs != null && invs.length > 0) {
                        for (int i = 0; i <= invs.length - 1; i++) {
                            inv = invs[i];
                            if (invls == null)
                                invls = new ArrayList<>();

                            invl = new InventoryLite();
                            invl.setBatch_ID(ABatchID);
                            invl.setIDX(inv.IDX);
                            invl.setStockCode(inv.StockCode);
                            invl.setHiddenStockCode("0_" + inv.StockCode);
                            invl.setDescription(inv.Description);
                            invl.setBarcode(inv.Barcode);
                            invl.setBalance(inv.Balance);
                            //invl.setvBalance(i < 9 ? i+1 : (i >= 10 && i <= 19) ? inv.Balance : 0);
                            invl.setFlag(inv.Flag);
                            invl.setInvTallyMode(
                                    (!((invl.Flag & 2) == 2) && !((invl.Flag & 4) == 4) && !((invl.Flag & 8) == 8) && !((invl.Flag & 16) == 16) && !((invl.Flag & 32) == 32) && !((invl.Flag & 64) == 64))
                                            ? 1 : 3
                            );

                            if (inv.StockMovement != null && inv.StockMovement.length > 0) {
                                for (int s = 0; s <= inv.StockMovement.length - 1; s++) {
                                    id = inv.StockMovement[s];
                                    if (id.StockReferences != null && id.StockReferences.length > 0) {
                                        for (int r = 0; r <= id.StockReferences.length - 1; r++) {
                                            ref = id.StockReferences[r];
                                            invl.addStockRefByStringRef(ref);
                                        }
                                    }
                                }
                            }
                            invls.add(invl);
                        }
                    }

                    if (invls != null && invls.size() > 0) {
                        ib = new InventoryBatchID(ABatchID, invls);
                        ib.addLogs(ABatchID + " BatchID detected.");
                        InventoryBatchIDStr = gson.toJson(ib);
                    }

                    if (InventoryBatchIDList == null)
                        InventoryBatchIDList = new ArrayList<>();

                    if (ib != null)
                        InventoryBatchIDList.add(ib);

                    InventoryBatchIDListJsonStr = (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) ? gson.toJson(InventoryBatchIDList) : "";
                } else {
                    ib.addLogs(ABatchID + " BatchID detected.");
                    InventoryBatchIDStr = gson.toJson(ib);
                    InventoryBatchIDList.set(foundindx, ib);
                    InventoryBatchIDListJsonStr = gson.toJson(InventoryBatchIDList);
                }

                String[] prefName = new String[]{"Batch_ID", "InventoryBatchIDStr", "InvMainMode", "InvSubMode", "InventoryBatchIDListJsonStr"};
                String[] prefvalue = new String[]{ib != null ? ABatchID : "", InventoryBatchIDStr, ib != null ? "1" : "0", ib != null ? "1" : "0", InventoryBatchIDListJsonStr};
                objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity GetInventoryBatch doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return ib;
            }
        }

        @Override
        protected void onPostExecute(final InventoryBatchID ib) {
            Context ctx = myApp.getAppContext();
            try {
                pdCheck.dismiss();
                if (ib == null) {
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnInvMainSummary);
                    playTone();
                    focusTextbox();
                    setTxtBoxHint(ctx.getString(R.string.batch_id));
                    lblBatch_ID.setText("");
                    //toggle(0, 0, 0, false, "", -1, null, "", "");
                    toggle(0, 0, 0, false, "", null, "", "");
                    lblMainBlankLog.setText(ctx.getString(R.string.err_retrieve_inventory_batch) + " " + ABatchID);
                    objGlobalMethods.writeToFile("Error downloading data for Batch ID: " + ABatchID, "", "", "", "");
                } else {
                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnInvMainSummary);
                    objGlobalMethods.writeToFile("Download data for Batch ID: " + ABatchID + " successful.", "", "", "", "");
                    lblMainBlankLog.setText("");
                    playSuccessVerifiedTone();
                    focusTextbox();
                    setTxtBoxHint(ctx.getString(R.string.stockcode));
                    lblBatch_ID.setText(ib.Batch_ID);
                    loaddata(ib.invls, null);
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at InventoryMainActivity GetInventoryBatch onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    public ArrayList<InventoryBatchID> getSavedInventoryBatchIDList(SharedPreferences ag) {
        String InventoryBatchIDListJsonStr = "";
        ArrayList<InventoryBatchID> InventoryBatchIDList = null;
        try {
            if (ag == null)
                ag = objGlobalMethods.getGcmPreferences();

            InventoryBatchIDListJsonStr = objGlobalMethods.getGCMPrefs(ag, "InventoryBatchIDListJsonStr");
            if (!objGlobalMethods.IsEmptyString(InventoryBatchIDListJsonStr)) {
                Gson gson = new Gson();
                InventoryBatchIDList = gson.fromJson(InventoryBatchIDListJsonStr, new TypeToken<List<InventoryBatchID>>() {
                }.getType());
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity getSavedInventoryBatchIDList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return InventoryBatchIDList;
        }
    }

    public InventoryBatchID getInventoryBatchID_from_SavedInventoryBatchIDList_by_BatchID(SharedPreferences ag, String BatchID) {
        InventoryBatchID ib = null;
        String InventoryBatchIDListJsonStr = "";
        ArrayList<InventoryBatchID> InventoryBatchIDList = null;
        try {
            if (ag == null)
                ag = objGlobalMethods.getGcmPreferences();

            InventoryBatchIDListJsonStr = objGlobalMethods.getGCMPrefs(ag, "InventoryBatchIDListJsonStr");
            if (!objGlobalMethods.IsEmptyString(InventoryBatchIDListJsonStr)) {
                Gson gson = new Gson();
                InventoryBatchIDList = gson.fromJson(InventoryBatchIDListJsonStr, new TypeToken<List<InventoryBatchID>>() {
                }.getType());
            }

            if (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) {
                for (int i = 0; i <= InventoryBatchIDList.size() - 1; i++) {
                    ib = InventoryBatchIDList.get(i);
                    if (objGlobalMethods.IsEqualString(ib.Batch_ID, BatchID)) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity getInventoryBatchID_from_SavedInventoryBatchIDList_by_BatchID. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return ib;
        }
    }

    public int getSavedInventoryBatchID_ListIndex(String BatchID, ArrayList<InventoryBatchID> InventoryBatchIDList) {
        int index = -1;
        InventoryBatchID ib = null;
        try {
            if (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) {
                for (int i = 0; i <= InventoryBatchIDList.size() - 1; i++) {
                    ib = InventoryBatchIDList.get(i);
                    if (ib != null && !objGlobalMethods.IsEmptyString(ib.Batch_ID) && objGlobalMethods.IsEqualString(ib.Batch_ID, BatchID)) {
                        index = i;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity getSavedInventoryBatchID_ListIndex doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return index;
        }
    }

    public void focusTextbox() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                txtInvMainBarcodeMANUALENTRY.setText("");
                txtInvMainBarcodeMANUALENTRY.requestFocus();
            }
        }, 100);
    }

    public void focustxtInvMainTallyQty() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                txtInvMainTallyQty.setText("");
                txtInvMainTallyQty.requestFocus();
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

    private void dispTallyProcessErr() {
        try {
            playTone();
            Context c = myApp.getAppContext();
            lblInvMainTallylog.setText(c.getString(R.string.unableprocess));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity dispTallyProcessErr. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void dispTallied(String sc) {
        try {
            playSuccessVerifiedTone();
            Context c = myApp.getAppContext();
            lblInvMainTallylog.setText((objGlobalMethods.IsEmptyString(sc) ? "" : sc + " ") + c.getString(R.string.items_tallied));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity dispTallied. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void dispTallyStockRefNotFound() {
        try {
            playTone();
            Context c = myApp.getAppContext();
            lblInvMainTallylog.setText(c.getString(R.string.stockref_not_found));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity dispTallyStockRefNotFound. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void dispTallyInvNotFound(String input) {
        try {
            playTone();
            Context c = myApp.getAppContext();
            lblInvMainTallylog.setText((!objGlobalMethods.IsEmptyString(input) ? (input + " ") : "") + c.getString(R.string.inventories_not_found));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity dispTallyInvNotFound. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void dispTallyStockRefNotBelongToStockCode(String stockref) {
        try {
            playTone();
            Context c = myApp.getAppContext();
            lblInvMainTallylog.setText((!objGlobalMethods.IsEmptyString(stockref) ? stockref + " " : "") +
                    c.getString(R.string.stockref_not_belong));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity dispTallyStockRefNotBelongToStockCode. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void dispTallyStockRefVerified(String stockref) {
        try {
            playSuccessVerifiedTone();
            Context c = myApp.getAppContext();
            lblInvMainTallylog.setText((!objGlobalMethods.IsEmptyString(stockref) ? stockref + " " : "") +
                    c.getString(R.string.item_verified));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity dispTallyStockRefVerified. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void promptErrProcessing() {
        try {
            playTone();
            Context c = myApp.getAppContext();
            objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.unableprocess)).show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity promptErrProcessing. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void promptInvNotFound() {
        try {
            playTone();
            Context c = myApp.getAppContext();
            objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.inventories_not_found)).show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity promptInvNotFound. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void promptStockRefNotFound() {
        try {
            playTone();
            Context c = myApp.getAppContext();
            objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.stockref_not_found)).show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity promptStockRefNotFound. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void promptItemsTallied(String sc) {
        try {
            playTone();
            Context c = myApp.getAppContext();
            objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), (objGlobalMethods.IsEmptyString(sc) ? "" : sc + " ") + c.getString(R.string.items_tallied)).show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity promptItemsTallied. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void setTxtBoxHint(String x) {
        txtInvMainBarcodeMANUALENTRY.setHint(x);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at InventoryMainActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryMainActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}