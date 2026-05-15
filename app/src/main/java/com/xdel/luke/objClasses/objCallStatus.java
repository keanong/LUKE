package com.xdel.luke.objClasses;

import com.xdel.luke.HRM.ProfileActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class objCallStatus {
    List<String> statuses;
    long timeStart;

    public objCallStatus(){
        this.statuses = new ArrayList<>();
        this.timeStart = new Date().getTime();
    }

    public void addStatus(String s) {
        try {
            if (this.statuses == null) {
                this.statuses = new ArrayList<>();
            }

            this.statuses.add(s);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at objCallStatus addStatus. Exception: " + e.getMessage(),"","","","");
        }
    }

    public void setTimeStart() {
        this.timeStart = new Date().getTime();
    }

    public long getTimeStart() {
        return this.timeStart;
    }

    public boolean isEngaged(){
        boolean asd = false;
        try {
            asd = this.statuses != null && this.statuses.size() > 0 && !this.statuses.contains("EARLY") &&
                    !this.statuses.contains("CONNECTING") && !this.statuses.contains("CONFIRMED") && !this.statuses.contains("DISCONNCTD");
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at objCallStatus isEngaged. Exception: " + e.getMessage(),"","","","");
            asd = false;
        } finally {
            return asd;
        }
    }

    public boolean canCallAgain() {
        boolean asd = true;
        try {
            if (this.timeStart > 0) {
                long currTimeMS = new Date().getTime();
                long leftTimeMS = 60000 - (currTimeMS - this.timeStart);
                asd = leftTimeMS <= 0;
                this.timeStart = 0;
            }
        } catch (Exception e ) {
            objGlobalMethods.writeToFile("Exception at objCallStatus canCallAgain. Exception: " + e.getMessage(),"","","","");
            asd = false;
        } finally {
            return asd;
        }
    }

    public long secondsLeft(){
        long leftTimeMS = 0;
        try {
            if (this.timeStart > 0) {
                long currTimeMS = new Date().getTime();
                leftTimeMS = 60000 - (currTimeMS - this.timeStart);
                if (leftTimeMS > 0) {
                    leftTimeMS = leftTimeMS/1000;
                } else if (leftTimeMS <= 0) {
                    leftTimeMS = 0;
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at objCallStatus secondsLeft. Exception: " + e.getMessage(),"","","","");
        } finally {
            return leftTimeMS;
        }
    }
}
