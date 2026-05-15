package com.xdel.luke.inventory;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.BottomSheet.securitySealDialog;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class InventorySummaryActivity extends AppCompatActivity {
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

    private SharedPreferences g, UpdateJobInfoPreferences;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    private ProgressDialog pd, pdCheck;
    ListView lvInvSummary;
    private TextView lblBatch_ID, lblInvMainUserIDX, lblInvMainDriverIDX, lblIsDefaultVRPpage, lblInvMainSubmitIsHomesUser;
    private Button btnInvSummaryCANCEL, btnInvSummary;
    private Inventory_Summary_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        langType = objGlobalMethods.getGCMPrefs(g, "language");
        setContentView(R.layout.activity_inventory_summary);

        lvInvSummary = findViewById(R.id.lvInvSummary);
        lblInvMainUserIDX = findViewById(R.id.lblInvMainUserIDX);
        lblInvMainDriverIDX = findViewById(R.id.lblInvMainDriverIDX);
        btnInvSummaryCANCEL = findViewById(R.id.btnInvSummaryCANCEL);
        btnInvSummary = findViewById(R.id.btnInvSummary);
        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);
        lblInvMainSubmitIsHomesUser = findViewById(R.id.lblInvMainSubmitIsHomesUser);
        lblBatch_ID = findViewById(R.id.lblBatch_ID);


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
            g = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(g)) {
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            context = myApp.getAppContext();

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(g);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            Boolean IsHOMESuser = objGlobalMethods.IsHomesUserString(ishomesuser, g);
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
                objGlobalMethods.writeToFile("InventorySummaryActivity !BTEnabled", "", "", "", "");
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

            objGlobalMethods.setGCMPrefs(g, "CannotTakePhoto", "");
            initPage(IsHOMESuser);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        g = null;
        UpdateJobInfoPreferences = null;
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at InventorySummaryActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at InventorySummaryActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        Intent parentIntent = new Intent(this, DrawerMainActivity.class);
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(g);
        ishomesuser = UsernamePwd[3];

        parentIntent = new Intent(this, InventoryMainActivity.class);

        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    private void backToMain(){
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

    private void backToInventoryMainActivity() {
        Intent i = new Intent(myApp.currentActivity, InventoryMainActivity.class);
        startActivity(i);
        this.finish();
    }

    private void initPage(boolean IsHOMESuser) {
        String InvMainMode = "";
        String InventoryBatchIDStr = "";
        g = objGlobalMethods.getGcmPreferences();
        InventoryBatchID ib = null;
        ArrayList<InventoryLite> invls = null;
        try {
            String Batch_ID = objGlobalMethods.getGCMPrefs(g, "Batch_ID");
            setTitle(objGlobalMethods.IsEmptyString(Batch_ID) ? "Batch_ID?" : Batch_ID);

            lblInvMainSubmitIsHomesUser.setText(IsHOMESuser ? "true" : "false");
            InventoryBatchIDStr = objGlobalMethods.getGCMPrefs(g, "InventoryBatchIDStr");
            if (!objGlobalMethods.IsEmptyString(InventoryBatchIDStr))
                ib = new Gson().fromJson(InventoryBatchIDStr, InventoryBatchID.class);

            if (ib != null) {
                invls = ib.invls;
                if (invls != null && invls.size() > 0) {
                    Collections.sort(invls,
                            Comparator.comparing(InventoryLite::getHiddenStockCode)
                    );
                    adapter = new Inventory_Summary_Adapter(this, invls);
                    lvInvSummary.setAdapter(adapter);
                } else {
                    objGlobalMethods.writeToFile("Unable to load page at InventorySummaryActivity initPage. invls == null || invls.size() == 0;","","","","");
                    showErrorMsgAndReturnBackToTallyPage();
                }
            } else {
                objGlobalMethods.writeToFile("Unable to load page at InventorySummaryActivity initPage. ib == null.","","","","");
                showErrorMsgAndReturnBackToTallyPage();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventorySummaryActivity initPage. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private class submit extends AsyncTask<Void, Void, Boolean> {
        public submit(){

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdCheck.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean asd = false;
            Context c = myApp.getAppContext();
            UploadStockVerificationList stockVerList = new UploadStockVerificationList();
            String InventoryBatchIDStr = "";
            g = objGlobalMethods.getGcmPreferences();
            InventoryBatchID ib = null;
            ArrayList<InventoryLite> invls = null;
            InventoryLite invl = null;
            StockVerificationList svl = null;
            VerifiedItem[] viArr = null;
            ArrayList<VerifiedItem> vilist = null;
            VerifiedItem vi = null;
            String[] logsArr = null;
            ArrayList<String> logslist = null;
            String[] vStockRefsArr = null;
            ArrayList<String> vStockRefs = null;
            ArrayList<StockRef> srs = null;
            StockRef sr = null;
            String BatchID = "";
            ArrayList<InventoryBatchID> SavedInventoryBatchIDList = null;
            String InventoryBatchIDListJsonStr = "";
            try {
                InventoryBatchIDStr = objGlobalMethods.getGCMPrefs(g, "InventoryBatchIDStr");
                if (!objGlobalMethods.IsEmptyString(InventoryBatchIDStr))
                    ib = new Gson().fromJson(InventoryBatchIDStr, InventoryBatchID.class);

                if (ib != null && ib.invls != null && ib.invls.size() > 0) {
                    invls = ib.invls;
                    for (int i = 0; i <= invls.size() -1; i++) {
                        vStockRefs = new ArrayList<>();
                        invl = invls.get(i);
                        if (invl.Stockrefs != null && invl.Stockrefs.size() > 0) {
                            srs = invl.Stockrefs;
                            for (int v = 0; v <= srs.size() -1; v++) {
                                sr = srs.get(v);
                                if (sr.verified) {
                                    vStockRefs.add(sr.StockReference);
                                }
                            }
                        }

                        vStockRefsArr = vStockRefs != null && vStockRefs.size() > 0 ? vStockRefs.toArray(new String[vStockRefs.size()]) : null;
                        vi = new VerifiedItem(invl.IDX, invl.vBalance, vStockRefsArr);
                        if (vilist == null)
                            vilist = new ArrayList<>();
                        vilist.add(vi);
                    }

                    if (vilist != null && vilist.size() > 0)
                        viArr = vilist.toArray(new VerifiedItem[vilist.size()]);

                    logslist = ib.logs;
                    if (logslist != null && logslist.size() > 0)
                        logsArr = logslist.toArray(new String[logslist.size()]);

                    svl = new StockVerificationList();
                    svl.setBatch_ID(ib.Batch_ID);
                    svl.setItems(viArr);
                    svl.setLogs(logsArr);

                    stockVerList.setAPIKey(c.getString(R.string.action_HOMESAPI));
                    stockVerList.setStockVerificationList(svl);
                    stockVerList.setAUserIDX(!objGlobalMethods.IsEmptyString(lblInvMainUserIDX.getText().toString()) ? Long.parseLong(lblInvMainUserIDX.getText().toString()) : 0);
                    stockVerList.setADriverIDX(!objGlobalMethods.IsEmptyString(lblInvMainDriverIDX.getText().toString()) ? Long.parseLong(lblInvMainDriverIDX.getText().toString()) : 0);

                    if (stockVerList != null) {
                        JobsHandler.addUploadStockVerificationListQueALPrefs(stockVerList);
                        sendBroadcast(new Intent(c, BackendRun_BroadcastReceiver.class));
                        asd = true;
                        SavedInventoryBatchIDList = getSavedInventoryBatchIDList(g);
                        if (SavedInventoryBatchIDList != null && SavedInventoryBatchIDList.size() > 0) {
                            int currInventoryBatchID_ListIndex = getSavedInventoryBatchID_ListIndex(ib.Batch_ID, SavedInventoryBatchIDList);
                            if (currInventoryBatchID_ListIndex > -1) {
                                SavedInventoryBatchIDList.remove(currInventoryBatchID_ListIndex);
                                InventoryBatchIDListJsonStr = SavedInventoryBatchIDList.size() == 0 ? "" : new Gson().toJson(SavedInventoryBatchIDList);
                                String[] prefName = new String[]{"Batch_ID", "InvMainMode", "InvSubMode", "InventoryBatchIDStr", "InventoryBatchIDListJsonStr", "StockCodeMode"};
                                String[] prefvalue = new String[]{"", "0", "0", "", InventoryBatchIDListJsonStr, ""};
                                objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                            } else {
                                String[] prefName = new String[]{"Batch_ID", "InvMainMode", "InvSubMode", "InventoryBatchIDStr", "StockCodeMode"};
                                String[] prefvalue = new String[]{"", "0", "0", "", ""};
                                objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                            }
                        } else {
                            String[] prefName = new String[]{"Batch_ID", "InvMainMode", "InvSubMode", "InventoryBatchIDStr", "StockCodeMode"};
                            String[] prefvalue = new String[]{"", "0", "0", "", ""};
                            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                        }
                    }
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exeption at InventorySummaryActivity submit doInbackground. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean asd) {
            try {
                pdCheck.dismiss();
                if (asd) {
                    playSuccessVerifiedTone();
                    showDataSubmittedPrompt();
                } else {
                    playTone();
                    showErrorProcessingMsg();
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exeption at InventorySummaryActivity submit onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
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

    public int getSavedInventoryBatchID_ListIndex(String BatchID, ArrayList<InventoryBatchID> InventoryBatchIDList) {
        int index = -1;
        InventoryBatchID ib = null;
        try {
            if (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) {
                for (int i = 0; i <= InventoryBatchIDList.size() -1; i++) {
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

    public void btnInvSummaryCANCEL_Click(View v) {
        back();
    }

    public void btnInvSummary_Click(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.cfm_submit_inv_summary));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
                submit sub = new submit();
                sub.execute();
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

    private void showErrorMsgAndReturnBackToTallyPage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_errorretrivedata2));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
                back();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDataSubmittedPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.data_submitted));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
                backToInventoryMainActivity();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showErrorProcessingMsg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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
            objGlobalMethods.writeToFile("Exception at InventorySummaryActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at InventorySummaryActivity playSuccessVerifiedTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at InventorySummaryActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at InventorySummaryActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at InventorySummaryActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at InventorySummaryActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at InventorySummaryActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at InventorySummaryActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventorySummaryActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}