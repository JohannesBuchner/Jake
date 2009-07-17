package com.jakeapp.jake.fss.exceptions;

/**
 * Thrown if a file that already exists should be created.
 * @author christopher
 *
 */
public class FileAlreadyExistsException extends Exception {
	private static final long serialVersionUID = -7962621103736249419L;

	public FileAlreadyExistsException() {
	}

	public FileAlreadyExistsException(String msg) {
		super(msg);
	}

	public FileAlreadyExistsException(Throwable arg0) {
		super(arg0);
	}
}
