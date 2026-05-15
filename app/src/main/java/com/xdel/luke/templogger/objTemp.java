package com.xdel.luke.templogger;

import com.xdel.luke.CustomGallery;

public class objTemp {
    public String TempLastRecorded = "";
    public String TempLastRecordedTimeStampTSstr = "";
    public CustomGallery item = null;
    public String ManualAuto = "0"; //manual record: 0, auto record: 1
    public int BatteryLvl = 0;
    public long lat = 0;
    public long lng = 0;
    public long MPDIDX = 0;
    public int Device_Type;

    public objTemp(){

    }

    public void setDevice_Type(int device_Type) {
        Device_Type = device_Type;
    }

    public void setManualAuto(String manualAuto) {
        ManualAuto = manualAuto;
    }

    public objTemp(String aTempLastRecorded, String aTempLastRecordedTimeStampTSstr) {
        this.TempLastRecorded = aTempLastRecorded;
        this.TempLastRecordedTimeStampTSstr = aTempLastRecordedTimeStampTSstr;
    }

    public void setTempLastRecorded(String tempLastRecorded) {
        TempLastRecorded = tempLastRecorded;
    }

    public void setTempLastRecordedTimeStampTSstr(String tempLastRecordedTimeStampTSstr) {
        TempLastRecordedTimeStampTSstr = tempLastRecordedTimeStampTSstr;
    }

    public void setItem(CustomGallery item) {
        this.item = item;
    }

    public void setBatteryLvl(int batteryLvl) {
        BatteryLvl = batteryLvl;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public void setLng(long lng) {
        this.lng = lng;
    }

    public void setMPDIDX(long MPDIDX) {
        this.MPDIDX = MPDIDX;
    }
}
