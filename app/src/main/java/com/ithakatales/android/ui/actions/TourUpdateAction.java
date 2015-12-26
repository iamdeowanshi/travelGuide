package com.ithakatales.android.ui.actions;

import android.widget.Button;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.presenter.TourDetailPresenter;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.ConnectivityUtil;
import com.ithakatales.android.util.DialogUtil;

import javax.inject.Inject;

public class TourUpdateAction extends TourAction {

    @Inject DialogUtil dialogUtil;
    @Inject Bakery bakery;
    @Inject ConnectivityUtil connectivityUtil;

    private TourDetailPresenter presenter;

    public TourUpdateAction(Button button, TourDetailPresenter presenter) {
        super(button);
        Injector.instance().inject(this);

        this.presenter = presenter;

        text = "Start Tour";
        enable = true;

        init();
    }

    @Override
    public void perform(final Attraction attraction) {
        dialogUtil.setDialogClickListener(new DialogUtil.DialogClickListener() {
            @Override
            public void onPositiveClick() {
                if ( ! connectivityUtil.isConnected()) {
                    bakery.toastShort("No network, Try later");
                    return;
                }

                bakery.toastShort("Updating..");
                presenter.updateAttraction(attraction);
            }

            @Override
            public void onNegativeClick() {
                bakery.toastShort("Under Development !");
            }
        });

        dialogUtil.setTitle("Update Available !")
                .setMessage("Some updates are made to this tour, would you like to updated it now ?")
                .setPositiveButtonText("Update")
                .setNegativeButtonText("Continue")
                .show(button.getContext());
    }

}