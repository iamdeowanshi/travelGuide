package com.ithakatales.android.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.util.ConnectivityUtil;

import javax.inject.Inject;

/**
 * @author Farhan Ali
 */
public class NoNetworkView extends RelativeLayout implements View.OnClickListener {

    @Inject ConnectivityUtil connectivityUtil;

    private Button buttonRetry;
    private NetworkRetryListener networkRetryListener = NetworkRetryListener.DEFAULT;

    public NoNetworkView(Context context) {
        super(context);
        init();
    }

    public NoNetworkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoNetworkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setNetworkRetryListener(NetworkRetryListener networkRetryListener) {
        this.networkRetryListener = networkRetryListener;
    }

    @Override
    public void onClick(View v) {
        retry();
    }

    public void retry() {
        if (networkRetryListener == null) return;

        if (connectivityUtil.isConnected()) {
            networkRetryListener.onNetworkAvailable();
            hide();
            return;
        }

        show();
        networkRetryListener.onNetworkNotAvailable();
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    private void init() {
        Injector.instance().inject(this);
        inflate(getContext(), R.layout.view_no_network, this);
        buttonRetry = (Button) findViewById(R.id.button_retry);
        buttonRetry.setOnClickListener(this);

        if (connectivityUtil.isConnected()) {
            hide();
        }
    }

    public interface NetworkRetryListener {
        NetworkRetryListener DEFAULT = new NetworkRetryListener() {
            @Override
            public void onNetworkNotAvailable() {}

            @Override
            public void onNetworkAvailable() {}
        };

        void onNetworkAvailable();
        void onNetworkNotAvailable();
    }

}
