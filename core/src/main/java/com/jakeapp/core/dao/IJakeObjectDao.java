package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.Tag;

import java.util.List;

/**
 * A generic interface for <code>JakeObject</code>s. 
 * @param <T> a subtype of <code>JakeObject</code>s.
 * Type of the persisted objects.
 * @author Chris, christopher, Simon
 */
public interface IJakeObjectDao <T extends JakeObject> {

	/**
	 * Persist a <code>JakeObject</code> by storing it to a database
     * permanently.
	 * @param jakeObject The <code>JakeObject</code>to be persisted
	 * @return The <code>JakeObject</code> that has been persisted
	 */
    T persist(T jakeObject);

    /**
     * Get all <code>JakeObject</code>s that are associated with the
     * given project.
     * @param project the assiciated project
     * @return all <code>JakeObject</code>s that are associated with the
     * given project
     */
    List<T> getAll(Project project);

    /**
     * Removes a <code>JakeObject</code> from its project's database
     * @param jakeObject the <code>JakeObject</code> to be deleted
     * @throws NoSuchJakeObjectException if the given <code>JakeObject</code>
     * does not exist.
     */
    public void makeTransient(T jakeObject) throws NoSuchJakeObjectException;

    /**
     * Add a tag to a <code>JakeObject</code>.
     * @param jakeObject the <code>JakeObject</code> to be tagged
     * @param tag the tag to be added 
     * @return the <code>JakeObject</code> that has been tagged
     */
    public T addTagTo(T jakeObject, final Tag tag);

	/**
	 * Convenience method to add one or more tags to a JakeObject
	 * @param jakeObject the jakeObject in question
	 * @param tags one or more tags
	 */
	public void addTagsTo(T jakeObject, final Tag... tags);

    /**
     * Remove a tag from a <code>JakeObject</code>. The tag will be removed if
     * it exists, if not, nothing happens.
     * @param jakeObject the <code>JakeObject</code> from which the tag
     * is removed
     * @param tag the tag to be removed
     * @return The <code>JakeObject</code> from which the tag has been removed.
     */
    public T removeTagFrom(T jakeObject, final Tag tag);

	/**
	 * Convenience method to remove one or more tags from a JakeObject.
	 * 	For <code>Tag</code>s that are not part of the
	 * 	<code>JakeObject</code>, nothing happens.
	 *  After calling this method, <code>jakeObject</code> will
	 *  not contain any <code>Tag</code> specified in <code>tags</code>.
	 * @param jakeObject the jakeObject in question
	 * @param tags one or more tags
	 */
	public void removeTagsFrom(T jakeObject, Tag... tags);


    /**
     * Return all tags of a specific JakeObject. 
     * @param jakeObject the jakeObject in question
     * @return List of Tags
     */
    public List<Tag> getTagsFor(T jakeObject);

}
