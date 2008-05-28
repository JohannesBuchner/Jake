package com.doublesignal.sepm.jake.core.domain;

import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidTagNameException;

/**
 * A simple tag. It only consists of a <code>name</code>.
 */
public class Tag {

	private String name;

	/**
	 * Construct a new Tag. The tag must not contain white-spaces.
	 * @param name
	 * @throws InvalidTagNameException
	 * @see #setName(String)
	 */
	public Tag(String name) throws InvalidTagNameException {
		setName(name);
	}

	public String getName() {
		return name;
	}

	/**
	 * Set the name of the <code>tag</code>. 
	 * @param name the new name of the tag. It must not contain a whitespace-character.
	 * @throws InvalidTagNameException Raised if the <code>name</code> contains
	 * a whitespace-character
	 */
	public void setName(String name) throws InvalidTagNameException {
		if (name.contains(" "))
			throw new InvalidTagNameException("A Tag may not contain a whitespace");
		this.name = name;
	}

	/**
	 * Test if two <code>tag</code>s are equal.
	 * @return <code>true</code> iff the <code>name</code>s are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass()))
			return false;
		
		Tag that = (Tag)obj;
		
		if (name == null && that.getName() != null)
			return false;
		if (name != null && !name.equals(that.getName()))
			return false;
		
		return true;
	}

	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * Returns the string representation of the <code>tag</code>.
	 * @return the name of the tag.
	 */
	public String toString() {
		return name;
	}
}
