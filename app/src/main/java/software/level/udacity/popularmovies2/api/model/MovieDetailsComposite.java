package software.level.udacity.popularmovies2.api.model;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsComposite {

    public ArrayList<MovieTrailer> trailers;
    public ArrayList<MovieReview> reviews;

    public boolean favorite;

    public MovieDetailsComposite(List<MovieTrailer> trailers, List<MovieReview> reviews, boolean favorite) {
        this.trailers = (ArrayList<MovieTrailer>) trailers;
        this.reviews = (ArrayList<MovieReview>) reviews;
        this.favorite = favorite;
    }

}
