package com.armannds.eldgos.katla.popularmovies.api;

import com.armannds.eldgos.katla.popularmovies.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {

    private static final String API_KEY_PARAM = "api_key";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        HttpUrl newHttpUrl = request
                .url()
                .newBuilder()
                .setQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVE_DB_API_KEY)
                .build();

        Request newRequest = request
                .newBuilder()
                .url(newHttpUrl)
                .build();

        return chain.proceed(newRequest);
    }
}
