package com.xdel.luke.objClasses;

import java.util.Date;

/**
 * Created by kean.ong on 27/07/2016.
 */
public class objCheckJobTagResult {

    public Long JobsIDX;
    public String ScannedAt;
    public String BarCode;
    public int Status;

    public Long getJobsIDX(){
        return  this.JobsIDX;
    }

    public void setJobsIDX(Long aJobsIDX){
        this.JobsIDX = aJobsIDX;
    }

    public String getScannedAt(){
        return this.ScannedAt;
    }

    public void setScannedAt(String aScannedAt){
        this.ScannedAt = aScannedAt;
    }

    public String getBarCode(){
        return this.BarCode;
    }

    public void setBarCode(String aBarCode){
        this.BarCode = aBarCode;
    }

    public int getStatus(){
        return this.Status;
    }

    public void setStatus(int aStatus){
        this.Status = aStatus;
    }


}
