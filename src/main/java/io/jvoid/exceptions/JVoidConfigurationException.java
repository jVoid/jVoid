package io.jvoid.exceptions;

/**
 * Thrown when the {@code JVoidConfiguration} is invalid.
 *
 */
public class JVoidConfigurationException extends Exception {

    private static final long serialVersionUID = 1L;

    public JVoidConfigurationException(String message) {
        super(message);
    }
    public JVoidConfigurationException(String message, Throwable e) {
        super(message, e);
    }

}
