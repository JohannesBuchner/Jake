package com.doublesignal.sepm.jake.core.dao.exceptions;

/**
 * The configuration option to look up was not found/assigned in the current
 * configuration.
 * 
 * @author domdorn
 */
@SuppressWarnings("serial")
public class NoSuchConfigOptionException extends Exception {
	public NoSuchConfigOptionException(String message) {
		super(message);
	}
}
