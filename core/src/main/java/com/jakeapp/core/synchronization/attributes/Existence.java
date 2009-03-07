package com.jakeapp.core.synchronization.attributes;

import com.jakeapp.core.domain.LogAction;

/**
 * @author johannes
 */
public enum Existence {
	NON_EXISTANT,

	EXISTS_LOCAL,

	EXISTS_REMOTE,

	EXISTS_ON_BOTH,;

	/**
	 * @param objectExistsLocally File is in FileSystem, Note is associated to Project
	 * @param lastLogAction			 {@link LogAction#JAKE_OBJECT_NEW_VERSION} or
	 *                            {@link LogAction#JAKE_OBJECT_DELETE} or null.
	 * @returnExistance
	 * @return
	 */
	static Existence getExistance(boolean objectExistsLocally,
																LogAction lastLogAction) {

		if (objectExistsLocally) {
			if (lastLogAction == LogAction.JAKE_OBJECT_DELETE || lastLogAction == null)
				return EXISTS_LOCAL;
			else if (lastLogAction == LogAction.JAKE_OBJECT_NEW_VERSION)
				return EXISTS_ON_BOTH;
			else throw new IllegalStateException("state was: objectExistsLocally = " + lastLogAction);
		} else {
			if (lastLogAction == LogAction.JAKE_OBJECT_DELETE || lastLogAction == null)
				return NON_EXISTANT;
			else if (lastLogAction == LogAction.JAKE_OBJECT_NEW_VERSION)
				return EXISTS_REMOTE;
			else throw new IllegalStateException("state was: objectExistsLocally = " + lastLogAction);
		}

	}
}
