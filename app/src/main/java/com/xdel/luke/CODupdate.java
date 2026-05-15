package com.xdel.luke;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
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
import com.xdel.luke.objClasses.UpdateDOCOD;
import com.xdel.luke.objClasses.codupdate_PostData;
import com.xdel.luke.objClasses.codupdate_PostItemAdapter;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objDOCOD;
import com.xdel.luke.objClasses.objDOObject;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objUpdateDOCOD;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjTradeInList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CODupdate extends AppCompatActivity {

    public static ListView listView;
    public static TextView lblTotalVal, lblBalanceVal;
    public static codupdate_PostItemAdapter adapter;
    public static ArrayList<codupdate_PostData> al;
    private static String regid;
    public static Activity activity;
    public static Button btnDOCODupdateNext;
    public static Double total = 0.0;
    public static Double balance = 0.0;
    public static String fromPage = "";
    public static Intent nextActivtyIntent;

    Context context;
    ConnectionDetector cd;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    private objJobInfo ji;
    private ProgressDialog pd;
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
        /*myApp.currentActivity = this;
        String languageToLoad = langType; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/

        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_codupdate);

        listView = (ListView) findViewById(R.id.listView);
        lblTotalVal = (TextView) findViewById(R.id.lblTotalVal);
        lblBalanceVal = (TextView) findViewById(R.id.lblBalanceVal);
        btnDOCODupdateNext = (Button) findViewById(R.id.btnDOCODupdateNext);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));
        activity = this;

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;

            context = myApp.getAppContext();
            setTitle(myApp.getAppContext().getString(R.string.title_activity_codupdate));

            /*getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);*/

            try {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "PreDeterminedStatusCodeIDX", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "mChildCheckStates2PrefStr", "");
                JobsHandler.resetrePositionedGroupItems(GcmPreferences);
                JobsHandler.checkForceLogOut();

                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];

                if (username.equals("") && password.equals("")) {
                    BackToLoginPage();
                }

                regid = objGlobalMethods.getRegistrationId(context, GcmPreferences);

                //cd = new ConnectionDetector(context);
                cd = new ConnectionDetector(null);
                if (!cd.isConnectingToInternet()) {
                    //// Internet Connection is not present
                    Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }

                /*Long JobsIDX = Long.parseLong("3266232");
                String hfJobInfoJsonStr = DAO_Jobs.gethfJobInfoJsonStrByJobsIDX(JobsIDX);*/
                String hfJobInfoJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "DOListArrJsonStr");

                if (al != null && al.size() > 0) {
                    loadlist("", al);
                } else if (hfJobInfoJsonStr != "" && hfJobInfoJsonStr != "[]") {
                    loadlist(hfJobInfoJsonStr, null);
                } else {
                    //show error n return
                    showErrorAlert();
                }


            } catch (Exception e) {
                //String stack = JobsHandler.getStackTraceString(e);
                //objGlobalMethods.writeToFile("Exception caught at CODupdate onResume. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                objGlobalMethods.writeToFile("Exception caught at CODupdate onResume. Exception: " + e.getMessage().toString(), "", "", "", "");
                showErrorAlert();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listView = null;
        lblTotalVal = null;
        lblBalanceVal = null;
        adapter = null;
        al = null;
        regid = null;
        activity = null;
        btnDOCODupdateNext = null;
        total = null;
        balance = null;
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CODupdate onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

        DAO_Jobs.nullifystaticitems();
    }

    @Override
    protected void onPause() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CODupdate onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_codupdate, menu);
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
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        Intent intent = null;

        objGlobalMethods.setGCMPrefs(GcmPreferences, "tempDOCODJsonStr", "");

        if (!fromPage.toUpperCase().equals("") && (fromPage.equals("JobInfoActivity") || fromPage.equals("OtpActivity"))) {
            intent = new Intent(this, JobInfoActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("JobsListGroupActivity")) {
            intent = new Intent(this, JobsListGroupActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("DOinvCheckActivity")) {
            intent = new Intent(this, DOinvCheckActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("CheckListActivity")) {
            intent = new Intent(this, CheckListActivity.class);
        } else {
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
            JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            intent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        }
        MainActivity.uponLogin = 0;
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(intent);
        this.finish();
    }

    public void BackToLoginPage() {
        al = null;
        adapter = null;
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
        parentIntent = new Intent(CODupdate.this, MainActivity.class);
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
        //objGlobalMethods.stopAlarmForCommon();
        FCMservice.notiNotRead = false;
        JobsHandler.clearGPSQue(LocationPrefs);
        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(2);
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

    private void showErrorAlert() {
        MainActivity.uponLogin = 0;
        //Dashboard2Activity.DashboardOpenFromPush = false;
        DrawerMainActivity.DashboardOpenFromPush = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "DOListArrJsonStr", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "tempDOCODJsonStr", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "updatemodeForFailure", "-1");
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                Intent i;
                al = null;
                adapter = null;
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(CODupdate.this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private static void showStaticErrorAlert() {
        try {
            final Context ctx = CODupdate.activity;

            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTypePrefName, "-1");
                    objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTitlePrefName, "");
                    objGlobalMethods.setGCMPrefs(gcmp, "DOListArrJsonStr", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "tempDOCODJsonStr", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "updatemodeForFailure", "-1");
                    JobsHandler.sethfJobsInfoArrJsonStrPref(gcmp, "");
                    Intent i;
                    al = null;
                    adapter = null;
                    boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                    i = new Intent(myApp.getAppContext(), !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                    //myApp.getAppContext().startActivity(i);
                    myApp.currentActivity.startActivity(i);
                    activity.finish();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at CODupdate showStaticErrorAlert. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
            objGlobalMethods.writeToFile("Exception caught at CODupdate showStaticErrorAlert. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public void CancelsetDeliveredStatus(View view) {
        onBackPressed();
    }

    private void loadlist(String hfJobInfoJsonStr, ArrayList<codupdate_PostData> existingal) {
        Gson gson = new Gson();
        codupdate_PostData data;
        Boolean success = false;
        Boolean[] amtTypeReq = new Boolean[]{false, false, false, false, false, false};
        List<Boolean> amtTypeReqList = new ArrayList<Boolean>(Arrays.asList(amtTypeReq));
        ArrayList<Long> AInvoicesIDXlist = new ArrayList<>();
        Boolean hasTradeIn = false;
        List<dbObjTradeInList> TradeInList = null;

        try {
            if (existingal != null && existingal.size() > 0) {
                al = existingal;
                adapter = new codupdate_PostItemAdapter(this, al);
                listView.setAdapter(adapter);
                recalculatebal();
            } else if ((existingal == null || existingal.size() == 0) && hfJobInfoJsonStr != "") {
                ArrayList<objDOObject[]> DOListArrayList = null;
                DOListArrayList = gson.fromJson(hfJobInfoJsonStr, new TypeToken<List<objDOObject[]>>() {
                }.getType());

                if (DOListArrayList != null && DOListArrayList.size() > 0) {
                    al = new ArrayList<>();
                    for (int i = 0; i <= DOListArrayList.size() - 1; i++) {
                        objDOObject[] dolist = DOListArrayList.get(i);
                        //amtTypeReq = initAmtTypeReq(dolist, amtTypeReq);
                        //al = initAL(amtTypeReq);
                        amtTypeReqList = initAmtTypeReqList(dolist, amtTypeReqList);
                        al = initAL2(amtTypeReqList);
                    }

                    for (int i = 0; i <= DOListArrayList.size() - 1; i++) {
                        objDOObject[] dolist = DOListArrayList.get(i);
                        for (int x = 0; x <= dolist.length - 1; x++) {
                            objDOObject DOObj = dolist[x];
                            if ((DOObj.DOIDX > 0) && !AInvoicesIDXlist.contains(DOObj.DOIDX) && (DOObj.CODList != null && DOObj.CODList.length > 0)) {
                                AInvoicesIDXlist.add(DOObj.DOIDX);
                            }
                        }
                    }

                    if (al != null && al.size() > 0) {
                        for (int i = 0; i <= DOListArrayList.size() - 1; i++) {
                            objDOObject[] dolist = DOListArrayList.get(i);
                            for (int x = 0; x <= dolist.length - 1; x++) {
                                objDOObject DOObj = dolist[x];
                                if (DOObj.CODList != null && DOObj.CODList.length > 0) {
                                    for (int z = 0; z <= DOObj.CODList.length - 1; z++) {
                                        objDOCOD docod = DOObj.CODList[z];
                                        if (docod.AmountTypeIDX == 6 && (docod.Reference != null && docod.Reference != "")) {
                                            TradeInList = DAO_Jobs.getTradeInModelByModel(docod.Reference, docod.Amount);
                                            if (TradeInList != null && TradeInList.size() > 0) {
                                                //do nothing
                                            } else {
                                                //add default as of CODLIST
                                                TradeInList = new ArrayList<dbObjTradeInList>();
                                                dbObjTradeInList objtil0 = new dbObjTradeInList(Long.parseLong("0"), "", docod.Reference, docod.Amount, docod.Reference, docod.Reference.replaceAll("\\s+", "").toUpperCase());
                                                TradeInList.add(objtil0);
                                            }
                                            dbObjTradeInList objtil = new dbObjTradeInList(Long.parseLong("0"), "", myApp.getAppContext().getString(R.string.tradeinmodel_none), 0.0, myApp.getAppContext().getString(R.string.tradeinmodel_none), myApp.getAppContext().getString(R.string.tradeinmodel_none_nospaces));
                                            TradeInList.add(0, objtil);
                                        }
                                        for (int j = 0; j <= al.size() - 1; j++) {
                                            if (docod.AmountTypeIDX == al.get(j).AmountTypeIDX) {
                                                if (docod.AmountTypeIDX == 6 && (docod.Reference != null && docod.Reference != "") &&
                                                        al.get(j).Amount == 0 && (al.get(j).Reference == null || al.get(j).Reference == "")) {
                                                    al.get(j).setAmountType(docod.AmountType);
                                                    al.get(j).addAmount(docod.Amount);
                                                    al.get(j).setTradInList(TradeInList);
                                                    al.get(j).setInitialValue(docod.Amount);
                                                    al.get(j).setReference(docod.Reference == null ? "" : docod.Reference);
                                                    al.get(j).setTradeInModelSpecifiedValue(docod.Reference == null ? "" : docod.Reference);
                                                    al.get(j).setselectedTradeInModelIndex(0);
                                                    if (TradeInList != null && TradeInList.size() > 0) {
                                                        for (int t = 0; t <= TradeInList.size() - 1; t++) {
                                                            //if (TradeInList.get(t).BrandModel.toUpperCase().equals(docod.Reference == null ? "" : docod.Reference.toUpperCase())) {
                                                            if (TradeInList.get(t).BrandModelNoSpaces.toUpperCase().equals(docod.Reference == null ? "" : docod.Reference.replaceAll("\\s+", "").toUpperCase())) {
                                                                TradeInList.get(t).setValue(docod.Amount);
                                                                al.get(j).setselectedTradeInModelIndex(t);
                                                                al.get(j).setPaidAmount(docod.Amount);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    break;
                                                } else if (docod.AmountTypeIDX != 6) {
                                                    al.get(j).setAmountType(docod.AmountType);
                                                    al.get(j).addAmount(docod.Amount);
                                                    //al.get(j).setTradInList(TradeInList);
                                                    al.get(j).addInitialValue(docod.Amount);
                                                    break;
                                                }
                                                //break;
                                            }
                                        }
                                    }
                                }
                                if (total == null) {
                                    total = DOObj.Amount;
                                } else {
                                    total += DOObj.Amount;
                                }

                                if (balance == null) {
                                    balance = DOObj.Amount;
                                } else {
                                    balance += DOObj.Amount;
                                }
                            }
                        }

                        for (int i = 0; i <= al.size() - 1; i++) {
                            al.get(i).setInvoiceIDX(AInvoicesIDXlist);
                            al.get(i).setInitialTotalCash(total);
                            al.get(i).setTotal(total);
                        }

                        if (al.size() > 0) {
                            adapter = new codupdate_PostItemAdapter(this, al);
                            listView.setAdapter(adapter);
                            recalculatebal();
                        } else {
                            showErrorAlert();
                        }
                    } else {
                        showErrorAlert();
                    }
                } else {
                    showErrorAlert();
                }
                //===========================================================================================================================================================
            } else {
                showErrorAlert();
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at CODupdate loadFreshlist. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at CODupdate loadFreshlist. Exception: " + e.getMessage().toString(), "", "", "", "");
            showErrorAlert();
        }
    }

    private Boolean[] initAmtTypeReq(objDOObject[] DOList, Boolean[] booArr) {

        try {
            for (int i = 0; i <= DOList.length - 1; i++) {
                objDOObject DOObj = DOList[i];
                if (DOObj.CODList != null && DOObj.CODList.length > 0) {
                    for (int x = 0; x <= DOObj.CODList.length - 1; x++) {
                        objDOCOD docod = DOObj.CODList[x];
                        if (docod.AmountTypeIDX == 1) {
                            //cash
                            booArr[0] = true; //cash
                            booArr[2] = true; //nets
                            booArr[3] = true; //visa
                        }
                        if (docod.AmountTypeIDX == 2) {
                            //cheque
                            //original
                            /*booArr[1] = true;*/

                            //newly requested by peter
                            booArr[0] = true; //cash
                            booArr[1] = true; //cheque
                            booArr[2] = true; //nets
                            booArr[3] = true; //visa
                        }
                        if (docod.AmountTypeIDX == 3) {
                            //nets
                            //original
                            booArr[0] = true; //cash
                            booArr[2] = true; //nets
                            booArr[3] = true; //visa
                        }
                        if (docod.AmountTypeIDX == 4) {
                            //credit
                            //original
                            booArr[0] = true; //cash
                            booArr[2] = true; //nets
                            booArr[3] = true; //visa
                        }
                        if (docod.AmountTypeIDX == 5) {
                            //voucher
                            //original
                            /*booArr[4] = true;*/

                            //newly requested by peter
                            booArr[0] = true; //cash
                            booArr[2] = true; //nets
                            booArr[3] = true; //visa
                            booArr[4] = true; //voucher
                        }
                        if (docod.AmountTypeIDX == 6 && (docod.Reference != null && docod.Reference != "")) {
                            //trade in
                            //original
                            booArr[0] = true; //cash
                            booArr[2] = true; //nets
                            booArr[3] = true; //visa
                            booArr[5] = true; //tradein
                        }
                    }
                }
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at CODupdate initAmtTypeReq. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at CODupdate initAmtTypeReq. Exception: " + e.getMessage().toString(), "", "", "", "");
            booArr = new Boolean[]{false, false, false, false, false, false};
        } finally {
            return booArr;
        }
    }

    private List<Boolean> initAmtTypeReqList(objDOObject[] DOList, List<Boolean> booArr) {

        try {
            for (int i = 0; i <= DOList.length - 1; i++) {
                objDOObject DOObj = DOList[i];
                if (DOObj.CODList != null && DOObj.CODList.length > 0) {
                    for (int x = 0; x <= DOObj.CODList.length - 1; x++) {
                        objDOCOD docod = DOObj.CODList[x];
                        if (docod.AmountTypeIDX == 1) {
                            //cash
                            booArr.set(0, true); //cash
                            booArr.set(2, true); //nets
                            booArr.set(3, true); //visa
                        }
                        if (docod.AmountTypeIDX == 2) {
                            //cheque
                            //original
                            /*booArr[1] = true;*/

                            //newly requested by peter
                            //newly requested by peter
                            booArr.set(0, true); //cash
                            booArr.set(1, true); //cheque
                            booArr.set(2, true); //nets
                            booArr.set(3, true); //visa
                        }
                        if (docod.AmountTypeIDX == 3) {
                            //nets
                            //original
                            booArr.set(0, true); //cash
                            booArr.set(2, true); //nets
                            booArr.set(3, true); //visa
                        }
                        if (docod.AmountTypeIDX == 4) {
                            //credit
                            //original
                            booArr.set(0, true); //cash
                            booArr.set(2, true); //nets
                            booArr.set(3, true); //visa
                        }
                        if (docod.AmountTypeIDX == 5) {
                            //voucher
                            //original
                            /*booArr[4] = true;*/

                            //newly requested by peter
                            booArr.set(0, true); //cash
                            booArr.set(2, true); //nets
                            booArr.set(3, true); //visa
                            booArr.set(4, true); //voucher
                        }
                        if (docod.AmountTypeIDX == 6 && (docod.Reference != null && docod.Reference != "")) {
                            //trade in
                            //original
                            if (booArr.get(5)) {
                                booArr.add(true);
                            } else {
                                booArr.set(0, true); //cash
                                booArr.set(2, true); //nets
                                booArr.set(3, true); //visa
                                booArr.set(5, true); //tradein
                            }
                        }
                        if (docod.AmountTypeIDX == 10) {
                            booArr.set(0, true); //cash
                            booArr.set(2, true); //nets
                            booArr.set(3, true); //visa
                        }
                    }
                }
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at CODupdate initAmtTypeReqList. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at CODupdate initAmtTypeReqList. Exception: " + e.getMessage().toString(), "", "", "", "");
            Boolean[] amtTypeReq = new Boolean[]{false, false, false, false, false, false};
            booArr = new ArrayList<Boolean>(Arrays.asList(amtTypeReq));
        } finally {
            return booArr;
        }
    }

    private ArrayList<codupdate_PostData> initAL(Boolean[] amtTypeReq) {
        ArrayList<codupdate_PostData> arr = new ArrayList<>();
        codupdate_PostData data;
        Boolean exist = false;

        try {
            for (int i = 0; i <= amtTypeReq.length - 1; i++) {
                if (amtTypeReq[i]) {
                    //Long DOObjectIDX, Long JobsIDX,
                    // Long AmountTypeIDX, String AmountType, String Reference, Double Amount, Double PaidAmount, int group, boolean showfull
                    ArrayList<Long> InvoiceIDXList = new ArrayList<>();
                    data = new codupdate_PostData(Long.parseLong("0"), InvoiceIDXList, Long.parseLong("0"), "0",
                            Long.parseLong(String.valueOf(i + 1)),
                            (i + 1) == 1 ? "CASH" : (i + 1) == 2 ? "CHEQUE" : (i + 1) == 3 ? "NETS" : (i + 1) == 4 ? "VISA/MASTER" : (i + 1) == 5 ? "VOUCHER" : "TRADE-IN",
                            "", 0.0, 0.0, 0, true, null, 0.0, 0, 0.0, 0.0, "");
                    arr.add(data);
                }
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caughtat CODupdate initAL. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caughtat CODupdate initAL. Exception: " + e.getMessage().toString(), "", "", "", "");
            arr = null;
        } finally {
            return arr;
        }
    }

    private ArrayList<codupdate_PostData> initAL2(List<Boolean> amtTypeReqList) {
        ArrayList<codupdate_PostData> arr = new ArrayList<>();
        codupdate_PostData data;
        Boolean exist = false;

        try {
            for (int i = 0; i <= amtTypeReqList.size() - 1; i++) {
                if (amtTypeReqList.get(i)) {
                    //Long DOObjectIDX, Long JobsIDX,
                    // Long AmountTypeIDX, String AmountType, String Reference, Double Amount, Double PaidAmount, int group, boolean showfull
                    ArrayList<Long> InvoiceIDXList = new ArrayList<>();
                    /*data = new codupdate_PostData(Long.parseLong("0"), InvoiceIDXList, Long.parseLong("0"), "0",
                            Long.parseLong(String.valueOf(i + 1)),
                            (i+1) == 1 ? "CASH" :(i+1) == 2 ? "CHEQUE" :(i+1) == 3 ? "NETS" :(i+1) == 4 ? "VISA/MASTER" :(i+1) == 5 ? "VOUCHER" : "TRADE-IN",
                            "", 0.0, 0.0, 0, true, null, 0.0, 0, 0.0, 0.0, "");*/
                    data = new codupdate_PostData(Long.parseLong("0"), InvoiceIDXList, Long.parseLong("0"), "0",
                            i + 1 >= 6 ? 6 : Long.parseLong(String.valueOf(i + 1)),
                            (i + 1) == 1 ? "CASH" : (i + 1) == 2 ? "CHEQUE" : (i + 1) == 3 ? "NETS" : (i + 1) == 4 ? "VISA/MASTER" : (i + 1) == 5 ? "VOUCHER" : "TRADE-IN",
                            "", 0.0, 0.0, 0, true, null, 0.0, 0, 0.0, 0.0, "");
                    arr.add(data);
                }
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caughtat CODupdate initAL2. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caughtat CODupdate initAL2. Exception: " + e.getMessage().toString(), "", "", "", "");
            arr = null;
        } finally {
            return arr;
        }
    }

    public static void recalculatebal() {
        DecimalFormat df = new DecimalFormat("0.00");
        codupdate_PostData data;
        Double paid = 0.0;
        Double datapaid = 0.0;
        total = 0.0;
        balance = 0.0;
        Boolean reqRef = false;
        Boolean RefNotFound = false;
        Boolean hasTradeIn = false;
        Boolean hasTradeInRef = false;
        DecimalFormat dec = new DecimalFormat("0.00");
        try {
            if (al != null) {
                for (int i = 0; i <= al.size() - 1; i++) {
                    data = al.get(i);
                    total = data.Total;
                    //paid += data.PaidAmount;
                    datapaid = Double.parseDouble(df.format(data.PaidAmount));
                    paid += datapaid;
                    if (data.AmountTypeIDX != 1) {
                        //if (data.PaidAmount > 0.0 && data.Reference.trim().equals("") && data.AmountTypeIDX != 3 && data.AmountTypeIDX != 4) {
                        if (datapaid > 0.0 && data.Reference.trim().equals("") && data.AmountTypeIDX != 3 && data.AmountTypeIDX != 4) {
                            RefNotFound = true;
                        }

                        if (data.AmountTypeIDX == 6) {
                            hasTradeIn = true;
                        }
                    }
                }

                //balance = total - paid;
                balance = Double.parseDouble(df.format(total - paid));

                if (balance == 0.0 && hasTradeIn) {
                    for (int i = al.size() - 1; i >= 0; i--) {
                        if (al.get(i).AmountTypeIDX == 6 && !al.get(i).Reference.trim().equals("")) {
                            hasTradeInRef = true;
                        }
                    }
                }

                lblTotalVal.setText(total == 0.0 ? "0.00" : String.valueOf(dec.format(total)));
                lblBalanceVal.setText(balance == 0.0 ? "0.00" : String.valueOf(dec.format(balance)));

                if (hasTradeIn && !hasTradeInRef) {
                    //ctrlSettings.enableDisableCommonButton(false, btnDOCODupdateNext);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnDOCODupdateNext);
                } else if (balance == 0.0 && !RefNotFound) {
                    //ctrlSettings.enableDisableCommonButton(true, btnDOCODupdateNext);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnDOCODupdateNext);
                } else {
                    //ctrlSettings.enableDisableCommonButton(false, btnDOCODupdateNext);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnDOCODupdateNext);
                }
            } else {
                showStaticErrorAlert();
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at CODupdate recalculatebal. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at CODupdate recalculatebal. Exception: " + e.getMessage().toString(), "", "", "", "");
            showStaticErrorAlert();
        }
    }

    public void btnNextClick(View view) {
        if (al != null && al.size() > 0) {
            next();
        } else {
            objGlobalMethods.writeToFile("Unable to next CODupdate. al is null or empty.", "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorupdatep1p2));
        }
    }

    public void next() {
        Gson gson = new Gson();
        Date dt = new Date();
        ArrayList<codupdate_PostData> result = al;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        ArrayList<Long> AInvoicesIDXlist = new ArrayList<>();
        Long[] AInvoicesIDXArr = null;
        ArrayList<objUpdateDOCOD> objUpdateDOCODlist = new ArrayList<>();
        objUpdateDOCOD[] objUpdateDOCODArr = null;
        final UpdateDOCOD updatedocod = new UpdateDOCOD();

        try {
            Long l = dt.getTime();
            final String timestampstr = "/Date(" + l + ")/";
            updatedocod.setARegID(regid);
            updatedocod.setAUTC(timestampstr);
            objGlobalMethods.writeToFile("CODupdate submit started.", "", "", "", "");

            for (int i = 0; i <= al.size() - 1; i++) {
                AInvoicesIDXlist = al.get(i).InvoiceIDXList;
                objUpdateDOCODlist.add(new objUpdateDOCOD(Long.parseLong("0"), Long.parseLong("0"), al.get(i).AmountTypeIDX, al.get(i).AmountType, al.get(i).Reference, al.get(i).PaidAmount));
            }

            if (AInvoicesIDXlist.size() > 0 && objUpdateDOCODlist.size() > 0) {
                AInvoicesIDXArr = AInvoicesIDXlist.toArray(new Long[AInvoicesIDXlist.size()]);
                objUpdateDOCODArr = objUpdateDOCODlist.toArray(new objUpdateDOCOD[objUpdateDOCODlist.size()]);
                updatedocod.setAInvoicesIDX(AInvoicesIDXArr);
                updatedocod.setACODList(objUpdateDOCODArr);

                String tempDOCODJsonStr = gson.toJson(updatedocod);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "tempDOCODJsonStr", tempDOCODJsonStr);

                startActivity(nextActivtyIntent);

            } else {
                objGlobalMethods.writeToFile("CODupdate progress dialog thread AInvoicesIDXlist or objDOCODlist is empty.", "", "", "", "");
                showErrorAlert();
            }

        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at CODupdate submit. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at CODupdate submit. Exception: " + e.getMessage().toString(), "", "", "", "");
            showErrorAlert();

        }


    }

}
