package software.level.udacity.popularmovies2.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieReviewEnvelope {
    @SerializedName("id")
    public Integer id;

    @SerializedName("page")
    public Integer page;

    @SerializedName("results")
    public List<MovieReview> reviews = null;

    @SerializedName("total_results")
    public Integer totalResults;

    @SerializedName("total_pages")
    public Integer totalPages;

    public MovieReviewEnvelope(List<MovieReview> reviews) {
        this.reviews = reviews;
    }
}
