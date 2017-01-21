package software.level.udacity.popularmovies2.ui;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

import software.level.udacity.popularmovies2.api.model.Movie;

public class MovieGridPresenter extends Presenter<MovieGridActivity> {

    public static final String TAG = MovieGridPresenter.class.getSimpleName();

    // Identifiers for the types of movie requests we can perform
    private static final int REQUEST_POPULAR = 100;
    private static final int REQUEST_TOPRATED = 101;
    private static final int REQUEST_FAVORITE = 102;
    private static final int REQUEST_DEFAULT = REQUEST_POPULAR;

    // Currently selected movie request type
    private int selectedRequestType = REQUEST_DEFAULT;

    // Key for the API set in the constructor
    private String apiKey;

    // Movie data that is pushed to the RecyclerView
    private ArrayList<Movie> movies;

    /**
     * Constructor to create a new instance of the presenter.
     *
     * @param apiKey Required key to access to movie api
     */
    public MovieGridPresenter(@NonNull String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Update the type of movie data that should be displayed to the user.
     * 
     * @param requestType Type of movie data to request (REQUEST_POPULAR, REQUEST_TOPRATED, REQUEST_FAVORITE)
     */
    public void updateRequestType(int requestType) {
        if(requestType != selectedRequestType) {
            selectedRequestType = requestType;
            updateMovieData();
        }
    }

    public void updateMovieData() {
        Log.d(TAG, "updateMovieData: Selected request type -- " + selectedRequestType);

    }

}
