package com.xdel.luke;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

public class Common_BroadcastReceiver extends BroadcastReceiver {

    private Context acontext;
    private MediaPlayer player;
    private boolean isreg;
    public static int count;
    private NotificationManager mNotificationManager;

    public void register(final Context context) {
        try {
            if (!isreg) {
                IntentFilter filters = new IntentFilter();
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
        this.acontext = context;
        if (FCMservice.notiNotRead) {
            if (count == 0) {
                count += 1;
            } else if (count > 0) {
                sendNotification(myApp.getAppContext().getString(R.string.alert_newpip));
                count += 1;
            }
        }
    }

    private void sendNotification(String msg) {
        try {
            DrawerMainActivity.DashboardOpenFromPush = true;
            mNotificationManager = (NotificationManager) acontext.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent contentIntent = PendingIntent.getActivity(acontext, 0, new Intent(acontext, DrawerMainActivity.class), 0);
            MainActivity.uponLogin = 0;
            Context ctx = myApp.getAppContext();
            Uri alarmSound = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.pip3);
            Bitmap bm = BitmapFactory.decodeResource(acontext.getResources(), R.mipmap.ic_action_info_outline);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(acontext)
                            .setSmallIcon(R.mipmap.ic_xdel_noti2)
                            .setLargeIcon(bm)
                            .setContentTitle(myApp.getAppContext().getString(R.string.alert_LukeNotification))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                            .setSound(alarmSound)
                            .setWhen(0).setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentText(msg)
                        /*.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})*/
                            .setAutoCancel(true);

            mBuilder.setContentIntent(contentIntent);

            mNotificationManager.notify(1, mBuilder.build());
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at Common_BroadcastReceiver sendNotification. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception at Common_BroadcastReceiver sendNotification. Exception: " + e.getMessage().toString(), "", "", "", "");
        }

    }

    private void sendNotification2New(Context ctx, String msg) {
        try {
            DrawerMainActivity.DashboardOpenFromPush = true;
            String group_id = "LUKEMPIP";
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent contentIntent = PendingIntent.getActivity(acontext, 0, new Intent(acontext, DrawerMainActivity.class), 0);
            MainActivity.uponLogin = 1;
            Bitmap bm = BitmapFactory.decodeResource(acontext.getResources(), R.mipmap.ic_action_info_outline);
            Uri alarmSound = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.pip3);

            NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, myApp.CHANNEL_ID_PIP);

            b.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_xdel_noti2)
                    .setLargeIcon(bm)
                    .setContentTitle(myApp.getAppContext().getString(R.string.alert_LukeNotification))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setWhen(System.currentTimeMillis())
                    .setGroup(group_id)
                    .setSound(alarmSound)
                    .setContentText(msg)
                    .setAutoCancel(true);
            b.setContentIntent(contentIntent);
            mNotificationManager.notify(1, b.build());
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GCMIntentService sendNotification2New. Exception: , " + e.getMessage().toString() + "\nStack: " + stack,
                    "", "", "", "");
        }
    }
}
