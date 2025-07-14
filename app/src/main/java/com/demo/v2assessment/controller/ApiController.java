package com.demo.v2assessment.controller;

import com.demo.v2assessment.api.MyApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiController {

    private static final String baseUrl="https://api.jsonbin.io/";
    private static ApiController clientObj;
    private static Retrofit retrofit;
    ApiController(){
        retrofit=new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static synchronized ApiController getInstance(){
        if(clientObj ==null){
            clientObj =new ApiController();
        }
        return clientObj;
    }
    public MyApi getApi(){
        return retrofit.create(MyApi.class);
    }
}
