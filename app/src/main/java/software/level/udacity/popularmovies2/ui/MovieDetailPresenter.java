package software.level.udacity.popularmovies2.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import software.level.udacity.popularmovies2.api.MovieServiceManager;
import software.level.udacity.popularmovies2.api.MovieServiceUtils;
import software.level.udacity.popularmovies2.api.model.Movie;
import software.level.udacity.popularmovies2.api.model.MovieDetailsComposite;
import software.level.udacity.popularmovies2.api.model.MovieReview;
import software.level.udacity.popularmovies2.api.model.MovieReviewEnvelope;
import software.level.udacity.popularmovies2.api.model.MovieTrailer;
import software.level.udacity.popularmovies2.api.model.MovieTrailerEnvelope;
import software.level.udacity.popularmovies2.data.MovieContract;


public class MovieDetailPresenter extends Presenter<MovieDetailActivity> {

    public static final String TAG = MovieDetailPresenter.class.getSimpleName();

    private String apiKey;
    private ContentResolver resolver;

    private boolean loading;
    private Boolean favorite;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Movie movie;
    private ArrayList<MovieTrailer> trailers = new ArrayList<>();
    private ArrayList<MovieReview> reviews = new ArrayList<>();

    private static final String KEY_MOVIE = "movie";
    private static final String KEY_TRAILERS = "trailers";
    private static final String KEY_REVIEWS = "reviews";
    private static final String KEY_FAVORITE = "favorite";

    /**
     * Creates a new instance of the MovieDetailPresenter
     *
     * @param apiKey required key to access the movie api
     * @param resolver ContentResolver object for retrieving information from the ContentProvider
     */
    public MovieDetailPresenter(String apiKey, ContentResolver resolver) {
        this.apiKey = apiKey;
        this.resolver = resolver;
    }

    @Override
    public void bindView(MovieDetailActivity view) {
        super.bindView(view);

        // If we are loading data when the view is bound, show the loading state
        if(loading) {
            showLoading();
            return;
        }

        loadMovieDetails();
    }

    @Override
    public void dispose() {
        compositeDisposable.clear();
        super.dispose();
    }

    /**
     * Generates a bundle with state information for recreating the presenter.
     *
     * @return Bundle containing state information
     */
    public Bundle saveState() {
        Bundle state = new Bundle();

        state.putParcelable(KEY_MOVIE, movie);
        state.putParcelableArrayList(KEY_TRAILERS, trailers);
        state.putParcelableArrayList(KEY_REVIEWS, reviews);
        state.putBoolean(KEY_FAVORITE, favorite);

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

        movie = state.getParcelable(KEY_MOVIE);
        trailers = state.getParcelableArrayList(KEY_TRAILERS);
        reviews = state.getParcelableArrayList(KEY_REVIEWS);
        favorite = state.getBoolean(KEY_FAVORITE);
    }

    /**
     * Set the movie data for the presenter so additional movie details can be fetched based on
     * the id of the movie.
     *
     * @param movie Movie object containing movie details
     */
    public void setMovieData(Movie movie) {
        Log.d(TAG, "setMovieData: " + movie);
        this.movie = movie;
    }

    /**
     * Loads the trailer and review data for the movie. A zip operator is used to combine the network
     * operations and deliver the data at the same time.
     */
    private void loadMovieDetails() {
        // Dispose of any existing observables, gets rid of any pending requests
        compositeDisposable.clear();

        // Create a new observer instance and add it to the composite disposable
        DisposableObserver<MovieDetailsComposite> observer = new MovieDetailPresenter.MovieObserver();
        compositeDisposable.add(observer);

        // Get the observables for all the movie data
        Observable<MovieTrailerEnvelope> trailers = getTrailers();
        Observable<MovieReviewEnvelope> reviews = getReviews();
        Observable<Boolean> favorite = getFavorite();

        // Combine all the observables with the zip operator
        Observable<MovieDetailsComposite> combined = Observable.zip(trailers, reviews, favorite,
                new Function3<MovieTrailerEnvelope, MovieReviewEnvelope, Boolean, MovieDetailsComposite>() {
                    @Override
                    public MovieDetailsComposite apply(MovieTrailerEnvelope trailerEnvelope, MovieReviewEnvelope reviewEnvelope, Boolean favorite) throws Exception {
                        return new MovieDetailsComposite(trailerEnvelope.trailers, reviewEnvelope.reviews, favorite);
                    }
                });

        combined.delay(0, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    /**
     * Creates an observable to fetch the movie reviews and if an error occurs returns an empty
     * list of reviews.
     *
     * @return Observable<MovieReviewEnvelope>
     */
    private Observable<MovieReviewEnvelope> getReviews() {
        if(!reviews.isEmpty()) {
            return Observable.just(new MovieReviewEnvelope(reviews));
        } else {
            return MovieServiceManager.getService().getReviews(movie.id, apiKey)
                    .onErrorReturn(new Function<Throwable, MovieReviewEnvelope>() {
                        @Override
                        public MovieReviewEnvelope apply(Throwable throwable) throws Exception {
                            Log.e(TAG, "Error fetching reviews: " + throwable.getMessage());

                            // Create an empty envelope with no reviews
                            MovieReviewEnvelope envelope = new MovieReviewEnvelope(new ArrayList<MovieReview>());
                            envelope.totalResults = 0;
                            envelope.totalPages = 0;
                            envelope.reviews = new ArrayList<>();

                            return envelope;
                        }
                    });
        }
    }

    /**
     * Creates an observable to fetch the movie trailers and if an error occurs returns an empty
     * list of trailers.
     *
     * @return Observable<MovieTrailerEnvelope>
     */
    private Observable<MovieTrailerEnvelope> getTrailers() {
        if(!trailers.isEmpty()) {
            return Observable.just(new MovieTrailerEnvelope(trailers));
        } else {
            return MovieServiceManager.getService().getTrailers(movie.id, apiKey)
                    .onErrorReturn(new Function<Throwable, MovieTrailerEnvelope>() {
                        @Override
                        public MovieTrailerEnvelope apply(Throwable throwable) throws Exception {
                            Log.e(TAG, "Error fetching trailers: " + throwable.getMessage());

                            // Create an empty envelope with no trailers
                            MovieTrailerEnvelope envelope = new MovieTrailerEnvelope(new ArrayList<MovieTrailer>());
                            envelope.trailers = new ArrayList<>();

                            return envelope;
                        }
                    });
        }
    }

    /**
     * Creates an Observable to determine if the movie is stored in the favorites database. Returns a single
     * true/false boolean value.
     *
     * @return Observable<Boolean>
     */
    private Observable<Boolean> getFavorite() {
        if(favorite != null) {
            return Observable.just(favorite);
        } else {

            return Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                    Cursor result = resolver.query(getMovieProviderUri(),
                            new String[]{MovieContract.MovieFavoriteEntry.COLUMN_MOVIEID},
                            null, null, null);

                    if(result != null) {
                        // If we got a result then the movie is stored in the favorites
                        if(result.moveToFirst()) {
                            e.onNext(true);
                        } else {
                            e.onNext(false);
                        }
                        result.close();
                    } else {
                        Log.e(TAG, "subscribe: Problem determining if movie is in favorites database.");
                        e.onNext(false);
                    }
                    e.onComplete();
                }
            });
        }
    }

    /**
     * Is the movie stored in the favorite database?
     *
     * @return boolean
     */
    public boolean isFavorite() {
        return favorite;
    }

    /**
     * Saves a movie to the favorites database using a Completable to offload the task to a background
     * thread.
     *
     * @param poster Bitmap image of the poster to be saved
     */
    public void setFavorite(final Bitmap poster) {

        favorite = true;

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {

                ContentValues values = new ContentValues();

                values.put(MovieContract.MovieFavoriteEntry.COLUMN_MOVIEID, movie.id);
                values.put(MovieContract.MovieFavoriteEntry.COLUMN_TITLE, movie.title);
                values.put(MovieContract.MovieFavoriteEntry.COLUMN_RELEASEDATE, movie.releaseDate);
                values.put(MovieContract.MovieFavoriteEntry.COLUMN_OVERVIEW, movie.overview);
                values.put(MovieContract.MovieFavoriteEntry.COLUMN_RATING, movie.voteAverage);
                values.put(MovieContract.MovieFavoriteEntry.COLUMN_POSTERPATH, movie.posterPath);

                // If we were passed a poster bitmap to save encode it into a byte array and store it in the database
                if(poster != null) {
                    movie.poster = MovieServiceUtils.encodeImageData(poster);
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_POSTER, movie.poster);
                }

                Uri result = resolver.insert(MovieContract.MovieFavoriteEntry.CONTENT_URI, values);

                if(result == null) {
                    e.onError(new Throwable("Problem saving favorite to database."));
                }

                e.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onComplete() {
                    Log.d(TAG, "onComplete: Saved movie to favorites database.");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "onError: " + e.getMessage());
                }
            });
    }

    /**
     * Remove movie from favorites stored in the database. Use a Completable to move the task
     * of the main thread.
     */
    public void removeFavorite() {

        favorite = false;

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                int rows = resolver.delete(getMovieProviderUri(), null, null);

                if(rows != 1) {
                    e.onError(new Throwable("Problem removing movie from database."));
                }

                e.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Removed movie from favorite database.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    private Uri getMovieProviderUri() {
        return MovieContract.MovieFavoriteEntry.CONTENT_URI
                .buildUpon()
                .appendPath(String.valueOf(movie.id))
                .build();
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
            view.updateData(movie, trailers, reviews);
            view.hideLoading();
        }
    }

    private class MovieObserver extends DisposableObserver<MovieDetailsComposite> {

        @Override
        protected void onStart() {
            super.onStart();
            loading = true;
            showLoading();
        }

        @Override
        public void onNext(MovieDetailsComposite composite) {
            trailers = composite.trailers;
            reviews = composite.reviews;
            favorite = composite.favorite;

            Log.d(TAG,  "onNext: Favorite: "        + favorite +
                        " Trailers: "               + trailers.toString() +
                        " Reviews: "                + reviews.toString());
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError: MovieObserver error", e);

            loading = false;

            if(view != null) {
                view.showError();
            }
        }

        @Override
        public void onComplete() {
            loading = false;
            showData();
        }
    }

}
