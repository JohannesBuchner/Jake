package com.jakeapp.core.domain.exceptions;


// FIXME: explicitly declare what criteria must be met to be valid.
/**
 * This exception gets thrown if the supplied credentials are not fully filled out.
 */
public class InvalidCredentialsException extends RuntimeException {
    private static final long serialVersionUID = -8083631804017188758L;

    public InvalidCredentialsException() {
        super();
    }

    public InvalidCredentialsException(final String message) {
        super(message);
    }

    public InvalidCredentialsException(final String message,
                                       final Throwable cause) {
        super(message, cause);
    }

    public InvalidCredentialsException(final Throwable cause) {
        super(cause);
    }
}
