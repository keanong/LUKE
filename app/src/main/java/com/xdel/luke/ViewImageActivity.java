package com.xdel.luke;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xdel.luke.HRM_Leave.LeaveApplicationActivity;
import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.temploggerclass;

import java.util.Date;

//import android.support.v7.app.ActionBarActivity;


//public class ViewImageActivity extends ActionBarActivity {
public class ViewImageActivity extends AppCompatActivity {
    Context context;
    ImageView preview;
    private int catType;
    private ProgressDialog pd;
    private SharedPreferences GcmPreferences;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    Bitmap bitmap;
    private TextView lblimgPathIDX;

    String langType = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        GcmPreferences = getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");

        /*String languageToLoad = langType; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/

        //disable taking screen shot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_view_image);

        getSupportActionBar().hide();

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_wait_loadingIMG));
        preview = (ImageView) findViewById(R.id.preview);
        lblimgPathIDX = (TextView) findViewById(R.id.lblimgPathIDX);
    }

    @Override
    protected void onResume() {
        GcmPreferences = getGcmPreferences();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            setPic(extras.getString("imgPath"));
            lblimgPathIDX.setText(extras.getString("imgPathIDX"));

        } else {
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_nopic));
        }
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setPic(String imgPath) {
        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /*Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;*/

        /* Get the size of the ImageView */
        int targetW = preview.getWidth();
        int targetH = preview.getHeight();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);

        try {
            ExifInterface exif = new ExifInterface(imgPath);
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
            //if (orientation == 0 || orientation == 1) rotationAngle = 90;


            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bmOptions.outWidth, bmOptions.outHeight, matrix, true);
            /* Associate the Bitmap to the ImageView */
            preview.setImageBitmap(bitmap);
            preview.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(this, "There was a problem grabbing the photo to view: " + e.getMessage(), Toast.LENGTH_LONG).show();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            useridx = UsernamePwd[2];
            String stack = JobsHandler.getStackTraceString(e);

            Mail m = new Mail("luke@xdel.com", "XDel2015");
            objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", "There was a problem grabbing the photo to view: " + e.getMessage() + "\n\nStack: " + stack,
                    "There was a problem grabbing the photo by " + username + ", useridx: " + useridx + ".", null);
            sendmail.execute();

            Date dt = new Date();
            objGlobalMethods.writeToFile("There was a problem grabbing the photo to view. Driver: " + username,
                    "", "", username, useridx);
        }
    }

    private void showErrorAlert(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_nopic));
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                Intent i = null;
                catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                if (catType == 0) {
                    i = new Intent(ViewImageActivity.this, setFailStatusActivity.class);
                } else if (catType == 2) {
                    if (setDeliveredStatusActivity.isFromSetOKP) {
                        setDeliveredStatusActivity.isFromSetOKP = false;
                        i = new Intent(ViewImageActivity.this, setDeliveredStatusActivity.class);
                    } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                        setDeliveredMassStatusActivity.isFromMassSetOKP = false;
                        i = new Intent(ViewImageActivity.this, setDeliveredMassStatusActivity.class);
                    } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                        setDunningDeliveredStatusActivity.isFromSetDunningOKP = false;
                        i = new Intent(ViewImageActivity.this, setDunningDeliveredStatusActivity.class);
                    } else {
                        i = new Intent(ViewImageActivity.this, setFailStatusActivity.class);
                    }
                } else if (catType == -1) {
                    LeaveApplicationActivity.isFromLeaveApplication = false;
                    i = new Intent(ViewImageActivity.this, LeaveApplicationActivity.class);
                }
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                bitmap = null;
                MainActivity.uponLogin = 0;
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
        Intent parentIntent = null;

        parentIntent = new Intent(this, CustomGalleryActivity.class);
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    public void btnViewImageBackClick(View view) {
        onBackPressed();
    }

    public void btnViewImageDiscardClick(View view) {
        Date dt = new Date();
        lblimgPathIDX = (TextView) findViewById(R.id.lblimgPathIDX);

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.picker_confirm_to_delete));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "ok", update job status
                    objGlobalMethods.writeToFile("Attempt to delete image.","","","","");
                    if (CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1 && CheckListActivity.al != null && CheckListActivity.al.size() > 0) {
                        final int imgPathIDX = Integer.parseInt(lblimgPathIDX.getText().toString());
                        CheckListActivity.al.get(CheckListActivity.itemIndex).dataT.remove(imgPathIDX);
                        Intent parentIntent;
                        if (CheckListActivity.al.get(CheckListActivity.itemIndex).dataT != null && CheckListActivity.al.get(CheckListActivity.itemIndex).dataT.size() > 0) {
                            parentIntent = new Intent(ViewImageActivity.this, CustomGalleryActivity.class);
                        } else {
                            parentIntent = new Intent(ViewImageActivity.this, CheckListActivity.class);
                        }
                        CheckListActivity.openCamera = null;
                        CustomGalleryActivity.imagePaths = null;
                        CustomGalleryActivity.dataT = null;
                        startActivity(parentIntent);
                        ViewImageActivity.this.finish();
                    } else if (setFailStatusActivity.isFromSetFailSatus) {
                        if (setFailStatusActivity.imagePaths != null && setFailStatusActivity.imagePaths.size() > 0 && !lblimgPathIDX.getText().toString().equals("")) {
                            final int imgPathIDX = Integer.parseInt(lblimgPathIDX.getText().toString());
                            setFailStatusActivity.imagePaths.remove(imgPathIDX);
                            setFailStatusActivity.dataT.remove(imgPathIDX);
                            Intent parentIntent;
                            if (setFailStatusActivity.imagePaths != null && setFailStatusActivity.imagePaths.size() > 0 && setFailStatusActivity.dataT != null && setFailStatusActivity.dataT.size() > 0) {
                                parentIntent = new Intent(ViewImageActivity.this, CustomGalleryActivity.class);
                            } else {
                                parentIntent = new Intent(ViewImageActivity.this, setFailStatusActivity.class);
                            }

                            startActivity(parentIntent);
                            ViewImageActivity.this.finish();
                        } else {
                            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errordeletepic));
                        }
                    } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                        if (setDeliveredMassStatusActivity.imagePaths != null && setDeliveredMassStatusActivity.imagePaths.size() > 0 && !lblimgPathIDX.getText().toString().equals("")) {
                            final int imgPathIDX = Integer.parseInt(lblimgPathIDX.getText().toString());
                            setDeliveredMassStatusActivity.imagePaths.remove(imgPathIDX);
                            setDeliveredMassStatusActivity.dataT.remove(imgPathIDX);
                            Intent parentIntent;
                            if (setDeliveredMassStatusActivity.imagePaths != null && setDeliveredMassStatusActivity.imagePaths.size() > 0 && setDeliveredMassStatusActivity.dataT != null && setDeliveredMassStatusActivity.dataT.size() > 0) {
                                parentIntent = new Intent(ViewImageActivity.this, CustomGalleryActivity.class);
                            } else {
                                parentIntent = new Intent(ViewImageActivity.this, setDeliveredMassStatusActivity.class);
                            }

                            startActivity(parentIntent);
                            ViewImageActivity.this.finish();
                        } else {
                            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errordeletepic));
                        }
                    } else if (setDeliveredStatusActivity.isFromSetOKP) {
                        if (setDeliveredStatusActivity.imagePaths != null && setDeliveredStatusActivity.imagePaths.size() > 0 && !lblimgPathIDX.getText().toString().equals("")) {
                            final int imgPathIDX = Integer.parseInt(lblimgPathIDX.getText().toString());
                            setDeliveredStatusActivity.imagePaths.remove(imgPathIDX);
                            setDeliveredStatusActivity.dataT.remove(imgPathIDX);
                            Intent parentIntent;
                            if (setDeliveredStatusActivity.imagePaths != null && setDeliveredStatusActivity.imagePaths.size() > 0 && setDeliveredStatusActivity.dataT != null && setDeliveredStatusActivity.dataT.size() > 0) {
                                parentIntent = new Intent(ViewImageActivity.this, CustomGalleryActivity.class);
                            } else {
                                parentIntent = new Intent(ViewImageActivity.this, setDeliveredStatusActivity.class);
                            }

                            startActivity(parentIntent);
                            ViewImageActivity.this.finish();
                        } else {
                            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errordeletepic));
                        }
                    } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {

                        if (setDunningDeliveredStatusActivity.imagePaths != null && setDunningDeliveredStatusActivity.imagePaths.size() > 0 && !lblimgPathIDX.getText().toString().equals("")) {
                            final int imgPathIDX = Integer.parseInt(lblimgPathIDX.getText().toString());
                            setDunningDeliveredStatusActivity.imagePaths.remove(imgPathIDX);
                            setDunningDeliveredStatusActivity.dataT.remove(imgPathIDX);
                            Intent parentIntent;
                            if (setDunningDeliveredStatusActivity.imagePaths != null && setDunningDeliveredStatusActivity.imagePaths.size() > 0 &&
                                    setDunningDeliveredStatusActivity.dataT != null && setDunningDeliveredStatusActivity.dataT.size() > 0) {
                                parentIntent = new Intent(ViewImageActivity.this, CustomGalleryActivity.class);
                            } else {
                                parentIntent = new Intent(ViewImageActivity.this, setDunningDeliveredStatusActivity.class);
                            }

                            startActivity(parentIntent);
                            ViewImageActivity.this.finish();
                        } else {
                            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errordeletepic));
                        }
                    } else if (LeaveApplicationActivity.isFromLeaveApplication) {
                        if (LeaveApplicationActivity.imagePaths != null && LeaveApplicationActivity.imagePaths.size() > 0 && !lblimgPathIDX.getText().toString().equals("")) {
                            final int imgPathIDX = Integer.parseInt(lblimgPathIDX.getText().toString());
                            LeaveApplicationActivity.imagePaths.remove(imgPathIDX);
                            LeaveApplicationActivity.dataT.remove(imgPathIDX);
                            Intent parentIntent;
                            if (LeaveApplicationActivity.imagePaths != null && LeaveApplicationActivity.imagePaths.size() > 0 &&
                                    LeaveApplicationActivity.dataT != null && LeaveApplicationActivity.dataT.size() > 0) {
                                parentIntent = new Intent(ViewImageActivity.this, CustomGalleryActivity.class);
                            } else {
                                parentIntent = new Intent(ViewImageActivity.this, LeaveApplicationActivity.class);
                            }

                            startActivity(parentIntent);
                            ViewImageActivity.this.finish();
                        } else {
                            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errordeletepic));
                        }
                    } else {
                        if (CustomGalleryActivity.imagePaths != null && CustomGalleryActivity.imagePaths.size() > 0 && !lblimgPathIDX.getText().toString().equals("")) {
                            final int imgPathIDX = Integer.parseInt(lblimgPathIDX.getText().toString());
                            CustomGalleryActivity.imagePaths.remove(imgPathIDX);
                            CustomGalleryActivity.dataT.remove(imgPathIDX);
                            Intent parentIntent;
                            if (CustomGalleryActivity.imagePaths != null && CustomGalleryActivity.imagePaths.size() > 0 && CustomGalleryActivity.dataT != null && CustomGalleryActivity.dataT.size() > 0) {
                                parentIntent = new Intent(ViewImageActivity.this, CustomGalleryActivity.class);
                            } else {
                                parentIntent = new Intent(ViewImageActivity.this, setFailStatusActivity.class);
                            }

                            startActivity(parentIntent);
                            ViewImageActivity.this.finish();
                        } else {
                            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errordeletepic));
                        }
                    }
                }
            });
            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at ViewImageActivity btnViewImageDiscardClick. " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private SharedPreferences getGcmPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at ViewImageActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ViewImageActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
