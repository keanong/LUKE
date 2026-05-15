package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 30/08/2017.
 */
public class NFCTag {
    public Long CAIDX1;
    public Long CNIDX1;
    public Long CAIDX2;
    public Long CNIDX2;
    public String UID;

    public NFCTag(Long aCAIDX1,Long aCNIDX1,Long aCAIDX2,Long aCNIDX2,String aUID){
        this.CAIDX1 = aCAIDX1;
        this.CNIDX1 = aCNIDX1;
        this.CAIDX2 = aCAIDX2;
        this.CNIDX2 = aCNIDX2;
        this.UID = aUID;
    }

    public Long getCAIDX1(){
        return this.CAIDX1;
    }
    public void setCAIDX1(Long aCAIDX1) {
        this.CAIDX1 = aCAIDX1;
    }

    public Long getCNIDX1(){
        return this.CNIDX1;
    }
    public void setCNIDX1(Long aCNIDX1) {
        this.CNIDX1 = aCNIDX1;
    }

    public Long getCAIDX2(){
        return this.CAIDX2;
    }
    public void setCAIDX2(Long aCAIDX2) {
        this.CAIDX2 = aCAIDX2;
    }

    public Long getCNIDX2(){
        return this.CNIDX2;
    }
    public void setCNIDX2(Long aCNIDX2) {
        this.CNIDX2 = aCNIDX2;
    }

    public String getUID(){
        return this.UID;
    }
    public void setUID(String aUID) {
        this.UID = aUID;
    }

}
