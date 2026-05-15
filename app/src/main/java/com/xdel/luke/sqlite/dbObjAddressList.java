package com.xdel.luke.sqlite;

/**
 * Created by kean.ong on 04/05/2017.
 */
public class dbObjAddressList {
    public String Address;
    public String Postal;
    public Double LAT;
    public Double LNG;
    public Long FromDateTime;
    public String FromDateTimeConverted;
    public String FromText;
    public Long ToDateTime;
    public String ToDateTimeConverted;
    public Long ExtDateTime;
    public String ExtDateTimeConverted;
    public String ToText;
    public String TimingText;
    public Integer JOBCOUNT;
    public Boolean SignaureFlag;
    public Boolean DNFFlag;
    public Boolean OTPFlag;
    public Boolean IsLate;
    public Boolean HasNewlyInserted;
    public String JobInfoJsonStr;
    public Boolean HasDOCOD;
    public Boolean HasTradeIn;
    public Integer FLAG;
    public Integer HasP2;
    public Integer isIntendedRecOnlyCount;
    public Integer isTotallyExplicitCount;
    public Integer canSlotLBCount;
    public Integer canSlotDoorCount;
    public Integer canGuardCount;
    public boolean isDIP;
    public int ReqNetsCount;
    public int ReqVisaCount;
    public int ReqTiCount;
    public int ColdCount;
    public int isPickAndSendCount;
    public int JobChangesCount;
    public int PriorityCount;
    public Integer canDoorstepCount;
    public Integer contactlessCount;
    public boolean HASLUNCHTIME;


    public dbObjAddressList(){

    }

    public void setHASLUNCHTIME(boolean HASLUNCHTIME) {
        if (!this.HASLUNCHTIME) {
            this.HASLUNCHTIME = HASLUNCHTIME;
        }
    }

    public void setIsIntendedRecOnlyCount(Integer aisIntendedRecOnlyCount) {
        this.isIntendedRecOnlyCount = aisIntendedRecOnlyCount;
    }

    public void setisTotallyExplicitCount(Integer aisTotallyExplicitCount) {
        this.isTotallyExplicitCount = aisTotallyExplicitCount;
    }

    public void setcanSlotLBCount(Integer acanSlotLBCount) {
        this.canSlotLBCount = acanSlotLBCount;
    }

    public void setcanSlotDoorCount(Integer acanSlotDoorCount) {
        this.canSlotDoorCount = acanSlotDoorCount;
    }

    public void setcanDoorstepCount(Integer acanDoorstepCount) {
        this.canDoorstepCount = acanDoorstepCount;
    }

    public void setcontactlessCount(Integer acontactlessCount) {
        this.contactlessCount = acontactlessCount;
    }

    public void setcanGuardCount(Integer acanGuardCount) {
        this.canGuardCount = acanGuardCount;
    }

    public void setIsPickAndSendCount(Integer aisPickAndSendCount) {
        this.isPickAndSendCount = aisPickAndSendCount;
    }

    public void setJobChangesCount(Integer aJobChangesCount) {
        this.JobChangesCount = aJobChangesCount;
    }

    public Integer getHasP2() {
        return this.HasP2;
    }
    public void setHasP2(Integer aHasP2) {
        this.HasP2 = aHasP2;
    }

    public Integer getFLAG(){
        return this.FLAG;
    }
    public void setFLAG(Integer aFLAG) {
        this.FLAG = aFLAG;
    }

    public Boolean getHasDOCOD(){
        return this.HasDOCOD;
    }
    public void setHasDOCOD(Boolean aHasDOCOD) {
        this.HasDOCOD = aHasDOCOD;
    }

    public Boolean getHasTradeIn(){
        return this.HasTradeIn;
    }
    public void setHasTradeIn(Boolean aHasTradeIn) {
        this.HasTradeIn = aHasTradeIn;
    }

    public String getAddress(){
        return this.Address;
    }
    public void setAddress(String aAddress) {
        this.Address = aAddress;
    }

    public String getPostal(){
        return this.Postal;
    }
    public void setPostal(String aPostal) {
        this.Postal = aPostal;
    }

    public Double getLAT(){
        return this.LAT;
    }
    public void setLAT(Double aLAT) {
        this.LAT = aLAT;
    }

    public Double getLNG(){
        return this.LNG;
    }
    public void setLNG(Double aLNG) {
        this.LNG = aLNG;
    }

    public Long getFromDateTime(){
        return this.FromDateTime;
    }
    public void setFromDateTime(Long aFromDateTime) {
        this.FromDateTime = aFromDateTime;
    }

    public String getFromDateTimeConverted(){
        return this.FromDateTimeConverted;
    }
    public void setFromDateTimeConverted(String afromDateTimeConverted){
        this.FromDateTimeConverted = afromDateTimeConverted;
    }

    public String getFromText(){
        return this.FromText;
    }
    public void setFromText(String aFromText){
        this.FromText = aFromText;
    }

    public Long getToDateTime(){
        return this.ToDateTime;
    }
    public void setToDateTime(Long aToDateTime) {
        this.ToDateTime = aToDateTime;
    }

    public Long getExtDateTime(){
        return this.ExtDateTime;
    }
    public void setExtDateTime(Long aExtDateTime) {
        this.ExtDateTime = aExtDateTime;
    }

    public String getToDateTimeConverted(){
        return this.ToDateTimeConverted;
    }
    public void setToDateTimeConverted(String aToDateTimeConverted){
        this.ToDateTimeConverted = aToDateTimeConverted;
    }

    public String getToText(){
        return this.ToText;
    }
    public void setToText(String aToText){
        this.ToText = aToText;
    }

    public String getTimingText(){
        return this.TimingText;
    }
    public void setTimingText(String aTimingText){
        this.TimingText = aTimingText;
    }

    public Integer getJOBCOUNT(){
        return this.JOBCOUNT;
    }
    public void setJOBCOUNT(Integer aJOBCOUNT) {
        this.JOBCOUNT = aJOBCOUNT;
    }

    public Boolean getSignaureFlag(){
        return this.SignaureFlag;
    }
    public void setSignaureFlag(Boolean aSignaureFlag) {
        this.SignaureFlag = aSignaureFlag;
    }

    public Boolean getDNFFlag(){
        return this.DNFFlag;
    }
    public void setDNFFlag(Boolean aDNFFlag) {
        this.DNFFlag = aDNFFlag;
    }

    public Boolean getOTPFlag(){
        return this.OTPFlag;
    }
    public void setOTPFlag(Boolean aOTPFlag) {
        this.OTPFlag = aOTPFlag;
    }

    public Boolean getIsLate(){
        return this.IsLate;
    }
    public void setIsLate(Boolean aIsLate){
        this.IsLate = aIsLate;
    }

    public Boolean getHasNewlyInserted(){
        return this.HasNewlyInserted;
    }
    public void setHasNewlyInserted(Boolean aHasNewlyInserted){
        this.HasNewlyInserted = aHasNewlyInserted;
    }

    public String getJobInfoJsonStr(){
        return this.JobInfoJsonStr;
    }
    public void setJobInfoJsonStr(String aJobInfoJsonStr) {
        this.JobInfoJsonStr = aJobInfoJsonStr;
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
    public void setPriorityCount(int aPriorityCount) {
        this.PriorityCount = aPriorityCount;
    }
}
