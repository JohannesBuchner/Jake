package com.doublesignal.sepm.jake.core.domain;


import java.util.HashSet;
import java.util.Set;

/**
 * This is the base of all Objects used in jake, like files, notes, etc.
 * The <code>JakeObject</code> has a <code>name </code> and may be tagged with
 * <code>tags</code>.
 */
public class JakeObject {

	private String name;
	private HashSet<Tag> tags = new HashSet<Tag>();

	/**
	 * Construct a new <code>JakeObject</code>.
	 * @param name the name of the object.
	 */
	public JakeObject(String name) {
		this.name = name;
	}

	/**
	 * Get the <code>name</code> of the object.
	 *
	 * @return <code>name</code>
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the tags of the object
	 *
	 * @return Set of <code>tags</code> that are appended to this object.
	 */
	public Set<Tag> getTags() {
		return tags;
	}

	/**
	 * Add a tag to the object.
	 *
	 * @param tag to be added
	 *
	 */
	public JakeObject addTag(Tag tag) {
		tags.add(tag);
		return this;
	}

	/**
	 * Removes all <code>tags</code> from the object that are equal to <code>tag</code>.
	 *
	 * @param tag to be removed
	 */
	public JakeObject removeTag(Tag tag) {
		tags.remove(tag);
		return this;
	}

	/**
	 * Test if this object is equal to obj
	 *
	 * @param obj the object to be tested
	 * @return <code>true</code> iff the <code>name</code> and <code>tags</code>
	 *         are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass()))
			return false;
		
		JakeObject that = (JakeObject) obj;

		if (name == null && that.getName() != null) 
			return false;
		if (name != null && !name.equals(that.getName())) 
			return false;
		
		return true;
	}
}
