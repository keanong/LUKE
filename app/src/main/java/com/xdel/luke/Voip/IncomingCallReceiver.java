package com.xdel.luke.Voip;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.sip.*;
import android.provider.Settings;
import android.util.Log;

import com.xdel.luke.objClasses.myApp;

/**
 * Listens for incoming SIP calls, intercepts and hands them off to WalkieTalkieActivity.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    /**
     * Processes the incoming call, answers it, and hands it over to the
     * WalkieTalkieActivity.
     * @param context The context under which the receiver is running.
     * @param intent The intent being received.
     */

    public static MediaPlayer player;
    @Override
    public void onReceive(Context context, Intent intent) {
        SipAudioCall incomingCall = null;
        final Context ctx = context;
        try {
            player = MediaPlayer.create(myApp.getAppContext(),
                    Settings.System.DEFAULT_RINGTONE_URI);

            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                @Override
                public void onRinging(SipAudioCall call, SipProfile caller) {
                    try {

                        player.start();
                        //call.answerCall(30);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    WalkieTalkieActivity.endCall(call);
                }
            };

            WalkieTalkieActivity wtActivity = (WalkieTalkieActivity) context;

            incomingCall = wtActivity.manager.takeAudioCall(intent, listener);
            player.start();
            //player.stop();
            /*incomingCall.answerCall(30);
            incomingCall.setSpeakerMode(true);
            incomingCall.startAudio();
            //incomingCall.setSpeakerMode(true);
            if(incomingCall.isMuted()) {
                incomingCall.toggleMute();
            }*/

            wtActivity.call = incomingCall;

            wtActivity.updateStatus(incomingCall);

        } catch (Exception e) {
            if (incomingCall != null) {
                incomingCall.close();
            }
        }
    }

}

