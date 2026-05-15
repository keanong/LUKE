package com.xdel.luke.BCnSVC;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CountdownTimerService extends Service {
    public CountdownTimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int rtnValue = START_STICKY;
        String durationSec = "0";
        long durationSecLong = 0;
        try {
            if (intent != null) {
                Bundle extra = intent.getExtras();
                if (extra != null) {
                    durationSec = extra.getString("durationSec") != null && !extra.getString("durationSec").equals("") ? extra.getString("durationSec") : "0";
                } else {
                    durationSec = "0";
                }
                durationSecLong = Long.parseLong(durationSec);
                if (durationSecLong > 0) {
                    new CountDownTimer(durationSecLong, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }
                        @Override
                        public void onFinish() {
                            objGlobalMethods.writeToFile("Starting BackendRun from CountdownTimerService.", "", "", "", "");
                            SimpleDateFormat dtfor = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date dt = new Date();
                            String dts = dtfor.format(dt);
                            //objGlobalMethods.prepareSendEmail("Luke CountdownTimer started.", dts, "");
                            //JobsHandler.backendRun(objGlobalMethods.getGcmPreferences(), objGlobalMethods.getUpdateJobInfoPreferences());
                            Context ctx = myApp.getAppContext();
                            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                        }
                    }.start();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CountdownTimreService onStartCommand. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return rtnValue;
        }
    }
}
