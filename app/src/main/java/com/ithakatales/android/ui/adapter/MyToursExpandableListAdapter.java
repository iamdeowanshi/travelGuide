package com.ithakatales.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.AudioRepository;
import com.ithakatales.android.download.TourDownloadProgressListener;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.download.model.AudioDownloadProgress;
import com.ithakatales.android.download.model.TourDownloadProgress;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author farhanali
 */

public class MyToursExpandableListAdapter extends BaseExpandableListAdapter implements TourDownloadProgressListener {

    @Inject Context context;
    @Inject LayoutInflater inflater;

    @Inject AttractionRepository attractionRepo;
    @Inject AudioRepository audioRepo;

    @Inject TourDownloader tourDownloader;

    private List<Attraction> attractions;
    private Map<Long, TourDownloadProgress> downloadProgressMap = new HashMap<>();

    public MyToursExpandableListAdapter(List<Attraction> attractions) {
        Injector.instance().inject(this);
        this.attractions = attractions;

        for (Attraction attraction : attractions) {
            downloadProgressMap.put(attraction.getId(), tourDownloader.readProgress(attraction.getId()));
        }
    }

    @Override
    public void onProgressChange(final TourDownloadProgress tourDownloadProgress) {

    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // Group related methods

    @Override
    public Object getGroup(int groupPosition) {
        return attractions.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return attractions.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_mytours_group, null);
            viewHolder = new GroupViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }

        Attraction attraction = (Attraction) getGroup(groupPosition);
        viewHolder.bindData(attraction);

        return convertView;
    }

    // Child related methods

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Attraction attraction = attractions.get(groupPosition);

        return downloadProgressMap.get(attraction.getId()).getAudioDownloadProgresses().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Attraction attraction = attractions.get(groupPosition);
        // size + 2 : first for header "Download Progress" & last for total image download progress
        return downloadProgressMap.get(attraction.getId()).getAudioDownloadProgresses().size() + 2;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        // first row is used as header
        if (childPosition == 0) {
            return inflater.inflate(R.layout.list_item_mytours_child_header, null);
        }

        /*
         * getting some unexpected situation where convertView.getTag() is null other than on fist position,
         * that makes viewHolder object null, so some not so interesting condition checks.. !!
         */
        ChildViewHolder viewHolder = (convertView != null)
                ? (ChildViewHolder) convertView.getTag()
                : null;

        if (convertView == null || viewHolder == null) {
            convertView = inflater.inflate(R.layout.list_item_mytours_child, null);
            viewHolder = new ChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        AudioDownloadProgress audioDownload;
        try {
            audioDownload = (AudioDownloadProgress) getChild(groupPosition, childPosition - 1);
            } catch (Exception e) {
            /*
             * will get exception on last row, which is used to show total image download progress,
             * so binding a dummy audio download object
             */
            audioDownload = new AudioDownloadProgress();
            audioDownload.setProgress(-1);
        }

        viewHolder.bindData(audioDownload, attractions.get(groupPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public class GroupViewHolder implements Target {
        @Bind(R.id.layout_item_container)
        RelativeLayout layoutItemContainer;
        @Bind(R.id.text_name)
        TextView textName;
        @Bind(R.id.text_caption)
        TextView textCaption;
        @Bind(R.id.text_progress)
        TextView textProgress;
        @Bind(R.id.progress)
        ProgressBar progress;

        public GroupViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

        public void bindData(Attraction attraction) {

            textName.setText(attraction.getName());
            textCaption.setText(attraction.getCaption());

            TourDownloadProgress downloadProgress = downloadProgressMap.get(attraction.getId());
            textProgress.setText(downloadProgress.getProgress() + "%");
            int progressDrawableId = downloadProgress.getProgress() < 100 ? R.drawable.progress_tour_partial : R.drawable.progress_tour_full;
            progress.setProgressDrawable(ContextCompat.getDrawable(context, progressDrawableId));
            progress.setProgress(downloadProgress.getProgress());

            Picasso.with(context)
                    .load(new File(attraction.getFeaturedImage().getPath()))
                    .placeholder(R.drawable.placeholder_ratio_3_2)
                    .error(R.drawable.placeholder_ratio_3_2)
                    .resize(600, 400)
                    .into(this);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            layoutItemContainer.setBackground(new BitmapDrawable(context.getResources(), bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            layoutItemContainer.setBackground(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            layoutItemContainer.setBackground(placeHolderDrawable);
        }
    }

    public class ChildViewHolder {
        @Bind(R.id.text_title)
        TextView textTitle;
        @Bind(R.id.progress)
        ProgressBar progress;

        public ChildViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

        public void bindData(AudioDownloadProgress audioDownload, Attraction attraction) {
            // audio download progress will be -1 for last entry, there we will show total image download progress
            if (audioDownload.getProgress() == -1) {
                bindTotalImageDownloadProgress(attraction);
                return;
            }

            textTitle.setText(audioDownload.getAudioName());
            setProgress(audioDownload.getProgress());
        }

        private void bindTotalImageDownloadProgress(Attraction attraction) {
            TourDownloadProgress progress = downloadProgressMap.get(attraction.getId());
            textTitle.setText(String.format("Images (%d/%d)", progress.getDownloadedImageCount(), progress.getImageDownloadProgresses().size()));
            setProgress(progress.getImageProgress());
        }

        private void setProgress(int progressValue) {
            int progressDrawableId = progressValue < 100 ? R.drawable.progress_audio_partial : R.drawable.progress_audio_full;
            progress.setProgressDrawable(ContextCompat.getDrawable(context, progressDrawableId));
            progress.setProgress(progressValue);
        }
    }

}
