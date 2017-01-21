package software.level.udacity.popularmovies2.ui;

import android.support.v4.util.SimpleArrayMap;
import android.util.Log;


/**
 * PresenterManager
 *
 * Singleton class that holds references to presenters as long as they are needed to manage
 * the state of their associated view.
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
     * Returns an instance of the presenter for a particular view. If the presenter
     * does not exist the factory will be used to create a new instance.
     *
     * @param key The identifier for the presenter in the cache, TAG from activity
     * @param factory A PresenterFactory implementation that describes how to create the presenter
     * @return The presenter
     */
    @SuppressWarnings("unchecked")
    public static <T extends Presenter> T getPresenter(String key, PresenterFactory<T> factory) {
        T presenter = null;
        try {
            presenter = (T) manager.cache.get(key);
        } catch (ClassCastException e) {
            Log.w(TAG, "getPresenter: " + e);
        }

        if(presenter == null) {
            presenter = factory.createPresenter();
            manager.cache.put(key, presenter);
        }

        return presenter;
    }

    /**
     * Remove a presenter from the cache when it is no longer needed. This method should be called
     * when the corresponding activity is destroyed and will not be recreated. This situation occurs
     * when an activity's onDestroy method is called without first calling onSaveInstanceState.
     *
     * @param key The TAG of the activity used to cache the presenter
     */
    public static void disposePresenter(String key) {
        manager.cache.remove(key);
    }
}
