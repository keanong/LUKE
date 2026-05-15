package com.xdel.luke.DOCOD;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.CODupdate;
import com.xdel.luke.CheckListActivity;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DOinvCheckActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsListGroupActivity;
import com.xdel.luke.MainActivity;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.UpdateDOCOD;
import com.xdel.luke.objClasses.codupdate_PostData;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objDOCOD;
import com.xdel.luke.objClasses.objDOObject;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objUpdateDOCOD;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjTradeInList;
import com.xdel.luke.templogger.temploggerclass;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CODupdate2 extends AppCompatActivity {

    private SharedPreferences gcmp, UpdateJobInfoPreferences, LocationPrefs;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    private MediaPlayer player;
    ConnectionDetector cd;
    Context context;

    private DOCOD_ListAdapter adapter;
    private ExpandableListView expListView;
    private EditText txtInput;

    public Double total = 0.0;
    public Double balance = 0.0;
    public static String fromPage = "";
    public static Intent nextActivtyIntent;
    public TextView lblTotalVal, lblBalanceVal;
    public Button btnCODupdate2Next;

    public static List<String> listDataHeader;
    public static HashMap<String, ArrayList<codupdate_PostData>> listDataChildSortedSQLite2;
    private ArrayList<String> headerAl;
    private ArrayList itemAL;
    private ArrayList<codupdate_PostData> subGroupItemSqliteAL;
    private final DecimalFormat dec = new DecimalFormat("0.00");
    static String cameraScannedCode;

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
        setContentView(R.layout.activity_codupdate2);

        lblTotalVal = (TextView) findViewById(R.id.lblTotalVal);
        lblBalanceVal = (TextView) findViewById(R.id.lblBalanceVal);
        btnCODupdate2Next = (Button) findViewById(R.id.btnCODupdate2Next);
        expListView = (ExpandableListView) findViewById(R.id.expListView);
        txtInput = (EditText) findViewById(R.id.txtInput);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                gcmp = objGlobalMethods.getGcmPreferences();
                if (!objGlobalMethods.UserCanScreenShot(gcmp)) {
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;
                context = myApp.getAppContext();
                setTitle(myApp.getAppContext().getString(R.string.title_activity_codupdate));
                objGlobalMethods.setGCMPrefs(gcmp, "PreDeterminedStatusCodeIDX", "");
                objGlobalMethods.setGCMPrefs(gcmp, "mChildCheckStates2PrefStr", "");
                JobsHandler.resetrePositionedGroupItems(gcmp);
                JobsHandler.checkForceLogOut();

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("CODupdate2 !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];

                if (username.equals("") && password.equals("")) {
                    BackToLoginPage();
                }

                //cd = new ConnectionDetector(context);
                cd = new ConnectionDetector(null);
                if (!cd.isConnectingToInternet()) {
                    //// Internet Connection is not present
                    Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }

                txtInput.setOnTouchListener(null);
                txtInput.setOnTouchListener(rtnEditTextOnTouchListener());
                loadlist2();

                cameraScannedCode = objGlobalMethods.getGCMPrefs(gcmp, "cameraScannedCode");
                if (!objGlobalMethods.IsEmptyString(cameraScannedCode)) {
                    scrolltojob();
                    objGlobalMethods.setGCMPrefs(gcmp, "cameraScannedCode", "");
                }

                txtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        cameraScannedCode = null;
                        if (!objGlobalMethods.IsEmptyString(txtInput.getText().toString())) {
                            scrolltojob();
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    expListView.clearFocus();
                                    txtInput.clearFocus();
                                    txtInput.setText("");
                                    txtInput.requestFocus();
                                }
                            }, 100);
                        }

                        return true;
                    }
                });

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at CODupdate2 onResume. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
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
        gcmp = objGlobalMethods.getGcmPreferences();
        String[] prefName = new String[]{"QRScanRequestIntent", "cameraScannedCode"};
        String[] prefvalue = new String[]{"CODupdate2", ""};
        objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);

        parentIntent = new Intent(CODupdate2.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listDataHeader = null;
        listDataChildSortedSQLite2 = null;
        adapter = null;
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CODupdate2 onDestroy unregisterContentObserver. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
                objGlobalMethods.writeToFile("Exception at CODupdate2 onPause unregisterContentObserver. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    private void back() {
        gcmp = objGlobalMethods.getGcmPreferences();
        Intent intent = null;

        objGlobalMethods.setGCMPrefs(gcmp, "tempDOCODListJsonStr", "");

        if (!fromPage.toUpperCase().equals("") && (fromPage.equals("JobInfoActivity") || fromPage.equals("OtpActivity"))) {
            intent = new Intent(this, JobInfoActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("JobsListGroupActivity")) {
            intent = new Intent(this, JobsListGroupActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("DOinvCheckActivity")) {
            intent = new Intent(this, DOinvCheckActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("CheckListActivity")) {
            intent = new Intent(this, CheckListActivity.class);
        } else {
            objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTypePrefName, "-1");
            objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTitlePrefName, "");
            JobsHandler.sethfJobsInfoArrJsonStrPref(gcmp, "");
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            intent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        }
        MainActivity.uponLogin = 0;
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void BackToLoginPage() {
        /*al = null;
        adapter = null;*/
        gcmp = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTitlePrefName, "");
        objGlobalMethods.setGCMPrefs(gcmp, "wifistate", "");
        objGlobalMethods.setGCMPrefs(gcmp, "leftofc", "");
        JobsHandler.resetAllJobsInfoRelatedPref(gcmp, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(gcmp);
        JobsHandler.sethfJobsInfoArrJsonStrPref(gcmp, "");
        Intent parentIntent;
        parentIntent = new Intent(this, MainActivity.class);
        JobsHandler.storeLogin(gcmp, "", "", "", "false");
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
                gcmp = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.categoryTitlePrefName, "");
                objGlobalMethods.setGCMPrefs(gcmp, "DOListArrJsonStr", "");
                objGlobalMethods.setGCMPrefs(gcmp, "tempDOCODListJsonStr", "");
                objGlobalMethods.setGCMPrefs(gcmp, "updatemodeForFailure", "-1");
                JobsHandler.sethfJobsInfoArrJsonStrPref(gcmp, "");
                Intent i;
                /*al = null;
                adapter = null;*/
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
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
                    objGlobalMethods.setGCMPrefs(gcmp, "tempDOCODListJsonStr", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "updatemodeForFailure", "-1");
                    JobsHandler.sethfJobsInfoArrJsonStrPref(gcmp, "");
                    Intent i;
                    /*al = null;
                    adapter = null;*/
                    boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                    i = new Intent(myApp.getAppContext(), !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                    //myApp.getAppContext().startActivity(i);
                    myApp.currentActivity.startActivity(i);
                    myApp.currentActivity.finish();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at CODupdate showStaticErrorAlert. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public void btnCODupdate2Cancel_Click(View v) {
        back();
    }

    private void loadlist2() {
        Gson gson = new Gson();
        objDOObject[] dolist = null;
        objDOObject DOObj = null;
        headerAl = new ArrayList<String>();
        itemAL = new ArrayList();
        codupdate_PostData postdata;
        ArrayList<codupdate_PostData> al;
        Boolean[] amtTypeReq = new Boolean[]{false, false, false, false, false, false};
        List<Boolean> amtTypeReqList;
        ArrayList<Long> AInvoicesIDXlist = null;
        List<dbObjTradeInList> TradeInList = null;
        String InvoiceStr = "";
        int headerALselectedIDX = 0;
        boolean canLoadList = false;
        try {
            if (listDataHeader != null && listDataChildSortedSQLite2 != null) {
                //adapter = new DOCOD_ListAdapter(this);
                adapter = new DOCOD_ListAdapter(this, listDataHeader, listDataChildSortedSQLite2);
                canLoadList = true;
            } else {
                listDataHeader = new ArrayList<String>();
                listDataChildSortedSQLite2 = new HashMap<String, ArrayList<codupdate_PostData>>();
                String DOListArrJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "DOListArrJsonStr");
                if (!objGlobalMethods.IsEmptyString(DOListArrJsonStr)) {
                    ArrayList<objDOObject[]> DOListArrayList = null;
                    DOListArrayList = gson.fromJson(DOListArrJsonStr, new TypeToken<List<objDOObject[]>>() {
                    }.getType());

                    if (DOListArrayList != null && DOListArrayList.size() > 0) {
                        for (int i = 0; i <= DOListArrayList.size() - 1; i++) {
                            total = 0.0;
                            InvoiceStr = "";
                            AInvoicesIDXlist = new ArrayList<>();
                            al = new ArrayList<>();
                            dolist = null;
                            dolist = DOListArrayList.get(i);
                            amtTypeReqList = new ArrayList<Boolean>(Arrays.asList(amtTypeReq));
                            amtTypeReqList = initAmtTypeReqList(dolist, amtTypeReqList);
                            al = initAL2(amtTypeReqList);

                            for (int x = 0; x <= dolist.length - 1; x++) {
                                DOObj = dolist[x];
                                InvoiceStr += DOObj.Invoice + ",";
                                if ((DOObj.DOIDX > 0) && !AInvoicesIDXlist.contains(DOObj.DOIDX) && (DOObj.CODList != null && DOObj.CODList.length > 0)) {
                                    AInvoicesIDXlist.add(DOObj.DOIDX);
                                }
                            }

                            if (al != null && al.size() > 0) {
                                for (int x = 0; x <= dolist.length - 1; x++) {
                                    DOObj = dolist[x];
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
                                                al.get(j).setJobsIDX(DOObj.JobsIDX);
                                                al.get(j).setJobNo(DOObj.JobNo);
                                                al.get(j).setCN(DOObj.CN);
                                                al.get(j).setDoNum(DOObj.Invoice);
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

                                /*if (balance == null) {
                                    balance = DOObj.Amount;
                                } else {
                                    balance += DOObj.Amount;
                                }*/
                                }


                                for (int a = 0; a <= al.size() - 1; a++) {
                                    al.get(a).setInvoiceIDX(AInvoicesIDXlist);
                                    al.get(a).setInitialTotalCash(total);
                                    al.get(a).setTotal(total);
                                }

                                if (!headerAl.contains(InvoiceStr.substring(0, InvoiceStr.length() -1))) {
                                    headerAl.add(InvoiceStr.substring(0, InvoiceStr.length() -1));
                                    subGroupItemSqliteAL = new ArrayList<>();
                                    itemAL.add(al);
                                } else {
                                    headerALselectedIDX = headerAl.indexOf(InvoiceStr.substring(0, InvoiceStr.length() -1));

                                }

                            }
                        }

                        ArrayList<codupdate_PostData> postdataal = null;
                        for (int i = 0; i <= headerAl.size() - 1; i++) {
                            listDataHeader.add(headerAl.get(i));
                            //listDataChildSortedSQLite2.put(headerAl.get(i), (ArrayList<codupdate_PostData>) itemAL.get(i));
                            for (int d = 0; d <= itemAL.size() - 1; d++) {
                                postdataal = (ArrayList<codupdate_PostData>) itemAL.get(d);
                                if (postdataal != null && postdataal.size() > 0) {
                                    if (objGlobalMethods.IsEqualString(postdataal.get(0).DoNum, headerAl.get(i))) {
                                        listDataChildSortedSQLite2.put(headerAl.get(i), postdataal);
                                        break;
                                    }
                                }
                            }
                        }

                        //adapter = new DOCOD_ListAdapter(this);
                        adapter = new DOCOD_ListAdapter(this, listDataHeader, listDataChildSortedSQLite2);
                        canLoadList = true;
                    } else {
                        showErrorAlert();
                    }
                } else {
                    showErrorAlert();
                }
            }

            if (canLoadList) {
                adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
                // setting list adapter
                expListView.setAdapter(adapter);
                setGroupIndicatorToRight();
                expandSortedSQLiteList(adapter);
                recalculatebal();
            } else {
                showErrorAlert();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CODupdate2 loadlist2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void loadlist() {
        Gson gson = new Gson();
        objDOObject[] dolist = null;
        objDOObject DOObj = null;
        headerAl = new ArrayList<String>();
        itemAL = new ArrayList();
        listDataHeader = new ArrayList<String>();
        listDataChildSortedSQLite2 = new HashMap<String, ArrayList<codupdate_PostData>>();
        codupdate_PostData postdata;
        ArrayList<codupdate_PostData> al;
        Boolean[] amtTypeReq = new Boolean[]{false, false, false, false, false, false};
        List<Boolean> amtTypeReqList;
        ArrayList<Long> AInvoicesIDXlist = null;
        List<dbObjTradeInList> TradeInList = null;
        String InvoiceStr = "";
        int headerALselectedIDX = 0;
        try {
            String hfJobInfoJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "DOListArrJsonStr");
            if (!objGlobalMethods.IsEmptyString(hfJobInfoJsonStr)) {
                ArrayList<objDOObject[]> DOListArrayList = null;
                DOListArrayList = gson.fromJson(hfJobInfoJsonStr, new TypeToken<List<objDOObject[]>>() {
                }.getType());

                if (DOListArrayList != null && DOListArrayList.size() > 0) {
                    for (int i = 0; i <= DOListArrayList.size() - 1; i++) {
                        total = 0.0;
                        InvoiceStr = "";
                        AInvoicesIDXlist = new ArrayList<>();
                        al = new ArrayList<>();
                        dolist = null;
                        dolist = DOListArrayList.get(i);
                        amtTypeReqList = new ArrayList<Boolean>(Arrays.asList(amtTypeReq));
                        amtTypeReqList = initAmtTypeReqList(dolist, amtTypeReqList);
                        al = initAL2(amtTypeReqList);

                        for (int x = 0; x <= dolist.length - 1; x++) {
                            DOObj = dolist[x];
                            InvoiceStr += DOObj.Invoice + ",";
                            if ((DOObj.DOIDX > 0) && !AInvoicesIDXlist.contains(DOObj.DOIDX) && (DOObj.CODList != null && DOObj.CODList.length > 0)) {
                                AInvoicesIDXlist.add(DOObj.DOIDX);
                            }
                        }

                        if (al != null && al.size() > 0) {
                            for (int x = 0; x <= dolist.length - 1; x++) {
                                DOObj = dolist[x];
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

                                /*if (balance == null) {
                                    balance = DOObj.Amount;
                                } else {
                                    balance += DOObj.Amount;
                                }*/
                            }


                            for (int a = 0; a <= al.size() - 1; a++) {
                                al.get(a).setInvoiceIDX(AInvoicesIDXlist);
                                al.get(a).setInitialTotalCash(total);
                                al.get(a).setTotal(total);
                            }

                            if (!headerAl.contains(InvoiceStr.substring(0, InvoiceStr.length() -1))) {
                                headerAl.add(InvoiceStr.substring(0, InvoiceStr.length() -1));
                                subGroupItemSqliteAL = new ArrayList<>();
                                itemAL.add(al);
                            } else {
                                headerALselectedIDX = headerAl.indexOf(InvoiceStr.substring(0, InvoiceStr.length() -1));

                            }

                        }




                    }

                    for (int i = 0; i <= headerAl.size() - 1; i++) {
                        listDataHeader.add(headerAl.get(i));
                        listDataChildSortedSQLite2.put(headerAl.get(i), (ArrayList<codupdate_PostData>) itemAL.get(i));
                    }

                    adapter = new DOCOD_ListAdapter(this, listDataHeader, listDataChildSortedSQLite2);
                    adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

                    // setting list adapter
                    expListView.setAdapter(adapter);
                    setGroupIndicatorToRight();
                    expandSortedSQLiteList(adapter);
                    recalculatebal();
                } else {
                    showErrorAlert();
                }
            } else {
                showErrorAlert();
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CODupdate2 loadlist. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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

    private void expandSortedSQLiteList(DOCOD_ListAdapter adapter) {
        for (int i = 0; i <= adapter.getGroupCount() - 1; i++) {
            expListView.expandGroup(i);
        }
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
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

    public void setselection(int selectionindex) {
        try {
            expListView.setSelection(selectionindex);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at CODupdate setselection. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public void recalculatebal() {
        codupdate_PostData data;
        Double individualpaid = 0.0;
        Double paid = 0.0;
        total = 0.0;
        balance = 0.0;
        Boolean reqRef = false;
        Boolean RefNotFound = false;
        Boolean hasTradeIn = false;
        Boolean hasTradeInRef = false;
        ArrayList<codupdate_PostData> al;
        boolean hasOverpaid = false;
        try {
            if (expListView != null) {
                adapter = (DOCOD_ListAdapter) expListView.getExpandableListAdapter();
                if (adapter != null) {
                    HashMap<String, ArrayList<codupdate_PostData>> _listDataChild = adapter.get_listDataChild();
                    if (_listDataChild != null) {
                        for (int x = 0; x <= _listDataChild.size()-1; x++) {
                            al = adapter.getList(x);
                            individualpaid = 0.0;
                            for (int i = 0; i <= al.size() - 1; i++) {
                                data = al.get(i);
                                if (i == 0) {
                                    total += data.Total;
                                }
                                individualpaid += data.PaidAmount;
                                paid += data.PaidAmount;
                                if (!hasOverpaid && (individualpaid > data.Total)) {
                                    hasOverpaid = true;
                                }
                            }
                        }
                        balance += total - paid;
                        lblTotalVal.setText(total == 0.0 ? "0.00" : String.valueOf(dec.format(total)));
                        lblBalanceVal.setText(balance == 0.0 ? "0.00" : String.valueOf(dec.format(balance)));

                        if (balance == 0.0 && !hasOverpaid) {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnCODupdate2Next);
                        } else {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnCODupdate2Next);
                        }

                    } else {
                        showStaticErrorAlert();
                    }
                } else {
                    showStaticErrorAlert();
                }
            } else {
                showStaticErrorAlert();
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at CODupdate2 recalculatebal. Exception: " + e.getMessage() + "\nStack: " + stack, "", "", "", "");
            showStaticErrorAlert();
        }
    }

    public void btnCODupdate2NextClick(View view) {
        if (expListView != null) {
            adapter = (DOCOD_ListAdapter) expListView.getExpandableListAdapter();
            if (adapter != null) {
                HashMap<String, ArrayList<codupdate_PostData>> _listDataChild = adapter.get_listDataChild();
                if (_listDataChild != null) {
                    next(_listDataChild);
                } else {
                    objGlobalMethods.writeToFile("Unable to next CODupdate2. _listDataChild is null or empty.", "", "", "", "");
                    showDialog(myApp.getAppContext().getString(R.string.alert_errorupdatep1p2));
                }
            } else {
                objGlobalMethods.writeToFile("Unable to next CODupdate2. adapter is null or empty.", "", "", "", "");
                showDialog(myApp.getAppContext().getString(R.string.alert_errorupdatep1p2));
            }
        } else {
            objGlobalMethods.writeToFile("Unable to next CODupdate2. expListView is null or empty.", "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorupdatep1p2));
        }
    }

    public void next(HashMap<String, ArrayList<codupdate_PostData>> _listDataChild) {
        Gson gson = new Gson();
        Date dt = new Date();
        ArrayList<codupdate_PostData> al;
        gcmp = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        ArrayList<Long> AInvoicesIDXlist = new ArrayList<>();
        Long[] AInvoicesIDXArr = null;
        ArrayList<objUpdateDOCOD> objUpdateDOCODlist = new ArrayList<>();
        objUpdateDOCOD[] objUpdateDOCODArr = null;
        List<UpdateDOCOD> updatedocodList = new ArrayList<>();
        UpdateDOCOD updatedocod = new UpdateDOCOD();
        String regid = objGlobalMethods.getRegistrationIdOnly();
        try {
            Long l = dt.getTime();
            final String timestampstr = "/Date(" + l + ")/";

            objGlobalMethods.writeToFile("CODupdate submit started.", "", "", "", "");
            for (int x = 0; x <= _listDataChild.size()-1; x++) {
                al = adapter.getList(x);
                AInvoicesIDXlist = new ArrayList<>();
                objUpdateDOCODlist = new ArrayList<>();
                if (al != null && al.size() > 0) {
                    updatedocod = new UpdateDOCOD();
                    updatedocod.setARegID(regid);
                    updatedocod.setAUTC(timestampstr);
                    for (int i = 0; i <= al.size() - 1; i++) {
                        AInvoicesIDXlist = al.get(i).InvoiceIDXList;
                        objUpdateDOCODlist.add(new objUpdateDOCOD(Long.parseLong("0"), Long.parseLong("0"), al.get(i).AmountTypeIDX, al.get(i).AmountType, al.get(i).Reference, al.get(i).PaidAmount));
                    }
                    AInvoicesIDXArr = AInvoicesIDXlist.toArray(new Long[AInvoicesIDXlist.size()]);
                    objUpdateDOCODArr = objUpdateDOCODlist.toArray(new objUpdateDOCOD[objUpdateDOCODlist.size()]);
                    updatedocod.setAInvoicesIDX(AInvoicesIDXArr);
                    updatedocod.setACODList(objUpdateDOCODArr);
                    updatedocodList.add(updatedocod);
                }
            }

            if (updatedocodList.size() > 0) {
                String tempDOCODListJsonStr = gson.toJson(updatedocodList);
                objGlobalMethods.setGCMPrefs(gcmp, "tempDOCODListJsonStr", tempDOCODListJsonStr);

                startActivity(nextActivtyIntent);
            } else {
                objGlobalMethods.writeToFile("CODupdate2 progress dialog thread AInvoicesIDXlist or objDOCODlist is empty.", "", "", "", "");
                showErrorAlert();
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at CODupdate2 submit. Exception: " + e.getMessage() + "\nStack: " + stack, "", "", "", "");
            showErrorAlert();
        }


    }

    public View.OnTouchListener rtnEditTextOnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;*/
                try {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        if(txtInput.getCompoundDrawables()[2]!=null){
                            if(event.getX() >= (txtInput.getRight()- txtInput.getLeft() - txtInput.getCompoundDrawables()[2].getBounds().width())) {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        scrolltojob();
                                    }
                                }, 100);
                            }
                        }
                    }
                    return false;
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at CODupdate2 rtnEditTextOnTouchListener. Exception: " + e.getMessage() + "\nStack: + s","","","","");
                }
                return true;
            }
        };
    }

    private void scrolltojob() {
        String c = "";
        try {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            if (cameraScannedCode != null) {
                if (!cameraScannedCode.equals("")) {
                    c = cameraScannedCode.toUpperCase();
                } else {
                    c = txtInput.getText().toString().toUpperCase();
                }
            } else {
                c = txtInput.getText().toString().toUpperCase();
            }

            if (c.indexOf(objGlobalVariables.JobIXKey) == 0) {
                c = c.substring(2, c.length());
                while (c.indexOf("0") == 0) {
                    c = c.substring(1, c.length());
                }
            }

            adapter = (DOCOD_ListAdapter) expListView.getExpandableListAdapter();
            List<String> DataHeader;
            HashMap<String, ArrayList<codupdate_PostData>> DataChild2;
            HashMap.Entry<String, ArrayList<codupdate_PostData>> entry = null;
            DataHeader = adapter._listDataHeader;
            DataChild2 = adapter._listDataChild;
            ArrayList<codupdate_PostData> list = null;
            codupdate_PostData data = null;
            Iterator<HashMap.Entry<String, ArrayList<codupdate_PostData>>> itr = null;
            String key = "";
            boolean found = false;
            int headerIndex = -1;
            int headerIndex2 = -1;
            int zIndex = 0;
            int index = -1;
            String headertext = "";
            boolean scrolltolast = false;
            boolean scrolltotop = false;

            for (int i = 0; i <= DataHeader.size() -1; i++) {
                headertext = DataHeader.get(i);
                if (objGlobalMethods.IsEqualString(headertext, c)) {
                    headerIndex = i;
                    found = true;
                    scrolltotop = (i == 0);
                    scrolltolast = (i == DataHeader.size()-1);
                    break;
                }
            }

            if (found) {
                if (scrolltolast) {
                    itr = DataChild2.entrySet().iterator();
                    while (itr.hasNext()) {
                        index++;
                        entry = itr.next();
                        key = entry.getKey();
                        list = entry.getValue();
                        headerIndex2 = DataHeader.indexOf(key);
                        if (!grpIsCollapsed(adapter, headerIndex2)) {
                            index += (list != null && list.size() > 0) ? list.size() : 0;
                        }
                    }
                } else if (scrolltotop) {
                    index = 0;
                } else {
                    itr = DataChild2.entrySet().iterator();
                    while (itr.hasNext()) {
                        entry = itr.next();
                        key = entry.getKey();
                        list = entry.getValue();
                        headerIndex2 = DataHeader.indexOf(key);
                        if (headerIndex2 < headerIndex) {
                            index++;
                            if (!grpIsCollapsed(adapter, headerIndex2)) {
                                index += (list != null && list.size() > 0) ? list.size() : 0;
                            }
                        } else if (headerIndex2 == headerIndex) {
                            index++; //purposely + 1 here
                        }
                    }
                }
            }

            if (!found) {
                itr = DataChild2.entrySet().iterator();
                outer:
                while (itr.hasNext()) {
                    entry = itr.next();
                    key = entry.getKey();
                    list = entry.getValue();
                    headerIndex = DataHeader.indexOf(key);
                    if (list != null && list.size() > 0) {
                        data = list.get(0);
                        if ((objGlobalMethods.IsEqualString(data.JobsIDX, c)) || (objGlobalMethods.IsEqualString(data.CN, c))) {
                            found = true;
                            scrolltotop = (headerIndex == 0);
                            scrolltolast = (headerIndex == DataHeader.size() -1);
                            break outer;
                        }
                    }
                }

                if (scrolltotop) {
                    index = 0;
                } else if (scrolltolast) {
                    itr = DataChild2.entrySet().iterator();
                    while (itr.hasNext()) {
                        index++;
                        entry = itr.next();
                        key = entry.getKey();
                        list = entry.getValue();
                        headerIndex2 = DataHeader.indexOf(key);
                        if (!grpIsCollapsed(adapter, headerIndex2)) {
                            index += (list != null && list.size() > 0) ? list.size() : 0;
                        }
                    }
                } else {
                    itr = DataChild2.entrySet().iterator();
                    while (itr.hasNext()) {
                        entry = itr.next();
                        key = entry.getKey();
                        list = entry.getValue();
                        headerIndex2 = DataHeader.indexOf(key);
                        if (headerIndex2 < headerIndex) {
                            index++;
                            if (!grpIsCollapsed(adapter, headerIndex2)) {
                                index += (list != null && list.size() > 0) ? list.size() : 0;
                            }
                        } else if (headerIndex2 == headerIndex) {
                            index++;
                        }
                    }
                }
            }

            if (found) {
                expListView.setSelection(index);
                txtInput.setText("");
            } else {
                playTone();
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_jobnotfound));
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CODupdate2 scrolltojob. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private boolean grpIsCollapsed(DOCOD_ListAdapter ad, int groupPos){
        boolean asd = false;
        try {
            if (groupPos <= ad.getGroupCount() -1 && groupPos >= 0) {
                asd = !(expListView.isGroupExpanded(groupPos));
            }
        } catch (Exception e) {
            String s =  JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CODupdate2 grpIsCollapsed. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    private Dialog showDialogNfocusTxtbox(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                txtInput.setText("");
                txtInput.requestFocus();
                dialog.dismiss();

            }
        });

        return builder.show();
    }

    private void stopPlayingTone(){
        try {
            if (player != null) {
                player.reset();
                //player.prepare();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity stopPlayingTone. Exception: " + e.getMessage(),"","","","");
        }
    }

    private void playTone() {
        /*player = MediaPlayer.create(JobsListGroupActivity.this, R.raw.error);
        player.start();*/
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity,  Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error));
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity playTone. Exception: " + e.getMessage(),"","","","");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at CODupdate2","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CODupdate2 onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}