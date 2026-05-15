package com.xdel.luke.Locker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.xdel.luke.AddressListNewActivity;
import com.xdel.luke.AddressListRoutingActivity;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.Camera5Activity;
import com.xdel.luke.CheckListActivity;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.HRM.objPassQR_IDbarcode;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsListGroupActivity;
import com.xdel.luke.MainActivity;
import com.xdel.luke.MainCODActivity;
import com.xdel.luke.MapsAllActivity;
import com.xdel.luke.Messaging;
import com.xdel.luke.QR.Contents;
import com.xdel.luke.QR.QRCodeEncoder;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.setDeliveredStatusActivity;
import com.xdel.luke.setFailStatusActivity;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;
import java.util.Date;

public class ShipmentActivity extends AppCompatActivity {

    private SharedPreferences gcmp, UpdateJobInfoPreferences;
    String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;

    Button btnLockerShipmentCANCEL, btnLockerShipmentNext, btnNormBarcode, btnQRBarcode;
    TextView lblJobNo, lblerrGenParcelIDBarcode, lblerrGenParcelIDQR, lblDipText;
    ImageView imgQR, imgParcelIDBarcode;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        gcmp = objGlobalMethods.getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(gcmp, "language");
        /*String languageToLoad = langType; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/
        setContentView(R.layout.activity_shipment);

        btnLockerShipmentCANCEL = (Button) findViewById(R.id.btnLockerShipmentCANCEL);
        btnLockerShipmentNext = (Button) findViewById(R.id.btnLockerShipmentNext);
        btnNormBarcode = (Button) findViewById(R.id.btnNormBarcode);
        btnQRBarcode = (Button) findViewById(R.id.btnQRBarcode);
        lblJobNo = (TextView) findViewById(R.id.lblJobNo);
        lblDipText = findViewById(R.id.lblDipText);
        lblerrGenParcelIDBarcode = (TextView) findViewById(R.id.lblerrGenParcelIDBarcode);
        lblerrGenParcelIDQR = (TextView) findViewById(R.id.lblerrGenParcelIDQR);
        imgParcelIDBarcode = (ImageView) findViewById(R.id.imgParcelIDBarcode);
        imgQR = (ImageView) findViewById(R.id.imgQR);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    @Override
    protected void onResume() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                if (!objGlobalMethods.UserCanScreenShot(gcmp)) {
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;
                setTitle("");

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("ShipmentActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                JobsHandler.checkForceLogOut();
                init();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ShipmentActivity onResume. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);

            btnLockerShipmentCANCEL = null;
            btnLockerShipmentNext = null;
            btnNormBarcode = null;
            btnQRBarcode = null;
            lblJobNo = null;
            lblerrGenParcelIDBarcode = null;
            lblerrGenParcelIDQR = null;
            imgParcelIDBarcode = null;
            imgQR = null;
            dismissNullifyPD(pd);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShipmentActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }
    private void dismissNullifyPD(ProgressDialog apd) {
        try {
            if (apd != null && apd.isShowing()) {
                apd.dismiss();
                apd = null;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShipmentActivity dismissNullifyPD. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onPause() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ShipmentActivity onPause unregisterContentObserver. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back(false);
    }

    public void btnLockerShipmentCANCEL_Click(View v) {
        back(false);
    }

    public void btnLockerShipmentNext_Click(View v) {
        next();
    }

    private void back(boolean backToJobInfo) {
        Intent parentIntent = new Intent(this, backToJobInfo ? JobInfoActivity.class : AuthActivity.class);
        MainActivity.uponLogin = 0;
        gcmp = objGlobalMethods.getGcmPreferences();
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    private void next() {
        try {
            gcmp = objGlobalMethods.getGcmPreferences();
            int catType = objGlobalMethods.getDashboard2ActivityCategoryType(gcmp);

            Intent parentIntent = new Intent(this, CheckListActivity.class);
            MainActivity.uponLogin = 0;
            gcmp = objGlobalMethods.getGcmPreferences();
            DrawerMainActivity.DashboardOpenFromPush = false;
            setDeliveredStatusActivity.fromPage = "CheckListActivity";
            CheckListActivity.ActionCode = "DS";
            CheckListActivity.nextActivtyIntent = new Intent(this, setDeliveredStatusActivity.class);
            CheckListActivity.fromPage = "ShipmentActivity";
            startActivity(parentIntent);
            this.finish();

            /*if (catType == 0) {
                String JobsIDXstr = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoIDX);
                if (!objGlobalMethods.IsEmptyString(JobsIDXstr)) {
                    final long JobsIDX = Long.parseLong(JobsIDXstr);
                    Context ctx = myApp.getAppContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setMessage(ctx.getString(R.string.alert_cfmupdatestatusPU));
                    builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user pressed "yes", update job status
                            UpdateJobStatusPU(JobsIDX);
                        }
                    });
                    builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user select "No", just cancel this dialog and continue with app
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Context ctx = myApp.getAppContext();
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_ErrorJobsIDX)).show();
                }
            } else {
                Intent parentIntent = new Intent(this, CheckListActivity.class);
                MainActivity.uponLogin = 0;
                gcmp = objGlobalMethods.getGcmPreferences();
                DrawerMainActivity.DashboardOpenFromPush = false;
                setDeliveredStatusActivity.fromPage = "CheckListActivity";
                CheckListActivity.ActionCode = "DS";
                CheckListActivity.nextActivtyIntent = new Intent(this, setDeliveredStatusActivity.class);
                CheckListActivity.fromPage = "ShipmentActivity";
                startActivity(parentIntent);
                this.finish();
            }*/
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShipmentActivity next. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void UpdateJobStatusPU(Long JobsIDX) {
        try {
            gcmp = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
            int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
            String RegID = objGlobalMethods.getRegistrationIdOnly();
            JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, JobsIDX, 49, timestampstr, "", "",
                    lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

            objGlobalMethods.writeToFile("Click to update job status.", "JobsIDX: " + String.valueOf(JobsIDX), "Submitted StatusCodeIDX: " + String.valueOf(49), "", "");
            submitSQLite(JobsIDX, 49, gcmp, UpdateJobInfoPreferences);
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at ShipmentActivity UpdateJobStatus. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void submitSQLite(long JobsIDX, long aaAStatusCodeIDX, SharedPreferences agcmp, SharedPreferences aupdatep) {
        try {
            //objGlobalMethods.writeToFile("JobInfoActivity submit started.", "", "", "", "");
            pd.show();
            Gson gson = new Gson();

            DAO_Jobs.updateJobsCatTypeID(1, JobsIDX);
            JobsHandler.setSystemCategoryJobsIDXhashSQLite(agcmp, 0);
            JobsHandler.setSystemCategoryJobsIDXhashSQLite(agcmp, 1);

            //objGlobalMethods.writeToFile("JobsHandler from JobInfoActivity submit started.", "", "", "", "");
            Context ctx = myApp.getAppContext();
            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

            pd.dismiss();
            BackStepWithoutUpdateSQLite();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at ShipmentActivity submitSQLite. Exception: " + e.toString(), "", "", "", "");
            pd.dismiss();
            if (aaAStatusCodeIDX != 368 && aaAStatusCodeIDX != 369 && aaAStatusCodeIDX != 388) {
                BackStepWithoutUpdateSQLite();
            }
        }
    }

    private void BackStepWithoutUpdateSQLite() {
        Intent parentIntent = null;
        gcmp = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        int CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(gcmp);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        try {
            Camera5Activity.mCurrentPhotoPath = null;
            setFailStatusActivity.reasons = "";
            setFailStatusActivity.selectedStatusCodesIDX = "";
            setFailStatusActivity.ddlSelectedIndex = 0;
            setDeliveredStatusActivity.ddlSelectedIndex = 0;
            setDeliveredStatusActivity.ishuman = "0";
            Gson gson = new Gson();
            objJobInfo[] JobInfoAL;
            objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.checkedItemsName, "");
            objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.checkedItemsIDX, "");
            objGlobalMethods.setGCMPrefs(gcmp, "ScannedCodeStrsPrefStr", "");
            objGlobalMethods.setGCMPrefs(gcmp, "massupdateokpwp2PostDataArr", "");
            objGlobalMethods.setGCMPrefs(gcmp, "cameraScannedCode", "");
            objGlobalMethods.setGCMPrefs(gcmp, "objJobInfoBringtoUpdateCODListJsonStr", "");
            objGlobalMethods.setGCMPrefs(gcmp, "mutlipleCODjobs", "");
            objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
            objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
            objGlobalMethods.setJobInfoJsonStrPref(gcmp, "");
            String Postal = objGlobalMethods.getGCMPrefs(gcmp, "desPostal");
            String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(gcmp, "SelectedVRPJobsIDXs");
            String fromSubMenu = objGlobalMethods.getGCMPrefs(gcmp, "fromSubMenu");
            int TotalJobsCountPerCatTypePerPostal = !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                    !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                            : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);

            if (!gcmp.getString("isFromMainCOD", "").isEmpty()) {
                objGlobalMethods.setGCMPrefs(gcmp, "desPostal", "");
                objGlobalMethods.setGCMPrefs(gcmp, "LAT", "");
                objGlobalMethods.setGCMPrefs(gcmp, "LNG", "");
                objGlobalMethods.setGCMPrefs(gcmp, "desAddress", "");
                objGlobalMethods.setGCMPrefs(gcmp, "isFromMainCOD", "");
                objGlobalMethods.setGCMPrefs(gcmp, "isFromMessaging", "");
                objGlobalMethods.setGCMPrefs(gcmp, "isFromDashboardSub", "");
                parentIntent = new Intent(this, MainCODActivity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (!gcmp.getString("isFromMessaging", "").isEmpty()) {
                objGlobalMethods.setGCMPrefs(gcmp, "desPostal", "");
                objGlobalMethods.setGCMPrefs(gcmp, "LAT", "");
                objGlobalMethods.setGCMPrefs(gcmp, "LNG", "");
                objGlobalMethods.setGCMPrefs(gcmp, "desAddress", "");
                objGlobalMethods.setGCMPrefs(gcmp, "isFromMainCOD", "");
                objGlobalMethods.setGCMPrefs(gcmp, "isFromMessaging", "");
                objGlobalMethods.setGCMPrefs(gcmp, "isFromDashboardSub", "");
                parentIntent = new Intent(this, Messaging.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (!gcmp.getString("isFromDashboardSub", "").isEmpty()) {
                if (TotalJobsCountPerCatTypePerPostal > 1) {
                    parentIntent = new Intent(this, JobsListGroupActivity.class);
                } else if (TotalJobsCountPerCatTypePerPostal == 0 || TotalJobsCountPerCatTypePerPostal == 1) {
                    Integer TotalPIPDIPJobsCountPerPostal = !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalPIPDIPJobsCountPerPostalWithJobsIDXs(Postal, SelectedVRPJobsIDXs)
                            : DAO_Jobs.getTotalPIPDIPJobsCountPerPostal(Postal);
                    objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTypePrefName, "-1");
                    objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTitlePrefName, "");
                    objGlobalMethods.setGCMPrefs(gcmp, "isFromMainCOD", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "isFromMessaging", "");
                    if (TotalPIPDIPJobsCountPerPostal > 1) {
                        parentIntent = new Intent(this, DashboardSubActivity.class);
                    } else {
                        objGlobalMethods.setGCMPrefs(gcmp, "desPostal", "");
                        objGlobalMethods.setGCMPrefs(gcmp, "LAT", "");
                        objGlobalMethods.setGCMPrefs(gcmp, "LNG", "");
                        objGlobalMethods.setGCMPrefs(gcmp, "desAddress", "");
                        objGlobalMethods.setGCMPrefs(gcmp, "isFromDashboardSub", "");

                        if (DashboardSubActivity.frompage != null) {
                            parentIntent = new Intent(this, MapsAllActivity.class);
                        } else {
                            parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                        }
                    }
                    MainActivity.uponLogin = 0;
                }
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (TotalJobsCountPerCatTypePerPostal == 0) {
                objGlobalMethods.setGCMPrefs(gcmp, "desPostal", "");
                objGlobalMethods.setGCMPrefs(gcmp, "LAT", "");
                objGlobalMethods.setGCMPrefs(gcmp, "LNG", "");
                objGlobalMethods.setGCMPrefs(gcmp, "desAddress", "");
                objGlobalMethods.setGCMPrefs(gcmp, "isFromMainCOD", "");
                objGlobalMethods.setGCMPrefs(gcmp, "isFromMessaging", "");
                objGlobalMethods.setGCMPrefs(gcmp, "isFromDashboardSub", "");
                int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
                if (TotalJobsCountPerCatType >= 1) {
                    parentIntent = new Intent(this, AddressListNewActivity.class);
                } else if (TotalJobsCountPerCatType < 1) {
                    parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                }
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (TotalJobsCountPerCatTypePerPostal > 1) {
                parentIntent = new Intent(this, JobsListGroupActivity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at ShipmentActivity BackStepWithoutUpdateSQLite. Exception: " + s, "", "", "", "");
            int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
            if (TotalJobsCountPerCatType >= 1) {
                parentIntent = new Intent(this, AddressListNewActivity.class);
            } else if (TotalJobsCountPerCatType < 1) {
                parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            }
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        }
    }

    private void init() {
        try {
            if (gcmp == null) {
                gcmp = objGlobalMethods.getGcmPreferences();
            }
            final String JobNo = objGlobalMethods.getGCMPrefs(gcmp, "lockerparcelid");
            if (!objGlobalMethods.IsEmptyString(JobNo))
                lblJobNo.setText(JobNo);
            int catType = objGlobalMethods.getDashboard2ActivityCategoryType(gcmp);
            Context c = myApp.getAppContext();
            if (catType == 2) {
                lblDipText.setText(c.getString(R.string.la_dip_msg));
                lblDipText.setVisibility(View.VISIBLE);
            }
            else {
                lblDipText.setText(c.getString(R.string.la_pu_msg));
                String LockerParcelIDBarcode64str = objGlobalMethods.getGCMPrefs(gcmp, "LockerParcelIDBarcode64str");
                String LockerParcelIDQR64str = objGlobalMethods.getGCMPrefs(gcmp, "LockerParcelIDQR64str");
                if (!objGlobalMethods.IsEmptyString(JobNo)) {
                    if (!objGlobalMethods.IsEmptyString(LockerParcelIDBarcode64str) || !objGlobalMethods.IsEmptyString(LockerParcelIDQR64str)) {
                        genNormBarcodeQRcodeFromBase64str t = new genNormBarcodeQRcodeFromBase64str(LockerParcelIDBarcode64str, LockerParcelIDQR64str);
                        t.execute();
                    } else {
                        new CountDownTimer(500, 500) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (pd != null && !pd.isShowing()) {
                                    pd.show();
                                }
                            }

                            @Override
                            public void onFinish() {
                                genNormBarcodeQRcode t = new genNormBarcodeQRcode(JobNo);
                                t.execute();
                            }
                        }.start();
                    }
                } else {
                    showError_n_Back();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShipmentActivity init. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private class genNormBarcodeQRcodeFromBase64str extends AsyncTask<Void, Void, ArrayList<byte[]>> {
        private final String LockerParcelIDBarcode64str;
        private final String LockerParcelIDQR64str;

        public genNormBarcodeQRcodeFromBase64str(String aLockerParcelIDBarcode64str, String aLockerParcelIDQR64str) {
            this.LockerParcelIDBarcode64str = aLockerParcelIDBarcode64str;
            this.LockerParcelIDQR64str = aLockerParcelIDQR64str;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            if (pd != null && !pd.isShowing()) {
                pd.show();
            }
        }

        @Override
        protected ArrayList<byte[]> doInBackground(Void... params) {
            ArrayList<byte[]> al = null;
            byte[] id = null;
            byte[] qr = null;
            try {
                id = Base64.decode(LockerParcelIDBarcode64str, Base64.DEFAULT);
                qr = Base64.decode(LockerParcelIDQR64str, Base64.DEFAULT);
                al = new ArrayList<>();
                al.add(id);
                al.add(qr);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ShipmentActivity genNormBarcodeQRcodeFromBase64str doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return al;
            }
        }

        @Override
        protected void onPostExecute(final ArrayList<byte[]> res) {
            boolean err = false;
            boolean IDerr = false;
            boolean QRerr = false;
            byte[] id = null;
            byte[] qr = null;

            try {
                if (res != null && res.size() > 0) {
                    lblerrGenParcelIDBarcode.setVisibility(View.GONE);
                    lblerrGenParcelIDQR.setVisibility(View.GONE);
                    imgParcelIDBarcode.setVisibility(View.VISIBLE);
                    imgQR.setVisibility(View.VISIBLE);
                    try {
                        id = res.get(0);
                    } catch (Exception e) {
                        id = null;
                        objGlobalMethods.writeToFile("Exception at ShipmentActivity genNormBarcodeQRcodeFromBase64str onPostExecute grabbing id byte[]. Exception: " + e.getMessage(), "", "", "", "");
                    }

                    try {
                        qr = res.get(1);
                    } catch (Exception e) {
                        qr = null;
                        objGlobalMethods.writeToFile("Exception at ShipmentActivity genNormBarcodeQRcodeFromBase64str onPostExecute grabbing qr byte[]. Exception: " + e.getMessage(), "", "", "", "");
                    }

                    if (id != null) {
                        imgParcelIDBarcode.setImageBitmap(BitmapFactory.decodeByteArray(id, 0, id.length));
                    } else {
                        lblerrGenParcelIDBarcode.setVisibility(View.VISIBLE);
                        imgParcelIDBarcode.setVisibility(View.GONE);
                    }

                    if (qr != null) {
                        imgQR.setImageBitmap(BitmapFactory.decodeByteArray(qr, 0, qr.length));
                    } else {
                        lblerrGenParcelIDQR.setVisibility(View.VISIBLE);
                        imgQR.setVisibility(View.GONE);
                    }

                } else {
                    err = true;
                }

                pd.dismiss();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ShipmentActivity genNormBarcodeQRcodeFromBase64str onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                lblerrGenParcelIDBarcode.setVisibility(err || IDerr ? View.VISIBLE : View.GONE);
                lblerrGenParcelIDQR.setVisibility(err || QRerr ? View.VISIBLE : View.GONE);
                imgParcelIDBarcode.setVisibility(err || IDerr ? View.GONE : View.VISIBLE);
                imgQR.setVisibility(err || QRerr ? View.GONE : View.VISIBLE);
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        }
    }

    private class genNormBarcodeQRcode extends AsyncTask<Void, Void, objPassQR_IDbarcode> {
        private final String IDval;
        private SharedPreferences gcmp;

        public genNormBarcodeQRcode(String aIDval) {
            this.IDval = aIDval;
            this.gcmp = objGlobalMethods.getGcmPreferences();
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            if (pd != null && !pd.isShowing()) {
                pd.show();
            }
        }

        @Override
        protected objPassQR_IDbarcode doInBackground(Void... params) {
            objPassQR_IDbarcode res = new objPassQR_IDbarcode();
            Bitmap bmp = null;
            try {
                if (gcmp == null) {
                    gcmp = objGlobalMethods.getGcmPreferences();
                }
                com.google.zxing.Writer writer = new MultiFormatWriter();
                String finaldata = Uri.encode(IDval, "utf-8");
                int height = imgParcelIDBarcode.getHeight();
                int width = imgParcelIDBarcode.getWidth();
                BitMatrix bm = writer.encode(finaldata, BarcodeFormat.CODE_128, width, height);
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                for (int i = 0; i < width; i++) {//width
                    for (int j = 0; j < height; j++) {//height
                        bmp.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                }
                res.setIDbarcode(bmp);

                //QR
                WindowManager manager = (WindowManager) myApp.getAppContext().getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int QRwidth = point.x;
                int QRheight = point.y;
                int smallerDimension = QRwidth < QRheight ? QRwidth : QRheight;
                smallerDimension = smallerDimension * 5 / 6;
                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(IDval,
                        null,
                        Contents.Type.TEXT,
                        BarcodeFormat.QR_CODE.toString(),
                        smallerDimension);
                Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                res.setQRimgBase64(JobsHandler.convertBitmapToBase64(bitmap));
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ShipmentActivity genNormBarcodeQRcode doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final objPassQR_IDbarcode res) {
            boolean err = false;
            boolean IDerr = false;
            boolean QRerr = false;
            Bitmap IDbarcode = null;
            String LockerParcelIDQR64str = "";
            String LockerParcelIDBarcode64str = "";
            try {
                if (res != null) {
                    lblerrGenParcelIDBarcode.setVisibility(View.GONE);
                    lblerrGenParcelIDQR.setVisibility(View.GONE);
                    imgParcelIDBarcode.setVisibility(View.VISIBLE);
                    imgQR.setVisibility(View.VISIBLE);

                    IDbarcode = res.getIDbarcode();
                    IDerr = IDbarcode == null ? true : false;
                    if (IDbarcode != null) {
                        imgParcelIDBarcode.setImageBitmap(IDbarcode);
                        LockerParcelIDBarcode64str = JobsHandler.convertBitmapToBase64(IDbarcode);
                    } else {
                        IDerr = true;
                        LockerParcelIDBarcode64str = "";
                    }

                    LockerParcelIDQR64str = res.getQRimgBase64();
                    if (!objGlobalMethods.IsEmptyString(LockerParcelIDQR64str)) {
                        byte[] qrImg = Base64.decode(LockerParcelIDQR64str, Base64.DEFAULT);
                        imgQR.setImageBitmap(BitmapFactory.decodeByteArray(qrImg, 0, qrImg.length));
                        gcmp = objGlobalMethods.getGcmPreferences();
                    } else {
                        QRerr = true;
                        LockerParcelIDQR64str = "";
                    }
                } else {
                    err = true;
                    LockerParcelIDQR64str = "";
                    LockerParcelIDBarcode64str = "";
                }

                if (gcmp == null) {
                    gcmp = objGlobalMethods.getGcmPreferences();
                }
                String[] prefName = new String[]{"LockerParcelIDBarcode64str", "LockerParcelIDQR64str"};
                String[] prefvalue = new String[]{
                        LockerParcelIDBarcode64str,
                        LockerParcelIDQR64str
                };
                objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
                pd.dismiss();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ShipmentActivity genNormBarcodeQRcode onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                lblerrGenParcelIDBarcode.setVisibility(err || IDerr ? View.VISIBLE : View.GONE);
                lblerrGenParcelIDQR.setVisibility(err || QRerr ? View.VISIBLE : View.GONE);
                imgParcelIDBarcode.setVisibility(err || IDerr ? View.GONE : View.VISIBLE);
                imgQR.setVisibility(err || QRerr ? View.GONE : View.VISIBLE);
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }

            }
        }

    }

    private void showError_n_Back() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.emptyjobno));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    back(true);
                } catch (ActivityNotFoundException e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at ShipmentActivity showError_n_Back. ActivityNotFoundException: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at ShipmentActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShipmentActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}