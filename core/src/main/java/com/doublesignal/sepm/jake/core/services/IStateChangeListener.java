package com.doublesignal.sepm.jake.core.services;

import com.doublesignal.sepm.jake.core.domain.JakeObject;


/**
 * Elements that want to be informed about changes in a Objects status will 
 * implement this.
 *
 * @see JakeGuiAccess
 * @author johannes
 */
public interface IStateChangeListener {
	/**
	 * when called with a null value, it signals all objects may have changed
	 * @param jo
	 */
	public void stateChanged(JakeObject jo);
}