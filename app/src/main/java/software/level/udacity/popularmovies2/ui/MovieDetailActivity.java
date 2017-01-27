package software.level.udacity.popularmovies2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import software.level.udacity.popularmovies2.R;
import software.level.udacity.popularmovies2.api.model.MovieDetailsComposite;
import software.level.udacity.popularmovies2.api.model.MovieTrailer;

public class MovieDetailActivity extends AppCompatActivity implements MovieDetailAdapter.MovieTrailerOnClickHandler {

    public static final String TAG = MovieDetailActivity.class.getSimpleName();

    @BindView(R.id.rv_movie_details) RecyclerView recyclerView;
    @BindView(R.id.pb_movie_details_loading) ProgressBar progressBar;

    private MovieDetailPresenter presenter;
    private MovieDetailAdapter adapter;

    // If the activity is going to be destroyed and the system does not expect to recreate it
    private boolean activityIsFinished = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        // Get the existing presenter or a new instance if it isn't cached
        presenter = PresenterManager.getPresenter(TAG, presenterFactory);

        configureRecyclerView();
    }

    /**
     * When the view is about to come on screen, bind to the presenter so the UI can be
     * updated.
     */
    @Override
    protected void onStart() {
        super.onStart();

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
     * recreated again and some information about the state shold be saved.
     *
     * @param outState Bundle where state information can be saved
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        activityIsFinished = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(activityIsFinished) {
            Log.d(TAG, "onDestroy: Instance of this activity is finished");

            presenter.dispose();
        }

    }

    @Override
    public void onTrailerClick(MovieTrailer trailer) {
        Log.d(TAG, "onTrailerClick: " + trailer);
    }

    /**
     * Performs the initial configuration for the RecyclerView. Configures the RecyclerView
     * to use a LinearLayoutManager and binds the adapter class.
     */
    private void configureRecyclerView() {
        // Initialize the layout manager and set the RecyclerView to use it
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Create the adapter and set it
        adapter = new MovieDetailAdapter(this);
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

    public void updateData(MovieDetailsComposite data) {
        adapter.setMovieData(data);
    }

    public void showError() {
        Toast.makeText(this, "Error loading movie data!", Toast.LENGTH_LONG).show();
        hideLoading();
    }

    /**
     * Factory that generates the appropriate presenter for this view.
     */
    private PresenterFactory<MovieDetailPresenter> presenterFactory = new PresenterFactory<MovieDetailPresenter>() {
        @NonNull @Override
        public MovieDetailPresenter createPresenter() {
            return new MovieDetailPresenter(getResources().getString(R.string.API_KEY),
                    getIntent().getIntExtra(Intent.EXTRA_TEXT, -1));
        }
    };


}
