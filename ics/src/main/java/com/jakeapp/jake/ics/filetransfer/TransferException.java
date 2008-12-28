package com.jakeapp.jake.ics.filetransfer;

/**
 * Something went wrong with the transfer
 * @author johannes
 */
@SuppressWarnings("serial")
public class TransferException extends Exception {

	public TransferException() {
		super();
	}

	public TransferException(String message) {
		super(message);
	}

	public TransferException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransferException(Throwable cause) {
		super(cause);
	}
}
