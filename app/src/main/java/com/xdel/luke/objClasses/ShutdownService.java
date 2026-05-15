package com.xdel.luke.objClasses;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.xdel.luke.JobsHandler;

/**
 * Created by kean.ong on 02/01/2018.
 */

public class ShutdownService extends IntentService {
    private Intent ShutdownSvc;
    SharedPreferences GcmPreferences;
    String[] UsernamePwd = new String[4];
    String username;
    String password;
    String useridx;

    public ShutdownService(){
        super("ShutdownService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ShutdownSvc = intent;
        try {
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            String FullName = objGlobalMethods.getGCMPrefs(GcmPreferences, "FullName");
            if (FullName.isEmpty() || FullName.equals("") || FullName == "") {
                FullName = objGlobalMethods.getGCMPrefs(GcmPreferences, "username");
            }
            //if (!username.equals("") && !password.equals("") && !useridx.equals("0") && !useridx.equals("")) {
            if (!username.equals("") && !password.equals("") && !useridx.equals("0") && !useridx.equals("")) {
                JobsHandler.sendShuttingDownEmail("Device is shutting down for GC: " + FullName + ", IDX: " + useridx + ".");
            } else {
                String prevLoginUserName = objGlobalMethods.getGCMPrefs(GcmPreferences, "prevLoginUserName");
                String prevLoginUserIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, "prevLoginUserIDX");
                JobsHandler.sendShuttingDownEmail("Device is shutting down for GC: " +
                        prevLoginUserName != null && !prevLoginUserName.isEmpty() && !prevLoginUserName.equals("") && prevLoginUserName != "" ? prevLoginUserName : ""
                        + ", IDX: " +
                        prevLoginUserIDX != null && !prevLoginUserIDX.isEmpty() && !prevLoginUserIDX.equals("") && prevLoginUserIDX != "" ? prevLoginUserIDX : "" + ".");
            }
            objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShutdownService onHandleIntent. Exception: " + e.getMessage().toString(),"","","","");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        } finally {
            ShutdownReceiver.completeWakefulIntent(ShutdownSvc);
        }
    }
}
