package com.ithakatales.android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseFragment;
import com.ithakatales.android.data.model.City;
import com.ithakatales.android.presenter.NavigationDrawerPresenter;
import com.ithakatales.android.presenter.NavigationDrawerViewInteractor;
import com.ithakatales.android.ui.adapter.NavigationDrawerAdapter;
import com.ithakatales.android.ui.adapter.RecyclerItemClickListener;
import com.ithakatales.android.util.Bakery;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class NavigationDrawerFragment extends BaseFragment implements NavigationDrawerViewInteractor, RecyclerItemClickListener<City> {

    @Inject NavigationDrawerPresenter presenter;
    @Inject Context context;
    @Inject Bakery bakery;

    @Bind(R.id.recycler_cities) RecyclerView recyclerCities;
    @Bind(R.id.progress) ProgressBar progress;

    private NavigationDrawerAdapter adapter;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private View drawerContainerView;

    private DrawerItemClickLister drawerItemClickLister;

    private List<City> cities = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
        presenter.setViewInteractor(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_drawer, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new NavigationDrawerAdapter(cities, this);
        recyclerCities.setAdapter(adapter);
        recyclerCities.setLayoutManager(new LinearLayoutManager(context));

        presenter.loadCities();
    }

    public void setUpDrawer(DrawerLayout drawerLayout, final Toolbar toolbar) {
        this.drawerLayout = drawerLayout;

        drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        this.drawerLayout.setDrawerListener(drawerToggle);
        this.drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });
    }

    @Override
    public void citiesLoaded(List<City> cities) {
        if (cities.size() > 0) {
            this.cities.clear();
            this.cities.addAll(cities);
            adapter.notifyDataSetChanged();
            selectItemAt(0);
        }
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
    public void onItemClick(City city) {
        drawerItemClickLister.onDrawerItemSelected(city);
        drawerLayout.closeDrawer(getContentView());
    }

    public void setDrawerItemClickLister(DrawerItemClickLister listener) {
        this.drawerItemClickLister = listener;
    }

    private void selectItemAt(int index) {
        if (cities.size() > index) {
            drawerItemClickLister.onDrawerItemSelected(cities.get(index));
        }
    }

    public interface DrawerItemClickLister {
        void onDrawerItemSelected(City city);
    }

}
