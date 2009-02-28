package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.jake.fss.IModificationListener;

public interface FilesChanged {
	public void filesChanged(String relpath, IModificationListener.ModifyActions action);
}
