package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.Tag;

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
     * @param objectId
     * @return
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

    /**
     * Add a tag to a <code>JakeObject</code>.
     *
     * @param jakeObject the <code>JakeObject</code> to be tagged
     * @param tag        the tag to be added
     * @return the <code>JakeObject</code> that has been tagged
     */
    public T addTagTo(final T jakeObject, final Tag tag) throws NoSuchJakeObjectException ;

    /**
     * Convenience method to add one or more tags to a JakeObject
     *
     * @param jakeObject the jakeObject in question
     * @param tags       one or more tags
     */
    public void addTagsTo(final T jakeObject, final Tag... tags) throws NoSuchJakeObjectException ;

    /**
     * Remove a tag from a <code>JakeObject</code>. The tag will be removed if
     * it exists, if not, nothing happens.
     *
     * @param jakeObject the <code>JakeObject</code> from which the tag
     *                   is removed
     * @param tag        the tag to be removed
     * @return The <code>JakeObject</code> from which the tag has been removed.
     */
    public T removeTagFrom(final T jakeObject, final Tag tag) throws NoSuchJakeObjectException ;

    /**
     * Convenience method to remove one or more tags from a JakeObject.
     * For <code>Tag</code>s that are not part of the
     * <code>JakeObject</code>, nothing happens.
     * After calling this method, <code>jakeObject</code> will
     * not contain any <code>Tag</code> specified in <code>tags</code>.
     *
     * @param jakeObject the jakeObject in question
     * @param tags       one or more tags
     */
    public void removeTagsFrom(final T jakeObject, final Tag... tags) throws NoSuchJakeObjectException ;


    /**
     * Return all tags of a specific JakeObject.
     *
     * @param jakeObject the jakeObject in question
     * @return List of Tags
     */
    public List<Tag> getTagsFor(final T jakeObject) throws NoSuchJakeObjectException ;

}
