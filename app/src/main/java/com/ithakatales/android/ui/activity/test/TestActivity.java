package com.ithakatales.android.ui.activity.test;

import android.os.Bundle;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.download.TourStorage;
import com.ithakatales.android.util.Bakery;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.inject.Inject;

import butterknife.OnClick;
import dalvik.system.DexFile;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.exceptions.RealmException;
import timber.log.Timber;

/**
 * @author Farhan Ali
 */
public class TestActivity extends BaseActivity {

    @Inject Realm realm;
    @Inject Bakery bakery;
    @Inject TourStorage tourStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        injectDependencies();
    }

    @OnClick(R.id.btn_api_test)
    void launchApiTest() {
        startActivity(ApiTestActivity.class, null);
    }

    @OnClick(R.id.btn_download_tour_test)
    void launchDownloadTourTest() {
    }

    @OnClick(R.id.btn_my_tours_test)
    void launchMyToursTest() {
        startActivity(MyToursTestActivity.class, null);
    }

    @OnClick(R.id.btn_clear_data)
    void clearData() {
        clearFiles();
        exportRealm();
        clearRealm();
        bakery.toastShort("Data Cleared");
    }

    @OnClick(R.id.btn_export_realm)
    void exportRealm() {
        File exportRealmFile = null;
        try {
            File dir = getIthakaFolder();
            exportRealmFile = new File(dir, "ithaka.realm");
            // if "export.realm" already exists, delete
            exportRealmFile.delete();
            // copy current realm to "export.realm"
            realm.writeCopyTo(exportRealmFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bakery.toastShort("Realm Exported: " + exportRealmFile.getPath());
        Timber.e(exportRealmFile.getPath());
    }

    private void clearRealm() {
        realm.beginTransaction();
        try {
            String packageCodePath = getPackageCodePath();
            DexFile df = new DexFile(packageCodePath);
            for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
                String className = iter.nextElement();
                if (className.contains("com.ithakatales.android.data.model")) {
                    Class cls = Class.forName(className);
                    try {
                        realm.clear((Class<? extends RealmObject>) cls);
                    } catch (RealmException e) {
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        realm.commitTransaction();
    }

    private void clearFiles() {
        File dir = getIthakaFolder();
        delete(dir);
    }

    private void delete(File file) {
        if ( ! file.exists()) return;

        if (file.isDirectory() && file.list().length > 0) {
            for (File child : file.listFiles()) {
                delete(child);
            }
        }

        file.delete();
    }

    private File getIthakaFolder() {
        return tourStorage.getIthakaDir();
    }

}
