package com.xdel.luke.templogger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.callback.BleScanCallback;
import com.tzone.bluetooth.data.BleDevice;
import com.tzone.bluetooth.data.BleScanState;
import com.tzone.bluetooth.scan.BleScanRuleConfig;
import com.tzone.devices.BaseDevice;
import com.tzone.devices.DeviceType;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.inventory.InventoryBatchID;
import com.xdel.luke.inventory.InventoryLite;
import com.xdel.luke.inventory.InventoryMainActivity;
import com.xdel.luke.inventory.InventorySummaryActivity;
import com.xdel.luke.inventory.Inventory_Summary_Adapter;
import com.xdel.luke.mpd.mpdclass;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.RegBTLscanClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class TempLoggerRegActivity extends AppCompatActivity {
    Context context;
    ConnectionDetector cd;
    private MediaPlayer player;
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);

    private String username;
    private String password;
    private String useridx;
    private String driveridx;
    private String ishomesuser;
    private String[] UsernamePwd = new String[4];

    private SharedPreferences g, UpdateJobInfoPreferences;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    ListView lvBTTL;
    private TextView lblBatch_ID, lblInvMainUserIDX, lblInvMainDriverIDX, lblIsDefaultVRPpage, lblInvMainSubmitIsHomesUser;
    private Button btnTempLoggerRegCANCEL, btnTempLoggerRegScan;
    private lukeTLregistry_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        langType = objGlobalMethods.getGCMPrefs(g, "language");
        setContentView(R.layout.activity_temp_logger_reg);

        lvBTTL = findViewById(R.id.lvBTTL);
        lblInvMainUserIDX = findViewById(R.id.lblInvMainUserIDX);
        lblInvMainDriverIDX = findViewById(R.id.lblInvMainDriverIDX);
        btnTempLoggerRegCANCEL = findViewById(R.id.btnTempLoggerRegCANCEL);
        btnTempLoggerRegScan = findViewById(R.id.btnTempLoggerRegScan);
        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);
        lblInvMainSubmitIsHomesUser = findViewById(R.id.lblInvMainSubmitIsHomesUser);
        lblBatch_ID = findViewById(R.id.lblBatch_ID);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
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
            setTitle(context.getString(R.string.btregpage));


            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(g);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            Boolean IsHOMESuser = objGlobalMethods.IsHomesUserString(ishomesuser, g);
            lblInvMainUserIDX.setText(IsHOMESuser ? useridx : "");
            lblInvMainDriverIDX.setText(!IsHOMESuser ? useridx : "");

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
                objGlobalMethods.writeToFile("TempLoggerRegActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

            JobsHandler.checkForceLogOut();
            DashboardSubActivity.frompage = null;
            lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

            boolean idledMoreThan3Hrs = JobsHandler.idledMoreThan3Hrs_new();
            if (idledMoreThan3Hrs) {
                objGlobalMethods.writeToFile("Idled more than 3 hours. Manual force logout.", "", "", "", "");
                JobsHandler.ManualForceLogout();
            }

            cd = new ConnectionDetector(null);

            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                //Toast.makeText(context, "Please connect to working Internet connection.", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
            }

            objGlobalMethods.setGCMPrefs(g, "CannotTakePhoto", "");
            initPage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        g = null;
        UpdateJobInfoPreferences = null;
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at TempLoggerRegActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at TempLoggerRegActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        Intent parentIntent = new Intent(this, DrawerMainActivity.class);
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(g);
        ishomesuser = UsernamePwd[3];

        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        parentIntent = new Intent(this, (Boolean.parseBoolean(ishomesuser)) ? DrawerMainActivity.class : !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);

        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    public void initPage() {
        SharedPreferences btp;
        ArrayList<lukeTLregistry> list = null;
        boolean allreg = false;
        try {
            btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            list = mpdclass.getlukeTLregistryList(btp);
            if (list != null && list.size() > 0) {
                adapter = new lukeTLregistry_Adapter(this, list);
                lvBTTL.setAdapter(adapter);
            } else {
                adapter = new lukeTLregistry_Adapter(this, new ArrayList<>());
                lvBTTL.setAdapter(adapter);
            }

            allreg = allTLregistered(list);
            ctrlSettings.enableDisableSubmitButtonWithStyles((list != null && list.size() > 0) && !allreg, btnTempLoggerRegScan);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerRegActivity initPage. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private boolean allTLregistered(ArrayList<lukeTLregistry> list) {
        boolean asd = true;
        try {
            if (list != null && list.size() > 0) {
                for (int i = 0; i <= list.size() -1; i++) {
                    if (list.get(i).registered == 0) {
                        asd = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerRegActivity allTLregistered. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public void btnTempLoggerRegCANCEL_Click(View v) {
        back();
    }

    public void btnTempLoggerRegScan_Click(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.temploggerscaninstruction2));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
                SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                com.xdel.luke.templogger.RegBTLscanClass rbt = new com.xdel.luke.templogger.RegBTLscanClass();
                String RegBTLscanClassStarted = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "RegBTLscanClassStarted");
                if (!objGlobalMethods.IsEmptyString(RegBTLscanClassStarted)) {
                    objGlobalMethods.showDialog(myApp.currentActivity, "OK", "Please wait for 1 minute.").show();
                } else {
                    ArrayList<lukeTLregistry> list = mpdclass.getlukeTLregistryList(btp);
                    rbt.setMacDevicePairs(list);
                    rbt.setMacDevicePairsToBeSaved(list);
                    rbt.scan();
                    //rbt.scan();
                }

            }
        });

        builder.setNeutralButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    private void stopPlayingTone() {
        try {
            if (player != null) {
                player.reset();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at TempLoggerRegActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playSuccessVerifiedTone() {
        try {
            stopPlayingTone();

            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, verifiedToneURI);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at TempLoggerRegActivity playSuccessVerifiedTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playTone() {
        try {
            stopPlayingTone();
            Uri uri = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error);
            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, uri);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at TempLoggerRegActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void showChangeAirplaneModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
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

    private void showChangeGPSModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
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
                    Date dt = new Date();
                    objGlobalMethods.writeToFile("ActivityNotFoundException at TempLoggerRegActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPStoHighAccModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmodehighacc));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at TempLoggerRegActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangePermissionDialog() {
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
                    objGlobalMethods.writeToFile("Bluetooth is on at TempLoggerRegActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TempLoggerRegActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}