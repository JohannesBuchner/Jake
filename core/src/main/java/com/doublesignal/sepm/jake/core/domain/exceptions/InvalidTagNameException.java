package com.doublesignal.sepm.jake.core.domain.exceptions;

/**
 * @author domdorn
 */
@SuppressWarnings("serial")
public class InvalidTagNameException extends Exception {
	public InvalidTagNameException()
	{
		super();	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidTagNameException(String s)
	{
		super(s);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidTagNameException(String s, Throwable throwable)
	{
		super(s, throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidTagNameException(Throwable throwable)
	{
		super(throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}
}
