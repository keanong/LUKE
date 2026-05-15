package com.xdel.luke.inventory;

public class UploadStockVerificationList {
    public String APIKey;
    public long AUserIDX;
    public long ADriverIDX;
    public StockVerificationList StockVerificationList;

    public UploadStockVerificationList(){}

    public UploadStockVerificationList(String aAPIKey, long aAUserIDX, long aADriverIDX, StockVerificationList aStockVerificationList){
        this.APIKey = aAPIKey;
        this.AUserIDX = aAUserIDX;
        this.ADriverIDX = aADriverIDX;
        this.StockVerificationList = aStockVerificationList;
    }

    public void setAPIKey(String APIKey) {
        this.APIKey = APIKey;
    }

    public void setAUserIDX(long AUserIDX) {
        this.AUserIDX = AUserIDX;
    }

    public void setADriverIDX(long ADriverIDX) {
        this.ADriverIDX = ADriverIDX;
    }

    public void setStockVerificationList(com.xdel.luke.inventory.StockVerificationList stockVerificationList) {
        StockVerificationList = stockVerificationList;
    }
}
