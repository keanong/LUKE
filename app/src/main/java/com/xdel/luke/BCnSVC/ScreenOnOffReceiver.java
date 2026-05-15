package com.xdel.luke.BCnSVC;

import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.data.BleScanState;
import com.xdel.luke.JobScheduler.Util;
import com.xdel.luke.JobScheduler.btLoggerJobService;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.mpd.mpdclass;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.MacDevicePair;
import com.xdel.luke.templogger.btReport;
import com.xdel.luke.templogger.bttemploggerclass;
import com.xdel.luke.templogger.temploggerclass;

import java.util.List;

public class ScreenOnOffReceiver extends BroadcastReceiver {

    public boolean IsRegistered;

    public void setRegistered(boolean registered) {
        IsRegistered = registered;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
                BleManager.getInstance().cancelScan();
                //objGlobalMethods.writeToFile("ScreenOnOffReceiver onReceive BleManager.getInstance().cancelScan() due BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING", "", "", "", "");
                ////on purpose
            }
            temploggerclass.disposeTimer();
            //objGlobalMethods.writeToFile("Screen Off", "", "", "", ""); ////on purpose
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            boolean morethan15min = true;

            try {
                Boolean BTEnabled = temploggerclass.isBTEnabled();

                if (BTEnabled != null && BTEnabled) {
                    //SharedPreferences g = objGlobalMethods.getGcmPreferences();
                    SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                    //String mdpsJsonStr =  objGlobalMethods.getGCMPrefs(g, "mdpsJsonStr");
                    String mdpsJsonStr =  objGlobalMethods.getMPDRelatedSP_KeyValue("mdpsJsonStr");

                    if (!objGlobalMethods.IsEmptyString(mdpsJsonStr) && !mdpsJsonStr.equals("[]")) {
                        boolean hasBTTL = !objGlobalMethods.IsEmptyString(objGlobalMethods.getBTLoggerRelatedPrefs(btp, "hasBTTL"));
                        if (hasBTTL) {
                            boolean reqRegBTTL = mpdclass.reqRegBTTL(btp);

                            if (!reqRegBTTL)
                                temploggerclass.startTimer(0);

                            String lastBTLScannedTimeLong = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "lastBTLScannedTimeLong");
                            if (!objGlobalMethods.IsEmptyString(lastBTLScannedTimeLong))
                                morethan15min = JobsHandler.moreThanXminutesZerofied(Long.parseLong(lastBTLScannedTimeLong), 15);
                            //morethan15min = true;
                            if (morethan15min && !reqRegBTTL) {
                                String ARegID = objGlobalMethods.getRegistrationIdOnly();
                                String bttemploggerclassStarted = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "bttemploggerclassStarted");
                                boolean emptyARegID = objGlobalMethods.IsEmptyString(ARegID);
                                boolean emptybttemploggerclassStarted = objGlobalMethods.IsEmptyString(bttemploggerclassStarted);
                                /*objGlobalMethods.writeToFile("Screen On. Scanning BTL. >= 15min !reqRegBTTL.\n" +
                                        "ScreenOnOffReceiver ARegiD " + (emptyARegID ? "Empty." : "!Empty.") + " bttemploggerclassStarted " +
                                        (emptybttemploggerclassStarted ? "Empty." : "!Empty."), "", "", "", "");*/
                                ////on purpose

                                if (!emptybttemploggerclassStarted) {
                                    objGlobalMethods.setBTLoggerRelatedPrefs(btp, "bttemploggerclassStarted", "");
                                /*BleManager.getInstance().disconnectAllDevice();
                                BleManager.getInstance().destroy();
                                objGlobalMethods.writeToFile("ScreenOnOffReceiver BleManager.getInstance().destroy()","","","","");*/
                                }

                                if (!objGlobalMethods.IsEmptyString(ARegID)) {
                                    if (objGlobalMethods.isScreenDisplayOn(myApp.getAppContext())) {
                                        if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
                                            BleManager.getInstance().cancelScan();
                                            //objGlobalMethods.writeToFile("ScreenOnOffReceiver onReceive BleManager.getInstance().cancelScan() due BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING", "", "", "", "");
                                            ////on purpose
                                        }

                                        BleManager.getInstance().disconnectAllDevice();
                                        BleManager.getInstance().destroy();
                                        BleManager.getInstance().init(myApp.getInstance());
                                        BleManager.getInstance()
                                                .enableLog(true)
                                                .setReConnectCount(3, 5000)
                                                .setConnectOverTime(20000)
                                                .setOperateTimeout(5000);
                                        //objGlobalMethods.writeToFile("BleManager initiated at ScreenOnOffReceiver","","","","");
                                        ////on purpose

                                        btReport.IsRunning = null;

                                        //objGlobalMethods.writeToFile("ScreenOnOffReceiver bttemploggerclass scan started.", "", "", "", "");
                                        ////on purpose
                                        bttemploggerclass btc = new bttemploggerclass();
                                        Gson gson = new Gson();
                                        List<MacDevicePair> mdps = gson.fromJson(mdpsJsonStr, new TypeToken<List<MacDevicePair>>() {
                                        }.getType());
                                        btc.setMacDevicePairs(mdps);
                                        btc.setARegID(ARegID);
                                        btc.scan();
                                    }
                                }
                            } else {
                                //objGlobalMethods.writeToFile("Screen On. Not scanning BTL." + (!morethan15min ? " < 15min." : " >= 15min.") + (!reqRegBTTL ? " !reqRegBTTL" : " reqRegBTTL"), "", "", "", "");
                                ////on purpose
                            }
                        }
                    } else {
                        //objGlobalMethods.writeToFile("Screen On mdpsJsonStr empty", "", "", "", "");
                        ////on purpose
                    }

                } else if (BTEnabled != null && !BTEnabled) {
                    //objGlobalMethods.writeToFile("Screen On !BTEnabled", "", "", "", "");
                    ////on purpose
                    temploggerclass.promptToOnBT();
                } else {
                    //objGlobalMethods.writeToFile("Screen On Device does not support Bluetooth", "", "", "", "");
                    ////on purpose
                }

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ScreenOnOffReceiver onReceive. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    public void registerNew(final Context context) {
        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            context.registerReceiver(this, filter);
        } catch(IllegalArgumentException e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at ScreenOnOffReceiver registerNew. Exception: " + s, "", "", "", "");
        }
    }

    public void unregisterNew(final Context context) {
        try {
            context.unregisterReceiver(this);
        } catch(IllegalArgumentException e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at ScreenOnOffReceiver unregisterNew. Exception: " + s, "", "", "", "");
        }
    }

    public void register(final Context context) {
        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            context.registerReceiver(this, filter);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at ScreenOnOffReceiver register. Exception: " + s, "", "", "", "");
        }
    }

    public void unregister(final Context context) {
        try {
            context.unregisterReceiver(this);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at ScreenOnOffReceiver unregister. Exception: " + s, "", "", "", "");
        }
    }

    public void StopBLEscanBLEconnect(){
        try {
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
                BleManager.getInstance().cancelScan();
                objGlobalMethods.writeToFile("ScreenOnOffReceiver BleManager.getInstance().cancelScan() due BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING", "", "", "", "");
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at ScreenOnOffReceiver unregister. Exception: " + s, "", "", "", "");
        }
    }

}