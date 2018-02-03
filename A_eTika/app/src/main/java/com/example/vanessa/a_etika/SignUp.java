package com.example.vanessa.a_etika;

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

import com.example.vanessa.a_etika.model.User;
import com.example.vanessa.a_etika.common.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    Button register;
    EditText email, password, name, phone;
    RelativeLayout rootlayout;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersdb = firebaseDatabase.getReference(Common.INFO_DRIVER_TB);

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
        name = (EditText)findViewById(R.id.nametxt);
        phone = (EditText)findViewById(R.id.phonetxt);
        rootlayout = (RelativeLayout)findViewById(R.id.rootlayout);

        if (TextUtils.isEmpty(email.getText().toString())) {
            Snackbar.make(rootlayout, "fill email address field", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone.getText().toString())) {
            Snackbar.make(rootlayout, "fill phone contact address field", Snackbar.LENGTH_SHORT).show();
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
                        User user = new User();
                        user.setEmail(email.getText().toString());
                        user.setName(name.getText().toString());
                        user.setPassword(password.getText().toString());
                        user.setPhone(phone.getText().toString());

                        //use uid to key
                        usersdb.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(rootlayout, name + "registered", Snackbar.LENGTH_SHORT).show();

                                startActivity(new Intent(SignUp.this, SignIn.class));
                                finish();
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
            }
        });

    }
}
