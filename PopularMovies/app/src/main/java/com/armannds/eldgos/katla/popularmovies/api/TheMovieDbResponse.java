package com.armannds.eldgos.katla.popularmovies.api;

import com.armannds.eldgos.katla.popularmovies.data.Movie;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TheMovieDbResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<Movie> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private long totalResults;

    public TheMovieDbResponse(int page, List<Movie> results, int totalPages, long totalResults) {
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    public int getPage() {
        return page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalResults() {
        return totalResults;
    }
}
