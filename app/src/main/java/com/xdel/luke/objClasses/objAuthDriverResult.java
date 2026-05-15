package com.xdel.luke.objClasses;

public class objAuthDriverResult {

    public long DriverIDX;
    public String ID = "";
    public String MaskedID = "";
    public String FullName = "";
    public byte[] Image = null;
    public String Message;
    public String Message_C;
    public int Status;
    public String DateConfirmed;
    public String probationGC;
    public Integer Flag;

    public String getFullName() {
        return FullName;
    }

    public byte[] getImage() {
        return Image;
    }

    public String getID() {
        return ID;
    }

    public String getMaskedID() { return MaskedID; }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public void setImage(byte[] image) {
        Image = image;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setMaskedID(String maskedID) {
        MaskedID = maskedID;
    }

    public String getDateConfirmed() {
        return DateConfirmed;
    }

    public void setProbationGC(String probationGC) {
        this.probationGC = probationGC;
    }

    public void setDateConfirmed(String dateConfirmed) {
        DateConfirmed = dateConfirmed;
    }

    public void setFlag(Integer flag) {
        Flag = flag;
    }
}
