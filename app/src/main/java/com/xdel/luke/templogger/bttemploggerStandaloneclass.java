package com.xdel.luke.templogger;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.xdel.luke.MPD_CheckOutActivity;
import com.xdel.luke.R;
import com.xdel.luke.mpd.MPDMainActivity;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class bttemploggerStandaloneclass {
    private Timer _Timer;
    public final String TAG = "bttemploggerStandaloneclass";
    public List<Scan> DeviceList;
    public String Key = "";
    public List<String> requestedMac;
    public List<String> requestedDeviceIDX;
    public String[] macArray;
    public String[] TemperatureLoggerIDXArray;
    public List<MacDevicePair> MacDevicePairs;
    public MacDevicePair mdp;
    public String ARegID;
    public int DeviceFound = 0;
    public long JobsIDX;
    boolean tempcheckforattachment = false;
    public String requestedBTTLMac = "";
    public double LiveTemp;
    private long BTTL_MPDIDX;
    TemperaturePayload tplsolo = null;
    private ProgressBarDialog pbTempReading;

    public void setBTTL_MPDIDX(long aBTTL_MPDIDX) {
        this.BTTL_MPDIDX = aBTTL_MPDIDX;
    }

    public double getLiveTemp() {
        return LiveTemp;
    }

    public void setRequestedBTTLMac(String requestedBTTLMac) {
        this.requestedBTTLMac = requestedBTTLMac;
    }

    public void setMacDevicePairs(List<MacDevicePair> aMacDevicePairs) {
        this.MacDevicePairs = aMacDevicePairs;
    }

    public void setTempcheckforattachment(boolean tempcheckforattachment) {
        this.tempcheckforattachment = tempcheckforattachment;
    }

    public void setJobsIDX(long jobsIDX) {
        JobsIDX = jobsIDX;
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

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at bttemploggerStandaloneclass scan. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void StopScan() {
        try {
            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            objGlobalMethods.setBTLoggerRelatedPrefs(btp, "bttemploggerStandaloneclassStarted", "");
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
            String t = t_null ? "bttemploggerStandaloneclass _Timer = null" : "";
            String b = bm_null ? "bttemploggerStandaloneclass BleManager.getInstance().cancelScan()" : "";
            String s = (!objGlobalMethods.IsEmptyString(t) ? t + ", " : "") + b;
            objGlobalMethods.writeToFile(s, "", "", "", "");

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at bttemploggerclass StopScan. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public BleScanCallback DeviceScanCallback = new BleScanCallback() {
        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            //Log.i(TAG, "onScanFinished => " + scanResultList.size());

            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            objGlobalMethods.setBTLoggerRelatedPrefs(btp, "bttemploggerStandaloneclassStarted", "");

            //Log.i(TAG, "DeviceFound => " + DeviceFound);
            try {
                if (pbTempReading != null && pbTempReading.isShowing()) {
                    pbTempReading.dismiss();
                    pbTempReading = null;
                }
                ////new code here
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobInfoActivity.class) {
                    String tplJsonStr = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "tplJsonStr");
                    if (objGlobalMethods.IsEmptyString(tplJsonStr)) {
                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "JobsIDXBTLScanned", String.valueOf(JobsIDX) + "|f");
                        objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.temp_not_recorded)).show();
                    } else {
                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "JobsIDXBTLScanned", String.valueOf(JobsIDX) + "|t");
                        //objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.temp_recorded_proceed)).show();

                        if (tplsolo != null && bs != null) {
                            Log.i(TAG, "calling generating report " + bs.scan.ID);
                            //start intentservice
                            Intent btReportGeneratorIntentSvc = new Intent(myApp.getAppContext(), btReportGeneratorIntentService.class);
                            btReportGeneratorIntentSvc.putExtra("d", new Gson().toJson(bs));
                            myApp.getAppContext().startService(btReportGeneratorIntentSvc);
                        }
                    }
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MPDMainActivity.class) {
                    final MPDMainActivity act = (MPDMainActivity) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            act.showReconditionMsg(LiveTemp);
                        }
                    });
                }
                ////new code here

                /*if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobInfoActivity.class) {
                    String tplJsonStr = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "tplJsonStr");
                    if (objGlobalMethods.IsEmptyString(tplJsonStr)) {
                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "JobsIDXBTLScanned", String.valueOf(JobsIDX) + "|f");
                        objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok),  myApp.getAppContext().getString(R.string.temp_not_recorded)).show();
                    } else {
                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "JobsIDXBTLScanned", String.valueOf(JobsIDX) + "|t");
                        objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.temp_recorded_proceed)).show();
                    }
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MPDMainActivity.class) {
                    final MPDMainActivity act = (MPDMainActivity) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            act.showReconditionMsg(LiveTemp);
                        }
                    });
                }*/
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at bttemploggerStandaloneclass onScanFinished. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }

        @Override
        public void onScanStarted(boolean success) {
            //objGlobalMethods.writeToFile("onScanStarted => " + success,"","","","");
            //Log.i(TAG, "onScanStarted => " + success);
            try {
                objGlobalMethods.setBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "bttemploggerStandaloneclassStarted", "t");
                if (pbTempReading != null) {
                    pbTempReading.dismiss();
                    pbTempReading = null;
                }
                pbTempReading = new ProgressBarDialog(myApp.currentActivity);
                pbTempReading.setCanceledOnTouchOutside(false);
                pbTempReading.setCancelable(false);
                pbTempReading.setText(myApp.getAppContext().getString(R.string.temp_read));
                pbTempReading.show();
                scanTimeout();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at bttemploggerStandaloneclass onScanStarted. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }

        @Override
        public void onScanning(BleDevice bleDevice) {
            if (bleDevice == null)
                return;
            //objGlobalMethods.writeToFile("onScanning => " + bleDevice.getMac() + " " + bleDevice.getName(),"","","","");
            //Log.i(TAG, "onScanning => " + bleDevice.getMac() + " " + bleDevice.getName());
            Scan device = new Scan();
            if (device.fromBroadcast(bleDevice) && !tempcheckforattachment)
                AddOrUpdate(device);

            if (device.fromBroadcast(bleDevice) && tempcheckforattachment)
                AddorUpdateForChecks(device);
        }
    };

    public void AddorUpdateForChecks(final Scan device) {
        TemperaturePayload tpl = null;

        try {
            if (device == null || device.getDeviceType() == DeviceType.Unknown)
                return;

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
            DeviceList.add(device);

            Scan d = null;
            boolean macremoved = false;

            outer:
            for (int i = DeviceList.size() - 1; i >= 0; i--) {
                d = DeviceList.get(i);
                if (objGlobalMethods.IsEqualString(d.getMac(), requestedBTTLMac)) {
                    objGlobalMethods.writeToFile(
                            "bttemploggerStandaloneclass AddorUpdateForChecks device detected => \n" + d.Mac + " " + d.getName() + " "
                                    + d.ID
                                    + "\nTemp: " + d.getTemperature() + d.getTemperatureUnitTypeID()
                                    + "\nBattery: " + d.getBattery()
                                    + "\nLastActive: " + d.getLastActiveTime()
                                    + "\nDeviceTime: " + d.getDeviceTime()
                                    + "\nFor jobs attachment.", "", "", "", "");

                    tpl = new TemperaturePayload();
                    tpl.setBatteryLevel((int) (d.getVoltage() * 100));
                    tpl.setHumidity(d.getHumidity());
                    tpl.setTemperature(d.getTemperature());
                    LiveTemp = d.getTemperature();
                    tpl.setLat(0);
                    tpl.setLong(0);
                    tpl.setTimestamp(String.valueOf(d.DeviceTime));
                    tpl.setMPDIDX(BTTL_MPDIDX);

                    _D = new BaseDevice();
                    _D.ID = d.getID();
                    _D.Name = d.getName();
                    _D.Mac = d.getMac();
                    _D.HardwareType = d.getHardwareType();
                    _D.Version = d.getVersion();

                    bs = new BaseDeviceScanObj();
                    bs.setScan(d);
                    bs.set_D(_D);
                    bs.setMPDIDX(BTTL_MPDIDX);

                    macremoved = true;
                    DeviceList.clear();
                    break;
                }
            }

            if (macremoved)
                StopScan();

            if (tpl != null) {
                DeviceFound++;
                Gson gson = new Gson();
                String tplJsonStr = gson.toJson(tpl);
                objGlobalMethods.setBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "tplJsonStr", tplJsonStr);

                if (bs != null) {
                    //start intentservice
                    Intent btReportGeneratorIntentSvc = new Intent(myApp.getAppContext(), btReportGeneratorIntentService.class);
                    btReportGeneratorIntentSvc.putExtra("d", new Gson().toJson(bs));
                    myApp.getAppContext().startService(btReportGeneratorIntentSvc);
                }
            }
        } catch (Exception ex) {
            String s = JobsHandler.getStackTraceString(ex);
            objGlobalMethods.writeToFile("Exception at bttemploggerStandaloneclass AddorUpdateForChecks. Exception: " + ex.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void AddOrUpdate(final Scan device) {
        TemperaturePayload tpl = null;

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
            DeviceList.add(device);

            Scan d = null;
            String m = "";
            int Device_Type;
            boolean macremoved = false;

            outer:
            for (int i = DeviceList.size() - 1; i >= 0; i--) {
                d = DeviceList.get(i);
                for (int x = MacDevicePairs.size() - 1; x >= 0; x--) {
                    mdp = MacDevicePairs.get(x);
                    m = mdp.Mac;
                    Device_Type = mdp.Device_Type;
                    macremoved = false;
                    //m = "asdassdasd";

                    if (Device_Type == 22 && !objGlobalMethods.IsEmptyString(m) && !objGlobalMethods.IsEmptyString(d.Mac) && d.Mac.equals(m) && temploggerclass.jobsidxExist(mdp.Jobs_Attached, JobsIDX)) {

                        objGlobalMethods.writeToFile(
                                "bttemploggerStandaloneclass AddOrUpdate device detected => \n" + d.Mac + " " + d.getName() + " "
                                        + d.ID
                                        + "\nTemp: " + d.getTemperature() + d.getTemperatureUnitTypeID()
                                        + "\nBattery: " + d.getBattery()
                                        + "\nLastActive: " + d.getLastActiveTime()
                                        + "\nDeviceTime: " + d.getDeviceTime()
                                        + "\nJX" + JobsIDX, "", "", "", "");

                        /*tpl = new TemperaturePayload();
                        tpl.setBatteryLevel((int) (d.getVoltage() * 100));
                        tpl.setHumidity(d.getHumidity());
                        tpl.setTemperature(d.getTemperature());
                        tpl.setLat(0);
                        tpl.setLong(0);
                        tpl.setTimestamp(String.valueOf(d.DeviceTime));
                        tpl.setMPDIDX(mdp.MPDIDX);*/

                        ////new code here
                        tplsolo = new TemperaturePayload();
                        tplsolo.setBatteryLevel((int) (d.getVoltage() * 100));
                        tplsolo.setHumidity(d.getHumidity());
                        tplsolo.setTemperature(d.getTemperature());
                        tplsolo.setLat(0);
                        tplsolo.setLong(0);
                        tplsolo.setTimestamp(String.valueOf(d.DeviceTime));
                        tplsolo.setMPDIDX(mdp.MPDIDX);
                        ////new code here

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

                        macremoved = true;
                        MacDevicePairs.clear();
                        DeviceList.clear();
                        break outer;
                    }
                }
            }

            if (macremoved)
                StopScan();

            /*if (tpl != null) {
                DeviceFound++;
                Gson gson = new Gson();
                String tplJsonStr = gson.toJson(tpl);
                objGlobalMethods.setBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "tplJsonStr", tplJsonStr);

                if (bs != null) {
                    Log.i(TAG, "calling generating report " + bs.scan.ID);
                    //start intentservice
                    Intent btReportGeneratorIntentSvc = new Intent(myApp.getAppContext(), btReportGeneratorIntentService.class);
                    btReportGeneratorIntentSvc.putExtra("d", new Gson().toJson(bs));
                    myApp.getAppContext().startService(btReportGeneratorIntentSvc);
                }
            }*/

            ////new code here
            if (tplsolo != null) {
                DeviceFound++;
                Gson gson = new Gson();
                String tplJsonStr = gson.toJson(tplsolo);
                objGlobalMethods.setBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "tplJsonStr", tplJsonStr);
            }
            ////new code here
        } catch (Exception ex) {
            String s = JobsHandler.getStackTraceString(ex);
            objGlobalMethods.writeToFile("Exception at bttemploggerStandaloneclass AddOrUpdate. Exception: " + ex.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    //start of DeviceActivity class
    public ArrayList<BaseDeviceScanObj> list = null;
    public BaseDeviceScanObj bs = null;
    public ArrayList<BaseDevice> _Dlist = null;
    public BaseDevice _D = null;
    CountDownTimer cdTimer;

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
                    if (DeviceFound == 0)
                        StopScan();
                }
            }.start();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at bttemploggerStandaloneclass scanTimeout. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

}
