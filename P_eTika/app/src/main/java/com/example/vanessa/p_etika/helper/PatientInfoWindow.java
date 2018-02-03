package com.example.vanessa.p_etika.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import com.example.vanessa.p_etika.R;

/**
 * Created by Vanessa on 15/11/2017.
 */

public class PatientInfoWindow implements GoogleMap.InfoWindowAdapter {

    View myView;

    public PatientInfoWindow(Context context) {
        myView = LayoutInflater.from(context)
                .inflate(R.layout.patient_info_custom_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView txtPickuptitle = (TextView)myView.findViewById(R.id.textPickupInf);
        txtPickuptitle.setText(marker.getTitle());

        TextView txtPickupSnip = (TextView)myView.findViewById(R.id.textPickupSnip);
        txtPickupSnip.setText(marker.getSnippet());

        return myView;

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
