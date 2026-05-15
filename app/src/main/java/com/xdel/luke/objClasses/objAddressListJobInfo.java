package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 26/06/2015.
 */
public class objAddressListJobInfo implements Comparable<objAddressListJobInfo>{

    public String postal;
    public String title;
    public int count;
    public String JobsInfoArrJsonStr;
    public String companyName;
    public String addressWithoutPostal;
    public String titleNfullAddress;
    public Boolean DNF;
    public Double LAT;
    public Double LNG;
    public String Building;
    public Boolean OTP;

    /*public int compareTo(objAddressListJobInfo other){
        return postal.compareTo(other.postal);
    }*/

    public int compareTo(objAddressListJobInfo other){
        return titleNfullAddress.compareTo(other.titleNfullAddress);
    }
}
