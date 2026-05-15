package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 21/10/2015.
 */
public class objSearchedResult {
    public int count;
    public int catType;
    public String ActivityName;
    public String hfJobInfoJsonStr;
    public String hfJobsInfoArrJsonStr;
    public objJobInfo JobInfo;

    public objSearchedResult(int acount, int acatType,String aActivityName, String ahfJobInfoJsonStr, String ahfJobsInfoArrJsonStr, objJobInfo aJobInfo){
        this.count = acount;
        this.catType = acatType;
        this.ActivityName = aActivityName;
        this.hfJobInfoJsonStr = ahfJobInfoJsonStr;
        this.hfJobsInfoArrJsonStr = ahfJobsInfoArrJsonStr;
        this.JobInfo = aJobInfo;
    }
}
