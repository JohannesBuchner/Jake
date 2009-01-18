package com.jakeapp.core.dao;

import java.util.UUID;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.FileObject;

/**
 * Interface for an IJakeObjectDao-Implementation that performs special
 * actions for <code>FileObject</code>s.
 */
public interface IFileObjectDao extends IJakeObjectDao<FileObject> {
	/**
	 * Gets the <code>FileObject</code> that is located at a given relative path
	 * @param relpath
	 * @return
	 * @throws NoSuchJakeObjectException
	 */
	public FileObject get(final String relpath) throws NoSuchJakeObjectException;
}
