package com.example.vanessa.a_etika;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.vanessa.a_etika.common.Common;
import com.example.vanessa.a_etika.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

public class SignIn extends AppCompatActivity {

    Button login;
    EditText email, password;
    RelativeLayout rootlayout;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();

        login = (Button)findViewById(R.id.loginbtn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {

        //disable butoon if signin is in progress
        login.setEnabled(false);

        email = (EditText)findViewById(R.id.emailtxt);
        password = (EditText)findViewById(R.id.passwordtxt);
        rootlayout = (RelativeLayout)findViewById(R.id.rootlayout);

        if (TextUtils.isEmpty(email.getText().toString())) {
            Snackbar.make(rootlayout, "fill email address field", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            Snackbar.make(rootlayout, "fill password field", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (password.getText().toString().length() < 6) {
            Snackbar.make(rootlayout, "password should have more than 6 characters", Snackbar.LENGTH_SHORT).show();
            return;
        }

        final AlertDialog waitingDialog = new SpotsDialog(SignIn.this);
        waitingDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        waitingDialog.dismiss();

                        FirebaseDatabase.getInstance().getReference(Common.AMBDRIVER_TB).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Common.currentUser = dataSnapshot.getValue(User.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        startActivity(new Intent(SignIn.this, MapsActivity.class));
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                waitingDialog.dismiss();

                Snackbar.make(rootlayout, "Failed"+e.getMessage(), Snackbar.LENGTH_SHORT).show();

                //active signin button
                login.setEnabled(true);
            }
        });
    }
}

//firebase db rules changes
//read: 'true'
//write: 'true'

//uncomment the alertdialog code snip
