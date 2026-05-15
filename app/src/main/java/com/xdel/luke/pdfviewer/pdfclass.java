package com.xdel.luke.pdfviewer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.widget.Toast;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

public class pdfclass {

    private static File getFile(Context context, String fileName) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File storageDir = context.getExternalFilesDir(null);
        return new File(storageDir, fileName);
    }

    private static Uri getFileUri(Context context, String fileName) {
        File file = getFile(context, fileName);
        return FileProvider.getUriForFile(context, "com.xdel.luke", file);
    }

    public static boolean pdfExist(){
        boolean asd = false;
        String filename = "ip_unbricking.pdf";
        try {
            File myFile = getFile(myApp.getAppContext(), filename);
            asd = myFile.exists();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at pdfclass pdfExist. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public static void pdfVersionCheck() {
        boolean downloadNewVersionPDFboo = false;
        try {
            long existingFileModifiedDateLong = getExistingFileModifiedDateLong();
            long serverFileLastModifiedDateLong = getServerFileModifiedDateLong();
            if (existingFileModifiedDateLong > -1 && serverFileLastModifiedDateLong > -1) {
                downloadNewVersionPDFboo = downloadNewVersionPDF(existingFileModifiedDateLong, serverFileLastModifiedDateLong);
                if (downloadNewVersionPDFboo) {
                    downloadpdf();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at pdfclass pdfVersionCheck. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private static void closeHttpURLConnection(HttpURLConnection con) throws IOException {
        try {
            if (con != null) {
                con.disconnect();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at pdfclass closeHttpURLConnection. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private static long getServerFileModifiedDateLong() {
        long fmd = -1;
        HttpURLConnection urlConnection = null;
        try {
            String urlpath = "http://www.xdel.com/lukeipunbrick";
            URL url = new URL(urlpath);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(50000);
            urlConnection.setConnectTimeout(70000);
            urlConnection.setRequestMethod("HEAD");
            urlConnection.setRequestProperty("connection", "close");
            urlConnection.connect();
            fmd = urlConnection.getLastModified();
            urlConnection.disconnect();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at pdfclass getServerFileModifiedDateLong. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            if (urlConnection != null) {
                closeHttpURLConnection(urlConnection);
            }
        } finally {
            return fmd;
        }
    }

    private static long getExistingFileModifiedDateLong() {
        long fmd = -1;
        String filename = "ip_unbricking.pdf";
        try {
            File myFile = getFile(myApp.getAppContext(), filename);
            if (myFile.exists()) {
                fmd = myFile.lastModified(); //ms
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at pdfclass getExistingFileModifiedDateLong. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return fmd;
        }
    }

    private static boolean downloadNewVersionPDF(long lastFMD, long newFMD) {
        boolean asd = false;
        Calendar lastFMDcal = Calendar.getInstance();
        Calendar newFMDcal = Calendar.getInstance();
        Date lastFMDdate;
        Date newFMDdate;
        try {
            //0 comes when two date are same,
            //1 comes when date1 is higher then date2
            //-1 comes when date1 is lower then date2
            lastFMDcal.setTimeInMillis(lastFMD);
            newFMDcal.setTimeInMillis(newFMD);
            lastFMDdate = lastFMDcal.getTime();
            newFMDdate = newFMDcal.getTime();
            asd = (newFMDdate.compareTo(lastFMDdate)) == 1;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at pdfclass downloadNewVersionPDF. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

    public static void openpdf() {
        try {
            String AUTHORITY = "com.xdel.luke";
            String filename = "ip_unbricking.pdf";
            /*File externalStorageDir = Environment.getExternalStorageDirectory();
            File myFile = new File(externalStorageDir, filename);
            Uri path = FileProvider.getUriForFile(this, "com.xdel.luke", myFile);*/

            File myFile = getFile(myApp.getAppContext(), filename);
            Uri path = getFileUri(myApp.getAppContext(), filename);
            if (myFile.exists()) {
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.putExtra(Intent.EXTRA_STREAM, path);
                target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                target.setDataAndType(path, "application/pdf");
                try {
                    //myApp.currentActivity.startActivity(target);
                    Intent intent = Intent.createChooser(target, "IP Unbricking");
                    myApp.currentActivity.startActivity(intent);
                }
                catch (ActivityNotFoundException e) {
                    objGlobalMethods.writeToFile("ActivityNotFoundException at pdfclass openPDFViewerIntent. Exception: " + e.getMessage() + "\nNo Application Available to View PDF", "", "", "", "");
                    Toast.makeText(myApp.currentActivity,
                            "No Application Available to View PDF",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at pdfclass openpdf. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    //copy file from XDelQueue folder to sdcard/android/data/com.xdel.luke/files
    //oso can be found in storage/self/primary/android/data/com.xdel.luke/files
    public static void savefile(){
        String copiedFileFilePath = "";
        boolean succ = false;
        try {
            String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
            File sourceLocation = new File(sdCard + "/XDelQueue/ip_unbricking.pdf");
            String filename = sourceLocation.getName();
            //save file into root folder data that contains app, data,local folders.
            //will save file into data/data/com.xdel.luke/files
            File storageDir = myApp.getAppContext().getExternalFilesDir(null);
            File targetLocation = new File(storageDir, filename);
            //File targetLocation = new File(myApp.getAppContext().getFilesDir() + "/" + filename);
            boolean targetLocation_mkdirBoolsucc = targetLocation.exists();
            boolean targetLocationFileCreatedsucc = false;
            boolean dirExists = false;
            /*if (!targetLocation.exists()) {
                File dir = new File(storageDir, filename);
                dirExists = dir.isDirectory();
                targetLocation_mkdirBoolsucc = !dirExists ? targetLocation.getParentFile().mkdirs() : true;
            }
            if (targetLocation_mkdirBoolsucc) {
                targetLocation.createNewFile();
                targetLocationFileCreatedsucc = targetLocation.exists();
            }*/
            targetLocation.createNewFile();
            targetLocationFileCreatedsucc = targetLocation.exists();

            // 1 = move the file, 2 = copy the file
            int actionChoice = 2;
            // moving the file to another directory
            if (actionChoice == 1 && targetLocationFileCreatedsucc) {

                if (sourceLocation.renameTo(targetLocation)) {

                    succ = true;
                } else {
                    succ = false;

                }
            }
            // we will copy the file
            else {
                // make sure the target file exists
                if (sourceLocation.exists() && targetLocationFileCreatedsucc) {
                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);
                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    succ = true;
                } else {
                    succ = false;
                }
            }
        } catch (Exception e) {
            //prompt
            String s = e.getMessage().toString();
        }
    }

    public static void downloadpdf() {
        int count;
        InputStream input = null;
        OutputStream output = null;
        try {
            URL url = new URL("http://www.xdel.com/lukeipunbrick");
            URLConnection connection = url.openConnection();
            connection.connect();
            objGlobalMethods.writeToFile("Begin download ipunbrick.", "", "", "", "");
            // getting file length
            int lengthOfFile = connection.getContentLength();
            // input stream to read file - with 8k buffer
            input = new BufferedInputStream(url.openStream(), 8192);
            output = new FileOutputStream(myApp.getAppContext().getExternalFilesDir(null) + "/ip_unbricking.pdf");

            byte data[] = new byte[4096];
            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                ////publishProgress((int) ((total * 100) / lengthOfFile));
                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at pdfclass downloadpdf. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
        }
    }

}
