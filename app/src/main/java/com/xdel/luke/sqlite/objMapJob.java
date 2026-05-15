package com.xdel.luke.sqlite;

public class objMapJob {
    public Long[] JobsIDX;
    public int PIPcount;
    public int DIPcount;

    public objMapJob() {

    }

    public void setJobsIDX(Long[] jobsIDX) {
        JobsIDX = jobsIDX;
    }

    public void setPIPcount(int PIPcount) {
        this.PIPcount = PIPcount;
    }

    public void setDIPcount(int DIPcount) {
        this.DIPcount = DIPcount;
    }
}
