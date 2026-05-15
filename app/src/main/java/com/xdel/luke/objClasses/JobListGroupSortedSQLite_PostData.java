package com.xdel.luke.objClasses;

import java.util.List;

/**
 * Created by kean.ong on 08/05/2017.
 */
public class JobListGroupSortedSQLite_PostData  implements Comparable<JobListGroupSortedSQLite_PostData>{
    public String ExpType;
    public String Company;
    public String Contactname;
    public String Address;
    public String JobInfoJsonStr;
    public String CNNO;
    public String Part;
    public String JobsIDX;
    public Boolean IsSelected;
    public Boolean CbIsEnabled;
    public Boolean DNF;
    public Boolean OTP;
    public Boolean Signature;
    public Integer FLAG;
    public Boolean IsPartOne;
    public Boolean IsDunning;
    public String TimingText;
    public Integer Pieces;
    public objJobInfo ji;
    public Boolean HasDOCOD;
    public Boolean HasTradeIn;
    public Boolean isDelivery = false;
    public Integer NetsVisaTi;
    public Integer isPickAndSendCount;
    public Integer JobChangesCount;
    public boolean LLjlgcbChangeColor;
    public boolean allowedSelection;
    public Long ClientIDX;
    public Integer position;
    public int oriPos;
    public Integer FLAG2;
    public boolean HASLUNCHTIME;
    public boolean blink;
    public int CatTypeID;
    public String DelBlock = "";
    public List<String> AllDelUnits;
    public List<String> AllDelUnitsCombined;

    public void setCatTypeID(int aCatTypeID) { this.CatTypeID = aCatTypeID; }

    public void setBlink(boolean blink) {
        this.blink = blink;
    }

    public void setSavedValued(Integer aposition, int aoriPos, boolean aallowedSelection, boolean aIsSelected, boolean aCbIsEnabled) {
        this.position = aposition;
        this.oriPos = aoriPos;
        this.allowedSelection = aallowedSelection;
        this.IsSelected = aIsSelected;
        this.CbIsEnabled = aCbIsEnabled;
    }

    public void setSavedValuedNew(boolean aallowedSelection, boolean aIsSelected) {
        this.allowedSelection = aallowedSelection;
        this.IsSelected = aIsSelected;
    }

    public int compareTo(JobListGroupSortedSQLite_PostData other){
        return position.compareTo(other.position);
    }

    public JobListGroupSortedSQLite_PostData(String aExpType, String aCompany, String aContactname, String aAddress, String aJobInfoJsonStr, String aCNNO, String aPart,
                                             String aJobsIDX, Boolean aIsSelected, Boolean aCbIsEnabled, Boolean aDNF, Boolean aOTP, Boolean aSignature, Integer aFLAG, Boolean aIsPartOne,
                                             Boolean aIsDunning, String aTimingText, Integer aPieces, objJobInfo aji, Boolean aHasDOCOD, Boolean aHasTradeIn, Boolean aisDelivery, Integer aNetsVisaTi,
                                             Integer aisPickAndSendCount, Integer aJobChangesCount, int aoriPos, Integer aFLAG2){
        this.ExpType = aExpType;
        this.Company = aCompany;
        this.Contactname = aContactname;
        this.Address = aAddress;
        this.JobInfoJsonStr = aJobInfoJsonStr;
        this.CNNO = aCNNO;
        this.Part = aPart;
        this.JobsIDX = aJobsIDX;
        this.IsSelected = aIsSelected;
        this.CbIsEnabled = aCbIsEnabled;
        this.DNF = aDNF;
        this.OTP = aOTP;
        this.Signature = aSignature;
        this.FLAG = aFLAG;
        this.IsPartOne = aIsPartOne;
        this.IsDunning = aIsDunning;
        this.TimingText = aTimingText;
        this.Pieces = aPieces;
        this.ji = aji;
        this.HasDOCOD = aHasDOCOD;
        this.HasTradeIn = aHasTradeIn;
        this.isDelivery = aisDelivery;
        this.NetsVisaTi = aNetsVisaTi;
        this.isPickAndSendCount = aisPickAndSendCount;
        this.JobChangesCount = aJobChangesCount;
        this.allowedSelection = false;
        this.ClientIDX = null;
        this.position = -1;
        this.oriPos = aoriPos;
        this.FLAG2 = aFLAG2;
    }

    public void setHASLUNCHTIME(boolean HASLUNCHTIME) {
        if (!this.HASLUNCHTIME) {
            this.HASLUNCHTIME = HASLUNCHTIME;
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setClientIDX(Long clientIDX) {
        ClientIDX = clientIDX;
    }

    public Boolean getIsSelected(){
        return this.IsSelected;
    }
    public void setIsSelected(Boolean aIsSelected) {
        this.IsSelected = aIsSelected;
    }

    public void setJobInfoJsonStr(String aJobInfoJsonStr) {
        this.JobInfoJsonStr = aJobInfoJsonStr;
    }

    public boolean isLLjlgcbChangeColor() {
        return LLjlgcbChangeColor;
    }

    public void setLLjlgcbChangeColor(boolean LLjlgcbChangeColor) {
        this.LLjlgcbChangeColor = LLjlgcbChangeColor;
    }

    public void setCbIsEnabled(Boolean cbIsEnabled) {
        CbIsEnabled = cbIsEnabled;
    }

    public void setCbEnabledAllowedSelection(boolean aCbEnabled, boolean aAllowedSelection, Integer aposition) {
        this.CbIsEnabled = aCbEnabled;
        this.allowedSelection = aAllowedSelection;
        //this.position = aposition;
    }

    public void setCbEnabledAllowedSelectionIsSelected(boolean aallowedSelection, boolean aIsSelected, boolean aCbIsEnabled){
        this.allowedSelection = aallowedSelection;
        this.IsSelected = aIsSelected;
        this.CbIsEnabled = aCbIsEnabled;
    }

    public void setAllowedSelection(boolean allowedSelection) {
        this.allowedSelection = allowedSelection;
    }

}
