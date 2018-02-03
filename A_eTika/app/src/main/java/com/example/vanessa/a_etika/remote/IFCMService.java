package com.example.vanessa.a_etika.remote;

import com.example.vanessa.a_etika.model.FCMResponse;
import com.example.vanessa.a_etika.model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Vanessa on 20/11/2017.
 */

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAqf-qpEQ:APA91bFlHAvYpndI9Nbp8w8-aRRkuLBQXjjH3gxMNrItOuLgBAh3LTydXJ_aduDEaSJQ5V_WGy5L0CP2qAX46EAWAletvt7exKjfZhNisDmGzxK-EoLeu2IvlN77IS9xIQYgmPHD27fm" //server key obtained from firebase - firebase cloud messenger : paste removing <SERVER KEY>
    })

    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);

}
