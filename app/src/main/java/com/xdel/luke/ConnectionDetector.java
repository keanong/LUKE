package com.xdel.luke;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

/**
 * Created by kean.ong on 7/4/2015.
 */
public class ConnectionDetector {

    private Context _context;

    public ConnectionDetector(Context context) {
        //this._context = context;
        this._context = myApp.getAppContext();
    }

    public boolean isConnectingToInternet() {
        boolean asd = false;
        try {
            ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            asd = true;
                        }
            }
        } catch (Exception e) {
            asd = true; //purposely return true, let the system deal w it
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ConnectionDetector isConnectingToInternet. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    /*public boolean WeakNetwork() {
        boolean asd = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network activeNetwork = cm.getActiveNetwork();
            NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(activeNetwork);
            int signalStrength = networkCapabilities.getSignalStrength(); //require min sdk 29, which is android 10, currently min sdk is 26, android 8
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ConnectionDetector WeakNetwork. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }*/

}
