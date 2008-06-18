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

    /**
     * method to be implemented by an modification listener to get notified of changes
     * @param file the FileObject changed
     * @param action the action that happend (created, deleted, modified..)
     */
    void fileModified(File file, ModifyActions action);
	
}
