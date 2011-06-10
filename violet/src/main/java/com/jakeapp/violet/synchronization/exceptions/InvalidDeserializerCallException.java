package com.jakeapp.violet.synchronization.exceptions;


public class InvalidDeserializerCallException extends RuntimeException {
	public InvalidDeserializerCallException() {
	}

	public InvalidDeserializerCallException(String message) {
		super(message);
	}
}
