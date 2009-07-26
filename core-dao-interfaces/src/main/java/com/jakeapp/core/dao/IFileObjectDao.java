package com.jakeapp.core.dao;

import java.util.UUID;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.FileObject;

/**
 * Interface for an <code>IJakeObjectDao</code>-Implementation that performs special
 * actions for <code>FileObject</code>s.
 */
public interface IFileObjectDao extends IJakeObjectDao<FileObject> {
	/**
	 * Gets the <code>FileObject</code> that is located at a given relative path
	 * @param relpath the relative Path to the <code>FileObject</code>
	 * @return the <code>FileObject</code> requested
	 * @throws NoSuchJakeObjectException if the <code>FileObject</code> in question does not exist
	 */
	public FileObject get(final String relpath) throws NoSuchJakeObjectException;
}
