package com.xdel.luke.HandTakeOver;

import java.util.ArrayList;

public class ho_json {
    public ArrayList<ho_PostData> scannedItems;
    public String refHOHash;
    public String HODriverIDX;
    public String HO_Timestamp = "";
    public double HO_LAT = 0.0;
    public double HO_LNG = 0.0;

    public ho_json(ArrayList<ho_PostData> ascanneditems, String arefHOHash, String aHODriverIDX) {
        this.scannedItems = ascanneditems;
        this.refHOHash = arefHOHash;
        this.HODriverIDX = aHODriverIDX;
    }

    public void setHO_Timestamp(String HO_Timestamp) {
        this.HO_Timestamp = HO_Timestamp;
    }

    public void setHO_LAT(double HO_LAT) {
        this.HO_LAT = HO_LAT;
    }

    public void setHO_LNG(double HO_LNG) {
        this.HO_LNG = HO_LNG;
    }

}
