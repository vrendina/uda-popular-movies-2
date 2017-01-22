package software.level.udacity.popularmovies2.api.model;

/**
 * Composite of all movie details. Object holds the movie details, reviews,
 * and trailers.
 */
public class MovieDetailsComposite {
    public MovieDetails details;
    public MovieReviewEnvelope reviewEnvelope;
    public MovieTrailerEnvelope trailerEnvelope;

    public MovieDetailsComposite(MovieDetails details, MovieReviewEnvelope reviewEnvelope, MovieTrailerEnvelope trailerEnvelope) {
        this.details = details;
        this.reviewEnvelope = reviewEnvelope;
        this.trailerEnvelope = trailerEnvelope;
    }
}
