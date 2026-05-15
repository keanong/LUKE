package com.xdel.luke.credits;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class credit {
    public int cred_postalcount;
    public int cred_postalcountused;
    public int cred_postalcountlimit;
    public ArrayList<String> credOpenedPostals = new ArrayList<>();
    public ArrayList<String> credUsedPostals = new ArrayList<>();
    public ArrayList<String> TotalCredPostals = new ArrayList<>();
    public ArrayList<String> ListA = new ArrayList<>();
    public ArrayList<String> ManualQueuedPostalUsedList = new ArrayList<>();
    public ArrayList<String> TotalNCUsedPostals = new ArrayList<>();

    public credit() {

    }

    public void setTotalNCUsedPostals(ArrayList<String> totalNCUsedPostals) {
        TotalNCUsedPostals = totalNCUsedPostals;
    }

    public void addTotalNCUsedPostals(String aTotalNCUsedPostals) {
        try {
            if (TotalNCUsedPostals == null)
                TotalNCUsedPostals = new ArrayList<>();
            if (!objGlobalMethods.IsEmptyString(aTotalNCUsedPostals))
                TotalNCUsedPostals.add(aTotalNCUsedPostals);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at credit addTotalNCUsedPostals. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void setManualQueuedPostalUsedList(ArrayList<String> manualQueuedPostalUsedList) {
        ManualQueuedPostalUsedList = manualQueuedPostalUsedList;
    }

    public void setCred_postalcount(int cred_postalcount) {
        this.cred_postalcount = cred_postalcount;
    }

    public void setCred_postalcountused(int cred_postalcountused) {
        this.cred_postalcountused = cred_postalcountused;
    }

    public void setCred_postalcountlimit(int cred_postalcountlimit) {
        this.cred_postalcountlimit = cred_postalcountlimit;
    }

    public void setCredOpenedPostals(ArrayList<String> credOpenedPostals) {
        this.credOpenedPostals = credOpenedPostals;
    }

    public void addCredOpenedPostals(String postal) {
        try {
            if (this.credOpenedPostals == null)
                this.credOpenedPostals = new ArrayList<String>();
            this.credOpenedPostals.add(postal);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at credit addCredOpenedPostals. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void addcredUsedPostals(ArrayList<String> credusedpostals) {
        try {
            if (credUsedPostals == null)
                credUsedPostals = new ArrayList<>();
            if (credusedpostals != null && credusedpostals.size() > 0) {
                for (int i = 0; i <= credusedpostals.size() -1; i++) {
                    credUsedPostals.add(credusedpostals.get(i));
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at credit addcredUsedPostals. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void setCredUsedPostals(ArrayList<String> credUsedPostals) {
        this.credUsedPostals = credUsedPostals;
    }

    public void setListA(ArrayList<String> listA) {
        ListA = listA;
    }

    public void addTotalCredPostals(ArrayList<String> totalCredPostals) {
        try {
            if (TotalCredPostals == null)
                TotalCredPostals = new ArrayList<>();
            if (totalCredPostals != null && totalCredPostals.size() > 0) {
                for (int i = 0; i <= totalCredPostals.size() -1; i++) {
                    TotalCredPostals.add(totalCredPostals.get(i));
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at credit addTotalCredPostals. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void setTotalCredPostals(ArrayList<String> totalCredPostals) {
        TotalCredPostals = totalCredPostals;
    }
}
