package com.example.vanessa.p_etika.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Vanessa on 28/11/2017.
 */

public interface IGoogleAPI {
    @GET
    Call<String> getPath(@Url String url);
}
