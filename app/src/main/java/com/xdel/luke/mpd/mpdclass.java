package com.xdel.luke.mpd;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.JobScheduler.Util;
import com.xdel.luke.JobScheduler.btLoggerJobService;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.MacDevicePair;
import com.xdel.luke.templogger.bttemploggerclass;
import com.xdel.luke.templogger.lukeTLregistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mpdclass {

    public MPD_Group[] MPDGroups;
    public String ARegID = null;
    String MPDGroupsJsonStr = null;

    public void setMPDGroups(MPD_Group[] AMPDGroups) {
        this.MPDGroups = AMPDGroups;
    }

    public void setARegID(String aARegID) {
        this.ARegID = aARegID;
    }

    public void processMPDGroupNew() {
        MPD_Group[]  mgs = null;
        MPD_Group mg = null;
        MPD_Group_Item[] items = null;
        MPD_Group_Item MPDGroupItem = null;
        MPD_Item MPDItem = null;
        List<String> macArray = null;
        List<String> TemperatureLoggerIDXArray = null;
        int btc = 0;
        int Jobs_AttachedCount = 0;
        int ActiveJobs_AttachedCount = 0;
        MacDevicePair savedmdp = null;
        MacDevicePair mdp = null;
        List<MacDevicePair> savedmdps = null;
        List<MacDevicePair> mdps = null;
        Gson gson = new Gson();
        boolean hasNewBTLogger = false;

        String savedmdpsJsonStr = "";
        String mdpsJsonStr = "";
        lukeTLregistry reg = null;
        ArrayList<lukeTLregistry> reglist = null;
        ArrayList<MPD_Item> btTLlist = null;
        String lukeTLregistryListJsonStr = "";
        try {
            if (MPDGroups != null && MPDGroups.length > 0) {
                mgs = MPDGroups;
            } else {
                MPDGroupsJsonStr = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDGroupsJsonStr");
                if (!objGlobalMethods.IsEmptyString(MPDGroupsJsonStr) && !MPDGroupsJsonStr.equals("[]")) {
                    mgs = gson.fromJson(MPDGroupsJsonStr, MPD_Group[].class);
                }
            }

            reglist = getlukeTLregistryList(objGlobalMethods.getBTLoggerRelatedSharedPreferences());

            if (mgs != null) {
                for (int i = 0; i <= mgs.length -1; i++) {
                    mg = mgs[i];
                    //Jobs_AttachedCount = mg.Jobs_Attached != null && mg.Jobs_Attached.length > 0 ? mg.Jobs_Attached.length : 0;
                    items = mg.Items;
                    if (items != null && items.length > 0) {
                        for (int x = 0; x <= items.length -1; x++) {
                            MPDGroupItem = items[x];
                            MPDItem = MPDGroupItem.MPD_Item;
                            if (MPDItem != null && (MPDItem.Device_Type == 21 || MPDItem.Device_Type == 22)) {
                                btc++;
                                mdp = new MacDevicePair(MPDItem.IDX, MPDItem.Device_Identifier, MPDItem.Device_Type, mg.Jobs_Attached);
                                if (mdps == null)
                                    mdps = new ArrayList<>();
                                mdps.add(mdp);

                                if (MPDItem.Device_Type == 22) {
                                    if (btTLlist == null)
                                        btTLlist = new ArrayList<>();
                                    btTLlist.add(MPDItem);
                                }
                            }
                        }
                    }
                }

                MPDGroupsJsonStr = gson.toJson(mgs);

                //prepare BTLogger Registry List
                if (btTLlist != null && btTLlist.size() > 0) {
                    //remove non-existent tl from reglist
                    boolean RegExistInBTTLList = false;
                    if (reglist != null && reglist.size() > 0) {
                        for (int t = reglist.size() -1; t >= 0; t--) {
                            RegExistInBTTLList = false;
                            reg = reglist.get(t);
                            for (int b = 0; b <= btTLlist.size() -1; b++) {
                                if (btTLlist.get(b).IDX == reg.MPDIDX) {
                                    RegExistInBTTLList = true;
                                    break;
                                }
                            }

                            if (!RegExistInBTTLList) {
                                reglist.remove(t);
                            }
                        }
                    }

                    if (reglist != null && reglist.size() > 0) {
                        for (int t = 0; t <= btTLlist.size() -1; t++) {
                            MPDItem = btTLlist.get(t);
                            if (!TLIDX_ExistInList(MPDItem.IDX, reglist)) {
                                reg = new lukeTLregistry(MPDItem.IDX, MPDItem.Device_Identifier, MPDItem.Serial_Number);
                                reglist.add(reg);
                            }
                        }

                        if (reglist != null && reglist.size() > 0) {
                            lukeTLregistryListJsonStr = gson.toJson(reglist);
                        }

                    } else {
                        reglist = new ArrayList<>();
                        for (int t = 0; t <= btTLlist.size() -1; t++) {
                            MPDItem = btTLlist.get(t);
                            reg = new lukeTLregistry(MPDItem.IDX, MPDItem.Device_Identifier, MPDItem.Serial_Number);
                            reglist.add(reg);
                        }
                        if (reglist != null && reglist.size() > 0) {
                            lukeTLregistryListJsonStr = gson.toJson(reglist);
                        }
                    }
                } else if (reglist != null && reglist.size() > 0) {
                    lukeTLregistryListJsonStr = "";
                }
                //prepare BTLogger Registry List

            }

            mdpsJsonStr = (mdps != null && mdps.size() > 0) ? gson. toJson(mdps) : "";

            String[] prefName = new String[]{"MPDGroupsJsonStr", "mdpsJsonStr"};
            String[] prefvalue = new String[]{MPDGroupsJsonStr, mdpsJsonStr};
            //objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);
            objGlobalMethods.setMpdSPMass(prefName, prefvalue);

            String[] btprefName = new String[]{"lukeTLregistryListJsonStr"};
            String[] btprefvalue = new String[]{lukeTLregistryListJsonStr};
            objGlobalMethods.setBTLoggerRelatedMass(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), btprefName, btprefvalue);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at mpdclass processMPDGroupNew. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public static ArrayList<lukeTLregistry> getlukeTLregistryList(SharedPreferences btp){
        String lukeTLregistryListJsonStr = "";
        ArrayList<lukeTLregistry> list = null;
        try {
            if (btp == null)
                btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();

            lukeTLregistryListJsonStr = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "lukeTLregistryListJsonStr");
            if (!objGlobalMethods.IsEmptyString(lukeTLregistryListJsonStr)) {
                Gson gson = new Gson();
                list = gson.fromJson(lukeTLregistryListJsonStr, new TypeToken<List<lukeTLregistry>>(){}.getType());
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at mpdclass getlukeTLregistryList. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return list;
        }
    }

    public boolean TLIDX_ExistInList(long idx, ArrayList<lukeTLregistry> l) {
        boolean asd = false;
        lukeTLregistry r = null;
        try {
             if (l != null && l.size() > 0) {
                 for (int i = 0; i <= l.size() -1; i++) {
                     r = l.get(i);
                     if (r.MPDIDX == idx) {
                         asd = true;
                         break;
                     }
                 }
             }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at mpdclass TLIDX_ExistInList. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public static boolean reqRegBTTL(SharedPreferences btp){
        boolean asd = false;
        ArrayList<lukeTLregistry> list = null;
        lukeTLregistry r = null;
        ArrayList<String> btNames = null;
        try {
            if (btp == null)
                btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();

            list = getlukeTLregistryList(btp);
            if (list != null && list.size() > 0) {
                for (int i = 0; i <= list.size() -1; i++) {
                    r = list.get(i);
                    if (r.registered == 0) {
                        if (btNames == null)
                            btNames = new ArrayList<>();
                        btNames.add(r.desc);
                    } else if (r.regtimestamplong > 0 && JobsHandler.isOneOrMoreDaysEarlier(r.regtimestamplong)) {
                        if (btNames == null)
                            btNames = new ArrayList<>();
                        btNames.add(r.desc);
                    }
                }

                asd = (btNames != null && btNames.size() > 0);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at mpdclass reqRegBTTL. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public static ArrayList<String> BTTL_Not_Reg_Yet(){
        ArrayList<lukeTLregistry> list = null;
        lukeTLregistry r = null;
        ArrayList<String> btNames = null;
        boolean asd = false;
        try {
            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            list = getlukeTLregistryList(btp);
            if (list != null && list.size() > 0) {
                for (int i = 0; i <= list.size() -1; i++) {
                    r = list.get(i);
                    if (r.registered == 0) {
                        if (btNames == null)
                            btNames = new ArrayList<>();
                        btNames.add(r.desc);
                    } else if (r.regtimestamplong > 0 && JobsHandler.isOneOrMoreDaysEarlier(r.regtimestamplong)) {
                        if (btNames == null)
                            btNames = new ArrayList<>();
                        btNames.add(r.desc);
                        list.get(i).setRegtimestamplong(0);
                        list.get(i).setRegistered(0);
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at mpdclass BTTL_Not_Reg_Yet. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return btNames;
        }
    }

    public static String promptRegBTTL_New() {
        String msg = "";
        ArrayList<lukeTLregistry> list = null;
        lukeTLregistry r = null;
        ArrayList<String> btNames = null;
        boolean resetlukeTLregistryListJson = false;
        String lukeTLregistryListJsonStr = "";
        try {
            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            boolean hasBTTL = !objGlobalMethods.IsEmptyString(objGlobalMethods.getBTLoggerRelatedPrefs(btp, "hasBTTL"));

            if (hasBTTL) {
                list = getlukeTLregistryList(btp);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i <= list.size() -1; i++) {
                        r = list.get(i);
                        if (r.registered == 0) {
                            if (btNames == null)
                                btNames = new ArrayList<>();
                            btNames.add(r.desc);
                        } else if (r.regtimestamplong > 0 && JobsHandler.isOneOrMoreDaysEarlier(r.regtimestamplong)) {
                            if (btNames == null)
                                btNames = new ArrayList<>();
                            btNames.add(r.desc);
                            resetlukeTLregistryListJson = true;
                            list.get(i).setRegtimestamplong(0);
                            list.get(i).setRegistered(0);
                        }
                    }

                    if (resetlukeTLregistryListJson) {
                        lukeTLregistryListJsonStr = new Gson().toJson(list);
                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "lukeTLregistryListJsonStr", lukeTLregistryListJsonStr);
                    }

                    if (btNames != null && btNames.size() > 0) {
                        String n = "";
                        for (int i = 0; i<=btNames.size() -1; i++) {
                            n += btNames.get(i) + "\n";
                        }
                        msg = n + "\n" + myApp.getAppContext().getString(R.string.temploggerregprompt);

                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "lastBTLScannedTimeLong", "");
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at mpdclass promptRegBTTL_New. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return msg;
        }
    }

    public static void promptRegBTTL() {
        ArrayList<lukeTLregistry> list = null;
        lukeTLregistry r = null;
        ArrayList<String> btNames = null;
        boolean resetlukeTLregistryListJson = false;
        String lukeTLregistryListJsonStr = "";
        try {
            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            boolean hasBTTL = !objGlobalMethods.IsEmptyString(objGlobalMethods.getBTLoggerRelatedPrefs(btp, "hasBTTL"));

            if (hasBTTL) {
                list = getlukeTLregistryList(btp);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i <= list.size() -1; i++) {
                        r = list.get(i);
                        if (r.registered == 0) {
                            if (btNames == null)
                                btNames = new ArrayList<>();
                            btNames.add(r.desc);
                        } else if (r.regtimestamplong > 0 && JobsHandler.isOneOrMoreDaysEarlier(r.regtimestamplong)) {
                            if (btNames == null)
                                btNames = new ArrayList<>();
                            btNames.add(r.desc);
                            resetlukeTLregistryListJson = true;
                            list.get(i).setRegtimestamplong(0);
                            list.get(i).setRegistered(0);
                        }
                    }

                    if (resetlukeTLregistryListJson) {
                        lukeTLregistryListJsonStr = new Gson().toJson(list);
                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "lukeTLregistryListJsonStr", lukeTLregistryListJsonStr);
                    }

                    if (btNames != null && btNames.size() > 0) {
                        String n = "";
                        for (int i = 0; i<=btNames.size() -1; i++) {
                            n += btNames.get(i) + "\n";
                        }
                        String msg = n + "\n" + myApp.getAppContext().getString(R.string.temploggerregprompt);

                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "lastBTLScannedTimeLong", "");
                        objGlobalMethods.showRegBTTL_DialogNew(msg, true);
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at mpdclass promptRegBTTL. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
