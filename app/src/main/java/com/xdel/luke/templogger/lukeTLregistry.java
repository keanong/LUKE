package com.xdel.luke.templogger;

public class lukeTLregistry {
    /*public long IDX;
    public String desc;
    public int registered; //0 not reg, 1 reg
    public String regtimestamp;
    public double temp;*/

    public long MPDIDX;
    public String desc;
    public String Mac;
    public int registered; //0 not reg, 1 reg
    public long regtimestamplong;
    public String regtimestamp;
    public double temp;


    public lukeTLregistry(){

    }

    public lukeTLregistry(long aMPDIDX, String aMac, String adesc) {
        this.MPDIDX = aMPDIDX;
        this.Mac = aMac;
        this.desc = adesc;
    }

    public void setMPDIDX(long aMPDIDX) {
        this.MPDIDX = aMPDIDX;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    public void setRegtimestamplong(long regtimestamplong) {
        this.regtimestamplong = regtimestamplong;
    }

    public void setRegtimestamp(String regtimestamp) {
        this.regtimestamp = regtimestamp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }
}
