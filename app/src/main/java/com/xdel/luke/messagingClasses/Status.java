package com.xdel.luke.messagingClasses;

/**
 * Created by kean.ong on 26/11/2015.
 */
public enum Status {
    SENT, DELIVERED;
    private static Status[] values = Status.values();

    public static Status getStatus(int i) {
        return values[i];
    }
}
