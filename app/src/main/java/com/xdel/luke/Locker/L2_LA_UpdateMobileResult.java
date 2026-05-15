package com.xdel.luke.Locker;

import org.apache.http.message.BasicStatusLine;

public class L2_LA_UpdateMobileResult {
    public String Status;
    public String User_ID;
    public boolean Allow_Retrieve;
    public String User_Key;
    public String User_Mobile;
    public String PIN_Expiry;
    public String PIN_Portal_URL;

    public L2_LA_UpdateMobileResult(String aStatus, String aUser_ID, boolean aAllow_Retrieve, String aUser_Key, String aUser_Mobile, String aPIN_Expiry, String aPIN_Portal_URL){
        this.Status = aStatus;
        this.User_ID = aUser_ID;
        this.Allow_Retrieve = aAllow_Retrieve;
        this.User_Key = aUser_Key;
        this.User_Mobile = aUser_Mobile;
        this.PIN_Expiry = aPIN_Expiry;
        this.PIN_Portal_URL = aPIN_Portal_URL;
    }

}
