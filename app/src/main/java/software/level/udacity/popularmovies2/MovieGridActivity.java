package software.level.udacity.popularmovies2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MovieGridActivity extends AppCompatActivity {

    public static final String TAG = MovieGridActivity.class.getSimpleName();

    // Constants to identify the type of movie request based on what menu item is selected
    private static final String REQUEST_TYPE_KEY = "movieRequestType";

    private static final int REQUEST_POPULAR = 100;
    private static final int REQUEST_TOPRATED = 101;
    private static final int REQUEST_FAVORITE = 102;
    private static final int REQUEST_DEFAULT = REQUEST_POPULAR;

    // Holds the currently selected movie request type
    private int selectedRequestType = REQUEST_DEFAULT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);

//        String apiKey = getResources().getString(R.string.API_KEY);
////        Call<MovieEnvelope> envelope = service.topRated(apiKey);
//
//        Call<MovieTrailerEnvelope> envelope = MovieServiceManager.getService().getTrailers(278, apiKey);
//
//
//
//        envelope.enqueue(new Callback<MovieTrailerEnvelope>() {
//
//            @Override
//            public void onResponse(Call<MovieTrailerEnvelope> call, Response<MovieTrailerEnvelope> response) {
//                Log.d(TAG, "onResponse: " + response.body().trailers.toString());
//            }
//
//            @Override
//            public void onFailure(Call<MovieTrailerEnvelope> call, Throwable t) {
//
//            }
//        });

        // If we have a saved request type, restore the state otherwise select the default
        if(savedInstanceState != null) {
            selectedRequestType = savedInstanceState.getInt(REQUEST_TYPE_KEY, REQUEST_DEFAULT);
        } else {
            selectedRequestType = REQUEST_DEFAULT;
        }

    }

    /**
     * Saves the save of the activity. Adds an integer to hold the currently selected movie
     * request type.
     *
     * @param outState The bundle that will be returned to the activity when restored
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(REQUEST_TYPE_KEY, selectedRequestType);
        super.onSaveInstanceState(outState);
    }

    /**
     * Inflates the menu resource for this activity.
     *
     * @param menu Interface that manages the menu
     * @return True if the menu is to be displayed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_grid, menu);
        updateSelectedMenuItem(menu);

        return true;
    }

    /**
     * Updates which menu item is visibly checked
     */
    private void updateSelectedMenuItem(Menu menu) {
        switch(selectedRequestType) {
            case REQUEST_FAVORITE:
                menu.findItem(R.id.action_favorite).setChecked(true);
                break;
            case REQUEST_POPULAR:
                menu.findItem(R.id.action_popular).setChecked(true);
                break;
            case REQUEST_TOPRATED:
                menu.findItem(R.id.action_toprated).setChecked(true);
                break;
        }
    }

    /**
     * Handles selection of items in the menu.
     *
     * @param item The menu item that was selected
     * @return Return false to allow normal menu processing, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If the item is already selected don't do anything
        if(item.isChecked()) return false;

        item.setChecked(true);

        int id = item.getItemId();
        switch(id) {
            case R.id.action_favorite:
                fetchMovieData(REQUEST_FAVORITE);
                return true;

            case R.id.action_popular:
                fetchMovieData(REQUEST_POPULAR);
                return true;

            case R.id.action_toprated:
                fetchMovieData(REQUEST_TOPRATED);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Kicks off the loading of movie data
     *
     * @param requestType Constant to identify the type of movie request
     */
    private void fetchMovieData(int requestType) {
        selectedRequestType = requestType;
        Log.d(TAG, "fetchMovieData: Movie request type -- " + requestType);
    }
}
