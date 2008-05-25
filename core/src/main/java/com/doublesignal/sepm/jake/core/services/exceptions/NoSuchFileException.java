package com.doublesignal.sepm.jake.core.services.exceptions;

import java.io.IOException;

/**
 * @author domdorn
 */
public class NoSuchFileException extends IOException {
	public NoSuchFileException(String message) {
		super(message);
	}
}
