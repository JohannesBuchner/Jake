package com.doublesignal.sepm.jake.fss;

/**
 * Objects listening for file modifications have to implement this.
 * 
 * @author johannes
 */

public interface ModificationListener {
	/**
	 * Actions that can occur for a file on a filesystem.
	 */
	enum ModifyActions { CREATED, REMOVED, CHANGED };
	
	void fileModified(String relpath, ModifyActions action);
	
}
