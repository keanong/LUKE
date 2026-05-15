package com.xdel.luke.messagingClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;

public class GetAllMsg_Receiver extends BroadcastReceiver {
    private int uponLogin;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // an Intent broadcast.
        /*ComponentName comp = new ComponentName(context.getPackageName(),
                GetAllMsg_IntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        //setResultCode(Activity.RESULT_OK);*/
        uponLogin = intent.getIntExtra("uponLogin", 0);
        Intent inn = new Intent(context, GetAllMsg_IntentService.class);
        inn.putExtra("uponLogin", uponLogin);
        startWakefulService(context, inn);
    }
}
