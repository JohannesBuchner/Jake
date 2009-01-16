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

	/**
	 * Debugging LIB
	 * Converts a array/hashtable/collection to a string
	 *
	 * @param array
	 * @return string representation of array
	 */
	public static String arrayToString(Object array) {
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
			StringBuffer sb = new StringBuffer("[");
			for (int i = 0; i < length; i++) {
				obj = Array.get(array, i);
				if (obj != null) {
					sb.append(obj);
				} else {
					sb.append("[NULL]");
				}
				if (i < lastItem) {
					sb.append(", ");
				}
			}
			sb.append("]");
			return sb.toString();
		}
	}
}
