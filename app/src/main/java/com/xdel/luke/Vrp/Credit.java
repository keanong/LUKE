package com.xdel.luke.Vrp;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.Date;

public class Credit {
    public long CreditDate;
    public int TotalCredits;
    public int UsedCredits;
    public int AvailCredits;

    public int getAvailCredits() {
        return AvailCredits;
    }

    public int getTotalCredits() {
        return TotalCredits;
    }

    public int getUsedCredits() {
        return UsedCredits;
    }

    public long getCreditDate() {
        return CreditDate;
    }

    public void setAvailCredits(int availCredits) {
        AvailCredits = availCredits;
    }

    public void setCreditDate(long creditDate) {
        CreditDate = creditDate;
    }

    public void setTotalCredits(int totalCredits) {
        TotalCredits = totalCredits;
    }

    public void setUsedCredits(int usedCredits) {
        UsedCredits = usedCredits;
    }

    public void saveCredits(Credit c) {
        try {
            SharedPreferences vrpsp = objGlobalMethods.getVrpRelatedSharedPreferences();
            Gson gson = new Gson();
            String CreditsJson = gson.toJson(c);
            objGlobalMethods.setGCMPrefs(vrpsp, "CreditsJson", CreditsJson);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Credit saveCredits. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void resetCredits(){
        try {
            this.CreditDate = new Date().getTime();
            this.TotalCredits = 0;
            this.UsedCredits = 0;
            this.AvailCredits = 0;
            SharedPreferences vrpsp = objGlobalMethods.getVrpRelatedSharedPreferences();
            Gson gson = new Gson();
            String CreditsJson = gson.toJson(this);
            objGlobalMethods.setGCMPrefs(vrpsp, "CreditsJson", CreditsJson);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Credit resetCredits. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }
}
