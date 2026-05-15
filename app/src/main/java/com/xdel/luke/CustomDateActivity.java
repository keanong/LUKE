package com.xdel.luke;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.xdel.luke.objClasses.Spinner_PostData;
import com.xdel.luke.objClasses.Spinner_PostItemAdapter;
import com.xdel.luke.objClasses.checklist_PostData;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.gvDatePickerDayAdapter2;
import com.xdel.luke.objClasses.gvDatePickerDay_PostData;
import com.xdel.luke.objClasses.gvDatePickerMthAdapter2;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CustomDateActivity extends AppCompatActivity {
    //private Integer checklistindex;
    public static checklist_PostData cldata;
    private static TextView lblestDate;
    private Button btnCancel;
    private static Button btnDateOK;
    private static GridView gvDays;
    private GridView gvMth;
    private Spinner ddlyear;
    ArrayList<Spinner_PostData> DatePickerYearAL;
    Context ctx;

    SimpleDateFormat Dateformatter = new SimpleDateFormat("dd/MM/yyyy");
    gvDatePickerMthAdapter2 mthAdapter;

    private static gvDatePickerDayAdapter2 dayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_datepicker);
        gvDays = (GridView) findViewById(R.id.gvDays);
        gvMth = (GridView) findViewById(R.id.gvMth);
        ddlyear = (Spinner) findViewById(R.id.ddlyear);
        lblestDate = (TextView) findViewById(R.id.lblestDate);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnDateOK = (Button) findViewById(R.id.btnOK);
        try {
            ctx = myApp.getAppContext();
            Date dt = new Date();
            Calendar c = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c2.setTime(dt);
            int selectedMth = -1;
            c.setTime(dt);
            loadDDLyearAL(ctx, c);
            loadgvMth(ctx);
            loadgvDay();
            displaySelectedPendingDate();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CustomDateActivity onCreate. Exception: " + e.getMessage().toString(), "", "", "", "");
            //set value to today date
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CustomDateActivity onResume. Exception: " + e.getMessage().toString(), "", "", "", "");
            //set value to today date
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (CheckListActivity.al != null && CheckListActivity.al.size() > 0 && cldata != null) {
                CheckListActivity.clearItemPendingData(cldata.index);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CustomDateActivity onBackPressed " + "Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            JobsHandler.sendErrorEmail("Exception at CustomDateActivity onBackPressed " + "Exception: " + e.getMessage().toString() + "\nStack: " + s);
        } finally {
            rtnToCheckList();
        }
    }

    private void loadDDLyearAL(Context actx, Calendar c) {
        DatePickerYearAL = new ArrayList<>();
        try {
            int currYear = c.get(Calendar.YEAR);

            Spinner_PostData DatePickerYearData;
            DatePickerYearData = new Spinner_PostData(myApp.getAppContext().getString(R.string.spinnerSelect), "-1", "-1", "");
            DatePickerYearAL.add(DatePickerYearData);
            DatePickerYearData = new Spinner_PostData(String.valueOf(currYear), String.valueOf(currYear), String.valueOf(currYear), "");
            DatePickerYearAL.add(DatePickerYearData);

            for (int x = 1; x <= 100; x++) {
                DatePickerYearData = new Spinner_PostData(String.valueOf(currYear - x), String.valueOf(currYear - x), String.valueOf(currYear - x), "");
                DatePickerYearAL.add(DatePickerYearData);
            }

            Spinner_PostItemAdapter DatePickerYear_adapter = new Spinner_PostItemAdapter(actx, DatePickerYearAL);
            ddlyear.setAdapter(DatePickerYear_adapter);
            ddlyear.setOnItemSelectedListener(DatePickerYear_SelectedIndexChanged());
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CustomDateActivity loadDDLyearAL. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static AdapterView.OnItemSelectedListener DatePickerYear_SelectedIndexChanged() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView lblDescEN = (TextView) view.findViewById(R.id.lblStatusCodesDescEN);
                try {
                    if (CheckListActivity.al != null && CheckListActivity.al.size() > 0) {
                        checklist_PostData d = CheckListActivity.al.get(cldata.index);
                        if (position == 0 && CheckListActivity.al != null && CheckListActivity.al.size() > 0) {
                            d.setSelectedDatePendingYear(null);
                            d.setSelectedDatePendingYearIndex(0);
                            CheckListActivity.al.set(cldata.index, d);
                        } else if (position > 0 && CheckListActivity.al != null && CheckListActivity.al.size() > 0) {
                            d.setSelectedDatePendingYear(Integer.parseInt(lblDescEN.getText().toString()));
                            d.setSelectedDatePendingYearIndex(position);
                            CheckListActivity.al.set(cldata.index, d);
                        }
                        loadgvDay();
                    }
                } catch (Exception e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter DatePickerYear_SelectedIndexChanged onItemSelected. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter DatePickerYear_SelectedIndexChanged onItemSelected. Exception: " + e.getMessage().toString(), "", "", "", "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private void loadgvMth(Context actx) {
        gvDatePickerDay_PostData gvdpdata;
        ArrayList<gvDatePickerDay_PostData> gvDatePickerMthAL = new ArrayList<gvDatePickerDay_PostData>();

        for (int x = 0; x <= 11; x++) {
            gvdpdata = new gvDatePickerDay_PostData(x + 1, false, true);
            gvDatePickerMthAL.add(gvdpdata);
        }

        mthAdapter = new gvDatePickerMthAdapter2(actx, gvDatePickerMthAL, cldata.index);
        gvMth.setAdapter(mthAdapter);
    }

    public static void loadgvDay() {
        int NoOfDaysInMth = 0;
        Date currdt = new Date();
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c2.setTime(dt);
        c2.set(Calendar.HOUR_OF_DAY, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);
        c2.set(Calendar.MILLISECOND, 0);
        currdt = c2.getTime();
        int currYear = c2.get(Calendar.YEAR);
        int currMth = c2.get(Calendar.MONTH);
        Boolean isFutureDate = false;
        gvDatePickerDay_PostData gvdpdata;
        ArrayList<gvDatePickerDay_PostData> gvDatePickerDayAL = new ArrayList<gvDatePickerDay_PostData>();

        try {
            c.set(Calendar.MONTH, cldata.SelectedDatePendingMth != null && cldata.SelectedDatePendingMth >= 0 ? cldata.SelectedDatePendingMth : currMth);
            c.set(Calendar.YEAR, cldata.SelectedDatePendingYear != null && cldata.SelectedDatePendingYear > 0 ? cldata.SelectedDatePendingYear : currYear);
            NoOfDaysInMth = cldata.SelectedDatePendingMth != null && cldata.SelectedDatePendingMth >= 0 ? c.getActualMaximum(Calendar.DAY_OF_MONTH) : 0;

            if (NoOfDaysInMth > 0) {
                for (int x = 0; x <= 30; x++) {
                    if (x <= NoOfDaysInMth - 1) {
                        c2.set(cldata.SelectedDatePendingYear != null && cldata.SelectedDatePendingYear > 0 ? cldata.SelectedDatePendingYear : currYear,
                                cldata.SelectedDatePendingMth != null && cldata.SelectedDatePendingMth >= 0 ? cldata.SelectedDatePendingMth : currMth, x + 1, 0, 0);
                        dt = c2.getTime();

                        //isFutureDate = cldata.SelectedDatePendingYear != null && cldata.SelectedDatePendingYear > 0 && (currdt.compareTo(dt) < 0 || currdt.compareTo(dt) == 0) ? true : false;
                        isFutureDate = cldata.SelectedDatePendingYear != null && cldata.SelectedDatePendingYear > 0 && currdt.compareTo(dt) < 0 ? true : false;
                        if (isFutureDate && cldata.SelectedDatePendingDay != null && cldata.SelectedDatePendingDay == x + 1) {
                            CheckListActivity.al.get(cldata.index).setSelectedDatePendingDay(null);
                        }
                        gvdpdata = new gvDatePickerDay_PostData(x + 1,
                                cldata.SelectedDatePendingDay != null && cldata.SelectedDatePendingDay == x + 1 && !isFutureDate ? true : false, isFutureDate ? false : true);
                    } else {
                        gvdpdata = new gvDatePickerDay_PostData(x + 1, false, false);
                    }
                    gvDatePickerDayAL.add(gvdpdata);
                }
            } else {
                for (int x = 0; x <= 30; x++) {
                    c2.set(cldata.SelectedDatePendingYear != null && cldata.SelectedDatePendingYear > 0 ? cldata.SelectedDatePendingYear : currYear,
                            cldata.SelectedDatePendingMth != null && cldata.SelectedDatePendingMth >= 0 ? cldata.SelectedDatePendingMth : currMth, x + 1, 0, 0);
                    gvdpdata = new gvDatePickerDay_PostData(x + 1, false, true);
                    gvDatePickerDayAL.add(gvdpdata);
                }
            }

            if (dayAdapter == null) {
                dayAdapter = new gvDatePickerDayAdapter2(myApp.getAppContext(), gvDatePickerDayAL, cldata.index);
                gvDays.setAdapter(dayAdapter);
            } else {
                dayAdapter.setItems(gvDatePickerDayAL);
                dayAdapter.notifyDataSetChanged();
            }
            displaySelectedPendingDate();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CustomDateActivity loadgvDay. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static void displaySelectedPendingDate() {
        Date dt = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int NoOfDaysInMth = 0;
        try {
            if (CheckListActivity.al != null && CheckListActivity.al.size() > 0 && cldata != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(dt);
                Integer PendingDay = cldata.SelectedDatePendingDay != null && cldata.SelectedDatePendingDay > 0 ? cldata.SelectedDatePendingDay : null;
                Integer PendingMth = cldata.SelectedDatePendingMth != null && cldata.SelectedDatePendingMth >= 0 ? cldata.SelectedDatePendingMth : null;
                Integer PendingYr = cldata.SelectedDatePendingYear != null && cldata.SelectedDatePendingYear > 0 ? cldata.SelectedDatePendingYear : null;

                if (PendingDay != null && PendingMth != null && PendingYr != null) {
                    //c.set(Calendar.DAY_OF_MONTH, PendingDay);
                    c.set(Calendar.DAY_OF_MONTH, 1);
                    c.set(Calendar.MONTH, PendingMth);
                    c.set(Calendar.YEAR, PendingYr);

                    NoOfDaysInMth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (PendingDay > NoOfDaysInMth) {
                        CheckListActivity.al.get(cldata.index).setSelectedDatePendingDay(null);
                        dt = null;
                    } else {
                        c.set(Calendar.DAY_OF_MONTH, PendingDay);
                        dt = c.getTime();
                        CheckListActivity.al.get(cldata.index).setSelectedDatePending(dt);
                    }
                } else {
                    dt = null;
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CustomDateActivity displaySelectedPendingDate. Exception: " + e.getMessage().toString(), "", "", "", "");
            dt = null;
        } finally {
            lblestDate.setText(dt == null ? "DD/MM/YYYY" : dateFormat.format(dt));
            ctrlSettings.enableDisableSubmitButtonWithStyles(dt == null ? false : true, btnDateOK);
        }
    }

    public void btnCancel_Click(View v) {
        onBackPressed();
    }

    public void btnDateOK_Click(View v) {
        try {
            if (CheckListActivity.al != null && CheckListActivity.al.size() > 0 && cldata != null) {

                if (cldata.SelectedDatePending != null && cldata.SelectedDatePendingDay != null && cldata.SelectedDatePendingMth != null && cldata.SelectedDatePendingYear != null) {
                    checklist_PostData d = CheckListActivity.al.get(cldata.index);
                    d.setSelectedDate(cldata.getSelectedDatePending());
                    if (cldata.OptionValue != null && cldata.OptionValue.size() > 0) {
                        d.OptionValue.clear();
                        d.OptionValue.add(Dateformatter.format(cldata.getSelectedDatePending()));
                    } else if (cldata.OptionValue != null && cldata.OptionValue.size() == 0) {
                        d.OptionValue.clear();
                        d.OptionValue.add(Dateformatter.format(cldata.getSelectedDatePending()));
                    } else if (cldata.OptionValue == null) {
                        d.OptionValue = new ArrayList<>();
                        d.OptionValue.add(Dateformatter.format(cldata.getSelectedDatePending()));
                    }

                    d.setSelectedDatePendingDay(null);
                    d.setSelectedDatePendingMth(null);
                    d.setSelectedDatePendingYear(null);
                    d.setSelectedDatePending(null);
                    d.setSelectedDatePendingYearIndex(0);
                    CheckListActivity.al.set(cldata.index, d);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CustomDateActivity btnDateOK_Click. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            JobsHandler.sendErrorEmail("Exception at CustomDateActivity btnDateOK_Click. Exception: " + e.getMessage().toString() + "\nStack: " + s);
        } finally {
            rtnToCheckList();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    private void rtnToCheckList(){
        lblestDate = null;
        dayAdapter = null;
        gvDays = null;
        cldata = null;
        btnDateOK = null;
        Intent i = new Intent(CustomDateActivity.this, CheckListActivity.class);
        startActivity(i);
        finish();
    }

}
