package com.ithakatales.android.app.di;

import android.content.Context;

import com.ithakatales.android.app.IthakaApplication;
import com.ithakatales.android.data.repository.realm.AttractionRepositoryRealm;
import com.ithakatales.android.data.repository.realm.AudioDownloadRepositoryRealm;
import com.ithakatales.android.data.repository.realm.AudioRepositoryRealm;
import com.ithakatales.android.data.repository.realm.ImageDownloadRepositoryRealm;
import com.ithakatales.android.data.repository.realm.ImageRepositoryRealm;
import com.ithakatales.android.data.repository.realm.TourDownloadRepositoryRealm;
import com.ithakatales.android.download.DownloadBuilder;
import com.ithakatales.android.download.TourDownloadService;
import com.ithakatales.android.download.manager.DefaultDownloader;
import com.ithakatales.android.presenter.concrete.SamplePresenterImpl;
import com.ithakatales.android.ui.activity.test.ApiTestActivity;
import com.ithakatales.android.ui.activity.test.DownloadTourTestActivity;
import com.ithakatales.android.ui.activity.test.TestActivity;
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

                // View specific classes - activities, fragments, adapters etc
                TestActivity.class,
                ApiTestActivity.class,
                DownloadTourTestActivity.class,

                // Presenters
                SamplePresenterImpl.class,

                // Repositories
                TourDownloadRepositoryRealm.class,
                AudioDownloadRepositoryRealm.class,
                AttractionRepositoryRealm.class,
                AudioRepositoryRealm.class,
                ImageRepositoryRealm.class,
                ImageDownloadRepositoryRealm.class,

                // Download
                TourDownloadService.class,
                DownloadBuilder.class,
                DefaultDownloader.class,

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

}
