package com.xdel.luke.JobScheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.util.Log;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

public class btLoggerJobService extends JobService {

    public btLoggerJobService(){

    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Boolean asd = false;
        Context ctx = myApp.getAppContext();
        try {
            /*String mdpsJsonStr = params.getExtras().getString("mdpsJsonStr");
            String ARegID = params.getExtras().getString("ARegID");

            if (!objGlobalMethods.IsEmptyString(mdpsJsonStr) && !mdpsJsonStr.equals("[]") && !objGlobalMethods.IsEmptyString(ARegID)) {

                Intent service = new Intent(ctx, btLoggerService.class);
                service.putExtra("mdpsJsonStr", mdpsJsonStr);
                service.putExtra("ARegID", ARegID);
                ctx.startService(service);

            }*/

            startBtLoggerService(params);

            Util.scheduleBtLoggerJob(ctx, null, null); // reschedule the job
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at btLoggerJobService onStartJob. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            asd = true;
        } finally {
            return asd;
        }
    }

    public void startBtLoggerService(JobParameters params) {
        Context ctx = myApp.getAppContext();
        String mdpsJsonStr = null;
        String ARegID = null;
        try {
            if (params != null) {
                mdpsJsonStr = params.getExtras().getString("mdpsJsonStr");
                ARegID = params.getExtras().getString("ARegID");
            }
            /*SharedPreferences g = objGlobalMethods.getGcmPreferences();
            if (mdpsJsonStr == null)
                mdpsJsonStr = objGlobalMethods.getGCMPrefs(g, "mdpsJsonStr");*/
            if (mdpsJsonStr == null)
                mdpsJsonStr = objGlobalMethods.getMPDRelatedSP_KeyValue("mdpsJsonStr");
            if (ARegID == null)
                ARegID = objGlobalMethods.getRegistrationIdOnly();

            if (!objGlobalMethods.IsEmptyString(mdpsJsonStr) && !mdpsJsonStr.equals("[]") && !objGlobalMethods.IsEmptyString(ARegID)) {

                Intent service = new Intent(ctx, btLoggerService.class);
                service.putExtra("mdpsJsonStr", mdpsJsonStr);
                service.putExtra("ARegID", ARegID);
                ctx.startService(service);
                Log.i("btLoggerJobService", "Start btLoggerService.");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at btLoggerJobService startBtLoggerService. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

}
