package com.xdel.luke.inventory;

public class StockVerificationList {
    public String Batch_ID;
    public VerifiedItem[] Items;
    public String[] Logs;

    public StockVerificationList() {

    }

    public StockVerificationList(String aBatch_ID, VerifiedItem[] aItems, String[] aLogs) {
        this.Batch_ID = aBatch_ID;
        this.Items = aItems;
        this.Logs = aLogs;
    }

    public void setBatch_ID(String batch_ID) {
        Batch_ID = batch_ID;
    }

    public void setItems(VerifiedItem[] items) {
        Items = items;
    }

    public void setLogs(String[] logs) {
        Logs = logs;
    }
}
