package software.level.udacity.popularmovies2.ui;

/**
 * Base class for all presenters.
 *
 * @param <T> View that will be attached to the presenter
 */
public class Presenter<T> {

    public T view;
    private String viewTag;

    public void bindView(T view) {
        this.view = view;
        this.viewTag = view.getClass().getSimpleName();
    }
    public void unbindView() { view = null; }

    public void dispose() {
        PresenterManager.disposePresenter(viewTag);
    }
}
