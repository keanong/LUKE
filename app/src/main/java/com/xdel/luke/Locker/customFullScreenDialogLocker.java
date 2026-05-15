package com.xdel.luke.Locker;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.xdel.luke.HRM.objPassQR_IDbarcode;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.QR.Contents;
import com.xdel.luke.QR.QRCodeEncoder;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import static android.content.Context.WINDOW_SERVICE;

public class customFullScreenDialogLocker extends DialogFragment {
    private Toolbar toolbar;
    private LinearLayout LL_lockerjobinfo, LL_lockerjobinfo_auth;
    private TextView lblJobNo, lblPrivateKey, lblerrGenParcelIDBarcode, lblerrGenUserKeyBarcode;
    private Button btnAuth, btnInfo, btnNormBarcode, btnQRBarcode;
    private ImageView imgQR, imgParcelIDBarcode, imgPrivateKeyBarcode;
    private ProgressDialog pd;
    ScreenShotContentObserver screenShotContentObserver;

    public customFullScreenDialogLocker() {

    }

    public static customFullScreenDialogLocker display(FragmentManager fragmentManager) {
        customFullScreenDialogLocker fs = new customFullScreenDialogLocker();
        try {
            fs.show(fragmentManager, "customFullScreenDialogLocker_dialog");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker display. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return fs;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
        try {
            screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
            if (!objGlobalMethods.UserCanScreenShot(objGlobalMethods.getGcmPreferences())){
                myApp.getAppContext().getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker onCreate registerContentObserver. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            if (screenShotContentObserver != null)
                myApp.getAppContext().getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker onDestroy unregisterContentObserver. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);

        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            String[] prefName = new String[]{"lockermode", "authbarcode_loaded", "parcelidbarcode_loaded", "parcelidqr_loaded"};
            String[] prefvalue = new String[]{
                    "",
                    "",
                    "",
                    ""
            };
            objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker onDismiss. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_lockerjobprofile, container, false);
        try {
            toolbar = view.findViewById(R.id.toolbar);
            LL_lockerjobinfo_auth = view.findViewById(R.id.LL_lockerjobinfo_auth);
            LL_lockerjobinfo = view.findViewById(R.id.LL_lockerjobinfo);
            lblPrivateKey = view.findViewById(R.id.lblPrivateKey);
            lblJobNo = view.findViewById(R.id.lblJobNo);
            lblerrGenUserKeyBarcode = view.findViewById(R.id.lblerrGenUserKeyBarcode);
            lblerrGenParcelIDBarcode = view.findViewById(R.id.lblerrGenParcelIDBarcode);
            btnAuth = view.findViewById(R.id.btnAuth);
            btnInfo = view.findViewById(R.id.btnInfo);
            btnNormBarcode = view.findViewById(R.id.btnNormBarcode);
            btnQRBarcode = view.findViewById(R.id.btnQRBarcode);
            imgPrivateKeyBarcode = view.findViewById(R.id.imgPrivateKeyBarcode);
            imgQR = view.findViewById(R.id.imgQR);
            imgParcelIDBarcode = view.findViewById(R.id.imgParcelIDBarcode);
            pd = new ProgressDialog(myApp.currentActivity);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            String[] prefName = new String[]{"lockermode", "authbarcode_loaded", "parcelidbarcode_loaded", "parcelidqr_loaded"};
            String[] prefvalue = new String[]{
                    "",
                    "",
                    "",
                    ""
            };
            objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker onCreateView. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return view;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            toolbar.setNavigationOnClickListener(null);
            //toolbar.setTitle(myApp.getAppContext().getString(R.string.tonote));
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customFullScreenDialogLocker.this.dismiss();
                }
            });

            btnAuth.setOnClickListener(null);
            btnInfo.setOnClickListener(null);
            btnNormBarcode.setOnClickListener(null);
            btnQRBarcode.setOnClickListener(null);
            btnAuth.setOnClickListener(btnAuth_Click());
            btnInfo.setOnClickListener(btnInfo_Click());
            btnNormBarcode.setOnClickListener(genNormBarcode());
            btnQRBarcode.setOnClickListener(genQRBarcode());

            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            String lockermode = objGlobalMethods.getGCMPrefs(gcmp, "lockermode");
            String JobNo = objGlobalMethods.getGCMPrefs(gcmp, "currJobInfoJobNo");
            if (objGlobalMethods.IsEmptyString(JobNo)) {
                objGlobalMethods.writeToFile("JobNo is empty at customFullScreenDialogLocker onViewCreated. customFullScreenDialogLocker dismissed.", "", "", "", "");
                customFullScreenDialogLocker.this.dismiss();
            } else {
                if (objGlobalMethods.IsEmptyString(lockermode)) {
                    //authenticate mode
                    loadAuth();
                } else {
                    //parcel mode
                    loadInfo();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void loadAuth() {
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        try {
            objGlobalMethods.setGCMPrefs(gcmp, "lockermode", "");
            String L2_LA_GetUserResultJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "GetUserResultJsonStr");
            String authbarcode_loaded = objGlobalMethods.getGCMPrefs(gcmp, "authbarcode_loaded");
            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnInfo);
            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnAuth);
            LL_lockerjobinfo_auth.setVisibility(View.VISIBLE);
            LL_lockerjobinfo.setVisibility(View.GONE);
            imgParcelIDBarcode.setVisibility(View.GONE);
            imgQR.setVisibility(View.GONE);
            if (!objGlobalMethods.IsEmptyString(authbarcode_loaded)) {
                LL_lockerjobinfo.setVisibility(View.GONE);
                LL_lockerjobinfo_auth.setVisibility(View.VISIBLE);
                lblerrGenUserKeyBarcode.setVisibility(View.GONE);
                lblerrGenParcelIDBarcode.setVisibility(View.GONE);
                imgPrivateKeyBarcode.setVisibility(View.VISIBLE);
            } else if (!objGlobalMethods.IsEmptyString(L2_LA_GetUserResultJsonStr)) {
                Gson gson = new Gson();
                final L2_LA_GetUserResult res = gson.fromJson(L2_LA_GetUserResultJsonStr, L2_LA_GetUserResult.class);
                if (res != null) {
                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnAuth);
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnInfo);
                    lblPrivateKey.setText(res.User_Key);

                    new CountDownTimer(1500, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (pd != null && !pd.isShowing()) {
                                pd.show();
                            }
                        }

                        @Override
                        public void onFinish() {
                            GenNormBarcodeAysnc t = new GenNormBarcodeAysnc(res.User_Key, imgPrivateKeyBarcode);
                            t.execute();
                        }
                    }.start();
                } else {
                    objGlobalMethods.writeToFile("res is null at customFullScreenDialogLocker loadAuth. customFullScreenDialogLocker dismissed.", "", "", "", "");
                    customFullScreenDialogLocker.this.dismiss();
                }
            } else {
                objGlobalMethods.writeToFile("L2_LA_GetUserResultJsonStr is empty at customFullScreenDialogLocker loadAuth. customFullScreenDialogLocker dismissed.", "", "", "", "");
                customFullScreenDialogLocker.this.dismiss();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker loadAuth. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            customFullScreenDialogLocker.this.dismiss();
        }
    }

    private void loadInfo() {
        try {
            ctrlSettings.enableDisableCommonButtonWithStyles(true, btnAuth);
            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnInfo);
            LL_lockerjobinfo_auth.setVisibility(View.GONE);
            LL_lockerjobinfo.setVisibility(View.VISIBLE);
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(gcmp, "lockermode", "1");
            String JobNo = objGlobalMethods.getGCMPrefs(gcmp, "currJobInfoJobNo");
            lblJobNo.setText(JobNo);
            lblerrGenParcelIDBarcode.setVisibility(View.GONE);
            imgParcelIDBarcode.setVisibility(View.GONE);
            imgQR.setVisibility(View.GONE);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker loadInfo. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public View.OnClickListener genNormBarcode() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                    String parcelidbarcode_loaded = objGlobalMethods.getGCMPrefs(gcmp, "parcelidbarcode_loaded");
                    if (!objGlobalMethods.IsEmptyString(parcelidbarcode_loaded)) {
                        imgParcelIDBarcode.setVisibility(View.VISIBLE);
                        imgQR.setVisibility(View.GONE);
                    } else {
                        imgParcelIDBarcode.setVisibility(View.VISIBLE);
                        imgQR.setVisibility(View.GONE);
                        new CountDownTimer(1500, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (pd != null && !pd.isShowing()) {
                                    pd.show();
                                }
                            }

                            @Override
                            public void onFinish() {
                                GenNormBarcodeAysnc t = new GenNormBarcodeAysnc(lblJobNo.getText().toString(), imgParcelIDBarcode);
                                t.execute();
                            }
                        }.start();
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker genNormBarcode. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };

    }

    private class GenNormBarcodeAysnc extends AsyncTask<Void, Void, objPassQR_IDbarcode> {
        private final String IDval;
        private final ImageView iv;
        private SharedPreferences gcmp;

        public GenNormBarcodeAysnc(String aIDval, ImageView aiv) {
            this.IDval = aIDval;
            this.iv = aiv;
            this.gcmp = objGlobalMethods.getGcmPreferences();
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            if (pd != null && !pd.isShowing()) {
                pd.show();
            }
        }

        @Override
        protected objPassQR_IDbarcode doInBackground(Void... params) {
            objPassQR_IDbarcode obj = new objPassQR_IDbarcode();
            Bitmap bmp = null;
            try {
                if (gcmp == null) {
                    gcmp = objGlobalMethods.getGcmPreferences();
                }
                com.google.zxing.Writer writer = new MultiFormatWriter();
                String finaldata = Uri.encode(IDval, "utf-8");
                int height = iv.getHeight();
                int width = iv.getWidth();
                BitMatrix bm = writer.encode(finaldata, BarcodeFormat.CODE_128, width, height);
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                for (int i = 0; i < width; i++) {//width
                    for (int j = 0; j < height; j++) {//height
                        bmp.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                }
                obj.setIDbarcode(bmp);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker GenNormBarcodeAysnc doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return obj;
            }
        }

        @Override
        protected void onPostExecute(final objPassQR_IDbarcode res) {
            boolean err = false;
            boolean IDerr = false;
            Bitmap IDbarcode = null;
            String lockermode = objGlobalMethods.getGCMPrefs(gcmp, "lockermode");
            if (gcmp == null) {
                gcmp = objGlobalMethods.getGcmPreferences();
            }
            try {
                IDbarcode = res.getIDbarcode();
                IDerr = IDbarcode == null ? true : false;

                if (IDbarcode != null) {
                    iv.setImageBitmap(IDbarcode);
                    if (!objGlobalMethods.IsEmptyString(lockermode)) {
                        lblerrGenUserKeyBarcode.setVisibility(View.GONE);
                        lblerrGenParcelIDBarcode.setVisibility(View.GONE);
                        imgPrivateKeyBarcode.setVisibility(View.GONE);
                        imgParcelIDBarcode.setVisibility(View.VISIBLE);
                        imgQR.setVisibility(View.GONE);

                        objGlobalMethods.setGCMPrefs(gcmp, "parcelidbarcode_loaded", "t");
                    } else {
                        lblerrGenUserKeyBarcode.setVisibility(View.GONE);
                        lblerrGenParcelIDBarcode.setVisibility(View.GONE);
                        imgPrivateKeyBarcode.setVisibility(View.VISIBLE);
                        imgParcelIDBarcode.setVisibility(View.GONE);
                        imgQR.setVisibility(View.GONE);

                        objGlobalMethods.setGCMPrefs(gcmp, "authbarcode_loaded", "t");
                    }
                } else {
                    IDerr = true;
                    String[] prefName = new String[]{"authbarcode_loaded", "parcelidbarcode_loaded"};
                    String[] prefvalue = new String[]{
                            "",
                            ""
                    };
                    objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
                }

                pd.dismiss();
            } catch (Exception e) {
                if (!objGlobalMethods.IsEmptyString(lockermode)) {
                    objGlobalMethods.setGCMPrefs(gcmp, "parcelidbarcode_loaded", "");
                } else {
                    objGlobalMethods.setGCMPrefs(gcmp, "authbarcode_loaded", "");
                }
                err = true;
                objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker GenNormBarcodeAysnc onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                if (pd != null)
                    pd.dismiss();

                if (err || IDerr) {
                    if (objGlobalMethods.IsEmptyString(lockermode)) {
                        lblerrGenUserKeyBarcode.setVisibility(View.VISIBLE);
                    } else {
                        lblerrGenParcelIDBarcode.setVisibility(View.VISIBLE);
                    }
                    imgPrivateKeyBarcode.setVisibility(View.GONE);
                    imgParcelIDBarcode.setVisibility(View.GONE);
                    imgQR.setVisibility(View.GONE);
                }
            }
        }
    }

    public View.OnClickListener genQRBarcode() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                    String parcelidqr_loaded = objGlobalMethods.getGCMPrefs(gcmp, "parcelidqr_loaded");
                    imgParcelIDBarcode.setVisibility(View.GONE);
                    if (!objGlobalMethods.IsEmptyString(parcelidqr_loaded)) {
                        imgQR.setVisibility(View.VISIBLE);
                    } else {
                        imgQR.setVisibility(View.VISIBLE);
                        new CountDownTimer(1500, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (pd != null && !pd.isShowing()) {
                                    pd.show();
                                }
                            }

                            @Override
                            public void onFinish() {
                                genQRbarcodeAysnc t = new genQRbarcodeAysnc(lblJobNo.getText().toString(), imgQR);
                                t.execute();
                            }
                        }.start();
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker genQRBarcode. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

    private class genQRbarcodeAysnc extends AsyncTask<Void, Void, String> {
        private final String IDval;
        private final ImageView iv;
        private SharedPreferences gcmp;

        public genQRbarcodeAysnc(String aIDval, ImageView aiv) {
            this.IDval = aIDval;
            this.iv = aiv;
            this.gcmp = objGlobalMethods.getGcmPreferences();
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String res = "";
            try {
                WindowManager manager = (WindowManager) myApp.getAppContext().getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int width = point.x;
                int height = point.y;
                int smallerDimension = width < height ? width : height;
                smallerDimension = smallerDimension * 5 / 6;
                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(IDval,
                        null,
                        Contents.Type.TEXT,
                        BarcodeFormat.QR_CODE.toString(),
                        smallerDimension);
                Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                res = JobsHandler.convertBitmapToBase64(bitmap);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker genQRbarcodeAysnc doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final String res) {
            boolean err = false;
            boolean QRerr = false;
            String lockermode = objGlobalMethods.getGCMPrefs(gcmp, "lockermode");
            if (gcmp == null) {
                gcmp = objGlobalMethods.getGcmPreferences();
            }
            try {
                if (res != null && res.length() > 0) {
                    lblerrGenParcelIDBarcode.setVisibility(View.GONE);
                    imgParcelIDBarcode.setVisibility(View.GONE);
                    byte[] ress = Base64.decode(res, Base64.DEFAULT);
                    iv.setImageBitmap(BitmapFactory.decodeByteArray(ress, 0, ress.length));
                    iv.setVisibility(View.VISIBLE);
                    objGlobalMethods.setGCMPrefs(gcmp, "parcelidqr_loaded", "t");
                } else {
                    QRerr = true;
                    objGlobalMethods.setGCMPrefs(gcmp, "parcelidqr_loaded", "");
                }

                pd.dismiss();
            } catch (Exception e) {
                objGlobalMethods.setGCMPrefs(gcmp, "parcelidqr_loaded", "");
                err = true;
                objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker genQRbarcodeAysnc onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                if (pd != null)
                    pd.dismiss();
                if (err || QRerr) {
                    if (objGlobalMethods.IsEmptyString(lockermode)) {
                        lblerrGenUserKeyBarcode.setVisibility(View.VISIBLE);
                    } else {
                        lblerrGenParcelIDBarcode.setVisibility(View.VISIBLE);
                    }
                    imgPrivateKeyBarcode.setVisibility(View.GONE);
                    imgParcelIDBarcode.setVisibility(View.GONE);
                    imgQR.setVisibility(View.GONE);
                }
            }
        }
    }

    public View.OnClickListener btnAuth_Click() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loadAuth();
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker btnAuth_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };

    }

    public View.OnClickListener btnInfo_Click() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loadInfo();
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at customFullScreenDialogLocker btnInfo_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

}
