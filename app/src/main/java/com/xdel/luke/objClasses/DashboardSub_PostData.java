package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 07/07/2016.
 */
public class DashboardSub_PostData {

    public String postTitle;
    public String postCount;
    public String JobsInfoArrJsonStr;
    public Boolean changeCountColorBool;
    public boolean isDIP;
    public int ReqNetsCount;
    public int ReqVisaCount;
    public int ReqTiCount;
    public int ColdCount;

    public DashboardSub_PostData(String postTitle, String postCount, String aJobsInfoArrJsonStr, Boolean achangeCountColorBool) {
        this.postTitle = postTitle;
        this.postCount = postCount;
        this.JobsInfoArrJsonStr = aJobsInfoArrJsonStr;
        this.changeCountColorBool = achangeCountColorBool;
    }
    public void setReqNetsCount(int aReqNetsCount){
        this.ReqNetsCount = aReqNetsCount;
    }
    public void setReqVisaCount(int aReqVisaCount){
        this.ReqVisaCount = aReqVisaCount;
    }
    public void setReqTiCount(int aReqTiCount){
        this.ReqTiCount = aReqTiCount;
    }
    public void setColdCount(int aColdCount) {  this.ColdCount = aColdCount; }
    public void setDIP(boolean aisDIP){
        this.isDIP = aisDIP;
    }
}
