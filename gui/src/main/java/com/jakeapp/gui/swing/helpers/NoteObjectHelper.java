package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.NoteObject;

/**
 * @author: studpete
 */
public class NoteObjectHelper {

	/**
	 * Get the first 100 Chars of the Note.
	 *
	 * @param noteObject
	 * @return
	 */
	public static String getTitle(NoteObject noteObject) {
		String content = noteObject.getContent();
		return content.substring(0, (content.length() > 100) ? 100 : content.length());
	}
}
