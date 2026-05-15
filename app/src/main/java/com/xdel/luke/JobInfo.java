package com.xdel.luke;

/**
 * Created by kean.ong on 18/05/2015.
 */
public class JobInfo {

    public String COMPANY;
    public String BUILDING;
    public String UNIT;
    public String CUSTOMER_NOTES;
    public String STREET;
    public String ACCOUNT;
    public String BLOCK;
    public long IDX;
    public int ADDRESSTYPE;
    String POSTALCODE;

    public JobInfo(String COMPANY, String BUILDING, String UNIT, String CUSTOMER_NOTES,
                   String STREET, String ACCOUNT, String BLOCK, long IDX, int ADDRESSTYPE, String POSTALCODE){
        this.COMPANY = COMPANY;
        this.BUILDING = BUILDING;
        this.UNIT = UNIT;
        this.CUSTOMER_NOTES = CUSTOMER_NOTES;
        this.STREET = STREET;
        this.ACCOUNT = ACCOUNT;
        this.BLOCK = BLOCK;
        this.IDX = IDX;
        this.ADDRESSTYPE = ADDRESSTYPE;
        this.POSTALCODE = POSTALCODE;
    }

    public String getCOMPANY(){
        return this.COMPANY;
    }

    public void setCOMPANY(String COMPANY){
        this.COMPANY = COMPANY;
    }

    public String getBUILDING(){
        return this.BUILDING;
    }

    public void setBUILDING(String BUILDING){
        this.BUILDING = BUILDING;
    }

    public String getUNIT(){
        return this.UNIT;
    }

    public void setUNIT(String UNIT){
        this.UNIT = UNIT;
    }

    public String getCUSTOMER_NOTES(){
        return this.CUSTOMER_NOTES;
    }

    public void setCUSTOMER_NOTES(String CUSTOMER_NOTES){
        this.CUSTOMER_NOTES = CUSTOMER_NOTES;
    }

    public String getSTREET(){
        return this.STREET;
    }

    public void setSTREET(String STREET){
        this.STREET = STREET;
    }

    public String getACCOUNT(){
        return this.ACCOUNT;
    }

    public void setACCOUNT(String ACCOUNT){
        this.ACCOUNT = ACCOUNT;
    }

    public String getBLOCK(){
        return this.BLOCK;
    }

    public void setBLOCK(String BLOCK){
        this.BLOCK = BLOCK;
    }

    public long getIDX(){
        return this.IDX;
    }

    public void setIDX(long IDX){
        this.IDX = IDX;
    }

    public int getADDRESSTYPE(){
        return this.ADDRESSTYPE;
    }

    public void setADDRESSTYPE(int ADDRESSTYPE){
        this.ADDRESSTYPE = ADDRESSTYPE;
    }

    public String getPOSTALCODE(){
        return this.POSTALCODE;
    }

    public void setPOSTALCODE(String POSTALCODE){
        this.POSTALCODE = POSTALCODE;
    }

}
