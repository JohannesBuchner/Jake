package com.doublesignal.sepm.jake.core.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * This is the base of all Objects used in jake, like files, notes, etc.
 * The <code>JakeObject</code> has a <code>name </code> and may be tagged with
 * <code>tags</code>.
 */
public class JakeObject {

    private String name;
    private LinkedList<Tag> tags;
    
    /**
     * Get the <code>name</code> of the object.
     * @return <code>name</code>
     */
    public String getName() {
    	return name;
    }
    
    /**
     * Get the tags of the object
     * @return List of <code>tags</code> that are appended to this object.
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Add a tag to the object.
     * @param tag to be added
     */
    public void addTag(Tag tag) {
        tags.add(tag);
    }

    /**
     * Removes all <code>tags</code> from the object that are equal to <code>tag</code>.
     * @param tag to be removed
     */
    public void removeTag(Tag tag) {
        /* I'm affright that is does not do what i want. remove() should be repeated
         * as long as it returns true...
         */ 
    	while (tags.remove(tag));
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
