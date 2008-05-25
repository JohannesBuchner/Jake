package com.doublesignal.sepm.jake.gui.i18n;

import com.doublesignal.sepm.jake.gui.i18n.exceptions.IllegalNumberOfArgumentsException;

/**
 * Defines an interface for a translation provider that retrieves internationalized
 * messages from some sort of data source.
 *
 * @author Chris
 */
public interface ITranslationProvider {
	/**
	 * Sets the language to be used by this TranslationProvider.
	 *
	 * @param language The ISO 639-1 code of the language
	 */
	public void setLanguage(String language);

	/**
	 * Retrieves the internationalized message for a given message ID
	 *
	 * Allows for placeholders (%0%, %1%, ..., %n%) in the message to be replaced
	 * with values in the vararg argument placeholderValues in the given order.
	 *
	 * Note that there is no exception if a translation is not found. This is so
	 * that one or two missing translations in a language don't result in total
	 * chaos, weird error messages to the end user or abnormal program termination.
	 * Instead, if a translation cannot be found for the given language and message
	 * identifier, the TranslationProvider will return the identifier itself.
	 *
	 * This way, translations can be used/tested as early as possible even though
	 * they might still be incomplete.
	 *
	 * @param messageIdentifier ID of the message
	 * @param placeholderValues Values that the placeholders in the message should
	 *                          be replaced with
	 * @return A string in the target language with placeholder values filled in
	 * @throws IllegalNumberOfArgumentsException if the number of varargs passed
	 *                                           for placeholderValues does not
	 *                                           match the number of placeholders
	 *                                           in the translation message.
	 */
	public String getTranslation(String messageIdentifier,
	                             String... placeholderValues)
			                       throws IllegalNumberOfArgumentsException;
}
