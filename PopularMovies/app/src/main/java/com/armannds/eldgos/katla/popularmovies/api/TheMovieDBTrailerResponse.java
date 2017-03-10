package com.armannds.eldgos.katla.popularmovies.api;

import com.armannds.eldgos.katla.popularmovies.data.Trailer;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TheMovieDBTrailerResponse {
    @SerializedName("id")
    private long movieId;

    @SerializedName("results")
    private ArrayList<Trailer> results;

    public TheMovieDBTrailerResponse(long movieId, ArrayList<Trailer> results) {
        this.movieId = movieId;
        this.results = results;
    }

    public long getMovieId() {
        return movieId;
    }

    public ArrayList<Trailer> getResults() {
        return results;
    }
}
