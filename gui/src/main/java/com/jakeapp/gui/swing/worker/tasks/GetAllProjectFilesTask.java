package com.jakeapp.gui.swing.worker.tasks;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.xcore.ObjectCache;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * @author studpete
 */
public class GetAllProjectFilesTask extends AbstractTask<Collection<FileObject>> {
	private static final Logger log = Logger.getLogger(GetAllProjectFilesTask.class);
	private Project project;

	public GetAllProjectFilesTask(Project project) {
		this.project = project;
	}

	@Override
	protected AvailableLaterObject<Collection<FileObject>> calculateFunction() {
		log.debug("calling GetAllProjectFilesTask:calculateFunction");
		return JakeMainApp.getCore().getFiles(project);
	}

	@Override
	protected void onDone() {
		log.info("Done GetAllProjectFilesTask");

		// done! save into object cache
		try {
			ObjectCache.get().setFiles(project, get());
		} catch (Exception e) {
			log.warn("GetAllProjectFilesTask failed", e);
		}
	}
}
