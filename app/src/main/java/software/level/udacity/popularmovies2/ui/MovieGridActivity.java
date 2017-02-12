package software.level.udacity.popularmovies2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import software.level.udacity.popularmovies2.R;
import software.level.udacity.popularmovies2.api.model.Movie;
import software.level.udacity.popularmovies2.api.model.MovieEnvelope;

public class MovieGridActivity extends AppCompatActivity implements MovieGridAdapter.MovieClickHandler {

    public static final String TAG = MovieGridActivity.class.getSimpleName();

    @BindView(R.id.rv_movie_grid) RecyclerView recyclerView;
    @BindView(R.id.pb_movies_loading) ProgressBar progressBar;

    private MovieGridPresenter presenter;
    private MovieGridAdapter adapter;
    
    // If the activity is going to be destroyed and the system does not expect to recreate it
    private boolean activityIsFinished = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);

        ButterKnife.bind(this);

        // Get the existing presenter or a new instance if it isn't cached
        presenter = PresenterManager.getPresenter(TAG, presenterFactory);

        // Restore the state if we have a bundle
        if(savedInstanceState != null) {
            Bundle presenterState = savedInstanceState.getBundle(TAG);
            presenter.restoreState(presenterState);
        }
        
        configureRecyclerView();
    }

    /**
     * When the view is about to come on screen, bind to the presenter so the UI can be
     * updated.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // By default we want to destroy the presenter when it goes off screen unless the state is saved
        activityIsFinished = true;

        presenter.bindView(this);
    }

    /**
     * If the view is off the screen, unbind from the presenter so the presenter doesn't try to
     * make any changes to the UI
     */
    @Override
    protected void onStop() {
        super.onStop();

        presenter.unbindView();
    }

    /**
     * If this method is called by the system, Android believes that this activity will be
     * recreated again and some information about the state should be saved.
     *
     * @param outState Bundle where state information can be saved
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        activityIsFinished = false;
        outState.putBundle(TAG, presenter.saveState());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if(activityIsFinished) {
            presenter.dispose();
        }

    }

    /**
     * Click handler for the selection of a movie. Starts the detail activity and passes
     * the id of the movie.
     *
     * @param movie Movie that was clicked
     */
    @Override
    public void onClickMovie(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(Movie.TAG, movie);

        startActivity(intent);
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
        int id = item.getItemId();
        switch(id) {
            case R.id.action_favorite:
                presenter.updateMovieData(MovieEnvelope.TYPE_FAVORITE);
                return true;

            case R.id.action_popular:
                presenter.updateMovieData(MovieEnvelope.TYPE_POPULAR);
                return true;

            case R.id.action_toprated:
                presenter.updateMovieData(MovieEnvelope.TYPE_TOPRATED);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Performs the initial configuration for the RecyclerView. Configures the RecyclerView
     * to use a GridLayoutManager and binds the adapter class.
     */
    private void configureRecyclerView() {
        // Depending on the screen orientation we can show a different number of columns
        int columns = getResources().getInteger(R.integer.movie_grid_columns);

        // Initialize the layout manager and set the RecyclerView to use it
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns);
        recyclerView.setLayoutManager(gridLayoutManager);

        // All the movie posters will be the same size
        recyclerView.setHasFixedSize(true);

        // Create the adapter and set it
        adapter = new MovieGridAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    
    public void showLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void updateData(ArrayList<Movie> movieData) {
        adapter.setMovieData(movieData);
    }

    public void showEmptyFavoritesWarning() {
        Toast.makeText(this, getString(R.string.warning_empty_favorites), Toast.LENGTH_LONG).show();
    }

    public void showError() {
        Toast.makeText(this, getString(R.string.error_loading_movies), Toast.LENGTH_LONG).show();
        hideLoading();
    }

    /**
     * Factory that generates the appropriate presenter for this view.
     */
    private PresenterFactory<MovieGridPresenter> presenterFactory =
            new PresenterFactory<MovieGridPresenter>() {
                @NonNull @Override
                public MovieGridPresenter createPresenter() {
                    return new MovieGridPresenter(getResources().getString(R.string.API_KEY), getContentResolver());
                }
            };

}
