package com.xdel.luke.jobinfo;

public class ArticleTag {
    public long jobsidx;
    public String[] BarcodeTags;

    public ArticleTag() {

    }

    public ArticleTag(long ajobsidx, String[] aBarcodeTags) {
        this.jobsidx = ajobsidx;
        this.BarcodeTags = aBarcodeTags;
    }

}
