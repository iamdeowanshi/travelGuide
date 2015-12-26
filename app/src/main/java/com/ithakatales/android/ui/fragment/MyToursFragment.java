package com.ithakatales.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseFragment;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.download.model.TourDownloadProgress;
import com.ithakatales.android.presenter.TourDetailPresenter;
import com.ithakatales.android.presenter.TourDetailViewInteractor;
import com.ithakatales.android.ui.actions.TourAction;
import com.ithakatales.android.ui.adapter.MyToursExpandableListAdapter;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.ConnectivityUtil;
import com.ithakatales.android.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class MyToursFragment extends BaseFragment implements TourDetailViewInteractor, MyToursExpandableListAdapter.TourActionClickListener {

    @Inject AttractionRepository attractionRepo;
    @Inject TourDownloader tourDownloader;

    @Inject Bakery bakery;
    @Inject DialogUtil dialogUtil;
    @Inject ConnectivityUtil connectivityUtil;

    @Inject TourDetailPresenter presenter;

    @Bind(R.id.list_my_tours) ExpandableListView listMyTours;

    private MyToursExpandableListAdapter adapter;
    private List<Attraction> attractions = new ArrayList<>();

    private boolean isAdapterNotified;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
        presenter.setViewInteractor(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mytours, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        attractions = attractionRepo.readAll();
        adapter = new MyToursExpandableListAdapter(attractions);
        adapter.setTourActionClickListener(this);
        listMyTours.setAdapter(adapter);
    }

    @Override
    public void onAttractionLoaded(Attraction attraction, int tourAction) {}

    @Override
    public void onDownloadProgressChange(TourDownloadProgress downloadProgress) {
        if ( ! isAdapterNotified) {
            updateAdapter();
            isAdapterNotified = true;
        }
    }

    @Override
    public void onNoNetwork() {
        bakery.toastShort("No Network");
    }

    @Override
    public void showProgress() {}

    @Override
    public void hideProgress() {}

    @Override
    public void onNetworkError(Throwable e) {
        bakery.toastShort(e.getMessage());
        Timber.e(e.getMessage(), e);
    }

    @Override
    public void onTourActionClick(Attraction attraction, int tourAction) {
        switch (tourAction) {
            case TourAction.START:
                bakery.toastShort("Under development");
                break;
            case TourAction.RETRY:
                tourDownloader.retryDownload(attraction);
                break;
            case TourAction.UPDATE:
                showUpdateAvailableDialog(attraction);
                break;
            case TourAction.DELETE:
                showTourDeletedDialog(attraction);
                break;
        }
    }

    public void updateAdapter() {
        adapter.updateProgressMap();
        adapter.notifyDataSetChanged();
    }

    private void showUpdateAvailableDialog(final Attraction attraction) {
        dialogUtil.setDialogClickListener(new DialogUtil.DialogClickListener() {
            @Override
            public void onPositiveClick() {
                if ( ! connectivityUtil.isConnected()) {
                    bakery.toastShort("No network, Try later");
                    return;
                }

                isAdapterNotified = false;
                bakery.toastShort("Updating..");
                presenter.updateAttraction(attraction);
            }

            @Override
            public void onNegativeClick() {
                startTour(attraction);
            }
        });

        dialogUtil.setTitle("Update Available !")
                .setMessage("Some updates are made to this tour, would you like to updated it now ?")
                .setPositiveButtonText("Update")
                .setNegativeButtonText("Continue")
                .show(getActivity());
    }

    private void showTourDeletedDialog(final Attraction attraction) {
        dialogUtil.setDialogClickListener(new DialogUtil.DialogClickListener() {
            @Override
            public void onPositiveClick() {
                bakery.toastShort("Deleting..");
                tourDownloader.delete(attraction);
            }

            @Override
            public void onNegativeClick() {
                startTour(attraction);
            }
        });

        dialogUtil.setTitle("Tour Removed !")
                .setMessage("This tour is no longer exist in our database, would you like to remove it now ?")
                .setPositiveButtonText("Remove")
                .setNegativeButtonText("Continue")
                .show(getActivity());
    }

    private void startTour(Attraction attraction) {
        bakery.toastShort("Under Development !");
    }

}
