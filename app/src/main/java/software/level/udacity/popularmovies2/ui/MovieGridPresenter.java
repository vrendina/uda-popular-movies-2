package software.level.udacity.popularmovies2.ui;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import software.level.udacity.popularmovies2.api.MovieServiceManager;
import software.level.udacity.popularmovies2.api.model.Movie;
import software.level.udacity.popularmovies2.api.model.MovieEnvelope;
import software.level.udacity.popularmovies2.data.MovieContract;

public class MovieGridPresenter extends Presenter<MovieGridActivity> {

    public static final String TAG = MovieGridPresenter.class.getSimpleName();

    final private String apiKey;
    private ContentResolver resolver;

    private FavoriteContentObserver observer;

    private boolean isLoading = false;

    private int selectedRequestType = MovieEnvelope.TYPE_POPULAR;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ArrayList<Movie> favorites = new ArrayList<>();
    private ArrayList<Movie> popular = new ArrayList<>();
    private ArrayList<Movie> toprated = new ArrayList<>();

    private static final String KEY_REQUEST_TYPE = "requestType";
    private static final String KEY_MOVIES_POPULAR = "popular";
    private static final String KEY_MOVIES_TOPRATED = "toprated";

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

        loadMovieData();
    }

    /**
     * Generates a bundle with state information for recreating the presenter. Does not save the list
     * of favorites to the bundle. Those can be recreated from the database if needed.
     *
     * @return Bundle containing state information
     */
    public Bundle saveState() {
        Bundle state = new Bundle();

        // Save selected request type
        state.putInt(KEY_REQUEST_TYPE, selectedRequestType);

        // Save the lists of movie data
        state.putParcelableArrayList(KEY_MOVIES_POPULAR, popular);
        state.putParcelableArrayList(KEY_MOVIES_TOPRATED, toprated);

        Log.d(TAG, "saveState: " + state.toString());

        return state;
    }

    /**
     * Restore the state of the presenter with the information stored in the passed Bundle.
     *
     * @param state Bundle that contains saved presenter state information
     */
    public void restoreState(Bundle state) {

        Log.d(TAG, "restoreState: " + state.toString());

        this.selectedRequestType = state.getInt(KEY_REQUEST_TYPE, MovieEnvelope.TYPE_POPULAR);

        this.toprated = state.getParcelableArrayList(KEY_MOVIES_TOPRATED);
        this.popular = state.getParcelableArrayList(KEY_MOVIES_POPULAR);
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
     * @param requestType Type of movie request
     */
    public void updateMovieData(int requestType) {

        // If the data is loading and we request the same type of data again do nothing
        if(isLoading && requestType == selectedRequestType) {
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

        getDataObservable().delay(0, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    /**
     * Gets an observable that will emit movie data based on the selected request type.
     *
     * @return Observable that emits a single MovieEnvelope object before termination
     */
    private Observable<MovieEnvelope> getDataObservable() {
        Observable<MovieEnvelope> observable;

        switch(selectedRequestType) {
            case MovieEnvelope.TYPE_POPULAR:
                observable = getPopularMovies();
                break;

            case MovieEnvelope.TYPE_TOPRATED:
                observable = getTopRatedMovies();
                break;

            case MovieEnvelope.TYPE_FAVORITE:
                observable = getFavoriteMovies();
                break;

            default:
                observable = getPopularMovies();
        }

        return observable;
    }

    private Observable<MovieEnvelope> getPopularMovies() {
        if(!popular.isEmpty()) {
            return Observable.just(new MovieEnvelope(popular, MovieEnvelope.TYPE_POPULAR));
        } else {
            return MovieServiceManager.getService()
                    .getPopularMovies(apiKey)
                    .map(new Function<MovieEnvelope, MovieEnvelope>() {
                        @Override
                        public MovieEnvelope apply(MovieEnvelope movieEnvelope) throws Exception {
                            movieEnvelope.resultType = MovieEnvelope.TYPE_POPULAR;
                            return movieEnvelope;
                        }
                    });
        }
    }

    private Observable<MovieEnvelope> getTopRatedMovies() {
        if(!toprated.isEmpty()) {
            return Observable.just(new MovieEnvelope(toprated, MovieEnvelope.TYPE_TOPRATED));
        } else {
            return MovieServiceManager.getService()
                    .getTopRatedMovies(apiKey)
                    .map(new Function<MovieEnvelope, MovieEnvelope>() {
                        @Override
                        public MovieEnvelope apply(MovieEnvelope movieEnvelope) throws Exception {
                            movieEnvelope.resultType = MovieEnvelope.TYPE_TOPRATED;
                            return movieEnvelope;
                        }
                    });
        }
    }

    private Observable<MovieEnvelope> getFavoriteMovies() {
        if(!favorites.isEmpty()) {
            return Observable.just(new MovieEnvelope(favorites, MovieEnvelope.TYPE_FAVORITE));
        } else {
            return Observable.create(new ObservableOnSubscribe<MovieEnvelope>() {
                @Override
                public void subscribe(ObservableEmitter<MovieEnvelope> e) throws Exception {
                    List<Movie> movies = new ArrayList<>();

                    Cursor results = resolver.query(MovieContract.MovieFavoriteEntry.CONTENT_URI, null, null, null, null);

                    // Build up the list of movies from the favorites stored in the database
                    if (results != null) {
                        while (results.moveToNext()) {
                            Movie movie = new Movie();

                            int idIndex = results.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_MOVIEID);
                            int posterPathIndex = results.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_POSTERPATH);
                            int overviewIndex = results.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_OVERVIEW);
                            int releaseDateIndex = results.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_RELEASEDATE);
                            int titleIndex = results.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_TITLE);
                            int voteAverageIndex = results.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_RATING);
                            int posterIndex = results.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_POSTER);

                            movie.id = results.getInt(idIndex);
                            movie.posterPath = results.getString(posterPathIndex);
                            movie.overview = results.getString(overviewIndex);
                            movie.releaseDate = results.getString(releaseDateIndex);
                            movie.title = results.getString(titleIndex);
                            movie.voteAverage = results.getDouble(voteAverageIndex);
                            movie.poster = results.getBlob(posterIndex);

                            movies.add(movie);
                        }

                        results.close();
                    }

                    e.onNext(new MovieEnvelope(movies, MovieEnvelope.TYPE_FAVORITE));
                    e.onComplete();
                }
            });
        }
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
    private void showData(ArrayList<Movie> movies) {
        if(view != null) {
            view.updateData(movies);
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
        public void onNext(MovieEnvelope envelope) {
            isLoading = false;

            ArrayList<Movie> movies = (ArrayList<Movie>) envelope.movies;

            switch (envelope.resultType) {
                case MovieEnvelope.TYPE_POPULAR:
                    popular = movies;
                    break;

                case MovieEnvelope.TYPE_TOPRATED:
                    toprated = movies;
                    break;

                case MovieEnvelope.TYPE_FAVORITE:
                    favorites = movies;

                    if(favorites.isEmpty()) {
                        view.showEmptyFavoritesWarning();
                    }

                    break;
            }
            Log.d(TAG, "onNext: Obtained list of movies from observable " + movies.toString());

            showData(movies);
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError: MovieObserver error", e);

            isLoading = false;
            showData(new ArrayList<Movie>());

            if(view != null) {
                view.showError();
            }
        }

        @Override
        public void onComplete() {}
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
         * to reload by clearing the movies array.
         *
         * @param selfChange True if this is a self-change notification
         */
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            Log.d(TAG, "onChange: Change to favorites observed, clearing list.");
            favorites = new ArrayList<>();
        }
    }

}
