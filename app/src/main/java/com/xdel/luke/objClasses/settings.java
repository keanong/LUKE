package com.xdel.luke.objClasses;

import com.xdel.luke.HRM_Leave.objLeaveBlock;

import java.util.ArrayList;

public class settings {

    public String settingsPWD = "XDel1550";
    public int GpsSendingInterval = 10000;
    public int GpsSendingIntervalCount = 5;
    public boolean SignatureIA = false;
    public boolean IsExpressGC = false;
    public boolean FollowVRP = false;
    public boolean UsesLiteVersion = false;
    public boolean UsesPointSys = false;
    //public ArrayList<objLeaveBlock> LeaveBlock = new ArrayList<objLeaveBlock>();
    public int PromiseInterval = 15;
    public int NumInterval = 4;

    public boolean getUsesLiteVersion() {
        return UsesLiteVersion;
    }
    public void setUsesLiteVersion(boolean usesLiteVersion) {
        UsesLiteVersion = usesLiteVersion;
    }

    public boolean getUsesPointSys() {
        return UsesPointSys;
    }
    public void setUsesPointSys(boolean usesPointSys) {
        UsesPointSys = usesPointSys;
    }

    public String getSettingsPWD(){
        return this.settingsPWD;
    }
    public void setSettingsPWD(String asettingsPWD) {
        this.settingsPWD = asettingsPWD;
    }

    public int getGpsSendingInterval() {
        return this.GpsSendingInterval;
    }
    public void setGpsSendingInterval(int aGpsSendingInterval) {
        this.GpsSendingInterval = aGpsSendingInterval;
    }

    public int getGpsSendingIntervalCount() {
        return this.getGpsSendingIntervalCount();
    }
    public void setGpsSendingIntervalCount(int aGpsSendingIntervalCount) {
        this.GpsSendingIntervalCount = aGpsSendingIntervalCount;
    }

    public boolean getSignatureIA() {
        return this.SignatureIA;
    }
    public void setSignatureIA(boolean aSignatureIA) {
        this.SignatureIA = aSignatureIA;
    }

    public boolean getIsExpressGC(){
        return this.IsExpressGC;
    }
    public void setIsExpressGC(boolean aIsExpressGC){
        this.IsExpressGC = aIsExpressGC;
    }

    public boolean getFollowVRP(){
        return this.FollowVRP;
    }
    public void setFollowVRP(boolean aFollowVRP) {
        this.FollowVRP = aFollowVRP;
    }

    public int getPromiseInterval() {
        return PromiseInterval;
    }
    public void setPromiseInterval(int promiseInterval) {
        PromiseInterval = promiseInterval;
    }

    public int getNumInterval() {
        return NumInterval;
    }
    public void setNumInterval(int numInterval) {
        NumInterval = numInterval;
    }
}
