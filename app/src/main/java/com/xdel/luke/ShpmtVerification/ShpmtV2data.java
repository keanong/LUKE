package com.xdel.luke.ShpmtVerification;

import com.xdel.luke.jobinfo.JobInfoLite;

public class ShpmtV2data {
    public JobInfoLite ji;
    public int expectedc;
    public int actualc;
    public int jxcount;
    public int jccount;
    public int valcount;
    public boolean verified;
    public boolean blink;

    public ShpmtV2data(){}

    public ShpmtV2data(JobInfoLite aji, int aactualc) {
        this.ji = aji;
        this.expectedc = aji.pcs;
        this.actualc = aactualc;
    }

    public void setJi(JobInfoLite ji) {
        this.ji = ji;
    }

    public int getActualc() {
        return actualc;
    }

    public void setActualc(int actualc) {
        this.actualc += actualc;
        verified = !ji.isAlps ? (this.actualc > 0) : (this.actualc >= expectedc);
    }

    public void setActualc2(int actualc) {
        this.actualc = actualc;
        verified = !ji.isAlps ? (this.actualc > 0) : (this.actualc >= expectedc);
    }

    public void setJxcount(int jxcount) {
        this.jxcount += jxcount;
    }

    public void setJccount(int jccount) {
        this.jccount += jccount;
    }

    public void setValcount(int valcount) {
        this.valcount += valcount;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setBlink(boolean blink) {
        this.blink = blink;
    }
}
