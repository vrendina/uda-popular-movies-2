package software.level.udacity.popularmovies2.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieEnvelope {
    @SerializedName("page")
    public Integer page;

    @SerializedName("results")
    public List<Movie> movies = null;

    @SerializedName("total_results")
    public Integer totalResults;

    @SerializedName("total_pages")
    public Integer totalPages;
}
