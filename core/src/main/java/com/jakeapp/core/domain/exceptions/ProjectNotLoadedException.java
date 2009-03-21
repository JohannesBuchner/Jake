package com.jakeapp.core.domain.exceptions;

/**
 * Exception that gets thrown if someone tries to access a project that is
 * not currently opened and therefor has to be loaded first.
 */
public class ProjectNotLoadedException extends Exception{
    private static final long serialVersionUID = 4303333602495036664L;

    public ProjectNotLoadedException() {
        super();
    }

    public ProjectNotLoadedException(final String message) {
        super(message);
    }

    public ProjectNotLoadedException(final String message,
                                     final Throwable cause) {
        super(message, cause);
    }

    public ProjectNotLoadedException(final Throwable cause) {
        super(cause);
    }
}
