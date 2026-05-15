package com.xdel.luke;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.xdel.luke.objClasses.objGlobalMethods;

public class BattLvl_Receiver extends WakefulBroadcastReceiver {
    private boolean isreg;
    public BattLvl_Receiver() {
    }

    public void register(final Context context) {
        try {
            if (!isreg) {
                final IntentFilter filters = new IntentFilter();
                filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
                filters.addAction("android.net.wifi.STATE_CHANGE");
                context.registerReceiver(this, filters);
                isreg = true;
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at Common_BroadcastReceiver register. Exception: " + s, "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at Common_BroadcastReceiver unregister. Exception: " + s, "", "", "", "");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        ComponentName comp = new ComponentName(context.getPackageName(),
                BattLvl_IntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        //setResultCode(Activity.RESULT_OK);
    }
}
