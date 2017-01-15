package software.level.udacity.popularmovies2.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movie {
    @SerializedName("poster_path")
    public String posterPath;

    @SerializedName("adult")
    public Boolean adult;

    @SerializedName("overview")
    public String overview;

    @SerializedName("release_date")
    public String releaseDate;

    @SerializedName("genre_ids")
    public List<Integer> genreIds = null;

    @SerializedName("id")
    public Integer id;

    @SerializedName("original_title")
    public String originalTitle;

    @SerializedName("original_language")
    public String originalLanguage;

    @SerializedName("title")
    public String title;

    @SerializedName("backdrop_path")
    public String backdropPath;

    @SerializedName("popularity")
    public Double popularity;

    @SerializedName("vote_count")
    public Integer voteCount;

    @SerializedName("video")
    public Boolean video;

    @SerializedName("vote_average")
    public Double voteAverage;

    @Override
    public String toString() {
        return title;
    }
}
