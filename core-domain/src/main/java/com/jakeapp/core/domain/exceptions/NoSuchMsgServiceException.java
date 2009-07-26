package com.jakeapp.core.domain.exceptions;


/**
 * This Exception gets thrown when one tries to remove an account but it does not exist in the
 * database and therefor also no MsgService exists for it. 
 */
public class NoSuchMsgServiceException extends Exception {
	private static final long serialVersionUID = 7286960508376826692L;

	/**
	 * {@inheritDoc}
	 */
	public NoSuchMsgServiceException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public NoSuchMsgServiceException(final String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public NoSuchMsgServiceException(final String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public NoSuchMsgServiceException(final Throwable cause) {
		super(cause);
	}
}