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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.armannds.eldgos.katla.popularmovies.data.Movie;
import com.armannds.eldgos.katla.popularmovies.utils.JsonStringUtils;
import com.armannds.eldgos.katla.popularmovies.utils.MovieCollectionUtils;
import com.armannds.eldgos.katla.popularmovies.utils.MovieConverterUtils;
import com.armannds.eldgos.katla.popularmovies.utils.TheMovieDBNetworkUtils;
import com.armannds.eldgos.katla.popularmovies.utils.UrlReader;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTestText = (TextView) findViewById(R.id.tv_test);
        new FetchMoviesTask().execute(0);
    }

    class FetchMoviesTask extends AsyncTask<Integer, Void, List<Movie>> {

        private final String TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(Integer... params) {
            //TODO wrap these commands into a service and create non static methods using dependency injection to increase testability.
            URL url = TheMovieDBNetworkUtils.buildPopularMoviesUrl();
            String jsonResults = UrlReader.readFromUrl(url);
            if (JsonStringUtils.isNotEmpty(jsonResults)) {
                return MovieConverterUtils.convertFromJson(jsonResults);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (MovieCollectionUtils.isNotEmpty(movies)) {
                mTestText.setText(movies.get(0).getTitle());
            }
        }
    }
}
