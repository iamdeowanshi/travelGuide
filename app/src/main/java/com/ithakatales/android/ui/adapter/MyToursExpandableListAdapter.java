package com.ithakatales.android.ui.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.AttractionUpdate;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.AttractionUpdateRepository;
import com.ithakatales.android.download.TourDownloadProgressListener;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.download.model.AudioDownloadProgress;
import com.ithakatales.android.download.model.TourDownloadProgress;
import com.ithakatales.android.ui.actions.TourAction;
import com.ithakatales.android.util.Bakery;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author farhanali
 */
public class MyToursExpandableListAdapter extends BaseExpandableListAdapter implements TourDownloadProgressListener {

    @Inject Context context;
    @Inject LayoutInflater inflater;

    @Inject TourDownloader tourDownloader;
    @Inject AttractionRepository attractionRepo;
    @Inject AttractionUpdateRepository attractionUpdateRepo;

    private TourActionClickListener tourActionClickListener;

    @Inject Bakery bakery;

    private List<Attraction> attractions;
    private Map<Long, TourDownloadProgress> downloadProgressMap = new HashMap<>();

    public MyToursExpandableListAdapter() {
        Injector.instance().inject(this);
        this.attractions = attractionRepo.readAll();
        updateProgressMap();
    }

    public void setTourActionClickListener(TourActionClickListener tourActionClickListener) {
        this.tourActionClickListener = tourActionClickListener;
    }

    @Override
    public void onProgressChange(final TourDownloadProgress download) {
        if (download == null) return;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                downloadProgressMap.put(download.getAttractionId(), download);
                notifyDataSetChanged();

                Timber.d(String.format("attraction: %d | total: %d | downloaded: %d | progress: %d | status: %d",
                        download.getAttractionId(), download.getBytesTotal(), download.getBytesDownloaded(),
                        download.getProgress(), download.getStatus()));

                if (download.getProgress() >= 100) {
                    tourDownloader.stopProgressListening(download.getAttractionId());
                }
            }
        });
    }

    public void updateProgressMap() {
        for (Attraction attraction : attractions) {
            TourDownloadProgress download = tourDownloader.readProgress(attraction.getId());
            downloadProgressMap.put(attraction.getId(), download);

            if (download.getProgress() < 100 && download.getStatus() == DownloadManager.STATUS_RUNNING) {
                tourDownloader.startProgressListening(attraction.getId(), this);
            }
        }
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
            convertView = inflater.inflate(R.layout.list_item_mytours_group, parent, false);
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
        TourDownloadProgress download = downloadProgressMap.get(attraction.getId());

        return download.getAudioDownloadProgresses().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Attraction attraction = attractions.get(groupPosition);
        TourDownloadProgress download = downloadProgressMap.get(attraction.getId());

        if (download.getStatus() != DownloadManager.STATUS_RUNNING) {
            return 0;
        }

        // size + 2 : first for header "TourDownloadAction Progress" & last for total image download progress
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
        @Bind(R.id.layout_item_container) RelativeLayout layoutItemContainer;
        @Bind(R.id.text_name) TextView textName;
        @Bind(R.id.text_caption) TextView textCaption;
        @Bind(R.id.text_progress) TextView textProgress;
        @Bind(R.id.progress) ProgressBar progress;
        @Bind(R.id.button_tour_action) Button buttonTourAction;

        private Attraction attraction;
        private int tourAction;

        public GroupViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

        public void bindData(Attraction attraction) {
            this.attraction = attraction;

            textName.setText(attraction.getName());
            textCaption.setText(attraction.getCaption());

            RequestCreator requestCreator = Picasso.with(context).load(new File(attraction.getFeaturedImage().getPath()));
            TourDownloadProgress download = downloadProgressMap.get(attraction.getId());

            switch (download.getStatus()) {
                case DownloadManager.STATUS_SUCCESSFUL:
                    tourAction = checkForUpdateOrDelete(attraction);
                    showTourActionButton("Start Tour", R.drawable.ic_button_start_tour, R.drawable.bg_button_teal_rounded, android.R.color.white);
                    break;
                case DownloadManager.STATUS_FAILED:
                    tourAction = TourAction.RETRY;
                    showTourActionButton("Retry", R.drawable.ic_button_retry, R.drawable.bg_button_gray_rounded, R.color.primary);
                    break;
                case DownloadManager.STATUS_RUNNING:
                    tourAction = TourAction.DOWNLOADING;
                    requestCreator = Picasso.with(context).load(attraction.getFeaturedImage().getUrl());
                    showProgressView(download.getProgress());
                    break;
            }

            // TODO: 16/12/15 chance path to be null when downloading
            requestCreator.placeholder(R.drawable.placeholder_ratio_3_2)
                    .error(R.drawable.placeholder_ratio_3_2)
                    .resize(600, 400)
                    .into(this);
        }

        @OnClick(R.id.button_tour_action)
        void onTourActionClick() {
            if (tourActionClickListener != null) {
                tourActionClickListener.onTourActionClick(attraction, tourAction);
            }
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

        private int checkForUpdateOrDelete(Attraction attraction) {
            AttractionUpdate attractionUpdate = attractionUpdateRepo.find(attraction.getId());
            // if deleted
            if (attractionUpdate != null && attractionUpdate.getDeletedAt() != null) {
                return TourAction.DELETE;
            }

            // if updated
            if (attractionUpdate != null && ! attractionUpdate.getUpdatedAt().equals(attraction.getUpdatedAt())) {
                return TourAction.UPDATE;
            }

            return TourAction.START;
        }

        private void showTourActionButton(String text, int leftDrawableId, int backgroundDrawableId, int textColorId) {
            hideProgressView();

            buttonTourAction.setVisibility(View.VISIBLE);
            buttonTourAction.setBackground(ContextCompat.getDrawable(context, backgroundDrawableId));
            buttonTourAction.setTextColor(ContextCompat.getColor(context, textColorId));
            buttonTourAction.setAllCaps(false);

            Spannable buttonLabel = new SpannableString("   " + text.toUpperCase());
            buttonLabel.setSpan(new ImageSpan(context, leftDrawableId,
                    ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            buttonTourAction.setText(buttonLabel);
        }

        private void showProgressView(int progressValue) {
            hidTourActionButton();

            textProgress.setVisibility(View.VISIBLE);
            progress.setVisibility(View.VISIBLE);
            textProgress.setText(String.format("%d%%", progressValue));
            int progressDrawableId = progressValue < 100 ? R.drawable.progress_tour_partial : R.drawable.progress_tour_full;
            progress.setProgressDrawable(ContextCompat.getDrawable(context, progressDrawableId));
            progress.setProgress(progressValue);
        }

        private void hidTourActionButton() {
            buttonTourAction.setVisibility(View.INVISIBLE);
        }

        private void hideProgressView() {
            textProgress.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
        }
    }

    public class ChildViewHolder {
        @Bind(R.id.text_title) TextView textTitle;
        @Bind(R.id.progress) ProgressBar progress;

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

    public static interface TourActionClickListener {
        void onTourActionClick(Attraction attraction, int tourAction);
    }

}
