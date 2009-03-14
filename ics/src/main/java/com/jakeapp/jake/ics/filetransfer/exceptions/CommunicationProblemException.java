package com.jakeapp.jake.ics.filetransfer.exceptions;

/**
 * Thrown if user tries to negotiate a transfer, but is not online
 * @author johannes
 *
 */
@SuppressWarnings("serial")
public class CommunicationProblemException extends Exception {

	public CommunicationProblemException() {
		super();
	}

	public CommunicationProblemException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommunicationProblemException(String message) {
		super(message);
	}

	public CommunicationProblemException(Throwable cause) {
		super(cause);
	}
}
