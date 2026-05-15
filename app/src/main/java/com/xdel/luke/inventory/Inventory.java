package com.xdel.luke.inventory;

public class Inventory {
    public long IDX;
    public long ClientIDX;
    public String DateStarted;
    public String UnitOfMeasure;
    public Double CostPerUnit;
    public Double WeightPerUnit;
    public Double BillingSurcharge;
    public String StockCode;
    public String Description;
    public String Barcode;
    public boolean Active;
    public InventoryDetails[] StockMovement;
    public int Balance;
    public VolumetricInfo vinfo;
    public int Flag;
    public int VFlag;

    public Inventory(){}

    public void setVFlag(int VFlag) {
        this.VFlag = VFlag;
    }
}
