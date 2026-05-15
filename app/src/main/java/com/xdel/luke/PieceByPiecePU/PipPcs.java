package com.xdel.luke.PieceByPiecePU;

public class PipPcs {
    public long JobsIDX;
    public int PCS;
    public int PCS_SCANNED;

    public PipPcs() {

    }

    public PipPcs(long aJobsIDX, int aPCS, int aPCS_SCANNED) {
        this.JobsIDX = aJobsIDX;
        this.PCS = aPCS;
        this.PCS_SCANNED = aPCS_SCANNED;
    }

    public void setJobsIDX(long jobsIDX) {
        JobsIDX = jobsIDX;
    }

    public void setPCS(int PCS) {
        this.PCS = PCS;
    }

    public void setPCS_SCANNED(int PCS_SCANNED) {
        this.PCS_SCANNED = PCS_SCANNED;
    }
}
