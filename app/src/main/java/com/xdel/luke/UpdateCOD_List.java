package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.UpdateDOCOD;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objConvertedBitmapToBase64;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objUpdateCOD_PostData;
import com.xdel.luke.objClasses.objUpdateCOD_PostItemAdapter;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UpdateCOD_List extends AppCompatActivity {

    public static ArrayList<objUpdateCOD_PostData> objUpdateCODPostDataList;
    public static ProgressDialog progressDoalog;

    ConnectionDetector cd;
    Context context;
    private ProgressDialog pd;
    //private String regid;
    private ListView listView;
    SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    private Button btnUpdateCODListNext, btnUpdateCODListSubmit;
    private ProgressDialog pdProcessing;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;

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

        setContentView(R.layout.activity_update_cod__list);

        listView = (ListView) findViewById(R.id.updatecod_listView);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));
        btnUpdateCODListSubmit = (Button) findViewById(R.id.btnUpdateCODListSubmit);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
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
            //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            Gson gson = new Gson();
            context = myApp.getAppContext();

            if (!temploggerclass.isBTEnabled()) {
                objGlobalMethods.writeToFile("UpdateCOD_List !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

            JobsHandler.checkForceLogOut();

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];

            if (username.equals("") && password.equals("")) {
                BackToLoginPage();
            }

            String RegID = objGlobalMethods.getRegistrationIdOnly();

            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);

            if (!cd.isConnectingToInternet()) {
                //// Internet Connection is not present
                Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
            }

            String JsonStr;
            JsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "mutlipleCODjobs");

            if (!JsonStr.equals("")) {
                try {
                    objUpdateCODPostDataList = gson.fromJson(JsonStr, new TypeToken<List<objUpdateCOD_PostData>>() {
                    }.getType());
                    if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                        if (objUpdateCODPostDataList.size() > 0) {
                            loadUpdateCOD_List();
                        } else {
                            btnUpdateCODListSubmit.performClick();
                        }

                        //loadUpdateCOD_List();
                    } else {
                        showErrorAlert("");
                    }
                } catch (Exception e) {
                    objUpdateCODPostDataList = null;
                    showErrorAlert("");
                }

            } else {
                showErrorAlert("");
            }
        }

    }

    @Override
    protected void onPause() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at UpdateCOD_List onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (progressDoalog != null && progressDoalog.isShowing()) {
            progressDoalog.dismiss();
            progressDoalog = null;
        }
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at UpdateCOD_List onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
        super.onDestroy();
    }

    private void loadUpdateCOD_List() {
        Gson gson = new Gson();
        DecimalFormat dec = new DecimalFormat("#.00");
        String CODncActivationJobsIDXInvoiceIDXstrALJsonStr = "";
        ArrayList<long[]> CODncActivationJobsIDXInvoiceIDXstrAL = null;
        long[] JobsIDXInvoiceIDXpairArr = null;
        Double DtotalAmtToCollect = 0.0;
        String StotalAmtToCollect = "0.00";
        try {
            Collections.sort(objUpdateCODPostDataList);

            CODncActivationJobsIDXInvoiceIDXstrALJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "CODncActivationJobsIDXInvoiceIDXstrALJsonStr");
            if (!CODncActivationJobsIDXInvoiceIDXstrALJsonStr.equals("") && !CODncActivationJobsIDXInvoiceIDXstrALJsonStr.equals("[]")) {
                CODncActivationJobsIDXInvoiceIDXstrAL = gson.fromJson(CODncActivationJobsIDXInvoiceIDXstrALJsonStr, new TypeToken<List<long[]>>() {
                }.getType());

                for (int i = 0; i <= CODncActivationJobsIDXInvoiceIDXstrAL.size() - 1; i++) {
                    JobsIDXInvoiceIDXpairArr = CODncActivationJobsIDXInvoiceIDXstrAL.get(i);
                    for (int x = 0; x <= objUpdateCODPostDataList.size() - 1; x++) {
                        if (objUpdateCODPostDataList.get(x).JobsIDX == JobsIDXInvoiceIDXpairArr[0] && objUpdateCODPostDataList.get(x).InvoiceIDX == JobsIDXInvoiceIDXpairArr[1]) {
                            objUpdateCODPostDataList.get(x).setEnableNCbutton(true);
                        }
                    }
                }
            }

            objUpdateCOD_PostItemAdapter adapter = new objUpdateCOD_PostItemAdapter(this, objUpdateCODPostDataList, this);
            listView.setAdapter(adapter);


            DtotalAmtToCollect = JobsHandler.returnTotalCODtoUpdateAmt(objUpdateCODPostDataList);
            if (DtotalAmtToCollect.equals(0.0)) {
                StotalAmtToCollect = "0.00";
            } else if (DtotalAmtToCollect > 0.0) {
                StotalAmtToCollect = String.valueOf(dec.format(DtotalAmtToCollect));
            }

            setTitle(myApp.getAppContext().getString(R.string.title_activity_update_cod__list) + " - " + "$" + StotalAmtToCollect);

            btnUpdateCODListSubmit.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at UpdateCOD_List loadUpdateCOD_List. Exception: " + e.toString(), "", "", "", "");
            showErrorAlert("");
        }
    }

    private void showChangeAirplaneModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateCOD_List.this);
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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

    private void showErrorAlert(String errmsg) {
        MainActivity.uponLogin = 0;
        DrawerMainActivity.DashboardOpenFromPush = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        if (errmsg.equals("")) {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
        } else {
            builder.setMessage(errmsg);
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                backstep();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showErrorUpdateAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alertErrorUpdateCODProceedWithoutUpdateCOD));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //if user pressed "yes", update job status
                Date dt = new Date();
                objGlobalMethods.writeToFile("Update Job status without COD Update.", "", "", "", "");
                submitWithoutCODUpdateSQLite();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_cod__list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void backstep() {
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        Intent parentIntent = null;
        String ojisPage = "";
        JobInfoActivity.accessFromDirection = 0;
        ojisPage = objGlobalMethods.getGCMPrefs(gcmp, "ojisPage");
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        if (ojisPage.equals("0")) {
            parentIntent = new Intent(UpdateCOD_List.this, JobInfoActivity.class);
            objGlobalMethods.setGCMPrefs(gcmp, "part2puHasCOD", "false");
        } else if (ojisPage.equals("1")) {
            parentIntent = new Intent(UpdateCOD_List.this, JobsListGroupActivity.class);
            objGlobalMethods.setGCMPrefs(gcmp, "part2puHasCOD", "false");
        } else if (ojisPage.equals("2")) {
            parentIntent = new Intent(UpdateCOD_List.this, setDeliveredStatusActivity.class);
        } else if (ojisPage.equals("3")) {
            parentIntent = new Intent(UpdateCOD_List.this, setDeliveredMassStatusActivity.class);
        } else if (ojisPage.equals("4")) {
            parentIntent = new Intent(UpdateCOD_List.this, MassUpdateOKPwP2.class);
        } else if (ojisPage.equals("5")) {
            parentIntent = new Intent(UpdateCOD_List.this, OtpActivity.class);
        } else if (ojisPage.equals("6")) {
            parentIntent = new Intent(UpdateCOD_List.this, CheckListActivity.class);
        } else {
            parentIntent = new Intent(UpdateCOD_List.this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            MainActivity.uponLogin = 0;
            objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTypePrefName, "-1");
            objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTitlePrefName, "");
            JobsHandler.sethfJobsInfoArrJsonStrPref(gcmp, "");
            objGlobalMethods.setJobInfoJsonStrPref(gcmp, "");
            objGlobalMethods.setGCMPrefs(gcmp, "part2puHasCOD", "false");
        }

        objGlobalMethods.setGCMPrefs(gcmp, "ojisPage", "");

        startActivity(parentIntent);
        UpdateCOD_List.this.finish();
    }

    @Override
    public void onBackPressed() {
        backstep();
    }

    public void btnCancelClick(View view) {
        backstep();
    }

    public void btnSubmitClick(View view) {
        //mainly for MASS PU
        try {
            if (ValidateSubmit()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdate));
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status.
                        //submitUpdate();
                        submitUpdateSQLite();
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
                showDialog(myApp.getAppContext().getString(R.string.alertvalidatesubmit));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at UpdateCOD_List btnSubmitClick. Exception: " + e.toString(), "", "", "", "");
            showErrorAlert(myApp.getAppContext().getString(R.string.alertUnableUpdateCODnPU));
        }
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(1);
        }
    };

    private void submitUpdateSQLite() {
        Boolean asd = false;
        Date dt = new Date();
        Gson gson = new Gson();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        String objJobInfoBringtoUpdateCODListJsonStr = "";
        Boolean canProceed = true;
        try {
            Long l = dt.getTime();
            final String timestampstr = "/Date(" + l + ")/";
            objGlobalMethods.writeToFile("UpdateCOD_List submitUpdateSQLite started.", "", "", "", "");

            if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                for (int i = 0; i <= objUpdateCODPostDataList.size() - 1; i++) {
                    if (objUpdateCODPostDataList.get(i).CODType == -1) {
                        canProceed = false;
                        break;
                    }
                }
            }

            if (!canProceed) {
                objGlobalMethods.writeToFile("Unable to submit Jobs status and COD status with CODType = -1.", "", "", "", "");
                showErrorUpdateAlert();
            } else if (canProceed) {
                objJobInfoBringtoUpdateCODListJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr");
                final objJobInfo[] objJobInfoBringtoUpdateCODAL = objJobInfoBringtoUpdateCODListJsonStr == "" ? null : gson.fromJson(objJobInfoBringtoUpdateCODListJsonStr, objJobInfo[].class);

                if (objJobInfoBringtoUpdateCODAL != null && objJobInfoBringtoUpdateCODAL.length > 0) {
                    final Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                    final int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);

                    progressDoalog = new ProgressDialog(this);
                    progressDoalog.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));
                    progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDoalog.setMax(objJobInfoBringtoUpdateCODAL.length);
                    progressDoalog.setCanceledOnTouchOutside(false);
                    progressDoalog.show();

                    final Thread t = new Thread() {
                        @Override
                        public void run() {
                            Gson gson = new Gson();
                            ArrayList<String> convertedBitmapToBase64AL;
                            ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = null;
                            String RegID = objGlobalMethods.getRegistrationIdOnly();
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                            try {
                                for (int i = 0; i <= objJobInfoBringtoUpdateCODAL.length - 1; i++) {
                                    Thread.sleep(200);
                                    handle.sendMessage(handle.obtainMessage());

                                    if (objJobInfoBringtoUpdateCODAL[i].pendingStatusUpdate == 49) {

                                        //String checklistvalue = objGlobalMethods.getGCMPrefs(GcmPreferences, "checklistvalue");
                                        String checklistvalue = objGlobalMethods.getChecklistRelatedSP_KeyValue("checklistvalue");
                                        String ARemarks = !objGlobalMethods.IsEmptyString(checklistvalue) ? checklistvalue : "";

                                        JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX, 49,
                                                timestampstr, ARemarks, "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

                                        ArrayList<objConvertedBitmapToBase64> CheckListconvertedBitmapToBase64List = null;
                                        CheckListconvertedBitmapToBase64List = RTNCheckListConvertedBitmapToBase64List();

                                        if (CheckListconvertedBitmapToBase64List != null && CheckListconvertedBitmapToBase64List.size() > 0) {
                                            for (int x = 0; x <= CheckListconvertedBitmapToBase64List.size() - 1; x++) {
                                                JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX,
                                                        !objGlobalMethods.IsEmptyString(CheckListconvertedBitmapToBase64List.get(x).photodesc) ? "[49]" + CheckListconvertedBitmapToBase64List.get(x).photodesc : "[49]",
                                                        CheckListconvertedBitmapToBase64List.get(x).convertedBitmapToBase64,
                                                        timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                                        CheckListconvertedBitmapToBase64List.get(x).FilePath);
                                            }
                                        }

                                        ////set from pip to pu
                                        Boolean ifJobsExist = DAO_Jobs.checkIfJobExist(objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                                        if (ifJobsExist) {
                                            DAO_Jobs.updateJobsCatTypeID(1, objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 0);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 1);
                                        } else {
                                            String JobInfoJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL[i]);
                                            DAO_Jobs.insertJobs(objJobInfoBringtoUpdateCODAL[i], 1, JobInfoJsonStr);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 1);
                                        }


                                        String scannedtype = objGlobalMethods.getGCMPrefs(GcmPreferences, "scannedtype");
                                        String scannedval = objGlobalMethods.getGCMPrefs(GcmPreferences, "scannedval");

                                        if (!scannedtype.isEmpty() && scannedtype.equals("barcode") && !scannedval.isEmpty()) {
                                            //update barcode
                                            Boolean succ = JobsHandler.processBarcodeTagUpdate(objJobInfoBringtoUpdateCODAL[i], scannedval);
                                            if (succ) {
                                                objGlobalMethods.writeToFile("UpdateCOD_List submitUpdateSQLite JobsHandler.processBarcodeTagUpdate ended successfully.", "", "", "", "");
                                            } else {
                                                objGlobalMethods.writeToFile("UpdateCOD_List submitUpdateSQLite JobsHandler.processBarcodeTagUpdate ended with error.", "", "", "", "");
                                            }
                                        } else if (!scannedtype.isEmpty() && scannedtype.equals("cnno") && !scannedval.isEmpty()) {
                                            //update cn
                                            Boolean succ = JobsHandler.processCNUpdate(objJobInfoBringtoUpdateCODAL[i], scannedval);
                                            if (succ) {
                                                objGlobalMethods.writeToFile("UpdateCOD_List submitUpdateSQLite JobsHandler.processCNUpdate ended successfully.", "", "", "", "");
                                            } else {
                                                objGlobalMethods.writeToFile("UpdateCOD_List submitUpdateSQLite JobsHandler.processCNUpdate ended with error.", "", "", "", "");
                                            }
                                        }
                                    } else if (objJobInfoBringtoUpdateCODAL[i].pendingStatusUpdate == 135) {
                                        Boolean DOfullySubmitted = JobsHandler.ProcessDOincForSubmission(GcmPreferences, UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                                        String statuscode = "";
                                        if (DOfullySubmitted) {
                                            JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX, 135,
                                                    timestampstr, objJobInfoBringtoUpdateCODAL[i].Reason, "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, objJobInfoBringtoUpdateCODAL[i].Receiver);
                                            statuscode = "[135]";
                                            ////set from dip to okp
                                            DAO_Jobs.updateJobsCatTypeID(3, objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 2);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 3);
                                        } else {
                                            JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX, 47,
                                                    timestampstr, objJobInfoBringtoUpdateCODAL[i].Reason, "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, objJobInfoBringtoUpdateCODAL[i].Receiver);
                                            statuscode = "[47]";
                                            ////set from dip to fd
                                            DAO_Jobs.updateJobsCatTypeID(5, objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 2);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 5);
                                        }

                                        //add update checklist photo to que
                                        if (objJobInfoBringtoUpdateCODAL[i].objCheckListConvertedBitmapToBase64List != null && objJobInfoBringtoUpdateCODAL[i].objCheckListConvertedBitmapToBase64List.size() > 0) {
                                            convertedBitmapToBase64List = objJobInfoBringtoUpdateCODAL[i].objCheckListConvertedBitmapToBase64List;
                                            for (int x = 0; x <= convertedBitmapToBase64List.size() - 1; x++) {
                                                /*JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX,
                                                        statuscode + objJobInfoBringtoUpdateCODAL[i].Receiver + "\n\n" + objJobInfoBringtoUpdateCODAL[i].Reason,
                                                        convertedBitmapToBase64List.get(x).convertedBitmapToBase64, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                                        convertedBitmapToBase64List.get(x).FilePath);*/
                                                JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX,
                                                        statuscode + convertedBitmapToBase64List.get(x).photodesc,
                                                        convertedBitmapToBase64List.get(x).convertedBitmapToBase64, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                                        convertedBitmapToBase64List.get(x).FilePath);
                                            }
                                        }

                                        //add update job photo to que
                                        if (objJobInfoBringtoUpdateCODAL[i].objConvertedBitmapToBase64 != null && objJobInfoBringtoUpdateCODAL[i].objConvertedBitmapToBase64.size() > 0) {
                                            convertedBitmapToBase64List = objJobInfoBringtoUpdateCODAL[i].objConvertedBitmapToBase64;
                                            for (int x = 0; x <= convertedBitmapToBase64List.size() - 1; x++) {
                                                JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX,
                                                        statuscode + objJobInfoBringtoUpdateCODAL[i].Receiver + "\n\n" + objJobInfoBringtoUpdateCODAL[i].Reason,
                                                        convertedBitmapToBase64List.get(x).convertedBitmapToBase64, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                                        convertedBitmapToBase64List.get(x).FilePath);
                                            }
                                        }

                                        //add update jobsignatureIA to que
                                        if (objJobInfoBringtoUpdateCODAL[i].UploadJobSignatureIABitmapToBase64List != null && objJobInfoBringtoUpdateCODAL[i].UploadJobSignatureIABitmapToBase64List.size() > 0) {
                                            convertedBitmapToBase64AL = objJobInfoBringtoUpdateCODAL[i].UploadJobSignatureIABitmapToBase64List;
                                            for (int x = 0; x <= convertedBitmapToBase64AL.size() - 1; x++) {
                                                JobsHandler.addUpdateJobSignatureIAQueALPrefs(UpdateJobInfoPreferences,
                                                        RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX, convertedBitmapToBase64AL.get(x),
                                                        timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                                            }
                                        }

                                        //add update job signature to que
                                        if (objJobInfoBringtoUpdateCODAL[i].SignatureStr != null && !objJobInfoBringtoUpdateCODAL[i].SignatureStr.equals("")) {
                                            JobsHandler.addUpdateJobSignatureQueALPrefs(UpdateJobInfoPreferences,
                                                    RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX, objJobInfoBringtoUpdateCODAL[i].SignatureStr,
                                                    timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                                        }
                                    } else {
                                        //update part 2 FP
                                        JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX, objJobInfoBringtoUpdateCODAL[i].pendingStatusUpdate,
                                                timestampstr, objJobInfoBringtoUpdateCODAL[i].FailureReason, "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

                                        Boolean ifJobsExist = DAO_Jobs.checkIfJobExist(objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                                        if (ifJobsExist) {
                                            DAO_Jobs.updateJobsCatTypeID(4, objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 0);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 4);
                                        } else {
                                            String JobInfoJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL[i]);
                                            DAO_Jobs.insertJobs(objJobInfoBringtoUpdateCODAL[i], 4, JobInfoJsonStr);
                                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 4);
                                        }

                                        //add update job photo to que
                                        if (objJobInfoBringtoUpdateCODAL[i].objConvertedBitmapToBase64 != null && objJobInfoBringtoUpdateCODAL[i].objConvertedBitmapToBase64.size() > 0) {
                                            convertedBitmapToBase64List = objJobInfoBringtoUpdateCODAL[i].objConvertedBitmapToBase64;
                                            for (int x = 0; x <= convertedBitmapToBase64List.size() - 1; x++) {
                                                JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX,
                                                        "[" + String.valueOf(objJobInfoBringtoUpdateCODAL[i].pendingStatusUpdate) + "]" + objJobInfoBringtoUpdateCODAL[i].FailureReason,
                                                        convertedBitmapToBase64List.get(x).convertedBitmapToBase64,
                                                        timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                                        convertedBitmapToBase64List.get(x).FilePath);
                                            }
                                        }
                                    }
                                }

                                if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                                    for (int i = 0; i <= objUpdateCODPostDataList.size() - 1; i++) {
                                        JobsHandler.addUpdateCODQueALPrefs(UpdateJobInfoPreferences, objUpdateCODPostDataList.get(i).ARegID,
                                                objUpdateCODPostDataList.get(i).AType, objUpdateCODPostDataList.get(i).AIDX, objUpdateCODPostDataList.get(i).CODType, objUpdateCODPostDataList.get(i).AUTC);
                                    }
                                }

                                String tempDOCODJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "tempDOCODJsonStr");
                                if (tempDOCODJsonStr != "" && tempDOCODJsonStr != "[]") {
                                    UpdateDOCOD updatedocod = gson.fromJson(tempDOCODJsonStr, UpdateDOCOD.class);
                                    JobsHandler.addUpdateDOCODQueALPrefs(UpdateJobInfoPreferences, updatedocod);
                                }

                                JobsHandler.processMassGetReviewLink(GcmPreferences, UpdateJobInfoPreferences, gson);

                                progressDoalog.dismiss();
                                JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "","", true);
                                Context ctx = myApp.getAppContext();
                                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "mutlipleCODjobs", "");
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", "");

                                objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, true);

                                ReturnToPrevPage2SQLite(true);

                            } catch (Exception e) {
                                progressDoalog.dismiss();
                                objGlobalMethods.writeToFile("Exception caught at MassUpdateOKPwP2 updateAllJobsStatus0SQLite progress dialog thread. Exception: " + e.getMessage().toString(), "", "", "", "");
                                JobsHandler.sendErrorEmail("Exception caught at MassUpdateOKPwP2 updateAllJobsStatus0SQLite progress dialog thread. Exception: " + e.getMessage().toString());
                                ReturnToPrevPage2SQLite(false);
                            }
                        }
                    };
                    t.start();
                } else {
                    asd = false;
                    ReturnToPrevPage2SQLite(asd);
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at UpdateCOD_List submitUpdateSQLite. Some Jobs status may not be submitted n COD status not submitted. Exception: " + e.toString(), "", "", "", "");
            asd = false;
            ReturnToPrevPage2SQLite(asd);
        }
    }

    private void submitWithoutCODUpdateSQLite() {
        Boolean asd = false;
        Date dt = new Date();
        Gson gson = new Gson();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        objJobInfo[] objJobInfoBringtoUpdateCODAL = null;
        ArrayList<String> convertedBitmapToBase64AL;
        ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = null;
        String objJobInfoBringtoUpdateCODListJsonStr = "";

        try {
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            objGlobalMethods.writeToFile("UpdateCOD_List submitWithoutCODUpdateSQLite started.", "", "", "", "");

            try {
                objJobInfoBringtoUpdateCODListJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr");
                objJobInfoBringtoUpdateCODAL = gson.fromJson(objJobInfoBringtoUpdateCODListJsonStr, objJobInfo[].class);
            } catch (Exception e) {
                objJobInfoBringtoUpdateCODAL = null;
            }

            if (objJobInfoBringtoUpdateCODAL != null && objJobInfoBringtoUpdateCODAL.length > 0) {
                String RegID = objGlobalMethods.getRegistrationIdOnly();
                Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                for (int i = 0; i <= objJobInfoBringtoUpdateCODAL.length - 1; i++) {
                    if (objJobInfoBringtoUpdateCODAL[i].pendingStatusUpdate == 49) {

                        JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX, 49,
                                timestampstr, "", "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

                        ////set from pip to pu
                        Boolean ifJobsExist = DAO_Jobs.checkIfJobExist(objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                        if (ifJobsExist) {
                            DAO_Jobs.updateJobsCatTypeID(1, objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 0);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 1);
                        } else {
                            String JobInfoJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL[i]);
                            DAO_Jobs.insertJobs(objJobInfoBringtoUpdateCODAL[i], 1, JobInfoJsonStr);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 1);
                        }
                    } else if (objJobInfoBringtoUpdateCODAL[i].pendingStatusUpdate == 135) {

                        Boolean DOfullySubmitted = JobsHandler.ProcessDOincForSubmission(GcmPreferences, UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                        String statuscode = "";
                        if (DOfullySubmitted) {
                            JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX, 135,
                                    timestampstr, objJobInfoBringtoUpdateCODAL[i].Reason, "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, objJobInfoBringtoUpdateCODAL[i].Receiver);
                            statuscode = "[135]";
                            ////set from dip to okp
                            DAO_Jobs.updateJobsCatTypeID(3, objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 2);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 3);
                        } else {
                            JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX, 47,
                                    timestampstr, objJobInfoBringtoUpdateCODAL[i].Reason, "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, objJobInfoBringtoUpdateCODAL[i].Receiver);
                            statuscode = "[47]";
                            ////set from dip to FD
                            DAO_Jobs.updateJobsCatTypeID(5, objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 2);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 5);
                        }


                        //add update job photo to que
                        /*if (objJobInfoBringtoUpdateCODAL[i].convertedBitmapToBase64 != null && objJobInfoBringtoUpdateCODAL[i].convertedBitmapToBase64.size() > 0) {
                            convertedBitmapToBase64AL = objJobInfoBringtoUpdateCODAL[i].convertedBitmapToBase64;
                            for (int x = 0; x <= convertedBitmapToBase64AL.size() - 1; x++) {
                                JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX,
                                        objJobInfoBringtoUpdateCODAL[i].Receiver + "\n\n" + objJobInfoBringtoUpdateCODAL[i].Reason,
                                        convertedBitmapToBase64AL.get(x), timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                            }
                        }*/
                        if (objJobInfoBringtoUpdateCODAL[i].objConvertedBitmapToBase64 != null && objJobInfoBringtoUpdateCODAL[i].objConvertedBitmapToBase64.size() > 0) {
                            convertedBitmapToBase64List = objJobInfoBringtoUpdateCODAL[i].objConvertedBitmapToBase64;
                            for (int x = 0; x <= convertedBitmapToBase64List.size() - 1; x++) {
                                JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX,
                                        statuscode + objJobInfoBringtoUpdateCODAL[i].Receiver + "\n\n" + objJobInfoBringtoUpdateCODAL[i].Reason,
                                        convertedBitmapToBase64List.get(x).convertedBitmapToBase64,
                                        timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                        !convertedBitmapToBase64List.get(x).isCallLogImg ? convertedBitmapToBase64List.get(x).FilePath : "");
                            }
                        }

                        //add update jobsignatureIA to que
                        if (objJobInfoBringtoUpdateCODAL[i].UploadJobSignatureIABitmapToBase64List != null && objJobInfoBringtoUpdateCODAL[i].UploadJobSignatureIABitmapToBase64List.size() > 0) {
                            convertedBitmapToBase64AL = objJobInfoBringtoUpdateCODAL[i].UploadJobSignatureIABitmapToBase64List;
                            for (int x = 0; x <= convertedBitmapToBase64AL.size() - 1; x++) {
                                JobsHandler.addUpdateJobSignatureIAQueALPrefs(UpdateJobInfoPreferences,
                                        RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX, convertedBitmapToBase64AL.get(x),
                                        timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                            }
                        }

                        //add update job signature to que
                        if (objJobInfoBringtoUpdateCODAL[i].SignatureStr != null && !objJobInfoBringtoUpdateCODAL[i].SignatureStr.equals("")) {
                            JobsHandler.addUpdateJobSignatureQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX,
                                    objJobInfoBringtoUpdateCODAL[i].SignatureStr, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                        }
                    } else {
                        //update part 2 FP
                        JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX, objJobInfoBringtoUpdateCODAL[i].pendingStatusUpdate,
                                timestampstr, objJobInfoBringtoUpdateCODAL[i].FailureReason, "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

                        Boolean ifJobsExist = DAO_Jobs.checkIfJobExist(objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                        if (ifJobsExist) {
                            DAO_Jobs.updateJobsCatTypeID(1, objJobInfoBringtoUpdateCODAL[i].JobsIDX);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 0);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 1);
                        } else {
                            String JobInfoJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL[i]);
                            DAO_Jobs.insertJobs(objJobInfoBringtoUpdateCODAL[i], 1, JobInfoJsonStr);
                            JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 1);
                        }

                        //add update job photo to que
                        if (objJobInfoBringtoUpdateCODAL[i].objConvertedBitmapToBase64 != null && objJobInfoBringtoUpdateCODAL[i].objConvertedBitmapToBase64.size() > 0) {
                            convertedBitmapToBase64List = objJobInfoBringtoUpdateCODAL[i].objConvertedBitmapToBase64;
                            for (int x = 0; x <= convertedBitmapToBase64List.size() - 1; x++) {
                                JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, objJobInfoBringtoUpdateCODAL[i].JobsIDX,
                                        "[" + objJobInfoBringtoUpdateCODAL[i].pendingStatusUpdate + "]" + objJobInfoBringtoUpdateCODAL[i].FailureReason, convertedBitmapToBase64List.get(x).convertedBitmapToBase64,
                                        timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                        !convertedBitmapToBase64List.get(x).isCallLogImg ? convertedBitmapToBase64List.get(x).FilePath : "");
                            }
                        }
                    }
                }

                String tempDOCODJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "tempDOCODJsonStr");
                if (tempDOCODJsonStr != "" && tempDOCODJsonStr != "[]") {
                    UpdateDOCOD updatedocod = gson.fromJson(tempDOCODJsonStr, UpdateDOCOD.class);
                    JobsHandler.addUpdateDOCODQueALPrefs(UpdateJobInfoPreferences, updatedocod);
                }

                JobsHandler.processMassGetReviewLink(GcmPreferences, UpdateJobInfoPreferences, gson);

                JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "","", true);
                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                objGlobalMethods.setGCMPrefs(GcmPreferences, "mutlipleCODjobs", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", "");

                objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, true);

                asd = true;
            } else {
                asd = false;
            }

            ReturnToPrevPage2SQLite(asd);

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at UpdateCOD_List submitWithoutCODUpdateSQLite. Exception: " + e.toString(), "", "", "", "");
            ReturnToPrevPage2SQLite(false);
        }
    }

    private void ReturnToPrevPage2SQLite(Boolean asd) {
        Gson gson = new Gson();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        String CODncActivationJobsIDXInvoiceIDXstrALJsonStr = "";
        ArrayList<long[]> CODncActivationJobsIDXInvoiceIDXstrAL = null;
        long[] JobsIDXInvoiceIDXpairArr = null;
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();

        try {
            CODncActivationJobsIDXInvoiceIDXstrALJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "CODncActivationJobsIDXInvoiceIDXstrALJsonStr");
            if (!CODncActivationJobsIDXInvoiceIDXstrALJsonStr.equals("") && !CODncActivationJobsIDXInvoiceIDXstrALJsonStr.equals("[]")) {
                CODncActivationJobsIDXInvoiceIDXstrAL = gson.fromJson(CODncActivationJobsIDXInvoiceIDXstrALJsonStr, new TypeToken<List<long[]>>() {
                }.getType());

                for (int i = CODncActivationJobsIDXInvoiceIDXstrAL.size() - 1; i >= 0; i--) {
                    JobsIDXInvoiceIDXpairArr = CODncActivationJobsIDXInvoiceIDXstrAL.get(i);
                    for (int x = 0; x <= objUpdateCODPostDataList.size() - 1; x++) {
                        if (objUpdateCODPostDataList.get(x).JobsIDX == JobsIDXInvoiceIDXpairArr[0] && objUpdateCODPostDataList.get(x).InvoiceIDX == JobsIDXInvoiceIDXpairArr[1]) {
                            CODncActivationJobsIDXInvoiceIDXstrAL.remove(i);
                        }
                    }
                }
                CODncActivationJobsIDXInvoiceIDXstrALJsonStr = gson.toJson(CODncActivationJobsIDXInvoiceIDXstrAL);
                if (CODncActivationJobsIDXInvoiceIDXstrALJsonStr.equals("[]") || CODncActivationJobsIDXInvoiceIDXstrALJsonStr.equals("")) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "CODncActivationJobsIDXInvoiceIDXstrALJsonStr", "");
                } else {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "CODncActivationJobsIDXInvoiceIDXstrALJsonStr", CODncActivationJobsIDXInvoiceIDXstrALJsonStr);
                }
            }

            if (asd) {
                setDeliveredMassStatusActivity.remarks = "";
                MassUpdateOKPwP2.massupdateokpwp2PostDataArr = null;
                String[] prefName = new String[]{"searchedval", objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr"};
                String[] prefvalue = new String[]{"", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
                objGlobalMethods.resetJobsListGroupScrollIndexNpostion(GcmPreferences);
                Integer CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                Intent parentIntent = null;
                if (!GcmPreferences.getString("isFromMainCOD", "").isEmpty()) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
                    parentIntent = new Intent(this, MainCODActivity.class);
                    MainActivity.uponLogin = 0;
                    DrawerMainActivity.DashboardOpenFromPush = false;
                    startActivity(parentIntent);
                    UpdateCOD_List.this.finish();
                } else {
                    int TotalJobsCountPerCatTypePerPostal = 0;
                    String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
                    if (Postal != "") {
                        String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
                        TotalJobsCountPerCatTypePerPostal = !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                                !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                                        : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);

                        if (TotalJobsCountPerCatTypePerPostal > 0) {
                            //back to JobsListGroupActivity
                            parentIntent = new Intent(UpdateCOD_List.this, JobsListGroupActivity.class);
                            com.xdel.luke.MainActivity.uponLogin = 0;
                            startActivity(parentIntent);
                            UpdateCOD_List.this.finish();
                        } else {
                            //back to DashboardSubActivity or AddressListRoutingActivity or AddressListNewActivity or DrawerMainActivity
                            if (!objGlobalMethods.getGCMPrefs(GcmPreferences, "isFromDashboardSub").isEmpty()) {
                                //back to DashboardSubActivity or AddressListRoutingActivity
                                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");

                                Integer TotalPIPDIPJobsCountPerPostal = DAO_Jobs.getTotalPIPDIPJobsCountPerPostal(Postal);
                                if (TotalPIPDIPJobsCountPerPostal > 0) {
                                    parentIntent = new Intent(this, DashboardSubActivity.class);
                                    MainActivity.uponLogin = 0;
                                    startActivity(parentIntent);
                                    UpdateCOD_List.this.finish();
                                } else {
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                                    objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                                    objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                                    if (DashboardSubActivity.frompage != null) {
                                        parentIntent = new Intent(this, MapsAllActivity.class);
                                    } else {
                                        //parentIntent = new Intent(this, AddressListRoutingActivity.class);
                                        parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                                    }
                                    MainActivity.uponLogin = 0;
                                    startActivity(parentIntent);
                                    UpdateCOD_List.this.finish();
                                }
                            } else {
                                //back to AddressListNewActivity or DrawerMainActivity
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                                MainActivity.uponLogin = 0;
                                int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
                                if (TotalJobsCountPerCatType == 0) {
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                                    parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                                    startActivity(parentIntent);
                                    UpdateCOD_List.this.finish();
                                } else {
                                    objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                                    objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                                    parentIntent = new Intent(this, AddressListNewActivity.class);
                                    startActivity(parentIntent);
                                    UpdateCOD_List.this.finish();
                                }
                            }
                        }
                    } else {
                        //back to DashboardSubActivity or AddressListRoutingActivity or AddressListNewActivity or DrawerMainActivity
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                        if (!objGlobalMethods.getGCMPrefs(GcmPreferences, "isFromDashboardSub").isEmpty()) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                            objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                            if (DashboardSubActivity.frompage != null) {
                                parentIntent = new Intent(this, MapsAllActivity.class);
                            } else {
                                //parentIntent = new Intent(this, AddressListRoutingActivity.class);
                                parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                            }

                            MainActivity.uponLogin = 0;
                            startActivity(parentIntent);
                            UpdateCOD_List.this.finish();
                        } else {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                            MainActivity.uponLogin = 0;
                            int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
                            if (TotalJobsCountPerCatType == 0) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                                parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                                startActivity(parentIntent);
                                UpdateCOD_List.this.finish();
                            } else {
                                objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                                objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                                parentIntent = new Intent(this, AddressListNewActivity.class);
                                startActivity(parentIntent);
                                UpdateCOD_List.this.finish();
                            }
                        }
                    }
                }
            } else {
                showErrorAlert("Fail to update some jobs.");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at ReturnToPrevPage2SQLite. Exception: " + e.toString(), "", "", "", "");
            showErrorAlert("Fail to update some jobs.");
        }
    }

    private Boolean ValidateSubmit() {
        Boolean asd = true;
        Integer CODType = -1;
        Boolean isCODJob = false;
        try {
            if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                for (int i = 0; i <= objUpdateCODPostDataList.size() - 1; i++) {
                    CODType = -1;
                    isCODJob = false;
                    CODType = objUpdateCODPostDataList.get(i).CODType;
                    isCODJob = objUpdateCODPostDataList.get(i).isCODJob;

                    if (CODType.equals(-1) && isCODJob) {
                        asd = false;
                        break;
                    }
                }
            } else {
                asd = false;
            }
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public void BackToLoginPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        Intent parentIntent;
        parentIntent = new Intent(UpdateCOD_List.this, MainActivity.class);
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
        //objGlobalMethods.stopAlarmForCommon();
        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(2);
        }
        FCMservice.notiNotRead = false;
        JobsHandler.clearGPSQue(LocationPrefs);
        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(1);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            objGlobalMethods.stopActiveForegroundSvc();
        } else {
            if (FCMservice.mNotificationManager != null) {
                FCMservice.mNotificationManager.cancel(-1);
            }
        }
        DAO_Jobs.dropLukeDB();
        startActivity(parentIntent);
        finish();
    }

    public void activateNCbutton(long JobsIDX, long InvoiceIDX) {
        int index = -1;
        try {
            objUpdateCOD_PostItemAdapter adapter = (objUpdateCOD_PostItemAdapter) listView.getAdapter();
            for (int i = 0; i <= objUpdateCODPostDataList.size() - 1; i++) {
                if (objUpdateCODPostDataList.get(i).JobsIDX == JobsIDX && objUpdateCODPostDataList.get(i).InvoiceIDX == InvoiceIDX) {
                    index = i;
                    objUpdateCODPostDataList.get(i).setEnableNCbutton(true);
                    Date dt = new Date();
                    objGlobalMethods.writeToFile("activateNCbutton set for JobsIDX " + String.valueOf(JobsIDX) + ", InvoiceIDX " + String.valueOf(InvoiceIDX), "", "", "", "");
                    break;
                }
            }

            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at UpdateCOD_List activateNCbutton. Exception: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at UpdateCOD_List","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at UpdateCOD_List onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    Bitmap bitmap = null;
    private ArrayList<objConvertedBitmapToBase64> RTNCheckListConvertedBitmapToBase64List() {
        ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = null;
        String convertedBitmapToBase64 = "";
        objConvertedBitmapToBase64 obj = null;
        boolean isCallLogImg = false;
        String photodesc = "";
        try {
            if (CheckListActivity.dataTlist != null && CheckListActivity.dataTlist.size() > 0) {
                for (int i = 0; i <= CheckListActivity.dataTlist.size() - 1; i++) {
                    String imgmCurrentPhotoPath = CheckListActivity.dataTlist.get(i).sdcardPath;
                    photodesc = objGlobalMethods.IsEmptyString(CheckListActivity.dataTlist.get(i).photodesc) ? "" : CheckListActivity.dataTlist.get(i).photodesc;
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(imgmCurrentPhotoPath, bmOptions);

                    if (bitmap != null) {
                        convertedBitmapToBase64 = convertBitmapToBase64(bitmap);
                        bitmap.recycle();
                        bitmap = null;
                    } else {
                        convertedBitmapToBase64 = "";
                    }

                    if (!convertedBitmapToBase64.equals("")) {
                        obj = new objConvertedBitmapToBase64(isCallLogImg, convertedBitmapToBase64);
                        obj.setFilePath(imgmCurrentPhotoPath);
                        obj.setPhotodesc(photodesc);
                        if (convertedBitmapToBase64List == null) {
                            convertedBitmapToBase64List = new ArrayList<>();
                            convertedBitmapToBase64List.add(obj);
                        } else {
                            convertedBitmapToBase64List.add(obj);
                        }
                    }
                }
            }
        } catch (Exception e) {
            convertedBitmapToBase64List = null;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at UpdateCOD_List RTNCheckListConvertedBitmapToBase64List. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return convertedBitmapToBase64List;
        }
    }

    private String convertBitmapToBase64(Bitmap bm) {
        String encoded;
        byte[] array;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

            array = byteArrayOutputStream.toByteArray();
            encoded = Base64.encodeToString(array, Base64.DEFAULT);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at UpdateCOD_List convertBitmapToBase64. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            return "";
        }
        return encoded;
    }


}
