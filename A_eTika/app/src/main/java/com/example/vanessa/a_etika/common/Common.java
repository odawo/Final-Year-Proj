package com.example.vanessa.a_etika.common;

import android.location.Location;

import com.example.vanessa.a_etika.model.User;
import com.example.vanessa.a_etika.remote.FCMClient;
import com.example.vanessa.a_etika.remote.IFCMService;
import com.example.vanessa.a_etika.remote.IGoogleAPI;
import com.example.vanessa.a_etika.remote.RetroFitClient;

/**
 * Created by Vanessa on 15/11/2017.
 */

public class Common {


//    has all table info from the firebase db to easen maintenance
    public static final String AMBDRIVER_TB = "AMBULANCEDRIVERS"; //userdriver
    public static final String INFO_DRIVER_TB = "DriverInfo";
    public static final String PATIENT_TB = "PatientsInfo";
    public static final String PICKUPREQ_TB = "PickUpRequest";
    public static final String TOKEN_TB = "Tokens";

    public static Location mLastLocation = null;

    public static User currentUser;

    public static String currentToken = "";

    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com/";

    public static IGoogleAPI getGoogleAPI() {
        return RetroFitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    public static IFCMService getFCMService() {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

}
