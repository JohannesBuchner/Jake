package com.jakeapp.gui.swing.listener;

import java.util.HashMap;
import java.util.Map;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.jake.fss.IFileModificationListener;
import com.jakeapp.jake.fss.IModificationListener.ModifyActions;

public class FileWatcherListenerMultiton {

	private static Map<Project, IFileModificationListener> listeners = new HashMap<Project, IFileModificationListener>();

	public static IFileModificationListener get(Project p) {
		if (!listeners.containsKey(p))
			listeners.put(p, createListener(p));
		return listeners.get(p);
	}

	private static IFileModificationListener createListener(final Project p) {
		return new IFileModificationListener() {

			@Override
			public void fileModified(String relpath, ModifyActions action) {
				EventCore.get().fireFilesChanged(p);
			}

		};
	}

}
