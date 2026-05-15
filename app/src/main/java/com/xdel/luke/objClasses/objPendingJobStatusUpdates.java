package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 19/01/2016.
 */
public class objPendingJobStatusUpdates {
    public String RegID;
    public long JobsIDX;
    public long AStatusCodeIDX;
    public String ATimeStamp;
    public String ARemarks;
    public String ABase64String;
    public double ALat;
    public double ALng;
    public String ARecipient;
    public objJobInfo currJobInfo;

    public objPendingJobStatusUpdates(String aRegID, long aJobsIDX, long aAStatusCodeIDX, String aATimeStamp, String aARemarks,
                                      String aABase64String, double aALat, double aALng, String aARecipient, objJobInfo acurrJobInfo){
        this.RegID = aRegID;
        this.JobsIDX = aJobsIDX;
        this.AStatusCodeIDX = aAStatusCodeIDX;
        this.ATimeStamp = aATimeStamp;
        this.ARemarks = aARemarks;
        this.ABase64String = aABase64String;
        this.ALat = aALat;
        this.ALng = aALng;
        this.ARecipient = aARecipient;
        this.currJobInfo = acurrJobInfo;
    }

}
