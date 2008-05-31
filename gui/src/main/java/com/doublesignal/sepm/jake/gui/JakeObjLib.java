package com.doublesignal.sepm.jake.gui;

import java.util.Set;

import com.doublesignal.sepm.jake.core.domain.Tag;

/**
 * Library for various JakeObject-related gui functions.
 * 
 * @author peter
 * 
 */
public final class JakeObjLib {

	/**
	 * Get String of Tags
	 * 
	 * @param tags
	 * @return
	 */
	public static Object getTagString(Set<Tag> tags) {
		String sTags = "";
		for (Tag tag : tags) {
			sTags = tag.toString() + ((!sTags.isEmpty()) ? ", " + sTags : "");
		}
		return sTags;
	}
}
