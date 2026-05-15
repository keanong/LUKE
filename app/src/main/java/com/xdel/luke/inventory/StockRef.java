package com.xdel.luke.inventory;

public class StockRef {
    public String StockReference;
    public boolean verified;
    public boolean blink = false;


    public StockRef(String aStockReference){
        this.StockReference = aStockReference;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setBlink(boolean ablink) {
        this.blink = ablink;
    }

}
