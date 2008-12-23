package com.jakeapp.core.domain.exceptions;

/**
 * This exception gets thrown when someone tries to create a userId
 * from a String, but the format of the userId is not allowed by
 * this IM-Service
 */
public class UserIdFormatException extends Exception {
    private static final long serialVersionUID = 1428639791927216493L;

    public UserIdFormatException() {
    }

    public UserIdFormatException(final String message) {
        super(message);
    }

    public UserIdFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserIdFormatException(final Throwable cause) {
        super(cause);
    }
}
