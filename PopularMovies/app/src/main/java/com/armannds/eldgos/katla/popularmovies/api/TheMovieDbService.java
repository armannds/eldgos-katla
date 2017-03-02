package com.armannds.eldgos.katla.popularmovies.api;

import com.armannds.eldgos.katla.popularmovies.data.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TheMovieDbService {

    @GET("movie/popular")
    Call<TheMovieDbResponse> getPopularMovies();

    @GET("movie/top_rated")
    Call<TheMovieDbResponse> getTopRatedMovies();
}
