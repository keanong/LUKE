package com.xdel.luke.Receivers;

import com.xdel.luke.BCnSVC.ScreenOnOffReceiver;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.BattLvl_Receiver;
import com.xdel.luke.Common_BroadcastReceiver;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.NetworkChanged_BroadcastReceiver;
import com.xdel.luke.Rooting.RootGuard;
import com.xdel.luke.objClasses.WifiBroadcastReceiver;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

public class RecevierMgr {
    private static WifiBroadcastReceiver wifibcr;
    private static Common_BroadcastReceiver commonbcr;
    private static BackendRun_BroadcastReceiver backendrunbcr;
    private static ScreenOnOffReceiver soor;
    private static BattLvl_Receiver battlvlbcr;

    public void regScreenOnOffReceiver(){
        try {
            if (soor == null)
                soor = new ScreenOnOffReceiver();

            if (!soor.IsRegistered)
                soor.register(myApp.getAppContext());
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RecevierMgr regScreenOnOffReceiver. Exception: " + s, "", "", "", "");
        }
    }

    public void unregScreenOnOffReceiver() {
        try {
            if (soor != null) {
                soor.setRegistered(false);
                soor.unregister(myApp.getAppContext());
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RecevierMgr regScreenOnOffReceiver. Exception: " + s, "", "", "", "");
        }
    }

    public static void regScreenOnOffReceiverNew() {
        //R6: no screen-on BLE trigger while RootGuard blocks this process
        if (RootGuard.blockOperation("RecevierMgr.regScreenOnOffReceiverNew", null)) {
            return;
        }
        try {
            if (soor == null)
                soor = new ScreenOnOffReceiver();

            if (!soor.IsRegistered) {
                soor.register(myApp.getAppContext());
                soor.setRegistered(true);
                //objGlobalMethods.writeToFile("ScreenOnOffReceiver Registered", "", "", "", "");

            }

            /*soor.registerNew(myApp.getAppContext());
            objGlobalMethods.writeToFile("ScreenOnOffReceiver Registered", "", "", "", "");*/
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RecevierMgr regScreenOnOffReceiverNew. Exception: " + s, "", "", "", "");
        }
    }

    public static void unregScreenOnOffReceiverNew() {
        try {
            if (soor != null) {
                soor.setRegistered(false);
                soor.unregister(myApp.getAppContext());
                objGlobalMethods.writeToFile("ScreenOnOffReceiver Unregistered", "", "", "", "");

                /*soor.unregisterNew(myApp.getAppContext());
                objGlobalMethods.writeToFile("ScreenOnOffReceiver Unregistered", "", "", "", "");*/
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RecevierMgr unregScreenOnOffReceiverNew. Exception: " + s, "", "", "", "");
        }
    }

    public static void registerbcrs() {
        try {
            if (wifibcr == null) {
                wifibcr = new WifiBroadcastReceiver();
                wifibcr.register(myApp.getApplicationCtx());
                //objGlobalMethods.writeToFile("Wifi broadcastReceiver registered.","","","","");
            }
            if (commonbcr == null) {
                commonbcr = new Common_BroadcastReceiver();
                commonbcr.register(myApp.getApplicationCtx());
                //objGlobalMethods.writeToFile("Common_BroadcastReceiver registered.","","","","");
            }
            /*if (backendrunbcr ==  null) {
                backendrunbcr = new BackendRun_BroadcastReceiver();
                backendrunbcr.register(myApp.getApplicationCtx());
                //objGlobalMethods.writeToFile("BackendRun_BroadcastReceiver registered.","","","","");
            }*/
            /*if (networkchangedbcr == null) {
                networkchangedbcr = new NetworkChanged_BroadcastReceiver();
                networkchangedbcr.register(myApp.getAppContext());
                //objGlobalMethods.writeToFile("NetworkChanged_BroadcastReceiver registered.","","","","");
            }*/

        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at RecevierMgr registerbcrs. Exception: " + s, "", "", "", "");
        }
    }

    public static void unregisterbcrs() {
        try {
            if (wifibcr != null) {
                wifibcr.unregister(myApp.getAppContext());
            }
            if (commonbcr != null) {
                commonbcr.unregister(myApp.getAppContext());
            }
            if (backendrunbcr != null) {
                backendrunbcr.unregister(myApp.getAppContext());
            }
            /*if (networkchangedbcr != null) {
                networkchangedbcr.unregister(myApp.getAppContext());
            }*/
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at RecevierMgr unregisterbcrs. Exception: " + s, "", "", "", "");
        }
    }
}
