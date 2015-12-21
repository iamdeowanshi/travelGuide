package com.ithakatales.android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseFragment;
import com.ithakatales.android.util.Bakery;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author Farhan Ali
 */
public class HomeFragment extends BaseFragment {

    public static final int TAB_COUNT       = 2;
    public static final int TAB_TOUR_LIST   = 0;
    public static final int TAB_MY_TOURS    = 1;

    @Inject Bakery bakery;

    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.view_pager) ViewPager viewPager;

    private HomePageAdapter homePageAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homePageAdapter = new HomePageAdapter(getChildFragmentManager());
        viewPager.setAdapter(new HomePageAdapter(getChildFragmentManager()));

        // The setupWithViewPager doesn't works without the runnable. Maybe a Support Library Bug.
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }

    private class HomePageAdapter extends FragmentStatePagerAdapter {
        TourListFragment listFragment = new TourListFragment();
        MyToursFragment myToursFragment = new MyToursFragment();

        public HomePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position) {
                case TAB_TOUR_LIST:
                    fragment = listFragment;
                    break;
                case TAB_MY_TOURS:
                    fragment = myToursFragment;
                    break;
            }

            if (fragment != null) {
                fragment.setArguments(getArguments());
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
                case TAB_TOUR_LIST:
                    return "Tours";
                case TAB_MY_TOURS:
                    return "My Tours";
            }

            return null;
        }
    }

}
