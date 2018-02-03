package com.example.vanessa.a_etika.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Vanessa on 15/11/2017.
 */

public interface IGoogleAPI {

    @GET
    Call<String> getPath(@Url String url);

}
