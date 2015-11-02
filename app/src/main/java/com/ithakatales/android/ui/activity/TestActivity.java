package com.ithakatales.android.ui.activity;

import android.os.Bundle;
import android.os.Environment;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.util.Bakery;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.OnClick;
import io.realm.Realm;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class TestActivity extends BaseActivity {

    @Inject Realm realm;
    @Inject Bakery bakery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        injectDependencies();
    }

    @OnClick(R.id.btn_api_test)
    void launchApiTest() {
        startActivity(ApiTestActivity.class, null);
    }

    @OnClick(R.id.btn_download_tour_test)
    void launchDownloadTourTest() {
        startActivity(DownloadTourTestActivity.class, null);
    }

    @OnClick(R.id.btn_my_tours_test)
    void launchMyToursTest() {
        startActivity(MyToursTestActivity.class, null);
    }

    @OnClick(R.id.btn_export_realm)
    void exportRealm() {
        File exportRealmFile = null;
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/Ithaka");
            if ( ! dir.exists()) dir.mkdirs();
            exportRealmFile = new File(dir, "ithaka.realm");

            // if "export.realm" already exists, delete
            exportRealmFile.delete();
            // copy current realm to "export.realm"
            realm.writeCopyTo(exportRealmFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        realm.close();

        bakery.toastLong(exportRealmFile.getPath());
        Timber.e(exportRealmFile.getPath());
    }

}
