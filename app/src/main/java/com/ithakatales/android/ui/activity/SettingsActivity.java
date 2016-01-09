package com.ithakatales.android.ui.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.ithakatales.android.util.DialogUtil;
import com.ithakatales.android.util.UserPreference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author farhanali
 */
public class SettingsActivity extends BaseActivity implements SettingsViewInteractor {

    private static final int REQUEST_CAMERA     = 100;
    private static final int REQUEST_GALLERY    = 101;
    private static final int REQUEST_CROP       = 102;

    @Inject SettingsPresenter presenter;
    @Inject TourDownloader tourDownloader;
    @Inject UserPreference preference;
    @Inject DialogUtil dialogUtil;
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
    private Uri imageCaptureUri;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        Uri uri = null;
        switch (requestCode) {
            case REQUEST_CAMERA:
                performCrop(imageCaptureUri);
                break;
            case REQUEST_GALLERY:
                performCrop(data.getData());
                break;
            case REQUEST_CROP:
                uri = saveCroppedPic(data.getExtras());
                break;
        }

        if (uri != null) {
            File imageFile = new File(getRealPathFromURI(uri));
            Picasso.with(this).load(imageFile).into(imageUser);
            presenter.uploadProfilePic(user, imageFile);
        }
    }

    @Override
    public void onProfilePicUploadSuccess(String url) {
        bakery.toastShort("Profile pic uploaded");
        user.setAvatar(url);
        presenter.updateProfile(user);
    }

    @Override
    public void onProfileUpdateSuccess(User user) {
        bakery.toastShort("Profile updated");
        this.user = user;
        preference.saveUser(user);
        loadUserInfo();
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
        bakery.toastShort("Update failed !, Confirm inputs are correct");
    }

    @OnClick(R.id.button_upload)
    void onUploadClick() {
        dialogUtil.setDialogClickListener(new DialogUtil.DialogClickListener() {
            @Override
            public void onPositiveClick() {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageCaptureUri = Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "tmp_avatar_"
                        + String.valueOf(System.currentTimeMillis())
                        + ".jpg"));
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                        imageCaptureUri);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, REQUEST_CAMERA);
            }

            @Override
            public void onNegativeClick() {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Photo"), REQUEST_GALLERY);
            }
        });

        dialogUtil.setTitle("Update profile")
                .setMessage("Choose a photo using")
                .setPositiveButtonText("Camera")
                .setNegativeButtonText("Gallery")
                .show(this);
    }

    @OnClick(R.id.button_save)
    void onSaveClick() {
        user.setAbout(inputAbout.getText().toString());
        presenter.updateProfile(user);
    }

    @OnClick(R.id.button_delete_downloads)
    void onDeleteDownloadsClick() {
        dialogUtil.setDialogClickListener(new DialogUtil.DialogClickListener() {
            @Override
            public void onPositiveClick() {
                tourDownloader.deleteAll();
                bakery.toastShort("All tours deleted");
            }

            @Override
            public void onNegativeClick() {}
        });

        dialogUtil.setTitle("Delete Tours")
                .setMessage("Are you sure to delete all downloaded tours ?")
                .setPositiveButtonText("Delete")
                .setNegativeButtonText("Cancel")
                .show(this);
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

    private String getRealPathFromURI(Uri contentUri) {
        String[] projections = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, projections, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();

        return result;
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 500);
            cropIntent.putExtra("outputY", 500);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_CROP);
        } catch (ActivityNotFoundException e) {
            bakery.toastShort("This device doesn't support the crop action!");
        }
    }

    private Uri saveCroppedPic(Bundle extras) {
        Bitmap bitmap = (Bitmap) extras.get("data");

        if (bitmap == null) return null;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "IthakatalesProfile", null);

        return Uri.parse(path);
    }

}
