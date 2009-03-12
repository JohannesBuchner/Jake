package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.NoteObject;

/**
 * @author studpete
 */
public class NotesHelper {

	/**
	 * Get the first line/first 100 Chars of the Note.
	 *
	 * @param noteObject
	 * @return
	 */
	public static String getTitle(NoteObject noteObject) {
		String content = noteObject.getContent();
		if(content.contains("\n"))
			content = content.substring(0, content.indexOf('\n'));
		return content.substring(0, (content.length() > 100) ? 100 : content.length());
	}
}
