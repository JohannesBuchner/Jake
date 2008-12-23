package com.jakeapp.core.domain.exceptions;

/**
 * Thrown if a tagname does not meet the requirements for
 * a <code>Tag</code>'s name.
 * @see com.jakeapp.core.domain.Tag#setName(String)
 * @author domdorn
 */
public class InvalidTagNameException extends Exception {
    private static final long serialVersionUID = -4195059131863532692L;

    public InvalidTagNameException()
	{
		super();
	}

	public InvalidTagNameException(final String s)
	{
		super(s);
	}

	public InvalidTagNameException(final String s, final Throwable throwable)
	{
		super(s, throwable);
	}

	public InvalidTagNameException(final Throwable throwable)
	{
		super(throwable);
	}
}
