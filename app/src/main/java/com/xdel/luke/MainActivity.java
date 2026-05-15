package com.xdel.luke;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.tzone.bluetooth.BleManager;
import com.xdel.luke.ACI.DigitalAgreementActivity;
import com.xdel.luke.BottomSheet.ProgressBarDialog;
import com.xdel.luke.ExceptionHandler.DefaultExceptionHandler;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.FileReader.fileReader;
import com.xdel.luke.HRM.ChangePwdActivity;
import com.xdel.luke.HRM.ProfileActivity;
import com.xdel.luke.JobScheduler.Util;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.HendersonLocationCustom;
import com.xdel.luke.objClasses.LocaleHelper;
import com.xdel.luke.objClasses.LocationService;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objAuthDriverResult;
import com.xdel.luke.objClasses.objAuthRequest;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objUpdateInfo;
import com.xdel.luke.objClasses.test;
import com.xdel.luke.permissions.PermissionsManager;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.temploggerWhse.TempLoggerWhseScanActivity;
import com.xdel.luke.templogger.btReport;
import com.xdel.luke.templogger.temploggerclass;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
//import android.support.v7.app.ActionBarActivity;

//public class MainActivity extends ActionBarActivity {
public class MainActivity extends AppCompatActivity implements LocationListener {
    static boolean errored = false;
    TextView statusTV;
    TextView mDisplay;
    CheckBox cbHomesUser;
    CheckBox cbApplyLeave;
    Button btnLogin;
    EditText txtUsername;
    EditText txtPwd;
    EditText txtQRcode;
    Context context;
    private static SharedPreferences GcmPreferences;
    private SharedPreferences UpdateJobInfoPreferences;
    private static String LoginName = "";
    private static String LoginPassword = "";
    private static String ISHOMESUSER = "false";
    public long LoginSuccess = 0;
    private ProgressDialog pd;
    private ProgressDialog pdUpdates;
    private ProgressDialog pddownload;
    private ProgressDialog pdRegID;
    private ProgressDialog pdGPSlockedCheck;
    private ProgressDialog pdCommunal;
    private LinearLayout linearLayout1, LLAttendanceQR;
    // Connection detector
    ConnectionDetector cd;
    public static int uponLogin;
    private String prevLoginUserName = "";
    private String prevLoginUserIDX = "";
    private String akinori_download_link = "http://akinori.xdel.com/app-debug.apk";
    private ImageView imgLogo;
    private String imgLogoLongClickPwd = "XDel9911";

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "17277152085";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "Luke";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regid;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    String langType = "en";
    private final int PERMISSION_REQUEST_CODE_LOCATION = 1001;
    private final int CAMERA_PERMISSION_CODE = 100;
    private final int STORAGE_PERMISSION_CODE = 101;
    private static LocationManager locationManager;
    private String provider;
    private static Location location;
    //private boolean isNearHendersonOfc;
    private String cameraScannedCode;
    ScreenShotContentObserver screenShotContentObserver;
    private PermissionsManager permissionsManager;

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location alocation) {
        try {
            float lat = (float) (alocation.getLatitude());
            float lng = (float) (alocation.getLongitude());
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            String gpsLocked = objGlobalMethods.getGCMPrefs(GcmPreferences, "gpsLocked");
            if (lat > 0 && lng > 0) {
                location = alocation;
            }
            if (lat > 0 && lng > 0 && (gpsLocked.isEmpty() || gpsLocked.equals("") || gpsLocked == "")) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "gpsLocked", "true");
                objGlobalMethods.writeToFile("locked gps " + lat + "," + lng, "", "", "", "");
                //isNearHendersonOfc = objGlobalMethods.isNearHendersonOfc(lat, lng);
            } else {
                //objGlobalMethods.writeToFile("gps not locked " + lat + "," + lng, "", "", "", "");
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    private AlertDialog EntePasswordalertDialog = null;

    private void ViewCommunalCreationPwdPrompt() {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setTitle(myApp.getAppContext().getString(R.string.enterpassword));
            final View settingsView = getLayoutInflater().inflate(R.layout.custom_settings_pwd, null);
            builder.setView(settingsView);
            Button btnCancel = (Button) settingsView.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                if (getCurrentFocus() != null) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }
                            }
                        }, 100);

                        EntePasswordalertDialog.dismiss();
                    } catch (Exception e) {
                        String s = e.getMessage().toString();
                    }
                }
            });

            Button btnVerify = (Button) settingsView.findViewById(R.id.btnVerify);
            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EditText txtSettingsPwd = (EditText) settingsView.findViewById(R.id.txtSettingsPwd);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                if (getCurrentFocus() != null) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }
                            }
                        }, 100);
                        if (!txtSettingsPwd.getText().toString().isEmpty() && !txtSettingsPwd.getText().toString().equals("") && txtSettingsPwd.getText().toString() != "") {
                            if (objGlobalMethods.verifyCreateCommunalFilePwd(txtSettingsPwd.getText().toString())) {
                                EntePasswordalertDialog.dismiss();
                                CreateCommunalFile c = new CreateCommunalFile(true);
                                c.execute();
                            } else {
                                Context ctx = myApp.getAppContext();
                                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.btn_ok), ctx.getString(R.string.alert_incorrectpwd)).show();
                            }
                        } else {
                            Context ctx = myApp.getAppContext();
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.btn_ok), ctx.getString(R.string.enterpassword)).show();
                        }
                    } catch (Exception e) {
                        String s = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at MainActivity ViewCommunalCreationPwdPrompt btnVerify.setOnClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    }
                }
            });

            builder.setCancelable(true);
            EntePasswordalertDialog = builder.create();
            EntePasswordalertDialog.show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MainActivity ViewCommunalCreationPwdPrompt. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public class CreateCommunalFile extends AsyncTask<Void, Void, Integer> {
        private final boolean isTempLoggerScannerDevice;

        public CreateCommunalFile(boolean _isTempLoggerScannerDevice) {
            this.isTempLoggerScannerDevice = _isTempLoggerScannerDevice;
        }

        @Override
        protected void onPreExecute() {
            try {
                pdCommunal.show();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Integer flag = 0; //1 file exists or create 2 file writtened
            boolean filecreated = false;
            boolean filewrittened = false;
            Boolean asd = false;
            //String tempLoggerScannerDeviceGsonStr = "{&quot;TempLoggerScanning&quot;:true}";
            String tempLoggerScannerDeviceGsonStr = "{\"TempLoggerScanning\":true}";
            try {
                boolean hasXDelCommunalDeviceFile_ = objGlobalMethods.hasXDelCommunalDeviceFile();
                if (!hasXDelCommunalDeviceFile_) {
                    filecreated = objGlobalMethods.createCommunalFile();
                    if (filecreated) {
                        flag = 1;
                        filewrittened = objGlobalMethods.writeToCommunalFile(tempLoggerScannerDeviceGsonStr);
                        flag = filewrittened ? 2 : 1;
                    }
                } else {
                    flag = 1;
                    boolean isTempLoggerScannerDevice = fileReader.isTempLoggerScannerDevice();
                    if (!isTempLoggerScannerDevice) {
                        filewrittened = objGlobalMethods.writeToCommunalFile(tempLoggerScannerDeviceGsonStr);
                        flag = filewrittened ? 2 : 1;
                    } else {
                        flag = 2;
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at MainActivity CreateCommunalFile doInBackground. Exception: " + e.getMessage().toString(), "", "", "", "");
            } finally {
                return flag;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Integer res) {
            try {
                pdCommunal.dismiss();
                Context ctx = myApp.getAppContext();
                if (res == 1) {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), "Error!\n\nCommunal File created, but not written.").show();
                } else if (res == 2) {
                    recreate();
                } else {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), "Error!\n\nCommunal File not created.").show();
                }

            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at MainActivity CreateCommunalFile onPostExecute, auto login if possible. Exception: " + e.getMessage().toString(), "", "", "", "");
            } finally {
                if (pdCommunal != null && pdCommunal.isShowing())
                    pdCommunal.dismiss();
            }
        }

    }

    private View.OnLongClickListener imgLogo_OnLongClickListener(boolean hasXDelCommunalDeviceFile_, boolean isTempLoggerScannerDevice) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    if (!hasXDelCommunalDeviceFile_) {
                        ViewCommunalCreationPwdPrompt();
                    } else if (hasXDelCommunalDeviceFile_ && isTempLoggerScannerDevice) {
                        Intent i = new Intent(myApp.currentActivity, TempLoggerWhseScanActivity.class);
                        startActivity(i);
                        finish();
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at MainActivity imgLogo_OnLongClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_errorperformoperation)).show();
                } finally {
                    return false;
                }
            }
        };
    }

    private View.OnClickListener imgLogo_OnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at MainActivity imgLogo_OnClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private void setCommunalDevice(boolean TempReaderDevice) {
        try {

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MainActivity setCommunalDevice. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp.setAppContext(getApplicationContext());
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
        myApp.getInstance().initAppLanguage(getBaseContext());
        myApp.getInstance().initAppLanguage(myApp.getAppContext());
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_main);

        try {
            //FirebaseApp.initializeApp(this);
            FirebaseApp.initializeApp(myApp.getAppContext());
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at MainActivity onCreate FirebaseApp.initializeApp. Exception: " + s, "", "", "", "");
        }

        try {
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            if ((username.isEmpty() || username.equals("") || username == "") && (password.isEmpty() && password.equals("") || password == "")) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "gpsLocked", "");
            }
        } catch (Exception e) {

        }

        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        linearLayout1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });
        LLAttendanceQR = (LinearLayout) findViewById(R.id.LLAttendanceQR);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPwd = (EditText) findViewById(R.id.txtPwd);
        txtQRcode = (EditText) findViewById(R.id.txtQRcode);
        cbHomesUser = (CheckBox) findViewById(R.id.cbHomesUser);
        cbHomesUser.setOnCheckedChangeListener(null);
        cbHomesUser.setOnCheckedChangeListener(cbHomesUserCheckedChangeListener());
        cbApplyLeave = (CheckBox) findViewById(R.id.cbApplyLeave);
        cbApplyLeave.setOnCheckedChangeListener(null);
        cbApplyLeave.setOnCheckedChangeListener(cbApplyLeaveCheckedChangeListener());
        statusTV = (TextView) findViewById(R.id.tv_result);
        mDisplay = (TextView) findViewById(R.id.lblRegID);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_wait_loading));

        pdUpdates = new ProgressDialog(this);
        pdUpdates.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdUpdates.setCanceledOnTouchOutside(false);
        pdUpdates.setMessage(myApp.getAppContext().getString(R.string.pd_wait_checkupdates));

        pdGPSlockedCheck = new ProgressDialog(this);
        pdGPSlockedCheck.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdGPSlockedCheck.setCanceledOnTouchOutside(false);
        pdGPSlockedCheck.setMessage(myApp.getAppContext().getString(R.string.pd_wait_checkgpslocked));

        pddownload = new ProgressDialog(this);
        pddownload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pddownload.setCanceledOnTouchOutside(false);
        pddownload.setCancelable(false);
        pddownload.setTitle(myApp.getAppContext().getString(R.string.title_downloadupdates));

        pddownload.setMessage(myApp.getAppContext().getString(R.string.pd_wait_downloading));
        pddownload.setIcon(R.mipmap.ic_action_downloading);
        pddownload.setProgressNumberFormat(null);
        pddownload.setProgressPercentFormat(NumberFormat.getPercentInstance());

        pdRegID = new ProgressDialog(this);
        pdRegID.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdRegID.setCanceledOnTouchOutside(false);
        pdRegID.setMessage(myApp.getAppContext().getString(R.string.pd_wait_registerregid));

        pdCommunal = new ProgressDialog(this);
        pdCommunal.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdCommunal.setCanceledOnTouchOutside(false);
        pdCommunal.setMessage(myApp.getAppContext().getString(R.string.pd_wait_creating_communal));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
        // Get the location manager
        objGlobalMethods.writeToFile("MainActivity onCreate", "", "", "", "");

        initGPS();


    } //end of OnCreate

    private void test() {
        try {
            String link = "https://home.xdel.com";
            URL url = new URL(link);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String webPage = "", data = "";

            while ((data = reader.readLine()) != null) {
                webPage += data + "\n";
            }

            String wp = webPage;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            String m = e.getMessage();
        }
    }

    private class testAsync extends AsyncTask<Void, Void, String> {

        testAsync() {
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String wp = "";
            try {
                String link = "https://home.xdel.com";
                URL url = new URL(link);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String webPage = "", data = "";

                while ((data = reader.readLine()) != null) {
                    webPage += data + "\n";
                }

                wp = webPage;
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                String m = e.getMessage();
            } finally {
                return wp;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final String success) {
            pd.dismiss();

        }
    }

    private void showEditTextID() {
        EditText et = null;
        String n = "";
        try {
            if (linearLayout1 != null) {
                ArrayList<EditText> myEditTextList = new ArrayList<EditText>();
                for (int i = 0; i <= linearLayout1.getChildCount() - 1; i++) {
                    if (linearLayout1.getChildAt(i) instanceof EditText) {
                        myEditTextList.add((EditText) linearLayout1.getChildAt(i));
                    }
                }

                if (myEditTextList != null && myEditTextList.size() > 0) {
                    for (int i = 0; i <= myEditTextList.size() - 1; i++) {
                        et = myEditTextList.get(i);
                        if (et != null) {
                            n += getResources().getResourceEntryName(et.getId()) + "\n";
                        }
                    }
                }

                if (!objGlobalMethods.IsEmptyString(n)) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), n).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            /*testAsync ta = new testAsync();
            ta.execute();*/

            try {
                /*BleManager bm1;
                bm1 = BleManager.getInstance();
                bm1.destroy();
                bm1 = BleManager.getInstance();*/
                //disableCopyPaste();
                objGlobalMethods.writeToFile("MainActivity onResume", "", "", "", "");
                GcmPreferences = getGcmPreferences();
                UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
                if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }

                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                //context = getApplicationContext();
                context = myApp.getAppContext();

                setTitle("LUKE v." + getAppVersionName(context));

                setHendersonLocation(GcmPreferences);

                Util.cancelAllJobs(context);
                FCMservice.notiNotRead = false;

                cd = new ConnectionDetector(null);
                // Check if Internet present

                if (FCMservice.mNotificationManager != null) {
                    FCMservice.mNotificationManager.cancel(2);
                }

                PermissionsManager permissionsManager = new PermissionsManager(this);

                // Check and request airplane mode to be turned off
                if (!permissionsManager.isAirplaneModeOff()) {
                    showChangeAirplaneModeSettingsDialogNew(permissionsManager);
                    return;
                }

                // Check if Bluetooth is enabled
                int f = bt_enabled();
                if (f == 1) {
                    objGlobalMethods.showDialog(myApp.currentActivity, context.getString(R.string.buttontext_ok), context.getString(R.string.bt_4_not_supported)).show();
                    return;
                } else if (f == 2) {
                    showEnableBTSettingsDialogNew(permissionsManager);
                    return;
                }

                // Check and request battery optimization to be disabled
                if (!permissionsManager.isBatteryOptimizationDisabled()) {
                    permissionsManager.requestDisableBatteryOptimization();
                    return;
                }

                // Or check everything including Bluetooth
                if (!permissionsManager.areAllPermissionsAndSettingsConfigured()) {
                    permissionsManager.requestMissingPermissionsAndSettings();
                    return;
                }

                // Check and request location mode to be high accuracy
                if (!permissionsManager.isLocationModeHighAccuracy()) {
                    permissionsManager.requestLocationHighAccuracy();
                    return;
                }

                // Check and request unrestricted data usage
                if (!permissionsManager.isUnrestrictedDataUsageEnabled()) {
                    //permissionsManager.requestUnrestrictedDataUsage();
                    showEnableBGandUnrestrictDataUsageSettingsDialogNew(permissionsManager);
                    return;
                }

                if (!permissionsManager.isHighGPSAccuracy())
                    showChangeGPStoHighAccModeSettingsDialog();

                if (TempLoggerWhseScanActivity.RestarterTimer != null) {
                    TempLoggerWhseScanActivity.RestarterTimer.cancel();
                    TempLoggerWhseScanActivity.RestarterTimer = null;
                    TempLoggerWhseScanActivity.RestarterTimerFinishCalled = null;
                }

                if (TempLoggerWhseScanActivity.MainTimer != null) {
                    TempLoggerWhseScanActivity.MainTimer.cancel();
                    TempLoggerWhseScanActivity.MainTimer = null;
                }

                if (TempLoggerWhseScanActivity.DeviceScanningTimer != null) {
                    TempLoggerWhseScanActivity.DeviceScanningTimer.cancel();
                    TempLoggerWhseScanActivity.DeviceScanningTimer = null;
                }

                if (TempLoggerWhseScanActivity.DeviceListViewRefresh_Timer != null) {
                    TempLoggerWhseScanActivity.DeviceListViewRefresh_Timer.cancel();
                    TempLoggerWhseScanActivity.DeviceListViewRefresh_Timer = null;
                }

                /*int f = bt_enabled();
                if (f == 1) {
                    objGlobalMethods.showDialog(myApp.currentActivity, context.getString(R.string.buttontext_ok), context.getString(R.string.bt_4_not_supported)).show();
                    return;
                } else if (f == 2) {
                    Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(mIntent, 1);
                }*/

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    myApp.BTConnectGranted();
                }

                String[] prefName = new String[]{"GrabSysChatHistoryTries", "GetAllMsgCompleted", "currPostal", "earlierPostal", "editingvrp",
                        "isrefreshing", "stopLocationService", "EmailQuePrefStr", "hasOpsQue", "relogging_in",
                        "GetUserResultJsonStr", "showpippop", "HOQRimgB64ListJsonStr", "refHOHash", "ho_jsonStr", "prevfirstQlocation", "CallRequeueFlagstr",
                        "scanResultListSize", "OpID12timeReceivedLongStr", "showMPD_Dialog_IsShowing", "showRegBTTL_Dialog_IsShowing"};
                String[] prefvalue = new String[]{"0", "", "", "", "",
                        "", "", "", "", "",
                        "", "", "", "", "", "", "",
                        "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                JobsHandler3.clearVrpSP();
                JobsHandler3.clearShpmtVSP(null);

                SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                boolean hasXDelCommunalDeviceFile_ = objGlobalMethods.hasXDelCommunalDeviceFile();
                boolean isTempLoggerScannerDevice = fileReader.isTempLoggerScannerDevice();

                imgLogo.setOnLongClickListener(null);
                imgLogo.setOnLongClickListener(imgLogo_OnLongClickListener(hasXDelCommunalDeviceFile_, isTempLoggerScannerDevice));
                imgLogo.setOnClickListener(null);
                imgLogo.setOnClickListener(imgLogo_OnClickListener());

                if (hasXDelCommunalDeviceFile_ && isTempLoggerScannerDevice) {
                    String[] BTprefName = new String[]{"btonConnectFail", "bttemploggerclassStarted", "bttemploggerStandaloneclassStarted",
                            "JobsIDXBTLScanned", "RegBTLscanClassStarted"};
                    String[] BTprefvalue = new String[]{"", "", "",
                            "", ""};
                    objGlobalMethods.setBTLoggerRelatedMass(btp, BTprefName, BTprefvalue);
                } else {
                    String[] BTprefName = new String[]{"btonConnectFail", "bttemploggerclassStarted", "bttemploggerStandaloneclassStarted",
                            "JobsIDXBTLScanned", "RegBTLscanClassStarted", "UploadWhseTempDataObjALPrefStr"};
                    String[] BTprefvalue = new String[]{"", "", "",
                            "", "", ""};
                    objGlobalMethods.setBTLoggerRelatedMass(btp, BTprefName, BTprefvalue);
                }


                temploggerclass.disposeTimer();

                String AdministrativeMode = objGlobalMethods.getGCMPrefs(GcmPreferences, "AdministrativeMode");

                JobsHandler.clearChangePwdActivityPrefs(GcmPreferences);
                JobsHandler.clearlAllDetailPagesStatic();
                JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "", "", true);

                JobsHandler.checkForceLogOut();

                boolean idledMoreThan3Hrs = JobsHandler.idledMoreThan3Hrs_new();
                //idledMoreThan3Hrs = true;
                if (idledMoreThan3Hrs) {
                    objGlobalMethods.writeToFile("Idled more than 3 hours. Manual force logout.", "", "", "", "");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "forceLogOutBooleanString", "true");
                    JobsHandler.ManualForceLogout();
                }

                //// Get the location manager
                //initGPS(); //shifted this to onCreate

                /*String credEditedDateLong = "1753879332000";
                String[] vrpspprefName = new String[]{"credEditedDateLong"};
                String[] vrpspprefvalue = new String[]{credEditedDateLong};
                objGlobalMethods.setVrpSPMass(objGlobalMethods.getVrpRelatedSharedPreferences(), vrpspprefName, vrpspprefvalue);*/

                objGlobalMethods.resetCred();
                objGlobalMethods.deleteLogFilesAsync t = new objGlobalMethods.deleteLogFilesAsync();
                t.execute();

                //objGlobalMethods.resetVrpUnlockPostalCounts_n_UsedCounts();

                JobsHandler.backEndIsRunning = false;
                FCMservice.enablePush = false;
                DashboardSubActivity.frompage = null;
                btReport.IsRunning = null;

                if (UsernamePwd == null) {
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                    username = UsernamePwd[0];
                    password = UsernamePwd[1];
                    useridx = UsernamePwd[2];
                    ishomesuser = UsernamePwd[3];

                }

                DAO_Jobs.dbhelper = null;

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ((username == null || username.length() == 0) && (password == null || password.length() == 0))) {
                    objGlobalMethods.stopActiveForegroundSvc();
                } else if (FCMservice.mNotificationManager != null) {
                    FCMservice.mNotificationManager.cancel(-1);
                }

                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();

                /*if (hasXDelCommunalDeviceFile_ && isTempLoggerScannerDevice) {
                    objGlobalMethods.showDialog(myApp.currentActivity, context.getString(R.string.buttontext_ok), "isTempLoggerScannerDevice").show();
                } else */
                if (!cd.isConnectingToInternet() || IsMaintenanceHours) {
                    //// Internet Connection is not present
                    if ((username.length() != 0 && username != "") && (password.length() != 0 && password != "") && !Boolean.parseBoolean(ishomesuser) && idledMoreThan3Hrs) {
                        objGlobalMethods.writeToFile("Attempt to auto Login, idledMoreThan3Hrs." + (IsMaintenanceHours ? " IsMaintenanceHours." : ""), "", "", "", "");
                        check4appUpdates(false);
                    } else if ((username.length() != 0 && username != "") && (password.length() != 0 && password != "") && Boolean.parseBoolean(ishomesuser)) {
                        //more for HOMES user
                        ForceLoginHomesUser(username);
                    } else if ((username.length() != 0 && username != "") && (password.length() != 0 && password != "") && !Boolean.parseBoolean(ishomesuser) && !objGlobalMethods.IsEmptyString(AdministrativeMode)) {
                        prefName = new String[]{"AdministrativeMode", "forceLogOutBooleanString"};
                        prefvalue = new String[]{"", "true"};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                        JobsHandler.checkForceLogOut();
                    } else if ((username.length() != 0 && username != "") && (password.length() != 0 && password != "") && !Boolean.parseBoolean(ishomesuser)) {
                        Intent intent = new Intent(myApp.currentActivity, !IsMaintenanceHours && objGlobalMethods.IsDefaultVRPpage() ? DrawerMain2Activity.class : DrawerMainActivity.class);
                        objGlobalMethods.writeToFile("Logged in without internet connection. Luke Version: " + getAppVersionName(context),
                                "", "", username, useridx);
                        uponLogin = 1;
                        JobsHandler.backEndIsRunning = false;
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            showActiveNotification2();
                        } else {
                            showActiveNotification(username);
                        }

                        String[] prefName2 = new String[]{"disablePush"};
                        String[] prefvalue2 = new String[]{""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName2, prefvalue2);

                       /* ////register screenOnOff BCR
                        RecevierMgr rm = new RecevierMgr();
                        rm.regScreenOnOffReceiver();*/

                        FCMservice.enablePush = true;
                        objGlobalMethods.GPSalarmStarted = false;
                        startActivity(intent);
                        finish();
                    } else {
                        /*if (!IsMaintenanceHours)
                            showDialog("Please login again \nor connect to working internet connection.");*/
                            //// stop executing code by return

                        if (cd.isConnectingToInternet()) {
                            if (checkPlayServices()) {
                                regid = FCMservice.getToken();
                                if (regid.isEmpty()) {
                                    objGlobalMethods.writeToFile("regid is empty. Registering now.", "", "", "", "");
                                    getFCMtoken();
                                }

                                if (!IsMaintenanceHours)
                                    showDialog("Please login again \nor connect to working internet connection.");
                            } else {
                                showErrorDialog(context.getString(R.string.alert_devicenotsupported));
                            }
                        } else {
                            if (!IsMaintenanceHours)
                                showDialog("Please login again \nor connect to working internet connection.");
                        }
                    }
                } else {
                    ////this portion is here due to DigitalAgreement rejected, will resend logout email in JobsHandler.checkForceLogOut();
                    //String agreementrejected = objGlobalMethods.getGCMPrefs(GcmPreferences, "agreementrejected");
                    //if (!objGlobalMethods.IsEmptyString(agreementrejected)) {
                    //objGlobalMethods.writeToFile("processQueuedEmailsToSend0 started at MainActivity due DA rejected.","","","","");
                    //JobsHandler.processQueuedEmailsToSend0 p = new JobsHandler.processQueuedEmailsToSend0();
                    //p.execute();
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, "agreementrejected", "");
                    //}
                    ////this portion is here due to DigitalAgreement rejected, will resend logout email in JobsHandler.checkForceLogOut();

                    // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
                    if (checkPlayServices()) {
                        regid = FCMservice.getToken();

                        if (regid.isEmpty()) {
                            objGlobalMethods.writeToFile("regid is empty. Registering now.", "", "", "", "");
                            getFCMtoken();
                        } else {
                            cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode").toUpperCase();
                            if (!cameraScannedCode.equals("") && !idledMoreThan3Hrs) {
                                txtQRcode.setText(cameraScannedCode);
                            }

                            /*////register screenOnOff BCR
                            RecevierMgr rm = new RecevierMgr();
                            rm.regScreenOnOffReceiver();*/
                            //RecevierMgr.regScreenOnOffReceiverNew();

                            if ((username.length() != 0 && username != "") && (password.length() != 0 && password != "") && !Boolean.parseBoolean(ishomesuser) && !idledMoreThan3Hrs && !objGlobalMethods.IsEmptyString(AdministrativeMode)) {
                                prefName = new String[]{"AdministrativeMode", "forceLogOutBooleanString"};
                                prefvalue = new String[]{"", "true"};
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                JobsHandler.checkForceLogOut();
                            } else if ((username.length() != 0 && username != "") && (password.length() != 0 && password != "") && !Boolean.parseBoolean(ishomesuser) && !idledMoreThan3Hrs) {
                                objGlobalMethods.writeToFile("Attempt to auto Login, !idledMoreThan3Hrs.", "", "", "", "");
                                JobsHandler.checkForceLogOut();
                                checkUsernamePwdValidity();
                            } else if ((username.length() != 0 && username != "") && (password.length() != 0 && password != "") && !Boolean.parseBoolean(ishomesuser) && idledMoreThan3Hrs) {
                                objGlobalMethods.writeToFile("Attempt to auto Login, idledMoreThan3Hrs.", "", "", "", "");
                                check4appUpdates(false);
                            } else if ((username.length() == 0 && username == "") && (password.length() == 0 && password == "") && !Boolean.parseBoolean(ishomesuser)) {
                                if (txtQRcode.getText().toString().isEmpty() || txtQRcode.getText().toString().equals("") || txtQRcode.getText().toString() == "") {
                                    isGPSLocked isgpsl = new isGPSLocked();
                                    isgpsl.execute();
                                }
                            } else if ((username.length() != 0 && username != "") && (password.length() != 0 && password != "") && Boolean.parseBoolean(ishomesuser)) {
                                //more for HOMES user
                                JobsHandler.checkForceLogOut();
                                checkUsernamePwdValidity();
                            }
                        }
                    } else {
                        showErrorDialog(context.getString(R.string.alert_devicenotsupported));
                    }
                }

                /*int x = 2;
                for (int i = x -1; i >=0; i--) {
                    Log.i("test", String.valueOf(i));
                    i++;
                }*/
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at MainActivity onResume. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
            //showEditTextID();

        }
    }

    private String getPermissionName(String permission) {
        switch (permission) {
            case android.Manifest.permission.CAMERA:
                return "Camera";
            case android.Manifest.permission.READ_CONTACTS:
            case android.Manifest.permission.WRITE_CONTACTS:
                return "Contacts";
            case android.Manifest.permission.READ_EXTERNAL_STORAGE:
            case android.Manifest.permission.WRITE_EXTERNAL_STORAGE:
            case android.Manifest.permission.READ_MEDIA_IMAGES:
            case android.Manifest.permission.READ_MEDIA_VIDEO:
            case android.Manifest.permission.READ_MEDIA_AUDIO:
                return "Files/Media";
            case android.Manifest.permission.ACCESS_FINE_LOCATION:
            case android.Manifest.permission.ACCESS_COARSE_LOCATION:
                return "Location (While using app)";
            case android.Manifest.permission.ACCESS_BACKGROUND_LOCATION:
                return "Location (All the time)";
            case android.Manifest.permission.RECORD_AUDIO:
                return "Microphone";
            case android.Manifest.permission.NEARBY_WIFI_DEVICES:
                return "Nearby Devices";
            case android.Manifest.permission.READ_PHONE_STATE:
            case android.Manifest.permission.CALL_PHONE:
            case android.Manifest.permission.READ_CALL_LOG:
            case android.Manifest.permission.WRITE_CALL_LOG:
                return "Phone";
            default:
                return permission;
        }
    }

    private void disableCopyPaste() {
        try {
            txtUsername.setOnTouchListener(null);
            txtPwd.setOnTouchListener(null);

            // For programmatic clipboard protection
            txtUsername.setCustomInsertionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }
            });
        }catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }
    }

    public void initGPS() {
        // Get the location manager
        if (ContextCompat.checkSelfPermission(myApp.getAppContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                if (locationManager == null) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
                    provider = locationManager.getBestProvider(criteria, false);
                    location = locationManager.getLastKnownLocation(provider);
                }

                //location = new Location("test");
                //henderson
                /*location.setLatitude(1.28152583783);
                location.setLongitude(103.819958272);*/
                //toh guan
                /*location.setLatitude(1.340438);
                location.setLongitude(103.746472);*/
                //clementi
                /*location = new Location("test");
                location.setLatitude(1.310965);
                location.setLongitude(103.767274);*/

                // Initialize the location fields
                if (location != null) {
                    onLocationChanged(location);
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1, this);

                objGlobalMethods.clearDialedNumberToHashmap();
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MainActivity onResume Get Location manager. Exception: " + s, "", "", "", "");
            }
        }
    }

    private class isGPSLocked extends AsyncTask<Void, Void, Boolean> {
        isGPSLocked() {

        }

        @Override
        protected void onPreExecute() {
            try {
                objGlobalMethods.writeToFile("isGPSLocked started.", "", "", "", "");
                pdGPSlockedCheck.show();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean asd = false;
            String gpsLocked = "";
            int x = 0;
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            try {
                do {
                    Thread.currentThread();
                    Thread.sleep(1000);
                    gpsLocked = objGlobalMethods.getGCMPrefs(GcmPreferences, "gpsLocked");
                    asd = Boolean.parseBoolean(!gpsLocked.isEmpty() && !gpsLocked.equals("") && gpsLocked != "" ? gpsLocked : "false");
                    x++;
                }
                while ((gpsLocked.isEmpty() || gpsLocked.equals("") || gpsLocked == "") && x < 10);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at MainActivity isGPSLocked doInBackground. Exception: " + e.getMessage().toString(), "", "", "", "");
                asd = false;
            } finally {
                return asd;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean aisGPSlocked) {
            try {
                pdGPSlockedCheck.dismiss();
                Context ctx = myApp.getAppContext();

                if (!aisGPSlocked) {
                    //objGlobalMethods.writeToFile("GPS not locked.", "", "", "", "");
                    AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                    builder.setCancelable(false);
                    String msg = ctx.getString(R.string.alert_gpsNotLocked);
                    builder.setMessage(msg);
                    builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isGPSLocked gpsl = new isGPSLocked();
                            gpsl.execute();
                        }
                    });

                    builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user select "No", just cancel this dialog and continue with app
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    JobsHandler.checkForceLogOut();
                    check4appUpdates(false);
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at MainActivity isGPSLocked onPostExecute, auto login if possible. Exception: " + e.getMessage().toString(), "", "", "", "");
            }
        }
    }

    private void launchQRScan() {
        Intent parentIntent;
        GcmPreferences = getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "mainactivity");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");

        //parentIntent = new Intent(MainActivity.this, QRBarcodeReader.class);
        parentIntent = new Intent(myApp.currentActivity, QRBarcodeReader.class);
        startActivity(parentIntent);
        //finish(); //newly added - comment away 1st
    }

    public void imgbtnQR_Click(View view) {
        launchQRScan();
    }

    private CompoundButton.OnCheckedChangeListener cbHomesUserCheckedChangeListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (LLAttendanceQR != null) {
                        LLAttendanceQR.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    }

                    if (cbApplyLeave != null && cbApplyLeave.isChecked()) {
                        cbApplyLeave.setOnCheckedChangeListener(null);
                        cbApplyLeave.setChecked(false);
                        cbApplyLeave.setOnCheckedChangeListener(cbApplyLeaveCheckedChangeListener());
                    }
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                    objGlobalMethods.writeToFile("Exception at MainActivity cbHomesUserCheckedChangeListener. Exception: " + s, "", "", "", "");
                }
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener cbApplyLeaveCheckedChangeListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (LLAttendanceQR != null) {
                        LLAttendanceQR.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    }

                    if (cbHomesUser != null && cbHomesUser.isChecked()) {
                        cbHomesUser.setOnCheckedChangeListener(null);
                        cbHomesUser.setChecked(false);
                        cbHomesUser.setOnCheckedChangeListener(cbHomesUserCheckedChangeListener());
                    }
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                    objGlobalMethods.writeToFile("Exception at MainActivity cbApplyLeaveCheckedChangeListener. Exception: " + s, "", "", "", "");
                }
            }
        };
    }

    public static void setHendersonLocation(SharedPreferences gcmp) {
        String HendersonLocationPrefStr = "";
        Gson gson = new Gson();
        try {
            /*HendersonLocationPrefStr = objGlobalMethods.getGCMPrefs(gcmp, "HendersonLocationPrefStr");
            if (HendersonLocationPrefStr.isEmpty()) {
                Location HendersonLocation = new Location("HendersonLocation");
                HendersonLocation.setLatitude(1.28152583783);
                HendersonLocation.setLongitude(103.819958272);
                HendersonLocationPrefStr = gson.toJson(HendersonLocation);
            }*/

            HendersonLocationCustom hlc = new HendersonLocationCustom();
            hlc.setLat(1.28152583783);
            hlc.setLng(103.819958272);
            HendersonLocationPrefStr = gson.toJson(hlc);

        } catch (Exception ex) {
            HendersonLocationPrefStr = "";
        } finally {
            objGlobalMethods.setGCMPrefs(gcmp, "HendersonLocationPrefStr", HendersonLocationPrefStr);
        }
    }

    ProgressBarDialog pb;

    public void Login(View view) {
        try {
            /*keantest t = new keantest();
            t.execute();*/
            /*pb = new ProgressBarDialog(myApp.currentActivity);
            pb.setCancelable(false);
            pb.setCanceledOnTouchOutside(false);
            pb.setText("test test");
            pb.show();

            long cTimeMS = new Date().getTime();

            CountDownTimer Timer = new CountDownTimer(3000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //do nothing
                }

                public void onFinish() {
                    if (pb != null && pb.isShowing()) {
                        pb.dismiss();
                        pb = null;
                    }
                }
            }.start();*/


            cd = new ConnectionDetector(null);
            if (context == null) {
                //context = this;
                context = myApp.getAppContext();
            }
            if (txtUsername.getText().length() != 0 && txtUsername.getText().toString() != "") {
                if (txtPwd.getText().length() != 0 && txtPwd.getText().toString() != "") {
                    LoginName = txtUsername.getText().toString();
                    LoginPassword = txtPwd.getText().toString();
                    if (cd.isConnectingToInternet()) {
                        ISHOMESUSER = cbHomesUser.isChecked() ? "true" : "false";
                        statusTV.setText("");
                        if (Boolean.parseBoolean(ISHOMESUSER)) {
                            AsyncCallHomesWS task = new AsyncCallHomesWS(LoginName, LoginPassword);
                            task.execute();
                        } else {
                            regid = FCMservice.getToken();
                            String qr = txtQRcode.getText().toString();
                            if (!qr.isEmpty() && !qr.equals("") && qr != "") {
                                int qrLength = qr.length();
                                if (qrLength < 10 || qrLength > 50) {
                                    showDialog(context.getString(R.string.alertAttendanceQRminVal));
                                } else {
                                    boolean hasXDelCommunalDeviceFile_ = objGlobalMethods.hasXDelCommunalDeviceFile();
                                    GCLogin task = new GCLogin(LoginName, LoginPassword, regid, qr, location, hasXDelCommunalDeviceFile_);
                                    task.execute();
                                }
                            } else {
                                if (location != null) {
                                    boolean hasXDelCommunalDeviceFile_ = objGlobalMethods.hasXDelCommunalDeviceFile();
                                    GCLogin task = new GCLogin(LoginName, LoginPassword, regid, "", location, hasXDelCommunalDeviceFile_);
                                    task.execute();
                                } else if (location == null && LoginName.equals("xd") && LoginPassword.equals("xd")) {
                                    boolean hasXDelCommunalDeviceFile_ = objGlobalMethods.hasXDelCommunalDeviceFile();
                                    GCLogin task = new GCLogin(LoginName, LoginPassword, regid, "", location, hasXDelCommunalDeviceFile_);
                                    task.execute();
                                } else {
                                    initGPS();
                                    showDialog(context.getString(R.string.gpsnotlocked));
                                }
                            }
                        }
                    } else {
                        checkUsernamePwdValidityInOfflineMode();
                    }
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_enterpwd));
                }
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_enterusername));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at MainActivity Login(). Exception: " + e.toString(), "", "", "", "");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        pd.dismiss();

        /*try {
            boolean isInBackground = objGlobalMethods.isApplicationBroughtToBackground();
            if (isInBackground) {
                JobsHandler.sendErrorEmail("MainActivity is in BACKGROUND");
            } else {
                JobsHandler.sendErrorEmail("MainActivity is in FOREGROUND");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*try {
            boolean isInBackground = objGlobalMethods.isApplicationBroughtToBackground();
            if (isInBackground) {
                JobsHandler.sendErrorEmail("MainActivity is in BACKGROUND");
            } else {
                JobsHandler.sendErrorEmail("MainActivity is in FOREGROUND");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            if (UsernamePwd != null) {
                username = UsernamePwd[0];
                password = UsernamePwd[1];
            } else {
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
            }

            if (username != null && username.length() > 0 && password != null && password.length() > 0) {
            } else {
                objGlobalMethods.stopGpsAlarm();
            }
            if (locationManager != null) {
                locationManager.removeUpdates(this);
            }
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");

        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MainActivity onPause. Exception:", "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at MainActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        objGlobalMethods.writeToFile("MainActivity onDestroy called.", "", "", "", "");

        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        if (pddownload != null && pddownload.isShowing()) {
            pddownload.dismiss();
        }
        if (pdGPSlockedCheck != null && pdGPSlockedCheck.isShowing()) {
            pdGPSlockedCheck.dismiss();
        }
        if (pdUpdates != null && pdUpdates.isShowing()) {
            pdUpdates.dismiss();
        }
        if (pdRegID != null) {
            pdRegID.dismiss();
        }

        /*if (imgLogo != null) {
            imgLogo.setImageDrawable(null);
        }
        imgLogo = null;*/

        pd = null;
        pddownload = null;
        pdGPSlockedCheck = null;
        pdUpdates = null;
        pdRegID = null;

        if (linearLayout1 != null) {
            linearLayout1.setOnTouchListener(null);
        }
        linearLayout1 = null;
        LLAttendanceQR = null;
        txtUsername = null;
        txtPwd = null;
        txtQRcode = null;
        cbHomesUser.setOnCheckedChangeListener(null);
        cbHomesUser = null;
        cbApplyLeave.setOnCheckedChangeListener(null);
        cbApplyLeave = null;
        statusTV = null;
        mDisplay = null;
        btnLogin = null;
        cd = null;
        locationManager = null;

        if (GcmPreferences == null) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
        }
        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
        username = UsernamePwd[0];
        password = UsernamePwd[1];
        if (username != null && username.length() > 0 && password != null && password.length() > 0) {
        } else {
            Util.cancelAllJobs(myApp.getAppContext());
            objGlobalMethods.stopGpsAlarm();
            //objGlobalMethods.stopNewPerMinAlarm();
            //objGlobalMethods.stopAlarmForCommon();
            if (FCMservice.mNotificationManager != null) {
                FCMservice.mNotificationManager.cancel(2);
            }
            FCMservice.notiNotRead = false;
            //objGlobalMethods.stopPerMinRunAlarm();
        }
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at MainActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        //objGlobalMethods.clearStaticActivities();
    }

    private void checkUsernamePwdValidity() {
        GcmPreferences = getGcmPreferences();
        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
        username = UsernamePwd[0];
        password = UsernamePwd[1];
        useridx = UsernamePwd[2];
        ishomesuser = UsernamePwd[3];

        if (username.length() != 0 && username != "") {
            if (password.length() != 0 && password != "") {
                LoginName = username;
                LoginPassword = password;
                ISHOMESUSER = ishomesuser;
                statusTV.setText("");
                if (Boolean.parseBoolean(ISHOMESUSER)) {
                    AsyncCallHomesWS task = new AsyncCallHomesWS(LoginName, LoginPassword);
                    task.execute();
                } else {
                    regid = FCMservice.getToken();
                    boolean hasXDelCommunalDeviceFile_ = objGlobalMethods.hasXDelCommunalDeviceFile();
                    GCLogin task = new GCLogin(LoginName, LoginPassword, regid, "", location, hasXDelCommunalDeviceFile_);
                    task.execute();
                }
            }
        } else {

        }
    }

    public static void showActiveNotification2() {
        try {
            objGlobalMethods.startActiveForegroundSvc("");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MainActivity showActiveNotification2. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void showActiveNotification(String name) {
        try {
            Context ctx = myApp.getAppContext();
            FCMservice.mNotificationManager = (NotificationManager)
                    ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, new Intent(ctx, DrawerMainActivity.class), 0);
            Bitmap bm = BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher2);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(ctx)
                            .setSmallIcon(R.mipmap.ic_xdel_noti2)
                            .setLargeIcon(bm)
                            .setContentTitle(name + " " + myApp.getAppContext().getString(R.string.alert_Active2))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(myApp.getAppContext().getString(R.string.alert_touch)))
                            .setWhen(System.currentTimeMillis())
                            .setOngoing(true)
                            .setContentText(myApp.getAppContext().getString(R.string.alert_touch))
                            .setAutoCancel(false);

            mBuilder.setContentIntent(contentIntent);
            FCMservice.mNotificationManager.notify(-1, mBuilder.build());
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
                bm = null;
            }
        } catch (Exception e) {
            //do nothing
        }
    }

    private void checkUsernamePwdValidityInOfflineMode() {
        try {
            GcmPreferences = getGcmPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];

            if (username.length() != 0 && username != "") {
                if (password.length() != 0 && password != "") {
                    if (username.equals(txtUsername.getText().toString()) && password.equals(txtPwd.getText().toString())) {
                        uponLogin = 1;
                        JobsHandler.backEndIsRunning = false;
                        FCMservice.enablePush = true;
                        objGlobalMethods.GPSalarmStarted = false;
                        //Intent intObj = new Intent(MainActivity.this, DrawerMainActivity.class);
                        Intent intObj = new Intent(myApp.currentActivity, DrawerMainActivity.class);
                        startActivity(intObj);
                        //finish(); //newly added - comment away 1st
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_loginfail2));
                    }
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_loginfail2));
                }
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_loginfail2));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at MainActivity checkUsernamePwdValidityInOfflineMode. Exception: " + e.toString(), "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_loginfail2));
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        //int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(myApp.getAppContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                /*GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        objGlobalVariables.PLAY_SERVICES_RESOLUTION_REQUEST).show();*/
                GooglePlayServicesUtil.getErrorDialog(resultCode, myApp.currentActivity,
                        objGlobalVariables.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            String versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private static String getAppVersionName(Context context) {
        try {
            String versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);

        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
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

    //Start showDialog
    private Dialog showDialog(String msg) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setTitle("");
        builder.setMessage(msg);
        builder.setCancelable(false);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    private void showMobileNetworkSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_mobiledatamode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    /*Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                    startActivity(intent);*/

                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                } catch (ActivityNotFoundException e) {
                    Date dt = new Date();
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showMobileNetworkSettingDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showIgnoringBatteryOptimizationsSettingsDialog() {
        try {
            Intent intent = new Intent();
            String packageName = getPackageName();
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MainActivity showIgnoringBatteryOptimizationsSettingsDialog. Exception: " + e, "", "", "", "");
        }
    }

    private void showChangeAirplaneModeSettingsDialog() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_airplanemode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (android.os.Build.VERSION.SDK_INT < 17) {
                    try {
                        Intent intentAirplaneMode = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                        intentAirplaneMode.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentAirplaneMode);
                        //finish(); //newly added - comment away 1st
                    } catch (ActivityNotFoundException e) {
                        Date dt = new Date();
                        objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                    }
                } else {
                    Intent intent1 = new Intent("android.settings.WIRELESS_SETTINGS");
                    intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                    //finish(); //newly added comment away 1st
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeAirplaneModeSettingsDialogNew(final PermissionsManager mgr) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_airplanemode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mgr.requestAirplaneModeOff();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showEnableBTSettingsDialogNew(final PermissionsManager mgr) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_enable_bt));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mgr.requestEnableBluetooth();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showEnableBGandUnrestrictDataUsageSettingsDialogNew(final PermissionsManager mgr) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_enable_bg_n_unrestrict_data));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mgr.requestUnrestrictedDataUsage();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPSModeSettingsDialog() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                    //finish(); //newly added - comment away 1st
                } catch (ActivityNotFoundException e) {
                    Date dt = new Date();
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPStoHighAccModeSettingsDialog() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        //builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmodehighacc));
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmodehighacc2));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                    //finish(); //newly added - comment away 1st
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showWhitelistBattOptimization() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.allow_app_run_background));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    /*Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);*/
                    dialog.dismiss();

                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showWhitelistBattOptimization. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        /*Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);*/
    }



    private void showAllowBackgroundLocationSettingsDialog() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_backgroundlocation_permission));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    //finish(); //newly added - comment away 1st
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showAllowBackgroundLocationSettingsDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangePermissionDialog() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpspermission));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    //finish(); //newly added - comment away 1st
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Dialog showErrorDialog(String msg) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                dialog.dismiss();
                finish();
            }
        });

        return builder.show();
    }

    public class keantest extends AsyncTask<Void, Void, Integer> {

        keantest() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int res = 0;
            try {
                int[] v = new int[]{1, 2, 3, 5};
                test t = new test(true, v);
                Gson gson = new Gson();
                String s = gson.toJson(t);
                res = WebService.keantest3(t);
                //res = Integer.parseInt(WebService.keantest("add"));
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at MainActivity GCLogin doInBackground. " + e.getMessage().toString(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Integer res) {
            String r = "null";
            try {
                pd.dismiss();
                /*if (!objGlobalMethods.IsEmptyString(res)) {
                    r = res;
                }*/
                r = String.valueOf(res);
                objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), r).show();

            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at MainActivity keantest onPostExecute. " + e.getMessage().toString(), "", "", "", "");
            }
        }
    }

    public class GCLogin extends AsyncTask<Void, Void, objAuthDriverResult> {
        private final String aLoginName;
        private final String aPassword;
        private final String aRegID;
        private final String aQRCode;
        private final Location aalocation;
        private final boolean hasXDelCommunalDeviceFile_;

        GCLogin(String aaLoginName, String aaPassword, String aaRegID, String aaQRCode, Location aaalocation, boolean ahasXDelCommunalDeviceFile_) {
            this.aLoginName = aaLoginName;
            this.aPassword = aaPassword;
            this.aRegID = aaRegID;
            this.aQRCode = aaQRCode;
            this.aalocation = aaalocation;
            this.hasXDelCommunalDeviceFile_ = ahasXDelCommunalDeviceFile_;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected objAuthDriverResult doInBackground(Void... params) {
            objAuthDriverResult res = null;
            try {
                Date dt = new Date();
                Long l = dt.getTime();
                String timestampstr = "/Date(" + l + ")/";

                Location lastknownlocation = aalocation;
                int Accuracy = lastknownlocation == null ? 0 : Math.round(lastknownlocation.getAccuracy() * 100);
                objAuthRequest req = new objAuthRequest(aLoginName, aPassword, aRegID, timestampstr,
                        lastknownlocation == null ? 0 : lastknownlocation.getLatitude(), lastknownlocation == null ? 0 : lastknownlocation.getLongitude(), Accuracy, aQRCode);

                req.setCommunalDevice(hasXDelCommunalDeviceFile_);
                req.setFlag(cbApplyLeave.isChecked() ? 1 : 0); //<Flag>Regular or AdministrativeMode or Unused1 or Unused2</Flag>
                res = WebService.getGCLoginStatus(req);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at MainActivity GCLogin doInBackground. " + e.getMessage().toString(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final objAuthDriverResult res) {
            try {
                pd.dismiss();

                if (GcmPreferences == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                }
                langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                if (!errored) {
                    if (res != null) {
                        if (res.Status >= 0) {
                            //login succ
                            String[] prefName = new String[]{"forceLogOutBooleanString", "connectionState", "wifistate", "gpsLocked", "cameraScannedCode", "disablePush"};
                            String[] prefvalue = new String[]{"false", "true", "", "", "", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                            Gson gson = new Gson();
                            //store Login Username
                            JobsHandler.storeLogin(GcmPreferences, aLoginName, aPassword, String.valueOf(res.DriverIDX), ISHOMESUSER);
                            //Navigate to Dashboard Screen
                            username = aLoginName;
                            useridx = String.valueOf(res.DriverIDX);
                            prevLoginUserIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, "prevLoginUserIDX");

                            String gcFullName = "";
                            String gcImgb64 = "";
                            String gcID = "";
                            String maskedgcID = "";
                            String probationGC = "";
                            String probationPeriod = "";

                            if (res.FullName != null && !res.FullName.isEmpty() && !res.FullName.equals("")) {
                                gcFullName = res.FullName;
                            }
                            if (res.Image != null && res.Image.length > 0) {
                                gcImgb64 = gson.toJson(res.Image);
                            }
                            if (res.ID != null && !res.ID.isEmpty() && !res.ID.isEmpty()) {
                                gcID = res.ID;
                                maskedgcID = objGlobalMethods.maskID(gcID);
                            }
                            probationGC = ((res.Status & 32) == 32) ? "y" : "";
                            probationPeriod = ((res.Status & 32) == 32) ? res.DateConfirmed : "";

                            /*probationGC = "y";
                            probationPeriod = "/Date(1695772800000+0800)/";*/

                            if (!useridx.equals(prevLoginUserIDX)) {
                                String[] pName = new String[]{"ChatMsgSendersALJsonStr", "sendMessagesArrPrefStr", "FullName", "Image", "ID", "MaskedID",
                                        "prevLoginUserName", "prevLoginUserIDX", "probationGC", "probationPeriod", "DriverFlag"};
                                String[] pvalue = new String[]{"", "", gcFullName, gcImgb64, gcID, maskedgcID,
                                        username, useridx, probationGC, probationPeriod, String.valueOf(res.Flag == null ? 0 : res.Flag)};
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);
                            } else {
                                objAuthDriverResult prevRes = objGlobalMethods.getGCdetails(GcmPreferences);
                                String prevName = prevRes.getFullName();
                                String prevImg = objGlobalMethods.getGCMPrefs(GcmPreferences, "Image");
                                String prevGcID = prevRes.getID();
                                String prevprobationGC = prevRes.probationGC;
                                String prevprobationPeriod = prevRes.DateConfirmed;
                                Integer DriverFlag = res.Flag == null ? 0 : res.Flag;
                                //driver flag 524288 show passport checkout in profile
                                //driver flag 262144 require GC to scan their pp to verify they received from ops b4, else ops cannot check-in their pp

                                gcFullName = !gcFullName.isEmpty() && !gcFullName.equals("") ? gcFullName : !prevName.isEmpty() && !prevName.equals("") ? prevName : gcFullName;
                                if (objGlobalMethods.IsEmptyString(probationGC)
                                        && !objGlobalMethods.IsEmptyString(prevprobationGC)
                                        && !objGlobalMethods.IsEmptyString(prevprobationPeriod)
                                        && !JobsHandler.isOneOrMoreDaysEarlierByJsonDate(prevprobationPeriod)) {
                                    probationGC = prevprobationGC;
                                    probationPeriod = prevprobationPeriod;
                                }
                                /*probationGC = !objGlobalMethods.IsEmptyString(probationGC) ? probationGC : !objGlobalMethods.IsEmptyString(prevprobationGC) ? prevprobationGC : probationGC;
                                probationPeriod = !objGlobalMethods.IsEmptyString(probationPeriod) ? probationPeriod : !objGlobalMethods.IsEmptyString(prevprobationPeriod) ? prevprobationPeriod : probationPeriod;*/
                                if (objGlobalMethods.IsEmptyString(gcImgb64)) {
                                    gcImgb64 = !objGlobalMethods.IsEmptyString(prevImg) ? prevImg : "";
                                }

                                gcID = !objGlobalMethods.IsEmptyString(gcID) ? gcID : !objGlobalMethods.IsEmptyString(prevGcID) ? prevGcID : gcID;
                                maskedgcID = objGlobalMethods.maskID(gcID);
                                String[] pName = new String[]{"FullName", "Image", "ID", "MaskedID", "prevLoginUserName", "prevLoginUserIDX", "probationGC", "probationPeriod", "DriverFlag"};
                                String[] pvalue = new String[]{gcFullName, gcImgb64, gcID, maskedgcID, username, useridx, probationGC, probationPeriod, String.valueOf(DriverFlag)};
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);
                            }

                            objGlobalMethods.writeToFile(username + ", " + String.valueOf(useridx) + " Logged in. Luke Version: " + getAppVersionName(context), "", "", username, useridx);
                            uponLogin = 1;

                            JobsHandler.backEndIsRunning = false;
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                showActiveNotification2();
                            } else {
                                showActiveNotification(username);
                            }

                            FCMservice.enablePush = true;
                            objGlobalMethods.GPSalarmStarted = false;
                            String prevDTJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "prevDTJsonStr");
                            if (prevDTJsonStr.isEmpty() || prevDTJsonStr.equals("") || prevDTJsonStr == "") {
                                JobsHandler.setCurrDTasPrevDTforIdlingCheck();
                            }

                            if ((res.Status & 16) == 16) {
                                //<Flag>Regular or AdministrativeMode or Unused1 or Unused2</Flag>
                                objGlobalMethods.writeToFile("Flag for AdministrativeMode. Use to apply leave only.", "", "", "", "");
                            }
                            if ((res.Status & 8) == 8) {
                                objGlobalMethods.writeToFile("Flag to override to skip DA.", "", "", "", "");
                            }
                            if ((res.Status & 4) == 4) {
                                objGlobalMethods.writeToFile("Flag to prompt force read DA.", "", "", "", "");
                            }
                            if ((res.Status & 1) == 1) {
                                objGlobalMethods.writeToFile("Flag to prompt advise to change pwd.", "", "", "", "");
                            }
                            if ((res.Status & 2) == 2) {
                                objGlobalMethods.writeToFile("Flag to prompt force change pwd.", "", "", "", "");
                            }
                            if ((res.Status & 32) == 32) {
                                objGlobalMethods.writeToFile("Flag indicated probation GC.", "", "", "", "");
                            }

                            /*objGlobalMethods.setGCMPrefs(GcmPreferences, "pwdchangeflag", (res.Status & 1) == 1 ? "1" : (res.Status & 2) == 2 ? "2" : "");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "ext2000", "");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "ext3000", "");*/

                            String[] pName = new String[]{"pwdchangeflag", "ext2000", "ext3000"};
                            String[] pvalue = new String[]{
                                    (res.Status & 1) == 1 ? "1" : (res.Status & 2) == 2 ? "2" : "",
                                    "",
                                    ""
                            };
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);

                            //res.Status = 0; //added here to skip change pwd prompt
                            if ((((res.Status & 1) == 1) || ((res.Status & 2) == 2)) && (res.DriverIDX == 934 || res.DriverIDX == 1767 || res.DriverIDX == 1294 || res.DriverIDX == 2084)) {
                                ////purposely to evade password change 1st
                                if ((res.Status & 1) == 1) {
                                    res.Status += -1;
                                }
                                if ((res.Status & 2) == 2) {
                                    res.Status += -2;
                                }
                            }
                            //res.Status = 0; //added here to skip change pwd prompt

                            ////the following is for direct GC to change pwd
                            //final Intent intObj = new Intent(MainActivity.this, res.Status == 2 ? ChangePwdActivity.class : (objGlobalMethods.IsDefaultVRPpage() ? DrawerMain2Activity.class : DrawerMainActivity.class));

                            ///as of version 3.1.1
                            //final Intent intObj = new Intent(MainActivity.this, (objGlobalMethods.IsDefaultVRPpage() ? DrawerMain2Activity.class : DrawerMainActivity.class));

                            //as of version 3.1.2
                            //////((res.Status & 4) == 4) means force to read DA
                            //////((res.Status & 8) == 8) means ops override skip DA
                            boolean showDigiAgreement = (res.DriverIDX == 2084) || ((objGlobalMethods.showDigiAgreement() && !hasXDelCommunalDeviceFile_) || ((res.Status & 4) == 4)) && !((res.Status & 8) == 8);
                            //final Intent intObj = new Intent(myApp.currentActivity, showDigiAgreement ? DigitalAgreementActivity.class : (objGlobalMethods.IsDefaultVRPpage() ? DrawerMain2Activity.class : DrawerMainActivity.class));
                            Intent intent = new Intent(myApp.currentActivity,
                                    ((res.Status & 2) == 2) ? ChangePwdActivity.class :
                                            showDigiAgreement ? DigitalAgreementActivity.class :
                                                    (objGlobalMethods.IsDefaultVRPpage() && !IsMaintenanceHours ? DrawerMain2Activity.class : DrawerMainActivity.class));
                            final Intent intObj = intent;

                            LocationService.mLastLocationReceived = aalocation;

                            locationManager.removeUpdates(MainActivity.this);
                            locationManager = null;
                            location = null;
                            txtUsername.setText("");
                            txtPwd.setText("");
                            txtQRcode.setText("");

                            //objGlobalMethods.setGCMPrefs(GcmPreferences, "AdministrativeMode", ((res.Status & 16) == 16) ? "t" : "");

                            String[] ppName = new String[]{"AdministrativeMode", "TempLastRecorded", "TempLastRecordedTime", "mTempLastRecorded", "mTempLastRecordedTime",
                                    "mtempLoggerPhotoJsonStr", "L2_GetJobTemperaturesJsonStr", "L2_GetJobTemperaturesLastRecordedTime"};
                            String[] ppvalue = new String[]{((res.Status & 16) == 16) ? "t" : "", "", "", "", "", "", "", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, ppName, ppvalue);

                            /*////register screenOnOff BCR
                            RecevierMgr rm = new RecevierMgr();
                            rm.regScreenOnOffReceiver();*/

                            if ((res.Status & 16) == 16) {
                                //objGlobalMethods.setGCMPrefs(GcmPreferences, "AdministrativeMode", "t");
                                //Intent AdminModeIntent = new Intent(myApp.currentActivity, LeaveActivity.class);
                                Intent AdminModeIntent = new Intent(myApp.currentActivity, ProfileActivity.class);
                                startActivity(AdminModeIntent);
                            } else if (((res.Status & 1) == 1) || ((res.Status & 2) == 2)) {
                                final Context ctx = myApp.getAppContext();
                                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                builder.setCancelable(false);
                                builder.setMessage(((res.Status & 1) == 1) ? ctx.getString(R.string.changepwd_advice) : ctx.getString(R.string.changepwd_compulsory));
                                builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(intObj);
                                        //finish(); //newly added - comment away 1st
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                startActivity(intObj);
                                //finish(); //newly added - comment away 1st
                            }

                            /*if (res.Status > 0) {
                                final Context ctx = myApp.getAppContext();
                                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                builder.setCancelable(false);
                                builder.setMessage(res.Status == 1 ? ctx.getString(R.string.changepwd_advice) : ctx.getString(R.string.changepwd_compulsory));
                                builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(intObj);
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                startActivity(intObj);
                            }*/
                        } else if (res.Status < 0) {
                            //err
                            JobsHandler.backEndIsRunning = false;
                            FCMservice.enablePush = false;
                            UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
                            //objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");

                            String[] pName = new String[]{"cameraScannedCode", "TempLastRecorded", "TempLastRecordedTime", "mTempLastRecorded", "mTempLastRecordedTime",
                                    "mtempLoggerPhotoJsonStr", "L2_GetJobTemperaturesJsonStr", "L2_GetJobTemperaturesLastRecordedTime"};
                            String[] pvalue = new String[]{"", "", "", "", "", "", "", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);

                            txtQRcode.setText("");
                            JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
                            showDialog(langType.equals("zh") ? res.Message_C : res.Message);
                        }
                    } else {
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                        username = UsernamePwd[0];
                        password = UsernamePwd[1];
                        useridx = UsernamePwd[2];
                        ishomesuser = UsernamePwd[3];
                        if (!objGlobalMethods.IsEmptyString(username) && !objGlobalMethods.IsEmptyString(password) && !Boolean.parseBoolean(ishomesuser)) {
                            Intent intent = new Intent(myApp.currentActivity, objGlobalMethods.IsDefaultVRPpage() && !IsMaintenanceHours ? DrawerMain2Activity.class : DrawerMainActivity.class);
                            objGlobalMethods.writeToFile("Logging in has error. Res is null. Logged in before, auto login now. Luke Version: " + getAppVersionName(context),
                                    "", "", username, useridx);
                            uponLogin = 1;
                            JobsHandler.backEndIsRunning = false;
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                showActiveNotification2();
                            } else {
                                showActiveNotification(username);
                            }

                            String[] pName = new String[]{"disablePush", "ext2000", "ext3000", "AdministrativeMode", "TempLastRecorded", "TempLastRecordedTime", "mTempLastRecorded", "mTempLastRecordedTime",
                                    "mtempLoggerPhotoJsonStr", "L2_GetJobTemperaturesJsonStr", "L2_GetJobTemperaturesLastRecordedTime"};
                            String[] pvalue = new String[]{"", "", "", "", "", "", "", "", "", "", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);

                            /*////register screenOnOff BCR
                            RecevierMgr rm = new RecevierMgr();
                            rm.regScreenOnOffReceiver();*/

                            FCMservice.enablePush = true;
                            objGlobalMethods.GPSalarmStarted = false;
                            startActivity(intent);
                            finish();
                        } else {
                            JobsHandler.backEndIsRunning = false;
                            FCMservice.enablePush = false;
                            UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
                            //objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");

                            String[] pName = new String[]{"cameraScannedCode", "TempLastRecorded", "TempLastRecordedTime", "mTempLastRecorded", "mTempLastRecordedTime",
                                    "mtempLoggerPhotoJsonStr", "L2_GetJobTemperaturesJsonStr", "L2_GetJobTemperaturesLastRecordedTime"};
                            String[] pvalue = new String[]{"", "", "", "", "", "", "", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);

                            txtQRcode.setText("");
                            JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
                            showDialog(myApp.getAppContext().getString(R.string.alert_loginfailqr));
                        }
                    }
                } else {
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                    username = UsernamePwd[0];
                    password = UsernamePwd[1];
                    useridx = UsernamePwd[2];
                    ishomesuser = UsernamePwd[3];

                    if (!objGlobalMethods.IsEmptyString(username) && !objGlobalMethods.IsEmptyString(password) && !Boolean.parseBoolean(ishomesuser)) {
                        //Intent intent = new Intent(myApp.getAppContext(), objGlobalMethods.IsDefaultVRPpage() ? DrawerMain2Activity.class : DrawerMainActivity.class);
                        Intent intent = new Intent(myApp.currentActivity, objGlobalMethods.IsDefaultVRPpage() && !IsMaintenanceHours ? DrawerMain2Activity.class : DrawerMainActivity.class);
                        objGlobalMethods.writeToFile("Logging in has error. Logged in before, auto login now. Luke Version: " + getAppVersionName(context),
                                "", "", username, useridx);
                        uponLogin = 1;
                        JobsHandler.backEndIsRunning = false;
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            showActiveNotification2();
                        } else {
                            showActiveNotification(username);
                        }

                        String[] pName = new String[]{"disablePush", "ext2000", "ext3000", "AdministrativeMode", "TempLastRecorded", "TempLastRecordedTime", "mTempLastRecorded", "mTempLastRecordedTime",
                                "mtempLoggerPhotoJsonStr", "L2_GetJobTemperaturesJsonStr", "L2_GetJobTemperaturesLastRecordedTime"};
                        String[] pvalue = new String[]{"", "", "", "", "", "", "", "", "", "", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);

                        /*////register screenOnOff BCR
                        RecevierMgr rm = new RecevierMgr();
                        rm.regScreenOnOffReceiver();*/

                        FCMservice.enablePush = true;
                        objGlobalMethods.GPSalarmStarted = false;
                        startActivity(intent);
                        finish();
                    } else {
                        JobsHandler.backEndIsRunning = false;
                        FCMservice.enablePush = false;
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        txtQRcode.setText("");
                        showDialog(myApp.getAppContext().getString(R.string.alert_errorInvoking));
                    }
                }
                errored = false;
            } catch (Exception e) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                txtQRcode.setText("");
                objGlobalMethods.writeToFile("Exception caught at MainActivity GCLogin onPostExecute. " + e.getMessage().toString(), "", "", "", "");
            }
        }
    }

    public class AsyncCallHomesWS extends AsyncTask<Void, Void, Boolean> {

        private final String loginname;
        private final String password;

        AsyncCallHomesWS(String aloginname, String apassword) {
            this.loginname = aloginname;
            this.password = apassword;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //android.os.Debug.waitForDebugger();
            //Call Web Method
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            LoginSuccess = WebService.getHomesLoginStatus(loginname, password, "AuthUser");
            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean success) {
            pd.dismiss();
            //Intent intObj = new Intent(MainActivity.this, DrawerMainActivity.class);
            Intent intObj = new Intent(myApp.currentActivity, DrawerMainActivity.class);
            //Error status is false
            if (!errored) {
                //Based on Boolean value returned from WebService
                if (LoginSuccess > 0) {
                    //store Login Username
                    GcmPreferences = getGcmPreferences();
                    JobsHandler.storeLogin(GcmPreferences, loginname, password, String.valueOf(LoginSuccess), ISHOMESUSER);

                    String[] prefName = new String[]{"connectionState", "prevLoginUserName", "pagemode", "hasOpsQue", "isVRPbySettings", "DriverFlag"};
                    String[] prefvalue = new String[]{"true", loginname, "dashboard", "", "", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    //Navigate to Dashboard Screen
                    uponLogin = 1;
                    JobsHandler.backEndIsRunning = false;
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        showActiveNotification2();
                    } else {
                        showActiveNotification(username);
                    }
                    String prevDTJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "prevDTJsonStr");
                    if (prevDTJsonStr.isEmpty() || prevDTJsonStr.equals("") || prevDTJsonStr == "") {
                        JobsHandler.setCurrDTasPrevDTforIdlingCheck();
                    }
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "disablePush", "");
                    FCMservice.enablePush = true;
                    objGlobalMethods.writeToFile(loginname + ", " + String.valueOf(LoginSuccess) + " Logged in. Luke Version: " + getAppVersionName(context), "", "", "", "");
                    objGlobalMethods.GPSalarmStarted = false;
                    startActivity(intObj);
                    //finish(); //newly added - comment away 1st
                } else {
                    //Set Error message
                    JobsHandler.backEndIsRunning = false;
                    FCMservice.enablePush = false;
                    showDialog(myApp.getAppContext().getString(R.string.alert_loginfail));
                }
                //Error status is true
            } else {
                JobsHandler.backEndIsRunning = false;
                FCMservice.enablePush = false;
                showDialog(myApp.getAppContext().getString(R.string.alert_errorInvoking));
            }
            //Re-initialize Error Status to False
            errored = false;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void ForceLoginHomesUser(String ausername) {
        try {
            Intent intObj = new Intent(myApp.currentActivity, DrawerMainActivity.class);
            String[] prefName = new String[]{"connectionState", "prevLoginUserName", "pagemode", "hasOpsQue", "isVRPbySettings", "DriverFlag"};
            String[] prefvalue = new String[]{"true", ausername, "dashboard", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            //Navigate to Dashboard Screen
            uponLogin = 1;
            JobsHandler.backEndIsRunning = false;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showActiveNotification2();
            } else {
                showActiveNotification(ausername);
            }
            String prevDTJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "prevDTJsonStr");
            if (prevDTJsonStr.isEmpty() || prevDTJsonStr.equals("") || prevDTJsonStr == "") {
                JobsHandler.setCurrDTasPrevDTforIdlingCheck();
            }
            objGlobalMethods.setGCMPrefs(GcmPreferences, "disablePush", "");
            FCMservice.enablePush = true;
            objGlobalMethods.writeToFile(ausername + " ForceLoginHomesUser" +
                    " Logged in. Luke Version: " + getAppVersionName(context), "", "", "", "");
            objGlobalMethods.GPSalarmStarted = false;
            startActivity(intObj);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MainActivity ForceLoginHomesUser. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.act_gps) {
            locationManager = null;
            provider = null;
            location = null;
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            objGlobalMethods.setGCMPrefs(GcmPreferences, "gpsLocked", "");
            objGlobalMethods.writeToFile("Reget GPS", "", "", "", "");
            initGPS();

            isGPSLocked isgpsl = new isGPSLocked();
            isgpsl.execute();

                /*Intent i = new Intent(this, TempLoggerWhseScanActivity.class);
                startActivity(i);*/
        } else if (id == R.id.exit) {
                /*Intent i = new Intent(myApp.currentActivity, ppMainActivity.class);
                startActivity(i);*/
            moveTaskToBack(true);

            //objGlobalMethods.setVrpRelatedSP_KeyValue2(objGlobalMethods.getVrpRelatedSharedPreferences(), "credEditedDateLong", "1754462264929");

                /*Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);*/
            ////finish(); //newly added - comment away 1st

                /*try {
                    // Launch Waze to look for Hawaii:
                    //if waze not installed, will open browser
                    //better to check if waze is stalled first
                    String LATstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LAT");
                    String LNGstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LNG");
                    String ll = LATstr + "," + LNGstr;
                    if (JobsHandler2.wazeInstalled()) {
                        //String url = "https://waze.com/ul?ll=1.32435477286284,103.802951580752&navigate=yes";
                        //String url = "https://waze.com/ul?ll=" + ll + "&navigate=yes"; //by lat lng
                        String url = "https://waze.com/ul?q=singapore 680114&navigate=yes"; //by lat lng
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                        startActivity(intent);
                    }
                } catch (ActivityNotFoundException ex) {
                    // If Waze is not installed, open it in Google Play:
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                    startActivity(intent);
                }*/

        } else if (id == R.id.act_language) {
            GcmPreferences = getGcmPreferences();
            langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
            if (langType.equals("en") || langType.equals("")) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "zh");
                LocaleHelper.setSelectedLanguageId("zh");
            } else if (langType.equals("zh")) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "en");
                LocaleHelper.setSelectedLanguageId("en");
            }
            this.recreate();
        }

        /*switch (item.getItemId()) {
            case R.id.act_gps:
                locationManager = null;
                provider = null;
                location = null;
                if (GcmPreferences == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                }
                objGlobalMethods.setGCMPrefs(GcmPreferences, "gpsLocked", "");
                objGlobalMethods.writeToFile("Reget GPS", "", "", "", "");
                initGPS();

                isGPSLocked isgpsl = new isGPSLocked();
                isgpsl.execute();

                *//*Intent i = new Intent(this, TempLoggerWhseScanActivity.class);
                startActivity(i);*//*
                break;
            case R.id.exit:
                *//*Intent i = new Intent(myApp.currentActivity, ppMainActivity.class);
                startActivity(i);*//*
                moveTaskToBack(true);

                //objGlobalMethods.setVrpRelatedSP_KeyValue2(objGlobalMethods.getVrpRelatedSharedPreferences(), "credEditedDateLong", "1754462264929");

                *//*Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);*//*
                ////finish(); //newly added - comment away 1st

                *//*try {
                    // Launch Waze to look for Hawaii:
                    //if waze not installed, will open browser
                    //better to check if waze is stalled first
                    String LATstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LAT");
                    String LNGstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LNG");
                    String ll = LATstr + "," + LNGstr;
                    if (JobsHandler2.wazeInstalled()) {
                        //String url = "https://waze.com/ul?ll=1.32435477286284,103.802951580752&navigate=yes";
                        //String url = "https://waze.com/ul?ll=" + ll + "&navigate=yes"; //by lat lng
                        String url = "https://waze.com/ul?q=singapore 680114&navigate=yes"; //by lat lng
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                        startActivity(intent);
                    }
                } catch (ActivityNotFoundException ex) {
                    // If Waze is not installed, open it in Google Play:
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                    startActivity(intent);
                }*//*

                break;
            case R.id.act_language:
                GcmPreferences = getGcmPreferences();
                langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
                if (langType.equals("en") || langType.equals("")) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "zh");
                    LocaleHelper.setSelectedLanguageId("zh");
                } else if (langType.equals("zh")) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "en");
                    LocaleHelper.setSelectedLanguageId("en");
                }
                this.recreate();
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        //finish(); //newly added - comment away 1st

    }

    public void openPlayStoreUpdateApp() {
        String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void check4appUpdates(boolean checkvalidity) {
        Date dt = new Date();
        Boolean IsManuallyInstalled = false;
        int verCode;
        String verName = "";
        String verNameLastValue = "";
        boolean isDebuggable = false;
        try {
            verCode = getAppVersion(myApp.getAppContext());
            verName = getAppVersionName(myApp.getAppContext());
            verNameLastValue = verName.substring(verName.length() - 1, verName.length());

            try {
                isDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
                IsManuallyInstalled = isDebuggable;
                //IsManuallyInstalled = false;
            } catch (Exception e) {
                IsManuallyInstalled = false;
            }

            check4AppUpdatesAsync task = new check4AppUpdatesAsync(IsManuallyInstalled, verCode, checkvalidity);
            task.execute();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at MainActivity check4appUpdates. Calling checkUsernamePwdValidity. Exception: " + e.getMessage().toString(), "", "", "", "");
            checkUsernamePwdValidity();
        }
    }

    private class check4AppUpdatesAsync extends AsyncTask<Void, Void, objUpdateInfo> {
        private final Boolean IsManuallyInstalled;
        private final int InstalledVersionCode;
        private final boolean checkvalidity;
        private String aURL = "";

        check4AppUpdatesAsync(Boolean aIsManuallyInstalled, int aInstalledVersionCode, boolean acheckvalidity) {
            this.IsManuallyInstalled = aIsManuallyInstalled;
            this.InstalledVersionCode = aInstalledVersionCode;
            this.checkvalidity = acheckvalidity;
        }

        @Override
        protected void onPreExecute() {
            pdUpdates.show();
        }

        @Override
        protected objUpdateInfo doInBackground(Void... params) {
            objUpdateInfo objUpdateInfoobj = null;
            try {
                objUpdateInfoobj = WebService.GetUpdate(IsManuallyInstalled, InstalledVersionCode);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at MainActivity check4AppUpdatesAsync doInBackground. Exception: " + e.getMessage().toString(), "", "", "", "");
                objUpdateInfoobj = null;
            } finally {
                return objUpdateInfoobj;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final objUpdateInfo success) {
            pdUpdates.dismiss();
            GcmPreferences = getGcmPreferences();
            try {

                if (success != null) {
                    if (IsManuallyInstalled) {
                        //success.IsAvailable = true;
                        if (success.IsAvailable) {
                            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                            username = UsernamePwd[0];
                            password = UsernamePwd[1];
                            useridx = UsernamePwd[2];
                            ishomesuser = UsernamePwd[3];

                            if (username != null && username.length() > 0 && password != null && password.length() > 0) {
                                objGlobalMethods.writeToFile("New Update available. Clear database and log out from server.", "", "", "", "");
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "forceLogOutBooleanString", "true");
                                Context ctx = myApp.getAppContext();
                                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                            }

                            aURL = success.Url;
                            // execute this when the downloader must be fired
                            /*if (ContextCompat.checkSelfPermission(myApp.getAppContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                final DownloadTask2 downloadTask = new DownloadTask2(myApp.getAppContext(), success.Url);
                                downloadTask.execute(aURL);

                                pdUpdates.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        downloadTask.cancel(true);
                                    }
                                });
                            }*/
                            if (ContextCompat.checkSelfPermission(myApp.getAppContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                final DownloadTask_akinori downloadTask = new DownloadTask_akinori(myApp.getAppContext(), success.Url);
                                downloadTask.execute(akinori_download_link);

                                pdUpdates.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        downloadTask.cancel(true);
                                    }
                                });
                            }
                        } else {
                            if (checkvalidity) {
                                checkUsernamePwdValidity();
                            } else {

                            }
                        }
                    } else {
                        //success.IsAvailable = true;
                        if (success.IsAvailable) {
                            try {
                                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                                username = UsernamePwd[0];
                                password = UsernamePwd[1];
                                useridx = UsernamePwd[2];
                                ishomesuser = UsernamePwd[3];

                                if (username != null && username.length() > 0 && password != null && password.length() > 0) {
                                    objGlobalMethods.writeToFile("New Update available. Clear database and log out from server.", "", "", "", "");
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "forceLogOutBooleanString", "true");
                                    Context ctx = myApp.getAppContext();
                                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                                }

                                //AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                builder.setCancelable(false);
                                builder.setMessage(myApp.getAppContext().getString(R.string.alertUpgradeREQ));

                                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //if user pressed "ok", open playstore.
                                        String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } catch (android.content.ActivityNotFoundException anfe) {
                                String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        } else {
                            if (checkvalidity) {
                                checkUsernamePwdValidity();
                            }
                        }
                    }
                } else {
                    if (checkvalidity) {
                        checkUsernamePwdValidity();
                    } else {

                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at MainActivity check4AppUpdatesAsync onPostExecute, auto login if possible. Exception: " + e.getMessage().toString(), "", "", "", "");
                checkUsernamePwdValidity();
            }
        }
    }

    private class testdownloadprogressdialog extends AsyncTask<Boolean, Integer, Boolean> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public testdownloadprogressdialog(Context ctx) {
            this.context = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            pddownload.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            /*pdUpdates.setIndeterminate(false);
            pdUpdates.setMax(100);
            pdUpdates.setProgress(progress[0]);*/
            pddownload.setProgress(progress[0]);
        }

        @Override
        protected Boolean doInBackground(Boolean... ress) {
            Boolean res = false;
            try {
                long total = 0;
                for (int i = 0; i <= 100; i++) {
                    total++;
                    publishProgress((int) ((total * 100) / 100));
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                res = false;
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(Boolean res) {
            mWakeLock.release();
            pddownload.dismiss();

        }
    }

    private class DownloadTask_akinori extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private final String l2apklink;

        public DownloadTask_akinori(Context ctx, String al2apklink) {
            this.context = ctx;
            this.l2apklink = al2apklink;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            pddownload.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            /*pdUpdates.setIndeterminate(false);
            pdUpdates.setMax(100);
            pdUpdates.setProgress(progress[0]);*/
            pddownload.setProgress(progress[0]);
        }

        @Override
        protected String doInBackground(String... sUrl) {
            String res = null;
            int count;
            InputStream input = null;
            OutputStream output = null;
            try {
                URL url = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                objGlobalMethods.writeToFile("Begin download new APK from akinori.", "", "", "", "");
                // getting file length
                int lengthOfFile = connection.getContentLength();
                // input stream to read file - with 8k buffer
                input = new BufferedInputStream(url.openStream(), 8192);
                output = new FileOutputStream("/sdcard/app-debug.apk");

                byte data[] = new byte[4096];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress((int) ((total * 100) / lengthOfFile));
                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                res = "";
            } catch (Exception e) {
                String s = e.getMessage();
                objGlobalMethods.writeToFile("Exception at MainActivity DownloadTask_akinori doInBackground. Exception: " + s, "", "", "", "");
                res = null;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                return res;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            pddownload.dismiss();
            if (pdUpdates != null && pdUpdates.isShowing()) {
                pdUpdates.dismiss();
            }
            if (result == null) {
                final DownloadTask2 downloadTask = new DownloadTask2(myApp.getAppContext(), l2apklink);
                downloadTask.execute(l2apklink);

                pdUpdates.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        downloadTask.cancel(true);
                    }
                });
            } else {
                Toast.makeText(context, "File downloaded. Installing...", Toast.LENGTH_SHORT).show();
                try {
                    objGlobalMethods.writeToFile("New APK from L2APK downloaded successfully. Begins to prompt install.", "", "", "", "");
                    //Intent intent = new Intent(Intent.ACTION_VIEW);
                    Intent intent = null;
                    File externalStorageDir = Environment.getExternalStorageDirectory();
                    File myFile = new File(externalStorageDir, "app-debug.apk");
                    Uri uri = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName(), myFile);
                        intent.setData(uri);
                        startActivity(intent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        uri = Uri.fromFile(myFile);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception caught at MainActivity DownloadTask onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
                    finish();
                }
            }
        }


    }

    private class DownloadTask2 extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private final String l2apklink;

        public DownloadTask2(Context ctx, String al2apklink) {
            this.context = ctx;
            this.l2apklink = al2apklink;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            pddownload.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            /*pdUpdates.setIndeterminate(false);
            pdUpdates.setMax(100);
            pdUpdates.setProgress(progress[0]);*/
            pddownload.setProgress(progress[0]);
        }

        @Override
        protected String doInBackground(String... sUrl) {
            String res = null;
            int count;
            InputStream input = null;
            OutputStream output = null;
            try {
                URL url = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                objGlobalMethods.writeToFile("Begin download new APK from L2APK.", "", "", "", "");
                // getting file length
                int lengthOfFile = connection.getContentLength();
                // input stream to read file - with 8k buffer
                input = new BufferedInputStream(url.openStream(), 8192);
                output = new FileOutputStream("/sdcard/app-debug.apk");

                byte data[] = new byte[4096];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress((int) ((total * 100) / lengthOfFile));
                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                res = "";
            } catch (Exception e) {
                String s = e.getMessage();
                objGlobalMethods.writeToFile("Exception at MainActivity DownloadTask2 doInBackground. Exception: " + s, "", "", "", "");
                res = null;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                return res;
            }

            /*try {
                URL url = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                objGlobalMethods.writeToFile("Begin download new APK from L2APK.", "", "", "", "");
                // getting file length
                int lengthOfFile = connection.getContentLength();
                // input stream to read file - with 8k buffer
                input = new BufferedInputStream(url.openStream(), 8192);
                output = new FileOutputStream("/sdcard/app-debug.apk");

                byte data[] = new byte[4096];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress((int) ((total * 100) / lengthOfFile));
                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                res = "";

            } catch (Exception e) {
                String s = e.getMessage();
                objGlobalMethods.writeToFile("Exception at MainActivity DownloadTask2 doInBackground. Exception: " + s, "", "", "", "");
                res = null;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                return res;
            }*/
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            pddownload.dismiss();
            if (result == null)
                Toast.makeText(context, "Download error", Toast.LENGTH_LONG).show();
            else {
                Toast.makeText(context, "File downloaded. Installing...", Toast.LENGTH_SHORT).show();
                try {
                    objGlobalMethods.writeToFile("New APK from L2APK downloaded successfully. Begins to prompt install.", "", "", "", "");
                    //Intent intent = new Intent(Intent.ACTION_VIEW);
                    Intent intent = null;
                    File externalStorageDir = Environment.getExternalStorageDirectory();
                    File myFile = new File(externalStorageDir, "app-debug.apk");
                    Uri uri = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName(), myFile);
                        intent.setData(uri);
                        startActivity(intent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        uri = Uri.fromFile(myFile);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception caught at MainActivity DownloadTask onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
                    finish();
                }
            }
        }

    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences();
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(objGlobalVariables.PROPERTY_REG_ID, regId);
        editor.putInt(objGlobalVariables.PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences();
        String registrationId = prefs.getString(objGlobalVariables.PROPERTY_REG_ID, "");
        //String registrationId = "";
        if (registrationId.isEmpty()) {
            //Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(objGlobalVariables.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            //Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void getFCMtoken() {
        try {
            objGlobalMethods.writeToFile("Registering regid.", "", "", "", "");
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                objGlobalMethods.writeToFile("Registering regid failed.", "", "", "", "");
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            objGlobalMethods.storeRegistrationId(myApp.getAppContext(), token, objGlobalMethods.getGcmPreferences());
                        }
                    });
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MainActivity getFCMtoken. Exception: " + s, "", "", "", "");
        }
    }

    public void sendWhatsappMsg(View v) {
        try {
            String toNumber = txtUsername.getText().toString(); // contains spaces, i.e., example +91 98765 43210
            toNumber = toNumber.replace("+", "").replace(" ", "");
            String customerPhoneNumber = String.format("%s%s", "65", toNumber);
            Uri uri = Uri.parse("smsto:" + customerPhoneNumber);
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
            sendIntent.setPackage("com.whatsapp");

            startActivity(sendIntent);

        } catch (ActivityNotFoundException ae) {
            showDialog("Please install Whatsapp!");
        } catch (Exception e) {
            String s = e.getMessage();
        }
    }

    public void sendWhatsappMsg2(View v) {
        try {
            String toNumber = txtUsername.getText().toString(); // contains spaces, i.e., example +91 98765 43210
            toNumber = toNumber.replace("+", "").replace(" ", "");
            String customerPhoneNumber = String.format("%s%s", "65", toNumber);
            String url = "https://api.whatsapp.com/send?phone=" + customerPhoneNumber;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }
    }

    public void BTScanGranted() {
        try {

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MainActivity BTScanGranted. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public int bt_enabled() {
        int f = 0;
        try {
            BleManager.getInstance().init(getApplication());
            BleManager.getInstance()
                    .enableLog(true)
                    .setReConnectCount(3, 5000)
                    .setConnectOverTime(20000)
                    .setOperateTimeout(5000);
            if (!BleManager.getInstance().isSupportBle()) {
                //It is detected that the device does not support Bluetooth 4.0 BLE
                objGlobalMethods.writeToFile("It is detected that the device does not support Bluetooth 4.0 BLE", "", "", "", "");
                f = 1;
            } else if (!BleManager.getInstance().isBlueEnable()) {
                f = 2;
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MainActivity bt_enabled. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
        } finally {
            return f;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            /*if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Bluetooth is on!", Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open!", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == 2) {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Camera Permission allowed!", Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Camera Permission not allowed!", Toast.LENGTH_SHORT).show();
                }
            }*/
            permissionsManager.handleActivityResult(requestCode, resultCode);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            String l = s;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /*if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(myApp.currentActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(myApp.currentActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(myApp.currentActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }*/
        try {
            permissionsManager.handlePermissionResult(requestCode, permissions, grantResults);
        } catch (Exception ex) {
            String s = JobsHandler.getStackTraceString(ex);
            String ss = s;
        }
    }


    public void promptForUnrestrictedAccess(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Enable Unrestricted Data Access")
                .setMessage("To ensure the app works properly when Data Saver is on, please allow unrestricted data access.\n\n" +
                        "You'll be taken to settings where you can enable this app.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    openUnrestrictedDataSettings(activity);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openUnrestrictedDataSettings(Activity activity) {
        boolean opened = false;

        // Try app-specific background data restriction settings (Android 7+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Intent intent = new Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
                opened = true;
            } catch (ActivityNotFoundException e) {
                // Continue to fallback
            }
        }

        // Fallback 1: Data usage settings
        if (!opened) {
            try {
                Intent intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                activity.startActivity(intent);
                opened = true;

                Toast.makeText(activity,
                        "Look for 'Data Saver' then 'Unrestricted data'",
                        Toast.LENGTH_LONG).show();

            } catch (ActivityNotFoundException e) {
                // Continue to next fallback
            }
        }

        // Fallback 2: App details settings
        if (!opened) {
            try {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
                opened = true;

                Toast.makeText(activity,
                        "Look for 'Mobile data & Wi-Fi' settings",
                        Toast.LENGTH_LONG).show();

            } catch (ActivityNotFoundException e) {
                // Continue to final fallback
            }
        }

        // Final fallback: Main settings
        if (!opened) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivity(intent);

            Toast.makeText(activity,
                    "Navigate to: Apps → [This App] → Mobile data & Wi-Fi → Allow unrestricted data usage",
                    Toast.LENGTH_LONG).show();
        }
    }


}
