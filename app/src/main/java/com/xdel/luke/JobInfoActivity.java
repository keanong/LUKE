package com.xdel.luke;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.xdel.luke.BCnSVC.SendQueuedEmail_Receiver;
import com.xdel.luke.BottomSheet.BottomSheet3DialogFragment;
import com.xdel.luke.BottomSheet.BottomSheetVerifyMeFragment;
import com.xdel.luke.BottomSheet.ProgressBarDialog;
import com.xdel.luke.BottomSheet.customFullScreenDialog;
import com.xdel.luke.BottomSheet.extDialog;
import com.xdel.luke.BottomSheet.promiseOptDialog;
import com.xdel.luke.BottomSheet.securitySealDialog;
import com.xdel.luke.BottomSheet.tempLoggerEntryDialog;
import com.xdel.luke.Ext.L2_RequestExtensionResult;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.FixedChecklist.fixedChecklists;
import com.xdel.luke.Locker.AuthActivity;
import com.xdel.luke.Locker.L2_LA_GetUserResult;
import com.xdel.luke.Locker.LockerProfileActivity;
import com.xdel.luke.QR.Contents;
import com.xdel.luke.QR.QRCodeEncoder;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.ShpmtVerification.ShpmtVActivity;
import com.xdel.luke.Voip.CallActivity;
import com.xdel.luke.Voip.MyAccount;
import com.xdel.luke.Voip.MyAppObserver;
import com.xdel.luke.Voip.MyBuddy;
import com.xdel.luke.Voip.MyCall;
import com.xdel.luke.Voip.VoipApp;
import com.xdel.luke.checklist.ChecklistVariables;
import com.xdel.luke.jobinfo.JobInfoLite;
import com.xdel.luke.messagingClasses.ChatMessage;
import com.xdel.luke.mpd.MPD_Group;
import com.xdel.luke.mpd.MPD_Group_Item;
import com.xdel.luke.mpd.MPD_Item;
import com.xdel.luke.objClasses.DestinationLocationCustom;
import com.xdel.luke.objClasses.LocationProvider;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objAddressStructure;
import com.xdel.luke.objClasses.objAuthDriverResult;
import com.xdel.luke.objClasses.objCallStatusHashmapList;
import com.xdel.luke.objClasses.objContactStructure;
import com.xdel.luke.objClasses.objDOCOD;
import com.xdel.luke.objClasses.objDOObject;
import com.xdel.luke.objClasses.objDelBlockUnits;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objUpdateCOD_PostData;
import com.xdel.luke.objClasses.xwsBase;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjJobs;
import com.xdel.luke.templogger.MacDevicePair;
import com.xdel.luke.templogger.TemperaturePayload;
import com.xdel.luke.templogger.UploadTempDataObj;
import com.xdel.luke.templogger.bttemploggerStandaloneclass;
import com.xdel.luke.templogger.objTemp;
import com.xdel.luke.templogger.temploggerclass;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class JobInfoActivity extends AppCompatActivity implements Handler.Callback, MyAppObserver, SensorEventListener {
    //public static Handler handler = new Handler();
    public static String frompage;
    //public static String regid;
    public static Boolean tempLoggerEntryDialogisCompleted;
    public static int accessFromDirection = 0;
    public static int TotalJobsCountPerCatTypePerPostalB4Updates = 0;
    public static Long CanGetReviewLinkJobsIDX = null;
    public static ArrayList<String> verifyItems = null;

    private Context context;
    private objJobInfo currJobInfo;
    private objJobInfo ji;
    private objJobInfo linkedJob;
    private objAddressStructure PUAddressStructure, DELAddressStructure;
    private objContactStructure PUcontactStructure, DELcontactStructure, DELRcontactStructure;
    private TextView lblJobNo, lblExpType, lblRdyTime, lblDelFrom, lblContent, lblPieces, lblWeight;
    private TextView lblJobNoVal, lblExpTypeVal, lblRdyTimeVal, lblDelFromVal, lblDelBy, lblDelByVal, lblextTil, lblextTilVal, lblContentVal, lblPiecesVal, lblWeightVal, lblAddress1, lblSI1,
            lblAddress2, lblSI2, lblCOD, lblDO, lblCODVal, lblPart, lblJobInfoCategoryDesc, lblSI1_1, lblbarcodeTag, lblbarcodeTagVal, lblExtras, lblAdditionalInfo, lbliarrive,
            lblpromiseOptFromVal, lblpromiseOptToVal;
    private EditText txtConNoteVal;
    private EditText txtLabelTagVal;
    private ImageView AddressListDNFimg, imgJobChange;
    private LinearLayout LLScrollView, LLiArriveBtn, LLCOD, LLDO, LLDipSpecialInstructions, LLSI1, LLSI2, LLbarcodeTag, LLPart, LLupdateTag, LLrdyDateTime,
            LLadditionalInfo, LLdelby, LLextTil, LLPUAddress, LLDELAddress, LLiArriveLbl, LLConsignmentInfo, LLimgJobChange, LLpromiseOptFrom, LLpromiseOptTo;
    private Button btnarr, btnUpdateCN, btndip, btnsubmit, btnFail, btnScanCN, btnScanTag, btnUpdateLabelTag, btnRtnCallActivity;
    String FromDateTime, PickByDateTime, ReadyDateTime, ToDateTime, ExtFromDateTime, ExtTilDateTime, ExpTypeDesc, lblFromDateTimeValStr, lblPickByDateTimeValStr, lblReadyDateTimeValStr,
            lblToDateTimeValStr, lblextTilValValStr, PUcompany, DELcompany, PUcontactTitle, DELcontactTitle, DELRcontactTitle, PUcontact, DELcontact, DELRcontact,
            PUdept, DELdept, DELRdept, PUtel, DELtel, DELRtel, PUOther, DELOther, DELROther, PUmobile, DELmobile, DELRmobile, PUaddress, DELaddress, PUsi, DELsi, SI, PART;
    private String PUcontactAddressSIstr = "";
    private String DELcontactAddressSIstr = "";
    private String cnno;
    private String Oldcnno;
    ConnectionDetector cd;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    private String JobInfoForFailureJsonStr;
    private LocationProvider mLocationProvider;
    private int catType;
    private String username;
    private String password;
    private String useridx;
    private String ishomesuser;
    private String[] UsernamePwd = new String[4];
    private String cameraScannedCode;
    private ProgressDialog pd;
    private ProgressDialog pdvoip;
    private ProgressDialog pdverifyMe;
    private String langType = "en";
    private MediaPlayer player;
    private Integer showRestrictionPromptInt = 0;
    private TextView lblPUcoyVal, lblPUaddressVal, lblPUcontactVal, lblPUdeptVal, lblPUtelVal, lblPUothersVal, lblPUmobileVal, lblPUonbehalfVal,
            lblDELcoyVal, lblDELaddressVal, lblDELcontactVal, lblDELdeptVal, lblDELtelVal, lblDELothersVal, lblDELmobileVal, lblDELonbehalfVal, lblNFCtagvalue,
            lblDELtel, lblDELothers, lblDELmobile, lblDELRtel, lblDELRothers, lblDELRmobile, lblDELtelValHidden, lblDELothersValHidden, lblDELmobileValHidden;
    private LinearLayout LLlblPUcoy, LLlblPUaddress, LllblPUcontact, LLlblPUdept, LLlblPUtel, LLlblPUothers, LLlblPUmobile, LLlblPUonbehalf,
            LLlblDELcoy, LLlblDELaddress, LLlblDELcontact, LLlblDELdept, LLlblDELtel, LLlblDELothers, LLlblDELmobile, LLlblDELonbehalf, LLCold;
    TextView lblBtnPUFM, lblBtnDELTO;
    NfcAdapter mNfcAdapter;
    CountDownTimer cdTimer;
    private LinearLayout LLOriginalHeader, LLAuthorizedHeader, LLlblDELRcontact, LLlblDELRdept, LLlblDELRtel, LLlblDELRothers, LLlblDELRmobile;
    private TextView lblDELRcontactVal, lblDELRdeptVal, lblDELRtelVal, lblDELRtelValHidden, lblDELRothersVal, lblDELRmobileVal;
    public final String MIME_TEXT_PLAIN = "text/plain";

    //voip
    public static VoipApp app = null;
    public static MyCall currentCall = null;
    public static MyAccount account = null;
    public static AccountConfig accCfg = null;
    private String lastRegStatus = "";
    public static String voipExt = "";
    private boolean showTel = false;
    public static Boolean maskContactNum;
    public static Boolean ongoingcall = false;
    public static SensorManager mSensorManager;
    public static Sensor mProximity;
    public static PowerManager.WakeLock proximityWakeLock;
    private PowerManager pm;

    private AlertDialog MultiCallalertDialog = null;
    private AlertDialog VerifyShipmentDialog = null;
    ScreenShotContentObserver screenShotContentObserver;
    private Boolean AreqNFCscan = false;
    private Boolean hasBTL = false;
    private boolean ahasOTP = false;
    private boolean showExtOpt = false;
    private boolean aOtpLocked = false;
    private boolean ahasVerifiedOTP = false;
    private boolean aCustIDVerificationNotRequired = false;
    private boolean ahasPin = false;
    //private boolean canReqExt = false;
    private Menu menu;
    private tempLoggerEntryDialog tle;
    private static customFullScreenDialog cfs;
    private static customFullScreenDialog cfsCold;
    private static customFullScreenDialog cfsNoNH;
    private BottomSheetDialogFragment bs;
    private BottomSheetDialogFragment bottomSheetDialogFragment;
    private promiseOptDialog pod;
    private extDialog ext;
    private securitySealDialog ssd;

    private objCallStatusHashmapList CallStatusHashmapList;

    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;
    TextView lblIsDefaultVRPpage;
    FloatingActionButton fabsi;
    ScrollView sv;
    private ProgressDialog pdTempRead;
    private ProgressBarDialog pdTempCompCheck;
    private ProgressBarDialog pdUploadingTemp;

    private void showEditTextID() {
        TextView et = null;
        String n = "";
        try {
            if (LLDipSpecialInstructions != null) {
                ArrayList<TextView> myEditTextList = new ArrayList<TextView>();
                for (int i = 0; i <= LLDipSpecialInstructions.getChildCount() - 1; i++) {
                    if (LLDipSpecialInstructions.getChildAt(i) instanceof TextView) {
                        myEditTextList.add((TextView) LLDipSpecialInstructions.getChildAt(i));
                    }
                }

                if (myEditTextList != null && myEditTextList.size() > 0) {
                    for (int i = 0; i <= myEditTextList.size() - 1; i++) {
                        et = myEditTextList.get(i);
                        if (et != null) {
                            n += et.getTag() + "\n";
                        }
                    }
                }

                if (!objGlobalMethods.IsEmptyString(n)) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), n).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
        Context ctx = myApp.getAppContext();

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

        setContentView(R.layout.activity_job_info_a);

        LLOriginalHeader = (LinearLayout) findViewById(R.id.LLOriginalHeader);
        LLAuthorizedHeader = (LinearLayout) findViewById(R.id.LLAuthorizedHeader);
        LLlblDELRcontact = (LinearLayout) findViewById(R.id.LLlblDELRcontact);
        LLlblDELRdept = (LinearLayout) findViewById(R.id.LLlblDELRdept);
        LLlblDELRtel = (LinearLayout) findViewById(R.id.LLlblDELRtel);
        LLlblDELRothers = (LinearLayout) findViewById(R.id.LLlblDELRothers);
        LLlblDELRmobile = (LinearLayout) findViewById(R.id.LLlblDELRmobile);
        LLConsignmentInfo = (LinearLayout) findViewById(R.id.LLConsignmentInfo);
        LLpromiseOptFrom = (LinearLayout) findViewById(R.id.LLpromiseOptFrom);
        LLpromiseOptTo = (LinearLayout) findViewById(R.id.LLpromiseOptTo);
        LLCold = (LinearLayout) findViewById(R.id.LLCold);
        lblpromiseOptFromVal = (TextView) findViewById(R.id.lblpromiseOptFromVal);
        lblpromiseOptToVal = (TextView) findViewById(R.id.lblpromiseOptToVal);
        lblDELRcontactVal = (TextView) findViewById(R.id.lblDELRcontactVal);
        lblDELRdeptVal = (TextView) findViewById(R.id.lblDELRdeptVal);
        lblDELRtel = (TextView) findViewById(R.id.lblDELRtel);
        lblDELRtelVal = (TextView) findViewById(R.id.lblDELRtelVal);
        lblDELRtelValHidden = (TextView) findViewById(R.id.lblDELRtelValHidden);
        lblDELRothers = (TextView) findViewById(R.id.lblDELRothers);
        lblDELRothersVal = (TextView) findViewById(R.id.lblDELRothersVal);
        lblDELRmobile = (TextView) findViewById(R.id.lblDELRmobile);
        lblDELRmobileVal = (TextView) findViewById(R.id.lblDELRmobileVal);

        lbliarrive = (TextView) findViewById(R.id.lbliarrive);
        lblJobNo = (TextView) findViewById(R.id.lblJobNo);
        lblExpType = (TextView) findViewById(R.id.lblExpType);
        lblRdyTime = (TextView) findViewById(R.id.lblRdyTime);
        lblDelFrom = (TextView) findViewById(R.id.lblDelFrom);
        lblDelBy = (TextView) findViewById(R.id.lblDelBy);
        lblextTil = (TextView) findViewById(R.id.lblextTil);
        lblContent = (TextView) findViewById(R.id.lblContent);
        lblPieces = (TextView) findViewById(R.id.lblPieces);
        lblWeight = (TextView) findViewById(R.id.lblWeight);
        lblCOD = (TextView) findViewById(R.id.lblCOD);
        lblNFCtagvalue = (TextView) findViewById(R.id.lblNFCtagvalue);
        lblExtras = (TextView) findViewById(R.id.lblExtras);
        lblJobNoVal = (TextView) findViewById(R.id.lblJobNoVal);
        txtConNoteVal = (EditText) findViewById(R.id.txtConNoteVal);
        txtLabelTagVal = (EditText) findViewById(R.id.txtLabelTagVal);
        lblbarcodeTag = (TextView) findViewById(R.id.lblbarcodeTag);
        lblbarcodeTagVal = (TextView) findViewById(R.id.lblbarcodeTagVal);
        lblExpTypeVal = (TextView) findViewById(R.id.lblExpTypeVal);
        lblRdyTimeVal = (TextView) findViewById(R.id.lblRdyTimeVal);
        lblDelFromVal = (TextView) findViewById(R.id.lblDelFromVal);
        lblDelByVal = (TextView) findViewById(R.id.lblDelByVal);
        lblextTilVal = (TextView) findViewById(R.id.lblextTilVal);
        lblContentVal = (TextView) findViewById(R.id.lblContentVal);
        lblPiecesVal = (TextView) findViewById(R.id.lblPiecesVal);
        lblWeightVal = (TextView) findViewById(R.id.lblWeightVal);
        //lblAddress1 = (TextView) findViewById(R.id.lblAddress1);
        lblSI1 = (TextView) findViewById(R.id.lblSI1);
        lblSI1_1 = (TextView) findViewById(R.id.lblSI1_1);
        //lblAddress2 = (TextView) findViewById(R.id.lblAddress2);
        lblSI2 = (TextView) findViewById(R.id.lblSI2);
        lblAdditionalInfo = (TextView) findViewById(R.id.lblAdditionalInfo);
        //lblJobInfoCategoryDesc = (TextView) findViewById(R.id.lblJobInfoCategoryDesc);
        btnarr = (Button) findViewById(R.id.btnarr);
        btnScanCN = (Button) findViewById(R.id.btnScanCN);
        btnUpdateCN = (Button) findViewById(R.id.btnUpdateCN);
        btnScanTag = (Button) findViewById(R.id.btnScanTag);
        btnUpdateLabelTag = (Button) findViewById(R.id.btnUpdateLabelTag);
        btndip = (Button) findViewById(R.id.btndip);
        btnsubmit = (Button) findViewById(R.id.btnsubmit);
        btnFail = (Button) findViewById(R.id.btnFail);
        btnRtnCallActivity = (Button) findViewById(R.id.btnRtnCallActivity);

        LLiArriveLbl = (LinearLayout) findViewById(R.id.LLiArriveLbl);
        LLCOD = (LinearLayout) findViewById(R.id.LLCOD);
        LLDO = (LinearLayout) findViewById(R.id.LLDO);
        LLupdateTag = (LinearLayout) findViewById(R.id.LLupdateTag);
        LLbarcodeTag = (LinearLayout) findViewById(R.id.LLbarcodeTag);
        LLrdyDateTime = (LinearLayout) findViewById(R.id.LLrdyDateTime);
        LLDipSpecialInstructions = (LinearLayout) findViewById(R.id.LLDipSpecialInstructions);
        LLSI1 = (LinearLayout) findViewById(R.id.LLSI1);
        LLSI2 = (LinearLayout) findViewById(R.id.LLSI2);
        LLadditionalInfo = (LinearLayout) findViewById(R.id.LLadditionalInfo);
        LLdelby = (LinearLayout) findViewById(R.id.LLdelby);
        LLextTil = (LinearLayout) findViewById(R.id.LLextTil);
        lblDO = (TextView) findViewById(R.id.lblDO);
        lblCODVal = (TextView) findViewById(R.id.lblCODVal);
        LLPart = (LinearLayout) findViewById(R.id.LLPart);
        LLPUAddress = (LinearLayout) findViewById(R.id.LLPUAddress);
        LLDELAddress = (LinearLayout) findViewById(R.id.LLDELAddress);
        lblPart = (TextView) findViewById(R.id.lblPart);
        //AddressListDNFimg = (ImageView) findViewById(R.id.AddressListDNFimg);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(ctx.getString(R.string.pd_waitprocessingupdates));

        pdvoip = new ProgressDialog(this);
        pdvoip.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdvoip.setCanceledOnTouchOutside(false);
        pdvoip.setMessage(ctx.getString(R.string.plswaitconnecting));

        pdverifyMe = new ProgressDialog(this);
        pdverifyMe.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdverifyMe.setCanceledOnTouchOutside(false);
        pdverifyMe.setMessage(ctx.getString(R.string.pd_waitprocessing));


        pdTempRead = new ProgressDialog(this);
        pdTempRead.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdTempRead.setCanceledOnTouchOutside(false);
        pdTempRead.setMessage(ctx.getString(R.string.reading_temp));

        lblPUcoyVal = (TextView) findViewById(R.id.lblPUcoyVal);
        lblPUaddressVal = (TextView) findViewById(R.id.lblPUaddressVal);
        lblPUcontactVal = (TextView) findViewById(R.id.lblPUcontactVal);
        lblPUdeptVal = (TextView) findViewById(R.id.lblPUdeptVal);
        lblPUtelVal = (TextView) findViewById(R.id.lblPUtelVal);
        lblPUothersVal = (TextView) findViewById(R.id.lblPUothersVal);
        lblPUmobileVal = (TextView) findViewById(R.id.lblPUmobileVal);
        lblPUonbehalfVal = (TextView) findViewById(R.id.lblPUonbehalfVal);
        lblDELcoyVal = (TextView) findViewById(R.id.lblDELcoyVal);
        lblDELaddressVal = (TextView) findViewById(R.id.lblDELaddressVal);
        lblDELcontactVal = (TextView) findViewById(R.id.lblDELcontactVal);
        lblDELdeptVal = (TextView) findViewById(R.id.lblDELdeptVal);
        lblDELtel = (TextView) findViewById(R.id.lblDELtel);
        lblDELtelVal = (TextView) findViewById(R.id.lblDELtelVal);
        lblDELtelValHidden = (TextView) findViewById(R.id.lblDELtelValHidden);
        lblDELothers = (TextView) findViewById(R.id.lblDELothers);
        lblDELothersVal = (TextView) findViewById(R.id.lblDELothersVal);
        lblDELothersValHidden = (TextView) findViewById(R.id.lblDELothersValHidden);
        lblDELmobile = (TextView) findViewById(R.id.lblDELmobile);
        lblDELmobileVal = (TextView) findViewById(R.id.lblDELmobileVal);
        lblDELmobileValHidden = (TextView) findViewById(R.id.lblDELmobileValHidden);
        lblDELonbehalfVal = (TextView) findViewById(R.id.lblDELonbehalfVal);

        LLScrollView = (LinearLayout) findViewById(R.id.LLScrollView);
        LLiArriveBtn = (LinearLayout) findViewById(R.id.LLiArriveBtn);
        LLlblPUcoy = (LinearLayout) findViewById(R.id.LLlblPUcoy);
        LLlblPUaddress = (LinearLayout) findViewById(R.id.LLlblPUaddress);
        LllblPUcontact = (LinearLayout) findViewById(R.id.LllblPUcontact);
        LLlblPUdept = (LinearLayout) findViewById(R.id.LLlblPUdept);
        LLlblPUtel = (LinearLayout) findViewById(R.id.LLlblPUtel);
        LLlblPUothers = (LinearLayout) findViewById(R.id.LLlblPUothers);
        LLlblPUmobile = (LinearLayout) findViewById(R.id.LLlblPUmobile);
        LLlblPUonbehalf = (LinearLayout) findViewById(R.id.LLlblPUonbehalf);
        LLlblDELcoy = (LinearLayout) findViewById(R.id.LLlblDELcoy);
        LLlblDELaddress = (LinearLayout) findViewById(R.id.LLlblDELaddress);
        LLlblDELcontact = (LinearLayout) findViewById(R.id.LLlblDELcontact);
        LLlblDELdept = (LinearLayout) findViewById(R.id.LLlblDELdept);
        LLlblDELtel = (LinearLayout) findViewById(R.id.LLlblDELtel);
        LLlblDELothers = (LinearLayout) findViewById(R.id.LLlblDELothers);
        LLlblDELmobile = (LinearLayout) findViewById(R.id.LLlblDELmobile);
        LLlblDELonbehalf = (LinearLayout) findViewById(R.id.LLlblDELonbehalf);

        imgJobChange = (ImageView) findViewById(R.id.imgJobChange);
        LLimgJobChange = (LinearLayout) findViewById(R.id.LLimgJobChange);

        lblBtnPUFM = (TextView) findViewById(R.id.lblBtnPUFM);
        lblBtnDELTO = (TextView) findViewById(R.id.lblBtnDELTO);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        voipExt = "";

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        lblExtras.setOnClickListener(MultiCOntactNoOnClick("1840/1841/1842", true));

        fabsi = (FloatingActionButton) findViewById(R.id.fabsi);
        sv = findViewById(R.id.sv);
    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            JobsHandler.clearSetDeliveredStatusStatic();
            JobsHandler.clearsetFailStatusActivityStatic();
            JobsHandler.clearSignatureNameActivityStatic();
            CODupdate.al = null;
            CODupdate.total = 0.0;
            try {
                if (ongoingcall == null || !ongoingcall) {
                    unRegisterSensorListener();
                } else {
                    mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
                }
                /*if (!myApp.isMobileDateOn()){
                    showMobileNetworkSettingDialog();
                    return;
                }*/

                int appFlag = myApp.getDeviceSettingsFlag();
                if (myApp.isAirplaneModeOnFlag(appFlag)) {
                    showChangeAirplaneModeSettingsDialog();
                    return;
                }
                if (myApp.isGpsOnFlag(appFlag)) {
                    showChangeGPSModeSettingsDialog();
                    return;
                }

                if (myApp.isAllPermissionGrantedFlag(appFlag)) {
                    showChangePermissionDialog();
                    return;
                }

                if (myApp.GPSisHighAccFlag(appFlag)) {
                    showChangeGPStoHighAccModeSettingsDialog();
                    return;
                }

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("JobInfoActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                context = myApp.getAppContext();

                try {
                    if (mNfcAdapter != null && myApp.isNfcEnabled(mNfcAdapter, context)) {
                        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
                        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
                        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};
                        PendingIntent nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                        mNfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilter, null);
                    }
                } catch (Exception e) {
                    String stack = JobsHandler.getStackTraceString(e);
                }

                lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

                objGlobalMethods.resetAllImageRelatedStaticVariables(false);
                JobsHandler.clearAllStaticFromPageAndNextActivityFields();
                JobsHandler.resetDOinvCheckList(GcmPreferences);

                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");

                String[] prefName = new String[]{"isCallLog", "isItemProof", "SecuritySealNumber", "tempUpdateaddUploadTaggingQueALPrefStr", "mChildCheckStates2PrefStr",
                        "checklistObjForMassOKP", "CanGetReviewLinkJobsIDXsJsonStr", "pJstr"};
                String[] prefvalue = new String[]{"", "", "", "", "",
                        "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                JobsHandler.resetrePositionedGroupItems(GcmPreferences);
                objGlobalMethods.clearOnResumeJobInfoActivityGcmPrefs(GcmPreferences);
                if (frompage == null || !frompage.equals("Jobs_Retrieval_Activity")) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "NFCLocationtagPrefVal", "");
                }
                JobsHandler.clearCheckListActivityStatic(GcmPreferences, false);
                JobsHandler.clearCODupdateActivityStatic();
                JobsHandler.checkForceLogOut();

                if (JobsHandler.hasOpsQ() && !JobsHandler.isReadingFirstQue_Location_Or_Job(myApp.currentActivity.getClass())) {
                    JobsHandler.redirectToVrpPage();
                } else {
                    //cd = new ConnectionDetector(context);
                    cd = new ConnectionDetector(null);
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                    username = UsernamePwd[0];
                    password = UsernamePwd[1];
                    useridx = UsernamePwd[2];
                    ishomesuser = UsernamePwd[3];

                    if (username.equals("") && password.equals("")) {
                        BackToLoginPage2();
                    }

                    if (cd.isConnectingToInternet()) {
                        objGlobalMethods.requireGcReLogin();
                    }

                    objGlobalMethods.checkGPSnPerMinRunAlarmRunning(ishomesuser);

                    String RegID = objGlobalMethods.getRegistrationIdOnly();
                    catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                    if (catType == 0) {
                        setTitle(myApp.getAppContext().getString(R.string.item_dashboard2PIP));
                    } else if (catType == 1) {
                        setTitle(myApp.getAppContext().getString(R.string.item_dashboard2PU));
                    } else if (catType == 2) {
                        setTitle(myApp.getAppContext().getString(R.string.item_dashboard2DIP));
                    } else if (catType == 3) {
                        setTitle(myApp.getAppContext().getString(R.string.item_dashboard2OKP));
                    } else if (catType == 4) {
                        setTitle(myApp.getAppContext().getString(R.string.item_dashboard2FP));
                    } else if (catType == 5) {
                        setTitle(myApp.getAppContext().getString(R.string.item_dashboard2FD));
                    } else if (catType == 6) {
                        setTitle(myApp.getAppContext().getString(R.string.item_dashboard2PD));
                    } else if (catType == 7) {
                        setTitle(myApp.getAppContext().getString(R.string.item_dashboard2TT));
                    }

                    btnarr = (Button) findViewById(R.id.btnarr);
                    btnFail = (Button) findViewById(R.id.btnFail);
                    btnsubmit = (Button) findViewById(R.id.btnsubmit);
                    btndip = (Button) findViewById(R.id.btndip);
                    btnRtnCallActivity = (Button) findViewById(R.id.btnRtnCallActivity);

                    // Check if Internet present
                    if (!cd.isConnectingToInternet()) {
                        // Internet Connection is not present
                        showTel = true;
                        Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                        // stop executing code by return
                        //return;
                        initPage();
                    } else {
                        initPage();
                    }
                    shideshowSubmitBtns(catType);
                    int queCount = 0;
                    queCount = JobsHandler.returnNumberOfQueToSend(UpdateJobInfoPreferences);
                    if (queCount > 0) {
                        DrawerMainActivity.handler.postDelayed(DrawerMainActivity.runnable, 2000);
                    } else {
                        //JobsHandler.doubleCheckUpdatePIPSQLite(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx, false);
                        JobsHandler.doubleCheckUpdatePIPSQLiteNew(GcmPreferences, UpdateJobInfoPreferences, RegID, username, useridx);

                        //doubleCheckUpdatePIP will call backendrun is it is not running,
                        //end of backendrun will do a resync
                    }

                    int qe = JobsHandler.numOfQueuedEmails();
                    if (qe > 0) {
                        objGlobalMethods.writeToFile("SendQueuedEmailReceiver called at JobInfoActivity.", "", "", "", "");
                        Intent SendQueuedEmailReceiver = new Intent(context, SendQueuedEmail_Receiver.class);
                        sendBroadcast(SendQueuedEmailReceiver);
                    }

                    String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");

                    String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
                    TotalJobsCountPerCatTypePerPostalB4Updates = !isVrpView() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal) :
                            !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(catType, Postal, SelectedVRPJobsIDXs)
                                    : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(catType, Postal);

                    //promptEarlyAttemptMsg();
                    JobInfoActivity.accessFromDirection = 0;
                    initFabSI();
                    //showEditTextID();
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at JobInfoActivity onResume. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            }
        }
    }

    private void show_cfsNoNH(long jobsidx, String AllValidTelNums, Date fromDateTime, Date toDateTime, Date ExtTilDateTime) {
        Context c = myApp.getAppContext();
        String msg = c.getString(R.string.attemptearlycalldip);
        String msg2 = "";
        String msg3 = c.getString(R.string.attemptearly_cannotfail);
        ArrayList<String> CrosstoNoteList = new ArrayList<>();
        ArrayList<String> toNoteList = new ArrayList<>();
        try {
            String noCallMade = "";
            /*if (!objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                JobsHandler.CheckAnyCallMadeWithin60min(AllValidTelNums);
                noCallMade = objGlobalMethods.getGCMPrefs(GcmPreferences, "noCallMade");
            }*/

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM h:mma");
            try {
                if (fromDateTime != null && toDateTime != null) {
                    msg2 += c.getString(R.string.deliverytimefrom) + "\n" + formatter.format(fromDateTime) + " " + c.getString(R.string.dateto) + " " + formatter.format(toDateTime);
                    if (ExtTilDateTime != null) {
                        msg2 += "\n" + c.getString(R.string.extended) + ": " + formatter.format(ExtTilDateTime);
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Excpetion at JobInfoActivity show_cfsNoNH set msg for early attempt. Exception: " + e.getMessage(), "", "", "", "");
            }

            objGlobalMethods.writeToFile("Early Delivery Attempt for jx" + jobsidx, "", "", "", "");
            if (objGlobalMethods.IsEmptyString(AllValidTelNums))
                msg = c.getString(R.string.attemptearlynocalldip);
            toNoteList.add(msg);
            toNoteList.add(msg2);
            CrosstoNoteList.add(msg3);

            cfsNoNH = new customFullScreenDialog();
            cfsNoNH.setHasColdItem(false);
            cfsNoNH.setShowTL(false);
            cfsNoNH.setDevice_Type(0);
            cfsNoNH.setPudlsi(toNoteList);
            cfsNoNH.setCrossSpecInst(CrosstoNoteList);

            if (cfsNoNH != null && cfsNoNH.getPudlsi() != null) {
                cfsNoNH.show(getSupportFragmentManager(), "cfsNoNH");
            }

            /*if (!objGlobalMethods.IsEmptyString(AllValidTelNums) && noCallMade.equals("t")) {
                objGlobalMethods.writeToFile("Early Delivery Attempt for jx" + jobsidx, "", "", "", "");

                toNoteList.add(msg);
                toNoteList.add(msg2);
                CrosstoNoteList.add(msg3);

                cfsNoNH = new customFullScreenDialog();
                cfsNoNH.setHasColdItem(false);
                cfsNoNH.setShowTL(false);
                cfsNoNH.setDevice_Type(0);
                cfsNoNH.setPudlsi(toNoteList);
                cfsNoNH.setCrossSpecInst(CrosstoNoteList);

                if (cfsNoNH != null && cfsNoNH.getPudlsi() != null) {
                    cfsNoNH.show(getSupportFragmentManager(), "cfsNoNH");
                }
            }*/
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobInfoActivity show_cfsNoNH. Exception: " + e.getMessage() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void promptEarlyAttemptMsg() {
        Context c = myApp.getAppContext();
        String msg = c.getString(R.string.attemptearlycalldip);
        try {
            if (currJobInfo == null) {
                Gson gson = new Gson();
                currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            }
            boolean isStandardRunJob = JobsHandler.isStandardRunJob(currJobInfo);
            boolean isXDelJob = JobsHandler.isXDelJob(currJobInfo);
            if (catType == 2 && accessFromDirection == 1 && !isStandardRunJob && !isXDelJob) {
                //boolean attemptTimeIsEarlierOrLaterThanWindow = JobsHandler.attemptTimeIsEarlierOrLaterThanWindow(catType, currJobInfo);
                boolean attemptTimeOneOrMoreDaysEarlierThanWindow = JobsHandler.attemptTimeOneOrMoreDaysEarlierThanWindow(catType, currJobInfo);

                /*String[] ContactlessCallPromptCheck = null;
                String ContactlessCallPromptMsg = "";
                String ContactlessCanProceed = "";*/
                String noCallMade = "";
                String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                //if (attemptTimeIsEarlierOrLaterThanWindow && !objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                if (attemptTimeOneOrMoreDaysEarlierThanWindow && !objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                    JobsHandler.CheckAnyCallMadeWithin60min(AllValidTelNums);
                    /*ContactlessCallPromptCheck = JobsHandler.getContactlessCallPromptMsg(2);
                    ContactlessCanProceed = ContactlessCallPromptCheck[0];
                    ContactlessCallPromptMsg = ContactlessCallPromptCheck[1];*/
                    noCallMade = objGlobalMethods.getGCMPrefs(GcmPreferences, "noCallMade");
                }

                //if (attemptTimeIsEarlierOrLaterThanWindow) {
                if (attemptTimeOneOrMoreDaysEarlierThanWindow) {
                    Date[] DatesAL = objGlobalMethods.returnAllDates(currJobInfo);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM h:mma");
                    try {
                        if (DatesAL[0] != null && DatesAL[3] != null) {
                            msg += "\n\n" + c.getString(R.string.deliverytimefrom) + "\n" + formatter.format(DatesAL[0]) + " " + c.getString(R.string.dateto) + " " + formatter.format(DatesAL[3]);
                            if (DatesAL[4] != null) {
                                msg += "\n" + c.getString(R.string.extended) + ": " + formatter.format(DatesAL[4]);
                            }
                        }
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Excpetion at JobInfoActivity onResume set msg for early attempt. Exception: " + e.getMessage(), "", "", "", "");
                    }
                }

                /*if (isReqCallOutFirst && !objGlobalMethods.IsEmptyString(AllValidTelNums) && !objGlobalMethods.IsEmptyString(ContactlessCallPromptMsg) && ContactlessCanProceed.equals("0")) {
                    objGlobalMethods.writeToFile("Require Callout first for jx" + currJobInfo.JobsIDX, "", "", "", "");
                    objGlobalMethods.showCustomCanCannotMsgPrompt("", attemptTimeIsEarlierOrLaterThanWindow ? msg : ContactlessCallPromptMsg, null, null);
                } else if (attemptTimeIsEarlierOrLaterThanWindow && !objGlobalMethods.IsEmptyString(AllValidTelNums) && noCallMade.equals("t")) {
                    objGlobalMethods.writeToFile("Early Delivery Attempt for jx" + currJobInfo.JobsIDX, "", "", "", "");
                    objGlobalMethods.showCustomCanCannotMsgPrompt("", msg, null, null);
                }*/
                //if (attemptTimeIsEarlierOrLaterThanWindow && !objGlobalMethods.IsEmptyString(AllValidTelNums) && noCallMade.equals("t")) {
                if (attemptTimeOneOrMoreDaysEarlierThanWindow && !objGlobalMethods.IsEmptyString(AllValidTelNums) && noCallMade.equals("t")) {
                    objGlobalMethods.writeToFile("Early Delivery Attempt for jx" + currJobInfo.JobsIDX, "", "", "", "");
                    objGlobalMethods.showCustomCanCannotMsgPrompt("", msg, null, null);
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity promptEarlyAttemptMsg. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean hasAutoFocus = false;
        PackageManager pm = getPackageManager();

        hasAutoFocus = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);

        if (hasAutoFocus) {
            int action = event.getAction();
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (action == KeyEvent.ACTION_DOWN) {
                        //TODO
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobInfoScanFrom", "");
                        launchQRScan();
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (action == KeyEvent.ACTION_DOWN) {
                        //TODO
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobInfoScanFrom", "");
                        launchQRScan();
                    }
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

        try {
            //objGlobalMethods.clearStaticActivities();
            DAO_Jobs.nullifystaticitems();
            if (cdTimer != null) {
                cdTimer.cancel();
                cdTimer = null;
            }
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            objGlobalMethods.setGCMPrefs(GcmPreferences, "ReloadDueNFCscan", "");
            mNfcAdapter = null;
            if (lblExtras != null) {
                lblExtras.setOnClickListener(null);
                lblExtras = null;
            }
            if (mSensorManager != null) {
                mProximity = null;
                mSensorManager.unregisterListener(this);
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() != CallActivity.class)
                    mSensorManager = null;
            }
            if (cfs != null) {
                cfs = null;
            }
            if (cfsCold != null) {
                cfsCold = null;
            }
            if (tle != null) {
                tle = null;
            }
            if (bs != null) {
                bs = null;
            }
            if (bottomSheetDialogFragment != null) {
                bottomSheetDialogFragment = null;
            }
            if (pod != null) {
                pod = null;
            }
            if (pm != null) {
                pm = null;
            }
            proximityWakeLock = null;
            if (ssd != null) {
                ssd = null;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity onDestroy. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity = null;
        try {
            unRegisterSensorListener();
            if (MultiCallalertDialog != null) {
                MultiCallalertDialog.cancel();
                MultiCallalertDialog = null;
            }
            if (VerifyShipmentDialog != null) {
                VerifyShipmentDialog.cancel();
                VerifyShipmentDialog = null;
            }
            if (mNfcAdapter != null)
                mNfcAdapter.disableForegroundDispatch(this);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private static void nullifyVOIP() {
        app = null;
        currentCall = null;
        account = null;
        accCfg = null;
    }

    private void launchQRScan() {
        Intent parentIntent;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "jobinfo");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        //parentIntent = new Intent(myApp.getAppContext(), QRBarcodeReader.class);
        parentIntent = new Intent(myApp.currentActivity, QRBarcodeReader.class);
        JobInfoActivity.accessFromDirection = 0;
        startActivity(parentIntent);
    }

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        //parentIntent = new Intent(myApp.getAppContext(), MainActivity.class);
        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    public void BackToLoginPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        Intent parentIntent;
        //parentIntent = new Intent(myApp.getAppContext(), MainActivity.class);
        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
        JobInfoActivity.accessFromDirection = 0;
        setFailStatusActivity.accessFromDirection = 0;
        setDeliveredStatusActivity.accessFromDirection = 0;
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
        //objGlobalMethods.stopAlarmForCommon();
        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(2);
        }
        FCMservice.notiNotRead = false;
        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(1);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            objGlobalMethods.stopActiveForegroundSvc();
        } else {
            if (FCMservice.mNotificationManager != null) {
                FCMservice.mNotificationManager.cancel(-1);
            }
        }
        JobsHandler.clearGPSQue(LocationPrefs);
        DAO_Jobs.dropLukeDB();
        startActivity(parentIntent);
        finish();
    }

    private void initPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String scanqr = objGlobalMethods.getGCMPrefs(GcmPreferences, "scanqr");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");

        if (!GcmPreferences.getString("hfJobInfoJsonStr", "").isEmpty()) {
            cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode").toUpperCase();

            if (!cameraScannedCode.equals("") && !scanqr.isEmpty() && !scanqr.equals("") && scanqr.equals("y")) {
                /*objGlobalMethods.setGCMPrefs(GcmPreferences, "NFCtagScannedVal", cameraScannedCode.toUpperCase());
                objGlobalMethods.setGCMPrefs(GcmPreferences, "ReloadDueNFCscan", "true");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");*/

                String[] prefName = new String[]{"NFCtagScannedVal", "ReloadDueNFCscan", "cameraScannedCode"};
                String[] prefvalue = new String[]{cameraScannedCode.toUpperCase(), "true", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                cameraScannedCode = "";
            }

            loadJobInfoDetails();

            //!currJobInfo.JobNo.equals("")
            txtConNoteVal.setOnClickListener(null);
            txtConNoteVal.setOnEditorActionListener(null);
            if (currJobInfo != null && (!currJobInfo.ConsignmentNote.equals("") || currJobInfo.ConsignmentNote != "")) {
                txtConNoteVal.setOnClickListener(txtConNoteValOnClick());
            } else {
                txtConNoteVal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (txtConNoteVal.getText().length() != 0 && txtConNoteVal.getText().toString() != "") {
                            if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                                return false;
                            if (actionId == 0 || actionId == EditorInfo.IME_ACTION_GO) {
                                // input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                                String cnVal = txtConNoteVal.getText().toString().toUpperCase();
                                if (currJobInfo == null) {
                                    Gson gson = new Gson();
                                    currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                                }
                                Boolean isSecurityTag = JobsHandler.validSecurityBagSealNumber(cnVal);
                                if (isSecurityTag) {
                                    showDialog(myApp.getAppContext().getString(R.string.alert_thisissecuritytag));
                                } else if (!objGlobalMethods.verifyBarcode(cnVal)) {
                                    if (JobsHandler.isJobsIDXcode(cnVal)) {
                                        if (JobsHandler.isMatchingJobsIDX(currJobInfo, cnVal)) {
                                            updateCNValviaBarcode(false, true, currJobInfo);
                                        } else {
                                            showDialog(myApp.getAppContext().getString(R.string.alert_pupromptwrongjx));
                                        }
                                    } else {
                                        updateCNValviaBarcode(false, false, currJobInfo);
                                    }
                                } else {
                                    //playTone();
                                    showDialog(myApp.getAppContext().getString(R.string.alert_thisistag));
                                }

                                if (getCurrentFocus() != null) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }
                            } else if (actionId == 6) {
                                return false;
                            }
                        } else {
                            //playTone();
                            showDialog(myApp.getAppContext().getString(R.string.alert_plsenterCN));
                        }

                        return true;
                    }
                });
            }

            txtLabelTagVal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (currJobInfo == null) {
                        Gson gson = new Gson();
                        currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                    }
                    Boolean isSecurityBag = JobsHandler.reqSecurityTag(currJobInfo);
                    if (txtLabelTagVal.getText().length() != 0 && txtLabelTagVal.getText().toString() != "") {
                        if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                            return false;
                        if (actionId == 0 || actionId == EditorInfo.IME_ACTION_GO) {
                            // input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                            //updateBarcodeViaBarcode(true);
                            if (isSecurityBag) {
                                updateBarcodeViaBarcode(true, currJobInfo);
                                /*if (!JobsHandler.validSecurityBagSealNumber(txtLabelTagVal.getText().toString())) {
                                    showDialog(myApp.getAppContext().getString(R.string.alert_InvalidSecuritySeal));
                                } else {
                                    updateBarcodeViaBarcode(true);
                                }*/
                            } else if (!isSecurityBag && objGlobalMethods.verifyBarcode(txtLabelTagVal.getText().toString())) {
                                updateBarcodeViaBarcode(true, currJobInfo);
                            } else {
                                showDialog(myApp.getAppContext().getString(R.string.alert_InvalidBarcode));
                            }

                            if (getCurrentFocus() != null) {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            }
                        } else if (actionId == 6) {
                            return false;
                        }
                    } else {
                        //playTone();
                        showDialog(isSecurityBag ? myApp.getAppContext().getString(R.string.alert_plsenterSecuritySeal) : myApp.getAppContext().getString(R.string.alert_plsenterBarcode));
                    }

                    return true;
                }
            });

            //cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode").toUpperCase();

            if (!cameraScannedCode.equals("")) {
                String JobInfoScanFrom = "";
                JobInfoScanFrom = objGlobalMethods.getGCMPrefs(GcmPreferences, "JobInfoScanFrom");
                if (!JobInfoScanFrom.equals("") && JobInfoScanFrom.equals("scanCN")) {
                    if (!objGlobalMethods.verifyBarcode(cameraScannedCode)) {
                        txtConNoteVal.setText(cameraScannedCode);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        //playTone();
                        showDialog(myApp.getAppContext().getString(R.string.alert_thisistag));
                    }
                } else if (!JobInfoScanFrom.equals("") && JobInfoScanFrom.equals("scanTag")) {
                    if (objGlobalMethods.verifyBarcode(cameraScannedCode)) {
                        txtLabelTagVal.setText(cameraScannedCode);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        //updateBarcodeViaBarcode(true);
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        //playTone();
                        showDialog(myApp.getAppContext().getString(R.string.alert_InvalidBarcode));
                    }
                } else if (!JobInfoScanFrom.equals("") && JobInfoScanFrom.equals("PUverification")) {
                    //check if is Tag
                    if (JobsHandler.isXdelArticleTag(cameraScannedCode)) {
                        //update tag then update job to PU
                        updatePU2(2, 1, cameraScannedCode);
                    }
                    //check if is JX
                    else if (JobsHandler.isJobsIDXcode(cameraScannedCode)) {
                        if (JobsHandler.isMatchingJobsIDX(currJobInfo, cameraScannedCode)) {
                            //update job to PU str8 away
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                            updatePU2(0, 0, "");
                        } else {
                            showDialog(myApp.getAppContext().getString(R.string.alert_pupromptwrongjx));
                        }
                    }
                    //all else
                    else {
                        //update val to job consignment field then update job to PU
                        updatePU2(2, 2, cameraScannedCode);
                    }
                } else if (!JobInfoScanFrom.equals("") && JobInfoScanFrom.equals("scanVerify")) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                    boolean verified = JobsHandler.isCorrectJobSelected(currJobInfo, cameraScannedCode, false, 0);
                    boolean hasColdItem = JobsHandler2.hasColdItem(currJobInfo);
                    if (verifyItems == null)
                        verifyItems = new ArrayList<>();
                    int jccount = JobsHandler2.jcCount(verifyItems);
                    int jxcount = JobsHandler2.jxCount(verifyItems);
                    boolean isAlps = JobsHandler.isAlpsClientIDX(currJobInfo);
                    boolean isLocker = JobsHandler.isLockerJob(currJobInfo);
                    Context ctx = myApp.getAppContext();
                    int pcs = currJobInfo.Pieces;
                    int scannedpcs = verifyItems != null ? verifyItems.size() : 0;
                    boolean isJC = JobsHandler2.isJC(cameraScannedCode);
                    boolean isJX = JobsHandler.isJobsIDXcode(cameraScannedCode);
                    if (verified) {
                        if (isAlps && hasColdItem && !isJC && !isJX) {
                            showDialogToRepromptShipmentVerificationNew(
                                    ((hasColdItem && pcs > 1) || (hasColdItem && pcs == 1)) ? ctx.getString(R.string.scan_pink_label) :
                                            ctx.getString(R.string.scan_white_label),
                                    pcs, verifyItems.size(), isAlps,
                                    ((hasColdItem && pcs > 1) || (hasColdItem && pcs == 1)) ? ctx.getString(R.string.scan_pink_label) :
                                            ctx.getString(R.string.scan_white_label));
                        } else if (pcs == 1 && isAlps && hasColdItem && isJC) {
                            verifyItems.add(isJC ? "jc" : "jx");
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                            btnPUOKPclickNew(null);
                        } else if (pcs == 1 && isAlps && hasColdItem && !isJC) {
                            //scan cold pink
                            showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_c), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_c));
                        } else if (pcs == 1 && isAlps && !hasColdItem) {
                            verifyItems.add(isJC ? "jc" : isJX ? "jx" : cameraScannedCode);
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                            btnPUOKPclickNew(null);
                        } else if (pcs == 1 && !isAlps) {
                            verifyItems.add(cameraScannedCode);
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                            btnPUOKPclickNew(null);
                        } else if (pcs > 1) {
                            if ((scannedpcs < pcs) && ((scannedpcs + 1) == pcs)) {
                                if (isAlps && hasColdItem && jccount == 0 && !isJC) {
                                    //scan cold pink
                                    showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_c), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_c));
                                } else if (isAlps && hasColdItem && jccount > 0 && !isJX) {
                                    //scan non-cold pink
                                    showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_nc), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_nc));
                                } else {
                                    verifyItems.add(isJC ? "jc" : isJX ? "jx" : cameraScannedCode);
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                                    btnPUOKPclickNew(null);
                                }
                            } else if (scannedpcs < pcs) {
                                if (jccount == 0 && jxcount == 0) {
                                    verifyItems.add(isJC ? "jc" : "jx");
                                    showDialogToRepromptShipmentVerificationNew(ctx.getString(
                                            isJC && hasColdItem ? R.string.scan_pink_label_nc :
                                                    isJX && hasColdItem && jccount == 0 ? R.string.scan_pink_label_cnc :
                                                            isJX && hasColdItem ? R.string.scan_pink_label_nc :
                                                                    R.string.scan_white_label),
                                            pcs, verifyItems.size(), isAlps,
                                            ctx.getString(
                                                    isJC && hasColdItem ? R.string.scan_pink_label_nc :
                                                            isJX && hasColdItem && jccount == 0 ? R.string.scan_pink_label_cnc :
                                                                    isJX && hasColdItem ? R.string.scan_pink_label_nc :
                                                                            R.string.scan_white_label));
                                } else if (isAlps && hasColdItem && jccount == 0 && !isJC) {
                                    verifyItems.add(isJC ? "jc" : "jx");
                                    showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_c), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_c));
                                } else if (isAlps && hasColdItem && jccount == 0 && isJC) {
                                    verifyItems.add(isJC ? "jc" : "jx");
                                    showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_nc), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_nc));
                                } else if (isAlps && hasColdItem && jccount > 0 && isJC) {
                                    showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_nc), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_nc));
                                } else if (isAlps && hasColdItem && jccount > 0 && isJX) {
                                    verifyItems.add(isJC ? "jc" : "jx");
                                    showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_nc), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_nc));
                                } else {
                                    verifyItems.add(isJC ? "jc" : isJX ? "jx" : cameraScannedCode);
                                    showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_white_label), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_white_label));
                                }
                            }
                        }
                    } else {
                        if (isAlps && hasColdItem) {
                            showDialogToRepromptShipmentVerificationNew(
                                    ctx.getString(R.string.verificationtxtno) + "\n\n" + ctx.getString(R.string.scan_pink_label), pcs, verifyItems.size(), isAlps,
                                    ctx.getString(R.string.scan_pink_label));
                        } else if (isAlps && !hasColdItem) {
                            showDialogToRepromptShipmentVerificationNew(
                                    ctx.getString(R.string.verificationtxtno) + "\n\n" + ctx.getString(R.string.scan_white_label), pcs, verifyItems.size(), isAlps,
                                    ctx.getString(R.string.scan_white_label));
                        } else {
                            showDialog(ctx.getString(R.string.verificationtxtno)).show();
                        }
                    }

                    /*boolean isCorrectJobSelectedbool = JobsHandler.isCorrectJobSelected(currJobInfo, cameraScannedCode, false, 0);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                    boolean hasColdItem = JobsHandler2.hasColdItem(currJobInfo);
                    Context ctx = myApp.getAppContext();
                    if (currJobInfo.Pieces > 0 && currJobInfo.Pieces < 2) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerified", isCorrectJobSelectedbool ? "y" : "");
                        if (isCorrectJobSelectedbool) {
                            btnPUOKPclick2(null);
                        } else {
                            showDialog(ctx.getString(R.string.verificationtxtno)).show();
                        }
                    } else if (currJobInfo.Pieces > 0 && currJobInfo.Pieces >= 2 && !hasColdItem) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerified", isCorrectJobSelectedbool ? "y" : "");
                        if (isCorrectJobSelectedbool) {
                            btnPUOKPclick2(null);
                        } else {
                            showDialog(ctx.getString(R.string.verificationtxtno)).show();
                        }
                    } else if (currJobInfo.Pieces > 0 && currJobInfo.Pieces >= 2 && hasColdItem) {
                        boolean isJC = false;
                        boolean isJX = false;
                        if (JobsHandler2.isJC(cameraScannedCode)) {
                            isJC = true;
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerifiedCold", isCorrectJobSelectedbool ? "y" : "");
                        } else if (JobsHandler.isJobsIDXcode(cameraScannedCode)) {
                            isJX = true;
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerified", isCorrectJobSelectedbool ? "y" : "");
                        }

                        if (JobsHandler2.ColdNonColdVerified(GcmPreferences)) {
                            if (isCorrectJobSelectedbool) {
                                btnPUOKPclick2(null);
                            } else {
                                showDialog(ctx.getString(R.string.verificationtxtno)).show();
                            }
                        } else if (!isJC && !isJX && isCorrectJobSelectedbool) { //as gd as not verified
                            showDialogToRepromptShipmentVerification(ctx.getString(R.string.scan_pink_label), true);
                        } else if ((isJC || isJX) && isCorrectJobSelectedbool) {
                            showDialogToRepromptShipmentVerification(isJC ? ctx.getString(R.string.scannext_nml) : ctx.getString(R.string.scannext_cold), true);
                        } else {
                            showDialog(ctx.getString(R.string.verificationtxtno)).show();
                        }
                    }*/
                } else {
                    if (catType == 0 || catType == 1) {
                        Boolean isSecurityBag = JobsHandler.reqSecurityTag(currJobInfo);
                        if (isSecurityBag || (!isSecurityBag && objGlobalMethods.verifyBarcode(cameraScannedCode))) {
                            txtLabelTagVal.setText(cameraScannedCode);
                        } else {
                            txtConNoteVal.setText(cameraScannedCode);
                        }
                    } else if (catType == 2 || catType == 3) {
                        if (!objGlobalMethods.verifyBarcode(cameraScannedCode)) {
                            txtConNoteVal.setText(cameraScannedCode);
                        } else {
                            showDialog(myApp.getAppContext().getString(R.string.alert_thisistag));
                        }
                    }
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                }
            }

            objGlobalMethods.setGCMPrefs(GcmPreferences, "scanqr", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "JobInfoScanFrom", "");
            txtConNoteVal.clearFocus();
            txtLabelTagVal.clearFocus();
            CallStatusHashmapList = objGlobalMethods.getCallStatusHashmapList(GcmPreferences);
        } else {
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata) + "\n" + "JobInfoJsonStr is empty.");
            BackStepWithoutUpdateSQLite();
        }
    }

    private void loadJobInfoDetails() {
        Boolean hasPickUpBy = false;
        Boolean isZoneTelAndHasAuthRecBool = false;
        try {
            String nfc_cap = "";
            Gson gson = new Gson();
            Date[] DatesAL;
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            nfc_cap = objGlobalMethods.getGCMPrefs(GcmPreferences, "nfc_cap");
            DecimalFormat dec = new DecimalFormat("#.00");
            String DOval = "0.00";
            String scrollTxt = "";
            currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            long idx = currJobInfo.JobsIDX;
            String jihfs = DAO_Jobs.gethfJobInfoJsonStrByJobsIDX(idx);
            currJobInfo = gson.fromJson(jihfs, objJobInfo.class);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM h:mma");
            SimpleDateFormat hmmaformatter = new SimpleDateFormat("h:mma");
            Date rdyDateTime;
            Date pubyDateTime;
            String avoid_lunch_txt = "";
            Boolean prompt_avoid_lunch_txt = false;
            isZoneTelAndHasAuthRecBool = JobsHandler.isZoneTelAndHasAuthRec(currJobInfo);
            Boolean isSecurityBag = false;
            Boolean hasSecuritySeal = false;
            Boolean reqNFCscan = false;
            Boolean hasIDVerification = false;
            int IMEIorSerialNumType = 0;
            Boolean hasIMEIorSerialNumber_Verification = false;
            Boolean hasIMEI_Verification = false;
            Boolean hasSerialNum_Verification = false;
            Boolean hasVoucher_Verification = false;
            String IMEIorSerialNumVal = "";
            String IMEIorSerialNumVal1 = "";
            String IMEIorSerialNumVal2 = "";
            String[] IMEIorSerialNumValBreakDown = null;
            String VoucherNumVal0 = "";
            String VoucherNumVal1 = "";
            String VoucherNumVal2 = "";
            String[] VoucherNumValBreakDown = null;
            String cust_id = "";
            boolean enableJobUpdates = false;
            boolean enableIarrBtn = false;
            boolean isPickAndSendJob = false;
            String PU_DEL_SI_Prompt_Txt = "";
            String SpecialInstrFromFlag = "";
            boolean isContactlessDelivery = false;
            Context ctx = myApp.getAppContext();
            ArrayList<String> tickSpecInst = new ArrayList<>();
            ArrayList<String> crossSpecInst = new ArrayList<>();
            ArrayList<String> toNoteList = new ArrayList<>();
            ArrayList<String> additionalList = new ArrayList<>();
            ArrayList<String> NoNHcrossSpecInst = new ArrayList<>();
            //ArrayList<String> InclusiveRecipientTypes = null;
            boolean CustIDVerificationNotRequired = false;
            boolean hasOTP = false;
            boolean isLockerJob = false;
            boolean isAlps = false;
            String linkedJobStr = "";
            boolean DipFromisOneOrMoreDaysLater = false;
            boolean exemptFromCallingForLBDoorGuard = false;
            int exemptFromCallingForLBDoorGuardmsgFlag = 0;
            boolean hasColdItem = JobsHandler2.hasColdItem(currJobInfo);
            boolean IsDIP;
            boolean reqLesserImgCompression = JobsHandler2.reqLesserImgCompression(currJobInfo);
            DestinationLocationCustom DestinationLocation = null;
            String DestinationLocationJsonStr = "";
            int DestinationTolerance = 0;
            boolean isEarlierThanDeliveryStartTime = false;
            boolean IsCLCN_For_DoorOpenPenalty = false;
            JobInfoLite jil = null;
            String JobInfoLiteJson = "";
            String DelBlock = "";
            String DelUnit_original = "";
            String DelUnit_combine = "";

            LLCold.setVisibility(hasColdItem ? View.VISIBLE : View.GONE);

            if (LLDipSpecialInstructions != null) {
                LLDipSpecialInstructions.removeAllViews();
            }
            if (LLadditionalInfo != null) {
                LLadditionalInfo.removeAllViews();
            }
            if (currJobInfo != null) {
                boolean isStandardRunJob = JobsHandler.isStandardRunJob(currJobInfo);
                boolean isXDelJob = JobsHandler.isXDelJob(currJobInfo);
                isAlps = JobsHandler.isAlpsClientIDX(currJobInfo);
                isLockerJob = JobsHandler.isLockerJob(currJobInfo);
                jil = new JobInfoLite(
                        currJobInfo.JobsIDX, currJobInfo.JobNo, currJobInfo.DOList, currJobInfo.BarcodeTags, currJobInfo.BarcodeTagsExt, currJobInfo.ConsignmentNote, hasColdItem,
                        isAlps, isLockerJob, isStandardRunJob, currJobInfo.Pieces
                );


                if (accessFromDirection == 1) {
                    objGlobalMethods.writeToFile("Viewing jx" + currJobInfo.JobsIDX, "", "", "", "");
                }

                /*IsDIP = DAO_Jobs.getDIPCatTypeCountByJobsIDX(currJobInfo.JobsIDX) > 0;
                if (catType == 2 && !IsDIP) {
                    showErrorAlert(ctx.getString(R.string.alert_unabletoattemptcallbacktocheck));
                    return;
                }*/

                lblDELtelVal.setOnClickListener(null);
                lblDELothersVal.setOnClickListener(null);
                lblDELmobileVal.setOnClickListener(null);
                lblDELRtelVal.setOnClickListener(null);
                lblDELRothersVal.setOnClickListener(null);
                lblDELRmobileVal.setOnClickListener(null);

                DestinationLocation = objGlobalMethods.getDestinationLocationCustom(currJobInfo, catType);
                DestinationTolerance = objGlobalMethods.getDestinationTolerance(currJobInfo, catType);
                DestinationLocation.setDestinationTolerance(DestinationTolerance);
                DestinationLocationJsonStr = gson.toJson(DestinationLocation);

                CustIDVerificationNotRequired = JobsHandler.CustIDVerificationNotRequired(currJobInfo);
                hasOTP = JobsHandler.hasOTP(currJobInfo);
                isContactlessDelivery = JobsHandler.isContactlessDelivery(currJobInfo);
                if (isContactlessDelivery && (catType == 2 || catType == 6)) {
                    String cld = ctx.getString(R.string.contactlessd);
                    additionalList.add(cld);
                    SpecialInstrFromFlag = cld;
                }
                if (CustIDVerificationNotRequired) {
                    String cidvnr = ctx.getString(R.string.idverifynotrequired);
                    additionalList.add(cidvnr);
                    SpecialInstrFromFlag = !objGlobalMethods.IsEmptyString(SpecialInstrFromFlag) ? "\n" + cidvnr : cidvnr;
                }

                String enableJobUpdatesPref = objGlobalMethods.getGCMPrefs(GcmPreferences, "enableJobUpdates");
                enableJobUpdates = enableJobUpdatesPref.equals("") || enableJobUpdatesPref.equals("true") ? true : false;
                isPickAndSendJob = currJobInfo.FLAG != null && currJobInfo.FLAG > 0 ? objGlobalMethods.isPickAndSendJob(currJobInfo.FLAG) : false;

                if (isLockerJob) {
                    tickSpecInst.add(ctx.getString(R.string.locker));
                }
                JobsHandler.setJobClientContactIDXforReceiverType(GcmPreferences, null, true);
                JobsHandler.setJobClientContactIDXforReceiverType(GcmPreferences, currJobInfo, false);

                cust_id = currJobInfo.IDVerification == null ? "" : currJobInfo.IDVerification;

                if (currJobInfo.OTPtries == null || currJobInfo.OTPtries < 3) {
                    currJobInfo.setTypeOfPinVerification(0);
                }
                if (currJobInfo.OTPtries == null || currJobInfo.OTPtries == 0) {
                    currJobInfo.setManualPinVerificaionText("");
                }
                hasSecuritySeal = JobsHandler.JobHasSecuritySeal(currJobInfo, false);
                isSecurityBag = JobsHandler.reqSecurityTag(currJobInfo);
                reqNFCscan = JobsHandler.reqNFCscan(currJobInfo);
                AreqNFCscan = reqNFCscan;
                hasBTL = hasBTLogger(currJobInfo.JobsIDX, GcmPreferences);

                LLConsignmentInfo.setVisibility((isSecurityBag && (catType == 0 || catType == 1)) ? View.GONE : View.VISIBLE);
                btnScanCN.setEnabled((isSecurityBag && (catType == 0 || catType == 1)) || (catType >= 2 && catType <= 7) ? false : true);
                btnUpdateCN.setEnabled((isSecurityBag && (catType == 0 || catType == 1)) || (catType >= 2 && catType <= 7) ? false : true);
                //LLConsignmentInfo.setVisibility((isSecurityBag && (catType == 0 || catType == 1)) || (catType >= 2 && catType <= 7) ? View.GONE : View.VISIBLE);
                hasIDVerification = JobsHandler.hasIDVerification(currJobInfo);
                hasIMEIorSerialNumber_Verification = JobsHandler.hasIMEIorSerialNumberForVerification(currJobInfo);
                hasVoucher_Verification = JobsHandler.hasVoucherForVerification(currJobInfo);
                if (hasIMEIorSerialNumber_Verification) {
                    IMEIorSerialNumVal = JobsHandler.getIMEIorSerialNumber(currJobInfo);
                    IMEIorSerialNumType = JobsHandler.isIMEIorSerialNum(IMEIorSerialNumVal);
                    hasIMEI_Verification = IMEIorSerialNumType == 1;
                    hasSerialNum_Verification = IMEIorSerialNumType == 2;
                    IMEIorSerialNumValBreakDown = JobsHandler.getIMEIorSerialorVoucherBreakdownString(IMEIorSerialNumType, IMEIorSerialNumVal);
                    if (IMEIorSerialNumValBreakDown != null && IMEIorSerialNumValBreakDown.length == 2) {
                        IMEIorSerialNumVal1 = IMEIorSerialNumValBreakDown[0];
                        IMEIorSerialNumVal2 = IMEIorSerialNumValBreakDown[1];
                    }
                }

                if (hasVoucher_Verification) {
                    VoucherNumVal0 = JobsHandler.getVoucherNumber(currJobInfo);
                    VoucherNumValBreakDown = JobsHandler.getIMEIorSerialorVoucherBreakdownString(3, VoucherNumVal0);
                    if (VoucherNumValBreakDown != null && VoucherNumValBreakDown.length == 2) {
                        VoucherNumVal1 = VoucherNumValBreakDown[0];
                        VoucherNumVal2 = VoucherNumValBreakDown[1];
                    }
                }

                if (currJobInfo.BarcodeTags != null && currJobInfo.BarcodeTags.length > 0) {
                    String barcodes = "";
                    for (int y = 0; y <= currJobInfo.BarcodeTags.length - 1; y++) {
                        barcodes += currJobInfo.BarcodeTags[y] + ", ";
                    }

                    if (!barcodes.equals("")) {
                        lblbarcodeTagVal.setText(barcodes.substring(0, barcodes.length() - 2));
                        LLbarcodeTag.setVisibility(hasSecuritySeal && (catType == 1 || catType == 2 || catType == 3 || catType == 5 || catType == 6 || catType == 7) ? View.GONE : View.VISIBLE);
                    } else {
                        LLbarcodeTag.setVisibility(View.GONE);
                    }
                } else {
                    LLbarcodeTag.setVisibility(View.GONE);
                }

                if ((catType == 0 || catType == 1) && isSecurityBag) {
                    btnScanTag.setText(ctx.getString(R.string.buttontext_scanSecuritySeal));
                    btnUpdateLabelTag.setText(ctx.getString(R.string.buttontext_updateSecuritySeal));
                }
                if (isSecurityBag) {
                    lblbarcodeTag.setText(ctx.getString(R.string.buttontext_SecuritySeal));
                }

                long JobsIDX = currJobInfo.JobsIDX;
                linkedJob = null;
                if (currJobInfo.LinkedJobs != null && currJobInfo.LinkedJobs.length > 0) {
                    linkedJob = currJobInfo.LinkedJobs[0];

                    linkedJobStr = gson.toJson(linkedJob);
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoLinkedJobInfo, linkedJobStr);
                } else {
                    linkedJobStr = "";
                }

                //mass saving prefs here from above

                long CNIDX = currJobInfo.ContactIDX;
                DatesAL = objGlobalMethods.returnAllDates(currJobInfo);
                String DatesALJson = gson.toJson(DatesAL);
                if (catType == 2 && accessFromDirection == 1 && !isStandardRunJob) {
                    isEarlierThanDeliveryStartTime = JobsHandler2.attemptTimeIsEarlierThanWindow(catType, DatesAL[0], DatesAL[3], DatesAL[5], DatesAL[4]);
                } else if (catType == 2 && !isStandardRunJob) {
                    isEarlierThanDeliveryStartTime = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isEarlierThanDeliveryStartTime"));
                }

                if (catType == 2 && accessFromDirection == 1 && !isStandardRunJob)
                    IsCLCN_For_DoorOpenPenalty = JobsHandler2.IsCLCN_For_DoorOpenPenalty(currJobInfo);

                lblJobNoVal.setText(!currJobInfo.JobNo.equals("") ? currJobInfo.JobNo : "");
                txtConNoteVal.setText(!currJobInfo.ConsignmentNote.equals("") ? currJobInfo.ConsignmentNote : "");

                Oldcnno = currJobInfo.ConsignmentNote;
                PART = "";
                PART = (currJobInfo.ParentJobsIDX == 0 && (currJobInfo.LinkedJobs != null && currJobInfo.LinkedJobs.length > 0)) ? ctx.getString(R.string.part1)
                        : currJobInfo.ParentJobsIDX > 0 ? ctx.getString(R.string.part2) : "";
                objGlobalMethods.writeToFile((currJobInfo.ParentJobsIDX == 0 && (currJobInfo.LinkedJobs != null && currJobInfo.LinkedJobs.length > 0))
                        ? "" + String.valueOf(currJobInfo.JobsIDX) + " has Part 2. Part 2 JobsIDX: " + String.valueOf(currJobInfo.LinkedJobs[0].JobsIDX)
                        : "", "", "", "", "");

                if (PART.equals("")) {
                    LLPart.setVisibility(View.GONE);
                } else {
                    lblPart.setText(PART);
                }

                ExpTypeDesc = "";
                try {
                    ExpTypeDesc = objGlobalVariables.returnExpTypeDESC(currJobInfo.ExpressType);
                } catch (Exception e) {

                }
                lblExpTypeVal.setText(ExpTypeDesc);

                if (DatesAL[2] != null) {
                    rdyDateTime = (Date) DatesAL[2];
                    ReadyDateTime = formatter.format(DatesAL[2]);
                } else {
                    rdyDateTime = null;
                    ReadyDateTime = "N.A";
                }
                lblReadyDateTimeValStr = ReadyDateTime;
                if (DatesAL[1] != null) {
                    pubyDateTime = (Date) DatesAL[1];
                    PickByDateTime = formatter.format(DatesAL[1]);
                } else {
                    pubyDateTime = null;
                    PickByDateTime = "N.A";
                }
                if (rdyDateTime != null && pubyDateTime != null) {
                    if (rdyDateTime.compareTo(pubyDateTime) != 0) {
                        lblReadyDateTimeValStr += "\n" + "(B4 )" + PickByDateTime;
                        hasPickUpBy = true;
                    }
                }

                FromDateTime = (DatesAL[0] != null) ? formatter.format(DatesAL[0]) : "N.A";
                lblFromDateTimeValStr = FromDateTime;
                DipFromisOneOrMoreDaysLater = JobsHandler.isOneOrMoreDaysLater(DatesAL[0].getTime());

                ToDateTime = (DatesAL[3] != null) ? formatter.format(DatesAL[3]) : "N.A";
                lblToDateTimeValStr = ToDateTime;

                ExtTilDateTime = (DatesAL[4] != null) ? formatter.format(DatesAL[4]) : "";
                ExtFromDateTime = (DatesAL[5] != null) ? formatter.format(DatesAL[5]) : "";

                //lblextTilValValStr = ExtTilDateTime;
                lblextTilValValStr = objGlobalMethods.IsEmptyString(ExtFromDateTime) && !objGlobalMethods.IsEmptyString(ExtTilDateTime) ? ExtTilDateTime :
                        !objGlobalMethods.IsEmptyString(ExtFromDateTime) && !objGlobalMethods.IsEmptyString(ExtTilDateTime) ? ExtFromDateTime + " - " + ExtTilDateTime : "";
                if (!objGlobalMethods.IsEmptyString(ExtFromDateTime) && !objGlobalMethods.IsEmptyString(ExtTilDateTime)) {
                    lblextTil.setText((!objGlobalMethods.IsEmptyString(ExtFromDateTime) && !objGlobalMethods.IsEmptyString(ExtTilDateTime)) ? ctx.getString(R.string.lblor) : ctx.getString(R.string.lblEXTTIL));
                }

                lblRdyTimeVal.setText(lblReadyDateTimeValStr);
                //lblDelFromVal.setText(lblFromDateTimeValStr);
                lblDelFromVal.setText(lblFromDateTimeValStr + " - " + lblToDateTimeValStr);
                lblDelByVal.setText(lblToDateTimeValStr);
                LLdelby.setVisibility(View.GONE);
                lblextTilVal.setText(lblextTilValValStr);

                LLextTil.setVisibility(!lblextTilValValStr.equals("") ? View.VISIBLE : View.GONE);

                Boolean IsExpSvc = objGlobalVariables.returnIsExpSvc(currJobInfo.ExpressType);
                if (IsExpSvc) {
                    LLextTil.setBackgroundColor(!lblextTilValValStr.equals("") ? Color.parseColor("#ffff0000") : Color.parseColor("#ffffff"));
                    lblextTil.setTextColor(!lblextTilValValStr.equals("") ? Color.parseColor("#ffffff") : Color.parseColor("#000000"));
                    lblextTilVal.setTextColor(!lblextTilValValStr.equals("") ? Color.parseColor("#ffffff") : Color.parseColor("#000000"));
                    LLdelby.setBackgroundColor(!lblextTilValValStr.equals("") ? Color.parseColor("#ffffff") : Color.parseColor("#ffff0000"));
                    lblDelBy.setTextColor(!lblextTilValValStr.equals("") ? Color.parseColor("#000000") : Color.parseColor("#ffffff"));
                    lblDelByVal.setTextColor(!lblextTilValValStr.equals("") ? Color.parseColor("#000000") : Color.parseColor("#ffffff"));
                } else {
                    LLextTil.setBackgroundColor(Color.parseColor("#ffffff"));
                    lblextTil.setTextColor(Color.parseColor("#000000"));
                    lblextTilVal.setTextColor(Color.parseColor("#000000"));
                    LLdelby.setBackgroundColor(Color.parseColor("#ffffff"));
                    lblDelBy.setTextColor(Color.parseColor("#000000"));
                    lblDelByVal.setTextColor(Color.parseColor("#000000"));
                }

                lblContentVal.setText(!currJobInfo.DeliveryContents.equals("") ? currJobInfo.DeliveryContents : "N.A");

                try {
                    lblPiecesVal.setText(String.valueOf(currJobInfo.Pieces));
                } catch (Exception e) {
                    lblPiecesVal.setText("0");
                }

                try {
                    lblWeightVal.setText(String.valueOf(currJobInfo.Weight) + "KG");
                } catch (Exception e) {
                    lblWeightVal.setText("0KG");
                }

                try {
                    if (currJobInfo.COD == 0.0) {
                        LLCOD.setVisibility(View.GONE);
                    } else {
                        if (currJobInfo.CODCollected == null || currJobInfo.CODCollected == false) {
                            lblCODVal.setText("$ " + String.valueOf(dec.format(currJobInfo.COD)));
                        } else if (currJobInfo.CODCollected) {
                            LLCOD.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    LLCOD.setVisibility(View.GONE);
                }

                PUsi = currJobInfo.PUSI != null && !currJobInfo.PUSI.equals("") ? currJobInfo.PUSI : "";
                if (catType == 0 && !PUsi.isEmpty() && !PUsi.equals("")) {
                    PU_DEL_SI_Prompt_Txt = PUsi;
                }
                DELsi = (currJobInfo.DLSI != null && !currJobInfo.DLSI.equals("")) ? currJobInfo.DLSI : "";
                if ((catType == 2 || catType == 6 || catType == 7) && !DELsi.isEmpty() && !DELsi.equals("")) {
                    //PU_DEL_SI_Prompt_Txt = "\u2022" + " " + DELsi;
                    PU_DEL_SI_Prompt_Txt = DELsi;
                }

                //shifted up to here
                PUAddressStructure = currJobInfo.PURedirectedLocation != null ? currJobInfo.PURedirectedLocation : currJobInfo.PULocation != null ? currJobInfo.PULocation : null;
                PUcontactStructure = PUAddressStructure.Contacts != null ? PUAddressStructure.Contacts[0] : null;

                if (currJobInfo.DLRedirectedLocation != null && objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "skiplocker"))) {
                    DELAddressStructure = !isZoneTelAndHasAuthRecBool ? currJobInfo.DLRedirectedLocation : currJobInfo.DLLocation;
                    DELRcontactStructure = (catType == 2 || catType == 3 || catType == 5 || catType == 6 || catType == 7) && (isZoneTelAndHasAuthRecBool && currJobInfo.DLRedirectedLocation.Contacts != null) ? currJobInfo.DLRedirectedLocation.Contacts[0] : null;
                } else if (currJobInfo.DLLocation != null) {
                    DELAddressStructure = currJobInfo.DLLocation;
                    DELRcontactStructure = null;
                } else {
                    DELAddressStructure = null;
                    DELRcontactStructure = null;
                }

                if (DELAddressStructure != null) {
                    DelBlock = DELAddressStructure.BLOCK;
                    DelUnit_original = DELAddressStructure.UNIT;
                    List<String> AllDelUnits = JobsHandler2.returnAllUnits(DelUnit_original).get(0);
                    List<String> AllDelUnitsCombined = JobsHandler2.returnAllUnits(DelUnit_original).get(1);

                    objDelBlockUnits dbu = new objDelBlockUnits(DelBlock, AllDelUnits, AllDelUnitsCombined);
                    String DelBlockUnitJsonStr = gson.toJson(dbu);
                    SharedPreferences JobInfoSP = objGlobalMethods.getJobInfoSP();
                    objGlobalMethods.setJobInfoSP_KeyValue(JobInfoSP, "DelBlockUnit", DelBlockUnitJsonStr);
                }

                if (jil != null)
                    JobInfoLiteJson = gson.toJson(jil);

                String[] prefName = new String[]{
                        "JobInfoLite",
                        "pcs",
                        "DatesALJson",
                        "isStandardRunJob",
                        "IsCLCN_For_DoorOpenPenalty",
                        "TypeOfPinVerificationStr",
                        "isEarlierThanDeliveryStartTime",
                        "DestinationLocationJsonStr",
                        "CNIDX",
                        "isLockerJob",
                        "CannotTakePhoto",
                        "jobflag",
                        "jobflag2",
                        "IDVerification",
                        "IMEIVerification",
                        "IMEIv1",
                        "IMEIv2",
                        "VOUCHERv0",
                        "VOUCHERv1",
                        "VOUCHERv2",
                        "hasIMEI_Verification",
                        "hasSerialNum_Verification",
                        "hasVoucher_Verification",
                        "reqLesserImgCompression",
                        objGlobalVariables.currJobInfoIDX,
                        "currJobInfoJobNo",
                        "currJobInfoCN",
                        objGlobalVariables.currJobInfoLinkedJobInfo};
                String[] prefvalue = new String[]{
                        !objGlobalMethods.IsEmptyString(JobInfoLiteJson) ? JobInfoLiteJson : "",
                        "",
                        !objGlobalMethods.IsEmptyString(DatesALJson) ? DatesALJson : "",
                        isStandardRunJob ? "t" : "",
                        IsCLCN_For_DoorOpenPenalty ? "t" : "",
                        String.valueOf(currJobInfo.getTypeOfPinVerification()),
                        isEarlierThanDeliveryStartTime ? "t" : "",
                        DestinationLocationJsonStr,
                        CNIDX > 0 ? String.valueOf(CNIDX) : "",
                        isLockerJob ? "t" : "",
                        JobsHandler.CannotTakePhoto(currJobInfo, catType) ? "1" : "0",
                        currJobInfo.FLAG != null && currJobInfo.FLAG > 0 ? String.valueOf(currJobInfo.FLAG) : "",
                        currJobInfo.FLAG2 != null && currJobInfo.FLAG2 > 0 ? String.valueOf(currJobInfo.FLAG2) : "",
                        hasIDVerification ? currJobInfo.IDVerification : "",
                        IMEIorSerialNumVal,
                        IMEIorSerialNumVal1,
                        IMEIorSerialNumVal2,
                        VoucherNumVal0,
                        VoucherNumVal1,
                        VoucherNumVal2,
                        hasIMEI_Verification ? "t" : "",
                        hasSerialNum_Verification ? "t" : "",
                        hasVoucher_Verification ? "t" : "",
                        reqLesserImgCompression ? "t" : "",
                        String.valueOf(JobsIDX),
                        String.valueOf(currJobInfo.JobNo),
                        String.valueOf(currJobInfo.ConsignmentNote),
                        linkedJobStr
                };
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                //mass saving prefs here from above

                DELcontactStructure = DELAddressStructure.Contacts != null ? DELAddressStructure.Contacts[0] : null;

                //PU LOCATION
                if (PUAddressStructure != null) {
                    PUcompany = (PUAddressStructure.COMPANY != null && !PUAddressStructure.COMPANY.equals("")) ? PUAddressStructure.COMPANY : "N.A";
                    PUaddress = objGlobalMethods.returnFullAddress(PUAddressStructure);
                    PUcontactTitle = PUcontactStructure != null && (PUcontactStructure.TITLE != null && !PUcontactStructure.TITLE.equals("")) ? PUcontactStructure.TITLE : "";
                    PUcontact = PUcontactStructure != null && (PUcontactStructure.NAME != null && !PUcontactStructure.NAME.equals("")) ? PUcontactStructure.NAME : "N.A";
                    PUdept = PUcontactStructure != null && (PUcontactStructure.DEPARTMENT != null && !PUcontactStructure.DEPARTMENT.equals("")) ? PUcontactStructure.DEPARTMENT : "N.A";
                    PUtel = PUcontactStructure != null && (PUcontactStructure.TEL != null && !PUcontactStructure.TEL.equals("")) ? objGlobalMethods.GetPhone(objGlobalMethods.getNonSpacesTelNum(PUcontactStructure.TEL)) : "N.A";
                    PUOther = PUcontactStructure != null && (PUcontactStructure.OTHER != null && !PUcontactStructure.OTHER.equals("")) ? objGlobalMethods.GetPhone(objGlobalMethods.getNonSpacesTelNum(PUcontactStructure.OTHER)) : "N.A";
                    PUmobile = PUcontactStructure != null && (PUcontactStructure.MOBILE != null && !PUcontactStructure.MOBILE.equals("")) ? objGlobalMethods.GetPhone(objGlobalMethods.getNonSpacesTelNum(PUcontactStructure.MOBILE)) : "N.A";
                } else {
                    PUcontactTitle = "";
                    PUcompany = "N.A";
                    PUaddress = "N.A";
                    PUcontact = "N.A";
                    PUdept = "N.A";
                    PUtel = "N.A";
                    PUOther = "N.A";
                    PUmobile = "N.A";
                }

                //DELIVERY LOCATION
                if (DELAddressStructure != null) {
                    DELcompany = (DELAddressStructure.COMPANY != null && !DELAddressStructure.COMPANY.equals("")) ? DELAddressStructure.COMPANY : "N.A";
                    DELaddress = objGlobalMethods.returnFullAddress(DELAddressStructure);
                    DELcontactTitle = DELcontactStructure != null && (DELcontactStructure.TITLE != null && !DELcontactStructure.TITLE.equals("")) ? DELcontactStructure.TITLE : "";
                    DELcontact = DELcontactStructure != null && (DELcontactStructure.NAME != null && !DELcontactStructure.NAME.equals("")) ? DELcontactStructure.NAME : "N.A";
                    DELdept = DELcontactStructure != null && (DELcontactStructure.DEPARTMENT != null && !DELcontactStructure.DEPARTMENT.equals("") && !DELcontactStructure.DEPARTMENT.equals(cust_id)) ? DELcontactStructure.DEPARTMENT : "N.A";
                    DELtel = DELcontactStructure != null && (DELcontactStructure.TEL != null && !DELcontactStructure.TEL.equals("")) ? objGlobalMethods.GetPhone(objGlobalMethods.getNonSpacesTelNum(DELcontactStructure.TEL)) : "N.A";
                    DELOther = DELcontactStructure != null && (DELcontactStructure.OTHER != null && !DELcontactStructure.OTHER.equals("")) ? objGlobalMethods.GetPhone(objGlobalMethods.getNonSpacesTelNum(DELcontactStructure.OTHER)) : "N.A";
                    DELmobile = DELcontactStructure != null && (DELcontactStructure.MOBILE != null && !DELcontactStructure.MOBILE.equals("")) ? objGlobalMethods.GetPhone(objGlobalMethods.getNonSpacesTelNum(DELcontactStructure.MOBILE)) : "N.A";

                    DELRcontactTitle = (catType == 2 || catType == 3 || catType == 5 || catType == 6 || catType == 7) && (isZoneTelAndHasAuthRecBool && DELRcontactStructure != null) &&
                            (DELRcontactStructure.TITLE != null && !DELRcontactStructure.TITLE.equals("")) ? DELRcontactStructure.TITLE : "";
                    DELRcontact = (catType == 2 || catType == 3 || catType == 5 || catType == 6 || catType == 7) && (isZoneTelAndHasAuthRecBool && DELRcontactStructure != null) &&
                            (DELRcontactStructure.NAME != null && !DELRcontactStructure.NAME.equals("")) ? DELRcontactStructure.NAME : "N.A";
                    DELRdept = (catType == 2 || catType == 3 || catType == 5 || catType == 6 || catType == 7) && (isZoneTelAndHasAuthRecBool && DELRcontactStructure != null) &&
                            (DELRcontactStructure.DEPARTMENT != null && !DELRcontactStructure.DEPARTMENT.equals("") && !DELRcontactStructure.DEPARTMENT.equals(cust_id)) ? DELRcontactStructure.DEPARTMENT : "N.A";
                    DELRtel = (catType == 2 || catType == 3 || catType == 5 || catType == 6 || catType == 7) && (isZoneTelAndHasAuthRecBool && DELRcontactStructure != null) &&
                            (DELRcontactStructure.TEL != null && !DELRcontactStructure.TEL.equals("")) ? objGlobalMethods.GetPhone(objGlobalMethods.getNonSpacesTelNum(DELRcontactStructure.TEL)) : "N.A";
                    DELROther = (catType == 2 || catType == 3 || catType == 5 || catType == 6 || catType == 7) && (isZoneTelAndHasAuthRecBool && DELRcontactStructure != null) &&
                            (DELRcontactStructure.OTHER != null && !DELRcontactStructure.OTHER.equals("")) ? objGlobalMethods.GetPhone(objGlobalMethods.getNonSpacesTelNum(DELRcontactStructure.OTHER)) : "N.A";
                    DELRmobile = (catType == 2 || catType == 3 || catType == 5 || catType == 6 || catType == 7) && (isZoneTelAndHasAuthRecBool && DELRcontactStructure != null) &&
                            (DELRcontactStructure.MOBILE != null && !DELRcontactStructure.MOBILE.equals("")) ? objGlobalMethods.GetPhone(objGlobalMethods.getNonSpacesTelNum(DELRcontactStructure.MOBILE)) : "N.A";

                    objGlobalMethods.setGCMPrefs(GcmPreferences, "IDVerificationName", hasIDVerification && isZoneTelAndHasAuthRecBool ? DELRcontact
                            : hasIDVerification && !isZoneTelAndHasAuthRecBool ? DELcontact : "");
                } else {
                    DELcontactTitle = "";
                    DELcompany = "N.A";
                    DELaddress = "N.A";
                    DELcontact = "N.A";
                    DELdept = "N.A";
                    DELtel = "N.A";
                    DELOther = "N.A";
                    DELmobile = "N.A";

                    DELRcontactTitle = "";
                    DELRcontact = "N.A";
                    DELRdept = "N.A";
                    DELRtel = "N.A";
                    DELROther = "N.A";
                    DELRmobile = "N.A";

                    objGlobalMethods.setGCMPrefs(GcmPreferences, "IDVerificationName", "");
                }

                //shifted up to here

                if (catType == 2 || catType == 3 || catType == 5 || catType == 6 || catType == 7) {
                    //dip,okp,fd, pd, transit
                    try {
                        if (currJobInfo.DOList != null && currJobInfo.DOList.length > 0) {
                            String doList = "\u2022" + " " + ctx.getString(R.string.lblDOINVOICE) + "\n";
                            for (int d = 0; d <= currJobInfo.DOList.length - 1; d++) {
                                DOval = String.valueOf(dec.format(currJobInfo.DOList[d].Amount));
                                if (DOval.equals(".00")) {
                                    DOval = "0.00";
                                }

                                if (d == currJobInfo.DOList.length - 1) {
                                    doList += "\u25BA" + " " + currJobInfo.DOList[d].Invoice + " COD $" + DOval;
                                } else {
                                    doList += "\u25BA" + " " + currJobInfo.DOList[d].Invoice + " COD $" + DOval + "\n";
                                }
                            }
                            lblDO.setText(doList);
                        } else {
                            LLDO.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        LLDO.setVisibility(View.GONE);
                    }

                    //InclusiveRecipientTypes = JobsHandler.rtnInclusiveRecipientTypes(GcmPreferences, langType, currJobInfo.ClientIDX, currJobInfo.ContactIDX);

                    if (isPickAndSendJob) {
                        additionalList.add(ctx.getString(R.string.jobtype_picknsend));
                        scrollTxt += "\u2022" + " " + ctx.getString(R.string.jobtype_picknsend) + "\n";
                    }

                    if (currJobInfo.DLLunch_Avoid != null && currJobInfo.DLLunch_Avoid &&
                            currJobInfo.DLLunch_From != null && !currJobInfo.DLLunch_From.equals("") &&
                            currJobInfo.DLLunch_To != null && !currJobInfo.DLLunch_To.equals("")) {
                        try {
                            Date DLLunch_From_d = objGlobalMethods.JsonDateToDate(currJobInfo.DLLunch_From);
                            String DLLunch_From_s = hmmaformatter.format(DLLunch_From_d);
                            Date DLLunch_To_d = objGlobalMethods.JsonDateToDate(currJobInfo.DLLunch_To);
                            String DLLunch_To_s = hmmaformatter.format(DLLunch_To_d);
                            //avoid_lunch_txt = "\u2022" + " " + ctx.getString(R.string.avoidlunchtime) + "  " + DLLunch_From_s + " - " + DLLunch_To_s;
                            avoid_lunch_txt = ctx.getString(R.string.avoidlunchtime) + DLLunch_From_s + " - " + DLLunch_To_s;
                            additionalList.add(ctx.getString(R.string.avoidlunchtime) + DLLunch_From_s + " - " + DLLunch_To_s);
                            scrollTxt += "\u2022" + " " + ctx.getString(R.string.avoidlunchtime) + "  " + DLLunch_From_s + " - " + DLLunch_To_s;
                            prompt_avoid_lunch_txt = objGlobalMethods.currTimeIsBetweenFromDateToDate(DLLunch_From_d, DLLunch_To_d);
                        } catch (Exception e) {
                            String s = JobsHandler.getStackTraceString(e);
                            objGlobalMethods.writeToFile("Exception at JobInfoActivity DLLunch_Avoid. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
                        }
                    }

                    try {
                        showRestrictionPromptInt = 0;
                        if (currJobInfo.FLAG != null && currJobInfo.FLAG > 0) {
                            String DipSpecialInstructions = "";
                            String speciallog = "";

                            Boolean intendedRecOnly = ((currJobInfo.FLAG & 16) == 16) ? true : false;
                            Boolean canSlotLB = (currJobInfo.FLAG & 1) == 1 ? true : false;
                            Boolean canSlotUnderDoor = (currJobInfo.FLAG & 2) == 2 ? true : false;
                            Boolean canGuard = (currJobInfo.FLAG & 4) == 4 ? true : false;
                            boolean canLeaveDoorStep = JobsHandler2.CanLeaveDoorStep(currJobInfo.FLAG2);

                            if (intendedRecOnly) {
                                if (hasOTP) {
                                    tickSpecInst.add(
                                            ctx.getString(R.string.wo_otp) + " " +
                                                    (!objGlobalMethods.IsEmptyString(DELcontact)
                                                            ? ctx.getString(R.string.canonlypassto) + "\n" + DELcontact.toUpperCase()
                                                            : ctx.getString(R.string.lblSI_1_4)));

                                    tickSpecInst.add(
                                            ctx.getString(R.string.w_otp) + " " +
                                                    ctx.getString(R.string.canonlypassto_w_otp));
                                } else {
                                    tickSpecInst.add(!objGlobalMethods.IsEmptyString(DELcontact)
                                            ? ctx.getString(R.string.canonlypassto) + "\n" + DELcontact.toUpperCase()
                                            : ctx.getString(R.string.lblSI_1_4));
                                }
                                /*tickSpecInst.add(!objGlobalMethods.IsEmptyString(DELcontact)
                                        ? ctx.getString(R.string.canonlypassto) + "\n" + DELcontact.toUpperCase()
                                        : ctx.getString(R.string.lblSI_1_4));*/

                                crossSpecInst.add(ctx.getString(R.string.lblSI_1_1_1));
                                crossSpecInst.add(ctx.getString(R.string.lblSI_1_2_2));
                                crossSpecInst.add(ctx.getString(R.string.lblSI_1_3_3));
                                crossSpecInst.add(ctx.getString(R.string.lblSI_1_6_6));

                                if (hasOTP) {
                                    DipSpecialInstructions += "\u2022" + " " +
                                            ctx.getString(R.string.wo_otp) + " " +
                                            (!objGlobalMethods.IsEmptyString(DELcontact)
                                                    ? ctx.getString(R.string.canonlypassto) + ":\n" + "  " + DELcontact.toUpperCase() + "\n\n"
                                                    : ctx.getString(R.string.lblSI_1_4) + "\n");

                                    DipSpecialInstructions += "\u2022" + " " +
                                            ctx.getString(R.string.w_otp) + " " +
                                            ctx.getString(R.string.canonlypassto_w_otp);
                                } else {
                                    DipSpecialInstructions += "\u2022" + " " + (!objGlobalMethods.IsEmptyString(DELcontact)
                                            ? ctx.getString(R.string.canonlypassto) + ":\n" + "  " + DELcontact.toUpperCase() + "\n\n"
                                            : ctx.getString(R.string.lblSI_1_4) + "\n");
                                }

                                /*DipSpecialInstructions += "\u2022" + " " + (!objGlobalMethods.IsEmptyString(DELcontact)
                                        ? ctx.getString(R.string.canonlypassto) + ":\n" + "  " + DELcontact.toUpperCase() + "\n\n"
                                        : ctx.getString(R.string.lblSI_1_4) + "\n");*/

                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_1_1) + "\n";
                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_2_2) + "\n";
                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_3_3) + "\n";
                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_6_6) + "\n";
                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_4_4) + "\n";
                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_5_5) + "\n";
                                showRestrictionPromptInt = 2;
                                speciallog += speciallog != "" ? ", can only pass to intended receiver" : "can only pass to intended receiver";
                            }
                            //else if ((explicit && (canSlotLB || canSlotUnderDoor || canGuard)) || !explicit) {
                            else if (canSlotLB || canSlotUnderDoor || canGuard || canLeaveDoorStep) {
                                /*if (isContactlessDelivery) {
                                    showRestrictionPromptInt = 1;
                                    tickSpecInst.add(ctx.getString(R.string.contactlessd));
                                    DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.contactlessd) + "\n";
                                    speciallog += speciallog != "" ? ", can slot letterbox" : "can slot letterbox";
                                }*/

                                exemptFromCallingForLBDoorGuard = JobsHandler2.exemptFromCallingForLBDoorGuard(currJobInfo);

                                if (canSlotLB) {
                                    showRestrictionPromptInt = 1;
                                    tickSpecInst.add(ctx.getString(R.string.lblSI_1_1));
                                    DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_1) + "\n";
                                    speciallog += speciallog != "" ? ", can slot letterbox" : "can slot letterbox";
                                    exemptFromCallingForLBDoorGuardmsgFlag = 1;
                                } else {
                                    crossSpecInst.add(ctx.getString(R.string.lblSI_1_1_1));
                                }
                                if (canSlotUnderDoor) { //door
                                    showRestrictionPromptInt = 1;
                                    tickSpecInst.add(ctx.getString(R.string.lblSI_1_2));
                                    DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_2) + "\n";
                                    speciallog += speciallog != "" ? ", can slot under door" : "can slot under door";
                                    exemptFromCallingForLBDoorGuardmsgFlag += 2;
                                } else {
                                    crossSpecInst.add(ctx.getString(R.string.lblSI_1_2_2));
                                }
                                if (canGuard) { //guard
                                    showRestrictionPromptInt = 1;
                                    tickSpecInst.add(ctx.getString(R.string.lblSI_1_3));
                                    DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_3) + "\n";
                                    speciallog += speciallog != "" ? ", can leave @ guardhouse" : "can leave @ guardhouse";
                                    exemptFromCallingForLBDoorGuardmsgFlag += 4;
                                } else {
                                    crossSpecInst.add(ctx.getString(R.string.lblSI_1_3_3));
                                }

                                if (canLeaveDoorStep) {
                                    showRestrictionPromptInt = 1;
                                    tickSpecInst.add(ctx.getString(R.string.lblSI_1_6));
                                    DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_6) + "\n";
                                    speciallog += speciallog != "" ? ", can leave @ doorstep" : "can leave @ doorstep";
                                    exemptFromCallingForLBDoorGuardmsgFlag += 2048;
                                } else {
                                    crossSpecInst.add(ctx.getString(R.string.lblSI_1_6_6));
                                }
                            }
                            //else if (!intendedRecOnly && explicit && !canSlotLB && !canSlotUnderDoor && !canGuard) {
                            else if (!intendedRecOnly && !canSlotLB && !canSlotUnderDoor && !canGuard && !canLeaveDoorStep) {
                                crossSpecInst.add(ctx.getString(R.string.lblSI_1_1_1));
                                crossSpecInst.add(ctx.getString(R.string.lblSI_1_2_2));
                                crossSpecInst.add(ctx.getString(R.string.lblSI_1_3_3));
                                crossSpecInst.add(ctx.getString(R.string.lblSI_1_6_6));
                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_1_1) + "\n";
                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_2_2) + "\n";
                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_3_3) + "\n";
                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_6_6) + "\n";
                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_4_4) + "\n";
                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.lblSI_1_5_5) + "\n";
                                showRestrictionPromptInt = 2;
                                speciallog += speciallog != "" ? ", cannot slot lb, door, guard, riser, cabinet, doorstep" : "cannot slot lb, door, guard, riser, cabinet, doorstep";
                            }

                            if ((currJobInfo.FLAG & 8) == 8) {
                                additionalList.add(ctx.getString(R.string.additionalInfoDNF));
                                scrollTxt += "\u2022" + " " + ctx.getString(R.string.additionalInfoDNF);
                                if (speciallog != "") {
                                    speciallog += ", is a DNF job";
                                } else {
                                    speciallog += "is a DNF job";
                                }
                            }
                            boolean hasPin = !objGlobalMethods.IsEmptyString(currJobInfo.PIN) ? true : false;
                            ahasPin = hasPin;
                            ahasOTP = hasOTP;
                            boolean hasCheckList = JobsHandler.HasCheckList(currJobInfo.CheckList, "DS") && catType == 2;
                            //TypeOfPinVerification OTP: 0 == 0 or within 3 tries, 1 == verified, 2 == after 3 fail tries, 3 == skipped
                            aOtpLocked = (currJobInfo.TypeOfPinVerification != null && currJobInfo.TypeOfPinVerification != 0 && currJobInfo.TypeOfPinVerification != 1 &&
                                    (currJobInfo.TypeOfPinVerification == 2 || currJobInfo.TypeOfPinVerification == 3));
                            ahasVerifiedOTP = currJobInfo.TypeOfPinVerification == null || currJobInfo.TypeOfPinVerification != 1 ? false : true;

                            if (hasOTP && !CustIDVerificationNotRequired && hasPin) {
                                additionalList.add(ctx.getString(R.string.additionalInfoOTP));
                                if (!scrollTxt.isEmpty()) {
                                    scrollTxt += "\n" + "\u2022" + " " + ctx.getString(R.string.additionalInfoOTP);
                                } else {
                                    scrollTxt += "\u2022" + " " + ctx.getString(R.string.additionalInfoOTP);
                                }
                                if (speciallog != "") {
                                    speciallog += ", requires OTP verification";
                                } else {
                                    speciallog += "requires OTP verification";
                                }
                            }

                            if (CustIDVerificationNotRequired) {
                                //additionalList.add(ctx.getString(R.string.idverifynotrequired));
                                if (!scrollTxt.isEmpty()) {
                                    scrollTxt += "\n" + "\u2022" + " " + ctx.getString(R.string.idverifynotrequired);
                                } else {
                                    scrollTxt += "\u2022" + " " + ctx.getString(R.string.idverifynotrequired);
                                }
                                if (speciallog != "") {
                                    speciallog += ", NRIC verification not required";
                                } else {
                                    speciallog += "NRIC verification not required";
                                }
                            }
                            if ((currJobInfo.FLAG & 1024) == 1024) {
                                additionalList.add(ctx.getString(R.string.additionalInfoSign));
                                if (!scrollTxt.isEmpty()) {
                                    scrollTxt += "\n" + "\u2022" + " " + ctx.getString(R.string.additionalInfoSign);
                                } else {
                                    scrollTxt += "\u2022" + " " + ctx.getString(R.string.additionalInfoSign);
                                }
                                if (speciallog != "") {
                                    speciallog += ", requires receiver signature";
                                } else {
                                    speciallog += "requires receiver signature";
                                }
                            }

                            DipSpecialInstructions = DipSpecialInstructions.trim();

                            crossSpecInst.add(ctx.getString(R.string.lblSI_1_4_4));
                            crossSpecInst.add(ctx.getString(R.string.lblSI_1_5_5));

                            if ((tickSpecInst == null) || (tickSpecInst != null && tickSpecInst.size() == 0 && isContactlessDelivery && !canSlotLB && !canSlotUnderDoor)) {
                                if (tickSpecInst == null) {
                                    tickSpecInst = new ArrayList<>();
                                }
                                tickSpecInst.add(ctx.getString(R.string.contactlessd));
                                DipSpecialInstructions += "\u2022" + " " + ctx.getString(R.string.contactlessd) + "\n";
                                speciallog += speciallog != "" ? ", Contactless Delivery" : "Contactless Delivery";
                            }

                            /*if (InclusiveRecipientTypes != null && InclusiveRecipientTypes.size() > 0) {
                                if (tickSpecInst == null) {
                                    tickSpecInst = new ArrayList<>();
                                }
                                for (int ir = 0; ir <= InclusiveRecipientTypes.size() - 1; ir++) {
                                    tickSpecInst.add(InclusiveRecipientTypes.get(ir));
                                }
                            }*/

                            if ((tickSpecInst != null && tickSpecInst.size() > 0) || (crossSpecInst != null && crossSpecInst.size() > 0)) {
                                lblSI1_1.setVisibility(View.GONE);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(2, 0, 0, 0);
                                //String tvid = "tvid";
                                if (tickSpecInst != null && tickSpecInst.size() > 0) {
                                    for (int ts = 0; ts <= tickSpecInst.size() - 1; ts++) {
                                        TextView tv = new TextView(ctx);
                                        //tv.setTag(tvid + String.valueOf(ts));
                                        tv.setText(tickSpecInst.get(ts));
                                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                        //tv.setTextColor(ContextCompat.getColor(ctx, R.color.tickgreen));
                                        tv.setTextColor(Color.BLACK);
                                        tv.setAllCaps(true);
                                        //tv.setBackgroundColor((ts % 2) == 0? Color.WHITE : Color.BLACK);
                                        tv.setTypeface(null, Typeface.BOLD);
                                        tv.setGravity(Gravity.CENTER_VERTICAL);
                                        tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_ok_black, 0, 0, 0);
                                        tv.setLayoutParams(params);
                                        tv.setBackgroundColor(ContextCompat.getColor(ctx, R.color.brightergreen));
                                        LLDipSpecialInstructions.addView(tv);
                                    }
                                }
                                if (crossSpecInst != null && crossSpecInst.size() > 0) {
                                    for (int cs = 0; cs <= crossSpecInst.size() - 1; cs++) {
                                        TextView tv = new TextView(ctx);
                                        //tv.setTag(tvid + String.valueOf(cs));
                                        tv.setText(crossSpecInst.get(cs));
                                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                        tv.setTextColor(Color.WHITE);
                                        tv.setAllCaps(true);
                                        //tv.setBackgroundColor((cs % 2) == 0? Color.WHITE : Color.BLACK);
                                        tv.setTypeface(null, Typeface.BOLD);
                                        tv.setGravity(Gravity.CENTER_VERTICAL);
                                        tv.setLayoutParams(params);
                                        tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_failpu_white, 0, 0, 0);
                                        tv.setBackgroundColor(ContextCompat.getColor(ctx, R.color.menu_red));
                                        LLDipSpecialInstructions.addView(tv);
                                    }
                                }
                                LLDipSpecialInstructions.setVisibility(View.VISIBLE);
                            } else {
                                LLDipSpecialInstructions.setVisibility(View.GONE);
                            }
                        } else if (currJobInfo.FLAG == null || currJobInfo.FLAG == 0) {
                            boolean canLeaveDoorStep = JobsHandler2.CanLeaveDoorStep(currJobInfo.FLAG2);
                            crossSpecInst.add(ctx.getString(R.string.lblSI_1_1_1));
                            crossSpecInst.add(ctx.getString(R.string.lblSI_1_2_2));
                            crossSpecInst.add(ctx.getString(R.string.lblSI_1_3_3));
                            if (!canLeaveDoorStep) {
                                crossSpecInst.add(ctx.getString(R.string.lblSI_1_6_6));
                            }
                            crossSpecInst.add(ctx.getString(R.string.lblSI_1_4_4));
                            crossSpecInst.add(ctx.getString(R.string.lblSI_1_5_5));
                            showRestrictionPromptInt = 2;

                            if (canLeaveDoorStep) {
                                if (tickSpecInst == null) {
                                    tickSpecInst = new ArrayList<>();
                                }
                                tickSpecInst.add(ctx.getString(R.string.lblSI_1_6));
                            }

                            if ((tickSpecInst != null && tickSpecInst.size() > 0) || (crossSpecInst != null && crossSpecInst.size() > 0)) {
                                lblSI1_1.setVisibility(View.GONE);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(2, 0, 0, 0);
                                //String tvid = "tvid";
                                if (tickSpecInst != null && tickSpecInst.size() > 0) {
                                    for (int ts = 0; ts <= tickSpecInst.size() - 1; ts++) {
                                        TextView tv = new TextView(ctx);
                                        //tv.setTag(tvid + String.valueOf(ts));
                                        tv.setText(tickSpecInst.get(ts));
                                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                        tv.setTextColor(ContextCompat.getColor(ctx, R.color.black));
                                        tv.setAllCaps(true);
                                        //tv.setBackgroundColor((ts % 2) == 0? Color.WHITE : Color.BLACK);
                                        tv.setTypeface(null, Typeface.BOLD);
                                        tv.setGravity(Gravity.CENTER_VERTICAL);
                                        tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_ok_black, 0, 0, 0);
                                        tv.setLayoutParams(params);
                                        tv.setBackgroundColor(ContextCompat.getColor(ctx, R.color.brightergreen));
                                        LLDipSpecialInstructions.addView(tv);
                                    }
                                }
                                if (crossSpecInst != null && crossSpecInst.size() > 0) {
                                    for (int cs = 0; cs <= crossSpecInst.size() - 1; cs++) {
                                        TextView tv = new TextView(ctx);
                                        //tv.setTag(tvid + String.valueOf(cs));
                                        tv.setText(crossSpecInst.get(cs));
                                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                        tv.setTextColor(ContextCompat.getColor(ctx, R.color.White));
                                        tv.setAllCaps(true);
                                        //tv.setBackgroundColor((cs % 2) == 0? Color.WHITE : Color.BLACK);
                                        tv.setTypeface(null, Typeface.BOLD);
                                        tv.setGravity(Gravity.CENTER_VERTICAL);
                                        tv.setLayoutParams(params);
                                        tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_failpu_white, 0, 0, 0);
                                        tv.setBackgroundColor(ContextCompat.getColor(ctx, R.color.menu_red));
                                        LLDipSpecialInstructions.addView(tv);
                                    }
                                }
                                LLDipSpecialInstructions.setVisibility(View.VISIBLE);
                            } else {
                                LLDipSpecialInstructions.setVisibility(View.GONE);
                            }

                            //LLDipSpecialInstructions.setVisibility(View.GONE);
                            objGlobalMethods.writeToFile("Job Flag is null or 0. No other special instructions for this job " + String.valueOf(JobsIDX) + ".", "", "", "", "");
                        }

                        try {
                            if ((catType == 2 || catType == 6 || catType == 7) && currJobInfo.DOList != null && currJobInfo.DOList.length > 0) {
                                int FlagSum = 0;
                                for (int d = 0; d <= currJobInfo.DOList.length - 1; d++) {
                                    objDOCOD[] DOCOD = currJobInfo.DOList[d].CODList;
                                    if (DOCOD != null && DOCOD.length > 0) {
                                        for (int c = 0; c <= DOCOD.length - 1; c++) {
                                            objDOCOD dcod = DOCOD[c];
                                            if (dcod != null) {
                                                if (dcod.AmountTypeIDX == 3 && (FlagSum & 4) != 4) {
                                                    FlagSum += 4;
                                                } else if (dcod.AmountTypeIDX == 4 && (FlagSum & 8) != 8) {
                                                    FlagSum += 8;
                                                } else if (dcod.AmountTypeIDX == 6 && (FlagSum & 16) != 16) {
                                                    FlagSum += 16;
                                                }
                                            }
                                            if ((FlagSum & 4) == 4 && (FlagSum & 8) == 8 && (FlagSum & 16) == 16) {
                                                break;
                                            }
                                        }
                                    }
                                }
                                if ((FlagSum & 4) == 4) {
                                    additionalList.add(ctx.getString(R.string.jobtype_nets));
                                    scrollTxt += "\n" + "\u2022" + " " + ctx.getString(R.string.jobtype_nets);
                                }
                                if ((FlagSum & 8) == 8) {
                                    additionalList.add(ctx.getString(R.string.jobtype_visamaster));
                                    scrollTxt += "\n" + "\u2022" + " " + ctx.getString(R.string.jobtype_visamaster);
                                }
                                if ((FlagSum & 16) == 16) {
                                    additionalList.add(ctx.getString(R.string.jobtype_tradein));
                                    scrollTxt += "\n" + "\u2022" + " " + ctx.getString(R.string.jobtype_tradein);
                                }
                            }
                        } catch (Exception e) {
                            String s = e.getMessage().toString();
                            objGlobalMethods.writeToFile("Exception at JobInfoActivity attempt to display nets visa trade in wordings. Exception: " + s, "", "", "", "");
                        }
                    } catch (Exception e) {
                        LLDipSpecialInstructions.setVisibility(View.GONE);
                        String stack = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception caught at JobInfoActivity loadJobInfoDetails while reading job FLAG. No other special instructions for this job "
                                + String.valueOf(JobsIDX) + ". Exception: " + e.getMessage().toString() + " \nStack: " + stack, "", "", "", "");
                    }
                } else {
                    if (isPickAndSendJob) {
                        additionalList.add(ctx.getString(R.string.jobtype_picknsend));
                        scrollTxt += "\u2022" + " " + ctx.getString(R.string.jobtype_picknsend) + "\n";
                    }

                    if (currJobInfo.PULunch_Avoid != null && currJobInfo.PULunch_Avoid &&
                            currJobInfo.PULunch_From != null && !currJobInfo.PULunch_From.equals("") &&
                            currJobInfo.PULunch_To != null && !currJobInfo.PULunch_To.equals("")) {
                        try {
                            Date PULunch_From_d = objGlobalMethods.JsonDateToDate(currJobInfo.PULunch_From);
                            String PULunch_From_s = hmmaformatter.format(PULunch_From_d);
                            Date PULunch_To_d = objGlobalMethods.JsonDateToDate(currJobInfo.PULunch_To);
                            String PULunch_To_s = hmmaformatter.format(PULunch_To_d);
                            //avoid_lunch_txt += "\u2022" + " " + ctx.getString(R.string.avoidlunchtime) + "  " + PULunch_From_s + " - " + PULunch_To_s;
                            avoid_lunch_txt += ctx.getString(R.string.avoidlunchtime) + PULunch_From_s + " - " + PULunch_To_s;
                            additionalList.add(ctx.getString(R.string.avoidlunchtime) + PULunch_From_s + " - " + PULunch_To_s);
                            scrollTxt += "\u2022" + " " + ctx.getString(R.string.avoidlunchtime) + "  " + PULunch_From_s + " - " + PULunch_To_s;
                            prompt_avoid_lunch_txt = objGlobalMethods.currTimeIsBetweenFromDateToDate(PULunch_From_d, PULunch_To_d);
                        } catch (Exception e) {
                            String s = JobsHandler.getStackTraceString(e);
                            objGlobalMethods.writeToFile("Exception at JobInfoActivity DLLunch_Avoid. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
                        }
                    }

                    lblDO.setVisibility(View.GONE);
                    LLDipSpecialInstructions.setVisibility(View.GONE);
                }

                if (!prompt_avoid_lunch_txt && !objGlobalMethods.IsEmptyString(PU_DEL_SI_Prompt_Txt)) {
                    prompt_avoid_lunch_txt = true;
                    avoid_lunch_txt = !objGlobalMethods.IsEmptyString(SpecialInstrFromFlag) ? SpecialInstrFromFlag + "\n\n" + PU_DEL_SI_Prompt_Txt : PU_DEL_SI_Prompt_Txt;
                    if (!objGlobalMethods.IsEmptyString(SpecialInstrFromFlag)) {
                        toNoteList.add(SpecialInstrFromFlag);
                    }
                    toNoteList.add(PU_DEL_SI_Prompt_Txt);
                } else if (prompt_avoid_lunch_txt && !objGlobalMethods.IsEmptyString(avoid_lunch_txt) && !objGlobalMethods.IsEmptyString(PU_DEL_SI_Prompt_Txt)) {
                    //avoid_lunch_txt = !objGlobalMethods.IsEmptyString(SpecialInstrFromFlag) ? SpecialInstrFromFlag + "\n\n" + PU_DEL_SI_Prompt_Txt + "\n\n" + avoid_lunch_txt : PU_DEL_SI_Prompt_Txt + "\n\n" + avoid_lunch_txt;
                    avoid_lunch_txt = !objGlobalMethods.IsEmptyString(SpecialInstrFromFlag) ? SpecialInstrFromFlag + "\n\n" + avoid_lunch_txt : avoid_lunch_txt;
                    if (!objGlobalMethods.IsEmptyString(SpecialInstrFromFlag)) {
                        toNoteList.add(SpecialInstrFromFlag);
                    }
                    toNoteList.add(PU_DEL_SI_Prompt_Txt);
                    toNoteList.add(avoid_lunch_txt);
                } else if (prompt_avoid_lunch_txt && !objGlobalMethods.IsEmptyString(avoid_lunch_txt) && objGlobalMethods.IsEmptyString(PU_DEL_SI_Prompt_Txt)) {
                    avoid_lunch_txt = !objGlobalMethods.IsEmptyString(SpecialInstrFromFlag) ? SpecialInstrFromFlag + "\n\n" + avoid_lunch_txt : avoid_lunch_txt;
                    if (!objGlobalMethods.IsEmptyString(SpecialInstrFromFlag)) {
                        toNoteList.add(SpecialInstrFromFlag);
                    }
                    toNoteList.add(avoid_lunch_txt);
                } else if (!objGlobalMethods.IsEmptyString(SpecialInstrFromFlag)) {
                    toNoteList.add(SpecialInstrFromFlag);
                }

                if (catType == 0) {
                    lblNFCtagvalue.setText(reqNFCscan && currJobInfo.PULocation != null && currJobInfo.PULocation.Contacts[0] != null ?
                            DAO_Jobs.getNFCtagListByCAIDXorCNIDX(String.valueOf(currJobInfo.PULocation.IDX), String.valueOf(currJobInfo.PULocation.Contacts[0].IDX)) : "");
                } else if (catType == 2) {
                    lblNFCtagvalue.setText(reqNFCscan && currJobInfo.DLLocation != null && currJobInfo.DLLocation.Contacts[0] != null ?
                            DAO_Jobs.getNFCtagListByCAIDXorCNIDX(String.valueOf(currJobInfo.DLLocation.IDX), String.valueOf(currJobInfo.DLLocation.Contacts[0].IDX)) : "");
                }

                if (catType == 0 || catType == 1 || catType == 4) {
                    //pip/pu/fp
                    Boolean isCollectionJob = JobsHandler.isCollectionJob(currJobInfo);
                    boolean hasCheckList = isCollectionJob ? JobsHandler.HasCheckList(currJobInfo.CheckList, "CS") : JobsHandler.HasCheckList(currJobInfo.CheckList, "PS");
                    boolean hasArticleTagCtrl = JobsHandler.CheckListHasCtrl(currJobInfo.CheckList, "CS", "AT");
                    LLupdateTag.setVisibility((isCollectionJob && hasCheckList && hasArticleTagCtrl) ? View.GONE : View.VISIBLE);
                    LLrdyDateTime.setVisibility(View.VISIBLE);

                    if (currJobInfo.CODCollectAt == null) {
                        LLCOD.setVisibility(View.GONE);
                    } else {
                        if (currJobInfo.CODCollectAt != 0) {
                            LLCOD.setVisibility(View.GONE);
                        }
                    }
                    PUcontactAddressSIstr = ctx.getString(R.string.lblPUFM) + "\n";
                    if (!PUcompany.equals("N.A")) {
                        PUcontactAddressSIstr += PUcompany + "\n";
                    }
                    PUcontactAddressSIstr += PUaddress + "\n\n";
                    PUcontactAddressSIstr += ctx.getString(R.string.lblCONTACT) + PUcontactTitle + " " + PUcontact + "\n";
                    if (!PUdept.equals("N.A")) {
                        PUcontactAddressSIstr += ctx.getString(R.string.lblDEPT) + PUdept + "\n";
                    }
                    PUcontactAddressSIstr += ctx.getString(R.string.lblTEL) + PUtel + "\n";
                    PUcontactAddressSIstr += ctx.getString(R.string.lblOTHER) + PUOther + "\n";
                    PUcontactAddressSIstr += ctx.getString(R.string.lblMOBILE) + PUmobile + "\n\n";
                    PUcontactAddressSIstr += ctx.getString(R.string.lblONBEHALFOF) + DELcontactTitle + " " + DELcontact + " (" + DELcompany + ")" + "\n";
                    PUcontactAddressSIstr += ctx.getString(R.string.lblTEL) + DELtel + ctx.getString(R.string.lblMOBILE) + DELmobile;
                    if (!PUsi.isEmpty()) {
                        //lblSI1.setText(PUsi);
                        LLSI1.setVisibility(View.VISIBLE);
                        setSpannableString(lblSI1, PUsi);
                    } else {
                        lblSI1.setText("");
                        LLSI1.setVisibility(View.GONE);
                    }
                    DELcontactAddressSIstr = ctx.getString(R.string.lblDELTO) + "\n";
                    DELcontactAddressSIstr += DELcompany + "\n";
                    DELcontactAddressSIstr += DELaddress + "\n\n";
                    DELcontactAddressSIstr += ctx.getString(R.string.lblCONTACT) + DELcontactTitle + " " + DELcontact + "\n";
                    DELcontactAddressSIstr += ctx.getString(R.string.lblTEL) + DELtel + "\n";
                    DELcontactAddressSIstr += ctx.getString(R.string.lblOTHER) + DELOther + "\n";
                    DELcontactAddressSIstr += ctx.getString(R.string.lblMOBILE) + DELmobile;

                    if (PUcompany.equals("N.A")) {
                        LLlblDELcoy.setVisibility(View.GONE);
                    } else {
                        LLlblDELcoy.setVisibility(View.VISIBLE);
                        lblDELcoyVal.setText(PUcompany);
                    }

                    if (PUaddress.equals("N.A")) {
                        LLlblDELaddress.setVisibility(View.GONE);
                    } else {
                        LLlblDELaddress.setVisibility(View.VISIBLE);
                        lblDELaddressVal.setText(PUaddress);
                    }

                    if (PUcontact.equals("N.A")) {
                        LLlblDELcontact.setVisibility(View.GONE);
                    } else {
                        LLlblDELcontact.setVisibility(View.VISIBLE);
                        if (PUcontactTitle.equals("")) {
                            lblDELcontactVal.setText(PUcontact);
                        } else {
                            lblDELcontactVal.setText(PUcontactTitle + " " + PUcontact);
                        }
                    }

                    if (PUdept.equals("N.A")) {
                        LLlblDELdept.setVisibility(View.GONE);
                    } else {
                        LLlblDELdept.setVisibility(View.VISIBLE);
                        lblDELdeptVal.setText(PUdept);
                    }

                    if (PUtel.equals("N.A") || (catType == 1 || catType == 4)) {
                        LLlblDELtel.setVisibility(View.GONE);
                        lblDELtelVal.setVisibility(View.GONE);
                    } else {
                        LLlblDELtel.setVisibility(View.VISIBLE);
                        //lblDELtelVal.setText(PUtel);
                        ////lblDELtelVal.setText(JobsHandler2.rtnMaskedContactNumberForDisplay(PUtel));
                        lblDELtelValHidden.setText(PUtel);
                    }

                    if (PUOther.equals("N.A") || (catType == 1 || catType == 4)) {
                        LLlblDELothers.setVisibility(View.GONE);
                        lblDELothersVal.setVisibility(View.GONE);
                    } else {
                        LLlblDELothers.setVisibility(View.VISIBLE);
                        //lblDELothersVal.setText(PUOther);
                        ////lblDELothersVal.setText(JobsHandler2.rtnMaskedContactNumberForDisplay(PUOther));
                        lblDELothersValHidden.setText(PUOther);
                    }

                    if (PUmobile.equals("N.A") || (catType == 1 || catType == 4)) {
                        LLlblDELmobile.setVisibility(View.GONE);
                        lblDELmobileVal.setVisibility(View.GONE);
                    } else {
                        LLlblDELmobile.setVisibility(View.VISIBLE);
                        //lblDELmobileVal.setText(PUmobile);
                        ////lblDELmobileVal.setText(JobsHandler2.rtnMaskedContactNumberForDisplay(PUmobile));
                        lblDELmobileValHidden.setText(PUmobile);
                    }

                    if (DELcontactTitle.equals("")) {
                        lblDELonbehalfVal.setText(DELcontact + " (" + DELcompany + ")");
                    } else {
                        lblDELonbehalfVal.setText(DELcontactTitle + " " + DELcontact + " (" + DELcompany + ")");
                    }

                    if (DELcompany.equals("N.A")) {
                        LLlblPUcoy.setVisibility(View.GONE);
                    } else {
                        LLlblPUcoy.setVisibility(View.VISIBLE);
                        lblPUcoyVal.setText(DELcompany);
                    }

                    if (DELaddress.equals("N.A")) {
                        LLlblPUaddress.setVisibility(View.GONE);
                    } else {
                        LLlblPUaddress.setVisibility(View.VISIBLE);
                        lblPUaddressVal.setText(DELaddress);
                    }

                    if (DELcontact.equals("N.A")) {
                        LllblPUcontact.setVisibility(View.GONE);
                    } else {
                        LllblPUcontact.setVisibility(View.VISIBLE);
                        if (DELcontactTitle.equals("")) {
                            lblPUcontactVal.setText(DELcontact);
                        } else {
                            lblPUcontactVal.setText(DELcontactTitle + " " + DELcontact);
                        }
                    }

                    if (DELdept.equals("N.A")) {
                        LLlblPUdept.setVisibility(View.GONE);
                    } else {
                        LLlblPUdept.setVisibility(View.VISIBLE);
                        lblPUdeptVal.setText(DELdept);
                    }

                    if (DELtel.equals("N.A")) {
                        LLlblPUtel.setVisibility(View.GONE);
                        lblPUtelVal.setVisibility(View.GONE);
                    } else {
                        LLlblPUtel.setVisibility(View.GONE);
                        lblPUtelVal.setText(DELtel);
                    }

                    if (DELOther.equals("N.A")) {
                        LLlblPUothers.setVisibility(View.GONE);
                        lblPUothersVal.setVisibility(View.GONE);
                    } else {
                        LLlblPUothers.setVisibility(View.GONE);
                        lblPUothersVal.setText(DELOther);
                    }

                    if (DELmobile.equals("N.A")) {
                        LLlblPUmobile.setVisibility(View.GONE);
                        lblPUmobileVal.setVisibility(View.GONE);
                    } else {
                        LLlblPUmobile.setVisibility(View.GONE);
                        lblPUmobileVal.setText(DELmobile);
                    }
                    LLlblPUonbehalf.setVisibility(View.GONE);
                    LLPUAddress.setVisibility(View.GONE);
                    LLDELAddress.setVisibility(View.VISIBLE);
                    lblBtnPUFM.setText(ctx.getString(R.string.lblDELTO));
                    lblBtnDELTO.setText(ctx.getString(R.string.lblPUFM2));
                    lblBtnDELTO.setClickable(false);
                } else if (catType == 2 || catType == 3 || catType == 5 || catType == 6 || catType == 7) {
                    LLupdateTag.setVisibility(View.GONE);
                    LLrdyDateTime.setVisibility(View.GONE);

                    if (currJobInfo.CODCollectAt == null) {
                        LLCOD.setVisibility(View.GONE);
                    } else {
                        if (currJobInfo.CODCollectAt != 1) {
                            LLCOD.setVisibility(View.GONE);
                        }
                    }

                    DELcontactAddressSIstr = ctx.getString(R.string.lblDELTO) + "\n";
                    if (DELcompany.equals("N.A")) {

                    } else {
                        DELcontactAddressSIstr += DELcompany + "\n";
                    }

                    DELcontactAddressSIstr += DELaddress + "\n\n";
                    DELcontactAddressSIstr += ctx.getString(R.string.lblCONTACT) + DELcontactTitle + " " + DELcontact + "\n";
                    if (!DELdept.equals("N.A")) {
                        DELcontactAddressSIstr += ctx.getString(R.string.lblDEPT) + DELdept + "\n";
                    }
                    DELcontactAddressSIstr += ctx.getString(R.string.lblTEL) + DELtel + "\n";
                    DELcontactAddressSIstr += ctx.getString(R.string.lblOTHER) + DELOther + "\n";
                    DELcontactAddressSIstr += ctx.getString(R.string.lblMOBILE) + DELmobile + "\n";

                    if (!DELsi.isEmpty()) {
                        //lblSI1.setText(DELsi);
                        LLSI1.setVisibility(View.VISIBLE);
                        setSpannableString(lblSI1, DELsi);
                    } else {
                        lblSI1.setText("");
                        LLSI1.setVisibility(View.GONE);
                    }

                    PUcontactAddressSIstr = ctx.getString(R.string.lblPUFM) + "\n";
                    if (PUcompany.equals("N.A")) {

                    } else {
                        PUcontactAddressSIstr += PUcompany + "\n";
                    }
                    PUcontactAddressSIstr += PUaddress + "\n\n";
                    PUcontactAddressSIstr += ctx.getString(R.string.lblCONTACT) + PUcontactTitle + " " + PUcontact + "\n";
                    PUcontactAddressSIstr += ctx.getString(R.string.lblTEL) + PUtel + "\n";
                    PUcontactAddressSIstr += ctx.getString(R.string.lblOTHER) + PUOther + "\n";
                    PUcontactAddressSIstr += ctx.getString(R.string.lblMOBILE) + PUmobile + "\n";


                    if (PUcompany.equals("N.A")) {
                        LLlblPUcoy.setVisibility(View.GONE);
                    } else {
                        LLlblPUcoy.setVisibility(View.VISIBLE);
                        lblPUcoyVal.setText(PUcompany);
                    }

                    if (PUaddress.equals("N.A")) {
                        LLlblPUaddress.setVisibility(View.GONE);
                    } else {
                        LLlblPUaddress.setVisibility(View.VISIBLE);
                        lblPUaddressVal.setText(PUaddress);
                    }

                    if (PUcontact.equals("N.A")) {
                        LllblPUcontact.setVisibility(View.GONE);
                    } else {
                        LllblPUcontact.setVisibility(View.VISIBLE);
                        lblPUcontactVal.setText(PUcontactTitle.equals("") ? PUcontact : PUcontactTitle + " " + PUcontact);
                    }

                    if (PUdept.equals("N.A")) {
                        LLlblPUdept.setVisibility(View.GONE);
                    } else {
                        LLlblPUdept.setVisibility(View.VISIBLE);
                        lblPUdeptVal.setText(PUdept);
                    }

                    if (PUtel.equals("N.A")) {
                        LLlblPUtel.setVisibility(View.GONE);
                        lblPUtelVal.setVisibility(View.GONE);
                    } else {
                        LLlblPUtel.setVisibility(View.GONE);
                        lblPUtelVal.setText(PUtel);
                    }

                    if (PUOther.equals("N.A")) {
                        LLlblPUothers.setVisibility(View.GONE);
                        lblPUothersVal.setVisibility(View.GONE);
                    } else {
                        LLlblPUothers.setVisibility(View.GONE);
                        lblPUothersVal.setText(PUOther);
                    }

                    if (PUmobile.equals("N.A")) {
                        LLlblPUmobile.setVisibility(View.GONE);
                        lblPUmobileVal.setVisibility(View.GONE);
                    } else {
                        LLlblPUmobile.setVisibility(View.GONE);
                        lblPUmobileVal.setText(PUmobile);
                    }

                    LLOriginalHeader.setVisibility(isZoneTelAndHasAuthRecBool ? View.VISIBLE : View.GONE);
                    LLAuthorizedHeader.setVisibility(isZoneTelAndHasAuthRecBool ? View.VISIBLE : View.GONE);

                    LLlblDELRdept.setVisibility(isZoneTelAndHasAuthRecBool ? View.VISIBLE : View.GONE);
                    LLlblDELRtel.setVisibility(isZoneTelAndHasAuthRecBool ? View.VISIBLE : View.GONE);
                    LLlblDELRothers.setVisibility(isZoneTelAndHasAuthRecBool ? View.VISIBLE : View.GONE);
                    LLlblDELRmobile.setVisibility(isZoneTelAndHasAuthRecBool ? View.VISIBLE : View.GONE);

                    if (DELcontactTitle.equals("")) {
                        lblPUonbehalfVal.setText(DELcontact + " (" + DELcompany + ")");
                    } else {
                        lblPUonbehalfVal.setText(DELcontactTitle + " " + DELcontact + " (" + DELcompany + ")");
                    }

                    if (DELcompany.equals("N.A")) {
                        LLlblDELcoy.setVisibility(View.GONE);
                    } else {
                        LLlblDELcoy.setVisibility(View.VISIBLE);
                        lblDELcoyVal.setText(DELcompany);
                    }

                    if (DELaddress.equals("N.A")) {
                        LLlblDELaddress.setVisibility(View.GONE);
                    } else {
                        LLlblDELaddress.setVisibility(View.VISIBLE);
                        lblDELaddressVal.setText(DELaddress);
                    }

                    if (DELcontact.equals("N.A")) {
                        LLlblDELcontact.setVisibility(View.GONE);
                    } else {
                        LLlblDELcontact.setVisibility(View.VISIBLE);
                        if (DELcontactTitle.equals("")) {
                            lblDELcontactVal.setText(DELcontact);
                        } else {
                            lblDELcontactVal.setText(DELcontactTitle + " " + DELcontact);
                        }
                    }

                    LLlblDELRcontact.setVisibility(isZoneTelAndHasAuthRecBool && !DELRcontact.equals("N.A") && !DELRcontact.equals("") ? View.VISIBLE : View.GONE);
                    lblDELRcontactVal.setText(isZoneTelAndHasAuthRecBool && !DELRcontact.equals("N.A") && !DELRcontactTitle.equals("") ?
                            DELRcontactTitle + " " + DELRcontact : isZoneTelAndHasAuthRecBool && !DELRcontact.equals("N.A") && DELRcontactTitle.equals("") ? DELRcontact : "");

                    if (DELdept.equals("N.A")) {
                        LLlblDELdept.setVisibility(View.GONE);
                    } else {
                        LLlblDELdept.setVisibility(View.VISIBLE);
                        lblDELdeptVal.setText(DELdept);
                    }

                    LLlblDELRdept.setVisibility(isZoneTelAndHasAuthRecBool && !DELRdept.equals("N.A") && !DELRdept.equals("") ? View.VISIBLE : View.GONE);
                    lblDELRdeptVal.setText(isZoneTelAndHasAuthRecBool && !DELRdept.equals("N.A") && !DELRdept.equals("") ? DELRdept : "");

                    if (DELtel.equals("N.A") || (catType == 3 || catType == 5 || catType == 6 || catType == 7)) {
                        LLlblDELtel.setVisibility(View.GONE);
                        lblDELtelVal.setVisibility(View.GONE);
                    } else {
                        LLlblDELtel.setVisibility(View.VISIBLE);
                        //lblDELtelVal.setText(DELtel);
                        ////lblDELtelVal.setText(JobsHandler2.rtnMaskedContactNumberForDisplay(DELtel));
                        lblDELtelValHidden.setText(DELtel);
                    }

                    boolean DELRtel_hasValidSGTelNum = objGlobalMethods.validSGTelNum(DELRtel, false);

                    LLlblDELRtel.setVisibility(isZoneTelAndHasAuthRecBool && !DELRtel.equals("N.A") && !DELRtel.equals("")
                            && DELRtel_hasValidSGTelNum && (catType != 3 && catType != 5) ? View.VISIBLE : View.GONE);
                    lblDELRtelVal.setText(isZoneTelAndHasAuthRecBool && !DELRtel.equals("N.A") && !DELRtel.equals("") ? DELRtel : "");
                    lblDELRtelVal.setVisibility(isZoneTelAndHasAuthRecBool && !DELRtel.equals("N.A") && !DELRtel.equals("")
                            && DELRtel_hasValidSGTelNum && (catType != 3 && catType != 5) ? View.VISIBLE : View.GONE);

                    /*LLlblDELRtel.setVisibility(isZoneTelAndHasAuthRecBool && !DELRtel.equals("N.A") && !DELRtel.equals("")
                            && objGlobalMethods.validSGTelNum(DELRtel, false) && (catType != 3 && catType != 5) ? View.VISIBLE : View.GONE);
                    lblDELRtelVal.setText(isZoneTelAndHasAuthRecBool && !DELRtel.equals("N.A") && !DELRtel.equals("") ? DELRtel : "");
                    lblDELRtelVal.setVisibility(isZoneTelAndHasAuthRecBool && !DELRtel.equals("N.A") && !DELRtel.equals("")
                            && objGlobalMethods.validSGTelNum(DELRtel, false) && (catType != 3 && catType != 5) ? View.VISIBLE : View.GONE);*/

                    if (DELOther.equals("N.A") || (catType == 3 || catType == 5 || catType == 6 || catType == 7)) {
                        LLlblDELothers.setVisibility(View.GONE);
                        lblDELothersVal.setVisibility(View.GONE);
                    } else {
                        LLlblDELothers.setVisibility(View.VISIBLE);
                        //lblDELothersVal.setText(DELOther);
                        ////lblDELothersVal.setText(JobsHandler2.rtnMaskedContactNumberForDisplay(DELOther));
                        lblDELothersValHidden.setText(DELOther);
                    }

                    boolean DELROther_hasValidSGTelNum = objGlobalMethods.validSGTelNum(DELROther, false);

                    LLlblDELRothers.setVisibility(isZoneTelAndHasAuthRecBool && !DELROther.equals("N.A") && !DELROther.equals("")
                            && DELROther_hasValidSGTelNum && (catType != 3 && catType != 5) ? View.VISIBLE : View.GONE);
                    lblDELRothersVal.setText(isZoneTelAndHasAuthRecBool && !DELROther.equals("N.A") && !DELROther.equals("") ? DELROther : "");
                    lblDELRothersVal.setVisibility(isZoneTelAndHasAuthRecBool && !DELROther.equals("N.A") && !DELROther.equals("")
                            && DELROther_hasValidSGTelNum && (catType != 3 && catType != 5) ? View.VISIBLE : View.GONE);

                    /*LLlblDELRothers.setVisibility(isZoneTelAndHasAuthRecBool && !DELROther.equals("N.A") && !DELROther.equals("")
                            && objGlobalMethods.validSGTelNum(DELROther, false) && (catType != 3 && catType != 5) ? View.VISIBLE : View.GONE);
                    lblDELRothersVal.setText(isZoneTelAndHasAuthRecBool && !DELROther.equals("N.A") && !DELROther.equals("") ? DELROther : "");
                    lblDELRothersVal.setVisibility(isZoneTelAndHasAuthRecBool && !DELROther.equals("N.A") && !DELROther.equals("")
                            && objGlobalMethods.validSGTelNum(DELROther, false) && (catType != 3 && catType != 5) ? View.VISIBLE : View.GONE);*/

                    if (DELmobile.equals("N.A") || (catType == 3 || catType == 5 || catType == 6 || catType == 7)) {
                        LLlblDELmobile.setVisibility(View.GONE);
                        lblDELmobileVal.setVisibility(View.GONE);
                    } else {
                        LLlblDELmobile.setVisibility(View.VISIBLE);
                        //lblDELmobileVal.setText(DELmobile);
                        ////lblDELmobileVal.setText(JobsHandler2.rtnMaskedContactNumberForDisplay(DELmobile));
                        lblDELmobileValHidden.setText(DELmobile);
                    }

                    boolean DELRmobile_hasValidSGTelNum = objGlobalMethods.validSGTelNum(DELRmobile, false);

                    LLlblDELRmobile.setVisibility(isZoneTelAndHasAuthRecBool && !DELRmobile.equals("N.A") && !DELRmobile.equals("")
                            && DELRmobile_hasValidSGTelNum && (catType != 3 && catType != 5) ? View.VISIBLE : View.GONE);
                    lblDELRmobileVal.setText(isZoneTelAndHasAuthRecBool && !DELRmobile.equals("N.A") && !DELRmobile.equals("") ? DELRmobile : "");
                    lblDELRmobileVal.setVisibility(isZoneTelAndHasAuthRecBool && !DELRmobile.equals("N.A") && !DELRmobile.equals("")
                            && DELRmobile_hasValidSGTelNum && (catType != 3 && catType != 5) ? View.VISIBLE : View.GONE);

                    /*LLlblDELRmobile.setVisibility(isZoneTelAndHasAuthRecBool && !DELRmobile.equals("N.A") && !DELRmobile.equals("")
                            && objGlobalMethods.validSGTelNum(DELRmobile, false) && (catType != 3 && catType != 5) ? View.VISIBLE : View.GONE);
                    lblDELRmobileVal.setText(isZoneTelAndHasAuthRecBool && !DELRmobile.equals("N.A") && !DELRmobile.equals("") ? DELRmobile : "");
                    lblDELRmobileVal.setVisibility(isZoneTelAndHasAuthRecBool && !DELRmobile.equals("N.A") && !DELRmobile.equals("")
                            && objGlobalMethods.validSGTelNum(DELRmobile, false) && (catType != 3 && catType != 5) ? View.VISIBLE : View.GONE);*/

                    LLlblDELonbehalf.setVisibility(View.GONE);
                    LLPUAddress.setVisibility(View.GONE);
                    LLDELAddress.setVisibility(View.VISIBLE);
                    lblBtnPUFM.setText(ctx.getString(R.string.lblPUFM));
                    lblBtnDELTO.setText(ctx.getString(R.string.lblDELTO2));
                    lblBtnDELTO.setClickable(false);
                }

                if (!currJobInfo.SpecialInstructions.isEmpty()) {
                    lblSI2.setText(ctx.getString(R.string.lblSPECIALINSTR) + "\n\n" + currJobInfo.SpecialInstructions + "\n");
                    LLSI2.setVisibility(View.VISIBLE);
                } else {
                    lblSI2.setText("");
                    LLSI2.setVisibility(View.GONE);
                }

                if (catType == 0 || catType == 2) {
                    objGlobalMethods.showIarrived(LLiArriveBtn, LLScrollView, true);
                    if (currJobInfo.i_have_arrived_timestamp > 0) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM h:mma");
                        String arrivaltime = sdf.format(new Date(currJobInfo.i_have_arrived_timestamp));
                        lbliarrive.setText(ctx.getString(R.string.buttontext_arrivaltime) + " " + arrivaltime);
                        handleIaRRBtn(currJobInfo);
                        enableIarrBtn = enableJobUpdates;
                        enableJobUpdates = enableJobUpdates;
                    } else {
                        LLiArriveLbl.setVisibility(View.GONE);
                        enableIarrBtn = enableJobUpdates;
                        enableJobUpdates = false;
                    }
                } else {
                    objGlobalMethods.showIarrived(LLiArriveBtn, LLScrollView, false);
                    LLiArriveLbl.setVisibility(View.GONE);
                    enableJobUpdates = true;
                    enableIarrBtn = true;
                }

                hideshowPromisedLLs(currJobInfo);

                /*if ((currJobInfo.JobsIDX == 8223526 || currJobInfo.JobsIDX == 8216438) && (currJobInfo.CheckList == null || currJobInfo.CheckList != null && currJobInfo.CheckList.length == 0)) {
                    currJobInfo.CheckList = new String[]{"0.PS.PH.MD|TAKE PHOTO OF SHIPMENT(S) COLLECTED.[ZH]将所有该取的货物, 拍摄几张清晰的照片."};
                }*/
                String hfJobInfoJsonStr = "";
                hfJobInfoJsonStr = gson.toJson(currJobInfo);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", hfJobInfoJsonStr.equals("[]") || hfJobInfoJsonStr.equals("") ? "" : hfJobInfoJsonStr);
                String ReloadDueNFCscan = objGlobalMethods.getGCMPrefs(GcmPreferences, "ReloadDueNFCscan");

                setContectNoOnClickForAllContactsLbl(enableIarrBtn);
                JobInfoActivity.maskContactNum = !JobsHandler2.canShowFullContactNumber(GcmPreferences);
                if (!(new ConnectionDetector(null).isConnectingToInternet()))
                    JobInfoActivity.maskContactNum = false;
                JobInfoActivity.maskContactNum = false; // forcefully added here until further notice (by boss)
                if (catType == 0 || catType == 1 || catType == 4) {
                    if (!(PUtel.equals("N.A") || (catType == 1 || catType == 4)))
                        lblDELtelVal.setText(JobsHandler2.rtnMaskedContactNumberForDisplay(PUtel));

                    if (!(PUOther.equals("N.A") || (catType == 1 || catType == 4)))
                        lblDELothersVal.setText(JobsHandler2.rtnMaskedContactNumberForDisplay(PUOther));

                    if (!(PUmobile.equals("N.A") || (catType == 1 || catType == 4)))
                        lblDELmobileVal.setText(JobsHandler2.rtnMaskedContactNumberForDisplay(PUmobile));
                } else {
                    if (!(DELtel.equals("N.A") || (catType == 3 || catType == 5 || catType == 6 || catType == 7)))
                        lblDELtelVal.setText(JobsHandler2.rtnMaskedContactNumberForDisplay(DELtel));

                    if (!(DELOther.equals("N.A") || (catType == 3 || catType == 5 || catType == 6 || catType == 7)))
                        lblDELothersVal.setText(JobsHandler2.rtnMaskedContactNumberForDisplay(DELOther));

                    if (!(DELmobile.equals("N.A") || (catType == 3 || catType == 5 || catType == 6 || catType == 7)))
                        lblDELmobileVal.setText(JobsHandler2.rtnMaskedContactNumberForDisplay(DELmobile));
                }

                String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");

                /*if (accessFromDirection == 1 && catType == 2 && !JobsHandler.isCirclesContactlessCanSlotLBDoorJob(currJobInfo)
                        && !objGlobalMethods.IsEmptyString(AllValidTelNums) && !exemptFromCallingForLBDoorGuard &&
                        (
                                ((exemptFromCallingForLBDoorGuardmsgFlag & 1) == 1) || ((exemptFromCallingForLBDoorGuardmsgFlag & 2) == 2)
                                || ((exemptFromCallingForLBDoorGuardmsgFlag & 4) == 4) || ((exemptFromCallingForLBDoorGuardmsgFlag & 2048) == 2048)
                        )
                )*/
                if (accessFromDirection == 1 && catType == 2
                        && !objGlobalMethods.IsEmptyString(AllValidTelNums) && !exemptFromCallingForLBDoorGuard &&
                        (
                                ((exemptFromCallingForLBDoorGuardmsgFlag & 1) == 1) || ((exemptFromCallingForLBDoorGuardmsgFlag & 2) == 2)
                                        || ((exemptFromCallingForLBDoorGuardmsgFlag & 4) == 4) || ((exemptFromCallingForLBDoorGuardmsgFlag & 2048) == 2048)
                        )
                ) {
                    String notexemptmsg = "";
                    String typemsg = "";
                    if ((exemptFromCallingForLBDoorGuardmsgFlag & 1) == 1) {
                        typemsg += "\n- " + ctx.getString(R.string.lblSI_1_1_1_1);
                    }
                    if ((exemptFromCallingForLBDoorGuardmsgFlag & 2) == 2) {
                        typemsg += "\n- " + ctx.getString(R.string.lblSI_1_2_2_2);
                    }
                    if ((exemptFromCallingForLBDoorGuardmsgFlag & 4) == 4) {
                        typemsg += "\n- " + ctx.getString(R.string.lblSI_1_3_3_3);
                    }
                    if ((exemptFromCallingForLBDoorGuardmsgFlag & 2048) == 2048) {
                        typemsg += "\n- " + ctx.getString(R.string.lblSI_1_6_6_6);
                    }

                    if (!objGlobalMethods.IsEmptyString(typemsg)) {
                        notexemptmsg += ctx.getString(R.string.notexemptedtocall_1) + typemsg + ctx.getString(R.string.calltoverifyaddress);
                    }
                    if (!objGlobalMethods.IsEmptyString(notexemptmsg)) {
                        toNoteList.add(notexemptmsg);
                    }
                }

                /*if (accessFromDirection == 1 && catType == 2 && JobsHandler.isCirclesContactlessCanSlotLBDoorJob(currJobInfo) &&
                        !objGlobalMethods.IsEmptyString(AllValidTelNums) && !exemptFromCallingForLBDoorGuard) {
                    String fullm = JobsHandler.getCirclesContactlessLBDoorMsg(currJobInfo.FLAG, 0);
                    if (!objGlobalMethods.IsEmptyString(fullm)) {
                        toNoteList.add(fullm);
                    }
                }*/

                /*//if (catType == 2 && showRestrictionPromptInt > 0 && accessFromDirection == 1 && ReloadDueNFCscan.equals("")) {
                if (catType == 2 && showRestrictionPromptInt > 0 && accessFromDirection == 1 && ReloadDueNFCscan.equals("") && (tickSpecInst != null || crossSpecInst != null)) {
                    objGlobalMethods.showCustomCanCannotMsgPrompt("", "", tickSpecInst, crossSpecInst);
                }
                if (accessFromDirection == 1 && prompt_avoid_lunch_txt && !avoid_lunch_txt.equals("") && ReloadDueNFCscan.equals("")) {
                    objGlobalMethods.showToNoteMsgPrompt("", toNoteList);
                    //showToNoteMsgPrompt0("", toNoteList);
                } else if (accessFromDirection == 1 && toNoteList != null && toNoteList.size() > 0) {
                    objGlobalMethods.showToNoteMsgPrompt("", toNoteList);
                    //showToNoteMsgPrompt0("", toNoteList);
                }*/


                /*boolean hasMobileForOTP = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "hasMobileForOTP"));
                if (!hasMobileForOTP) {
                    if (additionalList != null && additionalList.size() > 0) {
                        for (int a = additionalList.size() -1; a >= 0; a--) {
                            if (additionalList.get(a).equals(ctx.getString(R.string.additionalInfoOTP)))
                                additionalList.remove(a);
                        }
                    }
                }*/
                addAdditionalInfoText(additionalList, scrollTxt);

                if (!objGlobalMethods.isKeanHill(GcmPreferences)) {
                    cfs = null;
                    if (tempLoggerEntryDialogisCompleted != null && !tempLoggerEntryDialogisCompleted) {
                        tle = new tempLoggerEntryDialog();
                        tle.setJobsIDX(currJobInfo.JobsIDX);
                        tle.setLaunchCFS(false);
                        tle.show(getSupportFragmentManager(), "templogger");
                    } else {
                        if (catType == 2 && showRestrictionPromptInt > 0 && accessFromDirection == 1 && ReloadDueNFCscan.equals("") && (tickSpecInst != null || crossSpecInst != null)) {
                            cfs = new customFullScreenDialog();
                            cfs.setHasColdItem(hasColdItem);
                            objTemp ot = temploggerclass.getLatestRecordedTempObjNew2(objGlobalMethods.getGcmPreferences(), currJobInfo.JobsIDX);
                            cfs.setShowTL(ot != null && objGlobalMethods.IsEmptyString(ot.TempLastRecorded));
                            cfs.setDevice_Type(cfs.isShowTL() ? ot.Device_Type : 0);
                            cfs.setTickSpecInst(tickSpecInst != null ? tickSpecInst : null);
                            cfs.setCrossSpecInst(crossSpecInst != null ? crossSpecInst : null);
                        }
                        if (
                                (accessFromDirection == 1 && prompt_avoid_lunch_txt && !avoid_lunch_txt.equals("") && ReloadDueNFCscan.equals("")) ||
                                        (accessFromDirection == 1 && toNoteList != null && toNoteList.size() > 0)
                        ) {
                            if (cfs == null) {
                                cfs = new customFullScreenDialog();
                                cfs.setHasColdItem(hasColdItem);
                            }
                            cfs.setPudlsi(toNoteList);
                        }


                        if (cfs != null && (cfs.getPudlsi() != null || cfs.getTickSpecInst() != null || cfs.getCrossSpecInst() != null)) {
                            cfs.show(getSupportFragmentManager(), "jobinfomsg");
                        }

                        if (catType == 2 && showRestrictionPromptInt > 0 && accessFromDirection == 1 && ReloadDueNFCscan.equals("") && isEarlierThanDeliveryStartTime && !isStandardRunJob) {
                            show_cfsNoNH(currJobInfo.JobsIDX, AllValidTelNums, DatesAL[0], DatesAL[3], DatesAL[4]);
                        }
                    }
                } else {
                    if (catType == 2 && showRestrictionPromptInt > 0 && accessFromDirection == 1 && ReloadDueNFCscan.equals("") && (tickSpecInst != null || crossSpecInst != null)) {
                        objGlobalMethods.showCustomCanCannotMsgPrompt("", "", tickSpecInst, crossSpecInst);
                    }
                    if (accessFromDirection == 1 && prompt_avoid_lunch_txt && !avoid_lunch_txt.equals("") && ReloadDueNFCscan.equals("")) {
                        objGlobalMethods.showToNoteMsgPrompt("", toNoteList);
                    } else if (accessFromDirection == 1 && toNoteList != null && toNoteList.size() > 0) {
                        objGlobalMethods.showToNoteMsgPrompt("", toNoteList);
                    }
                }

                //addAdditionalInfoText(additionalList, scrollTxt);

                if ((catType == 0 || catType == 2) && reqNFCscan && ReloadDueNFCscan.equals("") && accessFromDirection == 1) {
                    if ((nfc_cap.isEmpty() || nfc_cap.equals("") || nfc_cap.equals("0"))) {
                        //no nfc hardware
                        //prompt to scan NFC QR
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), catType == 0 ? ctx.getString(R.string.alert_scannfc_card_onlyPIP) : ctx.getString(R.string.alert_scannfc_card_only)).show();
                    } else if (!nfc_cap.isEmpty() && !nfc_cap.equals("") && nfc_cap != "" && !nfc_cap.equals("0") && nfc_cap != "0") {
                        //nfc enabled oe not enabled
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), catType == 0 ? ctx.getString(R.string.alert_scannfc_card_onlyPIP) : ctx.getString(R.string.alert_scannfc_card_qr)).show();
                    }
                } else if ((catType == 0 || catType == 2) && reqNFCscan && !ReloadDueNFCscan.equals("")) {
                    boolean nfcverified = nfcVerified();
                    if (!nfcverified) {
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_nfc_card_err)).show();
                    } else {
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_nfc_card_identified)).show();
                    }
                }

                if (JobsHandler2.isSCBTradeSvcCLIDX(currJobInfo.ClientIDX)) {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.scbtradesvc_msg)).show();
                }

                if (JobsHandler2.isHSBCTradeSvcCLIDX(currJobInfo.ClientIDX)) {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.uobbgteam_msg)).show();
                }

                if (JobsHandler2.isUOB_BGTeam_CNIDXs(currJobInfo)) {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.uobbgteam_msg)).show();
                }

                //ctrlSettings.enableDisableCommonButtonWithStyles(!currJobInfo.ConsignmentNote.equals("") || (!enableJobUpdates && catType == 0) || (catType == 6) || (catType == 5) || (catType == 4) ? false : true, btnScanCN);
                //ctrlSettings.enableDisableCommonButtonWithStyles(!currJobInfo.ConsignmentNote.equals("") || (!enableJobUpdates && catType == 0) || (catType == 6) || (catType == 5) || (catType == 4) ? false : true, btnUpdateCN);
                ctrlSettings.enableDisableCommonButtonWithStyles(!currJobInfo.ConsignmentNote.equals("") || (!enableJobUpdates && (catType == 0 || catType == 2)) || (catType == 7) || (catType == 6) || (catType == 5) || (catType == 4) ? false : true, btnScanCN);
                ctrlSettings.enableDisableCommonButtonWithStyles(!currJobInfo.ConsignmentNote.equals("") || (!enableJobUpdates && (catType == 0 || catType == 2)) || (catType == 7) || (catType == 6) || (catType == 5) || (catType == 4) ? false : true, btnUpdateCN);
                ctrlSettings.enableDisableCommonButtonWithStyles((catType == 7) || (catType == 6) || (catType == 5) || (catType == 4) ? false : enableJobUpdates, btnScanTag);
                ctrlSettings.enableDisableCommonButtonWithStyles((catType == 7) || (catType == 6) || (catType == 5) || (catType == 4) ? false : enableJobUpdates, btnUpdateLabelTag);
                if (catType == 1 && isSecurityBag && hasSecuritySeal) {
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnScanTag);
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnUpdateLabelTag);
                }
                ctrlSettings.enableDisableSubmitButtonWithStyles(enableJobUpdates, btnsubmit);
                ctrlSettings.enableDisableSubmitButtonWithStyles(DipFromisOneOrMoreDaysLater ? false : enableJobUpdates, btndip);
                ctrlSettings.enableDisableFailButtonWithStyles(enableJobUpdates, btnFail);
                ctrlSettings.enableDisableSubmitButtonWithStyles(enableIarrBtn, btnarr);

                //setContectNoOnClickForAllContactsLbl();
                initJobChangesMessages(currJobInfo.JobsIDX);
                showExtOpt = JobsHandler2.showExtOpt(currJobInfo);
            } else {
                showErrorAlert(ctx.getString(R.string.alert_errorretrivedata) + "\n" + "JobInfo is Null.");
            }
        } catch (Exception e) {
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata) + "\n" + e.getMessage().toString());
        }
    }

    public static void showCFS() {
        try {
            if (cfs != null) {
                FragmentActivity activity = (FragmentActivity) myApp.currentActivity;
                cfs.show(activity.getSupportFragmentManager(), "jobinfomsg");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity showCFS. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void hideshowExtMenu(boolean visible) {
        try {
            if (this.menu != null) {
                this.menu.findItem(R.id.extOpt).setVisible(visible);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity hideshowExtMenu. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void hideshowPromisedLLs(objJobInfo ji) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
        try {
            if (ji != null && ji.getPromiseFrom() != null && ji.getPromiseTo() != null) {
                lblpromiseOptFromVal.setText(sdf.format(ji.getPromiseFrom()));
                LLpromiseOptFrom.setVisibility(View.VISIBLE);
                lblpromiseOptToVal.setText(sdf.format(ji.getPromiseTo()));
                LLpromiseOptTo.setVisibility(View.VISIBLE);
                if (this.menu != null) {
                    this.menu.findItem(R.id.promiseOpt).setVisible(false);
                }
            } else {
                if (LLpromiseOptFrom != null && LLpromiseOptTo != null) {
                    LLpromiseOptFrom.setVisibility(View.GONE);
                    LLpromiseOptTo.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity hideshowPromisedLLs. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void addAdditionalInfoText(ArrayList<String> atxt, String oritxt) {
        Context c = myApp.getAppContext();
        int added = 0;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);

        try {
            if (atxt != null && atxt.size() > 0 && lblAdditionalInfo != null && LLadditionalInfo != null) {

                for (int i = 0; i <= atxt.size() - 1; i++) {
                    TextView tv = new TextView(c);
                    tv.setText(atxt.get(i));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    tv.setTextColor(ContextCompat.getColor(c, R.color.White));
                    tv.setAllCaps(true);
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setLayoutParams(params);
                    tv.setPadding(0, 10, 0, 10);
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_checked_w, 0, 0, 0);
                    tv.setBackground(c.getDrawable(R.drawable.bottom_border));
                    LLadditionalInfo.addView(tv);
                    added++;
                }
                LLadditionalInfo.setVisibility(added > 0 ? View.VISIBLE : View.GONE);
            } else {
                LLadditionalInfo.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity addAdditionalInfoText. Exception: " + e.getMessage(), "", "", "", "");
            if (lblAdditionalInfo != null && LLadditionalInfo != null) {
                lblAdditionalInfo.setText(!objGlobalMethods.IsEmptyString(oritxt) ? oritxt.toUpperCase() : "");
                lblAdditionalInfo.setVisibility(View.VISIBLE);
                LLadditionalInfo.setVisibility(!objGlobalMethods.IsEmptyString(oritxt) ? View.VISIBLE : View.GONE);
            }
        }
    }

    private class ResetOTP extends AsyncTask<Void, Void, Boolean> {
        private long jobsidx = 0;

        public ResetOTP() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdverifyMe.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean asd = false;
            String ManualPinVerificaionText = "";
            try {
                if (currJobInfo == null) {
                    Gson gson = new Gson();
                    currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                }

                jobsidx = currJobInfo.JobsIDX;
                if (jobsidx > 0) {
                    currJobInfo.setOTPtries(0);
                    currJobInfo.setTypeOfPinVerification(0);

                    ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText();
                    if (!objGlobalMethods.IsEmptyString(ManualPinVerificaionText)) {
                        currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText + "\nGC enable OTP verification again.");
                    } else {
                        currJobInfo.setManualPinVerificaionText("GC enable OTP verification again.");
                    }

                    Gson gson = new Gson();
                    String currJobInfoPrefStr = gson.toJson(currJobInfo);
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    DAO_Jobs.updateJobInfoPrefStr(currJobInfo.JobsIDX, currJobInfoPrefStr);
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", currJobInfoPrefStr);
                    String[] prefName = null;
                    String[] prefvalue = null;
                    prefName = new String[]{"hfJobInfoJsonStr", "TypeOfPinVerificationStr", "hideshow10to19NewForced"};
                    prefvalue = new String[]{currJobInfoPrefStr, String.valueOf(currJobInfo.getTypeOfPinVerification()), "h"};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                }


            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInfoActivity ResetOTP doInBackground, Jobsidx: " + jobsidx + ". Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean r) {
            try {
                if (pdverifyMe != null) {
                    pdverifyMe.dismiss();
                }
                //btnPUOKPclick2(null);
                btnPUOKPclickNew(null);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInfoActivity ResetOTP onPostExecute, Jobsidx: " + jobsidx + ". Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }

    }

    private class VerifyMe extends AsyncTask<Void, Void, String> {
        private long jobsidx = 0;

        public VerifyMe() {
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdverifyMe.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String b64 = "";
            try {
                if (currJobInfo == null) {
                    Gson gson = new Gson();
                    currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                }

                jobsidx = currJobInfo.JobsIDX;

                if (jobsidx > 0) {
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3 / 4;
                    String qrInputText = "";
                    String jobsidxHash = DrawerMainActivity.getSaltedSHA1(String.valueOf(jobsidx));
                    qrInputText = String.format("www.xdel.com/XDelTrack/l.aspx?JOBSIDX=%s&HASH=%s", jobsidx, jobsidxHash);
                    QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText,
                            null,
                            Contents.Type.TEXT,
                            BarcodeFormat.QR_CODE.toString(),
                            smallerDimension);
                    try {
                        Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                        b64 = JobsHandler.convertBitmapToBase64(bitmap);
                    } catch (WriterException e) {
                        String s = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity generateVerifyMeQRstr generate qr bitmap, Jobsidx: " + jobsidx + ". Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    }

                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInfoActivity VerifyMe doInBackground, Jobsidx: " + jobsidx + ". Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return b64;
            }
        }

        @Override
        protected void onPostExecute(final String r) {
            Context ctx = myApp.getAppContext();
            try {
                if (pdverifyMe != null) {
                    pdverifyMe.dismiss();
                }
                if (!objGlobalMethods.IsEmptyString(r)) {
                    objAuthDriverResult res = objGlobalMethods.getGCdetails(GcmPreferences);
                    String GcName = res.getFullName();
                    byte[] GcImg = res.getImage();
                    String GcID = res.getID();
                    String MaskedGcID = res.getMaskedID();
                    String GcImgb64 = JobsHandler.convertByteArrToBase64String(GcImg);

                    bs = new BottomSheetVerifyMeFragment();
                    Bundle args = new Bundle();
                    args.putString("verifymeqrbase64", r);
                    args.putString("GcName", GcName);
                    args.putString("GcID", GcID);
                    args.putString("MaskedGcID", MaskedGcID);
                    args.putString("GcImgb64", GcImgb64);

                    bs.setArguments(args);
                    bs.show(getSupportFragmentManager(), bs.getTag());
                } else {
                    objGlobalMethods.writeToFile("QR website string is empty for jobsidx: " + jobsidx, "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInfoActivity VerifyMe onPostExecute, jobsidx: " + jobsidx + ". Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
            }
        }
    }

    private void initJobChangesMessages(final Long JobsIDX) {
        ArrayList<ChatMessage> cm = null;
        String msg = "";
        String rawtxt = "";
        String txt = "";
        String datetime = "";
        SimpleDateFormat datetimeformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            cm = DAO_Jobs.getJobChangesMessages(JobsIDX);
            if (cm != null && cm.size() > 0) {
                for (int i = 0; i <= cm.size() - 1; i++) {
                    datetime = datetimeformat.format(cm.get(i).messageDate);
                    rawtxt = cm.get(i).getMessageText();
                    txt = rawtxt.substring(13, rawtxt.length());
                    msg += datetime + txt + "\n\n";
                }
            }

            imgJobChange.setImageResource(!msg.isEmpty() && !msg.equals("") && msg != "" ? R.mipmap.ic_action_noti : R.mipmap.ic_action_no_noti);
            LLimgJobChange.setOnClickListener(null);
            if (!msg.isEmpty() && !msg.equals("") && msg != "") {
                //objGlobalMethods.Blink(LLimgJobChange, catType == 0 || catType == 2 ? true : false);
                final String finalmsg = msg;
                LLimgJobChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            ArrayList mal = null;
                            ArrayList ListAckMessages = new ArrayList<>();
                            ArrayList<Long> AckMessagesList = new ArrayList();
                            Long[] AckMessages = null;
                            ArrayList<Integer> NotiIDList = new ArrayList();
                            ArrayList<Long> MsgID = new ArrayList<>();

                            bottomSheetDialogFragment = new BottomSheet3DialogFragment();
                            Bundle args = new Bundle();
                            args.putString("jobchangestxt", finalmsg);
                            bottomSheetDialogFragment.setArguments(args);
                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                            mal = DAO_Jobs.getUnAckJobChangesMsgIDXByJobsIDX(JobsIDX);
                            if (mal != null && mal.size() > 0) {
                                for (int i = 0; i <= mal.size() - 1; i++) {
                                    ListAckMessages = (ArrayList<Long>) mal.get(i);
                                    AckMessagesList.add((Long) ListAckMessages.get(0));
                                    NotiIDList.add((Integer) ListAckMessages.get(1));
                                    MsgID.add((long) ListAckMessages.get(2));
                                }
                                if (AckMessagesList != null && AckMessagesList.size() > 0) {
                                    SharedPreferences updateJobInfoPref = objGlobalMethods.getUpdateJobInfoPreferences();
                                    AckMessages = AckMessagesList.toArray(new Long[AckMessagesList.size()]);
                                    JobsHandler.addAckMessagesQuePrefs(updateJobInfoPref, AckMessages);
                                    Context ctx = myApp.getAppContext();
                                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                                    if (MsgID != null && MsgID.size() > 0) {
                                        int dir = 2;
                                        for (int i = 0; i <= MsgID.size() - 1; i++) {
                                            DAO_Jobs.updateChatMessageDirection(MsgID.get(i), dir);
                                        }
                                    }

                                    if (NotiIDList != null && NotiIDList.size() > 0) {
                                        String ns = Context.NOTIFICATION_SERVICE;
                                        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
                                        try {
                                            for (int i = 0; i <= NotiIDList.size() - 1; i++) {
                                                nMgr.cancel(NotiIDList.get(i));
                                            }
                                        } catch (Exception e) {
                                            String s = e.getMessage();
                                            objGlobalMethods.writeToFile("Exception at JobInfoActivity initJobChangesMessages Canceling notification. Exception: " + s, "", "", "", "");
                                        }
                                    }
                                }
                            }
                            DAO_Jobs.updateChatMsgSendersByJobsIDX(JobsIDX);
                        } catch (Exception e) {
                            String s = e.getMessage();
                            objGlobalMethods.writeToFile("Exception at JobInfoActivity initJobChangesMessages LLimgJobChange.setOnClickListener when msg is not empty. Exception: " + s, "", "", "", "");
                        }
                    }
                });
            } else {
                //objGlobalMethods.Blink(LLimgJobChange, false);
                LLimgJobChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Context ctx = myApp.getAppContext();
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.btn_ok), ctx.getString(R.string.sheet_nojobchanges)).show();
                        } catch (Exception e) {
                            String s = e.getMessage();
                            objGlobalMethods.writeToFile("Exception at JobInfoActivity initJobChangesMessages LLimgJobChange.setOnClickListener when msg is empty. Exception: " + s, "", "", "", "");
                        }
                    }
                });
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at JobInfoActivity initJobChangesMessages. Exception: " + s, "", "", "", "");
        }
    }

    private boolean nfcVerified() {
        boolean asd = false;
        String full = "";
        ArrayList<String> al = null;
        try {
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            String NFCtagScannedVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "NFCtagScannedVal");
            NFCtagScannedVal = NFCtagScannedVal.toLowerCase();
            full = lblNFCtagvalue.getText().toString();
            if (!full.equals("") && !full.equals("[]")) {
                Gson gson = new Gson();
                al = gson.fromJson(full, new TypeToken<List<String>>() {
                }.getType());
                if (al != null && al.size() > 0) {
                    for (int i = 0; i <= al.size() - 1; i++) {
                        if (al.get(i).toLowerCase().equals(NFCtagScannedVal)) {
                            asd = true;
                            break;
                        }
                    }
                }
            }
            //asd = lblNFCtagvalue.getText().toString().toLowerCase().equals(NFCtagScannedVal.toLowerCase());
            objGlobalMethods.writeToFile("Value to verify at nfcVerified: " + NFCtagScannedVal, "", "", "", "");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity nfcVerified. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    public Dialog showDialog(String msg) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setTitle("");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    public Dialog showDialogToRepromptShipmentVerification(final String msg, final boolean showScanPinkLabelMsg) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setTitle("");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                //VerifyShipmentPrompt(objGlobalMethods.IsEqualString(msg, myApp.getAppContext().getString(R.string.scannext_cold)) ? true : false);
                VerifyShipmentPrompt(showScanPinkLabelMsg);
            }
        });

        return builder.show();
    }

    public Dialog showDialogToRepromptShipmentVerificationNew(final String msg, final int pcs, final int scannedpcs, final boolean isAlps, final String instr) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setTitle(scannedpcs + " / " + pcs);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                VerifyShipmentPromptNew(pcs, isAlps, instr);
            }
        });

        return builder.show();
    }

    private void showChangeAirplaneModeSettingsDialog() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_airplanemode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (android.os.Build.VERSION.SDK_INT < 17) {
                    try {
                        Intent intentAirplaneMode = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                        intentAirplaneMode.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentAirplaneMode);
                    } catch (ActivityNotFoundException e) {
                        objGlobalMethods.writeToFile("ActivityNotFoundException at JobInfoActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                    }
                } else {
                    Intent intent1 = new Intent("android.settings.WIRELESS_SETTINGS");
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPSModeSettingsDialog() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobInfoActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPStoHighAccModeSettingsDialog() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmodehighacc));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobInfoActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangePermissionDialog() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpspermission));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobInfoActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showErrorAlert(String errMsg) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, "");
                Intent i;
                //i = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                JobInfoActivity.accessFromDirection = 0;
                setFailStatusActivity.accessFromDirection = 0;
                setDeliveredStatusActivity.accessFromDirection = 0;
                MainActivity.uponLogin = 0;
                //Dashboard2Activity.DashboardOpenFromPush = false;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showReqCallLogImgAlert(final int cattype, final int submissionType) {
        Context ctx = myApp.getAppContext();
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setTitle("");
            builder.setCancelable(false);
            builder.setMessage(cattype == 0 ? ctx.getString(R.string.takecalllogphotopip) : ctx.getString(R.string.takecalllogphotodip));

            builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    dialog.dismiss();
                    SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(gcmp, "okpShowReqCallLogImgButton", "t");
                    submissionAction2(submissionType);
                }
            });

            AlertDialog alertdialog = builder.create();
            alertdialog.show();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity showReqCallLogImgAlert. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void shideshowSubmitBtns(int acatType) {
        try {
            Context ctx = myApp.getAppContext();

            btnFail.setVisibility(View.VISIBLE);
            btnsubmit.setVisibility(View.VISIBLE);
            btndip.setVisibility(View.VISIBLE);
            btnRtnCallActivity.setVisibility(View.VISIBLE);

            if (ongoingcall == null || !ongoingcall) {
                //objGlobalMethods.writeToFile("shideshowSubmitBtns called at JobInfoActivity. CatType " + String.valueOf(acatType), "", "", "", "");
                if (app != null && account != null) {
                    app.deinit();
                }

                if (acatType == 0) {
                    //pip
                    //boolean hasCheckList = JobsHandler.HasCheckList(currJobInfo.CheckList, "PS");
                    blink(false);
                    btnFail.setVisibility(View.VISIBLE);
                    btnsubmit.setVisibility(View.VISIBLE);
                    btndip.setVisibility(View.GONE);
                    btnRtnCallActivity.setVisibility(View.GONE);

                    btnFail.setText(ctx.getString(R.string.action_massFail));

                    Boolean isCollectionJob = JobsHandler.isCollectionJob(currJobInfo);
                    boolean hasCheckList = isCollectionJob ? JobsHandler.HasCheckList(currJobInfo.CheckList, "CS") : JobsHandler.HasCheckList(currJobInfo.CheckList, "PS");
                    btnsubmit.setText(hasCheckList ? ctx.getString(R.string.buttontext_checklist) : ctx.getString(R.string.action_massPU));
                } else if (acatType == 1) {
                    //pu
                    btnFail.setVisibility(View.GONE);
                    btnsubmit.setVisibility(View.GONE);
                    btndip.setVisibility(View.VISIBLE);
                    btnRtnCallActivity.setVisibility(View.GONE);

                    btndip.setText(ctx.getString(R.string.action_massDIP));
                } else if (acatType == 2) {
                    //dip
                    blink(false);
                    btnFail.setVisibility(View.VISIBLE);
                    btnsubmit.setVisibility(View.VISIBLE);
                    btndip.setVisibility(View.GONE);
                    btnRtnCallActivity.setVisibility(View.GONE);

                    btnFail.setText(ctx.getString(R.string.action_massFailDIP));
                    boolean hasCheckList = JobsHandler.HasCheckList(currJobInfo.CheckList, "DS");
                    btnsubmit.setText(hasCheckList ? ctx.getString(R.string.buttontext_checklist) : ctx.getString(R.string.action_massOKP));
                } else if (acatType == 3) {
                    //del
                    btnFail.setVisibility(View.GONE);
                    btnsubmit.setVisibility(View.GONE);
                    btndip.setVisibility(View.GONE);
                    btnRtnCallActivity.setVisibility(View.GONE);
                } else if (acatType == 4) {
                    //fp
                    //boolean hasCheckList = JobsHandler.HasCheckList(currJobInfo.CheckList, "PF");
                    btnFail.setVisibility(View.GONE);
                    btnsubmit.setVisibility(View.GONE);
                    btndip.setVisibility(View.GONE);
                    btnRtnCallActivity.setVisibility(View.GONE);
                } else if (acatType == 5) {
                    //fd
                    //boolean hasCheckList = JobsHandler.HasCheckList(currJobInfo.CheckList, "DF");
                    btnFail.setVisibility(View.GONE);
                    btnsubmit.setVisibility(View.GONE);
                    btndip.setVisibility(View.GONE);
                    btnRtnCallActivity.setVisibility(View.GONE);
                } else if (acatType == 6 || acatType == 7) {
                    //pd
                    //boolean hasCheckList = JobsHandler.HasCheckList(currJobInfo.CheckList, "DF");
                    btnFail.setVisibility(View.GONE);
                    btnsubmit.setVisibility(View.GONE);
                    btndip.setVisibility(View.GONE);
                    btnRtnCallActivity.setVisibility(View.GONE);
                }
            } else {
                btnFail.setVisibility(View.GONE);
                btnsubmit.setVisibility(View.GONE);
                btndip.setVisibility(View.GONE);
                btnRtnCallActivity.setVisibility(View.VISIBLE);
                blink(true);
            }

            btnarr.setText(ctx.getString(R.string.buttontext_arr));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity shideshowSubmitBtns. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void blink(boolean blink) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        try {
            if (blink) {
                btnRtnCallActivity.startAnimation(anim);
            } else {
                btnRtnCallActivity.clearAnimation(); // cancel blink animation
                btnRtnCallActivity.setAlpha(1.0f); // restore original alpha
                CallActivity.resetSco(false);
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(gcmp, "bt_connected", "");
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at JobInfoActivity blink. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void submissionAction2(int submissionType) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            if (submissionType == 1) {
                //pu
                updatePU();
            } else if (submissionType == 2) {
                //dip
                UpdateJobStatus(104, "", myApp.getAppContext().getString(R.string.alert_cfmupdatestatusDIP), false);
            } else if (submissionType == 0) {
                //FP
                Boolean hasChecklist = JobsHandler.HasCheckList(currJobInfo.CheckList, "PF");
                objGlobalMethods.writeToFile("FAIL PICK UP button clicked for JobsIDX: " + String.valueOf(currJobInfo.JobsIDX), "", "", "", "");
                final boolean isLockerJob = JobsHandler.isLockerJob(currJobInfo);
                if (hasChecklist) {
                    Intent pi;
                    pi = new Intent(myApp.currentActivity, CheckListActivity.class);
                    CheckListActivity.ActionCode = "PF";
                    CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, setFailStatusActivity.class);/*
                    String[] prefName = new String[]{"updatemodeForFailure", "ReloadDueNFCscan", "checklistvalue"};
                    String[] prefvalue = new String[]{"-1", "", ""};*/
                    String[] prefName = new String[]{"updatemodeForFailure", "ReloadDueNFCscan"};
                    String[] prefvalue = new String[]{"-1", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    objGlobalMethods.setChecklistRelatedSP_KeyValue("checklistvalue", "");

                    JobInfoActivity.accessFromDirection = 0;
                    setFailStatusActivity.accessFromDirection = 1;
                    MainActivity.uponLogin = 0;
                    startActivity(pi);
                    finish(); //newly inserted
                } else {
                    Intent parentIntent;
                    /*String[] prefName = new String[]{"updatemodeForFailure", "ReloadDueNFCscan", "checklistvalue"};
                    String[] prefvalue = new String[]{"-1", "", ""};*/
                    String[] prefName = new String[]{"updatemodeForFailure", "ReloadDueNFCscan"};
                    String[] prefvalue = new String[]{"-1", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    objGlobalMethods.setChecklistRelatedSP_KeyValue("checklistvalue", "");

                    parentIntent = new Intent(myApp.currentActivity, setFailStatusActivity.class);
                    JobInfoActivity.accessFromDirection = 0;
                    setFailStatusActivity.accessFromDirection = 1;
                    MainActivity.uponLogin = 0;
                    startActivity(parentIntent);
                    finish(); //newly inserted
                }
            } else if (submissionType == 3) {
                //fd
                objGlobalMethods.writeToFile("FAILED DELIVERY button clicked for JobsIDX: " + String.valueOf(currJobInfo.JobsIDX), "", "", "", "");
                final boolean isLockerJob = JobsHandler.isLockerJob(currJobInfo);
                if (showRestrictionPromptInt > 0 && showRestrictionPromptInt != 2) {
                    //1st alert dialog
                    String alertmsg = objGlobalMethods.getCurrInfoFlagMsg2(currJobInfo.FLAG, showRestrictionPromptInt, true, "");
                    if (!objGlobalMethods.IsEmptyString(alertmsg)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                        builder.setCancelable(false);
                        TextView at = new TextView(myApp.getAppContext());
                        at.setText(myApp.getAppContext().getString(R.string.reminder));
                        at.setTextSize(16);
                        at.setTypeface(null, Typeface.BOLD);
                        at.setTextColor(ContextCompat.getColor(myApp.getAppContext(), R.color.colorPrimary));
                        at.setPadding(75, 20, 0, 20);
                        at.setBackgroundColor(Color.WHITE);
                        builder.setCustomTitle(at);
                        builder.setMessage(alertmsg);

                        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO3), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });

                        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes3), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //check for any checklist for FD
                                Boolean hasChecklist = JobsHandler.HasCheckList(currJobInfo.CheckList, "DF");
                                if (hasChecklist) {
                                    Intent pi;
                                    pi = new Intent(myApp.currentActivity, CheckListActivity.class);
                                    CheckListActivity.ActionCode = "DF";
                                    CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, setFailStatusActivity.class);
                                    String[] prefName = new String[]{"updatemodeForFailure", "ReloadDueNFCscan"};
                                    String[] prefvalue = new String[]{"-1", ""};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                    /*objGlobalMethods.setGCMPrefs(GcmPreferences, "updatemodeForFailure", "-1");
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "ReloadDueNFCscan", "");*/

                                    JobInfoActivity.accessFromDirection = 0;
                                    setFailStatusActivity.accessFromDirection = 1;
                                    MainActivity.uponLogin = 0;
                                    startActivity(pi);
                                    finish(); //newly inserted
                                } else {
                                    Intent pi;
                                    pi = new Intent(myApp.currentActivity, setFailStatusActivity.class);
                                    String[] prefName = new String[]{"updatemodeForFailure", "ReloadDueNFCscan"};
                                    String[] prefvalue = new String[]{"-1", ""};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                    /*objGlobalMethods.setGCMPrefs(GcmPreferences, "updatemodeForFailure", "-1");
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "ReloadDueNFCscan", "");*/

                                    JobInfoActivity.accessFromDirection = 0;
                                    setFailStatusActivity.accessFromDirection = 1;
                                    MainActivity.uponLogin = 0;
                                    startActivity(pi);
                                    finish(); //newly inserted
                                }
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        //1st alert dialog
                    } else {
                        //check for any checklist for FD
                        Boolean hasChecklist = JobsHandler.HasCheckList(currJobInfo.CheckList, "DF");
                        if (hasChecklist) {
                            Intent pi;
                            pi = new Intent(myApp.currentActivity, CheckListActivity.class);
                            CheckListActivity.ActionCode = "DF";
                            CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, setFailStatusActivity.class);
                            String[] prefName = new String[]{"updatemodeForFailure", "ReloadDueNFCscan"};
                            String[] prefvalue = new String[]{"-1", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                            /*objGlobalMethods.setGCMPrefs(GcmPreferences, "updatemodeForFailure", "-1");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "ReloadDueNFCscan", "");*/

                            JobInfoActivity.accessFromDirection = 0;
                            setFailStatusActivity.accessFromDirection = 1;
                            MainActivity.uponLogin = 0;
                            startActivity(pi);
                            finish(); //newly inserted
                        } else {
                            Intent pi;
                            pi = new Intent(myApp.currentActivity, setFailStatusActivity.class);
                            String[] prefName = new String[]{"updatemodeForFailure", "ReloadDueNFCscan"};
                            String[] prefvalue = new String[]{"-1", ""};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                            /*objGlobalMethods.setGCMPrefs(GcmPreferences, "updatemodeForFailure", "-1");
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "ReloadDueNFCscan", "");*/

                            JobInfoActivity.accessFromDirection = 0;
                            setFailStatusActivity.accessFromDirection = 1;
                            MainActivity.uponLogin = 0;
                            startActivity(pi);
                            finish(); //newly inserted
                        }
                    }
                } else {
                    Boolean hasChecklist = JobsHandler.HasCheckList(currJobInfo.CheckList, "DF");
                    if (hasChecklist) {
                        Intent pi;
                        pi = new Intent(myApp.currentActivity, CheckListActivity.class);
                        CheckListActivity.ActionCode = "DF";
                        CheckListActivity.nextActivtyIntent = new Intent(myApp.currentActivity, setFailStatusActivity.class);
                        String[] prefName = new String[]{"updatemodeForFailure", "ReloadDueNFCscan"};
                        String[] prefvalue = new String[]{"-1", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                        /*objGlobalMethods.setGCMPrefs(GcmPreferences, "updatemodeForFailure", "-1");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ReloadDueNFCscan", "");*/

                        JobInfoActivity.accessFromDirection = 0;
                        setFailStatusActivity.accessFromDirection = 1;
                        MainActivity.uponLogin = 0;
                        startActivity(pi);
                        finish(); //newly inserted
                    } else {
                        Intent parentIntent;
                        parentIntent = new Intent(myApp.currentActivity, setFailStatusActivity.class);
                        String[] prefName = new String[]{"updatemodeForFailure", "ReloadDueNFCscan"};
                        String[] prefvalue = new String[]{"-1", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                        /*objGlobalMethods.setGCMPrefs(GcmPreferences, "updatemodeForFailure", "-1");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ReloadDueNFCscan", "");*/

                        JobInfoActivity.accessFromDirection = 0;
                        setFailStatusActivity.accessFromDirection = 1;
                        MainActivity.uponLogin = 0;
                        startActivity(parentIntent);
                        finish(); //newly inserted
                    }
                }
            } else if (submissionType == 4) {
                //okp
                //String IDVerificationVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "IDVerification");
                String IsCLCN_For_DoorOpenPenaltyStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "IsCLCN_For_DoorOpenPenalty");
                boolean IsCLCN_For_DoorOpenPenalty = !objGlobalMethods.IsEmptyString(IsCLCN_For_DoorOpenPenaltyStr);

                if (IsCLCN_For_DoorOpenPenalty) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                    builder.setCancelable(false);
                    TextView at = new TextView(myApp.getAppContext());
                    at.setText(myApp.getAppContext().getString(R.string.reminder));
                    at.setTextSize(16);
                    at.setTypeface(null, Typeface.BOLD);
                    at.setTextColor(ContextCompat.getColor(myApp.getAppContext(), R.color.colorPrimary));
                    at.setPadding(75, 20, 0, 20);
                    at.setBackgroundColor(Color.WHITE);
                    builder.setCustomTitle(at);
                    builder.setMessage(myApp.getAppContext().getString(R.string.ensure_door_open));

                    builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_opened), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            promptFor_updateOKP4(GcmPreferences);
                        }
                    });

                    builder.setNeutralButton(myApp.getAppContext().getString(R.string.buttontext_notopened), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    promptFor_updateOKP4(GcmPreferences);
                }


                /*if (isReqCallOutFirst && !objGlobalMethods.IsEmptyString(AllValidTelNums) && !objGlobalMethods.IsEmptyString(ContactlessCallPromptMsg) && ContactlessCanProceed.equals("0")) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), ContactlessCallPromptMsg).show();
                } else if (attemptTimeIsEarlierOrLaterThanWindow && !objGlobalMethods.IsEmptyString(AllValidTelNums) && noCallMade.equals("t")) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.attemptearlycalldip)).show();
                } else */

                //////above commented due not in used, below commented due shifted the code to promptFor_updateOKP4


                /*if (!objGlobalMethods.IsEmptyString(IDVerificationVal)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                    builder.setCancelable(false);
                    TextView at = new TextView(myApp.getAppContext());
                    at.setText(myApp.getAppContext().getString(R.string.reminder));
                    at.setTextSize(16);
                    at.setTypeface(null, Typeface.BOLD);
                    at.setTextColor(ContextCompat.getColor(myApp.getAppContext(), R.color.colorPrimary));
                    at.setPadding(75, 20, 0, 20);
                    at.setBackgroundColor(Color.WHITE);
                    builder.setCustomTitle(at);
                    builder.setMessage(myApp.getAppContext().getString(R.string.no_cust_id_photo));

                    builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateOKP4();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                    boolean exemptFromCallingForLBDoorGuard = JobsHandler2.exemptFromCallingForLBDoorGuard(currJobInfo);
                    boolean canDoorstep = JobsHandler2.CanLeaveDoorStep(currJobInfo.FLAG2);
                    boolean canSLOTLBorDOORorDOORSTEP = objGlobalMethods.canSLOTLBorDOORorDOORSTEP(currJobInfo.FLAG, currJobInfo.FLAG2);
                    //if ((JobsHandler.isCirclesContactlessCanSlotLBDoorJob(currJobInfo) || canDoorstep) && !objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                    if (!exemptFromCallingForLBDoorGuard && canSLOTLBorDOORorDOORSTEP && !objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                        Context c = myApp.getAppContext();
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 0);
                        final AlertDialog.Builder ToNoteBuilder = new AlertDialog.Builder(myApp.currentActivity);
                        final View ToNotedv = myApp.currentActivity.getLayoutInflater().inflate(R.layout.custom_tonote_dialog, null);
                        ToNoteBuilder.setView(ToNotedv);
                        ToNoteBuilder.setCancelable(false);
                        TextView txtToNoteTitle = ToNotedv.findViewById(R.id.txtToNoteTitle);
                        txtToNoteTitle.setText(c.getString(R.string.tonote));
                        TextView btnCustomToNoteOK = (TextView) ToNotedv.findViewById(R.id.btnCustomToNoteOK);
                        TextView btnCustomToNoteNeutral = (TextView) ToNotedv.findViewById(R.id.btnCustomToNoteNeutral);
                        btnCustomToNoteNeutral.setVisibility(View.VISIBLE);
                        btnCustomToNoteOK.setText(c.getString(R.string.btntxtproceed));
                        btnCustomToNoteOK.setAllCaps(true);
                        btnCustomToNoteOK.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        btnCustomToNoteNeutral.setText(c.getString(R.string.buttontext_back));
                        btnCustomToNoteNeutral.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        LinearLayout LLcustomToNote_msg = (LinearLayout) ToNotedv.findViewById(R.id.LLcustomToNote_msg);

                        //String fullm = JobsHandler.getCirclesContactlessLBDoorMsg(canDoorstep ? 0 : currJobInfo.FLAG, canDoorstep ?currJobInfo.FLAG2 : 0);
                        String fullm = JobsHandler.getCirclesContactlessLBDoorMsg(currJobInfo.FLAG, currJobInfo.FLAG2);
                        TextView tv = new TextView(c);
                        tv.setText(fullm);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        tv.setAllCaps(true);
                        tv.setTextColor(ContextCompat.getColor(c, R.color.black));
                        tv.setGravity(Gravity.CENTER_VERTICAL);
                        tv.setPadding(75, 40, 75, 40);
                        tv.setLayoutParams(params);
                        tv.setBackground(c.getDrawable(R.drawable.bottom_border));
                        LLcustomToNote_msg.addView(tv);

                        final AlertDialog ToNotedvd = ToNoteBuilder.create();

                        btnCustomToNoteNeutral.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    ToNotedvd.cancel();
                                } catch (Exception e) {

                                }
                            }
                        });

                        btnCustomToNoteOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    ToNotedvd.cancel();
                                    updateOKP4();
                                } catch (Exception e) {

                                }
                            }
                        });

                        ToNotedvd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        ToNotedvd.show();
                    } else {
                        updateOKP4();
                    }
                }*/
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobInfoActivity submissionAction2. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorupdatep1p2));
        }
    }

    public void promptFor_updateOKP4(SharedPreferences g) {
        String IDVerificationVal = objGlobalMethods.getGCMPrefs(g, "IDVerification");
        /*if (isReqCallOutFirst && !objGlobalMethods.IsEmptyString(AllValidTelNums) && !objGlobalMethods.IsEmptyString(ContactlessCallPromptMsg) && ContactlessCanProceed.equals("0")) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), ContactlessCallPromptMsg).show();
                } else if (attemptTimeIsEarlierOrLaterThanWindow && !objGlobalMethods.IsEmptyString(AllValidTelNums) && noCallMade.equals("t")) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.attemptearlycalldip)).show();
                } else */
        if (!objGlobalMethods.IsEmptyString(IDVerificationVal)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setCancelable(false);
            TextView at = new TextView(myApp.getAppContext());
            at.setText(myApp.getAppContext().getString(R.string.reminder));
            at.setTextSize(16);
            at.setTypeface(null, Typeface.BOLD);
            at.setTextColor(ContextCompat.getColor(myApp.getAppContext(), R.color.colorPrimary));
            at.setPadding(75, 20, 0, 20);
            at.setBackgroundColor(Color.WHITE);
            builder.setCustomTitle(at);
            builder.setMessage(myApp.getAppContext().getString(R.string.no_cust_id_photo));

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateOKP4();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } else {
            String AllValidTelNums = objGlobalMethods.getGCMPrefs(g, "AllValidTelNums");
            boolean exemptFromCallingForLBDoorGuard = JobsHandler2.exemptFromCallingForLBDoorGuard(currJobInfo);
            boolean canDoorstep = JobsHandler2.CanLeaveDoorStep(currJobInfo.FLAG2);
            boolean canSLOTLBorDOORorDOORSTEP = objGlobalMethods.canSLOTLBorDOORorDOORSTEP(currJobInfo.FLAG, currJobInfo.FLAG2);
            //if ((JobsHandler.isCirclesContactlessCanSlotLBDoorJob(currJobInfo) || canDoorstep) && !objGlobalMethods.IsEmptyString(AllValidTelNums)) {
            if (!exemptFromCallingForLBDoorGuard && canSLOTLBorDOORorDOORSTEP && !objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                Context c = myApp.getAppContext();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                final AlertDialog.Builder ToNoteBuilder = new AlertDialog.Builder(myApp.currentActivity);
                final View ToNotedv = myApp.currentActivity.getLayoutInflater().inflate(R.layout.custom_tonote_dialog, null);
                ToNoteBuilder.setView(ToNotedv);
                ToNoteBuilder.setCancelable(false);
                TextView txtToNoteTitle = ToNotedv.findViewById(R.id.txtToNoteTitle);
                txtToNoteTitle.setText(c.getString(R.string.tonote));
                TextView btnCustomToNoteOK = (TextView) ToNotedv.findViewById(R.id.btnCustomToNoteOK);
                TextView btnCustomToNoteNeutral = (TextView) ToNotedv.findViewById(R.id.btnCustomToNoteNeutral);
                btnCustomToNoteNeutral.setVisibility(View.VISIBLE);
                btnCustomToNoteOK.setText(c.getString(R.string.btntxtproceed));
                btnCustomToNoteOK.setAllCaps(true);
                btnCustomToNoteOK.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                btnCustomToNoteNeutral.setText(c.getString(R.string.buttontext_back));
                btnCustomToNoteNeutral.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                LinearLayout LLcustomToNote_msg = (LinearLayout) ToNotedv.findViewById(R.id.LLcustomToNote_msg);

                //String fullm = JobsHandler.getCirclesContactlessLBDoorMsg(canDoorstep ? 0 : currJobInfo.FLAG, canDoorstep ?currJobInfo.FLAG2 : 0);
                String fullm = JobsHandler.getCirclesContactlessLBDoorMsg(currJobInfo.FLAG, currJobInfo.FLAG2);
                TextView tv = new TextView(c);
                tv.setText(fullm);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tv.setAllCaps(true);
                tv.setTextColor(ContextCompat.getColor(c, R.color.black));
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setPadding(75, 40, 75, 40);
                tv.setLayoutParams(params);
                tv.setBackground(c.getDrawable(R.drawable.bottom_border));
                LLcustomToNote_msg.addView(tv);

                final AlertDialog ToNotedvd = ToNoteBuilder.create();

                btnCustomToNoteNeutral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ToNotedvd.cancel();
                        } catch (Exception e) {

                        }
                    }
                });

                btnCustomToNoteOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ToNotedvd.cancel();
                            updateOKP4();
                        } catch (Exception e) {

                        }
                    }
                });

                ToNotedvd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ToNotedvd.show();
            } else {
                updateOKP4();
            }
        }
    }

    public void btnIARRclick(View view) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        final Context ctx = myApp.getAppContext();

        //objGlobalMethods.setGCMPrefs(GcmPreferences, "relogging_in", "1");
        try {
            Boolean SettingsDistVioEnabled = !objGlobalMethods.getSettingsDistVioEnabled().equals("") ? true : false;
            SettingsDistVioEnabled = true;
            Boolean PromptDistanceViolationUponUpdate = JobsHandler.PromptDistanceViolationUponUpdate(currJobInfo, catType, catType == 0 ? 1 : 4);
            //PromptDistanceViolationUponUpdate = false;

            if (catType == 0) {
                UpdateJobStatus(368, "", (SettingsDistVioEnabled && PromptDistanceViolationUponUpdate) ?
                                ctx.getString(R.string.alert_outofrangecfm2) : ctx.getString(R.string.alert_cfmupdatestatusARR),
                        (SettingsDistVioEnabled && PromptDistanceViolationUponUpdate));
            } else if (catType == 2) {
                UpdateJobStatus(369, "", (SettingsDistVioEnabled && PromptDistanceViolationUponUpdate) ?
                                ctx.getString(R.string.alert_outofrangecfm2) : ctx.getString(R.string.alert_cfmupdatestatusARR),
                        (SettingsDistVioEnabled && PromptDistanceViolationUponUpdate));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity btnIARRclick. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public void btnFAILclick(View view) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        Context ctx = myApp.getAppContext();
        if (currJobInfo == null) {
            Gson gson = new Gson();
            currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
        }

        if (catType == 0) {
            //fp
            submissionAction2(0);
        } else if (catType == 2) {
            //fd
            /*boolean isStandardRunJob = JobsHandler.isStandardRunJob(currJobInfo);
            boolean isXDelJob = JobsHandler.isXDelJob(currJobInfo);
            if (!isStandardRunJob && !isXDelJob) {
                boolean isReqCallOutFirst = JobsHandler.isReqCallOutFirst(currJobInfo);
                boolean attemptTimeIsEarlierOrLaterThanWindow = JobsHandler.attemptTimeIsEarlierOrLaterThanWindow(catType, currJobInfo);
                String[] ContactlessCallPromptCheck = null;
                String ContactlessCallPromptMsg = "";
                String ContactlessCanProceed = "";
                String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                if ((isReqCallOutFirst || attemptTimeIsEarlierOrLaterThanWindow) && !objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                    setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
                    ContactlessCallPromptCheck = JobsHandler.getContactlessCallPromptMsg(2);
                    ContactlessCanProceed = ContactlessCallPromptCheck[0];
                    ContactlessCallPromptMsg = ContactlessCallPromptCheck[1];
                }
                String okpShowReqCallLogImgButton = objGlobalMethods.getGCMPrefs(GcmPreferences, "okpShowReqCallLogImgButton");
                if ((isReqCallOutFirst || attemptTimeIsEarlierOrLaterThanWindow) && !objGlobalMethods.IsEmptyString(AllValidTelNums) && !objGlobalMethods.IsEmptyString(ContactlessCallPromptMsg) && ContactlessCanProceed.equals("0")) {
                    objGlobalMethods.showDialog(JobInfoActivity.this, myApp.getAppContext().getString(R.string.buttontext_ok), ContactlessCallPromptMsg).show();
                } else if ((isReqCallOutFirst || attemptTimeIsEarlierOrLaterThanWindow) && !objGlobalMethods.IsEmptyString(AllValidTelNums) && !objGlobalMethods.IsEmptyString(ContactlessCallPromptMsg) && ContactlessCanProceed.equals("1") && objGlobalMethods.IsEmptyString(okpShowReqCallLogImgButton)) {
                    showReqCallLogImgAlert(catType, 3);
                } else {
                    submissionAction2(3);
                }
            } else {
                submissionAction2(3);
            }*/

            boolean isStandardRunJob = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isStandardRunJob"));
            boolean isEarlierThanDeliveryStartTime = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isEarlierThanDeliveryStartTime"));
            String DatesALJson = objGlobalMethods.getGCMPrefs(GcmPreferences, "DatesALJson");
            Date[] DatesAL = null;
            try {
                DatesAL = objGlobalMethods.IsEmptyString(DatesALJson) ? null : new Gson().fromJson(DatesALJson, Date[].class);
                if (DatesAL != null && DatesAL.length >= 5) {
                    boolean isEarlierThanDeliveryStartTime2 = isEarlierThanDeliveryStartTime;
                    if (!isStandardRunJob) {
                        isEarlierThanDeliveryStartTime2 = JobsHandler2.attemptTimeIsEarlierThanWindow(catType, DatesAL[0], DatesAL[3], DatesAL[5], DatesAL[4]);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "isEarlierThanDeliveryStartTime", isEarlierThanDeliveryStartTime2 ? "t" : "");
                    }
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInfoActivity btnFAILclick converting DatesAL. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }

            if (tempLoggerEntryDialogisCompleted != null)
                tempLoggerEntryDialogisCompleted = null;

            submissionAction2(3);
        }
    }

    public void btnPUOKPclick2(View view) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            long jidx = 0;
            Context ctx = myApp.getAppContext();
            if (currJobInfo == null) {
                Gson gson = new Gson();
                currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            }
            Boolean reqNFCscan = JobsHandler.reqNFCscan(currJobInfo);

            boolean isLockerJob = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isLockerJob"));
            String lockerparcelid = isLockerJob && currJobInfo != null && currJobInfo.LockerStation != null && !objGlobalMethods.IsEmptyString(currJobInfo.LockerStation.state) ? currJobInfo.LockerStation.state : "";
            String L2_LA_GetUserResultJsonStr = isLockerJob ? objGlobalMethods.getGCMPrefs(GcmPreferences, "GetUserResultJsonStr") : "";
            L2_LA_GetUserResult res = null;
            if (isLockerJob && !objGlobalMethods.IsEmptyString(L2_LA_GetUserResultJsonStr)) {
                Gson gson = new Gson();
                res = gson.fromJson(L2_LA_GetUserResultJsonStr, L2_LA_GetUserResult.class);
            }
            final int getLockerAllianceJobProceedStatus = isLockerJob ? JobsHandler.getLockerAllianceJobProceedStatus(res, lockerparcelid) : 0;

            if (catType == 0) {
                //pu
                boolean canProceed = false;
                //boolean isLockerJob = JobsHandler.isLockerJob(currJobInfo);
                boolean isStandardRunJob = JobsHandler.isStandardRunJob(currJobInfo);
                boolean isXDelJob = JobsHandler.isXDelJob(currJobInfo);

                if (reqNFCscan) {
                    String NFCtagScannedVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "NFCtagScannedVal");
                    if (NFCtagScannedVal.equals("")) {
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_scannfc_card_qr)).show();
                    } else {
                        boolean nfcverified = nfcVerified();
                        if (!nfcverified) {
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_nfc_card_err)).show();
                        } else {
                            canProceed = true;
                        }
                    }
                } else {
                    canProceed = true;
                }
                if (canProceed) {
                    jidx = currJobInfo != null ? currJobInfo.JobsIDX : 0;
                    final long JobsIDX = jidx;
                    if (JobsIDX > 0) {
                        objGlobalMethods.writeToFile("PU button clicked for JobsIDX: " + String.valueOf(JobsIDX), "", "", "", "");

                        if (isLockerJob) {
                            if (getLockerAllianceJobProceedStatus > 0) {
                                //AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                builder.setCancelable(false);
                                builder.setMessage(
                                        getLockerAllianceJobProceedStatus == 2 ? ctx.getString(R.string.userkeystatusunknown) :
                                                getLockerAllianceJobProceedStatus == 3 ? ctx.getString(R.string.usernopin) :
                                                        getLockerAllianceJobProceedStatus == 4 ? ctx.getString(R.string.userkeynotactive) :
                                                                getLockerAllianceJobProceedStatus == 5 ? ctx.getString(R.string.noparcelid) :
                                                                        ctx.getString(R.string.nouserkey)
                                );
                                builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        if (getLockerAllianceJobProceedStatus == 3) {
                                            String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, objGlobalVariables.categoryTypePrefName,
                                                    objGlobalVariables.categoryTitlePrefName, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "cameraScannedCode",
                                                    "objJobInfoBringtoUpdateCODListJsonStr", "mutlipleCODjobs", "objPendingJobStatusUpdatesobjALPrefStr", "objPendingJobPhotoUpdatesobjALPrefStr",
                                                    "desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD", "isFromDashboardSub", "isFromMessaging"
                                            };
                                            String[] prefvalue = new String[]{"", "", "-1",
                                                    "", "", "", "",
                                                    "", "", "", "",
                                                    "", "", "", "", "", "", ""
                                            };
                                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                            Intent i = new Intent(myApp.currentActivity, LockerProfileActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }
                                });
                                final AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                CheckListActivity.fromPage = "JobInfoActivity";
                                Intent authIntent = new Intent(myApp.currentActivity, AuthActivity.class);

                                String[] prefName = new String[]{"ReloadDueNFCscan", "isStandardRunJob", "lockerparcelid"};
                                String[] prefvalue = new String[]{
                                        "",
                                        isStandardRunJob || isXDelJob ? "y" : "",
                                        lockerparcelid
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                JobInfoActivity.accessFromDirection = 0;
                                setFailStatusActivity.accessFromDirection = 0;
                                setDeliveredStatusActivity.accessFromDirection = 1;
                                MainActivity.uponLogin = 0;
                                startActivity(authIntent);
                                finish(); //newly inserted
                            }
                        } else if (JobsHandler.PromptScanBarcodeUponPU(currJobInfo)) {
                            Boolean isSecurityBag = JobsHandler.reqSecurityTag(currJobInfo);
                            if (isSecurityBag) {
                                objGlobalMethods.writeToFile("JobIDX " + JobsIDX + " require security seal number.", "", "", "", "");
                            } else {
                                objGlobalMethods.writeToFile("JobIDX " + JobsIDX + " require Barcode Tag, JX or consignment.", "", "", "", "");
                            }

                            //AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                            builder.setCancelable(false);
                            builder.setMessage(isSecurityBag ? ctx.getString(R.string.alert_PUpromptSecurity) : ctx.getString(R.string.alert_PUprompt));
                            builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            final AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            //AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                            builder.setCancelable(false);
                            builder.setMessage(ctx.getString(R.string.alert_cfmupdatestatusPU));
                            builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if user pressed "yes", update job status

                                    updatePU2(0, 0, "");
                                }
                            });
                            builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if user select "No", just cancel this dialog and continue with app
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    } else {
                        showDialog(ctx.getString(R.string.alert_ErrorJobsIDX));
                    }
                }
            } else if (catType == 2) {
                //okp
                //new code for btnPUOKPclick3
                jidx = currJobInfo != null ? currJobInfo.JobsIDX : 0;
                if (jidx > 0) {
                    boolean hasCheckList = JobsHandler.HasCheckList(currJobInfo.CheckList, "DS");
                    String log = hasCheckList ? "CHECKLIST button clicked for JobsIDX: " : "DELIVERED button clicked for JobsIDX: ";
                    objGlobalMethods.writeToFile(log + String.valueOf(jidx), "", "", "", "");
                }
                Boolean SettingsDistVioEnabled = !objGlobalMethods.getSettingsDistVioEnabled().equals("") ? true : false;
                SettingsDistVioEnabled = false;
                Boolean PromptDistanceViolationUponUpdate = JobsHandler.PromptDistanceViolationUponUpdate(currJobInfo, catType, catType == 0 ? 1 : 4);
                PromptDistanceViolationUponUpdate = false;

                boolean isStandardRunJob = JobsHandler.isStandardRunJob(currJobInfo);
                boolean isXDelJob = JobsHandler.isXDelJob(currJobInfo);
                boolean isContactlessDelivery = JobsHandler.isContactlessDelivery(currJobInfo);
                boolean attemptTimeOneOrMoreDaysEarlierThanWindow = JobsHandler.attemptTimeOneOrMoreDaysEarlierThanWindow(catType, currJobInfo);
                boolean isReqShipmentVerification = JobsHandler.isReqShipmentVerification(currJobInfo);
                boolean hasColdItem = JobsHandler2.hasColdItem(currJobInfo);
                boolean shipmentIsVerified = false;
                boolean hasSecuritySeal = JobsHandler.JobHasSecuritySeal(currJobInfo, true);
                boolean skiplockerBool = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "skiplocker"));
                String NFCtagScannedVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "NFCtagScannedVal");
                String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                String ShipmentVerified = objGlobalMethods.getGCMPrefs(GcmPreferences, "ShipmentVerified");
                String ShipmentVerifiedCold = objGlobalMethods.getGCMPrefs(GcmPreferences, "ShipmentVerifiedCold");
                String noCallMade = "";

                /*if (attemptTimeOneOrMoreDaysEarlierThanWindow && !objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                    JobsHandler.CheckAnyCallMadeWithin60min(AllValidTelNums);
                    noCallMade = objGlobalMethods.getGCMPrefs(GcmPreferences, "noCallMade");
                }*/
                //String okpShowReqCallLogImgButton = objGlobalMethods.getGCMPrefs(GcmPreferences, "okpShowReqCallLogImgButton");

                if (SettingsDistVioEnabled && PromptDistanceViolationUponUpdate) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_outofrangecfm)).show();
                } else {
                    if (isLockerJob) {
                        if (!skiplockerBool) {
                            if (getLockerAllianceJobProceedStatus > 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                builder.setCancelable(false);
                                builder.setMessage(
                                        getLockerAllianceJobProceedStatus == 2 ? ctx.getString(R.string.userkeystatusunknown) :
                                                getLockerAllianceJobProceedStatus == 3 ? ctx.getString(R.string.usernopin) :
                                                        getLockerAllianceJobProceedStatus == 4 ? ctx.getString(R.string.userkeynotactive) :
                                                                getLockerAllianceJobProceedStatus == 5 ? ctx.getString(R.string.noparcelid) :
                                                                        ctx.getString(R.string.nouserkey)
                                );
                                builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        if (getLockerAllianceJobProceedStatus == 3) {
                                            String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, objGlobalVariables.categoryTypePrefName,
                                                    objGlobalVariables.categoryTitlePrefName, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "cameraScannedCode",
                                                    "objJobInfoBringtoUpdateCODListJsonStr", "mutlipleCODjobs", "objPendingJobStatusUpdatesobjALPrefStr", "objPendingJobPhotoUpdatesobjALPrefStr",
                                                    "desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD", "isFromDashboardSub", "isFromMessaging"
                                            };
                                            String[] prefvalue = new String[]{"", "", "-1",
                                                    "", "", "", "",
                                                    "", "", "", "",
                                                    "", "", "", "", "", "", ""
                                            };
                                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                            Intent i = new Intent(myApp.currentActivity, LockerProfileActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }
                                });
                                final AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                if (objGlobalMethods.IsEmptyString(ShipmentVerified)) {
                                    objGlobalMethods.writeToFile("Locker Job require Verify Shipment for JobsIDX: " + String.valueOf(jidx), "", "", "", "");
                                    VerifyShipmentPrompt(false);
                                } else {
                                    CheckListActivity.fromPage = "JobInfoActivity";
                                    Intent authIntent = new Intent(myApp.currentActivity, AuthActivity.class);
                                    String[] prefName = new String[]{"ReloadDueNFCscan", "isStandardRunJob", "lockerparcelid"};
                                    String[] prefvalue = new String[]{
                                            "",
                                            isStandardRunJob || isXDelJob ? "y" : "",
                                            lockerparcelid
                                    };
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                    JobInfoActivity.accessFromDirection = 0;
                                    setFailStatusActivity.accessFromDirection = 0;
                                    setDeliveredStatusActivity.accessFromDirection = 1;
                                    MainActivity.uponLogin = 0;
                                    startActivity(authIntent);
                                    finish(); //newly inserted
                                }
                            }
                        } else {
                            Intent parentIntent = new Intent(this, CheckListActivity.class);
                            MainActivity.uponLogin = 0;
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            DrawerMainActivity.DashboardOpenFromPush = false;
                            setDeliveredStatusActivity.fromPage = "CheckListActivity";
                            CheckListActivity.ActionCode = "DS";
                            CheckListActivity.nextActivtyIntent = new Intent(this, setDeliveredStatusActivity.class);
                            CheckListActivity.fromPage = "JobInfoActivity";
                            startActivity(parentIntent);
                            this.finish();
                        }

                    }
                    /*else if ((!isStandardRunJob && !isXDelJob) && attemptTimeOneOrMoreDaysEarlierThanWindow && !objGlobalMethods.IsEmptyString(AllValidTelNums) && noCallMade.equals("t")) {
                        objGlobalMethods.writeToFile("Attempt Early Delivery for jx" + currJobInfo.JobsIDX, "", "", "", "");
                        setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
                        String totalnumofcallstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalnumofcall");
                        String reqCallLogImg = objGlobalMethods.getGCMPrefs(GcmPreferences, "reqCallLogImg");
                        int totalnumofcall = totalnumofcallstr.equals("") ? 0 : Integer.parseInt(totalnumofcallstr);
                        String msg = "";
                        int CirclesLogicType = -1; // -1 = proceed, 0 = no call, 1 = 1 call, 2 = 2 or more & nid photo, 3 = calllogimg taken
                        CirclesLogicType = totalnumofcall == 0 && reqCallLogImg.equals("t") ? 0 :
                                totalnumofcall >= 0 && noCallMade.equals("t") && reqCallLogImg.equals("t") && totalnumofcall < 2 ? 1 :
                                        totalnumofcall >= 0 && noCallMade.equals("t") && reqCallLogImg.equals("t") && totalnumofcall >= 2 ? 2 :
                                                -1;
                        if (CirclesLogicType >= 0 && CirclesLogicType < 2) {
                            msg = CirclesLogicType == 0 ? ctx.getString(R.string.attemptearlycalldip) :
                                    CirclesLogicType == 1 ? ctx.getString(R.string.numcallsmade) + " " + String.valueOf(totalnumofcall) + "\n\n" + ctx.getString(R.string.attemptearlycalldip) :
                                            ctx.getString(R.string.attemptearlycalldip);
                            showEarlyAttemptMsg(msg, false);
                        } else if (CirclesLogicType == 2) {
                            msg = ctx.getString(R.string.takecalllogphotodip0);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "okpShowReqCallLogImgButton", "t");
                            showEarlyAttemptMsg(msg, true);
                        } else {
                            submissionAction2(4);
                        }
                    }*/
                    /* else if (reqNFCscan) {
                        String NFCtagScannedVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "NFCtagScannedVal");
                        if (NFCtagScannedVal.equals("")) {
                            objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_scannfc_card_qr)).show();
                        } else {
                            boolean nfcverified = nfcVerified();
                            if (!nfcverified) {
                                objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_nfc_card_err)).show();
                            } else {
                                submissionAction2(4);
                            }
                        }
                    }*/
                    else if (reqNFCscan && NFCtagScannedVal.equals("")) {
                        objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_scannfc_card_qr)).show();
                    } else if (reqNFCscan && !NFCtagScannedVal.equals("") && !nfcVerified()) {
                        objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_nfc_card_err)).show();
                    } else if (hasSecuritySeal && !JobsHandler.SecuritySealAllVerified(GcmPreferences)) {
                        if (ssd == null) {
                            ssd = new securitySealDialog();
                        }
                        ssd.show(getSupportFragmentManager(), "securitySealDialog");
                        //} else if (isReqShipmentVerification && hasColdItem && currJobInfo.Pieces >= 2 && (objGlobalMethods.IsEmptyString(ShipmentVerified) || objGlobalMethods.IsEmptyString(ShipmentVerifiedCold)) && !hasSecuritySeal) {
                    } else if (isReqShipmentVerification && hasColdItem && currJobInfo.Pieces >= 2 && (objGlobalMethods.IsEmptyString(ShipmentVerified) || objGlobalMethods.IsEmptyString(ShipmentVerifiedCold))) {
                        objGlobalMethods.writeToFile("Require Verify COLD and NON COLD Shipment for JobsIDX: " + jidx, "", "", "", "");
                        VerifyShipmentPrompt(true);
                        //} else if (isReqShipmentVerification && hasColdItem && currJobInfo.Pieces > 0 && currJobInfo.Pieces < 2 && objGlobalMethods.IsEmptyString(ShipmentVerified) && !hasSecuritySeal) {
                    } else if (isReqShipmentVerification && hasColdItem && currJobInfo.Pieces > 0 && currJobInfo.Pieces < 2 && objGlobalMethods.IsEmptyString(ShipmentVerified)) {
                        objGlobalMethods.writeToFile("Require Verify COLD Shipment for JobsIDX: " + jidx, "", "", "", "");
                        VerifyShipmentPrompt(true);
                        //} else if (isReqShipmentVerification && objGlobalMethods.IsEmptyString(ShipmentVerified) && !hasSecuritySeal) {
                    } else if (isReqShipmentVerification && objGlobalMethods.IsEmptyString(ShipmentVerified)) {
                        //prompt to scan/enter value to verify correct item taken
                        objGlobalMethods.writeToFile("Require Verify Shipment for JobsIDX: " + String.valueOf(jidx), "", "", "", "");
                        VerifyShipmentPrompt(false);
                    } else {
                        submissionAction2(4);
                    }
                }
                //new code for btnPUOKPclick3


                //submissionAction2(4);
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity btnPUOKPclick2. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    public void btnPUOKPclickNew(View view) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            long jidx = 0;
            Context ctx = myApp.getAppContext();
            if (currJobInfo == null) {
                Gson gson = new Gson();
                currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            }
            Boolean reqNFCscan = JobsHandler.reqNFCscan(currJobInfo);
            int pcs = currJobInfo.Pieces;
            boolean isLockerJob = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isLockerJob"));
            String lockerparcelid = isLockerJob && currJobInfo != null && currJobInfo.LockerStation != null && !objGlobalMethods.IsEmptyString(currJobInfo.LockerStation.state) ? currJobInfo.LockerStation.state : "";
            String L2_LA_GetUserResultJsonStr = isLockerJob ? objGlobalMethods.getGCMPrefs(GcmPreferences, "GetUserResultJsonStr") : "";
            L2_LA_GetUserResult res = null;
            if (isLockerJob && !objGlobalMethods.IsEmptyString(L2_LA_GetUserResultJsonStr)) {
                Gson gson = new Gson();
                res = gson.fromJson(L2_LA_GetUserResultJsonStr, L2_LA_GetUserResult.class);
            }
            final int getLockerAllianceJobProceedStatus = isLockerJob ? JobsHandler.getLockerAllianceJobProceedStatus(res, lockerparcelid) : 0;

            if (catType == 0) {
                Boolean isCollectionJob = JobsHandler.isCollectionJob(currJobInfo);
                boolean hasArticleTagCtrl = false;

                //pu
                boolean hasCheckList = false;
                jidx = currJobInfo != null ? currJobInfo.JobsIDX : 0;
                /*if ((currJobInfo.JobsIDX == 8223526 || currJobInfo.JobsIDX == 8216438) && (currJobInfo.CheckList == null || currJobInfo.CheckList != null && currJobInfo.CheckList.length == 0)) {
                    currJobInfo.CheckList = new String[]{"0.PS.PH.MD|TAKE PHOTO OF SHIPMENT(S) COLLECTED.[ZH]将所有该取的货物, 拍摄几张清晰的照片."};
                }*/
                if (jidx > 0) {
                    //hasCheckList = JobsHandler.HasCheckList(currJobInfo.CheckList, "PS");
                    hasCheckList = isCollectionJob ? JobsHandler.HasCheckList(currJobInfo.CheckList, "CS") : JobsHandler.HasCheckList(currJobInfo.CheckList, "PS");
                    String log = hasCheckList ? "CHECKLIST button clicked for JobsIDX: " : "PU button clicked for JobsIDX: ";
                    objGlobalMethods.writeToFile(log + String.valueOf(jidx), "", "", "", "");
                }

                if (isCollectionJob && hasCheckList)
                    hasArticleTagCtrl = JobsHandler.CheckListHasCtrl(currJobInfo.CheckList, "CS", "AT");

                boolean canProceed = false;
                //boolean isLockerJob = JobsHandler.isLockerJob(currJobInfo);
                boolean isStandardRunJob = JobsHandler.isStandardRunJob(currJobInfo);
                boolean isXDelJob = JobsHandler.isXDelJob(currJobInfo);

                if (reqNFCscan) {
                    String NFCtagScannedVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "NFCtagScannedVal");
                    if (NFCtagScannedVal.equals("")) {
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_scannfc_card_qr)).show();
                    } else {
                        boolean nfcverified = nfcVerified();
                        if (!nfcverified) {
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_nfc_card_err)).show();
                        } else {
                            canProceed = true;
                        }
                    }
                } else {
                    canProceed = true;
                }
                if (canProceed) {
                    jidx = currJobInfo != null ? currJobInfo.JobsIDX : 0;
                    final long JobsIDX = jidx;
                    if (JobsIDX > 0) {
                        objGlobalMethods.writeToFile("PU button clicked for JobsIDX: " + String.valueOf(JobsIDX), "", "", "", "");

                        if (isLockerJob) {
                            if (getLockerAllianceJobProceedStatus > 0) {
                                //AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                builder.setCancelable(false);
                                builder.setMessage(
                                        getLockerAllianceJobProceedStatus == 2 ? ctx.getString(R.string.userkeystatusunknown) :
                                                getLockerAllianceJobProceedStatus == 3 ? ctx.getString(R.string.usernopin) :
                                                        getLockerAllianceJobProceedStatus == 4 ? ctx.getString(R.string.userkeynotactive) :
                                                                getLockerAllianceJobProceedStatus == 5 ? ctx.getString(R.string.noparcelid) :
                                                                        ctx.getString(R.string.nouserkey)
                                );
                                builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        if (getLockerAllianceJobProceedStatus == 3) {
                                            String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, objGlobalVariables.categoryTypePrefName,
                                                    objGlobalVariables.categoryTitlePrefName, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "cameraScannedCode",
                                                    "objJobInfoBringtoUpdateCODListJsonStr", "mutlipleCODjobs", "objPendingJobStatusUpdatesobjALPrefStr", "objPendingJobPhotoUpdatesobjALPrefStr",
                                                    "desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD", "isFromDashboardSub", "isFromMessaging"
                                            };
                                            String[] prefvalue = new String[]{"", "", "-1",
                                                    "", "", "", "",
                                                    "", "", "", "",
                                                    "", "", "", "", "", "", ""
                                            };
                                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                            Intent i = new Intent(myApp.currentActivity, LockerProfileActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }
                                });
                                final AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                CheckListActivity.fromPage = "JobInfoActivity";
                                Intent authIntent = new Intent(myApp.currentActivity, AuthActivity.class);

                                String[] prefName = new String[]{"ReloadDueNFCscan", "isStandardRunJob", "lockerparcelid"};
                                String[] prefvalue = new String[]{
                                        "",
                                        isStandardRunJob || isXDelJob ? "y" : "",
                                        lockerparcelid
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                JobInfoActivity.accessFromDirection = 0;
                                setFailStatusActivity.accessFromDirection = 0;
                                setDeliveredStatusActivity.accessFromDirection = 1;
                                MainActivity.uponLogin = 0;
                                startActivity(authIntent);
                                finish(); //newly inserted
                            }
                        }
                        //else if (JobsHandler.PromptScanBarcodeUponPU(currJobInfo)) {
                        else if (isCollectionJob && !hasArticleTagCtrl && JobsHandler.PromptScanBarcodeUponPU(currJobInfo)) {
                            Boolean isSecurityBag = JobsHandler.reqSecurityTag(currJobInfo);
                            if (isSecurityBag) {
                                objGlobalMethods.writeToFile("JobIDX " + JobsIDX + " require security seal number.", "", "", "", "");
                            } else {
                                objGlobalMethods.writeToFile("JobIDX " + JobsIDX + " require Barcode Tag, JX or consignment.", "", "", "", "");
                            }

                            //AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                            builder.setCancelable(false);
                            builder.setMessage(isSecurityBag ? ctx.getString(R.string.alert_PUpromptSecurity) : ctx.getString(R.string.alert_PUprompt));
                            builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            final AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            if (!hasCheckList) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                builder.setCancelable(false);
                                builder.setMessage(ctx.getString(R.string.alert_cfmupdatestatusPU));
                                builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //if user pressed "yes", update job status

                                        updatePU2(0, 0, "");
                                    }
                                });
                                builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //if user select "No", just cancel this dialog and continue with app
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                updatePU_w_Checklist(0, 0, "", isCollectionJob);
                            }
                        }
                    } else {
                        showDialog(ctx.getString(R.string.alert_ErrorJobsIDX));
                    }
                }
            } else if (catType == 2) {
                //okp
                //new code for btnPUOKPclick3
                jidx = currJobInfo != null ? currJobInfo.JobsIDX : 0;
                if (jidx > 0) {
                    boolean hasCheckList = JobsHandler.HasCheckList(currJobInfo.CheckList, "DS");
                    String log = hasCheckList ? "CHECKLIST button clicked for JobsIDX: " : "DELIVERED button clicked for JobsIDX: ";
                    objGlobalMethods.writeToFile(log + String.valueOf(jidx), "", "", "", "");
                }
                Boolean SettingsDistVioEnabled = !objGlobalMethods.getSettingsDistVioEnabled().equals("") ? true : false;
                SettingsDistVioEnabled = false;
                Boolean PromptDistanceViolationUponUpdate = JobsHandler.PromptDistanceViolationUponUpdate(currJobInfo, catType, catType == 0 ? 1 : 4);
                PromptDistanceViolationUponUpdate = false;

                boolean isStandardRunJob = JobsHandler.isStandardRunJob(currJobInfo);
                boolean isXDelJob = JobsHandler.isXDelJob(currJobInfo);
                boolean isContactlessDelivery = JobsHandler.isContactlessDelivery(currJobInfo);
                boolean attemptTimeOneOrMoreDaysEarlierThanWindow = JobsHandler.attemptTimeOneOrMoreDaysEarlierThanWindow(catType, currJobInfo);
                boolean isReqShipmentVerification = JobsHandler.isReqShipmentVerification(currJobInfo);
                boolean hasColdItem = JobsHandler2.hasColdItem(currJobInfo);
                boolean shipmentIsVerified = false;
                boolean hasSecuritySeal = JobsHandler.JobHasSecuritySeal(currJobInfo, true);
                boolean skiplockerBool = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "skiplocker"));
                boolean isAlps = JobsHandler.isAlpsClientIDX(currJobInfo);
                String NFCtagScannedVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "NFCtagScannedVal");
                String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                String ShipmentVerified = objGlobalMethods.getGCMPrefs(GcmPreferences, "ShipmentVerified");
                String ShipmentVerifiedCold = objGlobalMethods.getGCMPrefs(GcmPreferences, "ShipmentVerifiedCold");
                String noCallMade = "";
                objTemp ot = temploggerclass.getLatestRecordedTempObjNew2(objGlobalMethods.getGcmPreferences(), currJobInfo.JobsIDX);
                String CurrJobInfoStatusCodeIDX = "104"; //String CurrJobInfoStatusCodeIDX = String.valueOf(currJobInfo.StatusCodeIDX);

                /*if (attemptTimeOneOrMoreDaysEarlierThanWindow && !objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                    JobsHandler.CheckAnyCallMadeWithin60min(AllValidTelNums);
                    noCallMade = objGlobalMethods.getGCMPrefs(GcmPreferences, "noCallMade");
                }*/
                //String okpShowReqCallLogImgButton = objGlobalMethods.getGCMPrefs(GcmPreferences, "okpShowReqCallLogImgButton");

                if (SettingsDistVioEnabled && PromptDistanceViolationUponUpdate) {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_outofrangecfm)).show();
                } else {
                    if (isLockerJob) {
                        if (!skiplockerBool) {
                            if (getLockerAllianceJobProceedStatus > 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                builder.setCancelable(false);
                                builder.setMessage(
                                        getLockerAllianceJobProceedStatus == 2 ? ctx.getString(R.string.userkeystatusunknown) :
                                                getLockerAllianceJobProceedStatus == 3 ? ctx.getString(R.string.usernopin) :
                                                        getLockerAllianceJobProceedStatus == 4 ? ctx.getString(R.string.userkeynotactive) :
                                                                getLockerAllianceJobProceedStatus == 5 ? ctx.getString(R.string.noparcelid) :
                                                                        ctx.getString(R.string.nouserkey)
                                );
                                builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        if (getLockerAllianceJobProceedStatus == 3) {
                                            String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, objGlobalVariables.categoryTypePrefName,
                                                    objGlobalVariables.categoryTitlePrefName, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "cameraScannedCode",
                                                    "objJobInfoBringtoUpdateCODListJsonStr", "mutlipleCODjobs", "objPendingJobStatusUpdatesobjALPrefStr", "objPendingJobPhotoUpdatesobjALPrefStr",
                                                    "desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD", "isFromDashboardSub", "isFromMessaging"
                                            };
                                            String[] prefvalue = new String[]{"", "", "-1",
                                                    "", "", "", "",
                                                    "", "", "", "",
                                                    "", "", "", "", "", "", ""
                                            };
                                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                            Intent i = new Intent(myApp.currentActivity, LockerProfileActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }
                                });
                                final AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                if (objGlobalMethods.IsEmptyString(ShipmentVerified)) {
                                    objGlobalMethods.writeToFile("Locker Job require Verify Shipment for JobsIDX: " + String.valueOf(jidx), "", "", "", "");
                                    VerifyShipmentPromptNew(pcs, false, "");
                                } else {
                                    CheckListActivity.fromPage = "JobInfoActivity";
                                    Intent authIntent = new Intent(myApp.currentActivity, AuthActivity.class);
                                    String[] DIPChecklist = new fixedChecklists().getLockerOKPChecklist();
                                    //String DIPChecklistJsonStr = DIPChecklist != null && DIPChecklist.length > 0 ? new Gson().toJson(DIPChecklist) : "";

                                    String[] checklist = DIPChecklist != null && DIPChecklist.length > 0 ? JobsHandler2.getSpecificChecklist(DIPChecklist, "DS") : null;
                                    String DIPChecklistJsonStr = checklist != null && checklist.length > 0 ? new Gson().toJson(checklist) : "";

                                    /*String[] prefName = new String[]{"ReloadDueNFCscan",
                                            "isStandardRunJob",
                                            "lockerparcelid",
                                            "DIPChecklist",
                                            "isLockerJobStr",
                                            "CurrJobInfoStatusCodeIDX",
                                            "isAlps"};
                                    String[] prefvalue = new String[]{
                                            "",
                                            isStandardRunJob || isXDelJob ? "y" : "",
                                            lockerparcelid,
                                            DIPChecklistJsonStr,
                                            "t",
                                            CurrJobInfoStatusCodeIDX,
                                            isAlps ? "t" : ""
                                    };*/
                                    String[] prefName = new String[]{"ReloadDueNFCscan",
                                            "isStandardRunJob",
                                            "lockerparcelid",
                                            "isLockerJobStr",
                                            "CurrJobInfoStatusCodeIDX",
                                            "isAlps"};
                                    String[] prefvalue = new String[]{
                                            "",
                                            isStandardRunJob || isXDelJob ? "y" : "",
                                            lockerparcelid,
                                            "t",
                                            CurrJobInfoStatusCodeIDX,
                                            isAlps ? "t" : ""
                                    };
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                    objGlobalMethods.saveChecklistSP(DIPChecklistJsonStr);

                                    JobInfoActivity.accessFromDirection = 0;
                                    setFailStatusActivity.accessFromDirection = 0;
                                    setDeliveredStatusActivity.accessFromDirection = 1;
                                    MainActivity.uponLogin = 0;
                                    startActivity(authIntent);
                                    finish(); //newly inserted
                                }
                            }
                        } else {
                            Intent parentIntent = new Intent(this, CheckListActivity.class);
                            MainActivity.uponLogin = 0;
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            DrawerMainActivity.DashboardOpenFromPush = false;
                            setDeliveredStatusActivity.fromPage = "CheckListActivity";
                            CheckListActivity.ActionCode = "DS";
                            CheckListActivity.nextActivtyIntent = new Intent(this, setDeliveredStatusActivity.class);
                            CheckListActivity.fromPage = "JobInfoActivity";
                            startActivity(parentIntent);
                            this.finish();
                        }

                    }
                    /*else if ((!isStandardRunJob && !isXDelJob) && attemptTimeOneOrMoreDaysEarlierThanWindow && !objGlobalMethods.IsEmptyString(AllValidTelNums) && noCallMade.equals("t")) {
                        objGlobalMethods.writeToFile("Attempt Early Delivery for jx" + currJobInfo.JobsIDX, "", "", "", "");
                        setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
                        String totalnumofcallstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "totalnumofcall");
                        String reqCallLogImg = objGlobalMethods.getGCMPrefs(GcmPreferences, "reqCallLogImg");
                        int totalnumofcall = totalnumofcallstr.equals("") ? 0 : Integer.parseInt(totalnumofcallstr);
                        String msg = "";
                        int CirclesLogicType = -1; // -1 = proceed, 0 = no call, 1 = 1 call, 2 = 2 or more & nid photo, 3 = calllogimg taken
                        CirclesLogicType = totalnumofcall == 0 && reqCallLogImg.equals("t") ? 0 :
                                totalnumofcall >= 0 && noCallMade.equals("t") && reqCallLogImg.equals("t") && totalnumofcall < 2 ? 1 :
                                        totalnumofcall >= 0 && noCallMade.equals("t") && reqCallLogImg.equals("t") && totalnumofcall >= 2 ? 2 :
                                                -1;
                        if (CirclesLogicType >= 0 && CirclesLogicType < 2) {
                            msg = CirclesLogicType == 0 ? ctx.getString(R.string.attemptearlycalldip) :
                                    CirclesLogicType == 1 ? ctx.getString(R.string.numcallsmade) + " " + String.valueOf(totalnumofcall) + "\n\n" + ctx.getString(R.string.attemptearlycalldip) :
                                            ctx.getString(R.string.attemptearlycalldip);
                            showEarlyAttemptMsg(msg, false);
                        } else if (CirclesLogicType == 2) {
                            msg = ctx.getString(R.string.takecalllogphotodip0);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "okpShowReqCallLogImgButton", "t");
                            showEarlyAttemptMsg(msg, true);
                        } else {
                            submissionAction2(4);
                        }
                    }*/
                    /* else if (reqNFCscan) {
                        String NFCtagScannedVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "NFCtagScannedVal");
                        if (NFCtagScannedVal.equals("")) {
                            objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_scannfc_card_qr)).show();
                        } else {
                            boolean nfcverified = nfcVerified();
                            if (!nfcverified) {
                                objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_nfc_card_err)).show();
                            } else {
                                submissionAction2(4);
                            }
                        }
                    }*/
                    else if (reqNFCscan && NFCtagScannedVal.equals("")) {
                        objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_scannfc_card_qr)).show();
                    } else if (reqNFCscan && !NFCtagScannedVal.equals("") && !nfcVerified()) {
                        objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_nfc_card_err)).show();
                    } else if (hasSecuritySeal && !JobsHandler.SecuritySealAllVerified(GcmPreferences)) {
                        if (ssd == null) {
                            ssd = new securitySealDialog();
                        }
                        ssd.show(getSupportFragmentManager(), "securitySealDialog");
                    } /*else if (pcs == 1 && isReqShipmentVerification && objGlobalMethods.IsEmptyString(ShipmentVerified)) {
                        //1 pcs n req verify shipment n not verified
                        objGlobalMethods.writeToFile("JobsIDX: " + jidx + ", 1pcs, req verify" + (hasColdItem ? ", has cold" : "."), "", "", "", "");

                    } else if (pcs > 1 && isReqShipmentVerification  && objGlobalMethods.IsEmptyString(ShipmentVerified)) {
                        //more than 1 pcs n req verify shipment n not verified
                        objGlobalMethods.writeToFile("JobsIDX: " + jidx + ", >1pcs, req verify" + (hasColdItem ? ", has cold" : "."), "", "", "", "");

                    } else if (pcs > 1 && !isReqShipmentVerification && objGlobalMethods.IsEmptyString(ShipmentVerified)) {
                        //more than 1 pcs n not req verify shipment n not verified
                        objGlobalMethods.writeToFile("JobsIDX: " + jidx + ", >1pcs, verify not req.", "", "", "", "");

                    }*/ else if (ot != null && objGlobalMethods.IsEmptyString(ot.TempLastRecorded)) {
                        if (ot.Device_Type == 22) {
                            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                            String JobsIDXBTLScanned = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "JobsIDXBTLScanned");
                            String jx = "";
                            String b = "";
                            int pipeidx = -1;
                            if (!objGlobalMethods.IsEmptyString(JobsIDXBTLScanned)) {
                                pipeidx = JobsIDXBTLScanned.indexOf("|");
                                jx = JobsIDXBTLScanned.substring(0, pipeidx);
                                b = JobsIDXBTLScanned.substring(pipeidx + 1, pipeidx + 2);
                            }
                            if (objGlobalMethods.IsEmptyString(JobsIDXBTLScanned) || (jx.equals(String.valueOf(currJobInfo.JobsIDX)) && b.equals("t"))) {
                                promptToScanForBTlogger();
                            } else if (!objGlobalMethods.IsEmptyString(JobsIDXBTLScanned) && !jx.equals(String.valueOf(currJobInfo.JobsIDX))) {
                                promptToScanForBTlogger();
                            } else if ((jx.equals(String.valueOf(currJobInfo.JobsIDX)) && b.equals("f")) || (!jx.equals(String.valueOf(currJobInfo.JobsIDX)))) {
                                tle = new tempLoggerEntryDialog();
                                tle.setJobsIDX(currJobInfo.JobsIDX);
                                tle.setLaunchCFS(false);
                                tle.show(getSupportFragmentManager(), "templogger");
                            }
                        } else {
                            tle = new tempLoggerEntryDialog();
                            tle.setJobsIDX(currJobInfo.JobsIDX);
                            tle.setLaunchCFS(false);
                            tle.show(getSupportFragmentManager(), "templogger");
                        }
                    }
                    /*else if (isReqShipmentVerification && hasColdItem && currJobInfo.Pieces >= 2 && (objGlobalMethods.IsEmptyString(ShipmentVerified) || objGlobalMethods.IsEmptyString(ShipmentVerifiedCold))) {
                        objGlobalMethods.writeToFile("Require Verify COLD and NON COLD Shipment for JobsIDX: " + jidx, "", "", "", "");
                        VerifyShipmentPromptNew(pcs, isAlps, ctx.getString(R.string.scan_pink_label_cnc));
                    } else if (isReqShipmentVerification && hasColdItem && currJobInfo.Pieces > 0 && currJobInfo.Pieces < 2 && objGlobalMethods.IsEmptyString(ShipmentVerified)) {
                        objGlobalMethods.writeToFile("Require Verify COLD Shipment for JobsIDX: " + jidx, "", "", "", "");
                        VerifyShipmentPromptNew(pcs, isAlps, ctx.getString(R.string.scan_pink_label_c));
                    } else if (isReqShipmentVerification && objGlobalMethods.IsEmptyString(ShipmentVerified)) {
                        objGlobalMethods.writeToFile("Require Verify Shipment for JobsIDX: " + String.valueOf(jidx), "", "", "", "");
                        VerifyShipmentPromptNew(pcs, isAlps, ctx.getString(R.string.scan_white_label));
                    } */
                    else {
                        submissionAction2(4);
                    }
                }
                //new code for btnPUOKPclick3


                //submissionAction2(4);
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity btnPUOKPclickNew. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void showEarlyAttemptMsg(final String directMsg, final boolean proceed) {
        Context c = myApp.getAppContext();
        int added = 0;
        try {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);

            final AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            final View dv = myApp.currentActivity.getLayoutInflater().inflate(R.layout.custom_can_cannot_dialog, null);
            builder.setView(dv);
            builder.setCancelable(false);
            TextView txtTitle = dv.findViewById(R.id.txtTitle);
            txtTitle.setText(c.getString(R.string.tonote));
            TextView btnCustomCanCannotOK = (TextView) dv.findViewById(R.id.btnCustomCanCannotOK);
            LinearLayout LLcustomCanCannot_msg = (LinearLayout) dv.findViewById(R.id.LLcustomCanCannot_msg);
            LinearLayout LLcustomCanCannotMain = (LinearLayout) dv.findViewById(R.id.LLcustomCanCannotMain);

            TextView tv = new TextView(c);
            tv.setText(directMsg);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setTextColor(ContextCompat.getColor(c, R.color.black));
            tv.setAllCaps(true);
            tv.setTypeface(null, Typeface.NORMAL);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setLayoutParams(params);
            tv.setPadding(75, 10, 75, 0);
            LLcustomCanCannot_msg.addView(tv);
            added++;
            final AlertDialog dvd = builder.create();

            btnCustomCanCannotOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dvd.cancel();
                        if (proceed) {
                            submissionAction2(4);
                        }
                        //objGlobalMethods.setGCMPrefs(objGlobalMethods.getGcmPreferences(), "okpShowReqCallLogImgButton", "t");
                    } catch (Exception e) {

                    }
                }
            });

            if (added > 0) {
                dvd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dvd.show();
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity showEarlyAttemptMsg. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void btnDIPclick(View view) {
        submissionAction2(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_job_info, menu);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        //canReqExt = currJobInfo.getPromiseFrom() != null && currJobInfo.getPromiseTo() != null ? false : true;
        if (catType == 0) {
            //pip
            menu.findItem(R.id.tl).setVisible(false);
            menu.findItem(R.id.menuTag).setVisible(false);
            menu.findItem(R.id.qr).setVisible(AreqNFCscan);
            menu.findItem(R.id.resetotp).setVisible(false);
            menu.findItem(R.id.promiseOpt).setVisible(false);
            menu.findItem(R.id.extOpt).setVisible(false);
        } else if (catType == 1) {
            //pu
            menu.findItem(R.id.tl).setVisible(false);
            menu.findItem(R.id.verifyme).setVisible(false);
            menu.findItem(R.id.menuTag).setVisible(false);
            menu.findItem(R.id.qr).setVisible(false);
            menu.findItem(R.id.resetotp).setVisible(false);
            menu.findItem(R.id.promiseOpt).setVisible(false);
            menu.findItem(R.id.extOpt).setVisible(false);
        } else if (catType == 2) {
            //dip
            menu.findItem(R.id.tl).setVisible(hasBTL);
            menu.findItem(R.id.menuTag).setVisible(false);
            menu.findItem(R.id.qr).setVisible(AreqNFCscan);
            menu.findItem(R.id.resetotp).setVisible((ahasOTP && aOtpLocked) && !aCustIDVerificationNotRequired && !ahasVerifiedOTP);
            //menu.findItem(R.id.promiseOpt).setVisible(canReqExt && JobsHandler2.hasPromiseInterval(GcmPreferences));
            menu.findItem(R.id.promiseOpt).setVisible(false);
            //menu.findItem(R.id.extOpt).setVisible(showExtOpt);
            menu.findItem(R.id.extOpt).setVisible(false);
        } else if (catType == 3) {
            //del
            menu.findItem(R.id.tl).setVisible(false);
            menu.findItem(R.id.verifyme).setVisible(false);
            menu.findItem(R.id.menuTag).setVisible(false);
            menu.findItem(R.id.qr).setVisible(false);
            menu.findItem(R.id.resetotp).setVisible(false);
            menu.findItem(R.id.promiseOpt).setVisible(false);
            menu.findItem(R.id.extOpt).setVisible(false);
        } else if (catType == 4) {
            //fp
            menu.findItem(R.id.tl).setVisible(false);
            menu.findItem(R.id.verifyme).setVisible(false);
            menu.findItem(R.id.menuTag).setVisible(false);
            menu.findItem(R.id.qr).setVisible(false);
            menu.findItem(R.id.resetotp).setVisible(false);
            menu.findItem(R.id.promiseOpt).setVisible(false);
            menu.findItem(R.id.extOpt).setVisible(false);
        } else if (catType == 5) {
            //fd
            menu.findItem(R.id.tl).setVisible(false);
            menu.findItem(R.id.verifyme).setVisible(false);
            menu.findItem(R.id.menuTag).setVisible(false);
            menu.findItem(R.id.qr).setVisible(false);
            menu.findItem(R.id.resetotp).setVisible(false);
            menu.findItem(R.id.promiseOpt).setVisible(false);
            menu.findItem(R.id.extOpt).setVisible(false);
        } else if (catType == 6 || catType == 7) {
            //pd
            menu.findItem(R.id.tl).setVisible(false);
            menu.findItem(R.id.verifyme).setVisible(false);
            menu.findItem(R.id.menuTag).setVisible(false);
            menu.findItem(R.id.qr).setVisible(false);
            menu.findItem(R.id.resetotp).setVisible(false);
            menu.findItem(R.id.promiseOpt).setVisible(false);
            menu.findItem(R.id.extOpt).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent parentIntent;
        Gson gson = new Gson();
        List<objUpdateCOD_PostData> objUpdateCODPostDataList = null;
        String mutlipleCODjobs = "";
        List<objJobInfo> objJobInfoBringtoUpdateCODList = null;
        String objJobInfoBringtoUpdateCODListJsonStr = "";
        //noinspection SimplifiableIfStatement
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        Boolean JobhasCOD = false;
        String part1cnno = "";

        if (item.getItemId() == R.id.tl) {
            promptToScanForBTlogger();
        } else if (item.getItemId() == R.id.waze) {
            cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()) {
                ////following attempts to open waze map, if installed, else, open playstore
                ////z == zoom level, max value 8192
                try {
                    // Launch Waze to look for Hawaii:
                    //if waze not installed, will open browser
                    //better to check if waze is stalled first
                    String LATstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LAT");
                    String LNGstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LNG");
                    String ll = LATstr + "," + LNGstr;
                    String postalstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
                    if (!objGlobalMethods.IsEmptyString(postalstr)) {
                        if (JobsHandler2.wazeInstalled()) {
                            //String url = "https://waze.com/ul?ll=1.32435477286284,103.802951580752&navigate=yes";
                            String url = "https://waze.com/ul?ll=" + ll + "&navigate=yes";
                            //String url = "https://waze.com/ul?q=Singapore " + postalstr + "&navigate=yes";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        } else {
                            objGlobalMethods.showInstallWaze().show();
                        }
                    }
                } catch (ActivityNotFoundException ex) {
                    // If Waze is not installed, open it in Google Play:
                    objGlobalMethods.showInstallWaze().show();
                }
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_unableopenmapnointernet));
            }
        } else if (item.getItemId() == R.id.promiseOpt) {
            pod = new promiseOptDialog();
            pod.show(getSupportFragmentManager(), "promiseOptDialog");
        } else if (item.getItemId() == R.id.extOpt) {
            ext = new extDialog();
            ext.show(getSupportFragmentManager(), "extDialog");
        } else if (item.getItemId() == R.id.resetotp) {
            ResetOTP ro = new ResetOTP();
            ro.execute();
        } else if (item.getItemId() == R.id.qr) {
            objGlobalMethods.setGCMPrefs(GcmPreferences, "scanqr", "y");
            launchQRScan();
        } else if (item.getItemId() == R.id.verifyme) {
            VerifyMe v = new VerifyMe();
            v.execute();
        } else if (item.getItemId() == R.id.map) {
            cd = new ConnectionDetector(null);
            if (cd.isConnectingToInternet()) {
                //parentIntent = new Intent(myApp.getAppContext(), MapsActivity.class);
                parentIntent = new Intent(myApp.currentActivity, MapsActivity.class);
                JobInfoActivity.accessFromDirection = 0;
                MainActivity.uponLogin = 0;
                startActivity(parentIntent);
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_unableopenmapnointernet));
            }
        } else if (item.getItemId() == R.id.menuTag) {
            parentIntent = new Intent(myApp.currentActivity, Label_Tagging_Activity.class);
            JobInfoActivity.accessFromDirection = 0;
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
        }



        /*switch (item.getItemId()) {
            case R.id.tl:
                promptToScanForBTlogger();
                break;
            case R.id.waze:
                cd = new ConnectionDetector(null);
                if (cd.isConnectingToInternet()) {
                    ////following attempts to open waze map, if installed, else, open playstore
                    ////z == zoom level, max value 8192
                    try {
                        // Launch Waze to look for Hawaii:
                        //if waze not installed, will open browser
                        //better to check if waze is stalled first
                        String LATstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LAT");
                        String LNGstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LNG");
                        String ll = LATstr + "," + LNGstr;
                        String postalstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
                        if (!objGlobalMethods.IsEmptyString(postalstr)) {
                            if (JobsHandler2.wazeInstalled()) {
                                //String url = "https://waze.com/ul?ll=1.32435477286284,103.802951580752&navigate=yes";
                                String url = "https://waze.com/ul?ll=" + ll + "&navigate=yes";
                                //String url = "https://waze.com/ul?q=Singapore " + postalstr + "&navigate=yes";
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                            } else {
                                objGlobalMethods.showInstallWaze().show();
                            }
                        }
                    } catch (ActivityNotFoundException ex) {
                        // If Waze is not installed, open it in Google Play:
                        objGlobalMethods.showInstallWaze().show();
                    }
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_unableopenmapnointernet));
                }
                break;
            case R.id.promiseOpt:
                pod = new promiseOptDialog();
                pod.show(getSupportFragmentManager(), "promiseOptDialog");
                break;
            case R.id.extOpt:
                ext = new extDialog();
                ext.show(getSupportFragmentManager(), "extDialog");
                break;
            case R.id.resetotp:
                ResetOTP ro = new ResetOTP();
                ro.execute();
                break;
            case R.id.qr:
                objGlobalMethods.setGCMPrefs(GcmPreferences, "scanqr", "y");
                launchQRScan();
                break;
            case R.id.verifyme:
                VerifyMe v = new VerifyMe();
                v.execute();
                break;
            case R.id.map:
                //open map
                //cd = new ConnectionDetector(context);
                cd = new ConnectionDetector(null);
                if (cd.isConnectingToInternet()) {
                    //parentIntent = new Intent(myApp.getAppContext(), MapsActivity.class);
                    parentIntent = new Intent(myApp.currentActivity, MapsActivity.class);
                    JobInfoActivity.accessFromDirection = 0;
                    MainActivity.uponLogin = 0;
                    startActivity(parentIntent);
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_unableopenmapnointernet));
                }
                break;
            case R.id.menuTag:
                //parentIntent = new Intent(myApp.getAppContext(), Label_Tagging_Activity.class);
                parentIntent = new Intent(myApp.currentActivity, Label_Tagging_Activity.class);
                JobInfoActivity.accessFromDirection = 0;
                MainActivity.uponLogin = 0;
                startActivity(parentIntent);
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void updatePU_w_Checklist(int updatemode, int scannedtype, String scannedval, boolean isCollectionJob) {
        try {
            String[] prefName = null;
            String[] prefvalue = null;
            Boolean JobhasCOD = false;
            Gson gson = new Gson();
            List<objUpdateCOD_PostData> objUpdateCODPostDataList = null;
            String mutlipleCODjobs = "";
            List<objJobInfo> objJobInfoBringtoUpdateCODList = null;
            String objJobInfoBringtoUpdateCODListJsonStr = "";
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (currJobInfo == null)
                currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);

            ////update COD portion
            if (currJobInfo.CODCollected == null || !currJobInfo.CODCollected) {
                JobhasCOD = JobsHandler.checkForCODwithCatType(currJobInfo, catType);
            } else if (currJobInfo.CODCollected != null && currJobInfo.CODCollected) {
                JobhasCOD = false;
            }
            if (JobhasCOD) {
                String RegID = objGlobalMethods.getRegistrationIdOnly();
                objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, currJobInfo, 0, true, RegID, false, "", "");
                currJobInfo.setpendingStatusUpdate(49);
                currJobInfo.setReceiver("");
                currJobInfo.setFailureReason("");

                objJobInfoBringtoUpdateCODList = JobsHandler.RTNobjJobInfoBringtoUpdateCODList(objJobInfoBringtoUpdateCODList, currJobInfo);

                if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                    objJobInfo[] objJobInfoBringtoUpdateCODAL = objJobInfoBringtoUpdateCODList.toArray(new objJobInfo[objJobInfoBringtoUpdateCODList.size()]);
                    objJobInfoBringtoUpdateCODListJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL);
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", objJobInfoBringtoUpdateCODListJsonStr);

                    objUpdateCOD_PostData[] objUpdateCODPostDataAL = objUpdateCODPostDataList.toArray(new objUpdateCOD_PostData[objUpdateCODPostDataList.size()]);
                    mutlipleCODjobs = gson.toJson(objUpdateCODPostDataAL);
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, "mutlipleCODjobs", mutlipleCODjobs);

                    //////ojisPage == 0 == JobInfoActivity | ojisPage == 1 == JobsListGroupActivity | ojisPage == 2 == setDeliveredStatusActivity | ojisPage == 3 == setDeliveredMassStatusActivity |
                    //////ojisPage == 6 == CheckListActivity
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, "ojisPage", "6");

                    prefName = new String[]{"objJobInfoBringtoUpdateCODListJsonStr", "mutlipleCODjobs", "ojisPage"};
                    prefvalue = new String[]{objJobInfoBringtoUpdateCODListJsonStr, mutlipleCODjobs, "6"};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    if (!GcmPreferences.getString("hfJobsInfoArrJsonStr", "").isEmpty()) {
                        String objCheckedItemsIDXJsonStr = "";
                        String[] CheckedItemsIDX = new String[]{String.valueOf(currJobInfo.JobsIDX)};
                        objCheckedItemsIDXJsonStr = gson.toJson(CheckedItemsIDX);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX, objCheckedItemsIDXJsonStr);
                    }

                    Intent intent = new Intent(myApp.currentActivity, UpdateCOD_List.class);
                    if (updatemode == 2) {
                        /*objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedtype", scannedtype == 1 ? "barcode" : scannedtype == 2 ? "cnno" : "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedval", scannedval);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");*/

                        prefName = new String[]{"scannedtype",
                                "scannedval", "cameraScannedCode"};
                        prefvalue = new String[]{scannedtype == 1 ? "barcode" : scannedtype == 2 ? "cnno" : "",
                                scannedval, ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    } else {
                        /*objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedtype", "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedval", "");*/

                        prefName = new String[]{"scannedtype", "scannedval"};
                        prefvalue = new String[]{"", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    }

                    //CheckListActivity.ActionCode = "PS";
                    CheckListActivity.ActionCode = isCollectionJob ? "CS" : "PS";
                    CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), UpdateCOD_List.class);
                    CheckListActivity.fromPage = "JobInfoActivity";
                    //String[] checklist = currJobInfo.CheckList != null && currJobInfo.CheckList.length > 0 ? JobsHandler2.getSpecificChecklist(currJobInfo.CheckList, "PS") : null;
                    String[] checklist = currJobInfo.CheckList != null && currJobInfo.CheckList.length > 0 ? JobsHandler2.getSpecificChecklist(currJobInfo.CheckList, CheckListActivity.ActionCode) : null;
                    String DIPChecklistJsonStr = checklist != null && checklist.length > 0 ? gson.toJson(checklist) : "";
                    //objGlobalMethods.setGCMPrefs(GcmPreferences, "DIPChecklist", DIPChecklistJsonStr);
                    objGlobalMethods.saveChecklistSP(DIPChecklistJsonStr);
                    intent = new Intent(myApp.currentActivity, CheckListActivity.class);

                    JobInfoActivity.accessFromDirection = 0;
                    startActivity(intent);
                    finish();
                } else {
                    if (updatemode == 2 && scannedtype != 0 && !scannedval.isEmpty()) {
                        if (scannedtype == 1) {
                            //barcode
                            Boolean succ = JobsHandler.processBarcodeTagUpdate(currJobInfo, scannedval);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                            if (succ) {
                                objGlobalMethods.writeToFile("JobInfoActivity updatePU_w_Checklist JobsHandler.processBarcodeTagUpdate ended successfully.", "", "", "", "");
                            } else {
                                objGlobalMethods.writeToFile("JobInfoActivity updatePU_w_Checklist JobsHandler.processBarcodeTagUpdate ended with error.", "", "", "", "");
                            }
                        } else if (scannedtype == 2) {
                            //cn
                            Boolean succ = JobsHandler.processCNUpdate(currJobInfo, scannedval);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                            if (succ) {
                                objGlobalMethods.writeToFile("JobInfoActivity updatePU_w_Checklist JobsHandler.processCNUpdate ended successfully.", "", "", "", "");
                            } else {
                                objGlobalMethods.writeToFile("JobInfoActivity updatePU_w_Checklist JobsHandler.processCNUpdate ended with error.", "", "", "", "");
                            }
                        }
                    } else {
                        /*objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedtype", "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedval", "");*/
                        prefName = new String[]{"scannedtype", "scannedval"};
                        prefvalue = new String[]{"", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    }

                    //CheckListActivity.ActionCode = "PS";
                    CheckListActivity.ActionCode = isCollectionJob ? "CS" : "PS";
                    CheckListActivity.fromPage = "JobInfoActivity";
                    String CurrJobInfoStatusCodeIDX = "103"; //String CurrJobInfoStatusCodeIDX = "";
                    //String[] checklist = currJobInfo.CheckList != null && currJobInfo.CheckList.length > 0 ? JobsHandler2.getSpecificChecklist(currJobInfo.CheckList, "PS") : null;
                    String[] checklist = currJobInfo.CheckList != null && currJobInfo.CheckList.length > 0 ? JobsHandler2.getSpecificChecklist(currJobInfo.CheckList, CheckListActivity.ActionCode) : null;
                    String DIPChecklistJsonStr = checklist != null && checklist.length > 0 ? gson.toJson(checklist) : "";

                    /*prefName = new String[]{"DIPChecklist", "CurrJobInfoStatusCodeIDX"};
                    prefvalue = new String[]{DIPChecklistJsonStr, CurrJobInfoStatusCodeIDX};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);*/

                    objGlobalMethods.setGCMPrefs(GcmPreferences, "CurrJobInfoStatusCodeIDX", CurrJobInfoStatusCodeIDX);
                    objGlobalMethods.saveChecklistSP(DIPChecklistJsonStr);

                    Intent intent = new Intent(myApp.currentActivity, CheckListActivity.class);

                    JobInfoActivity.accessFromDirection = 0;
                    startActivity(intent);
                    finish();

                    //UpdateJobStatusPU(currJobInfo.JobsIDX);
                }
            } else {
                if (updatemode == 2 && scannedtype != 0 && !scannedval.isEmpty()) {
                    if (scannedtype == 1) {
                        //barcode
                        Boolean succ = JobsHandler.processBarcodeTagUpdate(currJobInfo, scannedval);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        if (succ) {
                            objGlobalMethods.writeToFile("JobInfoActivity updatePU_w_Checklist JobsHandler.processBarcodeTagUpdate ended successfully.", "", "", "", "");
                        } else {
                            objGlobalMethods.writeToFile("JobInfoActivity updatePU_w_Checklist JobsHandler.processBarcodeTagUpdate ended with error.", "", "", "", "");
                        }
                    } else if (scannedtype == 2) {
                        //cn
                        Boolean succ = JobsHandler.processCNUpdate(currJobInfo, scannedval);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        if (succ) {
                            objGlobalMethods.writeToFile("JobInfoActivity updatePU_w_Checklist JobsHandler.processCNUpdate ended successfully.", "", "", "", "");
                        } else {
                            objGlobalMethods.writeToFile("JobInfoActivity updatePU_w_Checklist JobsHandler.processCNUpdate ended with error.", "", "", "", "");
                        }
                    }
                } else {
                    /*objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedtype", "");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedval", "");*/
                    prefName = new String[]{"scannedtype", "scannedval"};
                    prefvalue = new String[]{"", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                }

                //CheckListActivity.ActionCode = "PS";
                CheckListActivity.ActionCode = isCollectionJob ? "CS" : "PS";
                CheckListActivity.fromPage = "JobInfoActivity";
                String CurrJobInfoStatusCodeIDX = "103"; //String CurrJobInfoStatusCodeIDX = "";
                //String[] checklist = currJobInfo.CheckList != null && currJobInfo.CheckList.length > 0 ? JobsHandler2.getSpecificChecklist(currJobInfo.CheckList, "PS") : null;
                String[] checklist = currJobInfo.CheckList != null && currJobInfo.CheckList.length > 0 ? JobsHandler2.getSpecificChecklist(currJobInfo.CheckList, CheckListActivity.ActionCode) : null;
                String DIPChecklistJsonStr = checklist != null && checklist.length > 0 ? gson.toJson(checklist) : "";

                String dladdress = JobsHandler.getJobAddress(ji);

                prefName = new String[]{"dladdress", "CurrJobInfoStatusCodeIDX"};
                prefvalue = new String[]{dladdress, CurrJobInfoStatusCodeIDX};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                //objGlobalMethods.setGCMPrefs(GcmPreferences, "CurrJobInfoStatusCodeIDX", CurrJobInfoStatusCodeIDX);
                objGlobalMethods.saveChecklistSP(DIPChecklistJsonStr);

                Intent intent = new Intent(myApp.currentActivity, CheckListActivity.class);

                JobInfoActivity.accessFromDirection = 0;
                startActivity(intent);
                finish();

                //UpdateJobStatusPU(currJobInfo.JobsIDX);
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity updatePU_w_Checklist. Excpetion: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void updatePU2(int updatemode, int scannedtype, String scannedval) {
        try {
            Boolean JobhasCOD = false;
            Gson gson = new Gson();
            List<objUpdateCOD_PostData> objUpdateCODPostDataList = null;
            String mutlipleCODjobs = "";
            List<objJobInfo> objJobInfoBringtoUpdateCODList = null;
            String objJobInfoBringtoUpdateCODListJsonStr = "";
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (currJobInfo == null) {
                currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            }
            ////update COD portion
            if (currJobInfo.CODCollected == null || !currJobInfo.CODCollected) {
                JobhasCOD = JobsHandler.checkForCODwithCatType(currJobInfo, catType);
            } else if (currJobInfo.CODCollected != null && currJobInfo.CODCollected) {
                JobhasCOD = false;
            }
            if (JobhasCOD) {
                String RegID = objGlobalMethods.getRegistrationIdOnly();
                objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, currJobInfo, 0, true, RegID, false, "", "");
                currJobInfo.setpendingStatusUpdate(49);
                currJobInfo.setReceiver("");
                currJobInfo.setFailureReason("");

                objJobInfoBringtoUpdateCODList = JobsHandler.RTNobjJobInfoBringtoUpdateCODList(objJobInfoBringtoUpdateCODList, currJobInfo);

                if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                    objJobInfo[] objJobInfoBringtoUpdateCODAL = objJobInfoBringtoUpdateCODList.toArray(new objJobInfo[objJobInfoBringtoUpdateCODList.size()]);
                    objJobInfoBringtoUpdateCODListJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", objJobInfoBringtoUpdateCODListJsonStr);

                    objUpdateCOD_PostData[] objUpdateCODPostDataAL = objUpdateCODPostDataList.toArray(new objUpdateCOD_PostData[objUpdateCODPostDataList.size()]);
                    mutlipleCODjobs = gson.toJson(objUpdateCODPostDataAL);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "mutlipleCODjobs", mutlipleCODjobs);

                    //ojisPage == 0 == JobInfoActivity | ojisPage == 1 == JobsListGroupActivity | ojisPage == 2 == setDeliveredStatusActivity | ojisPage == 3 == setDeliveredMassStatusActivity |
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "ojisPage", "0");

                    if (!GcmPreferences.getString("hfJobsInfoArrJsonStr", "").isEmpty()) {
                        String objCheckedItemsIDXJsonStr = "";
                        String[] CheckedItemsIDX = new String[]{String.valueOf(currJobInfo.JobsIDX)};
                        objCheckedItemsIDXJsonStr = gson.toJson(CheckedItemsIDX);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX, objCheckedItemsIDXJsonStr);
                    }

                    //Intent intent = new Intent(myApp.getAppContext(), UpdateCOD_List.class);
                    Intent intent = new Intent(myApp.currentActivity, UpdateCOD_List.class);
                    if (updatemode == 2) {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedtype", scannedtype == 1 ? "barcode" : scannedtype == 2 ? "cnno" : "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedval", scannedval);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedtype", "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedval", "");
                    }
                    JobInfoActivity.accessFromDirection = 0;
                    startActivity(intent);
                    finish();
                } else {
                    if (updatemode == 2 && scannedtype != 0 && !scannedval.isEmpty()) {
                        if (scannedtype == 1) {
                            //barcode
                            Boolean succ = JobsHandler.processBarcodeTagUpdate(currJobInfo, scannedval);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                            if (succ) {
                                objGlobalMethods.writeToFile("JobInfoActivity updatePU2 JobsHandler.processBarcodeTagUpdate ended successfully.", "", "", "", "");
                            } else {
                                objGlobalMethods.writeToFile("JobInfoActivity updatePU2 JobsHandler.processBarcodeTagUpdate ended with error.", "", "", "", "");
                            }
                        } else if (scannedtype == 2) {
                            //cn
                            Boolean succ = JobsHandler.processCNUpdate(currJobInfo, scannedval);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                            if (succ) {
                                objGlobalMethods.writeToFile("JobInfoActivity updatePU2 JobsHandler.processCNUpdate ended successfully.", "", "", "", "");
                            } else {
                                objGlobalMethods.writeToFile("JobInfoActivity updatePU2 JobsHandler.processCNUpdate ended with error.", "", "", "", "");
                            }
                        }
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedtype", "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedval", "");
                    }
                    UpdateJobStatusPU(currJobInfo.JobsIDX);
                }
            } else {
                if (updatemode == 2 && scannedtype != 0 && !scannedval.isEmpty()) {
                    if (scannedtype == 1) {
                        //barcode
                        Boolean succ = JobsHandler.processBarcodeTagUpdate(currJobInfo, scannedval);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        if (succ) {
                            objGlobalMethods.writeToFile("JobInfoActivity updatePU2 JobsHandler.processBarcodeTagUpdate ended successfully.", "", "", "", "");
                        } else {
                            objGlobalMethods.writeToFile("JobInfoActivity updatePU2 JobsHandler.processBarcodeTagUpdate ended with error.", "", "", "", "");
                        }
                    } else if (scannedtype == 2) {
                        //cn
                        Boolean succ = JobsHandler.processCNUpdate(currJobInfo, scannedval);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        if (succ) {
                            objGlobalMethods.writeToFile("JobInfoActivity updatePU2 JobsHandler.processCNUpdate ended successfully.", "", "", "", "");
                        } else {
                            objGlobalMethods.writeToFile("JobInfoActivity updatePU2 JobsHandler.processCNUpdate ended with error.", "", "", "", "");
                        }
                    }
                } else {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedtype", "");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "scannedval", "");
                }
                UpdateJobStatusPU(currJobInfo.JobsIDX);
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity updatePU2. Excpetion: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void updatePU() {
        Boolean JobhasCOD = false;
        Gson gson = new Gson();
        List<objUpdateCOD_PostData> objUpdateCODPostDataList = null;
        String mutlipleCODjobs = "";
        List<objJobInfo> objJobInfoBringtoUpdateCODList = null;
        String objJobInfoBringtoUpdateCODListJsonStr = "";

        ////update COD portion
        if (currJobInfo.CODCollected == null || !currJobInfo.CODCollected) {
            JobhasCOD = JobsHandler.checkForCODwithCatType(currJobInfo, catType);
        } else if (currJobInfo.CODCollected != null && currJobInfo.CODCollected) {
            JobhasCOD = false;
        }
        if (JobhasCOD) {
            String RegID = objGlobalMethods.getRegistrationIdOnly();
            objUpdateCODPostDataList = JobsHandler.RTNobjUpdateCODPostDataList(objUpdateCODPostDataList, currJobInfo, 0, true, RegID, false, "", "");
            currJobInfo.setpendingStatusUpdate(49);
            currJobInfo.setReceiver("");
            currJobInfo.setFailureReason("");

            objJobInfoBringtoUpdateCODList = JobsHandler.RTNobjJobInfoBringtoUpdateCODList(objJobInfoBringtoUpdateCODList, currJobInfo);

            if (objUpdateCODPostDataList != null && objUpdateCODPostDataList.size() > 0) {
                objJobInfo[] objJobInfoBringtoUpdateCODAL = objJobInfoBringtoUpdateCODList.toArray(new objJobInfo[objJobInfoBringtoUpdateCODList.size()]);
                objJobInfoBringtoUpdateCODListJsonStr = gson.toJson(objJobInfoBringtoUpdateCODAL);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", objJobInfoBringtoUpdateCODListJsonStr);

                objUpdateCOD_PostData[] objUpdateCODPostDataAL = objUpdateCODPostDataList.toArray(new objUpdateCOD_PostData[objUpdateCODPostDataList.size()]);
                mutlipleCODjobs = gson.toJson(objUpdateCODPostDataAL);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "mutlipleCODjobs", mutlipleCODjobs);

                //ojisPage == 0 == JobInfoActivity | ojisPage == 1 == JobsListGroupActivity | ojisPage == 2 == setDeliveredStatusActivity | ojisPage == 3 == setDeliveredMassStatusActivity |
                objGlobalMethods.setGCMPrefs(GcmPreferences, "ojisPage", "0");

                if (!GcmPreferences.getString("hfJobsInfoArrJsonStr", "").isEmpty()) {
                    String objCheckedItemsIDXJsonStr = "";
                    String[] CheckedItemsIDX = new String[]{String.valueOf(currJobInfo.JobsIDX)};
                    objCheckedItemsIDXJsonStr = gson.toJson(CheckedItemsIDX);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX, objCheckedItemsIDXJsonStr);
                }

                //Intent intent = new Intent(myApp.getAppContext(), UpdateCOD_List.class);
                Intent intent = new Intent(myApp.currentActivity, UpdateCOD_List.class);
                JobInfoActivity.accessFromDirection = 0;
                setFailStatusActivity.accessFromDirection = 0;
                setDeliveredStatusActivity.accessFromDirection = 0;
                startActivity(intent);
                finish();
            } else {
                UpdateJobStatus(49, "", myApp.getAppContext().getString(R.string.alert_cfmupdatestatusPU), false);
            }
        } else {
            UpdateJobStatus(49, "", myApp.getAppContext().getString(R.string.alert_cfmupdatestatusPU), false);
        }
        ////update COD portion
    }

    private void updateOKP4() {
        Boolean JobhasCOD = false;
        Boolean P2JobhasCOD = false;
        Boolean hasDOinv = false;
        Boolean hasSpecialCOD = false;
        Boolean hasCheckList = false;
        Boolean hasOtp = false;
        Boolean OtpLocked = false;
        Boolean hasVerifiedOTP = false;
        boolean isLockerJob = false;
        boolean isResidentialType = false;
        boolean cannotTakePhoto = false;
        boolean jiAddedChecklist = false;
        //Boolean isSecurityBag = false;
        Boolean hasSecuritySeal = false;
        boolean reqItemProofImg = false;
        boolean CustIDVerificationNotRequired = false;
        boolean hasColdItem = false;
        String CurrJobInfoStatusCodeIDX = "";
        Gson gson = new Gson();
        Intent parentIntent = null;
        String JobhasCODStr = "";
        String P2JobhasCODStr = "";
        Context ctx = myApp.getAppContext();
        /*String ChecklistVariablesJsonStr = "";
        ChecklistVariables cv = new ChecklistVariables();*/
        try {
            hasCheckList = JobsHandler.HasCheckList(currJobInfo.CheckList, "DS");
            OtpLocked = ((currJobInfo.OTPtries == null || currJobInfo.OTPtries < 3) && hasCheckList) || (currJobInfo.CheckList == null || currJobInfo.CheckList.length == 0) ? false : true;
            hasVerifiedOTP = currJobInfo.TypeOfPinVerification == null || currJobInfo.TypeOfPinVerification != 1 ? false : true;
            hasDOinv = JobsHandler.JobHasDoinv2(currJobInfo, GcmPreferences);
            //hasSecuritySeal = JobsHandler.JobHasSecuritySeal(currJobInfo, true);
            hasSpecialCOD = JobsHandler.checkforDOCOD(currJobInfo);
            hasOtp = ((currJobInfo.FLAG & 512) == 512) && currJobInfo.PIN != null && !currJobInfo.PIN.isEmpty();
            CustIDVerificationNotRequired = JobsHandler.CustIDVerificationNotRequired(currJobInfo);
            isResidentialType = (JobsHandler2.getLocationType(currJobInfo) == 1);
            cannotTakePhoto = JobsHandler.CannotTakePhoto(currJobInfo, catType);
            hasColdItem = JobsHandler2.hasColdItem(currJobInfo);
            isLockerJob = JobsHandler.isLockerJob(currJobInfo);
            CurrJobInfoStatusCodeIDX = "104"; //CurrJobInfoStatusCodeIDX = String.valueOf(currJobInfo.StatusCodeIDX);
            List<String> CheckListList = null;
            boolean isReqShipmentVerification = JobsHandler.isReqShipmentVerification(currJobInfo);

            if (!hasCheckList && (hasColdItem && currJobInfo.Pieces >= 2)) {
                fixedChecklists fcl = new fixedChecklists();
                String[] cl = fcl.getColdNonCold_list();
                currJobInfo.CheckList = cl;
                hasCheckList = true;
                jiAddedChecklist = true;
            } else if (!hasCheckList && hasColdItem && currJobInfo.Pieces > 0 && currJobInfo.Pieces < 2) {
                fixedChecklists fcl = new fixedChecklists();
                String[] cl = fcl.getCold_list();
                currJobInfo.CheckList = cl;
                hasCheckList = true;
                jiAddedChecklist = true;
            } else if (hasCheckList && (hasColdItem && currJobInfo.Pieces >= 2)) {
                fixedChecklists fcl = new fixedChecklists();
                CheckListList = new ArrayList<String>(Arrays.asList(currJobInfo.CheckList));
                CheckListList = fcl.rtnEditedChecklist(CheckListList);
                if (!CheckListList.contains(fcl.coldnoncold_item_02)) {
                    CheckListList.add(0, fcl.coldnoncold_item_02);
                }
                if (!CheckListList.contains(fcl.coldnoncold_item_01)) {
                    CheckListList.add(0, fcl.coldnoncold_item_01);
                }

                CheckListList = fcl.rtnEditedChecklistTempReader(CheckListList);

                currJobInfo.CheckList = CheckListList.toArray(new String[CheckListList.size()]);
                hasCheckList = true;
                jiAddedChecklist = true;
            } else if (hasCheckList && hasColdItem && currJobInfo.Pieces > 0 && currJobInfo.Pieces < 2) {
                fixedChecklists fcl = new fixedChecklists();
                CheckListList = new ArrayList<String>(Arrays.asList(currJobInfo.CheckList));
                CheckListList = fcl.rtnEditedChecklist(CheckListList);
                if (!CheckListList.contains(fcl.coldonly_item_02)) {
                    CheckListList.add(0, fcl.coldonly_item_02);
                }
                if (!CheckListList.contains(fcl.coldonly_item_01)) {
                    CheckListList.add(0, fcl.coldonly_item_01);
                }

                CheckListList = fcl.rtnEditedChecklistTempReader(CheckListList);

                currJobInfo.CheckList = CheckListList.toArray(new String[CheckListList.size()]);
                hasCheckList = true;
                jiAddedChecklist = true;
            } else if (!hasCheckList && isResidentialType && !cannotTakePhoto) {
                String[] cl = new String[]{myApp.getAppContext().getString(R.string.residential_photo_checklist_item)};
                currJobInfo.CheckList = cl;
                hasCheckList = true;
                jiAddedChecklist = true;
            }

            /*if (!hasCheckList && isResidentialType) {
                String[] cl = new String[]{myApp.getAppContext().getString(R.string.residential_photo_checklist_item)};
                currJobInfo.CheckList = cl;
                hasCheckList = true;
                jiAddedChecklist = true;
            }*/

            /*////test TempLogger ChklistItem
            String[] cl = new String[]{myApp.getAppContext().getString(R.string.tl_checklist_item)};
            currJobInfo.CheckList = cl;
            hasCheckList = true;
            jiAddedChecklist = true;
            ////test TempLogger ChklistItem*/

            //isSecurityBag = JobsHandler.reqSecurityTag(currJobInfo);
            /*if (isSecurityBag) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "SecuritySealNumber", JobsHandler.getSingleSecuritySealNumber(currJobInfo));
            }*/

            /*if (!hasCheckList && JobsHandler.CustIDVerificationRequired(currJobInfo)) {
                hasCheckList = true;
            }*/

            SignatureNameActivity.requireSignature = (currJobInfo.FLAG & 1024) == 1024 ? true : false;
            this.CanGetReviewLinkJobsIDX = (currJobInfo.FLAG & 16777216) == 16777216 ? currJobInfo.JobsIDX : null;

            if (hasSpecialCOD) {
                objGlobalMethods.writeToFile("JobsIDX: " + String.valueOf(currJobInfo.JobsIDX) + " has SpecialCOD.", "", "", "", "");
                String DOListArrJsonStr = "";
                ArrayList<objDOObject[]> DOListArrayList = new ArrayList<>();
                DOListArrayList.add(currJobInfo.DOList);
                DOListArrJsonStr = gson.toJson(DOListArrayList);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "DOListArrJsonStr", DOListArrJsonStr == "[]" ? "" : DOListArrJsonStr == "" ? "" : DOListArrJsonStr);
            }

            if (currJobInfo.CODCollected == null || !currJobInfo.CODCollected) {
                JobhasCOD = JobsHandler.checkForCODwithCatType(currJobInfo, catType);
            } else if (currJobInfo.CODCollected != null && currJobInfo.CODCollected) {
                //JobhasCOD = false;
                JobhasCOD = JobsHandler.checkForCODwithCatType(currJobInfo, catType);
            }
            JobhasCODStr = JobhasCOD ? "t" : "";
            P2JobhasCOD = JobsHandler.checkPart2ForCOD(currJobInfo, GcmPreferences);
            P2JobhasCODStr = P2JobhasCOD ? "t" : "";
            if (!JobhasCOD) {
                JobhasCOD = P2JobhasCOD;
                //JobhasCOD = JobsHandler.checkPart2ForCOD(currJobInfo, GcmPreferences);
            }

            if (JobhasCOD) {
                if (!GcmPreferences.getString("hfJobsInfoArrJsonStr", "").isEmpty()) {
                    String objCheckedItemsIDXJsonStr = "";
                    String[] CheckedItemsIDX = new String[]{String.valueOf(currJobInfo.JobsIDX)};
                    objCheckedItemsIDXJsonStr = gson.toJson(CheckedItemsIDX);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX, objCheckedItemsIDXJsonStr);
                }
            }
            objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", JobhasCOD ? "true" : "false");

            if ((hasOtp && OtpLocked) || hasVerifiedOTP || CustIDVerificationNotRequired) {
                if (hasCheckList && hasDOinv && hasSpecialCOD) {
                    //doinv > checklist > specialcod > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CODupdate.fromPage = "CheckListActivity";
                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CODupdate.class);
                    CheckListActivity.fromPage = "DOinvCheckActivity";
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                    DOinvCheckActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), DOinvCheckActivity.class);
                } else if (hasCheckList && hasDOinv && !hasSpecialCOD) {
                    //doinv > checklist > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CheckListActivity";
                        CheckListActivity.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CheckListActivity";
                        CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.fromPage = "DOinvCheckActivity";
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                    DOinvCheckActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), DOinvCheckActivity.class);
                } else if (hasCheckList && !hasDOinv && hasSpecialCOD) {
                    //checklist > specialcod > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CODupdate.fromPage = "CheckListActivity";
                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CODupdate.class);
                    CheckListActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                } else if (hasCheckList && !hasDOinv && !hasSpecialCOD) {
                    //checklist > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CheckListActivity";
                        CheckListActivity.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CheckListActivity";
                        CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                } else if (!hasCheckList && hasDOinv && hasSpecialCOD) {
                    //doinv > specialcod > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CODupdate.fromPage = "DOinvCheckActivity";
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CODupdate.class);
                    DOinvCheckActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), DOinvCheckActivity.class);
                } else if (!hasCheckList && hasDOinv && !hasSpecialCOD) {
                    //doinv > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "DOinvCheckActivity";
                        DOinvCheckActivity.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "DOinvCheckActivity";
                        DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    DOinvCheckActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), DOinvCheckActivity.class);
                } else if (!hasCheckList && !hasDOinv && hasSpecialCOD) {
                    //specialcod > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CODupdate.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), CODupdate.class);
                } else if (!hasCheckList && !hasDOinv && !hasSpecialCOD) {
                    //okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "JobInfoActivity";
                        parentIntent = new Intent(myApp.getAppContext(), ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "JobInfoActivity";
                        parentIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }
                }
            } else {
                if (hasOtp && hasCheckList && hasDOinv && hasSpecialCOD && !CustIDVerificationNotRequired) {
                    //otp > doinv > checklist > specialcod > okp >
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CODupdate.fromPage = "CheckListActivity";
                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CODupdate.class);
                    CheckListActivity.fromPage = "DOinvCheckActivity";
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                    DOinvCheckActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), DOinvCheckActivity.class);
                } else if (hasOtp && hasCheckList && hasDOinv && !hasSpecialCOD && !CustIDVerificationNotRequired) {
                    //otp > doinv > checklist > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CheckListActivity";
                        CheckListActivity.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CheckListActivity";
                        CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.fromPage = "DOinvCheckActivity";
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                    DOinvCheckActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), DOinvCheckActivity.class);
                } else if (hasOtp && hasCheckList && !hasDOinv && hasSpecialCOD && !CustIDVerificationNotRequired) {
                    //otp > checklist > specialcod > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CODupdate.fromPage = "CheckListActivity";
                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CODupdate.class);
                    CheckListActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                } else if (hasOtp && hasCheckList && !hasDOinv && !hasSpecialCOD && !CustIDVerificationNotRequired) {
                    //otp > checklist > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CheckListActivity";
                        CheckListActivity.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CheckListActivity";
                        CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                } else if (hasOtp && !hasCheckList && hasDOinv && hasSpecialCOD && !CustIDVerificationNotRequired) {
                    //otp > doinv  > specialcod > okp >
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CODupdate.fromPage = "DOinvCheckActivity";
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CODupdate.class);
                    DOinvCheckActivity.fromPage = "OtpActivity";
                    OtpActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), DOinvCheckActivity.class);
                    OtpActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), OtpActivity.class);
                } else if (hasOtp && !hasCheckList && hasDOinv && !hasSpecialCOD && !CustIDVerificationNotRequired) {
                    //otp > doinv  > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "DOinvCheckActivity";
                        DOinvCheckActivity.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "DOinvCheckActivity";
                        DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    DOinvCheckActivity.fromPage = "OtpActivity";
                    OtpActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), DOinvCheckActivity.class);
                    OtpActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), OtpActivity.class);
                } else if (hasOtp && !hasCheckList && !hasDOinv && hasSpecialCOD && !CustIDVerificationNotRequired) {
                    //otp > specialcod > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CODupdate.fromPage = "OtpActivity";
                    OtpActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CODupdate.class);
                    OtpActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), OtpActivity.class);
                } else if (hasOtp && !hasCheckList && !hasDOinv && !hasSpecialCOD && !CustIDVerificationNotRequired) {
                    //otp > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "OtpActivity";
                        OtpActivity.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "OtpActivity";
                        OtpActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    OtpActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), OtpActivity.class);
                } else if (!hasOtp && hasCheckList && hasDOinv && hasSpecialCOD) {
                    //doinv > checklist > specialcod > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CODupdate.fromPage = "CheckListActivity";
                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CODupdate.class);
                    CheckListActivity.fromPage = "DOinvCheckActivity";
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                    DOinvCheckActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), DOinvCheckActivity.class);
                } else if (!hasOtp && hasCheckList && hasDOinv && !hasSpecialCOD) {
                    //doinv > checklist > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CheckListActivity";
                        CheckListActivity.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CheckListActivity";
                        CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.fromPage = "DOinvCheckActivity";
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                    DOinvCheckActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), DOinvCheckActivity.class);
                } else if (!hasOtp && hasCheckList && !hasDOinv && hasSpecialCOD) {
                    //checklist > specialcod > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CODupdate.fromPage = "CheckListActivity";
                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CODupdate.class);
                    CheckListActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                } else if (!hasOtp && hasCheckList && !hasDOinv && !hasSpecialCOD) {
                    //checklist > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CheckListActivity";
                        CheckListActivity.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CheckListActivity";
                        CheckListActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CheckListActivity.ActionCode = "DS";
                    CheckListActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                } else if (!hasOtp && !hasCheckList && hasDOinv && hasSpecialCOD) {
                    //doinv > specialcod > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CODupdate.fromPage = "DOinvCheckActivity";
                    DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CODupdate.class);
                    DOinvCheckActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), DOinvCheckActivity.class);
                } else if (!hasOtp && !hasCheckList && hasDOinv && !hasSpecialCOD) {
                    //doinv > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "DOinvCheckActivity";
                        DOinvCheckActivity.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "DOinvCheckActivity";
                        DOinvCheckActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    DOinvCheckActivity.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), DOinvCheckActivity.class);
                } else if (!hasOtp && !hasCheckList && !hasDOinv && hasSpecialCOD) {
                    //specialcod > okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(ctx, ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "CODupdate";
                        CODupdate.nextActivtyIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }

                    CODupdate.fromPage = "JobInfoActivity";
                    parentIntent = new Intent(myApp.getAppContext(), CODupdate.class);
                } else if (!hasOtp && !hasCheckList && !hasDOinv && !hasSpecialCOD) {
                    //okp
                    if (isReqShipmentVerification) {
                        setDeliveredStatusActivity.fromPage = "ShpmtVActivity";
                        ShpmtVActivity.nextActivtyIntent = new Intent(ctx, setDeliveredStatusActivity.class);
                        ShpmtVActivity.fromPage = "JobInfoActivity";
                        parentIntent = new Intent(myApp.getAppContext(), ShpmtVActivity.class);
                    } else {
                        setDeliveredStatusActivity.fromPage = "JobInfoActivity";
                        parentIntent = new Intent(myApp.getAppContext(), setDeliveredStatusActivity.class);
                    }
                }
            }

            boolean isStandardRunJob = JobsHandler.isStandardRunJob(currJobInfo);
            boolean isXDelJob = JobsHandler.isXDelJob(currJobInfo);
            boolean isContactlessDelivery = JobsHandler.isContactlessDelivery(currJobInfo);
            Boolean isSecurityBag = JobsHandler.reqSecurityTag(currJobInfo);
            String ReceiverName = JobsHandler.getRecipientNameFromJob(currJobInfo);
            Integer TypeOfPinVerification = currJobInfo.getTypeOfPinVerification();
            String[] prefName = null;
            String[] prefvalue = null;
            //String DIPChecklistJsonStr = currJobInfo.CheckList != null && currJobInfo.CheckList.length > 0 ? gson.toJson(currJobInfo.CheckList) : "";

            String[] checklist = currJobInfo.CheckList != null && currJobInfo.CheckList.length > 0 ? JobsHandler2.getSpecificChecklist(currJobInfo.CheckList, "DS") : null;
            String DIPChecklistJsonStr = checklist != null && checklist.length > 0 ? gson.toJson(checklist) : "";

            /*if (jiAddedChecklist) {
                String hfJobInfoJsonStr = gson.toJson(currJobInfo);
                prefName = new String[]{"ReloadDueNFCscan", "isStandardRunJob",
                        "isResidential", "hfJobInfoJsonStr", "DIPChecklist",
                        "hasOtpStr", "hasVerifiedOTPStr", "CustIDVerificationNotRequiredStr", "isLockerJobStr",
                        "CurrJobInfoStatusCodeIDX", "currJobInfoFlag", "TypeOfPinVerificationStr",
                        "ReceiverName", "isContactlessDeliveryStr", "JobhasCODStr", "P2JobhasCODStr", "isSecurityBagStr"};
                prefvalue = new String[]{"", isStandardRunJob || isXDelJob ? "y" : "",
                        isResidentialType ? "y" : "", hfJobInfoJsonStr, DIPChecklistJsonStr,
                        hasOtp ? "t" : "", hasVerifiedOTP ? "t" : "", CustIDVerificationNotRequired ? "t" : "", isLockerJob ? "t" : "",
                        CurrJobInfoStatusCodeIDX, String.valueOf(currJobInfo.FLAG), String.valueOf(TypeOfPinVerification),
                        ReceiverName, isContactlessDelivery ? "t" : "", JobhasCODStr, P2JobhasCODStr, isSecurityBag ? "t" : ""};
            } else {
                prefName = new String[]{"ReloadDueNFCscan", "isStandardRunJob",
                        "isResidential", "DIPChecklist",
                        "hasOtpStr", "hasVerifiedOTPStr", "CustIDVerificationNotRequiredStr", "isLockerJobStr",
                        "CurrJobInfoStatusCodeIDX", "currJobInfoFlag", "TypeOfPinVerificationStr",
                        "ReceiverName", "isContactlessDeliveryStr", "JobhasCODStr", "P2JobhasCODStr", "isSecurityBagStr"};
                prefvalue = new String[]{"", isStandardRunJob || isXDelJob ? "y" : "",
                        isResidentialType ? "y" : "", DIPChecklistJsonStr,
                        hasOtp ? "t" : "", hasVerifiedOTP ? "t" : "", CustIDVerificationNotRequired ? "t" : "", isLockerJob ? "t" : "",
                        CurrJobInfoStatusCodeIDX, String.valueOf(currJobInfo.FLAG), String.valueOf(TypeOfPinVerification),
                        ReceiverName, isContactlessDelivery ? "t" : "", JobhasCODStr, P2JobhasCODStr, isSecurityBag ? "t" : ""};
            }*/

            /*cv.setHasOTP(hasOtp);
            cv.setHasVerifiedOTP(hasVerifiedOTP);
            cv.setCustIDVerificationNotRequired(CustIDVerificationNotRequired);
            cv.setLockerJob(isLockerJob);
            cv.setCurrJobInfoStatusCodeIDX(CurrJobInfoStatusCodeIDX);
            cv.setFLAG(currJobInfo.FLAG);
            cv.setTypeOfPinVerification(TypeOfPinVerification);
            cv.setJobsIDX(currJobInfo.JobsIDX);
            String hasIMEI_Verification = objGlobalMethods.getGCMPrefs(GcmPreferences, "hasIMEI_Verification");
            String hasSerialNum_Verification = objGlobalMethods.getGCMPrefs(GcmPreferences, "hasSerialNum_Verification");
            String hasVoucher_Verification = objGlobalMethods.getGCMPrefs(GcmPreferences, "hasVoucher_Verification");
            cv.sethasIMEI_Verification(!objGlobalMethods.IsEmptyString(hasIMEI_Verification));
            cv.sethasSerialNum_Verification(!objGlobalMethods.IsEmptyString(hasSerialNum_Verification));
            cv.sethasVoucher_Verification(!objGlobalMethods.IsEmptyString(hasVoucher_Verification));
            ChecklistVariablesJsonStr = gson.toJson(cv);*/

            if (jiAddedChecklist) {
                String hfJobInfoJsonStr = gson.toJson(currJobInfo);
                prefName = new String[]{"ReloadDueNFCscan", "isStandardRunJob",
                        "isResidential", "hfJobInfoJsonStr",
                        "hasOtpStr", "hasVerifiedOTPStr", "CustIDVerificationNotRequiredStr", "isLockerJobStr",
                        "CurrJobInfoStatusCodeIDX", "currJobInfoFlag", "TypeOfPinVerificationStr",
                        "ReceiverName", "isContactlessDeliveryStr", "JobhasCODStr", "P2JobhasCODStr", "isSecurityBagStr"};
                prefvalue = new String[]{"", isStandardRunJob || isXDelJob ? "y" : "",
                        isResidentialType ? "y" : "", hfJobInfoJsonStr,
                        hasOtp ? "t" : "", hasVerifiedOTP ? "t" : "", CustIDVerificationNotRequired ? "t" : "", isLockerJob ? "t" : "",
                        CurrJobInfoStatusCodeIDX, String.valueOf(currJobInfo.FLAG), String.valueOf(TypeOfPinVerification),
                        ReceiverName, isContactlessDelivery ? "t" : "", JobhasCODStr, P2JobhasCODStr, isSecurityBag ? "t" : ""};


            } else {
                prefName = new String[]{"ReloadDueNFCscan", "isStandardRunJob",
                        "isResidential",
                        "hasOtpStr", "hasVerifiedOTPStr", "CustIDVerificationNotRequiredStr", "isLockerJobStr",
                        "CurrJobInfoStatusCodeIDX", "currJobInfoFlag", "TypeOfPinVerificationStr",
                        "ReceiverName", "isContactlessDeliveryStr", "JobhasCODStr", "P2JobhasCODStr", "isSecurityBagStr"};
                prefvalue = new String[]{"", isStandardRunJob || isXDelJob ? "y" : "",
                        isResidentialType ? "y" : "",
                        hasOtp ? "t" : "", hasVerifiedOTP ? "t" : "", CustIDVerificationNotRequired ? "t" : "", isLockerJob ? "t" : "",
                        CurrJobInfoStatusCodeIDX, String.valueOf(currJobInfo.FLAG), String.valueOf(TypeOfPinVerification),
                        ReceiverName, isContactlessDelivery ? "t" : "", JobhasCODStr, P2JobhasCODStr, isSecurityBag ? "t" : ""};
            }
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
            objGlobalMethods.saveChecklistSP(DIPChecklistJsonStr);
            //objGlobalMethods.setChecklistRelatedSP_KeyValue("ChecklistVariablesJsonStr", ChecklistVariablesJsonStr);


            JobInfoActivity.accessFromDirection = 0;
            setFailStatusActivity.accessFromDirection = 0;
            setDeliveredStatusActivity.accessFromDirection = 1;
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            finish(); //newly inserted
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobInfoActivity updateOKP4. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //BackStepWithoutUpdate();
        try {
            //cc = 0;
            if (ongoingcall == null || !ongoingcall) {
                BackStepWithoutUpdateSQLite();
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_middleofcall));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity onBackPressed. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void unRegisterSensorListener() {
        try {
            if (mSensorManager != null) {
                mSensorManager.unregisterListener(this);
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at JobInfoActivity unRegisterSensorListener. Exception: " + s, "", "", "", "");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (ongoingcall == null || !ongoingcall) {
                unRegisterSensorListener();
            } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] < event.sensor.getMaximumRange()) {
                    pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    proximityWakeLock = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "myapp:JobInfoActivityCallWakeLock");
                    proximityWakeLock.acquire();
                } else {
                    if (proximityWakeLock != null && proximityWakeLock.isHeld()) {
                        proximityWakeLock.release();
                    }
                }
            }

        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void BackStepWithoutUpdateSQLite() {
        Intent parentIntent = null;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        int CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        try {
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            Camera5Activity.mCurrentPhotoPath = null;
            setFailStatusActivity.reasons = "";
            setFailStatusActivity.selectedStatusCodesIDX = "";
            setFailStatusActivity.ddlSelectedIndex = 0;
            setDeliveredStatusActivity.ddlSelectedIndex = 0;
            setDeliveredStatusActivity.ishuman = "0";
            linkedJob = null;
            Gson gson = new Gson();
            objJobInfo[] JobInfoAL;

            String[] prefName = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX,
                    "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr", "cameraScannedCode",
                    "objJobInfoBringtoUpdateCODListJsonStr", "mutlipleCODjobs"};
            String[] prefvalue = new String[]{"", "", "", "", "", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
            objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");

            objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, "");

            String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
            String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
            String fromSubMenu = objGlobalMethods.getGCMPrefs(GcmPreferences, "fromSubMenu");
            /*int TotalJobsCountPerCatTypePerPostal = !objGlobalMethods.IsDefaultVRPpage() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                    !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                            : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);*/
            int TotalJobsCountPerCatTypePerPostal = !isVrpView() ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                    !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                            : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);

            if (frompage != null && frompage.equals("Jobs_Retrieval_Activity")) {
                JobInfoActivity.frompage = null;

                String[] prefName1 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD", "isFromMessaging", "isFromDashboardSub"};
                String[] prefvalue1 = new String[]{"", "", "", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName1, prefvalue1);

                parentIntent = new Intent(myApp.currentActivity, Jobs_Retrieval_Activity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (!GcmPreferences.getString("isFromMainCOD", "").isEmpty()) {

                String[] prefName1 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD", "isFromMessaging", "isFromDashboardSub"};
                String[] prefvalue1 = new String[]{"", "", "", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName1, prefvalue1);

                parentIntent = new Intent(myApp.currentActivity, MainCODActivity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (!GcmPreferences.getString("isFromMessaging", "").isEmpty()) {

                String[] prefName1 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD", "isFromMessaging", "isFromDashboardSub"};
                String[] prefvalue1 = new String[]{"", "", "", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName1, prefvalue1);

                parentIntent = new Intent(myApp.currentActivity, Messaging.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (!GcmPreferences.getString("isFromDashboardSub", "").isEmpty()) {
                if (TotalJobsCountPerCatTypePerPostal > 1) {
                    parentIntent = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
                } else if (TotalJobsCountPerCatTypePerPostal == 1 && TotalJobsCountPerCatTypePerPostalB4Updates > 1) {
                    parentIntent = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
                } else if (TotalJobsCountPerCatTypePerPostal == 1) {
                    parentIntent = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
                }
                //else if (TotalJobsCountPerCatTypePerPostal == 0 || TotalJobsCountPerCatTypePerPostal == 1) {
                else if (TotalJobsCountPerCatTypePerPostal == 0) {
                    Integer TotalPIPDIPJobsCountPerPostal = !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalPIPDIPJobsCountPerPostalWithJobsIDXs(Postal, SelectedVRPJobsIDXs)
                            : DAO_Jobs.getTotalPIPDIPJobsCountPerPostal(Postal);

                    String[] prefName1 = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName, "isFromMainCOD", "isFromMessaging"};
                    String[] prefvalue1 = new String[]{"-1", "", "", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName1, prefvalue1);

                    if (TotalPIPDIPJobsCountPerPostal > 1) {
                        parentIntent = new Intent(myApp.currentActivity, DashboardSubActivity.class);
                    } else {

                        String[] prefName2 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromDashboardSub"};
                        String[] prefvalue2 = new String[]{"", "", "", "", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName2, prefvalue2);

                        if (DashboardSubActivity.frompage != null) {
                            parentIntent = new Intent(myApp.currentActivity, MapsAllActivity.class);
                        } else {
                            parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                        }
                    }
                    MainActivity.uponLogin = 0;
                }
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (TotalJobsCountPerCatTypePerPostal == 0) {

                String[] prefName2 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromMainCOD", "isFromMessaging", "isFromDashboardSub"};
                String[] prefvalue2 = new String[]{"", "", "", "", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName2, prefvalue2);

                int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
                if (TotalJobsCountPerCatType >= 1) {
                    parentIntent = new Intent(myApp.currentActivity, AddressListNewActivity.class);
                } else if (TotalJobsCountPerCatType < 1) {
                    parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                }
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (TotalJobsCountPerCatTypePerPostal == 1 && TotalJobsCountPerCatTypePerPostalB4Updates > 1) {

                String[] prefName2 = new String[]{"isFromMainCOD", "isFromMessaging"};
                String[] prefvalue2 = new String[]{"", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName2, prefvalue2);

                parentIntent = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (TotalJobsCountPerCatTypePerPostal == 1 && TotalJobsCountPerCatTypePerPostalB4Updates <= 1) {

                String[] prefName2 = new String[]{"isFromMainCOD", "isFromMessaging"};
                String[] prefvalue2 = new String[]{"", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName2, prefvalue2);

                int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);

                boolean IsDefaultVRPpage = isVrpView() && !IsMaintenanceHours;
                if (IsDefaultVRPpage && !fromSubMenu.isEmpty() && !fromSubMenu.equals("")) {
                    List<dbObjJobs> dbObjJobsListAL = DAO_Jobs.getAlldbObjJobsPerCategeory(CatTypeID);
                    if (dbObjJobsListAL != null && dbObjJobsListAL.size() > 1) {
                        parentIntent = new Intent(myApp.currentActivity, AddressListNewActivity.class);
                    } else if (dbObjJobsListAL != null && dbObjJobsListAL.size() == 1) {

                        String[] prefName3 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromDashboardSub"};
                        String[] prefvalue3 = new String[]{"", "", "", "", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName3, prefvalue3);

                        parentIntent = new Intent(myApp.currentActivity, DrawerMain2Activity.class);
                    }
                } else if (IsDefaultVRPpage) {
                    Integer TotalPIPDIPJobsCountPerPostal = DAO_Jobs.getTotalPIPDIPJobsCountPerPostal(Postal);

                    String[] prefName3 = new String[]{objGlobalVariables.categoryTypePrefName, objGlobalVariables.categoryTitlePrefName};
                    String[] prefvalue3 = new String[]{"-1", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName3, prefvalue3);

                    if (TotalPIPDIPJobsCountPerPostal > 1) {
                        parentIntent = new Intent(myApp.currentActivity, DashboardSubActivity.class);
                    } else {

                        String[] prefName4 = new String[]{"desPostal", "LAT", "LNG", "desAddress", "isFromDashboardSub"};
                        String[] prefvalue4 = new String[]{"", "", "", "", ""};
                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName4, prefvalue4);

                        if (DashboardSubActivity.frompage != null) {
                            parentIntent = new Intent(myApp.currentActivity, MapsAllActivity.class);
                        } else {
                            parentIntent = new Intent(myApp.currentActivity, !isVrpView() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                        }
                    }
                } else {

                    String[] prefName4 = new String[]{"desPostal", "LAT", "LNG", "desAddress"};
                    String[] prefvalue4 = new String[]{"", "", "", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName4, prefvalue4);

                    if (TotalJobsCountPerCatType > 1) {
                        parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? AddressListNewActivity.class : DrawerMain2Activity.class);
                    } else if (TotalJobsCountPerCatType <= 1) {
                        parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                    }
                }

                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (TotalJobsCountPerCatTypePerPostal > 1) {
                parentIntent = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at JobInfoActivity BackStepWithoutUpdateSQLite. Exception: " + s, "", "", "", "");
            int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
            if (TotalJobsCountPerCatType >= 1) {
                parentIntent = new Intent(myApp.currentActivity, AddressListNewActivity.class);
            } else if (TotalJobsCountPerCatType < 1) {
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            }
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        }
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public void scanCN(View view) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobInfoScanFrom", "scanCN");
        launchQRScan();
    }

    public void scanTag(View view) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobInfoScanFrom", "scanTag");
        launchQRScan();
    }

    public void updateCN(View view) {
        if (txtConNoteVal.getText().length() != 0 && txtConNoteVal.getText().toString() != "") {
            if (currJobInfo == null) {
                Gson gson = new Gson();
                currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            }

            String cnVal = txtConNoteVal.getText().toString();
            Boolean isSecurityTag = JobsHandler.validSecurityBagSealNumber(cnVal);
            if (isSecurityTag) {
                showDialog(myApp.getAppContext().getString(R.string.alert_thisissecuritytag));
            } else if (!objGlobalMethods.verifyBarcode(cnVal)) {
                if (currJobInfo == null) {
                    Gson gson = new Gson();
                    currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                }
                if (JobsHandler.isJobsIDXcode(cnVal)) {
                    if (JobsHandler.isMatchingJobsIDX(currJobInfo, cnVal)) {
                        updateCNValviaBarcode(false, true, currJobInfo);
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_pupromptwrongjx));
                    }
                } else if (JobsHandler.isSameAsJobNo(currJobInfo, cnVal)) {
                    showDialog(myApp.getAppContext().getString(R.string.alert_PUprompt0));
                } else {
                    updateCNValviaBarcode(false, false, currJobInfo);
                }
                //updateCNValviaBarcode(false);
            } else {
                //playTone();
                showDialog(myApp.getAppContext().getString(R.string.alert_thisistag));
            }
        } else {
            //playTone();
            showDialog(myApp.getAppContext().getString(R.string.alert_plsenterCN));
        }
    }

    public void updateCNValviaBarcode(final Boolean viaKeyboardGoBtn, final Boolean isJX, objJobInfo ji) {
        try {
            long jidx = 0;
            String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
            if (!JobsIDXstr.equals("")) {
                try {
                    jidx = Long.parseLong(JobsIDXstr);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception caught at JobInfoActivity updateCN convert JobsIDXstr to Long value.", "", "", "", "");
                    jidx = 0;
                }
            }

            final long JobsIDX = jidx;

            if (JobsIDX > 0) {
                if (!viaKeyboardGoBtn) {
                    objGlobalMethods.writeToFile("Attempt to update Consignment value via main button click. JobsIDX: " + String.valueOf(JobsIDX) + ", Consignment value: " + txtConNoteVal.getText().toString(), "", "", "", "");
                } else {
                    objGlobalMethods.writeToFile("Attempt to update Consignment value via keyboard GO button. JobsIDX: " + String.valueOf(JobsIDX) + ", Consignment value: " + txtConNoteVal.getText().toString(), "", "", "", "");
                }

                String fulldladdress = "";
                String dladdress = JobsHandler.getJobAddress(ji);
                if (!objGlobalMethods.IsEmptyString(dladdress)) {
                    fulldladdress = "\n\n" + myApp.getAppContext().getString(R.string.tobedeliveredto) + "\n\n" + dladdress.toUpperCase();
                }

                //AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                builder.setCancelable(false);
                builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdateCN) + fulldladdress);
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status
                        if (isJX) {
                            updateCNValToJobInfoJsonStrOnly(currJobInfo, txtConNoteVal.getText().toString().toUpperCase());
                        } else {
                            updateCNVal(txtConNoteVal.getText().toString().toUpperCase(), !viaKeyboardGoBtn ? 0 : 1);
                        }
                        //updateCNVal(txtConNoteVal.getText().toString().toUpperCase(), !viaKeyboardGoBtn ? 0 : 1);
                    }
                });
                builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_ErrorJobsIDX));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobInfoActivity updateCNValviaBarcode. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
        } finally {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    private void updateCNValToJobInfoJsonStrOnly(objJobInfo ji, String val) {
        try {
            Boolean succ = JobsHandler.processCNUpdateToJobInfoJsonOnly(ji, val);
            if (succ) {
                objGlobalMethods.writeToFile("JobInfoActivity updateCNValToJobInfoJsonStrOnly ended successfully.", "", "", "", "");
                showDialog(myApp.getAppContext().getString(R.string.alert_CNdatasubmitted));
            } else {
                objGlobalMethods.writeToFile("JobInfoActivity updateCNValToJobInfoJsonStrOnly ended with error.", "", "", "", "");
                showDialog(myApp.getAppContext().getString(R.string.alert_ErrorCN));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity updateCNValToJobInfoJsonStrOnly. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void updateCNVal(String val, int updatemode) {
        //update mode : 0 == !viaKeyboardGoBtn, 1 == viaKeyboardGoBtn, 2 == after prompt upon pressing PU button
        try {
            if (updatemode == 0) {
                objGlobalMethods.writeToFile("Update Consignment value via main button click. JobsIDX: " + String.valueOf(currJobInfo.JobsIDX) + ", Consignment value: " + cnno + ".", "", "", "", "");
            } else if (updatemode == 1) {
                objGlobalMethods.writeToFile("Update Consignment value via keyboard GO button. JobsIDX: " + String.valueOf(currJobInfo.JobsIDX) + ", Consignment value: " + cnno + ".", "", "", "", "");
            } else if (updatemode == 2) {
                objGlobalMethods.writeToFile("Update Consignment value upon pressing PU button. JobsIDX: " + String.valueOf(currJobInfo.JobsIDX) + ", Consignment value: " + cnno + ".", "", "", "", "");
            }

            if (currJobInfo == null) {
                Gson gson = new Gson();
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            }

            Boolean succ = JobsHandler.processCNUpdate(currJobInfo, val);

            if (updatemode != 2 && succ) {
                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            }

            if (succ) {
                objGlobalMethods.writeToFile("JobInfoActivity updateCNVal ended successfully.", "", "", "", "");
                if (updatemode == 0 || updatemode == 1) {
                    showDialog(myApp.getAppContext().getString(R.string.alert_CNdatasubmitted));
                }
            } else {
                objGlobalMethods.writeToFile("JobInfoActivity updateCNVal ended with error.", "", "", "", "");
                if (updatemode == 0 || updatemode == 1) {
                    showDialog(myApp.getAppContext().getString(R.string.alert_ErrorCN));
                }
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity updateCNVal. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    public void updateBarcode(View view) {
        if (currJobInfo == null) {
            Gson gson = new Gson();
            currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
        }
        Boolean isSecurityBag = JobsHandler.reqSecurityTag(currJobInfo);
        if (txtLabelTagVal.getText().length() != 0 && txtLabelTagVal.getText().toString() != "") {
            if (isSecurityBag) {
                updateBarcodeViaBarcode(false, currJobInfo);
                /*if (!JobsHandler.validSecurityBagSealNumber(txtLabelTagVal.getText().toString())) {
                    showDialog(myApp.getAppContext().getString(R.string.alert_InvalidSecuritySeal));
                } else {
                    updateBarcodeViaBarcode(false);
                }*/
            } else if (objGlobalMethods.verifyBarcode(txtLabelTagVal.getText().toString())) {
                updateBarcodeViaBarcode(false, currJobInfo);
            } else {
                //playTone();
                showDialog(myApp.getAppContext().getString(R.string.alert_InvalidBarcode));
            }
        } else {
            //playTone();
            showDialog(isSecurityBag ? myApp.getAppContext().getString(R.string.alert_plsenterSecuritySeal) : myApp.getAppContext().getString(R.string.alert_plsenterBarcode));
        }
    }

    public void updateBarcodeViaBarcode(final boolean viaKeyboardGoBtn, objJobInfo ji) {
        try {
            long jidx = 0;
            String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
            if (!JobsIDXstr.equals("")) {
                try {
                    jidx = Long.parseLong(JobsIDXstr);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception caught at JobInfoActivity updateBarcodeViaBarcode convert JobsIDXstr to Long value.", "", "", "", "");
                    jidx = 0;
                }
            }

            final long JobsIDX = jidx;

            if (JobsIDX > 0) {
                if (!viaKeyboardGoBtn) {
                    objGlobalMethods.writeToFile("Attempt to update BarcodeTag value via main button click. JobsIDX: " + String.valueOf(JobsIDX) + ", BarcodeTag value: " + txtLabelTagVal.getText().toString().toUpperCase(), "", "", "", "");
                } else {
                    objGlobalMethods.writeToFile("Attempt to update BarcodeTag value via keyboard GO button. JobsIDX: " + String.valueOf(JobsIDX) + ", BarcodeTag value: " + txtLabelTagVal.getText().toString().toUpperCase(), "", "", "", "");
                }
                Boolean isSecurityBag = JobsHandler.reqSecurityTag(currJobInfo);

                String fulldladdress = "";
                String dladdress = JobsHandler.getJobAddress(ji);
                if (!objGlobalMethods.IsEmptyString(dladdress)) {
                    fulldladdress = "\n\n" + myApp.getAppContext().getString(R.string.tobedeliveredto) + "\n\n" + dladdress.toUpperCase();
                }

                //AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                builder.setCancelable(false);
                builder.setMessage((isSecurityBag ? myApp.getAppContext().getString(R.string.alert_cfmupdateSecuritySeal) : myApp.getAppContext().getString(R.string.alert_cfmupdateBarcodeLabel)) + fulldladdress);
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status
                        updateBarcodeVal(txtLabelTagVal.getText().toString().toUpperCase(), !viaKeyboardGoBtn ? 0 : 1);
                        txtLabelTagVal.setText("");
                    }
                });
                builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_ErrorJobsIDX));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobInfoActivity updateBarcodeViaBarcode. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_ErrorBarcode));
        } finally {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    private void updateBarcodeVal(String val, int updatemode) {
        //update mode : 0 == !viaKeyboardGoBtn, 1 == viaKeyboardGoBtn, 2 == after prompt upon pressing PU button
        try {
            if (updatemode == 0) {
                objGlobalMethods.writeToFile("Update BarcodeTag value via main button click. JobsIDX: " + String.valueOf(currJobInfo.JobsIDX) + ", BarcodeTag value: " + val.toUpperCase() + ".", "", "", "", "");
            } else if (updatemode == 1) {
                objGlobalMethods.writeToFile("Update BarcodeTag value via keyboard GO button. JobsIDX: " + String.valueOf(currJobInfo.JobsIDX) + ", BarcodeTag value: " + val.toUpperCase() + ".", "", "", "", "");
            } else if (updatemode == 2) {
                objGlobalMethods.writeToFile("Update BarcodeTag value upon pressing PU button. JobsIDX: " + String.valueOf(currJobInfo.JobsIDX) + ", BarcodeTag value: " + val.toUpperCase() + ".", "", "", "", "");
            }

            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            if (currJobInfo == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                Gson gson = new Gson();
                currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            }

            Boolean succ = JobsHandler.processBarcodeTagUpdate(currJobInfo, val);

            if (updatemode != 2 && succ) {
                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            }

            if (succ) {
                if (currJobInfo.BarcodeTags != null && currJobInfo.BarcodeTags.length > 0) {
                    String barcodes = "";
                    for (int y = 0; y <= currJobInfo.BarcodeTags.length - 1; y++) {
                        barcodes += currJobInfo.BarcodeTags[y] + ", ";
                    }

                    if (updatemode == 0 || updatemode == 1) {
                        if (!barcodes.equals("")) {
                            lblbarcodeTagVal.setText(barcodes.substring(0, barcodes.length() - 2));
                            LLbarcodeTag.setVisibility(View.VISIBLE);
                        } else {
                            lblbarcodeTagVal.setText("");
                            LLbarcodeTag.setVisibility(View.GONE);
                        }
                    }
                } else if (updatemode == 0 || updatemode == 1) {
                    LLbarcodeTag.setVisibility(View.GONE);
                }
                objGlobalMethods.writeToFile("JobInfoActivity updateBarcodeVal ended successfully.", "", "", "", "");
                if (updatemode == 0 || updatemode == 1) {
                    showDialog(myApp.getAppContext().getString(R.string.alert_Taggingdatasubmitted));
                }
            } else {
                objGlobalMethods.writeToFile("JobInfoActivity updateBarcodeVal ended with error.", "", "", "", "");
                if (updatemode == 0 || updatemode == 1) {
                    showDialog(myApp.getAppContext().getString(R.string.alert_ErrorBarcode));
                }
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity updateBarcodeVal. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void UpdateJobStatus(final long AStatusCodeIDX, final String Remarks, String alertMsg, final boolean PromptDistanceViolationUponUpdate) {
        try {
            long jidx = 0;
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
            if (!JobsIDXstr.equals("")) {
                try {
                    jidx = Long.parseLong(JobsIDXstr);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception caught at JobInfoActivity updateCN convert JobsIDXstr to Long value.", "", "", "", "");
                    jidx = 0;
                }
            }

            final long JobsIDX = jidx;

            if (JobsIDX > 0) {
                //AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                builder.setCancelable(false);
                builder.setMessage(alertMsg);
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();

                        Date dt = new Date();
                        Long l = dt.getTime();
                        String timestampstr = "/Date(" + l + ")/";
                        Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                        int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                        String RegID = objGlobalMethods.getRegistrationIdOnly();
                        JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, JobsIDX, AStatusCodeIDX, timestampstr, "", "",
                                lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

                        objGlobalMethods.writeToFile("Click to update job status.", "JobsIDX: " + String.valueOf(JobsIDX), "Submitted StatusCodeIDX: " + String.valueOf(AStatusCodeIDX), "", "");


                        /*if (PromptDistanceViolationUponUpdate) {
                            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                            objGlobalMethods.sendLukeDVmsgEmail(currJobInfo, catType);
                        }*/

                        submitSQLite(JobsIDX, AStatusCodeIDX, GcmPreferences, UpdateJobInfoPreferences);

                    }
                });
                builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_ErrorJobsIDX));
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobInfoActivity UpdateJobStatus. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void UpdateJobStatusPU(Long JobsIDX) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
            int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
            String RegID = objGlobalMethods.getRegistrationIdOnly();
            JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, JobsIDX, 49, timestampstr, "", "",
                    lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

            objGlobalMethods.writeToFile("Click to update job status.", "JobsIDX: " + String.valueOf(JobsIDX), "Submitted StatusCodeIDX: " + String.valueOf(49), "", "");
            submitSQLite(JobsIDX, 49, GcmPreferences, UpdateJobInfoPreferences);
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at JobInfoActivity UpdateJobStatus. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void submitSQLite(long JobsIDX, long aaAStatusCodeIDX, SharedPreferences agcmp, SharedPreferences aupdatep) {
        try {
            //objGlobalMethods.writeToFile("JobInfoActivity submit started.", "", "", "", "");
            pd.show();
            Gson gson = new Gson();

            if (aaAStatusCodeIDX == 49) {
                DAO_Jobs.updateJobsCatTypeID(1, JobsIDX);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(agcmp, 0);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(agcmp, 1);
            } else if (aaAStatusCodeIDX == 104) {
                DAO_Jobs.updateJobAddressFromPUtoDIP(2, JobsIDX, GcmPreferences.getString("hfJobInfoJsonStr", ""));
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(agcmp, 1);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(agcmp, 2);
                currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                currJobInfo.setI_have_arrived_timestamp(0);
                String hfJobInfoJsonStr = gson.toJson(currJobInfo);
                hfJobInfoJsonStr = hfJobInfoJsonStr == "[]" || hfJobInfoJsonStr == "" ? "" : hfJobInfoJsonStr;
                objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", hfJobInfoJsonStr);
                DAO_Jobs.updateJobInfoPrefStr(JobsIDX, hfJobInfoJsonStr);
            }

            //objGlobalMethods.writeToFile("JobsHandler from JobInfoActivity submit started.", "", "", "", "");
            Context ctx = myApp.getAppContext();
            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

            objGlobalMethods.setLastUpdatedJobLatLng(GcmPreferences, (aaAStatusCodeIDX == 368 || aaAStatusCodeIDX == 369 || aaAStatusCodeIDX == 388) ? false : true);

            pd.dismiss();
            if (aaAStatusCodeIDX != 368 && aaAStatusCodeIDX != 369 && aaAStatusCodeIDX != 388) {
                BackStepWithoutUpdateSQLite();
            } else if (aaAStatusCodeIDX == 368 || aaAStatusCodeIDX == 369) {
                Date dt = new Date();
                long ms = dt.getTime();
                currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                currJobInfo.setI_have_arrived_timestamp(ms);
                String hfJobInfoJsonStr = gson.toJson(currJobInfo);
                hfJobInfoJsonStr = hfJobInfoJsonStr == "[]" || hfJobInfoJsonStr == "" ? "" : hfJobInfoJsonStr;
                objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", hfJobInfoJsonStr);
                DAO_Jobs.updateJobInfoPrefStr(JobsIDX, hfJobInfoJsonStr);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM h:mma");
                String arrivaltime = sdf.format(new Date(ms));
                lbliarrive.setText(myApp.getAppContext().getString(R.string.buttontext_arrivaltime) + " " + arrivaltime);
                handleIaRRBtn(currJobInfo);
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnsubmit);
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btndip);
                ctrlSettings.enableDisableFailButtonWithStyles(true, btnFail);

                ctrlSettings.enableDisableCommonButtonWithStyles(!currJobInfo.ConsignmentNote.equals("") || (aaAStatusCodeIDX == 369) ? false : true, btnScanCN);
                ctrlSettings.enableDisableCommonButtonWithStyles(!currJobInfo.ConsignmentNote.equals("") || (aaAStatusCodeIDX == 369) ? false : true, btnUpdateCN);
                ctrlSettings.enableDisableCommonButtonWithStyles(true, btnScanTag);
                ctrlSettings.enableDisableCommonButtonWithStyles(true, btnUpdateLabelTag);

                if (aaAStatusCodeIDX == 369) {
                    objTemp ot = temploggerclass.getLatestRecordedTempObjNew2(objGlobalMethods.getGcmPreferences(), currJobInfo.JobsIDX);
                    if (ot != null && objGlobalMethods.IsEmptyString(ot.TempLastRecorded)) {
                        if (ot.Device_Type == 22) {
                            promptToScanForBTlogger();
                        } else {
                            tle = new tempLoggerEntryDialog();
                            tle.setJobsIDX(currJobInfo.JobsIDX);
                            tle.setLaunchCFS(false);
                            tle.show(getSupportFragmentManager(), "templogger");
                        }
                    }
                }
            } else if (aaAStatusCodeIDX == 388) {
                //ctrlSettings.enableDisableFailButtonWithStyles(true && enableFailButtonAfter2calls(), btnFail);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at JobInfoActivity submitSQLite. Exception: " + e.toString(), "", "", "", "");
            //JobsHandler.backendRun(agcmp, aupdatep);
            pd.dismiss();
            if (aaAStatusCodeIDX != 368 && aaAStatusCodeIDX != 369 && aaAStatusCodeIDX != 388) {
                BackStepWithoutUpdateSQLite();
            }
        }
    }

    private void stopPlayingTone() {
        try {
            if (player != null) {
                player.reset();
                //player.prepare();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void toggle_contents(View v) {
        LLPUAddress.setVisibility(LLPUAddress.isShown()
                ? View.GONE
                : View.VISIBLE);

        int aacatType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        if (aacatType == 0 || aacatType == 1 || aacatType == 4) {
            //pip related
            if (LLPUAddress.getVisibility() == View.VISIBLE) {
                lblBtnPUFM.setText(myApp.getAppContext().getString(R.string.lblDELTO_hide));
            } else {
                lblBtnPUFM.setText(myApp.getAppContext().getString(R.string.lblDELTO));
            }
        } else {
            //dip related
            if (LLPUAddress.getVisibility() == View.VISIBLE) {
                lblBtnPUFM.setText(myApp.getAppContext().getString(R.string.lblPUFM_hide));
            } else {
                lblBtnPUFM.setText(myApp.getAppContext().getString(R.string.lblPUFM));
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // TODO: handle Intent
        /*Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if(tag != null) {
            new NdefReaderTask().execute(tag);
        }*/

        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            String action = intent.getAction();
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

                String type = intent.getType();
                if (MIME_TEXT_PLAIN.equals(type)) {

                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    new NdefReaderTask().execute(tag);

                } else {
                    //Log.d(TAG, "Wrong mime type: " + type);
                    //objGlobalMethods.writeToFile("", "", "", "", "");
                }
            } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

                // In case we would still use the Tech Discovered Intent
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String[] techList = tag.getTechList();
                String searchedTech = Ndef.class.getName();

                for (String tech : techList) {
                    if (searchedTech.equals(tech)) {
                        new NdefReaderTask().execute(tag);
                        break;
                    }
                }
            } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                String tagID = objGlobalMethods.ByteArrayToHexString(id).trim();
                tagID = tagID != null && tagID.length() > 0 && tagID.length() == 14 ? tagID : "";
                objGlobalMethods.writeToFile("NFC tag Scanned: " + tagID, "", "", "", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "NFCtagScannedVal", tagID != null && !tagID.isEmpty() ? tagID.toUpperCase() : "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "ReloadDueNFCscan", "true");
            }

        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity handleIntent. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        String s = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity NdefReaderTask doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    }
                }
            }
            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            /*
             * See NFC forum specification for "Text Record Type Definition" at 3.2.1
             *
             * http://www.nfc-forum.org/specs/
             *
             * bit_7 defines encoding
             * bit_6 reserved for future use, must be 0
             * bit_5..0 length of IANA language code
             */

            byte[] payload = record.getPayload();

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, ((payload[0] & 128) == 0) ? Charset.forName("UTF-8") : Charset.forName("UTF-16"));
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                showDialog(result);
                //mTextView.setText("Read content: " + result);
            }
        }
    }

    private void handleIaRRBtn(final objJobInfo ji) {
        try {
            if (ji.i_have_arrived_timestamp > 0) {
                long currTimeMS = new Date().getTime();
                long prevTimeMS = ji.i_have_arrived_timestamp;
                long leftTimeMS = 60000 - (currTimeMS - prevTimeMS);
                final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM h:mma");
                Date arrivaldatetime = new Date(prevTimeMS);
                final String arrivaltime = sdf.format(arrivaldatetime);
                cdTimer = new CountDownTimer(leftTimeMS, 1000) {

                    public void onTick(long millisUntilFinished) {
                        btnarr.setText(myApp.getAppContext().getString(R.string.buttontext_arr_sec_remaining) + " " + millisUntilFinished / 1000);
                        //btnarr.setText(myApp.getAppContext().getString(R.string.buttontext_arrivaltime) + " " + arrivaltime);
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnarr);
                        LLiArriveLbl.setVisibility(View.VISIBLE);
                    }

                    public void onFinish() {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String enableJobUpdatesPref = objGlobalMethods.getGCMPrefs(GcmPreferences, "enableJobUpdates");
                        boolean enableJobUpdates = enableJobUpdatesPref.equals("") || enableJobUpdatesPref.equals("true") ? true : false;
                        btnarr.setText(myApp.getAppContext().getString(R.string.buttontext_arr));
                        ctrlSettings.enableDisableSubmitButtonWithStyles(enableJobUpdates, btnarr);
                        if (cdTimer != null) {
                            cdTimer.cancel();
                            cdTimer = null;
                        }
                    }
                }.start();
            }
        } catch (Exception e) {

        }
    }

    private View.OnClickListener txtConNoteValOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(myApp.getAppContext().getString(R.string.alert_cannotupdateCN));
            }
        };
    }

    public void unmaskTel() {
        try {
            lblDELtelVal.setText(lblDELtelValHidden.getText().toString());
            lblDELothersVal.setText(lblDELothersValHidden.getText().toString());
            lblDELmobileVal.setText(lblDELmobileValHidden.getText().toString());
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity unmaskTel. Exception:" + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void unmaskTelInJobInfoActivity() {
        try {
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobInfoActivity.class && !JobInfoActivity.maskContactNum) {
                final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.unmaskTel();
                    }
                });
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity unmaskTelInJobInfoActivity. Exception:" + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void setContectNoOnClickForAllContactsLbl(boolean enabled) {
        boolean telBoo = false;
        boolean othersBoo = false;
        boolean mobileBoo = false;
        boolean telRBoo = false;
        boolean othersRBoo = false;
        boolean mobileRBoo = false;
        boolean hasmobileboo = false;
        boolean hasmobileRboo = false;
        int hasMutliValidSGNum = 0;
        String AllValidTelNums = "";
        List<String> Mainvalidtels = new ArrayList<>();
        //boolean hasMobileForOTP = false;

        try {
            lblDELtelVal.setOnClickListener(null);
            lblDELothersVal.setOnClickListener(null);
            lblDELmobileVal.setOnClickListener(null);

            lblDELtel.setOnClickListener(null);
            lblDELothers.setOnClickListener(null);
            lblDELmobile.setOnClickListener(null);

            lblDELRtelVal.setOnClickListener(null);
            lblDELRothersVal.setOnClickListener(null);
            lblDELRmobileVal.setOnClickListener(null);

            lblDELRtel.setOnClickListener(null);
            lblDELRothers.setOnClickListener(null);
            lblDELRmobile.setOnClickListener(null);

            if (!enabled) {
                lblDELtelVal.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                lblDELtel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                lblDELothersVal.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                lblDELothers.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                lblDELmobileVal.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                lblDELmobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                lblDELRtelVal.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                lblDELRtel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                lblDELRothersVal.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                lblDELRothers.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                lblDELRmobileVal.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                lblDELRmobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                return;
            }

            //hasMutliValidSGNum = objGlobalMethods.hasMultiValidSGTelNum(lblDELtelVal.getText().toString());
            hasMutliValidSGNum = objGlobalMethods.hasMultiValidSGTelNum(lblDELtelValHidden.getText().toString());

            telBoo = lblDELtelVal.getVisibility() == View.VISIBLE && hasMutliValidSGNum > 0;
            lblDELtelVal.setCompoundDrawablesWithIntrinsicBounds(telBoo || hasMutliValidSGNum > 0 ? R.mipmap.ic_action_call : 0, 0, 0, 0);
            /*lblDELtelVal.setOnClickListener(telBoo && hasMutliValidSGNum == 1 ? contactNoOnClick(objGlobalMethods.returnAllValidSGTelNum(lblDELtelVal.getText().toString(), true)[0]) :
                    hasMutliValidSGNum > 1 ? MultiCOntactNoOnClick(lblDELtelVal.getText().toString(), false) : null);*/
            lblDELtelVal.setOnClickListener(telBoo && hasMutliValidSGNum == 1 ? contactNoOnClick(objGlobalMethods.returnAllValidSGTelNum(lblDELtelValHidden.getText().toString(), true)[0]) :
                    hasMutliValidSGNum > 1 ? MultiCOntactNoOnClick(lblDELtelValHidden.getText().toString(), false) : null);

            if (telBoo) {
                //String[] validtels = objGlobalMethods.returnALlValidSGMobileNum(lblDELtelVal.getText().toString());
                String[] validtels = objGlobalMethods.returnALlValidSGMobileNum(lblDELtelValHidden.getText().toString());
                if (validtels != null && validtels.length > 0) {
                    lblDELtel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.whatsapp_icon, 0);
                } else {
                    lblDELtel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }


                if (validtels != null && validtels.length == 1) {
                    //lblDELtel.setOnClickListener(contactNoWhatsAppOnClick(objGlobalMethods.returnALlValidSGMobileNum(lblDELtelVal.getText().toString())[0]));
                    lblDELtel.setOnClickListener(contactNoWhatsAppOnClick(objGlobalMethods.returnALlValidSGMobileNum(lblDELtelValHidden.getText().toString())[0]));
                } else if (validtels != null && validtels.length > 1) {
                    //lblDELtel.setOnClickListener(MultiCOntactNoWhatsAppOnClick(lblDELtelVal.getText().toString()));
                    lblDELtel.setOnClickListener(MultiCOntactNoWhatsAppOnClick(lblDELtelValHidden.getText().toString()));
                } else {
                    lblDELtel.setOnClickListener(null);
                }
            } else {
                lblDELtel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                lblDELtel.setOnClickListener(null);
            }

            if (telBoo || hasMutliValidSGNum > 0) {
                //String[] validtels = objGlobalMethods.returnAllValidSGTelNum(lblDELtelVal.getText().toString(), true);
                String[] validtels = objGlobalMethods.returnAllValidSGTelNum(lblDELtelValHidden.getText().toString(), true);
                if (validtels != null && validtels.length > 0) {
                    for (int i = 0; i <= validtels.length - 1; i++) {
                        if (!Mainvalidtels.contains(validtels[i])) {
                            Mainvalidtels.add(validtels[i]);
                        }
                    }
                }
            }

            //hasMutliValidSGNum = objGlobalMethods.hasMultiValidSGTelNum(lblDELothersVal.getText().toString());
            hasMutliValidSGNum = objGlobalMethods.hasMultiValidSGTelNum(lblDELothersValHidden.getText().toString());

            othersBoo = lblDELothersVal.getVisibility() == View.VISIBLE && hasMutliValidSGNum > 0;
            lblDELothersVal.setCompoundDrawablesWithIntrinsicBounds(othersBoo || hasMutliValidSGNum > 0 ? R.mipmap.ic_action_call : 0, 0, 0, 0);
            /*lblDELothersVal.setOnClickListener(othersBoo && hasMutliValidSGNum == 1 ? contactNoOnClick(objGlobalMethods.returnAllValidSGTelNum(lblDELothersVal.getText().toString(), true)[0]) :
                    hasMutliValidSGNum > 1 ? MultiCOntactNoOnClick(lblDELothersVal.getText().toString(), false) : null);*/
            lblDELothersVal.setOnClickListener(othersBoo && hasMutliValidSGNum == 1 ? contactNoOnClick(objGlobalMethods.returnAllValidSGTelNum(lblDELothersValHidden.getText().toString(), true)[0]) :
                    hasMutliValidSGNum > 1 ? MultiCOntactNoOnClick(lblDELothersValHidden.getText().toString(), false) : null);

            if (othersBoo) {
                //String[] validothers = objGlobalMethods.returnALlValidSGMobileNum(lblDELothersVal.getText().toString());
                String[] validothers = objGlobalMethods.returnALlValidSGMobileNum(lblDELothersValHidden.getText().toString());
                if (validothers != null && validothers.length > 0) {
                    lblDELothers.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.whatsapp_icon, 0);
                } else {
                    lblDELothers.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }


                if (validothers != null && validothers.length == 1) {
                    //lblDELothers.setOnClickListener(contactNoWhatsAppOnClick(objGlobalMethods.returnALlValidSGMobileNum(lblDELothersVal.getText().toString())[0]));
                    lblDELothers.setOnClickListener(contactNoWhatsAppOnClick(objGlobalMethods.returnALlValidSGMobileNum(lblDELothersValHidden.getText().toString())[0]));
                } else if (validothers != null && validothers.length > 1) {
                    //lblDELothers.setOnClickListener(MultiCOntactNoWhatsAppOnClick(lblDELothersVal.getText().toString()));
                    lblDELothers.setOnClickListener(MultiCOntactNoWhatsAppOnClick(lblDELothersValHidden.getText().toString()));
                } else {
                    lblDELothers.setOnClickListener(null);
                }
            } else {
                lblDELothers.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                lblDELothers.setOnClickListener(null);
            }

            if (othersBoo || hasMutliValidSGNum > 0) {
                //String[] validtels = objGlobalMethods.returnAllValidSGTelNum(lblDELothersVal.getText().toString(), true);
                String[] validtels = objGlobalMethods.returnAllValidSGTelNum(lblDELothersValHidden.getText().toString(), true);
                if (validtels != null && validtels.length > 0) {
                    for (int i = 0; i <= validtels.length - 1; i++) {
                        if (!Mainvalidtels.contains(validtels[i])) {
                            Mainvalidtels.add(validtels[i]);
                        }
                    }
                }
            }

            //hasMutliValidSGNum = objGlobalMethods.hasMultiValidSGTelNum(lblDELmobileVal.getText().toString());
            hasMutliValidSGNum = objGlobalMethods.hasMultiValidSGTelNum(lblDELmobileValHidden.getText().toString());

            mobileBoo = lblDELmobileVal.getVisibility() == View.VISIBLE && hasMutliValidSGNum > 0;
            lblDELmobileVal.setCompoundDrawablesWithIntrinsicBounds(mobileBoo || hasMutliValidSGNum > 0 ? R.mipmap.ic_action_call : 0, 0, 0, 0);
            /*lblDELmobileVal.setOnClickListener(mobileBoo && hasMutliValidSGNum == 1 ? contactNoOnClick(objGlobalMethods.returnAllValidSGTelNum(lblDELmobileVal.getText().toString(), true)[0]) :
                    hasMutliValidSGNum > 1 ? MultiCOntactNoOnClick(lblDELmobileVal.getText().toString(), false) : null);*/
            lblDELmobileVal.setOnClickListener(mobileBoo && hasMutliValidSGNum == 1 ? contactNoOnClick(objGlobalMethods.returnAllValidSGTelNum(lblDELmobileValHidden.getText().toString(), true)[0]) :
                    hasMutliValidSGNum > 1 ? MultiCOntactNoOnClick(lblDELmobileValHidden.getText().toString(), false) : null);

            if (mobileBoo) {
                //String[] validmobiletels = objGlobalMethods.returnALlValidSGMobileNum(lblDELmobileVal.getText().toString());
                String[] validmobiletels = objGlobalMethods.returnALlValidSGMobileNum(lblDELmobileValHidden.getText().toString());
                if (validmobiletels != null && validmobiletels.length > 0) {
                    lblDELmobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.whatsapp_icon, 0);
                } else {
                    lblDELmobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }


                if (validmobiletels != null && validmobiletels.length == 1) {
                    //lblDELmobile.setOnClickListener(contactNoWhatsAppOnClick(objGlobalMethods.returnALlValidSGMobileNum(lblDELmobileVal.getText().toString())[0]));
                    lblDELmobile.setOnClickListener(contactNoWhatsAppOnClick(objGlobalMethods.returnALlValidSGMobileNum(lblDELmobileValHidden.getText().toString())[0]));
                } else if (validmobiletels != null && validmobiletels.length > 1) {
                    //lblDELmobile.setOnClickListener(MultiCOntactNoWhatsAppOnClick(lblDELmobileVal.getText().toString()));
                    lblDELmobile.setOnClickListener(MultiCOntactNoWhatsAppOnClick(lblDELmobileValHidden.getText().toString()));
                } else {
                    lblDELmobile.setOnClickListener(null);
                }
            } else {
                lblDELmobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                lblDELmobile.setOnClickListener(null);
            }

            if (mobileBoo || hasMutliValidSGNum > 0) {
                //String[] validtels = objGlobalMethods.returnAllValidSGTelNum(lblDELmobileVal.getText().toString(), true);
                String[] validtels = objGlobalMethods.returnAllValidSGTelNum(lblDELmobileValHidden.getText().toString(), true);
                if (validtels != null && validtels.length > 0) {
                    for (int i = 0; i <= validtels.length - 1; i++) {
                        if (!Mainvalidtels.contains(validtels[i])) {
                            Mainvalidtels.add(validtels[i]);
                        }
                    }
                }
            }

            hasMutliValidSGNum = objGlobalMethods.hasMultiValidSGTelNum(lblDELRtelVal.getText().toString());
            //telRBoo = lblDELRtelVal.getVisibility() == View.VISIBLE && objGlobalMethods.validSGTelNum(lblDELRtelVal.getText().toString());
            telRBoo = lblDELRtelVal.getVisibility() == View.VISIBLE && hasMutliValidSGNum > 0;
            lblDELRtelVal.setCompoundDrawablesWithIntrinsicBounds(telRBoo || hasMutliValidSGNum > 0 ? R.mipmap.ic_action_call : 0, 0, 0, 0);
            lblDELRtelVal.setOnClickListener(telRBoo && hasMutliValidSGNum == 1 ? contactNoOnClick(objGlobalMethods.returnAllValidSGTelNum(lblDELRtelVal.getText().toString(), true)[0]) :
                    hasMutliValidSGNum > 1 ? MultiCOntactNoOnClick(lblDELRtelVal.getText().toString(), false) : null);

            if (telRBoo) {
                String[] validRtels = objGlobalMethods.returnALlValidSGMobileNum(lblDELRtelVal.getText().toString());
                if (validRtels != null && validRtels.length > 0) {
                    lblDELRtel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.whatsapp_icon, 0);
                } else {
                    lblDELRtel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }


                if (validRtels != null && validRtels.length == 1) {
                    lblDELRtel.setOnClickListener(contactNoWhatsAppOnClick(objGlobalMethods.returnALlValidSGMobileNum(lblDELRtelVal.getText().toString())[0]));
                } else if (validRtels != null && validRtels.length > 1) {
                    lblDELRtel.setOnClickListener(MultiCOntactNoWhatsAppOnClick(lblDELRtelVal.getText().toString()));
                } else {
                    lblDELRtel.setOnClickListener(null);
                }
            } else {
                lblDELRtel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                lblDELRtel.setOnClickListener(null);
            }

            if (telRBoo || hasMutliValidSGNum > 0) {
                String[] validtels = objGlobalMethods.returnAllValidSGTelNum(lblDELRtelVal.getText().toString(), true);
                if (validtels != null && validtels.length > 0) {
                    for (int i = 0; i <= validtels.length - 1; i++) {
                        if (!Mainvalidtels.contains(validtels[i])) {
                            Mainvalidtels.add(validtels[i]);
                        }
                    }
                }
            }

            hasMutliValidSGNum = objGlobalMethods.hasMultiValidSGTelNum(lblDELRothersVal.getText().toString());
            //othersRBoo = lblDELRothersVal.getVisibility() == View.VISIBLE && objGlobalMethods.validSGTelNum(lblDELRothersVal.getText().toString());
            othersRBoo = lblDELRothersVal.getVisibility() == View.VISIBLE && hasMutliValidSGNum > 0;
            lblDELRothersVal.setCompoundDrawablesWithIntrinsicBounds(othersRBoo || hasMutliValidSGNum > 0 ? R.mipmap.ic_action_call : 0, 0, 0, 0);
            lblDELRothersVal.setOnClickListener(othersRBoo && hasMutliValidSGNum == 1 ? contactNoOnClick(objGlobalMethods.returnAllValidSGTelNum(lblDELRothersVal.getText().toString(), true)[0]) :
                    hasMutliValidSGNum > 1 ? MultiCOntactNoOnClick(lblDELRothersVal.getText().toString(), false) : null);

            if (othersRBoo) {
                String[] validRothers = objGlobalMethods.returnALlValidSGMobileNum(lblDELRothersVal.getText().toString());
                if (validRothers != null && validRothers.length > 0) {
                    lblDELRothers.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.whatsapp_icon, 0);
                } else {
                    lblDELRothers.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }


                if (validRothers != null && validRothers.length == 1) {
                    lblDELRothers.setOnClickListener(contactNoWhatsAppOnClick(objGlobalMethods.returnALlValidSGMobileNum(lblDELRothersVal.getText().toString())[0]));
                } else if (validRothers != null && validRothers.length > 1) {
                    lblDELRothers.setOnClickListener(MultiCOntactNoWhatsAppOnClick(lblDELRothersVal.getText().toString()));
                } else {
                    lblDELRothers.setOnClickListener(null);
                }
            } else {
                lblDELRothers.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                lblDELRothers.setOnClickListener(null);
            }

            if (othersRBoo || hasMutliValidSGNum > 0) {
                String[] validtels = objGlobalMethods.returnAllValidSGTelNum(lblDELRothersVal.getText().toString(), true);
                if (validtels != null && validtels.length > 0) {
                    for (int i = 0; i <= validtels.length - 1; i++) {
                        if (!Mainvalidtels.contains(validtels[i])) {
                            Mainvalidtels.add(validtels[i]);
                        }
                    }
                }
            }

            hasMutliValidSGNum = objGlobalMethods.hasMultiValidSGTelNum(lblDELRmobileVal.getText().toString());
            //mobileRBoo = lblDELRmobileVal.getVisibility() == View.VISIBLE && objGlobalMethods.validSGTelNum(lblDELRmobileVal.getText().toString());
            mobileRBoo = lblDELRmobileVal.getVisibility() == View.VISIBLE && hasMutliValidSGNum > 0;
            lblDELRmobileVal.setCompoundDrawablesWithIntrinsicBounds(mobileRBoo || hasMutliValidSGNum > 0 ? R.mipmap.ic_action_call : 0, 0, 0, 0);
            lblDELRmobileVal.setOnClickListener(mobileRBoo && hasMutliValidSGNum == 1 ? contactNoOnClick(lblDELRmobileVal.getText().toString()) :
                    hasMutliValidSGNum > 1 ? MultiCOntactNoOnClick(lblDELRmobileVal.getText().toString(), false) : null);

            if (mobileRBoo) {
                String[] validRmobile = objGlobalMethods.returnALlValidSGMobileNum(lblDELRmobileVal.getText().toString());
                if (validRmobile != null && validRmobile.length > 0) {
                    lblDELRmobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.whatsapp_icon, 0);
                } else {
                    lblDELRmobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }


                if (validRmobile != null && validRmobile.length == 1) {
                    lblDELRmobile.setOnClickListener(contactNoWhatsAppOnClick(objGlobalMethods.returnALlValidSGMobileNum(lblDELRmobileVal.getText().toString())[0]));
                } else if (validRmobile != null && validRmobile.length > 1) {
                    lblDELRmobile.setOnClickListener(MultiCOntactNoWhatsAppOnClick(lblDELRmobileVal.getText().toString()));
                } else {
                    lblDELRmobile.setOnClickListener(null);
                }
            } else {
                lblDELRmobile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                lblDELRmobile.setOnClickListener(null);
            }

            if (mobileRBoo || hasMutliValidSGNum > 0) {
                String[] validtels = objGlobalMethods.returnAllValidSGTelNum(lblDELRmobileVal.getText().toString(), true);
                if (validtels != null && validtels.length > 0) {
                    for (int i = 0; i <= validtels.length - 1; i++) {
                        if (!Mainvalidtels.contains(validtels[i])) {
                            Mainvalidtels.add(validtels[i]);
                        }
                    }
                }
            }

            if (Mainvalidtels != null && Mainvalidtels.size() > 0) {
                for (int i = 0; i <= Mainvalidtels.size() - 1; i++) {
                    AllValidTelNums += Mainvalidtels.get(i) + ",";
                    /*if (!hasMobileForOTP && objGlobalMethods.IsMobile(Mainvalidtels.get(i)))
                        hasMobileForOTP = true;*/
                }
            }

            if (!AllValidTelNums.isEmpty() && !AllValidTelNums.equals("") && AllValidTelNums.substring(AllValidTelNums.length() - 1).equals(",")) {
                AllValidTelNums = AllValidTelNums.substring(0, AllValidTelNums.length() - 1);
                if (!AllValidTelNums.isEmpty() && !AllValidTelNums.equals("")) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "AllValidTelNums", AllValidTelNums);
                    setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);

                    /*GcmPreferences = objGlobalMethods.getGcmPreferences();
                    String[] prefName = new String[]{"AllValidTelNums", "hasMobileForOTP"};
                    String[] prefvalue = new String[]{AllValidTelNums, hasMobileForOTP ? "t" : ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);*/
                }
            } else {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, "AllValidTelNums", "");

                /*GcmPreferences = objGlobalMethods.getGcmPreferences();
                String[] prefName = new String[]{"AllValidTelNums", "hasMobileForOTP"};
                String[] prefvalue = new String[]{"", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);*/
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity setContectNoOnClickForAllContactsLbl. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private View.OnClickListener callOfcOnClick(final String tel) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ongoingcall == null || !ongoingcall) {
                    if (!tel.isEmpty() && !tel.equals("") && tel != "") {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                        final View av = v;

                        try {
                            //ConnectionDetector cd = new ConnectionDetector(context);
                            cd = new ConnectionDetector(null);
                            if (!cd.isConnectingToInternet()) {
                                String timestampstr = objGlobalMethods.rtntimestampstr();
                                Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                                int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                                String RegID = objGlobalMethods.getRegistrationIdOnly();
                                String callLog = objGlobalMethods.composeCallLog("", "No network. Unable to call out to " + tel + ".");

                                catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                                try {
                                    TextView lbl = (TextView) v;
                                    lbl.setTextColor(getResources().getColor(R.color.black));
                                } catch (Exception e) {
                                    objGlobalMethods.writeToFile("Exception at JobInfoActivity callOfcOnClick change textcolor. Exception: " + e.getMessage().toString(), "", "", "", "");
                                }
                                Context ctx = myApp.getAppContext();
                                showDialog(ctx.getString(R.string.nonetworknocall));
                            } else {
                                try {
                                    Context ctx = myApp.getAppContext();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                    builder.setCancelable(false);
                                    builder.setMessage(ctx.getString(R.string.cfmcall) + " " + tel + "?");
                                    builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ConnectingVOIP t = new ConnectingVOIP("0", tel, av, true);
                                            t.execute();
                                        }
                                    });
                                    builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //if user select "No", just cancel this dialog and continue with app
                                            dialog.cancel();
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                } catch (Exception e) {
                                    objGlobalMethods.writeToFile("Exception at JobInfoActivity callOfcOnClick attempt to call. Exception: " + e.getMessage(), "", "", "", "");
                                    ConnectingVOIP t = new ConnectingVOIP("0", tel, av, true);
                                    t.execute();
                                }
                            }
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception at JobInfoActivity callOfcOnClick. Exception: " + e.getMessage().toString(), "", "", "", "");
                        }
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_middleofcall));
                    }
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_middleofcall));
                }
            }
        };
    }

    private void callout(final String tel, final TextView av) {
        try {
            if (ongoingcall == null || !ongoingcall) {
                if (!tel.isEmpty() && !tel.equals("") && tel != "") {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                    long jidx = 0;
                    String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                    if (!JobsIDXstr.equals("")) {
                        try {
                            jidx = Long.parseLong(JobsIDXstr);
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception caught at JobInfoActivity updateCN convert JobsIDXstr to Long value.", "", "", "", "");
                            jidx = 0;
                        }
                    }

                    final long JobsIDX = jidx;
                    if (JobsIDX > 0) {
                        //ConnectionDetector cd = new ConnectionDetector(context);
                        cd = new ConnectionDetector(null);
                        if (!cd.isConnectingToInternet()) {
                            String timestampstr = objGlobalMethods.rtntimestampstr();
                            Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                            int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                            String RegID = objGlobalMethods.getRegistrationIdOnly();
                            String callLog = objGlobalMethods.composeCallLog("", "No network. Unable to call out to " + tel + ".");
                            JobsHandler.addUpdateJobInfoCallLogsQueALPrefs(UpdateJobInfoPreferences, RegID, JobsIDX, 388, timestampstr, callLog, "",
                                    lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");
                            objGlobalMethods.writeToFile(callLog, "JobsIDX: " + String.valueOf(JobsIDX), "Submitted StatusCodeIDX: " + String.valueOf(388), "", "");
                            submitSQLite(JobsIDX, 388, GcmPreferences, UpdateJobInfoPreferences);
                            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                            DAO_Jobs.insertCallLog(tel, new Date().getTime(), catType, true, false);
                            String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                            if (!objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                                setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
                            } else {
                                setFailStatusActivity.CheckReqCallLogImg2(tel);
                            }
                            JobInfoActivity.maskContactNum = !JobsHandler2.canShowFullContactNumber(GcmPreferences);
                            if (!(new ConnectionDetector(null).isConnectingToInternet()))
                                JobInfoActivity.maskContactNum = false;
                            JobInfoActivity.maskContactNum = false; // forcefully added here until further notice (by boss)

                            JobsHandler.AddRemoveClearObjCallback(GcmPreferences, lblJobNoVal.getText().toString(), AllValidTelNums, false);
                            Context ctx = myApp.getAppContext();
                            showDialog(ctx.getString(R.string.nonetworknocall));
                        } else {
                            try {
                                Context ctx = myApp.getAppContext();
                                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                builder.setCancelable(false);
                                builder.setMessage(ctx.getString(R.string.cfmcall) + " " + tel + "?");
                                builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ConnectingVOIP t = new ConnectingVOIP(String.valueOf(JobsIDX), tel, av, false);
                                        t.execute();
                                    }
                                });
                                builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //if user select "No", just cancel this dialog and continue with app
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } catch (Exception e) {
                                objGlobalMethods.writeToFile("Exception at JobInfoActivity callout attempt to call. Exception: " + e.getMessage(), "", "", "", "");
                                ConnectingVOIP t = new ConnectingVOIP(String.valueOf(JobsIDX), tel, av, false);
                                t.execute();
                            }
                        }
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.unabletocall2));
                    }
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_middleofcall));
                }
            } else {
                showDialog(myApp.getAppContext().getString(R.string.alert_middleofcall));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity callout. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private View.OnClickListener contactNoWhatsAppOnClick(final String tel) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = myApp.getAppContext();
                if (ongoingcall == null || !ongoingcall) {
                    if (!objGlobalMethods.IsEmptyString(tel)) {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                        final View av = v;

                        try {
                            long jidx = 0;
                            String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                            if (!JobsIDXstr.equals("")) {
                                try {
                                    jidx = Long.parseLong(JobsIDXstr);
                                } catch (Exception e) {
                                    objGlobalMethods.writeToFile("Exception caught at JobInfoActivity contactNoWhatsAppOnClick convert JobsIDXstr to Long value.", "", "", "", "");
                                    jidx = 0;
                                }
                            }

                            final long JobsIDX = jidx;

                            if (JobsIDX > 0) {
                                //ConnectionDetector cd = new ConnectionDetector(context);
                                cd = new ConnectionDetector(null);
                                if (!cd.isConnectingToInternet()) {
                                    objGlobalMethods.writeToFile("Unable to whatsapp for JobsIDX " + JobsIDX + " due no internet connection.", "", "", "", "");
                                    showDialog(ctx.getString(R.string.toast_workingconnection));
                                } else {
                                    try {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                        builder.setCancelable(false);
                                        builder.setMessage(ctx.getString(R.string.cfmwhatsapp) + " " + tel + "?");
                                        builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                objGlobalMethods.writeToFile("Attempt to whatsapp for JobsIDX " + JobsIDX + " to " + tel, "", "", "", "");
                                                sendWhatsappMsg2(tel);
                                            }
                                        });
                                        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //if user select "No", just cancel this dialog and continue with app
                                                dialog.cancel();
                                            }
                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    } catch (Exception e) {
                                        objGlobalMethods.writeToFile("Exception at JobInfoActivity contactNoWhatsAppOnClick attempt to call. Exception: " + e.getMessage(), "", "", "", "");

                                    }
                                }
                            } else {
                                showDialog(myApp.getAppContext().getString(R.string.alert_operationnotallowed));
                            }
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception at JobInfoActivity contactNoWhatsAppOnClick. Exception: " + e.getMessage().toString(), "", "", "", "");
                        }
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_middleofcall));
                    }
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_middleofcall));
                }
            }
        };
    }

    private View.OnClickListener contactNoOnClick(final String tel) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = myApp.getAppContext();
                if (ongoingcall == null || !ongoingcall) {
                    if (!tel.isEmpty() && !tel.equals("") && tel != "") {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                        final View av = v;

                        try {
                            long jidx = 0;
                            String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                            if (!JobsIDXstr.equals("")) {
                                try {
                                    jidx = Long.parseLong(JobsIDXstr);
                                } catch (Exception e) {
                                    objGlobalMethods.writeToFile("Exception caught at JobInfoActivity contactNoOnClick convert JobsIDXstr to Long value.", "", "", "", "");
                                    jidx = 0;
                                }
                            }

                            final long JobsIDX = jidx;

                            if (JobsIDX > 0) {
                                //ConnectionDetector cd = new ConnectionDetector(context);
                                cd = new ConnectionDetector(null);
                                if (!cd.isConnectingToInternet()) {
                                    String timestampstr = objGlobalMethods.rtntimestampstr();
                                    Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                                    int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                                    String RegID = objGlobalMethods.getRegistrationIdOnly();
                                    String callLog = objGlobalMethods.composeCallLog("", "No network. Unable to call out to " + tel + ".");
                                    JobsHandler.addUpdateJobInfoCallLogsQueALPrefs(UpdateJobInfoPreferences, RegID, JobsIDX, 388, timestampstr, callLog, "",
                                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");
                                    objGlobalMethods.writeToFile(callLog, "JobsIDX: " + String.valueOf(JobsIDX), "Submitted StatusCodeIDX: " + String.valueOf(388), "", "");
                                    submitSQLite(JobsIDX, 388, GcmPreferences, UpdateJobInfoPreferences);
                                    catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                                    DAO_Jobs.insertCallLog(tel, new Date().getTime(), catType, true, false);
                                    String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                                    if (!objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                                        setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
                                    } else {
                                        setFailStatusActivity.CheckReqCallLogImg2(tel);
                                    }
                                    JobInfoActivity.maskContactNum = !JobsHandler2.canShowFullContactNumber(GcmPreferences);
                                    if (!(new ConnectionDetector(null).isConnectingToInternet()))
                                        JobInfoActivity.maskContactNum = false;
                                    JobInfoActivity.maskContactNum = false; // forcefully added here until further notice (by boss)
                                    try {
                                        TextView lbl = (TextView) v;
                                        lbl.setTextColor(getResources().getColor(R.color.black));
                                    } catch (Exception e) {
                                        String s = JobsHandler.getStackTraceString(e);
                                        objGlobalMethods.writeToFile("Exception at JobInfoActivity contactNoOnClick change textcolor. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                                    }

                                    showDialog(ctx.getString(R.string.nonetworknocall));
                                } else {
                                    /*CallStatusHashmapList = objGlobalMethods.getCallStatusHashmapList(GcmPreferences);
                                    if (CallStatusHashmapList.calllist.size() > 0 && CallStatusHashmapList.calllist.containsKey(tel)
                                            && CallStatusHashmapList.calllist.get(tel).isEngaged() && !CallStatusHashmapList.calllist.get(tel).canCallAgain()) {
                                        showDialog(ctx.getString(R.string.number_engaged1)
                                                + String.valueOf(CallStatusHashmapList.calllist.get(tel).secondsLeft())
                                                + " " + ctx.getString(R.string.seconds) + ".");
                                    } else {*/
                                    try {
                                        String maskedt = tel;
                                        if (!objGlobalMethods.IsEmptyString(tel) && tel.length() > 4 && (JobInfoActivity.maskContactNum != null && JobInfoActivity.maskContactNum)) {
                                            maskedt = JobsHandler2.rtnMaskedContactNumber(tel);
                                        }

                                        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                        builder.setCancelable(false);
                                        //builder.setMessage(ctx.getString(R.string.cfmcall) + " " + tel + "?");
                                        builder.setMessage(ctx.getString(R.string.cfmcall) + " " + maskedt + "?");
                                        builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //if user pressed "yes"
                                                /*objGlobalMethods.writeToFile("Attempt to call out to " + tel + ".", "JobsIDX: " + String.valueOf(JobsIDX), "Submitted StatusCodeIDX: " + String.valueOf(388), "", "");
                                                regVOIP("1888");
                                                String buddy_uri = "sip:" + tel + "@27.54.42.188:5060";
                                                MyCall call = new MyCall(account, -1);
                                                CallOpParam prm = new CallOpParam(true);
                                                try {
                                                    call.makeCall(buddy_uri, prm);
                                                } catch (Exception e) {
                                                    call.delete();
                                                    return;
                                                }

                                                currentCall = call;
                                                showCallActivity(String.valueOf(JobsIDX), tel, "1888");*/
                                                ConnectingVOIP t = new ConnectingVOIP(String.valueOf(JobsIDX), tel, av, false);
                                                t.execute();
                                            }
                                        });
                                        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //if user select "No", just cancel this dialog and continue with app
                                                dialog.cancel();
                                            }
                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                        /*TextView msgtv = (TextView) alert.findViewById(android.R.id.message);
                                        msgtv.setTextSize(20);*/
                                    } catch (Exception e) {
                                        String s = JobsHandler.getStackTraceString(e);
                                        objGlobalMethods.writeToFile("Exception at JobInfoActivity contactNoOnClick attempt to call. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                                        ConnectingVOIP t = new ConnectingVOIP(String.valueOf(JobsIDX), tel, av, false);
                                        t.execute();
                                    }
                                    //}
                                }
                            } else {
                                showDialog(myApp.getAppContext().getString(R.string.unabletocall2));
                            }
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception at JobInfoActivity contactNoOnClick. Exception: " + e.getMessage().toString(), "", "", "", "");
                        }
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_middleofcall));
                    }
                } else {
                    showDialog(myApp.getAppContext().getString(R.string.alert_middleofcall));
                }
            }
        };
    }

    private void VerifyShipmentPrompt(boolean showScanPinkLabelMsg) {
        try {
            final Context ctx = myApp.currentActivity;
            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            final View callView = getLayoutInflater().inflate(R.layout.custom_verify_dialog, null);
            builder.setView(callView);
            Button btnVerifyShipmentCancel = (Button) callView.findViewById(R.id.btnVerifyShipmentCancel);
            Button btnVerifyShipmentScan = (Button) callView.findViewById(R.id.btnVerifyShipmentScan);
            Button btnVerifyShipmentSubmitValue = (Button) callView.findViewById(R.id.btnVerifyShipmentSubmitValue);
            TextView lblverifyInstruction = (TextView) callView.findViewById(R.id.lblverifyInstruction);

            if (showScanPinkLabelMsg) {
                lblverifyInstruction.setText(ctx.getString(R.string.verificationtxt_cold));
            }

            btnVerifyShipmentCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        /*final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                if (getCurrentFocus() != null) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                                }
                            }
                        }, 100);*/
                        /*InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(myApp.currentActivity.getWindow().getDecorView().getRootView().getWindowToken(), 0);*/
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerified", "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerifiedCold", "");
                        //VerifyShipmentDialog.cancel();
                        VerifyShipmentDialog.dismiss();


                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity btnVerifyShipmentCancel.setOnClickListener. Exception: " + e.getMessage(), "", "", "", "");
                    }
                }
            });

            btnVerifyShipmentScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        VerifyShipmentDialog.cancel();
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobInfoScanFrom", "scanVerify");
                        launchQRScan();
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity btnVerifyShipmentScan.setOnClickListener. Exception: " + e.getMessage(), "", "", "", "");
                    }
                }
            });

            btnVerifyShipmentSubmitValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                if (getCurrentFocus() != null) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }
                            }
                        }, 500);
                        EditText txtVerifyShipmentSubmitValue = (EditText) callView.findViewById(R.id.txtVerifyShipmentSubmitValue);
                        String val = txtVerifyShipmentSubmitValue.getText().toString();
                        VerifyShipmentDialog.cancel();
                        if (!objGlobalMethods.IsEmptyString(val)) {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            Gson gson = new Gson();
                            currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                            if (currJobInfo != null) {
                                boolean verified = JobsHandler.isCorrectJobSelected(currJobInfo, val, false, 1);
                                /*objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerified", verified ? "y" : "");
                                showDialog(verified ? ctx.getString(R.string.verificationtxtyes) : ctx.getString(R.string.verificationtxtno)).show();*/

                                boolean hasColdItem = JobsHandler2.hasColdItem(currJobInfo);
                                if (currJobInfo.Pieces > 0 && currJobInfo.Pieces < 2) { // 1 item only
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerified", verified ? "y" : "");
                                    //showDialog(verified ? ctx.getString(R.string.verificationtxtyes) : ctx.getString(R.string.verificationtxtno)).show();
                                    if (verified) {
                                        btnPUOKPclick2(null);
                                    } else {
                                        showDialog(ctx.getString(R.string.verificationtxtno)).show();
                                    }
                                } else if (currJobInfo.Pieces > 0 && currJobInfo.Pieces >= 2 && !hasColdItem) {
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerified", verified ? "y" : "");
                                    //showDialog(verified ? ctx.getString(R.string.verificationtxtyes) : ctx.getString(R.string.verificationtxtno)).show();
                                    if (verified) {
                                        btnPUOKPclick2(null);
                                    } else {
                                        showDialog(ctx.getString(R.string.verificationtxtno)).show();
                                    }
                                } else if (currJobInfo.Pieces > 0 && currJobInfo.Pieces >= 2 && hasColdItem) {
                                    /*if (JobsHandler2.isJC(val)) {
                                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerifiedCold", verified ? "y" : "");
                                    } else {
                                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerified", verified ? "y" : "");
                                    }*/
                                    boolean isJC = false;
                                    boolean isJX = false;
                                    if (JobsHandler2.isJC(val)) {
                                        isJC = true;
                                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerifiedCold", verified ? "y" : "");
                                    } else if (JobsHandler.isJobsIDXcode(val)) {
                                        isJX = true;
                                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerified", verified ? "y" : "");
                                    }

                                    if (JobsHandler2.ColdNonColdVerified(GcmPreferences)) {
                                        if (verified) {
                                            btnPUOKPclick2(null);
                                        } else {
                                            showDialog(ctx.getString(R.string.verificationtxtno)).show();
                                        }
                                    } else if (!isJC && !isJX && verified) { //as gd as not verified
                                        showDialogToRepromptShipmentVerification(ctx.getString(R.string.scan_pink_label), true);
                                    } else if ((isJC || isJX) && verified) {
                                        showDialogToRepromptShipmentVerification(isJC ? ctx.getString(R.string.scannext_nml) : ctx.getString(R.string.scannext_cold), true);
                                    } else {
                                        showDialog(ctx.getString(R.string.verificationtxtno)).show();
                                    }
                                }
                            } else {
                                showDialog(ctx.getString(R.string.alert_errorperformoperation)).show();
                            }
                        } else {
                            showDialog(ctx.getString(R.string.verificationtxtenterval)).show();
                        }

                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity btnVerifyShipmentSubmitValue.setOnClickListener. Exception: " + e.getMessage(), "", "", "", "");
                    }
                }
            });

            builder.setCancelable(true);
            VerifyShipmentDialog = builder.create();
            VerifyShipmentDialog.show();

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity VerifyShipmentPrompt. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void VerifyShipmentPromptNew(final int pcs, final boolean isAlps, final String instr) {
        try {
            final Context ctx = myApp.currentActivity;
            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            final View callView = getLayoutInflater().inflate(R.layout.custom_verify_dialog, null);
            builder.setView(callView);
            Button btnVerifyShipmentCancel = (Button) callView.findViewById(R.id.btnVerifyShipmentCancel);
            Button btnVerifyShipmentScan = (Button) callView.findViewById(R.id.btnVerifyShipmentScan);
            Button btnVerifyShipmentSubmitValue = (Button) callView.findViewById(R.id.btnVerifyShipmentSubmitValue);
            final TextView lblscanned = (TextView) callView.findViewById(R.id.lblscanned);
            TextView lblTotal = (TextView) callView.findViewById(R.id.lblTotal);
            lblTotal.setText(String.valueOf(pcs));
            lblscanned.setText(verifyItems != null ? String.valueOf(verifyItems.size()) : "0");
            LinearLayout LLpieces = (LinearLayout) callView.findViewById(R.id.LLpieces);
            LLpieces.setVisibility(isAlps ? View.VISIBLE : View.GONE);
            TextView lblverifyInstruction = (TextView) callView.findViewById(R.id.lblverifyInstruction);

            if (isAlps) {

                lblverifyInstruction.setText(instr);

                /*lblverifyInstruction.setText((whitepinkflag & 1) == 1 ?
                        ctx.getString(R.string.scan_pink_label_nc) :
                        (whitepinkflag & 2) == 2 ? ctx.getString(R.string.scan_pink_label_c) :
                                (whitepinkflag & 4) == 4 ? ctx.getString(R.string.scan_pink_label) :
                                ctx.getString(R.string.scan_white_or_pink_label));*/
            }


            btnVerifyShipmentCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        /*final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                if (getCurrentFocus() != null) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                                }
                            }
                        }, 100);*/
                        /*InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(myApp.currentActivity.getWindow().getDecorView().getRootView().getWindowToken(), 0);*/
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerified", "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "ShipmentVerifiedCold", "");
                        //VerifyShipmentDialog.cancel();
                        VerifyShipmentDialog.dismiss();


                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity btnVerifyShipmentCancel.setOnClickListener. Exception: " + e.getMessage(), "", "", "", "");
                    }
                }
            });

            btnVerifyShipmentScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        VerifyShipmentDialog.cancel();
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "JobInfoScanFrom", "scanVerify");
                        launchQRScan();
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity btnVerifyShipmentScan.setOnClickListener. Exception: " + e.getMessage(), "", "", "", "");
                    }
                }
            });

            btnVerifyShipmentSubmitValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                if (getCurrentFocus() != null) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }
                            }
                        }, 500);
                        EditText txtVerifyShipmentSubmitValue = (EditText) callView.findViewById(R.id.txtVerifyShipmentSubmitValue);
                        String val = txtVerifyShipmentSubmitValue.getText().toString();
                        VerifyShipmentDialog.cancel();
                        if (!objGlobalMethods.IsEmptyString(val)) {
                            if (verifyItems == null)
                                verifyItems = new ArrayList<>();
                            int scannedpcs = verifyItems != null ? verifyItems.size() : 0;
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            Gson gson = new Gson();
                            currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                            if (currJobInfo != null) {
                                boolean verified = JobsHandler.isCorrectJobSelected(currJobInfo, val, false, 1);
                                boolean hasColdItem = JobsHandler2.hasColdItem(currJobInfo);
                                int jccount = JobsHandler2.jcCount(verifyItems);
                                int jxcount = JobsHandler2.jxCount(verifyItems);
                                boolean isJC = JobsHandler2.isJC(val);
                                boolean isJX = JobsHandler.isJobsIDXcode(val);

                                if (verified) {
                                    if (isAlps && hasColdItem && !isJC && !isJX) {
                                        showDialogToRepromptShipmentVerificationNew(
                                                ((hasColdItem && pcs > 1) || (hasColdItem && pcs == 1)) ? ctx.getString(R.string.scan_pink_label) :
                                                        ctx.getString(R.string.scan_white_label),
                                                pcs, verifyItems.size(), isAlps,
                                                ((hasColdItem && pcs > 1) || (hasColdItem && pcs == 1)) ? ctx.getString(R.string.scan_pink_label) :
                                                        ctx.getString(R.string.scan_white_label));
                                    } else if (pcs == 1 && isAlps && hasColdItem && isJC) {
                                        verifyItems.add(isJC ? "jc" : "jx");
                                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                                        btnPUOKPclickNew(null);
                                    } else if (pcs == 1 && isAlps && hasColdItem && !isJC) {
                                        //scan cold pink
                                        showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_c), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_c));
                                    } else if (pcs == 1 && isAlps && !hasColdItem) {
                                        verifyItems.add(isJC ? "jc" : isJX ? "jx" : val);
                                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                                        btnPUOKPclickNew(null);
                                    } else if (pcs == 1 && !isAlps) {
                                        verifyItems.add(val);
                                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                                        btnPUOKPclickNew(null);
                                    } else if (pcs > 1) {
                                        if ((scannedpcs < pcs) && ((scannedpcs + 1) == pcs)) {
                                            if (isAlps && hasColdItem && jccount == 0 && !isJC) {
                                                //scan cold pink
                                                showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_c), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_c));
                                            } else if (isAlps && hasColdItem && jccount > 0 && !isJX) {
                                                //scan non-cold pink
                                                showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_nc), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_nc));
                                            } else {
                                                verifyItems.add(isJC ? "jc" : isJX ? "jx" : val);
                                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                                                btnPUOKPclickNew(null);
                                            }
                                        } else if (scannedpcs < pcs) {
                                            if (jccount == 0 && jxcount == 0) {
                                                verifyItems.add(isJC ? "jc" : "jx");
                                                showDialogToRepromptShipmentVerificationNew(ctx.getString(
                                                        isJC && hasColdItem ? R.string.scan_pink_label_nc :
                                                                isJX && hasColdItem && jccount == 0 ? R.string.scan_pink_label_cnc :
                                                                        isJX && hasColdItem ? R.string.scan_pink_label_nc :
                                                                                R.string.scan_white_label),
                                                        pcs, verifyItems.size(), isAlps,
                                                        ctx.getString(
                                                                isJC && hasColdItem ? R.string.scan_pink_label_nc :
                                                                        isJX && hasColdItem && jccount == 0 ? R.string.scan_pink_label_cnc :
                                                                                isJX && hasColdItem ? R.string.scan_pink_label_nc :
                                                                                        R.string.scan_white_label));
                                            } else if (isAlps && hasColdItem && jccount == 0 && !isJC) {
                                                verifyItems.add(isJC ? "jc" : "jx");
                                                showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_c), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_c));
                                            } else if (isAlps && hasColdItem && jccount == 0 && isJC) {
                                                verifyItems.add(isJC ? "jc" : "jx");
                                                showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_nc), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_nc));
                                            } else if (isAlps && hasColdItem && jccount > 0 && isJC) {
                                                showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_nc), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_nc));
                                            } else if (isAlps && hasColdItem && jccount > 0 && isJX) {
                                                verifyItems.add(isJC ? "jc" : "jx");
                                                showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_pink_label_nc), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_pink_label_nc));
                                            } else {
                                                verifyItems.add(isJC ? "jc" : isJX ? "jx" : val);
                                                showDialogToRepromptShipmentVerificationNew(ctx.getString(R.string.scan_white_label), pcs, verifyItems.size(), isAlps, ctx.getString(R.string.scan_white_label));
                                            }
                                        }
                                    }
                                } else {
                                    if (isAlps && hasColdItem) {
                                        showDialogToRepromptShipmentVerificationNew(
                                                ctx.getString(R.string.verificationtxtno) + "\n\n" + ctx.getString(R.string.scan_pink_label), pcs, verifyItems.size(), isAlps,
                                                ctx.getString(R.string.scan_pink_label));
                                    } else if (isAlps && !hasColdItem) {
                                        showDialogToRepromptShipmentVerificationNew(
                                                ctx.getString(R.string.verificationtxtno) + "\n\n" + ctx.getString(R.string.scan_white_label), pcs, verifyItems.size(), isAlps,
                                                ctx.getString(R.string.scan_white_label));
                                    } else {
                                        showDialog(ctx.getString(R.string.verificationtxtno)).show();
                                    }
                                }

                                /*if (verified) {
                                    if (verifyItems == null)
                                        verifyItems = new ArrayList<>();
                                    boolean isJC = JobsHandler2.isJC(val);
                                    boolean isJX = JobsHandler.isJobsIDXcode(val);

                                    if (isAlps && !isJC && !isJX) {
                                        showDialogToRepromptShipmentVerificationNew(((hasColdItem && pcs > 1) || (hasColdItem && pcs == 1)) ? ctx.getString(R.string.scan_pink_label) :
                                                                ctx.getString(R.string.scan_white_label),
                                                pcs, isAlps, ((hasColdItem && pcs > 1) || (hasColdItem && pcs == 1)) ? ctx.getString(R.string.scan_pink_label) :
                                                        ctx.getString(R.string.scan_white_label));
                                    } else if (pcs == 1 && isAlps && (isJC || isJX)) {
                                        verifyItems.add(isJC ? "jc" : "jx");
                                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                                        btnPUOKPclickNew(null);
                                    } else if (pcs == 1 && !isAlps) {
                                        verifyItems.add(isJC ? "jc" : "jx");
                                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                                        btnPUOKPclickNew(null);
                                    } else if (pcs > 1) {
                                        if ((scannedpcs < pcs) && ((scannedpcs + 1) == pcs)) {
                                            if (isAlps && hasColdItem && jccount == 0 && !isJC) {
                                                //scan cold pink
                                                showDialogToRepromptShipmentVerificationNew( ctx.getString(R.string.scan_pink_label_c), pcs, isAlps, ctx.getString(R.string.scan_pink_label_c));
                                            } else if (isAlps && hasColdItem && jccount > 0 && !isJX){
                                                //scan non-cold pink
                                                showDialogToRepromptShipmentVerificationNew( ctx.getString(R.string.scan_pink_label_nc), pcs, isAlps, ctx.getString(R.string.scan_pink_label_nc));
                                            } else {
                                                verifyItems.add(isJC ? "jc" : "jx");
                                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                                                btnPUOKPclickNew(null);
                                            }
                                        } else if (scannedpcs < pcs) {
                                            if (jccount == 0 && jxcount == 0) {
                                                verifyItems.add(isJC ? "jc" : "jx");
                                                showDialogToRepromptShipmentVerificationNew(ctx.getString(
                                                        isJC && hasColdItem ? R.string.scan_pink_label_nc :
                                                                isJX && hasColdItem && jccount == 0 ? R.string.scan_pink_label_cnc :
                                                                        isJX && hasColdItem ? R.string.scan_pink_label_nc :
                                                                                R.string.scan_white_label),
                                                        pcs, isAlps, ctx.getString(
                                                                isJC && hasColdItem ? R.string.scan_pink_label_nc :
                                                                        isJX && hasColdItem && jccount == 0 ? R.string.scan_pink_label_cnc :
                                                                                isJX && hasColdItem ? R.string.scan_pink_label_nc :
                                                                                        R.string.scan_white_label));
                                            } else if (isAlps && hasColdItem && jccount == 0 && !isJC) {
                                                verifyItems.add(isJC ? "jc" : "jx");
                                                showDialogToRepromptShipmentVerificationNew( ctx.getString(R.string.scan_pink_label_c), pcs, isAlps, ctx.getString(R.string.scan_pink_label_c));
                                            } else if (isAlps && hasColdItem && jccount > 0 && isJC) {
                                                showDialogToRepromptShipmentVerificationNew( ctx.getString(R.string.scan_pink_label_nc), pcs, isAlps, ctx.getString(R.string.scan_pink_label_nc));
                                            } else {
                                                verifyItems.add(isJC ? "jc" : "jx");
                                                showDialogToRepromptShipmentVerificationNew( ctx.getString(R.string.scan_white_label), pcs, isAlps, ctx.getString(R.string.scan_white_label));
                                            }
                                        }
                                    }

                                } else {
                                    showDialog(ctx.getString(R.string.verificationtxtno)).show();
                                }*/
                            } else {
                                showDialog(ctx.getString(R.string.alert_errorperformoperation)).show();
                            }
                        } else {
                            showDialog(ctx.getString(R.string.verificationtxtenterval)).show();
                        }

                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity btnVerifyShipmentSubmitValue.setOnClickListener. Exception: " + e.getMessage(), "", "", "", "");
                    }
                }
            });

            builder.setCancelable(true);
            VerifyShipmentDialog = builder.create();
            VerifyShipmentDialog.show();

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity VerifyShipmentPromptNew. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private View.OnClickListener MultiCOntactNoWhatsAppOnClick(final String tels) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int tvAddedCount = 0;
                    Context ctx = myApp.getAppContext();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                    builder.setIcon(R.mipmap.whatsapp_icon);
                    builder.setTitle(ctx.getString(R.string.chooseNumToWhatsApp));
                    final View callView = getLayoutInflater().inflate(R.layout.custom_callout_dialog, null);
                    builder.setView(callView);
                    Button btnCallCancel = (Button) callView.findViewById(R.id.btnCallCancel);
                    btnCallCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                MultiCallalertDialog.cancel();
                            } catch (Exception e) {

                            }
                        }
                    });

                    String[] validtels = objGlobalMethods.returnALlValidSGMobileNum(tels);
                    LinearLayout LLnumbers = (LinearLayout) callView.findViewById(R.id.LLnumbers);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 100);
                    if (LLnumbers != null && validtels != null) {
                        for (int i = 0; i <= validtels.length - 1; i++) {
                            TextView tv = new TextView(ctx);
                            tv.setText(validtels[i]);
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                            tv.setTextColor(Color.BLACK);
                            tv.setTypeface(null, Typeface.BOLD);
                            tv.setLayoutParams(params);
                            tv.setOnClickListener(contactNoWhatsAppOnClick(tv.getText().toString()));
                            LLnumbers.addView(tv);
                            tvAddedCount += 1;
                        }

                        if (tvAddedCount > 0) {
                            builder.setCancelable(true);
                            MultiCallalertDialog = builder.create();
                            MultiCallalertDialog.show();
                        }
                    }


                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at JobInfoActivity MultiCOntactNoWhatsAppOnClick. Exception: " + e.getMessage(), "", "", "", "");
                }
            }
        };
    }

    private View.OnClickListener MultiCOntactNoOnClick(final String tels, final boolean callOfcExtension) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    int tvAddedCount = 0;
                    Context ctx = myApp.getAppContext();

                    final AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                    builder.setIcon(R.mipmap.ic_action_call);
                    builder.setTitle(ctx.getString(R.string.chooseNumToCall));
                    final View callView = getLayoutInflater().inflate(R.layout.custom_callout_dialog, null);
                    builder.setView(callView);
                    Button btnCallCancel = (Button) callView.findViewById(R.id.btnCallCancel);
                    btnCallCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                MultiCallalertDialog.cancel();
                            } catch (Exception e) {

                            }
                        }
                    });

                    String[] validtels = objGlobalMethods.returnAllValidSGTelNum(tels, true);
                    LinearLayout LLnumbers = (LinearLayout) callView.findViewById(R.id.LLnumbers);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 100);
                    if (LLnumbers != null && validtels != null) {
                        String t = "";
                        String maskedt = "";
                        String validt = "";
                        for (int i = 0; i <= validtels.length - 1; i++) {
                            //new codes
                            t = validtels[i];
                            validt = t;
                            maskedt = t;
                            if (!callOfcExtension && !objGlobalMethods.IsEmptyString(validt) && validt.length() > 4 && (JobInfoActivity.maskContactNum != null && JobInfoActivity.maskContactNum)) {
                                validt = JobsHandler2.rtnMaskedContactNumber(validt);
                            }

                            TextView htv = new TextView(ctx);
                            htv.setText(maskedt);
                            htv.setVisibility(View.GONE);

                            TextView tv = new TextView(ctx);
                            tv.setText(validt);
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                            tv.setTextColor(Color.BLACK);
                            tv.setTypeface(null, Typeface.BOLD);
                            tv.setLayoutParams(params);
                            tv.setOnClickListener(callOfcExtension ? callOfcOnClick(maskedt) : contactNoOnClick(maskedt));
                            LLnumbers.addView(tv);
                            tvAddedCount += 1;

                            //new codes


                            ////old codes
                            /*TextView tv = new TextView(ctx);
                            tv.setText(validtels[i]);
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                            tv.setTextColor(Color.BLACK);
                            tv.setTypeface(null, Typeface.BOLD);
                            tv.setLayoutParams(params);
                            tv.setOnClickListener(callOfcExtension ? callOfcOnClick(tv.getText().toString()) : contactNoOnClick(tv.getText().toString()));
                            LLnumbers.addView(tv);
                            tvAddedCount += 1;*/
                            ////old codes
                        }

                        if (tvAddedCount > 0) {
                            builder.setCancelable(true);
                            MultiCallalertDialog = builder.create();
                            MultiCallalertDialog.show();
                        }
                    }

                } catch (Exception e) {
                    String s = e.getMessage().toString();
                    objGlobalMethods.writeToFile("Exception at JobInfoActivity MultiCOntactNoOnClick. Exception: " + s, "", "", "", "");
                }
            }
        };
    }

    //VOIP
    private void regVOIP(String ext) {
        try {
            if (app != null) {
                app.deinit();
                //app.delobj();
                app = null;
            }

            if (app == null) {
                //objGlobalMethods.writeToFile("app at JobInfoActivity at regVOIP is null. Initializing new app.", "", "", "", "");
                app = new VoipApp();
                // Wait for GDB to init, for native debugging only
                if (false &&
                        (getApplicationInfo().flags &
                                ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        String s = e.getMessage().toString();
                    }
                }
                //Uri uri = Uri.parse("sip:" + ext + "@27.54.52.188");
                app.init(this, getFilesDir().getAbsolutePath());
            }

            if (app.accList.size() > 0) {
                for (int ac = app.accList.size() - 1; ac >= 0; ac--) {
                    app.accList.remove(ac);
                }
            }

            if (app.accList.size() == 0) {
                //objGlobalMethods.writeToFile("app.accList.size() is == 0 at JobInfoActivity at regVOIP.", "", "", "", "");
                accCfg = new AccountConfig();
                ////accCfg.setIdUri("sip:localhost");
                /*accCfg.setIdUri("sip:" + ext + "@27.54.52.188:5060");
                accCfg.getRegConfig().setRegistrarUri("sip:27.54.42.188:5060");*/
                accCfg.setIdUri("sip:" + ext + "@v.xdel.com:5060");
                accCfg.getRegConfig().setRegistrarUri("sip:v.xdel.com:5060");
                accCfg.getNatConfig().setIceEnabled(false);
                accCfg.getVideoConfig().setAutoTransmitOutgoing(true);
                accCfg.getVideoConfig().setAutoShowIncoming(true);

                // Credentials BEFORE account creation, so the first REGISTER
                // can answer the 401 challenge internally.
                AuthCredInfoVector creds = accCfg.getSipConfig().getAuthCreds();
                creds.clear();
                creds.add(new AuthCredInfo("Digest", "*", ext, 0, "XD3xtpa$$"));

                account = app.addAcc(accCfg);
            } else {
                //objGlobalMethods.writeToFile("app.accList.size() is > 0 at JobInfoActivity at regVOIP.", "", "", "", "");
                account = app.accList.get(0);
                accCfg = account.cfg;
                /*accCfg.setIdUri("sip:" + ext + "@27.54.52.188:5060");
                accCfg.getRegConfig().setRegistrarUri("sip:27.54.42.188:5060");*/
                accCfg.setIdUri("sip:" + ext + "@v.xdel.com:5060");
                accCfg.getRegConfig().setRegistrarUri("sip:v.xdel.com:5060");

                accCfg.getNatConfig().setIceEnabled(false);
                accCfg.getVideoConfig().setAutoTransmitOutgoing(true);
                accCfg.getVideoConfig().setAutoShowIncoming(true);
            }

            try {
                account.modify(accCfg);
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Unable to register sip account. Exception: " + e.getMessage().toString(), "", "", "", "");
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    public void showCallActivity(String JobsIDXstr, String tel, String ext) {
        try {
            //Intent intent = new Intent(myApp.getAppContext(), CallActivity.class);
            Intent intent = new Intent(myApp.currentActivity, CallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            /*intent.putExtra("JobsIDX", JobsIDXstr);
            intent.putExtra("tel", tel);
            intent.putExtra("ext", ext);*/

            GcmPreferences = objGlobalMethods.getGcmPreferences();
            String callOfc = objGlobalMethods.getGCMPrefs(GcmPreferences, "callOfc");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "CallJobsIDX", JobsIDXstr);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "Calltel", tel);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "Callext", ext);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "CallRtn", "n");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "callloginserted", "f");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "CallLogInsertedTime", "");
            //CallActivity.callloginserted = null;
            CallActivity.log = null;
            if (callOfc.equals("")) {
                CallActivity.log = objGlobalMethods.composeCallLog(CallActivity.log, "Attempt to call " + tel + " using ext " + ext + ".");
            }
            CallActivity.cdTimer0 = null;
            CallActivity.cdTimer = null;
            CallActivity.timeConnectingStarted = null;
            CallActivity.timeFirstInContactToThisPage = null;
            CallActivity.callendedlogged = false;
            CallActivity.timeConnected = null;
            CallActivity.timeEnded = null;
            CallActivity.callHungUp = null;
            CallActivity.startRtnToJobInfoActivity2_called = null;
            JobInfoActivity.accessFromDirection = 0;
            startActivity(intent);
            finish();
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    public void returnCallActivity(View v) {
        try {
            //Intent intent = new Intent(myApp.getAppContext(), CallActivity.class);
            Intent intent = new Intent(myApp.currentActivity, CallActivity.class);
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "CallRtn", "y");
            JobInfoActivity.accessFromDirection = 0;
            startActivity(intent);
            finish();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at JobInfoActivity returnCallActivity. Exception: " + s, "", "", "", "");
        }
    }
    // ---- Pending-call gating: call is placed only after SIP registration succeeds ----
    private String pendingCallTel = null;
    private String pendingCallJobsIDX = null;
    private String pendingCallExt = null;
    private boolean pendingCallOfc = false;
    private View pendingCallView = null;
    private final Handler regTimeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable regTimeoutRunnable = null;
    private static final int REG_TIMEOUT_MS = 15000;
    public final Handler handlerVoip = new Handler(this);

    public class MSG_TYPE {
        public final static int INCOMING_CALL = 1;
        public final static int CALL_STATE = 2;
        public final static int REG_STATE = 3;
        public final static int BUDDY_STATE = 4;
        public final static int CALL_MEDIA_STATE = 5;
    }

    @Override
    public boolean handleMessage(Message m) {
        boolean asd = true;
        try {
            if (m.what == 0) {
                if (app != null) {
                    app.deinit();
                    //app.delobj();
                }
            } else if (m.what == MSG_TYPE.CALL_STATE) {
                CallInfo ci = (CallInfo) m.obj;

                /* Forward the message to CallActivity */
                if (CallActivity.handler_ != null) {
                    Message m2 = Message.obtain(CallActivity.handler_, MSG_TYPE.CALL_STATE, ci);
                    m2.sendToTarget();
                }
            } else if (m.what == MSG_TYPE.CALL_MEDIA_STATE) {
                /* Forward the message to CallActivity */
                if (CallActivity.handler_ != null) {
                    Message m2 = Message.obtain(CallActivity.handler_, MSG_TYPE.CALL_MEDIA_STATE, null);
                    m2.sendToTarget();
                }
            } else if (m.what == MSG_TYPE.REG_STATE) {
                /*String msg_str = (String) m.obj;
                lastRegStatus = msg_str;*/

                String msg_str = (String) m.obj;
                lastRegStatus = msg_str;
                objGlobalMethods.writeToFile("RegState: " + msg_str + " (code " + m.arg1 + ", exp " + m.arg2 + ")", "", "", "", "");
                if (m.arg1 / 100 == 2 && m.arg2 > 0) {
                    // 2xx AND not an unregistration -> account is registered
                    placePendingCall();
                }
                // Non-2xx: do nothing here; the 15s timeout is the terminal failure path.
            } else if (m.what == MSG_TYPE.INCOMING_CALL) {
                /* Incoming call */
                final MyCall call = (MyCall) m.obj;
                CallOpParam prm = new CallOpParam();

                /* Only one call at anytime */
                if (currentCall != null) {
                    // TODO: set status code
                    call.delete();
                    asd = true;
                }
                /* Answer with ringing */
                prm.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
                try {
                    call.answer(prm);
                } catch (Exception e) {
                }

                currentCall = call;
                showCallActivity("", "", "");
            } else {
                /* Message not handled */
                asd = false;
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at JobInfoActivity handleMessage. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    /*
     * === MyAppObserver ===
     *
     * As we cannot do UI from worker thread, the callbacks mostly just send
     * a message to UI/main thread.
     */

    public void notifyIncomingCall(MyCall call) {
        try {
            Message m = Message.obtain(handlerVoip, MSG_TYPE.INCOMING_CALL, call);
            m.sendToTarget();
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    /*public void notifyRegState(int code, String reason, long expiration) {
        try {
            String msg_str = "";
            if (expiration == 0)
                msg_str += "Unregistration";
            else
                msg_str += "Registration";

            if (code / 100 == 2)
                msg_str += " successful";
            else
                msg_str += " failed: " + reason;

            Message m = Message.obtain(handlerVoip, MSG_TYPE.REG_STATE, msg_str);
            m.sendToTarget();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity notifyRegState. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }*/

    public void notifyRegState(int code, String reason, long expiration) {
        try {
            String msg_str = "";
            if (expiration == 0)
                msg_str += "Unregistration";
            else
                msg_str += "Registration";

            if (code / 100 == 2)
                msg_str += " successful";
            else
                msg_str += " failed: " + reason;

            Message m = Message.obtain(handlerVoip, MSG_TYPE.REG_STATE, msg_str);
            m.arg1 = code;                // SIP status code (e.g. 200, 401, 408)
            m.arg2 = (int) expiration;    // 0 = unregistration
            m.sendToTarget();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity notifyRegState. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public void notifyCallState(MyCall call) {
        try {
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            String disconnectionProcessingAtCallActivity = objGlobalMethods.getGCMPrefs(gcmp, "disconnectionProcessingAtCallActivity");
            final String callOfc = objGlobalMethods.getGCMPrefs(gcmp, "callOfc");

            if (currentCall == null || call.getId() != currentCall.getId())
                return;

            CallInfo ci;
            try {
                ci = call.getInfo();
            } catch (Exception e) {
                ci = null;
            }
            Message m = Message.obtain(handlerVoip, MSG_TYPE.CALL_STATE, ci);
            m.sendToTarget();

            if (ci != null && ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                CallActivity.setAudioMode(0);
                //currentCall = null;
                objGlobalMethods.writeToFile("Call disconnected at JobInfoActivity.", "", "", "", "");
                ongoingcall = null;

                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobInfoActivity.class) {
                    final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            //objGlobalMethods.writeToFile("shideshowSubmitBtns at JobInfoActivity.", "", "", "", "");
                            act.shideshowSubmitBtns(objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences));
                        }
                    });
                }

                if (CallActivity.cdTimer0 != null) {
                    CallActivity.cdTimer0.cancel();
                    CallActivity.cdTimer0 = null;
                }

                if (CallActivity.cdTimer != null) {
                    CallActivity.cdTimer.cancel();
                    CallActivity.cdTimer = null;
                }

                if (myApp.currentActivity.getClass() != null && myApp.currentActivity.getClass() == CallActivity.class) {
                    if (!disconnectionProcessingAtCallActivity.equals("t")) {
                        CallActivity.forceHangUpLeavePage();
                    }
                } else {
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences gp = objGlobalMethods.getGcmPreferences();
                            String callloginserted = objGlobalMethods.getGCMPrefs(gp, "callloginserted");
                            boolean within10s = JobsHandler.within10s(gp);
                            if (!callloginserted.equals("t") && !within10s && callOfc.equals("")) {
                                objGlobalMethods.setGCMPrefs(gp, "callloginserted", "t");
                                Gson gson = new Gson();
                                String CallLogInsertedTime = gson.toJson(new Date());
                                objGlobalMethods.setGCMPrefs(gp, "CallLogInsertedTime", CallLogInsertedTime);
                                //objGlobalMethods.writeToFile("Insert call log at JobInfoActivity notifyCallState PJSIP_INV_STATE_DISCONNECTED.", "", "", "", "");
                                objGlobalMethods.insertCallLog(gp, false);
                                String AllValidTelNums = objGlobalMethods.getGCMPrefs(gp, "AllValidTelNums");
                                if (!objGlobalMethods.IsEmptyString(AllValidTelNums))
                                    setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
                                JobInfoActivity.maskContactNum = !JobsHandler2.canShowFullContactNumber(objGlobalMethods.getGcmPreferences());
                                if (!(new ConnectionDetector(null).isConnectingToInternet()))
                                    JobInfoActivity.maskContactNum = false;
                                JobInfoActivity.maskContactNum = false; // forcefully added here until further notice (by boss)
                                //unmaskTelInJobInfoActivity();
                            }
                        }
                    }, 500);


                    CallActivity.timeEnded = CallActivity.init_timeEnded();
                    CallActivity.log = objGlobalMethods.getGCMPrefs(gcmp, "CallLogFullMsg");
                    if ((myApp.currentActivity.getClass() != null && myApp.currentActivity.getClass() == JobInfoActivity.class) && CallActivity.log != null) {
                        if (CallActivity.callendedlogged != null && CallActivity.callendedlogged) {
                            //do nothing
                        } else {
                            CallActivity.callendedlogged = true;
                            if (callOfc.equals("")) {
                                CallActivity.log = objGlobalMethods.composeCallLog(CallActivity.log, "Call ended. Call Duration: " + String.valueOf(CallActivity.returnCallDuration()) + " seconds.");
                            }
                        }
                    }

                    if (CallActivity.callHungUp != null && CallActivity.callHungUp) {
                        //do nothing
                    } else {
                        objGlobalMethods.writeToFile("Calling UpdateCallLogCallEnded at JobInfoActivity.", "", "", "", "");
                        if (callOfc.equals("")) {
                            objGlobalMethods.UpdateCallLogCallEnded();
                        }
                    }

                    CallActivity.log = null;
                    CallActivity.handler_ = null;

                    objGlobalMethods.setGCMPrefs(gcmp, "callloginserted", "f");
                    if (currentCall != null) {
                        CallOpParam prm = new CallOpParam();
                        prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
                        try {
                            currentCall.hangup(prm);
                        } catch (Exception e) {
                            String s = e.getMessage().toString();
                        }
                    }
                }
                unRegisterSensorListener();
                currentCall = null;
            } else if (ci != null && ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                objGlobalMethods.writeToFile("Call connected at JobInfoActivity.", "", "", "", "");
                if (CallActivity.cdTimer0 != null) {
                    CallActivity.cdTimer0.cancel();
                    CallActivity.cdTimer0 = null;
                }

                if (CallActivity.cdTimer != null) {
                    CallActivity.cdTimer.cancel();
                    CallActivity.cdTimer = null;
                }

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences gp = objGlobalMethods.getGcmPreferences();
                        String callloginserted = objGlobalMethods.getGCMPrefs(gp, "callloginserted");
                        boolean within10s = JobsHandler.within10s(gp);
                        if (!callloginserted.equals("t") && !within10s && callOfc.equals("")) {
                            objGlobalMethods.setGCMPrefs(gp, "callloginserted", "t");
                            Gson gson = new Gson();
                            String CallLogInsertedTime = gson.toJson(new Date());
                            objGlobalMethods.setGCMPrefs(gp, "CallLogInsertedTime", CallLogInsertedTime);
                            //objGlobalMethods.writeToFile("Insert call log at JobInfoActivity notifyCallState PJSIP_INV_STATE_DISCONNECTED.", "", "", "", "");
                            objGlobalMethods.insertCallLog(gp, true);
                            String AllValidTelNums = objGlobalMethods.getGCMPrefs(gp, "AllValidTelNums");
                            if (!objGlobalMethods.IsEmptyString(AllValidTelNums))
                                setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
                            JobInfoActivity.maskContactNum = !JobsHandler2.canShowFullContactNumber(objGlobalMethods.getGcmPreferences());
                            if (!(new ConnectionDetector(null).isConnectingToInternet()))
                                JobInfoActivity.maskContactNum = false;
                            JobInfoActivity.maskContactNum = false; // forcefully added here until further notice (by boss)
                            //unmaskTelInJobInfoActivity();
                        }
                    }
                }, 500);

                CallActivity.log = objGlobalMethods.getGCMPrefs(gcmp, "CallLogFullMsg");
                if ((myApp.currentActivity.getClass() != null && myApp.currentActivity.getClass() == CallActivity.class) && CallActivity.log != null) {
                    CallActivity.setAudioMode(3);
                    if (callOfc.equals("")) {
                        CallActivity.timeConnected = CallActivity.init_timeConnected();
                        CallActivity.log = objGlobalMethods.composeCallLog(CallActivity.log, "Customer Answered/Into Voicemail.");
                    }
                    final CallActivity act = (CallActivity) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            act.forceShowBtnHangUp(true);
                        }
                    });
                } else if ((myApp.currentActivity.getClass() != null && myApp.currentActivity.getClass() == JobInfoActivity.class) && CallActivity.log != null) {
                    CallActivity.setAudioMode(3);
                    if (callOfc.equals("")) {
                        CallActivity.timeConnected = CallActivity.init_timeConnected();
                        CallActivity.log = objGlobalMethods.composeCallLog(CallActivity.log, "Customer Answered/Into Voicemail.");
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity notifyCallState. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public void notifyCallMediaState(MyCall call) {
        try {
            Message m = Message.obtain(handlerVoip, MSG_TYPE.CALL_MEDIA_STATE, null);
            m.sendToTarget();
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    public void notifyBuddyState(MyBuddy buddy) {
        try {
            Message m = Message.obtain(handlerVoip, MSG_TYPE.BUDDY_STATE, buddy);
            m.sendToTarget();
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    private class Ping extends AsyncTask<Void, Void, Boolean> {
        public Ping() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean asd = false;
            int pcount = 0;
            long currentTime = 0;
            boolean isPinged;
            try {
                do {
                    if (pcount < 4) {
                        currentTime = System.currentTimeMillis();
                        //isPinged = InetAddress.getByName("27.54.42.188").isReachable(2000); // 2 seconds
                        isPinged = InetAddress.getByName("v.xdel.com").isReachable(2000); // 2 seconds
                        currentTime = System.currentTimeMillis() - currentTime;
                        CallActivity.log = objGlobalMethods.composeCallLog(CallActivity.log, !isPinged ? "Ping failed." : "Pinged successfully in " + currentTime + "millisecond\n");
                        pcount += 1;
                    }
                } while (pcount < 4);
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at JobInfoActivity Ping. Exception: " + s, "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean asd) {
            try {

            } catch (Exception e) {

            }
        }
    }

    private void handleVoipCallFailure(String JobsIDX, String tel, String ext,
                                       boolean callOfc, View v, String allpinglog) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();

            String timestampstr = objGlobalMethods.rtntimestampstr();
            Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
            int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
            String RegID = objGlobalMethods.getRegistrationIdOnly();
            String callLog = objGlobalMethods.composeCallLog(allpinglog, "Unable to call out to " + tel + " using ext" + ext + ". Connection issue.");

            if (!callOfc) {
                JobsHandler.addUpdateJobInfoCallLogsQueALPrefs(UpdateJobInfoPreferences, RegID, Long.parseLong(JobsIDX), 388, timestampstr, callLog, "",
                        lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");
                objGlobalMethods.writeToFile(callLog, "JobsIDX: " + String.valueOf(JobsIDX), "Submitted StatusCodeIDX: " + String.valueOf(388), "", "");
                submitSQLite(Long.parseLong(JobsIDX), 388, GcmPreferences, UpdateJobInfoPreferences);
                catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                DAO_Jobs.insertCallLog(tel, new Date().getTime(), catType, true, false);
                String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                if (!objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                    setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
                } else {
                    setFailStatusActivity.CheckReqCallLogImg2(tel);
                }
                JobInfoActivity.maskContactNum = !JobsHandler2.canShowFullContactNumber(GcmPreferences);
                if (!(new ConnectionDetector(null).isConnectingToInternet()))
                    JobInfoActivity.maskContactNum = false;
                JobInfoActivity.maskContactNum = false; // forcefully added here until further notice (by boss)
                //unmaskTelInJobInfoActivity();
                JobsHandler.AddRemoveClearObjCallback(GcmPreferences, lblJobNoVal.getText().toString(), AllValidTelNums, false);
            }

            try {
                TextView lbl = (TextView) v;
                lbl.setTextColor(getResources().getColor(R.color.black));
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at JobInfoActivity ConnectingVOIP onPostExecute change textcolor. Exception: " + e.getMessage().toString(), "", "", "", "");
            }
            showDialog(myApp.getAppContext().getString(R.string.nonetworknocall2));
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at handleVoipCallFailure. Exception: " + e.getMessage() + "\nStack:\n" + JobsHandler.getStackTraceString(e), "", "", "", "");
        }
    }

    private void placePendingCall() {
        try {
            if (pendingCallTel == null) return;   // no call waiting (e.g. periodic re-registration)
            if (regTimeoutRunnable != null) {
                regTimeoutHandler.removeCallbacks(regTimeoutRunnable);
                regTimeoutRunnable = null;
            }
            final String tel = pendingCallTel;
            final String JobsIDX = pendingCallJobsIDX;
            final String ext = pendingCallExt;
            final boolean callOfc = pendingCallOfc;
            final View v = pendingCallView;
            pendingCallTel = null;
            pendingCallJobsIDX = null;
            pendingCallExt = null;
            pendingCallView = null;

            if (pdvoip != null) {
                pdvoip.dismiss();
            }

            String buddy_uri = "sip:" + tel + "@v.xdel.com:5060";
            MyCall call = new MyCall(account, -1);
            CallOpParam prm = new CallOpParam(true);
            try {
                call.makeCall(buddy_uri, prm);
            } catch (Exception e) {
                call.delete();
                objGlobalMethods.writeToFile("Exception at placePendingCall call.makeCall. Exception: " + e.getMessage() + "\nStack:\n" + JobsHandler.getStackTraceString(e), "", "", "", "");
                handleVoipCallFailure(JobsIDX, tel, ext, callOfc, v, "");
                return;
            }

            currentCall = call;
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
            JobsHandler.AddRemoveClearObjCallback(GcmPreferences, lblJobNoVal.getText().toString(), AllValidTelNums, false);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "callOfc", callOfc ? "y" : "");
            showCallActivity(JobsIDX, tel, ext);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at placePendingCall. Exception: " + e.getMessage() + "\nStack:\n" + JobsHandler.getStackTraceString(e), "", "", "", "");
        }
    }

    private void onRegTimeout(String allpinglog) {
        try {
            if (pendingCallTel == null) return;   // call already placed; nothing to do
            objGlobalMethods.writeToFile("SIP registration timed out after " + REG_TIMEOUT_MS + "ms. Last reg status: " + lastRegStatus, "", "", "", "");
            final String tel = pendingCallTel;
            final String JobsIDX = pendingCallJobsIDX;
            final String ext = pendingCallExt;
            final boolean callOfc = pendingCallOfc;
            final View v = pendingCallView;
            pendingCallTel = null;
            pendingCallJobsIDX = null;
            pendingCallExt = null;
            pendingCallView = null;
            regTimeoutRunnable = null;

            if (pdvoip != null) {
                pdvoip.dismiss();
            }
            handleVoipCallFailure(JobsIDX, tel, ext, callOfc, v, allpinglog);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at onRegTimeout. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private class ConnectingVOIP extends AsyncTask<Void, Void, String> {
        private final String JobsIDX;
        private final String tel;
        private final View v;
        private String allpinglog = "";
        private final boolean callOfc;

        public ConnectingVOIP(String aJobsIDX, String atel, View av, boolean acallOfc) {
            this.JobsIDX = aJobsIDX;
            this.tel = atel;
            this.v = av;
            this.callOfc = acallOfc;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            try {
                if (pdvoip == null) {
                    Context ctx = myApp.getAppContext();
                    pdvoip = new ProgressDialog(ctx);
                    pdvoip.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pdvoip.setCanceledOnTouchOutside(false);
                    pdvoip.setMessage(myApp.getAppContext().getString(R.string.plswaitconnecting));
                }
                pdvoip.show();
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at JobInfoActivity ConnectingVOIP onPreExecute. Exception: " + s, "", "", "", "");
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            boolean asd = false;
            boolean asd2 = false;
            String ext = "";
            Long extLong = Long.parseLong("0");
            String ext2000 = "";
            String ext3000 = "";
            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();

                /*Ping VOIP server*/
                /*int pcount = 1;
                long currentTime = 0;
                boolean isPinged;
                allpinglog = objGlobalMethods.getMobileOrWifiSignalStrength() + "\n";
                try {
                    Pinger pinger = new Pinger();
                    do {
                        if (pcount < 2) {
                            allpinglog += pinger.ping2("27.54.42.188", 2); //2 sec
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "pinglog", allpinglog);
                            pcount += 1;
                        }
                    } while (pcount < 2);
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                    objGlobalMethods.writeToFile("Exception at JobInfoActivity ConnectingVOIP Ping VOIP server. Exception: " + s,"","","","");
                }*/
                /*Ping VOIP server*/

                //old codes
                String RegID = objGlobalMethods.getRegistrationIdOnly();
                ext = WebService.GetVoIPExtension(RegID);
                if (ext != null && !ext.isEmpty() && !ext.equals("") && ext != "" && !ext.substring(0, 1).equals("3") && JobsHandler.is3000series(GcmPreferences)) {
                    try {
                        extLong = Long.parseLong(ext);
                        extLong += 1000;
                        ext = String.valueOf(extLong);
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity ConnectingVOIP doInBackground converting ext to long value. Exception: " + e.getMessage().toString(), "", "", "", "");
                    }
                }
                //old codes

            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at JobInfoActivity ConnectingVOIP doInBackground. Exception: " + s, "", "", "", "");
            } finally {
                /*if (cc == 1) {
                    ext = null;
                }*/
                return ext;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final String ext) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            try {
                if (ext != null && !ext.isEmpty()) {
                    objGlobalMethods.writeToFile("Attempt to call " + tel + " using ext " + ext + ". Registering SIP account first.", "", "", "", "");

                    // Stash the call; it is placed in placePendingCall() once
                    // registration succeeds (handleMessage REG_STATE 2xx).
                    pendingCallTel = tel;
                    pendingCallJobsIDX = JobsIDX;
                    pendingCallExt = ext;
                    pendingCallOfc = callOfc;
                    pendingCallView = v;

                    regVOIP(ext);   // async: result arrives via notifyRegState

                    regTimeoutRunnable = new Runnable() {
                        @Override
                        public void run() {
                            onRegTimeout(allpinglog);
                        }
                    };
                    regTimeoutHandler.postDelayed(regTimeoutRunnable, REG_TIMEOUT_MS);
                    // NOTE: pdvoip is intentionally NOT dismissed here — the spinner
                    // stays up until placePendingCall() or onRegTimeout().
                } else {
                    if (pdvoip != null) {
                        pdvoip.dismiss();
                    }
                    objGlobalMethods.writeToFile("GetVoIPExtension return null or empty. Unable to call out.", "", "", "", "");
                    handleVoipCallFailure(JobsIDX, tel, ext, callOfc, v, allpinglog);
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInfoActivity ConnectingVOIP onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                if (pdvoip != null) {
                    pdvoip.dismiss();
                }
                handleVoipCallFailure(JobsIDX, tel, ext, callOfc, v, allpinglog);
            }
            /*boolean asd = false;
            boolean asd2 = false;
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            try {
                if (pdvoip != null) {
                    pdvoip.dismiss();
                }

                if (ext != null && !ext.isEmpty() && !ext.equals("") && ext != "") {
                    objGlobalMethods.writeToFile("Attempt to call " + tel + " using ext " + ext, "", "", "", "");
                    regVOIP(ext);

                    ////String buddy_uri = "sip:" + tel + "@27.54.42.188:5060";
                    String buddy_uri = "sip:" + tel + "@v.xdel.com:5060";
                    MyCall call = new MyCall(account, -1);
                    CallOpParam prm = new CallOpParam(true);
                    try {
                        call.makeCall(buddy_uri, prm);
                    } catch (Exception e) {
                        call.delete();
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity call.makeCall. Exception: " + e.getMessage() + "\nStack:\n" + JobsHandler.getStackTraceString(e), "", "", "", "");
                        asd2 = true;
                    }

                    if (!asd2) {
                        currentCall = call;
                        asd = true;
                    }
                } else {
                    objGlobalMethods.writeToFile("GetVoIPExtension return null or empty. Unable to call out.", "", "", "", "");
                }

                if (asd) {
                    String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                    JobsHandler.AddRemoveClearObjCallback(GcmPreferences, lblJobNoVal.getText().toString(), AllValidTelNums, false);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "callOfc", callOfc ? "y" : "");
                    showCallActivity(String.valueOf(JobsIDX), tel, ext);
                } else {
                    String timestampstr = objGlobalMethods.rtntimestampstr();
                    Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                    int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                    String RegID = objGlobalMethods.getRegistrationIdOnly();
                    String callLog = objGlobalMethods.composeCallLog(allpinglog, "Unable to call out to " + tel + " using ext" + ext + ". Connection issue.");

                    if (!callOfc) {
                        JobsHandler.addUpdateJobInfoCallLogsQueALPrefs(UpdateJobInfoPreferences, RegID, Long.parseLong(JobsIDX), 388, timestampstr, callLog, "",
                                lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");
                        objGlobalMethods.writeToFile(callLog, "JobsIDX: " + String.valueOf(JobsIDX), "Submitted StatusCodeIDX: " + String.valueOf(388), "", "");
                        submitSQLite(Long.parseLong(JobsIDX), 388, GcmPreferences, UpdateJobInfoPreferences);
                        catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                        DAO_Jobs.insertCallLog(tel, new Date().getTime(), catType, true, false);
                        String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                        if (!objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                            setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
                        } else {
                            setFailStatusActivity.CheckReqCallLogImg2(tel);
                        }
                        JobInfoActivity.maskContactNum = !JobsHandler2.canShowFullContactNumber(GcmPreferences);
                        if (!(new ConnectionDetector(null).isConnectingToInternet()))
                            JobInfoActivity.maskContactNum = false;
                        JobInfoActivity.maskContactNum = false;
                        JobsHandler.AddRemoveClearObjCallback(GcmPreferences, lblJobNoVal.getText().toString(), AllValidTelNums, false);
                    }

                    try {
                        TextView lbl = (TextView) v;
                        lbl.setTextColor(getResources().getColor(R.color.black));
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity ConnectingVOIP onPostExecute change textcolor. Exception: " + e.getMessage().toString(), "", "", "", "");
                    }
                    showDialog(myApp.getAppContext().getString(R.string.nonetworknocall2));
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInfoActivity ConnectingVOIP onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                if (!callOfc) {
                    try {
                        String callLog = objGlobalMethods.composeCallLog(allpinglog, "Unable to call out to " + tel + " using ext" + ext + ". Connection issue.");
                        String timestampstr = objGlobalMethods.rtntimestampstr();
                        Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                        int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                        String RegID = objGlobalMethods.getRegistrationIdOnly();
                        JobsHandler.addUpdateJobInfoCallLogsQueALPrefs(UpdateJobInfoPreferences, RegID, Long.parseLong(JobsIDX), 388, timestampstr, callLog, "",
                                lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");
                        objGlobalMethods.writeToFile(callLog, "JobsIDX: " + String.valueOf(JobsIDX), "Submitted StatusCodeIDX: " + String.valueOf(388), "", "");
                        submitSQLite(Long.parseLong(JobsIDX), 388, GcmPreferences, UpdateJobInfoPreferences);
                        catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                        DAO_Jobs.insertCallLog(tel, new Date().getTime(), catType, true, false);
                        String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                        if (!objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                            setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
                        } else {
                            setFailStatusActivity.CheckReqCallLogImg2(tel);
                        }
                        JobInfoActivity.maskContactNum = !JobsHandler2.canShowFullContactNumber(GcmPreferences);
                        if (!(new ConnectionDetector(null).isConnectingToInternet()))
                            JobInfoActivity.maskContactNum = false;
                        JobInfoActivity.maskContactNum = false;
                        JobsHandler.AddRemoveClearObjCallback(GcmPreferences, lblJobNoVal.getText().toString(), AllValidTelNums, false);
                    } catch (Exception ex) {
                        String ss = JobsHandler.getStackTraceString(ex);
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity ConnectingVOIP onPostExecute Exception submit call log. Exception: " + ss, "", "", "", "");
                    }
                }
                DAO_Jobs.insertCallLog(tel, new Date().getTime(), catType, true, false);
                setFailStatusActivity.CheckReqCallLogImg2(tel);
                showDialog(myApp.getAppContext().getString(R.string.voiperr_restart));
            }*/
        }

    }

    /*public void promptReqMPD() {
        objGlobalMethods.promptReqMPD();
    }*/

    /* === end of MyAppObserver ==== */
    //VOIP

    public class ShowCustomCallOutAlertDialog {
        public void showAlertDialog(Activity activity, String msg, ArrayList<ArrayList> sentenceHasValidContactNo) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_alert_dialog);

            Button btnok = (Button) dialog.findViewById(R.id.btn_ok);
            TextView txt = (TextView) dialog.findViewById(R.id.txt);
            try {
                if (sentenceHasValidContactNo != null && sentenceHasValidContactNo.size() > 0) {
                    SpannableString ss = new SpannableString(msg);
                    /*for (int i = 0; i <= sentenceHasValidContactNo.size() - 1; i++) {
                        final ArrayList al = sentenceHasValidContactNo.get(i);
                        ClickableSpan cs = new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                contactNoOnClick((String) al.get(0));
                            }
                        };
                        ss.setSpan(cs, (Integer) al.get(1), (Integer) al.get(2), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }*/
                    ClickableSpan cs = new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(myApp.currentActivity, "test", Toast.LENGTH_SHORT).show();
                        }
                    };
                    ss.setSpan(cs, (Integer) 0, msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    txt.setText(ss);
                    txt.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                    txt.setText(msg);
                    txt.setOnClickListener(null);
                }

                btnok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at JobInfoActivity ShowCustomCallOutAlertDialog. Exception: " + e.getMessage(), "", "", "", "");
                txt.setText(msg);
                txt.setOnClickListener(null);
            }
        }
    }

    private void setSpannableString(final TextView v, String msg) {
        try {
            ArrayList<ArrayList> sentence_has_valid_ContactNo = objGlobalMethods.sentenceHasValidContactNo(msg);
            if (sentence_has_valid_ContactNo != null && sentence_has_valid_ContactNo.size() > 0) {
                SpannableString ss = new SpannableString(msg);
                for (int i = 0; i <= sentence_has_valid_ContactNo.size() - 1; i++) {
                    final ArrayList al = sentence_has_valid_ContactNo.get(i);
                    ClickableSpan cs = new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            callout((String) al.get(0), v);
                            //Toast.makeText(myApp.currentActivity, (String) al.get(0), Toast.LENGTH_SHORT).show();
                        }
                    };
                    ss.setSpan(cs, (Integer) al.get(1), (Integer) al.get(2), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                v.setText(ss);
                v.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                v.setText(msg);
                v.setOnClickListener(null);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity setSpannableString. Exception: " + e.getMessage(), "", "", "", "");
            v.setText(msg);
        }
    }

    public void sendWhatsappMsg(final String tel) {
        try {
            String toNumber = tel; // contains spaces, i.e., example +91 98765 43210
            toNumber = toNumber.replace("+", "").replace(" ", "");
            String customerPhoneNumber = String.format("%s%s", "+65", toNumber);
            Uri uri = Uri.parse("smsto:" + customerPhoneNumber);
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } catch (ActivityNotFoundException aex) {
            objGlobalMethods.writeToFile("ActivityNotFoundException at JobInfoActivity sendWhatsappMsg.", "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.installwhatsapp));
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity sendWhatsappMsg. Exception: " + e.getMessage(), "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    public void sendWhatsappMsg2(final String tel) {
        try {
            String toNumber = tel; // contains spaces, i.e., example +91 98765 43210
            toNumber = toNumber.replace("+", "").replace(" ", "");
            String customerPhoneNumber = String.format("%s%s", "+65", toNumber);
            String url = "https://api.whatsapp.com/send?phone=" + customerPhoneNumber;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity sendWhatsappMsg2. Exception: " + e.getMessage(), "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    private void initFabSI() {
        try {
            if (fabsi != null) {
                fabsi.setOnClickListener(null);
            } else {
                fabsi = findViewById(R.id.fabWaze);
            }

            if (LLSI2.getVisibility() == View.VISIBLE) {
                fabsi.setOnClickListener(fabSI_OnClickListener());
                fabsi.show();
            } else {
                fabsi.hide();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInfoActivity initFabSI. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private View.OnClickListener fabSI_OnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //sv.fullScroll(View.FOCUS_DOWN);
                    sv.smoothScrollBy(0, 1000);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at JobInfoActivity fabSI_OnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    public void requestExt(long JobsIDX, int minute) {
        try {
            RequestExtension r = new RequestExtension(JobsIDX, minute);
            r.execute();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity requestExt. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public class RequestExtension extends AsyncTask<Void, Void, L2_RequestExtensionResult> {
        private final long idx;
        private final int min;

        public RequestExtension(long aidx, int amin) {
            this.idx = aidx;
            this.min = amin;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            try {
                if (pdverifyMe == null) {
                    Context ctx = myApp.getAppContext();
                    pdverifyMe = new ProgressDialog(ctx);
                    pdverifyMe.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pdverifyMe.setCanceledOnTouchOutside(false);
                    pdverifyMe.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));
                }
                pdverifyMe.show();
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at JobInfoActivity ConnectingVOIP onPreExecute. Exception: " + s, "", "", "", "");
            }
        }

        @Override
        protected L2_RequestExtensionResult doInBackground(Void... params) {
            L2_RequestExtensionResult res = null;
            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                String RegID = objGlobalMethods.getRegistrationIdOnly();
                res = WebService.L2_RequestExtension(RegID, idx, min);
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at JobInfoActivity RequestExtension doInBackground. Exception: " + s, "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final L2_RequestExtensionResult res) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            Context c = myApp.getAppContext();

            try {
                if (pdverifyMe != null) {
                    pdverifyMe.dismiss();
                }

                if (res != null && res.Status == 0) {
                    objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.alert_ext_req_ok)).show();
                } else if (res != null && res.Status != 0) {
                    objGlobalMethods.writeToFile("JobsIDX: " + idx + "\n" + res.Message, "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.alert_ext_req_fail)).show();
                } else {
                    objGlobalMethods.writeToFile("JobsIDX: " + idx + "\n res is null.", "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.alert_ext_req_fail)).show();
                }
            } catch (Exception e) {
                if (pdverifyMe != null) {
                    pdverifyMe.dismiss();
                }
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInfoActivity RequestExtension onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }

    }

    private boolean hasBTLogger(long JobsIDX, SharedPreferences g) {
        MPD_Group[] mgs = null;
        MPD_Group mg = null;
        boolean asd = false;
        int Jobs_AttachedCount = 0;
        MPD_Group_Item mgi = null;
        MPD_Item mi = null;
        try {
            if (g == null)
                g = objGlobalMethods.getGcmPreferences();


            //String mgsJsonStr = objGlobalMethods.getGCMPrefs(g, "MPDGroupsJsonStr");
            String mgsJsonStr = objGlobalMethods.getMPDRelatedSP_KeyValue("MPDGroupsJsonStr");
            if (!objGlobalMethods.IsEmptyString(mgsJsonStr) && !mgsJsonStr.equals("[]")) {
                mgs = new Gson().fromJson(mgsJsonStr, MPD_Group[].class);
            }

            if (mgs != null) {
                for (int x = 0; x <= mgs.length - 1; x++) {
                    mg = mgs[x];
                    Jobs_AttachedCount = mg.Jobs_Attached != null && mg.Jobs_Attached.length > 0 ? mg.Jobs_Attached.length : 0;
                    if (mg != null && mg.Items != null && mg.Items.length > 0) {
                        for (int i = 0; i <= mg.Items.length - 1; i++) {
                            mgi = mg.Items[i];
                            mi = mgi.MPD_Item;
                            if (mi.Device_Type == 22 && Jobs_AttachedCount > 0 && temploggerclass.jobsidxExist(mg.Jobs_Attached, JobsIDX)) {
                                asd = true;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity hasBTLogger. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public void promptToScanForBTlogger() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.temp_prompt2));

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    try {

                        GcmPreferences = objGlobalMethods.getGcmPreferences();

                        String mdpsJsonStr = null;
                        String ARegID = null;
                        bttemploggerStandaloneclass btc = new bttemploggerStandaloneclass();
                        SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                        String bttemploggerStandaloneclassStarted = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "bttemploggerStandaloneclassStarted");

                        if (!objGlobalMethods.IsEmptyString(bttemploggerStandaloneclassStarted)) {
                            objGlobalMethods.showDialog(myApp.currentActivity, "OK", "Please wait for 1 minute.").show();
                        } else {
                            //mdpsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "mdpsJsonStr");
                            mdpsJsonStr = objGlobalMethods.getMPDRelatedSP_KeyValue("mdpsJsonStr");
                            ARegID = objGlobalMethods.getRegistrationIdOnly();
                            if (!objGlobalMethods.IsEmptyString(mdpsJsonStr)) {
                                Gson gson = new Gson();
                                List<MacDevicePair> mdps = gson.fromJson(mdpsJsonStr, new TypeToken<List<MacDevicePair>>() {
                                }.getType());
                                btc.setMacDevicePairs(mdps);
                                btc.setJobsIDX(currJobInfo.JobsIDX);
                                btc.setTempcheckforattachment(false);
                                btc.setARegID(ARegID);
                                btc.scan();
                            } else {
                                //do nothing
                            }
                        }

                    } catch (Exception e) {
                        String s = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity setPositiveButton. Exception: " + e.getMessage().toString(), "", "", "", "");
                    }
                }
            });

            builder.setNeutralButton(myApp.getAppContext().getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        String s = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at JobInfoActivity setPositiveButton. Exception: " + e.getMessage().toString(), "", "", "", "");
                    }
                }
            });


            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity promptToScanForBTlogger. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public void complianceCheck(UploadTempDataObj utd) {
        try {
            Check_Cold_Item_Handling_Compliance c = new Check_Cold_Item_Handling_Compliance(utd);
            c.execute();
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at JobInfoActivity complianceCheck. Exception: " + s, "", "", "", "");
        }
    }

    public void showUploadingTempPD() {
        try {
            Context ctx = myApp.getAppContext();
            if (pdUploadingTemp != null && pdUploadingTemp.isShowing())
                pdUploadingTemp.dismiss();

            pdUploadingTemp = null;
            pdUploadingTemp = new ProgressBarDialog(myApp.currentActivity);
            pdUploadingTemp.setCanceledOnTouchOutside(false);
            pdUploadingTemp.setCancelable(false);
            pdUploadingTemp.setText(ctx.getString(R.string.uploading_temp));

            pdUploadingTemp.show();
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at JobInfoActivity showUploadingTempPD. Exception: " + s, "", "", "", "");
        }
    }

    public void dismissUploadingTempPD() {
        try {
            if (pdUploadingTemp != null) {
                pdUploadingTemp.dismiss();
            }
            pdUploadingTemp = null;
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at JobInfoActivity dismissUploadingTempPD. Exception: " + s, "", "", "", "");
        }
    }

    public void showTempCompCheckPD() {
        try {
            Context ctx = myApp.getAppContext();
            if (pdTempCompCheck != null && pdTempCompCheck.isShowing())
                pdTempCompCheck.dismiss();

            pdTempCompCheck = null;
            pdTempCompCheck = new ProgressBarDialog(myApp.currentActivity);
            pdTempCompCheck.setCanceledOnTouchOutside(false);
            pdTempCompCheck.setCancelable(false);
            pdTempCompCheck.setText(ctx.getString(R.string.verify_temp_compliance));

            pdTempCompCheck.show();
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at JobInfoActivity showTempCompCheckPD. Exception: " + s, "", "", "", "");
        }
    }

    public void dismissTEmpCompCheckPD() {
        try {
            if (pdTempCompCheck != null) {
                pdTempCompCheck.dismiss();
            }
            pdTempCompCheck = null;
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at JobInfoActivity dismissTEmpCompCheckPD. Exception: " + s, "", "", "", "");
        }
    }

    public class Check_Cold_Item_Handling_Compliance extends AsyncTask<Void, Void, xwsBase> {
        private final UploadTempDataObj utd;

        public Check_Cold_Item_Handling_Compliance(UploadTempDataObj autd) {
            this.utd = autd;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            try {
                showTempCompCheckPD();
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at JobInfoActivity ConnectingVOIP onPreExecute. Exception: " + s, "", "", "", "");
            }
        }

        @Override
        protected xwsBase doInBackground(Void... params) {
            xwsBase xb = null;
            Boolean uploadtempsuccess = null;
            boolean TempIsCompliant = false;
            try {
                ConnectionDetector cd = new ConnectionDetector(null);
                if (cd.isConnectingToInternet()) {
                    String ARegID = objGlobalMethods.getRegistrationIdOnly();
                    uploadtempsuccess = WebService.L2_Upload_Temperature(ARegID, utd.TemperaturePayloads);
                    if (uploadtempsuccess == null) {
                        xb = new xwsBase();
                        if (utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0) {
                            TemperaturePayload tpl = utd.TemperaturePayloads[utd.TemperaturePayloads.length - 1];
                            if (tpl != null) {
                                TempIsCompliant = temploggerclass.TempIsCompliant(tpl.Temperature);
                                xb.setStatus(TempIsCompliant ? 0 : -1);
                                xb.setMessage("L2_Upload_Temperature returns null.\nTemperature recorded: " + tpl.Temperature + ".\n"
                                        + (TempIsCompliant ? "Compliant." : "Non Compliant."));
                                long tsl = tpl.TimestampLong;
                                xb.setExMessage(TempIsCompliant ? "" :
                                        tpl.Temperature + "°C" + " ON " + objGlobalMethods.MillisecToDateStr2(tsl, "dd/MM/yyyy HH:mm"));
                            } else {
                                xb.setStatus(-1);
                                xb.setMessage("L2_Upload_Temperature returns null..\nTemperaturePayload tpl is null");
                                xb.setExMessage("L2_Upload_Temperature returns null..\nTemperaturePayload tpl is null");
                            }
                        } else {
                            xb.setStatus(-1);
                            xb.setMessage("L2_Upload_Temperature returns null.\n!(utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0)");
                            xb.setExMessage("L2_Upload_Temperature returns null.\n!(utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0)");
                        }

                        JobsHandler.addUploadTempDataObjQue(objGlobalMethods.getUpdateJobInfoPreferences(), utd);
                        Context ctx = myApp.getAppContext();
                        ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                    } else if (uploadtempsuccess) {
                        Date dt = new Date();
                        Long l = dt.getTime();
                        String timestampstr = "/Date(" + l + ")/";
                        long jidx = 0;
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                        if (!JobsIDXstr.equals("")) {
                            try {
                                jidx = Long.parseLong(JobsIDXstr);
                            } catch (Exception e) {
                                objGlobalMethods.writeToFile("Exception caught at JobInfoActivity Check_Cold_Item_Handling_Compliance doInBackground convert JobsIDXstr to Long value.", "", "", "", "");
                                jidx = 0;
                            }
                        }

                        if (jidx > 0) {
                            xb = WebService.L2_Check_Cold_Item_Handling_Compliance(ARegID, jidx, timestampstr);
                            if (xb != null && xb.Status != 0) {
                                xb.setExMessage(xb.Message);
                            } else if (xb == null) {
                                xb = new xwsBase();
                                if (utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0) {
                                    TemperaturePayload tpl = utd.TemperaturePayloads[utd.TemperaturePayloads.length - 1];
                                    if (tpl != null) {
                                        TempIsCompliant = temploggerclass.TempIsCompliant(tpl.Temperature);
                                        xb.setStatus(TempIsCompliant ? 0 : -1);
                                        xb.setMessage("L2_Check_Cold_Item_Handling_Compliance returns null.\nTemperature recorded: " + tpl.Temperature + ".\n"
                                                + (TempIsCompliant ? "Compliant." : "Non Compliant."));
                                        long tsl = tpl.TimestampLong;
                                        xb.setExMessage(TempIsCompliant ? "" :
                                                tpl.Temperature + "°C" + " ON " + objGlobalMethods.MillisecToDateStr2(tsl, "dd/MM/yyyy HH:mm"));
                                    } else {
                                        xb.setStatus(-1);
                                        xb.setMessage("L2_Check_Cold_Item_Handling_Compliance returns null..\nTemperaturePayload tpl is null");
                                        xb.setExMessage("L2_Check_Cold_Item_Handling_Compliance returns null..\nTemperaturePayload tpl is null");
                                    }
                                } else {
                                    xb.setStatus(-1);
                                    xb.setMessage("L2_Check_Cold_Item_Handling_Compliance returns null.\n!(utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0)");
                                    xb.setExMessage("L2_Check_Cold_Item_Handling_Compliance returns null.\n!(utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0)");
                                }
                            }
                        } else {
                            xb = new xwsBase();
                            if (utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0) {
                                TemperaturePayload tpl = utd.TemperaturePayloads[utd.TemperaturePayloads.length - 1];
                                if (tpl != null) {
                                    TempIsCompliant = temploggerclass.TempIsCompliant(tpl.Temperature);
                                    xb.setStatus(TempIsCompliant ? 0 : -1);
                                    xb.setMessage("Unable to call L2_Check_Cold_Item_Handling_Compliance. jidx == 0.\nTemperature recorded is " + tpl.Temperature + ".\n"
                                            + (TempIsCompliant ? "Compliant." : "Non Compliant."));
                                    long tsl = tpl.TimestampLong;
                                    xb.setExMessage(TempIsCompliant ? "" :
                                            tpl.Temperature + "°C" + " ON " + objGlobalMethods.MillisecToDateStr2(tsl, "dd/MM/yyyy HH:mm"));
                                } else {
                                    xb.setStatus(-1);
                                    xb.setMessage("Unable to call L2_Check_Cold_Item_Handling_Compliance. jidx == 0.\nTemperaturePayload tpl is null");
                                    xb.setExMessage("Unable to call L2_Check_Cold_Item_Handling_Compliance. jidx == 0.\nTemperaturePayload tpl is null");
                                }
                            } else {
                                xb.setStatus(-1);
                                xb.setMessage("Unable to call L2_Check_Cold_Item_Handling_Compliance. jidx == 0.\n!(utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0)");
                                xb.setExMessage("Unable to call L2_Check_Cold_Item_Handling_Compliance. jidx == 0.\n!(utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0)");
                            }
                        }
                    } else {
                        //do nothing?
                        xb = new xwsBase();
                        if (utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0) {
                            TemperaturePayload tpl = utd.TemperaturePayloads[utd.TemperaturePayloads.length - 1];
                            if (tpl != null) {
                                TempIsCompliant = temploggerclass.TempIsCompliant(tpl.Temperature);
                                xb.setStatus(TempIsCompliant ? 0 : -1);
                                xb.setMessage("L2_Upload_Temperature returns false.\nTemperature recorded is " + tpl.Temperature + ".\n"
                                        + (TempIsCompliant ? "Compliant." : "Non Compliant."));
                                long tsl = tpl.TimestampLong;
                                xb.setExMessage(TempIsCompliant ? "" :
                                        tpl.Temperature + "°C" + " ON " + objGlobalMethods.MillisecToDateStr2(tsl, "dd/MM/yyyy HH:mm"));
                            } else {
                                xb.setStatus(-1);
                                xb.setMessage("L2_Upload_Temperature returns false.\nTemperaturePayload tpl is null");
                                xb.setExMessage("L2_Upload_Temperature returns false.\nTemperaturePayload tpl is null");
                            }
                        } else {
                            xb.setStatus(-1);
                            xb.setMessage("L2_Upload_Temperature returns false.\n!(utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0)");
                            xb.setExMessage("L2_Upload_Temperature returns false.\n!(utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0)");
                        }
                    }
                } else {
                    xb = new xwsBase();
                    if (utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0) {
                        TemperaturePayload tpl = utd.TemperaturePayloads[utd.TemperaturePayloads.length - 1];
                        if (tpl != null) {
                            TempIsCompliant = temploggerclass.TempIsCompliant(tpl.Temperature);
                            xb.setStatus(TempIsCompliant ? 0 : -1);
                            xb.setMessage("No network to call L2_Upload_Temperature.\nTemperature recorded is " + tpl.Temperature + ".\n"
                                    + (TempIsCompliant ? "Compliant." : "Non Compliant."));
                            long tsl = tpl.TimestampLong;
                            xb.setExMessage(TempIsCompliant ? "" :
                                    tpl.Temperature + "°C" + " ON " + objGlobalMethods.MillisecToDateStr2(tsl, "dd/MM/yyyy HH:mm"));
                        } else {
                            xb.setStatus(-1);
                            xb.setMessage("No network to call L2_Upload_Temperature.\nTemperaturePayload tpl is null");
                            xb.setExMessage("No network to call L2_Upload_Temperature.\nTemperaturePayload tpl is null");
                        }
                    } else {
                        xb.setStatus(-1);
                        xb.setMessage("No network to call L2_Upload_Temperature.\n!(utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0)");
                        xb.setExMessage("No network to call L2_Upload_Temperature.\n!(utd != null && utd.TemperaturePayloads != null && utd.TemperaturePayloads.length > 0)");
                    }

                    JobsHandler.addUploadTempDataObjQue(objGlobalMethods.getUpdateJobInfoPreferences(), utd);
                    Context ctx = myApp.getAppContext();
                    ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInfoActivity uploadTemp_N_Compliance_Check doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return xb;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final xwsBase xb) {
            Context ctx = myApp.getAppContext();
            try {
                dismissTEmpCompCheckPD();
                if (xb == null) {
                    String[] prefName = new String[]{"FailAsND", "FailAsND_Reason"};
                    String[] prefvalue = new String[]{"", ""};
                    objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);

                    objGlobalMethods.writeToFile("Check_Cold_Item_Handling_Compliance xb is null.", "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.temp_recorded_proceed)).show();
                } else if (xb != null && xb.Status == 0) {
                    String[] prefName = new String[]{"FailAsND", "FailAsND_Reason"};
                    String[] prefvalue = new String[]{"", ""};
                    objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);

                    objGlobalMethods.writeToFile(xb.Message, "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.temp_recorded_proceed)).show();
                } else {
                    objGlobalMethods.writeToFile(xb.Message, "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.temp_not_compliant)).show();
                    objGlobalMethods.setBTLoggerRelatedPrefs(objGlobalMethods.getBTLoggerRelatedSharedPreferences(), "tplJsonStr", "");

                    String[] prefName = new String[]{"FailAsND", "FailAsND_Reason"};
                    String[] prefvalue = new String[]{"t", xb.ExMessage};
                    objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);

                    disableDeliveryButtons();
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInfoActivity uploadTemp_N_Compliance_Check onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    private void disableDeliveryButtons() {
        try {
            if (btnarr == null)
                btnarr = (Button) findViewById(R.id.btnarr);
            if (btnScanCN == null)
                btnScanCN = (Button) findViewById(R.id.btnScanCN);
            if (btnUpdateCN == null)
                btnUpdateCN = (Button) findViewById(R.id.btnUpdateCN);
            if (btnScanTag == null)
                btnScanTag = (Button) findViewById(R.id.btnScanTag);
            if (btnUpdateLabelTag == null)
                btnUpdateLabelTag = (Button) findViewById(R.id.btnUpdateLabelTag);
            if (btndip == null)
                btndip = (Button) findViewById(R.id.btndip);
            if (btnsubmit == null)
                btnsubmit = (Button) findViewById(R.id.btnsubmit);
            if (btnFail == null)
                btnFail = (Button) findViewById(R.id.btnFail);

            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnarr);
            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnScanCN);
            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnUpdateCN);
            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnScanTag);
            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnUpdateLabelTag);
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btndip);
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnsubmit);
            ctrlSettings.enableDisableFailButtonWithStyles(true, btnFail);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity disableDeliveryButtons. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at JobInfoActivity", "", "", "", "");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInfoActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }


}
