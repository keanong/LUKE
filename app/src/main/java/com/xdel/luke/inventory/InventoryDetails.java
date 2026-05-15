package com.xdel.luke.inventory;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryDetails {
    public long IDX;
    public long InventoryIDX;
    public String Timestamp;
    public long JobsIDX;
    public String DeliveryOrder;
    public int StockMovement;
    public String Remarks;
    public int Flag;
    public String Batch_ID;
    public String[] StockReferences;
    public List<String> NVStockReferences;

    public InventoryDetails(){

    }

    public void setNVStockReferences(String[] NVStockReferences) {
        this.NVStockReferences = NVStockReferences != null && NVStockReferences.length > 0 ? new ArrayList<>(Arrays.asList(NVStockReferences)): null;
    }

    public void removeNVStockReferences(String ref) {
        try {
            if (this.NVStockReferences != null && this.NVStockReferences.size() > 0) {
                for (int i = 0; i <= this.NVStockReferences.size() - 1; i++) {
                    if (NVStockReferences.get(i).toUpperCase().equals(ref.toUpperCase())) {
                        this.NVStockReferences.remove(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryDetails removeNVStockReferences. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }
}
