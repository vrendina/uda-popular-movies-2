package software.level.udacity.popularmovies2.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import software.level.udacity.popularmovies2.api.model.MovieDetails;
import software.level.udacity.popularmovies2.api.model.MovieEnvelope;
import software.level.udacity.popularmovies2.api.model.MovieReviewEnvelope;
import software.level.udacity.popularmovies2.api.model.MovieTrailerEnvelope;

public interface MovieService {
    @GET("popular")
    Observable<MovieEnvelope> getPopularMovies(@Query("api_key") String apiKey);

    @GET("top_rated")
    Observable<MovieEnvelope> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("{id}")
    Observable<MovieDetails> getMovieDetails(@Query("api_key") String apiKey);

    @GET("{id}/reviews")
    Observable<MovieReviewEnvelope> getReviews(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET("{id}/videos")
    Observable<MovieTrailerEnvelope> getTrailers(@Path("id") int movieId, @Query("api_key") String apiKey);
}