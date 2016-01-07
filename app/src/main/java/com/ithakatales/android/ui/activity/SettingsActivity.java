package com.ithakatales.android.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.Config;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.data.model.User;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.presenter.SettingsPresenter;
import com.ithakatales.android.presenter.SettingsViewInteractor;
import com.ithakatales.android.util.Bakery;
import com.ithakatales.android.util.UserPreference;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author farhanali
 */
public class SettingsActivity extends BaseActivity implements SettingsViewInteractor {

    @Inject SettingsPresenter presenter;
    @Inject TourDownloader tourDownloader;
    @Inject UserPreference preference;
    @Inject Bakery bakery;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.layout_about_user) RelativeLayout layoutAboutUser;
    @Bind(R.id.layout_manage_downloads) RelativeLayout layoutManageDownloads;
    @Bind(R.id.image_user) ImageView imageUser;
    @Bind(R.id.text_name) TextView textName;
    @Bind(R.id.text_email) TextView textEmail;
    @Bind(R.id.input_about) EditText inputAbout;
    @Bind(R.id.progress) ProgressBar progress;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        injectDependencies();
        presenter.setViewInteractor(this);

        // setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        user = preference.readUser();
        if (user == null) {
            layoutAboutUser.setVisibility(View.GONE);
            layoutManageDownloads.setVisibility(View.GONE);
            return;
        }

        loadUserInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // nothing todo - no menu in settings activity.
        return true;
    }

    @Override
    public void onProfilePicUploadSuccess(String url) {
        bakery.snackLong(getContentView(), "Profile pic uploaded");
        user.setAvatar(url);
        presenter.updateProfile(user);
    }

    @Override
    public void onProfileUpdateSuccess(User user) {
        bakery.snackLong(getContentView(), "Profile updated");
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkError(Throwable e) {
        Timber.e(e.getMessage(), e);
        bakery.snackLong(getContentView(), "Update failed !, Confirm inputs are correct");
    }

    @OnClick(R.id.image_user)
    void onUerImageClick() {
        bakery.toastShort("Under development !");
    }

    @OnClick(R.id.button_upload)
    void onUploadClick() {
        bakery.toastShort("Under development !");
    }

    @OnClick(R.id.button_save)
    void onSaveClick() {
        user.setAbout(inputAbout.getText().toString());
        presenter.updateProfile(user);
    }

    @OnClick(R.id.button_delete_downloads)
    void onDeleteDownloadsClick() {
        tourDownloader.deleteAll();
        bakery.snackLong(getContentView(), "All tours deleted !");
    }

    @OnClick(R.id.button_logout)
    void onLogoutClick() {
        preference.removeUser();
        startActivityClearTop(LaunchActivity.class, null);
    }

    @OnClick(R.id.text_privacy_link)
    void onPrivacyLinkClick() {
        openLink(Config.LINK_PRIVACY_POLICY);
    }

    @OnClick(R.id.text_terms_link)
    void onTermsLinkClick() {
        openLink(Config.LINK_TERMS);
    }

    private void openLink(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void loadUserInfo() {
        if (user != null) {
            Picasso.with(this).load(user.getAvatar()).into(imageUser);
            inputAbout.setText(user.getAbout());
            textName.setText(user.getFullName());
            textEmail.setText(user.getEmail());
        }
    }

}
