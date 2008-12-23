package com.jakeapp.core.dao.exceptions;

/**
 * Exception that is thrown if a <code>LogEntry</code> that was searched
 * for by a method was not found.
 * @author domdorn
 */
public class NoSuchLogEntryException extends Exception {
    private static final long serialVersionUID = -1068192047550203676L;

    public NoSuchLogEntryException() {
    }

    public NoSuchLogEntryException(final String message) {
        super(message);
    }

    public NoSuchLogEntryException(
    		final String message, final Throwable cause) {
        super(message, cause);
    }

    public NoSuchLogEntryException(final Throwable cause) {
        super(cause);
    }
}
