package com.xdel.luke.inventory;

public class savedInvByBatchID {
    public String Batch_ID;
    public Inventory[] Inventories;

    public savedInvByBatchID(){}

    public void setBatch_ID(String batch_ID) {
        Batch_ID = batch_ID;
    }

    public void setInventories(Inventory[] inventories) {
        Inventories = inventories;
    }
}
