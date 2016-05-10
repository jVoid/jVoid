package io.jvoid.exceptions;

/**
 * Thrown when there are problem at the instrumentation level.
 *
 */
public class JVoidIntrumentationException extends RuntimeException {

    private static final long serialVersionUID = 1;

    public JVoidIntrumentationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JVoidIntrumentationException(String cause) {
        super(cause);
    }

}
