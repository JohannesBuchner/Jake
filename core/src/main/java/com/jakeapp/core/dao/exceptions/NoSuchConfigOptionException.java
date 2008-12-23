package com.jakeapp.core.dao.exceptions;

/**
 * The configuration option to look up was not found/assigned in the current
 * configuration.
 * 
 * @author domdorn
 */
@SuppressWarnings("serial")
public class NoSuchConfigOptionException extends Exception {
	public NoSuchConfigOptionException(final String message) {
		super(message);
	}
}
