package com.jvoid.metadata.checksum;

/**
 * Any class able to perform a checksum on a certain type T
 *
 */
public interface Checksummer<T> {

    String checksum(T input);

}
