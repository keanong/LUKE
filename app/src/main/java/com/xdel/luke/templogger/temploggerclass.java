package com.xdel.luke.templogger;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.data.BleScanState;
import com.xdel.luke.AddressListNewActivity;
import com.xdel.luke.AddressListRoutingActivity;
import com.xdel.luke.BottomSheet.tempLoggerEntryDialog;
import com.xdel.luke.BulkPickUpActivity;
import com.xdel.luke.Caging.CageInOutActivity;
import com.xdel.luke.Caging.CageScanActivity;
import com.xdel.luke.CheckListActivity;
import com.xdel.luke.CreditCardActivity;
import com.xdel.luke.CustomGallery;
import com.xdel.luke.CustomGallery2Activity;
import com.xdel.luke.CustomGalleryActivity;
import com.xdel.luke.DOCOD.CODupdate2;
import com.xdel.luke.DOinvCheckActivity;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.DriverPointMain_Activity;
import com.xdel.luke.DriverPointSummaryActivity;
import com.xdel.luke.HRM.ChangePwdActivity;
import com.xdel.luke.HRM.ProfileActivity;
import com.xdel.luke.HRM.StaffPassActivity;
import com.xdel.luke.HRM_GC_Schedule.GCScheduleActivity;
import com.xdel.luke.HRM_Leave.LeaveActivity;
import com.xdel.luke.HRM_Leave.LeaveApplicationActivity;
import com.xdel.luke.HandTakeOver.HandoverActivity;
import com.xdel.luke.HandTakeOver.TakeoverActivity;
import com.xdel.luke.ItemGroupCheck;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.JobsListGroupActivity;
import com.xdel.luke.JobsheetActivity;
import com.xdel.luke.JobsheetSubmitActivity;
import com.xdel.luke.Locker.AuthActivity;
import com.xdel.luke.Locker.LockerProfileActivity;
import com.xdel.luke.Locker.ShipmentActivity;
import com.xdel.luke.MPD_CheckOutActivity;
import com.xdel.luke.MainCODActivity;
import com.xdel.luke.MapsActivity;
import com.xdel.luke.MapsAllActivity;
import com.xdel.luke.MassUpdateOKPwP2;
import com.xdel.luke.Messaging;
import com.xdel.luke.Messaginglist;
import com.xdel.luke.OtpActivity;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.Rooting.RootGuard;
import com.xdel.luke.SecuritySealActivity;
import com.xdel.luke.SignatureNameActivity;
import com.xdel.luke.UpdateCOD_List;
import com.xdel.luke.ViewImageActivity;
import com.xdel.luke.Voip.CallActivity;
import com.xdel.luke.WebService;
import com.xdel.luke.inventory.InventoryMainActivity;
import com.xdel.luke.inventory.InventorySummaryActivity;
import com.xdel.luke.mpd.MPDMainActivity;
import com.xdel.luke.mpd.MPD_Group;
import com.xdel.luke.mpd.mpdclass;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objMPD_Info;
import com.xdel.luke.setDeliveredMassStatusActivity;
import com.xdel.luke.setDeliveredStatusActivity;
import com.xdel.luke.setDunningDeliveredStatusActivity;
import com.xdel.luke.setFailStatusActivity;
import com.xdel.luke.sqlite.DAO_Jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class temploggerclass {
    public static CountDownTimer Timer;

    public static JobTemperaturePayload getJobTemperaturePayload(long JobsIDX) {
        JobTemperaturePayload pl = null;
        JobTemperaturePayload[] res = null;
        String L2_GetJobTemperaturesJsonStr = "";
        SharedPreferences g = objGlobalMethods.getGcmPreferences();

        try {
            L2_GetJobTemperaturesJsonStr = objGlobalMethods.getGCMPrefs(g, "L2_GetJobTemperaturesJsonStr");
            if (!objGlobalMethods.IsEmptyString(L2_GetJobTemperaturesJsonStr)) {
                Gson gson = new Gson();
                res = gson.fromJson(L2_GetJobTemperaturesJsonStr, JobTemperaturePayload[].class);
                if (res != null && res.length > 0) {
                    for (int i = 0; i <= res.length - 1; i++) {
                        if (res[i].AJobsIDX == JobsIDX) {
                            pl = res[i];
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at temploggerclass getJobTemperaturePayload. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return pl;
        }
    }

    public static boolean jobsidxExist(long[] Jobs_Attached, long JobsIDX) {
        boolean asd = false;
        try {
            if (Jobs_Attached != null && Jobs_Attached.length > 0) {
                for (int i = 0; i <= Jobs_Attached.length - 1; i++) {
                    if (Jobs_Attached[i] == JobsIDX) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at temploggerclass jobsidxExist. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static objTemp getLatestRecordedTempObjNew2(SharedPreferences g, long JobsIDX) {
        objTemp ot = null;
        ArrayList<TemperaturePayload> tpll = null;
        TemperaturePayload tpl = null;
        List<MacDevicePair> mdps = null;
        MacDevicePair mdp = null;

        if (g == null)
            g = objGlobalMethods.getGcmPreferences();

        try {
            //String mdpsJsonStr = objGlobalMethods.getGCMPrefs(g, "mdpsJsonStr");
            String mdpsJsonStr = objGlobalMethods.getMPDRelatedSP_KeyValue("mdpsJsonStr");
            if (!objGlobalMethods.IsEmptyString(mdpsJsonStr) && !mdpsJsonStr.equals("[]")) {
                Gson gson = new Gson();
                mdps = gson.fromJson(mdpsJsonStr, new TypeToken<List<MacDevicePair>>() {
                }.getType());
                if (mdps != null && mdps.size() > 0) {
                    String tplJsonStr = objGlobalMethods.getBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "tplJsonStr");
                    if (!objGlobalMethods.IsEmptyString(tplJsonStr) && !tplJsonStr.equals("")) {
                        tpl = gson.fromJson(tplJsonStr, TemperaturePayload.class);
                    }

                    asd:
                    for (int i = 0; i <= mdps.size() - 1; i++) {
                        mdp = mdps.get(i);

                        if (mdp.Jobs_Attached != null && mdp.Jobs_Attached.length > 0 && jobsidxExist(mdp.Jobs_Attached, JobsIDX)) {
                            if (tpl != null) {
                                if (tpl.MPDIDX == mdp.MPDIDX) {

                                    if (!objGlobalMethods.IsEmptyString(tpl.Timestamp) && !JobsHandler.moreThanXminutesZerofied(Long.parseLong(tpl.Timestamp), 10)) {
                                        ot = new objTemp();
                                        ot.setManualAuto("1");
                                        ot.setTempLastRecorded(String.valueOf(tpl.Temperature));
                                        ot.setTempLastRecordedTimeStampTSstr(tpl.Timestamp);
                                        ot.setBatteryLvl(tpl.BatteryLevel);
                                        ot.setMPDIDX(tpl.MPDIDX);
                                        break asd;
                                    }

                                    /*ot = new objTemp();
                                    ot.setManualAuto("1");
                                    ot.setTempLastRecorded(String.valueOf(tpl.Temperature));
                                    ot.setTempLastRecordedTimeStampTSstr(tpl.Timestamp);
                                    ot.setBatteryLvl(tpl.BatteryLevel);
                                    ot.setMPDIDX(tpl.MPDIDX);
                                    break asd;*/
                                }
                                if (ot == null) {
                                    ot = new objTemp();
                                    ot.setDevice_Type(mdp.Device_Type);
                                    String JobTemperaturePayloadByJobsIDXJsonStr = objGlobalMethods.getGCMPrefs(g, "JobTemperaturePayloadByJobsIDXJsonStr");
                                    if (!objGlobalMethods.IsEmptyString(JobTemperaturePayloadByJobsIDXJsonStr)) {
                                        JobTemperaturePayload plByJobsIDX = new Gson().fromJson(JobTemperaturePayloadByJobsIDXJsonStr, JobTemperaturePayload.class);
                                        if (plByJobsIDX != null && plByJobsIDX.AJobsIDX == JobsIDX && plByJobsIDX.TemperatureLogs != null && plByJobsIDX.TemperatureLogs.length > 0) {
                                            ot.setTempLastRecorded(String.valueOf(plByJobsIDX.TemperatureLogs[0].Temperature));
                                            ot.setTempLastRecordedTimeStampTSstr(plByJobsIDX.TemperatureLogs[0].Timestamp);
                                            ot.setItem(plByJobsIDX.TemperatureLogs[0].item);
                                        }
                                    }
                                    break asd;
                                }
                            } else {
                                ot = new objTemp();
                                ot.setDevice_Type(mdp.Device_Type);
                                String JobTemperaturePayloadByJobsIDXJsonStr = objGlobalMethods.getGCMPrefs(g, "JobTemperaturePayloadByJobsIDXJsonStr");
                                if (!objGlobalMethods.IsEmptyString(JobTemperaturePayloadByJobsIDXJsonStr)) {
                                    JobTemperaturePayload plByJobsIDX = new Gson().fromJson(JobTemperaturePayloadByJobsIDXJsonStr, JobTemperaturePayload.class);
                                    if (plByJobsIDX != null && plByJobsIDX.AJobsIDX == JobsIDX && plByJobsIDX.TemperatureLogs != null && plByJobsIDX.TemperatureLogs.length > 0) {
                                        ot.setTempLastRecorded(String.valueOf(plByJobsIDX.TemperatureLogs[0].Temperature));
                                        ot.setTempLastRecordedTimeStampTSstr(plByJobsIDX.TemperatureLogs[0].Timestamp);
                                        ot.setItem(plByJobsIDX.TemperatureLogs[0].item);
                                    }
                                }
                                break asd;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at temploggerclass getLatestRecordedTempObjNew2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return ot;
        }
    }

    public static void decideToStartTempTimer(SharedPreferences btp, String logtxt) {
        try {
            if (btp == null)
                btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();

            boolean hasBTTL = !objGlobalMethods.IsEmptyString(objGlobalMethods.getBTLoggerRelatedPrefs(btp, "hasBTTL"));
            boolean reqRegBTTL = false;
            if (hasBTTL)
                reqRegBTTL = mpdclass.reqRegBTTL(btp);

            if (!reqRegBTTL) {
                //objGlobalMethods.writeToFile("TempTimer started at " + logtxt, "", "", "", "");
                temploggerclass.disposeTimer();
                temploggerclass.startTimer(0);
            } else {
                //objGlobalMethods.writeToFile("TempTimer not started at " + logtxt, "", "", "", "");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at temploggerclass decideToStartTempTimer. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static void startTimer(long fastLeftTimeMS) {
        //R6: no BLE timer, and no re-arming of it, while RootGuard blocks this process
        if (RootGuard.blockOperation("temploggerclass.startTimer", null)) {
            disposeTimer();
            return;
        }
        long cTimeMS = new Date().getTime();
        long leftTimeMS = 960000; //purposely put 16min instead of 15min to cater 1min buffer for ScreenOnOffReceiver side to scan BTTL
        //long leftTimeMS = 180000; //purposely put 3min instead of 5min to cater 1min buffer for ScreenOnOffReceiver side to scan BTTL
        try {
            if (Timer == null) {
                SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                if (fastLeftTimeMS > 0) {
                    leftTimeMS = fastLeftTimeMS;
                } else {
                    String lastTimerRanTimeLong = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "lastTimerRanTimeLong");
                    if (!objGlobalMethods.IsEmptyString(lastTimerRanTimeLong))
                        leftTimeMS = 960000 - (cTimeMS - Long.parseLong(lastTimerRanTimeLong)); //16min after last scanned time
                }

                if (leftTimeMS < 0)
                    leftTimeMS = 60000;

                /*if (!objGlobalMethods.IsEmptyString(lastTimerRanTimeLong))
                    leftTimeMS = 180000 - (cTimeMS - Long.parseLong(lastTimerRanTimeLong)); //16min after last scanned time

                if (leftTimeMS < 0)
                    leftTimeMS = 180000;*/

                Timer = new CountDownTimer(leftTimeMS, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //do nothing
                    }

                    public void onFinish() {
                        boolean morethan15min = true;
                        final Context ctx = myApp.getAppContext();
                        boolean isScreenDisplayOn = objGlobalMethods.isScreenDisplayOn(ctx);
                        try {
                            if (isScreenDisplayOn) {
                                Boolean BTEnabled = temploggerclass.isBTEnabled();

                                if (BTEnabled != null && BTEnabled) {
                                    SharedPreferences g = objGlobalMethods.getGcmPreferences();
                                    SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                                    String mdpsJsonStr = objGlobalMethods.getMPDRelatedSP_KeyValue("mdpsJsonStr");

                                    if (!objGlobalMethods.IsEmptyString(mdpsJsonStr) && !mdpsJsonStr.equals("[]")) {
                                        boolean hasBTTL = !objGlobalMethods.IsEmptyString(objGlobalMethods.getBTLoggerRelatedPrefs(btp, "hasBTTL"));
                                        if (hasBTTL) {
                                            boolean reqRegBTTL = mpdclass.reqRegBTTL(btp);
                                            String lastBTLScannedTimeLong = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "lastBTLScannedTimeLong");
                                            if (!objGlobalMethods.IsEmptyString(lastBTLScannedTimeLong))
                                                morethan15min = JobsHandler.moreThanXminutesZerofied(Long.parseLong(lastBTLScannedTimeLong), 15);

                                            if (morethan15min && !reqRegBTTL) {
                                                String ARegID = objGlobalMethods.getRegistrationIdOnly();
                                                String bttemploggerclassStarted = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "bttemploggerclassStarted");
                                                String bttemploggerStandaloneclassStarted = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "bttemploggerStandaloneclassStarted");
                                                boolean emptyARegID = objGlobalMethods.IsEmptyString(ARegID);
                                                boolean emptybttemploggerclassStarted = objGlobalMethods.IsEmptyString(bttemploggerclassStarted);
                                                boolean emptybttemploggerStandaloneclassStarted = objGlobalMethods.IsEmptyString(bttemploggerStandaloneclassStarted);

                                                /*objGlobalMethods.writeToFile("temploggerclass startTimer onFinish Scanning BTL. >= 15min !reqRegBTTL.\n" +
                                                                "ARegiD " + (emptyARegID ? "Empty." : "!Empty.")
                                                                + " bttemploggerclassStarted " + (emptybttemploggerclassStarted ? "Empty." : "!Empty.")
                                                                + " bttemploggerStandaloneclassStarted " + (emptybttemploggerStandaloneclassStarted ? "Empty." : "!Empty.")
                                                        , "", "", "", "");*/
                                                ////on purpose

                                                if (!emptybttemploggerclassStarted && emptybttemploggerStandaloneclassStarted) {
                                                    objGlobalMethods.setBTLoggerRelatedPrefs(btp, "bttemploggerclassStarted", "");
                                                /*BleManager.getInstance().disconnectAllDevice();
                                                BleManager.getInstance().destroy();
                                                objGlobalMethods.writeToFile("temploggerclass startTimer onFinish BleManager.getInstance().destroy()","","","","");*/
                                                    emptybttemploggerclassStarted = true;
                                                }

                                                if (!objGlobalMethods.IsEmptyString(ARegID) && emptybttemploggerclassStarted && emptybttemploggerStandaloneclassStarted) {
                                                    if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
                                                        BleManager.getInstance().cancelScan();
                                                        //objGlobalMethods.writeToFile("temploggerclass startTimer onFinish BleManager.getInstance().cancelScan() due BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING", "", "", "", "");
                                                        ////on purpose
                                                    }

                                                    BleManager.getInstance().disconnectAllDevice();
                                                    BleManager.getInstance().destroy();
                                                    BleManager.getInstance().init(myApp.getInstance());
                                                    BleManager.getInstance()
                                                            .enableLog(true)
                                                            .setReConnectCount(3, 5000)
                                                            .setConnectOverTime(20000)
                                                            .setOperateTimeout(5000);
                                                    //objGlobalMethods.writeToFile("BleManager initiated at temploggerclass startTimer onFinish", "", "", "", "");
                                                    ////on purpose

                                                    btReport.IsRunning = null;

                                                    //objGlobalMethods.writeToFile("temploggerclass startTimer onFinish bttemploggerclass scan started. disposeTimer() startTimer()", "", "", "", "");
                                                    ////on purpose
                                                    disposeTimer();
                                                    long currTimeMS = new Date().getTime();
                                                    objGlobalMethods.setBTLoggerRelatedPrefs(btp, "lastTimerRanTimeLong", String.valueOf(currTimeMS));
                                                    startTimer(0);

                                                    bttemploggerclass btc = new bttemploggerclass();
                                                    Gson gson = new Gson();
                                                    List<MacDevicePair> mdps = gson.fromJson(mdpsJsonStr, new TypeToken<List<MacDevicePair>>() {
                                                    }.getType());
                                                    btc.setMacDevicePairs(mdps);
                                                    btc.setARegID(ARegID);
                                                    btc.scan();
                                                }
                                            } else {
                                                disposeTimer();
                                                long currTimeMS = new Date().getTime();
                                                objGlobalMethods.setBTLoggerRelatedPrefs(btp, "lastTimerRanTimeLong", String.valueOf(currTimeMS));
                                                startTimer(0);
                                                /*objGlobalMethods.writeToFile("temploggerclass startTimer onFinish. Not scanning BTL."
                                                        + (!morethan15min ? " < 15min." : " >= 15min.") + (!reqRegBTTL ? " !reqRegBTTL" : " reqRegBTTL")
                                                        + " disposeTimer() startTimer()", "", "", "", "");*/
                                                ////on purpose
                                            }
                                        }
                                    } else {
                                        disposeTimer();
                                        long currTimeMS = new Date().getTime();
                                        objGlobalMethods.setBTLoggerRelatedPrefs(btp, "lastTimerRanTimeLong", String.valueOf(currTimeMS));
                                        startTimer(0);
                                        //objGlobalMethods.writeToFile("temploggerclass startTimer onFinish mdpsJsonStr empty. disposeTimer() startTimer()", "", "", "", "");
                                        ////on purpose
                                    }
                                } else if (BTEnabled != null && !BTEnabled) {
                                    disposeTimer();
                                    long currTimeMS = new Date().getTime();
                                    objGlobalMethods.setBTLoggerRelatedPrefs(btp, "lastTimerRanTimeLong", String.valueOf(currTimeMS));
                                    startTimer(0);
                                    //objGlobalMethods.writeToFile("temploggerclass startTimer onFinish !BTEnabled disposeTimer() startTimer()", "", "", "", "");
                                    ////on purpose
                                    promptToOnBT();
                                } else {
                                    //objGlobalMethods.writeToFile("temploggerclass startTimer onFinish Device does not support Bluetooth", "", "", "", "");
                                    ////on purpose
                                }
                            } else {
                                disposeTimer();
                                long currTimeMS = new Date().getTime();
                                objGlobalMethods.setBTLoggerRelatedPrefs(btp, "lastTimerRanTimeLong", String.valueOf(currTimeMS));
                                startTimer(0);
                                //objGlobalMethods.writeToFile("temploggerclass startTimer onFinish. Not scanning BTL !isScreenDisplayOn. disposeTimer() startTimer()", "", "", "", "");
                                ////on purpose
                            }
                        } catch (Exception e) {
                            String s = JobsHandler.getStackTraceString(e);
                            disposeTimer();
                            objGlobalMethods.writeToFile("Exception at temploggerclass startTimer onFinish. disposeTimer(). Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                        }
                    }
                }.start();
                objGlobalMethods.writeToFile("Start temploggerclass Timer.", "", "", "", "");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exeption at temploggerclass startTimer. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static void disposeTimer() {
        try {
            if (Timer != null) {
                //objGlobalMethods.writeToFile("temploggerclass Timer not null. Setting Timer.cancel() and to null.", "", "", "", "");
                ////on purpose
                Timer.cancel();
                Timer = null;
            } else {
                //objGlobalMethods.writeToFile("temploggerclass Timer is already null.", "", "", "", "");
                ////on purpose
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at temploggerclass disposeTimer. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static Boolean isBTEnabled() {
        Boolean asd = null;
        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter != null) {
                asd = mBluetoothAdapter.isEnabled();
            } else {
                // Device does not support Bluetooth
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at temploggerclass isBTEnabled. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static void promptToOnBT() {
        try {
            if (myApp.currentActivity != null) {
                Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class) {
                    final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                    final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListRoutingActivity.class) {
                    final AddressListRoutingActivity act = (AddressListRoutingActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListNewActivity.class) {
                    final AddressListNewActivity act = (AddressListNewActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobsListGroupActivity.class) {
                    final JobsListGroupActivity act = (JobsListGroupActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DashboardSubActivity.class) {
                    final DashboardSubActivity act = (DashboardSubActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MapsAllActivity.class) {
                    final MapsAllActivity act = (MapsAllActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MapsActivity.class) {
                    final MapsActivity act = (MapsActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobInfoActivity.class) {
                    final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CheckListActivity.class) {
                    final CheckListActivity act = (CheckListActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CageInOutActivity.class) {
                    final CageInOutActivity act = (CageInOutActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CODupdate2.class) {
                    final CODupdate2 act = (CODupdate2) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == ChangePwdActivity.class) {
                    final ChangePwdActivity act = (ChangePwdActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == ProfileActivity.class) {
                    final ProfileActivity act = (ProfileActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == StaffPassActivity.class) {
                    final StaffPassActivity act = (StaffPassActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == GCScheduleActivity.class) {
                    final GCScheduleActivity act = (GCScheduleActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == LeaveActivity.class) {
                    final LeaveActivity act = (LeaveActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == LeaveApplicationActivity.class) {
                    final LeaveApplicationActivity act = (LeaveApplicationActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == InventorySummaryActivity.class) {
                    final InventorySummaryActivity act = (InventorySummaryActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AuthActivity.class) {
                    final AuthActivity act = (AuthActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == LockerProfileActivity.class) {
                    final LockerProfileActivity act = (LockerProfileActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == ShipmentActivity.class) {
                    final ShipmentActivity act = (ShipmentActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CallActivity.class) {
                    final CallActivity act = (CallActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CustomGalleryActivity.class) {
                    final CustomGalleryActivity act = (CustomGalleryActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CustomGallery2Activity.class) {
                    final CustomGallery2Activity act = (CustomGallery2Activity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DOinvCheckActivity.class) {
                    final DOinvCheckActivity act = (DOinvCheckActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DriverPointMain_Activity.class) {
                    final DriverPointMain_Activity act = (DriverPointMain_Activity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DriverPointSummaryActivity.class) {
                    final DriverPointSummaryActivity act = (DriverPointSummaryActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == ItemGroupCheck.class) {
                    final ItemGroupCheck act = (ItemGroupCheck) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MassUpdateOKPwP2.class) {
                    final MassUpdateOKPwP2 act = (MassUpdateOKPwP2) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == OtpActivity.class) {
                    final OtpActivity act = (OtpActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == QRBarcodeReader.class) {
                    final QRBarcodeReader act = (QRBarcodeReader) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == SecuritySealActivity.class) {
                    final SecuritySealActivity act = (SecuritySealActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == setDeliveredMassStatusActivity.class) {
                    final setDeliveredMassStatusActivity act = (setDeliveredMassStatusActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == setDeliveredStatusActivity.class) {
                    final setDeliveredStatusActivity act = (setDeliveredStatusActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == setDunningDeliveredStatusActivity.class) {
                    final setDunningDeliveredStatusActivity act = (setDunningDeliveredStatusActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == setFailStatusActivity.class) {
                    final setFailStatusActivity act = (setFailStatusActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == SignatureNameActivity.class) {
                    final SignatureNameActivity act = (SignatureNameActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == UpdateCOD_List.class) {
                    final UpdateCOD_List act = (UpdateCOD_List) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == ViewImageActivity.class) {
                    final ViewImageActivity act = (ViewImageActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == BulkPickUpActivity.class) {
                    final BulkPickUpActivity act = (BulkPickUpActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobsheetActivity.class) {
                    final JobsheetActivity act = (JobsheetActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobsheetSubmitActivity.class) {
                    final JobsheetSubmitActivity act = (JobsheetSubmitActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CageScanActivity.class) {
                    final CageScanActivity act = (CageScanActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == HandoverActivity.class) {
                    final HandoverActivity act = (HandoverActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TakeoverActivity.class) {
                    final TakeoverActivity act = (TakeoverActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MainCODActivity.class) {
                    final MainCODActivity act = (MainCODActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == Messaginglist.class) {
                    final Messaginglist act = (Messaginglist) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == Messaging.class) {
                    final Messaging act = (Messaging) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CreditCardActivity.class) {
                    final CreditCardActivity act = (CreditCardActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MPD_CheckOutActivity.class) {
                    final MPD_CheckOutActivity act = (MPD_CheckOutActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MPDMainActivity.class) {
                    final MPDMainActivity act = (MPDMainActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == InventoryMainActivity.class) {
                    final InventoryMainActivity act = (InventoryMainActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerRegActivity.class) {
                    final TempLoggerRegActivity act = (TempLoggerRegActivity) myApp.currentActivity;
                    act.startActivityForResult(mIntent, 1);
                }
            } else {
                objGlobalMethods.writeToFile("temploggerclass promptToOnBT not working due myApp.currentActivity == null", "", "", "", "");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at temploggerclass promptToOnBT. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static boolean TempIsCompliant(double LiveTemp) {
        boolean asd = false;
        asd = LiveTemp >= 1.5 && LiveTemp <= 8.5;
        return asd;
    }

}
