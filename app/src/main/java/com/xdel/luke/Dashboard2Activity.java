package com.xdel.luke;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;


public class Dashboard2Activity extends AppCompatActivity {
    private final Semaphore mutex = new Semaphore(1);
    private Context context;
    ConnectionDetector cd;
    private MenuItem menuItem;
    private String LoginName = "";
    private String LoginPassword = "";
    private String regid;
    private String cHashstr;
    private int catType;
    private int categoryType = -1;
    private String categoryTitle = "";
    private ProgressDialog pd, pdCheck, pdCOD, pdRouting;
    static boolean errored = false;
    /*private String[] val = new String[]{"NEW PICK UP","PICKED UP","NEW DELIVERY","DELIVERED","FAILED PICKUP","FAILED DELIVERY","BULK PICK UP","JOB SHEET","CHECK STATUS","CREDIT CARD", "MESSAGING", "MAP",
            "CAMERA", "GET STATUS CODES"};*/
    /*private String[] val = new String[]{
            myApp.getAppContext().getString(R.string.item_dashboard2PIP),
            myApp.getAppContext().getString(R.string.item_dashboard2PU),
            myApp.getAppContext().getString(R.string.item_dashboard2DIP),
            myApp.getAppContext().getString(R.string.item_dashboard2OKP),
            myApp.getAppContext().getString(R.string.item_dashboard2FP),
            myApp.getAppContext().getString(R.string.item_dashboard2FD),
            myApp.getAppContext().getString(R.string.item_dashboard2BPU),
            myApp.getAppContext().getString(R.string.item_dashboard2JOBSHEET),
            myApp.getAppContext().getString(R.string.item_dashboard2STATUS),
            myApp.getAppContext().getString(R.string.item_dashboard2CC),
            myApp.getAppContext().getString(R.string.item_dashboard2MSG),
            "MAP",
            "CAMERA",
            "GET STATUS CODES"};*/
    private String[] val;
    public static ArrayList DriverJobsArrayList;
    private AlertDialog ad;
    public static Handler handler = new Handler();
    private ListView listView;
    private boolean changeCountColor = false;
    public static boolean DashboardOpenFromPush = false;
    private static boolean isRefresh;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    private int queCount = 0;
    private boolean DeAuthDriverResult;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    public static boolean updateJobInfoStatusTrueFalse = true;
    private String[] JobsCategoryHash;
    public static boolean JobsCategoryHasherrored = false;
    public static boolean GetDriverJobsALerrored = false;
    public static boolean alarmStarted = false;
    public static boolean alarmStarted2 = false;
    static PendingIntent pendingIntent, pendingIntent2;
    static AlarmManager manager, manager2;
    private File attachment;
    public static EditText txtDashboard2Input;
    private MediaPlayer player;
    static String cameraScannedCode;
    private Boolean refreshQueBooleanString = false;
    public static ProgressDialog pdLogout;
    private static ProgressDialog pdSearch;
    private static LinearLayout Dashboard2UnreadMsgLinearLayout;
    private static LinearLayout Dashboard2ListviewLinearLayout;
    private static PendingIntent GPSpendingIntent;
    private static AlarmManager GPSmanager;
    String langType = "en";
    private static String showRouteList = "";
    private static Double totalCOD;
    private static Boolean isDunning = false;
    GoogleCloudMessaging gcm;
    String SENDER_ID = "17277152085";

    private void initDashboardMenu(SharedPreferences gcmp) {
        showRouteList = objGlobalMethods.getGCMPrefs(gcmp, "showRouteList");
        showRouteList = "true";
        objGlobalMethods.setGCMPrefs(gcmp, "showRouteList", "true");
        if (!showRouteList.equals("")) {
            val = new String[]{
                    myApp.getAppContext().getString(R.string.title_activity_allMap),
                    myApp.getAppContext().getString(R.string.item_routing),
                    myApp.getAppContext().getString(R.string.item_dashboard2PIP),
                    myApp.getAppContext().getString(R.string.item_dashboard2PU),
                    myApp.getAppContext().getString(R.string.item_dashboard2DIP),
                    myApp.getAppContext().getString(R.string.item_dashboard2OKP),
                    myApp.getAppContext().getString(R.string.item_dashboard2FP),
                    myApp.getAppContext().getString(R.string.item_dashboard2FD),
                    myApp.getAppContext().getString(R.string.item_dashboard2BPU),
                    myApp.getAppContext().getString(R.string.item_dashboard2JOBSHEET),
                    myApp.getAppContext().getString(R.string.item_dashboard2STATUS),
                    myApp.getAppContext().getString(R.string.item_dashboard2CC),
                    myApp.getAppContext().getString(R.string.item_dashboard2MSG),
                    myApp.getAppContext().getString(R.string.item_dashboard2COD),
                    "CAMERA",
                    "GET STATUS CODES"};
        } else {
            val = new String[]{
                    myApp.getAppContext().getString(R.string.item_dashboard2PIP),
                    myApp.getAppContext().getString(R.string.item_dashboard2PU),
                    myApp.getAppContext().getString(R.string.item_dashboard2DIP),
                    myApp.getAppContext().getString(R.string.item_dashboard2OKP),
                    myApp.getAppContext().getString(R.string.item_dashboard2FP),
                    myApp.getAppContext().getString(R.string.item_dashboard2FD),
                    myApp.getAppContext().getString(R.string.item_dashboard2BPU),
                    myApp.getAppContext().getString(R.string.item_dashboard2JOBSHEET),
                    myApp.getAppContext().getString(R.string.item_dashboard2STATUS),
                    myApp.getAppContext().getString(R.string.item_dashboard2CC),
                    myApp.getAppContext().getString(R.string.item_dashboard2MSG),
                    myApp.getAppContext().getString(R.string.item_dashboard2COD),
                    "CAMERA",
                    "GET STATUS CODES"};
        }
    }

    private void changeActiveNotiLang() {
        String langChangeStr = "false";
        Boolean langChange = false;
        try {
            langChangeStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "langChangeStr");
            try {
                langChange = Boolean.parseBoolean(langChangeStr);
            } catch (Exception e) {
                langChange = false;
            }

            if (langChange) {
                langChange = false;
                langChangeStr = String.valueOf(langChange);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "langChangeStr", langChangeStr);
                FCMservice.mNotificationManager.cancel(-1);
                MainActivity.showActiveNotification("");
            } else {
                //do nothing
            }
        } catch (Exception e) {
            langChange = false;
            langChangeStr = String.valueOf(langChange);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "langChangeStr", langChangeStr);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            //FCMservice.currentActivity = this;
            myApp.currentActivity = this;
            JobsHandler.currentActivity = this;
        }
        super.onCreate(savedInstanceState);

        /*GcmPreferences = getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");

        String languageToLoad = langType; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_dashboard2);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));

        pdSearch = new ProgressDialog(this);
        pdSearch.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdSearch.setCanceledOnTouchOutside(false);
        pdSearch.setMessage(myApp.getAppContext().getString(R.string.pd_waitSearch));

        listView = (ListView) findViewById(R.id.listView);
        pdCheck = new ProgressDialog(this);
        pdCheck.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdCheck.setCanceledOnTouchOutside(false);
        pdCheck.setMessage(myApp.getAppContext().getString(R.string.pd_waitCheck));

        pdCOD = new ProgressDialog(this);
        pdCOD.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdCOD.setCanceledOnTouchOutside(false);
        pdCOD.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfoCOD));

        pdRouting = new ProgressDialog(this);
        pdRouting.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdRouting.setCanceledOnTouchOutside(false);
        pdRouting.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveRouting));

        txtDashboard2Input = (EditText) findViewById(R.id.txtDashboard2Input);*/
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /*private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences();
        int appVersion = getAppVersion(context);
        //Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(objGlobalVariables.PROPERTY_REG_ID, regId);
        editor.putInt(objGlobalVariables.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    public void Dashboard2UnreadMsgtextClick(View view) {
        Intent parentIntent = new Intent(this, Messaginglist.class);
        startActivity(parentIntent);
        this.finish();
    }

    private class doSearch extends AsyncTask<Void, Void, objSearchedResult> {
        private final String searchValue;

        doSearch(String asearchValue) {
            this.searchValue = asearchValue;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pdSearch.show();
        }

        @Override
        protected objSearchedResult doInBackground(Void... params) {
            objSearchedResult rs = null;
            try {
                GcmPreferences = getGcmPreferences();
                rs = JobsHandler.getSearchedResult(GcmPreferences, searchValue, "", false);
            } catch (Exception e) {
                Date dt = new Date();
                objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity doSearch doInBackground. Exception: " + e.getMessage().toString(), "", "", "", "");
                rs = null;
            } finally {
                return rs;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final objSearchedResult success) {
            Intent intent = new Intent(Dashboard2Activity.this, JobInfoActivity.class);
            pdSearch.hide();
            try {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                if (success != null && success.count > 0) {
                    if (success.ActivityName.equals("JobInfoActivity")) {
                        if ((success.JobInfo.ClientIDX == 14753 || success.JobInfo.ClientIDX == 4868) && success.JobInfo.DeliveryContents.toUpperCase().equals("DUNNING DOCUMENT")) {
                            long JobsIDX = success.JobInfo.JobsIDX;
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX, String.valueOf(JobsIDX));
                            String cn = success.JobInfo.JobNo;
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "currJobInfoCN", String.valueOf(cn));
                            intent = new Intent(Dashboard2Activity.this, setDunningDeliveredStatusActivity.class);
                            JobInfoActivity.accessFromDirection = 0;
                        } else {
                            intent = new Intent(Dashboard2Activity.this, JobInfoActivity.class);
                            JobInfoActivity.accessFromDirection = 1;
                        }
                        startActivity(intent);
                    } else {
                        GcmPreferences = getGcmPreferences();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "searchedval", searchValue);
                        //intent = new Intent(Dashboard2Activity.this, JobsListGroupActivity.class);
                        intent = new Intent(Dashboard2Activity.this, JobsListGroupActivity.class);
                        JobInfoActivity.accessFromDirection = 0;
                        startActivity(intent);
                    }
                } else {
                    playTone();
                    showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
                }
            } catch (Exception e) {
                Date dt = new Date();
                objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity doSearch onPostExecute. Exception: " + e.getMessage().toString(), "", "", "", "");
                playTone();
                showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
            }
        }
    }

    private class doSearchViaBarcode extends AsyncTask<Void, Void, objSearchedResult> {
        private final String searchValue;

        doSearchViaBarcode(String asearchValue) {
            this.searchValue = asearchValue;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pdSearch.show();
        }

        @Override
        protected objSearchedResult doInBackground(Void... params) {
            objSearchedResult rs = null;
            objCheckJobTagResult objCheckJobTagResultobj = null;
            try {
                objCheckJobTagResultobj = WebService.getSearchedJobsIDXviaBarcodeTag(regid, searchValue);
                if (objCheckJobTagResultobj != null && objCheckJobTagResultobj.JobsIDX != null && objCheckJobTagResultobj.JobsIDX > 0) {
                    GcmPreferences = getGcmPreferences();
                    rs = JobsHandler.getSearchedResult(GcmPreferences, String.valueOf(objCheckJobTagResultobj.JobsIDX), "", true);
                } else if (objCheckJobTagResultobj == null) {
                    rs = null;
                }
            } catch (Exception e) {
                Date dt = new Date();
                objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity doSearch doInBackground. Exception: " + e.getMessage().toString(), "", "", "", "");
                rs = null;
            } finally {
                return rs;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final objSearchedResult success) {
            pdSearch.hide();
            try {
                if (success != null && success.count > 0) {
                    Intent intent;
                    intent = new Intent(Dashboard2Activity.this, JobInfoActivity.class);
                    JobInfoActivity.accessFromDirection = 1;
                    startActivity(intent);
                } else {
                    playTone();
                    showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
                }
            } catch (Exception e) {
                Date dt = new Date();
                objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity doSearch onPostExecute. Exception: " + e.getMessage().toString(), "", "", "", "");
                playTone();
                showDialog(myApp.getAppContext().getString(R.string.alert_errorlocatingjob));
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
        GcmPreferences = getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "dashboard");
        parentIntent = new Intent(Dashboard2Activity.this, QRBarcodeReader.class);
        handler.removeCallbacks(runnable);
        startActivity(parentIntent);
    }

    @Override
    protected void onPause() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            FCMservice.currentActivity = null;
            myApp.currentActivity = null;
            //handler.removeCallbacks(runnable);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FCMservice.currentActivity = null;
        pd.dismiss();
        pdCheck.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FCMservice.currentActivity = null;
        pd.dismiss();
        pdCheck.dismiss();
    }

    //Start showDialog
    public Dialog showDialog(String msg) {
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
                        Date dt = new Date();
                        objGlobalMethods.writeToFile(dt, "ActivityNotFoundException at MainActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard2Activity.this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Date dt = new Date();
                    objGlobalMethods.writeToFile(dt, "ActivityNotFoundException at MainActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showMobileNetworkSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard2Activity.this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_mobiledatamode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                } catch (ActivityNotFoundException e) {
                    Date dt = new Date();
                    objGlobalMethods.writeToFile(dt, "ActivityNotFoundException at MainActivity showMobileNetworkSettingDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showErrorAlertAndLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("There was an error retrieving data. Click OK to log out and log in again.");

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                LogOut();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard2, menu);

        if (Boolean.parseBoolean(ishomesuser)) {
            menu.findItem(R.id.refresh).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent parentIntent;
        cd = new ConnectionDetector(context);
        switch (item.getItemId()) {
            *//*case R.id.test:
                Intent p = new Intent(this, OtpActivity.class);
                startActivity(p);
*//*
            case R.id.refresh:
                if (cd.isConnectingToInternet()) {
                    isRefresh = true;
                    *//*menuItem = item;
                    menuItem.setActionView(R.layout.progressbar);
                    menuItem.expandActionView();*//*
                    GcmPreferences = getGcmPreferences();
                    UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
                    //GetDriverJobsCount2 task = new GetDriverJobsCount2(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                    GetDriverJobsCount task = new GetDriverJobsCount(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                    task.execute();
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_cannotRefreshNoInternet));
                }
                break;
            case R.id.qrscan:
                launchQRScan();
                break;
            case R.id.logout:
                //internet connection check is inside LogOut function
                LogOut();
                break;
            case R.id.settings:
                showDialog(item.getTitle().toString());
                break;
            case R.id.about:
                parentIntent = new Intent(this, AboutActivity.class);
                MainActivity.uponLogin = 0;
                Dashboard2Activity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
                break;
            case R.id.dashboard2Language:
                GcmPreferences = getGcmPreferences();
                langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
                if (langType.equals("en") || langType.equals("")) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "zh");
                } else if (langType.equals("zh")) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "en");
                }

                Boolean langChange = true;
                String langChangeStr = String.valueOf(langChange);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "langChangeStr", langChangeStr);

                Intent intent = getIntent();
                finish();
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //internet connection check is inside LogOut function
        LogOut();
    }

    public void refresh() {
        try {
            GcmPreferences = getGcmPreferences();
            if (cd.isConnectingToInternet()) {
                isRefresh = true;
                UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
                //GetDriverJobsCount2 task = new GetDriverJobsCount2(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                GetDriverJobsCount task = new GetDriverJobsCount(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                task.execute();
            } else {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "refreshQueBooleanString", "true");
            }
        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2 refresh. " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void LogOut() {
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            showDialog(myApp.getAppContext().getString(R.string.alert_cannotLogoutNoInternet));
            // stop executing code by return
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmlogout));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if user pressed "yes",
                // 1. check that there is no more ques in the backend, if not, have to wait for all the ques to be cleared b4,
                // 2. then send back all the plotted locations,
                // 3. then he is allowed to exit from application

                pdCheck.show();
                dialog.dismiss();
                GcmPreferences = getGcmPreferences();
                UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];

                stopRunnable();
                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                Date dt = new Date();
                queCount = JobsHandler.returnNumberOfQueToSend(UpdateJobInfoPreferences);

                if (queCount > 0) {
                    //unable to logout due there are still ques
                    pdCheck.hide();
                    objGlobalMethods.writeToFile(dt, "Unable to log out. There are still data waiting to be sent out. Please try again later.", "", "", "", "");
                    showDialog(myApp.getAppContext().getString(R.string.alert_cannotlogoutsendingdata));
                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                    return;
                } else if (queCount == 0) {
                    //no que, send back all plotted locations
                    //then logout
                    if (username.length() != 0 && username != "" && password.length() != 0 && password != "") {
                        LoginName = username;
                        LoginPassword = password;
                    } else {
                        pdCheck.hide();
                        objGlobalMethods.writeToFile(dt, "Unable to log out. Please close the app.", "", "", "", "");
                        showDialog(myApp.getAppContext().getString(R.string.alert_cannotlogoutcloseapp));
                        return;
                    }

                    if (Boolean.parseBoolean(ishomesuser)) {
                        objGlobalMethods.writeToFile(dt, "Logging out HOMES USER.", "", "", "", "");
                        LogOutHomesWSCall();
                    } else {
                        objGlobalMethods.writeToFile(dt, "Logging out.", "", "", "", "");
                        checkForAttachmentToEmailOut task = new checkForAttachmentToEmailOut();
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
    }

    private class checkForAttachmentToEmailOut extends AsyncTask<Void, Void, File> {
        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
        }

        @Override
        protected File doInBackground(Void... params) {

            try {
                Date dt = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
                String Filenamedtstr = "";
                Filenamedtstr = formatter.format(dt);
                File externalStorageDir = Environment.getExternalStorageDirectory();
                attachment = new File(externalStorageDir, Filenamedtstr + "_log.txt");
            } catch (Exception e) {
                attachment = null;
            }

            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final File success) {
            String attachmentfilename = "";
            GcmPreferences = getGcmPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            try {
                if (attachment.exists()) {
                    attachmentfilename = attachment.getAbsolutePath().toString();
                    if (!attachmentfilename.equals("")) {
                        Mail m = new Mail("luke@xdel.com", "XDel2015");
                        objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, attachmentfilename, "", "Luke report from " + username + ".", attachment);
                        sendmail.execute();
                    }
                }
            } catch (Exception e) {

            }

            LogOutWSCall task = new LogOutWSCall(username, password);
            task.execute();
        }
    }

    private class LogOutWSCall extends AsyncTask<Void, Void, Boolean> {
        private final String LoginName;
        private final String Password;

        LogOutWSCall(String aLoginName, String aPassword) {
            LoginName = aLoginName;
            Password = aPassword;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            DeAuthDriverResult = WebService.UnAuthDriver(regid, timestampstr, 0, 0);

            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean success) {
            try {
                pdCheck.hide();
                GcmPreferences = getGcmPreferences();
                UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                useridx = UsernamePwd[2];
                if (!errored) {
                    //no error
                    if (!DeAuthDriverResult) {
                        //if result return false, cannot logout
                        Intent parentIntent;
                        parentIntent = new Intent(Dashboard2Activity.this, MainActivity.class);

                        LocationService.stopLocationUpdates();
                        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
                        JobsHandler.clearGPSQue(UpdateJobInfoPreferences);
                        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
                        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
                        //stopAlarm();
                        //stopPerMinuteAlarm();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "prevLoginUserIDX", useridx);
                        objGlobalMethods.stopGpsAlarm();
                        objGlobalMethods.stopPerMinRunAlarm();
                        objGlobalMethods.stopAlarmForCommon();
                        cancelGPSalarm();
                        FCMservice.mNotificationManager.cancel(-1);
                        startActivity(parentIntent);
                        finish();
                    } else {
                        //if result return true, do nothing
                        //clear  LoginName,Password inside prefs, finish this activity and launch MainActivity
                        Intent parentIntent;
                        parentIntent = new Intent(Dashboard2Activity.this, MainActivity.class);

                        LocationService.stopLocationUpdates();
                        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
                        JobsHandler.clearGPSQue(UpdateJobInfoPreferences);
                        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
                        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
                        //stopAlarm();
                        //stopPerMinuteAlarm();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "prevLoginUserIDX", useridx);
                        objGlobalMethods.stopGpsAlarm();
                        objGlobalMethods.stopPerMinRunAlarm();
                        objGlobalMethods.stopAlarmForCommon();
                        cancelGPSalarm();
                        FCMservice.mNotificationManager.cancel(-1);
                        startActivity(parentIntent);
                        finish();
                    }
                } else {
                    //if error
                    //showDialog("error, result is " + DeAuthDriverResult);
                    Intent parentIntent;
                    parentIntent = new Intent(Dashboard2Activity.this, MainActivity.class);

                    LocationService.stopLocationUpdates();
                    JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
                    JobsHandler.clearGPSQue(UpdateJobInfoPreferences);
                    JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
                    JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
                    //stopAlarm();
                    //stopPerMinuteAlarm();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "prevLoginUserIDX", useridx);
                    objGlobalMethods.stopGpsAlarm();
                    objGlobalMethods.stopPerMinRunAlarm();
                    objGlobalMethods.stopAlarmForCommon();
                    cancelGPSalarm();
                    FCMservice.mNotificationManager.cancel(-1);
                    startActivity(parentIntent);
                    finish();
                }
            } catch (Exception e) {
                Date dt = new Date();
                objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity LogOutWSCall onPostExecute. Exception: " + e.toString(), "", "", "", "");
            }
            errored = false;
        }
    }

    private void LogOutHomesWSCall() {

        Intent parentIntent;
        parentIntent = new Intent(Dashboard2Activity.this, MainActivity.class);
        GcmPreferences = getGcmPreferences();
        UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        JobsHandler.clearGPSQue(UpdateJobInfoPreferences);
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
        objGlobalMethods.stopAlarmForCommon();
        //stopAlarm();
        //stopPerMinuteAlarm();
        startActivity(parentIntent);
        finish();
    }

    public void BackToLoginPage() {
        GcmPreferences = getGcmPreferences();
        UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        JobsHandler.clearGPSQue(UpdateJobInfoPreferences);
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
        objGlobalMethods.stopAlarmForCommon();
        //stopAlarm();
        //stopPerMinuteAlarm();
        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(1);
        }
        Intent parentIntent;
        parentIntent = new Intent(Dashboard2Activity.this, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    public void setCategoryHashes(Context context, String valName, String val, String RetrieveTypeNameStr, String JobIDXsJsonStr,
                                  String JobInfoJsonPrefName, String JobInfoJsonPrefJsonStr, String JobsCountName, String JobCount) {

        GcmPreferences = getGcmPreferences();
        JobsHandler.setCategoryHashesForDashboard(GcmPreferences, valName, val, RetrieveTypeNameStr, JobIDXsJsonStr, JobInfoJsonPrefName, JobInfoJsonPrefJsonStr, JobsCountName, String.valueOf(JobCount));
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
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getSystemJobHashesPreferences(Context context) {
        // This sample app persists the Login username in shared preferences, but
        // how you store the username in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private ArrayList getSystemJobsCountAL() {
        final SharedPreferences prefs = getGcmPreferences();
        ArrayList al = new ArrayList();
        al.add(prefs.getString(objGlobalVariables.PIPjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.PUjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.DIPjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.DELjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.FPjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.FDjobsCountName, ""));
        al.add(prefs.getString(objGlobalVariables.JobsheetCountName, ""));
        return al;
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

    private void loadListFirst() {
        try {
            GcmPreferences = getGcmPreferences();
            ArrayList systemJobsCountAL = getSystemJobsCountAL();
            ArrayList<Dashboard_PostData> dashboardPostDataArr = new ArrayList<Dashboard_PostData>();
            Dashboard_PostData postdata;
            DecimalFormat dec = new DecimalFormat("#.00");

            for (int i = 0; i <= val.length - 1; i++) {
                if (i <= 5) {
                    if (i == 0) {
                        if (!Boolean.parseBoolean(ishomesuser)) {
                            if (DashboardOpenFromPush) {
                                postdata = new Dashboard_PostData(val[i], (String) systemJobsCountAL.get(i), true, true);
                                dashboardPostDataArr.add(postdata);
                            } else {
                                postdata = new Dashboard_PostData(val[i], (String) systemJobsCountAL.get(i), false, true);
                                dashboardPostDataArr.add(postdata);
                            }
                        } else {
                            postdata = new Dashboard_PostData(val[i], "0", false, true);
                            dashboardPostDataArr.add(postdata);
                        }
                    } else {
                        if (!Boolean.parseBoolean(ishomesuser)) {
                            postdata = new Dashboard_PostData(val[i], (String) systemJobsCountAL.get(i), false, true);
                            dashboardPostDataArr.add(postdata);
                        } else {
                            postdata = new Dashboard_PostData(val[i], "0", false, true);
                            dashboardPostDataArr.add(postdata);
                        }
                    }
                } else if (i == 6) {
                    //show Bulk Pick UP
                    *//*if (!Boolean.parseBoolean(ishomesuser)){
                        postdata  = new Dashboard_PostData(val[i], String.valueOf(""),false,false);
                        dashboardPostDataArr.add(postdata);
                    }*//*
                    postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                    dashboardPostDataArr.add(postdata);
                } else if (i == 7) {
                    //show JobSheet
                    //postdata  = new Dashboard_PostData(val[i], String.valueOf(""),false,false);
                    postdata = new Dashboard_PostData(val[i], (String) systemJobsCountAL.get(6), false, true);
                    dashboardPostDataArr.add(postdata);
                } else if (i == 8) {
                    //show check status
                    postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                    dashboardPostDataArr.add(postdata);
                } else if (i == 9) {
                    //show credit card
                    if (!Boolean.parseBoolean(ishomesuser)) {
                        postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                        dashboardPostDataArr.add(postdata);
                    }
                } else if (i == 10) {
                    //show messaging
                    if (!Boolean.parseBoolean(ishomesuser)) {
                        postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                        dashboardPostDataArr.add(postdata);
                    }
                } else if (i == 11) {
                    //show COD
                    if (!Boolean.parseBoolean(ishomesuser)) {
                        Double codval = 0.0;
                        String codvalPrefStr = "";
                        codvalPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalCOD");
                        try {
                            codval = Double.parseDouble(codvalPrefStr);
                        } catch (Exception e) {
                            Date dt = new Date();
                            objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity loadListFirst totalCODPrefStr conversion", "", "", "", "");
                            codval = 0.0;
                        }
                        if (codval == 0.0) {
                            postdata = new Dashboard_PostData(val[i] + "  " + "$0.00", "0", false, false);
                            dashboardPostDataArr.add(postdata);
                        } else if (codval > 0.0) {
                            postdata = new Dashboard_PostData(val[i] + "  " + "$" + String.valueOf(dec.format(codval)), "1", false, false);
                            dashboardPostDataArr.add(postdata);
                        }
                    }
                } else {
                    *//*postdata  = new Dashboard_PostData(val[i], String.valueOf(""),false);
                    dashboardPostDataArr.add(postdata);*//*
                }
            }

            Dashboard_PostItemAdapter adapter = new Dashboard_PostItemAdapter(this, dashboardPostDataArr);

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i;
                    view.clearFocus();
                    View v;
                    TextView lblCount;
                    GcmPreferences = getGcmPreferences();
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                    username = UsernamePwd[0];
                    password = UsernamePwd[1];
                    useridx = UsernamePwd[2];
                    ishomesuser = UsernamePwd[3];

                    switch (position) {
                        case 0:
                            categoryType = 0;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2PIP);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2PIP));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoPIPALjsonPrefName);
                            }

                            break;
                        case 1:
                            categoryType = 1;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2PU);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2PU));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "1");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoPUALjsonPrefName);
                            }

                            break;
                        case 2:
                            categoryType = 2;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2DIP);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2DIP));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "2");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoDIPALjsonPrefName);
                            }

                            break;
                        case 3:
                            categoryType = 3;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2OKP);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2OKP));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "3");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoDELALjsonPrefName);
                            }

                            break;
                        case 4:
                            categoryType = 4;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2FP);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2FP));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "4");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoFPALjsonPrefName);
                            }

                            break;
                        case 5:
                            categoryType = 5;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2FD);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2FD));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "5");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoFDALjsonPrefName);
                            }

                            break;
                        case 6:
                            //Bulk Pick Up
                            if (Boolean.parseBoolean(ishomesuser)) {
                                ////do nothing for HOMES users
                                //showDialog("Option not available for HOMES user.");
                                showDialog(myApp.getAppContext().getString(R.string.alert_optionNotAvail));
                                return;
                            } else {
                                useridx = "D" + useridx;
                                String SCANNEDCURRENTID = useridx.toUpperCase() + "|" + username.toUpperCase();
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "BulkPickUpScannedIDnUsername", SCANNEDCURRENTID);
                                i = new Intent(Dashboard2Activity.this, BulkPickUpActivity.class);
                            }

                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 7:
                            //Jobsheet
                            if (Boolean.parseBoolean(ishomesuser)) {
                                i = new Intent(Dashboard2Activity.this, JobsheetActivity.class);
                            } else {
                                useridx = "D" + useridx;
                                String SCANNEDCURRENTID = useridx.toUpperCase() + "|" + username.toUpperCase();
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedIDnUsername", SCANNEDCURRENTID);
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedUsername", username.toUpperCase());
                                i = new Intent(Dashboard2Activity.this, JobsheetSubmitActivity.class);
                            }

                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 8:
                            //Check Status
                            i = new Intent(Dashboard2Activity.this, CheckStatusActivity.class);
                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 9:
                            //Credit Card
                            i = new Intent(Dashboard2Activity.this, CreditCardActivity.class);
                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 10:
                            i = new Intent(Dashboard2Activity.this, Messaginglist.class);
                            handler.removeCallbacks(runnable);
                            List<ChatMsgSenders> chatmsgsendersAL = null;
                            chatmsgsendersAL = JobsHandler.getChatMsgSendersAL();
                            if (chatmsgsendersAL != null && chatmsgsendersAL.size() > 0) {
                                startActivity(i);
                            } else {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "unreadMsgExist", "false");
                                showDialog(myApp.getAppContext().getString(R.string.alert_nomsg));
                            }

                            break;
                        case 11:
                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                cd = new ConnectionDetector(context);
                                if (!cd.isConnectingToInternet()) {
                                    showDialog(myApp.getAppContext().getString(R.string.toast_workingconnection));
                                } else {
                                    lblCount = (TextView) v.findViewById(R.id.lblCount);
                                    startRedirectionToMainCODActivityPage(lblCount);
                                }
                            }

                            break;
                    }
                }
            });
        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity loadListFirst. Exception: " + e.toString(), "", "", "", "");
        } finally {

        }
    }

    private void loadListFirst2() {
        Date dt = new Date();
        try {
            GcmPreferences = getGcmPreferences();
            ArrayList systemJobsCountAL = getSystemJobsCountAL();
            ArrayList<Dashboard_PostData> dashboardPostDataArr = new ArrayList<Dashboard_PostData>();
            Dashboard_PostData postdata;
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            DecimalFormat dec = new DecimalFormat("#.00");

            for (int i = 0; i <= val.length - 1; i++) {
                if (i >= 2 && i <= 7) {
                    if (i == 2) {
                        if (!Boolean.parseBoolean(ishomesuser)) {
                            if (DashboardOpenFromPush) {
                                postdata = new Dashboard_PostData(val[i], (String) systemJobsCountAL.get(i - 2), true, true);
                                dashboardPostDataArr.add(postdata);
                            } else {
                                postdata = new Dashboard_PostData(val[i], (String) systemJobsCountAL.get(i - 2), false, true);
                                dashboardPostDataArr.add(postdata);
                            }
                        } else {
                            postdata = new Dashboard_PostData(val[i], "0", false, true);
                            dashboardPostDataArr.add(postdata);
                        }
                    } else {
                        if (!Boolean.parseBoolean(ishomesuser)) {
                            postdata = new Dashboard_PostData(val[i], (String) systemJobsCountAL.get(i - 2), false, true);
                            dashboardPostDataArr.add(postdata);
                        } else {
                            postdata = new Dashboard_PostData(val[i], "0", false, true);
                            dashboardPostDataArr.add(postdata);
                        }
                    }
                } else if (i == 0) {
                    //all maps
                    postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                    dashboardPostDataArr.add(postdata);
                } else if (i == 1) {
                    //routing
                    postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                    dashboardPostDataArr.add(postdata);
                } else if (i == 8) {
                    //show Bulk Pick UP
                    postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                    dashboardPostDataArr.add(postdata);
                }
                //else if ( i == 7){
                else if (i == 9) {
                    //show JobSheet
                    postdata = new Dashboard_PostData(val[i], (String) systemJobsCountAL.get(6), false, true);
                    dashboardPostDataArr.add(postdata);
                }
                //else if ( i == 8){
                else if (i == 10) {
                    //show check status
                    postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                    dashboardPostDataArr.add(postdata);
                }
                //else if ( i == 9){
                else if (i == 11) {
                    //show credit card
                    if (!Boolean.parseBoolean(ishomesuser)) {
                        postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                        dashboardPostDataArr.add(postdata);
                    }
                }
                //else if (i == 10){
                else if (i == 12) {
                    //show messaging
                    if (!Boolean.parseBoolean(ishomesuser)) {
                        postdata = new Dashboard_PostData(val[i], String.valueOf(""), false, false);
                        dashboardPostDataArr.add(postdata);
                    }
                }
                //else if (i == 11){
                else if (i == 13) {
                    //show COD
                    if (!Boolean.parseBoolean(ishomesuser)) {
                        Double codval = 0.0;
                        String codvalPrefStr = "";
                        codvalPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalCOD");
                        try {
                            codval = Double.parseDouble(codvalPrefStr);
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity loadListFirst2 totalCODPrefStr conversion", "", "", "", "");
                            codval = 0.0;
                        }
                        if (codval == 0.0) {
                            postdata = new Dashboard_PostData(val[i] + "  " + "$0.00", "0", false, false);
                            dashboardPostDataArr.add(postdata);
                        } else if (codval > 0.0) {
                            postdata = new Dashboard_PostData(val[i] + "  " + "$" + String.valueOf(dec.format(codval)), "1", false, false);
                            dashboardPostDataArr.add(postdata);
                        }
                    }
                }
                *//*else if (i == 12){
                    postdata  = new Dashboard_PostData(val[i], String.valueOf(""),false, false);
                    dashboardPostDataArr.add(postdata);
                }*//*
                else {
                    *//*postdata  = new Dashboard_PostData(val[i], String.valueOf(""),false);
                    dashboardPostDataArr.add(postdata);*//*
                }
            }

            Dashboard_PostItemAdapter adapter = new Dashboard_PostItemAdapter(this, dashboardPostDataArr);

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i;
                    view.clearFocus();
                    View v;
                    TextView lblCount;
                    //GcmPreferences = getGcmPreferences(context);
                    GcmPreferences = getGcmPreferences();
                    switch (position) {
                        case 0:
                            if (Boolean.parseBoolean(ishomesuser)) {

                            } else {
                                String RouteListJsonStr = getRouteListJsonStr(GcmPreferences);
                                if (!RouteListJsonStr.equals("")) {
                                    i = new Intent(Dashboard2Activity.this, MapsAllActivity.class);
                                    handler.removeCallbacks(runnable);
                                    startActivity(i);
                                } else {
                                    getRoutingList task = new getRoutingList(true);
                                    task.execute();
                                }
                            }

                            break;
                        case 1:
                            if (Boolean.parseBoolean(ishomesuser)) {

                            } else {
                                String RouteListJsonStr = getRouteListJsonStr(GcmPreferences);
                                if (!RouteListJsonStr.equals("")) {
                                    i = new Intent(Dashboard2Activity.this, AddressListRoutingActivity.class);
                                    handler.removeCallbacks(runnable);
                                    startActivity(i);
                                } else {
                                    getRoutingList task = new getRoutingList(false);
                                    task.execute();
                                }
                            }

                            break;
                        case 2:
                            categoryType = 0;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2PIP);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2PIP));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoPIPALjsonPrefName);
                            }

                            break;
                        case 3:
                            categoryType = 1;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2PU);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2PU));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "1");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoPUALjsonPrefName);
                            }

                            break;
                        case 4:
                            categoryType = 2;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2DIP);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2DIP));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "2");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoDIPALjsonPrefName);
                            }

                            break;
                        case 5:
                            categoryType = 3;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2OKP);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2OKP));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "3");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoDELALjsonPrefName);
                            }

                            break;
                        case 6:
                            categoryType = 4;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2FP);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2FP));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "4");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoFPALjsonPrefName);
                            }

                            break;
                        case 7:
                            categoryType = 5;
                            categoryTitle = myApp.getAppContext().getString(R.string.item_dashboard2FD);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, myApp.getAppContext().getString(R.string.item_dashboard2FD));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "5");

                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {
                                return;
                            } else {
                                lblCount = (TextView) v.findViewById(R.id.lblCount);
                                startRedirection(lblCount, objGlobalVariables.JobInfoFDALjsonPrefName);
                            }

                            break;
                        case 8:
                            //Bulk Pick Up
                            if (Boolean.parseBoolean(ishomesuser)) {
                                ////do nothing for HOMES users
                                showDialog(myApp.getAppContext().getString(R.string.alert_optionNotAvail));
                                return;
                            } else {
                                useridx = "D" + useridx;
                                String SCANNEDCURRENTID = useridx.toUpperCase() + "|" + username.toUpperCase();
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "BulkPickUpScannedIDnUsername", SCANNEDCURRENTID);
                                i = new Intent(Dashboard2Activity.this, BulkPickUpActivity.class);
                            }

                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 9:
                            //Jobsheet
                            if (Boolean.parseBoolean(ishomesuser)) {
                                i = new Intent(Dashboard2Activity.this, JobsheetActivity.class);
                            } else {
                                useridx = "D" + useridx;
                                String SCANNEDCURRENTID = useridx.toUpperCase() + "|" + username.toUpperCase();
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedIDnUsername", SCANNEDCURRENTID);
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "JobsheetScannedUsername", username.toUpperCase());
                                i = new Intent(Dashboard2Activity.this, JobsheetSubmitActivity.class);
                            }

                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 10:
                            //Check Status
                            i = new Intent(Dashboard2Activity.this, CheckStatusActivity.class);
                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 11:
                            //Credit Card
                            i = new Intent(Dashboard2Activity.this, CreditCardActivity.class);
                            handler.removeCallbacks(runnable);
                            startActivity(i);
                            break;
                        case 12:
                            i = new Intent(Dashboard2Activity.this, Messaginglist.class);
                            handler.removeCallbacks(runnable);
                            List<ChatMsgSenders> chatmsgsendersAL = null;
                            chatmsgsendersAL = JobsHandler.getChatMsgSendersAL();
                            if (chatmsgsendersAL != null && chatmsgsendersAL.size() > 0) {
                                startActivity(i);
                            } else {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "unreadMsgExist", "false");
                                showDialog(myApp.getAppContext().getString(R.string.alert_nomsg));
                            }

                            break;
                        case 13:
                            v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                            if (v == null) {

                                return;
                            } else {
                                cd = new ConnectionDetector(context);
                                if (!cd.isConnectingToInternet()) {
                                    showDialog(myApp.getAppContext().getString(R.string.toast_workingconnection));
                                } else {
                                    lblCount = (TextView) v.findViewById(R.id.lblCount);
                                    startRedirectionToMainCODActivityPage(lblCount);
                                }
                            }

                            break;

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    private void initRedirectionToJobInfo(String JobInfoALjsonPrefName) {
        Gson gson = new Gson();
        String JsonStr, JobInfoJsonStr;
        objJobInfo[] JobInfoAL;
        objJobInfo JobInfo;
        String postal = "";
        String address = "";
        String addressWithUnit = "";
        double LAT = 0;
        double LNG = 0;
        GcmPreferences = getGcmPreferences();
        Boolean hasCOD = false;

        try {
            JsonStr = GcmPreferences.getString(JobInfoALjsonPrefName, "");
            JobInfoAL = gson.fromJson(JsonStr, objJobInfo[].class);
            JobInfo = JobInfoAL[0];
            JobInfoJsonStr = gson.toJson(JobInfo);
            objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, JobInfoJsonStr);
            JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");

            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            if (catType == 0 || catType == 1 || catType == 4) {
                //pip/pu/fp
                if (JobInfo.PURedirectedLocation != null) {
                    postal = JobInfo.PURedirectedLocation.getPOSTALCODE();
                    LAT = JobInfo.PURedirectedLocation.LAT;
                    LNG = JobInfo.PURedirectedLocation.LNG;
                    address = objGlobalMethods.returnAddressWithoutPostalNUnit(JobInfo.PURedirectedLocation);
                } else if (JobInfo.PULocation != null) {
                    postal = JobInfo.PULocation.getPOSTALCODE();
                    LAT = JobInfo.PULocation.LAT;
                    LNG = JobInfo.PULocation.LNG;
                    address = objGlobalMethods.returnAddressWithoutPostalNUnit(JobInfo.PULocation);
                }

                objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", String.valueOf(LAT));
                objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", String.valueOf(LNG));
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", postal);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", address);
            } else if (catType == 2 || catType == 3 || catType == 5) {
                //dip/okp/fd
                if (JobInfo.DLRedirectedLocation != null) {
                    postal = JobInfo.DLRedirectedLocation.getPOSTALCODE();
                    LAT = JobInfo.DLRedirectedLocation.LAT;
                    LNG = JobInfo.DLRedirectedLocation.LNG;
                    address = objGlobalMethods.returnAddressWithoutPostalNUnit(JobInfo.DLRedirectedLocation);
                    //only for dunning
                    addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(JobInfo.DLRedirectedLocation);

                } else if (JobInfo.DLLocation != null) {
                    postal = JobInfo.DLLocation.getPOSTALCODE();
                    LAT = JobInfo.DLLocation.LAT;
                    LNG = JobInfo.DLLocation.LNG;
                    address = objGlobalMethods.returnAddressWithoutPostalNUnit(JobInfo.DLLocation);
                    //only for dunning
                    addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(JobInfo.DLLocation);
                }

                if (catType == 2) {
                    hasCOD = JobsHandler.checkForCODwithCatType(JobInfo, catType);
                    if (hasCOD) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", "true");
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", "false");
                    }

                    isDunning = false;
                    if ((JobInfo.ClientIDX == 14753 || JobInfo.ClientIDX == 4868) && JobInfo.DeliveryContents.toUpperCase().equals("DUNNING DOCUMENT")) {
                        isDunning = true;
                        long JobsIDX = JobInfo.JobsIDX;
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX, String.valueOf(JobsIDX));
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "addressWithUnit", addressWithUnit);
                        String cn = JobInfo.JobNo;
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "currJobInfoCN", String.valueOf(cn));
                    }
                }

                objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", String.valueOf(LAT));
                objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", String.valueOf(LNG));
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", postal);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", address);

            }

        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity, initRedirectionToJobInfo." + "\n" + e.getMessage().toString(), "", "", "", "");

            if (cd.isConnectingToInternet()) {
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                JobsHandler.sendErrorEmail("Exception caught at Dashboard2Activity for " + username + ", initRedirectionToJobInfo." + "\n" + e.getMessage().toString() + ". Refreshing data.");
                isRefresh = true;
                GcmPreferences = getGcmPreferences();
                UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
                GetDriverJobsCount task = new GetDriverJobsCount(GcmPreferences, UpdateJobInfoPreferences, regid, username, useridx);
                task.execute();
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_cannotRefreshNoInternet));
            }
        }
    }

    private void startRedirection(TextView lblCount, String JobInfoALjsonPrefName) {
        Intent i = new Intent(Dashboard2Activity.this, JobInfoActivity.class);
        int count;
        try {
            if (!lblCount.getText().toString().isEmpty() && !lblCount.getText().toString().toLowerCase().equals("error")) {
                count = Integer.parseInt(lblCount.getText().toString());
                if (count > 0 && count > 1) {
                    //more than 1 jobs, go to addresslist

                    i = new Intent(Dashboard2Activity.this, AddressListNewActivity.class);
                    JobInfoActivity.accessFromDirection = 0;
                    handler.removeCallbacks(runnable);
                    startActivity(i);
                } else if (count > 0 && count == 1) {
                    //only 1 job, go str8 to jobInfo
                    initRedirectionToJobInfo(JobInfoALjsonPrefName);
                    if (categoryType == 0) {
                        objGlobalMethods.stopAlarmForCommon();
                    } else if (categoryType == 3 || categoryType == 4 || categoryType == 5) {
                        i = new Intent(Dashboard2Activity.this, JobInfoActivity.class);
                        JobInfoActivity.accessFromDirection = 1;
                    } else {
                        if (isDunning) {
                            i = new Intent(Dashboard2Activity.this, setDunningDeliveredStatusActivity.class);
                            JobInfoActivity.accessFromDirection = 0;
                        } else {
                            i = new Intent(Dashboard2Activity.this, JobInfoActivity.class);
                            JobInfoActivity.accessFromDirection = 1;
                        }
                    }


                    handler.removeCallbacks(runnable);
                    startActivity(i);
                } else if (count == 0) {
                    //0 jobs, do nothing
                }
            } else {
                //assume to be 0 jobs, do nothing
            }
        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity, startRedirection." + "\n" + e.getMessage().toString(), "", "", "", "");
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            JobsHandler.sendErrorEmail("Exception caught at Dashboard2Activity for " + username + ", startRedirection." + "\n" + e.getMessage().toString());
        }

    }

    private void startRedirectionToMainCODActivityPage(TextView lblCount) {
        try {
            getCODList task = new getCODList();
            task.execute();
        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity startRedirectionToMainCODActivityPage. Exception: " + e.toString(), "", "", "", "");
        }
    }

    public class getCODList extends AsyncTask<Void, Void, Boolean> {
        getCODList() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdCOD.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean asd = true;

            try {
                JobsHandler.processCODJobs();
            } catch (Exception e) {
                Date dt = new Date();
                objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity getCODList doInBackground. Excpetion: " + e.toString(), "", "", "", "");
                asd = false;
            } finally {
                return asd;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean success) {

            try {
                pdCOD.dismiss();
                Double codval = 0.0;
                String codvalPrefStr = "";
                GcmPreferences = getGcmPreferences();
                codvalPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalCOD");
                try {
                    codval = Double.parseDouble(codvalPrefStr);
                } catch (Exception e) {
                    Date dt = new Date();
                    objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity loadListFirst totalCODPrefStr conversion", "", "", "", "");
                    codval = 0.0;
                }

                if (codval == 0.0) {

                } else if (codval > 0.0) {
                    Intent i = new Intent(Dashboard2Activity.this, MainCODActivity.class);
                    handler.removeCallbacks(runnable);
                    startActivity(i);
                }

            } catch (Exception e) {
                pdCOD.dismiss();
                Date dt = new Date();
                objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity getCODList onPostExecute. Excpetion: " + e.toString(), "", "", "", "");
            }
        }
    }

    private String getRouteListJsonStr(SharedPreferences gcmp) {
        String JsonStr = "";
        try {
            JsonStr = objGlobalMethods.getGCMPrefs(gcmp, "RouteList");
        } catch (Exception e) {
            JsonStr = "";
            Date dt = new Date();
            objGlobalMethods.writeToFile(dt, "", "", "", "", "");
        } finally {
            return JsonStr;
        }
    }

    public class getRoutingList extends AsyncTask<Void, Void, Boolean> {
        private final Boolean isForMap;

        getRoutingList(Boolean aisForMap) {
            this.isForMap = aisForMap;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdRouting.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean asd = true;

            try {
                JobsHandler.HandleRouteList(regid, GcmPreferences);
            } catch (Exception e) {
                Date dt2 = new Date();
                objGlobalMethods.writeToFile(dt2, "Exception caught at Dashboard2Activity getRoutingList doInBackground. Excpetion: " + e.toString(), "", "", "", "");
                asd = false;
            } finally {
                return asd;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean success) {
            String RouteListJsonStr = "";

            try {
                pdRouting.dismiss();
                RouteListJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "RouteList");
                if (!RouteListJsonStr.equals("")) {
                    Intent i = null;
                    if (isForMap) {
                        i = new Intent(Dashboard2Activity.this, MapsAllActivity.class);
                    } else {
                        i = new Intent(Dashboard2Activity.this, AddressListRoutingActivity.class);
                    }

                    handler.removeCallbacks(runnable);
                    startActivity(i);
                } else if (RouteListJsonStr.equals("")) {
                    showDialog(myApp.getAppContext().getString(R.string.alertNoRouteToShow));
                }
            } catch (Exception e) {
                pdRouting.dismiss();
                Date dt3 = new Date();
                objGlobalMethods.writeToFile(dt3, "Exception caught at Dashboard2Activity getCODList onPostExecute. Excpetion: " + e.toString(), "", "", "", "");
            }
        }
    }

    public void updateListPerCategory(int ListItemIndex, String objGlobalVariablesjobsCountName, boolean changeCountColorBool) {
        //final SharedPreferences prefs = getGcmPreferences(context);
        final SharedPreferences prefs = getGcmPreferences();

        updateListviewRow2(ListItemIndex, prefs.getString(objGlobalVariablesjobsCountName, ""), changeCountColorBool);
    }

    private void updateListviewRow2(int index, String count, boolean changeCountTextColor) {

        Dashboard_PostData postdata = null;
        Dashboard_PostData postdata11 = null;
        try {
            Dashboard_PostItemAdapter adapter = (Dashboard_PostItemAdapter) listView.getAdapter();
            if (!showRouteList.equals("")) {
                postdata = adapter.getItem(index + 2);
            } else {
                postdata = adapter.getItem(index);
            }

            postdata.postCount = count;
            postdata.changeCountColorBool = true;

            Double codval = 0.0;
            String codvalPrefStr = "";
            GcmPreferences = getGcmPreferences();
            codvalPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalCOD");
            try {
                codval = Double.parseDouble(codvalPrefStr);
            } catch (Exception e) {
                Date dt = new Date();
                objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity updateListviewRow totalCODPrefStr conversion", "", "", "", "");
                codval = 0.0;
            }
            DecimalFormat dec = new DecimalFormat("#.00");

            if (!showRouteList.equals("")) {
                postdata11 = adapter.getItem(13);
                postdata11.changeCountColorBool = true;
                if (codval == 0.0) {
                    postdata11.postTitle = val[13] + "  " + "$0.00";
                    postdata11.postCount = "0";
                } else if (codval > 0.0) {
                    postdata11.postTitle = val[13] + "  " + "$" + String.valueOf(dec.format(codval));
                    postdata11.postCount = "1";
                }
            } else {
                postdata11 = adapter.getItem(12);
                postdata11.changeCountColorBool = true;
                if (codval == 0.0) {
                    postdata11.postTitle = val[12] + "  " + "$0.00";
                    postdata11.postCount = "0";
                } else if (codval > 0.0) {
                    postdata11.postTitle = val[12] + "  " + "$" + String.valueOf(dec.format(codval));
                    postdata11.postCount = "1";
                }
            }

            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Acitivty updateListviewRow2. Call loadListFirst(). Exception: " + e.toString(), "", "", "", "");
            if (!showRouteList.equals("")) {
                loadListFirst2();
            } else {
                loadListFirst();
            }

        }
    }

    public void updateListviewRowJustForCOD() {
        Dashboard_PostData postdata11 = null;
        try {
            Dashboard_PostItemAdapter adapter = (Dashboard_PostItemAdapter) listView.getAdapter();
            if (adapter != null) {
                Double codval = 0.0;
                String codvalPrefStr = "";
                GcmPreferences = getGcmPreferences();
                codvalPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalCOD");
                try {
                    codval = Double.parseDouble(codvalPrefStr);
                } catch (Exception e) {
                    Date dt = new Date();
                    objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity updateListviewRow totalCODPrefStr conversion", "", "", "", "");
                    codval = 0.0;
                }

                DecimalFormat dec = new DecimalFormat("#.00");
                if (!showRouteList.equals("")) {
                    postdata11 = adapter.getItem(13);
                    postdata11.changeCountColorBool = true;
                    if (codval == 0.0) {
                        postdata11.postTitle = val[13] + "  " + "$0.00";
                        postdata11.postCount = "0";
                    } else if (codval > 0.0) {
                        postdata11.postTitle = val[13] + "  " + "$" + String.valueOf(dec.format(codval));
                        postdata11.postCount = "1";
                    }
                } else {
                    postdata11 = adapter.getItem(12);
                    postdata11.changeCountColorBool = true;
                    if (codval == 0.0) {
                        postdata11.postTitle = val[12] + "  " + "$0.00";
                        postdata11.postCount = "0";
                    } else if (codval > 0.0) {
                        postdata11.postTitle = val[12] + "  " + "$" + String.valueOf(dec.format(codval));
                        postdata11.postCount = "1";
                    }
                }


                adapter.notifyDataSetChanged();

                *//*if (codval == 0.0){

                }
                else if (codval > 0.0){
                    Intent i = new Intent(Dashboard2Activity.this, MainCODActivity.class);
                    handler.removeCallbacks(runnable);
                    startActivity(i);
                }*//*

            }
        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Acitivty updateListviewRowJustForCOD. Exception: " + e.toString(), "", "", "", "");
        }
    }

    public void updateListviewRowJustForJobsheet() {
        Dashboard_PostData postdata7 = null;
        String count = "0";
        try {
            Dashboard_PostItemAdapter adapter = (Dashboard_PostItemAdapter) listView.getAdapter();
            if (adapter != null) {
                GcmPreferences = getGcmPreferences();
                count = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.JobsheetCountName);
                if (!showRouteList.equals("")) {
                    postdata7 = adapter.getItem(9);
                    postdata7.changeCountColorBool = true;
                    postdata7.postCount = count;
                } else {
                    postdata7 = adapter.getItem(8);
                    postdata7.changeCountColorBool = true;
                    postdata7.postCount = count;
                }


                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Acitivty updateListviewRowJustForJobsheet. Exception: " + e.toString(), "", "", "", "");
        }
    }

    //start ASYNC TASKS

    private class GetDriverJobsCount extends AsyncTask<Void, Void, Boolean> {
        final SharedPreferences gcmPref;
        final SharedPreferences aUpdateJobInfoPreferences;
        final String aregid;
        final String ausername;
        final String auseridx;

        GetDriverJobsCount(SharedPreferences agcmPref, SharedPreferences aUpdateJobInfoPreferences,
                           String aaregid, String aausername, String aauseridx) {
            this.gcmPref = agcmPref;
            this.aUpdateJobInfoPreferences = aUpdateJobInfoPreferences;
            this.aregid = aaregid;
            this.ausername = aausername;
            this.auseridx = aauseridx;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean asd = true;
            Gson gson = new Gson();
            Date dt = new Date();
            ArrayList DriverJobsArrayListAL = null;
            String JobInfoJsonStr;
            int count;
            totalCOD = 0.0;
            ArrayList<objCODjobInfo> CODJobInfoAR = null;
            String CODJobInfoARPrefStr = "";
            Boolean exist = false;
            int selectedCODindex = 0;
            objJobInfo ji = null;
            objJobInfo masterji = null;
            objCODjobInfo CODjobInfo = null;

            Long CODjobInfoJobsIDX;
            Integer CODjobInfoCashChequeTypeInt; //0 == unknown 1 == cash 2 == cheq
            Integer CODjobInfoCustorXDelCash; //1 == xdel 2 == cust
            Long CODjobInfoDOIDX;
            String CODjobInfoDOInvoice;
            int CODjobInfocatType;
            String CODjobInfocategoryTitle;
            Double CODjobInfocashchequeAmt;
            objJobInfo CODjobInfoJobInfo;
            String CODConsignmentNote;
            GcmPreferences = getGcmPreferences();

            try {
                if (!Boolean.parseBoolean(ishomesuser)) {
                    DriverJobsArrayList = JobsHandler.GetDriverJobsArrayList(regid);
                } else {
                    DriverJobsArrayList = null;
                }

                if (DriverJobsArrayList != null && DriverJobsArrayList.size() > 0) {
                    CODJobInfoARPrefStr = objGlobalMethods.getGCMPrefs(gcmPref, "custXDelTotalCashPrefStr");
                    CODJobInfoAR = gson.fromJson(CODJobInfoARPrefStr, new TypeToken<List<objCODjobInfo>>() {
                    }.getType());
                    for (int i = 0; i <= DriverJobsArrayList.size() - 1; i++) {
                        ArrayList arr = new ArrayList();
                        arr = (ArrayList) DriverJobsArrayList.get(i);
                        long[] JobsIDXarr = (long[]) arr.get(0);
                        count = (int) arr.get(1);
                        Arrays.sort(JobsIDXarr);
                        String s = "";
                        String JsonStr = "";
                        for (int v = 0; v <= JobsIDXarr.length - 1; v++) {
                            s += "[" + String.valueOf(JobsIDXarr[v]) + "]";
                        }

                        if (!s.trim().equals("")) {
                            try {
                                cHashstr = SHA1(s);
                            } catch (Exception e) {
                                e.printStackTrace();
                                s = "";
                                cHashstr = "";
                            }
                        } else {
                            cHashstr = "";
                        }


                        JsonStr = gson.toJson(JobsIDXarr);

                        objJobInfo[] JobInfoAL;
                        JobInfoJsonStr = "";
                        if ((objJobInfo[]) arr.get(2) != null) {
                            JobInfoAL = (objJobInfo[]) arr.get(2);
                            *//*for (int x = 0; x <= JobInfoAL.length-1;x++){
                                JobInfoAL[x].CODCollected = true;
                            }*//*
                            JobInfoJsonStr = gson.toJson(JobInfoAL);
                        }


                        if (i == 0) {
                            JobsHandler.setCategoryHashesForDashboard(GcmPreferences, objGlobalVariables.PIPhashName, cHashstr, objGlobalVariables.rtNewJobsIDXarrName, JsonStr, objGlobalVariables.JobInfoPIPALjsonPrefName, JobInfoJsonStr, objGlobalVariables.PIPjobsCountName, String.valueOf(count));
                        }
                        if (i == 1) {
                            JobsHandler.setCategoryHashesForDashboard(GcmPreferences, objGlobalVariables.PUhashName, cHashstr, objGlobalVariables.rtPickedJobsIDXarrName, JsonStr, objGlobalVariables.JobInfoPUALjsonPrefName, JobInfoJsonStr, objGlobalVariables.PUjobsCountName, String.valueOf(count));
                        }
                        if (i == 2) {
                            JobsHandler.setCategoryHashesForDashboard(GcmPreferences, objGlobalVariables.DIPhashName, cHashstr, objGlobalVariables.rtDeliveryJobsIDXarrName, JsonStr, objGlobalVariables.JobInfoDIPALjsonPrefName, JobInfoJsonStr, objGlobalVariables.DIPjobsCountName, String.valueOf(count));
                        }
                        if (i == 3) {
                            JobsHandler.setCategoryHashesForDashboard(GcmPreferences, objGlobalVariables.DELhashName, cHashstr, objGlobalVariables.rtDeliveredJobsIDXarrName, JsonStr, objGlobalVariables.JobInfoDELALjsonPrefName, JobInfoJsonStr, objGlobalVariables.DELjobsCountName, String.valueOf(count));
                        }
                        if (i == 4) {
                            JobsHandler.setCategoryHashesForDashboard(GcmPreferences, objGlobalVariables.FPhashName, cHashstr, objGlobalVariables.rtFailedPickUpJobsIDXarrName, JsonStr, objGlobalVariables.JobInfoFPALjsonPrefName, JobInfoJsonStr, objGlobalVariables.FPjobsCountName, String.valueOf(count));
                        }
                        if (i == 5) {
                            JobsHandler.setCategoryHashesForDashboard(GcmPreferences, objGlobalVariables.FDhashName, cHashstr, objGlobalVariables.rtFailedDeliveryJobsIDXarrName, JsonStr, objGlobalVariables.JobInfoFDALjsonPrefName, JobInfoJsonStr, objGlobalVariables.FDjobsCountName, String.valueOf(count));
                        }
                    }

                    *//*if (isRefresh){

                    }
                    else {
                        JobsHandler.processCODJobs();

                        String CODJobInfoARJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "custXDelTotalCashPrefStr");
                        CODJobInfoAR = gson.fromJson(CODJobInfoARJsonStr, new TypeToken<List<objCODjobInfo>>(){}.getType());

                        if (CODJobInfoAR != null && CODJobInfoAR.size() > 0){
                            CODJobInfoARPrefStr = gson.toJson(CODJobInfoAR);

                            for (int l=0;l<=CODJobInfoAR.size()-1;l++){
                                totalCOD += CODJobInfoAR.get(l).Amount;
                            }

                            if (totalCOD == null || totalCOD == 0.0){
                                objGlobalMethods.setGCMPrefs(gcmPref, "totalCOD", "0.0");
                            }
                            else if (totalCOD != null && totalCOD > 0.0){
                                objGlobalMethods.setGCMPrefs(gcmPref,"totalCOD", String.valueOf(totalCOD));
                            }

                            objGlobalMethods.setGCMPrefs(gcmPref,"custXDelTotalCashPrefStr", CODJobInfoARPrefStr);
                        }
                        else if (CODJobInfoAR == null || CODJobInfoAR.size() == 0){
                            objGlobalMethods.setGCMPrefs(gcmPref,"custXDelTotalCashPrefStr", "");
                        }
                    }*//*

                    JobsHandler.HandleRouteList(regid, gcmPref);

                    JobsHandler.processCODJobs();

                    String CODJobInfoARJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "custXDelTotalCashPrefStr");
                    CODJobInfoAR = gson.fromJson(CODJobInfoARJsonStr, new TypeToken<List<objCODjobInfo>>() {
                    }.getType());

                    if (CODJobInfoAR != null && CODJobInfoAR.size() > 0) {
                        CODJobInfoARPrefStr = gson.toJson(CODJobInfoAR);

                        for (int l = 0; l <= CODJobInfoAR.size() - 1; l++) {
                            totalCOD += CODJobInfoAR.get(l).Amount;
                        }

                        if (totalCOD == null || totalCOD == 0.0) {
                            objGlobalMethods.setGCMPrefs(gcmPref, "totalCOD", "0.0");
                        } else if (totalCOD != null && totalCOD > 0.0) {
                            objGlobalMethods.setGCMPrefs(gcmPref, "totalCOD", String.valueOf(totalCOD));
                        }

                        objGlobalMethods.setGCMPrefs(gcmPref, "custXDelTotalCashPrefStr", CODJobInfoARPrefStr);
                    } else if (CODJobInfoAR == null || CODJobInfoAR.size() == 0) {
                        objGlobalMethods.setGCMPrefs(gcmPref, "custXDelTotalCashPrefStr", "");
                    }

                    asd = true;

                } else {
                    asd = false;
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity GetDriverJobsCount doInBackground. Exception: " + e.toString(), "", "", "", "");
                asd = false;
                totalCOD = 0.0;
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Date dt = new Date();
            try {
                if (success) {
                    if (isRefresh) {
                        //updateList();
                        if (refreshQueBooleanString) {
                            GcmPreferences = getGcmPreferences();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "refreshQueBooleanString", "false");
                        } else {
                            *//*menuItem.collapseActionView();
                            menuItem.setActionView(null);*//*
                        }

                        *//*menuItem.collapseActionView();
                        menuItem.setActionView(null);*//*
                        isRefresh = false;
                        if (!showRouteList.equals("")) {
                            loadListFirst2();
                        } else {
                            loadListFirst();
                        }
                    } else {
                        if (!showRouteList.equals("")) {
                            loadListFirst2();
                        } else {
                            loadListFirst();
                        }
                    }
                } else {
                    *//*if (isRefresh) {
                        menuItem.collapseActionView();
                        menuItem.setActionView(null);
                    }*//*

                    if (!showRouteList.equals("")) {
                        loadListFirst2();
                    } else {
                        loadListFirst();
                    }

                    if (cd.isConnectingToInternet()) {
                        showDialog(myApp.getAppContext().getString(R.string.alert_noRetrieveDataFail));
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_noRetrieveDataFailDueNoInternet));
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity GetDriverJobsCount onPostExecute. Exception: " + e.toString(), "", "", "", "");
            } finally {
                pd.hide();
                //check for any new job yet to be updated to PIP
                JobsHandler.doubleCheckUpdatePIP(gcmPref, aUpdateJobInfoPreferences, aregid, ausername, auseridx, false);
            }
        }
    }

    //end ASYNC TASKS

    //start timer section
    public static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                *//* do what you need to do *//*
                if (!JobsHandler.backEndIsRunning) {
                    //runner Is Not Running
                    Context context = myApp.getAppContext();
                    context.sendBroadcast(new Intent(context, BackendRun_BroadcastReceiver.class));
                } else {

                }

      *//* and here comes the "trick" *//*

                //start backendRun again after 15s
                //handler.postDelayed(this, 15000);
            } catch (Exception e) {
                e.printStackTrace();
                Date dt = new Date();
                objGlobalMethods.writeToFile(dt, "Exception caught @ Dashboard2Activity Runnable(). " + e.getMessage().toString(),
                        "", "", "", "");
            }

        }
    };

    public static void stopRunnable() {
        JobsHandler.backEndIsRunning = false;
        handler.removeCallbacks(runnable);
    }

    public static void postphoneRunnable() {
        JobsHandler.backEndIsRunning = false;
        handler.postDelayed(runnable, 15000);
    }

    //end timer section

    //start Hash SHA1
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString().toUpperCase();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    //end HASH SHA1

    private String getStoredStatusCodes(Context context, String enzh) {
        final SharedPreferences prefs = getGcmPreferences();
        String storedStatusCodes = "";
        if (enzh.equals("en")) {
            storedStatusCodes = prefs.getString(objGlobalVariables.storedStatusCodesPrefNameEN, "");
        } else {
            storedStatusCodes = prefs.getString(objGlobalVariables.storedStatusCodesPrefNameZH, "");
        }

        return storedStatusCodes;
    }

    private void downloadStatusCodes() {
        try {
            GcmPreferences = getGcmPreferences();
            JobsHandler.downloadStatusCodesLibWS task = new JobsHandler.downloadStatusCodesLibWS(regid, GcmPreferences);
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopAlarm() {
        try {
            if (manager != null) {
                alarmStarted = false;
                manager.cancel(pendingIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopPerMinuteAlarm() {
        try {
            if (manager2 != null) {
                alarmStarted2 = false;
                manager2.cancel(pendingIntent2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playTone() {
        player = MediaPlayer.create(Dashboard2Activity.this, R.raw.error);
        player.start();
    }

    private void cancelGPSalarm() {
        Date dt = new Date();
        try {
            if (GPSmanager != null) {
                GPSmanager.cancel(GPSpendingIntent);

            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile(dt, "Exception caught at Dashboard2Activity cancelGPSalarm. Exception: " + e.toString(), "", "", "", "");
        }
    }*/


}
