package com.xdel.luke.temploggerWhse;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.callback.BleScanCallback;
import com.tzone.bluetooth.data.BleDevice;
import com.tzone.bluetooth.data.BleScanState;
import com.tzone.bluetooth.scan.BleScanRuleConfig;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.BottomSheet.temploggerwhse_settingsDialog;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.GrabLogFileOnDemand_BroadcastReceiver;
import com.xdel.luke.HRM.ChangePwdActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objAuthDriverResult;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;
import com.xdel.luke.templogger.Scan;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TempLoggerWhseScanActivity extends AppCompatActivity {

    ////////code is based on ScanActivity
    public final String TAG = "TempLoggerWhseScanActivity";
    Context context;
    ConnectionDetector cd;
    private String APIKey = myApp.getAppContext().getString(R.string.action_LukeAPI);
    public ListView lvBTTL;
    private TextView lblMainText0, lblMainText1, lblDeviceScanningText1, lblHrlyStartTime, lblIntervalStartTime, lblStatus;
    private Button btnTempLoggerStop, btnTempLoggerStart, btnTempLoggerCancel;
    private SharedPreferences g, UpdateJobInfoPreferences;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    public Lv_ScanDeviceListAdapter _ListView_deviceAdapter;

    public static Boolean RestarterTimerFinishCalled = null;
    public static Boolean countdownToStartRestarterTimerCalled = null;
    public static CountDownTimer RestarterTimer;
    public static CountDownTimer MainTimer;
    public static CountDownTimer DeviceScanningTimer;
    public static Timer DeviceListViewRefresh_Timer;
    private long RestarterTimer_leftTimeMS = 3600000; //1hr
    //private long RestarterTimer_leftTimeMS = 900000; //15min
    private long MainTimer_leftTimeMS = 900000; //5min
    //private long MainTimer_leftTimeMS = 180000; //3min
    private long DeviceScanningTimer_leftTimeMS = 30000; //30s
    private temploggerwhse_settingsDialog twsd = null;
    private ProgressDialog pd;

    public void setMainTimer_leftTimeMS(long mainTimer_leftTimeMS) {
        MainTimer_leftTimeMS = mainTimer_leftTimeMS;
    }

    public void setDeviceScanningTimer_leftTimeMS(long deviceScanningTimer_leftTimeMS) {
        DeviceScanningTimer_leftTimeMS = deviceScanningTimer_leftTimeMS;
    }


    //HomeWatcher mHomeWatcher = new HomeWatcher(this);

    private void sendZippedLogsEmail() {
        try {
            cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()) {
                getZippedLogsEmailTask t = new getZippedLogsEmailTask();
                t.execute();
            } else {
                objGlobalMethods.showDialog(myApp.currentActivity, "OK", "No Internet Connection!").show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity sendZippedLogsEmail. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public class getZippedLogsEmailTask extends AsyncTask<Void,Void,Boolean> {

        public getZippedLogsEmailTask() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            if (pd != null) {
                pd.show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean asd = false;
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
            String zippedFilePath = "";
            try {
                String versionname = objGlobalMethods.getAppVersionName(myApp.getAppContext());

                zippedFilePath = objGlobalMethods.getZippedLogFilesAbsolutePath();
                if (!zippedFilePath.isEmpty() && !zippedFilePath.equals("") && zippedFilePath != "") {
                    String subj = "Luke report from Temp Logger Device, Version: " + versionname;
                    objGlobalMethods.prepareSendEmail(new String[]{"it@xdel.com"}, subj,
                            subj, zippedFilePath, false, subj);

                    Context ctx = myApp.getAppContext();
                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                } else {
                    sendLogEmail();
                }
                asd = true;
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity getZippedLogsEmailTask doInBackground. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            } finally {
                return asd;
            }
        }


        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean res) {
            try {
                pd.dismiss();
                //pd = null;
                objGlobalMethods.showDialog(myApp.currentActivity, "OK", res ? "Email sent." : "Error sending email.").show();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity getZippedLogsEmailTask onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            }
        }

    }

    private void getLogs() {
        File externalStorageDir = Environment.getExternalStorageDirectory();
        String storagePathStr = "";
        ArrayList<String> requiredFilesPaths = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        String zippedFilename = formatter.format(new Date()) + "_last7logs.zip";
        String fullzippedFilename = "";
        File zippedFile = null;
        String[] UsernamePwd = new String[4];
        String username = "", useridx = "";
        SharedPreferences GcmPref;
        String zippedFilePath = "";
        try {
            String versionname = objGlobalMethods.getAppVersionName(myApp.getAppContext());

            zippedFilePath = objGlobalMethods.getZippedLogFilesAbsolutePath();
            if (!zippedFilePath.isEmpty() && !zippedFilePath.equals("") && zippedFilePath != "") {
                String subj = "Luke report from Temp Logger Device, Version: " + versionname;
                objGlobalMethods.prepareSendEmail(new String[]{"it@xdel.com"}, subj,
                        subj, zippedFilePath, false, subj);

                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            } else {
                sendLogEmail();
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GrabLogFileOnDemand_IntentService getLogFiles outer try catch. Will not send email. ENDED. Exception: "
                    + e.toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void sendLogEmail() {
        try {
            Date dt = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
            String Filenamedtstr = "";
            Filenamedtstr = formatter.format(dt);
            File externalStorageDir = Environment.getExternalStorageDirectory();
            File attachment = new File(externalStorageDir, Filenamedtstr + "_log.txt");
            if (attachment != null && attachment.exists()) {
                String attachmentfilename = "";
                attachmentfilename = attachment.getAbsolutePath().toString();
                if (!attachmentfilename.equals("")) {
                    Mail m = new Mail("luke@xdel.com", "XDel2015");
                    String versionname = objGlobalMethods.getAppVersionName(myApp.getAppContext());
                    objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, attachmentfilename, "",
                            "Luke report from Temp Logger Device, Version: " + versionname, attachment);
                    sendmail.execute();
                    objGlobalMethods.showDialog(myApp.currentActivity, "OK", "Logs sent.").show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity sendLogEmail. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private View.OnLongClickListener lblMainText0_OnLongClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    sendLogEmail();
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity lblMainText0_OnLongClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_errorperformoperation)).show();
                } finally {
                    return false;
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        langType = objGlobalMethods.getGCMPrefs(g, "language");
        setContentView(R.layout.activity_temp_logger_whse_scan);

        lvBTTL = findViewById(R.id.lvBTTL);
        btnTempLoggerStop = findViewById(R.id.btnTempLoggerStop);
        btnTempLoggerStart = findViewById(R.id.btnTempLoggerStart);
        btnTempLoggerCancel = findViewById(R.id.btnTempLoggerCancel);

        lblMainText0 = findViewById(R.id.lblMainText0);
        lblMainText1 = findViewById(R.id.lblMainText1);
        lblDeviceScanningText1 = findViewById(R.id.lblDeviceScanningText1);
        lblHrlyStartTime = findViewById(R.id.lblHrlyStartTime);
        lblIntervalStartTime = findViewById(R.id.lblIntervalStartTime);
        lblStatus = findViewById(R.id.lblStatus);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_wait_loading));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

        /*mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                // do something here...

            }
            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            g = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(g)) {
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            context = myApp.getAppContext();

            int appFlag = myApp.getDeviceSettingsFlag();
            if (myApp.isAirplaneModeOnFlag(appFlag)) {
                showChangeAirplaneModeSettingsDialog();
                return;
            }
            if (myApp.isAllPermissionGrantedFlag(appFlag)) {
                showChangePermissionDialog();
                return;
            }

            if (!temploggerclass.isBTEnabled()) {
                objGlobalMethods.writeToFile("TempLoggerWhseScanActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

            /*lblMainText0.setOnLongClickListener(null);
            lblMainText0.setOnLongClickListener(lblMainText0_OnLongClickListener());*/

            refreshSettingsDisplay();
            setButtons();
        }

    }

    public void setButtons() {
        try {
            boolean isrunning = (MainTimer != null || DeviceListViewRefresh_Timer != null || DeviceScanningTimer != null);
            btnTempLoggerStart.setVisibility(isrunning ? View.GONE :View.VISIBLE);
            btnTempLoggerStop.setVisibility(isrunning ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity setButtons. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void refreshSettingsDisplay() {
        SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
        try {
            String temploggerWhseReadingSettingsStr = objGlobalMethods.getGCMPrefs(btp, "temploggerWhseReadingSettingsStr");
            temploggerWhseReadingSettings settings = new temploggerWhseReadingSettings();
            if (!objGlobalMethods.IsEmptyString(temploggerWhseReadingSettingsStr))
                settings = new Gson().fromJson(temploggerWhseReadingSettingsStr, temploggerWhseReadingSettings.class);

            String maintxt = temploggerwhse_settingsDialog.convertMillisecondsToMinutesStr(String.valueOf(settings.MainTimer_leftTimeMS));
            String deviceScantxt = temploggerwhse_settingsDialog.convertMillisecondsToMinutesStr(String.valueOf(settings.DeviceScanningTimer_leftTimeMS));
            String HourlyRestarterTimerStartTimeStr = objGlobalMethods.getGCMPrefs(btp, "HourlyRestarterTimerStartTime");
            String MainTimerStartTimeStr = objGlobalMethods.getGCMPrefs(btp,"MainTimerStartTime");
            long HourlyRestarterTimerStartTime = !objGlobalMethods.IsEmptyString(HourlyRestarterTimerStartTimeStr) ? Long.parseLong(HourlyRestarterTimerStartTimeStr) : 0;
            long MainTimerStartTime = !objGlobalMethods.IsEmptyString(MainTimerStartTimeStr) ? Long.parseLong(MainTimerStartTimeStr) : 0;
            String HourlyRestarterTimerStartTimeVal = JobsHandler2.getDateTimeFromLong(HourlyRestarterTimerStartTime);
            String MainTimerStartTimeVal = JobsHandler2.getDateTimeFromLong(MainTimerStartTime);

            lblMainText1.setText(maintxt);
            lblDeviceScanningText1.setText(deviceScantxt);
            lblHrlyStartTime.setText(HourlyRestarterTimerStartTimeVal);
            lblIntervalStartTime.setText(MainTimerStartTimeVal);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity refreshSettingsDisplay. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_temploggerwhsescan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.tempsettings) {
            if (MainTimer != null || DeviceListViewRefresh_Timer != null || DeviceScanningTimer != null) {
                objGlobalMethods.showDialog(myApp.currentActivity, "OK", "Timer still running.\n\nClick STOP to stop scanning first.").show();
            } else {
                ViewTempLoggerWhseScanActivitySettingsPwdPrompt();
            }
        } else if (id == R.id.templogs) {
            Context ctx = myApp.getAppContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Confirm to send logs?");

            builder.setPositiveButton(ctx.getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendZippedLogsEmail();
                }
            });

            builder.setNegativeButton(ctx.getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        /*switch (item.getItemId()) {
            case R.id.tempsettings:
                if (MainTimer != null || DeviceListViewRefresh_Timer != null || DeviceScanningTimer != null) {
                    objGlobalMethods.showDialog(myApp.currentActivity, "OK", "Timer still running.\n\nClick STOP to stop scanning first.").show();
                } else {
                    ViewTempLoggerWhseScanActivitySettingsPwdPrompt();
                }

                break;
            case R.id.templogs:
                Context ctx = myApp.getAppContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage("Confirm to send logs?");

                builder.setPositiveButton(ctx.getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendZippedLogsEmail();
                    }
                });

                builder.setNegativeButton(ctx.getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                break;
        }*/
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog EntePasswordalertDialog = null;

    private void ViewTempLoggerWhseScanActivitySettingsPwdPrompt() {
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

                                twsd = new temploggerwhse_settingsDialog();

                                FragmentActivity activity = (FragmentActivity) myApp.currentActivity;
                                twsd.show(activity.getSupportFragmentManager(), "TempLoggerWhseScanOpenSettings");
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
                        objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity ViewTempLoggerWhseScanActivitySettingsPwdPrompt btnVerify.setOnClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    }
                }
            });

            builder.setCancelable(true);
            EntePasswordalertDialog = builder.create();
            EntePasswordalertDialog.show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity ViewTempLoggerWhseScanActivitySettingsPwdPrompt. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        g = null;
        UpdateJobInfoPreferences = null;

        if (EntePasswordalertDialog != null && EntePasswordalertDialog.isShowing())
            EntePasswordalertDialog.dismiss();
        EntePasswordalertDialog = null;

        if (twsd != null && twsd.getDialog().isShowing())
            twsd.dismiss();

        twsd = null;
        StopScan(true);

        /*if (mHomeWatcher != null)
            mHomeWatcher.stopWatch();
        mHomeWatcher = null;*/

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity = null;

        if (EntePasswordalertDialog != null && EntePasswordalertDialog.isShowing())
            EntePasswordalertDialog.dismiss();
        EntePasswordalertDialog = null;

        if (twsd != null && twsd.getDialog() != null && twsd.getDialog().isShowing())
            twsd.dismiss();

        twsd = null;

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        if (MainTimer != null || DeviceListViewRefresh_Timer != null || DeviceScanningTimer != null) {
            objGlobalMethods.showDialog(myApp.currentActivity, "OK", "Timer still running.\n\nClick STOP to stop scanning\nthen press the back.").show();
        } else {
            Intent parentIntent = new Intent(this, DrawerMainActivity.class);
            parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            this.finish();
        }
    }

    public static void showActiveNotification2() {
        try {
            objGlobalMethods.startActiveForegroundSvc("");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity showActiveNotification2. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
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

    public void btnTempLoggerStart_Click(View v) {
        btnTempLoggerStart.setVisibility(View.GONE);
        btnTempLoggerStop.setVisibility(View.VISIBLE);
        try {
            /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showActiveNotification2();
            } else {
                showActiveNotification("Temp Logger");
            }*/
            StartRestarterTimer();
            StartScanProcess();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);

            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity btnTempLoggerStart_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnTempLoggerStop_Click(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //builder.setMessage(myApp.getAppContext().getString(R.string.failremaining));
        builder.setMessage("Confirm to STOP?");

        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                btnTempLoggerStart.setVisibility(View.VISIBLE);
                btnTempLoggerStop.setVisibility(View.GONE);
                StopScan(true);

                /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    objGlobalMethods.stopActiveForegroundSvc();
                } else {
                    if (FCMservice.mNotificationManager != null) {
                        FCMservice.mNotificationManager.cancel(-1);
                    }
                }*/

            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void btnTempLoggerCancel_Click(View v) {
        back();
    }

    private void StartScanProcess() {
        try {
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerWhseScanActivity.class && lvBTTL != null)
                lvBTTL.setAdapter(null);

            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerWhseScanActivity.class && _ListView_deviceAdapter == null)
                _ListView_deviceAdapter = new Lv_ScanDeviceListAdapter(this);

            if (myApp.currentActivity == null && _ListView_deviceAdapter == null)
                _ListView_deviceAdapter = new Lv_ScanDeviceListAdapter();

            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerWhseScanActivity.class && lvBTTL != null)
                lvBTTL.setAdapter(_ListView_deviceAdapter);

            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerWhseScanActivity.class && lblStatus != null)
                lblStatus.setText("Processing");

            ((Lv_ScanDeviceListAdapter) lvBTTL.getAdapter()).Clear();

            Scan();

            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            String temploggerWhseReadingSettingsStr = objGlobalMethods.getGCMPrefs(btp, "temploggerWhseReadingSettingsStr");
            temploggerWhseReadingSettings settings = new temploggerWhseReadingSettings();
            if (!objGlobalMethods.IsEmptyString(temploggerWhseReadingSettingsStr))
                settings = new Gson().fromJson(temploggerWhseReadingSettingsStr, temploggerWhseReadingSettings.class);

            DeviceScanningTimer_leftTimeMS = (settings.DeviceScanningTimer_leftTimeMS);
            MainTimer_leftTimeMS = (settings.MainTimer_leftTimeMS);

            if (DeviceScanningTimer != null) {
                DeviceScanningTimer.cancel();
                DeviceScanningTimer = null;
            }

            DeviceScanningTimer = new CountDownTimer(DeviceScanningTimer_leftTimeMS, 1000) {

                public void onTick(long millisUntilFinished) {
                    //do nothing
                }

                public void onFinish() {
                    StopScan(false);
                    StartReadingData();
                }
            }.start();
            //Log.e(TAG, "StartScanProcess() - DeviceScanningTimer Start.");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity StartScanProcess. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void StartRestarterTimer() {
        try {
            if (RestarterTimer != null) {
                RestarterTimer.cancel();
                RestarterTimer = null;
            }

            RestarterTimer = new CountDownTimer(RestarterTimer_leftTimeMS, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //do nothing
                }

                @Override
                public void onFinish() {
                    RestarterTimerFinishCalled = true;
                    if (WhseBtReport.IsRunning != null && WhseBtReport.IsRunning) {
                        delayStartRestarterTimer();
                    } else {
                        //StopBleManager(); //shifted to countdownToStartRestarterTimer()
                        countdownToStartRestarterTimer();
                    }
                }
            }.start();
            long HourlyRestarterTimerStartTime = new Date().getTime();
            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            objGlobalMethods.setBTLoggerRelatedPrefs(btp, "HourlyRestarterTimerStartTime", String.valueOf(HourlyRestarterTimerStartTime));
            objGlobalMethods.writeToFile("HourlyRestarterTimerStartTime started","","","","");
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerWhseScanActivity.class) {
                String HourlyRestarterTimerStartTimeVal = JobsHandler2.getDateTimeFromLong(HourlyRestarterTimerStartTime);
                lblHrlyStartTime.setText(HourlyRestarterTimerStartTimeVal);
            }
            RestarterTimerFinishCalled = null;
            //Log.e(TAG, "StartRestarterTimer() Start.");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity StartRestarterTimer(). Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void countdownToStartRestarterTimer() {
        try {
            objGlobalMethods.writeToFile("Start countdownToStartRestarterTimer","","","","");
            CountDownTimer delayTimer = new CountDownTimer(30000, 1000) { //30s delay
                @Override
                public void onTick(long millisUntilFinished) {
                    countdownToStartRestarterTimerCalled = true;
                }

                @Override
                public void onFinish() {
                    RestarterTimerFinishCalled = null;
                    countdownToStartRestarterTimerCalled = null;
                    StopBleManager();
                    StartRestarterTimer();
                    StartScanProcess();
                }
            }.start();
            countdownToStartRestarterTimerCalled  = true;
            stopMainTimer("TempLoggerWhseScanActivity countdownToStartRestarterTimer");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity countdownToStartRestarterTimer(). Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void delayStartRestarterTimer() {
        try {
            objGlobalMethods.writeToFile("Start delayStartRestarterTimer","","","","");
            CountDownTimer delayTimer = new CountDownTimer(15000, 1000) { //15s delay
                //CountDownTimer delayTimer = new CountDownTimer(300000, 1000) { //5min delay
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    RestarterTimerFinishCalled = true;
                    if (WhseBtReport.IsRunning != null && WhseBtReport.IsRunning) {
                        objGlobalMethods.writeToFile("Start delayStartRestarterTimer at delayStartRestarterTimer onFinish","","","","");
                        delayStartRestarterTimer();
                    } else {
                        countdownToStartRestarterTimer();
                    }
                }
            }.start();
            stopMainTimer("TempLoggerWhseScanActivity delayStartRestarterTimer");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity delayStartRestarterTimer(). Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void stopMainTimer(String func) {
        try {
            if (MainTimer != null) {
                MainTimer.cancel();
                MainTimer = null;
                objGlobalMethods.writeToFile("stopMainTimer() in " + func,"","","","");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity stopMainTimer(). Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void StartMainTimer() {
        try {
            stopMainTimer("TempLoggerWhseScanActivity StartMainTimer");

            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            String temploggerWhseReadingSettingsStr = objGlobalMethods.getGCMPrefs(btp, "temploggerWhseReadingSettingsStr");
            temploggerWhseReadingSettings settings = new temploggerWhseReadingSettings();
            if (!objGlobalMethods.IsEmptyString(temploggerWhseReadingSettingsStr))
                settings = new Gson().fromJson(temploggerWhseReadingSettingsStr, temploggerWhseReadingSettings.class);

            MainTimer_leftTimeMS = (settings.MainTimer_leftTimeMS);
            MainTimer = new CountDownTimer(MainTimer_leftTimeMS, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //do nothing
                }

                @Override
                public void onFinish() {
                    //Log.e(TAG, "startMainTimer() - MainTimer onFinish.");
                    StartScanProcess();
                }
            }.start();

            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerWhseScanActivity.class && lblStatus != null)
                lblStatus.setText("Idling");
            long MainTimerStartTime = new Date().getTime();
            objGlobalMethods.setBTLoggerRelatedPrefs(btp, "MainTimerStartTime", String.valueOf(MainTimerStartTime));
            objGlobalMethods.writeToFile("MainTimerStartTime started","","","","");
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerWhseScanActivity.class) {
                String MainTimerStartTimeVal = JobsHandler2.getDateTimeFromLong(MainTimerStartTime);
                lblIntervalStartTime.setText(MainTimerStartTimeVal);
            }
            //Log.e(TAG, "MainTimer Start.");


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity startMainTimer. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void Scan() {
        try {
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING)
                BleManager.getInstance().cancelScan();

            objGlobalMethods.writeToFile("Start scanning.","","","","");

            BleManager.getInstance().disconnectAllDevice();
            BleManager.getInstance().destroy();
            BleManager.getInstance().init(myApp.getInstance());
            BleManager.getInstance()
                    .enableLog(true)
                    .setReConnectCount(3, 5000)
                    .setConnectOverTime(20000)
                    .setOperateTimeout(5000);

            BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                    .setScanTimeOut(1000 * 60)
                    //.setAutoConnect(true)
                    .build();
            BleManager.getInstance().initScanRule(scanRuleConfig);
            BleManager.getInstance().scan(DeviceScanCallback);

            if (DeviceListViewRefresh_Timer != null)
                DeviceListViewRefresh_Timer.cancel();
            DeviceListViewRefresh_Timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_IDLE)
                                BleManager.getInstance().scan(DeviceScanCallback);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RefreshUI();
                                }
                            });
                        }
                    } catch (Exception ex) {
                    }
                }
            };
            DeviceListViewRefresh_Timer.schedule(timerTask, 1000, 500);
            //Log.e(TAG, "Scan() - DeviceListViewRefresh_Timer.schedule");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity Scan. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void StopScan(boolean stopMain) {
        try {

            if (stopMain && RestarterTimer != null) {
                RestarterTimer.cancel();
                //Log.e(TAG, "StopScan() - RestarterTimer.cancel().");
                RestarterTimer = null;
                //Log.e(TAG, "StopScan() - RestarterTimer = null.");
                RestarterTimerFinishCalled = null;
                //Log.e(TAG, "StopScan() - RestarterTimerFinishCalled = null.");
            }

            if (stopMain && MainTimer != null) {
                stopMainTimer("TempLoggerWhseScanActivity StopScan");
            }

            if (DeviceScanningTimer != null) {
                DeviceScanningTimer.cancel();
                //Log.e(TAG, "StopScan() - DeviceScanningTimer.cancel().");
                DeviceScanningTimer = null;
                //Log.e(TAG, "StopScan() - DeviceScanningTimer = null.");
            }

            if (DeviceListViewRefresh_Timer != null) {
                DeviceListViewRefresh_Timer.cancel();
                //Log.e(TAG, "StopScan() - DeviceListViewRefresh_Timer.cancel().");
                DeviceListViewRefresh_Timer = null;
                //Log.e(TAG, "StopScan() - DeviceListViewRefresh_Timer = null.");
            }

            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING)
                BleManager.getInstance().cancelScan();

            BleManager.getInstance().disconnectAllDevice();
            BleManager.getInstance().destroy();
            BleManager.getInstance().init(myApp.getInstance());
            BleManager.getInstance()
                    .enableLog(true)
                    .setReConnectCount(3, 5000)
                    .setConnectOverTime(20000)
                    .setOperateTimeout(5000);

            //Log.e(TAG, "StopScan");
        } catch (Exception ex) {
            //Log.e(TAG, "StopScan => " + ex.toString());
        }
    }

    public void StopBleManager() {
        try {
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING)
                BleManager.getInstance().cancelScan();

            BleManager.getInstance().disconnectAllDevice();
            BleManager.getInstance().destroy();
            BleManager.getInstance().init(myApp.getInstance());
            BleManager.getInstance()
                    .enableLog(true)
                    .setReConnectCount(3, 5000)
                    .setConnectOverTime(20000)
                    .setOperateTimeout(5000);

            //Log.e(TAG, "StopBleManager");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity StopBleManager. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public BleScanCallback DeviceScanCallback = new BleScanCallback() {
        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            //Log.i(TAG, "onScanFinished => " + scanResultList.size());
        }

        @Override
        public void onScanStarted(boolean success) {
            //Log.i(TAG, "onScanStarted => " + success);
        }

        @Override
        public void onScanning(BleDevice bleDevice) {
            if (bleDevice == null)
                return;
            //Log.i(TAG, "onScanning => " + bleDevice.getMac() + " " + bleDevice.getName());
            Scan device = new Scan();
            if (device.fromBroadcast(bleDevice))
                _ListView_deviceAdapter.AddOrUpdate(device);
        }
    };

    private void RefreshUI() {
        try {
            /*if (IsRefreshing)
                return;*/

            if (_ListView_deviceAdapter != null && myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerWhseScanActivity.class) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            try {
                                _ListView_deviceAdapter.notifyDataSetChanged();
                            } catch (Exception ex) {
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            //IsRefreshing = false;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity RefreshUI. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void StartReadingData() {
        try {
            if (countdownToStartRestarterTimerCalled == null) {
                if (_ListView_deviceAdapter != null && _ListView_deviceAdapter.getDeviceList() != null && _ListView_deviceAdapter.getDeviceList().size() > 0) {
                    objGlobalMethods.writeToFile("StartReadingData","","","","");
                    List<Scan> DeviceList = _ListView_deviceAdapter.getDeviceList();

                    /*if (DeviceList != null && DeviceList.size() > 0) {
                        for (int i = DeviceList.size() -1; i >= 0; i--) {
                            if (!DeviceList.get(i).getMac().equals("E0:79:8D:6B:AC:60"))
                                DeviceList.remove(i);
                        }
                    }*/

                    WhseBTLscanClass wbt = new WhseBTLscanClass();
                    wbt.setDeviceList(DeviceList);
                    wbt.scan();
                } else {
                    StartMainTimer();
                }
            } else {
                objGlobalMethods.writeToFile("countdownToStartRestarterTimerCalled !null at StartReadingData(), not starting wbt.scan() or StartMainTimer()", "", "", "", "");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerWhseScanActivity StartReadingData. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at TempLoggerRegActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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

    private void showChangePermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at TempLoggerRegActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at TempLoggerRegActivity", "", "", "", "");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    /*temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);*/
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerRegActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }


}