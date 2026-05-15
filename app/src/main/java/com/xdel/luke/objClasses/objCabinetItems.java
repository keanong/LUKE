package com.xdel.luke.objClasses;

import java.io.Serializable;

/**
 * Created by kean.ong on 13/11/2017.
 */

public class objCabinetItems implements Serializable {
    public long ID;
    public long JOBSIDX;
    public long PULID;
    public long OKPLID;
    public String BagID;

    public objCabinetItems(long aID, long AJOBSIDX, long aPULID, long aOKPLID, String aBagID){
        this.ID = aID;
        this.JOBSIDX = AJOBSIDX;
        this.PULID = aPULID;
        this.OKPLID = aOKPLID;
        this.BagID = aBagID;
    }
}
