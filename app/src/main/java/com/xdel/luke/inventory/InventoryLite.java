package com.xdel.luke.inventory;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.Comparator;

public class InventoryLite {
    public String Batch_ID;
    public long IDX;
    public String StockCode;
    public String hiddenStockCode;
    public String Description;
    public String Barcode;
    public int Balance;
    public int prevBalance;
    public int vBalance;
    public ArrayList<String> StockReferences;
    public ArrayList<String> VStockReferences;
    public ArrayList<StockRef> Stockrefs;
    public int Flag;
    public int VFlag;
    public int InvTallyMode = 1;

    public InventoryLite(){}

    public void setInvTallyMode(int invTallyMode) {
        InvTallyMode = invTallyMode;
    }

    public void setHiddenStockCode(String hiddenStockCode) {
        this.hiddenStockCode = hiddenStockCode;
    }

    public String getHiddenStockCode() {
        return hiddenStockCode;
    }

    public ArrayList<StockRef> getStockrefs() {
        return Stockrefs == null ? new ArrayList<>() : Stockrefs;
    }

    public void setSpecificStocRefs(int index, StockRef sr) {
        if (this.Stockrefs != null && this.Stockrefs.size() > 0 && index <= this.Stockrefs.size() -1) {
            this.Stockrefs.set(index, sr);
        }
    }

    public void setStockrefs(ArrayList<StockRef> stockref) {
        Stockrefs = stockref;
    }

    public void addStockRef(StockRef sr) {
        boolean exist = false;
        if (Stockrefs == null)
            Stockrefs = new ArrayList<>();
        try {
            Stockrefs.add(sr);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryLite addStockRef. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void addStockRefByStringRef(String ref) {
        boolean exist = false;
        StockRef sr = null;
        if (Stockrefs == null)
            Stockrefs = new ArrayList<>();
        try {
            for (int i = 0; i <= Stockrefs.size() -1; i++) {
                if (Stockrefs.get(i).StockReference.toUpperCase().equals(ref)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                sr = new StockRef(ref);
                Stockrefs.add(sr);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryLite addStockRefByStringRef. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void setBatch_ID(String batch_ID) {
        Batch_ID = batch_ID;
    }

    public void setIDX(long IDX) {
        this.IDX = IDX;
    }

    public void setStockCode(String stockCode) {
        StockCode = stockCode;
    }

    public String getStockCode() {
        return StockCode;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDescription() {
        return Description;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public void setBalance(int balance) {
        Balance = balance;
    }

    public int getBalance() {
        return Balance;
    }

    public void setPrevBalance(int prevBalance) {
        this.prevBalance = prevBalance;
    }

    public int getPrevBalance() {
        return prevBalance;
    }

    public void setvBalance(int vBalance) {
        this.vBalance = vBalance;
    }

    public int getvBalance() {
        return vBalance;
    }

    public void setStockReferences(String stockRef) {
        if (StockReferences == null)
            StockReferences = new ArrayList<>();
        try {
            if (!StockReferences.contains(stockRef))
                StockReferences.add(stockRef);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryLite setStockReferences. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void setVStockReferences(String stockRef) {
        if (VStockReferences == null)
            VStockReferences = new ArrayList<>();
        try {
            if (!VStockReferences.contains(stockRef))
                VStockReferences.add(stockRef);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryLite setStockReferences. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void setFlag(int flag) {
        Flag = flag;
    }

    public void setVFlag(int VFlag) {
        this.VFlag = VFlag;
    }

    public int getVFlag() {
        return VFlag;
    }
}
