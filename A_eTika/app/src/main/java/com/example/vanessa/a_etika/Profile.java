package com.example.vanessa.a_etika;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vanessa.a_etika.model.UserProfData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends Fragment {

    EditText firstname, lastname, contacts;
    TextView email;
    ImageButton profpic, editicon;
    Button save;

    DatabaseReference rootdatabaseReference, nodedatabaseReference;
    FirebaseAuth firebaseAuth;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firstname = (EditText)view.findViewById(R.id.firstname);
        lastname = (EditText)view.findViewById(R.id.lastname);
        contacts = (EditText)view.findViewById(R.id.phonenumber);
        email = (TextView) view.findViewById(R.id.email);
        profpic = (ImageButton) view.findViewById(R.id.profpic);
        editicon = (ImageButton) view.findViewById(R.id.editicon);
        save = (Button) view.findViewById(R.id.save);

        firebaseAuth = FirebaseAuth.getInstance();
        String user_id = firebaseAuth.getCurrentUser().getUid();
        rootdatabaseReference = FirebaseDatabase.getInstance().getReference();
        nodedatabaseReference = rootdatabaseReference.child("driverdetails").child(user_id);

        profpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setprofilepic();
            }
        });

        editicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                firstname.edit();
//                lastname.isClickable();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveedits();
            }
        });

        return view;
    }

    private void setprofilepic() {

        /*
        * pick photo from gallery
        * or  take a photo
        * crop the photo and save it
        * */

    }


    private boolean saveedits() {

        boolean alldone = true;

       /* * save profile edits and updates besides the email address*/
        String fname = firstname.getText().toString().trim();
        String lname = lastname.getText().toString().trim();
        String phone = contacts.getText().toString().trim();

        if (TextUtils.isEmpty(fname) && TextUtils.isEmpty(lname) && TextUtils.isEmpty(phone)) {
            firstname.setError("first name field empty");
            lastname.setError("last name field empty");
            contacts.setError("contacts field empty");

            firstname.getText().clear();
            lastname.getText().clear();
            contacts.getText().clear();

            return false;
        } else {
            alldone = true;
            firstname.setError(null);
            lastname.setError(null);
            contacts.setError(null);
        }

        //create UserProfData object and set properties
        UserProfData userProfData = new UserProfData();
        userProfData.setFrstname(fname);
        userProfData.setLstname(lname);
        userProfData.setCntact(phone);

        if (firebaseAuth.getCurrentUser() != null) {
            //save user at usernode under uid of user

            nodedatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(userProfData, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {

                        Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        return alldone;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
