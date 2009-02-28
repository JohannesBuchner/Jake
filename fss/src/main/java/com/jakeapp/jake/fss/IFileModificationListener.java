package com.jakeapp.jake.fss;

import com.jakeapp.jake.fss.IModificationListener.ModifyActions;

/**
 * Objects listening for file modifications have to implement this.
 * 
 * @author johannes
 */

public interface IFileModificationListener {
	void fileModified(String relpath, ModifyActions action);
}
