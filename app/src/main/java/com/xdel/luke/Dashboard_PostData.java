package com.xdel.luke;

/**
 * Created by kean.ong on 09/06/2015.
 */
public class Dashboard_PostData {

    public String postTitle;
    public String postCount;
    public boolean changeCountColorBool;
    public boolean showCountLbl;
    public boolean isDIP;
    public int ReqNetsCount;
    public int ReqVisaCount;
    public int ReqTiCount;
    public int ColdCount;
    public boolean IsHomesUser;
    public boolean isOKP;
    public boolean lite;

    public Dashboard_PostData(String postTitle, String postCount, boolean changeCountColorBool, boolean ashowCountLbl) {
        this.postTitle = postTitle;
        this.postCount = postCount;
        this.changeCountColorBool = changeCountColorBool;
        this.showCountLbl = ashowCountLbl;
        this.IsHomesUser = false;
        this.lite = false;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public void setReqNetsCount(int aReqNetsCount) {
        this.ReqNetsCount = aReqNetsCount;
    }

    public void setReqVisaCount(int aReqVisaCount) {
        this.ReqVisaCount = aReqVisaCount;
    }

    public void setReqTiCount(int aReqTiCount) {
        this.ReqTiCount = aReqTiCount;
    }

    public void setColdCount(int aColdCount) { this.ColdCount = aColdCount; }

    public void setDIP(boolean aisDIP) {
        this.isDIP = aisDIP;
    }

    public void setOKP(boolean aisOKP) { this.isOKP = aisOKP; }

    public void setIsHomesUser(boolean aIsHomesUser) {
        this.IsHomesUser = aIsHomesUser;
    }

    public void setLite(boolean lite) {
        this.lite = lite;
    }
}
