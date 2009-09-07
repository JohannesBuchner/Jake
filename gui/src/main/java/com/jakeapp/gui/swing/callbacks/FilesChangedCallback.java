package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.jake.fss.IModificationListener;

public interface FilesChangedCallback {
	public void filesChanged(String relpath, IModificationListener.ModifyActions action);
}
