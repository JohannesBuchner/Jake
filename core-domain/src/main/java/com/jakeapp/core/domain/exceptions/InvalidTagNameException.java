package com.jakeapp.core.domain.exceptions;

/**
 * Thrown if a tagname does not meet the requirements for
 * a <code>Tag</code>'s name.
 *
 * @author domdorn
 * @see com.jakeapp.core.domain.Tag#setName(String)
 */
public class InvalidTagNameException extends Exception {
	private static final long serialVersionUID = -4195059131863532692L;

	/**
	 * {@inheritDoc}
	 */
	public InvalidTagNameException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public InvalidTagNameException(final String s) {
		super(s);
	}

	/**
	 * {@inheritDoc}
	 */
	public InvalidTagNameException(final String s, final Throwable throwable) {
		super(s, throwable);
	}

	/**
	 * {@inheritDoc}
	 */
	public InvalidTagNameException(final Throwable throwable) {
		super(throwable);
	}
}
