package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 22/04/2016.
 */
public class objUpdateCOD {
    public String ARegID;
    public int AType;
    public long AIDX;
    public int CODType;
    public String AUTC;

    public objUpdateCOD(String aARegID, int aAType, long aAIDX, int aCODType, String aAUTC){
        this.ARegID = aARegID;
        this.AType = aAType;
        this.AIDX = aAIDX;
        this.CODType = aCODType;
        this.AUTC = aAUTC;
    }
}
