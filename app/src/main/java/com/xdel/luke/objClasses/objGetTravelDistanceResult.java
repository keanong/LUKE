package com.xdel.luke.objClasses;

public class objGetTravelDistanceResult {
    public int Status;
    public String Message;
    public objDistance_Info[] Distance;

    public objGetTravelDistanceResult(int aStatus, String aMessage, objDistance_Info[] aDistance) {
        this.Status = aStatus;
        this.Message = aMessage;
        this.Distance = aDistance;
    }
}
