package com.xdel.luke.BCnSVC;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SendQueuedEmail_IntentService extends IntentService {
    ConnectionDetector cd;
    private static Intent SendQueuedEmailIntentService;

    public SendQueuedEmail_IntentService() {
        super("SendQueuedEmail_IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Context ctx = myApp.getAppContext();
            try {
                //cd = new ConnectionDetector(ctx);
                cd = new ConnectionDetector(null);
                if (cd.isConnectingToInternet()) {
                    if (!JobsHandler.backEndIsRunning) {
                        objGlobalMethods.writeToFile("SendQueuedEmail_IntentService onHandleIntent started.","","","","");
                        JobsHandler.processQueuedEmailsToSend0 p = new JobsHandler.processQueuedEmailsToSend0();
                        p.execute();
                    } else {
                        objGlobalMethods.writeToFile("SendQueuedEmail_IntentService onHandleIntent started. backEndIsRunning. Not processing emails here.","","","","");
                    }
                } else {
                    objGlobalMethods.writeToFile("SendQueuedEmail_IntentService onHandleIntent cannot run due no network.","","","","");
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at SendQueuedEmail_IntentService onHandleIntent. Exception: " + e.getMessage(),"","","","");
            }
        } else {
            objGlobalMethods.writeToFile("SendQueuedEmail_IntentService onHandleIntent intent == null.","","","","");
        }
    }

}
