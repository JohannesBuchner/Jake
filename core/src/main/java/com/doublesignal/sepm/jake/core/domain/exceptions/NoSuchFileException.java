package com.doublesignal.sepm.jake.core.domain.exceptions;

import java.io.IOException;

/**
 * @author domdorn
 */
public class NoSuchFileException extends IOException {
	public NoSuchFileException(String message) {
		super(message);
	}
}
