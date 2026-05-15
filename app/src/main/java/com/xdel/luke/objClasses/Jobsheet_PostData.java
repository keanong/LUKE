package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 17/08/2015.
 */
public class Jobsheet_PostData {

    public String ScannedIDnUsername;
    public String postTitle;
    public String datetimeStr;
    public String combinedStr;
    public boolean blink;

    public Jobsheet_PostData(String ScannedIDnUsername, String apostTitle, String datetimeStr, String combinedStr){
        this.ScannedIDnUsername = ScannedIDnUsername;
        this.postTitle = apostTitle;
        this.datetimeStr = datetimeStr;
        this.combinedStr = combinedStr;
    }

    public void setBlink(boolean blink) {
        this.blink = blink;
    }

}
