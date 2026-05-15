package com.xdel.luke;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import java.util.Date;

public class ProcessCOD_IntentService extends IntentService {

    private static Intent ProcessCODIntentService;
    ConnectionDetector cd;

    public ProcessCOD_IntentService() {
        super("ProcessCOD_IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = myApp.getAppContext();
        ProcessCODIntentService = intent;

        try{
            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()){
                objGlobalMethods.writeToFile("ProcessCOD_IntentService started.","","","","");


                JobsHandler.processCODJobs();

                objGlobalMethods.writeToFile( "processCODJobs ended.", "", "", "", "");
                BackendRun_BroadcastReceiver.completeWakefulIntent(ProcessCODIntentService);
            }
            else {
                //no internet connection
                objGlobalMethods.writeToFile( "No Internet connection. ProcessCOD_IntentService will not run onHandleIntent. completing WakefulIntent.", "", "", "", "");
                BackendRun_BroadcastReceiver.completeWakefulIntent(ProcessCODIntentService);
            }
        }
        catch(Exception e){
            objGlobalMethods.writeToFile("Exception caught at ProcessCOD_IntentService onHandleIntent. Exception: " + e.toString(),"","","","");
            BackendRun_BroadcastReceiver.completeWakefulIntent(ProcessCODIntentService);
        }
    }
}
