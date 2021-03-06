package com.ithakatales.android.ui.adapter;

import android.content.Context;
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
import com.ithakatales.android.data.model.IconMap;
import com.ithakatales.android.util.AttractionUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Farhan Ali
 */
public class ToursListRecyclerAdapter extends RecyclerView.Adapter<ToursListRecyclerAdapter.ViewHolder> {

    @Inject LayoutInflater inflater;
    @Inject Context context;

    private RecyclerItemClickListener<Attraction> itemClickListener;

    private List<Attraction> attractions;

    public ToursListRecyclerAdapter(List<Attraction> attractions, RecyclerItemClickListener<Attraction> itemClickListener) {
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
        @Bind(R.id.image_featured) ImageView imageFeatured;
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
            textCaption.setText(attraction.getCaption());
            iconType.setImageResource(IconMap.tourTypeLight.get(attraction.getType().getId()));
            textDuration.setText(AttractionUtil.attractionDurationToString(attraction.getDuration()));
            textDescription.setText(attraction.getShortDescription());

            // load background to item root view
            if (attraction.getFeaturedImage() == null) return;

            Picasso.with(context)
                    .load(attraction.getFeaturedImage().getUrl())
                    .placeholder(R.drawable.placeholder_ratio_3_2)
                    .error(R.drawable.placeholder_ratio_3_2)
                    .into(imageFeatured);
        }

    }

}
