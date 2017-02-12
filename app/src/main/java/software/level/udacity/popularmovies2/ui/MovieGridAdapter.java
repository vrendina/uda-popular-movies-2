package software.level.udacity.popularmovies2.ui;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import software.level.udacity.popularmovies2.R;
import software.level.udacity.popularmovies2.api.MovieServiceUtils;
import software.level.udacity.popularmovies2.api.model.Movie;

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieGridAdapterViewHolder> {

    // Movie data that is displayed in the RecyclerView
    private ArrayList<Movie> movies;

    // Click handler for selection of movies
    private MovieClickHandler clickHandler;

    public MovieGridAdapter(MovieClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public MovieGridAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate the layout for the parent view that contains the child views that are recycled
        View view = inflater.inflate(R.layout.item_movie_poster, parent, false);

        return new MovieGridAdapterViewHolder(view);
    }


    private void loadPosterImage(ImageView poster, Movie movie) {
        // If we have image data in the database decode it and display it
        if(movie.poster != null) {
            Bitmap bitmap = MovieServiceUtils.decodeImageData(movie.poster);
            poster.setImageBitmap(bitmap);

            // If there is no image data saved, load it from the web
        } else {
            String imageUrl = MovieServiceUtils.buildImageURL(movie.posterPath,
                    poster.getContext().getString(R.string.poster_size)).toString();

            Picasso.with(poster.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.poster_placeholder)
                    .into(poster);
        }
    }

    @Override
    public void onBindViewHolder(MovieGridAdapterViewHolder holder, int position) {
        Movie movie = movies.get(position);
        loadPosterImage(holder.poster, movie);
    }

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

    public class MovieGridAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView poster;

        public MovieGridAdapterViewHolder(View view) {
            super(view);
            poster = (ImageView) view.findViewById(R.id.iv_movie_poster);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Movie clickedMovie = movies.get(getAdapterPosition());
            clickHandler.onClickMovie(clickedMovie);
        }
    }

    /**
     * Interface that defines what a movie click handler object should implement
     */
    public interface MovieClickHandler {
        void onClickMovie(Movie movie);
    }
}
