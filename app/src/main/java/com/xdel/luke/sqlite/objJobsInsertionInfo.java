package com.xdel.luke.sqlite;

import com.xdel.luke.objClasses.objJobInfo;

public class objJobsInsertionInfo {
    public objJobInfo[] JobInfoAL;
    public int CatTypeID;

    public objJobsInsertionInfo(objJobInfo[] aJobInfoAL, int aCatTypeID){
        this.JobInfoAL = aJobInfoAL;
        this.CatTypeID = aCatTypeID;
    }

    public objJobInfo[] getJobInfoAL() {
        return JobInfoAL;
    }

    public int getCatTypeID() {
        return CatTypeID;
    }
}
