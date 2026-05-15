package com.xdel.luke.sqlite;

public class dbObjSimpleJobInfo {
    public String hfJobInfoJsonStr;
    public double Lat;
    public double Lng;
    public int catType;
    public String postal;
    public String address;
    public String addressWithUnit;

    public dbObjSimpleJobInfo(String ahfJobInfoJsonStr, double aLat, double aLng, int acatType, String apostal, String aaddress, String aaddressWithUnit) {
        this.hfJobInfoJsonStr = ahfJobInfoJsonStr;
        this.Lat = aLat;
        this.Lng = aLng;
        this.catType = acatType;
        this.postal = apostal;
        this.address = aaddress;
        this.addressWithUnit = aaddressWithUnit;
    }
}
