package software.level.udacity.popularmovies2.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieTrailerEnvelope {
    @SerializedName("id")
    public Integer id;

    @SerializedName("results")
    public List<MovieTrailer> trailers = null;

    public MovieTrailerEnvelope(List<MovieTrailer> trailers) {
        this.trailers = trailers;
    }
}
