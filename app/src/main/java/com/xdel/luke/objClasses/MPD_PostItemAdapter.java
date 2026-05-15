package com.xdel.luke.objClasses;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.Ext.extOpts_PostItemAdapter;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;

import java.util.ArrayList;

public class MPD_PostItemAdapter  extends ArrayAdapter<objMPD_Info> {
    private Context ctx;
    private ChildViewHolder h;

    public MPD_PostItemAdapter(Context context, ArrayList<objMPD_Info> data){
        super(context, 0, data);
        this.ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        try {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.mpd_row, parent, false);
                h = new ChildViewHolder();
                h.linearLayout1 = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
                h.lblMPD_Type = (TextView) convertView.findViewById(R.id.lblMPD_Type);
                h.lblMPD_SN = (TextView) convertView.findViewById(R.id.lblMPD_SN);
                convertView.setTag(h);
            } else {
                h = (ChildViewHolder) convertView.getTag();
            }

            objMPD_Info data = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view

            // Lookup view for data population
            TextView lblMPD_Type = (TextView) convertView.findViewById(R.id.lblMPD_Type);
            TextView lblMPD_SN = (TextView) convertView.findViewById(R.id.lblMPD_SN);

            // Populate the data into the template view using the data object
            h.linearLayout1.setOnClickListener(null);
            /*h.lblMPD_Type.setText(
                    data.Device_Type == 1 ? ctx.getString(R.string.mpdcc) :
                            data.Device_Type == 2 ? ctx.getString(R.string.mpdnets) :
                                    data.Device_Type == 3 ? ctx.getString(R.string.mpdnets) :
                                            data.Device_Type == 4 ? ctx.getString(R.string.mpdnets) :
                                                    data.Device_Type == 5 ? ctx.getString(R.string.mpdnets) :
                                                            data.Device_Type == 6 ? ctx.getString(R.string.mpdnets) :
                                                                    data.Device_Type == 13 ? ctx.getString(R.string.mpdnets) :
                                                                            data.Device_Type == 14 ? ctx.getString(R.string.mpdnets) :
                                                                                    data.Device_Type == 15 ? ctx.getString(R.string.mpdnets) :
                                                                                            data.Device_Type == 16 ? ctx.getString(R.string.mpdnets) :
                                                                                                    data.Device_Type == 21 ? ctx.getString(R.string.mpdtemplog) :
                                                                                                            ctx.getString(R.string.mpdunknown));*/
            h.lblMPD_Type.setText(
                    ctx.getString(
                            data.Device_Type == 1 ? R.string.mpdcc :
                                    data.Device_Type == 2 ? R.string.mpdnets :
                                            data.Device_Type == 3 ? R.string.mpdcba :
                                                    data.Device_Type == 4 ? R.string.mpdcbb :
                                                            data.Device_Type == 5 ? R.string.mpdcbc :
                                                                    data.Device_Type == 6 ? R.string.mpdcbd :
                                                                            data.Device_Type == 13 ? R.string.mpdpcmbs :
                                                                                    data.Device_Type == 14 ? R.string.mpdpcmbl :
                                                                                            data.Device_Type == 15 ? R.string.mpdpcmg :
                                                                                                    data.Device_Type == 16 ? R.string.mpdpcmw :
                                                                                                            data.Device_Type == 21 || data.Device_Type == 22 ? R.string.mpdtl :
                                                                                                                    R.string.mpdunknown));

            h.lblMPD_SN.setText(data.Serial_Number);
            h.lblMPD_SN.setPaintFlags((data.Device_Type == 4) ? (h.lblMPD_SN.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG) : 0);
            // Return the completed view to render on screen

            if (data.Device_Type == 4) {
                h.linearLayout1.setOnClickListener(rtnLLonClickListener(data.Serial_Number));
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MPD_PostItemAdapter getView. Exception: " + s,"","","","");
        } finally {
            return convertView;
        }
    }

    private View.OnClickListener rtnLLonClickListener(final String typeNnumber) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Context c = myApp.getAppContext();
                    objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), typeNnumber + " selected.").show();
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at MPD_PostItemAdapter rtnLLonClickListener. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
                }
            }
        };
    }

    public final class ChildViewHolder {
        LinearLayout linearLayout1;
        TextView lblMPD_Type;
        TextView lblMPD_SN;
    }


}
