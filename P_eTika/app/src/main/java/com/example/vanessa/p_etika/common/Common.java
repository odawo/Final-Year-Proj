package com.example.vanessa.p_etika.common;

import com.example.vanessa.p_etika.model.Patient;
import com.example.vanessa.p_etika.remote.FCMClient;
import com.example.vanessa.p_etika.remote.GoogleMapAPI;
import com.example.vanessa.p_etika.remote.IFCMService;
import com.example.vanessa.p_etika.remote.IGoogleAPI;

/**
 * Created by Vanessa on 15/11/2017.
 */

public class Common {
//    has all table info from the firebase db to easen maintenance

    public static final String AMBDRIVER_TB = "AMBULANCEDRIVERS";
    public static final String INFO_DRIVER_TB = "DriverInfo";
    public static final String PATIENT_TB = "PatientsInfo";
    public static final String PICKUPREQ_TB = "PickUpRequest";
    public static final String TOKEN_TB = "Tokens";
    public static final String EMERGENCY_CONTACT = "EmergencyContact";

    public static Patient currentUser;

    public static final String fcmURL = "https://fcm.googleapis.com/";
    public static final String googleAPIUrl = "https://maps.googleapis.com";

    private static double base_fare = 50;
    private static double time_rate = 0.1;
    private static double distance_rate = 0.5;

    public static  double getPrice(double km, int min) {
        return (base_fare*(time_rate*min)*(distance_rate*km));
    }

    public static IFCMService getFCMService() {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

    public static IGoogleAPI getGoogleService() {
        return GoogleMapAPI.getClient(googleAPIUrl).create(IGoogleAPI.class);
    }

}
