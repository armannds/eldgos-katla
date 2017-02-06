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

import android.util.Log;

import com.armannds.eldgos.katla.popularmovies.data.DefaultMovie;
import com.armannds.eldgos.katla.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This helper class converts Movie between different representations.
 */
public class MovieConverterUtils {

    private static final String TAG = MovieConverterUtils.class.getSimpleName();
    private static final String JSON_ARRAY = "results";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String PLOT_SYNOPSIS = "overview";
    private static final String BACKDROP_PATH = "backdrop_path";

    private MovieConverterUtils() {}

    /**
     * Converts a Json result string into a List<Movie>, returns an empty collection if something
     * went wrong during conversion.
     * @param json string containing the results array
     * @return list of Movie
     */
    public static List<Movie> convertFromJson(String json) {
        try {
            JSONObject movieJson = new JSONObject(json);
            return convertJsonArray(movieJson.getJSONArray(JSON_ARRAY));
        } catch (JSONException e) {
            Log.d(TAG, "Failed to convert movies from JSON response! Due to " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private static List<Movie> convertJsonArray(JSONArray jsonMovieArray) throws JSONException {
        List<Movie> convertedResults = new ArrayList<>();
        if (jsonMovieArray != null) {
            for (int i = 0; i < jsonMovieArray.length(); i++) {
                JSONObject jsonMovie = jsonMovieArray.getJSONObject(i);
                Movie convertedMovie = convertJsonMovie(jsonMovie);
                if (isNotDefaultMovie(convertedMovie)) {
                    convertedResults.add(convertedMovie);
                }
            }
        }
        return convertedResults;
    }

    private static Movie convertJsonMovie(JSONObject jsonMovie) throws JSONException {
        if (jsonMovie == null) {
            return new DefaultMovie();
        }
        return new Movie(
                jsonMovie.getString(ID),
                jsonMovie.getString(TITLE),
                jsonMovie.getString(RELEASE_DATE),
                jsonMovie.getString(POSTER_PATH),
                jsonMovie.getString(VOTE_AVERAGE),
                jsonMovie.getString(PLOT_SYNOPSIS),
                jsonMovie.getString(BACKDROP_PATH));
    }

    private static boolean isNotDefaultMovie(Movie movie) {
        return !(movie instanceof DefaultMovie);
    }
}