package com.xdel.luke.objClasses;

import com.xdel.luke.sqlite.dbObjTradeInList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kean.ong on 14/06/2017.
 */
public class codupdate_PostData {
    public Long IDX;
    public ArrayList<Long> InvoiceIDXList;
    public Long DOObjectIDX;
    public String JobsIDX;
    public String JobNo;
    public String CN;
    public String DoNum;
    //public Long DOIDX;
    public Long AmountTypeIDX;
    public String AmountType;
    public String Reference;
    public Double Amount;
    public int RefNo;
    public Double PaidAmount;
    public int ReturnMethod;
    public String ReturnDate;
    public int group;
    public Boolean showfull;
    public List<dbObjTradeInList> TradInList;
    public Double Total;
    public int selectedTradeInModelIndex;
    public Double InitialValue;
    public Double InitialTotalCash;
    public String TradeInModelSpecifiedValue;



    public codupdate_PostData(Long aIDX, ArrayList<Long> aInvoiceIDXList, Long aDOObjectIDX, String aJobsIDX, Long aAmountTypeIDX, String aAmountType, String aReference,
                              Double aAmount, Double aPaidAmount, int agroup, Boolean ashowfull, List<dbObjTradeInList> aTradInList, Double aTotal,
                              int aselectedTradeInModelIndex, Double aInitialValue, Double aInitialTotalCash, String aTradeInModelSpecifiedValue){
        this.IDX = aIDX;
        this.InvoiceIDXList = aInvoiceIDXList;
        this.DOObjectIDX = aDOObjectIDX;
        this.JobsIDX = aJobsIDX;
        //this.DOIDX = aDOIDX;
        this.AmountTypeIDX = aAmountTypeIDX;
        this.AmountType = aAmountType;
        this.Reference = aReference;
        this.Amount = aAmount;
        this.PaidAmount = aPaidAmount;
        this.group = agroup;
        this.showfull = ashowfull;
        this.TradInList = aTradInList;
        this.Total = aTotal;
        this.selectedTradeInModelIndex = aselectedTradeInModelIndex;
        this.InitialValue = aInitialValue;
        this.InitialTotalCash = aInitialTotalCash;
        this.TradeInModelSpecifiedValue = aTradeInModelSpecifiedValue;
    }

    public void setTradeInModelSpecifiedValue(String val) {
        this.TradeInModelSpecifiedValue = val;
    }

    public void setInitialTotalCash(Double aInitialTotalCash) {
        this.InitialTotalCash = aInitialTotalCash;
    }

    public void addInitialValue(Double aInitialValue) {
        this.InitialValue += aInitialValue;
    }

    public void setInitialValue(Double aInitialValue) {
        this.InitialValue = aInitialValue;
    }

    public void setselectedTradeInModelIndex(int aselectedTradeInModelIndex) {
        this.selectedTradeInModelIndex = aselectedTradeInModelIndex;
    }

    public void setTotal(Double aTotal) {
        this.Total = aTotal;
    }

    public void setIDX(Long aidx){
        this.IDX = aidx;
    }

    public void setInvoiceIDX(ArrayList<Long> aInvoiceIDXList) {
        this.InvoiceIDXList = aInvoiceIDXList;
    }

    public void setDOObjectIDX(Long aDOObjectIDX) {
        this.DOObjectIDX = aDOObjectIDX;
    }

    public void setAmountType(String amountType) {
        this.AmountType = amountType;
    }

    public void setJobsIDX(String aJobsIDX) {
        this.JobsIDX = aJobsIDX;
    }

    public void setDoNum(String aDoNum) { this.DoNum = aDoNum; }

    public void setJobNo(String aJobNo) { this.JobNo = aJobNo; }

    public void setCN(String aCN) { this.CN = aCN; }

    public void addAmount(Double aAmount) {
        this.Amount += aAmount;
    }

    public void minusAmount(Double aAmount) {
        this.Amount -= aAmount;
    }

    public void setAmount(Double aAmount) {
        this.Amount = aAmount;
    }

    public Double getPaidAmount(){
        return this.PaidAmount;
    }

    public void setPaidAmount(Double aPaidAmount) {
        this.PaidAmount = aPaidAmount;
    }

    public void setReference(String ref){
        this.Reference = ref;
    }

    public void setTradInList(List<dbObjTradeInList> aTradInList) {
        this.TradInList = aTradInList;
    }

}
