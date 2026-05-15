package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 03/04/2017.
 */

public class DriverPointDailyGroup_PostData{

    public long IDX;
    public long JobsIDX;
    public String JobNo;
    public String DeliveryContent;
    public long StatusCodeIDX;
    public String StatusCode;
    public String TimeStamp;
    public long UpdateMode;
    public double UpdateLat;
    public double UpdateLng;
    public int UpdateAccuracy;
    public int Flag;
    public int AlternateColor;
    public String Unit;


    public DriverPointDailyGroup_PostData(long aIDX, long aJobsIDX, String aJobNo, String aDeliveryContent, long aStatusCodeIDX, String aStatusCode, String aTimeStamp,
                                          long aUpdateMode, double aUpdateLat, long aUpdateLng, int aUpdateAccuracy, int aFlag, int aAlternateColor, String aUnit){
        this.IDX = aIDX;
        this.JobsIDX = aJobsIDX;
        this.JobNo = aJobNo;
        this.DeliveryContent = aDeliveryContent;
        this.StatusCodeIDX = aStatusCodeIDX;
        this.StatusCode = aStatusCode;
        this.TimeStamp = aTimeStamp;
        this.UpdateMode = aUpdateMode;
        this.UpdateLat = aUpdateLat;
        this.UpdateLng = aUpdateLng;
        this.UpdateAccuracy = aUpdateAccuracy;
        this.Flag = aFlag;
        this.AlternateColor = aAlternateColor;
        this.Unit = aUnit;
    }


}
