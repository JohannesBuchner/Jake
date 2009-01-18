package com.jakeapp.core.domain.exceptions;

/**
 * This Exception gets thrown if a client tries to access a method but
 * is not logged
 * in and therefor nor authorized to do so.
 */
public class NotLoggedInException extends RuntimeException {
	private static final long serialVersionUID = 7900725036326667321L;

	public NotLoggedInException() {
		super();
	}

	public NotLoggedInException(final String message) {
		super(message);
	}

	public NotLoggedInException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NotLoggedInException(final Throwable cause) {
		super(cause);
	}
}
