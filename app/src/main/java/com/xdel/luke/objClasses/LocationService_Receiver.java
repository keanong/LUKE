package com.xdel.luke.objClasses;

import android.content.Context;
import android.content.Intent;
import androidx.legacy.content.WakefulBroadcastReceiver;
import com.xdel.luke.JobScheduler.Util;

public class LocationService_Receiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ////old code
        //context.startService(new Intent(context, LocationService.class));
        try {
            Util.scheduleLocationJob(context);
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }
}
