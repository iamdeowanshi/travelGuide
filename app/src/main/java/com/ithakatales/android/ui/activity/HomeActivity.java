package com.ithakatales.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.City;
import com.ithakatales.android.ui.fragment.HomeFragment;
import com.ithakatales.android.ui.fragment.NavigationDrawerFragment;
import com.ithakatales.android.util.Bakery;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author Farhan Ali
 */
public class HomeActivity extends BaseActivity implements NavigationDrawerFragment.DrawerItemClickLister {

    @Inject Bakery bakery;

    @Bind(R.id.layout_drawer) DrawerLayout mDrawerLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;

    private NavigationDrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Delhi");
        getSupportActionBar().setIcon(R.mipmap.app_icon);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        /*setting navigation drawer*/
        drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUpDrawer((DrawerLayout) findViewById(R.id.layout_drawer), toolbar);
        drawerFragment.setDrawerItemClickLister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                bakery.toastShort("setting clicked");
                break;
            case R.id.action_send_feedback:
                bakery.toastShort("send feedback clicked");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(City city) {
        loadHomeFragment(city);
    }

    // TODO: 29/11/15 Optimization required: Adding seperate fragment on each item click is not required
    private void loadHomeFragment(City city) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("city_id", city.getId());
        homeFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.layout_fragment_container, homeFragment);
        fragmentTransaction.commit();

        // set the toolbar title
        getSupportActionBar().setTitle(city.getName());
    }

}