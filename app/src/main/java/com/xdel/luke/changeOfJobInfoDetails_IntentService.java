package com.xdel.luke;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.Date;

public class changeOfJobInfoDetails_IntentService extends IntentService {

    private static Intent changeOfJobInfoDetailsIntentService;

    public changeOfJobInfoDetails_IntentService() {
        super("changeOfJobInfoDetails_IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Date dt = new Date();
        Long[] storededitedJobsIDXAL = null;
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        changeOfJobInfoDetailsIntentService = intent;
        try {
            if (!JobsHandler.changeOfJobInfoDetailsIsRunning) {
                storededitedJobsIDXAL = JobsHandler.checkForJobInfoDetailsToBeEdited();

                if (storededitedJobsIDXAL != null && storededitedJobsIDXAL.length > 0) {
                    //objGlobalMethods.writeToFile("Calling JobsHandler.changeOfJobInfoDetailsSQLite.", "", "", "", "");

                    objGlobalMethods.setGCMPrefs(GcmPreferences, "storededitedJobsIDXJsonStr", "");
                    JobsHandler.changeOfJobInfoDetailsSQLite(storededitedJobsIDXAL);
                } else {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "storededitedJobsIDXJsonStr", "");
                }
                changeOfJobInfoDetails_BroadcastReceiver.completeWakefulIntent(changeOfJobInfoDetailsIntentService);
            } else {
                JobsHandler.changeOfJobInfoDetailsIsRunning = false;
                objGlobalMethods.setGCMPrefs(GcmPreferences, "storededitedJobsIDXJsonStr", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "refreshQueBooleanString", "true");
                objGlobalMethods.writeToFile("changeOfJobInfoDetails_IntentService is already running.", "", "", "", "");
                changeOfJobInfoDetails_BroadcastReceiver.completeWakefulIntent(changeOfJobInfoDetailsIntentService);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at changeOfJobInfoDetails_IntentService onHandleIntent. Resetting storededitedJobsIDXJsonStr to empty and force refresh set. Exception: "
                    + e.toString(), "", "", "", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "storededitedJobsIDXJsonStr", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "refreshQueBooleanString", "true");
            changeOfJobInfoDetails_BroadcastReceiver.completeWakefulIntent(changeOfJobInfoDetailsIntentService);
        }
    }

}
