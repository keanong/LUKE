package com.xdel.luke.templogger;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class bttl_saved_log_obj {
    public String ID;
    public ArrayList<TemperaturePayload> tempPayLoad;

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setTempPayLoad(TemperaturePayload tempPayLoad) {
        try {
            if (this.tempPayLoad == null)
                this.tempPayLoad = new ArrayList<>();

            this.tempPayLoad.add(tempPayLoad);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at bttl_saved_log_obj setTempPayLoad. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }
}
