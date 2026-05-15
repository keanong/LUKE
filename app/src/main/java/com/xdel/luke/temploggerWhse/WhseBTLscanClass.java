package com.xdel.luke.temploggerWhse;

import android.content.SharedPreferences;
import android.util.Log;

import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.data.BleScanState;
import com.tzone.devices.BaseDevice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.BaseDeviceScanObj;
import com.xdel.luke.templogger.TemperaturePayload;
import com.xdel.luke.templogger.Scan;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class WhseBTLscanClass {
    public final String TAG = "WhseBTLscanClass";
    private Timer _Timer;
    public List<Scan> DeviceList;
    public ArrayList<TemperaturePayload> tpll = null;
    public ArrayList<String> tpllmac = null;
    public BaseDeviceScanObj bs = null;
    public BaseDevice _D = null;
    public ArrayList<BaseDeviceScanObj> list = null;
    public int DeviceFound = 0;

    public void setDeviceList(List<Scan> aDeviceList){
        this.DeviceList = aDeviceList;
    }

    public void scan() {
        try {
            if (DeviceList != null && DeviceList.size() > 0) {
                Scan d = null;
                for (int i = DeviceList.size() -1; i >= 0; i--) {
                    d = DeviceList.get(i);
                    //Log.e(TAG, "device detected => " + d.Mac + " " + d.getName());
                    objGlobalMethods.writeToFile("device detected => " + d.Mac + " [" + d.getID() + "] " + d.getName(),"","","","");
                    if (!tpllmacExist(d.Mac)) {
                        _D = new BaseDevice();
                        _D.ID = d.getID();
                        _D.Name = d.getName();
                        _D.Mac = d.getMac();
                        _D.HardwareType = d.getHardwareType();
                        _D.Version = d.getVersion();

                        bs = new BaseDeviceScanObj();
                        bs.setScan(d);
                        bs.set_D(_D);

                        if (list == null)
                            list = new ArrayList<>();
                        list.add(bs);
                    }
                }
            }

            if (list != null && list.size() > 0) {
                bs = list.get(0);
                if (WhseBtReport.IsRunning == null || !WhseBtReport.IsRunning) {
                    DeviceFound++;
                    String ss = "Alist size " + list.size() + " " + "WhseBTLscanClass scan() br.scanNew() > " + bs._D.getID();
                    list.remove(0);
                    WhseBtReport br = new WhseBtReport();
                    br.setBs(bs);
                    br.setAlist(list);
                    objGlobalMethods.writeToFile(ss,"","","","");
                    br.scanNew();
                }
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerWhseScanActivity.class) {
                //Log.e(TAG, "WhseBTLscanClass scan() act.startMainTimer()");
                final TempLoggerWhseScanActivity act = (TempLoggerWhseScanActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        // Notifiy activity
                        objGlobalMethods.writeToFile("WhseBTLscanClass scan() act.runOnUiThread act.StartMainTimer()","","","","");
                        act.StartMainTimer();
                    }
                });
            } else {
                objGlobalMethods.writeToFile("WhseBTLscanClass scan() act.StartMainTimer()","","","","");
                final TempLoggerWhseScanActivity act = new TempLoggerWhseScanActivity();
                act.StartMainTimer();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at WhseBTLscanClass scan. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


    public boolean tpllmacExist(String mac) {
        boolean asd = false;
        TemperaturePayload tpl = null;
        try {
            if (tpllmac != null && tpllmac.size() > 0) {
                for (int i = 0; i <= tpllmac.size() -1; i++) {
                    asd = objGlobalMethods.IsEqualString(tpllmac.get(i), mac);
                    if (asd)
                        break;
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at WhseBTLscanClass tpllmacExist. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public void triggerTempLoggerWhseScanActivityMainTimer(String func) {
        try {
            //Log.e(TAG, func + " - triggerTempLoggerWhseScanActivityMainTimer");

            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerWhseScanActivity.class) {
                final TempLoggerWhseScanActivity act = (TempLoggerWhseScanActivity) myApp.currentActivity;
                act.StartMainTimer();

            } else {
                final TempLoggerWhseScanActivity act = new TempLoggerWhseScanActivity();
                act.StartMainTimer();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exeption at WhseBtReport triggerTempLoggerWhseScanActivityMainTimer. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void StopScan(boolean trigger) {
        try {
            objGlobalMethods.setBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "RegBTLscanClassStarted", "");

            if (_Timer != null) {
                _Timer.cancel();
                _Timer = null;
            }

            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
                BleManager.getInstance().cancelScan();
            }
            WhseBtReport.rbtStopOrCarryOnScanBool = null;
            if (trigger)
                triggerTempLoggerWhseScanActivityMainTimer("StopScan()");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at WhseBTLscanClass StopScan. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void carryOnScan(ArrayList<BaseDeviceScanObj> alist) {
        try {
            WhseBtReport.rbtStopOrCarryOnScanBool = null;
            if (alist != null && alist.size() > 0) {
                //Log.e(TAG, "WhseBTLscanClass carryOnScan started. list size: " + alist.size());

                bs = alist.get(0);
                //Log.e(TAG, "WhseBTLscanClass carryOnScan btReport started.");
                SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                objGlobalMethods.setBTLoggerRelatedPrefs(btp, "btReportCallingClass", "RegBTLscanClass");
                DeviceFound++;
                String ss = "Alist size " + alist.size() + " " + "WhseBTLscanClass carryOnScan() > " + bs._D.getID();
                alist.remove(0);
                WhseBtReport br = new WhseBtReport();
                br.setBs(bs);
                objGlobalMethods.writeToFile(ss,"","","","");
                br.setAlist(alist);
                br.scanNew();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at WhseBTLscanClass carryOnScan. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
