package software.level.udacity.popularmovies2.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsComposite implements Parcelable {

    public ArrayList<MovieTrailer> trailers;
    public ArrayList<MovieReview> reviews;

    public MovieDetailsComposite(List<MovieTrailer> trailers, List<MovieReview> reviews) {
        this.trailers = (ArrayList<MovieTrailer>) trailers;
        this.reviews = (ArrayList<MovieReview>) reviews;
    }

    protected MovieDetailsComposite(Parcel in) {
        trailers = in.createTypedArrayList(MovieTrailer.CREATOR);
        reviews = in.createTypedArrayList(MovieReview.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(trailers);
        dest.writeTypedList(reviews);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieDetailsComposite> CREATOR = new Creator<MovieDetailsComposite>() {
        @Override
        public MovieDetailsComposite createFromParcel(Parcel in) {
            return new MovieDetailsComposite(in);
        }

        @Override
        public MovieDetailsComposite[] newArray(int size) {
            return new MovieDetailsComposite[size];
        }
    };
}
