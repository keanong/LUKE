package com.xdel.luke.HandTakeOver;

import java.util.ArrayList;

public class to_json {
    public ArrayList<ho_PostData> scannedItems;
    public String refTOHash;
    public String TODriverIDX;
    public String TO_Timestamp = "";
    public double TO_LAT = 0.0;
    public double TO_LNG = 0.0;

    public to_json(String aTODriverIDX, String arefTOHash, String aTO_Timestamp, double aTO_LAT, double aTO_LNG) {
        this.refTOHash = arefTOHash;
        this.TODriverIDX = aTODriverIDX;
        this.TO_Timestamp = aTO_Timestamp;
        this.TO_LAT = aTO_LAT;
        this.TO_LNG = aTO_LNG;
    }

    public void setScannedItems(ArrayList<ho_PostData> scannedItems) {
        this.scannedItems = scannedItems;
    }
}
