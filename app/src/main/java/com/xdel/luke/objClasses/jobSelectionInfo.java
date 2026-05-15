package com.xdel.luke.objClasses;

import android.content.SharedPreferences;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.JobsHandler3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class jobSelectionInfo {
    public long JobsIDX;
    public long StatusCodeIDX = 103;

    public boolean HAS_STDRUN;
    public int HAS_STDRUNCount;

    public boolean HAS_OTP;
    public int HAS_OTPCount;

    public boolean CANNOT_SKIP_OTP;
    public int CANNOT_SKIP_OTPCount;

    public boolean HAS_CUSTID;
    public int HAS_CUSTIDCount;

    public boolean HAS_CUSTID_VERIFICATION_REQ;
    public int HAS_CUSTID_VERIFICATION_REQCount;

    public boolean HAS_CUSTID_VERIFICATION_NOT_REQ;
    public int HAS_CUSTID_VERIFICATION_NOT_REQCount;

    public boolean HAS_CONTACTLESS;
    public int HAS_CONTACTLESSCount;

    public boolean HAS_CHECKLIST;
    public int HAS_CHECKLISTCount;

    public boolean HAS_TRADE_IN;
    public int HAS_TRADE_INCount;

    public boolean HAS_SECURITY_SEAL;
    public int HAS_SECURITY_SEALCount;

    public boolean HAS_REQ_STAFF_ID;
    public int HAS_REQ_STAFF_IDCount;

    public boolean HAS_REQ_NFC_SCAN;
    public int HAS_REQ_NFC_SCANCount;

    public boolean HAS_DO_COD;
    public int HAS_DO_CODCount;

    public boolean HAS_RECEIVER_ONLY;
    public int HAS_RECEIVER_ONLYCount;

    public boolean IS_DUNNING;
    public int IS_DUNNINGCount;

    public boolean IS_LOCKER;
    public int IS_LOCKERCount;

    public boolean IS_ALPS;
    public int IS_ALPSCount;

    public boolean IS_NORMALJOB;
    public int IS_NORMALJOBCount;

    public boolean IS_DOC;
    public int IS_DOCCount;

    public boolean hasReqItemProof;
    public int hasReqItemProofCount;

    public boolean CanSlotLB;
    public int CanSlotLBCount;

    public boolean CanSlotUnderDoor;
    public int CanSlotUnderDoorCount;

    public boolean CanGuard;
    public int CanGuardCount;

    public boolean CanLeaveDoorStep;
    public int CanLeaveDoorStepCount;

    public boolean ReceiverOnly;
    public int ReceiverOnlyCount;

    public boolean ReviewLinkSMS;
    public int ReviewLinkSMSCount;

    public boolean ReqSign;
    public int ReqSignCount;

    public boolean NotAllowedSelection;
    public int NotAllowedSelectionCount;

    public boolean HAS_CANNOT_PHOTO_DL;
    public int HAS_CANNOT_PHOTO_DLCount;


    public String RECEIVER_NAME;

    public String CUSTID;

    public String PIN;

    public String DelBlock = "";

    public String POSTAL;

    public ArrayList<String> AllDelUnits;

    public ArrayList<String> AllDelUnitsCombinedList;

    public String[] CHECKLIST;

    public ArrayList<String> MOBILE_NOsList;

    public ArrayList<String> OTPList;

    public ArrayList<String> ShpmtV_RefList;

    public ArrayList<Long> JobIDXList;

    public ArrayList<Long> InvalidJobIDXList;

    public ArrayList<Long> SelectedValidJobIDXList;

    public ArrayList<Long> SelectedInvalidJobIDXList;

    public ArrayList<Long> JobIDXsForContactlessChecklist;

    public ArrayList<Long> JobIDXsForVerificationChecklist;

    public ArrayList<String> JobNosForContactlessChecklist;

    public ArrayList<String> JobNosForVerificationChecklist;


    public int FLAG;
    public int FLAG2;

    /*public jobSelectionInfo(long aJobsIDX, boolean aHAS_OTP, boolean aCANNOT_SKIP_OTP, boolean aHAS_CUSTID, boolean aHAS_CUSTID_VERIFICATION_REQ, boolean aHAS_CUSTID_VERIFICATION_NOT_REQ,
                            boolean aHAS_CHECKLIST, boolean aHAS_TRADE_IN, boolean aHAS_SECURITY_SEAL, boolean aHAS_REQ_STAFF_ID, boolean aHAS_REQ_NFC_SCAN,
                            boolean aHAS_DO_COD, boolean aHAS_RECEIVER_ONLY, boolean aIS_DUNNING, boolean aIS_LOCKER, boolean aIS_ALPS, boolean aIS_NORMALJOB,
                            String aRECEIVER_NAME, String aCUSTID, String aDelBlock, String aPOSTAL, ArrayList<String> aAllDelUnitsCombinedList,
                            String[] aCHECKLIST, ArrayList<String> aMOBILE_NOs) {
        this.JobsIDX = aJobsIDX;
        this.HAS_OTP = aHAS_OTP;
        this.CANNOT_SKIP_OTP = aCANNOT_SKIP_OTP;
        this.HAS_CUSTID = aHAS_CUSTID;
        this.HAS_CUSTID_VERIFICATION_REQ = aHAS_CUSTID_VERIFICATION_REQ;
        this.HAS_CUSTID_VERIFICATION_NOT_REQ = aHAS_CUSTID_VERIFICATION_NOT_REQ;
        this.HAS_CHECKLIST = aHAS_CHECKLIST;
        this.HAS_TRADE_IN = aHAS_TRADE_IN;
        this.HAS_SECURITY_SEAL = aHAS_SECURITY_SEAL;
        this.HAS_REQ_STAFF_ID = aHAS_REQ_STAFF_ID;
        this.HAS_REQ_NFC_SCAN = aHAS_REQ_NFC_SCAN;
        this.HAS_DO_COD = aHAS_DO_COD;
        this.HAS_RECEIVER_ONLY = aHAS_RECEIVER_ONLY;
        this.IS_DUNNING = aIS_DUNNING;
        this.IS_LOCKER = aIS_LOCKER;
        this.IS_ALPS = aIS_ALPS;
        this.IS_NORMALJOB = aIS_NORMALJOB;
        this.RECEIVER_NAME = aRECEIVER_NAME;
        this.CUSTID = aCUSTID;
        this.DelBlock = aDelBlock;
        this.POSTAL = aPOSTAL;
        this.AllDelUnitsCombinedList = aAllDelUnitsCombinedList;
        this.CHECKLIST = aCHECKLIST;
        this.MOBILE_NOsList = aMOBILE_NOs;
    }*/


    public jobSelectionInfo(long aJobsIDX, long aStatusCodeIDX, String aRECEIVER_NAME, String aCUSTID, String aDelBlock, String aPOSTAL, ArrayList<String> aAllDelUnits,
                            ArrayList<String> aAllDelUnitsCombinedList, String[] aCHECKLIST, ArrayList<String> aMOBILE_NOs, String aPIN) {
        this.JobsIDX = aJobsIDX;
        this.StatusCodeIDX = aStatusCodeIDX;
        this.RECEIVER_NAME = aRECEIVER_NAME;
        this.CUSTID = aCUSTID;
        this.DelBlock = aDelBlock;
        this.POSTAL = aPOSTAL;
        this.AllDelUnits = aAllDelUnits;
        this.AllDelUnitsCombinedList = aAllDelUnitsCombinedList;
        this.CHECKLIST = aCHECKLIST;
        this.MOBILE_NOsList = aMOBILE_NOs;
        this.PIN = aPIN;
    }

    public void addFLAG(int f) {
        if ((this.FLAG & f) != f) {
            this.FLAG = this.FLAG | f;
        }
    }

    public void removeFLAG(int f) {
        FLAG = FLAG & ~f;
    }

    public void addFLAG2(int f) {
        if ((this.FLAG2 & f) != f) {
            this.FLAG2 = this.FLAG2 | f;
        }
    }

    public void removeFLAG2(int f) {
        FLAG2 = FLAG2 & ~f;
    }

    public static jobSelectionInfo getjobSelectionInfo(objJobInfo ji, boolean first) {
        jobSelectionInfo jsi = null;
        try {
            long JobsIDX = ji.JobsIDX;
            long StatusCodeIDX = ji.StatusCodeIDX;
            boolean hasStdRun = JobsHandler.isStandardRunJob(ji);
            boolean hasOTP = JobsHandler.hasOTPwPin(ji);
            boolean CANNOT_SKIP_OTP = JobsHandler.cannotSkipOTP(ji);
            String CustID = ji.IDVerification;
            boolean HAS_CUSTID_VERIFICATION_REQ = JobsHandler.CustIDVerificationRequired(ji);
            boolean HAS_CUSTID_VERIFICATION_NOT_REQ = JobsHandler.CustIDVerificationNotRequired(ji);
            boolean HAS_CONTACTLESS = JobsHandler.isContactlessDelivery(ji) && !HAS_CUSTID_VERIFICATION_REQ;
            boolean IDVerificationRequired = HAS_CUSTID_VERIFICATION_REQ && !HAS_CUSTID_VERIFICATION_NOT_REQ && !HAS_CONTACTLESS;
            boolean HAS_CUSTID = !objGlobalMethods.IsEmptyString(CustID) && IDVerificationRequired;
            boolean HAS_CHECKLIST = JobsHandler.HasCheckList(ji.CheckList, "DS");
            boolean HAS_TRADE_IN = JobsHandler.hasTradeIn(ji);
            boolean HAS_SECURITY_SEAL = JobsHandler.JobHasSecuritySeal(ji, false);
            boolean HAS_REQ_STAFF_ID = objGlobalMethods.isReqStaffID(ji.FLAG);
            boolean HAS_REQ_NFC_SCAN = JobsHandler.reqNFCscan(ji);
            boolean HAS_DO_COD = JobsHandler.checkforDOCOD(ji);
            boolean HAS_RECEIVER_ONLY = JobsHandler.ReceiverOnly(ji);
            boolean IS_DUNNING = JobsHandler.isDunning(ji);
            boolean IS_LOCKER = JobsHandler.isLockerJob(ji);
            boolean IS_ALPS = JobsHandler.isAlpsClientIDX(ji);
            boolean IS_NORMALJOB = (!hasOTP && !HAS_CUSTID && !IDVerificationRequired && !HAS_CHECKLIST && !HAS_CONTACTLESS) ||
                    (!hasOTP && HAS_CUSTID && !IDVerificationRequired && !HAS_CHECKLIST && !HAS_CONTACTLESS);
            boolean ReqSign = (ji.FLAG & 1024) == 1024;
            boolean hasReviewLinkSMS = JobsHandler.hasReviewLinkSMS(ji);
            boolean hasReqItemProof = JobsHandler.hasReqItemProofImg(ji, null, 2);
            boolean cannotPhotoDL = JobsHandler.CannotTakePhoto(ji, 2);

            objAddressContact ac = JobsHandler3.getDelAddressContact(ji);
            String RECEIVER_NAME = "";
            String DelBlock = "";
            String DelUnit_original = "";
            String DelPostal = "";
            ArrayList<ArrayList<String>> AddressInfo = new ArrayList<>();
            ArrayList<String> AllDelUnits = new ArrayList<>();
            ArrayList<String> AllDelUnitsCombined = new ArrayList<>();
            if (ac != null && ac.AddressStructure != null) {
                DelBlock = !objGlobalMethods.IsEmptyString(ac.AddressStructure.BLOCK) ? ac.AddressStructure.BLOCK : "";
                DelUnit_original = !objGlobalMethods.IsEmptyString(ac.AddressStructure.UNIT) ? ac.AddressStructure.UNIT : "";
                DelPostal = !objGlobalMethods.IsEmptyString(ac.AddressStructure.POSTALCODE) ? ac.AddressStructure.POSTALCODE : "";
                AddressInfo = JobsHandler3.returnAllUnitsArrayList(DelUnit_original);
                AllDelUnits = AddressInfo.get(0);
                AllDelUnitsCombined = AddressInfo.get(1);
            }

            if (ac != null && ac.ContactStructure != null) {
                RECEIVER_NAME = !objGlobalMethods.IsEmptyString(ac.ContactStructure.NAME) ? ac.ContactStructure.NAME : "";
            }

            //boolean hasSpecialCOD = JobsHandler2.checkforSpecialDOCO(ji);
            String[] Checklist = HAS_CHECKLIST ? ji.CheckList : null;

            String JobContactNumbers = JobsHandler.getValidTelNumsFromJobNew(ji, 2);
            if (objGlobalMethods.IsEmptyString(JobContactNumbers)) {
                JobContactNumbers = JobsHandler.getTelNumsFromJobNoValidation(ji, 2);
            }
            ArrayList<String> MobileNo = JobsHandler3.returnALlValidSGMobileNumArrayList(JobContactNumbers);
            String PIN = !objGlobalMethods.IsEmptyString(ji.PIN) ? ji.PIN : "";

            jsi = new jobSelectionInfo(JobsIDX, StatusCodeIDX, RECEIVER_NAME, CustID, DelBlock, DelPostal, AllDelUnits, AllDelUnitsCombined, Checklist, MobileNo, PIN);

            jsi.setHAS_STDRUNCount(hasStdRun ? 1 : 0);
            jsi.setHAS_OTPCount(hasOTP ? 1 : 0);
            if (jsi.HAS_OTP && !objGlobalMethods.IsEmptyString(ji.PIN) && first)
                jsi.addOTPList(ji.PIN);

            jsi.setCANNOT_SKIP_OTPCount(CANNOT_SKIP_OTP ? 1 : 0);
            jsi.setHAS_CUSTIDCount(HAS_CUSTID ? 1 : 0);
            jsi.setHAS_CUSTID_VERIFICATION_REQCount(HAS_CUSTID_VERIFICATION_REQ ? 1 : 0);
            jsi.setHAS_CUSTID_VERIFICATION_NOT_REQCount(HAS_CUSTID_VERIFICATION_NOT_REQ ? 1 : 0);
            jsi.setHAS_CONTACTLESSCount(HAS_CONTACTLESS ? 1 : 0);

            jsi.setHAS_CHECKLISTCount(HAS_CHECKLIST ? 1 : 0);
            jsi.setHAS_TRADE_INCount(HAS_TRADE_IN ? 1 : 0);
            jsi.setHAS_SECURITY_SEALCount(HAS_SECURITY_SEAL ? 1 : 0);
            jsi.setHAS_REQ_STAFF_IDCount(HAS_REQ_STAFF_ID ? 1 : 0);
            jsi.setHAS_REQ_NFC_SCANCount(HAS_REQ_NFC_SCAN ? 1 : 0);
            jsi.setHAS_DO_CODCount(HAS_DO_COD ? 1 : 0);
            jsi.setHAS_RECEIVER_ONLYCount(HAS_RECEIVER_ONLY ? 1 : 0);
            jsi.setIS_DUNNINGCount(IS_DUNNING ? 1 : 0);
            jsi.setIS_LOCKERCount(IS_LOCKER ? 1 : 0);
            jsi.setIS_ALPSCount(IS_ALPS ? 1 : 0);
            //jsi.setIS_NORMALJOBCount(IS_NORMALJOB ? 1 : 0);
            jsi.setIS_NORMALJOB(IS_NORMALJOB);

            jsi.setCanSlotLBCount(objGlobalMethods.isCanSlotLB(ji.FLAG) ? 1 : 0);
            jsi.setCanSlotUnderDoorCount(objGlobalMethods.isCanSlotUnderDoor(ji.FLAG) ? 1 : 0);
            jsi.setCanGuardCount(objGlobalMethods.isCanGuard(ji.FLAG) ? 1 : 0);
            jsi.setCanLeaveDoorStepCount(JobsHandler2.CanLeaveDoorStep(ji.FLAG2) ? 1 : 0);

            jsi.setReviewLinkSMSCount(hasReviewLinkSMS ? 1 : 0);
            jsi.setReqSignCount(ReqSign ? 1 : 0);
            jsi.sethasReqItemProofCount(hasReqItemProof ? 1 : 0);
            jsi.setHAS_CANNOT_PHOTO_DLCount(cannotPhotoDL ? 1 : 0);

            if (jsi.HAS_OTP)
                jsi.addFLAG(512);
            if (jsi.CANNOT_SKIP_OTP)
                jsi.addFLAG(8192);
            if (jsi.HAS_CONTACTLESS)
                jsi.addFLAG(67108864);
            if (jsi.HAS_REQ_STAFF_ID)
                jsi.addFLAG(4096);
            if (jsi.HAS_REQ_NFC_SCAN)
                jsi.addFLAG(262144);
            if (jsi.HAS_RECEIVER_ONLY)
                jsi.addFLAG(16);
            if (jsi.ReqSign)
                jsi.addFLAG(1024);
            if (jsi.ReviewLinkSMS)
                jsi.addFLAG(16777216);
            if (jsi.hasReqItemProof)
                jsi.addFLAG(536870912);
            if (jsi.CanSlotLB)
                jsi.addFLAG(1);
            if (jsi.CanSlotUnderDoor)
                jsi.addFLAG(2);
            if (jsi.CanGuard)
                jsi.addFLAG(4);

            if (jsi.HAS_CUSTID_VERIFICATION_REQ)
                jsi.addFLAG2(128);
            if (jsi.HAS_CUSTID_VERIFICATION_NOT_REQ)
                jsi.addFLAG2(32);
            if (jsi.CanLeaveDoorStep)
                jsi.addFLAG2(2048);


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo getjobSelectionInfo. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return jsi;
        }
    }

    public void setFieldsFromj2(jobSelectionInfo j2) {
        try {
            this.setHAS_STDRUNCount(j2.HAS_STDRUNCount);
            this.setHAS_OTPCount(j2.HAS_OTPCount);
            if (j2.HAS_OTP && !objGlobalMethods.IsEmptyString(j2.PIN))
                this.addOTPList(j2.PIN);

            if (j2.HAS_OTP)
                this.addMOBILE_NOsListByArrayList(j2.MOBILE_NOsList);

            this.setCANNOT_SKIP_OTPCount(j2.CANNOT_SKIP_OTPCount);
            this.setHAS_CUSTIDCount(j2.HAS_CUSTIDCount);
            this.setHAS_CUSTID_VERIFICATION_REQCount(j2.HAS_CUSTID_VERIFICATION_REQCount);
            this.setHAS_CUSTID_VERIFICATION_NOT_REQCount(j2.HAS_CUSTID_VERIFICATION_NOT_REQCount);
            this.setHAS_CONTACTLESSCount(j2.HAS_CONTACTLESSCount);

            this.setHAS_CHECKLISTCount(j2.HAS_CHECKLISTCount);
            this.setHAS_TRADE_INCount(j2.HAS_TRADE_INCount);
            this.setHAS_SECURITY_SEALCount(j2.HAS_SECURITY_SEALCount);
            this.setHAS_REQ_STAFF_IDCount(j2.HAS_REQ_STAFF_IDCount);
            this.setHAS_REQ_NFC_SCANCount(j2.HAS_REQ_NFC_SCANCount);
            this.setHAS_DO_CODCount(j2.HAS_DO_CODCount);
            this.setHAS_RECEIVER_ONLYCount(j2.HAS_RECEIVER_ONLYCount);
            this.setIS_DUNNINGCount(j2.IS_DUNNINGCount);
            this.setIS_LOCKERCount(j2.IS_LOCKERCount);
            this.setIS_ALPSCount(j2.IS_ALPSCount);
            //this.setIS_NORMALJOBCount(j2.IS_NORMALJOBCount);

            this.setCanSlotLBCount(j2.CanSlotLBCount);
            this.setCanSlotUnderDoorCount(j2.CanSlotUnderDoorCount);
            this.setCanGuardCount(j2.CanGuardCount);
            this.setCanLeaveDoorStepCount(j2.CanLeaveDoorStepCount);

            this.setReviewLinkSMSCount(j2.ReviewLinkSMSCount);
            this.setReqSignCount(j2.ReqSignCount);
            this.sethasReqItemProofCount(j2.hasReqItemProofCount);
            this.setHAS_CANNOT_PHOTO_DLCount(j2.HAS_CANNOT_PHOTO_DLCount);

            if (j2.HAS_OTP)
                this.addFLAG(512);
            if (j2.CANNOT_SKIP_OTP)
                this.addFLAG(8192);
            if (j2.HAS_CONTACTLESS)
                this.addFLAG(67108864);
            if (j2.HAS_REQ_STAFF_ID)
                this.addFLAG(4096);
            if (j2.HAS_REQ_NFC_SCAN)
                this.addFLAG(262144);
            if (j2.HAS_RECEIVER_ONLY)
                this.addFLAG(16);
            if (j2.ReqSign)
                this.addFLAG(1024);
            if (j2.ReviewLinkSMS)
                this.addFLAG(16777216);
            if (j2.hasReqItemProof)
                this.addFLAG(536870912);
            if (j2.CanSlotLB)
                this.addFLAG(1);
            if (j2.CanSlotUnderDoor)
                this.addFLAG(2);
            if (j2.CanGuard)
                this.addFLAG(4);

            if (j2.HAS_CUSTID_VERIFICATION_REQ)
                this.addFLAG2(128);
            if (j2.HAS_CUSTID_VERIFICATION_NOT_REQ)
                this.addFLAG2(32);
            if (j2.CanLeaveDoorStep)
                this.addFLAG2(2048);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo setFieldsFromj2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void removeFieldsFromj2(jobSelectionInfo j2) {
        try {
            this.setHAS_STDRUNCount(j2.HAS_STDRUN ? -1 : 0);
            this.setHAS_OTPCount(j2.HAS_OTP ? -1 : 0);
            this.setCANNOT_SKIP_OTPCount(j2.CANNOT_SKIP_OTP ? -1 : 0);
            this.setHAS_CUSTIDCount(j2.HAS_CUSTID ? -1 : 0);
            this.setHAS_CUSTID_VERIFICATION_REQCount(j2.HAS_CUSTID_VERIFICATION_REQ ? -1 : 0);
            this.setHAS_CUSTID_VERIFICATION_NOT_REQCount(j2.HAS_CUSTID_VERIFICATION_NOT_REQ ? -1 : 0);
            this.setHAS_CONTACTLESSCount(j2.HAS_CONTACTLESS ? -1 : 0);

            this.setHAS_CHECKLISTCount(j2.HAS_CHECKLIST ? -1 : 0);
            this.setHAS_TRADE_INCount(j2.HAS_TRADE_IN ? -1 : 0);
            this.setHAS_SECURITY_SEALCount(j2.HAS_SECURITY_SEAL ? -1 : 0);
            this.setHAS_REQ_STAFF_IDCount(j2.HAS_REQ_STAFF_ID ? -1 : 0);
            this.setHAS_REQ_NFC_SCANCount(j2.HAS_REQ_NFC_SCAN ? -1 : 0);
            this.setHAS_DO_CODCount(j2.HAS_DO_COD ? -1 : 0);
            this.setHAS_RECEIVER_ONLYCount(j2.HAS_RECEIVER_ONLY ? -1 : 0);
            this.setIS_DUNNINGCount(j2.IS_DUNNING ? -1 : 0);
            this.setIS_LOCKERCount(j2.IS_LOCKER ? -1 : 0);
            this.setIS_ALPSCount(j2.IS_ALPS ? -1 : 0);
            //this.setIS_NORMALJOBCount(j2.IS_NORMALJOB ? -1 : 0);

            this.setCanSlotLBCount(j2.CanSlotLB ? -1 : 0);
            this.setCanSlotUnderDoorCount(j2.CanSlotUnderDoor ? -1 : 0);
            this.setCanGuardCount(j2.CanGuard ? -1 : 0);
            this.setCanLeaveDoorStepCount(j2.CanLeaveDoorStep ? -1 : 0);

            this.setReviewLinkSMSCount(j2.ReviewLinkSMS ? -1 : 0);
            this.setReqSignCount(j2.ReqSign ? -1 : 0);
            this.sethasReqItemProofCount(j2.hasReqItemProof ? -1 : 0);
            this.setHAS_CANNOT_PHOTO_DLCount(j2.HAS_CANNOT_PHOTO_DL ? -1 : 0);

            if (j2.HAS_OTP && !objGlobalMethods.IsEmptyString(j2.PIN))
                this.removeOTPList(j2.PIN);
            if (j2.HAS_OTP)
                this.removeMOBILE_NOsListByArrayList(j2.MOBILE_NOsList);

            if (!this.HAS_OTP)
                this.removeFLAG(512);
            if (!this.CANNOT_SKIP_OTP)
                this.removeFLAG(8192);
            if (!this.HAS_CONTACTLESS)
                this.removeFLAG(67108864);
            if (!this.HAS_REQ_STAFF_ID)
                this.removeFLAG(4096);
            if (!this.HAS_REQ_NFC_SCAN)
                this.removeFLAG(262144);
            if (!this.HAS_RECEIVER_ONLY)
                this.removeFLAG(16);
            if (!this.ReqSign)
                this.removeFLAG(1024);
            if (!this.ReviewLinkSMS)
                this.removeFLAG(16777216);
            if (!this.hasReqItemProof)
                this.removeFLAG(536870912);
            if (!this.HAS_CANNOT_PHOTO_DL)
                this.removeFLAG(65536);
            if (!this.CanSlotLB)
                this.removeFLAG(1);
            if (!this.CanSlotUnderDoor)
                this.removeFLAG(2);
            if (!this.CanGuard)
                this.removeFLAG(4);

            if (!this.HAS_CUSTID_VERIFICATION_REQ)
                this.removeFLAG2(128);
            if (!this.HAS_CUSTID_VERIFICATION_NOT_REQ)
                this.removeFLAG2(32);
            if (!this.CanLeaveDoorStep)
                this.removeFLAG2(2048);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo removeFieldsFromj2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public boolean canMassWithOthers() {
        try {
            return (!this.HAS_DO_COD &&
                    !this.HAS_REQ_NFC_SCAN &&
                    !this.HAS_REQ_STAFF_ID &&
                    !this.HAS_SECURITY_SEAL &&
                    !this.HAS_TRADE_IN &&
                    !this.HAS_RECEIVER_ONLY &&
                    !this.IS_LOCKER &&
                    !this.HAS_CONTACTLESS &&
                    !this.IS_DUNNING);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            return false;
        }
    }

    public boolean canMass(jobSelectionInfo j2) {
        ////"this" is always the 1st job selected, as a base checker
        try {
            if (!this.canMassWithOthers())
                return false;

            if (j2 == null)
                return false;

            if (!j2.canMassWithOthers())
                return false;

            if ((this.IS_ALPS && !j2.IS_ALPS) || (!this.IS_ALPS && j2.IS_ALPS))
                return false;

            if (this.IS_ALPS && j2.IS_ALPS) {
                if (!verifyPostalBlkUnit(j2))
                    return false;

                /*if ((this.HAS_OTP && this.CANNOT_SKIP_OTP) || (j2.HAS_OTP && j2.CANNOT_SKIP_OTP))
                    return false;*/

                /*if ((this.HAS_OTP || j2.HAS_OTP) && !this.ContactNumberExistIn(j2.MOBILE_NOsList))
                    return false;*/
                if ((this.HAS_OTP && j2.HAS_OTP) && !this.ContactNumberExistIn(j2.MOBILE_NOsList))
                    return false;

                if ((this.HAS_OTP && j2.HAS_OTP) && this.PIN.length() != j2.PIN.length())
                    return false;

                if (this.CHECKLIST != null && j2.CHECKLIST != null && !this.hasSameChecklist(j2.CHECKLIST))
                    return false;
            } else if (!this.IS_ALPS && !j2.IS_ALPS) {
                ////non alps jobs
                if ((!this.IS_NORMALJOB || !j2.IS_NORMALJOB) && (!verifyPostalBlkUnit(j2)))
                    return false;

                if ((!this.IS_NORMALJOB || !j2.IS_NORMALJOB) && NameUtils.nameSimilarity(this.RECEIVER_NAME, j2.RECEIVER_NAME) < 75f)
                    return false;

                if ((this.HAS_OTP || j2.HAS_OTP) && !this.ContactNumberExistIn(j2.MOBILE_NOsList))
                    return false;

                if ((this.HAS_OTP && j2.HAS_OTP) && this.PIN.length() != j2.PIN.length())
                    return false;

                /*if ((this.HAS_STDRUN && !j2.HAS_STDRUN) || (!this.HAS_STDRUN && j2.HAS_STDRUN))
                    return false;*/

                if (this.HAS_STDRUN ^ j2.HAS_STDRUN) //same logic as above, cleaner version
                    return false;

                boolean j1ReqCustIDVerification = this.HAS_CUSTID_VERIFICATION_REQ && !this.HAS_CUSTID_VERIFICATION_NOT_REQ;
                boolean j2ReqCustIDVerification = this.HAS_CUSTID_VERIFICATION_REQ && !this.HAS_CUSTID_VERIFICATION_NOT_REQ;

                if (!this.HAS_CUSTID && j2.HAS_CUSTID && j2ReqCustIDVerification) {
                    //do nothing
                }

                if (this.HAS_CUSTID && !j2.HAS_CUSTID && j1ReqCustIDVerification) {
                    //do nothing
                }

                if (this.HAS_CUSTID && j1ReqCustIDVerification && j2.HAS_CUSTID && j2ReqCustIDVerification && !objGlobalMethods.IsEqualString(this.CUSTID, j2.CUSTID))
                    return false;

                if (!this.HAS_CUSTID && j2.HAS_CUSTID)
                    this.setCUSTID(j2.CUSTID);

                if (this.CHECKLIST != null && this.CHECKLIST.length > 0 && j2.CHECKLIST != null && j2.CHECKLIST.length > 0 && !this.hasSameChecklist(j2.CHECKLIST))
                    return false;

                if ((this.CHECKLIST == null || this.CHECKLIST != null && this.CHECKLIST.length == 0) && j2.CHECKLIST != null && j2.CHECKLIST.length > 0)
                    this.setCHECKLIST(j2.CHECKLIST);


            }
            return true;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            return false;
        }
    }

    public Boolean verifyPostalBlkUnit(jobSelectionInfo j2) {
        boolean asd = false;
        boolean emptyPostal1 = false;
        boolean emptyPostal2 = false;
        boolean emptyBlk1 = false;
        boolean emptyBlk2 = false;
        boolean emptyAllDelUnitsCombinedList1 = false;
        boolean emptyAllDelUnitsCombinedList2 = false;
        try {
            emptyPostal1 = objGlobalMethods.IsEmptyString(this.POSTAL);
            emptyPostal2 = objGlobalMethods.IsEmptyString(j2.POSTAL);
            emptyBlk1 = objGlobalMethods.IsEmptyString(this.DelBlock);
            emptyBlk2 = objGlobalMethods.IsEmptyString(j2.DelBlock);
            emptyAllDelUnitsCombinedList1 = ((this.AllDelUnitsCombinedList == null || (this.AllDelUnitsCombinedList != null && this.AllDelUnitsCombinedList.size() == 0)));
            emptyAllDelUnitsCombinedList2 = ((j2.AllDelUnitsCombinedList == null || (j2.AllDelUnitsCombinedList != null && j2.AllDelUnitsCombinedList.size() == 0)));

            if (emptyPostal1 || emptyPostal2)
                return false;

            if (!objGlobalMethods.IsEqualString(this.POSTAL, j2.POSTAL))
                return false;

            if (emptyBlk1 || emptyBlk2)
                return false;

            if (emptyAllDelUnitsCombinedList1 && emptyAllDelUnitsCombinedList2) {
                asd = objGlobalMethods.IsEqualString(this.DelBlock, j2.DelBlock);
                if (!asd)
                    return false;
            }

            if (!emptyAllDelUnitsCombinedList1 && !emptyAllDelUnitsCombinedList2) {
                ArrayList main = this.AllDelUnitsCombinedList.size() > j2.AllDelUnitsCombinedList.size() ? this.AllDelUnitsCombinedList : j2.AllDelUnitsCombinedList;
                ArrayList sub = j2.AllDelUnitsCombinedList.size() > this.AllDelUnitsCombinedList.size() ? j2.AllDelUnitsCombinedList : this.AllDelUnitsCombinedList;
                asd = false;
                for (int i = 0; i <= sub.size() - 1; i++) {
                    if (main.contains(sub.get(i))) {
                        asd = true;
                        break;
                    }
                }
                if (!asd)
                    return false;
            }
            return true;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo verifyPostalBlkUnit. Exception: " + e.getMessage(), "", "", "", "");
            return false;
        }
    }

    public boolean ContactNumberExistIn(ArrayList<String> mob) {
        boolean asd = false;
        try {
            if (this.MOBILE_NOsList != null && this.MOBILE_NOsList.size() > 0 && mob != null && mob.size() > 0) {
                for (int i = 0; i <= mob.size() - 1; i++) {
                    if (this.MOBILE_NOsList.contains(mob.get(i))) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo ContactNumberExistIn. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public boolean hasSameChecklist(String[] cl) {
        //if you want to check whether two String[] arrays have exactly the same length and the same values in each corresponding index (order matters)
        boolean asd = false;
        try {
            String[] listA = this.CHECKLIST;
            String[] listB = cl;

            // Convert both arrays to uppercase
            listA = Arrays.stream(listA)
                    .map(String::toUpperCase)
                    .toArray(String[]::new);

            listB = Arrays.stream(listB)
                    .map(String::toUpperCase)
                    .toArray(String[]::new);

            asd =  Arrays.equals(listA, listB);

            /*if (Arrays.equals(CHECKLIST, cl)) {
                // They are the same
                asd = true;
            } else {
                // They are different
            }*/
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo hasSameChecklist. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }


    public void setHAS_STDRUNCount(int c) {
        this.HAS_STDRUNCount += c;
        if (this.HAS_STDRUNCount < 0)
            this.HAS_STDRUNCount = 0;
        this.HAS_STDRUN = (this.HAS_STDRUNCount > 0);
    }

    public void setHAS_OTPCount(int c) {
        this.HAS_OTPCount += c;
        if (this.HAS_OTPCount < 0)
            this.HAS_OTPCount = 0;
        this.HAS_OTP = (this.HAS_OTPCount > 0);
    }

    public void setCANNOT_SKIP_OTPCount(int c) {
        this.CANNOT_SKIP_OTPCount += c;
        if (this.CANNOT_SKIP_OTPCount < 0)
            this.CANNOT_SKIP_OTPCount = 0;
        this.CANNOT_SKIP_OTP = (this.CANNOT_SKIP_OTPCount > 0);
    }

    public void setHAS_CUSTIDCount(int c) {
        this.HAS_CUSTIDCount += c;
        if (this.HAS_CUSTIDCount < 0)
            this.HAS_CUSTIDCount = 0;
        this.HAS_CUSTID = (this.HAS_CUSTIDCount > 0);
    }

    public void setHAS_CUSTID_VERIFICATION_REQCount(int c) {
        this.HAS_CUSTID_VERIFICATION_REQCount += c;
        if (this.HAS_CUSTID_VERIFICATION_REQCount < 0)
            this.HAS_CUSTID_VERIFICATION_REQCount = 0;
        this.HAS_CUSTID_VERIFICATION_REQ = (this.HAS_CUSTID_VERIFICATION_REQCount > 0);
    }

    public void setHAS_CUSTID_VERIFICATION_NOT_REQCount(int c) {
        this.HAS_CUSTID_VERIFICATION_NOT_REQCount += c;
        if (this.HAS_CUSTID_VERIFICATION_NOT_REQCount < 0)
            this.HAS_CUSTID_VERIFICATION_NOT_REQCount = 0;
        this.HAS_CUSTID_VERIFICATION_NOT_REQ = (this.HAS_CUSTID_VERIFICATION_NOT_REQCount > 0);
    }

    public void setHAS_CONTACTLESSCount(int c) {
        this.HAS_CONTACTLESSCount += c;
        if (this.HAS_CONTACTLESSCount < 0)
            this.HAS_CONTACTLESSCount = 0;
        this.HAS_CONTACTLESS = (this.HAS_CONTACTLESSCount > 0);
    }

    public void setHAS_CHECKLISTCount(int c) {
        this.HAS_CHECKLISTCount += c;
        if (this.HAS_CHECKLISTCount < 0)
            this.HAS_CHECKLISTCount = 0;
        this.HAS_CHECKLIST = (this.HAS_CHECKLISTCount > 0);
    }

    public void setHAS_TRADE_INCount(int c) {
        this.HAS_TRADE_INCount += c;
        if (this.HAS_TRADE_INCount < 0)
            this.HAS_TRADE_INCount = 0;
        this.HAS_TRADE_IN = (this.HAS_TRADE_INCount > 0);
    }

    public void setHAS_SECURITY_SEALCount(int c) {
        this.HAS_SECURITY_SEALCount += c;
        if (this.HAS_SECURITY_SEALCount < 0)
            this.HAS_SECURITY_SEALCount = 0;
        this.HAS_SECURITY_SEAL = (this.HAS_SECURITY_SEALCount > 0);
    }

    public void setHAS_REQ_STAFF_IDCount(int c) {
        this.HAS_REQ_STAFF_IDCount += c;
        if (this.HAS_REQ_STAFF_IDCount < 0)
            this.HAS_REQ_STAFF_IDCount = 0;
        this.HAS_REQ_STAFF_ID = (this.HAS_REQ_STAFF_IDCount > 0);
    }

    public void setHAS_REQ_NFC_SCANCount(int c) {
        this.HAS_REQ_NFC_SCANCount += c;
        if (this.HAS_REQ_NFC_SCANCount < 0)
            this.HAS_REQ_NFC_SCANCount = 0;
        this.HAS_REQ_NFC_SCAN = (this.HAS_REQ_NFC_SCANCount > 0);
    }

    public void setHAS_DO_CODCount(int c) {
        this.HAS_DO_CODCount += c;
        if (this.HAS_DO_CODCount < 0)
            this.HAS_DO_CODCount = 0;
        this.HAS_DO_COD = (this.HAS_DO_CODCount > 0);
    }

    public void setHAS_RECEIVER_ONLYCount(int c) {
        this.HAS_RECEIVER_ONLYCount += c;
        if (this.HAS_RECEIVER_ONLYCount < 0)
            this.HAS_RECEIVER_ONLYCount = 0;
        this.HAS_RECEIVER_ONLY = (this.HAS_RECEIVER_ONLYCount > 0);
    }

    public void setIS_DUNNINGCount(int c) {
        this.IS_DUNNINGCount += c;
        if (this.IS_DUNNINGCount < 0)
            this.IS_DUNNINGCount = 0;
        this.IS_DUNNING = (this.IS_DUNNINGCount > 0);
    }

    public void setIS_LOCKERCount(int c) {
        this.IS_LOCKERCount += c;
        if (this.IS_LOCKERCount < 0)
            this.IS_LOCKERCount = 0;
        this.IS_LOCKER = (this.IS_LOCKERCount > 0);
    }

    public void setIS_ALPSCount(int c) {
        this.IS_ALPSCount += c;
        if (this.IS_ALPSCount < 0)
            this.IS_ALPSCount = 0;
        this.IS_ALPS = (this.IS_ALPSCount > 0);
    }

    public void setIS_NORMALJOBCount(int c) {
        this.IS_NORMALJOBCount += c;
        if (this.IS_NORMALJOBCount < 0)
            this.IS_NORMALJOBCount = 0;
        //this.IS_NORMALJOB = (this.IS_NORMALJOBCount > 0);
    }

    public void setIS_NORMALJOB(boolean IS_NORMALJOB) {
        this.IS_NORMALJOB = IS_NORMALJOB;
    }

    public void setIS_DOCCount(int c) {
        this.IS_DOCCount += c;
        if (this.IS_DOCCount < 0)
            this.IS_DOCCount = 0;
        this.IS_DOC = (this.IS_DOCCount > 0);
    }

    public void sethasReqItemProofCount(int c) {
        this.hasReqItemProofCount += c;
        if (this.hasReqItemProofCount < 0)
            this.hasReqItemProofCount = 0;
        this.hasReqItemProof = (this.hasReqItemProofCount > 0);
    }

    public void setCanSlotLBUnderDoorGuard(Integer jobFlag, Integer jobFlag2) {
        this.setCanSlotLBCount(objGlobalMethods.isCanSlotLB(jobFlag) ? 1 : 0);
        this.setCanSlotUnderDoorCount(objGlobalMethods.isCanSlotUnderDoor(jobFlag) ? 1 : 0);
        this.setCanGuardCount(objGlobalMethods.isCanGuard(jobFlag) ? 1 : 0);
        this.setCanLeaveDoorStepCount(JobsHandler2.CanLeaveDoorStep(jobFlag2) ? 1 : 0);
    }

    public void setCanSlotLBCount(int c) {
        this.CanSlotLBCount += c;
        if (this.CanSlotLBCount < 0)
            this.CanSlotLBCount = 0;
        this.CanSlotLB = (this.CanSlotLBCount > 0);
    }

    public void setCanSlotUnderDoorCount(int c) {
        this.CanSlotUnderDoorCount += c;
        if (this.CanSlotUnderDoorCount < 0)
            this.CanSlotUnderDoorCount = 0;
        this.CanSlotUnderDoor = (this.CanSlotUnderDoorCount > 0);
    }

    public void setCanGuardCount(int c) {
        this.CanGuardCount += c;
        if (this.CanGuardCount < 0)
            this.CanGuardCount = 0;
        this.CanGuard = (this.CanGuardCount > 0);
    }

    public void setCanLeaveDoorStepCount(int c) {
        this.CanLeaveDoorStepCount += c;
        if (this.CanLeaveDoorStepCount < 0)
            this.CanLeaveDoorStepCount = 0;
        this.CanLeaveDoorStep = (this.CanLeaveDoorStepCount > 0);
    }

    public void setReviewLinkSMSCount(int c) {
        this.ReviewLinkSMSCount += c;
        if (this.ReviewLinkSMSCount < 0)
            this.ReviewLinkSMSCount = 0;
        this.ReviewLinkSMS = (this.ReviewLinkSMSCount > 0);
    }

    public void setReqSignCount(int c) {
        this.ReqSignCount += c;
        if (this.ReqSignCount < 0)
            this.ReqSignCount = 0;
        this.ReqSign = (this.ReqSignCount > 0);
    }

    public void setNotAllowedSelectionCount(int c) {
        this.NotAllowedSelectionCount += c;
        if (this.NotAllowedSelectionCount < 0)
            this.NotAllowedSelectionCount = 0;
        this.NotAllowedSelection = (this.NotAllowedSelectionCount > 0);
    }

    public void setHAS_CANNOT_PHOTO_DLCount(int c) {
        this.HAS_CANNOT_PHOTO_DLCount += c;
        if (this.HAS_CANNOT_PHOTO_DLCount < 0)
            this.HAS_CANNOT_PHOTO_DLCount = 0;
        this.HAS_CANNOT_PHOTO_DL = (this.HAS_CANNOT_PHOTO_DLCount > 0);
    }


    public void removeMOBILE_NOsListByArrayList(ArrayList<String> list) {
        try {
            if (this.MOBILE_NOsList == null)
                return;

            String n = "";
            if (list != null && list.size() > 0) {
                for (int i = 0; i <= list.size() - 1; i++) {
                    n = list.get(i);
                    if (this.MOBILE_NOsList.contains(n))
                        this.MOBILE_NOsList.remove(n);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo removeMOBILE_NOsListByArrayList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void addMOBILE_NOsListByArrayList(ArrayList<String> list) {
        try {
            if (MOBILE_NOsList == null)
                MOBILE_NOsList = new ArrayList<>();
            /*if (list != null && list.size() > 0) {
                for (int i = 0; i <= list.size() - 1; i++) {
                    if (!MOBILE_NOsList.contains(list.get(i)))
                        MOBILE_NOsList.add(list.get(i));
                }
            }*/
            if (list != null && list.size() > 0) {
                for (int i = 0; i <= list.size() - 1; i++) {
                    MOBILE_NOsList.add(list.get(i));
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo addMOBILE_NOsListByArrayList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void addMOBILE_NOsList(String aMobileNo) {
        try {
            if (MOBILE_NOsList == null)
                MOBILE_NOsList = new ArrayList<>();
            if (!MOBILE_NOsList.contains(aMobileNo))
                MOBILE_NOsList.add(aMobileNo);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo addMOBILE_NOsList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void removeMOBILE_NOsList(String aMobileNo) {
        try {
            if (MOBILE_NOsList == null)
                MOBILE_NOsList = new ArrayList<>();
            if (MOBILE_NOsList.contains(aMobileNo))
                MOBILE_NOsList.remove(aMobileNo);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo removeMOBILE_NOsList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void addAllDelUnitsCombinedList(String aAllDelUnitsCombined) {
        try {
            if (AllDelUnitsCombinedList == null)
                AllDelUnitsCombinedList = new ArrayList<>();
            if (!AllDelUnitsCombinedList.contains(aAllDelUnitsCombined))
                AllDelUnitsCombinedList.add(aAllDelUnitsCombined);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo addAllDelUnitsCombinedList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void removeAllDelUnitsCombinedList(String aAllDelUnitsCombined) {
        try {
            if (AllDelUnitsCombinedList == null)
                AllDelUnitsCombinedList = new ArrayList<>();
            if (AllDelUnitsCombinedList.contains(aAllDelUnitsCombined))
                AllDelUnitsCombinedList.remove(aAllDelUnitsCombined);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo removeAllDelUnitsCombinedList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void addOTPList(String aPin) {
        try {
            if (OTPList == null)
                OTPList = new ArrayList<>();
            if (!OTPList.contains(aPin))
                OTPList.add(aPin);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo addOTPList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void removeOTPList(String aPin) {
        try {
            if (OTPList == null)
                OTPList = new ArrayList<>();
            if (OTPList.contains(aPin))
                OTPList.remove(aPin);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo removeOTPList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void addShpmtV_RefList(String aShpmtV_Ref) {
        try {
            if (ShpmtV_RefList == null)
                ShpmtV_RefList = new ArrayList<>();
            if (!ShpmtV_RefList.contains(aShpmtV_Ref))
                ShpmtV_RefList.add(aShpmtV_Ref);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo addShpmtV_RefList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void removeShpmtV_RefList(String aShpmtV_Ref) {
        try {
            if (ShpmtV_RefList == null)
                ShpmtV_RefList = new ArrayList<>();
            if (ShpmtV_RefList.contains(aShpmtV_Ref))
                ShpmtV_RefList.remove(aShpmtV_Ref);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo removeShpmtV_RefList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void addJobIDXList(long JobsIDX) {
        try {
            if (JobIDXList == null)
                JobIDXList = new ArrayList<>();
            if (!JobIDXList.contains(JobsIDX))
                JobIDXList.add(JobsIDX);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo addJobIDXList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void addInvalidJobIDXList(long JobsIDX) {
        try {
            if (InvalidJobIDXList == null)
                InvalidJobIDXList = new ArrayList<>();
            if (!InvalidJobIDXList.contains(JobsIDX))
                InvalidJobIDXList.add(JobsIDX);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo addInvalidJobIDXList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void removeInvalidJobIDXList(long JobsIDX) {
        try {
            if (InvalidJobIDXList == null)
                return;
            if (InvalidJobIDXList.contains(JobsIDX))
                InvalidJobIDXList.remove(JobsIDX);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo removeInvalidJobIDXList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void addSelectedValidJobIDXList(long JobsIDX) {
        try {
            if (SelectedValidJobIDXList == null)
                SelectedValidJobIDXList = new ArrayList<>();
            if (!SelectedValidJobIDXList.contains(JobsIDX))
                SelectedValidJobIDXList.add(JobsIDX);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo addSelectedValidJobIDXList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void removeSelectedValidJobIDXList(long JobsIDX) {
        try {
            if (SelectedValidJobIDXList == null)
                return;
            if (SelectedValidJobIDXList.contains(JobsIDX))
                SelectedValidJobIDXList.remove(JobsIDX);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo removeSelectedValidJobIDXList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void addSelectedInvalidJobIDXList(long JobsIDX) {
        try {
            if (SelectedInvalidJobIDXList == null)
                SelectedInvalidJobIDXList = new ArrayList<>();
            if (!SelectedInvalidJobIDXList.contains(JobsIDX))
                SelectedInvalidJobIDXList.add(JobsIDX);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo addSelectedInvalidJobIDXList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void removeSelectedInvalidJobIDXList(long JobsIDX) {
        try {
            if (SelectedInvalidJobIDXList == null)
                return;
            if (SelectedInvalidJobIDXList.contains(JobsIDX))
                SelectedInvalidJobIDXList.remove(JobsIDX);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo removeSelectedInvalidJobIDXList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void clearLists() {
        clearInvalidJobIDXList();
        clearSelectedValidJobIDXList();
        clearSelectedInvalidJobIDXList();
    }

    public void clearInvalidJobIDXList() {
        if (InvalidJobIDXList != null)
            InvalidJobIDXList.clear();
    }

    public void clearSelectedValidJobIDXList() {
        if (SelectedValidJobIDXList != null)
            SelectedValidJobIDXList.clear();
    }

    public void clearSelectedInvalidJobIDXList() {
        if (SelectedInvalidJobIDXList != null)
            SelectedInvalidJobIDXList.clear();
    }

    public boolean isValidJobsIDX(long JobsIDX) {
        boolean asd = false;
        try {
            asd = (SelectedValidJobIDXList != null && SelectedValidJobIDXList.size() > 0 && SelectedValidJobIDXList.contains(JobsIDX));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo isValidJobsIDX. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public boolean isInvalidJobsIDX(long JobsIDX) {
        boolean asd = true; //default true on purpose in case of exception
        try {
            asd = (SelectedInvalidJobIDXList != null && SelectedInvalidJobIDXList.size() > 0 && SelectedInvalidJobIDXList.contains(JobsIDX));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at jobSelectionInfo isInvalidJobsIDX. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public void setPOSTAL(String POSTAL) {
        this.POSTAL = POSTAL;
    }

    public void setDelBlock(String delBlock) {
        DelBlock = delBlock;
    }

    public void setAllDelUnitsCombinedList(ArrayList<String> allDelUnitsCombinedList) {
        AllDelUnitsCombinedList = allDelUnitsCombinedList;
    }

    public void setCUSTID(String CUSTID) {
        this.CUSTID = CUSTID;
    }

    public void setPIN(String aPIN) {
        this.PIN = aPIN;
    }

    public void setMOBILE_NOsList(ArrayList<String> MOBILE_NOsList) {
        this.MOBILE_NOsList = MOBILE_NOsList;
    }

    public void setCHECKLIST(String[] CHECKLIST) {
        this.CHECKLIST = CHECKLIST;
    }

}
