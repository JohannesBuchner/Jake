package com.jakeapp.core.dao.exceptions;

/**
 * Exception that is thrown if a <code>JakeObject</code> that was searched
 * for by a method was not found.
 * @author domdorn
 */
@SuppressWarnings("serial")
public class NoSuchJakeObjectException extends Exception {
    public NoSuchJakeObjectException() {
    }

    public NoSuchJakeObjectException(final String message) {
        super(message);
    }

    public NoSuchJakeObjectException(
    		final String message, final Throwable cause) {
        super(message, cause);
    }

    public NoSuchJakeObjectException(final Throwable cause) {
        super(cause);
    }
}
