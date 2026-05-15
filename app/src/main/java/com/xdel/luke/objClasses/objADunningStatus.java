package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 02/09/2016.
 */
public class objADunningStatus {
    public long JobsIDX;
    public String Completed_At;
    public int Delivery_Status;
    public int Type_of_Dwelling;
    public String Remarks;
    /*public int Cat1;
    public int Cat2;
    public int Cat3;
    public int Cat4;*/

    public objADunningStatus(long aJobsIDX, String aCompleted_At, int aDelivery_Status, int aType_of_Dwelling, String aRemarks){
        this.JobsIDX = aJobsIDX;
        this.Completed_At = aCompleted_At;
        this.Delivery_Status = aDelivery_Status;
        this.Type_of_Dwelling = aType_of_Dwelling;
        this.Remarks = aRemarks;
        /*this.Cat1 = 0;
        this.Cat2 = 0;
        this.Cat3 = 0;
        this.Cat4 = 0;*/
    }
}
