package com.kwakdevs.kwak123.mybakingapp.data.retrofit;

import android.content.Context;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Useful information taken from
 * <a href="https://stackoverflow.com/questions/23429046/can-retrofit-with-okhttp-use-cache-data-when-offline">here.</a>
 */
public class BakingClient {

    // Only need one URL here, only one call to make
    private static final String URL = "http://go.udacity.com/";
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context){
        if (retrofit != null) {
            return retrofit;
        }

        // TODO: Handling no permission to write to external storage?
        File cacheDir = context.getCacheDir();
        int cacheSize = 1024 * 1024; //1MB
        Cache cache = new Cache(cacheDir, cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}