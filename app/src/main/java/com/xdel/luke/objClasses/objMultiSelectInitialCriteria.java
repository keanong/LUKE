package com.xdel.luke.objClasses;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class objMultiSelectInitialCriteria {
    public boolean hasSpecialCOD;
    public boolean hasTradeIn;
    public boolean hasSecuritySeal;
    public boolean hasIsReqStaffID;
    public boolean hasReqShipmentVerification;
    private boolean hasContactless;
    public boolean hasOTP;
    public boolean hasReqItemProof;
    public boolean hasCustID;
    //private boolean IDVerificationRequired;
    public String CustID;
    public long JobsIDX;
    public Long ClientIDX;
    public String[] CheckList;
    public String[] ChecklistContactless;
    public String[] ChecklistIDVerification;
    public String OTPpinNotFirstSelected = "";
    public List<String> JobContactNumbersList;
    public String ContactName = "";
    //public boolean ReceiverOnly;
    //public boolean ReviewLinkSMS;
    //public boolean hasReqSign;
    public boolean hasReqNFCscan;
    public boolean hasDunning;
    public String JobNoCN;
    public int NotAllowedSelectionCount;
    public int IDVerificationReqCountToDetermineChecklist;
    public int ContactlessCount;
    public int CanSlotLBCount;
    public int CanSlotUnderDoorCount;
    public int CanGuardCount;
    public int CanLeaveDoorStepCount;
    public int ReceiverOnlyCount;
    public int ReviewLinkSMSCount;
    public int ReqSignCount;
    public long JobIDXforIDVerification;
    public ArrayList<Long> JobIDXsForContactlessChecklist;
    public ArrayList<Long> JobIDXsForVerificationChecklist;
    public ArrayList<String> JobNosForContactlessChecklist;
    public ArrayList<String> JobNosForVerificationChecklist;
    public int IDVerificationReqFlag = 0;
    public int IDVerificationNotReqFlag = 0;


    public objMultiSelectInitialCriteria(boolean ahasSpecialCOD, boolean ahasTradeIn, boolean ahasSecuritySeal, boolean ahasIsReqStaffID,
                                         boolean ahasReqShipmentVerification, boolean ahasContactless, boolean ahasOTP, boolean ahasReqItemProof,
                                         boolean ahasCustID, boolean aIDVerificationRequired, String aCustID, long aJobsIDX, Long aClientIDX,
                                         String[] aChecklist, String[] aJobContactNumbers, String aContactName, boolean aReceiverOnly, boolean ahasDunning, String aJobNoCN,
                                         boolean aRegSign, boolean aHasReviewLinkSMS) {
        this.hasSpecialCOD = ahasSpecialCOD;
        this.hasTradeIn = ahasTradeIn;
        this.hasSecuritySeal = ahasSecuritySeal;
        this.hasIsReqStaffID = ahasIsReqStaffID;
        this.hasReqShipmentVerification = ahasReqShipmentVerification;
        this.ContactlessCount = ahasContactless ? 1 : 0;
        this.hasContactless = this.ContactlessCount > 0 ? true : false;
        this.hasOTP = ahasOTP;
        this.hasReqItemProof = ahasReqItemProof;
        this.hasCustID = ahasCustID;
        this.CustID = aCustID;
        this.JobsIDX = aJobsIDX;
        this.ClientIDX = aClientIDX;
        this.CheckList = aChecklist;
        this.ChecklistContactless = hasContactless && (aChecklist != null && aChecklist.length > 0) && !aIDVerificationRequired ? aChecklist : null;
        this.ChecklistIDVerification = !hasContactless && (aChecklist != null && aChecklist.length > 0) && aIDVerificationRequired ? aChecklist : null;
        this.OTPpinNotFirstSelected = "";
        this.JobContactNumbersList = new ArrayList<>();
        this.JobContactNumbersList = aJobContactNumbers != null ? new ArrayList<String>(Arrays.asList(aJobContactNumbers)) : null;
        this.ContactName = aContactName;
        this.hasDunning = ahasDunning;
        this.JobNoCN = aJobNoCN;
        this.IDVerificationReqCountToDetermineChecklist = aIDVerificationRequired && (aChecklist != null && aChecklist.length > 0) ? 1 : -1;
        this.ReceiverOnlyCount = aReceiverOnly ? 1 : 0;
        this.ReviewLinkSMSCount = aHasReviewLinkSMS ? 1 : 0;
        this.ReqSignCount = aRegSign ? 1 : 0;
    }

    public void setIDVerificationReqFlag(int aIDVerificationReqFlag) {
        this.IDVerificationReqFlag = aIDVerificationReqFlag;
    }

    public void setIDVerificationNotReqFlag(int aIDVerificationNotReqFlag) {
        this.IDVerificationNotReqFlag = aIDVerificationNotReqFlag;
    }

    public String[] getFinalChecklist() {
        if (ChecklistIDVerification != null && ChecklistIDVerification.length > 0) {
            return ChecklistIDVerification;
        } else if (ChecklistContactless != null && ChecklistContactless.length > 0) {
            return ChecklistContactless;
        } else if (CheckList != null && CheckList.length > 0) {
            return CheckList;
        } else {
            return null;
        }
    }

    public void addJobNosForVerificationChecklist(String jobno) {
        if (this.JobNosForVerificationChecklist == null) {
            this.JobNosForVerificationChecklist = new ArrayList<>();
        }
        if (!this.JobNosForVerificationChecklist.contains(jobno)) {
            this.JobNosForVerificationChecklist.add(jobno);
        }
    }

    public void removeJobNosForVerificationChecklist(String jobno) {
        if (this.JobNosForVerificationChecklist != null && this.JobNosForVerificationChecklist.size() > 0 && this.JobNosForVerificationChecklist.contains(jobno)) {
            for (int x = this.JobNosForVerificationChecklist.size()-1; x >=0; x--) {
                if (this.JobNosForVerificationChecklist.get(x).equalsIgnoreCase(jobno)) {
                    this.JobNosForVerificationChecklist.remove(x);
                    break;
                }
            }
        }
    }

    public void addJobNosForContactlessChecklist(String jobno) {
        if (this.JobNosForContactlessChecklist == null) {
            this.JobNosForContactlessChecklist = new ArrayList<>();
        }
        if (!this.JobNosForContactlessChecklist.contains(jobno)) {
            this.JobNosForContactlessChecklist.add(jobno);
        }
    }

    public void removeJobNosForContactlessChecklist(String jobno) {
        if (this.JobNosForContactlessChecklist != null && this.JobNosForContactlessChecklist.size() > 0 && this.JobNosForContactlessChecklist.contains(jobno)) {
            for (int x = this.JobNosForContactlessChecklist.size()-1; x >=0; x--) {
                if (this.JobNosForContactlessChecklist.get(x).equalsIgnoreCase(jobno)) {
                    this.JobNosForContactlessChecklist.remove(x);
                    break;
                }
            }
        }
    }

    public void addJobIDXsForVerificationChecklist(long idx) {
        if (this.JobIDXsForVerificationChecklist == null) {
            this.JobIDXsForVerificationChecklist = new ArrayList<>();
        }
        if (!this.JobIDXsForVerificationChecklist.contains(idx)) {
            this.JobIDXsForVerificationChecklist.add(idx);
        }
    }

    public void removeJobIDXsForVerificationChecklist(long idx) {
        if (this.JobIDXsForVerificationChecklist != null && this.JobIDXsForVerificationChecklist.size() > 0 && this.JobIDXsForVerificationChecklist.contains(idx)) {
            for (int x = this.JobIDXsForVerificationChecklist.size()-1; x >=0; x--) {
                if (this.JobIDXsForVerificationChecklist.get(x) == idx) {
                    this.JobIDXsForVerificationChecklist.remove(x);
                    break;
                }
            }
        }
    }

    public void addJobIDXsForContactlessChecklist(long idx) {
        if (this.JobIDXsForContactlessChecklist == null) {
            this.JobIDXsForContactlessChecklist = new ArrayList<>();
        }
        if (!this.JobIDXsForContactlessChecklist.contains(idx)) {
            this.JobIDXsForContactlessChecklist.add(idx);
        }
    }

    public void removeJobIDXsForContactlessChecklist(long idx) {
        if (this.JobIDXsForContactlessChecklist != null && this.JobIDXsForContactlessChecklist.size() > 0 && this.JobIDXsForContactlessChecklist.contains(idx)) {
            for (int x = this.JobIDXsForContactlessChecklist.size()-1; x >=0; x--) {
                if (this.JobIDXsForContactlessChecklist.get(x) == idx) {
                    this.JobIDXsForContactlessChecklist.remove(x);
                    break;
                }
            }
        }
    }

    public boolean HasCheckList(){
        String[] finalcl = getFinalChecklist();
        return finalcl != null && finalcl.length > 0 ? true : false;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public boolean getReqSign(){
        return ReqSignCount > 0 ? true : false;
    }

    public void setReqSignCount(int c) {
        if (c < 0 && this.ReqSignCount <= 0) {
            this.ReqSignCount = 0;
        } else if (c < 0 && this.ReqSignCount > 0) {
            this.ReqSignCount--;
        } else if (c > 0 && this.ReqSignCount <= 0) {
            this.ReqSignCount = 1;
        } else if (c > 0) {
            this.ReqSignCount++;
        }
    }

    public boolean getReviewLinkSMS() {
        return ReviewLinkSMSCount > 0 ? true : false;
    }

    public void setReviewLinkSMSCount(int c) {
        if (c < 0 && ReviewLinkSMSCount <= 0) {
            ReviewLinkSMSCount = 0;
        } else if (c < 0 && ReviewLinkSMSCount > 0) {
            ReviewLinkSMSCount--;
        } else if (c > 0 && ReviewLinkSMSCount <= 0 ) {
            ReviewLinkSMSCount = 1;
        } else if (c > 0) {
            ReviewLinkSMSCount++;
        }
    }

    public boolean getReceiverOnly() {
        return ReceiverOnlyCount > 0 ? true : false;
    }

    public void setReceiverOnlyCount(int c) {
        if (c < 0 && ReceiverOnlyCount <= 0) {
            ReceiverOnlyCount = 0;
        } else if (c < 0 && ReceiverOnlyCount > 0) {
            ReceiverOnlyCount--;
        } else if (c > 0 && ReceiverOnlyCount <= 0 ) {
            ReceiverOnlyCount = 1;
        } else if (c > 0) {
            ReceiverOnlyCount++;
        }
    }

    public boolean getHasContactless() {
        return ContactlessCount > 0 && !getIDVerificationRequired() ? true : false;
    }

    public void setContactlessCount(int c) {
        if (c < 0 && ContactlessCount <= 0) {
            ContactlessCount = 0;
        } else if (c < 0 && ContactlessCount > 0) {
            ContactlessCount--;
        } else if (c > 0 && ContactlessCount <= 0) {
            ContactlessCount = 1;
        } else if (c > 0) {
            ContactlessCount++;
        }
        if (ContactlessCount == 0) {
            this.ChecklistContactless = null;
        }
    }

    public void setChecklistContactless(String[] c) {
        this.ChecklistContactless = c;
    }

    public void setChecklistIDVerification(String[] c) {
        this.ChecklistIDVerification = c;
    }

    public boolean getIDVerificationRequired() {
        return IDVerificationReqCountToDetermineChecklist > 0 ? true : false;
    }

    public void setIDVerificationReqCountToDetermineChecklist(int c) {
        if (c < 0 && IDVerificationReqCountToDetermineChecklist <= 0) {
            IDVerificationReqCountToDetermineChecklist = 0;
        } else if (c < 0 && IDVerificationReqCountToDetermineChecklist > 0) {
            IDVerificationReqCountToDetermineChecklist--;
        } else if (c > 0 && IDVerificationReqCountToDetermineChecklist <= 0) {
            IDVerificationReqCountToDetermineChecklist = 1;
        } else if (c > 0) {
            IDVerificationReqCountToDetermineChecklist++;
        }
        if (IDVerificationReqCountToDetermineChecklist == 0) {
            this.ChecklistIDVerification = null;
            this.CustID = "";
        }
    }

    public void setNotAllowedSelectionCount(int c) {
        if (c < 0 && NotAllowedSelectionCount <= 0) {
            NotAllowedSelectionCount = 0;
        } else if (c < 0 && NotAllowedSelectionCount > 0) {
            NotAllowedSelectionCount--;
        } else if (c > 0 && NotAllowedSelectionCount <= 0) {
            NotAllowedSelectionCount = 1;
        } else if (c > 0) {
            NotAllowedSelectionCount++;
        }
    }

    public void setHasReqNFCscan(boolean hasReqNFCscan) {
        this.hasReqNFCscan = hasReqNFCscan;
    }

    public boolean getCanLeaveDoorStep() {
        return CanLeaveDoorStepCount > 0 ? true : false;
    }

    public void setCanLeaveDoorStepCount(int c) {
        if (c < 0 && CanLeaveDoorStepCount <= 0) {
            CanLeaveDoorStepCount = 0;
        } else if (c < 0 && CanLeaveDoorStepCount > 0) {
            CanLeaveDoorStepCount--;
        } else if (c > 0 && CanLeaveDoorStepCount <= 0) {
            CanLeaveDoorStepCount = 1;
        } else if (c > 0) {
            CanLeaveDoorStepCount++;
        }
    }

    public boolean getCanSlotLB() {
        return CanSlotLBCount > 0 ? true : false;
    }

    public void setCanSlotLBCount(int c) {
        if (c < 0 && CanSlotLBCount <= 0) {
            CanSlotLBCount = 0;
        } else if (c < 0 && CanSlotLBCount > 0) {
            CanSlotLBCount--;
        } else if (c > 0 && CanSlotLBCount <= 0) {
            CanSlotLBCount = 1;
        } else if (c > 0) {
            CanSlotLBCount++;
        }
    }

    public boolean getCanSlotUnderDoor() {
        return CanSlotUnderDoorCount > 0 ? true : false;
    }

    public void setCanSlotUnderDoorCount(int c) {
        if (c < 0 && CanSlotUnderDoorCount <= 0) {
            CanSlotUnderDoorCount = 0;
        } else if (c < 0 && CanSlotUnderDoorCount > 0) {
            CanSlotUnderDoorCount--;
        } else if (c > 0 && CanSlotUnderDoorCount <= 0) {
            CanSlotUnderDoorCount = 1;
        } else if (c > 0) {
            CanSlotUnderDoorCount++;
        }
    }

    public boolean getCanGuard() {
        return CanGuardCount > 0 ? true : false;
    }

    public void setCanGuardCount(int c) {
        if (c < 0 && CanGuardCount <= 0) {
            CanGuardCount = 0;
        } else if (c < 0 && CanGuardCount > 0) {
            CanGuardCount--;
        } else if (c > 0 && CanGuardCount <= 0) {
            CanGuardCount = 1;
        } else if (c > 0) {
            CanGuardCount++;
        }
    }

    public void setCanSlotLBUnderDoorGuard(Integer jobFlag, Integer jobFlag2) {
        if (objGlobalMethods.isCanSlotLB(jobFlag)) {
            setCanSlotLBCount(1);
        } else {
            setCanSlotLBCount(-1);
        }

        if (objGlobalMethods.isCanSlotUnderDoor(jobFlag)) {
            setCanSlotUnderDoorCount(1);
        } else {
            setCanSlotUnderDoorCount(-1);
        }

        if (objGlobalMethods.isCanGuard(jobFlag)) {
            setCanGuardCount(1);
        } else {
            setCanGuardCount(-1);
        }

        if (JobsHandler2.CanLeaveDoorStep(jobFlag2)) {
            setCanLeaveDoorStepCount(1);
        } else {
            setCanLeaveDoorStepCount(-1);
        }
    }

    public void addJobContactNumbersToList(String[] tellist) {
        String tel = "";
        if (tellist != null && tellist.length > 0) {
            if (this.JobContactNumbersList == null) {
                this.JobContactNumbersList = new ArrayList<>();
            }

            for (int i = 0; i <= tellist.length - 1; i++) {
                tel = tellist[i];
                this.JobContactNumbersList.add(tel);
            }
        }
    }

    public void removeJobContactNumbersFromList(String[] tellist) {
        String tel = "";
        if (tellist != null && tellist.length > 0 && this.JobContactNumbersList != null && this.JobContactNumbersList.size() > 0) {
            for (int i = 0; i <= tellist.length - 1; i++) {
                tel = tellist[i];
                for (int x = this.JobContactNumbersList.size()-1; x >=0; x--) {
                    if (this.JobContactNumbersList.get(x).equalsIgnoreCase(tel)) {
                        this.JobContactNumbersList.remove(x);
                        break;
                    }
                }
            }
        }

    }

    public boolean ContactNumberExistInCriteria(String[] telsearch) {
        boolean asd = false;
        try {
            if (this.JobContactNumbersList != null && this.JobContactNumbersList.size() > 0 && telsearch != null && telsearch.length > 0) {
                for (int i = 0; i <= telsearch.length - 1; i++) {
                    if (this.JobContactNumbersList.contains(telsearch[i])) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at objMultiSelectInitialCriteria ContactNumberExistInCriteria. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public void setHasOTP(boolean hasOTP) {
        this.hasOTP = hasOTP;
    }

    public void setHasCustID(boolean hasCustID) {
        this.hasCustID = hasCustID;
    }

    public void setCustID(String custID) {
        CustID = custID;
        this.hasCustID = true;
    }

}
