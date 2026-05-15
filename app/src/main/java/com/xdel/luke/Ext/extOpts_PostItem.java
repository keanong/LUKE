package com.xdel.luke.Ext;

import java.util.Date;

public class extOpts_PostItem {
    public int idx;
    public String desc;
    public int value;
    public boolean IsSelected;
    public long JobsIDX;

    public extOpts_PostItem() {

    }

    public extOpts_PostItem(int aidx, String adesc, int avalue){
        this.idx = aidx;
        this.desc = adesc;
        this.value = avalue;
    }

    public void setJobsIDX(long jobsIDX) {
        JobsIDX = jobsIDX;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setIsSelected(boolean selected) {
        IsSelected = selected;
    }

}
