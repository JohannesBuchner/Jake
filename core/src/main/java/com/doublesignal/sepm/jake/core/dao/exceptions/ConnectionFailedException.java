package com.doublesignal.sepm.jake.core.dao.exceptions;

/**
 * Thrown if unable to connect to DB
 */
public class ConnectionFailedException extends RuntimeException {
	public ConnectionFailedException(String message) {
		super(message);
	}
}
