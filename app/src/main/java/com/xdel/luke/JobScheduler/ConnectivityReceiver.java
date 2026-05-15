package com.xdel.luke.JobScheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

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

public class ConnectivityReceiver extends BroadcastReceiver {

    private ConnectivityReceiverListener mConnectivityReceiverListener;

    ConnectivityReceiver(ConnectivityReceiverListener listener) {
        mConnectivityReceiverListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        try {
            mConnectivityReceiverListener.onNetworkConnectionChanged(isConnected(context));
        } catch (Exception e) {
            boolean asd = false;
            boolean asddd = asd;
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = null;
        NetworkInfo activeNetwork = null;
        try {
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetwork = cm.getActiveNetworkInfo();

            String username = "";
            String password = "";
            String[] UsernamePwd = new String[4];

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(objGlobalMethods.getGcmPreferences());
            username = UsernamePwd[0];
            password = UsernamePwd[1];

            //objGlobalMethods.writeToFile("isConnected at ConnectivityReceiver.", "", "", "", "");
            ////wifi detection related
            if (!objGlobalMethods.IsEmptyString(username) && !objGlobalMethods.IsEmptyString(password)) {
                if (activeNetwork != null) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                        String wifistate = objGlobalMethods.getGCMPrefs(gcmp, "wifistate");
                        if (activeNetwork.isConnected() && wifistate.equals("")) {
                            //wifi connected
                            objGlobalMethods.writeToFile("wifi isConnected at ConnectivityReceiver.", "", "", "", "");
                            updateHQasync t = new updateHQasync(context, true);
                            t.execute();
                        /*objGlobalMethods.writeToFile("starting BackendRun_IntentService at ConnectivityReceiver when connected to Wifi.","","","","");
                        JobsHandler.backendRun(gcmp, objGlobalMethods.getUpdateJobInfoPreferences());*/
                        }
                    } else if (activeNetwork.isConnected()) {
                        objGlobalMethods.writeToFile("Mobile data isConnected at ConnectivityReceiver, starting BackendRun_IntentService when connected to mobile data.", "", "", "", "");
                        updateHQasync t = new updateHQasync(context, false);
                        t.execute();
                    }
                }
            }
            //wifi detection related
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ConnectivityReceiver isConnected. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

    //wifi related
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
                                    Long l = currWifiConnectedDateTime.getTime();
                                    String timestampstr = "/Date(" + l + ")/";
                                    updateReportHQ(regid, gcmp, WifiConnectedDateTimeJsonStr, timestampstr);
                                }
                            } else {
                                Long l = currWifiConnectedDateTime.getTime();
                                String timestampstr = "/Date(" + l + ")/";
                                updateReportHQ(regid, gcmp, WifiConnectedDateTimeJsonStr, timestampstr);
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
                Context ctx = myApp.getAppContext();
                ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at WifiService updateHQasync. Exception: " + e.getMessage().toString(), "", "", "", "");
            } finally {
                return null;
            }
        }
    }

    private static void updateReportHQ(String regid, SharedPreferences gcmp, String WifiConnectedDateTimeJsonStr, String atimestampstr) {
        Boolean L2_ReportHQResult = false;
        try {
            Gson gson = new Gson();
            objReportHQ rhq = new objReportHQ(regid, atimestampstr);
            String rhqJsonStr = gson.toJson(rhq);
            //JobsHandler.addReportHQQueALPrefs(regid);
            JobsHandler.addReportHQQueALPrefs(rhqJsonStr);
            objGlobalMethods.setGCMPrefs(gcmp, "wifistate", "connected");
            objGlobalMethods.setGCMPrefs(gcmp, "WifiConnectedDateTimeJsonStr", WifiConnectedDateTimeJsonStr);
            objGlobalMethods.setGCMPrefs(gcmp, "leftofc", "");
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
                    Long l = currWifiConnectedDateTime.getTime();
                    String timestampstr = "/Date(" + l + ")/";
                    objReportHQ rhq = new objReportHQ(regid, timestampstr);
                    String rhqJsonStr = gson.toJson(rhq);

                    //JobsHandler.addLeaveHQQueALPrefs(regid);
                    JobsHandler.addLeaveHQQueALPrefs(rhqJsonStr);
                    objGlobalMethods.setGCMPrefs(gcmp, "wifistate", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "leftofc", "true");
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at WifiIntentService updateLeaveHQ. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

}
