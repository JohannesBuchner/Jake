package com.doublesignal.sepm.jake.core.domain.exceptions;

/**
 * @author domdorn
 */
public class InvalidCharactersException extends Exception {
	public InvalidCharactersException() {
		super();	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidCharactersException(String s) {
		super(s);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidCharactersException(String s, Throwable throwable) {
		super(s, throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidCharactersException(Throwable throwable) {
		super(throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}
}
