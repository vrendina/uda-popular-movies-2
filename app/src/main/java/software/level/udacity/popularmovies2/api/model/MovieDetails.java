package software.level.udacity.popularmovies2.api.model;

import com.google.gson.annotations.SerializedName;

public class MovieDetails {
    
    @SerializedName("adult")
    public Boolean adult;

    @SerializedName("backdrop_path")
    public String backdropPath;

    @SerializedName("belongs_to_collection")
    public Object belongsToCollection;

    @SerializedName("budget")
    public Integer budget;

    @SerializedName("homepage")
    public String homepage;

    @SerializedName("id")
    public Integer id;

    @SerializedName("imdb_id")
    public String imdbId;

    @SerializedName("original_language")
    public String originalLanguage;

    @SerializedName("original_title")
    public String originalTitle;

    @SerializedName("overview")
    public String overview;

    @SerializedName("popularity")
    public Double popularity;

    @SerializedName("poster_path")
    public String posterPath;

    @SerializedName("release_date")
    public String releaseDate;

    @SerializedName("revenue")
    public Integer revenue;

    @SerializedName("runtime")
    public Integer runtime;

    @SerializedName("status")
    public String status;

    @SerializedName("tagline")
    public String tagline;

    @SerializedName("title")
    public String title;

    @SerializedName("video")
    public Boolean video;

    @SerializedName("vote_average")
    public Double voteAverage;

    @SerializedName("vote_count")
    public Integer voteCount;

    // If the movie is added to the list of favorites this will be true
    public boolean favorite = false;

    // Holds the byte array that contains to the movie encodedPoster image
    public byte[] encodedPoster;

    @Override
    public String toString() {
        return title;
    }
}
