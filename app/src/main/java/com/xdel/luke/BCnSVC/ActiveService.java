package com.xdel.luke.BCnSVC;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobScheduler.Util;
import com.xdel.luke.JobScheduler.WifiService;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

public class ActiveService extends Service {

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_START_FOREGROUND_LOCATIONSERVICE = "ACTION_START_FOREGROUND_LOCATIONSERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_STARTBACKENDRUN = "STARTBACKENDRUN";
    public static final String ACTION_STARTWIFISERVICE = "STARTWIFISERVICE";
    public static final String ACTION_STARTGPSALARMSERVICE = "STARTGPSALARMSERVICE";
    public static final String ACTION_START_DOWNLOAD_DATA = "STARTDOWNLOADDATA";
    public static final String ACTION_START_COUNTDOWN_TIMER = "STARTCOUNTDOWNTIMER";

    private SharedPreferences GcmPreferences;
    private String[] UsernamePwd = new String[4];
    private String username;
    private String password;
    private String useridx;
    private String ishomesuser;
    private boolean homesuser;
    private String bootup;

    public ActiveService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            /*if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];

            if (!username.equals("") && !password.equals("")) {
                objGlobalMethods.startGpsAlarm();
                RecevierMgr.registerbcrs();
            }*/

            //OLD
            /*if (bootup != null && !bootup.equals("") && !username.equals("") && !password.equals("")) {
                //objGlobalMethods.writeToFile("Device bootup. startGpsAlarm at ActiveService.","","","","");
                objGlobalMethods.startGpsAlarm();
            } else if (!username.equals("") && !password.equals("")){
                //objGlobalMethods.checkGPSnPerMinRunAlarmRunning(ishomesuser);
                objGlobalMethods.startGpsAlarm();
            }*/
        } catch (Exception e) {
            //String s = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at ActiveService onCreate. Exception: " + e.getMessage().toString() + "\nStack: " + s,"","","","");
            objGlobalMethods.writeToFile("Exception at ActiveService onCreate. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            Bundle extra = intent.getExtras();
            bootup = (extra != null && (extra.getString("bootup") != null && !extra.getString("bootup").equals(""))) ? extra.getString("bootup") : "";
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            String durationSec = extra != null && extra.getString("durationSec") != null && !extra.getString("durationSec").equals("") ? extra.getString("durationSec") : "0";

            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService(username);
                    Context ctx = myApp.getAppContext();
                    Util.scheduleNewPerMinJob(ctx);
                    Util.scheduleDetectNetworkDropJob(ctx);
                    //Util.scheduleBtLoggerJob(ctx, null, null);
                    if (objGlobalMethods.isNotHomesUser(username, password, ishomesuser)) {
                        //objGlobalMethods.writeToFile("ActiveService onStartCommand calling startGpsAlarm()", "", "", "", "");
                        objGlobalMethods.startGpsAlarm();
                    }
                    /*if (!username.equals("") && !password.equals("") && !homesuser) {
                        objGlobalMethods.startGpsAlarm();
                        Context ctx = myApp.getAppContext();
                        Util.scheduleNewPerMinJob(ctx);
                        Util.scheduleDetectNetworkDropJob(ctx);
                        //objGlobalMethods.startNewPerMinAlarm();
                        //RecevierMgr.registerbcrs();
                    }*/
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    break;
                case ACTION_STARTBACKENDRUN:
                    startBackEndRunService();
                    break;
                case ACTION_STARTWIFISERVICE:
                    if (objGlobalMethods.isNotHomesUser(username, password, ishomesuser)) {
                        startWifiService();
                    }
                    break;
                case ACTION_STARTGPSALARMSERVICE:
                    //if (objGlobalMethods.isNotHomesUser(username, password, ishomesuser)) {
                        startGPSAlarmService();
                    //}
                    break;
                case ACTION_START_DOWNLOAD_DATA:
                    startDownloadDataService();
                    break;
                case ACTION_START_COUNTDOWN_TIMER:
                    startCountDownTimerService(durationSec);
                    break;
            }
        }
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void startForegroundService(String name) {
        try {
            //objGlobalMethods.writeToFile("Active Foreground service is started.", "", "", "", "");
            Notification notification = buildAcctLoginedForegroundNotification2(name);
            // Start foreground service.
            startForeground(-1, notification);
        } catch (Exception e) {
            //String s = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at ActiveService startForegroundService. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            objGlobalMethods.writeToFile("Exception at ActiveService startForegroundService. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void stopForegroundService() {
        try {
            //objGlobalMethods.writeToFile("Active Foreground service is stopped.", "", "", "", "");
            // Stop foreground service and remove the notification.
            stopForeground(true);
            objGlobalMethods.stopGpsAlarm();
            // Stop the foreground service.
            stopSelf();
        } catch (Exception e) {
            //String s = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at ActiveService stopForegroundService. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            objGlobalMethods.writeToFile("Exception at ActiveService stopForegroundService. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void startBackEndRunService() {
        Context context = myApp.getAppContext();
        SharedPreferences gcmPrefs, UpdateJobInfoPrefs;
        ConnectionDetector cd;
        try {
            gcmPrefs = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPrefs = objGlobalMethods.getUpdateJobInfoPreferences();
            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);

            if (cd.isConnectingToInternet()) {
                objGlobalMethods.writeToFile("backendRun started at ActiveService startBackEndRunService.", "", "", "", "");
                JobsHandler.backendRun(gcmPrefs, UpdateJobInfoPrefs);
                FCMservice.enablePush = true;
                JobsHandler.backEndIsRunning = false;
            } else {
                //no internet connection
                objGlobalMethods.writeToFile("backendRun will not run at ActiveService startBackEndRunService.", "", "", "", "");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ActiveService startBackEndRunService. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void startWifiService() {
        Context context = myApp.getAppContext();
        try {
            context.startService(new Intent(context, WifiService.class));
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ActiveService startWifiService. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void startCountDownTimerService(String durationSec){
        Context ctx = myApp.getAppContext();
        try {
            Intent i = new Intent(ctx, CountdownTimerService.class);
            i.putExtra("durationSec", durationSec);
            ctx.startService(i);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ActiveService startCountDownTimerService. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void startGPSAlarmService() {
        try {
            objGlobalMethods.writeToFile("ActiveService startGPSAlarmService calling startGpsAlarm()", "", "", "", "");
            objGlobalMethods.startGpsAlarm();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ActiveService startGPSAlarmService. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void startDownloadDataService(){
        try {
            Context ctx = myApp.getAppContext();
            sendBroadcast(new Intent(ctx, Download_Data_BroadcastReceiver.class));
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ActiveService startDownloadDataService. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static Notification buildAcctLoginedForegroundNotification2(String name) {
        Context ctx = myApp.getAppContext();
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        String[] UsernamePwd = new String[4];
        String username, password, useridx;

        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
        username = UsernamePwd[0];
        password = UsernamePwd[1];
        useridx = UsernamePwd[2];
        String group_id = "LUKE";
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        boolean IsDefaultVRPpage = objGlobalMethods.IsDefaultVRPpage() && !IsMaintenanceHours;
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                new Intent(ctx, (!username.equals("") && !password.equals("") && !useridx.equals("")) && !IsDefaultVRPpage ? DrawerMainActivity.class :
                        (!username.equals("") && !password.equals("") && !useridx.equals("")) && IsDefaultVRPpage ? DrawerMain2Activity.class  : MainActivity.class), 0);
        //Bitmap bm = BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher2);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, myApp.CHANNEL_ID);

        b.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_xdel_noti2)
                //.setLargeIcon(bm)
                .setContentTitle(name + " " + myApp.getAppContext().getString(R.string.alert_Active2))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(myApp.getAppContext().getString(R.string.alert_touch)))
                .setWhen(System.currentTimeMillis())
                .setGroup(group_id)
                .setSound(null)
                .setContentText(myApp.getAppContext().getString(R.string.alert_touch))
                //.setColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                .setAutoCancel(false);
        b.setContentIntent(contentIntent);
        return (b.build());
    }

    public static void playTone(Context ctx, int resid) {
        try {
            MediaPlayer player = MediaPlayer.create(ctx, resid);
            player.start();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at objGlobalMethods playTone. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

}