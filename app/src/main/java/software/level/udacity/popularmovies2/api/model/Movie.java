package software.level.udacity.popularmovies2.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {

    public static final String TAG = Movie.class.getSimpleName();

    @SerializedName("id")
    public Integer id;

    @SerializedName("poster_path")
    public String posterPath;

    @SerializedName("overview")
    public String overview;

    @SerializedName("release_date")
    public String releaseDate;

    @SerializedName("title")
    public String title;

    @SerializedName("vote_average")
    public Double voteAverage;

    public byte[] poster;

    public Movie() {}

    protected Movie(Parcel in) {
        id = in.readInt();
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        title = in.readString();
        voteAverage = in.readDouble();
        poster = in.createByteArray();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public String toString() {
        return  "Title: "           + title +
                ", Release date: "  + releaseDate +
                ", Vote average: "  + voteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(title);
        dest.writeDouble(voteAverage);
        dest.writeByteArray(poster);
    }
}
