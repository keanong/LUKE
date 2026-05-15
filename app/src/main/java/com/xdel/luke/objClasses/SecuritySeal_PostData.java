package com.xdel.luke.objClasses;

public class SecuritySeal_PostData {

    public String EnteredValue;
    public boolean Verified;

    public SecuritySeal_PostData(String aEnteredValue){
        this.EnteredValue = aEnteredValue;
    }

    public void setEnteredValue(String aEnteredValue) {
        this.EnteredValue =  aEnteredValue;
    }

    public void setVerified(boolean aVerified) {
        this.Verified = aVerified;
    }

}
