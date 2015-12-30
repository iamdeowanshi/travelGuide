package com.ithakatales.android.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.City;
import com.ithakatales.android.ui.custom.NoNetworkView;
import com.ithakatales.android.ui.fragment.MyToursFragment;
import com.ithakatales.android.ui.fragment.NavigationDrawerFragment;
import com.ithakatales.android.ui.fragment.TourListFragment;
import com.ithakatales.android.util.Bakery;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author Farhan Ali
 */
public class HomeActivity extends BaseActivity implements NavigationDrawerFragment.DrawerItemClickLister {

    public static final int TAB_COUNT           = 2;
    public static final int POSITION_TOUR_LIST  = 0;
    public static final int POSITION_MY_TOURS   = 1;

    @Inject Bakery bakery;

    @Bind(R.id.layout_drawer) DrawerLayout drawerLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.view_pager) ViewPager viewPager;

    private NavigationDrawerFragment drawerFragment;
    private HomePageAdapter homePageAdapter;

    private Map<Integer, Fragment> pageFragmentMap = new HashMap<>();

    private City selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        injectDependencies();

        setupActionBar();
        setupNavigationDrawer();
        loadHomePages();
    }

    @Override
    public void onDrawerItemSelected(City city) {
        selectedCity = city;
        TourListFragment tourListFragment = (TourListFragment) homePageAdapter.getFragment(POSITION_TOUR_LIST);

        if (tourListFragment != null && selectedCity != null) {
            getSupportActionBar().setTitle(selectedCity.getName());
            tourListFragment.onCitySelectionChanged(selectedCity);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMyToursView();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Ithakatales");
        getSupportActionBar().setIcon(R.mipmap.app_icon);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name,
                R.string.app_name);
    }

    private void setupNavigationDrawer() {
        drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setDrawerItemClickLister(this);
        drawerFragment.setUpDrawer(drawerLayout, toolbar);
    }

    private void loadHomePages() {
        homePageAdapter = new HomePageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(new HomePageAdapter(getSupportFragmentManager()));
        // The setupWithViewPager doesn't works without the runnable. Maybe a Support Library Bug.
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }

    private void refreshMyToursView() {
        MyToursFragment myToursFragment = (MyToursFragment) homePageAdapter.getFragment(POSITION_MY_TOURS);

        if (myToursFragment != null) {
            myToursFragment.updateAdapter();
        }
    }

    private void setupNetworkRetryListener(TourListFragment tourListFragment) {
        tourListFragment.setNetworkRetryListener(new NoNetworkView.NetworkRetryListener() {
            @Override
            public void onNetworkAvailable() {
                drawerFragment.loadCities();
            }

            @Override
            public void onNetworkNotAvailable() {}
        });
    }

    private class HomePageAdapter extends FragmentStatePagerAdapter {
        public HomePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position) {
                case POSITION_TOUR_LIST:
                    TourListFragment tourListFragment = new TourListFragment();
                    setupNetworkRetryListener(tourListFragment);
                    fragment = tourListFragment;
                    break;
                case POSITION_MY_TOURS:
                    fragment = new MyToursFragment();
                    break;
            }

            if (fragment != null) {
                pageFragmentMap.put(position, fragment);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case POSITION_TOUR_LIST:
                    return "Tours";
                case POSITION_MY_TOURS:
                    return "My Tours";
            }

            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            pageFragmentMap.remove(position);
        }

        /**
         * After an orientation change, the fragments are saved in the adapter, and
         * I don't want to double save them: I will retrieve them and put them in my
         * list again here.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            pageFragmentMap.put(position, fragment);

            return fragment;
        }

        public Fragment getFragment(int position) {
            return pageFragmentMap.get(position);
        }

    }

}