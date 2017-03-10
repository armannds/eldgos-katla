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

package com.armannds.eldgos.katla.popularmovies.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armannds.eldgos.katla.popularmovies.PopularMoviesApplication;
import com.armannds.eldgos.katla.popularmovies.R;
import com.armannds.eldgos.katla.popularmovies.api.TheMovieDBTrailerResponse;
import com.armannds.eldgos.katla.popularmovies.api.TheMovieDbService;
import com.armannds.eldgos.katla.popularmovies.data.Movie;
import com.armannds.eldgos.katla.popularmovies.data.Trailer;
import com.armannds.eldgos.katla.popularmovies.ui.OnItemClickListener;
import com.armannds.eldgos.katla.popularmovies.utils.CollectionUtils;
import com.armannds.eldgos.katla.popularmovies.utils.TheMovieDBNetworkUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity implements OnItemClickListener<Trailer> {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    @BindString(R.string.movie_key)
    static String sMovieKey;
    @BindString(R.string.trailers_key)
    static String mTrailersKey;
    @BindView(R.id.iv_backdrop)
    ImageView mBackdropImage;
    @BindView(R.id.tv_plot_synopsis)
    TextView mPlotSynopsis;
    @BindView(R.id.iv_poster)
    ImageView mPoster;
    @BindView(R.id.tv_release_date)
    TextView mReleaseDate;
    @BindView(R.id.tv_voteAverage)
    TextView mVoteAverage;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.rv_movie_trailers)
    RecyclerView mMovieTrailers;
    @BindView(R.id.trailer_layout)
    LinearLayout mTrailerLayout;

    @Inject
    TheMovieDbService theMovieDbService;

    private Movie mMovie;
    private TrailersAdapter mTrailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        ((PopularMoviesApplication) getApplication()).getNetworkComponent().inject(this);

        if (isSaveInstanceStateValid(savedInstanceState)) {


        } else {
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(Movie.EXTRA)) {
                    mMovie = Parcels.unwrap(intent.getParcelableExtra(Movie.EXTRA));
                    loadImages();
                    initViews();
                    loadTrailers();
                }
            }
        }
    }

    private boolean isSaveInstanceStateValid(Bundle saveInstanceState) {
        return saveInstanceState != null
                && conainsMovie(saveInstanceState)
                && containsTrailers(saveInstanceState);
    }

    private boolean conainsMovie(Bundle saveInstanceState) {
        return saveInstanceState.containsKey(sMovieKey)
                && saveInstanceState.getParcelable(sMovieKey) != null;
    }

    private boolean containsTrailers(Bundle saveInstanceState) {
        return saveInstanceState.containsKey(mTrailersKey)
                && saveInstanceState.getParcelable(mTrailersKey) != null;
    }

    private void loadImages() {
        Context context = this;
        Picasso.with(context)
                .load(TheMovieDBNetworkUtils.buildMovieBackdropUrl(mMovie))
                .into(mBackdropImage);

        Picasso.with(context)
                .load(TheMovieDBNetworkUtils.buildMoviePosterUrl(mMovie))
                .into(mPoster);
    }

    private void initViews() {
        setTitle(mMovie.getTitle());
        mTitle.setText(mMovie.getTitle());
        mPlotSynopsis.setText(mMovie.getPlotSynopsis());
        mReleaseDate.setText(formatDate(mMovie.getReleaseDate()));
        mVoteAverage.setText(getString(R.string.vote_average, mMovie.getVoteAverage()));
        mTrailerAdapter = new TrailersAdapter(this, this);
        mMovieTrailers.setLayoutManager(new GridLayoutManager(this, 2));
        mMovieTrailers.setAdapter(mTrailerAdapter);
        mMovieTrailers.setHasFixedSize(true);
    }

    private String formatDate(String date) {
        SimpleDateFormat jsonReleaseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date releaseDate = jsonReleaseDate.parse(date);
            return new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(releaseDate);
        } catch (ParseException e) {
            Log.v(TAG, "Could not parse date " + date);
            return date;
        }
    }

    private void loadTrailers() {
        Call<TheMovieDBTrailerResponse> trailerResponse = theMovieDbService.getMovieTrailers(mMovie.getId());
        trailerResponse.enqueue(new Callback<TheMovieDBTrailerResponse>() {
            @Override
            public void onResponse(Call<TheMovieDBTrailerResponse> call, Response<TheMovieDBTrailerResponse> response) {
                List<Trailer> trailers = response.body().getResults();
                mTrailerAdapter.setTrailers(trailers);
                mTrailerLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<TheMovieDBTrailerResponse> call, Throwable t) {
                Log.i(TAG, "No trailers exist for movie with id " + mMovie.getId());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(View view, Trailer trailer) {
        if (trailer != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (CollectionUtils.isNotEmpty(mTrailerAdapter.getTrailers())) {
            outState.putParcelable(mTrailersKey, Parcels.wrap(mTrailerAdapter.getTrailers()));
        }
        if (mMovie != null) {
            outState.putParcelable(sMovieKey, Parcels.wrap(mMovie));
        }
    }
}