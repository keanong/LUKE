package com.xdel.luke.objClasses;

public class xwsBase {
    public int Status;
    public String Message;
    public String ExMessage = "";

    public void setStatus(int status) {
        Status = status;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public void setExMessage(String exMessage) {
        ExMessage = exMessage;
    }
}
