package com.xdel.luke.templogger;

public class TemperaturePayload {

    public long MPDIDX;
    public String MACAddress;
    public String Timestamp;
    public long TimestampLong;
    public double Temperature;
    public double Humidity;
    public int BatteryLevel;
    public double Lat;
    public double Long;

    public TemperaturePayload(){

    }

    public void setMPDIDX(long MPDIDX) {
        this.MPDIDX = MPDIDX;
    }

    public void setMACAddress(String MACAddress) {
        this.MACAddress = MACAddress;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public void setTimestampLong(long timestampLong) {
        TimestampLong = timestampLong;
    }

    public void setTemperature(double temperature) {
        Temperature = temperature;
    }

    public void setHumidity(double humidity) {
        Humidity = humidity;
    }

    public void setBatteryLevel(int batteryLevel) {
        BatteryLevel = batteryLevel;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }
}
