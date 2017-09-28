package com.kwakdevs.kwak123.mybakingapp;

import android.app.Application;

import com.kwakdevs.kwak123.mybakingapp.data.repository.BakingRepository;
import com.kwakdevs.kwak123.mybakingapp.data.repository.BakingRepositoryImpl;
import com.squareup.leakcanary.LeakCanary;

public class MyBakingApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) return;
        LeakCanary.install(this);
    }
}
