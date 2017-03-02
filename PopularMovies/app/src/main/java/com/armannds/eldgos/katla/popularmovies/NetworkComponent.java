package com.armannds.eldgos.katla.popularmovies;

import com.armannds.eldgos.katla.popularmovies.api.NetworkModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface NetworkComponent {
    void inject(MainActivity mainActivity);
    void inject(DetailsActivity detailsActivity);
}
