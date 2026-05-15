package com.xdel.luke;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.xdel.luke.ShpmtVerification.ShpmtV2Activity;
import com.xdel.luke.jobinfo.JobInfoLite;
import com.xdel.luke.objClasses.JobListGroupSortedSQLite_PostData;
import com.xdel.luke.objClasses.objAddressContact;
import com.xdel.luke.objClasses.objAddressStructure;
import com.xdel.luke.objClasses.objCheckedItems;
import com.xdel.luke.objClasses.objContactStructure;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobsHandler3 {

    public static void clearShpmtVSP(SharedPreferences ShpmtVSP) {
        try {
            String[] PrefName = new String[]{"verifyItemsV2JsonStr"};
            String[] PrefValue = new String[]{""};
            objGlobalMethods.setShpmtVSPMass(ShpmtVSP, PrefName, PrefValue);
            ShpmtV2Activity.verifyItemsV2 = null;
            ShpmtV2Activity.fromPage = null;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler3 clearShpmtV. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void clearVrpSP(){
        try {
            String[] PrefName = new String[]{"saveAmended4QuesJsonStr"};
            String[] PrefValue = new String[]{""};
            objGlobalMethods.setVrpSPMass2( PrefName, PrefValue);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler3 clearVrpSP. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void clearMedsColSP(SharedPreferences MedsColSP) {
        try {
            String[] PrefName = new String[]{"mChildCheckStates2PrefStr", "SQGroupScrollSavedIndex", "SQGroupScrollSavedPosition", "scanned"};
            String[] PrefValue = new String[]{"", "", "", ""};
            objGlobalMethods.setMedsColPrefsMass(MedsColSP, PrefName, PrefValue);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler3 clearMedsColSP. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void clearPassportSP(SharedPreferences PassportSP) {
        try {
            String[] PrefName = new String[]{"PPMainMode"};
            String[] PrefValue = new String[]{""};
            objGlobalMethods.setPassportSPMass(PassportSP, PrefName, PrefValue);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler3 clearPassportSP. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static void clearMpdSP() {
        try {
            String[] PrefName = new String[]{"MPDGroupIDX", "MPD_GroupStr", "MPD_GetGroup_ObjStr", "MPDScanMode", "MPDMainMode"};
            String[] PrefValue = new String[]{"", "", "", "", ""};
            objGlobalMethods.setMpdSPMass(PrefName, PrefValue);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler3 clearMpdSP. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static boolean isAM(){
        boolean asd = false;
        try {
            LocalTime now = LocalTime.now();
            LocalTime start = LocalTime.of(14, 0); // 2:00 PM

            asd = now.isBefore(start);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler3 isAM. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public static boolean isPM(){
        boolean asd = false;
        try {
            LocalTime now = LocalTime.now();
            LocalTime start = LocalTime.of(14, 0); // 2:00 PM
            LocalTime end = LocalTime.of(19, 0);   // 7:00 PM

            asd = !now.isBefore(start) && now.isBefore(end);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler3 isPM. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public static boolean isAOH(){
        boolean asd = false;
        try {
            LocalTime now = LocalTime.now();
            LocalTime start = LocalTime.of(19, 0); // 7:00 PM

            asd = !now.isBefore(start);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler3 isAOH. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public static objAddressContact getDelAddressContact(objJobInfo ji) {
        objAddressContact ac = null;
        objAddressStructure AddressStructure = null;
        objContactStructure contactStructure = null;
        boolean isZoneTelAndHasAuthRecBool = false;
        try {
            isZoneTelAndHasAuthRecBool = JobsHandler.isZoneTelAndHasAuthRec(ji);
            if (ji.DLRedirectedLocation != null) {
                AddressStructure = !isZoneTelAndHasAuthRecBool ? ji.DLRedirectedLocation : ji.DLLocation;
                contactStructure = AddressStructure.Contacts[0];
            } else if (ji.DLLocation != null) {
                AddressStructure = ji.DLLocation;
                contactStructure = AddressStructure.Contacts[0];
            }
            ac = new objAddressContact(AddressStructure, contactStructure);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsHandler3 getDelAddressContact. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return ac;
        }
    }

    public static ArrayList<ArrayList<String>> returnAllUnitsArrayList(String DelUnit_original) {
        ArrayList<ArrayList<String>> lm = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> listc = new ArrayList<>();
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
                            firstunitCombined = JobsHandler2.getDelUnit_combine(firstunit);
                            list.add(firstunit);
                            listc.add(firstunitCombined);
                            continue;
                        }
                        reservedCharCount = firstunit.length() - v.length();
                        newunit = firstunit.substring(0, reservedCharCount) + v;
                        if (!list.contains(newunit)) {
                            newunitCombined = JobsHandler2.getDelUnit_combine(newunit);
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
            objGlobalMethods.writeToFile("Exception at JobsHandler3.returnAllUnitsArrayList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return lm;
        }
    }

    public static ArrayList<String> returnALlValidSGMobileNumArrayList(String tel) {
        ArrayList<String> vl = new ArrayList<>();
        boolean asd = false;
        String newtel = "";
        String newtelwExt = "";
        String firsttel = "";
        int reservedTelCharCount = 0;
        try {
            tel = tel.replaceAll("\\s+", ""); // replace all spaces to empty string
            String[] array = tel.split("\\s*,\\s*");
            if (array != null && array.length > 0) {
                for (int i = 0; i <= array.length - 1; i++) {
                    newtelwExt = "";
                    newtel = array[i];
                    asd = objGlobalMethods.validSGMobileNum(newtel);

                    if (asd) {
                        newtel = objGlobalMethods.GetPhone(newtel);
                        if (i > 0 && asd && newtel.length() == 8 && !vl.contains(newtel)) {
                            vl.add(newtel);
                        } else if (i > 0 && asd && newtel.length() > 0 && newtel.length() < 8 && !firsttel.isEmpty() && !firsttel.equals("")) {
                            reservedTelCharCount = firsttel.length() - newtel.length();
                            newtelwExt = firsttel.substring(0, reservedTelCharCount) + newtel;
                            if (!vl.contains(newtelwExt)) {
                                vl.add(newtelwExt);
                            }
                            //vl.add(newtelwExt);
                        } else if (i == 0 && asd) {
                            firsttel = newtel;
                            vl.add(newtel);
                        }
                    }
                }
            }

        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at objGlobalMethods returnALlValidSGMobileNumArrayList. Exception: " + s, "", "", "", "");
        } finally {
            return vl;
        }
    }

    public static void processCheckedItemsBeforeSubmitSQLiteNew(JobListGroupSortedSQLite_PostData jpd, boolean Add, SharedPreferences GcmPreferences) {
        Gson gson = new Gson();
        objCheckedItems[] objCheckedItemsAL = null;
        List<objCheckedItems> objCheckedItemsList = null;
        objCheckedItems objCheckedItemsobj = null;
        String[] CheckedItemsIDX = null;
        List<String> CheckedItemsIDXList = null;
        String checkedItemsIDXsingle = "";
        Boolean exist = false;
        String action = "";

        try {
            String objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
            String objCheckedItemsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX);
            int catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);

            if (!objCheckedItemsJsonStr.equals("") && !objCheckedItemsIDXJsonStr.equals("")) {
                objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                CheckedItemsIDX = gson.fromJson(objCheckedItemsIDXJsonStr, String[].class);

                if (objCheckedItemsAL.length > 0)
                    objCheckedItemsList = new ArrayList<objCheckedItems>(Arrays.asList(objCheckedItemsAL));

                if (CheckedItemsIDX.length > 0)
                    CheckedItemsIDXList = new ArrayList<String>(Arrays.asList(CheckedItemsIDX));

                if (Add) {
                    //Add
                    if (objCheckedItemsList != null && objCheckedItemsList.size() > 0) {
                        for (int i = 0; i <= objCheckedItemsList.size() - 1; i++) {
                            if (objCheckedItemsList.get(i).JobsIDX.equals(jpd.JobsIDX)) {
                                exist = true;
                                break;
                            }
                        }

                        if (!exist) {
                            if (catType == 0) {
                                //PIP, set to PU statusidx 49
                                objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 49, jpd.JobInfoJsonStr, false, jpd.ji, jpd.allowedSelection);
                            } else if (catType == 1) {
                                //pu, set to DIP statusidx 104
                                objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 104, jpd.JobInfoJsonStr, false, jpd.ji, jpd.allowedSelection);
                            } else if (catType == 2) {
                                //dip, set to OKP statusidx 135
                                if (jpd.IsPartOne) {
                                    objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 135, jpd.JobInfoJsonStr, true, jpd.ji, jpd.allowedSelection);
                                } else {
                                    objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 135, jpd.JobInfoJsonStr, false, jpd.ji, jpd.allowedSelection);
                                }
                            }

                            objGlobalMethods.writeToFile(String.valueOf(jpd.JobsIDX) + " selected at JobsListGroupActivity for " + catType, "", "", "", "");
                            objCheckedItemsList.add(objCheckedItemsobj);
                            objCheckedItemsAL = objCheckedItemsList.toArray(new objCheckedItems[objCheckedItemsList.size()]);
                            objCheckedItemsJsonStr = gson.toJson(objCheckedItemsAL);

                            CheckedItemsIDXList.add(String.valueOf(jpd.JobsIDX));
                            CheckedItemsIDX = CheckedItemsIDXList.toArray(new String[CheckedItemsIDXList.size()]);
                            objCheckedItemsIDXJsonStr = gson.toJson(CheckedItemsIDX);

                            String ScannedCodeStrsPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr");
                            String[] ScannedCodeStrs = gson.fromJson(ScannedCodeStrsPrefStr, String[].class);
                            List<String> ScannedCodeStrsList = null;

                            ScannedCodeStrsList = ScannedCodeStrs != null ? new ArrayList<String>(Arrays.asList(ScannedCodeStrs)) : new ArrayList<String>();
                            ScannedCodeStrsList.add(String.valueOf(jpd.JobsIDX));
                            ScannedCodeStrs = ScannedCodeStrsList.toArray(new String[ScannedCodeStrsList.size()]);
                            ScannedCodeStrsPrefStr = gson.toJson(ScannedCodeStrs);

                            String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr"};
                            String[] prefvalue = new String[]{objCheckedItemsJsonStr, objCheckedItemsIDXJsonStr, ScannedCodeStrsPrefStr};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                        }
                    } else {
                        if (catType == 0) {
                            //PIP, set to PU statusidx 49
                            objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 49, jpd.JobInfoJsonStr, false, jpd.ji, jpd.allowedSelection);
                        } else if (catType == 1) {
                            //pu, set to DIP statusidx 104
                            objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 104, jpd.JobInfoJsonStr, false, jpd.ji, jpd.allowedSelection);
                        } else if (catType == 2) {
                            //dip, set to OKP statusidx 135
                            if (jpd.IsPartOne) {
                                objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 135, jpd.JobInfoJsonStr, true, jpd.ji, jpd.allowedSelection);
                            } else {
                                objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 135, jpd.JobInfoJsonStr, false, jpd.ji, jpd.allowedSelection);
                            }

                        }
                        objGlobalMethods.writeToFile(String.valueOf(jpd.JobsIDX) + " selected at JobsListGroupActivity for " + catType, "", "", "", "");
                        objCheckedItemsAL = new objCheckedItems[]{objCheckedItemsobj};
                        objCheckedItemsJsonStr = gson.toJson(objCheckedItemsAL);

                        CheckedItemsIDX = new String[]{String.valueOf(jpd.JobsIDX)};
                        objCheckedItemsIDXJsonStr = gson.toJson(CheckedItemsIDX);

                        String ScannedCodeStrsPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr");
                        String[] ScannedCodeStrs = gson.fromJson(ScannedCodeStrsPrefStr, String[].class);
                        List<String> ScannedCodeStrsList = null;

                        ScannedCodeStrsList = ScannedCodeStrs != null ? new ArrayList<String>(Arrays.asList(ScannedCodeStrs)) : new ArrayList<String>();
                        ScannedCodeStrsList.add(String.valueOf(jpd.JobsIDX));
                        ScannedCodeStrs = ScannedCodeStrsList.toArray(new String[ScannedCodeStrsList.size()]);
                        ScannedCodeStrsPrefStr = gson.toJson(ScannedCodeStrs);

                        String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr"};
                        String[] prefvalue = new String[]{objCheckedItemsJsonStr, objCheckedItemsIDXJsonStr, ScannedCodeStrsPrefStr};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    }
                } else {
                    //remove
                    boolean hasNotAllowedSelected = false;
                    if (objCheckedItemsList != null && objCheckedItemsList.size() > 0) {
                        for (int i = 0; i <= objCheckedItemsList.size() - 1; i++) {
                            if (objCheckedItemsList.get(i).JobsIDX.equals(jpd.JobsIDX)) {
                                objCheckedItemsList.remove(i);
                                break;
                            }
                        }

                        for (int i = 0; i <= CheckedItemsIDXList.size() - 1; i++) {
                            if (CheckedItemsIDXList.get(i).equals(String.valueOf(jpd.JobsIDX))) {
                                CheckedItemsIDXList.remove(i);
                                break;
                            }
                        }

                        if (catType == 2) {
                            for (int i = 0; i <= objCheckedItemsList.size() - 1; i++) {
                                if (!objCheckedItemsList.get(i).allowedSelection) {
                                    hasNotAllowedSelected = true;
                                    break;
                                }
                            }
                        }

                        objGlobalMethods.writeToFile(String.valueOf(jpd.JobsIDX) + " removed at JobsListGroupActivity for " + catType, "", "", "", "");
                        objCheckedItemsAL = objCheckedItemsList.toArray(new objCheckedItems[objCheckedItemsList.size()]);
                        objCheckedItemsJsonStr = gson.toJson(objCheckedItemsAL);
                        if (objCheckedItemsJsonStr.equals("[]") || objGlobalMethods.IsEmptyString(objCheckedItemsJsonStr))
                            objCheckedItemsJsonStr = "";

                        CheckedItemsIDX = CheckedItemsIDXList.toArray(new String[CheckedItemsIDXList.size()]);
                        objCheckedItemsIDXJsonStr = gson.toJson(CheckedItemsIDX);
                        if (objCheckedItemsIDXJsonStr.equals("[]") || objGlobalMethods.IsEmptyString(objCheckedItemsIDXJsonStr))
                            objCheckedItemsIDXJsonStr = "";

                        String ScannedCodeStrsPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr");
                        String[] ScannedCodeStrs = gson.fromJson(ScannedCodeStrsPrefStr, String[].class);
                        List<String> ScannedCodeStrsList = null;
                        if (ScannedCodeStrs != null) {
                            ScannedCodeStrsList = new ArrayList<String>(Arrays.asList(ScannedCodeStrs));
                            for (int i = 0; i <= ScannedCodeStrsList.size() - 1; i++) {
                                if (ScannedCodeStrsList.get(i).equals(String.valueOf(jpd.JobsIDX))) {
                                    ScannedCodeStrsList.remove(i);
                                    break;
                                }
                            }
                            ScannedCodeStrs = ScannedCodeStrsList.toArray(new String[ScannedCodeStrsList.size()]);
                            ScannedCodeStrsPrefStr = gson.toJson(ScannedCodeStrs);
                            if (ScannedCodeStrsPrefStr.equals("[]") || objGlobalMethods.IsEmptyString(ScannedCodeStrsPrefStr))
                                ScannedCodeStrsPrefStr = "";

                        } else {
                            ScannedCodeStrsPrefStr = "";
                        }

                        String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr"};
                        String[] prefvalue = new String[]{objCheckedItemsJsonStr, objCheckedItemsIDXJsonStr, ScannedCodeStrsPrefStr};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    } else {
                        //do nothing
                        /*objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName, "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr", "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "objMultiSelectInitialCriteriaJsonStr", "");*/
                        String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr"};
                        String[] prefvalue = new String[]{"", "", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    }
                }

            } else {
                if (Add) {
                    //Add
                    if (catType == 0) {
                        //PIP, set to PU statusidx 49
                        objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 49, jpd.JobInfoJsonStr, false, jpd.ji, jpd.allowedSelection);
                    } else if (catType == 1) {
                        //pu, set to DIP statusidx 104
                        objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 104, jpd.JobInfoJsonStr, false, jpd.ji, jpd.allowedSelection);
                    } else if (catType == 2) {
                        //dip, set to OKP statusidx 135
                        if (jpd.IsPartOne) {
                            objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 135, jpd.JobInfoJsonStr, true, jpd.ji, jpd.allowedSelection);
                        } else {
                            objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 135, jpd.JobInfoJsonStr, false, jpd.ji, jpd.allowedSelection);
                        }

                    } else if (catType == 3) {
                        objCheckedItemsobj = new objCheckedItems(catType, jpd.JobsIDX, jpd.CNNO, 135, jpd.JobInfoJsonStr, false, jpd.ji, jpd.allowedSelection);
                    }
                    objGlobalMethods.writeToFile(String.valueOf(jpd.JobsIDX) + " selected at JobsListGroupActivity for " + catType, "", "", "", "");
                    objCheckedItemsAL = new objCheckedItems[]{objCheckedItemsobj};
                    objCheckedItemsJsonStr = gson.toJson(objCheckedItemsAL);

                    CheckedItemsIDX = new String[]{String.valueOf(jpd.JobsIDX)};
                    objCheckedItemsIDXJsonStr = gson.toJson(CheckedItemsIDX);

                    String ScannedCodeStrsPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr");
                    String[] ScannedCodeStrs = gson.fromJson(ScannedCodeStrsPrefStr, String[].class);
                    List<String> ScannedCodeStrsList = null;

                    ScannedCodeStrsList = ScannedCodeStrs != null ? new ArrayList<String>(Arrays.asList(ScannedCodeStrs)) : new ArrayList<String>();
                    ScannedCodeStrsList.add(String.valueOf(jpd.JobsIDX));
                    ScannedCodeStrs = ScannedCodeStrsList.toArray(new String[ScannedCodeStrsList.size()]);
                    ScannedCodeStrsPrefStr = gson.toJson(ScannedCodeStrs);

                    String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr"};
                    String[] prefvalue = new String[]{objCheckedItemsJsonStr, objCheckedItemsIDXJsonStr, ScannedCodeStrsPrefStr};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                } else {
                    //remove
                    //do nothing
                    /*objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName, "");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr", "");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "objMultiSelectInitialCriteriaJsonStr", "");*/
                    String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr"};
                    String[] prefvalue = new String[]{"", "", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler3 processCheckedItemsBeforeSubmitSQLiteNew. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static boolean JobFound(JobInfoLite ji, String val, boolean DOonly, int mode) {
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
                isMatchingDObool = JobsHandler2.isMatchingDO(ji, val);
            } else {
                isMatchingJobsIDXbool = JobsHandler2.isMatchingJobsIDX(ji, val);
                isMatchingJobsJCbool = JobsHandler2.isMatchingJobsJC(ji, val);
                isMatchingCNbool = JobsHandler2.isMatchingCN(ji, val);
                isMatchingSMbool = JobsHandler2.isMatchingSM(ji, val);
                isMatchingDObool = JobsHandler2.isMatchingDO(ji, val);
                isMatchingArticleTagbool = JobsHandler2.isMatchingArticleTag(ji, val);
                isMatchingJobNobool = JobsHandler2.isMatchingJobNo(ji, val);
                isMatchingCNFieldbool = JobsHandler2.isMatchingCNField(ji, val);
            }

            asd = isMatchingJobsIDXbool || isMatchingJobsJCbool || isMatchingCNbool || isMatchingSMbool || isMatchingDObool || isMatchingArticleTagbool || isMatchingJobNobool || isMatchingCNFieldbool || isMatchingLockerParcelID;
            if (mode == 0) {
                objGlobalMethods.writeToFile(!asd ? "JobFound is false. Scanned value: " + val : "JobFound is true. Scanned value: " + val, "", "", "", "");
            } else {
                objGlobalMethods.writeToFile(!asd ? "JobFound is false. Entered value: " + val : "JobFound is true. Entered value: " + val, "", "", "", "");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsHandler3 JobFound. Exception: " + e.getMessage().toString(), "", "", "", "");
            asd = true;
        } finally {
            return asd;
        }
    }


}
