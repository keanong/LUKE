package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 09/03/2016.
 */
public class objCODjobInfo {

    public Long JobsIDX;
    public long InvoiceIDX;
    public double Amount;
    public int CollectionStatus;
    public String ConsignmentNote;
    public String DOInvoice;
    public int catType;
    public String categoryTitle;
    public objJobInfo JobInfo;
    public Integer imgmaincodStatusImgSrc; //-1 == ? 0 == X img 1 == ok img
    public Integer CODcollectionAtCatType;


    public objCODjobInfo(Long aJobsIDX, long aInvoiceIDX, double aAmount, int aCollectionStatus, String aConsignmentNote,
                         String aDOInvoice, int acatType, String acategoryTitle, objJobInfo aJobInfo,
                         Integer aimgmaincodStatusImgSrc, Integer aCODcollectionAtCatType){
        this.JobsIDX = aJobsIDX;
        this.InvoiceIDX = aInvoiceIDX;
        this.Amount = aAmount;
        this.CollectionStatus = aCollectionStatus;
        this.ConsignmentNote = aConsignmentNote;
        this.DOInvoice = aDOInvoice;
        this.catType = acatType;
        this.categoryTitle = acategoryTitle;
        this.JobInfo = aJobInfo;
        this.imgmaincodStatusImgSrc = aimgmaincodStatusImgSrc;
        this.ConsignmentNote = aConsignmentNote;
        this.CODcollectionAtCatType = aCODcollectionAtCatType;
    }


}
