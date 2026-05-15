package com.xdel.luke.ppReader.ui;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.LocaleHelper;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.ppReader.mlkit.camera.CameraSource;
import com.xdel.luke.ppReader.mlkit.camera.CameraSourcePreview;
import com.xdel.luke.ppReader.mlkit.other.GraphicOverlay;
import com.xdel.luke.ppReader.mlkit.text.TextRecognitionProcessor;
import com.xdel.luke.ppReader.model.DocType;

import org.jmrtd.lds.icao.MRZInfo;

import java.io.IOException;
import java.util.Locale;

import kotlinx.coroutines.Job;

public class CaptureActivity extends AppCompatActivity implements TextRecognitionProcessor.ResultListener {

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    private ActionBar actionBar;

    public static final String MRZ_RESULT = "MRZ_RESULT";
    public static final String DOC_TYPE = "DOC_TYPE";

    private DocType docType = DocType.OTHER;

    private static String TAG = CaptureActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_title);

        if(getIntent().hasExtra(DOC_TYPE)) {
            docType = (DocType) getIntent().getSerializableExtra(DOC_TYPE);
            if(docType == DocType.PASSPORT) {
                actionBar.hide();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        preview = findViewById(R.id.camera_source_preview);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphics_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        createCameraSource();
        startCameraSource();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startCameraSource();
    }

/** Stops the camera. */

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
        /*Locale current = getResources().getConfiguration().getLocales().get(0);
        String lc = current.getLanguage();
        try {
            myApp.getInstance().initAppLanguage(getBaseContext());
            myApp.getInstance().initAppLanguage(myApp.getAppContext());
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    private void createCameraSource() {

        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
            cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
        }

        cameraSource.setMachineLearningFrameProcessor(new TextRecognitionProcessor(docType, this));
    }

    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onSuccess(MRZInfo mrzInfo) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(MRZ_RESULT, mrzInfo);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onError(Exception exp) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}

