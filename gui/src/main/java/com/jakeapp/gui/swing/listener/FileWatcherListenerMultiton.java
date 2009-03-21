package com.jakeapp.gui.swing.listener;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.worker.AnnounceJakeObjectTask;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.jake.fss.IFileModificationListener;
import com.jakeapp.jake.fss.IModificationListener.ModifyActions;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class FileWatcherListenerMultiton {
	private static final Logger log = Logger.getLogger(FileWatcherListenerMultiton.class);	
	private static Map<Project, IFileModificationListener> listeners =
					new HashMap<Project, IFileModificationListener>();

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

				if (p.isAutoAnnounceEnabled()) {
					FileObject fo = new FileObject(p, relpath);
					Attributed<FileObject> aFo = JakeMainApp.getCore().getAttributed(fo);

					if (aFo.getLastVersionLogEntry() == null) {
						log.info("AutoAnnouncing " + relpath);
						JakeExecutor.exec(new AnnounceJakeObjectTask(fo, null));
					}
				}
			}
		};
	}
}