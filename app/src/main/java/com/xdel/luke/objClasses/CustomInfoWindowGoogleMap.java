package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.xdel.luke.R;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter  {
    private Context ctx;

    public CustomInfoWindowGoogleMap(Context actx){
        ctx = actx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = null;
        try {
            v = ((Activity)ctx).getLayoutInflater()
                    .inflate(R.layout.custominfowindowgooglemap, null);
            TextView address = v.findViewById(R.id.address);
            TextView postal = v.findViewById(R.id.postal);
            address.setText(marker.getTitle());
            postal.setText(marker.getSnippet());

        } catch (Exception e) {
            String s = e.getMessage().toString();
        } finally {
            return v;
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }




}
