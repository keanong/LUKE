package com.xdel.luke.Caging;

public class objCageInOutItem {
    public String ref;
    public boolean IsNotPartOfCage;
    public String job_ref;
    public long JobsIDX;
    public int color = 0;
    public boolean blink = false;

    public objCageInOutItem(String aref, boolean aIsNotPartOfCage, String ajob_ref, long aJobsIDX){
        this.ref = aref;
        this.IsNotPartOfCage = aIsNotPartOfCage;
        this.job_ref = ajob_ref;
        this.JobsIDX = aJobsIDX;
        this.blink = false;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setNotPartOfCage(boolean notPartOfCage) {
        IsNotPartOfCage = notPartOfCage;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setBlink(boolean ablink) {
        this.blink = ablink;
    }
}
