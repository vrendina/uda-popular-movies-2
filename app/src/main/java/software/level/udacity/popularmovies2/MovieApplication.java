package software.level.udacity.popularmovies2;

import android.app.Application;

import software.level.udacity.popularmovies2.api.NetworkManager;


public class MovieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create the network manager singleton class
        NetworkManager.initializeNetworkManager();
    }

}
