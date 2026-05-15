package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 01/06/2017.
 */
public class objDriverPointSummary {
    public long DriverIDX;
    public String Date_Of_Work;
    public String Attendance;
    public Integer Points;
    public Integer Half_Points;
    public int Actual_Jobs;
    public int Distance_Violations;
    public int Jobs_Failed_Early;
    public int Jobs_Failed_Late;
    public int Attend_Early;
    public int Attend_Late;
    public double Total_Distance;

    public void setTotal_Distance(double total_Distance) {
        Total_Distance = total_Distance;
    }
}
