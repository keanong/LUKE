package com.xdel.luke.HandTakeOver;

public class HOTOItem {
    public long IDX;
    public long JobsIDX;
    public String HO_Reference;
    public String TO_Reference;
    public int Flag = 0;

    public HOTOItem(String ref) {
        this.Flag = 0;
        this.HO_Reference = ref;
        this.TO_Reference = ref;
    }

    public void setHO_Reference(String HO_Reference) {
        this.HO_Reference = HO_Reference;
    }

    public void setTO_Reference(String TO_Reference) {
        this.TO_Reference = TO_Reference;
    }
}
