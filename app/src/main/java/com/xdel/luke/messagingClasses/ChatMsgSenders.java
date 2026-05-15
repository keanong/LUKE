package com.xdel.luke.messagingClasses;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by kean.ong on 14/12/2015.
 */
public class ChatMsgSenders  implements Comparable<ChatMsgSenders>{
    public String UserIDX;
    public String UserName;
    public ArrayList<ChatMessage> chatMessages;
    public String messageTime;
    public Boolean Read = false;
    public int notiID;
    public Long msgDateLong;
    public String JobsIDX;
    public String UN;

    public ChatMsgSenders(String aUserIDX, String aUserName, ArrayList<ChatMessage> achatMessages, String amessageTime, Boolean aRead, int anotiID, Long amsgDateLong, String aJobsIDX){
        this.UserIDX = aUserIDX;
        this.UserName = aUserName;
        this.chatMessages = achatMessages;
        this.messageTime = amessageTime;
        this.Read = aRead;
        this.notiID = anotiID;
        this.msgDateLong = amsgDateLong;
        this.JobsIDX = aJobsIDX;
    }

    public static class OrderbymsgDateLong implements Comparator<ChatMsgSenders>{
        @Override
        public int compare(ChatMsgSenders a, ChatMsgSenders b){
            return a.msgDateLong > b.msgDateLong ? 1 : (a.msgDateLong < b.msgDateLong ? - 1 : 0);
        }
    }

    public int compareTo(ChatMsgSenders other){
        return this.msgDateLong > other.msgDateLong ? 1 : (this.msgDateLong < other.msgDateLong ? - 1 : 0);
    }

    public void setUN(String aUN) {
        this.UN = aUN;
    }
}