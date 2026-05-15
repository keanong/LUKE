package com.xdel.luke.test;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Looper;
import android.os.Build.VERSION;

import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.bluetooth.BleBluetooth;
import com.tzone.bluetooth.bluetooth.MultipleBluetoothController;
import com.tzone.bluetooth.bluetooth.SplitWriter;
import com.tzone.bluetooth.callback.BleGattCallback;
import com.tzone.bluetooth.callback.BleIndicateCallback;
import com.tzone.bluetooth.callback.BleMtuChangedCallback;
import com.tzone.bluetooth.callback.BleNotifyCallback;
import com.tzone.bluetooth.callback.BleReadCallback;
import com.tzone.bluetooth.callback.BleRssiCallback;
import com.tzone.bluetooth.callback.BleScanAndConnectCallback;
import com.tzone.bluetooth.callback.BleScanCallback;
import com.tzone.bluetooth.callback.BleWriteCallback;
import com.tzone.bluetooth.data.BleDevice;
import com.tzone.bluetooth.data.BleScanState;
import com.tzone.bluetooth.exception.BleException;
import com.tzone.bluetooth.exception.OtherException;
import com.tzone.bluetooth.scan.BleScanRuleConfig;
import com.tzone.bluetooth.scan.BleScanner;
import com.tzone.bluetooth.utils.BleLog;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class bm {
    private Application context;
    private BleScanRuleConfig bleScanRuleConfig;
    private BluetoothAdapter bluetoothAdapter;
    private MultipleBluetoothController multipleBluetoothController;
    private BluetoothManager bluetoothManager;
    private BleScanner bles;
    public static final int DEFAULT_SCAN_TIME = 10000;
    private static final int DEFAULT_MAX_MULTIPLE_DEVICE = 7;
    private static final int DEFAULT_OPERATE_TIME = 5000;
    private static final int DEFAULT_CONNECT_RETRY_COUNT = 0;
    private static final int DEFAULT_CONNECT_RETRY_INTERVAL = 5000;
    private static final int DEFAULT_MTU = 23;
    private static final int DEFAULT_MAX_MTU = 512;
    private static final int DEFAULT_WRITE_DATA_SPLIT_COUNT = 20;
    private static final int DEFAULT_CONNECT_OVER_TIME = 10000;
    private int maxConnectCount = 7;
    private int operateTimeout = 5000;
    private int reConnectCount = 0;
    private long reConnectInterval = 5000L;
    private int splitWriteNum = 20;
    private long connectOverTime = 10000L;

    public bm() {
    }

    public static bm getInstance() {
        return bm.BleManagerHolder.sBleManager;
    }

    public void init(Application app, BleScanner bs) {
        if (this.context == null && app != null) {
            this.context = app;
            if (this.isSupportBle()) {
                this.bluetoothManager = (BluetoothManager)this.context.getSystemService("bluetooth");
            }

            this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            this.multipleBluetoothController = new MultipleBluetoothController();
            this.bleScanRuleConfig = new BleScanRuleConfig();
            this.bles = bs;
        }

    }

    public Context getContext() {
        return this.context;
    }

    public BluetoothManager getBluetoothManager() {
        return this.bluetoothManager;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return this.bluetoothAdapter;
    }

    public BleScanRuleConfig getScanRuleConfig() {
        return this.bleScanRuleConfig;
    }

    public MultipleBluetoothController getMultipleBluetoothController() {
        return this.multipleBluetoothController;
    }

    public void initScanRule(BleScanRuleConfig config) {
        this.bleScanRuleConfig = config;
    }

    public int getMaxConnectCount() {
        return this.maxConnectCount;
    }

    public bm setMaxConnectCount(int count) {
        if (count > 7) {
            count = 7;
        }

        this.maxConnectCount = count;
        return this;
    }

    public int getOperateTimeout() {
        return this.operateTimeout;
    }

    public bm setOperateTimeout(int count) {
        this.operateTimeout = count;
        return this;
    }

    public void setBScanner(BleScanner ss){
        this.bles = ss;
    }

    public int getReConnectCount() {
        return this.reConnectCount;
    }

    public long getReConnectInterval() {
        return this.reConnectInterval;
    }

    public bm setReConnectCount(int count) {
        return this.setReConnectCount(count, 5000L);
    }

    public bm setReConnectCount(int count, long interval) {
        if (count > 10) {
            count = 10;
        }

        if (interval < 0L) {
            interval = 0L;
        }

        this.reConnectCount = count;
        this.reConnectInterval = interval;
        return this;
    }

    public int getSplitWriteNum() {
        return this.splitWriteNum;
    }

    public bm setSplitWriteNum(int num) {
        if (num > 0) {
            this.splitWriteNum = num;
        }

        return this;
    }

    public long getConnectOverTime() {
        return this.connectOverTime;
    }

    public bm setConnectOverTime(long time) {
        if (time <= 0L) {
            time = 100L;
        }

        this.connectOverTime = time;
        return this;
    }

    public bm enableLog(boolean enable) {
        BleLog.isPrint = enable;
        return this;
    }

    public void scan(BleScanCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("BleScanCallback can not be Null!");
        } else if (!this.isBlueEnable()) {
            BleLog.e("Bluetooth not enable!");
            callback.onScanStarted(false);
        } else {
            UUID[] serviceUuids = this.bleScanRuleConfig.getServiceUuids();
            String[] deviceNames = this.bleScanRuleConfig.getDeviceNames();
            String deviceMac = this.bleScanRuleConfig.getDeviceMac();
            boolean fuzzy = this.bleScanRuleConfig.isFuzzy();
            long timeOut = this.bleScanRuleConfig.getScanTimeOut();
            //BleScanner.getInstance().scan(serviceUuids, deviceNames, deviceMac, fuzzy, timeOut, callback);
            this.bles.scan(serviceUuids, deviceNames, deviceMac, fuzzy, timeOut, callback);
        }
    }

    public void scanAndConnect(BleScanAndConnectCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("BleScanAndConnectCallback can not be Null!");
        } else if (!this.isBlueEnable()) {
            BleLog.e("Bluetooth not enable!");
            callback.onScanStarted(false);
        } else {
            UUID[] serviceUuids = this.bleScanRuleConfig.getServiceUuids();
            String[] deviceNames = this.bleScanRuleConfig.getDeviceNames();
            String deviceMac = this.bleScanRuleConfig.getDeviceMac();
            boolean fuzzy = this.bleScanRuleConfig.isFuzzy();
            long timeOut = this.bleScanRuleConfig.getScanTimeOut();
            //BleScanner.getInstance().scanAndConnect(serviceUuids, deviceNames, deviceMac, fuzzy, timeOut, callback);
            this.bles.scanAndConnect(serviceUuids, deviceNames, deviceMac, fuzzy, timeOut, callback);
        }
    }

    public BluetoothGatt connect(BleDevice bleDevice, BleGattCallback bleGattCallback) {
        if (bleGattCallback == null) {
            throw new IllegalArgumentException("BleGattCallback can not be Null!");
        } else if (!this.isBlueEnable()) {
            BleLog.e("Bluetooth not enable!");
            bleGattCallback.onConnectFail(bleDevice, new OtherException("Bluetooth not enable!"));
            return null;
        } else {
            if (Looper.myLooper() == null || Looper.myLooper() != Looper.getMainLooper()) {
                BleLog.w("Be careful: currentThread is not MainThread!");
            }

            if (bleDevice != null && bleDevice.getDevice() != null) {
                BleBluetooth bleBluetooth = this.multipleBluetoothController.buildConnectingBle(bleDevice);
                boolean autoConnect = this.bleScanRuleConfig.isAutoConnect();
                return bleBluetooth.connect(bleDevice, autoConnect, bleGattCallback);
            } else {
                bleGattCallback.onConnectFail(bleDevice, new OtherException("Not Found Device Exception Occurred!"));
                return null;
            }
        }
    }

    public BluetoothGatt connect(String mac, BleGattCallback bleGattCallback) {
        BluetoothDevice bluetoothDevice = this.getBluetoothAdapter().getRemoteDevice(mac);
        BleDevice bleDevice = new BleDevice(bluetoothDevice, 0, (byte[])null, 0L);
        return this.connect(bleDevice, bleGattCallback);
    }

    public void cancelScan() {
        //BleScanner.getInstance().stopLeScan();
        this.bles.stopLeScan();
    }

    public void notify(BleDevice bleDevice, String uuid_service, String uuid_notify, BleNotifyCallback callback) {
        this.notify(bleDevice, uuid_service, uuid_notify, false, callback);
    }

    public void notify(BleDevice bleDevice, String uuid_service, String uuid_notify, boolean useCharacteristicDescriptor, BleNotifyCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("BleNotifyCallback can not be Null!");
        } else {
            BleBluetooth bleBluetooth = this.multipleBluetoothController.getBleBluetooth(bleDevice);
            if (bleBluetooth == null) {
                callback.onNotifyFailure(new OtherException("This device not connect!"));
            } else {
                bleBluetooth.newBleConnector().withUUIDString(uuid_service, uuid_notify).enableCharacteristicNotify(callback, uuid_notify, useCharacteristicDescriptor);
            }

        }
    }

    public void indicate(BleDevice bleDevice, String uuid_service, String uuid_indicate, BleIndicateCallback callback) {
        this.indicate(bleDevice, uuid_service, uuid_indicate, false, callback);
    }

    public void indicate(BleDevice bleDevice, String uuid_service, String uuid_indicate, boolean useCharacteristicDescriptor, BleIndicateCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("BleIndicateCallback can not be Null!");
        } else {
            BleBluetooth bleBluetooth = this.multipleBluetoothController.getBleBluetooth(bleDevice);
            if (bleBluetooth == null) {
                callback.onIndicateFailure(new OtherException("This device not connect!"));
            } else {
                bleBluetooth.newBleConnector().withUUIDString(uuid_service, uuid_indicate).enableCharacteristicIndicate(callback, uuid_indicate, useCharacteristicDescriptor);
            }

        }
    }

    public boolean stopNotify(BleDevice bleDevice, String uuid_service, String uuid_notify) {
        return this.stopNotify(bleDevice, uuid_service, uuid_notify, false);
    }

    public boolean stopNotify(BleDevice bleDevice, String uuid_service, String uuid_notify, boolean useCharacteristicDescriptor) {
        BleBluetooth bleBluetooth = this.multipleBluetoothController.getBleBluetooth(bleDevice);
        if (bleBluetooth == null) {
            return false;
        } else {
            boolean success = bleBluetooth.newBleConnector().withUUIDString(uuid_service, uuid_notify).disableCharacteristicNotify(useCharacteristicDescriptor);
            if (success) {
                bleBluetooth.removeNotifyCallback(uuid_notify);
            }

            return success;
        }
    }

    public boolean stopIndicate(BleDevice bleDevice, String uuid_service, String uuid_indicate) {
        return this.stopIndicate(bleDevice, uuid_service, uuid_indicate, false);
    }

    public boolean stopIndicate(BleDevice bleDevice, String uuid_service, String uuid_indicate, boolean useCharacteristicDescriptor) {
        BleBluetooth bleBluetooth = this.multipleBluetoothController.getBleBluetooth(bleDevice);
        if (bleBluetooth == null) {
            return false;
        } else {
            boolean success = bleBluetooth.newBleConnector().withUUIDString(uuid_service, uuid_indicate).disableCharacteristicIndicate(useCharacteristicDescriptor);
            if (success) {
                bleBluetooth.removeIndicateCallback(uuid_indicate);
            }

            return success;
        }
    }

    public void write(BleDevice bleDevice, String uuid_service, String uuid_write, byte[] data, final long delay, final BleWriteCallback callback) {
        this.write(bleDevice, uuid_service, uuid_write, data, new BleWriteCallback() {
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException var5) {
                }

                if (callback != null) {
                    callback.onWriteSuccess(current, total, justWrite);
                }

            }

            public void onWriteFailure(BleException exception) {
                if (callback != null) {
                    callback.onWriteFailure(exception);
                }

            }
        });
    }

    public void write(BleDevice bleDevice, String uuid_service, String uuid_write, byte[] data, BleWriteCallback callback) {
        this.write(bleDevice, uuid_service, uuid_write, data, true, callback);
    }

    public void write(BleDevice bleDevice, String uuid_service, String uuid_write, byte[] data, boolean split, BleWriteCallback callback) {
        this.write(bleDevice, uuid_service, uuid_write, data, split, true, 0L, callback);
    }

    public void write(BleDevice bleDevice, String uuid_service, String uuid_write, byte[] data, boolean split, boolean sendNextWhenLastSuccess, long intervalBetweenTwoPackage, BleWriteCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("BleWriteCallback can not be Null!");
        } else if (data == null) {
            BleLog.e("data is Null!");
            callback.onWriteFailure(new OtherException("data is Null!"));
        } else {
            if (data.length > 20 && !split) {
                BleLog.w("Be careful: data's length beyond 20! Ensure MTU higher than 23, or use spilt write!");
            }

            BleBluetooth bleBluetooth = this.multipleBluetoothController.getBleBluetooth(bleDevice);
            if (bleBluetooth == null) {
                callback.onWriteFailure(new OtherException("This device not connect!"));
            } else if (split && data.length > this.getSplitWriteNum()) {
                (new SplitWriter()).splitWrite(bleBluetooth, uuid_service, uuid_write, data, sendNextWhenLastSuccess, intervalBetweenTwoPackage, callback);
            } else {
                bleBluetooth.newBleConnector().withUUIDString(uuid_service, uuid_write).writeCharacteristic(data, callback, uuid_write);
            }

        }
    }

    public void read(BleDevice bleDevice, String uuid_service, String uuid_read, final long delay, final BleReadCallback callback) {
        this.read(bleDevice, uuid_service, uuid_read, new BleReadCallback() {
            public void onReadSuccess(byte[] data) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException var3) {
                }

                if (callback != null) {
                    callback.onReadSuccess(data);
                }

            }

            public void onReadFailure(BleException exception) {
                if (callback != null) {
                    callback.onReadFailure(exception);
                }

            }
        });
    }

    public void read(BleDevice bleDevice, String uuid_service, String uuid_read, BleReadCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("BleReadCallback can not be Null!");
        } else {
            BleBluetooth bleBluetooth = this.multipleBluetoothController.getBleBluetooth(bleDevice);
            if (bleBluetooth == null) {
                callback.onReadFailure(new OtherException("This device is not connected!"));
            } else {
                bleBluetooth.newBleConnector().withUUIDString(uuid_service, uuid_read).readCharacteristic(callback, uuid_read);
            }

        }
    }

    public void readRssi(BleDevice bleDevice, BleRssiCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("BleRssiCallback can not be Null!");
        } else {
            BleBluetooth bleBluetooth = this.multipleBluetoothController.getBleBluetooth(bleDevice);
            if (bleBluetooth == null) {
                callback.onRssiFailure(new OtherException("This device is not connected!"));
            } else {
                bleBluetooth.newBleConnector().readRemoteRssi(callback);
            }

        }
    }

    public void setMtu(BleDevice bleDevice, int mtu, BleMtuChangedCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("BleMtuChangedCallback can not be Null!");
        } else if (mtu > 512) {
            BleLog.e("requiredMtu should lower than 512 !");
            callback.onSetMTUFailure(new OtherException("requiredMtu should lower than 512 !"));
        } else if (mtu < 23) {
            BleLog.e("requiredMtu should higher than 23 !");
            callback.onSetMTUFailure(new OtherException("requiredMtu should higher than 23 !"));
        } else {
            BleBluetooth bleBluetooth = this.multipleBluetoothController.getBleBluetooth(bleDevice);
            if (bleBluetooth == null) {
                callback.onSetMTUFailure(new OtherException("This device is not connected!"));
            } else {
                bleBluetooth.newBleConnector().setMtu(mtu, callback);
            }

        }
    }

    public boolean requestConnectionPriority(BleDevice bleDevice, int connectionPriority) {
        if (VERSION.SDK_INT >= 21) {
            BleBluetooth bleBluetooth = this.multipleBluetoothController.getBleBluetooth(bleDevice);
            return bleBluetooth == null ? false : bleBluetooth.newBleConnector().requestConnectionPriority(connectionPriority);
        } else {
            return false;
        }
    }

    public boolean isSupportBle() {
        return VERSION.SDK_INT >= 18 && this.context.getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
    }

    @SuppressLint({"MissingPermission"})
    public void enableBluetooth() {
        if (this.bluetoothAdapter != null) {
            this.bluetoothAdapter.enable();
        }

    }

    @SuppressLint({"MissingPermission"})
    public void disableBluetooth() {
        if (this.bluetoothAdapter != null && this.bluetoothAdapter.isEnabled()) {
            this.bluetoothAdapter.disable();
        }

    }

    public boolean isBlueEnable() {
        return this.bluetoothAdapter != null && this.bluetoothAdapter.isEnabled();
    }

    public BleDevice convertBleDevice(BluetoothDevice bluetoothDevice) {
        return new BleDevice(bluetoothDevice);
    }

    @TargetApi(21)
    public BleDevice convertBleDevice(ScanResult scanResult) {
        if (scanResult == null) {
            throw new IllegalArgumentException("scanResult can not be Null!");
        } else {
            BluetoothDevice bluetoothDevice = scanResult.getDevice();
            int rssi = scanResult.getRssi();
            ScanRecord scanRecord = scanResult.getScanRecord();
            byte[] bytes = null;
            if (scanRecord != null) {
                bytes = scanRecord.getBytes();
            }

            long timestampNanos = scanResult.getTimestampNanos();
            return new BleDevice(bluetoothDevice, rssi, bytes, timestampNanos);
        }
    }

    public BleBluetooth getBleBluetooth(BleDevice bleDevice) {
        return this.multipleBluetoothController != null ? this.multipleBluetoothController.getBleBluetooth(bleDevice) : null;
    }

    public BluetoothGatt getBluetoothGatt(BleDevice bleDevice) {
        BleBluetooth bleBluetooth = this.getBleBluetooth(bleDevice);
        return bleBluetooth != null ? bleBluetooth.getBluetoothGatt() : null;
    }

    public List<BluetoothGattService> getBluetoothGattServices(BleDevice bleDevice) {
        BluetoothGatt gatt = this.getBluetoothGatt(bleDevice);
        return gatt != null ? gatt.getServices() : null;
    }

    public List<BluetoothGattCharacteristic> getBluetoothGattCharacteristics(BluetoothGattService service) {
        return service.getCharacteristics();
    }

    public void removeConnectGattCallback(BleDevice bleDevice) {
        BleBluetooth bleBluetooth = this.getBleBluetooth(bleDevice);
        if (bleBluetooth != null) {
            bleBluetooth.removeConnectGattCallback();
        }

    }

    public void removeRssiCallback(BleDevice bleDevice) {
        BleBluetooth bleBluetooth = this.getBleBluetooth(bleDevice);
        if (bleBluetooth != null) {
            bleBluetooth.removeRssiCallback();
        }

    }

    public void removeMtuChangedCallback(BleDevice bleDevice) {
        BleBluetooth bleBluetooth = this.getBleBluetooth(bleDevice);
        if (bleBluetooth != null) {
            bleBluetooth.removeMtuChangedCallback();
        }

    }

    public void removeNotifyCallback(BleDevice bleDevice, String uuid_notify) {
        BleBluetooth bleBluetooth = this.getBleBluetooth(bleDevice);
        if (bleBluetooth != null) {
            bleBluetooth.removeNotifyCallback(uuid_notify);
        }

    }

    public void removeIndicateCallback(BleDevice bleDevice, String uuid_indicate) {
        BleBluetooth bleBluetooth = this.getBleBluetooth(bleDevice);
        if (bleBluetooth != null) {
            bleBluetooth.removeIndicateCallback(uuid_indicate);
        }

    }

    public void removeWriteCallback(BleDevice bleDevice, String uuid_write) {
        BleBluetooth bleBluetooth = this.getBleBluetooth(bleDevice);
        if (bleBluetooth != null) {
            bleBluetooth.removeWriteCallback(uuid_write);
        }

    }

    public void removeReadCallback(BleDevice bleDevice, String uuid_read) {
        BleBluetooth bleBluetooth = this.getBleBluetooth(bleDevice);
        if (bleBluetooth != null) {
            bleBluetooth.removeReadCallback(uuid_read);
        }

    }

    public void clearCharacterCallback(BleDevice bleDevice) {
        BleBluetooth bleBluetooth = this.getBleBluetooth(bleDevice);
        if (bleBluetooth != null) {
            bleBluetooth.clearCharacterCallback();
        }

    }

    public BleScanState getScanSate() {
        //return BleScanner.getInstance().getScanState();
        return this.bles.getScanState();
    }

    public List<BleDevice> getAllConnectedDevice() {
        return this.multipleBluetoothController == null ? null : this.multipleBluetoothController.getDeviceList();
    }

    @SuppressLint({"MissingPermission"})
    public int getConnectState(BleDevice bleDevice) {
        return bleDevice != null ? this.bluetoothManager.getConnectionState(bleDevice.getDevice(), 7) : 0;
    }

    public boolean isConnected(BleDevice bleDevice) {
        return this.getConnectState(bleDevice) == 2;
    }

    public boolean isConnected(String mac) {
        List<BleDevice> list = this.getAllConnectedDevice();
        Iterator var3 = list.iterator();

        BleDevice bleDevice;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            bleDevice = (BleDevice)var3.next();
        } while(bleDevice == null || !bleDevice.getMac().equals(mac));

        return true;
    }

    public void disconnect(BleDevice bleDevice) {
        if (this.multipleBluetoothController != null) {
            this.multipleBluetoothController.disconnect(bleDevice);
        }

    }

    public void disconnectAllDevice() {
        if (this.multipleBluetoothController != null) {
            this.multipleBluetoothController.disconnectAllDevice();
        }

    }

    public void destroy() {
        if (this.multipleBluetoothController != null) {
            this.multipleBluetoothController.destroy();
        }

    }

    private static class BleManagerHolder {
        private static final bm sBleManager = new bm();

        private BleManagerHolder() {
        }
    }
}
