package com.ithakatales.android.ui.actions;

import android.content.Intent;
import android.widget.Button;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.presenter.TourDetailPresenter;
import com.ithakatales.android.ui.activity.LoginActivity;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.ConnectivityUtil;
import com.ithakatales.android.util.DialogUtil;
import com.ithakatales.android.util.UserPreference;

import javax.inject.Inject;

public class TourDownloadAction extends TourAction {

    @Inject UserPreference preference;
    @Inject DialogUtil dialogUtil;
    @Inject Bakery bakery;
    @Inject ConnectivityUtil connectivityUtil;

    private TourDetailPresenter presenter;

    public TourDownloadAction(Button button, TourDetailPresenter presenter) {
        super(button);
        Injector.instance().inject(this);

        this.presenter = presenter;
        text = "DOWNLOAD";
        enable = true;
        init();
    }

    @Override
    public void perform(Attraction attraction) {
        if (preference.readUser() == null) {
            showNotLoggedInDialog();
            return;
        }

        if ( ! connectivityUtil.isConnected()) {
            bakery.toastShort("No network, Try later");
            return;
        }

        presenter.downloadAttraction(attraction);

    }

    private void showNotLoggedInDialog() {
        dialogUtil.setDialogClickListener(new DialogUtil.DialogClickListener() {
            @Override
            public void onPositiveClick() {
                Intent intent = new Intent(button.getContext(), LoginActivity.class);
                button.getContext().startActivity(intent);
            }

            @Override
            public void onNegativeClick() {}
        });

        dialogUtil.setTitle("Not logged")
                .setMessage("Please login to download the IthakaTale")
                .setPositiveButtonText("Login")
                .setNegativeButtonText("Cancel")
                .show(button.getContext());
    }

}
