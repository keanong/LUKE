package com.xdel.luke.objClasses;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by kean.ong on 06/03/2018.
 */

public class BootupService extends IntentService {
    private Intent BootUpIntent;
    SharedPreferences GcmPreferences;
    String[] UsernamePwd = new String[4];
    String username;
    String password;
    String useridx;
    private String ishomesuser;

    public BootupService() {
        super("BootupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            BootUpIntent = intent;
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            if (objGlobalMethods.isNotHomesUser(username, password, ishomesuser)) {
                Context ctx = myApp.getAppContext();
                LocationService.currentlyProcessingLocation = false;
                ctx.sendBroadcast(new Intent(ctx, LocationService_Receiver.class));
                objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
                objGlobalMethods.writeToFile("Device boot up at BootupService for Driver: " + username + ", IDX: " + useridx, "", "", "", "");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at BootupService onHandleIntent. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            BootupReceiver.completeWakefulIntent(BootUpIntent);
        }
    }
}
