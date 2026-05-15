package com.xdel.luke;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class CustomGalleryAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater infalter;
    private ArrayList<CustomGallery> data = new ArrayList<CustomGallery>();
    public ImageLoader imageLoader;

    private boolean isActionMultiplePick;

    public CustomGalleryAdapter(Context c, ImageLoader imageLoader) {
        infalter = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = c;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CustomGallery getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setMultiplePick(boolean isMultiplePick) {
        this.isActionMultiplePick = isMultiplePick;
    }

    public void selectAll(boolean selection) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).isSeleted = selection;
        }

        notifyDataSetChanged();
    }

    public ArrayList<CustomGallery> getSelected() {
        ArrayList<CustomGallery> dataList = new ArrayList<CustomGallery>();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSeleted) {
                dataList.add(data.get(i));
            }
        }

        return dataList;
    }

    public int getSelectedCount() {
        int count = 0;
        ArrayList<CustomGallery> dataList = new ArrayList<CustomGallery>();
        try {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).isSeleted) {
                    count++;
                }
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at CUstomGalleryAdapter getSelectedCount. " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at CUstomGalleryAdapter getSelectedCount. " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return count;
        }
    }

    public void addAll(ArrayList<CustomGallery> files) {

        try {
            this.data.clear();
            this.data.addAll(files);

        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    public void changeSelection(View v, int position) {

        if (data.get(position).isSeleted) {
            data.get(position).isSeleted = false;
        } else {
            data.get(position).isSeleted = true;
        }

        ((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(data
                .get(position).isSeleted);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {

            convertView = infalter.inflate(R.layout.gallery_item, null);
            holder = new ViewHolder();
            holder.imgQueue = (ImageView) convertView
                    .findViewById(R.id.imgQueue);

            holder.imgQueueMultiSelected = (ImageView) convertView
                    .findViewById(R.id.imgQueueMultiSelected);

            if (isActionMultiplePick) {
                holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
            } else {
                holder.imgQueueMultiSelected.setVisibility(View.GONE);
            }

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imgQueue.setTag(position);

        try {

            imageLoader.displayImage("file://" + data.get(position).sdcardPath,
                    holder.imgQueue, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.imgQueue
                                    .setImageResource(R.drawable.no_media);
                            super.onLoadingStarted(imageUri, view);
                        }
                    });


			/*Picasso
                    .with(mContext)
					.load(new File(data.get(position).sdcardPath))
					.fit()
					.into(holder.imgQueue);*/

            if (isActionMultiplePick) {

                holder.imgQueueMultiSelected
                        .setSelected(data.get(position).isSeleted);

            }

        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at CustomGalleryAdapter getView. " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at CustomGalleryAdapter getView. " + e.getMessage().toString(), "", "", "", "");
        }

        return convertView;
    }

    public class ViewHolder {
        ImageView imgQueue;
        ImageView imgQueueMultiSelected;
    }

    public void clearCache() {
        imageLoader.clearDiscCache();
        imageLoader.clearMemoryCache();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

}
