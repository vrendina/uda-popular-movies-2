package software.level.udacity.popularmovies2.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieEnvelope {

    public static final int TYPE_POPULAR = 100;
    public static final int TYPE_TOPRATED = 101;
    public static final int TYPE_FAVORITE = 102;

    @SerializedName("page")
    public Integer page;

    @SerializedName("results")
    public List<Movie> movies = null;

    @SerializedName("total_results")
    public Integer totalResults;

    @SerializedName("total_pages")
    public Integer totalPages;

    public int resultType;

    public MovieEnvelope(List<Movie> movies, int resultType) {
        this.movies = movies;
        this.resultType = resultType;
    }
}
