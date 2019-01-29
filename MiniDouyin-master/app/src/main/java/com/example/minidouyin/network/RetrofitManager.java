package com.example.minidouyin.network;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitManager {

    static Map<String, Retrofit> sHostMap2Retrofit = new HashMap<>();

    public static synchronized Retrofit get(String host) {
        Retrofit r = sHostMap2Retrofit.get(host);
        if (r == null) {
            // add more log
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            r = new Retrofit.Builder()
                    .baseUrl(host)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            sHostMap2Retrofit.put(host, r);
        }
        return r;
    }
}
