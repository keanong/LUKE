package com.xdel.luke.JobInventoryVerify;

public class InventoryEntry {
    public long IDX;
    public String StockCode;
    public String Reference;
    public String Description;
    public int Quantity;
    public int Flag;
    public int InventoryFlag;
    public boolean Verified;
    public boolean blink = false;

    public InventoryEntry(long aIDX, String aStockCode, String aReference, String aDescription, int aQuantity, int aFlag, int aInventoryFlag) {
        this.IDX = aIDX;
        this.StockCode = aStockCode;
        this.Reference = aReference;
        this.Description = aDescription;
        this.Quantity = aQuantity;
        this.Flag = aFlag;
        this.InventoryFlag = aInventoryFlag;
    }

    public void setVerified(boolean verified) {
        this.Verified = verified;
    }

    public void setBlink(boolean ablink) {
        this.blink = ablink;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }
}
