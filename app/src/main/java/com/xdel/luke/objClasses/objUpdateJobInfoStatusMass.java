package com.xdel.luke.objClasses;

public class objUpdateJobInfoStatusMass {

    public String RegID;
    public Long[] JobsIDX;
    public String JobsIDXStr;
    public long StatusCodeIDX;
    public String TimeStamp;
    public String Remarks;
    public String Base64String;
    public double Lat;
    public double Lng;
    public int Accuracy;
    public String Recipient;
    public String FilePath;

    public objUpdateJobInfoStatusMass(String ARegID, Long[] AJobsIDX, String aJobsIDXStr, long AStatusCodeIDX, String ATimeStamp, String ARemarks, String ABase64String, double ALat, double ALng, int AAccuracy, String ARecipient){
        this.RegID = ARegID;
        this.JobsIDX = AJobsIDX;
        this.JobsIDXStr = aJobsIDXStr;
        this.StatusCodeIDX = AStatusCodeIDX;
        this.TimeStamp = ATimeStamp;
        this.Remarks = ARemarks;
        this.Base64String = ABase64String;
        this.Lat = ALat;
        this.Lng = ALng;
        this.Accuracy = AAccuracy;
        this.Recipient = ARecipient;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }
}
