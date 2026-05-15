package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 15/01/2018.
 */

public class objSettings {
    public Boolean DistVioEnabled;
    public Long DistVioExpectedMeters;
    public int GPSalarmInterval;
    public int GPSsendingIntervalCount;

    public objSettings () {

    }
    public objSettings(Boolean aDistVioEnabled, Long aDistVioExpectedMeters, int aGPSalarmInterval, int aGPSsendingIntervalCount){
        this.DistVioEnabled = aDistVioEnabled;
        this.DistVioExpectedMeters = aDistVioExpectedMeters;
        this.GPSalarmInterval = aGPSalarmInterval;
        this.GPSsendingIntervalCount = aGPSsendingIntervalCount;
    }

    public void setDistVioEnabled(Boolean aDistVioEnabled) {
        this.DistVioEnabled = aDistVioEnabled;
    }
    public void setDistVioExpectedMeters(Long aDistVioExpectedMeters) {
        this.DistVioExpectedMeters = aDistVioExpectedMeters;
    }
    public void setGPSalarmInterval(int aGPSalarmInterval) {
        this.GPSalarmInterval = aGPSalarmInterval;
    }
    public void setGPSsendingIntervalCount(int aGPSsendingIntervalCount) {
        this.GPSsendingIntervalCount = aGPSsendingIntervalCount;
    }
}
