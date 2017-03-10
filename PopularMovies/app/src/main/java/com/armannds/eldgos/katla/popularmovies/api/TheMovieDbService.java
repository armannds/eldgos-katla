package com.armannds.eldgos.katla.popularmovies.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TheMovieDbService {

    @GET("movie/popular")
    Call<TheMovieDbResponse> getPopularMovies();

    @GET("movie/top_rated")
    Call<TheMovieDbResponse> getTopRatedMovies();

    @GET("movie/{movie_id}/videos")
    Call<TheMovieDBTrailerResponse> getMovieTrailers(@Path("movie_id") long movieId);
}
