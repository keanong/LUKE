package com.xdel.luke.objClasses;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.legacy.content.WakefulBroadcastReceiver;

/**
 * Created by kean.ong on 02/01/2018.
 */

public class ShutdownReceiver extends WakefulBroadcastReceiver {
    private Context ctx;
    private String[] UsernamePwd = new String[4];
    private String username;
    private String password;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        ctx = context;

        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        try {
            if (gcmp == null) {
                gcmp = objGlobalMethods.getGcmPreferences();
            }
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
            username = UsernamePwd[0];
            password = UsernamePwd[1];

            if (!username.equals("") && !password.equals("")) {
                ComponentName comp = new ComponentName(context.getPackageName(),
                        ShutdownService.class.getName());
                // Start the service, keeping the device awake while it is launching.
                startWakefulService(context, (intent.setComponent(comp)));
                //setResultCode(Activity.RESULT_OK);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ShutdownReceiver onReceive. Exception: " + e.getMessage().toString(),"","","","");
        }
        /*ComponentName comp = new ComponentName(context.getPackageName(),
                ShutdownService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        //setResultCode(Activity.RESULT_OK);*/
    }
}
