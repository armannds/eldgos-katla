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

import com.armannds.eldgos.katla.popularmovies.data.Movie;

/**
 * This class is responsible for creating the connection to The MovieDB API
 */
public class TheMovieDBNetworkUtils {

    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w185";
    private static final String BACKDROP_SIZE = "w342";

    private TheMovieDBNetworkUtils() {}

    public static String buildMoviePosterUrl(Movie movie) {
        return IMAGE_BASE_URL + POSTER_SIZE + movie.getPosterPath();
    }

    public static String buildMovieBackdropUrl(Movie movie) {
        return IMAGE_BASE_URL + BACKDROP_SIZE + movie.getBackdropPath();
    }
}
