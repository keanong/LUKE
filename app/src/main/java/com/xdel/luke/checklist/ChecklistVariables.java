package com.xdel.luke.checklist;

public class ChecklistVariables {
    public boolean hasOTP = false;
    public boolean hasVerifiedOTP = false;
    public boolean CustIDVerificationNotRequired = false;
    public boolean isLockerJob = false;
    public String CurrJobInfoStatusCodeIDX = "104";
    public long StatusCodeIDX;
    public Integer FLAG = 0;
    public Integer TypeOfPinVerification = 0;
    public long JobsIDX;
    public boolean skiplocker = false;
    public boolean hasIMEI_Verification = false;
    public boolean hasSerialNum_Verification = false;
    public boolean hasVoucher_Verification = false;

    public ChecklistVariables(){

    }

    public boolean getHasOTP() {
        return hasOTP;
    }

    public boolean getHasVerifiedOTP() {
        return hasVerifiedOTP;
    }

    public boolean getCustIDVerificationNotRequired(){
        return this.CustIDVerificationNotRequired;
    }

    public boolean getisLockerJob() {
        return isLockerJob;
    }

    public String getCurrJobInfoStatusCodeIDX() {
        return CurrJobInfoStatusCodeIDX;
    }

    public long getStatusCodeIDX() {
        return StatusCodeIDX;
    }

    public Integer getFLAG() {
        return FLAG;
    }

    public Integer getTypeOfPinVerification() {
        return TypeOfPinVerification;
    }

    public long getJobsIDX() {
        return JobsIDX;
    }

    public boolean gethasIMEI_Verification() {
        return hasIMEI_Verification;
    }

    public boolean gethasSerialNum_Verification() {
        return hasSerialNum_Verification;
    }

    public boolean gethasVoucher_Verification() {
        return hasVoucher_Verification;
    }

    public boolean isSkiplocker() {
        return skiplocker;
    }

    public void setHasOTP(boolean hasOTP) {
        this.hasOTP = hasOTP;
    }

    public void setHasVerifiedOTP(boolean hasVerifiedOTP) {
        this.hasVerifiedOTP = hasVerifiedOTP;
    }

    public void setCustIDVerificationNotRequired(boolean custIDVerificationNotRequired) {
        CustIDVerificationNotRequired = custIDVerificationNotRequired;
    }

    public void setisLockerJob(boolean lockerJob) {
        isLockerJob = lockerJob;
    }

    public void setCurrJobInfoStatusCodeIDX(String currJobInfoStatusCodeIDX) {
        CurrJobInfoStatusCodeIDX = currJobInfoStatusCodeIDX;
    }

    public void setStatusCodeIDX(long statusCodeIDX) {
        StatusCodeIDX = statusCodeIDX;
    }

    public void setFLAG(Integer FLAG) {
        this.FLAG = FLAG;
    }

    public void setTypeOfPinVerification(Integer typeOfPinVerification) {
        TypeOfPinVerification = typeOfPinVerification;
    }

    public void setJobsIDX(long jobsIDX) {
        JobsIDX = jobsIDX;
    }

    public void sethasIMEI_Verification(boolean ahasIMEI_Verification) {
        this.hasIMEI_Verification = ahasIMEI_Verification;
    }

    public void sethasSerialNum_Verification(boolean ahasSerialNum_Verification) {
        this.hasSerialNum_Verification = ahasSerialNum_Verification;
    }

    public void sethasVoucher_Verification(boolean ahasVoucher_Verification) {
        this.hasVoucher_Verification = ahasVoucher_Verification;
    }

    public void setSkiplocker(boolean skiplocker) {
        this.skiplocker = skiplocker;
    }
}
