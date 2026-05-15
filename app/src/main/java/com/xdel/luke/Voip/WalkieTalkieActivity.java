package com.xdel.luke.Voip;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.*;
import android.net.sip.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;

import java.io.IOException;
import java.text.ParseException;

/**
 * Handles all calling, receiving calls, and UI interaction in the WalkieTalkie app.
 */
public class WalkieTalkieActivity extends AppCompatActivity implements View.OnTouchListener {

    public String sipAddress = null;

    public SipManager manager = null;
    public SipProfile me = null;
    public SipAudioCall call = null;
    public IncomingCallReceiver callReceiver;

    private static final int CALL_ADDRESS = 1;
    private static final int SET_AUTH_INFO = 2;
    private static final int UPDATE_SETTINGS_DIALOG = 3;
    private static final int HANG_UP = 4;
    private static MediaPlayer mp = null;
    private Button  btnCall,btnEndCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkie_talkie);

        try {
            Button btnCall = (Button) findViewById(R.id.btnCall);
            Button btnEndCall = (Button) findViewById(R.id.btnEndCall);

            ToggleButton pushToTalkButton = (ToggleButton) findViewById(R.id.pushToTalk);
            pushToTalkButton.setOnTouchListener(this);

            // Set up the intent filter.  This will be used to fire an
            // IncomingCallReceiver when someone calls the SIP address used by this
            // application.
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.SipDemo.INCOMING_CALL");
            callReceiver = new IncomingCallReceiver();
            this.registerReceiver(callReceiver, filter);

            TextView labelView = (TextView) findViewById(R.id.sipLabel);
            if (SipManager.isVoipSupported(this) && SipManager.isApiSupported(this)){

            }
            labelView.setText(SipManager.isVoipSupported(this) && SipManager.isApiSupported(this) ? "VOIP supported!" : "Not supported!");

            // "Push to talk" can be a serious pain when the screen keeps turning off.
            // Let's prevent that.
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            initializeManager();
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // When we get back from the preference setting Activity, assume
        // settings have changed, and re-login with new auth info.
        initializeManager();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, DrawerMainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.close();
        }

        closeLocalProfile();

        if (callReceiver != null) {
            this.unregisterReceiver(callReceiver);
        }
    }

    public void initializeManager() {
        try {
            if(manager == null) {
                manager = SipManager.newInstance(this);
            }

            initializeLocalProfile();
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    /**
     * Logs you into your SIP provider, registering this device as the location to
     * send SIP calls to for your SIP address.
     */
    public void initializeLocalProfile() {
        try {
            if (manager == null) {
                return;
            }

            if (me != null) {
                closeLocalProfile();
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String username = prefs.getString("namePref", "");
            String domain = prefs.getString("domainPref", "");
            String password = prefs.getString("passPref", "");

            if (username.length() == 0 || domain.length() == 0 || password.length() == 0) {
                showDialog(UPDATE_SETTINGS_DIALOG);
                return;
            }

            try {
                SipProfile.Builder builder = new SipProfile.Builder(username, domain);
                builder.setAuthUserName(username);
                builder.setPassword(password);
                me = builder.build();

                Intent i = new Intent();
                i.setAction("android.SipDemo.INCOMING_CALL");
                PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
                manager.open(me, pi, null);


                // This listener must be added AFTER manager.open is called,
                // Otherwise the methods aren't guaranteed to fire.

                manager.setRegistrationListener(me.getUriString(), new SipRegistrationListener() {
                    public void onRegistering(String localProfileUri) {
                        updateStatus("Registering with SIP Server...");
                    }

                    public void onRegistrationDone(String localProfileUri, long expiryTime) {
                        updateStatus("Ready");
                    }

                    public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                     String errorMessage) {
                        updateStatus("Registration failed.  Please check settings.");
                    }
                });
            } catch (ParseException pe) {
                updateStatus("Connection Error.");
            } catch (SipException se) {
                updateStatus("Connection error.");
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }

    }

    /**
     * Closes out your local profile, freeing associated objects into memory
     * and unregistering your device from the server.
     */
    public void closeLocalProfile() {
        if (manager == null) {
            return;
        }
        try {
            if (me != null) {
                manager.close(me.getUriString());
            }
        } catch (Exception ee) {
            //Log.d("WalkieTalkieActivity/onDestroy", "Failed to close local profile.", ee);
            String s = JobsHandler.getStackTraceString(ee);
            String ss = ee.getMessage().toString() + " Stack: " + s;
        }
    }

    /**
     * Make an outgoing call.
     */
    public void initiateCall() {

        updateStatus(sipAddress);

        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.
                @Override
                public void onCallEstablished(SipAudioCall call) {
                    if (mp != null) {
                        mp.stop();
                        mp = null;
                    }

                    call.setSpeakerMode(false);
                    call.startAudio();

                    if (call.isMuted()) {
                        call.toggleMute();
                    }
                    updateStatus(call);
                }

                @Override
                public void onRingingBack(final SipAudioCall call) {
                    try {
                        Resources res = getResources();

                        AssetFileDescriptor afd = res.openRawResourceFd(R.raw.call_ringingtone);
                        mp = new MediaPlayer();
                        mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                        mp.setLooping(true);
                        try {
                            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            mp.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mp.start();
                    } catch (IllegalArgumentException e1) {
                        e1.printStackTrace();
                    } catch (SecurityException e1) {
                        e1.printStackTrace();
                    } catch (IllegalStateException e1) {
                        e1.printStackTrace();
                    }

                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    updateStatus("Ready.");
                }
            };

            call = manager.makeAudioCall(me.getUriString(), sipAddress, listener, 30);

        }
        catch (Exception e) {
            //Log.i("WalkieTalkieActivity/InitiateCall", "Error when trying to close manager.", e);
            String s = JobsHandler.getStackTraceString(e);
            String ss = e.getMessage().toString() + " Stack: " + s;
            if (me != null) {
                try {
                    manager.close(me.getUriString());
                } catch (Exception ee) {
                    /*Log.i("WalkieTalkieActivity/InitiateCall",
                            "Error when trying to close manager.", ee);*/
                    String sss = JobsHandler.getStackTraceString(ee);
                    String ssss = ee.getMessage().toString() + " Stack: " + sss;
                    //ee.printStackTrace();
                }
            }
            if (call != null) {
                call.close();
            }
        }
    }

    public void initiateCall2() {

        updateStatus(sipAddress);

        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.
                @Override
                public void onCallEstablished(SipAudioCall call) {
                    if (mp != null) {
                        mp.stop();
                        mp = null;
                    }

                    call.setSpeakerMode(true);
                    call.startAudio();
                    //call.setSpeakerMode(true);
                    if (call.isMuted()) {
                        call.toggleMute();
                    }
                    //call.toggleMute();
                    updateStatus(call);
                }

                @Override
                public void onRingingBack(final SipAudioCall call) {
                    try {
                        Resources res = getResources();

                        AssetFileDescriptor afd = res.openRawResourceFd(R.raw.call_ringingtone);
                        mp = new MediaPlayer();
                        mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                        mp.setLooping(true);
                        try {
                            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            mp.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mp.start();
                    } catch (IllegalArgumentException e1) {
                        e1.printStackTrace();
                    } catch (SecurityException e1) {
                        e1.printStackTrace();
                    } catch (IllegalStateException e1) {
                        e1.printStackTrace();
                    }

                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    updateStatus("Ready.");
                }
            };

            call = manager.makeAudioCall(me.getUriString(), sipAddress, listener, 30);

        }
        catch (Exception e) {
            //Log.i("WalkieTalkieActivity/InitiateCall", "Error when trying to close manager.", e);
            String s = JobsHandler.getStackTraceString(e);
            String ss = e.getMessage().toString() + " Stack: " + s;
            if (me != null) {
                try {
                    manager.close(me.getUriString());
                } catch (Exception ee) {
                    /*Log.i("WalkieTalkieActivity/InitiateCall",
                            "Error when trying to close manager.", ee);*/
                    String sss = JobsHandler.getStackTraceString(ee);
                    String ssss = ee.getMessage().toString() + " Stack: " + sss;
                    //ee.printStackTrace();
                }
            }
            if (call != null) {
                call.close();
            }
        }
    }

    /**
     * Updates the status box at the top of the UI with a messege of your choice.
     * @param status The String to display in the status box.
     */
    public void updateStatus(final String status) {
        try {
            // Be a good citizen.  Make sure UI changes fire on the UI thread.
            this.runOnUiThread(new Runnable() {
                public void run() {
                    TextView labelView = (TextView) findViewById(R.id.sipLabel);
                    labelView.setText(status);
                }
            });
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }

    }

    /**
     * Updates the status box with the SIP address of the current call.
     * @param call The current, active call.
     */
    public void updateStatus(SipAudioCall call) {
        try {
            String useName = call.getPeerProfile().getDisplayName();
            if(useName == null) {
                useName = call.getPeerProfile().getUserName();
            }
            updateStatus(useName + "@" + call.getPeerProfile().getSipDomain());
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    /**
     * Updates whether or not the user's voice is muted, depending on whether the button is pressed.
     * @param v The View where the touch event is being fired.
     * @param event The motion to act on.
     * @return boolean Returns false to indicate that the parent view should handle the touch event
     * as it normally would.
     */
    public boolean onTouch(View v, MotionEvent event) {
        if (call == null) {
            return false;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN && call != null && call.isMuted()) {
            call.toggleMute();
        } else if (event.getAction() == MotionEvent.ACTION_UP && !call.isMuted()) {
            call.toggleMute();
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, CALL_ADDRESS, 0, "Call someone");
        menu.add(0, SET_AUTH_INFO, 0, "Edit your SIP Info.");
        menu.add(0, HANG_UP, 0, "End Current Call.");

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CALL_ADDRESS:
                showDialog(CALL_ADDRESS);
                break;
            case SET_AUTH_INFO:
                updatePreferences();
                break;
            case HANG_UP:
                endCall(call);
                /*if(call != null) {
                    try {
                        call.endCall();
                    } catch (SipException se) {
                        *//*Log.d("WalkieTalkie",
                                "Error ending call.", se);*//*
                        String s = JobsHandler.getStackTraceString(se);
                        String ss = se.getMessage().toString() + " Stack: " + s;
                    }
                    call.close();
                }
                if (IncomingCallReceiver.player != null) {
                    IncomingCallReceiver.player.stop();
                    IncomingCallReceiver.player = null;
                }*/
                break;
        }
        return true;
    }

    public void call(View v) {
        showDialog(CALL_ADDRESS);
    }

    public void endcall(View v) {
        endCall(call);
    }

    public static void endCall(final SipAudioCall acall) {
        if(acall != null) {
            try {
                acall.endCall();
            } catch (SipException se) {
                        /*Log.d("WalkieTalkie",
                                "Error ending call.", se);*/
                String s = JobsHandler.getStackTraceString(se);
                String ss = se.getMessage().toString() + " Stack: " + s;
            }
            acall.close();
        }
        if (mp != null) {
            mp.stop();
            mp = null;
        }
        if (IncomingCallReceiver.player != null) {
            IncomingCallReceiver.player.stop();
            IncomingCallReceiver.player = null;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CALL_ADDRESS:

                LayoutInflater factory = LayoutInflater.from(this);
                final View textBoxView = factory.inflate(R.layout.call_address_dialog, null);
                return new AlertDialog.Builder(this)
                        .setTitle("Call Someone.")
                        .setView(textBoxView)
                        .setPositiveButton(
                                android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        EditText textField = (EditText)
                                                (textBoxView.findViewById(R.id.calladdress_edit));
                                        //sipAddress = "sip:" + textField.getText().toString() + "@27.54.42.188";
                                        sipAddress = "sip:" + textField.getText().toString() + "@v.xdel.com";
                                        initiateCall();

                                    }
                                })
                        .setNegativeButton(
                                android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Noop.
                                    }
                                })
                        .create();

            case UPDATE_SETTINGS_DIALOG:
                return new AlertDialog.Builder(this)
                        .setMessage("Please update your SIP Account Settings.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                updatePreferences();
                            }
                        })
                        .setNegativeButton(
                                android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Noop.
                                    }
                                })
                        .create();
        }
        return null;
    }

    public void updatePreferences() {
        try {
            Intent settingsActivity = new Intent(getBaseContext(),
                    SipSettings.class);
            startActivity(settingsActivity);
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }

    }
}

