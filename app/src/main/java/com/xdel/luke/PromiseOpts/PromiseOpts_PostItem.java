package com.xdel.luke.PromiseOpts;

import java.util.Date;

public class PromiseOpts_PostItem {
    public int idx;
    public int value;
    public Date from;
    public Date to;
    public boolean IsSelected;

    public PromiseOpts_PostItem() {

    }

    public PromiseOpts_PostItem(int aidx, int avalue, Date afrom, Date ato){
        this.idx = aidx;
        this.value = avalue;
        this.from = afrom;
        this.to = ato;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public boolean getIsSelected() {
        return IsSelected;
    }

    public void setIsSelected(boolean selected) {
        IsSelected = selected;
    }
}
