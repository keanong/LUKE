package com.xdel.luke.objClasses;

import java.util.List;

/**
 * Created by kean.ong on 23/08/2016.
 */
public class objRouteLocationList {
    public List<String> claddressidx;
    public List<List<Long>> JobsIDXarrList;

    public objRouteLocationList(List<String> acladdressidx, List<List<Long>> aJobsIDXarrList){
        this.claddressidx = acladdressidx;
        this.JobsIDXarrList = aJobsIDXarrList;
    }

    public List<String> getcladdressidx(){
        return this.claddressidx;
    }

    public void setcladdressidx(List<String> acladdressidx){
        this.claddressidx = acladdressidx;
    }

    public List<List<Long>> getJobsIDXarrList(){
        return this.JobsIDXarrList;
    }

    public void setJobsIDXarrList(List<List<Long>> aJobsIDXarrList){
        this.JobsIDXarrList = aJobsIDXarrList;
    }

}
