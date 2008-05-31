package com.doublesignal.sepm.jake.gui;

import com.doublesignal.sepm.jake.core.domain.Tag;

import java.util.Set;

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
	public static String getTagString(Set<Tag> tags) {
		String sTags = "";


		for (Tag tag : tags) {
			sTags = tag.toString() + ((!sTags.isEmpty()) ? ", " + sTags : "");
		}
		return sTags;
	}
}
