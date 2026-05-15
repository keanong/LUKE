package com.xdel.luke;

import android.app.AlertDialog;
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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.DriverPointDailyGroup_PostData;
import com.xdel.luke.objClasses.DriverPointDailyGroup_PostItemAdapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objDriverPointItem;
import com.xdel.luke.objClasses.objDriverPointResult;
import com.xdel.luke.objClasses.objDriverPoints;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class DriverPointMain_Activity extends AppCompatActivity {

    ConnectionDetector cd;
    Context context;
    //private String regid;
    private ExpandableListView expListView;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    private ProgressDialog pdPoints;

    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];

    /*exp list view items*/
    private List<String> listDataHeader;
    private HashMap<String, List<DriverPointDailyGroup_PostData>> listDataChildSorted2;
    private ArrayList<String> headerAl;
    private ArrayList itemAL;
    private List<DriverPointDailyGroup_PostData> subGroupItemAL;
    /*exp list view items*/

    private TextView lbldriverpointdaily;

    String langType = "en";

    ScreenShotContentObserver screenShotContentObserver;

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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at DriverPointMainActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DriverPointMainActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DriverPointMainActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at DriverPointMainActivity showChangePermissionDialog. ActivityNotFoundException: "
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
        parentIntent = new Intent(DriverPointMain_Activity.this, MainActivity.class);
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
        objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        Intent parentIntent;
        parentIntent = new Intent(DriverPointMain_Activity.this, MainActivity.class);
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

        setContentView(R.layout.activity_driver_point_main_);

        setTitle(myApp.getAppContext().getString(R.string.title_activity_driver_point_main_));

        expListView = (ExpandableListView) findViewById(R.id.expListView);
        lbldriverpointdaily = (TextView) findViewById(R.id.lbldriverpointdaily);

        pdPoints = new ProgressDialog(this);
        pdPoints.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdPoints.setCanceledOnTouchOutside(false);
        pdPoints.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfoPoints));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

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
            //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
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
                objGlobalMethods.writeToFile("DriverPointMain_Activity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

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

            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                //showErrorAlert(myApp.getAppContext().getString(R.string.toast_workingconnection));

                String JsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "dailypoints");
                if (!JsonStr.equals("")) {
                    loadList(JsonStr);
                } else {
                    loademptylist();
                }
                // stop executing code by return
            } else {
                Long l = null;
                String currentpointdatelong = objGlobalMethods.getGCMPrefs(GcmPreferences, "currentpointdatelong");
                String JsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "dailypoints");

                if (!currentpointdatelong.equals("")) {
                    l = Long.parseLong(currentpointdatelong);
                    if (l != null) {
                        getPoints task = new getPoints(l);
                        task.execute();
                    } else {
                        getYestpoint();
                    }
                } else {
                    getYestpoint();
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
            objGlobalMethods.writeToFile("Exception at DriverPointMain_Activity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at DriverPointMain_Activity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driver_point_main_, menu);
        menu.findItem(R.id.refresh).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent parentIntent;
        //cd = new ConnectionDetector(context);
        cd = new ConnectionDetector(null);

        if (item.getItemId() == R.id.refresh) {
            if (cd.isConnectingToInternet()) {
                getYestpoint();
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_cannotRefreshNoInternet));
            }
        }

        /*switch (item.getItemId()) {
            case R.id.refresh:
                if (cd.isConnectingToInternet()) {
                    getYestpoint();
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_cannotRefreshNoInternet));
                }
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void showErrorAlert(String msg) {
        MainActivity.uponLogin = 0;
        //Dashboard2Activity.DashboardOpenFromPush = false;
        DrawerMainActivity.DashboardOpenFromPush = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                Intent i;
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(DriverPointMain_Activity.this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
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
        super.onBackPressed();
        //Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        Intent parentIntent = new Intent(this, DriverPointSummaryActivity.class);
        MainActivity.uponLogin = 0;
        //Dashboard2Activity.DashboardOpenFromPush = false;
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    public void getYestpoint() {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "currentpointdatelong", "");
            //get today's points
            Date dt = new Date();

            Calendar cal = GregorianCalendar.getInstance();
            cal.add(Calendar.DATE, -1);
            dt = cal.getTime();

            Long l = dt.getTime();
            if (l != null) {
                getPoints task = new getPoints(l);
                task.execute();
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_errorloadingpoints));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DriverPointMain_Activity getYestpoint. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorloadingpoints));
        }
    }

    public void getSummary(View view) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "currentpointdatelong", "");
        Intent intent = new Intent(DriverPointMain_Activity.this, DriverPointSummaryActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void loadList(String jsonStr) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        headerAl = new ArrayList<String>();
        itemAL = new ArrayList();
        listDataHeader = new ArrayList<String>();
        listDataChildSorted2 = new HashMap<String, List<DriverPointDailyGroup_PostData>>();
        int headerALselectedIDX = 0;
        objDriverPointResult rs = null;
        Gson gson = new Gson();
        objDriverPoints[] dpARR = null;
        objDriverPoints dp = null;
        objDriverPointItem DriverPointItem = null;
        objDriverPointItem[] DriverPointItemitems = null;
        List<objDriverPointItem> DriverPointItemsAL = null;
        String pointName = "";
        String pointNameTitle = myApp.getAppContext().getString(R.string.point);
        DriverPointDailyGroup_PostData postdata;
        String point = myApp.getAppContext().getString(R.string.dailypoint);
        double p = 0.0;
        double tp = 0.0;

        try {
            String currentpointdatelong = objGlobalMethods.getGCMPrefs(GcmPreferences, "currentpointdatelong");
            String datestr = myApp.getAppContext().getString(R.string.yestpoint) + " ";
            if (!currentpointdatelong.equals("")) {
                Date currentpointdate = new Date(Long.parseLong(currentpointdatelong));
                datestr = formatter.format(currentpointdate) + " ";
            }
            rs = gson.fromJson(jsonStr, objDriverPointResult.class);
            dpARR = rs.Points;
            //dpAL = new ArrayList<objDriverPoints>(Arrays.asList(dpARR));

            if (dpARR != null && dpARR.length > 0) {
                for (int i = 0; i <= dpARR.length - 1; i++) {

                    dp = dpARR[i];

                    subGroupItemAL = new ArrayList<DriverPointDailyGroup_PostData>();
                    DriverPointItemitems = dp.Items;
                    DriverPointItemsAL = new ArrayList<objDriverPointItem>(Arrays.asList(DriverPointItemitems));
                    Collections.sort(DriverPointItemsAL);

                    for (int x = 0; x <= DriverPointItemsAL.size() - 1; x++) {
                        DriverPointItem = DriverPointItemsAL.get(x);
                        postdata = new DriverPointDailyGroup_PostData(DriverPointItem.IDX, DriverPointItem.JobsIDX, DriverPointItem.JobNo, DriverPointItem.DeliveryContent,
                                DriverPointItem.StatusCodeIDX, DriverPointItem.StatusCode, DriverPointItem.TimeStamp, DriverPointItem.UpdateMode, DriverPointItem.UpdateLat,
                                DriverPointItem.UpdateAccuracy, DriverPointItem.UpdateAccuracy, DriverPointItem.Flag, x % 2 == 0 ? 0 : 1, dp.Unit);
                        if (postdata != null) {
                            subGroupItemAL.add(postdata);
                        }
                    }
                    itemAL.add(subGroupItemAL);
                    //pointName = pointNameTitle + " " + String.valueOf(i + 1) + " - S(" + dpARR[i].PostalCode + ") - " + subGroupItemAL.size() + " " + myApp.getAppContext().getString(R.string.noofjobs);
                    p = (((dp.Flag & 1) == 1) || ((dp.Flag & 6) == 4) || ((dp.Flag & 24) == 16)) ? 0.5 : 1.0;
                    tp += p;
                    pointName = String.valueOf(p) + " " + pointNameTitle + " - S(" + dpARR[i].PostalCode + ") - " + subGroupItemAL.size() + " " + myApp.getAppContext().getString(R.string.noofjobs);
                    headerAl.add(pointName);
                }

                for (int i = 0; i <= headerAl.size() - 1; i++) {
                    listDataHeader.add(headerAl.get(i));
                    listDataChildSorted2.put(headerAl.get(i), (List<DriverPointDailyGroup_PostData>) itemAL.get(i));
                }

                DriverPointDailyGroup_PostItemAdapter expandableSortedListAdapter = new DriverPointDailyGroup_PostItemAdapter(this, listDataHeader, listDataChildSorted2);

                expandableSortedListAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

                // setting list adapter
                expListView.setAdapter(expandableSortedListAdapter);
                setGroupIndicatorToRight();
                //expandSortedList(expandableSortedListAdapter);

                //lbldriverpointdaily.setText(datestr + point + " " + String.valueOf(dpARR.length));
                lbldriverpointdaily.setText(datestr + point + " " + String.valueOf(tp));
            } else {
                loademptylist();
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DriverPointMain_Activity loadList. Exception: " + e.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
            lbldriverpointdaily.setText(point + " 0");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorloadingpoints));
        }
    }

    private void loademptylist() {
        try {
            listDataHeader = new ArrayList<String>();
            listDataChildSorted2 = new HashMap<String, List<DriverPointDailyGroup_PostData>>();
            final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String point = myApp.getAppContext().getString(R.string.dailypoint);
            String currentpointdatelong = objGlobalMethods.getGCMPrefs(GcmPreferences, "currentpointdatelong");
            String datestr = myApp.getAppContext().getString(R.string.yestpoint) + " ";
            if (!currentpointdatelong.equals("")) {
                Date currentpointdate = new Date(Long.parseLong(currentpointdatelong));
                datestr = formatter.format(currentpointdate) + " ";
            }

            DriverPointDailyGroup_PostItemAdapter expandableSortedListAdapter = new DriverPointDailyGroup_PostItemAdapter(this, listDataHeader, listDataChildSorted2);
            expandableSortedListAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
            // setting list adapter
            expListView.setAdapter(expandableSortedListAdapter);
            setGroupIndicatorToRight();

            lbldriverpointdaily.setText(datestr + point + " 0");
        } catch (Exception e) {

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

    private void expandSortedList(DriverPointDailyGroup_PostItemAdapter adapter) {
        for (int i = 0; i <= adapter.getGroupCount() - 1; i++) {
            expListView.expandGroup(i);
        }
    }

    //Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private class getPoints extends AsyncTask<Void, Void, objDriverPointResult> {
        private final long datelong;

        getPoints(long adatelong) {
            this.datelong = adatelong;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdPoints.show();
        }

        @Override
        protected objDriverPointResult doInBackground(Void... params) {
            objDriverPointResult rs = null;

            try {

                String timestampstr = "/Date(" + datelong + ")/";
                String RegID = objGlobalMethods.getRegistrationIdOnly();
                //RegID = "fC9WkiHpMZM:APA91bHVQoN-pFpL7HlJOrrD4R1OcOxnitjHuqUwllivSf09Y18Zw4fK7TotClrj_Bn19bL7WOvOBF3X5CHIWLNwQkVeNWn8Vn-BPC3bYx9Ij102eyct-pmix4Fa2NiKTXQ9unYyjPQ-"; //redzuan
                rs = WebService.getDriverPointResult(RegID, timestampstr);

            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at DrawerMainActivity getPoints doInBackground. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                rs = null;
            } finally {
                return rs;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final objDriverPointResult rs) {
            Gson gson = new Gson();
            String JsonStr = "";
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            try {
                if (rs == null) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "dailypoints", "");
                } else {
                    JsonStr = gson.toJson(rs);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "dailypoints", JsonStr);
                }
                pdPoints.dismiss();
                loadList(JsonStr);
            } catch (Exception e) {
                pdPoints.dismiss();
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at DriverPointMain_Activity getPoints onPostExecute. Excpetion: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                showDialog(myApp.getAppContext().getString(R.string.alert_errorloadingpoints));
            }
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
                    objGlobalMethods.writeToFile("Bluetooth is on at DriverPointMain_Activity", "", "", "", "");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DriverPointMain_Activity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

}
