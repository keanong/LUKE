package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 20/09/2017.
 */
public class objOtpJobsTries {
    public Integer Tries;
    public Long JobsIDX;
    public String PIN;
    public Integer LockedStatus;

    public objOtpJobsTries(Integer aTries, Long aJobsIDX, String aPIN, Integer aLockedStatus){
        this.Tries = aTries;
        this.JobsIDX = aJobsIDX;
        this.PIN = aPIN;
        this.LockedStatus = aLockedStatus;
    }

    public Integer getTries(){
        return this.Tries;
    }
    public void setTries(Integer aTries) {
        this.Tries = aTries;
    }

    public Long getJobsIDX(){
        return this.JobsIDX;
    }
    public void setJobsIDX(Long aJobsIDX) {
        this.JobsIDX = aJobsIDX;
    }

    public String getPIN(){
        return this.PIN;
    }
    public void setPIN(String aPIN) {
        this.PIN = aPIN;
    }

    public Integer getLockedStatus(){
        return this.LockedStatus;
    }
    public void setLockedStatus(Integer aLockedStatus) {
        this.LockedStatus = aLockedStatus;
    }

}
