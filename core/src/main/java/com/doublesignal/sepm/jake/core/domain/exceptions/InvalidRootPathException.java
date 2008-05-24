package com.doublesignal.sepm.jake.core.domain.exceptions;

/**
 * @author domdorn
 */
public class InvalidRootPathException extends Exception {
	public InvalidRootPathException() {
		super();	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidRootPathException(String s) {
		super(s);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidRootPathException(String s, Throwable throwable) {
		super(s, throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidRootPathException(Throwable throwable) {
		super(throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}
}
