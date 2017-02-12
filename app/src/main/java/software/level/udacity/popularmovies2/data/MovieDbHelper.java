package software.level.udacity.popularmovies2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import software.level.udacity.popularmovies2.data.MovieContract.MovieFavoriteEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVORITE_TABLE =

            "CREATE TABLE " + MovieFavoriteEntry.TABLE_NAME + " (" +

                MovieFavoriteEntry._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, "    +
                MovieFavoriteEntry.COLUMN_MOVIEID       + " INTEGER NOT NULL, "                     +
                MovieFavoriteEntry.COLUMN_TITLE         + " TEXT, "                                 +
                MovieFavoriteEntry.COLUMN_OVERVIEW      + " TEXT, "                                 +
                MovieFavoriteEntry.COLUMN_RELEASEDATE   + " TEXT, "                                 +
                MovieFavoriteEntry.COLUMN_POSTERPATH    + " TEXT, "                                 +
                MovieFavoriteEntry.COLUMN_RATING        + " REAL, "                                 +
                MovieFavoriteEntry.COLUMN_POSTER        + " BLOB, "                                 +

            " UNIQUE (" + MovieFavoriteEntry.COLUMN_MOVIEID +") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieFavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
