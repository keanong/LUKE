package com.xdel.luke.Caging;

public class objCageItem {
    public long IDX;
    public long CageIDX;
    public long UserIDX;
    public String Timestamp;
    public String Reference;
    public long JobsIDX;
    public String NotBefore;
    public long DriverIDX;
    public int Flag;
    public String Job_Ref;

    public objCageItem(long aIDX, long aCageIDX, long aUserIDX, String aTimestamp, String aReference, long aJobsIDX, String aNotBefore, long aDriverIDX, int aFlag, String aJob_Ref){
        this.IDX = aIDX;
        this.CageIDX = aCageIDX;
        this.UserIDX = aUserIDX;
        this.Timestamp = aTimestamp;
        this.Reference = aReference;
        this.JobsIDX = aJobsIDX;
        this.NotBefore = aNotBefore;
        this.DriverIDX = aDriverIDX;
        this.Flag = aFlag;
        this.Job_Ref = aJob_Ref;
    }

    public long getIDX() {
        return IDX;
    }

    public void setIDX(long IDX) {
        this.IDX = IDX;
    }

    public long getCageIDX() {
        return CageIDX;
    }

    public void setCageIDX(long cageIDX) {
        CageIDX = cageIDX;
    }

    public long getUserIDX() {
        return UserIDX;
    }

    public void setUserIDX(long userIDX) {
        UserIDX = userIDX;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getReference() {
        return Reference;
    }

    public void setReference(String reference) {
        Reference = reference;
    }

    public String getNotBefore() {
        return NotBefore;
    }

    public void setNotBefore(String notBefore) {
        NotBefore = notBefore;
    }

    public long getDriverIDX() {
        return DriverIDX;
    }

    public void setDriverIDX(long driverIDX) {
        DriverIDX = driverIDX;
    }

    public int getFlag() {
        return Flag;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }

    public String getJob_Ref() {
        return Job_Ref;
    }

    public void setJob_Ref(String job_Ref) {
        Job_Ref = job_Ref;
    }
}
