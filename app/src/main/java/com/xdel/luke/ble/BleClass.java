package com.xdel.luke.ble;

import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.data.BleDevice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.List;

public class BleClass {

    public static void disconnectBTTL(){
        try {
            /*BleManager bm = BleManager.getInstance();
            List<BleDevice> bds = BleManager.getInstance().getAllConnectedDevice();
            BleDevice bd = null;
            if (bds != null && bds.size() > 0) {
                for (int i = 0; i <= bds.size()-1; i++) {
                    bd = bds.get(i);

                }
            }
            BleManager.getInstance().disconnectAllDevice();*/
            boolean asd = BleManager.getInstance().isConnected("E0:79:8D:6B:AC:3F");
            objGlobalMethods.showDialog(myApp.currentActivity, "ok", asd ? "connected" : "not connected").show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at BleClass disconnectBTTL. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }
}
