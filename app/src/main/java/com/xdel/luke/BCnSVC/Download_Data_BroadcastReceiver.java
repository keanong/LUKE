package com.xdel.luke.BCnSVC;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.xdel.luke.objClasses.objGlobalMethods;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;

public class Download_Data_BroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        try {
            ComponentName comp = new ComponentName(context.getPackageName(),
                    Download_Data_IntentService.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            //setResultCode(Activity.RESULT_OK);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at Download_Data_BroadcastReceiver onReceive: " + s,"","","","");

        }
    }
}
