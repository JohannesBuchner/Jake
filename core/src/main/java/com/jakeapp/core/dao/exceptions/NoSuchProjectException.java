package com.jakeapp.core.dao.exceptions;


/**
 * Exception thrown when a <code>Project</code> is not found.
 * @author christopher
 */
public class NoSuchProjectException extends Exception {
    private static final long serialVersionUID = 6735786916981314421L;

    public NoSuchProjectException() {
    }

    public NoSuchProjectException(final String message) {
		super(message);
	}
}
