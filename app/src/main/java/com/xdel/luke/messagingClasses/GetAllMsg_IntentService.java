package com.xdel.luke.messagingClasses;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.xdel.luke.JobsHandler2;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GetAllMsg_IntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.xdel.luke.messagingClasses.action.FOO";
    private static final String ACTION_BAZ = "com.xdel.luke.messagingClasses.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.xdel.luke.messagingClasses.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.xdel.luke.messagingClasses.extra.PARAM2";

    public GetAllMsg_IntentService() {
        super("GetAllMsg_IntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GetAllMsg_IntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GetAllMsg_IntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Integer CC = 0;
        int uponLogin = intent.getIntExtra("uponLogin", 0);
        if (intent != null && uponLogin == 1) {
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            String username = "";
            try {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "GetAllMsgCompleted", "");
                CC = uponLogin == 1 ? 0 : DAO_Jobs.getChatMsgSendersCount();
                if (CC < 2) {
                    Context ctx = myApp.getAppContext();
                    String regid = objGlobalMethods.getRegistrationId(ctx, GcmPreferences);
                    if (!regid.equals("")) {
                        objChatMessage[] cm = WebService.RetrieveMessages(regid);
                        if (cm != null && cm.length > 0) {
                            for (int i = 0; i <= cm.length-1; i++) {
                                username = cm[i].UserName;
                                cm[i].setUserName(username);
                            }
                            DAO_Jobs.insertChatMessagesBatch(cm);
                            List<ChatMsgSenders> cmsal = DAO_Jobs.getChatMsgSendersList();
                            ChatMsgSenders cms = null;
                            ArrayList<ChatMessage> cmal;
                            if (cmsal != null && cmsal.size() > 0) {
                                for (int i = 0; i <= cmsal.size()-1; i++) {
                                    cms = cmsal.get(i);
                                    cmal = DAO_Jobs.getchatMessages2(Long.parseLong(cms.JobsIDX), Long.parseLong(cms.UserIDX));
                                    if (cmal != null && cmal.size() > 0) {
                                        Collections.sort(cmal, new ChatMessage.OrderbymessageDate());
                                        Collections.sort(cmal, Collections.reverseOrder());
                                        addDateToChatMessage(cmal, Long.parseLong(cms.UserIDX), cms.UN,
                                                cms.JobsIDX == null || cms.JobsIDX.equals("") || cms.JobsIDX.equals("0") ? "0" : cms.JobsIDX, true);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at GetAllMsg_IntentService onHandleIntent. Exception: " + e.getMessage().toString(),"","","","");
            } finally {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "GetAllMsgCompleted", "true");
            }
        }
    }

    public static void addDateToChatMessage(ArrayList<ChatMessage> cmal, Long UserIDX, String UserName, String JobsIDX, boolean IsOldMsg){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String prevMsgDate = "";
        String currProscessingDate = "";
        ChatMessage cm = null;
        ChatMessage newDatecm = null;
        try{
            if (IsOldMsg || cmal.size() < 2) {
                cm = cmal.get(0);
            } else if (cmal.size() >= 2) {
                cm = cmal.get(1);
            }

            newDatecm = objGlobalMethods.createNewDateCM(cm.messageDate);
            Date dd = new Date(cm.messageDate);
            dd = objGlobalMethods.getZeroTimeDate(dd);
            prevMsgDate = dateFormat.format(dd);

            if (IsOldMsg) {
                DAO_Jobs.insertChatMessage(new objChatMessage(String.valueOf(dd.getTime()), cm.getID(), UserIDX, UserName, Long.parseLong(JobsIDX),
                        newDatecm.getMessageText(), 2, newDatecm.getMessageStatus().ordinal(), newDatecm.getUserType().ordinal(), cm.getID()));
                for (int i = 1; i <= cmal.size() - 1; i++) {
                    cm = cmal.get(i);
                    dd = new Date(cm.messageDate);
                    dd = objGlobalMethods.getZeroTimeDate(dd);
                    currProscessingDate = dateFormat.format(dd);
                    if (!currProscessingDate.equals(prevMsgDate)) {
                        newDatecm = objGlobalMethods.createNewDateCM(cm.messageDate);
                        DAO_Jobs.insertChatMessage(new objChatMessage(String.valueOf(dd.getTime()), cm.getID(), UserIDX, UserName, Long.parseLong(JobsIDX),
                                newDatecm.getMessageText(),2, newDatecm.getMessageStatus().ordinal(), newDatecm.getUserType().ordinal(), cm.getID()));
                        prevMsgDate = currProscessingDate;
                    }
                }
            } else {
                cm = cmal.get(0);
                dd = new Date(cm.messageDate);
                dd = objGlobalMethods.getZeroTimeDate(dd);
                currProscessingDate = dateFormat.format(dd);
                if (!currProscessingDate.equals(prevMsgDate)) {
                    newDatecm = objGlobalMethods.createNewDateCM(cm.messageDate);
                    DAO_Jobs.insertChatMessage(new objChatMessage(String.valueOf(dd.getTime()), cm.getID(), UserIDX, UserName, Long.parseLong(JobsIDX),
                            newDatecm.getMessageText(),2, newDatecm.getMessageStatus().ordinal(), newDatecm.getUserType().ordinal(), cm.getID()));
                    prevMsgDate = currProscessingDate;
                }
            }


        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at GetAllMsg_IntentService addDateToChatMessage. Exception: " + e.getMessage().toString(),"","","","");
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
