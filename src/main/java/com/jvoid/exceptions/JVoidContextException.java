package com.jvoid.exceptions;

/**
 * Thrown when the JVoid execution context encounters anomalies.
 *
 */
public class JVoidContextException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JVoidContextException() {
        super();
    }

    public JVoidContextException(String msg) {
        super(msg);
    }

    public JVoidContextException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JVoidContextException(Throwable cause) {
        super(cause);
    }

}
