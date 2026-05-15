package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 13/06/2017.
 */
public class objDOCOD {
    public Long IDX;
    public Long DOObjectIDX;
    public Long AmountTypeIDX;
    public String AmountType;
    public String Reference;
    public double Amount;
    public String RefNo;
    public int ReturnMethod;
    public String ReturnDate;
    public String DeviceReference;

    public objDOCOD(Long aIDX,Long aDOObjectIDX, Long aAmountTypeIDX, String aAmountType, String aReference, double aAmount, String aRefNo, int aReturnMethod, String aReturnDate, String aDeviceReference) {
        this.IDX = aIDX;
        this.DOObjectIDX = aDOObjectIDX;
        this.AmountTypeIDX = aAmountTypeIDX;
        this.AmountType = aAmountType;
        this.Reference = aReference;
        this.Amount = aAmount;
        this.RefNo = aRefNo;
        this.ReturnMethod = aReturnMethod;
        this.ReturnDate = aReturnDate;
        this.DeviceReference = aDeviceReference;
    }
}
