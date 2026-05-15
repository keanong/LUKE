package com.xdel.luke.jobinfo;

import com.xdel.luke.objClasses.objDOObject;

public class DO {
    public long jobsidx;
    public objDOObject[] DOList;

    public DO() {

    }

    public DO(long ajobsidx, objDOObject[] aDOList) {
        this.jobsidx = ajobsidx;
        this.DOList = aDOList;
    }
}
