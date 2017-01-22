package software.level.udacity.popularmovies2.ui;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import software.level.udacity.popularmovies2.api.MovieServiceManager;
import software.level.udacity.popularmovies2.api.model.Movie;
import software.level.udacity.popularmovies2.api.model.MovieEnvelope;

public class MovieGridPresenter extends Presenter<MovieGridActivity> {

    public static final String TAG = MovieGridPresenter.class.getSimpleName();

    // Identifiers for the types of movie requests we can perform
    public static final int REQUEST_POPULAR = 100;
    public static final int REQUEST_TOPRATED = 101;
    public static final int REQUEST_FAVORITE = 102;

    // Currently selected movie request type
    private int selectedRequestType = REQUEST_POPULAR;

    // Key for the API set in the constructor
    private String apiKey;

    // If the presenter is currently trying to load data
    private boolean isLoading = false;

    // CompositeDisposable used to dispose of any observables when destroying the presenter
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Movie data that is pushed to the RecyclerView
    private ArrayList<Movie> movieData = new ArrayList<>();

    /**
     * Constructor to create a new instance of the presenter.
     *
     * @param apiKey Required key to access to movie api
     */
    public MovieGridPresenter(@NonNull String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Attaches the view to the presenter and loads any necessary data.
     *
     * @param view Activity to bind to the presenter
     */
    @Override
    public void bindView(MovieGridActivity view) {
        super.bindView(view);

        // If we are loading data when the view is bound, show the loading state
        if(isLoading) {
            showLoading();
            return;
        }

        // If we don't have any movie data and aren't trying to load any force the loading
        if(movieData.isEmpty()) {
            loadMovieData();
            return;
        }

        // We already have movie data and we aren't trying to load any, show the data immediately
        showData();
    }

    @Override
    public void dispose() {
        compositeDisposable.clear();
        super.dispose();
    }

    /**
     * Updates the movie data based on what the user has requested.
     *
     * @param requestType Type of movie request (REQUEST_POPULAR, REQUEST_TOPRATED, REQUEST_FAVORITE)
     */
    public void updateMovieData(int requestType) {

        // If the data is loading and we request the same type of data again do nothing
        if(isLoading && requestType == selectedRequestType) {
            Log.d(TAG, "loadMovieData: Currently loading data for the selected request " + requestType);
            return;
        }

        // If the data is done loading and we request the same type of data again display the data immediately
        if(!isLoading && requestType == selectedRequestType) {
            Log.d(TAG, "loadMovieData: Requested same data again we already have " + requestType);
            showData();
            return;
        }

        // Update the selected request type, meaning we will proceed with getting new data for the user
        selectedRequestType = requestType;

        // Load the new movie data
        loadMovieData();
    }

    /**
     * Loads movie data from the appropriate data source. This could be from the Movie Database
     * API or from the local database for stored favorites.
     */
    private void loadMovieData() {
        Log.d(TAG, "loadMovieData: Obtaining data for request type " + selectedRequestType);

        // Dispose of any existing observables, gets rid of any pending requests
        compositeDisposable.clear();

        // Create a new observer instance and add it to the composite disposable
        DisposableObserver<MovieEnvelope> observer = new MovieObserver();
        compositeDisposable.add(observer);

        // A delay has been added for lifecycle testing
        getDataObservable().delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    /**
     * Gets an observable that will emit movie data.
     *
     * @return Observable that emits a single MovieEnvelope object before termination
     */
    private Observable<MovieEnvelope> getDataObservable() {
        Observable<MovieEnvelope> observable;

        switch(selectedRequestType) {
            case REQUEST_POPULAR:
                observable = MovieServiceManager.getService().getPopularMovies(apiKey);
                break;

            case REQUEST_TOPRATED:
                observable = MovieServiceManager.getService().getTopRatedMovies(apiKey);
                break;

            default:
                observable = MovieServiceManager.getService().getPopularMovies(apiKey);
        }

        return observable;
    }

    /**
     * Tells the view to show a loading state.
     */
    private void showLoading() {
        if(view != null) {
            view.showLoading();
        }
    }

    /**
     * Tells the view to display the movie data and stop showing any loading indicators.
     */
    private void showData() {
        if(view != null) {
            view.updateData(movieData);
            view.hideLoading();
        }
    }

    /**
     * Observer inner class that handles any data that comes back from the observables.
     */
    private class MovieObserver extends DisposableObserver<MovieEnvelope> {

        @Override
        protected void onStart() {
            super.onStart();
            isLoading = true;
            showLoading();
        }

        @Override
        public void onNext(MovieEnvelope movieEnvelope) {
            movieData = (ArrayList<Movie>) movieEnvelope.movies;
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError: MovieObserver error", e);

            isLoading = false;

            if(view != null) {
                view.showError();
            }
        }

        @Override
        public void onComplete() {
            isLoading = false;
            showData();
        }
    }

}
