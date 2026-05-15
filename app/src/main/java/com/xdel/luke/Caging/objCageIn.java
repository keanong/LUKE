package com.xdel.luke.Caging;

import java.util.ArrayList;

public class objCageIn {
    public String APIKey;
    public long AUserIDX;
    public long ACageIDX;
    public String NotBefore;
    public ArrayList<String> AItems;
    public String AItemsStr;

    public objCageIn (long aAUserIDX, long aACageIDX, String aNotBefore, ArrayList<String> aAItems, String aAItemsStr){
        this.APIKey = "EED6AD08118FAB9AAEFF756C1B00101254F8B6C0";
        this.AUserIDX = aAUserIDX;
        this.ACageIDX = aACageIDX;
        this.NotBefore = aNotBefore;
        this.AItems = aAItems;
        this.AItemsStr = aAItemsStr;
    }
}
