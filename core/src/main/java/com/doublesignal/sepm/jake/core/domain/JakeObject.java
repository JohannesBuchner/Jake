package com.doublesignal.sepm.jake.core.domain;

import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidTagNameException;

import java.util.Set;
import java.util.HashSet;

/**
 * This is the base of all Objects used in jake, like files, notes, etc.
 * The <code>JakeObject</code> has a <code>name </code> and may be tagged with
 * <code>tags</code>.
 */
public class JakeObject {

    private String name;
    private HashSet<Tag> tags = new HashSet<Tag>();

	public JakeObject(String name) {
		this.name = name;	
	}
    
    /**
     * Get the <code>name</code> of the object.
     * @return <code>name</code>
     */
    public String getName() {
    	return name;
    }
    
    /**
     * Get the tags of the object
     * @return Set of <code>tags</code> that are appended to this object.
     */
    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * Add a tag to the object.
     * @param tag to be added
     * @throws com.doublesignal.sepm.jake.core.domain.exceptions.InvalidTagNameException
     */
    public void addTag(Tag tag) throws InvalidTagNameException {
        tags.add(tag);
    }

    /**
     * Removes all <code>tags</code> from the object that are equal to <code>tag</code>.
     * @param tag to be removed
     */
    public void removeTag(Tag tag) {
	    tags.remove(tag);
    }

    /**
     * Test if this object is equal to obj
     * @param obj the object to be tested
     * @return <code>true</code> iff the <code>name</code> and <code>tags</code>
     * are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && this.getClass().equals(obj.getClass())) {
        	JakeObject that = (JakeObject)obj;
        	return (this.name.equals(that.getName()) &&
        	        this.tags.equals(that.getTags()));
        }
        return false;
    }
}
