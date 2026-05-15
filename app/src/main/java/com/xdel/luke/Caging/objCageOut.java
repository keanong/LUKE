package com.xdel.luke.Caging;

public class objCageOut {
    public long ACageIDX;
    public String[] AItems;
    public String[] Excluded;
    public String AItemsStr;
    public String ExcludedStr;

    public objCageOut(long aACageIDX, String[] aAItems, String[] aExcluded) {
        this.ACageIDX = aACageIDX;
        this.AItems = aAItems;
        this.Excluded = aExcluded;
    }

    public void setAItemsStr(String AItemsStr) {
        this.AItemsStr = AItemsStr;
    }

    public void setExcludedStr(String excludedStr) {
        ExcludedStr = excludedStr;
    }
}
