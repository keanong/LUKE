package com.xdel.luke.HRM_GC_Schedule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.HRM.ProfileActivity;
import com.xdel.luke.HandTakeOver.L2_Get_WorkScheduleResult;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GCScheduleActivity extends AppCompatActivity {

    ConnectionDetector cd;
    Context context;
    //private String regid;
    TextView txtDateFrom, txtDateTo;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    private ProgressDialog pdPoints;

    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    private GCscheduleExpandable_Adapter expandableSortedSQLiteListAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<GCschedule_PostData>> listDataChildSortedSQLite2;
    private ArrayList<String> headerAl;
    private ArrayList itemAL;
    private List<GCschedule_PostData> subGroupItemSqliteAL;
    private ExpandableListView expListView;
    DatePickerDialog picker;
    SimpleDateFormat aformat = new SimpleDateFormat("dd/MM/yyyy");

    String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;

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
                        String stack = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("ActivityNotFoundException at GCscheduleActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: "
                                + e.toString() + "\nStack: " + stack, "", "", "", "");
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
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at GCscheduleActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at GCscheduleActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at GCscheduleActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    public void BackToLoginPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "gcsch", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "summaryfromdate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "summarytodate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "summaryfromdate2", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "summarytodate2", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        Intent parentIntent;
        parentIntent = new Intent(this, MainActivity.class);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");

        setContentView(R.layout.activity_gc_schedule);

        setTitle(myApp.getAppContext().getString(R.string.gcscheduletitle));
        pdPoints = new ProgressDialog(this);
        pdPoints.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdPoints.setCanceledOnTouchOutside(false);
        pdPoints.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveGCschedule));

        txtDateFrom = findViewById(R.id.txtDateFrom);
        txtDateTo = findViewById(R.id.txtDateTo);

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        expListView = (ExpandableListView) findViewById(R.id.expListView);
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
                objGlobalMethods.writeToFile("GCScheduleActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

            lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

            objGlobalMethods.resetAllImageRelatedStaticVariables(false);
            JobsHandler.checkForceLogOut();
            context = myApp.getAppContext();


            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];

            if (username.equals("") && password.equals("")) {
                BackToLoginPage2();
            }

            objGlobalMethods.clearOnResumeDriverPointMain_Activity_n_DriverPointSummaryActivityGcmPrefs(GcmPreferences);

            objGlobalMethods.checkGPSnPerMinRunAlarmRunning(ishomesuser);
            JobsHandler.clearJobsRetrievalActivityPrefs(GcmPreferences);
            DashboardSubActivity.frompage = null;

            String RegID = objGlobalMethods.getRegistrationIdOnly();
            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);

            String JsonStr;
            String summaryfromdate1, summarytodate1, summaryfromdate2, summarytodate2;
            JsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "gcsch");
            summaryfromdate1 = objGlobalMethods.getGCMPrefs(GcmPreferences, "summaryfromdate");
            summarytodate1 = objGlobalMethods.getGCMPrefs(GcmPreferences, "summarytodate");
            summaryfromdate2 = objGlobalMethods.getGCMPrefs(GcmPreferences, "summaryfromdate2");
            summarytodate2 = objGlobalMethods.getGCMPrefs(GcmPreferences, "summarytodate2");

            int[] prevmonthstart = objGlobalMethods.getPrevMonthDateRangeRangeDMYLongValue();
            //int[] currmonthstart = objGlobalMethods.getCurrMonthDateRangeRangeDMYLongValue();
            //int[] currmonthend = objGlobalMethods.getCurrMonthToDateRangeDMYLongValue();
            int[] currmonthend = objGlobalMethods.getCurrMonthFullDateRangePlus1MthDMYLongValue();
            Calendar pmc = Calendar.getInstance();
            Calendar cmc = Calendar.getInstance();
            pmc.set(Calendar.DAY_OF_MONTH, prevmonthstart[0]);
            pmc.set(Calendar.MONTH, prevmonthstart[1]);
            pmc.set(Calendar.YEAR, prevmonthstart[2]);

            cmc.set(Calendar.DAY_OF_MONTH, currmonthend[0]);
            cmc.set(Calendar.MONTH, currmonthend[1]);
            cmc.set(Calendar.YEAR, currmonthend[2]);

            showDate(txtDateFrom, pmc);
            showDate(txtDateTo, cmc);
            txtDateFrom.setOnClickListener(null);
            txtDateTo.setOnClickListener(null);
            txtDateFrom.setOnClickListener(txtDateOnClick(pmc, cmc));
            txtDateTo.setOnClickListener(txtDateOnClick(pmc, cmc));

            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                Toast.makeText(context, context.getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                if (!objGlobalMethods.IsEmptyString(JsonStr)) {
                    Gson gson = new Gson();
                    List<objGCSchedule[]> list = gson.fromJson(JsonStr, new TypeToken<List<objGCSchedule[]>>() {
                    }.getType());
                    loadList(list, summaryfromdate1, summarytodate1, summaryfromdate2, summarytodate2);
                    showSavedDate(txtDateFrom, summaryfromdate1, txtDateTo, summarytodate2, summarytodate1);
                } else {
                    showErrorAlert(context.getString(R.string.alertpointunableloadinternet));
                }
                // stop executing code by return
            } else {
                if (!objGlobalMethods.IsEmptyString(JsonStr) &&
                        (!objGlobalMethods.IsEmptyString(summaryfromdate1) && !objGlobalMethods.IsEmptyString(summarytodate1) && !objGlobalMethods.IsEmptyString(summaryfromdate2) && !objGlobalMethods.IsEmptyString(summarytodate2)) ||
                        (!objGlobalMethods.IsEmptyString(summaryfromdate1) && !objGlobalMethods.IsEmptyString(summarytodate1) && objGlobalMethods.IsEmptyString(summaryfromdate2) && objGlobalMethods.IsEmptyString(summarytodate2))) {
                    Gson gson = new Gson();
                    List<objGCSchedule[]> list = gson.fromJson(JsonStr, new TypeToken<List<objGCSchedule[]>>() {
                    }.getType());
                    loadList(list, summaryfromdate1, summarytodate1, summaryfromdate2, summarytodate2);
                    showSavedDate(txtDateFrom, summaryfromdate1, txtDateTo, summarytodate2, summarytodate1);
                } else {
                    btnInquire_Click(null);
                }

                //JobsHandler.doubleCheckUpdatePIPSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx, false);
                JobsHandler.doubleCheckUpdatePIPSQLiteNew(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at GCscheduleActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at GCscheduleActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gc_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*//noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void showErrorAlert(String msg) {
        MainActivity.uponLogin = 0;
        DrawerMainActivity.DashboardOpenFromPush = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, "gcsch", "summaryfromdate",
                        "summarytodate", "summaryfromdate2", "summarytodate2"};
                String[] prefvalue = new String[]{"-1", "", "", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                Intent i;
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        Intent parentIntent = new Intent(this, ProfileActivity.class);
        MainActivity.uponLogin = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String[] prefName = new String[]{"gcsch", "summaryfromdate", "summarytodate", "summaryfromdate2", "summarytodate2"};
        String[] prefvalue = new String[]{"", "", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    private void loadList(List<objGCSchedule[]> list, String summaryfromdate1, String summarytodate1, String summaryfromdate2, String summarytodate2) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        objGCSchedule[] rs = null;
        objGCSchedule obj = null;
        objGCSchedule sobj = null;
        objGCSchedule eobj = null;
        GCschedule_PostData postdata = null;
        GCschedule_PostData prevpostdata = null;
        Date start = null;
        Date end = null;
        String startstr = "";
        String endstr = "";
        String headerStr1;
        String FromTo = "";
        headerAl = new ArrayList<String>();
        itemAL = new ArrayList();
        listDataHeader = new ArrayList<String>();
        listDataChildSortedSQLite2 = new HashMap<String, List<GCschedule_PostData>>();
        Context ctx = myApp.getAppContext();
        boolean addnew = false;
        boolean isToday = false;
        try {
            if (list != null && list.size() > 0) {
                for (int i = 0; i <= list.size() - 1; i++) {
                    rs = list.get(i);
                    if (rs != null && rs.length > 0) {
                        subGroupItemSqliteAL = new ArrayList<GCschedule_PostData>();
                        sobj = rs[0];
                        eobj = rs[rs.length - 1];
                        start = objGlobalMethods.JsonDateToDate(sobj.Date);
                        end = objGlobalMethods.JsonDateToDate(eobj.Date);
                        startstr = start != null ? formatter.format(start) : "";
                        endstr = end != null ? formatter.format(end) : "";
                        headerStr1 = startstr + " " + ctx.getString(R.string.pointsummarydateto) + " " + endstr;
                        if (!headerAl.contains(headerStr1)) {
                            headerAl.add(headerStr1);
                        }

                        for (int x = 0; x <= rs.length - 1; x++) {
                            addnew = false;
                            isToday = false;
                            obj = rs[x];

                            Date d = objGlobalMethods.JsonDateToDate(obj.Date);
                            isToday = JobsHandler2.isToday(d);
                            FromTo = (obj.FromTime + " - " + obj.ToTime);

                            if (subGroupItemSqliteAL != null && subGroupItemSqliteAL.size() > 0) {
                                prevpostdata = subGroupItemSqliteAL.get(subGroupItemSqliteAL.size() - 1);
                                if (prevpostdata != null && prevpostdata.DateString.equals(formatter.format(d))) {
                                    if (prevpostdata.Remarks.contains("NOT SCHEDULED") && !obj.Remarks.contains("NOT SCHEDULED") && (!obj.FromTime.equals(obj.ToTime))) {
                                        prevpostdata.setRemarks(obj.Remarks);
                                        prevpostdata.setFrom(obj.FromTime);
                                        prevpostdata.setTo(obj.ToTime);
                                        prevpostdata.setFromTo(FromTo);
                                        subGroupItemSqliteAL.set(subGroupItemSqliteAL.size() - 1, prevpostdata);
                                    } else if (!prevpostdata.Remarks.contains("NOT SCHEDULED") && obj.Remarks.contains("NOT SCHEDULED") && (obj.FromTime.equals(obj.ToTime))) {
                                        //do nothing
                                    } else if (!prevpostdata.FromTo.equals(FromTo)) {
                                        subGroupItemSqliteAL.get(subGroupItemSqliteAL.size() - 1).setFromTo(prevpostdata.FromTo + "\n" + FromTo);
                                    }
                                } else {
                                    addnew = true;
                                    postdata = new GCschedule_PostData(formatter.format(d), obj.Remarks, obj.FromTime, obj.ToTime, FromTo, isToday);
                                }

                            } else {
                                addnew = true;
                                postdata = new GCschedule_PostData(formatter.format(d), obj.Remarks, obj.FromTime, obj.ToTime, FromTo, isToday);
                            }

                            if (addnew) {
                                subGroupItemSqliteAL.add(postdata);
                                if (i == 0) {
                                    if (x == 0) {
                                        itemAL.add(subGroupItemSqliteAL);
                                    } else if (x > 0) {
                                        itemAL.set(0, subGroupItemSqliteAL);
                                    }
                                } else if (i == 1) {
                                    if (x == 0) {
                                        itemAL.add(subGroupItemSqliteAL);
                                    } else {
                                        itemAL.set(1, subGroupItemSqliteAL);
                                    }
                                } else {
                                    if (x == 0) {
                                        itemAL.add(subGroupItemSqliteAL);
                                    } else {
                                        itemAL.set(2, subGroupItemSqliteAL);
                                    }
                                }
                            }

                        }
                    }
                }

                if (itemAL != null && itemAL.size() > 0) {
                    List<GCschedule_PostData> al = null;
                    GCschedule_PostData ob = null;
                    for (int  i = 0; i <= itemAL.size() -1; i++) {
                        al = (List<GCschedule_PostData>) itemAL.get(i);
                        if (al != null && al.size() > 0) {
                            for (int o = 0; o <= al.size() -1; o++) {
                                ob = al.get(o);
                                if (ob.From.equals(ob.To)) {
                                    ob.FromTo = "";
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i <= headerAl.size() - 1; i++) {
                    listDataHeader.add(headerAl.get(i));
                    listDataChildSortedSQLite2.put(headerAl.get(i), (List<GCschedule_PostData>) itemAL.get(i));
                }

                if (headerAl.size() > 0) {
                    expandableSortedSQLiteListAdapter = new GCscheduleExpandable_Adapter(this, listDataHeader, listDataChildSortedSQLite2);
                    expandableSortedSQLiteListAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

                    // setting list adapter
                    expListView.setAdapter(expandableSortedSQLiteListAdapter);
                    setGroupIndicatorToRight();
                    //expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                } else {
                    showErrorAlert(ctx.getString(R.string.nosch));
                }

            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GCScheduleActivity loadList. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
            showErrorAlert(ctx.getString(R.string.alert_errorloadingsch));
        }
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private void expandSortedSQLiteList(GCscheduleExpandable_Adapter adapter) {
        /*for (int i = 0; i <= adapter.getGroupCount() - 1; i++) {
            expListView.expandGroup(i);
        }*/
        //expListView.expandGroup(adapter.getGroupCount() - 1);
        int c = adapter.getGroupCount();
        expListView.expandGroup(c > 1 ? 1 : 0);
    }

    private class getSchedule extends AsyncTask<Void, Void, List<objGCSchedule[]>> {
        private final String id;
        private final String summaryfromdate;
        private final String summarytodate;
        private final String summaryfromdate2;
        private final String summarytodate2;

        getSchedule(String aregid, String asummaryfromdate, String asummarytodate, String asummaryfromdate2, String asummarytodate2) {
            this.id = aregid;
            this.summaryfromdate = asummaryfromdate;
            this.summarytodate = asummarytodate;
            this.summaryfromdate2 = asummaryfromdate2;
            this.summarytodate2 = asummarytodate2;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdPoints.show();
        }

        @Override
        protected List<objGCSchedule[]> doInBackground(Void... params) {
            List<objGCSchedule[]> list = new ArrayList<>();
            L2_Get_WorkScheduleResult res = null;
            L2_Get_WorkScheduleResult res2 = null;
            boolean is2mthahead = false;
            String summaryfromdatemid = "";
            String summarytodatemid = "";
            Date summarytodateD;
            Date summaryfromdate2D;

            try {
                if (!objGlobalMethods.IsEmptyString(summaryfromdate) && !objGlobalMethods.IsEmptyString(summarytodate)) {
                    res = WebService.L2_Get_WorkSchedule(id, summaryfromdate, summarytodate);
                    if (res != null && res.Status == 0 && res.Schedules != null && res.Schedules.length > 0) {
                        list.add(res.Schedules);
                    }
                }

                if (!objGlobalMethods.IsEmptyString(summaryfromdate2)) {
                    summarytodateD = objGlobalMethods.JsonDateToDate(summarytodate);
                    summaryfromdate2D = objGlobalMethods.JsonDateToDate(summaryfromdate2);
                    Calendar ptoD = Calendar.getInstance();
                    ptoD.setTime(summarytodateD);
                    Calendar fromD = Calendar.getInstance();
                    fromD.setTime(summaryfromdate2D);
                    is2mthahead = (ptoD.get(Calendar.MONTH) + 2) == (fromD.get(Calendar.MONTH));
                    if (is2mthahead) {
                        Date midFromDate = new Date();
                        Date midTodate = new Date();

                        ptoD.add(Calendar.MONTH, 1);
                        ptoD.set(Calendar.DAY_OF_MONTH, ptoD.getActualMinimum(Calendar.DAY_OF_MONTH));
                        ptoD.set(Calendar.HOUR_OF_DAY, 0);
                        ptoD.set(Calendar.MINUTE, 0);
                        ptoD.set(Calendar.SECOND, 0);
                        ptoD.set(Calendar.MILLISECOND, 0);

                        midFromDate = ptoD.getTime();
                        summaryfromdatemid = "/Date(" + midFromDate.getTime() + ")/";

                        ptoD.set(Calendar.DAY_OF_MONTH, ptoD.getActualMaximum(Calendar.DAY_OF_MONTH));
                        midTodate = ptoD.getTime();
                        summarytodatemid = "/Date(" + midTodate.getTime() + ")/";

                        res = WebService.L2_Get_WorkSchedule(id, summaryfromdatemid, summarytodatemid);
                        if (res != null && res.Status == 0 && res.Schedules != null && res.Schedules.length > 0) {
                            list.add(res.Schedules);
                        }
                    }
                }

                if (!objGlobalMethods.IsEmptyString(summaryfromdate2) && !objGlobalMethods.IsEmptyString(summarytodate2)) {
                    res = WebService.L2_Get_WorkSchedule(id, summaryfromdate2, summarytodate2);
                    if (res != null && res.Status == 0 && res.Schedules != null && res.Schedules.length > 0) {
                        list.add(res.Schedules);
                    }
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at GCScheduleActivity getSchedule doInBackground. Excpetion: " + e.toString() + "\n" + "Stack: " + stack, "", "", "", "");
                list = null;
            } finally {
                return list;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final List<objGCSchedule[]> list) {
            Gson gson = new Gson();
            String JsonStr = "";

            try {
                pdPoints.dismiss();
                if (list == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    String[] prefName = new String[]{"gcsch", "summaryfromdate", "summarytodate", "summaryfromdate2", "summarytodate2"};
                    String[] prefvalue = new String[]{"", summaryfromdate, summarytodate, summaryfromdate2, summarytodate2};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorloadingsch));
                } else {
                    JsonStr = gson.toJson(list);
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    String[] prefName = new String[]{"gcsch", "summaryfromdate", "summarytodate", "summaryfromdate2", "summarytodate2"};
                    String[] prefvalue = new String[]{JsonStr, summaryfromdate, summarytodate, summaryfromdate2, summarytodate2};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    loadList(list, summaryfromdate, summarytodate, summaryfromdate2, summarytodate2);
                }
            } catch (Exception e) {
                pdPoints.dismiss();
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at GCScheduleActivity getSchedule onPostExecute. Excpetion: " + e.toString() + "\n" + "Stack: " + stack, "", "", "", "");
                showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorloadingsch));
            }
        }
    }

    private void showSavedDate(TextView tv1, String savedFromJsonDate, TextView tv2, String savedToJsonDate2, String savedToJsonDate1) {
        try {
            Calendar fromc = Calendar.getInstance();
            fromc.setTime(objGlobalMethods.JsonDateToDate(savedFromJsonDate));
            Calendar toc = Calendar.getInstance();
            toc.setTime(objGlobalMethods.JsonDateToDate(!objGlobalMethods.IsEmptyString(savedToJsonDate2) ? savedToJsonDate2 : savedToJsonDate1));
            showDate(tv1, fromc);
            showDate(tv2, toc);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at GCScheduleActivity showSavedDate. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void showDate(TextView tv, Calendar calendar) {
        try {
            /*Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, d);
            calendar.set(Calendar.MONTH, d -1);
            calendar.set(Calendar.YEAR, y);*/
            tv.setText(aformat.format(calendar.getTime()));
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at GCScheduleActivity showDate. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private View.OnClickListener txtDateOnClick(final Calendar pd, final Calendar cd) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final int id = v.getId();
                    Calendar todayCldr = Calendar.getInstance();
                    Calendar cldr = Calendar.getInstance();
                    cldr = id == R.id.txtDateFrom
                            ? objGlobalMethods.toCalendar(objGlobalMethods.rtnddMMyyyyToDateObj(txtDateFrom.getText().toString()))
                            : objGlobalMethods.toCalendar(objGlobalMethods.rtnddMMyyyyToDateObj(txtDateTo.getText().toString()));
                    final int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);

                    // date picker dialog
                    picker = new DatePickerDialog(myApp.currentActivity,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    if (id == R.id.txtDateFrom) {
                                        Date datefrom = objGlobalMethods.toCalendar(year, monthOfYear, dayOfMonth).getTime();
                                        txtDateFrom.setText(aformat.format(datefrom));

                                        Date dateto = objGlobalMethods.rtnddMMyyyyToDateObj(txtDateTo.getText().toString());
                                        //0 comes when two date are same,
                                        //1 comes when date1 is higher then date2
                                        //-1 comes when date1 is lower then date2
                                        if (dateto.compareTo(datefrom) <= 0) {
                                            txtDateTo.setText(aformat.format(datefrom));
                                        }
                                    } else {
                                        Date dateto = objGlobalMethods.toCalendar(year, monthOfYear, dayOfMonth).getTime();
                                        txtDateTo.setText(aformat.format(dateto));

                                        Date datefrom = objGlobalMethods.rtnddMMyyyyToDateObj(txtDateFrom.getText().toString());
                                        if (dateto.compareTo(datefrom) <= 0) {
                                            txtDateFrom.setText(aformat.format(dateto));
                                        }
                                    }
                                }
                            }, year, month, day);

                    picker.getDatePicker().setMinDate(
                            //objGlobalMethods.toCalendar(objGlobalMethods.rtnddMMyyyyToDateObj(txtDateFrom.getText().toString())).getTimeInMillis()
                            pd.getTimeInMillis()
                    );

                    picker.getDatePicker().setMaxDate(cd.getTimeInMillis());
                    picker.show();

                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at GCScheduleActivity txtDateOnClick. Exception: " + e.getMessage(), "", "", "", "");
                }
            }
        };
    }

    public void btnReset_Click(View v) {
        try {
            int[] prevmonthstart = objGlobalMethods.getPrevMonthDateRangeRangeDMYLongValue();
            //int[] currmonthstart = objGlobalMethods.getCurrMonthDateRangeRangeDMYLongValue();
            //int[] currmonthend = objGlobalMethods.getCurrMonthToDateRangeDMYLongValue();
            int[] currmonthend = objGlobalMethods.getCurrMonthFullDateRangePlus1MthDMYLongValue();
            Calendar pmc = Calendar.getInstance();
            Calendar cmc = Calendar.getInstance();
            pmc.set(Calendar.DAY_OF_MONTH, prevmonthstart[0]);
            pmc.set(Calendar.MONTH, prevmonthstart[1]);
            pmc.set(Calendar.YEAR, prevmonthstart[2]);
            cmc.set(Calendar.DAY_OF_MONTH, currmonthend[0]);
            cmc.set(Calendar.MONTH, currmonthend[1]);
            cmc.set(Calendar.YEAR, currmonthend[2]);

            showDate(txtDateFrom, pmc);
            showDate(txtDateTo, cmc);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at GCScheduleActivity btnReset_Click. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void btnInquire_Click(View v) {
        Context ctx = myApp.getAppContext();
        String RegID = objGlobalMethods.getRegistrationIdOnly();
        Date FromDate, ToDate;
        Calendar FromDateCal, FromDateEndCal, ToDateCal, ToDateStartCal;
        long FromDateLong, ToDateLong;
        String start01 = null;
        String end01 = null;
        String start02 = null;
        String end02 = null;
        String FromTimestampstr = "";
        String ToTimestampstr = "";
        boolean sameMth = false;
        try {
            FromDate = objGlobalMethods.rtnddMMyyyyToDateObj(txtDateFrom.getText().toString());
            ToDate = objGlobalMethods.rtnddMMyyyyToDateObj(txtDateTo.getText().toString());
            FromDateCal = objGlobalMethods.toCalendar(FromDate);
            ToDateCal = objGlobalMethods.toCalendar(ToDate);
            sameMth = FromDateCal.get(Calendar.MONTH) == ToDateCal.get(Calendar.MONTH);
            if (sameMth) {
                start01 = objGlobalMethods.convertCalendarToLocaleDate(FromDateCal);
                end01 = objGlobalMethods.convertCalendarToLocaleDate(ToDateCal);
            } else {
                start01 = objGlobalMethods.convertCalendarToLocaleDate(FromDateCal);
                FromDateEndCal = objGlobalMethods.getCalendarForNow();
                FromDateEndCal.set(Calendar.YEAR, FromDateCal.get(Calendar.YEAR));
                FromDateEndCal.set(Calendar.MONTH, FromDateCal.get(Calendar.MONTH));
                FromDateEndCal.set(Calendar.DAY_OF_MONTH, FromDateCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                end01 = objGlobalMethods.convertCalendarToLocaleDate(FromDateEndCal);

                ToDateStartCal = objGlobalMethods.getCalendarForNow();
                ToDateStartCal.set(Calendar.YEAR, ToDateCal.get(Calendar.YEAR));
                ToDateStartCal.set(Calendar.MONTH, ToDateCal.get(Calendar.MONTH));
                ToDateStartCal.set(Calendar.DAY_OF_MONTH, ToDateCal.getActualMinimum(Calendar.DAY_OF_MONTH));
                start02 = objGlobalMethods.convertCalendarToLocaleDate(ToDateStartCal);
                end02 = objGlobalMethods.convertCalendarToLocaleDate(ToDateCal);
            }

            //RegID = "fC9WkiHpMZM:APA91bHVQoN-pFpL7HlJOrrD4R1OcOxnitjHuqUwllivSf09Y18Zw4fK7TotClrj_Bn19bL7WOvOBF3X5CHIWLNwQkVeNWn8Vn-BPC3bYx9Ij102eyct-pmix4Fa2NiKTXQ9unYyjPQ-"; //redzuan
            getSchedule task = new getSchedule(RegID, start01, end01, start02, end02);
            task.execute();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at GCScheduleActivity txtDateOnClick. Exception: " + e.getMessage(), "", "", "", "");
            showErrorAlert(ctx.getString(R.string.alert_errorloadingsch));
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
                    objGlobalMethods.writeToFile("Bluetooth is on at GCScheduleActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at GCScheduleActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}