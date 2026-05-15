package com.xdel.luke.sqlite;

public class objRouteJobEarliestTiming {
    public long StatusCodeIDX;
    public String ExpectedTime;
    public String ReadyDateTime;
    public String PickByDateTime;
    public String FromDateTime;
    public String ToDateTime;
    public String ExtDateTime;
    public String ExtFromDateTime;
    public boolean isLate;

    public objRouteJobEarliestTiming(){

    }

    public void setStatusCodeIDX(long statusCodeIDX) {
        StatusCodeIDX = statusCodeIDX;
    }

    public void setExpectedTime(String expectedTime) {
        ExpectedTime = expectedTime;
    }

    public void setReadyDateTime(String readyDateTime) {
        ReadyDateTime = readyDateTime;
    }

    public void setPickByDateTime(String pickByDateTime) {
        PickByDateTime = pickByDateTime;
    }

    public void setFromDateTime(String fromDateTime) {
        FromDateTime = fromDateTime;
    }

    public void setToDateTime(String toDateTime) {
        ToDateTime = toDateTime;
    }

    public void setExtDateTime(String extDateTime) {
        ExtDateTime = extDateTime;
    }

    public void setExtFromDateTime(String extFromDateTime) {
        ExtFromDateTime = extFromDateTime;
    }

    public void setLate(boolean late) {
        isLate = late;
    }
}
