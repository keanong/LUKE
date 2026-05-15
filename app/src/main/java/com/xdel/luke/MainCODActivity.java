package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.MainCOD_PostData;
import com.xdel.luke.objClasses.MainCOD_PostItemAdapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCODjobInfo;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainCODActivity extends AppCompatActivity {
    Context context;
    ConnectionDetector cd;
    private ProgressDialog pd;
    private String regid;
    private ListView listView;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];

    String langType = "en";
    private ArrayList<objCODjobInfo> CODJobInfoAR;
    NfcAdapter mNfcAdapter;
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        GcmPreferences = getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
        myApp.currentActivity = this;
        /*String languageToLoad  = langType; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/

        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_main_cod);

        listView = (ListView) findViewById(R.id.listView);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            GcmPreferences = getGcmPreferences();
            UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;

            Gson gson = new Gson();
            context = myApp.getAppContext();

            JobsHandler.clearJobsRetrievalActivityPrefs(GcmPreferences);
            JobsHandler.clearlAllDetailPagesStatic();
            JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "", "", true);
            //setTitle(myApp.getAppContext().getString(R.string.title_activity_main_cod));

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
                objGlobalMethods.writeToFile("MainCODActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

            lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

            try {
                if (mNfcAdapter != null && myApp.isNfcEnabled(mNfcAdapter, context)) {
                    IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                    IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
                    IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
                    IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};
                    PendingIntent nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                    mNfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilter, null);
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                //objGlobalMethods.writeToFile("Exception caught at MainCODActivity onResume mNfcAdapter portion. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
            }

            DashboardSubActivity.frompage = null;
            JobsHandler.checkForceLogOut();

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];

            if (username.equals("") && password.equals("")) {
                BackToLoginPage2();
            }

            regid = getRegistrationId();

            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);
            if (!cd.isConnectingToInternet()) {
                //// Internet Connection is not present
                Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
            }

            objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");

            String JsonStr;
            JsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "custXDelTotalCashPrefStr");
            if (!JsonStr.equals("")) {
                CODJobInfoAR = gson.fromJson(JsonStr, new TypeToken<List<objCODjobInfo>>() {
                }.getType());
                if (CODJobInfoAR != null && CODJobInfoAR.size() > 0) {
                    loadCODList2();
                } else {
                    showErrorAlert();
                }
            } else {
                showErrorAlert();
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
            objGlobalMethods.writeToFile("Exception at MainCODActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity = null;
        try {
            if (mNfcAdapter != null)
                mNfcAdapter.disableForegroundDispatch(this);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MainCODActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at MainCODActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void loadCODList2() {
        MainCOD_PostData postdata = null;
        MainCOD_PostData Firstpostdata = null;
        objJobInfo ji;
        objCODjobInfo CODjobInfo;
        String jiPrefStr = "";
        ArrayList<objCODjobInfo> XDelCashJobs = null;
        ArrayList<objCODjobInfo> CustCashJobs = null;
        Gson gson = new Gson();
        ArrayList<MainCOD_PostData> MainCODPostDataArr1 = new ArrayList<MainCOD_PostData>();
        ArrayList<MainCOD_PostData> MainCODPostDataArr2 = new ArrayList<MainCOD_PostData>();
        ArrayList<MainCOD_PostData> MainCODPostDataArrMain = new ArrayList<MainCOD_PostData>();
        Double totalCOD = 0.0;
        Double totalXdelCOD = 0.0;
        Double totalCustCOD = 0.0;
        DecimalFormat dec = new DecimalFormat("#.00");
        Double[] LATLNG = null;
        Double LAT, LNG = 0.0;
        int ct = -1;
        try {
            for (int i = 0; i <= CODJobInfoAR.size() - 1; i++) {
                ji = null;
                CODjobInfo = CODJobInfoAR.get(i);
                if (CODjobInfo.InvoiceIDX > 0) {
                    //Cust cash
                    if (CustCashJobs == null) {
                        CustCashJobs = new ArrayList<objCODjobInfo>();
                        CustCashJobs.add(CODjobInfo);
                    } else {
                        CustCashJobs.add(CODjobInfo);
                    }
                } else {
                    //XDel cash
                    if (XDelCashJobs == null) {
                        XDelCashJobs = new ArrayList<objCODjobInfo>();
                        XDelCashJobs.add(CODjobInfo);
                    } else {
                        XDelCashJobs.add(CODjobInfo);
                    }
                }
            }


            if (XDelCashJobs != null && XDelCashJobs.size() > 0) {
                for (int i = 0; i <= XDelCashJobs.size() - 1; i++) {
                    ji = null;
                    jiPrefStr = "";

                    ji = XDelCashJobs.get(i).JobInfo;
                    jiPrefStr = gson.toJson(ji);
                    LATLNG = null;
                    LATLNG = objGlobalMethods.getJobLATLNG(ji, XDelCashJobs.get(i).catType);

                    if (LATLNG != null) {
                        LAT = LATLNG[0];
                        LNG = LATLNG[1];
                    } else {
                        LAT = 0.0;
                        LNG = 0.0;
                    }


                    postdata = new MainCOD_PostData(false, true, true, true, XDelCashJobs.get(i).ConsignmentNote, "", XDelCashJobs.get(i).CollectionStatus,
                            XDelCashJobs.get(i).Amount, XDelCashJobs.get(i).imgmaincodStatusImgSrc, jiPrefStr, true,
                            LAT, LNG, XDelCashJobs.get(i).catType, XDelCashJobs.get(i).categoryTitle, XDelCashJobs.get(i).CODcollectionAtCatType);
                    MainCODPostDataArr1.add(postdata);
                    totalCOD += XDelCashJobs.get(i).Amount;
                    totalXdelCOD += XDelCashJobs.get(i).Amount;
                }

                Collections.sort(MainCODPostDataArr1);

                postdata = new MainCOD_PostData(true, false, false, false, myApp.getAppContext().getString(R.string.XDelCashCheque),
                        "$" + String.valueOf(dec.format(totalXdelCOD)), 0, 0.0, 0, "", false, 0.0, 0.0, -1, "", -1);
                MainCODPostDataArr1.add(0, postdata);
            }


            if (CustCashJobs != null && CustCashJobs.size() > 0) {
                for (int i = 0; i <= CustCashJobs.size() - 1; i++) {
                    ji = null;
                    jiPrefStr = "";

                    ji = CustCashJobs.get(i).JobInfo;
                    jiPrefStr = gson.toJson(ji);

                    LATLNG = null;
                    LATLNG = objGlobalMethods.getJobLATLNG(ji, CustCashJobs.get(i).catType);

                    if (LATLNG != null) {
                        LAT = LATLNG[0];
                        LNG = LATLNG[1];
                    } else {
                        LAT = 0.0;
                        LNG = 0.0;
                    }


                    postdata = new MainCOD_PostData(true, true, true, true, CustCashJobs.get(i).ConsignmentNote, CustCashJobs.get(i).DOInvoice, CustCashJobs.get(i).CollectionStatus,
                            CustCashJobs.get(i).Amount, CustCashJobs.get(i).imgmaincodStatusImgSrc, jiPrefStr, true,
                            LAT, LNG, CustCashJobs.get(i).catType, CustCashJobs.get(i).categoryTitle, CustCashJobs.get(i).CODcollectionAtCatType);
                    MainCODPostDataArr2.add(postdata);
                    totalCOD += CustCashJobs.get(i).Amount;
                    totalCustCOD += CustCashJobs.get(i).Amount;
                }

                Collections.sort(MainCODPostDataArr2);

                postdata = new MainCOD_PostData(true, false, false, false, myApp.getAppContext().getString(R.string.CUSTCashCheque),
                        "$" + String.valueOf(dec.format(totalCustCOD)), 0, 0.0, 0, "", false, 0.0, 0.0, -1, "", -1);
                MainCODPostDataArr2.add(0, postdata);
            }

            if (MainCODPostDataArr1.size() > 0) {
                for (int i = 0; i <= MainCODPostDataArr1.size() - 1; i++) {
                    MainCODPostDataArrMain.add(MainCODPostDataArr1.get(i));
                }
            }

            if (MainCODPostDataArr2.size() > 0) {
                for (int i = 0; i <= MainCODPostDataArr2.size() - 1; i++) {
                    MainCODPostDataArrMain.add(MainCODPostDataArr2.get(i));
                }
            }

            if (MainCODPostDataArrMain.size() > 0) {
                MainCOD_PostItemAdapter adapter = new MainCOD_PostItemAdapter(this, MainCODPostDataArrMain);
                listView.setAdapter(adapter);

                setTitle(myApp.getAppContext().getString(R.string.title_activity_main_cod) + " - " + "$" + String.valueOf(dec.format(totalCOD)));

                objGlobalMethods.checkToPromptNotiForBattCharging();
            } else {
                showErrorAlert();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at MainCODActivity loadCODList. Exception: " + e.toString(), "", "", "", "");
            showErrorAlert();
        }
    }

    private void showMobileNetworkSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_mobiledatamode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    /*Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                    startActivity(intent);*/

                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                } catch (ActivityNotFoundException e) {
                    Date dt = new Date();
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainCODActivity showMobileNetworkSettingDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeAirplaneModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainCODActivity.this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_airplanemode));

        //builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at MainCODActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainCODActivity.this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmode));


            //builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        Date dt = new Date();
                        objGlobalMethods.writeToFile("ActivityNotFoundException at MainCODActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                    }

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exeption at MainCODActivity showChangeGPSModeSettingsDialog. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainCODActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainCODActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
                GcmPreferences = getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                Intent i;
                //i = new Intent(MainCODActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(MainCODActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        //Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        MainActivity.uponLogin = 0;
        //Dashboard2Activity.DashboardOpenFromPush = false;
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    private SharedPreferences getGcmPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private SharedPreferences getUpdateJobInfoPreferences() {
        return getSharedPreferences("UpdateJobInfoPrefs",
                Context.MODE_PRIVATE);
    }

    private String getRegistrationId() {
        final SharedPreferences prefs = getGcmPreferences();
        String registrationId = prefs.getString(objGlobalVariables.PROPERTY_REG_ID, "");
        //String registrationId = "";
        if (registrationId.isEmpty()) {

            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(objGlobalVariables.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(myApp.getAppContext());
        if (registeredVersion != currentVersion) {
            //Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_cod, menu);
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

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(MainCODActivity.this, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    public void BackToLoginPage() {
        GcmPreferences = getGcmPreferences();
        UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        Intent parentIntent;
        parentIntent = new Intent(MainCODActivity.this, MainActivity.class);
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

    public Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    public Dialog showChargeBattDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                Boolean asd = false;
                SharedPreferences gcmp = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                objGlobalMethods.setGCMPrefs(gcmp, "showBattChargeNotiPrefStr", asd.toString());
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // TODO: handle Intent
        /*Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if(tag != null) {
            new NdefReaderTask().execute(tag);
        }*/

        try {
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            String action = intent.getAction();
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {


            } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {


            } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                return;
            }

        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MainCODActivity handleIntent. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
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
                    objGlobalMethods.writeToFile("Bluetooth is on at MainCODActivity", "", "", "", "");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MainCODActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

}
