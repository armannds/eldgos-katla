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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        loadMovies(R.string.popular);
    }

    private void loadMovies(int movieFilter) {
        new FetchMoviesTask().execute(movieFilter);
        setTitle(movieFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_popular) {
            loadMovies(R.string.popular);
            return true;
        }
        if (id == R.id.action_top_rated) {
            loadMovies(R.string.top_rated);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class FetchMoviesTask extends AsyncTask<Integer, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }
            int movieFilter = params[0];
            return getMovies(movieFilter);
        }

        @Nullable
        private List<Movie> getMovies(int movieFilter) {
            //TODO wrap these commands into a service and create non static methods using dependency injection to increase testability.
            URL url = getMovieUrl(movieFilter);
            String jsonResults = UrlReader.readFromUrl(url);
            if (JsonStringUtils.isNotEmpty(jsonResults)) {
                return MovieConverterUtils.convertFromJson(jsonResults);
            } else {
                return null;
            }
        }

        private URL getMovieUrl(int movieFilter) {
            if (movieFilter == R.string.top_rated) {
                return TheMovieDBNetworkUtils.buildTopRatedMoviesUrl();
            } else {
                return TheMovieDBNetworkUtils.buildPopularMoviesUrl();
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
