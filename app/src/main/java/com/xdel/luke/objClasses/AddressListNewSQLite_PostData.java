package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 05/05/2017.
 */
public class AddressListNewSQLite_PostData {
    public String Address;
    public String Postal;
    public String Count;
    public Double LAT;
    public Double LNG;
    public Boolean SIGNATURE;
    public Boolean DNF;
    public Boolean OTP;
    public String TimingStr;
    public Boolean SetlateColor;
    public Boolean NewlyInsertedCT;
    public Boolean HasDOCOD;
    public Boolean HasTradeIn;
    public Boolean isDelivery = false;
    public Integer HasP2;
    public Integer isIntendedRecOnlyCount;
    public Integer isTotallyCannotSlot;
    public Integer canSlotLBCount;
    public Integer canSlotDoorCount;
    public Integer canGuardCount;
    public Integer isPickAndSendCount;
    public Integer JobChangesCount;
    public boolean isDIP;
    public int ReqNetsCount;
    public int ReqVisaCount;
    public int ReqTiCount;
    public int ColdCount;
    public Integer canDoorstepCount;
    public Integer contactlessCount;
    public boolean HASLUNCHTIME;

    //public Boolean setBoutlateColor;

    public AddressListNewSQLite_PostData(String aAddress, String apostal, String aCount, Double aLAT, Double aLNG, Boolean aSIGNATURE, Boolean aDNF,
                                         Boolean aOTP, String aTimingStr, Boolean aSetlateColor, Boolean aNewlyInsertedCT, Boolean aHasDOCOD, Boolean aHasTradeIn,
                                         Boolean aisDelivery, Integer aHasP2, Integer aisIntendedRecOnlyCount, Integer aisTotallyExplicitCount, Integer acanSlotLBCount,
                                         Integer acanSlotDoorCount, Integer acanGuardCount, Integer aisPickAndSendCount, Integer aJobChangesCount, Integer acanDoorstepCount, Integer acontactlessCount){
        this.Address = aAddress;
        this.Postal = apostal;
        this.Count = aCount;
        this.LAT = aLAT;
        this.LNG = aLNG;
        this.SIGNATURE = aSIGNATURE;
        this.DNF = aDNF;
        this.OTP = aOTP;
        this.TimingStr = aTimingStr;
        this.SetlateColor = aSetlateColor;
        this.NewlyInsertedCT = aNewlyInsertedCT;
        this.HasDOCOD = aHasDOCOD;
        this.HasTradeIn = aHasTradeIn;
        this.isDelivery = aisDelivery;
        this.HasP2 = aHasP2;
        this.isIntendedRecOnlyCount = aisIntendedRecOnlyCount;
        this.isTotallyCannotSlot = aisTotallyExplicitCount;
        this.canSlotLBCount = acanSlotLBCount;
        this.canSlotDoorCount = acanSlotDoorCount;
        this.canGuardCount = acanGuardCount;
        this.isPickAndSendCount = aisPickAndSendCount;
        this.JobChangesCount = aJobChangesCount;
        this.canDoorstepCount = acanDoorstepCount;
        this.contactlessCount = acontactlessCount;
    }
    public void setReqNetsCount(int aReqNetsCount){
        this.ReqNetsCount = aReqNetsCount;
    }
    public void setReqVisaCount(int aReqVisaCount){
        this.ReqVisaCount = aReqVisaCount;
    }
    public void setReqTiCount(int aReqTiCount){
        this.ReqTiCount = aReqTiCount;
    }
    public void setColdCount(int aColdCount) { this.ColdCount = aColdCount; }
    public void setDIP(boolean aisDIP){
        this.isDIP = aisDIP;
    }

    public void setHASLUNCHTIME(boolean HASLUNCHTIME) {
        if (!this.HASLUNCHTIME) {
            this.HASLUNCHTIME = HASLUNCHTIME;
        }
    }


}
