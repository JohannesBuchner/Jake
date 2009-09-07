package com.jakeapp.gui.swing.exceptions;

/**
 * Thrown when a new folder cannot be created
 */
public class InvalidNewFolderException extends Exception {
	public InvalidNewFolderException(String msg) {
		super(msg);
	}
}
