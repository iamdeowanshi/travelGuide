package com.ithakatales.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.AttractionType;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Farhan Ali
 */
public class ToursListAdapter extends RecyclerView.Adapter<ToursListAdapter.ViewHolder> {

    @Inject LayoutInflater inflater;
    @Inject Context context;

    private RecyclerItemClickListener<Attraction> itemClickListener;

    private List<Attraction> attractions;

    public ToursListAdapter(List<Attraction> attractions, RecyclerItemClickListener<Attraction> itemClickListener) {
        Injector.instance().inject(this);
        this.attractions = attractions;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_tours, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(attractions.get(position));
    }

    @Override
    public int getItemCount() {
        return attractions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.layout_item_container) RelativeLayout layoutItemContainer;
        @Bind(R.id.text_name) TextView textName;
        @Bind(R.id.text_caption) TextView textCaption;
        @Bind(R.id.icon_type) ImageView iconType;
        @Bind(R.id.text_duration) TextView textDuration;
        @Bind(R.id.text_description) TextView textDescription;

        private Attraction attraction;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(attraction);
                }
            });
        }

        public void bindData(Attraction attraction) {
            this.attraction = attraction;

            textName.setText(attraction.getName());
            // TODO: 27/11/15 set caption text - textCaption
            int typeIconResource = attraction.getType().getId() == AttractionType.WALKING
                    ? R.drawable.icon_walk
                    : R.drawable.icon_view;
            iconType.setImageResource(typeIconResource);
            int durationInMinute = (int) (attraction.getDuration() / 60);
            textDuration.setText(durationInMinute + " Mins");
            textDescription.setText(attraction.getShortDescription());

            // load background to item root view
            Picasso.with(context).load(attraction.getFeaturedImage().getUrl()).resize(400, 300).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    layoutItemContainer.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });
        }
    }

}
