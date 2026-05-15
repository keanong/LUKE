package com.xdel.luke.BottomSheet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class vrpUnlockListViewAdapter extends ArrayAdapter<vrpUnlockPostData> {
    private ArrayList<vrpUnlockPostData> list; // Complete list including hidden items
    private Context ctx;
    private ArrayList<vrpUnlockPostData> visibleList; // Only visible items for ListView
    private ArrayList<vrpUnlockPostData> invisibleList; // Only invisible items

    public vrpUnlockListViewAdapter(Context context, ArrayList<vrpUnlockPostData> pd) {
        super(context, 0, pd);
        list = pd;
        this.visibleList = new ArrayList<>();
        this.invisibleList = new ArrayList<>();
        ctx = context;


        // Initialize invisible list by filtering out hidden items
        updateInvisibleList();
        // Initialize visible list by filtering out hidden items
        updateVisibleList();
    }

    private void updateInvisibleList() {
        invisibleList.clear();
        if (list != null) {
            for (vrpUnlockPostData item : list) {
                if (item.hidden) { // Assuming you add isHidden() method to vrpUnlockPostData
                    invisibleList.add(item);
                }
            }
        }
    }

    private void updateVisibleList() {
        visibleList.clear();
        if (list != null) {
            for (vrpUnlockPostData item : list) {
                if (!item.hidden) { // Assuming you add isHidden() method to vrpUnlockPostData
                    visibleList.add(item);
                }
            }
        }
    }

    @Override
    public int getCount() {
        return visibleList != null ? visibleList.size() : 0;
    }

    @Override
    public vrpUnlockPostData getItem(int position) {
        return visibleList != null ? visibleList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View v, ViewGroup parent) {
        holder h;
        //vrpUnlockPostData d = getItem(position);
        vrpUnlockPostData d = visibleList.get(position);
        try {
            if (ctx == null)
                ctx = myApp.getAppContext();
            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.layout_vrpunlock_item, parent, false);
                h = new holder();
                h.cl = v.findViewById(R.id.lljobquelistitem);
                h.lblJobsIDXs = v.findViewById(R.id.lblJobsIDXs);
                h.lblVrpAddress = v.findViewById(R.id.lblVrpAddress);
                h.lblVrpPostal = v.findViewById(R.id.lblVrpPostal);
                h.lblCount = v.findViewById(R.id.lblCount);
                h.lblpNum_dNum = v.findViewById(R.id.lblpNum_dNum);
                h.lblVrpTW = v.findViewById(R.id.lblVrpTW);
                h.lblpVrpLunch = v.findViewById(R.id.lblpVrpLunch);
                h.lblVrpCold = v.findViewById(R.id.lblVrpCold);
                h.lblVrpNets = v.findViewById(R.id.lblVrpNets);
                h.lblVrpVisa = v.findViewById(R.id.lblVrpVisa);
                h.lblVrpTradein = v.findViewById(R.id.lblVrpTradein);
                h.lblPosition = v.findViewById(R.id.lblPosition);
                v.setTag(h);
            } else {
                h = (holder) v.getTag();
            }

            h.lblJobsIDXs.setText(d.JOBSIDXS);
            h.lblVrpAddress.setText(d.AddressWithPostalNoUnit);
            h.lblVrpPostal.setText(d.POSTAL);
            h.lblpNum_dNum.setText(ctx.getString(R.string.lblpNum) + " " + String.valueOf(d.PIP) + " " + ctx.getString(R.string.lbldNum) + " " + String.valueOf(d.DIP));
            h.lblVrpCold.setVisibility(d.ColdCount > 0 ? View.VISIBLE : View.GONE);
            h.lblVrpNets.setVisibility(d.ReqNetsCount > 0 ? View.VISIBLE : View.GONE);
            h.lblVrpVisa.setVisibility(d.ReqVisaCount > 0 ? View.VISIBLE : View.GONE);
            h.lblVrpTradein.setVisibility(d.ReqTiCount > 0 ? View.VISIBLE : View.GONE);
            h.lblPosition.setVisibility(d.position > 0 ? View.VISIBLE : View.GONE);
            h.lblPosition.setText(d.position > 0 ? String.valueOf(d.position) : "");
            h.lblVrpTW.setText(d.TW);

            h.cl.setBackgroundResource(d.position > 0 ? R.color.brightgreen : R.color.White);

            h.cl.setOnTouchListener(null);
            h.cl.setOnLongClickListener(null);
            h.cl.setOnClickListener(null);
            h.cl.setOnClickListener(clOnClickListener(position));


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at vrpUnlockListViewAdapter getView. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return v;
        }
    }

    public View.OnClickListener clOnClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    changeListPositionValue(position);
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at vrpUnlockListViewAdapter clOnClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

    public vrpUnlockPostData getData(int pos) {
        vrpUnlockPostData d = null;
        try {
            if (list != null && list.size() > 0 && pos >= 0 && pos <= list.size() - 1)
                d = list.get(pos);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at vrpUnlockListViewAdapter getData. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return d;
        }
    }

    public void changeListPositionValue(int editedPos) {
        vrpUnlockPostData d = null;
        int newPositionCurrMax = 0;
        int PositionCurrEdit = -1;
        int usedpostalcount = 0;
        int limitpostalcount = 0;
        int selectedcount = 0;
        boolean selected = false;
        try {
            visibleList = getVisibleList(); //list = getList();
            if (visibleList != null) {
                d = visibleList.get(editedPos);
                usedpostalcount = d.usedpostalcount;
                limitpostalcount = d.limitpostalcount;
                selected = d.selected;
                selectedcount = d.selectedcount;

                if (!selected) { //!selected means its currently adding to q
                    if ((selectedcount + usedpostalcount) < limitpostalcount) {
                        newPositionCurrMax = d.positionCurrMax + 1;
                        for (int i = 0; i <= visibleList.size() - 1; i++) {
                            visibleList.get(i).setPositionCurrMax(newPositionCurrMax);
                            visibleList.get(i).setSelectedcount(selectedcount + 1);

                            if (i == editedPos) {
                                visibleList.get(i).setSelected(true);
                                visibleList.get(i).setPositionCurrMax(newPositionCurrMax);
                                visibleList.get(i).setPosition(newPositionCurrMax);
                            }
                        }
                    } else {
                        Context c = myApp.getAppContext();
                        //objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.max_selection) + " " + String.valueOf(limitpostalcount) + " " + ctx.getString(R.string.selection)).show();
                        objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.max_selection2) + " " + String.valueOf(d.positionCurrMax) + " " + ctx.getString(R.string.selection)).show();
                    }
                } else { //remove from q
                    PositionCurrEdit = d.position;
                    newPositionCurrMax = d.positionCurrMax - 1;
                    for (int i = 0; i <= visibleList.size() - 1; i++) {
                        visibleList.get(i).setPositionCurrMax(newPositionCurrMax);
                        visibleList.get(i).setSelectedcount(selectedcount > 0 ? selectedcount - 1 : 0);

                        if (i == editedPos) {
                            visibleList.get(i).setSelected(false);
                            visibleList.get(i).setPosition(0);
                        } else if (i != editedPos && visibleList.get(i).selected && visibleList.get(i).position > PositionCurrEdit) {
                            visibleList.get(i).setPosition(visibleList.get(i).position - 1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at vrpUnlockListViewAdapter changeListPositionValue. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            notifyDataSetChanged();
        }
    }

    public ArrayList<vrpUnlockPostData> getList() {
        ArrayList<vrpUnlockPostData> l = null;
        if (list != null && list.size() > 0)
            l = list;
        return l;
    }

    public ArrayList<vrpUnlockPostData> getVisibleList() {
        ArrayList<vrpUnlockPostData> l = null;
        if (visibleList != null && visibleList.size() > 0)
            l = visibleList;
        return l;
    }

    public ArrayList<vrpUnlockPostData> getInvisibleList() {
        ArrayList<vrpUnlockPostData> l = null;
        if (invisibleList != null && invisibleList.size() > 0)
            l = invisibleList;
        return l;
    }

    public ArrayList<vrpUnlockPostData> getSelectedList() {
        ArrayList<vrpUnlockPostData> l = null;
        if (visibleList != null && visibleList.size() > 0) {
            for (int i = 0; i <= visibleList.size() - 1; i++) {
                if (visibleList.get(i).position > 0) {
                    if (l == null)
                        l = new ArrayList<>();
                    l.add(visibleList.get(i));
                }
            }
        }
        /*if (invisibleList != null && invisibleList.size() > 0) {
            for (int i = 0; i <= invisibleList.size() - 1; i++) {
                //if (invisibleList.get(i).position > 0) {
                if (l == null)
                    l = new ArrayList<>();

                invisibleList.get(i).setPosition(l.size()+1);
                l.add(invisibleList.get(i));
                //}
            }
        }*/


        if (l != null && l.size() > 0)
            Collections.sort(l,
                    Comparator.comparing(vrpUnlockPostData::getPosition));

        return l;
    }


    public final class holder {
        RelativeLayout cl;
        TextView lblJobsIDXs;
        TextView lblVrpAddress;
        TextView lblVrpPostal;
        TextView lblCount;
        TextView lblpNum_dNum;
        TextView lblVrpTW;
        TextView lblpVrpLunch;
        TextView lblVrpCold;
        TextView lblVrpNets;
        TextView lblVrpVisa;
        TextView lblVrpTradein;
        TextView lblPosition;
    }

}
