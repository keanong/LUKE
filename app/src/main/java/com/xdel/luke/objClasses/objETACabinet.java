package com.xdel.luke.objClasses;

import java.util.ArrayList;

public class objETACabinet {
    public String RegID;
    public objCabinet Cabinet;
    public ArrayList<objGC_CustomQueue> AQueue;

    public objETACabinet(String aRegID, objCabinet aCabinet, ArrayList<objGC_CustomQueue> aAQueue) {
        this.RegID = aRegID;
        this.Cabinet = aCabinet;
        this.AQueue = aAQueue;
    }

    public String getRegID() {
        return this.RegID;
    }

    public objCabinet getCabinet() {
        return this.Cabinet;
    }

    public ArrayList<objGC_CustomQueue> getAQueue() {
        return AQueue;
    }
}
