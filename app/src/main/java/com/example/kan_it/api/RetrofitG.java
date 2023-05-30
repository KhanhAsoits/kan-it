package com.example.kan_it.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitG {
    private final String BASE_URL = "http://192.168.1.104:5000/";
    private static RetrofitG I;
    Retrofit retrofit;

    public static RetrofitG gI() {
        if (I == null) {
            I = new RetrofitG();
        }
        return I;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public RetrofitG() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
