package com.jakeapp.gui.swing.helpers;

/**
 * @author: studpete
 */
public class StringUtilities {

	/**
	 * Returns a string that is cut by maxlen
	 *
	 * @param str
	 * @param maxlen
	 * @return string not larger than maxlen
	 */
	public static String maxLen(String str, int maxlen) {
		if (str.length() > maxlen) {
			return str.substring(0, maxlen);
		} else {
			return str;
		}
	}

	// prevent instantiation
	private StringUtilities() {

	}
}
