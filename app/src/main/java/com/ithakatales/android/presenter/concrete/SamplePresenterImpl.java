package com.ithakatales.android.presenter.concrete;

import com.ithakatales.android.app.base.BasePresenter;
import com.ithakatales.android.presenter.SamplePresenter;
import com.ithakatales.android.presenter.SampleViewInteractor;

/**
 * @author Farhan Ali
 */
public class SamplePresenterImpl extends BasePresenter<SampleViewInteractor>
        implements SamplePresenter {

    @Override
    public void doSomething() {
        viewInteractor.showSomeMessage("Doing something..");
        viewInteractor.showSomeMessage("Done something");
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

}
