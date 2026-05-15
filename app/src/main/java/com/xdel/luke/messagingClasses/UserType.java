package com.xdel.luke.messagingClasses;

/**
 * Created by kean.ong on 26/11/2015.
 */
public enum UserType {
    OTHER, SELF, DATE;
    private static UserType[] values = UserType.values();

    public static UserType getUserType(int i) {
        return values[i];
    }
}
