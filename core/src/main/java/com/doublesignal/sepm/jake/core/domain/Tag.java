package com.doublesignal.sepm.jake.core.domain;

import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidTagNameException;

/**
 * A Tag belongs to a JakeObject.
 */
public class Tag {
	/**
	 * The name of the tag
	 */
	private String name;

	public Tag(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) throws InvalidTagNameException {
		this.name = name;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Tag tag = (Tag) o;

		return name.equals(tag.getName());
	}

	public int hashCode() {
		return name.hashCode();
	}
}
