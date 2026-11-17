package com.xdel.luke.templogger;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
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
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.Rooting.RootGuard;
import com.xdel.luke.WebService;
import com.xdel.luke.mpd.MPDMainActivity;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.test.Report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class btReportGeneratorIntentService extends IntentService {

    //start of DeviceActivity class
    public ArrayList<BaseDevice> _Dlist = null;
    public BaseDevice _D = null;
    public BaseDevice _Device = null;
    public BleDevice _BleDevice = null;
    public DataManagerBase _DataManager = null;
    public Report _Report = null;
    public UploadTempDataObj autd = null;
    public String Password = "778899"; //Set Password
    private String[] RequestDataInfo;
    public List<AlarmSetting> AlarmList = null;
    public List<Integer> MarkList = null;
    public  final String TAG = "btReportGeneratorIntentService";
    public DeviceRecordType InitialStatus = DeviceRecordType.Unknown;
    public BaseDeviceScanObj bs = null;
    public Scan scan = null;
    public long MPDIDX;

    public btReportGeneratorIntentService() {
        super("btReportGeneratorIntentService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (intent != null && intent.hasExtra("dc")) {
                objGlobalMethods.writeToFile("1、Disconnecting all devices...","","","", "");
                BleManager.getInstance().disconnectAllDevice();
            } else if (intent != null && intent.hasExtra("d")) {
                String json = intent.getExtras().getString("d");

                bs = new Gson().fromJson(json, BaseDeviceScanObj.class);
                _Device = bs != null && bs._D != null ? bs._D : null;
                scan = bs != null && bs.scan != null ? bs.scan : null;
                MPDIDX = bs != null ? bs.MPDIDX : 0;

                if(_Device == null)
                    return;
                if (scan == null)
                    return;

                //R6: no logger connection, read or clear while RootGuard blocks this process
                if (RootGuard.blockOperation("btReportGeneratorIntentService.onHandleIntent", null))
                    return;

                ////new code here
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobInfoActivity.class) {
                    final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            act.showUploadingTempPD();
                        }
                    });
                }
                ////new code here

                objGlobalMethods.writeToFile("1、Connecting..." + _Device.getID() + "\nJson:" + json,"","","", "");
                BleManager.getInstance().connect(_Device.getMac(), bleGattCallback);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at btReportGeneratorIntentService onHandleIntent. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void InitDataManager() {
        if (_BleDevice == null)
            return;
        try {
            //R6: no unlock, read or clear while RootGuard blocks this process; close a connection that reached this point
            if (RootGuard.blockOperation("btReportGeneratorIntentService.InitDataManager", null)) {
                BleManager.getInstance().disconnect(_BleDevice);
                return;
            }
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
            }else {
                Unlock();
            }
        } catch (Exception ex) {
            String s = JobsHandler.getStackTraceString(ex);
            objGlobalMethods.writeToFile("Exception at btReportGeneratorIntentService InitDataManager. Exception: " + ex.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void Unlock() {
        if (_DataManager == null)
            return;
        try {
            _DataManager.Unlock(Password);
        } catch (Exception ex) {
            String s = JobsHandler.getStackTraceString(ex);
            objGlobalMethods.writeToFile("Exception at btReportGeneratorIntentService Unlock. Exception: " + ex.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public BleGattCallback bleGattCallback = new BleGattCallback() {
        @Override
        public void onStartConnect() {
            objGlobalMethods.writeToFile("onStartConnect: OK","","","","");
            //Log.i(TAG, "onStartConnect: OK");
        }

        @Override
        public void onConnectFail(BleDevice bleDevice, BleException exception) {
            ////new code here
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobInfoActivity.class) {
                final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.dismissUploadingTempPD();
                    }
                });
            }
            ////new code here


            objGlobalMethods.writeToFile("onConnectFail: bleDevice => " + (bleDevice != null ? bleDevice.getMac() : " null"),"","","", "");
            //Log.i(TAG, "onConnectFail: bleDevice => " + (bleDevice != null ? bleDevice.getMac() : " null"));
            BleManager.getInstance().destroy();
            objGlobalMethods.setBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "btonConnectFail", "1");
            stopSelf();
        }

        @Override
        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
            //Log.i(TAG, "onConnectSuccess: bleDevice => " + (bleDevice != null ? bleDevice.getMac() : " null"));
            objGlobalMethods.writeToFile("onConnectSuccess: bleDevice => " + (bleDevice != null ? bleDevice.getMac() : " null"),"","","","");
            objGlobalMethods.setBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "btonConnectFail", "");
            _BleDevice = bleDevice;
            InitDataManager();
        }

        @Override
        public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
            //Log.i(TAG, "onDisConnected: bleDevice => " + (device != null ? device.getMac() : " null"));
            objGlobalMethods.writeToFile("onDisConnected: bleDevice => " + (device != null ? device.getMac() : " null"),"","","","");
            _BleDevice = null;


            ////new code here
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobInfoActivity.class) {
                final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.dismissUploadingTempPD();
                    }
                });
            }
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobInfoActivity.class) {
                if (autd != null) {
                    final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            act.complianceCheck(autd);
                        }
                    });
                } else {
                    objGlobalMethods.writeToFile("complianceCheck not done due autd is null.","","","","");
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.temp_recorded_proceed)).show();
                }
            }
            ////new code here

        }
    };

    public IDataCallback dataCallback = new IDataCallback() {
        @Override
        public void onNotify(boolean status) {
            //Log.i(TAG, "onNotify: " + status);

            if (status) {
                //Log.i(TAG, "4、Unlock");
                Unlock();
            }
        }

        @Override
        public void onUnlock(boolean status, boolean isOk) {
            //Log.i(TAG, "onUnlock: " + status);
            if (status) {
                if (isOk) {
                    //Log.i(TAG, "5、GetLogStatus");
                    _DataManager.GetLogStatus();
                } else {
                    //Password is error
                    Password = "";
                }
            }
        }

        @Override
        public void onGetLogStatus(boolean status, DeviceRecordType recordStatus) {
            //Log.i(TAG, "onGetLogStatus: " + status);
            if(status){
                //Log.i(TAG, "6、SetConfig");

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
            //Log.i(TAG, "onSetConfig: " + status);
            if (status) {
                //Log.i(TAG, "9、RequestDataInfo");
                _DataManager.RequestDataInfo();
            }
        }

        @Override
        public void onGetAlarm(boolean status, List<AlarmSetting> tempAlarmList, List<AlarmSetting> rhAlarmList) {
            //Log.i(TAG, "onGetAlarm: " + status);
            if(status){
                //Log.i(TAG, "8、GetMark");
                _DataManager.GetMark();
            }
        }

        @Override
        public void onGetMark(boolean status, List<Integer> markList) {
            //Log.i(TAG, "onGetMark: " + status);
            if(status){
                //Log.i(TAG, "9、RequestDataInfo");
                _DataManager.RequestDataInfo();
            }
        }

        @Override
        public void onRequestDataInfo(boolean status, String[] info) {
            //Log.i(TAG, "onRequestDataInfo: " + status);
            if (status) {
                int dataCount = 0;
                if (info != null) {
                    dataCount = Integer.parseInt(info[0]);
                    RequestDataInfo = info;
                }

                if (dataCount > 0) {
                    //Log.i(TAG, "10、Receive(true)");
                    _DataManager.Receive(true);
                }
            }
        }

        @Override
        public void onReceive(boolean status, boolean isOpen) {
            //Log.i(TAG, "onReceive: " + status + " isOpen:" + isOpen);
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
            //Log.i(TAG, "11、Receive(false)");
            _DataManager.Receive(false);
            //Log.i(TAG, "12、Generating Report");
            GenerateReport(data);
        }

        @Override
        public void onPassword(boolean status) {

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

    public void GenerateReport(final List<LoggingData> data) {
        try {
            if (data == null || data.size() == 0) {
                return;
            }

            //objGlobalMethods.writeToFile("initial LoggingData for " + _Device.getID() + "\n" + new Gson().toJson(data),"","","","");
            objGlobalMethods.writeToFile("btReportGeneratorIntentService GenerateReport LoggingData for " + _Device.getID() + ", " + data.size() + " records.","","","","");

            int dataCount = 0;
            long beginTime = 0;
            long timeSpan = 0;
            DeviceRecordType recordStatus = DeviceRecordType.Initialize;
            long delayTime = 0;
            //boolean repeatStart = false;
            TemperatureUnitType temperatureUnit = TemperatureUnitType.C;
            //boolean stopButton = false;
            int startMode = 0;
            //long startTime = 0;
            String description = null;
            double mkt = -1000;

            if (RequestDataInfo != null) {
                try {
                    dataCount = Integer.parseInt(RequestDataInfo[0]);
                    beginTime = Long.parseLong(RequestDataInfo[1]);
                    timeSpan = Long.parseLong(RequestDataInfo[2]);
                    recordStatus = DeviceRecordType.values()[Integer.parseInt(RequestDataInfo[3])];
                    delayTime = Long.parseLong(RequestDataInfo[4]);
                    //repeatStart = Integer.parseInt(RequestDataInfo[5]) == 1 ? true : false;
                    temperatureUnit = RequestDataInfo[6].equals(TemperatureUnitType.F.toString()) ? TemperatureUnitType.F : TemperatureUnitType.C;
                    //stopButton = Integer.parseInt(RequestDataInfo[7]) == 1 ? true : false;
                    startMode = Integer.parseInt(RequestDataInfo[8]);
                    //startTime = Long.parseLong(RequestDataInfo[9]);
                    description = RequestDataInfo[10];
                    if(!RequestDataInfo[12].equals(""))
                        mkt = Double.parseDouble(RequestDataInfo[12]);
                } catch (Exception ex) {}

                if (beginTime == 0)
                    beginTime = data.get(0).getCreateTime();
            }
            if (dataCount != data.size()) {
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

            /*////old code here
            uploadTemp(_Report);
            _DataManager.SetStop();
            _DataManager.SetStart();

            if (_BleDevice != null)
                BleManager.getInstance().disconnect(_BleDevice);
            ////old code here*/

            ////new code here
            _DataManager.SetStop();
            _DataManager.SetStart();

            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobInfoActivity.class)
                uploadTempNow(_Report);

            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MPDMainActivity.class)
                uploadTemp(_Report);


            if (_BleDevice != null && myApp.currentActivity != null && myApp.currentActivity.getClass() == MPDMainActivity.class)
                BleManager.getInstance().disconnect(_BleDevice);
            ////new code here

        } catch (Exception ex) {
            String s = JobsHandler.getStackTraceString(ex);
            objGlobalMethods.writeToFile("Exception at btReportGeneratorIntentService GenerateReport. Exception: " + ex.getMessage() + "\nStack: " + s,"","","","");
            stopSelf();
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
            objGlobalMethods.writeToFile("Exception at btReportGeneratorIntentService getDataList. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return dl;
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
                        tpl.setMPDIDX(MPDIDX);

                        if (tpll == null)
                            tpll = new ArrayList<TemperaturePayload>();

                        tpll.add(tpl);
                    }
                }
            }

            if (tpll != null && tpll.size() > 0) {
                UploadTempDataObj utd = new UploadTempDataObj("", tpll.toArray(new TemperaturePayload[tpll.size()]));

                JobsHandler.addUploadTempDataObjQue(objGlobalMethods.getUpdateJobInfoPreferences(), utd);
                Context ctx = myApp.getAppContext();
                ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at btReportGeneratorIntentService uploadTemp. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            stopSelf();
        }
    }

    public void uploadTempNow(Report r) {
        ArrayList<TemperaturePayload> tpll = null;
        TemperaturePayload tpl = null;
        Date dt = new Date();
        Long l = dt.getTime();
        String timestampstr = "/Date(" + l + ")/";
        String TimestampReadable = "";
        List<LoggingData> dl = null;
        LoggingData d = null;

        try {
            if (scan != null) {
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
                        tpl.setTimestampLong(l);
                        tpl.setMPDIDX(MPDIDX);

                        if (tpll == null)
                            tpll = new ArrayList<TemperaturePayload>();

                        tpll.add(tpl);
                    }
                }
            }

            if (tpll != null && tpll.size() > 0) {
                //UploadTempDataObj utd = new UploadTempDataObj("", tpll.toArray(new TemperaturePayload[tpll.size()]));
                autd = new UploadTempDataObj("", tpll.toArray(new TemperaturePayload[tpll.size()]));
                objGlobalMethods.writeToFile("autd created. TemperaturePayload " + tpll.size() + "\n" + new Gson().toJson(tpll),"","","","");

                /*Boolean success = null;
                String ARegID = objGlobalMethods.getRegistrationIdOnly();
                success = WebService.L2_Upload_Temperature(ARegID, utd.TemperaturePayloads);
                JobsHandler.setCurrDTasPrevDTforIdlingCheck();
                if (success == null) {
                    objGlobalMethods.writeToFile("Error invoking L2_Upload_Temperature at btReportGeneratorIntentService, adding back to que.",
                            "", "", "", "");

                    SharedPreferences AupdateJobInfoPref = objGlobalMethods.getUpdateJobInfoPreferences();
                    JobsHandler.addUploadTempDataObjQue(AupdateJobInfoPref, utd);
                    JobsHandler.backEndIsRunning = false;
                    objGlobalMethods.writeToFile("Backend ended due Error invoking L2_Upload_Temperature at btReportGeneratorIntentService.", "", "", "", "");

                    //setCurrDTasPrevDTforIdlingCheck();
                    JobsHandler.startBackendRun15SecCounter();
                } else if (success) {
                    //if result return true, do nothing
                    objGlobalMethods.writeToFile("L2_Upload_Temperature successful at btReportGeneratorIntentService.", "", "", "", "");


                } else if (!success) {
                    //if result return false, stop current activity and postpone, do not add back to que
                    objGlobalMethods.writeToFile("L2_Upload_Temperature return false at btReportGeneratorIntentService, not add back to que.",
                            "", "", "", "");

                    JobsHandler.backEndIsRunning = false;
                    objGlobalMethods.writeToFile("Backend ended due L2_Upload_Temperature return false at btReportGeneratorIntentService.", "", "", "", "");

                    //setCurrDTasPrevDTforIdlingCheck();
                    JobsHandler.startBackendRun15SecCounter();
                }

                callComplianceCheck = success != null && success;*/

                if (_BleDevice != null)
                    BleManager.getInstance().disconnect(_BleDevice);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at btReportGeneratorIntentService uploadTempNow. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            stopSelf();
        }
    }

}