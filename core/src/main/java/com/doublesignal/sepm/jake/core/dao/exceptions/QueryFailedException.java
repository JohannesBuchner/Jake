package com.doublesignal.sepm.jake.core.dao.exceptions;

/**
 * Thrown if a query fails for any reason
 */
public class QueryFailedException extends RuntimeException {
	public QueryFailedException(String message) {
		super(message);
	}
}
