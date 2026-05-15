package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 11/04/2017.
 */
public class DOinvCheck_PostData {

    //public Long InvoicesIDX;
    public String tag;
    public boolean selected;
    public Long JobsIDX;
    public String JobNoOrCNNO;
    public objJobInfo jobinfo;
    public Integer total;

    public DOinvCheck_PostData(Long aJobsIDX, String atag, boolean aselected, String aJobNoOrCNNO, objJobInfo ji, Integer atotal){
        this.JobsIDX = aJobsIDX;
        //this.InvoicesIDX = aInvoicesIDX;
        this.tag = atag;
        this.selected = aselected;
        this.JobNoOrCNNO = aJobNoOrCNNO;
        this.jobinfo = ji;
        this.total = atotal;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
