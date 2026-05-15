package com.xdel.luke.PieceByPiecePU;

public class dbMedsJobs {
    public Long _ID;
    public Long JobsIDX;
    public Integer PCS;
    public Integer PCS_SCANNED;
    public Integer FPCS;
    public String Postal;
    public String AddressWithPostalNoUnit;
    public String AddressWithoutPostal;
    public Double LAT;
    public Double LNG;
    public Integer ExpType;
    public String ExpTypeDesc;
    public String JobNo;
    public String CNNo;
    public Integer FLAG;
    public Integer FLAG2;
    public String JobInfoJsonStr;
    public String CheckLists;
    public String TimingTxt;
    public String DelAddFull = "";
    public Integer PUC;

    public dbMedsJobs(){

    }

    public void setPUC(Integer PUC) {
        this.PUC = PUC;
    }

    public void setDelAddFull(String delAddFull) {
        DelAddFull = delAddFull;
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

    public Integer getFLAG(){
        return this.FLAG;
    }
    public void setFLAG(Integer aFLAG){
        this.FLAG = aFLAG;
    }

    public Integer getFLAG2(){
        return this.FLAG2;
    }
    public void setFLAG2(Integer aFLAG2){
        this.FLAG2 = aFLAG2;
    }

    public Integer getPCS(){
        return this.PCS;
    }
    public void setPCS(Integer aPCS){
        this.PCS = aPCS;
    }

    public Integer getFPCS(){
        return this.FPCS;
    }
    public void setFPCS(Integer aFPCS){
        this.FPCS = aFPCS;
    }

    public Integer getPCS_SCANNED() {
        return PCS_SCANNED;
    }
    public void setPCS_SCANNED(Integer PCS_SCANNED) {
        this.PCS_SCANNED = PCS_SCANNED;
    }

    public String getJobInfoJsonStr(){
        return this.JobInfoJsonStr;
    }
    public void setJobInfoJsonStr(String aJobInfoJsonStr){
        this.JobInfoJsonStr = aJobInfoJsonStr;
    }

    public String getTimingTxt(){
        return this.TimingTxt;
    }
    public void setTimingTxt(String aTimingTxt) {
        this.TimingTxt = aTimingTxt;
    }


    public String getCheckLists(){ return this.CheckLists; }
    public void setCheckLists(String aCheckLists) {
        this.CheckLists = aCheckLists;
    }

}
