package software.level.udacity.popularmovies2;

import android.app.Application;

import software.level.udacity.popularmovies2.api.NetworkManager;
import software.level.udacity.popularmovies2.ui.PresenterManager;


public class MovieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Create the network manager singleton object
        NetworkManager.initializeNetworkManager();

        // Create the presenter manager singleton object
        PresenterManager.initializePresenterManager();
    }

}
