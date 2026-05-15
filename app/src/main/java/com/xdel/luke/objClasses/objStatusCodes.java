package com.xdel.luke.objClasses;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kean.ong on 03/06/2015.
 */
//public class objStatusCodes  implements Comparable<objStatusCodes>{
public class objStatusCodes {
    public String CodeType;
    public String Description;
    public String DriverAffected;
    public long IDX;
    public String StatusCode;
    public String Remarks = "";
    public String RemarksEN = "";
    public int Flag;


    /*public objStatusCodes(String CodeType, String Description, String DriverAffected, long IDX, String StatusCode ){
        this.CodeType = CodeType;
        this.Description = Description;
        this.DriverAffected = DriverAffected;
        this.IDX = IDX;
        this.StatusCode = StatusCode;

    }*/

    /*public int compareTo(objStatusCodes other){
        return Description.compareTo(other.Description);
    }*/

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

    public String getRemarksEN(){
        return this.RemarksEN;
    }

    public void setRemarksEN(String aRemarksC){
        this.RemarksEN = aRemarksC;
    }

    public int getFlag() {
        return Flag;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }
}
