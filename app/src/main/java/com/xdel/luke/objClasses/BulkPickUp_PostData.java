package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 20/10/2015.
 */
public class BulkPickUp_PostData {

    public String Cnno;
    public boolean blink;

    public BulkPickUp_PostData(String aCnno){
        this.Cnno = aCnno;
    }

    public void setBlink(boolean blink) {
        this.blink = blink;
    }

}
