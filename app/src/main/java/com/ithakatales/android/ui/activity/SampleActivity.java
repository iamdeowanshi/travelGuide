package com.ithakatales.android.ui.activity;

import android.os.Bundle;
import android.widget.Button;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.presenter.SamplePresenter;
import com.ithakatales.android.presenter.SampleViewInteractor;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class SampleActivity extends BaseActivity implements SampleViewInteractor {

    @Inject SamplePresenter presenter;

    @Bind(R.id.btn_do_something) Button btnDoSomething;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // call to inject dependencies
        injectDependencies();

        presenter.setViewInteractor(this);
    }

    @OnClick(R.id.btn_do_something)
    void doSomething() {
        presenter.doSomething();
    }

    @Override
    public void showSomeMessage(String message) {
        toastShort(message);
    }

}
