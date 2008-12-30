package com.jakeapp.core.domain.exceptions;


public class InvalidUserIdException extends Exception {
    private static final long serialVersionUID = -5835492841689381607L;


    public InvalidUserIdException() {
    }

    public InvalidUserIdException(String message) {
        super(message);
    }

    public InvalidUserIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserIdException(Throwable cause) {
        super(cause);
    }
}
