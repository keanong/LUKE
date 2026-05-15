package com.xdel.luke.PieceByPiecePU;

public class SQHeaderJobsPcsCount {
    public int JobsCount = 0;
    public int PcsCount = 0;
    public int PUCount = 0;

    public SQHeaderJobsPcsCount() {

    }

    public SQHeaderJobsPcsCount(int aJobsCount, int aPcsCount, int aPUCount) {
        this.JobsCount = aJobsCount;
        this.PcsCount = aPcsCount;
        this.PUCount = aPUCount;
    }

    public void setJobsCount(int jobsCount) {
        JobsCount += jobsCount;
    }

    public void setPcsCount(int pcsCount) {
        PcsCount += pcsCount;
    }

    public void setPcsCountMinus() {
        PcsCount -= 1;
    }

}
