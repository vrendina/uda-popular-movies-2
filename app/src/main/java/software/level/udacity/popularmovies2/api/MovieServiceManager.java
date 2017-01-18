package software.level.udacity.popularmovies2.api;

import retrofit2.Retrofit;

/**
 * Management singleton class for the Retrofit MovieService.
 */
public class MovieServiceManager {

    private static MovieServiceManager manager;
    private MovieService service;

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";

    private MovieServiceManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIE_BASE_URL)
                .addCallAdapterFactory(NetworkManager.getRxJava2CallAdapterFactory())
                .addConverterFactory(NetworkManager.getGsonConverter())
                .client(NetworkManager.getClient())
                .build();
        service = retrofit.create(MovieService.class);
    }

    public static MovieService getService() {
        if(manager == null) {
            manager = new MovieServiceManager();
        }
        return manager.service;
    }

}
