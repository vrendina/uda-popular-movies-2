package software.level.udacity.popularmovies2.api.model;

import com.google.gson.annotations.SerializedName;

public class MovieReview {
    @SerializedName("id")
    public String id;

    @SerializedName("author")
    public String author;

    @SerializedName("content")
    public String content;

    @SerializedName("url")
    public String url;

    @Override
    public String toString() {
        return url;
    }
}
