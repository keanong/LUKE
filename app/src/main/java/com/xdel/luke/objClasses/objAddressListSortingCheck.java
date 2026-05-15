package com.xdel.luke.objClasses;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by kean.ong on 24/02/2017.
 */
public class objAddressListSortingCheck  implements Comparable<objAddressListSortingCheck>{

    public Long timeval;
    public String postal;
    public String fulladdress;
    //public String fulladdressnospaces;
    public String fulladdressnopostal;
    public double lat;
    public double lng;
    public ArrayList<objJobInfo> jiAL;
    public Boolean skip;
    public Boolean DNF;
    public Boolean OTP;
    public Boolean altDNF = false;
    public Boolean altOTP = false;
    public Boolean altSign = false;
    public String timingStr;
    public int ExpType;
    public Boolean setLateColor = false;
    public Boolean setBoutLateColor = false;
    public Boolean addressHasBlock = false;
    public Date delfromdate;
    public Date pubydate;
    public Date rdydate;
    public Date delbydate;
    public Date extdate;

    public objAddressListSortingCheck (Long atimeval, String apostal, String afulladdress, String afulladdressnopostal, double alat, double alng, Boolean askip,
                                       Boolean aDNF, Boolean aOTP, String atimingStr,
                                       int aExpType, boolean asetLateColor, boolean asetBoutLateColor, Boolean aaddressHasBlock,
                                       Date ardydate, Date apubydate, Date adelfromdate, Date adelbydate, Date aextdate) {
        this.timeval = atimeval;
        this.postal = apostal;
        this.fulladdress = afulladdress;
        this.fulladdressnopostal = afulladdressnopostal;
        this.lat = alat;
        this.lng = alng;
        this.skip = askip;
        this.DNF = aDNF;
        this.OTP = aOTP;
        this.timingStr = atimingStr;
        this.ExpType = aExpType;
        this.setLateColor = asetLateColor;
        this.setBoutLateColor = asetBoutLateColor;
        this.addressHasBlock = aaddressHasBlock;
        this.rdydate = ardydate;
        this.pubydate = apubydate;
        this.delfromdate = adelfromdate;
        this.delbydate = adelbydate;
        this.extdate = aextdate;

    }

    public int compareTo(objAddressListSortingCheck other){
        return timeval.compareTo(other.timeval);
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

    public void setFulladdressnopostal(String afulladdressnopostal) {
        this.fulladdressnopostal = afulladdressnopostal;
    }

    public String getFulladdress(){
        return this.fulladdress;
    }

    public void setFullAddress(String afulladdress){
        this.fulladdress = afulladdress;
    }

    public Boolean getaddressHasBlock(){
        return this. addressHasBlock;
    }

    public void setaddressHasBlock(Boolean aaddressHasBlock) {
        this.addressHasBlock = aaddressHasBlock;
    }

    public Boolean getsetBoutLateColor(){
        return this.setBoutLateColor;
    }

    public void setsetBoutLateColor(Boolean asetBoutLateColor) {
        this.setBoutLateColor = asetBoutLateColor;
    }

    public Boolean getsetLateColor(){
        return this.setLateColor;
    }

    public void setsetLateColor(Boolean asetLateColor) {
        this.setLateColor = setLateColor;
    }

    public int getExpType(){
        return ExpType;
    }

    public String gettimingStr(){
        return this.timingStr;
    }

    public void settimingStr(String atimingStr) {
        this.timingStr = atimingStr;
    }

    public Boolean getSkip(){
        return this.skip;
    }

    public Boolean getDNF(){
        return this.DNF;
    }

    public Boolean getOTP(){
        return this.OTP;
    }

    public Boolean getaltSign(){return this.altSign;}

    public void setaltSign(Boolean aaltSign) {this.altSign = aaltSign;}

    public Boolean getaltDNF(){
        return this.altDNF;
    }

    public void setaltDNF(Boolean aaltDNF) {
        this.altDNF = aaltDNF;
    }

    public Boolean getaltOTP(){
        return this.altOTP;
    }

    public void setaltOTP(Boolean aaltOTP){
        this.altOTP = aaltOTP;
    }

    public ArrayList<objJobInfo> getJobInfoAL(){
        return this.jiAL;
    }

    public void addJobInfoIntoJobInfoAL(objJobInfo ji){
        if (this.jiAL != null) {
            this.jiAL.add(ji);
        } else {
            this.jiAL = new ArrayList<>();
            this.jiAL.add(ji);
        }
    }


}
