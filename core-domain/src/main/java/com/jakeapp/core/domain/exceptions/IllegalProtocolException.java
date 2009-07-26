package com.jakeapp.core.domain.exceptions;

/**
 * This Exception gets thrown if someone tries to supply a UserId from a
 * different IM-Service to a project belonging to another service 
 */
public class IllegalProtocolException extends Exception {
    private static final long serialVersionUID = -7071617378801583597L;

    public IllegalProtocolException() {
        super();
    }

    public IllegalProtocolException(final String message) {
        super(message);
    }

    public IllegalProtocolException(final String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalProtocolException(final Throwable cause) {
        super(cause);
    }
}
