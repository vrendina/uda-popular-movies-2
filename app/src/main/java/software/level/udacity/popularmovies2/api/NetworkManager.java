package software.level.udacity.popularmovies2.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class to setup the required dependencies for Retrofit. In the
 * future this should be migrated to Dagger 2.
 */
public class NetworkManager {

    private static NetworkManager manager;

    private static OkHttpClient client;
    private static GsonConverterFactory gsonConverter;
    private static RxJava2CallAdapterFactory rxJava2CallAdapterFactory;

    private NetworkManager() {
        client = new OkHttpClient();
        gsonConverter = GsonConverterFactory.create();
        rxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create();
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

    public static GsonConverterFactory getGsonConverter() {
        if(gsonConverter == null) {
            initializeNetworkManager();
        }
        return gsonConverter;
    }

    public static RxJava2CallAdapterFactory getRxJava2CallAdapterFactory() {
        if(rxJava2CallAdapterFactory == null) {
            initializeNetworkManager();
        }
        return rxJava2CallAdapterFactory;
    }

}
