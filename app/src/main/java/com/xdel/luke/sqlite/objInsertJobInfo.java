package com.xdel.luke.sqlite;

public class objInsertJobInfo {
    public long JobsIDX;
    public long ClientIDX;
    public long ContactIDX;
    public String INDUSTRY = "";
    public int CatTypeID;
    public String Postal;
    public String AddressWithPostalNoUnit;
    public String AddressWithoutPostal;
    public double LAT;
    public double LNG;
    public long FM_AddressIDX;
    public long FM_ContactIDX;
    public long TO_AddressIDX;
    public long TO_ContactIDX;
    public int ExpType;
    public String ExpTypeDESC;
    public int IsPartOne;
    public String PartDesc;
    public int IsDunning;
    public String JobNo;
    public String CNNo;
    public String CompanyName;
    public String ContactName;
    public String DO_INV_No;
    public String DO_INV_IDX;
    public String TAGS;
    public long FromDateTime;
    public long PickByDateTime;
    public long ReadyDateTime;
    public long ToDateTime;
    public long ExtDateTime;
    public long ExtFromDateTime;
    public int FLAG;
    public int PCS;
    public long StatusCodeIDX;
    public String JobInfoJsonStr;
    public int hasDOCOD;
    public int hasTradeIn;
    public String CHECKLISTS;
    public int NetsVisaTI;
    public int ExtCount;
    public int FLAG2;
    public int SlotFlag;
    public int HASLUNCHTIME;
    public String DelAddFull = "";

    public objInsertJobInfo(long aJobsIDX, long aClientIDX, long aContactIDX, String aINDUSTRY, int aCatTypeID, String aPostal, String aAddressWithPostalNoUnit, String aAddressWithoutPostal, double aLAT, double aLNG,
                            long aFM_AddressIDX, long aFM_ContactIDX, long aTO_AddressIDX, long aTO_ContactIDX, int aExpType, String aExpTypeDESC, int aIsPartOne, String aPartDesc,
                            int aIsDunning, String aJobNo, String aCNNo, String aCompanyName, String aContactName, String aDO_INV_No, String aDO_INV_IDX, String aTAGS,
                            long aFromDateTime, long aPickByDateTime, long aReadyDateTime, long aToDateTime, long aExtDateTime, int aFLAG, int aPCS, long aStatusCodeIDX,
                            String aJobInfoJsonStr, int ahasDOCOD, int ahasTradeIn, String aCHECKLISTS, int aNetsVisaTI, int aExtCount, int aFLAG2, int aHASLUNCHTIME, String aDelAddFull, int aSlotFlag){
        this.JobsIDX = aJobsIDX;
        this.ClientIDX = aClientIDX;
        this.ContactIDX = aContactIDX;
        this.INDUSTRY = aINDUSTRY;
        this.CatTypeID = aCatTypeID;
        this.Postal = aPostal;
        this.AddressWithPostalNoUnit = aAddressWithPostalNoUnit;
        this.AddressWithoutPostal = aAddressWithoutPostal;
        this.LAT = aLAT;
        this.LNG = aLNG;
        this.FM_AddressIDX = aFM_AddressIDX;
        this.FM_ContactIDX = aFM_ContactIDX;
        this.TO_AddressIDX = aTO_AddressIDX;
        this.TO_ContactIDX = aTO_ContactIDX;
        this.ExpType = aExpType;
        this.ExpTypeDESC = aExpTypeDESC;
        this.IsPartOne = aIsPartOne;
        this.PartDesc = aPartDesc;
        this.IsDunning = aIsDunning;
        this.JobNo = aJobNo;
        this.CNNo = aCNNo;
        this.CompanyName = aCompanyName;
        this.ContactName = aContactName;
        this.DO_INV_No = aDO_INV_No;
        this.DO_INV_IDX = aDO_INV_IDX;
        this.TAGS = aTAGS;
        this.FromDateTime = aFromDateTime;
        this.PickByDateTime = aPickByDateTime;
        this.ReadyDateTime = aReadyDateTime;
        this.ToDateTime = aToDateTime;
        this.ExtDateTime = aExtDateTime;
        this.FLAG = aFLAG;
        this.PCS = aPCS;
        this.StatusCodeIDX = aStatusCodeIDX;
        this.JobInfoJsonStr = aJobInfoJsonStr;
        this.hasDOCOD = ahasDOCOD;
        this.hasTradeIn = ahasTradeIn;
        this.CHECKLISTS = aCHECKLISTS;
        this.NetsVisaTI = aNetsVisaTI;
        this.ExtCount = aExtCount;
        this.FLAG2 = aFLAG2;
        this.HASLUNCHTIME = aHASLUNCHTIME;
        this.DelAddFull = aDelAddFull;
        this.SlotFlag = aSlotFlag;
    }

    public void setExtFromDateTime(long extFromDateTime) {
        ExtFromDateTime = extFromDateTime;
    }
}
