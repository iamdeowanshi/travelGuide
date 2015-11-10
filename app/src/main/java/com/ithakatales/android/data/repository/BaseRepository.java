package com.ithakatales.android.data.repository;

import java.util.List;

/**
 * @author Farhan Ali
 */
public interface BaseRepository<T> {

    void save(T obj);

    T find(long id);

    List<T> readAll();

    void remove(long id);

}
