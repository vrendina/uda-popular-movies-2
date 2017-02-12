package software.level.udacity.popularmovies2.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import software.level.udacity.popularmovies2.R;
import software.level.udacity.popularmovies2.api.model.Movie;
import software.level.udacity.popularmovies2.api.model.MovieReview;
import software.level.udacity.popularmovies2.api.model.MovieTrailer;

public class MovieDetailActivity extends AppCompatActivity implements MovieDetailAdapter.MovieOnClickHandler {

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

        // Pull the movie information out of the passed intent and give it to the presenter
        Movie movie = getIntent().getParcelableExtra(Movie.TAG);

        if(movie != null) {
            presenter.setMovieData(movie);
        }

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

    @Override
    public void onTrailerClickPlay(MovieTrailer trailer) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("vnd.youtube:" + trailer.key), "video/*");

        // If the YouTube app is not installed, fall back on the browser
        if(intent.resolveActivity(getPackageManager()) == null) {
            intent.setData(Uri.parse("https://youtu.be/" + trailer.key));
        }

        startActivity(Intent.createChooser(intent, getString(R.string.trailer_chooser)));
    }

    @Override
    public void onTrailerClickShare(MovieTrailer trailer) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_TEXT, "https://youtu.be/" + trailer.key);
        startActivity(Intent.createChooser(intent, getString(R.string.trailer_share_chooser)));
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
        adapter = new MovieDetailAdapter(this.presenter, this);
        recyclerView.setAdapter(adapter);

        // Add dividers
        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);

        // Don't show the change animation -- this gets rid of that opacity change on redraw
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    public void showLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void updateData(Movie movie, ArrayList<MovieTrailer> trailers, ArrayList<MovieReview> reviews) {
        adapter.setMovieData(movie, trailers, reviews);
    }

    public void showError() {
        Toast.makeText(this, getString(R.string.error_loading_movies), Toast.LENGTH_LONG).show();
        hideLoading();
    }

    /**
     * Factory that generates the appropriate presenter for this view.
     */
    private PresenterFactory<MovieDetailPresenter> presenterFactory = new PresenterFactory<MovieDetailPresenter>() {
        @NonNull @Override
        public MovieDetailPresenter createPresenter() {
            return new MovieDetailPresenter(getResources().getString(R.string.API_KEY),
                    getContentResolver());
        }
    };


}
