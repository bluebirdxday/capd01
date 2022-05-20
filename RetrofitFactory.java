package com.example.capd.CoordinatesCalculate;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// retrofit 객체 생성

public class RetrofitFactory {

    private static RetrofitFactory INSTANCE;
    private static final String BASE_URL = "https://dapi.kakao.com";

    public static RetrofitFactory getRetrofit(){
        if (INSTANCE == null){
            INSTANCE = new RetrofitFactory();
        }
        return INSTANCE;
    }

    private RetrofitFactory(){
    }

    public GetCoordinatesService getCoordinatesService(){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        return retrofit.create(GetCoordinatesService.class);
    }
}
