package com.xdel.luke.inventory;

public class VerifiedItem {
    public long InventoryIDX;
    public int Verified_Quantity;
    public String[] Verified_StockReferences;

    public VerifiedItem(){}

    public VerifiedItem(long aInventoryIDX, int aVerified_Quantity, String[] aVerified_StockReferences){
        this.InventoryIDX = aInventoryIDX;
        this.Verified_Quantity = aVerified_Quantity;
        this.Verified_StockReferences = aVerified_StockReferences;
    }

    public void setInventoryIDX(long inventoryIDX) {
        InventoryIDX = inventoryIDX;
    }

    public void setVerified_Quantity(int verified_Quantity) {
        Verified_Quantity = verified_Quantity;
    }

    public void setVerified_StockReferences(String[] verified_StockReferences) {
        Verified_StockReferences = verified_StockReferences;
    }
}
