package com.jakeapp.core.domain.exceptions;


public class NoSuchMsgServiceException extends Exception {

    public NoSuchMsgServiceException() {
        super();
    }

    public NoSuchMsgServiceException(final String message) {
        super(message);
    }

    public NoSuchMsgServiceException(final String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchMsgServiceException(final Throwable cause) {
        super(cause);
    }
}