package com.xdel.luke.objClasses;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.xdel.luke.BCnSVC.ActiveService;
import com.xdel.luke.JobScheduler.WifiService;
import com.xdel.luke.Receivers.RecevierMgr;

public class WifiBroadcastReceiver extends WakefulBroadcastReceiver {

    private Context ctx;
    private String connectedBSSID;
    private boolean isreg;
    private String[] UsernamePwd = new String[4];
    private String username;
    private String password;

    public WifiBroadcastReceiver() {
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
            objGlobalMethods.writeToFile("Exception at WifiBroadcastReceiver register. Exception: " + s, "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at WifiBroadcastReceiver unregister. Exception: " + s, "", "", "", "");
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        ctx = context;
        Boolean connected = false;
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        String wifistate = objGlobalMethods.getGCMPrefs(gcmp, "wifistate");
        ConnectivityManager myConnManager = null;
        try {
            if (gcmp == null) {
                gcmp = objGlobalMethods.getGcmPreferences();
            }
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
            username = UsernamePwd[0];
            password = UsernamePwd[1];

            if (!username.equals("") && !password.equals("")) {
                //context.startService(new Intent(context, WifiService.class));
                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent i = new Intent(ctx, ActiveService.class);
                    i.setAction(ActiveService.ACTION_STARTWIFISERVICE);
                    ctx.startService(i);
                } else {
                    context.startService(new Intent(ctx, WifiService.class));
                }
            } else {
                RecevierMgr.unregisterbcrs();
            }

            //Util.scheduleWifiJob(context);

            /*////test 3
            NetworkInfo ni = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni == null) {
                myConnManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
               ni = myConnManager.getActiveNetworkInfo();
            }

            if(ni.getType() == ConnectivityManager.TYPE_WIFI){
                if (myConnManager == null) {
                    myConnManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                }

                NetworkInfo myNetworkInfo = myConnManager.getActiveNetworkInfo();
                if (myNetworkInfo != null && myNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI && myNetworkInfo.isConnected()) {
                    //wifi connected
                    if (wifistate.equals("")) {
                        objGlobalMethods.setGCMPrefs(gcmp,"wifistate","connecting");
                        ComponentName comp = new ComponentName(context.getPackageName(), WifiIntentService.class.getName());
                        startWakefulService(context, (intent.setComponent(comp)));
                        //Util.scheduleWifiJob(context);
                    }
                } else if (myNetworkInfo != null && myNetworkInfo.getType() != ConnectivityManager.TYPE_WIFI && myNetworkInfo.isConnected()) {
                    //mobile data connected
                    if (!wifistate.equals("")) {
                        objGlobalMethods.setGCMPrefs(gcmp, "wifistate" , "");
                        ComponentName comp = new ComponentName(context.getPackageName(), WifiIntentService.class.getName());
                        startWakefulService(context, (intent.setComponent(comp)));
                        //Util.scheduleWifiJob(context);
                    }
                } else {
                    //any other connection or not connected
                    if (!wifistate.equals("")) {
                        //left ofc wifi vicinity
                        objGlobalMethods.setGCMPrefs(gcmp, "wifistate" , "");
                        ComponentName comp = new ComponentName(context.getPackageName(), WifiIntentService.class.getName());
                        startWakefulService(context, (intent.setComponent(comp)));
                        //Util.scheduleWifiJob(context);
                    }
                }
            }
            ////test 3*/
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at objClasses WifiBroadcastReceiver onReceive. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    /**
     * Detect you are connected to a specific network.
     */
    private boolean checkConnectedToDesiredWifi() {
        boolean connected = false;
        try {
            String desiredMacAddress1 = "56:d9:e7:0b:9b:ac";
            String desiredMacAddress2 = "56:d9:e7:0b:a1:1a";

            WifiManager wifiManager =
                    (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);

            WifiInfo wifi = wifiManager.getConnectionInfo();

            if (wifi != null) {
                String bssid = wifi.getBSSID();
                if (desiredMacAddress1.equals(bssid) || desiredMacAddress2.equals(bssid)) {
                    connectedBSSID = bssid;
                    connected = true;
                }

            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at objClasses WifiBroadcastReceiver checkConnectedToDesiredWifi. Exception: " + e.getMessage().toString(), "", "", "", "");
            connected = false;
        } finally {
            return connected;
        }
    }

}
