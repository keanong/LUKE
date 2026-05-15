package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.messagingClasses.ChatMsgSenders;
import com.xdel.luke.messagingClasses.MessagingList_PostData;
import com.xdel.luke.messagingClasses.MessagingList_PostItemAdapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;
import java.util.List;

public class Messaginglist extends AppCompatActivity {

    ConnectionDetector cd;
    private ListView messaginglist;
    private static SharedPreferences GcmPreferences;
    private Context context;
    public MessagingList_PostItemAdapter adapter;
    public ArrayList<MessagingList_PostData> MessagingListPostDataArr;
    private ProgressDialog pd;
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_messaginglist);

        messaginglist = (ListView) findViewById(R.id.messaginglist);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait. Retrieving Info...");

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onResume() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            myApp.currentActivity = this;
            setTitle(myApp.getAppContext().getString(R.string.title_activity_messaginglist));

            String[] prefName = new String[]{"FailAsND", "FailAsND_Reason"};
            String[] prefvalue = new String[]{"", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            try {
                lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

                context = myApp.getAppContext();
                JobsHandler.clearlAllDetailPagesStatic();
                JobsHandler.AddRemoveClearObjCallback(null, "","", true);
                loadmessengerlist2();

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("Messaginglist !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at Messaginglist onResume. " + e.getMessage().toString(), "", "", "", "");
                showErrorAlert();
            }
        }

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messaginglist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            //Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            Intent parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            this.finish();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at Messaginglist onBackPressed. " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private SharedPreferences getGcmPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private void showErrorAlert() {
        MainActivity.uponLogin = 0;
        DrawerMainActivity.DashboardOpenFromPush = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Error loading Messaging List.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                Intent i;
                //i = new Intent(Messaginglist.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(Messaginglist.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadmessengerlist2() {
        List<ChatMsgSenders> chatmsgsendersForAllUsersAL = null;
        List<ChatMsgSenders> chatmsgsendersAL = null;
        GcmPreferences = getGcmPreferences();
        Boolean unreadMsgExist = false;
        ChatMsgSenders cms = null;
        ChatMsgSenders cms2 = null;
        try {
            chatmsgsendersForAllUsersAL = DAO_Jobs.getChatMsgSendersListForAllUsers();
            chatmsgsendersAL = DAO_Jobs.getChatMsgSendersList();
            if (chatmsgsendersForAllUsersAL.size() > chatmsgsendersAL.size()) {
                for (int i = 0; i <= chatmsgsendersForAllUsersAL.size() -1; i++) {
                    cms = chatmsgsendersForAllUsersAL.get(i);
                    DAO_Jobs.insertChatMsgSenders2(cms);
                }
            } else if (chatmsgsendersAL != null && chatmsgsendersAL.size() > 0) {
                for (int i = 0; i <= chatmsgsendersAL.size() - 1; i++) {
                    if (chatmsgsendersAL.get(i).Read == null || chatmsgsendersAL.get(i).Read == false) {
                        unreadMsgExist = true;
                        break;
                    }
                }
                objGlobalMethods.setGCMPrefs(GcmPreferences, "unreadMsgExist", unreadMsgExist ? "true" : "false");
            } else if (chatmsgsendersAL == null || chatmsgsendersAL.size() == 0) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "unreadMsgExist", "false");
            }
            chatmsgsendersAL = DAO_Jobs.getChatMsgSendersList();

            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
            nMgr.cancel(2);
            loadListFirst2(chatmsgsendersAL);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Messaginglist loadmessengerlist2. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "unreadMsgExist", "false");
        }
    }

    private void loadListFirst2(final List<ChatMsgSenders> chatmsgsendersAL) {
        try {
            GcmPreferences = getGcmPreferences();
            MessagingListPostDataArr = new ArrayList<MessagingList_PostData>();
            MessagingList_PostData postdata;
            String UserIDX, UserName, JobsIDX;
            boolean Read = false;
            if (chatmsgsendersAL != null && chatmsgsendersAL.size() > 0) {
                /*Collections.sort(chatmsgsendersAL, new ChatMsgSenders.OrderbymsgDateLong());
                Collections.sort(chatmsgsendersAL, Collections.reverseOrder());*/

                for (int i = 0; i <= chatmsgsendersAL.size() - 1; i++) {
                    UserIDX = chatmsgsendersAL.get(i).UserIDX;
                    UserName = chatmsgsendersAL.get(i).UserName;
                    Read = chatmsgsendersAL.get(i).Read;
                    JobsIDX = chatmsgsendersAL.get(i).JobsIDX;
                    postdata = new MessagingList_PostData(JobsIDX, UserIDX, UserName, Read);
                    MessagingListPostDataArr.add(postdata);
                }

                adapter = new MessagingList_PostItemAdapter(this, MessagingListPostDataArr);
                messaginglist.setAdapter(adapter);

                messaginglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        View v = messaginglist.getChildAt(position - messaginglist.getFirstVisiblePosition());
                        if (v == null) {
                            return;
                        } else {
                            GcmPreferences = getGcmPreferences();
                            TextView lblMsgUserIDX = (TextView) v.findViewById(R.id.lblMsgUserIDX);
                            TextView lblMsgJobsIDX = (TextView) v.findViewById(R.id.lblMsgJobsIDX);
                            final String msgUserIDX = lblMsgUserIDX.getText().toString();
                            final String JobsIDX = lblMsgJobsIDX.getText().toString();
                            try {
                                if (chatmsgsendersAL != null) {
                                    for (int i = 0; i <= chatmsgsendersAL.size() - 1; i++) {
                                        if (chatmsgsendersAL.get(i).UserIDX == msgUserIDX) {
                                            chatmsgsendersAL.get(i).Read = true;
                                            DAO_Jobs.updateChatMsgSenders(chatmsgsendersAL.get(i));
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                objGlobalMethods.writeToFile("Exception caught at Messaginglist loadListFirst messaginglist.setOnItemClickListener. " + e.getMessage().toString(), "", "", "", "");
                            }
                            /*objGlobalMethods.setGCMPrefs(GcmPreferences, "readingMsgOfSelectedUserIDX_id_JobsIDXPrefStr", JobsIDX);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "readingMsgOfSelectedUserIDX_idPrefStr", _id);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "readingMsgOfSelectedUserIDXPrefStr", msgUserIDX);*/
                            Messaging.JobsIDX = JobsIDX;
                            Messaging.msgUserIDX = msgUserIDX;
                            Intent intent = new Intent(Messaginglist.this, Messaging.class);
                            startActivity(intent);
                        }
                    }
                });
            } else {
                showErrorAlert();
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at Messaginglist loadListFirst2. " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public void updateList2(){
        try {
            List<ChatMsgSenders> chatmsgsendersAL = null;
            chatmsgsendersAL = DAO_Jobs.getChatMsgSendersList();
            MessagingListPostDataArr = new ArrayList<MessagingList_PostData>();
            MessagingList_PostData postdata;
            String UserIDX, UserName, JobsIDX;
            boolean Read = false;

            if (chatmsgsendersAL != null && chatmsgsendersAL.size() > 0) {
                /*Collections.sort(chatmsgsendersAL, new ChatMsgSenders.OrderbymsgDateLong());
                Collections.sort(chatmsgsendersAL, Collections.reverseOrder());*/
                for (int i = 0; i <= chatmsgsendersAL.size() - 1; i++) {
                    UserIDX = chatmsgsendersAL.get(i).UserIDX;
                    UserName = chatmsgsendersAL.get(i).UserName;
                    Read = chatmsgsendersAL.get(i).Read;
                    JobsIDX = chatmsgsendersAL.get(i).JobsIDX;
                    postdata = new MessagingList_PostData(JobsIDX, UserIDX, UserName, Read);
                    MessagingListPostDataArr.add(postdata);
                }
            }

            if (adapter != null && MessagingListPostDataArr.size() > 0)
                adapter.datas = MessagingListPostDataArr;
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Messaginglist updateList2. Exception: " + e.getMessage().toString() + "\nStack: " + s,"","","","");
        }
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
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(gcmp, "showBattChargeNotiPrefStr", asd.toString());
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity = null;
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at Messaginglist onPause unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GcmPreferences = null;
        adapter = null;
        MessagingListPostDataArr = null;
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at Messaginglist onDestroy unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
        DAO_Jobs.nullifystaticitems();

        messaginglist = null;
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at Messaginglist","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Messaginglist onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
