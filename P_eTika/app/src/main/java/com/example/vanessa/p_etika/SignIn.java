package com.example.vanessa.p_etika;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.vanessa.p_etika.common.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class SignIn extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersdb;

    EditText email, passw;
    Button tonextPage;
    RelativeLayout rootlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersdb = firebaseDatabase.getReference(Common.PATIENT_TB);
        Log.d("onCreate", "onCreate: CommonPatient" + Common.PATIENT_TB);

        rootlayout = (RelativeLayout)findViewById(R.id.rootlayout);


        email = findViewById(R.id.emailtxt);
        passw = findViewById(R.id.passwordtxt);

        tonextPage = findViewById(R.id.tomain_next);


        tonextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tonextPage.setEnabled(false);
                tonextPage.setVisibility(View.GONE);


                if (TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(rootlayout, "fill email address field", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(passw.getText().toString())) {
                    Snackbar.make(rootlayout, "fill password field", Snackbar.LENGTH_SHORT).show();
                    return;
                }

//                ////////
                final android.app.AlertDialog waitingDialog = new SpotsDialog(SignIn.this);
                waitingDialog.show();

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), passw.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                waitingDialog.dismiss();

                                Toast.makeText(SignIn.this,"loading", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignIn.this, DiseasesActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                waitingDialog.dismiss();

                                Snackbar.make(rootlayout, "Failed"+e.getMessage(), Snackbar.LENGTH_SHORT).show();

                                //active signin button
                                tonextPage.setEnabled(true);
                                tonextPage.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });

    }

}
