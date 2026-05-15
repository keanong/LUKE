package com.xdel.luke;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.xdel.luke.objClasses.objGlobalMethods;

public class changeOfJobInfoDetails_BroadcastReceiver extends WakefulBroadcastReceiver {
    public changeOfJobInfoDetails_BroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        try {
            ComponentName comp = new ComponentName(context.getPackageName(),
                    changeOfJobInfoDetails_IntentService.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            //setResultCode(Activity.RESULT_OK);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at changeOfJobInfoDetails_BroadcastReceiver onReceive: " + s,"","","","");
        }
    }
}
