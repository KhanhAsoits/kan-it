package com.example.kan_it.api;

import com.example.kan_it.model.Chat;
import com.example.kan_it.model.ChatReq;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface IChatBot {
    @GET("chat")
    Call<Chat> sendChat(@Body ChatReq chatReq, @Header("Authorization") String auth);
}
