package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 01/06/2017.
 */
public class DriverPointSummary_PostData {
    String DateString;
    Integer Points;
    Integer Half_Points;
    Integer Actual_Jobs;
    public int AlternateColor;
    String Attendance;
    int UpdateEarlyCount;
    int UpdateLateCount;
    double Total_Distance;
    public String Today_ddMMyyyy;

    public DriverPointSummary_PostData(String aDateString, Integer aPoints, Integer aHalf_Points, Integer aActual_Jobs, int aAlternateColor, String aAttendance, int aUpdateEarlyCount, int aUpdateLateCount, double aTotal_Distance){
        this.DateString = aDateString;
        this.Points = aPoints;
        this.Half_Points = aHalf_Points;
        this.Actual_Jobs = aActual_Jobs;
        this.AlternateColor = aAlternateColor;
        this.Attendance = aAttendance;
        this.UpdateEarlyCount = aUpdateEarlyCount;
        this.UpdateLateCount = aUpdateLateCount;
        this.Total_Distance = aTotal_Distance;
    }

    public void setToday_ddMMyyyy(String today_ddMMyyyy) {
        Today_ddMMyyyy = today_ddMMyyyy;
    }

    public String getDateString(){
        return this.DateString;
    }

    public void setUpdateEarlyCount(int updateEarlyCount) {
        UpdateEarlyCount = updateEarlyCount;
    }

    public void setUpdateLateCount(int updateLateCount) {
        UpdateLateCount = updateLateCount;
    }
}
