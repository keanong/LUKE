package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 18/05/2016.
 */
public class objUpdateCOD_PostData  implements Comparable<objUpdateCOD_PostData>{
    public String ARegID;
    public int AType;
    public long AIDX;
    public Integer CODType;
    public String AUTC;
    public String CNnoINVno;
    public Double Amount;
    public objJobInfo ji;
    public Boolean isCODJob;
    public long JobsIDX;
    public long InvoiceIDX;
    public Boolean EnableNCbutton;
    public String Receiver;

    public objUpdateCOD_PostData(String aARegID, int aAType, long aAIDX, Integer aCODType, String aAUTC, String aCNnoINVno, Double aAmount, objJobInfo aji,
                                 Boolean aisCODJob, long aJobsIDX, long aInvoiceIDX, Boolean aEnableNCbutton, String aReceiver){
        this.ARegID = aARegID;
        this.AType = aAType;
        this.AIDX = aAIDX;
        this.CODType = aCODType;
        this.AUTC = aAUTC;
        this.CNnoINVno = aCNnoINVno;
        this.Amount = aAmount;
        this.ji = aji;
        this.isCODJob = aisCODJob;
        this.JobsIDX = aJobsIDX;
        this.InvoiceIDX = aInvoiceIDX;
        this.EnableNCbutton = aEnableNCbutton;
        this.Receiver = aReceiver;
    }

    public void setStatus(Integer aCODType){
        this.CODType = aCODType;
    }

    public void setAUTC(String aAUTC){
        this.AUTC = aAUTC;
    }

    public void setEnableNCbutton(Boolean asd){
        this.EnableNCbutton = asd;
    }

    public int compareTo(objUpdateCOD_PostData other){
        //return AType.compareTo(other.AType);
        //asc order
        return this.AType - other.AType;
    }

}
