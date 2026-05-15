package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 15/03/2016.
 */
public class objCODObject {

    public long JobsIDX;
    public long InvoiceIDX;
    public double Amount;
    public int CollectionStatus;
    public objJobInfo JobInfo;
    public String DOInvoice;
    public String ConsignmentNote;

    public objCODObject(long aJobsIDX, long aInvoiceIDX, double aAmount, int aCollectionStatus, objJobInfo aJobInfo, String aDOInvoice, String aConsignmentNote){
        this.JobsIDX = aJobsIDX;
        this.InvoiceIDX = aInvoiceIDX;
        this.Amount = aAmount;
        this.CollectionStatus = aCollectionStatus;
        this.JobInfo = aJobInfo;
        this.DOInvoice = aDOInvoice;
        this.ConsignmentNote = aConsignmentNote;
    }

}
