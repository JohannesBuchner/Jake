package com.jakeapp.core.synchronization.exceptions;


public class InvalidDeserializerCallException extends RuntimeException {
	public InvalidDeserializerCallException() {
	}

	public InvalidDeserializerCallException(String message) {
		super(message);
	}
}
