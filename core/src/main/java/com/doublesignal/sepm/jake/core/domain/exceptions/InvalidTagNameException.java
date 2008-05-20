package com.doublesignal.sepm.jake.core.domain.exceptions;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 18, 2008
 * Time: 3:52:12 AM
 */
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
