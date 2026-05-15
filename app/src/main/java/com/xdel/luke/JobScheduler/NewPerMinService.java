package com.xdel.luke.JobScheduler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import com.xdel.luke.BackendRun_IntentService;
import com.xdel.luke.BattLvl_Receiver;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;

public class NewPerMinService extends Service {
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    public static int count;
    public static int COD_five_min_Count;
    private static int Battlvl_five_min_count;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Context context = myApp.getApplicationCtx();
            //objGlobalMethods.writeToFile("Runing NewPerMinService.","","","","");
            /*if (FCMservice.notiNotRead) {
                if (count == 0) {
                    count += 1;
                } else if (count > 0) {
                    sendNotification2New(myApp.getAppContext().getString(R.string.alert_newpip));
                    count += 1;
                }
            }*/

            GcmPreferences = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                    Context.MODE_PRIVATE);
            UpdateJobInfoPreferences = context.getSharedPreferences("UpdateJobInfoPrefs",
                    Context.MODE_PRIVATE);

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            COD_five_min_Count += 1;
            Battlvl_five_min_count += 1;

            //ConnectionDetector cd = new ConnectionDetector(context);
            ConnectionDetector cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()) {
                try {
                    int queCount = 0;
                    queCount = JobsHandler.returnNumberOfQueToSend(UpdateJobInfoPreferences);
                    if (queCount > 0) {
                        if (!username.equals("") && !password.equals("") && count >= 1) {
                            objGlobalMethods.writeToFile("Calling BackendRun on per minute run check. Driver: " + username + ", DriverIDX: " + useridx,
                                    "", "", username, useridx);
                            count = 0;

                            objGlobalMethods.writeToFile("PerMinuteRun counter reset. Starting BackendRun_BroadcastReceiver.",
                                    "", "", "", "");

                            JobsHandler.backEndIsRunning = false;
                            BackendRun_IntentService.isRunning = false;
                            FCMservice.enablePush = false;
                            //context.sendBroadcast(new Intent(context, BackendRun_BroadcastReceiver.class));
                            JobsHandler.backendRun(GcmPreferences, UpdateJobInfoPreferences);
                            FCMservice.enablePush = true;
                            JobsHandler.backEndIsRunning = false;
                        }
                    } else {
                        count = 0;
                    }

                    if (!username.equals("") && !password.equals("")) {
                        Integer battlvlInt = objGlobalMethods.getBattLvl(context);
                        boolean isCharging = objGlobalMethods.getBattChargingState(context);
                        Boolean isOnNonWorkingScreenBool = myApp.isOnNonWorkingScreen();
                        Boolean isScreenOn = objGlobalMethods.isScreenOn(context);
                        if ((battlvlInt == 30 || battlvlInt == 20 || battlvlInt == 15 || battlvlInt == 10 || battlvlInt == 5) && !isCharging) {
                            if (isScreenOn && isOnNonWorkingScreenBool) {
                                Boolean showBattChargeNoti = objGlobalMethods.checkshowBattChargeNoti();
                                if (!showBattChargeNoti) {
                                    objGlobalMethods.promptNotiForBattCharging(GcmPreferences);
                                }
                            } else {
                                Boolean asd = true;
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "showBattChargeNotiPrefStr", asd.toString());
                            }
                        } else {
                            //do nothing
                            Boolean asd = false;
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "showBattChargeNotiPrefStr", asd.toString());
                        }

                        //send battery timer
                        if (Battlvl_five_min_count == 3) {
                            Battlvl_five_min_count = 0;
                            context.sendBroadcast(new Intent(context, BattLvl_Receiver.class));
                        }
                        //send battery timer
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception caught at NewPerMinService onReceive. " + e.getMessage().toString(), "", "", "", "");
                    count = 0;
                    COD_five_min_Count = 0;
                }
            }


        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at NewPerMinService onStartCommand. Exception: " + e.getMessage().toString(),"","","","");
        } finally {
            return START_STICKY;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
