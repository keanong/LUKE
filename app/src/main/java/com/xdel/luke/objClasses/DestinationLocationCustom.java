package com.xdel.luke.objClasses;

public class DestinationLocationCustom {
    public double lat;
    public double lng;
    public int DestinationTolerance;

    public DestinationLocationCustom(){}

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setDestinationTolerance(int destinationTolerance) {
        DestinationTolerance = destinationTolerance;
    }
}
