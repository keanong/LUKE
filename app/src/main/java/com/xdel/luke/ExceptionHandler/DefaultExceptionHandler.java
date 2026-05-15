package com.xdel.luke.ExceptionHandler;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.xdel.luke.BCnSVC.GrabUncaughtErrorLogFileOnDemand_BroadcastReceiver;
import com.xdel.luke.GrabLogFileOnDemand_BroadcastReceiver;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
    Activity activity;

    public DefaultExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String s = JobsHandler.getThrowableStackTraceString(ex);
        Throwable cause = ex.getCause();
        String cs = JobsHandler.getThrowableStackTraceString(cause);
        objGlobalMethods.writeToFile("DefaultExceptionHandler uncaughtException. \nThrowable ex:\n" + ex.getMessage() +
                "\nStack:\n" + s +
                "\nCause:\n" + cs, "", "", "", "");

        getLogFiles();

        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                myApp.getInstance().getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Restart your app after 2 seconds
        AlarmManager mgr = (AlarmManager) myApp.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
        //finishing the activity.
        activity.finish();
        //Stopping application
        //System.exit(2);
        System.exit(1);


        /*Intent intenta = new Intent (myApp.getAppContext(), MainActivity.class);
        intenta.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                myApp.getInstance().getBaseContext(), 0, intenta, PendingIntent.FLAG_ONE_SHOT);

        //Restart your app after 2 seconds
        AlarmManager mgr = (AlarmManager) myApp.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);

        //Stopping application
        //System.exit(2);
        System.exit(1);*/

    }

    public static void getLogFiles() {
        File att;
        String attachmentfilename = "";
        String[] UsernamePwd = new String[4];
        String username = "", useridx = "";
        try {
            objGlobalMethods.writeToFile("DefaultExceptionHandler getLogFiles started.", "", "", "", "");

            Date dt = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
            String Filenamedtstr = "";
            Filenamedtstr = formatter.format(dt);
            File externalStorageDir = Environment.getExternalStorageDirectory();
            att = new File(externalStorageDir, Filenamedtstr + "_log.txt");

            if (att != null) {
                if (att.exists()) {
                    attachmentfilename = att.getAbsolutePath().toString();
                    if (!attachmentfilename.equals("")) {
                        Mail m = new Mail("luke@xdel.com", "XDel2015");

                        SharedPreferences g = objGlobalMethods.getGcmPreferences();
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(g);
                        username = UsernamePwd[0];
                        String FullName = objGlobalMethods.getGCMPrefs(g, "FullName");
                        if (FullName.isEmpty() || FullName.equals("") || FullName == "") {
                            FullName = username;
                        }
                        String versionname = objGlobalMethods.getAppVersionName(myApp.getAppContext());
                        String subj = "Luke UNCAUGHT ERROR report from " + FullName + ". Version " + versionname;
                        objGlobalMethods.prepareSendEmail(new String[]{"it@xdel.com"}, subj,
                                "Luke UNCAUGHT ERROR report", attachmentfilename, false, subj);
                        objGlobalMethods.writeToFile("DefaultExceptionHandler getLogFiles ENDED.", "", "", "", "");
                    }
                } else {
                    objGlobalMethods.writeToFile("DefaultExceptionHandler getLogFiles File obj does not exist. Will not send email. ENDED.", "", "", "", "");
                }
            } else {
                objGlobalMethods.writeToFile("DefaultExceptionHandler getLogFiles return NULL file obj. Will not send email. ENDED.", "", "", "", "");
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at DefaultExceptionHandler getLogFiles outer try catch. Will not send email. ENDED. Exception: "
                    + e.toString() + "\nStack: " + stack, "", "", "", "");
        }
    }


}
