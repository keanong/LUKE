package com.xdel.luke.templogger;

public class JobTemperaturePayload {
    public long AJobsIDX;
    public DeviceInfo DeviceInfo;
    public Temperature_Log[] TemperatureLogs;
    public int Status;
    public String Message;

    public void setTemperatureLogs(Temperature_Log[] temperatureLogs) {
        TemperatureLogs = temperatureLogs;
    }

    public void setAJobsIDX(long AJobsIDX) {
        this.AJobsIDX = AJobsIDX;
    }
}
