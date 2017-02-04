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
package com.armannds.eldgos.katla.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.armannds.eldgos.katla.popularmovies.data.Movie;
import com.armannds.eldgos.katla.popularmovies.utils.MovieConverterUtils;
import com.armannds.eldgos.katla.popularmovies.utils.TheMovieDBNetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private TextView mTestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTestText = (TextView) findViewById(R.id.tv_test);
        new FetchMoviesTask().execute(0);
    }

    private class FetchMoviesTask extends AsyncTask<Integer, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(Integer... params) {

            URL url = TheMovieDBNetworkUtils.buildPopularMoviesUrl();

            String simpleJsonStringResults = null;
            if (url != null) {
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    InputStream in = urlConnection.getInputStream();
                    Scanner scanner = new Scanner(in);
                    scanner.useDelimiter("\\A");

                    boolean hasInput = scanner.hasNext();
                    if (hasInput) {
                        simpleJsonStringResults = scanner.next();
                    } else {
                        simpleJsonStringResults = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
                return MovieConverterUtils.convertFromJsonString(simpleJsonStringResults);
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null && !movies.isEmpty()) {
                mTestText.setText(movies.get(0).getTitle());
            }
        }
    }
}
