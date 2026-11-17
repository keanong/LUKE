package com.xdel.luke;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.PieceByPiecePU.PipPcs;
import com.xdel.luke.Rooting.RootGuard;
import com.xdel.luke.Vrp.JobQueue;
import com.xdel.luke.checklist.ChecklistVariables;
import com.xdel.luke.credits.credit;
import com.xdel.luke.inventory.Inventory;
import com.xdel.luke.inventory.InventoryBatchID;
import com.xdel.luke.inventory.InventoryDetails;
import com.xdel.luke.inventory.InventoryLite;
import com.xdel.luke.inventory.StockRef;
import com.xdel.luke.inventory.StockVerificationList;
import com.xdel.luke.inventory.UploadStockVerificationList;
import com.xdel.luke.inventory.VerifiedItem;
import com.xdel.luke.jobinfo.JobInfoLite;
import com.xdel.luke.messagingClasses.ChatMessage;
import com.xdel.luke.messagingClasses.ChatMsgSenders;
import com.xdel.luke.messagingClasses.objUnreadPUOKPFAILjobsMsgInfo;
import com.xdel.luke.objClasses.DriverFlag;
import com.xdel.luke.objClasses.jobSelectionInfo;
import com.xdel.luke.objClasses.lastUpdatedLocationCustom;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCabinet;
import com.xdel.luke.objClasses.objCheckedItems;
import com.xdel.luke.objClasses.objDOCOD;
import com.xdel.luke.objClasses.objDOObject;
import com.xdel.luke.objClasses.objETACabinet;
import com.xdel.luke.objClasses.objGC_CustomQueue;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.settings;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.BaseDeviceScanObj;
import com.xdel.luke.templogger.UploadTempDataObj;
import com.xdel.luke.templogger.Upload_TemperatureResult;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import static com.xdel.luke.JobsHandler.sendErrorEmail;

public class JobsHandler2 {
    public static final Semaphore mutex = new Semaphore(1);
    public static boolean backEnd2IsRunning = false;
    private static ConnectionDetector cd;

    public static ChecklistVariables getChecklistVariables() {
        ChecklistVariables cv = new ChecklistVariables();
        String ChecklistVariablesJsonStr = "";
        try {
            ChecklistVariablesJsonStr = objGlobalMethods.getChecklistRelatedSP_KeyValue("ChecklistVariablesJsonStr");
            if (!objGlobalMethods.IsEmptyString(ChecklistVariablesJsonStr)) {
                Gson g = new Gson();
                cv = g.fromJson(ChecklistVariablesJsonStr, cv.getClass());
                if (cv != null) {
                    SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                    String hasOtpStr = objGlobalMethods.getGCMPrefs(gcmp, "hasOtpStr");
                    Boolean hasOTP = !objGlobalMethods.IsEmptyString(hasOtpStr);
                    String hasVerifiedOTPStr = objGlobalMethods.getGCMPrefs(gcmp, "hasVerifiedOTPStr");
                    Boolean hasVerifiedOTP = !objGlobalMethods.IsEmptyString(hasVerifiedOTPStr);
                    String CustIDVerificationNotRequiredStr = objGlobalMethods.getGCMPrefs(gcmp, "CustIDVerificationNotRequiredStr");
                    boolean CustIDVerificationNotRequired = !objGlobalMethods.IsEmptyString(CustIDVerificationNotRequiredStr);
                    String isLockerJobStr = objGlobalMethods.getGCMPrefs(gcmp, "isLockerJobStr");
                    boolean isLockerJob = !objGlobalMethods.IsEmptyString(isLockerJobStr);
                    String CurrJobInfoStatusCodeIDX = objGlobalMethods.getGCMPrefs(gcmp, "CurrJobInfoStatusCodeIDX");
                    long StatusCodeIDX = !objGlobalMethods.IsEmptyString(CurrJobInfoStatusCodeIDX) ? Long.parseLong(CurrJobInfoStatusCodeIDX) : 0;
                    String currJobInfoFlagStr = objGlobalMethods.getGCMPrefs(gcmp, "currJobInfoFlag");
                    Integer FLAG = !objGlobalMethods.IsEmptyString(currJobInfoFlagStr) ? Integer.parseInt(currJobInfoFlagStr) : 0;
                    String TypeOfPinVerificationStr = objGlobalMethods.getGCMPrefs(gcmp, "TypeOfPinVerificationStr");
                    String hideshow10to19NewForced = objGlobalMethods.getGCMPrefs(gcmp, "hideshow10to19NewForced");
                    Integer TypeOfPinVerification = !objGlobalMethods.IsEmptyString(TypeOfPinVerificationStr) ? Integer.parseInt(TypeOfPinVerificationStr) : 0;
                    String currJobInfoIDXStr = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoIDX);
                    long JobsIDX = !objGlobalMethods.IsEmptyString(currJobInfoIDXStr) ? Long.parseLong(currJobInfoIDXStr) : 0;
                    String hasIMEI_Verification = objGlobalMethods.getGCMPrefs(gcmp, "hasIMEI_Verification");
                    String hasSerialNum_Verification = objGlobalMethods.getGCMPrefs(gcmp, "hasSerialNum_Verification");
                    String hasVoucher_Verification = objGlobalMethods.getGCMPrefs(gcmp, "hasVoucher_Verification");

                    cv.setHasOTP(hasOTP);
                    cv.setHasVerifiedOTP(hasVerifiedOTP);
                    cv.setCustIDVerificationNotRequired(CustIDVerificationNotRequired);
                    cv.setisLockerJob(isLockerJob);
                    cv.setCurrJobInfoStatusCodeIDX(CurrJobInfoStatusCodeIDX);
                    cv.setStatusCodeIDX(StatusCodeIDX);
                    cv.setFLAG(FLAG);
                    cv.setTypeOfPinVerification(TypeOfPinVerification);


                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 getChecklistVariables. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return cv;
        }
    }

    public static String[] getSpecificChecklist(String[] checklist, String ActionCode) {
        String[] list = null;
        ArrayList<String> l = new ArrayList<>();
        int index = 0;
        String FullCode = "";
        String[] codesArr = null;
        String ActionCode_A = "";
        try {
            if (checklist != null && checklist.length > 0) {
                for (int i = 0; i <= checklist.length - 1; i++) {
                    index = checklist[i].indexOf("|");
                    if (index >= 0) {
                        FullCode = checklist[i].substring(0, index);
                        if (FullCode != "") {
                            codesArr = FullCode.split("\\.");
                            ActionCode_A = codesArr[1];
                            if (ActionCode_A.equals(ActionCode)) {
                                l.add(checklist[i]);
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
            }

            list = new String[l.size()];
            list = l.toArray(list);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 getSpecificChecklist. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return list;
        }
    }

    public static boolean isMatchingDO(JobInfoLite ji, String dostr) {
        Boolean asd = false;
        long DOIX = Long.parseLong("0");
        String convertedDOIDXStr = "";
        //String dostrUpper = dostr.toUpperCase().replaceAll("\\s+", ""); //remove all spaces
        String dostrUpper = dostr.toUpperCase();
        try {
            if (!objGlobalMethods.IsEmptyString(dostrUpper) && ji != null && ji.DOList != null && ji.DOList.length > 0) {
                boolean isDOIX = dostrUpper.indexOf(objGlobalVariables.InvoiceIXKey) == 0;
                convertedDOIDXStr = isDOIX ? dostrUpper.substring(2, dostrUpper.length()) : "";

                for (int i = 0; i <= ji.DOList.length - 1; i++) {
                    try {
                        if (isDOIX) {
                            DOIX = ji.DOList[i].DOIDX;
                        }
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at JobsHandler2 isMatchingDO converting DOIX. Exception: " + e.getMessage().toString(), "", "", "", "");
                    }

                    asd = isDOIX && !objGlobalMethods.IsEmptyString(convertedDOIDXStr) ? String.valueOf(DOIX).equals(convertedDOIDXStr) : ji.DOList[i].Invoice.toUpperCase().equals(dostrUpper);
                    if (asd) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isMatchingDO. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean isMatchingJobsIDX(JobInfoLite ji, String jxstr) {
        Boolean asd = false;
        try {
            if (jxstr.toUpperCase().indexOf(objGlobalVariables.JobIXKey) == 0) {
                String convertedJobsIDXStr = jxstr.substring(2, jxstr.length());
                Long idx;
                try {
                    idx = Long.parseLong(convertedJobsIDXStr);
                } catch (Exception e) {
                    idx = Long.parseLong("0");
                }
                asd = (idx > 0 && ji.JobsIDX == idx) ? true : false;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isMatchingJobsIDX. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean isMatchingJobsJC(JobInfoLite ji, String jcstr) {
        Boolean asd = false;
        try {
            if (jcstr.toUpperCase().indexOf(objGlobalVariables.JobJCKey) == 0) {
                String convertedJobsIDXStr = jcstr.substring(2, jcstr.length());
                Long idx;
                try {
                    idx = Long.parseLong(convertedJobsIDXStr);
                } catch (Exception e) {
                    idx = Long.parseLong("0");
                }
                asd = (idx > 0 && ji.JobsIDX == idx) ? true : false;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isMatchingJobsJC. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean isMatchingCN(JobInfoLite ji, String jxstr) {
        Boolean asd = false;
        try {
            String cn = jxstr.toUpperCase();
            if (cn.indexOf(objGlobalVariables.CNKey) == 0) {
                asd = (ji.ConsignmentNote.toUpperCase().equals(cn.toUpperCase())) ? true : false;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isMatchingCN. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static Boolean isMatchingSM(JobInfoLite ji, String jxstr) {
        Boolean asd = false;
        try {
            String cn = jxstr.toUpperCase();
            if (cn.indexOf(objGlobalVariables.SMKey) == 0) {
                asd = (ji.ConsignmentNote.toUpperCase().equals(cn.toUpperCase())) ? true : false;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isMatchingSM. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean isMatchingArticleTag(JobInfoLite ji, String atstr) {
        Boolean asd = false;
        try {
            if (!objGlobalMethods.IsEmptyString(atstr) && ji != null && ji.BarcodeTags != null && ji.BarcodeTags.length > 0) {
                atstr = atstr.toUpperCase();
                for (int i = 0; i <= ji.BarcodeTags.length - 1; i++) {
                    asd = ji.BarcodeTags[i].toUpperCase().equals(atstr);
                    if (asd) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isMatchingArticleTag. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean isMatchingJobNo(JobInfoLite ji, String val) {
        boolean asd = false;
        try {
            if (!objGlobalMethods.IsEmptyString(val) && ji != null && !objGlobalMethods.IsEmptyString(ji.JobNo)) {
                asd = ji.JobNo.toUpperCase().equals(val.toUpperCase());
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isMatchingJobNo. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean isMatchingCNField(JobInfoLite ji, String jxstr) {
        Boolean asd = false;
        try {
            if (!objGlobalMethods.IsEmptyString(ji.ConsignmentNote) && ji.ConsignmentNote.length() >= 4 && !objGlobalMethods.IsEmptyString(jxstr) && jxstr.length() >= 4) {
                asd = (ji.ConsignmentNote.toUpperCase().equals(jxstr.toUpperCase())) ? true : false;
            } else if (!objGlobalMethods.IsEmptyString(ji.ConsignmentNote) && ji.ConsignmentNote.length() < 4) {
                objGlobalMethods.writeToFile("ConsignmentNote in jobinfo length less than 4.", "", "", "", "");
            } else if (objGlobalMethods.IsEmptyString(ji.ConsignmentNote)) {
                objGlobalMethods.writeToFile("ConsignmentNote in jobinfo is empty.", "", "", "", "");
            } else if (!objGlobalMethods.IsEmptyString(jxstr) && jxstr.length() < 4) {
                objGlobalMethods.writeToFile("Value scanned entered to check length less than 4.", "", "", "", "");
            } else if (objGlobalMethods.IsEmptyString(jxstr)) {
                objGlobalMethods.writeToFile("Value scanned entered to check is empty.", "", "", "", "");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isMatchingCNField. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean isCorrectJobSelected(JobInfoLite ji, String val, boolean DOonly, int mode) {
        //mode 0 == scanned, mode 1 == manual entry
        boolean asd = false;
        boolean isMatchingJobsIDXbool = false;
        boolean isMatchingJobsJCbool = false;
        boolean isMatchingCNbool = false;
        boolean isMatchingSMbool = false;
        boolean isMatchingDObool = false;
        boolean isMatchingArticleTagbool = false;
        boolean isMatchingJobNobool = false;
        boolean isMatchingCNFieldbool = false;
        boolean isMatchingLockerParcelID = false;
        boolean isLocker = false;
        boolean hasColdItem = false;

        try {
            isLocker = ji.isLocker;
            ////for locker, let it b e same as normal job
            /*if (isLocker) {
                isMatchingLockerParcelID = isMatchingLockerParcelID(ji, val);
            } else*/
            if (DOonly) {
                isMatchingDObool = isMatchingDO(ji, val);
            } else {
                isMatchingJobsIDXbool = isMatchingJobsIDX(ji, val);
                isMatchingJobsJCbool = isMatchingJobsJC(ji, val);
                isMatchingCNbool = isMatchingCN(ji, val);
                isMatchingSMbool = isMatchingSM(ji, val);
                isMatchingDObool = isMatchingDO(ji, val);
                isMatchingArticleTagbool = isMatchingArticleTag(ji, val);
                isMatchingJobNobool = isMatchingJobNo(ji, val);
                isMatchingCNFieldbool = isMatchingCNField(ji, val);
            }

            asd = isMatchingJobsIDXbool || isMatchingJobsJCbool || isMatchingCNbool || isMatchingSMbool || isMatchingDObool || isMatchingArticleTagbool || isMatchingJobNobool || isMatchingCNFieldbool || isMatchingLockerParcelID;
            if (mode == 0) {
                objGlobalMethods.writeToFile(!asd ? "isCorrectJobSelected is false. Scanned value: " + val : "isCorrectJobSelected is true. Scanned value: " + val, "", "", "", "");
            } else {
                objGlobalMethods.writeToFile(!asd ? "isCorrectJobSelected is false. Entered value: " + val : "isCorrectJobSelected is true. Entered value: " + val, "", "", "", "");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isCorrectJobSelected. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = true;
        } finally {
            return asd;
        }
    }

    public static String getDateTimeFromLong(long ms) {
        String v = "";
        Date msd = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mma");
        try {
            if (ms > 0) {
                msd = new Date(ms);
                v = formatter.format(msd);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 getDateTimeFromLong. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return v;
        }
    }

    public static String get_ddMMFromLong(long ms) {
        String v = "";
        Date msd = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM h:mma");
        try {
            if (ms > 0) {
                msd = new Date(ms * 1000);
                v = formatter.format(msd);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 get_ddMMFromLong. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return v;
        }
    }

    public static Date getDateFromLong(long ms) {
        Date msd = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM h:mma");
        try {
            if (ms > 0) {
                msd = new Date(ms);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 getDateFromLong. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return msd;
        }
    }

    public static void backendRun(SharedPreferences g, SharedPreferences u) {
        //R6: return before the connectivity check, so a blocked process does not touch the warehouse temperature queue
        if (RootGuard.blockOperation("JobsHandler2.backendRun", null)) {
            return;
        }
        try {
            //cd = new ConnectionDetector(myApp.getAppContext());
            cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()) {
                if (!backEnd2IsRunning) {
                    backEnd2IsRunning = true;
                    processUploadWhseTemp(g, u);
                } else {
                    //log down that backend if running
                    Log.i("Backend2", "Backend2 is running already.");
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 backendRun. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void processUploadWhseTemp(SharedPreferences g, SharedPreferences u) {
        try {
            UploadTempDataObj obj = getUploadWhseTempDataObj(u);
            if (obj != null) {
                updateUploadWhseTemperature(obj, u, g);
            } else {

            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler processUploadWhseTemp. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static UploadTempDataObj getUploadWhseTempDataObj(SharedPreferences updateJobInfoPref) {
        Gson gson = new Gson();
        UploadTempDataObj[] UploadTempDataObjALQue;
        UploadTempDataObj utd = null;

        try {
            mutex.acquire();
            String UploadWhseTempDataObjALPrefStr = updateJobInfoPref.getString("UploadWhseTempDataObjALPrefStr", "");
            if (!UploadWhseTempDataObjALPrefStr.isEmpty()) {
                UploadTempDataObjALQue = gson.fromJson(UploadWhseTempDataObjALPrefStr, UploadTempDataObj[].class);
                if (UploadTempDataObjALQue != null) {
                    List<UploadTempDataObj> ListUploadTempDataObjAL = new ArrayList<UploadTempDataObj>(Arrays.asList(UploadTempDataObjALQue));
                    if (ListUploadTempDataObjAL != null && ListUploadTempDataObjAL.size() > 0) {
                        utd = ListUploadTempDataObjAL.get(0);
                        ListUploadTempDataObjAL.remove(0);
                        UploadTempDataObjALQue = ListUploadTempDataObjAL.toArray(new UploadTempDataObj[ListUploadTempDataObjAL.size()]);
                        UploadWhseTempDataObjALPrefStr = gson.toJson(UploadTempDataObjALQue);
                        SharedPreferences.Editor editor = updateJobInfoPref.edit();
                        if (UploadWhseTempDataObjALPrefStr.equals("[]")) {
                            UploadWhseTempDataObjALPrefStr = "";
                        }
                        editor.putString("UploadWhseTempDataObjALPrefStr", UploadWhseTempDataObjALPrefStr);
                        editor.apply();
                    } else if (ListUploadTempDataObjAL == null || ListUploadTempDataObjAL.size() == 0) {
                        SharedPreferences.Editor editor = updateJobInfoPref.edit();
                        editor.putString("UploadWhseTempDataObjALPrefStr", "");
                        editor.apply();
                    }
                } else if (UploadTempDataObjALQue == null) {
                    SharedPreferences.Editor editor = updateJobInfoPref.edit();
                    editor.putString("UploadWhseTempDataObjALPrefStr", "");
                    editor.apply();
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 getUploadWhseTempDataObj. Exception: " + e.getMessage().toString(), "", "", "", "");
            utd = null;
        } finally {
            mutex.release();
            return utd;
        }
    }

    public static void addUploadWhseTempDataObjQue(SharedPreferences updateJobInfoPref, UploadTempDataObj utd) {
        try {
            mutex.acquire();
            String UploadWhseTempDataObjALPrefStr = updateJobInfoPref.getString("UploadWhseTempDataObjALPrefStr", "");
            Gson gson = new Gson();
            UploadTempDataObj[] queAL;
            if (!UploadWhseTempDataObjALPrefStr.isEmpty()) {
                UploadTempDataObj savedObj;
                queAL = gson.fromJson(UploadWhseTempDataObjALPrefStr, UploadTempDataObj[].class);
                if (queAL != null) {
                    List<UploadTempDataObj> ListUploadTempDataObjAL = new ArrayList<UploadTempDataObj>(Arrays.asList(queAL));
                    ListUploadTempDataObjAL.add(utd);

                    queAL = ListUploadTempDataObjAL.toArray(new UploadTempDataObj[ListUploadTempDataObjAL.size()]);
                    UploadWhseTempDataObjALPrefStr = gson.toJson(queAL);
                    SharedPreferences.Editor editor = updateJobInfoPref.edit();
                    editor.putString("UploadWhseTempDataObjALPrefStr", UploadWhseTempDataObjALPrefStr);
                    editor.apply();
                } else {
                    queAL = new UploadTempDataObj[]{utd};
                    UploadWhseTempDataObjALPrefStr = gson.toJson(queAL);
                    SharedPreferences.Editor editor = updateJobInfoPref.edit();
                    editor.putString("UploadWhseTempDataObjALPrefStr", UploadWhseTempDataObjALPrefStr);
                    editor.apply();
                }
            } else {
                queAL = new UploadTempDataObj[]{utd};
                UploadWhseTempDataObjALPrefStr = gson.toJson(queAL);
                SharedPreferences.Editor editor = updateJobInfoPref.edit();
                editor.putString("UploadWhseTempDataObjALPrefStr", UploadWhseTempDataObjALPrefStr);
                editor.apply();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 addUploadWhseTempDataObjQue. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            mutex.release();
        }
    }

    public static void updateUploadWhseTemperature(UploadTempDataObj utd, SharedPreferences AupdateJobInfoPref, SharedPreferences GcmPref) {
        Upload_TemperatureResult success = null;
        String[] UsernamePwd = new String[4];
        String username = "", useridx = "";
        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPref);
        username = UsernamePwd[0];
        useridx = UsernamePwd[2];
        int queCount = 0;
        try {
            String APIKey = myApp.getAppContext().getString(R.string.action_HOMESAPI);
            success = WebService.Upload_Temperature(APIKey, utd.TemperaturePayloads);

            if (success == null) {
                objGlobalMethods.writeToFile("Error invoking Upload_Temperature, adding back to que. Device Mac " + utd.TemperaturePayloads[0].MACAddress,
                        "", "", "", "");

                addUploadWhseTempDataObjQue(AupdateJobInfoPref, utd);
                backEnd2IsRunning = false;
                objGlobalMethods.writeToFile("Backend2 ended due Error invoking updateUploadWhseTemperature.", "", "", "", "");

            } else if (success != null && success.Status == 0) {
                //if result return true, do nothing
                objGlobalMethods.writeToFile("Upload_Temperature successful.", "", "", username, useridx);

                //processWriteGCLog(GcmPref, AupdateJobInfoPref);
                processUploadWhseTemp(GcmPref, AupdateJobInfoPref);

            } else if (success != null && success.Status != 0) {
                //if result return false, stop current activity and postpone, do not add back to que
                objGlobalMethods.writeToFile("Upload_Temperature return false. Message: " + success.Message,
                        "", "", "", "");

                sendErrorEmail("Upload_Temperature return false. Message: " + success.Message);

                backEnd2IsRunning = false;
                objGlobalMethods.writeToFile("Backend2 ended due Upload_Temperature return false.", "", "", username, useridx);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobsHandler2 updateUploadWhseTemperature. Exception: " + e.toString() + "\nStack:" + s, "", "", "", "");

            sendErrorEmail("Exception caught at updateUploadWhseTemperature, not add back to que. Driver: " + username + ", DriverIDX: " + useridx +
                    ", ARegID = " + "\n" + e.toString() + "\nStack: " + s);

            backEnd2IsRunning = false;
            objGlobalMethods.writeToFile("Backend2 ended due Exception caught at updateUploadWhseTemperature.", "", "", username, useridx);
        }
    }

    public static boolean IsCLCN_For_DoorOpenPenalty(objJobInfo ji) {
        boolean asd = false;
        boolean isM1ESHOP_CNIDX = false;
        boolean isM1ESHOP_No_CustID_CNIDX = false;
        boolean isSH_CNIDX = false;
        boolean isM1PDCS_CLIDX = false;
        boolean isAlps_CLIDX = false;
        boolean isXDEL_SG_CNIDX = false;
        boolean isHill_CLIDX = false;
        try {
            isM1ESHOP_CNIDX = ji.ContactIDX == 1466671;
            isM1ESHOP_No_CustID_CNIDX = ji.ContactIDX == 3698738;
            isSH_CNIDX = ji.ContactIDX == 4670360;
            isM1PDCS_CLIDX = ji.ClientIDX == 111000;
            isAlps_CLIDX = ji.ClientIDX == 28657;

            isXDEL_SG_CNIDX = ji.ContactIDX == 1151839;
            isHill_CLIDX = ji.ClientIDX == 4868;

            //asd = isM1ESHOP_CNIDX || isM1ESHOP_No_CustID_CNIDX || isSH_CNIDX || isM1PDCS_CLIDX || isAlps_CLIDX || isHill_CLIDX;
            asd = isM1ESHOP_CNIDX || isM1ESHOP_No_CustID_CNIDX || isSH_CNIDX || isM1PDCS_CLIDX || isAlps_CLIDX;

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 IsCLCN_For_DoorOpenPenalty. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean attemptTimeIsEarlierThanWindow(int cattype, Date fromDateTime, Date toDateTime, Date ExtFromDateTime, Date ExtTilDateTime) {
        boolean asd = false;
        boolean fromIsToday = false;
        boolean extfromIsToday = false;
        boolean isEarlierThanFromDT = false;
        boolean FromDTisLaterThanToday = false;
        boolean extFromDTisLaterThanToday = false;
        boolean isEarlierThanToDT = false;
        boolean isLaterThanToDT = false;
        boolean isEarlierThanExtFromDT = false;
        Date now = new Date();
        Date ZeroTimeDateNow = objGlobalMethods.getZeroTimeDate(now);
        Date ZeroSecondsNow = objGlobalMethods.getZeroSecondsTimeDate(now);
        //0 comes when two date are same,
        //1 comes when date1 is higher then date2
        //-1 comes when date1 is lower then date2


        try {
            if (cattype == 2) {
                fromDateTime = fromDateTime == null ? null : objGlobalMethods.getZeroSecondsTimeDate(fromDateTime);
                toDateTime = toDateTime == null ? null : objGlobalMethods.getZeroSecondsTimeDate(toDateTime);
                ExtFromDateTime = ExtFromDateTime == null ? null : objGlobalMethods.getZeroSecondsTimeDate(ExtFromDateTime);
                ExtTilDateTime = ExtTilDateTime == null ? null : objGlobalMethods.getZeroSecondsTimeDate(ExtTilDateTime);

                if (fromDateTime != null) {
                    Calendar fromDateTimeC = Calendar.getInstance();
                    fromDateTimeC.setTime(fromDateTime);
                    //fromDateTimeC.add(Calendar.MINUTE, -5);
                    fromDateTime = fromDateTimeC.getTime();
                    fromIsToday = objGlobalMethods.getZeroTimeDate(fromDateTime).compareTo(ZeroTimeDateNow) == 0;
                    FromDTisLaterThanToday = objGlobalMethods.getZeroTimeDate(fromDateTime).compareTo(ZeroTimeDateNow) == 1;
                    isEarlierThanFromDT = fromDateTime.compareTo(ZeroSecondsNow) == 1;
                }

                if (toDateTime != null) {
                    Calendar toDateTimeC = Calendar.getInstance();
                    toDateTimeC.setTime(toDateTime);
                    toDateTimeC.add(Calendar.MINUTE, 15);
                    toDateTime = toDateTimeC.getTime();
                    isEarlierThanToDT = toDateTime.compareTo(ZeroSecondsNow) == 1;
                    isLaterThanToDT = toDateTime.compareTo(ZeroSecondsNow) == -1;
                }

                if (ExtFromDateTime != null) {
                    Calendar extFromDateTimeC = Calendar.getInstance();
                    extFromDateTimeC.setTime(ExtFromDateTime);
                    //extFromDateTimeC.add(Calendar.MINUTE, 5);
                    ExtFromDateTime = extFromDateTimeC.getTime();
                    extfromIsToday = objGlobalMethods.getZeroTimeDate(ExtFromDateTime).compareTo(ZeroTimeDateNow) == 0;
                    extFromDTisLaterThanToday = objGlobalMethods.getZeroTimeDate(ExtFromDateTime).compareTo(ZeroTimeDateNow) == 1;
                    isEarlierThanExtFromDT = ExtFromDateTime.compareTo(ZeroSecondsNow) == 1;
                }

                if (ExtTilDateTime != null) {
                    Calendar extDateTimeC = Calendar.getInstance();
                    extDateTimeC.setTime(ExtTilDateTime);
                    extDateTimeC.add(Calendar.MINUTE, 15);
                    ExtTilDateTime = extDateTimeC.getTime();
                }

                if ((fromDateTime == null || toDateTime == null) || (fromDateTime == null && toDateTime == null)) {
                    asd = false;
                } else if (fromDateTime != null && toDateTime != null) {
                    if (FromDTisLaterThanToday || (fromIsToday && isEarlierThanFromDT)) {
                        //del fromDate is later than today or fromDate is today and now is earlier than fromDatetime
                        asd = true;
                    } else if (fromIsToday && isLaterThanToDT && ExtFromDateTime != null && (extFromDTisLaterThanToday || (extfromIsToday && isEarlierThanExtFromDT))) {
                        asd = true;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 everNDorOOTbefore. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean everNDorOOTbefore(int flag2) {
        boolean asd = false;
        try {
            asd = (flag2 & 268435456) == 268435456;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 attemptTimeIsEarlierThanWindow. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean isHSBCTradeSvcCLIDX(long clidx) {
        boolean asd = false;
        try {
            asd = clidx == 28709;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isHSBCTradeSvcCLIDX. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean isSCBTradeSvcCLIDX(long clidx) {
        boolean asd = false;
        try {
            asd = clidx == 28505;
            //asd = clidx == 4868;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isSCBTradeSvcCLIDX. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean isUOB_BGTeam_CNIDXs(objJobInfo ji) {
        boolean asd = false;
        try {
            if (ji != null && ji.ContactIDX > 0) {
                //contact FULFILMENT (NO ID VERIFICATION), TESTEER, XDEL SG
                asd = (ji.ContactIDX == 5012266) || (ji.ContactIDX == 5012269 || (ji.ContactIDX == 5012771) || (ji.ContactIDX == 3650876));
                //asd = (ji.ContactIDX == 3584293);
                ////asd = ji.ContactIDX == 2147238; //hill's kean's contactidx
            }
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean isPCDreamFulfilmentCNDIX(long cnidx) {
        boolean asd = false;
        try {
            asd = cnidx == 5327349;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isPCDreamFulfilmentCNDIX. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static final Semaphore mtx = new Semaphore(1);

    public static String rtnMaskedContactNumberForDisplay(String tel) {
        String mt = "";
        String t = "";
        String finalt = "";
        boolean asd = false;
        try {
            if (JobInfoActivity.maskContactNum) {
                tel = tel.replaceAll("\\s+", ""); // replace all spaces to empty string
                String[] array = tel.split("/");
                if (array != null && array.length > 0) {
                    for (int i = 0; i <= array.length - 1; i++) {
                        t = array[i];
                        asd = objGlobalMethods.validSGTelNum(t, true);
                        if (asd && t.length() > 4) {
                            mt += (!objGlobalMethods.IsEmptyString(mt)) ? "/" + rtnMaskedContactNumber(t) : rtnMaskedContactNumber(t);
                        } else if (asd && t.length() > 0 && t.length() <= 4) {
                            mt += (!objGlobalMethods.IsEmptyString(mt)) ? "/" + t : t;
                        } else {
                            mt += (!objGlobalMethods.IsEmptyString(mt)) ? "/" + t : t;
                        }
                    }
                }
            } else {
                mt = tel;
            }
        } catch (Exception e) {
            mt = tel;
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at JobsHandler2 rtnMaskedContactNumberForDisplay. Exception: " + s, "", "", "", "");
        } finally {
            return mt;
        }
    }

    public static String rtnMaskedContactNumber(String vt) {
        String st = "";
        String ft = "";
        try {
            if (!objGlobalMethods.IsEmptyString(vt) && vt.length() > 5) {
                st = vt.substring(0, 5);
                ft = vt.substring(0, vt.length());
                vt = vt.replace(st, "XXXXX");
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at JobsHandler2 rtnMaskedContactNumber. Exception: " + s, "", "", "", "");
        } finally {
            return vt;
        }
    }

    public static boolean canShowFullContactNumber(SharedPreferences p) {
        boolean asd = false;
        try {
            if (p == null)
                p = objGlobalMethods.getGcmPreferences();

            //String noCallMade = objGlobalMethods.getGCMPrefs(p, "noCallMade");
            String totalnumofcallstr = objGlobalMethods.getGCMPrefs(p, "totalnumofcall");
            int totalnumofcall = totalnumofcallstr.equals("") ? 0 : Integer.parseInt(totalnumofcallstr);
            //String exemptFromCallingForLBDoorGuard = objGlobalMethods.getGCMPrefs(p, "exemptFromCallingForLBDoorGuard");

            asd = totalnumofcall >= 2;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobHandler2 canShowFullContactNumber. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static void addBaseDeviceScanObjToBeDetectedForReport(BaseDeviceScanObj bs) {
        Gson gson = new Gson();
        String BaseDeviceScanObjToBeDetectedJsonStr = "";
        SharedPreferences b = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
        ArrayList<BaseDeviceScanObj> list = null;
        boolean found = false;
        try {
            mtx.acquire();
            BaseDeviceScanObjToBeDetectedJsonStr = b.getString("BaseDeviceScanObjToBeDetectedJsonStr", "");
            if (!objGlobalMethods.IsEmptyString(BaseDeviceScanObjToBeDetectedJsonStr) && !objGlobalMethods.IsEqualString(BaseDeviceScanObjToBeDetectedJsonStr, "[]")) {
                list = gson.fromJson(BaseDeviceScanObjToBeDetectedJsonStr, new TypeToken<List<BaseDeviceScanObj>>() {
                }.getType());

                if (list != null && list.size() > 0) {
                    for (int i = 0; i <= list.size() - 1; i++) {
                        if (list.get(i).MPDIDX == bs.MPDIDX) {
                            found = true;
                            break;
                        }
                    }
                }
            }

            if (list == null)
                list = new ArrayList<BaseDeviceScanObj>();

            if (!found)
                list.add(bs);

            BaseDeviceScanObjToBeDetectedJsonStr = list != null && list.size() > 0 ? gson.toJson(list) : "";
            objGlobalMethods.setGCMPrefs(b, "BaseDeviceScanObjToBeDetectedJsonStr", BaseDeviceScanObjToBeDetectedJsonStr);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobHandler2 addBaseDeviceScanObjToBeDetectedForReport. Exception: " + s, "", "", "", "");
        } finally {
            mtx.release();
        }
    }

    public static void removeBaseDeviceScanObjToBeDetectedForReport(long MPDIDX) {
        Gson gson = new Gson();
        String BaseDeviceScanObjToBeDetectedJsonStr = "";
        SharedPreferences b = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
        ArrayList<BaseDeviceScanObj> list = null;
        try {
            mtx.acquire();
            BaseDeviceScanObjToBeDetectedJsonStr = b.getString("BaseDeviceScanObjToBeDetectedJsonStr", "");
            if (!objGlobalMethods.IsEmptyString(BaseDeviceScanObjToBeDetectedJsonStr) && !objGlobalMethods.IsEqualString(BaseDeviceScanObjToBeDetectedJsonStr, "[]")) {
                list = gson.fromJson(BaseDeviceScanObjToBeDetectedJsonStr, new TypeToken<List<BaseDeviceScanObj>>() {
                }.getType());

                if (list != null && list.size() > 0) {
                    for (int i = 0; i <= list.size() - 1; i++) {
                        if (list.get(i).MPDIDX == MPDIDX) {
                            list.remove(i);
                            break;
                        }
                    }
                }
            }

            BaseDeviceScanObjToBeDetectedJsonStr = list != null && list.size() > 0 ? gson.toJson(list) : "";
            objGlobalMethods.setGCMPrefs(b, "BaseDeviceScanObjToBeDetectedJsonStr", BaseDeviceScanObjToBeDetectedJsonStr);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobHandler2 removeBaseDeviceScanObjToBeDetectedForReport. Exception: " + s, "", "", "", "");
        } finally {
            mtx.release();
        }
    }

    public static boolean StdRunnerCanVRP() {
        boolean asd = false;
        boolean sr = false;
        boolean satsun = false;
        try {
            satsun = isSatSun();

            if (satsun) {
                asd = DAO_Jobs.getStdRunJobsCount() == 0;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 StdRunnerCanVRP. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean reqPPSafeKeep(SharedPreferences gcmp) {
        boolean asd = false;
        try {
            if (gcmp == null)
                gcmp = objGlobalMethods.getGcmPreferences();
            String dfs = objGlobalMethods.getGCMPrefs(gcmp, "DriverFlag");
            int df = !objGlobalMethods.IsEmptyString(dfs) ? Integer.parseInt(dfs) : 0;
            asd = ((df & 524288) == 524288);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 reqPPSafeKeep. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean reqAckRtnPPSafeKeep(SharedPreferences gcmp) {
        boolean asd = false;
        try {
            if (gcmp == null)
                gcmp = objGlobalMethods.getGcmPreferences();
            String dfs = objGlobalMethods.getGCMPrefs(gcmp, "DriverFlag");
            int df = !objGlobalMethods.IsEmptyString(dfs) ? Integer.parseInt(dfs) : 0;
            asd = ((df & 262144) == 262144);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 reqAckRtnPPSafeKeep. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean isLuke_Reduced(SharedPreferences gcmp) {
        boolean asd = false;
        try {
            if (gcmp == null)
                gcmp = objGlobalMethods.getGcmPreferences();
            String dfs = objGlobalMethods.getGCMPrefs(gcmp, "DriverFlag");
            int df = !objGlobalMethods.IsEmptyString(dfs) ? Integer.parseInt(dfs) : 0;
            DriverFlag driverflag = new DriverFlag();
            asd = ((df & driverflag.Flag_Luke_Reduced) == driverflag.Flag_Luke_Reduced);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isLuke_Reduced. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean isWalker(SharedPreferences gcmp) {
        boolean asd = false;
        try {
            if (gcmp == null)
                gcmp = objGlobalMethods.getGcmPreferences();
            String dfs = objGlobalMethods.getGCMPrefs(gcmp, "DriverFlag");
            int df = !objGlobalMethods.IsEmptyString(dfs) ? Integer.parseInt(dfs) : 0;
            DriverFlag driverflag = new DriverFlag();
            asd = ((df & driverflag.Flag_VT_Walker) == driverflag.Flag_VT_Walker);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isWalker. Exception: " + s, "", "", "", "");

        } finally {
            return asd;
        }
    }

    public static boolean isStdRunner(SharedPreferences gcmp) {
        boolean asd = false;
        try {
            if (gcmp == null)
                gcmp = objGlobalMethods.getGcmPreferences();
            String dfs = objGlobalMethods.getGCMPrefs(gcmp, "DriverFlag");
            int df = !objGlobalMethods.IsEmptyString(dfs) ? Integer.parseInt(dfs) : 0;
            DriverFlag driverflag = new DriverFlag();
            asd = ((df & driverflag.Flag_DT_StdRun) == driverflag.Flag_DT_StdRun);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isStdRunner. Exception: " + s, "", "", "", "");

        } finally {
            return asd;
        }
    }

    public static boolean isSatSun() {
        boolean ss = false;
        try {
            Date tdy = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(tdy);
            int dow = c.get(Calendar.DAY_OF_WEEK);

            switch (dow) {
                case Calendar.SUNDAY:
                    ss = true;
                    break;
                case Calendar.SATURDAY:
                    ss = true;
                    break;
                case Calendar.FRIDAY:
                    ss = false;
                    break;
                case Calendar.THURSDAY:
                    ss = false;
                    break;
                case Calendar.WEDNESDAY:
                    ss = false;
                    break;
                case Calendar.TUESDAY:
                    ss = false;
                    break;
                case Calendar.MONDAY:
                    ss = false;
                    break;

            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        } finally {
            return ss;
        }
    }

    public static boolean isAOHtimeTest() {
        boolean asd = false;
        Date currDT = new Date();
        Date targetDT = new Date();
        try {
            Calendar currC = Calendar.getInstance();
            currC.setTime(currDT);

            Calendar targetC = new GregorianCalendar(currC.get(Calendar.YEAR), currC.get(Calendar.MONTH), currC.get(Calendar.DAY_OF_MONTH), 18, 45, 0);
            targetC.set(Calendar.DAY_OF_MONTH, 4);
            targetDT = targetC.getTime();

            currC.set(Calendar.DAY_OF_MONTH, 4);
            currC.set(Calendar.HOUR, 1);
            currC.set(Calendar.MINUTE, 22);
            currDT = currC.getTime();

            //0 comes when two date are same,
            //1 comes when date1 is higher then date2
            //-1 comes when date1 is lower then date2
            asd = currDT.compareTo(targetDT) > 0;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isAOHtime. Exception: " + e.getMessage(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean isAOHtime() {
        boolean asd = false;
        Date currDT = new Date();
        Date targetDT = new Date();
        try {
            Calendar currC = Calendar.getInstance();
            currC.setTime(currDT);

            Calendar targetC = new GregorianCalendar(currC.get(Calendar.YEAR), currC.get(Calendar.MONTH), currC.get(Calendar.DAY_OF_MONTH), 18, 45, 0);
            //Calendar targetC = new GregorianCalendar(currC.get(Calendar.YEAR), currC.get(Calendar.MONTH), currC.get(Calendar.DAY_OF_MONTH), 14, 30, 0);
            targetDT = targetC.getTime();

            //0 comes when two date are same,
            //1 comes when date1 is higher then date2
            //-1 comes when date1 is lower then date2
            asd = currDT.compareTo(targetDT) > 0;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isAOHtime. Exception: " + e.getMessage(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public static boolean isBefore1005am() {
        boolean asd = false;
        Date currDT = new Date();
        Date targetDT = new Date();
        try {
            Calendar currC = Calendar.getInstance();
            currC.setTime(currDT);

            Calendar targetC = new GregorianCalendar(currC.get(Calendar.YEAR), currC.get(Calendar.MONTH), currC.get(Calendar.DAY_OF_MONTH), 10, 5, 0);
            //Calendar targetC = new GregorianCalendar(currC.get(Calendar.YEAR), currC.get(Calendar.MONTH), currC.get(Calendar.DAY_OF_MONTH), 15, 59, 0);
            targetDT = targetC.getTime();

            //0 comes when two date are same,
            //1 comes when date1 is higher then date2
            //-1 comes when date1 is lower then date2
            asd = currDT.compareTo(targetDT) < 0;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isBefore1005am. Exception: " + e.getMessage(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }


    public static boolean VRP_StartEarly(SharedPreferences gcmp) {
        if (gcmp == null)
            gcmp = objGlobalMethods.getGcmPreferences();
        String dfs = objGlobalMethods.getGCMPrefs(gcmp, "DriverFlag");
        int df = !objGlobalMethods.IsEmptyString(dfs) ? Integer.parseInt(dfs) : 0;
        DriverFlag driverflag = new DriverFlag();
        return ((df & driverflag.Flag_VRP_StartEarly) == driverflag.Flag_VRP_StartEarly);
    }

    public static String[] returnNoPhotoTakingCheckList(String[] ori) {
        String[] nl = ori;
        List<String> checkList = null;
        String codes = "";
        int index = 0;
        String[] codesArr = null;
        String ControlCode = "";
        String displaytextNoptions = "";
        String DisplayText = "";
        String DisplayTextFinal = "";
        String item = "";

        try {
            if (ori != null && ori.length > 0) {
                checkList = new ArrayList<String>(Arrays.asList(ori));
                if (checkList != null && checkList.size() > 0) {
                    for (int i = checkList.size() - 1; i >= 0; i--) {
                        DisplayTextFinal = "";
                        item = checkList.get(i);
                        index = item.indexOf("|");
                        codes = item.substring(0, index);

                        if (codes != "") {
                            codesArr = codes.split("\\.");
                            ControlCode = codesArr[2];
                            displaytextNoptions = item.substring(index + 1, ori[i].length());

                            if (ControlCode.equals("PH") && displaytextNoptions != "") {
                                index = displaytextNoptions.indexOf("|");
                                if (index >= 0) {
                                    DisplayText = displaytextNoptions.substring(0, index);

                                    int newlineIndex = DisplayText.indexOf("[ZH]");
                                    if (newlineIndex >= 0) {
                                        DisplayTextFinal = DisplayText.substring(0, newlineIndex);
                                    } else {
                                        DisplayTextFinal = DisplayText;
                                    }
                                } else {
                                    DisplayText = displaytextNoptions;

                                    int newlineIndex = DisplayText.indexOf("[ZH]");
                                    if (newlineIndex >= 0) {
                                        DisplayTextFinal = DisplayText.substring(0, newlineIndex);
                                    } else {
                                        DisplayTextFinal = DisplayText;
                                    }
                                }

                                if (!DisplayTextFinal.toUpperCase().contains("LOA")) {
                                    checkList.remove(i);
                                    continue;
                                }
                            } else if (ControlCode.equals("PH")) {
                                //simply remove
                                checkList.remove(i);
                                continue;
                            }

                            if (ControlCode.equals("PR")) {
                                checkList.remove(i);
                                continue;
                            }

                        }
                    }
                }
            }

            if (checkList != null && checkList.size() > 0) {
                nl = checkList.toArray(new String[checkList.size()]);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exeption at JobsHandler2 returnNoPhotoTakingCheckList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            nl = ori;
        } finally {
            return nl;
        }
    }

    public static void UpdateUnreadMsgAftJobUpdate() {
        ArrayList<objUnreadPUOKPFAILjobsMsgInfo> al = null;
        objUnreadPUOKPFAILjobsMsgInfo o = null;
        ChatMsgSenders cms = null;
        int notiID;
        SharedPreferences gp;
        SharedPreferences up;
        String AckMessagesToBeSentALPrefStr = "";
        Long[] AckMessagesToBeSentAL = null;
        Long[] AckMessages = null;
        ArrayList<ChatMessage> chatMessages = null;
        List<Long> ListAckMessages = null;
        try {
            al = DAO_Jobs.getUnreadPUOKPFAILjobsMsgInfo();
            if (al != null && al.size() > 0) {
                for (int i = 0; i <= al.size() - 1; i++) {
                    o = al.get(i);
                    cms = DAO_Jobs.getChatMsgSendersWithJobsIDXnChatMsgSendersIDX(o.jobsidx, o.useridx);
                    if (cms != null) {
                        notiID = cms.notiID;
                        String ns = Context.NOTIFICATION_SERVICE;
                        //NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
                        NotificationManager nMgr = (NotificationManager) myApp.getAppContext().getSystemService(ns);
                        nMgr.cancel(notiID);
                        gp = objGlobalMethods.getGcmPreferences();
                        up = objGlobalMethods.getUpdateJobInfoPreferences();
                        AckMessagesToBeSentALPrefStr = gp.getString("AckMessagesToBeSentALPrefStr", "");
                        cms.Read = true;
                        DAO_Jobs.updateChatMsgSenders(cms);
                        chatMessages = DAO_Jobs.getchatMessages2(!objGlobalMethods.IsEmptyString(cms.JobsIDX) ? Long.parseLong(cms.JobsIDX) : 0, !objGlobalMethods.IsEmptyString(cms.UserIDX) ? Long.parseLong(cms.UserIDX) : 0);
                        ListAckMessages = new ArrayList<>();
                        if (chatMessages != null) {
                            AckMessagesToBeSentAL = null;
                            AckMessages = null;

                            if (!AckMessagesToBeSentALPrefStr.isEmpty()) {
                                Gson gson = new Gson();
                                AckMessagesToBeSentAL = gson.fromJson(AckMessagesToBeSentALPrefStr, Long[].class);
                                if (AckMessagesToBeSentAL != null && AckMessagesToBeSentAL.length > 0) {
                                    for (int z = 0; z <= AckMessagesToBeSentAL.length - 1; z++) {
                                        for (int x = 0; x <= chatMessages.size() - 1; x++) {
                                            if (AckMessagesToBeSentAL[z].equals(chatMessages.get(x).getAckIDX())) {
                                                ListAckMessages.add(AckMessagesToBeSentAL[z]);
                                            }
                                        }
                                    }
                                }
                            }


                            for (int z = 0; z <= chatMessages.size() - 1; z++) {
                                if (chatMessages.get(z).Direction != null && (chatMessages.get(z).Direction != 1 && chatMessages.get(z).Direction != 2)) {
                                    chatMessages.get(z).setDirection(chatMessages.get(z).Direction == 100 ? 1 : 2);
                                    DAO_Jobs.updateChatMessageDirection(chatMessages.get(z).getID(), chatMessages.get(z).Direction);
                                    ListAckMessages.add(chatMessages.get(z).getAckIDX());
                                }
                            }

                            if (ListAckMessages != null && ListAckMessages.size() > 0) {
                                AckMessages = ListAckMessages.toArray(new Long[ListAckMessages.size()]);
                                JobsHandler.addAckMessagesQuePrefs(up, AckMessages);
                                JobsHandler.addAckMessagesToBeSent(gp, AckMessages, false);
                            }
                        }
                    }
                }
                myApp.getAppContext().sendBroadcast(new Intent(myApp.getAppContext(), BackendRun_BroadcastReceiver.class));
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at JobsHandler2 UpdateUnreadMsgAftJobUpdate. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void updateUnreadMsgAftJobUpdate(ArrayList<Long> JobsIDXs) {
        ArrayList<ChatMessage> chatMessages = null;
        ArrayList<ChatMsgSenders> al = null;
        ChatMsgSenders cms = null;
        String AckMessagesToBeSentALPrefStr = "";
        SharedPreferences gp;
        SharedPreferences up;
        Long[] AckMessagesToBeSentAL = null;
        Long[] AckMessages = null;
        List<Long> ListAckMessages = null;
        int notiID;
        try {
            chatMessages = new ArrayList<>();
            if (JobsIDXs != null && JobsIDXs.size() > 0) {
                for (int i = 0; i <= JobsIDXs.size() - 1; i++) {
                    al = DAO_Jobs.getChatMsgSendersWithJobsIDXnChatMsgSendersIDXList(JobsIDXs.get(i));
                    if (al != null && al.size() > 0) {
                        for (int c = 0; c <= al.size() - 1; c++) {
                            cms = al.get(c);
                            if (cms != null) {
                                notiID = cms.notiID;
                                String ns = Context.NOTIFICATION_SERVICE;
                                //NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
                                NotificationManager nMgr = (NotificationManager) myApp.getAppContext().getSystemService(ns);
                                nMgr.cancel(notiID);

                                gp = objGlobalMethods.getGcmPreferences();
                                up = objGlobalMethods.getUpdateJobInfoPreferences();
                                AckMessagesToBeSentALPrefStr = gp.getString("AckMessagesToBeSentALPrefStr", "");
                                cms.Read = true;
                                DAO_Jobs.updateChatMsgSenders(cms);
                                chatMessages = DAO_Jobs.getchatMessages2(!objGlobalMethods.IsEmptyString(cms.JobsIDX) ? Long.parseLong(cms.JobsIDX) : 0, !objGlobalMethods.IsEmptyString(cms.UserIDX) ? Long.parseLong(cms.UserIDX) : 0);
                                ListAckMessages = new ArrayList<>();
                                if (chatMessages != null) {
                                    AckMessagesToBeSentAL = null;
                                    AckMessages = null;

                                    if (!AckMessagesToBeSentALPrefStr.isEmpty()) {
                                        Gson gson = new Gson();
                                        AckMessagesToBeSentAL = gson.fromJson(AckMessagesToBeSentALPrefStr, Long[].class);
                                        if (AckMessagesToBeSentAL != null && AckMessagesToBeSentAL.length > 0) {
                                            for (int z = 0; z <= AckMessagesToBeSentAL.length - 1; z++) {
                                                for (int x = 0; x <= chatMessages.size() - 1; x++) {
                                                    if (AckMessagesToBeSentAL[z].equals(chatMessages.get(x).getAckIDX())) {
                                                        ListAckMessages.add(AckMessagesToBeSentAL[z]);
                                                    }
                                                }
                                            }
                                        }
                                    }


                                    for (int z = 0; z <= chatMessages.size() - 1; z++) {
                                        if (chatMessages.get(z).Direction != null && (chatMessages.get(z).Direction != 1 && chatMessages.get(z).Direction != 2)) {
                                            chatMessages.get(z).setDirection(chatMessages.get(z).Direction == 100 ? 1 : 2);
                                            DAO_Jobs.updateChatMessageDirection(chatMessages.get(z).getID(), chatMessages.get(z).Direction);
                                            ListAckMessages.add(chatMessages.get(z).getAckIDX());
                                        }
                                    }

                                    if (ListAckMessages != null && ListAckMessages.size() > 0) {
                                        AckMessages = ListAckMessages.toArray(new Long[ListAckMessages.size()]);
                                        JobsHandler.addAckMessagesQuePrefs(up, AckMessages);
                                        JobsHandler.addAckMessagesToBeSent(gp, AckMessages, false);
                                    }


                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at JobsHandler2 updateUnreadMsgAftJobUpdate. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void regroupParkedJobs(String lastpostal) {
        String oriPipDipPostals = "";
        try {
            oriPipDipPostals = DAO_Jobs.getAllPipDipPostals();
            if (!objGlobalMethods.IsEmptyString(oriPipDipPostals)) {
                if (!objGlobalMethods.IsEmptyString(lastpostal)) {
                    oriPipDipPostals += "," + lastpostal;
                }
                DAO_Jobs.updateParkedJobsToDIPCat(oriPipDipPostals);
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at JobsHandler2 regroupParkedJobs. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static int getCallRequeueFlag(SharedPreferences g) {
        int f = 0;
        String str = "";
        try {
            str = objGlobalMethods.getGCMPrefs(g, "CallRequeueFlagstr");
            f = !objGlobalMethods.IsEmptyString(str) ? Integer.parseInt(str) : 0;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 getCalLRequeueFlag. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return f;
        }
    }

    public static void removeCallRequeue(SharedPreferences g, int type) {
        ////type
        //1 == L2_LA_GetUser 2== 4== 8== 16== 32== 64== 128== 256== 512== 1024==
        int CallRequeueFlag = 0;
        String CallRequeueFlagstr = "";
        try {
            if (g == null)
                g = objGlobalMethods.getGcmPreferences();

            CallRequeueFlagstr = objGlobalMethods.getGCMPrefs(g, "CallRequeueFlagstr");
            if (!objGlobalMethods.IsEmptyString(CallRequeueFlagstr)) {
                try {
                    CallRequeueFlag = Integer.parseInt(CallRequeueFlagstr);
                } catch (Exception e) {
                    String ss = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception Integer.parseInt(CallRequeueFlagstr). Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
                    CallRequeueFlag = 0;
                }
            }

            if (type == 1 && (CallRequeueFlag & 1) == 1) {
                //CallRequeueFlag = CallRequeueFlag | 1;
                CallRequeueFlag += -1;
            }

            objGlobalMethods.setGCMPrefs(g, "CallRequeueFlagstr", String.valueOf(CallRequeueFlag));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 addCallRequeue. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void addCallRequeue(SharedPreferences g, int type) {
        ////type
        //1 == L2_LA_GetUser 2== 4== 8== 16== 32== 64== 128== 256== 512== 1024==
        int CallRequeueFlag = 0;
        String CallRequeueFlagstr = "";
        try {
            if (g == null)
                g = objGlobalMethods.getGcmPreferences();

            CallRequeueFlagstr = objGlobalMethods.getGCMPrefs(g, "CallRequeueFlagstr");
            if (!objGlobalMethods.IsEmptyString(CallRequeueFlagstr)) {
                try {
                    CallRequeueFlag = Integer.parseInt(CallRequeueFlagstr);
                } catch (Exception e) {
                    String ss = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception Integer.parseInt(CallRequeueFlagstr). Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
                    CallRequeueFlag = 0;
                }
            }

            if (type == 1 && (CallRequeueFlag & 1) != 1) {
                //CallRequeueFlag = CallRequeueFlag | 1;
                CallRequeueFlag += 1;
            }

            objGlobalMethods.setGCMPrefs(g, "CallRequeueFlagstr", String.valueOf(CallRequeueFlag));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 addCallRequeue. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static boolean isToday(Date d) {
        boolean asd = false;
        Date now = new Date();
        try {
            Date ZeroTimeDateNow = objGlobalMethods.getZeroTimeDate(now);
            asd = objGlobalMethods.getZeroTimeDate(d).compareTo(ZeroTimeDateNow) == 0;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isToday. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static void updateCabinetDueNewIDXsAddedToInitialSetOfJobsIDXsArr(SharedPreferences gcmp, String aregid, String saveAmended4QuesJsonStr, String InitialSetOfJobsIDXsJsonStr) {
        try {
            String useridx;
            String[] UsernamePwd = new String[4];
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
            useridx = UsernamePwd[2];

            String[] qal = new String[]{
                    "true", saveAmended4QuesJsonStr, InitialSetOfJobsIDXsJsonStr
            };
            Gson gson = new Gson();
            String alJsonStr = qal != null ? gson.toJson(qal) : "";
            objGlobalMethods.setGCMPrefs(gcmp, "QCabinetDataJsonStr", alJsonStr);
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            objCabinet cabinet = new objCabinet(0, Long.parseLong(!useridx.isEmpty() && !useridx.equals("") && useridx != "" ? useridx : "0"), "2", alJsonStr, timestampstr);
            JobsHandler.addUpdateCabinetQueALPrefs(objGlobalMethods.getUpdateJobInfoPreferences(), aregid, cabinet);
            objGlobalMethods.writeToFile("addUpdateCabinetQueALPrefs performed at Jobshandler2 updateCabinetDueNewIDXsAddedToInitialSetOfJobsIDXsArr", "", "", "", "");
            myApp.getAppContext().sendBroadcast(new Intent(myApp.getAppContext(), BackendRun_BroadcastReceiver.class));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 updateCabinetDueNewIDXsAddedToInitialSetOfJobsIDXsArr. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void updateCabinetForPrevDayDataToNew(objCabinet Cabinet, String aregid, String useridx) {
        String CabinetLastUpdated = "";
        Date lastupdated = null;
        boolean lastupdatedisprevday = false;
        try {
            if (Cabinet != null) {
                CabinetLastUpdated = Cabinet.Last_Updated;
                if (!objGlobalMethods.IsEmptyString(CabinetLastUpdated)) {
                    lastupdated = objGlobalMethods.JsonDateToDate(CabinetLastUpdated);
                }
                if (lastupdated != null) {
                    lastupdatedisprevday = JobsHandler.moreThanXdaysZerofied(lastupdated.getTime(), 1, true, false);
                }
                if (lastupdatedisprevday) {
                    String[] qal = new String[]{
                            "false", "", ""
                    };
                    Gson gson = new Gson();
                    String alJsonStr = qal != null ? gson.toJson(qal) : "";
                    Date dt = new Date();
                    Long l = dt.getTime();
                    String timestampstr = "/Date(" + l + ")/";
                    objCabinet cabinet = new objCabinet(0, Long.parseLong(!useridx.isEmpty() && !useridx.equals("") && useridx != "" ? useridx : "0"), "2", alJsonStr, timestampstr);
                    boolean savecabinet2 = WebService.SaveCabinet(aregid, cabinet);
                    objGlobalMethods.writeToFile("updateCabinet at DrawerMain2Activity successful: " + (savecabinet2 ? "true" : "false") + " for past data.", "", "", "", "");
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 updateCabinetForPrevDayDataToNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static boolean showExtOpt(objJobInfo ji) {
        boolean asd = true;
        Gson gson = new Gson();
        SharedPreferences p = objGlobalMethods.getGcmPreferences();
        boolean dnf = false;
        boolean isNoOpsExt = false;
        boolean hasExt = false;
        try {
            if (ji == null) {
                ji = gson.fromJson(p.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            }
            if (ji != null) {
                dnf = isDNF(ji.FLAG);
                isNoOpsExt = isNoOpsExt(ji.FLAG);
                hasExt = !objGlobalMethods.IsEmptyString(ji.ExtDateTime);
                asd = !dnf && !isNoOpsExt && !hasExt;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 showExtOpt. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean isDNF(int f) {
        return ((f & 8) == 8);
    }

    public static boolean isNoOpsExt(int f) {
        return ((f & 33554432) == 33554432);
    }

    public static boolean isOneHrOpsExtension(int f) {
        return ((f & 1073741824) == 1073741824);
    }

    public static boolean isprobationGC(SharedPreferences p) {
        boolean asd = false;
        Context c = myApp.getAppContext();
        try {
            if (p == null) {
                p = objGlobalMethods.getGcmPreferences();
            }
            String probationGC = objGlobalMethods.getGCMPrefs(p, "probationGC");
            asd = !objGlobalMethods.IsEmptyString(probationGC);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isprobationGC. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static void emptyLULC(SharedPreferences p) {
        objGlobalMethods.setGCMPrefs(p, "lastUpdatedLocationCustomJsonStr", "");
    }

    public static void set203HendersonAsLULC(SharedPreferences p) {
        try {
            if (p == null) {
                p = objGlobalMethods.getGcmPreferences();
            }
            String[] prefName = new String[]{"fLAT", "fLNG"};
            String[] prefvalue = new String[]{"1.281813", "103.820943"};
            objGlobalMethods.setGCMPrefsMass(p, prefName, prefvalue);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 set203HendersonAsLULC. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void setOutsideHendersonLULC(SharedPreferences p) {
        try {
            if (p == null) {
                p = objGlobalMethods.getGcmPreferences();
            }
            String[] prefName = new String[]{"fLAT", "fLNG"};
            String[] prefvalue = new String[]{"1.3407859167934801", "103.74692008949968"};
            objGlobalMethods.setGCMPrefsMass(p, prefName, prefvalue);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 setOutsideHendersonLULC. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static lastUpdatedLocationCustom getlastUpdatedLocationCustom(SharedPreferences p) {
        lastUpdatedLocationCustom lulc = null;
        try {
            if (p == null) {
                p = objGlobalMethods.getGcmPreferences();
            }
            String fLAT = objGlobalMethods.getGCMPrefs(p, "fLAT");
            String fLNG = objGlobalMethods.getGCMPrefs(p, "fLNG");

            if (!objGlobalMethods.IsEmptyString(fLAT) && !objGlobalMethods.IsEmptyString(fLNG)) {
                lulc = new lastUpdatedLocationCustom();
                lulc.setLat(Double.parseDouble(fLAT));
                lulc.setLng(Double.parseDouble(fLNG));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 getlastUpdatedLocationCustom. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return lulc;
        }
    }

    public static boolean enableVRP_EditRouteButton_NearHendersonOfcNew(SharedPreferences p, Boolean isRefresh) {
        boolean asd = false;
        try {
            if (p == null) {
                p = objGlobalMethods.getGcmPreferences();
            }
            if (isRefresh == null)
                isRefresh = false;
            Location gpsLocationFD = objGlobalMethods.getLastKnownLocationForJobUpdate();
            boolean isNearHendersonOfc = objGlobalMethods.isNearHendersonOfc((float) gpsLocationFD.getLatitude(), (float) gpsLocationFD.getLongitude());
            //isNearHendersonOfc = false;
            if (isNearHendersonOfc && isRefresh) {
                String[] prefName = new String[]{"QCabinetDataJsonStr", "InitialSetOfJobsIDXsJsonStr", "CabinetLastUpdated"};
                String[] prefvalue = new String[]{"", "", ""};
                objGlobalMethods.setGCMPrefsMass(p, prefName, prefvalue);

                String[] vrpPrefName = new String[]{"saveAmended4QuesJsonStr"};
                String[] vrpPrefValue = new String[]{""};
                objGlobalMethods.setVrpSPMass2(vrpPrefName, vrpPrefValue);


                asd = true;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 enableVRP_EditRouteButton_NearHendersonOfcNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean wazeInstalled() {
        boolean asd = false;
        Context c = myApp.getAppContext();
        try {
            // check if available
            c.getPackageManager().getPackageInfo("com.waze", 0);
            asd = true;
        } catch (PackageManager.NameNotFoundException e) {
            // if not available set
            // available as false
            asd = false;
        } finally {
            return asd;
        }
    }

    public static float getDistance(Location a, Location b, float adjustment) {
        ////return in meters
        float v = 0;
        try {
            v = a.distanceTo(b) * adjustment;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 getDistance. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return v;
        }
    }

    public static String SHA1(String text, boolean addsalt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        if (addsalt) {
            String salt = "xd3l199908000gh4r0ld1993k34n2014ch4r537"; //39chars
            text = text + salt;
        }

        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    //start Hash SHA1
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        String v = "";
        try {
            for (byte b : data) {
                int halfbyte = (b >>> 4) & 0x0F;
                int two_halfs = 0;
                do {
                    buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                    halfbyte = b & 0x0F;
                } while (two_halfs++ < 1);
            }
            v = buf.toString().toUpperCase();
        } catch (Exception e) {
            v = "";
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 convertToHex. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return v;
        }
    }

    public static String convertLongArrayToString(Long[] l) {
        String v = "";
        try {
            if (l != null && l.length > 0) {
                for (int i = 0; i <= l.length - 1; i++) {
                    v += String.valueOf(l[i]) + ",";
                }
                if (!objGlobalMethods.IsEmptyString(v)) {
                    v = v.substring(0, v.length() - 1);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 convertLongArrayToString. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return v;
        }
    }

    public static boolean CanLeaveDoorStep(int flag2) {
        boolean asd = false;
        try {
            asd = (flag2 & 2048) == 2048;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 CanLeaveDoorStep. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean hasResidentialLocationType(objCheckedItems[] objCheckedItemsAL) {
        boolean asd = false;
        objJobInfo ji = null;
        objCheckedItems objCheckedItemsobj = null;
        try {
            if (objCheckedItemsAL != null && objCheckedItemsAL.length > 0) {
                for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                    objCheckedItemsobj = objCheckedItemsAL[i];
                    ji = objCheckedItemsobj.jobinfo;
                    asd = (getLocationType(ji) == 1);
                    if (asd) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 hasResidentialLocationType. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static int getLocationType(objJobInfo ji) {
        int type = 1; //1 == residential, 2 == ofc, if is null or 0, default residential
        boolean isStandardRunJob = false;
        try {
            /*if (ji != null && ji.DLRedirectedLocation != null) {
                type = ji.DLRedirectedLocation.LocationType;
            } else if (ji != null && ji.DLLocation != null) {
                type = ji.DLLocation.LocationType;
            }*/

            /*if (ji != null && ji.DLRedirectedLocation != null) {
                type = ji.DLRedirectedLocation.LocationType == 0 ? 1 : ji.DLRedirectedLocation.LocationType;
            } else if (ji != null && ji.DLLocation != null) {
                type = ji.DLLocation.LocationType == 0 ? 1 : ji.DLLocation.LocationType;
            }*/


            //by default, backend regards null or 0 as office for vrp purpose
            //but in luke,
            // 1 == residential, 2 == ofc, if is null or 0, default residential,
            //thus must check for standard run n make standard run job with locationtype null or 0 as office
            if (ji != null)
                isStandardRunJob = JobsHandler.isStandardRunJob(ji);

            if (isStandardRunJob) {
                type = 2;
            } else if (ji != null && ji.DLRedirectedLocation != null) {
                type = ji.DLRedirectedLocation.LocationType == 0 ? 1 : ji.DLRedirectedLocation.LocationType;
            } else if (ji != null && ji.DLLocation != null) {
                type = ji.DLLocation.LocationType == 0 ? 1 : ji.DLLocation.LocationType;
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 getLocationType. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return type;
        }
    }

    public static boolean reqLesserImgCompression(objJobInfo ji) {
        boolean asd = false;
        try {
            if (ji != null && ji.FLAG2 != null && ji.FLAG2 > 0) {
                asd = (ji.FLAG2 & 512) == 512;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 reqLesserImgCompression. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean canUnbrick(objJobInfo ji) { //not using, flag2 512 in used for pc dreams
        boolean asd = false;
        try {
            if (ji != null && ji.FLAG2 != null && ji.FLAG2 > 0) {
                asd = (ji.FLAG2 & 512) == 512;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 canUnbrick. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static ArrayList getPromiseIntervalDesc(Date d, int min, int idx) {
        ArrayList al = new ArrayList();
        Calendar nc = Calendar.getInstance();
        nc.setTime(d);
        int nc_min = -1;
        boolean set_hr = false;
        int set_min = 0;
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        Date f, t;
        try {
            from.setTime(d);
            from.set(Calendar.MINUTE, 0);
            to.setTime(d);
            to.set(Calendar.MINUTE, 0);

            nc_min = nc.get(Calendar.MINUTE) - (idx > 0 ? min : 0);

            if (nc_min >= 0 && nc_min < 15) {
                set_min = 15;
            } else if (nc_min >= 15 && nc_min < 30) {
                set_min = 30;
            } else if (nc_min >= 30 && nc_min < 45) {
                set_min = 45;
            } else if (nc_min >= 45 && nc_min <= 59) {
                set_hr = true;
                set_min = 0;
            }

            if (set_hr) {
                from.add(Calendar.HOUR, 1);
                to.add(Calendar.HOUR, 1);
            }

            from.add(Calendar.MINUTE, set_min);
            to.add(Calendar.MINUTE, set_min + min);
            f = from.getTime();
            t = to.getTime();

            al.add(f);
            al.add(t);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 getPromiseIntervalDesc. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return al;
        }
    }

    public static int getPromiseInterval(String finalSettingsStr) {
        int PromiseInterval = 0;
        settings sets = null;
        Gson gson = new Gson();
        try {
            if (!objGlobalMethods.IsEmptyString(finalSettingsStr)) {
                sets = gson.fromJson(finalSettingsStr, settings.class);
                PromiseInterval = sets.getPromiseInterval();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 getPromiseInterval. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return PromiseInterval;
        }
    }

    public static boolean hasPromiseInterval(SharedPreferences gcmp) {
        boolean asd = false;
        settings sets = null;
        Gson gson = new Gson();
        try {
            if (gcmp == null) {
                gcmp = objGlobalMethods.getGcmPreferences();
            }
            String finalSettingsStr = objGlobalMethods.getGCMPrefs(gcmp, "finalSettingsStr");
            if (!objGlobalMethods.IsEmptyString(finalSettingsStr)) {
                sets = gson.fromJson(finalSettingsStr, settings.class);
                if (sets != null) {
                    asd = true;
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 hasPromiseInterval. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean checkMassUpdateHasDOCODtoCollectOrNot(objCheckedItems[] objCheckedItemsAL) {
        boolean asd = false;
        objJobInfo ji = null;
        objCheckedItems objCheckedItemsobj = null;
        try {
            if (objCheckedItemsAL != null && objCheckedItemsAL.length > 0) {
                for (int i = objCheckedItemsAL.length - 1; i >= 0; i--) {
                    ji = null;
                    ji = objCheckedItemsAL[i].jobinfo;
                    if (JobsHandler.checkforDOCOD(ji)) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobsHandler2 checkMassUpdateHasDOCODtoCollectOrNot. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static Boolean checkforSpecialDOCO(objJobInfo ji) {
        Boolean asd = false;
        objDOObject DOList = null;
        objDOCOD[] CODList = null;
        objDOCOD DOCOD = null;
        try {
            if (ji.DOList != null || ji.DOList.length > 0) {
                for (int i = 0; i <= ji.DOList.length - 1; i++) {
                    DOList = null;
                    CODList = null;
                    DOCOD = null;
                    DOList = ji.DOList[i];
                    CODList = DOList.CODList;

                    if (CODList != null && CODList.length > 0) {
                        for (int x = 0; x <= CODList.length - 1; x++) {
                            DOCOD = CODList[x];
                            if (DOCOD.AmountTypeIDX == 6 || DOCOD.AmountTypeIDX == 7 || DOCOD.AmountTypeIDX == 8 || DOCOD.AmountTypeIDX == 9 || DOCOD.AmountTypeIDX == 10) {
                                asd = true;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobsHandler2 checkforSpecialDOCO. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    //returns true if shipment verification req verify both nml n cold item, if return false
    public static int needVerifyNnCitems(objJobInfo ji) {
        int asd = 0; //0 == no req, 1 == verify 1 only, 2 == verify NnC
        try {
            if (ji != null) {
                if (ji.Pieces > 0 && ji.Pieces < 2) {
                    asd = 1;
                } else if (ji.Pieces > 0 && ji.Pieces >= 2) {
                    asd = 2;
                }
            }
        } catch (Exception e) {
            asd = 0;
            objGlobalMethods.writeToFile("Exception at JobsHandler2 needVerifyNnCitems. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean ColdNonColdVerified(SharedPreferences gcmp) {
        boolean asd = false;
        String vc = "";
        String vn = "";
        try {
            vc = objGlobalMethods.getGCMPrefs(gcmp, "ShipmentVerifiedCold");
            vn = objGlobalMethods.getGCMPrefs(gcmp, "ShipmentVerified");
            asd = !objGlobalMethods.IsEmptyString(vc) && !objGlobalMethods.IsEmptyString(vn);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 ColdNonColdVerified. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static int jcCount(ArrayList<String> ar) {
        int c = 0;
        try {
            if (ar != null) {
                for (int i = 0; i <= ar.size() - 1; i++) {
                    if (ar.get(i).equals("jc")) {
                        c++;
                    }
                }
            }
        } catch (Exception e) {
            c = 0;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 jcCount. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return c;
        }
    }

    public static int jxCount(ArrayList<String> ar) {
        int c = 0;
        try {
            if (ar != null) {
                for (int i = 0; i <= ar.size() - 1; i++) {
                    if (ar.get(i).equals("jx")) {
                        c++;
                    }
                }
            }
        } catch (Exception e) {
            c = 0;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 jxCount. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return c;
        }
    }

    public static boolean isJC(String vstr) {
        boolean asd = false;
        try {
            asd = vstr.toUpperCase().indexOf(objGlobalVariables.JobJCKey) == 0;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isJC. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean hasColdItem(objJobInfo ji) {
        boolean asd = false;
        try {
            asd = (ji != null && ((ji.FLAG2 & 64) == 64));
        } catch (Exception e) {
            asd = false;
            objGlobalMethods.writeToFile("Exception at JobsHandler2 hasColdItem. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean isColdItem(int FLAG2) {
        boolean asd = false;
        try {
            asd = (FLAG2 & 64) == 64;
        } catch (Exception e) {
            asd = false;
            objGlobalMethods.writeToFile("Exception at JobsHandler2 isColdItem. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static String getDelUnit_combine(String DelUnit_combine) {
        String newval = DelUnit_combine;
        try {
            newval = newval.replace(" ", "");
            newval = newval.replace("-", "");
            newval = newval.replace("_", "");
            newval = newval.toLowerCase().replace("level", "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 getDelUnit_combine. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return newval;
        }
    }

    public static List<List<String>> returnAllUnits(String DelUnit_original) {
        List<List<String>> lm = new ArrayList<>();
        List<String> list = new ArrayList<>();
        List<String> listc = new ArrayList<>();
        String[] array = null;
        String v = "";
        String firstunit = "";
        String firstunitCombined = "";
        String newunit = "";
        String newunitCombined = "";
        int reservedCharCount = 0;
        try {
            DelUnit_original = DelUnit_original.replaceAll("\\s+", ""); // replace all spaces to empty string
            if (!objGlobalMethods.IsEmptyString(DelUnit_original)) {
                array = DelUnit_original.split("/");
                if (array != null && array.length > 0) {
                    for (int i = 0; i <= array.length - 1; i++) {
                        v = array[i];
                        if (i == 0) {
                            firstunit = v;
                            firstunitCombined = getDelUnit_combine(firstunit);
                            list.add(firstunit);
                            listc.add(firstunitCombined);
                            continue;
                        }
                        reservedCharCount = firstunit.length() - v.length();
                        newunit = firstunit.substring(0, reservedCharCount) + v;
                        if (!list.contains(newunit)) {
                            newunitCombined = getDelUnit_combine(newunit);
                            list.add(newunit);
                            listc.add(newunitCombined);
                        }
                    }
                }
            }
            lm.add(list);
            lm.add(listc);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2.returnAllUnits. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return lm;
        }
    }

    public static Boolean verifyBlkUnitNumber(String val, String DelBlock, List<String> AllDelUnits, List<String> AllDelUnitsCombined) {
        boolean asd = false;
        String[] array = null;
        List<String> list = null;
        boolean hasArray = false;
        try {

            if ((AllDelUnits == null || (AllDelUnits != null && AllDelUnits.size() == 0)) && (AllDelUnits == null || (AllDelUnits != null && AllDelUnits.size() == 0))) {
                asd = objGlobalMethods.IsEqualString(val, DelBlock);
            } else {
                for (int i = 0; i <= AllDelUnitsCombined.size() - 1; i++) {
                    asd = objGlobalMethods.IsEqualString(val, AllDelUnitsCombined.get(i));
                    if (asd)
                        break;
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 verifyBlkUnitNumber. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static Boolean verifyBlkUnitNumber2(jobSelectionInfo j1, jobSelectionInfo j2) {
        boolean asd = false;
        boolean emptyPostal1 = false;
        boolean emptyPostal2 = false;
        boolean emptyBlk1 = false;
        boolean emptyBlk2 = false;
        boolean emptyAllDelUnitsCombinedList1 = false;
        boolean emptyAllDelUnitsCombinedList2 = false;
        try {
            emptyPostal1 = objGlobalMethods.IsEmptyString(j1.POSTAL);
            emptyPostal2 = objGlobalMethods.IsEmptyString(j2.POSTAL);
            emptyBlk1 = objGlobalMethods.IsEmptyString(j1.DelBlock);
            emptyBlk2 = objGlobalMethods.IsEmptyString(j2.DelBlock);
            emptyAllDelUnitsCombinedList1 = ((j1.AllDelUnitsCombinedList == null || (j1.AllDelUnitsCombinedList != null && j1.AllDelUnitsCombinedList.size() == 0)));
            emptyAllDelUnitsCombinedList2 = ((j2.AllDelUnitsCombinedList == null || (j2.AllDelUnitsCombinedList != null && j2.AllDelUnitsCombinedList.size() == 0)));

            if (emptyPostal1 || emptyPostal2)
                return false;

            if (!objGlobalMethods.IsEqualString(j1.POSTAL, j2.POSTAL))
                return false;

            if (emptyBlk1 || emptyBlk2)
                return false;

            if (emptyAllDelUnitsCombinedList1 && emptyAllDelUnitsCombinedList2) {
                asd = objGlobalMethods.IsEqualString(j1.DelBlock, j2.DelBlock);
                if (!asd)
                    return false;
            }

            if (!emptyAllDelUnitsCombinedList1 && !emptyAllDelUnitsCombinedList2) {
                ArrayList main = j1.AllDelUnitsCombinedList.size() > j2.AllDelUnitsCombinedList.size() ? j1.AllDelUnitsCombinedList : j2.AllDelUnitsCombinedList;
                ArrayList sub = j2.AllDelUnitsCombinedList.size() > j1.AllDelUnitsCombinedList.size() ? j2.AllDelUnitsCombinedList : j1.AllDelUnitsCombinedList;
                boolean exists = false;
                for (int i = 0; i <= sub.size() -1; i++) {
                    if (main.contains(sub.get(i))) {
                        exists = true;
                        break;
                    }
                }
                if (!exists)
                    return false;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 verifyBlkUnitNumber2. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean verifyContactNumsLast4(String AllValidTelNums, String last4) {
        boolean asd = false;
        String[] al = null;
        String v = "";
        String vlast4 = "";
        try {
            AllValidTelNums = AllValidTelNums.replaceAll(" ", "");
            if (!objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                al = AllValidTelNums.split(",");
            }
            if (al != null && al.length > 0) {
                for (int i = 0; i <= al.length - 1; i++) {
                    vlast4 = "";
                    v = al[i];
                    vlast4 = !objGlobalMethods.IsEmptyString(v) ? v.substring(v.length() - 4, v.length()) : "";
                    asd = !objGlobalMethods.IsEmptyString(vlast4) ? objGlobalMethods.IsEqualString(last4, vlast4) : false;
                    if (asd) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 verifyContactNumsLast4. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean RecipientTypeRequireRemarks(int flag) {
        boolean asd = false;
        try {
            asd = (flag & 8) == 8;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 RecipientTypeRequireRemarks. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean DipFromIsFutureDate() {
        boolean asd = false;
        try {

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 DipFromIsFutureDate. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean exemptFromCallingForLBDoorGuard(objJobInfo ji) {
        boolean asd = false;
        try {
            asd = ji != null && ji.FLAG != null && ((ji.FLAG & 268435456) == 268435456);
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(gcmp, "exemptFromCallingForLBDoorGuard", asd ? "t" : "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 exemptFromCallingForLBDoorGuard. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static void GetInventoryBatch(String ABatchID) {
        ArrayList<InventoryLite> invls = null;
        InventoryLite invl = null;
        Inventory[] invs = null;
        Inventory inv = null;
        Context ctx = myApp.getAppContext();
        String InventoryBatchIDListJsonStr = "";
        ArrayList<InventoryBatchID> InventoryBatchIDList = null;
        InventoryBatchID saved_ib = null;
        InventoryBatchID ib = null;
        InventoryDetails[] ids = null;
        InventoryDetails id = null;
        String InventoryBatchIDStr = "";
        String[] StockReferences = null;
        String ref = "";
        ArrayList<StockRef> srs = null;
        StockRef sr = null;
        Gson gson = new Gson();
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        boolean found = false;
        try {
            InventoryBatchIDListJsonStr = objGlobalMethods.getGCMPrefs(g, "InventoryBatchIDListJsonStr");
            if (!objGlobalMethods.IsEmptyString(InventoryBatchIDListJsonStr)) {
                InventoryBatchIDList = gson.fromJson(InventoryBatchIDListJsonStr, new TypeToken<List<InventoryBatchID>>() {
                }.getType());
            }

            if (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) {
                for (int i = 0; i <= InventoryBatchIDList.size() - 1; i++) {
                    saved_ib = InventoryBatchIDList.get(i);
                    if (!objGlobalMethods.IsEmptyString(saved_ib.Batch_ID) && objGlobalMethods.IsEqualString(saved_ib.Batch_ID, ABatchID)) {
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                String HOMESAPI = ctx.getString(R.string.action_HOMESAPI);
                invs = WebService.GetInventoryBatch(HOMESAPI, ABatchID);

                if (invs != null && invs.length > 0) {
                    for (int i = 0; i <= invs.length - 1; i++) {
                        inv = invs[i];
                        if (invls == null)
                            invls = new ArrayList<>();

                        invl = new InventoryLite();
                        invl.setBatch_ID(ABatchID);
                        invl.setIDX(inv.IDX);
                        invl.setStockCode(inv.StockCode);
                        invl.setHiddenStockCode("0_" + inv.StockCode);
                        invl.setDescription(inv.Description);
                        invl.setBarcode(inv.Barcode);
                        invl.setBalance(inv.Balance);
                        //invl.setvBalance(i < 9 ? i+1 : (i >= 10 && i <= 19) ? inv.Balance : 0);
                        invl.setFlag(inv.Flag);
                        invl.setInvTallyMode(
                                (!((invl.Flag & 2) == 2) && !((invl.Flag & 4) == 4) && !((invl.Flag & 8) == 8) && !((invl.Flag & 16) == 16) && !((invl.Flag & 32) == 32) && !((invl.Flag & 64) == 64))
                                        ? 1 : 3
                        );

                        if (inv.StockMovement != null && inv.StockMovement.length > 0) {
                            for (int s = 0; s <= inv.StockMovement.length - 1; s++) {
                                id = inv.StockMovement[s];
                                if (id.StockReferences != null && id.StockReferences.length > 0) {
                                    for (int r = 0; r <= id.StockReferences.length - 1; r++) {
                                        ref = id.StockReferences[r];
                                        invl.addStockRefByStringRef(ref);
                                    }
                                }
                            }
                        }
                        invls.add(invl);
                    }
                }

                if (invls != null && invls.size() > 0) {
                    ib = new InventoryBatchID(ABatchID, invls);
                    ib.addLogs(ABatchID + " BatchID detected.");
                    InventoryBatchIDStr = gson.toJson(ib);
                }

                if (InventoryBatchIDList == null)
                    InventoryBatchIDList = new ArrayList<>();

                if (ib != null)
                    InventoryBatchIDList.add(ib);

                InventoryBatchIDListJsonStr = (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) ? gson.toJson(InventoryBatchIDList) : "";

                String[] prefName = new String[]{"InventoryBatchIDListJsonStr"};
                String[] prefvalue = new String[]{InventoryBatchIDListJsonStr};
                objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
                objGlobalMethods.writeToFile("BatchID " + ABatchID + " downloaded.", "", "", "", "");
            } else {
                objGlobalMethods.writeToFile("BatchID " + ABatchID + " already exists. Skipped JobsHandler2 GetInventoryBatch.", "", "", "", "");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 GetInventoryBatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void checkForBatchIDsDataToSendBack() {
        ArrayList<InventoryLite> invls = null;
        InventoryLite invl = null;
        Inventory[] invs = null;
        Inventory inv = null;
        Context c = myApp.getAppContext();
        String InventoryBatchIDListJsonStr = "";
        ArrayList<InventoryBatchID> InventoryBatchIDList = null;
        InventoryBatchID saved_ib = null;
        InventoryBatchID ib = null;
        InventoryDetails[] ids = null;
        InventoryDetails id = null;
        String InventoryBatchIDStr = "";
        String[] StockReferences = null;
        String ref = "";
        ArrayList<StockRef> srs = null;
        StockRef sr = null;
        Gson gson = new Gson();
        SharedPreferences g = objGlobalMethods.getGcmPreferences();
        ArrayList<String> vStockRefs = null;
        String[] vStockRefsArr = null;
        VerifiedItem vi = null;
        ArrayList<VerifiedItem> vilist = null;
        StockVerificationList svl = null;
        VerifiedItem[] viArr = null;
        String[] logsArr = null;
        ArrayList<String> logslist = null;
        UploadStockVerificationList stockVerList = null;

        String username;
        String password;
        String useridx;
        String driveridx;
        String ishomesuser;
        String[] UsernamePwd = new String[4];

        try {
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(g);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            Boolean IsHOMESuser = objGlobalMethods.IsHomesUserString(ishomesuser, g);

            InventoryBatchIDListJsonStr = objGlobalMethods.getGCMPrefs(g, "InventoryBatchIDListJsonStr");
            if (!objGlobalMethods.IsEmptyString(InventoryBatchIDListJsonStr)) {
                InventoryBatchIDList = gson.fromJson(InventoryBatchIDListJsonStr, new TypeToken<List<InventoryBatchID>>() {
                }.getType());
            }

            if (InventoryBatchIDList != null && InventoryBatchIDList.size() > 0) {
                for (int z = InventoryBatchIDList.size() - 1; z >= 0; z--) {
                    ib = InventoryBatchIDList.get(z);
                    vilist = null;
                    if (ib != null && ib.invls != null && ib.invls.size() > 0) {
                        invls = ib.invls;
                        for (int i = 0; i <= invls.size() - 1; i++) {
                            vStockRefs = new ArrayList<>();
                            invl = invls.get(i);
                            if (invl.Stockrefs != null && invl.Stockrefs.size() > 0) {
                                srs = invl.Stockrefs;
                                for (int v = 0; v <= srs.size() - 1; v++) {
                                    sr = srs.get(v);
                                    if (sr.verified) {
                                        vStockRefs.add(sr.StockReference);
                                    }
                                }
                            }

                            vStockRefsArr = vStockRefs != null && vStockRefs.size() > 0 ? vStockRefs.toArray(new String[vStockRefs.size()]) : null;
                            vi = new VerifiedItem(invl.IDX, invl.vBalance, vStockRefsArr);
                            if (vilist == null)
                                vilist = new ArrayList<>();
                            vilist.add(vi);
                        }

                        if (vilist != null && vilist.size() > 0)
                            viArr = vilist.toArray(new VerifiedItem[vilist.size()]);

                        logslist = ib.logs;
                        if (logslist != null && logslist.size() > 0)
                            logsArr = logslist.toArray(new String[logslist.size()]);

                        svl = new StockVerificationList();
                        svl.setBatch_ID(ib.Batch_ID);
                        svl.setItems(viArr);
                        svl.setLogs(logsArr);

                        stockVerList = new UploadStockVerificationList();
                        stockVerList.setAPIKey(c.getString(R.string.action_HOMESAPI));
                        stockVerList.setStockVerificationList(svl);
                        stockVerList.setAUserIDX(IsHOMESuser && !objGlobalMethods.IsEmptyString(useridx) ? Long.parseLong(useridx) : 0);
                        stockVerList.setADriverIDX(!IsHOMESuser && !objGlobalMethods.IsEmptyString(useridx) ? Long.parseLong(useridx) : 0);

                        if (stockVerList != null) {
                            JobsHandler.addUploadStockVerificationListQueALPrefs(stockVerList);
                            //InventoryBatchIDList.remove(z);
                        }

                    }

                    InventoryBatchIDList.remove(z);

                }
                objGlobalMethods.setGCMPrefs(g, "InventoryBatchIDListJsonStr", "");
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 checkForBatchIDsDataToSendBack. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static ArrayList<objJobInfo[]> separateNormMedPIPJobInfoAL(objJobInfo[] jia) {
        ArrayList<objJobInfo[]> list = null;
        objJobInfo ji = null;
        objJobInfo jin = null;
        objJobInfo jim = null;
        ArrayList<objJobInfo> norm = null;
        ArrayList<objJobInfo> med = null;

        String INDUSTRY = "";
        long CL_CONTACTIDX = 3650876L; //test contactidx to get meds jobs

        try {
            if (jia != null && jia.length > 0) {
                for (int i = 0; i <= jia.length - 1; i++) {
                    ji = jia[i];
                    jin = null;
                    jim = null;
                    if ((ji.StatusCodeIDX == 103 || ji.StatusCodeIDX == 0) && ji.ContactIDX == CL_CONTACTIDX) {
                        //med pip
                        jim = ji;
                    } else if ((ji.StatusCodeIDX == 103 || ji.StatusCodeIDX == 0) && ji.ContactIDX != CL_CONTACTIDX) {
                        //norm pip
                        jin = ji;
                    }

                    if (jim != null) {
                        if (med == null)
                            med = new ArrayList<>();
                        med.add(jim);
                    } else if (jin != null) {
                        if (norm == null)
                            norm = new ArrayList<>();
                        norm.add(jin);
                    }
                }

                if (norm != null || med != null) {
                    if (list == null)
                        list = new ArrayList<>();

                    list.add(norm.toArray(new objJobInfo[norm.size()]));
                    list.add(med.toArray(new objJobInfo[med.size()]));
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 separateNormMedPIPJobInfoAL. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return list;
        }
    }


    public static ArrayList<PipPcs> rtnPipPcs(objJobInfo[] ar, List<objJobInfo> al) {
        ArrayList<PipPcs> list = null;
        PipPcs p = null;
        boolean isMedCol = false;
        objJobInfo ji = null;
        try {
            if (ar != null && ar.length > 0) {
                for (int i = 0; i <= ar.length - 1; i++) {
                    ji = ar[i];
                    isMedCol = JobsHandler.isMedColCNIDX(ji.ContactIDX) || JobsHandler.isAlpsClientIDX(ji) || JobsHandler.isMedColIndustry(ji.INDUSTRY);
                    if (isMedCol) {
                        p = new PipPcs(ji.JobsIDX, ji.Pieces, 0);
                        if (list == null)
                            list = new ArrayList<>();
                        list.add(p);
                    }
                }
            }
            if (al != null && al.size() > 0) {
                for (int i = 0; i <= al.size() - 1; i++) {
                    ji = al.get(i);
                    isMedCol = JobsHandler.isMedColCNIDX(ji.ContactIDX) || JobsHandler.isAlpsClientIDX(ji) || JobsHandler.isMedColIndustry(ji.INDUSTRY);
                    if (isMedCol) {
                        p = new PipPcs(ji.JobsIDX, ji.Pieces, 0);
                        if (list == null)
                            list = new ArrayList<>();
                        list.add(p);
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 rtnPipPcs. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return list;
        }
    }

    public static void removePostalFrom_credOpenedPostalsNew(SharedPreferences sp, boolean resetQ) {
        int cred_postalcountusedd = 0;
        int cred_postalcountuseddp = 0;
        List<String> listPipDip = null;
        List<String> missing = null;
        ArrayList<String> credUsedPostals = new ArrayList<>();
        ArrayList<String> credUsedPostalsp = new ArrayList<>();
        ArrayList<String> credOpenedPostals = null;
        ArrayList<String> credOpenedPostalsp = null;
        boolean updateCabinet = false;
        String saveAmended4QuesJsonStr = "";
        List<JobQueue> SaveAmended4Ques = null;
        List<JobQueue> OldSaveAmended4Ques = null;
        List<String> removedList = null;
        List<Integer> QueIdxList = null;
        int queue = 0;
        objGC_CustomQueue GC_CustomQueue = null;
        ArrayList<objGC_CustomQueue> AQueue = null;
        String ProcessJobQueJsonStr = "";
        String ManualQueuedPostalUsedJson = "";
        List<String> ManualQueuedPostalUsedList = new ArrayList<>();
        Gson gson = new Gson();

        if (sp == null)
            sp = objGlobalMethods.getVrpRelatedSharedPreferences();
        int c = 0;
        try {
            /*boolean isAM = JobsHandler3.isAM();
            boolean isPM = JobsHandler3.isPM();
            boolean isAOH = JobsHandler3.isAOH();*/
            int AMONLYc = 0;
            int AMc = 0;
            int PMc = 0;
            int AOHc = 0;
            String creditAM = "";
            String creditPM = "";
            String creditAOH = "";
            creditAM = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "creditAM");
            creditPM = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "creditPM");
            creditAOH = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "creditAOH");
            credit prevcd = null;
            credit cdAM = null;
            credit cdPM = null;
            credit cdAOH = null;
            credit cdc = null;
            credit cd = null;

            AMONLYc = DAO_Jobs.getAllPostalsInPipDipByAMOnlySlot().size();
            listPipDip = DAO_Jobs.getAllPostalsInPipDipByAMSlot();
            AMc = AMONLYc > 0 ? listPipDip.size() : 0;
            if (AMc == 0) {
                listPipDip = DAO_Jobs.getAllPostalsInPipDipByPMSlot();
                PMc = listPipDip.size();
                if (PMc == 0) {
                    listPipDip = DAO_Jobs.getAllPostalsInPipDipByAOHSlot();
                    AOHc = listPipDip.size();
                }
            }

            /*cdAM = (isAM || isPM) && !objGlobalMethods.IsEmptyString(creditAM) ? gson.fromJson(creditAM, credit.class) : null;
            cdPM = isPM && !objGlobalMethods.IsEmptyString(creditPM) ? gson.fromJson(creditPM, credit.class) : null;
            cdAOH = isAOH && !objGlobalMethods.IsEmptyString(creditAOH) ? gson.fromJson(creditAOH, credit.class) : null;
            prevcd = isPM ? cdAM : isAOH ? cdPM : null;
            cd = isAM ? cdAM : isPM ? cdPM : isAOH ? cdAOH : null;*/
            cdAM = (AMc > 0) && !objGlobalMethods.IsEmptyString(creditAM) ? gson.fromJson(creditAM, credit.class) : null;
            cdPM = (AMc == 0 && PMc > 0) && !objGlobalMethods.IsEmptyString(creditPM) ? gson.fromJson(creditPM, credit.class) : null;
            cdAOH = (PMc == 0 && AOHc > 0) && !objGlobalMethods.IsEmptyString(creditAOH) ? gson.fromJson(creditAOH, credit.class) : null;
            cd = cdAM != null ? cdAM : cdPM != null ? cdPM : cdAOH != null ? cdAOH : null;

            if (prevcd != null) {
                credOpenedPostalsp = prevcd.credOpenedPostals;
                if (credOpenedPostalsp != null && credOpenedPostalsp.size() > 0) {
                    //listPipDip = DAO_Jobs.getAllPostalsInPipDipBySlot(isAM ? 1 : isPM ? 2 : 4);
                    List<String> listPipDipA = listPipDip;
                    missing = credOpenedPostalsp.stream()
                            .filter(item -> !listPipDipA.contains(item))
                            .collect(Collectors.toList());
                    if (missing != null && missing.size() > 0) {
                        for (int i = 0; i <= missing.size() -1; i++) {
                            credOpenedPostalsp.remove(missing.get(i));
                            updateCabinet = true;
                            if (removedList == null)
                                removedList = new ArrayList<>();
                            removedList.add(missing.get(i));
                        }
                    }
                }

                prevcd.setCredOpenedPostals(credOpenedPostalsp);
                credUsedPostalsp = prevcd.credUsedPostals;
                cred_postalcountuseddp = credUsedPostalsp != null ? credUsedPostalsp.size() : 0;
                if (cred_postalcountuseddp < 0)
                    cred_postalcountuseddp = 0;
                prevcd.setCred_postalcountused(cred_postalcountuseddp);

                if (cd != null) {
                    credOpenedPostals = cd.credOpenedPostals;
                    if (credOpenedPostals != null && credOpenedPostals.size() > 0) {
                        /*if (listPipDip == null)
                            listPipDip = DAO_Jobs.getAllPostalsInPipDipBySlot(isAM ? 1 : isPM ? 2 : 4);*/
                        List<String> listPipDipA = listPipDip;
                        missing = credOpenedPostals.stream()
                                .filter(item -> !listPipDipA.contains(item))
                                .collect(Collectors.toList());
                        if (missing != null && missing.size() > 0) {
                            for (int i = 0; i <= missing.size() -1; i++) {
                                credOpenedPostals.remove(missing.get(i));
                                updateCabinet = true;
                                if (removedList == null)
                                    removedList = new ArrayList<>();
                                removedList.add(missing.get(i));
                            }
                        }
                    }

                    cd.setCredOpenedPostals(credOpenedPostals);
                    credUsedPostals = cd.credUsedPostals;
                    cred_postalcountusedd = credUsedPostals != null ? credUsedPostals.size() : 0;
                    if (cred_postalcountusedd < 0)
                        cred_postalcountusedd = 0;
                    cd.setCred_postalcountused(cred_postalcountusedd);
                }
            } else if (cd != null) {
                credOpenedPostals = cd.credOpenedPostals;
                if (credOpenedPostals != null && credOpenedPostals.size() > 0) {
                    /*if (listPipDip == null)
                        listPipDip = DAO_Jobs.getAllPostalsInPipDipBySlot(isAM ? 1 : isPM ? 2 : 4);*/
                    List<String> listPipDipA = listPipDip;
                    missing = credOpenedPostals.stream()
                            .filter(item -> !listPipDipA.contains(item))
                            .collect(Collectors.toList());
                    if (missing != null && missing.size() > 0) {
                        for (int i = 0; i <= missing.size() -1; i++) {
                            credOpenedPostals.remove(missing.get(i));
                            updateCabinet = true;
                            if (removedList == null)
                                removedList = new ArrayList<>();
                            removedList.add(missing.get(i));
                        }
                    }
                }

                cd.setCredOpenedPostals(credOpenedPostals);
                credUsedPostals = cd.credUsedPostals;
                cred_postalcountusedd = credUsedPostals != null ? credUsedPostals.size() : 0;
                if (cred_postalcountusedd < 0)
                    cred_postalcountusedd = 0;
                cd.setCred_postalcountused(cred_postalcountusedd);
            } else {
                return;
            }

            if (updateCabinet && removedList != null && removedList.size() > 0) {
                saveAmended4QuesJsonStr = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "saveAmended4QuesJsonStr");
                if (!objGlobalMethods.IsEmptyString(saveAmended4QuesJsonStr))
                    SaveAmended4Ques = gson.fromJson(saveAmended4QuesJsonStr, new TypeToken<ArrayList<JobQueue>>() {
                    }.getType());

                if (SaveAmended4Ques != null && SaveAmended4Ques.size() > 0) {
                    OldSaveAmended4Ques = new ArrayList<>();
                    for (int i = 0; i <= SaveAmended4Ques.size() - 1; i++) {
                        OldSaveAmended4Ques.add(SaveAmended4Ques.get(i));
                    }
                }

                if (removedList != null && removedList.size() > 0 && SaveAmended4Ques != null && SaveAmended4Ques.size() > 1) {
                    for (int i = SaveAmended4Ques.size() - 1; i >= 0; i--) {
                        if (removedList.contains(SaveAmended4Ques.get(i).PostalCode))
                            SaveAmended4Ques.remove(i);
                    }

                    JobQueue jq = null;
                    JobQueue njq = null;
                    List<JobQueue> al = new ArrayList<JobQueue>();
                    List<Long> jqJobsIDXsList = null;
                    List<JobQueue> NewJQlist = null;
                    ProcessJobQueJsonStr = objGlobalMethods.getVrpRelatedSP_KeyValue2(sp, "ProcessJobQueJsonStr");
                    if (!objGlobalMethods.IsEmptyString(ProcessJobQueJsonStr))
                        NewJQlist = gson.fromJson(ProcessJobQueJsonStr, new TypeToken<ArrayList<JobQueue>>() {
                        }.getType());

                    for (int a = 0; a <= SaveAmended4Ques.size() - 1; a++) {
                        jq = SaveAmended4Ques.get(a);
                        if (NewJQlist != null && NewJQlist.size() > 0) {
                            for (int n = 0; n <= NewJQlist.size() - 1; n++) {
                                njq = NewJQlist.get(n);
                                if (jq.PostalCode.equals(njq.PostalCode)) {

                                    jqJobsIDXsList = njq.getJobsIDXsList();
                                    if (jqJobsIDXsList != null && jqJobsIDXsList.size() > 0) {
                                        if (al == null)
                                            al = new ArrayList<>();

                                        if (al.contains(njq))
                                            continue;

                                        al.add(njq);

                                        if (QueIdxList == null)
                                            QueIdxList = new ArrayList<>();
                                        QueIdxList.add(n);
                                        queue++;
                                        for (int x = 0; x <= jqJobsIDXsList.size() - 1; x++) {
                                            GC_CustomQueue = new objGC_CustomQueue(jqJobsIDXsList.get(x), queue);
                                            if (AQueue == null) {
                                                AQueue = new ArrayList<objGC_CustomQueue>();
                                            }
                                            AQueue.add(GC_CustomQueue);
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    if ((SaveAmended4Ques == null || (SaveAmended4Ques != null && SaveAmended4Ques.size() == 0)) && NewJQlist != null && NewJQlist.size() > 1) {
                        for (int n = 0; n <= NewJQlist.size() - 1; n++) {
                            njq = NewJQlist.get(n);
                            jqJobsIDXsList = njq.getJobsIDXsList();
                            if (jqJobsIDXsList != null && jqJobsIDXsList.size() > 0) {
                                if (al == null)
                                    al = new ArrayList<>();

                                if (al.contains(njq))
                                    continue;

                                al.add(njq);

                                if (QueIdxList == null)
                                    QueIdxList = new ArrayList<>();
                                QueIdxList.add(n);
                                queue++;
                                for (int x = 0; x <= jqJobsIDXsList.size() - 1; x++) {
                                    GC_CustomQueue = new objGC_CustomQueue(jqJobsIDXsList.get(x), queue);
                                    if (AQueue == null) {
                                        AQueue = new ArrayList<objGC_CustomQueue>();
                                    }
                                    AQueue.add(GC_CustomQueue);
                                }
                                break;
                            }
                        }
                    }

                    if (AQueue != null && AQueue.size() > 0) {
                        SharedPreferences updateJobInfoPref = objGlobalMethods.getUpdateJobInfoPreferences();
                        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                        String regid = objGlobalMethods.getRegistrationId(myApp.getAppContext(), gcmp);

                        saveAmended4QuesJsonStr = al != null && al.size() > 0 ? gson.toJson(al) : "";

                        objGlobalMethods.setVrpRelatedSP_KeyValue2(sp, "saveAmended4QuesJsonStr", saveAmended4QuesJsonStr);
                        String[] UsernamePwd = new String[4];
                        String useridx = "";
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
                        useridx = UsernamePwd[2];
                        Date dt = new Date();
                        Long l = dt.getTime();
                        String timestampstr = "/Date(" + l + ")/";
                        String finalInitialSetOfJobsIDXsJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "InitialSetOfJobsIDXsJsonStr");
                        String[] qal = new String[]{
                                "true", saveAmended4QuesJsonStr, finalInitialSetOfJobsIDXsJsonStr
                        };
                        String alJsonStr = qal != null ? gson.toJson(qal) : "";
                        if (!alJsonStr.isEmpty() && !alJsonStr.equals("[]") && !alJsonStr.equals("")) {
                            objGlobalMethods.setGCMPrefs(gcmp, "QCabinetDataJsonStr", alJsonStr);
                        }
                        objCabinet cabinet = new objCabinet(0, Long.parseLong(!useridx.isEmpty() && !useridx.equals("") && useridx != "" ? useridx : "0"),
                                "2", alJsonStr, timestampstr);
                        objETACabinet ETACabinet = new objETACabinet(regid, cabinet, AQueue);
                        objGlobalMethods.writeToFile("System opens New GC Queue. Updating new GC Queue.", "", "", "", "");
                        JobsHandler.addUpdateGCETA_n_Cabinet_List(updateJobInfoPref, ETACabinet);
                        myApp.getAppContext().sendBroadcast(new Intent(myApp.getAppContext(), BackendRun_BroadcastReceiver.class));
                    }
                }
            }

            if (AMc > 0) {
                if (cd != null && resetQ)
                    cd.setCredOpenedPostals(null);
                creditAM = cd != null ? gson.toJson(cd) : "";
                String[] vrpspprefName = new String[]{"creditAM"};
                String[] vrpspprefvalue = new String[]{creditAM};
                objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
            }
            if (PMc > 0) {
                if (cd != null && resetQ)
                    cd.setCredOpenedPostals(null);
                creditPM = cd != null ? gson.toJson(cd) : "";
                String[] vrpspprefName = new String[]{"creditPM"};
                String[] vrpspprefvalue = new String[]{creditPM};
                objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
            }
            if (AOHc > 0) {
                if (cd != null && resetQ)
                    cd.setCredOpenedPostals(null);
                creditAOH = cd != null ? gson.toJson(cd) : "";
                String[] vrpspprefName = new String[]{"creditAOH"};
                String[] vrpspprefvalue = new String[]{creditAOH};
                objGlobalMethods.setVrpSPMass(sp, vrpspprefName, vrpspprefvalue);
            }

            String res = "";
            if (cd != null)
                res += (AMc > 0 ? "AM cred_postalcountused: " : PMc > 0 ? "PM cred_postalcountused: " : AOHc > 0 ? "AOH cred_postalcountused: " : "") + cd.cred_postalcountused;

            objGlobalMethods.writeToFile("JobsHandler2 removePostalFrom_credOpenedPostalsNew completed #1. " + res, "", "", "", "");

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 removePostalFrom_credOpenedPostalsNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void processedPostalCreditNew() {
        String credUsedPostalsStr = "";
        SharedPreferences vsp = objGlobalMethods.getVrpRelatedSharedPreferences();
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        ArrayList<String> credOpenedPostals = null;
        String cred_postalcountused = "";
        int cred_postalcountusedd = 0;
        ArrayList<String> credUsedPostals = new ArrayList<>();
        ArrayList<String> listcurr = null;
        boolean OnlyOneCredPostalUsed = false;
        String ManualQueuedPostalJson = "";
        ArrayList<String> ManualQueuedPostalList = new ArrayList<>();
        Gson gson = new Gson();
        String ManualQueuedPostalUsedJson = "";
        ArrayList<String> ManualQueuedPostalUsedList = new ArrayList<>();
        String creditJson = "";

        try {
            String postal = objGlobalMethods.getGCMPrefs(gcmp, "desPostal");
            /*boolean isAM = JobsHandler3.isAM();
            boolean isPM = JobsHandler3.isPM();
            boolean isAOH = JobsHandler3.isAOH();*/
            int AMONLYc = 0;
            int PMONLYc = 0;
            int AMc = 0;
            int PMc = 0;
            int AOHc = 0;
            String creditAM = "";
            String creditPM = "";
            String creditAOH = "";

            boolean updatePrevcd = false;
            credit prevcd = null;
            credit cdc = null;
            credit cd = null;
            credit cdAM = null;
            credit cdPM = null;
            credit cdAOH = null;

            creditAM = objGlobalMethods.getVrpRelatedSP_KeyValue2(vsp, "creditAM");
            creditPM = objGlobalMethods.getVrpRelatedSP_KeyValue2(vsp, "creditPM");
            creditAOH = objGlobalMethods.getVrpRelatedSP_KeyValue2(vsp, "creditAOH");

            AMONLYc = DAO_Jobs.getAllPostalsInPipDipByAMOnlySlot().size();
            if (AMONLYc == 0 && objGlobalMethods.IsEmptyString(creditPM)) ////this line is only for here
                AMONLYc++;
            listcurr = DAO_Jobs.getAllPostalsInPipDipByAMSlot();
            AMc = AMONLYc > 0 ? listcurr.size() : 0;
            if (AMc == 0) {
                listcurr = DAO_Jobs.getAllPostalsInPipDipByPMSlot();
                PMONLYc = listcurr.size();
                if (PMONLYc == 0 && objGlobalMethods.IsEmptyString(creditAOH))
                    PMONLYc++;
                PMc = PMONLYc > 0 ? listcurr.size() : 0;
                if (PMc == 0) {
                    listcurr = DAO_Jobs.getAllPostalsInPipDipByAOHSlot();
                    AOHc = listcurr.size();
                }
            }
            //listcurr = DAO_Jobs.getAllPostalsInPipDipBySlot(isAM ? 1 : isPM ? 2 : 4);
            if (listcurr != null && listcurr.size() > 0 && listcurr.contains(postal))
                return;

            cdAM = (AMc > 0) && !objGlobalMethods.IsEmptyString(creditAM) ? gson.fromJson(creditAM, credit.class) : null;
            cdPM = (AMc == 0 && PMc > 0) && !objGlobalMethods.IsEmptyString(creditPM) ? gson.fromJson(creditPM, credit.class) : null;
            cdAOH = (PMc == 0 && AOHc > 0) && !objGlobalMethods.IsEmptyString(creditAOH) ? gson.fromJson(creditAOH, credit.class) : null;
            cd = cdAM != null ? cdAM : cdPM != null ? cdPM : cdAOH != null ? cdAOH : null;

            if (cd == null)
                AMc = 1;
            if (cd == null)
                cd = new credit();

            credOpenedPostals = cd.credOpenedPostals;
            ManualQueuedPostalUsedList = cd.ManualQueuedPostalUsedList;
            if (ManualQueuedPostalUsedList == null)
                ManualQueuedPostalUsedList = new ArrayList<String>();

            ManualQueuedPostalJson = objGlobalMethods.getVrpRelatedSP_KeyValue2(vsp, "ManualQueuedPostalJson");
            if (!objGlobalMethods.IsEmptyString(ManualQueuedPostalJson))
                ManualQueuedPostalList = gson.fromJson(ManualQueuedPostalJson, new TypeToken<ArrayList<String>>() {
                }.getType());

            if (ManualQueuedPostalList != null && ManualQueuedPostalList.size() > 0 && ManualQueuedPostalList.contains(postal)) {
                for (int i = ManualQueuedPostalList.size() - 1; i >= 0; i--) {
                    if (ManualQueuedPostalList.get(i).equals(postal)) {
                        ManualQueuedPostalList.remove(i);
                        ManualQueuedPostalUsedList.add(postal);
                    }
                }
            }
            else if (credOpenedPostals != null && credOpenedPostals.size() > 0 && (credOpenedPostals.contains(postal))) {
                //listcurr = DAO_Jobs.getAllPostalsInPipDip();
                credUsedPostals = cd.credUsedPostals;
                if (credUsedPostals == null)
                    credUsedPostals = new ArrayList<String>();

                if (((listcurr != null && !listcurr.contains(postal)) || (listcurr == null))) {
                    credUsedPostals.add(postal);
                    credOpenedPostals.remove(postal);
                    cd.setCredUsedPostals(credUsedPostals);
                    cd.setCredOpenedPostals(credOpenedPostals);
                    cred_postalcountusedd = cd.cred_postalcountused;
                    cred_postalcountusedd++;
                    cd.setCred_postalcountused(cred_postalcountusedd);
                }
            } else {
                cd.addTotalNCUsedPostals(postal);
            }

            ManualQueuedPostalJson = ManualQueuedPostalList.size() > 0 ? gson.toJson(ManualQueuedPostalList) : "";
            cd.setManualQueuedPostalUsedList(ManualQueuedPostalUsedList);

            creditJson = gson.toJson(cd);


            if (AMc > 0) {
                String[] vrpspprefName = new String[]{"creditAM", "ManualQueuedPostalJson"};
                String[] vrpspprefvalue = new String[]{creditJson, ManualQueuedPostalJson};
                objGlobalMethods.setVrpSPMass(vsp, vrpspprefName, vrpspprefvalue);
            }
            if (PMc > 0) {
                String[] vrpspprefName = new String[]{"creditPM", "ManualQueuedPostalJson"};
                String[] vrpspprefvalue = new String[]{creditJson, ManualQueuedPostalJson};
                objGlobalMethods.setVrpSPMass(vsp, vrpspprefName, vrpspprefvalue);
            }
            if (AOHc > 0) {
                String[] vrpspprefName = new String[]{"creditAOH", "ManualQueuedPostalJson"};
                String[] vrpspprefvalue = new String[]{creditJson, ManualQueuedPostalJson};
                objGlobalMethods.setVrpSPMass(vsp, vrpspprefName, vrpspprefvalue);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler2 processedPostalCreditNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

}
