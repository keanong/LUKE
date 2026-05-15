package com.xdel.luke.objClasses;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xdel.luke.R;

import java.util.List;

/**
 * Created by kean.ong on 13/06/2017.
 */
public class COD_List_RecyclerViewAdapter extends RecyclerView.Adapter<COD_List_RecyclerViewAdapter.MyViewHolder> {

    private List<COD_List_PostData> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblid, year, genre;

        public MyViewHolder(View view) {
            super(view);
            lblid = (TextView) view.findViewById(R.id.lblid);

        }
    }


    public COD_List_RecyclerViewAdapter(List<COD_List_PostData> list){
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.codhorizontallistview_column, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        COD_List_PostData data = list.get(position);
        holder.lblid.setText(data.getID());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}
