package com.xdel.luke.objClasses;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kean.ong on 24/02/2017.
 */
public class AddressListNew_PostData {

    public String Address;
    public String postal;
    public String Count;
    public ArrayList<objJobInfo> JobInfoAL;
    public String JobsInfoArrJsonStr;
    public boolean changeCountColorBool;
    public Boolean DNF;
    public Double LAT;
    public Double LNG;
    public String Building;
    public Boolean OTP;
    public Boolean altDNF;
    public Boolean altOTP;
    public Boolean altSign;
    public String timingStr;
    public int altExpType;
    public Long timeval;
    public Boolean setlateColor;
    public Boolean setBoutlateColor;
    public Date delfromdate;
    public Date pubydate;
    public Date rdydate;
    public Date delbydate;
    public Integer HasP2;

    public AddressListNew_PostData(String aAddress, String apostal, String aCount, ArrayList<objJobInfo> aJobInfoAL, String aJobsInfoArrJsonStr, Boolean aDNF, Boolean aaltDNF,
                                   Boolean aOTP, Boolean aaltOTP, Boolean aaltSign, String atimingStr, int aaltExpType, Long atimeval, Boolean asetlateColor, Boolean asetBoutlateColor, Boolean achangeCountColorBool,
                                   Double aLAT, Double aLNG, Date ardydate, Date apubydate, Date adelfromdate, Date adelbydate, Integer aHasP2) {
        this.Address = aAddress;
        this.postal = apostal;
        this.Count = aCount;
        this.JobInfoAL = aJobInfoAL;
        this.JobsInfoArrJsonStr = aJobsInfoArrJsonStr;
        this.DNF = aDNF;
        this.altDNF = aaltDNF;
        this.OTP = aOTP;
        this.altOTP = aaltOTP;
        this.altSign = aaltSign;
        this.timingStr = atimingStr;
        this.altExpType = aaltExpType;
        this.timeval = atimeval;
        this.setlateColor = asetlateColor;
        this.setBoutlateColor = asetBoutlateColor;
        this.changeCountColorBool = achangeCountColorBool;
        this.LAT = aLAT;
        this.LNG = aLNG;
        this.rdydate = ardydate;
        this.pubydate = apubydate;
        this.delfromdate = adelfromdate;
        this.delbydate = adelbydate;
        this.HasP2 = aHasP2;
    }

    public void setchangeCountColorBool(Boolean achangeCountColorBool) {
        this.changeCountColorBool = achangeCountColorBool;
    }

    public Date getrdydate(){
        return this.rdydate;
    }

    public Date getpubydate(){
        return pubydate;
    }

    public Date getdelfromdate(){
        return this.delfromdate;
    }

    public Date getdelbydate(){
        return this.delbydate;
    }
}
