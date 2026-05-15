package com.xdel.luke.objClasses;

import android.util.Log;

import com.xdel.luke.JobInventoryVerify.InventoryEntry;
import com.xdel.luke.Locker.LockerStation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kean.ong on 11/06/2015.
 */
public class objJobInfo {

    public long AddressIDX;
    public String CNURL;
    public double COD;
    public Integer CODCollectAt;
    public long ClientIDX;
    public String INDUSTRY = "";
    public String ConsignmentNote;
    public long ContactIDX;
    public objAddressStructure DLLocation;
    public objAddressStructure DLRedirectedLocation;
    public String DLSI;
    public objDOObject[] DOList;
    public String DeliveryContents;
    public int ExpressType;
    public int ExtCount;
    public String ExtFromDateTime;
    public String ExtDateTime;
    public String FromDateTime;
    public objJobLogObject[] JobLogObject;
    public String JobNo;
    public long JobsIDX;
    public objJobInfo[] LinkedJobs;
    public objAddressStructure PULocation;
    public objAddressStructure PURedirectedLocation;
    public String PUSI;
    public long ParentJobsIDX;
    public String PickByDateTime;
    public String PicturesURL;
    public int Pieces;
    public String ReadyDateTime;
    public String ScansURL;
    public String SpecialInstructions;
    public long StatusCodeIDX;
    //public objLukeDefinitions.eTOSType TOSType;
    public int TOSType;
    public String ToDateTime;
    public int Weight;
    public Boolean DNF;
    public Boolean CODCollected;
    public Integer CashChequeTypeInt; //0 == unknown 1 == cash 2 == cheq
    public Integer CustorXDelCash; //1 == xdel 2 == cust
    public Long DOIDX;
    public String DOInvoice;
    public int catType;
    public String categoryTitle;
    public Double cashchequeAmt;
    public Integer imgmaincodStatusImgSrc;
    public Integer FLAG;
    public Integer FLAG2;
    public String Receiver;
    public ArrayList<String> convertedBitmapToBase64;
    public ArrayList<objConvertedBitmapToBase64> objConvertedBitmapToBase64;
    public ArrayList<objConvertedBitmapToBase64> objCheckListConvertedBitmapToBase64List;
    public ArrayList<String> UploadJobSignatureIABitmapToBase64List;
    public String SignatureStr;
    public long pendingStatusUpdate;
    public String FailureReason;
    public String[] BarcodeTags;
    public String[] BarcodeTagsExt;
    public String PIN;
    public Integer TypeOfPinVerification; //0 = no verification required, 1 = verified via OTP, 2 = verified via manual verification, 4 == fail dip
    public String ManualPinVerificaionText;
    public String Reason;
    public int timedifference;
    public String[] CheckList;
    public Integer OTPtries;
    public long i_have_arrived_timestamp;
    public Boolean PULunch_Avoid;
    public String PULunch_From;
    public String PULunch_To;
    public Boolean DLLunch_Avoid;
    public String DLLunch_From;
    public String DLLunch_To;
    public String IDVerification;
    public String NFC_Tag_ID;
    public Boolean NFC_Tag_ID_Verified;
    public LockerStation LockerStation;
    public Date promiseFrom;
    public Date promiseTo;
    public int HASLUNCHTIME;
    public InventoryEntry[] InventoryItems;
    public String DelAddFull = "";
    public int SlotFlag;

    public void setSlotFlag(int slotFlag) {
        SlotFlag = slotFlag;
    }

    public void setDelAddFull(String delAddFull) {
        DelAddFull = delAddFull;
    }

    public void setHASLUNCHTIME(int HASLUNCHTIME) {
        this.HASLUNCHTIME = HASLUNCHTIME;
    }

    public Date getPromiseFrom() {
        return promiseFrom;
    }
    public void setPromiseFrom(Date aPromiseFrom) {
        this.promiseFrom = aPromiseFrom;
    }

    public Date getPromiseTo() {
        return promiseTo;
    }
    public void setPromiseTo(Date aPromiseTo) {
        this.promiseTo = aPromiseTo;
    }

    public com.xdel.luke.Locker.LockerStation getLockerStation() {
        return LockerStation;
    }
    public void setLockerStation(com.xdel.luke.Locker.LockerStation lockerStation) {
        LockerStation = lockerStation;
    }

    public Boolean getNFC_Tag_ID_Verified(){
        return this.NFC_Tag_ID_Verified;
    }
    public void setNFC_Tag_ID_Verified(Boolean aNFC_Tag_ID_Verified) {
        this.NFC_Tag_ID_Verified = aNFC_Tag_ID_Verified;
    }

    public String getNFC_Tag_ID(){
        return this.NFC_Tag_ID;
    }
    public void setNFC_Tag_ID(String aNFC_Tag_ID) {
        this.NFC_Tag_ID = aNFC_Tag_ID;
    }

    public String getIDVerification(){
        return this.IDVerification;
    }
    public void setIDVerification(String aIDVerification){
        this.IDVerification = aIDVerification;
    }

    public Boolean getPULunch_Avoid() {
        return this.PULunch_Avoid;
    }
    public void setPULunch_Avoid(Boolean aPULunch_Avoid){
        this.PULunch_Avoid = aPULunch_Avoid;
    }

    public String getPULunch_From() {
        return this.PULunch_From;
    }
    public void setPULunch_From(String aPULunch_From) {
        this.PULunch_From = aPULunch_From;
    }

    public String getPULunch_To(){
        return this.PULunch_To;
    }
    public void setPULunch_To(String aPULunch_To) {
        this.PULunch_To = aPULunch_To;
    }

    public Boolean getDLLunch_Avoid() {
        return this.DLLunch_Avoid;
    }
    public void setDLLunch_Avoid(Boolean aDLLunch_Avoid) {
        this.DLLunch_Avoid = aDLLunch_Avoid;
    }

    public String getDLLunch_From() {
        return this.DLLunch_From;
    }
    public void setDLLunch_From(String aDLLunch_From) {
        this.DLLunch_From = aDLLunch_From;
    }

    public String getDLLunch_To() {
        return this.DLLunch_To;
    }
    public void setDLLunch_To(String aDLLunch_To) {
        this.DLLunch_To = aDLLunch_To;
    }

    public long getI_have_arrived_timestamp(){
        return this.i_have_arrived_timestamp;
    }
    public void setI_have_arrived_timestamp(long ai_have_arrived_timestamp) {
        this.i_have_arrived_timestamp = ai_have_arrived_timestamp;
    }

    public Integer getOTPtries() {
        return this.OTPtries;
    }
    public void setOTPtries(Integer aOTPtries) {
        this.OTPtries = aOTPtries;
    }

    public String getReason(){
        return this.Reason;
    }

    public void setReason(String aReason) {
        this.Reason = aReason;
    }

    public Integer getTypeOfPinVerification(){
        if (TypeOfPinVerification == null) {
            return 0;
        } else {
            return this.TypeOfPinVerification;
        }
    }

    public void setTypeOfPinVerification(Integer aTypeOfPinVerification) {
        this.TypeOfPinVerification = aTypeOfPinVerification;
    }

    public String getManualPinVerificaionText(){
        if (this.ManualPinVerificaionText == null) {
            return "";
        } else {
            return this.ManualPinVerificaionText;
        }
    }

    public void setManualPinVerificaionText(String aManualPinVerificaionText) {
        this.ManualPinVerificaionText = aManualPinVerificaionText;
    }

    public void setFailureReason(String aFailureReason){
        this.FailureReason = aFailureReason;
    }

    public long getpendingStatusUpdate(){
        return this.pendingStatusUpdate;
    }

    public void setpendingStatusUpdate(long apendingStatusUpdate){
        this.pendingStatusUpdate = apendingStatusUpdate;
    }

    public String getSignatureStr(){
        return this.SignatureStr;
    }

    public void setSignatureStr(String aSignatureStr){
        this.SignatureStr = aSignatureStr;
    }

    public ArrayList<String> getconvertedBitmapToBase64(){
        return this.convertedBitmapToBase64;
    }

    public void setconvertedBitmapToBase64(ArrayList<String> aconvertedBitmapToBase64){
        this.convertedBitmapToBase64 = aconvertedBitmapToBase64;
    }

    public void setObjConvertedBitmapToBase64(ArrayList<objConvertedBitmapToBase64> objConvertedBitmapToBase64) {
        this.objConvertedBitmapToBase64 = objConvertedBitmapToBase64;
    }

    public void setObjCheckListConvertedBitmapToBase64List(ArrayList<objConvertedBitmapToBase64> aobjCheckListConvertedBitmapToBase64List) {
        this.objCheckListConvertedBitmapToBase64List = aobjCheckListConvertedBitmapToBase64List;
    }

    public ArrayList<String> getUploadJobSignatureIABitmapToBase64List(){
        return this.UploadJobSignatureIABitmapToBase64List;
    }

    public void setUploadJobSignatureIABitmapToBase64List(ArrayList<String> aUploadJobSignatureIABitmapToBase64List){
        this.UploadJobSignatureIABitmapToBase64List = aUploadJobSignatureIABitmapToBase64List;
    }

    public String getReceiver(){
        return this.Receiver;
    }

    public void setReceiver(String aReceiver){
        this.Receiver = aReceiver;
    }

    public Integer getCashChequeTypeInt(){
        return this.CashChequeTypeInt;
    }

    public void setCashChequeTypeInt(Integer aCashChequeTypeInt){
        this.CashChequeTypeInt = aCashChequeTypeInt;
    }

    public Integer getCustorXDelCash(){
        return this.CustorXDelCash;
    }

    public void setCustorXDelCash(Integer aCustorXDelCash){
        this.CustorXDelCash = aCustorXDelCash;
    }

    public long getDOIDX(){
        return this.DOIDX;
    }

    public void setDOIDX(Long aDOIDX){
        this.DOIDX = aDOIDX;
    }

    public String getDOInvoice(){
        return this.DOInvoice;
    }

    public void setDOInvoice(String aDOInvoice){
        this.DOInvoice = aDOInvoice;
    }

    public int getcatType(){
        return this.catType;
    }

    public void setcatType(int acatType){
        this.catType = acatType;
    }

    public String getcategoryTitle(){
        return this.categoryTitle;
    }

    public void setcategoryTitle(String acategoryTitle){
        this.categoryTitle = acategoryTitle;
    }

    public Double getcashchequeAmt(){
        return this.cashchequeAmt;
    }

    public void setcashchequeAmt(Double acashchequeAmt){
        this.cashchequeAmt = acashchequeAmt;
    }

    public Integer getimgmaincodStatusImgSrc(){
        return this.imgmaincodStatusImgSrc;
    }

    public void setimgmaincodStatusImgSrc(Integer aimgmaincodStatusImgSrc){
        this.imgmaincodStatusImgSrc = aimgmaincodStatusImgSrc;
    }

    public long getAddressIDX(){
        return this.AddressIDX;
    }

    public void setAddressIDX(long aAddressIDX){
        this.AddressIDX = aAddressIDX;
    }

    public String getCNURL(){
        return this.CNURL;
    }

    public void setCNURL(String aCNURL){
        this.CNURL = aCNURL;
    }

    public double getCOD(){
        return this.COD;
    }

    public void setCOD(double aCOD){
        this.COD = aCOD;
    }

    /*public int getCODCollectAt(){
        return this.CODCollectAt;
    }*/
    public Integer getCODCollectAt(){
        return this.CODCollectAt;
    }

    /*public void setCODCollectAt(int aCODCollectAt){
        this.CODCollectAt = aCODCollectAt;
    }*/
    public void setCODCollectAt(Integer aCODCollectAt){
        this.CODCollectAt = aCODCollectAt;
    }

    public long getClientIDX(){
        return this.ClientIDX;
    }

    public void setClientIDX(long aClientIDX){
        this.ClientIDX = aClientIDX;
    }

    public String getINDUSTRY() {
        return INDUSTRY;
    }

    public void setINDUSTRY(String INDUSTRY) {
        this.INDUSTRY = INDUSTRY;
    }

    public String getConsignmentNote(){
        return this.ConsignmentNote;
    }

    public void setConsignmentNote(String aConsignmentNote){
        this.ConsignmentNote = aConsignmentNote;
    }

    public String[] getBarcodeTags() {
        return this.BarcodeTags;
    }

    public void setBarcodeTags(String[] aBarcodeTags) {
        this.BarcodeTags = aBarcodeTags;
    }

    public String[] getBarcodeTagsExt() {
        return this.BarcodeTagsExt;
    }

    public void setBarcodeTagsExt(String[] aBarcodeTagsExt) {
        this.BarcodeTagsExt = aBarcodeTagsExt;
    }

    public long getContactIDX(){
        return this.ContactIDX;
    }

    public void setContactIDX(long aContactIDX){
        this.ContactIDX = aContactIDX;
    }

    public objAddressStructure getDLLocation(){
        return this.DLLocation;
    }

    public void setDLLocation(objAddressStructure aDLLocation){
        this.DLLocation = aDLLocation;
    }

    public objAddressStructure getDLRedirectedLocation(){
        return this.DLRedirectedLocation;
    }

    public void setDLRedirectedLocation(objAddressStructure aDLRedirectedLocation){
        this.DLRedirectedLocation = aDLRedirectedLocation;
    }

    public String getDLSI(){
        return this.DLSI;
    }

    public void setDLSI(String aDLSI){
        this.DLSI = aDLSI;
    }

    public objDOObject[] getDOList(){
        return this.DOList;
    }

    public void setDOList(objDOObject[] aDOList){
        this.DOList = aDOList;
    }

    public String getDeliveryContents(){
        return this.DeliveryContents;
    }

    public void setDeliveryContents(String aDeliveryContents){
        this.DeliveryContents = aDeliveryContents;
    }

    public int getExpressType(){
        return this.ExpressType;
    }

    public void setExpressType(int aExpressType){
        this.ExpressType = aExpressType;
    }

    public int getExtCount(){
        return this.ExtCount;
    }

    public void setExtCount(int aExtCount){
        this.ExtCount = aExtCount;
    }

    public String getFromDateTime(){
        return this.FromDateTime;
    }

    public void setFromDateTime(String aFromDateTime){
        this.FromDateTime = aFromDateTime;
    }

    public objJobLogObject[] getJobLogObject(){
        return this.JobLogObject;
    }

    public void setJobLogObject(objJobLogObject[] aJobLogObject){
        this.JobLogObject = aJobLogObject;
    }

    public String getJobNo(){
        return this.JobNo;
    }

    public void setJobNo(String aJobNo){
        this.JobNo = aJobNo;
    }

    public long getJobsIDX(){
        return this.JobsIDX;
    }

    public void setJobsIDX(long aJobsIDX){
        this.JobsIDX = aJobsIDX;
    }

    public objJobInfo[] getLinkedJobs(){
        return this.LinkedJobs;
    }

    public void setLinkedJobs(objJobInfo[] aLinkedJobs){
        this.LinkedJobs = aLinkedJobs;
    }

    public objAddressStructure getPULocation(){
        return this.PULocation;
    }

    public void setPULocation(objAddressStructure aPULocation){
        this.PULocation = aPULocation;
    }

    public objAddressStructure getPURedirectedLocation(){
        return this.PURedirectedLocation;
    }

    public void setPURedirectedLocation(objAddressStructure aPURedirectedLocation){
        this.PURedirectedLocation = aPURedirectedLocation;
    }

    public String getPUSI(){
        return this.PUSI;
    }

    public void setPUSI(String aPUSI){
        this.PUSI = aPUSI;
    }

    public long getParentJobsIDX(){
        return this.ParentJobsIDX;
    }

    public void setParentJobsIDX(long aParentJobsIDX){
        this.ParentJobsIDX = aParentJobsIDX;
    }

    public String getPickByDateTime(){
        return this.PickByDateTime;
    }

    public void setPickByDateTime(String aPickByDateTime){
        this.PickByDateTime = aPickByDateTime;
    }

    public String getPicturesURL(){
        return this.PicturesURL;
    }

    public void setPicturesURL(String aPicturesURL){
        this.PicturesURL = aPicturesURL;
    }

    public int getPieces(){
        return this.Pieces;
    }

    public void setPieces(int aPieces){
        this.Pieces = aPieces;
    }

    public String getReadyDateTime(){
        return this.ReadyDateTime;
    }

    public void setReadyDateTime(String aReadyDateTime){
        this.ReadyDateTime = aReadyDateTime;
    }

    public String getScansURL(){
        return this.ScansURL;
    }

    public void setScansURL(String aScansURL){
        this.ScansURL = aScansURL;
    }

    public String getSpecialInstructions(){
        return this.SpecialInstructions;
    }

    public void setSpecialInstructions(String aSpecialInstructions){
        this.SpecialInstructions = aSpecialInstructions;
    }

    public long getStatusCodeIDX(){
        return this.StatusCodeIDX;
    }

    public void setStatusCodeIDX(long aStatusCodeIDX){
        this.StatusCodeIDX = aStatusCodeIDX;
    }

    public int getTOSType(){
        return this.TOSType;
    }

    public void setTOSType(int aTOSType){
        this.TOSType = aTOSType;
    }

    public String getToDateTime(){
        return this.ToDateTime;
    }

    public void setToDateTime(String aToDateTime){
        this.ToDateTime = aToDateTime;
    }

    public int getWeight(){
        return this.Weight;
    }

    public void setWeight(int aWeight){
        this.Weight = aWeight;
    }

    private String convertJSONdateToJavaDateString(String input){
        String javaDateString="";
        try
        {
            Integer datetimestamp = Integer.parseInt(input.replaceAll("\\D", ""));
            Date date = new Date(datetimestamp);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
            javaDateString = formatter.format(date);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return javaDateString;
    }



}
