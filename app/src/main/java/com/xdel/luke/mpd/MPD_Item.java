package com.xdel.luke.mpd;

public class MPD_Item {
    public long IDX;
    public int Device_Type;
    public String Serial_Number;
    public String Device_Name;
    public String Device_Identifier;
    public String Remarks;
    public int Flag;

    public MPD_Item(){

    }

    public void setFlag(int flag) {
        Flag = flag;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public void setDevice_Identifier(String device_Identifier) {
        Device_Identifier = device_Identifier;
    }

    public void setDevice_Name(String device_Name) {
        Device_Name = device_Name;
    }

    public void setSerial_Number(String serial_Number) {
        Serial_Number = serial_Number;
    }

    public void setDevice_Type(int device_Type) {
        Device_Type = device_Type;
    }

    public void setIDX(long IDX) {
        this.IDX = IDX;
    }
}
