package com.example.getkracking.API;

import android.content.Context;
import android.os.Build;

import com.example.getkracking.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.GsonBuildConfig;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final int CONNECT_TIMEOUT = 60;
    private static final int WRITE_TIMEOUT = 60;
    private static final int READ_TIMEOUT = 60;

    private static final String BASE_URL = Config.BASE_URL;

    private ApiClient(){

    }

    public static <S> S create(Context context, Class<S> service ) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor().
            setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE );

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new AuthInterceptor(context))
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(CONNECT_TIMEOUT , TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT , TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT , TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class , new ApiDateTypeAdapater())
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();

        return retrofit.create(service);
    }
}
