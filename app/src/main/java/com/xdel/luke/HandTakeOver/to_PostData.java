package com.xdel.luke.HandTakeOver;

public class to_PostData {

    public String ref;
    public boolean selected;
    public boolean blink;

    public to_PostData() {

    }

    public to_PostData(String aref, boolean aselected) {
        this.ref = aref;
        this.selected = aselected;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setBlink(boolean blink) {
        this.blink = blink;
    }

}
