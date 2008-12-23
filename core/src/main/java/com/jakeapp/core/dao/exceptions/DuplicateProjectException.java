package com.jakeapp.core.dao.exceptions;

/**
 * Exception thrown when a <code>Project</code> with the same name
 * as another <code>Project</code> would be inserted into the database.
 * @author christopher
 */
public class DuplicateProjectException extends Exception {
    private static final long serialVersionUID = 7942579043158590939L;

    public DuplicateProjectException(final String message) {
		super(message);
	}
}
