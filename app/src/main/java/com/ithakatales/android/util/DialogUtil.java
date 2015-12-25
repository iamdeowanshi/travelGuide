package com.ithakatales.android.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;

import javax.inject.Inject;

/**
 * @author Farhan Ali
 */
public class DialogUtil implements DialogInterface.OnClickListener {

    private static final String BUTTON_TEXT_OK      = "Ok";
    private static final String BUTTON_TEXT_CANCEL  = "Cancel";

    private String title;
    private String message;
    private String positiveButtonText;
    private String negativeButtonText;

    private DialogClickListener dialogClickListener = DialogClickListener.DEFAULT;

    public DialogUtil setDialogClickListener(DialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;

        return this;
    }

    public DialogUtil setTitle(String title) {
        this.title = title;

        return this;
    }

    public DialogUtil setMessage(String message) {
        this.message = message;

        return this;
    }

    public DialogUtil setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;

        return this;
    }

    public DialogUtil setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;

        return this;
    }

    public void show(Context activityContext) {
        new AlertDialog.Builder(activityContext, R.style.AlertDialogStyle)
                .setIcon(R.mipmap.app_icon)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, this)
                .setNegativeButton(negativeButtonText, this)
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (dialogClickListener == null) return;

        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                dialogClickListener.onPositiveClick();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialogClickListener.onNegativeClick();
                break;
        }
    }

    public static interface DialogClickListener {
        DialogClickListener DEFAULT = new DialogClickListener() {
            @Override
            public void onPositiveClick() {}

            @Override
            public void onNegativeClick() {}
        };

        void onPositiveClick();
        void onNegativeClick();
    }

}
