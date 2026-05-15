package com.xdel.luke.inventory;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InventoryBatchID {
    public String Batch_ID;
    public ArrayList<InventoryLite> invls;
    public ArrayList<String> logs;

    public InventoryBatchID(){}

    public InventoryBatchID(String aBatch_ID, ArrayList<InventoryLite> ainvls){
        this.Batch_ID = aBatch_ID;
        this.invls = ainvls;
    }

    public void setBatch_ID(String batch_ID) {
        Batch_ID = batch_ID;
    }

    public void setInventory(ArrayList<InventoryLite> ainvls) {
        this.invls = ainvls;
    }

    public void addLogs(String txt) {
        try {
            if (logs == null)
                logs = new ArrayList<>();

            String currentDateTime = "";
            Date dt = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            currentDateTime = formatter.format(dt);
            logs.add(currentDateTime + "\n" + txt);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryBatchID addLogs. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public ArrayList<String> getLogs() {
        return logs;
    }
}
