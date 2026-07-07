package com.xdel.luke.objClasses;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.BottomSheet.tempLoggerEntryDialog;
import com.xdel.luke.Camera5Activity;
import com.xdel.luke.CheckListActivity;
import com.xdel.luke.CustomDateActivity;
import com.xdel.luke.CustomGallery;
import com.xdel.luke.CustomGalleryActivity;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.MainActivity;
import com.xdel.luke.OtpActivity;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.SlideDateTimePicker.SlideDateListener;
import com.xdel.luke.SlideDateTimePicker.SlideDateTimeListener;
import com.xdel.luke.setFailStatusActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by kean.ong on 06/07/2017.
 */
public class checklist_PostItemAdapter extends ArrayAdapter<checklist_PostData> {
    private ArrayList<checklist_PostData> list;
    private Context ctx;
    //public static boolean[] hidden = null;
    public static boolean[] hidden = null;
    public static AlertDialog alertD = null;
    SimpleDateFormat Dateformatter = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat DateTimeformatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
    ArrayList<Integer> hiddenPositions = null;
    public static Button btnDateOK;
    public int selectedShowParentPos = -1;
    public int selectedHideParentPos = -1;
    ArrayList<Integer> idxprocessed = null;

    public checklist_PostItemAdapter(Context context, ArrayList<checklist_PostData> checklistPostData) {
        super(context, 0, checklistPostData);
        list = checklistPostData;
        ctx = myApp.getAppContext();
        try {
            if (hidden == null) {
                hidden = new boolean[list.size()];
                //new code
                for (int i = 0; i <= list.size() - 1; i++) {
                    if (list.get(i).IsChild && list.get(i).ParentIDX > -1 && list.get(i).ParentIDX < i && !list.get(list.get(i).ParentIDX).IsChecked) {
                        hidden[i] = true;
                        list.get(i).setHidden(true);
                    }
                }
                //new code
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter checklist_PostItemAdapter. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

    }

    @Override
    public int getCount() {
        int hiddencount = getHiddenCount();
        return list.size() - hiddencount;
        //return list.size() - hiddenPositions.size();
    }

    private int getHiddenCount() {
        int count = 0;
        try {
            for (int i = 0; i < list.size(); i++)
                if (hidden[i])
                    count++;
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter getHiddenCount. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return count;
        }
        /*for (int i = 0; i < list.size(); i++)
            if (hidden[i])
                count++;
        return count;*/
    }

    public void hide(int position) {
        try {
            int pos = getRealPosition(position);
            hidden[pos] = true;
            if (list != null && list.size() > 0 && (pos >= 0 && pos <= list.size() - 1)) {
                list.get(pos).setHidden(true);
            }
            notifyDataSetChanged();
            notifyDataSetInvalidated();
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hide. Exception: " + e.getMessage(), "", "", "", "");
        }

    }

    private int getRealPosition(int position) {
        int hElements = getHiddenCountUpTo(position);
        int diff = 0;
        try {
            for (int i = 0; i < hElements; i++) {
                diff++;
                if (position + diff > hidden.length - 1) {
                    diff--;
                    break;
                }
                if (hidden[position + diff])
                    i--;
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter getRealPosition. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return (position + diff);
        }
    }

    private int getHiddenCountUpTo(int location) {
        int count = 0;
        try {
            for (int i = 0; i <= location; i++) {
                if (hidden[i])
                    count++;
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter getHiddenCountUpTo. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return count;
        }
    }

    private boolean isAYANFYFN(String c) {
        boolean asd = false;
        try {
            asd = (c.equals("AY") || c.equals("AN") || c.equals("FY") || c.equals("FN"));
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter isAYANFYFN. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean isPHPRSOIDV1RORD(String c) {
        boolean asd = false;
        try {
            asd = (c.equals("PH") || c.equals("PR") || c.equals("SO") || c.equals("ID") || c.equals("V1") || c.equals("V5") || c.equals("RO") || c.equals("RD"));
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter isPHPRSOIDV1RORD. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean isIDV1V2V3V4ROTL(String c) {
        boolean asd = false;
        try {
            asd = (c.equals("ID") || c.equals("V1") || c.equals("V2") || c.equals("V3") || c.equals("V4") || c.equals("V5") || c.equals("RO") || c.equals("TL"));
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter isIDV1V2V3V4ROTL. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean isPHPRSOIDV1RO(String c) {
        boolean asd = false;
        try {
            asd = (c.equals("PH") || c.equals("PR") || c.equals("SO") || c.equals("ID") || c.equals("V1") || c.equals("RO"));
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter isPHPRSOIDV1RO. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    private Boolean hasChild(int parentPos) {
        Boolean asd = false;
        try {
            for (int i = parentPos + 1; i <= list.size() - 1; i++) {
                if ((list.get(i).HierarchyCode % 10) == 0) {
                    break;
                }
                if ((list.get(i).HierarchyCode > 0 || list.get(i).HierarchyCode < 0) && (list.get(i).ParentIDX == parentPos)) {
                    asd = true;
                    break;
                }
                /*if (list.get(i).HierarchyCode > 0 || list.get(i).HierarchyCode < 0) {
                    asd = true;
                    break;
                }*/
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hasChild. Exception: " + e.getMessage(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    private boolean hasPositiveChild(int parentPos) {
        Boolean asd = false;
        try {
            for (int i = parentPos + 1; i <= list.size() - 1; i++) {
                if ((list.get(i).HierarchyCode % 10) == 0) {
                    break;
                }
                if ((list.get(i).HierarchyCode > 0) && (list.get(i).ParentIDX == parentPos)) {
                    asd = true;
                    break;
                }
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hasPositiveChild. Exception: " + e.getMessage(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    private int hasChildInYNRD(int parentPos) {
        int f = 0;
        try {
            for (int i = parentPos + 1; i <= list.size() - 1; i++) {
                if ((list.get(i).HierarchyCode % 10) == 0) {
                    break;
                }
                if (list.get(i).HierarchyCode > 0 && (list.get(i).ParentIDX == parentPos)) {
                    f++;
                    break;
                }
            }

            for (int i = parentPos + 1; i <= list.size() - 1; i++) {
                if ((list.get(i).HierarchyCode % 10) == 0) {
                    break;
                }
                if (list.get(i).HierarchyCode < 0 && (list.get(i).ParentIDX == parentPos)) {
                    f++;
                    break;
                }
            }

        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hasChildInYNRD. Exception: " + e.getMessage(), "", "", "", "");
            f = 0;
        } finally {
            return f;
        }
    }

    private boolean hasElderSibling(int currPos) {
        //type 1 == AY (any 1 is YES) 2 == AN (any 1 is NO) 3 == FY (all must be YES) 4 == FN (all must be NO)
        boolean asd = false;
        checklist_PostData fafyfn = null;
        checklist_PostData sibling = null;
        try {
            if (list != null && list.size() - 1 >= currPos && currPos - 1 >= 0) {
                fafyfn = list.get(currPos);
                if (fafyfn != null) {
                    for (int i = currPos - 1; i >= 0; i--) {
                        sibling = list.get(i);
                        if (sibling.HierarchyCode == fafyfn.HierarchyCode) {
                            asd = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter hasElderSibling. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    private void hide10to19(boolean hide, ChildViewHolder childViewHolder) {
        try {
            if (list != null) {
                checklist_PostData d = null;
                for (int i = 0; i <= list.size() - 1; i++) {
                    d = list.get(i);
                    if (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) {
                        hidden[i] = hide;
                        list.get(i).setHidden(hide);
                        if (hide) {
                            hideHierarchyNew(i, childViewHolder);
                        } else {
                            showHierarchyNew(i, null, null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hide10to19. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private boolean isYNRD(int i) {
        boolean asd = false;
        try {
            if (list != null && list.size() > 0 && i > 0 && i <= list.size() - 1) {
                asd = list.get(i).ControlCode.equals("YN") || list.get(i).ControlCode.equals("RD");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter isYNRD. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean isYNRD(checklist_PostData d) {
        boolean asd = false;
        try {
            asd = d != null && (d.ControlCode.equals("YN") || d.ControlCode.equals("RD"));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter isYNRD. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean isYNRDV1(checklist_PostData d) {
        boolean asd = false;
        try {
            asd = d != null && (d.ControlCode.equals("YN") || d.ControlCode.equals("RD") || d.ControlCode.equals("V1") || d.ControlCode.equals("V5"));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter isYNRDV1. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean isOV_YES(checklist_PostData d) {
        boolean asd = false;
        try {
            asd = d != null && (d.OptionValue != null && d.OptionValue.size() > 0 && d.OptionValue.get(0).equals("YES"));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter isOV_YES. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean isOV2_YES(checklist_PostData d) {
        boolean asd = false;
        try {
            asd = d != null && (d.OptionValue2 != null && d.OptionValue2.size() > 0 && d.OptionValue2.get(0).equals("YES"));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter isOV2_YES. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean isDirectChild(int parentPos, int idx) {
        boolean asd = false;
        checklist_PostData d = null;
        int c = 0;
        try {
            asd = list.get(idx).ParentIDX == parentPos;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter isDirectChild. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean isInnerChild(int parentPos, int idx) {
        boolean asd = false;
        int c = 0;
        checklist_PostData d = null;
        try {
            if (list != null && list.size() > 0) {
                for (int i = idx; i >= 0; i--) {
                    d = list.get(i);
                    if (d.ParentIDX > 0 && list.get(d.ParentIDX).ParentIDX > 0 && list.get(d.ParentIDX).ParentIDX == parentPos)
                        c++;

                    asd = c > 0;
                    if (asd)
                        break;
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter isInnerChild. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private void showHierarchy(int parentPos, ChildViewHolder childViewHolder, checklist_PostData data) {
        try {
            //for (int i = parentPos + 1; i <= list.size() - 1; i++) {
            for (int i = parentPos + 1; i <= list.size() - 1; i++) {
                if ((list.get(i).HierarchyCode % 10) == 0) {
                    break;
                }

                checklist_PostData d = list.get(i);
                if (hidden[i]) {
                    list.get(i).setAltColor(false);
                }
                list.get(i).setFinalManOptCode(d.ManOptCode.equals("MD") && d.HierarchyCode > 0 ? "MD" : "OP");
                list.get(i).setSelectedDate(d.HierarchyCode < 0 ? null : d.SelectedDate);
                list.get(i).setPhotoImagePath(d.HierarchyCode < 0 ? "" : d.PhotoImagePath);
                list.get(i).setDataT(d.HierarchyCode < 0 && hidden[i] ? null : d.dataT);
                list.get(i).OptionValue = (isPHPRSOIDV1RORD(d.ControlCode)) ? d.OptionValue : (d.HierarchyCode < 0) ? null : d.OptionValue;
                list.get(i).setIsChecked((isPHPRSOIDV1RORD(d.ControlCode)) ? true : d.HierarchyCode < 0 ? false : d.IsChecked);

                //type 1 == AY (any 1 is YES) 2 == AN (any 1 is NO) 3 == FY (all must be YES) 4 == FN (all must be NO)
                if (isAYANFYFN(d.ControlCode)) {
                    int fafyfnHierarchyCode = -999;
                    fafyfnHierarchyCode = d.HierarchyCode;
                    boolean hasElderSibling = hasElderSibling(i);
                    if (hasElderSibling) {
                        boolean canProceedToFail = canProceedToFail(i);
                        boolean show = showFAFYFN2(i, d.ControlCode.equals("AY") ? 1 : d.ControlCode.equals("AN") ? 2 : d.ControlCode.equals("FY") ? 3 : 4, d) && canProceedToFail;
                        hidden[i] = !show;
                        list.get(i).setHidden(!show);
                    } else {
                        hidden[i] = d.HierarchyCode < 0 ? true : false;
                        //list.get(i).setHidden(d.HierarchyCode < 0 ? true : false);
                        list.get(i).setHidden(hidden[i]);
                    }
                } else {
                    hidden[i] = (d.HierarchyCode < 0 && d.ParentIDX != parentPos)
                            || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && d.ParentIDX < parentPos && d.hidden)
                            || (d.HierarchyCode < 0 && d.ParentIDX == parentPos && list.get(parentPos).OptionValue != null && list.get(parentPos).OptionValue.get(0).equals("YES")) ? true : false;
                    //list.get(i).setHidden(d.HierarchyCode < 0 && d.ParentIDX != parentPos ? true : false);
                    list.get(i).setHidden(hidden[i]);
                    if (hidden[i] && d.ControlCode.equals("RO")) {
                        list.get(i).setOptionValue(null);
                        list.get(i).setOptionValue2(null);
                    }
                    if (hidden[i] && d.ControlCode.equals("RD")) {
                        list.get(i).setOptionValue(null);
                        list.get(i).setOptionValue2(null);
                    }
                    if (!hidden[i] && d.ControlCode.equals("RD")) {
                        if (list.get(i).OptionValue == null) {
                            list.get(i).OptionValue = new ArrayList<>();
                            list.get(i).OptionValue.add("NO");
                        }
                    }
                }

                /*list.get(i).setFinalManOptCode(list.get(i).ManOptCode.equals("MD") && list.get(i).HierarchyCode > 0 ? "MD" : "OP");
                list.get(i).setSelectedDate(list.get(i).HierarchyCode < 0 ? null : list.get(i).SelectedDate);
                list.get(i).setPhotoImagePath(list.get(i).HierarchyCode < 0 ? "" : list.get(i).PhotoImagePath);
                list.get(i).setDataT(list.get(i).HierarchyCode < 0 ? null : list.get(i).dataT);
                list.get(i).OptionValue = (isPHPRSO(d.ControlCode)) ? list.get(i).OptionValue : (list.get(i).HierarchyCode < 0) ? null : list.get(i).OptionValue;
                list.get(i).setIsChecked((isPHPRSO(d.ControlCode)) ? true : list.get(i).HierarchyCode < 0 ? false : list.get(i).IsChecked);*/
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter showHierarchy. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void hideHierarchy(int parentPos, ChildViewHolder childViewHolder) {
        try {
            for (int i = parentPos + 1; i <= list.size() - 1; i++) {
                if ((list.get(i).HierarchyCode % 10) == 0) {
                    break;
                }
                //hidden[i] = true;
                //list.get(i).setSelectedDate(null);
                //list.get(i).setPhotoImagePath("");
                //list.get(i).OptionValue = null;
                //list.get(i).setFinalManOptCode("OP");


                //new code for review, got issue
                /*checklist_PostData d = list.get(i);
                if (d.ParentIDX == parentPos) {
                    hidden[i] = list.get(i).HierarchyCode < 0 && list.get(parentPos).IsChecked ? false : true;
                    list.get(i).setSelectedDate(list.get(i).HierarchyCode < 0 ? list.get(i).SelectedDate : null);
                    list.get(i).setPhotoImagePath(list.get(i).HierarchyCode < 0 ? list.get(i).PhotoImagePath : "");
                    list.get(i).setDataT(list.get(i).HierarchyCode < 0 ? list.get(i).dataT : null);
                    list.get(i).OptionValue = list.get(i).HierarchyCode < 0 ? list.get(i).OptionValue : null;
                    list.get(i).setFinalManOptCode(list.get(i).HierarchyCode < 0 ? "MD" : "OP");
                    list.get(i).setIsChecked(list.get(i).HierarchyCode < 0 && list.get(i).IsChecked ? true : false);
                    if (list.get(i).FinalManOptCode.equals("MD") && list.get(i).HierarchyCode > 0) {
                        EnableDisableCtrlsNew(false, childViewHolder, list.get(i));
                    }
                }*/
                //new code for review, got issue
                checklist_PostData d = list.get(i);
                if (d.ParentIDX == parentPos) {
                    hidden[i] = d.HierarchyCode < 0 && list.get(parentPos).IsChecked ? false : true;
                    //list.get(i).setHidden(list.get(i).HierarchyCode < 0 && list.get(parentPos).IsChecked ? false : true);
                    list.get(i).setHidden(hidden[i]);
                    list.get(i).setSelectedDate(d.HierarchyCode < 0 ? d.SelectedDate : null);
                    list.get(i).setPhotoImagePath(d.HierarchyCode < 0 ? d.PhotoImagePath : "");
                    list.get(i).setDataT(d.HierarchyCode < 0 ? d.dataT : null);
                    list.get(i).OptionValue = (d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ? d.OptionValue : null;
                    list.get(i).setFinalManOptCode(d.HierarchyCode < 0 ? "MD" : "OP");
                    list.get(i).setIsChecked((d.HierarchyCode < 0 && d.IsChecked) || (isPHPRSOIDV1RORD(d.ControlCode)) ? true : false);
                    if (d.FinalManOptCode.equals("MD") && d.HierarchyCode > 0) {
                        EnableDisableCtrlsNew(false, childViewHolder, list.get(i));
                    }
                    if (d.ControlCode.equals("RD")) {
                        list.get(i).setOptionValue(null);
                        list.get(i).setOptionValue2(null);
                        String[] prefName = new String[]{"PreDeterminedStatusCodeIDX"};
                        String[] prefvalue = new String[]{""}; //PreDeterminedStatusCodeIDX 80 is Rejected Delivery
                        objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);
                    }
                } else if (d.ParentIDX != parentPos && isAYANFYFN(d.ControlCode)) {
                    //do nothing
                } else if (d.ParentIDX != parentPos && d.ParentIDX < parentPos && d.IsChild && d.ParentIDX > 0 && list.get(d.ParentIDX).IsChecked) {
                    //do nothing, do not hide
                    //this portion is a child, after another item that is both a child n a parent
                    hidden[i] = true;
                } else {
                    /*hidden[i] = list.get(i).HierarchyCode < 0 && list.get(parentPos).IsChecked ? false : true;
                    list.get(i).setHidden(list.get(i).HierarchyCode < 0 && list.get(parentPos).IsChecked ? false : true);*/
                    hidden[i] = (d.HierarchyCode < 0 && d.ParentIDX == parentPos) && list.get(parentPos).IsChecked ? false : true;
                    //list.get(i).setHidden((list.get(i).HierarchyCode < 0 && list.get(i).ParentIDX == parentPos) && list.get(parentPos).IsChecked ? false : true);
                    list.get(i).setHidden(hidden[i]);
                    list.get(i).setSelectedDate(d.HierarchyCode < 0 ? d.SelectedDate : null);
                    list.get(i).setPhotoImagePath((d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ? d.PhotoImagePath : "");
                    list.get(i).setDataT((d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ? d.dataT : null);
                    list.get(i).OptionValue = (d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ? d.OptionValue : null;
                    list.get(i).setFinalManOptCode(d.HierarchyCode < 0 ? "MD" : "OP");
                    list.get(i).setIsChecked((d.HierarchyCode < 0 && d.IsChecked) || (isPHPRSOIDV1RORD(d.ControlCode)) ? true : false);
                    if (d.FinalManOptCode.equals("MD") && d.HierarchyCode > 0) {
                        EnableDisableCtrlsNew(false, childViewHolder, list.get(i));
                    }
                }

                //original code
                /*hidden[i] = list.get(i).HierarchyCode < 0 && list.get(parentPos).IsChecked ? false : true;
                list.get(i).setSelectedDate(list.get(i).HierarchyCode < 0 ? list.get(i).SelectedDate : null);
                list.get(i).setPhotoImagePath(list.get(i).HierarchyCode < 0 ? list.get(i).PhotoImagePath : "");
                list.get(i).setDataT(list.get(i).HierarchyCode < 0 ? list.get(i).dataT : null);
                list.get(i).OptionValue = list.get(i).HierarchyCode < 0 ? list.get(i).OptionValue : null;
                list.get(i).setFinalManOptCode(list.get(i).HierarchyCode < 0 ? "MD" : "OP");
                list.get(i).setIsChecked(list.get(i).HierarchyCode < 0 && list.get(i).IsChecked ? true : false);
                if (list.get(i).FinalManOptCode.equals("MD") && list.get(i).HierarchyCode > 0) {
                    EnableDisableCtrlsNew(false, childViewHolder, list.get(i));
                }*/
                //original code
            }
            notifyDataSetChanged();

            /*final int selectionindex = parentPos;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    if (CheckListActivity.listView != null) {
                        if (selectionindex - 1 <= 0) {
                            CheckListActivity.listView.setSelection(0);
                        } else if (selectionindex - 1 > 0) {
                            CheckListActivity.listView.setSelection(selectionindex - 1);
                        }
                    }
                }
            }, 100);*/
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hideHierarchy. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void showHierarchyNew(int parentPos, ChildViewHolder childViewHolder, checklist_PostData data) {
        try {
            int HierarchyCodeP = -99;
            //for (int i = parentPos + 1; i <= list.size() - 1; i++) {
            for (int i = parentPos + 1; i <= list.size() - 1; i++) {
                if ((list.get(i).HierarchyCode % 10) == 0) {
                    break;
                }
                if (i == 8 || i == 10) {
                    HierarchyCodeP = -99;
                }

                checklist_PostData d = list.get(i);
                if (hidden[i]) {
                    list.get(i).setAltColor(false);
                }
                list.get(i).setFinalManOptCode(d.ManOptCode.equals("MD") && d.HierarchyCode > 0 ? "MD" : "OP");
                list.get(i).setSelectedDate(d.HierarchyCode < 0 ? null : d.SelectedDate);
                list.get(i).setPhotoImagePath(d.HierarchyCode < 0 ? "" : d.PhotoImagePath);
                list.get(i).setDataT(d.HierarchyCode < 0 && hidden[i] ? null : d.dataT);
                /*list.get(i).OptionValue = (isPHPRSOIDV1RORD(d.ControlCode)) ? d.OptionValue : (d.HierarchyCode < 0) ? null : d.OptionValue;
                list.get(i).setIsChecked((isPHPRSOIDV1RORD(d.ControlCode)) ? true : d.HierarchyCode < 0 ? false : d.IsChecked);*/
                if (d.IsChild && d.ControlCode.equals("YN")) {
                    d.OptionValue = new ArrayList<>();
                    d.OptionValue.add("NO");
                }
                list.get(i).OptionValue = (d.IsChild && d.ControlCode.equals("YN")) ? d.OptionValue : (isPHPRSOIDV1RORD(d.ControlCode)) ? d.OptionValue : (d.HierarchyCode < 0) ? null : d.OptionValue;
                list.get(i).setIsChecked((
                        isPHPRSOIDV1RORD(d.ControlCode)
                                || (d.IsChild && d.ControlCode.equals("YN"))
                                || (d.IsChild && d.ControlCode.equals("RG"))
                                || (d.IsChild && d.ControlCode.equals("V2"))
                                || (d.IsChild && d.ControlCode.equals("V3"))
                                || (d.IsChild && d.ControlCode.equals("V4"))
                ) ? true : d.HierarchyCode < 0 ? false : d.IsChecked);

                //type 1 == AY (any 1 is YES) 2 == AN (any 1 is NO) 3 == FY (all must be YES) 4 == FN (all must be NO)
                if (isAYANFYFN(d.ControlCode)) {
                    int fafyfnHierarchyCode = -999;
                    fafyfnHierarchyCode = d.HierarchyCode;
                    boolean hasElderSibling = hasElderSibling(i);
                    if (hasElderSibling) {
                        boolean canProceedToFail = canProceedToFail(i);
                        boolean show = showFAFYFN2(i, d.ControlCode.equals("AY") ? 1 : d.ControlCode.equals("AN") ? 2 : d.ControlCode.equals("FY") ? 3 : 4, d) && canProceedToFail;
                        hidden[i] = !show;
                        list.get(i).setHidden(!show);
                    } else {
                        hidden[i] = d.HierarchyCode < 0 ? true : false;
                        list.get(i).setHidden(hidden[i]);
                    }
                } else {
                    HierarchyCodeP = d.ParentIDX >= 0 ? list.get(d.ParentIDX).HierarchyCode : -99;
                    checklist_PostData par = list.get(d.ParentIDX);
                    hidden[i] = (d.HierarchyCode < 0 && d.ParentIDX != parentPos)
                            || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && d.ParentIDX < parentPos && d.hidden)
                            || (d.HierarchyCode < 0 && d.ParentIDX == parentPos && list.get(parentPos).OptionValue != null && list.get(parentPos).OptionValue.get(0).equals("YES")
                            || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && HierarchyCodeP != -99 && ((d.HierarchyCode - d.HierarchyCode - d.HierarchyCode) == HierarchyCodeP))
                            || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && (d.HierarchyCode - 1) == HierarchyCodeP && (par.ParentIDX != parentPos))
                            //|| (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && (d.HierarchyCode - 1) == HierarchyCodeP)
                            //|| (d.HierarchyCode > 0 && d.ParentIDX == parentPos && HierarchyCodeP < 0 && HierarchyCodeP != -99 && ((d.HierarchyCode - d.HierarchyCode - d.HierarchyCode) == HierarchyCodeP))
                    ) ? true : false;
                    list.get(i).setHidden(hidden[i]);
                    if (hidden[i] && d.ControlCode.equals("RO")) {
                        list.get(i).setOptionValue(null);
                        list.get(i).setOptionValue2(null);
                    }
                    if (hidden[i] && d.ControlCode.equals("RD")) {
                        list.get(i).setOptionValue(null);
                        list.get(i).setOptionValue2(null);
                    }
                    if (!hidden[i] && d.ControlCode.equals("RD")) {
                        if (list.get(i).OptionValue == null) {
                            list.get(i).OptionValue = new ArrayList<>();
                            list.get(i).OptionValue.add("NO");
                        }
                    }
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter showHierarchyNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void hideHierarchyNew(int parentPos, ChildViewHolder childViewHolder) {
        try {
            for (int i = parentPos + 1; i <= list.size() - 1; i++) {
                if ((list.get(i).HierarchyCode % 10) == 0) {
                    break;
                }
                if (i == 8 || i == 10) {
                    int HierarchyCodeP = -99;
                }

                checklist_PostData d = list.get(i);
                checklist_PostData par = list.get(parentPos);
                if (d.ParentIDX == parentPos) {
                    //hidden[i] = d.HierarchyCode < 0 && list.get(parentPos).IsChecked ? false : true;
                    hidden[i] = (d.HierarchyCode > 0 && d.IsChild && par.IsChecked && (par.ControlCode.equals("RD") || par.ControlCode.equals("YN")) && (par.OptionValue != null && par.OptionValue.get(0).equals("NO"))) ? true
                            : d.HierarchyCode < 0 && list.get(parentPos).IsChecked ? false : true;
                    list.get(i).setHidden(hidden[i]);
                    list.get(i).setSelectedDate(d.HierarchyCode < 0 ? d.SelectedDate : null);
                    list.get(i).setPhotoImagePath(d.HierarchyCode < 0 ? d.PhotoImagePath : "");
                    list.get(i).setDataT(d.HierarchyCode < 0 ? d.dataT : null);
                    list.get(i).OptionValue = (d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ? d.OptionValue : null;
                    list.get(i).setFinalManOptCode(d.HierarchyCode < 0 ? "MD" : "OP");
                    //list.get(i).setIsChecked((d.HierarchyCode < 0 && d.IsChecked) || (isPHPRSOIDV1RORD(d.ControlCode) || (d.IsChild && d.ControlCode.equals("YN"))) ? true : false);
                    list.get(i).setIsChecked(
                            (d.HierarchyCode < 0 && d.IsChecked)
                                    || (d.HierarchyCode < 0 && d.ControlCode.equals("RG") && par.ControlCode.equals("YN"))
                                    || (isPHPRSOIDV1RORD(d.ControlCode) || (d.IsChild && d.ControlCode.equals("YN"))) ? true : false);
                    if (d.FinalManOptCode.equals("MD") && d.HierarchyCode > 0) {
                        EnableDisableCtrlsNew(false, childViewHolder, list.get(i));
                    }
                    if (d.ControlCode.equals("RD")) {
                        list.get(i).setOptionValue(null);
                        list.get(i).setOptionValue2(null);
                        String[] prefName = new String[]{"PreDeterminedStatusCodeIDX"};
                        String[] prefvalue = new String[]{""}; //PreDeterminedStatusCodeIDX 80 is Rejected Delivery
                        objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);
                    }
                } else if (d.ParentIDX != parentPos && isAYANFYFN(d.ControlCode)) {
                    //do nothing
                } else if (d.ParentIDX != parentPos && d.ParentIDX < parentPos && d.IsChild && d.ParentIDX > 0 && list.get(d.ParentIDX).IsChecked) {
                    //do nothing, do not hide
                    //this portion is a child, after another item that is both a child n a parent
                    hidden[i] = true;
                } else {
                    //hidden[i] = (d.HierarchyCode < 0 && d.ParentIDX == parentPos) && list.get(parentPos).IsChecked ? false : true;
                    hidden[i] = ((d.HierarchyCode < 0 && d.ParentIDX == parentPos) && list.get(parentPos).IsChecked)
                            || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && list.get(d.ParentIDX).IsChecked
                            && list.get(d.ParentIDX).OptionValue != null && list.get(d.ParentIDX).OptionValue.size() > 0 && list.get(d.ParentIDX).OptionValue.get(0).equals("YES")) ? false : true;
                    list.get(i).setHidden(hidden[i]);
                    list.get(i).setSelectedDate(d.HierarchyCode < 0 ? d.SelectedDate : null);
                    list.get(i).setPhotoImagePath((d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ? d.PhotoImagePath : "");
                    list.get(i).setDataT((d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ? d.dataT : null);
                    //list.get(i).OptionValue = (d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ? d.OptionValue : null;
                    list.get(i).OptionValue =
                            (d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ||
                                    (d.HierarchyCode > 0 && d.IsChild && d.IsChecked && list.get(d.ParentIDX).IsChecked)
                                    ? d.OptionValue : null;
                    list.get(i).setFinalManOptCode(d.HierarchyCode < 0 ? "MD" : "OP");
                    //list.get(i).setIsChecked((d.HierarchyCode < 0 && d.IsChecked) || (isPHPRSOIDV1RORD(d.ControlCode)) ? true : false);
                    list.get(i).setIsChecked((d.HierarchyCode < 0 && d.IsChecked) || (isPHPRSOIDV1RORD(d.ControlCode) || (d.IsChild && d.ControlCode.equals("RG")) || (d.IsChild && d.ControlCode.equals("YN"))) ? true : false);
                    if (d.FinalManOptCode.equals("MD") && d.HierarchyCode > 0) {
                        EnableDisableCtrlsNew(false, childViewHolder, list.get(i));
                    }
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hideHierarchyNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void adjustHiddenFirst() {
        try {
            checklist_PostData d = null;
            String TypeOfPinVerification = "";
            //hide10to19(TypeOfPinVerification.equals("0") || TypeOfPinVerification.equals("1") ? true : false, childViewHolder);
            if (list != null && list.size() > 0) {
                for (int i = 0; i <= list.size() - 1; i++) {
                    d = list.get(i);
                    if (d.ControlCode.equals("OT")) {
                        if (d.OptionValue != null && d.OptionValue.size() > 0 && d.OptionValue.get(0) != null) {
                            TypeOfPinVerification = d.OptionValue.get(0);
                        }
                        if (TypeOfPinVerification.equals("0") || TypeOfPinVerification.equals("1"))
                            hide10to19New();

                    } else if (hasChild(i))
                        hideHierarchyOnly(i, null);
                }
            }
            String s = "";
            if (hidden != null && hidden.length > 0)
                s = Arrays.toString(hidden);
            Log.i("hidden", s);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter adjustHidden. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void showHierarchyOnly(int parentPos) {
        try {
            boolean asdasd = false;
            int HierarchyCodeP = -99;
            boolean pIsYNRD = false;
            boolean pYNRD_isOV_YES = false;

            checklist_PostData d = null;
            boolean dIsYNRD = false;
            boolean dYNRD_isOV_YES = false;

            checklist_PostData par = null;
            boolean parIsYNRD = false;
            boolean parYNRD_isOV_YES = false;

            checklist_PostData dirpar = null;
            boolean dirparIsYNRD = false;
            boolean dirparYNRD_isOV_YES = false;
            if (parentPos == 9) {
                Log.i("", "");
            }

            if (idxprocessed == null)
                idxprocessed = new ArrayList<>();

            for (int i = parentPos + 1; i <= list.size() - 1; i++) {
                if ((list.get(i).HierarchyCode % 10) == 0) {
                    break;
                }
                if (i == 8 || i == 10) {
                    HierarchyCodeP = -99;
                }

                d = list.get(i);
                dIsYNRD = isYNRDV1(d);
                dYNRD_isOV_YES = isOV_YES(d);

                par = list.get(parentPos);
                parIsYNRD = isYNRDV1(par);
                parYNRD_isOV_YES = isOV_YES(par);

                dirpar = d.ParentIDX > 0 ? list.get(d.ParentIDX) : null;
                dirparIsYNRD = isYNRDV1(dirpar);
                dirparYNRD_isOV_YES = isOV_YES(dirpar);

                if (d.ParentIDX == parentPos) {
                    if (d.HierarchyCode > 0) {
                        hidden[i] = (d.IsChild && dirpar.IsChecked && dirparIsYNRD && !dirparYNRD_isOV_YES);
                    } else {
                        hidden[i] = (d.IsChild && dirpar.IsChecked && dirparIsYNRD && dirparYNRD_isOV_YES && !hidden[d.ParentIDX]) || (d.IsChild && dirpar.IsChecked && dirparIsYNRD && !dirparYNRD_isOV_YES && hidden[d.ParentIDX]);
                    }
                    if (hasChild(i))
                        hideHierarchyOnly(i, null);
                }

                list.get(i).setHidden(hidden[i]);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter showHierarchyOnly. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void hide10to19New() {
        checklist_PostData d = null;
        try {
            idxprocessed = null;

            for (int i = 0; i <= list.size() - 1; i++) {
                d = list.get(i);
                if (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) {
                    hidden[i] = true;
                    list.get(i).setHidden(true);
                    if (hasChild(i))
                        hideHierarchyOnly(i, null);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hide10to19New. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void show10to19New() {
        checklist_PostData d = null;
        try {
            idxprocessed = null;

            for (int i = 0; i <= list.size() - 1; i++) {
                if (idxprocessed != null && idxprocessed.size() > 0 && idxprocessed.contains(i)) {
                    continue;
                } else {
                    d = list.get(i);
                    if (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) {
                        hidden[i] = false;
                        list.get(i).setHidden(false);
                        if (hasChild(i))
                            hideHierarchyOnly(i, null);
                    }
                }
            }
            /*for (int i = 0; i <= list.size() - 1; i++) {
                d = list.get(i);
                if (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) {
                    hidden[i] = false;
                    list.get(i).setHidden(false);
                    if (hasChild(i))
                        hideHierarchyOnly(i, null);
                }
            }*/
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hide10to19New. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void hideHierarchyOnly(int parentPos, ChildViewHolder childViewHolder) {
        try {
            boolean asdasd = false;
            int HierarchyCodeP = -99;
            boolean pIsYNRD = false;
            boolean pYNRD_isOV_YES = false;

            checklist_PostData d = null;
            boolean dIsYNRD = false;
            boolean dYNRD_isOV_YES = false;

            checklist_PostData par = null;
            boolean parIsYNRD = false;
            boolean parYNRD_isOV_YES = false;

            checklist_PostData dirpar = null;
            boolean dirparIsYNRD = false;
            boolean dirparYNRD_isOV_YES = false;
            if (parentPos == 9) {
                Log.i("", "");
            }

            if (idxprocessed == null)
                idxprocessed = new ArrayList<>();

            for (int i = parentPos + 1; i <= list.size() - 1; i++) {
                if ((list.get(i).HierarchyCode % 10) == 0) {
                    break;
                }
                if (i == 8 || i == 10) {
                    HierarchyCodeP = -99;
                }

                d = list.get(i);
                dIsYNRD = isYNRDV1(d);
                //dYNRD_isOV_YES = isOV_YES(d);
                dYNRD_isOV_YES = (dIsYNRD && d.ControlCode.equals("YN") || d.ControlCode.equals("RD")) ? isOV_YES(d) : (dIsYNRD && (d.ControlCode.equals("V1") || d.ControlCode.equals("V5"))) ? isOV2_YES(d) : false;

                par = list.get(parentPos);
                parIsYNRD = isYNRDV1(par);
                //parYNRD_isOV_YES = isOV_YES(par);
                parYNRD_isOV_YES = (parIsYNRD && par.ControlCode.equals("YN") || par.ControlCode.equals("RD")) ? isOV_YES(par) : (parIsYNRD && (par.ControlCode.equals("V1") || par.ControlCode.equals("V5"))) ? isOV2_YES(par) : false;

                dirpar = d.ParentIDX > 0 ? list.get(d.ParentIDX) : null;
                dirparIsYNRD = isYNRDV1(dirpar);
                //dirparYNRD_isOV_YES = isOV_YES(dirpar);
                dirparYNRD_isOV_YES = (dirparIsYNRD && dirpar.ControlCode.equals("YN") || dirpar.ControlCode.equals("RD")) ? isOV_YES(dirpar) : (dirparIsYNRD && (dirpar.ControlCode.equals("V1") || dirpar.ControlCode.equals("V5"))) ? isOV2_YES(dirpar) : false;

                if (d.ParentIDX == parentPos) {
                    if (d.HierarchyCode > 0) {
                        //hidden[i] = (d.IsChild && dirpar.IsChecked && dirparIsYNRD && !dirparYNRD_isOV_YES);
                        hidden[i] = (dirpar.IsChecked && dirparIsYNRD && !dirparYNRD_isOV_YES) || (!dirpar.IsChild && dirparIsYNRD && !dirparYNRD_isOV_YES);
                    } else {
                        hidden[i] = (d.IsChild && dirpar.IsChecked && dirparIsYNRD && dirparYNRD_isOV_YES && !hidden[d.ParentIDX]) || (d.IsChild && dirpar.IsChecked && dirparIsYNRD && !dirparYNRD_isOV_YES && hidden[d.ParentIDX]);
                    }

                    if (hidden[i]) {
                        list.get(i).setSelectedDate(d.HierarchyCode < 0 ? d.SelectedDate : null);
                        list.get(i).setPhotoImagePath(d.HierarchyCode < 0 ? d.PhotoImagePath : "");
                        list.get(i).setDataT(d.HierarchyCode < 0 ? d.dataT : null);
                        list.get(i).setIsChecked((d.HierarchyCode < 0 && d.IsChecked) || (isIDV1V2V3V4ROTL(d.ControlCode)) || (isPHPRSOIDV1RORD(d.ControlCode) || (d.IsChild && d.ControlCode.equals("RG")) || (d.IsChild && d.ControlCode.equals("YN"))) ? true : false);
                        list.get(i).OptionValue = null;
                        list.get(i).dataT = null;
                        list.get(i).setAltColor(false);
                        //list.get(i).OptionValue = (d.HierarchyCode < 0) || (isPHPRSOIDV1RO(d.ControlCode)) ? d.OptionValue : null;
                        //list.get(i).OptionValue = (d.HierarchyCode < 0 && dirparIsYNRD && !dirparYNRD_isOV_YES) || (isPHPRSOIDV1RO(d.ControlCode)) ? d.OptionValue : null;
                        if (d.ControlCode.equals("RD")) {
                            list.get(i).OptionValue2 = null;
                            String[] prefName = new String[]{"PreDeterminedStatusCodeIDX"};
                            String[] prefvalue = new String[]{""}; //PreDeterminedStatusCodeIDX 80 is Rejected Delivery
                            objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);
                        }
                    }

                    list.get(i).setHidden(hidden[i]);
                    idxprocessed.add(i);
                    if (hasChild(i))
                        hideHierarchyOnly(i, null);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hideHierarchyOnly. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void showHierarchyNew2(int parentPos, ChildViewHolder childViewHolder, checklist_PostData data) {
        try {
            int HierarchyCodeP = -99;
            boolean isDirectChild = false;
            boolean isInnerChild = false;

            checklist_PostData d = null;
            boolean dIsYNRD = false;
            boolean dYNRD_isOV_YES = false;

            checklist_PostData p = null;
            boolean pIsYNRD = false;
            boolean pYNRD_HASYES = false;

            checklist_PostData parr = null;
            boolean parrIsYNRD = false;
            boolean parrYNRD_HasYES = false;

            checklist_PostData par = null;
            boolean parIsYNRD = false;
            boolean parYNRD_isOV_YES = false;
            int pi = -1;
            int pppos = -1;

            for (int i = parentPos + 1; i <= list.size() - 1; i++) {
                if (pi == i) {
                    Log.i("pi", "pi = " + String.valueOf(i));
                    continue;
                }

                if (selectedHideParentPos > -1 && selectedHideParentPos == i) {
                    selectedHideParentPos = -1;
                    break;
                } else if (selectedShowParentPos > -1 && selectedShowParentPos == i) {
                    selectedShowParentPos = -1;
                    break;
                }
                if ((list.get(i).HierarchyCode % 10) == 0) {
                    break;
                }
                if (i == 8 || i == 10) {
                    HierarchyCodeP = -99;
                }

                d = list.get(i);
                dIsYNRD = isYNRD(d);
                //dYNRD_isOV_YES = isOV_YES(d);
                dYNRD_isOV_YES = (dIsYNRD && d.ControlCode.equals("YN") || d.ControlCode.equals("RD")) ? isOV_YES(d) : (dIsYNRD && (d.ControlCode.equals("V1") || d.ControlCode.equals("V5"))) ? isOV2_YES(d) : false;

                par = list.get(parentPos);
                parIsYNRD = isYNRD(par);
                //parYNRD_isOV_YES = isOV_YES(par);
                parYNRD_isOV_YES = (parIsYNRD && par.ControlCode.equals("YN") || par.ControlCode.equals("RD")) ? isOV_YES(par) : (parIsYNRD && (par.ControlCode.equals("V1") || par.ControlCode.equals("V5"))) ? isOV2_YES(par) : false;

                if (hidden[i]) {
                    list.get(i).setAltColor(false);
                }
                list.get(i).setFinalManOptCode(d.ManOptCode.equals("MD") && d.HierarchyCode > 0 ? "MD" : "OP");
                list.get(i).setSelectedDate(d.HierarchyCode < 0 ? null : d.SelectedDate);
                list.get(i).setPhotoImagePath(d.HierarchyCode < 0 ? "" : d.PhotoImagePath);
                list.get(i).setDataT(d.HierarchyCode < 0 && hidden[i] ? null : d.dataT);
                ////added on 6/8/2024 for multi parent child Hierarchy
                parr = d.ParentIDX > 0 ? list.get(d.ParentIDX) : null;
                boolean parrHasOpVal = parr != null && parr.OptionValue != null && parr.OptionValue.size() > 0;
                parrIsYNRD = isYNRD(parr);
                parrYNRD_HasYES = isOV_YES(parr);
                String parrYesNoOpVal = parrHasOpVal && parrIsYNRD ? parr.OptionValue.get(0) : "";

                if (d.IsParent && d.IsChild && d.ControlCode.equals("YN") && d.OptionValue != null && d.OptionValue.size() > 0 && d.OptionValue.get(0).equals("YES") &&
                        parrIsYNRD && parrHasOpVal && ((d.HierarchyCode < 0 && parrYesNoOpVal.equals("NO")) || (d.HierarchyCode > 0 && parrYesNoOpVal.equals("YES")))) {
                    ////do nothing
                    //String ppp = d.DisplayText;
                } else if (d.IsChild && d.ControlCode.equals("YN") && !hasPositiveChild(i) && d.OptionValue != null && d.OptionValue.size() > 0 && d.OptionValue.get(0).equals("YES")) {
                    //do nothing
                } else if (d.IsChild && d.ControlCode.equals("YN") && !hasChild(i)) {
                    //do nothing
                } else if (d.IsChild && d.ControlCode.equals("YN")) {
                    d.OptionValue = new ArrayList<>();
                    d.OptionValue.add("NO");
                }
                ////added on 6/8/2024 for multi parent child Hierarchy

                list.get(i).OptionValue = (d.IsChild && d.ControlCode.equals("YN")) ? d.OptionValue : (isPHPRSOIDV1RORD(d.ControlCode)) ? d.OptionValue : (d.HierarchyCode < 0) ? null : d.OptionValue;
                list.get(i).setIsChecked((
                        isPHPRSOIDV1RORD(d.ControlCode)
                                || (d.IsChild && d.ControlCode.equals("YN"))
                                || (d.IsChild && d.ControlCode.equals("RG"))
                                || (d.IsChild && d.ControlCode.equals("V2"))
                                || (d.IsChild && d.ControlCode.equals("V3"))
                                || (d.IsChild && d.ControlCode.equals("V4"))
                ) ? true : d.HierarchyCode < 0 ? false : d.IsChecked);

                //type 1 == AY (any 1 is YES) 2 == AN (any 1 is NO) 3 == FY (all must be YES) 4 == FN (all must be NO)
                if (isAYANFYFN(d.ControlCode)) {
                    int fafyfnHierarchyCode = -999;
                    fafyfnHierarchyCode = d.HierarchyCode;
                    boolean hasElderSibling = hasElderSibling(i);
                    if (hasElderSibling) {
                        boolean canProceedToFail = canProceedToFail(i);
                        boolean show = showFAFYFN2(i, d.ControlCode.equals("AY") ? 1 : d.ControlCode.equals("AN") ? 2 : d.ControlCode.equals("FY") ? 3 : 4, d) && canProceedToFail;
                        hidden[i] = !show;
                        list.get(i).setHidden(!show);
                    } else {
                        hidden[i] = d.HierarchyCode < 0 ? true : false;
                        list.get(i).setHidden(hidden[i]);
                    }
                } else {
                    HierarchyCodeP = d.ParentIDX >= 0 ? list.get(d.ParentIDX).HierarchyCode : -99;
                    p = list.get(d.ParentIDX);
                    pIsYNRD = isYNRD(p);
                    pYNRD_HASYES = pIsYNRD && isOV_YES(p);

                    if (d.ParentIDX == parentPos) {
                        hidden[i] = (d.HierarchyCode < 0 && d.ParentIDX != parentPos && d.ParentIDX < parentPos && d.hidden)
                                || ((d.HierarchyCode < 0 && d.ParentIDX == parentPos && list.get(parentPos).OptionValue != null && list.get(parentPos).OptionValue.get(0).equals("YES"))
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode + 1) - (d.HierarchyCode + 1) - (d.HierarchyCode + 1)) == HierarchyCodeP && pIsYNRD && pYNRD_HASYES)
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode + 1) - (d.HierarchyCode + 1) - (d.HierarchyCode + 1)) == HierarchyCodeP && (p.ParentIDX != parentPos) && pIsYNRD && !pYNRD_HASYES)
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && ((d.HierarchyCode + 1) == HierarchyCodeP) && (p.ParentIDX != parentPos) && pIsYNRD && pYNRD_HASYES)
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && ((d.HierarchyCode + 1) == HierarchyCodeP) && (p.ParentIDX != parentPos) && pIsYNRD && !pYNRD_HASYES)
                                || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && HierarchyCodeP != -99 && ((d.HierarchyCode - d.HierarchyCode - d.HierarchyCode) == HierarchyCodeP))
                                || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode - 1) == HierarchyCodeP) && (p.ParentIDX != parentPos) && pIsYNRD && !pYNRD_HASYES)
                        ) ? true : false;

                        if (i == 13 && hidden[i])
                            Log.i("", "line 1063, parentPos = " + parentPos);
                    } else {
                        hidden[i] = (d.HierarchyCode < 0 && d.ParentIDX != parentPos && d.ParentIDX < parentPos && d.hidden)
                                || ((d.HierarchyCode < 0 && d.ParentIDX == parentPos && list.get(parentPos).OptionValue != null && list.get(parentPos).OptionValue.get(0).equals("YES"))
                                //|| (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode + 1) - (d.HierarchyCode + 1) - (d.HierarchyCode + 1)) == HierarchyCodeP && pIsYNRD && pYNRD_HASYES)
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode + 1) - (d.HierarchyCode + 1) - (d.HierarchyCode + 1)) == HierarchyCodeP && pIsYNRD && !pYNRD_HASYES)
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && ((d.HierarchyCode + 1) == HierarchyCodeP) && pIsYNRD && pYNRD_HASYES)
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && ((d.HierarchyCode + 1) == HierarchyCodeP) && pIsYNRD && !pYNRD_HASYES)
                                || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && HierarchyCodeP != -99 && ((d.HierarchyCode - d.HierarchyCode - d.HierarchyCode) == HierarchyCodeP) && pIsYNRD && !pYNRD_HASYES)
                                || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode - 1) == HierarchyCodeP) && pIsYNRD && !pYNRD_HASYES)
                        ) ? true : false;

                        if (i == 13 && hidden[i])
                            Log.i("", "line 1073, parentPos = " + parentPos);
                    }

                    list.get(i).setHidden(hidden[i]);
                    if (hidden[i] && d.ControlCode.equals("RO")) {
                        list.get(i).setOptionValue(null);
                        list.get(i).setOptionValue2(null);
                    }
                    if (hidden[i] && d.ControlCode.equals("RD")) {
                        list.get(i).setOptionValue(null);
                        list.get(i).setOptionValue2(null);
                    }
                    if (!hidden[i] && d.ControlCode.equals("RD")) {
                        if (list.get(i).OptionValue == null) {
                            list.get(i).OptionValue = new ArrayList<>();
                            list.get(i).OptionValue.add("NO");
                        }
                    }
                }
                pi = i;
            }
            notifyDataSetChanged();


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter showHierarchyNew2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void hideHierarchyNew2(int parentPos, ChildViewHolder childViewHolder) {
        try {
            boolean asdasd = false;
            int HierarchyCodeP = -99;
            boolean pIsYNRD = false;
            boolean pYNRD_HASYES = false;

            checklist_PostData d = null;
            boolean dIsYNRD = false;
            boolean dYNRD_HasYES = false;

            checklist_PostData par = null;
            boolean parIsYNRD = false;
            boolean parYNRD_HasYES = false;

            checklist_PostData dirpar = null;
            boolean dirparIsYNRD = false;
            boolean dirparYNRD_HasYES = false;

            int pi = -1;

            for (int i = parentPos + 1; i <= list.size() - 1; i++) {
                if (pi == i) {
                    Log.i("pi", "pi = " + String.valueOf(i));
                    continue;
                }

                if (selectedHideParentPos > -1 && selectedHideParentPos == i) {
                    selectedHideParentPos = -1;
                    break;
                } else if (selectedShowParentPos > -1 && selectedShowParentPos == i) {
                    selectedShowParentPos = -1;
                    break;
                }
                if ((list.get(i).HierarchyCode % 10) == 0) {
                    break;
                }
                if (i == 8 || i == 10) {
                    HierarchyCodeP = -99;
                }

                d = list.get(i);
                dIsYNRD = isYNRD(d);
                dYNRD_HasYES = isOV_YES(d);

                par = list.get(parentPos);
                parIsYNRD = isYNRD(par);
                parYNRD_HasYES = isOV_YES(par);

                dirpar = d.ParentIDX > 0 ? list.get(d.ParentIDX) : null;
                dirparIsYNRD = isYNRD(dirpar);
                dirparYNRD_HasYES = isOV_YES(dirpar);

                if (d.ParentIDX == parentPos) {
                    //hidden[i] = d.HierarchyCode < 0 && list.get(parentPos).IsChecked ? false : true;
                    hidden[i] = (d.HierarchyCode > 0 && d.IsChild && par.IsChecked && (par.ControlCode.equals("RD") || par.ControlCode.equals("YN")) && (par.OptionValue != null && par.OptionValue.get(0).equals("NO"))) ? true
                            : d.HierarchyCode < 0 && list.get(parentPos).IsChecked ? false : true;

                    if (i == 13 && hidden[i])
                        Log.i("", "line 1157, parentPos = " + parentPos);

                    list.get(i).setHidden(hidden[i]);
                    list.get(i).setSelectedDate(d.HierarchyCode < 0 ? d.SelectedDate : null);
                    list.get(i).setPhotoImagePath(d.HierarchyCode < 0 ? d.PhotoImagePath : "");
                    list.get(i).setDataT(d.HierarchyCode < 0 ? d.dataT : null);
                    list.get(i).OptionValue = (d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ? d.OptionValue : null;
                    list.get(i).setFinalManOptCode(d.HierarchyCode < 0 ? "MD" : "OP");
                    //list.get(i).setIsChecked((d.HierarchyCode < 0 && d.IsChecked) || (isPHPRSOIDV1RORD(d.ControlCode) || (d.IsChild && d.ControlCode.equals("YN"))) ? true : false);
                    list.get(i).setIsChecked(
                            (d.HierarchyCode < 0 && d.IsChecked)
                                    || (d.HierarchyCode < 0 && d.ControlCode.equals("RG") && par.ControlCode.equals("YN"))
                                    || (isPHPRSOIDV1RORD(d.ControlCode) || (d.IsChild && d.ControlCode.equals("YN"))) ? true : false);
                    if (d.FinalManOptCode.equals("MD") && d.HierarchyCode > 0) {
                        EnableDisableCtrlsNew(false, childViewHolder, list.get(i));
                    }
                    if (d.ControlCode.equals("RD")) {
                        list.get(i).setOptionValue(null);
                        list.get(i).setOptionValue2(null);
                        String[] prefName = new String[]{"PreDeterminedStatusCodeIDX"};
                        String[] prefvalue = new String[]{""}; //PreDeterminedStatusCodeIDX 80 is Rejected Delivery
                        objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);
                    }
                } else if (d.ParentIDX != parentPos && isAYANFYFN(d.ControlCode)) {
                    //do nothing
                    String ss = d.DisplayTextCN;
                } else if (d.ParentIDX != parentPos && d.ParentIDX < parentPos && d.IsChild && d.ParentIDX > 0 && list.get(d.ParentIDX).IsChecked) {
                    //do nothing, do not hide
                    //this portion is a child, after another item that is both a child n a parent
                    hidden[i] = true;

                    if (i == 13 && hidden[i])
                        Log.i("", "line 1189, parentPos = " + parentPos);
                }
                ////added on 6/8/2024 for multi parent child Hierarchy
                ////to hide the item n if is grandchild YN RD, set as NO
                else if (d.ParentIDX != parentPos && d.ParentIDX > parentPos && d.IsChild && d.ParentIDX > 0 && list.get(d.ParentIDX).IsChecked
                        && parentPosIsUpperParent(i, parentPos)) {
                    HierarchyCodeP = d.ParentIDX >= 0 ? list.get(d.ParentIDX).HierarchyCode : -99;
                    checklist_PostData p = list.get(d.ParentIDX);
                    pIsYNRD = isYNRD(p);
                    pYNRD_HASYES = pIsYNRD && isOV_YES(p);

                    if (p.ParentIDX != parentPos) {
                        hidden[i] = (d.HierarchyCode < 0 && d.ParentIDX != parentPos && d.ParentIDX < parentPos && d.hidden)
                                || ((d.HierarchyCode < 0 && d.ParentIDX == parentPos && list.get(parentPos).OptionValue != null && list.get(parentPos).OptionValue.get(0).equals("YES"))
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode + 1) - (d.HierarchyCode + 1) - (d.HierarchyCode + 1)) == HierarchyCodeP && pIsYNRD && pYNRD_HASYES)
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode + 1) - (d.HierarchyCode + 1) - (d.HierarchyCode + 1)) == HierarchyCodeP && (p.ParentIDX != parentPos) && pIsYNRD && !pYNRD_HASYES)
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && ((d.HierarchyCode + 1) == HierarchyCodeP) && (p.ParentIDX != parentPos) && pIsYNRD && pYNRD_HASYES)
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && ((d.HierarchyCode + 1) == HierarchyCodeP) && (p.ParentIDX != parentPos) && pIsYNRD && !pYNRD_HASYES)
                                || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && HierarchyCodeP != -99 && ((d.HierarchyCode - d.HierarchyCode - d.HierarchyCode) == HierarchyCodeP))
                                || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode - 1) == HierarchyCodeP) && (p.ParentIDX != parentPos) && pIsYNRD && !pYNRD_HASYES)
                                || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && parIsYNRD && !parYNRD_HasYES)
                        ) ? true : false;

                        if (i == 13 && hidden[i])
                            Log.i("", "line 1213, parentPos = " + parentPos);
                    } else {
                        hidden[i] = (d.HierarchyCode < 0 && d.ParentIDX != parentPos && d.ParentIDX < parentPos && d.hidden)
                                || ((d.HierarchyCode < 0 && d.ParentIDX == parentPos && list.get(parentPos).OptionValue != null && list.get(parentPos).OptionValue.get(0).equals("YES"))
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode + 1) - (d.HierarchyCode + 1) - (d.HierarchyCode + 1)) == HierarchyCodeP && pIsYNRD && pYNRD_HASYES)
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode + 1) - (d.HierarchyCode + 1) - (d.HierarchyCode + 1)) == HierarchyCodeP && pIsYNRD && !pYNRD_HASYES)
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && ((d.HierarchyCode + 1) == HierarchyCodeP) && pIsYNRD && pYNRD_HASYES)
                                || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && ((d.HierarchyCode + 1) == HierarchyCodeP) && pIsYNRD && !pYNRD_HASYES)
                                || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && HierarchyCodeP != -99 && ((d.HierarchyCode - d.HierarchyCode - d.HierarchyCode) == HierarchyCodeP) && pIsYNRD && !pYNRD_HASYES)
                                || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode - 1) == HierarchyCodeP) && pIsYNRD && !pYNRD_HASYES)
                        ) ? true : false;

                        if (i == 13 && hidden[i])
                            Log.i("", "line 1226, parentPos = " + parentPos);
                    }

                    if (hidden[i] && selectedHideParentPos > -1 && i > selectedHideParentPos)
                        list.get(i).OptionValue = null;

                    list.get(i).setHidden(hidden[i]);
                } else if (d.ParentIDX != parentPos && d.ParentIDX > parentPos && d.IsChild && d.ParentIDX > 0 && list.get(d.ParentIDX).IsChecked
                        && parentPosIsDirectParent(i, parentPos)) {
                    HierarchyCodeP = d.ParentIDX >= 0 ? list.get(d.ParentIDX).HierarchyCode : -99;
                    checklist_PostData p = list.get(d.ParentIDX);
                    pIsYNRD = isYNRD(p);
                    pYNRD_HASYES = pIsYNRD && isOV_YES(p);
                    hidden[i] = (d.HierarchyCode < 0 && d.ParentIDX != parentPos && d.ParentIDX < parentPos && d.hidden)
                            || ((d.HierarchyCode < 0 && d.ParentIDX == parentPos && list.get(parentPos).OptionValue != null && list.get(parentPos).OptionValue.get(0).equals("YES"))
                            || (d.HierarchyCode < 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode + 1) - (d.HierarchyCode + 1) - (d.HierarchyCode + 1)) == HierarchyCodeP && pIsYNRD && pYNRD_HASYES)
                            || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP < 0 && HierarchyCodeP != -99 && ((d.HierarchyCode - d.HierarchyCode - d.HierarchyCode) == HierarchyCodeP))
                            || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && HierarchyCodeP >= 0 && ((d.HierarchyCode - 1) == HierarchyCodeP) && (p.ParentIDX == parentPos) && pIsYNRD && !pYNRD_HASYES)
                    ) ? true : false;

                    if (i == 13 && hidden[i])
                        Log.i("", "line 1247, parentPos = " + parentPos);
                    list.get(i).setHidden(hidden[i]);

                }
                ////added on 6/8/2024 for multi parent child Hierarchy
                ////to hide the item n if is grandchild YN RD, set as NO
                else {
                    //hidden[i] = (d.HierarchyCode < 0 && d.ParentIDX == parentPos) && list.get(parentPos).IsChecked ? false : true;
                    hidden[i] = ((d.HierarchyCode < 0 && d.ParentIDX == parentPos) && list.get(parentPos).IsChecked)
                            || (d.HierarchyCode > 0 && d.ParentIDX != parentPos && list.get(d.ParentIDX).IsChecked
                            && list.get(d.ParentIDX).OptionValue != null && list.get(d.ParentIDX).OptionValue.size() > 0 && list.get(d.ParentIDX).OptionValue.get(0).equals("YES")) ? false : true;

                    if (i == 13 && hidden[i])
                        Log.i("", "line 1260, parentPos = " + parentPos);
                    list.get(i).setHidden(hidden[i]);
                    list.get(i).setSelectedDate(d.HierarchyCode < 0 ? d.SelectedDate : null);
                    list.get(i).setPhotoImagePath((d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ? d.PhotoImagePath : "");
                    list.get(i).setDataT((d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ? d.dataT : null);
                    //list.get(i).OptionValue = (d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ? d.OptionValue : null;
                    list.get(i).OptionValue =
                            (d.HierarchyCode < 0) || (isPHPRSOIDV1RORD(d.ControlCode)) ||
                                    (d.HierarchyCode > 0 && d.IsChild && d.IsChecked && list.get(d.ParentIDX).IsChecked)
                                    ? d.OptionValue : null;
                    list.get(i).setFinalManOptCode(d.HierarchyCode < 0 ? "MD" : "OP");
                    //list.get(i).setIsChecked((d.HierarchyCode < 0 && d.IsChecked) || (isPHPRSOIDV1RORD(d.ControlCode)) ? true : false);
                    list.get(i).setIsChecked((d.HierarchyCode < 0 && d.IsChecked) || (isPHPRSOIDV1RORD(d.ControlCode) || (d.IsChild && d.ControlCode.equals("RG")) || (d.IsChild && d.ControlCode.equals("YN"))) ? true : false);
                    if (d.FinalManOptCode.equals("MD") && d.HierarchyCode > 0) {
                        EnableDisableCtrlsNew(false, childViewHolder, list.get(i));
                    }
                }
                pi = i;
            }
            notifyDataSetChanged();


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hideHierarchyNew2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private boolean parentPosIsDirectParent(int currIDX, int parentPos) {
        boolean asd = false;
        checklist_PostData d = null;
        int c = 0;

        try {
            for (int i = currIDX; i >= 0; i--) {
                d = list.get(i);
                if (d.ParentIDX > 0 && list.get(d.ParentIDX).ParentIDX > 0 && list.get(d.ParentIDX).ParentIDX == parentPos)
                    c++;

                if (c > 1)
                    break;
            }

            asd = c == 1;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter parentPosIsDirectParent. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean parentPosIsUpperParent(int currIDX, int parentPos) {
        boolean asd = false;
        checklist_PostData d = null;
        int c = 0;
        int pidx = 0;

        try {
            /*for (int x = currIDX; x >= 0; x--) {
                d = list.get(x);
                if (d.ParentIDX > 0 && list.get(d.ParentIDX).ParentIDX > 0 && list.get(d.ParentIDX).ParentIDX == parentPos)
                    c++;

                asd = c > 1;
                if (asd)
                    break;
            }*/
            for (int x = currIDX; x >= 0; x--) {
                d = list.get(x);
                pidx = d.ParentIDX >= 0 ? d.ParentIDX : 0;
                do {
                    if (list.get(pidx).ParentIDX >= 0 && list.get(pidx).ParentIDX == parentPos) {
                        c++;
                        pidx = -1;
                    } else if (list.get(pidx).ParentIDX >= 0) {
                        pidx = list.get(pidx).ParentIDX;
                    } else {
                        pidx = -1;
                    }
                } while (pidx >= 0);

                asd = c > 0;
                if (asd)
                    break;
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter parentPosIsUpperParent. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public final class ChildViewHolder {
        LinearLayout LLTypeRow1;
        LinearLayout LLDisplayText;
        LinearLayout LLDisplayTextCBArea;
        LinearLayout LLtxtValue;
        CheckBox cbMD;
        TextView lblText;
        LinearLayout LLOthers;
        LinearLayout LLOthersCBArea;
        ImageView imgRequired;
        CheckBox cbMD2;
        CheckBox cb;
        TextView lblValue;
        EditText txtValue;
        EditText txtValue2;
        TextView lbldegc;
        TextView lbltempTS;
        RadioGroup rg1;
        LinearLayout LLCheckbox;
        LinearLayout LLDateTxtB;
        LinearLayout LLDate;
        LinearLayout LLDateTime;
        LinearLayout LLPhoto;
        LinearLayout LLIDVerification;
        LinearLayout LLIMEIVerification;
        LinearLayout LLArticleTag;
        LinearLayout LLUpdatePieces;
        Switch yesno;
        Switch yesnoIDVerificationSkip;

        TextView lblIDVerificationVal;
        EditText txtVerification;
        ImageView imgVerification;
        Button btnVerification;
        TextView lblenterlast;
        Button btnOTP;

        TextView lblIMEIVerificationVal;
        TextView lblIMVCFirstFewChar;
        EditText txtIMEIVerification;
        ImageView imgIMVCVerification;
        Button btnVerificationIMEI;
        EditText txtLabelTagVal;
        EditText txtLabelUpdatePiecesVal;
        Button btnScanTag;
        Button btnUpdateLabelTag;

        EditText txtdd;
        EditText txtmm;
        EditText txtyyyy;

        ImageView imgbtnDate;
        TextView lblDate;
        ImageView imgbtnDateTimeTime;
        TextView lblDateTimeTime;

        TextView lblphotorequiredlabel;
        CheckBox cbPhoto;
        ImageView imgPhoto;
        ImageView imgCancelPhoto;

        TextWatcher txtValueTextWatcher;
        TextWatcher txtValueTextWatcher2;
        TextWatcher txtLabelUpdatePiecesValTextWatcher;
        InputFilter inputFilter;
        ROtxtValueTextChangeListener rotxtValueTextChangeListener;
        CompoundButton.OnCheckedChangeListener cbMDOnCheckedChangeListener;
        CompoundButton.OnCheckedChangeListener yesnoOnCheckedChangeListener;
        CompoundButton.OnCheckedChangeListener yesnoOnCheckedChangeListenerSub;
        View.OnClickListener DateOnClickListener;
        View.OnClickListener OpenCameraOnClickListener;
        View.OnClickListener ViewPhotoOnClickListener;
        View.OnClickListener BtnVerificationOnClickListener;
        View.OnClickListener BtnV2VerificationOnClickListener;
        View.OnClickListener BtnV3VerificationOnClickListener;
        View.OnClickListener BtnV4VerificationOnClickListener;
        View.OnClickListener BtnV5VerificationOnClickListener;
        View.OnClickListener btnScanTagOnClickListener;
        View.OnClickListener btnUpdateLabelTagOnClickListener;
        SlideDateListener slideDateListener;
        SlideDateTimeListener slideDateTimeListener;
        View.OnClickListener lblDateTimeTimeOnClickListener;
        View.OnClickListener OTPOnClickListener;
        View.OnClickListener SignatureOnClickListener;

        ArrayList<gvDatePickerDay_PostData> gvDatePickerDayAL;
        ArrayList<gvDatePickerDay_PostData> gvDatePickerMthAL;

        CompoundButton.OnCheckedChangeListener cbForLabelOnCheckedChangeListener;
        CompoundButton.OnCheckedChangeListener cbForTextboxOnCheckedChangeListener;
        CompoundButton.OnCheckedChangeListener cbForCheckboxOnCheckedChangeListener;
        CompoundButton.OnCheckedChangeListener cbForCheckboxGroupOnCheckedChangeListener;
        CompoundButton.OnCheckedChangeListener cbForRadioGroupOnCheckedChangeListener;
        CompoundButton.OnCheckedChangeListener cbForDateOnCheckedChangeListener;
        CompoundButton.OnCheckedChangeListener cbForDateTimeOnCheckedChangeListener;
        CompoundButton.OnCheckedChangeListener cbForYesNoOnCheckedChangeListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //final ChildViewHolder childViewHolder;
        ChildViewHolder childViewHolder;
        final int pos = position;
        final int realpos = getRealPosition(position);
        /*int realpos = position;
        if (hiddenPositions.contains(position)) {
            do {
                realpos ++;
            } while (hiddenPositions.contains(realpos));
        }*/

        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.checklist_item, parent, false);
                childViewHolder = new ChildViewHolder();
                childViewHolder.LLTypeRow1 = (LinearLayout) convertView.findViewById(R.id.LLTypeRow1);
                childViewHolder.LLDisplayText = (LinearLayout) convertView.findViewById(R.id.LLDisplayText);
                childViewHolder.LLDisplayTextCBArea = (LinearLayout) convertView.findViewById(R.id.LLDisplayTextCBArea);
                childViewHolder.LLtxtValue = (LinearLayout) convertView.findViewById(R.id.LLtxtValue);
                childViewHolder.cbMD = (CheckBox) convertView.findViewById(R.id.cbMD);
                childViewHolder.lblText = (TextView) convertView.findViewById(R.id.lblText);
                childViewHolder.LLOthers = (LinearLayout) convertView.findViewById(R.id.LLOthers);
                childViewHolder.LLOthersCBArea = (LinearLayout) convertView.findViewById(R.id.LLOthersCBArea);
                childViewHolder.cbMD2 = (CheckBox) convertView.findViewById(R.id.cbMD2);
                childViewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb);
                childViewHolder.lblValue = (TextView) convertView.findViewById(R.id.lblValue);
                childViewHolder.txtValue = (EditText) convertView.findViewById(R.id.txtValue);
                childViewHolder.txtValue2 = (EditText) convertView.findViewById(R.id.txtValue2);
                childViewHolder.lbldegc = (TextView) convertView.findViewById(R.id.lbldegc);
                childViewHolder.lbltempTS = (TextView) convertView.findViewById(R.id.lbltempTS);
                childViewHolder.rg1 = (RadioGroup) convertView.findViewById(R.id.rg1);
                childViewHolder.LLCheckbox = (LinearLayout) convertView.findViewById(R.id.LLCheckbox);
                childViewHolder.LLDateTxtB = (LinearLayout) convertView.findViewById(R.id.LLDateTxtB);
                childViewHolder.LLDate = (LinearLayout) convertView.findViewById(R.id.LLDate);
                childViewHolder.LLDateTime = (LinearLayout) convertView.findViewById(R.id.LLDateTime);
                childViewHolder.LLPhoto = (LinearLayout) convertView.findViewById(R.id.LLPhoto);
                childViewHolder.LLIDVerification = (LinearLayout) convertView.findViewById(R.id.LLIDVerification);
                childViewHolder.yesno = (Switch) convertView.findViewById(R.id.yesno);
                childViewHolder.yesnoIDVerificationSkip = (Switch) convertView.findViewById(R.id.yesnoIDVerificationSkip);

                childViewHolder.imgbtnDate = (ImageView) convertView.findViewById(R.id.imgbtnDate);
                childViewHolder.lblDate = (TextView) convertView.findViewById(R.id.lblDate);
                childViewHolder.imgbtnDateTimeTime = (ImageView) convertView.findViewById(R.id.imgbtnDateTimeTime);
                childViewHolder.lblDateTimeTime = (TextView) convertView.findViewById(R.id.lblDateTimeTime);

                childViewHolder.lblphotorequiredlabel = (TextView) convertView.findViewById(R.id.lblphotorequiredlabel);
                childViewHolder.cbPhoto = (CheckBox) convertView.findViewById(R.id.cbPhoto);
                childViewHolder.imgPhoto = (ImageView) convertView.findViewById(R.id.imgPhoto);
                childViewHolder.imgCancelPhoto = (ImageView) convertView.findViewById(R.id.imgCancelPhoto);

                childViewHolder.lblIDVerificationVal = (TextView) convertView.findViewById(R.id.lblIDVerificationVal);
                childViewHolder.txtVerification = (EditText) convertView.findViewById(R.id.txtVerification);
                childViewHolder.imgVerification = (ImageView) convertView.findViewById(R.id.imgVerification);
                childViewHolder.btnVerification = (Button) convertView.findViewById(R.id.btnVerification);
                childViewHolder.lblenterlast = (TextView) convertView.findViewById(R.id.lblenterlast);


                childViewHolder.LLIMEIVerification = (LinearLayout) convertView.findViewById(R.id.LLIMEIVerification);
                childViewHolder.lblIMEIVerificationVal = (TextView) convertView.findViewById(R.id.lblIMEIVerificationVal);
                childViewHolder.lblIMVCFirstFewChar = (TextView) convertView.findViewById(R.id.lblIMVCFirstFewChar);
                childViewHolder.txtIMEIVerification = (EditText) convertView.findViewById(R.id.txtIMEIVerification);
                childViewHolder.imgIMVCVerification = (ImageView) convertView.findViewById(R.id.imgIMVCVerification);
                childViewHolder.btnVerificationIMEI = (Button) convertView.findViewById(R.id.btnVerificationIMEI);

                childViewHolder.LLArticleTag = (LinearLayout) convertView.findViewById(R.id.LLArticleTag);
                childViewHolder.txtLabelTagVal = (EditText) convertView.findViewById(R.id.txtLabelTagVal);
                childViewHolder.btnScanTag = (Button) convertView.findViewById(R.id.btnScanTag);
                childViewHolder.btnUpdateLabelTag = (Button) convertView.findViewById(R.id.btnUpdateLabelTag);

                childViewHolder.LLUpdatePieces = (LinearLayout) convertView.findViewById(R.id.LLUpdatePieces);
                childViewHolder.txtLabelUpdatePiecesVal = (EditText) convertView.findViewById(R.id.txtLabelUpdatePiecesVal);


                childViewHolder.btnOTP = (Button) convertView.findViewById(R.id.btnOTP);

                convertView.setTag(childViewHolder);
            } else {
                childViewHolder = (ChildViewHolder) convertView.getTag();
            }

            final checklist_PostData data = list.get(realpos);
            hideshowControlsNew(childViewHolder, data, realpos);

            convertView.clearFocus();
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter getView. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter getView. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
        //// Return the completed view to render on screen
        boolean[] h = hidden;
        return convertView;
    }

    private void hideshowControlsNew(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            Boolean showgray = false;
            int showncount = -1;
            for (int i = 0; i <= position; i++) {
                if (!hidden[i]) {
                    showncount++;
                    if (showncount % 2 == 0 && position == i) {
                        showgray = true;
                        break;
                    }
                }

            }

            //childViewHolder.LLTypeRow1.setBackgroundResource(showgray ? R.drawable.edittextstylelightgray : R.drawable.edittextstyle);
            childViewHolder.LLTypeRow1.setBackgroundResource(showgray ? R.drawable.bg_borderless_lightgray : R.drawable.bg_borderless_white);
            childViewHolder.LLDisplayTextCBArea.setBackgroundResource(data.AltColor ? R.drawable.bg_borderless_red : showgray ? R.drawable.bg_borderless_lightgray : R.drawable.bg_borderless_white);
            childViewHolder.LLOthersCBArea.setBackgroundResource(data.AltColor ? R.drawable.bg_borderless_red : showgray ? R.drawable.bg_borderless_lightgray : R.drawable.bg_borderless_white);

            /*if (data.AltColor) {
                childViewHolder.LLTypeRow1.setBackgroundResource(R.drawable.edittextstyle_visamaster);
            }*/

            if (data.ControlCode.equals("AY") || data.ControlCode.equals("AN") || data.ControlCode.equals("FY") || data.ControlCode.equals("FN")) {
                childViewHolder.LLTypeRow1.setBackgroundResource(R.drawable.gradient_bg_orange);
            }

            childViewHolder.cbMD2.setOnCheckedChangeListener(null);
            childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
            childViewHolder.cbMD2.setEnabled(data.IsChecked ? false : true);

            if (!data.IsChecked) {
                EnableDisableCtrlsNew(false, childViewHolder, data);
            }

            //LABEL
            if (data.ControlCode.equals("LB")) {
                handleLabel(childViewHolder, data, position);
            }

            //CHECKBOX
            if (data.ControlCode.equals("CB")) {
                handleCheckBox(childViewHolder, data, position);
            }

            //TEXTBOX
            /*if (data.ControlCode.equals("TB")) {
                handleTextBox(childViewHolder, data, position);
            }*/
            if (data.ControlCode.toUpperCase().contains("TB")) {
                handleTextBox(childViewHolder, data, position, data.ControlCode);
            }

            //DATE
            if (data.ControlCode.equals("DD")) {
                handleDate(childViewHolder, data, position);
            }

            //DATETIME
            if (data.ControlCode.equals("DT")) {
                handleDateTime(childViewHolder, data, position);
            }

            //TOGGLE
            if (data.ControlCode.equals("YN")) {
                handleYesNo(childViewHolder, data, position);
            }

            //PHOTO
            if (data.ControlCode.equals("PH")) {
                handlePhoto(childViewHolder, data, position, false);
            }

            //PHOTO PROOF OF SHIPMENT
            if (data.ControlCode.equals("PR")) {
                handlePhoto(childViewHolder, data, position, true);
            }

            //CHECKBOX GROUP
            if (data.ControlCode.equals("CG")) {
                handleCheckBoxGroup(childViewHolder, data, position);
            }

            //RADIOBUTTON
            if (data.ControlCode.equals("RG")) {
                handleRadioGroup(childViewHolder, data, position);
            }

            //ID VERIFICATION
            if (data.ControlCode.equals("ID")) {
                handleIDVerification(childViewHolder, data, position);
            }

            //Tele Others Mobile last 4 digit verification
            if (data.ControlCode.equals("V1")) {
                handleV1Verification(childViewHolder, data, position);
            }

            //AY AN FY FN
            if (data.ControlCode.equals("AY") || data.ControlCode.equals("AN") || data.ControlCode.equals("FY") || data.ControlCode.equals("FN")) {
                //type 1 == AY (any 1 is YES) 2 == AN (any 1 is NO) 3 == FY (all must be YES) 4 == FN (all must be NO)
                handleFAFYFN(childViewHolder, data, position);
            }

            if (data.ControlCode.equals("OT")) {
                handleOTP(childViewHolder, data, position);
            }

            if (data.ControlCode.equals("SO")) {
                handleSignatureButton(childViewHolder, data, position);
            }

            if (data.ControlCode.equals("RO")) {
                handleRO(childViewHolder, data, position);
            }

            if (data.ControlCode.equals("TL")) {
                handleTempLogger(childViewHolder, data, position);
            }

            if (data.ControlCode.equals("RD")) {
                handleRD(childViewHolder, data, position);
            }

            if (data.ControlCode.equals("V2")) {
                handleV2IMEIVerification(childViewHolder, data, position);
            }

            if (data.ControlCode.equals("V3")) {
                handleV3IMEIVerification(childViewHolder, data, position);
            }

            if (data.ControlCode.equals("V4")) {
                handleV4IMEIVerification(childViewHolder, data, position);
            }

            if (data.ControlCode.equals("V5")) {
                handleV5Verification(childViewHolder, data, position);
            }

            if (data.ControlCode.equals("AT")) {
                handleArticleTag(childViewHolder, data, position);
            }

            if (data.ControlCode.equals("QT")) {
                handleUpdatePieces(childViewHolder, data, position);
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hideshowControlsNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleTempLogger(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        //in the remarks add in [172] for status
        try {
            if (childViewHolder.txtValueTextWatcher2 != null) {
                childViewHolder.txtValue.removeTextChangedListener(childViewHolder.txtValueTextWatcher2);
                childViewHolder.txtValueTextWatcher2 = null;
            }

            childViewHolder.txtValue2.setText(
                    data.OptionValue2 != null && data.OptionValue2.size() > 0 ? data.OptionValue2.get(0) : "");

            childViewHolder.txtValueTextWatcher2 = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    /*String value = s.toString();
                    Date dt = new Date();
                    Long l = dt.getTime();
                    try {
                        String TempLastRecordedTimeStampTSstr = objGlobalMethods.MillisecToDateStr2(l, "dd/MM h:mma");
                        if (value.length() > 0) {
                            if (data.OptionValue2 != null && data.OptionValue2.size() > 0) {
                                data.OptionValue2.clear();
                                data.OptionValue2.add(value);
                                data.OptionValue2.add(TempLastRecordedTimeStampTSstr);
                            } else if (data.OptionValue2 != null && data.OptionValue2.size() == 0) {
                                data.OptionValue2.clear();
                                data.OptionValue2.add(value);
                                data.OptionValue2.add(TempLastRecordedTimeStampTSstr);
                            } else if (data.OptionValue2 == null) {
                                data.OptionValue2 = new ArrayList<>();
                                data.OptionValue2.add(value);
                                data.OptionValue2.add(TempLastRecordedTimeStampTSstr);
                            }
                        } else {
                            if (data.FinalManOptCode.equals("OP")) {
                                data.OptionValue2.clear();
                                data.OptionValue2.add(value);
                                data.OptionValue2.add(TempLastRecordedTimeStampTSstr);
                            } else {
                                data.OptionValue2 = null;
                            }
                        }
                    } catch (Exception e) {
                        String sts = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleTempLogger txtValueTextWatcher onTextChanged. Exception: " + e.getMessage() + "\nStack: " + sts,"","","","");
                    }*/
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String value = s.toString();
                    String str2 = "";
                    if (!value.isEmpty()) {
                        str2 = tempLoggerEntryDialog.PerfectDecimal(value, 2, 2);
                        if (!str2.equals(value)) {
                            value = str2;
                            if (childViewHolder.txtValue2 != null) {
                                childViewHolder.txtValue2.setText(value);
                                childViewHolder.txtValue2.setSelection(value.length());
                            }
                        }
                    }

                    Date dt = new Date();
                    Long l = dt.getTime();
                    try {
                        String TempLastRecordedTimeStampTSstr = objGlobalMethods.MillisecToDateStr2(l, "dd/MM h:mma");
                        if (data.OptionValue2 != null && data.OptionValue2.size() > 0) {
                            //TempLastRecordedTimeStampTSstr = data.OptionValue2.get(1);
                            TempLastRecordedTimeStampTSstr = objGlobalMethods.MillisecToDateStr2(Long.parseLong(data.OptionValue2.get(1)), "dd/MM h:mma");
                        }

                        /*if (data.OptionValue != null && data.OptionValue.size() > 0) {
                            if (data.OptionValue.get(0) == "1" && data.OptionValue2 != null && data.OptionValue2.size() > 0) {
                                TempLastRecordedTimeStampTSstr = data.OptionValue2.get(1);
                            }
                        }*/

                        if (value.length() > 0) {
                            if (data.OptionValue2 != null && data.OptionValue2.size() > 0) {
                                data.OptionValue2.clear();
                                data.OptionValue2.add(value);
                                //data.OptionValue2.add(TempLastRecordedTimeStampTSstr);
                                data.OptionValue2.add(String.valueOf(l));
                            } else if (data.OptionValue2 != null && data.OptionValue2.size() == 0) {
                                data.OptionValue2.clear();
                                data.OptionValue2.add(value);
                                //data.OptionValue2.add(TempLastRecordedTimeStampTSstr);
                                data.OptionValue2.add(String.valueOf(l));
                            } else if (data.OptionValue2 == null) {
                                data.OptionValue2 = new ArrayList<>();
                                data.OptionValue2.add(value);
                                //data.OptionValue2.add(TempLastRecordedTimeStampTSstr);
                                data.OptionValue2.add(String.valueOf(l));
                            }
                        } else {
                            if (data.FinalManOptCode.equals("OP")) {
                                data.OptionValue2.clear();
                                data.OptionValue2.add(value);
                                //data.OptionValue2.add(TempLastRecordedTimeStampTSstr);
                                data.OptionValue2.add(String.valueOf(l));
                            } else {
                                data.OptionValue2 = null;
                            }
                        }
                    } catch (Exception e) {
                        String sts = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleTempLogger txtValueTextWatcher2 onTextChanged. Exception: " + e.getMessage() + "\nStack: " + sts, "", "", "", "");
                    }
                }
            };
            childViewHolder.txtValue2.addTextChangedListener(childViewHolder.txtValueTextWatcher2);

            //childViewHolder.txtValue.setFilters(new InputFilter[] {getTwoDecPlcInputFilter()});

            try {
                LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) childViewHolder.txtValue2.getLayoutParams();
                p.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                childViewHolder.txtValue2.setLayoutParams(p);
            } catch (Exception e) {
                String g = e.getMessage();
            }

            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.cb.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.GONE);
            }

            childViewHolder.lbldegc.setVisibility(View.VISIBLE);
            childViewHolder.lbltempTS.setVisibility(View.VISIBLE);
            //childViewHolder.lbltempTS.setText(data.OptionValue2 != null && data.OptionValue2.size() > 0 ? data.OptionValue2.get(1) : "");
            childViewHolder.lbltempTS.setText(data.OptionValue2 != null && data.OptionValue2.size() > 0 ?
                    objGlobalMethods.MillisecToDateStr2(Long.parseLong(data.OptionValue2.get(1)), "dd/MM h:mma") : "");

            childViewHolder.imgCancelPhoto.setEnabled(data.dataT != null && data.dataT.size() > 0 ? true : false);
            childViewHolder.imgCancelPhoto.setImageResource(data.dataT != null && data.dataT.size() > 0 ? R.mipmap.ic_action_collections_enabled : R.mipmap.ic_action_collections);
            childViewHolder.imgPhoto.setEnabled(data.IsChecked ? true : false);
            childViewHolder.imgPhoto.setImageResource(data.IsChecked ? R.mipmap.ic_action_camera_alt_enabled : R.mipmap.ic_action_camera_alt);

            childViewHolder.imgPhoto.setOnClickListener(null);
            childViewHolder.imgCancelPhoto.setOnClickListener(null);
            childViewHolder.OpenCameraOnClickListener = rtnTakePhotoOnClickListener(position, true);
            childViewHolder.ViewPhotoOnClickListener = rtnViewPhotoOnClickListener(position, data.ControlCode.equals("PR") ? true : false);
            childViewHolder.imgPhoto.setOnClickListener(childViewHolder.OpenCameraOnClickListener);
            childViewHolder.imgCancelPhoto.setOnClickListener(childViewHolder.ViewPhotoOnClickListener);

            childViewHolder.cbPhoto.setChecked(data.dataT != null && data.dataT.size() > 0 ? true : false);
            childViewHolder.lblphotorequiredlabel.setText(data.dataT != null && data.dataT.size() > 0 ? R.string.lblphotoacquired : R.string.lblphotorequired);
            childViewHolder.lblphotorequiredlabel.setTextColor(data.dataT != null && data.dataT.size() > 0 ? Color.BLACK : Color.RED);

            childViewHolder.LLOthers.setVisibility(View.VISIBLE);

            childViewHolder.LLPhoto.setVisibility(
                    (data.OptionValue != null && data.OptionValue.size() > 0 && !objGlobalMethods.IsEmptyString(data.OptionValue.get(0)) && data.OptionValue.get(0).equals("0"))
                            || (data.dataT != null && data.dataT.size() > 0) ? View.VISIBLE : View.GONE);

            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.VISIBLE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue2.getVisibility());

            childViewHolder.txtValue2.setEnabled(
                    (data.OptionValue != null && data.OptionValue.size() > 0 && !objGlobalMethods.IsEmptyString(data.OptionValue.get(0)) && data.OptionValue.get(0).equals("0")));

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);


            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");
            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleTempLogger. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private InputFilter getTwoDecPlcInputFilter() {
        InputFilter f = null;
        try {
            f = new InputFilter() {
                final int maxDigitsBeforeDecimalPoint = 2;
                final int maxDigitsAfterDecimalPoint = 1;

                @Override
                public CharSequence filter(CharSequence source, int start, int end,
                                           Spanned dest, int dstart, int dend) {
                    StringBuilder builder = new StringBuilder(dest);
                    builder.replace(dstart, dend, source
                            .subSequence(start, end).toString());
                    if (!builder.toString().matches(
                            "(([1-9]{1})([0-9]{0," + (maxDigitsBeforeDecimalPoint - 1) + "})?)?(\\.[0-9]{0," + maxDigitsAfterDecimalPoint + "})?"

                    )) {
                        if (source.length() == 0)
                            return dest.subSequence(dstart, dend);
                        return "";
                    }

                    return null;

                }
            };
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter getTwoDecPlcInputFilter. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return f;
        }
    }

    private void handleLabel(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            if (data.IsParent && !data.IsChild) {
                childViewHolder.LLDisplayText.setVisibility(View.VISIBLE);
                childViewHolder.LLOthers.setVisibility(View.GONE);

                childViewHolder.cbMDOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cbMD.setOnCheckedChangeListener(null);
                childViewHolder.cbMD.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMD.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);
                childViewHolder.cbMD.setVisibility(View.VISIBLE);
                childViewHolder.cbMD2.setVisibility(View.GONE);

                //childViewHolder.lblText.setText(data.DisplayText);
                childViewHolder.lblText.setText(data.DisplayTextCN);
                childViewHolder.lblText.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblText.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

            } else if (data.IsChild) {
                childViewHolder.LLDisplayText.setVisibility(View.GONE);
                childViewHolder.LLOthers.setVisibility(View.VISIBLE);

                childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.cb.setVisibility(View.VISIBLE);
                //childViewHolder.cb.setText(data.DisplayText);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForLabelOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForLabelOnCheckedChangeListener);
            }

            childViewHolder.lblValue.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleLabel. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleLabel. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void handleCheckBox(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.VISIBLE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                //childViewHolder.lblValue.setText(data.DisplayText);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);

            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cb.setVisibility(View.VISIBLE);
                //childViewHolder.cb.setText(data.DisplayText);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForCheckboxOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.GONE);
            }

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);

            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleCheckBox. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleCheckBox. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void handleTextBox(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position, String ControlCode) {
        try {
            if (childViewHolder.txtValueTextWatcher != null) {
                childViewHolder.txtValue.removeTextChangedListener(childViewHolder.txtValueTextWatcher);
                childViewHolder.txtValueTextWatcher = null;
            }
            int colIndex = -1;
            int maxLength = 0;
            try {
                colIndex = ControlCode.indexOf(":");
                if (colIndex > -1) {
                    maxLength = Integer.parseInt(ControlCode.substring(colIndex + 1));
                }
            } catch (Exception e) {
                maxLength = 0;
            }
            childViewHolder.txtValue.setFilters(maxLength <= 0 ? new InputFilter[]{} : new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            childViewHolder.txtValue.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? data.OptionValue.get(0) : "");
            childViewHolder.txtValueTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String value = s.toString();
                    if (value.length() > 0) {
                        if (data.OptionValue != null && data.OptionValue.size() > 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add(value);
                        } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add(value);
                        } else if (data.OptionValue == null) {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add(value);
                        }
                    } else {
                        if (data.FinalManOptCode.equals("OP")) {
                            data.OptionValue.clear();
                            data.OptionValue.add(value);
                        } else {
                            data.OptionValue = null;
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };
            childViewHolder.txtValue.addTextChangedListener(childViewHolder.txtValueTextWatcher);

            if (data.IsParent && !data.IsChild) {
                /*childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.VISIBLE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener = cbForTextboxOnCheckedChangeListener(data, childViewHolder);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);*/


                childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cb.setVisibility(View.GONE);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

            } else if (data.IsChild) {
                /*childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cb.setVisibility(View.VISIBLE);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForCheckboxOnCheckedChangeListener = cbForTextboxOnCheckedChangeListener(data, childViewHolder);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.GONE);*/

                childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.cbMD.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setVisibility(View.GONE);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
            }

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.txtValue.setVisibility(View.VISIBLE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            //childViewHolder.txtValue.setEnabled((data.FinalManOptCode.equals("MD") && data.IsChecked) ? true : (data.FinalManOptCode.equals("MD") && !data.IsChecked) ? false : true);
            childViewHolder.txtValue.setEnabled(true);
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleTextBox. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleTextBox. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void handleIDVerification(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            Context ctx = myApp.getAppContext();

            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            }

            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            String IDVerificationVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "IDVerification");
            childViewHolder.lblIDVerificationVal.setText(IDVerificationVal);
            childViewHolder.btnVerification.setOnClickListener(null);

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.VISIBLE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");


            if (childViewHolder.BtnVerificationOnClickListener != null) {
                childViewHolder.BtnVerificationOnClickListener = null;
            }

            childViewHolder.txtVerification.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? data.OptionValue.get(0) : "");
            childViewHolder.imgVerification.setImageResource(data.OptionValue != null && data.OptionValue.size() > 0 ? R.mipmap.ic_single_tick : R.mipmap.ic_action_failpu);
            childViewHolder.btnVerification.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? ctx.getString(R.string.otpbuttontext_verified) : ctx.getString(R.string.btnVerify));
            EnableDisableCtrlsNew(data.IsChecked && (data.OptionValue == null || data.OptionValue != null && data.OptionValue.size() == 0) ? true : false, childViewHolder, data);

            //childViewHolder.BtnVerificationOnClickListener = rtnBtnVerificationOnClickListener(position, childViewHolder, data);
            childViewHolder.BtnVerificationOnClickListener = rtnBtnMultiVerificationOnClickListener(position, childViewHolder, data);

            childViewHolder.btnVerification.setOnClickListener(childViewHolder.BtnVerificationOnClickListener);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleIDVerification. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleUpdatePieces(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            if (childViewHolder.txtLabelUpdatePiecesValTextWatcher != null) {
                childViewHolder.txtLabelUpdatePiecesVal.removeTextChangedListener(childViewHolder.txtLabelUpdatePiecesValTextWatcher);
                childViewHolder.txtLabelUpdatePiecesValTextWatcher = null;
            }
            Context ctx = myApp.getAppContext();

            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            }

            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.txtIMEIVerification.setText("");
            childViewHolder.btnVerificationIMEI.setOnClickListener(null);

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");
            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;


            childViewHolder.LLUpdatePieces.setVisibility(View.VISIBLE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

            childViewHolder.btnVerification.setOnClickListener(null);
            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.BtnV5VerificationOnClickListener = null;


            childViewHolder.txtLabelUpdatePiecesVal.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? data.OptionValue.get(0) : "");

            childViewHolder.txtLabelUpdatePiecesValTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String value = s.toString();
                    if (value.length() > 0) {
                        if (data.OptionValue != null && data.OptionValue.size() > 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add(value);
                        } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add(value);
                        } else if (data.OptionValue == null) {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add(value);
                        }
                    } else {
                        if (data.FinalManOptCode.equals("OP")) {
                            data.OptionValue.clear();
                            data.OptionValue.add(value);
                        } else {
                            data.OptionValue = null;
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };
            childViewHolder.txtLabelUpdatePiecesVal.addTextChangedListener(childViewHolder.txtLabelUpdatePiecesValTextWatcher);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleUpdatePieces. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleArticleTag(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            Context ctx = myApp.getAppContext();

            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            }

            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.txtIMEIVerification.setText("");
            childViewHolder.btnVerificationIMEI.setOnClickListener(null);

            childViewHolder.LLArticleTag.setVisibility(View.VISIBLE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.btnVerification.setOnClickListener(null);
            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.BtnV5VerificationOnClickListener = null;

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");


            childViewHolder.txtLabelTagVal.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? data.OptionValue.get(0) : "");
            EnableDisableCtrlsNew(data.OptionValue2 != null && data.OptionValue2.size() > 0 ? false : true, childViewHolder, data);

            childViewHolder.btnScanTagOnClickListener = rtnbtnScanTagOnClickListener(position, childViewHolder, data);
            childViewHolder.btnUpdateLabelTagOnClickListener = rtnbtnUpdateLabelTagOnClickListenerNew(position, childViewHolder, data);

            childViewHolder.btnScanTag.setOnClickListener(childViewHolder.btnScanTagOnClickListener);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(childViewHolder.btnUpdateLabelTagOnClickListener);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleArticleTag. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleV2IMEIVerification(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            Context ctx = myApp.getAppContext();

            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            }

            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            childViewHolder.LLIMEIVerification.setVisibility(View.VISIBLE);
            String IMEIv1 = objGlobalMethods.getGCMPrefs(GcmPreferences, "IMEIv1");
            String IMEIv2 = objGlobalMethods.getGCMPrefs(GcmPreferences, "IMEIv2");
            childViewHolder.lblIMVCFirstFewChar.setText(IMEIv1);
            childViewHolder.lblIMEIVerificationVal.setText(IMEIv2);
            childViewHolder.btnVerificationIMEI.setOnClickListener(null);

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

            childViewHolder.btnVerification.setOnClickListener(null);
            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            if (childViewHolder.BtnV2VerificationOnClickListener != null) {
                childViewHolder.BtnV2VerificationOnClickListener = null;
            }


            int maxLength = 7;
            childViewHolder.txtIMEIVerification.setFilters(maxLength <= 0 ? new InputFilter[]{} : new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            childViewHolder.txtIMEIVerification.setHint(ctx.getString(R.string.cl_imei));

            childViewHolder.txtIMEIVerification.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? data.OptionValue.get(0) : "");
            childViewHolder.imgIMVCVerification.setImageResource(data.OptionValue != null && data.OptionValue.size() > 0 ? R.mipmap.ic_single_tick : R.mipmap.ic_action_failpu);
            childViewHolder.btnVerificationIMEI.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? ctx.getString(R.string.otpbuttontext_verified) : ctx.getString(R.string.btnVerify));
            EnableDisableCtrlsNew(data.IsChecked && (data.OptionValue == null || data.OptionValue != null && data.OptionValue.size() == 0) ? true : false, childViewHolder, data);

            childViewHolder.BtnV2VerificationOnClickListener = rtnBtnMultiV2VerificationOnClickListener(position, childViewHolder, data);

            childViewHolder.btnVerificationIMEI.setOnClickListener(childViewHolder.BtnV2VerificationOnClickListener);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleV2IMEIVerification. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleV3IMEIVerification(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            Context ctx = myApp.getAppContext();

            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            }

            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            childViewHolder.LLIMEIVerification.setVisibility(View.VISIBLE);
            String IMEIv1 = objGlobalMethods.getGCMPrefs(GcmPreferences, "IMEIv1");
            String IMEIv2 = objGlobalMethods.getGCMPrefs(GcmPreferences, "IMEIv2");
            childViewHolder.lblIMVCFirstFewChar.setText(IMEIv1);
            childViewHolder.lblIMEIVerificationVal.setText(IMEIv2);
            childViewHolder.btnVerificationIMEI.setOnClickListener(null);

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

            childViewHolder.btnVerification.setOnClickListener(null);
            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            if (childViewHolder.BtnV3VerificationOnClickListener != null) {
                childViewHolder.BtnV3VerificationOnClickListener = null;
            }


            int maxLength = 4;
            childViewHolder.txtIMEIVerification.setFilters(maxLength <= 0 ? new InputFilter[]{} : new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            childViewHolder.txtIMEIVerification.setHint(ctx.getString(R.string.cl_sn));

            childViewHolder.txtIMEIVerification.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? data.OptionValue.get(0) : "");
            childViewHolder.imgIMVCVerification.setImageResource(data.OptionValue != null && data.OptionValue.size() > 0 ? R.mipmap.ic_single_tick : R.mipmap.ic_action_failpu);
            childViewHolder.btnVerificationIMEI.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? ctx.getString(R.string.otpbuttontext_verified) : ctx.getString(R.string.btnVerify));
            EnableDisableCtrlsNew(data.IsChecked && (data.OptionValue == null || data.OptionValue != null && data.OptionValue.size() == 0) ? true : false, childViewHolder, data);

            childViewHolder.BtnV3VerificationOnClickListener = rtnBtnMultiV3VerificationOnClickListener(position, childViewHolder, data);

            childViewHolder.btnVerificationIMEI.setOnClickListener(childViewHolder.BtnV3VerificationOnClickListener);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleV3IMEIVerification. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleV4IMEIVerification(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            Context ctx = myApp.getAppContext();

            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            }

            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            childViewHolder.LLIMEIVerification.setVisibility(View.VISIBLE);
            String VoucherNumVal1 = objGlobalMethods.getGCMPrefs(GcmPreferences, "VOUCHERv1");
            String VoucherNumVal2 = objGlobalMethods.getGCMPrefs(GcmPreferences, "VOUCHERv2");
            childViewHolder.lblIMVCFirstFewChar.setText(VoucherNumVal1);
            childViewHolder.lblIMEIVerificationVal.setText(VoucherNumVal2);
            childViewHolder.btnVerificationIMEI.setOnClickListener(null);

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

            childViewHolder.btnVerification.setOnClickListener(null);
            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            if (childViewHolder.BtnV4VerificationOnClickListener != null) {
                childViewHolder.BtnV4VerificationOnClickListener = null;
            }


            int maxLength = 4;
            childViewHolder.txtIMEIVerification.setFilters(maxLength <= 0 ? new InputFilter[]{} : new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            childViewHolder.txtIMEIVerification.setHint(ctx.getString(R.string.cl_vc));

            childViewHolder.txtIMEIVerification.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? data.OptionValue.get(0) : "");
            childViewHolder.imgIMVCVerification.setImageResource(data.OptionValue != null && data.OptionValue.size() > 0 ? R.mipmap.ic_single_tick : R.mipmap.ic_action_failpu);
            childViewHolder.btnVerificationIMEI.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? ctx.getString(R.string.otpbuttontext_verified) : ctx.getString(R.string.btnVerify));
            EnableDisableCtrlsNew(data.IsChecked && (data.OptionValue == null || data.OptionValue != null && data.OptionValue.size() == 0) ? true : false, childViewHolder, data);

            childViewHolder.BtnV4VerificationOnClickListener = rtnBtnMultiV4VerificationOnClickListener(position, childViewHolder, data);

            childViewHolder.btnVerificationIMEI.setOnClickListener(childViewHolder.BtnV4VerificationOnClickListener);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleV4IMEIVerification. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleV4VCVerification(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            Context ctx = myApp.getAppContext();

            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            }

            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            String IDVerificationVal = objGlobalMethods.getGCMPrefs(GcmPreferences, "IDVerification");
            childViewHolder.lblIDVerificationVal.setText(IDVerificationVal);
            childViewHolder.btnVerification.setOnClickListener(null);

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.VISIBLE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

            if (childViewHolder.BtnVerificationOnClickListener != null) {
                childViewHolder.BtnVerificationOnClickListener = null;
            }

            childViewHolder.txtVerification.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? data.OptionValue.get(0) : "");
            childViewHolder.imgVerification.setImageResource(data.OptionValue != null && data.OptionValue.size() > 0 ? R.mipmap.ic_single_tick : R.mipmap.ic_action_failpu);
            childViewHolder.btnVerification.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? ctx.getString(R.string.otpbuttontext_verified) : ctx.getString(R.string.btnVerify));
            EnableDisableCtrlsNew(data.IsChecked && (data.OptionValue == null || data.OptionValue != null && data.OptionValue.size() == 0) ? true : false, childViewHolder, data);

            //childViewHolder.BtnVerificationOnClickListener = rtnBtnVerificationOnClickListener(position, childViewHolder, data);
            childViewHolder.BtnVerificationOnClickListener = rtnBtnMultiVerificationOnClickListener(position, childViewHolder, data);

            childViewHolder.btnVerification.setOnClickListener(childViewHolder.BtnVerificationOnClickListener);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleVCVerification. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleV1Verification(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            Context ctx = myApp.getAppContext();

            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener = cbForV1VerificationOnCheckedChangeListener(data, childViewHolder);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cb.setVisibility(View.VISIBLE);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForCheckboxOnCheckedChangeListener = cbForV1VerificationOnCheckedChangeListener(data, childViewHolder);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.GONE);
            }

            childViewHolder.btnVerification.setOnClickListener(null);

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.yesnoIDVerificationSkip.setVisibility(View.VISIBLE);
            childViewHolder.LLIDVerification.setVisibility(View.VISIBLE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            if (childViewHolder.BtnVerificationOnClickListener != null) {
                childViewHolder.BtnVerificationOnClickListener = null;
            }

            childViewHolder.lblenterlast.setText(ctx.getString(R.string.lblunabletoverify));
            childViewHolder.txtVerification.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            childViewHolder.txtVerification.setHint(ctx.getString(R.string.cl_contactnum));
            childViewHolder.txtVerification.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? data.OptionValue.get(0) : "");
            childViewHolder.imgVerification.setImageResource(
                    data.OptionValue != null && data.OptionValue.size() > 0 && !objGlobalMethods.IsEmptyString(data.OptionValue.get(0)) ? R.mipmap.ic_single_tick
                            : R.mipmap.ic_action_failpu);
            childViewHolder.btnVerification.setText(data.OptionValue != null && data.OptionValue.size() > 0 && !objGlobalMethods.IsEmptyString(data.OptionValue.get(0)) ? ctx.getString(R.string.otpbuttontext_verified) : ctx.getString(R.string.btnVerify));
            EnableDisableCtrlsNew(data.IsChecked && (data.OptionValue == null || data.OptionValue != null && data.OptionValue.size() == 0) ? true : false, childViewHolder, data);

            childViewHolder.BtnVerificationOnClickListener = rtnV1BtnVerificationOnClickListener(position, childViewHolder, data);
            childViewHolder.btnVerification.setOnClickListener(childViewHolder.BtnVerificationOnClickListener);

            if (childViewHolder.yesnoOnCheckedChangeListenerSub != null) {
                childViewHolder.yesnoIDVerificationSkip.setOnCheckedChangeListener(null);
            }

            Boolean haschild = hasChild(position);
            if (data.OptionValue2 != null && data.OptionValue2.size() > 0) {
                childViewHolder.yesnoIDVerificationSkip.setChecked(data.OptionValue2.get(0).equals("YES") ? true : false);
                childViewHolder.yesnoIDVerificationSkip.setText(data.OptionValue2.get(0));

                /*if (data.OptionValue2.get(0).equals("NO")) {
                    if (haschild) {
                        hideHierarchyNew2(position, childViewHolder);
                    }
                } else {
                    if (haschild) {
                        showHierarchyNew2(position, childViewHolder, data);
                    }
                }*/
            } else {
                childViewHolder.yesnoIDVerificationSkip.setChecked(false);
                childViewHolder.yesnoIDVerificationSkip.setText("NO");
                /*if (haschild) {
                    hideHierarchyNew2(position, childViewHolder);
                }*/
            }

            childViewHolder.yesnoOnCheckedChangeListenerSub = yesnoOnCheckedChangeListenerSub(position, data, childViewHolder);
            childViewHolder.yesnoIDVerificationSkip.setOnCheckedChangeListener(childViewHolder.yesnoOnCheckedChangeListenerSub);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleV1Verification. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleV5Verification(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            Context ctx = myApp.getAppContext();

            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener = cbForV1VerificationOnCheckedChangeListener(data, childViewHolder);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cb.setVisibility(View.VISIBLE);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForCheckboxOnCheckedChangeListener = cbForV1VerificationOnCheckedChangeListener(data, childViewHolder);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.GONE);
            }

            childViewHolder.btnVerification.setOnClickListener(null);

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.yesnoIDVerificationSkip.setVisibility(View.VISIBLE);
            childViewHolder.LLIDVerification.setVisibility(View.VISIBLE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            if (childViewHolder.BtnV5VerificationOnClickListener != null) {
                childViewHolder.BtnV5VerificationOnClickListener = null;
            }

            childViewHolder.lblenterlast.setText(ctx.getString(R.string.lblunabletoverify));
            childViewHolder.txtVerification.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
            childViewHolder.txtVerification.setHint(ctx.getString(R.string.cl_unitnum));
            childViewHolder.txtVerification.setHint(ctx.getString(R.string.cl_unitnum));
            childViewHolder.txtVerification.setText(data.OptionValue != null && data.OptionValue.size() > 0 ? data.OptionValue.get(0) : "");
            childViewHolder.imgVerification.setImageResource(
                    data.OptionValue != null && data.OptionValue.size() > 0 && !objGlobalMethods.IsEmptyString(data.OptionValue.get(0)) ? R.mipmap.ic_single_tick
                            : R.mipmap.ic_action_failpu);
            childViewHolder.btnVerification.setText(data.OptionValue != null && data.OptionValue.size() > 0 && !objGlobalMethods.IsEmptyString(data.OptionValue.get(0)) ? ctx.getString(R.string.otpbuttontext_verified) : ctx.getString(R.string.btnVerify));
            EnableDisableCtrlsNew(data.IsChecked && (data.OptionValue == null || data.OptionValue != null && data.OptionValue.size() == 0) ? true : false, childViewHolder, data);

            childViewHolder.BtnV5VerificationOnClickListener = rtnV5BtnVerificationOnClickListener(position, childViewHolder, data);
            childViewHolder.btnVerification.setOnClickListener(childViewHolder.BtnV5VerificationOnClickListener);

            if (childViewHolder.yesnoOnCheckedChangeListenerSub != null) {
                childViewHolder.yesnoIDVerificationSkip.setOnCheckedChangeListener(null);
            }

            //Boolean haschild = hasChild(position);
            if (data.OptionValue2 != null && data.OptionValue2.size() > 0) {
                childViewHolder.yesnoIDVerificationSkip.setChecked(data.OptionValue2.get(0).equals("YES") ? true : false);
                childViewHolder.yesnoIDVerificationSkip.setText(data.OptionValue2.get(0));

                /*if (data.OptionValue2.get(0).equals("NO")) {
                    if (haschild) {
                        hideHierarchyNew2(position, childViewHolder);
                    }
                } else {
                    if (haschild) {
                        showHierarchyNew2(position, childViewHolder, data);
                    }
                }*/
            } else {
                childViewHolder.yesnoIDVerificationSkip.setChecked(false);
                childViewHolder.yesnoIDVerificationSkip.setText("NO");
                /*if (haschild) {
                    hideHierarchyNew2(position, childViewHolder);
                }*/
            }

            childViewHolder.yesnoOnCheckedChangeListenerSub = yesnoOnCheckedChangeListenerSub2(position, data, childViewHolder);
            childViewHolder.yesnoIDVerificationSkip.setOnCheckedChangeListener(childViewHolder.yesnoOnCheckedChangeListenerSub);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleV5Verification. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleOTP(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener = cbForV1VerificationOnCheckedChangeListener(data, childViewHolder);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cb.setVisibility(View.VISIBLE);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForCheckboxOnCheckedChangeListener = cbForV1VerificationOnCheckedChangeListener(data, childViewHolder);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.GONE);
            }

            childViewHolder.btnVerification.setOnClickListener(null);

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.yesnoIDVerificationSkip.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.VISIBLE);
            childViewHolder.lblValue.setVisibility(View.GONE);

            if (childViewHolder.OTPOnClickListener != null) {
                childViewHolder.OTPOnClickListener = null;
            }

            EnableDisableCtrlsNew(true, childViewHolder, data);

            childViewHolder.OTPOnClickListener = rtnOtpOnClickListener(position);
            childViewHolder.btnOTP.setOnClickListener(childViewHolder.OTPOnClickListener);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleOTP. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleSignatureButton(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener = cbForV1VerificationOnCheckedChangeListener(data, childViewHolder);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cb.setVisibility(View.GONE);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForCheckboxOnCheckedChangeListener = cbForV1VerificationOnCheckedChangeListener(data, childViewHolder);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);
            }

            childViewHolder.lblValue.setVisibility(View.VISIBLE);
            childViewHolder.lblValue.setText(data.DisplayTextCN);
            childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
            childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

            if (childViewHolder.BtnVerificationOnClickListener != null) {
                childViewHolder.BtnVerificationOnClickListener = null;
            }
            childViewHolder.btnVerification.setOnClickListener(null);

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.yesnoIDVerificationSkip.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.VISIBLE);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");


            if (childViewHolder.OTPOnClickListener != null) {
                childViewHolder.OTPOnClickListener = null;
            }
            childViewHolder.btnOTP.setOnClickListener(null);

            EnableDisableCtrlsNew(true, childViewHolder, data);

            if (ctx == null) {
                ctx = myApp.getAppContext();
            }

            ctrlSettings.tottleButtonsWithStyles(true, childViewHolder.btnOTP);

            if (childViewHolder.SignatureOnClickListener != null) {
                childViewHolder.SignatureOnClickListener = null;
            }
            childViewHolder.SignatureOnClickListener = rtnSignatureOnClickListener(position, data.dataT);
            childViewHolder.btnOTP.setOnClickListener(childViewHolder.SignatureOnClickListener);

            childViewHolder.btnOTP.setText(ctx.getString((data.dataT == null || (data.dataT != null && data.dataT.size() == 0)) ? R.string.rec_signature_clicktosign : R.string.rec_signature_signed));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleSignatureButton. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleDate(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.VISIBLE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cb.setVisibility(View.VISIBLE);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForCheckboxOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.GONE);
            }

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.VISIBLE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            /*if (childViewHolder.slideDateListener != null) {
                childViewHolder.imgbtnDate.setOnClickListener(null);
                childViewHolder.lblDate.setOnClickListener(null);
            }*/
            if (childViewHolder.DateOnClickListener != null) {
                //childViewHolder.slideDateListener = null;
                childViewHolder.DateOnClickListener = null;
                childViewHolder.lblDate.setOnClickListener(null);
            }

            childViewHolder.lblDate.setText(data.SelectedDate != null ? dateformat.format(data.SelectedDate) : "DD/MM/YYYY");
            childViewHolder.lblDate.setEnabled((data.FinalManOptCode.equals("MD") && data.IsChecked) ? true : (data.FinalManOptCode.equals("MD") && !data.IsChecked) ? false : true);
            //childViewHolder.slideDateListener = rtnSlideDateListener(childViewHolder.lblDate, data, childViewHolder);
            childViewHolder.DateOnClickListener = rtnCustomDatePickerOnClickListener2(data, childViewHolder);
            childViewHolder.lblDate.setOnClickListener(childViewHolder.DateOnClickListener);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleDate. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleDate. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void handleDateTime(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        SimpleDateFormat datetimeformat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        try {
            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.VISIBLE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cb.setVisibility(View.VISIBLE);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForCheckboxOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.GONE);
            }

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);

            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.VISIBLE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            if (childViewHolder.slideDateTimeListener != null) {
                childViewHolder.imgbtnDateTimeTime.setOnClickListener(null);
                childViewHolder.lblDateTimeTime.setOnClickListener(null);
            }
            if (childViewHolder.DateOnClickListener != null) {
                childViewHolder.slideDateTimeListener = null;
                childViewHolder.lblDateTimeTime.setOnClickListener(null);
            }

            childViewHolder.lblDateTimeTime.setText(data.SelectedDate != null ? datetimeformat.format(data.SelectedDate) : "DD/MM/YYYY HH:mm");
            childViewHolder.lblDateTimeTime.setEnabled((data.FinalManOptCode.equals("MD") && data.IsChecked) ? true : (data.FinalManOptCode.equals("MD") && !data.IsChecked) ? false : true);
            childViewHolder.slideDateTimeListener = rtnSlideDateTimeListener(childViewHolder.lblDateTimeTime, data, childViewHolder);
            childViewHolder.lblDateTimeTimeOnClickListener = CheckListActivity.rtnlblDateTimeTimeOnClickListener(childViewHolder.slideDateTimeListener, data.SelectedDate == null ? null : data.SelectedDate);
            childViewHolder.lblDateTimeTime.setOnClickListener(childViewHolder.lblDateTimeTimeOnClickListener);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleDateTime. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleDateTime. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void handleYesNo(final ChildViewHolder childViewHolder, final checklist_PostData data, int position) {
        try {
            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.VISIBLE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener = cbForYesNoOnCheckedChangeListener(data);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.ManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.ManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                /*childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cb.setVisibility(View.VISIBLE);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForCheckboxOnCheckedChangeListener = cbForYesNoOnCheckedChangeListener(data);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.GONE);*/

                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            }

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);

            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.VISIBLE);
            childViewHolder.yesno.setEnabled((data.FinalManOptCode.equals("MD") && data.IsChecked) ? true : (data.FinalManOptCode.equals("MD") && !data.IsChecked) ? false : true);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            if (childViewHolder.yesnoOnCheckedChangeListener != null) {
                childViewHolder.yesno.setOnCheckedChangeListener(null);
            }

            Boolean haschild = hasChild(position);
            if (data.OptionValue != null && data.OptionValue.size() > 0) {
                childViewHolder.yesno.setChecked(data.OptionValue.get(0).equals("YES") ? true : false);
                childViewHolder.yesno.setText(data.OptionValue.get(0));

                /*if (data.OptionValue.get(0).equals("NO")) {
                    if (haschild) {
                        hideHierarchyNew2(position, childViewHolder);
                    }
                } else {
                    if (haschild) {
                        showHierarchyNew2(position, childViewHolder, data);
                    }
                }*/
            } else {
                childViewHolder.yesno.setChecked(false);
                childViewHolder.yesno.setText("NO");
                /*if (haschild) {
                    hideHierarchyNew2(position, childViewHolder);
                }*/
            }

            childViewHolder.yesnoOnCheckedChangeListener = yesnoOnCheckedChangeListener(position, data, childViewHolder);
            childViewHolder.yesno.setOnCheckedChangeListener(childViewHolder.yesnoOnCheckedChangeListener);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleYesNo. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleRD(final ChildViewHolder childViewHolder, final checklist_PostData data, int position) {
        try {
            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            }

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);

            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.VISIBLE);
            childViewHolder.yesno.setEnabled((data.FinalManOptCode.equals("MD") && data.IsChecked) ? true : (data.FinalManOptCode.equals("MD") && !data.IsChecked) ? false : true);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            if (childViewHolder.yesnoOnCheckedChangeListener != null) {
                childViewHolder.yesno.setOnCheckedChangeListener(null);
            }

            Boolean haschild = hasChild(position);
            if (data.OptionValue != null && data.OptionValue.size() > 0) {
                childViewHolder.yesno.setChecked(data.OptionValue.get(0).equals("YES") ? true : false);
                childViewHolder.yesno.setText(data.OptionValue.get(0));

                /*if (data.OptionValue.get(0).equals("NO")) {
                    if (haschild) {
                        hideHierarchyNew2(position, childViewHolder);
                    }
                } else {
                    if (haschild) {
                        showHierarchyNew2(position, childViewHolder, data);
                    }
                }*/
            } else {
                childViewHolder.yesno.setChecked(false);
                childViewHolder.yesno.setText("NO");

                /*if (haschild) {
                    hideHierarchyNew2(position, childViewHolder);
                }*/
            }

            childViewHolder.yesnoOnCheckedChangeListener = RDonCheckedChangedListener(position, data, childViewHolder);
            childViewHolder.yesno.setOnCheckedChangeListener(childViewHolder.yesnoOnCheckedChangeListener);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleRD. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void handleFAFYFN(final ChildViewHolder childViewHolder, final checklist_PostData data, int position) {
        try {
            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.VISIBLE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener = cbForYesNoOnCheckedChangeListener(data);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(Color.WHITE);
                childViewHolder.lblValue.setTypeface(null, data.ManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD.setVisibility(View.GONE);
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cb.setVisibility(View.VISIBLE);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setTextColor(Color.BLACK);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                /*childViewHolder.cb.setChecked(true);
                childViewHolder.cb.setEnabled(true);*/

                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);

                childViewHolder.cbForCheckboxOnCheckedChangeListener = cbForYesNoOnCheckedChangeListener(data);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.GONE);
            }

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);

            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.VISIBLE);
            childViewHolder.yesno.setEnabled(true);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            if (childViewHolder.yesnoOnCheckedChangeListener != null) {
                childViewHolder.yesno.setOnCheckedChangeListener(null);
            }

            if (data.OptionValue != null && data.OptionValue.size() > 0) {
                childViewHolder.yesno.setChecked(data.OptionValue.get(0).equals("YES") ? true : false);
                childViewHolder.yesno.setText(data.OptionValue.get(0));
            } else {
                childViewHolder.yesno.setChecked(false);
                childViewHolder.yesno.setText("NO");
            }

            childViewHolder.yesnoOnCheckedChangeListener = FAFYFNOnCheckedChangedListener(position, data, childViewHolder);
            childViewHolder.yesno.setOnCheckedChangeListener(childViewHolder.yesnoOnCheckedChangeListener);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleFAFYFN. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void handlePhoto(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position, final boolean itemproof) {
        try {
            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);

                /*childViewHolder.cb.setVisibility(View.VISIBLE);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForCheckboxOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.GONE);*/

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);
            }

            childViewHolder.imgCancelPhoto.setEnabled(data.dataT != null && data.dataT.size() > 0 ? true : false);
            childViewHolder.imgCancelPhoto.setImageResource(data.dataT != null && data.dataT.size() > 0 ? R.mipmap.ic_action_collections_enabled : R.mipmap.ic_action_collections);
            childViewHolder.imgPhoto.setEnabled(data.IsChecked ? true : false);
            childViewHolder.imgPhoto.setImageResource(data.IsChecked ? R.mipmap.ic_action_camera_alt_enabled : R.mipmap.ic_action_camera_alt);

            childViewHolder.imgPhoto.setOnClickListener(null);
            childViewHolder.imgCancelPhoto.setOnClickListener(null);
            childViewHolder.OpenCameraOnClickListener = rtnTakePhotoOnClickListener(position, itemproof);
            childViewHolder.ViewPhotoOnClickListener = rtnViewPhotoOnClickListener(position, data.ControlCode.equals("PR") ? true : false);
            childViewHolder.imgPhoto.setOnClickListener(childViewHolder.OpenCameraOnClickListener);
            childViewHolder.imgCancelPhoto.setOnClickListener(childViewHolder.ViewPhotoOnClickListener);

            childViewHolder.cbPhoto.setChecked(data.dataT != null && data.dataT.size() > 0 ? true : false);
            childViewHolder.lblphotorequiredlabel.setText(data.dataT != null && data.dataT.size() > 0 ? R.string.lblphotoacquired : R.string.lblphotorequired);
            childViewHolder.lblphotorequiredlabel.setTextColor(data.dataT != null && data.dataT.size() > 0 ? Color.BLACK : Color.RED);

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);

            childViewHolder.LLPhoto.setVisibility(View.VISIBLE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");


        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handlePhoto. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void handleCheckBoxGroup(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.VISIBLE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                //childViewHolder.lblValue.setText(data.DisplayText);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cb.setVisibility(View.VISIBLE);
                //childViewHolder.cb.setText(data.DisplayText);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForCheckboxOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.GONE);
            }

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);

            childViewHolder.LLCheckbox.setVisibility(View.VISIBLE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.rg1.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");


            final int childcount = childViewHolder.LLCheckbox.getChildCount();
            if (childcount == 0) {
                //newly initialised CBL
                //add cb to rbl
                for (int i = 0; i <= data.Option.length - 1; i++) {
                    final CheckBox cb = (CheckBox) LayoutInflater.from(getContext()).inflate(R.layout.custom_checkbox, null);
                    cb.setText(data.Option[i]);
                    cb.setId(i + 1 + 1000);
                    childViewHolder.LLCheckbox.addView(cb);
                }
                //add cb to rbl

                for (int i = 0; i <= childViewHolder.LLCheckbox.getChildCount() - 1; i++) {
                    final CheckBox cb = (CheckBox) childViewHolder.LLCheckbox.getChildAt(i);
                    final int rbnIndex = i;
                    cb.setOnCheckedChangeListener(null);
                    cb.setChecked(data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.get(i).equals("YES") ? true : false);
                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                if (data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.size() == childViewHolder.LLCheckbox.getChildCount()) {
                                    data.OptionValue.set(rbnIndex, "YES");
                                } else {
                                    data.OptionValue = new ArrayList<String>();
                                    for (int i = 0; i <= childViewHolder.LLCheckbox.getChildCount() - 1; i++) {
                                        if (i != rbnIndex) {
                                            data.OptionValue.add("NO");
                                        } else {
                                            data.OptionValue.add("YES");
                                        }
                                    }
                                }
                            } else {
                                if (data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.size() == childViewHolder.LLCheckbox.getChildCount()) {
                                    data.OptionValue.set(rbnIndex, "NO");
                                } else {
                                    data.OptionValue = new ArrayList<String>();
                                    for (int i = 0; i <= childViewHolder.LLCheckbox.getChildCount() - 1; i++) {
                                        if (i != rbnIndex) {
                                            data.OptionValue.add("NO");
                                        } else {
                                            data.OptionValue.add("NO");
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            } else {
                //CheckListActivity onResume after onPause, views are already loaded b4
                for (int xx = 0; xx <= childcount - 1; xx++) {
                    final CheckBox cb = (CheckBox) childViewHolder.LLCheckbox.getChildAt(xx);
                    final int rbnIndex = xx;
                    cb.setOnCheckedChangeListener(null);
                    cb.setChecked(data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.get(xx).equals("YES") ? true : false);

                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                if (data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.size() == childcount) {
                                    data.OptionValue.set(rbnIndex, "YES");
                                } else {
                                    data.OptionValue = new ArrayList<String>();
                                    for (int i = 0; i <= childcount - 1; i++) {
                                        if (i != rbnIndex) {
                                            data.OptionValue.add("NO");
                                        } else {
                                            data.OptionValue.add("YES");
                                        }
                                    }
                                }
                            } else {
                                if (data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.size() == childcount) {
                                    data.OptionValue.set(rbnIndex, "NO");
                                } else {
                                    data.OptionValue = new ArrayList<String>();
                                    for (int i = 0; i <= childcount - 1; i++) {
                                        if (i != rbnIndex) {
                                            data.OptionValue.add("NO");
                                        } else {
                                            data.OptionValue.add("NO");
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }

            if (data.FinalManOptCode.equals("MD") && !data.IsChecked) {
                for (int i = 0; i <= childViewHolder.LLCheckbox.getChildCount() - 1; i++) {
                    ((CheckBox) childViewHolder.LLCheckbox.getChildAt(i)).setEnabled(false);
                }
            } else {
                for (int i = 0; i <= childViewHolder.LLCheckbox.getChildCount() - 1; i++) {
                    ((CheckBox) childViewHolder.LLCheckbox.getChildAt(i)).setEnabled(true);
                }
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleCheckBoxGroup. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleCheckBoxGroup. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void handleRadioGroup(final ChildViewHolder childViewHolder, final checklist_PostData data, final int position) {
        try {
            if (data.IsParent && !data.IsChild) {
                childViewHolder.cbMD2.setVisibility(View.VISIBLE);

                childViewHolder.cbMD2.setOnCheckedChangeListener(null);
                childViewHolder.cbMD2.setChecked(data.IsChecked ? true : false);
                childViewHolder.cbMD2.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbMDOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cbMD2.setOnCheckedChangeListener(childViewHolder.cbMDOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                //childViewHolder.lblValue.setText(data.DisplayText);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);

                childViewHolder.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                /*childViewHolder.cbMD2.setVisibility(View.GONE);

                childViewHolder.cb.setVisibility(View.VISIBLE);
                childViewHolder.cb.setText(data.DisplayTextCN);
                childViewHolder.cb.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.cb.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setOnCheckedChangeListener(null);
                childViewHolder.cb.setChecked(data.IsChecked ? true : false);
                childViewHolder.cb.setEnabled(data.IsChecked && data.FinalManOptCode.equals("MD") ? false : true);
                childViewHolder.cbForCheckboxOnCheckedChangeListener =
                        data.FinalManOptCode.equals("MD") ? cbMDOnCheckedChangeListener(data, childViewHolder, position) : cbOPOnCheckedChangeListener(data, childViewHolder, position);
                childViewHolder.cb.setOnCheckedChangeListener(childViewHolder.cbForCheckboxOnCheckedChangeListener);

                childViewHolder.lblValue.setVisibility(View.GONE);*/

                childViewHolder.cbMD2.setVisibility(View.GONE);
                childViewHolder.lblValue.setVisibility(View.VISIBLE);
                childViewHolder.lblValue.setText(data.DisplayTextCN);
                childViewHolder.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                childViewHolder.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                childViewHolder.cb.setVisibility(View.GONE);
            }

            childViewHolder.LLDisplayText.setVisibility(View.GONE);
            childViewHolder.LLOthers.setVisibility(View.VISIBLE);

            childViewHolder.rg1.setVisibility(View.VISIBLE);
            childViewHolder.LLPhoto.setVisibility(View.GONE);
            childViewHolder.cbMD.setVisibility(View.GONE);
            childViewHolder.txtValue.setVisibility(View.GONE);
            childViewHolder.txtValue2.setVisibility(View.GONE);
            childViewHolder.LLtxtValue.setVisibility(childViewHolder.txtValue.getVisibility());
            childViewHolder.LLCheckbox.setVisibility(View.GONE);
            childViewHolder.LLDateTxtB.setVisibility(View.GONE);
            childViewHolder.LLDate.setVisibility(View.GONE);
            childViewHolder.LLDateTime.setVisibility(View.GONE);
            childViewHolder.yesno.setVisibility(View.GONE);
            childViewHolder.LLIDVerification.setVisibility(View.GONE);
            childViewHolder.btnOTP.setVisibility(View.GONE);

            childViewHolder.btnVerificationIMEI.setOnClickListener(null);
            childViewHolder.LLIMEIVerification.setVisibility(View.GONE);
            childViewHolder.BtnV2VerificationOnClickListener = null;
            childViewHolder.BtnV3VerificationOnClickListener = null;
            childViewHolder.BtnV4VerificationOnClickListener = null;
            childViewHolder.lblIMEIVerificationVal.setText("");
            childViewHolder.lblIMVCFirstFewChar.setText("");
            childViewHolder.txtIMEIVerification.setText("");

            childViewHolder.LLArticleTag.setVisibility(View.GONE);
            childViewHolder.btnScanTag.setOnClickListener(null);
            childViewHolder.btnUpdateLabelTag.setOnClickListener(null);
            childViewHolder.txtLabelTagVal.setText("");

            childViewHolder.btnUpdateLabelTagOnClickListener = null;
            childViewHolder.btnScanTagOnClickListener = null;

            childViewHolder.LLUpdatePieces.setVisibility(View.GONE);
            childViewHolder.txtLabelUpdatePiecesVal.setText("");


            final int rg1childcount = childViewHolder.rg1.getChildCount();
            if (rg1childcount == 0) {
                //newly initialised RBL
                if (data.OptionCN != null && data.OptionCN.length > 0) {
                    //add rb to rbl
                    for (int i = 0; i <= data.OptionCN.length - 1; i++) {
                        final RadioButton rbn = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.custom_radiobutton, null);
                        rbn.setId(i + 1 + 1000);
                        rbn.setText(data.OptionCN[i]);
                        rbn.setTextColor(Color.BLACK);
                        childViewHolder.rg1.addView(rbn);
                    }
                    //add rb to rbl

                    for (int xx = 0; xx <= childViewHolder.rg1.getChildCount() - 1; xx++) {
                        final RadioButton rbn = (RadioButton) childViewHolder.rg1.getChildAt(xx);
                        final int rbnIndex = xx;
                        rbn.setOnClickListener(null);

                        if (data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.size() == childViewHolder.rg1.getChildCount()) {
                            rbn.setChecked(data.OptionValue.get(xx).equals("YES") ? true : false);
                        } else {
                                /*data.OptionValue = new ArrayList<String>();
                                for (int w = 0; w <= childViewHolder.rg1.getChildCount() - 1; w++) {
                                    data.OptionValue.add("NO");
                                }
                                rbn.setChecked(data.OptionValue.get(xx).equals("YES") ? true : false);*/
                        }

                        rbn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.size() == childViewHolder.rg1.getChildCount()) {
                                    for (int i = 0; i <= data.OptionValue.size() - 1; i++) {
                                        if (i != rbnIndex) {
                                            data.OptionValue.set(i, "NO");
                                        } else if (i == rbnIndex) {
                                            data.OptionValue.set(i, "YES");
                                        }
                                    }
                                } else {
                                    data.OptionValue = new ArrayList<String>();
                                    for (int i = 0; i <= childViewHolder.rg1.getChildCount() - 1; i++) {
                                        if (i != rbnIndex) {
                                            data.OptionValue.add("NO");
                                        } else {
                                            data.OptionValue.add("YES");
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            } else {
                //CheckListActivity onResume after onPause, views are already loaded b4
                for (int xx = 0; xx <= rg1childcount - 1; xx++) {
                    final RadioButton rbn = (RadioButton) childViewHolder.rg1.getChildAt(xx);
                    final int rbnIndex = xx;

                    rbn.setOnClickListener(null);
                    rbn.setChecked(data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.size() == rg1childcount
                            && data.OptionValue.get(rbnIndex).equals("YES") ? true : false);
                    rbn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.size() == rg1childcount) {
                                for (int i = 0; i <= data.OptionValue.size() - 1; i++) {
                                    if (i != rbnIndex) {
                                        data.OptionValue.set(i, "NO");
                                    } else if (i == rbnIndex) {
                                        data.OptionValue.set(i, "YES");
                                    }
                                }
                            } else {
                                data.OptionValue = new ArrayList<String>();
                                for (int i = 0; i <= rg1childcount - 1; i++) {
                                    if (i != rbnIndex) {
                                        data.OptionValue.add("NO");
                                    } else {
                                        data.OptionValue.add("YES");
                                    }
                                }
                            }
                        }
                    });
                }
            }

            if (data.FinalManOptCode.equals("MD") && !data.IsChecked) {
                for (int i = 0; i <= childViewHolder.rg1.getChildCount() - 1; i++) {
                    ((RadioButton) childViewHolder.rg1.getChildAt(i)).setEnabled(false);
                    ((RadioButton) childViewHolder.rg1.getChildAt(i)).setTextColor(Color.BLACK);
                }
            } else {
                for (int i = 0; i <= childViewHolder.rg1.getChildCount() - 1; i++) {
                    ((RadioButton) childViewHolder.rg1.getChildAt(i)).setEnabled(true);
                    ((RadioButton) childViewHolder.rg1.getChildAt(i)).setTextColor(Color.BLACK);
                }
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleRadioGroup. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter handleRadioGroup. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void handleRO(final ChildViewHolder ch, final checklist_PostData data, final int position) {
        try {
            if (data.IsParent && !data.IsChild) {
                ch.cbMD2.setVisibility(View.GONE);
                ch.lblValue.setVisibility(View.VISIBLE);
                ch.lblValue.setText(data.DisplayTextCN);
                ch.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                ch.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                ch.cb.setVisibility(View.GONE);
            } else if (data.IsChild) {
                ch.cbMD2.setVisibility(View.GONE);
                ch.lblValue.setVisibility(View.VISIBLE);
                ch.lblValue.setText(data.DisplayTextCN);
                ch.lblValue.setTextColor(data.FinalManOptCode.equals("MD") ? Color.RED : Color.BLACK);
                ch.lblValue.setTypeface(null, data.FinalManOptCode.equals("MD") ? Typeface.BOLD : Typeface.NORMAL);
                ch.cb.setVisibility(View.GONE);
            }

            ch.LLDisplayText.setVisibility(View.GONE);
            ch.LLOthers.setVisibility(View.VISIBLE);

            ch.rg1.setVisibility(View.VISIBLE);
            ch.LLPhoto.setVisibility(View.GONE);
            ch.cbMD.setVisibility(View.GONE);
            ch.txtValue.setVisibility(View.GONE);
            ch.txtValue2.setVisibility(View.GONE);
            ch.LLtxtValue.setVisibility(ch.txtValue.getVisibility());

            if (ch.rotxtValueTextChangeListener != null) {
                ch.txtValue.removeTextChangedListener(ch.rotxtValueTextChangeListener);
                ch.rotxtValueTextChangeListener = null;
            }

            ch.txtValue.setText("");
            ch.txtValue.setHint(myApp.getAppContext().getString(R.string.hintenterreasonshort));
            ch.LLCheckbox.setVisibility(View.GONE);
            ch.LLDateTxtB.setVisibility(View.GONE);
            ch.LLDate.setVisibility(View.GONE);
            ch.LLDateTime.setVisibility(View.GONE);
            ch.yesno.setVisibility(View.GONE);
            ch.LLIDVerification.setVisibility(View.GONE);
            ch.btnOTP.setVisibility(View.GONE);

            ch.btnVerificationIMEI.setOnClickListener(null);
            ch.LLIMEIVerification.setVisibility(View.GONE);
            ch.BtnV2VerificationOnClickListener = null;
            ch.BtnV3VerificationOnClickListener = null;
            ch.BtnV4VerificationOnClickListener = null;
            ch.lblIMEIVerificationVal.setText("");
            ch.lblIMVCFirstFewChar.setText("");
            ch.txtIMEIVerification.setText("");

            ch.LLArticleTag.setVisibility(View.GONE);
            ch.btnScanTag.setOnClickListener(null);
            ch.btnUpdateLabelTag.setOnClickListener(null);
            ch.txtLabelTagVal.setText("");

            ch.btnUpdateLabelTagOnClickListener = null;
            ch.btnScanTagOnClickListener = null;

            ch.LLUpdatePieces.setVisibility(View.GONE);
            ch.txtLabelUpdatePiecesVal.setText("");


            final int rg1childcount = ch.rg1.getChildCount();
            if (rg1childcount == 0) {
                //newly initialised RBL
                if (data.OptionCN != null && data.OptionCN.length > 0) {
                    //add rb to rbl
                    for (int i = 0; i <= data.OptionCN.length - 1; i++) {
                        final RadioButton rbn = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.custom_radiobutton, null);
                        rbn.setId(i + 1 + 1000);
                        rbn.setText(data.OptionCN[i]);
                        rbn.setTextColor(Color.BLACK);
                        ch.rg1.addView(rbn);
                    }
                    //add rb to rbl

                    for (int xx = 0; xx <= ch.rg1.getChildCount() - 1; xx++) {
                        final RadioButton rbn = (RadioButton) ch.rg1.getChildAt(xx);
                        final int rbnIndex = xx;
                        final boolean isLastIndex = (rbnIndex == ch.rg1.getChildCount() - 1);
                        rbn.setOnClickListener(null);

                        if (data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.size() == ch.rg1.getChildCount()) {
                            rbn.setChecked(data.OptionValue.get(xx).equals("YES") ? true : false);
                            if (data.OptionValue.get(xx).equals("YES") && xx == ch.rg1.getChildCount() - 1) {
                                ch.txtValue.setVisibility(View.VISIBLE);
                                ch.LLtxtValue.setVisibility(ch.txtValue.getVisibility());
                                ch.txtValue.setText(data.OptionValue2 != null && data.OptionValue2.size() > 0 && !objGlobalMethods.IsEmptyString(data.OptionValue2.get(0).trim()) ? data.OptionValue2.get(0).trim() : "");
                                ch.rotxtValueTextChangeListener = new ROtxtValueTextChangeListener(data);
                                ch.txtValue.addTextChangedListener(ch.rotxtValueTextChangeListener);
                                ch.txtValue.setEnabled(true);
                            }
                        }

                        rbn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.size() == ch.rg1.getChildCount()) {
                                    for (int i = 0; i <= data.OptionValue.size() - 1; i++) {
                                        if (i != rbnIndex) {
                                            data.OptionValue.set(i, "NO");
                                            data.setOptionValue2(null);

                                            ch.txtValue.setVisibility(View.GONE);
                                            ch.LLtxtValue.setVisibility(ch.txtValue.getVisibility());

                                        } else if (i == rbnIndex) {
                                            data.OptionValue.set(i, "YES");
                                            ch.txtValue.setVisibility(isLastIndex ? View.VISIBLE : View.GONE);
                                            ch.LLtxtValue.setVisibility(ch.txtValue.getVisibility());
                                            if (isLastIndex) {
                                                ch.rotxtValueTextChangeListener = new ROtxtValueTextChangeListener(data);
                                                ch.txtValue.addTextChangedListener(ch.rotxtValueTextChangeListener);
                                                ch.txtValue.setEnabled(true);
                                                RadioButton rbtn = (RadioButton) ch.rg1.getChildAt(rbnIndex);
                                                rbtn.setChecked(true);
                                            } else {
                                                if (ch.rotxtValueTextChangeListener != null) {
                                                    ch.txtValue.removeTextChangedListener(ch.rotxtValueTextChangeListener);
                                                    ch.rotxtValueTextChangeListener = null;
                                                    ch.txtValue.setText("");
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    data.OptionValue = new ArrayList<String>();
                                    for (int i = 0; i <= ch.rg1.getChildCount() - 1; i++) {
                                        if (i != rbnIndex) {
                                            data.OptionValue.add("NO");
                                            data.setOptionValue2(null);
                                            ch.txtValue.setVisibility(View.GONE);
                                            ch.LLtxtValue.setVisibility(ch.txtValue.getVisibility());
                                        } else {
                                            data.OptionValue.add("YES");
                                            ch.txtValue.setVisibility(isLastIndex ? View.VISIBLE : View.GONE);
                                            ch.LLtxtValue.setVisibility(ch.txtValue.getVisibility());
                                            if (isLastIndex) {
                                                ch.rotxtValueTextChangeListener = new ROtxtValueTextChangeListener(data);
                                                ch.txtValue.addTextChangedListener(ch.rotxtValueTextChangeListener);
                                                ch.txtValue.setEnabled(true);
                                                RadioButton rbtn = (RadioButton) ch.rg1.getChildAt(rbnIndex);
                                                rbtn.setChecked(true);
                                            } else {
                                                if (ch.rotxtValueTextChangeListener != null) {
                                                    ch.txtValue.removeTextChangedListener(ch.rotxtValueTextChangeListener);
                                                    ch.rotxtValueTextChangeListener = null;
                                                    ch.txtValue.setText("");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            } else {
                //CheckListActivity onResume after onPause, views are already loaded b4
                ch.rg1.clearCheck();
                for (int xx = 0; xx <= rg1childcount - 1; xx++) {
                    final RadioButton rbn = (RadioButton) ch.rg1.getChildAt(xx);
                    final int rbnIndex = xx;
                    final boolean isLastIndex = (rbnIndex == ch.rg1.getChildCount() - 1);

                    rbn.setChecked(data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.size() == rg1childcount
                            && data.OptionValue.get(rbnIndex).equals("YES") ? true : false);

                    if (data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.get(xx).equals("YES") && xx == ch.rg1.getChildCount() - 1) {
                        ch.txtValue.setVisibility(View.VISIBLE);
                        ch.LLtxtValue.setVisibility(ch.txtValue.getVisibility());
                        ch.txtValue.setText(data.OptionValue2 != null && data.OptionValue2.size() > 0 && !objGlobalMethods.IsEmptyString(data.OptionValue2.get(0).trim()) ? data.OptionValue2.get(0).trim() : "");
                        ch.rotxtValueTextChangeListener = new ROtxtValueTextChangeListener(data);
                        ch.txtValue.addTextChangedListener(ch.rotxtValueTextChangeListener);
                        ch.txtValue.setEnabled(true);
                    }

                    rbn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (data.OptionValue != null && data.OptionValue.size() > 0 && data.OptionValue.size() == rg1childcount) {
                                for (int i = 0; i <= data.OptionValue.size() - 1; i++) {
                                    if (i != rbnIndex) {
                                        data.OptionValue.set(i, "NO");
                                        data.setOptionValue2(null);
                                        ch.txtValue.setVisibility(View.GONE);
                                        ch.LLtxtValue.setVisibility(ch.txtValue.getVisibility());
                                    } else if (i == rbnIndex) {
                                        data.OptionValue.set(i, "YES");
                                        ch.txtValue.setVisibility(isLastIndex ? View.VISIBLE : View.GONE);
                                        ch.LLtxtValue.setVisibility(ch.txtValue.getVisibility());
                                        if (isLastIndex) {
                                            ch.rotxtValueTextChangeListener = new ROtxtValueTextChangeListener(data);
                                            ch.txtValue.addTextChangedListener(ch.rotxtValueTextChangeListener);
                                            ch.txtValue.setEnabled(true);
                                            RadioButton rbtn = (RadioButton) ch.rg1.getChildAt(rbnIndex);
                                            rbtn.setChecked(true);
                                        } else {
                                            if (ch.rotxtValueTextChangeListener != null) {
                                                ch.txtValue.removeTextChangedListener(ch.rotxtValueTextChangeListener);
                                                ch.rotxtValueTextChangeListener = null;
                                                ch.txtValue.setText("");
                                            }
                                        }
                                    }
                                }
                            } else {
                                data.OptionValue = new ArrayList<String>();
                                for (int i = 0; i <= rg1childcount - 1; i++) {
                                    if (i != rbnIndex) {
                                        data.OptionValue.add("NO");
                                        data.setOptionValue2(null);
                                        ch.txtValue.setVisibility(View.GONE);
                                        ch.LLtxtValue.setVisibility(ch.txtValue.getVisibility());
                                    } else {
                                        data.OptionValue.add("YES");
                                        ch.txtValue.setVisibility(isLastIndex ? View.VISIBLE : View.GONE);
                                        ch.LLtxtValue.setVisibility(ch.txtValue.getVisibility());
                                        if (isLastIndex) {
                                            ch.rotxtValueTextChangeListener = new ROtxtValueTextChangeListener(data);
                                            ch.txtValue.addTextChangedListener(ch.rotxtValueTextChangeListener);
                                            ch.txtValue.setEnabled(true);
                                            RadioButton rbtn = (RadioButton) ch.rg1.getChildAt(rbnIndex);
                                            rbtn.setChecked(true);
                                        } else {
                                            if (ch.rotxtValueTextChangeListener != null) {
                                                ch.txtValue.removeTextChangedListener(ch.rotxtValueTextChangeListener);
                                                ch.rotxtValueTextChangeListener = null;
                                                ch.txtValue.setText("");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }

            if (data.FinalManOptCode.equals("MD") && !data.IsChecked) {
                for (int i = 0; i <= ch.rg1.getChildCount() - 1; i++) {
                    ((RadioButton) ch.rg1.getChildAt(i)).setEnabled(false);
                    ((RadioButton) ch.rg1.getChildAt(i)).setTextColor(Color.BLACK);
                }
            } else {
                for (int i = 0; i <= ch.rg1.getChildCount() - 1; i++) {
                    ((RadioButton) ch.rg1.getChildAt(i)).setEnabled(true);
                    ((RadioButton) ch.rg1.getChildAt(i)).setTextColor(Color.BLACK);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter handleRO. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private class ROtxtValueTextChangeListener implements TextWatcher {

        private final checklist_PostData data;

        public ROtxtValueTextChangeListener(checklist_PostData adata) {
            this.data = adata;

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String value = s.toString();
            if (value.length() > 0) {
                if (data.OptionValue2 != null && data.OptionValue2.size() > 0) {
                    data.OptionValue2.clear();
                    data.OptionValue2.add(value);
                } else if (data.OptionValue2 != null && data.OptionValue2.size() == 0) {
                    data.OptionValue2.clear();
                    data.OptionValue2.add(value);
                } else if (data.OptionValue2 == null) {
                    data.OptionValue2 = new ArrayList<>();
                    data.OptionValue2.add(value);
                }
            } else {
                if (data.FinalManOptCode.equals("OP")) {
                    data.OptionValue2.clear();
                    data.OptionValue2.add(value);
                } else {
                    data.OptionValue2 = null;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private boolean showFAFYFN2(int currPos, int type, final checklist_PostData fafyfn) {
        //type 1 == AY (any 1 is YES) 2 == AN (any 1 is NO) 3 == FY (all must be YES) 4 == FN (all must be NO)
        boolean asd = type == 3 || type == 4 ? true : false;
        int fafyfnHierarchyCode = -999;
        checklist_PostData sibling = null;
        checklist_PostData parent = null;
        try {
            if (list != null && list.size() - 1 >= currPos && currPos - 1 >= 0) {
                if (fafyfn != null) {
                    fafyfnHierarchyCode = fafyfn.HierarchyCode;
                    for (int i = currPos - 1; i >= 0; i--) {
                        parent = null;
                        sibling = list.get(i);
                        if (sibling.HierarchyCode >= fafyfnHierarchyCode && sibling.ControlCode.equals("YN") && (!(sibling.IsChild && sibling.IsParent) && sibling.IsChild && !sibling.IsParent)) {
                            //check YN sibling only
                            //type 1 == AY (any 1 is YES) 2 == AN (any 1 is NO) 3 == FY (all must be YES) 4 == FN (all must be NO)
                            if (type == 1) {
                                if (sibling.OptionValue != null && sibling.OptionValue.size() > 0 && sibling.OptionValue.get(0).equals("YES")) {
                                    asd = true;
                                    break;
                                }
                            } else if (type == 2) {
                                if (sibling.OptionValue != null && sibling.OptionValue.size() > 0 && sibling.OptionValue.get(0).equals("NO")) {
                                    asd = true;
                                    break;
                                }
                            } else if (type == 3) {
                                if (sibling.OptionValue != null && sibling.OptionValue.size() > 0 && sibling.OptionValue.get(0).equals("NO")) {
                                    asd = false;
                                    break;
                                }
                            } else if (type == 4) {
                                if (sibling.OptionValue != null && sibling.OptionValue.size() > 0 && sibling.OptionValue.get(0).equals("YES")) {
                                    asd = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter showFAFYFN2. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean canProceedToFailNew(int currPos) {
        boolean asd = true;
        checklist_PostData d = null;
        checklist_PostData pd = null;
        boolean isParent = false;
        boolean isChild = false;
        boolean isMD = false;
        try {
            if (list != null && list.size() > 0) {
                outer:
                for (int i = 0; i <= list.size() - 1; i++) {
                    d = list.get(i);
                    isMD = d.FinalManOptCode.equals("MD");
                    isParent = d.IsParent;
                    isChild = d.IsChild;
                    if (i == currPos) {
                        break outer;
                    } else if (d.FinalManOptCode.equals("MD") && ((!(isChild && isParent)) || (isChild && !isParent) || (isChild && !isParent) || (!isChild && isParent))) {

                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter canProceedToFailNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean canProceedToFail(int currPos) {
        boolean asd = true;
        checklist_PostData d = null;
        checklist_PostData pd = null;
        try {
            if (list != null && list.size() > 0) {
                outer:
                for (int i = 0; i <= list.size() - 1; i++) {
                    d = list.get(i);
                    pd = null;
                    if (i == currPos) {
                        break outer;
                    } else if (d.FinalManOptCode.equals("MD") && ((!(d.IsChild && d.IsParent)) || (d.IsChild && !d.IsParent) || (d.IsChild && !d.IsParent) || (!d.IsChild && d.IsParent))) {
                        if (d.FinalManOptCode.equals("MD") && d.ControlCode.equals("YN") && d.IsChild && !d.IsParent) {
                            if (i - 1 > 0) {
                                pd = list.get(d.ParentIDX);
                                if (pd != null && pd.ControlCode.equals("YN") && pd.IsParent) {
                                    if (pd.IsChecked) {
                                        if (ArrayListIsNullOrEmpty(pd.OptionValue)) {
                                            //do nothing, YN not selected. default value is NO
                                        } else if (pd.OptionValue.get(0).equals("YES")) {
                                            if (!d.IsChecked) {
                                                asd = false;
                                                break outer;
                                            }
                                        }
                                    } else {
                                        asd = false;
                                        break outer;
                                    }
                                }
                            }
                        } else if (d.FinalManOptCode.equals("MD") && (d.ControlCode.equals("PH") || d.ControlCode.equals("PR")) &&
                                (d.dataT == null || (d.dataT != null && d.dataT.size() == 0))) {
                            asd = false;
                            break outer;
                        } else if (d.FinalManOptCode.equals("MD") && !d.getIsChecked() && (d.OptionValue == null || d.OptionValue.size() == 0)) {
                            asd = false;
                            break outer;
                        } else if (d.FinalManOptCode.equals("MD") && d.getIsChecked() && (d.OptionValue == null || d.OptionValue.size() == 0)) {
                            asd = false;
                            break outer;
                        } else if (d.FinalManOptCode.equals("MD") && d.getIsChecked() && (d.OptionValue != null && d.OptionValue.size() > 0)
                                && d.ControlCode.equals("CG")) {
                            int c = 0;
                            for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                                if (d.OptionValue.get(x).equals("NO")) {
                                    c++;
                                }
                            }
                            if (c == list.get(i).OptionValue.size()) {
                                asd = false;
                            }
                        } else if (d.FinalManOptCode.equals("MD") && d.getIsChecked() && (d.OptionValue != null && d.OptionValue.size() > 0)
                                && d.ControlCode.equals("RG")) {
                            int c = 0;
                            for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                                if (d.OptionValue.get(x).equals("NO")) {
                                    c++;
                                }
                            }
                            if (c == d.OptionValue.size()) {
                                asd = false;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter canProceedToFail. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    private boolean ArrayListIsNullOrEmpty(ArrayList a) {
        boolean asd = false;
        try {
            asd = (a == null || a.size() == 0);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter ArrayListIsNullOrEmpty. Exception: " + e + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private CompoundButton.OnCheckedChangeListener RDonCheckedChangedListener(final int position, final checklist_PostData data, final ChildViewHolder childViewHolder) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    //notifyDataSetChanged();
                    SharedPreferences g = objGlobalMethods.getGcmPreferences();
                    if (isChecked) {
                        if (data.OptionValue != null && data.OptionValue.size() > 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add("YES");
                        } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add("YES");
                        } else if (data.OptionValue == null) {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add("YES");
                        }

                        childViewHolder.yesno.setText("YES");
                        Boolean haschild = hasChild(position);
                        if (haschild) {
                            /*selectedShowParentPos = position;
                            selectedHideParentPos = -1;
                            showHierarchyNew2(position, childViewHolder, data);*/
                            hideHierarchyOnly(position, childViewHolder);
                        }
                        String[] prefName = new String[]{"PreDeterminedStatusCodeIDX"};
                        String[] prefvalue = new String[]{"129"}; //PreDeterminedStatusCodeIDX 80 is Rejected Delivery
                        objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);
                    } else {
                        if (data.FinalManOptCode.equals("OP")) {
                            if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                data.OptionValue = null;
                            } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                data.OptionValue = null;
                            } else if (data.OptionValue == null) {
                                data.OptionValue = null;
                            }
                        } else {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add("NO");
                        }

                        childViewHolder.yesno.setText("NO");
                        Boolean haschild = hasChild(position);
                        if (haschild) {
                            /*selectedShowParentPos = -1;
                            selectedHideParentPos = position;
                            hideHierarchyNew2(position, childViewHolder);*/
                            hideHierarchyOnly(position, childViewHolder);
                        }
                        String[] prefName = new String[]{"PreDeterminedStatusCodeIDX"};
                        String[] prefvalue = new String[]{""}; //PreDeterminedStatusCodeIDX 80 is Rejected Delivery
                        objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);
                    }

                    notifyDataSetChanged();
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter FAFYFNOnCheckedChangedListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener FAFYFNOnCheckedChangedListener(final int position, final checklist_PostData data, final ChildViewHolder childViewHolder) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        //prompt to go Fail page
                        //no need to set any value or change text to yes,
                        //cuz it will ask if want to proceed to fail.
                        //if click no, remains as no, if click yes, str8 away fail page
                        //if return from fail page, means its a NO too


                        /*if (data.OptionValue != null && data.OptionValue.size() > 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add("YES");
                        } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add("YES");
                        } else if (data.OptionValue == null) {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add("YES");
                        }

                        childViewHolder.yesno.setText("YES");*/

                    } else {
                        //do nothing
                        /*if (data.FinalManOptCode.equals("OP")) {
                            if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                data.OptionValue = null;
                            } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                data.OptionValue = null;
                            } else if (data.OptionValue == null) {
                                data.OptionValue = null;
                            }
                        } else {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add("NO");
                        }

                        childViewHolder.yesno.setText("NO");*/
                    }
                    //notifyDataSetChanged();
                    if (isChecked) {
                        promptFailpage(childViewHolder, data, position);
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter FAFYFNOnCheckedChangedListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

    private String getChecklistValueForFail(int currPos) {
        String v = "";
        try {
            if (list != null && list.size() > 0) {
                for (int i = 0; i <= list.size() - 1; i++) {
                    if (i == currPos) {
                        break;
                    } else if (!list.get(i).ControlCode.equals("LB")) {
                        if (list.get(i).ControlCode.equals("CG")) {
                            if (list.get(i).OptionValue != null && list.get(i).OptionValue.size() > 0) {
                                v += list.get(i).getDisplayText().trim() + ":\n";
                                for (int x = 0; x <= list.get(i).OptionValue.size() - 1; x++) {
                                    if (list.get(i).OptionValue.get(x).equals("YES")) {
                                        v += " - " + list.get(i).Option[x].trim() + "\n";
                                    }
                                }
                            }
                        } else if (list.get(i).ControlCode.equals("RG")) {
                            if (list.get(i).OptionValue != null && list.get(i).OptionValue.size() > 0) {
                                v += list.get(i).getDisplayText().trim() + ": ";
                                for (int x = 0; x <= list.get(i).OptionValue.size() - 1; x++) {
                                    if (list.get(i).OptionValue.get(x).equals("YES")) {
                                        v += list.get(i).Option[x].trim() + "\n";
                                        break;
                                    }
                                }
                            }
                        } else if (list.get(i).ControlCode.equals("PH") || list.get(i).ControlCode.equals("PR")) {
                            ArrayList<CustomGallery> dataT = list.get(i).dataT;
                            boolean hasimg = false;
                            if (dataT != null && dataT.size() > 0) {
                                for (int t = 0; t <= dataT.size() - 1; t++) {
                                    if (dataT.get(t) != null && !objGlobalMethods.IsEmptyString(dataT.get(t).sdcardPath)) {
                                        if (CheckListActivity.dataTlist == null) {
                                            CheckListActivity.dataTlist = new ArrayList<>();
                                        }
                                        CheckListActivity.dataTlist.add(dataT.get(t));
                                        hasimg = true;
                                    }
                                }
                            }
                            if (hasimg) {
                                v += list.get(i).getDisplayText().trim() + ": YES" + "\n";
                            } else {
                                v += list.get(i).getIsChecked() ? list.get(i).getDisplayText().trim() + ": NO" + "\n" : "";
                            }
                        } else if (list.get(i).ControlCode.equals("ID")) {
                            if (list.get(i).OptionValue != null && list.get(i).OptionValue.size() > 0) {
                                v += "Verified ID:";
                                for (int x = 0; x <= list.get(i).OptionValue.size() - 1; x++) {
                                    if (x == 0) {
                                        v += " " + list.get(i).OptionValue.get(x).trim().toUpperCase() + "\n";
                                    } else {
                                        v += ", " + list.get(i).OptionValue.get(x).trim().toUpperCase() + "\n";
                                    }
                                }
                            }
                        } else if (list.get(i).ControlCode.equals("V1")) {
                            if (list.get(i).OptionValue != null && list.get(i).OptionValue.size() > 0) {
                                v += "Verified Contact Number:";
                                for (int x = 0; x <= list.get(i).OptionValue.size() - 1; x++) {
                                    if (x == 0) {
                                        v += " " + list.get(i).OptionValue.get(x).trim().toUpperCase() + "\n";
                                    } else {
                                        v += ", " + list.get(i).OptionValue.get(x).trim().toUpperCase() + "\n";
                                    }
                                }
                            }
                        } else if (list.get(i).ControlCode.equals("V5")) {
                            if (list.get(i).OptionValue != null && list.get(i).OptionValue.size() > 0) {
                                v += "Verified Unit Number:";
                                for (int x = 0; x <= list.get(i).OptionValue.size() - 1; x++) {
                                    if (x == 0) {
                                        v += " " + list.get(i).OptionValue.get(x).trim().toUpperCase() + "\n";
                                    } else {
                                        v += ", " + list.get(i).OptionValue.get(x).trim().toUpperCase() + "\n";
                                    }
                                }
                            }
                        } else {
                            if (list.get(i).OptionValue != null && list.get(i).OptionValue.size() > 0) {
                                v += list.get(i).getDisplayText().trim() + ":";
                                for (int x = 0; x <= list.get(i).OptionValue.size() - 1; x++) {
                                    if (x == 0) {
                                        v += " " + list.get(i).OptionValue.get(x).trim().toUpperCase() + "\n";
                                    } else {
                                        v += ", " + list.get(i).OptionValue.get(x).trim().toUpperCase() + "\n";
                                    }
                                }
                            }
                        }
                    } else {

                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter getChecklistValueForFail. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return v;
        }
    }

    private void promptFailpage(final ChildViewHolder childViewHolder, final checklist_PostData data, final int currPos) {
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        int catType = objGlobalMethods.getDashboard2ActivityCategoryType(gcmp);
        Context ctx = myApp.getAppContext();
        String msg = catType == 0 ? ctx.getString(R.string.alert_cfmupdatestatusFailPU) : ctx.getString(R.string.alert_cfmupdatestatusFailOKP);
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setCancelable(false);
            builder.setMessage(msg);

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Context ctx = myApp.getAppContext();
                    SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                    String checklistvalue = getChecklistValueForFail(currPos);
                    Intent parentIntent;

                    /*String[] prefName = new String[]{"updatemodeForFailure", "ReloadDueNFCscan", "checklistvalue", "PreDeterminedStatusCodeIDX"};
                    String[] prefvalue = new String[]{"-1", "", checklistvalue, "80"}; //PreDeterminedStatusCodeIDX 80 is Rejected Delivery*/
                    String[] prefName = new String[]{"updatemodeForFailure", "ReloadDueNFCscan", "PreDeterminedStatusCodeIDX"};
                    String[] prefvalue = new String[]{"-1", "", "80"}; //PreDeterminedStatusCodeIDX 80 is Rejected Delivery
                    objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
                    objGlobalMethods.setChecklistRelatedSP_KeyValue("checklistvalue", checklistvalue);

                    parentIntent = new Intent(myApp.currentActivity, setFailStatusActivity.class);
                    JobInfoActivity.accessFromDirection = 0;
                    setFailStatusActivity.accessFromDirection = 1;
                    setFailStatusActivity.fromPage = "checklist";
                    MainActivity.uponLogin = 0;
                    //ctx.startActivity(parentIntent);
                    myApp.currentActivity.startActivity(parentIntent);
                }
            });

            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (data != null && childViewHolder != null) {
                        if (data.FinalManOptCode.equals("OP")) {
                            if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                data.OptionValue = null;
                            } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                data.OptionValue = null;
                            } else if (data.OptionValue == null) {
                                data.OptionValue = null;
                            }
                        } else {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add("NO");
                        }

                        childViewHolder.yesno.setText("NO");
                        notifyDataSetChanged();
                    }
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter promptFailpage. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private CompoundButton.OnCheckedChangeListener yesnoOnCheckedChangeListenerSub2(final int position, final checklist_PostData data, final ChildViewHolder childViewHolder) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        if (data.OptionValue2 != null && data.OptionValue2.size() > 0) {
                            data.OptionValue2.clear();
                            data.OptionValue2.add("YES");
                        } else if (data.OptionValue2 != null && data.OptionValue2.size() == 0) {
                            data.OptionValue2.clear();
                            data.OptionValue2.add("YES");
                        } else if (data.OptionValue2 == null) {
                            data.OptionValue2 = new ArrayList<>();
                            data.OptionValue2.add("YES");
                        }

                        if (data.OptionValue != null && data.OptionValue.size() > 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add("");
                        } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add("");
                        } else if (data.OptionValue == null) {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add("");
                        }
                        Context ctx = myApp.getAppContext();
                        childViewHolder.txtVerification.setHint(ctx.getString(R.string.lblnotverified));
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerification);

                        childViewHolder.yesnoIDVerificationSkip.setText("YES");
                        Boolean haschild = hasChild(position);
                        if (haschild) {
                            //showHierarchyNew2(position, childViewHolder, data);
                            hideHierarchyOnly(position, childViewHolder);
                        }
                    } else {
                        data.OptionValue2 = new ArrayList<>();
                        data.OptionValue2.add("NO");

                        data.setOptionValue(null);
                        childViewHolder.txtVerification.setHint(ctx.getString(R.string.cl_unitnum));
                        ctrlSettings.enableDisableSubmitButtonWithStyles(true, childViewHolder.btnVerification);

                        childViewHolder.yesnoIDVerificationSkip.setText("NO");
                        Boolean haschild = hasChild(position);
                        if (haschild) {
                            //hideHierarchyNew2(position, childViewHolder);
                            hideHierarchyOnly(position, childViewHolder);
                        }
                    }
                    notifyDataSetChanged();
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter yesnoOnCheckedChangeListenerSub. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener yesnoOnCheckedChangeListenerSub(final int position, final checklist_PostData data, final ChildViewHolder childViewHolder) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        if (data.OptionValue2 != null && data.OptionValue2.size() > 0) {
                            data.OptionValue2.clear();
                            data.OptionValue2.add("YES");
                        } else if (data.OptionValue2 != null && data.OptionValue2.size() == 0) {
                            data.OptionValue2.clear();
                            data.OptionValue2.add("YES");
                        } else if (data.OptionValue2 == null) {
                            data.OptionValue2 = new ArrayList<>();
                            data.OptionValue2.add("YES");
                        }

                        if (data.OptionValue != null && data.OptionValue.size() > 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add("");
                        } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add("");
                        } else if (data.OptionValue == null) {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add("");
                        }
                        Context ctx = myApp.getAppContext();
                        childViewHolder.txtVerification.setHint(ctx.getString(R.string.lblnotverified));
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerification);

                        childViewHolder.yesnoIDVerificationSkip.setText("YES");
                        Boolean haschild = hasChild(position);
                        if (haschild) {
                            //showHierarchyNew2(position, childViewHolder, data);
                            hideHierarchyOnly(position, childViewHolder);
                        }
                    } else {
                        data.OptionValue2 = new ArrayList<>();
                        data.OptionValue2.add("NO");

                        data.setOptionValue(null);
                        childViewHolder.txtVerification.setHint(ctx.getString(R.string.cl_contactnum));
                        ctrlSettings.enableDisableSubmitButtonWithStyles(true, childViewHolder.btnVerification);

                        childViewHolder.yesnoIDVerificationSkip.setText("NO");
                        Boolean haschild = hasChild(position);
                        if (haschild) {
                            //hideHierarchyNew2(position, childViewHolder);
                            hideHierarchyOnly(position, childViewHolder);
                        }
                    }
                    notifyDataSetChanged();
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter yesnoOnCheckedChangeListenerSub. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener yesnoOnCheckedChangeListener(final int position, final checklist_PostData data, final ChildViewHolder childViewHolder) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        if (data.OptionValue != null && data.OptionValue.size() > 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add("YES");
                        } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add("YES");
                        } else if (data.OptionValue == null) {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add("YES");
                        }

                        childViewHolder.yesno.setText("YES");
                        Boolean haschild = hasChild(position);
                        if (haschild) {
                            /*selectedShowParentPos = position;
                            selectedHideParentPos = -1;
                            showHierarchyNew2(position, childViewHolder, data);*/
                            hideHierarchyOnly(position, childViewHolder);
                        }

                       /* ////added on 6/8/2024 for multi parent child Hierarchy
                        int hasChildInYNRDf = hasChildInYNRD(position);
                        if ((hasChildInYNRDf & 2) == 2) {
                            showHierarchyNew(position, childViewHolder, data);
                        }
                        ////added on 6/8/2024 for multi parent child Hierarchy*/
                    } else {
                        if (data.FinalManOptCode.equals("OP")) {
                            if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                data.OptionValue = null;
                            } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                data.OptionValue = null;
                            } else if (data.OptionValue == null) {
                                data.OptionValue = null;
                            }
                        } else {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add("NO");
                        }

                        childViewHolder.yesno.setText("NO");
                        Boolean haschild = hasChild(position);
                        if (haschild) {
                            /*selectedShowParentPos = -1;
                            selectedHideParentPos = position;
                            hideHierarchyNew2(position, childViewHolder);*/
                            hideHierarchyOnly(position, childViewHolder);
                        }
                    }
                    notifyDataSetChanged();
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter yesnoOnCheckedChangeListener. Exception: " + e.getMessage(), "", "", "", "");
                }

            }
        };
    }

    private View.OnClickListener rtnOtpOnClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Context ctx = myApp.getAppContext();
                    Intent i = new Intent(ctx, OtpActivity.class);
                    OtpActivity.nextActivtyIntent = new Intent(myApp.getAppContext(), CheckListActivity.class);
                    OtpActivity.fromPage = "CheckListActivity";
                    myApp.currentActivity.startActivity(i);
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnOtpOnClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

    private View.OnClickListener rtnSignatureOnClickListener(final int position, final ArrayList<CustomGallery> dataT) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CheckListActivity.class) {
                        objGlobalMethods.writeToFile("rtnSignatureOnClickListener click", "", "", "", "");

                        final CheckListActivity act = (CheckListActivity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                act.startSignatureNameActivity(position, dataT);
                            }
                        });

                    }
                    /*SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.writeToFile("rtnSignatureOnClickListener click", "", "", "", "");
                    Context ctx = myApp.getAppContext();
                    Intent i = new Intent(ctx, SignatureNameActivity.class);
                    String[] prefName = new String[]{"reqSign", "reqName"};
                    String[] prefvalue = new String[]{"y", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    CheckListActivity.itemIndex = position;
                    CheckListActivity.signatureString = !(dataT == null || (dataT != null && dataT.size() == 0)) ? dataT.get(0).ImgBitmapStr : "";
                    SignatureNameActivity.rtnPageName = "CheckListActivity";
                    myApp.currentActivity.startActivity(i);*/
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnSignatureOnClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

    private View.OnClickListener rtnTakePhotoOnClickListener(final int position, final boolean itemproof) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Context ctx = myApp.getAppContext();
                    CheckListActivity.openCamera = true;
                    CheckListActivity.itemIndex = position;
                    Intent i = new Intent(ctx, Camera5Activity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    objGlobalMethods.setGCMPrefs(objGlobalMethods.getGcmPreferences(), "isItemProof", itemproof ? "t" : "");
                    //objGlobalMethods.writeToFile("checklist_PostItemAdapter btnCameraClick. ", "", "", "", "");
                    //ctx.startActivity(i);
                    myApp.currentActivity.startActivity(i);
                } catch (Exception e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnTakePhotoOnClickListener. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnTakePhotoOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                }
            }
        };
    }

    private View.OnClickListener rtnViewPhotoOnClickListener(final int position, final boolean isPR) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Context ctx = myApp.getAppContext();
                    CheckListActivity.openCamera = true;
                    CheckListActivity.itemIndex = position;
                    Intent i = new Intent(ctx, CustomGalleryActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(gcmp, "isItemProof", isPR ? "t" : "");
                    //ctx.startActivity(i);
                    myApp.currentActivity.startActivity(i);
                } catch (Exception e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnViewPhotoOnClickListener. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnViewPhotoOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                }
            }
        };
    }

    private View.OnClickListener rtnbtnScanTagOnClickListener(final int position, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    Intent parentIntent;
                    String[] prefName = new String[]{"QRScanRequestIntent", "cameraScannedCode"};
                    String[] prefvalue = new String[]{"chklist", ""};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    CheckListActivity.itemIndex = position;
                    parentIntent = new Intent(myApp.currentActivity, QRBarcodeReader.class);
                    JobInfoActivity.accessFromDirection = 0;
                    myApp.currentActivity.startActivity(parentIntent);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnbtnScanTagOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private View.OnClickListener rtnbtnUpdatePiecesOnClickListenerNew(final int position, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String val = childViewHolder.txtLabelTagVal != null ? childViewHolder.txtLabelTagVal.getText().toString().toUpperCase() : "";
                    if (!objGlobalMethods.IsEmptyString(val)) {
                        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                        long jidx = 0;
                        String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                        if (!JobsIDXstr.equals("")) {
                            try {
                                jidx = Long.parseLong(JobsIDXstr);
                            } catch (Exception e) {
                                objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter rtnbtnUpdatePiecesOnClickListenerNew convert JobsIDXstr to Long value.", "", "", "", "");
                                jidx = 0;
                            }
                        }
                        final long JobsIDX = jidx;
                        if (JobsIDX > 0) {
                            objGlobalMethods.writeToFile("Attempt to update PIECES value in checklist. JobsIDX: " + String.valueOf(JobsIDX) + ", PIECES value: " + val.toUpperCase(), "", "", "", "");

                            String fulldladdress = "";
                            String dladdress = objGlobalMethods.getGCMPrefs(GcmPreferences, "dladdress");
                            if (!objGlobalMethods.IsEmptyString(dladdress))
                                fulldladdress = "\n\n" + myApp.getAppContext().getString(R.string.tobedeliveredto) + "\n\n" + dladdress.toUpperCase();

                            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                            builder.setCancelable(false);
                            builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdateJobPieces) + fulldladdress);
                            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if user pressed "yes", update job status
                                    updateChecklistWithNumberOfPieces(position, val, JobsIDX, childViewHolder, data);
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
                        } else
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_ErrorUpdatePiecesJobsIDX)).show();
                    } else
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_plsenterpieces)).show();
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnbtnUpdatePiecesOnClickListenerNew. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private void updateChecklistWithNumberOfPieces(final int position, String val, long JobsIDX, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        boolean succ = false;
        try {
            objGlobalMethods.writeToFile("updateChecklistWithNumberOfPieces Update PIECES value in checklist. JobsIDX: " + String.valueOf(JobsIDX) + ", PIECES value: " + val.toUpperCase() + ".", "", "", "", "");
            if (list != null && list.size() > 0 && position <= list.size() - 1) {
                list.get(position).OptionValue = new ArrayList<String>() {{
                    add(val);
                }};
                list.get(position).OptionValue2 = new ArrayList<String>() {{
                    add(val);
                }};
                succ = true;
            } else {
                list.get(position).OptionValue = null;
                list.get(position).OptionValue2 = null;
            }

            if (succ) {
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_Piecesdatasubmitted)).show();
                EnableDisableCtrlsNew(false, childViewHolder, data);
            } else {
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_ErrorUpdatePieces)).show();
                EnableDisableCtrlsNew(true, childViewHolder, data);
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter updateChecklistWithNumberOfPieces. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_ErrorUpdatePieces)).show();
        }
    }

    private View.OnClickListener rtnbtnUpdateLabelTagOnClickListenerNew(final int position, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String val = childViewHolder.txtLabelTagVal != null ? childViewHolder.txtLabelTagVal.getText().toString().toUpperCase() : "";
                    if (!objGlobalMethods.IsEmptyString(val)) {
                        if (objGlobalMethods.verifyBarcode(val)) {
                            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                            long jidx = 0;
                            String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                            if (!JobsIDXstr.equals("")) {
                                try {
                                    jidx = Long.parseLong(JobsIDXstr);
                                } catch (Exception e) {
                                    objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter rtnbtnUpdateLabelTagOnClickListener convert JobsIDXstr to Long value.", "", "", "", "");
                                    jidx = 0;
                                }
                            }
                            final long JobsIDX = jidx;
                            if (JobsIDX > 0) {
                                objGlobalMethods.writeToFile("Attempt to update BarcodeTag value in checklist. JobsIDX: " + String.valueOf(JobsIDX) + ", BarcodeTag value: " + val.toUpperCase(), "", "", "", "");

                                String fulldladdress = "";
                                String dladdress = objGlobalMethods.getGCMPrefs(GcmPreferences, "dladdress");
                                if (!objGlobalMethods.IsEmptyString(dladdress))
                                    fulldladdress = "\n\n" + myApp.getAppContext().getString(R.string.tobedeliveredto) + "\n\n" + dladdress.toUpperCase();

                                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                builder.setCancelable(false);
                                builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdateBarcodeLabel) + fulldladdress);
                                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //if user pressed "yes", update job status
                                        updateChecklistWithArticleTag(position, val, JobsIDX, childViewHolder, data);
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


                            } else
                                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_ErrorJobsIDXupdate)).show();
                        } else
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_InvalidBarcode)).show();
                    } else
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_plsenterBarcode)).show();
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnbtnUpdateLabelTagOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private View.OnClickListener rtnbtnUpdateLabelTagOnClickListener(final int position, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String val = childViewHolder.txtLabelTagVal != null ? childViewHolder.txtLabelTagVal.getText().toString().toUpperCase() : "";
                    if (!objGlobalMethods.IsEmptyString(val)) {
                        if (objGlobalMethods.verifyBarcode(val)) {
                            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                            long jidx = 0;
                            String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                            if (!JobsIDXstr.equals("")) {
                                try {
                                    jidx = Long.parseLong(JobsIDXstr);
                                } catch (Exception e) {
                                    objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter rtnbtnUpdateLabelTagOnClickListener convert JobsIDXstr to Long value.", "", "", "", "");
                                    jidx = 0;
                                }
                            }
                            final long JobsIDX = jidx;
                            if (JobsIDX > 0) {
                                objGlobalMethods.writeToFile("Attempt to update BarcodeTag value in checklist. JobsIDX: " + String.valueOf(JobsIDX) + ", BarcodeTag value: " + val.toUpperCase(), "", "", "", "");

                                String fulldladdress = "";
                                String dladdress = objGlobalMethods.getGCMPrefs(GcmPreferences, "dladdress");
                                if (!objGlobalMethods.IsEmptyString(dladdress))
                                    fulldladdress = "\n\n" + myApp.getAppContext().getString(R.string.tobedeliveredto) + "\n\n" + dladdress.toUpperCase();

                                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                                builder.setCancelable(false);
                                builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmupdateBarcodeLabel) + fulldladdress);
                                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //if user pressed "yes", update job status
                                        updateBarcodeVal(position, val, JobsIDX, childViewHolder, data);
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


                            } else
                                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_ErrorJobsIDX)).show();
                        } else
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_InvalidBarcode)).show();
                    } else
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_plsenterBarcode)).show();
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnbtnUpdateLabelTagOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private void updateChecklistWithArticleTag(final int position, String val, long JobsIDX, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        boolean succ = false;
        try {
            objGlobalMethods.writeToFile("updateChecklistWithArticleTag Update BarcodeTag value in checklist. JobsIDX: " + String.valueOf(JobsIDX) + ", BarcodeTag value: " + val.toUpperCase() + ".", "", "", "", "");
            if (list != null && list.size() > 0 && position <= list.size() - 1) {
                list.get(position).OptionValue = new ArrayList<String>() {{
                    add(val);
                }};
                list.get(position).OptionValue2 = new ArrayList<String>() {{
                    add(val);
                }};
                succ = true;
            } else {
                list.get(position).OptionValue = null;
                list.get(position).OptionValue2 = null;
            }

            if (succ) {
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_Taggingdataupdated)).show();
                EnableDisableCtrlsNew(false, childViewHolder, data);
            } else {
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_ErrorBarcodeUpdate)).show();
                EnableDisableCtrlsNew(true, childViewHolder, data);
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter updateChecklistWithArticleTag. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_ErrorBarcodeUpdate)).show();
        }
    }

    private void updateBarcodeVal(final int position, String val, long JobsIDX, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        try {
            objGlobalMethods.writeToFile("Update BarcodeTag value in checklist. JobsIDX: " + String.valueOf(JobsIDX) + ", BarcodeTag value: " + val.toUpperCase() + ".", "", "", "", "");
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();

            objJobInfo currJobInfo = new Gson().fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);

            Boolean succ = JobsHandler.processBarcodeTagUpdate(currJobInfo, val);

            if (succ) {
                Context ctx = myApp.getAppContext();
                ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            }

            if (succ)
                objGlobalMethods.writeToFile("checklist_PostItemAdapter updateBarcodeVal ended successfully.", "", "", "", "");
            else
                objGlobalMethods.writeToFile("checklist_PostItemAdapter updateBarcodeVal ended with error.", "", "", "", "");

            if (succ)
                EnableDisableCtrlsNew(false, childViewHolder, data);
            else
                EnableDisableCtrlsNew(true, childViewHolder, data);

            if (succ && list != null && list.size() > 0 && position <= list.size() - 1) {
                list.get(position).OptionValue = new ArrayList<String>() {{
                    add(val);
                }};
                list.get(position).OptionValue2 = new ArrayList<String>() {{
                    add(val);
                }};
            } else if (!succ) {
                list.get(position).OptionValue = null;
                list.get(position).OptionValue2 = null;
            }

            if (succ)
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_Taggingdatasubmitted)).show();
            else
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_ErrorBarcode)).show();
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter updateBarcodeVal. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private View.OnClickListener rtnBtnMultiVerificationOnClickListener(final int position, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String IDVerificationName = childViewHolder.lblIDVerificationVal.getText().toString().toLowerCase();
                    String IDVerificationNameLast4Char = "";
                    String[] split = null;
                    List<String> al = null;
                    String id = "";
                    String idlast4 = "";

                    if (!objGlobalMethods.IsEmptyString(IDVerificationName) && IDVerificationName.contains("|")) {
                        split = IDVerificationName.split("\\|");

                        if (split.length > 0) {
                            for (int i = 0; i <= split.length - 1; i++) {
                                id = split[i];
                                idlast4 = id.length() > 4 ? id.substring(id.length() - 4, id.length()) : id;
                                split[i] = idlast4.toUpperCase();
                            }
                        }
                        al = Arrays.asList(split);
                    } else if (!objGlobalMethods.IsEmptyString(IDVerificationName) && !IDVerificationName.contains("|")) {
                        IDVerificationNameLast4Char = IDVerificationName.length() > 4 ?
                                IDVerificationName.substring(IDVerificationName.length() - 4, IDVerificationName.length()) :
                                IDVerificationName;
                        al = new ArrayList<>();
                        al.add(IDVerificationNameLast4Char.toUpperCase());
                    }

                    if (al == null) {
                        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                        objGlobalMethods.writeToFile("IDVerification empty for jobsidx: " + JobsIDXstr, "", "", "", "");
                        String[] UsernamePwd = new String[4];
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                        String useridx = UsernamePwd[2];
                        Mail m = new Mail("luke@xdel.com", "XDel2015");
                        objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", "Empty IDVerification for JobsIDX: " + JobsIDXstr + ", DriverIDX: " + useridx
                                , "Empty IDVerification.", null);
                        sendmail.execute();
                    }

                    String enteredIDVerificationName = childViewHolder.txtVerification.getText().toString().toLowerCase();
                    String enteredIDVerificationNameLast4Char = "";
                    if (!objGlobalMethods.IsEmptyString(enteredIDVerificationName)) {
                        enteredIDVerificationNameLast4Char = enteredIDVerificationName.length() > 4 ?
                                enteredIDVerificationName.substring(enteredIDVerificationName.length() - 4, enteredIDVerificationName.length()) :
                                enteredIDVerificationName;
                    } else {
                        enteredIDVerificationNameLast4Char = enteredIDVerificationName;
                    }

                    Context ctx = myApp.getAppContext();
                    if (!objGlobalMethods.IsEmptyString(enteredIDVerificationNameLast4Char)) {
                        if (al != null && al.size() > 0) {
                            if (al.contains(enteredIDVerificationNameLast4Char.toUpperCase())) {
                                objGlobalMethods.writeToFile("NRIC verified.", "", "", "", "");
                                childViewHolder.imgVerification.setImageResource(R.mipmap.ic_single_tick);
                                childViewHolder.btnVerification.setText(ctx.getString(R.string.otpbuttontext_verified));
                                ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerification);
                                EnableDisableCtrlsNew(false, childViewHolder, data);
                                if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                    data.OptionValue.clear();
                                    data.OptionValue.add(enteredIDVerificationNameLast4Char.toUpperCase());
                                } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                    data.OptionValue.clear();
                                    data.OptionValue.add(enteredIDVerificationNameLast4Char.toUpperCase());
                                } else if (data.OptionValue == null) {
                                    data.OptionValue = new ArrayList<>();
                                    data.OptionValue.add(enteredIDVerificationNameLast4Char.toUpperCase());
                                }
                                data.setAltColor(false);
                                notifyDataSetChanged();
                            } else {
                                String systemNRICs = "";
                                for (int i = 0; i <= al.size() - 1; i++) {
                                    systemNRICs += (i == 0) ? al.get(i) : "," + al.get(i);
                                }
                                //objGlobalMethods.writeToFile("NRIC not verified. Entered: " + enteredIDVerificationNameLast4Char + ", System NRIC: " + IDVerificationNameLast4Char, "", "", "", "");
                                objGlobalMethods.writeToFile("NRIC not verified. Entered: " + enteredIDVerificationNameLast4Char + ", System NRIC: " + systemNRICs, "", "", "", "");
                                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_invalidnricfin)).show();
                            }
                        } else {
                            objGlobalMethods.writeToFile("NRIC not verified. Entered: " + enteredIDVerificationNameLast4Char + ", System NRIC empty", "", "", "", "");
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_sysnovalidnricfin)).show();
                        }
                    } else {
                        objGlobalMethods.writeToFile("NRIC not entered for verification.", "", "", "", "");
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_nricfin)).show();
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnBtnMultiVerificationOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private View.OnClickListener rtnBtnMultiV2VerificationOnClickListener(final int position, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String IDVerificationName = childViewHolder.lblIMEIVerificationVal.getText().toString().toLowerCase();
                    String IDVerificationNameLast4Char = "";
                    String[] split = null;
                    List<String> al = null;
                    String id = "";
                    String idlast4 = "";

                    if (!objGlobalMethods.IsEmptyString(IDVerificationName) && IDVerificationName.contains("|")) {
                        split = IDVerificationName.split("\\|");

                        if (split.length > 0) {
                            for (int i = 0; i <= split.length - 1; i++) {
                                id = split[i];
                                split[i] = id.toUpperCase();
                            }
                        }
                        al = Arrays.asList(split);
                    } else if (!objGlobalMethods.IsEmptyString(IDVerificationName) && !IDVerificationName.contains("|")) {
                        al = new ArrayList<>();
                        al.add(IDVerificationName.toUpperCase());
                    }

                    if (al == null) {
                        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                        objGlobalMethods.writeToFile("IMEI Verification empty for jobsidx: " + JobsIDXstr, "", "", "", "");
                        String[] UsernamePwd = new String[4];
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                        String useridx = UsernamePwd[2];
                        Mail m = new Mail("luke@xdel.com", "XDel2015");
                        objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", "Empty IMEI Verification for JobsIDX: " + JobsIDXstr + ", DriverIDX: " + useridx
                                , "Empty IMEI Verification.", null);
                        sendmail.execute();
                    }

                    String enteredIDVerificationName = childViewHolder.txtIMEIVerification.getText().toString().toLowerCase();


                    Context ctx = myApp.getAppContext();
                    if (!objGlobalMethods.IsEmptyString(enteredIDVerificationName)) {
                        if (al != null && al.size() > 0) {
                            if (al.contains(enteredIDVerificationName.toUpperCase())) {
                                objGlobalMethods.writeToFile("IMEI verified.", "", "", "", "");
                                childViewHolder.imgVerification.setImageResource(R.mipmap.ic_single_tick);
                                childViewHolder.btnVerification.setText(ctx.getString(R.string.otpbuttontext_verified));
                                ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerification);
                                EnableDisableCtrlsNew(false, childViewHolder, data);
                                if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                    data.OptionValue.clear();
                                    data.OptionValue.add(enteredIDVerificationName.toUpperCase());
                                } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                    data.OptionValue.clear();
                                    data.OptionValue.add(enteredIDVerificationName.toUpperCase());
                                } else if (data.OptionValue == null) {
                                    data.OptionValue = new ArrayList<>();
                                    data.OptionValue.add(enteredIDVerificationName.toUpperCase());
                                }
                                data.setAltColor(false);
                                notifyDataSetChanged();
                            } else {
                                String systemIMEIs = "";
                                for (int i = 0; i <= al.size() - 1; i++) {
                                    systemIMEIs += (i == 0) ? al.get(i) : "," + al.get(i);
                                }
                                objGlobalMethods.writeToFile("IMEI not verified. Entered: " + enteredIDVerificationName + ", System IMEI: " + systemIMEIs, "", "", "", "");
                                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_invalidimei)).show();
                            }
                        } else {
                            objGlobalMethods.writeToFile("IMEI not verified. Entered: " + enteredIDVerificationName + ", System IMEI empty", "", "", "", "");
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_sysnovalidimei)).show();
                        }
                    } else {
                        objGlobalMethods.writeToFile("IMEI not entered for verification.", "", "", "", "");
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_imei)).show();
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnBtnMultiV2VerificationOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private View.OnClickListener rtnBtnMultiV3VerificationOnClickListener(final int position, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String IDVerificationName = childViewHolder.lblIMEIVerificationVal.getText().toString().toLowerCase();
                    String IDVerificationNameLast4Char = "";
                    String[] split = null;
                    List<String> al = null;
                    String id = "";
                    String idlast4 = "";

                    if (!objGlobalMethods.IsEmptyString(IDVerificationName) && IDVerificationName.contains("|")) {
                        split = IDVerificationName.split("\\|");

                        if (split.length > 0) {
                            for (int i = 0; i <= split.length - 1; i++) {
                                id = split[i];
                                split[i] = id.toUpperCase();
                            }
                        }
                        al = Arrays.asList(split);
                    } else if (!objGlobalMethods.IsEmptyString(IDVerificationName) && !IDVerificationName.contains("|")) {
                        al = new ArrayList<>();
                        al.add(IDVerificationName.toUpperCase());
                    }

                    if (al == null) {
                        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                        objGlobalMethods.writeToFile("Serial Number Verification empty for jobsidx: " + JobsIDXstr, "", "", "", "");
                        String[] UsernamePwd = new String[4];
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                        String useridx = UsernamePwd[2];
                        Mail m = new Mail("luke@xdel.com", "XDel2015");
                        objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", "Empty Serial Number Verification for JobsIDX: " + JobsIDXstr + ", DriverIDX: " + useridx
                                , "Empty Serial Number Verification.", null);
                        sendmail.execute();
                    }

                    String enteredIDVerificationName = childViewHolder.txtIMEIVerification.getText().toString().toLowerCase();


                    Context ctx = myApp.getAppContext();
                    if (!objGlobalMethods.IsEmptyString(enteredIDVerificationName)) {
                        if (al != null && al.size() > 0) {
                            if (al.contains(enteredIDVerificationName.toUpperCase())) {
                                objGlobalMethods.writeToFile("Serial Number verified.", "", "", "", "");
                                childViewHolder.imgVerification.setImageResource(R.mipmap.ic_single_tick);
                                childViewHolder.btnVerification.setText(ctx.getString(R.string.otpbuttontext_verified));
                                ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerification);
                                EnableDisableCtrlsNew(false, childViewHolder, data);
                                if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                    data.OptionValue.clear();
                                    data.OptionValue.add(enteredIDVerificationName.toUpperCase());
                                } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                    data.OptionValue.clear();
                                    data.OptionValue.add(enteredIDVerificationName.toUpperCase());
                                } else if (data.OptionValue == null) {
                                    data.OptionValue = new ArrayList<>();
                                    data.OptionValue.add(enteredIDVerificationName.toUpperCase());
                                }
                                data.setAltColor(false);
                                notifyDataSetChanged();
                            } else {
                                String systemIMEIs = "";
                                for (int i = 0; i <= al.size() - 1; i++) {
                                    systemIMEIs += (i == 0) ? al.get(i) : "," + al.get(i);
                                }
                                objGlobalMethods.writeToFile("Serial Number not verified. Entered: " + enteredIDVerificationName + ", System Serial Number: " + systemIMEIs, "", "", "", "");
                                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_invalidsn)).show();
                            }
                        } else {
                            objGlobalMethods.writeToFile("Serial Number not verified. Entered: " + enteredIDVerificationName + ", System Serial Number empty", "", "", "", "");
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_sysnovalidsn)).show();
                        }
                    } else {
                        objGlobalMethods.writeToFile("Serial Number not entered for verification.", "", "", "", "");
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_sn)).show();
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnBtnMultiV3VerificationOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private View.OnClickListener rtnBtnMultiV4VerificationOnClickListener(final int position, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String IDVerificationName = childViewHolder.lblIMEIVerificationVal.getText().toString().toLowerCase();
                    String IDVerificationNameLast4Char = "";
                    String[] split = null;
                    List<String> al = null;
                    String id = "";
                    String idlast4 = "";

                    if (!objGlobalMethods.IsEmptyString(IDVerificationName) && IDVerificationName.contains("|")) {
                        split = IDVerificationName.split("\\|");

                        if (split.length > 0) {
                            for (int i = 0; i <= split.length - 1; i++) {
                                id = split[i];
                                split[i] = id.toUpperCase();
                            }
                        }
                        al = Arrays.asList(split);
                    } else if (!objGlobalMethods.IsEmptyString(IDVerificationName) && !IDVerificationName.contains("|")) {
                        al = new ArrayList<>();
                        al.add(IDVerificationName.toUpperCase());
                    }

                    if (al == null) {
                        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                        objGlobalMethods.writeToFile("Voucher Code Verification empty for jobsidx: " + JobsIDXstr, "", "", "", "");
                        String[] UsernamePwd = new String[4];
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                        String useridx = UsernamePwd[2];
                        Mail m = new Mail("luke@xdel.com", "XDel2015");
                        objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", "Empty Voucher Code Verification for JobsIDX: " + JobsIDXstr + ", DriverIDX: " + useridx
                                , "Empty Voucher Code Verification.", null);
                        sendmail.execute();
                    }

                    String enteredIDVerificationName = childViewHolder.txtIMEIVerification.getText().toString().toLowerCase();


                    Context ctx = myApp.getAppContext();
                    if (!objGlobalMethods.IsEmptyString(enteredIDVerificationName)) {
                        if (al != null && al.size() > 0) {
                            if (al.contains(enteredIDVerificationName.toUpperCase())) {
                                objGlobalMethods.writeToFile("Voucher Code verified.", "", "", "", "");
                                childViewHolder.imgVerification.setImageResource(R.mipmap.ic_single_tick);
                                childViewHolder.btnVerification.setText(ctx.getString(R.string.otpbuttontext_verified));
                                ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerification);
                                EnableDisableCtrlsNew(false, childViewHolder, data);
                                if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                    data.OptionValue.clear();
                                    data.OptionValue.add(enteredIDVerificationName.toUpperCase());
                                } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                    data.OptionValue.clear();
                                    data.OptionValue.add(enteredIDVerificationName.toUpperCase());
                                } else if (data.OptionValue == null) {
                                    data.OptionValue = new ArrayList<>();
                                    data.OptionValue.add(enteredIDVerificationName.toUpperCase());
                                }
                                data.setAltColor(false);
                                notifyDataSetChanged();
                            } else {
                                String systemIMEIs = "";
                                for (int i = 0; i <= al.size() - 1; i++) {
                                    systemIMEIs += (i == 0) ? al.get(i) : "," + al.get(i);
                                }
                                objGlobalMethods.writeToFile("Voucher Number not verified. Entered: " + enteredIDVerificationName + ", System Voucher Number: " + systemIMEIs, "", "", "", "");
                                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_invalidvc)).show();
                            }
                        } else {
                            objGlobalMethods.writeToFile("Voucher Number not verified. Entered: " + enteredIDVerificationName + ", System Voucher Number empty", "", "", "", "");
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_sysnovalidvc)).show();
                        }
                    } else {
                        objGlobalMethods.writeToFile("Voucher Number not entered for verification.", "", "", "", "");
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_vc)).show();
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnBtnMultiV4VerificationOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private View.OnClickListener rtnBtnVerificationOnClickListener(final int position, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String IDVerificationName = childViewHolder.lblIDVerificationVal.getText().toString().toLowerCase();
                    String IDVerificationNameLast4Char = "";
                    if (IDVerificationName != null && !IDVerificationName.isEmpty() && !IDVerificationName.equals("") && IDVerificationName != "") {
                        IDVerificationNameLast4Char = IDVerificationName.length() > 4 ?
                                IDVerificationName.substring(IDVerificationName.length() - 4, IDVerificationName.length()) :
                                IDVerificationName;
                    } else {
                        IDVerificationNameLast4Char = IDVerificationName;
                    }

                    if (IDVerificationNameLast4Char.isEmpty() || IDVerificationNameLast4Char.equals("")) {
                        //send error email jobsidx has empty customerID
                        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                        objGlobalMethods.writeToFile("IDVerification empty for jobsidx: " + JobsIDXstr, "", "", "", "");
                        String[] UsernamePwd = new String[4];
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                        String useridx = UsernamePwd[2];
                        Mail m = new Mail("luke@xdel.com", "XDel2015");
                        objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", "Empty IDVerification for JobsIDX: " + JobsIDXstr + ", DriverIDX: " + useridx
                                , "Empty IDVerification.", null);
                        sendmail.execute();
                    }

                    String enteredIDVerificationName = childViewHolder.txtVerification.getText().toString().toLowerCase();
                    String enteredIDVerificationNameLast4Char = "";
                    if (enteredIDVerificationName != null && !enteredIDVerificationName.isEmpty() && !enteredIDVerificationName.equals("") && enteredIDVerificationName != "") {
                        enteredIDVerificationNameLast4Char = enteredIDVerificationName.length() > 4 ?
                                enteredIDVerificationName.substring(enteredIDVerificationName.length() - 4, enteredIDVerificationName.length()) :
                                enteredIDVerificationName;
                    } else {
                        enteredIDVerificationNameLast4Char = enteredIDVerificationName;
                    }

                    Context ctx = myApp.getAppContext();
                    if (!enteredIDVerificationNameLast4Char.isEmpty() && !enteredIDVerificationNameLast4Char.equals("") && enteredIDVerificationNameLast4Char != "") {
                        if (enteredIDVerificationNameLast4Char.equals(IDVerificationNameLast4Char)) {
                            objGlobalMethods.writeToFile("NRIC verified.", "", "", "", "");
                            childViewHolder.imgVerification.setImageResource(R.mipmap.ic_single_tick);
                            childViewHolder.btnVerification.setText(ctx.getString(R.string.otpbuttontext_verified));
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerification);
                            EnableDisableCtrlsNew(false, childViewHolder, data);
                            if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                data.OptionValue.clear();
                                data.OptionValue.add(enteredIDVerificationNameLast4Char);
                            } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                data.OptionValue.clear();
                                data.OptionValue.add(enteredIDVerificationNameLast4Char);
                            } else if (data.OptionValue == null) {
                                data.OptionValue = new ArrayList<>();
                                data.OptionValue.add(enteredIDVerificationNameLast4Char);
                            }
                            data.setAltColor(false);
                            notifyDataSetChanged();
                        } else {
                            objGlobalMethods.writeToFile("NRIC not verified. Entered: " + enteredIDVerificationNameLast4Char + ", System NRIC: " + IDVerificationNameLast4Char, "", "", "", "");
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_invalidnricfin)).show();
                        }
                    } else {
                        objGlobalMethods.writeToFile("NRIC not entered for verification.", "", "", "", "");
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_nricfin)).show();
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnBtnVerificationOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private View.OnClickListener rtnV1BtnVerificationOnClickListener(final int position, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //verify last 4 char of tele/others/mobile number
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");

                    if (objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                        //send error email jobsidx has No valid contact numbers for verification
                        String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                        objGlobalMethods.writeToFile("No valid contact numbers for jobsidx: " + JobsIDXstr, "", "", "", "");
                        String[] UsernamePwd = new String[4];
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                        String useridx = UsernamePwd[2];
                        Mail m = new Mail("luke@xdel.com", "XDel2015");
                        objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", "No valid contact numbers for verification for JobsIDX: " + JobsIDXstr + ", DriverIDX: " + useridx
                                , "No valid contact numbers for verification.", null);
                        sendmail.execute();
                    }

                    String enteredNum = childViewHolder.txtVerification.getText().toString().toLowerCase();
                    if (enteredNum.length() < 4) {
                        objGlobalMethods.writeToFile("Contact Num entered for verification less than 4 digit. Entered: " + enteredNum, "", "", "", "");
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_contactnum)).show();
                    } else {
                        String enteredNumLast4Char = "";
                        if (!objGlobalMethods.IsEmptyString(enteredNum)) {
                            enteredNumLast4Char = enteredNum.length() > 4 ?
                                    enteredNum.substring(enteredNum.length() - 4, enteredNum.length()) :
                                    enteredNum;
                        } else {
                            enteredNumLast4Char = enteredNum;
                        }

                        Context ctx = myApp.getAppContext();
                        if (!objGlobalMethods.IsEmptyString(enteredNumLast4Char)) {
                            if (JobsHandler2.verifyContactNumsLast4(AllValidTelNums, enteredNumLast4Char)) {
                                objGlobalMethods.writeToFile("ContactNum last4 verified. " + enteredNumLast4Char, "", "", "", "");
                                childViewHolder.imgVerification.setImageResource(R.mipmap.ic_single_tick);
                                childViewHolder.btnVerification.setText(ctx.getString(R.string.otpbuttontext_verified));
                                ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerification);
                                EnableDisableCtrlsNew(false, childViewHolder, data);
                                if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                    data.OptionValue.clear();
                                    data.OptionValue.add(enteredNumLast4Char);
                                } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                    data.OptionValue.clear();
                                    data.OptionValue.add(enteredNumLast4Char);
                                } else if (data.OptionValue == null) {
                                    data.OptionValue = new ArrayList<>();
                                    data.OptionValue.add(enteredNumLast4Char);
                                }
                                notifyDataSetChanged();
                            } else {
                                objGlobalMethods.writeToFile("Contact Num not verified. Entered: " + enteredNumLast4Char + ", Avail Contact Num: " + AllValidTelNums, "", "", "", "");
                                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_invalidcontactnum)).show();
                            }
                        } else {
                            objGlobalMethods.writeToFile("Contact Num not entered for verification.", "", "", "", "");
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_contactnum)).show();
                        }
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnV1BtnVerificationOnClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private View.OnClickListener rtnV5BtnVerificationOnClickListener(final int position, final ChildViewHolder childViewHolder, final checklist_PostData data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //verify last 4 char of tele/others/mobile number
                    //verify unit number or blk
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    //String AllValidTelNums = objGlobalMethods.getGCMPrefs(GcmPreferences, "AllValidTelNums");
                    String DelBlock = data.DelBlock;
                    List<String> DelUnits = data.AllDelUnits;
                    List<String> DelUnitsCombined = data.AllDelUnitsCombined;
                    boolean senderremail = objGlobalMethods.IsEmptyString(DelBlock) && (DelUnits == null || (DelUnitsCombined != null && DelUnitsCombined.size() == 0));

                    if (senderremail) {
                        String JobsIDXstr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                        objGlobalMethods.writeToFile("No DelBlock, DelUnit_original for jobsidx: " + JobsIDXstr, "", "", "", "");
                        String[] UsernamePwd = new String[4];
                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                        String useridx = UsernamePwd[2];
                        Mail m = new Mail("luke@xdel.com", "XDel2015");
                        objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", "No DelBlock, DelUnit_original for verification for JobsIDX: " + JobsIDXstr + ", DriverIDX: " + useridx
                                , "No DelBlock, DelUnit_original for verification.", null);
                        sendmail.execute();
                    }

                    String val = childViewHolder.txtVerification.getText().toString().toLowerCase();
                    final String originalval = val;

                    if (objGlobalMethods.IsEmptyString(val)) {
                        objGlobalMethods.writeToFile("Unit Number not entered for verification.", "", "", "", "");
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_del_blk_unit)).show();
                    } else {
                        val = JobsHandler2.getDelUnit_combine(val);

                        if (JobsHandler2.verifyBlkUnitNumber(val, DelBlock, DelUnits, DelUnitsCombined)) {
                            objGlobalMethods.writeToFile("Block Unit number verified. " + val, "", "", "", "");
                            childViewHolder.imgVerification.setImageResource(R.mipmap.ic_single_tick);
                            childViewHolder.btnVerification.setText(ctx.getString(R.string.otpbuttontext_verified));
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerification);
                            EnableDisableCtrlsNew(false, childViewHolder, data);
                            if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                data.OptionValue.clear();
                                data.OptionValue.add(originalval);
                            } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                data.OptionValue.clear();
                                data.OptionValue.add(originalval);
                            } else if (data.OptionValue == null) {
                                data.OptionValue = new ArrayList<>();
                                data.OptionValue.add(originalval);
                            }
                            notifyDataSetChanged();
                        } else {
                            objGlobalMethods.writeToFile("Block Unit number not verified. Entered: " + originalval, "", "", "", "");
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_invalid_del_blk_unit)).show();

                        }
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnV1BtnVerificationOnClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    private View.OnClickListener rtnDateOnClickListener(final TextView lblDate, final checklist_PostData data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                try {
                                    lblDate.setText(String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear + 1) + "/" + year);

                                    if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                        data.OptionValue.remove(0);
                                        data.OptionValue.add(0, String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear + 1) + "/" + year);
                                    } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                        data.OptionValue.add(0, String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear + 1) + "/" + year);
                                    } else if (data.OptionValue == null) {
                                        data.OptionValue = new ArrayList<>();
                                        data.OptionValue.add(0, String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear + 1) + "/" + year);
                                    }
                                } catch (Exception e) {
                                    /*String stack = JobsHandler.getStackTraceString(e);
                                    objGlobalMethods.writeToFile("", "", "", "", "");*/
                                }

                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                dpd.show();
            }
        };
    }

    private SlideDateTimeListener rtnSlideDateTimeListener(final TextView lblTime, final checklist_PostData data, final ChildViewHolder childViewHolder) {
        return new SlideDateTimeListener() {
            @Override
            public void onDateTimeSet(Date date) {
                // Do something with the date. This Date object contains
                // the date and time that the user has selected.
                try {
                    lblTime.setText(DateTimeformatter.format(date));

                    data.SelectedDate = date;
                    if (data.OptionValue != null && data.OptionValue.size() > 0) {
                        data.OptionValue.clear();
                        data.OptionValue.add(DateTimeformatter.format(date));
                    } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                        data.OptionValue.clear();
                        data.OptionValue.add(DateTimeformatter.format(date));
                    } else if (data.OptionValue == null) {
                        data.OptionValue = new ArrayList<>();
                        data.OptionValue.add(DateTimeformatter.format(date));
                    }

                    /*data.setAltColor(false);
                    childViewHolder.LLTypeRow1.setBackgroundResource(R.drawable.edittextstylelightgray);*/
                    childViewHolder.lblDateTimeTimeOnClickListener = null;
                    childViewHolder.lblDateTimeTime.setOnClickListener(null);
                    childViewHolder.lblDateTimeTimeOnClickListener = CheckListActivity.rtnlblDateTimeTimeOnClickListener(childViewHolder.slideDateTimeListener, date);
                    childViewHolder.lblDateTimeTime.setOnClickListener(childViewHolder.lblDateTimeTimeOnClickListener);
                } catch (Exception e) {
                    /*String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("", "", "", "", "");*/
                }

            }

            @Override
            public void onDateTimeCancel() {
                // Overriding onDateTimeCancel() is optional.
            }
        };
    }

    private SlideDateListener rtnSlideDateListener(final TextView lblDate, final checklist_PostData data, final ChildViewHolder childViewHolder) {
        return new SlideDateListener() {

            @Override
            public void onDateSet(Date date) {
                // Do something with the date. This Date object contains
                // the date and time that the user has selected.
                try {
                    lblDate.setText(Dateformatter.format(date));

                    data.SelectedDate = date;
                    if (data.OptionValue != null && data.OptionValue.size() > 0) {
                        data.OptionValue.clear();
                        data.OptionValue.add(Dateformatter.format(date));
                    } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                        data.OptionValue.clear();
                        data.OptionValue.add(Dateformatter.format(date));
                    } else if (data.OptionValue == null) {
                        data.OptionValue = new ArrayList<>();
                        data.OptionValue.add(Dateformatter.format(date));
                    }

                    /*data.setAltColor(false);
                    childViewHolder.LLTypeRow1.setBackgroundResource(R.drawable.edittextstylelightgray);*/
                    childViewHolder.DateOnClickListener = null;
                    childViewHolder.lblDate.setOnClickListener(null);
                    childViewHolder.DateOnClickListener = CheckListActivity.rtnlblDateOnClickListener(childViewHolder.slideDateListener, date);
                    childViewHolder.lblDate.setOnClickListener(childViewHolder.DateOnClickListener);
                } catch (Exception e) {
                    /*String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnSlideDateListener. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");*/
                }
            }

            @Override
            public void onDateCancel() {
                // Overriding onDateTimeCancel() is optional.
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener cbMDOnCheckedChangeListener(final checklist_PostData data, final ChildViewHolder childViewHolder, final int pos) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    Boolean hasChild = hasChild(pos);
                    if (isChecked) {
                        data.setIsChecked(true);
                        data.setAltColor(false);
                        if (data.ControlCode.equals("LB")) {
                            if (data.OptionValue != null) {
                                data.OptionValue.clear();
                                data.OptionValue.add("ACKNOWLEDGED");
                            } else {
                                data.OptionValue = new ArrayList<String>();
                                data.OptionValue.add("ACKNOWLEDGED");
                            }
                        } else if (data.ControlCode.equals("CB") || (data.ControlCode.equals("PH") || data.ControlCode.equals("PR"))) {
                            if (data.OptionValue != null) {
                                data.OptionValue.clear();
                                data.OptionValue.add("YES");
                            } else {
                                data.OptionValue = new ArrayList<String>();
                                data.OptionValue.add("YES");
                            }
                        }

                        if (data.IsParent && hasChild) {
                            showHierarchyNew2(pos, childViewHolder, data);
                        }
                        EnableDisableCtrlsNew(true, childViewHolder, data);
                        notifyDataSetChanged();
                    } else {
                        data.setIsChecked(false);
                        data.OptionValue = null;

                        if (data.IsParent && hasChild) {
                            //hideHierarchyNew2(pos, childViewHolder);
                            hideHierarchyOnly(pos, childViewHolder);
                        }
                        EnableDisableCtrlsNew(false, childViewHolder, data);
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter cbMDOnCheckedChangeListener. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter cbMDOnCheckedChangeListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                }

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener cbOPOnCheckedChangeListener(final checklist_PostData data, final ChildViewHolder childViewHolder, final int pos) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    Boolean hasChild = hasChild(pos);
                    if (isChecked) {
                        data.setIsChecked(true);
                        data.setAltColor(false);
                        if (data.ControlCode.equals("LB")) {
                            if (data.OptionValue != null) {
                                data.OptionValue.clear();
                                data.OptionValue.add("ACKNOWLEDGED");
                            } else {
                                data.OptionValue = new ArrayList<String>();
                                data.OptionValue.add("ACKNOWLEDGED");
                            }
                        } else if (data.ControlCode.equals("CB")) {
                            if (data.OptionValue != null) {
                                data.OptionValue.clear();
                                data.OptionValue.add("YES");
                            } else {
                                data.OptionValue = new ArrayList<String>();
                                data.OptionValue.add("YES");
                            }
                        }

                        if (data.IsParent && hasChild) {
                            showHierarchyNew2(pos, childViewHolder, data);
                        }
                        notifyDataSetChanged();
                    } else {
                        data.setIsChecked(false);
                        data.OptionValue = null;

                        if (data.IsParent && hasChild) {
                            //hideHierarchyNew2(pos, childViewHolder);
                            hideHierarchyOnly(pos, childViewHolder);
                        }
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter cbOPOnCheckedChangeListener. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter cbOPOnCheckedChangeListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                }

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener cbForTextboxOnCheckedChangeListener(final checklist_PostData data, final ChildViewHolder childViewHolder) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        data.setIsChecked(true);
                        data.OptionValue = null;
                        if (data.FinalManOptCode.equals("MD")) {
                            EnableDisableCtrlsNew(true, childViewHolder, data);
                        } else {
                            EnableDisableCtrlsNew(false, childViewHolder, data);
                        }
                        notifyDataSetChanged();
                    } else {
                        data.setIsChecked(false);
                        data.OptionValue = null;
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter cbForTextboxOnCheckedChangeListener. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter cbForTextboxOnCheckedChangeListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                }

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener cbForIDVerificationOnCheckedChangeListener(final checklist_PostData data, final ChildViewHolder childViewHolder) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        data.setIsChecked(true);
                        data.OptionValue = null;
                        if (data.FinalManOptCode.equals("MD")) {
                            EnableDisableCtrlsNew(true, childViewHolder, data);
                        } else {
                            EnableDisableCtrlsNew(false, childViewHolder, data);
                        }
                        notifyDataSetChanged();
                    } else {
                        data.setIsChecked(false);
                        data.OptionValue = null;
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter cbForIDVerificationOnCheckedChangeListener. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter cbForIDVerificationOnCheckedChangeListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                }

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener cbForV1VerificationOnCheckedChangeListener(final checklist_PostData data, final ChildViewHolder childViewHolder) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        data.setIsChecked(true);
                        data.OptionValue = null;
                        if (data.FinalManOptCode.equals("MD")) {
                            EnableDisableCtrlsNew(true, childViewHolder, data);
                        } else {
                            EnableDisableCtrlsNew(false, childViewHolder, data);
                        }
                        notifyDataSetChanged();
                        Context ctx = myApp.getAppContext();
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_askcontactnum_disciplinary)).show();
                    } else {
                        data.setIsChecked(false);
                        data.OptionValue = null;
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter cbForV1VerificationOnCheckedChangeListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener cbForYesNoOnCheckedChangeListener(final checklist_PostData data) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        data.setIsChecked(true);
                        data.setAltColor(false);
                        if (data.OptionValue != null && data.OptionValue.size() > 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add("NO");
                        } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                            data.OptionValue.clear();
                            data.OptionValue.add("NO");
                        } else if (data.OptionValue == null) {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add("NO");
                        }
                    } else {
                        data.setIsChecked(false);
                        if (data.FinalManOptCode.equals("OP")) {
                            if (data.OptionValue != null && data.OptionValue.size() > 0) {
                                data.OptionValue = null;
                            } else if (data.OptionValue != null && data.OptionValue.size() == 0) {
                                data.OptionValue = null;
                            } else if (data.OptionValue == null) {
                                data.OptionValue = null;
                            }
                        } else {
                            data.OptionValue = new ArrayList<>();
                            data.OptionValue.add("NO");
                        }
                    }
                    notifyDataSetChanged();
                } catch (Exception e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter cbForYesNoOnCheckedChangeListener. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter cbForYesNoOnCheckedChangeListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                }

            }
        };
    }

    private void EnableDisableOTPbtn(ChildViewHolder childViewHolder, checklist_PostData data) {
        //TypeOfPinVerification OTP: 0 == 0 or within 3 tries, 1 == verified, 2 == after 3 fail tries, 3 == skipped
        Context ctx = myApp.getAppContext();
        String txt = ctx.getString(R.string.buttontext_otp_not_verified);
        Boolean b_enabled = false;
        String TypeOfPinVerification = "";

        try {
            if (data.OptionValue != null && data.OptionValue.size() > 0) {
                if (data.OptionValue.get(0) != null) {
                    TypeOfPinVerification = data.OptionValue.get(0);
                    b_enabled = TypeOfPinVerification.equals("0") ? true : false;
                }
            }

            txt = TypeOfPinVerification.equals("1") ? ctx.getString(R.string.buttontext_otp_verified) :
                    TypeOfPinVerification.equals("2") ? ctx.getString(R.string.buttontext_otp_not_verified2) :
                            TypeOfPinVerification.equals("3") ? ctx.getString(R.string.buttontext_otp_skipped) :
                                    ctx.getString(R.string.buttontext_otp_not_verified);

            childViewHolder.btnOTP.setText(txt);
            ctrlSettings.enableDisableSubmitButtonWithStyles(b_enabled, childViewHolder.btnOTP);
            hide10to19(TypeOfPinVerification.equals("0") || TypeOfPinVerification.equals("1") ? true : false, childViewHolder);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter EnableDisableOTPbtn. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void EnableDisableOTPbtnNew(ChildViewHolder childViewHolder, checklist_PostData data) {
        //TypeOfPinVerification OTP: 0 == 0 or within 3 tries, 1 == verified, 2 == after 3 fail tries, 3 == skipped
        Context ctx = myApp.getAppContext();
        String txt = ctx.getString(R.string.buttontext_otp_not_verified);
        Boolean b_enabled = false;
        String TypeOfPinVerification = "";
        Integer FLAG = 0;

        try {
            if (data.OptionValue != null && data.OptionValue.size() > 0) {
                if (data.OptionValue.get(0) != null) {
                    TypeOfPinVerification = data.OptionValue.get(0);
                    b_enabled = TypeOfPinVerification.equals("0") ? true : false;
                }
            }

            FLAG = data.FLAG;

            txt = TypeOfPinVerification.equals("1") ? ctx.getString(R.string.buttontext_otp_verified) :
                    TypeOfPinVerification.equals("2") && ((FLAG & 8192) == 8192) ? ctx.getString(R.string.buttontext_otp_not_verified3) :
                            TypeOfPinVerification.equals("2") ? ctx.getString(R.string.buttontext_otp_not_verified2) :
                                    TypeOfPinVerification.equals("3") ? ctx.getString(R.string.buttontext_otp_skipped) :
                                            ctx.getString(R.string.buttontext_otp_not_verified);

            childViewHolder.btnOTP.setText(txt);
            ctrlSettings.enableDisableSubmitButtonWithStyles(b_enabled, childViewHolder.btnOTP);
            //hide10to19(TypeOfPinVerification.equals("0") || TypeOfPinVerification.equals("1") ? true : false, childViewHolder);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter EnableDisableOTPbtnNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void EnableDisableCtrlsNew(Boolean enabled, ChildViewHolder childViewHolder, checklist_PostData data) {
        try {
            if (enabled) {
                childViewHolder.cbMD.setEnabled(false);
                childViewHolder.cbMD2.setEnabled(false);
                childViewHolder.cb.setEnabled(false);

                if (data.ControlCode.equals("LB")) {

                } else if (data.ControlCode.equals("CB")) {

                } else if (data.ControlCode.equals("AT")) {
                    childViewHolder.txtLabelTagVal.setEnabled(true);
                    childViewHolder.btnScanTag.setEnabled(true);
                    childViewHolder.btnUpdateLabelTag.setEnabled(true);
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, childViewHolder.btnScanTag);
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, childViewHolder.btnUpdateLabelTag);
                } else if (data.ControlCode.equals("TB")) {
                    childViewHolder.txtValue.setEnabled(true);
                    childViewHolder.txtValue2.setEnabled(true);
                } else if (data.ControlCode.equals("DD")) {
                    childViewHolder.lblDate.setEnabled(true);
                } else if (data.ControlCode.equals("YN")) {
                    childViewHolder.yesno.setEnabled(true);
                } else if (data.ControlCode.equals("PH") || data.ControlCode.equals("PR")) {
                    //data.setPhotoImagePath("");
                    data.setDataT(null);
                    childViewHolder.imgPhoto.setEnabled(true);
                    childViewHolder.imgPhoto.setImageResource(R.mipmap.ic_action_camera_alt_enabled);
                    /*childViewHolder.imgCancelPhoto.setEnabled(data.PhotoImagePath != null && !data.PhotoImagePath.equals("") ? true : false);
                    childViewHolder.imgCancelPhoto.setImageResource(data.PhotoImagePath != null && !data.PhotoImagePath.equals("") ? R.mipmap.ic_action_collections_enabled : R.mipmap.ic_action_collections);*/
                    childViewHolder.imgCancelPhoto.setEnabled(data.dataT != null && data.dataT.size() > 0 ? true : false);
                    childViewHolder.imgCancelPhoto.setImageResource(data.dataT != null && data.dataT.size() > 0 ? R.mipmap.ic_action_collections_enabled : R.mipmap.ic_action_collections);
                } else if (data.ControlCode.equals("CG")) {
                    childViewHolder.LLCheckbox.setEnabled(true);
                } else if (data.ControlCode.equals("RG")) {
                    childViewHolder.rg1.setEnabled(true);
                } else if (data.ControlCode.equals("ID") || data.ControlCode.equals("V1") || data.ControlCode.equals("V5")) {
                    childViewHolder.txtVerification.setEnabled(true);
                    childViewHolder.btnVerification.setEnabled(true);
                    if (data.ControlCode.equals("V1") || data.ControlCode.equals("V5")) {
                        childViewHolder.yesnoIDVerificationSkip.setEnabled(true);
                    }
                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, childViewHolder.btnVerification);
                } else if (data.ControlCode.equals("OT")) {
                    EnableDisableOTPbtnNew(childViewHolder, data);
                } else if (data.ControlCode.equals("V2")) {
                    childViewHolder.txtIMEIVerification.setEnabled(true);
                    childViewHolder.btnVerificationIMEI.setEnabled(true);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, childViewHolder.btnVerificationIMEI);
                } else if (data.ControlCode.equals("V3")) {
                    childViewHolder.txtIMEIVerification.setEnabled(true);
                    childViewHolder.btnVerificationIMEI.setEnabled(true);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, childViewHolder.btnVerificationIMEI);
                } else if (data.ControlCode.equals("V4")) {
                    childViewHolder.txtIMEIVerification.setEnabled(true);
                    childViewHolder.btnVerificationIMEI.setEnabled(true);
                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, childViewHolder.btnVerificationIMEI);
                }
            } else {
                childViewHolder.txtValue.setEnabled(false);
                childViewHolder.txtValue2.setEnabled(false);

                for (int i = 0; i <= childViewHolder.rg1.getChildCount() - 1; i++) {
                    ((RadioButton) childViewHolder.rg1.getChildAt(i)).setEnabled(false);
                    ((RadioButton) childViewHolder.rg1.getChildAt(i)).setTextColor(Color.GRAY);
                }
                for (int i = 0; i <= childViewHolder.LLCheckbox.getChildCount() - 1; i++) {
                    ((CheckBox) childViewHolder.LLCheckbox.getChildAt(i)).setEnabled(false);
                }
                childViewHolder.imgbtnDate.setEnabled(false);
                childViewHolder.lblDate.setEnabled(false);
                childViewHolder.imgbtnDateTimeTime.setEnabled(false);
                childViewHolder.lblDateTimeTime.setEnabled(false);
                childViewHolder.yesno.setEnabled(false);
                childViewHolder.imgPhoto.setEnabled(false);
                childViewHolder.imgPhoto.setImageResource(R.mipmap.ic_action_camera_alt);
                childViewHolder.imgCancelPhoto.setEnabled(false);
                childViewHolder.imgCancelPhoto.setImageResource(R.mipmap.ic_action_collections);
                if (data.ControlCode.equals("ID") || data.ControlCode.equals("V1") || data.ControlCode.equals("V5")) {
                    childViewHolder.txtVerification.setEnabled(false);
                    childViewHolder.txtVerification.setTextColor(Color.parseColor("#000000"));
                    if (data.ControlCode.equals("V1") || data.ControlCode.equals("V5")) {
                        if (data.OptionValue2 == null) {
                            childViewHolder.yesnoIDVerificationSkip.setEnabled(false);
                        } else {
                            childViewHolder.yesnoIDVerificationSkip.setEnabled(true);
                        }
                    }
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerification);
                } else if (data.ControlCode.equals("AT")) {
                    childViewHolder.txtLabelTagVal.setEnabled(false);
                    childViewHolder.btnScanTag.setEnabled(false);
                    childViewHolder.btnUpdateLabelTag.setEnabled(false);
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, childViewHolder.btnScanTag);
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, childViewHolder.btnUpdateLabelTag);
                } else if (data.ControlCode.equals("OT")) {
                    EnableDisableOTPbtnNew(childViewHolder, data);
                } else if (data.ControlCode.equals("V2")) {
                    childViewHolder.txtIMEIVerification.setEnabled(false);
                    childViewHolder.txtIMEIVerification.setTextColor(Color.parseColor("#000000"));
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerificationIMEI);
                } else if (data.ControlCode.equals("V3")) {
                    childViewHolder.txtIMEIVerification.setEnabled(false);
                    childViewHolder.txtIMEIVerification.setTextColor(Color.parseColor("#000000"));
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerificationIMEI);
                } else if (data.ControlCode.equals("V4")) {
                    childViewHolder.txtIMEIVerification.setEnabled(false);
                    childViewHolder.txtIMEIVerification.setTextColor(Color.parseColor("#000000"));
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, childViewHolder.btnVerificationIMEI);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter EnableDisableCtrlsNew. Exception: " + e.getMessage() + "\nStack:: " + s, "", "", "", "");
        }
    }

    //custom date picker
    public static void displaySelectedPendingDate(final TextView lblestDate, final Integer checklistindex, final Button abtnOK) {
        Date dt = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int NoOfDaysInMth = 0;
        try {
            if (CheckListActivity.al != null && CheckListActivity.al.size() > 0) {

                Calendar c = Calendar.getInstance();
                c.setTime(dt);
                Integer PendingDay = CheckListActivity.al.get(checklistindex).SelectedDatePendingDay != null &&
                        CheckListActivity.al.get(checklistindex).SelectedDatePendingDay > 0 ? CheckListActivity.al.get(checklistindex).SelectedDatePendingDay : null;
                Integer PendingMth = CheckListActivity.al.get(checklistindex).SelectedDatePendingMth != null &&
                        CheckListActivity.al.get(checklistindex).SelectedDatePendingMth >= 0 ? CheckListActivity.al.get(checklistindex).SelectedDatePendingMth : null;
                Integer PendingYr = CheckListActivity.al.get(checklistindex).SelectedDatePendingYear != null &&
                        CheckListActivity.al.get(checklistindex).SelectedDatePendingYear > 0 ? CheckListActivity.al.get(checklistindex).SelectedDatePendingYear : null;

                if (PendingDay != null && PendingMth != null && PendingYr != null) {
                    //c.set(Calendar.DAY_OF_MONTH, PendingDay);
                    c.set(Calendar.DAY_OF_MONTH, 1);
                    c.set(Calendar.MONTH, PendingMth);
                    c.set(Calendar.YEAR, PendingYr);

                    NoOfDaysInMth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (PendingDay > NoOfDaysInMth) {
                        CheckListActivity.al.get(checklistindex).setSelectedDatePendingDay(null);
                        dt = null;
                    } else {
                        c.set(Calendar.DAY_OF_MONTH, PendingDay);
                        dt = c.getTime();
                        CheckListActivity.al.get(checklistindex).setSelectedDatePending(dt);
                    }
                } else {
                    dt = null;
                }
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter displaySelectedPendingDate. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter displaySelectedPendingDate. Exception: " + e.getMessage().toString(), "", "", "", "");
            dt = null;
        } finally {
            lblestDate.setText(dt == null ? "DD/MM/YYYY" : dateFormat.format(dt));
            //ctrlSettings.enableDisableSubmitButtonWithStyles(dt == null ? false : true, abtnOK);
            ctrlSettings.enableDisableSubmitButtonWithStyles(dt == null ? false : true, btnDateOK);
        }
    }

    public static void changeDayPickerAL(final TextView lblestDate, final Integer achecklistindex, GridView gvDays, final Button abtnOK) {
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
        gvDatePickerDay_PostData gvdpdata;
        ArrayList<gvDatePickerDay_PostData> gvDatePickerDayAL = new ArrayList<gvDatePickerDay_PostData>();
        Boolean isFutureDate = false;
        checklist_PostData data = null;
        try {
            if (CheckListActivity.al != null && CheckListActivity.al.size() > 0) {
                data = CheckListActivity.al.get(achecklistindex);
                c.set(Calendar.MONTH, data.SelectedDatePendingMth != null && data.SelectedDatePendingMth >= 0 ? data.SelectedDatePendingMth : currMth);
                c.set(Calendar.YEAR, data.SelectedDatePendingYear != null && data.SelectedDatePendingYear > 0 ? data.SelectedDatePendingYear : currYear);
                NoOfDaysInMth = data.SelectedDatePendingMth != null && data.SelectedDatePendingMth >= 0 ? c.getActualMaximum(Calendar.DAY_OF_MONTH) : 0;

                if (NoOfDaysInMth > 0) {
                    for (int x = 0; x <= 30; x++) {
                        if (x <= NoOfDaysInMth - 1) {
                            c2.set(data.SelectedDatePendingYear != null && data.SelectedDatePendingYear > 0 ? data.SelectedDatePendingYear : currYear,
                                    data.SelectedDatePendingMth != null && data.SelectedDatePendingMth >= 0 ? data.SelectedDatePendingMth : currMth, x + 1, 0, 0);
                            dt = c2.getTime();

                            isFutureDate = data.SelectedDatePendingYear != null && data.SelectedDatePendingYear > 0 && (currdt.compareTo(dt) < 0 || currdt.compareTo(dt) == 0) ? true : false;
                            if (isFutureDate && data.SelectedDatePendingDay != null && data.SelectedDatePendingDay == x + 1) {
                                CheckListActivity.al.get(achecklistindex).setSelectedDatePendingDay(null);
                            }
                            gvdpdata = new gvDatePickerDay_PostData(x + 1,
                                    data.SelectedDatePendingDay != null && data.SelectedDatePendingDay == x + 1 && !isFutureDate ? true : false, isFutureDate ? false : true);
                        } else {
                            gvdpdata = new gvDatePickerDay_PostData(x + 1, false, false);
                        }
                        gvDatePickerDayAL.add(gvdpdata);
                    }
                } else {
                    for (int x = 0; x <= 30; x++) {
                        c2.set(data.SelectedDatePendingYear != null && data.SelectedDatePendingYear > 0 ? data.SelectedDatePendingYear : currYear,
                                data.SelectedDatePendingMth != null && data.SelectedDatePendingMth >= 0 ? data.SelectedDatePendingMth : currMth, x + 1, 0, 0);
                        dt = c2.getTime();
                        //isFutureDate = data.SelectedDatePendingYear != null && data.SelectedDatePendingYear > 0 && (currdt.compareTo(dt) < 0 || currdt.compareTo(dt) == 0) ? true : false;
                        //gvdpdata = new gvDatePickerDay_PostData(x + 1, false, isFutureDate ? false : true);
                        gvdpdata = new gvDatePickerDay_PostData(x + 1, false, true);
                        gvDatePickerDayAL.add(gvdpdata);
                    }
                }

                gvDays.setAdapter(new gvDatePickerDayAdapter(myApp.getAppContext(), gvDatePickerDayAL, gvDays, achecklistindex, lblestDate, abtnOK));
                displaySelectedPendingDate(lblestDate, achecklistindex, abtnOK);
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter changeDayPickerAL. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter changeDayPickerAL. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public static AdapterView.OnItemSelectedListener DatePickerYear_SelectedIndexChanged(final TextView lblestDate, final Integer checklistindex, final GridView gvDays, final Button abtnOK) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView lblDescEN = (TextView) view.findViewById(R.id.lblStatusCodesDescEN);
                try {
                    if (CheckListActivity.al != null && CheckListActivity.al.size() > 0) {
                        if (position == 0 && CheckListActivity.al != null && CheckListActivity.al.size() > 0) {
                            CheckListActivity.al.get(checklistindex).setSelectedDatePendingYear(null);
                            CheckListActivity.al.get(checklistindex).setSelectedDatePendingYearIndex(0);
                        } else if (position > 0 && CheckListActivity.al != null && CheckListActivity.al.size() > 0) {
                            CheckListActivity.al.get(checklistindex).setSelectedDatePendingYear(Integer.parseInt(lblDescEN.getText().toString()));
                            CheckListActivity.al.get(checklistindex).setSelectedDatePendingYearIndex(position);
                        }
                        changeDayPickerAL(lblestDate, checklistindex, gvDays, abtnOK);
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

    private View.OnClickListener rtnCustomDatePickerOnClickListener2(final checklist_PostData data, final ChildViewHolder vh) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDateActivity.cldata = data;
                Intent i = new Intent(myApp.currentActivity, CustomDateActivity.class);
                //myApp.getAppContext().startActivity(i);
                myApp.currentActivity.startActivity(i);
            }
        };
    }

    private View.OnClickListener rtnCustomDatePickerOnClickListener(final checklist_PostData data, final ChildViewHolder vh) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    final Context ctx = myApp.getAppContext();
                    int width = (int) (ctx.getResources().getDisplayMetrics().widthPixels * 0.90);
                    int height = (int) (ctx.getResources().getDisplayMetrics().heightPixels * 0.90);
                    LayoutInflater layoutInflater = LayoutInflater.from(myApp.currentActivity);
                    final View promptView = layoutInflater.inflate(R.layout.custom_datepicker, null);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myApp.currentActivity);
                    alertDialogBuilder.setView(promptView);
                    final Button btnCancel = (Button) promptView.findViewById(R.id.btnCancel);
                    final Button btnOK = (Button) promptView.findViewById(R.id.btnOK);
                    btnDateOK = btnOK;
                    final TextView lblestDate = (TextView) promptView.findViewById(R.id.lblestDate);
                    final GridView gvDays = (GridView) promptView.findViewById(R.id.gvDays);
                    final GridView gvMth = (GridView) promptView.findViewById(R.id.gvMth);
                    final Spinner ddlyear = (Spinner) promptView.findViewById(R.id.ddlyear);
                    Date dt = new Date();
                    Calendar c = Calendar.getInstance();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(dt);
                    int selectedMth = -1;
                    c.setTime(dt);
                    int currYear = c.get(Calendar.YEAR);
                    ArrayList<Spinner_PostData> DatePickerYearAL = new ArrayList<>();
                    Spinner_PostData DatePickerYearData;
                    DatePickerYearData = new Spinner_PostData(myApp.getAppContext().getString(R.string.spinnerSelect), "-1", "-1", "");
                    DatePickerYearAL.add(DatePickerYearData);
                    DatePickerYearData = new Spinner_PostData(String.valueOf(currYear), String.valueOf(currYear), String.valueOf(currYear), "");
                    DatePickerYearAL.add(DatePickerYearData);
                    gvDatePickerDay_PostData gvdpdata;
                    ArrayList<gvDatePickerDay_PostData> gvDatePickerMthAL = new ArrayList<gvDatePickerDay_PostData>();

                    for (int x = 1; x <= 100; x++) {
                        DatePickerYearData = new Spinner_PostData(String.valueOf(currYear - x), String.valueOf(currYear - x), String.valueOf(currYear - x), "");
                        DatePickerYearAL.add(DatePickerYearData);
                    }

                    Spinner_PostItemAdapter DatePickerYear_adapter = new Spinner_PostItemAdapter(ctx, DatePickerYearAL);
                    ddlyear.setAdapter(DatePickerYear_adapter);

                    if (data.SelectedDate != null) {
                        dt = data.SelectedDate;
                        c.setTime(dt);
                        selectedMth = c.get(Calendar.MONTH);
                    } else {
                        c.setTime(dt);
                        c.add(Calendar.DATE, -1);
                        selectedMth = data.SelectedDatePendingMth != null ? data.SelectedDatePendingMth : c.get(Calendar.MONTH);
                    }

                    ddlyear.setOnItemSelectedListener(null);
                    int spinnerPosition = data.SelectedDatePendingYearIndex != null ? data.SelectedDatePendingYearIndex : 0;
                    ddlyear.setSelection(spinnerPosition);
                    //ddlyear.setOnItemSelectedListener(DatePickerYear_SelectedIndexChanged(lblestDate, data.index, gvDays, btnOK));
                    ddlyear.setOnItemSelectedListener(DatePickerYear_SelectedIndexChanged(lblestDate, data.index, gvDays, btnDateOK));

                    for (int x = 0; x <= 11; x++) {
                        gvdpdata = new gvDatePickerDay_PostData(x + 1, false, true);
                        gvDatePickerMthAL.add(gvdpdata);
                    }

                    //gvMth.setAdapter(new gvDatePickerMthAdapter(ctx, gvDatePickerMthAL, gvDays, data.index, lblestDate, btnOK));
                    //changeDayPickerAL(lblestDate, data.index, gvDays, btnOK);
                    gvMth.setAdapter(new gvDatePickerMthAdapter(ctx, gvDatePickerMthAL, gvDays, data.index, lblestDate, btnDateOK));
                    changeDayPickerAL(lblestDate, data.index, gvDays, btnDateOK);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (CheckListActivity.al != null && CheckListActivity.al.size() > 0) {
                                    CheckListActivity.al.get(data.index).setSelectedDatePendingDay(null);
                                    CheckListActivity.al.get(data.index).setSelectedDatePendingMth(null);
                                    CheckListActivity.al.get(data.index).setSelectedDatePendingYear(null);
                                    CheckListActivity.al.get(data.index).setSelectedDatePending(null);
                                    CheckListActivity.al.get(data.index).setSelectedDatePendingYearIndex(0);
                                }
                            } catch (Exception e) {
                                String s = JobsHandler.getStackTraceString(e);
                                objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnCustomDatePickerOnClickListener " +
                                        "btnCancel.setOnClickListener. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
                                JobsHandler.sendErrorEmail("Exception at checklist_PostItemAdapter rtnCustomDatePickerOnClickListener " +
                                        "btnCancel.setOnClickListener. Exception: " + e.getMessage().toString() + "\nStack: " + s);
                            } finally {
                                if (alertD != null) {
                                    alertD.dismiss();
                                    alertD = null;
                                }
                            }
                        }
                    });

                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (CheckListActivity.al != null && CheckListActivity.al.size() > 0) {
                                    if (CheckListActivity.al.get(data.index).SelectedDatePending != null &&
                                            CheckListActivity.al.get(data.index).SelectedDatePendingDay != null &&
                                            CheckListActivity.al.get(data.index).SelectedDatePendingMth != null &&
                                            CheckListActivity.al.get(data.index).SelectedDatePendingYear != null) {

                                        CheckListActivity.al.get(data.index).setSelectedDate(CheckListActivity.al.get(data.index).getSelectedDatePending());
                                        if (CheckListActivity.al.get(data.index).OptionValue != null && CheckListActivity.al.get(data.index).OptionValue.size() > 0) {
                                            CheckListActivity.al.get(data.index).OptionValue.clear();
                                            CheckListActivity.al.get(data.index).OptionValue.add(Dateformatter.format(CheckListActivity.al.get(data.index).getSelectedDatePending()));
                                        } else if (CheckListActivity.al.get(data.index).OptionValue != null && CheckListActivity.al.get(data.index).OptionValue.size() == 0) {
                                            CheckListActivity.al.get(data.index).OptionValue.clear();
                                            CheckListActivity.al.get(data.index).OptionValue.add(Dateformatter.format(CheckListActivity.al.get(data.index).getSelectedDatePending()));
                                        } else if (CheckListActivity.al.get(data.index).OptionValue == null) {
                                            CheckListActivity.al.get(data.index).OptionValue = new ArrayList<>();
                                            CheckListActivity.al.get(data.index).OptionValue.add(Dateformatter.format(CheckListActivity.al.get(data.index).getSelectedDatePending()));
                                        }

                                        CheckListActivity.al.get(data.index).setSelectedDatePendingDay(null);
                                        CheckListActivity.al.get(data.index).setSelectedDatePendingMth(null);
                                        CheckListActivity.al.get(data.index).setSelectedDatePendingYear(null);
                                        CheckListActivity.al.get(data.index).setSelectedDatePending(null);
                                        CheckListActivity.al.get(data.index).setSelectedDatePendingYearIndex(0);

                                        notifyDataSetChanged();
                                    }
                                }
                            } catch (Exception e) {
                                String s = JobsHandler.getStackTraceString(e);
                                objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnCustomDatePickerOnClickListener " +
                                        "btnOK.setOnClickListener. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
                                JobsHandler.sendErrorEmail("Exception at checklist_PostItemAdapter rtnCustomDatePickerOnClickListener " +
                                        "btnOK.setOnClickListener. Exception: " + e.getMessage().toString() + "\nStack: " + s);
                            } finally {
                                if (alertD != null) {
                                    alertD.dismiss();
                                    alertD = null;
                                }
                            }
                        }
                    });

                    alertDialogBuilder.setCancelable(false);
                    // create an alert dialog
                    alertD = alertDialogBuilder.create();
                    alertD.getWindow().setLayout(width, height);
                    alertD.show();
                } catch (Exception e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter rtnCustomDatePickerOnClickListener. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                }
            }
        };
    }
    //custom date picker
}


