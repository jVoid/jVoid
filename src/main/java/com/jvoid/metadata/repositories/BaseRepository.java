package com.jvoid.metadata.repositories;

/**
 *
 */
public interface BaseRepository<T, I> {

    T findById(I id);

    T add(T obj);

}
