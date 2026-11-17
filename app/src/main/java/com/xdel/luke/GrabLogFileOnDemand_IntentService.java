package com.xdel.luke;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GrabLogFileOnDemand_IntentService extends IntentService {

    private static Intent GrabLogFileOnDemandIntentService;
    ConnectionDetector cd;

    public GrabLogFileOnDemand_IntentService() {
        super("GrabLogFileOnDemand_IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Date dt = new Date();
        Context context = myApp.getAppContext();

        GrabLogFileOnDemandIntentService = intent;
        try {

            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()) {
                objGlobalMethods.writeToFile("GrabLogFileOnDemand_IntentService onHandleIntent started.", "", "", "", "");

                //getLogFileAttachmentOnDemand();
                getLogFiles();

                objGlobalMethods.writeToFile("GrabLogFileOnDemand_IntentService onHandleIntent ENDED.", "", "", "", "");
                GrabLogFileOnDemand_BroadcastReceiver.completeWakefulIntent(GrabLogFileOnDemandIntentService);
            } else {
                //no internet connection
                objGlobalMethods.writeToFile("backendRun will not run at GrabLogFileOnDemand_IntentService onHandleIntent. completing WakefulIntent.", "", "", "", "");
                GrabLogFileOnDemand_BroadcastReceiver.completeWakefulIntent(GrabLogFileOnDemandIntentService);
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GrabLogFileOnDemand_IntentService onHandleIntent. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
            GrabLogFileOnDemand_BroadcastReceiver.completeWakefulIntent(GrabLogFileOnDemandIntentService);
        }
    }

    private void getLogFiles() {
        File externalStorageDir = Environment.getExternalStorageDirectory();
        String storagePathStr = "";
        ArrayList<String> requiredFilesPaths = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        String zippedFilename = formatter.format(new Date()) + "_last7logs.zip";
        String fullzippedFilename = "";
        File zippedFile = null;
        String[] UsernamePwd = new String[4];
        String username = "", useridx = "";
        SharedPreferences GcmPref;
        String zippedFilePath = "";
        try {
            objGlobalMethods.writeToFile("GrabLogFileOnDemand_IntentService getLogFiles started.", "", "", "", "");
            GcmPref = objGlobalMethods.getGcmPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPref);
            username = UsernamePwd[0];
            String versionname = objGlobalMethods.getAppVersionName(myApp.getAppContext());
            String FullName = objGlobalMethods.getGCMPrefs(GcmPref, "FullName");
            if (FullName.isEmpty() || FullName.equals("") || FullName == "") {
                FullName = username;
            }

            zippedFilePath = objGlobalMethods.getZippedLogFilesAbsolutePath();
            if (!zippedFilePath.isEmpty() && !zippedFilePath.equals("") && zippedFilePath != "") {
                String subj = "Luke report on demand from " + FullName + ". Version " + versionname;
                objGlobalMethods.prepareSendEmail(new String[]{"it@xdel.com"}, "Luke report on demand from " + FullName + ". Version " + versionname,
                        "Luke report from handheld.", zippedFilePath, false, subj);
            } else {
                getLogFileAttachmentOnDemand();
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GrabLogFileOnDemand_IntentService getLogFiles outer try catch. Will not send email. ENDED. Exception: "
                    + e.toString() + "\nStack: " + stack, "", "", "", "");
        }
    }


    private void getLogFileAttachmentOnDemand() {
        Date dt = new Date();
        File att = null;
        String attachmentfilename = "";
        String[] UsernamePwd = new String[4];
        String username = "", useridx = "";
        SharedPreferences GcmPref;

        try {
            objGlobalMethods.writeToFile("GrabLogFileOnDemand_IntentService getLogFileAttachmentOnDemand started.", "", "", "", "");
            try {

                SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
                String Filenamedtstr = "";
                Filenamedtstr = formatter.format(dt);
                File externalStorageDir = Environment.getExternalStorageDirectory();
                att = new File(externalStorageDir, Filenamedtstr + "_log.txt");
            } catch (Exception e) {
                att = null;
                objGlobalMethods.writeToFile("Exception caught at GrabLogFileOnDemand_IntentService getLogFileAttachmentOnDemand inner try catch. Returning Null File obj. Exception: "
                        + e.toString(), "", "", "", "");
            }

            if (att != null) {
                if (att.exists()) {
                    attachmentfilename = att.getAbsolutePath().toString();
                    if (!attachmentfilename.equals("")) {

                        Mail m = new Mail("luke@xdel.com", "XDel2015");

                        GcmPref = objGlobalMethods.getGcmPreferences();
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPref);
                        username = UsernamePwd[0];
                        String FullName = objGlobalMethods.getGCMPrefs(GcmPref, "FullName");
                        if (FullName.isEmpty() || FullName.equals("") || FullName == "") {
                            FullName = username;
                        }
                        String versionname = objGlobalMethods.getAppVersionName(myApp.getAppContext());
                        sendEmailOnDemand(m, attachmentfilename, "", "Luke report on demand from " + FullName + ". Version " + versionname, att);
                        objGlobalMethods.writeToFile("GrabLogFileOnDemand_IntentService getLogFileAttachmentOnDemand ENDED.", "", "", "", "");
                    }
                } else {
                    objGlobalMethods.writeToFile("GrabLogFileOnDemand_IntentService getLogFileAttachmentOnDemand File obj does not exist. Will not send email. ENDED.", "", "", "", "");
                }
            } else {
                objGlobalMethods.writeToFile("GrabLogFileOnDemand_IntentService getLogFileAttachmentOnDemand return NULL file obj. Will not send email. ENDED.", "", "", "", "");
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GrabLogFileOnDemand_IntentService getLogFileAttachmentOnDemand outer try catch. Will not send email. ENDED. Exception: "
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
