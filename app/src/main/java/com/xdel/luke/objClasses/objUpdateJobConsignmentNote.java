package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 29/06/2015.
 */
public class objUpdateJobConsignmentNote extends objUpdates{
    public String RegID;
    public long JobsIDX;
    public String ConsignmentNote;
    public double Lat;
    public double Lng;

    public objUpdateJobConsignmentNote(String ARedID, long AJobsIDX, String AConsignment, double ALat, double ALng){
        this.RegID = ARedID;
        this.JobsIDX =AJobsIDX;
        this.ConsignmentNote = AConsignment;
        this.Lat = ALat;
        this.Lng = ALng;
    }
}
