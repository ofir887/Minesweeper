package com.example.ofirm.minesweeper;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by programing on 03/01/2016.
 */
public class PopupAdapter implements InfoWindowAdapter{
    private View popup=null;
    private LayoutInflater inflater=null;

    PopupAdapter(LayoutInflater inflater) {
        this.inflater=inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.popup, null);
        }

        TextView tv=(TextView)popup.findViewById(R.id.title);

        tv.setText(marker.getTitle());
        tv=(TextView)popup.findViewById(R.id.snippet);
        tv.setText(marker.getSnippet());

        return(popup);
    }
}
