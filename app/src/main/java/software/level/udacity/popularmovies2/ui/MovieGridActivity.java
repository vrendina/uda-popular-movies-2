package software.level.udacity.popularmovies2.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import software.level.udacity.popularmovies2.R;
import software.level.udacity.popularmovies2.api.MovieServiceManager;
import software.level.udacity.popularmovies2.api.model.Movie;
import software.level.udacity.popularmovies2.api.model.MovieEnvelope;

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

    // View binding from Butterknife
    @BindView(R.id.rv_movie_grid) RecyclerView recyclerView;
    @BindView(R.id.pb_movies_loading) ProgressBar progressBar;

    // Adapter used for managing views in the RecyclerView
    MovieGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);

        ButterKnife.bind(this);

        // If we have a saved request type, restore the state otherwise select the default
        if(savedInstanceState != null) {
            selectedRequestType = savedInstanceState.getInt(REQUEST_TYPE_KEY, REQUEST_DEFAULT);
        } else {
            selectedRequestType = REQUEST_DEFAULT;
        }

        configureRecyclerView();


        String apiKey = getResources().getString(R.string.API_KEY);
        Observable<MovieEnvelope> observable = MovieServiceManager.getService().getPopularMovies(apiKey);

        DisposableObserver<MovieEnvelope> observer = new DisposableObserver<MovieEnvelope>() {
            @Override
            public void onNext(MovieEnvelope movieEnvelope) {
                Log.d(TAG, "onNext: ");

                ArrayList<Movie> data = (ArrayList<Movie>) movieEnvelope.movies;
                adapter.setMovieData(data);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        };

        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(observer);

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observer);

    }

    /**
     * Saves the state of the activity. Adds an integer to hold the currently selected movie
     * request type to the bundle that is will obtained when restoring the activity.
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
     * Updates which menu item is visibly checked.
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
     * Performs the initial configuration for the RecyclerView. Configures the RecyclerView
     * to use a GridLayoutManager and binds the adapter class.
     */
    private void configureRecyclerView() {
        // Depending on the screen orientation we can show a different number of columns
        //int columns = getResources().getInteger(R.integer.movie_columns);

        // Initialize the layout manager and set the RecyclerView to use it
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // All the movie posters will be the same size
        recyclerView.setHasFixedSize(true);

        // Create the adapter and set it
        adapter = new MovieGridAdapter();
        recyclerView.setAdapter(adapter);
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
