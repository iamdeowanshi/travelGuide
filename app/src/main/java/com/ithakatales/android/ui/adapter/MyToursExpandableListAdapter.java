package com.ithakatales.android.ui.adapter;

import java.io.File;
import java.util.List;
 
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

import com.google.common.base.Strings;
import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.Audio;
import com.ithakatales.android.data.model.AudioDownload;
import com.ithakatales.android.data.model.TourDownload;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.AudioRepository;
import com.ithakatales.android.data.repository.ImageDownloadRepository;
import com.ithakatales.android.download.DownloadStatus;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author farhanali
 */
public class MyToursExpandableListAdapter extends BaseExpandableListAdapter {
 
    @Inject Context context;
    @Inject LayoutInflater inflater;

    @Inject AttractionRepository attractionRepo;
    @Inject AudioRepository audioRepo;
    @Inject ImageDownloadRepository imageDownloadRepo;

    private List<TourDownload> tourDownloads;
 
    public MyToursExpandableListAdapter(List<TourDownload> tourDownloads) {
        Injector.instance().inject(this);

        this.tourDownloads = tourDownloads;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // Group related methods

    @Override
    public Object getGroup(int groupPosition) {
        return tourDownloads.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return tourDownloads.size();
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

        TourDownload tourDownload = (TourDownload) getGroup(groupPosition);
        viewHolder.bindData(tourDownload);
 
        return convertView;
    }

    // Child related methods

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return tourDownloads.get(groupPosition).getAudioDownloads().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // size + 2 : first for header "Download Progress" & last for total image download progress
        return tourDownloads.get(groupPosition).getAudioDownloads().size() + 2;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        // first row is used as header
        if(childPosition == 0) {
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

        AudioDownload audioDownload;
        try {
            audioDownload = (AudioDownload) getChild(groupPosition, childPosition - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            /*
             * will get exception on last row, which is used to show total image download progress,
             * so binding a dummy audio download object
             */
            audioDownload = new AudioDownload();
            audioDownload.setId(-1);
            audioDownload.setTourId(((TourDownload) getGroup(groupPosition)).getId());
        }

        viewHolder.bindData(audioDownload);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public class GroupViewHolder implements Target {
        @Bind(R.id.layout_item_container) RelativeLayout layoutItemContainer;
        @Bind(R.id.text_name) TextView textName;
        @Bind(R.id.text_caption) TextView textCaption;
        @Bind(R.id.text_progress) TextView textProgress;
        @Bind(R.id.progress) ProgressBar progress;

        public GroupViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

        public void bindData(TourDownload tourDownload) {
            Attraction attraction = attractionRepo.find(tourDownload.getId());
            Picasso.with(context)
                    .load(new File(attraction.getFeaturedImage().getPath()))
                    .placeholder(R.drawable.placeholder_ratio_3_2)
                    .error(R.drawable.placeholder_ratio_3_2)
                    .resize(600, 400)
                    .into(this);
            textName.setText(attraction.getName());
            textCaption.setText(attraction.getCaption());

            int progressValue = tourDownload.getProgress();
            textProgress.setText(progressValue + "%");
            int progressDrawableId = progressValue < 100 ? R.drawable.progress_tour_partial : R.drawable.progress_tour_full;
            progress.setProgressDrawable(ContextCompat.getDrawable(context, progressDrawableId));
            progress.setProgress(progressValue);
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
        @Bind(R.id.text_title) TextView textTitle;
        @Bind(R.id.progress) ProgressBar progress;

        public ChildViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

        public void bindData(AudioDownload audioDownload) {
            // audio download id will be -1 for last entry, there we will show total image download progress
            if (audioDownload.getId() == -1) {
                bindTotalImageDownloadProgress(audioDownload);
                return;
            }

            Audio audio = audioRepo.find(audioDownload.getAudioId());
            textTitle.setText(audio.getName());
            setProgress(audioDownload.getProgress());
        }

        private void bindTotalImageDownloadProgress(AudioDownload dummyAudioDownload) {
            int totalCount = imageDownloadRepo.readByTourId(dummyAudioDownload.getTourId()).size();
            int successCount = imageDownloadRepo.readByTourAndStatus(dummyAudioDownload.getTourId(), DownloadStatus.SUCCESS).size();
            String title = String.format("Images(%d / %d )", successCount, totalCount);
            textTitle.setText(title);
            setProgress(imageDownloadRepo.getTotalProgressByTour(dummyAudioDownload.getTourId()));
        }

        private void setProgress(int progressValue) {
            int progressDrawableId = progressValue < 100 ? R.drawable.progress_audio_partial : R.drawable.progress_audio_full;
            progress.setProgressDrawable(ContextCompat.getDrawable(context, progressDrawableId));
            progress.setProgress(progressValue);
        }
    }

}