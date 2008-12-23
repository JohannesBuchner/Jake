package com.jakeapp.jake.fss.exceptions;

import java.io.IOException;

/**
 * Launching the file using the OS failed
 * @author johannes
 *
 */
@SuppressWarnings("serial")
public class LaunchException extends Exception {
	IOException innerException;
	public LaunchException(IOException innerException) {
		this.innerException = innerException;
	}
	public IOException getInnerException() {
		return innerException;
	}

}
