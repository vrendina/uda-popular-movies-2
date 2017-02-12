package software.level.udacity.popularmovies2.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import software.level.udacity.popularmovies2.R;
import software.level.udacity.popularmovies2.api.MovieServiceUtils;
import software.level.udacity.popularmovies2.api.model.Movie;
import software.level.udacity.popularmovies2.api.model.MovieReview;
import software.level.udacity.popularmovies2.api.model.MovieTrailer;

public class MovieDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = MovieDetailAdapter.class.getSimpleName();

    private Movie movie;
    private ArrayList<MovieTrailer> trailers;
    private ArrayList<MovieReview> reviews;

    private MovieDetailPresenter presenter;
    private MovieOnClickHandler clickHandler;

    private static final int VIEW_HEADER = 100;
    private static final int VIEW_TRAILER = 101;
    private static final int VIEW_REVIEW = 102;

    public MovieDetailAdapter(MovieDetailPresenter presenter, MovieOnClickHandler clickHandler) {
        this.presenter = presenter;
        this.clickHandler = clickHandler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view;
        switch(viewType) {
            case VIEW_HEADER:
                view = inflater.inflate(R.layout.item_movie_detail_header, parent, false);
                return new MovieDetailHeaderViewHolder(view);

            case VIEW_TRAILER:
                view = inflater.inflate(R.layout.item_movie_trailer, parent, false);
                return new MovieTrailerViewHolder(view);

            case VIEW_REVIEW:
                view = inflater.inflate(R.layout.item_movie_review, parent, false);
                return new MovieReviewViewHolder(view);

            default:
                Log.e(TAG, "onCreateViewHolder: Unknown viewType passed");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder untypedHolder, int position) {
        if(untypedHolder instanceof MovieDetailHeaderViewHolder) {
            bindHeaderViewHolder((MovieDetailHeaderViewHolder) untypedHolder);
        } else if(untypedHolder instanceof MovieTrailerViewHolder) {
            int trailerPosition = getTrailerPositionFromAdapterPosition(position);
            bindTrailerViewHolder((MovieTrailerViewHolder) untypedHolder, trailerPosition);
        } else if(untypedHolder instanceof MovieReviewViewHolder) {
            int reviewPosition = getReviewPositionFromAdapterPosition(position);
            bindReviewViewHolder((MovieReviewViewHolder) untypedHolder, reviewPosition);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return VIEW_HEADER;
        } else if(position > 0 && position <= trailers.size()) {
            return VIEW_TRAILER;
        }
        return VIEW_REVIEW;
    }

    @Override
    public int getItemCount() {
        if(movie == null) {
            return 0;
        }
        return 1 + trailers.size() + reviews.size();
    }

    public void setMovieData(Movie movie, ArrayList<MovieTrailer> trailers, ArrayList<MovieReview> reviews) {
        this.movie = movie;
        this.trailers = trailers;
        this.reviews = reviews;

        notifyDataSetChanged();
    }

    private int getTrailerPositionFromAdapterPosition(int adapterPosition) {
        return adapterPosition - 1;
    }

    private int getReviewPositionFromAdapterPosition(int adapterPosition) {
        return adapterPosition - trailers.size() - 1;
    }

    private void loadPosterImage(ImageView poster) {
        // If we have image data in the database decode it and display it
        if(movie.poster != null) {
            Log.d(TAG, "bindHeaderViewHolder: Movie contains poster data, decoding...");
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

    private void bindHeaderViewHolder(MovieDetailHeaderViewHolder holder) {
        Context context = holder.itemView.getContext();

        // Set the movie title and release year
        holder.title.setText(String.format(context.getString(R.string.details_movie_title),
                movie.title,
                Integer.valueOf(movie.releaseDate.split("-")[0])));

        // Set the rating
        holder.rating.setText(String.format(Locale.ENGLISH, "%.1f", movie.voteAverage));

        // Set the overview
        holder.overview.setText(movie.overview);

        // If the movie is a favorite set up the button appropriately
        if(presenter.isFavorite()) {
            holder.favorite.setText(context.getString(R.string.favorite_remove));
            holder.heart.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            holder.favorite.setText(context.getString(R.string.favorite_add));
            holder.heart.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }

        // Load the poster image
        loadPosterImage(holder.poster);
    }

    private void bindTrailerViewHolder(MovieTrailerViewHolder holder, int trailerPosition) {
        Context context = holder.itemView.getContext();

        try {
            MovieTrailer trailer = trailers.get(trailerPosition);

            // Set the title of the trailer
            holder.title.setText(trailer.name);

            // Set the video size (standard definition if less than 720p)
            String size = context.getString(R.string.trailer_high_definition);
            if (trailer.size < 720) {
                size = context.getString(R.string.trailer_standard_definition);
            }

            holder.size.setText(String.format(context.getString(R.string.trailer_size),
                    size, trailer.size));

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "onBindViewHolder: Requested trailer out of array bounds", e);
        }
    }

    private void bindReviewViewHolder(MovieReviewViewHolder holder, int reviewPosition) {
        Context context = holder.itemView.getContext();

        try {
            MovieReview review = reviews.get(reviewPosition);

            // Set the author header
            holder.title.setText(String.format(context.getString(R.string.review_title),
                    review.author));

            // Set the content of the review
            holder.review.setText(review.content);

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "onBindViewHolder: Requested review out of array bounds", e);
        }
    }

    private class MovieDetailHeaderViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView rating;
        final TextView favorite;
        final TextView overview;

        final ImageView poster;
        final ImageView heart;

        final LinearLayout favoriteButton;

        MovieDetailHeaderViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.tv_movie_title);
            favorite = (TextView) view.findViewById(R.id.tv_favorite);
            rating = (TextView) view.findViewById(R.id.tv_rating);
            overview = (TextView) view.findViewById(R.id.tv_overview);

            poster = (ImageView) view.findViewById(R.id.iv_movie_poster);
            heart = (ImageView) view.findViewById(R.id.iv_favorite_heart);

            favoriteButton = (LinearLayout) view.findViewById(R.id.ll_favorite_button);

            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // If we already are a favorite movie remove it from the database
                    if(presenter.isFavorite()) {
                        presenter.removeFavorite();

                    // Add the movie as a favorite
                    } else {

                        Drawable drawable = poster.getDrawable();

                        // If we have a bitmap drawable set for the poster, save the data
                        if(drawable instanceof BitmapDrawable) {
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                            presenter.setFavorite(bitmap);

                        // Don't save any image data if we don't have the bitmap set
                        } else {
                            presenter.setFavorite(null);
                        }
                    }
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }

    private class MovieTrailerViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView size;
        final ImageButton share;

        MovieTrailerViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.tv_trailer_title);
            size = (TextView) view.findViewById(R.id.tv_trailer_size);
            share = (ImageButton) view.findViewById(R.id.ib_share);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getTrailerPositionFromAdapterPosition(getAdapterPosition());
                    clickHandler.onTrailerClickPlay(trailers.get(position));
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getTrailerPositionFromAdapterPosition(getAdapterPosition());
                    clickHandler.onTrailerClickShare(trailers.get(position));
                }
            });
        }
    }

    private class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView review;

        MovieReviewViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.tv_review_title);
            review = (TextView) view.findViewById(R.id.tv_review);
        }
    }

    public interface MovieOnClickHandler {
        void onTrailerClickPlay(MovieTrailer trailer);
        void onTrailerClickShare(MovieTrailer trailer);
    }
}
