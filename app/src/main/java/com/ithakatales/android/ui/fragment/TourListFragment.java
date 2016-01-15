package com.ithakatales.android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseFragment;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.City;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.TourListPresenter;
import com.ithakatales.android.presenter.TourListViewInteractor;
import com.ithakatales.android.ui.activity.TourDetailActivity;
import com.ithakatales.android.ui.adapter.RecyclerItemClickListener;
import com.ithakatales.android.ui.adapter.ToursListRecyclerAdapter;
import com.ithakatales.android.ui.custom.NoNetworkView;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.UserPreference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class TourListFragment extends BaseFragment implements TourListViewInteractor, RecyclerItemClickListener<Attraction> {

    @Inject TourListPresenter presenter;
    @Inject Context context;
    @Inject Bakery bakery;
    @Inject UserPreference preference;

    @Bind(R.id.recycler_tours) RecyclerView recyclerTours;
    @Bind(R.id.view_loading) RelativeLayout viewLoading;
    @Bind(R.id.view_no_network) NoNetworkView viewNoNetwork;
    @Bind(R.id.layout_tour_empty) RelativeLayout viewTourEmpty;

    private ToursListRecyclerAdapter toursListRecyclerAdapter;

    private List<Attraction> attractions = new ArrayList<>();
    private City selectedCity;

    private NoNetworkView.NetworkRetryListener networkRetryListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
        presenter.setViewInteractor(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tour_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toursListRecyclerAdapter = new ToursListRecyclerAdapter(attractions, this);
        recyclerTours.setAdapter(toursListRecyclerAdapter);
        recyclerTours.setLayoutManager(new LinearLayoutManager(context));

        viewNoNetwork.setNetworkRetryListener(networkRetryListener);

        User user = preference.readUser();
        if (user != null) {
            presenter.loadAttractionUpdates(user);
        }
    }

    @Override
    public void attractionsLoaded(List<Attraction> attractions) {
        viewTourEmpty.setVisibility(View.GONE);
        this.attractions.clear();
        this.attractions.addAll(attractions);
        toursListRecyclerAdapter.notifyDataSetChanged();

        if (attractions.size() <= 0) {
            viewTourEmpty.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void showProgress() {
        viewLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        viewLoading.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkError(Throwable e) {
        Timber.e(e.getMessage(), e);
        bakery.toastShort(e.getMessage());
    }

    @Override
    public void onItemClick(Attraction item) {
        Bundle bundle = new Bundle();
        bundle.putLong("attraction_id", item.getId());
        startActivity(TourDetailActivity.class, bundle);
    }

    public void onCitySelectionChanged(City city) {
        selectedCity = city;
        presenter.loadAttractions(city.getId());
    }

    public void setNetworkRetryListener(NoNetworkView.NetworkRetryListener networkRetryListener) {
        this.networkRetryListener = networkRetryListener;
    }

}
