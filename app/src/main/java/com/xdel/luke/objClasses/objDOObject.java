package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 11/06/2015.
 */
public class objDOObject {

    public long DOIDX;
    public String Invoice;
    public double Amount;
    public String Remarks;
    public objDOCOD[] CODList;
    public String JobNo;
    public String CN;
    public String JobsIDX;
    public Integer Flag;

    public objDOObject(){
        DOIDX = 0;
        Invoice ="";
        Amount = 0;
        Remarks = "";
        CODList = null;
    }

    public objDOObject(long aDOIDX,String aInvoice,double aAmount, String aRemarks, objDOCOD[] aDOCODObject){
        this.DOIDX = aDOIDX;
        this.Invoice = aInvoice;
        this.Amount = aAmount;
        this.Remarks = aRemarks;
        this.CODList = aDOCODObject;
    }

    public void setFLAG(Integer FLAG) {
        this.Flag = FLAG;
    }

    public void setJobNo(String aJobNo) {
        this.JobNo = aJobNo;
    }

    public void setCN(String aCN) { this.CN = aCN; }

    public void setJobsIDX(String aJobsIDX) { this.JobsIDX = aJobsIDX; }

    public long getDOIDX(){
        return this.DOIDX;
    }

    public void setDOIDX(long aDOIDX){
        this.DOIDX = aDOIDX;
    }

    public String getInvoice(){
        return this.Invoice;
    }

    public void setInvoice(String aInvoice){
        this.Invoice = aInvoice;
    }

    public double getAmount(){
        return this.Amount;
    }

    public void setAmount(double aAmount){
        this.Amount = aAmount;
    }


}
