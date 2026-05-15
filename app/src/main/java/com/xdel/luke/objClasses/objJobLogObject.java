package com.xdel.luke.objClasses;

import java.util.Date;

/**
 * Created by kean.ong on 11/06/2015.
 */
public class objJobLogObject {

    public long StatusCodeIDX;
    public Date TimeStamp;
    public String Remarks;

    public objJobLogObject(){
        StatusCodeIDX = 0;
        TimeStamp = new Date();
        Remarks = "";
    }

    public objJobLogObject(long aStatusCodeIDX,Date aTimeStamp,String aRemarks){
        StatusCodeIDX = aStatusCodeIDX;
        TimeStamp = aTimeStamp;
        Remarks = aRemarks;
    }

    public long getStatusCodeIDX(){
        return this.StatusCodeIDX;
    }

    public void setStatusCodeIDX(long aStatusCodeIDX){
        this.StatusCodeIDX = aStatusCodeIDX;
    }

    public Date getTimeStamp(){
        return this.TimeStamp;
    }

    public void setTimeStamp(Date aTimeStamp){
        this.TimeStamp = aTimeStamp;
    }

    public String getRemarks(){
        return this.Remarks;
    }

    public void setRemarks(String aRemarks){
        this.Remarks = aRemarks;
    }

}
