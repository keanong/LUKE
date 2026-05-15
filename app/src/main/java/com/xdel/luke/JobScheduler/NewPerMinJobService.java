package com.xdel.luke.JobScheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;

import com.xdel.luke.objClasses.LocationService;
import com.xdel.luke.objClasses.myApp;

public class NewPerMinJobService extends JobService {
    public NewPerMinJobService(){

    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Boolean asd = false;
        try {
            Context ctx = myApp.getAppContext();
            Intent service = new Intent(ctx, NewPerMinService.class);
            ctx.startService(service);
            Util.scheduleNewPerMinJob(ctx); // reschedule the job
        } catch (Exception e) {
            asd = true;
        } finally {
            return asd;
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
