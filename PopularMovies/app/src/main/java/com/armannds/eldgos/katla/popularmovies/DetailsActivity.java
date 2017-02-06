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
import android.widget.ImageView;
import android.widget.TextView;

import com.armannds.eldgos.katla.popularmovies.data.Movie;
import com.armannds.eldgos.katla.popularmovies.utils.TheMovieDBNetworkUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private ImageView mBackdropImage;
    private TextView mPlotSynopsis;
    private ImageView mPoster;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private TextView mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mBackdropImage = (ImageView) findViewById(R.id.iv_backdrop);
        mPlotSynopsis = (TextView) findViewById(R.id.tv_plot_synopsis);
        mPoster = (ImageView) findViewById(R.id.iv_poster);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mVoteAverage = (TextView) findViewById(R.id.tv_voteAverage);
        mTitle = (TextView) findViewById(R.id.tv_title);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Movie.EXTRA)) {
                Movie movieToDisplay = intent.getParcelableExtra(Movie.EXTRA);

                loadImages(movieToDisplay);
                setTitle(movieToDisplay.getTitle());
                mTitle.setText(movieToDisplay.getTitle());
                mPlotSynopsis.setText(movieToDisplay.getPlotSynopsis());
                mReleaseDate.setText(formatDate(movieToDisplay.getReleaseDate()));
                mVoteAverage.setText(getString(R.string.vote_average, movieToDisplay.getVoteAverage()));
            }
        }
    }

    private void loadImages(Movie movieToDisplay) {
        Context context = this;
        Picasso.with(context)
                .load(TheMovieDBNetworkUtils.buildMovieBackdropUrl(movieToDisplay))
                .into(mBackdropImage);

        Picasso.with(context)
                .load(TheMovieDBNetworkUtils.buildMoviePosterUrl(movieToDisplay))
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
}
