package com.example.vanessa.p_etika.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.vanessa.p_etika.R;
import com.example.vanessa.p_etika.model.Notification;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Vanessa on 20/11/2017.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

       if (remoteMessage.getNotification().getTitle().equals("Cancelled")) {
           Handler handler = new Handler(Looper.getMainLooper());
           handler.post(new Runnable() {
               @Override
               public void run() {
                   Toast.makeText(MyFirebaseMessaging.this, ""+remoteMessage.getNotification().getBody(), Toast.LENGTH_SHORT).show();
               }
           });
       } else if (remoteMessage.getNotification().getTitle().equals("Arrival")) {

           showArrivalNotification(remoteMessage.getNotification().getBody());
       }

    }

    private void showArrivalNotification(String body) {
//        create notification channel for api 26 and above

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext()
        , 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true).setDefaults(android.app.Notification.DEFAULT_LIGHTS| android.app.Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Arrival").setContentText(body)
                .setContentIntent(contentIntent);
        NotificationManager manager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }
}
