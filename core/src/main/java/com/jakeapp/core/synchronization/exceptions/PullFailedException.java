package com.jakeapp.core.synchronization.exceptions;


public class PullFailedException extends Exception {
	private static final long serialVersionUID = 1L;

	public PullFailedException() {
		super();
	}

	public PullFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PullFailedException(String message) {
		super(message);
	}

	public PullFailedException(Throwable cause) {
		super(cause);
	}
	
}
