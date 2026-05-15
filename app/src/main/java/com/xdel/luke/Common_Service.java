package com.xdel.luke;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

public class Common_Service extends IntentService {
    public Common_Service() {
        super("Common_Service");
    }

    private MediaPlayer player;

    @Override
    protected void onHandleIntent(Intent intent) {
        if(FCMservice.notiNotRead){
            playTone();
        }
    }

    private void stopPlayingTone(){
        try {
            if (player != null) {
                player.reset();
                //player.prepare();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at Common_Service stopPlayingTone. Exception: " + e.getMessage(),"","","","");
        }
    }

    private void playTone(){
        /*player = MediaPlayer.create(Common_Service.this, R.raw.hit);
        player.start();*/
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(Common_Service.this,  Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.hit));
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at Common_Service playTone. Exception: " + e.getMessage(),"","","","");
        }
    }

}
