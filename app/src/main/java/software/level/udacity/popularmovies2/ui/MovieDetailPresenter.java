package software.level.udacity.popularmovies2.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import software.level.udacity.popularmovies2.api.MovieServiceManager;
import software.level.udacity.popularmovies2.api.MovieServiceUtils;
import software.level.udacity.popularmovies2.api.model.MovieDetails;
import software.level.udacity.popularmovies2.api.model.MovieDetailsComposite;
import software.level.udacity.popularmovies2.api.model.MovieReviewEnvelope;
import software.level.udacity.popularmovies2.api.model.MovieTrailerEnvelope;
import software.level.udacity.popularmovies2.data.MovieContract;

public class MovieDetailPresenter extends Presenter<MovieDetailActivity> {

    public static final String TAG = MovieDetailPresenter.class.getSimpleName();

    // Key for the API set in the constructor
    private String apiKey;

    // Id of movie we are displaying data for
    final private int id;

    // ContentResolver passed into the constructor for using the ContentProvider
    private ContentResolver resolver;

    // If the presenter is currently trying to load data
    private boolean isLoading = false;

    // CompositeDisposable used to dispose of any observables when destroying the presenter
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Movie data that is pushed to the RecyclerView
    private MovieDetailsComposite data;

    /**
     * Creates a new instance of the presenter.
     *
     * @param apiKey required key to access the movie api
     * @param id id of movie we are displaying data for
     */
    public MovieDetailPresenter(String apiKey, int id, ContentResolver resolver) {
        this.apiKey = apiKey;
        this.id = id;
        this.resolver = resolver;
    }

    @Override
    public void bindView(MovieDetailActivity view) {
        super.bindView(view);

        // If we are loading data when the view is bound, show the loading state
        if(isLoading) {
            showLoading();
            return;
        }

        // If we don't have any movie data and aren't trying to load any force the loading
        if(data == null) {
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
     * Loads the movie data. In this case we are using the zip operator to combine movie details,
     * reviews, and trailers all into the same observable. The data will only be presented to the
     * user once all three have completed.
     */
    private void loadMovieData() {
        // Determine if the movie is stored in the favorites database
        Cursor result = resolver.query(getMovieProviderUri(), null, null, null, null);

        Observable<MovieDetails> details;
        // If we have the movie stored in the database return the details from the database
        if(result.getCount() > 0) {

            result.moveToFirst();

            final MovieDetails movieDetails = new MovieDetails();
            movieDetails.favorite = true;
            movieDetails.id = result.getInt(result.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_MOVIEID));
            movieDetails.title = result.getString(result.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_TITLE));
            movieDetails.releaseDate = result.getString(result.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_RELEASEDATE));
            movieDetails.runtime = result.getInt(result.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_RUNTIME));
            movieDetails.voteAverage = result.getDouble(result.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_RATING));
            movieDetails.overview = result.getString(result.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_OVERVIEW));
            movieDetails.encodedPoster = result.getBlob(result.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_POSTER));
            movieDetails.posterPath = result.getString(result.getColumnIndex(MovieContract.MovieFavoriteEntry.COLUMN_POSTERPATH));

            details = Observable.create(new ObservableOnSubscribe<MovieDetails>() {
                @Override
                public void subscribe(ObservableEmitter<MovieDetails> e) throws Exception {
                    e.onNext(movieDetails);
                    e.onComplete();
                }
            });

        // If the movie isn't a favorite go out and fetch the details from the movie service
        } else {
            details = MovieServiceManager.getService().getMovieDetails(id, apiKey);
        }

        result.close();

        // Dispose of any existing observables, gets rid of any pending requests
        compositeDisposable.clear();

        // Create a new observer instance and add it to the composite disposable
        DisposableObserver<MovieDetailsComposite> observer = new MovieDetailPresenter.MovieObserver();
        compositeDisposable.add(observer);

        // Create the observables for all the types of data
        Observable<MovieReviewEnvelope> reviews = MovieServiceManager.getService().getReviews(id, apiKey);
        Observable<MovieTrailerEnvelope> trailers = MovieServiceManager.getService().getTrailers(id, apiKey);

        // Combine all the observables with the zip operator
        Observable<MovieDetailsComposite> combined = Observable.zip(details, reviews, trailers,
                new Function3<MovieDetails, MovieReviewEnvelope, MovieTrailerEnvelope, MovieDetailsComposite>() {
            @Override
            public MovieDetailsComposite apply(MovieDetails details, MovieReviewEnvelope reviewEnvelope, MovieTrailerEnvelope trailerEnvelope) throws Exception {
                return new MovieDetailsComposite(details, reviewEnvelope, trailerEnvelope);
            }
        });

        combined.delay(0, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observer);

    }

    public void setFavorite(Bitmap poster) {

        data.details.favorite = true;

        ContentValues values = new ContentValues();

        values.put(MovieContract.MovieFavoriteEntry.COLUMN_MOVIEID, data.details.id);
        values.put(MovieContract.MovieFavoriteEntry.COLUMN_TITLE, data.details.title);
        values.put(MovieContract.MovieFavoriteEntry.COLUMN_RELEASEDATE, data.details.releaseDate);
        values.put(MovieContract.MovieFavoriteEntry.COLUMN_RUNTIME, data.details.runtime);
        values.put(MovieContract.MovieFavoriteEntry.COLUMN_OVERVIEW, data.details.overview);
        values.put(MovieContract.MovieFavoriteEntry.COLUMN_RATING, data.details.voteAverage);
        values.put(MovieContract.MovieFavoriteEntry.COLUMN_POSTERPATH, data.details.posterPath);

        // If we were passed a poster bitmap to save encode it into a byte array and store it in the database
        if(poster != null) {
            byte[] encodedPoster = MovieServiceUtils.encodeImageData(poster);

            data.details.encodedPoster = encodedPoster;
            values.put(MovieContract.MovieFavoriteEntry.COLUMN_POSTER, encodedPoster);
        }

        resolver.insert(MovieContract.MovieFavoriteEntry.CONTENT_URI, values);
    }

    public void removeFavorite() {
        data.details.favorite = false;
        resolver.delete(getMovieProviderUri(), null, null);
    }

    private Uri getMovieProviderUri() {
        return MovieContract.MovieFavoriteEntry.CONTENT_URI
                .buildUpon()
                .appendPath(String.valueOf(id))
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
            view.updateData(data);
            view.hideLoading();
        }
    }

    private class MovieObserver extends DisposableObserver<MovieDetailsComposite> {

        @Override
        protected void onStart() {
            super.onStart();
            isLoading = true;
            showLoading();
        }

        @Override
        public void onNext(MovieDetailsComposite composite) {
            data = composite;
            Log.d(TAG, "onNext: " + data.details.toString() + data.reviewEnvelope.reviews.toString() + data.trailerEnvelope.trailers.toString());
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
