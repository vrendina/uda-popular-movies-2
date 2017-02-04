package software.level.udacity.popularmovies2.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import software.level.udacity.popularmovies2.data.MovieContract.MovieFavoriteEntry;

public class MovieProvider extends ContentProvider {

    private MovieDbHelper dbHelper;

    public static final int CODE_FAVORITES = 100;
    public static final int CODE_FAVORITES_WITH_ID = 101;

    private static UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // content://software.level.udacity.popularmovies2/favorites
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITES,
                CODE_FAVORITES);

        // content://software.level.udacity.popularmovies2/favorites/#
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITES + "/#",
                CODE_FAVORITES_WITH_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor cursor;

        switch(matcher.match(uri)) {

            case CODE_FAVORITES:

                cursor = dbHelper.getReadableDatabase().query(
                        MovieFavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_FAVORITES_WITH_ID:

                selectionArgs = new String[]{uri.getLastPathSegment()};

                cursor = dbHelper.getReadableDatabase().query(
                        MovieFavoriteEntry.TABLE_NAME,
                        projection,
                        MovieFavoriteEntry.COLUMN_MOVIEID + " = ? ",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Invalid uri for query: " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        switch(matcher.match(uri)) {

            case CODE_FAVORITES:

                // Insert the item into the favorites table
                long rowId = dbHelper.getWritableDatabase().insert(MovieFavoriteEntry.TABLE_NAME,
                        null,
                        values);

                // If the insertion was successful notify that a change occurred and return the uri
                if(rowId != -1) {
                    Uri insertedUri = MovieFavoriteEntry.CONTENT_URI.buildUpon()
                            .appendPath(values.get(MovieFavoriteEntry.COLUMN_MOVIEID).toString())
                            .build();

                    getContext().getContentResolver().notifyChange(insertedUri, null);

                    return insertedUri;
                }

                break;

            default:
                throw new IllegalArgumentException("Invalid uri for insert: " + uri);
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int numRowsDeleted;

        switch(matcher.match(uri)) {

            case CODE_FAVORITES_WITH_ID:

                selectionArgs = new String[]{uri.getLastPathSegment()};

                numRowsDeleted = dbHelper.getWritableDatabase().delete(
                        MovieFavoriteEntry.TABLE_NAME,
                        MovieFavoriteEntry.COLUMN_MOVIEID + " = ? ",
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Invalid uri for delete: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Nullable @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("getType will not be implemented");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("update will not be implemented");
    }
}
