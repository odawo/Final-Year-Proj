package com.example.vanessa.a_etika.service;

import com.example.vanessa.a_etika.common.Common;
import com.example.vanessa.a_etika.model.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Vanessa on 20/11/2017.
 */

public class MyFirebaseService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        when token is refreshed, an update needs to be done to the firebase db
        updateTokenToServer(refreshedToken);
    }

    private void updateTokenToServer(String refreshedToken) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.TOKEN_TB);

        Token token = new Token(refreshedToken);
//        if already logged in, token must be updated
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
        }
    }
}
