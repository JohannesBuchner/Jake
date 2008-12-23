package com.jakeapp.core.dao.exceptions;

/**
 * Exception thrown when a member would be added to
 * a <code>Project</code> twice.
 * @author christopher
 */
@SuppressWarnings("serial")
public class DuplicateProjectMemberException extends Exception {
	public DuplicateProjectMemberException(final String message) {
		super(message);
	}
}
