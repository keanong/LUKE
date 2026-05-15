package com.xdel.luke.JobScheduler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;

import com.google.gson.Gson;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WifiService extends Service {
    private Context ctx;
    ConnectivityManager myConnManager = null;

    public WifiService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int rtnValue = START_STICKY;
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        String wifistate = objGlobalMethods.getGCMPrefs(gcmp, "wifistate");
        try {
            ctx = myApp.getAppContext();
            myConnManager = (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
            NetworkInfo ni = myConnManager.getActiveNetworkInfo();
            if (ni != null) {
                if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (myConnManager == null) {
                        myConnManager = (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
                    }

                    NetworkInfo myNetworkInfo = myConnManager.getActiveNetworkInfo();
                    if (myNetworkInfo != null && myNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI && myNetworkInfo.isConnected()) {
                        //wifi connected
                        if (wifistate.equals("")) {
                            updateHQasync t = new updateHQasync(ctx, true);
                            t.execute();
                        }
                    } else if (myNetworkInfo != null && myNetworkInfo.getType() != ConnectivityManager.TYPE_WIFI && myNetworkInfo.isConnected()) {
                        //mobile data connected
                        if (!wifistate.equals("")) {
                            updateHQasync t = new updateHQasync(ctx, false);
                            t.execute();
                        }
                    } else {
                        //any other connection or not connected
                        if (!wifistate.equals("")) {
                            //left ofc wifi vicinity
                            updateHQasync t = new updateHQasync(ctx, false);
                            t.execute();
                        }
                    }
                } else {
                    updateHQasync t = new updateHQasync(ctx, false);
                    t.execute();
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WifiService onStartCommand. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return rtnValue;
        }
    }

    public static class updateHQasync extends AsyncTask<Void, Void, Void> {
        private final Context ctx;
        private final boolean wificonnected;

        public updateHQasync(Context actx, boolean awificonnected) {
            this.ctx = actx;
            this.wificonnected = awificonnected;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Boolean isConnectedToXDelWifi = false;
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            String bssidorMac = "";
            String wifistate = objGlobalMethods.getGCMPrefs(gcmp, "wifistate");

            try {
                WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifi = wifiManager.getConnectionInfo();
                String regid = objGlobalMethods.getRegistrationId(ctx, gcmp);
                if (wifi != null) {
                    int ip = wifi.getIpAddress();
                    bssidorMac = wifi.getBSSID();
                    String[] HomeMac = WebService.L2_GetWIFIHome(regid);
                    if (HomeMac != null && HomeMac.length > 0 && bssidorMac != null) {
                        List<String> HomeMacList = new ArrayList<String>(Arrays.asList(HomeMac));
                        if (HomeMacList.contains(bssidorMac.toUpperCase())) {
                            isConnectedToXDelWifi = true;
                        }
                    }

                    if (isConnectedToXDelWifi != null && isConnectedToXDelWifi) {
                        if (wifistate != null && !wifistate.equals("connected") && wificonnected) {
                            //connected to XDel wifi, do the necessary
                            Date prevWifiConnectedDateTime = null;
                            Gson gson = new Gson();
                            Date currWifiConnectedDateTime = new Date();
                            String WifiConnectedDateTimeJsonStr = gson.toJson(currWifiConnectedDateTime);
                            String prevWifiConnectedDateTimeJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "WifiConnectedDateTimeJsonStr");
                            prevWifiConnectedDateTime = prevWifiConnectedDateTimeJsonStr.equals("") ? null : gson.fromJson(prevWifiConnectedDateTimeJsonStr, Date.class);
                            objGlobalMethods.writeToFile("wifi connected", "", "", "", "");
                            if (prevWifiConnectedDateTime != null) {

                                long diffInMillisec = currWifiConnectedDateTime.getTime() - prevWifiConnectedDateTime.getTime();
                                long diffSeconds2 = diffInMillisec / 1000;
                                if (diffSeconds2 <= 300) {
                                    ///if last connected time is less than 5min old, ignore it
                                } else if (diffSeconds2 > 300) {
                                    ///if last connected time is more than 5min old
                                    updateReportHQ(regid, gcmp, WifiConnectedDateTimeJsonStr);
                                }
                            } else {
                                updateReportHQ(regid, gcmp, WifiConnectedDateTimeJsonStr);
                            }
                            //updateReportHQ(regid, gcmp, WifiConnectedDateTimeJsonStr);
                        }
                    } else {
                        String leftofc = objGlobalMethods.getGCMPrefs(gcmp, "leftofc");
                        if (leftofc != null && leftofc.equals("") && !wifistate.equals("")) {
                            objGlobalMethods.writeToFile("wifi disconnected", "", "", "", "");
                            updateLeaveHQ(regid, gcmp);
                        }
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at WifiService updateHQasync. Exception: " + e.getMessage().toString(), "", "", "", "");
            } finally {
                return null;
            }
        }
    }

    private static void updateReportHQ(String regid, SharedPreferences gcmp, String WifiConnectedDateTimeJsonStr) {
        Boolean L2_ReportHQResult = false;
        try {
            JobsHandler.addReportHQQueALPrefs(regid);
            objGlobalMethods.setGCMPrefs(gcmp, "wifistate", "connected");
            objGlobalMethods.setGCMPrefs(gcmp, "WifiConnectedDateTimeJsonStr", WifiConnectedDateTimeJsonStr);
            objGlobalMethods.setGCMPrefs(gcmp, "leftofc", "");
            Context ctx = myApp.getAppContext();
            ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at WifiIntentService updateReportHQ. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private static void updateLeaveHQ(String regid, SharedPreferences gcmp) {
        Boolean L2_LeaveHQResult = false;
        try {
            Date prevWifiConnectedDateTime = null;
            Gson gson = new Gson();
            Date currWifiConnectedDateTime = new Date();
            String prevWifiConnectedDateTimeJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "WifiConnectedDateTimeJsonStr");
            prevWifiConnectedDateTime = prevWifiConnectedDateTimeJsonStr.equals("") ? null : gson.fromJson(prevWifiConnectedDateTimeJsonStr, Date.class);

            if (prevWifiConnectedDateTime != null) {
                long diffInMillisec = currWifiConnectedDateTime.getTime() - prevWifiConnectedDateTime.getTime();
                long diffSeconds2 = diffInMillisec / 1000;
                if (diffSeconds2 > 120) {
                    ///if last connected time is more than 1min old
                    JobsHandler.addLeaveHQQueALPrefs(regid);
                    objGlobalMethods.setGCMPrefs(gcmp, "wifistate", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "leftofc", "true");
                    Context ctx = myApp.getAppContext();
                    ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                }
            }/* else {
                JobsHandler.addLeaveHQQueALPrefs(regid);
                objGlobalMethods.setGCMPrefs(gcmp, "wifistate", "");
                objGlobalMethods.setGCMPrefs(gcmp, "leftofc", "true");
                Context ctx = myApp.getAppContext();
                ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            }*/
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at WifiIntentService updateLeaveHQ. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
