package software.level.udacity.popularmovies2.ui;

import android.support.annotation.NonNull;

public interface PresenterFactory<T extends Presenter> {
    /**
     * Interface that defines the required method for a presenter factory to implement.
     * The presenter factory is responsible for creating a new instance of the presenter.
     *
     * @return Instance of the presenter
     */
    @NonNull T createPresenter();
}
