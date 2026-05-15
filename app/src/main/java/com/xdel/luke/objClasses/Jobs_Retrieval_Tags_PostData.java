package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 16/10/2017.
 */

public class Jobs_Retrieval_Tags_PostData {
    public String tagID;
    public long JobsIDX;
    public long PULID;
    public long OKPLID;

    public Jobs_Retrieval_Tags_PostData(String atagID, long aJobsIDX, long aPULID, long aOKPLID) {
        this.tagID = atagID;
        this.JobsIDX = aJobsIDX;
        this.PULID = aPULID;
        this.OKPLID = aOKPLID;
    }
}
