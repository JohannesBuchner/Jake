package com.jakeapp.gui.swing.helpers;

/**
 * Various Utilities to work better with strings.
 *
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

	/**
	 * Makes a string bold if condition bold is true.
	 * Uses html to make the changes.
	 * Do not forget to add <html></html> into your string!
	 *
	 * @param str:  string to make bold (eventually)
	 * @param bold: surrounds string with <b> on true
	 * @return str or <b>str</b>
	 */
	public static String boldIf(String str, boolean bold) {
		if (bold) {
			return "<b>" + str + "</b>";
		} else {
			return str;
		}
	}

	// prevent instantiation
	private StringUtilities() {

	}

	/**
	 * Surrounds string with html
	 *
	 * @param str
	 * @return string with html tags sorrounded.
	 */
	public static String htmlize(String str) {
		return "<html>" + str + "</html>";
	}
}
