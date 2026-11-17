package com.xdel.luke.templogger;

import android.app.ActivityManager;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.viewpager.widget.PagerAdapter;

import com.google.gson.Gson;
import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.callback.BleScanCallback;
import com.tzone.bluetooth.data.BleDevice;
import com.tzone.bluetooth.data.BleScanState;
import com.tzone.bluetooth.scan.BleScanRuleConfig;
import com.tzone.devices.BaseDevice;
import com.tzone.devices.DeviceType;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.Rooting.RootGuard;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class bttemploggerclass {
    private Timer _Timer;
    public final String TAG = "bttemploggerclass";
    public List<Scan> DeviceList;
    public String Key = "";
    public List<String> requestedMac;
    public List<String> requestedDeviceIDX;
    public String[] macArray;
    public String[] TemperatureLoggerIDXArray;
    public List<MacDevicePair> MacDevicePairs;
    public MacDevicePair mdp;
    public String ARegID;
    public List<MacDevicePair> BTMacDevicePairs;
    public int DeviceFound = 0;

    public void setMacDevicePairs(List<MacDevicePair> aMacDevicePairs) {
        this.MacDevicePairs = aMacDevicePairs;
        this.BTMacDevicePairs = aMacDevicePairs;
        try {
            if (BTMacDevicePairs != null && BTMacDevicePairs.size() > 0) {
                for (int i = BTMacDevicePairs.size() - 1; i >= 0; i--) {
                    if (BTMacDevicePairs.get(i).Device_Type != 22) {
                        BTMacDevicePairs.remove(i);
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at bttemploggerclass. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void setARegID(String ARegID) {
        this.ARegID = ARegID;
    }

    public void scan() {
        try {
            //R6: no logger scan while RootGuard blocks this process
            if (RootGuard.blockOperation("bttemploggerclass.scan", null)) {
                StopScan(); //same cleanup as the screen-off branch: clears bttemploggerclassStarted, cancels any scan
                return;
            }
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
                BleManager.getInstance().cancelScan();
                objGlobalMethods.writeToFile("BleManager.getInstance().cancelScan() due BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING", "", "", "", "");
            }

            if (objGlobalMethods.isScreenDisplayOn(myApp.getAppContext())) {
                BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                        .setScanTimeOut(1000 * 60)
                        //.setAutoConnect(true)
                        .build();
                BleManager.getInstance().initScanRule(scanRuleConfig);
                BleManager.getInstance().scan(DeviceScanCallback);
                //objGlobalMethods.writeToFile("bttemploggerclass BleManager.getInstance().scan(DeviceScanCallback)", "", "", "", ""); ////on purpose
            } else {
                //objGlobalMethods.writeToFile("bttemploggerclass scan() not started due !isScreenDisplayOn.", "", "", "", ""); //on purpose
                StopScan();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at bttemploggerclass scan. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void StopScan() {
        try {
            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            objGlobalMethods.setBTLoggerRelatedPrefs(btp, "bttemploggerclassStarted", "");
            boolean t_null = false;
            boolean bm_null = false;

            if (_Timer != null) {
                _Timer.cancel();
                _Timer = null;
                t_null = true;
            }

            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
                BleManager.getInstance().cancelScan();
                bm_null = true;
            }
            /*String t = t_null ? "bttemploggerclass _Timer = null" : "";
            String b = bm_null ? "bttemploggerclass BleManager.getInstance().cancelScan()" : "";
            String s = "bttemploggerclass StopScan() done. " + (!objGlobalMethods.IsEmptyString(t) ? t + ", " : "") + b;
            objGlobalMethods.writeToFile(s, "", "", "", "");*/ ////on purpose

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at bttemploggerclass StopScan. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public BleScanCallback DeviceScanCallback = new BleScanCallback() {
        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            //objGlobalMethods.writeToFile("bttemploggerclass onScanFinished", "", "", "", "");
            ////on purpose
            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            objGlobalMethods.setBTLoggerRelatedPrefs(btp, "bttemploggerclassStarted", "");
        }

        @Override
        public void onScanStarted(boolean success) {
            try {
                if (objGlobalMethods.isScreenDisplayOn(myApp.getAppContext())) {
                    //objGlobalMethods.writeToFile("bttemploggerclass onScanStarted", "", "", "", ""); ////on purpose
                    SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                    objGlobalMethods.setBTLoggerRelatedPrefs(btp, "bttemploggerclassStarted", "t");
                    scanTimeout();
                } else {
                    //objGlobalMethods.writeToFile("bttemploggerclass onScanStarted StopScan started due !isScreenDisplayOn.", "", "", "", "");
                    ////on purpose
                    StopScan();
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at bttemploggerclass onScanStarted. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }

        @Override
        public void onScanning(BleDevice bleDevice) {
            if (objGlobalMethods.isScreenDisplayOn(myApp.getAppContext())) {
                if (bleDevice == null) {
                    objGlobalMethods.writeToFile("bttemploggerclass onScanning return due (bleDevice == null)", "", "", "", "");
                    return;
                }

                Scan device = new Scan();
                if (device.fromBroadcast(bleDevice)) {
                    //objGlobalMethods.writeToFile("bttemploggerclass onScanning calling AddOrUpdateNew", "", "", "", "");
                    AddOrUpdateNew(device);
                }
            } else {
                //objGlobalMethods.writeToFile("bttemploggerclass onScanning StopScan started due !isScreenDisplayOn.", "", "", "", ""); //on purpose
                StopScan();
            }
        }
    };

    public void AddOrUpdateNew(final Scan device) {
        TemperaturePayload tpl = null;

        try {
            if (device == null || device.getDeviceType() == DeviceType.Unknown) {
                //objGlobalMethods.writeToFile("bttemploggerclass AddOrUpdateNew return due (device == null || device.getDeviceType() == DeviceType.Unknown)", "", "", "", "");
                ////on purpose
                return;
            }

            if (MacDevicePairs == null || MacDevicePairs.size() == 0) {
                //objGlobalMethods.writeToFile("bttemploggerclass AddOrUpdateNew return due (MacDevicePairs == null || MacDevicePairs.size() == 0)", "", "", "", "");
                ////on purpose
                return;
            }

            if (Key != null && !Key.isEmpty()) {
                if (!device.getName().contains(Key) && !device.getMac().contains(Key) && !device.getID().contains(Key)) {
                    //objGlobalMethods.writeToFile("bttemploggerclass AddOrUpdateNew return due (!device.getName().contains(Key) && !device.getMac().contains(Key) && !device.getID().contains(Key))", "", "", "", "");
                    ////on purpose
                    return;
                }

            }

            if (DeviceList == null)
                DeviceList = new ArrayList<Scan>();

            for (int i = 0; i < DeviceList.size(); i++) {
                Scan d = DeviceList.get(i);
                if (d.getMac().equals(device.getMac())) {
                    d.BroadcastUpdate(device);
                    return;
                }
            }

            if (MacDevicePairs != null && MacDevicePairs.size() > 0) {
                for (int i = 0; i <= MacDevicePairs.size() -1; i++) {
                    if (device.getMac().equals(MacDevicePairs.get(i).Mac)) {
                        DeviceList.add(device);
                        objGlobalMethods.writeToFile("bttemploggerclass AddOrUpdateNew DeviceList.add(device). " + device.ID ,"","","","");
                        break;
                    }
                }
            }

            Scan d = null;
            String m = "";
            int Device_Type;
            for (int i = DeviceList.size() - 1; i >= 0; i--) {
                d = DeviceList.get(i);
                for (int x = MacDevicePairs.size() - 1; x >= 0; x--) {
                    mdp = MacDevicePairs.get(x);
                    m = mdp.Mac;
                    Device_Type = mdp.Device_Type;
                    //m = "asdassdasd";

                    if (Device_Type == 22 && !objGlobalMethods.IsEmptyString(m) && !objGlobalMethods.IsEmptyString(d.Mac) && d.Mac.equals(m)) {
                        //Log.i(TAG, "device detected => " + d.Mac + " " + d.getName());
                        objGlobalMethods.writeToFile(
                                "bttemploggerclass AddOrUpdateNew device detected => \n" + d.Mac + " " + d.getName() + " "
                                        + d.ID
                                        + "\nTemp: " + d.getTemperature() + d.getTemperatureUnitTypeID(), "", "", "", "");

                        if (!tplExist(mdp.MPDIDX)) {
                            tpl = new TemperaturePayload();
                            tpl.setBatteryLevel(d.getBattery());
                            tpl.setHumidity(d.getHumidity());
                            tpl.setTemperature(d.getTemperature());
                            tpl.setLat(0);
                            tpl.setLong(0);
                            tpl.setTimestamp(String.valueOf(d.getDeviceTime()));
                            tpl.setMPDIDX(mdp.MPDIDX);

                            if (tpll == null)
                                tpll = new ArrayList<>();

                            tpll.add(tpl);

                            _D = new BaseDevice();
                            _D.ID = d.getID();
                            _D.Name = d.getName();
                            _D.Mac = d.getMac();
                            _D.HardwareType = d.getHardwareType();
                            _D.Version = d.getVersion();

                            bs = new BaseDeviceScanObj();
                            bs.setScan(d);
                            bs.set_D(_D);
                            bs.setMPDIDX(mdp.MPDIDX);

                            if (list == null)
                                list = new ArrayList<>();

                            list.add(bs);
                            MacDevicePairs.remove(x);
                        }

                        break;
                    }
                }
            }

            if (objGlobalMethods.isScreenDisplayOn(myApp.getAppContext())) {
                if (list != null && list.size() > 0) {
                    //objGlobalMethods.writeToFile("bttemploggerclass AddOrUpdateNew list size: " + list.size(), "", "", "", "");
                    ////on purpose
                    bs = list.get(0);
                    if ( (btReport.IsRunning == null || !btReport.IsRunning) ) {
                        SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "btReportCallingClass", "bttemploggerclass");
                        DeviceFound++;
                        list.remove(0);
                        btReport br = new btReport();
                        br.setBs(bs);
                        br.setAlist(list);
                        br.scanNew();
                    } else {
                        /*objGlobalMethods.writeToFile("bttemploggerclass AddOrUpdateNew btReport not started. " +
                                ((btReport.IsRunning != null && btReport.IsRunning) ? "(btReport.IsRunning != null && btReport.IsRunning)" : ""), "", "", "", "");*/
                        ////on purpose
                    }
                }
            } else {
                objGlobalMethods.writeToFile("bttemploggerclass AddOrUpdateNew btReport not started due !isScreenDisplayOn.", "", "", "", "");
                StopScan();
            }
        } catch (Exception ex) {
            String s = JobsHandler.getStackTraceString(ex);
            objGlobalMethods.writeToFile("Exception at bttemploggerclass AddOrUpdateNew. Exception: " + ex.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void carryOnScan(ArrayList<BaseDeviceScanObj> alist) {
        try {
            if (alist != null && alist.size() > 0) {
                objGlobalMethods.writeToFile("bttemploggerclass carryOnScan list size: " + alist.size(), "", "", "", "");

                bs = alist.get(0);
                objGlobalMethods.writeToFile("bttemploggerclass carryOnScan processing.", "", "", "", "");
                SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                objGlobalMethods.setBTLoggerRelatedPrefs(btp, "btReportCallingClass", "bttemploggerclass");
                DeviceFound++;
                alist.remove(0);
                btReport br = new btReport();
                br.setBs(bs);
                br.setAlist(alist);
                br.scanNew();

                /*if ((btReport.CompletedScanID == null || btReport.CompletedScanID.size() == 0) || (btReport.CompletedScanID != null && btReport.CompletedScanID.size() > 0 && !btReport.CompletedScanID.contains(bs.scan.ID))) {

                    objGlobalMethods.writeToFile("bttemploggerclass carryOnScan processing.", "", "", "", "");
                    SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                    objGlobalMethods.setBTLoggerRelatedPrefs(btp, "btReportCallingClass", "bttemploggerclass");
                    DeviceFound++;
                    alist.remove(0);
                    btReport br = new btReport();
                    br.setBs(bs);
                    br.setAlist(alist);
                    br.scanNew();
                } else {
                    objGlobalMethods.writeToFile("bttemploggerclass carryOnScan btReport not started. " +
                            (!(btReport.CompletedScanID == null || btReport.CompletedScanID.size() == 0) ? "!(btReport.CompletedScanID == null || btReport.CompletedScanID.size() == 0)" : "") +
                            (!(btReport.CompletedScanID != null && btReport.CompletedScanID.size() > 0 && !btReport.CompletedScanID.contains(bs.scan.ID)) ? "!(btReport.CompletedScanID != null && btReport.CompletedScanID.size() > 0 && !btReport.CompletedScanID.contains(bs.scan.ID))" : ""), "", "", "", "");
                }*/
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at bttemploggerclass carryOnScan. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    CountDownTimer cdTimer = null;

    public boolean tplExist(long MPDIDX) {
        boolean asd = false;
        TemperaturePayload tpl = null;
        try {
            if (tpll != null && tpll.size() > 0) {
                for (int i = 0; i <= tpll.size() - 1; i++) {
                    tpl = tpll.get(i);
                    if (tpl.MPDIDX == MPDIDX) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at bttemploggerclass tplExist. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    //start of DeviceActivity class
    public ArrayList<BaseDeviceScanObj> list = null;
    public BaseDeviceScanObj bs = null;
    public ArrayList<BaseDevice> _Dlist = null;
    public BaseDevice _D = null;
    public ArrayList<TemperaturePayload> tpll = null;

    public void scanTimeout() {
        try {
            cdTimer = new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    if (cdTimer != null) {
                        cdTimer.cancel();
                        cdTimer = null;
                    }
                    if (DeviceFound > 0) {
                        //objGlobalMethods.writeToFile("bttemploggerclass scanTimeout StopScan started.", "", "", "", "");
                        ////on purpose
                        StopScan();
                    }
                }
            }.start();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at bttemploggerclass scanTimeout. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }


}
