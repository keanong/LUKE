package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 23/02/2016.
 */
public class Spinner_PostDataOKP {

    public String Desc;
    public String DescEN;
    public String IDX;
    public String Name_Required;
    public String ishuman;
    public String instructions;
    public String subcatEN;
    public String subcatZH;
    public int Flag;

    public Spinner_PostDataOKP(String aDesc,String aDescEN, String aIDX, String aName_Required, String aishuman, String ainstructions, String asubcatEN, String asubcatZH, int aFlag){
        this.Desc = aDesc;
        this.DescEN = aDescEN;
        this.IDX = aIDX;
        this.Name_Required = aName_Required;
        this.ishuman = aishuman;
        this.instructions = ainstructions;
        this.subcatEN = asubcatEN;
        this.subcatZH = asubcatZH;
        this.Flag = aFlag;
    }
}
