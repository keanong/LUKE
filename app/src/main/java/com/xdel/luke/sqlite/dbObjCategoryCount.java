package com.xdel.luke.sqlite;

/**
 * Created by kean.ong on 04/05/2017.
 */
public class dbObjCategoryCount {
    public Integer CatTypeID;
    public Integer Count;
    public int NetsVisaTi;
    public int ReqNetsCount;
    public int ReqVisaCount;
    public int ReqTiCount;
    public int ColdCount;

    public dbObjCategoryCount(){

    }

    public Integer getCatTypeID(){
        return this.CatTypeID;
    }
    public void setCatTypeID(Integer aCatTypeID){
        this.CatTypeID = aCatTypeID;
    }

    public Integer getCount(){
        return this.Count;
    }
    public void setCount(Integer aCount) {
        this.Count = aCount;
    }

    public int getNetsVisaTi(){
        return this.NetsVisaTi;
    }
    public void setNetsVisaTi(int aNetsVisaTi){
        this.NetsVisaTi = aNetsVisaTi;
    }

    public void setReqNetsCount(int aReqNetsCount){
        this.ReqNetsCount = aReqNetsCount;
    }
    public void setReqVisaCount(int aReqVisaCount){
        this.ReqVisaCount = aReqVisaCount;
    }
    public void setReqTiCount(int aReqTiCount){
        this.ReqTiCount = aReqTiCount;
    }
    public void setColdCount(int aColdCount) { this.ColdCount = aColdCount; }

}
