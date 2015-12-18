package com.ithakatales.android.data.repository.realm;

import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.repository.BaseRepository;
import com.ithakatales.android.data.repository.RepoCallback;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * @author Farhan Ali
 */
public abstract class BaseRepositoryRealm<T extends RealmObject> implements BaseRepository<T> {

    @Inject Realm realm;

    protected Class<T> modelType;

    public BaseRepositoryRealm(Class<T> modelType) {
        this.modelType = modelType;
        Injector.instance().inject(this);
    }

    @Override
    public void save(T obj) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(obj);
        realm.commitTransaction();
    }

    @Override
    public void save(List<T> collection) {
        realm.beginTransaction();

        for (T obj : collection) {
            realm.copyToRealmOrUpdate(obj);
        }

        realm.commitTransaction();
    }

    @Override
    public void saveAsync(final T obj, final RepoCallback callback) {
        Realm.Transaction transaction = new Realm.Transaction() {
            @Override
            public void execute(Realm asyncRealm) {
                asyncRealm.copyToRealmOrUpdate(obj);
            }
        };

        realm.executeTransaction(transaction, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                if (callback != null) callback.onSuccess();
            }

            @Override
            public void onError(Exception e) {
                if (callback != null) callback.onError(e);
            }
        });
    }

    @Override
    public T find(long id) {
        return realm.where(modelType).equalTo("id", id).findFirst();
    }

    @Override
    public List<T> readAll() {
        return realm.where(modelType).findAll();
    }

    @Override
    public void remove(long id) {
        realm.beginTransaction();
        find(id).removeFromRealm();
        realm.commitTransaction();
    }

}
