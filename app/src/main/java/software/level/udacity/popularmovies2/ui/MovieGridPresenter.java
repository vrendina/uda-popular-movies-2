package software.level.udacity.popularmovies2.ui;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import software.level.udacity.popularmovies2.api.MovieServiceManager;
import software.level.udacity.popularmovies2.api.model.Movie;
import software.level.udacity.popularmovies2.api.model.MovieEnvelope;
import software.level.udacity.popularmovies2.data.MovieContract;

public class MovieGridPresenter extends Presenter<MovieGridActivity> {

    public static final String TAG = MovieGridPresenter.class.getSimpleName();

    // Identifiers for the types of movie requests we can perform
    public static final int REQUEST_POPULAR = 100;
    public static final int REQUEST_TOPRATED = 101;
    public static final int REQUEST_FAVORITE = 102;

    // Default movie request type
    private static final int REQUEST_DEFAULT = REQUEST_POPULAR;

    // Currently selected movie request type
    private int selectedRequestType = REQUEST_DEFAULT;

    // Key for the API set in the constructor
    final private String apiKey;

    // ContentResolver used to pull data from the ContentProvider
    private ContentResolver resolver;

    // ContentObserver to watch for changes in the favorite data
    private FavoriteContentObserver observer;

    // If the presenter is currently trying to load data
    private boolean isLoading = false;

    // CompositeDisposable used to dispose of any observables when destroying the presenter
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Movie data that is pushed to the RecyclerView
    private ArrayList<Movie> movieData = new ArrayList<>();

    /**
     * Constructor to create a new instance of the presenter.
     *
     * @param apiKey required key to access to movie api
     * @param resolver ContentResolver object for retrieving information from the ContentProvider
     */
    public MovieGridPresenter(String apiKey, ContentResolver resolver) {
        this.apiKey = apiKey;
        this.resolver = resolver;

        // Create a content observer to be notified of changes to the favorites list
        observer = new FavoriteContentObserver(new Handler());
        resolver.registerContentObserver(MovieContract.MovieFavoriteEntry.CONTENT_URI, true, observer);
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
        resolver.unregisterContentObserver(observer);

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
            return;
        }

        // If the data is done loading and we request the same type of data again display the data immediately
        if(!isLoading && requestType == selectedRequestType) {
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
        // Dispose of any existing observables, gets rid of any pending requests
        compositeDisposable.clear();

        // Create a new observer instance and add it to the composite disposable
        DisposableObserver<MovieEnvelope> observer = new MovieObserver();
        compositeDisposable.add(observer);

        getDataObservable().observeOn(AndroidSchedulers.mainThread())
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

            case REQUEST_FAVORITE:
                observable = Observable.create(new ObservableOnSubscribe<MovieEnvelope>() {
                    @Override
                    public void subscribe(ObservableEmitter<MovieEnvelope> e) throws Exception {
                        MovieEnvelope envelope = new MovieEnvelope();
                        envelope.movies = new ArrayList<>();

                        Cursor results = resolver.query(MovieContract.MovieFavoriteEntry.CONTENT_URI, null, null, null, null);

                        // Build up the list of movies from the favorites stored in the database
                        if(results != null) {
                            while(results.moveToNext()) {
                                Movie movie = new Movie();
                                movie.id = results.getInt(results.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_MOVIEID));
                                movie.posterPath = results.getString(results.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_POSTERPATH));
                                movie.encodedPoster = results.getBlob(results.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_POSTER));

                                envelope.movies.add(movie);
                            }

                            results.close();
                        }

                        e.onNext(envelope);
                        e.onComplete();
                    }
                });
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

            // If we select favorites but we don't have any show a message and switch back to default
            if(selectedRequestType == REQUEST_FAVORITE && movieData.isEmpty()) {
                view.showEmptyFavoritesWarning();
                updateMovieData(REQUEST_DEFAULT);
            }
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

    /**
     * If a change is made in the ContentProvider to the list of favorite movies this object will
     * be notified. When returning from a MovieDetailActivity after making a change to the favorites
     * the movie grid will not be updated to reflect the change unless the data is forced to reload.
     */
    private class FavoriteContentObserver extends ContentObserver {

        public FavoriteContentObserver(Handler handler) {
            super(handler);
        }

        /**
         * If the favorite list is displayed and a change occurs in the ContentProvider force the data
         * to reload by clearing the movieData array.
         *
         * @param selfChange True if this is a self-change notification
         */
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            if(selectedRequestType == REQUEST_FAVORITE) {
                movieData = new ArrayList<>();
            }
        }
    }

}
