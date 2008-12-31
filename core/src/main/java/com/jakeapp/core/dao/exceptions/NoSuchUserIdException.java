package com.jakeapp.core.dao.exceptions;

/**
 * This Exception gets thrown, when the UserIdDao tries to get
 * a UserId that is not persisted/does not exist
 */
public class NoSuchUserIdException extends Exception {
    private static final long serialVersionUID = 2487062406514071590L;


    public NoSuchUserIdException() {
    }

    public NoSuchUserIdException(String message) {
        super(message);
    }

    public NoSuchUserIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchUserIdException(Throwable cause) {
        super(cause);
    }
}
