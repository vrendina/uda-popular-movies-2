package software.level.udacity.popularmovies2.api;

import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class to setup the required dependencies for Retrofit. In the
 * future this should be switched over to Dagger.
 */
public class NetworkManager {

    private static NetworkManager manager;

    private static OkHttpClient client;
    private static GsonConverterFactory converter;

    private NetworkManager() {
        client = new OkHttpClient();
        converter = GsonConverterFactory.create();
    }

    public static void initializeNetworkManager() {
        if(manager == null) {
            manager = new NetworkManager();
        }
    }

    public static OkHttpClient getClient() {
        if(client == null) {
            initializeNetworkManager();
        }
        return client;
    }

    public static GsonConverterFactory getConverter() {
        if(converter == null) {
            initializeNetworkManager();
        }
        return converter;
    }

}
