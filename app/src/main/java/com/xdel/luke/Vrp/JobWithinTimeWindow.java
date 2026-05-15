package com.xdel.luke.Vrp;

public class JobWithinTimeWindow {
    public Boolean TRUEFALSE;
    public long C1;
    public long C2;

    public JobWithinTimeWindow(){
        this.TRUEFALSE = true;
        this.C1 = 0;
        this.C2 = 0;
    }

    public void setTRUEFALSE(Boolean TRUEFALSE) {
        this.TRUEFALSE = TRUEFALSE;
    }

    public void setC1(long c1) {
        C1 = c1;
    }

    public void setC2(long c2) {
        C2 = c2;
    }
}
