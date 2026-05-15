package com.xdel.luke.objClasses;

public class objSendReviewLink {
    public String RegID;
    public Long JobsIDX;

    public objSendReviewLink(String aRegID, Long aJobsIDX){
        this.RegID = aRegID;
        this.JobsIDX = aJobsIDX;
    }

    public String getRegID() {
        return RegID;
    }
    public void setRegID(String regID) {
        RegID = regID;
    }

    public Long getJobsIDX() {
        return JobsIDX;
    }

    public void setJobsIDX(Long jobsIDX) {
        JobsIDX = jobsIDX;
    }
}
