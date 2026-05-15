package com.xdel.luke;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.Vrp.JobQueue;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;

import java.util.ArrayList;
import java.util.List;

public class JobQHandler {

    public static List<JobQueue> getProcessJobQue() {
        final Gson gson = new Gson();
        Context ctx = myApp.getAppContext();
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        JobQueue[] res;
        List<JobQueue> NewJQlist = new ArrayList<>();
        String InitialSetOfJobsIDXsJsonStr = "";
        List<Long> InitialSetOfJobsIDXsArr = null;
        boolean InitialSetOfJobsIDXsArrIsProcessed = false;
        boolean newIDXsAddedToInitialSetOfJobsIDXsArr = false;
        String NonProcessJobQueJsonStr = "";
        String StdRunPostalCodeJsonStr = "";
        List<String> StdRunPostalCode = null;
        List<Long> existingJobsIDX = null;
        List<Long> existingPIPDIPJobsIDX = null;
        try {
            final String regid = objGlobalMethods.getRegistrationId(myApp.getAppContext(), gcmp);
            NonProcessJobQueJsonStr = objGlobalMethods.getVrpRelatedSP_KeyValue("NonProcessJobQueJsonStr");
            StdRunPostalCodeJsonStr = objGlobalMethods.getVrpRelatedSP_KeyValue("StdRunPostalCodeJsonStr");
            if (!objGlobalMethods.IsEmptyString(StdRunPostalCodeJsonStr))
                StdRunPostalCode = gson.fromJson(StdRunPostalCodeJsonStr, new TypeToken<List<String>>() {}.getType());
            else
                StdRunPostalCode = new ArrayList<>();

            if (!NonProcessJobQueJsonStr.equals("") || !NonProcessJobQueJsonStr.equals("[]")) {
                res = gson.fromJson(NonProcessJobQueJsonStr, JobQueue[].class);
                if (res != null && res.length > 0) {
                    existingPIPDIPJobsIDX = DAO_Jobs.getAllPIPDIPJobsIDXSQLstr();
                    existingJobsIDX = DAO_Jobs.getAllPIPPUDIPOKPFPFDPDJobsIDXSQLstr();

                    InitialSetOfJobsIDXsJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "InitialSetOfJobsIDXsJsonStr");
                    if (InitialSetOfJobsIDXsJsonStr != null && !InitialSetOfJobsIDXsJsonStr.isEmpty() && !InitialSetOfJobsIDXsJsonStr.equals("[]")
                            && !InitialSetOfJobsIDXsJsonStr.equals(""))
                        InitialSetOfJobsIDXsArr = gson.fromJson(InitialSetOfJobsIDXsJsonStr, new TypeToken<List<Long>>() {}.getType());


                    if (InitialSetOfJobsIDXsArr != null && InitialSetOfJobsIDXsArr.size() > 0) {
                        if (existingJobsIDX != null && existingJobsIDX.size() > 0) {
                            long InitialJobsIDX = 0;
                            for (int i = InitialSetOfJobsIDXsArr.size() - 1; i >= 0; i--) {
                                InitialJobsIDX = InitialSetOfJobsIDXsArr.get(i);
                                if (!existingJobsIDX.contains(InitialJobsIDX)) {
                                    InitialSetOfJobsIDXsArr.remove(i);
                                    InitialSetOfJobsIDXsArrIsProcessed = true;
                                }
                            }
                            if (InitialSetOfJobsIDXsArrIsProcessed) {
                                InitialSetOfJobsIDXsJsonStr = InitialSetOfJobsIDXsArr != null && InitialSetOfJobsIDXsArr.size() > 0 ? gson.toJson(InitialSetOfJobsIDXsArr) : "";
                                objGlobalMethods.setGCMPrefs(gcmp, "InitialSetOfJobsIDXsJsonStr", InitialSetOfJobsIDXsJsonStr);
                            }
                        } else {
                            InitialSetOfJobsIDXsArrIsProcessed = true;
                            objGlobalMethods.setGCMPrefs(gcmp, "InitialSetOfJobsIDXsJsonStr", "");
                        }

                        if (existingPIPDIPJobsIDX != null && existingPIPDIPJobsIDX.size() > 0) {
                            long idx = 0;
                            for (int i = 0; i <= existingPIPDIPJobsIDX.size() - 1; i++) {
                                idx = existingPIPDIPJobsIDX.get(i);
                                if (!InitialSetOfJobsIDXsArr.contains(idx)) {
                                    InitialSetOfJobsIDXsArr.add(idx);
                                    newIDXsAddedToInitialSetOfJobsIDXsArr = true;
                                }
                            }
                            /*if (newIDXsAddedToInitialSetOfJobsIDXsArr && !objGlobalMethods.IsEmptyString(saveAmended4QuesJsonStr)) {
                                InitialSetOfJobsIDXsJsonStr = InitialSetOfJobsIDXsArr != null && InitialSetOfJobsIDXsArr.size() > 0 ? gson.toJson(InitialSetOfJobsIDXsArr) : "";
                                objGlobalMethods.setGCMPrefs(gcmp, "InitialSetOfJobsIDXsJsonStr", InitialSetOfJobsIDXsJsonStr);
                                ////update luke_cabinet
                                JobsHandler2.updateCabinetDueNewIDXsAddedToInitialSetOfJobsIDXsArr(gcmp, regid, saveAmended4QuesJsonStr, InitialSetOfJobsIDXsJsonStr);
                            }*/
                        }
                    }

                } else {
                    JobQueue nr = new JobQueue(null, null, null, null, null, null, null, null, null, null, null, null, null, false);
                    nr.setAddressWithPostalOnly(ctx.getString(R.string.lblnojobs));
                    nr.setIsHeader(false);
                    //nr.setID(NewJQlist.size() + 1);
                    nr.setItemID(NewJQlist.size());
                    nr.setID(NewJQlist.size());
                    nr.setOldID(NewJQlist.size());
                    nr.setStartID(NewJQlist.size());
                    nr.setIsEnabled(false);
                    nr.setCanClick(false);
                    nr.setAllJobsCompleted(true);
                    nr.setType("vrp");
                    NewJQlist.add(nr);
                }
            } else {
                JobQueue nr = new JobQueue(null, null, null, null, null, null, null, null, null, null, null, null, null, false);
                nr.setAddressWithPostalOnly(ctx.getString(R.string.lblnojobs));
                nr.setIsHeader(false);
                //nr.setID(NewJQlist.size() + 1);
                nr.setItemID(NewJQlist.size());
                nr.setID(NewJQlist.size());
                nr.setOldID(NewJQlist.size());
                nr.setStartID(NewJQlist.size());
                nr.setIsEnabled(false);
                nr.setCanClick(false);
                nr.setAllJobsCompleted(true);
                nr.setType("vrp");
                NewJQlist.add(nr);
            }

        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at JobQHandler getProcessJobQue. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            NewJQlist = null;
        } finally {
            objGlobalMethods.setGCMPrefs(gcmp, "currPostal", "");
            objGlobalMethods.setGCMPrefs(gcmp, "earlierPostal", "");
            return NewJQlist;
        }
    }
}
