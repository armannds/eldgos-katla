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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Helper class to read from a URL and return an JSON string.
 */
public class UrlReader {

    private static final String TAG = UrlReader.class.getSimpleName();
    private static final String EMPTY_RESULT = null;

    private UrlReader() {
    }

    public static String readFromUrl(URL urlToReadFrom) {
        if (urlToReadFrom == null) {
            Log.e(TAG, "URL can't be null when passed to UrlReader.readFromUrl");
            return EMPTY_RESULT;
        }
        String jsonResults = null;
        HttpURLConnection urlConnection = openUrlConnection(urlToReadFrom);
        if (urlConnection != null) {
            jsonResults = getJsonResultsFromUrlConnection(urlConnection);
            closeUrlConnection(urlConnection);
        }
        return jsonResults;
    }

    private static HttpURLConnection openUrlConnection(URL url) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.e(TAG, "Could not open connection from url " + url + " due to " + e.getMessage());
        }
        return urlConnection;
    }

    private static String getJsonResultsFromUrlConnection(HttpURLConnection urlConnection) {
        String jsonResults = null;
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                jsonResults = scanner.next();
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not read Json string!");
            jsonResults = EMPTY_RESULT;
        }
        return jsonResults;
    }

    private static void closeUrlConnection(HttpURLConnection urlConnection) {
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
    }
}