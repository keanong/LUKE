package com.xdel.luke.HRM_Leave;

public class objLeaveBlock {
    public long longfrom;
    public long longto;

    public objLeaveBlock(){

    }

    public objLeaveBlock(long alongfrom, long alongto){
        this.longfrom = alongfrom;
        this.longto = alongto;
    }

    public void setLongfrom(long longfrom) {
        this.longfrom = longfrom;
    }

    public long getLongfrom() {
        return longfrom;
    }

    public long getLongto() {
        return longto;
    }

    public void setLongto(long longto) {
        this.longto = longto;
    }
}
