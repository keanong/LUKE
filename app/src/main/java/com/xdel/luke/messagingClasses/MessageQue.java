package com.xdel.luke.messagingClasses;

/**
 * Created by kean.ong on 14/12/2015.
 */
public class MessageQue {
    public String ARegID;
    public ChatMessage Message;
    public String AUserIDX;
    public String AJobsIDX;

    public MessageQue(String aARegID, ChatMessage aMessage, String aAUserIDX, String aAJobsIDX){
        this.ARegID = aARegID;
        this.Message = aMessage;
        this.AUserIDX = aAUserIDX;
        this.AJobsIDX = aAJobsIDX;
    }
}
