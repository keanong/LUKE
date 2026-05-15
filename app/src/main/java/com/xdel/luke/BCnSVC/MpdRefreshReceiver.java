package com.xdel.luke.BCnSVC;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objGlobalMethods;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;

public class MpdRefreshReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ComponentName comp = new ComponentName(context.getPackageName(),
                    MpdRefreshIntentService.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MpdRefreshReceiver onReceive. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }
}