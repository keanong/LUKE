package com.xdel.luke.Voip;

import android.util.Log;

import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objGlobalMethods;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.ContainerNode;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.JsonDocument;
import org.pjsip.pjsua2.LogConfig;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.UaConfig;
import org.pjsip.pjsua2.pj_log_decoration;
import org.pjsip.pjsua2.pjsip_transport_type_e;
import org.pjsip.pjsua2.pjsua_stun_use;

import java.util.ArrayList;


public class VoipApp {
    static {
        try {
            System.loadLibrary("openh264");
            System.loadLibrary("yuv");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("UnsatisfiedLinkError: " + e.getMessage());
            System.out.println("This could be safely ignored if you " +
                    "don't need video.");
        }
        System.loadLibrary("pjsua2");
        System.out.println("Library loaded");
    }

    public static Endpoint ep = new Endpoint();
    public static MyAppObserver observer;
    public ArrayList<MyAccount> accList = new ArrayList<MyAccount>();

    private ArrayList<MyAccountConfig> accCfgs =
            new ArrayList<MyAccountConfig>();
    private EpConfig epConfig = new EpConfig();
    private TransportConfig sipTpConfig = new TransportConfig();
    private String appDir;

    /* Maintain reference to log writer to avoid premature cleanup by GC */
    private MyLogWriter logWriter;

    private final String configName = "pjsua2.json";
    private final int SIP_PORT = 2002;
    private final int LOG_LEVEL = 4;

    public void delobj() {
        try {
            /*if (logWriter != null) {
                logWriter.delete();
            }
            if (sipTpConfig != null) {
                sipTpConfig.delete();
            }
            if (accCfgs != null && accCfgs.size() > 0) {
                for (int i = accCfgs.size() -1; i >= 0; i--) {
                    accCfgs.get(i).accCfg.delete();
                    accCfgs.remove(i);
                }
            }
            accCfgs = null;
            if (accList != null && accList.size() > 0) {
                for (int i = accList.size() -1; i >= 0; i--) {
                    accList.get(i).delete();
                    accList.remove(i);
                }
            }
            accList = null;
            if (epConfig != null) {
                epConfig.delete();
            }
            epConfig = null;*/

            if (sipTpConfig != null) {
                sipTpConfig.delete();
            }
            if (epConfig != null) {
                epConfig.delete();
            }
            if (JobInfoActivity.account != null) {
                JobInfoActivity.account.delete();
                JobInfoActivity.app.delAcc(JobInfoActivity.account);
            }

        } catch (Exception e) {
            String s = e.getMessage();
        }
    }

    public void init(MyAppObserver obs, String app_dir) {
        init(obs, app_dir, false);
    }

    public void init(MyAppObserver obs, String app_dir,
                     boolean own_worker_thread) {
        observer = obs;
        appDir = app_dir;

        if (ep == null) {
            ep = new Endpoint();
        }

        /* Create endpoint */
        try {
            ep.libCreate();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VoipApp init ep.libCreate(). Exception: " + e.getMessage().toString(),"","","","");
            return;
        }

        sipTpConfig.setPort(0); // OS assigns a free port, or else use 5060

        objGlobalMethods.writeToFile("Register voip using port: " + String.valueOf(sipTpConfig.getPort()),"","","","");

        /* Override log level setting */
        epConfig.getLogConfig().setLevel(LOG_LEVEL);
        epConfig.getLogConfig().setConsoleLevel(LOG_LEVEL);

        /* Set log config. */
        LogConfig log_cfg = epConfig.getLogConfig();
        logWriter = new MyLogWriter();
        log_cfg.setWriter(logWriter);
        log_cfg.setDecor(log_cfg.getDecor() &
                ~(pj_log_decoration.PJ_LOG_HAS_CR |
                        pj_log_decoration.PJ_LOG_HAS_NEWLINE));

        /* Set ua config. */
        UaConfig ua_cfg = epConfig.getUaConfig();
        ua_cfg.setUserAgent("Pjsua2 Android " + ep.libVersion().getFull());
        // No endpoint STUN servers configured — intentional. See addAcc() NAT comment.

        if (own_worker_thread) {
            ua_cfg.setThreadCnt(0);
            ua_cfg.setMainThreadOnly(true);
        }

        /* Init endpoint */
        try {
            ep.libInit(epConfig);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at VoipApp init ep.libInit(epConfig). Exception: " + e.getMessage().toString() + ". Stack:\n" + JobsHandler.getStackTraceString(e),"","","","");
            return;
        }

        /* Create transports. */
        try {
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP,
                    sipTpConfig);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at VoipApp init ep.transportCreate. Exception: " + e.getMessage().toString() + ". Stack:\n" + JobsHandler.getStackTraceString(e),"","","","");
            //System.out.println(e);
        }

        /* Do NOT create a TCP transport here. pjsip auto-switches any request
         * larger than ~1300 bytes (i.e. every INVITE with SDP) to TCP per
         * RFC 3261 sec 18.1.1 when a TCP transport exists — and the PBX does not
         * accept TCP on 5060. Tested 2026-07-08: with TCP created, REGISTER (small,
         * stays UDP) succeeded but INVITEs vanished and calls never rang. UDP only. */

        /* TCP transport. Creating it does NOT move traffic to TCP —
         * accounts continue to use UDP unless a SIP URI carries
         * ;transport=tcp. This only makes TCP available as an option.
         * Uses its own TransportConfig (not sipTpConfig) so UDP and TCP
         * each get their own OS-assigned port. */

        /*try {
            TransportConfig tcpTpConfig = new TransportConfig();
            tcpTpConfig.setPort(0); // OS-assigned port, same policy as UDP
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP,
                    tcpTpConfig);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at VoipApp init ep.transportCreate TCP. Exception: " + e.getMessage().toString() + ". Stack:\n" + JobsHandler.getStackTraceString(e),"","","","");
        }*/

        /* Create accounts. */
        for (int i = 0; i < accCfgs.size(); i++) {
            MyAccountConfig my_cfg = accCfgs.get(i);

            /* Customize account config */
            my_cfg.accCfg.getNatConfig().setIceEnabled(false);
            my_cfg.accCfg.getVideoConfig().setAutoTransmitOutgoing(true);
            my_cfg.accCfg.getVideoConfig().setAutoShowIncoming(true);

            MyAccount acc = addAcc(my_cfg.accCfg);
            if (acc == null)
                continue;

            /* Add Buddies */
            for (int j = 0; j < my_cfg.buddyCfgs.size(); j++) {
                BuddyConfig bud_cfg = my_cfg.buddyCfgs.get(j);
                acc.addBuddy(bud_cfg);
            }
        }

        /* Start. */
        try {
            ep.libStart();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VoipApp init ep.libStart(). Exception: " + e.getMessage().toString(),"","","","");
            return;
        }
    }

    public MyAccount addAcc(AccountConfig cfg) {
        // NAT strategy: no STUN, no ICE. The carrier's symmetric CGNAT makes
        // STUN-discovered addresses invalid toward the PBX; we rely on the
        // server's rport + symmetric-RTP latching instead. Verified working
        // on mobile data 2026-07. Do NOT re-enable without re-testing on SIM data.
        cfg.getNatConfig().setIceEnabled(false);
        cfg.getNatConfig().setSipStunUse(pjsua_stun_use.PJSUA_STUN_USE_DISABLED);
        cfg.getNatConfig().setMediaStunUse(pjsua_stun_use.PJSUA_STUN_USE_DISABLED);

        MyAccount acc = new MyAccount(cfg);
        try {
            acc.create(cfg);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at VoipApp addAcc. Exception: " + e.getMessage().toString(),"","","","");
            acc = null;
            return null;
        }

        accList.add(acc);
        return acc;
    }

    public void delAcc(MyAccount acc) {
        accList.remove(acc);
    }

    private void loadConfig(String filename) {
        JsonDocument json = new JsonDocument();

        try {
            /* Load file */
            json.loadFile(filename);
            ContainerNode root = json.getRootContainer();

            /* Read endpoint config */
            epConfig.readObject(root);

            /* Read transport config */
            ContainerNode tp_node = root.readContainer("SipTransport");
            sipTpConfig.readObject(tp_node);

            /* Read account configs */
            accCfgs.clear();
            ContainerNode accs_node = root.readArray("accounts");
            while (accs_node.hasUnread()) {
                MyAccountConfig acc_cfg = new MyAccountConfig();
                acc_cfg.readObject(accs_node);
                accCfgs.add(acc_cfg);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at VoipApp loadConfig. Exception: " + e.getMessage(),"","","","");
            //System.out.println(e);
        }

        /* Force delete json now, as I found that Java somehow destroys it
         * after lib has been destroyed and from non-registered thread.
         */
        json.delete();
    }

    private void buildAccConfigs() {
        /* Sync accCfgs from accList */
        accCfgs.clear();
        for (int i = 0; i < accList.size(); i++) {
            MyAccount acc = accList.get(i);
            MyAccountConfig my_acc_cfg = new MyAccountConfig();
            my_acc_cfg.accCfg = acc.cfg;

            my_acc_cfg.buddyCfgs.clear();
            for (int j = 0; j < acc.buddyList.size(); j++) {
                MyBuddy bud = acc.buddyList.get(j);
                my_acc_cfg.buddyCfgs.add(bud.cfg);
            }

            accCfgs.add(my_acc_cfg);
        }
    }

    private void saveConfig(String filename) {
        JsonDocument json = new JsonDocument();

        try {
            /* Write endpoint config */
            json.writeObject(epConfig);

            /* Write transport config */
            ContainerNode tp_node = json.writeNewContainer("SipTransport");
            sipTpConfig.writeObject(tp_node);

            /* Write account configs */
            buildAccConfigs();
            ContainerNode accs_node = json.writeNewArray("accounts");
            for (int i = 0; i < accCfgs.size(); i++) {
                accCfgs.get(i).writeObject(accs_node);
            }

            /* Save file */
            json.saveFile(filename);
        } catch (Exception e) {
        }

        /* Force delete json now, as I found that Java somehow destroys it
         * after lib has been destroyed and from non-registered thread.
         */
        json.delete();
    }

    public void deinit() {
        try {
            String configPath = appDir + "/" + configName;
            saveConfig(configPath);

            delobj();

            /* Shutdown pjsua. Note that Endpoint destructor will also invoke
             * libDestroy(), so this will be a test of double libDestroy().
             */
            try {
                if (ep != null) {
                    ep.libDestroy();
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exceptoin at VoipApp deinit ep.libDestroy(). Exception: " + e.getMessage() + "\nStack:\n" + JobsHandler.getStackTraceString(e),"","","","");
            }

            /* Force delete Endpoint here, to avoid deletion from a non-
             * registered thread (by GC?).
             */
            /*if (ep != null) {
                ep.delete();
                ep = null;
            }*/

            ep.delete();
            ep = null;

            //newly added due memory leak
            observer = null;
            //newly added due memory leak
            if (JobInfoActivity.app != null) {
                JobInfoActivity.app = null;
            }

            /* Try force GC to avoid late destroy of PJ objects as they should be
             * deleted before lib is destroyed.
             */
            //Runtime.getRuntime().gc();

        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VoipApp deinit. Exception: " + s + "\nStack:\n" + JobsHandler.getStackTraceString(e), "", "", "", "");
        }

    }

    public static synchronized void checkThread() {
        try{
            if (ep != null && !ep.libIsThreadRegistered()) {
                //ep.libRegisterThread(Thread.currentThread().getName());

                ep.libRegisterThread("pj_thread_t");
            }
            //ep.libRegisterThread("SipApi");
        } catch (Exception e) {
            Log.w("SIP", "Threading: libRegisterThread failed: " + e.getMessage());
            String s = e.getMessage();
        }
    }
}



