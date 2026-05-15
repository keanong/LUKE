package com.xdel.luke.objClasses;

import java.util.ArrayList;

/**
 * Created by kean.ong on 15/04/2017.
 */
public class objCCimportData {
    public int citidbshsbc;
    public ArrayList SWTDriverIDXal;
    public int AJobType;
    public CreditCard_PostData[] AJobs;

    /*public objCCimportData(int acitidbshsbc, ArrayList aSWTDriverIDXal){
        this.citidbshsbc = acitidbshsbc;
        this.SWTDriverIDXal = aSWTDriverIDXal;
    }*/

    public objCCimportData(int aAJobType, CreditCard_PostData[] aAJobs) {
        this.AJobType = aAJobType;
        this.AJobs = aAJobs;
    }
}
