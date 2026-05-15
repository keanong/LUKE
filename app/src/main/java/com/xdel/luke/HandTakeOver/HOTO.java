package com.xdel.luke.HandTakeOver;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HOTO {

    public long IDX;
    public String GUID;
    public long HO_DriverIDX;
    public String HO_Timestamp;
    public double HO_LAT;
    public double HO_LNG;
    public long TO_DriverIDX;
    public String TO_Timestamp;
    public double TO_LAT;
    public double TO_LNG;
    public int Flag = 0;
    public ArrayList<HOTOItem> Items;

    private String MinDateTimeStr = "/Date(" + (new Date(0).getTime()) + ")/";

    public HOTO() {
        this.HO_DriverIDX = 0;
        this.HO_Timestamp = MinDateTimeStr;
        this.HO_LAT = 0.0;
        this.HO_LNG = 0.0;
        this.TO_DriverIDX = 0;
        this.TO_Timestamp = MinDateTimeStr;
        this.TO_LAT = 0.0;
        this.TO_LNG = 0.0;
        this.Flag = 0;
        this.Items = null;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public void setHO_DriverIDX(long HO_DriverIDX) {
        this.HO_DriverIDX = HO_DriverIDX;
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

    public void setTO_DriverIDX(long TO_DriverIDX) {
        this.TO_DriverIDX = TO_DriverIDX;
    }


    public void setTO_Timestamp(String TO_Timestamp) {
        this.TO_Timestamp = TO_Timestamp;
    }

    public void setTO_LAT(double TO_LAT) {
        this.TO_LAT = TO_LAT;
    }

    public void setTO_LNG(double TO_LNG) {
        this.TO_LNG = TO_LNG;
    }

    public void setItems(ArrayList<HOTOItem> items) {
        Items = items;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }
}
