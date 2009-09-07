package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.gui.swing.exceptions.InvalidTagStringFormatException;
import com.jakeapp.gui.swing.JakeMainApp;

import java.util.*;

public class TagHelper {
	public static String tagsToString(Set<Tag> tags) {
		if (tags == null || tags.isEmpty()) return "";

		StringBuffer sb = new StringBuffer();
		for (Tag t : tags) {
			sb.append(t.getName()).append(" ");
		}

		return sb.toString();
	}

	public static Set<Tag> stringToTags(FileObject fo, String tags) throws InvalidTagStringFormatException {
		Set<Tag> oldtags = JakeMainApp.getCore().getTagsForFileObject(fo);
		Set<Tag> newtags = new HashSet<Tag>();

		if (!tags.matches("[-a-zA-Z0-9_ ]*") || tags.matches("( ){2,}")) {
			throw new InvalidTagStringFormatException();
		}

		ArrayList<String> tagstrings = new ArrayList<String>(Arrays.asList(tags.split(" ")));

		// Add all existing tags
		for (Tag t : oldtags) {
			if (tagstrings.contains(t.getName())) {
				newtags.add(t);
				tagstrings.remove(t.getName());
			}
		}

		// Add all remaining (=new) tags
		for (String s : tagstrings) {
			try {
				newtags.add(new Tag(s));
			} catch (InvalidTagNameException e) {
				// Ignore, simply don't add the tag
			}
		}

		return newtags;
	}
}
