package com.xdel.luke.HRM_GC_Schedule;

public class GCschedule_PostData {
    String DateString;
    String Remarks;
    String From;
    String To;
    String FromTo;
    boolean isToday;

    public GCschedule_PostData(String aDateString, String aRemarks, String aFrom, String aTo, String aFromTo, boolean aisToday) {
        this.DateString = aDateString;
        this.Remarks = aRemarks;
        this.From = aFrom;
        this.To = aTo;
        this.FromTo = aFromTo;
        this.isToday = aisToday;
    }

    public void setFromTo(String fromTo) {
        FromTo = fromTo;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public void setFrom(String from) {
        From = from;
    }

    public void setTo(String to) {
        To = to;
    }
}
