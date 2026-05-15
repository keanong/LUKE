package com.xdel.luke.HandTakeOver;

import java.util.ArrayList;

public class hotoqr {
    public String HODriverIDX;
    public ArrayList<String> scannedItems;
    public String refHOHash;

    public hotoqr(String aHODriverIDX, ArrayList<String> ascannedItems, String arefHOHash){
        this.HODriverIDX = aHODriverIDX;
        this.scannedItems = ascannedItems;
        this.refHOHash = arefHOHash;
    }
}
