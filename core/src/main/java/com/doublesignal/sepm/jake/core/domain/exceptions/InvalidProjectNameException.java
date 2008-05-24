package com.doublesignal.sepm.jake.core.domain.exceptions;

/**
 * @author domdorn
 */
public class InvalidProjectNameException extends Exception {
	public InvalidProjectNameException() {
		super();	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidProjectNameException(String s) {
		super(s);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidProjectNameException(String s, Throwable throwable) {
		super(s, throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidProjectNameException(Throwable throwable) {
		super(throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}
}
