package com.xdel.luke.jobsqueue;

public class JobQueueLite {
    public String Postal;
    public String AddressWithPostalNoUnit;
    public int JCOUNT;
    public String JobsIDXs;
    public Double Latitude;
    public Double Longitude;

    public JobQueueLite(){}

    public void setPostal(String postal) {
        Postal = postal;
    }

    public void setAddressWithPostalNoUnit(String addressWithPostalNoUnit) {
        AddressWithPostalNoUnit = addressWithPostalNoUnit;
    }

    public void setJobsIDXs(String jobsIDXs) {
        JobsIDXs = jobsIDXs;
    }

    public void setJCOUNT(int JCOUNT) {
        this.JCOUNT = JCOUNT;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }
}
