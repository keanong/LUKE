package com.xdel.luke.HRM_GC_Schedule;

public class objGCSchedule {
    public String Date;
    public String FromTime;
    public String ToTime;
    public String Remarks;

    public objGCSchedule() {

    }

    public objGCSchedule(String aDate, String aFromTime, String aToTime, String aRemarks) {
        this.Date = aDate;
        this.FromTime = aFromTime;
        this.ToTime = aToTime;
        this.Remarks = aRemarks;
    }

}
