package com.example.vanessa.a_etika;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vanessa.a_etika.common.Common;
import com.example.vanessa.a_etika.model.FCMResponse;
import com.example.vanessa.a_etika.model.Notification;
import com.example.vanessa.a_etika.model.Sender;
import com.example.vanessa.a_etika.model.Token;
import com.example.vanessa.a_etika.remote.IFCMService;
import com.example.vanessa.a_etika.remote.IGoogleAPI;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientCall extends AppCompatActivity {

    TextView timetext, addresstext, distancetext;
    Button btnaccept, btndecline;

    MediaPlayer mediaPlayer;

    IGoogleAPI mService;
    IFCMService mIfcmService;

    String patientId;
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_call);

        timetext = findViewById(R.id.timingTxt);
        addresstext = findViewById(R.id.addressTxt);
        distancetext = findViewById(R.id.distanceTxt);

        mService = Common.getGoogleAPI();
        mIfcmService = Common.getFCMService();

        btnaccept = (Button)findViewById(R.id.btnaccept);
        btndecline = (Button)findViewById(R.id.btndecline);

        btndecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRequest(patientId);
            }
        });

        btnaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                send patient data to another activity
                Intent intent =  new Intent(PatientCall.this, AmbulanceTracker.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                intent.putExtra("patientId",patientId);

                startActivity(intent);
                finish();
            }
        });

        //adding track to MP. Is the alarm notification during alert
        mediaPlayer = MediaPlayer.create(this, R.raw.nws_alert_test_tone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (getIntent() != null) {
            lat = getIntent().getDoubleExtra("lat", -1.0);
            lng = getIntent().getDoubleExtra("lng", -1.0);

            System.out.println("LAT + LANG = "+ lat+ "  "+lng);
            patientId = getIntent().getStringExtra("patient");

            getDirection(lat,lng);
        }
    }

    private void cancelRequest(String patientId) {

        Token token = new Token(patientId);

//        Notification notification = new Notification("Sorry!", "Ambulance request service took too long. Please try requesting again");
        Notification notification = new Notification("Sorry!", "Ambulance request had to be cancelled");
        Sender sender = new Sender(notification, token.getToken());

        mIfcmService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                Toast.makeText(PatientCall.this, "Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

            }
        });
    }

    private void getDirection(double lat, double lng) {

        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin="+ Common.mLastLocation.getLatitude()+","+Common.mLastLocation.getLongitude()+
                    "&"+"destination"+lat+","+lng+"&"+"key="+getResources()
                    .getString(R.string.google_direction_api); //print url for debug

            Log.d("eTika", requestApi);
            mService.getPath(requestApi).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    try {

//                        System.out.println("LAT + LANG = "+ response);

                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONArray routes = jsonObject.getJSONArray("routes");
//                        retrieve first elements of routes
                        JSONObject object = routes.getJSONObject(0);
//                        retrieve array with legs name**
                        JSONArray legs = object.getJSONArray("legs");
//                        get first element of legs array
                        JSONObject legsObj = legs.getJSONObject(0);
//                        get distance
                        JSONObject distance = legsObj.getJSONObject("distance");
                        distancetext.setText(distance.getString("text"));
//                        get time
                        JSONObject time = legsObj.getJSONObject("duration");
                        timetext.setText(time.getString("text"));
//                        get address
                        String address = legsObj.getString("end_address");
                        addresstext.setText(address);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(PatientCall.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }
}
