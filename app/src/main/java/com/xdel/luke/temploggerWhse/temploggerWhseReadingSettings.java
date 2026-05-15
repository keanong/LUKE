package com.xdel.luke.temploggerWhse;

public class temploggerWhseReadingSettings {
    public long MainTimer_leftTimeMS = 900000; //15min
    public long DeviceScanningTimer_leftTimeMS = 30000; //30s

    public temploggerWhseReadingSettings() {

    }

    public temploggerWhseReadingSettings(long aMainTimerSeconds, long aDeviceScanningTimer_leftTimeMS) {
        this.MainTimer_leftTimeMS = aMainTimerSeconds;
        this.DeviceScanningTimer_leftTimeMS = aDeviceScanningTimer_leftTimeMS;
    }

    public void MainTimer_leftTimeMS(long mainTimerSeconds) {
        MainTimer_leftTimeMS = mainTimerSeconds;
    }

    public void setDeviceScanningTimer_leftTimeMS(long deviceScanningTimer_leftTimeMS) {
        DeviceScanningTimer_leftTimeMS = deviceScanningTimer_leftTimeMS;
    }
}
