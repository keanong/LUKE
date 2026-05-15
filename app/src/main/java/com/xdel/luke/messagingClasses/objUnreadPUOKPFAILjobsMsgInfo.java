package com.xdel.luke.messagingClasses;

public class objUnreadPUOKPFAILjobsMsgInfo {
    public long jobsidx;
    public long idx;
    public long useridx;
    public int count;

    public objUnreadPUOKPFAILjobsMsgInfo() {

    }

    public objUnreadPUOKPFAILjobsMsgInfo(long ajobsidx, long aidx, long auseridx, int acount) {
        this.jobsidx = ajobsidx;
        this.idx = aidx;
        this.useridx = auseridx;
        this.count = acount;
    }

    public void setJobsidx(long jobsidx) {
        this.jobsidx = jobsidx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public void setUseridx(long useridx) {
        this.useridx = useridx;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
