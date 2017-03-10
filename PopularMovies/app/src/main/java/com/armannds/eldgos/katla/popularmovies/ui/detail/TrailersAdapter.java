package com.armannds.eldgos.katla.popularmovies.ui.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.armannds.eldgos.katla.popularmovies.R;
import com.armannds.eldgos.katla.popularmovies.data.Trailer;
import com.armannds.eldgos.katla.popularmovies.ui.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    private Context mContext;
    private List<Trailer> mTrailers;
    private OnItemClickListener<Trailer> mOnItemClickListener;

    public TrailersAdapter(Context context, OnItemClickListener clickListener) {
        mContext = context;
        mOnItemClickListener = clickListener;
    }

    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        int listItemLayoutId = R.layout.trailer_list_item;
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(listItemLayoutId, parent, shouldAttachToParentImmediately);
        return new TrailersAdapter.TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrailersAdapterViewHolder holder, int position) {
        Trailer trailer = mTrailers.get(position);
        holder.mTrailerName.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return (mTrailers == null) ? 0 : mTrailers.size();
    }

    public List<Trailer> getTrailers() {
        return mTrailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_trailer_name)
        TextView mTrailerName;

        public TrailersAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int currentPosition = getAdapterPosition();
            Trailer currentTrailer = mTrailers.get(currentPosition);
            mOnItemClickListener.onItemClicked(v, currentTrailer);
        }
    }
}
