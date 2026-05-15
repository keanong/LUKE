package com.xdel.luke.HRM;

public class objHRM_Info {
    public String title;
    public int pageid;

    public objHRM_Info(String atitle, int apageid){
        this.title = atitle;
        this.pageid = apageid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPageid() {
        return pageid;
    }

    public void setPageid(int pageid) {
        this.pageid = pageid;
    }
}
