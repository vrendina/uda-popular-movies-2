package software.level.udacity.popularmovies2.ui;

public class Presenter<T> {

    private T view;

    public void bindView(T view) { this.view = view; }
    public void unbindView() { this.view = null; }

}
