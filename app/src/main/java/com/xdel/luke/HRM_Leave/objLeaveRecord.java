package com.xdel.luke.HRM_Leave;

import java.util.Comparator;

public class objLeaveRecord {

    public long IDX;
    public int RecordType;
    public long GC_UserIDX;
    public String LeaveFrom;
    public int FromAMPM;
    public String LeaveTo;
    public int ToAMPM;
    public double Units;
    public int UOM;
    public long LeaveTypeIDX;
    public long ApprovingOfficerIDX;
    public int Status;
    public String Remarks;
    public String[] Images;
    public long LeaveFromDate;
    public long LeaveToDate;
    public String LeaveFromDateFormatStr = "";
    public String LeaveToDateFormatStr = "";

    public objLeaveRecord(long aIDX, objRecordType.RecordType aRecordType, long aGC_UserIDX, String aLeaveFrom, objAMPM.AMPM aFromAMPM, String aLeaveTo, objAMPM.AMPM aToAMPM,
                          double aUnits, objUOM.UOM aUOM, long aLeaveTypeIDX, long aApprovingOfficerIDX, objStatus.Status aStatus, String aRemarks, String[] aImages){
        this.IDX = aIDX;
        this.RecordType = aRecordType.ordinal();
        this.GC_UserIDX = aGC_UserIDX;
        this.LeaveFrom = aLeaveFrom;
        this.FromAMPM = aFromAMPM.ordinal();
        this.LeaveTo = aLeaveTo;
        this.ToAMPM = aToAMPM.ordinal();
        this.Units = aUnits;
        this.UOM = aUOM.ordinal();
        this.LeaveTypeIDX = aLeaveTypeIDX;
        this.ApprovingOfficerIDX = aApprovingOfficerIDX;
        this.Status = aStatus.ordinal();
        this.Remarks = aRemarks;
        this.Images = aImages;
    }

    public static Comparator<objLeaveRecord> ComparatorLeaveTypeIDX = new Comparator<objLeaveRecord>() {

        @Override
        public int compare(objLeaveRecord lr1, objLeaveRecord lr2) {
            return (int) (lr1.getLeaveTypeIDX() - lr2.getLeaveTypeIDX());
        }
    };

    public static Comparator<objLeaveRecord> ComparatorLeaveFromDate = new Comparator<objLeaveRecord>() {

        @Override
        public int compare(objLeaveRecord lr1, objLeaveRecord lr2) {
            return (int) (lr1.getLeaveFromDate() - lr2.getLeaveFromDate());
        }
    };


    public long getIDX() {
        return IDX;
    }

    public void setIDX(long IDX) {
        this.IDX = IDX;
    }

    public int getRecordType() {
        return RecordType;
    }

    public void setRecordType(int recordType) {
        RecordType = recordType;
    }

    public long getGC_UserIDX() {
        return GC_UserIDX;
    }

    public void setGC_UserIDX(long GC_UserIDX) {
        this.GC_UserIDX = GC_UserIDX;
    }

    public String getLeaveFrom() {
        return LeaveFrom;
    }

    public void setLeaveFrom(String leaveFrom) {
        LeaveFrom = leaveFrom;
    }

    public int getFromAMPM() {
        return FromAMPM;
    }

    public void setFromAMPM(int fromAMPM) {
        FromAMPM = fromAMPM;
    }

    public String getLeaveTo() {
        return LeaveTo;
    }

    public void setLeaveTo(String leaveTo) {
        LeaveTo = leaveTo;
    }

    public int getToAMPM() {
        return ToAMPM;
    }

    public void setToAMPM(int toAMPM) {
        ToAMPM = toAMPM;
    }

    public double getUnits() {
        return Units;
    }

    public void setUnits(double units) {
        Units = units;
    }

    public int getUOM() {
        return UOM;
    }

    public void setUOM(int UOM) {
        this.UOM = UOM;
    }

    public long getLeaveTypeIDX() {
        return LeaveTypeIDX;
    }

    public void setLeaveTypeIDX(long leaveTypeIDX) {
        LeaveTypeIDX = leaveTypeIDX;
    }

    public long getApprovingOfficerIDX() {
        return ApprovingOfficerIDX;
    }

    public void setApprovingOfficerIDX(long approvingOfficerIDX) {
        ApprovingOfficerIDX = approvingOfficerIDX;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public long getLeaveFromDate() {
        return LeaveFromDate;
    }

    public void setLeaveFromDate(long leaveFromDate) {
        LeaveFromDate = leaveFromDate;
    }

    public long getLeaveToDate() {
        return LeaveToDate;
    }

    public void setLeaveToDate(long leaveToDate) {
        LeaveToDate = leaveToDate;
    }

    public String getLeaveFromDateFormatStr() {
        return LeaveFromDateFormatStr;
    }

    public void setLeaveFromDateFormatStr(String leaveFromDateFormatStr) {
        LeaveFromDateFormatStr = leaveFromDateFormatStr;
    }

    public String getLeaveToDateFormatStr() {
        return LeaveToDateFormatStr;
    }

    public void setLeaveToDateFormatStr(String leaveToDateFormatStr) {
        LeaveToDateFormatStr = leaveToDateFormatStr;
    }

    public String[] getImages() {
        return Images;
    }

    public void setImages(String[] images) {
        Images = images;
    }
}
