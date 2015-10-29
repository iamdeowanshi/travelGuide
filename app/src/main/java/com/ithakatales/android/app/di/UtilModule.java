package com.ithakatales.android.app.di;

import com.ithakatales.android.util.PreferenceUtil;
import com.mtvindia.connect.util.PreferenceUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provides all presenter class dependencies.
 *
 * @author Farhan Ali
 */
@Module(
        complete = false,
        library = true
)
public class UtilModule {

    @Provides
    @Singleton
    public PreferenceUtil providePreferenceUtil() {
        return new PreferenceUtil();
    }

}
