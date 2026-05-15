package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 21/08/2015.
 */
public class CreditCard_PostData {

    /*public String postCNNO;
    public String postPostal;
    public String combinedStr;
    public objStringWTag swt;

    public CreditCard_PostData(String apostCNNO, String apostPostal, String acombinedStr, objStringWTag aswt){
        this.postCNNO = apostCNNO;
        this.postPostal = apostPostal;
        this.combinedStr = acombinedStr;
        this.swt = aswt;
    }*/

    public String PostalCode;
    public String ConsignmentNote;
    public String UnitNo;
    public String Name;
    public String ContactNumber;

    public CreditCard_PostData(String aPostalCode, String aConsignmentNote, String aUnitNo, String aName, String aContactNumber) {
        this.PostalCode = aPostalCode;
        this.ConsignmentNote = aConsignmentNote;
        this.UnitNo = aUnitNo;
        this.Name = aName;
        this.ContactNumber = aContactNumber;
    }
}
