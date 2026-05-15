package com.xdel.luke.JobScheduler;

import android.content.Context;
import android.content.Intent;
import androidx.legacy.content.WakefulBroadcastReceiver;

public class NewPerMinBCR extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ////old code
        //context.startService(new Intent(context, LocationService.class));

        Util.scheduleNewPerMinJob(context);
    }
}
