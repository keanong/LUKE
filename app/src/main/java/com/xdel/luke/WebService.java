package com.xdel.luke;

/**
 * Created by kean.ong on 8/4/2015.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.google.gson.Gson;
import com.xdel.luke.Caging.objCage_InResult;
import com.xdel.luke.Caging.objCan_Cage_InResult;
import com.xdel.luke.Caging.objGet_CageResult;
import com.xdel.luke.Caging.objL2_Cage_OutResult;
import com.xdel.luke.Caging.objL2_Get_CageResult;
import com.xdel.luke.Caging.objL2_Verify_CageResult;
import com.xdel.luke.Caging.objL2_Verify_Cage_ItemResult;
import com.xdel.luke.Ext.L2_RequestExtensionResult;
import com.xdel.luke.HRM_Leave.objLeaveRecord;
import com.xdel.luke.HRM_Leave.objLeaveRecordSubmit;
import com.xdel.luke.HRM_Leave.objLeaveType;
import com.xdel.luke.HandTakeOver.HOTO;
import com.xdel.luke.HandTakeOver.L2_Get_WorkScheduleResult;
import com.xdel.luke.Locker.L2_LA_GetUserResult;
import com.xdel.luke.Locker.L2_LA_UpdateMobileResult;
import com.xdel.luke.Vrp.JobQueue;
import com.xdel.luke.inventory.Inventory;
import com.xdel.luke.inventory.L2_UploadStockVerificationListResult;
import com.xdel.luke.inventory.StockVerificationList;
import com.xdel.luke.messagingClasses.objChatMessage;
import com.xdel.luke.mpd.MPD_Attach_Jobs_To_MPDGroupIDXResult;
import com.xdel.luke.mpd.MPD_Detach_Jobs_From_MPDGroupIDXResult;
import com.xdel.luke.mpd.MPD_Group;
import com.xdel.luke.objClasses.CreditCard_PostData;
import com.xdel.luke.objClasses.NFCTag;
import com.xdel.luke.objClasses.QuickAdd;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objADunningStatus;
import com.xdel.luke.objClasses.objAddressStructure;
import com.xdel.luke.objClasses.objAddressStructure2;
import com.xdel.luke.objClasses.objAuthDriverResult;
import com.xdel.luke.objClasses.objAuthRequest;
import com.xdel.luke.objClasses.objCODjobInfo;
import com.xdel.luke.objClasses.objCabinet;
import com.xdel.luke.objClasses.objChangePasswordResult;
import com.xdel.luke.objClasses.objContactStructure;
import com.xdel.luke.objClasses.objContactStructure2;
import com.xdel.luke.objClasses.objDriverPointResult;
import com.xdel.luke.objClasses.objDriverPointSummary;
import com.xdel.luke.objClasses.objGC_CustomQueue;
import com.xdel.luke.objClasses.objGetTravelDistanceResult;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objJobInfo2;
import com.xdel.luke.objClasses.objL2QuickAddJobsResult;
import com.xdel.luke.objClasses.objRecipientType;
import com.xdel.luke.objClasses.objRtnGPS;
import com.xdel.luke.objClasses.objStringWTag;
import com.xdel.luke.objClasses.objUpdateDOCOD;
import com.xdel.luke.objClasses.objUpdateInfo;
import com.xdel.luke.objClasses.test;
import com.xdel.luke.objClasses.xwsBase;
import com.xdel.luke.sqlite.dbObjTradeInList;
import com.xdel.luke.templogger.JobTemperaturePayload;
import com.xdel.luke.templogger.L2_GetJobTemperatureResult;
import com.xdel.luke.templogger.L2_GetJobTemperaturesResult;
import com.xdel.luke.templogger.TemperaturePayload;
import com.xdel.luke.templogger.Upload_TemperatureResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class WebService {
    private static String NAMESPACE = "https://www.xdel.com/lukews/";
    private static String ipURL = "https://ws.xdel.com/lukews/luke.svc/"; //27.54.42.182 //Make sure you changed IP address
    private static String ipURL1_ = "https://ws.xdel.com/lukews/luke.svc/"; //27.54.42.182 //Make sure you changed IP address
    private static String ipURL2_ = "https://ws2.xdel.com/lukews/luke.svc/"; //27.54.42.185
    private static String ipURL3_ = "https://www.xdel.com/lukews/luke.svc/"; //27.54.42.189
    private static String ipURL4_ = "https://223.25.70.6/lukews/luke.svc/"; //
    private static String ipURL5_ = "https://223.25.70.9/lukews/luke.svc/"; //
    private static String localURL = "https://192.168.0.20/lukews/luke.svc/"; //when connected to XDEl WIFI
    private static String SOAP_ACTION = "https://www.xdel.com/lukews/";

    /*private static String NAMESPACE = "http://www.xdel.com/lukews/";
    private static String ipURL = "http://ws.xdel.com/lukews/luke.svc/"; //27.54.42.182 //Make sure you changed IP address
    private static String ipURL1_ = "http://ws.xdel.com/lukews/luke.svc/"; //27.54.42.182 //Make sure you changed IP address
    private static String ipURL2_ = "http://ws2.xdel.com/lukews/luke.svc/"; //27.54.42.185
    private static String ipURL3_ = "http://www.xdel.com/lukews/luke.svc/"; //27.54.42.189
    private static String ipURL4_ = "http://223.25.70.6/lukews/luke.svc/"; //
    private static String ipURL5_ = "http://223.25.70.9/lukews/luke.svc/"; //
    private static String localURL = "http://192.168.0.20/lukews/luke.svc/"; //when connected to XDEl WIFI
    private static String SOAP_ACTION = "http://www.xdel.com/lukews/";*/

    private static String url4ip = "223.25.70.6/*";
    private static String url5ip = "223.25.70.9/*";
    private static String url6ip = "223.25.70.4/*";

    /*private static JSONObject jobj;*/

    static String SOAP_ACTION2 = "http://tempuri.org/";
    static String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
    //static String SOAP_ADDRESS = "https://www.xdel.com/keantest/ws.asmx/";
    static String SOAP_ADDRESS = "https://www.xdel.com/keantest/ws.svc/";

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static void changeIPURL() {
        final String initialURL = ipURL;
        String changedURL = "";

        if (ipURL == ipURL1_) {
            changedURL = ipURL2_;
        } else if (ipURL == ipURL2_) {
            changedURL = ipURL3_;
        } else if (ipURL == ipURL3_) {
            changedURL = ipURL4_;
        } else if (ipURL == ipURL4_) {
            changedURL = ipURL5_;
        } else if (ipURL == ipURL5_) {
            changedURL = ipURL1_;
        } else if (ipURL == localURL) {
            changedURL = ipURL1_;
        } else {
            changedURL = ipURL;
        }

        /*if (ipURL == ipURL1_) {
            changedURL = ipURL2_;
        } else if (ipURL == ipURL2_) {
            changedURL = ipURL3_;
        } else if (ipURL == ipURL3_) {
            changedURL = ipURL1_;
        } else if (ipURL == localURL) {
            changedURL = ipURL1_;
        } else {
            changedURL = ipURL;
        }*/

        ipURL = changedURL;
        objGlobalMethods.writeToFile("ipURL changed from " + initialURL + " to " + changedURL, "", "", "", "");
    }

    public static int keantest3(test o) {
        JSONObject objAuthDriverResultJsonObj;
        int i = 0;
        String addWResult = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("o", o);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            objAuthDriverResultJsonObj = getJSONObj00("AddW", jsonParameters);
            addWResult = objAuthDriverResultJsonObj.getString("AddWResult").toString();
            if (!addWResult.equals("")) {
                i = Integer.parseInt(addWResult);
            }
        } catch (Exception e) {
            i = 0;
        } finally {
            return i;
        }
    }

    public static int keantest2(String webMethName) {
        int i = 0;
        try {
            test t = new test(true, new int[] {1,2,3,4});
            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,webMethName);
            PropertyInfo pi = new PropertyInfo();
            pi.setName("o");
            pi.setValue(t);
            pi.setType(test.class);
            request.addProperty(pi);
            /*pi = new PropertyInfo();
            pi.setName("y");
            pi.setValue(2);
            pi.setType(Integer.class);
            request.addProperty(pi);*/

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
            Object response = null;
            httpTransport.call(SOAP_ACTION2 + webMethName, envelope);
            response = envelope.getResponse();
            i = Integer.parseInt(response.toString());
        } catch (Exception e) {
            i = 0;
        } finally {
            return i;
        }
    }

    public static String keantest(String webMethName) {
        String L2_SubmitHOTOResult = "na";
        JSONObject JsonObj;
        try {
            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,webMethName);
            PropertyInfo pi=new PropertyInfo();
            pi.setName("x");
            pi.setValue(1);
            pi.setType(Integer.class);
            request.addProperty(pi);
            pi=new PropertyInfo();
            pi.setName("y");
            pi.setValue(2);
            pi.setType(Integer.class);
            request.addProperty(pi);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
            Object response = null;
            httpTransport.call(SOAP_ACTION2 + webMethName, envelope);
            response = envelope.getResponse();
            L2_SubmitHOTOResult = response.toString();
        } catch (Exception e) {
            L2_SubmitHOTOResult = "error";
        } finally {
            return L2_SubmitHOTOResult;
        }
    }

    public static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() { @Override public boolean verify(String hostname, SSLSession session) { return true; } });
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getJSONObj00(String webMethName, String jsonParameters) {
        HttpURLConnection urlConnection;
        String result = null;
        JSONObject jobj = null;
        Context ctx = myApp.getAppContext();
        try {
            URL url = new URL(SOAP_ADDRESS + webMethName);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setReadTimeout(objGlobalMethods.IsEqualString(webMethName, "L2_CreateProblem") ? 50000 : 30000);
            urlConnection.setConnectTimeout(objGlobalMethods.IsEqualString(webMethName, "L2_CreateProblem") ? 70000 : 60000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("connection", "close");
            urlConnection.connect();

            //Write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(jsonParameters);
            writer.close();
            outputStream.close();

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();
            result = sb.toString();

            jobj = new JSONObject(result);

        } catch (UnsupportedEncodingException e) {
            jobj = null;
            objGlobalMethods.writeToFile("Exception UnsupportedEncodingException caught at WebService getJSONObj0 calling method: " + webMethName + ". Exception: " + e.toString(), "", "", "", "");
        } catch (IOException e) {
            jobj = null;
            if (webMethName == "Get_Cage" || webMethName == "L2_Verify_Cage" || webMethName == "L2_Get_Cage" || webMethName == "Can_Cage_In" || webMethName == "Cage_In"
                    || webMethName == "L2_Verify_Cage_Item" || webMethName == "L2_Cage_Out") {
                objGlobalMethods.writeToFile("Exception IOException caught at WebService getJSONObj0 calling method: " + webMethName + ". Exception: " + e.toString(), "", "", "", "");
            }
            changeIPURL();
        } catch (Exception e) {
            jobj = null;
            objGlobalMethods.writeToFile("Exception Exception caught at WebService getJSONObj0 calling method: " + webMethName + ". Exception: " + e.toString(), "", "", "", "");
        } finally {
            return jobj;
        }
    }

    public static JSONObject getJSONObj0(String webMethName, String jsonParameters) {
        HttpURLConnection urlConnection;
        //HttpsURLConnection urlConnection;
        String result = null;
        JSONObject jobj = null;
        Context ctx = myApp.getAppContext();
        HostnameVerifier hnv = null;
        SSLContext sc = null;
        try {
            //Connect
            //ipURL = ipURL1_;
            try {
                ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
                NetworkInfo ni = connectivity.getActiveNetworkInfo();
                if (ni != null) {
                    if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                        if (connectivity == null) {
                            connectivity = (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
                        }
                        NetworkInfo myNetworkInfo = connectivity.getActiveNetworkInfo();
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (myNetworkInfo != null && myNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI && myNetworkInfo.isConnected()) {
                                WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                WifiInfo wifi = wifiManager.getConnectionInfo();
                                if (wifi != null && wifi.getSSID().toLowerCase().equals("\"xdel\"")) {
                                    ipURL = localURL;
                                }
                            }
                        } else {
                            //below is for andorid 7 and below
                            if (myNetworkInfo != null && myNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI && myNetworkInfo.isConnected()) {
                                if (myNetworkInfo.getExtraInfo() != null && myNetworkInfo.getExtraInfo().toLowerCase().equals("\"xdel\"")) {
                                    ipURL = localURL;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at WebService getJSONObj0 checking xdel wifi connection. Exception: " + e.getMessage(),"","","","");
            }

            //objGlobalMethods.writeToFile("ipURL in use: " + ipURL,"","","","");
            //ipURL = ipURL4_;

            URL url = new URL(ipURL + webMethName);

            /*if (ipURL.equals(ipURL4_) || ipURL.equals(ipURL5_) || ipURL.equals(localURL)) {
                disableSSLCertificateChecking();
            } else {
                disableSSLCertificateChecking();
            }*/

            disableSSLCertificateChecking();

            //urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection = (HttpsURLConnection) url.openConnection();


            urlConnection.setDoOutput(true);
            //urlConnection.setReadTimeout(10000);
            //urlConnection.setConnectTimeout(15000);
            //urlConnection.setReadTimeout(objGlobalMethods.IsEqualString(webMethName, "L2_CreateProblem") ? 50000 : 30000);
            urlConnection.setReadTimeout(objGlobalMethods.IsEqualString(webMethName, "L2_CreateProblem") ? 70000 : 60000);
            urlConnection.setConnectTimeout(objGlobalMethods.IsEqualString(webMethName, "L2_CreateProblem") ? 70000 : 60000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("connection", "close");
            urlConnection.connect();

            //Write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(jsonParameters);
            writer.close();
            outputStream.close();

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();
            result = sb.toString();

            jobj = new JSONObject(result);

        } catch (UnsupportedEncodingException e) {
            jobj = null;
            objGlobalMethods.writeToFile("Exception UnsupportedEncodingException caught at WebService getJSONObj0 calling method: " + webMethName + ". Exception: " + e.toString(), "", "", "", "");
        } catch (IOException e) {
            jobj = null;
            /*if (webMethName == "Get_Cage" || webMethName == "L2_Verify_Cage" || webMethName == "L2_Get_Cage" || webMethName == "Can_Cage_In" || webMethName == "Cage_In"
                    || webMethName == "L2_Verify_Cage_Item" || webMethName == "L2_Cage_Out") {
                objGlobalMethods.writeToFile("Exception IOException caught at WebService getJSONObj0 calling method: " + webMethName + ". Exception: " + e.toString(), "", "", "", "");
            }*/
            String s = JobsHandler.getStackTraceString(e);
            if (!objGlobalMethods.IsEmptyString(webMethName))
                objGlobalMethods.writeToFile("IOException caught at WebService getJSONObj0 calling method: " + webMethName + ". Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            changeIPURL();
        } catch (Exception e) {
            jobj = null;
            String s = JobsHandler.getStackTraceString(e);
            /*if (webMethName == "Get_Cage" || webMethName == "L2_Verify_Cage" || webMethName == "L2_Get_Cage" || webMethName == "Can_Cage_In" || webMethName == "Cage_In"
                    || webMethName == "L2_Verify_Cage_Item" || webMethName == "L2_Cage_Out") {
                objGlobalMethods.writeToFile("Exception caught at WebService getJSONObj0 calling method: " + webMethName + ". Exception: " + e.toString(), "", "", "", "");
            }*/
            if (!objGlobalMethods.IsEmptyString(webMethName))
                objGlobalMethods.writeToFile("Exception caught at WebService getJSONObj0 calling method: " + webMethName + ". Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            changeIPURL();
            objGlobalMethods.writeToFile("Exception Exception caught at WebService getJSONObj0 calling method: " + webMethName + ". Exception: " + e.toString(), "", "", "", "");
        } finally {
            return jobj;
        }
    }

    public static JSONObject getRoutingJSONObj(String webMethName, String jsonParameters) {
        String jsonstr = "";
        HttpURLConnection urlConnection;
        String result = null;
        JSONObject Routingjobj = null;

        try {
            //Connect
            URL url = new URL(ipURL + webMethName);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            /*urlConnection.setReadTimeout(10000 *//* milliseconds *//*);
            urlConnection.setConnectTimeout(15000 *//* milliseconds *//*);*/
            urlConnection.setReadTimeout(30000 /* milliseconds */);
            urlConnection.setConnectTimeout(60000 /* milliseconds */);

            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            //Write
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(jsonParameters);
            writer.close();
            outputStream.close();

            //Read
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();
            result = sb.toString();

            Routingjobj = new JSONObject(result);
        } catch (UnsupportedEncodingException e) {
            Routingjobj = null;
        } catch (IOException e) {
            Routingjobj = null;
        } catch (Exception e) {
            Routingjobj = null;
        } finally {
            return Routingjobj;
        }
    }

    public static String checkStatus(String SearchCriteria, String webMethName) {
        String status = "N.A.";

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", "40115DFEA6C4982D386B4B284BCE4A7919D30D70");
            postParameters.put("ASearchString", SearchCriteria);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("CheckStatus", jsonParameters);
            status = jobj.getString("CheckStatusResult").toString();
        } catch (Exception e) {
            CheckStatusActivity.errored = true;
        } finally {
            return status;
        }
    }

    public static objAuthDriverResult getGCLoginStatus(objAuthRequest req){
        JSONObject objAuthDriverResultJsonObj;
        objAuthDriverResult res = null;
        String objAuthDriverResultStr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("AAuthRequest", req);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            objAuthDriverResultJsonObj = getJSONObj0("L2_AuthDriver", jsonParameters);
            objAuthDriverResultStr = objAuthDriverResultJsonObj.getString("L2_AuthDriverResult").toString();
            if (!objAuthDriverResultStr.equals("")) {
                res = gson.fromJson(objAuthDriverResultStr, objAuthDriverResult.class);
            }
        } catch (Exception e) {
            MainActivity.errored = true;
            res = null;
            objGlobalMethods.writeToFile("Exception at WebService getGCLoginStatus. Exception: " + e.getMessage(),"","","","");
        } finally {
            return res;
        }
    }

    public static long GetLoginStatusA(String userName, String passWord, String RegID, String aAUTC, double ALat, double ALng, int Accuracy) {
        long L2_AuthDriverResult = 0;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("LoginName", userName);
            postParameters.put("Password", passWord);
            postParameters.put("RegID", RegID);
            postParameters.put("AUTC", aAUTC);
            postParameters.put("Lat", ALat);
            postParameters.put("Lng", ALng);
            postParameters.put("Accuracy", Accuracy);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("L2_Auth_DriverA", jsonParameters);
            L2_AuthDriverResult = Long.parseLong(jobj.getString("L2_Auth_DriverAResult").toString());
        } catch (Exception e) {
            MainActivity.errored = true;
        } finally {
            return L2_AuthDriverResult;
        }
    }

    public static long GetLoginStatusQR(String LoginName, String Password, String RegID, String AUTC, String QRCode) {
        long L2_Auth_DriverQRResult = 0;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("LoginName", LoginName);
            postParameters.put("Password", Password);
            postParameters.put("RegID", RegID);
            postParameters.put("AUTC", AUTC);
            postParameters.put("QRCode", QRCode);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("L2_Auth_DriverQR", jsonParameters);
            L2_Auth_DriverQRResult = Long.parseLong(jobj.getString("L2_Auth_DriverQRResult").toString());
        } catch (Exception e) {
            MainActivity.errored = true;
        } finally {
            return L2_Auth_DriverQRResult;
        }
    }

    public static long getHomesLoginStatus(String userName, String passWord, String webMethName) {
        long AuthUserResult = 0;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("LoginName", userName);
            postParameters.put("Password", passWord);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("AuthUser", jsonParameters);
            AuthUserResult = Long.parseLong(jobj.getString("AuthUserResult").toString());
        } catch (Exception e) {
            MainActivity.errored = true;
        } finally {
            return AuthUserResult;
        }
    }

    public static boolean UnAuthDriver(String RegID, String aAUTC, double ALat, double ALng) {
        boolean L2_UnAuthDriverResult = false;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("RegID", RegID);
            postParameters.put("AUTC", aAUTC);
            postParameters.put("Lat", ALat);
            postParameters.put("Lng", ALng);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("L2_UnAuth_Driver", jsonParameters);
            L2_UnAuthDriverResult = Boolean.parseBoolean(jobj.getString("L2_UnAuth_DriverResult").toString());
        } catch (Exception e) {
            //Dashboard2Activity.errored = true;
            DrawerMainActivity.errored = true;
            L2_UnAuthDriverResult = false;
        } finally {
            return L2_UnAuthDriverResult;
        }
    }

    public static JSONObject GetStatusCodesLibArrayList(String ARegID, String webMethName) {
        ArrayList AL = null;
        JSONObject JsonObj = null;
        JSONObject obj = null;
        JSONArray jarr = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JsonObj = getJSONObj0("L2_GetStatusCodeLibraryEx", jsonParameters);
            obj = JsonObj.getJSONObject("L2_GetStatusCodeLibraryExResult");

        } catch (Exception e) {
            JsonObj = null;
        } finally {
            return JsonObj;
        }
    }

    public static JSONObject GetGetStatusCodeLibraryL2ArrayList(String ARegID) {
        ArrayList AL = null;
        JSONObject JsonObj = null;
        JSONObject obj = null;
        JSONArray jarr = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JsonObj = getJSONObj0("L2_GetStatusCodeLibraryL2", jsonParameters);
            obj = JsonObj.getJSONObject("L2_GetStatusCodeLibraryL2Result");

        } catch (Exception e) {
            JsonObj = null;
        } finally {
            return JsonObj;
        }
    }

    public static objRecipientType[] GetRecipientTypes(String ARegID, String webMethName) {
        objRecipientType[] RecipientTypesArray = null;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("L2_GetRecipientTypes", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_GetRecipientTypesResult");

            RecipientTypesArray = gson.fromJson(jarr.toString(), objRecipientType[].class);
        } catch (Exception e) {
            JobsHandler.downLoadRecipientTypesWSerrored = true;
        } finally {
            return RecipientTypesArray;
        }
    }

    public static String[] GetDriverJobsHash(String ARegID, String webMethName) {
        String[] DriverJobsHash = null;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("L2_GetDriverJobsHash", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_GetDriverJobsHashResult");
            DriverJobsHash = gson.fromJson(jarr.toString(), String[].class);
            //int size = DriverJobsHash.length;
        } catch (Exception e) {
            //Dashboard2Activity.JobsCategoryHasherrored = true;
            DrawerMainActivity.JobsCategoryHasherrored = true;
            JobsHandler.JobsCategoryHasherrored = true;
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at WebService GetDriverJobsHash. " + e.getMessage().toString(), "", "", "", "");
            DriverJobsHash = null;
        } finally {
            return DriverJobsHash;
        }
    }

    public static ArrayList GetDriverJobsAL(String ARegID, int RetrieveType, String webMethName) {
        ArrayList DriverJobsArr = new ArrayList();
        Long[] DriverJobsIDXArray = null;
        int DriverJobsCount = 0;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ARetrieveType", RetrieveType);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("L2_GetDriverJobsIDX", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_GetDriverJobsIDXResult");
            DriverJobsIDXArray = gson.fromJson(jarr.toString(), Long[].class);
            DriverJobsCount = DriverJobsIDXArray.length;
            DriverJobsArr.add(DriverJobsIDXArray);
            DriverJobsArr.add(DriverJobsCount);
        } catch (Exception e) {
            DriverJobsArr = null;
            objGlobalMethods.writeToFile("Exception at WebService GetDriverJobsAL when RetrieveType = " + String.valueOf(RetrieveType) + ". Exception: " + e.getMessage().toString(), "", "", "", "");
            JobsHandler.RegetSpecificJobCategoryCounterrored = true;
            DrawerMainActivity.GetDriverJobsALerrored = true;
        } finally {
            return DriverJobsArr;
        }
    }

    public static objJobInfo[] GetDriverJobsInfoAL(String ARegID, Long[] JobsIDXs, String webMethName) {
        objJobInfo[] DriverJobsInfoArr = null;
        objJobInfo2[] DriverJobsInfoArr2 = null;
        JSONObject jobj = null;
        JSONArray jarr = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", JobsIDXs);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            jobj = getJSONObj0("L2_GetDriverJobInfo", jsonParameters);
            jarr = jobj.getJSONArray("L2_GetDriverJobInfoResult");
            try {
                DriverJobsInfoArr = gson.fromJson(jarr.toString(), objJobInfo[].class);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at WebService GetDriverJobsInfoAL DriverJobsInfoArr = gson.fromJson(jarr.toString(), objJobInfo[].class). Exception: " + e.getMessage().toString(), "", "", "", "");
            }
            if (DriverJobsInfoArr == null) {
                DriverJobsInfoArr2 = gson.fromJson(jarr.toString(), objJobInfo2[].class);
            }
            if (DriverJobsInfoArr2 != null && DriverJobsInfoArr2.length > 0) {
                DriverJobsInfoArr = reArrangeJobInfo(DriverJobsInfoArr2);
            }
            //int size = DriverJobsInfoArr.length;
        } catch (Exception e) {
            DriverJobsInfoArr = null;
            objGlobalMethods.writeToFile("Exception caught at WebService GetDriverJobsInfoAL. " + e.getMessage().toString(), "", "", "", "");
            SharedPreferences GcmPreferences = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "refreshQueBooleanString", "true");
        } finally {
            return DriverJobsInfoArr;
        }
    }

    public static objJobInfo[] GetDriverJobsInfoAL2(String ARegID, Long[] JobsIDXs, String webMethName) {
        objJobInfo[] DriverJobsInfoArr = null;
        objJobInfo2[] DriverJobsInfoArr2 = null;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", JobsIDXs);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("L2_GetDriverJobInfo", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_GetDriverJobInfoResult");

            DriverJobsInfoArr2 = gson.fromJson(jarr.toString(), objJobInfo2[].class);

            if (DriverJobsInfoArr2 != null && DriverJobsInfoArr2.length > 0) {
                DriverJobsInfoArr = reArrangeJobInfo(DriverJobsInfoArr2);
            }
            //int size = DriverJobsInfoArr.length;
        } catch (Exception e) {
            e.printStackTrace();
            DriverJobsInfoArr = null;
            objGlobalMethods.writeToFile("Exception caught at WebService GetDriverJobsInfoAL2. " + e.getMessage().toString(), "", "", "", "");
            SharedPreferences GcmPreferences = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "refreshQueBooleanString", "true");
        } finally {
            return DriverJobsInfoArr;
        }
    }

    private static objJobInfo[] reArrangeJobInfo(objJobInfo2[] ar2) {
        objJobInfo[] ar0 = new objJobInfo[ar2.length];
        objJobInfo arX = null;
        objJobInfo2 arY = null;

        int i = 0;

        try {
            for (i = 0; i <= ar2.length - 1; i++) {
                arY = ar2[i];
                arX = ar0[i];

                arX.AddressIDX = arY.AddressIDX;
                arX.CNURL = arY.CNURL;
                arX.COD = arY.COD;
                arX.CODCollectAt = arY.CODCollectAt;
                arX.ClientIDX = arY.ClientIDX;
                arX.INDUSTRY = arY.INDUSTRY;
                arX.DelAddFull = arY.DelAddFull;

                arX.ConsignmentNote = arY.ConsignmentNote;
                arX.ContactIDX = arY.ContactIDX;
                if (arY.DLLocation != null) {
                    arX.DLLocation = reArrangeAddressStructure(arY.DLLocation);
                } else {
                    arX.DLLocation = null;
                }

                if (arY.DLRedirectedLocation != null) {
                    arX.DLRedirectedLocation = reArrangeAddressStructure(arY.DLRedirectedLocation);
                } else {
                    arX.DLRedirectedLocation = null;
                }

                arX.DLSI = arY.DLSI;
                arX.DOList = arY.DOList;
                arX.DeliveryContents = arY.DeliveryContents;
                arX.ExpressType = arY.ExpressType;
                arX.ExtCount = arY.ExtCount;
                arX.FromDateTime = arY.FromDateTime;
                arX.JobLogObject = arY.JobLogObject;
                arX.JobNo = arY.JobNo;
                arX.JobsIDX = arY.JobsIDX;
                if (arY.LinkedJobs != null && arY.LinkedJobs.length > 0) {
                    arX.LinkedJobs = reArrangeJobInfo(arY.LinkedJobs);
                } else {
                    arX.LinkedJobs = null;
                }

                if (arY.PULocation != null) {
                    arX.PULocation = reArrangeAddressStructure(arY.PULocation);
                } else {
                    arX.PULocation = null;
                }
                if (arY.PURedirectedLocation != null) {
                    arX.PURedirectedLocation = reArrangeAddressStructure(arY.PURedirectedLocation);
                } else {
                    arX.PURedirectedLocation = null;
                }
                arX.PUSI = arY.PUSI;
                arX.ParentJobsIDX = arY.ParentJobsIDX;
                arX.PickByDateTime = arY.PickByDateTime;
                arX.PicturesURL = arY.PicturesURL;
                arX.Pieces = arY.Pieces;
                arX.ReadyDateTime = arY.ReadyDateTime;
                arX.ScansURL = arY.ScansURL;
                arX.SpecialInstructions = arY.SpecialInstructions;
                arX.StatusCodeIDX = arY.StatusCodeIDX;
                arX.TOSType = arY.TOSType;
                arX.ToDateTime = arY.ToDateTime;
                arX.ExtDateTime = arY.ExtDateTime;
                arX.Weight = arY.Weight;
                arX.DNF = arY.DNF;
                arX.FLAG = arY.FLAG;
                arX.BarcodeTags = arY.BarcodeTags;
                arX.BarcodeTagsExt = arY.BarcodeTagsExt;
                arX.PIN = arY.PIN;
                ar0[i] = arX;
            }
        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at WebService, reArrangeJobInfo. " + e.getMessage().toString(), "", "", "", "");
            ar0 = null;
        } finally {
            return ar0;
        }
    }

    private static objAddressStructure reArrangeAddressStructure(objAddressStructure2 ar2) {
        objAddressStructure ar0 = new objAddressStructure();
        objAddressStructure arX = null;
        objAddressStructure2 arY = null;

        int i = 0;

        try {
            ar0.ACCOUNT = arY.ACCOUNT;
            ar0.ACTIVE = arY.ACTIVE;
            ar0.ADDRESSTYPE = arY.ADDRESSTYPE;
            ar0.BLOCK = arY.BLOCK;
            ar0.BUILDING = arY.BUILDING;
            ar0.CLIENTIDX = arY.CLIENTIDX;
            ar0.COMPANY = arY.COMPANY;
            ar0.CUSTOMER_NOTES = arY.CUSTOMER_NOTES;
            if (arY.Contacts != null && arY.Contacts.length > 0) {
                ar0.Contacts = reArrangeContactStructure(arY.Contacts);
            } else {
                ar0.Contacts = null;
            }

            ar0.IDX = arY.IDX;
            ar0.POSTALCODE = arY.POSTALCODE;
            ar0.STREET = arY.STREET;
            ar0.UNIT = arY.UNIT;
            ar0.LAT = arY.LAT;
            ar0.LNG = arY.LNG;
        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at WebService, reArrangeJobInfo. " + e.getMessage().toString(), "", "", "", "");
            ar0 = null;
        } finally {
            return ar0;
        }
    }

    private static objContactStructure[] reArrangeContactStructure(objContactStructure2[] ar2) {
        objContactStructure[] ar0 = new objContactStructure[ar2.length];
        objContactStructure arX = null;
        objContactStructure2 arY = null;

        int i = 0;

        try {
            for (i = 0; i <= ar2.length - 1; i++) {
                arY = ar2[i];
                arX = ar0[i];

                arX.ACCESS = arY.ACCESS;
                arX.ACTIVE = arY.ACTIVE;
                arX.CLIENTADDRESSIDX = arY.CLIENTADDRESSIDX;
                arX.COST_CENTER = arY.COST_CENTER;
                arX.CUSTOMER_NOTES = arY.CUSTOMER_NOTES;
                arX.DEPARTMENT = arY.DEPARTMENT;
                arX.EMAILADDRESS = arY.EMAILADDRESS;
                arX.FAX = arY.FAX;
                arX.IDX = arY.IDX;
                arX.IDX = arY.IDX;
                arX.MOBILE = arY.MOBILE;
                arX.NAME = arY.NAME;
                arX.OTHER = arY.OTHER;
                arX.SI = arY.SI;
                arX.TEL = arY.TEL;
                arX.TITLE = arY.TITLE;
                arX.WEBACCESS = arY.WEBACCESS;
                arX.WEBLOGIN = arY.WEBLOGIN;
                arX.WEBPASSWORD = arY.WEBPASSWORD;

                ar0[i] = arX;
            }
        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at WebService, reArrangeJobInfo. " + e.getMessage().toString(), "", "", "", "");
            ar0 = null;
        } finally {
            return ar0;
        }
    }

    public static Boolean UpdateJobConsignmentNoteWithGPS(String ARegID, long AJobsIDX, String AConsignmentNote, double ALat, double ALng, String webMethName) {
        Boolean UpdateJobConsignmentNoteResult = null;
        JSONObject UpdateJobConsignmentNoteWithGPSJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("AConsignmentNote", AConsignmentNote);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            UpdateJobConsignmentNoteWithGPSJsonObj = getJSONObj0("L2_UpdateJobConsignmentNote", jsonParameters);
            if (UpdateJobConsignmentNoteWithGPSJsonObj != null) {
                UpdateJobConsignmentNoteResult = Boolean.parseBoolean(UpdateJobConsignmentNoteWithGPSJsonObj.getString("L2_UpdateJobConsignmentNoteResult").toString());
            } else {
                UpdateJobConsignmentNoteResult = null;
            }
        } catch (Exception e) {
            UpdateJobConsignmentNoteResult = null;
        } finally {
            return UpdateJobConsignmentNoteResult;
        }
    }

    public static Boolean UpdateJobStatusCodeWithGPSAccuracyMass(String ARegID, Long[] AJobsIDX, long AStatusCodeIDX, String ARecipient,
                                                                 String ARemarks, String aAUTC, double ALat, double ALng, int Accuracy) {
        Boolean L2_UpdateJobsStatusAResult = null;
        JSONObject UpdateJobStatusCodeWithGPSJsonObj;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("AStatusCodeIDX", AStatusCodeIDX);
            postParameters.put("ARecipient", ARecipient);
            postParameters.put("ARemarks", ARemarks);
            postParameters.put("AUTC", aAUTC);
            postParameters.put("Lat", ALat);
            postParameters.put("Lng", ALng);
            postParameters.put("Accuracy", Accuracy);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            UpdateJobStatusCodeWithGPSJsonObj = getJSONObj0("L2_UpdateJobsStatusA", jsonParameters);
            if (UpdateJobStatusCodeWithGPSJsonObj != null) {
                L2_UpdateJobsStatusAResult = Boolean.parseBoolean(UpdateJobStatusCodeWithGPSJsonObj.getString("L2_UpdateJobsStatusAResult").toString());
            } else {
                L2_UpdateJobsStatusAResult = null;
            }
        } catch (Exception e) {
            L2_UpdateJobsStatusAResult = null;
            objGlobalMethods.writeToFile("Exception at WebService UpdateJobStatusCodeWithGPSAccuracyMass. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return L2_UpdateJobsStatusAResult;
        }
    }

    public static Boolean UpdateJobStatusCodeWithGPSAccuracy(String ARegID, long AJobsIDX, long AStatusCodeIDX, String ARecipient,
                                                             String ARemarks, String aAUTC, double ALat, double ALng, int Accuracy, String webMethName) {
        Boolean UpdateJobStatusCodeResult = null;
        JSONObject UpdateJobStatusCodeWithGPSJsonObj;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("AStatusCodeIDX", AStatusCodeIDX);
            postParameters.put("ARecipient", ARecipient);
            postParameters.put("ARemarks", ARemarks);
            postParameters.put("AUTC", aAUTC);
            postParameters.put("Lat", ALat);
            postParameters.put("Lng", ALng);
            postParameters.put("Accuracy", Accuracy);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            UpdateJobStatusCodeWithGPSJsonObj = getJSONObj0("L2_UpdateJobStatusA", jsonParameters);
            if (UpdateJobStatusCodeWithGPSJsonObj != null) {
                UpdateJobStatusCodeResult = Boolean.parseBoolean(UpdateJobStatusCodeWithGPSJsonObj.getString("L2_UpdateJobStatusAResult").toString());
            } else {
                UpdateJobStatusCodeResult = null;
            }
        } catch (Exception e) {
            UpdateJobStatusCodeResult = null;
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception at WebService UpdateJobStatusCodeWithGPS. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return UpdateJobStatusCodeResult;
        }
    }

    public static Boolean UpdateDunningStatusWithGPSAccuracy(String ARegID, objADunningStatus ADunningStatus, double Lat, double Lng, int Accuracy, String webMethName) {
        Boolean UpdateDunningStatusCodeResult = null;
        JSONObject UpdateDunningStatusCodeWithGPSJsonObj;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ADunningStatus", ADunningStatus);
            postParameters.put("Lat", Lat);
            postParameters.put("Lng", Lng);
            postParameters.put("Accuracy", Accuracy);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            UpdateDunningStatusCodeWithGPSJsonObj = getJSONObj0("L2_Update_DunningA", jsonParameters);
            if (UpdateDunningStatusCodeWithGPSJsonObj != null) {
                UpdateDunningStatusCodeResult = Boolean.parseBoolean(UpdateDunningStatusCodeWithGPSJsonObj.getString("L2_Update_DunningAResult").toString());
            } else {
                UpdateDunningStatusCodeResult = null;
            }
        } catch (Exception e) {
            UpdateDunningStatusCodeResult = null;
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception at WebService UpdateDunningStatusWithGPS. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return UpdateDunningStatusCodeResult;
        }
    }

    public static Boolean UploadJobImageWithGPSandAccuracy(String ARegID, long AJobsIDX, String ARemarks, String AImageData, String aAUTC, double ALat, double ALng, int Accuracy, String webMethName) {
        Boolean UploadJobImageAResult = null;
        JSONObject UploadJobImageWithGPSJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("ARemarks", ARemarks);
            postParameters.put("ABase64String", AImageData);
            postParameters.put("AUTC", aAUTC);
            postParameters.put("Lat", ALat);
            postParameters.put("Lng", ALng);
            postParameters.put("Accuracy", Accuracy);
            String jsonParameters = gson.toJson(postParameters);
            UploadJobImageWithGPSJsonObj = getJSONObj0("L2_UploadJobImageA", jsonParameters);
            if (UploadJobImageWithGPSJsonObj != null) {
                UploadJobImageAResult = Boolean.parseBoolean(UploadJobImageWithGPSJsonObj.getString("L2_UploadJobImageAResult").toString());
            } else {
                UploadJobImageAResult = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService UploadJobImageWithGPSandAccuracy. Exception: " + e.getMessage(), "", "", "", "");
            UploadJobImageAResult = null;
        } finally {
            return UploadJobImageAResult;
        }
    }

    public static Boolean UploadJobSignature(String ARegID, long AJobsIDX, String AImageData, String aAUTC, double ALat, double ALng, int Accuracy, String webMethName) {
        Boolean UploadJobSignatureResult = null;
        JSONObject UploadJobSignatureJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("ABase64String", AImageData);
            postParameters.put("AUTC", aAUTC);
            postParameters.put("Lat", ALat);
            postParameters.put("Lng", ALng);
            postParameters.put("Accuracy", Accuracy);
            String jsonParameters = gson.toJson(postParameters);

            UploadJobSignatureJsonObj = getJSONObj0("L2_UploadJobSignatureA", jsonParameters);
            if (UploadJobSignatureJsonObj != null) {
                UploadJobSignatureResult = Boolean.parseBoolean(UploadJobSignatureJsonObj.getString("L2_UploadJobSignatureAResult").toString());
            } else {
                UploadJobSignatureResult = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService UploadJobSignature. Exception: " + e.getMessage(), "", "", "", "");
            UploadJobSignatureResult = null;
        } finally {
            return UploadJobSignatureResult;
        }
    }

    public static Boolean UploadJobSignatureIA(String ARegID, long AJobsIDX, String AImageData, String aAUTC, double ALat, double ALng, int Accuracy, String webMethName) {
        Boolean L2_UploadJobSignatureIAResult = null;
        JSONObject UploadJobSignatureIAJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("ARemarks", "");
            postParameters.put("ABase64String", AImageData);
            postParameters.put("AUTC", aAUTC);
            postParameters.put("Lat", ALat);
            postParameters.put("Lng", ALng);
            postParameters.put("Accuracy", Accuracy);
            String jsonParameters = gson.toJson(postParameters);

            UploadJobSignatureIAJsonObj = getJSONObj0("L2_UploadJobSignatureIA", jsonParameters);
            if (UploadJobSignatureIAJsonObj != null) {
                L2_UploadJobSignatureIAResult = Boolean.parseBoolean(UploadJobSignatureIAJsonObj.getString("L2_UploadJobSignatureIAResult").toString());
            } else {
                L2_UploadJobSignatureIAResult = null;
            }
        } catch (Exception e) {
            L2_UploadJobSignatureIAResult = null;
        } finally {
            return L2_UploadJobSignatureIAResult;
        }
    }

    public static Boolean UpdateCOD(String ARegID, int AType, long AIDX, int CODType, String AUTC, String webMethName) {
        Boolean UpdateCODResult = null;
        JSONObject UpdateCODJsonObj;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AType", AType);
            postParameters.put("AIDX", AIDX);
            postParameters.put("CODType", CODType);
            postParameters.put("AUTC", AUTC);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            UpdateCODJsonObj = getJSONObj0("L2_UpdateCOD", jsonParameters);
            if (UpdateCODJsonObj != null) {
                UpdateCODResult = Boolean.parseBoolean(UpdateCODJsonObj.getString("L2_UpdateCODResult").toString());
            } else {
                UpdateCODResult = null;
            }
        } catch (Exception e) {
            UpdateCODResult = null;
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception at WebService UpdateCOD. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return UpdateCODResult;
        }
    }

    public static Boolean UpdateDOCOD(String ARegID, Long[] AInvoicesIDX, objUpdateDOCOD[] ACODList, String AUTC, String webMethName) {
        Boolean UpdateDOCODResult = null;
        JSONObject UpdateDOCODJsonObj;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AInvoicesIDX", AInvoicesIDX);
            postParameters.put("ACODList", ACODList);
            postParameters.put("AUTC", AUTC);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            UpdateDOCODJsonObj = getJSONObj0("L2_UpdateDOCOD", jsonParameters);
            if (UpdateDOCODJsonObj != null) {
                UpdateDOCODResult = Boolean.parseBoolean(UpdateDOCODJsonObj.getString("L2_UpdateDOCODResult").toString());
            } else {
                UpdateDOCODResult = null;
            }
        } catch (Exception e) {
            UpdateDOCODResult = null;
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception at WebService UpdateDOCOD. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return UpdateDOCODResult;
        }
    }

    public static Boolean UpdateDOinv(String ARegID, long AJobsIDX, String[] ATags, Boolean ADelivered, String AUTC) {
        Boolean UpdateDOinvResult = null;
        JSONObject UpdateCODJsonObj;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("ATags", ATags);
            postParameters.put("ADelivered", ADelivered);
            postParameters.put("AUTC", AUTC);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            UpdateCODJsonObj = getJSONObj0("L2_UpdateTagStatus", jsonParameters);
            if (UpdateCODJsonObj != null) {
                UpdateDOinvResult = Boolean.parseBoolean(UpdateCODJsonObj.getString("L2_UpdateTagStatusResult").toString());
            } else {
                UpdateDOinvResult = null;
            }
        } catch (Exception e) {
            UpdateDOinvResult = null;
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception at WebService UpdateDOinv. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return UpdateDOinvResult;
        }
    }

    public static Boolean UploadGPS2(String ARegID, objRtnGPS[] RtnGPS, Integer BattPct, String webMethName) {
        Boolean L2_Upload_GPSResult = null;
        JSONObject UploadGPSJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("Locations", RtnGPS);
            postParameters.put("BattPct", BattPct);
            String jsonParameters = gson.toJson(postParameters);

            UploadGPSJsonObj = getJSONObj0("L2_Upload_GPSBatt", jsonParameters);
            if (UploadGPSJsonObj != null) {
                L2_Upload_GPSResult = Boolean.parseBoolean(UploadGPSJsonObj.getString("L2_Upload_GPSBattResult").toString());
            } else {
                L2_Upload_GPSResult = null;
            }
        } catch (Exception e) {
            L2_Upload_GPSResult = null;
        } finally {
            return L2_Upload_GPSResult;
        }
    }

    public static Boolean UploadTagging(String aARegID, long aAJobsIDX, String[] aATagArray, String aAUTC, double aLat, double aLng) {
        Boolean L2_UploadJobTagsResult = null;
        JSONObject UploadTaggingJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", aARegID);
            postParameters.put("AJobsIDX", aAJobsIDX);
            postParameters.put("ATagArray", aATagArray);
            postParameters.put("AUTC", aAUTC);
            postParameters.put("Lat", aLat);
            postParameters.put("Lng", aLng);
            String jsonParameters = gson.toJson(postParameters);

            UploadTaggingJsonObj = getJSONObj0("L2_UploadJobTags", jsonParameters);
            if (UploadTaggingJsonObj != null) {
                L2_UploadJobTagsResult = Boolean.parseBoolean(UploadTaggingJsonObj.getString("L2_UploadJobTagsResult").toString());
            } else {
                L2_UploadJobTagsResult = null;
            }
        } catch (Exception e) {
            L2_UploadJobTagsResult = null;
        } finally {
            return L2_UploadJobTagsResult;
        }
    }

    public static Boolean RemoveTagging(String aARegID, long aAJobsIDX, String[] aATagArray, String aAUTC, double aLat, double aLng) {
        Boolean L2_RemoveJobTagResult = null;
        JSONObject RemoveTaggingJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", aARegID);
            postParameters.put("AJobsIDX", aAJobsIDX);
            postParameters.put("ATagArray", aATagArray);
            postParameters.put("AUTC", aAUTC);
            postParameters.put("Lat", aLat);
            postParameters.put("Lng", aLng);
            String jsonParameters = gson.toJson(postParameters);

            RemoveTaggingJsonObj = getJSONObj0("L2_RemoveJobTags", jsonParameters);
            if (RemoveTaggingJsonObj != null) {
                L2_RemoveJobTagResult = Boolean.parseBoolean(RemoveTaggingJsonObj.getString("L2_RemoveJobTagsResult").toString());
            } else {
                L2_RemoveJobTagResult = null;
            }
        } catch (Exception e) {
            L2_RemoveJobTagResult = null;
        } finally {
            return L2_RemoveJobTagResult;
        }
    }

    public static Boolean UploadJobsheet(String aAPIKey, String[] val) {
        Boolean SubmitJobSheetResult = null;
        JSONObject UploadJobsheetJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("JSD", val);
            String jsonParameters = gson.toJson(postParameters);

            UploadJobsheetJsonObj = getJSONObj0("SubmitJobSheet", jsonParameters);
            if (UploadJobsheetJsonObj != null) {
                SubmitJobSheetResult = Boolean.parseBoolean(UploadJobsheetJsonObj.getString("SubmitJobSheetResult").toString());
            } else {
                SubmitJobSheetResult = null;
            }
        } catch (Exception e) {
            SubmitJobSheetResult = null;
        } finally {
            return SubmitJobSheetResult;
        }
    }

    public static Boolean SubmitShuttleSheet(String aAPIKey, String[] val) {
        Boolean SubmitJobSheetResult = null;
        JSONObject UploadJobsheetJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("JSD", val);
            String jsonParameters = gson.toJson(postParameters);

            UploadJobsheetJsonObj = getJSONObj0("SubmitShuttleSheet", jsonParameters);
            if (UploadJobsheetJsonObj != null) {
                SubmitJobSheetResult = Boolean.parseBoolean(UploadJobsheetJsonObj.getString("SubmitShuttleSheetResult").toString());
            } else {
                SubmitJobSheetResult = null;
            }
        } catch (Exception e) {
            SubmitJobSheetResult = null;
        } finally {
            return SubmitJobSheetResult;
        }
    }

    public static String CheckTaggingGroup(String val1, String val2) {
        String result = "";
        JSONObject UploadItemGroupJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", objGlobalVariables.LukeAPIKey);
            postParameters.put("AFirstPart", val1);
            postParameters.put("ASecondPart", val2);
            String jsonParameters = gson.toJson(postParameters);

            UploadItemGroupJsonObj = getJSONObj0("CheckTaggingGroup", jsonParameters);
            if (UploadItemGroupJsonObj != null) {
                result = UploadItemGroupJsonObj.getString("CheckTaggingGroupResult").toString();
            } else {
                result = "";
            }
        } catch (Exception e) {
            result = "error";
            JobsHandler.sendErrorEmail("Error invoking SubmitItemGroup, adding back to que. Submitted values: " + val1 + ", " + val2);
        } finally {
            return result;
        }
    }

    public static Boolean UploadBulkPickUp(String ARegID, String[] JSParr, String aAUTC, double ALat, double ALng) {
        Boolean L2_JobSpoolResult = null;
        JSONObject UploadBulkPickUpJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("JobReferences", JSParr);
            postParameters.put("AUTC", aAUTC);
            postParameters.put("Lat", ALat);
            postParameters.put("Lng", ALng);
            String jsonParameters = gson.toJson(postParameters);

            UploadBulkPickUpJsonObj = getJSONObj0("L2_JobSpool", jsonParameters);
            if (UploadBulkPickUpJsonObj != null) {
                L2_JobSpoolResult = Boolean.parseBoolean(UploadBulkPickUpJsonObj.getString("L2_JobSpoolResult").toString());
            } else {
                L2_JobSpoolResult = null;
            }
        } catch (Exception e) {
            L2_JobSpoolResult = null;
        } finally {
            return L2_JobSpoolResult;
        }
    }

    //phasing out
    /*public static Boolean UploadCreditCard(String aAPIKey, long aDriverIDX, ArrayList<objStringWTag> SWTarr) {
        Boolean EnterCitiJobsResultBool = null;
        String EnterCitiJobsResult = "";
        JSONObject UploadCreditCardJsonObj;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("DriverIDX", aDriverIDX);
            postParameters.put("ConNotePostal", SWTarr);
            String jsonParameters = gson.toJson(postParameters);

            UploadCreditCardJsonObj = getJSONObj0("EnterCitiJobs", jsonParameters);
            if (UploadCreditCardJsonObj != null) {
                EnterCitiJobsResult = UploadCreditCardJsonObj.getString("EnterCitiJobsResult").toString();
                if (EnterCitiJobsResult.equals("Jobs Queued")) {
                    EnterCitiJobsResultBool = true;
                } else {
                    EnterCitiJobsResultBool = false;
                }
            } else {
                EnterCitiJobsResultBool = null;
            }
        } catch (Exception e) {
            EnterCitiJobsResultBool = null;
        } finally {
            return EnterCitiJobsResultBool;
        }
    }*/

    //phasing out
    /*public static Boolean UploadCreditCard3H(String aAPIKey, long aDriverIDX, ArrayList<objStringWTag> SWTarr) {
        Boolean EnterCitiJobs3HResultBool = null;
        String EnterCitiJobs3HResult = "";
        JSONObject UploadCreditCard3HJsonObj;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("DriverIDX", aDriverIDX);
            postParameters.put("ConNotePostal", SWTarr);
            String jsonParameters = gson.toJson(postParameters);

            UploadCreditCard3HJsonObj = getJSONObj0("EnterCitiJobs3H", jsonParameters);
            if (UploadCreditCard3HJsonObj != null) {
                EnterCitiJobs3HResult = UploadCreditCard3HJsonObj.getString("EnterCitiJobs3HResult").toString();
                if (EnterCitiJobs3HResult.equals("Jobs Queued")) {
                    EnterCitiJobs3HResultBool = true;
                } else {
                    EnterCitiJobs3HResultBool = false;
                }
            } else {
                EnterCitiJobs3HResultBool = null;
            }
        } catch (Exception e) {
            EnterCitiJobs3HResultBool = null;
        } finally {
            return EnterCitiJobs3HResultBool;
        }
    }*/

    //phasing out
    /*public static Boolean UploadCreditCardDBS(String aAPIKey, long aDriverIDX, ArrayList<objStringWTag> SWTarr) {
        Boolean EnterCitiJobsResultBool = null;
        String EnterCitiJobsResult = "";
        JSONObject UploadCreditCardJsonObj;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("DriverIDX", aDriverIDX);
            postParameters.put("ConNotePostal", SWTarr);
            String jsonParameters = gson.toJson(postParameters);

            UploadCreditCardJsonObj = getJSONObj0("EnterDBSJobs", jsonParameters);
            if (UploadCreditCardJsonObj != null) {
                EnterCitiJobsResult = UploadCreditCardJsonObj.getString("EnterDBSJobsResult").toString();
                if (EnterCitiJobsResult.equals("Jobs Queued")) {
                    EnterCitiJobsResultBool = true;
                } else {
                    EnterCitiJobsResultBool = false;
                }
            } else {
                EnterCitiJobsResultBool = null;
            }
        } catch (Exception e) {
            EnterCitiJobsResultBool = null;
        } finally {
            return EnterCitiJobsResultBool;
        }
    }*/

    public static Boolean AckMessages(String ARegID, Long[] MessageIDX, String aAUTC, double ALat, double ALng) {
        Boolean AckMessagesResultBool = null;
        JSONObject AckMessagesJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("MessageIDX", MessageIDX);
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            postParameters.put("AUTC", timestampstr);
            postParameters.put("Lat", ALat);
            postParameters.put("Lng", ALng);
            String jsonParameters = gson.toJson(postParameters);


            AckMessagesJsonObj = getJSONObj0("L2_AckMessages", jsonParameters);
            if (AckMessagesJsonObj != null) {
                AckMessagesResultBool = Boolean.parseBoolean(AckMessagesJsonObj.getString("L2_AckMessagesResult").toString());
            } else {
                AckMessagesResultBool = null;
            }
        } catch (Exception e) {
            AckMessagesResultBool = null;
        } finally {
            return AckMessagesResultBool;
        }
    }

    public static Boolean SendJobMessageA(String ARegID, String Message, String AUserIDX, String AJobsIDX, String aAUTC, double ALat, double ALng) {
        Boolean SendJobMessageAResultBool = null;
        JSONObject AckMessagesJsonObj;
        Long UserIDX, JobsIDX;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("Message", Message);
            if (!AUserIDX.equals("")) {
                UserIDX = Long.parseLong(AUserIDX);
            } else {
                UserIDX = Long.parseLong("0");
            }
            postParameters.put("AUserIDX", UserIDX);
            if (!AJobsIDX.equals("")) {
                JobsIDX = Long.parseLong(AJobsIDX);
            } else {
                JobsIDX = Long.parseLong("0");
            }
            postParameters.put("AJobsIDX", JobsIDX);
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            postParameters.put("AUTC", timestampstr);
            postParameters.put("Lat", ALat);
            postParameters.put("Lng", ALng);
            String jsonParameters = gson.toJson(postParameters);

            AckMessagesJsonObj = getJSONObj0("L2_SendJobMessageA", jsonParameters);
            if (AckMessagesJsonObj != null) {
                SendJobMessageAResultBool = Boolean.parseBoolean(AckMessagesJsonObj.getString("L2_SendJobMessageAResult").toString());
            } else {
                SendJobMessageAResultBool = null;
            }
        } catch (Exception e) {
            SendJobMessageAResultBool = null;
        } finally {
            return SendJobMessageAResultBool;
        }
    }

    public static objUpdateInfo GetUpdate(Boolean aIsManuallyInstalled, int aInstalledVersionCode) {
        objUpdateInfo objUpdateInfoobj = null;
        String objUpdateInfoobjStr = "";
        JSONObject objUpdateInfoJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("IsManuallyInstalled", aIsManuallyInstalled);
            postParameters.put("InstalledVersionCode", aInstalledVersionCode);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            objUpdateInfoJsonObj = getJSONObj0("L2_GetUpdate", jsonParameters);
            objUpdateInfoobjStr = objUpdateInfoJsonObj.getString("L2_GetUpdateResult").toString();
            if (!objUpdateInfoobjStr.equals("")) {
                objUpdateInfoobj = gson.fromJson(objUpdateInfoobjStr, objUpdateInfo.class);
            }

            //int size = DriverJobsInfoArr.length;
        } catch (Exception e) {
            e.printStackTrace();
            objUpdateInfoobj = null;
        } finally {
            return objUpdateInfoobj;
        }
    }

    public static objCODjobInfo[] getCODjobInfoAL(String ARegID) {
        Date dt = new Date();
        objCODjobInfo[] obj = null;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("L2_GetDriverCOD", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_GetDriverCODResult");

            obj = gson.fromJson(jarr.toString(), objCODjobInfo[].class);

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at WebService getCODjobInfoAL. Exception: " + e.toString(), "", "", "", "");
            obj = null;
        } finally {
            return obj;
        }
    }

    public static Boolean UpdateBattLvl(String ARegID, Integer BattPct) {
        Boolean L2_Update_BattResult = null;
        JSONObject L2_Update_BattResultJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("BattPct", BattPct);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            L2_Update_BattResultJsonObj = getJSONObj0("L2_Update_Batt", jsonParameters);
            if (L2_Update_BattResultJsonObj != null) {
                L2_Update_BattResult = Boolean.parseBoolean(L2_Update_BattResultJsonObj.getString("L2_Update_BattResult").toString());
            } else {
                L2_Update_BattResult = null;
            }

        } catch (Exception e) {
            L2_Update_BattResult = null;
        } finally {
            return L2_Update_BattResult;
        }
    }

    public static String getRouteListJsonStr(String ARegID) {
        String RouteListJsonStr = "";
        JSONObject jobj0 = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            jobj0 = getRoutingJSONObj("L2_GetRouteList", jsonParameters);
            RouteListJsonStr = jobj0.getString("L2_GetRouteListResult").toString();

        } catch (Exception e) {
            RouteListJsonStr = "";
            objGlobalMethods.writeToFile("Exception caught at WebService GetRouteList. " + e.getMessage().toString(), "", "", "", "");
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "refreshQueBooleanString", "true");
        } finally {
            return RouteListJsonStr;
        }
    }

    public static Boolean getSendPinResult(String ARegID, long aAJobsIDX, String aAUTC, double ALat, double ALng, int aAccuracy) {
        Boolean L2_SendPINResult = null;
        JSONObject getSendPinResultJsonObj;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", aAJobsIDX);
            postParameters.put("AUTC", aAUTC);
            postParameters.put("Lat", ALat);
            postParameters.put("Lng", ALng);
            postParameters.put("Accuracy", aAccuracy);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            getSendPinResultJsonObj = getJSONObj0("L2_SendPIN", jsonParameters);
            if (getSendPinResultJsonObj != null) {
                L2_SendPINResult = Boolean.parseBoolean(getSendPinResultJsonObj.getString("L2_SendPINResult").toString());
            } else {
                L2_SendPINResult = null;
            }
        } catch (Exception e) {
            L2_SendPINResult = null;
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception at WebService getSendPinResult. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return L2_SendPINResult;
        }
    }

    public static objDriverPointResult getDriverPointResult(String ARegID, String aAUTC) {
        objDriverPointResult rs = null;
        JSONObject job0 = null;
        Date dt = new Date();
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ADate", aAUTC);
            //postParameters.put("ADate", "05/30/2017");

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            job0 = getJSONObj0("L2_GetDriverPoints", jsonParameters);
            rs = gson.fromJson(job0.getString("L2_GetDriverPointsResult").toString(), objDriverPointResult.class);
        } catch (Exception e) {
            rs = null;
            objGlobalMethods.writeToFile("Exception caught at WebService getDriverPointResult. " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return rs;
        }
    }

    public static objDriverPointSummary[] getobjDriverPointSummary(String ARegID, String aAStartDateAUTC, String aAEndDateAUTC) {
        objDriverPointSummary[] rs = null;
        JSONObject job0 = null;
        Date dt = new Date();
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AStartDate", aAStartDateAUTC);
            postParameters.put("AEndDate", aAEndDateAUTC);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            job0 = getJSONObj0("L2_GetDriverPointSummary", jsonParameters);
            rs = gson.fromJson(job0.getString("L2_GetDriverPointSummaryResult").toString(), objDriverPointSummary[].class);
        } catch (Exception e) {
            rs = null;
            objGlobalMethods.writeToFile("Exception caught at WebService getobjDriverPointSummary. " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return rs;
        }
    }

    public static objGetTravelDistanceResult getTravelDistanceResult(String ARegID, String AFromDate, String AToDate) {
        objGetTravelDistanceResult L2_GetTravelDistanceResult = null;
        JSONObject job0 = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AFromDate", AFromDate);
            postParameters.put("AToDate", AToDate);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            job0 = getJSONObj0("L2_GetTravelDistance", jsonParameters);
            L2_GetTravelDistanceResult = gson.fromJson(job0.getString("L2_GetTravelDistanceResult").toString(), objGetTravelDistanceResult.class);
        } catch (Exception e) {
            L2_GetTravelDistanceResult = null;
            objGlobalMethods.writeToFile("Exception caught at WebService getTravelDistanceResult. " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return L2_GetTravelDistanceResult;
        }
    }

    public static String[] L2_GetWIFIHome(String ARegID) {
        Date dt = new Date();
        String[] obj = null;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("L2_GetWIFIHome", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_GetWIFIHomeResult");

            obj = gson.fromJson(jarr.toString(), String[].class);

        } catch (Exception e) {
            //objGlobalMethods.writeToFile("Exception caught at WebService L2_GetWIFIHome. Exception: " + e.toString(), "", "", "", "");
            obj = null;
        } finally {
            return obj;
        }
    }

    public static dbObjTradeInList[] GetTradeInList(String ARegID) {
        Date dt = new Date();
        dbObjTradeInList[] obj = null;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("L2_GetTradeInList", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_GetTradeInListResult");

            obj = gson.fromJson(jarr.toString(), dbObjTradeInList[].class);

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at WebService GetTradeInList. Exception: " + e.toString(), "", "", "", "");
            obj = null;
        } finally {
            return obj;
        }
    }

    public static NFCTag[] GetNFCTagsList(String ARegID) {
        NFCTag[] obj = null;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("L2_NFCTags", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_NFCTagsResult");

            obj = gson.fromJson(jarr.toString(), NFCTag[].class);

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at WebService GetNFCTagsList. Exception: " + e.toString(), "", "", "", "");
            obj = null;
        } finally {
            return obj;
        }
    }

    public static Boolean ReportHQ(String ARegID) {
        Boolean L2_ReportHQResult = false;
        JSONObject getReportHQResultJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            getReportHQResultJsonObj = getJSONObj0("L2_ReportHQ", jsonParameters);
            if (getReportHQResultJsonObj != null) {
                L2_ReportHQResult = Boolean.parseBoolean(getReportHQResultJsonObj.getString("L2_ReportHQResult").toString());
            } else {
                L2_ReportHQResult = false;
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at WebService ReportHQ. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at WebService ReportHQ. Exception: " + e.getMessage().toString(), "", "", "", "");
            L2_ReportHQResult = false;
        } finally {
            return L2_ReportHQResult;
        }
    }

    public static Boolean LeaveHQ(String ARegID) {
        Boolean L2_LeaveHQResult = false;
        JSONObject getLeaveHQResultJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            getLeaveHQResultJsonObj = getJSONObj0("L2_LeaveHQ", jsonParameters);
            if (getLeaveHQResultJsonObj != null) {
                L2_LeaveHQResult = Boolean.parseBoolean(getLeaveHQResultJsonObj.getString("L2_LeaveHQResult").toString());
            } else {
                L2_LeaveHQResult = false;
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at WebService LeaveHQ. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at WebService LeaveHQ. Exception: " + e.getMessage().toString(), "", "", "", "");
            L2_LeaveHQResult = false;
        } finally {
            return L2_LeaveHQResult;
        }
    }

    public static Boolean ReportHQTS(String ARegID, String aAUTC) {
        Boolean L2_ReportHQTSResult = false;
        JSONObject getReportHQResultJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ATimestamp", aAUTC);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            getReportHQResultJsonObj = getJSONObj0("L2_ReportHQTS", jsonParameters);
            if (getReportHQResultJsonObj != null) {
                L2_ReportHQTSResult = Boolean.parseBoolean(getReportHQResultJsonObj.getString("L2_ReportHQTSResult").toString());
            } else {
                L2_ReportHQTSResult = false;
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at WebService ReportHQ. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at WebService ReportHQTS. Exception: " + e.getMessage().toString(), "", "", "", "");
            L2_ReportHQTSResult = false;
        } finally {
            return L2_ReportHQTSResult;
        }
    }

    public static Boolean LeaveHQTS(String ARegID, String aAUTC) {
        Boolean L2_LeaveHQTSResult = false;
        JSONObject getLeaveHQResultJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ATimestamp", aAUTC);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            getLeaveHQResultJsonObj = getJSONObj0("L2_LeaveHQTS", jsonParameters);
            if (getLeaveHQResultJsonObj != null) {
                L2_LeaveHQTSResult = Boolean.parseBoolean(getLeaveHQResultJsonObj.getString("L2_LeaveHQTSResult").toString());
            } else {
                L2_LeaveHQTSResult = false;
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at WebService LeaveHQ. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at WebService LeaveHQTS. Exception: " + e.getMessage().toString(), "", "", "", "");
            L2_LeaveHQTSResult = false;
        } finally {
            return L2_LeaveHQTSResult;
        }
    }

    public static Boolean SaveGCETA_n_Cabinet(String ARegID, objCabinet Cabinet, ArrayList<objGC_CustomQueue> AQueue) {
        Boolean L2_SetETACabinetResult = false;
        JSONObject jsonobj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ACabinet", Cabinet);
            postParameters.put("AQueue", AQueue);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            jsonobj = getJSONObj0("L2_SetETACabinet", jsonParameters);
            if (jsonobj != null) {
                L2_SetETACabinetResult = Boolean.parseBoolean(jsonobj.getString("L2_SetETACabinetResult").toString());
            } else {
                L2_SetETACabinetResult = false;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService SaveGCETA_n_Cabinet. Exception: " + e.getMessage().toString(), "", "", "", "");
            L2_SetETACabinetResult = false;
        } finally {
            return L2_SetETACabinetResult;
        }
    }

    public static Boolean SaveCabinet(String ARegID, objCabinet Cabinet) {
        Boolean L2_SaveCabinetResult = false;
        JSONObject jsonobj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ACabinet", Cabinet);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            jsonobj = getJSONObj0("L2_SaveCabinet", jsonParameters);
            if (jsonobj != null) {
                L2_SaveCabinetResult = Boolean.parseBoolean(jsonobj.getString("L2_SaveCabinetResult").toString());
            } else {
                L2_SaveCabinetResult = false;
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at WebService SaveCabinet. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception at WebService SaveCabinet. Exception: " + e.getMessage().toString(), "", "", "", "");
            L2_SaveCabinetResult = false;
        } finally {
            return L2_SaveCabinetResult;
        }
    }

    public static objCabinet GetCabinet(String ARegID, String ACabinet_ID) {
        objCabinet Cabinet = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ACabinet_ID", ACabinet_ID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            jsonobj = getJSONObj0("L2_GetCabinet", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_GetCabinetResult").toString();
            if (!jsonobjstr.equals("")) {
                Cabinet = gson.fromJson(jsonobjstr, objCabinet.class);
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at WebService GetCabinet. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception at WebService GetCabinet. Exception: " + e.getMessage().toString(), "", "", "", "");
            Cabinet = null;
        } finally {
            return Cabinet;
        }
    }

    public static objCabinet[] GetCabinets(String ARegID) {
        objCabinet[] obj = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            JSONObject jobj = getJSONObj0("L2_GetCabinets", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_GetCabinetsResult");
            obj = gson.fromJson(jarr.toString(), objCabinet[].class);
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at WebService GetCabinets. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at WebService GetCabinets. Exception: " + e.getMessage().toString(), "", "", "", "");
            obj = null;
        } finally {
            return obj;
        }
    }

    public static Boolean UpdateJobPieces(String ARegID, long AJobsIDX, Integer Pieces) {
        Boolean L2_UpdateJobPiecesResult = null;
        JSONObject jsonobj;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("Pieces", Pieces);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_UpdateJobPieces", jsonParameters);
            if (jsonobj != null) {
                L2_UpdateJobPiecesResult = Boolean.parseBoolean(jsonobj.getString("L2_UpdateJobPiecesResult").toString());
            } else {
                L2_UpdateJobPiecesResult = null;
            }
        } catch (Exception e) {
            L2_UpdateJobPiecesResult = null;
            objGlobalMethods.writeToFile("Exception at WebService UpdateJobPieces. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return L2_UpdateJobPiecesResult;
        }
    }

    public static objChatMessage[] RetrieveMessages(String ARegID) {
        objChatMessage[] obj = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            JSONObject jobj = getJSONObj0("L2_RetrieveMessages", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_RetrieveMessagesResult");
            obj = gson.fromJson(jarr.toString(), objChatMessage[].class);
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at WebService RetrieveMessages. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception at WebService RetrieveMessages. Exception: " + e.getMessage().toString(), "", "", "", "");
            obj = null;
        } finally {
            return obj;
        }
    }

    public static JobQueue[] L2_GetETA(String ARegID) {
        JobQueue[] obj = null;
        JobQueue[] test = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            JSONObject jobj = getJSONObj0("L2_GetETA", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_GetETAResult");
            obj = gson.fromJson(jarr.toString(), JobQueue[].class);
            /*obj = gson.fromJson("", JobQueue[].class);
            test = obj;*/
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at WebService RetrieveMessages. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception at WebService L2_GetETA. Exception: " + e.getMessage().toString(), "", "", "", "");
            obj = null;
        } finally {
            return obj;
        }
    }

    public static String GetVoIPExtension(String ARegID) {
        Date dt = new Date();
        String VoIPExtension = "";
        JSONObject jobj0 = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            jobj0 = getRoutingJSONObj("L2_GetVoIPExtension", jsonParameters);
            VoIPExtension = jobj0.getString("L2_GetVoIPExtensionResult").toString();

        } catch (Exception e) {
            VoIPExtension = "";
            objGlobalMethods.writeToFile("Exception caught at WebService GetVoIPExtension. " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return VoIPExtension;
        }
    }

    public static String[] MPD_Check_Holding(String ARegID) {
        String[] MPD_Check_HoldingResult = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("MPD_Check_Holding", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("MPD_Check_HoldingResult");

            MPD_Check_HoldingResult = gson.fromJson(jarr.toString(), String[].class);

        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception caught at WebService MPD_Check_Holding. Exception: " + s, "", "", "", "");
            MPD_Check_HoldingResult = null;
        } finally {
            return MPD_Check_HoldingResult;
        }
    }

    public static int MPD_Check_Out(String ARegID, String ASerialNumber) {
        int MPD_Check_OutResult = 0;
        JSONObject jsonobj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ASerialNumber", ASerialNumber);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("MPD_Check_Out", jsonParameters);
            if (jsonobj != null) {
                MPD_Check_OutResult = Integer.parseInt(jsonobj.getString("MPD_Check_OutResult").toString());
            } else {
                MPD_Check_OutResult = 0;
            }
        } catch (Exception e) {
            String s = "";
            objGlobalMethods.writeToFile("Exception at WebService MPD_Check_Out. Exception: " + s, "", "", "", "");
        } finally {
            return MPD_Check_OutResult;
        }

    }

    public static Boolean L2_SetGCETA(String ARegID, ArrayList<objGC_CustomQueue> AQueue) {
        Boolean L2_SetGCETAResult = null;
        JSONObject JsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AQueue", AQueue);
            String jsonParameters = gson.toJson(postParameters);

            JsonObj = getJSONObj0("L2_SetGCETA", jsonParameters);
            if (JsonObj != null) {
                L2_SetGCETAResult = Boolean.parseBoolean(JsonObj.getString("L2_SetGCETAResult").toString());
            } else {
                L2_SetGCETAResult = false;
            }
        } catch (Exception e) {
            L2_SetGCETAResult = false;
        } finally {
            return L2_SetGCETAResult;
        }
    }

    public static boolean L2_SetOpsETA(String ARegID, ArrayList<objGC_CustomQueue> AQueue) {
        Boolean L2_SetOpsETAResult = null;
        JSONObject JsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AQueue", AQueue);
            String jsonParameters = gson.toJson(postParameters);

            JsonObj = getJSONObj0("L2_SetOpsETA", jsonParameters);
            if (JsonObj != null) {
                L2_SetOpsETAResult = Boolean.parseBoolean(JsonObj.getString("L2_SetOpsETAResult").toString());
            } else {
                L2_SetOpsETAResult = false;
            }
        } catch (Exception e) {
            L2_SetOpsETAResult = false;
        } finally {
            return L2_SetOpsETAResult;
        }
    }

    public static Boolean CanDeliver(String APIKey, Long ADriverIDX, String ANRIC, String AItem) {
        Boolean CanDeliverResult = null;
        JSONObject JsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", APIKey);
            postParameters.put("ADriverIDX", ADriverIDX);
            postParameters.put("ANRIC", ANRIC);
            postParameters.put("AItem", AItem);
            String jsonParameters = gson.toJson(postParameters);

            JsonObj = getJSONObj0("CanDeliver", jsonParameters);
            if (JsonObj != null) {
                CanDeliverResult = Boolean.parseBoolean(JsonObj.getString("CanDeliverResult").toString());
            } else {
                CanDeliverResult = false;
            }
        } catch (Exception e) {
            CanDeliverResult = true;
        } finally {
            return CanDeliverResult;
        }
    }

    public static Boolean SendReviewLink(String ARegID, long AJobsIDX) {
        Boolean L2_SendReviewLinkResult = false;
        JSONObject JsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            String jsonParameters = gson.toJson(postParameters);

            JsonObj = getJSONObj0("L2_SendReviewLink", jsonParameters);
            if (JsonObj != null) {
                L2_SendReviewLinkResult = Boolean.parseBoolean(JsonObj.getString("L2_SendReviewLinkResult").toString());
            } else {
                L2_SendReviewLinkResult = false;
            }
        } catch (Exception e) {
            L2_SendReviewLinkResult = null;
        } finally {
            return L2_SendReviewLinkResult;
        }
    }

    public static objLeaveType[] GetLeaveTypes(String ARegID) {
        objLeaveType[] obj = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            JSONObject jobj = getJSONObj0("L2_GetLeaveTypes", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_GetLeaveTypesResult");
            obj = gson.fromJson(jarr.toString(), objLeaveType[].class);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService GetLeaveTypes. Exception: " + e.getMessage().toString(), "", "", "", "");
            obj = null;
        } finally {
            return obj;
        }
    }

    public static objLeaveRecord[] GetLeaveApplications(String ARegID, long ALeaveTypeIDX) {
        objLeaveRecord[] obj = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ALeaveTypeIDX", ALeaveTypeIDX);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            JSONObject jobj = getJSONObj0("L2_GetLeaveApplications", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_GetLeaveApplicationsResult");
            obj = gson.fromJson(jarr.toString(), objLeaveRecord[].class);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService GetLeaveApplications. Exception: " + e.getMessage().toString(), "", "", "", "");
            obj = null;
        } finally {
            return obj;
        }
    }

    public static objLeaveRecord UpdateLeaveApplication(String ARegID, objLeaveRecordSubmit ALeaveRecord) {
        objLeaveRecord objLeaveRecordobj = null;
        String objLeaveRecordobjStr = "";
        JSONObject objLeaveRecordJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ALeaveRecord", ALeaveRecord);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            objLeaveRecordJsonObj = getJSONObj0("L2_UpdateLeaveApplication", jsonParameters);
            objLeaveRecordobjStr = objLeaveRecordJsonObj.getString("L2_UpdateLeaveApplicationResult").toString();
            if (!objLeaveRecordobjStr.equals("")) {
                objLeaveRecordobj = gson.fromJson(objLeaveRecordobjStr, objLeaveRecord.class);
            }

            //int size = DriverJobsInfoArr.length;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService UpdateLeaveApplication. Exception: " + e.getMessage().toString(), "", "", "", "");
            objLeaveRecordobj = null;
        } finally {
            return objLeaveRecordobj;
        }
    }

    public static objChangePasswordResult L2ChangePassword(String ARegID, String AOldPassword, String ANewPassword){
        objChangePasswordResult res = null;
        String objChangePasswordResultStr = "";
        JSONObject objChangePasswordResultJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AOldPassword", AOldPassword);
            postParameters.put("ANewPassword", ANewPassword);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            objChangePasswordResultJsonObj = getJSONObj0("L2_ChangePassword", jsonParameters);
            objChangePasswordResultStr = objChangePasswordResultJsonObj.getString("L2_ChangePasswordResult").toString();
            if (!objChangePasswordResultStr.equals("")) {
                res = gson.fromJson(objChangePasswordResultStr, objChangePasswordResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2ChangePassword. Exception: " + e.getMessage().toString(), "", "", "", "");
            res = null;
        } finally {
            return res;
        }
    }

    //Caging

    public static objGet_CageResult Get_Cage(String WDBarcode) {
        objGet_CageResult Get_CageResult = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", "EED6AD08118FAB9AAEFF756C1B00101254F8B6C0");
            postParameters.put("WDBarcode", WDBarcode);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("Get_Cage", jsonParameters);
            jsonobjstr = jsonobj.getString("Get_CageResult").toString();
            if (!jsonobjstr.equals("")) {
                Get_CageResult = gson.fromJson(jsonobjstr, objGet_CageResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService Get_Cage. Exception: " + e.getMessage(), "", "", "", "");
            Get_CageResult = null;
        } finally {
            return Get_CageResult;
        }
    }

    public static objCan_Cage_InResult Can_Cage_In(long ACageIDX, String ASearchString) {
        objCan_Cage_InResult Can_Cage_InResult = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", "EED6AD08118FAB9AAEFF756C1B00101254F8B6C0");
            postParameters.put("ACageIDX", ACageIDX);
            postParameters.put("ASearchString", ASearchString);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("Can_Cage_In", jsonParameters);
            jsonobjstr = jsonobj.getString("Can_Cage_InResult").toString();
            if (!jsonobjstr.equals("")) {
                Can_Cage_InResult = gson.fromJson(jsonobjstr, objCan_Cage_InResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService Can_Cage_In. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return Can_Cage_InResult;
        }
    }

    public static objCan_Cage_InResult Can_CageIn(long AUserIDX, long ACageIDX, String ASearchString) {
        objCan_Cage_InResult Can_Cage_InResult = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", "EED6AD08118FAB9AAEFF756C1B00101254F8B6C0");
            postParameters.put("AUserIDX", AUserIDX);
            postParameters.put("ACageIDX", ACageIDX);
            postParameters.put("ASearchString", ASearchString);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("Can_CageIn", jsonParameters);
            jsonobjstr = jsonobj.getString("Can_CageInResult").toString();
            if (!jsonobjstr.equals("")) {
                Can_Cage_InResult = gson.fromJson(jsonobjstr, objCan_Cage_InResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService Can_CageIn. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return Can_Cage_InResult;
        }
    }

    public static objCage_InResult Cage_In(long AUserIDX, long ACageIDX, String NotBefore, ArrayList<String> AItems) {
        objCage_InResult Cage_InResult = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", "EED6AD08118FAB9AAEFF756C1B00101254F8B6C0");
            postParameters.put("AUserIDX", AUserIDX);
            postParameters.put("ACageIDX", ACageIDX);
            postParameters.put("NotBefore", NotBefore);
            postParameters.put("AItems", AItems);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("Cage_In", jsonParameters);
            jsonobjstr = jsonobj.getString("Cage_InResult").toString();
            if (!jsonobjstr.equals("")) {
                Cage_InResult = gson.fromJson(jsonobjstr, objCage_InResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService Cage_In. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return Cage_InResult;
        }
    }

    public static objL2_Verify_CageResult L2_Verify_Cage(String ARegID, String WDBarcode){
        objL2_Verify_CageResult L2_Verify_CageResult = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("WDBarcode", WDBarcode);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_Verify_Cage", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_Verify_CageResult").toString();
            if (!jsonobjstr.equals("")) {
                L2_Verify_CageResult = gson.fromJson(jsonobjstr, objL2_Verify_CageResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_Verify_Cage. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return L2_Verify_CageResult;
        }
    }

    public static objL2_Get_CageResult L2_Get_Cage(String ARegID, String WDBarcode){
        objL2_Get_CageResult L2_Get_CageResult = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("WDBarcode", WDBarcode);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_Get_Cage", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_Get_CageResult").toString();
            if (!jsonobjstr.equals("")) {
                L2_Get_CageResult = gson.fromJson(jsonobjstr, objL2_Get_CageResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_Get_Cage. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return L2_Get_CageResult;
        }
    }

    public static objL2_Verify_Cage_ItemResult L2_Verify_Cage_Item(String ARegID, long ACageIDX, String ASearchString) {
        objL2_Verify_Cage_ItemResult L2_Verify_Cage_ItemResult = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ACageIDX", ACageIDX);
            postParameters.put("ASearchString", ASearchString);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_Verify_Cage_Item", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_Verify_Cage_ItemResult").toString();
            if (!jsonobjstr.equals("")) {
                L2_Verify_Cage_ItemResult = gson.fromJson(jsonobjstr, objL2_Verify_Cage_ItemResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_Verify_Cage_Item. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return L2_Verify_Cage_ItemResult;
        }
    }

    public static objL2_Cage_OutResult L2_Cage_Out(String ARegID, long ACageIDX, ArrayList<String> AItems, ArrayList<String> Excluded){
        objL2_Cage_OutResult L2_Cage_OutResult = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ACageIDX", ACageIDX);
            postParameters.put("AItems", AItems);
            postParameters.put("Excluded", Excluded);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_Cage_Out", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_Cage_OutResult").toString();
            if (!jsonobjstr.equals("")) {
                L2_Cage_OutResult = gson.fromJson(jsonobjstr, objL2_Cage_OutResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_Cage_Out. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return L2_Cage_OutResult;
        }
    }

    public static byte[] L2_GetGCPassQRCode(String ARegID) {
        byte[] GetGCPassQRCodeResult = null;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("L2_GetGCPassQRCode", jsonParameters);
            JSONArray jarr = jobj.getJSONArray("L2_GetGCPassQRCodeResult");

            GetGCPassQRCodeResult = gson.fromJson(jarr.toString(), byte[].class);

        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception caught at WebService L2_GetGCPassQRCode. Exception: " + s, "", "", "", "");
            GetGCPassQRCodeResult = null;
        } finally {
            return GetGCPassQRCodeResult;
        }
    }

    public static Boolean L2_Write_GC_Log(String ARegID, String ARemarks, int AFlag, String AUTC) {
        Boolean L2_Write_GC_Log = false;
        JSONObject getL2_Write_GC_LogResultJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ARemarks", ARemarks);
            postParameters.put("AFlag", AFlag);
            postParameters.put("AUTC", AUTC);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            getL2_Write_GC_LogResultJsonObj = getJSONObj0("L2_Write_GC_Log", jsonParameters);
            L2_Write_GC_Log = (getL2_Write_GC_LogResultJsonObj != null) ? Boolean.parseBoolean(getL2_Write_GC_LogResultJsonObj.getString("L2_Write_GC_LogResult")) : false;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at WebService L2_Write_GC_Log. Exception: " + e.getMessage().toString(), "", "", "", "");
            L2_Write_GC_Log = false;
        } finally {
            return L2_Write_GC_Log;
        }
    }

    //Caging

    public static Boolean L2_DeleteProblem(String ARegID) {
        Boolean L2_DeleteProblemResult = false;
        JSONObject L2_DeleteProblemResultJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            L2_DeleteProblemResultJsonObj = getJSONObj0("L2_DeleteProblem", jsonParameters);
            L2_DeleteProblemResult = (L2_DeleteProblemResultJsonObj != null) ? Boolean.parseBoolean(L2_DeleteProblemResultJsonObj.getString("L2_DeleteProblemResult")) : false;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at WebService L2_DeleteProblem. Exception: " + e.getMessage().toString(), "", "", "", "");
            L2_DeleteProblemResult = false;
        } finally {
            return L2_DeleteProblemResult;
        }
    }

    public static Boolean L2_CreateProblem(String ARegID) {
        Boolean L2_CreateProblemResult = false;
        JSONObject L2_CreateProblemResultJsonObj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("ServiceDuration", 300);
            postParameters.put("RServiceDuration", 600);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            L2_CreateProblemResultJsonObj = getJSONObj0("L2_CreateProblem", jsonParameters);
            jsonobjstr =  L2_CreateProblemResultJsonObj.getString("L2_CreateProblemResult");
            L2_CreateProblemResult = !objGlobalMethods.IsEmptyString(jsonobjstr);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at WebService L2_CreateProblem. Exception: " + e.getMessage().toString(), "", "", "", "");
            L2_CreateProblemResult = false;
        } finally {
            return L2_CreateProblemResult;
        }
    }

    public static boolean L2_DeleteJobQueue(String ARegID) {
        Boolean L2_DeleteJobQueueResult = false;
        JSONObject L2_DeleteJobQueueResultJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            L2_DeleteJobQueueResultJsonObj = getJSONObj0("L2_DeleteJobQueue", jsonParameters);
            L2_DeleteJobQueueResult = (L2_DeleteJobQueueResultJsonObj != null) ? Boolean.parseBoolean(L2_DeleteJobQueueResultJsonObj.getString("L2_DeleteProblemResult")) : false;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at WebService L2_DeleteJobQueue. Exception: " + e.getMessage().toString(), "", "", "", "");
            L2_DeleteJobQueueResult = false;
        } finally {
            return L2_DeleteJobQueueResult;
        }
    }

    public static L2_LA_GetUserResult L2_LA_GetUser(String ARegID){
        L2_LA_GetUserResult GetUserResult = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_LA_GetUser", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_LA_GetUserResult").toString();
            if (!jsonobjstr.equals("")) {
                GetUserResult = gson.fromJson(jsonobjstr, L2_LA_GetUserResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_LA_GetUser. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return GetUserResult;
        }
    }

    public static L2_LA_UpdateMobileResult L2_LA_UpdateMobile(String ARegID, String AMobile) {
        L2_LA_UpdateMobileResult UpdateMobileResult = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AMobile", AMobile);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_LA_UpdateMobile", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_LA_UpdateMobileResult").toString();
            if (!jsonobjstr.equals("")) {
                UpdateMobileResult = gson.fromJson(jsonobjstr, L2_LA_UpdateMobileResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_LA_UpdateMobile. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return UpdateMobileResult;
        }
    }

    public static long VerifyGCID(long HOMESuserIDX, String GCID) {
        long VerifyGCIDResult = 0;

        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", "EED6AD08118FAB9AAEFF756C1B00101254F8B6C0");
            postParameters.put("AUserIDX", HOMESuserIDX);
            postParameters.put("AID", GCID);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            JSONObject jobj = getJSONObj0("VerifyGCID", jsonParameters);
            VerifyGCIDResult = Long.parseLong(jobj.getString("VerifyGCIDResult").toString());
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService VerifyGCID. Exception: " + e.getMessage(),"","","","");
        } finally {
            return VerifyGCIDResult;
        }
    }

    public static objL2QuickAddJobsResult L2_QuickAddJobs(String ARegID, int AJobType, CreditCard_PostData[] AJobs) {
        objL2QuickAddJobsResult L2QuickAddJobsResult = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobType", AJobType);
            postParameters.put("AJobs", AJobs);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_QuickAddJobs", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_QuickAddJobsResult").toString();
            if (!jsonobjstr.equals("")) {
                L2QuickAddJobsResult = gson.fromJson(jsonobjstr, objL2QuickAddJobsResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_LA_UpdateMobile. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return L2QuickAddJobsResult;
        }
    }

    public static Boolean L2_SubmitHOTO(String ARegID, HOTO AHoto) {
        Boolean L2_SubmitHOTOResult = false;
        JSONObject JsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AHoto", AHoto);
            String jsonParameters = gson.toJson(postParameters);

            JsonObj = getJSONObj0("L2_SubmitHOTO", jsonParameters);
            if (JsonObj != null) {
                L2_SubmitHOTOResult = Boolean.parseBoolean(JsonObj.getString("L2_SubmitHOTOResult").toString());
            } else {
                L2_SubmitHOTOResult = false;
            }
        } catch (Exception e) {
            L2_SubmitHOTOResult = false;
        } finally {
            return L2_SubmitHOTOResult;
        }
    }

    public static L2_RequestExtensionResult L2_RequestExtension(String ARegID, long AJobsIDX, int AMinutes) {
        L2_RequestExtensionResult res = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("AMinutes", AMinutes);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_RequestExtension", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_RequestExtensionResult").toString();
            if (!jsonobjstr.equals("")) {
                res = gson.fromJson(jsonobjstr, L2_RequestExtensionResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_RequestExtension. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return res;
        }
    }

    public static L2_Get_WorkScheduleResult L2_Get_WorkSchedule(String ARegID, String AFromDate, String AToDate){
        L2_Get_WorkScheduleResult res = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AFromDate", AFromDate);
            postParameters.put("AToDate", AToDate);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_Get_WorkSchedule", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_Get_WorkScheduleResult").toString();
            if (!jsonobjstr.equals("")) {
                res = gson.fromJson(jsonobjstr, L2_Get_WorkScheduleResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_RequestExtension. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return res;
        }
    }

    public static L2_GetJobTemperatureResult L2_GetJobTemperature(String ARegID, long AJobsIDX, int LastNEntries){
        L2_GetJobTemperatureResult res = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("LastNEntries", LastNEntries);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_GetJobTemperature", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_GetJobTemperatureResult");
            if (!jsonobjstr.equals("")) {
                res = gson.fromJson(jsonobjstr, L2_GetJobTemperatureResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_GetJobTemperature. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return res;
        }
    }

    public static JobTemperaturePayload[] L2_GetJobTemperatures(String ARegID, Long[] AJobsIDX, int LastNEntries) {
        JobTemperaturePayload[] res = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("LastNEntries", LastNEntries);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_GetJobTemperatures", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_GetJobTemperaturesResult");
            if (!jsonobjstr.equals("")) {
                res = gson.fromJson(jsonobjstr, JobTemperaturePayload[].class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_GetJobTemperatures. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return res;
        }
    }

    public static Boolean L2_Upload_Temperature(String ARegID, TemperaturePayload[] APayloads) {
        Boolean L2_Upload_TemperatureResult = false;
        JSONObject L2_TemperatureResultJsonObj;
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("APayloads", APayloads);
            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            L2_TemperatureResultJsonObj = getJSONObj0("L2_Upload_Temperature", jsonParameters);
            L2_Upload_TemperatureResult = (L2_TemperatureResultJsonObj != null) ? Boolean.parseBoolean(L2_TemperatureResultJsonObj.getString("L2_Upload_TemperatureResult")) : false;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at WebService L2_Upload_Temperature. Exception: " + e.getMessage(), "", "", "", "");
            L2_Upload_TemperatureResult = null;
        } finally {
            return L2_Upload_TemperatureResult;
        }
    }

    public static Upload_TemperatureResult Upload_Temperature(String APIKey, TemperaturePayload[] APayloads) {
        Upload_TemperatureResult res = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", APIKey);
            postParameters.put("APayloads", APayloads);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("Upload_Temperature", jsonParameters);
            jsonobjstr = jsonobj.getString("Upload_TemperatureResult");
            if (!jsonobjstr.equals("")) {
                res = gson.fromJson(jsonobjstr, Upload_TemperatureResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at WebService Upload_Temperature. Exception: " + e.getMessage(), "", "", "", "");
            res = null;
        } finally {
            return res;
        }
    }

    public static MPD_Group[] L2_Get_MPD_Groups(String ARegID) {
        MPD_Group[] res = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_Get_MPD_Groups", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_Get_MPD_GroupsResult");
            if (!jsonobjstr.equals("")) {
                res = gson.fromJson(jsonobjstr, MPD_Group[].class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_Get_MPD_Groups. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return res;
        }
    }

    public static MPD_Group MPD_GetGroup_ByBarcode(String aAPIKey, String AMPDItem_Barcode) {
        MPD_Group res = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("AMPDItem_Barcode", AMPDItem_Barcode);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("MPD_GetGroup_ByBarcode", jsonParameters);
            jsonobjstr = jsonobj.getString("MPD_GetGroup_ByBarcodeResult");
            if (!jsonobjstr.equals("")) {
                res = gson.fromJson(jsonobjstr, MPD_Group.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService MPD_GetGroup_ByBarcode. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return res;
        }
    }

    public static MPD_Group MPD_GetGroup(String aAPIKey, long aAGroupIDX) {
        MPD_Group res = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("AGroupIDX", aAGroupIDX);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("MPD_GetGroup", jsonParameters);
            jsonobjstr = jsonobj.getString("MPD_GetGroupResult");
            if (!jsonobjstr.equals("")) {
                res = gson.fromJson(jsonobjstr, MPD_Group.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService MPD_GetGroup. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return res;
        }
    }

    public static MPD_Attach_Jobs_To_MPDGroupIDXResult MPD_Attach_Jobs_To_MPDGroupIDX(String aAPIKey, String aAJobReferences, long aAGroupIDX, long aAUserIDX, long aADriverIDX) {
        MPD_Attach_Jobs_To_MPDGroupIDXResult res = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("AJobReferences", aAJobReferences);
            postParameters.put("AGroupIDX", aAGroupIDX);
            postParameters.put("AUserIDX", aAUserIDX);
            postParameters.put("ADriverIDX", aADriverIDX);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("MPD_Attach_Jobs_To_MPDGroupIDX", jsonParameters);
            jsonobjstr = jsonobj.getString("MPD_Attach_Jobs_To_MPDGroupIDXResult");
            if (!jsonobjstr.equals("")) {
                res = gson.fromJson(jsonobjstr, MPD_Attach_Jobs_To_MPDGroupIDXResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService MPD_Attach_Jobs_To_MPDGroupIDX. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return res;
        }
    }

    public static MPD_Detach_Jobs_From_MPDGroupIDXResult MPD_Detach_Jobs_From_MPDGroupIDX(String aAPIKey, String aAJobReferences, long aAGroupIDX, long aAUserIDX, long aADriverIDX) {
        MPD_Detach_Jobs_From_MPDGroupIDXResult res = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("AJobReferences", aAJobReferences);
            postParameters.put("AGroupIDX", aAGroupIDX);
            postParameters.put("AUserIDX", aAUserIDX);
            postParameters.put("ADriverIDX", aADriverIDX);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("MPD_Detach_Jobs_From_MPDGroupIDX", jsonParameters);
            jsonobjstr = jsonobj.getString("MPD_Detach_Jobs_From_MPDGroupIDXResult");
            if (!jsonobjstr.equals("")) {
                res = gson.fromJson(jsonobjstr, MPD_Detach_Jobs_From_MPDGroupIDXResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService MPD_Detach_Jobs_From_MPDGroupIDX. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return res;
        }
    }

    public static String MPD_AddGroupItemBySerialNumber(String aAPIKey, long aAGroupIDX, long aAUserIDX, String aSerialNumbers) {
        String MPD_AddGroupItemBySerialNumberResult = "";
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("AGroupIDX", aAGroupIDX);
            postParameters.put("AUserIDX", aAUserIDX);
            postParameters.put("SerialNumbers", aSerialNumbers);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("MPD_AddGroupItemBySerialNumber", jsonParameters);
            jsonobjstr = jsonobj.getString("MPD_AddGroupItemBySerialNumberResult");
            if (!jsonobjstr.equals("")) {
                MPD_AddGroupItemBySerialNumberResult = jsonobjstr;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService MPD_AddGroupItemBySerialNumber. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return MPD_AddGroupItemBySerialNumberResult;
        }
    }

    public static String MPD_RemoveGroupItemBySerialNumber(String aAPIKey, long aAGroupIDX, long aAUserIDX, String aSerialNumbers) {
        String MPD_RemoveGroupItemBySerialNumberResult = "";
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("AGroupIDX", aAGroupIDX);
            postParameters.put("AUserIDX", aAUserIDX);
            postParameters.put("SerialNumbers", aSerialNumbers);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("MPD_RemoveGroupItemBySerialNumber", jsonParameters);
            jsonobjstr = jsonobj.getString("MPD_RemoveGroupItemBySerialNumberResult");
            if (!jsonobjstr.equals("")) {
                MPD_RemoveGroupItemBySerialNumberResult = jsonobjstr;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService MPD_RemoveGroupItemBySerialNumber. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return MPD_RemoveGroupItemBySerialNumberResult;
        }
    }

    public static Inventory[] GetInventoryBatch(String aAPIKey, String ABatchID) {
        Inventory[] res = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("ABatchID", ABatchID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("GetInventoryBatch", jsonParameters);
            jsonobjstr = jsonobj.getString("GetInventoryBatchResult");
            if (!jsonobjstr.equals("")) {
                res = gson.fromJson(jsonobjstr, Inventory[].class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService GetInventoryBatch. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return res;
        }
    }

    public static L2_UploadStockVerificationListResult L2_UploadStockVerificationList(String aAPIKey, long aAUserIDX, long aADriverIDX, StockVerificationList aAStockVerificationList) {
        L2_UploadStockVerificationListResult res = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", aAPIKey);
            postParameters.put("AUserIDX", aAUserIDX);
            postParameters.put("ADriverIDX", aADriverIDX);
            postParameters.put("AStockVerificationList", aAStockVerificationList);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_UploadStockVerificationList", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_UploadStockVerificationListResult");
            if (!jsonobjstr.equals("")) {
                res = gson.fromJson(jsonobjstr, L2_UploadStockVerificationListResult.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_UploadStockVerificationList. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return res;
        }
    }

    public static xwsBase L2_Check_Cold_Item_Handling_Compliance(String ARegID, long AJobsIDX, String AUTC) {
        xwsBase b = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("AUTC", AUTC);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_Check_Cold_Item_Handling_Compliance", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_Check_Cold_Item_Handling_ComplianceResult");
            if (!jsonobjstr.equals("")) {
                b = gson.fromJson(jsonobjstr, xwsBase.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_Check_Cold_Item_Handling_Compliance. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return b;
        }
    }

    public static xwsBase L2_GetPaymentTerms(String ARegID) {
        xwsBase b = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("L2_GetPaymentTerms", jsonParameters);
            jsonobjstr = jsonobj.getString("L2_GetPaymentTermsResult");
            if (!jsonobjstr.equals("")) {
                b = gson.fromJson(jsonobjstr, xwsBase.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService L2_GetPaymentTerms. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return b;
        }
    }

    public static objJobInfo GetJob(long AJobsIDX) {
        objJobInfo ji = null;
        JSONObject jsonobj = null;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", "EED6AD08118FAB9AAEFF756C1B00101254F8B6C0");
            postParameters.put("AJobsIDX", AJobsIDX);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);

            jsonobj = getJSONObj0("GetJob", jsonParameters);
            jsonobjstr = jsonobj.getString("GetJobResult");
            if (!jsonobjstr.equals("")) {
                ji = gson.fromJson(jsonobjstr, objJobInfo.class);
            }
        } catch (Exception e) {
            ji = null;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at WebService GetJob. " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return ji;
        }
    }

    public static String ValidateJobInventory(long AUserIDX, long AJobsIDX, String ARemarks) {
        String ValidateJobInventoryResult = "";
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", "EED6AD08118FAB9AAEFF756C1B00101254F8B6C0");
            postParameters.put("AUserIDX", AUserIDX);
            postParameters.put("AJobsIDX", AJobsIDX);
            postParameters.put("ARemarks", ARemarks);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("ValidateJobInventory", jsonParameters);
            jsonobjstr = jsonobj.getString("ValidateJobInventoryResult");
            if (!jsonobjstr.equals("")) {
                ValidateJobInventoryResult = jsonobjstr;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService ValidateJobInventory. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return ValidateJobInventoryResult;
        }
    }

    public static long CheckJobStatus(long JobsIDX) {
        String CheckJobStatusResultstr = "";
        long CheckJobStatusResult = 0;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", "EED6AD08118FAB9AAEFF756C1B00101254F8B6C0");
            postParameters.put("JobsIDX", JobsIDX);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("CheckJobStatus", jsonParameters);
            jsonobjstr = jsonobj.getString("CheckJobStatusResult");
            if (!jsonobjstr.equals("")) {
                CheckJobStatusResult = Long.parseLong(jsonobjstr);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService CheckJobStatus. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return CheckJobStatusResult;
        }
    }

    public static xwsBase CheckIn_Passport(long AUserIDX, String APassportNumber, String AExpiryDate) {
        xwsBase b = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", "EED6AD08118FAB9AAEFF756C1B00101254F8B6C0");
            postParameters.put("AUserIDX", AUserIDX);
            postParameters.put("APassportNumber", APassportNumber);
            postParameters.put("AExpiryDate", AExpiryDate);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("CheckIn_Passport", jsonParameters);
            jsonobjstr = jsonobj.getString("CheckIn_PassportResult");
            if (!jsonobjstr.equals("")) {
                b = gson.fromJson(jsonobjstr, xwsBase.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService CheckIn_Passport. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return b;
        }
    }

    public static xwsBase CheckOut_Passport(String ARegID, String APassportNumber, String AExpiryDate) {
        xwsBase b = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("APassportNumber", APassportNumber);
            postParameters.put("AExpiryDate", AExpiryDate);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("CheckOut_Passport", jsonParameters);
            jsonobjstr = jsonobj.getString("CheckOut_PassportResult");
            if (!jsonobjstr.equals("")) {
                b = gson.fromJson(jsonobjstr, xwsBase.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService CheckOut_Passport. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return b;
        }
    }

    public static xwsBase CheckOut_Passport_By_Ops(long AUserIDX, String APassportNumber, String AExpiryDate) {
        xwsBase b = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("APIKey", "EED6AD08118FAB9AAEFF756C1B00101254F8B6C0");
            postParameters.put("AUserIDX", AUserIDX);
            postParameters.put("APassportNumber", APassportNumber);
            postParameters.put("AExpiryDate", AExpiryDate);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("CheckOut_Passport", jsonParameters);
            jsonobjstr = jsonobj.getString("CheckOut_PassportResult");
            if (!jsonobjstr.equals("")) {
                b = gson.fromJson(jsonobjstr, xwsBase.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService CheckOut_Passport_By_Ops. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return b;
        }
    }

    public static xwsBase Confirm_Collected_Passport(String ARegID, String APassportNumber, String AExpiryDate) {
        xwsBase b = null;
        JSONObject jsonobj;
        String jsonobjstr = "";
        try {
            Gson gson = new Gson();
            HashMap<String, Object> postParameters = new HashMap<String, Object>();
            postParameters.put("ARegID", ARegID);
            postParameters.put("APassportNumber", APassportNumber);
            postParameters.put("AExpiryDate", AExpiryDate);

            gson.toJson(postParameters);
            String jsonParameters = gson.toJson(postParameters);
            jsonobj = getJSONObj0("Confirm_Collected_Passport", jsonParameters);
            jsonobjstr = jsonobj.getString("Confirm_Collected_PassportResult");
            if (!jsonobjstr.equals("")) {
                b = gson.fromJson(jsonobjstr, xwsBase.class);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at WebService Confirm_Collected_Passport. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return b;
        }
    }

}


