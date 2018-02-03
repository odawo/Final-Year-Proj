package com.example.vanessa.p_etika;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class AidRequest extends AppCompatActivity {

    Button requestAmbbutton;
    ListView listView;

    ArrayList<String> list = new ArrayList<>();

    //db to spinner
    DatabaseReference dref, dataref;

    //push to db
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_request);

        setTitle("Request Aid");

        //recieve passed location notification from the maps fragmentactivity into this activity
        Intent intent = getIntent();
        final String location = intent.getStringExtra("location");

        listView = (ListView)findViewById(R.id.listviewdata);
        requestAmbbutton = (Button)findViewById(R.id.requestambulance);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        dref = FirebaseDatabase.getInstance().getReference();
        dataref = dref.child("ambulancetypes");
        dataref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

              // extra   String value = dataSnapshot.getValue(String.class);

                list.add(dataSnapshot.getValue(String.class));
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }
//
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                list.remove(dataSnapshot.getValue(String.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//
//        dataref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(String.class);
//                list.add(value);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        requestAmbbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestAmbbutton.setText("Calling an Ambulance");

            }
        });

    }
}
//aid is called in homeactivity since button is there. move the button to mapsactivity
// then pass notification across the fragments.  DONE :)