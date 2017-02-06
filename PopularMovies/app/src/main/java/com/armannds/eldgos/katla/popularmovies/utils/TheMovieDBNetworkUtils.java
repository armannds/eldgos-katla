/*
 * Copyright 2017 Armann David Sigurdsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.armannds.eldgos.katla.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.armannds.eldgos.katla.popularmovies.BuildConfig;
import com.armannds.eldgos.katla.popularmovies.data.Movie;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is responsible for creating the connection to The MovieDB API
 */
public class TheMovieDBNetworkUtils {

    private static final String TAG = TheMovieDBNetworkUtils.class.getSimpleName();
    private static final String HTTPS_SHEME = "https";
    private static final String MOVIE_BASE_URL = "api.themoviedb.org";
    private static final String VERSION_PATH = "3";
    private static final String MOVIE_PATH = "movie";
    private static final String POPULAR_PATH = "popular";
    private static final String TOP_RATED_PATH = "top_rated";
    private static final String API_KEY_PARAM = "api_key";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185";
    private static final String BACKDROP_SIZE = "w342";

    private TheMovieDBNetworkUtils() {}

    /**
     * Creates a URL to retrieve popular movies from The MovieDB
     * @return URL for popular movies
     */
    public static URL buildPopularMoviesUrl() {
        return buildTheMovieDBUrl(POPULAR_PATH);
    }

    /**
     * Creates a URL to retrieve top rated movies from The MovieDB
     * @return URL for top rated movies
     */
    public static URL buildTopRatedMoviesUrl() {
        return buildTheMovieDBUrl(TOP_RATED_PATH);
    }

    private static URL buildTheMovieDBUrl(String sortByPath) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(HTTPS_SHEME)
                .authority(MOVIE_BASE_URL)
                .appendPath(VERSION_PATH)
                .appendPath(MOVIE_PATH)
                .appendPath(sortByPath)
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVE_DB_API_KEY);

        URL theMovieDbUrl = null;
        try {
            theMovieDbUrl = new URL(builder.toString());
            Log.d(TAG, "The MovieDB URL " + theMovieDbUrl.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Could not create URL for " + sortByPath + " movies!");
        }
        return theMovieDbUrl;
    }

    public static String buildMoviePosterUrl(Movie movie) {
        return IMAGE_BASE_URL + POSTER_SIZE + movie.getPosterPath();
    }

    public static String buildMovieBackdropUrl(Movie movie) {
        return IMAGE_BASE_URL + BACKDROP_SIZE + movie.getBackdropPath();
    }
}
