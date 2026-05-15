package com.xdel.luke.messagingClasses;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by kean.ong on 26/11/2015.
 */
public class ChatMessage  implements Comparable<ChatMessage> {

    private long id;
    private String messageText;
    private UserType userType;
    private Status messageStatus;
    private Long ackIDX;
    public Long messageDate;
    private long messageTime;
    public String jsondate;
    public Integer Direction;

    public ChatMessage(){

    }

    public ChatMessage(long aid, String amessageText, UserType auserType, Status amessageStatus, Long aackIDX, Long amessageDate, long amessageTime){
        this.id = aid;
        this.messageText = amessageText;
        this.userType = auserType;
        this.messageStatus = amessageStatus;
        this.ackIDX = aackIDX;
        this.messageDate = amessageDate;
        this.messageTime = amessageTime;
    }

    public void setDirection(Integer aDirection){
        this.Direction = aDirection;
    }

    public long getID(){
        return this.id;
    }

    public void setID(long aid){
        this.id = aid;
    }

    public Long getAckIDX(){
        return this.ackIDX;
    }

    public void setAckIDX(Long aackIDX){
        this.ackIDX = aackIDX;
    }

    public void setjsondate(String ajsondate) {
        this.jsondate = ajsondate;
    }

    public void setmessageDate(long amessageDate) {
        this.messageDate = amessageDate;
    }
    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setMessageStatus(Status messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageText() {

        return messageText;
    }

    public UserType getUserType() {
        return userType;
    }

    public Status getMessageStatus() {
        return messageStatus;
    }

    public static class OrderbymessageDate implements Comparator<ChatMessage> {
        @Override
        public int compare(ChatMessage a, ChatMessage b){
            return a.messageDate > b.messageDate ? 1 : (a.messageDate < b.messageDate ? - 1 : 0);
        }
    }

    public int compareTo(ChatMessage other){
        return this.messageDate > other.messageDate ? 1 : (this.messageDate < other.messageDate ? - 1 : 0);
    }
}
