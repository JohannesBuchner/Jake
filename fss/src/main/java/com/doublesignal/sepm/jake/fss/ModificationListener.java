package com.doublesignal.sepm.jake.fss;

/**
 * Objects listening for file modifications have to implement this.
 * 
 * @author johannes
 */

public interface ModificationListener {
	public enum ModifyActions { CREATED, REMOVED, CHANGED };
	
	public void fileModified(String relpath, ModifyActions action);
	
}
