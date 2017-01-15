package software.level.udacity.popularmovies2;

import com.google.gson.annotations.SerializedName;

public class MovieEnvelope {
    @SerializedName("page")
    public Integer page;
//    @SerializedName("results")
//    @Expose
//    public List<Result> results = null;
    @SerializedName("total_results")
    public Integer totalResults;
    @SerializedName("total_pages")
    public Integer totalPages;
}
