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

package com.armannds.eldgos.katla.popularmovies.data;


/**
 * This class is a default representation of the Movie class. This is part of the The Null Object
 * design pattern and is used to be returned instead of a null value.
 */
public final class DefaultMovie extends Movie {

    private static final String ID = "default";
    private static final String TITLE = "";
    private static final String RELEASE_DATE = "";
    private static final String MOVIE_POSTER_PATH = "";
    private static final String VOTE_AVERAGE = "";
    private static final String PLOT_SYNOPSIS = "";
    private static final String BACKDROP_PATH = "";

    public DefaultMovie() {
        this(ID, TITLE, RELEASE_DATE, MOVIE_POSTER_PATH, VOTE_AVERAGE, PLOT_SYNOPSIS, BACKDROP_PATH);
    }

    private DefaultMovie(String id, String title, String releaseDate, String moviePosterPath, String voteAverage, String plotSynopsis, String backdropPath) {
        super(id, title, releaseDate, moviePosterPath, voteAverage, plotSynopsis, backdropPath);
    }
}
