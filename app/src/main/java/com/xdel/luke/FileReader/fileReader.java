package com.xdel.luke.FileReader;

import android.os.Environment;

import com.google.gson.Gson;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class fileReader {

    public static boolean isTempLoggerScannerDevice() {
        boolean asd = false;
        try {
            String fs = readTxtFromFile("XDelCommunalDeviceFile.txt");
            if(!objGlobalMethods.IsEmptyString(fs)) {
                objCommunalSettings cs = new Gson().fromJson(fs, objCommunalSettings.class);
                asd = cs != null && cs.TempLoggerScanning;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at fileReader isTempLoggerScannerDevice. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static String readTxtFromFile(String filename) throws IOException {
        String res = "";
        try {
            String sdCard = Environment.getExternalStorageDirectory().toString();
            File targetLocation = new File(sdCard + "/XDelQueue/" + filename);
            boolean asd = targetLocation.exists();
            if (asd) {

                StringBuilder text = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(targetLocation));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                }
                br.close();
                res = text.toString();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at fileReader readTxtFromFile. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return res;
        }
    }


}
