package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 05/07/2016.
 */
public class Location {

    public String postal;
    public String id;
    public String lat;
    public String lon;
    public String name;
    public String full_address;
    public String jobsidxs;

    public Location() {

    }

    public Location(String apostal, String alat, String alon) {
        this.postal = apostal;
        this.lat = alat;
        this.lon = alon;
    }


    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
