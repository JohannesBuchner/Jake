package com.doublesignal.sepm.jake.core.services;

import com.doublesignal.sepm.jake.core.domain.JakeObject;


/**
 * Elements that want to be informed about conflicts will implement this.
 *
 * @see JakeGuiAccess
 * @author johannes
 */
public interface IConflictCallback {
	public void conflictOccured(JakeObject jo);
}