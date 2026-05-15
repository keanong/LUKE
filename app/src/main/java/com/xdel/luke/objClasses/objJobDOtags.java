package com.xdel.luke.objClasses;

import java.util.List;

/**
 * Created by kean.ong on 17/04/2017.
 */
public class objJobDOtags {
    public Long JobsIDX;
    public List<String> tags;
    public int totalcount;
    public int selectedcount;

    public objJobDOtags(Long aJobsIDX, List<String> atags, int atotalcount, int aselectedcount) {
        this.JobsIDX = aJobsIDX;
        this.tags = atags;
        this.totalcount = atotalcount;
        this.selectedcount = aselectedcount;
    }

    public void settags(List<String> atags) {
        this.tags = atags;
    }

    public void setSelectedcount(int aselectedcount){
        this.selectedcount = aselectedcount;
    }
}
