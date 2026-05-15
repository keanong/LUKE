package com.xdel.luke.objClasses;

import java.util.Date;

/**
 * Created by kean.ong on 05/07/2016.
 */
public class RouteLocation {
    public Location Location;
    public String Arrive;
    public String Depart;
    public Long[] JobsIDXList;
    public Long[] PUJobsIDXList;
    public Long[] DLJobsIDXList;
    public boolean newPIP;
    public boolean IsEnabledForJobUpdate;

    public void setEnabledForJobUpdate(boolean enabledForJobUpdate) {
        IsEnabledForJobUpdate = enabledForJobUpdate;
    }

    public Long[] getJobsIDXList(){
        return this.JobsIDXList;
    }
    public void setJobsIDXList(Long[] aJobsIDXList){
        this.JobsIDXList = aJobsIDXList;
    }

    public void setPUJobsIDXList(Long[] PUJobsIDXList) {
        this.PUJobsIDXList = PUJobsIDXList;
    }

    public void setDLJobsIDXList(Long[] DLJobsIDXList) {
        this.DLJobsIDXList = DLJobsIDXList;
    }

    public void setNewPIP(boolean newPIP) {
        this.newPIP = newPIP;
    }

    public void setLocation(com.xdel.luke.objClasses.Location location) {
        Location = location;
    }
}
