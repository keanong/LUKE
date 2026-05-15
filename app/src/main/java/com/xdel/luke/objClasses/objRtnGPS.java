package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 20/07/2015.
 */
public class objRtnGPS {

    public String UTC;
    public double Lat;
    public double Lng;
    public int Accuracy;

    public objRtnGPS(String adatetime, double aLat, double aLng, int aAccuracy){
        this.UTC = adatetime;
        this.Lat = aLat;
        this.Lng = aLng;
        this.Accuracy = aAccuracy;
    }
}
