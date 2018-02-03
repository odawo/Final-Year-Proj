package com.example.vanessa.p_etika.nearby_hospital;


import com.example.vanessa.p_etika.R;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Vanessa on 17/7/16.
 */
public interface RetrofitMaps {

    /*
     * Retrofit GET annotation with our URL
     * And our method that will return us details of hospitals.
     */
    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyD9B1xMj9btX9B7V2KD8JtACXlG7xJVy1Q")
    Call<Example> getNearbyPlaces(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);

}
