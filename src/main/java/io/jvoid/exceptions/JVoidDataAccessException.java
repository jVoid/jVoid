package io.jvoid.exceptions;

/**
 * Thrown when there are issues in accessing the database used to store
 * JVoid metadata.
 *
 */
public class JVoidDataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JVoidDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JVoidDataAccessException(Throwable cause) {
        super(cause);
    }

}
