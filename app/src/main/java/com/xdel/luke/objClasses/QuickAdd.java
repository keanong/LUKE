package com.xdel.luke.objClasses;

public class QuickAdd {
    public String PostalCode;
    public String ConsignmentNote;
    public String UnitNo;
    public String Name;
    public String ContactNumber;

    public QuickAdd(){

    }

    public QuickAdd(String aPostalCode, String aConsignmentNote, String aUnitNo, String aName, String aContactNumber){
        this.PostalCode = aPostalCode;
        this.ConsignmentNote = aConsignmentNote;
        this.UnitNo = aUnitNo;
        this.Name = aName;
        this.ContactNumber = aContactNumber;
    }

    public String getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(String postalCode) {
        PostalCode = postalCode;
    }

    public String getConsignmentNote() {
        return ConsignmentNote;
    }

    public void setConsignmentNote(String consignmentNote) {
        ConsignmentNote = consignmentNote;
    }

    public String getUnitNo() {
        return UnitNo;
    }

    public void setUnitNo(String unitNo) {
        UnitNo = unitNo;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }
}
