package software.level.udacity.popularmovies2.ui;

/**
 * Base class for all presenters.
 *
 * @param <T> View that will be attached to the presenter
 */
public class Presenter<T> {

    private T view;

    public void bindView(T view) { this.view = view; }
    public void unbindView() { view = null; }
    public void dispose() { PresenterManager.disposePresenter(view.getClass().getSimpleName()); }
}
