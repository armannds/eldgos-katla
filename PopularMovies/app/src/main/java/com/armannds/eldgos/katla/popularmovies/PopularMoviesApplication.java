package com.armannds.eldgos.katla.popularmovies;

import android.app.Application;

import com.armannds.eldgos.katla.popularmovies.api.NetworkModule;

public class PopularMoviesApplication extends Application {

    private NetworkComponent mNetworkComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mNetworkComponent = DaggerNetworkComponent
                .builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public NetworkComponent getNetworkComponent() {
        return mNetworkComponent;
    }
}
