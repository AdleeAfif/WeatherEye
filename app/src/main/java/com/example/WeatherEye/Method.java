package com.example.WeatherEye;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Method {

    @GET("1&2&4.json?api_key=1NZVJLNFHRAE5EP4&results=700&timezone=Asia/Kuala_Lumpur")
    Call<Model> getAllData();
}
