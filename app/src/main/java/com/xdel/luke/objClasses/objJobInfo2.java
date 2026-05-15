package com.xdel.luke.objClasses;

import com.xdel.luke.Locker.LockerStation;

/**
 * Created by kean.ong on 03/12/2015.
 */
public class objJobInfo2 {

    public String CNURL;
    public String PicturesURL;
    public int ExtCount;
    //public objLukeDefinitions.eTOSType TOSType;
    public int TOSType;
    public String ScansURL;
    public long StatusCodeIDX;
    public int Pieces;
    public String JobNo;
    public objAddressStructure2 DLRedirectedLocation;
    public long AddressIDX;
    public long ClientIDX;
    public String INDUSTRY = "";
    public String ReadyDateTime;
    public objJobInfo2[] LinkedJobs;
    public String PickByDateTime;
    public int Weight;
    public long ParentJobsIDX;
    public String DLSI;
    public double COD;
    public objJobLogObject[] JobLogObject;
    public String PUSI;
    public String DeliveryContents;
    public objAddressStructure2 PULocation;
    public String ConsignmentNote;
    public int ExpressType;
    public String ToDateTime;
    public String ExtFromDateTime;
    public String ExtDateTime;
    public String FromDateTime;
    public objDOObject[] DOList;
    public String SpecialInstructions;
    public objAddressStructure2 PURedirectedLocation;
    public long ContactIDX;
    public objAddressStructure2 DLLocation;
    public long JobsIDX;
    public Integer CODCollectAt;
    public Boolean DNF;
    public Boolean CODCollected;
    public int CashChequeTypeInt;
    public int CustorXDelCash;
    public long DOIDX;
    public String DOInvoice;
    public int catType;
    public String categoryTitle;
    public Double cashchequeAmt;
    public Integer imgmaincodStatusImgSrc;
    public Integer FLAG;
    public String[] BarcodeTags;
    public String[] BarcodeTagsExt;
    public String PIN;
    public Integer TypeOfPinVerification;
    public String ManualPinVerificaionText;
    public String IDVerification;
    public LockerStation LockerStation;
    public String DelAddFull = "";
    public int SlotFlag;

    public void setDelAddFull(String delAddFull) {
        DelAddFull = delAddFull;
    }


    public void setConsignmentNote(String aConsignmentNote){
        this.ConsignmentNote = aConsignmentNote;
    }

    public void setStatusCodeIDX(long aStatusCodeIDX){
        this.StatusCodeIDX = aStatusCodeIDX;
    }



}
