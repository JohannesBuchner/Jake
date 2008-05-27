package com.doublesignal.sepm.jake.gui.i18n.exceptions;

/**
 * The number of arguments to fill in was not correct.
 */
@SuppressWarnings("serial")
public class IllegalNumberOfArgumentsException extends RuntimeException {
	public IllegalNumberOfArgumentsException(String message) {
		super(message);	
	}
}
