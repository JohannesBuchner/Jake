package com.jakeapp.jake.ics.exceptions;

/**
 * @author domdorn, johannes
 * 
 * Is used on unexpected connection breakdown, transmission abort, etc. 
 */
@SuppressWarnings("serial")
public class NetworkException extends Exception {

	public NetworkException() {
		super();
	}

	public NetworkException(String message) {
		super(message);
	}

	public NetworkException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetworkException(Throwable cause) {
		super(cause);
	}
}
