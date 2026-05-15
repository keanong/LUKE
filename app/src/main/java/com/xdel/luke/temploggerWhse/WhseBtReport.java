package com.xdel.luke.temploggerWhse;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.callback.BleGattCallback;
import com.tzone.bluetooth.data.BleDevice;
import com.tzone.bluetooth.exception.BleException;
import com.tzone.devices.AlarmSetting;
import com.tzone.devices.BaseDevice;
import com.tzone.devices.DataManagerBase;
import com.tzone.devices.DeviceRecordType;
import com.tzone.devices.DeviceType;
import com.tzone.devices.IDataCallback;
import com.tzone.devices.LoggingData;
import com.tzone.devices.TemperatureUnitType;
import com.tzone.utils.StringUtil;
import com.xdel.luke.BackendRun2_BroadcastReceiver;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.JobInfo;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.BaseDeviceScanObj;
import com.xdel.luke.templogger.Scan;
import com.xdel.luke.templogger.TemperaturePayload;
import com.xdel.luke.templogger.UploadTempDataObj;
import com.xdel.luke.templogger.bttemploggerclass;
import com.xdel.luke.test.Report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WhseBtReport {

    public final String TAG = "WhseBtReport";
    public ArrayList<BaseDevice> _Dlist = null;
    public BaseDevice _D = null;
    public BaseDevice _Device = null;
    public BleDevice _BleDevice = null;
    public DataManagerBase _DataManager = null;
    public Report _Report = null;
    public String Password = "778899"; //Set Password
    private String[] RequestDataInfo;
    public List<AlarmSetting> AlarmList = null;
    public List<Integer> MarkList = null;
    public DeviceRecordType InitialStatus = DeviceRecordType.Unknown;
    public ArrayList<BaseDeviceScanObj> alist;
    public BaseDeviceScanObj bs = null;
    public Scan scan = null;
    public String MAC;
    public static Boolean IsRunning = null;
    public static Boolean rbtStopOrCarryOnScanBool = null;
    public static Boolean GenerateReportStartedBool = null;
    public String ScanID;
    public String dJson;
    public Integer WhseBtReport_onConnectFailCount = null;
    public static String ConnectSuccessMac = null;
    public static Boolean pauserbool = null;


    public void setBs(BaseDeviceScanObj bs) {
        this.bs = bs;
    }

    public void setAlist(ArrayList<BaseDeviceScanObj> vlist) {
        this.alist = vlist;
    }

    public void scanNew() {
        try {
            _Device = bs != null && bs._D != null ? bs._D : null;
            scan = bs != null && bs.scan != null ? bs.scan : null;
            MAC = bs != null ? bs.MAC : "";
            ScanID = bs != null && bs.scan != null ? bs.scan.ID : "";
            pauserbool = null;

            if (_Device == null) {
                objGlobalMethods.writeToFile("WhseBtReport scanNew return due (_Device == null)","","","","");
                //Log.e(TAG, "WhseBtReport scanNew return due (_Device == null)");
                return;
            }
            if (scan == null) {
                objGlobalMethods.writeToFile("WhseBtReport scanNew return due (scan == null)","","","","");
                //Log.e(TAG, "WhseBtReport scanNew return due (scan == null)");
                return;
            }

            /*if (objGlobalMethods.isScreenDisplayOn(myApp.getAppContext())) {
                IsRunning = true;
                GenereateLiveReportFromScanObjOnly();
            } else {
                pauserbool = null;
                IsRunning = null;
                GenerateReportStartedBool = null;
                rbtStopOrCarryOnScanBool = true;
                objGlobalMethods.writeToFile("WhseBtReport scanNew not started due !isScreenDisplayOn for " +  ScanID,"","","","");
                rbtStopOrCarryOnScan("WhseBtReport scanNew not started");
            }*/

            IsRunning = true;
            GenereateLiveReportFromScanObjOnly();






            /*if (objGlobalMethods.isScreenDisplayOn(myApp.getAppContext())) {
                IsRunning = true;

                BleManager.getInstance().connect(_Device.getMac(), bleGattCallback);
                //Log.e(TAG, "WhseBtReport BleManager.getInstance().scan(DeviceScanCallback) for " + ScanID);
            } else {
                IsRunning = null;
                objGlobalMethods.writeToFile("WhseBtReport scanNew not started due !isScreenDisplayOn for " +  ScanID,"","","","");
                //Log.e(TAG, "WhseBtReport scanNew not started due !isScreenDisplayOn for " +  ScanID);
                if (_BleDevice != null)
                    BleManager.getInstance().disconnect(_BleDevice);

            }*/
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at WhseBtReport scanNew. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");

            pauserbool = null;
            IsRunning = null;
            GenerateReportStartedBool = null;
            rbtStopOrCarryOnScanBool = true;
            rbtStopOrCarryOnScan("Exception at WhseBtReport scanNew");
        }
    }

    public void InitDataManager() {
        if (_BleDevice == null) {
            objGlobalMethods.writeToFile("WhseBtReport InitDataManager return due (_BleDevice == null)","","","","");
            //Log.e(TAG, "WhseBtReport InitDataManager return due (_BleDevice == null)");
            return;
        }

        try {

            if (objGlobalMethods.isScreenDisplayOn(myApp.getAppContext())) {
                if (_DataManager == null) {
                    if (_Device.getDeviceType() == DeviceType.TempU06L60)
                        _DataManager = new com.tzone.devices.u06L60.DataManager();
                    else if (_Device.getDeviceType() == DeviceType.TempU06L100)
                        _DataManager = new com.tzone.devices.u06L100.DataManager();
                    else if (_Device.getDeviceType() == DeviceType.TempU06L200)
                        _DataManager = new com.tzone.devices.u06L200.DataManager();
                    else if (_Device.getDeviceType() == DeviceType.BT04)
                        _DataManager = new com.tzone.devices.bt04.DataManager();
                    else if (_Device.getDeviceType() == DeviceType.BT04B)
                        _DataManager = new com.tzone.devices.bt04b.DataManager();
                    else if (_Device.getDeviceType() == DeviceType.BT05)
                        _DataManager = new com.tzone.devices.bt05.DataManager();
                    else if (_Device.getDeviceType() == DeviceType.BT05B)
                        _DataManager = new com.tzone.devices.bt05b.DataManager();
                    else if (_Device.getDeviceType() == DeviceType.BT06)
                        _DataManager = new com.tzone.devices.bt06.DataManager();
                    else {
                        //Log.e(TAG, "WhseBtReport InitDataManager return due (_BleDevice == null)");
                        return;
                    }
                    _DataManager.Initialize(_BleDevice, dataCallback);
                }

                _Report = null;

                if (_Device.getDeviceType() == DeviceType.TempU06L60
                        || _Device.getDeviceType() == DeviceType.TempU06L80
                        || _Device.getDeviceType() == DeviceType.TempU06L100
                        || _Device.getDeviceType() == DeviceType.TempU06L200
                        || _Device.getDeviceType() == DeviceType.BT06) {
                    _DataManager.Notify();
                } else {
                    Unlock();
                }
            } else {
                objGlobalMethods.writeToFile("WhseBtReport InitDataManager not running due !isScreenDisplayOn.","","","","");
                //Log.e(TAG, "WhseBtReport InitDataManager not running due !isScreenDisplayOn.");
                if (_BleDevice != null)
                    BleManager.getInstance().disconnect(_BleDevice);
            }
        } catch (Exception ex) {
            String s = JobsHandler.getStackTraceString(ex);
            objGlobalMethods.writeToFile("Exception at WhseBtReport InitDataManager. Exception: " + ex.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void Unlock() {
        if (_DataManager == null) {
            //Log.e(TAG, "WhseBtReport Unlock return due (_DataManager == null)");
            return;
        }

        try {
            if (objGlobalMethods.isScreenDisplayOn(myApp.getAppContext())) {
                _DataManager.Unlock(Password);
            } else {
                //Log.e(TAG, "WhseBtReport Unlock not running due !isScreenDisplayOn.");
                if (_BleDevice != null)
                    BleManager.getInstance().disconnect(_BleDevice);
            }
        } catch (Exception ex) {
            String s = JobsHandler.getStackTraceString(ex);
            objGlobalMethods.writeToFile("Exception at WhseBtReport Unlock. Exception: " + ex.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public BleGattCallback bleGattCallback = new BleGattCallback() {
        @Override
        public void onStartConnect() {
            ConnectSuccessMac = null;
            objGlobalMethods.writeToFile("WhseBtReport onStartConnect","","","","");
            //Log.e(TAG, "WhseBtReport onStartConnect");
        }

        @Override
        public void onConnectFail(BleDevice bleDevice, BleException exception) {
            String mac = bleDevice != null ? " " + bleDevice.getMac() : "";
            objGlobalMethods.writeToFile("WhseBtReport onConnectFail" + mac,"","","","");
            //Log.e(TAG, "WhseBtReport onConnectFail");
            //BleManager.getInstance().destroy();
            _BleDevice = null;
            IsRunning = null;

            try {
                /*if (TempLoggerWhseScanActivity.RestarterTimerFinishCalled != null && TempLoggerWhseScanActivity.RestarterTimerFinishCalled) {
                    objGlobalMethods.writeToFile("WhseBtReport onConnectFail rbt.StopScan(false).","","","","");
                    //Log.e(TAG, "WhseBtReport GenerateReport StopScan() started (RestarterTimerFinishCalled - true).");
                    WhseBTLscanClass rbt = new WhseBTLscanClass();
                    rbt.StopScan(false);
                } else if (alist != null && alist.size() > 0) {
                    objGlobalMethods.writeToFile("WhseBtReport onConnectFail rbt.carryOnScan(alist).","","","","");
                    WhseBTLscanClass rbt = new WhseBTLscanClass();
                    rbt.carryOnScan(alist);
                }*/

                //rbtStopOrCarryOnScan("WhseBtReport onConnectFail");
                if (rbtStopOrCarryOnScanBool != null && rbtStopOrCarryOnScanBool) {
                    //do nothing
                } else {
                    rbtStopOrCarryOnScan("WhseBtReport onConnectFail");
                }

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at WhseBtReport bleGattCallback onConnectFail. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            }

        }

        @Override
        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
            String mac = bleDevice != null ? " " + bleDevice.getMac() : "";
            ConnectSuccessMac = mac;
            objGlobalMethods.writeToFile("WhseBtReport onConnectSuccess" + mac,"","","","");
            //Log.e(TAG, "WhseBtReport onConnectSuccess");
            _BleDevice = bleDevice;
            InitDataManager();
        }

        @Override
        public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
            try {
                String mac = device != null ? " " + device.getMac() : "";
                objGlobalMethods.writeToFile("WhseBtReport onDisConnected" + mac,"","","","");
                //Log.e(TAG, "WhseBtReport onDisConnected");
                _BleDevice = null;
                //IsRunning = null;

                boolean isConnectedSuccessMac = (!objGlobalMethods.IsEmptyString(ConnectSuccessMac)) && objGlobalMethods.IsEqualString(ConnectSuccessMac, mac);

                ////comment this away 1st
            /*if (IsRunning != null && IsRunning && GenerateReportStartedBool == null) {
                GenerateReportStartedBool = null;
                IsRunning = null;
                rbtStopOrCarryOnScan("WhseBtReport onDisConnected (IsRunning != null && IsRunning && GenerateReportStartedBool == null)");
            } else*/
                if (IsRunning != null && IsRunning) {
                    //do nothing
                    IsRunning = null;
                    if (isConnectedSuccessMac && rbtStopOrCarryOnScanBool == null && pauserbool == null)
                        rbtStopOrCarryOnScan("WhseBtReport onDisConnected isConnectedSuccessMac");
                } else if (rbtStopOrCarryOnScanBool != null && rbtStopOrCarryOnScanBool) {
                    //do nothing
                    IsRunning = null;
                } else {
                    IsRunning = null;
                    rbtStopOrCarryOnScan("WhseBtReport onDisConnected");
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at WhseBtReport BleGattCallback onDisConnected. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            }
        }
    };

    public IDataCallback dataCallback = new IDataCallback() {
        @Override
        public void onNotify(boolean status) {
            String mac = _DataManager != null && _DataManager._BleDevice != null ? " " + _DataManager._BleDevice.getMac() : "";
            objGlobalMethods.writeToFile("IDataCallback onNotify" + mac + " status" + (status ? " true" : " false"),"","","","");
            if (status) {
                Unlock();
            } else {
                IsRunning = null;
                rbtStopOrCarryOnScan("WhseBtReport IDataCallback onNotify status false");
            }
        }

        @Override
        public void onUnlock(boolean status, boolean isOk) {
            String mac = _DataManager != null && _DataManager._BleDevice != null ? " " + _DataManager._BleDevice.getMac() : "";
            if (status) {
                if (isOk) {
                    objGlobalMethods.writeToFile("IDataCallback onUnlock" + mac + " status true isOK","","","","");
                    _DataManager.GetLogStatus();
                } else {
                    objGlobalMethods.writeToFile("IDataCallback onUnlock" + mac + " status true !isOK","","","","");
                    //Password is error
                    Password = "";
                    IsRunning = null;
                    rbtStopOrCarryOnScan("WhseBtReport IDataCallback onUnlock status true !isOK");
                }
            } else {
                objGlobalMethods.writeToFile("IDataCallback onUnlock" + mac + " status false","","","","");
                IsRunning = null;
                rbtStopOrCarryOnScan("WhseBtReport IDataCallback onUnlock status false");
            }
        }

        @Override
        public void onGetLogStatus(boolean status, DeviceRecordType recordStatus) {
            if(status){

                //InitialStatus = recordStatus;
                if (recordStatus != DeviceRecordType.Recording){
                    //_DataManager.SetConfig(0, 0);
                    _DataManager.SetStart();
                } else {
                    _DataManager.SetConfig(0, 0);
                }
                _DataManager.SetConfig(0, 0);
            }
        }

        @Override
        public void onSetConfig(boolean status) {
            if (status) {
                _DataManager.RequestDataInfo();
            }
        }

        @Override
        public void onGetAlarm(boolean status, List<AlarmSetting> tempAlarmList, List<AlarmSetting> rhAlarmList) {
            if(status){
                _DataManager.GetMark();
            }
        }

        @Override
        public void onGetMark(boolean status, List<Integer> markList) {
            if(status){
                _DataManager.RequestDataInfo();
            }
        }

        @Override
        public void onRequestDataInfo(boolean status, String[] info) {
            if (status) {
                int dataCount = 0;
                if (info != null) {
                    dataCount = Integer.parseInt(info[0]);
                    RequestDataInfo = info;
                }

                if (dataCount > 0) {
                    _DataManager.Receive(true);
                }
            }
        }

        @Override
        public void onReceive(boolean status, boolean isOpen) {
            if (isOpen) {
                if (status) {

                }
            }
        }

        @Override
        public void onProgress(int progress) {
            //Log.i(TAG, "onProgress: progress => " + progress);
        }

        @Override
        public void onData(final List<LoggingData> data) {
            _DataManager.Receive(false);
            //GenerateReport(data);
            GenerateLiveReport();
        }

        @Override
        public void onPassword(boolean status) {
            String mac = _DataManager != null && _DataManager._BleDevice != null ? " " + _DataManager._BleDevice.getMac() : "";
            objGlobalMethods.writeToFile("IDataCallback onPassword status " + (status ? "true" : false) + mac,"","","","");
        }

        @Override
        public void onFligthMode(boolean status) {

        }

        @Override
        public void onStart(boolean status) {

        }

        @Override
        public void onStop(boolean status) {

        }

        @Override
        public void onMark(boolean status) {

        }

        @Override
        public void onClearFlash(boolean b) {

        }
    };

    public void GenereateLiveReportFromScanObjOnly() {
        try {
            GenerateReportStartedBool = true;
            if (scan == null) {
                objGlobalMethods.writeToFile("WhseBtReport GenereateLiveReportFromScanObjOnly scan == null","","","","");
                /*if (_BleDevice != null)
                    BleManager.getInstance().disconnect(_BleDevice);
                IsRunning = false;*/

                pauserbool = null;
                IsRunning = null;
                GenerateReportStartedBool = null;
                rbtStopOrCarryOnScanBool = true;
                rbtStopOrCarryOnScan("scan == null");
                return;
            }

            objGlobalMethods.writeToFile("LoggingData for " + scan.getID(),"","","","");
            uploadLiveTemp();

            pauserbool = null;
            new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //do nothing
                    pauserbool = true;
                }

                @Override
                public void onFinish() {
                    objGlobalMethods.writeToFile("WhseBtReport GenereateLiveReportFromScanObjOnly 3s pause timer onFinish.","","","","");
                    pauserbool = null;
                    IsRunning = false;
                    GenerateReportStartedBool = null;
                    rbtStopOrCarryOnScanBool = true;
                    rbtStopOrCarryOnScan("Completed");
                }
            }.start();
            objGlobalMethods.writeToFile("WhseBtReport GenereateLiveReportFromScanObjOnly 3s pause timer started.","","","","");

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at WhseBtReport GenereateLiveReportFromScanObjOnly. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");

            pauserbool = null;
            IsRunning = null;
            GenerateReportStartedBool = null;
            rbtStopOrCarryOnScanBool = true;
            rbtStopOrCarryOnScan("Exception at WhseBtReport GenereateLiveReportFromScanObjOnly");
        }
    }

    public void GenerateLiveReport() {
        try {
            GenerateReportStartedBool = true;
            if (scan == null) {
                objGlobalMethods.writeToFile("WhseBtReport GenerateLiveReport scan == null","","","","");
                if (_BleDevice != null)
                    BleManager.getInstance().disconnect(_BleDevice);
                IsRunning = false;

                rbtStopOrCarryOnScan("scan == null");
                GenerateReportStartedBool = null;
                return;
            }
            if (objGlobalMethods.isScreenDisplayOn(myApp.getAppContext())) {

                objGlobalMethods.writeToFile("LoggingData for " + scan.getID(),"","","","");
                uploadLiveTemp();
                _DataManager.SetStop(); //this will stop the temp logger from recording and clear all data in the temp logger
                _DataManager.SetStart(); //this will set the temp logger to start recording

                if (_BleDevice != null)
                    BleManager.getInstance().disconnect(_BleDevice);

                pauserbool = null;
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //do nothing
                        pauserbool = true;
                    }

                    @Override
                    public void onFinish() {
                        objGlobalMethods.writeToFile("WhseBtReport GenerateReport 3s pause timer onFinish.","","","","");
                        pauserbool = null;
                        IsRunning = false;
                        GenerateReportStartedBool = null;
                        rbtStopOrCarryOnScanBool = true;
                        rbtStopOrCarryOnScan("Completed");
                    }
                }.start();
                objGlobalMethods.writeToFile("WhseBtReport GenerateReport 3s pause timer started.","","","","");
            } else {
                objGlobalMethods.writeToFile("WhseBtReport GenerateLiveReport BleManager.getInstance().disconnect(_BleDevice) due !isScreenDisplayOn.","","","","");
                //Log.e(TAG, "WhseBtReport GenerateReport not running due !isScreenDisplayOn.");
                IsRunning = null;
                GenerateReportStartedBool = null;
                if (_BleDevice != null)
                    BleManager.getInstance().disconnect(_BleDevice);
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at WhseBtReport GenerateLiveReport. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void GenerateReport(final List<LoggingData> data) {
        try {
            GenerateReportStartedBool = true;
            if (data == null || data.size() == 0) {
                objGlobalMethods.writeToFile("WhseBtReport GenerateReport data " + data == null ? "null" : "0.","","","","");
                if (_BleDevice != null)
                    BleManager.getInstance().disconnect(_BleDevice);
                IsRunning = false;

                rbtStopOrCarryOnScan("(data == null || data.size() == 0)");
                GenerateReportStartedBool = null;
                return;
            }



            if (objGlobalMethods.isScreenDisplayOn(myApp.getAppContext())) {
                //Log.e(TAG, "WhseBtReport LoggingData for " + _Device.getID() + ", " + data.size() + " records.");
                objGlobalMethods.writeToFile("LoggingData for " + _Device.getID() + ", " + data.size() + " records.","","","","");

                int dataCount = 0;
                long beginTime = 0;
                long timeSpan = 0;
                DeviceRecordType recordStatus = DeviceRecordType.Initialize;
                long delayTime = 0;
                TemperatureUnitType temperatureUnit = TemperatureUnitType.C;
                int startMode = 0;
                String description = null;
                double mkt = -1000;

                if (RequestDataInfo != null) {
                    try {
                        dataCount = Integer.parseInt(RequestDataInfo[0]);
                        beginTime = Long.parseLong(RequestDataInfo[1]);
                        timeSpan = Long.parseLong(RequestDataInfo[2]);
                        recordStatus = DeviceRecordType.values()[Integer.parseInt(RequestDataInfo[3])];
                        delayTime = Long.parseLong(RequestDataInfo[4]);
                        temperatureUnit = RequestDataInfo[6].equals(TemperatureUnitType.F.toString()) ? TemperatureUnitType.F : TemperatureUnitType.C;
                        startMode = Integer.parseInt(RequestDataInfo[8]);
                        description = RequestDataInfo[10];
                        if(!RequestDataInfo[12].equals(""))
                            mkt = Double.parseDouble(RequestDataInfo[12]);
                    } catch (Exception ex) {}

                    if (beginTime == 0)
                        beginTime = data.get(0).getCreateTime();
                }
                if (dataCount != data.size()) {
                    objGlobalMethods.writeToFile("WhseBtReport GenerateReport dataCount != data.size()","","","","");
                    if (_BleDevice != null)
                        BleManager.getInstance().disconnect(_BleDevice);
                    IsRunning = false;

                    rbtStopOrCarryOnScan("(dataCount != data.size())");
                    GenerateReportStartedBool = null;

                    return;
                }

                String serialID = StringUtil.PadLeft("1", 6);
                _Report = new Report();
                _Report.DeviceID = _Device.getID();
                _Report.SerialID = serialID;
                _Report.DeviceTypeID = _Device.getDeviceType();
                _Report.DeviceName = _Device.getName();
                _Report.Version = _Device.getVersion();
                _Report.Mac = _Device.getMac();

                _Report.BeginTime = beginTime;
                _Report.TemperatureUnitTypeID = temperatureUnit;
                _Report.LoggingInterval = timeSpan;
                _Report.StartDelay = delayTime;
                _Report.StartMode = startMode;
                _Report.RecordStatus = recordStatus;
                _Report.MKT = mkt;
                _Report.Description = description;

                _Report.AlarmList = AlarmList;
                for (int i = 0; i < data.size(); i++) {
                    LoggingData loggingData = data.get(i);
                    _Report.DataList.add(loggingData);
                }
                if (MarkList != null && MarkList.size() > 0) {
                    for (int j = 0; j < MarkList.size(); j++) {
                        int did = MarkList.get(j);
                        if (_Report.DataList != null && did < _Report.DataList.size())
                            _Report.DataList.get(did).setDataTypeID(LoggingData.DataType.Mark);
                    }
                }
                _Report.CreateTime = new Date().getTime();
                _Report.Generate();

                List<LoggingData> ld = getDataList(_Report);
                if (ld != null && ld.size() > 0 && ld.size() <= 36) { //3hrs worth
                    //Log.e(TAG, "WhseBtReport Uploading temp.\n" + new Gson().toJson(ld));
                }

                uploadTemp(_Report);
                _DataManager.SetStop(); //this will stop the temp logger from recording and clear all data in the temp logger
                _DataManager.SetStart(); //this will set the temp logger to start recording

                if (_BleDevice != null)
                    BleManager.getInstance().disconnect(_BleDevice);

                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //do nothing
                    }

                    @Override
                    public void onFinish() {
                        objGlobalMethods.writeToFile("WhseBtReport GenerateReport 3s pause timer onFinish.","","","","");
                        IsRunning = false;
                        GenerateReportStartedBool = null;
                        rbtStopOrCarryOnScanBool = true;
                        rbtStopOrCarryOnScan("Completed");
                    }
                }.start();
                objGlobalMethods.writeToFile("WhseBtReport GenerateReport 3s pause timer started.","","","","");


                /*IsRunning = false;

                rbtStopOrCarryOnScanBool = true;
                rbtStopOrCarryOnScan("Completed");*/

                /*if (TempLoggerWhseScanActivity.RestarterTimerFinishCalled != null && TempLoggerWhseScanActivity.RestarterTimerFinishCalled) {
                    objGlobalMethods.writeToFile("WhseBtReport GenerateReport rbt.StopScan(false).","","","","");
                    //Log.e(TAG, "WhseBtReport GenerateReport StopScan() started (RestarterTimerFinishCalled - true).");
                    WhseBTLscanClass rbt = new WhseBTLscanClass();
                    rbt.StopScan(false);
                } else if (alist != null && alist.size() > 0) {
                    objGlobalMethods.writeToFile("WhseBtReport GenerateReport rbt.carryOnScan(alist).","","","","");
                    WhseBTLscanClass rbt = new WhseBTLscanClass();
                    rbt.carryOnScan(alist);
                } else {
                    objGlobalMethods.writeToFile("WhseBtReport GenerateReport rbt.StopScan(true).","","","","");
                    //Log.e(TAG, "WhseBtReport GenerateReport StopScan() started.");
                    WhseBTLscanClass rbt = new WhseBTLscanClass();
                    rbt.StopScan(true);
                }*/
            } else {
                objGlobalMethods.writeToFile("WhseBtReport GenerateReport BleManager.getInstance().disconnect(_BleDevice) due !isScreenDisplayOn.","","","","");
                //Log.e(TAG, "WhseBtReport GenerateReport not running due !isScreenDisplayOn.");
                IsRunning = null;
                GenerateReportStartedBool = null;
                if (_BleDevice != null)
                    BleManager.getInstance().disconnect(_BleDevice);
            }

        } catch (Exception ex) {
            String s = JobsHandler.getStackTraceString(ex);
            objGlobalMethods.writeToFile("Exception at WhseBtReport GenerateReport. Exception: " + ex.getMessage() + "\nStack: " + s,"","","","");
            IsRunning = null;
            GenerateReportStartedBool = null;
        }
    }

    private void rbtStopOrCarryOnScan(String func) {
        try {
            if (TempLoggerWhseScanActivity.RestarterTimerFinishCalled != null && TempLoggerWhseScanActivity.RestarterTimerFinishCalled) {
                objGlobalMethods.writeToFile("WhseBtReport GenerateReport " + func + " rbt.StopScan(false).","","","","");
                //Log.e(TAG, "WhseBtReport GenerateReport StopScan() started (RestarterTimerFinishCalled - true).");
                WhseBTLscanClass rbt = new WhseBTLscanClass();
                rbt.StopScan(false);
            } else if (alist != null && alist.size() > 0) {
                objGlobalMethods.writeToFile("WhseBtReport GenerateReport " + func +" rbt.carryOnScan(alist).","","","","");
                WhseBTLscanClass rbt = new WhseBTLscanClass();
                rbt.carryOnScan(alist);
            } else {
                objGlobalMethods.writeToFile("WhseBtReport GenerateReport " + func + " rbt.StopScan(true).","","","","");
                //Log.e(TAG, "WhseBtReport GenerateReport StopScan() started.");
                WhseBTLscanClass rbt = new WhseBTLscanClass();
                rbt.StopScan(true);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at WhseBtReport rbtStopOrCarryOnScan. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            IsRunning = null;
        }
    }

    public List<LoggingData> getDataList(Report r) {
        List<LoggingData> dl = null;
        try {
            if (r != null && r.DataList != null && r.DataList.size() > 0) {
                dl = r.DataList;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at WhseBtReport getDataList. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return dl;
        }
    }

    public void uploadLiveTemp() {
        ArrayList<TemperaturePayload> tpll = null;
        TemperaturePayload tpl = null;
        Date dt = new Date();
        Long l = dt.getTime();
        String timestampstr = "/Date(" + l + ")/";
        try {
            if (scan != null) {
                l = scan.getLastActiveTime();
                String dd = JobsHandler2.getDateTimeFromLong(l);
                objGlobalMethods.writeToFile("scan.getLastActiveTime() " + dd, "", "", "", "");
                timestampstr = "/Date(" + l + ")/";

                tpl = new TemperaturePayload();
                tpl.setBatteryLevel((int)(scan.getVoltage() * 100));
                tpl.setHumidity(scan.getHumidity());
                tpl.setTemperature(scan.getTemperature());
                tpl.setLat(0);
                tpl.setLong(0);
                tpl.setTimestamp(timestampstr);
                tpl.setMPDIDX(0);
                tpl.setMACAddress(scan.getMac());

                if (tpll == null)
                    tpll = new ArrayList<TemperaturePayload>();

                tpll.add(tpl);

                if (tpll != null && tpll.size() > 0) {
                    UploadTempDataObj utd = new UploadTempDataObj("", tpll.toArray(new TemperaturePayload[tpll.size()]));
                    JobsHandler2.addUploadWhseTempDataObjQue(objGlobalMethods.getUpdateJobInfoPreferences(), utd);
                    Context ctx = myApp.getAppContext();
                    ctx.sendBroadcast(new Intent(ctx, BackendRun2_BroadcastReceiver.class));
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at WhseBtReport uploadLiveTemp. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void uploadTemp(Report r) {
        ArrayList<TemperaturePayload> tpll = null;
        TemperaturePayload tpl = null;
        Date dt = new Date();
        Long l = dt.getTime();
        String timestampstr = "/Date(" + l + ")/";
        List<LoggingData> dl = null;
        LoggingData d = null;

        try {
            if (scan != null) {
                //ShowLog("Uploading Temperature Data.");
                dl = getDataList(r);
                if (dl != null && dl.size() > 0) {

                    for (int i = 0; i <= dl.size() -1; i++) {
                        d = dl.get(i);
                        l = d.getCreateTime();
                        timestampstr = "/Date(" + l + ")/";

                        tpl = new TemperaturePayload();
                        tpl.setBatteryLevel((int)(scan.getVoltage() * 100));
                        tpl.setHumidity(d.getHumidity());
                        tpl.setTemperature(d.getTemperature());
                        tpl.setLat(0);
                        tpl.setLong(0);
                        tpl.setTimestamp(timestampstr);
                        tpl.setMPDIDX(0);
                        tpl.setMACAddress(scan.getMac());

                        if (tpll == null)
                            tpll = new ArrayList<TemperaturePayload>();

                        tpll.add(tpl);
                    }
                }
            }

            if (tpll != null && tpll.size() > 0) {
                UploadTempDataObj utd = new UploadTempDataObj("", tpll.toArray(new TemperaturePayload[tpll.size()]));
                JobsHandler2.addUploadWhseTempDataObjQue(objGlobalMethods.getUpdateJobInfoPreferences(), utd);
                Context ctx = myApp.getAppContext();
                ctx.sendBroadcast(new Intent(ctx, BackendRun2_BroadcastReceiver.class));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at WhseBtReport uploadTemp. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
