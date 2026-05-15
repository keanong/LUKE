package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 03/04/2017.
 */
public class objDriverPointItem   implements Comparable<objDriverPointItem>{
    public long IDX;
    public long DriverPointIDX;
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
    public int Rank;
    public String PointType;
    public int QueuePosition;
    public String FromDateTime;
    public String ToDateTime;
    public String ExtFromDateTime;
    public String ExtDateTime;

    public objDriverPointItem(long aIDX, long aJobsIDX, String aJobNo, String aDeliveryContent, long aStatusCodeIDX, String aStatusCode, String aTimeStamp,
                              long aUpdateMode, double aUpdateLat, long aUpdateLng, int aUpdateAccuracy, int aFlag) {
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
    }

    public int compareTo(objDriverPointItem other){
        return StatusCode.compareTo(other.StatusCode);
    }
}
