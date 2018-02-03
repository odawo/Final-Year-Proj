package com.example.vanessa.a_etika.service;

import android.content.Intent;

import com.example.vanessa.a_etika.PatientCall;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

/**
 * Created by Vanessa on 20/11/2017.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        converted to LatLng since it is the location being sent
        LatLng patient_location = new Gson().fromJson(remoteMessage.getNotification().getBody(), LatLng.class);

        Intent intent = new Intent(getBaseContext(), PatientCall.class);
        intent.putExtra("lat", patient_location.latitude);
        intent.putExtra("lng", patient_location.longitude);
        intent.putExtra("patient", remoteMessage.getNotification().getTitle());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
