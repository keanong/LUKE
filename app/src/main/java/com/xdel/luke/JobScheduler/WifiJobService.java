package com.xdel.luke.JobScheduler;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.xdel.luke.objClasses.LocationService;
import com.xdel.luke.objClasses.WifiIntentService;
import com.xdel.luke.objClasses.myApp;

public class WifiJobService extends JobService {
    public WifiJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Boolean asd = false;
        try {
            Context ctx = myApp.getAppContext();
            Intent service = new Intent(ctx, WifiService.class);
            ctx.startService(service);
            Util.scheduleWifiJob(ctx); // reschedule the job
            asd =  true;

        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
