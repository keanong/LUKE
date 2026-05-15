package com.xdel.luke.objClasses;

public class MPD_PostData {
    public long MPD_IDX;
    public String MPD_Type;
    public String MPD_SN;

    public MPD_PostData(String aMPD_Type, String aMPD_SN){
        this.MPD_Type = aMPD_Type;
        this.MPD_SN = aMPD_SN;
    }
}
