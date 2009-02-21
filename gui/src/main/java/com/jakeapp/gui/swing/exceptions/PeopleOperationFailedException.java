package com.jakeapp.gui.swing.exceptions;

/**
 * Exception for people related operations.
 * @author Simon
 *
 */
public class PeopleOperationFailedException extends NestedException {

	public PeopleOperationFailedException(Exception ex) {
		super(ex);
	}

	public PeopleOperationFailedException() {

	}
}
