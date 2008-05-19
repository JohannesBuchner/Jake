package com.doublesignal.sepm.jake.core.domain.exceptions;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 18, 2008
 * Time: 4:28:41 AM
 */
public class InvalidCharactersException extends Exception {
	public InvalidCharactersException()
	{
		super();	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidCharactersException(String s)
	{
		super(s);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidCharactersException(String s, Throwable throwable)
	{
		super(s, throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidCharactersException(Throwable throwable)
	{
		super(throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}
}
