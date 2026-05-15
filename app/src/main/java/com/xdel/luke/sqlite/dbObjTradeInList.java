package com.xdel.luke.sqlite;

/**
 * Created by kean.ong on 28/06/2017.
 */
public class dbObjTradeInList {
    public Long ClientIDX;
    public String Brand;
    public String Model;
    public Double Value;
    public String BrandModel;
    public String BrandModelNoSpaces;

    public dbObjTradeInList(Long aClientIDX, String aBrand, String aModel, Double aValue, String aBrandModel, String aBrandModelNoSpaces) {
        this.ClientIDX = aClientIDX;
        this.Brand = aBrand;
        this.Model = aModel;
        this.Value = aValue;
        this.BrandModel = aBrandModel;
        this.BrandModelNoSpaces = aBrandModelNoSpaces;
    }

    public void setClientIDX(Long aClientIDX) {
        this.ClientIDX = aClientIDX;
    }

    public void setBrand(String aBrand) {
        this.Brand = aBrand;
    }

    public void setModel(String aModel) {
        this.Model = aModel;
    }

    public void setValue(Double aValue) {
        this.Value = aValue;
    }

    public void setBrandModel(String aBrandModel) {
        this.BrandModel = aBrandModel;
    }

    public void setBrandModelNoSpaces(String aBrandModelNoSpaces) {
        this.BrandModelNoSpaces = aBrandModelNoSpaces;
    }
}
