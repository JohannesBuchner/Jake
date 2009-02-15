package com.jakeapp.gui.swing.exceptions;

import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;

/**
 * @author: studpete
 */
public class FileOperationFailedException extends NestedException {
	public FileOperationFailedException(FrontendNotLoggedInException e) {
		super(e);
	}
}
