package com.xdel.luke.templogger;

public class MacDevicePair {
    public long MPDIDX;
    public String Mac;
    public int Device_Type;
    public long[] Jobs_Attached;

    public MacDevicePair(){

    }

    public MacDevicePair(long aTemperatureLoggerIDX, String aMac, int aDevice_Type, long[] aJobs_Attached){
        this.MPDIDX = aTemperatureLoggerIDX;
        this.Mac = aMac;
        this.Device_Type = aDevice_Type;
        this.Jobs_Attached = aJobs_Attached;
    }

    public void setMPDIDX(long MPDIDX) {
        this.MPDIDX = MPDIDX;
    }

    public void setMac(String aMac) {
        this.Mac = aMac;
    }

    public void setDevice_Type(int device_Type) {
        Device_Type = device_Type;
    }

    public void setJobs_Attached(long[] aJobs_Attached) {
        this.Jobs_Attached = aJobs_Attached;
    }
}
