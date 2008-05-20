package com.doublesignal.sepm.jake.core.domain.exceptions;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 18, 2008
 * Time: 1:24:37 PM
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
