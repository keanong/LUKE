package com.xdel.luke.jobinfo;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objDOObject;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.List;

public class JobInfoLite {
    public long JobsIDX;
    public String JobNo;
    public objDOObject[] DOList;
    public String[] BarcodeTags;
    public String[] BarcodeTagsExt;
    public String ConsignmentNote;
    public boolean hasColdItem;
    public boolean isAlps;
    public boolean isLocker;
    public boolean isStandardRunJob;
    public int pcs;
    public String DelBlock;
    public String DelUnit_original;
    public String DelUnit_combine;
    public List<String> AllDelUnits;

    public JobInfoLite(long aJobsIDX, String aJobNo, objDOObject[] aDOList, String[] aBarcodeTags, String[] aBarcodeTagsExt, String aConsignmentNote, boolean ahasColdItem, boolean aisAlps, boolean aisLocker, boolean aisStandardRunJob, int apcs) {
        this.JobsIDX = aJobsIDX;
        this.JobNo = aJobNo;
        this.DOList = aDOList;
        this.BarcodeTags = aBarcodeTags;
        this.BarcodeTagsExt = aBarcodeTagsExt;
        this.ConsignmentNote = aConsignmentNote;
        this.hasColdItem = ahasColdItem;
        this.isAlps = aisAlps;
        this.isLocker = aisLocker;
        this.isStandardRunJob = aisStandardRunJob;
        this.pcs = apcs;
    }

    public String getDelBlock() {
        return DelBlock;
    }

    public void setDelBlock(String delBlock) {
        DelBlock = delBlock;
    }

    public String getDelUnit_original() {
        return DelUnit_original;
    }

    public void setDelUnit_original(String delUnit_original) {
        DelUnit_original = delUnit_original;
    }

    public String getDelUnit_combine() {
        return DelUnit_combine;
    }

    public void setDelUnit_combine(String delUnit_combine) {
        DelUnit_combine = delUnit_combine;
    }

    public List<String> getAllDelUnits() {
        return AllDelUnits;
    }

    public void setAllDelUnits(List<String> allDelUnits) {
        AllDelUnits = allDelUnits;
    }

}
