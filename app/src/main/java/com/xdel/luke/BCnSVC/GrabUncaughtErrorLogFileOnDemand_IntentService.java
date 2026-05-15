package com.xdel.luke.BCnSVC;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.GrabLogFileOnDemand_BroadcastReceiver;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GrabUncaughtErrorLogFileOnDemand_IntentService extends IntentService {

    private static Intent GrabUncaughtErrorLogFileOnDemandIntentService;
    ConnectionDetector cd;

    public GrabUncaughtErrorLogFileOnDemand_IntentService() {
        super("GrabUncaughtErrorLogFileOnDemand_IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GrabUncaughtErrorLogFileOnDemandIntentService = intent;
        try {
            cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()) {
                objGlobalMethods.writeToFile("GrabUncaughtErrorLogFileOnDemand_IntentService onHandleIntent started.", "", "", "", "");

                getLogFiles();
                objGlobalMethods.writeToFile("GrabUncaughtErrorLogFileOnDemand_IntentService onHandleIntent ENDED.", "", "", "", "");
                GrabUncaughtErrorLogFileOnDemand_BroadcastReceiver.completeWakefulIntent(GrabUncaughtErrorLogFileOnDemandIntentService);
            } else {
                //no internet connection
                objGlobalMethods.writeToFile("backendRun will not run at GrabUncaughtErrorLogFileOnDemand_IntentService onHandleIntent. completing WakefulIntent.", "", "", "", "");
                GrabUncaughtErrorLogFileOnDemand_BroadcastReceiver.completeWakefulIntent(GrabUncaughtErrorLogFileOnDemandIntentService);
            }
        } catch (Exception e){
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GrabUncaughtErrorLogFileOnDemand_IntentService onHandleIntent. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
            GrabUncaughtErrorLogFileOnDemand_BroadcastReceiver.completeWakefulIntent(GrabUncaughtErrorLogFileOnDemandIntentService);
        }
    }

    private void getLogFiles() {
        File att;
        String attachmentfilename = "";
        String[] UsernamePwd = new String[4];
        String username = "", useridx = "";
        try {
            objGlobalMethods.writeToFile("GrabUncaughtErrorLogFileOnDemand_IntentService getLogFiles started.", "", "", "", "");

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
                        sendEmailOnDemand(m, attachmentfilename, "", "Luke UNCAUGHT ERROR report from " + FullName + ". Version " + versionname, att);
                        objGlobalMethods.writeToFile("GrabUncaughtErrorLogFileOnDemand_IntentService getLogFiles ENDED.", "", "", "", "");
                    }
                } else {
                    objGlobalMethods.writeToFile("GrabUncaughtErrorLogFileOnDemand_IntentService getLogFiles File obj does not exist. Will not send email. ENDED.", "", "", "", "");
                }
            } else {
                objGlobalMethods.writeToFile("GrabUncaughtErrorLogFileOnDemand_IntentService getLogFiles return NULL file obj. Will not send email. ENDED.", "", "", "", "");
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GrabUncaughtErrorLogFileOnDemand_IntentService getLogFiles outer try catch. Will not send email. ENDED. Exception: "
                    + e.toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void sendEmailOnDemand(Mail m, String attachmentfilename, String bodystr, String subjectstr, File myFile) {
        Boolean asd = false;
        try {
            String[] toArr = {"it@xdel.com"};
            m.setTo(toArr);
            m.setFrom("luke@xdel.com"); // pwd XDel2015
            if (subjectstr.equals("")) {
                m.setSubject("Luke report on Demand.");
            } else {
                m.setSubject(subjectstr);
            }

            if (bodystr.equals("")) {
                m.setBody("Luke report from handheld.");
            } else {
                m.setBody(bodystr);
            }

            if (!attachmentfilename.equals("")) {
                m.addAttachment(attachmentfilename);
            }

            asd = m.send();

            if (asd) {
                objGlobalMethods.writeToFile("GrabLogFileOnDemand_IntentService sendEmailOnDemand m.send() returns true. Email sent.", "", "", "", "");
            } else {
                objGlobalMethods.writeToFile("GrabLogFileOnDemand_IntentService sendEmailOnDemand m.send() returns false. Email not sent.", "", "", "", "");
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GrabLogFileOnDemand_IntentService sendEmailOnDemand. Email not sent. Exception: " + e.toString(), "", "", "", "");
        }
    }

}
