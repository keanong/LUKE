package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 15/06/2015.
 */
public class AddressList_PostData {

    public String postTitle;
    public String postCount;
    public String JobsInfoArrJsonStr;
    public boolean changeCountColorBool;
    public String Postal;
    public String companyName;
    public String addressWithoutPostal;
    public Boolean DNF;
    public Double LAT;
    public Double LNG;
    public String Building;
    public Boolean OTP;


    public AddressList_PostData(String postTitle, String postCount, String aJobsInfoArrJsonStr, boolean changeCountColorBool, String Postal,
                                String companyName, String addressWithoutPostal, Boolean aDNF, Double aLAT, Double aLNG, String aBuilding, Boolean aOTP) {
        this.postTitle = postTitle;
        this.postCount = postCount;
        this.JobsInfoArrJsonStr = aJobsInfoArrJsonStr;
        this.changeCountColorBool = changeCountColorBool;
        this.Postal = Postal;
        this.companyName = companyName;
        this.addressWithoutPostal = addressWithoutPostal;
        this.DNF = aDNF;
        this.LAT = aLAT;
        this.LNG = aLNG;
        this.Building = aBuilding;
        this.OTP = aOTP;
    }
}
