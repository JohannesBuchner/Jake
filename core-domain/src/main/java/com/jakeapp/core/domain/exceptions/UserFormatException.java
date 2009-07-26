package com.jakeapp.core.domain.exceptions;

/**
 * This exception gets thrown when someone tries to create a <code>User</code>
 * from a String, but the format of the <code>UserId</code> is not allowed by
 * this IM-Service
 */
public class UserFormatException extends Exception {
	private static final long serialVersionUID = 1428639791927216493L;

	/**
	 * {@inheritDoc}
	 */
	public UserFormatException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public UserFormatException(final String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public UserFormatException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public UserFormatException(final Throwable cause) {
		super(cause);
	}
}
