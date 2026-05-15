package com.xdel.luke.templogger;

import com.xdel.luke.CustomGallery;

public class Temperature_Log {
    public String Timestamp;
    public double Temperature;
    public double Humidity;
    public CustomGallery item = null;

    public void setTemperature(double temperature) {
        Temperature = temperature;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public void setItem(CustomGallery item) {
        this.item = item;
    }
}

