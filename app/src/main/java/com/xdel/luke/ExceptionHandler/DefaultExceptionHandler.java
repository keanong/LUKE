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
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
    Activity activity;

    //R6: crash-loop protection, the time of the last crash is kept in SystemRelatedSharedPreferences
    private static final String LAST_CRASH_KEY = "crashHandlerLastCrashTimeLong";
    private static final long CRASH_LOOP_WINDOW_MS = 60 * 1000L;
    private static final Object CRASH_LOCK = new Object();
    private static final AtomicBoolean handling = new AtomicBoolean(false);

    public DefaultExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    /**
     * Only the first crashing thread handles the crash and ends the process.
     * Every step is isolated, so a failing step cannot skip the others, and the finally block always ends the process.
     * A handler that throws would otherwise leave the process alive with a dead main thread.
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handling.compareAndSet(false, true)) {
            //another thread is already handling a crash and will end the process; return without exiting
            return;
        }

        try {
            boolean allowRestart = false;
            try {
                allowRestart = recordCrashAndCheckRestartAllowed();
            } catch (Throwable t) {
                allowRestart = false;
            }

            try {
                String s = JobsHandler.getThrowableStackTraceString(ex);
                Throwable cause = ex != null ? ex.getCause() : null;
                String cs = JobsHandler.getThrowableStackTraceString(cause);
                objGlobalMethods.writeToFile("DefaultExceptionHandler uncaughtException. \nThrowable ex:\n" + (ex != null ? ex.getMessage() : "null") +
                        "\nStack:\n" + s +
                        "\nCause:\n" + cs, "", "", "", "");
            } catch (Throwable t) {
            }

            if (allowRestart) {
                try {
                    getLogFiles();
                } catch (Throwable t) {
                }

                try {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            myApp.getInstance().getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

                    //Restart your app after 2 seconds
                    AlarmManager mgr = (AlarmManager) myApp.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
                } catch (Throwable t) {
                }
            } else {
                try {
                    objGlobalMethods.writeToFile("DefaultExceptionHandler repeated crash within 60s or crash time not stored. Error report and restart skipped.", "", "", "", "");
                } catch (Throwable t) {
                }
            }

            try {
                //finishing the activity.
                activity.finish();
            } catch (Throwable t) {
            }
        } finally {
            //Stopping application, also when any step above failed; reached only by the thread that owns the handling flag
            //System.exit(2);
            System.exit(1);
        }


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

    /**
     * Stores the time of this crash in SystemRelatedSharedPreferences with a synchronous commit, and decides whether
     * an automatic restart and error report are allowed.
     * True only if the previous crash was at least CRASH_LOOP_WINDOW_MS ago and the new time was committed.
     * Fails safe: false if the preferences cannot be opened, the stored value cannot be read, or the commit fails,
     * so that a crash loop can never restart the app or queue repeated error reports.
     */
    private static boolean recordCrashAndCheckRestartAllowed() {
        synchronized (CRASH_LOCK) {
            long now = System.currentTimeMillis();

            SharedPreferences sp = null;
            try {
                sp = objGlobalMethods.getSystemRelatedSharedPreferences();
            } catch (Throwable t) {
                sp = null;
            }
            if (sp == null) {
                return false;
            }

            long last = 0;
            boolean readOk = true;
            try {
                last = sp.getLong(LAST_CRASH_KEY, 0); //0 when the key does not exist yet, the first crash
            } catch (Throwable t) {
                readOk = false;
            }

            boolean committed = false;
            try {
                //also overwrites an unreadable value, so a damaged entry cannot disable restarts permanently
                committed = sp.edit().putLong(LAST_CRASH_KEY, now).commit();
            } catch (Throwable t) {
                committed = false;
            }

            if (!readOk || !committed) {
                return false;
            }
            boolean repeated = last > 0 && now >= last && now - last < CRASH_LOOP_WINDOW_MS;
            return !repeated;
        }
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
