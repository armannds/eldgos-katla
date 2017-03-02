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

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.armannds.eldgos.katla.popularmovies.data.Movie;
import com.armannds.eldgos.katla.popularmovies.utils.MovieCollectionUtils;
import com.armannds.eldgos.katla.popularmovies.utils.MovieConverterUtils;
import com.armannds.eldgos.katla.popularmovies.utils.TheMovieDBNetworkUtils;
import com.armannds.eldgos.katla.popularmovies.utils.UrlReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    @BindString(R.string.movie_key) String mMovieKey;
    @BindString(R.string.title_key) String mTitleKey;
    @BindView(R.id.rv_movies) RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    private MoviesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Context context = this;
        MoviesAdapter.MoviesAdapterOnClickHandler clickHandler = this;
        mAdapter = new MoviesAdapter(context, clickHandler);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, calculateNoOfColumns()));
        mRecyclerView.setHasFixedSize(true);

        if (isSavedInstanceStateValid(savedInstanceState)) {
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList(mMovieKey);
            mAdapter.setMovies(movies);
            String title = savedInstanceState.getString(mTitleKey);
            setTitle(title);
        } else {
            if (isOnline()) {
                loadMovies(R.string.popular);
            } else {
                showErrorMessage();
            }
        }
    }

    private int calculateNoOfColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

    private boolean isSavedInstanceStateValid(Bundle savedInstanceState) {
        return savedInstanceState != null && savedInstanceState.containsKey(mMovieKey)
                && savedInstanceState.containsKey(mTitleKey);
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (MovieCollectionUtils.isNotEmpty(mAdapter.getmMovies())) {
            ArrayList<Movie> movies = new ArrayList<>(mAdapter.getmMovies());
            outState.putParcelableArrayList(mMovieKey, movies);
        }
        outState.putString(mTitleKey, getTitle().toString());
    }

    private void loadMovies(int movieFilter) {
        showMoviesGrid();
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

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailsActivity.class;
        Intent intentToStartDetailsActivity = new Intent(context, destinationClass);
        intentToStartDetailsActivity.putExtra(Movie.EXTRA, movie);
        startActivity(intentToStartDetailsActivity);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMoviesGrid() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }


    class FetchMoviesTask extends AsyncTask<Integer, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

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
            if (!TextUtils.isEmpty(jsonResults)) {
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
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (MovieCollectionUtils.isNotEmpty(movies)) {
                showMoviesGrid();
                mAdapter.setMovies(movies);
            } else {
                showErrorMessage();
            }
        }
    }
}
