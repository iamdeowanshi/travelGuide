package com.ithakatales.android.app.di;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.ConnectivityUtil;
import com.ithakatales.android.util.DialogUtil;
import com.ithakatales.android.util.DisplayUtil;
import com.ithakatales.android.util.PreferenceUtil;
import com.ithakatales.android.util.UserPreference;

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
    public AwesomeValidation provideValidator() {
        return new AwesomeValidation(ValidationStyle.BASIC);
    }

    @Provides
    @Singleton
    public PreferenceUtil providePreferenceUtil() {
        return new PreferenceUtil();
    }

    @Provides
    @Singleton
    public UserPreference provideUserPreference() {
        return new UserPreference();
    }

    @Provides
    @Singleton
    public ConnectivityUtil provideConnectivityUtil() {
        return new ConnectivityUtil();
    }

    @Provides
    @Singleton
    public Bakery provideBakery() {
        return new Bakery();
    }

    @Provides
    public DialogUtil provideDialogUtil() {
        return new DialogUtil();
    }

    @Provides
    public DisplayUtil provideDisplayUtil() {
        return new DisplayUtil();
    }

}
