package com.ithakatales.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.util.AttractionUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Farhan Ali
 */
public class PlayListRecyclerAdapter extends RecyclerView.Adapter<PlayListRecyclerAdapter.ViewHolder> {

    @Inject LayoutInflater inflater;
    @Inject Context context;

    private PlaylistItemClickListener itemClickListener;
    private List<Audio> audios;
    private View lastItemSelected;
    private int selectedItemPosition;

    public PlayListRecyclerAdapter(List<Audio> audios, PlaylistItemClickListener itemClickListener) {
        Injector.instance().inject(this);
        this.audios = audios;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_tour_playlist, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    public void setSelectedItemPosition(int itemPosition) {
        this.selectedItemPosition = itemPosition;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.text_name) TextView textName;
        @Bind(R.id.text_duration) TextView textDuration;

        private int position;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onAudioItemClick(position);
                }
            });
        }

        public void bindData(int position) {
            this.position = position;
            Audio audio = audios.get(position);
            textName.setText(audio.getName());
            textDuration.setText(AttractionUtil.audioDurationToString(audio.getDuration()));

            updateItemSelection(position);
        }

        private void updateItemSelection(int position) {
            itemView.setSelected(position == selectedItemPosition);

            if (position != selectedItemPosition) return;

            if (lastItemSelected != null) {
                lastItemSelected.setSelected(false);
            }

            lastItemSelected = itemView;
        }

    }

}
