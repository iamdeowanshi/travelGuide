package com.ithakatales.android.app.di;

import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.PreferenceUtil;
import com.ithakatales.android.util.StorageUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provides all util class dependencies.
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

    @Provides
    @Singleton
    public StorageUtil provideStorageUtil() {
        return new StorageUtil();
    }

    @Provides
    @Singleton
    public Bakery provideBakery() {
        return new Bakery();
    }

}
