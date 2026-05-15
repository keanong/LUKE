package com.xdel.luke.objClasses;

public class objNumOfCallOutPast15min {
    public int reqCallLogImg;
    public int CallConnected;
    public int NumOfCallOutPast15min;

    public objNumOfCallOutPast15min(int areqCallLogImg, int aCallConnected, int aNumOfCallOutPast15min){
        this.reqCallLogImg = areqCallLogImg;
        this.CallConnected = aCallConnected;
        this.NumOfCallOutPast15min = aNumOfCallOutPast15min;
    }

    public int getNumOfCallOutPast15min() {
        return NumOfCallOutPast15min;
    }

    public void setNumOfCallOutPast15min(int numOfCallOutPast15min) {
        NumOfCallOutPast15min = numOfCallOutPast15min;
    }

    public int getReqCallLogImg() {
        return reqCallLogImg;
    }

    public void setReqCallLogImg(int reqCallLogImg) {
        this.reqCallLogImg = reqCallLogImg;
    }

    public int getCallConnected() {
        return CallConnected;
    }

    public void setCallConnected(int callConnected) {
        CallConnected = callConnected;
    }
}
