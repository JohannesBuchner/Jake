package com.jakeapp.gui.swing.helpers;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * Debug Helper has various
 *
 * @author: studpete
 */
public class DebugHelper {
	private static boolean enabled = true;

	public enum DebugFormat {
		NO, BRACES, FULL
	}

	/**
	 * Debugging LIB
	 * Converts a array/hashtable/collection to a string
	 *
	 * @param array
	 * @param formatted
	 * @return string representation of array
	 */
	public static String arrayToString(Object array, DebugFormat formatted, int maxLen) {
		if (array == null) {
			return "[NULL]";
		} else {
			Object obj = null;
			if (array instanceof Hashtable) {
				array = ((Hashtable) array).entrySet().toArray();
			} else if (array instanceof HashSet) {
				array = ((HashSet) array).toArray();
			} else if (array instanceof Collection) {
				array = ((Collection) array).toArray();
			}
			int length = Array.getLength(array);
			int lastItem = length - 1;
			StringBuffer sb = new StringBuffer(((formatted == DebugFormat.FULL) ? "<html>" : "") + "[");
			for (int i = 0; i < length && (i < maxLen || maxLen < 0); i++) {
				obj = Array.get(array, i);
				if (obj != null) {
					sb.append(obj + ((formatted == DebugFormat.FULL || formatted == DebugFormat.BRACES) ? "<br>" : ""));
				} else {
					sb.append("[NULL]");
				}
				if (i < lastItem) {
					sb.append(", ");
				}
			}
			sb.append("]");
			if (formatted == DebugFormat.FULL) sb.append("</html>");
			return sb.toString();
		}
	}

	public static String arrayToString(Object array, DebugFormat format) {
		return arrayToString(array, format, -1);
	}

	/**
	 * Debugging LIB
	 * Converts a array/hashtable/collection to a string
	 *
	 * @param array
	 * @return string representation of array
	 */
	public static String arrayToString(Object array) {
		return arrayToString(array, DebugFormat.NO);
	}

	public static boolean isEnabled() {
		return enabled;
	}
}
