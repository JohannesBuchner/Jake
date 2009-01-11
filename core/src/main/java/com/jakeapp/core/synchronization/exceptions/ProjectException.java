package com.jakeapp.core.synchronization.exceptions;


/**
 * An error happened while starting the project
 * 
 * @author johannes
 */
@SuppressWarnings("serial")
public class ProjectException extends Exception {

	public ProjectException() {
		super();
	}

	public ProjectException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProjectException(String message) {
		super(message);
	}

	public ProjectException(Throwable cause) {
		super(cause);
	}

}
