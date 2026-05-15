package com.xdel.luke.messagingClasses;

/**
 * Created by kean.ong on 17/11/2017.
 */

public class objChatMessage {
    public String TimeStamp;
    public long IDX;
    public long UserIDX;
    public String UserName;
    public long JobsIDX;
    public String Message;
    public Integer Direction;
    public Integer MessageStatus;
    public Integer UserType;
    public long ackIDX;

    public objChatMessage(String aTimeStamp, long aIDX, long aUserIDX, String aUserName, long aJobsIDX, String aMessage, Integer aDirection, Integer aMessageStatus, Integer aUserType, long aackIDX) {
        this.TimeStamp = aTimeStamp;
        this.IDX = aIDX;
        this.UserIDX = aUserIDX;
        this.UserName = aUserName;
        this.JobsIDX = aJobsIDX;
        this.Message = aMessage;
        this.Direction = aDirection;
        this.MessageStatus = aMessageStatus;
        this.UserType = aUserType;
        this.ackIDX = aackIDX;
    }

    public void setIDX(long aidx) {
        this.IDX = aidx;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
