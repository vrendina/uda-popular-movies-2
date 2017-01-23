package software.level.udacity.popularmovies2.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import software.level.udacity.popularmovies2.R;
import software.level.udacity.popularmovies2.api.model.MovieDetailsComposite;

public class MovieDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private MovieDetailsComposite data;

    private int trailerCount = 0;
    private int reviewCount = 0;

    public static final int VIEW_HEADER = 0;
    public static final int VIEW_TRAILER = 1;
    public static final int VIEW_REVIEW = 2;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if(viewType == VIEW_HEADER) {
            View view = inflater.inflate(R.layout.item_movie_detail_header, parent, false);
            return new MovieDetailHeaderViewHolder(view);
        }

        if(viewType == VIEW_TRAILER) {
            View view = inflater.inflate(R.layout.item_movie_trailer, parent, false);
            return new MovieTrailerViewHolder(view);
        }

        if(viewType == VIEW_REVIEW) {
            View view = inflater.inflate(R.layout.item_movie_review, parent, false);
            return new MovieReviewViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof MovieDetailHeaderViewHolder) {
            ((MovieDetailHeaderViewHolder) holder).title.setText("Testing");
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return VIEW_HEADER;
        }

        if(position > 0 && position < trailerCount) {
            return VIEW_TRAILER;
        }

        if(position > trailerCount && position < trailerCount + reviewCount) {
            return VIEW_REVIEW;
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return trailerCount + reviewCount + 1;
    }

    public void setMovieData(MovieDetailsComposite data) {
        this.data = data;

        this.trailerCount = data.trailerEnvelope.trailers.size();
        this.reviewCount = data.reviewEnvelope.reviews.size();

        notifyDataSetChanged();
    }

    public class MovieDetailHeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public MovieDetailHeaderViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.textView);
        }
    }

    public class MovieTrailerViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public MovieTrailerViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.textView);
        }
    }

    public class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public MovieReviewViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.textView);
        }
    }


}
