package io.jvoid.metadata.checksum;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.jvoid.configuration.JVoidConfiguration;
import io.jvoid.exceptions.JVoidIntrumentationException;
import lombok.AllArgsConstructor;

/**
 * Generic class that generates a checksum for the entities tracked in JVoid.
 * (Those entities are typically classes, methods, constructors, static blocks, etc.)
 */
@AllArgsConstructor
public abstract class AbstractChecksummer<T> implements Checksummer<T> {

    protected JVoidConfiguration jVoidConfiguration;

    protected String computeChecksum(byte[] input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(input);
            return String.format("%064x", new java.math.BigInteger(1, md.digest()));
        } catch (NoSuchAlgorithmException e) {
            throw new JVoidIntrumentationException(e.getLocalizedMessage(), e);
        }
    }

}
