package com.xdel.luke.JobScheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Log;

import com.google.gson.Gson;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.MacDevicePair;

import java.util.List;

public class Util {

    public static void cancelAllJobs(Context ctx) {
        try {
            JobScheduler jobScheduler = (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.cancelAll();
            objGlobalMethods.writeToFile("Cancel all scheduled jobs.", "", "", "", "");
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at Util cancelAllJobs. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void scheduleLocationJob(Context ctx) {
        try {
            // schedule the start of the service every 1 - 3 seconds
            ComponentName serviceComponent = new ComponentName(ctx, LocationJobService.class);
            JobInfo.Builder b = new JobInfo.Builder(0, serviceComponent);
            b.setPersisted(true);
            b.setMinimumLatency(1 * 1000); // wait at least
            b.setOverrideDeadline(3 * 1000); // maximum delay
            //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
            b.setRequiresDeviceIdle(false); // When set true, ensure that this job will not run if the device is in active use.
            b.setRequiresCharging(false); // we don't care if the device is charging or not
            JobScheduler jobScheduler = ctx.getSystemService(JobScheduler.class);
            jobScheduler.schedule(b.build());
        } catch (Exception e) {
            //String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Util scheduleLocationJob. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void scheduleNewPerMinJob(Context ctx) {
        try {
            // schedule the start of the service every 10 - 30 seconds
            ComponentName serviceComponent = new ComponentName(ctx, NewPerMinJobService.class);
            JobInfo.Builder b = new JobInfo.Builder(1, serviceComponent);
            b.setPersisted(true);
            b.setMinimumLatency(30 * 1000); // wait at least
            b.setOverrideDeadline(30 * 1000); // maximum delay
            //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
            b.setRequiresDeviceIdle(false); // When set true, ensure that this job will not run if the device is in active use.
            b.setRequiresCharging(false); // we don't care if the device is charging or not
            JobScheduler jobScheduler = ctx.getSystemService(JobScheduler.class);
            jobScheduler.schedule(b.build());
            //objGlobalMethods.writeToFile("scheduling NewPerMinService.","","","","");
        } catch (Exception e) {
            //String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Util scheduleNewPerMinJob. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void scheduleDetectNetworkDropJob(Context ctx) {
        try {
            ComponentName serviceComponent = new ComponentName(ctx, NetworkSchedulerService.class);
            JobInfo.Builder b = new JobInfo.Builder(2, serviceComponent);
            b.setPersisted(true);
            b.setMinimumLatency(1 * 1000); // wait at least
            b.setOverrideDeadline(2 * 1000); // maximum delay
            b.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
            b.setRequiresDeviceIdle(false); // When set true, ensure that this job will not run if the device is in active use.
            b.setRequiresCharging(false); // we don't care if the device is charging or not
            JobScheduler jobScheduler = ctx.getSystemService(JobScheduler.class);
            jobScheduler.schedule(b.build());
            //objGlobalMethods.writeToFile("scheduling NetworkSchedulerService.","","","","");
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at Util scheduleDetectNetworkDropJob. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void scheduleDetectWIfiJob(Context ctx) {
        try {
            ComponentName serviceComponent = new ComponentName(ctx, NetworkSchedulerService.class);
            JobInfo.Builder b = new JobInfo.Builder(3, serviceComponent);
            b.setPersisted(true);
            b.setMinimumLatency(1 * 1000); // wait at least
            b.setOverrideDeadline(2 * 1000); // maximum delay
            b.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
            b.setRequiresDeviceIdle(false); // When set true, ensure that this job will not run if the device is in active use.
            b.setRequiresCharging(false); // we don't care if the device is charging or not
            JobScheduler jobScheduler = ctx.getSystemService(JobScheduler.class);
            jobScheduler.schedule(b.build());
            //objGlobalMethods.writeToFile("scheduling NetworkSchedulerService.","","","","");
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at Util scheduleDetectWIfiJob. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void scheduleWifiJob(Context ctx) {
        try {
            // schedule the start of the service every 10 - 30 seconds
            ComponentName serviceComponent = new ComponentName(ctx, WifiJobService.class);
            JobInfo.Builder b = new JobInfo.Builder(4, serviceComponent);
            b.setPersisted(true);
            b.setMinimumLatency(10 * 1000); // wait at least
            b.setOverrideDeadline(20 * 1000); // maximum delay
            //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
            b.setRequiresDeviceIdle(false); // When set true, ensure that this job will not run if the device is in active use.
            b.setRequiresCharging(false); // we don't care if the device is charging or not
            JobScheduler jobScheduler = ctx.getSystemService(JobScheduler.class);
            jobScheduler.schedule(b.build());
        } catch (Exception e) {
            //String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Util scheduleWifiJob. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    //public static void scheduleBtLoggerJob(Context ctx, String[] macArray, String[] TemperatureLoggerIDXArray, String ARegID) {
    public static void scheduleBtLoggerJob(Context ctx, String mdpsJsonStr, String ARegID) {
        try {
            // schedule the start of the service every 10 - 30 seconds
            // schedule the start of the service every 120 - 180 seconds
            ComponentName serviceComponent = new ComponentName(ctx, btLoggerJobService.class);

            /*if (mdpsJsonStr == null)
                mdpsJsonStr = objGlobalMethods.getGCMPrefs(objGlobalMethods.getGcmPreferences(), "mdpsJsonStr");*/
            if (mdpsJsonStr == null)
                mdpsJsonStr = objGlobalMethods.getMPDRelatedSP_KeyValue("mdpsJsonStr");

            if (ARegID == null)
                ARegID = objGlobalMethods.getRegistrationIdOnly();

            PersistableBundle bundle = null;
            bundle = new PersistableBundle();
            bundle.putString("mdpsJsonStr", mdpsJsonStr);
            bundle.putString("ARegID", ARegID);

            JobInfo.Builder b = new JobInfo.Builder(5, serviceComponent);
            if (bundle != null) {
                b.setExtras(bundle);
            }
            b.setPersisted(true);
            b.setMinimumLatency(5 * 60 * 1000); // wait at least 5min or 300s
            b.setOverrideDeadline(6 * 60 * 1000); // maximum delay 6min or 360s
            //b.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            b.setRequiresDeviceIdle(false); // When set true, ensure that this job will not run if the device is in active use.
            b.setRequiresCharging(false); // we don't care if the device is charging or not
            JobScheduler jobScheduler = ctx.getSystemService(JobScheduler.class);
            jobScheduler.schedule(b.build());
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Util scheduleBtLoggerJob. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void CancelBtLoggerJob() {
        try {
            final JobScheduler jobScheduler = (JobScheduler) myApp.getAppContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
            Log.i("Util","Cancel BT Logger job.");
            jobScheduler.cancel(5);
            btLoggerJobSchedulerIsValid();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Util CancelBtLoggerJob. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static boolean btLoggerJobSchedulerIsValid() {
        boolean asd = false;
        try {
            final JobScheduler jobScheduler = (JobScheduler) myApp.getAppContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
            for (android.app.job.JobInfo jobInfo : jobScheduler.getAllPendingJobs()) {
                if (jobInfo.getId() == 5) {
                    asd = true;
                    break;
                }
            }

            Log.i("Util", "btLoggerJobSchedulerIsValid: " + (asd ? "True" : "False"));
            //objGlobalMethods.writeToFile("btLoggerJobSchedulerIsValid: " + (asd ? "True" : "False"), "", "", "", "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Util btLoggerJobSchedulerIsValid. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

}
