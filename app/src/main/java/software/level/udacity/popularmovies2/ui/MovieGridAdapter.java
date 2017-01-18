package software.level.udacity.popularmovies2.ui;

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

    // Holds the movie data that is displayed in the RecyclerView
    private ArrayList<Movie> movies;

    @Override
    public MovieGridAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate the layout for the parent view that contains the child views that are recycled
        View view = inflater.inflate(R.layout.item_movie_poster, parent, false);

        return new MovieGridAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MovieGridAdapterViewHolder holder, int position) {
        Movie movie = movies.get(position);
        String imageUrl = MovieServiceUtils.buildImageURL(movie.posterPath, "w185").toString();

        Picasso.with(holder.poster.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.poster_placeholder)
                .into(holder.poster);
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

    public class MovieGridAdapterViewHolder extends RecyclerView.ViewHolder {

        public ImageView poster;

        public MovieGridAdapterViewHolder(View view) {
            super(view);
            poster = (ImageView) view.findViewById(R.id.iv_movie_poster);
        }

    }
}
