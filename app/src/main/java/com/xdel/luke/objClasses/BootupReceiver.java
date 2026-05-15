package com.xdel.luke.objClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.xdel.luke.JobsHandler;

/**
 * Created by kean.ong on 06/03/2018.
 */

public class BootupReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        /*ComponentName comp = new ComponentName(context.getPackageName(),
                BootupService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));*/
        try {
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            String[] UsernamePwd = new String[4];
            String username, password, useridx;
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];

            //boolean idledMoreThan3Hrs = JobsHandler.idledMoreThan3Hrs();
            boolean idledMoreThan3Hrs = false;
            if (idledMoreThan3Hrs) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "forceLogOutBooleanString", "true");
                objGlobalMethods.writeToFile("Device boot up at BootupReceiver but idle more than 3hours since last update. Manual force logout for Driver: " + username + ", IDX: " + useridx, "", "", "", "");
                JobsHandler.ManualForceLogout();
            } else if (!username.equals("") && !password.equals("") && !useridx.equals("0") && !useridx.equals("")) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
                objGlobalMethods.writeToFile("Device boot up at BootupReceiver for Driver: " + username + ", IDX: " + useridx, "", "", "", "");
                objGlobalMethods.startActiveForegroundSvc("true");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Excpetion at BootupReceiver. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            completeWakefulIntent(intent);
        }
    }
}
