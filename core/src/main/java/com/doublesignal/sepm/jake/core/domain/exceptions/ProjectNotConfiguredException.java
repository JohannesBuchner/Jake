package com.doublesignal.sepm.jake.core.domain.exceptions;

/**
 * @author domdorn
 */
public class ProjectNotConfiguredException extends Exception {
	public ProjectNotConfiguredException()
	{
		super();	//To change body of overridden methods use File | Settings | File Templates.
	}

	public ProjectNotConfiguredException(String s)
	{
		super(s);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public ProjectNotConfiguredException(String s, Throwable throwable)
	{
		super(s, throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public ProjectNotConfiguredException(Throwable throwable)
	{
		super(throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}
}
