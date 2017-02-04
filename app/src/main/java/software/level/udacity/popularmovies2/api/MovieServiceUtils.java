package software.level.udacity.popularmovies2.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MovieServiceUtils {

    public static final String TAG = MovieServiceUtils.class.getSimpleName();

    // Base URL for image service from the Movie Database
    private static final String MOVIE_IMAGE_BASE_URL = "https://image.tmdb.org/t/p";

    /**
     * Generates a URL for downloading an image from The Movie DB image service
     * @param path API supplied poster_path or backdrop_path
     * @param size Size of image required (w92, w154, w185, w342, w500, w780, original)
     * @return URL for downloading image
     */
    public static URL buildImageURL(String path, String size) {
        // Strip any preceding slash out of the path
        path = path.replace("/", "");

        Uri uri = Uri.parse(MOVIE_IMAGE_BASE_URL)
                .buildUpon()
                .appendPath(size)
                .appendPath(path)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem with constructing URL" + e);
        }

        return url;
    }

    public static byte[] encodeImageData(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, stream);

        return stream.toByteArray();
    }

    public static Bitmap decodeImageData(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
