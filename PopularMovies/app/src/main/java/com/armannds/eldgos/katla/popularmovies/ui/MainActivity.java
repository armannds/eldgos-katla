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
package com.armannds.eldgos.katla.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.armannds.eldgos.katla.popularmovies.PopularMoviesApplication;
import com.armannds.eldgos.katla.popularmovies.R;
import com.armannds.eldgos.katla.popularmovies.api.TheMovieDbResponse;
import com.armannds.eldgos.katla.popularmovies.api.TheMovieDbService;
import com.armannds.eldgos.katla.popularmovies.data.Movie;
import com.armannds.eldgos.katla.popularmovies.utils.MovieCollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MoviesAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MOVIE_LOADER = 17;

    @BindView(R.id.rv_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindString(R.string.movie_key)
    String mMovieKey;
    @BindString(R.string.title_key)
    String mTitleKey;

    private MoviesAdapter mAdapter;
    private int mMovieFilter;
    @Inject TheMovieDbService theMovieDbService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((PopularMoviesApplication) getApplication()).getNetworkComponent().inject(this);

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
        mMovieFilter = movieFilter;
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Movie>> movieLoader = loaderManager.getLoader(MOVIE_LOADER);
        if (movieLoader == null) {
            loaderManager.initLoader(MOVIE_LOADER, null, this);
        } else {
            loaderManager.restartLoader(MOVIE_LOADER, null, this);
        }
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

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> mMovies;

            @Override
            public void onStartLoading() {
                if (mMovies != null) {
                    deliverResult(mMovies);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(List<Movie> data) {
                mMovies = data;
                super.deliverResult(data);
            }

            @Override
            public List<Movie> loadInBackground() {
                Call<TheMovieDbResponse> theMovieDbResponse = null;
                if (mMovieFilter == R.string.top_rated) {
                    theMovieDbResponse = theMovieDbService.getTopRatedMovies();
                } else {
                    theMovieDbResponse = theMovieDbService.getPopularMovies();
                }

                final Object[] movies = new Object[1];
                if (theMovieDbResponse != null) {
                    try {
                        movies[0] = theMovieDbResponse.execute().body().getResults();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return (List<Movie>) movies[0];
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMoviesGrid();
            mAdapter.setMovies(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }
}
