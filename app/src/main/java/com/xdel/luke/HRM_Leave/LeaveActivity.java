package com.xdel.luke.HRM_Leave;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.BottomSheet.customFullScreenDialog;
import com.xdel.luke.Camera5Activity;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.HRM.ProfileActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.temploggerclass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LeaveActivity extends AppCompatActivity {

    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String langType = "en";
    String[] UsernamePwd = new String[4];
    String username;
    String password;
    String useridx;
    String ishomesuser;
    ConnectionDetector cd;
    //Spinner ddlLeaveType;
    Bitmap bitmap = null;
    Button btnApply;
    private ProgressDialog pd, pdCheck;
    private objLeaveType[] objLeaveTypelist = null;
    ExpandableListView expListView;
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;
    private customFullScreenDialog cfs;
    public static Boolean InsideLeaveModule;
    private boolean hasAnnualLeave;
    private Menu menu;

    private Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
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

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        //parentIntent = new Intent(LeaveActivity.this, MainActivity.class);
        parentIntent = new Intent(myApp.getAppContext(), MainActivity.class);
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

        setContentView(R.layout.activity_leave);

        btnApply = (Button) findViewById(R.id.btnApply);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));

        pdCheck = new ProgressDialog(this);
        pdCheck.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdCheck.setCanceledOnTouchOutside(false);
        pdCheck.setMessage(myApp.getAppContext().getString(R.string.pd_waitCheck));

        //ddlLeaveType = (Spinner) findViewById(R.id.ddlLeaveType);
        expListView = (ExpandableListView) findViewById(R.id.expListView);

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onResume() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;
                Context ctx = myApp.getAppContext();
                setTitle(ctx.getString(R.string.title_activity_apply_leave));

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("LeaveActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                JobsHandler.checkForceLogOut();

                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];

                if (username.equals("") && password.equals("")) {
                    BackToLoginPage2();
                }

                lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

                //cd = new ConnectionDetector(ctx);
                cd = new ConnectionDetector(null);
                // Check if Internet present
                if (!cd.isConnectingToInternet()) {
                    //// Internet Connection is not present
                    showErrorAlert(myApp.getAppContext().getString(R.string.toast_workingconnection));
                } else {
                    initPage();
                }

                JobsHandler.clearLeaveApplicationActivityPrefs(GcmPreferences);
                objGlobalMethods.resetAllImageRelatedStaticVariables(false);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LeaveActivity onResume. Exception: " + e.getMessage(), "", "", "", "");
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

            if (cfs != null) {
                cfs = null;
            }

            if (pd != null) {
                pd.dismiss();
                pd = null;
            }

            if (pdCheck != null) {
                pdCheck.dismiss();
                pdCheck = null;
            }

            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
            bitmap = null;

            cd = null;
            objLeaveTypelist = null;
            expListView = null;
            GcmPreferences = null;
            UpdateJobInfoPreferences = null;
            langType = null;
            UsernamePwd = null;
            username = null;
            password = null;
            useridx = null;
            ishomesuser = null;
            btnApply = null;

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LeaveActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
    }

    @Override
    protected void onPause() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LeaveActivity onPause unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
            }
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    private void showErrorAlert(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_noLeaveTypedata));
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                Camera5Activity.mCurrentPhotoPath = null;
                if (bitmap != null) {
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    bitmap = null;
                }
                onBackPressed();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            /*Context ctx = myApp.getAppContext();
            Intent parentIntent = new Intent(ctx, ProfileActivity.class);
            MainActivity.uponLogin = 0;
            GcmPreferences = objGlobalMethods.getGcmPreferences();

            String AdministrativeMode = objGlobalMethods.getGCMPrefs(GcmPreferences, "AdministrativeMode");
            if (!objGlobalMethods.IsEmptyString(AdministrativeMode)) {
                //objGlobalMethods.setGCMPrefs(GcmPreferences, "AdministrativeMode", "");
                logout();
            } else {
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            }*/

            Context ctx = myApp.getAppContext();
            Intent parentIntent = new Intent(ctx, ProfileActivity.class);
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Excpetion at LeaveActivity openLeaveApplicationBS. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            BackToLoginPage2();
        }

    }

    private void initPage() {
        //boolean LeaveTypeDDLloaded = false;
        boolean LeaveTypeInited = false;
        try {
            loadLeaveApplications t = new loadLeaveApplications(0);
            t.execute();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LeaveActivity initPage. Exception: " + e.getMessage(), "", "", "", "");
            onBackPressed();
        }
    }

    private boolean initLeaveType() {
        boolean asd = false;
        objLeaveType type = null;
        ArrayList<objLeaveType> SpinnerPostDataArr = new ArrayList<>();
        Gson gson = new Gson();
        try {
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            String LeaveTypesJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LeaveTypesJsonStr");
            if (!LeaveTypesJsonStr.equals("") && !LeaveTypesJsonStr.equals("[]")) {
                objLeaveTypelist = gson.fromJson(LeaveTypesJsonStr, objLeaveType[].class);
                asd = (objLeaveTypelist != null && objLeaveTypelist.length > 0);
            }
        } catch (Exception e) {
            asd = false;
            objGlobalMethods.writeToFile("Exception at LeaveActivity initLeaveType. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public void openLeaveApplicationBS(View v) {
        try {
            /*LeaveApplication_BottomSheet_Dialog bs = new LeaveApplication_BottomSheet_Dialog();
            bs.show(getSupportFragmentManager(), "LeaveApplication_BottomSheet_Dialog");*/

            //Intent intent = new Intent(LeaveActivity.this, LeaveApplicationActivity.class);
            Intent intent = new Intent(myApp.getAppContext(), LeaveApplicationActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Excpetion at LeaveActivity openLeaveApplicationBS. Exception: " + s, "", "", "", "");
        }
    }

    private void reloadMenu() {
        try {
            if (this.menu != null) {
                this.menu.findItem(R.id.info).setVisible(hasAnnualLeave ? true : false);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LeaveActivity reloadMenu. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_leave, menu);
        menu.findItem(R.id.info).setVisible(hasAnnualLeave ? true : false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            refresh();
        } else if (item.getItemId() == R.id.info) {
            showCFs();
        }

        /*switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                break;
            case R.id.info:
                showCFs();
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void refresh(){
        loadLeaveApplications t = new loadLeaveApplications(0);
        t.execute();
    }

    private class loadLeaveApplications extends AsyncTask<Void, Void, objLeaveRecord[]> {
        final long LeaveTypeIDX;

        loadLeaveApplications(long aLeaveTypeIDX) {
            this.LeaveTypeIDX = aLeaveTypeIDX;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected objLeaveRecord[] doInBackground(Void... params) {
            objLeaveRecord[] arr = null;
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mma");
            Date LeaveFromDate, LeaveToDate;
            Gson gson = new Gson();
            try {
                if (GcmPreferences == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                }
                String regid = objGlobalMethods.getRegistrationId(null, GcmPreferences);
                //regid = "fC9WkiHpMZM:APA91bHVQoN-pFpL7HlJOrrD4R1OcOxnitjHuqUwllivSf09Y18Zw4fK7TotClrj_Bn19bL7WOvOBF3X5CHIWLNwQkVeNWn8Vn-BPC3bYx9Ij102eyct-pmix4Fa2NiKTXQ9unYyjPQ-"; //redzuan
                objLeaveTypelist = WebService.GetLeaveTypes(regid);
                if (objLeaveTypelist != null && objLeaveTypelist.length > 0) {
                    JobsHandler.saveLeaveTypes(objLeaveTypelist);
                    hasAnnualLeave = hasAnnualLeave();
                    arr = WebService.GetLeaveApplications(regid, LeaveTypeIDX);
                    if (arr != null && arr.length > 0) {
                        for (int i = 0; i <= arr.length - 1; i++) {
                            LeaveFromDate = objGlobalMethods.JsonDateToDate(arr[i].LeaveFrom);
                            LeaveToDate = objGlobalMethods.JsonDateToDate(arr[i].LeaveTo);
                            arr[i].setLeaveFromDate(LeaveFromDate.getTime());
                            arr[i].setLeaveToDate(LeaveToDate.getTime());
                            arr[i].setLeaveFromDateFormatStr(formatter.format(LeaveFromDate));
                            arr[i].setLeaveToDateFormatStr(formatter.format(LeaveToDate));
                        }
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LeaveActivity loadLeaveApplications doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return arr;
            }
        }

        @Override
        protected void onPostExecute(final objLeaveRecord[] res) {
            boolean listloaded = false;
            try {
                pd.dismiss();
                reloadMenu();
                if (res != null) {
                    loadlist(res);
                    listloaded = true;
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LeaveActivity loadLeaveApplications onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                if (pd != null)
                    pd.dismiss();
                /*if (!listloaded) {
                    showErrorAlert("");
                }*/
                if ((InsideLeaveModule == null || !InsideLeaveModule) && hasAnnualLeave) {
                    InsideLeaveModule = true;
                    showCFs();
                }
            }
        }
    }

    private boolean hasAnnualLeave() {
        boolean asd = false;
        objLeaveType[] objLeaveTypelist = null;
        String desc = "";
        try {
            String LeaveTypesJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LeaveTypesJsonStr");
            if (!LeaveTypesJsonStr.equals("") && !LeaveTypesJsonStr.equals("[]")) {
                Gson gson = new Gson();
                objLeaveTypelist = gson.fromJson(LeaveTypesJsonStr, objLeaveType[].class);
                if (objLeaveTypelist != null && objLeaveTypelist.length > 0) {
                    for (int i = 0; i <= objLeaveTypelist.length - 1; i++) {
                        desc = objLeaveTypelist[i].Description;
                        if (objGlobalMethods.IsEqualString(desc.toUpperCase(), "ANNUAL LEAVE")) {
                            asd = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LeaveActivity hasAnnualLeave. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    private void showCFs() {
        try {
            ArrayList<String> toNoteList = new ArrayList<>();
            Context ctx = myApp.getAppContext();
            toNoteList.add(ctx.getString((objGlobalMethods.currTimeIsBeforeGivenDate(2023, 6, 1)) ? R.string.leave_unplanned_june : R.string.leave_unplanned_immed));
            toNoteList.add(ctx.getString(R.string.leave_less_3days_1));
            toNoteList.add(ctx.getString(R.string.leave_less_3days_2));
            toNoteList.add(ctx.getString(R.string.leave_childcare_annual));
            toNoteList.add(ctx.getString(R.string.leave_unplanned_annual));

            cfs = new customFullScreenDialog();
            cfs.setNotice(toNoteList);
            if (cfs != null && (cfs.getNotice() != null)) {
                cfs.show(getSupportFragmentManager(), "leave_info");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LeaveActivity showCFs. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private String getLeaveTypeDesc(long LeaveTypeIDX) {
        String desc = "--";
        try {
            if (objLeaveTypelist != null && objLeaveTypelist.length > 0) {
                for (int i = 0; i <= objLeaveTypelist.length - 1; i++) {
                    if (LeaveTypeIDX == objLeaveTypelist[i].IDX) {
                        if (langType.equals("zh")) {
                            desc = objLeaveTypelist[i].Description_C == null || objLeaveTypelist[i].Description_C.equals("") ? objLeaveTypelist[i].Description : objLeaveTypelist[i].Description_C;
                        } else {
                            desc = objLeaveTypelist[i].Description;
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            desc = "--";
        } finally {
            return desc;
        }
    }

    private void loadlist(objLeaveRecord[] res) {
        String headerStr1 = "";
        ArrayList<String> headerAl = new ArrayList<String>();
        ArrayList itemAL = new ArrayList();
        ArrayList<Double> itemDaysAL = new ArrayList<>();
        ArrayList<Double> itemHoursAL = new ArrayList<>();
        List<objLeaveRecord> subGroupItemSqliteAL;
        List<String> listDataHeader = new ArrayList<String>();
        HashMap<String, List<objLeaveRecord>> listDataChildSortedSQLite2 = new HashMap<String, List<objLeaveRecord>>();
        objLeaveRecord obj = null;
        int headerALselectedIDX = 0;
        double days = 0;
        double hours = 0;
        Context ctx = myApp.getAppContext();

        try {
            //sort list base on leavetypeidx first
            Arrays.sort(res, objLeaveRecord.ComparatorLeaveTypeIDX);
            //sort list base on leavefrom
            Arrays.sort(res, objLeaveRecord.ComparatorLeaveFromDate);

            for (int i = 0; i <= res.length - 1; i++) {
                obj = res[i];
                headerStr1 = getLeaveTypeDesc(obj.LeaveTypeIDX);
                if (!headerAl.contains(headerStr1)) {
                    headerAl.add(headerStr1);
                    subGroupItemSqliteAL = new ArrayList<objLeaveRecord>();
                    subGroupItemSqliteAL.add(obj);
                    itemAL.add(subGroupItemSqliteAL);
                    if (obj.getUOM() == objUOM.UOM.umDays.ordinal()) {
                        itemDaysAL.add(obj.Units);
                        itemHoursAL.add(0.0);
                    } else {
                        itemDaysAL.add(0.0);
                        itemHoursAL.add(obj.Units);
                    }
                } else {
                    headerALselectedIDX = headerAl.indexOf(headerStr1);
                    subGroupItemSqliteAL = (List<objLeaveRecord>) itemAL.get(headerALselectedIDX);
                    subGroupItemSqliteAL.add(obj);
                    itemAL.set(headerALselectedIDX, subGroupItemSqliteAL);
                    if (obj.getUOM() == objUOM.UOM.umDays.ordinal()) {
                        days = itemDaysAL.get(headerALselectedIDX);
                        days += obj.Units;
                        itemDaysAL.set(headerALselectedIDX, days);
                    } else {
                        hours = itemHoursAL.get(headerALselectedIDX);
                        hours += obj.Units;
                        itemHoursAL.set(headerALselectedIDX, hours);
                    }
                }
            }

            for (int i = 0; i <= headerAl.size() - 1; i++) {
                headerStr1 = headerAl.get(i);
                try {
                    days = itemDaysAL.get(i);
                } catch (Exception e) {
                    days = 0.0;
                }

                try {
                    hours = itemHoursAL.get(i);
                } catch (Exception e) {
                    hours = 0.0;
                }

                if (days > 0.0) {
                    headerStr1 += "\n" + String.valueOf(days) + " " + ctx.getString(R.string.days);
                }

                if (hours > 0.0 && days > 0.0) {
                    headerStr1 += ", " + String.valueOf(hours) + " " + ctx.getString(R.string.hours);
                } else if (hours > 0.0 && days <= 0.0) {
                    headerStr1 += "\n" + String.valueOf(hours) + " " + ctx.getString(R.string.hours);
                }

                headerAl.set(i, headerStr1);
            }

            for (int i = 0; i <= headerAl.size() - 1; i++) {
                listDataHeader.add(headerAl.get(i));
                listDataChildSortedSQLite2.put(headerAl.get(i), (List<objLeaveRecord>) itemAL.get(i));
            }

            LeaveRecordSummaryyExpandableList_PostItemAdapter adapter = new LeaveRecordSummaryyExpandableList_PostItemAdapter(this, listDataHeader, listDataChildSortedSQLite2);
            adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
            expListView.setAdapter(adapter);
            setGroupIndicatorToRight();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LeaveActivity loadlist. Exception: " + e.getMessage(), "", "", "", "");
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

    private void expandSortedSQLiteList(LeaveRecordSummaryyExpandableList_PostItemAdapter adapter) {
        for (int i = 0; i <= adapter.getGroupCount() - 1; i++) {
            expListView.expandGroup(i);
        }
    }

    public void nullpdCheck() {
        try {
            if (pdCheck != null) {
                pdCheck.dismiss();
                pdCheck = null;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LeaveActivity nullpdCheck. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private void logout() {
        try {
            Context ctx = myApp.getAppContext();
            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                showDialog(myApp.getAppContext().getString(R.string.alert_cannotLogoutNoInternet));
                // stop executing code by return
                return;
            }

            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            ishomesuser = UsernamePwd[3];

            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setCancelable(false);
            String logoutmsg = !Boolean.parseBoolean(ishomesuser) ? ctx.getString(R.string.logoutrestartphone) + "\n\n" + ctx.getString(R.string.alert_cfmlogout) : ctx.getString(R.string.alert_cfmlogout);
            builder.setMessage(logoutmsg);

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // if user pressed "yes",
                    // 1. check that there is no more ques in the backend, if not, have to wait for all the ques to be cleared b4,
                    // 2. then send back all the plotted locations,
                    // 3. then he is allowed to exit from application
                    pdCheck.show();
                    dialog.dismiss();
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                    username = UsernamePwd[0];
                    password = UsernamePwd[1];
                    useridx = UsernamePwd[2];
                    ishomesuser = UsernamePwd[3];

                    Context ctx = myApp.getAppContext();
                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                    int queCount = JobsHandler.returnNumberOfQueToSend(UpdateJobInfoPreferences);

                    if (queCount > 0) {
                        //unable to logout due there are still ques
                        pdCheck.dismiss();
                        objGlobalMethods.writeToFile("Unable to log out. There are still data waiting to be sent out. Please try again later.", "", "", "", "");
                        showDialog(myApp.getAppContext().getString(R.string.alert_cannotlogoutsendingdata));
                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                        return;
                    } else if (queCount == 0) {
                        //no que, send back all plotted locations
                        //then logout
                        if (Boolean.parseBoolean(ishomesuser)) {
                            objGlobalMethods.writeToFile("Logging out HOMES USER.", "", "", "", "");
                            //LogOutHomesWSCall();
                            JobsHandler.checkForAttachmentToEmailOut task = new JobsHandler.checkForAttachmentToEmailOut(username, password);
                            task.execute();
                        } else {
                            objGlobalMethods.writeToFile("Logging out.", "", "", "", "");
                            JobsHandler.checkForAttachmentToEmailOut task = new JobsHandler.checkForAttachmentToEmailOut(username, password);
                            task.execute();
                        }
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


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LeaveActivity logout. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at LeaveActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LeaveActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
