package com.jakeapp.core.dao.exceptions;

/**
 * Exception that is thrown if a <code>Project</code>'s member was not found.
 * @author domdorn
 */
public class NoSuchUserException extends Exception {
    private static final long serialVersionUID = -983863803306075517L;

    public NoSuchUserException(final String message) {
		super(message);
	}
}
