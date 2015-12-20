package com.ithakatales.android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseFragment;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.presenter.TourListPresenter;
import com.ithakatales.android.presenter.TourListViewInteractor;
import com.ithakatales.android.ui.activity.TourDetailActivity;
import com.ithakatales.android.ui.adapter.RecyclerItemClickListener;
import com.ithakatales.android.ui.adapter.ToursListRecyclerAdapter;
import com.ithakatales.android.util.Bakery;

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

    @Bind(R.id.recycler_tours) RecyclerView recyclerTours;
    @Bind(R.id.progress) ProgressBar progress;

    private ToursListRecyclerAdapter toursListRecyclerAdapter;

    private List<Attraction> attractions = new ArrayList<>();

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

        presenter.loadAttractions(getArguments().getLong("city_id", 0));

        // TODO: 18/12/15 - dummy user - to be updated
        presenter.loadAttractionUpdates(User.dummy());
    }

    @Override
    public void attractionsLoaded(List<Attraction> attractions) {
        this.attractions.clear();
        this.attractions.addAll(attractions);
        toursListRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.INVISIBLE);
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

}
