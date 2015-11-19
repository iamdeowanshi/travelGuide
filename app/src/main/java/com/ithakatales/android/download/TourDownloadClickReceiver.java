package com.ithakatales.android.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ithakatales.android.download.manager.Downloadable;
import com.ithakatales.android.download.manager.Downloader;

import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class TourDownloadClickReceiver extends BroadcastReceiver {

    private TourIdProvider tourIdProvider;

    public TourDownloadClickReceiver(TourIdProvider tourIdProvider) {
        this.tourIdProvider = (tourIdProvider != null)
                ? tourIdProvider
                : TourIdProvider.DEFAULT;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("Tour download clicked: " + intent.getStringExtra(Downloader.EXTRA_TITLE));

        Downloadable downloadable = new Downloadable();
        downloadable.setId(intent.getLongExtra(Downloader.EXTRA_ID, 0));
        downloadable.setUrl(intent.getStringExtra(Downloader.EXTRA_URL));

        long tourId = tourIdProvider.getTourIdByDownloadable(downloadable);

        // broadcast intent for tour download click
        Intent tourActivityIntent = new Intent(TourDownloadService.ACTION_VIEW_TOUR);
        tourActivityIntent.putExtra(TourDownloadService.EXTRA_TOUR_ID, tourId);
        tourActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(tourActivityIntent);
    }

    static interface TourIdProvider {

        TourIdProvider DEFAULT = new TourIdProvider() {
            @Override
            public long getTourIdByDownloadable(Downloadable downloadable) {
                return 0;
            }
        };

        long getTourIdByDownloadable(Downloadable downloadable);

    }

}
