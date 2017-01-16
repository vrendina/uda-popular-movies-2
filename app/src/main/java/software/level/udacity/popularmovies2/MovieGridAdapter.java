package software.level.udacity.popularmovies2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import software.level.udacity.popularmovies2.api.model.Movie;

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieGridAdapterViewHolder> {

    private ArrayList<Movie> movies;

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ViewHolder that holds the View for each list item
     */
    @Override
    public MovieGridAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate the layout for the parent view that contains the child views that are recycled
        View view = inflater.inflate(R.layout.item_movie_poster, parent, false);

        return new MovieGridAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     *
     * @param holder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieGridAdapterViewHolder holder, int position) {
        Movie movie = movies.get(position);

        ImageView poster = holder.poster;

//        Picasso.with(holder.poster.getContext())
//                .load(imageURL.toString())
//                .placeholder(R.drawable.poster_placeholder)
//                .into(holder.poster);

    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our data source
     */
    @Override
    public int getItemCount() {
        if(movies == null) {
            return 0;
        }
        return movies.size();
    }

    /**
     * Set the movie data and refresh the RecyclerView to display the new data
     * @param data An ArrayList of Movie objects
     */
    public void setMovieData(ArrayList<Movie> data) {
        movies = data;
        notifyDataSetChanged();
    }

    /**
     * Viewholder class to store references to recycled views. Class also passes along the onClick
     * event.
     */
    public class MovieGridAdapterViewHolder extends RecyclerView.ViewHolder {

        public ImageView poster;

        public MovieGridAdapterViewHolder(View view) {
            super(view);
            poster = (ImageView) view.findViewById(R.id.iv_movie_poster);
        }

    }
}
