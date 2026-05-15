package com.xdel.luke;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.Vrp.JobQueue;
import com.xdel.luke.messagingClasses.AndroidUtilities;
import com.xdel.luke.messagingClasses.ChatListAdapter;
import com.xdel.luke.messagingClasses.ChatMessage;
import com.xdel.luke.messagingClasses.ChatMsgSenders;
import com.xdel.luke.messagingClasses.Emoji;
import com.xdel.luke.messagingClasses.EmojiView;
import com.xdel.luke.messagingClasses.MessageQue;
import com.xdel.luke.messagingClasses.NotificationCenter;
import com.xdel.luke.messagingClasses.SizeNotifierRelativeLayout;
import com.xdel.luke.messagingClasses.Status;
import com.xdel.luke.messagingClasses.UserType;
import com.xdel.luke.messagingClasses.objChatMessage;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjSimpleJobInfo;
import com.xdel.luke.templogger.temploggerclass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Messaging extends AppCompatActivity implements SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutDelegate, NotificationCenter.NotificationCenterDelegate {

    private TextView lblmsg_JobCount, lblmsg_JobsIDX, lblmsg_UserIDX;
    private ListView chatListView;
    private EditText chatEditText1;
    public static ArrayList<ChatMessage> chatMessages;
    private ImageView enterChatView1, emojiButton;
    private static ChatListAdapter listAdapter;
    private EmojiView emojiView;
    private SizeNotifierRelativeLayout sizeNotifierRelativeLayout;
    private boolean showingEmoji;
    private int keyboardHeight;
    private boolean keyboardVisible;
    private WindowManager.LayoutParams windowLayoutParams;
    private MediaPlayer player;
    public static String JobsIDX;
    public static String msgUserIDX;
    private int notiID;
    private ProgressDialog pd;
    ScreenShotContentObserver screenShotContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            FCMservice.currentActivity = this;
            JobsHandler.currentActivity = this;
        }*/
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_messaging);

        chatListView = (ListView) findViewById(R.id.chat_list_view);
        chatEditText1 = (EditText) findViewById(R.id.chat_edit_text1);
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);

        lblmsg_JobCount = (TextView) findViewById(R.id.lblmsg_JobCount);
        lblmsg_JobsIDX = (TextView) findViewById(R.id.lblmsg_JobsIDX);
        lblmsg_UserIDX = (TextView) findViewById(R.id.lblmsg_UserIDX);

        /*SharedPreferences gcmMsgPrefs = objGlobalMethods.getGcmPreferences();
        JobsIDX = objGlobalMethods.getGCMPrefs(gcmMsgPrefs, "readingMsgOfSelectedUserIDX_id_JobsIDXPrefStr");
        _id = objGlobalMethods.getGCMPrefs(gcmMsgPrefs, "readingMsgOfSelectedUserIDX_idPrefStr");
        msgUserIDX = objGlobalMethods.getGCMPrefs(gcmMsgPrefs, "readingMsgOfSelectedUserIDXPrefStr");*/
        lblmsg_JobsIDX.setText(JobsIDX);
        lblmsg_UserIDX.setText(msgUserIDX);
        Integer c = DAO_Jobs.getJobCountByJobsIDX(lblmsg_JobsIDX.getText().toString().equals("") || lblmsg_JobsIDX.getText().toString().equals("0") ? 0 : Long.parseLong(lblmsg_JobsIDX.getText().toString()));
        lblmsg_JobCount.setText(String.valueOf(c));

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    @Override
    protected void onResume() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;

            String[] prefName = new String[]{"FailAsND", "FailAsND_Reason"};
            String[] prefvalue = new String[]{"", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            try {
                AndroidUtilities.statusBarHeight = getStatusBarHeight();
                // Hide the emoji on click of edit text
                chatEditText1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (showingEmoji)
                            hideEmojiPopup();
                    }
                });

                //emojiButton = (ImageView)findViewById(R.id.emojiButton);

       /* emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmojiPopup(!showingEmoji);
            }
        });*/
                JobsHandler.clearlAllDetailPagesStatic();
                JobsHandler.AddRemoveClearObjCallback(null, "","", true);
                loadList();

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("Messaging !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at Messaging onResume. " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            }
        }

        super.onResume();
    }

    private void loadList() {
        chatMessages = new ArrayList<>();
        SharedPreferences gcmMsgPrefs, updateJobInfoPref;
        try {
            gcmMsgPrefs = objGlobalMethods.getGcmPreferences();
            updateJobInfoPref = objGlobalMethods.getUpdateJobInfoPreferences();
            String uidx = lblmsg_UserIDX.getText().toString();
            String jidx = lblmsg_JobsIDX.getText().toString();
            long aUserIDX = uidx.equals("") || uidx.equals("0") ? 0 : Long.parseLong(uidx);
            long aJobsIDX = jidx.equals("") || jidx.equals("0") ? 0 : Long.parseLong(jidx);

            //ChatMsgSenders chatmsgsender = DAO_Jobs.getChatMsgSenders(Long.parseLong(msgUserIDX));
            ChatMsgSenders chatmsgsender = DAO_Jobs.getChatMsgSendersWithJobsIDXnChatMsgSendersIDX(aJobsIDX, aUserIDX);

            if (chatmsgsender != null) {
                notiID = chatmsgsender.notiID;
                setTitle(chatmsgsender.UserName);
                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
                nMgr.cancel(notiID);
                //check for unread msg n send back ack
                Long[] AckMessagesToBeSentAL = null;
                Long[] AckMessages = null;
                List<Long> ListAckMessages = new ArrayList<>();
                String AckMessagesToBeSentALPrefStr = gcmMsgPrefs.getString("AckMessagesToBeSentALPrefStr", "");
                chatmsgsender.Read = true;
                DAO_Jobs.updateChatMsgSenders(chatmsgsender);
                //chatMessages = DAO_Jobs.getchatMessages(Long.parseLong(msgUserIDX));
                chatMessages = DAO_Jobs.getchatMessages2(aJobsIDX, aUserIDX);

                if (chatMessages != null) {
                    try {
                        if (!AckMessagesToBeSentALPrefStr.isEmpty()) {
                            Gson gson = new Gson();
                            AckMessagesToBeSentAL = gson.fromJson(AckMessagesToBeSentALPrefStr, Long[].class);
                            if (AckMessagesToBeSentAL != null && AckMessagesToBeSentAL.length > 0) {
                                for (int i = 0; i <= AckMessagesToBeSentAL.length - 1; i++) {
                                    for (int x = 0; x <= chatMessages.size() - 1; x++) {
                                        if (AckMessagesToBeSentAL[i].equals(chatMessages.get(x).getAckIDX())) {
                                            ListAckMessages.add(AckMessagesToBeSentAL[i]);
                                        }
                                    }
                                }
                            }
                        }

                        for (int i = 0; i <= chatMessages.size() - 1; i++) {
                            if (chatMessages.get(i).Direction != null && (chatMessages.get(i).Direction != 1 && chatMessages.get(i).Direction != 2)) {
                                chatMessages.get(i).setDirection(chatMessages.get(i).Direction == 100 ? 1 : 2);
                                DAO_Jobs.updateChatMessageDirection(chatMessages.get(i).getID(), chatMessages.get(i).Direction);
                                ListAckMessages.add(chatMessages.get(i).getAckIDX());
                            }
                        }

                        if (ListAckMessages != null && ListAckMessages.size() > 0) {
                            AckMessages = ListAckMessages.toArray(new Long[ListAckMessages.size()]);
                            JobsHandler.addAckMessagesQuePrefs(updateJobInfoPref, AckMessages);
                            JobsHandler.addAckMessagesToBeSent(gcmMsgPrefs, AckMessages, false);
                            Context ctx = myApp.getAppContext();
                            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                        }

                        listAdapter = new ChatListAdapter(chatMessages, this);
                        chatListView.setAdapter(listAdapter);
                        chatEditText1.setOnKeyListener(keyListener);
                        enterChatView1.setOnClickListener(clickListener);
                        chatEditText1.addTextChangedListener(watcher1);
                        sizeNotifierRelativeLayout = (SizeNotifierRelativeLayout) findViewById(R.id.chat_layout);
                        sizeNotifierRelativeLayout.delegate = this;

                        if (chatMessages.size() > 0) {
                            //runs after listview is done updating itself
                            chatListView.post(new Runnable() {
                                public void run() {
                                    //scroll the listview to the last item
                                    if (chatListView.getCount() > 0) {
                                        chatListView.setSelection(chatListView.getCount() - 1);
                                    }
                                }
                            });
                        }

                        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);

                        objGlobalMethods.checkToPromptNotiForBattCharging();
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception caught inside Messaging onResume processing MessageIDX. " + e.getMessage().toString(), "", "", "", "");
                        showErrorAlert();
                    }
                } else {
                    showErrorAlert();
                }
                //check for unread msg n send back ack
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Messaging loadList. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            showErrorAlert();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messaging, menu);
        menu.findItem(R.id.action_viewjob).setVisible(lblmsg_JobCount.getText().toString().equals("") || lblmsg_JobCount.getText().toString().equals("0") ? false : true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_viewjob) {
            Context ctx = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(ctx.getString(R.string.menu_viewjob) + "?");
            builder.setPositiveButton(ctx.getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    long JobsIDX = lblmsg_JobsIDX.getText().toString().equals("") || lblmsg_JobsIDX.getText().toString().equals("0") ? 0 : Long.parseLong(lblmsg_JobsIDX.getText().toString());
                    openJobInfo t = new openJobInfo(JobsIDX);
                    t.execute();

                }
            });
            builder.setNegativeButton(ctx.getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        /*switch (item.getItemId()) {
            case R.id.action_viewjob:
                Context ctx = this;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(ctx.getString(R.string.menu_viewjob) + "?");
                builder.setPositiveButton(ctx.getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long JobsIDX = lblmsg_JobsIDX.getText().toString().equals("") || lblmsg_JobsIDX.getText().toString().equals("0") ? 0 : Long.parseLong(lblmsg_JobsIDX.getText().toString());
                        openJobInfo t = new openJobInfo(JobsIDX);
                        t.execute();

                    }
                });
                builder.setNegativeButton(ctx.getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                break;
        }*/
        return super.onOptionsItemSelected(item);
    }

    private class openJobInfo extends AsyncTask<Void, Void, dbObjSimpleJobInfo> {
        private final long JobsIDX;

        public openJobInfo(long aJobsIDX) {
            this.JobsIDX = aJobsIDX;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected dbObjSimpleJobInfo doInBackground(Void... params) {
            dbObjSimpleJobInfo obj = null;
            try {
                obj = DAO_Jobs.gethfJobInfoJsonStrAndLatLngByJobsIDX(JobsIDX);
            } catch (Exception e) {
                obj = null;
            } finally {
                return obj;
            }
        }

        @Override
        protected void onPostExecute(final dbObjSimpleJobInfo obj) {
            boolean asd = false;
            boolean aisDunning = false;
            Intent i = new Intent(Messaging.this, JobInfoActivity.class);
            List<JobQueue> First_3_JobQueue = null;
            try {
                boolean IsDefaultVRPpage = objGlobalMethods.IsDefaultVRPpage();
                if (obj != null && !obj.hfJobInfoJsonStr.equals("")) {
                    int CatTypeID = obj.catType;
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, obj.hfJobInfoJsonStr);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", String.valueOf(obj.Lat));
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", String.valueOf(obj.Lng));
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", obj.postal);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", obj.address);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMessaging", "true");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, String.valueOf(CatTypeID));
                    if (CatTypeID == 0) {
                        //objGlobalMethods.stopAlarmForCommon();
                        if (FCMservice.mNotificationManager != null) {
                            FCMservice.mNotificationManager.cancel(2);
                        }
                        FCMservice.notiNotRead = false;
                    }

                    if (CatTypeID == 2 || CatTypeID == 3 || CatTypeID == 5) {
                        //DIP,OKP,FD
                        Gson gson = new Gson();
                        objJobInfo JobInfo = gson.fromJson(obj.hfJobInfoJsonStr, objJobInfo.class);
                        aisDunning = JobsHandler.isDunning(JobInfo);
                        if (CatTypeID == 2 && aisDunning) {
                            objGlobalMethods.writeToFile(JobInfo.JobNo + " is Dunning.", "", "", "", "");
                            if (JobInfo != null) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", JobsHandler.CannotTakePhoto(JobInfo, 2) ? "1" : "0");
                            } else {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
                            }

                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX, String.valueOf(JobInfo.JobsIDX));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "currJobInfoCN", JobInfo.JobNo);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "addressWithUnit", obj.addressWithUnit);
                        }
                    }

                    if (CatTypeID == 3 || CatTypeID == 4 || CatTypeID == 5) {
                        //OKP,FP,FD
                        JobInfoActivity.accessFromDirection = 1;
                    } else {
                        if (aisDunning) {
                            i = new Intent(Messaging.this, setDunningDeliveredStatusActivity.class);
                            JobInfoActivity.accessFromDirection = 0;
                        } else {
                            JobInfoActivity.accessFromDirection = 1;
                        }
                    }

                    if (IsDefaultVRPpage) {
                        Gson gson = new Gson();
                        String First_3_JobQueueJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "First_3_JobQueueJsonStr");
                        try {
                            if ((!First_3_JobQueueJsonStr.isEmpty() && !First_3_JobQueueJsonStr.equals("") && First_3_JobQueueJsonStr != "")) {
                                First_3_JobQueue = gson.fromJson(First_3_JobQueueJsonStr, new TypeToken<List<JobQueue>>() {
                                }.getType());
                            }

                            if (First_3_JobQueue != null && First_3_JobQueue.size() > 0) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "enableJobUpdates", String.valueOf(false));
                                for (int x = 0; x <= First_3_JobQueue.size() - 1; x++) {
                                    if (First_3_JobQueue.get(x).getPostalCode() != null && First_3_JobQueue.get(x).getPostalCode().equals(obj.postal)) {
                                        objGlobalMethods.setGCMPrefs(GcmPreferences, "enableJobUpdates", String.valueOf(First_3_JobQueue.get(x).getIsEnabled()));
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception at Messaging openJobInfo onPostExecute First_3_JobQueueJsonStr. Exception: " + e.getMessage().toString(),"","","","");
                        }
                    }
                    asd = true;
                }
            } catch (Exception e) {
                asd = false;
                objGlobalMethods.writeToFile("Exception at Messaging openJobInfo. Excpetion: " + e.getMessage().toString(), "", "", "", "");
            } finally {
                if (pd != null) {
                    pd.dismiss();
                }
                Context ctx = myApp.getAppContext();
                if (asd) {
                    startActivity(i);
                } else {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorretrivedata)).show();
                }
            }
        }
    }

    private ArrayList<ChatMessage> addDateToChatMessage2(ArrayList<ChatMessage> achatMessages) {
        Date dt = new Date();
        ArrayList<ChatMessage> aachatMessages = achatMessages;
        ArrayList<ChatMessage> aachatMessages2 = new ArrayList<>();
        //Date prevMsgDate = null;
        Long prevMsgDate = null;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateText = "";
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        try {


            for (int i = 0; i <= aachatMessages.size() - 1; i++) {
                if (i == 0) {
                    if (aachatMessages.get(i).messageDate == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -1);
                        aachatMessages.get(i).messageDate = cal.getTimeInMillis();
                        aachatMessages.get(i).setMessageText(dateFormat.format(cal.getTime()));
                    }

                    if (dateFormat.format(aachatMessages.get(i).messageDate).equals(dateFormat.format(dt)) && aachatMessages.get(i).getMessageText().equals("Today")) {
                        prevMsgDate = aachatMessages.get(i).messageDate;
                    } else {
                        if (dateFormat.format(aachatMessages.get(i).messageDate).equals(dateFormat.format(dt.getTime()))) {
                            dateText = "Today";
                        } else {
                            dateText = dateFormat.format(aachatMessages.get(i).messageDate);
                        }
                        final ChatMessage message = new ChatMessage();

                        aachatMessages.get(i).setMessageText(dateText);
                        message.setMessageTime(new Date().getTime());
                        prevMsgDate = aachatMessages.get(i).messageDate;
                    }
                    aachatMessages2.add(aachatMessages.get(i));
                } else if (i > 0) {
                    if (aachatMessages.get(i).messageDate != null
                            && !dateFormat.format(aachatMessages.get(i).messageDate).equals(dateFormat.format(prevMsgDate))
                            && !aachatMessages.get(i).getMessageText().equals("Today")) {
                        if (dateFormat.format(aachatMessages.get(i).messageDate).equals(dateFormat.format(dt))) {
                            dateText = "Today";
                        } else {
                            dateText = dateFormat.format(aachatMessages.get(i).messageDate);
                        }

                        if (!aachatMessages.get(i).getMessageText().equals(dateText)) {
                            final ChatMessage message = new ChatMessage();

                            message.setID(m);
                            message.setMessageStatus(Status.SENT);
                            message.setMessageText(dateText);
                            message.setUserType(UserType.DATE);
                            message.setMessageTime(new Date().getTime());
                            message.messageDate = aachatMessages.get(i).messageDate;
                            aachatMessages2.add(message);
                        }
                        //prevMsgDate = aachatMessages.get(i).messageDate;
                    }
                    prevMsgDate = aachatMessages.get(i).messageDate;
                    aachatMessages2.add(aachatMessages.get(i));
                }

                //aachatMessages2.add(aachatMessages.get(i));
            }

            if (aachatMessages2.size() > 0) {
                int x = aachatMessages2.size() - 1;
                if (!dateFormat.format(aachatMessages2.get(x).messageDate).equals(dateFormat.format(dt))
                        && !aachatMessages2.get(x).getMessageText().equals("Today")) {
                    final ChatMessage xmessage = new ChatMessage();
                    m = random.nextInt(9999 - 1000) + 1000;
                    xmessage.setID(m);
                    xmessage.setMessageStatus(Status.SENT);
                    xmessage.setMessageText("Today");
                    xmessage.setUserType(UserType.DATE);
                    xmessage.setMessageTime(new Date().getTime());
                    //xmessage.messageDate = dt;
                    xmessage.messageDate = dt.getTime();
                    aachatMessages2.add(xmessage);
                }
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at Messaging addDateToChatMessage2. " + e.getMessage().toString(), "", "", "", "");
            aachatMessages2 = achatMessages;
        } finally {
            return aachatMessages2;
        }
    }

    private void addTodayDateToChatMessage() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        ChatMessage TodayCM = null;
        Date todayDate = new Date();
        String todatDateStr = dateFormat.format(todayDate);
        try {
            String uidx = lblmsg_UserIDX.getText().toString();
            String jidx = lblmsg_JobsIDX.getText().toString();
            long aUserIDX = uidx.equals("") || uidx.equals("0") ? 0 : Long.parseLong(uidx);
            long aJobsIDX = jidx.equals("") || jidx.equals("0") ? 0 : Long.parseLong(jidx);

            if (chatMessages != null && chatMessages.size() > 0) {
                for (int i = chatMessages.size() - 1; i >= 0; i--) {
                    Date dd = new Date(chatMessages.get(i).messageDate);
                    String currProscessingDate = dateFormat.format(dd);
                    if (!currProscessingDate.equals(todatDateStr)) {
                        TodayCM = objGlobalMethods.createNewDateCM(todayDate.getTime());
                        /*objChatMessage cm = new objChatMessage(String.valueOf(todayDate.getTime()), 0, Long.parseLong(msgUserIDX), "", 0, TodayCM.getMessageText(), 1
                                , Status.SENT.ordinal(), UserType.DATE.ordinal(), 0);*/
                        DAO_Jobs.insertChatMessage(new objChatMessage(String.valueOf(dd.getTime()), chatMessages.get(i).getID(), aUserIDX, "", aJobsIDX,
                                TodayCM.getMessageText(), 1, Status.SENT.ordinal(), UserType.DATE.ordinal(), 0));
                    }
                    break;
                }

                if (TodayCM != null && chatMessages != null) {
                    chatMessages.add(TodayCM);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Messaging addTodayDateToChatMessage. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void sendMessage3(final String messageText, final UserType userType) {
        Date dt = new Date();
        SharedPreferences gcmMsgPrefs, updateJobInfoPref;
        try {
            String uidx = lblmsg_UserIDX.getText().toString();
            String jidx = lblmsg_JobsIDX.getText().toString();
            long aUserIDX = uidx.equals("") || uidx.equals("0") ? 0 : Long.parseLong(uidx);
            long aJobsIDX = jidx.equals("") || jidx.equals("0") ? 0 : Long.parseLong(jidx);
            long newMsgIDX = 0;

            if (messageText.trim().length() == 0)
                return;

            gcmMsgPrefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            updateJobInfoPref = getSharedPreferences("UpdateJobInfoPrefs", Context.MODE_PRIVATE);

            final ChatMessage message = new ChatMessage();
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            message.setID(m);
            message.setMessageStatus(Status.SENT);
            message.setMessageText(messageText);
            message.setUserType(userType);
            message.setMessageTime(new Date().getTime());
            message.messageDate = dt.getTime();

            objChatMessage cm = new objChatMessage(String.valueOf(new Date().getTime()), 0, aUserIDX, "", aJobsIDX, messageText, 1
                    , Status.SENT.ordinal(), userType.ordinal(), 0);
            newMsgIDX = DAO_Jobs.insertSentChatMessage(cm);
            message.setID(newMsgIDX);
            objGlobalMethods.writeToFile("Message sent to XDel: " + messageText, "", "", "", "");
            String RegID = objGlobalMethods.getRegistrationId(myApp.getAppContext(), gcmMsgPrefs);
            MessageQue objMessageQue = new MessageQue(RegID, message, uidx, jidx.equals("") || jidx.equals("0") ? "0" : jidx);
            JobsHandler.addSendMessageQuePrefs2(updateJobInfoPref, objMessageQue);
            sendBroadcast(new Intent(myApp.getAppContext(), BackendRun_BroadcastReceiver.class));

            addTodayDateToChatMessage();
            if (chatMessages != null) {
                chatMessages.add(message);
            }

            if (listAdapter != null)
                listAdapter.notifyDataSetChanged();

            //runs after listview is done updating itself
            chatListView.post(new Runnable() {
                public void run() {
                    ////scroll the listview to the last item
                    chatListView.setSelection(chatListView.getCount() - 1);
                }
            });

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Messaging sendMessage3. " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    private Activity getActivity() {
        return this;
    }

    /**
     * Show or hide the emoji popup
     *
     * @param show
     */
    private void showEmojiPopup(boolean show) {
        showingEmoji = show;

        if (show) {
            if (emojiView == null) {
                if (getActivity() == null) {
                    return;
                }
                emojiView = new EmojiView(getActivity());

                emojiView.setListener(new EmojiView.Listener() {
                    public void onBackspace() {
                        chatEditText1.dispatchKeyEvent(new KeyEvent(0, 67));
                    }

                    public void onEmojiSelected(String symbol) {
                        int i = chatEditText1.getSelectionEnd();
                        if (i < 0) {
                            i = 0;
                        }
                        try {
                            CharSequence localCharSequence = Emoji.replaceEmoji(symbol, chatEditText1.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20));
                            chatEditText1.setText(chatEditText1.getText().insert(i, localCharSequence));
                            int j = i + localCharSequence.length();
                            chatEditText1.setSelection(j, j);
                        } catch (Exception e) {
                            //Log.e(Constants.TAG, "Error showing emoji");
                        }
                    }
                });


                windowLayoutParams = new WindowManager.LayoutParams();
                windowLayoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                if (Build.VERSION.SDK_INT >= 21) {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                } else {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                    windowLayoutParams.token = getActivity().getWindow().getDecorView().getWindowToken();
                }
                windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }

            final int currentHeight;

            if (keyboardHeight <= 0)
                keyboardHeight = myApp.getInstance().getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200));

            currentHeight = keyboardHeight;

            WindowManager wm = (WindowManager) myApp.getInstance().getSystemService(Activity.WINDOW_SERVICE);

            windowLayoutParams.height = currentHeight;
            windowLayoutParams.width = AndroidUtilities.displaySize.x;

            try {
                if (emojiView.getParent() != null) {
                    wm.removeViewImmediate(emojiView);
                }
            } catch (Exception e) {
                //Log.e(Constants.TAG, e.getMessage());
            }

            try {
                wm.addView(emojiView, windowLayoutParams);
            } catch (Exception e) {
                //Log.e(Constants.TAG, e.getMessage());
                return;
            }

            if (!keyboardVisible) {
                if (sizeNotifierRelativeLayout != null) {
                    sizeNotifierRelativeLayout.setPadding(0, 0, 0, currentHeight);
                }

                return;
            }

        } else {
            removeEmojiWindow();
            if (sizeNotifierRelativeLayout != null) {
                sizeNotifierRelativeLayout.post(new Runnable() {
                    public void run() {
                        if (sizeNotifierRelativeLayout != null) {
                            sizeNotifierRelativeLayout.setPadding(0, 0, 0, 0);
                        }
                    }
                });
            }
        }


    }

    /**
     * Remove emoji window
     */
    private void removeEmojiWindow() {
        if (emojiView == null) {
            return;
        }
        try {
            if (emojiView.getParent() != null) {
                WindowManager wm = (WindowManager) myApp.getInstance().getSystemService(Context.WINDOW_SERVICE);
                wm.removeViewImmediate(emojiView);
            }
        } catch (Exception e) {
            //Log.e(Constants.TAG, e.getMessage());
        }
    }

    /**
     * Hides the emoji popup
     */
    public void hideEmojiPopup() {
        if (showingEmoji) {
            showEmojiPopup(false);
        }
    }

    /**
     * Check if the emoji popup is showing
     *
     * @return
     */
    public boolean isEmojiPopupShowing() {
        return showingEmoji;
    }

    /**
     * Updates emoji views when they are complete loading
     *
     * @param id
     * @param args
     */
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded) {
            if (emojiView != null) {
                emojiView.invalidateViews();
            }

            if (chatListView != null) {
                chatListView.invalidateViews();
            }
        }
    }

    @Override
    public void onSizeChanged(int height) {

        Rect localRect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);

        WindowManager wm = (WindowManager) myApp.getInstance().getSystemService(Activity.WINDOW_SERVICE);
        if (wm == null || wm.getDefaultDisplay() == null) {
            return;
        }


        if (height > AndroidUtilities.dp(50) && keyboardVisible) {
            keyboardHeight = height;
            myApp.getInstance().getSharedPreferences("emoji", 0).edit().putInt("kbd_height", keyboardHeight).apply();
        }


        if (showingEmoji) {
            int newHeight = 0;

            newHeight = keyboardHeight;

            if (windowLayoutParams.width != AndroidUtilities.displaySize.x || windowLayoutParams.height != newHeight) {
                windowLayoutParams.width = AndroidUtilities.displaySize.x;
                windowLayoutParams.height = newHeight;

                wm.updateViewLayout(emojiView, windowLayoutParams);
                if (!keyboardVisible) {
                    sizeNotifierRelativeLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if (sizeNotifierRelativeLayout != null) {
                                sizeNotifierRelativeLayout.setPadding(0, 0, 0, windowLayoutParams.height);
                                sizeNotifierRelativeLayout.requestLayout();
                            }
                        }
                    });
                }
            }
        }


        boolean oldValue = keyboardVisible;
        keyboardVisible = height > 0;
        if (keyboardVisible && sizeNotifierRelativeLayout.getPaddingBottom() > 0) {
            showEmojiPopup(false);
        } else if (!keyboardVisible && keyboardVisible != oldValue && showingEmoji) {
            showEmojiPopup(false);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity = null;
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at Messaging onPause unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
        hideEmojiPopup();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatMessages = null;
        listAdapter = null;
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at Messaging onDestroy unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
        DAO_Jobs.nullifystaticitems();

        lblmsg_JobCount = null;
        lblmsg_JobsIDX = null;
        lblmsg_UserIDX = null;
        chatListView = null;
        chatEditText1 = null;
        enterChatView1 = null;
        emojiView = null;
        sizeNotifierRelativeLayout = null;
        windowLayoutParams = null;
        player = null;
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }

    }

    /**
     * Get the system status bar height
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        List<ChatMsgSenders> chatmsgsendersAL = null;
        try {
            Intent parentIntent = new Intent(this, Messaginglist.class);
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            this.finish();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Messaging onBackPressed. " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnViewImageBackClick(View view) {
        onBackPressed();
    }

    public void updateListView2() {
        try {
            SharedPreferences gcmMsgPrefs = objGlobalMethods.getGcmPreferences();
            SharedPreferences updateJobInfoPref = objGlobalMethods.getUpdateJobInfoPreferences();
            /*msgUserIDX = objGlobalMethods.getGCMPrefs(gcmMsgPrefs, "readingMsgOfSelectedUserIDXPrefStr");
            ChatMsgSenders chatmsgsender = DAO_Jobs.getChatMsgSenders(Long.parseLong(msgUserIDX));*/
            String uidx = lblmsg_UserIDX.getText().toString();
            String jidx = lblmsg_JobsIDX.getText().toString();
            long aUserIDX = uidx.equals("") || uidx.equals("0") ? 0 : Long.parseLong(uidx);
            long aJobsIDX = jidx.equals("") || jidx.equals("0") ? 0 : Long.parseLong(jidx);
            ChatMsgSenders chatmsgsender = DAO_Jobs.getChatMsgSendersWithJobsIDXnChatMsgSendersIDX(aJobsIDX, aUserIDX);

            if (chatmsgsender != null) {
                chatmsgsender.Read = true;
                DAO_Jobs.updateChatMsgSenders(chatmsgsender);
                chatMessages = DAO_Jobs.getchatMessages2(aJobsIDX, aUserIDX);
            }

            if (listAdapter != null)
                listAdapter.chatMessages = chatMessages;
            listAdapter.notifyDataSetChanged();

            //runs after listview is done updating itself
            chatListView.post(new Runnable() {
                public void run() {
                    ////scroll the listview to the last item
                    if (chatListView.getCount() > 0) {
                        chatListView.setSelection(chatListView.getCount() - 1);
                        playTone();
                    }
                }
            });

            //check for unread msg n send back ack

            Long[] AckMessagesToBeSentAL = null;
            Long[] AckMessages = null;
            List<Long> ListAckMessages = null;
            String AckMessagesToBeSentALPrefStr = gcmMsgPrefs.getString("AckMessagesToBeSentALPrefStr", "");
            Gson gson = new Gson();
            try {
                if (!AckMessagesToBeSentALPrefStr.isEmpty()) {
                    AckMessagesToBeSentAL = gson.fromJson(AckMessagesToBeSentALPrefStr, Long[].class);
                    if (AckMessagesToBeSentAL != null && AckMessagesToBeSentAL.length > 0) {
                        ListAckMessages = new ArrayList<>();
                        for (int i = 0; i <= AckMessagesToBeSentAL.length - 1; i++) {
                            for (int x = 0; x <= chatMessages.size() - 1; x++) {
                                if (AckMessagesToBeSentAL[i].equals(chatMessages.get(x).getAckIDX())) {
                                    ListAckMessages.add(AckMessagesToBeSentAL[i]);
                                }
                            }

                        }
                    }

                    if (ListAckMessages != null && ListAckMessages.size() > 0) {
                        AckMessages = ListAckMessages.toArray(new Long[ListAckMessages.size()]);
                        JobsHandler.addAckMessagesQuePrefs(updateJobInfoPref, AckMessages);
                        JobsHandler.addAckMessagesToBeSent(gcmMsgPrefs, AckMessages, false);
                        objGlobalMethods.writeToFile("Calling backendRun from Messaging updateListView.", "", "", "", "");
                        Context ctx = myApp.getAppContext();
                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught inside Messaging updateListView2 processing MessageIDX. " + e.getMessage().toString(), "", "", "", "");
            }
            //check for unread msg n send back ack
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Messaging updateListView2. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void updateListViewWithSent2() {
        SharedPreferences gcmMsgPrefs;
        ArrayList<ChatMessage> chatmsg = new ArrayList<>();
        ChatMsgSenders chatmsgsender;
        try {
            /*gcmMsgPrefs = objGlobalMethods.getGcmPreferences();
            msgUserIDX = objGlobalMethods.getGCMPrefs(gcmMsgPrefs, "readingMsgOfSelectedUserIDXPrefStr");
            chatmsgsender = DAO_Jobs.getChatMsgSenders(Long.parseLong(msgUserIDX));*/
            String uidx = lblmsg_UserIDX.getText().toString();
            String jidx = lblmsg_JobsIDX.getText().toString();
            long aUserIDX = uidx.equals("") || uidx.equals("0") ? 0 : Long.parseLong(uidx);
            long aJobsIDX = jidx.equals("") || jidx.equals("0") ? 0 : Long.parseLong(jidx);
            chatmsgsender = DAO_Jobs.getChatMsgSendersWithJobsIDXnChatMsgSendersIDX(aJobsIDX, aUserIDX);
            if (chatmsgsender != null) {
                chatMessages = DAO_Jobs.getchatMessages2(aJobsIDX, aUserIDX);
            }
            if (listAdapter != null)
                listAdapter.chatMessages = chatMessages;
            listAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Messaging updateListViewWithSent2. " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void playTone() {
        player = MediaPlayer.create(Messaging.this, R.raw.msg_in);
        player.start();
    }

    public Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    public Dialog showChargeBattDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                Boolean asd = false;
                SharedPreferences gcmp = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                objGlobalMethods.setGCMPrefs(gcmp, "showBattChargeNotiPrefStr", asd.toString());
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    private void showErrorAlert() {
        MainActivity.uponLogin = 0;
        DrawerMainActivity.DashboardOpenFromPush = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Error loading messages.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                Intent i;
                i = new Intent(Messaging.this, Messaginglist.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if (v == chatEditText1) {
                    sendMessage3(editText.getText().toString(), UserType.SELF);
                }

                chatEditText1.setText("");

                return true;
            }
            return false;

        }
    };

    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == enterChatView1) {
                sendMessage3(chatEditText1.getText().toString(), UserType.SELF);
            }

            chatEditText1.setText("");

        }
    };

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {

            } else {
                enterChatView1.setImageResource(R.mipmap.ic_chat_send);

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 0) {
                enterChatView1.setImageResource(R.mipmap.ic_chat_send);
            } else {
                enterChatView1.setImageResource(R.mipmap.ic_chat_send_active);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at Messaging","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Messaging onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
