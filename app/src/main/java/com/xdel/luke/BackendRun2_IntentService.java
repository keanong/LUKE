package com.xdel.luke;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;

import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

public class BackendRun2_IntentService extends IntentService {

    private static Intent Backendrun2ServiceIntent;
    //public static Boolean isRunning = false;
    ConnectionDetector cd;

    public BackendRun2_IntentService() {
        super("BackendRun2_IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences gcmPrefs, UpdateJobInfoPrefs;

        gcmPrefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);

        UpdateJobInfoPrefs = getSharedPreferences("UpdateJobInfoPrefs",
                Context.MODE_PRIVATE);

        Backendrun2ServiceIntent = intent;
        try {

            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()) {
                JobsHandler2.backendRun(gcmPrefs, UpdateJobInfoPrefs);

                JobsHandler2.backEnd2IsRunning = false;

                //isRunning = false;
                BackendRun2_BroadcastReceiver.completeWakefulIntent(Backendrun2ServiceIntent);
            } else {
                //no internet connection
                objGlobalMethods.writeToFile("no internet connection. backendRun2 will not run at BackendRun2_IntentService onHandleIntent. completing WakefulIntent.", "", "", "", "");
                BackendRun2_BroadcastReceiver.completeWakefulIntent(Backendrun2ServiceIntent);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at BackendRun2_IntentService onHandleIntent. Exception: " + e.toString(), "", "", "", "");
            BackendRun2_BroadcastReceiver.completeWakefulIntent(Backendrun2ServiceIntent);
        }


    }

}