package com.jakeapp.gui.swing.exceptions;

/**
 * @author: studpete
 */
public class FileOperationFailedException extends NestedException {
	public FileOperationFailedException(Exception e) {
		super(e);
	}
}
