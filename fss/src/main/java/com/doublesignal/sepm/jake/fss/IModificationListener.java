package com.doublesignal.sepm.jake.fss;

import java.io.File;

/**
 * Objects listening for file modifications have to implement this.
 * 
 * @author johannes
 */

public interface IModificationListener {
	/**
	 * Actions that can occur for a file on a filesystem.
	 */
	public enum ModifyActions { CREATED, DELETED, MODIFIED }
	
	void fileModified(File f, ModifyActions action);
	
}
