package com.jakeapp.gui.swing.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * This Exception is raised whenever a note could not be created.
 * @author Simon
 *
 */
public class NoteOperationFailedException extends Exception {
	
	private List<Exception> nestedExceptions;
	{
		this.nestedExceptions = new ArrayList<Exception>();
	}
	
	public void append(Exception e) {
		this.nestedExceptions.add(e);
	}

	/**
	 * Get a list of nested <code>Exception</code>s.
	 * @return list of nested <code>Exception</code>s
	 */
	protected List<Exception> getNestedExceptions() {
		return this.nestedExceptions;
	}

	@Override
	public String getMessage() {
		String str = new String();
		str = "CouldNotCreateNoteException: " + super.getMessage();
		
		return str;
	}
	
	
}
