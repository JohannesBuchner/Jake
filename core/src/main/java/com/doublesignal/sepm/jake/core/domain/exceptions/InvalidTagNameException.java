package com.doublesignal.sepm.jake.core.domain.exceptions;

/**
 * @author domdorn
 */
@SuppressWarnings("serial")
public class InvalidTagNameException extends Exception {
	public InvalidTagNameException()
	{
		super();
	}

	public InvalidTagNameException(String s)
	{
		super(s);
	}

	public InvalidTagNameException(String s, Throwable throwable)
	{
		super(s, throwable);
	}

	public InvalidTagNameException(Throwable throwable)
	{
		super(throwable);
	}
}
