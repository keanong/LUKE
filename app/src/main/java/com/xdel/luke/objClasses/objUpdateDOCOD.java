package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 27/06/2017.
 */
public class objUpdateDOCOD {

    public Long IDX;
    public Long DOObjectIDX;
    public Long AmountTypeIDX;
    public String AmountType;
    public String Reference;
    public Double Amount;

    public objUpdateDOCOD (Long aIDX, Long aDOObjectIDX, Long aAmountTypeIDX, String aAmountType, String aReference, Double aAmount) {
        this.IDX = aIDX;
        this.DOObjectIDX = aDOObjectIDX;
        this.AmountTypeIDX = aAmountTypeIDX;
        this.AmountType = aAmountType;
        this.Reference = aReference;
        this.Amount = aAmount;
    }
}
