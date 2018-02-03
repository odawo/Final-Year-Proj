package com.example.vanessa.p_etika;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.vanessa.p_etika.common.Common;
import com.example.vanessa.p_etika.model.Patient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersdb;

    Button register;
    EditText email, password, name, phone;
    RelativeLayout rootlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersdb = firebaseDatabase.getReference(Common.PATIENT_TB);
        Log.d("onCreate", "onCreate: CommonPatient" + Common.PATIENT_TB);

        rootlayout = (RelativeLayout)findViewById(R.id.rootlayout);

        register = (Button)findViewById(R.id.registerbtn);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

    }

    private void registerUser() {
        email = (EditText)findViewById(R.id.emailtxt);
        password = (EditText)findViewById(R.id.passwordtxt);
        phone = (EditText)findViewById(R.id.phonetxt);

        if (TextUtils.isEmpty(email.getText().toString())) {
            Snackbar.make(rootlayout, "fill email address field", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone.getText().toString())) {
            Snackbar.make(rootlayout, "fill phone contactitems address field", Snackbar.LENGTH_SHORT).show();
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

        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //save user to db
                        Patient patient = new Patient();
                        patient.setEmail(email.getText().toString());
                        patient.setPassword(password.getText().toString());
                        patient.setPhone(phone.getText().toString());

                        register.setVisibility(View.INVISIBLE);

                        //use uid to key
                        usersdb.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(patient)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(rootlayout, "Registered", Snackbar.LENGTH_SHORT).show();

                                        startActivity(new Intent(SignUp.this, SignIn.class));
                                        finish();
//                                        signInPopUp();
                                    }
                                })

                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(rootlayout, "Failed"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(rootlayout, "Failed"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        register.setVisibility(View.VISIBLE);
                    }
                });
    }

//    private void signInPopUp() {
//
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
////        dialog.setIcon(R.drawable.ic_logo);
//        dialog.setTitle("Confirmation");
//        dialog.setCancelable(false);
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View signinlayout = inflater.inflate(R.layout.activity_signin, null);
//
//        final EditText emaill = signinlayout.findViewById(R.id.emailtxt);
//        final EditText passw = signinlayout.findViewById(R.id.passwordtxt);
//
//        final Button tonextPage = signinlayout.findViewById(R.id.tomain_next);
//
//        dialog.setView(signinlayout);
//
//        tonextPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tonextPage.setEnabled(false);
//                tonextPage.setVisibility(View.GONE);
//
//
//                if (TextUtils.isEmpty(emaill.getText().toString())) {
//                    Snackbar.make(rootlayout, "fill email address field", Snackbar.LENGTH_SHORT).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(passw.getText().toString())) {
//                    Snackbar.make(rootlayout, "fill password field", Snackbar.LENGTH_SHORT).show();
//                    return;
//                }
//
////                ////////
//                final android.app.AlertDialog waitingDialog = new SpotsDialog(SignUp.this);
//                waitingDialog.show();
//
//                firebaseAuth.signInWithEmailAndPassword(emaill.getText().toString(), passw.getText().toString())
//                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                            @Override
//                            public void onSuccess(AuthResult authResult) {
//
//                                waitingDialog.dismiss();
//
////                                FirebaseDatabase.getInstance().getReference(Common.PATIENT_TB).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
////                                        .addListenerForSingleValueEvent(new ValueEventListener() {
////                                            @Override
////                                            public void onDataChange(DataSnapshot dataSnapshot) {
////                                                Common.currentUser = dataSnapshot.getValue(Patient.class);
////                                            }
////
////                                            @Override
////                                            public void onCancelled(DatabaseError databaseError) {
////
////                                            }
////                                        });
////                                Snackbar.make(rootlayout,"loading", Snackbar.LENGTH_LONG).show();
//                                Toast.makeText(SignUp.this,"loading", Toast.LENGTH_LONG).show();
//                                startActivity(new Intent(SignUp.this, DiseasesActivity.class));
//                                finish();
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                                waitingDialog.dismiss();
//
//                                Snackbar.make(rootlayout, "Failed"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
//
//                                //active signin button
//                                tonextPage.setEnabled(true);
//                                tonextPage.setVisibility(View.VISIBLE);
//                            }
//                        });
//            }
//        });
//
//        dialog.show();
//
//    }


//    private void contactPopup() {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setTitle("Patient Contact");
//        dialog.setMessage("Please use your current phone contactitems to make reaching you easy");
//
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        View contactpopupLayout = layoutInflater.inflate(R.layout.activity_signin, null);
//
//        final EditText patientcontactphone = contactpopupLayout.findViewById(R.id.patientcontactno);
//
//        dialog.setView(contactpopupLayout);
//
//        ImageButton tomainimg = (ImageButton)contactpopupLayout.findViewById(R.id.tomain_next);
//
//        tomainimg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (TextUtils.isEmpty(patientcontactphone.getText().toString())) {
//
//                    Snackbar.make(rootLinear, "Kindly fill in your contactitems", Snackbar.LENGTH_SHORT).show();
//                    return;
//
//                }else {
//                    Patient patient = new Patient();
//                    patient.setPhone(patientcontactphone.getText().toString());
//
//                    usersdb.child(Common.PATIENT_TB).setValue(patient).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Snackbar.make(rootLinear, " " + patientcontactphone + " will be used to contactitems you once you make your ambulance request.", Snackbar.LENGTH_SHORT).show();
//
//                            startActivity(new Intent(SignUp.this, HomeActivity.class));
//                            finish();
//                        }
//                    });
//                }
//            }
//        });
//
//        dialog.show();
//    }


}
