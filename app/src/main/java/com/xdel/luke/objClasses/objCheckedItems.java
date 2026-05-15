package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 17/09/2015.
 */
public class objCheckedItems {
    public int categoryType;
    public String JobsIDX;
    public String CNNO;
    public long StatusCodeIDX;
    public String JobInfoJsonStr;
    public Boolean isPartOne;
    public objJobInfo jobinfo;
    public boolean allowedSelection;
    public int pcs;

    public objCheckedItems(int acategoryType, String aJobsIDX, String aCNNO, long aStatusCodeIDX, String aJobInfoJsonStr, Boolean aisPartOne, objJobInfo ajobinfo, boolean aallowedSelection){
        this.categoryType = acategoryType;
        this.JobsIDX = aJobsIDX;
        this.CNNO = aCNNO;
        this.StatusCodeIDX = aStatusCodeIDX;
        this.JobInfoJsonStr = aJobInfoJsonStr;
        this.isPartOne = aisPartOne;
        this.jobinfo = ajobinfo;
        this.allowedSelection = aallowedSelection;
    }

    public void setJobinfo(objJobInfo jobinfo) {
        this.jobinfo = jobinfo;
    }

    public void setPcs(int pcs) {
        this.pcs = pcs;
    }
}
