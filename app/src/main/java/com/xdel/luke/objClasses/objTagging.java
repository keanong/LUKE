package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 25/04/2016.
 */
public class objTagging {

    public String ARegID;
    public long AJobsIDX;
    public String[] ATagArray;
    public String AUTC;
    public double Lat;
    public double Lng;

    public objTagging(String aARegID, long aAJobsIDX, String[] aATagArray, String aAUTC, double aLat, double aLng){
        this.ARegID = aARegID;
        this.AJobsIDX = aAJobsIDX;
        this.ATagArray = aATagArray;
        this.AUTC = aAUTC;
        this.Lat = aLat;
        this.Lng = aLng;
    }

}
