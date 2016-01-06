package com.ithakatales.android.ui.actions;

import android.widget.Button;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.presenter.TourDetailPresenter;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.DialogUtil;

import javax.inject.Inject;

/**
 * @author farhanali
 */
public class TourDeleteAction extends TourAction {

    @Inject DialogUtil dialogUtil;
    @Inject Bakery bakery;

    private TourDetailPresenter presenter;

    public TourDeleteAction(Button button, TourDetailPresenter presenter) {
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
                bakery.toastShort("Deleting..");
                presenter.deleteAttraction(attraction);
            }

            @Override
            public void onNegativeClick() {
                bakery.toastShort("Under Development !");
            }
        });

        dialogUtil.setTitle("Tour Removed !")
                .setMessage("This tour is no longer exist in our database, would you like to remove it now ?")
                .setPositiveButtonText("Remove")
                .setNegativeButtonText("Continue")
                .show(button.getContext());
    }

}