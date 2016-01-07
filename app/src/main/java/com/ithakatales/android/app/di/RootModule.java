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
import com.ithakatales.android.presenter.concrete.LoginPresenterImpl;
import com.ithakatales.android.presenter.concrete.NavigationDrawerPresenterImpl;
import com.ithakatales.android.presenter.concrete.PasswordForgotPresenterImpl;
import com.ithakatales.android.presenter.concrete.PasswordResetPresenterImpl;
import com.ithakatales.android.presenter.concrete.RegistrationPresenterImpl;
import com.ithakatales.android.presenter.concrete.SamplePresenterImpl;
import com.ithakatales.android.presenter.concrete.SettingsPresenterImpl;
import com.ithakatales.android.presenter.concrete.TourDetailPresenterImpl;
import com.ithakatales.android.presenter.concrete.TourListPresenterImpl;
import com.ithakatales.android.presenter.concrete.VerifyAccountPresenterImpl;
import com.ithakatales.android.ui.actions.TourAction;
import com.ithakatales.android.ui.actions.TourDeleteAction;
import com.ithakatales.android.ui.actions.TourDownloadAction;
import com.ithakatales.android.ui.actions.TourDownloadRetryAction;
import com.ithakatales.android.ui.actions.TourStartAction;
import com.ithakatales.android.ui.actions.TourUpdateAction;
import com.ithakatales.android.ui.activity.HomeActivity;
import com.ithakatales.android.ui.activity.LaunchActivity;
import com.ithakatales.android.ui.activity.LoginActivity;
import com.ithakatales.android.ui.activity.PasswordForgotActivity;
import com.ithakatales.android.ui.activity.PasswordResetActivity;
import com.ithakatales.android.ui.activity.RegistrationActivity;
import com.ithakatales.android.ui.activity.SettingsActivity;
import com.ithakatales.android.ui.activity.SocialLoginEnabledActivity;
import com.ithakatales.android.ui.activity.TourDetailActivity;
import com.ithakatales.android.ui.activity.TourGalleryActivity;
import com.ithakatales.android.ui.activity.TourPlayerActivity;
import com.ithakatales.android.ui.activity.UserOnBoardActivity;
import com.ithakatales.android.ui.activity.VerifyAccountActivity;
import com.ithakatales.android.ui.activity.test.ApiTestActivity;
import com.ithakatales.android.ui.activity.test.TestActivity;
import com.ithakatales.android.ui.adapter.GalleryPagerAdapter;
import com.ithakatales.android.ui.adapter.MyToursExpandableListAdapter;
import com.ithakatales.android.ui.adapter.NavigationDrawerAdapter;
import com.ithakatales.android.ui.adapter.PlayListRecyclerAdapter;
import com.ithakatales.android.ui.adapter.TagGridAdapter;
import com.ithakatales.android.ui.adapter.ToursListRecyclerAdapter;
import com.ithakatales.android.ui.adapter.UserOnBoardPagerAdapter;
import com.ithakatales.android.ui.custom.NoNetworkView;
import com.ithakatales.android.ui.fragment.MyToursFragment;
import com.ithakatales.android.ui.fragment.NavigationDrawerFragment;
import com.ithakatales.android.ui.fragment.TourListFragment;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.ConnectivityUtil;
import com.ithakatales.android.util.DialogUtil;
import com.ithakatales.android.util.PreferenceUtil;
import com.ithakatales.android.util.UserPreference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Include all other modules, provide BaseActivity dependency. All activity, fragment, presenter and
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
        LaunchActivity.class,
        UserOnBoardActivity.class,
        SocialLoginEnabledActivity.class,
        LoginActivity.class,
        RegistrationActivity.class,
        VerifyAccountActivity.class,
        PasswordForgotActivity.class,
        PasswordResetActivity.class,
        SettingsActivity.class,
        HomeActivity.class,
        TourDetailActivity.class,
        TourPlayerActivity.class,
        TourGalleryActivity.class,

        // Fragments
        NavigationDrawerFragment.class,
        TourListFragment.class,
        MyToursFragment.class,

        // Tour Actions
        TourAction.class,
        TourDeleteAction.class,
        TourDownloadAction.class,
        TourDownloadRetryAction.class,
        TourStartAction.class,
        TourUpdateAction.class,

        // Adapters
        UserOnBoardPagerAdapter.class,
        ToursListRecyclerAdapter.class,
        NavigationDrawerAdapter.class,
        TagGridAdapter.class,
        MyToursExpandableListAdapter.class,
        GalleryPagerAdapter.class,
        PlayListRecyclerAdapter.class,

        // Custom view
        NoNetworkView.class,

        // Presenters
        SamplePresenterImpl.class,
        LoginPresenterImpl.class,
        RegistrationPresenterImpl.class,
        PasswordForgotPresenterImpl.class,
        PasswordResetPresenterImpl.class,
        VerifyAccountPresenterImpl.class,
        SettingsPresenterImpl.class,
        NavigationDrawerPresenterImpl.class,
        TourListPresenterImpl.class,
        TourDetailPresenterImpl.class,

        // Repositories
        AttractionRepositoryRealm.class,
        AttractionUpdateRepositoryRealm.class,
        AudioRepositoryRealm.class,
        ImageRepositoryRealm.class,

        // Tour Downloader
        TourDownloader.class,
        TourDownloadProgressReader.class,
        TourDownloadProgressObserver.class,

        // PlayerDurationUtil
        PreferenceUtil.class,
        UserPreference.class,
        Bakery.class,
        ConnectivityUtil.class,
        DialogUtil.class,
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
