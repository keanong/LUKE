package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 08/03/2016.
 */
public class MainCOD_PostData  implements Comparable<MainCOD_PostData> {

    public Boolean Showmaincod_row_Invoicerow;
    public Boolean Showmaincod_row_firstBreakrow;
    public Boolean Showmaincod_row_statusrow;
    public Boolean Showmaincod_row_lastBreakrow;
    public String lblmaincodCNValue;
    public String lblInvoiceValue;
    public Integer CollectionStatus; //-1 == empty 0 == not collected 1== Cash 2== Cheque
    public Double Amount;
    public Integer imgmaincodStatusImgSrc;
    public String JobInfoJsonStr;
    public Boolean Clickable;
    public Double LAT;
    public Double LNG;
    public int catType;
    public String categoryTitle;
    public Integer CODcollectionAtCatType;

    public MainCOD_PostData(Boolean aShowmaincod_row_Invoicerow, Boolean aShowmaincod_row_firstBreakrow, Boolean aShowmaincod_row_statusrow, Boolean aShowmaincod_row_lastBreakrow,
                            String alblmaincodCNValue, String alblInvoiceValue, Integer aCollectionStatus,
                            Double aAmount, int aimgmaincodStatusImgSrc, String aJobInfoJsonStr, Boolean aClickable, Double aLAT, Double aLNG,
                            int acatType, String acategoryTitle, Integer aCODcollectionAtCatType){
        this.Showmaincod_row_Invoicerow = aShowmaincod_row_Invoicerow;
        this.Showmaincod_row_firstBreakrow = aShowmaincod_row_firstBreakrow;
        this.Showmaincod_row_statusrow = aShowmaincod_row_statusrow;
        this.Showmaincod_row_lastBreakrow = aShowmaincod_row_lastBreakrow;
        this.lblmaincodCNValue = alblmaincodCNValue;
        this.lblInvoiceValue = alblInvoiceValue;
        this.CollectionStatus = aCollectionStatus;
        this.Amount = aAmount;
        this.imgmaincodStatusImgSrc = aimgmaincodStatusImgSrc; //0 == ? 1 == ok
        this.JobInfoJsonStr = aJobInfoJsonStr;
        this.Clickable = aClickable;
        this.LAT = aLAT;
        this.LNG = aLNG;
        this.catType = acatType;
        this.categoryTitle = acategoryTitle;
        this.CODcollectionAtCatType = aCODcollectionAtCatType;
    }

    public int compareTo(MainCOD_PostData other){
        //return AType.compareTo(other.AType);
        //asc order
        return this.CollectionStatus - other.CollectionStatus;
    }

}
