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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.armannds.eldgos.katla.popularmovies.data.Movie;
import com.armannds.eldgos.katla.popularmovies.utils.TheMovieDBNetworkUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    @BindView(R.id.iv_backdrop) ImageView mBackdropImage;
    @BindView(R.id.tv_plot_synopsis) TextView mPlotSynopsis;
    @BindView(R.id.iv_poster) ImageView mPoster;
    @BindView(R.id.tv_release_date) TextView mReleaseDate;
    @BindView(R.id.tv_voteAverage) TextView mVoteAverage;
    @BindView(R.id.tv_title) TextView mTitle;

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Movie.EXTRA)) {
                mMovie = intent.getParcelableExtra(Movie.EXTRA);
                loadImages();
                setTitle(mMovie.getTitle());
                mTitle.setText(mMovie.getTitle());
                mPlotSynopsis.setText(mMovie.getPlotSynopsis());
                mReleaseDate.setText(formatDate(mMovie.getReleaseDate()));
                mVoteAverage.setText(getString(R.string.vote_average, mMovie.getVoteAverage()));
            }
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}