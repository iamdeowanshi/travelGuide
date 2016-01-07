package com.ithakatales.android.data.repository;

import java.util.List;

/**
 * @author Farhan Ali
 */
public interface BaseRepository<T> {

    void save(T obj);

    void save(List<T> collection);

    T find(long id);

    List<T> readAll();

    void remove(long id);

    void removeAll();

}
