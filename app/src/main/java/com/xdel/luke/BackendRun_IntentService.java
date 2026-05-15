package com.xdel.luke;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

public class BackendRun_IntentService extends IntentService {

    private static Intent BackendrunServiceIntent;
    public static Boolean isRunning = false;
    ConnectionDetector cd;

    public BackendRun_IntentService() {
        super("BackendRun_IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = myApp.getAppContext();
        Boolean regetCOD = false;
        SharedPreferences gcmPrefs, UpdateJobInfoPrefs;

        gcmPrefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);

        UpdateJobInfoPrefs = getSharedPreferences("UpdateJobInfoPrefs",
                Context.MODE_PRIVATE);

        BackendrunServiceIntent = intent;
        try {

            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()) {
                //objGlobalMethods.writeToFile("backendRun started at BackendRun_IntentService onHandleIntent.", "", "", "", "");
                JobsHandler.backendRun(gcmPrefs, UpdateJobInfoPrefs);

                FCMservice.enablePush = true;
                JobsHandler.backEndIsRunning = false;

                /*regetCOD = Boolean.parseBoolean(objGlobalMethods.getGCMPrefs(gcmPrefs,"regetCOD"));
                objGlobalMethods.setGCMPrefs(gcmPrefs,"regetCOD", "false");
                if (regetCOD){
                    objGlobalMethods.writeToFile(dt,"From backendRun, processCODJobs started.","","","","");
                    JobsHandler.processCODJobs();

                    try{
                        regetCOD = Boolean.parseBoolean(objGlobalMethods.getGCMPrefs(gcmPrefs,"regetCOD"));
                    }
                    catch (Exception e){
                        regetCOD = false;
                    }

                    if (!regetCOD){
                        objGlobalMethods.setGCMPrefs(gcmPrefs,"regetCOD","false");
                    }
                }*/

                //objGlobalMethods.writeToFile("backendRun ended at BackendRun_IntentService onHandleIntent. completing WakefulIntent.", "", "", "", "");
                isRunning = false;
                BackendRun_BroadcastReceiver.completeWakefulIntent(BackendrunServiceIntent);
            } else {
                //no internet connection
                objGlobalMethods.writeToFile("no internet connection. backendRun will not run at BackendRun_IntentService onHandleIntent. completing WakefulIntent.", "", "", "", "");
                BackendRun_BroadcastReceiver.completeWakefulIntent(BackendrunServiceIntent);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at BackendRun_IntentService onHandleIntent. Exception: " + e.toString(), "", "", "", "");
            BackendRun_BroadcastReceiver.completeWakefulIntent(BackendrunServiceIntent);
        }


    }

}
