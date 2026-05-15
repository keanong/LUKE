package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 22/02/2016.
 */
public class objStatusCodeStructure implements Comparable<objStatusCodeStructure>{
    public String CodeType;
    public String Description;
    public String DriverAffected;
    public long IDX;
    public String StatusCode;
    public String Remarks;

    public int compareTo(objStatusCodeStructure other){
        return Description.compareTo(other.Description);
    }

    public String getCodeType(){
        return this.CodeType;
    }

    public void setCodeType(String CodeType){
        this.CodeType = CodeType;
    }

    public String getDescription(){
        return this.Description;
    }

    public void setDescription(String Description){
        this.Description = Description;
    }

    public String getDriverAffected(){
        return this.DriverAffected;
    }

    public void setDriverAffected(String DriverAffected){
        this.DriverAffected = DriverAffected;
    }

    public long getIDX(){
        return this.IDX;
    }

    public void setIDX(long IDX){
        this.IDX = IDX;
    }

    public String getStatusCode(){
        return this.StatusCode;
    }

    public void setStatusCode(String StatusCode){
        this.StatusCode = StatusCode;
    }

    public String getRemarks(){
        return this.Remarks;
    }

    public void setRemarks(String aRemarks){
        this.Remarks = aRemarks;
    }


}
