package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 21/10/2015.
 */
public class objBulkPickUp {
    public String RegID;
    public String[] JSParr;
    public String AUTC;
    public double Lat;
    public double Lng;

    public objBulkPickUp(String aRegID, String[] aJSParr, String aAUTC, double aLat, double aLng){
        this.RegID = aRegID;
        this.JSParr = aJSParr;
        this.AUTC = aAUTC;
        this.Lat = aLat;
        this.Lng = aLng;
    }
}
