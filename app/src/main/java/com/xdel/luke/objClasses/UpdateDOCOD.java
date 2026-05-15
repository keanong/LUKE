package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 23/06/2017.
 */
public class UpdateDOCOD {
    public String ARegID;
    public Long[] AInvoicesIDX;
    public objUpdateDOCOD[] ACODList;
    public String AUTC;

    public void setARegID(String str) {
        this.ARegID = str;
    }

    public void setAInvoicesIDX(Long[] arr) {
        this.AInvoicesIDX = arr;
    }

    public void setACODList(objUpdateDOCOD[] arr) {
        this.ACODList = arr;
    }

    public void setAUTC(String str) {
        this.AUTC = str;
    }
}
