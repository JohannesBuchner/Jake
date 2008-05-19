package com.doublesignal.sepm.jake.core.domain;

import java.io.Serializable;

/**
 * A <code>NoteObject</code> is a Extension of a JakeObject
 * consists of <code>content</code>.
 * @author Dominik, Philipp
 */

public class NoteObject extends JakeObject implements Serializable {

    private String content;

    public NoteObject(String content) {
        this.content = content;


    }

    /**
     * Get the content of a NoteObject.
     *
     * @return content
     */
    public String getContent() {
        return content;
    }
    /**
     * Set the content of a NoteObject.
     *
     * @return content
     */
    public void setContent(String content) {
        this.content = content;
    }


    /**
	 * Tests if two contents are equal.
	 * 
	 * @return <code>true</code> if <code>content</code>
	 * is equal.
	 */
	@Override
	public boolean equals(Object obj) {
        if (obj != null && obj.getClass().equals(this.getClass())) {
        	NoteObject that = (NoteObject) obj;
        	return (this.content.equals(that.getContent()));
        }
        return false;
    }
}
