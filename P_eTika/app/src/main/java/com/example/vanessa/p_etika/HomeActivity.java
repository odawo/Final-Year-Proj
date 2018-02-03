package com.example.vanessa.p_etika;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vanessa.p_etika.common.Common;
import com.example.vanessa.p_etika.helper.PatientInfoWindow;
import com.example.vanessa.p_etika.model.FCMResponse;
import com.example.vanessa.p_etika.model.Notification;
import com.example.vanessa.p_etika.model.Patient;
import com.example.vanessa.p_etika.model.Sender;
import com.example.vanessa.p_etika.model.Token;
import com.example.vanessa.p_etika.nearby_hospital.Example;
import com.example.vanessa.p_etika.nearby_hospital.RetrofitMaps;
import com.example.vanessa.p_etika.remote.IFCMService;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    Button buttonPickup;
    ImageView imgExpandable;

    BottomSheetPatientRideFrag mBottomSheetPatientRideFrag;

    SupportMapFragment mapFragment;

    FirebaseAuth firebaseAuth;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    Marker mUserMarker, markerDestination;

    private static final int MY_PERMISSION_REQUEST_CODE = 11;
    private static final int PLAY_SERVICR_RES_REQUEST = 29;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 3000;

    DatabaseReference pref;
    GeoFire geoFire;

//    for sending alerts
    IFCMService mService;

//    presense sys
    DatabaseReference driversAvailable;

    PlaceAutocompleteFragment placedestination, placelocation;
    //remove place location
    String mPlaceLocation, mPlaceDestination;
    String disease;

    boolean ifAmbulanceFound = false;
    String ambdriverId = "";
    int radius = 1; //1 km distance from distress call
    int distance = 1; //1km of all available ambulances
    private static final int limit = 10; //limit of 3km search of available ambulances around ... *for now. can be inc to 10km morefindAmb

//    hospital latitudes and longitudes
    double latitude;
    double longitude;
    private int PROXIMITY_RADIUS = 10000;

//    send sms on ambulance request
    public static String messageString = "";
    public static String s = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Toast.makeText(this, "Click on the map to set hospital destination", Toast.LENGTH_LONG).show();
//        displayLocation(); //this is a change
        mService = Common.getFCMService();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        init view
//        init bottom sheet to app
        imgExpandable = (ImageView)findViewById(R.id.imgExpandable);

        buttonPickup = (Button)findViewById(R.id.btnPickUp);
        buttonPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ifAmbulanceFound) {
                    requestPickup(FirebaseAuth.getInstance().getCurrentUser().getUid());
                } else {
                    sendRequestToAmbDriver(ambdriverId);
//                    sendSMS(s.toString(), messageString.toString());
                }
            }
        });

        placedestination = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_destination);
        placelocation = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_location);

        placelocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //remove place location
                mPlaceLocation = place.getAddress().toString();

                mMap.clear();
                mUserMarker = mMap.addMarker(new MarkerOptions()
                        .position(place.getLatLng())
                        .icon(BitmapDescriptorFactory.defaultMarker())
                        .title("Victim Location"));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));
                //remove place location
//                BottomSheetPatientRideFrag mBottmSheet = BottomSheetPatientRideFrag.newInstance(mPlaceLocation, mPlaceDestination);
//                mBottmSheet.show(getSupportFragmentManager(), mBottmSheet.getTag());
            }

            @Override
            public void onError(Status status) {

            }
        });

        placedestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                mPlaceDestination = place.getAddress().toString();

//                add new destination marker
                mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));

                //show info bottom sheet
                BottomSheetPatientRideFrag mBottomSheetPatientRideFrag = BottomSheetPatientRideFrag.newInstance(mPlaceLocation, mPlaceDestination, false, DiseasesActivity.EXTRA_DISEASE);
                mBottomSheetPatientRideFrag.show(getSupportFragmentManager(), mBottomSheetPatientRideFrag.getTag());
            }

            @Override
            public void onError(Status status) {

            }
        });

        setUpLocation();

        updateFirebaseToken();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

//    private void sendSMS(String phoneNo, String sms) {
//        SmsManager smsManager = SmsManager.getDefault();
//        StringBuffer smsBody = new StringBuffer();
//        smsBody.append(sms +"My location is : "
//                + String.valueOf(mLastLocation.getLatitude()) +" & "
//                + String.valueOf(mLastLocation.getLongitude()));
//        smsManager.sendTextMessage(phoneNo, null, smsBody.toString(), null, null);
//
//    }

    private void updateFirebaseToken() {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null)
            Toast.makeText(this, "no logged in user", Toast.LENGTH_SHORT).show();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.TOKEN_TB);

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
//        if already logged in, token must be updated
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
        //tokens.child(Common.TOKEN_TB).setValue(token);
//        tokens.child();

    }


    private void sendRequestToAmbDriver(String ambdriverId) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.TOKEN_TB);

        tokens.orderByKey().equalTo(ambdriverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()) {

                    Token token = postSnapshot.getValue(Token.class);

//                    make raw payload
                    String json_lat_lng = new Gson().toJson(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    String patientToken = FirebaseInstanceId.getInstance().getToken();
//                    send notification to driver app. the notification will be deserialized
                    Notification data = new Notification(patientToken,json_lat_lng);
//                    Notification data = new Notification("etika",json_lat_lng);
                    Sender content = new Sender(token.getToken(), data);

                    mService.sendMessage(content).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                            if (response.body().success == 1) {
                                Toast.makeText(HomeActivity.this, "Ambulance request sent", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, "Ambulance request failed! Retry", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.e("Error", t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void requestPickup(String uid) {
        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference(Common.PICKUPREQ_TB);
        GeoFire geoFire = new GeoFire(dbRequest);
        geoFire.setLocation(uid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        if (mUserMarker.isVisible())
            mUserMarker.remove();
//        set new marker
        mUserMarker = mMap.addMarker(new MarkerOptions()
                .title("Pickup Point").snippet("")
                .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mUserMarker.showInfoWindow();

        buttonPickup.setText("Searching for an ambulance .. ..");

        findAmbulance();
    }

    private void findAmbulance() {
        DatabaseReference ambdriver = FirebaseDatabase.getInstance().getReference(Common.AMBDRIVER_TB);
        GeoFire geoFire = new GeoFire(ambdriver);

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
//                if ambulance is found
                if (!ifAmbulanceFound) {
                    ifAmbulanceFound = true;
                    ambdriverId = key;
                    buttonPickup.setText("Contact Ambulance ");
//                    Toast.makeText(HomeActivity.this, ""+key, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
//                if no drivers are found nearby, increase distance
                if (!ifAmbulanceFound && radius < limit) {
                    radius++;
                    findAmbulance();
                } else {
                    Toast.makeText(HomeActivity.this, "No ambulance is available within your location", Toast.LENGTH_SHORT).show();
                    buttonPickup.setText("REQUEST PICKUP AGAIN");
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
                break;
        }
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//            repeat runtime permission
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

//            presense sys
            driversAvailable = FirebaseDatabase.getInstance().getReference(Common.AMBDRIVER_TB);
            driversAvailable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loadAvailableAmbulances(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

            //marker
            if (mUserMarker != null)
                //remove present marker
                mUserMarker.remove();
            mUserMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(String.format("My Location")));

            //move camera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

            loadAvailableAmbulances(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

            Log.d("e-Tika", String.format("Your location was changed: %f / %f ", latitude, longitude));

        } else {
            Log.d("e-Tika", "Unable to retrieve user location");
        }

    }

    private void loadAvailableAmbulances(final LatLng location) {

//        deletes all markers on map
        mMap.clear();
//        adds location again
        mMap.addMarker(new MarkerOptions().position(location).title("Emergency call Location"));
//        load available ambulances around distress call. distance of 3km for now
        DatabaseReference ambLocation = FirebaseDatabase.getInstance().getReference(Common.AMBDRIVER_TB);
        GeoFire geoFire1 = new GeoFire(ambLocation);

        GeoQuery geoQuery = geoFire1.queryAtLocation(new GeoLocation(location.latitude, location.longitude),distance);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                /*
                * use key to info from users. These are from the driver
                * registration account and notification update
                * */

                FirebaseDatabase.getInstance().getReference(Common.INFO_DRIVER_TB).child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        use patient model to obtain user since both and patient and user nodes contain similar properties
                        Patient patient = dataSnapshot.getValue(Patient.class);
//                        adding driver to map
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(location.latitude, location.longitude))
                                .flat(true).title(patient.getEmail())
                                .snippet("Phone : " + patient.getPhone())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ambicn)));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance <= limit) {
                    distance++;
                    loadAvailableAmbulances(location);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICR_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "Device not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        displaySelectedScreen(item.getItemId());
        return true;

    }

    private void displaySelectedScreen(int itemId) {

        Fragment fragment = null;

        switch (itemId) {
            case R.id.nav_map:
                fragment = new Fragment();
                break;
//            case R.id.nav_contact:
//                startActivity(new Intent(this, Contacts.class));
//                break;
            case R.id.nav_payment:
                startActivity(new Intent(this, Payment.class));
                break;
            case R.id.nav_hospitals:
                fragment = new Fragment();
                Button h = findViewById(R.id.btn_hospitals);
                h.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                AlphaAnimation mAnimation = new AlphaAnimation(1, 0);
                mAnimation.setDuration(200);
                mAnimation.setInterpolator(new LinearInterpolator());
                mAnimation.setRepeatCount(Animation.INFINITE);
                mAnimation.setRepeatMode(Animation.REVERSE);
                h.startAnimation(mAnimation);

//                onMapReady(GoogleMap);
        }

        if (fragment != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.content_home, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new PatientInfoWindow(this));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                /*
                * *cehck markerDestination, if not null remove available marker
                * */
                if (markerDestination != null)
                    markerDestination.remove();

                markerDestination = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .position(latLng).title("Emergency Drop Off Point"));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

//                when user clicks on map, we will get latitude & longitude to start address
                BottomSheetPatientRideFrag mBottomSheetPatientRideFrag = BottomSheetPatientRideFrag.newInstance(String.format("%f,%f", mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                        String.format("%f,%f",latLng.latitude, latLng.longitude), true, DiseasesActivity.EXTRA_DISEASE);
//                BottomSheetPatientRideFrag mBottomSheetPatientRideFrag = BottomSheetPatientRideFrag.newInstance(String.format(String.valueOf(Locale.getDefault())),
//                        String.format(String.valueOf(Locale.getDefault())), true, DiseasesActivity.EXTRA_DISEASE);
                mBottomSheetPatientRideFrag.show(getSupportFragmentManager(), mBottomSheetPatientRideFrag.getTag());
            }
        });

        Button h = findViewById(R.id.btn_hospitals);
        h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getNearbyHospitals();
                v.clearAnimation();
                build_retrofit_and_get_response("hospital");
            }
        });


    }

//    private void getNearbyHospitals() {
//        build_retrofit_and_get_response("hospital");
//    }

    private void build_retrofit_and_get_response(String hosp) {

        String url = "https://maps.googleapis.com/maps/";
        //String url = "https://maps.googleapis.com/maps/places/json/";


        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);


        //i dont know why lat and lang is blank , i do not have time to look
        // at it so for now ill just make it static --- Kiprop -12-1-2018

        //Call<Example> call = service.getNearbyPlaces(hosp, latitude + "," + longitude, PROXIMITY_RADIUS);
        Call<Example> call = service.getNearbyPlaces(hosp, -1.28333 + "," + 36.81667, PROXIMITY_RADIUS);

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                try {


                    System.out.println("Response "+response.toString());
//                    mMap.clear();
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < response.body().getResults().size(); i++) {
                        Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                        Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
                        String placeName = response.body().getResults().get(i).getName();
                        String vicinity = response.body().getResults().get(i).getVicinity();
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(lat, lng);
                        // Position of Marker on Map
                        markerOptions.position(latLng);
                        // Adding Title to the Marker
                        markerOptions.title(placeName + " : " + vicinity);
                        // Adding Marker to the Camera.
                        Marker m = mMap.addMarker(markerOptions);
                        // Adding colour to the marker
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        // move map camera
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdate();
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyCT5Eza7etWN5HzTSWFE2rX8UtC2CW5Qh0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "onLocationChanged: Entered");
        mLastLocation = location;
        displayLocation();
    }
}
