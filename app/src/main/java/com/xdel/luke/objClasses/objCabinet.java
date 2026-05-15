package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 13/11/2017.
 */

public class objCabinet {

    public long IDX;
    public long DriverIDX;
    public String Cabinet_ID;
    public String Data;
    public String Last_Updated;

    public objCabinet(long aIDX, long aDriverIDX, String aCabinet_ID, String aData, String aLast_Updated){
        this.IDX = aIDX;
        this.DriverIDX = aDriverIDX;
        this.Cabinet_ID = aCabinet_ID;
        this.Data = aData;
        this.Last_Updated = aLast_Updated;
    }
}
