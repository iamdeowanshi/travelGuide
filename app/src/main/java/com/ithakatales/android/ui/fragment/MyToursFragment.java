package com.ithakatales.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseFragment;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.download.model.TourDownloadProgress;
import com.ithakatales.android.presenter.TourDetailPresenter;
import com.ithakatales.android.presenter.TourDetailViewInteractor;
import com.ithakatales.android.ui.actions.TourAction;
import com.ithakatales.android.ui.activity.LoginActivity;
import com.ithakatales.android.ui.activity.TourDetailActivity;
import com.ithakatales.android.ui.activity.TourPlayerActivity;
import com.ithakatales.android.ui.adapter.MyToursExpandableListAdapter;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.ConnectivityUtil;
import com.ithakatales.android.util.DialogUtil;
import com.ithakatales.android.util.UserPreference;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class MyToursFragment extends BaseFragment implements TourDetailViewInteractor, MyToursExpandableListAdapter.TourActionClickListener {

    @Inject TourDetailPresenter presenter;
    @Inject TourDownloader tourDownloader;
    @Inject AttractionRepository attractionRepo;

    @Inject Bakery bakery;
    @Inject DialogUtil dialogUtil;
    @Inject ConnectivityUtil connectivityUtil;
    @Inject UserPreference preference;

    @Bind(R.id.list_my_tours) ExpandableListView listMyTours;
    @Bind(R.id.layout_tour_empty) RelativeLayout layoutTourEmpty;
    @Bind(R.id.layout_not_logged) RelativeLayout layoutNotLoggedin;

    private MyToursExpandableListAdapter adapter;

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

        refreshAdapterAndView();
    }

    @Override
    public void onAttractionLoaded(Attraction attraction, int tourAction) {}

    @Override
    public void onDownloadProgressChange(TourDownloadProgress downloadProgress) {
        if ( ! isAdapterNotified) {
            refreshAdapter();
            isAdapterNotified = true;
        }
    }

    @Override
    public void onDownloadComplete(long attractionId) {}

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
                startTour(attraction);
                break;
            case TourAction.RETRY:
                retryDownload(attraction);
                break;
            case TourAction.UPDATE:
                showUpdateAvailableDialog(attraction);
                break;
            case TourAction.DELETE:
                showTourDeletedDialog(attraction);
                break;
        }
    }

    @Override
    public void onTourClick(Attraction attraction) {
        Bundle bundle = new Bundle();
        bundle.putLong("attraction_id", attraction.getId());
        startActivity(TourDetailActivity.class, bundle);
    }

    @Override
    public void onTourLongClick(final Attraction attraction) {
        dialogUtil.setDialogClickListener(new DialogUtil.DialogClickListener() {
            @Override
            public void onPositiveClick() {
                String attractionName = attraction.getName();
                tourDownloader.delete(attraction);
                bakery.toastShort(String.format("%s deleted", attractionName));
                refreshAdapterAndView();
            }

            @Override
            public void onNegativeClick() {
            }
        });

        dialogUtil.setTitle("Tour Delete")
                .setMessage(String.format("Are you sure to delete tour %s ?", attraction.getName()))
                .setPositiveButtonText("Delete")
                .setNegativeButtonText("Cancel")
                .show(getActivity());
    }

    @OnClick(R.id.button_login)
    void onLoginClick() {
        startActivity(LoginActivity.class, null);
    }

    public void refreshAdapterAndView() {
        refreshAdapter();
        refreshView();
    }

    private void refreshAdapter() {
        adapter = new MyToursExpandableListAdapter();
        adapter.setTourActionClickListener(this);
        listMyTours.setAdapter(adapter);
        adapter.updateProgressMap();
        adapter.notifyDataSetChanged();
    }

    private void refreshView() {
        changeViewIfToursEmpty();
        changeViewIfNotLoggedIn();
    }

    private void retryDownload(Attraction attraction) {
        if (!connectivityUtil.isConnected()) {
            bakery.toastShort("No network, Try later");
            return;
        }

        isAdapterNotified = false;
        presenter.retryDownloadAttraction(attraction);
    }

    private void startTour(Attraction attraction) {
        Bundle bundle = new Bundle();
        bundle.putLong("attraction_id", attraction.getId());
        startActivity(TourPlayerActivity.class, bundle);
    }

    private void showUpdateAvailableDialog(final Attraction attraction) {
        dialogUtil.setDialogClickListener(new DialogUtil.DialogClickListener() {
            @Override
            public void onPositiveClick() {
                if (!connectivityUtil.isConnected()) {
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
                presenter.deleteAttraction(attraction);
                refreshAdapterAndView();
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

    private boolean changeViewIfNotLoggedIn() {
        boolean isLoggedIn = preference.isLoggedIn();

        if ( ! isLoggedIn && layoutNotLoggedin.getVisibility() != View.VISIBLE) {
            layoutNotLoggedin.setVisibility(View.VISIBLE);
            layoutTourEmpty.setVisibility(View.GONE);
            listMyTours.setVisibility(View.GONE);
        }

        return isLoggedIn;
    }

    private boolean changeViewIfToursEmpty() {
        boolean isEmpty = attractionRepo.readAll().size() <= 0;

        if (isEmpty && layoutTourEmpty.getVisibility() != View.VISIBLE) {
            layoutTourEmpty.setVisibility(View.VISIBLE);
            layoutNotLoggedin.setVisibility(View.GONE);
            listMyTours.setVisibility(View.GONE);
        }

        if ( ! isEmpty && listMyTours.getVisibility() != View.VISIBLE) {
            layoutTourEmpty.setVisibility(View.GONE);
            layoutNotLoggedin.setVisibility(View.GONE);
            listMyTours.setVisibility(View.VISIBLE);
        }

        return isEmpty;
    }

}
