package com.jakeapp.violet.model.attributes;


/**
 * @author johannes
 */
public enum Existence {
	NON_EXISTANT,

	EXISTS_LOCAL,

	EXISTS_REMOTE,

	EXISTS_ON_BOTH, ;

	/**
	 * @param objectExistsLocally
	 *            File is in FileSystem, Note is associated to Project
	 * @param lastLogAction
	 *            {@link LogAction#JAKE_OBJECT_NEW_VERSION} or
	 *            {@link LogAction#JAKE_OBJECT_DELETE} or null.
	 * @returnExistance
	 * @return
	 */
	static Existence getExistance(boolean objectExistsLocally,
			boolean lastWasNewVersion) {

		if (objectExistsLocally) {
			if (lastWasNewVersion)
				return EXISTS_ON_BOTH;
			else
				return EXISTS_LOCAL;
		} else {
			if (lastWasNewVersion)
				return EXISTS_REMOTE;
			else
				return NON_EXISTANT;
		}

	}
}
