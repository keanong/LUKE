package com.xdel.luke.HRM_Leave;

public class objLeaveRecordSubmit {
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

    public objLeaveRecordSubmit(long aIDX, int aRecordType, long aGC_UserIDX, String aLeaveFrom, int aFromAMPM, String aLeaveTo, int aToAMPM,
                          double aUnits, int aUOM, long aLeaveTypeIDX, long aApprovingOfficerIDX, int aStatus, String aRemarks, String[] aImages){
        this.IDX = aIDX;
        this.RecordType = aRecordType;
        this.GC_UserIDX = aGC_UserIDX;
        this.LeaveFrom = aLeaveFrom;
        this.FromAMPM = aFromAMPM;
        this.LeaveTo = aLeaveTo;
        this.ToAMPM = aToAMPM;
        this.Units = aUnits;
        this.UOM = aUOM;
        this.LeaveTypeIDX = aLeaveTypeIDX;
        this.ApprovingOfficerIDX = aApprovingOfficerIDX;
        this.Status = aStatus;
        this.Remarks = aRemarks;
        this.Images = aImages;
    }

    public void setIDX(long IDX) {
        this.IDX = IDX;
    }

    public void setUnits(double units) {
        Units = units;
    }

    public void setLeaveFrom(String leaveFrom) {
        LeaveFrom = leaveFrom;
    }

    public void setLeaveTo(String leaveTo) {
        LeaveTo = leaveTo;
    }
}
