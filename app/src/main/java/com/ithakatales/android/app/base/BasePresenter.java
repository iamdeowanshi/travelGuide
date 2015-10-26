package com.ithakatales.android.app.base;

import com.ithakatales.android.app.di.Injector;

/**
 * Provides presenter viewInteractor related operations, all presenters should extend this class.
 *
 * @author Farhan Ali
 */
public abstract class BasePresenter<T extends ViewInteractor> implements Presenter<T> {

    protected T viewInteractor;

    @Override
    public void setViewInteractor(T viewInteractor) {
        this.viewInteractor = viewInteractor;
    }

    protected void injectDependencies() {
        Injector.instance().inject(this);
    }

    /**
     * Return class name as TAG.
     *
     * @return String tag
     */
    public String tag() {
        return getClass().getName();
    }

}
