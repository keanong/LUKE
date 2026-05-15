package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 11/06/2015.
 */
public class objAddressStructure {

    public long IDX;
    public long CLIENTIDX;
    public int ADDRESSTYPE;
    public String ACCOUNT;
    public String COMPANY;
    public String BLOCK;
    public String STREET;
    public String UNIT;
    public String BUILDING;
    public String POSTALCODE;
    public Boolean ACTIVE;
    public String CUSTOMER_NOTES;
    public objContactStructure[] Contacts;
    public double LAT;
    public double LNG;
    public int DISTANCE_TOLERANCE;
    public int FLAG;
    public int LocationType;

    public int getDISTANCE_TOLERANCE() {
        return DISTANCE_TOLERANCE;
    }

    public void setDISTANCE_TOLERANCE(int DISTANCE_TOLERANCE) {
        this.DISTANCE_TOLERANCE = DISTANCE_TOLERANCE;
    }

    public String getACCOUNT(){
        return this.ACCOUNT;
    }

    public void setACCOUNT(String aACCOUNT){
        this.ACCOUNT = aACCOUNT;
    }

    public Boolean getACTIVE(){
        return this.ACTIVE;
    }

    public void setACTIVE(Boolean aACTIVE){
        this.ACTIVE = aACTIVE;
    }

    public int getADDRESSTYPE(){
        return this.ADDRESSTYPE;
    }

    public void setADDRESSTYPE(int aADDRESSTYPE){
        this.ADDRESSTYPE = aADDRESSTYPE;
    }

    public String getBLOCK(){
        return this.BLOCK;
    }

    public void setBLOCK(String aBLOCK){
        this.BLOCK = aBLOCK;
    }

    public String getBUILDING(){
        return this.BUILDING;
    }

    public void setBUILDING(String aBUILDING){
        this.BUILDING = aBUILDING;
    }

    public long getCLIENTIDX(){
        return this.CLIENTIDX;
    }

    public void setCLIENTIDX(long aCLIENTIDX){
        this.CLIENTIDX = aCLIENTIDX;
    }

    public String getCOMPANY(){
        return this.COMPANY;
    }

    public void setCOMPANY(String aCOMPANY){
        this.COMPANY = aCOMPANY;
    }

    public String getCUSTOMER_NOTES(){
        return this.CUSTOMER_NOTES;
    }

    public void setCUSTOMER_NOTES(String aCUSTOMER_NOTES){
        this.CUSTOMER_NOTES = aCUSTOMER_NOTES;
    }

    public objContactStructure[] getContacts(){
        return this.Contacts;
    }

    public void setContacts(objContactStructure[] aContacts){
        this.Contacts = aContacts;
    }

    public long getIDX(){
        return this.IDX;
    }

    public void setIDX(long aIDX){
        this.IDX = aIDX;
    }

    public String getPOSTALCODE(){
        return this.POSTALCODE;
    }

    public void setPOSTALCODE(String aPOSTALCODE){
        this.POSTALCODE = aPOSTALCODE;
    }

    public String getSTREET(){
        return this.STREET;
    }

    public void setSTREET(String aSTREET){
        this.STREET = aSTREET;
    }

    public String getUNIT(){
        return this.UNIT;
    }

    public void setUNIT(String aUNIT){
        this.UNIT = aUNIT;
    }

}
