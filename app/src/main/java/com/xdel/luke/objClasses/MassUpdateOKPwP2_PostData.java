package com.xdel.luke.objClasses;

import java.util.ArrayList;

/**
 * Created by kean.ong on 18/01/2016.
 */
public class MassUpdateOKPwP2_PostData {
    public String Part1JobsIDX;
    public String Part2JobsIDX;
    public String Part1JobNo;
    public String Part2ImageString;
    public String StatusCodesIDX;
    public String FailureReason;
    public objJobInfo currJobInfo;
    public ArrayList<String> Part2convertedBitmapToBase64;

    public MassUpdateOKPwP2_PostData(String aPart1JobsIDX, String aPart2JobsIDX, String aPart1JobNo, String aPart2ImageString,
                                     String aStatusCodesIDX, String aFailureReason, objJobInfo acurrJobInfo, ArrayList<String> aPart2convertedBitmapToBase64){
        this.Part1JobsIDX = aPart1JobsIDX;
        this.Part2JobsIDX = aPart2JobsIDX;
        this.Part1JobNo = aPart1JobNo;
        this.Part2ImageString = aPart2ImageString;
        this.StatusCodesIDX = aStatusCodesIDX;
        this.FailureReason = aFailureReason;
        this.currJobInfo = acurrJobInfo;
        this.Part2convertedBitmapToBase64 = aPart2convertedBitmapToBase64;
    }

    public void setStatusCodesIDX(String statuscodeidx){
        this.StatusCodesIDX = statuscodeidx;
    }

}
