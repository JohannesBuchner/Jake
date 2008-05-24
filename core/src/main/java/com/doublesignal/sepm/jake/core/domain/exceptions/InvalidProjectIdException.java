package com.doublesignal.sepm.jake.core.domain.exceptions;

/**
 * @author domdorn
 */
public class InvalidProjectIdException extends Exception {
	public InvalidProjectIdException() {
		super();	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidProjectIdException(String s) {
		super(s);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidProjectIdException(String s, Throwable throwable) {
		super(s, throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidProjectIdException(Throwable throwable) {
		super(throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}
}
