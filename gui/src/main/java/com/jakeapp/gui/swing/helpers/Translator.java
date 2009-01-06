package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.exceptions.IllegalNumberOfArgumentsException;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

/**
 * @author: studpete
 */
public class Translator {
	private static final Logger log = Logger.getLogger(Translator.class);

	public static String get(ResourceMap map, String identifier, String... placeholderValues)
			  throws IllegalNumberOfArgumentsException {
		log.debug("Translating " + identifier);

		String result = map.getString(identifier);
		if (result == null)
			return identifier;

		for (int i = 0; i < placeholderValues.length; i++) {
			if (placeholderValues[i] == null)
				placeholderValues[i] = "(null)";
			result = result.replaceAll("%" + i + "%", placeholderValues[i]);
		}
		if (result.matches(".*%[0-9]{1,}%.*"))
			throw new IllegalNumberOfArgumentsException(
					  "The identifier '" + identifier + "' needs more arguments: " +
								 map.getString(identifier));

		return result;
	}
}
