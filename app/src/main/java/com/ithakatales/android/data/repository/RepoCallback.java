package com.ithakatales.android.data.repository;

/**
 * @author Farhan Ali
 */
public interface RepoCallback {

    void onSuccess();

    void onError(Throwable e);

}
