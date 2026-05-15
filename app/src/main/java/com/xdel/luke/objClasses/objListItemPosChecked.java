package com.xdel.luke.objClasses;

public class objListItemPosChecked implements Comparable<objListItemPosChecked>{
    Integer pos;
    boolean checked;
    public objListItemPosChecked(Integer apos, boolean achecked) {
        this.pos = apos;
        this.checked = achecked;
    }

    public void setIsCheck_N_NewPos(boolean chk, Integer apos) {
        this.checked = chk;
        //this.pos = apos;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int compareTo(objListItemPosChecked other){
        return pos.compareTo(other.pos);
    }
}
