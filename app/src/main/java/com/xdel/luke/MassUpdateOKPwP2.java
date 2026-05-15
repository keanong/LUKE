package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.MassUpdateOKPwP2_PostData;
import com.xdel.luke.objClasses.MassUpdateOKPwP2_PostItemAdapter;
import com.xdel.luke.objClasses.UpdateDOCOD;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCheckedItems;
import com.xdel.luke.objClasses.objConvertedBitmapToBase64;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objUpdateCOD_PostData;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MassUpdateOKPwP2 extends AppCompatActivity {

    public static ArrayList<MassUpdateOKPwP2_PostData> massupdateokpwp2PostDataArr;
    public static boolean isFailFromMassUpdateOKPwP2 = false;
    public static Boolean ListHasCOD = false;

    ConnectionDetector cd;
    Context context;
    //private String regid;
    SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    private Button btnMassOKPwP2Cancel, btnMassOKPwP2Submit;
    private ListView MassOKPwP2listView;
    private ProgressDialog pdProcessing;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    String langType = "en";
    private ProgressDialog progressDoalog;
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

        setContentView(R.layout.activity_mass_update_okpw_p2);

        MassOKPwP2listView = (ListView) findViewById(R.id.MassOKPwP2listView);
        btnMassOKPwP2Cancel = (Button) findViewById(R.id.btnMassOKPwP2Cancel);
        btnMassOKPwP2Submit = (Button) findViewById(R.id.btnMassOKPwP2Submit);

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        pdProcessing = new ProgressDialog(this);
        pdProcessing.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdProcessing.setCanceledOnTouchOutside(false);
        //pdProcessing.setMessage("Please wait. Processing.");
        pdProcessing.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));
        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
            getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        }
        //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        myApp.currentActivity = this;
        setTitle(myApp.getAppContext().getString(R.string.title_activity_mass_update_okpw_p2));

        JobsHandler.checkForceLogOut();
        context = myApp.getAppContext();

        if (!temploggerclass.isBTEnabled()) {
            objGlobalMethods.writeToFile("MassUpdateOKPwP2 !BTEnabled", "", "", "", "");
            temploggerclass.promptToOnBT();
            return;
        }

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
            // Internet Connection is not present
            Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
            // stop executing code by return
        }

        lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

        if (massupdateokpwp2PostDataArr != null) {
            loadList2();
        } else {
            String objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
            if (!objCheckedItemsJsonStr.equals("")) {
                loadList(objCheckedItemsJsonStr);
            } else {
                showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata2) + "\n" + "GcmPreferences.getString(hfJobsInfoArrJsonStr) is empty.");
            }
        }

        Gson gson = new Gson();
        String objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
        objCheckedItems[] objCheckedItemsAL = null;
        if (!objCheckedItemsJsonStr.equals("")) {
            objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
        } else {
            objCheckedItemsAL = null;
        }

        ListHasCOD = JobsHandler.checkMassUpdateHasCODtoCollectOrNot(objCheckedItemsAL, 2, GcmPreferences);
        btnMassOKPwP2Submit.setText(context.getString(ListHasCOD ? R.string.buttontext_next : R.string.buttontext_submit));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDoalog != null && progressDoalog.isShowing()) {
            progressDoalog.dismiss();
            progressDoalog = null;
        }
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at MassUpdateOKPwP2 onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
    }

    @Override
    protected void onPause() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at MassUpdateOKPwP2 onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mass_update_okpw_p2, menu);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backStep();
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

    private void showErrorAlert(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                massupdateokpwp2PostDataArr = null;
                Camera5Activity.mCurrentPhotoPath = null;
                CustomGalleryActivity.dataT = null;
                CustomGalleryActivity.imagePaths = null;
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "selectedRouteAddressJobsIDXs", "");
                Intent i;
                //i = new Intent(MassUpdateOKPwP2.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(MassUpdateOKPwP2.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showUpdateErrorDialog(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                Camera5Activity.mCurrentPhotoPath = null;
                CustomGalleryActivity.imagePaths = null;
                CustomGalleryActivity.dataT = null;
                setFailStatusActivity.imagePaths = null;
                setFailStatusActivity.dataT = null;
                setDeliveredMassStatusActivity.imagePaths = null;
                setDeliveredMassStatusActivity.SimagePaths = null;
                setDeliveredMassStatusActivity.dataT = null;
                setFailStatusActivity.selectedStatusCodesIDX = "";
                setDeliveredMassStatusActivity.ddlSelectedIndex = 0;
                setDeliveredMassStatusActivity.ishuman = "0";
                massupdateokpwp2PostDataArr = null;
                objGlobalMethods.setGCMPrefs(GcmPreferences, "searchedval", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName, "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX, "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "massupdateokpwp2PostDataArr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "selectedRouteAddressJobsIDXs", "");
                //Intent in = new Intent(MassUpdateOKPwP2.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                Intent in = new Intent(MassUpdateOKPwP2.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                com.xdel.luke.MainActivity.uponLogin = 0;
                startActivity(in);
                MassUpdateOKPwP2.this.finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadList(String objCheckedItemsJsonStr) {
        Gson gson = new Gson();
        objCheckedItems[] objCheckedItemsAL = null;
        objCheckedItems objCheckedItemsobj = null;
        massupdateokpwp2PostDataArr = null;
        MassUpdateOKPwP2_PostData postdata;
        String JobInfoJsonStr = "";
        objJobInfo objJobInfoObj;
        objJobInfo linkedJobsInfo = null;
        String CNNO = "";
        try {
            objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
            if (objCheckedItemsAL != null && objCheckedItemsAL.length > 0) {
                for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                    JobInfoJsonStr = "";
                    objJobInfoObj = null;
                    linkedJobsInfo = null;
                    CNNO = "";
                    objCheckedItemsobj = objCheckedItemsAL[i];
                    JobInfoJsonStr = objCheckedItemsobj.JobInfoJsonStr;
                    if (!JobInfoJsonStr.equals("")) {
                        objJobInfoObj = gson.fromJson(JobInfoJsonStr, objJobInfo.class);
                        if (objJobInfoObj != null && objJobInfoObj.LinkedJobs != null) {
                            if (objJobInfoObj.LinkedJobs.length > 0) {
                                linkedJobsInfo = objJobInfoObj.LinkedJobs[0];
                            }

                            if (linkedJobsInfo != null) {
                                if (objJobInfoObj.ConsignmentNote != null && !objJobInfoObj.ConsignmentNote.equals("")) {
                                    CNNO = objJobInfoObj.ConsignmentNote;
                                } else {
                                    CNNO = objJobInfoObj.JobNo;
                                }
                                postdata = new MassUpdateOKPwP2_PostData(String.valueOf(objJobInfoObj.JobsIDX), String.valueOf(linkedJobsInfo.JobsIDX), CNNO, "", "0", "", linkedJobsInfo, null);
                                if (massupdateokpwp2PostDataArr == null) {
                                    massupdateokpwp2PostDataArr = new ArrayList<MassUpdateOKPwP2_PostData>();
                                    massupdateokpwp2PostDataArr.add(postdata);
                                } else {
                                    massupdateokpwp2PostDataArr.add(postdata);
                                }
                            }
                        }
                    }
                }

                if (massupdateokpwp2PostDataArr != null && massupdateokpwp2PostDataArr.size() > 0) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    String massupdateokpwp2PostDataArrPrefStr = gson.toJson(massupdateokpwp2PostDataArr);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "massupdateokpwp2PostDataArr", massupdateokpwp2PostDataArrPrefStr);

                    MassUpdateOKPwP2_PostItemAdapter adapter = new MassUpdateOKPwP2_PostItemAdapter(this, massupdateokpwp2PostDataArr);
                    MassOKPwP2listView.setAdapter(adapter);
                } else {
                    objGlobalMethods.writeToFile("Excepion caught at MassUpdateOKPwP2 loadList. massupdateokpwp2PostDataArr is null or size 0.", "", "", "", "");
                    showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata2) + "\n" + "massupdateokpwp2PostDataArr.size = 0");
                }
            } else {
                objGlobalMethods.writeToFile("Excepion caught at MassUpdateOKPwP2 loadList. objCheckedItemsAL is null or size 0.", "", "", "", "");
                showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata2) + "\n" + "objCheckedItemsAL.length = 0");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Excepion caught at MassUpdateOKPwP2 loadList. Exception: " + e.toString(), "", "", "", "");
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata2) + "\n" + e.getMessage().toString());
        }

    }

    private void loadList2() {
        try {
            MassUpdateOKPwP2_PostItemAdapter adapter = new MassUpdateOKPwP2_PostItemAdapter(this, massupdateokpwp2PostDataArr);
            MassOKPwP2listView.setAdapter(adapter);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Excepion caught at MassUpdateOKPwP2 loadList3. Exception: " + e.toString(), "", "", "", "");
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata2) + "\n" + e.getMessage().toString());
        }
    }

    public void btnCancelClick(View view) {
        backStep();
    }

    private void backStep() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        massupdateokpwp2PostDataArr = null;
        Camera5Activity.mCurrentPhotoPath = null;
        objGlobalMethods.setGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr", "");
        Intent parentIntent = new Intent(this, setDeliveredMassStatusActivity.class);
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    public void BackToLoginPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        massupdateokpwp2PostDataArr = null;
        Camera5Activity.mCurrentPhotoPath = null;
        CustomGalleryActivity.dataT = null;
        CustomGalleryActivity.imagePaths = null;
        setFailStatusActivity.dataT = null;
        setFailStatusActivity.imagePaths = null;
        setDeliveredMassStatusActivity.dataT = null;
        setDeliveredMassStatusActivity.imagePaths = null;
        setDeliveredMassStatusActivity.SimagePaths = null;
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "massupdateokpwp2PostDataArr", "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX, "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objUpdateCODPostDataListJsonStr", "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objJobInfoBringtoUpdateCODListJsonStr", "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "mutlipleCODjobs", "");
        objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "ojisPage", "");

        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "selectedRouteAddressJobsIDXs", "");

        Intent parentIntent;
        parentIntent = new Intent(MassUpdateOKPwP2.this, MainActivity.class);
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
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

    private Boolean validateSubmission() {
        Boolean ok = true;
        try {
            if (massupdateokpwp2PostDataArr != null && massupdateokpwp2PostDataArr.size() > 0) {
                for (int i = 0; i <= massupdateokpwp2PostDataArr.size() - 1; i++) {
                    if (massupdateokpwp2PostDataArr.get(i).StatusCodesIDX.equals("0") || massupdateokpwp2PostDataArr.get(i).StatusCodesIDX.equals("")) {
                        ok = false;
                        break;
                    }
                }
            } else if (massupdateokpwp2PostDataArr == null || massupdateokpwp2PostDataArr.size() == 0) {
                ok = false;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at MassUpdateOKPwP2 validateSubmission. Exception: " + e.toString(), "", "", "", "");
            ok = false;
        } finally {
            return ok;
        }
    }

    public void btnSubmitClick2(View view) {
        if (validateSubmission()) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            Gson gson = new Gson();
            Boolean P2hasCOD = false;
            String objUpdateCODPostDataListJsonStr = "";
            String objJobInfoBringtoUpdateCODListJsonStr = "";
            String mutlipleCODjobs = "";
            List<objUpdateCOD_PostData> objUpdateCODPostDataList = null;
            List<objJobInfo> objJobInfoBringtoUpdateCODList = null;
            String part1cnno = "";
            String RegID = objGlobalMethods.getRegistrationIdOnly();
            if (ListHasCOD) {
                //check for P2, check if have COD, add to CODlist & update status list, if no COD, add to update status list
                objJobInfo ji = null;

                objUpdateCODPostDataListJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "mutlipleCODjobs");
                if (objUpdateCODPostDataListJsonStr.equals("") || objUpdateCODPostDataListJsonStr.equals("[]")) {
                    objUpdateCODPostDataList = null;
                } else {
                    objUpdateCODPostDataList = gson.fromJson(objUpdateCODPostDataListJsonStr, new TypeToken<List<objUpdateCOD_PostData>>() {
                    }.getType());
                }

                objJobInfoBringtoUpdateCODListJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr");
                if (objJobInfoBringtoUpdateCODListJsonStr.equals("") || objJobInfoBringtoUpdateCODListJsonStr.equals("[]")) {
                    objJobInfoBringtoUpdateCODList = null;
                } else {
                    objJobInfoBringtoUpdateCODList = gson.fromJson(objJobInfoBringtoUpdateCODListJsonStr, new TypeToken<List<objJobInfo>>() {
                    }.getType());
                }

                for (int i = 0; i <= massupdateokpwp2PostDataArr.size() - 1; i++) {
                    part1cnno = "";
                    P2hasCOD = false;
                    ji = massupdateokpwp2PostDataArr.get(i).currJobInfo;
                    if (objJobInfoBringtoUpdateCODList != null && objJobInfoBringtoUpdateCODList.size() > 0) {
                        for (int x = 0; x <= objJobInfoBringtoUpdateCODList.size() - 1; x++) {
                            if (objJobInfoBringtoUpdateCODList.get(x).LinkedJobs != null && objJobInfoBringtoUpdateCODList.get(x).LinkedJobs.length > 0 &&
                                    objJobInfoBringtoUpdateCODList.get(x).LinkedJobs[0].JobsIDX == ji.JobsIDX) {
                                if (objJobInfoBringtoUpdateCODList.get(x).ConsignmentNote != null && !objJobInfoBringtoUpdateCODList.get(x).ConsignmentNote.equals("")) {
                                    part1cnno = objJobInfoBringtoUpdateCODList.get(x).ConsignmentNote;
                                    break;
                                } else {
                                    part1cnno = objJobInfoBringtoUpdateCODList.get(x).JobNo;
                                    break;
                                }
                            }
                        }
                    } else {
                        if (ji.ConsignmentNote != null && !ji.ConsignmentNote.equals("")) {
                            part1cnno = ji.ConsignmentNote;
                        } else {
                            part1cnno = ji.JobNo;
                        }
                    }
                    if (ji.pendingStatusUpdate == 49) {
                        P2hasCOD = JobsHandler.checkForCODwithCatType(ji, 0);
                        if (P2hasCOD) {
                            objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, ji, 0, true,
                                    RegID, true, part1cnno, "");
                        }
                    } else {
                        for (int x = 0; x <= objUpdateCODPostDataList.size() - 1; x++) {
                            if (objUpdateCODPostDataList.get(x).JobsIDX == ji.JobsIDX) {
                                objUpdateCODPostDataList.remove(x);
                                break;
                            }
                        }
                    }

                    objJobInfoBringtoUpdateCODList = JobsHandler.RTNobjJobInfoBringtoUpdateCODList(objJobInfoBringtoUpdateCODList, ji);
                }

                objJobInfo[] objJobInfoBringtoUpdateCODAL = objJobInfoBringtoUpdateCODList.toArray(new objJobInfo[objJobInfoBringtoUpdateCODList.size()]);
                objJobInfoBringtoUpdateCODListJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL);
                if (objJobInfoBringtoUpdateCODListJsonStr.equals("[]")) {
                    objJobInfoBringtoUpdateCODListJsonStr = "";
                }
                objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", objJobInfoBringtoUpdateCODListJsonStr);

                objUpdateCOD_PostData[] objUpdateCODPostDataAL = objUpdateCODPostDataList.toArray(new objUpdateCOD_PostData[objUpdateCODPostDataList.size()]);
                mutlipleCODjobs = gson.toJson(objUpdateCODPostDataAL);
                if (mutlipleCODjobs.equals("[]")) {
                    mutlipleCODjobs = "";
                }
                objGlobalMethods.setGCMPrefs(GcmPreferences, "mutlipleCODjobs", mutlipleCODjobs);

                //ojisPage == 0 == JobInfoActivity | ojisPage == 1 == JobsListGroupActivity | ojisPage == 2 == setDeliveredStatusActivity |
                //ojisPage == 3 == setDeliveredMassStatusActivity | ojisPage == 4 == MassUpdateOKPwP2 |
                objGlobalMethods.setGCMPrefs(GcmPreferences, "ojisPage", "4");
                Intent intent = new Intent(this, UpdateCOD_List.class);
                startActivity(intent);
                finish();
            } else {
                submitStr8();
            }
        } else {
            showDialog(myApp.getAppContext().getString(R.string.alert_unableupdatep1p2));
        }
    }

    private void submitStr8() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdatep1p2));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", update job status
                try {
                    objGlobalMethods.writeToFile("Click to mass OKP jobs both with and without part 2.", "", "", "", "");
                    updateAllJobsStatus0SQLite();

                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception caught at setDeliveredStatusActivity btnSubmitClick. " + e.getMessage().toString(), "", "", "", "");
                    showDialog(myApp.getAppContext().getString(R.string.alert_errorupdatep1p2));
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

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(1);
        }
    };

    //checked
    private void updateAllJobsStatus0SQLite() {
        Date dt = new Date();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        String objJobInfoBringtoUpdateCODListJsonStr = "";
        Gson gson = new Gson();
        Long l = dt.getTime();
        final String timestampstr = "/Date(" + l + ")/";

        try {
            //=========================update all part 1 first==================================================================================
            objJobInfoBringtoUpdateCODListJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr");
            final objJobInfo[] objJobInfoBringtoUpdateCODAL = objJobInfoBringtoUpdateCODListJsonStr.equals("") || objJobInfoBringtoUpdateCODListJsonStr.equals("[]") ? null : gson.fromJson(objJobInfoBringtoUpdateCODListJsonStr, objJobInfo[].class);

            if (objJobInfoBringtoUpdateCODAL != null && objJobInfoBringtoUpdateCODAL.length > 0) {
                progressDoalog = new ProgressDialog(this);
                progressDoalog.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdatesp1));
                progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                final int totalcount = objJobInfoBringtoUpdateCODAL.length + massupdateokpwp2PostDataArr.size();
                progressDoalog.setMax(totalcount);
                progressDoalog.setCanceledOnTouchOutside(false);
                progressDoalog.show();
                final Thread t = new Thread() {
                    Gson gson = new Gson();

                    @Override
                    public void run() {
                        int count = 0;
                        try {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                            String RegID = objGlobalMethods.getRegistrationIdOnly();
                            //=========================update all part 1 first==================================================================================
                            for (int i = 0; i <= objJobInfoBringtoUpdateCODAL.length - 1; i++) {
                                Thread.sleep(200);
                                handle.sendMessage(handle.obtainMessage());
                                objJobInfo ji = null;
                                ji = objJobInfoBringtoUpdateCODAL[i];
                                Boolean DOfullySubmitted = JobsHandler.ProcessDOincForSubmission(GcmPreferences, UpdateJobInfoPreferences, RegID, ji.JobsIDX);

                                if (DOfullySubmitted) {
                                    addJobStatusUpdateToQueSQLite(GcmPreferences, UpdateJobInfoPreferences, ji, timestampstr);
                                } else {
                                    ji.setpendingStatusUpdate(47);
                                    addJobStatusUpdateToQueSQLite(GcmPreferences, UpdateJobInfoPreferences, ji, timestampstr);
                                }
                            }
                            //=========================update all part 1 first==================================================================================
                            //=========================update all part 2========================================================================================
                            for (int i = 0; i <= massupdateokpwp2PostDataArr.size() - 1; i++) {
                                Thread.sleep(200);
                                handle.sendMessage(handle.obtainMessage());
                                objJobInfo ji = null;
                                ji = massupdateokpwp2PostDataArr.get(i).currJobInfo;
                                addJobStatusUpdateToQueSQLite(GcmPreferences, UpdateJobInfoPreferences, ji, timestampstr);
                            }
                            //=========================update all part 2========================================================================================

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

                            //=========================redirect back to which page==============================================================================
                            String[] prefName = new String[]{"mutlipleCODjobs", "objJobInfoBringtoUpdateCODListJsonStr", objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX};
                            String[] prefvalue = new String[]{"", "", "", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                            objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
                            objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
                            objGlobalMethods.resetJobsListGroupScrollIndexNpostion(GcmPreferences);

                            objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, true);

                            resetAddressListActivityhfJobsInfoArrJsonStr0SQLite(GcmPreferences);

                        } catch (Exception e) {
                            progressDoalog.dismiss();
                            objGlobalMethods.writeToFile("Exception caught at MassUpdateOKPwP2 updateAllJobsStatus0SQLite progress dialog thread. Exception: " + e.getMessage().toString(), "", "", "", "");
                            JobsHandler.sendErrorEmail("Exception caught at MassUpdateOKPwP2 updateAllJobsStatus0SQLite progress dialog thread. Exception: " + e.getMessage().toString());
                            resetAddressListActivityhfJobsInfoArrJsonStr0SQLite(GcmPreferences);
                        }
                    }
                };
                t.start();
                //=========================redirect back to which page=============================================================================
            } else {

                showUpdateErrorDialog(myApp.getAppContext().getString(R.string.alert_unableupdatep1p21by1));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at MassUpdateOKPwP2 updateAllJobsStatus0SQLite. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
            showUpdateErrorDialog(myApp.getAppContext().getString(R.string.alert_unableupdatep1p21by1));
        }
    }

    private void addJobStatusUpdateToQueSQLite(SharedPreferences aGcmPreferences, SharedPreferences aUpdateJobInfoPreferences, objJobInfo ji, String atimestampstr) {
        String aJobsIDX = "";
        ArrayList<String> convertedBitmapToBase64AL = null;
        ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = null;
        ArrayList<objConvertedBitmapToBase64> CheckListconvertedBitmapToBase64List = null;
        if (ji != null) {
            aJobsIDX = String.valueOf(ji.JobsIDX);
        }

        String RegID = objGlobalMethods.getRegistrationIdOnly();
        try {
            Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
            int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
            if (ji.pendingStatusUpdate == 135) {
                JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(aUpdateJobInfoPreferences, RegID, ji.JobsIDX, ji.pendingStatusUpdate, atimestampstr, ji.Reason, "",
                        lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, ji.Receiver);

                //add photos from checklist
                CheckListconvertedBitmapToBase64List = RTNCheckListConvertedBitmapToBase64List();
                if (CheckListconvertedBitmapToBase64List != null && CheckListconvertedBitmapToBase64List.size() > 0) {
                    for (int x = 0; x <= CheckListconvertedBitmapToBase64List.size() - 1; x++) {
                        JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(aUpdateJobInfoPreferences, RegID, ji.JobsIDX,
                                !objGlobalMethods.IsEmptyString(CheckListconvertedBitmapToBase64List.get(x).photodesc) ? "[" + ji.pendingStatusUpdate + "]" + CheckListconvertedBitmapToBase64List.get(x).photodesc : "[" + ji.pendingStatusUpdate + "]" + ji.Receiver,
                                CheckListconvertedBitmapToBase64List.get(x).convertedBitmapToBase64,
                                atimestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                                CheckListconvertedBitmapToBase64List.get(x).FilePath);
                    }
                }

            } else {
                JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(aUpdateJobInfoPreferences, RegID, ji.JobsIDX, ji.pendingStatusUpdate, atimestampstr, ji.FailureReason, "",
                        lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, ji.Receiver);
            }

            //add update job photo to que
            if (ji.objConvertedBitmapToBase64 != null && ji.objConvertedBitmapToBase64.size() > 0) {
                convertedBitmapToBase64List = ji.objConvertedBitmapToBase64;
                for (int x = 0; x <= convertedBitmapToBase64List.size() - 1; x++) {
                    JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(aUpdateJobInfoPreferences, RegID, ji.JobsIDX,
                            "[" + ji.pendingStatusUpdate + "]" + ji.Receiver, convertedBitmapToBase64List.get(x).convertedBitmapToBase64,
                            atimestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                            convertedBitmapToBase64List.get(x).FilePath);
                }
            }

            //add update jobsignatureIA to que
            if (ji.UploadJobSignatureIABitmapToBase64List != null && ji.UploadJobSignatureIABitmapToBase64List.size() > 0) {
                convertedBitmapToBase64AL = ji.UploadJobSignatureIABitmapToBase64List;
                for (int x = 0; x <= convertedBitmapToBase64AL.size() - 1; x++) {
                    JobsHandler.addUpdateJobSignatureIAQueALPrefs(UpdateJobInfoPreferences,
                            RegID, ji.JobsIDX, convertedBitmapToBase64AL.get(x),
                            atimestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
                }
            }

            //add update job signature to que
            if (ji.SignatureStr != null && !ji.SignatureStr.equals("")) {
                JobsHandler.addUpdateJobSignatureQueALPrefs(aUpdateJobInfoPreferences,
                        RegID, ji.JobsIDX, ji.SignatureStr, atimestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy);
            }

            if (ji.pendingStatusUpdate == 49) {
                ////set from pip to pu
                Gson gson = new Gson();
                String p2JobInfoJsonStr = gson.toJson(ji);
                DAO_Jobs.insertJobs(ji, 1, p2JobInfoJsonStr);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(aGcmPreferences, 0);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(aGcmPreferences, 1);
                //DAO_Jobs.updateJobsCatTypeID(1, ji.JobsIDX);
            } else if (ji.pendingStatusUpdate == 135) {
                ////set from dip to okp
                DAO_Jobs.updateJobsCatTypeID(3, ji.JobsIDX);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(aGcmPreferences, 2);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(aGcmPreferences, 3);
            } else {
                //add Part 2 jobinfo to fp pref
                Gson gson = new Gson();
                String p2JobInfoJsonStr = gson.toJson(ji);
                DAO_Jobs.insertJobs(ji, 4, p2JobInfoJsonStr);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(aGcmPreferences, 0);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(aGcmPreferences, 4);
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at MassUpdateOKPwP2 addJobStatusUpdateToQueSQLite. JobsIDX " + aJobsIDX + " not added to que. Exception: "
                    + e.toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void resetAddressListActivityhfJobsInfoArrJsonStr0SQLite(SharedPreferences gcmp) {
        Intent parentIntent = null;
        try {
            Integer CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            int TotalJobsCountPerCatTypePerPostal = 0;
            String Postal = objGlobalMethods.getGCMPrefs(gcmp, "desPostal");
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();

            if (Postal != "") {
                String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
                /*TotalJobsCountPerCatTypePerPostal = !objGlobalMethods.IsDefaultVRPpage() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                        !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                                : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);*/
                TotalJobsCountPerCatTypePerPostal = !isVrpView() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                        !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                                : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);

                if (TotalJobsCountPerCatTypePerPostal > 0) {
                    //back to JobsListGroupActivity
                    parentIntent = new Intent(MassUpdateOKPwP2.this, JobsListGroupActivity.class);
                    com.xdel.luke.MainActivity.uponLogin = 0;
                    startActivity(parentIntent);
                    MassUpdateOKPwP2.this.finish();
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
                            MassUpdateOKPwP2.this.finish();
                        } else {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                            objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);

                            if (DashboardSubActivity.frompage != null) {
                                parentIntent = new Intent(this, MapsAllActivity.class);
                            } else {
                                //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                                parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                            }

                            MainActivity.uponLogin = 0;
                            startActivity(parentIntent);
                            MassUpdateOKPwP2.this.finish();
                        }
                    } else {
                        //back to AddressListNewActivity or DrawerMainActivity
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                        MainActivity.uponLogin = 0;
                        int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
                        if (TotalJobsCountPerCatType == 0) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                            //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                            parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                            startActivity(parentIntent);
                            MassUpdateOKPwP2.this.finish();
                        } else {
                            objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                            objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                            parentIntent = new Intent(this, AddressListNewActivity.class);
                            startActivity(parentIntent);
                            MassUpdateOKPwP2.this.finish();
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
                        //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                        parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                    }

                    MainActivity.uponLogin = 0;
                    startActivity(parentIntent);
                    MassUpdateOKPwP2.this.finish();
                } else {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                    MainActivity.uponLogin = 0;
                    int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
                    if (TotalJobsCountPerCatType == 0) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                        //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                        parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                        startActivity(parentIntent);
                        MassUpdateOKPwP2.this.finish();
                    } else {
                        objGlobalMethods.resetAddressListNewScrollIndexNpostion(GcmPreferences);
                        objGlobalMethods.resetAddressListRoutingScrollIndexNpostion(GcmPreferences);
                        parentIntent = new Intent(this, AddressListNewActivity.class);
                        startActivity(parentIntent);
                        MassUpdateOKPwP2.this.finish();
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at setDeliveredMassStatusActivity resetAddressListActivityhfJobsInfoArrJsonStr2SQLite. Exception: " + e.toString(), "", "", "", "");
            objGlobalMethods.setGCMPrefs(gcmp, "isFromDashboardSub", "");
            objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTypePrefName, "-1");
            objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTitlePrefName, "");
            objGlobalMethods.setGCMPrefs(gcmp, "selectedRouteAddressJobsIDXs", "");
            //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            MassUpdateOKPwP2.this.finish();
        }
    }

    private ArrayList<objConvertedBitmapToBase64> RTNCheckListConvertedBitmapToBase64List() {
        ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = null;
        String convertedBitmapToBase64 = "";
        objConvertedBitmapToBase64 obj = null;
        boolean isCallLogImg = false;
        String photodesc = "";
        Bitmap bitmap = null;
        try {
            /*if (CheckListActivity.PhotoImagePathList != null && CheckListActivity.PhotoImagePathList.size() > 0) {
                for (int i = 0; i <= CheckListActivity.PhotoImagePathList.size() - 1; i++) {
                    String imgmCurrentPhotoPath = CheckListActivity.PhotoImagePathList.get(i);
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
                        if (convertedBitmapToBase64List == null) {
                            convertedBitmapToBase64List = new ArrayList<>();
                            convertedBitmapToBase64List.add(obj);
                        } else {
                            convertedBitmapToBase64List.add(obj);
                        }
                    }
                }
            }*/
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
            objGlobalMethods.writeToFile("Exception at MassUpdateOKPwP2 RTNCheckListConvertedBitmapToBase64List. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
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
            objGlobalMethods.writeToFile("Exception at MassUpdateOKPwP2 convertBitmapToBase64. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            return "";
        }
        return encoded;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at MassUpdateOKPwP2","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MassUpdateOKPwP2 onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }



}
