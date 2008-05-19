package com.doublesignal.sepm.jake.core.domain.exceptions;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 18, 2008
 * Time: 4:26:44 AM
 */
public class InvalidNicknameException extends Exception {
	public InvalidNicknameException(String s)
	{
		super(s);
	}

	public InvalidNicknameException()
	{
		super();	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidNicknameException(String s, Throwable throwable)
	{
		super(s, throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidNicknameException(Throwable throwable)
	{
		super(throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}
}
