package com.ithakatales.android.data.api;

import rx.Observer;

/**
 * Wrapper for rx.Observer with default implementation for onCompleted & onError.
 *
 * @author Farhan Ali
 */
public abstract class ApiObserver<T> implements Observer<T> {

    public static final ApiObserver DEFAULT = new ApiObserver() {
        @Override
        public void onResult(Object result) {}

        @Override
        public void onError(Throwable e) {}
    };

    /**
     * Publish result to observer.
     *
     * @param result
     */
    public abstract void onResult(T result);

    @Override
    public void onCompleted() {
        // Default implementation, may override.
    }

    @Override
    public void onNext(T result) {
        onResult(result);
    }

}
