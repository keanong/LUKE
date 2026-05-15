package com.xdel.luke;

/**
 * Created by kean.ong on 04/05/2015.
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.objGlobalMethods;


/**
 * This {@code WakefulBroadcastReceiver} takes care of creating and managing a
 * partial wake lock for your app. It passes off the work of processing the GCM
 * message to an {@code IntentService}, while ensuring that the device does not
 * go back to sleep in the transition. The {@code IntentService} calls
 * {@code GcmBroadcastReceiver.completeWakefulIntent()} when it is ready to
 * release the wake lock.
 */

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        try {
            ComponentName comp = new ComponentName(context.getPackageName(),
                    GcmIntentService.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        } catch (IllegalStateException ise) {
            String s = ise.getMessage().toString();
            String x = JobsHandler.getStackTraceString(ise);
            String err = "IllegalStateException at GcmBroadcastReceiver onReceive. IllegalStateException: " + s + "\nStack: " + x;
            objGlobalMethods.writeToFile(err, "", "", "", "");
            sendBugReport("IllegalStateException at GcmBroadcastReceiver onReceive", err);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            String x = JobsHandler.getStackTraceString(e);
            String err = "Exception at GcmBroadcastReceiver onReceive. Exception: " + s + "\nStack: " + x;
            objGlobalMethods.writeToFile(err, "", "", "", "");
            sendBugReport("Exception at GcmBroadcastReceiver onReceive.",err);
        }
    }

    private void sendBugReport(String title, String msg){
        try {
            String useridx;
            String[] UsernamePwd = new String[4];
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            useridx = UsernamePwd[2];
            Mail m = new Mail("luke@xdel.com", "XDel2015");
            objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", msg
                    , title + " DriverIDX: " + useridx + ".", null);
            sendmail.execute();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at GcmBroadcastReceiver sendBugReport. Exception: " + s , "", "", "", "");
        }
    }
}
