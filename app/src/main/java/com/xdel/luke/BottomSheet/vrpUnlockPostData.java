package com.xdel.luke.BottomSheet;

public class vrpUnlockPostData {
    public String POSTAL;
    public String AddressWithPostalNoUnit;
    public int PIP;
    public int DIP;
    public int PARKED;
    public long RDYDT;
    public long PUBYDT;
    public long FDT;
    public long TDT;
    public long EFDT;
    public long ETDT;
    public String JOBSIDXS;
    public int ColdCount;
    public int ReqNetsCount;
    public int ReqVisaCount;
    public int ReqTiCount;
    public int position;
    public int positionCurrMax;
    public int usedpostalcount;
    public int limitpostalcount;
    public int selectedcount = 0;
    public boolean selected;
    public String TW;
    public boolean hidden;
    public boolean isUsedInPrevCred;

    public vrpUnlockPostData(){};

    public vrpUnlockPostData(String aPOSTAL, String aAddressWithPostalNoUnit, int aPIP, int aDIP,
                             long aRDYDT, long aPUBYDT, long aFDT, long aTDT, long aEFDT, long aETDT, String aJOBSIDXS, int aColdCount, int aReqNetsCount, int aReqVisaCount, int aReqTiCount) {
        this.POSTAL = aPOSTAL;
        this.AddressWithPostalNoUnit = aAddressWithPostalNoUnit;
        this.PIP = aPIP;
        this.DIP = aDIP;
        this.RDYDT = aRDYDT;
        this.PUBYDT = aPUBYDT;
        this.FDT = aFDT;
        this.TDT = aTDT;
        this.EFDT = aEFDT;
        this.ETDT = aETDT;
        this.JOBSIDXS = aJOBSIDXS;
        this.ColdCount = aColdCount;
        this.ReqNetsCount = aReqNetsCount;
        this.ReqVisaCount = aReqVisaCount;
        this.ReqTiCount = aReqTiCount;
    }

    public void setUsedInPrevCred(boolean usedInPrevCred) {
        isUsedInPrevCred = usedInPrevCred;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setPOSTAL(String POSTAL) {
        this.POSTAL = POSTAL;
    }

    public void setAddressWithPostalNoUnit(String addressWithPostalNoUnit) {
        AddressWithPostalNoUnit = addressWithPostalNoUnit;
    }

    public void setPIP(int PIP) {
        this.PIP = PIP;
    }

    public void setDIP(int DIP) {
        this.DIP = DIP;
    }

    public void setRDYDT(long RDYDT) {
        this.RDYDT = RDYDT;
    }

    public void setPUBYDT(long PUBYDT) {
        this.PUBYDT = PUBYDT;
    }

    public void setFDT(long FDT) {
        this.FDT = FDT;
    }

    public void setTDT(long TDT) {
        this.TDT = TDT;
    }

    public void setEFDT(long EFDT) {
        this.EFDT = EFDT;
    }

    public void setETDT(long ETDT) {
        this.ETDT = ETDT;
    }

    public void setJOBSIDXS(String JOBSIDXS) {
        this.JOBSIDXS = JOBSIDXS;
    }

    public void setColdCount(int coldCount) {
        ColdCount = coldCount;
    }

    public void setReqNetsCount(int reqNetsCount) {
        ReqNetsCount = reqNetsCount;
    }

    public void setReqVisaCount(int reqVisaCount) {
        ReqVisaCount = reqVisaCount;
    }

    public void setReqTiCount(int reqTiCount) {
        ReqTiCount = reqTiCount;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPositionCurrMax(int positionCurrMax) {
        this.positionCurrMax = positionCurrMax;
    }

    public void setUsedpostalcount(int usedpostalcount) {
        this.usedpostalcount = usedpostalcount;
    }

    public void setLimitpostalcount(int limitpostalcount) {
        this.limitpostalcount = limitpostalcount;
    }

    public void setSelectedcount(int selectedcount) {
        this.selectedcount = selectedcount;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTW(String TW) {
        this.TW = TW;
    }
}
