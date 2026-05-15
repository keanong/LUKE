package com.xdel.luke.JobScheduler;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.xdel.luke.objClasses.LocationService;
import com.xdel.luke.objClasses.myApp;

public class LocationJobService extends JobService {
    public LocationJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Boolean asd = false;
        try {
            Context ctx = myApp.getAppContext();
            Intent service = new Intent(ctx, LocationService.class);
            ctx.startService(service);
            Util.scheduleLocationJob(ctx); // reschedule the job
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
