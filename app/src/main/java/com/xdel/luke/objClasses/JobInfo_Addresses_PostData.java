package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 10/02/2017.
 */
public class JobInfo_Addresses_PostData {

    public String CompanyName;
    public String Address;
    public String ContactName;
    public String DeptName;
    public String Tel;
    public String Other;
    public String Mobile;
    public String OnHalf;

    public JobInfo_Addresses_PostData (String aCompanyName, String aAddress, String aContactName, String aDeptName, String aTel, String aOther, String aMobile, String aOnHalf) {
        this.CompanyName = aCompanyName;
        this.Address = aAddress;
        this.ContactName = aContactName;
        this.DeptName = aDeptName;
        this.Tel = aTel;
        this.Other = aOther;
        this.Mobile = aMobile;
        this.OnHalf = aOnHalf;
    }
}
