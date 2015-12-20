package com.ithakatales.android.app.di;

import android.content.Context;
import android.view.LayoutInflater;

import com.ithakatales.android.app.IthakaApplication;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.repository.realm.AttractionRepositoryRealm;
import com.ithakatales.android.data.repository.realm.AttractionUpdateRepositoryRealm;
import com.ithakatales.android.data.repository.realm.AudioRepositoryRealm;
import com.ithakatales.android.data.repository.realm.ImageRepositoryRealm;
import com.ithakatales.android.download.TourDownloadProgressObserver;
import com.ithakatales.android.download.TourDownloadProgressReader;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.presenter.concrete.NavigationDrawerPresenterImpl;
import com.ithakatales.android.presenter.concrete.SamplePresenterImpl;
import com.ithakatales.android.presenter.concrete.TourDetailPresenterImpl;
import com.ithakatales.android.presenter.concrete.TourListPresenterImpl;
import com.ithakatales.android.ui.activity.HomeActivity;
import com.ithakatales.android.ui.activity.TourDetailActivity;
import com.ithakatales.android.ui.activity.test.ApiTestActivity;
import com.ithakatales.android.ui.activity.test.TestActivity;
import com.ithakatales.android.ui.adapter.MyToursExpandableListAdapter;
import com.ithakatales.android.ui.adapter.NavigationDrawerAdapter;
import com.ithakatales.android.ui.adapter.TagGridAdapter;
import com.ithakatales.android.ui.adapter.ToursListRecyclerAdapter;
import com.ithakatales.android.ui.fragment.HomeFragment;
import com.ithakatales.android.ui.fragment.MyToursFragment;
import com.ithakatales.android.ui.fragment.NavigationDrawerFragment;
import com.ithakatales.android.ui.fragment.TourListFragment;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.ConnectivityUtil;
import com.ithakatales.android.util.PreferenceUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Include all other modules, provide Context dependency. All activity, fragment, presenter and
 * any classes that are going to use dependency injection should be registered here.
 *
 * @author Farhan Ali
 */
@Module(
        includes = {
                PresenterModule.class,
                ApiModule.class,
                OrmModule.class,
                DownloadModule.class,
                UtilModule.class
        },
        injects = {
                IthakaApplication.class,

                // Activities
                BaseActivity.class,
                TestActivity.class,
                ApiTestActivity.class,
                HomeActivity.class,
                TourDetailActivity.class,

                // Fragments
                HomeFragment.class,
                NavigationDrawerFragment.class,
                TourListFragment.class,
                MyToursFragment.class,

                // Adapters
                ToursListRecyclerAdapter.class,
                NavigationDrawerAdapter.class,
                TagGridAdapter.class,
                MyToursExpandableListAdapter.class,

                // Presenters
                SamplePresenterImpl.class,
                NavigationDrawerPresenterImpl.class,
                TourListPresenterImpl.class,
                TourDetailPresenterImpl.class,

                // Repositories
                AttractionRepositoryRealm.class,
                AttractionUpdateRepositoryRealm.class,
                AudioRepositoryRealm.class,
                ImageRepositoryRealm.class,

                // Download
                TourDownloader.class,
                TourDownloadProgressReader.class,
                TourDownloadProgressObserver.class,

                // Utilities
                PreferenceUtil.class,
                Bakery.class,
                ConnectivityUtil.class,
        }
)
public class RootModule {

    private Context context;

    public RootModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return context;
    }

    @Provides
    @Singleton
    public LayoutInflater provideLayoutInflater() {
        return LayoutInflater.from(context);
    }

}
