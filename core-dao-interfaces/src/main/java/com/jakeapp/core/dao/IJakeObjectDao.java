package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.JakeObject;

import java.util.List;
import java.util.UUID;

/**
 * A generic interface for <code>JakeObject</code>s.
 *
 * @author Chris, christopher, Simon
 * @param <T> a subtype of <code>JakeObject</code>s.
 * Type of the persisted objects.
 */
public interface IJakeObjectDao<T extends JakeObject> {

	/**
	 * Persist a <code>JakeObject</code> by storing it to a database
	 * permanently.
	 *
	 * @param jakeObject The <code>JakeObject</code>to be persisted
	 * @return The <code>JakeObject</code> that has been persisted
	 */
	public T persist(final T jakeObject);


	/**
	 * Gets the <code>JakeObject</code> with the given Id
	 *
	 * @param objectId the <code>UUID</code> of the <code>JakeObject</code> we want to get
	 * @return the <code>JakeObject</code> corresponding to the <code>UUID</code> given
	 * @throws NoSuchJakeObjectException if no such <code>JakeObject</code> exists
	 */
	public T get(final UUID objectId) throws NoSuchJakeObjectException;

	/**
	 * UUID or identifier (relpath) can be null
	 * this method returns the completed <code>JakeObject</code>
	 * <br />
	 * NOTE: the <code>Project</code> is set to null in the <code>JakeObject</code>s. The caller of this method
	 * has to set it.
	 *
	 * @param jakeObject an incomplete <code>JakeObject</code>
	 * @return the completed <code>JakeObject</code> (without the <code>Project</code> set!)
	 * @throws NoSuchJakeObjectException if no such <code>JakeObject</code> can be found.
	 */
	public T complete(final T jakeObject) throws NoSuchJakeObjectException;

	/**
	 * Get all <code>JakeObject</code>s that are associated with the given project.
	 * <p/>
	 * NOTE: the project is set to null in the JakeObjects. The caller of this method
	 * has to set it.
	 *
	 * @return all <code>JakeObject</code>s that are associated with the
	 *         given project
	 */
	public List<T> getAll();

	/**
	 * Removes a <code>JakeObject</code> from its project's database
	 *
	 * @param jakeObject the <code>JakeObject</code> to be deleted
	 * @throws NoSuchJakeObjectException if the given <code>JakeObject</code>
	 *                                   does not exist.
	 */
	public void delete(final T jakeObject) throws NoSuchJakeObjectException;

}
