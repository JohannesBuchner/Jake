package com.doublesignal.sepm.jake.core.domain;

import java.io.Serializable;
import java.util.Date;

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
	 * @param name The name of the object, as in <code>JakeObject</code>. 
	 *    It starts with 'note:'
	 * @param content The content of the note
	 */
	public NoteObject(String name, String content) {
		super(name);
		this.content = content;
	}
	
	/**
	 * Creates a new JakeObject: The name is constructed like this: 
	 * 'note:' + userid + ':' + timestamp
	 */
	public static NoteObject createNoteObject(String userid, String content) {
		return new NoteObject(
				"note:" + userid + ":" + System.currentTimeMillis(), 
				content);
	}
	
	public String getContent() {
		return content;
	}
	
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

			if (getName() == null && that.getName() != null) 
				return false;
			if (getName() != null && !getName().equals(that.getName())) 
				return false;
			
			if (content == null && that.getContent() != null) return false;
			if (content != null && !content.equals(that.getContent())) return false;
		}
		return true;
	}
}
