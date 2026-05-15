package com.xdel.luke;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import androidx.legacy.content.WakefulBroadcastReceiver;

public class GrabLogFileOnDemand_BroadcastReceiver extends WakefulBroadcastReceiver {
    public GrabLogFileOnDemand_BroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        ComponentName comp = new ComponentName(context.getPackageName(),
                GrabLogFileOnDemand_IntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        //setResultCode(Activity.RESULT_OK);
    }
}
