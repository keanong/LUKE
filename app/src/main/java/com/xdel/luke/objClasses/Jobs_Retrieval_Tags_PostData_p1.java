package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 15/11/2017.
 */

public class Jobs_Retrieval_Tags_PostData_p1 {
    public long JobsIDX;
    public String JobNo;
    public String DateStr;
    public String JobInfoJsonStr;
    public String Address;
    public String contenttype;
    public String SecuritySealNumber;

    public Jobs_Retrieval_Tags_PostData_p1(long aJobsIDX, String aJobNo, String aDateStr, String aJobInfoJsonStr, String aAddress, String acontenttype, String aSecuritySealNumber){
        this.JobsIDX = aJobsIDX;
        this.JobNo = aJobNo;
        this.DateStr = aDateStr;
        this.JobInfoJsonStr = aJobInfoJsonStr;
        this.Address = aAddress;
        this.contenttype = acontenttype;
        this.SecuritySealNumber = aSecuritySealNumber;
    }

}
