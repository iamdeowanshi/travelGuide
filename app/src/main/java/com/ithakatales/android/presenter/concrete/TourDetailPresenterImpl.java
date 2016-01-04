package com.ithakatales.android.presenter.concrete;

import android.app.DownloadManager;
import android.os.Handler;
import android.os.Looper;

import com.ithakatales.android.app.base.BaseNetworkPresenter;
import com.ithakatales.android.data.api.ApiModels;
import com.ithakatales.android.data.api.ApiObserver;
import com.ithakatales.android.data.api.IthakaApi;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.AttractionUpdate;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.data.repository.AttractionUpdateRepository;
import com.ithakatales.android.download.TourDownloadProgressListener;
import com.ithakatales.android.download.TourDownloadProgressReader;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.download.model.TourDownloadProgress;
import com.ithakatales.android.presenter.TourDetailPresenter;
import com.ithakatales.android.presenter.TourDetailViewInteractor;
import com.ithakatales.android.ui.actions.TourAction;
import com.ithakatales.android.util.Bakery;

import javax.inject.Inject;

import retrofit.client.Response;
import rx.Observable;

/**
 * @author Farhan Ali
 */
public class TourDetailPresenterImpl extends BaseNetworkPresenter<TourDetailViewInteractor>
        implements TourDetailPresenter {

    @Inject IthakaApi api;
    @Inject TourDownloader tourDownloader;
    @Inject TourDownloadProgressReader progressReader;

    @Inject AttractionRepository attractionRepo;
    @Inject AttractionUpdateRepository attractionUpdateRepo;

    @Inject Bakery bakery;

    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    private TourDownloadProgressListener progressListener = new TourDownloadProgressListener() {
        @Override
        public void onProgressChange(final TourDownloadProgress tourDownloadProgress) {
            if (tourDownloadProgress == null) return;

            // run on main thread - called from another thread
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    viewInteractor.onDownloadProgressChange(tourDownloadProgress);

                    if (tourDownloadProgress.getProgress() >= 100) {
                        tourDownloader.stopProgressListening(tourDownloadProgress.getAttractionId());
                        loadAttraction(tourDownloadProgress.getAttractionId());
                    }
                }
            });
        }
    };

    public TourDetailPresenterImpl() {
        injectDependencies();
    }

    @Override
    public void loadAttraction(long attractionId) {
        viewInteractor.showProgress();

        Attraction attraction = attractionRepo.find(attractionId);

        if (attraction == null) {
            loadAttractionFromApi(attractionId);
            return;
        }

        TourDownloadProgress downloadProgress = progressReader.readProgress(attractionId);

        switch (downloadProgress.getStatus()) {
            case DownloadManager.STATUS_FAILED:
                viewInteractor.onAttractionLoaded(attraction, TourAction.RETRY);
                break;
            case DownloadManager.STATUS_RUNNING:
                viewInteractor.onAttractionLoaded(attraction, TourAction.DOWNLOADING);
                tourDownloader.startProgressListening(attraction.getId(), progressListener);
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                loadAttractionAfterUpdateAndDeleteCheck(attraction);
                break;
        }

        viewInteractor.hideProgress();
    }

    @Override
    public void downloadAttraction(final Attraction attraction) {
        tourDownloader.download(attraction);
        tourDownloader.startProgressListening(attraction.getId(), progressListener);

        // TODO: 21/12/15 dummy user
        updateAttractionDownload(User.dummy(), attraction.getId());
    }

    @Override
    public void retryDownloadAttraction(Attraction attraction) {
        tourDownloader.retryDownload(attraction);
        tourDownloader.startProgressListening(attraction.getId(), progressListener);
    }

    @Override
    public void updateAttraction(final Attraction attraction) {
        Observable<Attraction> observable = api.getAttraction(attraction.getId());

        subscribeForNetwork(observable, new ApiObserver<Attraction>() {
            @Override
            public void onResult(Attraction result) {
                tourDownloader.update(result);
                tourDownloader.startProgressListening(result.getId(), progressListener);
            }

            @Override
            public void onError(Throwable e) {
                viewInteractor.onNetworkError(e);
                viewInteractor.hideProgress();
            }
        });
    }

    @Override
    public void deleteAttraction(Attraction attraction) {
        bakery.toastShort("Deleting Attraction");
        tourDownloader.delete(attraction);
    }

    private void loadAttractionFromApi(final long attractionId) {
        Observable<Attraction> observable = api.getAttraction(attractionId);

        subscribeForNetwork(observable, new ApiObserver<Attraction>() {
            @Override
            public void onResult(Attraction result) {
                viewInteractor.onAttractionLoaded(result, TourAction.DOWNLOAD);
                viewInteractor.hideProgress();

                // TODO: 21/12/15 dummy user
                updateAttractionView(User.dummy(), attractionId);
            }

            @Override
            public void onError(Throwable e) {
                viewInteractor.onNetworkError(e);
                viewInteractor.hideProgress();
            }
        });
    }

    private void loadAttractionAfterUpdateAndDeleteCheck(Attraction attraction) {
        AttractionUpdate attractionUpdate = attractionUpdateRepo.find(attraction.getId());

        // if deleted
        if (attractionUpdate != null && attractionUpdate.getDeletedAt() != null) {
            viewInteractor.onAttractionLoaded(attraction, TourAction.DELETE);
            return;
        }

        // if updated
        if (attractionUpdate != null && ! attractionUpdate.getUpdatedAt().equals(attraction.getUpdatedAt())) {
            viewInteractor.onAttractionLoaded(attraction, TourAction.UPDATE);
            return;
        }

        viewInteractor.onAttractionLoaded(attraction, TourAction.START);
    }

    private void updateAttractionView(User user, long attractionId) {
        ApiModels.AttractionViewedRequest requestBody = new ApiModels.AttractionViewedRequest();
        Observable<Response> observable = api.attractionViewed(user.getAccessToken(), requestBody);

        subscribeForNetwork(observable, new ApiObserver<Response>() {
            @Override
            public void onResult(Response response) {
            }

            @Override
            public void onError(Throwable e) {
                viewInteractor.onNetworkError(e);
            }
        });
    }

    private void updateAttractionDownload(User user, long attractionId) {
        ApiModels.AttractionDownloadedRequest requestBody = new ApiModels.AttractionDownloadedRequest();
        requestBody.userId = user.getId();
        requestBody.attractionId = attractionId;

        Observable<Response> observable = api.attractionDownloaded(user.getAccessToken(), requestBody);

        subscribeForNetwork(observable, new ApiObserver<Response>() {
            @Override
            public void onResult(Response response) {
            }

            @Override
            public void onError(Throwable e) {
                viewInteractor.onNetworkError(e);
            }
        });
    }

}
