package com.xdel.luke;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.objAddressStructure;
import com.xdel.luke.objClasses.objContactStructure;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objJobInfo;

public class JobInfoP2Activity extends AppCompatActivity {
    public static String frompage;
    //public static String regid;
    public static int accessFromDirection = 0;
    public static int TotalJobsCountPerCatTypePerPostalB4Updates = 0;
    public static Long CanGetReviewLinkJobsIDX = null;

    private Context context;
    private objJobInfo currJobInfo;
    private objJobInfo ji;
    private objJobInfo linkedJob;
    private objAddressStructure PUAddressStructure, DELAddressStructure;
    private objContactStructure PUcontactStructure, DELcontactStructure, DELRcontactStructure;
    private TextView lblJobNo, lblExpType, lblRdyTime, lblDelFrom, lblContent, lblPieces, lblWeight;
    private TextView lblJobNoVal, lblExpTypeVal, lblRdyTimeVal, lblDelFromVal, lblDelBy, lblDelByVal, lblextTil, lblextTilVal, lblContentVal, lblPiecesVal, lblWeightVal, lblAddress1, lblSI1,
            lblAddress2, lblSI2, lblCOD, lblDO, lblCODVal, lblPart, lblJobInfoCategoryDesc, lblSI1_1, lblbarcodeTag, lblbarcodeTagVal, lblExtras, lblAdditionalInfo, lbliarrive, lblCNNoVal;
    private EditText txtConNoteVal;
    private EditText txtLabelTagVal;
    private LinearLayout LLScrollView, LLbarcodeTag, LLPart, LLupdateTag, LLPUAddress, LLDELAddress, LLiArriveLbl, LLConsignmentInfo, LLimgJobChange;
    private Button btnDone, btnCancel, btnScanTag, btnUpdateLabelTag;
    String PUcompany, DELcompany, PUcontactTitle, DELcontactTitle, DELRcontactTitle, PUcontact, DELcontact, DELRcontact,
            PUdept, DELdept, DELRdept, PUtel, DELtel, DELRtel, PUOther, DELOther, DELROther, PUmobile, DELmobile, DELRmobile, PUaddress, DELaddress, PART;
    ConnectionDetector cd;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    private String cameraScannedCode, PUcontactAddressSIstr, DELcontactAddressSIstr;
    private String langType = "en";
    private TextView lblPUcoyVal, lblPUaddressVal, lblPUcontactVal, lblPUdeptVal, lblPUtelVal, lblPUothersVal, lblPUmobileVal, lblPUonbehalfVal,
            lblDELcoyVal, lblDELaddressVal, lblDELcontactVal, lblDELdeptVal, lblDELtelVal, lblDELothersVal, lblDELmobileVal, lblDELonbehalfVal, lblNFCtagvalue;
    private LinearLayout LLlblPUcoy, LLlblPUaddress, LllblPUcontact, LLlblPUdept, LLlblPUtel, LLlblPUothers, LLlblPUmobile, LLlblPUonbehalf,
            LLlblDELcoy, LLlblDELaddress, LLlblDELcontact, LLlblDELdept, LLlblDELtel, LLlblDELothers, LLlblDELmobile, LLlblDELonbehalf;
    TextView lblBtnPUFM, lblBtnDELTO;
    private LinearLayout LLOriginalHeader, LLAuthorizedHeader, LLlblDELRcontact, LLlblDELRdept, LLlblDELRtel, LLlblDELRothers, LLlblDELRmobile;
    private TextView lblDELRcontactVal, lblDELRdeptVal, lblDELRtelVal, lblDELRothersVal, lblDELRmobileVal;
    public final String MIME_TEXT_PLAIN = "text/plain";

    ScreenShotContentObserver screenShotContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");

        /*String languageToLoad = langType; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/

        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_job_info_p2);


    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {



            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at JobInfoP2Activity onResume. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
