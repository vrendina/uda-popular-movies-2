package software.level.udacity.popularmovies2.ui;

import android.support.v4.util.SimpleArrayMap;
import android.util.Log;


/**
 * PresenterManager
 *
 * Singleton class that holds references to presenters as long as they are needed to manage
 * the state of their associated view.
 *
 */
public class PresenterManager {

    public static final String TAG = PresenterManager.class.getSimpleName();

    private static PresenterManager manager;

    // Cache to retain instances of the presenters
    private SimpleArrayMap<String, Presenter> cache;

    // The presenter manager is initialized by the application class when the app first launches
    private PresenterManager() {}

    public static void initializePresenterManager() {
        if(manager == null) {
            manager = new PresenterManager();
            manager.cache = new SimpleArrayMap<>();
        }
    }

    /**
     * Returns the instance of the presenter manager.
     *
     * @return The presenter manager
     */
    public static PresenterManager getPresenterManager() {
        if(manager == null) {
            initializePresenterManager();
        }
        return manager;
    }

    /**
     * Returns an instance of the presenter for a particular view. If the presenter
     * does not exist the factory will be used to create a new instance.
     *
     * @param key The identifier for the presenter in the cache, TAG from activity
     * @param factory A PresenterFactory implementation that describes how to create the presenter
     * @param <T> The presenter type which must extend Presenter
     * @return The presenter
     */
    @SuppressWarnings("unchecked")
    public <T extends Presenter> T getPresenter(String key, PresenterFactory<T> factory) {

        Log.d(TAG, "getPresenter: Obtaining presenter for " + key);

        T presenter = null;
        try {
            presenter = (T) cache.get(key);
        } catch (ClassCastException e) {
            Log.w(TAG, "getPresenter: " + e);
        }

        if(presenter == null) {
            Log.d(TAG, "getPresenter: Presenter did not exist, creating new instance");
            presenter = factory.createPresenter();
            cache.put(key, presenter);
        }

        return presenter;
    }




}
