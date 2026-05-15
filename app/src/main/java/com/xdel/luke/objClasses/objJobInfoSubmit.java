package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 20/05/2016.
 */
public class objJobInfoSubmit {
    public long idx;
    public long StatusCodeIDX;
    public objJobInfo currJobInfo;

    public objJobInfoSubmit(long aidx, long aStatusCodeIDX, objJobInfo acurrJobInfo){
        this.idx = aidx;
        this.StatusCodeIDX = aStatusCodeIDX;
        this.currJobInfo = acurrJobInfo;
    }
}
