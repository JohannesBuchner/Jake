package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.xcore.ObjectCache;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author studpete
 */
public class GetAllProjectFilesWorker extends
				SwingWorkerWithAvailableLaterObject<List<FileObject>> {
	private static final Logger log = Logger.getLogger(GetAllProjectFilesWorker.class);
	private Project project;

	public GetAllProjectFilesWorker(Project project) {
		this.project = project;
	}

	@Override
	protected AvailableLaterObject<List<FileObject>> calculateFunction() {
		log.debug("calling GetAllProjectFilesWorker:calculateFunction");
		return JakeMainApp.getCore().getFiles(project);
	}

	@Override
	protected void done() {
		log.info("Done GetAllProjectFilesWorker");

		// done! save into object cache
		try {
			ObjectCache.get().setFiles(project, get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
