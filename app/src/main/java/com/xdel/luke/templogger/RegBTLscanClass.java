package com.xdel.luke.templogger;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.gson.Gson;
import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.callback.BleScanCallback;
import com.tzone.bluetooth.data.BleDevice;
import com.tzone.bluetooth.data.BleScanState;
import com.tzone.bluetooth.scan.BleScanRuleConfig;
import com.tzone.devices.BaseDevice;
import com.tzone.devices.DeviceType;
import com.xdel.luke.BottomSheet.ProgressBarDialog;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class RegBTLscanClass {
    private Timer _Timer;
    public  final String TAG = "bttemploggerclass";
    public List<Scan> DeviceList;
    public String Key = "";
    public List<String> requestedMac;
    public List<String> requestedDeviceIDX;
    public String[] macArray;
    public String[] TemperatureLoggerIDXArray;
    public List<lukeTLregistry> MacDevicePairs;
    public List<lukeTLregistry> MacDevicePairsToBeSaved;
    public lukeTLregistry mdp;
    public String ARegID;
    public int DeviceFound = 0;
    private ProgressBarDialog pdTempRead;

    public void setMacDevicePairsToBeSaved(List<lukeTLregistry> macDevicePairsToBeSaved) {
        MacDevicePairsToBeSaved = new ArrayList<lukeTLregistry>(macDevicePairsToBeSaved);
    }

    public void setMacDevicePairs(List<lukeTLregistry> aMacDevicePairs){
        this.MacDevicePairs = aMacDevicePairs;
    }

    public void setARegID(String ARegID) {
        this.ARegID = ARegID;
    }

    public void scan() {
        try {
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING)
                BleManager.getInstance().cancelScan();
            BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                    .setScanTimeOut(1000 * 60)
                    //.setAutoConnect(true)
                    .build();
            BleManager.getInstance().initScanRule(scanRuleConfig);
            BleManager.getInstance().scan(DeviceScanCallback);
            objGlobalMethods.writeToFile("RegBTLscanClass BleManager.getInstance().scan(DeviceScanCallback)", "", "", "", "");


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RegBTLscanClass scan. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void StopScan(){
        try {
            objGlobalMethods.setBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "RegBTLscanClassStarted", "");
            //objGlobalMethods.writeToFile("RegBTLscanClass StopScan started." ,"","","","");

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
            String t = t_null ? "RegBTLscanClass _Timer = null" : "";
            String b = bm_null ? "RegBTLscanClass BleManager.getInstance().cancelScan()" : "";
            String s = (!objGlobalMethods.IsEmptyString(t) ? t + ", " : "") + b;

            if (MacDevicePairsToBeSaved != null && MacDevicePairsToBeSaved.size() > 0) {
                String lukeTLregistryListJsonStr = new Gson().toJson(MacDevicePairsToBeSaved);
                objGlobalMethods.writeToFile("RegBTLscanClass StopScan\n"+ lukeTLregistryListJsonStr + "\n","","","","");
                objGlobalMethods.setBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "lukeTLregistryListJsonStr", lukeTLregistryListJsonStr);
            }

            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerRegActivity.class) {
                final TempLoggerRegActivity act = (TempLoggerRegActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        temploggerclass.disposeTimer();
                        temploggerclass.startTimer(0);
                        act.initPage();
                    }
                });
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RegBTLscanClass StopScan. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public BleScanCallback DeviceScanCallback = new BleScanCallback() {
        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            objGlobalMethods.writeToFile("RegBTLscanClass onScanFinished", "", "", "", "");
            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            String[] BTprefName = new String[]{"RegBTLscanClassStarted", "bttemploggerclassStarted"};
            String[] BTprefvalue = new String[]{"", ""};
            objGlobalMethods.setBTLoggerRelatedMass(btp, BTprefName, BTprefvalue);

            //objGlobalMethods.writeToFile("RegBTLscanClass onScanFinished started." ,"","","","");
            //Log.i(TAG, "DeviceFound => " + DeviceFound);
            try {
                if (pdTempRead != null && pdTempRead.isShowing()) {
                    pdTempRead.dismiss();
                    pdTempRead = null;
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at TempLoggerRegActivity RegBTLscanClass onScanFinished. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            }
        }

        @Override
        public void onScanStarted(boolean success) {
            //objGlobalMethods.writeToFile("onScanStarted => " + success,"","","","");
            //Log.i(TAG, "onScanStarted => " + success);
            try {
                objGlobalMethods.writeToFile("RegBTLscanClass onScanStarted started." ,"","","","");
                objGlobalMethods.setBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "RegBTLscanClassStarted", "t");
                pdTempRead = new ProgressBarDialog(myApp.currentActivity);
                pdTempRead.setCanceledOnTouchOutside(false);
                pdTempRead.setCancelable(false);
                pdTempRead.setText(myApp.getAppContext().getString(R.string.temp_read));
                pdTempRead.show();
                scanTimeout();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at RegBTLscanClass onScanStarted. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            }
        }

        @Override
        public void onScanning(BleDevice bleDevice) {
            if (bleDevice == null)
                return;

            Scan device = new Scan();

            if (device.fromBroadcast(bleDevice)) {
                objGlobalMethods.writeToFile("RegBTLscanClass onScanning calling AddOrUpdate", "", "", "", "");
                AddOrUpdate(device);
            }
        }
    };

    public void AddOrUpdate(final Scan device) {
        TemperaturePayload tpl = null;
        Date dt = new Date();
        Long l = dt.getTime();
        //objGlobalMethods.writeToFile("RegBTLscanClass AddOrUpdate started." ,"","","","");

        try {
            if (device == null || device.getDeviceType() == DeviceType.Unknown)
                return;

            if (MacDevicePairs == null || MacDevicePairs.size() == 0) {
                return;
            }

            if (Key != null && !Key.isEmpty()) {
                if (!device.getName().contains(Key)
                        && !device.getMac().contains(Key)
                        && !device.getID().contains(Key))
                    return;
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
                        objGlobalMethods.writeToFile("RegBTLscanClass DeviceList.add(device). " + device.ID ,"","","","");
                        break;
                    }
                }
            }
            /*DeviceList.add(device);
            objGlobalMethods.writeToFile("RegBTLscanClass DeviceList.add(device). " + device.ID ,"","","","");*/


            if (DeviceList != null && DeviceList.size() > 0 && MacDevicePairs != null && MacDevicePairs.size() > 0 && DeviceList.size() == MacDevicePairs.size()) {
                Scan d = null;
                String m = "";
                String mm = "";
                int Device_Type;
                boolean macremoved = false;
                for (int i = DeviceList.size() -1; i >= 0; i--) {
                    d = DeviceList.get(i);
                    for (int x = MacDevicePairs.size() -1; x >= 0; x--) {
                        mdp = MacDevicePairs.get(x);
                        m = mdp.Mac;
                        macremoved = false;
                        //m = "asdassdasd";

                        if (!objGlobalMethods.IsEmptyString(m) && !objGlobalMethods.IsEmptyString(d.Mac) &&  d.Mac.equals(m) && mdp.registered == 0) {
                            //Log.i(TAG, "device detected => " + d.Mac + " " + d.getName());
                            objGlobalMethods.writeToFile(
                                    "RegBTLscanClass AddOrUpdate device detected => \n" + d.Mac + " " + d.getName() + " "
                                            + d.ID
                                            + "\nTemp: " + d.getTemperature() + d.getTemperatureUnitTypeID()
                                            + "\nBattery: " + d.getBattery()
                                            + "\nLastActive: " + d.getLastActiveTime()
                                            + "\nDeviceTime: " + d.getDeviceTime(),"","","","");

                            for (int z = 0; z <= MacDevicePairsToBeSaved.size() -1; z ++) {
                                mm = MacDevicePairsToBeSaved.get(z).Mac;
                                if (!objGlobalMethods.IsEmptyString(mm) && d.Mac.equals(mm)) {
                                    MacDevicePairsToBeSaved.get(z).setRegtimestamplong(l);
                                    //MacDevicePairsToBeSaved.get(z).setRegtimestamplong(Long.parseLong("1695709230000")); //setting earlier date
                                    MacDevicePairsToBeSaved.get(z).setRegistered(1);


                                }
                            }

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

                if (list != null && list.size() > 0) {
                    objGlobalMethods.writeToFile("list size: " + list.size(),"","","","");
                    bs = list.get(0);
                    if ((btReport.IsRunning == null || !btReport.IsRunning))  {

                        //objGlobalMethods.writeToFile("RegBTLscanClass AddOrUpdate btReport started." ,"","","","");
                        SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "btReportCallingClass", "RegBTLscanClass");
                        DeviceFound++;
                        list.remove(0);
                        btReport br = new btReport();
                        br.setLukeTLregistrylist(MacDevicePairsToBeSaved);
                        br.setBs(bs);
                        br.setAlist(list);
                        br.scanNew();
                    } else {
                        objGlobalMethods.writeToFile("RegBTLscanClass AddOrUpdatebtReport not started. " +
                                ((btReport.IsRunning != null && btReport.IsRunning) ? "(btReport.IsRunning != null && btReport.IsRunning)" : ""),"","","","");
                    }
                }

                /*if (list != null && list.size() > 0) {
                    objGlobalMethods.writeToFile("list size: " + list.size(),"","","","");
                    bs = list.get(0);
                    if ((btReport.IsRunning == null || !btReport.IsRunning) && ((btReport.CompletedScanID == null || btReport.CompletedScanID.size() == 0) || (btReport.CompletedScanID != null && btReport.CompletedScanID.size() > 0 && !btReport.CompletedScanID.contains(bs.scan.ID))))  {

                        //objGlobalMethods.writeToFile("RegBTLscanClass AddOrUpdate btReport started." ,"","","","");
                        SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "btReportCallingClass", "RegBTLscanClass");
                        DeviceFound++;
                        list.remove(0);
                        btReport br = new btReport();
                        br.setLukeTLregistrylist(MacDevicePairsToBeSaved);
                        br.setBs(bs);
                        br.setAlist(list);
                        br.scanNew();
                    } else {
                        objGlobalMethods.writeToFile("RegBTLscanClass AddOrUpdatebtReport not started. " +
                                (!(btReport.IsRunning == null || !btReport.IsRunning) ? "!(btReport.IsRunning == null || !btReport.IsRunning)" : "") +
                                (!(btReport.CompletedScanID == null || btReport.CompletedScanID.size() == 0) ? "!(btReport.CompletedScanID == null || btReport.CompletedScanID.size() == 0)" : "") +
                                (!(btReport.CompletedScanID != null && btReport.CompletedScanID.size() > 0 && !btReport.CompletedScanID.contains(bs.scan.ID)) ? "!(btReport.CompletedScanID != null && btReport.CompletedScanID.size() > 0 && !btReport.CompletedScanID.contains(bs.scan.ID))" : ""),"","","","");
                    }
                }*/
            }
        } catch (Exception ex) {
            String s = JobsHandler.getStackTraceString(ex);
            objGlobalMethods.writeToFile("Exception at RegBTLscanClass AddOrUpdate. Exception: " + ex.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void prepToGenReport() {
        TemperaturePayload tpl = null;
        Date dt = new Date();
        Long l = dt.getTime();
        objGlobalMethods.writeToFile("RegBTLscanClass prepToGenReport started." ,"","","","");
        try {
            if (DeviceList != null && DeviceList.size() > 0 && MacDevicePairs != null && MacDevicePairs.size() > 0 && DeviceList.size() == MacDevicePairs.size()) {
                Scan d = null;
                String m = "";
                String mm = "";
                int Device_Type;
                boolean macremoved = false;
                for (int i = DeviceList.size() -1; i >= 0; i--) {
                    d = DeviceList.get(i);
                    for (int x = MacDevicePairs.size() -1; x >= 0; x--) {
                        mdp = MacDevicePairs.get(x);
                        m = mdp.Mac;
                        macremoved = false;
                        //m = "asdassdasd";

                        if (!objGlobalMethods.IsEmptyString(m) && !objGlobalMethods.IsEmptyString(d.Mac) &&  d.Mac.equals(m) && mdp.registered == 0) {
                            //Log.i(TAG, "device detected => " + d.Mac + " " + d.getName());
                            objGlobalMethods.writeToFile(
                                    "RegBTLscanClass prepToGenReport device detected => \n" + d.Mac + " " + d.getName() + " "
                                            + d.ID
                                            + "\nTemp: " + d.getTemperature() + d.getTemperatureUnitTypeID()
                                            + "\nBattery: " + d.getBattery()
                                            + "\nLastActive: " + d.getLastActiveTime()
                                            + "\nDeviceTime: " + d.getDeviceTime(),"","","","");

                            for (int z = 0; z <= MacDevicePairsToBeSaved.size() -1; z ++) {
                                mm = MacDevicePairsToBeSaved.get(z).Mac;
                                if (!objGlobalMethods.IsEmptyString(mm) && d.Mac.equals(mm)) {
                                    MacDevicePairsToBeSaved.get(z).setRegtimestamplong(l);
                                    //MacDevicePairsToBeSaved.get(z).setRegtimestamplong(Long.parseLong("1695709230000")); //setting earlier date
                                    MacDevicePairsToBeSaved.get(z).setRegistered(1);


                                }
                            }

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

                if (list != null && list.size() > 0) {
                    objGlobalMethods.writeToFile("list size: " + list.size(),"","","","");
                    bs = list.get(0);
                    if ((btReport.IsRunning == null || !btReport.IsRunning))  {

                        objGlobalMethods.writeToFile("RegBTLscanClass prepToGenReport btReport started." ,"","","","");
                        SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "btReportCallingClass", "RegBTLscanClass");
                        DeviceFound++;
                        list.remove(0);
                        btReport br = new btReport();
                        br.setLukeTLregistrylist(MacDevicePairsToBeSaved);
                        br.setBs(bs);
                        br.setAlist(list);
                        br.scanNew();
                    } else {
                        objGlobalMethods.writeToFile("RegBTLscanClass prepToGenReport btReport not started. " +
                                ((btReport.IsRunning != null && btReport.IsRunning) ? "(btReport.IsRunning != null && btReport.IsRunning)" : ""),"","","","");
                    }
                }


                /*if (list != null && list.size() > 0) {
                    objGlobalMethods.writeToFile("list size: " + list.size(),"","","","");
                    bs = list.get(0);
                    if ((btReport.IsRunning == null || !btReport.IsRunning) && ((btReport.CompletedScanID == null || btReport.CompletedScanID.size() == 0) || (btReport.CompletedScanID != null && btReport.CompletedScanID.size() > 0 && !btReport.CompletedScanID.contains(bs.scan.ID))))  {

                        objGlobalMethods.writeToFile("RegBTLscanClass prepToGenReport btReport started." ,"","","","");
                        SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "btReportCallingClass", "RegBTLscanClass");
                        DeviceFound++;
                        list.remove(0);
                        btReport br = new btReport();
                        br.setLukeTLregistrylist(MacDevicePairsToBeSaved);
                        br.setBs(bs);
                        br.setAlist(list);
                        br.scanNew();
                    } else {
                        objGlobalMethods.writeToFile("RegBTLscanClass prepToGenReport btReport not started. " +
                                (!(btReport.IsRunning == null || !btReport.IsRunning) ? "!(btReport.IsRunning == null || !btReport.IsRunning)" : "") +
                                (!(btReport.CompletedScanID == null || btReport.CompletedScanID.size() == 0) ? "!(btReport.CompletedScanID == null || btReport.CompletedScanID.size() == 0)" : "") +
                                (!(btReport.CompletedScanID != null && btReport.CompletedScanID.size() > 0 && !btReport.CompletedScanID.contains(bs.scan.ID)) ? "!(btReport.CompletedScanID != null && btReport.CompletedScanID.size() > 0 && !btReport.CompletedScanID.contains(bs.scan.ID))" : ""),"","","","");
                    }
                }*/
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RegBTLscanClass prepToGenReport. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void carryOnScan(ArrayList<BaseDeviceScanObj> alist) {
        try {
            if (alist != null && alist.size() > 0) {

                objGlobalMethods.writeToFile("RegBTLscanClass carryOnScan started. list size: " + alist.size() ,"","","","");

                bs = alist.get(0);
                objGlobalMethods.writeToFile("RegBTLscanClass carryOnScan btReport started." ,"","","","");
                SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                objGlobalMethods.setBTLoggerRelatedPrefs(btp, "btReportCallingClass", "RegBTLscanClass");
                DeviceFound++;
                alist.remove(0);
                btReport br = new btReport();
                br.setLukeTLregistrylist(MacDevicePairsToBeSaved);
                br.setBs(bs);
                br.setAlist(alist);
                br.scanNew();

                /*if ((btReport.CompletedScanID == null || btReport.CompletedScanID.size() == 0) || (btReport.CompletedScanID != null && btReport.CompletedScanID.size() > 0 && !btReport.CompletedScanID.contains(bs.scan.ID)))  {

                    objGlobalMethods.writeToFile("RegBTLscanClass carryOnScan btReport started." ,"","","","");
                    SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                    objGlobalMethods.setBTLoggerRelatedPrefs(btp, "btReportCallingClass", "RegBTLscanClass");
                    DeviceFound++;
                    alist.remove(0);
                    btReport br = new btReport();
                    br.setLukeTLregistrylist(MacDevicePairsToBeSaved);
                    br.setBs(bs);
                    br.setAlist(alist);
                    br.scanNew();
                } else {
                    objGlobalMethods.writeToFile("RegBTLscanClass carryOnScan btReport not started. " +
                            (!(btReport.IsRunning == null || !btReport.IsRunning) ? "!(btReport.IsRunning == null || !btReport.IsRunning)" : "") +
                            (!(btReport.CompletedScanID == null || btReport.CompletedScanID.size() == 0) ? "!(btReport.CompletedScanID == null || btReport.CompletedScanID.size() == 0)" : "") +
                            (!(btReport.CompletedScanID != null && btReport.CompletedScanID.size() > 0 && !btReport.CompletedScanID.contains(bs.scan.ID)) ? "!(btReport.CompletedScanID != null && btReport.CompletedScanID.size() > 0 && !btReport.CompletedScanID.contains(bs.scan.ID))" : ""),"","","","");
                }*/
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RegBTLscanClass carryOnScan. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    CountDownTimer cdTimer = null;

    private boolean btReportGeneratorIntentSvcRUNNING(Class<?> serviceClass) {
        boolean asd = false;
        Context ctx = myApp.getAppContext();
        try {
            ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    asd = true; break;
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RegBTLscanClass btReportGeneratorIntentSvcRUNNING. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public boolean bsExist(long MPDIDX) {
        boolean asd = false;
        BaseDeviceScanObj bs = null;
        try {
            if (list != null && list.size() > 0) {
                for (int i = 0; i <= list.size() -1; i++) {
                    bs = list.get(i);
                    if (bs.MPDIDX == MPDIDX){
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RegBTLscanClass bsExist. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public boolean tplExist(long MPDIDX) {
        boolean asd = false;
        TemperaturePayload tpl = null;
        try {
            if (tpll != null && tpll.size() > 0) {
                for (int i = 0; i <= tpll.size() -1; i++) {
                    tpl = tpll.get(i);
                    if (tpl.MPDIDX == MPDIDX){
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RegBTLscanClass tplExist. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
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

    public void removeListItem() {
        try {
            if (list != null && list.size() > 0) {
                list.remove(list.size() -1);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RegBTLscanClass removeListItem. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void scanTimeout() {
        try {
            cdTimer = new CountDownTimer(15000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    if (cdTimer != null) {
                        cdTimer.cancel();
                        cdTimer = null;
                        //Log.i(TAG, "scanTimeout.");
                    }
                    if (DeviceFound > 0) {
                        //Log.i(TAG, "scanTimeout.");
                        objGlobalMethods.writeToFile("RegBTLscanClass scanTimeout onFinish started. StopScan() started." ,"","","","");
                        StopScan();
                    } else if (DeviceFound == 0 && (DeviceList != null && DeviceList.size() > 0 && MacDevicePairs != null && MacDevicePairs.size() > 0 && DeviceList.size() < MacDevicePairs.size())) {
                        boolean found = false;
                        String m = "";
                        String dm = "";
                        objGlobalMethods.writeToFile("RegBTLscanClass scanTimeout onFinish started. Starting prepToGenReport." ,"","","","");


                        for (int i = MacDevicePairs.size() -1; i >= 0; i--) {
                            m = MacDevicePairs.get(i).Mac;
                            for (int v = 0; v <= DeviceList.size() -1; v++) {
                                dm = DeviceList.get(v).getMac();
                                if (dm.equals(m)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                MacDevicePairs.remove(i);
                            }
                        }

                        prepToGenReport();

                    } else {
                        //objGlobalMethods.writeToFile("RegBTLscanClass scanTimeout onFinish started." ,"","","","");
                    }
                }
            }.start();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at RegBTLscanClass scanTimeout. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
