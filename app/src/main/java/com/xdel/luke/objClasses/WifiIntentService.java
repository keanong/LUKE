package com.xdel.luke.objClasses;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.WebService;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WifiIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.xdel.luke.objClasses.action.FOO";
    private static final String ACTION_BAZ = "com.xdel.luke.objClasses.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.xdel.luke.objClasses.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.xdel.luke.objClasses.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, WifiIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, WifiIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public WifiIntentService() {
        super("WifiIntentService");
    }

    private static Intent IntentSvc;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Boolean isConnectedToXDelWifi = false;
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            IntentSvc = intent;
            String bssidorMac = "";
            String wifistate = objGlobalMethods.getGCMPrefs(gcmp, "wifistate");
            try {
                Context ctx = getApplicationContext();
                WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifi = wifiManager.getConnectionInfo();

                if (wifi != null) {
                    bssidorMac = wifi.getBSSID();
                    String regid = objGlobalMethods.getRegistrationId(ctx, gcmp);
                    String[] HomeMac = WebService.L2_GetWIFIHome(regid);
                    if (HomeMac != null && HomeMac.length > 0 && bssidorMac != null) {
                        List<String> HomeMacList = new ArrayList<String>(Arrays.asList(HomeMac));
                        if (HomeMacList.contains(bssidorMac.toUpperCase())) {
                            isConnectedToXDelWifi = true;
                        }
                    }

                    if (isConnectedToXDelWifi != null && isConnectedToXDelWifi) {
                        if (wifistate != null && !wifistate.equals("connected")) {
                            //connected to XDel wifi, do the necessary
                            Gson gson = new Gson();
                            Date currWifiConnectedDateTime = new Date();
                            String WifiConnectedDateTimeJsonStr = gson.toJson(currWifiConnectedDateTime);
                        /*Date prevWifiConnectedDateTime = null;
                        String prevWifiConnectedDateTimeJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "WifiConnectedDateTimeJsonStr");
                        prevWifiConnectedDateTime = prevWifiConnectedDateTimeJsonStr.equals("") ? null : gson.fromJson(prevWifiConnectedDateTimeJsonStr, Date.class);

                        if (prevWifiConnectedDateTime != null) {
                            long diffInMillisec = currWifiConnectedDateTime.getTime() - prevWifiConnectedDateTime.getTime();
                            long diffSeconds2 = diffInMillisec / 1000;
                            if (diffSeconds2 <= 600) {
                                ///if last connected time is less than 10min old, ignore it
                            } else if (diffSeconds2 > 600) {
                                ///if last connected time is more than 10min old
                                updateReportHQ(regid, gcmp, WifiConnectedDateTimeJsonStr);
                            }
                        } else {
                            updateReportHQ(regid, gcmp, WifiConnectedDateTimeJsonStr);
                        }*/

                            updateReportHQ(regid, gcmp, WifiConnectedDateTimeJsonStr);
                        }
                    } else {
                        String leftofc = objGlobalMethods.getGCMPrefs(gcmp, "leftofc");
                        if (leftofc != null && leftofc.equals("")) {
                            updateLeaveHQ(regid, gcmp);
                        }
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at objClasses WifiIntentService onHandleIntent. Exception: " + e.getMessage().toString(), "", "", "", "");
            } finally {
                WifiBroadcastReceiver.completeWakefulIntent(IntentSvc);
            }
        } else {

        }
    }

    private void updateReportHQ(String regid, SharedPreferences gcmp, String WifiConnectedDateTimeJsonStr) {
        Boolean L2_ReportHQResult = false;
        try {
            do {
                L2_ReportHQResult = WebService.ReportHQ(regid);
                if (L2_ReportHQResult) {
                    objGlobalMethods.setGCMPrefs(gcmp, "wifistate", "connected");
                    objGlobalMethods.setGCMPrefs(gcmp, "WifiConnectedDateTimeJsonStr", WifiConnectedDateTimeJsonStr);
                    objGlobalMethods.setGCMPrefs(gcmp,"leftofc","");
                }
            } while (L2_ReportHQResult == false || L2_ReportHQResult == null);

        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at WifiIntentService updateReportHQ. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at WifiIntentService updateReportHQ. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void updateLeaveHQ(String regid, SharedPreferences gcmp) {
        Boolean L2_LeaveHQResult = false;
        try {
            do {
                L2_LeaveHQResult = WebService.LeaveHQ(regid);
                if (L2_LeaveHQResult) {
                    objGlobalMethods.setGCMPrefs(gcmp,"leftofc","true");
                }
            } while (L2_LeaveHQResult == false || L2_LeaveHQResult == null);
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at WifiIntentService updateLeaveHQ. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at WifiIntentService updateLeaveHQ. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
