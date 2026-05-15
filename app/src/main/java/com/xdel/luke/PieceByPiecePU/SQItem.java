package com.xdel.luke.PieceByPiecePU;

import com.xdel.luke.objClasses.objJobInfo;

public class SQItem implements Comparable<SQItem>{
    public String ExpType;
    //public String Company;
    //public String Contactname;
    public String Address;
    public String JobInfoJsonStr;
    public String CNNO;
    //public String Part;
    public String JobsIDX;
    public Boolean IsSelected;
    //public Boolean CbIsEnabled;
    //public Boolean DNF;
    //public Boolean OTP;
    //public Boolean Signature;
    public Integer FLAG;
    //public Boolean IsPartOne;
    //public Boolean IsDunning;
    public String TimingText;
    public Integer PCS;
    public Integer PCS_SCANNED;
    public objJobInfo ji;
    //public Boolean HasDOCOD;
    //public Boolean HasTradeIn;
    //public Boolean isDelivery = false;
    //public Integer NetsVisaTi;
    //public Integer isPickAndSendCount;
    //public Integer JobChangesCount;
    public boolean LLjlgcbChangeColor;
    //public boolean allowedSelection;
    //public Long ClientIDX;
    public Integer position;
    public int oriPos;
    public Integer FLAG2;
    public boolean blink;
    public int blinkmode = 0;
    public boolean manual = false;

    public SQItem(String aExpType, String aAddress, String aJobInfoJsonStr, String aCNNO,
                  String aJobsIDX, Boolean aIsSelected, Integer aFLAG,
                  String aTimingText, Integer aPCS, Integer aPCS_SCANNED, int aoriPos, Integer aFLAG2, objJobInfo aji, boolean amanual){
        this.ExpType = aExpType;
        this.Address = aAddress;
        this.JobInfoJsonStr = aJobInfoJsonStr;
        this.CNNO = aCNNO;
        this.JobsIDX = aJobsIDX;
        this.IsSelected = aIsSelected;
        this.FLAG = aFLAG;
        this.TimingText = aTimingText;
        this.PCS = aPCS;
        this.PCS_SCANNED = aPCS_SCANNED;
        this.position = -1;
        this.oriPos = aoriPos;
        this.FLAG2 = aFLAG2;
        this.ji = aji;
        this.manual = amanual;
    }

    public void setBlinkmode(int blinkmode) {
        this.blinkmode = blinkmode;
    }

    public void setBlink(boolean blink) {
        this.blink = blink;
    }

    public void setSavedValued(Integer aposition, int aoriPos, boolean aIsSelected) {
        this.position = aposition;
        this.oriPos = aoriPos;
        this.IsSelected = aIsSelected;
    }

    public int compareTo(SQItem other){
        //return position.compareTo(other.position);
        return PCS.compareTo(other.PCS);
    }

    public void setPCS_SCANNED(Integer PCS_SCANNED) {
        this.PCS_SCANNED += PCS_SCANNED;
    }

    public void setPosition(int position) {
        this.position = position;
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

}
