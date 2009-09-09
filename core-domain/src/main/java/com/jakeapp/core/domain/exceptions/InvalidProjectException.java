package com.jakeapp.core.domain.exceptions;

// FIXME: make the conditions for a valid project more explicit and precise. 
/**
 * This Exception is raised if one tries to create a <code>Project</code> with invalid
 * data, e.g. the <code>UUID</code> is invalid, or the ProjectName is invalid, etc.
 */
public class InvalidProjectException extends Exception {

    private static final long serialVersionUID = -4401012821170678053L;

    /**
     * {@inheritDoc}
     */
    public InvalidProjectException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public InvalidProjectException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public InvalidProjectException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
 	 * {@inheritDoc}
     */
    public InvalidProjectException(Throwable cause) {
        super(cause);
    }
}
