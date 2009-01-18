package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.exceptions.IllegalNumberOfArgumentsException;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

/**
 * Translator to inject parameters into i18n strings that are retrieved from the given <code>
 * ResourceMap</code>.
 * @author: studpete, simon
 */
public class Translator {
	private static final Logger log = Logger.getLogger(Translator.class);

	/**
	 * Get a i18n from the <code>ResourceMap</code> with the injected place holder values.
	 * <p><b>usage:</p> The translator replaces all %i% tokens in the string retrieved from the map, where i denotes 
	 * which of the given place holder values is to be inserted, starting at index 0. e.g: </p>
	 * <p><code>get(map, id, "fine", "yeah")<code></p>
	 * with the i18n string in the map: <code>id=The weather is %0%, %1%, %1%!<code></p>
	 * <p>It will return the string <code>The weather is fine, yeah, yeah!</code></p>
	 * @param map the <code>ResourceMap</code> from which the i18n strings are taken. 
	 * @param identifier the identifier for the look up in the map
	 * @param placeholderValues place holder values that are inserted.
	 * @return the i18n string with the injected place holder values.
	 */
	public static String get(ResourceMap map, String identifier, String... placeholderValues) {
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
