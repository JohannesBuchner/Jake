package com.jakeapp.core.dao.exceptions;

/**
 * Exception that is thrown if a <code>Project</code>'s owner is set
 * to another owner than it had before. A <code>Project</code>'s
 * owner may only be set once.
 * @author christopher
 */
public class ProjectOwnerChangedException extends IllegalStateException {
    private static final long serialVersionUID = 624387346861671586L;

    public ProjectOwnerChangedException(final String message) {
		super(message);
	}
}
