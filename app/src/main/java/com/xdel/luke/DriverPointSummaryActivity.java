package com.xdel.luke;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.HRM.ProfileActivity;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.DriverPointSummaryExpandableList_PostItemAdapter;
import com.xdel.luke.objClasses.DriverPointSummary_PostData;
import com.xdel.luke.objClasses.DriverPointSummary_PostItemAdapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objDistance_Info;
import com.xdel.luke.objClasses.objDriverPointSummary;
import com.xdel.luke.objClasses.objGetTravelDistanceResult;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DriverPointSummaryActivity extends AppCompatActivity {

    ConnectionDetector cd;
    Context context;
    //private String regid;
    private ListView listView, listView2;
    LinearLayout DriverPointSummaryDateFromLinearLayout, DriverPointSummaryDateFromLinearLayout2, DriverPointSummaryListviewLinearLayout, DriverPointSummaryListviewLinearLayout2;
    TextView txtDateFrom, txtDateTo;
    ImageView imgExpand, imgExpand2;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    private ProgressDialog pdPoints;

    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    private DriverPointSummaryExpandableList_PostItemAdapter expandableSortedSQLiteListAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<DriverPointSummary_PostData>> listDataChildSortedSQLite2;
    private ArrayList<String> headerAl;
    private ArrayList itemAL;
    private List<DriverPointSummary_PostData> subGroupItemSqliteAL;
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at DriverPointSummaryActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DriverPointSummaryActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DriverPointSummaryActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DriverPointSummaryActivity showChangePermissionDialog. ActivityNotFoundException: "
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
        parentIntent = new Intent(DriverPointSummaryActivity.this, MainActivity.class);
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
        objGlobalMethods.setGCMPrefs(GcmPreferences, "dailypoints", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "summarypoints", "");
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

        setContentView(R.layout.activity_driver_point_summary);

        setTitle(myApp.getAppContext().getString(R.string.pointsummarydatefromto2));
        pdPoints = new ProgressDialog(this);
        pdPoints.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdPoints.setCanceledOnTouchOutside(false);
        pdPoints.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfoPoints));

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
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
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
                objGlobalMethods.writeToFile("DriverPointSummaryActivity !BTEnabled", "", "", "", "");
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
            JsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "summarypoints");
            summaryfromdate1 = objGlobalMethods.getGCMPrefs(GcmPreferences, "summaryfromdate");
            summarytodate1 = objGlobalMethods.getGCMPrefs(GcmPreferences, "summarytodate");
            summaryfromdate2 = objGlobalMethods.getGCMPrefs(GcmPreferences, "summaryfromdate2");
            summarytodate2 = objGlobalMethods.getGCMPrefs(GcmPreferences, "summarytodate2");

            int[] prevmonthstart = objGlobalMethods.getPrevMonthDateRangeRangeDMYLongValue();
            int[] currmonthend = objGlobalMethods.getCurrMonthToDateRangeDMYLongValue();
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
                    List<objDriverPointSummary[]> list = gson.fromJson(JsonStr, new TypeToken<List<objDriverPointSummary[]>>() {
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
                    List<objDriverPointSummary[]> list = gson.fromJson(JsonStr, new TypeToken<List<objDriverPointSummary[]>>() {
                    }.getType());
                    loadList(list, summaryfromdate1, summarytodate1, summaryfromdate2, summarytodate2);
                    showSavedDate(txtDateFrom, summaryfromdate1, txtDateTo, summarytodate2, summarytodate1);
                } else {
                    String[] prevMonthStartEndLongValueStr = objGlobalMethods.getPrevMonthDateRangeLongValueString();
                    String[] currentMonthStartEndLongValueStr = objGlobalMethods.getCurrMonthToDateRangeLongValueString();

                    if (RegID.equals("")) {
                        RegID = objGlobalMethods.getRegistrationId(null, GcmPreferences);
                    }
                    //RegID = "fC9WkiHpMZM:APA91bHVQoN-pFpL7HlJOrrD4R1OcOxnitjHuqUwllivSf09Y18Zw4fK7TotClrj_Bn19bL7WOvOBF3X5CHIWLNwQkVeNWn8Vn-BPC3bYx9Ij102eyct-pmix4Fa2NiKTXQ9unYyjPQ-"; //redzuan
                    getPoints2 task = new getPoints2(RegID, prevMonthStartEndLongValueStr[0], prevMonthStartEndLongValueStr[1], currentMonthStartEndLongValueStr[0], currentMonthStartEndLongValueStr[1]);
                    task.execute();
                }

                //JobsHandler.doubleCheckUpdatePIPSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx, false);
                JobsHandler.doubleCheckUpdatePIPSQLiteNew(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DriverPointSummaryActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at DriverPointSummaryActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driver_point_summary, menu);
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
                String[] prefName = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, "summarypoints", "summaryfromdate",
                        "summarytodate", "summaryfromdate2", "summarytodate2"};
                String[] prefvalue = new String[]{"-1", "", "", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                Intent i;
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(DriverPointSummaryActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);

                String AdministrativeMode = objGlobalMethods.getGCMPrefs(GcmPreferences, "AdministrativeMode");
                if (!objGlobalMethods.IsEmptyString(AdministrativeMode)) {
                    i = new Intent(myApp.currentActivity, ProfileActivity.class);
                }
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    //Start showDialog
    private Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                dialog.dismiss();

            }
        });

        return builder.show();
    }

    @Override
    public void onBackPressed() {
        ///Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        MainActivity.uponLogin = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String[] prefName = new String[]{"summarypoints", "summaryfromdate", "summarytodate", "summaryfromdate2", "summarytodate2"};
        String[] prefvalue = new String[]{"", "", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

        String AdministrativeMode = objGlobalMethods.getGCMPrefs(GcmPreferences, "AdministrativeMode");
        if (!objGlobalMethods.IsEmptyString(AdministrativeMode)) {
            parentIntent = new Intent(myApp.currentActivity, ProfileActivity.class);
        }

        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    private void loadList(List<objDriverPointSummary[]> list, String summaryfromdate1, String summarytodate1, String summaryfromdate2, String summarytodate2) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DecimalFormat dec = new DecimalFormat("#.00");
        objDriverPointSummary[] rs = null;
        objDriverPointSummary[] rs2 = null;
        objDriverPointSummary obj = null;
        DriverPointSummary_PostData postdata;
        String Today_ddMMyyyy = formatter.format(new Date());
        Date start = null;
        Date end = null;
        Date start2 = null;
        Date end2 = null;
        String startstr = "";
        String endstr = "";
        String headerStr1, headerStr2;
        String startstr2 = "";
        String endstr2 = "";
        Integer totalpoints = 0;
        Integer totalpoints2 = 0;
        headerAl = new ArrayList<String>();
        itemAL = new ArrayList();
        listDataHeader = new ArrayList<String>();
        listDataChildSortedSQLite2 = new HashMap<String, List<DriverPointSummary_PostData>>();
        Context ctx = myApp.getAppContext();
        int headerALselectedIDX = 0;
        int updateEarlyCount = 0;
        int updateLateCount = 0;
        double tp = 0.0;
        double p = 0.0;
        double hp = 0.0;
        double td = 0.00;
        try {
            if (list != null && list.size() > 0) {
                for (int i = 0; i <= list.size() - 1; i++) {
                    p = 0;
                    tp = 0;
                    td = 0;
                    totalpoints = 0;
                    rs = list.get(i);
                    if (rs != null && rs.length > 0) {
                        subGroupItemSqliteAL = new ArrayList<DriverPointSummary_PostData>();
                        for (int x = 0; x <= rs.length - 1; x++) {
                            p = 0;
                            hp = 0;
                            obj = rs[x];
                            totalpoints += obj.Points;

                            p = obj.Points != null ? obj.Points.doubleValue() : 0.0;
                            hp = obj.Half_Points != null ? obj.Half_Points.doubleValue() : 0.0;
                            if (hp > 0)
                                hp = hp / 2;
                            p = p - hp;
                            tp += p;

                            td += obj.Total_Distance;
                        }

                        start = i == 0 ? objGlobalMethods.JsonDateToDate(summaryfromdate1) : objGlobalMethods.JsonDateToDate(summaryfromdate2);
                        end = i == 0 ? objGlobalMethods.JsonDateToDate(summarytodate1) : objGlobalMethods.JsonDateToDate(summarytodate2);
                        startstr = start != null ? formatter.format(start) : "";
                        endstr = end != null ? formatter.format(end) : "";

                        //headerStr1 = startstr + " " + ctx.getString(R.string.pointsummarydateto) + ": " + endstr + "\n" + ctx.getString(R.string.dailypoint) + " " + String.valueOf(totalpoints);
                        headerStr1 = startstr + " " + ctx.getString(R.string.pointsummarydateto) + ": " + endstr + "\n" + ctx.getString(R.string.dailypoint) + " " + tp + "\n" + ctx.getString(R.string.performancetxt03) + " " + (td == 0.0 ? "0.00" : dec.format(td)) + " " + ctx.getString(R.string.performancetxt04);
                        headerAl.add(headerStr1);

                        for (int x = 0; x <= rs.length - 1; x++) {
                            updateEarlyCount = 0;
                            updateLateCount = 0;
                            obj = rs[x];
                            updateEarlyCount = obj.Attend_Early + obj.Jobs_Failed_Early;
                            updateLateCount = obj.Attend_Late + obj.Jobs_Failed_Late;
                            Date d = objGlobalMethods.JsonDateToDate(obj.Date_Of_Work);
                            postdata = new DriverPointSummary_PostData(formatter.format(d), obj.Points, obj.Half_Points, obj.Actual_Jobs, i % 2 == 0 ? 0 : 1, obj.Attendance, updateEarlyCount, updateLateCount, obj.Total_Distance);
                            postdata.setToday_ddMMyyyy(Today_ddMMyyyy);
                            subGroupItemSqliteAL.add(postdata);
                            if (x == 0) {
                                itemAL.add(subGroupItemSqliteAL);
                            } else if (x > 0 && itemAL.size() == 1) {
                                itemAL.set(0, subGroupItemSqliteAL);
                            } else if (x > 0 && itemAL.size() > 1) {
                                itemAL.set(1, subGroupItemSqliteAL);
                            }
                        }
                    }
                }

                for (int i = 0; i <= headerAl.size() - 1; i++) {
                    listDataHeader.add(headerAl.get(i));
                    listDataChildSortedSQLite2.put(headerAl.get(i), (List<DriverPointSummary_PostData>) itemAL.get(i));
                }

                if (headerAl.size() > 0) {
                    expandableSortedSQLiteListAdapter = new DriverPointSummaryExpandableList_PostItemAdapter(this, listDataHeader, listDataChildSortedSQLite2);
                    expandableSortedSQLiteListAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

                    // setting list adapter
                    expListView.setAdapter(expandableSortedSQLiteListAdapter);
                    setGroupIndicatorToRight();
                    expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                } else {
                    showErrorAlert(ctx.getString(R.string.nopoint));
                }

            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DriverPointSummaryActivity loadList. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
            showErrorAlert(ctx.getString(R.string.alert_errorloadingpoints));
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

    private void expandSortedSQLiteList(DriverPointSummaryExpandableList_PostItemAdapter adapter) {
        /*for (int i = 0; i <= adapter.getGroupCount() - 1; i++) {
            expListView.expandGroup(i);
        }*/
        expListView.expandGroup(adapter.getGroupCount() - 1);
    }

    private void loademptylist2(String startstr, String endstr, ListView lv, TextView lblDPSDF, TextView lblDPSTP) {
        try {
            lblDPSDF.setText("From: " + startstr + " To:" + endstr);
            ArrayList<DriverPointSummary_PostData> postdataAL = new ArrayList<>();
            DriverPointSummary_PostItemAdapter adapter = new DriverPointSummary_PostItemAdapter(this, postdataAL);
            lv.setAdapter(adapter);
            lblDPSTP.setText("0");
        } catch (Exception e) {

        }
    }

    private class getPoints2 extends AsyncTask<Void, Void, List<objDriverPointSummary[]>> {
        private final String id;
        private final String summaryfromdate;
        private final String summarytodate;
        private final String summaryfromdate2;
        private final String summarytodate2;

        getPoints2(String aregid, String asummaryfromdate, String asummarytodate, String asummaryfromdate2, String asummarytodate2) {
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
        protected List<objDriverPointSummary[]> doInBackground(Void... params) {
            List<objDriverPointSummary[]> list = new ArrayList<>();
            objDriverPointSummary objr = null;
            objDriverPointSummary[] rs = null;
            objDriverPointSummary[] rs2 = null;
            objGetTravelDistanceResult drs = null;
            objGetTravelDistanceResult drs2 = null;
            objDistance_Info[] distrs = null;
            objDistance_Info[] distrs2 = null;
            objDistance_Info objd = null;
            DecimalFormat dec = new DecimalFormat("#.00");

            try {
                if (!objGlobalMethods.IsEmptyString(summaryfromdate) && !objGlobalMethods.IsEmptyString(summarytodate)) {
                    rs = WebService.getobjDriverPointSummary(id, summaryfromdate, summarytodate);
                    drs = WebService.getTravelDistanceResult(id, summaryfromdate, summarytodate);
                    if (drs != null && drs.Status == 0 && drs.Distance != null && drs.Distance.length > 0) {
                        distrs = drs.Distance;
                    }

                    if (distrs != null && distrs.length > 0 && rs != null && rs.length > 0) {
                        outer:
                        for (int r = 0; r <= rs.length -1; r++) {
                            objr = rs[r];
                            for (int d = 0; d <= distrs.length -1; d++) {
                                objd = distrs[d];
                                if (objGlobalMethods.IsEqualString(objd.Date, objr.Date_Of_Work)) {
                                    rs[r].setTotal_Distance(Double.parseDouble(dec.format(objd.Distance)));
                                    continue outer;
                                }
                            }
                        }
                    }

                    list.add(rs);
                }
                if (!objGlobalMethods.IsEmptyString(summaryfromdate2) && !objGlobalMethods.IsEmptyString(summarytodate2)) {
                    rs2 = WebService.getobjDriverPointSummary(id, summaryfromdate2, summarytodate2);
                    drs2 = WebService.getTravelDistanceResult(id, summaryfromdate2, summarytodate2);
                    if (drs2 != null && drs2.Status == 0 && drs2.Distance != null && drs2.Distance.length > 0) {
                        distrs2 = drs2.Distance;
                    }

                    if (distrs2 != null && distrs2.length > 0 && rs2 != null && rs2.length > 0) {
                        outer:
                        for (int r = 0; r <= rs2.length -1; r++) {
                            objr = rs2[r];
                            for (int d = 0; d <= distrs2.length -1; d++) {
                                objd = distrs2[d];
                                if (objGlobalMethods.IsEqualString(objd.Date, objr.Date_Of_Work)) {
                                    rs2[r].setTotal_Distance(Double.parseDouble(dec.format(objd.Distance)));
                                    continue outer;
                                }
                            }
                        }
                    }

                    list.add(rs2);
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at DriverPointSummaryActivity getPoints2 doInBackground. Excpetion: " + e.toString() + "\n" + "Stack: " + stack, "", "", "", "");
                list = null;
            } finally {
                return list;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final List<objDriverPointSummary[]> list) {
            Gson gson = new Gson();
            String JsonStr = "";

            try {
                pdPoints.dismiss();
                if (list == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    String[] prefName = new String[]{"summarypoints", "summaryfromdate", "summarytodate", "summaryfromdate2", "summarytodate2"};
                    String[] prefvalue = new String[]{"", summaryfromdate, summarytodate, summaryfromdate2, summarytodate2};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorloadingpoints));
                } else {
                    JsonStr = gson.toJson(list);
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    String[] prefName = new String[]{"summarypoints", "summaryfromdate", "summarytodate", "summaryfromdate2", "summarytodate2"};
                    String[] prefvalue = new String[]{JsonStr, summaryfromdate, summarytodate, summaryfromdate2, summarytodate2};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    loadList(list, summaryfromdate, summarytodate, summaryfromdate2, summarytodate2);
                }
            } catch (Exception e) {
                pdPoints.dismiss();
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at DriverPointSummaryActivity getPoints2 onPostExecute. Excpetion: " + e.toString() + "\n" + "Stack: " + stack, "", "", "", "");
                showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorloadingpoints));
            }
        }
    }

    private void hideshowlistview() {
        String visiblelv = "";
        Context ctx = myApp.getAppContext();
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            visiblelv = objGlobalMethods.getGCMPrefs(GcmPreferences, "visiblelv");
            DriverPointSummaryListviewLinearLayout.setVisibility(visiblelv.isEmpty() || visiblelv.equals("") ? View.VISIBLE : View.GONE);
            DriverPointSummaryListviewLinearLayout2.setVisibility(visiblelv.isEmpty() || visiblelv.equals("") ? View.GONE : View.VISIBLE);
            imgExpand.setImageResource(visiblelv.isEmpty() || visiblelv.equals("") ? R.mipmap.ic_action_expand_less : R.mipmap.ic_action_expand_more);
            imgExpand2.setImageResource(visiblelv.isEmpty() || visiblelv.equals("") ? R.mipmap.ic_action_expand_more : R.mipmap.ic_action_expand_less);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DriverPointSummaryActivity hideshowlistview. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void hideshowlistviewClick(View v) {
        String visiblelv = "";

        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            visiblelv = objGlobalMethods.getGCMPrefs(GcmPreferences, "visiblelv");
            if (visiblelv.isEmpty() || visiblelv.equals("")) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "visiblelv", "lv2");
                DriverPointSummaryListviewLinearLayout.setVisibility(View.GONE);
                DriverPointSummaryListviewLinearLayout2.setVisibility(View.VISIBLE);
                imgExpand.setImageResource(R.mipmap.ic_action_expand_more);
                imgExpand2.setImageResource(R.mipmap.ic_action_expand_less);
            } else {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "visiblelv", "");
                DriverPointSummaryListviewLinearLayout.setVisibility(View.VISIBLE);
                DriverPointSummaryListviewLinearLayout2.setVisibility(View.GONE);
                imgExpand.setImageResource(R.mipmap.ic_action_expand_less);
                imgExpand2.setImageResource(R.mipmap.ic_action_expand_more);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DriverPointSummaryActivity hideshowlistviewClick. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at DriverPointSummaryActivity showSavedDate. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at DriverPointSummaryActivity showDate. Exception: " + e.getMessage(), "", "", "", "");
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
                    picker = new DatePickerDialog(DriverPointSummaryActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    if (id == R.id.txtDateFrom) {
                                        Date datefrom = objGlobalMethods.toCalendar(year, monthOfYear, dayOfMonth).getTime();
                                        txtDateFrom.setText(aformat.format(datefrom));
                                        //txtDateFrom.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

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
                    objGlobalMethods.writeToFile("Exception at DriverPointSummaryActivity txtDateOnClick. Exception: " + e.getMessage(), "", "", "", "");
                }
            }
        };
    }

    public void btnReset_Click(View v) {
        try {
            int[] prevmonthstart = objGlobalMethods.getPrevMonthDateRangeRangeDMYLongValue();
            int[] currmonthend = objGlobalMethods.getCurrMonthToDateRangeDMYLongValue();
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
            objGlobalMethods.writeToFile("Exception at DriverPointSummaryActivity btnReset_Click. Exception: " + e.getMessage(), "", "", "", "");
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
            getPoints2 task = new getPoints2(RegID, start01, end01, start02, end02);
            task.execute();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DriverPointSummaryActivity txtDateOnClick. Exception: " + e.getMessage(), "", "", "", "");
            showErrorAlert(ctx.getString(R.string.alert_errorloadingpoints));
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
                    objGlobalMethods.writeToFile("Bluetooth is on at DriverPointSummaryActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DriverPointSummaryActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
