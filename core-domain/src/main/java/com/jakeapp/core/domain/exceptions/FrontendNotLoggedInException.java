package com.jakeapp.core.domain.exceptions;

/**
 * This Exception gets thrown if a client tries to access a method but
 * is not logged in and therefor nor authorized to do so.
 */
public class FrontendNotLoggedInException extends RuntimeException {
	private static final long serialVersionUID = 7900725036326667321L;

	public FrontendNotLoggedInException() {
		super();
	}

	public FrontendNotLoggedInException(final String message) {
		super(message);
	}

	public FrontendNotLoggedInException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public FrontendNotLoggedInException(final Throwable cause) {
		super(cause);
	}
}
