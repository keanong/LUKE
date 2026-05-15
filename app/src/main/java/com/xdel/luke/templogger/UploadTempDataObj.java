package com.xdel.luke.templogger;

public class UploadTempDataObj {
    public String RegID;
    public TemperaturePayload[] TemperaturePayloads;

    public UploadTempDataObj() {

    }

    public UploadTempDataObj(String aRegID, TemperaturePayload[] aTemperaturePayloads) {
        this.RegID = aRegID;
        this.TemperaturePayloads = aTemperaturePayloads;
    }


}
