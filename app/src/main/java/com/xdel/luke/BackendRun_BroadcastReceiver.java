package com.xdel.luke;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.xdel.luke.objClasses.objGlobalMethods;

public class BackendRun_BroadcastReceiver extends WakefulBroadcastReceiver {
    private boolean isreg;

    public BackendRun_BroadcastReceiver(){

    }

    public void register(final Context context) {
        try {
            if (!isreg) {
                IntentFilter filters = new IntentFilter();
                context.registerReceiver(this, filters);
                isreg = true;
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at BackendRun_BroadcastReceiver register. Exception: " + s, "", "", "", "");
        }
    }

    public void unregister(final Context context) {
        try {
            if (isreg) {
                context.unregisterReceiver(this);
                isreg = false;
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at BackendRun_BroadcastReceiver unregister. Exception: " + s, "", "", "", "");
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        try {
            ComponentName comp = new ComponentName(context.getPackageName(),
                    BackendRun_IntentService.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            //setResultCode(Activity.RESULT_OK);

        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at BackendRun_BroadcastReceiver onReceive: " + s,"","","","");
        }

    }
}
