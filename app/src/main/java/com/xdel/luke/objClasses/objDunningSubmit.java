package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 02/09/2016.
 */
public class objDunningSubmit {
    public String ARegID;
    public objADunningStatus ADunningStatus;
    public String Delivery_StatusEnVal;
    public String Type_of_DwellingEngVal;
    public double Lat;
    public double Lng;
    public int Accuracy;

    public objDunningSubmit(String aARegID, objADunningStatus aADunningStatus, String aDelivery_StatusEnVal, String aType_of_DwellingEngVal, double aLat, double aLng, int aAccuracy){
        this.ARegID = aARegID;
        this.ADunningStatus = aADunningStatus;
        this.Delivery_StatusEnVal = aDelivery_StatusEnVal;
        this.Type_of_DwellingEngVal = aType_of_DwellingEngVal;
        this.Lat = aLat;
        this.Lng = aLng;
        this.Accuracy = aAccuracy;
    }

    public String getARegID() {
        return this.ARegID;
    }

    public void setARegID(String aARegID) {
        this.ARegID = aARegID;
    }

    public objADunningStatus getADunningStatus() {
        return this.ADunningStatus;
    }

    public void setADunningStatus(objADunningStatus aADunningStatus) {
        this.ADunningStatus = aADunningStatus;
    }

    public double getLat() {
        return this.Lat;
    }

    public void setLat(double aLat) {
        this.Lat = aLat;
    }

    public double getLng() {
        return this.Lng;
    }

    public void setLng(double aLng) {
        this.Lng = aLng;
    }

    public int getAccuracy(){
        return this.Accuracy;
    }

    public void setAccuracy(int aAccuracy) {
        this.Accuracy = aAccuracy;
    }
}
