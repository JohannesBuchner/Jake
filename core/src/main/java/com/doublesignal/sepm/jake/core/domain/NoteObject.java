package com.doublesignal.sepm.jake.core.domain;

import java.io.Serializable;

/**
 * A <code>NoteObject</code> is a Extension of a JakeObject
 * consists of <code>content</code>.
 *
 * @author Dominik, Philipp
 */

public class NoteObject extends JakeObject {

	private String content;

	/**
	 * Construct a new <code>NoteObject</code>. 
	 * @param name The name of the object, as in <code>JakeObject</code>
	 * @param content The content of the note
	 */
	public NoteObject(String name, String content) {
		super(name);
		this.content = content;
	}

	/**
	 * Get the content of a <code>NoteObject</code>.
	 *
	 * @return content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Set the content of a <code>NoteObject</code>.
	 *
	 * @return content
	 */
	public void setContent(String content) {
		this.content = content;
	}


	/**
	 * Tests if two notes are equal.
	 *
	 * @return <code>true</code> iff the <code>contents</code> of the two notes 
	 * are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass().equals(this.getClass())) {
			NoteObject that = (NoteObject) obj;
			
			if (content == null && that.getContent() != null) return false;
			if (content != null && !content.equals(that.getContent())) return false;
		}
		return true;
	}
}
