package com.xdel.luke.BCnSVC;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.xdel.luke.AddressListNewActivity;
import com.xdel.luke.AddressListRoutingActivity;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsListGroupActivity;
import com.xdel.luke.MPD_CheckOutActivity;
import com.xdel.luke.MapsAllActivity;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objMPD_Info;
import com.xdel.luke.templogger.TempLoggerRegActivity;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MpdRefreshIntentService extends IntentService {
    ConnectionDetector cd;



    public MpdRefreshIntentService() {
        super("MpdRefreshIntentService");
    }

        @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Context ctx = myApp.getAppContext();
            try {
                cd = new ConnectionDetector(null);
                if (cd.isConnectingToInternet()) {
                    Log.i("MpdRefreshIntentService","onHandleIntent");
                    ArrayList<objMPD_Info> mpds = objGlobalMethods.grabMPDFromServer();
                    //objGlobalMethods.setMPDsPrefStr(mpds, objGlobalMethods.getGcmPreferences());
                    objGlobalMethods.setMPDsPrefStrNew(mpds);
                    refreshMPD_CheckOutList();
                } else {
                    objGlobalMethods.writeToFile("MpdRefreshIntentService onHandleIntent cannot run due no network.","","","","");
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at MpdRefreshIntentService onHandleIntent. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            }
        }
    }

    private void refreshMPD_CheckOutList() {
        try {
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MPD_CheckOutActivity.class) {
                final MPD_CheckOutActivity act = (MPD_CheckOutActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.refreshlist();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MapsAllActivity.class) {
                final MapsAllActivity act = (MapsAllActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListRoutingActivity.class) {
                final AddressListRoutingActivity act = (AddressListRoutingActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class) {
                final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListNewActivity.class) {
                final AddressListNewActivity act = (AddressListNewActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobsListGroupActivity.class) {
                final JobsListGroupActivity act = (JobsListGroupActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DashboardSubActivity.class) {
                final DashboardSubActivity act = (DashboardSubActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerRegActivity.class) {
                final TempLoggerRegActivity act = (TempLoggerRegActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.initPage();
                    }
                });
            }/* else {
                ArrayList<objMPD_Info> mpds = objGlobalMethods.grabMPDFromServer();
                SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setMPDsPrefStr(mpds, GcmPreferences);
            }*/
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MpdRefreshIntentService refreshMPD_CheckOutList. Exception: " + s, "", "", "", "");
        }
    }

}