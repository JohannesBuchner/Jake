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
		// fixme: what errors can you throw?
    public T persist(final T jakeObject);


    /**
     * Gets the <code>JakeObject</code> with the given Id
     *
     * @param objectId
     * @return
		 * @throws com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException
     */
    public T get(final UUID objectId) throws NoSuchJakeObjectException ;

    /**
     * UUID or identifier (relpath) can be null
     * this method returns the completed object
     * 
     * NOTE: the project is set to null in the JakeObjects. The caller of this method 
     * has to set it.
     * 
     * @param jakeObject
     * @return
     * @throws NoSuchJakeObjectException
     */
    public T complete(final T jakeObject) throws NoSuchJakeObjectException;
    
    /**
     * Get all <code>JakeObject</code>s that are associated with the
     * given project.
     *
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
