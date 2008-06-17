package com.doublesignal.sepm.jake.fss;

import com.doublesignal.sepm.jake.fss.IModificationListener.ModifyActions;

/**
 * Objects listening for file modifications have to implement this.
 * 
 * @author johannes
 */

public interface IProjectModificationListener {
	void fileModified(String relpath, ModifyActions action);
}
