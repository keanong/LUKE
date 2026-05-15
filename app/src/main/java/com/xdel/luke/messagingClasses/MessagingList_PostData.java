package com.xdel.luke.messagingClasses;

/**
 * Created by kean.ong on 14/12/2015.
 */
public class MessagingList_PostData {
    public String JobsIDX;
    public String UserIDX;
    public String UserName;
    public boolean changeCountColorBool;

    public MessagingList_PostData(String aJobsIDX, String aUserIDX, String aUserName, boolean achangeCountColorBool){
        this.JobsIDX = aJobsIDX;
        this.UserIDX = aUserIDX;
        this.UserName = aUserName;
        this.changeCountColorBool = achangeCountColorBool;

    }

}
