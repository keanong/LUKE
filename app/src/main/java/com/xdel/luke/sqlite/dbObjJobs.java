package com.xdel.luke.sqlite;

/**
 * Created by kean.ong on 03/05/2017.
 */
public class dbObjJobs {
    public Long _ID;
    public Long JobsIDX;
    public long ClientIDX;
    public String INDUSTRY = "";
    public Long ContactIDX;
    public Integer CatTypeID;
    public String Postal;
    public String AddressWithPostalNoUnit;
    public String AddressWithoutPostal;
    public Double LAT;
    public Double LNG;
    public Long FM_AddressIDX;
    public Long FM_ContactIDX;
    public Long TO_AddressIDX;
    public Long TO_ContactIDX;
    public Integer ExpType;
    public String ExpTypeDesc;
    public Boolean IsPartOne;
    public String PartDesc;
    public Boolean IsDunning;
    public Boolean HasDOCOD;
    public Boolean HasTradeIn;
    public String JobNo;
    public String CNNo;
    public String CompanyName;
    public String ContactName;
    public String DO_INV_No;
    public String DO_INV_IDX;
    public String TAGS;
    public String FromDateTime;
    public String PickByDateTime;
    public String ReadyDateTime;
    public String ToDateTime;
    public String ExtDateTime;
    public Integer FLAG;
    public Integer PCS;
    public String JobInfoJsonStr;
    public Long StatusCodeIDX;
    public String TimingTxt;
    public Integer IsNewlyInserted;
    public String CheckLists;
    public Integer OtpLocked;
    public Integer NetsVisaTi;
    public int isPickAndSendCount;
    public int JobChangesCount;
    public boolean HASLUNCHTIME;
    public String DelAddFull = "";


    public dbObjJobs(){}

    public void setINDUSTRY(String INDUSTRY) {
        this.INDUSTRY = INDUSTRY;
    }

    public void setClientIDX(long clientIDX) {
        ClientIDX = clientIDX;
    }

    public void setContactIDX(Long contactIDX) {
        ContactIDX = contactIDX;
    }

    public void setDelAddFull(String delAddFull) {
        DelAddFull = delAddFull;
    }

    public void setHASLUNCHTIME(boolean HASLUNCHTIME) {
        if (!this.HASLUNCHTIME) {
            this.HASLUNCHTIME = HASLUNCHTIME;
        }
    }

    public void setisPickAndSendCount(int aisPickAndSendCount){
        this.isPickAndSendCount = aisPickAndSendCount;
    }

    public void setJobChangesCount(int aJobChangesCount) {
        this.JobChangesCount = aJobChangesCount;
    }

    public Integer getOtpLocked(){
        return this.OtpLocked;
    }
    public void setOtpLocked(Integer aOtpLocked) {
        this.OtpLocked = aOtpLocked;
    }

    public Long get_ID(){
        return this._ID;
    }
    public void set_ID(Long a_ID) {
        this._ID = a_ID;
    }

    public Long getJobsIDX(){
        return this.JobsIDX;
    }
    public void setJobsIDX(Long aJobsIDX){
        this.JobsIDX = aJobsIDX;
    }

    public Integer getCatTypeID(){
        return this.CatTypeID;
    }
    public void setCatTypeID(Integer aCatTypeID) {
        this.CatTypeID = aCatTypeID;
    }

    public String getPostal(){
        return this.Postal;
    }
    public void setPostal(String aPostal) {
        this.Postal = aPostal;
    }

    public String getAddressWithPostalNoUnit(){
        return this.AddressWithPostalNoUnit;
    }
    public void setAddressWithPostalNoUnit(String aAddressWithPostalNoUnit) {
        this.AddressWithPostalNoUnit = aAddressWithPostalNoUnit;
    }

    public String getAddressWithoutPostal(){
        return this.AddressWithoutPostal;
    }
    public void setAddressWithoutPostal(String aAddressWithoutPostal) {
        this.AddressWithoutPostal = aAddressWithoutPostal;
    }

    public Double getLAT(){
        return this.LAT;
    }
    public void setLAT(Double aLAT){
        this.LAT = aLAT;
    }

    public Double getLNG(){
        return this.LNG;
    }
    public void setLNG(Double aLNG){
        this.LNG = aLNG;
    }

    public Long getFM_AddressIDX() {
        return this.FM_AddressIDX;
    }
    public void setFM_AddressIDX(Long aFM_AddressIDX) {
        this.FM_AddressIDX = aFM_AddressIDX;
    }

    public Long getFM_ContactIDX(){
        return this.FM_ContactIDX;
    }
    public void setFM_ContactIDX(Long aFM_ContactIDX) {
        this.FM_ContactIDX = aFM_ContactIDX;
    }

    public Long getTO_AddressIDX(){
        return this.TO_AddressIDX;
    }
    public void setTO_AddressIDX(Long aTO_AddressIDX) {
        this.TO_AddressIDX = aTO_AddressIDX;
    }

    public Long getTO_ContactIDX(){
        return this.TO_ContactIDX;
    }
    public void setTO_ContactIDX(Long aTO_ContactIDX) {
        this.TO_ContactIDX = aTO_ContactIDX;
    }

    public Integer getExpType(){
        return this.ExpType;
    }
    public void setExpType(Integer aExpType) {
        this.ExpType = aExpType;
    }

    public String getExpTypeDesc(){
        return this.ExpTypeDesc;
    }
    public void setExpTypeDesc(String aExpTypeDesc) {
        this.ExpTypeDesc = aExpTypeDesc;
    }

    public Boolean getIsPartOne(){
        return this.IsPartOne;
    }
    public void setIsPartOne(Boolean aIsPartOne) {
        this.IsPartOne = aIsPartOne;
    }

    public String getPartDesc(){
        return this.PartDesc;
    }
    public void setPartDesc(String aPartDesc) {
        this.PartDesc = aPartDesc;
    }

    public Boolean getIsDunning(){
        return this.IsDunning;
    }
    public void setIsDunning(Boolean aIsDunning) {
        this.IsDunning = aIsDunning;
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

    public String getJobNo(){
        return this.JobNo;
    }
    public void setJobNo(String aJobNo) {
        this.JobNo = aJobNo;
    }

    public String getCNNo(){
        return this.CNNo;
    }
    public void setCNNo(String aCNNo) {
        this.CNNo = aCNNo;
    }

    public String getCompanyName(){
        return this.CompanyName;
    }
    public void setCompanyName(String aCompanyName) {
        this.CompanyName = aCompanyName;
    }

    public String getContactName(){
        return this.ContactName;
    }
    public void setContactName(String aContactName) {
        this.ContactName = aContactName;
    }

    public String getDO_INV_No(){
        return this.DO_INV_No;
    }
    public void setDO_INV_No(String aDO_INV_No) {
        this.DO_INV_No = aDO_INV_No;
    }

    public String getDO_INV_IDX(){
        return this.DO_INV_IDX;
    }
    public void setDO_INV_IDX(String aDO_INV_IDX) {
        this.DO_INV_IDX = aDO_INV_IDX;
    }

    public String getTAGS(){
        return this.TAGS;
    }
    public void setTAGS(String aTAGS){
        this.TAGS = aTAGS;
    }

    public String getFromDateTime(){
        return this.FromDateTime;
    }
    public void setFromDateTime(String aFromDateTime){
        this.FromDateTime = aFromDateTime;
    }

    public String getPickByDateTime(){
        return this.PickByDateTime;
    }
    public void setPickByDateTime(String aPickByDateTime){
        this.PickByDateTime = aPickByDateTime;
    }

    public String getReadyDateTime(){
        return this.ReadyDateTime;
    }
    public void setReadyDateTime(String aReadyDateTime) {
        this.ReadyDateTime = aReadyDateTime;
    }

    public String getToDateTime(){
        return this.ToDateTime;
    }
    public void setToDateTime(String aToDateTime){
        this.ToDateTime = aToDateTime;
    }

    public String getExtDateTime(){
        return this.ExtDateTime;
    }
    public void setExtDateTime(String aExtDateTime){
        this.ExtDateTime = aExtDateTime;
    }

    public Integer getFLAG(){
        return this.FLAG;
    }
    public void setFLAG(Integer aFLAG){
        this.FLAG = aFLAG;
    }

    public Integer getPCS(){
        return this.PCS;
    }
    public void setPCS(Integer aPCS){
        this.PCS = aPCS;
    }

    public String getJobInfoJsonStr(){
        return this.JobInfoJsonStr;
    }
    public void setJobInfoJsonStr(String aJobInfoJsonStr){
        this.JobInfoJsonStr = aJobInfoJsonStr;
    }

    public Long getStatusCodeIDX(){
        return this.StatusCodeIDX;
    }
    public void setStatusCodeIDX(Long aStatusCodeIDX){
        this.StatusCodeIDX = aStatusCodeIDX;
    }

    public String getTimingTxt(){
        return this.TimingTxt;
    }
    public void setTimingTxt(String aTimingTxt) {
        this.TimingTxt = aTimingTxt;
    }

    public Integer getIsNewlyInserted(){
        return this.IsNewlyInserted;
    }
    public void setIsNewlyInserted(Integer aIsNewlyInserted) {
        this.IsNewlyInserted = aIsNewlyInserted;
    }

    public String getCheckLists(){ return this.CheckLists; }
    public void setCheckLists(String aCheckLists) {
        this.CheckLists = aCheckLists;
    }

    public void setNetsVisaTi(Integer aNetsVisaTi) {
        this.NetsVisaTi = aNetsVisaTi;
    }
}
