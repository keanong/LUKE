package com.xdel.luke.objClasses;

import java.util.ArrayList;

public class checklistObjForMassOKP {
    public String JobsIDXs;
    public String[] checklist;
    public Integer FLAG;
    public ArrayList<String> OTPList;
    public String CustID;

    public String getJobsIDXs() {
        return this.JobsIDXs;
    }

    public String[] getChecklist() {
        return this.checklist;
    }

    public long getFlag() {
        return this.FLAG;
    }

    public void addJobsIDX(String idx) {
        if (!objGlobalMethods.IsEmptyString(this.JobsIDXs)) {
            this.JobsIDXs += "," + idx;
        } else {
            this.JobsIDXs = idx;
        }
    }

    public void setChecklist(String[] checklist) {
        this.checklist = checklist;
    }

    public void setFlag(Integer flag) {
        FLAG = flag;
    }

    public void setOTPList(ArrayList<String> OTPList) {
        this.OTPList = OTPList;
    }

    public void setCustID(String custID) {
        CustID = custID;
    }
}
