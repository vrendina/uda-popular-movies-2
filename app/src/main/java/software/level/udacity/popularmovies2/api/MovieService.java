package software.level.udacity.popularmovies2.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import software.level.udacity.popularmovies2.api.model.MovieEnvelope;
import software.level.udacity.popularmovies2.api.model.MovieReviewEnvelope;
import software.level.udacity.popularmovies2.api.model.MovieTrailerEnvelope;

public interface MovieService {
    @GET("popular")
    Call<MovieEnvelope> popular(@Query("api_key") String apiKey);

    @GET("top_rated")
    Call<MovieEnvelope> topRated(@Query("api_key") String apiKey);

    @GET("{id}/reviews")
    Call<MovieReviewEnvelope> reviews(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET("{id}/videos")
    Call<MovieTrailerEnvelope> trailers(@Path("id") int movieId, @Query("api_key") String apiKey);
}